import Foundation
import UIKit
import UniformTypeIdentifiers

final class NativeListView: UIView {
  var onRowAction: ((String) -> Void)?
  var onSelectionDelta: ((String) -> Void)?
  var onReorder: ((String) -> Void)?
  var onEndReached: ((String) -> Void)?
  var onVisibleRangeChanged: ((String) -> Void)?

  private let flowLayout = NativeListFlowLayout()
  private lazy var collectionView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout)
  private let footerContainer = UIView()
  private let footerCell = NativeListCell(frame: .zero)
  private let sectionIndexView = NativeListSectionIndexView()
  private let sectionIndexPreview = UILabel()
  private var footerHeightConstraint: NSLayoutConstraint!
  private var dataSource: UICollectionViewDiffableDataSource<Int, String>!
  private var config: NativeListConfig?
  private var itemsByKey: [String: NativeListItem] = [:]
  private var endReachedGeneration: Int?
  private var lastVisibleRange: (first: Int, last: Int, firstKey: String?, lastKey: String?)?
  private var visibleEventScheduled = false
  private var sectionIndexEntries: [NativeListSectionIndexEntry] = []
  private var sectionIndexScrubbing = false
  private var sectionIndexHapticsEnabled = true
  private var lastLayoutDirection: UIUserInterfaceLayoutDirection?
  private let sectionIndexFeedback = UISelectionFeedbackGenerator()

  private static let sectionIndexGutter: CGFloat = 44

  override init(frame: CGRect) {
    super.init(frame: frame)
    collectionView.register(NativeListCell.self, forCellWithReuseIdentifier: NativeListCell.reuseIdentifier)
    collectionView.backgroundColor = .clear
    collectionView.delegate = self
    collectionView.dragDelegate = self
    collectionView.dropDelegate = self
    collectionView.alwaysBounceVertical = true

    addSubview(collectionView)
    addSubview(footerContainer)
    addSubview(sectionIndexView)
    addSubview(sectionIndexPreview)
    footerContainer.addSubview(footerCell)
    collectionView.translatesAutoresizingMaskIntoConstraints = false
    footerContainer.translatesAutoresizingMaskIntoConstraints = false
    sectionIndexView.translatesAutoresizingMaskIntoConstraints = false
    sectionIndexPreview.translatesAutoresizingMaskIntoConstraints = false
    footerCell.frame = footerContainer.bounds
    footerCell.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    footerHeightConstraint = footerContainer.heightAnchor.constraint(equalToConstant: 0)
    NSLayoutConstraint.activate([
      collectionView.leadingAnchor.constraint(equalTo: leadingAnchor),
      collectionView.trailingAnchor.constraint(equalTo: trailingAnchor),
      collectionView.topAnchor.constraint(equalTo: topAnchor),
      collectionView.bottomAnchor.constraint(equalTo: footerContainer.topAnchor),
      footerContainer.leadingAnchor.constraint(equalTo: leadingAnchor),
      footerContainer.trailingAnchor.constraint(equalTo: trailingAnchor),
      footerContainer.bottomAnchor.constraint(equalTo: bottomAnchor),
      footerHeightConstraint,
      sectionIndexView.trailingAnchor.constraint(equalTo: safeAreaLayoutGuide.trailingAnchor),
      sectionIndexView.topAnchor.constraint(equalTo: safeAreaLayoutGuide.topAnchor),
      sectionIndexView.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor),
      sectionIndexView.widthAnchor.constraint(equalToConstant: Self.sectionIndexGutter),
      sectionIndexPreview.centerXAnchor.constraint(equalTo: collectionView.centerXAnchor),
      sectionIndexPreview.centerYAnchor.constraint(equalTo: collectionView.centerYAnchor),
      sectionIndexPreview.widthAnchor.constraint(equalToConstant: 72),
      sectionIndexPreview.heightAnchor.constraint(equalToConstant: 72),
    ])

    sectionIndexView.isHidden = true
    sectionIndexView.onSelect = { [weak self] index, interacting in
      self?.selectSectionIndex(index, interacting: interacting)
    }
    sectionIndexView.onInteractionEnded = { [weak self] in
      self?.finishSectionIndexInteraction()
    }
    sectionIndexPreview.isHidden = true
    sectionIndexPreview.alpha = 0
    sectionIndexPreview.layer.cornerRadius = 16
    sectionIndexPreview.layer.masksToBounds = true
    sectionIndexPreview.textAlignment = .center
    sectionIndexPreview.adjustsFontForContentSizeCategory = true
    sectionIndexPreview.font = nativeListFont(ofSize: 28, weight: .semibold)
    sectionIndexPreview.isAccessibilityElement = false

    footerCell.onAction = { [weak self] item, action, target in
      self?.handleAction(item: item, actionKey: action, target: target)
    }
    let footerTap = UITapGestureRecognizer(target: self, action: #selector(footerPressed))
    footerTap.delegate = self
    footerCell.addGestureRecognizer(footerTap)
    let footerHighlight = UILongPressGestureRecognizer(
      target: self,
      action: #selector(footerHighlightChanged(_:))
    )
    footerHighlight.minimumPressDuration = 0
    footerHighlight.cancelsTouchesInView = false
    footerHighlight.delegate = self
    footerCell.addGestureRecognizer(footerHighlight)
    dataSource = UICollectionViewDiffableDataSource<Int, String>(collectionView: collectionView) {
      [weak self] collectionView, indexPath, key in
      guard let self,
            let item = self.itemsByKey[key],
            let cell = collectionView.dequeueReusableCell(
              withReuseIdentifier: NativeListCell.reuseIdentifier,
              for: indexPath
            ) as? NativeListCell else { return nil }
      cell.onAction = { [weak self] item, action, target in
        self?.handleAction(item: item, actionKey: action, target: target)
      }
      self.bind(cell: cell, item: item, itemIndex: indexPath.item)
      return cell
    }
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    let direction = effectiveUserInterfaceLayoutDirection
    if lastLayoutDirection != direction, let config {
      configureLayout(config)
    }
  }

  func applySnapshotJson(_ json: String) {
    guard let next = try? NativeListConfig.parse(json: json) else { return }
    if let current = config, isControlledSelectionSnapshotUpdate(from: current, to: next) {
      config = next
      itemsByKey = Dictionary(uniqueKeysWithValues: next.items.map { ($0.key, $0) })
      refreshVisibleSelection()
      return
    }
    let oldItems = itemsByKey
    config = next
    itemsByKey = Dictionary(uniqueKeysWithValues: next.items.map { ($0.key, $0) })
    endReachedGeneration = nil
    configureSectionIndex(next)
    configureLayout(next)
    configureRefresh(next)
    configureFooter(next)

    var snapshot = NSDiffableDataSourceSnapshot<Int, String>()
    snapshot.appendSections([0])
    let keys = next.items.map(\.key)
    snapshot.appendItems(keys, toSection: 0)
    let changedKeys = keys.filter { key in
      guard let old = oldItems[key], let new = itemsByKey[key] else { return false }
      return old.revision != new.revision || old.content != new.content
    }
    snapshot.reconfigureItems(changedKeys)
    dataSource.apply(snapshot, animatingDifferences: false) { [weak self] in
      self?.emitVisibleRangeIfNeeded()
      self?.checkEndReached()
      self?.syncSectionIndexToVisibleRows()
    }
  }

  func applyPatchesJson(_ json: String) {
    guard var current = config,
          let bytes = json.data(using: .utf8),
          let patches = try? JSONSerialization.jsonObject(with: bytes) as? [[String: Any]] else { return }
    let indexByKey = Dictionary(uniqueKeysWithValues: current.items.enumerated().map { ($1.key, $0) })
    var seen = Set<String>()
    var pending: [(Int, [String: Any])] = []
    for patch in patches {
      let key = patch.string("key")
      guard let index = indexByKey[key],
            seen.insert(key).inserted,
            patch.string("type") == current.items[index].type,
            let changes = patch.dictionary("changes") else { return }
      pending.append((index, changes))
    }

    var changedKeys: [String] = []
    for (index, changes) in pending {
      var merged = current.items[index].data
      changes.forEach { key, value in
        if key != "key" && key != "type" { merged[key] = value }
      }
      guard let item = try? NativeListItem(data: merged) else { return }
      current.items[index] = item
      changedKeys.append(item.key)
      if let selected = changes["selected"] as? Bool {
        if selected { current.selectedKeys.insert(item.key) } else { current.selectedKeys.remove(item.key) }
      }
    }
    config = current
    itemsByKey = Dictionary(uniqueKeysWithValues: current.items.map { ($0.key, $0) })
    var snapshot = dataSource.snapshot()
    snapshot.reconfigureItems(changedKeys.filter { snapshot.indexOfItem($0) != nil })
    dataSource.apply(snapshot, animatingDifferences: false)
    configureFooter(current)
  }

  func reconcileSelectionJson(_ json: String) {
    guard var current = config,
          let bytes = json.data(using: .utf8),
          let keys = try? JSONSerialization.jsonObject(with: bytes) as? [String] else { return }
    let known = Set(current.items.filter(\.isSelectable).map(\.key))
    let next = Set(keys)
    guard next.isSubset(of: known), current.selectionMode != "single" || next.count <= 1 else { return }
    current.selectedKeys = next
    config = current
    refreshVisibleSelection()
  }

  func scrollToKey(_ key: String, animated: Bool, alignment: String) {
    guard let index = config?.items.firstIndex(where: { $0.key == key }) else { return }
    scrollToIndex(index, animated: animated, alignment: alignment)
  }

  func scrollToIndex(_ index: Int, animated: Bool, alignment: String) {
    guard let count = config?.items.count, index >= 0, index < count else { return }
    let position: UICollectionView.ScrollPosition
    if config?.orientation == "horizontal" {
      position = alignment == "end" ? .right : alignment == "center" ? .centeredHorizontally : .left
    } else {
      position = alignment == "end" ? .bottom : alignment == "center" ? .centeredVertically : .top
    }
    collectionView.scrollToItem(at: IndexPath(item: index, section: 0), at: position, animated: animated)
  }

  func setRefreshing(_ refreshing: Bool) {
    guard var current = config else { return }
    current.refreshing = refreshing
    config = current
    if refreshing {
      collectionView.refreshControl?.beginRefreshing()
    } else {
      collectionView.refreshControl?.endRefreshing()
    }
  }

  private func configureLayout(_ config: NativeListConfig) {
    let isHorizontal = config.orientation == "horizontal"
    let indexGutter = sectionIndexEntries.isEmpty ? 0 : Self.sectionIndexGutter
    let isRightToLeft = effectiveUserInterfaceLayoutDirection == .rightToLeft
    flowLayout.scrollDirection = isHorizontal ? .horizontal : .vertical
    flowLayout.minimumLineSpacing = config.itemSpacing
    flowLayout.minimumInteritemSpacing = config.itemSpacing
    flowLayout.sectionInset = UIEdgeInsets(
      top: config.contentPaddingTop,
      left: config.contentPaddingHorizontal + (isRightToLeft ? indexGutter : 0),
      bottom: config.contentPaddingBottom,
      right: config.contentPaddingHorizontal + (isRightToLeft ? 0 : indexGutter)
    )
    flowLayout.stickyItemIndexes = config.stickyHeaders
      ? Set(config.items.enumerated().compactMap {
          $0.element.type == "sectionHeader" && $0.element.data.string("variant") != "summary"
            ? $0.offset
            : nil
        })
      : []
    flowLayout.invalidateLayout()
    collectionView.alwaysBounceHorizontal = isHorizontal
    collectionView.alwaysBounceVertical = !isHorizontal
    collectionView.showsVerticalScrollIndicator = sectionIndexEntries.isEmpty
    collectionView.dragInteractionEnabled = config.reorderable
    lastLayoutDirection = effectiveUserInterfaceLayoutDirection
  }

  private func configureSectionIndex(_ config: NativeListConfig) {
    let previousKey = sectionIndexView.activeIndex.flatMap { sectionIndexEntries[safe: $0]?.key }
    finishSectionIndexInteraction(immediately: true)
    let enabled = config.sectionIndexEnabled &&
      config.layout == "sectioned" &&
      config.orientation != "horizontal"
    sectionIndexEntries = enabled
      ? config.items.enumerated().compactMap { position, item in
          guard item.type == "sectionHeader" else { return nil }
          let title = item.data.string("indexTitle")
          return title.isEmpty ? nil : NativeListSectionIndexEntry(
            key: item.key,
            title: title,
            position: position
          )
        }
      : []
    sectionIndexHapticsEnabled = config.sectionIndexHapticsEnabled
    sectionIndexView.configure(
      titles: sectionIndexEntries.map(\.title),
      textColor: nativeListColor(config.theme, "secondaryText", "#646464"),
      activeColor: nativeListColor(config.theme, "accent", "#108303")
    )
    sectionIndexView.isHidden = sectionIndexEntries.isEmpty
    sectionIndexPreview.backgroundColor = nativeListColor(
      config.theme,
      "inverseBackground",
      "#202020"
    )
    sectionIndexPreview.textColor = nativeListColor(config.theme, "inverseText", "#FCFCFC")
    if let previousKey,
       let index = sectionIndexEntries.firstIndex(where: { $0.key == previousKey }) {
      sectionIndexView.setActiveIndex(index)
    } else {
      sectionIndexView.setActiveIndex(nil)
    }
    if sectionIndexEntries.isEmpty { finishSectionIndexInteraction(immediately: true) }
  }

  private func selectSectionIndex(_ index: Int, interacting: Bool) {
    guard let entry = sectionIndexEntries[safe: index] else { return }
    let changed = sectionIndexView.activeIndex != index
    sectionIndexScrubbing = interacting
    sectionIndexView.setActiveIndex(index)
    scrollToIndex(entry.position, animated: false, alignment: "start")
    if interacting {
      sectionIndexPreview.layer.removeAllAnimations()
      sectionIndexPreview.text = entry.title
      sectionIndexPreview.isHidden = false
      sectionIndexPreview.alpha = 1
      if changed && sectionIndexHapticsEnabled {
        sectionIndexFeedback.selectionChanged()
        sectionIndexFeedback.prepare()
      }
    }
  }

  private func finishSectionIndexInteraction(immediately: Bool = false) {
    sectionIndexScrubbing = false
    let hide: () -> Void = { [weak self] in
      guard let self else { return }
      self.sectionIndexPreview.alpha = 0
    }
    let completion: (Bool) -> Void = { [weak self] _ in
      guard let self else { return }
      self.sectionIndexPreview.isHidden = true
    }
    sectionIndexPreview.layer.removeAllAnimations()
    if immediately {
      hide()
      completion(true)
    } else {
      UIView.animate(withDuration: 0.15, animations: hide, completion: completion)
    }
  }

  private func syncSectionIndexToVisibleRows() {
    guard !sectionIndexScrubbing, !sectionIndexEntries.isEmpty else { return }
    let firstVisible = collectionView.indexPathsForVisibleItems.map(\.item).min() ?? 0
    let index = sectionIndexEntries.lastIndex { $0.position <= firstVisible }
    sectionIndexView.setActiveIndex(index)
  }

  private func configureRefresh(_ config: NativeListConfig) {
    if config.pullToRefresh {
      if collectionView.refreshControl == nil {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refreshTriggered), for: .valueChanged)
        collectionView.refreshControl = refreshControl
      }
      if config.refreshing { collectionView.refreshControl?.beginRefreshing() }
      else { collectionView.refreshControl?.endRefreshing() }
    } else {
      collectionView.refreshControl = nil
    }
  }

  private func configureFooter(_ config: NativeListConfig) {
    guard let footer = config.fixedFooter else {
      footerHeightConstraint.constant = 0
      footerCell.isHidden = true
      return
    }
    footerCell.isHidden = false
    footerHeightConstraint.constant = rowHeight(footer)
    bind(cell: footerCell, item: footer, itemIndex: nil)
  }

  private func bind(cell: NativeListCell, item: NativeListItem, itemIndex: Int? = nil) {
    guard let config else { return }
    cell.bind(
      item: item,
      theme: config.theme,
      layout: config.layout,
      itemIndex: itemIndex,
      selected: config.selectedKeys.contains(item.key),
      checkboxState: { [weak self] item, target, fallback in
        self?.resolveCheckboxState(item: item, target: target, fallback: fallback) ?? fallback
      }
    )
  }

  private func handleRowPress(_ item: NativeListItem) {
    guard let config, !item.data.bool("disabled") else { return }
    if config.rowPressToggles && item.isSelectable && config.selectionMode != "none" {
      updateSelection(target: NativeSelectionTarget(scope: "row", key: item.key), sourceKey: item.key)
      return
    }
    if item.type == "action" {
      emit(onRowAction, rowActionPayload(item: item, actionKey: item.data.string("actionKey")))
    } else if item.type == "system", item.data.string("variant") == "retry" {
      emit(onRowAction, rowActionPayload(item: item, actionKey: item.data.string("actionKey")))
    } else {
      emit(onRowAction, rowActionPayload(item: item, actionKey: "press"))
    }
  }

  private func handleAction(
    item: NativeListItem,
    actionKey: String,
    target: NativeSelectionTarget?
  ) {
    guard !item.data.bool("disabled") else { return }
    if let target, config?.selectionMode != "none" {
      updateSelection(target: target, sourceKey: item.key)
      return
    }
    emit(onRowAction, rowActionPayload(item: item, actionKey: actionKey))
  }

  private func updateSelection(target: NativeSelectionTarget, sourceKey: String) {
    guard var current = config, current.selectionMode != "none" else { return }
    let keys = selectionKeys(target: target, items: current.items)
    guard !keys.isEmpty else { return }
    let before = current.selectedKeys
    var after = before
    if current.selectionMode == "single" {
      let key = keys[0]
      after.removeAll()
      if !before.contains(key) { after.insert(key) }
    } else {
      let allSelected = keys.allSatisfy(before.contains)
      keys.forEach { if allSelected { after.remove($0) } else { after.insert($0) } }
    }
    guard before != after else { return }
    current.selectedKeys = after
    config = current
    refreshVisibleSelection()
    emit(onSelectionDelta, [
      "addedKeys": Array(after.subtracting(before)),
      "removedKeys": Array(before.subtracting(after)),
      "source": target.scope,
      "sourceKey": target.key ?? sourceKey,
    ])
  }

  private func selectionKeys(
    target: NativeSelectionTarget,
    items: [NativeListItem]
  ) -> [String] {
    switch target.scope {
    case "row": return items.filter { $0.key == target.key && $0.isSelectable }.map(\.key)
    case "section": return items.filter { $0.sectionKey == target.key && $0.isSelectable }.map(\.key)
    case "list": return items.filter(\.isSelectable).map(\.key)
    default: return []
    }
  }

  private func resolveCheckboxState(
    item: NativeListItem,
    target: NativeSelectionTarget?,
    fallback: String
  ) -> String {
    guard let config else { return fallback }
    let keys = selectionKeys(
      target: target ?? NativeSelectionTarget(scope: "row", key: item.key),
      items: config.items
    )
    guard !keys.isEmpty else { return fallback }
    let count = keys.filter(config.selectedKeys.contains).count
    if count == 0 { return "unchecked" }
    if count == keys.count { return "checked" }
    return "indeterminate"
  }

  private func refreshVisibleSelection() {
    guard let config else { return }
    let checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String = {
      [weak self] item, target, fallback in
      self?.resolveCheckboxState(item: item, target: target, fallback: fallback) ?? fallback
    }
    for indexPath in collectionView.indexPathsForVisibleItems {
      guard let item = config.items[safe: indexPath.item],
            let cell = collectionView.cellForItem(at: indexPath) as? NativeListCell else { continue }
      cell.updateSelection(
        item: item,
        selected: config.selectedKeys.contains(item.key),
        checkboxState: checkboxState
      )
    }
    if let footer = config.fixedFooter, !footerCell.isHidden {
      footerCell.updateSelection(
        item: footer,
        selected: config.selectedKeys.contains(footer.key),
        checkboxState: checkboxState
      )
    }
  }

  private func isControlledSelectionSnapshotUpdate(
    from current: NativeListConfig,
    to next: NativeListConfig
  ) -> Bool {
    guard current.generation == next.generation,
          current.layout == next.layout,
          current.orientation == next.orientation,
          current.gridColumns == next.gridColumns,
          current.stickyHeaders == next.stickyHeaders,
          current.contentPadding == next.contentPadding,
          current.contentPaddingHorizontal == next.contentPaddingHorizontal,
          current.contentPaddingTop == next.contentPaddingTop,
          current.contentPaddingBottom == next.contentPaddingBottom,
          current.itemSpacing == next.itemSpacing,
          current.selectionMode == next.selectionMode,
          current.rowPressToggles == next.rowPressToggles,
          current.reorderable == next.reorderable,
          current.pullToRefresh == next.pullToRefresh,
          current.refreshing == next.refreshing,
          current.loadMore == next.loadMore,
          current.endReachedThreshold == next.endReachedThreshold,
          current.sectionIndexEnabled == next.sectionIndexEnabled,
          current.sectionIndexHapticsEnabled == next.sectionIndexHapticsEnabled,
          dictionariesEqual(current.theme, next.theme),
          current.fixedFooter?.content == next.fixedFooter?.content,
          current.items.count == next.items.count else { return false }

    return zip(current.items, next.items).allSatisfy { old, new in
      guard old.key == new.key, old.type == new.type else { return false }
      if old.content == new.content { return true }
      guard old.type == "sectionHeader",
            old.data.string("variant") == "summary",
            new.data.string("variant") == "summary" else { return false }
      var oldData = old.data
      var newData = new.data
      oldData.removeValue(forKey: "title")
      oldData.removeValue(forKey: "value")
      newData.removeValue(forKey: "title")
      newData.removeValue(forKey: "value")
      return jsonData(oldData) == jsonData(newData)
    }
  }

  private func dictionariesEqual(_ lhs: [String: Any]?, _ rhs: [String: Any]?) -> Bool {
    switch (lhs, rhs) {
    case (nil, nil): return true
    case let (lhs?, rhs?): return jsonData(lhs) == jsonData(rhs)
    default: return false
    }
  }

  private func jsonData(_ value: [String: Any]) -> Data? {
    guard JSONSerialization.isValidJSONObject(value) else { return nil }
    return try? JSONSerialization.data(withJSONObject: value, options: [.sortedKeys])
  }

  private func rowHeight(_ item: NativeListItem) -> CGFloat {
    if item.type == "system", item.data.string("variant") == "spacer" {
      return CGFloat(item.data.int("height"))
    }
    if item.type == "identity", item.data.string("presentation") == "walletSidebar" {
      return 68
    }
    if item.type == "identity", item.data.string("presentation") == "networkSelector" {
      return 47
    }
    let base: CGFloat
    switch item.type {
    case "rail": base = 40
    case "activity": base = item.data.dictionaries("footerActions").isEmpty ? 60 : 100
    case "message": base = messageHeight(item)
    case "mediaTile": base = 244
    case "metricCard":
      base = item.data.string("variant") == "activity"
        ? 160 + 1 / UIScreen.main.scale
        : item.data.string("variant") == "performance" ? 178 : 132
    case "sectionHeader":
      let variant = item.data.string("variant")
      let isNetworkSelector = item.data.string("presentation") == "networkSelector"
      let isHistory = variant == "history" ||
        item.key.hasPrefix("history-") ||
        (item.sectionKey?.hasPrefix("history-") ?? false)
      base = config?.layout == "table"
        ? 28
        : isNetworkSelector
          ? 47
        : isHistory
          ? 16
          : variant == "summary"
            ? 68
            : variant == "gallery"
              ? 32
              : item.data.dictionary("checkbox") != nil
                ? 56
                : config?.layout == "linear" ? 30 : 36
    case "system":
      switch item.data.string("variant") {
      case "noMatch", "end": base = 36
      case "retry": base = 44
      default: base = 56
      }
    case "action":
      base = item.data.string("presentation") == "accountSelector"
        ? 48
        : item.data.dictionary("icon") == nil ? 44 : 60
    case "dataRow":
      base = item.data.dictionaries("columns").contains {
        !$0.string("secondaryText").isEmpty
      } ? 60 : 56
    default:
      if item.type == "identity", !item.data.string("tertiary").isEmpty {
        base = 72
      } else if item.type == "identity", !item.data.string("subtitle").isEmpty {
        base = 60
      } else {
        base = 56
      }
    }
    let hasFixedHeaderHeight = item.type == "sectionHeader" &&
      ["summary", "gallery"].contains(item.data.string("variant"))
    let modifier: CGFloat = hasFixedHeaderHeight
      ? 0
      : item.data.string("size", default: "medium") == "small"
        ? -8
        : item.data.string("size") == "large" ? 12 : 0
    let sectionSpacing: CGFloat = 0
    let tableAdjustment: CGFloat = config?.layout == "table" &&
      item.type == "dataRow" &&
      !item.data.dictionaries("columns").contains(where: { !$0.string("secondaryText").isEmpty })
      ? -8
      : 0
    return max(0, base + modifier + sectionSpacing + tableAdjustment)
  }

  private func messageHeight(_ item: NativeListItem) -> CGFloat {
    let maximumBodyLines = min(3, max(1, item.data.int("bodyLines", default: 3)))
    let horizontalInsets = (config?.contentPaddingHorizontal ?? 0) * 2 + 40
    let leadingWidth: CGFloat = item.data.dictionary("leading") == nil ? 0 : 40
    let thumbnailWidth: CGFloat = item.data.dictionary("thumbnail") == nil ? 0 : 76
    let textWidth = max(1, collectionView.bounds.width - horizontalInsets - leadingWidth - thumbnailWidth)
    let bodyBounds = (item.data.string("body") as NSString).boundingRect(
      with: CGSize(width: textWidth, height: .greatestFiniteMagnitude),
      options: [.usesLineFragmentOrigin, .usesFontLeading],
      attributes: [.font: nativeListFont(ofSize: 14)],
      context: nil
    )
    let titleBounds = (item.data.string("title") as NSString).boundingRect(
      with: CGSize(width: textWidth, height: .greatestFiniteMagnitude),
      options: [.usesLineFragmentOrigin, .usesFontLeading],
      attributes: [.font: nativeListFont(ofSize: 14, weight: .semibold)],
      context: nil
    )
    let bodyLines = min(maximumBodyLines, max(1, Int(ceil(bodyBounds.height / 20))))
    let titleLines = min(2, max(1, Int(ceil(titleBounds.height / 20))))
    return 32 + CGFloat(titleLines * 20 + bodyLines * 20) + 22
  }

  private func emit(_ block: ((String) -> Void)?, _ value: [String: Any]) {
    guard JSONSerialization.isValidJSONObject(value),
          let data = try? JSONSerialization.data(withJSONObject: value),
          let json = String(data: data, encoding: .utf8) else { return }
    block?(json)
  }

  private func rowActionPayload(item: NativeListItem, actionKey: String) -> [String: Any] {
    var payload: [String: Any] = ["rowKey": item.key, "actionKey": actionKey]
    if let sectionKey = item.sectionKey { payload["sectionKey"] = sectionKey }
    return payload
  }

  @objc private func refreshTriggered() {
    emit(onRowAction, ["actionKey": "nativeList.refresh"])
  }

  @objc private func footerPressed() {
    guard let footer = config?.fixedFooter else { return }
    handleRowPress(footer)
  }

  @objc private func footerHighlightChanged(_ recognizer: UILongPressGestureRecognizer) {
    switch recognizer.state {
    case .began, .changed:
      footerCell.setPressed(footerCell.bounds.contains(recognizer.location(in: footerCell)))
    default:
      footerCell.setPressed(false)
    }
  }

  private func emitVisibleRangeIfNeeded() {
    guard !visibleEventScheduled else { return }
    visibleEventScheduled = true
    DispatchQueue.main.async { [weak self] in
      guard let self else { return }
      self.visibleEventScheduled = false
      let indexes = self.collectionView.indexPathsForVisibleItems.map(\.item).sorted()
      let first = indexes.first ?? -1
      let last = indexes.last ?? -1
      let firstKey = self.config?.items[safe: first]?.key
      let lastKey = self.config?.items[safe: last]?.key
      guard self.lastVisibleRange?.first != first ||
              self.lastVisibleRange?.last != last ||
              self.lastVisibleRange?.firstKey != firstKey ||
              self.lastVisibleRange?.lastKey != lastKey else { return }
      self.lastVisibleRange = (first, last, firstKey, lastKey)
      var payload: [String: Any] = ["firstIndex": first, "lastIndex": last]
      if let firstKey { payload["firstKey"] = firstKey }
      if let lastKey { payload["lastKey"] = lastKey }
      self.emit(self.onVisibleRangeChanged, payload)
    }
  }

  private func checkEndReached() {
    guard let config,
          config.loadMore,
          endReachedGeneration != config.generation,
          !config.items.isEmpty else { return }
    let lastVisible = collectionView.indexPathsForVisibleItems.map(\.item).max() ?? -1
    let threshold = max(1, Int(ceil(Double(config.items.count) * config.endReachedThreshold)))
    guard lastVisible >= config.items.count - threshold else { return }
    endReachedGeneration = config.generation
    var payload: [String: Any] = ["generation": config.generation]
    if let lastKey = config.items.last?.key { payload["lastKey"] = lastKey }
    emit(onEndReached, payload)
  }
}

extension NativeListView: UIGestureRecognizerDelegate {
  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
    var view = touch.view
    while let current = view, current !== footerCell {
      if current is UIControl { return false }
      view = current.superview
    }
    return true
  }

  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
  ) -> Bool {
    gestureRecognizer.view === footerCell || otherGestureRecognizer.view === footerCell
  }
}

extension NativeListView: UICollectionViewDelegateFlowLayout {
  func collectionView(
    _ collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    guard let config, let item = config.items[safe: indexPath.item] else { return .zero }
    let insets = flowLayout.sectionInset
    if config.orientation == "horizontal" {
      let width: CGFloat = item.type == "rail" ? railWidth(item) : item.type == "mediaTile" ? 200 : 280
      let availableHeight = max(0, collectionView.bounds.height - insets.top - insets.bottom)
      let height = item.type == "rail" ? min(rowHeight(item), availableHeight) : availableHeight
      return CGSize(width: width, height: height)
    }
    let available = max(0, collectionView.bounds.width - insets.left - insets.right)
    let structural = item.type == "sectionHeader" || item.type == "system" || item.type == "action"
    if config.layout == "grid", !structural {
      let spacing = CGFloat(config.gridColumns - 1) * config.itemSpacing
      let width = floor((available - spacing) / CGFloat(config.gridColumns))
      let height = item.type == "mediaTile" ? width + 48 : rowHeight(item)
      return CGSize(width: width, height: height)
    }
    return CGSize(width: available, height: rowHeight(item))
  }

  private func railWidth(_ item: NativeListItem) -> CGFloat {
    let titleWidth = (item.data.string("title") as NSString).size(
      withAttributes: [.font: nativeListFont(ofSize: 12, weight: .medium)]
    ).width
    let badge = item.data.dictionary("badge")?.string("text") ?? ""
    let badgeWidth = (badge as NSString).size(
      withAttributes: [.font: nativeListTabularFont(ofSize: 12, weight: .medium)]
    ).width
    let status = item.data.string("status")
    let statusWidth = status.isEmpty || status == "none" ? 0 : (status as NSString).size(
      withAttributes: [.font: nativeListTabularFont(ofSize: 12)]
    ).width
    let visibleTextCount = 1 + (badgeWidth > 0 ? 1 : 0) + (statusWidth > 0 ? 1 : 0)
    let width = 4 + 20 + 6 + titleWidth + badgeWidth + statusWidth
      + CGFloat(max(0, visibleTextCount - 1)) * 6 + 4
    return min(288, max(72, ceil(width + 8)))
  }

  func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
    guard let item = config?.items[safe: indexPath.item] else { return }
    handleRowPress(item)
  }

  func collectionView(_ collectionView: UICollectionView, shouldHighlightItemAt indexPath: IndexPath) -> Bool {
    guard let item = config?.items[safe: indexPath.item] else { return false }
    return !item.data.bool("disabled")
  }

  func collectionView(_ collectionView: UICollectionView, shouldSelectItemAt indexPath: IndexPath) -> Bool {
    guard let item = config?.items[safe: indexPath.item] else { return false }
    return !item.data.bool("disabled")
  }

  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    syncSectionIndexToVisibleRows()
    emitVisibleRangeIfNeeded()
    checkEndReached()
  }
}

extension NativeListView: UICollectionViewDragDelegate, UICollectionViewDropDelegate {
  func collectionView(
    _ collectionView: UICollectionView,
    itemsForBeginning session: UIDragSession,
    at indexPath: IndexPath
  ) -> [UIDragItem] {
    guard config?.reorderable == true,
          let item = config?.items[safe: indexPath.item],
          item.isReorderable else { return [] }
    let dragItem = UIDragItem(itemProvider: NSItemProvider(object: item.key as NSString))
    dragItem.localObject = indexPath
    return [dragItem]
  }

  func collectionView(
    _ collectionView: UICollectionView,
    dropSessionDidUpdate session: UIDropSession,
    withDestinationIndexPath destinationIndexPath: IndexPath?
  ) -> UICollectionViewDropProposal {
    UICollectionViewDropProposal(operation: session.localDragSession == nil ? .forbidden : .move, intent: .insertAtDestinationIndexPath)
  }

  func collectionView(_ collectionView: UICollectionView, performDropWith coordinator: UICollectionViewDropCoordinator) {
    guard var current = config,
          let drop = coordinator.items.first,
          let source = drop.dragItem.localObject as? IndexPath else { return }
    let destination = coordinator.destinationIndexPath ?? IndexPath(item: max(0, current.items.count - 1), section: 0)
    guard current.items.indices.contains(source.item), current.items.indices.contains(destination.item) else { return }
    let sourceItem = current.items[source.item]
    let targetItem = current.items[destination.item]
    guard sourceItem.isReorderable, targetItem.isReorderable, sourceItem.sectionKey == targetItem.sectionKey else { return }
    current.items.remove(at: source.item)
    current.items.insert(sourceItem, at: destination.item)
    config = current
    itemsByKey = Dictionary(uniqueKeysWithValues: current.items.map { ($0.key, $0) })
    var snapshot = NSDiffableDataSourceSnapshot<Int, String>()
    snapshot.appendSections([0])
    snapshot.appendItems(current.items.map(\.key))
    dataSource.apply(snapshot, animatingDifferences: false)
    coordinator.drop(drop.dragItem, toItemAt: destination)
    var payload: [String: Any] = [
      "key": sourceItem.key,
      "fromIndex": source.item,
      "toIndex": destination.item,
    ]
    if let before = current.items[safe: destination.item - 1] { payload["beforeKey"] = before.key }
    if let after = current.items[safe: destination.item + 1] { payload["afterKey"] = after.key }
    emit(onReorder, payload)
  }
}

final class NativeListFlowLayout: UICollectionViewFlowLayout {
  var stickyItemIndexes: Set<Int> = []
  private var horizontalAttributes: [IndexPath: UICollectionViewLayoutAttributes] = [:]
  private var horizontalContentSize: CGSize = .zero

  override func prepare() {
    super.prepare()
    horizontalAttributes.removeAll(keepingCapacity: true)
    guard scrollDirection == .horizontal, let collectionView else { return }

    var nextX = sectionInset.left
    let itemCount = collectionView.numberOfItems(inSection: 0)
    for item in 0..<itemCount {
      let indexPath = IndexPath(item: item, section: 0)
      guard let attributes = super.layoutAttributesForItem(at: indexPath)?.copy()
        as? UICollectionViewLayoutAttributes else { continue }
      attributes.frame.origin = CGPoint(x: nextX, y: sectionInset.top)
      horizontalAttributes[indexPath] = attributes
      nextX = attributes.frame.maxX + minimumLineSpacing
    }
    let contentWidth = itemCount == 0
      ? sectionInset.left + sectionInset.right
      : nextX - minimumLineSpacing + sectionInset.right
    horizontalContentSize = CGSize(
      width: max(collectionView.bounds.width, contentWidth),
      height: collectionView.bounds.height
    )
  }

  override var collectionViewContentSize: CGSize {
    scrollDirection == .horizontal ? horizontalContentSize : super.collectionViewContentSize
  }

  override func shouldInvalidateLayout(forBoundsChange newBounds: CGRect) -> Bool {
    if scrollDirection == .horizontal, collectionView?.bounds.size != newBounds.size { return true }
    return !stickyItemIndexes.isEmpty || super.shouldInvalidateLayout(forBoundsChange: newBounds)
  }

  override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
    if scrollDirection == .horizontal {
      return horizontalAttributes.values
        .filter { $0.frame.intersects(rect) }
        .sorted { $0.indexPath.item < $1.indexPath.item }
    }
    guard scrollDirection == .vertical,
          !stickyItemIndexes.isEmpty,
          let collectionView else { return super.layoutAttributesForElements(in: rect) }
    let base = super.layoutAttributesForElements(in: rect)?.compactMap { $0.copy() as? UICollectionViewLayoutAttributes } ?? []
    let firstVisible = collectionView.indexPathsForVisibleItems.map(\.item).min() ?? 0
    guard let stickyIndex = stickyItemIndexes.filter({ $0 <= firstVisible }).max(),
          let original = super.layoutAttributesForItem(at: IndexPath(item: stickyIndex, section: 0)),
          let sticky = original.copy() as? UICollectionViewLayoutAttributes else { return base }
    let pinY = collectionView.contentOffset.y + collectionView.adjustedContentInset.top
    let nextHeaderY = stickyItemIndexes
      .filter { $0 > stickyIndex }
      .min()
      .flatMap { super.layoutAttributesForItem(at: IndexPath(item: $0, section: 0))?.frame.minY }
    sticky.frame.origin.y = min(max(pinY, original.frame.minY), (nextHeaderY ?? .greatestFiniteMagnitude) - sticky.frame.height)
    sticky.zIndex = 10_000
    let filtered = base.filter { $0.indexPath != sticky.indexPath }
    return filtered + [sticky]
  }

  override func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
    if scrollDirection == .horizontal { return horizontalAttributes[indexPath] }
    return super.layoutAttributesForItem(at: indexPath)
  }
}

private struct NativeListSectionIndexEntry {
  let key: String
  let title: String
  let position: Int
}

private final class NativeListSectionIndexView: UIControl {
  var onSelect: ((Int, Bool) -> Void)?
  var onInteractionEnded: (() -> Void)?
  private var titles: [String] = []
  private var labels: [UILabel] = []
  private var textColor: UIColor = .secondaryLabel
  private var activeColor: UIColor = .tintColor
  private var lastTouchIndex: Int?
  private(set) var activeIndex: Int?

  override init(frame: CGRect) {
    super.init(frame: frame)
    isAccessibilityElement = true
    accessibilityLabel = "Section index"
    accessibilityTraits = [.adjustable]
    isExclusiveTouch = true
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(titles: [String], textColor: UIColor, activeColor: UIColor) {
    self.titles = titles
    self.textColor = textColor
    self.activeColor = activeColor
    labels.forEach { $0.removeFromSuperview() }
    labels = titles.map { title in
      let label = UILabel()
      label.text = title
      label.textAlignment = .center
      label.adjustsFontForContentSizeCategory = true
      label.isAccessibilityElement = false
      addSubview(label)
      return label
    }
    activeIndex = nil
    accessibilityValue = nil
    setNeedsLayout()
    updateLabelStyles()
  }

  func setActiveIndex(_ index: Int?) {
    guard activeIndex != index else { return }
    activeIndex = index
    accessibilityValue = index.flatMap { titles[safe: $0] }
    updateLabelStyles()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    guard !labels.isEmpty else { return }
    let height = min(16, max(8, bounds.height / CGFloat(labels.count)))
    let originY = (bounds.height - height * CGFloat(labels.count)) / 2
    for (index, label) in labels.enumerated() {
      label.frame = CGRect(
        x: 0,
        y: originY + CGFloat(index) * height,
        width: bounds.width,
        height: height
      )
    }
  }

  override func beginTracking(_ touch: UITouch, with event: UIEvent?) -> Bool {
    guard !titles.isEmpty else { return false }
    lastTouchIndex = nil
    select(at: touch.location(in: self).y, interacting: true)
    return true
  }

  override func continueTracking(_ touch: UITouch, with event: UIEvent?) -> Bool {
    select(at: touch.location(in: self).y, interacting: true)
    return true
  }

  override func endTracking(_ touch: UITouch?, with event: UIEvent?) {
    if let touch { select(at: touch.location(in: self).y, interacting: true) }
    lastTouchIndex = nil
    onInteractionEnded?()
  }

  override func cancelTracking(with event: UIEvent?) {
    lastTouchIndex = nil
    onInteractionEnded?()
  }

  override func accessibilityIncrement() {
    guard !titles.isEmpty else { return }
    select(index: min(titles.count - 1, (activeIndex ?? -1) + 1), interacting: false)
  }

  override func accessibilityDecrement() {
    guard !titles.isEmpty else { return }
    select(index: max(0, (activeIndex ?? 1) - 1), interacting: false)
  }

  private func select(at y: CGFloat, interacting: Bool) {
    let height = min(16, max(8, bounds.height / CGFloat(titles.count)))
    let originY = (bounds.height - height * CGFloat(titles.count)) / 2
    let index = Int(floor((y - originY) / height)).clamped(to: 0...(titles.count - 1))
    if interacting && lastTouchIndex == index { return }
    lastTouchIndex = interacting ? index : nil
    select(index: index, interacting: interacting)
  }

  private func select(index: Int, interacting: Bool) {
    onSelect?(index, interacting)
    setActiveIndex(index)
  }

  private func updateLabelStyles() {
    for (index, label) in labels.enumerated() {
      let active = index == activeIndex
      label.textColor = active ? activeColor : textColor
      label.font = nativeListFont(ofSize: 10, weight: active ? .semibold : .medium)
    }
  }
}

private extension Comparable {
  func clamped(to range: ClosedRange<Self>) -> Self {
    min(max(self, range.lowerBound), range.upperBound)
  }
}

private extension Array {
  subscript(safe index: Int) -> Element? {
    indices.contains(index) ? self[index] : nil
  }
}
