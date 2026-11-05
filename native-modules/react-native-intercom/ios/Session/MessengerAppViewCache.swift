// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import Foundation

@_objcRuntimeName(_TtC18IntercomSDKPrivate21MessengerAppViewCache)
class MessengerAppViewCache {
    private var views: [String: MessengerAppBlockView] = [:]

    func cache(_ view: MessengerAppBlockView?, id: String) { views[id] = view }
    func view(for id: String) -> MessengerAppBlockView? { views[id] }
}
