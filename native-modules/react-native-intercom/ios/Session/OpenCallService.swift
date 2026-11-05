// Source recovery modifications: see IOS_PROVENANCE.md. Private Swift ABI pending.
import Foundation
import os

@objc enum OpenCallServiceState: Int { case loading, success, failure }

@objc(_TtP18IntercomSDKPrivate23OpenCallServiceProtocol_)
protocol OpenCallServiceProtocol {
    var state: OpenCallServiceState { get }
    var lastError: Error? { get }
    func performOpenCallIfNeeded()
    func retry()
    func invalidate()
}

@objc(_TtC18IntercomSDKPrivate15OpenCallService)
class OpenCallService: NSObject, OpenCallServiceProtocol {
    @objc static let shared = OpenCallService(dataManager: ICMDataManager.shared(), notificationCenter: NotificationCenter.default)
    @objc var lastError: Error?
    private var internalState: OpenCallServiceState?
    private var userJwtChangedInFlight = false
    private let dataManager: ICMDataManager
    private let notificationCenter: NotificationCenterInterface

    @objc var state: OpenCallServiceState {
        if internalState == nil {
            ICMLogger.privateLog("⚠️ OpenCallService.state read before performOpenCallIfNeeded(). Auto-recovering.", type: .error)
            performOpenCallIfNeeded()
        }
        return internalState ?? .loading
    }

    init(dataManager: ICMDataManager, notificationCenter: NotificationCenterInterface) {
        self.dataManager = dataManager
        self.notificationCenter = notificationCenter
        super.init()
        notificationCenter.addObserver(self, selector: #selector(messengerDidHide), name: Notification.Name("IntercomWindowDidHideNotification"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(userJwtDidChange), name: Notification.Name("IntercomUserJwtDidChangeNotification"), object: nil)
    }

    override init() { fatalError("init() has not been implemented") }
    deinit { notificationCenter.removeObserver(self) }

    @objc func performOpenCallIfNeeded() {
        if internalState == nil || internalState == .failure { executeOpenCall() }
    }

    @objc func retry() {
        if internalState == .failure { executeOpenCall() }
    }

    private func executeOpenCall() {
        internalState = .loading
        lastError = nil
        userJwtChangedInFlight = false
        notificationCenter.post(name: Notification.Name("IntercomOpenCallStateDidChangeNotification"), object: self, userInfo: nil)
        dataManager.refreshOpenResponse(success: { [weak self] in
            guard let self, self.internalState == .loading else { return }
            self.internalState = .success
            self.lastError = nil
            self.notificationCenter.post(name: Notification.Name("IntercomOpenCallStateDidChangeNotification"), object: self, userInfo: nil)
        }, error: { [weak self] error in
            guard let self, self.internalState == .loading else { return }
            if self.userJwtChangedInFlight && ErrorProcessor.isUserJwtExpired(error as NSError?) {
                self.executeOpenCall()
            } else {
                self.internalState = .failure
                self.lastError = error
                self.notificationCenter.post(name: Notification.Name("IntercomOpenCallStateDidChangeNotification"), object: self, userInfo: nil)
            }
        })
    }

    @objc func invalidate() {
        internalState = nil
        lastError = nil
        dataManager.clearOpenResponseData()
    }

    @objc private func messengerDidHide() { invalidate() }
    @objc private func userJwtDidChange() {
        if internalState == .loading { userJwtChangedInFlight = true }
        else if internalState == .failure && ErrorProcessor.isUserJwtExpired(lastError as NSError?) { executeOpenCall() }
    }
}
