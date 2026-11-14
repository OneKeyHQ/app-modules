// SOURCE_RESTORED_UNLINKED: complete IdentityStore/Bridge dependencies remain missing.
import Foundation
import os
#if os(iOS)
@preconcurrency import UIKit
#endif

protocol UserJwtIdentityStoring {
    func userFingerprint() -> [Any]?
    func userJwt(forFingerprint fingerprint: [Any]) -> String?
    func applyRefreshedUserJwt(_ jwt: String, replacing staleJwt: String, forFingerprint fingerprint: [Any]) -> Bool
}

// The original descriptor has one getter; its source spelling was erased by WMO.
protocol ApplicationStateProviding { var applicationState: Int { get } }
#if os(iOS)
struct LiveApplicationState: ApplicationStateProviding {
    var applicationState: Int { UIApplication.shared.applicationState.rawValue }
}
extension ICMIdentityStore: UserJwtIdentityStoring {
    func userJwt(forFingerprint fingerprint: [Any]) -> String? {
        userJwt(forFingerprint: Optional(fingerprint))
    }
    func applyRefreshedUserJwt(_ jwt: String, replacing staleJwt: String, forFingerprint fingerprint: [Any]) -> Bool {
        applyRefreshedUserJwt(Optional(jwt), replacing: staleJwt, forFingerprint: Optional(fingerprint))
    }
}
#endif

@objc(_TtC18IntercomSDKPrivate16UserJwtRefresher)
class UserJwtRefresher: NSObject {
    typealias Completion = (String?) -> Void
    typealias Provider = (@escaping Completion) -> Void

    private let identityStore: () -> UserJwtIdentityStoring
    private let applicationState: ApplicationStateProviding
    private let mainQueue: Dispatcher
    private let uptime: () -> Double
    private let now: () -> Date
    private let scheduleAfter: (Double, @escaping () -> Void) -> Void
    private let onApplied: () -> Void
    private let queue = DispatchQueue(label: "com.intercom.user-jwt-refresher")
    private var storedProvider: Provider?
    private var pending: PendingRefresh?
    private var coolDownUntil: Double = 0
    private var coolDownFingerprint: [Any]?

    private final class PendingRefresh {
        let staleJwt: String
        let fingerprint: [Any]
        let deadline: Double
        var callbacks: [Completion] = []
        var expired = false
        var handled = false
        init(staleJwt: String, fingerprint: [Any], deadline: Double) {
            self.staleJwt = staleJwt; self.fingerprint = fingerprint; self.deadline = deadline
        }
        func takeCallbacks() -> [Completion] {
            let result = callbacks; callbacks = []; return result
        }
    }

    init(identityStore: @escaping () -> UserJwtIdentityStoring,
         applicationState: ApplicationStateProviding, mainQueue: Dispatcher,
         uptime: @escaping () -> Double, now: @escaping () -> Date,
         scheduleAfter: @escaping (Double, @escaping () -> Void) -> Void,
         onApplied: @escaping () -> Void) {
        self.identityStore = identityStore; self.applicationState = applicationState
        self.mainQueue = mainQueue; self.uptime = uptime; self.now = now
        self.scheduleAfter = scheduleAfter; self.onApplied = onApplied
        super.init()
    }

    #if os(iOS)
    @objc static let shared = UserJwtRefresher()
    @objc override convenience init() {
        self.init(identityStore: { ICMIdentityStore.currentIdentity() },
                  applicationState: LiveApplicationState(), mainQueue: AsyncQueue.main,
                  uptime: { ProcessInfo.processInfo.systemUptime }, now: Date.init,
                  scheduleAfter: { delay, work in
                      DispatchQueue.global(qos: .default).asyncAfter(deadline: .now() + delay) { work() }
                  }, onApplied: { ICMBridge.refreshedUserJwtWasApplied() })
    }
    #endif

    @objc var provider: Provider? {
        get { queue.sync { storedProvider } }
        set { queue.sync { storedProvider = newValue } }
    }
    @objc var hasProvider: Bool { queue.sync { storedProvider != nil } }

    @objc(fingerprintForUserJwt:)
    func fingerprint(forUserJwt jwt: String) -> [Any]? {
        let store = identityStore()
        guard let fingerprint = store.userFingerprint(),
              store.userJwt(forFingerprint: fingerprint) == jwt else { return nil }
        return fingerprint
    }
    @objc func shouldRefreshBeforeSending(_ jwt: String) -> Bool {
        guard provider != nil, let expiry = JWTExpiry.expiry(of: jwt) else { return false }
        return expiry.timeIntervalSince(now()) <= 60
    }
    private func equal(_ first: [Any]?, _ second: [Any]) -> Bool {
        guard let first else { return false }
        return (first as NSArray).isEqual(to: second)
    }

    @objc(refreshWithStaleJwt:fingerprint:completion:)
    func refresh(staleJwt: String, fingerprint: [Any]?, completion: @escaping Completion) {
        guard let fingerprint else { completion(nil); return }
        guard let current = identityStore().userJwt(forFingerprint: fingerprint) else { completion(nil); return }
        if !current.isEmpty, current != staleJwt {
            completion(current); return
        }
        guard provider != nil else { completion(nil); return }
        var displaced: [Completion] = []
        let result: (PendingRefresh?, Provider?) = queue.sync {
            if let current = pending, current.deadline <= uptime() {
                displaced += expireLocked(current)
            }
            if let current = pending, !equal(current.fingerprint, fingerprint) {
                pending = nil; current.handled = true; displaced += current.takeCallbacks()
            }
            if let current = pending {
                current.callbacks.append(completion)
                return (current, nil)
            }
            guard let provider = storedProvider else { return (nil, nil) }
            if uptime() < coolDownUntil && equal(coolDownFingerprint, fingerprint) { return (nil, nil) }
            let next = PendingRefresh(staleJwt: staleJwt, fingerprint: fingerprint, deadline: uptime() + 10)
            next.callbacks.append(completion); pending = next
            return (next, provider)
        }
        for callback in displaced { callback(nil) }
        guard let current = result.0 else { completion(nil); return }
        if let provider = result.1 { start(current, provider: provider) }
    }

    private func expireLocked(_ current: PendingRefresh) -> [Completion] {
        pending = nil; current.expired = true
        coolDownUntil = current.deadline + 30; coolDownFingerprint = current.fingerprint
        return current.takeCallbacks()
    }
    private func start(_ current: PendingRefresh, provider: @escaping Provider) {
        scheduleAfter(10) { [weak self] in self?.expireIfPending(current) }
        mainQueue.dispatch { [weak self] in
            guard let self else { return }
            guard self.applicationState.applicationState == 0 else { self.finishWithoutAnswer(current); return }
            provider { [weak self] jwt in self?.complete(current, jwt: jwt, fromProvider: true) }
        }
    }
    private func expireIfPending(_ current: PendingRefresh) {
        let callbacks: [Completion] = queue.sync {
            guard pending === current else { return [] }
            return expireLocked(current)
        }
        if !callbacks.isEmpty {
            ICMLogger.log("The user JWT provider did not respond within 10s.", type: .error)
        }
        for callback in callbacks { callback(nil) }
    }
    private func finishWithoutAnswer(_ current: PendingRefresh) {
        let callbacks: [Completion] = queue.sync {
            guard pending === current else { return [] }
            pending = nil; current.handled = true
            return current.takeCallbacks()
        }
        for callback in callbacks { callback(nil) }
    }

    private func complete(_ current: PendingRefresh, jwt: String?, fromProvider: Bool) {
        let candidate = jwt.flatMap { !$0.isEmpty && $0 != current.staleJwt ? $0 : nil }
        let matches = equal(identityStore().userFingerprint(), current.fingerprint)
        var toApply: String?
        var deliver: String?
        var callbacks: [Completion] = []
        queue.sync {
            if pending === current {
                pending = nil; current.handled = true
                deliver = matches ? candidate : nil
                if deliver == nil {
                    coolDownUntil = uptime() + 30; coolDownFingerprint = current.fingerprint
                }
                if fromProvider { toApply = deliver }
                callbacks = current.takeCallbacks()
            } else if current.expired && !current.handled && fromProvider {
                current.handled = true; toApply = candidate
                if candidate != nil { coolDownUntil = 0 }
            }
        }
        let applied = toApply.map { identityStore().applyRefreshedUserJwt($0, replacing: current.staleJwt, forFingerprint: current.fingerprint) } ?? false
        if deliver != nil {
            if !equal(identityStore().userFingerprint(), current.fingerprint) {
                deliver = nil
            } else if toApply != nil && !applied {
                let latest = identityStore().userJwt(forFingerprint: current.fingerprint)
                deliver = latest.flatMap { !$0.isEmpty && $0 != current.staleJwt ? $0 : nil }
            }
        }
        for callback in callbacks { callback(deliver) }
        if applied { onApplied() }
    }

    @objc func userJwtWasSet(_ jwt: String) {
        let current: PendingRefresh? = queue.sync { coolDownUntil = 0; return pending }
        if let current { complete(current, jwt: jwt, fromProvider: false) }
    }
}
