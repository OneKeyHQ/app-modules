import UIKit

// Common list-facing lifecycle. Legacy templates and migrated renderers share
// this contract, not their view allocation or reset implementation.
class NativeListRowHost: UICollectionViewCell {
  class func measure(_ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String)
    -> CGFloat?
  { nil }
  class func appliesSizePreset(_ item: NativeListItem) -> Bool { true }

  var bindingEpoch = 0
  var listStyle: [String: Any]?
  var onAction: ((NativeListItem, String, NativeSelectionTarget?, NativeListActionOrigin?) -> Void)?
  var onBindingInvalidated: ((NativeListRowHost, Int) -> Void)?

  func bind(
    item: NativeListItem, theme: [String: Any]?, layout: String, itemIndex: Int? = nil,
    selected: Bool, checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {
    preconditionFailure("A row host must implement binding")
  }
  func updateSelection(
    item: NativeListItem, selected: Bool,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String
  ) {}
  func updateMarketQuote(_ item: NativeListItem, theme: [String: Any]?) {}
  func setPressed(_ pressed: Bool) { isHighlighted = pressed && isUserInteractionEnabled }
  func canStartWalletGroupReorder(at point: CGPoint) -> Bool { true }
  func setWalletGroupReorderCompact(_ compact: Bool) {}
  func prepareWalletGroupReorderExpansion() {}
  func animateWalletGroupReorderExpansion() {}
  func finishWalletGroupReorderExpansion() {}
  func rowActionOrigin() -> NativeListActionOrigin {
    NativeListActionOrigin(
      sourceView: contentView, ownerCell: self, bindingEpoch: bindingEpoch, source: "row")
  }
}
