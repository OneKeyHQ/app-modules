import UIKit

// Plain Swift copy of `ImageCropperAppearance`.
struct ImageCropperAppearanceConfig {
  let colorScheme: ImageCropperColorScheme?
  let backgroundColor: String?
  let titleColor: String?
  let iconColor: String?
  let cancelButtonColor: String?
  let cancelButtonPressedColor: String?
  let cancelButtonTextColor: String?
  let confirmButtonColor: String?
  let confirmButtonPressedColor: String?
  let confirmButtonTextColor: String?
  let titleFontFamily: String?
  let buttonFontFamily: String?
  let scale: Double?

  init(_ appearance: ImageCropperAppearance?) {
    colorScheme = appearance?.colorScheme
    backgroundColor = appearance?.backgroundColor
    titleColor = appearance?.titleColor
    iconColor = appearance?.iconColor
    cancelButtonColor = appearance?.cancelButtonColor
    cancelButtonPressedColor = appearance?.cancelButtonPressedColor
    cancelButtonTextColor = appearance?.cancelButtonTextColor
    confirmButtonColor = appearance?.confirmButtonColor
    confirmButtonPressedColor = appearance?.confirmButtonPressedColor
    confirmButtonTextColor = appearance?.confirmButtonTextColor
    titleFontFamily = appearance?.titleFontFamily
    buttonFontFamily = appearance?.buttonFontFamily
    scale = appearance?.scale
  }
}

// Resolved colors, fonts and metrics of the cropper screen. The layout
// matches Android's ImageCropperActivity.
struct ImageCropperTheme {
  let isDark: Bool
  let backgroundColor: UIColor
  let titleColor: UIColor
  let iconColor: UIColor
  let cancelButtonColor: UIColor
  let cancelButtonPressedColor: UIColor
  let cancelButtonTextColor: UIColor
  let confirmButtonColor: UIColor
  let confirmButtonPressedColor: UIColor
  let confirmButtonTextColor: UIColor
  let titleFont: UIFont
  let buttonFont: UIFont
  let scale: CGFloat

  // OneKey's tokens: $bgApp, $text, $icon, $bgStrong, $bgStrongActive,
  // $bgPrimary, $bgPrimaryActive and $textInverse.
  private struct Palette {
    let background: String
    let title: String
    let icon: String
    let cancelButton: String
    let cancelButtonPressed: String
    let cancelButtonText: String
    let confirmButton: String
    let confirmButtonPressed: String
    let confirmButtonText: String
  }

  private static let lightPalette = Palette(
    background: "#FFFFFF",
    title: "#000000DF",
    icon: "#0000009B",
    cancelButton: "#0000000F",
    cancelButtonPressed: "#0000001F",
    cancelButtonText: "#000000DF",
    confirmButton: "#000000DF",
    confirmButtonPressed: "#0000009B",
    confirmButtonText: "#FFFFFFED"
  )

  private static let darkPalette = Palette(
    background: "#0F0F0F",
    title: "#FFFFFFED",
    icon: "#FFFFFFAF",
    cancelButton: "#FFFFFF12",
    cancelButtonPressed: "#FFFFFF22",
    cancelButtonText: "#FFFFFFED",
    confirmButton: "#FFFFFFED",
    confirmButtonPressed: "#FFFFFF72",
    confirmButtonText: "#000000DF"
  )

  init(_ appearance: ImageCropperAppearanceConfig, systemIsDark: Bool) {
    switch appearance.colorScheme {
    case .dark:
      isDark = true
    case .light:
      isDark = false
    case nil:
      isDark = systemIsDark
    }
    let palette = isDark ? Self.darkPalette : Self.lightPalette

    func color(_ value: String?, fallback: String) -> UIColor {
      return ImageCropPickerImageProcessor.color(fromHex: value)
        ?? ImageCropPickerImageProcessor.color(fromHex: fallback)
        ?? .clear
    }

    backgroundColor = color(appearance.backgroundColor, fallback: palette.background)
    titleColor = color(appearance.titleColor, fallback: palette.title)
    iconColor = color(appearance.iconColor, fallback: palette.icon)
    cancelButtonColor = color(appearance.cancelButtonColor, fallback: palette.cancelButton)
    cancelButtonPressedColor = color(
      appearance.cancelButtonPressedColor,
      fallback: palette.cancelButtonPressed
    )
    cancelButtonTextColor = color(
      appearance.cancelButtonTextColor,
      fallback: palette.cancelButtonText
    )
    confirmButtonColor = color(appearance.confirmButtonColor, fallback: palette.confirmButton)
    confirmButtonPressedColor = color(
      appearance.confirmButtonPressedColor,
      fallback: palette.confirmButtonPressed
    )
    confirmButtonTextColor = color(
      appearance.confirmButtonTextColor,
      fallback: palette.confirmButtonText
    )

    let resolvedScale = CGFloat(appearance.scale ?? 1)
    scale = resolvedScale > 0 ? resolvedScale : 1
    // OneKey's $headingLg and $bodyLgMedium, rounded like its scaled fonts.
    titleFont = Self.font(
      named: appearance.titleFontFamily,
      size: (18 * scale).rounded(),
      fallbackWeight: .semibold
    )
    buttonFont = Self.font(
      named: appearance.buttonFontFamily,
      size: (16 * scale).rounded(),
      fallbackWeight: .medium
    )
  }

  func metric(_ value: CGFloat) -> CGFloat {
    return (value * scale).rounded()
  }

  private static func font(named name: String?, size: CGFloat, fallbackWeight: UIFont.Weight) -> UIFont {
    if let name, let font = UIFont(name: name, size: size) {
      return font
    }
    return .systemFont(ofSize: size, weight: fallbackWeight)
  }
}
