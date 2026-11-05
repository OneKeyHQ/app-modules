// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import Foundation

@_objcRuntimeName(_TtC18IntercomSDKPrivate19VideoBlockViewCache)
class VideoBlockViewCache {
    private var views: [String: VideoBlockView] = [:]

    func cache(_ view: VideoBlockView?, id: String) { views[id] = view }
    func view(for id: String) -> VideoBlockView? { views[id] }
}
