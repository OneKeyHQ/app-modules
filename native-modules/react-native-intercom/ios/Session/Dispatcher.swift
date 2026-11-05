// Original shared dispatch dependency used by JWT refresh and image delivery.
import Foundation

protocol Dispatcher {
    var queue: DispatchQueue { get }
    func dispatch(_ block: @escaping () -> Void)
}
struct AsyncQueue: Dispatcher {
    let queue: DispatchQueue
    static let main = AsyncQueue(queue: .main)
    func dispatch(_ block: @escaping () -> Void) { queue.async { block() } }
}
