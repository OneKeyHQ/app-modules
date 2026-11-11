import Foundation
import QuartzCore
import SDWebImage
import UIKit

private final class OneKeyIconBundleToken: NSObject {}

@objc(OneKeyNamedSVGBridge)
internal final class OneKeyNamedSVGBridge: NSObject {
  @objc(cancelWithOwner:)
  static func cancel(owner: AnyObject) {
    OneKeyNamedSVG.shared.cancel(owner: owner)
  }

  @objc(requestWithOwner:name:color:width:height:scale:complete:)
  static func request(
    owner: AnyObject,
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Double,
    complete: @escaping (UIImage?) -> Void
  ) {
    OneKeyNamedSVG.shared.request(
      owner: owner,
      name: name,
      color: color,
      width: width,
      height: height,
      scale: CGFloat(scale),
      complete: complete
    )
  }

  /// Called off the main thread at process start; see `OneKeyNamedSVGStartupLoader`.
  @objc(prewarmStartupSet)
  static func prewarmStartupSet() {
    OneKeyNamedSVG.shared.prewarmStartupSet()
  }
}

/// Process-owned resource metadata and immutable raster cache, independent of JS runtimes.
public final class OneKeyNamedSVG {
  public static let shared = OneKeyNamedSVG()
  private init() {}
  public enum RecolorMode: String { case fixed, single, multiple, raster }
  public struct IconMetadata {
    public let width: Double
    public let height: Double
    public let recolorMode: RecolorMode

    public init(width: Double, height: Double, recolorMode: RecolorMode) {
      self.width = width
      self.height = height
      self.recolorMode = recolorMode
    }
  }
  public struct RenderResult {
    public let image: UIImage
    public let mode: RecolorMode
    public let sourceIdentity: String
    public let maskAlpha: CGFloat

    public func canTint(_ color: String) -> Bool {
      guard let value = OneKeyNamedSVG.channels(color) else { return false }
      return mode == .fixed || mode == .single || (mode == .multiple && value[3] == maskAlpha)
    }
    public func tintColor(_ color: String) -> UIColor? {
      guard mode == .single || mode == .multiple, let value = OneKeyNamedSVG.channels(color) else { return nil }
      return UIColor(red: value[0], green: value[1], blue: value[2], alpha: mode == .single ? value[3] : 1)
    }
    fileprivate func finalImage(_ color: String) -> UIImage {
      guard let tint = tintColor(color) else { return image }
      if OneKeyNamedSVG.channels(color) == [0, 0, 0, 1] { return image }
      // UIImage tint wrappers can retain the original black cgImage. Legacy callers
      // receive actual final-color pixels, so bake the wrapper on the worker.
      let format = UIGraphicsImageRendererFormat(); format.scale = 1; format.preferredRange = .standard
      return UIGraphicsImageRenderer(size: image.size, format: format).image { _ in
        image.withTintColor(tint, renderingMode: .alwaysOriginal).draw(in: CGRect(origin: .zero, size: image.size))
      }
    }
  }

  private static func channels(_ color: String) -> [CGFloat]? {
    guard color.range(of: #"^rgba\(\d{1,3},\d{1,3},\d{1,3},(?:0(?:\.\d+)?|1(?:\.0+)?)\)$"#, options: .regularExpression) != nil else { return nil }
    let parts = color.dropFirst(5).dropLast().split(separator: ",").compactMap { Double($0) }
    guard parts.count == 4, parts.prefix(3).allSatisfy({ (0...255).contains($0) }), (0...1).contains(parts[3]) else { return nil }
    return parts.enumerated().map { CGFloat($0.offset == 3 ? $0.element : $0.element / 255) }
  }
  public static func isValidColor(_ color: String) -> Bool { channels(color) != nil }

  private struct Request {
    let name: String
    let color: String
    let width: Int
    let height: Int
    let scale: CGFloat
    let finalColor: Bool
    let complete: (RenderResult?) -> Void
  }
  private static let rasterDiskCache: SDImageCache = {
    let config = SDImageCacheConfig()
    config.shouldCacheImagesInMemory = false
    config.shouldUseWeakMemoryCache = false
    // Source digests invalidate changed assets; retain only size-based eviction.
    config.maxDiskAge = -1
    config.maxDiskSize = 128 * 1024 * 1024
    return SDImageCache(namespace: "onekey-named-svg-raster-v1", diskCacheDirectory: nil, config: config)
  }()
  private static let diskWriter = DispatchQueue(label: "onekey.svg.raster-disk", qos: .utility)

  private let lock = NSLock()
  private let metadataLock = NSLock()
  private var pending: [ObjectIdentifier: Request] = [:]
  private var order: [ObjectIdentifier] = []
  private var workers = 0
  private var metadata: [String: [String: Any]]?
  private var resourceBundle: Bundle?
  private var cache: [String: RenderResult] = [:]
  private var recency: [String] = []
  private var bytes = 0

  // MARK: Startup prewarm
  //
  // A cold process starts with an empty memory cache, so every icon of the first
  // screen would miss `cachedRender` and paint one frame late. The requests seen
  // right after launch are persisted and decoded again on a background queue at
  // the next process start, before JS mounts the first screen.

  struct StartupIdentity: Hashable {
    let name: String
    let color: String
    let width: Int
    let height: Int
    let scale: Double
  }

  struct StartupEntry: Codable, Equatable {
    let name: String
    let color: String
    let width: Int
    let height: Int
    let scale: Double
    var launch: Int

    var identity: StartupIdentity {
      StartupIdentity(name: name, color: color, width: width, height: height, scale: scale)
    }

    private enum CodingKeys: String, CodingKey {
      case name
      case color
      case width
      case height
      case scale
      case launch
    }

    init(name: String, color: String, width: Int, height: Int, scale: Double, launch: Int = 0) {
      self.name = name
      self.color = color
      self.width = width
      self.height = height
      self.scale = scale
      self.launch = launch
    }

    init(from decoder: Decoder) throws {
      let container = try decoder.container(keyedBy: CodingKeys.self)
      self.name = try container.decode(String.self, forKey: .name)
      self.color = try container.decode(String.self, forKey: .color)
      self.width = try container.decode(Int.self, forKey: .width)
      self.height = try container.decode(Int.self, forKey: .height)
      self.scale = try container.decode(Double.self, forKey: .scale)
      self.launch = try container.decodeIfPresent(Int.self, forKey: .launch) ?? 0
    }

    func encode(to encoder: Encoder) throws {
      var container = encoder.container(keyedBy: CodingKeys.self)
      try container.encode(name, forKey: .name)
      try container.encode(color, forKey: .color)
      try container.encode(width, forKey: .width)
      try container.encode(height, forKey: .height)
      try container.encode(scale, forKey: .scale)
      if launch > 0 {
        try container.encode(launch, forKey: .launch)
      }
    }
  }

  struct StartupMetrics: Equatable {
    var entryCount: Int = 0
    var decodedCount: Int = 0
    var manifestLoadMs: Double = 0
    var totalMs: Double = 0
  }

  static let startupWindow: TimeInterval = 3
  static let startupMaxDuration: TimeInterval = 60
  static let startupMinimumSettledCount = 8
  static let startupLimit = 96
  static let startupMaxLaunchAge = 3
  private static let startupFile: URL? = FileManager.default
    .urls(for: .cachesDirectory, in: .userDomainMask).first?
    .appendingPathComponent("onekey-named-svg-startup-v1.json")
  private let startupLock = NSLock()
  private var startupSeen: [StartupEntry] = []
  private var startupPrevious: [StartupEntry] = []
  private var startupMaxLaunch = 0
  private var startupRecording: Bool?
  private var startupFirstRequestAt: CFTimeInterval = 0
  private var startupFlushWorkItem: DispatchWorkItem?
  private var customStartupFile: URL?
  private var startupMetrics = StartupMetrics()

  private var activeStartupFileURL: URL? {
    customStartupFile ?? Self.startupFile
  }

  func prewarmStartupSet() {
    let start = CACurrentMediaTime()
    // The manifest is needed by every main-thread fast path, with or without a list.
    metadataLock.lock()
    loadManifestIfNeeded()
    metadataLock.unlock()
    let manifestMs = (CACurrentMediaTime() - start) * 1000.0
    startupLock.lock()
    let file = activeStartupFileURL
    startupLock.unlock()
    guard let file, let data = try? Data(contentsOf: file),
          let entries = try? JSONDecoder().decode([StartupEntry].self, from: data) else {
      startupLock.lock()
      startupMetrics = StartupMetrics(
        entryCount: 0,
        decodedCount: 0,
        manifestLoadMs: manifestMs,
        totalMs: manifestMs
      )
      startupLock.unlock()
      return
    }
    let highestLaunch = entries.map(\.launch).max() ?? 0
    startupLock.lock()
    startupPrevious = entries
    startupMaxLaunch = highestLaunch
    startupLock.unlock()
    var decodedCount = 0
    let bounded = entries.prefix(Self.startupLimit)
    for entry in bounded {
      autoreleasepool {
        if decode(Request(name: entry.name, color: entry.color, width: entry.width,
          height: entry.height, scale: CGFloat(entry.scale), finalColor: false, complete: { _ in })) != nil {
          decodedCount += 1
        }
      }
    }
    let totalMs = (CACurrentMediaTime() - start) * 1000.0
    startupLock.lock()
    startupMetrics = StartupMetrics(
      entryCount: bounded.count,
      decodedCount: decodedCount,
      manifestLoadMs: manifestMs,
      totalMs: totalMs
    )
    startupLock.unlock()
    NSLog(
      "[OneKeyImageStartupMetrics] iconPrewarm entries=%d decoded=%d manifestMs=%.3f totalMs=%.3f",
      bounded.count,
      decodedCount,
      manifestMs,
      totalMs
    )
  }

  private func noteStartupRequest(name: String, color: String, width: Int, height: Int, scale: CGFloat) {
    guard (1...4096).contains(width), (1...4096).contains(height),
          scale.isFinite, scale > 0, Self.isValidColor(color) else { return }
    let identity = StartupIdentity(name: name, color: color, width: width, height: height, scale: Double(scale))
    startupLock.lock()
    defer { startupLock.unlock() }
    if startupRecording == false { return }
    let now = CACurrentMediaTime()
    if startupRecording == nil {
      startupRecording = true
      startupFirstRequestAt = now
    }
    guard startupSeen.count < Self.startupLimit,
          !startupSeen.contains(where: { $0.identity == identity }) else { return }
    startupSeen.append(
      StartupEntry(name: name, color: color, width: width, height: height, scale: Double(scale), launch: 0)
    )
    startupFlushWorkItem?.cancel()
    let item = DispatchWorkItem { [weak self] in
      self?.persistStartupSet(forceClose: false)
    }
    startupFlushWorkItem = item
    Self.diskWriter.asyncAfter(deadline: .now() + Self.startupWindow, execute: item)
  }

  private func persistStartupSet(forceClose: Bool) {
    startupLock.lock()
    guard startupRecording == true else {
      startupLock.unlock()
      return
    }
    let elapsed = CACurrentMediaTime() - startupFirstRequestAt
    let shouldClose =
      forceClose
      || startupSeen.count >= Self.startupMinimumSettledCount
      || startupSeen.count >= Self.startupLimit
      || elapsed >= Self.startupMaxDuration

    let seenIdentities = Set(startupSeen.map(\.identity))
    let previousIdentities = startupPrevious.map(\.identity)
    let currentIdentities = startupSeen.map(\.identity)
    let allPreviousAtMaxLaunch = startupPrevious.allSatisfy { $0.launch == startupMaxLaunch }
    let steadyStateUnchanged =
      !startupPrevious.isEmpty
      && currentIdentities == previousIdentities
      && allPreviousAtMaxLaunch

    let nextLaunch = steadyStateUnchanged ? startupMaxLaunch : (startupMaxLaunch + 1)
    let currentStamped = startupSeen.map { entry -> StartupEntry in
      var copy = entry
      copy.launch = nextLaunch
      return copy
    }
    // This launch's first screen leads; recent earlier entries stay warm until aged out.
    let retainedPrevious = startupPrevious.filter { entry in
      !seenIdentities.contains(entry.identity) && (nextLaunch - entry.launch) < Self.startupMaxLaunchAge
    }
    let merged = Array((currentStamped + retainedPrevious).prefix(Self.startupLimit))
    let file = activeStartupFileURL

    if shouldClose {
      startupRecording = false
      startupFlushWorkItem = nil
      startupSeen = []
      startupPrevious = merged
      startupMaxLaunch = nextLaunch
    }
    startupLock.unlock()

    guard !steadyStateUnchanged, let file,
          let data = try? JSONEncoder().encode(merged) else { return }
    try? data.write(to: file, options: .atomic)
  }

  func startupMetricsSnapshot() -> StartupMetrics {
    startupLock.lock()
    defer { startupLock.unlock() }
    return startupMetrics
  }

  func flushStartupForTesting(forceClose: Bool = true) {
    Self.diskWriter.sync {
      self.startupLock.lock()
      self.startupFlushWorkItem?.cancel()
      self.startupFlushWorkItem = nil
      self.startupLock.unlock()
      self.persistStartupSet(forceClose: forceClose)
    }
  }

  func resetStartupForTesting(fileURL: URL? = nil, keepPreviousFromDisk: Bool = false) {
    Self.diskWriter.sync {
      self.startupLock.lock()
      self.startupFlushWorkItem?.cancel()
      self.startupFlushWorkItem = nil
      self.startupSeen = []
      if !keepPreviousFromDisk {
        self.startupPrevious = []
        self.startupMaxLaunch = 0
      }
      self.startupRecording = nil
      self.startupFirstRequestAt = 0
      self.customStartupFile = fileURL
      self.startupMetrics = StartupMetrics()
      self.startupLock.unlock()
    }
  }

  func clearMemoryCacheForTesting() {
    lock.lock()
    cache.removeAll()
    recency.removeAll()
    bytes = 0
    lock.unlock()
  }

  public func cancel(owner: AnyObject) {
    lock.lock(); defer { lock.unlock() }
    let key = ObjectIdentifier(owner)
    pending.removeValue(forKey: key)
    order.removeAll { $0 == key }
  }

  private func loadManifestIfNeeded() {
    guard metadata == nil else { return }
    let owner = Bundle(for: OneKeyIconBundleToken.self)
    let url = owner.url(forResource: "OneKeyIcons", withExtension: "bundle")
      ?? Bundle.main.url(forResource: "OneKeyIcons", withExtension: "bundle")
    resourceBundle = url.flatMap(Bundle.init(url:))
    if let file = resourceBundle?.url(forResource: "manifest", withExtension: "json"),
       let data = try? Data(contentsOf: file) {
      metadata = (try? JSONSerialization.jsonObject(with: data)) as? [String: [String: Any]]
    }
    if metadata == nil {
      // Hosts own the resources; a missing bundle means every named icon stays blank.
      NSLog("[OneKeyIcon] OneKeyIcons.bundle/manifest.json is missing or invalid; named icons will not render")
      metadata = [:]
    }
  }

  private static func renderKey(name: String, digest: String, width: Int, height: Int,
                                scale: CGFloat, mode: RecolorMode, color: String,
                                channels: [CGFloat]) -> (identity: String, key: String, maskAlpha: CGFloat) {
    let identity = "\(name)|\(digest)|\(width)|\(height)|\(scale)"
    let maskAlpha: CGFloat = mode == .multiple ? channels[3] : 1
    let key = "\(identity)|\(mode.rawValue)|\(mode == .raster ? color : mode == .multiple ? String(Double(maskAlpha)) : "")"
    return (identity, key, maskAlpha)
  }

  /// Main-thread fast path: returns an already-decoded result without disk I/O or
  /// manifest loading, so remounted and recycled views paint in the same frame.
  public func cachedRender(name: String, color: String, width: Int, height: Int,
                           scale: CGFloat) -> RenderResult? {
    noteStartupRequest(name: name, color: color, width: width, height: height, scale: scale)
    guard let channels = Self.channels(color), metadataLock.try() else { return nil }
    let entry = metadata?[name]
    metadataLock.unlock()
    guard let digest = entry?["digest"] as? String else { return nil }
    let mode = (entry?["recolorMode"] as? String).flatMap(RecolorMode.init(rawValue:)) ?? .raster
    let key = Self.renderKey(name: name, digest: digest, width: width, height: height, scale: scale,
                             mode: mode, color: color, channels: channels).key
    lock.lock(); defer { lock.unlock() }
    guard let hit = cache[key] else { return nil }
    recency.removeAll { $0 == key }; recency.append(key)
    return hit
  }

  public func iconMetadata(forName name: String) -> IconMetadata? {
    let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
    guard !trimmed.isEmpty else { return nil }
    metadataLock.lock()
    loadManifestIfNeeded()
    let entry = metadata?[trimmed]
    metadataLock.unlock()
    guard let entry else { return nil }
    let mode = (entry["recolorMode"] as? String).flatMap(RecolorMode.init(rawValue:)) ?? .raster
    let width: Double
    let height: Double
    if let vb = entry["viewBox"] as? [NSNumber], vb.count >= 4 {
      let vbWidth = vb[2].doubleValue
      let vbHeight = vb[3].doubleValue
      width = (vbWidth > 0 && vbHeight > 0) ? vbWidth : 24
      height = (vbWidth > 0 && vbHeight > 0) ? vbHeight : 24
    } else {
      width = 24
      height = 24
    }
    return IconMetadata(width: width, height: height, recolorMode: mode)
  }

  public func request(owner: AnyObject, name: String, color: String, width: Int,
               height: Int, scale: CGFloat, complete: @escaping (UIImage?) -> Void) {
    if Thread.isMainThread {
      if let hit = cachedRender(name: name, color: color, width: width, height: height, scale: scale)
        ?? diskRender(name: name, color: color, width: width, height: height, scale: scale) {
        cancel(owner: owner)
        complete(hit.finalImage(color))
        return
      }
    } else {
      noteStartupRequest(name: name, color: color, width: width, height: height, scale: scale)
    }
    enqueue(owner: owner, name: name, color: color, width: width, height: height, scale: scale,
      finalColor: true) { complete($0?.image) }
  }

  public func requestRender(owner: AnyObject, name: String, color: String, width: Int,
               height: Int, scale: CGFloat, complete: @escaping (RenderResult?) -> Void) {
    if Thread.isMainThread {
      if let hit = cachedRender(name: name, color: color, width: width, height: height, scale: scale)
        ?? diskRender(name: name, color: color, width: width, height: height, scale: scale) {
        cancel(owner: owner)
        complete(hit)
        return
      }
    } else {
      noteStartupRequest(name: name, color: color, width: width, height: height, scale: scale)
    }
    enqueue(owner: owner, name: name, color: color, width: width, height: height, scale: scale,
      finalColor: false, complete: complete)
  }

  private func enqueue(owner: AnyObject, name: String, color: String, width: Int,
               height: Int, scale: CGFloat, finalColor: Bool, complete: @escaping (RenderResult?) -> Void) {
    lock.lock(); defer { lock.unlock() }
    let key = ObjectIdentifier(owner)
    if pending[key] == nil { order.append(key) }
    pending[key] = Request(name: name, color: color, width: width, height: height, scale: scale,
      finalColor: finalColor, complete: complete)
    while workers < 2 && workers < pending.count {
      workers += 1
      DispatchQueue.global(qos: .userInitiated).async { self.drain() }
    }
  }

  private func drain() {
    while true {
      lock.lock()
      guard !order.isEmpty, let request = pending.removeValue(forKey: order.removeFirst()) else {
        workers -= 1; lock.unlock(); return
      }
      lock.unlock()
      let image = autoreleasepool { () -> RenderResult? in
        guard let result = decode(request) else { return nil }
        if !request.finalColor { return result }
        return RenderResult(image: result.finalImage(request.color), mode: .raster,
          sourceIdentity: result.sourceIdentity, maskAlpha: result.maskAlpha)
      }
      DispatchQueue.main.async { request.complete(image) }
    }
  }

  /// Main-thread path for a memory miss: a small icon whose raster is already on
  /// disk is read and bound in the same frame. It never parses SVG, loads the
  /// manifest or waits for a lock; any of those leaves the miss to the workers.
  public func diskRender(name: String, color: String, width: Int, height: Int,
                         scale: CGFloat) -> RenderResult? {
    guard width <= OneKeyMainThreadDecode.maximumIconEdge,
          height <= OneKeyMainThreadDecode.maximumIconEdge else { return nil }
    return OneKeyMainThreadDecode.run {
      decode(Request(name: name, color: color, width: width, height: height, scale: scale,
        finalColor: false, complete: { _ in }), diskOnly: true)
    }
  }

  private func decode(_ request: Request, diskOnly: Bool = false) -> RenderResult? {
    guard (1...4096).contains(request.width), (1...4096).contains(request.height),
          request.width * request.height <= 4_194_304, request.scale.isFinite,
          request.scale > 0,
          let channels = Self.channels(request.color) else { return nil }
    if diskOnly {
      guard metadataLock.try() else {
        OneKeyMainThreadDecode.noteLockContention()
        return nil
      }
      guard metadata != nil else {
        metadataLock.unlock()
        OneKeyMainThreadDecode.noteDiskMiss()
        return nil
      }
    } else {
      metadataLock.lock()
      loadManifestIfNeeded()
    }
    let entry = metadata?[request.name]
    let bundle = resourceBundle
    metadataLock.unlock()
    guard let digest = entry?["digest"] as? String else {
      if diskOnly { OneKeyMainThreadDecode.noteDiskMiss() }
      return nil
    }
    let mode = (entry?["recolorMode"] as? String).flatMap(RecolorMode.init(rawValue:)) ?? .raster
    let (sourceIdentity, key, maskAlpha) = Self.renderKey(
      name: request.name, digest: digest, width: request.width, height: request.height,
      scale: request.scale, mode: mode, color: request.color, channels: channels)
    let paintColor: String
    switch mode {
    case .fixed, .single: paintColor = "rgba(0,0,0,1)"
    case .multiple: paintColor = "rgba(0,0,0,\(maskAlpha))"
    case .raster: paintColor = request.color
    }
    lock.lock()
    if let image = cache[key] {
      recency.removeAll { $0 == key }; recency.append(key)
      lock.unlock(); return image
    }
    lock.unlock()
    // Only workers access disk. The source digest and all raster inputs are in the key.
    let diskCache = Self.rasterDiskCache
    let diskImage = diskCache.imageFromDiskCache(forKey: key)
    if let image = diskImage,
       image.cgImage?.width == request.width, image.cgImage?.height == request.height {
      let result = RenderResult(image: image, mode: mode, sourceIdentity: sourceIdentity, maskAlpha: maskAlpha)
      remember(result, key: key)
      return result
    }
    if diskOnly {
      OneKeyMainThreadDecode.noteDiskMiss()
      return nil
    }
    guard let url = bundle?.url(forResource: request.name, withExtension: "svg"),
          let source = try? String(contentsOf: url, encoding: .utf8),
          let pattern = try? NSRegularExpression(pattern: #"((?:fill|stroke)\s*=\s*["'])currentColor(["'])"#) else { return nil }
    // CoreSVG interprets integer rgba alpha as an 8-bit channel; decimals retain CSS endpoints.
    let svgColor = paintColor.replacingOccurrences(of: #",(0|1)\)$"#, with: ",$1.0)", options: .regularExpression)
    let xml = pattern.stringByReplacingMatches(in: source, range: NSRange(source.startIndex..., in: source),
      withTemplate: "$1\(svgColor)$2")
    guard let data = xml.data(using: .utf8),
          let image = OneKeyImageCoderBridge.decodeSVG(data, pixelSize: CGSize(width: request.width, height: request.height)),
          image.size.width > 0, image.size.height > 0 else { return nil }
    let size = CGSize(width: request.width, height: request.height)
    let format = UIGraphicsImageRendererFormat()
    format.scale = 1
    format.preferredRange = .standard
    let output = UIGraphicsImageRenderer(size: size, format: format).image { _ in
      let ratio = min(size.width / image.size.width, size.height / image.size.height)
      let fitted = CGSize(width: image.size.width * ratio, height: image.size.height * ratio)
      image.draw(in: CGRect(x: (size.width - fitted.width) / 2, y: (size.height - fitted.height) / 2,
                           width: fitted.width, height: fitted.height))
    }
    let result = RenderResult(image: output, mode: mode, sourceIdentity: sourceIdentity, maskAlpha: maskAlpha)
    remember(result, key: key)
    if (output.cgImage?.bytesPerRow ?? request.width * 4) * request.height <= 8_388_608 {
      Self.diskWriter.async {
        if let data = output.pngData() {
          Self.rasterDiskCache.storeImageData(toDisk: data, forKey: key)
        }
      }
    }
    return result
  }

  private func remember(_ result: RenderResult, key: String) {
    let cost = (result.image.cgImage?.bytesPerRow ?? Int(result.image.size.width) * 4) * (result.image.cgImage?.height ?? Int(result.image.size.height))
    if cost <= 8_388_608 {
      lock.lock(); defer { lock.unlock() }
      if let previous = cache[key] { bytes -= (previous.image.cgImage?.bytesPerRow ?? 0) * (previous.image.cgImage?.height ?? 0) }
      cache[key] = result
      recency.removeAll { $0 == key }; recency.append(key)
      bytes += cost
      while cache.count > 128 || bytes > 8_388_608 {
        let oldest = recency.removeFirst()
        if let image = cache.removeValue(forKey: oldest) { bytes -= (image.image.cgImage?.bytesPerRow ?? 0) * (image.image.cgImage?.height ?? 0) }
      }
    }
  }
}
