import SwiftUI
import UIKit

struct VoiceTranscriptionUIViewRepresentable: UIViewRepresentable {
    let viewModel: AudioRecorderViewModel
    let onDismiss: (() -> Void)?
    let themeProvider: any ThemeProvider
    func makeUIView(context: Context) -> UIView {
        let host = UIHostingController(rootView: VStack(alignment: .center, spacing: nil) {
            Spacer(minLength: nil)
            ComposerTranscribingView(onDismiss: onDismiss, themeProvider: themeProvider, viewModel: viewModel)
        })
        let firstView = host.view!
        firstView.backgroundColor = .clear
        return host.view!
    }
    func updateUIView(_ uiView: UIView, context: Context) {
        // Original witness is a bare return; the hosted tree observes its model.
    }
}
