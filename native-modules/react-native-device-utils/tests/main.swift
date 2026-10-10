import Foundation

let store = InMemoryStore.shared
precondition(store.get("startup") == nil, "A fresh process must start empty")
precondition(store.setIfAbsent("startup", "first"))
precondition(!InMemoryStore.shared.setIfAbsent("startup", "reload"))
precondition(store.get("startup") == "first")
store.set("other", "")
precondition(store.get("other") == "")
precondition(!store.setIfAbsent("other", "replacement"))
precondition(store.get("startup") == "first")
store.set("other", "updated")
precondition(store.get("other") == "updated")
precondition(store.remove("other"))
precondition(!store.remove("other"))
precondition(store.setIfAbsent("other", "retry"))

let resultLock = NSLock()
var winners = 0
DispatchQueue.concurrentPerform(iterations: 256) { _ in
    if InMemoryStore.shared.setIfAbsent("race", "winner") {
        resultLock.lock()
        winners += 1
        resultLock.unlock()
    }
}
precondition(winners == 1, "Concurrent runtimes must have one winner")
precondition(store.get("race") == "winner")
print("Swift in-memory store: passed")
