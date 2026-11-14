import SwiftUI
import UIKit

struct MessageInputField: UIViewRepresentable {
    @Binding var text: String
    @Binding var isFocused: Bool
    let themeProvider: any ThemeProvider
    let controller: ComposerTextController

    func makeCoordinator() -> Coordinator { Coordinator(parent: self) }
    func makeUIView(context: Context) -> ComposerTextView { makeTextView(coordinator: context.coordinator) }

    func makeTextView(coordinator: Coordinator) -> ComposerTextView {
        let view = ComposerTextView()
        view.delegate = coordinator
        view.font = .preferredFont(forTextStyle: .body)
        view.adjustsFontForContentSizeCategory = true
        view.textColor = themeProvider.theme.textDefault
        view.isScrollEnabled = false
        view.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        view.accessibilityLabel = "Message input"
        view.accessibilityHint = "Type your message here"
        if #available(iOS 16, *) {
            view.backgroundColor = .clear
            view.textContainerInset = .zero
            view.textContainer.lineFragmentPadding = 0
        } else { view.backgroundColor = .clear }
        view.accessibilityValue = text.isEmpty ? "Empty" : text
        view.text = text
        view.recomputeContentBounds()
        let width = view.bounds.width > 0 ? view.bounds.width : .greatestFiniteMagnitude
        let size = view.sizeThatFits(CGSize(width: width, height: .greatestFiniteMagnitude))
        let scroll = size.height > view.maxContentHeight
        if view.isScrollEnabled != scroll { view.isScrollEnabled = scroll; view.invalidateIntrinsicContentSize() }
        controller.textView = view
        controller.coordinator = coordinator
        return view
    }

    func updateUIView(_ uiView: ComposerTextView, context: Context) {
        updateTextView(uiView, coordinator: context.coordinator)
    }

    func updateTextView(_ view: ComposerTextView, coordinator: Coordinator) {
        coordinator.parent = self
        if view.text != text && !coordinator.isEditing {
            view.text = text
            view.accessibilityValue = text.isEmpty ? "Empty" : text
            let width = view.bounds.width > 0 ? view.bounds.width : .greatestFiniteMagnitude
            let size = view.sizeThatFits(CGSize(width: width, height: .greatestFiniteMagnitude))
            let scroll = size.height > view.maxContentHeight
            if view.isScrollEnabled != scroll { view.isScrollEnabled = scroll; view.invalidateIntrinsicContentSize() }
        }
        if isFocused && !view.isFirstResponder { view.becomeFirstResponder() }
        else if !isFocused && view.isFirstResponder { view.resignFirstResponder() }
    }

    static func dismantleUIView(_ uiView: ComposerTextView, coordinator: Coordinator) {
        let controller = coordinator.parent.controller
        if let current = controller.textView, current === uiView {
            controller.textView = nil
            controller.coordinator = nil
        }
    }

    @available(iOS 16, *)
    func sizeThatFits(_ proposal: ProposedViewSize, uiView: ComposerTextView, context: Context) -> CGSize? {
        let width = proposal.width ?? uiView.bounds.width
        if !(width > 0) { return CGSize(width: width, height: uiView.minContentHeight) }
        let fit = uiView.sizeThatFits(CGSize(width: width, height: .greatestFiniteMagnitude)).height
        let lower = fit <= uiView.minContentHeight ? uiView.minContentHeight : fit
        let height = uiView.maxContentHeight < lower ? uiView.maxContentHeight : lower
        return CGSize(width: width, height: height)
    }

    class Coordinator: NSObject, UITextViewDelegate {
        var parent: MessageInputField
        var isEditing = false
        init(parent: MessageInputField) { self.parent = parent; super.init() }
        override init() { fatalError("init() has not been implemented") }

        func syncBindingAndAccessibility(_ textView: UITextView) {
            let value = textView.text ?? ""
            if parent.text != value { parent.text = value }
            textView.accessibilityValue = value.isEmpty ? "Empty" : value
            if let view = textView as? ComposerTextView {
                let width = view.bounds.width > 0 ? view.bounds.width : .greatestFiniteMagnitude
                let size = view.sizeThatFits(CGSize(width: width, height: .greatestFiniteMagnitude))
                let scroll = size.height > view.maxContentHeight
                if view.isScrollEnabled != scroll { view.isScrollEnabled = scroll; view.invalidateIntrinsicContentSize() }
            }
        }
        func textViewDidChange(_ textView: UITextView) { syncBindingAndAccessibility(textView) }
        func textViewDidBeginEditing(_ textView: UITextView) { isEditing = true; parent.isFocused = true }
        func textViewDidEndEditing(_ textView: UITextView) {
            syncBindingAndAccessibility(textView)
            isEditing = false
            parent.isFocused = false
        }
    }
}
