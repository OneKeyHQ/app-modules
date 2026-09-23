import UIKit

/// Composite only: member content, assets and local styles belong to Identity.
final class NativeListWalletGroupCell: NativeListRendererCell, UIGestureRecognizerDelegate {
  private struct MemberView {
    let cell: NativeListIdentityCell
    let height: NSLayoutConstraint
  }
  private var members: [NativeListItem] = []
  private var memberViews: [String: MemberView] = [:]
  private var compactCell: NativeListIdentityCell?
  private let compactContainer = UIView()
  private let dragBadge = NativeListInsetLabel()
  private var compact = false
  private lazy var memberTap = UITapGestureRecognizer(
    target: self, action: #selector(memberPressed(_:)))
  override var usesIntrinsicContentHeight: Bool { true }
  override var defaultCornerRadius: CGFloat { 20 }
  override var defaultCornerCurve: CALayerCornerCurve { .continuous }
  override var defaultBorderWidth: CGFloat { 1 }
  override var pressChangesBackground: Bool { false }
  override var showsSelection: Bool { false }
  override func defaultBorderColor(_ theme: [String: Any]?) -> UIColor {
    nativeListColor(theme, "separator", "#E0E0E0")
  }
  override func unselectedBackground(
    _ item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int?
  ) -> UIColor {
    nativeListColor(theme, "subduedBackground", "#F9F9F9")
  }
  private static func memberItems(_ item: NativeListItem) -> [NativeListItem] {
    ([item.data.dictionary("parent")].compactMap { $0 } + item.data.dictionaries("children"))
      .compactMap { try? NativeListItem(data: $0) }
  }
  private static func memberHeight(_ item: NativeListItem) -> CGFloat {
    item.styledHeight
      ?? (item.data["height"] != nil
        ? CGFloat(item.data.double("height"))
        : NativeListIdentityCell.measure(item, width: 0, theme: nil, layout: "linear") ?? 68)
  }
  override class func appliesSizePreset(_ item: NativeListItem) -> Bool { false }
  override class func measure(
    _ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String
  ) -> CGFloat? {
    let members = memberItems(item)
    return members.reduce(
      CGFloat(max(0, members.count - 1) * 12 + (members.first?.data["height"] != nil ? 2 : 0))
    ) { $0 + memberHeight($1) }
  }
  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .vertical
    root.alignment = .fill
    root.spacing = 12
    memberTap.delegate = self
    root.addGestureRecognizer(memberTap)
    compactContainer.translatesAutoresizingMaskIntoConstraints = false
    compactContainer.isUserInteractionEnabled = false
    compactContainer.isHidden = true
    contentView.addSubview(compactContainer)
    dragBadge.translatesAutoresizingMaskIntoConstraints = false
    dragBadge.horizontalInset = 6
    dragBadge.textAlignment = .center
    dragBadge.font = nativeListTabularFont(ofSize: 12, weight: .semibold)
    dragBadge.layer.cornerRadius = 12
    dragBadge.layer.masksToBounds = true
    dragBadge.layer.borderWidth = 1
    compactContainer.addSubview(dragBadge)
    NSLayoutConstraint.activate([
      compactContainer.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
      compactContainer.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
      compactContainer.topAnchor.constraint(equalTo: contentView.topAnchor),
      compactContainer.heightAnchor.constraint(equalToConstant: 68),
      dragBadge.trailingAnchor.constraint(equalTo: compactContainer.trailingAnchor, constant: -4),
      dragBadge.bottomAnchor.constraint(equalTo: compactContainer.bottomAnchor, constant: -4),
      dragBadge.heightAnchor.constraint(equalToConstant: 24),
      dragBadge.widthAnchor.constraint(greaterThanOrEqualToConstant: 24),
    ])
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
  override func accessibilityText(_ item: NativeListItem) -> String {
    item.data.string(
      "accessibilityLabel", default: item.data.dictionary("parent")?.string("title") ?? "")
  }
  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    members = Self.memberItems(item)
    let keys = Set(members.map(\.key))
    for key in Array(memberViews.keys) where !keys.contains(key) {
      let view = memberViews.removeValue(forKey: key)!
      view.cell.prepareForReuse()
      root.removeArrangedSubview(view.cell)
      view.cell.removeFromSuperview()
    }
    let style = item.data.dictionary("style") ?? [:]
    let inset: Double = members.first?.data["height"] != nil ? 1 : 0
    let horizontal = CGFloat(style.double("horizontalPadding", default: inset))
    let vertical = CGFloat(style.double("verticalPadding", default: inset))
    contentInsets = UIEdgeInsets(
      top: vertical, left: horizontal, bottom: vertical, right: horizontal)
    for (index, member) in members.enumerated() {
      let view: MemberView
      if let existing = memberViews[member.key] {
        view = existing
      } else {
        let cell = NativeListIdentityCell(frame: .zero)
        cell.translatesAutoresizingMaskIntoConstraints = false
        let height = cell.heightAnchor.constraint(equalToConstant: Self.memberHeight(member))
        height.isActive = true
        view = MemberView(cell: cell, height: height)
        memberViews[member.key] = view
        cell.onAction = { [weak self] item, action, target, origin in
          self?.onAction?(item, action, target, origin)
        }
        cell.onBindingInvalidated = { [weak self] cell, epoch in
          self?.onBindingInvalidated?(cell, epoch)
        }
      }
      view.height.constant = Self.memberHeight(member)
      view.cell.listStyle = listStyle
      view.cell.bind(
        item: member, theme: theme, layout: layout, selected: member.data.bool("selected"),
        checkboxState: checkboxState)
      root.insertArrangedSubview(view.cell, at: index)
    }
    if let parent = members.first {
      if compactCell == nil {
        let cell = NativeListIdentityCell(frame: .zero)
        cell.translatesAutoresizingMaskIntoConstraints = false
        compactContainer.insertSubview(cell, at: 0)
        NSLayoutConstraint.activate([
          cell.leadingAnchor.constraint(equalTo: compactContainer.leadingAnchor),
          cell.trailingAnchor.constraint(equalTo: compactContainer.trailingAnchor),
          cell.topAnchor.constraint(equalTo: compactContainer.topAnchor),
          cell.bottomAnchor.constraint(equalTo: compactContainer.bottomAnchor),
        ])
        compactCell = cell
      }
      compactCell?.listStyle = listStyle
      compactCell?.bind(
        item: parent, theme: theme, layout: layout, selected: parent.data.bool("selected"),
        checkboxState: checkboxState)
    }
    let count = members.dropFirst().filter { ($0.data["draggable"] as? Bool) != false }.count
    dragBadge.text = count > 0 ? "+\(count)" : nil
    dragBadge.isHidden = count == 0
    dragBadge.backgroundColor = nativeListColor(theme, "inverseBackground", "#202020")
    dragBadge.textColor = nativeListColor(theme, "inverseText", "#FCFCFC")
    dragBadge.layer.borderColor = nativeListColor(theme, "rowBackground", "#FFFFFF").cgColor
  }
  override func bindSelectionContent(
    _ item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    members = Self.memberItems(item)
    for member in members {
      memberViews[member.key]?.cell.updateSelection(
        item: member, selected: member.data.bool("selected"), checkboxState: checkboxState)
    }
    if let parent = members.first {
      compactCell?.updateSelection(
        item: parent, selected: parent.data.bool("selected"), checkboxState: checkboxState)
    }
  }
  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch)
    -> Bool
  {
    var view = touch.view
    while let current = view, current !== root {
      if current is UIControl { return false }
      view = current.superview
    }
    return true
  }
  @objc private func memberPressed(_ gesture: UITapGestureRecognizer) {
    let point = gesture.location(in: root)
    for member in members {
      guard let cell = memberViews[member.key]?.cell, cell.frame.contains(point) else { continue }
      if !member.data.bool("disabled") && !member.data.bool("pressDisabled") {
        onAction?(member, "press", nil, cell.rowActionOrigin())
      }
      return
    }
  }
  override func canStartWalletGroupReorder(at point: CGPoint) -> Bool {
    let point = root.convert(point, from: self)
    for member in members where memberViews[member.key]?.cell.frame.contains(point) == true {
      return (member.data["draggable"] as? Bool) != false && !member.data.bool("disabled")
    }
    return true
  }
  override func applyAppearance() {
    super.applyAppearance()
    if compact {
      contentView.backgroundColor = .clear
      contentView.layer.borderWidth = 0
    }
  }
  override func setPressed(_ pressed: Bool) {
    if compact { compactCell?.setPressed(pressed) } else { super.setPressed(pressed) }
  }
  override func setWalletGroupReorderCompact(_ compact: Bool) {
    self.compact = compact
    root.isHidden = compact
    root.alpha = compact ? 0 : 1
    compactContainer.isHidden = !compact
    compactContainer.alpha = compact ? 1 : 0
    applyAppearance()
  }
  override func prepareWalletGroupReorderExpansion() {
    compact = false
    root.isHidden = false
    root.alpha = 0
    compactContainer.isHidden = false
    compactContainer.alpha = 1
    applyAppearance()
  }
  override func animateWalletGroupReorderExpansion() {
    root.alpha = 1
    compactContainer.alpha = 0
  }
  override func finishWalletGroupReorderExpansion() { setWalletGroupReorderCompact(false) }
  override func recycleContent() {
    for view in memberViews.values {
      view.cell.prepareForReuse()
      view.cell.removeFromSuperview()
    }
    memberViews.removeAll()
    members.removeAll()
    compactCell?.prepareForReuse()
    compactCell?.removeFromSuperview()
    compactCell = nil
    compact = false
    root.isHidden = false
    root.alpha = 1
    compactContainer.isHidden = true
    compactContainer.alpha = 1
    dragBadge.text = nil
  }
}
