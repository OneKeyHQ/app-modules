import CoreText
import UIKit

enum NativeListFontWeight {
  case regular
  case medium
  case semibold
  case bold

  var systemWeight: UIFont.Weight {
    switch self {
    case .regular: return .regular
    case .medium: return .medium
    case .semibold: return .semibold
    case .bold: return .bold
    }
  }
}

struct NativeListFontFamilies: Equatable {
  let names: [String: String]
  init(names: [String: String] = [:]) { self.names = names }

  init?(json: String) {
    guard let data = json.data(using: .utf8),
      let value = try? JSONSerialization.jsonObject(with: data) as? [String: Any]
    else { return nil }
    let weights: Set<String> = ["regular", "medium", "semibold", "bold"]
    var names: [String: String] = [:]
    for (key, value) in value {
      guard weights.contains(key), let name = value as? String, !name.isEmpty,
        name.utf16.count <= 128,
        !name.unicodeScalars.contains(where: { $0.value < 32 || $0.value == 127 })
      else { return nil }
      names[key] = name
    }
    self.names = names
  }

  func font(ofSize size: CGFloat, weight: NativeListFontWeight) -> UIFont {
    let key: String
    switch weight {
    case .regular: key = "regular"
    case .medium: key = "medium"
    case .semibold: key = "semibold"
    case .bold: key = "bold"
    }
    return names[key].flatMap { UIFont(name: $0, size: size) }
      ?? UIFont.systemFont(ofSize: size, weight: weight.systemWeight)
  }
}

func nativeListFont(
  ofSize size: CGFloat, weight: NativeListFontWeight = .regular,
  fonts: NativeListFontFamilies = NativeListFontFamilies()
) -> UIFont {
  fonts.font(ofSize: size, weight: weight)
}

func nativeListTabularFont(
  ofSize size: CGFloat, weight: NativeListFontWeight = .regular,
  fonts: NativeListFontFamilies = NativeListFontFamilies()
) -> UIFont {
  let font = fonts.font(ofSize: size, weight: weight)
  let settings: [[UIFontDescriptor.FeatureKey: Int]] = [[
      .type: kNumberSpacingType, .selector: kMonospacedNumbersSelector
  ]]
  return UIFont(
    descriptor: font.fontDescriptor.addingAttributes([.featureSettings: settings]), size: size)
}

func nativeListIconMetadata(named name: String) -> NativeListIconMetadata? {
  if name == "Circle" { return NativeListIconMetadata(name: "Circle", size: CGSize(width: 24, height: 24), original: false) }
  return NativeListGeneratedIcons.icons[name]
}
