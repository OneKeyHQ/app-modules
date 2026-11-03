import Foundation
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
    if metadata == nil { metadata = [:] }
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
    enqueue(owner: owner, name: name, color: color, width: width, height: height, scale: scale,
      finalColor: true) { complete($0?.image) }
  }

  public func requestRender(owner: AnyObject, name: String, color: String, width: Int,
               height: Int, scale: CGFloat, complete: @escaping (RenderResult?) -> Void) {
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

  private func decode(_ request: Request) -> RenderResult? {
    guard (1...4096).contains(request.width), (1...4096).contains(request.height),
          request.width * request.height <= 4_194_304, request.scale.isFinite,
          request.scale > 0,
          let channels = Self.channels(request.color) else { return nil }
    metadataLock.lock()
    loadManifestIfNeeded()
    let entry = metadata?[request.name]
    let bundle = resourceBundle
    metadataLock.unlock()
    guard let digest = entry?["digest"] as? String,
          let url = bundle?.url(forResource: request.name, withExtension: "svg") else { return nil }
    let mode = (entry?["recolorMode"] as? String).flatMap(RecolorMode.init(rawValue:)) ?? .raster
    let sourceIdentity = "\(request.name)|\(digest)|\(request.width)|\(request.height)|\(request.scale)"
    let maskAlpha: CGFloat = mode == .multiple ? channels[3] : 1
    let paintColor: String
    switch mode {
    case .fixed, .single: paintColor = "rgba(0,0,0,1)"
    case .multiple: paintColor = "rgba(0,0,0,\(maskAlpha))"
    case .raster: paintColor = request.color
    }
    let key = "\(sourceIdentity)|\(mode.rawValue)|\(mode == .raster ? request.color : mode == .multiple ? String(Double(maskAlpha)) : "")"
    lock.lock()
    if let image = cache[key] {
      recency.removeAll { $0 == key }; recency.append(key)
      lock.unlock(); return image
    }
    lock.unlock()
    guard let source = try? String(contentsOf: url, encoding: .utf8),
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
    let cost = (output.cgImage?.bytesPerRow ?? request.width * 4) * request.height
    if cost <= 8_388_608 {
      lock.lock(); defer { lock.unlock() }
      if let previous = cache[key] { bytes -= (previous.image.cgImage?.bytesPerRow ?? request.width * 4) * request.height }
      cache[key] = result
      recency.removeAll { $0 == key }; recency.append(key)
      bytes += cost
      while cache.count > 128 || bytes > 8_388_608 {
        let oldest = recency.removeFirst()
        if let image = cache.removeValue(forKey: oldest) { bytes -= (image.image.cgImage?.bytesPerRow ?? 0) * (image.image.cgImage?.height ?? 0) }
      }
    }
    return result
  }
}
