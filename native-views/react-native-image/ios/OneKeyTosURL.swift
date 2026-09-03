import Foundation

enum OneKeyTosURL {
  static let widthBuckets = [
    32, 40, 48, 64, 96, 128, 160, 200, 256, 320, 480, 640, 960, 1280,
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

    let normalizedScale = scale.isFinite ? min(max(scale, 1), 3) : 1
    let normalizedOverscan = overscan.isFinite ? max(overscan, 1) : 1
    let requestedPixels = ceil(displaySize * normalizedScale * normalizedOverscan)
    let bucket: Int
    if !requestedPixels.isFinite || requestedPixels >= CGFloat(widthBuckets.last!) {
      bucket = widthBuckets.last!
    } else {
      let requested = Int(requestedPixels)
      bucket = widthBuckets.first(where: { $0 >= requested }) ?? widthBuckets.last!
    }
    var queryItems = components.queryItems ?? []
    queryItems.append(URLQueryItem(name: "x-tos-process", value: "image/resize,w_\(bucket)"))
    components.queryItems = queryItems
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
