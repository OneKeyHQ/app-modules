import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate26HomescreenHeaderInteractor)
class HomescreenHeaderInteractor {
    weak var headerUpdatingDelegate: HomescreenHeaderViewModel?
    private var config: any ICMConfigProtocol
    private let notificationCenter: any NotificationCenterInterface
    init(config: any ICMConfigProtocol, notificationCenter: any NotificationCenterInterface) {
        self.config = config
        self.notificationCenter = notificationCenter
        notificationCenter.addObserver(self, selector: #selector(configChanged), name: Notification.Name("kICMIntercomConfigChangedNotification"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(headerImageReady), name: Notification.Name("kICMHomeHeaderImageLoaded"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(logoImageReady), name: Notification.Name("kICMHomeHeaderLogoLoaded"), object: nil)
    }
    func getHomeHeaderViewInfoFromConfig() -> HomescreenHeaderViewInfo? {
        guard let first = config.modules else { return nil }
        let header = ((first as AnyObject) as! ConfigModules).home.header
        guard let second = config.modules else { return nil }
        let customization = ((second as AnyObject) as! ConfigModules).customizationConfig.headerCustomization
        let background = customization.dynamicBackgroundColor
        let foreground = customization.dynamicForegroundColor
        return HomescreenHeaderViewInfo(header: header, backgroundColor: background, foregroundColor: foreground)
    }
    private func reloadHeaderFromConfig() {
        config = ICMConfig.shared()!
        guard let info = getHomeHeaderViewInfoFromConfig() else { return }
        headerUpdatingDelegate?.setViewState(for: info)
    }
    @objc func configChanged() { reloadHeaderFromConfig() }
    @objc func headerImageReady() { reloadHeaderFromConfig() }
    @objc func logoImageReady() { reloadHeaderFromConfig() }
}
