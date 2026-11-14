import UIKit

// Deleted private witness signatures remain a separate ABI boundary; these are the original default consumers.
@_objcRuntimeName(_TtC18IntercomSDKPrivate20NavigationBarManager)
class NavigationBarManager {
    let themeProvider: ThemeProvider
    weak var collapsedHeaderView: CollapsedConversationHeaderView?
    weak var navigationController: ICMNavigationController?
    weak var managerDelegate: ConversationViewController?
    var config: ICMConfigProtocol
    let dataManager: ICMDataManagerProtocol
    let notificationCenter: NotificationCenterInterface
    var navigationBarMetricService: NavigationBarManagerMetricService
    let shouldUseConversationFirstNavigationBar: Bool

    init(collapsedHeaderView: CollapsedConversationHeaderView?, navigationController: ICMNavigationController?,
         delegate: ConversationViewController?, shouldUseConversationFirstNavigationBar: Bool,
         dataManager: ICMDataManagerProtocol, config: ICMConfigProtocol,
         notificationCenter: NotificationCenterInterface, navigationBarMetricService: NavigationBarManagerMetricService,
         themeProvider: ThemeProvider) {
        self.collapsedHeaderView = collapsedHeaderView
        self.navigationController = navigationController
        managerDelegate = delegate
        self.config = config
        self.dataManager = dataManager
        self.notificationCenter = notificationCenter
        self.navigationBarMetricService = navigationBarMetricService
        self.shouldUseConversationFirstNavigationBar = shouldUseConversationFirstNavigationBar
        self.themeProvider = themeProvider
    }

    func updateTitleViewWith(header: ConversationHeader) {
        collapsedHeaderView?.conversationHeader = header
    }
}
