import UIKit
import OneKeyImage

// The renderer owns its text subtree. Image slots and row constraints remain
// borrowed from the legacy host until the shared image primitive is extracted.
enum NativeListMessageRenderer {
  final class Views {
    let root: UIStackView
    let column = UIStackView()
    let title = NativeListTextLabel()
    let body = NativeListTextLabel()
    let time = NativeListInsetLabel()
    let unread: UIView
    let thumbnail: OneKeyImageReusableView
    let top: NSLayoutConstraint
    let bottom: NSLayoutConstraint
    let leadingWidth: NSLayoutConstraint
    let leadingHeight: NSLayoutConstraint
    let thumbnailWidth: NSLayoutConstraint
    let thumbnailHeight: NSLayoutConstraint

    init(
      root: UIStackView, unread: UIView, thumbnail: OneKeyImageReusableView,
      top: NSLayoutConstraint, bottom: NSLayoutConstraint,
      leadingWidth: NSLayoutConstraint, leadingHeight: NSLayoutConstraint,
      thumbnailWidth: NSLayoutConstraint, thumbnailHeight: NSLayoutConstraint
    ) {
      self.root = root
      self.unread = unread
      self.thumbnail = thumbnail
      self.top = top
      self.bottom = bottom
      self.leadingWidth = leadingWidth
      self.leadingHeight = leadingHeight
      self.thumbnailWidth = thumbnailWidth
      self.thumbnailHeight = thumbnailHeight
      column.axis = .vertical
      column.alignment = .fill
      column.spacing = 2
      column.setContentHuggingPriority(UILayoutPriority(1), for: .horizontal)
      column.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
      // Keep the existing title line's layout priorities without Market badges.
      let titleLine = UIStackView(arrangedSubviews: [title])
      titleLine.axis = .horizontal
      titleLine.alignment = .center
      titleLine.setContentHuggingPriority(.defaultLow, for: .horizontal)
      titleLine.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
      title.setContentHuggingPriority(.defaultLow, for: .horizontal)
      title.setContentCompressionResistancePriority(.required, for: .horizontal)
      [titleLine, body, time].forEach(column.addArrangedSubview)
    }

    func resetText(theme: [String: Any]?) {
      for label in [title, body, time] {
        label.attributedText = nil
        label.text = nil
        label.isHidden = true
        label.textAlignment = .natural
        label.lineBreakMode = .byTruncatingTail
        label.rowVerticalAlignment = nil
        label.rowOffsetY = 0
      }
      title.font = nativeListFont(ofSize: 14, weight: .semibold)
      body.font = nativeListFont(ofSize: 14)
      time.font = nativeListFont(ofSize: 12)
      title.textColor = nativeListColor(theme, "primaryText", "#202020")
      body.textColor = nativeListColor(theme, "secondaryText", "#646464")
      time.textColor = nativeListColor(theme, "disabledText", "#8D8D8D")
      time.lineBreakMode = .byWordWrapping
      time.topInset = 2
    }
  }

  static func bind(
    _ views: Views, item: NativeListItem, theme: [String: Any]?,
    show: (UILabel, String, Int) -> Void,
    lineHeight: (UILabel, String, CGFloat) -> Void,
    leading: ([String: Any]) -> Void,
    image: ([String: Any], OneKeyImageReusableView) -> Void
  ) {
    views.resetText(theme: theme)
    views.root.alignment = .top
    views.top.constant = 16
    views.bottom.constant = -16
    views.leadingWidth.constant = 28
    views.leadingHeight.constant = 28
    if let visual = item.data.dictionary("leading") { leading(visual) }
    views.unread.isHidden = !item.data.bool("unread")
    views.root.addArrangedSubview(views.column)
    show(views.title, item.data.string("title"), 2)
    lineHeight(views.title, item.data.string("title"), 20)
    show(views.body, item.data.string("body"), min(3, max(1, item.data.int("bodyLines", default: 3))))
    lineHeight(views.body, item.data.string("body"), 20)
    show(views.time, item.data.string("time"), 1)
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
