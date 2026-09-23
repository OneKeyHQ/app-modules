import UIKit

// Closed internal registry; no public plugin or bridge surface.
enum NativeListRendererRegistry {
  private static let hosts:
    [NativeListRendererKey: (type: NativeListRowHost.Type, create: () -> NativeListRowHost)] = [
      .sectionHeader: (
        NativeListSectionHeaderCell.self, { NativeListSectionHeaderCell(frame: .zero) }
      ),
      .walletGroup: (NativeListWalletGroupCell.self, { NativeListWalletGroupCell(frame: .zero) }),
      .identity: (NativeListIdentityCell.self, { NativeListIdentityCell(frame: .zero) }),
      .market: (NativeListMarketCell.self, { NativeListMarketCell(frame: .zero) }),
      .message: (NativeListMessageCell.self, { NativeListMessageCell(frame: .zero) }),
      .rail: (NativeListRailCell.self, { NativeListRailCell(frame: .zero) }),
      .mediaTile: (NativeListMediaTileCell.self, { NativeListMediaTileCell(frame: .zero) }),
      .metricCard: (NativeListMetricCardCell.self, { NativeListMetricCardCell(frame: .zero) }),
      .dataRow: (NativeListDataRowCell.self, { NativeListDataRowCell(frame: .zero) }),
      .activity: (NativeListActivityCell.self, { NativeListActivityCell(frame: .zero) }),
      .system: (NativeListSystemCell.self, { NativeListSystemCell(frame: .zero) }),
      .action: (NativeListActionCell.self, { NativeListActionCell(frame: .zero) }),
    ]
  static func create(_ key: NativeListRendererKey) -> NativeListRowHost { hosts[key]!.create() }
  static func reuseIdentifier(for key: NativeListRendererKey) -> String {
    "NativeList.\(key.rawValue)"
  }
  static func register(in collectionView: UICollectionView) {
    for (key, host) in hosts {
      collectionView.register(host.type, forCellWithReuseIdentifier: reuseIdentifier(for: key))
    }
  }
  static func appliesSizePreset(_ item: NativeListItem) -> Bool {
    hosts[item.rendererKey]!.type.appliesSizePreset(item)
  }
  static func measure(_ item: NativeListItem, width: CGFloat, theme: [String: Any]?, layout: String)
    -> CGFloat?
  {
    hosts[item.rendererKey]!.type.measure(item, width: width, theme: theme, layout: layout)
  }
}
