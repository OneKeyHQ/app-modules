import UIKit

// Closed internal registry. Legacy templates keep their existing implementation
// until their own migration; no public plugin or bridge surface is introduced.
enum NativeListRendererRegistry {
  private static let hosts: [NativeListRendererKey: NativeListRowHost.Type] = [
    .legacy: NativeListCell.self, .message: NativeListMessageCell.self,
    .rail: NativeListRailCell.self,
  ]
  static func key(for type: String) -> NativeListRendererKey {
    NativeListRendererKey(rawValue: type) ?? .legacy
  }
  static func reuseIdentifier(for key: NativeListRendererKey) -> String {
    "NativeList.\(key.rawValue)"
  }
  static func register(in collectionView: UICollectionView) {
    for (key, host) in hosts {
      collectionView.register(host, forCellWithReuseIdentifier: reuseIdentifier(for: key))
    }
  }
  static func measure(_ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String)
    -> CGFloat?
  {
    if item.rendererKey == .rail { return 40 }
    guard item.rendererKey == .message else { return nil }
    return NativeListMessageRenderer.Resolved(item, theme: theme, layout: layout).measure(
      width: width)
  }
}
