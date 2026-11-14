import UIKit
import QuickLook

@objc(_TtC18IntercomSDKPrivate28MediaPreviewerViewController)
class MediaPreviewerViewController: QLPreviewController, QLPreviewControllerDelegate, QLPreviewControllerDataSource {
    private var previewItems: [URL]
    private var action: PreviewAction?
    init(previewItems: [URL], action: PreviewAction?) {
        self.previewItems = previewItems
        self.action = action
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.backButtonDisplayMode = .minimal
        dataSource = self
        delegate = self
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        guard let action = action else { return }
        let primaryAction = UIAction { [weak self] _ in
            guard let self = self else { return }
            self.dismiss(animated: true) {
                self.action?.handler?(self.previewItems)
            }
        }
        let button: UIBarButtonItem
        switch action.buttonType {
        case .text(let title): button = UIBarButtonItem(title: title, image: nil, primaryAction: primaryAction, menu: nil)
        case .image(let title, let image): button = UIBarButtonItem(title: title, image: image, primaryAction: primaryAction, menu: nil)
        case .system(let item): button = UIBarButtonItem(systemItem: item, primaryAction: primaryAction, menu: nil)
        }
        button.tag = 0x5c2757a05a1087a1
        navigationItem.setRightBarButton(button, animated: true)
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if #available(iOS 26.0, *) { navigationController?.setNavigationBarHidden(false, animated: false) }
        else { navigationController?.setNavigationBarHidden(false, animated: animated) }
    }
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int { previewItems.count }
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> any QLPreviewItem { previewItems[index] as NSURL }
    func previewController(_ controller: QLPreviewController, editingModeFor previewItem: any QLPreviewItem) -> QLPreviewItemEditingMode {
        action?.mode == .edit ? .createCopy : .disabled
    }
    func previewController(_ controller: QLPreviewController, didSaveEditedCopyOf previewItem: any QLPreviewItem, at modifiedContentsURL: URL) {
        for index in previewItems.indices {
            if previewItems[index] == previewItem.previewItemURL {
                previewItems[index] = modifiedContentsURL
                return
            }
        }
    }
}
