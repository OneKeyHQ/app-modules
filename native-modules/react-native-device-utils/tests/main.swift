import Foundation

let store = InMemoryStore.shared
precondition(try! store.get("startup")?.asType(String.self) == nil, "A fresh process must start empty")
precondition(try! store.setIfAbsent("startup", .second("first")))
precondition(!(try! InMemoryStore.shared.setIfAbsent("startup", .second("reload"))))
precondition(try! store.get("startup")?.asType(String.self) == "first")
try! store.set("other", .second(""))
precondition(try! store.get("other")?.asType(String.self) == "")
precondition(!(try! store.setIfAbsent("other", .second("replacement"))))
precondition(try! store.get("startup")?.asType(String.self) == "first")
try! store.set("other", .second("updated"))
precondition(try! store.get("other")?.asType(String.self) == "updated")
precondition(try! store.remove("other"))
precondition(!(try! store.remove("other")))
precondition(try! store.setIfAbsent("other", .second("retry")))

let resultLock = NSLock()
var winners = 0
DispatchQueue.concurrentPerform(iterations: 256) { _ in
    if try! InMemoryStore.shared.setIfAbsent("race", .second("winner")) {
        resultLock.lock()
        winners += 1
        resultLock.unlock()
    }
}
precondition(winners == 1, "Concurrent runtimes must have one winner")
precondition(try! store.get("race")?.asType(String.self) == "winner")

// Keep false and zero distinct from missing keys and preserve overwrite types.
precondition(try! store.setIfAbsent("primitive", .first(false)))
precondition(try! store.get("primitive")?.asType(Bool.self) == false)
precondition(try! store.get("primitive")?.asType(Double.self) == nil)
precondition(!(try! store.setIfAbsent("primitive", .third(0))))
try! store.set("primitive", .first(true))
precondition(try! store.get("primitive")?.asType(Bool.self) == true)
try! store.set("primitive", .third(0))
precondition(try! store.get("primitive")?.asType(Double.self) == 0)
precondition(try! store.get("primitive")?.asType(Bool.self) == nil)
precondition(!(try! store.setIfAbsent("primitive", .first(false))))
try! store.set("primitive", .third(-0.0))
precondition(try! store.get("primitive")?.asType(Double.self)?.sign == .minus)
for number in [-42.0, 1.5, Double.nan, Double.infinity, -Double.infinity,
               Double.greatestFiniteMagnitude, Double.leastNonzeroMagnitude] {
    try! store.set("primitive", .third(number))
    let actual = (try! store.get("primitive"))!.asType(Double.self)!
    precondition(number.isNaN ? actual.isNaN : actual == number)
}
try! store.set("primitive", .second("false"))
precondition(try! store.get("primitive")?.asType(String.self) == "false")
precondition(try! store.get("primitive")?.asType(Bool.self) == nil)
precondition(try! store.remove("primitive"))
precondition(try! store.get("primitive") == nil)
precondition(try! store.setIfAbsent("primitive", .third(0)))
precondition(try! store.remove("primitive"))
precondition(try! store.setIfAbsent("primitive", .first(false)))
precondition(try! store.remove("primitive"))

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
try! store.set(maxKey, .second(maxValue))
precondition(try! store.get(maxKey)?.asType(String.self) == maxValue)
assertRejected(keyError) { try store.set(maxKey + "x", .second("value")) }
assertRejected(keyError) { _ = try store.setIfAbsent(maxKey + "x", .second("value")) }
assertRejected(keyError) { _ = try store.get(maxKey + "x")?.asType(String.self) }
assertRejected(keyError) { _ = try store.remove(maxKey + "x") }
assertRejected(valueError) { try store.set(maxKey, .second(maxValue + "x")) }
assertRejected(valueError) { _ = try store.setIfAbsent(maxKey, .second(maxValue + "x")) }
assertRejected(valueError) { _ = try store.setIfAbsent("oversized", .second(maxValue + "x")) }
precondition(try! store.get(maxKey)?.asType(String.self) == maxValue)
precondition(try! store.get("oversized")?.asType(String.self) == nil)
let asciiKey = String(repeating: "k", count: 256)
let asciiValue = String(repeating: "v", count: 4096)
try! store.set(asciiKey, .second(asciiValue))
assertRejected(keyError) { try store.set(asciiKey + "x", .second("value")) }
assertRejected(valueError) { try store.set(asciiKey, .second(asciiValue + "x")) }
precondition(try! store.get(asciiKey)?.asType(String.self) == asciiValue)
try! store.set("", .second(""))
precondition(try! store.get("")?.asType(String.self) == "")
try! store.set("\u{00e9}", .second("composed"))
precondition(try! store.setIfAbsent("e\u{0301}", .second("decomposed")))
precondition(try! store.get("\u{00e9}")?.asType(String.self) == "composed")
precondition(try! store.get("e\u{0301}")?.asType(String.self) == "decomposed")
for key in ["startup", "other", "race", maxKey, asciiKey, "", "\u{00e9}", "e\u{0301}"] {
    precondition(try! store.remove(key))
}

for index in 0..<128 {
    precondition(try! store.setIfAbsent("slot:\(index)", .second("first")))
}
assertRejected(capacityError) { try store.set("overflow", .second("value")) }
assertRejected(capacityError) { _ = try store.setIfAbsent("overflow", .second("value")) }
precondition(try! store.get("overflow")?.asType(String.self) == nil)
try! store.set("slot:0", .second("updated"))
precondition(try! store.get("slot:0")?.asType(String.self) == "updated")
precondition(!(try! store.setIfAbsent("slot:0", .second("replacement"))))
precondition(try! store.remove("slot:0"))
precondition(try! store.setIfAbsent("overflow", .second("retry")))
for index in 1..<128 { precondition(try! store.remove("slot:\(index)")) }
precondition(try! store.remove("overflow"))

var capacityWinners = 0
var rejectedClaims = 0
DispatchQueue.concurrentPerform(iterations: 256) { index in
    do {
        let inserted = try store.setIfAbsent("capacity:\(index)", .second("winner"))
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
