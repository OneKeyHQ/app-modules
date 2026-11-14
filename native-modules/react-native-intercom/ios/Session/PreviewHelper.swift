import UIKit

@objc(_TtC18IntercomSDKPrivate13PreviewHelper)
class PreviewHelper: NSObject {
    private var action: PreviewAction?
    private var mainQueue: any Dispatcher
    private var attachmentFileManager: AttachmentFileManager
    private var localFileURL: URL?
    private var onFailure: (([URL], Error?) -> Void)?
    init(mainQueue: any Dispatcher, attachmentFileManager: AttachmentFileManager, onFailure: (([URL], Error?) -> Void)?) {
        self.mainQueue = mainQueue
        self.attachmentFileManager = attachmentFileManager
        self.onFailure = onFailure
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    func preview(urls: [URL], action: PreviewAction?, from controller: UIViewController) {
        getLocalCopies(of: urls) { [weak self, controller] urls in
            self?.presentPreview(with: urls, action: action, from: controller)
        }
    }
    func preview(urls: [URL], action: PreviewAction?, from navigationController: UINavigationController?) {
        getLocalCopies(of: urls) { [weak self, navigationController] urls in
            self?.pushPreview(with: urls, action: action, in: navigationController)
        }
    }
    private func getLocalCopies(of urls: [URL], success: @escaping ([URL]) -> Void) {
        let local = urls.filter { $0.isFileURL }
        let remote = urls.filter { !$0.isFileURL }
        if remote.isEmpty { success(local); return }
        attachmentFileManager.saveLocalCopyOfFiles(at: remote) { result in
            switch result {
            case .success(let saved): success(local + saved)
            case .failure(let error):
                self.mainQueue.dispatch { self.onFailure?(remote, error) }
            }
        }
    }
    private func presentPreview(with urls: [URL], action: PreviewAction?, from controller: UIViewController) {
        let media = MediaPreviewerViewController(previewItems: urls, action: action)
        let navigation = UINavigationController(rootViewController: media)
        navigation.view.tintColor = DefaultThemeProvider.shared.theme.iconDefault
        navigation.navigationBar.tintColor = DefaultThemeProvider.shared.theme.iconDefault
        mainQueue.dispatch { controller.present(navigation, animated: true, completion: nil) }
    }
    private func pushPreview(with urls: [URL], action: PreviewAction?, in navigationController: UINavigationController?) {
        let media = MediaPreviewerViewController(previewItems: urls, action: action)
        mainQueue.dispatch { navigationController?.pushViewController(media, animated: true) }
    }
    func preview(image: UIImage, title: String?, from controller: UIViewController) {
        let loader = ITBImageLoader.default()!
        let preview = ImagePreviewerViewController(imageLoader: loader)
        let navigation = UINavigationController(rootViewController: preview)
        navigation.view.tintColor = DefaultThemeProvider.shared.theme.iconDefault
        navigation.navigationBar.tintColor = DefaultThemeProvider.shared.theme.iconDefault
        preview.imageView.image = image
        preview.navigationItem.title = title
        controller.present(navigation, animated: true, completion: nil)
    }
}
