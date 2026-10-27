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

// MARK: - RCTBottomTabsContainerView

class RCTBottomTabsContainerView: UIView {

  // MARK: - Internal State

  private var tabBarController: UITabBarController?
  private var childViews: [UIView] = []
  private var bottomAccessoryView: UIView?
  private var loadedIcons: [Int: UIImage] = [:]
  // Solid/focused icons shown for the selected tab via UITabBarItem.selectedImage.
  // Loaded once and swapped natively by UIKit on selection, so the focused
  // artwork rides the system selection animation instead of waiting for a JS
  // round-trip + async reload.
  private var loadedSelectedIcons: [Int: UIImage] = [:]
  private var imageLoader: RCTImageLoaderProtocol?
  private let iconSize = CGSize(width: 27, height: 27)
  // Double press is derived from consecutive selection requests so no extra
  // recognizer competes with the system tab bar gestures (iOS 26 drag-to-switch).
  private static let doublePressInterval: CFTimeInterval = 0.3
  private var lastPressedTabKey: String?
  private var lastPressedTabTime: CFTimeInterval = 0
  /// Cached view controllers keyed by tab key to avoid unnecessary reparenting
  private var cachedViewControllers: [String: UIViewController] = [:]
  // Vertical tab bar (iPhone Duo side strip): last reported placement and the
  // round accessory button drawn above the bar.
  private var reportedTabBarPlacement: String?
  private var sideAccessoryButton: UIButton?
  private var loadedSideAccessoryIcon: UIImage?
  private var isSideAccessoryLayoutScheduled = false
  private static let sideAccessorySize: CGFloat = 48
  private static let sideAccessorySpacing: CGFloat = 12

  // MARK: - Props (set by Fabric ComponentView via KVC)

  @objc var items: [NSDictionary]? {
    didSet { rebuildViewControllers() }
  }

  @objc var selectedPage: String? {
    didSet { updateSelectedTab() }
  }

  @objc var icons: [NSDictionary]? {
    didSet { loadIconsFromSources() }
  }

  @objc var selectedIcons: [NSDictionary]? {
    didSet { loadSelectedIconsFromSources() }
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
    didSet { updateTabBarHidden() }
  }

  @objc var translucent: NSNumber? {
    didSet { updateTabBarAppearance() }
  }

  @objc var barTintColor: UIColor? {
    didSet { updateTabBarAppearance() }
  }

  @objc var activeTintColor: UIColor? {
    // Rebuild so the baked selectedImage tint follows light/dark theme changes.
    didSet { updateTintColors(); rebuildViewControllers() }
  }

  @objc var inactiveTintColor: UIColor? {
    // Rebuild so the baked unselected-image tint follows theme changes.
    didSet {
      updateTabBarAppearance()
      rebuildViewControllers()
      sideAccessoryButton?.tintColor = inactiveTintColor
    }
  }

  @objc var rippleColor: UIColor? // Android only

  @objc var activeIndicatorColor: UIColor? // Android only

  @objc var fontFamily: String? {
    didSet { updateTabBarAppearance() }
  }

  @objc var fontWeight: String? {
    didSet { updateTabBarAppearance() }
  }

  @objc var fontSize: NSNumber? {
    didSet { updateTabBarAppearance() }
  }

  @objc var sideAccessoryIcon: NSDictionary? {
    didSet { loadSideAccessoryIcon() }
  }

  @objc var sideAccessoryLabel: String? {
    didSet { sideAccessoryButton?.accessibilityLabel = sideAccessoryLabel }
  }

  // MARK: - Events (RCTDirectEventBlock)

  @objc var onPageSelected: RCTDirectEventBlock?
  @objc var onTabDoublePress: RCTDirectEventBlock?
  @objc var onTabBarMeasured: RCTDirectEventBlock?
  @objc var onNativeLayout: RCTDirectEventBlock?
  @objc var onTabBarPlacementChange: RCTDirectEventBlock?
  @objc var onSideAccessoryPress: RCTDirectEventBlock?

  // MARK: - Init

  override init(frame: CGRect) {
    super.init(frame: frame)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
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
    handleLayout()
  }

  // MARK: - Bottom Accessory (iOS 26+)

  private func attachBottomAccessory() {
    // TODO: Re-enable when UITabBar.bottomAccessoryView is available in the SDK
  }

  private func detachBottomAccessory() {
    // TODO: Re-enable when UITabBar.bottomAccessoryView is available in the SDK
  }

  // MARK: - Vertical tab bar (iOS 27.1+)

  /// Where the system draws the tab bar: "bottom", or "leading"/"trailing" for
  /// the vertical bar some devices (iPhone Duo) place in a side strip.
  private func currentTabBarPlacement() -> String {
    #if compiler(>=6.4)
    if #available(iOS 27.1, *), let tbc = tabBarController {
      switch tbc.traitCollection.verticalBarEdge {
      case .leading: return "leading"
      case .trailing: return "trailing"
      default: break
      }
    }
    #endif
    return "bottom"
  }

  private func registerVerticalBarTraitChanges(_ tbc: UITabBarController) {
    #if compiler(>=6.4)
    if #available(iOS 27.1, *) {
      tbc.registerForTraitChanges(
        UITraitCollection.systemTraitsAffectingVerticalBarEdge
      ) { [weak self] (_: UITabBarController, _: UITraitCollection) in
        self?.scheduleSideAccessoryLayout()
      }
    }
    #endif
  }

  /// Coalesces requests and runs after UIKit has laid out the bar, whose
  /// floating platter is positioned in UIKit's own layout pass.
  private func scheduleSideAccessoryLayout() {
    guard !isSideAccessoryLayoutScheduled else { return }
    isSideAccessoryLayoutScheduled = true
    DispatchQueue.main.async { [weak self] in
      guard let self else { return }
      self.isSideAccessoryLayoutScheduled = false
      self.updateTabBarPlacementAndSideAccessory()
    }
  }

  private func updateTabBarPlacementAndSideAccessory() {
    guard let tbc = tabBarController else { return }
    let placement = currentTabBarPlacement()
    if placement != reportedTabBarPlacement {
      reportedTabBarPlacement = placement
      onTabBarPlacementChange?(["placement": placement])
    }

    guard placement != "bottom",
          !tabBarHidden,
          let icon = loadedSideAccessoryIcon,
          let platterFrame = verticalTabBarPlatterFrame(in: tbc) else {
      sideAccessoryButton?.isHidden = true
      return
    }

    let button = ensureSideAccessoryButton(in: tbc.view)
    button.configuration?.image = icon
    let size = RCTBottomTabsContainerView.sideAccessorySize
    button.frame = CGRect(
      x: platterFrame.midX - size / 2,
      y: platterFrame.minY - RCTBottomTabsContainerView.sideAccessorySpacing - size,
      width: size,
      height: size
    )
    button.isHidden = false
    tbc.view.bringSubviewToFront(button)
  }

  /// UIKit draws the vertical bar items in a floating platter it does not
  /// expose. Read that view's frame (no private API is called) and return nil
  /// when it cannot be found, so the accessory hides instead of misplacing.
  private func verticalTabBarPlatterFrame(in tbc: UITabBarController) -> CGRect? {
    let root = tbc.view!
    // Tab content can hold large React trees; the bar lives outside them.
    let contentViews = Set((tbc.viewControllers ?? []).compactMap { vc in
      vc.isViewLoaded ? ObjectIdentifier(vc.view) : nil
    })
    var queue: [UIView] = root.subviews
    var index = 0
    while index < queue.count {
      let view = queue[index]
      index += 1
      if view === sideAccessoryButton || contentViews.contains(ObjectIdentifier(view)) {
        continue
      }
      if NSStringFromClass(type(of: view)).contains("TabBarItemPlatterView") {
        guard !view.isHidden, view.window != nil else { return nil }
        let frame = view.convert(view.bounds, to: root)
        return frame.isEmpty ? nil : frame
      }
      queue.append(contentsOf: view.subviews)
    }
    return nil
  }

  private func ensureSideAccessoryButton(in parent: UIView) -> UIButton {
    if let button = sideAccessoryButton {
      if button.superview !== parent { parent.addSubview(button) }
      return button
    }
    var configuration: UIButton.Configuration
    #if compiler(>=6.2)
    if #available(iOS 26.0, *) {
      configuration = .glass()
    } else {
      configuration = .gray()
    }
    #else
    configuration = .gray()
    #endif
    configuration.cornerStyle = .capsule
    configuration.contentInsets = .zero
    let button = UIButton(configuration: configuration)
    button.tintColor = inactiveTintColor
    button.accessibilityLabel = sideAccessoryLabel
    button.addAction(UIAction { [weak self] _ in
      self?.onSideAccessoryPress?([:])
    }, for: .primaryActionTriggered)
    parent.addSubview(button)
    sideAccessoryButton = button
    return button
  }

  private func loadSideAccessoryIcon() {
    let source = sideAccessoryIcon.map { IconSource(dict: $0) }
    guard let source, let url = URL(string: source.uri) else {
      loadedSideAccessoryIcon = nil
      scheduleSideAccessoryLayout()
      return
    }
    // The image loader arrives with the first state update; setImageLoader
    // calls this again.
    guard let imageLoader else { return }
    imageLoader.loadImage(
      with: URLRequest(url: url),
      size: CGSize(width: source.width, height: source.height),
      scale: CGFloat(source.scale),
      clipped: true,
      resizeMode: RCTResizeMode.contain,
      progressBlock: { _, _ in },
      partialLoad: { _ in },
      completionBlock: { [weak self] error, image in
        guard error == nil, let image else { return }
        DispatchQueue.main.async {
          guard let self else { return }
          self.loadedSideAccessoryIcon = image
            .resizeImageTo(size: self.iconSize)?
            .withRenderingMode(.alwaysTemplate)
          self.scheduleSideAccessoryLayout()
        }
      })
  }

  // MARK: - Lifecycle

  private func handleLayout() {
    let bounds = self.bounds
    guard bounds.width > 0 && bounds.height > 0 else { return }

    setupTabBarControllerIfNeeded()
    tabBarController?.view.frame = bounds
    onNativeLayout?(["width": Double(bounds.width), "height": Double(bounds.height)])
    scheduleSideAccessoryLayout()
  }

  private func setupTabBarControllerIfNeeded() {
    guard tabBarController == nil else { return }
    guard let parentVC = self.reactViewController() else { return }

    let tbc = UITabBarController()
    tbc.delegate = BottomTabsDelegateProxy.shared
    BottomTabsDelegateProxy.shared.register(self, for: tbc)
    tbc.view.backgroundColor = .clear

    parentVC.addChild(tbc)
    self.addSubview(tbc.view)
    tbc.view.frame = self.bounds
    tbc.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    tbc.didMove(toParent: parentVC)

    self.tabBarController = tbc
    registerVerticalBarTraitChanges(tbc)

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

  // MARK: - Tab management

  private var parsedItems: [TabItem] {
    (items ?? []).map { TabItem(dict: $0) }
  }

  private var filteredItems: [TabItem] {
    parsedItems.filter { !$0.hidden || $0.key == selectedPage }
  }

  private func rebuildViewControllers() {
    guard let tbc = tabBarController else { return }

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
      } else {
        vc = UIViewController()
        vc.view.backgroundColor = .clear
        vc.view.clipsToBounds = true
        cachedViewControllers[tabData.key] = vc
      }

      // Attach child view if not already attached to this VC
      if originalIndex < childViews.count {
        let childView = childViews[originalIndex]
        if childView.superview !== vc.view {
          childView.removeFromSuperview()
          vc.view.addSubview(childView)
          // Use autoresizingMask instead of Auto Layout constraints — Fabric sets
          // frames directly and constraints conflict with that on subsequent mounts.
          childView.translatesAutoresizingMaskIntoConstraints = true
          childView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
          childView.frame = vc.view.bounds
        }
      }

      // Configure tab bar item
      let icon = loadedIcons[originalIndex]
      let selectedIcon = loadedSelectedIcons[originalIndex]
      let sfSymbol = tabData.sfSymbol ?? ""

      var tabImage: UIImage?
      if let icon {
        tabImage = icon
      } else if !sfSymbol.isEmpty {
        tabImage = UIImage(systemName: sfSymbol)
      }

      // Focused (solid) artwork for the selected state. Fall back to the
      // unfocused image when no selected icon was provided so the selected
      // tab still renders something.
      var selectedTabImage: UIImage?
      if let selectedIcon {
        selectedTabImage = selectedIcon
      } else if !sfSymbol.isEmpty {
        selectedTabImage = UIImage(systemName: sfSymbol)
      }

      // Bake the inactive/active colors directly into the images as
      // .alwaysOriginal. iOS 26 Liquid Glass ignores appearance.normal.iconColor
      // and washes every icon to a single near-white tint, which is why the
      // unselected tabs never render the gray `iconSubdued`. Pre-tinting the
      // artwork and marking it .alwaysOriginal stops the system from re-tinting,
      // restoring the gray(unselected) → active(selected) color change natively.
      let normalImage = bakeTint(tabImage, inactiveTintColor)
      let selectedImage = bakeTint(selectedTabImage ?? tabImage, activeTintColor)

      let isLabeled = labeled?.boolValue ?? true
      let title = isLabeled ? tabData.title : nil
      vc.tabBarItem = UITabBarItem(title: title, image: normalImage, selectedImage: selectedImage)
      vc.tabBarItem.tag = originalIndex
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

      let inactiveColor = inactiveTintColor
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
      if disablePageAnimations {
        UIView.performWithoutAnimation {
          tbc.setViewControllers(viewControllers, animated: false)
        }
      } else {
        tbc.setViewControllers(viewControllers, animated: false)
      }
    }

    updateSelectedTab()
    // The platter height follows the number of visible tabs.
    scheduleSideAccessoryLayout()
  }

  // MARK: - Selection

  private func updateSelectedTab() {
    guard let tbc = tabBarController,
          let selectedPage else { return }

    let filtered = filteredItems
    guard let index = filtered.firstIndex(where: { $0.key == selectedPage }) else { return }

    // Common case: the user tapped the tab. UIKit's delegate already selected
    // it optimistically and started the iOS 26 Liquid Glass capsule animation.
    // Re-assigning selectedIndex here — especially wrapped in
    // performWithoutAnimation — would cancel that in-flight animation, which is
    // what made the icon/color change lag a beat behind the glass. When the
    // selection is already correct, just refresh tint and leave the native
    // animation alone.
    if tbc.selectedIndex == index {
      updateTintColors()
      return
    }

    // Programmatic selection (deep link, external navigation, preventsDefault
    // tabs): keep the existing no-animation behavior to avoid the historically
    // reverted tab-switch animation regression.
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
    tabBar.barTintColor = barTintColor
    tabBar.isTranslucent = translucent?.boolValue ?? true
    tabBar.unselectedItemTintColor = inactiveTintColor

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

    if let bgColor = barTintColor {
      appearance.backgroundColor = bgColor
    }

    let itemAppearance = UITabBarItemAppearance()
    let inactiveColor = inactiveTintColor

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

    var tintColor = activeTintColor
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
    guard let tbc = tabBarController else { return }
    // Keep UIKit's tab bar visibility state in sync on iOS 18+.
    #if compiler(>=6.0)
    if #available(iOS 18.0, *) {
      tbc.setTabBarHidden(tabBarHidden, animated: false)
    } else {
      tbc.tabBar.isHidden = tabBarHidden
    }
    #else
    tbc.tabBar.isHidden = tabBarHidden
    #endif
    tbc.tabBar.isUserInteractionEnabled = !tabBarHidden

    if !tabBarHidden {
      onTabBarMeasured?(["height": Double(tbc.tabBar.frame.size.height)])
    }
    scheduleSideAccessoryLayout()
  }

  private func updateSidebarAdaptable() {
    guard let tbc = tabBarController else { return }
    if #available(iOS 18.0, *) {
      #if compiler(>=6.0)
      if sidebarAdaptable {
        tbc.mode = .tabSidebar
        // Remove compact override so sidebar can use the natural size class
        if UIDevice.current.userInterfaceIdiom == .pad {
          tbc.traitOverrides.remove(UITraitHorizontalSizeClass.self)
        }
      } else {
        tbc.mode = .tabBar
        // Force compact horizontal size class on iPad to keep TabBar at bottom
        // iPad defaults to .regular which causes iOS 18+ to show tabs at top
        if UIDevice.current.userInterfaceIdiom == .pad {
          tbc.traitOverrides.horizontalSizeClass = .compact
        }
      }
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

  private func emitHapticFeedback() {
    guard hapticFeedbackEnabled else { return }
    UISelectionFeedbackGenerator().selectionChanged()
  }

  // MARK: - Icon tinting

  /// Returns a copy of `image` recolored to `color` and marked
  /// `.alwaysOriginal`, so iOS 26 Liquid Glass won't override the icon color.
  /// Falls back to the untinted image when no color is provided.
  private func bakeTint(_ image: UIImage?, _ color: UIColor?) -> UIImage? {
    guard let image else { return nil }
    guard let color else { return image }
    return image
      .withRenderingMode(.alwaysTemplate)
      .withTintColor(color, renderingMode: .alwaysOriginal)
  }

  // MARK: - Icon loading

  @objc func setImageLoader(_ loader: RCTImageLoaderProtocol) {
    self.imageLoader = loader
    loadIconsFromSources()
    loadSelectedIconsFromSources()
    loadSideAccessoryIcon()
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

  private func loadSelectedIconsFromSources() {
    guard let imageLoader, let iconDicts = selectedIcons else { return }

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
            self.loadedSelectedIcons[index] = image.resizeImageTo(size: self.iconSize)
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

    onPageSelected?(["key": tabData.key])
    emitHapticFeedback()
    emitDoublePressIfNeeded(key: tabData.key)
    return isReselection ? false : !tabData.preventsDefault
  }

  private func emitDoublePressIfNeeded(key: String) {
    let now = CACurrentMediaTime()
    if lastPressedTabKey == key && now - lastPressedTabTime <= Self.doublePressInterval {
      lastPressedTabKey = nil
      onTabDoublePress?(["key": key])
      return
    }
    lastPressedTabKey = key
    lastPressedTabTime = now
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

  // MARK: - Fabric child management

  @objc func insertChild(_ child: UIView, atIndex index: Int) {
    if child is RCTBottomAccessoryContainerView {
      self.bottomAccessoryView = child
      attachBottomAccessory()
      return
    }
    guard index >= 0 && index <= childViews.count else { return }
    childViews.insert(child, at: index)
    rebuildViewControllers()
  }

  @objc func removeChild(atIndex index: Int) {
    guard index >= 0 && index < childViews.count else { return }
    let child = childViews[index]
    if child is RCTBottomAccessoryContainerView {
      detachBottomAccessory()
      self.bottomAccessoryView = nil
      return
    }
    // Fabric recycles this view for unrelated <View>s once it is unmounted.
    // Leaving the flexible mask on would let UIKit autoresize it to whatever
    // superview it lands in next (it showed up as page-sized "flakes").
    child.autoresizingMask = []
    childViews.remove(at: index)
    rebuildViewControllers()
  }
}

// OneKey patch: keep native tab selection immediate while suppressing UIKit's content crossfade.
private final class ImmediateTabTransitionAnimator: NSObject, UIViewControllerAnimatedTransitioning {
  static let shared = ImmediateTabTransitionAnimator()

  func transitionDuration(using transitionContext: UIViewControllerContextTransitioning?) -> TimeInterval {
    0
  }

  func animateTransition(using transitionContext: UIViewControllerContextTransitioning) {
    guard let toViewController = transitionContext.viewController(forKey: .to),
          let toView = transitionContext.view(forKey: .to) ?? toViewController.view else {
      transitionContext.completeTransition(false)
      return
    }

    toView.frame = transitionContext.finalFrame(for: toViewController)
    transitionContext.containerView.addSubview(toView)
    transitionContext.completeTransition(!transitionContext.transitionWasCancelled)
  }
}

// MARK: - UITabBarControllerDelegate proxy

class BottomTabsDelegateProxy: NSObject, UITabBarControllerDelegate {
  static let shared = BottomTabsDelegateProxy()
  private var mapping: [ObjectIdentifier: RCTBottomTabsContainerView] = [:]

  func register(_ view: RCTBottomTabsContainerView, for controller: UITabBarController) {
    mapping[ObjectIdentifier(controller)] = view
  }

  func unregister(for controller: UITabBarController) {
    mapping.removeValue(forKey: ObjectIdentifier(controller))
  }

  func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
    guard let containerView = mapping[ObjectIdentifier(tabBarController)] else {
      return false
    }
    let isReselection = tabBarController.selectedViewController == viewController

    if let index = tabBarController.viewControllers?.firstIndex(of: viewController) {
      return containerView.shouldSelectTab(at: index, isReselection: isReselection)
    }

    return false
  }

  func tabBarController(
    _ tabBarController: UITabBarController,
    animationControllerForTransitionFrom fromViewController: UIViewController,
    to toViewController: UIViewController
  ) -> UIViewControllerAnimatedTransitioning? {
    guard let containerView = mapping[ObjectIdentifier(tabBarController)],
          containerView.disablePageAnimations else {
      return nil
    }

    return ImmediateTabTransitionAnimator.shared
  }
}
