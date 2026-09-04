import Foundation
import UIKit

struct NativeListItem {
  let key: String
  let type: String
  let sectionKey: String?
  let revision: Int
  let data: [String: Any]
  let content: String

  var isSelectable: Bool {
    let disabled = data["disabled"] as? Bool ?? false
    return !disabled && Self.selectableTypes.contains(type)
  }

  var isReorderable: Bool {
    guard !data.bool("disabled") else { return false }
    if type == "rail", data.bool("draggable") { return true }
    return data.dictionaries("trailing").contains { $0.string("kind") == "drag" }
  }

  init(data: [String: Any]) throws {
    guard let key = data["key"] as? String, !key.isEmpty,
          let type = data["type"] as? String, !type.isEmpty else {
      throw NativeListModelError.invalidRow
    }
    self.key = key
    self.type = type
    self.sectionKey = data["sectionKey"] as? String
    self.revision = data["revision"] as? Int ?? 0
    self.data = data
    let jsonData = try JSONSerialization.data(withJSONObject: data, options: [.sortedKeys])
    self.content = String(data: jsonData, encoding: .utf8) ?? ""
  }

  private static let selectableTypes: Set<String> = [
    "identity", "rail", "activity", "message", "dataRow", "mediaTile", "metricCard",
  ]
}

struct NativeListConfig {
  let generation: Int
  let layout: String
  let orientation: String
  let gridColumns: Int
  let stickyHeaders: Bool
  let contentPadding: CGFloat
  let contentPaddingHorizontal: CGFloat
  let contentPaddingTop: CGFloat
  let contentPaddingBottom: CGFloat
  let itemSpacing: CGFloat
  let selectionMode: String
  let rowPressToggles: Bool
  var selectedKeys: Set<String>
  let reorderable: Bool
  let pullToRefresh: Bool
  var refreshing: Bool
  let loadMore: Bool
  let endReachedThreshold: Double
  let sectionIndexEnabled: Bool
  let sectionIndexHapticsEnabled: Bool
  let theme: [String: Any]?
  let fixedFooter: NativeListItem?
  var items: [NativeListItem]

  static func parse(json: String) throws -> NativeListConfig {
    guard let bytes = json.data(using: .utf8),
          let root = try JSONSerialization.jsonObject(with: bytes) as? [String: Any],
          root["schemaVersion"] as? Int == 1,
          let generation = root["generation"] as? Int,
          let layoutData = root["layout"] as? [String: Any],
          let layout = layoutData["kind"] as? String,
          let rowData = root["rows"] as? [[String: Any]] else {
      throw NativeListModelError.invalidSnapshot
    }

    var items = try rowData.map(NativeListItem.init)
    let keys = Set(items.map(\.key))
    guard keys.count == items.count else { throw NativeListModelError.duplicateKey }
    if items.isEmpty, let emptyData = root["emptyState"] as? [String: Any] {
      items = [try NativeListItem(data: emptyData)]
    }

    let selection = root["selection"] as? [String: Any]
    let selectedKeys = Set(selection?["selectedKeys"] as? [String] ?? [])
    guard selectedKeys.isSubset(of: keys) else { throw NativeListModelError.unknownSelectionKey }
    let capabilities = root["capabilities"] as? [String: Any]
    let sectionIndex = capabilities?["sectionIndex"] as? [String: Any]
    let footer = try (root["fixedFooter"] as? [String: Any]).map(NativeListItem.init)

    let contentPadding = max(0, CGFloat(layoutData.double("contentPadding")))
    let contentPaddingHorizontal = layoutData["contentPaddingHorizontal"] == nil
      ? contentPadding
      : max(0, CGFloat(layoutData.double("contentPaddingHorizontal")))
    let contentPaddingTop = layoutData["contentPaddingTop"] == nil
      ? contentPadding
      : max(0, CGFloat(layoutData.double("contentPaddingTop")))
    let contentPaddingBottom = layoutData["contentPaddingBottom"] == nil
      ? contentPadding
      : max(0, CGFloat(layoutData.double("contentPaddingBottom")))

    return NativeListConfig(
      generation: generation,
      layout: layout,
      orientation: layoutData["orientation"] as? String ?? "vertical",
      gridColumns: min(4, max(1, layoutData["gridColumns"] as? Int ?? 1)),
      stickyHeaders: layoutData["stickyHeaders"] as? Bool ?? false,
      contentPadding: contentPadding,
      contentPaddingHorizontal: contentPaddingHorizontal,
      contentPaddingTop: contentPaddingTop,
      contentPaddingBottom: contentPaddingBottom,
      itemSpacing: max(0, layoutData["itemSpacing"] as? CGFloat ?? 0),
      selectionMode: selection?["mode"] as? String ?? "none",
      rowPressToggles: selection?["rowPressToggles"] as? Bool ?? false,
      selectedKeys: selectedKeys,
      reorderable: capabilities?["reorderable"] as? Bool ?? false,
      pullToRefresh: capabilities?["pullToRefresh"] as? Bool ?? false,
      refreshing: capabilities?["refreshing"] as? Bool ?? false,
      loadMore: capabilities?["loadMore"] as? Bool ?? false,
      endReachedThreshold: capabilities?["endReachedThreshold"] as? Double ?? 0.2,
      sectionIndexEnabled: sectionIndex?["enabled"] as? Bool ?? false,
      sectionIndexHapticsEnabled: sectionIndex?["hapticsEnabled"] as? Bool ?? true,
      theme: root["theme"] as? [String: Any],
      fixedFooter: footer,
      items: items
    )
  }
}

enum NativeListModelError: Error {
  case invalidSnapshot
  case invalidRow
  case duplicateKey
  case unknownSelectionKey
}

struct NativeSelectionTarget {
  let scope: String
  let key: String?
}

extension Dictionary where Key == String, Value == Any {
  func string(_ key: String, default fallback: String = "") -> String {
    self[key] as? String ?? fallback
  }

  func bool(_ key: String, default fallback: Bool = false) -> Bool {
    self[key] as? Bool ?? fallback
  }

  func int(_ key: String, default fallback: Int = 0) -> Int {
    if let value = self[key] as? Int { return value }
    if let value = self[key] as? NSNumber { return value.intValue }
    return fallback
  }

  func double(_ key: String, default fallback: Double = 0) -> Double {
    if let value = self[key] as? Double { return value }
    if let value = self[key] as? NSNumber { return value.doubleValue }
    return fallback
  }

  func dictionary(_ key: String) -> [String: Any]? {
    self[key] as? [String: Any]
  }

  func dictionaries(_ key: String) -> [[String: Any]] {
    self[key] as? [[String: Any]] ?? []
  }
}

extension UIColor {
  convenience init(nativeListHex value: String, fallback: UIColor) {
    let value = value.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
    var hex: UInt64 = 0
    guard Scanner(string: value).scanHexInt64(&hex) else {
      self.init(cgColor: fallback.cgColor)
      return
    }
    let red: CGFloat
    let green: CGFloat
    let blue: CGFloat
    let alpha: CGFloat
    switch value.count {
    case 8:
      red = CGFloat((hex >> 24) & 0xff) / 255
      green = CGFloat((hex >> 16) & 0xff) / 255
      blue = CGFloat((hex >> 8) & 0xff) / 255
      alpha = CGFloat(hex & 0xff) / 255
    case 6:
      red = CGFloat((hex >> 16) & 0xff) / 255
      green = CGFloat((hex >> 8) & 0xff) / 255
      blue = CGFloat(hex & 0xff) / 255
      alpha = 1
    default:
      self.init(cgColor: fallback.cgColor)
      return
    }
    self.init(red: red, green: green, blue: blue, alpha: alpha)
  }
}

func nativeListColor(
  _ theme: [String: Any]?,
  _ key: String,
  _ fallback: String
) -> UIColor {
  UIColor(
    nativeListHex: theme?[key] as? String ?? fallback,
    fallback: UIColor(nativeListHex: fallback, fallback: .black)
  )
}
