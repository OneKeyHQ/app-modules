import UIKit

@objc(_TtC18IntercomSDKPrivate11ColorHelper)
class ColorHelper: NSObject {
    @objc static func isLightColor(_ color: UIColor) -> Bool {
        (UIColor.white.luminance + 0.05) / (color.luminance + 0.05) < 4.51
    }

    @objc(suitableForegroundColorFor:theme:)
    static func suitableForegroundColor(for color: UIColor, theme: Theme) -> UIColor {
        let candidates = [theme.textAlwaysLight, theme.textAlwaysDark]
        let contrasts = candidates.map { candidate -> (UIColor, CGFloat) in
            let background = color.contrastLuminance
            let foreground = candidate.contrastLuminance
            let largest = background <= foreground ? foreground : background
            let smallest = foreground < background ? foreground : background
            return (candidate, (largest + 0.05) / (smallest + 0.05))
        }
        guard var best = contrasts.first else { return theme.textDefault }
        for candidate in contrasts.dropFirst() where best.1 < candidate.1 { best = candidate }
        return best.0
    }

    @objc(suitableDynamicForegroundColorFor:theme:)
    static func suitableDynamicForegroundColor(for color: UIColor, theme: Theme) -> UIColor {
        let light = color.resolvedColor(with: UITraitCollection(userInterfaceStyle: .light))
        let dark = color.resolvedColor(with: UITraitCollection(userInterfaceStyle: .dark))
        let lightForeground = suitableForegroundColor(for: light, theme: theme)
        let darkForeground = suitableForegroundColor(for: dark, theme: theme)
        return UIColor { traits in traits.userInterfaceStyle == .dark ? darkForeground : lightForeground }
    }
}

extension UIColor {
    func adjustingBrightness(by factor: CGFloat) -> UIColor {
        var hue: CGFloat = 0, saturation: CGFloat = 0, brightness: CGFloat = 0, alpha: CGFloat = 0
        guard getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: &alpha) else { return self }
        let adjusted = brightness * factor
        let capped = adjusted > 1 ? 1 : adjusted
        let bounded: CGFloat = capped <= 0 ? 0 : capped
        return UIColor(hue: hue, saturation: saturation, brightness: bounded, alpha: alpha)
    }

    private static func linearChannel(_ channel: CGFloat) -> CGFloat {
        channel <= 0.03928 ? channel / 12.92 : pow((channel + 0.055) / 1.055, 2.4)
    }

    var luminance: CGFloat {
        var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0, alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        return (Self.linearChannel(red) * 0.2126 + Self.linearChannel(green) * 0.7152)
            + Self.linearChannel(blue) * 0.0722
    }

    fileprivate var contrastLuminance: CGFloat {
        var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0, alpha: CGFloat = 0
        guard getRed(&red, green: &green, blue: &blue, alpha: &alpha) else { return 0 }
        return (Self.linearChannel(red) * 0.2126 + Self.linearChannel(green) * 0.7152)
            + Self.linearChannel(blue) * 0.0722
    }

    private var rgbValues: (red: CGFloat, green: CGFloat, blue: CGFloat) {
        var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: nil)
        return (red * 255, green * 255, blue * 255)
    }

    @objc var isWhite: Bool {
        let rgb = rgbValues
        return rgb.red > 240 && rgb.green > 240 && rgb.blue > 240
    }

    @objc var isBlack: Bool {
        let rgb = rgbValues
        return rgb.red < 25 && rgb.green < 25 && rgb.blue < 25
    }
}
