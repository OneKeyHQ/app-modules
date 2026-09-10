import Foundation
import CoreFoundation
import UIKit
import UniformTypeIdentifiers

final class NativeListView: UIView {
  private final class ActionAnchorRecord {
    let token: String
    weak var sourceView: UIView?
    weak var ownerCell: NativeListCell?
    let bindingEpoch: Int
    var open = false
    var invalidatedReason: String?

    init(token: String, origin: NativeListActionOrigin) {
      self.token = token
      sourceView = origin.sourceView
      ownerCell = origin.ownerCell
      bindingEpoch = origin.bindingEpoch
    }
  }

  private enum ReorderAnimation {
    static let longPressDuration: TimeInterval = 0.2
    static let allowableMovement: CGFloat = 10
    static let duration: TimeInterval = 0.3
    static let damping: CGFloat = 25
    static let stiffness: CGFloat = 400
    static let mass: CGFloat = 0.4
    static let placeholderInset: CGFloat = 8
    static let placeholderRadius: CGFloat = 12

    static var timing: UISpringTimingParameters {
      UISpringTimingParameters(
        mass: mass,
        stiffness: stiffness,
        damping: damping,
        initialVelocity: .zero
      )
    }
  }

  private enum ScrollRequest {
    case key(String, Bool, String, Double, Double)
    case index(Int, Bool, String, Double, Double)
    case offset(Double, Bool)
    case end(Bool)
  }

  var onRowAction: ((String) -> Void)?
  var onActionAnchorInvalidated: ((String) -> Void)?
  var onSelectionDelta: ((String) -> Void)?
  var onReorder: ((String) -> Void)?
  var onEndReached: ((String) -> Void)?
  var onVisibleRangeChanged: ((String) -> Void)? {
    didSet {
      lastVisibleRange = nil
      if onVisibleRangeChanged != nil { emitVisibleRangeIfNeeded() }
    }
  }

  private let flowLayout = NativeListFlowLayout()
  private lazy var collectionView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout)
  private let footerContainer = UIView()
  private let footerCell = NativeListCell(frame: .zero)
  private let sectionIndexView = NativeListSectionIndexView()
  private let sectionIndexPreview = NativeListSectionIndexPreviewView()
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
  private var lastLayoutSize: CGSize?
  private var pendingScrollRequest: ScrollRequest?
  private let sectionIndexFeedback = UISelectionFeedbackGenerator()
  private lazy var reorderLongPress = UILongPressGestureRecognizer(
    target: self,
    action: #selector(reorderLongPressChanged(_:))
  )
  // OneKey patch: claim only vertical drags so held rows and ancestor pagers stay responsive.
  private lazy var listBodyGestureGuard = UIPanGestureRecognizer(target: nil, action: nil)
  private var interactiveReorderSource: (key: String, index: Int)?
  private weak var interactiveReorderCell: NativeListCell?
  private var interactiveReorderCompactKey: String?
  private var interactiveReorderUsesAtomicTargeting = false
  private var interactiveReorderTargetIndex: Int?
  private var interactiveReorderTransformedCells: [NativeListCell] = []
  private let interactiveReorderPlaceholder = UIView()
  private var interactiveReorderAnimator: UIViewPropertyAnimator?
  private let reorderStartFeedback = UIImpactFeedbackGenerator(style: .medium)
  private let reorderMoveFeedback = UISelectionFeedbackGenerator()
  private var interactiveReorderFeedbackIndex: Int?
  private var actionAnchor: ActionAnchorRecord?
  private let actionAnchorInstanceID = UUID().uuidString
  private var actionAnchorCounter = 0

  private static let sectionIndexContentInset: CGFloat = 16
  private static let sectionIndexRailWidth: CGFloat = 32
  private static let sectionIndexPreviewWidth: CGFloat = 60
  private static let sectionIndexPreviewHeight: CGFloat = 50
  private static let sectionIndexPreviewEndMargin: CGFloat = 40

  override init(frame: CGRect) {
    super.init(frame: frame)
    collectionView.register(NativeListCell.self, forCellWithReuseIdentifier: NativeListCell.reuseIdentifier)
    collectionView.backgroundColor = .clear
    collectionView.delegate = self
    collectionView.dragDelegate = self
    collectionView.dropDelegate = self
    collectionView.alwaysBounceVertical = true
    // OneKey patch: keep list-body drags from being claimed by an ancestor modal sheet.
    listBodyGestureGuard.cancelsTouchesInView = false
    listBodyGestureGuard.isEnabled = false
    listBodyGestureGuard.delegate = self
    collectionView.addGestureRecognizer(listBodyGestureGuard)
    reorderLongPress.minimumPressDuration = ReorderAnimation.longPressDuration
    reorderLongPress.allowableMovement = ReorderAnimation.allowableMovement
    reorderLongPress.delegate = self
    collectionView.addGestureRecognizer(reorderLongPress)
    interactiveReorderPlaceholder.isHidden = true
    interactiveReorderPlaceholder.isUserInteractionEnabled = false
    interactiveReorderPlaceholder.layer.cornerRadius = ReorderAnimation.placeholderRadius
    interactiveReorderPlaceholder.layer.cornerCurve = .continuous
    collectionView.insertSubview(interactiveReorderPlaceholder, at: 0)

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
      sectionIndexView.widthAnchor.constraint(equalToConstant: Self.sectionIndexRailWidth),
      sectionIndexPreview.trailingAnchor.constraint(
        equalTo: safeAreaLayoutGuide.trailingAnchor,
        constant: -Self.sectionIndexPreviewEndMargin
      ),
      sectionIndexPreview.centerYAnchor.constraint(equalTo: collectionView.centerYAnchor),
      sectionIndexPreview.widthAnchor.constraint(equalToConstant: Self.sectionIndexPreviewWidth),
      sectionIndexPreview.heightAnchor.constraint(equalToConstant: Self.sectionIndexPreviewHeight),
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
    sectionIndexPreview.isAccessibilityElement = false

    footerCell.onAction = { [weak self] item, action, target, origin in
      self?.handleAction(item: item, actionKey: action, target: target, origin: origin)
    }
    footerCell.onBindingInvalidated = { [weak self] cell, epoch in
      self?.handleBindingInvalidated(cell: cell, epoch: epoch)
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
      cell.onAction = { [weak self] item, action, target, origin in
        self?.handleAction(item: item, actionKey: action, target: target, origin: origin)
      }
      cell.onBindingInvalidated = { [weak self] cell, epoch in
        self?.handleBindingInvalidated(cell: cell, epoch: epoch)
      }
      self.bind(cell: cell, item: item, itemIndex: indexPath.item)
      return cell
    }
    dataSource.reorderingHandlers.canReorderItem = { [weak self] key in
      guard let self else { return false }
      return self.config?.reorderable == true && self.itemsByKey[key]?.isReorderable == true
    }
    dataSource.reorderingHandlers.didReorder = { [weak self] transaction in
      guard self?.interactiveReorderUsesAtomicTargeting != true else { return }
      self?.completeInteractiveReorder(transaction.finalSnapshot)
    }
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    let direction = effectiveUserInterfaceLayoutDirection
    if let lastLayoutSize,
       lastLayoutSize != bounds.size || lastLayoutDirection != nil && lastLayoutDirection != direction {
      invalidateActionAnchor(reason: "layout")
    }
    lastLayoutSize = bounds.size
    if lastLayoutDirection != direction, let config {
      configureLayout(config)
    }
    performPendingScrollIfNeeded()
  }

  func applySnapshotJson(_ json: String) {
    guard let next = try? NativeListConfig.parse(json: json) else { return }
    invalidateActionAnchor(reason: "snapshot")
    if let current = config, isControlledSelectionSnapshotUpdate(from: current, to: next) {
      let changedSummaryKeys = Set(zip(current.items, next.items).compactMap { old, new in
        old.content != new.content ? new.key : nil
      })
      config = next
      itemsByKey = Dictionary(uniqueKeysWithValues: next.items.map { ($0.key, $0) })
      refreshVisibleSelection(changedSummaryKeys: changedSummaryKeys)
      return
    }
    let oldItems = itemsByKey
    let themeChanged = !dictionariesEqual(config?.theme, next.theme)
    if config?.generation != next.generation { endReachedGeneration = nil }
    config = next
    itemsByKey = Dictionary(uniqueKeysWithValues: next.items.map { ($0.key, $0) })
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
      return themeChanged || old.revision != new.revision || old.content != new.content
    }
    snapshot.reconfigureItems(changedKeys)
    dataSource.apply(snapshot, animatingDifferences: false) { [weak self] in
      guard let self else { return }
      self.collectionView.layoutIfNeeded()
      self.performPendingScrollIfNeeded()
      self.emitVisibleRangeIfNeeded()
      self.checkEndReached()
      self.syncSectionIndexToVisibleRows()
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
    var sizeChanged = false
    for (index, changes) in pending {
      var merged = current.items[index].data
      changes.forEach { key, value in
        if key != "key" && key != "type" { merged[key] = value }
      }
      guard let item = try? NativeListItem(data: merged) else { return }
      if rowHeight(current.items[index]) != rowHeight(item) { sizeChanged = true }
      current.items[index] = item
      changedKeys.append(item.key)
      if let selected = changes["selected"] as? Bool {
        if selected { current.selectedKeys.insert(item.key) } else { current.selectedKeys.remove(item.key) }
      }
    }
    invalidateActionAnchor(reason: "snapshot")
    config = current
    itemsByKey = Dictionary(uniqueKeysWithValues: current.items.map { ($0.key, $0) })
    var snapshot = dataSource.snapshot()
    snapshot.reconfigureItems(changedKeys.filter { snapshot.indexOfItem($0) != nil })
    dataSource.apply(snapshot, animatingDifferences: false) { [weak self] in
      guard let self else { return }
      if sizeChanged { self.flowLayout.invalidateLayout() }
      self.collectionView.layoutIfNeeded()
      self.emitVisibleRangeIfNeeded()
      self.checkEndReached()
    }
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

  func scrollToKey(
    _ key: String,
    animated: Bool,
    alignment: String,
    viewPosition: Double = 0,
    viewOffset: Double = 0
  ) {
    requestScroll(.key(key, animated, alignment, viewPosition, viewOffset))
  }

  func scrollToIndex(
    _ index: Int,
    animated: Bool,
    alignment: String,
    viewPosition: Double = 0,
    viewOffset: Double = 0
  ) {
    requestScroll(.index(index, animated, alignment, viewPosition, viewOffset))
  }

  func scrollToOffset(_ offset: Double, animated: Bool) {
    requestScroll(.offset(offset, animated))
  }

  func scrollToEnd(animated: Bool) {
    requestScroll(.end(animated))
  }

  private func requestScroll(_ request: ScrollRequest) {
    if performScroll(request) {
      pendingScrollRequest = nil
    } else {
      pendingScrollRequest = request
    }
  }

  private func performPendingScrollIfNeeded() {
    guard let request = pendingScrollRequest, performScroll(request) else { return }
    pendingScrollRequest = nil
  }

  private func performScroll(_ request: ScrollRequest) -> Bool {
    guard let config else { return false }
    guard dataSource.snapshot().numberOfItems == config.items.count else { return false }
    let isHorizontal = config.orientation == "horizontal"
    let viewportLength = isHorizontal
      ? collectionView.bounds.width
      : collectionView.bounds.height
    guard viewportLength > 0 else { return false }
    collectionView.layoutIfNeeded()

    switch request {
    case let .key(key, animated, alignment, viewPosition, viewOffset):
      guard let index = config.items.firstIndex(where: { $0.key == key }) else { return true }
      return performIndexScroll(
        index,
        animated: animated,
        alignment: alignment,
        viewPosition: viewPosition,
        viewOffset: viewOffset
      )
    case let .index(index, animated, alignment, viewPosition, viewOffset):
      guard index >= 0, index < config.items.count else { return true }
      return performIndexScroll(
        index,
        animated: animated,
        alignment: alignment,
        viewPosition: viewPosition,
        viewOffset: viewOffset
      )
    case let .offset(offset, animated):
      let insets = collectionView.adjustedContentInset
      let startInset = isHorizontal ? insets.left : insets.top
      setScrollOffset(CGFloat(offset) - startInset, animated: animated)
      return true
    case let .end(animated):
      let insets = collectionView.adjustedContentInset
      let endInset = isHorizontal ? insets.right : insets.bottom
      let contentLength = isHorizontal
        ? collectionView.contentSize.width
        : collectionView.contentSize.height
      setScrollOffset(
        max(0, contentLength - viewportLength + endInset),
        animated: animated
      )
      return true
    }
  }

  private func performIndexScroll(
    _ index: Int,
    animated: Bool,
    alignment: String,
    viewPosition: Double,
    viewOffset: Double
  ) -> Bool {
    let indexPath = IndexPath(item: index, section: 0)
    guard let attributes = collectionView.collectionViewLayout
      .layoutAttributesForItem(at: indexPath) else { return false }
    let isHorizontal = config?.orientation == "horizontal"
    let insets = collectionView.adjustedContentInset
    let viewportLength = isHorizontal
      ? collectionView.bounds.width - insets.left - insets.right
      : collectionView.bounds.height - insets.top - insets.bottom
    let itemStart = isHorizontal ? attributes.frame.minX : attributes.frame.minY
    let itemLength = isHorizontal ? attributes.frame.width : attributes.frame.height
    let currentOffset = isHorizontal
      ? collectionView.contentOffset.x
      : collectionView.contentOffset.y
    let startInset = isHorizontal ? insets.left : insets.top
    let visibleStart = currentOffset + startInset
    let visibleEnd = visibleStart + viewportLength
    let resolvedPosition: CGFloat
    if alignment == "nearest" {
      if itemStart < visibleStart {
        resolvedPosition = 0
      } else if itemStart + itemLength > visibleEnd {
        resolvedPosition = 1
      } else {
        return true
      }
    } else {
      resolvedPosition = CGFloat(min(1, max(0, viewPosition)))
    }
    let target = itemStart
      - startInset
      - resolvedPosition * max(0, viewportLength - itemLength)
      - CGFloat(viewOffset)
    setScrollOffset(target, animated: animated)
    return true
  }

  private func setScrollOffset(_ requestedOffset: CGFloat, animated: Bool) {
    let isHorizontal = config?.orientation == "horizontal"
    let insets = collectionView.adjustedContentInset
    let viewportLength = isHorizontal ? collectionView.bounds.width : collectionView.bounds.height
    let contentLength = isHorizontal
      ? collectionView.contentSize.width
      : collectionView.contentSize.height
    let minimum = -(isHorizontal ? insets.left : insets.top)
    let maximum = max(
      minimum,
      contentLength - viewportLength + (isHorizontal ? insets.right : insets.bottom)
    )
    let offset = min(maximum, max(minimum, requestedOffset))
    let point = isHorizontal
      ? CGPoint(x: offset, y: collectionView.contentOffset.y)
      : CGPoint(x: collectionView.contentOffset.x, y: offset)
    collectionView.setContentOffset(point, animated: animated)
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
    // OneKey patch: the section index overlays rows and keeps only an accessory-safe inset.
    let indexGutter = sectionIndexEntries.isEmpty ? 0 : Self.sectionIndexContentInset
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
          $0.element.type == "sectionHeader" && $0.element.data.bool("sticky", default: true) && $0.element.data.string("variant") != "summary"
            ? $0.offset
            : nil
        })
      : []
    flowLayout.invalidateLayout()
    collectionView.alwaysBounceHorizontal = isHorizontal
    collectionView.alwaysBounceVertical = !isHorizontal
    listBodyGestureGuard.isEnabled = !isHorizontal
    collectionView.showsVerticalScrollIndicator = sectionIndexEntries.isEmpty
    collectionView.dragInteractionEnabled = false
    lastLayoutDirection = effectiveUserInterfaceLayoutDirection
  }

  @objc private func reorderLongPressChanged(_ gesture: UILongPressGestureRecognizer) {
    switch gesture.state {
    case .began:
      let point = gesture.location(in: collectionView)
      guard let current = config,
            current.reorderable,
            let indexPath = collectionView.indexPathForItem(at: point),
            let item = item(at: indexPath),
            item.isReorderable,
            let cell = collectionView.cellForItem(at: indexPath) as? NativeListCell else {
        interactiveReorderSource = nil
        interactiveReorderCell = nil
        return
      }
      // OneKey patch: a hidden wallet child starts dragging its whole logical group.
      // if item.type == "walletGroup", gesture.location(in: cell).y > 68 {
      // interactiveReorderSource = nil
      // interactiveReorderCell = nil
      // return
      // }
      interactiveReorderSource = (item.key, indexPath.item)
      interactiveReorderCell = cell
      interactiveReorderUsesAtomicTargeting = item.type == "identity" &&
        item.data.string("presentation") == "walletSidebar" &&
        current.items.contains { $0.type == "walletGroup" }
      interactiveReorderTargetIndex = indexPath.item
      if item.type == "walletGroup" {
        interactiveReorderCompactKey = item.key
        cell.setWalletGroupReorderCompact(true)
        flowLayout.invalidateLayout()
        collectionView.layoutIfNeeded()
      }
      cell.setPressed(true)
      guard collectionView.beginInteractiveMovementForItem(at: indexPath) else {
        interactiveReorderCompactKey = nil
        cell.setWalletGroupReorderCompact(false)
        cell.setPressed(false)
        interactiveReorderSource = nil
        interactiveReorderCell = nil
        interactiveReorderUsesAtomicTargeting = false
        interactiveReorderTargetIndex = nil
        interactiveReorderFeedbackIndex = nil
        flowLayout.invalidateLayout()
        collectionView.layoutIfNeeded()
        return
      }
      interactiveReorderFeedbackIndex = indexPath.item
      reorderStartFeedback.impactOccurred(intensity: 0.7)
      reorderStartFeedback.prepare()
      reorderMoveFeedback.prepare()
      showInteractiveReorderPlaceholder(at: indexPath, item: item, config: current)
    case .changed:
      guard interactiveReorderSource != nil else { return }
      let point = gesture.location(in: collectionView)
      collectionView.updateInteractiveMovementTargetPosition(point)
      if let indexPath = nearestReorderIndexPath(to: point),
         let item = item(at: indexPath) {
        let targetChanged = interactiveReorderFeedbackIndex != indexPath.item
        if interactiveReorderUsesAtomicTargeting {
          updateAtomicReorderTarget(to: indexPath.item, animated: targetChanged)
        }
        if targetChanged {
          interactiveReorderFeedbackIndex = indexPath.item
          reorderMoveFeedback.selectionChanged()
          reorderMoveFeedback.prepare()
          showInteractiveReorderPlaceholder(at: indexPath, item: item, config: config)
        }
      }
    case .ended:
      guard interactiveReorderSource != nil else { return }
      interactiveReorderCell?.setPressed(false)
      finishInteractiveReorder(cancelled: false)
    case .cancelled, .failed:
      interactiveReorderCell?.setPressed(false)
      finishInteractiveReorder(cancelled: true)
    default:
      break
    }
  }

  private func nearestReorderIndexPath(to point: CGPoint) -> IndexPath? {
    collectionView.indexPathsForVisibleItems.min { lhs, rhs in
      let lhsFrame = flowLayout.layoutAttributesForItem(at: lhs)?.frame ?? .zero
      let rhsFrame = flowLayout.layoutAttributesForItem(at: rhs)?.frame ?? .zero
      return abs(lhsFrame.midY - point.y) < abs(rhsFrame.midY - point.y)
    }
  }

  private func showInteractiveReorderPlaceholder(
    at indexPath: IndexPath,
    item: NativeListItem,
    config: NativeListConfig?
  ) {
    guard (
      item.type == "walletGroup" ||
        (item.type == "identity" && item.data.string("presentation") == "walletSidebar")
    ),
          var frame = flowLayout.layoutAttributesForItem(at: indexPath)?.frame else { return }
    let targetMaxY = frame.maxY
    frame.origin.x += ReorderAnimation.placeholderInset
    frame.size.width = max(0, frame.width - ReorderAnimation.placeholderInset * 2)
    frame.size.height = 68
    if interactiveReorderUsesAtomicTargeting,
       item.type == "walletGroup",
       let source = interactiveReorderSource,
       indexPath.item > source.index {
      frame.origin.y = targetMaxY - frame.height
    }
    interactiveReorderPlaceholder.backgroundColor = nativeListColor(
      config?.theme,
      "rowPressedBackground",
      "#00000017"
    )
    interactiveReorderAnimator?.stopAnimation(true)
    if interactiveReorderPlaceholder.isHidden {
      interactiveReorderPlaceholder.frame = frame
      interactiveReorderPlaceholder.alpha = 1
      interactiveReorderPlaceholder.isHidden = false
      return
    }
    let animator = UIViewPropertyAnimator(
      duration: ReorderAnimation.duration,
      timingParameters: ReorderAnimation.timing
    )
    animator.addAnimations { [weak self] in self?.interactiveReorderPlaceholder.frame = frame }
    interactiveReorderAnimator = animator
    animator.startAnimation()
  }

  private func finishInteractiveReorder(cancelled: Bool) {
    if interactiveReorderUsesAtomicTargeting {
      finishAtomicInteractiveReorder(cancelled: cancelled)
      return
    }
    interactiveReorderAnimator?.stopAnimation(true)
    interactiveReorderFeedbackIndex = nil
    if cancelled {
      collectionView.cancelInteractiveMovement()
    } else {
      collectionView.endInteractiveMovement()
    }
    let animator = UIViewPropertyAnimator(
      duration: ReorderAnimation.duration,
      timingParameters: ReorderAnimation.timing
    )
    animator.addAnimations { [weak self] in
      guard let self else { return }
      self.collectionView.layoutIfNeeded()
      self.interactiveReorderPlaceholder.alpha = 0
    }
    animator.addCompletion { [weak self] _ in
      guard let self else { return }
      self.interactiveReorderPlaceholder.isHidden = true
      self.interactiveReorderPlaceholder.alpha = 1
      self.interactiveReorderAnimator = nil
      if self.interactiveReorderCompactKey != nil {
        self.expandWalletGroupAfterInteractiveReorder()
      } else {
        self.interactiveReorderSource = nil
        self.interactiveReorderCell = nil
      }
    }
    interactiveReorderAnimator = animator
    animator.startAnimation()
  }

  private func completeInteractiveReorder(
    _ snapshot: NSDiffableDataSourceSnapshot<Int, String>
  ) {
    guard var current = config,
          let source = interactiveReorderSource else { return }
    defer {
      interactiveReorderSource = nil
      interactiveReorderCell = nil
    }
    let keys = snapshot.itemIdentifiers
    let items = keys.compactMap { itemsByKey[$0] }
    guard keys.count == current.items.count,
          let toIndex = keys.firstIndex(of: source.key),
          items.count == keys.count else { return }
    current.items = items
    config = current
    guard source.index != toIndex else { return }
    var payload: [String: Any] = [
      "key": source.key,
      "fromIndex": source.index,
      "toIndex": toIndex,
    ]
    if let before = items[safe: toIndex - 1] { payload["beforeKey"] = before.key }
    if let after = items[safe: toIndex + 1] { payload["afterKey"] = after.key }
    emit(onReorder, payload)
  }

  private func expandWalletGroupAfterInteractiveReorder() {
    guard let key = interactiveReorderCompactKey else { return }
    collectionView.layoutIfNeeded()
    walletGroupCell(for: key)?.prepareWalletGroupReorderExpansion()
    interactiveReorderCompactKey = nil
    flowLayout.invalidateLayout()
    let animator = UIViewPropertyAnimator(
      duration: ReorderAnimation.duration,
      timingParameters: ReorderAnimation.timing
    )
    animator.addAnimations { [weak self] in
      guard let self else { return }
      self.collectionView.layoutIfNeeded()
      self.walletGroupCell(for: key)?.animateWalletGroupReorderExpansion()
    }
    animator.addCompletion { [weak self] _ in
      guard let self else { return }
      self.walletGroupCell(for: key)?.finishWalletGroupReorderExpansion()
      self.interactiveReorderSource = nil
      self.interactiveReorderCell = nil
    }
    interactiveReorderAnimator = animator
    animator.startAnimation()
  }

  private func walletGroupCell(for key: String) -> NativeListCell? {
    guard let index = dataSource.snapshot().indexOfItem(key) else { return nil }
    return collectionView.cellForItem(at: IndexPath(item: index, section: 0)) as? NativeListCell
  }

  private func item(at indexPath: IndexPath) -> NativeListItem? {
    if let key = dataSource.itemIdentifier(for: indexPath), let item = itemsByKey[key] {
      return item
    }
    return config?.items[safe: indexPath.item]
  }

  private func updateAtomicReorderTarget(to targetIndex: Int, animated: Bool) {
    guard let source = interactiveReorderSource else { return }
    interactiveReorderTargetIndex = targetIndex
    let distance = CGFloat(68) + flowLayout.minimumLineSpacing
    let updates = { [weak self] in
      guard let self else { return }
      for case let cell as NativeListCell in self.collectionView.visibleCells {
        guard let indexPath = self.collectionView.indexPath(for: cell),
              indexPath.item != source.index else { continue }
        let offset: CGFloat
        if targetIndex < source.index,
           indexPath.item >= targetIndex,
           indexPath.item < source.index {
          offset = distance
        } else if targetIndex > source.index,
                  indexPath.item > source.index,
                  indexPath.item <= targetIndex {
          offset = -distance
        } else {
          offset = 0
        }
        if offset != 0,
           !self.interactiveReorderTransformedCells.contains(where: { $0 === cell }) {
          self.interactiveReorderTransformedCells.append(cell)
        }
        cell.transform = CGAffineTransform(translationX: 0, y: offset)
      }
    }
    if animated {
      UIView.animate(
        withDuration: ReorderAnimation.duration,
        delay: 0,
        options: [.allowUserInteraction, .beginFromCurrentState, .curveEaseInOut],
        animations: updates
      )
    } else {
      UIView.performWithoutAnimation(updates)
    }
  }

  private func finishAtomicInteractiveReorder(cancelled: Bool) {
    interactiveReorderAnimator?.stopAnimation(true)
    interactiveReorderFeedbackIndex = nil
    guard let source = interactiveReorderSource else { return }
    let targetIndex = interactiveReorderTargetIndex ?? source.index
    collectionView.cancelInteractiveMovement()

    if !cancelled, targetIndex != source.index {
      var snapshot = dataSource.snapshot()
      let keys = snapshot.itemIdentifiers
      if targetIndex < source.index, let targetKey = keys[safe: targetIndex] {
        snapshot.moveItem(source.key, beforeItem: targetKey)
      } else if let targetKey = keys[safe: targetIndex] {
        snapshot.moveItem(source.key, afterItem: targetKey)
      }
      dataSource.apply(snapshot, animatingDifferences: false) { [weak self] in
        guard let self else { return }
        self.resetAtomicReorderTransforms()
        self.collectionView.layoutIfNeeded()
        self.completeInteractiveReorder(snapshot)
        self.clearAtomicInteractiveReorderState()
      }
    } else {
      resetAtomicReorderTransforms()
      interactiveReorderSource = nil
      interactiveReorderCell = nil
      clearAtomicInteractiveReorderState()
    }
  }

  private func clearAtomicInteractiveReorderState() {
    interactiveReorderUsesAtomicTargeting = false
    interactiveReorderTargetIndex = nil
    interactiveReorderPlaceholder.isHidden = true
    interactiveReorderPlaceholder.alpha = 1
  }

  private func resetAtomicReorderTransforms() {
    UIView.performWithoutAnimation {
      for cell in interactiveReorderTransformedCells {
        cell.transform = .identity
      }
      for cell in collectionView.visibleCells {
        cell.transform = .identity
      }
      interactiveReorderTransformedCells.removeAll()
      collectionView.layoutIfNeeded()
    }
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
      textColor: nativeListColor(config.theme, "disabledText", "#8D8D8D"),
      activeColor: nativeListColor(config.theme, "positive", "#218358"),
      activeTextColor: nativeListColor(config.theme, "inverseText", "#FCFCFC"),
      centeredInWindow: config.sectionIndexCenteredInWindow
    )
    sectionIndexView.isHidden = sectionIndexEntries.isEmpty
    sectionIndexPreview.configure(
      fillColor: UIColor(
        red: 202.0 / 255.0,
        green: 202.0 / 255.0,
        blue: 202.0 / 255.0,
        alpha: 1
      ),
      textColor: .white
    )
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
      positionSectionIndexPreview(at: sectionIndexView.centerY(for: index))
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

  private func positionSectionIndexPreview(at sectionIndexY: CGFloat) {
    layoutIfNeeded()
    let targetY = sectionIndexView.convert(
      CGPoint(x: sectionIndexView.bounds.midX, y: sectionIndexY),
      to: self
    ).y
    let safeFrame = safeAreaLayoutGuide.layoutFrame
    let halfHeight = Self.sectionIndexPreviewHeight / 2
    let minimumY = safeFrame.minY + halfHeight
    let maximumY = max(minimumY, safeFrame.maxY - halfHeight)
    let clampedY = targetY.clamped(to: minimumY...maximumY)
    sectionIndexPreview.transform = CGAffineTransform(
      translationX: 0,
      y: clampedY - sectionIndexPreview.center.y
    )
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
      selected: item.data.bool("selected") || config.selectedKeys.contains(item.key),
      checkboxState: { [weak self] item, target, fallback in
        self?.resolveCheckboxState(item: item, target: target, fallback: fallback) ?? fallback
      }
    )
    if item.key == interactiveReorderCompactKey {
      cell.setWalletGroupReorderCompact(true)
    }
  }

  private func handleRowPress(_ item: NativeListItem, origin: NativeListActionOrigin?) {
    // OneKey patch: missing-address rows keep accessory actions available.
    guard let config, !item.data.bool("disabled"), !item.data.bool("pressDisabled") else { return }
    if config.rowPressToggles && item.isSelectable && config.selectionMode != "none" {
      updateSelection(target: NativeSelectionTarget(scope: "row", key: item.key), sourceKey: item.key)
      return
    }
    if item.type == "action" {
      emit(onRowAction, rowActionPayload(item: item, actionKey: item.data.string("actionKey"), origin: origin))
    } else if item.type == "system", item.data.string("variant") == "retry" {
      emit(onRowAction, rowActionPayload(item: item, actionKey: item.data.string("actionKey"), origin: origin))
    } else {
      emit(onRowAction, rowActionPayload(item: item, actionKey: "press", origin: origin))
    }
  }

  private func handleAction(
    item: NativeListItem,
    actionKey: String,
    target: NativeSelectionTarget?,
    origin: NativeListActionOrigin?
  ) {
    guard !item.data.bool("disabled") else { return }
    if let target, config?.selectionMode != "none" {
      updateSelection(target: target, sourceKey: item.key)
      return
    }
    emit(onRowAction, rowActionPayload(item: item, actionKey: actionKey, origin: origin))
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

  private func refreshVisibleSelection(changedSummaryKeys: Set<String> = []) {
    guard let config else { return }
    let checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String = {
      [weak self] item, target, fallback in
      self?.resolveCheckboxState(item: item, target: target, fallback: fallback) ?? fallback
    }
    for indexPath in collectionView.indexPathsForVisibleItems {
      guard let item = config.items[safe: indexPath.item],
            let cell = collectionView.cellForItem(at: indexPath) as? NativeListCell else { continue }
      if changedSummaryKeys.contains(item.key) {
        bind(cell: cell, item: item, itemIndex: indexPath.item)
        continue
      }
      cell.updateSelection(
        item: item,
        selected: item.data.bool("selected") || config.selectedKeys.contains(item.key),
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
          current.sectionIndexCenteredInWindow == next.sectionIndexCenteredInWindow,
          dictionariesEqual(current.theme, next.theme),
          current.fixedFooter?.content == next.fixedFooter?.content,
          current.items.count == next.items.count else { return false }

    return zip(current.items, next.items).allSatisfy { old, new in
      guard old.key == new.key, old.type == new.type else { return false }
      if old.content == new.content { return true }
      // OneKey patch: controlled echoes can also update row and checkbox selection fields.
      // guard old.type == "sectionHeader",
      //       old.data.string("variant") == "summary",
      //       new.data.string("variant") == "summary" else { return false }
      // var oldData = old.data
      // var newData = new.data
      // oldData.removeValue(forKey: "title")
      // oldData.removeValue(forKey: "value")
      // newData.removeValue(forKey: "title")
      // newData.removeValue(forKey: "value")
      let controlled = next.selectionMode == "single" || next.selectionMode == "multiple"
      guard let oldData = selectionComparisonData(old.data, controlled: controlled),
            let newData = selectionComparisonData(new.data, controlled: controlled) else { return false }
      return jsonData(oldData) == jsonData(newData)
    }
  }

  // OneKey patch: remove only fields that the existing lightweight binder refreshes.
  private func selectionComparisonData(_ data: [String: Any], controlled: Bool) -> [String: Any]? {
    guard let type = data["type"] as? String else { return nil }
    var result = data
    if let selected = result["selected"] {
      guard CFGetTypeID(selected as CFTypeRef) == CFBooleanGetTypeID() else { return nil }
      result.removeValue(forKey: "selected")
    }
    if type == "walletGroup" {
      guard let parent = data["parent"] as? [String: Any], parent["type"] as? String == "identity",
            let children = data["children"] as? [[String: Any]],
            let normalizedParent = selectionComparisonData(parent, controlled: false) else { return nil }
      var normalizedChildren: [[String: Any]] = []
      for child in children {
        guard child["type"] as? String == "identity",
              let normalized = selectionComparisonData(child, controlled: false) else { return nil }
        normalizedChildren.append(normalized)
      }
      result["parent"] = normalizedParent
      result["children"] = normalizedChildren
    }
    if type == "sectionHeader", data["variant"] as? String == "summary" {
      result.removeValue(forKey: "title")
      result.removeValue(forKey: "value")
    }
    guard controlled else { return result }
    func checkboxData(_ value: Any) -> [String: Any]? {
      guard var checkbox = value as? [String: Any], checkbox["kind"] as? String == "checkbox" else { return nil }
      if let state = checkbox["state"] {
        guard let state = state as? String, ["checked", "unchecked", "indeterminate"].contains(state) else { return nil }
      }
      checkbox.removeValue(forKey: "state")
      return checkbox
    }
    if ["dataRow", "sectionHeader", "action"].contains(type), let checkbox = result["checkbox"] {
      guard let normalized = checkboxData(checkbox) else { return nil }
      result["checkbox"] = normalized
    }
    if type == "identity", let trailing = result["trailing"] {
      guard var accessories = trailing as? [[String: Any]], accessories.count <= 2,
            accessories.filter({ $0["kind"] as? String == "checkbox" }).count <= 1 else { return nil }
      for index in accessories.indices where accessories[index]["kind"] as? String == "checkbox" {
        guard let normalized = checkboxData(accessories[index]) else { return nil }
        accessories[index] = normalized
      }
      result["trailing"] = accessories
    }
    return result
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
    // OneKey patch: honor selector baseline geometry; keep compact drag sizing.
    if item.type != "walletGroup", item.data["height"] != nil { return CGFloat(item.data.double("height")) }
    if item.type == "system", item.data.string("variant") == "spacer" {
      return CGFloat(item.data.int("height"))
    }
    // OneKey patch: warning height follows the current native font and available width.
    if item.type == "system", item.data.string("variant") == "warning" {
      let textWidth = max(1, collectionView.bounds.width - (config?.contentPaddingHorizontal ?? 0) * 2 - 24)
      func textHeight(_ key: String, weight: NativeListFontWeight) -> CGFloat {
        let paragraph = NSMutableParagraphStyle()
        paragraph.minimumLineHeight = 20
        paragraph.maximumLineHeight = 20
        return ceil((item.data.string(key) as NSString).boundingRect(with: CGSize(width: textWidth, height: .greatestFiniteMagnitude), options: [.usesLineFragmentOrigin, .usesFontLeading], attributes: [.font: nativeListFont(ofSize: 14, weight: weight), .paragraphStyle: paragraph], context: nil).height / 20) * 20
      }
      return 32 + textHeight("title", weight: .medium) + textHeight("message", weight: .regular)
    }
    if item.type == "walletGroup" {
      if item.key == interactiveReorderCompactKey { return 68 }
      let childCount = item.data.dictionaries("children").count
      // OneKey patch: wallet badges contribute their own member heights.
      // return CGFloat((childCount + 1) * 68 + childCount * 12)
      let members = [item.data.dictionary("parent")].compactMap { $0 } + item.data.dictionaries("children")
      return members.reduce(CGFloat(childCount * 12 + (members.first?["height"] != nil ? 2 : 0))) { total, data in
        total + CGFloat(data.double("height", default: data.dictionaries("badges").isEmpty ? 68 : 92))
      }
    }
    if item.type == "identity", item.data.string("presentation") == "walletSidebar" {
      // OneKey patch: default sidebar badge geometry is 24 points taller.
      // return 68
      return item.data.dictionaries("badges").isEmpty ? 68 : 92
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

  private func rowActionPayload(
    item: NativeListItem,
    actionKey: String,
    origin: NativeListActionOrigin? = nil
  ) -> [String: Any] {
    var payload: [String: Any] = ["rowKey": item.key, "actionKey": actionKey]
    if let sectionKey = item.sectionKey { payload["sectionKey"] = sectionKey }
    if let origin, let anchor = createActionAnchor(origin: origin) { payload["anchor"] = anchor }
    return payload
  }

  func setActionAnchorStateJson(_ json: String) {
    guard let bytes = json.data(using: .utf8),
          let state = try? JSONSerialization.jsonObject(with: bytes) as? [String: Any],
          let token = state["token"] as? String,
          let open = state["open"] as? Bool,
          let anchor = actionAnchor,
          anchor.token == token else { return }
    if !open {
      if state.bool("restoreFocus"), isActionAnchorValid(anchor), let sourceView = anchor.sourceView {
        UIAccessibility.post(notification: .layoutChanged, argument: sourceView)
      }
      actionAnchor = nil
      return
    }
    if let reason = anchor.invalidatedReason {
      emitActionAnchorInvalidated(anchor: anchor, reason: reason)
    } else if isActionAnchorValid(anchor) {
      anchor.open = true
    } else {
      emitActionAnchorInvalidated(anchor: anchor, reason: "rebind")
    }
  }

  func disposeActionAnchor() {
    invalidateActionAnchor(reason: "destroy")
    actionAnchor = nil
  }

  private func createActionAnchor(origin: NativeListActionOrigin) -> [String: Any]? {
    guard let sourceView = origin.sourceView,
          let ownerCell = origin.ownerCell,
          ownerCell.bindingEpoch == origin.bindingEpoch,
          sourceView.window != nil,
          sourceView === ownerCell.contentView || sourceView.isDescendant(of: ownerCell.contentView) else { return nil }
    invalidateActionAnchor(reason: "rebind")
    actionAnchorCounter &+= 1
    let generation = config?.generation ?? 0
    let token = "\(actionAnchorInstanceID):\(generation):\(actionAnchorCounter):\(origin.bindingEpoch)"
    let rect = sourceView.convert(sourceView.bounds, to: window).insetBy(dx: origin.anchorInset, dy: origin.anchorInset)
    let record = ActionAnchorRecord(token: token, origin: origin)
    actionAnchor = record
    var anchor: [String: Any] = [
      "token": token,
      "windowRect": [
        "x": rect.minX,
        "y": rect.minY,
        "width": rect.width,
        "height": rect.height,
      ],
      "source": origin.source,
      "generation": generation,
      "layoutDirection": sourceView.effectiveUserInterfaceLayoutDirection == .rightToLeft
        ? "rtl"
        : "ltr",
    ]
    if let slot = origin.slot { anchor["slot"] = slot }
    return anchor
  }

  private func isActionAnchorValid(_ anchor: ActionAnchorRecord) -> Bool {
    guard anchor.invalidatedReason == nil,
          let sourceView = anchor.sourceView,
          let ownerCell = anchor.ownerCell else { return false }
    return ownerCell.bindingEpoch == anchor.bindingEpoch
      && sourceView.window != nil
      && (sourceView === ownerCell.contentView || sourceView.isDescendant(of: ownerCell.contentView))
  }

  private func handleBindingInvalidated(cell: NativeListCell, epoch: Int) {
    guard let anchor = actionAnchor,
          anchor.ownerCell === cell,
          anchor.bindingEpoch == epoch else { return }
    invalidateActionAnchor(reason: "rebind")
  }

  private func invalidateActionAnchor(reason: String) {
    guard let anchor = actionAnchor, anchor.invalidatedReason == nil else { return }
    anchor.invalidatedReason = reason
    if anchor.open { emitActionAnchorInvalidated(anchor: anchor, reason: reason) }
  }

  private func emitActionAnchorInvalidated(anchor: ActionAnchorRecord, reason: String) {
    guard actionAnchor === anchor else { return }
    actionAnchor = nil
    emit(onActionAnchorInvalidated, ["token": anchor.token, "reason": reason])
  }

  @objc private func refreshTriggered() {
    emit(onRowAction, ["actionKey": "nativeList.refresh"])
  }

  @objc private func footerPressed() {
    guard let footer = config?.fixedFooter else { return }
    handleRowPress(footer, origin: footerCell.rowActionOrigin())
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
      guard let onVisibleRangeChanged = self.onVisibleRangeChanged else { return }
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
      self.emit(onVisibleRangeChanged, payload)
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

  override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    guard gestureRecognizer === listBodyGestureGuard,
          let pan = gestureRecognizer as? UIPanGestureRecognizer else { return true }
    let velocity = pan.velocity(in: collectionView)
    return abs(velocity.y) > abs(velocity.x)
  }
}

extension NativeListView: UIGestureRecognizerDelegate {
  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
    if gestureRecognizer === listBodyGestureGuard { return true }
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
    if gestureRecognizer === listBodyGestureGuard {
      guard let otherView = otherGestureRecognizer.view else { return false }
      return otherView === collectionView || otherView.isDescendant(of: collectionView)
    }
    if otherGestureRecognizer === listBodyGestureGuard {
      guard let gestureView = gestureRecognizer.view else { return false }
      return gestureView === collectionView || gestureView.isDescendant(of: collectionView)
    }
    return gestureRecognizer.view === footerCell || otherGestureRecognizer.view === footerCell
  }

  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldBeRequiredToFailBy otherGestureRecognizer: UIGestureRecognizer
  ) -> Bool {
    guard gestureRecognizer === listBodyGestureGuard,
          otherGestureRecognizer is UIPanGestureRecognizer,
          let otherView = otherGestureRecognizer.view,
          otherView !== collectionView else { return false }
    return collectionView.isDescendant(of: otherView)
  }
}

extension NativeListView: UICollectionViewDelegateFlowLayout {
  @available(iOS 15.0, *)
  func collectionView(
    _ collectionView: UICollectionView,
    targetIndexPathForMoveOfItemFromOriginalIndexPath originalIndexPath: IndexPath,
    atCurrentIndexPath currentIndexPath: IndexPath,
    toProposedIndexPath proposedIndexPath: IndexPath
  ) -> IndexPath {
    interactiveReorderUsesAtomicTargeting ? currentIndexPath : proposedIndexPath
  }

  func collectionView(
    _ collectionView: UICollectionView,
    targetIndexPathForMoveFromItemAt originalIndexPath: IndexPath,
    toProposedIndexPath proposedIndexPath: IndexPath
  ) -> IndexPath {
    interactiveReorderUsesAtomicTargeting ? originalIndexPath : proposedIndexPath
  }

  func collectionView(
    _ collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    guard let config, let item = item(at: indexPath) else { return .zero }
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
    if item.type == "walletGroup" { return }
    let origin = (collectionView.cellForItem(at: indexPath) as? NativeListCell)?.rowActionOrigin()
    handleRowPress(item, origin: origin)
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
    invalidateActionAnchor(reason: "scroll")
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

private final class NativeListSectionIndexPreviewView: UIView {
  private let shapeLayer = CAShapeLayer()
  private let label = UILabel()

  var text: String? {
    get { label.text }
    set { label.text = newValue }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    isUserInteractionEnabled = false
    backgroundColor = .clear
    layer.addSublayer(shapeLayer)
    label.textAlignment = .center
    label.adjustsFontForContentSizeCategory = true
    label.font = nativeListFont(ofSize: 30)
    label.isAccessibilityElement = false
    addSubview(label)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(fillColor: UIColor, textColor: UIColor) {
    shapeLayer.fillColor = fillColor.cgColor
    label.textColor = textColor
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    let height = bounds.height
    let bodyWidth = min(height, max(0, bounds.width - 10))
    let midY = height / 2
    let bodyMidX = bodyWidth / 2
    let shoulderX = bodyWidth * 0.92
    let path = UIBezierPath()
    path.move(to: CGPoint(x: bodyMidX, y: 0))
    path.addCurve(
      to: CGPoint(x: 0, y: midY),
      controlPoint1: CGPoint(x: bodyWidth * 0.22, y: 0),
      controlPoint2: CGPoint(x: 0, y: height * 0.22)
    )
    path.addCurve(
      to: CGPoint(x: bodyMidX, y: height),
      controlPoint1: CGPoint(x: 0, y: height * 0.78),
      controlPoint2: CGPoint(x: bodyWidth * 0.22, y: height)
    )
    path.addCurve(
      to: CGPoint(x: shoulderX, y: height * 0.75),
      controlPoint1: CGPoint(x: bodyWidth * 0.72, y: height),
      controlPoint2: CGPoint(x: bodyWidth * 0.86, y: height * 0.88)
    )
    path.addLine(to: CGPoint(x: bounds.width, y: midY))
    path.addLine(to: CGPoint(x: shoulderX, y: height * 0.25))
    path.addCurve(
      to: CGPoint(x: bodyMidX, y: 0),
      controlPoint1: CGPoint(x: bodyWidth * 0.86, y: height * 0.12),
      controlPoint2: CGPoint(x: bodyWidth * 0.72, y: 0)
    )
    path.close()
    shapeLayer.frame = bounds
    shapeLayer.path = path.cgPath
    label.frame = CGRect(x: 0, y: 0, width: bodyWidth, height: height)
  }
}

// OneKey patch: arbitrate index scrubbing against ancestor dismissal gestures.
// private final class NativeListSectionIndexView: UIControl {
private final class NativeListSectionIndexView: UIControl, UIGestureRecognizerDelegate {
  private static let edgePadding: CGFloat = 8
  private static let labelSize: CGFloat = 14
  private static let labelSpacing: CGFloat = 16

  var onSelect: ((Int, Bool) -> Void)?
  var onInteractionEnded: (() -> Void)?
  private var titles: [String] = []
  private var labels: [UILabel] = []
  private var textColor: UIColor = .secondaryLabel
  private var activeColor: UIColor = .tintColor
  private var activeTextColor: UIColor = .white
  private var centeredInWindow = false
  private var lastTouchIndex: Int?
  private(set) var activeIndex: Int?

  override init(frame: CGRect) {
    super.init(frame: frame)
    isAccessibilityElement = true
    accessibilityLabel = "Section index"
    accessibilityTraits = [.adjustable]
    isExclusiveTouch = true

    // UIControl tracking alone cannot prevent an ancestor sheet pan from taking the touch.
    // Recognize immediately, but keep delivering touches to the existing tracking methods.
    let scrubGesture = UILongPressGestureRecognizer(target: nil, action: nil)
    scrubGesture.minimumPressDuration = 0
    scrubGesture.allowableMovement = .greatestFiniteMagnitude
    scrubGesture.cancelsTouchesInView = false
    scrubGesture.delaysTouchesEnded = false
    scrubGesture.delegate = self
    addGestureRecognizer(scrubGesture)
  }

  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldBeRequiredToFailBy otherGestureRecognizer: UIGestureRecognizer
  ) -> Bool {
    guard otherGestureRecognizer is UIPanGestureRecognizer,
          let otherView = otherGestureRecognizer.view,
          otherView !== self else { return false }
    // The dependency applies only to touches starting in this index, including moves outside it.
    return isDescendant(of: otherView)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(
    titles: [String],
    textColor: UIColor,
    activeColor: UIColor,
    activeTextColor: UIColor,
    centeredInWindow: Bool
  ) {
    self.titles = titles
    self.textColor = textColor
    self.activeColor = activeColor
    self.activeTextColor = activeTextColor
    self.centeredInWindow = centeredInWindow
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
    let metrics = indexMetrics(count: labels.count)
    let visibleIndices = visibleLabelIndices(
      count: labels.count,
      trackHeight: metrics.trackHeight
    )
    labels.forEach { $0.isHidden = true }
    for (visibleIndex, index) in visibleIndices.sorted().enumerated() {
      let label = labels[index]
      label.isHidden = false
      let centerY = visibleLabelCenterY(
        visibleIndex: visibleIndex,
        visibleCount: visibleIndices.count,
        metrics: metrics
      )
      label.frame = CGRect(
        x: bounds.width - Self.labelSize - 3,
        y: centerY - Self.labelSize / 2,
        width: Self.labelSize,
        height: Self.labelSize
      )
    }
    updateLabelStyles()
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
    let metrics = indexMetrics(count: titles.count)
    guard metrics.trackHeight > 0 else { return }
    let progress = ((y - metrics.originY) / metrics.trackHeight).clamped(to: 0...1)
    let index = Int(floor(progress * CGFloat(titles.count)))
      .clamped(to: 0...(titles.count - 1))
    if interacting && lastTouchIndex == index { return }
    lastTouchIndex = interacting ? index : nil
    select(index: index, interacting: interacting)
  }

  func centerY(for index: Int) -> CGFloat {
    entryCenterY(index: index, metrics: indexMetrics(count: titles.count))
  }

  private func indexMetrics(count: Int) -> (originY: CGFloat, trackHeight: CGFloat) {
    guard count > 0 else { return (bounds.midY, 0) }
    let availableHeight = max(0, bounds.height - Self.edgePadding * 2)
    let trackHeight = min(availableHeight, Self.labelSpacing * CGFloat(count))
    let centeredOriginY = (bounds.height - trackHeight) / 2
    guard centeredInWindow, let window else { return (centeredOriginY, trackHeight) }
    let windowCenterY = window.safeAreaLayoutGuide.layoutFrame.midY
    let localCenterY = convert(CGPoint(x: 0, y: windowCenterY), from: window).y
    return (
      (localCenterY - trackHeight / 2).clamped(
        to: Self.edgePadding...max(Self.edgePadding, bounds.height - Self.edgePadding - trackHeight)
      ),
      trackHeight
    )
  }

  private func entryCenterY(
    index: Int,
    metrics: (originY: CGFloat, trackHeight: CGFloat)
  ) -> CGFloat {
    guard !titles.isEmpty else { return bounds.midY }
    return metrics.originY + metrics.trackHeight * (CGFloat(index) + 0.5) / CGFloat(titles.count)
  }

  private func visibleLabelCenterY(
    visibleIndex: Int,
    visibleCount: Int,
    metrics: (originY: CGFloat, trackHeight: CGFloat)
  ) -> CGFloat {
    let visibleTrackHeight = min(metrics.trackHeight, Self.labelSpacing * CGFloat(visibleCount))
    let visibleOriginY = metrics.originY + (metrics.trackHeight - visibleTrackHeight) / 2
    return visibleOriginY
      + visibleTrackHeight * (CGFloat(visibleIndex) + 0.5) / CGFloat(max(visibleCount, 1))
  }

  private func visibleLabelIndices(count: Int, trackHeight: CGFloat) -> Set<Int> {
    guard count > 0 else { return [] }
    let maxVisible = max(1, Int(floor(trackHeight / Self.labelSpacing)))
    guard count > maxVisible else { return Set(0..<count) }
    guard maxVisible > 1 else { return [0] }
    return Set((0..<maxVisible).map { slot in
      Int(round(CGFloat(slot * (count - 1)) / CGFloat(maxVisible - 1)))
    })
  }

  private func select(index: Int, interacting: Bool) {
    onSelect?(index, interacting)
    setActiveIndex(index)
  }

  private func updateLabelStyles() {
    for (index, label) in labels.enumerated() {
      let active = index == activeIndex && !label.isHidden
      label.textColor = active ? activeTextColor : textColor
      label.backgroundColor = active ? activeColor : .clear
      label.layer.cornerRadius = Self.labelSize / 2
      label.layer.masksToBounds = active
      label.font = nativeListFont(ofSize: 10, weight: active ? .medium : .regular)
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
