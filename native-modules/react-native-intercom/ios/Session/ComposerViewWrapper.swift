// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import SwiftUI
import UIKit

// Semantic recovery labels for the five proven action bytes; no private raw ABI is claimed.
enum AttachmentMenuAction { case camera, photos, files, gif, mic }

@objc(_TtC18IntercomSDKPrivate19ComposerViewWrapper)
class ComposerViewWrapper: UIView {
    weak var delegate: ConversationViewModel?
    var onAttachmentAction: ((AttachmentMenuAction) -> Void)?
    var onMediaTap: ((URL) -> Void)?
    var onMediaErrorTap: ((UUID, ComposerMediaError) -> Void)?
    var onMediaRemove: ((UUID) -> Void)?
    var isEnabled = false { didSet { viewModel.isEnabled = isEnabled } }
    var placeholderText = ""
    let viewModel: ComposerViewModel
    let themeProvider: any ThemeProvider
    var composerInputs: [ComposerInput]
    var initialText: String?
    var pendingSizeChangeWork: DispatchWorkItem?
    var hostingController: UIHostingController<ComposerView>?

    init(viewModel: ComposerViewModel, composerInputs: [ComposerInput], initialText: String?,
         placeholderText: String, isEnabled: Bool, themeProvider: any ThemeProvider) {
        self.viewModel = viewModel
        self.composerInputs = composerInputs
        self.initialText = initialText
        self.placeholderText = placeholderText
        self.isEnabled = isEnabled
        self.themeProvider = themeProvider
        viewModel.text = initialText ?? ""
        viewModel.isEnabled = isEnabled
        super.init(frame: .zero)
        setupSwiftUIView()
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func setupSwiftUIView() {
        let model = viewModel
        let theme = themeProvider
        let config = ICMConfig.shared()!
        let inputs = composerInputs
        let callbacks = createCallbacks()
        let composer = ComposerView(viewModel: model, themeProvider: theme, config: config, inputs: inputs, callbacks: callbacks)
        hostingController = UIHostingController(rootView: composer)
        configureHostingController()
        addSubview(hostingController!.view!)
        hostingController!.view!.translatesAutoresizingMaskIntoConstraints = false
        hostingController!.view!.topAnchor.constraint(equalTo: topAnchor).isActive = true
        if hostingController!.view!.effectiveUserInterfaceLayoutDirection == .leftToRight {
            hostingController!.view!.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
            hostingController!.view!.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        } else {
            hostingController!.view!.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
            hostingController!.view!.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        }
        hostingController!.view!.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    private func configureHostingController() {
        hostingController!.view!.backgroundColor = themeProvider.theme.base
        hostingController!.view!.layer.cornerRadius = 20
        hostingController!.view!.layer.masksToBounds = true
        if #available(iOS 16, *) { hostingController!.sizingOptions = [.intrinsicContentSize] }
        if #available(iOS 16.4, *) { hostingController!.safeAreaRegions = [] }
    }

    override var intrinsicContentSize: CGSize {
        guard let hostingController else { return .zero }
        return hostingController.view!.intrinsicContentSize
    }

    func setText(_ text: String) { viewModel.text = text; handleTextChanged() }

    func handleTextChanged() {
        if let hostingController { hostingController.view!.invalidateIntrinsicContentSize() }
        invalidateIntrinsicContentSize()
        setNeedsLayout()
        layoutIfNeeded()
        pendingSizeChangeWork?.cancel()
        let work = DispatchWorkItem { [weak self] in
            let controller: ConversationViewController?
            do {
                let model: ConversationViewModel?
                do {
                    guard let wrapper = self else { return }
                    model = wrapper.delegate
                }
                controller = model?.view
            }
            guard let controller, let index = controller.lastItemIndexPath else { return }
            controller.conversationCollectionView.scrollToItem(at: index, at: .bottom, animated: true)
        }
        pendingSizeChangeWork = work
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1, execute: work)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        if let hostingController { hostingController.view!.invalidateIntrinsicContentSize() }
        invalidateIntrinsicContentSize()
        let size = hostingController.map { $0.view!.intrinsicContentSize } ?? .zero
        let boundsSize = bounds.size
        if size.width != boundsSize.width || size.height != boundsSize.height {
            guard let currentView = delegate?.view, let index = currentView.lastItemIndexPath else { return }
            currentView.conversationCollectionView.scrollToItem(at: index, at: .bottom, animated: true)
        }
    }

    private func createCallbacks() -> ComposerCallbacks {
        ComposerCallbacks(onSend: { [weak self] text in
            guard let self else { return }
            withExtendedLifetime(self) {
                let urls = self.viewModel.getUploadedMediaLocalURLs()
                self.delegate?.handleSend(text: text, mediaURLs: urls)
            }
        }, onCameraSelected: { [weak self] in
            guard let self else { return }
            withExtendedLifetime(self) { self.onAttachmentAction?(.camera) }
        }, onPhotosSelected: { [weak self] in
            guard let self else { return }
            withExtendedLifetime(self) { self.onAttachmentAction?(.photos) }
        }, onFilesSelected: { [weak self] in
            guard let self else { return }
            withExtendedLifetime(self) { self.onAttachmentAction?(.files) }
        }, onGIFSelected: { [weak self] in
            guard let self else { return }
            withExtendedLifetime(self) { self.onAttachmentAction?(.gif) }
        }, onMicSelected: { [weak self] in
            guard let self else { return }
            withExtendedLifetime(self) { self.onAttachmentAction?(.mic) }
        }, onTextChanged: { [weak self] _ in self?.handleTextChanged() },
        onMediaTap: { [weak self] url in
            let callback = self?.onMediaTap
            callback?(url)
        }, onMediaErrorTap: { [weak self] id, error in
            let callback = self?.onMediaErrorTap
            callback?(id, error)
        }, onMediaRemove: { [weak self] id in
            let callback = self?.onMediaRemove
            callback?(id)
        })
    }
}
