// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import SwiftUI
import UIKit

struct WaveformView: View {
    let audioLevels: [Float]
    let isRecording: Bool
    let themeProvider: any ThemeProvider
    @Environment(\.sizeCategory) private var sizeCategory

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .center, spacing: UIFontMetrics.default.scaledValue(for: 2)) {
                    ForEach(0..<audioLevels.count, id: \.self) { index in
                        RoundedRectangle(cornerRadius: UIFontMetrics.default.scaledValue(for: 1), style: .continuous)
                            .fill(barColor(for: audioLevels[index]))
                            .frame(width: UIFontMetrics.default.scaledValue(for: 1), height: barHeight(for: audioLevels[index]), alignment: .center)
                            .id(index)
                    }
                }
            }
            .onChange(of: audioLevels.count) { _ in
                withAnimation(nil) {
                    if !audioLevels.isEmpty { proxy.scrollTo(audioLevels.count - 1, anchor: .trailing) }
                }
            }
        }
    }

    private func barColor(for level: Float) -> Color {
        if level < 0 { return Color(themeProvider.theme.iconMuted) }
        let recording = isRecording
        let theme = themeProvider.theme
        return Color(recording ? theme.primaryForeground : theme.iconMuted)
    }
    private func barHeight(for level: Float) -> CGFloat {
        let metrics = UIFontMetrics.default
        if level < 0 { return metrics.scaledValue(for: 1) }
        let minimum = metrics.scaledValue(for: 2)
        let converted = CGFloat(level)
        let candidate = UIFontMetrics.default.scaledValue(for: 48) * converted
        return candidate >= minimum ? candidate : minimum
    }
}
