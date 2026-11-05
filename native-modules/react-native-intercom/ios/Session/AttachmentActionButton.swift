import SwiftUI
import UIKit

struct AttachmentActionButton: View {
    let image: UIImage
    let themeProvider: any ThemeProvider
    let action: () -> Void
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        Button(action: action) {
            Image(uiImage: image).renderingMode(.template)
                .resizable(capInsets: EdgeInsets(), resizingMode: .stretch).scaledToFit()
                .foregroundColor(Color(themeProvider.theme.iconMuted))
                .frame(width: UIFontMetrics.default.scaledValue(for: 22), height: UIFontMetrics.default.scaledValue(for: 22), alignment: .center)
                .frame(width: UIFontMetrics.default.scaledValue(for: 40), height: UIFontMetrics.default.scaledValue(for: 40), alignment: .center)
                .contentShape(Rectangle(), eoFill: false)
        }
    }
}

private struct AttachmentMenuButton: UIViewRepresentable {
    let image: UIImage
    let tintColor: UIColor
    let iconSize: CGFloat
    let menuBuilder: (DeferredActionMenuButton) -> UIMenu

    // Keep the original UIKit property call in this private deprecated-API context.
    @available(iOS, deprecated: 15.0)
    func makeUIView(context: Context) -> DeferredActionMenuButton {
        let button = DeferredActionMenuButton(type: .custom)
        button.showsMenuAsPrimaryAction = true
        button.adjustsImageWhenHighlighted = false
        button.imageView?.contentMode = .scaleAspectFit
        button.accessibilityLabel = TranslationManager.messenger.translation(forKey: "attachments")
        return button
    }

    func updateUIView(_ button: DeferredActionMenuButton, context: Context) {
        button.setImage(image.withTintColor(tintColor, renderingMode: .alwaysOriginal), for: .normal)
        button.menu = menuBuilder(button)
        guard let imageView = button.imageView else { return }
        let size = button.iconSizeConstraints.first?.constant ?? 0
        if size == iconSize { return }
        NSLayoutConstraint.deactivate(button.iconSizeConstraints)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        let constraints = [imageView.widthAnchor.constraint(equalToConstant: iconSize),
                           imageView.heightAnchor.constraint(equalToConstant: iconSize),
                           imageView.centerXAnchor.constraint(equalTo: button.centerXAnchor),
                           imageView.centerYAnchor.constraint(equalTo: button.centerYAnchor)]
        NSLayoutConstraint.activate(constraints)
        button.iconSizeConstraints = constraints
    }
}

struct AttachmentsView: View {
    let inputs: [ComposerInput]
    let config: any ICMConfigProtocol
    let themeProvider: any ThemeProvider
    let mediaItemCount: Int
    let textCharacterCount: Int
    let onGIFSelected: (() -> Void)?
    let onCameraSelected: (() -> Void)?
    let onPhotosSelected: (() -> Void)?
    let onFilesSelected: (() -> Void)?
    let onMicSelected: (() -> Void)?
    @State private var showingMaxFilesAlert = false
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        HStack(alignment: .center, spacing: 0) {
            if config.attachmentSettings.cameraEnabled || config.attachmentSettings.mediaEnabled || config.attachmentSettings.filesEnabled {
                if mediaItemCount <= 9 {
                    AttachmentMenuButton(image: ICMAssets.paperclip()!, tintColor: themeProvider.theme.iconMuted,
                        iconSize: UIFontMetrics.default.scaledValue(for: 22), menuBuilder: createMenu)
                        .frame(width: UIFontMetrics.default.scaledValue(for: 40), height: UIFontMetrics.default.scaledValue(for: 40), alignment: .center)
                } else {
                    AttachmentActionButton(image: ICMAssets.paperclip()!, themeProvider: themeProvider) { showingMaxFilesAlert = true }
                }
            }
            if mediaItemCount <= 0 && textCharacterCount <= 0 && inputs.contains(.gif) {
                AttachmentActionButton(image: ICMAssets.gif()!, themeProvider: themeProvider) { [onGIFSelected] in onGIFSelected?() }
            }
            if mediaItemCount <= 0 && textCharacterCount <= 0 && inputs.contains(.mic) {
                AttachmentActionButton(image: ICMAssets.mic()!, themeProvider: themeProvider) { [onMicSelected] in onMicSelected?() }
            }
        }
        .alert(TranslationManager.messenger.translation(forKey: "upload_max_files_allowed").replacingOccurrences(of: "{limit}", with: String(10)), isPresented: $showingMaxFilesAlert) {
            // The original alert's cancel action is empty.
            Button(TranslationManager.messenger.translation(forKey: "ok"), role: .cancel) {}
        }
    }

    private func createMenu(_ button: DeferredActionMenuButton) -> UIMenu {
        var actions: [UIAction] = []
        if config.attachmentSettings.cameraEnabled {
            actions.append(UIAction(title: TranslationManager.messenger.translation(forKey: "camera")) { [weak button, callback = onCameraSelected] _ in
                guard let button else { return }
                button.pendingAction = { callback?() }
            })
        }
        if config.attachmentSettings.mediaEnabled {
            actions.append(UIAction(title: TranslationManager.messenger.translation(forKey: "photo_and_video_library")) { [weak button, callback = onPhotosSelected] _ in
                guard let button else { return }
                button.pendingAction = { callback?() }
            })
        }
        if config.attachmentSettings.filesEnabled {
            actions.append(UIAction(title: TranslationManager.messenger.translation(forKey: "tickets_add_file")) { [weak button, callback = onFilesSelected] _ in
                guard let button else { return }
                button.pendingAction = { callback?() }
            })
        }
        return UIMenu(title: "", image: nil, identifier: nil, options: [], children: actions)
    }
}
