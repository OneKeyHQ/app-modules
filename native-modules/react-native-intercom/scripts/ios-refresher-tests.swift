import Foundation

private final class FixtureStore: UserJwtIdentityStoring {
    var fingerprint: [Any]? = ["fixture"]
    var jwt = "old"
    var missingJwt = false
    var writes = 0
    var rejectWrite = false
    var onWrite: (() -> Void)?
    var events: [String] = []
    func userFingerprint() -> [Any]? { fingerprint }
    func userJwt(forFingerprint value: [Any]) -> String? {
        guard !missingJwt, let fingerprint, (fingerprint as NSArray).isEqual(to: value) else { return nil }
        return jwt
    }
    func applyRefreshedUserJwt(_ candidate: String, replacing stale: String, forFingerprint value: [Any]) -> Bool {
        writes += 1; events.append("cas")
        onWrite?()
        guard !rejectWrite, userJwt(forFingerprint: value) == stale else { return false }
        jwt = candidate; return true
    }
}
private final class FixtureState: ApplicationStateProviding { var applicationState = 0 }
private struct ImmediateDispatcher: Dispatcher {
    let queue = DispatchQueue.main
    func dispatch(_ block: @escaping () -> Void) { block() }
}
private final class FixtureClock {
    var uptime = 100.0
    var now = Date(timeIntervalSince1970: 2000)
    var timers: [(Double, () -> Void)] = []
}
private final class Fixture {
    let store = FixtureStore()
    let state = FixtureState()
    let clock = FixtureClock()
    var answers: [UserJwtRefresher.Completion] = []
    lazy var refresher = UserJwtRefresher(identityStore: { self.store },
        applicationState: state, mainQueue: ImmediateDispatcher(), uptime: { self.clock.uptime },
        now: { self.clock.now }, scheduleAfter: { self.clock.timers.append(($0, $1)) },
        onApplied: { self.store.events.append("applied") })
    func installProvider() { refresher.provider = { self.answers.append($0) } }
    func refresh(_ callback: @escaping UserJwtRefresher.Completion) {
        refresher.refresh(staleJwt: "old", fingerprint: ["fixture"], completion: callback)
    }
}
@main struct RefresherTests {
    static func main() throws {
        var passed: [String] = []
        func check(_ value: @autoclosure () -> Bool, _ name: String) {
            precondition(value(), name); passed.append(name)
        }
        func jwt(_ expiration: Double) throws -> String {
            let data = try JSONSerialization.data(withJSONObject: ["exp": expiration])
            return "header." + data.base64EncodedString().replacingOccurrences(of: "+", with: "-").replacingOccurrences(of: "/", with: "_").replacingOccurrences(of: "=", with: "") + ".signature"
        }
        let expiredJwt = try jwt(1900), nearJwt = try jwt(2060), laterJwt = try jwt(2061)
        do {
            let f = Fixture(); var callbacks = 0
            f.refresher.refresh(staleJwt: "old", fingerprint: nil) { value in precondition(value == nil); callbacks += 1 }
            f.refresh { value in precondition(value == nil); callbacks += 1 }
            check(callbacks == 2 && f.clock.timers.isEmpty && !f.refresher.hasProvider, "nil fingerprint/no provider complete immediately without timer")
            f.store.jwt = "already-new"
            var result: String?
            f.refresh { result = $0 }
            check(result == "already-new" && f.answers.isEmpty, "new current JWT bypasses absent provider")
            check(f.refresher.fingerprint(forUserJwt: "already-new") != nil && f.refresher.fingerprint(forUserJwt: "other") == nil, "fingerprint only matches exact current JWT")
            check(!f.refresher.shouldRefreshBeforeSending(expiredJwt), "preflight requires provider even for expired JWT")
            f.installProvider()
            check(f.refresher.shouldRefreshBeforeSending(nearJwt) && !f.refresher.shouldRefreshBeforeSending(laterJwt), "preflight includes exactly sixty seconds and excludes sixty-one")
            check(f.refresher.shouldRefreshBeforeSending(expiredJwt) && !f.refresher.shouldRefreshBeforeSending("malformed"), "preflight accepts expired numeric exp and rejects malformed payload")
        }
        do {
            let f = Fixture(); f.installProvider(); f.store.missingJwt = true
            var callbacks = 0
            f.refresh { value in precondition(value == nil); callbacks += 1 }
            check(callbacks == 1 && f.answers.isEmpty, "nil current JWT returns nil without starting provider")
            f.store.missingJwt = false; f.store.jwt = ""
            f.refresh { _ in callbacks += 1 }
            check(callbacks == 1 && f.answers.count == 1, "empty current JWT can still start provider")
        }
        do {
            let f = Fixture(); f.installProvider()
            f.refresh { f.store.events.append("first:" + ($0 ?? "nil")) }
            f.refresh { f.store.events.append("second:" + ($0 ?? "nil")) }
            check(f.answers.count == 1 && f.clock.timers.map(\.0) == [10], "same fingerprint merges into one provider and ten-second timer")
            f.answers[0]("fresh")
            check(f.store.events == ["cas", "first:fresh", "second:fresh", "applied"] && f.store.jwt == "fresh", "CAS then ordered callbacks then onApplied")
            f.answers[0]("duplicate")
            check(f.store.writes == 1, "duplicate provider callback cannot apply twice")
            f.clock.timers[0].1()
            check(f.store.events.count == 4, "old timer cannot expire handled pending")
        }
        do {
            let f = Fixture(); f.installProvider(); var results: [String] = []
            f.refresh { results.append($0 ?? "nil") }
            f.refresher.provider = nil
            f.answers[0]("fresh")
            check(results == ["fresh"] && f.store.writes == 1, "provider removal preserves already pending captured answer")
        }
        do {
            let f = Fixture(); f.installProvider(); var results: [String] = []
            f.refresh { results.append($0 ?? "nil") }
            f.clock.timers[0].1()
            check(results == ["nil"] && f.store.writes == 0, "scheduled timeout expires pending without rechecking clock")
            f.refresh { results.append($0 ?? "nil") }
            check(results == ["nil", "nil"] && f.answers.count == 1, "timeout establishes same-fingerprint cooldown")
            f.answers[0]("late-fresh")
            check(f.store.jwt == "late-fresh" && results == ["nil", "nil"] && f.store.events == ["cas", "applied"], "late provider may apply once without waking expired callbacks")
            f.answers[0]("late-duplicate")
            check(f.store.writes == 1, "late provider duplicate remains handled")
        }
        do {
            let f = Fixture(); f.installProvider()
            f.refresh { _ in }
            f.clock.uptime = 200; f.clock.timers[0].1()
            f.refresh { _ in }
            check(f.answers.count == 2, "timeout cooldown is deadline plus thirty rather than timer-fire time")
        }
        do {
            let f = Fixture(); f.installProvider(); var results: [String] = []
            f.refresh { results.append($0 ?? "nil") }; f.answers[0](nil)
            f.clock.uptime = 129; f.refresh { results.append($0 ?? "nil") }
            check(f.answers.count == 1 && results == ["nil", "nil"], "nil answer cooldown uses answer uptime plus thirty")
            f.clock.uptime = 130; f.refresh { results.append($0 ?? "nil") }
            check(f.answers.count == 2, "cooldown boundary permits new provider")
        }
        do {
            let f = Fixture(); f.installProvider(); var results: [String] = []
            f.refresh { results.append($0 ?? "nil") }
            f.clock.uptime = 110; f.refresh { results.append($0 ?? "nil") }
            check(results == ["nil", "nil"] && f.answers.count == 1, "new refresh expires pending at exact deadline then observes cooldown")
        }
        do {
            let f = Fixture(); f.installProvider(); f.state.applicationState = 1
            var results: [String] = []
            f.refresh { results.append($0 ?? "nil") }
            f.state.applicationState = 0; f.refresh { results.append($0 ?? "nil") }
            check(results == ["nil"] && f.answers.count == 1 && f.clock.timers.count == 2, "inactive app finishes without setting cooldown")
            f.clock.timers[0].1()
            check(results == ["nil"], "old inactive timer cannot expire newer pending")
        }
        do {
            let f = Fixture(); f.installProvider(); var results: [String] = []
            f.refresh { results.append($0 ?? "nil") }
            f.store.jwt = "external"
            f.refresher.userJwtWasSet("external")
            check(results == ["external"] && f.store.writes == 0 && f.store.events.isEmpty, "external JWT delivers without CAS or onApplied")
            f.answers[0]("late")
            check(f.store.writes == 0, "external completion handles pending and rejects provider later answer")
        }
        do {
            let f = Fixture(); f.installProvider(); var results: [String] = []
            f.refresh { results.append("old:" + ($0 ?? "nil")) }
            f.store.fingerprint = ["changed"]
            f.refresher.refresh(staleJwt: "old", fingerprint: ["changed"]) { results.append("new:" + ($0 ?? "nil")) }
            check(results == ["old:nil"] && f.answers.count == 2, "identity switch cancels old callbacks and starts new provider")
            f.answers[0]("obsolete"); f.answers[1]("fresh")
            check(results == ["old:nil", "new:fresh"] && f.store.writes == 1, "cancelled identity answer cannot apply while current identity answer succeeds")
        }
        do {
            let f = Fixture(); f.installProvider(); f.store.rejectWrite = true
            f.store.onWrite = { f.store.jwt = "other-new" }
            var result: String?
            f.refresh { result = $0 }; f.answers[0]("candidate")
            check(result == "other-new" && f.store.events == ["cas"], "CAS failure returns already-new current JWT without onApplied")
        }
        do {
            let f = Fixture(); f.installProvider(); var result = "unanswered"
            f.refresh { result = $0 ?? "nil" }
            f.store.fingerprint = ["changed"]; f.answers[0]("fresh")
            check(result == "nil" && f.store.writes == 0, "identity mismatch before provider answer prevents CAS and delivers nil")
        }
        do {
            let f = Fixture(); f.installProvider(); var callbackOnMain = true
            f.refresh { _ in callbackOnMain = Thread.isMainThread }
            let finished = DispatchSemaphore(value: 0)
            DispatchQueue.global().async { f.answers[0]("fresh"); finished.signal() }
            precondition(finished.wait(timeout: .now() + 3) == .success)
            check(!callbackOnMain && f.store.events == ["cas", "applied"], "provider background completion keeps callback thread")
        }
        let data = try JSONSerialization.data(withJSONObject: ["status": "passed", "testCount": passed.count, "tests": passed,
            "scope": "Injected core state-machine fixtures; default iOS initializer/Bridge/session remain unlinked"], options: [.sortedKeys])
        print(String(decoding: data, as: UTF8.self))
    }
}
