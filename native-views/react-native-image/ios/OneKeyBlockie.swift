// OneKey patch: Render the versioned local avatar URI without a JS PNG payload.
// Algorithm ported from ethereum-blockies-base64 1.0.2 by MyCrypto (MIT):
// https://github.com/MyCryptoHQ/ethereum-blockies-base64
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

import CoreGraphics
import Foundation
import ImageIO

struct OneKeyBlockieDescriptor {
  let seed: String
  let cacheKey: String

  init?(url: URL) {
    guard let parts = URLComponents(url: url, resolvingAgainstBaseURL: false),
      parts.scheme == "onekey-avatar", parts.host == "blockie",
      parts.user == nil, parts.password == nil, parts.port == nil,
      parts.query == nil, parts.fragment == nil,
      parts.percentEncodedPath.hasPrefix("/v1/")
    else { return nil }
    let encoded = String(parts.percentEncodedPath.dropFirst(4))
    guard !encoded.contains("/"), let seed = encoded.removingPercentEncoding,
      !seed.isEmpty
    else { return nil }
    // The caller already applied JS lowercase. ASCII keys preserve distinct
    // UTF16 seeds that Swift String otherwise compares as canonically equal.
    let allowed = CharacterSet(charactersIn:
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.!~*'()")
    guard let canonical = seed.addingPercentEncoding(withAllowedCharacters: allowed) else { return nil }
    self.seed = seed
    cacheKey = "onekey-avatar://blockie/v1/\(canonical)"
  }
}

enum OneKeyBlockie {
  static let pixelSize = 128

  private struct Random {
    var state = [Int32](repeating: 0, count: 4)

    init(seed: String) {
      for (index, unit) in seed.utf16.enumerated() {
        let slot = index % 4
        state[slot] = (state[slot] &<< 5) &- state[slot] &+ Int32(unit)
      }
    }

    mutating func next() -> Double {
      let t = state[0] ^ (state[0] &<< 11)
      state[0] = state[1]; state[1] = state[2]; state[2] = state[3]
      state[3] = state[3] ^ (state[3] >> 19) ^ t ^ (t >> 8)
      return Double(UInt32(bitPattern: state[3])) / 2_147_483_648
    }

    mutating func color() -> [UInt8] {
      let h = floor(next() * 360) / 360
      let s = (next() * 60 + 40) / 100
      let l = ((next() + next() + next() + next()) * 25) / 100
      let q = l < 0.5 ? l * (1 + s) : l + s - l * s
      let p = 2 * l - q
      func channel(_ value: Double) -> UInt8 {
        var t = value
        if t < 0 { t += 1 }
        if t > 1 { t -= 1 }
        let value: Double
        if t < 1.0 / 6 { value = p + (q - p) * 6 * t }
        else if t < 1.0 / 2 { value = q }
        else if t < 2.0 / 3 { value = p + (q - p) * (2.0 / 3 - t) * 6 }
        else { value = p }
        return UInt8(truncatingIfNeeded: Int(floor(value * 255 + 0.5)))
      }
      return [channel(h + 1.0 / 3), channel(h), channel(h - 1.0 / 3), 255]
    }
  }

  static func rgba(seed: String, isCancelled: () -> Bool = { false }) -> Data? {
    var random = Random(seed: seed)
    let foreground = random.color(), background = random.color(), spot = random.color()
    var pixels = [UInt8](repeating: 0, count: pixelSize * pixelSize * 4)
    for row in 0..<8 {
      guard !isCancelled() else { return nil }
      let half = (0..<4).map { _ in Int(floor(random.next() * 2.3)) }
      let cells = half + half.reversed()
      for column in 0..<8 {
        let color = cells[column] == 0 ? background : cells[column] == 1 ? foreground : spot
        for y in (row * 16)..<((row + 1) * 16) {
          for x in (column * 16)..<((column + 1) * 16) {
            let offset = (y * pixelSize + x) * 4
            for channel in 0..<4 { pixels[offset + channel] = color[channel] }
          }
        }
      }
    }
    return Data(pixels)
  }

  static func png(seed: String, isCancelled: () -> Bool = { false }) -> Data? {
    guard let data = rgba(seed: seed, isCancelled: isCancelled), !isCancelled(),
      let provider = CGDataProvider(data: data as CFData),
      let image = CGImage(width: pixelSize, height: pixelSize, bitsPerComponent: 8,
        bitsPerPixel: 32, bytesPerRow: pixelSize * 4, space: CGColorSpaceCreateDeviceRGB(),
        bitmapInfo: CGBitmapInfo(rawValue: CGImageAlphaInfo.last.rawValue),
        provider: provider, decode: nil, shouldInterpolate: false, intent: .defaultIntent)
    else { return nil }
    let output = NSMutableData()
    guard let destination = CGImageDestinationCreateWithData(output, "public.png" as CFString, 1, nil)
    else { return nil }
    CGImageDestinationAddImage(destination, image, nil)
    guard CGImageDestinationFinalize(destination), !isCancelled() else { return nil }
    return output as Data
  }
}
