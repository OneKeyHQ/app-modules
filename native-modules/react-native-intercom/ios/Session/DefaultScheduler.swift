import Foundation

// The deleted isTimerValid requirement remains unrecovered; these are the proven Code-view paths.
final class DefaultScheduler {
    var timer: Timer?
    private let queue: any Dispatcher

    init(timer: Timer? = nil, queue: any Dispatcher = AsyncQueue.main) {
        self.timer = timer
        self.queue = queue
    }

    func schedule(timeInterval: Double, repeats: Bool, block: @escaping () -> Void) {
        guard timeInterval > 0 else {
            ICMLogger.log("Scheduler: The supplied `timeInterval` (\(timeInterval)) must be a positive, non-zero value.", type: .debug)
            return
        }
        queue.dispatch {
            self.timer = Timer.scheduledTimer(withTimeInterval: timeInterval, repeats: repeats) { _ in block() }
        }
    }

    func invalidate() {
        queue.dispatch {
            self.timer?.invalidate()
            self.timer = nil
        }
    }
}
