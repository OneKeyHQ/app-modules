import Foundation
import UIKit
import NitroModules
import React

// HybridTabView conforms to the nitrogen-generated HybridTabViewSpec protocol.
// Since nitrogen hasn't been run yet, we define the conformance against the
// protocol that WILL be generated. The protocol will look like:
//
//   protocol HybridTabViewSpec: HybridObject, HybridView {
//     var items: [TabItemStruct]? { get set }
//     var selectedPage: String? { get set }
//     var icons: [IconSourceStruct]? { get set }
//     ... (all other props)
//     var onPageSelected: ((String) -> Void)? { get set }
//     var onTabLongPress: ((String) -> Void)? { get set }
//     var onTabBarMeasured: ((Double) -> Void)? { get set }
//     var onNativeLayout: ((Double, Double) -> Void)? { get set }
//     func insertChild(tag: Double, index: Double) throws
//     func removeChild(tag: Double, index: Double) throws
//   }
//
// For now we write the class so it will compile once nitrogen generates the spec.

class HybridTabView: HybridTabViewSpec {

  // MARK: - HybridView

  var view: UIView

  // MARK: - Internal State

  private var tabBarController: UITabBarController?
  private var childViews: [UIView] = []
  private var bottomAccessoryView: UIView?
  private var loadedIcons: [Int: UIImage] = [:]
  private var imageLoader: RCTImageLoaderProtocol?
  private let iconSize = CGSize(width: 27, height: 27)
  private var longPressHandler: LongPressGestureHandler?

  // MARK: - Props

  var items: [TabItemStruct]? {
    didSet { rebuildViewControllers() }
  }

  var selectedPage: String? {
    didSet { updateSelectedTab() }
  }

  var icons: [IconSourceStruct]? {
    didSet { loadIconsFromSources() }
  }

  var labeled: Bool? {
    didSet { rebuildViewControllers() }
  }

  var sidebarAdaptable: Bool? {
    didSet { updateSidebarAdaptable() }
  }

  var disablePageAnimations: Bool? {
    didSet {}
  }

  var hapticFeedbackEnabled: Bool? {
    didSet {}
  }

  var scrollEdgeAppearance: String? {
    didSet { updateTabBarAppearance() }
  }

  var minimizeBehavior: String? {
    didSet { updateMinimizeBehavior() }
  }

  var tabBarHidden: Bool? {
    didSet { updateTabBarHidden() }
  }

  var translucent: Bool? {
    didSet { updateTabBarAppearance() }
  }

  var barTintColor: String? {
    didSet { updateTabBarAppearance() }
  }

  var activeTintColor: String? {
    didSet { updateTintColors() }
  }

  var inactiveTintColor: String? {
    didSet { updateTabBarAppearance() }
  }

  var rippleColor: String? {
    didSet {} // Android only
  }

  var activeIndicatorColor: String? {
    didSet {} // Android only
  }

  var fontFamily: String? {
    didSet { updateTabBarAppearance() }
  }

  var fontWeight: String? {
    didSet { updateTabBarAppearance() }
  }

  var fontSize: Double? {
    didSet { updateTabBarAppearance() }
  }

  // MARK: - Events

  var onPageSelected: ((_ key: String) -> Void)?
  var onTabLongPress: ((_ key: String) -> Void)?
  var onTabBarMeasured: ((_ height: Double) -> Void)?
  var onNativeLayout: ((_ width: Double, _ height: Double) -> Void)?

  // MARK: - Init

  override init() {
    self.view = TabViewLayoutView()
    super.init()
    (self.view as? TabViewLayoutView)?.layoutCallback = { [weak self] in
      self?.handleLayout()
    }
  }

  // MARK: - Methods (called from custom component view for child management)

  func insertChild(tag: Double, index: Double) throws {
    // The custom component view's mountChildComponentView calls this.
    // index == -1 signals a bottom accessory view.
    let indexInt = Int(index)

    // Find the child view in our view's subviews by tag
    let childView = findChildView(tag: Int(tag))

    if indexInt == -1 {
      // Bottom accessory view
      if let accessoryView = childView {
        self.bottomAccessoryView = accessoryView
        attachBottomAccessory()
      }
      return
    }

    guard let childView else { return }
    guard indexInt >= 0 && indexInt <= childViews.count else { return }
    childViews.insert(childView, at: indexInt)
    rebuildViewControllers()
  }

  func removeChild(tag: Double, index: Double) throws {
    let indexInt = Int(index)

    if indexInt == -1 {
      // Remove bottom accessory view
      detachBottomAccessory()
      self.bottomAccessoryView = nil
      return
    }

    guard indexInt >= 0 && indexInt < childViews.count else { return }
    childViews.remove(at: indexInt)
    rebuildViewControllers()
  }

  private func findChildView(tag: Int) -> UIView? {
    // The child was already added to contentView (our view) by the .mm
    for subview in view.subviews {
      if subview.reactTag?.intValue == tag {
        return subview
      }
    }
    // Also check recursively in the tab bar controller's view
    if let tbcView = tabBarController?.view {
      for subview in tbcView.subviews {
        if subview.reactTag?.intValue == tag {
          return subview
        }
      }
    }
    return nil
  }

  // MARK: - Bottom Accessory (iOS 26+)

  private func attachBottomAccessory() {
    // TODO: Re-enable when UITabBar.bottomAccessoryView is available in the SDK
    // #if compiler(>=6.2)
    // if #available(iOS 26.0, *) {
    //   guard let tbc = tabBarController,
    //         let accessoryView = bottomAccessoryView else { return }
    //   accessoryView.removeFromSuperview()
    //   let container = UIView()
    //   container.addSubview(accessoryView)
    //   accessoryView.translatesAutoresizingMaskIntoConstraints = false
    //   accessoryView.pinEdges(to: container)
    //   tbc.tabBar.bottomAccessoryView = container
    // }
    // #endif
  }

  private func detachBottomAccessory() {
    // TODO: Re-enable when UITabBar.bottomAccessoryView is available in the SDK
    // #if compiler(>=6.2)
    // if #available(iOS 26.0, *) {
    //   guard let tbc = tabBarController else { return }
    //   tbc.tabBar.bottomAccessoryView = nil
    // }
    // #endif
  }

  // MARK: - Lifecycle

  private func handleLayout() {
    let bounds = view.bounds
    guard bounds.width > 0 && bounds.height > 0 else { return }

    setupTabBarControllerIfNeeded()
    tabBarController?.view.frame = bounds

    onNativeLayout?(Double(bounds.width), Double(bounds.height))
  }

  private func setupTabBarControllerIfNeeded() {
    guard tabBarController == nil else { return }
    guard let parentVC = view.reactViewController() else { return }

    let tbc = UITabBarController()
    tbc.delegate = DelegateProxy.shared
    DelegateProxy.shared.register(self, for: tbc)
    tbc.view.backgroundColor = .clear

    parentVC.addChild(tbc)
    view.addSubview(tbc.view)
    tbc.view.frame = view.bounds
    tbc.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    tbc.didMove(toParent: parentVC)

    self.tabBarController = tbc

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
        self.onTabBarMeasured?(Double(tbc.tabBar.frame.size.height))
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
      self.onTabLongPress?(tabData.key)
      self.emitHapticFeedback(longPress: true)
    }

    let gesture = UILongPressGestureRecognizer(target: handler, action: #selector(LongPressGestureHandler.handleLongPress(_:)))
    gesture.minimumPressDuration = 0.5
    tbc.tabBar.addGestureRecognizer(gesture)
    self.longPressHandler = handler
  }

  // MARK: - Tab management

  private var filteredItems: [TabItemStruct] {
    (items ?? []).filter { !($0.hidden ?? false) || $0.key == selectedPage }
  }

  private func rebuildViewControllers() {
    guard let tbc = tabBarController else { return }

    let filtered = filteredItems
    let allItems = items ?? []
    var viewControllers: [UIViewController] = []

    for (_, tabData) in filtered.enumerated() {
      guard let originalIndex = allItems.firstIndex(where: { $0.key == tabData.key }) else { continue }

      let vc = UIViewController()
      vc.view.backgroundColor = .clear

      if originalIndex < childViews.count {
        let childView = childViews[originalIndex]
        vc.view.addSubview(childView)
        childView.translatesAutoresizingMaskIntoConstraints = false
        childView.pinEdges(to: vc.view)
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

      let isLabeled = labeled ?? true
      let title = isLabeled ? tabData.title : nil
      vc.tabBarItem = UITabBarItem(title: title, image: tabImage, tag: originalIndex)
      vc.tabBarItem.badgeValue = tabData.badge
      vc.tabBarItem.accessibilityIdentifier = tabData.testID

      // Badge colors (iOS 16+)
      if let badgeBgColor = tabData.badgeBackgroundColor {
        vc.tabBarItem.badgeColor = UIColor(rgb: Int(badgeBgColor))
      }
      if #available(iOS 16.0, *), let badgeTxtColor = tabData.badgeTextColor {
        let badge = vc.tabBarItem
        badge?.setBadgeTextAttributes(
          [.foregroundColor: UIColor(rgb: Int(badgeTxtColor))],
          for: .normal
        )
      }

      let inactiveColor = colorFromHex(inactiveTintColor)
      let attributes = TabBarFontSize.createNormalStateAttributes(
        fontSize: fontSize.map { Int($0) },
        fontFamily: fontFamily,
        fontWeight: fontWeight,
        inactiveColor: inactiveColor
      )
      vc.tabBarItem.setTitleTextAttributes(attributes, for: .normal)

      viewControllers.append(vc)
    }

    let animated = !(disablePageAnimations ?? false)
    if animated {
      tbc.setViewControllers(viewControllers, animated: false)
    } else {
      UIView.performWithoutAnimation {
        tbc.setViewControllers(viewControllers, animated: false)
      }
    }

    updateSelectedTab()
  }

  // MARK: - Selection

  private func updateSelectedTab() {
    guard let tbc = tabBarController,
          let selectedPage else { return }

    let filtered = filteredItems
    guard let index = filtered.firstIndex(where: { $0.key == selectedPage }) else { return }

    if disablePageAnimations ?? false {
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
    tabBar.isTranslucent = translucent ?? true
    tabBar.unselectedItemTintColor = colorFromHex(inactiveTintColor)

    guard let tabBarItems = tabBar.items else { return }

    let attributes = TabBarFontSize.createNormalStateAttributes(
      fontSize: fontSize.map { Int($0) },
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

    if translucent == false {
      appearance.configureWithOpaqueBackground()
    }

    if let bgColor = colorFromHex(barTintColor) {
      appearance.backgroundColor = bgColor
    }

    let itemAppearance = UITabBarItemAppearance()
    let inactiveColor = colorFromHex(inactiveTintColor)

    let attributes = TabBarFontSize.createNormalStateAttributes(
      fontSize: fontSize.map { Int($0) },
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
       let tabData = (items ?? []).findByKey(selectedPage),
       let perTabColor = tabData.activeTintColor {
      tintColor = UIColor(rgb: Int(perTabColor))
    }

    if let tintColor {
      tbc.tabBar.tintColor = tintColor
    }
  }

  private func updateTabBarHidden() {
    guard let tbc = tabBarController else { return }
    let hidden = tabBarHidden ?? false
    tbc.tabBar.isHidden = hidden
    tbc.tabBar.isUserInteractionEnabled = !hidden

    if !hidden {
      onTabBarMeasured?(Double(tbc.tabBar.frame.size.height))
    }
  }

  private func updateSidebarAdaptable() {
    guard let tbc = tabBarController else { return }
    if #available(iOS 18.0, *) {
      #if compiler(>=6.0)
      tbc.mode = (sidebarAdaptable ?? false) ? .tabSidebar : .tabBar
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
    guard hapticFeedbackEnabled ?? false else { return }

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
    guard let imageLoader, let iconSources = icons else { return }

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

  // MARK: - Tab delegate callback

  func handleTabSelected(at index: Int) {
    let filtered = filteredItems
    guard let tabData = filtered[safe: index] else { return }
    onPageSelected?(tabData.key)
    emitHapticFeedback()
  }

  func shouldSelectTab(at index: Int, isReselection: Bool) -> Bool {
    let filtered = filteredItems
    guard let tabData = filtered[safe: index] else { return false }

    if isReselection {
      onPageSelected?(tabData.key)
      emitHapticFeedback()
      return false
    }

    onPageSelected?(tabData.key)
    emitHapticFeedback()
    return !(tabData.preventsDefault ?? false)
  }

  // MARK: - Bottom accessory placement

  func handlePlacementChanged(_ placement: String) {
    // Find the HybridBottomAccessoryView component in the bottom accessory view hierarchy
    // and notify it of the placement change
    guard let accessoryView = bottomAccessoryView else { return }
    notifyPlacementChanged(in: accessoryView, placement: placement)
  }

  private func notifyPlacementChanged(in view: UIView, placement: String) {
    // The BottomAccessoryView's component view has a HybridBottomAccessoryView
    // whose onPlacementChanged callback we need to invoke.
    // Since the HybridBottomAccessoryView is managed by Nitro, the callback
    // is set via props. We look for a well-known notification pattern.
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

// MARK: - UIColor from int

extension UIColor {
  convenience init(rgb: Int) {
    self.init(
      red: CGFloat((rgb >> 16) & 0xFF) / 255.0,
      green: CGFloat((rgb >> 8) & 0xFF) / 255.0,
      blue: CGFloat(rgb & 0xFF) / 255.0,
      alpha: CGFloat((rgb >> 24) & 0xFF) / 255.0
    )
  }
}

// MARK: - TabItemStruct findByKey extension

extension Collection where Element == TabItemStruct {
  func findByKey(_ key: String?) -> Element? {
    guard let key else { return nil }
    return first { $0.key == key }
  }
}

// MARK: - UITabBarControllerDelegate proxy

// We use a shared proxy because HybridTabView is not an NSObject subclass
// (it extends HybridTabViewSpec_base from Nitro) and cannot directly conform
// to UITabBarControllerDelegate.
class DelegateProxy: NSObject, UITabBarControllerDelegate {
  static let shared = DelegateProxy()
  private var mapping: [ObjectIdentifier: HybridTabView] = [:]

  func register(_ hybridView: HybridTabView, for controller: UITabBarController) {
    mapping[ObjectIdentifier(controller)] = hybridView
  }

  func unregister(for controller: UITabBarController) {
    mapping.removeValue(forKey: ObjectIdentifier(controller))
  }

  func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
    guard let hybridView = mapping[ObjectIdentifier(tabBarController)] else { return false }
    let isReselection = tabBarController.selectedViewController == viewController

    if let index = tabBarController.viewControllers?.firstIndex(of: viewController) {
      return hybridView.shouldSelectTab(at: index, isReselection: isReselection)
    }

    return false
  }

  // iOS 26: Bottom accessory placement change
  // TODO: Re-enable when UITabBarController.BottomAccessoryPlacement is available in the SDK
  // #if compiler(>=6.2)
  // @available(iOS 26.0, *)
  // func tabBarController(
  //   _ tabBarController: UITabBarController,
  //   placementDidChange placement: UITabBarController.BottomAccessoryPlacement,
  //   forBottomAccessoryView accessoryView: UIView
  // ) {
  //   guard let hybridView = mapping[ObjectIdentifier(tabBarController)] else { return }
  //
  //   var placementString = "none"
  //   if placement == .inline {
  //     placementString = "inline"
  //   } else if placement == .expanded {
  //     placementString = "expanded"
  //   }
  //
  //   hybridView.handlePlacementChanged(placementString)
  // }
  // #endif
}

// MARK: - Layout-aware UIView

private class TabViewLayoutView: UIView {
  var layoutCallback: (() -> Void)?

  override func layoutSubviews() {
    super.layoutSubviews()
    layoutCallback?()
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
