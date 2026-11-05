// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate26TicketCreateFormInteractor)
class TicketCreateFormInteractor: NSObject {
    let apiBridge: SwiftAPIBridge
    init(apiBridge: SwiftAPIBridge) { self.apiBridge = apiBridge; super.init() }
    override init() { fatalError("init() has not been implemented") }
    func getTicketForm(request: TicketFormRequest) async throws -> TicketFormResponse {
        try await apiBridge.getTicketForm(request: request)
    }
}
