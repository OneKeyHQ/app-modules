import Foundation
import MetricKit

@objc(_TtC18IntercomSDKPrivate20DiagnosticSubscriber)
class DiagnosticSubscriber: NSObject, MXMetricManagerSubscriber {
    let store: DiagnosticStore
    let userDefaults: UserDefaults
    let configProvider: () -> DiagnosticConfiguration
    var diskWriteFailedDuringCycle = false
    private let filter: DiagnosticFilter
    private let payloadBuilder: DiagnosticPayloadBuilder
    private let processingQueue = DispatchQueue(label: "com.intercom.diagnostics", qos: .utility)
    private var isReceiving = false
    private var hasProcessedPastPayloads = false

    @objc static let shared: DiagnosticSubscriber = {
        let defaults = DiagnosticConfiguration.default
        let store = DiagnosticStore(directory: ICMUtils.intercomDocumentsDirectory()!,
                                    maxPayloads: defaults.maxStoredPayloads, maxAge: 604_800)
        let binaryIdentifier = BinaryIdentifier.current
        let device = ICMDeviceData()
        let context: [String: Any] = [
            "os_name": "iOS", "os_version": device.platformVersion ?? "",
            "device_model": device.platform ?? "", "app_build": device.version ?? "",
            "app_bundle_id": device.applicationId ?? ""
        ]
        return DiagnosticSubscriber(store: store, filter: DiagnosticFilter(binaryIdentifier: binaryIdentifier),
                                    payloadBuilder: DiagnosticPayloadBuilder(binaryIdentifier: binaryIdentifier,
                                                                             deviceContext: context),
                                    userDefaults: .standard)
    }()

    init(store: DiagnosticStore, filter: DiagnosticFilter, payloadBuilder: DiagnosticPayloadBuilder,
         userDefaults: UserDefaults,
         configProvider: @escaping () -> DiagnosticConfiguration = {
             DiagnosticConfiguration(serverConfig: ICMConfig.shared().crashReportingConfig as? [String: Any])
         }) {
        self.store = store
        self.userDefaults = userDefaults
        self.configProvider = configProvider
        self.filter = filter
        self.payloadBuilder = payloadBuilder
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    @objc func startReceiving() { startReceiving(completion: nil) }

    @objc(startReceivingWithCompletion:)
    func startReceiving(completion: (() -> Void)?) {
        if isReceiving {
            completion?()
            return
        }
        isReceiving = true
        MXMetricManager.shared.add(self)
        if hasProcessedPastPayloads {
            completion?()
            return
        }
        hasProcessedPastPayloads = true
        let payloads = MXMetricManager.shared.pastDiagnosticPayloads
        processingQueue.async { [weak self] in
            self?.processDiagnosticPayloads(payloads)
            completion?()
        }
    }

    @objc func stopReceiving() {
        guard isReceiving else { return }
        isReceiving = false
        MXMetricManager.shared.remove(self)
    }

    func didReceive(_ payloads: [MXDiagnosticPayload]) {
        processingQueue.async { [weak self] in self?.processDiagnosticPayloads(payloads) }
    }

    private func processDiagnosticPayloads(_ payloads: [MXDiagnosticPayload]) {
        dispatchPrecondition(condition: .notOnQueue(.main))
        processExtractedPayloads(payloads.map(extractDiagnosticPayload))
    }

    @objc func fetchStoredPayloadsSnapshot() -> NSDictionary {
        let payloads = store.fetchAll()
        return ["ids": payloads.map(\.id), "payloads": payloads.map(\.payload)] as NSDictionary
    }

    @objc(clearPayloadsWithIds:)
    func clearPayloads(withIds ids: [String]) { store.remove(ids: Set(ids)) }

    func processExtractedPayloads(_ payloads: [ExtractedPayload]) {
        let config = configProvider()
        guard config.isEnabled else { return }
        let watermarkKey = "com.intercom.diagnostics.lastProcessedTimestamp"
        let watermark = userDefaults.object(forKey: watermarkKey) as? Date ?? .distantPast
        let newPayloads = payloads.filter { $0.timeStampEnd > watermark }
        guard !newPayloads.isEmpty else { return }
        diskWriteFailedDuringCycle = false
        for payload in newPayloads {
            for diagnostic in payload.diagnostics {
                guard config.enabledTypes.contains(diagnostic.type),
                      let filtered = filter.filterCallStackJSON(diagnostic.callStackJSON),
                      Double.random(in: 0..<1) < config.samplingRate,
                      var result = payloadBuilder.buildPayload(
                        diagnosticType: diagnostic.type, filteredCallStackJSON: filtered.data,
                        metadata: diagnostic.metadata, matchedBinaryNames: filtered.matchedBinaryNames,
                        sdkAttribution: filtered.sdkAttribution, timestamp: payload.timeStampEnd) else { continue }
                if diagnostic.type == .crash, let session = SessionIdentifierManager.shared.lastLaunchSessionId {
                    result["session_id"] = session
                }
                guard let data = try? JSONSerialization.data(withJSONObject: result, options: []),
                      data.count <= config.maxPayloadSizeBytes else { continue }
                if !store.append(result) { diskWriteFailedDuringCycle = true }
            }
        }
        if !diskWriteFailedDuringCycle, let latest = newPayloads.map(\.timeStampEnd).max() {
            userDefaults.set(latest, forKey: watermarkKey)
        }
    }
}
