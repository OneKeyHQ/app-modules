package com.margelo.nitro.reactnativeperfstats

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import android.util.TypedValue
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.BufferedReader
import java.io.FileReader
import java.util.Locale

private const val TAG = "PerfStats"
private const val MIN_INTERVAL_MS = 200L
// 24 h. `Double.POSITIVE_INFINITY.toLong()` saturates to Long.MAX_VALUE
// and `postDelayed(_, Long.MAX_VALUE)` would silently never fire again,
// so we cap before handing to the scheduler. Mirrors iOS kMaxIntervalMs.
private const val MAX_INTERVAL_MS = 86_400_000L
// Standard Android USER_HZ. If a device differs the absolute CPU% scales
// accordingly, but values stay self-consistent across samples.
private const val CLOCK_TICKS_PER_SECOND = 100L

// Anomaly logging thresholds. We only emit a warn after the metric has
// stayed over the threshold for SUSTAIN samples in a row, to skip
// transient spikes (e.g. JS startup, GC). After firing we throttle for
// COOLDOWN_MS to avoid flooding native-logger.
private const val CPU_ANOMALY_PCT = 150.0
// 800 MiB.
private const val RSS_ANOMALY_BYTES = 838_860_800.0
// Below ~45 fps the UI thread feels janky on a 60 Hz panel. On 90/120 Hz
// devices this still represents a clearly degraded experience, so we keep
// one threshold rather than introducing refresh-rate detection.
private const val UI_FPS_ANOMALY_BELOW = 45.0
// Below ~30 fps the JS thread is missing every other frame's RAF callback.
private const val JS_FPS_ANOMALY_BELOW = 30.0
private const val ANOMALY_SUSTAIN_SAMPLES = 5
private const val ANOMALY_COOLDOWN_MS = 30_000L
// JS FPS hints older than this are treated as stale and reported as 0,
// so the overlay doesn't keep showing a fossilised number after the
// JS-side tracker has been stopped (or is still booting).
private const val JS_FPS_HINT_TTL_MS = 2_000L

@DoNotStrip
class ReactNativePerfStats : HybridReactNativePerfStatsSpec() {

    override fun start(intervalMs: Double) {
        // Drop NaN/Inf at the JS↔native boundary: `Double.NaN.toLong()`
        // returns 0 (then clamps to MIN), but `Infinity.toLong()` saturates
        // to Long.MAX_VALUE and would freeze the sampler indefinitely.
        val safe = if (intervalMs.isFinite()) intervalMs.toLong() else 1000L
        Sampler.start(safe.coerceIn(MIN_INTERVAL_MS, MAX_INTERVAL_MS))
    }

    override fun stop() {
        Sampler.stop()
    }

    override fun showOverlay() {
        Overlay.show()
    }

    override fun hideOverlay() {
        Overlay.hide()
    }

    override fun sample(): Promise<PerfSample> {
        return Promise.async {
            // recordBaseline=false: one-shot reads must not write the
            // CPU baseline used by the periodic tick. See takeSample.
            Sampler.takeSample(recordBaseline = false)
        }
    }

    override fun setJsFpsHint(fps: Double) {
        // Drop NaN/Inf at the boundary; JsFpsHolder caches the value and
        // the overlay would happily render "inf fps" otherwise.
        if (!fps.isFinite()) return
        JsFpsHolder.set(fps)
    }

    override fun addMemoryWarningListener(
        callback: (MemoryWarningEvent) -> Unit,
    ): Double {
        return MemoryWarningCenter.add(callback).toDouble()
    }

    override fun removeMemoryWarningListener(id: Double) {
        MemoryWarningCenter.remove(id.toLong())
    }

    override fun cleanupNativeCaches() {
        // Same hint that the LOW / CRITICAL warning path runs. ART may
        // ignore Runtime.gc() at low pressure, but the cost of asking
        // is negligible.
        MemoryWarningCenter.performNativeCleanup()
    }
}

// ---- Sampler ----------------------------------------------------------
//
// Singleton state shared across all HybridObject instances. The
// HandlerThread runs the polling loop off the main / native-modules queue,
// so JS-thread freezes do not stop overlay updates.

private object Sampler {
    // The HandlerThread is created lazily on start() and quit on stop() so
    // an idle process doesn't keep a worker thread (~512KB stack + VM
    // overhead) alive forever. Recreated on next start().
    //
    // schedulerLock guards `handlerThread`, `handler`, `running`, `generation`
    // and `intervalMs` together — start/stop must transition all of them
    // atomically, otherwise a start() lambda queued on the (now-quitting)
    // looper could resurrect `running=true` after stop() and strand the
    // sampler with no live handler.
    private val schedulerLock = Any()
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    // Bumped on every stop(); any in-flight tick whose generation no longer
    // matches drops itself instead of rescheduling on a stale handler.
    private var generation = 0L
    private var running = false
    private var intervalMs: Long = 1000L

    private val lock = Any()
    @Volatile private var lastCpuTicks: Long = -1L
    @Volatile private var lastMonoNs: Long = -1L
    // Last UI FPS computed by the periodic tick. takeSample() reads this
    // directly so one-shot sample() calls do not steal frames from the
    // UiFpsMonitor counter (which is owned by the periodic tick).
    @Volatile private var lastUiFps: Double = 0.0

    // Anomaly state lives on the sampler HandlerThread (single consumer),
    // so no synchronization is needed. lastLogMs intentionally persists
    // across stop()/start() cycles to keep the cooldown honest if the
    // caller toggles the sampler rapidly.
    private var cpuOverCount = 0
    private var rssOverCount = 0
    private var uiFpsUnderCount = 0
    private var jsFpsUnderCount = 0
    private var lastCpuLogMs = 0L
    private var lastRssLogMs = 0L
    private var lastUiFpsLogMs = 0L
    private var lastJsFpsLogMs = 0L

    fun start(intervalMsNew: Long) {
        synchronized(schedulerLock) {
            val prevInterval = intervalMs
            intervalMs = intervalMsNew
            if (running) {
                // Mirror iOS dispatch_source schedule() on a running timer:
                // a fresh start() with a new interval re-paces the loop
                // immediately rather than waiting for the old period to
                // elapse. Bump generation so the in-flight tick (if any)
                // self-rejects before rescheduling on the stale cadence.
                if (prevInterval != intervalMsNew) {
                    generation++
                    handler?.removeCallbacksAndMessages(null)
                    scheduleTick(generation, handler!!)
                }
                return
            }
            // Cold-start path: reset CPU baseline now, before any tick is
            // scheduled. Doing it here rather than in stop() is the only
            // race-free option — stop()'s `quitSafely` doesn't synchronously
            // drain a tick whose `active` check already passed, so a
            // reset in stop() can be silently undone by that in-flight
            // tick writing its captured cpuTicks back into lastCpuTicks.
            // takeSample() called from one-shot sample() (recordBaseline
            // = false) also wouldn't be able to clobber this, so the
            // first periodic tick after start() always begins from a
            // clean slate.
            synchronized(lock) {
                lastCpuTicks = -1L
                lastMonoNs = -1L
            }
            if (handler == null) {
                val ht = HandlerThread("PerfStatsSampler").apply { start() }
                handlerThread = ht
                handler = Handler(ht.looper)
            }
            running = true
            scheduleTick(generation, handler!!)
        }
        UiFpsMonitor.start()
    }

    fun stop() {
        synchronized(schedulerLock) {
            generation++
            running = false
            handler?.removeCallbacksAndMessages(null)
            // quitSafely lets in-flight tick body finish; the generation
            // check below prevents that body from rescheduling itself.
            handlerThread?.quitSafely()
            handlerThread = null
            handler = null
            lastUiFps = 0.0
        }
        // No CPU baseline reset here on purpose: an in-flight tick that
        // already passed the `active` check would race with us and write
        // its captured cpuTicks back, undoing the reset. start() resets
        // on its cold-start path instead, where no sampler thread is
        // running yet.
        UiFpsMonitor.stop()
        Overlay.hide()
    }

    /** Caller must hold schedulerLock; [h] is the handler captured at start time. */
    private fun scheduleTick(genAtStart: Long, h: Handler) {
        h.post(object : Runnable {
            override fun run() {
                val active = synchronized(schedulerLock) {
                    genAtStart == generation && running
                }
                if (!active) return
                // Refresh the cached UI FPS *before* takeSample reads it,
                // so the value covers exactly the just-elapsed interval.
                lastUiFps = UiFpsMonitor.readAndReset(System.nanoTime())
                val sample = takeSample()
                Overlay.update(sample)
                checkAnomalyAndLog(sample)
                synchronized(schedulerLock) {
                    if (genAtStart != generation || !running) return
                    handler?.postDelayed(this, intervalMs)
                }
            }
        })
    }

    /**
     * Emits a warn to native-logger when CPU or RSS has stayed over its
     * threshold for [ANOMALY_SUSTAIN_SAMPLES] consecutive samples. Only
     * called from the periodic sampler tick; one-off `sample()` calls do
     * not trip this path. Each metric tracks its own counter and cooldown.
     */
    private fun checkAnomalyAndLog(s: PerfSample) {
        val nowMs = SystemClock.uptimeMillis()

        if (s.cpu >= CPU_ANOMALY_PCT) {
            cpuOverCount++
            if (cpuOverCount >= ANOMALY_SUSTAIN_SAMPLES &&
                nowMs - lastCpuLogMs >= ANOMALY_COOLDOWN_MS
            ) {
                OneKeyLog.warn(
                    TAG,
                    String.format(
                        Locale.US,
                        "Sustained high CPU: %.1f%% over %d samples",
                        s.cpu, cpuOverCount,
                    ),
                )
                lastCpuLogMs = nowMs
                cpuOverCount = 0
            }
        } else {
            cpuOverCount = 0
        }

        if (s.rss >= RSS_ANOMALY_BYTES) {
            rssOverCount++
            if (rssOverCount >= ANOMALY_SUSTAIN_SAMPLES &&
                nowMs - lastRssLogMs >= ANOMALY_COOLDOWN_MS
            ) {
                val mb = s.rss / 1024.0 / 1024.0
                OneKeyLog.warn(
                    TAG,
                    String.format(
                        Locale.US,
                        "Sustained high RSS: %.1f MB over %d samples",
                        mb, rssOverCount,
                    ),
                )
                lastRssLogMs = nowMs
                rssOverCount = 0
            }
        } else {
            rssOverCount = 0
        }

        // FPS thresholds. We require uiFps > 0 to avoid logging during the
        // first sample (when no interval has elapsed yet); jsFps > 0 to skip
        // when the JS-side tracker hasn't been started.
        if (s.uiFps in 0.001..UI_FPS_ANOMALY_BELOW) {
            uiFpsUnderCount++
            if (uiFpsUnderCount >= ANOMALY_SUSTAIN_SAMPLES &&
                nowMs - lastUiFpsLogMs >= ANOMALY_COOLDOWN_MS
            ) {
                OneKeyLog.warn(
                    TAG,
                    String.format(
                        Locale.US,
                        "Sustained low UI FPS: %.1f over %d samples",
                        s.uiFps, uiFpsUnderCount,
                    ),
                )
                lastUiFpsLogMs = nowMs
                uiFpsUnderCount = 0
            }
        } else {
            uiFpsUnderCount = 0
        }

        if (s.jsFps in 0.001..JS_FPS_ANOMALY_BELOW) {
            jsFpsUnderCount++
            if (jsFpsUnderCount >= ANOMALY_SUSTAIN_SAMPLES &&
                nowMs - lastJsFpsLogMs >= ANOMALY_COOLDOWN_MS
            ) {
                OneKeyLog.warn(
                    TAG,
                    String.format(
                        Locale.US,
                        "Sustained low JS FPS: %.1f over %d samples",
                        s.jsFps, jsFpsUnderCount,
                    ),
                )
                lastJsFpsLogMs = nowMs
                jsFpsUnderCount = 0
            }
        } else {
            jsFpsUnderCount = 0
        }
    }

    /** Public alias used by [MemoryWarningCenter] to attach an RSS reading
     *  to each emitted event. Safe to call from any thread. */
    fun residentBytesPublic(): Long = readResidentBytes()

    /**
     * @param recordBaseline true for periodic ticks (the next sample's
     *   delta is computed against this read). false for one-shot
     *   sample() calls so they don't pollute the periodic CPU% baseline
     *   — between stop() and start(), or between two periodic ticks,
     *   a sample() would otherwise insert a new baseline that the next
     *   periodic tick reads, producing a CPU% covering the gap.
     */
    fun takeSample(recordBaseline: Boolean = true): PerfSample {
        val nowMonoNs = System.nanoTime()
        val cpuTicks = readProcessCpuTicks()
        val rssBytes = readResidentBytes()
        val nowWallMs = System.currentTimeMillis().toDouble()

        var cpuPct = 0.0
        synchronized(lock) {
            val prevCpu = lastCpuTicks
            val prevMono = lastMonoNs
            if (cpuTicks != null && prevCpu >= 0 && prevMono > 0) {
                val dTicks = cpuTicks - prevCpu
                val dWallNs = nowMonoNs - prevMono
                if (dWallNs > 0 && dTicks >= 0) {
                    val cpuSec = dTicks.toDouble() / CLOCK_TICKS_PER_SECOND
                    val wallSec = dWallNs.toDouble() / 1_000_000_000.0
                    cpuPct = (cpuSec / wallSec) * 100.0
                }
            }
            if (recordBaseline && cpuTicks != null) {
                lastCpuTicks = cpuTicks
                lastMonoNs = nowMonoNs
            }
        }

        return PerfSample(
            cpu = cpuPct,
            rss = rssBytes.toDouble(),
            uiFps = lastUiFps,
            jsFps = JsFpsHolder.read(nowMonoNs),
            timestamp = nowWallMs,
        )
    }

    private fun readProcessCpuTicks(): Long? {
        return try {
            BufferedReader(FileReader("/proc/self/stat")).use { reader ->
                val line = reader.readLine() ?: return null
                // Field 2 (comm) is in parens and may itself contain spaces,
                // so split everything *after* the last ')'.
                val rparen = line.lastIndexOf(')')
                if (rparen < 0 || rparen + 2 >= line.length) return null
                val tail = line.substring(rparen + 2).split(" ")
                if (tail.size < 13) return null
                tail[11].toLong() + tail[12].toLong()
            }
        } catch (e: Exception) {
            OneKeyLog.warn(TAG, "Failed to read /proc/self/stat: ${e.message}")
            null
        }
    }

    // Cache page size; sysconf is not free, and the value never changes.
    // Pixel 9 Pro Fold's 16K-page system image is why we don't hard-code 4096.
    private val pageSize: Long by lazy {
        try {
            android.system.Os.sysconf(android.system.OsConstants._SC_PAGESIZE)
        } catch (_: Throwable) {
            4096L
        }
    }

    /**
     * Reads /proc/self/statm field 2 (resident pages) and converts to bytes.
     * One readLine, ~50 byte allocation; vs /proc/self/status which scans
     * ~25 lines until VmRSS — at higher polling rates statm produces ~20x
     * less GC pressure for an equivalent value.
     */
    private fun readResidentBytes(): Long {
        return try {
            val line = BufferedReader(FileReader("/proc/self/statm")).use { it.readLine() }
                ?: return 0L
            val parts = line.split(" ")
            if (parts.size < 2) return 0L
            parts[1].toLong() * pageSize
        } catch (e: Exception) {
            OneKeyLog.warn(TAG, "Failed to read /proc/self/statm: ${e.message}")
            0L
        }
    }
}

// ---- UiFpsMonitor -----------------------------------------------------
//
// Counts main-thread frames via Choreographer.FrameCallback. The
// callback fires once per refresh cycle (60/90/120 Hz) when the UI
// thread is responsive, so the per-second count is a faithful proxy
// for "did the UI thread service its frame callbacks".
//
// `Choreographer.getInstance()` returns the per-thread instance, so
// the registration must run on the main looper to observe main-thread
// frames; the worker HandlerThread has no Choreographer.

private object UiFpsMonitor {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val frameCounter = AtomicInteger(0)
    @Volatile private var registered = false
    @Volatile private var lastReadMonoNs: Long = -1L

    private val callback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            frameCounter.incrementAndGet()
            if (registered) {
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
    }

    fun start() {
        // Idempotency check runs inside the main-thread lambda so two
        // concurrent start() calls can't both pass the gate and post the
        // callback twice — Choreographer happily enqueues the same instance
        // multiple times, which would double (then exponentially grow) the
        // frame count and repost cadence.
        mainHandler.post {
            if (registered) return@post
            registered = true
            Choreographer.getInstance().postFrameCallback(callback)
        }
    }

    fun stop() {
        // Mirror start(): serialize the toggle on the main looper so a
        // start/stop pair posted from a non-main thread can't interleave
        // out of order.
        mainHandler.post {
            if (!registered) return@post
            registered = false
            Choreographer.getInstance().removeFrameCallback(callback)
            frameCounter.set(0)
            lastReadMonoNs = -1L
        }
    }

    /**
     * Returns the FPS observed since the previous call. The first call
     * after [start] returns 0 because there's no baseline interval yet.
     * Safe to invoke from any thread.
     */
    fun readAndReset(nowMonoNs: Long): Double {
        val prev = lastReadMonoNs
        val frames = frameCounter.getAndSet(0)
        lastReadMonoNs = nowMonoNs
        if (prev <= 0) return 0.0
        val dWallSec = (nowMonoNs - prev).toDouble() / 1_000_000_000.0
        if (dWallSec <= 0) return 0.0
        return frames / dWallSec
    }
}

// ---- JsFpsHolder ------------------------------------------------------
//
// Caches the most recent JS-thread FPS hint pushed via
// `setJsFpsHint`. The value is computed entirely on the JS side via
// the `requestAnimationFrame` ticker started by `startJsFpsTracker`,
// then reported here. We stale out hints older than [JS_FPS_HINT_TTL_MS]
// to avoid surfacing a fossilised number when the JS tracker has been
// stopped or has not yet booted.

private object JsFpsHolder {
    // Pair holds (fps, lastSetMonoNs). Stored together so a concurrent
    // reader can't observe a new timestamp paired with a stale fps (or
    // vice versa) — which two independent @Volatile fields would allow.
    private val state = AtomicReference(Pair(0.0, -1L))

    fun set(fps: Double) {
        state.set(Pair(fps, System.nanoTime()))
    }

    fun read(nowMonoNs: Long): Double {
        val (fps, last) = state.get()
        if (last < 0) return 0.0
        val ageMs = (nowMonoNs - last) / 1_000_000L
        if (ageMs > JS_FPS_HINT_TTL_MS) return 0.0
        return fps
    }
}

// ---- Overlay ----------------------------------------------------------
//
// Attaches a TextView via WindowManager.addView at z=1005, the AOSP slot
// `TYPE_APPLICATION_ABOVE_SUB_PANEL` (FIRST_SUB_WINDOW + 5). The named
// constant is `@hide` in the public SDK, so we use the literal value
// directly — `WINDOW_TYPE_ABOVE_SUB_PANEL` below — to keep this file
// compilable against the public SDK while preserving the original z-order
// intent. This sits above:
//   - The activity's main window (TYPE_APPLICATION = 2)
//   - PANEL / SUB_PANEL (1000 / 1002)
//   - ATTACHED_DIALOG used by RN's Modal (1003)
// and below system-level windows (Toast = 2005, OVERLAY = 2038).
// Tied to the current Activity's window token, so it stays inside the app
// (no SYSTEM_ALERT_WINDOW permission required) and is removed automatically
// when the Activity is paused/destroyed.
//
// `bootstrap(app)` is invoked by `PerfStatsInitProvider` during process
// startup, which guarantees the launcher Activity's onResumed event is
// captured. Without this hook, JS code calling showOverlay() after the
// React tree mounts would arrive too late and currentActivity would stay
// null indefinitely.

// FIRST_SUB_WINDOW (1000) + 5; the public SDK hides the named constant
// `TYPE_APPLICATION_ABOVE_SUB_PANEL` even though the value is honoured at
// runtime. See the block comment above for the z-order rationale.
private const val WINDOW_TYPE_ABOVE_SUB_PANEL = 1005

internal object Overlay : Application.ActivityLifecycleCallbacks {
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile private var registered = false
    @Volatile private var visible = false
    @Volatile private var currentActivity: Activity? = null
    @Volatile private var overlayView: TextView? = null
    @Volatile private var attachedToWindowManager = false
    @Volatile private var lastSample: PerfSample? = null

    // Persist drag position across Activity transitions
    @Volatile private var posX: Int = -1
    @Volatile private var posY: Int = -1

    // Configuration callback for rotation / split-screen / foldable
    // size changes. Activities declaring android:configChanges (a common
    // optimization in RN apps) don't re-create on rotation, so
    // attachIfPossible's posted clamp never re-runs — handle it here.
    private val configCallbacks = object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) {
            val view = overlayView ?: return
            mainHandler.post { clampOverlayToWindow(view) }
        }
        override fun onLowMemory() {
            // No-op. Memory pressure is handled by MemoryWarningCenter
            // via ComponentCallbacks2 on a separate registration.
        }
    }

    /** Called from PerfStatsInitProvider at process start so we never miss
     *  the launcher Activity's onResumed event. */
    fun bootstrap(app: Application) {
        if (registered) return
        app.registerActivityLifecycleCallbacks(this)
        app.registerComponentCallbacks(configCallbacks)
        registered = true
    }

    fun show() {
        visible = true
        mainHandler.post {
            ensureRegistered()
            attachIfPossible()
        }
    }

    fun hide() {
        visible = false
        mainHandler.post { detach() }
    }

    /**
     * Coalesce updates: if the main thread is blocked, we don't want N
     * setText calls piling up only to all fire when it unblocks. removing
     * any pending instance and posting a fresh one guarantees at most one
     * pending update at a time, and it always reads the latest sample.
     */
    private val updateRunnable = Runnable {
        val s = lastSample ?: return@Runnable
        overlayView?.text = renderText(s)
    }

    fun update(sample: PerfSample) {
        lastSample = sample
        if (!visible) return
        mainHandler.removeCallbacks(updateRunnable)
        mainHandler.post(updateRunnable)
    }

    /** Late-init fallback if bootstrap() somehow didn't run. */
    private fun ensureRegistered() {
        if (registered) return
        val ctx = NitroModules.applicationContext
        if (ctx == null) {
            OneKeyLog.warn(TAG, "applicationContext is null at showOverlay()")
            return
        }
        val app = ctx.applicationContext as? Application
        if (app == null) {
            OneKeyLog.warn(TAG, "applicationContext is not Application")
            return
        }
        app.registerActivityLifecycleCallbacks(this)
        app.registerComponentCallbacks(configCallbacks)
        registered = true
    }

    private fun attachIfPossible() {
        val activity = currentActivity ?: return
        if (overlayView != null) return

        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (wm == null) {
            OneKeyLog.warn(TAG, "WindowManager unavailable on current Activity")
            return
        }

        val token = activity.window?.decorView?.windowToken
        if (token == null) {
            // decorView may not have a token before the first frame draws.
            // Defer to next onActivityResumed which will retry.
            OneKeyLog.warn(TAG, "Activity window token is null; deferring overlay")
            return
        }

        val tv = createOverlay(activity)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WINDOW_TYPE_ABOVE_SUB_PANEL,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            this.token = token
            gravity = Gravity.TOP or Gravity.START
            x = if (posX >= 0) posX else dp(activity, 30)
            y = if (posY >= 0) posY else dp(activity, 100)
        }

        try {
            wm.addView(tv, params)
            attachedToWindowManager = true
            overlayView = tv
            attachDragListener(tv)
            lastSample?.let { tv.text = renderText(it) }
            // posX/posY are persisted from previous orientations and
            // sessions; after rotation or a fold/unfold the saved
            // position can land off the new window. Posted after addView
            // so tv.width/height are measured, then re-clamped once.
            tv.post { clampOverlayToWindow(tv) }
        } catch (e: Exception) {
            OneKeyLog.warn(TAG, "Failed to addView overlay: ${e.message}")
        }
    }

    /** Preferred window size for clamping: the host Activity's decorView
     *  (correct under split-screen / foldable, where the Activity owns
     *  only part of the physical display), falling back to displayMetrics
     *  if the Activity / decorView isn't ready yet. */
    private fun resolveWindowSize(view: TextView): Pair<Int, Int> {
        val activity = view.context as? Activity
        val decor = activity?.window?.decorView
        val metrics = view.context.resources.displayMetrics
        val w = decor?.width?.takeIf { it > 0 } ?: metrics.widthPixels
        val h = decor?.height?.takeIf { it > 0 } ?: metrics.heightPixels
        return w to h
    }

    /** Re-clamps the overlay's current params to the host window. Called
     *  after addView (when posX/posY may have come from a different
     *  orientation) and could be reused on configuration changes. */
    private fun clampOverlayToWindow(tv: TextView) {
        val (winW, winH) = resolveWindowSize(tv)
        val maxX = (winW - tv.width).coerceAtLeast(0)
        val maxY = (winH - tv.height).coerceAtLeast(0)
        val curParams = (tv.layoutParams as? WindowManager.LayoutParams) ?: return
        val newX = curParams.x.coerceIn(0, maxX)
        val newY = curParams.y.coerceIn(0, maxY)
        if (newX == curParams.x && newY == curParams.y) return
        curParams.x = newX
        curParams.y = newY
        posX = newX
        posY = newY
        val wm = tv.context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        try { wm?.updateViewLayout(tv, curParams) } catch (_: Exception) {}
    }

    private fun detach() {
        val view = overlayView ?: return
        if (attachedToWindowManager) {
            try {
                // Use the view's own context, not currentActivity. onActivityDestroyed
                // posts detach() to the main handler and then clears currentActivity
                // synchronously, so by the time this runs currentActivity may already
                // be null and the overlay would otherwise leak.
                val wm = view.context.getSystemService(Context.WINDOW_SERVICE)
                    as? WindowManager
                wm?.removeView(view)
            } catch (e: Exception) {
                OneKeyLog.warn(TAG, "Failed to removeView overlay: ${e.message}")
            }
        }
        attachedToWindowManager = false
        overlayView = null
    }

    private fun createOverlay(activity: Activity): TextView {
        return TextView(activity).apply {
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setBackgroundColor(0xB0000000.toInt())
            setPadding(
                dp(activity, 12),
                dp(activity, 8),
                dp(activity, 12),
                dp(activity, 8),
            )
            gravity = Gravity.START
            // Min width prevents re-layout when digit count changes
            minWidth = dp(activity, 130)
            text = "CPU: --\nRAM: --\nUI:  --\nJS:  --"
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun attachDragListener(view: TextView) {
        var dX = 0f
        var dY = 0f
        view.setOnTouchListener { v, event ->
            val params = (v.layoutParams as? WindowManager.LayoutParams)
                ?: return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = event.rawX - params.x
                    dY = event.rawY - params.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // Clamp to the host Activity window — without this the
                    // overlay can be dragged off-screen and stays
                    // unreachable, since hit-testing follows params.x/y.
                    // resolveWindowSize uses decorView dimensions so
                    // split-screen / foldable sessions don't let the
                    // overlay wander into another app's pane (where the
                    // overlay's window token can't receive touches).
                    val newX = (event.rawX - dX).toInt()
                    val newY = (event.rawY - dY).toInt()
                    val (winW, winH) = resolveWindowSize(v as TextView)
                    val maxX = (winW - v.width).coerceAtLeast(0)
                    val maxY = (winH - v.height).coerceAtLeast(0)
                    val clampedX = newX.coerceIn(0, maxX)
                    val clampedY = newY.coerceIn(0, maxY)
                    params.x = clampedX
                    params.y = clampedY
                    posX = clampedX
                    posY = clampedY
                    val wm = v.context.getSystemService(Context.WINDOW_SERVICE)
                        as? WindowManager
                    try { wm?.updateViewLayout(v, params) } catch (_: Exception) {}
                    true
                }
                // Terminate the gesture cleanly so the touch sequence
                // we claimed in ACTION_DOWN doesn't leak to whatever is
                // below the overlay window on the up-stroke.
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> true
                else -> false
            }
        }
    }

    private fun renderText(s: PerfSample): String {
        val cpu = if (s.cpu > 0) String.format(Locale.US, "%.1f%%", s.cpu) else "--"
        val mb = s.rss / 1024.0 / 1024.0
        val mem = if (mb > 0) String.format(Locale.US, "%.1f MB", mb) else "--"
        val ui = if (s.uiFps > 0) String.format(Locale.US, "%.0f fps", s.uiFps) else "--"
        val js = if (s.jsFps > 0) String.format(Locale.US, "%.0f fps", s.jsFps) else "--"
        return "CPU: $cpu\nRAM: $mem\nUI:  $ui\nJS:  $js"
    }

    private fun dp(activity: Activity, v: Int): Int =
        (v * activity.resources.displayMetrics.density).toInt()

    // Application.ActivityLifecycleCallbacks ----------------------------

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        if (visible && overlayView == null) {
            mainHandler.post { attachIfPossible() }
        }
    }
    override fun onActivityPaused(activity: Activity) {
        if (currentActivity === activity) {
            mainHandler.post { detach() }
        }
    }
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) {
            mainHandler.post { detach() }
            currentActivity = null
        }
    }
}

// ---- MemoryWarningCenter ---------------------------------------------
//
// Bridges Android's ComponentCallbacks2 callbacks to Nitro listeners.
//
// Levels are normalised to match iOS:
//   - TRIM_MEMORY_RUNNING_MODERATE / TRIM_MEMORY_RUNNING_LOW   -> "low"
//   - TRIM_MEMORY_RUNNING_CRITICAL                             -> "critical"
//   - onLowMemory() (deprecated, fires alongside CRITICAL or
//     standalone on older devices)                             -> "critical"
//   - TRIM_MEMORY_UI_HIDDEN and TRIM_MEMORY_BACKGROUND/MODERATE/COMPLETE
//     are ignored. UI_HIDDEN is a backgrounding signal, not memory
//     pressure; the BACKGROUND tier is too late to be useful for a
//     foreground-pressure response (process is already targeted for
//     LRU eviction). Subscribers who care about backgrounding can use
//     AppState directly.
//
// Registration is process-wide via `Application.registerComponentCallbacks`,
// triggered lazily by [PerfStatsInitProvider] or the first `add()` call.
// Listeners are invoked on the main thread (the thread the callbacks
// fire on). Nitro's dispatcher hops back to the JS thread before the JS
// closure runs.
//
// We coalesce duplicate critical events that arrive within
// [DEDUP_WINDOW_MS]: Android often fires onLowMemory() right after
// TRIM_MEMORY_RUNNING_CRITICAL, and we don't want JS to receive two
// back-to-back cache-purge signals.

internal object MemoryWarningCenter : ComponentCallbacks2 {
    private const val MEMORY_TAG = "PerfStats.Memory"
    private const val DEDUP_WINDOW_MS = 500L

    private val lock = Any()
    private val listeners = LinkedHashMap<Long, (MemoryWarningEvent) -> Unit>()
    private val nextId = AtomicLong(1)
    @Volatile private var registered = false
    private val lastEmitTimestamp = AtomicLong(0L)

    /** Called from [PerfStatsInitProvider] at process start so we never
     *  miss the first memory-pressure event on Android 14+ where the
     *  system may fire one shortly after Application onCreate. */
    fun bootstrap(app: Application) {
        ensureRegistered(app)
    }

    fun add(callback: (MemoryWarningEvent) -> Unit): Long {
        val id = nextId.getAndIncrement()
        synchronized(lock) { listeners[id] = callback }
        // Late-init fallback: PerfStatsInitProvider normally registers
        // us, but defend against test harnesses or stripped manifests.
        ensureRegistered(null)
        return id
    }

    fun remove(id: Long) {
        synchronized(lock) { listeners.remove(id) }
    }

    private fun ensureRegistered(appHint: Application?) {
        if (registered) return
        val app = appHint
            ?: (NitroModules.applicationContext as? Application)
            ?: run {
                OneKeyLog.warn(MEMORY_TAG, "applicationContext is null; skipping registration")
                return
            }
        synchronized(lock) {
            if (registered) return
            app.registerComponentCallbacks(this)
            registered = true
        }
    }

    override fun onTrimMemory(level: Int) {
        val normalised = when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> MemoryWarningLevel.LOW
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> MemoryWarningLevel.CRITICAL
            else -> return // background / UI_HIDDEN — see header comment
        }
        emit(normalised, "onTrimMemory($level)")
    }

    override fun onLowMemory() {
        emit(MemoryWarningLevel.CRITICAL, "onLowMemory")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // No-op. We only implement ComponentCallbacks2 for the memory
        // callbacks above; the configuration channel is unused.
    }

    private fun emit(level: MemoryWarningLevel, source: String) {
        val nowMs = System.currentTimeMillis()
        if (level == MemoryWarningLevel.CRITICAL) {
            // Suppress: TRIM_MEMORY_RUNNING_CRITICAL + onLowMemory() often
            // arrive within tens of ms of each other; one cache purge is
            // enough. ComponentCallbacks2 fires on the main thread in
            // practice, but we use CAS so the read/check/write window is
            // closed even if a future Android version posts these from a
            // worker thread.
            val prev = lastEmitTimestamp.get()
            if (nowMs - prev < DEDUP_WINDOW_MS) return
            if (!lastEmitTimestamp.compareAndSet(prev, nowMs)) return
        }
        val rssBytes = Sampler.residentBytesPublic()
        val event = MemoryWarningEvent(
            level = level,
            rss = rssBytes.toDouble(),
            timestamp = nowMs.toDouble(),
        )
        OneKeyLog.warn(
            MEMORY_TAG,
            String.format(
                Locale.US,
                "Memory warning received (%s) from %s, RSS=%.1f MB",
                level.name.lowercase(Locale.US), source, rssBytes / 1024.0 / 1024.0,
            ),
        )

        // Native cleanup before notifying JS subscribers. On Android the
        // analogues of iOS's URLCache / malloc_pressure are far weaker:
        // - Dalvik/ART has no equivalent of malloc_zone_pressure_relief;
        //   `System.gc()` is a hint only, the runtime decides whether to
        //   honour it. We still call it because under TRIM_MEMORY_RUNNING_*
        //   the runtime is more likely to act on the hint.
        // - HTTP caches live inside each OkHttpClient and there's no
        //   process-wide shared instance to drop, so we leave that to JS-
        //   side subscribers that own their clients.
        // - WebView cache clearing needs a WebView instance; defer to JS.
        //
        // Fires on both LOW and CRITICAL: iOS triggers cleanup on every
        // warning (there's only one level), and JS handlers reasonably
        // assume "native already attempted a reclaim before this event"
        // across platforms. Runtime.gc() is a hint anyway, so even when
        // ART ignores it on LOW the cost is negligible.
        performNativeCleanup()

        val snapshot = synchronized(lock) { listeners.values.toList() }
        for (cb in snapshot) cb(event)
    }

    internal fun performNativeCleanup() {
        // Hint to ART; safe to call from main, returns quickly. ART may
        // ignore under low pressure but under TRIM_MEMORY_RUNNING_CRITICAL
        // it generally promotes to a concurrent collection.
        try {
            Runtime.getRuntime().gc()
        } catch (e: Throwable) {
            OneKeyLog.warn(MEMORY_TAG, "Runtime.gc() failed: ${e.message}")
        }
    }
}
