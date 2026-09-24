import UIKit

// Shared chrome and binding lifetime; the subclass owns its template views.
enum NativeListRendererUpdate { case unchanged, content, assets, replace }

class NativeListRendererCell: NativeListRowHost {
  let root = UIStackView()
  var contentInsets = UIEdgeInsets.zero
  var usesIntrinsicContentHeight: Bool { false }
  var defaultBorderWidth: CGFloat { 0 }
  func defaultBorderColor(_ theme: [String: Any]?) -> UIColor { .clear }
  var defaultCornerRadius: CGFloat { 0 }
  /// Legacy pressed rows round all corners (12pt) while highlighted; nil opts out.
  var pressedCornerRadius: CGFloat? { pressChangesBackground ? 12 : nil }
  /// Radius for `groupPosition` rows when listStyle does not set one (legacy 12).
  var groupCornerRadius: CGFloat { 12 }
  var defaultCornerCurve: CALayerCornerCurve { .circular }
  var pressChangesBackground: Bool { true }
  func pressedBackground(_ theme: [String: Any]?) -> UIColor {
    nativeListColor(theme, "rowPressedBackground", "#E8E8E8")
  }
  var showsSelection: Bool { true }
  /// Whether the legacy row-level `backgroundColor` paints this row (resting and full-width
  /// fills). `style.container.backgroundColor` always applies.
  var honorsRowBackgroundColor: Bool { true }
  var defaultVerticalAlignment: String? { nil }
  var defaultSeparatorInset: CGFloat { 12 }
  func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {}
  func recycleContent() {}
  func bindSelectionContent(
    _ item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {}
  var assetFields: [String] { [] }
  private func update(from old: NativeListItem?, to new: NativeListItem) -> NativeListRendererUpdate
  {
    guard let old, old.key == new.key, old.rendererKey == new.rendererKey else { return .replace }
    if old.content == new.content { return .unchanged }
    for field in assetFields where !nativeListValuesEqual(old.data[field], new.data[field]) {
      return .assets
    }
    return .content
  }
  private let separator = UIView()
  private let fullWidthBackground = CALayer()
  private var contentPosition: NSLayoutConstraint?
  private var rootConstraints: [NSLayoutConstraint] = []
  private var item: NativeListItem?
  private var theme: [String: Any]?
  private var layout = "linear"
  private var itemIndex: Int?
  func unselectedBackground(
    _ item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int?
  ) -> UIColor { nativeListColor(theme, "rowBackground", "#FFFFFF") }
  private(set) var selectedState = false
  // Last applied list-level inputs; compared structurally (no serialization/hashing per bind).
  // The layout direction is an input because renderers resolve start/end alignment at bind.
  private var boundInputs:
    (
      theme: NSDictionary?, layout: String, listStyle: NSDictionary?,
      direction: UIUserInterfaceLayoutDirection
    )?

  override init(frame: CGRect) {
    super.init(frame: frame)
    root.translatesAutoresizingMaskIntoConstraints = false
    contentView.addSubview(root)
    contentView.addSubview(separator)
    rootConstraints = [
      root.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
      root.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
      root.topAnchor.constraint(equalTo: contentView.topAnchor),
      root.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
    ]
    NSLayoutConstraint.activate(rootConstraints)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  func accessibilityText(_ item: NativeListItem) -> String {
    item.data.string("accessibilityLabel", default: item.data.string("title"))
  }
  @discardableResult func retainBoundItem(_ item: NativeListItem) -> Bool {
    guard self.item?.key == item.key else { return false }
    self.item = item
    return true
  }

  override func bind(
    item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int? = nil,
    selected: Bool, checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    let update = update(from: self.item, to: item)
    let inputs = (
      theme: theme.map { $0 as NSDictionary }, layout: layout,
      listStyle: listStyle.map { $0 as NSDictionary },
      direction: effectiveUserInterfaceLayoutDirection
    )
    let inputsChanged =
      boundInputs.map {
        // A missing theme/listStyle is the empty object, as in the former `?? [:]` JSON digest.
        $0.layout != inputs.layout || $0.direction != inputs.direction
          || !nativeListValuesEqual($0.theme ?? [:], inputs.theme ?? [:])
          || !nativeListValuesEqual($0.listStyle ?? [:], inputs.listStyle ?? [:])
      } ?? true
    if update != .unchanged || inputsChanged {
      if self.item != nil { onBindingInvalidated?(self, bindingEpoch) }
      bindingEpoch &+= 1
      if update == .replace {
        recycleContent()
        isHighlighted = false
      }
      contentPosition?.isActive = false
      contentPosition = nil
      NSLayoutConstraint.activate(Array(rootConstraints.suffix(2)))
      bindContent(item, theme: theme, layout: layout, checkboxState: checkboxState)
      rootConstraints[0].constant = contentInsets.left
      rootConstraints[1].constant = -contentInsets.right
      rootConstraints[2].constant = contentInsets.top
      rootConstraints[3].constant = -contentInsets.bottom
      if root.axis == .vertical,
        let alignment = item.data.dictionary("style")?.dictionary("container")?[
          "contentVerticalAlignment"] as? String
          ?? (item.styledHeight != nil || usesIntrinsicContentHeight
            ? (defaultVerticalAlignment ?? "top") : nil)
      {
        NSLayoutConstraint.deactivate(Array(rootConstraints.suffix(2)))
        contentPosition =
          alignment == "bottom"
          ? root.bottomAnchor.constraint(
            equalTo: contentView.bottomAnchor, constant: -contentInsets.bottom)
          : alignment == "center"
            ? root.centerYAnchor.constraint(
              equalTo: contentView.centerYAnchor,
              constant: (contentInsets.top - contentInsets.bottom) / 2)
            : root.topAnchor.constraint(equalTo: contentView.topAnchor, constant: contentInsets.top)
        contentPosition?.isActive = true
      }
      boundInputs = inputs
    }
    self.item = item
    self.theme = theme
    self.layout = layout
    self.itemIndex = itemIndex
    self.selectedState = selected
    bindSelectionContent(item, checkboxState: checkboxState)
    accessibilityLabel = accessibilityText(item)
    accessibilityIdentifier = item.data["testID"] as? String
    isUserInteractionEnabled = !item.data.bool("disabled")
    if !isUserInteractionEnabled { isHighlighted = false }
    applyAppearance()
    setNeedsLayout()
  }

  override func updateSelection(
    item: NativeListItem, selected: Bool,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    guard self.item?.key == item.key else { return }
    self.item = item
    self.selectedState = selected
    bindSelectionContent(item, checkboxState: checkboxState)
    applyAppearance()
  }
  override var isHighlighted: Bool { didSet { applyAppearance() } }

  func emitAction(
    _ key: String, from view: UIView, source: String, slot: Int? = nil,
    target: NativeSelectionTarget? = nil, anchorInset: CGFloat = 0
  ) {
    guard let item, !key.isEmpty, !item.data.bool("disabled") else { return }
    onAction?(
      item, key, target,
      NativeListActionOrigin(
        sourceView: view, ownerCell: self,
        bindingEpoch: bindingEpoch, source: source, slot: slot, anchorInset: anchorInset))
  }

  func applyAppearance() {
    guard let item else { return }
    let container = item.data.dictionary("style")?.dictionary("container") ?? [:]
    let showSelection =
      showsSelection && selectedState && (layout != "sectioned" || item.data.bool("selected"))
    var background =
      showSelection
      ? nativeListColor(theme, "rowSelectedBackground", "#F0F0F0")
      : unselectedBackground(item, theme: theme, layout: layout, itemIndex: itemIndex)
    let rowFill =
      container["backgroundColor"]
      ?? (honorsRowBackgroundColor ? item.data["backgroundColor"] : nil)
    if let color = rowFill as? String {
      background = UIColor(nativeListHex: color, fallback: background)
    }
    contentView.backgroundColor =
      isHighlighted && pressChangesBackground
      ? pressedBackground(theme) : background
    contentView.alpha =
      CGFloat(container.double("opacity", default: item.data.double("opacity", default: 1)))
      * (item.data.bool("disabled") ? 0.5 : 1)
    let position = item.data.string("groupPosition")
    let grouped = ["first", "last", "single"].contains(position)
    let pressedRadius =
      isHighlighted && isUserInteractionEnabled ? pressedCornerRadius.map(Double.init) : nil
    let fallbackRadius =
      defaultCornerRadius > 0
      ? Double(defaultCornerRadius)
      : grouped
        ? listStyle?.double("groupCornerRadius", default: Double(groupCornerRadius))
          ?? Double(groupCornerRadius) : 0
    let radius = CGFloat(
      container.double(
        "cornerRadius", default: pressedRadius ?? fallbackRadius))
    let top: CACornerMask = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
    let bottom: CACornerMask = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
    layer.maskedCorners =
      container["cornerRadius"] != nil || position == "single" || defaultCornerRadius > 0
        || pressedRadius != nil
      ? top.union(bottom) : position == "first" ? top : position == "last" ? bottom : []
    layer.cornerCurve = defaultCornerCurve
    contentView.layer.cornerCurve = defaultCornerCurve
    layer.cornerRadius = radius
    layer.masksToBounds = radius > 0
    contentView.layer.cornerRadius = radius
    contentView.layer.maskedCorners = layer.maskedCorners
    contentView.layer.borderWidth = CGFloat(
      container.double("borderWidth", default: Double(defaultBorderWidth)))
    contentView.layer.borderColor =
      UIColor(
        nativeListHex: container.string("borderColor"), fallback: defaultBorderColor(theme)
      ).cgColor
    if item.data.bool("backgroundFullWidth"),
      let fill = rowFill as? String
    {
      fullWidthBackground.backgroundColor = UIColor(nativeListHex: fill, fallback: .clear).cgColor
      contentView.layer.insertSublayer(fullWidthBackground, at: 0)
      clipsToBounds = false
    } else {
      fullWidthBackground.removeFromSuperlayer()
    }
    separator.isHidden = !item.data.bool("separator")
    separator.backgroundColor = UIColor(
      nativeListHex: listStyle?.dictionary("separator")?.string(
        "color", default: theme?.string("separator", default: "#E0E0E0") ?? "#E0E0E0") ?? theme?
        .string("separator", default: "#E0E0E0") ?? "#E0E0E0", fallback: .lightGray)
  }
  override func layoutSubviews() {
    super.layoutSubviews()
    fullWidthBackground.frame = CGRect(
      x: -frame.minX, y: 0, width: superview?.bounds.width ?? bounds.width, height: bounds.height)
    let inset = CGFloat(
      listStyle?.dictionary("separator")?.double("inset", default: Double(defaultSeparatorInset))
        ?? Double(defaultSeparatorInset))
    separator.frame = CGRect(
      x: effectiveUserInterfaceLayoutDirection == .rightToLeft ? 0 : inset,
      y: contentView.bounds.height - 1 / UIScreen.main.scale,
      width: max(0, contentView.bounds.width - inset), height: 1 / UIScreen.main.scale)
  }
  override func prepareForReuse() {
    super.prepareForReuse()
    if item != nil { onBindingInvalidated?(self, bindingEpoch) }
    bindingEpoch &+= 1
    item = nil
    boundInputs = nil
    recycleContent()
    fullWidthBackground.removeFromSuperlayer()
    isHighlighted = false
  }
}

/// Type-aware structural equality for bridged JSON values, matching the former
/// sorted-keys JSON digest without serializing: `true` != `1` (CFBoolean vs NSNumber),
/// numbers compare by value (`1` == `1.0`), strings by UTF-16 code units, and inside objects
/// an explicit `null` differs from a missing key. At the top level a missing value equals
/// `NSNull` (the digest used `value ?? NSNull()`).
func nativeListValuesEqual(_ lhs: Any?, _ rhs: Any?) -> Bool {
  switch (lhs.flatMap { $0 is NSNull ? nil : $0 }, rhs.flatMap { $0 is NSNull ? nil : $0 }) {
  case (nil, nil): return true
  case let (lhs?, rhs?): return nativeListJSONEqual(lhs as AnyObject, rhs as AnyObject)
  default: return false
  }
}

private func nativeListJSONEqual(_ lhs: AnyObject, _ rhs: AnyObject) -> Bool {
  if lhs === rhs { return true }
  if let lhs = lhs as? NSNumber, let rhs = rhs as? NSNumber {
    let boolean = CFBooleanGetTypeID()
    return (CFGetTypeID(lhs) == boolean) == (CFGetTypeID(rhs) == boolean) && lhs.isEqual(to: rhs)
  }
  if let lhs = lhs as? NSString, let rhs = rhs as? NSString { return lhs.isEqual(to: rhs as String) }
  if let lhs = lhs as? NSDictionary, let rhs = rhs as? NSDictionary {
    guard lhs.count == rhs.count else { return false }
    for (key, value) in lhs {
      guard let other = rhs.object(forKey: key),
        nativeListJSONEqual(value as AnyObject, other as AnyObject)
      else { return false }
    }
    return true
  }
  if let lhs = lhs as? NSArray, let rhs = rhs as? NSArray {
    guard lhs.count == rhs.count else { return false }
    for index in 0..<lhs.count
    where !nativeListJSONEqual(lhs[index] as AnyObject, rhs[index] as AnyObject) {
      return false
    }
    return true
  }
  if lhs is NSNull || rhs is NSNull { return lhs is NSNull && rhs is NSNull }
  return lhs.isEqual(rhs)
}
