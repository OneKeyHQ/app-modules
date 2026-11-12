import UIKit
import SDWebImage
import XCTest

@testable import OneKeyImage

final class OneKeyIconTests: XCTestCase {
  private func resolve(_ name: String, color: String = "rgba(255,0,0,0.5019607843137255)",
                       width: Int = 128, height: Int = 128, scale: CGFloat = 1) -> UIImage? {
    let done = expectation(description: name)
    let owner = NSObject()
    var output: UIImage?
    withExtendedLifetime(owner) {
      OneKeyNamedSVG.shared.request(owner: owner, name: name, color: color,
        width: width, height: height, scale: scale) { image in
        output = image
        done.fulfill()
      }
      wait(for: [done], timeout: 5)
    }
    return output
  }

  private func resolveRender(_ name: String, color: String = "rgba(0,0,255,1)", scale: CGFloat = 1) throws -> OneKeyNamedSVG.RenderResult {
    let done = expectation(description: "Shape result")
    let owner = NSObject(); var result: OneKeyNamedSVG.RenderResult?
    OneKeyNamedSVG.shared.requestRender(owner: owner, name: name, color: color,
      width: 128, height: 128, scale: scale) { result = $0; done.fulfill() }
    withExtendedLifetime(owner) { wait(for: [done], timeout: 5) }
    return try XCTUnwrap(result)
  }

  private func draw(_ view: UIImageView) -> UIImage {
    let format = UIGraphicsImageRendererFormat(); format.scale = view.window?.screen.scale ?? 1
    return UIGraphicsImageRenderer(size: view.bounds.size, format: format).image { _ in
      view.drawHierarchy(in: view.bounds, afterScreenUpdates: true)
    }
  }

  private func field<T>(_ name: String, as type: T.Type = T.self) throws -> T {
    let value = try XCTUnwrap(Mirror(reflecting: OneKeyNamedSVG.shared).children.first { $0.label == name }?.value)
    return try XCTUnwrap(value as? T)
  }

  private func eventually(_ description: String, _ condition: () -> Bool) {
    let deadline = Date().addingTimeInterval(5)
    while !condition(), Date() < deadline { RunLoop.main.run(until: Date().addingTimeInterval(0.01)) }
    XCTAssertTrue(condition(), description)
  }

  private func save(_ image: UIImage, name: String) throws {
    guard let directory = ProcessInfo.processInfo.environment["ICON_EVIDENCE_DIR"] else { return }
    try image.pngData()?.write(to: URL(fileURLWithPath: directory).appendingPathComponent(name + ".png"))
  }

  private func pixels(_ image: UIImage) throws -> [UInt8] {
    let cgImage = try XCTUnwrap(image.cgImage)
    let width = cgImage.width, height = cgImage.height
    var bytes = [UInt8](repeating: 0, count: width * height * 4)
    let context = try XCTUnwrap(CGContext(data: &bytes, width: width, height: height,
      bitsPerComponent: 8, bytesPerRow: width * 4, space: CGColorSpaceCreateDeviceRGB(),
      bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue))
    context.draw(cgImage, in: CGRect(x: 0, y: 0, width: width, height: height))
    return bytes
  }

  func testDiskRasterSurvivesDecodedMemoryEviction() throws {
    let name = "DotVerSolid"
    let color = "rgba(0,0,0,1)"
    let scale: CGFloat = 1.375
    let ownerBundle = Bundle(for: HybridOneKeyIcon.self)
    let bundleURL = try XCTUnwrap(ownerBundle.url(forResource: "OneKeyIcons", withExtension: "bundle")
      ?? Bundle.main.url(forResource: "OneKeyIcons", withExtension: "bundle"))
    let bundle = try XCTUnwrap(Bundle(url: bundleURL))
    let manifestURL = try XCTUnwrap(bundle.url(forResource: "manifest", withExtension: "json"))
    let manifest = try XCTUnwrap(JSONSerialization.jsonObject(with: Data(contentsOf: manifestURL)) as? [String: [String: Any]])
    let entry = try XCTUnwrap(manifest[name])
    let digest = try XCTUnwrap(entry["digest"] as? String)
    let mode = entry["recolorMode"] as? String ?? "raster"
    let colorKey = mode == "raster" ? color : mode == "multiple" ? "1.0" : ""
    let key = "\(name)|\(digest)|37|29|\(scale)|\(mode)|\(colorKey)"
    let disk = SDImageCache(namespace: "onekey-named-svg-raster-v1")
    let original = try XCTUnwrap(resolve(name, color: color, width: 37, height: 29, scale: scale))
    eventually("Worker output is persisted as a PNG") { disk.diskImageData(forKey: key) != nil }
    let persisted = try XCTUnwrap(disk.imageFromDiskCache(forKey: key))
    XCTAssertEqual(persisted.cgImage?.width, 37)
    XCTAssertEqual(persisted.cgImage?.height, 29)
    XCTAssertEqual(try pixels(persisted), try pixels(original))

    // Exhaust the real decoded LRU; the next request must leave its memory fast path.
    for width in 122...250 {
      XCTAssertNotNil(resolve(name, color: color, width: width, height: 25, scale: scale))
    }
    XCTAssertNil(OneKeyNamedSVG.shared.cachedRender(name: name, color: color,
      width: 37, height: 29, scale: scale))
    let restored = try XCTUnwrap(resolve(name, color: color, width: 37, height: 29, scale: scale))
    XCTAssertEqual(try pixels(restored), try pixels(original))
  }

  func testNamedColorEndpointsRetainCSSAlpha() throws {
    for (alpha, expected) in [("1", 255), ("0.5", 128), ("0", 0)] {
      let image = try XCTUnwrap(resolve("DotVerSolid", color: "rgba(255,0,0,\(alpha))", width: 192, height: 192))
      let bytes = try pixels(image)
      let center = (96 * 192 + 96) * 4
      XCTAssertEqual(Int(bytes[center + 3]), expected, accuracy: 1)
      XCTAssertEqual(Int(bytes[center]), expected, accuracy: 1)
      XCTAssertEqual(bytes[center + 1], 0); XCTAssertEqual(bytes[center + 2], 0)
      try save(image, name: "named-alpha-" + alpha)
    }
    let mixed = try XCTUnwrap(resolve("OnekeyTextIllus", color: "rgba(255,0,0,0)", width: 128, height: 128))
    let bytes = try pixels(mixed)
    XCTAssertTrue(stride(from: 0, to: bytes.count, by: 4).contains { bytes[$0 + 1] > bytes[$0] && bytes[$0 + 3] == 255 })
  }

  func testAllBundledSDKFixturesDecode() throws {
    let ownerBundle = Bundle(for: HybridOneKeyIcon.self)
    let url = try XCTUnwrap(ownerBundle.url(forResource: "OneKeyIcons", withExtension: "bundle")
      ?? Bundle.main.url(forResource: "OneKeyIcons", withExtension: "bundle"))
    let bundle = try XCTUnwrap(Bundle(url: url))
    let manifestURL = try XCTUnwrap(bundle.url(forResource: "manifest", withExtension: "json"))
    let manifest = try XCTUnwrap(JSONSerialization.jsonObject(with: Data(contentsOf: manifestURL)) as? [String: Any])
    XCTAssertEqual(manifest.count, 63)
    let all = expectation(description: "All named resources")
    all.expectedFulfillmentCount = manifest.count
    let owners = manifest.keys.sorted().map { _ in NSObject() }
    var failed: [String] = []
    withExtendedLifetime(owners) {
      for (index, name) in manifest.keys.sorted().enumerated() {
        OneKeyNamedSVG.shared.request(owner: owners[index], name: name, color: "rgba(255,0,0,0.5019607843137255)",
          width: 128, height: 128, scale: 1) { image in
          if image == nil { failed.append(name) }
          all.fulfill()
        }
      }
      wait(for: [all], timeout: 60)
    }
    XCTAssertEqual(failed, [])
  }

  func testMixedColorTinyAlphaAndUnknownName() throws {
    XCTAssertNil(resolve("MissingIcon"))
    let image = try XCTUnwrap(resolve("OnekeyTextIllus", color: "rgba(255,0,0,0)"))
    let cgImage = try XCTUnwrap(image.cgImage)
    var pixels = [UInt8](repeating: 0, count: 128 * 128 * 4)
    let context = try XCTUnwrap(CGContext(data: &pixels, width: 128, height: 128,
      bitsPerComponent: 8, bytesPerRow: 128 * 4, space: CGColorSpaceCreateDeviceRGB(),
      bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue))
    context.draw(cgImage, in: CGRect(x: 0, y: 0, width: 128, height: 128))
    var green = 0, red = 0
    for index in stride(from: 0, to: pixels.count, by: 4) where pixels[index + 3] > 0 {
      if pixels[index + 1] > pixels[index] { green += 1 }
      if pixels[index] > pixels[index + 1] { red += 1 }
    }
    XCTAssertGreaterThan(green, 0)
    XCTAssertEqual(red, 0)
  }

  func testCacheAndCoalescedQueueBounds() throws {
    let stateLock: NSLock = try field("lock")
    let metadataLock: NSLock = try field("metadataLock")
    let blockerOwners = (0..<4).map { _ in NSObject() }
    let owner = NSObject()
    let blockers = expectation(description: "Blocked decodes drain after metadata release")
    blockers.expectedFulfillmentCount = blockerOwners.count
    metadataLock.lock()
    do {
      defer { metadataLock.unlock() }
      for blocker in blockerOwners {
        OneKeyNamedSVG.shared.request(owner: blocker, name: "DotVerSolid", color: "rgba(0,0,255,1)",
          width: 24, height: 24, scale: 1) { _ in blockers.fulfill() }
      }
      eventually("Exactly two production workers are blocked at metadata") {
        stateLock.lock(); defer { stateLock.unlock() }
        return (try? self.field("workers", as: Int.self)) == 2
      }
      for _ in 0..<100 {
        OneKeyNamedSVG.shared.request(owner: owner, name: "DotVerSolid", color: "rgba(255,0,0,1)",
          width: 24, height: 24, scale: 1) { _ in XCTFail("Cancelled coalesced request completed") }
      }
      let (order, keys, workers): ([ObjectIdentifier], [ObjectIdentifier], Int) = try {
        stateLock.lock(); defer { stateLock.unlock() }
        let order: [ObjectIdentifier] = try field("order")
        let pending: Any = try field("pending")
        print("ICON_QUEUE_REFLECTION type=\(Swift.type(of: pending)) style=\(String(describing: Mirror(reflecting: pending).displayStyle))")
        XCTAssertEqual(Mirror(reflecting: pending).displayStyle, .dictionary)
        let keys = Mirror(reflecting: pending).children.compactMap {
          Mirror(reflecting: $0.value).children.first?.value as? ObjectIdentifier
        }
        return (order, keys, try field("workers"))
      }()
      XCTAssertEqual(order.filter { $0 == ObjectIdentifier(owner) }.count, 1)
      XCTAssertEqual(keys.filter { $0 == ObjectIdentifier(owner) }.count, 1)
      XCTAssertLessThanOrEqual(workers, 2)
      print("ICON_QUEUE_BOUND ownerOrder=\(order.filter { $0 == ObjectIdentifier(owner) }.count) ownerPending=\(keys.filter { $0 == ObjectIdentifier(owner) }.count) workers=\(workers)")
      OneKeyNamedSVG.shared.cancel(owner: owner)
      let cancelledOrder: [ObjectIdentifier] = try {
        stateLock.lock(); defer { stateLock.unlock() }
        return try field("order")
      }()
      XCTAssertFalse(cancelledOrder.contains(ObjectIdentifier(owner)))
    }
    withExtendedLifetime(blockerOwners) { wait(for: [blockers], timeout: 5) }
    eventually("All workers completed") {
      stateLock.lock(); defer { stateLock.unlock() }
      return (try? self.field("workers", as: Int.self)) == 0
    }
    var firstSizeImage: UIImage?
    var lastSizeImage: UIImage?
    for width in 24..<174 {
      let image = try XCTUnwrap(resolve("DotVerSolid", color: "rgba(0,0,0,1)", width: width, height: 24))
      if width == 24 { firstSizeImage = image }
      lastSizeImage = image
    }
    let smallCount: Int = try {
      stateLock.lock(); defer { stateLock.unlock() }
      let cache: [String: OneKeyNamedSVG.RenderResult] = try field("cache")
      return cache.count
    }()
    XCTAssertEqual(smallCount, 128)
    let sizeCache: [String: OneKeyNamedSVG.RenderResult] = try field("cache")
    XCTAssertFalse(sizeCache.values.contains { $0.image === firstSizeImage })
    XCTAssertTrue(sizeCache.values.contains { $0.image === lastSizeImage })
    _ = try XCTUnwrap(resolve("AppleBrand", width: 1024, height: 1024))
    _ = try XCTUnwrap(resolve("BotIllus", width: 1024, height: 1024))
    let uncached = try XCTUnwrap(resolve("BotIllus", width: 1536, height: 1536))
    let (cache, storedBytes): ([String: OneKeyNamedSVG.RenderResult], Int) = try {
      stateLock.lock(); defer { stateLock.unlock() }
      return (try field("cache"), try field("bytes"))
    }()
    let actualBytes = cache.values.reduce(0) { $0 + ($1.image.cgImage?.bytesPerRow ?? 0) * ($1.image.cgImage?.height ?? 0) }
    XCTAssertLessThanOrEqual(cache.count, 128)
    XCTAssertLessThanOrEqual(actualBytes, 8_388_608)
    XCTAssertEqual(storedBytes, actualBytes)
    XCTAssertFalse(cache.values.contains { $0.image === uncached })
    let a = try resolveRender("DotVerSolid")
    let same = try resolveRender("DotVerSolid", color: "rgba(255,0,0,1)")
    let otherScale = try resolveRender("DotVerSolid", scale: 2)
    XCTAssertTrue(a.image === same.image)
    XCTAssertFalse(a.image === otherScale.image)
    XCTAssertNil(resolve("DotVerSolid", width: 4097))
    XCTAssertNil(resolve("DotVerSolid", width: 0))
  }

  private func assertSemanticPixels(_ actual: UIImage, _ expected: UIImage, name: String, alpha: Double) throws {
    let a = try pixels(actual), b = try pixels(expected)
    let width = try XCTUnwrap(actual.cgImage).width, height = try XCTUnwrap(actual.cgImage).height
    XCTAssertEqual(a.count, b.count)
    // These fixture modes are independent source facts, not production classification output.
    let single = name == "DotVerSolid"
    let multiple = name == "ChartPositionOutline" || name == "LimitOrderOutline"
    let maximum = zip(a, b).map { abs(Int($0)-Int($1)) }.max() ?? 0
    if !single && !multiple { XCTAssertLessThanOrEqual(maximum, 1, name); return }
    var alphaMaximum = 0, flatMaximum = 0, colorMaximum = 0
    var actualMass = 0, rawMass = 0, covered = 0, alphaError = 0
    var actualBox = [width, height, -1, -1], rawBox = actualBox
    for y in 0..<height { for x in 0..<width {
      let offset = (y * width+x)*4, aa = Int(a[offset+3]), ba = Int(b[offset+3])
      actualMass += aa; rawMass += ba; alphaMaximum = max(alphaMaximum, abs(aa-ba))
      if aa > 0 || ba > 0 { covered += 1; alphaError += abs(aa-ba) }
      for (values, isActual) in [(aa, true), (ba, false)] where values > 0 {
        var box = isActual ? actualBox : rawBox
        box = [min(box[0],x), min(box[1],y), max(box[2],x), max(box[3],y)]
        if isActual { actualBox = box } else { rawBox = box }
      }
      for (channel, rgb) in [63,111,199].enumerated() {
        colorMaximum = max(colorMaximum, abs(Int(a[offset+channel])-Int((Double(rgb)*Double(aa)/255).rounded())))
      }
      if ba > 0 && x >= 2 && y >= 2 && x < width-2 && y < height-2 {
        var flat = true
        for dy in -2...2 { for dx in -2...2 { for channel in 0..<4 {
          if b[((y+dy)*width+x+dx)*4+channel] != b[offset+channel] { flat = false }
        } } }
        if flat { for channel in 0..<4 { flatMaximum = max(flatMaximum, abs(Int(a[offset+channel])-Int(b[offset+channel]))) } }
      }
    } }
    XCTAssertLessThanOrEqual(colorMaximum, 1, "Dynamic RGB follows requested color times actual alpha")
    if multiple || alpha == 1 { XCTAssertLessThanOrEqual(alphaMaximum, 1, name) }
    else if alpha == 0 { XCTAssertEqual(actualMass, 0, name) }
    else {
      XCTAssertLessThanOrEqual(flatMaximum, 1, "Raw flat interiors remain equivalent")
      XCTAssertLessThanOrEqual(alphaMaximum, 16, "Only bounded single-paint antialiasing differences")
      XCTAssertLessThanOrEqual(Double(abs(actualMass-rawMass))/Double(max(1,rawMass)), 0.02)
      XCTAssertLessThanOrEqual(Double(alphaError)/Double(max(1,covered)), 2)
      for (left,right) in zip(actualBox,rawBox) { XCTAssertLessThanOrEqual(abs(left-right), 1) }
    }
    print("ICON_SEMANTIC name=\(name) alpha=\(alpha) rawMax=\(maximum) alphaMax=\(alphaMaximum) flatMax=\(flatMaximum) tintMax=\(colorMaximum) massDelta=\(actualMass-rawMass)")
  }

  func testRenderedShapesMatchIndependentRawDecoder() throws {
    guard UIApplication.perform(NSSelectorFromString("sharedApplication"))?.takeUnretainedValue() as? UIApplication != nil else {
      throw XCTSkip("A UIApplication host is required for consumer pixel comparison")
    }
    let owner = Bundle(for: HybridOneKeyIcon.self)
    let bundle = try XCTUnwrap(Bundle(url: try XCTUnwrap(owner.url(forResource: "OneKeyIcons", withExtension: "bundle")
      ?? Bundle.main.url(forResource: "OneKeyIcons", withExtension: "bundle"))))
    let window = UIWindow(frame: UIScreen.main.bounds); let controller = UIViewController()
    window.rootViewController = controller; window.makeKeyAndVisible(); defer { window.isHidden = true }
    for name in ["DotVerSolid", "ChartPositionOutline", "LimitOrderOutline", "BotIllus", "OnekeyTextIllus", "BadgeVerifiedSolid"] {
      for alpha in ["0.0", "0.5", "1.0"] {
        let color = "rgba(63,111,199,\(alpha))"
        let icon = HybridOneKeyIcon(); let view = try XCTUnwrap(icon.view as? UIImageView)
        view.frame = CGRect(x: 0, y: 80, width: 64, height: 64); controller.view.addSubview(view)
        icon.iconName = name; icon.resolvedColor = color; icon.afterUpdate()
        eventually("Raw baseline sample ready") { view.image != nil }
        let actual = draw(view)
        let source = try String(contentsOf: XCTUnwrap(bundle.url(forResource: name, withExtension: "svg")), encoding: .utf8)
        let data = try XCTUnwrap(source.replacingOccurrences(of: "currentColor", with: color).data(using: .utf8))
        let size = CGSize(width: 64 * window.screen.scale, height: 64 * window.screen.scale)
        let decoded = try XCTUnwrap(OneKeyImageCoderBridge.decodeSVG(data, pixelSize: size))
        let format = UIGraphicsImageRendererFormat(); format.scale = 1
        let expected = UIGraphicsImageRenderer(size: size, format: format).image { _ in
          let ratio = min(size.width / decoded.size.width, size.height / decoded.size.height)
          let fitted = CGSize(width: decoded.size.width * ratio, height: decoded.size.height * ratio)
          decoded.draw(in: CGRect(x: (size.width-fitted.width)/2, y: (size.height-fitted.height)/2, width: fitted.width, height: fitted.height))
        }
        try assertSemanticPixels(actual, expected, name: name, alpha: try XCTUnwrap(Double(alpha)))
        try save(actual, name: "shape-actual-\(name)-\(alpha)")
        try save(expected, name: "shape-raw-\(name)-\(alpha)")
        icon.prepareForRecycle(); view.removeFromSuperview()
      }
    }
    let edge = Int(64 * window.screen.scale)
    let size = CGSize(width: edge, height: edge)
    func raster(_ body: String) throws -> UIImage {
      let source = "<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'>\(body)</svg>"
      let decoded = try XCTUnwrap(OneKeyImageCoderBridge.decodeSVG(Data(source.utf8), pixelSize: size))
      let format = UIGraphicsImageRendererFormat(); format.scale = 1
      return UIGraphicsImageRenderer(size: size, format: format).image { _ in decoded.draw(in: CGRect(origin: .zero, size: size)) }
    }
    let overlap = "<rect width='16' height='24' fill='rgba(0,0,0,0.5)'/><rect x='8' width='16' height='24' fill='rgba(0,0,0,0.5)'/>"
    let mask = try raster(overlap)
    let result = OneKeyNamedSVG.RenderResult(image: mask, mode: .multiple, sourceIdentity: "test-overlap", maskAlpha: 0.5)
    let probe = UIImageView(frame: CGRect(x: 0,y: 80,width: 64,height: 64)); controller.view.addSubview(probe)
    probe.image = result.image.withRenderingMode(.alwaysTemplate); probe.tintColor = result.tintColor("rgba(255,0,0,0.5)")
    let rendered = draw(probe), actual = try pixels(rendered)
    let raw = try raster(overlap.replacingOccurrences(of: "rgba(0,0,0,0.5)", with: "rgba(255,0,0,0.5)"))
    let expected = try pixels(raw), center = ((edge/2)*edge+edge/2)*4
    XCTAssertEqual(expected[center+3], 192); XCTAssertEqual(actual[center+3], 192)
    let opacityMask = try raster("<rect width='24' height='24' fill='rgba(0,0,0,1.0)' fill-opacity='0.4'/>")
    let single = OneKeyNamedSVG.RenderResult(image: opacityMask, mode: .single, sourceIdentity: "test-source-opacity", maskAlpha: 1)
    probe.image = single.image.withRenderingMode(.alwaysTemplate); probe.tintColor = single.tintColor("rgba(255,0,0,0.5)")
    let opacityActual = try pixels(draw(probe))
    XCTAssertEqual(Int(opacityActual[center+3]), 51, accuracy: 1)
    probe.removeFromSuperview()
  }

  func testShapeTintAndPendingColorRetention() throws {
    guard UIApplication.perform(NSSelectorFromString("sharedApplication"))?.takeUnretainedValue() as? UIApplication != nil else {
      throw XCTSkip("A UIApplication host is required for actual tint drawing")
    }
    let window = UIWindow(frame: UIScreen.main.bounds); let controller = UIViewController()
    window.rootViewController = controller; window.makeKeyAndVisible(); defer { window.isHidden = true }
    let icon = HybridOneKeyIcon(); let view = try XCTUnwrap(icon.view as? UIImageView)
    view.frame = CGRect(x: 0, y: 80, width: 64, height: 64); controller.view.addSubview(view)
    icon.iconName = "DotVerSolid"; icon.resolvedColor = "rgba(255,0,0,1)"; icon.afterUpdate()
    eventually("Initial shape mask") { view.image != nil }
    let mask = try XCTUnwrap(view.image)
    let other = HybridOneKeyIcon(); let otherView = try XCTUnwrap(other.view as? UIImageView)
    otherView.frame = CGRect(x: 80, y: 80, width: 64, height: 64); controller.view.addSubview(otherView)
    other.iconName = "DotVerSolid"; other.resolvedColor = "rgba(0,0,255,1)"; other.afterUpdate()
    eventually("Second view shares cached shape") { otherView.image != nil }
    XCTAssertTrue(mask.cgImage === otherView.image?.cgImage)
    let stateLock: NSLock = try field("lock")
    eventually("Initial shape requests drained") {
      stateLock.lock(); defer { stateLock.unlock() }
      return (try? self.field("workers", as: Int.self)) == 0
    }
    view.isHidden = true; XCTAssertTrue(view.image === mask)
    view.isHidden = false; XCTAssertTrue(view.image === mask)
    let metadataLock: NSLock = try field("metadataLock")
    metadataLock.lock()
    do {
      defer { metadataLock.unlock() }
      for color in ["rgba(0,0,255,1)", "rgba(0,255,0,0.5)"] {
        icon.resolvedColor = color; icon.afterUpdate()
        XCTAssertTrue(view.image === mask, "Immediate tint retains the immutable image")
      }
      let bytes = try pixels(draw(view)); let width = Int(64 * window.screen.scale)
      let center = (width / 2 * width + width / 2) * 4
      XCTAssertEqual(bytes[center], 0); XCTAssertEqual(bytes[center + 2], 0)
      XCTAssertEqual(Int(bytes[center + 1]), 128, accuracy: 1)
      XCTAssertEqual(Int(bytes[center + 3]), 128, accuracy: 1)
      let otherBytes = try pixels(draw(otherView))
      XCTAssertEqual(otherBytes[center], 0); XCTAssertEqual(otherBytes[center + 1], 0)
      XCTAssertEqual(otherBytes[center + 2], 255); XCTAssertEqual(otherBytes[center + 3], 255)
      stateLock.lock()
      defer { stateLock.unlock() }
      let pending: Any = try field("pending")
      XCTAssertEqual(Mirror(reflecting: pending).children.count, 0, "RGB/alpha single-paint tint submits no worker request")
      XCTAssertEqual(try field("workers", as: Int.self), 0, "No in-flight worker can hide a submitted request")
    }
    icon.iconName = "ChartPositionOutline"; icon.resolvedColor = "rgba(255,0,0,1)"; icon.afterUpdate()
    eventually("Multiple-paint shape") { view.image != nil }
    let multiple = try XCTUnwrap(view.image)
    icon.resolvedColor = "rgba(0,0,255,1)"; icon.afterUpdate(); XCTAssertTrue(view.image === multiple)
    metadataLock.lock()
    do {
      defer { metadataLock.unlock() }
      icon.resolvedColor = "rgba(0,255,0,0.5)"; icon.afterUpdate()
      XCTAssertTrue(view.image === multiple, "New mask alpha retains the successful old image")
      icon.resolvedColor = "invalid"; icon.afterUpdate(); XCTAssertNil(view.image)
    }
    RunLoop.main.run(until: Date().addingTimeInterval(0.1)); XCTAssertNil(view.image)
    icon.iconName = "BotIllus"; icon.resolvedColor = "rgba(255,0,0,1)"; icon.afterUpdate()
    eventually("Fixed original") { view.image != nil }
    let fixed = try XCTUnwrap(view.image)
    icon.resolvedColor = "rgba(0,0,255,0)"; icon.afterUpdate(); XCTAssertTrue(view.image === fixed)
    icon.iconName = "OnekeyTextIllus"; icon.afterUpdate()
    eventually("Mixed original") { view.image != nil }
    let mixed = try XCTUnwrap(view.image)
    metadataLock.lock()
    do {
      defer { metadataLock.unlock() }
      icon.resolvedColor = "rgba(255,0,0,0.5)"; icon.afterUpdate(); XCTAssertTrue(view.image === mixed)
      icon.iconName = "MissingIcon"; icon.afterUpdate(); XCTAssertNil(view.image)
    }
    RunLoop.main.run(until: Date().addingTimeInterval(0.1)); XCTAssertNil(view.image)
    icon.prepareForRecycle(); other.prepareForRecycle(); XCTAssertNil(view.image)
    let one = try resolveRender("DotVerSolid"); XCTAssertEqual(one.mode, .single)
    let multi = try resolveRender("ChartPositionOutline", color: "rgba(0,0,255,0.5)"); XCTAssertEqual(multi.mode, .multiple)
    XCTAssertTrue(multi.canTint("rgba(255,0,0,0.5)")); XCTAssertFalse(multi.canTint("rgba(255,0,0,1)"))
    XCTAssertEqual(try resolveRender("BotIllus").mode, .fixed)
    XCTAssertEqual(try resolveRender("OnekeyTextIllus").mode, .raster)
  }

  func testProductionViewLifecycleAndRepresentativeRendering() throws {
    guard let application = UIApplication.perform(NSSelectorFromString("sharedApplication"))?.takeUnretainedValue() as? UIApplication else {
      throw XCTSkip("A real UIApplication host is required for window lifecycle acceptance")
    }
    XCTAssertTrue(Thread.isMainThread)
    XCTAssertEqual(application.applicationState, .active)
    let window = UIWindow(frame: UIScreen.main.bounds)
    let controller = UIViewController()
    controller.view.backgroundColor = UIColor(white: 0.125, alpha: 1)
    window.rootViewController = controller
    window.makeKeyAndVisible()
    XCTAssertTrue(window.isKeyWindow)
    XCTAssertFalse(window.isHidden)
    defer { window.isHidden = true }
    let icon = HybridOneKeyIcon()
    let view = try XCTUnwrap(icon.view as? UIImageView)
    view.frame = CGRect(x: 8, y: 60, width: 64, height: 64)
    controller.view.addSubview(view)
    XCTAssertTrue(view.window === window)
    for index in 0..<100 {
      icon.iconName = index.isMultiple(of: 2) ? "BotIllus" : "DotVerSolid"
      icon.resolvedColor = "rgba(255,0,0,1)"
      icon.afterUpdate()
    }
    icon.iconName = "DotVerSolid"; icon.resolvedColor = "rgba(0,0,255,0.5019607843137255)"; icon.afterUpdate()
    eventually("Final blue view image displayed") { view.image != nil }
    let blue = draw(view)
    let bytes = try pixels(blue)
    var blueCount = 0, redCount = 0
    for index in stride(from: 0, to: bytes.count, by: 4) where bytes[index + 3] > 0 {
      if bytes[index + 2] > bytes[index] { blueCount += 1 }
      if bytes[index] > bytes[index + 2] { redCount += 1 }
    }
    XCTAssertGreaterThan(blueCount, 0); XCTAssertEqual(redCount, 0)
    let maximumAlpha = stride(from: 3, to: bytes.count, by: 4).map { bytes[$0] }.max() ?? 0
    let cgImage = try XCTUnwrap(blue.cgImage)
    let interiorAlpha = bytes[((cgImage.height / 2) * cgImage.width + cgImage.width / 2) * 4 + 3]
    print("ICON_ALPHA_PROBE solidInteriorAlpha=\(interiorAlpha) resampledEdgeMaximumAlpha=\(maximumAlpha)")
    for y in [cgImage.height / 6, cgImage.height / 2, cgImage.height * 5 / 6] {
      let offset = (y * cgImage.width + cgImage.width / 2) * 4
      XCTAssertEqual(bytes[offset], 0)
      XCTAssertEqual(bytes[offset + 1], 0)
      XCTAssertTrue((127...128).contains(Int(bytes[offset + 3])))
      XCTAssertEqual(bytes[offset + 2], bytes[offset + 3])
    }
    XCTAssertEqual(bytes[3], 0)
    try save(blue, name: "ios-native-view-blue")
    view.frame.size = CGSize(width: 96, height: 64); view.setNeedsLayout(); view.layoutIfNeeded()
    eventually("Resized raster is current") { view.image?.cgImage?.width == Int(ceil(96 * window.screen.scale)) }
    XCTAssertEqual(view.image?.cgImage?.height, Int(ceil(64 * window.screen.scale)))
    view.frame.size = .zero; view.setNeedsLayout(); view.layoutIfNeeded()
    XCTAssertNil(view.image)
    view.frame.size = CGSize(width: 64, height: 64); view.setNeedsLayout(); view.layoutIfNeeded()
    eventually("Restored layout decodes") { view.image != nil }
    icon.iconName = "MissingIcon"; icon.afterUpdate(); XCTAssertNil(view.image)
    icon.iconName = "BotIllus"; icon.afterUpdate()
    view.removeFromSuperview(); XCTAssertNil(view.window); XCTAssertNil(view.image)
    RunLoop.main.run(until: Date().addingTimeInterval(0.1)); XCTAssertNil(view.image)
    controller.view.addSubview(view)
    eventually("Reattached view decodes") { view.image != nil }
    icon.prepareForRecycle(); XCTAssertNil(view.image)
    RunLoop.main.run(until: Date().addingTimeInterval(0.1)); XCTAssertNil(view.image)
    icon.iconName = "BotIllus"; icon.resolvedColor = "rgba(255,0,0,1)"; icon.afterUpdate()
    eventually("Reused view decodes") { view.image != nil }
    icon.onDropView(); XCTAssertNil(view.image)
    view.removeFromSuperview()

    let names = ["GooglePayIllus", "ShortcutsCustom", "SidebarLeftArrowCustom", "PassKeySolid",
      "ArrowTopRightIllus", "OnekeyTextIllus", "CheckboxCheckedCustom", "AppleBrand", "BotIllus",
      "OnekeyPrimeLightColored", "OnekeyPrimeDarkColored", "ShieldDeviceDarkIllus", "CoinsIllus",
      "MaticCircleIllus", "UserAvatarFallbackColored"]
    var gallery: [HybridOneKeyIcon] = []
    for (index, name) in names.enumerated() {
      let hybrid = HybridOneKeyIcon()
      hybrid.view.frame = CGRect(x: CGFloat(8 + (index % 3) * 128), y: CGFloat(60 + (index / 3) * 140), width: 112, height: 112)
      controller.view.addSubview(hybrid.view)
      hybrid.iconName = name; hybrid.resolvedColor = "rgba(255,0,0,0.5019607843137255)"; hybrid.afterUpdate()
      gallery.append(hybrid)
    }
    eventually("All representative production views display") {
      gallery.allSatisfy { ($0.view as? UIImageView)?.image != nil }
    }
    for (index, hybrid) in gallery.enumerated() {
      try save(draw(try XCTUnwrap(hybrid.view as? UIImageView)), name: names[index] + ".svg")
    }
    let format = UIGraphicsImageRendererFormat(); format.scale = 1
    let screenshot = UIGraphicsImageRenderer(bounds: controller.view.bounds, format: format).image { _ in
      controller.view.drawHierarchy(in: controller.view.bounds, afterScreenUpdates: true)
    }
    try save(screenshot, name: "ios-production-gallery")
    gallery.forEach { $0.onDropView() }
  }

  func testStartupPrewarmDiskRenderAndSynchronousBridgeRequest() throws {
    let tempFile = FileManager.default.temporaryDirectory
      .appendingPathComponent("onekey-icon-startup-test-\(UUID().uuidString).json")
    defer {
      OneKeyNamedSVG.shared.resetStartupForTesting(fileURL: nil)
      try? FileManager.default.removeItem(at: tempFile)
    }
    OneKeyNamedSVG.shared.resetStartupForTesting(fileURL: tempFile)
    OneKeyMainThreadDecode.resetForTesting()

    // Warm one raster to disk so diskRender and prewarm can replay it.
    let disk = SDImageCache(namespace: "onekey-named-svg-raster-v1")
    _ = try XCTUnwrap(resolve("DotVerSolid", color: "rgba(0,0,0,1)", width: 48, height: 48, scale: 2))
    eventually("Disk raster written for DotVerSolid 48x48") {
      OneKeyNamedSVG.shared.clearMemoryCacheForTesting()
      return OneKeyNamedSVG.shared.diskRender(
        name: "DotVerSolid",
        color: "rgba(0,0,0,1)",
        width: 48,
        height: 48,
        scale: 2
      ) != nil
    }
    _ = disk

    // 1. Synchronous callback on OneKeyNamedSVGBridge.request (BottomTabs path).
    var syncCallbackFired = false
    let owner = NSObject()
    OneKeyNamedSVGBridge.request(
      owner: owner,
      name: "DotVerSolid",
      color: "rgba(0,0,0,1)",
      width: 48,
      height: 48,
      scale: 2
    ) { image in
      XCTAssertNotNil(image)
      syncCallbackFired = true
    }
    XCTAssertTrue(syncCallbackFired, "Bridge request must invoke completion synchronously on main thread when cached")

    // 2. diskRender size bound (>192px and >32KiB rejected) and metrics recording.
    OneKeyNamedSVG.shared.clearMemoryCacheForTesting()
    OneKeyMainThreadDecode.resetForTesting()
    XCTAssertNil(
      OneKeyNamedSVG.shared.diskRender(
        name: "DotVerSolid",
        color: "rgba(0,0,0,1)",
        width: 200,
        height: 200,
        scale: 2
      )
    )
    let rasterPath = try XCTUnwrap(
      OneKeyNamedSVG.shared.rasterDiskCachePathForTesting(
        name: "DotVerSolid",
        color: "rgba(0,0,0,1)",
        width: 48,
        height: 48,
        scale: 2
      )
    )
    let rasterURL = URL(fileURLWithPath: rasterPath)
    let originalRasterData = try Data(contentsOf: rasterURL)
    try Data(repeating: 0, count: OneKeyMainThreadDecode.maximumEncodedBytes + 1024)
      .write(to: rasterURL, options: .atomic)
    defer {
      try? originalRasterData.write(to: rasterURL, options: .atomic)
    }
    XCTAssertNil(
      OneKeyNamedSVG.shared.diskRender(
        name: "DotVerSolid",
        color: "rgba(0,0,0,1)",
        width: 48,
        height: 48,
        scale: 2
      )
    )
    try originalRasterData.write(to: rasterURL, options: .atomic)
    XCTAssertNotNil(
      OneKeyNamedSVG.shared.diskRender(
        name: "DotVerSolid",
        color: "rgba(0,0,0,1)",
        width: 48,
        height: 48,
        scale: 2
      )
    )
    let decodeMetrics = OneKeyMainThreadDecode.metricsSnapshot()
    XCTAssertEqual(decodeMetrics.hits, 1)
    XCTAssertEqual(decodeMetrics.limitRejections, 1)

    // 2b. Synchronous diskRender must not wait on SDImageCache.ioQueue when a concurrent
    // write or cache cleanup is occupying ioQueue, and must handle removed files cleanly.
    if let ioQueue = OneKeyNamedSVG.shared.rasterDiskCacheForTesting.value(forKey: "ioQueue")
      as? DispatchQueue
    {
      OneKeyNamedSVG.shared.clearMemoryCacheForTesting()
      OneKeyMainThreadDecode.resetForTesting()
      let ioEntered = DispatchSemaphore(value: 0)
      let ioRelease = DispatchSemaphore(value: 0)
      let ioFinished = DispatchSemaphore(value: 0)
      ioQueue.async {
        ioEntered.signal()
        _ = ioRelease.wait(timeout: .now() + 2)
        ioFinished.signal()
      }
      XCTAssertEqual(ioEntered.wait(timeout: .now() + 1), .success)
      let writerDone = DispatchSemaphore(value: 0)
      DispatchQueue.global(qos: .utility).async {
        OneKeyNamedSVG.shared.rasterDiskCacheForTesting.storeImageData(
          toDisk: originalRasterData,
          forKey: "concurrent-writer-probe"
        )
        writerDone.signal()
      }
      var finishedWhileIoBlocked = false
      let hitWhileBusy = OneKeyNamedSVG.shared.diskRender(
        name: "DotVerSolid",
        color: "rgba(0,0,0,1)",
        width: 48,
        height: 48,
        scale: 2
      )
      finishedWhileIoBlocked = (ioFinished.wait(timeout: .now()) == .timedOut)
      ioRelease.signal()
      _ = ioFinished.wait(timeout: .now() + 2)
      _ = writerDone.wait(timeout: .now() + 2)
      OneKeyNamedSVG.shared.rasterDiskCacheForTesting.removeImageFromDisk(
        forKey: "concurrent-writer-probe"
      )
      XCTAssertNotNil(hitWhileBusy)
      XCTAssertTrue(
        finishedWhileIoBlocked,
        "diskRender must read directly without waiting for SDImageCache.ioQueue"
      )
    }
    try? FileManager.default.removeItem(at: rasterURL)
    OneKeyNamedSVG.shared.clearMemoryCacheForTesting()
    OneKeyMainThreadDecode.resetForTesting()
    XCTAssertNil(
      OneKeyNamedSVG.shared.diskRender(
        name: "DotVerSolid",
        color: "rgba(0,0,0,1)",
        width: 48,
        height: 48,
        scale: 2
      )
    )
    XCTAssertEqual(OneKeyMainThreadDecode.metricsSnapshot().diskMisses, 1)
    try originalRasterData.write(to: rasterURL, options: .atomic)

    // 3. App Lock multi-burst window + launch-generation eviction.
    OneKeyNamedSVG.shared.resetStartupForTesting(fileURL: tempFile)
    _ = OneKeyNamedSVG.shared.cachedRender(
      name: "DotVerSolid",
      color: "rgba(0,0,0,1)",
      width: 48,
      height: 48,
      scale: 2
    )
    // Simulate first 3s quiet flush while still on App Lock (< 8 icons): recording stays open.
    OneKeyNamedSVG.shared.flushStartupForTesting(forceClose: false)
    _ = OneKeyNamedSVG.shared.cachedRender(
      name: "BotIllus",
      color: "rgba(0,0,0,1)",
      width: 48,
      height: 48,
      scale: 2
    )
    OneKeyNamedSVG.shared.flushStartupForTesting(forceClose: true)

    let firstLaunchEntries = try JSONDecoder().decode(
      [OneKeyNamedSVG.StartupEntry].self,
      from: Data(contentsOf: tempFile)
    )
    XCTAssertEqual(firstLaunchEntries.map(\.name), ["DotVerSolid", "BotIllus"])
    XCTAssertEqual(firstLaunchEntries.map(\.launch), [1, 1])

    // Simulate prewarm on launch 2, then only "BotIllus" is seen for 3 launches.
    for expectedLaunch in 2...4 {
      OneKeyNamedSVG.shared.clearMemoryCacheForTesting()
      OneKeyNamedSVG.shared.resetStartupForTesting(fileURL: tempFile)
      OneKeyNamedSVG.shared.prewarmStartupSet()
      XCTAssertGreaterThanOrEqual(OneKeyNamedSVG.shared.startupMetricsSnapshot().entryCount, 1)
      _ = OneKeyNamedSVG.shared.cachedRender(
        name: "BotIllus",
        color: "rgba(0,0,0,1)",
        width: 48,
        height: 48,
        scale: 2
      )
      OneKeyNamedSVG.shared.flushStartupForTesting(forceClose: true)
      let currentEntries = try JSONDecoder().decode(
        [OneKeyNamedSVG.StartupEntry].self,
        from: Data(contentsOf: tempFile)
      )
      if expectedLaunch < 4 {
        XCTAssertEqual(currentEntries.map(\.name), ["BotIllus", "DotVerSolid"])
      } else {
        // Launch 4: DotVerSolid (launch 1) has age 4 - 1 == 3 >= startupMaxLaunchAge(3) and is evicted.
        XCTAssertEqual(currentEntries.map(\.name), ["BotIllus"])
        XCTAssertEqual(currentEntries.first?.launch, 4)
      }
    }
  }
}
