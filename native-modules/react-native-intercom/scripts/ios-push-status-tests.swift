import Foundation
import UserNotifications

@main struct PushStatusTests {
    static func main() throws {
        var passed: [String] = []
        func check(_ value: Bool, _ name: String) {
            guard value else { fputs("FAIL: \(name)\n", stderr); exit(1) }
            passed.append(name)
        }
        var replies: [(UNAuthorizationStatus) -> Void] = []
        let state = PushNotificationStatus { replies.append($0) }
        check(replies.count == 1 && !state.isEnabled, "initialization immediately queries provider while default enabled is false")
        for raw in [0, 1, 2, 3, 4, 5, -1, Int.max] {
            replies[0](UNAuthorizationStatus(rawValue: raw)!)
            check(state.isEnabled == (raw == 2 || raw == 3), "exact raw status enabled classification \(raw)")
        }
        state.refresh()
        check(replies.count == 2, "refresh always makes a fresh provider call")
        replies[1](.authorized)
        check(state.isEnabled, "refresh reply updates before returning on caller thread")
        replies[0](.denied)
        check(!state.isEnabled, "older provider answer remains able to overwrite newer status")
        var events: [Bool] = []
        state.refresh { events.append(state.isEnabled) }
        check(replies.count == 3 && events.isEmpty, "completion remains pending until provider responds")
        replies[2](.provisional)
        replies[2](UNAuthorizationStatus(rawValue: 4)!)
        check(events == [true, false] && !state.isEnabled, "repeated provider replies synchronously update then complete each time")
        let background = DispatchSemaphore(value: 0)
        var offMain = false
        state.refresh { offMain = !Thread.isMainThread; background.signal() }
        let callback = replies[3]
        DispatchQueue.global().async { callback(.authorized) }
        check(background.wait(timeout: .now() + 2) == .success && offMain && state.isEnabled, "background provider completion retains callback thread and observes synced update")
        var late: [(UNAuthorizationStatus) -> Void] = []
        var optional: PushNotificationStatus? = PushNotificationStatus { late.append($0) }
        weak let weakState = optional
        var completed = 0
        optional!.refresh { completed += 1 }
        optional = nil
        check(weakState == nil, "pending provider callbacks hold state weakly")
        late[0](.authorized)
        late[1](.provisional)
        check(completed == 1, "completion still runs after state deallocation")
        let immediate = PushNotificationStatus { $0(.provisional) }
        check(immediate.isEnabled, "synchronous initial provider answer applies during initialization")
        let data = try JSONSerialization.data(withJSONObject: ["status":"passed", "testCount":passed.count, "tests":passed, "scope":"Injected actual status core; no iOS default init/observer/system notification query"], options:.prettyPrinted)
        FileHandle.standardOutput.write(data); print()
    }
}
