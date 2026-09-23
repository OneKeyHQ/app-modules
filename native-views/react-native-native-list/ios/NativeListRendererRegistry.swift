import UIKit

// Closed internal registry. Legacy templates keep their existing implementation
// until their own migration; no public plugin or bridge surface is introduced.
enum NativeListRendererRegistry {
  private static let hosts:
    [NativeListRendererKey: (type: NativeListRowHost.Type, create: () -> NativeListRowHost)] = [
      .legacy: (NativeListCell.self, { NativeListCell(frame: .zero) }),
      .message: (NativeListMessageCell.self, { NativeListMessageCell(frame: .zero) }),
      .rail: (NativeListRailCell.self, { NativeListRailCell(frame: .zero) }),
      .mediaTile: (NativeListMediaTileCell.self, { NativeListMediaTileCell(frame: .zero) }),
      .dataRow: (NativeListDataRowCell.self, { NativeListDataRowCell(frame: .zero) }),
      .activity: (NativeListActivityCell.self, { NativeListActivityCell(frame: .zero) }),
      .system: (NativeListSystemCell.self, { NativeListSystemCell(frame: .zero) }),
      .action: (NativeListActionCell.self, { NativeListActionCell(frame: .zero) }),
    ]
  static func create(_ key: NativeListRendererKey) -> NativeListRowHost { hosts[key]!.create() }
  static func key(for type: String) -> NativeListRendererKey {
    NativeListRendererKey(rawValue: type) ?? .legacy
  }
  static func reuseIdentifier(for key: NativeListRendererKey) -> String {
    "NativeList.\(key.rawValue)"
  }
  static func register(in collectionView: UICollectionView) {
    for (key, host) in hosts {
      collectionView.register(host.type, forCellWithReuseIdentifier: reuseIdentifier(for: key))
    }
  }
  static func appliesSizePreset(_ item: NativeListItem) -> Bool {
    item.rendererKey != .system || NativeListSystemRenderer.appliesSizePreset(item)
  }
  static func measure(_ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String)
    -> CGFloat?
  {
    if item.rendererKey == .dataRow {
      let secondary = item.data.dictionaries("columns").contains { !$0.string("secondaryText").isEmpty }
      return secondary ? 60 : layout == "table" ? 48 : 56
    }
    if item.rendererKey == .activity { return item.data.dictionaries("footerActions").isEmpty ? 60 : 100 }
    if item.rendererKey == .system { return NativeListSystemRenderer.measure(item, width: width) }
    if item.rendererKey == .action {
      return item.data.string("presentation") == "accountSelector"
        ? 48 : item.data.dictionary("icon") == nil ? 44 : 60
    }
    if item.rendererKey == .rail { return 40 }
    if item.rendererKey == .mediaTile { return 244 }
    guard item.rendererKey == .message else { return nil }
    return NativeListMessageRenderer.Resolved(item, theme: theme, layout: layout).measure(
      width: width)
  }
}
