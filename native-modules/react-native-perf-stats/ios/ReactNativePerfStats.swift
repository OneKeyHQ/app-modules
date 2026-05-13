import NitroModules
import ReactNativeNativeLogger
import Darwin
import UIKit
import WebKit
// Used for `malloc_zone_pressure_relief(nil, 0)` — see `performNativeCleanup`.
import Foundation

private let kTag = "PerfStats"
private let kMinIntervalMs: Double = 200
// 24 h. The interval feeds DispatchSource.schedule(deadline: .now() + .milliseconds(Int(ms))),
// and Int(Double) traps on values that don't fit in Int — cap before the cast.
private let kMaxIntervalMs: Double = 86_400_000

// Anomaly logging thresholds. We only emit a warn after the metric has
// stayed over the threshold for kAnomalySustainSamples in a row, to
// skip transient spikes (e.g. JS startup, GC). After firing we throttle
// for kAnomalyCooldownSec to avoid flooding native-logger.
private let kCpuAnomalyPct: Double = 150.0
private let kRssAnomalyBytes: Double = 800.0 * 1024.0 * 1024.0
// Below ~45 fps the UI thread feels janky on a 60 Hz panel. On 90/120 Hz
// devices this still represents a clearly degraded experience, so we keep
// one threshold rather than introducing refresh-rate detection.
private let kUiFpsAnomalyBelow: Double = 45.0
// Below ~30 fps the JS thread is missing every other frame's RAF callback.
private let kJsFpsAnomalyBelow: Double = 30.0
private let kAnomalySustainSamples: Int = 5
private let kAnomalyCooldownSec: Double = 30.0
// JS FPS hints older than this are treated as stale and reported as 0.
private let kJsFpsHintTtlSec: Double = 2.0

class ReactNativePerfStats: HybridReactNativePerfStatsSpec {

    func start(intervalMs: Double) throws {
        // Sanitise the JS-supplied value: NaN/Inf would trap on Int(...)
        // inside DispatchSource scheduling, and max(NaN, x) returns NaN
        // in Swift so the lower-bound clamp below can't catch it.
        let finite = intervalMs.isFinite ? intervalMs : 1000.0
        let clamped = min(max(finite, kMinIntervalMs), kMaxIntervalMs)
        Sampler.shared.start(intervalMs: clamped)
    }

    func stop() throws {
        Sampler.shared.stop()
    }

    func showOverlay() throws {
        Overlay.shared.show()
    }

    func hideOverlay() throws {
        Overlay.shared.hide()
    }

    func sample() throws -> Promise<PerfSample> {
        return Promise.async {
            return Sampler.shared.takeSample()
        }
    }

    func setJsFpsHint(fps: Double) throws {
        JsFpsHolder.shared.set(fps: fps)
    }

    func addMemoryWarningListener(callback: @escaping (MemoryWarningEvent) -> Void) throws -> Double {
        return Double(MemoryWarningCenter.shared.add(callback: callback))
    }

    func removeMemoryWarningListener(id: Double) throws {
        MemoryWarningCenter.shared.remove(id: Int(id))
    }
}

// MARK: - Sampler
//
// Singleton; one timer fires on a private background queue regardless of
// how many HybridObject instances exist or which thread calls start(). This
// keeps overlay updates flowing even when the JS thread is blocked.

private final class Sampler {
    static let shared = Sampler()

    private let queue = DispatchQueue(label: "io.onekey.perfstats.sampler", qos: .utility)
    private let lock = NSLock()
    private var timer: DispatchSourceTimer?
    private var running = false
    private var intervalMs: Double = 1000
    private var lastCpuSec: Double = -1
    private var lastMonoSec: Double = -1

    // Anomaly state lives on the sampler's serial dispatch queue, so no
    // synchronization is needed. lastLogSec intentionally persists across
    // stop()/start() cycles to keep the cooldown honest if the caller
    // toggles the sampler rapidly.
    private var cpuOverCount: Int = 0
    private var rssOverCount: Int = 0
    private var uiFpsUnderCount: Int = 0
    private var jsFpsUnderCount: Int = 0
    private var lastCpuLogSec: Double = 0
    private var lastRssLogSec: Double = 0
    private var lastUiFpsLogSec: Double = 0
    private var lastJsFpsLogSec: Double = 0

    // Last UI FPS computed by the periodic timer. Guarded by `lock`:
    // the periodic tick writes from the sampler queue while takeSample()
    // may read from Nitro's worker queue when called via Promise.async.
    private var lastUiFps: Double = 0

    func start(intervalMs ms: Double) {
        queue.async { [weak self] in
            guard let self = self else { return }
            let prev = self.intervalMs
            self.intervalMs = ms
            if self.running, let t = self.timer {
                // Skip reschedule when interval is unchanged — calling
                // `schedule` on a running source resets the next deadline,
                // which would briefly extend the gap unnecessarily.
                if prev != ms {
                    t.schedule(
                        deadline: .now() + .milliseconds(Int(ms)),
                        repeating: .milliseconds(Int(ms))
                    )
                }
                return
            }
            self.running = true
            let t = DispatchSource.makeTimerSource(queue: self.queue)
            t.schedule(
                deadline: .now() + .milliseconds(Int(ms)),
                repeating: .milliseconds(Int(ms))
            )
            t.setEventHandler { [weak self] in
                guard let self = self, self.running else { return }
                // Refresh cached UI FPS *before* takeSample reads it, so
                // the value covers exactly the just-elapsed interval.
                let newUiFps = UiFpsMonitor.shared.readAndReset(nowSec: self.monotonicSec())
                self.lock.lock()
                self.lastUiFps = newUiFps
                self.lock.unlock()
                let s = self.takeSample()
                Overlay.shared.update(sample: s)
                self.checkAnomalyAndLog(sample: s)
            }
            self.timer = t
            t.resume()
        }
        UiFpsMonitor.shared.start()
    }

    func stop() {
        queue.async { [weak self] in
            guard let self = self else { return }
            self.running = false
            self.timer?.cancel()
            self.timer = nil
            self.lock.lock()
            self.lastUiFps = 0
            self.lock.unlock()
        }
        UiFpsMonitor.shared.stop()
        Overlay.shared.hide()
    }

    func takeSample() -> PerfSample {
        let nowMono = monotonicSec()
        let nowCpu = processCpuSec()
        let rssBytes = residentBytes()
        let nowWallMs = Date().timeIntervalSince1970 * 1000.0

        var cpuPct: Double = 0
        var uiFps: Double = 0
        lock.lock()
        if nowCpu >= 0 && lastCpuSec >= 0 && lastMonoSec > 0 {
            let dCpu = nowCpu - lastCpuSec
            let dWall = nowMono - lastMonoSec
            if dWall > 0 && dCpu >= 0 {
                cpuPct = (dCpu / dWall) * 100.0
            }
        }
        if nowCpu >= 0 {
            lastCpuSec = nowCpu
            lastMonoSec = nowMono
        }
        uiFps = lastUiFps
        lock.unlock()

        return PerfSample(
            cpu: cpuPct,
            rss: Double(rssBytes),
            uiFps: uiFps,
            jsFps: JsFpsHolder.shared.read(nowSec: nowMono),
            timestamp: nowWallMs
        )
    }

    /// Emits a warn to native-logger when CPU or RSS has stayed over its
    /// threshold for `kAnomalySustainSamples` consecutive samples. Only
    /// called from the periodic timer; one-off `sample()` calls do not
    /// trip this path. Each metric tracks its own counter and cooldown.
    private func checkAnomalyAndLog(sample s: PerfSample) {
        let nowSec = monotonicSec()

        if s.cpu >= kCpuAnomalyPct {
            cpuOverCount += 1
            if cpuOverCount >= kAnomalySustainSamples,
               nowSec - lastCpuLogSec >= kAnomalyCooldownSec {
                OneKeyLog.warn(
                    kTag,
                    String(
                        format: "Sustained high CPU: %.1f%% over %d samples",
                        s.cpu, cpuOverCount
                    )
                )
                lastCpuLogSec = nowSec
                cpuOverCount = 0
            }
        } else {
            cpuOverCount = 0
        }

        if s.rss >= kRssAnomalyBytes {
            rssOverCount += 1
            if rssOverCount >= kAnomalySustainSamples,
               nowSec - lastRssLogSec >= kAnomalyCooldownSec {
                let mb = s.rss / 1024.0 / 1024.0
                OneKeyLog.warn(
                    kTag,
                    String(
                        format: "Sustained high RSS: %.1f MB over %d samples",
                        mb, rssOverCount
                    )
                )
                lastRssLogSec = nowSec
                rssOverCount = 0
            }
        } else {
            rssOverCount = 0
        }

        // FPS thresholds. Require uiFps > 0 to avoid logging the very first
        // sample (no interval yet), and jsFps > 0 to skip when no JS-side
        // tracker has been started.
        if s.uiFps > 0 && s.uiFps <= kUiFpsAnomalyBelow {
            uiFpsUnderCount += 1
            if uiFpsUnderCount >= kAnomalySustainSamples,
               nowSec - lastUiFpsLogSec >= kAnomalyCooldownSec {
                OneKeyLog.warn(
                    kTag,
                    String(
                        format: "Sustained low UI FPS: %.1f over %d samples",
                        s.uiFps, uiFpsUnderCount
                    )
                )
                lastUiFpsLogSec = nowSec
                uiFpsUnderCount = 0
            }
        } else {
            uiFpsUnderCount = 0
        }

        if s.jsFps > 0 && s.jsFps <= kJsFpsAnomalyBelow {
            jsFpsUnderCount += 1
            if jsFpsUnderCount >= kAnomalySustainSamples,
               nowSec - lastJsFpsLogSec >= kAnomalyCooldownSec {
                OneKeyLog.warn(
                    kTag,
                    String(
                        format: "Sustained low JS FPS: %.1f over %d samples",
                        s.jsFps, jsFpsUnderCount
                    )
                )
                lastJsFpsLogSec = nowSec
                jsFpsUnderCount = 0
            }
        } else {
            jsFpsUnderCount = 0
        }
    }

    private func monotonicSec() -> Double {
        var ts = timespec()
        if clock_gettime(CLOCK_MONOTONIC, &ts) != 0 {
            return Date().timeIntervalSince1970
        }
        return Double(ts.tv_sec) + Double(ts.tv_nsec) / 1_000_000_000.0
    }

    /// Aggregate CPU time consumed by all threads (live + terminated) of the
    /// current process, in seconds. Returns -1 on failure.
    private func processCpuSec() -> Double {
        var ts = timespec()
        if clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &ts) != 0 {
            OneKeyLog.warn(kTag, "clock_gettime(CLOCK_PROCESS_CPUTIME_ID) failed: errno=\(errno)")
            return -1
        }
        return Double(ts.tv_sec) + Double(ts.tv_nsec) / 1_000_000_000.0
    }

    /// Public alias used by `MemoryWarningCenter` to attach an RSS reading
    /// to each emitted event. Reads `phys_footprint` (or `resident_size`
    /// as a fallback) on the calling thread; safe to call from main.
    func residentBytesPublic() -> UInt64 {
        return residentBytes()
    }

    private func residentBytes() -> UInt64 {
        var vmInfo = task_vm_info_data_t()
        var count = mach_msg_type_number_t(
            MemoryLayout<task_vm_info_data_t>.size / MemoryLayout<natural_t>.size
        )
        let kr = withUnsafeMutablePointer(to: &vmInfo) { ptr in
            ptr.withMemoryRebound(to: integer_t.self, capacity: Int(count)) { intPtr in
                task_info(mach_task_self_, task_flavor_t(TASK_VM_INFO), intPtr, &count)
            }
        }
        if kr == KERN_SUCCESS {
            return UInt64(vmInfo.phys_footprint)
        }

        var basicInfo = mach_task_basic_info_data_t()
        var basicCount = mach_msg_type_number_t(
            MemoryLayout<mach_task_basic_info_data_t>.size / MemoryLayout<natural_t>.size
        )
        let basicKr = withUnsafeMutablePointer(to: &basicInfo) { ptr in
            ptr.withMemoryRebound(to: integer_t.self, capacity: Int(basicCount)) { intPtr in
                task_info(mach_task_self_, task_flavor_t(MACH_TASK_BASIC_INFO), intPtr, &basicCount)
            }
        }
        if basicKr == KERN_SUCCESS {
            return UInt64(basicInfo.resident_size)
        }

        OneKeyLog.warn(kTag, "Failed to read resident memory; vmKr=\(kr) basicKr=\(basicKr)")
        return 0
    }
}

// MARK: - UiFpsMonitor
//
// CADisplayLink fires once per main-thread refresh cycle (60/90/120 Hz)
// when the UI thread is responsive, so the per-second count is a faithful
// proxy for "did the main thread service its frame callbacks". The link
// must be attached to the main RunLoop, so start/stop dispatch to .main.

private final class UiFpsMonitor: NSObject {
    static let shared = UiFpsMonitor()

    private override init() { super.init() }

    private var displayLink: CADisplayLink?
    // Read-modify-write of frameCounter happens on the display-link
    // callback (main thread) and on readAndReset (sampler thread), so
    // guard with a lock. Lightweight: two integer ops per call.
    private let counterLock = NSLock()
    private var frameCounter: Int = 0
    private var lastReadSec: Double = -1

    func start() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            if self.displayLink != nil { return }
            let link = CADisplayLink(target: self, selector: #selector(self.tick))
            link.add(to: .main, forMode: .common)
            self.displayLink = link
        }
    }

    func stop() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.displayLink?.invalidate()
            self.displayLink = nil
        }
        counterLock.lock()
        frameCounter = 0
        lastReadSec = -1
        counterLock.unlock()
    }

    @objc private func tick(link: CADisplayLink) {
        counterLock.lock()
        frameCounter += 1
        counterLock.unlock()
    }

    /// Returns the FPS observed since the previous call. The first call
    /// after `start` returns 0 because there's no baseline interval yet.
    /// Safe to invoke from any thread.
    func readAndReset(nowSec: Double) -> Double {
        counterLock.lock()
        let frames = frameCounter
        frameCounter = 0
        let prev = lastReadSec
        lastReadSec = nowSec
        counterLock.unlock()
        if prev <= 0 { return 0 }
        let dWall = nowSec - prev
        if dWall <= 0 { return 0 }
        return Double(frames) / dWall
    }
}

// MARK: - JsFpsHolder
//
// Caches the most recent JS-thread FPS hint pushed via `setJsFpsHint`.
// The value is computed entirely on the JS side via the
// `requestAnimationFrame` ticker started by `startJsFpsTracker`, then
// reported here. Hints older than `kJsFpsHintTtlSec` are reported as 0
// so the overlay doesn't keep showing a fossilised number after the
// JS tracker has been stopped (or has not yet booted).

private final class JsFpsHolder {
    static let shared = JsFpsHolder()

    private init() {}

    private let lock = NSLock()
    private var lastFps: Double = 0
    private var lastSetSec: Double = -1

    func set(fps: Double) {
        let nowSec: Double = {
            var ts = timespec()
            if clock_gettime(CLOCK_MONOTONIC, &ts) != 0 {
                return Date().timeIntervalSince1970
            }
            return Double(ts.tv_sec) + Double(ts.tv_nsec) / 1_000_000_000.0
        }()
        lock.lock()
        lastFps = fps
        lastSetSec = nowSec
        lock.unlock()
    }

    func read(nowSec: Double) -> Double {
        lock.lock()
        let last = lastSetSec
        let fps = lastFps
        lock.unlock()
        if last < 0 { return 0 }
        if nowSec - last > kJsFpsHintTtlSec { return 0 }
        return fps
    }
}

// MARK: - Overlay
//
// Singleton overlay rendered on its own dedicated UIWindow at
// `.alert + 1` windowLevel. The dedicated window is required because
// React Native's <Modal> presents view controllers via UIKit's modal
// presentation, which renders above any subview added to the host
// app's main UIWindow — a UILabel attached to keyWindow ends up behind
// the modal regardless of view-hierarchy z-order. A separate UIWindow
// with a higher `windowLevel` sits above modal-presented view
// controllers and system alerts, so the overlay stays visible.
//
// `OverlayPassthroughWindow` overrides `hitTest` to return nil for
// touches outside the label, letting them fall through to the
// underlying app windows — otherwise the overlay window would swallow
// every tap on the screen.
//
// Inherits NSObject so UIPanGestureRecognizer's target/action selector
// dispatch resolves cleanly via Obj-C runtime.

private final class OverlayPassthroughWindow: UIWindow {
    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        let hit = super.hitTest(point, with: event)
        // Anything that bubbles up to the window itself or its empty
        // root view means the touch missed our overlay subview — pass it
        // through to whatever's below this window.
        if hit === self || hit === self.rootViewController?.view {
            return nil
        }
        return hit
    }
}

private final class Overlay: NSObject {
    static let shared = Overlay()

    private override init() { super.init() }

    private var label: UILabel?
    private var overlayWindow: UIWindow?

    // `_lastSample` is written by the Sampler timer thread and read by the
    // main thread in attach() and inside the coalesced update closure.
    // Optional<struct> is not atomic in Swift, so guard with a lock to avoid
    // torn reads / undefined behaviour. `_updatePending` and `_visible` join
    // the same lock: _updatePending coalesces overlay refreshes; _visible is
    // toggled by show()/hide() on the caller's thread but read in update()'s
    // main-thread closure, so snapshotting it under the existing lock keeps
    // the read race-free without a second lock.
    private let sampleLock = NSLock()
    private var _lastSample: PerfSample?
    private var _updatePending = false
    private var _visible = false

    private var lastSample: PerfSample? {
        get {
            sampleLock.lock(); defer { sampleLock.unlock() }
            return _lastSample
        }
        set {
            sampleLock.lock(); defer { sampleLock.unlock() }
            _lastSample = newValue
        }
    }

    private var visible: Bool {
        get {
            sampleLock.lock(); defer { sampleLock.unlock() }
            return _visible
        }
        set {
            sampleLock.lock(); defer { sampleLock.unlock() }
            _visible = newValue
        }
    }

    func show() {
        visible = true
        DispatchQueue.main.async { [weak self] in self?.attach() }
    }

    func hide() {
        visible = false
        DispatchQueue.main.async { [weak self] in self?.detach() }
    }

    /// Coalesce updates: if the main thread is blocked, we don't want N
    /// label.text writes piling up only to all fire when it unblocks. The
    /// `_updatePending` flag (held under sampleLock) ensures at most one
    /// pending dispatch at a time, and the closure always reads the
    /// latest sample on main.
    func update(sample: PerfSample) {
        sampleLock.lock()
        _lastSample = sample
        let shouldPost = !_updatePending
        if shouldPost { _updatePending = true }
        sampleLock.unlock()

        if !shouldPost { return }

        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.sampleLock.lock()
            self._updatePending = false
            let snapshot = self._lastSample
            let isVisible = self._visible
            self.sampleLock.unlock()
            guard isVisible, let snapshot = snapshot else { return }
            self.label?.text = self.renderText(snapshot)
        }
    }

    private func attach() {
        if label != nil { return }
        guard let scene = currentForegroundScene() else {
            OneKeyLog.warn(kTag, "No foreground UIWindowScene; overlay deferred")
            return
        }

        // Dedicated host VC keeps the window's view hierarchy minimal —
        // the empty UIView serves only as the parent for the label and
        // as the hitTest sentinel checked by OverlayPassthroughWindow.
        let host = UIViewController()
        host.view.backgroundColor = .clear

        let window = OverlayPassthroughWindow(windowScene: scene)
        window.frame = scene.coordinateSpace.bounds
        // .alert is 2000 on iOS; +1 puts the overlay above modal-presented
        // controllers, system alerts and action sheets. Status bar is
        // .statusBar (1000), which we stay below intentionally.
        window.windowLevel = UIWindow.Level.alert + 1
        window.backgroundColor = .clear
        window.isHidden = false
        window.rootViewController = host

        let lbl = UILabel(frame: CGRect(x: 30, y: 100, width: 170, height: 92))
        lbl.backgroundColor = UIColor.black.withAlphaComponent(0.7)
        lbl.layer.cornerRadius = 8
        lbl.layer.masksToBounds = true
        lbl.textColor = .white
        lbl.font = .systemFont(ofSize: 13, weight: .bold)
        lbl.textAlignment = .center
        lbl.numberOfLines = 4
        lbl.text = "CPU: --\nRAM: --\nUI:  --\nJS:  --"
        lbl.isUserInteractionEnabled = true

        let pan = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
        lbl.addGestureRecognizer(pan)

        host.view.addSubview(lbl)
        label = lbl
        overlayWindow = window

        if let s = lastSample {
            lbl.text = renderText(s)
        }
    }

    private func detach() {
        label?.removeFromSuperview()
        label = nil
        // Hide before nilling so UIKit can release the window cleanly;
        // assigning nil to rootViewController first avoids a fleeting
        // warning when the window is torn down with a controller still
        // attached.
        overlayWindow?.isHidden = true
        overlayWindow?.rootViewController = nil
        overlayWindow = nil
    }

    @objc private func handlePan(_ gesture: UIPanGestureRecognizer) {
        guard let view = gesture.view, let parent = view.superview else { return }
        let translation = gesture.translation(in: parent)
        let newCenter = CGPoint(
            x: view.center.x + translation.x,
            y: view.center.y + translation.y
        )
        let halfW = view.bounds.size.width / 2
        let halfH = view.bounds.size.height / 2
        view.center = CGPoint(
            x: max(halfW, min(parent.bounds.size.width - halfW, newCenter.x)),
            y: max(halfH, min(parent.bounds.size.height - halfH, newCenter.y))
        )
        gesture.setTranslation(.zero, in: parent)
    }

    private func renderText(_ s: PerfSample) -> String {
        let cpuStr = s.cpu > 0 ? String(format: "%.1f%%", s.cpu) : "--"
        let mb = s.rss / 1024.0 / 1024.0
        let memStr = mb > 0 ? String(format: "%.1f MB", mb) : "--"
        let uiStr = s.uiFps > 0 ? String(format: "%.0f fps", s.uiFps) : "--"
        let jsStr = s.jsFps > 0 ? String(format: "%.0f fps", s.jsFps) : "--"
        return "CPU: \(cpuStr)\nRAM: \(memStr)\nUI:  \(uiStr)\nJS:  \(jsStr)"
    }

    /// Locate an active foreground `UIWindowScene` to host the overlay
    /// window. Prefers `.foregroundActive`; falls back to the first
    /// connected `UIWindowScene` so we still attach during transient
    /// states like cold launch when no scene is `.foregroundActive` yet.
    private func currentForegroundScene() -> UIWindowScene? {
        var fallback: UIWindowScene?
        for scene in UIApplication.shared.connectedScenes {
            guard let ws = scene as? UIWindowScene else { continue }
            if ws.activationState == .foregroundActive {
                return ws
            }
            if fallback == nil { fallback = ws }
        }
        return fallback
    }
}

// MARK: - MemoryWarningCenter
//
// Bridges UIKit's memory-warning notification to Nitro callbacks.
//
// iOS only emits one level — `UIApplicationDidReceiveMemoryWarningNotification`
// — which we map to `critical`. There is no `low` analog on iOS, by design
// (`MemoryWarningEvent.level` is normalised across platforms).
//
// The observer is registered lazily on the first `add(callback:)` and kept
// for the process lifetime. iOS guarantees a single NotificationCenter
// callback per notification, so cost is negligible. Callbacks fire on the
// main thread because that's the queue UIApplication posts on; Nitro's
// dispatcher will hop back to the JS thread.

private final class MemoryWarningCenter: NSObject {
    static let shared = MemoryWarningCenter()

    private override init() { super.init() }

    private let lock = NSLock()
    private var listeners: [Int: (MemoryWarningEvent) -> Void] = [:]
    private var nextId: Int = 1
    private var observed = false

    func add(callback: @escaping (MemoryWarningEvent) -> Void) -> Int {
        lock.lock()
        let id = nextId
        nextId += 1
        listeners[id] = callback
        let needsObserve = !observed
        if needsObserve { observed = true }
        lock.unlock()
        if needsObserve {
            // Register on main so the observer is associated with the main
            // run loop. NotificationCenter retains observers weakly via
            // selector pattern; we hold the singleton statically.
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                NotificationCenter.default.addObserver(
                    self,
                    selector: #selector(self.handleWarning),
                    name: UIApplication.didReceiveMemoryWarningNotification,
                    object: nil
                )
            }
        }
        return id
    }

    func remove(id: Int) {
        lock.lock()
        listeners.removeValue(forKey: id)
        lock.unlock()
    }

    @objc private func handleWarning() {
        let rssBefore = Sampler.shared.residentBytesPublic()
        let event = MemoryWarningEvent(
            level: .critical,
            rss: Double(rssBefore),
            timestamp: Date().timeIntervalSince1970 * 1000.0
        )
        OneKeyLog.warn(kTag, String(
            format: "Memory warning received (critical), RSS=%.1f MB",
            event.rss / 1024.0 / 1024.0
        ))

        // Native cleanup BEFORE notifying JS. JS handlers (jotai cache
        // purge, WS disconnect, etc.) come right after on the JS thread;
        // running these native reclaims first means by the time JS sees
        // the event, page-level memory is already lower.
        performNativeCleanup()

        let rssAfter = Sampler.shared.residentBytesPublic()
        let deltaMB = (Double(rssBefore) - Double(rssAfter)) / 1024.0 / 1024.0
        if deltaMB > 0.5 {
            OneKeyLog.warn(kTag, String(
                format: "Native cleanup reclaimed %.1f MB (RSS %.1f → %.1f)",
                deltaMB,
                Double(rssBefore) / 1024.0 / 1024.0,
                Double(rssAfter) / 1024.0 / 1024.0
            ))
        }

        lock.lock()
        let snapshot = Array(listeners.values)
        lock.unlock()
        for cb in snapshot { cb(event) }
    }

    /// Three reclaim paths, ordered from cheap-and-safe to system-level:
    ///
    /// 1. `URLCache.shared.removeAllCachedResponses()` — drops the
    ///    process-wide HTTP response cache (CFNetwork). Empirically the
    ///    largest single non-WebView pool, often 50–200 MB. Cheap and
    ///    transparent to app code; the next request just goes to the
    ///    network.
    /// 2. `WKWebsiteDataStore.removeData(...)` for memory + disk + offline
    ///    caches only. Excludes cookies / localStorage / IndexedDB so
    ///    in-WebView auth (TradingView, etc.) survives. Asynchronous;
    ///    fire-and-forget.
    /// 3. `malloc_zone_pressure_relief(nil, 0)` — asks libmalloc to walk
    ///    every registered zone and return free pages to the kernel.
    ///    This is the only API that actually drops `phys_footprint`;
    ///    the previous two reduce allocator usage but pages stay mapped
    ///    until pressure relief runs.
    private func performNativeCleanup() {
        URLCache.shared.removeAllCachedResponses()

        // Caches only — never auth/state. The set is hand-picked so a
        // logged-in DApp / TradingView doesn't get bounced.
        let cacheTypes: Set<String> = [
            WKWebsiteDataTypeMemoryCache,
            WKWebsiteDataTypeDiskCache,
            WKWebsiteDataTypeOfflineWebApplicationCache,
        ]
        WKWebsiteDataStore.default().removeData(
            ofTypes: cacheTypes,
            modifiedSince: Date(timeIntervalSince1970: 0),
            completionHandler: {}
        )

        // Must run last: the two clears above free objects in the
        // allocator's free lists; pressure-relief is what gives those
        // pages back to the kernel.
        malloc_zone_pressure_relief(nil, 0)
    }
}
