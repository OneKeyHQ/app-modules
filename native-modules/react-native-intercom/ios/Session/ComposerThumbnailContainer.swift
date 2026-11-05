import SwiftUI
import UIKit

enum ThumbnailState { case error(allowsRetry: Bool), idle, loadingPreview, uploading }
enum ComposerConstants {
    enum Thumbnail { static let borderColor = UIColor(red: 0.035, green: 0.055, blue: 0.082, alpha: 0.1) }
}

extension View {
    func applyDrawingGroupIfNeeded() -> some View {
        if #available(iOS 26, *) { return AnyView(self) }
        return AnyView(drawingGroup(opaque: false, colorMode: .nonLinear))
    }
}

struct CircularSpinner: View {
    let themeProvider: any ThemeProvider
    let customColor: Color?
    @State private var rotation: Double = 0

    var body: some View {
        Circle().stroke(AngularGradient(gradient: Gradient(stops: [
            .init(color: (customColor ?? Color(themeProvider.theme.textAlwaysLight)).opacity(0), location: 0),
            .init(color: (customColor ?? Color(themeProvider.theme.textAlwaysLight)).opacity(0.3), location: 0.3),
            .init(color: (customColor ?? Color(themeProvider.theme.textAlwaysLight)).opacity(0.6), location: 0.6),
            .init(color: customColor ?? Color(themeProvider.theme.textAlwaysLight), location: 0.75),
            .init(color: customColor ?? Color(themeProvider.theme.textAlwaysLight), location: 1)
        ]), center: .center, startAngle: .radians(0), endAngle: .radians(2 * .pi)),
            style: StrokeStyle(lineWidth: 4, lineCap: .round))
            .rotationEffect(.degrees(rotation), anchor: .center)
            .onAppear { withAnimation(.linear(duration: 0.8).repeatForever(autoreverses: false)) { rotation = 360 } }
    }
}

struct ThumbnailCloseButton: View {
    let themeProvider: any ThemeProvider
    let onClose: () -> Void
    var body: some View {
        VStack(alignment: .center) {
            HStack(alignment: .center) {
                Spacer()
                Button(action: onClose) {
                    Image(systemName: "xmark.circle.fill").symbolRenderingMode(.palette)
                        .foregroundStyle(Color(themeProvider.theme.textInversed), Color(themeProvider.theme.textDefault))
                        .font(.system(size: UIFontMetrics.default.scaledValue(for: 24), weight: .regular, design: .default))
                }
                .buttonStyle(PlainButtonStyle())
                .frame(width: UIFontMetrics.default.scaledValue(for: 24), height: UIFontMetrics.default.scaledValue(for: 24), alignment: .center)
                .offset(x: 4, y: -4).accessibilityLabel(Text("Remove"))
            }
            Spacer()
        }
    }
}

struct ThumbnailErrorBadge: View {
    let themeProvider: any ThemeProvider
    var body: some View {
        VStack(alignment: .center) {
            Spacer()
            HStack(alignment: .center) {
                Spacer()
                ZStack(alignment: .center) {
                    Circle().fill(Color(themeProvider.theme.iconError))
                        .frame(width: UIFontMetrics.default.scaledValue(for: 24), height: UIFontMetrics.default.scaledValue(for: 24))
                    Image(systemName: "exclamationmark")
                        .font(.system(size: UIFontMetrics.default.scaledValue(for: 24) * 0.6, weight: .bold, design: .default))
                        .foregroundColor(Color(themeProvider.theme.fileIconColor))
                }
                .applyDrawingGroupIfNeeded()
                .frame(width: UIFontMetrics.default.scaledValue(for: 24), height: UIFontMetrics.default.scaledValue(for: 24))
                .offset(x: 4, y: 4).accessibilityHidden(true)
            }
        }
    }
}

struct ComposerThumbnailContainer<Content: View>: View {
    let themeProvider: any ThemeProvider
    let state: ThumbnailState
    let onClose: () -> Void
    let onRetry: (() -> Void)?
    let accessibilityLabel: String
    let content: () -> Content
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        ZStack(alignment: .center) {
            content()
            if case .loadingPreview = state {
                ZStack(alignment: .center) {
                    RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 16), style: .continuous).fill(Color.black.opacity(0.5))
                    CircularSpinner(themeProvider: themeProvider, customColor: Color(themeProvider.theme.alwaysWhite))
                        .frame(width: UIFontMetrics.default.scaledValue(for: 24), height: UIFontMetrics.default.scaledValue(for: 24))
                }.applyDrawingGroupIfNeeded().accessibilityLabel(Text("Loading thumbnail"))
            }
            if case .uploading = state {
                ZStack(alignment: .center) {
                    RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 16), style: .continuous).fill(Color.black.opacity(0.5019607843137255))
                    CircularSpinner(themeProvider: themeProvider, customColor: Color(themeProvider.theme.alwaysWhite))
                        .frame(width: UIFontMetrics.default.scaledValue(for: 24), height: UIFontMetrics.default.scaledValue(for: 24))
                }.applyDrawingGroupIfNeeded().accessibilityLabel(Text("Uploading"))
            }
            if case .error(let allowsRetry) = state {
                ZStack(alignment: .center) {
                    RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 16), style: .continuous).fill(Color.black.opacity(0.5))
                    if allowsRetry {
                        // The original direct SDK-class asset uses the approved own-bundle relocation.
                        Image("retry", bundle: ICMUtils.intercomBundle())
                            .resizable(capInsets: EdgeInsets(), resizingMode: .stretch)
                            .frame(width: UIFontMetrics.default.scaledValue(for: 24), height: UIFontMetrics.default.scaledValue(for: 24))
                            .foregroundColor(Color(themeProvider.theme.alwaysWhite))
                    }
                }.applyDrawingGroupIfNeeded().onTapGesture(count: 1) { onRetry?() }
                    .accessibilityLabel(Text(allowsRetry ? "Retry upload" : "Upload error"))
            }
            ThumbnailCloseButton(themeProvider: themeProvider, onClose: onClose)
            if case .error = state { ThumbnailErrorBadge(themeProvider: themeProvider) }
        }
        .frame(width: UIFontMetrics.default.scaledValue(for: 64), height: UIFontMetrics.default.scaledValue(for: 64), alignment: .center)
        .accessibilityLabel(Text(accessibilityLabel))
    }
}
