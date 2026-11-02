import UIKit
import XCTest
import OneKeyImage
@testable import NativeListModule

@MainActor
final class NativeListSharedIconTests: XCTestCase {
  private func field<T>(_ object: Any, _ name: String, as type: T.Type = T.self) throws -> T {
    var mirror: Mirror? = Mirror(reflecting: object)
    while let current = mirror {
      if let child = current.children.first(where: { $0.label == name }) { return try XCTUnwrap(child.value as? T) }
      mirror = current.superclassMirror
    }
    throw NSError(domain: "Missing field " + name, code: 1)
  }
  private func binding(_ button: UIButton) throws -> Any {
    let mirror = try XCTUnwrap(Mirror(reflecting: button).superclassMirror)
    let storage = try XCTUnwrap(mirror.children.first { $0.label?.contains("iconBinding") == true }?.value)
    return try XCTUnwrap(Mirror(reflecting: storage).children.first?.value)
  }
  private func eventually(_ message: String, _ condition: () -> Bool) {
    let end = Date().addingTimeInterval(5)
    while !condition(), Date() < end { RunLoop.main.run(until: Date().addingTimeInterval(0.01)) }
    XCTAssertTrue(condition(), message)
  }
  private func window() throws -> UIWindow {
    guard UIApplication.perform(NSSelectorFromString("sharedApplication"))?.takeUnretainedValue() is UIApplication else {
      throw XCTSkip("A UIApplication host is required for attached glyph acceptance")
    }
    let window = UIWindow(frame: UIScreen.main.bounds)
    window.rootViewController = UIViewController()
    window.makeKeyAndVisible()
    return window
  }
  private func save(_ image: UIImage, _ name: String) throws {
    guard let directory = ProcessInfo.processInfo.environment["ICON_EVIDENCE_DIR"] else { return }
    try XCTUnwrap(image.pngData()).write(to: URL(fileURLWithPath: directory).appendingPathComponent(name + ".png"))
  }
  private func bind(_ stack: NativeListAccessoryStack, amount: String = "1", name: String = "DotVerSolid", kind: String = "icon", disabled: Bool = false) throws {
    let descriptors: [[String: Any]] = [
      ["kind": "valuePair", "primary": amount, "secondary": "$1"],
      ["kind": kind, "name": name, "text": "Text", "buttonStyle": "tertiary", "actionKey": "menu", "disabled": disabled],
    ]
    let item = try NativeListItem(data: ["key": "swap", "type": "identity", "title": "USDC", "height": 60, "trailing": descriptors])
    stack.axis = .horizontal
    stack.bind(item, descriptors: descriptors, theme: nil,
      style: ["horizontalPadding": 20, "verticalPadding": 8, "leadingGap": 12, "trailingGap": 8],
      checkboxState: { _, _, fallback in fallback }, fonts: NativeListFontFamilies())
    stack.setNeedsLayout(); stack.layoutIfNeeded()
  }
  func testDetachedMeasurementAndRealSwapRebindPreservePendingAndReadyImage() throws {
    let service = OneKeyNamedSVG.shared
    let metadataLock: NSLock = try field(service, "metadataLock")
    let stateLock: NSLock = try field(service, "lock")
    func state(_ owner: NSObject? = nil) throws -> (Int, Int) {
      stateLock.lock(); defer { stateLock.unlock() }
      let pending: Any = try field(service, "pending")
      let keys = Mirror(reflecting: pending).children.compactMap { Mirror(reflecting: $0.value).children.first?.value as? ObjectIdentifier }
      return (try field(service, "workers"), owner.map { owner in keys.filter { $0 == ObjectIdentifier(owner) }.count } ?? keys.count)
    }
    metadataLock.lock()
    var locked = true
    defer { if locked { metadataLock.unlock() } }
    let baseline = try state()
    let stack = NativeListAccessoryStack(frame: CGRect(x: 10, y: 80, width: 180, height: 44))
    try bind(stack)
    let button = try XCTUnwrap(stack.subviews.compactMap { $0 as? NativeListAccessoryButton }.first { !$0.isHidden && $0.image(for: .normal) != nil })
    let slot = try binding(button)
    let owner: NSObject = try field(slot, "owner")
    XCTAssertEqual(try state(owner).0, baseline.0)
    XCTAssertEqual(try state(owner).1, 0)
    let beforeSize = try XCTUnwrap(button.image(for: .normal)).size
    let detachedSize = stack.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
    XCTAssertEqual(try state(owner).0, baseline.0)
    XCTAssertEqual(try state(owner).1, 0)
    let window = try window(); defer { window.isHidden = true }
    do {
      defer { metadataLock.unlock(); locked = false }
      window.rootViewController!.view.addSubview(stack)
      stack.layoutIfNeeded()
      XCTAssertGreaterThan(try state(owner).0 + state(owner).1, baseline.0)
      let bounds = button.bounds.size
      let revision: UInt64 = try field(slot, "revision")
      for index in 0..<100 { try bind(stack, amount: String(index)) }
      XCTAssertEqual(try field(slot, "revision", as: UInt64.self), revision)
      XCTAssertEqual(button.image(for: .normal)?.size, beforeSize)
      XCTAssertEqual(button.bounds.size, bounds)
      XCTAssertEqual(stack.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).height, detachedSize.height)
    }
    eventually("Attached Swap glyph has an actual raster") { button.image(for: .normal)?.cgImage != nil && button.image(for: .normal)?.size == beforeSize && (try? self.field(slot, "currentImage", as: UIImage?.self)) != nil }
    // A transparent measurement placeholder also has a CGImage: require painted pixels.
    eventually("Decoded glyph is visible") { self.alphaCount(button.image(for: .normal)) > 0 }
    let ready = try XCTUnwrap(button.image(for: .normal))
    XCTAssertEqual(maximumAlpha(ready), 255)
    try bind(stack, amount: "101")
    XCTAssertTrue(button.image(for: .normal) === ready)
    button.tintColor = .blue
    XCTAssertTrue(button.image(for: .normal) === ready)
    XCTAssertNotNil(button.image(for: .disabled))
    try bind(stack, amount: "101")
    func captureButton() -> UIImage {
      let rect = button.convert(button.bounds, to: stack)
      return UIGraphicsImageRenderer(size: button.bounds.size).image { context in
        context.cgContext.translateBy(x: -rect.minX, y: -rect.minY)
        stack.drawHierarchy(in: stack.bounds, afterScreenUpdates: true)
      }
    }
    try save(captureButton(), "ios-swap-menu-normal")
    var actions = 0
    stack.onAction = { _, _, _, _ in actions += 1 }
    try bind(stack, amount: "101", disabled: true)
    XCTAssertFalse(button.isEnabled); XCTAssertEqual(button.alpha, 0.4, accuracy: 0.000001)
    XCTAssertTrue(button.image(for: .normal) === ready)
    button.sendActions(for: .touchUpInside); XCTAssertEqual(actions, 0)
    button.layoutIfNeeded()
    try save(captureButton(), "ios-swap-menu-disabled")
    try bind(stack, amount: "101")
    try bind(stack, name: "UnknownIcon")
    XCTAssertNil(button.image(for: .normal))
    try bind(stack, kind: "value")
    XCTAssertNil(button.image(for: .normal))
    stack.removeFromSuperview()
    RunLoop.main.run(until: Date().addingTimeInterval(0.05))
    XCTAssertNil(button.image(for: .normal))
  }
  private func alphaCount(_ image: UIImage?) -> Int {
    guard let cg = image?.cgImage else { return 0 }
    var bytes = [UInt8](repeating: 0, count: cg.width * cg.height * 4)
    guard let context = CGContext(data: &bytes, width: cg.width, height: cg.height, bitsPerComponent: 8,
      bytesPerRow: cg.width * 4, space: CGColorSpaceCreateDeviceRGB(), bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue) else { return 0 }
    context.draw(cg, in: CGRect(x: 0, y: 0, width: cg.width, height: cg.height))
    return stride(from: 3, to: bytes.count, by: 4).filter { bytes[$0] > 0 }.count
  }
  private func maximumAlpha(_ image: UIImage) -> UInt8 {
    guard let cg = image.cgImage else { return 0 }
    var bytes = [UInt8](repeating: 0, count: cg.width * cg.height * 4)
    guard let context = CGContext(data: &bytes, width: cg.width, height: cg.height, bitsPerComponent: 8,
      bytesPerRow: cg.width * 4, space: CGColorSpaceCreateDeviceRGB(), bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue) else { return 0 }
    context.draw(cg, in: CGRect(x: 0, y: 0, width: cg.width, height: cg.height))
    return stride(from: 3, to: bytes.count, by: 4).map { bytes[$0] }.max() ?? 0
  }
  func testLifecycleLegacyModesAndSizeOverride() throws {
    let window = try window(); defer { window.isHidden = true }
    let view = NativeListIconImageView(frame: CGRect(x: 10, y: 80, width: 64, height: 64))
    window.rootViewController!.view.addSubview(view)
    for name in ["AppleBrand", "BotIllus", "GoogleIllus", "AccountErrorCustom", "StarOutline", "StarSolid", "BadgeVerifiedSolid", "CheckboxCheckedCustom", "CheckboxIndeterminateCustom"] {
      view.bindIcon(name)
      eventually(name) { self.alphaCount(view.image) > 0 }
      let metadata = try XCTUnwrap(nativeListIconMetadata(named: name))
      XCTAssertEqual(view.image?.size, metadata.size)
      if !metadata.original { XCTAssertEqual(maximumAlpha(try XCTUnwrap(view.image)), 255) }
      XCTAssertEqual(view.image?.renderingMode, metadata.original ? .alwaysOriginal : .alwaysTemplate)
      let image = try XCTUnwrap(view.image)
      try save(metadata.original ? image : image.withTintColor(.red, renderingMode: .alwaysOriginal), "ios-new-" + name)
    }
    view.bindIcon("AppleBrand", size: CGSize(width: 28, height: 20))
    eventually("Non-square source stretches to legacy explicit size") { self.alphaCount(view.image) > 0 }
    XCTAssertEqual(view.image?.size, CGSize(width: 28, height: 20))
    XCTAssertEqual(view.image?.renderingMode, .alwaysTemplate)
    try save(try XCTUnwrap(view.image).withTintColor(.red, renderingMode: .alwaysOriginal), "ios-new-AppleBrand-override")
    view.bindIcon("DotVerSolid")
    view.removeFromSuperview()
    RunLoop.main.run(until: Date().addingTimeInterval(0.1))
    XCTAssertEqual(alphaCount(view.image), 0)
    window.rootViewController!.view.addSubview(view)
    eventually("Same glyph redraws after detached completion") { self.alphaCount(view.image) > 0 }
    view.bindIcon("UnknownIcon"); XCTAssertNil(view.image)
    let ordinary = UIGraphicsImageRenderer(size: CGSize(width: 8, height: 8)).image { c in UIColor.green.setFill(); c.fill(CGRect(x: 0, y: 0, width: 8, height: 8)) }
    view.image = ordinary
    XCTAssertTrue(view.image === ordinary)
  }
  func testLegacyCompiledAssetRasterComparison() throws {
    guard let directory = ProcessInfo.processInfo.environment["NATIVE_LIST_LEGACY_BUNDLE"], let bundle = Bundle(path: directory) else {
      throw XCTSkip("An archived pre-migration resource bundle is required for raster comparison")
    }
    let window = try window(); defer { window.isHidden = true }
    let view = NativeListIconImageView(frame: CGRect(x: 0, y: 80, width: 96, height: 96))
    window.rootViewController!.view.addSubview(view)
    let names = [
      "AppleBrand": "onekey_selector_apple_brand", "BotIllus": "onekey_selector_bot_illus",
      "GoogleIllus": "onekey_selector_google_illus", "AccountErrorCustom": "onekey_selector_account_error_custom",
      "BadgeVerifiedSolid": "onekey_badge_verified_solid", "StarOutline": "onekey_star", "StarSolid": "onekey_star_solid",
      "CheckboxCheckedCustom": "onekey_checkbox_checked", "CheckboxIndeterminateCustom": "onekey_checkbox_indeterminate",
    ]
    func raster(_ image: UIImage) -> UIImage {
      let format = UIGraphicsImageRendererFormat(); format.scale = 1
      return UIGraphicsImageRenderer(size: CGSize(width: 96, height: 96), format: format).image { _ in
        image.draw(in: CGRect(x: 0, y: 0, width: 96, height: 96))
      }
    }
    for name in names.keys.sorted() {
      let metadata = try XCTUnwrap(nativeListIconMetadata(named: name))
      let old = try XCTUnwrap(UIImage(named: names[name]!, in: bundle, compatibleWith: nil))
      view.bindIcon(name)
      eventually("Shared raster for legacy comparison " + name) { self.alphaCount(view.image) > 0 }
      let next = try XCTUnwrap(view.image)
      XCTAssertEqual(next.size, old.size)
      let before = raster(metadata.original ? old : old.withTintColor(.red, renderingMode: .alwaysOriginal))
      let after = raster(metadata.original ? next : next.withTintColor(.red, renderingMode: .alwaysOriginal))
      XCTAssertGreaterThan(alphaCount(before), 0); XCTAssertGreaterThan(alphaCount(after), 0)
      if name == "AccountErrorCustom" {
        // The source eye center is inside its 0.447-opacity fill, away from raster edges.
        for image in [before, after] {
          let cg = try XCTUnwrap(image.cgImage)
          var bytes = [UInt8](repeating: 0, count: 96 * 96 * 4)
          let context = try XCTUnwrap(CGContext(data: &bytes, width: 96, height: 96, bitsPerComponent: 8,
            bytesPerRow: 96 * 4, space: CGColorSpaceCreateDeviceRGB(), bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue))
          context.draw(cg, in: CGRect(x: 0, y: 0, width: 96, height: 96))
          for (x,y) in [(6,48),(48,6),(48,91),(91,64),(67,61)] {
            let index = (y * 96 + x) * 4
            XCTAssertEqual(Array(bytes[index..<(index+3)]), [0,0,0])
            XCTAssertEqual(bytes[index+3], 114)
          }
          for (x,y) in [(48,48),(15,48)] { XCTAssertEqual(bytes[(y * 96 + x) * 4 + 3], 0) }
        }
      } else { XCTAssertEqual(maximumAlpha(after), maximumAlpha(before)) }
      try save(before, "ios-before-" + name); try save(after, "ios-after-" + name)
    }
  }

}
