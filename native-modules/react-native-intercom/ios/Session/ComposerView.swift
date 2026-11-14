import SwiftUI
import UIKit

struct ComposerCallbacks {
    let onSend: ((String) -> Void)?
    let onCameraSelected: (() -> Void)?
    let onPhotosSelected: (() -> Void)?
    let onFilesSelected: (() -> Void)?
    let onGIFSelected: (() -> Void)?
    let onMicSelected: (() -> Void)?
    let onTextChanged: ((String) -> Void)?
    let onMediaTap: ((URL) -> Void)?
    let onMediaErrorTap: ((UUID, ComposerMediaError) -> Void)?
    let onMediaRemove: ((UUID) -> Void)?
}

struct ComposerView: View {
    @ObservedObject var viewModel: ComposerViewModel
    let themeProvider: any ThemeProvider
    let config: any ICMConfigProtocol
    let inputs: [ComposerInput]
    let callbacks: ComposerCallbacks
    @FocusState private var isTextFieldFocused: Bool
    @State private var isComposerFocused = false
    @StateObject private var textController = ComposerTextController()

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            mediaThumbnailView
            MessageInputField(text: $viewModel.text, isFocused: $isComposerFocused,
                themeProvider: themeProvider, controller: textController)
                .overlay(alignment: .topLeading) { if viewModel.text.isEmpty { placeholder } }
                .focused($isTextFieldFocused)
                .padding(.horizontal, 20).padding(.top, mediaTopPadding)
                .onChange(of: viewModel.text) { callbacks.onTextChanged?($0) }
                .onChange(of: isComposerFocused) {
                    isTextFieldFocused = $0
                    callbacks.onTextChanged?(viewModel.text)
                }
                .onChange(of: isTextFieldFocused) { isComposerFocused = $0 }
                .padding(.bottom, 9)
            HStack(alignment: .center) {
                if hasAttachments {
                    AttachmentsView(inputs: inputs, config: config, themeProvider: themeProvider,
                        mediaItemCount: mediaItems.count,
                        textCharacterCount: viewModel.text.trimmingCharacters(in: .whitespacesAndNewlines).count,
                        onGIFSelected: callbacks.onGIFSelected, onCameraSelected: callbacks.onCameraSelected,
                        onPhotosSelected: callbacks.onPhotosSelected, onFilesSelected: callbacks.onFilesSelected,
                        onMicSelected: callbacks.onMicSelected)
                        .padding(.leading, 8).padding(.bottom, 8).disabled(!viewModel.isEnabled)
                }
                Spacer()
                SendButton(isEnabled: viewModel.canSend, themeProvider: themeProvider) {
                    callbacks.onSend?(viewModel.text)
                    textController.clearText()
                    viewModel.mediaState = .none
                }.padding(.trailing, 11).padding(.bottom, 11)
            }
        }
        .background(RoundedRectangle(cornerRadius: 20, style: .continuous).fill(Color(themeProvider.theme.base)))
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
        .overlay(RoundedRectangle(cornerRadius: 20, style: .continuous)
            .stroke(Color(isComposerFocused ? themeProvider.theme.primaryForeground : themeProvider.theme.neutralBorderEmphasis),
                    style: StrokeStyle(lineWidth: 1.5))
            .animation(.easeInOut(duration: 0.2), value: isComposerFocused), alignment: .center)
    }

    private var hasAttachments: Bool {
        if inputs.contains(.gif) { return true }
        if config.attachmentSettings.cameraEnabled { return true }
        if config.attachmentSettings.mediaEnabled { return true }
        if config.attachmentSettings.filesEnabled { return true }
        return config.attachmentSettings.voiceTranscriptionEnabled
    }
    private var mediaTopPadding: CGFloat {
        switch viewModel.mediaState { case .none: return 18; case .hasMedia: return 0 }
    }
    private var mediaItems: [MediaItem] {
        switch viewModel.mediaState { case .none: return []; case .hasMedia(let items): return items }
    }
    @ViewBuilder private var placeholder: some View {
        let text = Text(TranslationManager.messenger.translation(forKey: "ask_a_question_with_ellipsis"))
            .font(Font(UIFont.preferredFont(forTextStyle: .body)))
            .foregroundColor(Color(themeProvider.theme.textMuted)).allowsHitTesting(false).accessibilityHidden(true)
        if #available(iOS 16, *) { text }
        else { text.padding(.top, 8).padding(.leading, 4) }
    }
    @ViewBuilder private var mediaThumbnailView: some View {
        if !mediaItems.isEmpty {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .center, spacing: 8) {
                    ForEach(mediaItems) { item in
                        ComposerMediaThumbnailView(mediaType: item.type, mediaURL: item.url,
                            mediaError: { if case .failed(let error) = item.uploadState { return error }; return nil }(),
                            isUploadingMedia: item.uploadState == .uploading, themeProvider: themeProvider,
                            onRemove: {
                                callbacks.onMediaRemove?(item.id)
                                viewModel.removeMedia(id: item.id)
                            }, onPreviewTap: {
                                guard let url = item.url else { return }
                                callbacks.onMediaTap?(url)
                            }, onErrorTap: {
                                if case .failed(let error) = item.uploadState { callbacks.onMediaErrorTap?(item.id, error) }
                            }, onMediaFailure: { error in viewModel.markItemAsFailed(id: item.id, error: error) })
                    }.animation(.easeInOut(duration: 0.3), value: mediaItems.map { $0.id })
                }.padding(.leading, 12).padding(.trailing, 12).padding(.top, 8).padding(.bottom, 4)
            }.padding(.top, 4).padding(.bottom, 12)
        }
    }
}
