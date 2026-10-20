import Foundation
import UIKit
import React

extension Notification.Name {
  static let bottomAccessoryPlacementChanged = Notification.Name("bottomAccessoryPlacementChanged")
}

class RCTBottomAccessoryContainerView: UIView {

  // MARK: - Events
  @objc var onNativeLayout: RCTDirectEventBlock?
  @objc var onPlacementChanged: RCTDirectEventBlock?

  // MARK: - Init

  override init(frame: CGRect) {
    super.init(frame: frame)

    NotificationCenter.default.addObserver(
      self,
      selector: #selector(handlePlacementNotification(_:)),
      name: .bottomAccessoryPlacementChanged,
      object: nil
    )
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
  }

  // MARK: - Layout

  override func layoutSubviews() {
    super.layoutSubviews()
    let bounds = self.bounds
    guard bounds.width > 0 && bounds.height > 0 else { return }
    onNativeLayout?(["width": Double(bounds.width), "height": Double(bounds.height)])
  }

  // MARK: - Placement

  @objc private func handlePlacementNotification(_ notification: Notification) {
    guard let placement = notification.userInfo?["placement"] as? String else { return }
    onPlacementChanged?(["placement": placement])
  }
}
