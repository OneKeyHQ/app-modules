package com.margelo.nitro.reactnativeperfstats

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.BufferedReader
import java.io.FileReader
import java.util.Locale

private const val TAG = "PerfStats"
private const val MIN_INTERVAL_MS = 200L
// Standard Android USER_HZ. If a device differs the absolute CPU% scales
// accordingly, but values stay self-consistent across samples.
private const val CLOCK_TICKS_PER_SECOND = 100L

@DoNotStrip
class ReactNativePerfStats : HybridReactNativePerfStatsSpec() {

    override fun start(intervalMs: Double) {
        Sampler.start(intervalMs.toLong().coerceAtLeast(MIN_INTERVAL_MS))
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
            Sampler.takeSample()
        }
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

    fun start(intervalMsNew: Long) {
        synchronized(schedulerLock) {
            intervalMs = intervalMsNew
            if (running) return
            if (handler == null) {
                val ht = HandlerThread("PerfStatsSampler").apply { start() }
                handlerThread = ht
                handler = Handler(ht.looper)
            }
            running = true
            scheduleTick(generation, handler!!)
        }
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
        }
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
                val sample = takeSample()
                Overlay.update(sample)
                synchronized(schedulerLock) {
                    if (genAtStart != generation || !running) return
                    handler?.postDelayed(this, intervalMs)
                }
            }
        })
    }

    fun takeSample(): PerfSample {
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
            if (cpuTicks != null) {
                lastCpuTicks = cpuTicks
                lastMonoNs = nowMonoNs
            }
        }

        return PerfSample(
            cpu = cpuPct,
            rss = rssBytes.toDouble(),
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

// ---- Overlay ----------------------------------------------------------
//
// Attaches a TextView via WindowManager.addView using
// TYPE_APPLICATION_ABOVE_SUB_PANEL (z=1005). This sits above:
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

    /** Called from PerfStatsInitProvider at process start so we never miss
     *  the launcher Activity's onResumed event. */
    fun bootstrap(app: Application) {
        if (registered) return
        app.registerActivityLifecycleCallbacks(this)
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
            WindowManager.LayoutParams.TYPE_APPLICATION_ABOVE_SUB_PANEL,
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
        } catch (e: Exception) {
            OneKeyLog.warn(TAG, "Failed to addView overlay: ${e.message}")
        }
    }

    private fun detach() {
        val view = overlayView ?: return
        if (attachedToWindowManager) {
            try {
                val wm = currentActivity?.getSystemService(Context.WINDOW_SERVICE)
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
            minWidth = dp(activity, 110)
            text = "CPU: --\nRAM: --"
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
                    val newX = (event.rawX - dX).toInt()
                    val newY = (event.rawY - dY).toInt()
                    params.x = newX
                    params.y = newY
                    posX = newX
                    posY = newY
                    val wm = v.context.getSystemService(Context.WINDOW_SERVICE)
                        as? WindowManager
                    try { wm?.updateViewLayout(v, params) } catch (_: Exception) {}
                    true
                }
                else -> false
            }
        }
    }

    private fun renderText(s: PerfSample): String {
        val cpu = if (s.cpu > 0) String.format(Locale.US, "%.1f%%", s.cpu) else "--"
        val mb = s.rss / 1024.0 / 1024.0
        val mem = if (mb > 0) String.format(Locale.US, "%.1f MB", mb) else "--"
        return "CPU: $cpu\nRAM: $mem"
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
