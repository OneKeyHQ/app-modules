// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate25TicketCreateFormViewModel)
class TicketCreateFormViewModel: NSObject {
    let conversation: ICMConversation?
    var ticket: Ticket?
    var fileUploadCellItem: TicketCreateFormFileUploadCellItem?
    var formCellItems: [any TicketCreateFormCellItem] = []
    // Deleted private interfaces use the proved concrete default graph.
    weak var view: TicketCreateFormViewController?
    let interactor: TicketCreateFormInteractor
    var identityStore: ICMIdentityStore
    var ticketType: TicketType
    let notificationCenter: any NotificationCenterInterface
    let metricService: any MessagesMetricServiceProtocol
    let queue: any Dispatcher
    let config: any ICMConfigProtocol
    var isLoading = true
    @objc(initWithViewController:ticketType:conversation:)
    init(viewController: TicketCreateFormViewController, ticketType: TicketType, conversation: ICMConversation?) {
        let interactor = TicketCreateFormInteractor(apiBridge: SwiftAPIBridge())
        let identity = ICMIdentityStore.currentIdentity()
        let center = NotificationCenter.default
        let metric = MessagesMetricService(metricInstance: ICMMetric.sharedInstance())
        let queue = AsyncQueue.main
        let config = ICMConfig.shared()!
        self.interactor = interactor
        identityStore = identity
        self.ticketType = ticketType
        self.conversation = conversation
        notificationCenter = center
        metricService = metric
        self.queue = queue
        self.config = config
        view = viewController
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    func viewDidLoad() {
        if let view = view { view.contentDelegate?.configureContentTitle?(ticketType.name) }
        fetchForm()
    }
    func fetchForm() {
        setupInitialFormState()
        let currentValues = getCurrentFormValues()
        let request = buildTicketFormRequest()
        Task { @MainActor in
            do { self.handleSuccessfulFormResponse(try await self.interactor.getTicketForm(request: request), currentValues: currentValues) }
            catch { self.handleFormError() }
        }
    }
    func startListeningForNotifications() {
        notificationCenter.addObserver(self, selector: #selector(adjustForKeyboard(notification:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        notificationCenter.addObserver(self, selector: #selector(adjustForKeyboard(notification:)), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    @objc(adjustForKeyboardWithNotification:)
    func adjustForKeyboard(notification: NSNotification) { view?.adjustForKeyboard(notification) }
    func setupInitialFormState() {
        view?.errorView.isHidden = true
        isLoading = true
        refreshSubmitButton()
        if !formCellItems.isEmpty { return }
        if let view = view { view.formTableView.isHidden = true; view.loadingSpinner.startAnimating() }
    }
    func getCurrentFormValues() -> [(String, Any)] {
        var values: [(String, Any)] = []
        for case let input as any TicketCreateFormInputCellItem in formCellItems {
            guard let value = input.attribute.value else { continue }
            guard let id = input.attribute.attributeId, !id.isEmpty else { continue }
            values.append((id, value))
        }
        return values
    }
    func buildTicketFormRequest() -> TicketFormRequest {
        let attributes = getAttributesFrom(cellItems: formCellItems).compactMap { dictionary -> TicketFormRequestAttribute? in
            guard let id = dictionary["descriptor_id"] as? String else { return nil }
            return TicketFormRequestAttribute(descriptorID: id, value: dictionary["value"])
        }
        return TicketFormRequest(ticketTypeID: ticketType.id, attributes: attributes)
    }
    func handleSuccessfulFormResponse(_ response: TicketFormResponse, currentValues: [(String, Any)]) {
        isLoading = false
        refreshSubmitButton()
        if let view = view { view.formTableView.isHidden = false; view.loadingSpinner.stopAnimating() }
        do {
            let attributes = try processFormFields(response.formFields, currentValues: currentValues)
            ticketType = TicketType(id: String(response.ticketTypeId), name: response.name, emoji: nil, attributes: attributes, isArchived: false)
            configureFormCellTypes()
        } catch { handleFormError() }
    }
    func handleFormError() {
        if let view = view { view.formTableView.isHidden = false; view.loadingSpinner.stopAnimating() }
        view?.errorView.isHidden = false
    }
    func processFormFields(_ fields: [TicketFormResponseFormField], currentValues: [(String, Any)]) throws -> [FormAttribute] {
        _ = JSONEncoder()
        var attributes: [FormAttribute] = []
        for field in fields {
            guard let attribute = FormAttribute(with: field) else { continue }
            if let saved = currentValues.first(where: { attribute.attributeId == $0.0 }) {
                if attribute.formInputType == .options {
                    attribute.value = attribute.listOptions?.first(where: { $0.label == saved.1 as? String })?.label
                } else { attribute.value = saved.1 }
            }
            attributes.append(attribute)
        }
        return attributes
    }
    func getAttributesFrom(cellItems: [any TicketCreateFormCellItem]) -> [[String: Any]] {
        let inputs = cellItems.compactMap { $0 as? any TicketCreateFormInputCellItem }
        let identified = inputs.filter { $0.attribute.attributeId != nil }
        let valid = identified.filter { $0.valid && $0.attribute.value != nil }
        return valid.compactMap { convertAttribute($0) }
    }
    private func convertAttribute(_ input: any TicketCreateFormInputCellItem) -> [String: Any]? {
        guard let value = input.attribute.value, let id = input.attribute.attributeId else { return nil }
        func result(_ value: Any) -> [String: Any] { ["descriptor_id": id, "value": value] }
        if input.attribute.formInputType == .boolean {
            let first = (value as? [String])?.first
            return result(first == TranslationManager.messenger.translation(forKey: "attribute_collector_positive"))
        }
        if input.attribute.formInputType == .integer, let text = value as? String, let number = Int(text) { return result(number) }
        if input.attribute.formInputType == .float, let text = value as? String { return result(NSDecimalNumber(string: text)) }
        if input.attribute.formInputType == .dateTime || input.attribute.formInputType == .date {
            let number = (value as? Double) ?? (value as? Int).map(Double.init)
            if let number = number { return result(ISO8601DateFormatter().string(from: Date(timeIntervalSince1970: number))) }
        }
        if input.attribute.formInputType == .options, let text = value as? String,
           let option = input.attribute.listOptions?.first(where: { $0.label == text }) { return result(option.id) }
        if input.attribute.formInputType == .dateTime || input.attribute.formInputType == .date { return nil }
        return result(value)
    }
    private func cellType(for attribute: FormAttribute) -> TicketCreateFormTableViewCell.Type {
        switch attribute.formInputType {
        case .text, .phone, .website, .unknown: return TicketCreateFormTextTableViewCell.self
        case .email: return TicketCreateFormEmailTableViewCell.self
        case .options: return TicketCreateFormPickerTableViewCell.self
        case .integer: return TicketCreateFormIntegerTableViewCell.self
        case .float: return TicketCreateFormFloatTableViewCell.self
        case .date, .dateTime: return TicketCreateFormDateTimeTableViewCell.self
        case .boolean: return TicketCreateFormBooleanTableViewCell.self
        case .multiline: return TicketCreateFormLongTextTableViewCell.self
        case .files: return TicketCreateFormFileUploadTableViewCell.self
        }
    }
    private func makeStandardItem(_ attribute: FormAttribute, detail: String?) -> TicketCreateFormStandardInputCellItem {
        let currentView = view
        return TicketCreateFormStandardInputCellItem(cellType: cellType(for: attribute), attribute: attribute, detail: detail, isEnabled: true, refreshAction: { [weak currentView] in currentView?.formTableView.reloadData() }, onValueChange: { [weak self, attribute] _ in
            guard let self = self, attribute.isControllingAttribute else { return }
            self.fetchForm()
        })
    }
    func configureFormCellTypes() {
        let oldFiles = formCellItems.compactMap { $0 as? TicketCreateFormFileUploadCellItem }
        var email: TicketCreateFormStandardInputCellItem?
        if identityStore.userIdentity.isIdentified(), identityStore.userIdentity.email != nil {
            let name = TranslationManager.messenger.translation(forKey: "placeholder_email_input")
            let attribute = FormAttribute(identityEmailName: name, value: identityStore.userIdentity.email)
            email = makeStandardItem(attribute, detail: TranslationManager.messenger.translation(forKey: "tickets_email_copy"))
        }
        var items: [any TicketCreateFormCellItem] = []
        for attribute in ticketType.attributes {
            if attribute.formInputType == .files {
                let currentView = view
                let freshConfig = ICMConfig.shared()!
                let maximum = attribute.type == "files" && attribute.identifier == "multiple" ? attribute.maximumSelection : 1
                let description: String?
                if attribute.type == "files" && attribute.identifier == "multiple" {
                    description = TranslationManager.messenger.translation(forKey: "file_max_limit").replacingOccurrences(of: "{limit}", with: String(maximum))
                } else { description = nil }
                let item = TicketCreateFormFileUploadCellItem(cellType: TicketCreateFormFileUploadTableViewCell.self, attribute: attribute, detail: nil, maxFileCount: maximum, isEnabled: true, description: description, delegate: self, refreshAction: { [weak currentView] in currentView?.formTableView.reloadData() }, onValueChange: { [weak self, attribute] _ in
                    guard let self = self, attribute.isControllingAttribute else { return }
                    self.fetchForm()
                }, config: freshConfig)
                if let id = attribute.attributeId, let old = oldFiles.first(where: { $0.attribute.attributeId == id }) { item.takeOverAttachments(from: old) }
                items.append(item)
            } else if attribute.formInputType != .files { items.append(makeStandardItem(attribute, detail: nil)) }
        }
        let submit = TicketCreateFormActionCellItem(cellType: TicketCreateFormSubmitTableViewCell.self, buttonTitle: TranslationManager.messenger.translation(forKey: "tickets_create_ticket"), buttonImage: ICMAssets.ticketIcon(), buttonAction: { self.submitButtonTappedClosure() })
        let currentView = view
        let cancel = TicketCreateFormActionCellItem(cellType: TicketCreateFormCancelTableViewCell.self, buttonTitle: TranslationManager.messenger.translation(forKey: "cancel"), buttonImage: nil, buttonAction: { [weak currentView] in currentView?.dismiss(animated: true, completion: nil) })
        formCellItems = (email.map { [$0 as any TicketCreateFormCellItem] } ?? []) + items + [submit, cancel]
        view?.formTableView.reloadData()
        refreshSubmitButton()
    }
    func closeButtonTapped() {
        let inputs = formCellItems.compactMap { $0 as? any TicketCreateFormInputCellItem }
        for input in inputs { input.attribute.value = nil }
    }
    func allUploadsComplete() -> Bool {
        let files = formCellItems.compactMap { $0 as? TicketCreateFormFileUploadCellItem }
        let values = files.map { $0.allUploadProgressComplete }
        return values.allSatisfy { $0 }
    }
    func refreshSubmitButton() {
        let actions = formCellItems.compactMap { $0 as? TicketCreateFormActionCellItem }
        guard let submit = actions.first(where: { $0.cellType is TicketCreateFormSubmitTableViewCell.Type }) else { return }
        let complete = allUploadsComplete()
        let loading = isLoading
        submit.cellDelegate?.setActionButtonState(complete && !loading, isLoading: loading)
    }
    func refreshActionCells() {
        let refreshable = formCellItems.compactMap { $0 as? any TicketCreateFormCellRefreshable }
        let actions = refreshable.filter { $0 is TicketCreateFormActionCellItem }
        for action in actions { action.refresh() }
    }
    func refreshInputCells() {
        let refreshable = formCellItems.compactMap { $0 as? any TicketCreateFormCellRefreshable }
        let inputs = refreshable.filter { $0 is any TicketCreateFormInputCellItem }
        for input in inputs { input.refresh() }
    }
    func validateAttributesFrom(_ snapshot: [any TicketCreateFormCellItem]) -> Bool {
        refreshInputCells()
        for (index, item) in snapshot.enumerated() {
            guard let input = item as? any TicketCreateFormInputCellItem else { continue }
            if !input.valid && input.attribute.required {
                view?.scrollTo(indexPath: IndexPath(row: index, section: 0), setVoiceOverToIndex: true)
                return false
            }
        }
        return true
    }
    func submitButtonTappedClosure() {
        if let view = view { view.view!.endEditing(true) }
        guard validateAttributesFrom(formCellItems) else { refreshSubmitButton(); return }
        if let id = Int(ticketType.id) { metricService.addCreateTicketFormIsSubmitted(conversationId: conversation?.conversationId, ticketTypeId: id) }
        let id = ticketType.id
        let conversationId = conversation?.conversationId
        let attributes = getAttributesFrom(cellItems: formCellItems)
        interactor.apiBridge.createTicket(for: id, conversationId: conversationId, attributes: attributes) { [weak self] result in
            guard let self = self, let view = self.view else { return }
            switch result {
            case .success(let ticket):
                self.conversation?.ticket = ticket
                self.ticket = ticket
                self.queue.dispatch {
                    self.refreshActionCells()
                    if let navigation = view.navigationController {
                        ICMPresentationManager.sharedInstance().pushTicket(ticket, from: navigation, didCreateTicket: true, triggerSource: 0)
                    }
                }
            case .failure:
                self.queue.dispatch {
                    self.refreshActionCells()
                    self.showCreationError { [weak retryOwner = self] in retryOwner?.submitButtonTappedClosure() }
                }
            }
        }
    }
    private func showCreationError(retry: @escaping () -> Void) {
        guard let view = view else { return }
        let translator = TranslationManager.messenger
        let title = translator.translation(forKey: "spaces_error_title")
        let message = translator.translation(forKey: "ticket_could_not_be_created")
        let again = UIAlertAction(title: translator.translation(forKey: "try_again"), style: .default) { _ in retry() }
        let cancel = UIAlertAction(title: translator.translation(forKey: "cancel"), style: .cancel, handler: nil)
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(again)
        alert.addAction(cancel)
        view.present(alert, animated: true, completion: nil)
    }
    func performUpload(_ upload: ITBUpload) {
        let count = upload.uploadData.count
        if count > config.attachmentSettings.uploadSizeLimit { upload.uploadState = 2; return }
        interactor.apiBridge.uploadFile(upload: upload) { [weak self, upload] result in
            let queue: any Dispatcher
            do { guard let owner = self else { return }; queue = owner.queue }
            queue.dispatch {
                switch result {
                case .failure: upload.uploadState = 2
                case .success: upload.uploadProgress = 1
                }
            }
        }
    }
    func showAttachmentPicker(cellItem: TicketCreateFormFileUploadCellItem, selectionLimit: Int, sourceView: UIView?) {
        fileUploadCellItem = cellItem
        view?.presentAttachmentPickerSheet(selectionLimit: selectionLimit, sourceView: sourceView)
    }
    func uploadingAttachmentViewTapped(_ dataSource: any AttachmentViewDataSource, cellItem: TicketCreateFormFileUploadCellItem, sourceView: UIView) {
        dataSource.upload?.uploadDataTask?.suspend()
        let stop = UIAlertAction(title: TranslationManager.messenger.translation(forKey: "stop_upload"), style: .destructive) { [weak self, dataSource, cellItem] _ in
            if let upload = dataSource.upload { self?.stopAttachmentUpload(upload, cellItem: cellItem) }
        }
        guard let view = view else { return }
        let alert = UIAlertController(title: dataSource.fileName, message: nil, preferredStyle: .actionSheet)
        alert.popoverPresentationController?.sourceView = sourceView
        alert.addAction(stop)
        alert.addAction(UIAlertAction(title: TranslationManager.messenger.translation(forKey: "cancel"), style: .cancel) { [dataSource] _ in dataSource.upload?.uploadDataTask?.resume() })
        view.present(alert, animated: true, completion: nil)
    }
    func stopAttachmentUpload(_ upload: ITBUpload, cellItem: TicketCreateFormFileUploadCellItem) {
        upload.cancelled = true
        let id: String = upload.localId
        cellItem.deleteAttachment(withId: id)
        view?.formTableView.reloadData()
    }
    func uploadedAttachmentViewTapped(_ dataSource: any AttachmentViewDataSource, cellItem: TicketCreateFormFileUploadCellItem, sourceView: UIView) {
        guard let attributeId = cellItem.attribute.attributeId, let view = view else { return }
        let url = dataSource.attachmentUrl
        let id = dataSource.id
        let name = dataSource.fileName
        view.showPreviewWithDeleteOption(url: url, id: id, fileName: name, attributeId: attributeId, sourceView: sourceView)
    }
    func errorAttachmentViewTapped(_ dataSource: any AttachmentViewDataSource, cellItem: TicketCreateFormFileUploadCellItem, sourceView: UIView) {
        guard let view = view else { return }
        var actions: [UIAlertAction] = []
        if dataSource.attachmentError == .uploadFailed, let upload = dataSource.upload {
            actions.append(UIAlertAction(title: TranslationManager.messenger.translation(forKey: "try_again"), style: .default) { [weak self, upload] _ in
                guard let self = self else { return }
                upload.uploadState = 1
                self.performUpload(upload)
            })
        }
        actions.append(UIAlertAction(title: TranslationManager.messenger.translation(forKey: "delete_attachment"), style: .destructive) { [weak self, cellItem, dataSource] _ in
            guard let attributeId = cellItem.attribute.attributeId, let self = self else { return }
            let id = dataSource.id
            (self.getInputCellItem(withId: attributeId) as? TicketCreateFormFileUploadCellItem)?.deleteAttachment(withId: id)
        })
        let alert = UIAlertController(title: titleForAttachmentError(dataSource), message: nil, preferredStyle: .actionSheet)
        alert.popoverPresentationController?.sourceView = sourceView
        for action in actions { alert.addAction(action) }
        alert.addAction(UIAlertAction(title: TranslationManager.messenger.translation(forKey: "cancel"), style: .cancel) { _ in })
        view.present(alert, animated: true, completion: nil)
    }
    private func titleForAttachmentError(_ dataSource: any AttachmentViewDataSource) -> String? {
        let prefix = TranslationManager.messenger.translation(forKey: "upload_failed")
        switch dataSource.attachmentError {
        case .attachmentSizeLimitExceeded(let limit): return prefix + ". " + TranslationManager.messenger.translation(forKey: "upload_max_files_size").replacingOccurrences(of: "{limit}", with: String(limit)) + "."
        case .attachmentCountExceeded(let limit): return prefix + ". " + TranslationManager.messenger.translation(forKey: "file_max_limit").replacingOccurrences(of: "{limit}", with: String(limit)) + "."
        default: return prefix
        }
    }
    func getInputCellItem(withId id: String) -> (any TicketCreateFormInputCellItem)? {
        let inputs = formCellItems.compactMap { $0 as? any TicketCreateFormInputCellItem }
        return inputs.first { $0.attribute.attributeId == id }
    }
}
