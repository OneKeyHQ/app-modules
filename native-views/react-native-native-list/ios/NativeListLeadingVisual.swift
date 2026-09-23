import UIKit

// Bounded visual primitive: three source slots, two optional overlays. It knows
// visual descriptors and request lifetimes, never row types or business keys.
final class NativeListLeadingVisual: UIView {
  var glyphSize: CGFloat = 18
  private let fallback = UILabel()
  private let icon = UIImageView()
  private let unread = UIView()
  private let networkBackdrop = UIView()
  private let corner = UIImageView()
  private let cornerBackdrop = UIView()
  private var slots: [NativeListImageSlot] = []
  private var overlaySlots: [NativeListImageSlot] = []
  private var overlays: [(UIView, [String: Any])] = []
  private let dashedBorder = CAShapeLayer()
  private var visual: [String: Any] = [:]
  private var imageStyle: [String: Any] = [:]
  private var sources: [([String: Any], String)] = []

  override init(frame: CGRect) {
    super.init(frame: frame)
    [fallback, icon, networkBackdrop, cornerBackdrop, corner, unread].forEach(addSubview)
    fallback.textAlignment = .center
    fallback.font = nativeListFont(ofSize: 13, weight: .bold)
    icon.contentMode = .scaleAspectFit
    corner.contentMode = .scaleAspectFit
    unread.backgroundColor = UIColor(nativeListHex: "#E5484D", fallback: .red)
    unread.layer.cornerRadius = 4
    networkBackdrop.layer.cornerRadius = 10
    cornerBackdrop.layer.cornerRadius = 10
    layer.addSublayer(dashedBorder)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  func bind(
    _ visual: [String: Any], style: [String: Any], key: String, theme: [String: Any]?,
    isUnread: Bool
  ) {
    self.visual = visual
    imageStyle = style
    let kind = visual.string("kind")
    let variant =
      kind == "token"
      ? "token"
      : kind == "network" ? "network" : ["account", "wallet"].contains(kind) ? "avatar" : "generic"
    sources =
      kind == "stackedImages"
      ? visual.dictionaries("images").prefix(3).map { ($0, "generic") }
      : kind == "icon" ? [] : visual.dictionary("image").map { [($0, variant)] } ?? []
    if kind == "token", !sources.isEmpty, let network = visual.dictionary("networkImage") {
      sources.append((network, "network"))
    }
    while slots.count < sources.count {
      let slot = NativeListImageSlot()
      slots.append(slot)
      insertSubview(slot.view, belowSubview: slots.count == 1 ? networkBackdrop : cornerBackdrop)
    }
    let placeholder = theme?.string("strongBackground", default: "#0000000F") ?? "#0000000F"
    let fallbackIcon = visual.dictionary("fallbackIcon")
    let sourceFallback =
      kind != "icon" && !sources.isEmpty
      && sources[0].0.string("loadingStrategy", default: "none") != "none"
      && (visual["fallbackText"] != nil || fallbackIcon != nil)
    let fallbackKey = sourceFallback ? nativeListSourceFallbackStateKey(sources[0].0) : nil
    let restored = fallbackKey.map(NativeListSourceFallbackState.has) ?? false
    let fallbackData = kind == "icon" ? visual : fallbackIcon
    fallback.text = String(visual.string("fallbackText").prefix(2))
    fallback.textColor = UIColor(nativeListHex: "#8D8D8D", fallback: .gray)
    icon.image = fallbackData.map { nativeListIcon(named: $0.string("name")) } ?? nil
    icon.tintColor = UIColor(
      nativeListHex: fallbackData?.string("tintColor", default: "#646464") ?? "#646464",
      fallback: .darkGray)
    fallback.isHidden = kind == "icon" || fallbackIcon != nil || !sources.isEmpty && !restored
    icon.isHidden = !(kind == "icon" || (sources.isEmpty || restored) && fallbackIcon != nil)
    let visualBackground = UIColor(
      nativeListHex: visual.string(
        "backgroundColor", default: sources.isEmpty ? placeholder : "#00000000"), fallback: .clear)
    backgroundColor =
      sourceFallback ? UIColor(nativeListHex: placeholder, fallback: .clear) : visualBackground
    layer.borderWidth = kind == "icon" ? 1 / UIScreen.main.scale : 0
    layer.borderColor = UIColor(nativeListHex: "#0000001F", fallback: .clear).cgColor
    for (index, slot) in slots.enumerated() {
      guard index < sources.count else {
        slot.recycle()
        slot.view.isHidden = true
        continue
      }
      let source = sources[index]
      slot.view.isHidden = index > 0 || sourceFallback
      slot.bind(
        source.0, key: "\(key):leading:\(index)", variant: source.1,
        fit: index == 0 ? style["contentFit"] as? String : nil, placeholder: placeholder
      ) { [weak self, weak slot] loaded in
        slot?.view.isHidden = !loaded && (index > 0 || sourceFallback)
        guard let self, index == 0, sourceFallback else { return }
        if let fallbackKey {
          if loaded {
            NativeListSourceFallbackState.forget(fallbackKey)
          } else {
            NativeListSourceFallbackState.remember(fallbackKey)
          }
        }
        self.backgroundColor =
          loaded ? visualBackground : UIColor(nativeListHex: placeholder, fallback: .clear)
        self.fallback.isHidden = loaded || fallbackIcon != nil
        self.icon.isHidden = loaded || fallbackIcon == nil
      }
    }
    networkBackdrop.isHidden = !(kind == "token" && sources.count > 1)
    networkBackdrop.backgroundColor = nativeListColor(theme, "rowBackground", "#FFFFFF")
    let badge = visual.dictionary("cornerIcon")
    corner.isHidden = badge == nil
    cornerBackdrop.isHidden = badge == nil
    corner.image = badge.flatMap { nativeListIcon(named: $0.string("name")) }
    corner.tintColor = UIColor(
      nativeListHex: badge?.string("tintColor", default: "#646464") ?? "#646464",
      fallback: .darkGray)
    cornerBackdrop.backgroundColor = UIColor(
      nativeListHex: badge?.string("backgroundColor", default: "#FFFFFF") ?? "#FFFFFF",
      fallback: .white)
    unread.isHidden = !isUnread
    let previousOverlays = overlays
    overlays.removeAll(keepingCapacity: true)
    let descriptors = Array(visual.dictionaries("overlays").prefix(2))
    while overlaySlots.count < descriptors.count { overlaySlots.append(NativeListImageSlot()) }
    for (index, data) in descriptors.enumerated() {
      let frame = index < previousOverlays.count ? previousOverlays[index].0 : UIView()
      frame.clipsToBounds = true
      frame.backgroundColor = UIColor(
        nativeListHex: data.string(
          "backgroundColor",
          default: theme?.string("rowBackground", default: "#00000000") ?? "#00000000"),
        fallback: .clear)
      let child: UIView
      if let image = data.dictionary("image") {
        let slot = overlaySlots[index]
        child = slot.view
        child.isHidden = true
        slot.bind(image, key: "\(key):overlay:\(index)", placeholder: placeholder) {
          [weak child] loaded in child?.isHidden = !loaded
        }
      } else if !data.string("text").isEmpty {
        overlaySlots[index].recycle()
        let label = frame.subviews.first as? UILabel ?? UILabel()
        label.text = data.string("text")
        label.textAlignment = .center
        label.font = nativeListFont(ofSize: 10, weight: .medium)
        label.textColor = UIColor(
          nativeListHex: data.string("tintColor", default: "#646464"), fallback: .darkGray)
        child = label
      } else {
        overlaySlots[index].recycle()
        let glyph = frame.subviews.first as? UIImageView ?? UIImageView()
        glyph.image = nativeListIcon(named: data.string("name"))
        glyph.contentMode = .scaleAspectFit
        glyph.tintColor = UIColor(
          nativeListHex: data.string("tintColor", default: "#646464"), fallback: .darkGray)
        child = glyph
      }
      if child.superview !== frame {
        frame.subviews.forEach { $0.removeFromSuperview() }
        frame.addSubview(child)
      }
      if frame.superview == nil { addSubview(frame) }
      overlays.append((frame, data))
    }
    previousOverlays.dropFirst(descriptors.count).forEach { $0.0.removeFromSuperview() }
    for slot in overlaySlots.dropFirst(descriptors.count) { slot.recycle() }
    bringSubviewToFront(unread)
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    let kind = visual.string("kind")
    let rtl = effectiveUserInterfaceLayoutDirection == .rightToLeft
    func logical(_ rect: CGRect) -> CGRect {
      rtl
        ? CGRect(x: bounds.width - rect.maxX, y: rect.minY, width: rect.width, height: rect.height)
        : rect
    }
    let shape = imageStyle.string(
      "shape", default: visual.string("shape", default: kind == "image" ? "rounded" : "circle"))
    let radius =
      imageStyle["cornerRadius"] != nil
      ? CGFloat(imageStyle.double("cornerRadius"))
      : shape == "square"
        ? 0
        : shape == "rounded"
          ? (imageStyle["shape"] != nil ? 10 : min(10, bounds.height / 4))
          : min(bounds.width, bounds.height) / 2
    layer.cornerRadius = radius
    clipsToBounds = sources.count < 2 && overlays.isEmpty && visual.dictionary("cornerIcon") == nil
    let ellipse = imageStyle.string("shape") == "circle" && imageStyle["cornerRadius"] == nil
    if ellipse && clipsToBounds {
      let mask = layer.mask as? CAShapeLayer ?? CAShapeLayer()
      mask.path = UIBezierPath(ovalIn: bounds).cgPath
      layer.mask = mask
    } else {
      layer.mask = nil
    }
    fallback.frame = bounds
    let fallbackIcon = visual.dictionary("fallbackIcon")
    let iconSize: CGFloat =
      !sources.isEmpty && fallbackIcon != nil
      ? min(bounds.width, bounds.height)
        * (fallbackIcon?.string("name") == "GlobusOutline" ? 1.2 : 1) : glyphSize
    icon.frame = CGRect(
      x: (bounds.width - iconSize) / 2, y: (bounds.height - iconSize) / 2, width: iconSize,
      height: iconSize)
    let tokenPair = kind == "token" && sources.count > 1
    for (index, slot) in slots.prefix(sources.count).enumerated() {
      let frame: CGRect
      if sources.count == 1 || tokenPair && index == 0 {
        frame = bounds
      } else if tokenPair {
        frame = CGRect(x: bounds.width - 14, y: bounds.height - 14, width: 16, height: 16)
      } else {
        let side = bounds.height * 0.72
        frame = CGRect(
          x: CGFloat(index) * (bounds.width - side) / CGFloat(max(1, sources.count - 1)),
          y: (bounds.height - side) / 2, width: side, height: side)
      }
      slot.view.frame = logical(frame)
      slot.view.clipsToBounds = true
      slot.view.layer.cornerRadius =
        index == 0 && (sources.count == 1 || tokenPair) ? radius : frame.height / 2
    }
    for (index, slot) in slots.enumerated() {
      if ellipse && index == 0 {
        let mask = slot.view.layer.mask as? CAShapeLayer ?? CAShapeLayer()
        mask.path = UIBezierPath(ovalIn: slot.view.bounds).cgPath
        slot.view.layer.mask = mask
      } else {
        slot.view.layer.mask = nil
      }
    }
    networkBackdrop.frame = logical(
      CGRect(x: bounds.width - 16, y: bounds.height - 16, width: 20, height: 20))
    cornerBackdrop.frame = networkBackdrop.frame
    corner.frame = cornerBackdrop.frame.insetBy(dx: 1, dy: 1)
    unread.frame = logical(CGRect(x: bounds.width - 8, y: 0, width: 8, height: 8))
    for (frame, data) in overlays {
      let size = CGFloat(data.double("size", default: 20))
      let width = CGFloat(data.double("width", default: Double(size)))
      let height = CGFloat(data.double("height", default: Double(size)))
      let offset = data.double("offset", default: 2)
      let x = CGFloat(data.double("offsetX", default: offset))
      let y = CGFloat(data.double("offsetY", default: offset))
      frame.frame = logical(
        CGRect(
          x: data.string("position") == "topLeft" ? -x : bounds.width - width + x,
          y: data.string("position") == "topLeft" ? -y : bounds.height - height + y, width: width,
          height: height))
      frame.layer.cornerRadius = min(width, height) / 2
      let inset = CGFloat(data.double("padding", default: 0))
      frame.subviews.first?.frame = frame.bounds.insetBy(
        dx: inset,
        dy: inset)
    }
    dashedBorder.isHidden = visual.string("borderStyle") != "dashed"
    dashedBorder.strokeColor =
      UIColor(nativeListHex: visual.string("borderColor", default: "#8D8D8D"), fallback: .gray)
      .cgColor
    dashedBorder.fillColor = UIColor.clear.cgColor
    dashedBorder.lineWidth = 2
    dashedBorder.lineDashPattern = [4, 4]
    dashedBorder.path = UIBezierPath(ovalIn: bounds.insetBy(dx: 1, dy: 1)).cgPath
  }
  func recycle() {
    slots.forEach { $0.recycle() }
    overlaySlots.forEach { $0.recycle() }
  }
}
