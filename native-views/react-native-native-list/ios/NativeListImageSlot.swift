import CryptoKit
import OneKeyImage
import UIKit

// Request lifetime is independent of row action epochs. An unchanged descriptor
// can outlive a text-only rebind; a new row/slot/source always invalidates it.
final class NativeListImageSlot {
  let view = OneKeyImageReusableView(frame: .zero)
  private var identity: String?
  private var epoch = 0
  private var retry: DispatchWorkItem?
  private var completion: ((Bool) -> Void)?
  private var result: Bool?

  static func signature(_ value: [String: Any]) -> String {
    guard let data = try? JSONSerialization.data(withJSONObject: value, options: [.sortedKeys])
    else { return "" }
    return SHA256.hash(data: data).map { String(format: "%02x", $0) }.joined()
  }

  func bind(
    _ source: [String: Any], key: String, variant: String = "generic",
    fit: String? = nil, placeholder: String = "#0000000F", completion: ((Bool) -> Void)? = nil
  ) {
    self.completion = completion
    let next = Self.signature([
      "source": source, "key": key, "variant": variant,
      "fit": fit ?? source.string("contentFit", default: "cover"), "placeholder": placeholder,
    ])
    if identity == next {
      if let result { completion?(result) }
      return
    }
    recycle()
    self.completion = completion
    identity = next
    configure(
      source, key: key, variant: variant, fit: fit, placeholder: placeholder, attempt: 0,
      token: epoch)
  }

  private func configure(
    _ source: [String: Any], key: String, variant: String, fit: String?, placeholder: String,
    attempt: Int, token: Int
  ) {
    guard token == self.epoch else { return }
    var completed = false
    let headers = source.dictionary("headers").flatMap {
      try? JSONSerialization.data(withJSONObject: $0, options: [.sortedKeys])
    }
    self.view.configure(
      sourceUri: source.string("uri").trimmingCharacters(in: .whitespacesAndNewlines),
      sourceHeadersJson: headers.flatMap { String(data: $0, encoding: .utf8) },
      variant: variant, contentFit: fit ?? source.string("contentFit", default: "cover"),
      cachePolicy: source.string("cachePolicy", default: "memory-disk"),
      autoplay: source.bool("autoplay"),
      recyclingKey: attempt == 0 ? key : "\(key):retry:\(attempt)",
      optimizeTos: attempt == 0 && source.bool("optimizeTos", default: true),
      overscan: source.double("overscan", default: 1.1),
      loadingStrategy: source.string("loadingStrategy", default: "none"),
      placeholderColor: placeholder,
      onLoad: { [weak self] in
        guard let self, self.epoch == token, !completed else { return }
        completed = true
        self.retry?.cancel()
        self.retry = nil
        self.result = true
        self.completion?(true)
      },
      onError: { [weak self] in
        guard let self, self.epoch == token, !completed else { return }
        completed = true
        self.result = false
        self.completion?(false)
        guard attempt < source.int("retryTimes"), self.retry == nil else { return }
        let work = DispatchWorkItem { [weak self] in
          guard let self, self.epoch == token else { return }
          self.retry = nil
          self.configure(
            source, key: key, variant: variant, fit: fit, placeholder: placeholder,
            attempt: attempt + 1, token: token)
        }
        self.retry = work
        DispatchQueue.main.asyncAfter(
          deadline: .now() + Double(Int.random(in: 0...2)), execute: work)
      })
  }

  func recycle() {
    epoch &+= 1
    retry?.cancel()
    retry = nil
    completion = nil
    identity = nil
    result = nil
    view.prepareForReuse()
  }
  deinit { retry?.cancel() }
}

enum NativeListSourceFallbackState {
  private static let sources: NSCache<NSString, NSNumber> = {
    let cache = NSCache<NSString, NSNumber>()
    cache.countLimit = 128
    return cache
  }()

  static func has(_ key: String) -> Bool {
    sources.object(forKey: key as NSString) != nil
  }

  static func remember(_ key: String) {
    sources.setObject(NSNumber(value: true), forKey: key as NSString)
  }

  static func forget(_ key: String) {
    sources.removeObject(forKey: key as NSString)
  }
}

func nativeListSourceFallbackStateKey(_ source: [String: Any]) -> String? {
  let uri = source.string("uri").trimmingCharacters(in: .whitespacesAndNewlines)
  guard !uri.isEmpty else { return nil }
  // The fallback-state cache is process-wide, so key it by a digest of the request identity
  // instead of retaining raw header values, which can carry credentials such as Authorization.
  let headers = source.dictionary("headers") ?? [:]
  let fields: [(name: String, value: String)] = headers.map { key, value in
    (name: key.lowercased(), value: String(describing: value))
  }
  let sortedFields = fields.sorted { lhs, rhs in
    lhs.name == rhs.name ? lhs.value < rhs.value : lhs.name < rhs.name
  }
  var canonicalValue = "\(uri.utf8.count):\(uri)"
  for field in sortedFields {
    canonicalValue +=
      "\(field.name.utf8.count):\(field.name)\(field.value.utf8.count):\(field.value)"
  }
  let digest = SHA256.hash(data: Data(canonicalValue.utf8))
  return digest.map { String(format: "%02x", $0) }.joined()
}
