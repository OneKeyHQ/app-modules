import Foundation
import UIKit
import NitroModules

extension Notification.Name {
  static let bottomAccessoryPlacementChanged = Notification.Name("bottomAccessoryPlacementChanged")
}

class HybridBottomAccessoryView: HybridBottomAccessoryViewSpec {

  // MARK: - HybridView

  var view: UIView

  // MARK: - Events

  var onNativeLayout: ((_ width: Double, _ height: Double) -> Void)?
  var onPlacementChanged: ((_ placement: String) -> Void)?

  // MARK: - Init

  override init() {
    self.view = BottomAccessoryLayoutView()
    super.init()
    (self.view as? BottomAccessoryLayoutView)?.layoutCallback = { [weak self] in
      self?.handleLayout()
    }

    NotificationCenter.default.addObserver(
      self,
      selector: #selector(handlePlacementNotification(_:)),
      name: .bottomAccessoryPlacementChanged,
      object: nil
    )
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
  }

  private func handleLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 && bounds.height > 0 else { return }
    onNativeLayout?(Double(bounds.width), Double(bounds.height))
  }

  @objc private func handlePlacementNotification(_ notification: Notification) {
    guard let placement = notification.userInfo?["placement"] as? String else { return }
    onPlacementChanged?(placement)
  }
}

private class BottomAccessoryLayoutView: UIView {
  var layoutCallback: (() -> Void)?

  override func layoutSubviews() {
    super.layoutSubviews()
    layoutCallback?()
  }
}
