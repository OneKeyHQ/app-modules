import Foundation
import UIKit

@objc(_TtC18IntercomSDKPrivate22ConversationInteractor)
class ConversationInteractor: NSObject, IntercomNexusObserverProtocol {
    var initialMessage: String?
    var undeliveredParts: [ICMConversationPart] = []
    var localArticleMetadataPart: ICMConversationPart?
    var partsToSendAfterCreation: [ICMConversationPart] = []
    let existingConversation: ICMConversation
    var undeliveredConversation: ICMConversation?
    var conversationId: String?
    private var newConversationStartRefused = false
    let apiBridge: SwiftAPIBridge
    let dataManager: any ICMDataManagerProtocol
    private let config: ICMConfig
    private let notificationCenter: any NotificationCenterInterface
    private var articleMetadata: ArticleMetaData?
    var adminDidBeginTyping: ((TypingAdminDetails?) -> Void)?
    var adminDidEndTyping: (() -> Void)?
    var botDidBeginThinking: (() -> Void)?
    var finDidStreamPart: (([ITBBlock], Int) -> Void)?
    var finDidEndStreaming: (() -> Void)?
    private let videoFileProcessor: any VideoFileProcessorProtocol
    private var nexusDidEverDisconnect = false
    var typingBaseline: TypingBaseline?

    init(apiBridge: SwiftAPIBridge, dataManager: any ICMDataManagerProtocol, config: ICMConfig,
         notificationCenter: any NotificationCenterInterface, conversation: ICMConversation,
         videoFileProcessor: any VideoFileProcessorProtocol) {
        self.apiBridge = apiBridge
        self.dataManager = dataManager
        self.config = config
        self.notificationCenter = notificationCenter
        articleMetadata = conversation.articleMetaData
        conversationId = conversation.conversationId
        existingConversation = conversation
        initialMessage = conversation.initialMessage
        self.videoFileProcessor = videoFileProcessor
        super.init()
        dataManager.conversationToMark = conversationId
    }

    deinit {
        dataManager.conversationToMark = nil
        IntercomNexusClient.shared()!.removeObserver(self)
    }

    func updateWithExistingConversationIfNeeded() {
        if conversationId != nil {
            dataManager.update(with: existingConversation)
            dataManager.conversationToMark = conversationId
        }
        IntercomNexusClient.shared()!.addObserver(self)
    }

    var poweredByForConversation: PoweredBy? {
        let first: ICMConversation?
        if let id = conversationId { first = dataManager.findExistingConversation(withId: id) }
        else { first = undeliveredConversation }
        guard let first, first.conversationId != nil else { return dataManager.poweredBy }
        let second: ICMConversation?
        if let id = conversationId { second = dataManager.findExistingConversation(withId: id) }
        else { second = undeliveredConversation }
        return second?.poweredBy
    }

    func shouldFetchNewConversation() -> Bool {
        let initialArticle = articleMetadata
        let initialId = conversationId
        let flag = initialArticle != nil && initialId == nil
        let found: ICMConversation?
        let lookupHadId: Bool
        if let id = conversationId {
            lookupHadId = true
            found = dataManager.findExistingConversation(withId: id)
        } else {
            lookupHadId = false
            found = undeliveredConversation
        }
        if found != nil {
            if !flag { return false }
        } else {
            let noArticle = lookupHadId ? articleMetadata == nil : initialArticle == nil
            if !noArticle && !flag { return false }
        }
        return existingConversation.ticket == nil
    }
    private func ratingBlock(in part: ICMConversationPart) -> ICMConversationRatingBlock? {
        guard let blocks = part.blocks else { return nil }
        for block in blocks {
            if block.type() == 15 { return block as? ICMConversationRatingBlock }
        }
        return nil
    }
    private func preserveLocalRatingState(from old: [ICMConversationPart], to new: [ICMConversationPart]) {
        for current in new {
            guard let previous = old.first(where: { $0.partId == current.partId }) else { continue }
            guard let newRating = ratingBlock(in: current) else { continue }
            guard let oldRating = ratingBlock(in: previous) else { continue }
            carryForwardRatingStateIfNeeded(from: oldRating, to: newRating)
        }
    }
    private func carryForwardRatingStateIfNeeded(from old: ICMConversationRatingBlock, to new: ICMConversationRatingBlock) {
        let copySelection = old.selectedRatingIndex != nil && new.selectedRatingIndex == nil
        let copyRemark = old.remark != nil && new.remark == nil
        if copySelection { new.selectedRatingIndex = old.selectedRatingIndex }
        if copyRemark { new.remark = old.remark }
    }
    func removeDuplicatesByPartId(from input: [ICMConversationPart]) -> [ICMConversationPart] {
        let sorted = input.sorted { $0.createdAt! >= $1.createdAt! }
        var seen: [String: Bool] = [:]
        var result: [ICMConversationPart] = []
        for part in sorted {
            let id = part.partId!
            if seen.updateValue(true, forKey: id) == nil { result.append(part) }
        }
        return Array(result.reversed())
    }
    func removeDuplicatesByClientUUID(from input: [ICMConversationPart]) -> [ICMConversationPart] {
        let sorted = input.sorted { $0.createdAt! >= $1.createdAt! }
        var indices: [String: Int] = [:]
        var result: [ICMConversationPart] = []
        for current in sorted {
            if let id = current.clientAssignedUUID, let index = indices[id] {
                let stored = result[index]
                result[index] = preserved(existingPart: current, newPart: stored)
            } else {
                if let id = current.clientAssignedUUID { indices[id] = result.count }
                result.append(current)
            }
        }
        return Array(result.reversed())
    }
    private func preserved(existingPart: ICMConversationPart, newPart: ICMConversationPart) -> ICMConversationPart {
        if existingPart.isVideoFile() || existingPart.isSingleImagePart() {
            if newPart.isServerPart() && existingPart.upload != nil { existingPart.state = 0 }
            return existingPart
        }
        if !existingPart.isServerPart() { return newPart }
        return newPart.isServerPart() ? newPart : existingPart
    }
    func handle(fetchedConversation input: ICMConversation) {
        let id = input.conversationId!
        var accumulated: [ICMConversationPart] = []
        if let old = dataManager.findExistingConversation(withId: id) {
            let oldParts = old.conversationParts! as! [ICMConversationPart]
            let newParts = input.conversationParts! as! [ICMConversationPart]
            preserveLocalRatingState(from: oldParts, to: newParts)
            accumulated = oldParts
        }
        accumulated.append(contentsOf: input.conversationParts! as! [ICMConversationPart])
        let unique = removeDuplicatesByPartId(from: accumulated)
        mergeUndeliveredParts(with: unique, in: input)
    }

    func mergeUndeliveredParts(with input: [ICMConversationPart], in conversation: ICMConversation) {
        var combined = undeliveredParts
        combined.append(contentsOf: input)
        combined = removeDuplicatesByClientUUID(from: combined)
        combined.sort { $0.createdAt! < $1.createdAt! }
        undeliveredParts = undeliveredParts.filter { local in
            !combined.contains { candidate in candidate.clientAssignedUUID == local.clientAssignedUUID }
        }
        let last = combined.removeLast()
        var filtered = combined.filter { !$0.isQuickReply() && !$0.isComposerSuggestions() }
        if !conversation.preventEndUserReplies || !last.isComposerSuggestions() { filtered.append(last) }
        conversation.conversationParts = filtered
        if let baseline = typingBaseline, baseline.isEnded(by: conversation) {
            typingBaseline = nil
            adminDidEndTyping?()
        }
        dataManager.update(with: conversation)
    }

    func conversationWithArticlePart(_ supplied: ICMConversation) -> ICMConversation? {
        guard let copy = existingConversation.copy() as? ICMConversation else { return nil }
        guard let metadata = copy.articleMetaData else { return nil }
        let helpCenterURL = config.helpCenterUrlString ?? ""
        let temporary = ICMConversationPart.temporaryArticlePart(with: metadata, helpCenterURLString: helpCenterURL)
        var parts = existingConversation.conversationParts! as! [ICMConversationPart]
        if let temporary { parts.append(temporary) }
        parts.sort { $0.createdAt! < $1.createdAt! }
        copy.conversationParts = parts
        return copy
    }
}

extension ConversationInteractor {
    func fetchConversation(reason: ConversationFetchReason, lastAdminPartId: String?,
                           lastAdminPartCreatedAt: Date?, success: (() -> Void)?,
                           failure: ((ConversationError) -> Void)?) {
        guard let id = conversationId else { return }
        requestConversation(id: id, reason: reason, lastAdminPartId: lastAdminPartId,
                            lastAdminPartCreatedAt: lastAdminPartCreatedAt,
                            success: success, failure: failure)
    }

    func requestConversation(id: String, reason: ConversationFetchReason,
                                     lastAdminPartId: String?, lastAdminPartCreatedAt: Date?,
                                     success: (() -> Void)?, failure: ((ConversationError) -> Void)?) {
        let metadata = IntercomNexusClient.shared()!.getConnectionStatusMetadata()
        apiBridge.conversationDetail(for: id, reason: reason, nexusMetadata: metadata,
            lastAdminPartId: lastAdminPartId, lastAdminPartCreatedAt: lastAdminPartCreatedAt) { [weak self] result in
            switch result {
            case .failure(let error):
                let mapped: ConversationError
                switch error.errorType {
                case .serverError, .userJwtExpired: mapped = .serverError(error.errorType)
                default: mapped = .somethingWentWrong
                }
                failure?(mapped)
                if let current = self {
                    current.typingBaseline = nil
                    current.adminDidEndTyping?()
                }
            case .success(let conversation):
                if let current = self {
                    current.conversationId = conversation.conversationId
                    current.dataManager.conversationToMark = current.conversationId
                }
                self?.handle(fetchedConversation: conversation)
                success?()
            }
        }
    }

    @objc func eventOccurred(_ event: IntercomNexusEvent) {
        let lastAdmin: ICMConversationPart?
        if let id = event.conversationId { lastAdmin = dataManager.findExistingConversation(withId: id)?.lastAdminPart() }
        else { lastAdmin = nil }
        let eventType = event.type
        withExtendedLifetime(lastAdmin) {
            switch eventType {
            case 2: nexusDidEverDisconnect = true
            case 1:
                guard nexusDidEverDisconnect else { return }
                let partId = lastAdmin?.partId
                let createdAt = lastAdmin?.createdAt
                guard let id = conversationId else { return }
                requestConversation(id: id, reason: .nexusReconnected, lastAdminPartId: partId,
                    lastAdminPartCreatedAt: createdAt, success: nil, failure: nil)
            case 6, 17:
                let type = eventType
                guard let currentId = conversationId, let eventId = event.conversationId, currentId == eventId else { return }
                let partId = lastAdmin?.partId
                let createdAt = lastAdmin?.createdAt
                if let id = conversationId {
                    requestConversation(id: id, reason: type == 6 ? .nexusNewComment : .userContentSeenByAdmin,
                        lastAdminPartId: partId, lastAdminPartCreatedAt: createdAt, success: nil, failure: nil)
                }
                if type == 6 { finDidEndStreaming?() }
            case 13:
                let eventId = event.conversationId
                let currentId = conversationId
                if eventId != currentId {
                    let pending = undeliveredParts
                    var admitted = false
                    for part in pending {
                        let uuid = part.clientAssignedUUID
                        if uuid == event.clientId { admitted = true; break }
                    }
                    if !admitted { return }
                }
                let current: ICMConversation?
                if let id = conversationId { current = dataManager.findExistingConversation(withId: id) }
                else { current = undeliveredConversation }
                typingBaseline = TypingBaseline(conversation: current)
                if !event.showAvatar { adminDidBeginTyping?(nil); return }
                if event.isBot { botDidBeginThinking?(); return }
                guard let name = event.adminName, let avatar = event.adminAvatar else {
                    adminDidBeginTyping?(nil)
                    return
                }
                let shape: AvatarShape
                if event.avatarShapeString == nil { shape = .circle }
                else { shape = event.avatarShapeString! == "squircle" ? .squircle : .circle }
                adminDidBeginTyping?(TypingAdminDetails(adminName: name, adminImageUrl: avatar, avatarShape: shape))
            case 7:
                if UIAccessibility.isReduceMotionEnabled { return }
                guard let raw = event.blocks else { return }
                let dictionaries = raw as! [[AnyHashable: Any]]
                if dictionaries.isEmpty { return }
                guard let currentId = conversationId, let eventId = event.conversationId, currentId == eventId else { return }
                let blocks = dictionaries.compactMap {
                    ICMBlockLoader.block(from: $0, appearance: AIAnswerPartBlockAppearance())
                }
                typingBaseline = nil
                adminDidEndTyping?()
                guard let callback = finDidStreamPart else { return }
                let index = event.tokenSequenceIndex!.intValue
                callback(blocks, index)
            default: break
            }
        }
    }
}

extension ConversationInteractor {
    func dismissPrivacyPolicy() {
        let client = apiBridge.httpClient
        let attributes = ["dismissed_privacy_policy_notice_at": Int(Date().timeIntervalSince1970)]
        // Preserve the original metatype read before constructing the request attributes.
        SwiftAPIBridge(httpClient: client, persistentStore: apiBridge.persistentStore)
            .updateUser(with: attributes, newSession: false, sentFromBackground: false, carouselVisible: false) { _ in
                // The original completion ignores the converted result.
            }
    }
}

extension ConversationInteractor {
    func submitFormParams(_ attribute: FormAttribute, conversationPartId: String, conversationId: String,
                          completionHandler: @escaping (Result<ICMConversation, APIError>) -> Void) {
        let parameters: [AnyHashable: Any] = ["value": attribute.value!,
            "conversation_part_id": conversationPartId,
            "identifier": attribute.identifier as Any,
            "type": attribute.type as Any]
        apiBridge.submitForm(parameters, to: conversationId) { [weak self] result in
            if case .success(let conversation) = result { self?.handle(fetchedConversation: conversation) }
            completionHandler(result)
        }
    }
}

extension ConversationInteractor {
    func buildImageBlock(dimensions: CGSize, publicURL: String) -> [String: Any] {
        let width = NSNumber(value: Double(dimensions.width))
        let height = NSNumber(value: Double(dimensions.height))
        return ["type": "image", "url": publicURL, "width": width, "height": height]
    }

    func attachmentListBlock(for metadata: UploadMetadata, publicURL: String) -> [String: Any] {
        let attachment: [String: Any] = ["id": metadata.uploadId, "contentType": metadata.mimeType,
            "url": publicURL, "size": metadata.fileSize, "name": metadata.filename]
        return ["type": "attachmentList", "attachments": [attachment]]
    }

    func attachmentListBlock(for upload: ITBUpload, publicURL: String) -> [String: Any] {
        let id = String._unconditionallyBridgeFromObjectiveC(upload.uploadId?.stringValue as NSString?)
        let contentType = String._unconditionallyBridgeFromObjectiveC(upload.mimeType as NSString?)
        let url = publicURL
        let size = Data._unconditionallyBridgeFromObjectiveC(upload.uploadData as NSData?).count
        let filename = String._unconditionallyBridgeFromObjectiveC(upload.filename as NSString?)
        let attachment: [String: Any] = ["id": id, "contentType": contentType, "url": url, "size": size, "name": filename]
        return ["type": "attachmentList", "attachments": [attachment]]
    }

    func buildBlockFor(_ upload: ITBUpload, with publicURL: String, thumbnailUrl: String?) -> [[AnyHashable: Any]] {
        if upload.isImage() { return [buildImageBlock(dimensions: upload.dimensions, publicURL: publicURL) as [AnyHashable: Any]] }
        return [attachmentListBlock(for: upload, publicURL: publicURL) as [AnyHashable: Any]]
    }

    func preUploadFile(url: URL, completion: @escaping (Result<(uploadURL: String, upload: ITBUpload), Error>) -> Void) {
        guard let upload = ITBUpload.createUpload(forFileURL: url) else {
            completion(.failure(NSError(domain: "ConversationInteractor", code: -1,
                userInfo: [NSLocalizedDescriptionKey: "Failed to create upload"])))
            return
        }
        if !upload.isVideo() {
            apiBridge.uploadFile(upload: upload) { [upload] result in
                switch result {
                case .success(let url): completion(.success((uploadURL: url, upload: upload)))
                case .failure(let error): completion(.failure(error))
                }
            }
            return
        }
        let downstream: (ITBUpload) -> Void = { [weak self] selected in
            let bridge: SwiftAPIBridge
            do { guard let current = self else { return }; bridge = current.apiBridge }
            bridge.uploadFile(upload: selected) { [selected] result in
                switch result {
                case .success(let url): completion(.success((uploadURL: url, upload: selected)))
                case .failure(let error): completion(.failure(error))
                }
            }
        }
        let processor = videoFileProcessor
        let fileURL = upload.fileURL!
        processor.compressVideo(at: fileURL) { [upload] result in
            switch result {
            case .success(let url): downstream(ITBUpload.createUpload(forFileURL: url) ?? upload)
            case .failure: downstream(upload)
            }
        }
    }
}

extension ConversationInteractor {
    private func setLocalPartForSending(with content: [[AnyHashable: Any]], upload: ITBUpload?,
        in conversation: ICMConversation, retryWith: String?) -> (localPart: ICMConversationPart, clientAssignedUUID: String)? {
        if let retryWith {
            let parts = conversation.conversationParts! as! [ICMConversationPart]
            guard let index = parts.firstIndex(where: { $0.clientAssignedUUID == retryWith && $0.state == 2 }) else { return nil }
            let fresh = conversation.conversationParts! as! [ICMConversationPart]
            guard let copied = fresh[index].copy() as? ICMConversationPart else { return nil }
            copied.state = 1
            var updated = conversation.conversationParts! as! [ICMConversationPart]
            updated[index] = copied
            conversation.conversationParts = updated
            dataManager.update(with: conversation)
            return (copied, retryWith)
        }
        guard let part = ICMConversationPart.sendingPart(withBlocks: content, upload: upload) else { return nil }
        var parts = conversation.conversationParts! as! [ICMConversationPart]
        parts.append(part)
        parts.sort { $0.createdAt! < $1.createdAt! }
        conversation.conversationParts = parts
        let uuid = part.partId!
        part.state = 1
        undeliveredParts.append(part)
        if conversation.conversationId != nil { dataManager.update(with: conversation) }
        return (part, uuid)
    }
    private func markPartAsFailed(_ part: ICMConversationPart, in conversation: ICMConversation) {
        guard let copy = part.copy() as? ICMConversationPart else { return }
        let parts = conversation.conversationParts! as! [ICMConversationPart]
        guard let index = parts.firstIndex(where: { $0.clientAssignedUUID == part.clientAssignedUUID }),
              let undelivered = undeliveredParts.firstIndex(where: { $0.clientAssignedUUID == part.clientAssignedUUID }) else { return }
        copy.state = 2
        var fresh = conversation.conversationParts! as! [ICMConversationPart]
        fresh[index] = copy
        conversation.conversationParts = fresh
        undeliveredParts[undelivered] = copy
    }
    private func handleSendingFailure(_ part: ICMConversationPart, conversation: ICMConversation,
        error: APIError, failure: ((ConversationError) -> Void)?) {
        let mapped: ConversationError = (error.additionalInfo?["errorCode"] as? String) == "inbound_conversation_already_open"
            ? .conversationAlreadyOpen : .messageSendingFailure
        if conversation.conversationId != nil {
            part.sendFailureAllowsRetry = mapped == .messageSendingFailure
            markPartAsFailed(part, in: conversation)
        } else {
            let retry: Bool
            if mapped == .conversationAlreadyOpen { newConversationStartRefused = true; retry = false }
            else { retry = !newConversationStartRefused }
            part.sendFailureAllowsRetry = retry
            undeliveredConversation = conversation
            if let current = undeliveredConversation {
                let parts = current.conversationParts! as! [ICMConversationPart]
                for part in parts { part.state = 2; if part.isPersonalPart() { part.sendFailureAllowsRetry = retry } }
            } else { newConversationStartRefused = false }
        }
        dataManager.update(with: conversation)
        failure?(mapped)
    }
}

extension ConversationInteractor {
    private func handle(fetchedConversationPart part: ICMConversationPart, for id: String, failure: ((ConversationError) -> Void)?) {
        guard let conversation = dataManager.findExistingConversation(withId: id) else { failure?(.conversationNotFound); return }
        var parts = conversation.conversationParts! as! [ICMConversationPart]
        parts.append(part)
        let unique = removeDuplicatesByPartId(from: parts)
        mergeUndeliveredParts(with: unique, in: conversation)
    }
    private func performUpload(_ upload: ITBUpload, localPart: ICMConversationPart, conversation: ICMConversation,
        success: @escaping ([[AnyHashable: Any]]) -> Void, failure: ((ConversationError) -> Void)?) {
        apiBridge.uploadFile(upload: upload) { [weak self] result in
            guard let self else { return }
            switch result {
            case .failure(let error): self.handleSendingFailure(localPart, conversation: conversation, error: error, failure: failure)
            case .success(let publicURL):
                let blocks = self.buildBlockFor(upload, with: publicURL, thumbnailUrl: nil)
                upload.uploadProgress = 1
                success(blocks)
            }
        }
    }
    func replyToConversation(with blocks: [[AnyHashable: Any]], upload: ITBUpload?, lastAdminPartCreatedAt: Int?,
        retryWith: String?, failure: ((ConversationError) -> Void)?) {
        let current: ICMConversation
        if let undelivered = undeliveredConversation {
            current = undelivered
            dataManager.clearUndeliveredConversations()
        } else {
            guard let id = conversationId, let stored = dataManager.findExistingConversation(withId: id) else {
                failure?(.conversationNotFound); return
            }
            current = stored
        }
        guard let local = setLocalPartForSending(with: blocks, upload: upload, in: current, retryWith: retryWith) else {
            failure?(.somethingWentWrong); return
        }
        let reply: ([[AnyHashable: Any]]) -> Void = { [weak self] content in
            guard let self else { return }
            let metadata = IntercomNexusClient.shared()!.getConnectionStatusMetadata() ?? [:]
            self.apiBridge.reply(to: current, clientAssignedUUID: local.clientAssignedUUID, with: content,
                nexusMetadata: metadata, lastAdminPartCreatedAt: lastAdminPartCreatedAt) { [weak self] result in
                    guard let self else { return }
                    switch result {
                    case .failure(let error): self.handleSendingFailure(local.localPart, conversation: current, error: error, failure: failure)
                    case .success(let part): self.handle(fetchedConversationPart: part, for: current.conversationId!, failure: failure)
                    }
                }
        }
        if let upload {
            let continuation: ([[AnyHashable: Any]]) -> Void = { [self] generated in
                withExtendedLifetime(self) { reply(blocks + generated) }
            }
            _ = upload.isVideo()
            performUpload(upload, localPart: local.localPart, conversation: current, success: continuation, failure: failure)
        } else { reply(blocks) }
    }
    private func sendRemainingQueuedParts(failure: ((ConversationError) -> Void)?) {
        let snapshot = partsToSendAfterCreation
        for part in snapshot {
            let blocks: [[AnyHashable: Any]]
            if part.upload != nil { blocks = [] }
            else { blocks = Array<[AnyHashable: Any]>._unconditionallyBridgeFromObjectiveC(part.blockDictionaries as NSArray?) }
            let upload = part.upload
            replyToConversation(with: blocks, upload: upload, lastAdminPartCreatedAt: nil, retryWith: nil, failure: failure)
        }
        partsToSendAfterCreation = []
    }
}

extension ConversationInteractor {
    func createConversation(with blocks: [[AnyHashable: Any]], upload: ITBUpload?,
        success: (ICMConversation) -> Void, failure: ((ConversationError) -> Void)?) {
        guard let local = ICMConversationPart.sendingPart(withBlocks: blocks, upload: upload) else { return }
        if !undeliveredParts.isEmpty { partsToSendAfterCreation.append(local); return }
        var conversation = dataManager.matchedNewConversation()!
        let original = conversation.conversationParts! as! [ICMConversationPart]
        if original.isEmpty, let enriched = conversationWithArticlePart(existingConversation) { conversation = enriched }
        undeliveredParts.append(local)
        var parts = conversation.conversationParts! as! [ICMConversationPart]
        parts.append(contentsOf: undeliveredParts)
        parts = parts.filter { !$0.isQuickReply() && !$0.isComposerSuggestions() }
        parts.sort { $0.createdAt! < $1.createdAt! }
        conversation.conversationParts = parts
        success(conversation)
        if let upload {
            let continuation: ([[AnyHashable: Any]]) -> Void = { [self] generated in
                self.startNewConversation(with: blocks + generated, localPart: local, conversation: conversation, failure: failure)
            }
            _ = upload.isVideo()
            performUpload(upload, localPart: local, conversation: conversation, success: continuation, failure: failure)
        } else { startNewConversation(with: blocks, localPart: local, conversation: conversation, failure: failure) }
    }
    func retryCreateConversation(success: (ICMConversation) -> Void, failure: ((ConversationError) -> Void)?) {
        guard let conversation = undeliveredConversation else { return }
        let initial = conversation.conversationParts! as! [ICMConversationPart]
        guard let first = initial.first(where: { $0.isPersonalPart() }) else { return }
        for part in conversation.conversationParts! as! [ICMConversationPart] { part.state = 1 }
        success(conversation)
        if (conversation.conversationParts! as! [ICMConversationPart]).count >= 2 {
            let fresh = conversation.conversationParts! as! [ICMConversationPart]
            let queued = fresh.filter { $0.isPersonalPart() && $0.partId != first.partId }
            partsToSendAfterCreation = queued
            undeliveredParts = queued
        }
        dataManager.update(with: conversation)
        let upload = first.upload
        let blocks = Array<[AnyHashable: Any]>._unconditionallyBridgeFromObjectiveC(first.blockDictionaries as NSArray?)
        if let upload {
            let continuation: ([[AnyHashable: Any]]) -> Void = { [self] generated in
                self.startNewConversation(with: blocks + generated, localPart: first, conversation: conversation, failure: failure)
            }
            _ = upload.isVideo()
            performUpload(upload, localPart: first, conversation: conversation, success: continuation, failure: failure)
        } else { startNewConversation(with: blocks, localPart: first, conversation: conversation, failure: failure) }
    }
}

extension ConversationInteractor {
    private func startNewConversation(with blocks: [[AnyHashable: Any]], localPart: ICMConversationPart,
        conversation: ICMConversation, failure: ((ConversationError) -> Void)?) {
        let bridge = apiBridge
        let uuid = localPart.clientAssignedUUID
        let metadata = articleMetadata
        let suggestions = conversation.composerSuggestions
        bridge.createConversation(with: blocks, clientAssignedUUID: uuid, articleMetaData: metadata,
            composerSuggestions: suggestions) { [weak self] result in
                switch result {
                case .failure(let error):
                    self?.handleSendingFailure(localPart, conversation: conversation, error: error, failure: failure)
                    self?.partsToSendAfterCreation = []
                case .success(let fetched):
                    if let self {
                        let id = fetched.conversationId
                        self.conversationId = id
                        self.dataManager.conversationToMark = id
                    }
                    if let self, let article = self.localArticleMetadataPart {
                        var parts = fetched.conversationParts! as! [ICMConversationPart]
                        parts.append(article)
                        fetched.conversationParts = self.removeDuplicatesByArticleId(from: parts)
                    }
                    if let manager = self?.dataManager { manager.clearUndeliveredConversations() }
                    if let manager = self?.dataManager { manager.update(with: fetched) }
                    self?.undeliveredParts = []
                    if let center = self?.notificationCenter {
                        center.post(name: .IntercomDidStartNewConversation, object: fetched, userInfo: nil)
                    }
                    if let self {
                        self.undeliveredConversation = nil
                        if self.undeliveredConversation == nil { self.newConversationStartRefused = false }
                    }
                    self?.sendRemainingQueuedParts(failure: failure)
                }
            }
    }
}

extension ConversationInteractor {
    func quickReply(with option: ReplyOption, partId: String?, overwriting: ICMConversationPart?, failure: ((ConversationError) -> Void)?) {
        guard let id = conversationId else { failure?(.missingConversationId); return }
        let manager = dataManager
        guard let conversation = manager.findExistingConversation(withId: id) else { failure?(.conversationNotFound); return }
        guard let action = ICMTextAction(text: option.replyText),
              let part = ICMConversationPart.sendingPart(withBlocks: action.blocks, upload: nil) else { return }
        part.selectedReplyOption = option
        part.quickReplyPartId = partId
        let existing = conversation.conversationParts! as! [ICMConversationPart]
        var parts = existing.filter { $0.partId != overwriting?.partId }
        parts.append(part)
        parts.sort { $0.createdAt! < $1.createdAt! }
        conversation.conversationParts = parts
        let uuid = part.partId
        if let index = undeliveredParts.firstIndex(where: { $0.clientAssignedUUID == overwriting?.clientAssignedUUID }) {
            undeliveredParts.remove(at: index)
        }
        undeliveredParts.append(part)
        manager.update(with: conversation)
        let bridge = apiBridge
        let optionId = option.uuid
        bridge.quickReply(to: id, clientAssignedUUID: uuid, partId: partId, optionsId: optionId) { [weak self] result in
            guard let self else { return }
            switch result {
            case .failure(let error): self.handleSendingFailure(part, conversation: conversation, error: error, failure: failure)
            case .success(let fetched): self.handle(fetchedConversationPart: fetched, for: id, failure: failure)
            }
        }
    }
    private func buildConversationForTriggerInboundBot(with option: ReplyOption, overwriting: ICMConversationPart) -> (localPart: ICMConversationPart, conversation: ICMConversation)? {
        guard let action = ICMTextAction(text: option.replyText),
              let part = ICMConversationPart.sendingPart(withBlocks: action.blocks, upload: nil) else { return nil }
        var conversation: ICMConversation
        if let undeliveredConversation { conversation = undeliveredConversation }
        else {
            conversation = dataManager.matchedNewConversation()!
            let parts = conversation.conversationParts! as! [ICMConversationPart]
            if parts.isEmpty, let enriched = conversationWithArticlePart(existingConversation) { conversation = enriched }
        }
        part.selectedReplyOption = option
        let existing = conversation.conversationParts! as! [ICMConversationPart]
        var parts = existing.filter { $0.partId != overwriting.partId }
        parts.append(part)
        parts.sort { $0.createdAt! < $1.createdAt! }
        conversation.conversationParts = parts
        return (part, conversation)
    }
    func triggerInboundBot(with option: ReplyOption, overwriting: ICMConversationPart,
        success: (ICMConversation) -> Void, failure: ((ConversationError) -> Void)?) {
        guard let pair = buildConversationForTriggerInboundBot(with: option, overwriting: overwriting) else { return }
        undeliveredParts = [pair.localPart]
        success(pair.conversation)
        apiBridge.triggerInboundBot(with: option.uuid, clientAssignedUUID: pair.localPart.clientAssignedUUID) { [weak self] result in
            if let manager = self?.dataManager { manager.clearUndeliveredConversations() }
            switch result {
            case .failure(let error):
                self?.handleSendingFailure(pair.localPart, conversation: pair.conversation, error: error, failure: failure)
            case .success(let fetched):
                if let self {
                    self.undeliveredConversation = nil
                    if self.undeliveredConversation == nil { self.newConversationStartRefused = false }
                }
                if let self {
                    let id = fetched.conversationId
                    self.conversationId = id
                    self.dataManager.conversationToMark = id
                }
                self?.handle(fetchedConversation: fetched)
                if let center = self?.notificationCenter {
                    center.post(name: .IntercomDidStartNewConversation, object: fetched, userInfo: nil)
                }
            }
        }
    }
}

extension ConversationInteractor {
    private func removeDuplicatesByArticleId(from input: [ICMConversationPart]) -> [ICMConversationPart] {
        let sorted = input.sorted { $0.createdAt! >= $1.createdAt! }
        var result: [ICMConversationPart] = []
        for candidate in sorted {
            if let index = result.firstIndex(where: { kept in
                guard let keptId = kept.articleId else { return false }
                guard let candidateId = candidate.articleId else { return false }
                return keptId == candidateId
            }) {
                result[index] = preserved(existingPart: candidate, newPart: result[index])
            } else { result.append(candidate) }
        }
        return Array(result.reversed())
    }
    func deletePart(containing upload: ITBUpload, completion: (ICMConversation) -> Void) {
        guard let found = undeliveredParts.first(where: { part in
            guard let current = part.upload else { return false }
            return current == upload
        }) else { return }
        found.state = 2
        delete(part: found, completion: completion)
    }
    func delete(part: ICMConversationPart, completion: (ICMConversation) -> Void) {
        if let id = conversationId {
            guard let conversation = dataManager.findExistingConversation(withId: id) else { return }
            let parts = conversation.conversationParts! as! [ICMConversationPart]
            guard let index = parts.firstIndex(where: { $0.clientAssignedUUID == part.clientAssignedUUID && $0.state == 2 }),
                  let undelivered = undeliveredParts.firstIndex(where: { $0.clientAssignedUUID == part.clientAssignedUUID }) else { return }
            undeliveredParts.remove(at: undelivered)
            var fresh = conversation.conversationParts! as! [ICMConversationPart]
            fresh.remove(at: index)
            conversation.conversationParts = fresh
            dataManager.update(with: conversation)
            return
        }
        if let conversation = undeliveredConversation {
            let parts = conversation.conversationParts! as! [ICMConversationPart]
            guard let index = parts.firstIndex(where: { $0.clientAssignedUUID == part.clientAssignedUUID }) else { return }
            var fresh = conversation.conversationParts! as! [ICMConversationPart]
            fresh.remove(at: index)
            conversation.conversationParts = fresh
            let remaining = conversation.conversationParts! as! [ICMConversationPart]
            if remaining.contains(where: { $0.isPersonalPart() }) { completion(conversation); return }
            undeliveredConversation = nil
            if undeliveredConversation == nil { newConversationStartRefused = false }
            undeliveredParts = []
            dataManager.clearUndeliveredConversations()
        } else {
            guard let index = undeliveredParts.firstIndex(where: { $0.clientAssignedUUID == part.clientAssignedUUID }) else { return }
            undeliveredParts.remove(at: index)
        }
        var matched = dataManager.matchedNewConversation()!
        let parts = matched.conversationParts! as! [ICMConversationPart]
        if parts.isEmpty, let enriched = conversationWithArticlePart(existingConversation) { matched = enriched }
        completion(matched)
    }
}

extension ConversationInteractor {
    func transcribeAudio(_ data: Data, success: ((String) -> Void)?, failure: ((ConversationError) -> Void)?) {
        apiBridge.transcribeAudio(data, conversationId: conversationId) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let text): success?(text)
                case .failure(let error): failure?(.serverError(error.errorType))
                }
            }
        }
    }
}

extension ConversationInteractor {
    func markConversationAsRead() {
        guard let id = conversationId else { return }
        dataManager.unreadManager.conversationIdRead(id)
        apiBridge.httpClient.markConversation(asRead: id, success: { _ in
            // The original callback is the genuine bare return at 0x9188.
        }, error: { _ in
            // The original callback is the genuine bare return at 0x9188.
        })
        markTicketAsRead()
        guard let freshId = conversationId,
              let event = NexusEventFactory.createNexusEvent(with: 16, conversationId: freshId) else { return }
        IntercomNexusClient.shared()!.send(event)
    }
    private func markTicketAsRead() {
        guard let ticket = existingConversation.ticket, !ticket.read else { return }
        ticket.read = true
        dataManager.unreadManager.ticketIdRead(ticket.id)
        let bridge = apiBridge
        let id = ticket.id
        bridge.markTicketAsRead(id) { [weak self, ticket] in
            let manager = self?.dataManager
            manager?.update(with: ticket)
        }
    }
}
