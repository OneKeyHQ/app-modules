import Foundation

let store = InMemoryStore.shared
precondition(try! store.get("startup") == nil, "A fresh process must start empty")
precondition(try! store.setIfAbsent("startup", "first"))
precondition(!(try! InMemoryStore.shared.setIfAbsent("startup", "reload")))
precondition(try! store.get("startup") == "first")
try! store.set("other", "")
precondition(try! store.get("other") == "")
precondition(!(try! store.setIfAbsent("other", "replacement")))
precondition(try! store.get("startup") == "first")
try! store.set("other", "updated")
precondition(try! store.get("other") == "updated")
precondition(try! store.remove("other"))
precondition(!(try! store.remove("other")))
precondition(try! store.setIfAbsent("other", "retry"))

let resultLock = NSLock()
var winners = 0
DispatchQueue.concurrentPerform(iterations: 256) { _ in
    if try! InMemoryStore.shared.setIfAbsent("race", "winner") {
        resultLock.lock()
        winners += 1
        resultLock.unlock()
    }
}
precondition(winners == 1, "Concurrent runtimes must have one winner")
precondition(try! store.get("race") == "winner")

func assertRejected(_ expectedMessage: String, _ operation: () throws -> Void) {
    do {
        try operation()
        preconditionFailure("An over-budget operation must fail")
    } catch {
        precondition(error.localizedDescription == expectedMessage)
    }
}

let keyError = "InMemoryStore key exceeds 256 UTF-8 bytes"
let valueError = "InMemoryStore value exceeds 4096 UTF-8 bytes"
let capacityError = "InMemoryStore capacity exceeds 128 entries"
let maxKey = String(repeating: "é", count: 128)
let maxValue = String(repeating: "😀", count: 1024)
try! store.set(maxKey, maxValue)
precondition(try! store.get(maxKey) == maxValue)
assertRejected(keyError) { try store.set(maxKey + "x", "value") }
assertRejected(keyError) { _ = try store.setIfAbsent(maxKey + "x", "value") }
assertRejected(keyError) { _ = try store.get(maxKey + "x") }
assertRejected(keyError) { _ = try store.remove(maxKey + "x") }
assertRejected(valueError) { try store.set(maxKey, maxValue + "x") }
assertRejected(valueError) { _ = try store.setIfAbsent(maxKey, maxValue + "x") }
assertRejected(valueError) { _ = try store.setIfAbsent("oversized", maxValue + "x") }
precondition(try! store.get(maxKey) == maxValue)
precondition(try! store.get("oversized") == nil)
let asciiKey = String(repeating: "k", count: 256)
let asciiValue = String(repeating: "v", count: 4096)
try! store.set(asciiKey, asciiValue)
assertRejected(keyError) { try store.set(asciiKey + "x", "value") }
assertRejected(valueError) { try store.set(asciiKey, asciiValue + "x") }
precondition(try! store.get(asciiKey) == asciiValue)
try! store.set("", "")
precondition(try! store.get("") == "")
try! store.set("\u{00e9}", "composed")
precondition(try! store.setIfAbsent("e\u{0301}", "decomposed"))
precondition(try! store.get("\u{00e9}") == "composed")
precondition(try! store.get("e\u{0301}") == "decomposed")
for key in ["startup", "other", "race", maxKey, asciiKey, "", "\u{00e9}", "e\u{0301}"] {
    precondition(try! store.remove(key))
}

for index in 0..<128 {
    precondition(try! store.setIfAbsent("slot:\(index)", "first"))
}
assertRejected(capacityError) { try store.set("overflow", "value") }
assertRejected(capacityError) { _ = try store.setIfAbsent("overflow", "value") }
precondition(try! store.get("overflow") == nil)
try! store.set("slot:0", "updated")
precondition(try! store.get("slot:0") == "updated")
precondition(!(try! store.setIfAbsent("slot:0", "replacement")))
precondition(try! store.remove("slot:0"))
precondition(try! store.setIfAbsent("overflow", "retry"))
for index in 1..<128 { precondition(try! store.remove("slot:\(index)")) }
precondition(try! store.remove("overflow"))

var capacityWinners = 0
var rejectedClaims = 0
DispatchQueue.concurrentPerform(iterations: 256) { index in
    do {
        let inserted = try store.setIfAbsent("capacity:\(index)", "winner")
        precondition(inserted)
        resultLock.lock()
        capacityWinners += 1
        resultLock.unlock()
    } catch {
        precondition(error.localizedDescription == capacityError)
        resultLock.lock()
        rejectedClaims += 1
        resultLock.unlock()
    }
}
precondition(capacityWinners == 128 && rejectedClaims == 128)
print("Swift in-memory store: passed")
