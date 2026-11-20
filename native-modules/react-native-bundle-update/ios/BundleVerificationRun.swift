import Foundation

final class BundleVerificationRun: @unchecked Sendable {
    private let lock = NSLock()
    private var cancelled = false
    private var published = false
    private var finished = false
    private var waiters: [UUID: CheckedContinuation<Bool, Never>] = [:]

    var wasCancelledByClear: Bool {
        lock.lock()
        defer { lock.unlock() }
        return cancelled
    }

    func cancel() {
        lock.lock()
        defer { lock.unlock() }
        if !published {
            cancelled = true
        }
    }

    func publish(_ body: () throws -> Void) throws {
        lock.lock()
        defer { lock.unlock() }
        guard !cancelled else { throw CancellationError() }
        try body()
        published = true
    }

    func finish() {
        lock.lock()
        finished = true
        let currentWaiters = Array(waiters.values)
        waiters.removeAll()
        lock.unlock()
        currentWaiters.forEach { $0.resume(returning: true) }
    }

    func waitForCompletion(timeout: TimeInterval) async -> Bool {
        await withCheckedContinuation { continuation in
            let id = UUID()
            lock.lock()
            if finished {
                lock.unlock()
                continuation.resume(returning: true)
                return
            }
            waiters[id] = continuation
            lock.unlock()
            DispatchQueue.global().asyncAfter(deadline: .now() + timeout) { [weak self] in
                guard let self else { return }
                self.lock.lock()
                let waiter = self.waiters.removeValue(forKey: id)
                self.lock.unlock()
                waiter?.resume(returning: false)
            }
        }
    }
}
