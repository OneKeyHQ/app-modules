import UIKit
import OneKeyImage

// Template-owned binding and semantic text targets. The existing cell owns
// allocation/reset until the row-host migration replaces the legacy view tree.
enum NativeListMessageRenderer {
  struct Views {
    let root: UIStackView
    let column: UIStackView
    let title: UILabel
    let body: UILabel
    let time: UILabel
    let unread: UIView
    let thumbnail: OneKeyImageReusableView
    let top: NSLayoutConstraint
    let bottom: NSLayoutConstraint
    let leadingWidth: NSLayoutConstraint
    let leadingHeight: NSLayoutConstraint
    let thumbnailWidth: NSLayoutConstraint
    let thumbnailHeight: NSLayoutConstraint
  }

  static func bind(
    _ views: Views, item: NativeListItem, theme: [String: Any]?,
    show: (UILabel, String, Int) -> Void,
    lineHeight: (UILabel, String, CGFloat) -> Void,
    timeInset: (CGFloat) -> Void,
    leading: ([String: Any]) -> Void,
    image: ([String: Any], OneKeyImageReusableView) -> Void
  ) {
    views.root.alignment = .top
    views.top.constant = 16
    views.bottom.constant = -16
    views.leadingWidth.constant = 28
    views.leadingHeight.constant = 28
    if let visual = item.data.dictionary("leading") { leading(visual) }
    views.unread.isHidden = !item.data.bool("unread")
    views.root.addArrangedSubview(views.column)
    show(views.title, item.data.string("title"), 2)
    views.title.font = nativeListFont(ofSize: 14, weight: .semibold)
    lineHeight(views.title, item.data.string("title"), 20)
    show(views.body, item.data.string("body"), min(3, max(1, item.data.int("bodyLines", default: 3))))
    views.body.font = nativeListFont(ofSize: 14)
    lineHeight(views.body, item.data.string("body"), 20)
    show(views.time, item.data.string("time"), 1)
    views.time.font = nativeListFont(ofSize: 12)
    views.time.textColor = nativeListColor(theme, "disabledText", "#8D8D8D")
    timeInset(2)
    lineHeight(views.time, item.data.string("time"), 16)
    if let source = item.data.dictionary("thumbnail") {
      views.root.addArrangedSubview(views.thumbnail)
      views.thumbnailWidth.constant = 64
      views.thumbnailHeight.constant = 64
      views.thumbnailWidth.isActive = true
      views.thumbnailHeight.isActive = true
      views.thumbnail.layer.borderWidth = 1 / UIScreen.main.scale
      views.thumbnail.layer.borderColor = UIColor(nativeListHex: "#0000000F", fallback: .lightGray).cgColor
      image(source, views.thumbnail)
    }
  }

  static func applyTextStyles(_ views: Views, style: [String: Any], apply: (UILabel, [String: Any]) -> Void) {
    if let text = style.dictionary("title") { apply(views.title, text) }
    if let text = style.dictionary("body") { apply(views.body, text) }
    if let text = style.dictionary("time") { apply(views.time, text) }
  }

  private static func textWeight(_ style: [String: Any]?, fallback: NativeListFontWeight) -> NativeListFontWeight {
    switch style?.string("fontWeight") {
    case "regular": return .regular
    case "medium": return .medium
    case "semibold": return .semibold
    case "bold": return .bold
    default: return fallback
    }
  }

  static func measure(_ item: NativeListItem, availableWidth: CGFloat, contentPaddingHorizontal: CGFloat) -> CGFloat {
    let style = item.data.dictionary("style")
    let horizontalInsets = contentPaddingHorizontal * 2 + CGFloat(style?.double("horizontalPadding", default: 20) ?? 20) * 2
    let leadingWidth: CGFloat = item.data.dictionary("leading") == nil ? 0 : CGFloat(style?.dictionary("image")?.double("width", default: 28) ?? 28) + CGFloat(style?.double("leadingGap", default: 12) ?? 12)
    let thumbnailWidth: CGFloat = item.data.dictionary("thumbnail") == nil ? 0 : 76
    let textWidth = max(1, availableWidth - horizontalInsets - leadingWidth - thumbnailWidth)
    func textHeight(_ key: String, lines: Int, weight: NativeListFontWeight) -> CGFloat {
      let textStyle = style?.dictionary(key)
      let lineHeight = CGFloat(textStyle?.double("lineHeight", default: 20) ?? 20)
      let size = CGFloat(textStyle?.double("fontSize", default: 14) ?? 14)
      let paragraph = NSMutableParagraphStyle()
      paragraph.minimumLineHeight = lineHeight
      paragraph.maximumLineHeight = lineHeight
      let bounds = (item.data.string(key) as NSString).boundingRect(with: CGSize(width: textWidth, height: .greatestFiniteMagnitude), options: [.usesLineFragmentOrigin, .usesFontLeading], attributes: [.font: nativeListFont(ofSize: size, weight: textWeight(textStyle, fallback: weight)), .paragraphStyle: paragraph], context: nil)
      let maximumLines = min(3, max(1, textStyle?.int("lines", default: lines) ?? lines))
      return CGFloat(min(maximumLines, max(1, Int(ceil(bounds.height / lineHeight))))) * lineHeight
    }
    return CGFloat(style?.double("verticalPadding", default: 16) ?? 16) * 2
      + textHeight("title", lines: 2, weight: .semibold)
      + textHeight("body", lines: item.data.int("bodyLines", default: 3), weight: .regular)
      + textHeight("time", lines: 1, weight: .regular)
      + CGFloat(style?.double("lineGap", default: 1) ?? 1) * 2
  }

}
