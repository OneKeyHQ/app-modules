import Foundation
import UIKit
import React

// MARK: - Data types for props from JS

struct TabItem {
  let key: String
  let title: String
  let sfSymbol: String?
  let badge: String?
  let badgeBackgroundColor: Int?
  let badgeTextColor: Int?
  let activeTintColor: Int?
  let hidden: Bool
  let testID: String?
  let role: String?
  let preventsDefault: Bool

  init(dict: NSDictionary) {
    self.key = dict["key"] as? String ?? ""
    self.title = dict["title"] as? String ?? ""
    self.sfSymbol = dict["sfSymbol"] as? String
    self.badge = dict["badge"] as? String
    self.badgeBackgroundColor = dict["badgeBackgroundColor"] as? Int
    self.badgeTextColor = dict["badgeTextColor"] as? Int
    self.activeTintColor = dict["activeTintColor"] as? Int
    self.hidden = dict["hidden"] as? Bool ?? false
    self.testID = dict["testID"] as? String
    self.role = dict["role"] as? String
    self.preventsDefault = dict["preventsDefault"] as? Bool ?? false
  }
}

struct IconSource {
  let uri: String
  let width: Double
  let height: Double
  let scale: Double

  init(dict: NSDictionary) {
    self.uri = dict["uri"] as? String ?? ""
    self.width = dict["width"] as? Double ?? 0
    self.height = dict["height"] as? Double ?? 0
    self.scale = dict["scale"] as? Double ?? 1
  }
}

// MARK: - RCTTabViewContainerView

class RCTTabViewContainerView: UIView {

  // MARK: - Debug logging

  private static let debugLog = true

  private func log(_ message: String, function: String = #function) {
    guard Self.debugLog else { return }
    RCTTabViewLog.debug("RCTTabView", message: "[\(function)] \(message)")
  }

  // MARK: - Internal State

  private var tabBarController: UITabBarController?
  private var childViews: [UIView] = []
  private var bottomAccessoryView: UIView?
  private var loadedIcons: [Int: UIImage] = [:]
  private var imageLoader: RCTImageLoaderProtocol?
  private let iconSize = CGSize(width: 27, height: 27)
  private var longPressHandler: LongPressGestureHandler?
  private var tabBarHiddenObservation: NSKeyValueObservation?
  private var tabBarAlphaObservation: NSKeyValueObservation?
  private var tabBarFrameObservation: NSKeyValueObservation?
  /// Cached view controllers keyed by tab key to avoid unnecessary reparenting
  private var cachedViewControllers: [String: UIViewController] = [:]

  // MARK: - Props (set by ViewManager via RCT_EXPORT_VIEW_PROPERTY)

  @objc var items: [NSDictionary]? {
    didSet {
      log("items prop changed: \(oldValue?.count ?? 0) -> \(items?.count ?? 0) items")
      rebuildViewControllers()
    }
  }

  @objc var selectedPage: String? {
    didSet {
      log("selectedPage prop changed: \(oldValue ?? "nil") -> \(selectedPage ?? "nil")")
      updateSelectedTab()
    }
  }

  @objc var icons: [NSDictionary]? {
    didSet { loadIconsFromSources() }
  }

  @objc var labeled: NSNumber? {
    didSet { rebuildViewControllers() }
  }

  @objc var sidebarAdaptable: Bool = false {
    didSet { updateSidebarAdaptable() }
  }

  @objc var disablePageAnimations: Bool = false

  @objc var hapticFeedbackEnabled: Bool = false

  @objc var scrollEdgeAppearance: String? {
    didSet { updateTabBarAppearance() }
  }

  @objc var minimizeBehavior: String? {
    didSet { updateMinimizeBehavior() }
  }

  @objc var tabBarHidden: Bool = false {
    didSet {
      log("tabBarHidden prop changed: \(oldValue) -> \(tabBarHidden)")
      updateTabBarHidden()
    }
  }

  @objc var translucent: NSNumber? {
    didSet { updateTabBarAppearance() }
  }

  @objc var barTintColor: String? {
    didSet { updateTabBarAppearance() }
  }

  @objc var activeTintColor: String? {
    didSet { updateTintColors() }
  }

  @objc var inactiveTintColor: String? {
    didSet { updateTabBarAppearance() }
  }

  @objc var rippleColor: String? // Android only

  @objc var activeIndicatorColor: String? // Android only

  @objc var fontFamily: String? {
    didSet { updateTabBarAppearance() }
  }

  @objc var fontWeight: String? {
    didSet { updateTabBarAppearance() }
  }

  @objc var fontSize: NSNumber? {
    didSet { updateTabBarAppearance() }
  }

  // MARK: - Events (RCTDirectEventBlock)

  @objc var onPageSelected: RCTDirectEventBlock?
  @objc var onTabLongPress: RCTDirectEventBlock?
  @objc var onTabBarMeasured: RCTDirectEventBlock?
  @objc var onNativeLayout: RCTDirectEventBlock?

  // MARK: - Init

  override init(frame: CGRect) {
    super.init(frame: frame)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  // MARK: - Fabric subview management override

  // Fabric's `didUpdateReactSubviews` (called from RCTLegacyViewManagerInteropComponentView
  // finalizeUpdates:) re-adds all React children as direct subviews of this container via
  // insertSubview:atIndex:. This steals our childViews from their VC views, breaking touch
  // delivery and scroll. Override to keep childViews in VC views and only add non-managed
  // subviews (like bottomAccessoryView) normally.
  override func didUpdateReactSubviews() {
    // Do NOT call super — super would re-insert all reactSubviews as direct children of self,
    // undoing our placement into UITabBarController's VC views.
    log("didUpdateReactSubviews: suppressed Fabric re-mount (childViews=\(childViews.count))")

    // Ensure childViews are in VC views (they may have just been inserted via insertReactSubview)
    if tabBarController != nil {
      rebuildViewControllers()
    }
  }

  override func didAddSubview(_ subview: UIView) {
    super.didAddSubview(subview)
    if let tbcView = tabBarController?.view, subview !== tbcView {
      bringSubviewToFront(tbcView)
    }
  }

  // MARK: - Layout

  override func layoutSubviews() {
    super.layoutSubviews()
    if let tbc = tabBarController {
      log("layoutSubviews: bounds=\(bounds), tabBar.isHidden=\(tbc.tabBar.isHidden), tabBar.frame=\(tbc.tabBar.frame), tabBar.alpha=\(tbc.tabBar.alpha)")
    }
    handleLayout()
  }

  // MARK: - React subview management

  override func insertReactSubview(_ subview: UIView!, at atIndex: Int) {
    super.insertReactSubview(subview, at: atIndex)
    log("insertReactSubview at index \(atIndex), subview type: \(type(of: subview as Any)), total childViews: \(childViews.count)")

    if subview is RCTBottomAccessoryContainerView {
      self.bottomAccessoryView = subview
      attachBottomAccessory()
      return
    }

    guard atIndex >= 0 && atIndex <= childViews.count else { return }
    childViews.insert(subview, at: atIndex)
    rebuildViewControllers()
  }

  override func removeReactSubview(_ subview: UIView!) {
    super.removeReactSubview(subview)
    log("removeReactSubview, subview type: \(type(of: subview as Any)), remaining childViews: \(childViews.count)")

    if subview is RCTBottomAccessoryContainerView {
      detachBottomAccessory()
      self.bottomAccessoryView = nil
      return
    }

    if let index = childViews.firstIndex(of: subview) {
      childViews.remove(at: index)
      rebuildViewControllers()
    }
  }

  // MARK: - Bottom Accessory (iOS 26+)

  private func attachBottomAccessory() {
    // TODO: Re-enable when UITabBar.bottomAccessoryView is available in the SDK
  }

  private func detachBottomAccessory() {
    // TODO: Re-enable when UITabBar.bottomAccessoryView is available in the SDK
  }

  // MARK: - Lifecycle

  private func handleLayout() {
    let bounds = self.bounds
    guard bounds.width > 0 && bounds.height > 0 else { return }

    setupTabBarControllerIfNeeded()
    tabBarController?.view.frame = bounds

    // Log container's direct subviews
    if let tbc = tabBarController {
      let strayCount = self.subviews.filter { sv in sv !== tbc.view && childViews.contains(where: { $0 === sv }) }.count
      if strayCount > 0 {
        log("⚠️ handleLayout: \(strayCount) stray childViews found as direct subviews of self!")
      }
      log("handleLayout: self.subviews=\(self.subviews.count), strayChildren=\(strayCount)")
    }

    onNativeLayout?(["width": Double(bounds.width), "height": Double(bounds.height)])
  }

  private func setupTabBarControllerIfNeeded() {
    guard tabBarController == nil else { return }
    guard let parentVC = self.reactViewController() else {
      log("setupTabBarControllerIfNeeded: no parent view controller found")
      return
    }
    log("setupTabBarControllerIfNeeded: creating UITabBarController, parentVC=\(type(of: parentVC))")

    let tbc = UITabBarController()
    tbc.delegate = TabViewDelegateProxy.shared
    TabViewDelegateProxy.shared.register(self, for: tbc)
    tbc.view.backgroundColor = .clear

    parentVC.addChild(tbc)
    self.addSubview(tbc.view)
    tbc.view.frame = self.bounds
    tbc.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    tbc.didMove(toParent: parentVC)

    self.tabBarController = tbc

    // KVO observers to detect external changes to tab bar visibility
    if Self.debugLog {
      tabBarHiddenObservation = tbc.tabBar.observe(\.isHidden, options: [.old, .new]) { [weak self] tabBar, change in
        self?.log("KVO tabBar.isHidden changed: \(change.oldValue ?? false) -> \(change.newValue ?? false), frame=\(tabBar.frame)")
        if change.newValue == true && self?.tabBarHidden == false {
          self?.log("⚠️ TAB BAR WAS HIDDEN EXTERNALLY! tabBarHidden prop is false but isHidden became true")
          Thread.callStackSymbols.prefix(15).forEach { self?.log("  \($0)") }
        }
      }
      tabBarAlphaObservation = tbc.tabBar.observe(\.alpha, options: [.old, .new]) { [weak self] tabBar, change in
        if change.oldValue != change.newValue {
          self?.log("KVO tabBar.alpha changed: \(change.oldValue ?? 0) -> \(change.newValue ?? 0), frame=\(tabBar.frame)")
        }
      }
      tabBarFrameObservation = tbc.tabBar.observe(\.frame, options: [.old, .new]) { [weak self] tabBar, change in
        let oldFrame = change.oldValue ?? .zero
        let newFrame = change.newValue ?? .zero
        if oldFrame != newFrame {
          self?.log("KVO tabBar.frame changed: \(oldFrame) -> \(newFrame), isHidden=\(tabBar.isHidden), alpha=\(tabBar.alpha)")
        }
      }
    }

    setupLongPressGesture()
    rebuildViewControllers()
    updateTabBarAppearance()
    updateTintColors()
    updateTabBarHidden()
    updateSidebarAdaptable()
    updateMinimizeBehavior()

    DispatchQueue.main.async { [weak self] in
      guard let self, let tbc = self.tabBarController else { return }
      if !tbc.tabBar.isHidden {
        self.onTabBarMeasured?(["height": Double(tbc.tabBar.frame.size.height)])
      }
    }
  }

  // MARK: - Long press

  private func setupLongPressGesture() {
    guard let tbc = tabBarController else { return }

    if let existingGestures = tbc.tabBar.gestureRecognizers {
      for gesture in existingGestures where gesture is UILongPressGestureRecognizer {
        tbc.tabBar.removeGestureRecognizer(gesture)
      }
    }

    let handler = LongPressGestureHandler(tabBar: tbc.tabBar) { [weak self] index, _ in
      guard let self else { return }
      let filtered = self.filteredItems
      guard let tabData = filtered[safe: index] else { return }
      self.onTabLongPress?(["key": tabData.key])
      self.emitHapticFeedback(longPress: true)
    }

    let gesture = UILongPressGestureRecognizer(target: handler, action: #selector(LongPressGestureHandler.handleLongPress(_:)))
    gesture.minimumPressDuration = 0.5
    tbc.tabBar.addGestureRecognizer(gesture)
    self.longPressHandler = handler
  }

  // MARK: - Tab management

  private var parsedItems: [TabItem] {
    (items ?? []).map { TabItem(dict: $0) }
  }

  private var filteredItems: [TabItem] {
    parsedItems.filter { !$0.hidden || $0.key == selectedPage }
  }

  private func rebuildViewControllers() {
    guard let tbc = tabBarController else {
      log("rebuildViewControllers: no tabBarController yet")
      return
    }
    log("rebuildViewControllers: tabBar.isHidden=\(tbc.tabBar.isHidden), tabBarHidden=\(tabBarHidden), items=\(parsedItems.count), childViews=\(childViews.count)")

    let filtered = filteredItems
    let allItems = parsedItems
    var viewControllers: [UIViewController] = []
    var usedKeys = Set<String>()

    for (_, tabData) in filtered.enumerated() {
      guard let originalIndex = allItems.firstIndex(where: { $0.key == tabData.key }) else { continue }
      usedKeys.insert(tabData.key)

      // Reuse cached VC or create a new one
      let vc: UIViewController
      if let cached = cachedViewControllers[tabData.key] {
        vc = cached
        log("rebuildViewControllers: reusing cached VC for tab[\(tabData.key)]")
      } else {
        vc = UIViewController()
        vc.view.backgroundColor = .clear
        vc.view.clipsToBounds = true
        cachedViewControllers[tabData.key] = vc
        log("rebuildViewControllers: created new VC for tab[\(tabData.key)]")
      }

      // Attach child view if not already attached to this VC
      if originalIndex < childViews.count {
        let childView = childViews[originalIndex]
        let currentSuperview = childView.superview
        let superviewType = currentSuperview.map { String(describing: type(of: $0)) } ?? "nil"
        let isSelf = currentSuperview === self
        let isVcView = currentSuperview === vc.view
        log("rebuildViewControllers: tab[\(tabData.key)] childView superview=\(superviewType), isSelf=\(isSelf), isVcView=\(isVcView), userInteraction=\(childView.isUserInteractionEnabled)")
        if childView.superview !== vc.view {
          childView.removeFromSuperview()
          vc.view.addSubview(childView)
          childView.translatesAutoresizingMaskIntoConstraints = false
          childView.pinEdges(to: vc.view)
          log("rebuildViewControllers: tab[\(tabData.key)] MOVED childView to vc.view (was \(superviewType))")
        }
      }

      // Configure tab bar item
      let icon = loadedIcons[originalIndex]
      let sfSymbol = tabData.sfSymbol ?? ""

      var tabImage: UIImage?
      if let icon {
        tabImage = icon
      } else if !sfSymbol.isEmpty {
        tabImage = UIImage(systemName: sfSymbol)
      }

      let isLabeled = labeled?.boolValue ?? true
      let title = isLabeled ? tabData.title : nil
      vc.tabBarItem = UITabBarItem(title: title, image: tabImage, tag: originalIndex)
      vc.tabBarItem.badgeValue = tabData.badge
      vc.tabBarItem.accessibilityIdentifier = tabData.testID

      // Badge colors (iOS 16+)
      if let badgeBgColor = tabData.badgeBackgroundColor {
        vc.tabBarItem.badgeColor = UIColor(rgb: badgeBgColor)
      }
      if #available(iOS 16.0, *), let badgeTxtColor = tabData.badgeTextColor {
        let badge = vc.tabBarItem
        badge?.setBadgeTextAttributes(
          [.foregroundColor: UIColor(rgb: badgeTxtColor)],
          for: .normal
        )
      }

      let inactiveColor = colorFromHex(inactiveTintColor)
      let attributes = TabBarFontSize.createNormalStateAttributes(
        fontSize: fontSize?.intValue,
        fontFamily: fontFamily,
        fontWeight: fontWeight,
        inactiveColor: inactiveColor
      )
      vc.tabBarItem.setTitleTextAttributes(attributes, for: .normal)

      viewControllers.append(vc)
    }

    // Clean up cached VCs for removed tabs
    for key in cachedViewControllers.keys where !usedKeys.contains(key) {
      cachedViewControllers.removeValue(forKey: key)
    }

    // Only call setViewControllers if the VC list actually changed
    let currentVCs = tbc.viewControllers ?? []
    if currentVCs.count != viewControllers.count || !zip(currentVCs, viewControllers).allSatisfy({ $0 === $1 }) {
      log("rebuildViewControllers: updating VCs (old=\(currentVCs.count), new=\(viewControllers.count))")
      if disablePageAnimations {
        UIView.performWithoutAnimation {
          tbc.setViewControllers(viewControllers, animated: false)
        }
      } else {
        tbc.setViewControllers(viewControllers, animated: false)
      }
    } else {
      log("rebuildViewControllers: VCs unchanged, skipping setViewControllers")
    }

    // Log the view hierarchy after setting VCs
    log("rebuildViewControllers: tbc.view.subviews=\(tbc.view.subviews.map { "\(type(of: $0)):\(String(format: "%.0f", $0.frame.width))x\(String(format: "%.0f", $0.frame.height))" })")
    if let selectedVC = tbc.selectedViewController {
      log("rebuildViewControllers: selectedVC.view.frame=\(selectedVC.view.frame), subviews=\(selectedVC.view.subviews.count)")
    }

    updateSelectedTab()
  }

  // MARK: - Selection

  private func updateSelectedTab() {
    guard let tbc = tabBarController,
          let selectedPage else {
      log("updateSelectedTab: skipped (tbc=\(tabBarController != nil), selectedPage=\(selectedPage ?? "nil"))")
      return
    }
    log("updateSelectedTab: selectedPage=\(selectedPage), tabBar.isHidden=\(tbc.tabBar.isHidden)")

    let filtered = filteredItems
    guard let index = filtered.firstIndex(where: { $0.key == selectedPage }) else { return }

    if disablePageAnimations {
      UIView.performWithoutAnimation {
        tbc.selectedIndex = index
      }
    } else {
      tbc.selectedIndex = index
    }

    updateTintColors()
  }

  // MARK: - Appearance

  private func updateTabBarAppearance() {
    guard let tbc = tabBarController else { return }
    let tabBar = tbc.tabBar

    if scrollEdgeAppearance == "transparent" {
      configureTransparentAppearance(tabBar: tabBar)
      return
    }

    configureStandardAppearance(tabBar: tabBar)
  }

  private func configureTransparentAppearance(tabBar: UITabBar) {
    tabBar.barTintColor = colorFromHex(barTintColor)
    tabBar.isTranslucent = translucent?.boolValue ?? true
    tabBar.unselectedItemTintColor = colorFromHex(inactiveTintColor)

    guard let tabBarItems = tabBar.items else { return }

    let attributes = TabBarFontSize.createNormalStateAttributes(
      fontSize: fontSize?.intValue,
      fontFamily: fontFamily,
      fontWeight: fontWeight,
      inactiveColor: nil
    )

    tabBarItems.forEach { item in
      item.setTitleTextAttributes(attributes, for: .normal)
    }
  }

  private func configureStandardAppearance(tabBar: UITabBar) {
    let appearance = UITabBarAppearance()

    switch scrollEdgeAppearance {
    case "opaque":
      appearance.configureWithOpaqueBackground()
    default:
      appearance.configureWithDefaultBackground()
    }

    if translucent?.boolValue == false {
      appearance.configureWithOpaqueBackground()
    }

    if let bgColor = colorFromHex(barTintColor) {
      appearance.backgroundColor = bgColor
    }

    let itemAppearance = UITabBarItemAppearance()
    let inactiveColor = colorFromHex(inactiveTintColor)

    let attributes = TabBarFontSize.createNormalStateAttributes(
      fontSize: fontSize?.intValue,
      fontFamily: fontFamily,
      fontWeight: fontWeight,
      inactiveColor: inactiveColor
    )

    if let inactiveColor {
      itemAppearance.normal.iconColor = inactiveColor
    }

    itemAppearance.normal.titleTextAttributes = attributes

    appearance.stackedLayoutAppearance = itemAppearance
    appearance.inlineLayoutAppearance = itemAppearance
    appearance.compactInlineLayoutAppearance = itemAppearance

    tabBar.standardAppearance = appearance
    tabBar.scrollEdgeAppearance = appearance.copy()
  }

  private func updateTintColors() {
    guard let tbc = tabBarController else { return }

    var tintColor = colorFromHex(activeTintColor)
    if let selectedPage,
       let tabData = parsedItems.first(where: { $0.key == selectedPage }),
       let perTabColor = tabData.activeTintColor {
      tintColor = UIColor(rgb: perTabColor)
    }

    if let tintColor {
      tbc.tabBar.tintColor = tintColor
    }
  }

  private func updateTabBarHidden() {
    guard let tbc = tabBarController else {
      log("updateTabBarHidden: no tabBarController yet")
      return
    }
    log("updateTabBarHidden: setting tabBar.isHidden=\(tabBarHidden), current tabBar.isHidden=\(tbc.tabBar.isHidden), tabBar.frame=\(tbc.tabBar.frame)")
    tbc.tabBar.isHidden = tabBarHidden
    tbc.tabBar.isUserInteractionEnabled = !tabBarHidden

    if !tabBarHidden {
      onTabBarMeasured?(["height": Double(tbc.tabBar.frame.size.height)])
    }
  }

  private func updateSidebarAdaptable() {
    guard let tbc = tabBarController else { return }
    if #available(iOS 18.0, *) {
      #if compiler(>=6.0)
      tbc.mode = sidebarAdaptable ? .tabSidebar : .tabBar
      #endif
    }
  }

  private func updateMinimizeBehavior() {
    #if compiler(>=6.2)
    if #available(iOS 26.0, *) {
      guard let tbc = tabBarController,
            let behavior = minimizeBehavior else { return }
      switch behavior {
      case "automatic":
        tbc.tabBarMinimizeBehavior = .automatic
      case "never":
        tbc.tabBarMinimizeBehavior = .never
      case "onScrollUp":
        tbc.tabBarMinimizeBehavior = .onScrollUp
      case "onScrollDown":
        tbc.tabBarMinimizeBehavior = .onScrollDown
      default:
        break
      }
    }
    #endif
  }

  // MARK: - Haptic feedback

  private func emitHapticFeedback(longPress: Bool = false) {
    guard hapticFeedbackEnabled else { return }

    if longPress {
      UINotificationFeedbackGenerator().notificationOccurred(.success)
    } else {
      UISelectionFeedbackGenerator().selectionChanged()
    }
  }

  // MARK: - Icon loading

  func setImageLoader(_ loader: RCTImageLoaderProtocol) {
    self.imageLoader = loader
    loadIconsFromSources()
  }

  private func loadIconsFromSources() {
    guard let imageLoader, let iconDicts = icons else { return }

    let iconSources = iconDicts.map { IconSource(dict: $0) }

    for (index, source) in iconSources.enumerated() {
      guard !source.uri.isEmpty else { continue }

      let url = URL(string: source.uri)
      guard let url else { continue }

      let request = URLRequest(url: url)
      let size = CGSize(width: source.width, height: source.height)
      let scale = CGFloat(source.scale)

      imageLoader.loadImage(
        with: request,
        size: size,
        scale: scale,
        clipped: true,
        resizeMode: RCTResizeMode.contain,
        progressBlock: { _, _ in },
        partialLoad: { _ in },
        completionBlock: { [weak self] error, image in
          if error != nil { return }
          guard let image, let self else { return }
          DispatchQueue.main.async {
            self.loadedIcons[index] = image.resizeImageTo(size: self.iconSize)
            self.rebuildViewControllers()
          }
        })
    }
  }

  // MARK: - Tab delegate callbacks

  func handleTabSelected(at index: Int) {
    let filtered = filteredItems
    guard let tabData = filtered[safe: index] else { return }
    onPageSelected?(["key": tabData.key])
    emitHapticFeedback()
  }

  func shouldSelectTab(at index: Int, isReselection: Bool) -> Bool {
    let filtered = filteredItems
    guard let tabData = filtered[safe: index] else { return false }

    if isReselection {
      onPageSelected?(["key": tabData.key])
      emitHapticFeedback()
      return false
    }

    onPageSelected?(["key": tabData.key])
    emitHapticFeedback()
    return !tabData.preventsDefault
  }

  // MARK: - Bottom accessory placement

  func handlePlacementChanged(_ placement: String) {
    guard bottomAccessoryView != nil else { return }
    NotificationCenter.default.post(
      name: .bottomAccessoryPlacementChanged,
      object: nil,
      userInfo: ["placement": placement]
    )
  }

  // MARK: - Color helpers

  private func colorFromHex(_ hex: String?) -> UIColor? {
    guard let hex, !hex.isEmpty else { return nil }
    var sanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
    if sanitized.hasPrefix("#") {
      sanitized.remove(at: sanitized.startIndex)
    }

    var color: UInt64 = 0
    Scanner(string: sanitized).scanHexInt64(&color)

    if sanitized.count == 8 {
      let a = CGFloat((color >> 24) & 0xFF) / 255.0
      let r = CGFloat((color >> 16) & 0xFF) / 255.0
      let g = CGFloat((color >> 8) & 0xFF) / 255.0
      let b = CGFloat(color & 0xFF) / 255.0
      return UIColor(red: r, green: g, blue: b, alpha: a)
    } else {
      let r = CGFloat((color >> 16) & 0xFF) / 255.0
      let g = CGFloat((color >> 8) & 0xFF) / 255.0
      let b = CGFloat(color & 0xFF) / 255.0
      return UIColor(red: r, green: g, blue: b, alpha: 1.0)
    }
  }
}

// MARK: - UITabBarControllerDelegate proxy

class TabViewDelegateProxy: NSObject, UITabBarControllerDelegate {
  static let shared = TabViewDelegateProxy()
  private var mapping: [ObjectIdentifier: RCTTabViewContainerView] = [:]

  func register(_ view: RCTTabViewContainerView, for controller: UITabBarController) {
    mapping[ObjectIdentifier(controller)] = view
  }

  func unregister(for controller: UITabBarController) {
    mapping.removeValue(forKey: ObjectIdentifier(controller))
  }

  func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
    guard let containerView = mapping[ObjectIdentifier(tabBarController)] else {
      RCTTabViewLog.debug("RCTTabView", message: "[DelegateProxy] shouldSelect: no container view found!")
      return false
    }
    let isReselection = tabBarController.selectedViewController == viewController
    RCTTabViewLog.debug("RCTTabView", message: "[DelegateProxy] shouldSelect: isReselection=\(isReselection), tabBar.isHidden=\(tabBarController.tabBar.isHidden)")

    if let index = tabBarController.viewControllers?.firstIndex(of: viewController) {
      let result = containerView.shouldSelectTab(at: index, isReselection: isReselection)
      RCTTabViewLog.debug("RCTTabView", message: "[DelegateProxy] shouldSelect result=\(result)")
      return result
    }

    return false
  }
}

// MARK: - Long press gesture handler

private class LongPressGestureHandler: NSObject {
  private weak var tabBar: UITabBar?
  private let handler: (Int, Bool) -> Void

  init(tabBar: UITabBar, handler: @escaping (Int, Bool) -> Void) {
    self.tabBar = tabBar
    self.handler = handler
    super.init()
  }

  @objc func handleLongPress(_ recognizer: UILongPressGestureRecognizer) {
    guard recognizer.state == .began,
          let tabBar else { return }

    let location = recognizer.location(in: tabBar)

    let tabBarButtons = tabBar.subviews
      .filter { String(describing: type(of: $0)).contains("UITabBarButton") }
      .sorted { $0.frame.minX < $1.frame.minX }

    for (index, button) in tabBarButtons.enumerated() {
      if button.frame.contains(location) {
        handler(index, true)
        break
      }
    }
  }
}
