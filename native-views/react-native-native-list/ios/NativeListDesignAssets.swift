import CoreText
import UIKit

private final class NativeListResourceToken {}

enum NativeListFontWeight {
  case regular
  case medium
  case semibold
  case bold

  fileprivate var postScriptName: String {
    switch self {
    case .regular: return "Roobert-Regular"
    case .medium: return "Roobert-Medium"
    case .semibold: return "Roobert-SemiBold"
    case .bold: return "Roobert-Bold"
    }
  }

  fileprivate var systemWeight: UIFont.Weight {
    switch self {
    case .regular: return .regular
    case .medium: return .medium
    case .semibold: return .semibold
    case .bold: return .bold
    }
  }
}

private enum NativeListResources {
  static let bundle: Bundle = {
    let owners = [Bundle(for: NativeListResourceToken.self), .main]
    for owner in owners {
      if let url = owner.url(forResource: "NativeListResources", withExtension: "bundle"),
         let bundle = Bundle(url: url) {
        return bundle
      }
    }
    return Bundle(for: NativeListResourceToken.self)
  }()

  static let registerFonts: Void = {
    ["Regular", "Medium", "SemiBold", "Bold"].forEach { weight in
      let directURL = bundle.url(forResource: "Roobert-\(weight)", withExtension: "ttf")
      let nestedURL = bundle.url(
        forResource: "Roobert-\(weight)",
        withExtension: "ttf",
        subdirectory: "fonts"
      )
      guard let url = directURL ?? nestedURL else { return }
      CTFontManagerRegisterFontsForURL(url as CFURL, .process, nil)
    }
  }()
}

func nativeListFont(ofSize size: CGFloat, weight: NativeListFontWeight = .regular) -> UIFont {
  _ = NativeListResources.registerFonts
  return UIFont(name: weight.postScriptName, size: size)
    ?? UIFont.systemFont(ofSize: size, weight: weight.systemWeight)
}

func nativeListTabularFont(
  ofSize size: CGFloat,
  weight: NativeListFontWeight = .regular
) -> UIFont {
  let font = nativeListFont(ofSize: size, weight: weight)
  let settings: [[UIFontDescriptor.FeatureKey: Int]] = [[
    .type: kNumberSpacingType,
    .selector: kMonospacedNumbersSelector,
  ]]
  let descriptor = font.fontDescriptor.addingAttributes([
    .featureSettings: settings,
  ])
  return UIFont(descriptor: descriptor, size: size)
}

func nativeListIcon(named name: String) -> UIImage? {
  // OneKey patch: the connection indicator is a solid circle at its requested size.
  if name == "Circle" {
    return UIGraphicsImageRenderer(size: CGSize(width: 24, height: 24)).image { _ in
      UIColor.black.setFill()
      UIBezierPath(ovalIn: CGRect(x: 0, y: 0, width: 24, height: 24)).fill()
    }.withRenderingMode(.alwaysTemplate)
  }
  let assetName: String
  switch name {
  // OneKey patch: official selector icon geometry.
  case "AccountErrorCustom": assetName = "onekey_selector_account_error_custom"
  // OneKey patch: official selector icon geometry.
  case "CrossedSmallSolid": assetName = "onekey_selector_crossed_small_solid"
  // OneKey patch: official selector icon geometry.
  case "AllNetworksSolid": assetName = "onekey_selector_all_networks_solid"
  // OneKey patch: official selector icon geometry.
  case "BotIllus": assetName = "onekey_selector_bot_illus"
  // OneKey patch: official selector icon geometry.
  case "AppleBrand": assetName = "onekey_selector_apple_brand"
  // OneKey patch: official selector icon geometry.
  case "GoogleIllus": assetName = "onekey_selector_google_illus"
  // OneKey patch: official selector icon geometry.
  case "LockSolid": assetName = "onekey_selector_lock_solid"
  // OneKey patch: official selector icon geometry.
  case "GlobusOutline": assetName = "onekey_selector_globus_outline"
  case "ArrowBottomOutline": assetName = "onekey_arrow_bottom"
  case "ArrowTopOutline": assetName = "onekey_arrow_top"
  case "ChartTrendingUpOutline": assetName = "onekey_chart_trending_up"
  case "SwapHorOutline": assetName = "onekey_swap_hor"
  case "ShieldExclamationOutline": assetName = "onekey_shield_exclamation"
  case "InfoCircleOutline": assetName = "onekey_info_circle"
  case "SpeakerPromoteOutline": assetName = "onekey_speaker_promote"
  case "PlusCircleOutline": assetName = "onekey_plus_circle"
  case "PlusSmallOutline": assetName = "onekey_plus_small"
  case "DotHorOutline": assetName = "onekey_dot_hor"
  case "PencilOutline": assetName = "onekey_pencil"
  case "MinusCircleSolid": assetName = "onekey_minus_circle_solid"
  case "MinusCircleOutline": assetName = "onekey_minus_circle"
  case "ChevronRightSmallOutline": assetName = "onekey_chevron_right_small"
  case "DragOutline": assetName = "onekey_drag"
  case "StarOutline": assetName = "onekey_star"
  case "StarSolid": assetName = "onekey_star_solid"
  case "BadgeVerifiedSolid": assetName = "onekey_badge_verified_solid"
  case "ChevronGrabberVerOutline": assetName = "onekey_chevron_grabber_ver"
  case "ChevronBottomOutline": assetName = "onekey_chevron_bottom"
  case "ChevronTopOutline": assetName = "onekey_chevron_top"
  case "CheckboxCheckedCustom": assetName = "onekey_checkbox_checked"
  case "CheckboxIndeterminateCustom": assetName = "onekey_checkbox_indeterminate"
  case "ErrorSolid": assetName = "onekey_error_solid"
  case "QuestionmarkSolid": assetName = "onekey_questionmark_solid"
  case "ImageSquareWavesOutline": assetName = "onekey_image_square_waves"
  default: return nil
  }
  return UIImage(named: assetName, in: NativeListResources.bundle, compatibleWith: nil)?
    .withRenderingMode(["GoogleIllus", "BotIllus", "AccountErrorCustom"].contains(name) ? .alwaysOriginal : .alwaysTemplate)
}

func nativeListIcon(named name: String, size: CGSize) -> UIImage? {
  guard let image = nativeListIcon(named: name) else { return nil }
  return UIGraphicsImageRenderer(size: size).image { _ in
    image.draw(in: CGRect(origin: .zero, size: size))
  }.withRenderingMode(.alwaysTemplate)
}
