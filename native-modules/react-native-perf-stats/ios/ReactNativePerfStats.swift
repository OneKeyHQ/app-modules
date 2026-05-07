import NitroModules
import ReactNativeNativeLogger
import Darwin
import UIKit

private let kTag = "PerfStats"
private let kMinIntervalMs: Double = 200

class ReactNativePerfStats: HybridReactNativePerfStatsSpec {

    func start(intervalMs: Double) throws {
        Sampler.shared.start(intervalMs: max(intervalMs, kMinIntervalMs))
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
                let s = self.takeSample()
                Overlay.shared.update(sample: s)
            }
            self.timer = t
            t.resume()
        }
    }

    func stop() {
        queue.async { [weak self] in
            guard let self = self else { return }
            self.running = false
            self.timer?.cancel()
            self.timer = nil
        }
        Overlay.shared.hide()
    }

    func takeSample() -> PerfSample {
        let nowMono = monotonicSec()
        let nowCpu = processCpuSec()
        let rssBytes = residentBytes()
        let nowWallMs = Date().timeIntervalSince1970 * 1000.0

        var cpuPct: Double = 0
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
        lock.unlock()

        return PerfSample(
            cpu: cpuPct,
            rss: Double(rssBytes),
            timestamp: nowWallMs
        )
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

// MARK: - Overlay
//
// Singleton UILabel attached to the current key UIWindow. Updates always
// dispatch to main. No floating-window permission needed; overlay only
// shows while the app is in the foreground.
//
// Inherits NSObject so UIPanGestureRecognizer's target/action selector
// dispatch resolves cleanly via Obj-C runtime.

private final class Overlay: NSObject {
    static let shared = Overlay()

    private override init() { super.init() }

    private var label: UILabel?
    private var visible = false

    // `_lastSample` is written by the Sampler timer thread and read by the
    // main thread in attach() and inside the coalesced update closure.
    // Optional<struct> is not atomic in Swift, so guard with a lock to avoid
    // torn reads / undefined behaviour. `_updatePending` is part of the same
    // protected state to coalesce overlay refreshes.
    private let sampleLock = NSLock()
    private var _lastSample: PerfSample?
    private var _updatePending = false

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
            self.sampleLock.unlock()
            guard self.visible, let snapshot = snapshot else { return }
            self.label?.text = self.renderText(snapshot)
        }
    }

    private func attach() {
        if label != nil { return }
        guard let window = currentKeyWindow() else {
            OneKeyLog.warn(kTag, "No key UIWindow available; overlay deferred")
            return
        }

        let lbl = UILabel(frame: CGRect(x: 30, y: 100, width: 160, height: 60))
        lbl.backgroundColor = UIColor.black.withAlphaComponent(0.7)
        lbl.layer.cornerRadius = 8
        lbl.layer.masksToBounds = true
        lbl.textColor = .white
        lbl.font = .systemFont(ofSize: 13, weight: .bold)
        lbl.textAlignment = .center
        lbl.numberOfLines = 2
        lbl.text = "CPU: --\nRAM: --"
        lbl.isUserInteractionEnabled = true

        let pan = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
        lbl.addGestureRecognizer(pan)

        window.addSubview(lbl)
        label = lbl

        if let s = lastSample {
            lbl.text = renderText(s)
        }
    }

    private func detach() {
        label?.removeFromSuperview()
        label = nil
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
        return "CPU: \(cpuStr)\nRAM: \(memStr)"
    }

    private func currentKeyWindow() -> UIWindow? {
        // iOS 15+ preferred path
        if #available(iOS 15.0, *) {
            for scene in UIApplication.shared.connectedScenes {
                guard
                    let windowScene = scene as? UIWindowScene,
                    windowScene.activationState == .foregroundActive
                else { continue }
                if let kw = windowScene.keyWindow {
                    return kw
                }
            }
        }
        // Fallback: scan all connected scenes
        for scene in UIApplication.shared.connectedScenes {
            guard let windowScene = scene as? UIWindowScene else { continue }
            if let kw = windowScene.windows.first(where: { $0.isKeyWindow }) {
                return kw
            }
            if let any = windowScene.windows.first {
                return any
            }
        }
        return nil
    }
}
