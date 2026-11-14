import SwiftUI
import UIKit

struct ComposerTranscribingView: View {
    let onDismiss: (() -> Void)?
    let themeProvider: any ThemeProvider
    @ObservedObject var viewModel: AudioRecorderViewModel
    @Environment(\.sizeCategory) private var sizeCategory
    var body: some View {
        ZStack(alignment: .center) {
            HStack(alignment: .center, spacing: UIFontMetrics.default.scaledValue(for: 12)) {
                cancelButton
                centerContent
                rightButton
            }
            if viewModel.state == .starting {
                ProgressView().scaleEffect(UIFontMetrics.default.scaledValue(for: 0.8), anchor: .center)
            }
        }
        .padding(.horizontal, UIFontMetrics.default.scaledValue(for: 8))
        .padding(.vertical, UIFontMetrics.default.scaledValue(for: 8))
        .background(Color(themeProvider.theme.inputAlt), ignoresSafeAreaEdges: .all)
        .overlay(RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 1000), style: .continuous)
            .stroke(Color(themeProvider.theme.neutralBorderEmphasis), lineWidth: UIFontMetrics.default.scaledValue(for: 1)), alignment: .center)
        .clipShape(RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 1000), style: .continuous))
        .onAppear { Task { @MainActor in await viewModel.startRecording() } }
    }
    private var cancelButton: some View {
        Button {
            Task { @MainActor in await viewModel.clearRecording() }
            onDismiss?()
        } label: {
            Image(systemName: "xmark")
                .font(.system(size: UIFontMetrics.default.scaledValue(for: 16), weight: .medium, design: .default))
                .foregroundColor(Color(themeProvider.theme.iconDefault))
                .frame(width: UIFontMetrics.default.scaledValue(for: 32), height: UIFontMetrics.default.scaledValue(for: 32), alignment: .center)
                .background(Color(themeProvider.theme.neutralContainer))
                .clipShape(Circle())
        }
    }
    @ViewBuilder private var centerContent: some View {
        switch viewModel.state {
        case .starting, .recordingAudio:
            WaveformView(audioLevels: viewModel.audioLevels, isRecording: true, themeProvider: themeProvider)
                .frame(maxWidth: .infinity, alignment: .center)
        case .transcribingAudio:
            HStack(alignment: .center, spacing: 8) {
                ProgressView().scaleEffect(UIFontMetrics.default.scaledValue(for: 0.8), anchor: .center)
                Text(TranslationManager.messenger.translation(forKey: "transcribing"))
                    .font(.system(size: UIFontMetrics.default.scaledValue(for: 15), weight: .medium, design: .default))
                    .foregroundColor(Color(themeProvider.theme.textMuted))
            }.frame(maxWidth: .infinity, alignment: .center)
        case .error(let error):
            HStack(alignment: .center, spacing: 8) {
                Image(systemName: "exclamationmark.triangle.fill")
                    .font(.system(size: UIFontMetrics.default.scaledValue(for: 14), weight: .medium, design: .default))
                    .foregroundColor(Color(themeProvider.theme.iconError))
                Text(TranslationManager.messenger.translation(forKey: error == .fileTooLarge ? "file_too_large_to_transcribe" : "transcription_failed"))
                    .font(.system(size: UIFontMetrics.default.scaledValue(for: 15), weight: .medium, design: .default))
                    .foregroundColor(Color(themeProvider.theme.textError))
            }.frame(maxWidth: .infinity, alignment: .center)
        }
    }
    @ViewBuilder private var rightButton: some View {
        switch viewModel.state {
        case .starting, .recordingAudio:
            Button {
                Task { @MainActor in try? await viewModel.stopRecording() }
            } label: {
                Image(systemName: "stop.fill")
                    .font(.system(size: UIFontMetrics.default.scaledValue(for: 16), weight: .medium, design: .default))
                    .foregroundColor({
                        let theme = themeProvider.theme
                        return Color(ColorHelper.suitableDynamicForegroundColor(for: theme.primaryBackground, theme: theme))
                    }())
                    .frame(width: UIFontMetrics.default.scaledValue(for: 32), height: UIFontMetrics.default.scaledValue(for: 32), alignment: .center)
                    .background(Color(themeProvider.theme.primaryForeground))
                    .clipShape(Circle())
            }.disabled(!viewModel.hasRecordingPermission)
        case .error, .transcribingAudio:
            // Original disabled SendButton action is a one-instruction return.
            SendButton(isEnabled: false, themeProvider: themeProvider, action: {})
        }
    }
}
