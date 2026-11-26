import UIKit

/// Mirrors `src/toast/types.ts`. Everything a toast card paints arrives as
/// data; nothing here refers to a React view.
enum NativeOverlayToastColor {
  /// Parses the CSS colour forms the JS theme resolves to.
  static func parse(_ value: Any?) -> UIColor? {
    guard let raw = (value as? String)?.trimmingCharacters(in: .whitespaces).lowercased(),
          !raw.isEmpty else { return nil }
    if raw == "transparent" { return .clear }
    if raw.hasPrefix("#") { return hex(String(raw.dropFirst())) }
    guard let open = raw.firstIndex(of: "("), let close = raw.lastIndex(of: ")") else { return nil }
    let name = raw[..<open]
    let parts = raw[raw.index(after: open)..<close]
      .split(whereSeparator: { $0 == "," || $0 == " " || $0 == "/" })
      .map(String.init)
    func number(_ text: String, scale: CGFloat) -> CGFloat? {
      if text.hasSuffix("%") {
        return Double(text.dropLast()).map { CGFloat($0) / 100 }
      }
      return Double(text).map { CGFloat($0) / scale }
    }
    if name == "rgb" || name == "rgba", parts.count >= 3,
       let r = number(parts[0], scale: 255), let g = number(parts[1], scale: 255),
       let b = number(parts[2], scale: 255) {
      let a = parts.count > 3 ? (number(parts[3], scale: 1) ?? 1) : 1
      return UIColor(red: r, green: g, blue: b, alpha: a)
    }
    if name == "hsl" || name == "hsla", parts.count >= 3,
       let h = Double(parts[0].replacingOccurrences(of: "deg", with: "")),
       let s = number(parts[1], scale: 100), let l = number(parts[2], scale: 100) {
      let a = parts.count > 3 ? (number(parts[3], scale: 1) ?? 1) : 1
      return hsl(CGFloat(h), s, l, a)
    }
    return nil
  }

  private static func hex(_ text: String) -> UIColor? {
    var digits = text
    if digits.count == 3 || digits.count == 4 {
      digits = digits.map { "\($0)\($0)" }.joined()
    }
    guard digits.count == 6 || digits.count == 8, let value = UInt64(digits, radix: 16) else { return nil }
    let hasAlpha = digits.count == 8
    let r = CGFloat((value >> (hasAlpha ? 24 : 16)) & 0xff) / 255
    let g = CGFloat((value >> (hasAlpha ? 16 : 8)) & 0xff) / 255
    let b = CGFloat((value >> (hasAlpha ? 8 : 0)) & 0xff) / 255
    let a = hasAlpha ? CGFloat(value & 0xff) / 255 : 1
    return UIColor(red: r, green: g, blue: b, alpha: a)
  }

  private static func hsl(_ h: CGFloat, _ s: CGFloat, _ l: CGFloat, _ a: CGFloat) -> UIColor {
    let chroma = (1 - abs(2 * l - 1)) * s
    let sector = (h.truncatingRemainder(dividingBy: 360) + 360).truncatingRemainder(dividingBy: 360) / 60
    let x = chroma * (1 - abs(sector.truncatingRemainder(dividingBy: 2) - 1))
    let (r, g, b): (CGFloat, CGFloat, CGFloat)
    switch Int(sector) {
    case 0: (r, g, b) = (chroma, x, 0)
    case 1: (r, g, b) = (x, chroma, 0)
    case 2: (r, g, b) = (0, chroma, x)
    case 3: (r, g, b) = (0, x, chroma)
    case 4: (r, g, b) = (x, 0, chroma)
    default: (r, g, b) = (chroma, 0, x)
    }
    let m = l - chroma / 2
    return UIColor(red: r + m, green: g + m, blue: b + m, alpha: a)
  }

  /// Formats a colour into the strict `rgba(r,g,b,a)` string the named-icon
  /// registry accepts.
  static func rgba(_ color: UIColor) -> String {
    var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 1
    color.getRed(&r, green: &g, blue: &b, alpha: &a)
    let ri = Int((min(max(r, 0), 1) * 255).rounded())
    let gi = Int((min(max(g, 0), 1) * 255).rounded())
    let bi = Int((min(max(b, 0), 1) * 255).rounded())
    let clampedA = min(max(a, 0), 1)
    let alpha: String
    if clampedA >= 1 {
      alpha = "1"
    } else if clampedA <= 0 {
      alpha = "0"
    } else {
      alpha = String(format: "%.4f", locale: Locale(identifier: "en_US_POSIX"), Double(clampedA))
        .replacingOccurrences(of: #"0+$"#, with: "", options: .regularExpression)
        .replacingOccurrences(of: #"\.$"#, with: "", options: .regularExpression)
    }
    return "rgba(\(ri),\(gi),\(bi),\(alpha))"
  }
}

private func toastNumber(_ value: Any?) -> CGFloat? {
  (value as? NSNumber).map { CGFloat($0.doubleValue) }
}

struct NativeOverlayToastTextRun {
  var text: String
  var color: UIColor?
  var fontWeight: String?
  var fontSize: CGFloat?
  var underline: Bool
  var link: String?

  init?(json: Any?) {
    guard let dict = json as? [String: Any], let text = dict["text"] as? String else { return nil }
    self.text = text
    color = NativeOverlayToastColor.parse(dict["color"])
    fontWeight = dict["fontWeight"] as? String
    fontSize = toastNumber(dict["fontSize"])
    underline = (dict["underline"] as? NSNumber)?.boolValue ?? false
    link = dict["link"] as? String
  }
}

struct NativeOverlayToastText {
  var paragraphs: [[NativeOverlayToastTextRun]]
  var color: UIColor
  var fontSize: CGFloat
  var lineHeight: CGFloat
  var fontWeight: String
  var letterSpacing: CGFloat
  var fontFamily: String?
  /// Font name per weight, for typefaces that ship one face per weight.
  var fontFaces: [String: String]
  var tabularNumbers: Bool
  var numberOfLines: Int

  init?(json: Any?) {
    guard let dict = json as? [String: Any] else { return nil }
    paragraphs = ((dict["paragraphs"] as? [Any]) ?? []).map { paragraph in
      ((paragraph as? [Any]) ?? []).compactMap(NativeOverlayToastTextRun.init(json:))
    }
    color = NativeOverlayToastColor.parse(dict["color"]) ?? .label
    fontSize = toastNumber(dict["fontSize"]) ?? 14
    lineHeight = toastNumber(dict["lineHeight"]) ?? 20
    fontWeight = dict["fontWeight"] as? String ?? "400"
    letterSpacing = toastNumber(dict["letterSpacing"]) ?? 0
    fontFamily = dict["fontFamily"] as? String
    fontFaces = dict["fontFaces"] as? [String: String] ?? [:]
    tabularNumbers = (dict["tabularNumbers"] as? NSNumber)?.boolValue ?? false
    numberOfLines = (dict["numberOfLines"] as? NSNumber)?.intValue ?? 0
  }
}

enum NativeOverlayToastIcon {
  case named(name: String, colorString: String, color: UIColor, size: CGFloat)
  case spinner(color: UIColor, size: CGFloat)
  case image(uri: String, size: CGFloat, radius: CGFloat)

  init?(json: Any?) {
    guard let dict = json as? [String: Any], let kind = dict["kind"] as? String else { return nil }
    let size = toastNumber(dict["size"]) ?? 20
    switch kind {
    case "icon":
      guard let name = dict["name"] as? String else { return nil }
      let color = NativeOverlayToastColor.parse(dict["color"]) ?? .black
      self = .named(
        name: name, colorString: NativeOverlayToastColor.rgba(color),
        color: color, size: size
      )
    case "spinner":
      self = .spinner(color: NativeOverlayToastColor.parse(dict["color"]) ?? .gray, size: size)
    case "image":
      guard let uri = dict["uri"] as? String else { return nil }
      self = .image(uri: uri, size: size, radius: toastNumber(dict["borderRadius"]) ?? 0)
    default:
      return nil
    }
  }

  var size: CGFloat {
    switch self {
    case let .named(_, _, _, size), let .spinner(_, size), let .image(_, size, _): return size
    }
  }
}

struct NativeOverlayToastAction {
  var id: String
  var label: String
  var disabled: Bool
  var testID: String?
  var icon: NativeOverlayToastIcon?
  var iconGap: CGFloat
  var color: UIColor
  var backgroundColor: UIColor
  var pressedBackgroundColor: UIColor?
  var fontSize: CGFloat
  var lineHeight: CGFloat
  var fontWeight: String
  var fontFamily: String?
  var fontFaces: [String: String]
  var paddingHorizontal: CGFloat
  var paddingVertical: CGFloat
  var marginHorizontal: CGFloat
  var marginVertical: CGFloat
  var borderWidth: CGFloat
  var borderRadius: CGFloat
  var disabledOpacity: CGFloat

  init?(json: Any?) {
    guard let dict = json as? [String: Any], let id = dict["id"] as? String else { return nil }
    self.id = id
    label = dict["label"] as? String ?? ""
    disabled = (dict["disabled"] as? NSNumber)?.boolValue ?? false
    testID = dict["testID"] as? String
    icon = NativeOverlayToastIcon(json: dict["icon"])
    iconGap = toastNumber(dict["iconGap"]) ?? 0
    color = NativeOverlayToastColor.parse(dict["color"]) ?? .label
    backgroundColor = NativeOverlayToastColor.parse(dict["backgroundColor"]) ?? .clear
    pressedBackgroundColor = NativeOverlayToastColor.parse(dict["pressedBackgroundColor"])
    fontSize = toastNumber(dict["fontSize"]) ?? 14
    lineHeight = toastNumber(dict["lineHeight"]) ?? 20
    fontWeight = dict["fontWeight"] as? String ?? "500"
    fontFamily = dict["fontFamily"] as? String
    fontFaces = dict["fontFaces"] as? [String: String] ?? [:]
    paddingHorizontal = toastNumber(dict["paddingHorizontal"]) ?? 0
    paddingVertical = toastNumber(dict["paddingVertical"]) ?? 0
    marginHorizontal = toastNumber(dict["marginHorizontal"]) ?? 0
    marginVertical = toastNumber(dict["marginVertical"]) ?? 0
    borderWidth = toastNumber(dict["borderWidth"]) ?? 0
    borderRadius = toastNumber(dict["borderRadius"]) ?? 0
    disabledOpacity = toastNumber(dict["disabledOpacity"]) ?? 1
  }
}

struct NativeOverlayToastSpec {
  enum Layout: String {
    case message
    case notification
  }

  var id: String
  var layout: Layout
  /// Milliseconds; negative never times out.
  var duration: Double
  var testID: String?
  var icon: NativeOverlayToastIcon?
  var title: NativeOverlayToastText?
  var message: NativeOverlayToastText?
  var actions: [NativeOverlayToastAction]
  var actionsAlignLeft: Bool
  var badgeColor: UIColor
  var badgeSize: CGFloat
  var image: NativeOverlayToastIcon?
  var pressable: Bool

  init?(json: Any?) {
    guard let dict = json as? [String: Any], let id = dict["id"] as? String else { return nil }
    self.id = id
    layout = Layout(rawValue: dict["layout"] as? String ?? "") ?? .message
    duration = (dict["duration"] as? NSNumber)?.doubleValue ?? 5000
    testID = dict["testID"] as? String
    icon = NativeOverlayToastIcon(json: dict["icon"])
    title = NativeOverlayToastText(json: dict["title"])
    message = NativeOverlayToastText(json: dict["message"])
    actions = ((dict["actions"] as? [Any]) ?? []).compactMap(NativeOverlayToastAction.init(json:))
    actionsAlignLeft = (dict["actionsAlign"] as? String) == "left"
    badgeColor = NativeOverlayToastColor.parse(dict["badgeColor"]) ?? .clear
    badgeSize = toastNumber(dict["badgeSize"]) ?? 28
    if var imageDict = dict["image"] as? [String: Any] {
      imageDict["kind"] = "image"
      image = NativeOverlayToastIcon(json: imageDict)
    }
    pressable = (dict["pressable"] as? NSNumber)?.boolValue ?? false
  }
}

struct NativeOverlayToastItem {
  var spec: NativeOverlayToastSpec
  var visible: Bool
  var revision: Int
  var generation: Int

  static func parse(_ jsonString: String) -> [NativeOverlayToastItem] {
    guard let data = jsonString.data(using: .utf8),
          let list = try? JSONSerialization.jsonObject(with: data) as? [Any] else { return [] }
    return list.compactMap { entry in
      guard let dict = entry as? [String: Any],
            let spec = NativeOverlayToastSpec(json: dict["spec"]) else { return nil }
      return NativeOverlayToastItem(
        spec: spec,
        visible: (dict["visible"] as? NSNumber)?.boolValue ?? true,
        revision: (dict["revision"] as? NSNumber)?.intValue ?? 0,
        generation: (dict["generation"] as? NSNumber)?.intValue ?? 0
      )
    }
  }
}

struct NativeOverlayToasterConfig {
  var cardColor: UIColor = .systemBackground
  /// Width of the card's content box; nil derives it from the window.
  var contentWidth: CGFloat?
  var contentMaxHeight: CGFloat?
  var topObstruction: CGFloat = 0
  var topObstructionGap: CGFloat = 8
  var topObstructionRoom: CGFloat = 88
  var respectReducedMotion = true

  init() {}

  init(jsonString: String) {
    guard let data = jsonString.data(using: .utf8),
          let dict = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else { return }
    cardColor = NativeOverlayToastColor.parse(dict["cardColor"]) ?? .systemBackground
    contentWidth = toastNumber(dict["contentWidth"])
    contentMaxHeight = toastNumber(dict["contentMaxHeight"])
    topObstruction = toastNumber(dict["topObstruction"]) ?? 0
    topObstructionGap = toastNumber(dict["topObstructionGap"]) ?? 8
    topObstructionRoom = toastNumber(dict["topObstructionRoom"]) ?? 88
    respectReducedMotion = (dict["respectReducedMotion"] as? NSNumber)?.boolValue ?? true
  }
}

enum NativeOverlayToastFont {
  /// The face for a weight: a per-weight font name when the typeface ships
  /// one, the family at that weight otherwise, the system font as a last resort.
  static func make(
    family: String?, faces: [String: String], weight: String, size: CGFloat, tabular: Bool
  ) -> UIFont {
    var font: UIFont?
    if let name = faces[weight] {
      font = UIFont(name: name, size: size)
    }
    if font == nil, let family, !family.isEmpty {
      let descriptor = UIFontDescriptor(fontAttributes: [.family: family])
        .addingAttributes([.traits: [UIFontDescriptor.TraitKey.weight: uiWeight(weight)]])
      let candidate = UIFont(descriptor: descriptor, size: size)
      // An unknown family silently resolves to a serif fallback.
      if candidate.familyName.caseInsensitiveCompare(family) == .orderedSame {
        font = candidate
      } else {
        font = UIFont(name: family, size: size)
      }
    }
    let resolved = font ?? UIFont.systemFont(ofSize: size, weight: uiWeight(weight))
    guard tabular else { return resolved }
    let descriptor = resolved.fontDescriptor.addingAttributes([
      .featureSettings: [[
        UIFontDescriptor.FeatureKey.type: kNumberSpacingType,
        UIFontDescriptor.FeatureKey.selector: kMonospacedNumbersSelector,
      ]],
    ])
    return UIFont(descriptor: descriptor, size: size)
  }

  private static func uiWeight(_ weight: String) -> UIFont.Weight {
    switch weight {
    case "500": return .medium
    case "600": return .semibold
    case "700": return .bold
    default: return .regular
    }
  }
}
