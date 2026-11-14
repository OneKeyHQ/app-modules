import SwiftUI
import UIKit

struct SendButton: View {
    let isEnabled: Bool
    let themeProvider: any ThemeProvider
    let action: () -> Void
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        Button(action: action) { label }
            .buttonStyle(PlainButtonStyle())
            .disabled(!isEnabled)
            .accessibilityLabel(Text("Send message"))
            .accessibilityHint(isEnabled ? Text("Double tap to send your message") : Text("Type a message to enable sending"))
    }
    private var label: some View {
        let enabled = isEnabled
        return ZStack(alignment: .center) {
            Circle()
                .fill(Color(enabled ? themeProvider.theme.primaryForeground : themeProvider.theme.neutralBorder))
                .frame(width: UIFontMetrics.default.scaledValue(for: 36), height: UIFontMetrics.default.scaledValue(for: 36), alignment: .center)
            Image(systemName: "arrow.up")
                .font(.callout.weight(.bold))
                .foregroundColor(Color(enabled ? themeProvider.theme.iconInversed : themeProvider.theme.iconMuted))
        }
        .accessibilityHidden(true)
        .animation(nil, value: isEnabled)
    }
}
