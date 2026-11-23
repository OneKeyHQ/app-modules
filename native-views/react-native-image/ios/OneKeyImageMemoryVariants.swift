import Foundation

struct OneKeyImageMemoryFamilyKey: Hashable, Sendable {
  let rawURL: URL
  let headersDigest: String?

  init(rawURL: URL, headersJson: String?) {
    self.rawURL = rawURL
    headersDigest =
      OneKeyImageHeadersIdentity(
        headers: OneKeyImageRequestContext.headers(from: headersJson)
      ).cacheKeyComponent
  }
}

struct OneKeyImageMemoryVariant: Hashable, Sendable {
  let requestURL: URL
  let pixelWidth: Int
  let pixelHeight: Int

  init(requestURL: URL, thumbnailPixelSize: CGSize) {
    self.requestURL = requestURL
    pixelWidth = max(Int(thumbnailPixelSize.width.rounded()), 1)
    pixelHeight = max(Int(thumbnailPixelSize.height.rounded()), 1)
  }

  var thumbnailPixelSize: CGSize {
    CGSize(width: pixelWidth, height: pixelHeight)
  }

  var area: Int64 { Int64(pixelWidth) * Int64(pixelHeight) }
}

/// Bounded hints for reproducing size-specific SDWebImage memory-cache keys.
/// SDImageCache remains authoritative; stale hints are removed on probe miss.
final class OneKeyImageMemoryVariantRegistry: @unchecked Sendable {
  static let shared = OneKeyImageMemoryVariantRegistry()

  private static let maximumFamilies = 1024
  private static let maximumVariantsPerFamily = 8
  private static let maximumAspectRatioDrift = 1.1

  private let lock = NSLock()
  private var variantsByFamily: [OneKeyImageMemoryFamilyKey: [OneKeyImageMemoryVariant]] = [:]
  private var familyAccessOrder: [OneKeyImageMemoryFamilyKey] = []

  func record(_ variant: OneKeyImageMemoryVariant, for family: OneKeyImageMemoryFamilyKey) {
    lock.lock()
    defer { lock.unlock() }
    var variants = variantsByFamily[family] ?? []
    variants.removeAll { $0 == variant }
    variants.append(variant)
    if variants.count > Self.maximumVariantsPerFamily {
      variants.removeFirst(variants.count - Self.maximumVariantsPerFamily)
    }
    variantsByFamily[family] = variants
    touch(family)
    while familyAccessOrder.count > Self.maximumFamilies {
      let oldest = familyAccessOrder.removeFirst()
      variantsByFamily.removeValue(forKey: oldest)
    }
  }

  func remove(_ variant: OneKeyImageMemoryVariant, for family: OneKeyImageMemoryFamilyKey) {
    lock.lock()
    defer { lock.unlock() }
    guard var variants = variantsByFamily[family] else { return }
    variants.removeAll { $0 == variant }
    if variants.isEmpty {
      variantsByFamily.removeValue(forKey: family)
      familyAccessOrder.removeAll { $0 == family }
    } else {
      variantsByFamily[family] = variants
    }
  }

  func candidates(
    for family: OneKeyImageMemoryFamilyKey,
    target: OneKeyImageMemoryVariant,
    excluding exactVariants: Set<OneKeyImageMemoryVariant>
  ) -> [OneKeyImageMemoryVariant] {
    lock.lock()
    let variants = variantsByFamily[family] ?? []
    if !variants.isEmpty { touch(family) }
    lock.unlock()
    return Self.orderedCandidates(
      variants: variants,
      target: target,
      excluding: exactVariants
    )
  }

  func clear() {
    lock.lock()
    variantsByFamily.removeAll()
    familyAccessOrder.removeAll()
    lock.unlock()
  }

  static func orderedCandidates(
    variants: [OneKeyImageMemoryVariant],
    target: OneKeyImageMemoryVariant,
    excluding exactVariants: Set<OneKeyImageMemoryVariant> = []
  ) -> [OneKeyImageMemoryVariant] {
    var seen: Set<OneKeyImageMemoryVariant> = []
    let compatible = variants.reversed().filter {
      seen.insert($0).inserted && !exactVariants.contains($0) && aspectRatioIsCompatible($0, target)
    }
    let larger =
      compatible
      .filter { $0.pixelWidth >= target.pixelWidth && $0.pixelHeight >= target.pixelHeight }
      .min { lhs, rhs in lhs.area < rhs.area }
    let smaller =
      compatible
      .filter { $0.pixelWidth <= target.pixelWidth && $0.pixelHeight <= target.pixelHeight }
      .max { lhs, rhs in lhs.area < rhs.area }
    var result: [OneKeyImageMemoryVariant] = []
    if let larger { result.append(larger) }
    if let smaller, smaller != larger { result.append(smaller) }
    return result
  }

  private static func aspectRatioIsCompatible(
    _ candidate: OneKeyImageMemoryVariant,
    _ target: OneKeyImageMemoryVariant
  ) -> Bool {
    let candidateRatio = Double(candidate.pixelWidth) / Double(candidate.pixelHeight)
    let targetRatio = Double(target.pixelWidth) / Double(target.pixelHeight)
    return max(candidateRatio, targetRatio) / min(candidateRatio, targetRatio)
      <= maximumAspectRatioDrift
  }

  private func touch(_ family: OneKeyImageMemoryFamilyKey) {
    familyAccessOrder.removeAll { $0 == family }
    familyAccessOrder.append(family)
  }
}
