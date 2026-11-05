// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import SwiftUI
import UIKit

struct ComposerFileThumbnail: View {
    let fileName: String
    let themeProvider: any ThemeProvider
    let state: ThumbnailState
    let onClose: () -> Void
    let onRetry: (() -> Void)?
    let onFileTap: (() -> Void)?
    @ScaledMetric private var cornerRadius: CGFloat = 16
    @ScaledMetric private var spinnerSize: CGFloat = 24
    @ScaledMetric private var retryIconSize: CGFloat = 24
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        ZStack(alignment: .center) {
            fileContent
            if case .error = state { ThumbnailErrorBadge(themeProvider: themeProvider).allowsHitTesting(false) }
            ThumbnailCloseButton(themeProvider: themeProvider, onClose: onClose)
        }
        .frame(width: 200, alignment: .center).fixedSize(horizontal: true, vertical: true)
    }
    @ViewBuilder private var fileContent: some View {
        if case .error(true) = state, let onRetry { row.onTapGesture(count: 1, perform: onRetry) }
        else if let onFileTap { row.onTapGesture(count: 1, perform: onFileTap) }
        else { row }
    }
    private var row: some View {
        HStack(alignment: .center, spacing: 8) {
            iconStack
            VStack(alignment: .leading, spacing: 2) {
                Text(fileName).font(.footnote).fontWeight(.medium)
                    .foregroundColor(Color(themeProvider.theme.textDefault)).lineLimit(1).truncationMode(.tail)
                Text(fileExtension).font(.footnote).foregroundColor(Color(themeProvider.theme.textMuted)).lineLimit(1)
            }
            Spacer()
        }
        .padding(.horizontal, 12).padding(.vertical, 12)
        .frame(width: 200, alignment: .center)
        .background(Color(themeProvider.theme.neutralContainer), ignoresSafeAreaEdges: .all)
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
        .overlay(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
            .stroke(Color(ComposerConstants.Thumbnail.borderColor), lineWidth: 1), alignment: .center)
        .applyDrawingGroupIfNeeded().accessibilityLabel(Text(accessibilityLabel))
    }
    private var accessibilityLabel: String {
        switch state {
        case .error(true): return "Retry upload"
        case .error(false): return "File upload error - tap for details"
        default: return "File thumbnail - tap to view"
        }
    }
    private var showIcon: Bool {
        switch state {
        case .idle, .error(false): return true
        default: return false
        }
    }
    private var iconStack: some View {
        ZStack(alignment: .center) {
            FileTypeIconView(fileName: fileName, themeProvider: themeProvider, showIcon: showIcon)
            if case .loadingPreview = state {
                CircularSpinner(themeProvider: themeProvider, customColor: Color(themeProvider.theme.textInversed))
                    .frame(width: spinnerSize, height: spinnerSize)
            }
            if case .uploading = state {
                CircularSpinner(themeProvider: themeProvider, customColor: Color(themeProvider.theme.textInversed))
                    .frame(width: spinnerSize, height: spinnerSize)
            }
            if case .error(true) = state {
                Image("retry", bundle: ICMUtils.intercomBundle()).resizable(capInsets: EdgeInsets(), resizingMode: .stretch)
                    .frame(width: retryIconSize, height: retryIconSize).foregroundColor(Color(themeProvider.theme.textInversed))
            }
        }
    }
    private var fileExtension: String {
        switch FileType(fileName: fileName) {
        case .pdf: return "PDF"
        case .zip: return "ZIP"
        case .csv: return "CSV"
        case .text: return "TXT"
        case .other: return TranslationManager.messenger.translation(forKey: "file")
        }
    }
}
