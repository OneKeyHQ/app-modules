// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import Combine

@_objcRuntimeName(_TtC18IntercomSDKPrivate22ComposerTextController)
class ComposerTextController: ObservableObject {
    weak var textView: ComposerTextView?
    weak var coordinator: MessageInputField.Coordinator?

    func clearText() {
        guard let current = textView else { return }
        current.unmarkText()
        current.text = ""
        coordinator?.syncBindingAndAccessibility(current)
    }
}
