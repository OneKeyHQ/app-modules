import Foundation

enum OneKeyTosURL {
  // Select by layout size first. Density can only choose between this tier's renditions.
  static let sizeTiers: [(maxDisplaySize: CGFloat, standard: Int, highDensity: Int)] = [
    (48, 96, 160), (96, 192, 320), (192, 384, 640),
    (384, 768, 1280), (.infinity, 1280, 1280),
  ]

  static func optimized(
    rawURL: URL,
    displaySize: CGFloat,
    scale: CGFloat,
    overscan: Double,
    hasCustomIdentity: Bool
  ) -> URL {
    guard !hasCustomIdentity,
      displaySize.isFinite,
      displaySize > 0,
      let host = rawURL.host?.lowercased(),
      isAllowedHost(host),
      !isUnsupportedPath(rawURL.path),
      var components = URLComponents(url: rawURL, resolvingAgainstBaseURL: false),
      !hasProtectedQuery(components.queryItems ?? [])
    else {
      return rawURL
    }

    let tier = sizeTiers.first(where: { displaySize <= $0.maxDisplaySize })!
    let normalizedScale = scale.isFinite ? min(max(scale, 1), 3) : 1
    let normalizedOverscan = overscan.isFinite ? max(overscan, 1) : 1.1
    // Fixed renditions already account for the default margin. Custom margins stay within the tier.
    let density = normalizedScale * CGFloat(normalizedOverscan / 1.1)
    let pixelWidth = density > 2 ? tier.highDensity : tier.standard
    // Match Android and Web without rewriting the source URL's existing query.
    let query = components.percentEncodedQuery ?? ""
    let separator = query.isEmpty || query.hasSuffix("&") ? "" : "&"
    components.percentEncodedQuery = "\(query)\(separator)x-tos-process=image%2Fresize%2Cw_\(pixelWidth)"
    return components.url ?? rawURL
  }

  private static func isAllowedHost(_ host: String) -> Bool {
    [
      "app-assets.onekey.so",
      "common.onekey-asset.com",
      "asset.onekey-asset.com",
      "uni.onekey-asset.com",
      "uni-test.onekey-asset.com",
    ].contains(host)
  }

  private static func isUnsupportedPath(_ path: String) -> Bool {
    let ext = URL(fileURLWithPath: path).pathExtension.lowercased()
    return ["svg", "mp4", "webm", "m4v", "mov", "avi"].contains(ext)
  }

  private static func hasProtectedQuery(_ items: [URLQueryItem]) -> Bool {
    let protectedNames = [
      "expires", "policy", "signature", "token", "auth_key", "accesskeyid",
      "ossaccesskeyid", "security-token",
    ]
    return items.contains { item in
      let name = item.name.lowercased()
      return ["x-amz-", "x-oss-", "x-tos-"].contains(where: name.hasPrefix)
        || protectedNames.contains(name)
    }
  }
}
