import SwiftUI
import UIKit

struct ComposerImageThumbnail: View {
    let image: UIImage?
    let themeProvider: any ThemeProvider
    let state: ThumbnailState
    let onClose: () -> Void
    let onRetry: (() -> Void)?
    let onImageTap: (() -> Void)?
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        ComposerThumbnailContainer(themeProvider: themeProvider, state: state, onClose: onClose,
            onRetry: onRetry, accessibilityLabel: accessibilityLabel) { imageContent }
    }
    private var accessibilityLabel: String {
        if case .error = state { return "Image upload error - tap for details" }
        return "Image thumbnail - tap to preview"
    }
    @ViewBuilder private var imageContent: some View {
        if let onImageTap { thumbnail.onTapGesture(count: 1, perform: onImageTap) }
        else { thumbnail }
    }
    @ViewBuilder private var thumbnail: some View {
        if let image {
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
