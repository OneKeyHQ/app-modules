// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import SwiftUI
import UIKit

struct ComposerVideoThumbnail: View {
    let thumbnail: UIImage?
    let themeProvider: any ThemeProvider
    let state: ThumbnailState
    let onClose: () -> Void
    let onRetry: (() -> Void)?
    let onVideoTap: (() -> Void)?
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        ComposerThumbnailContainer(themeProvider: themeProvider, state: state, onClose: onClose,
            onRetry: onRetry, accessibilityLabel: accessibilityLabel) {
            ZStack(alignment: .center) {
                imageContent
                if case .idle = state {
                    Image(systemName: "play.fill")
                        .font(.system(size: UIFontMetrics.default.scaledValue(for: 24), weight: .semibold, design: .default))
                        .foregroundColor(Color(themeProvider.theme.alwaysWhite))
                        .shadow(color: Color(themeProvider.theme.textAlwaysDark).opacity(0.3), radius: 4, x: 0, y: 2)
                        .allowsHitTesting(false)
                }
            }
        }
    }
    private var accessibilityLabel: String {
        if case .error = state { return "Video upload error - tap for details" }
        return "Video thumbnail - tap to preview"
    }
    @ViewBuilder private var imageContent: some View {
        if let onVideoTap { thumbnailContent.onTapGesture(count: 1, perform: onVideoTap) }
        else { thumbnailContent }
    }
    @ViewBuilder private var thumbnailContent: some View {
        if let image = thumbnail {
            Image(uiImage: image).resizable(capInsets: EdgeInsets(), resizingMode: .stretch)
                .aspectRatio(nil, contentMode: .fill)
                .frame(width: UIFontMetrics.default.scaledValue(for: 64), height: UIFontMetrics.default.scaledValue(for: 64), alignment: .center)
                .clipped().clipShape(RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 16), style: .continuous))
                .overlay(RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 16), style: .continuous)
                    .stroke(Color(ComposerConstants.Thumbnail.borderColor), lineWidth: 1), alignment: .center)
        } else {
            Rectangle().fill(Color(themeProvider.theme.neutralContainer))
                .frame(width: UIFontMetrics.default.scaledValue(for: 64), height: UIFontMetrics.default.scaledValue(for: 64), alignment: .center)
                .clipShape(RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 16), style: .continuous))
                .overlay(RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 16), style: .continuous)
                    .stroke(Color(ComposerConstants.Thumbnail.borderColor), lineWidth: 1), alignment: .center)
        }
    }
}
