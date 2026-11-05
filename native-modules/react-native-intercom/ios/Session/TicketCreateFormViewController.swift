// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate30TicketCreateFormViewController)
class TicketCreateFormViewController: IntercomTableViewController, UITableViewDataSource, ICMErrorViewDelegate, ICMContentViewController, MediaPickerDelegate, FilePickerDelegate, CameraPickerDelegate {
    var viewModel: TicketCreateFormViewModel?
    @objc weak var contentDelegate: (any ICMContentViewControllerDelegate)?
    let themeProvider: any ThemeProvider
    lazy var errorView: UIView = {
        let container = UIView()
        container.backgroundColor = themeProvider.theme.neutralContainerAlt
        let translator = TranslationManager.messenger
        let title = translator.translation(forKey: "spaces_error_title")
        let message = translator.translation(forKey: "spaces_error_description")
        let action = translator.translation(forKey: "reload")
        let error = ICMErrorView(title: title, message: message, showActionButton: true, actionButtonTitle: action)!
        container.addSubview(error)
        error.translatesAutoresizingMaskIntoConstraints = false
        error.centerXAnchor.constraint(equalTo: error.superview!.centerXAnchor).isActive = true
        error.translatesAutoresizingMaskIntoConstraints = false
        error.centerYAnchor.constraint(equalTo: error.superview!.centerYAnchor).isActive = true
        error.delegate = self
        return container
    }()
    lazy var loadingSpinner: UIActivityIndicatorView = {
        let spinner = UIActivityIndicatorView(style: .large)
        spinner.hidesWhenStopped = true
        return spinner
    }()
    lazy var previewer: PreviewHelper = {
        let failure: ([URL], Error?) -> Void = { [weak self] _, _ in self?.openMediaSelectionErrorAlert() }
        let queue = AsyncQueue.main
        let files = FileManager.default
        let session = URLSession.shared
        let callback = DispatchQueue.main
        let manager = AttachmentFileManager(fileManager: files, urlSession: session, callbackQueue: callback)
        return PreviewHelper(mainQueue: queue, attachmentFileManager: manager, onFailure: failure)
    }()
    lazy var mediaPicker = MediaPicker(attachmentFileManager: makeFileManager(), delegate: self)
    lazy var filePicker = FilePicker(attachmentFileManager: makeFileManager(), config: ICMConfig.shared()!, delegate: self)
    lazy var formTableView: UITableView = {
        let table = UITableView()
        table.dataSource = self
        table.separatorStyle = .none
        table.tableFooterView = UIView()
        table.rowHeight = UITableView.automaticDimension
        table.allowsSelection = false
        table.contentInset = UIEdgeInsets(top: 20, left: 0, bottom: 0, right: 0)
        table.keyboardDismissMode = .onDrag
        let types: [TicketCreateFormTableViewCell.Type] = [TicketCreateFormEmailTableViewCell.self, TicketCreateFormTextTableViewCell.self, TicketCreateFormLongTextTableViewCell.self, TicketCreateFormIntegerTableViewCell.self, TicketCreateFormFloatTableViewCell.self, TicketCreateFormBooleanTableViewCell.self, TicketCreateFormDateTimeTableViewCell.self, TicketCreateFormPickerTableViewCell.self, TicketCreateFormSubmitTableViewCell.self, TicketCreateFormCancelTableViewCell.self, TicketCreateFormFileUploadTableViewCell.self]
        for type in types { table.register(type, forCellReuseIdentifier: _typeName(type, qualified: false)) }
        return table
    }()
    @objc(initWithTicketType:conversation:)
    init(ticketType: TicketType, conversation: ICMConversation?) {
        themeProvider = DefaultThemeProvider.shared
        super.init(nibName: nil, bundle: nil)
        viewModel = TicketCreateFormViewModel(viewController: self, ticketType: ticketType, conversation: conversation)
    }
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func makeFileManager() -> AttachmentFileManager {
        AttachmentFileManager(fileManager: .default, urlSession: URLSession.shared, callbackQueue: .main)
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.backButtonDisplayMode = .minimal
        viewModel!.viewDidLoad()
        contentDelegate?.contentDidLoad(withDismissEnabled: true)
        setupFormTableView()
        view!.backgroundColor = DefaultThemeProvider.shared.theme.base
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if #available(iOS 26.0, *) { navigationController?.setNavigationBarHidden(false, animated: false) }
        else { navigationController?.setNavigationBarHidden(false, animated: animated) }
        viewModel!.startListeningForNotifications()
    }
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        let model = viewModel!
        model.notificationCenter.removeObserver(model)
    }
    @objc func closeButtonTapped() { viewModel!.closeButtonTapped(); contentDelegate?.closeContentModal() }
    @objc func reload() {}
    @objc func viewDidDismissBySwipe() {}
    @objc func didSelectErrorAction() { viewModel!.fetchForm() }
    private func setupFormTableView() {
        view!.addSubview(formTableView)
        pinEdges(formTableView)
        view!.addSubview(loadingSpinner)
        loadingSpinner.translatesAutoresizingMaskIntoConstraints = false
        loadingSpinner.centerXAnchor.constraint(equalTo: loadingSpinner.superview!.centerXAnchor).isActive = true
        loadingSpinner.translatesAutoresizingMaskIntoConstraints = false
        loadingSpinner.centerYAnchor.constraint(equalTo: loadingSpinner.superview!.centerYAnchor).isActive = true
        view!.addSubview(errorView)
        pinEdges(errorView)
    }
    private func pinEdges(_ child: UIView) {
        child.translatesAutoresizingMaskIntoConstraints = false
        child.topAnchor.constraint(equalTo: child.superview!.topAnchor).isActive = true
        if child.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            child.translatesAutoresizingMaskIntoConstraints = false
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
            child.translatesAutoresizingMaskIntoConstraints = false
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
        } else {
            child.translatesAutoresizingMaskIntoConstraints = false
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
            child.translatesAutoresizingMaskIntoConstraints = false
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
        }
        child.translatesAutoresizingMaskIntoConstraints = false
        child.bottomAnchor.constraint(equalTo: child.superview!.bottomAnchor).isActive = true
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { viewModel!.formCellItems.count }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = viewModel!.formCellItems[indexPath.row]
        let name = _typeName(item.cellType, qualified: false)
        let cell = tableView.dequeueReusableCell(withIdentifier: name, for: indexPath) as? TicketCreateFormTableViewCell ?? TicketCreateFormTableViewCell()
        cell.configure(with: item)
        return cell
    }
    func adjustForKeyboard(_ notification: NSNotification) {
        guard notification.name == UIResponder.keyboardWillShowNotification,
              let frame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect else {
            formTableView.contentInset = UIEdgeInsets(top: 20, left: 0, bottom: 0, right: 0)
            return
        }
        UIResponder.internalCurrentFirstResponder = nil
        UIApplication.shared.sendAction(#selector(UIResponder.findFirstResponder(sender:)), to: nil, from: nil, for: nil)
        var cell: TicketCreateFormLongTextTableViewCell?
        if let responder = UIResponder.internalCurrentFirstResponder as? UIView {
            var candidate = responder.superview
            while let current = candidate {
                if let found = current as? TicketCreateFormLongTextTableViewCell { cell = found; break }
                candidate = current.superview
            }
        }
        formTableView.contentInset = UIEdgeInsets(top: 20, left: 0, bottom: frame.height, right: 0)
        if let cell = cell { scrollTo(view: cell, keyboardHeight: frame.height) }
    }
    private func scrollTo(view child: UIView, keyboardHeight: CGFloat) {
        let navHeight = navigationController?.navigationBar.frame.height ?? 0
        let insetTop = formTableView.contentInset.top
        let rect = child.convert(child.bounds, to: formTableView as any UICoordinateSpace)
        let available = CGRect(x: 0, y: 0, width: formTableView.bounds.width, height: formTableView.bounds.height - keyboardHeight)
        let y = available.contains(rect) ? -(navHeight + insetTop) : rect.origin.y + rect.height - formTableView.bounds.height + keyboardHeight
        formTableView.setContentOffset(CGPoint(x: 0, y: y), animated: true)
    }
    func scrollTo(indexPath: IndexPath, setVoiceOverToIndex: Bool) {
        UIView.animate(withDuration: 0.3, animations: { self.formTableView.scrollToRow(at: indexPath, at: .top, animated: false) }, completion: { _ in
            let cell = self.formTableView.cellForRow(at: indexPath)
            UIAccessibility.post(notification: .layoutChanged, argument: cell?.contentView)
        })
    }
    func presentAttachmentPickerSheet(selectionLimit: Int, sourceView: UIView?) {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.popoverPresentationController?.sourceView = sourceView
        let translator = TranslationManager.messenger
        alert.addAction(UIAlertAction(title: translator.translation(forKey: "photo_and_video_library"), style: .default) { [weak self] _ in
            guard let self = self else { return }
            let picker = self.mediaPicker
            let title = TranslationManager.messenger.translation(forKey: "add")
            picker.presentPhotoGallery(from: self, selectionLimit: selectionLimit, actionButtonTitle: title)
        })
        alert.addAction(UIAlertAction(title: translator.translation(forKey: "files"), style: .default) { [weak self] _ in
            guard let self = self else { return }
            self.filePicker.presentFilePicker(from: self, selectionLimit: selectionLimit)
        })
        alert.addAction(UIAlertAction(title: translator.translation(forKey: "cancel"), style: .cancel, handler: nil))
        present(alert, animated: true, completion: nil)
    }
    func picker(_ picker: MediaPicker, didPickMedia items: [(id: UUID, type: ComposerMediaType)]) -> [UUID] { items.map(\.id) }
    func picker(_ picker: FilePicker, didPickMedia items: [(id: UUID, type: ComposerMediaType)]) -> [UUID] { items.map(\.id) }
    func picker(_ picker: CameraPicker, didPickMedia id: UUID, type: ComposerMediaType) -> Bool { true }
    func picker(_ picker: MediaPicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>) { finishPicking(result) }
    func picker(_ picker: FilePicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>) { finishPicking(result) }
    func picker(_ picker: CameraPicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>) { finishPicking(result) }
    private func finishPicking(_ result: Result<URL, PickerError>) {
        switch result {
        case .success(let url):
            guard let upload = ITBUpload.createUpload(forFileURL: url) else { return }
            Task { @MainActor [weak self, upload] in
                let model: TicketCreateFormViewModel
                do { guard let controller = self else { return }; model = controller.viewModel! }
                if let item = model.fileUploadCellItem {
                    let url: URL = upload.fileURL
                    item.addAttachmentView(for: upload, localResourceUrl: url)
                }
                model.performUpload(upload)
            }
        case .failure(let error):
            Task { @MainActor [weak self, error] in
                _ = error
                self?.openMediaSelectionErrorAlert()
            }
        }
    }
    func openMediaSelectionErrorAlert() {
        let translator = TranslationManager.messenger
        let title = translator.translation(forKey: "error_selecting_media_title")
        let message = translator.translation(forKey: "error_selecting_media_title_description")
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: translator.translation(forKey: "ok"), style: .default, handler: nil))
        present(alert, animated: true, completion: nil)
    }
    func showPreviewWithDeleteOption(url: URL, id: String, fileName: String, attributeId: String, sourceView: UIView) {
        let action = PreviewAction(mode: .view, buttonType: .system(item: .trash), handler: { [weak self, id, attributeId, fileName, sourceView] _ in
            guard let self = self else { return }
            let title = TranslationManager.messenger.translation(forKey: "delete_attachment")
            let delete = UIAlertAction(title: title, style: .destructive) { [weak self, id, attributeId] _ in
                let model: TicketCreateFormViewModel
                do { guard let controller = self else { return }; model = controller.viewModel! }
                (model.getInputCellItem(withId: attributeId) as? TicketCreateFormFileUploadCellItem)?.deleteAttachment(withId: id)
            }
            let alert = UIAlertController(title: fileName, message: nil, preferredStyle: .actionSheet)
            alert.popoverPresentationController?.sourceView = sourceView
            alert.addAction(delete)
            alert.addAction(UIAlertAction(title: TranslationManager.messenger.translation(forKey: "cancel"), style: .cancel) { _ in })
            self.present(alert, animated: true, completion: nil)
        })
        previewer.preview(urls: [url], action: action, from: self)
    }
}
