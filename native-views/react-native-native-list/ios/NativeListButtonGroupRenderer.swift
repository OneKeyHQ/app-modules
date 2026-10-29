import UIKit

final class NativeListButtonGroupCell: NativeListRendererCell {
  override class func measure(
    _ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String,
    fonts: NativeListFontFamilies
  ) -> CGFloat? { 64 }

  private var buttons: [UIButton] = []
  private var actionKeys: [String] = []
  private var rowOpacity: CGFloat = 1

  override func applyAppearance() {
    super.applyAppearance()
    contentView.alpha = rowOpacity
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    root.axis = .horizontal
    root.alignment = .fill
    root.distribution = .fillEqually
    root.spacing = 10
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func bindContent(
    _ item: NativeListItem, theme: [String: Any]?, layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    recycleContent()
    rowOpacity = CGFloat(item.data.dictionary("style")?.dictionary("container")?.double("opacity", default: item.data.double("opacity", default: 1)) ?? item.data.double("opacity", default: 1))
    contentInsets = UIEdgeInsets(top: 8, left: 0, bottom: 8, right: 0)
    let info = nativeListColor(theme, "info", "#0F69F6")
    for (index, descriptor) in item.data.dictionaries("buttons").enumerated() {
      let button = NativeListAccessoryButton(type: .custom)
      button.tag = index
      button.setTitle(descriptor.string("label"), for: .normal)
      button.titleLabel?.font = nativeListFont(ofSize: 14, weight: .medium, fonts: fonts)
      button.titleLabel?.lineBreakMode = .byTruncatingTail
      button.setTitleColor(info, for: .normal)
      button.contentEdgeInsets = UIEdgeInsets(top: 12, left: 8, bottom: 12, right: 8)
      button.layer.cornerRadius = 12
      button.layer.borderWidth = 1
      button.layer.borderColor = info.withAlphaComponent(0.35).cgColor
      button.normalBackgroundColor = .clear
      button.pressedBackgroundColor = info.withAlphaComponent(0.17)
      button.isEnabled = !item.data.bool("disabled") && !descriptor.bool("disabled") && !descriptor.bool("loading")
      button.alpha = button.isEnabled ? 1 : 0.4
      button.accessibilityIdentifier = descriptor["testID"] as? String
      button.accessibilityLabel = descriptor.string("accessibilityLabel", default: descriptor.string("label"))
      button.addTarget(self, action: #selector(pressed(_:)), for: .touchUpInside)
      if descriptor.bool("loading"), let titleLabel = button.titleLabel {
        let spinner = UIActivityIndicatorView(style: .medium)
        spinner.color = info
        spinner.isUserInteractionEnabled = false
        spinner.translatesAutoresizingMaskIntoConstraints = false
        button.addSubview(spinner)
        button.contentEdgeInsets = UIEdgeInsets(top: 12, left: 36, bottom: 12, right: 8)
        NSLayoutConstraint.activate([
          spinner.trailingAnchor.constraint(equalTo: titleLabel.leadingAnchor, constant: -8),
          spinner.centerYAnchor.constraint(equalTo: button.centerYAnchor),
          spinner.widthAnchor.constraint(equalToConstant: 20),
          spinner.heightAnchor.constraint(equalToConstant: 20),
        ])
        spinner.startAnimating()
      }
      buttons.append(button)
      actionKeys.append(descriptor.string("actionKey"))
      root.addArrangedSubview(button)
    }
  }

  @objc private func pressed(_ button: UIButton) {
    guard button.isEnabled, buttons.indices.contains(button.tag), buttons[button.tag] === button else { return }
    emitAction(actionKeys[button.tag], from: button, source: "buttonGroup", slot: button.tag)
  }

  override func recycleContent() {
    for button in buttons {
      button.removeTarget(self, action: nil, for: .allEvents)
      button.subviews.compactMap { $0 as? UIActivityIndicatorView }.forEach { $0.stopAnimating() }
      root.removeArrangedSubview(button)
      button.removeFromSuperview()
    }
    buttons.removeAll()
    actionKeys.removeAll()
  }
}
