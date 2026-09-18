import Foundation
import ImageIO
import UIKit

// Plain Swift copy of the options, so no C++ backed struct outlives the JS call.
struct ImageCropPickerConfig {
  let width: Double?
  let height: Double?
  let cropping: Bool
  let includeBase64: Bool
  let compressImageQuality: Double?
  let compressImageMaxWidth: Double?
  let compressImageMaxHeight: Double?
  let freeStyleCropEnabled: Bool
  let cropperCircleOverlay: Bool
  let cropperToolbarTitle: String?
  let cropperChooseText: String?
  let cropperCancelText: String?
  let cropperRotateButtonsHidden: Bool
  let showCropGuidelines: Bool
  let appearance: ImageCropperAppearanceConfig

  init(_ options: ImageCropPickerOptions, forceCropping: Bool = false) {
    width = options.width
    height = options.height
    cropping = forceCropping || (options.cropping ?? false)
    includeBase64 = options.includeBase64 ?? false
    compressImageQuality = options.compressImageQuality
    compressImageMaxWidth = options.compressImageMaxWidth
    compressImageMaxHeight = options.compressImageMaxHeight
    freeStyleCropEnabled = options.freeStyleCropEnabled ?? false
    cropperCircleOverlay = options.cropperCircleOverlay ?? false
    cropperToolbarTitle = options.cropperToolbarTitle
    cropperChooseText = options.cropperChooseText
    cropperCancelText = options.cropperCancelText
    cropperRotateButtonsHidden = options.cropperRotateButtonsHidden ?? false
    showCropGuidelines = options.showCropGuidelines ?? true
    appearance = ImageCropperAppearanceConfig(options.cropperAppearance)
  }

  var targetSize: CGSize? {
    guard let width, let height, width > 0, height > 0 else {
      return nil
    }
    return CGSize(width: width, height: height)
  }
}

struct DecodedImage {
  let image: UIImage
  // Source pixels per decoded pixel. Greater than 1 when the source was downsampled.
  let sourceScale: CGFloat
}

enum ImageCropPickerImageProcessor {
  // Bounds memory for huge photos (48 MP decodes to ~200 MB). Crop targets are
  // far smaller than this, so no visible quality is lost.
  static let maxDecodedPixelSize = 4096
  static let defaultCompressQuality = 0.8
  static let temporaryDirectoryName = "react-native-image-crop-picker"

  static func decodeImage(at url: URL) -> DecodedImage? {
    let sourceOptions = [kCGImageSourceShouldCache: false] as CFDictionary
    guard let source = CGImageSourceCreateWithURL(url as CFURL, sourceOptions) else {
      return nil
    }
    return decodeImage(source: source)
  }

  static func decodeImage(data: Data) -> DecodedImage? {
    let sourceOptions = [kCGImageSourceShouldCache: false] as CFDictionary
    guard let source = CGImageSourceCreateWithData(data as CFData, sourceOptions) else {
      return nil
    }
    return decodeImage(source: source)
  }

  private static func decodeImage(source: CGImageSource) -> DecodedImage? {
    guard CGImageSourceGetCount(source) > 0 else {
      return nil
    }
    let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [CFString: Any]
    let pixelWidth = (properties?[kCGImagePropertyPixelWidth] as? NSNumber)?.intValue ?? 0
    let pixelHeight = (properties?[kCGImagePropertyPixelHeight] as? NSNumber)?.intValue ?? 0
    let sourceMaxDimension = max(pixelWidth, pixelHeight)
    let maxPixelSize = sourceMaxDimension > 0
      ? min(sourceMaxDimension, maxDecodedPixelSize)
      : maxDecodedPixelSize

    // The thumbnail API applies the EXIF orientation, so the result is always `.up`.
    let thumbnailOptions: [CFString: Any] = [
      kCGImageSourceCreateThumbnailFromImageAlways: true,
      kCGImageSourceCreateThumbnailWithTransform: true,
      kCGImageSourceShouldCacheImmediately: true,
      kCGImageSourceThumbnailMaxPixelSize: maxPixelSize,
    ]
    guard let cgImage = CGImageSourceCreateThumbnailAtIndex(
      source,
      0,
      thumbnailOptions as CFDictionary
    ) else {
      return nil
    }

    let decodedMaxDimension = max(cgImage.width, cgImage.height)
    let sourceScale = sourceMaxDimension > 0 && decodedMaxDimension > 0
      ? CGFloat(sourceMaxDimension) / CGFloat(decodedMaxDimension)
      : 1
    return DecodedImage(
      image: UIImage(cgImage: cgImage, scale: 1, orientation: .up),
      sourceScale: max(sourceScale, 1)
    )
  }

  static func loadImage(fromPath path: String) throws -> DecodedImage {
    if path.hasPrefix("data:") {
      guard
        let commaIndex = path.firstIndex(of: ","),
        let data = Data(
          base64Encoded: String(path[path.index(after: commaIndex)...]),
          options: .ignoreUnknownCharacters
        ),
        let decoded = decodeImage(data: data)
      else {
        throw ImageCropPickerError.cropperImageNotFound
      }
      return decoded
    }

    if let url = URL(string: path), let scheme = url.scheme?.lowercased(),
       scheme == "http" || scheme == "https" {
      guard let data = try? downloadData(from: url), let decoded = decodeImage(data: data) else {
        throw ImageCropPickerError.cropperImageNotFound
      }
      return decoded
    }

    guard let fileURL = fileURL(fromPath: path),
          let decoded = decodeImage(at: fileURL) else {
      throw ImageCropPickerError.cropperImageNotFound
    }
    return decoded
  }

  static func fileURL(fromPath path: String) -> URL? {
    if path.hasPrefix("file://") {
      if let url = URL(string: path), url.isFileURL {
        return url
      }
      // Unescaped paths (e.g. containing spaces) are not valid URL strings.
      let rawPath = String(path.dropFirst("file://".count))
      return URL(fileURLWithPath: rawPath.removingPercentEncoding ?? rawPath)
    }
    if path.hasPrefix("/") {
      return URL(fileURLWithPath: path)
    }
    return nil
  }

  private final class DownloadResult: @unchecked Sendable {
    var data: Data?
  }

  // Called from a background queue only.
  private static func downloadData(from url: URL) throws -> Data {
    let semaphore = DispatchSemaphore(value: 0)
    let result = DownloadResult()
    let task = URLSession.shared.dataTask(with: url) { data, response, error in
      defer { semaphore.signal() }
      guard error == nil else {
        return
      }
      if let httpResponse = response as? HTTPURLResponse,
         !(200..<300).contains(httpResponse.statusCode) {
        return
      }
      result.data = data
    }
    task.resume()
    semaphore.wait()
    guard let data = result.data else {
      throw ImageCropPickerError.cropperImageNotFound
    }
    return data
  }

  // Scales a cropped image to the requested size; smaller crops are scaled up.
  // With a locked aspect ratio the crop box can still end up a few pixels off
  // that ratio, so the result is scaled to exactly the requested size.
  // Free-style crops keep their own aspect ratio and fit inside it.
  static func resizeCroppedImage(_ image: UIImage, config: ImageCropPickerConfig) -> UIImage {
    guard let targetSize = config.targetSize else {
      return image
    }
    let exactSize = CGSize(width: targetSize.width.rounded(), height: targetSize.height.rounded())
    guard config.freeStyleCropEnabled else {
      return draw(image, size: exactSize)
    }
    let sourceSize = pixelSize(of: image)
    guard sourceSize.width > 0, sourceSize.height > 0 else {
      return image
    }
    let widthRatio = exactSize.width / sourceSize.width
    let heightRatio = exactSize.height / sourceSize.height
    let destinationSize = widthRatio < heightRatio
      ? CGSize(width: exactSize.width, height: (sourceSize.height * widthRatio).rounded())
      : CGSize(width: (sourceSize.width * heightRatio).rounded(), height: exactSize.height)
    return draw(image, size: destinationSize)
  }

  static func makeResult(
    image: UIImage,
    config: ImageCropPickerConfig,
    cropRect: CGRect?,
    filename: String?
  ) throws -> PickedImage {
    var output = image
    let size = pixelSize(of: output)
    let maxWidth = config.compressImageMaxWidth.map { CGFloat($0) }
    let maxHeight = config.compressImageMaxHeight.map { CGFloat($0) }
    let shouldResizeWidth = maxWidth.map { $0 < size.width } ?? false
    let shouldResizeHeight = maxHeight.map { $0 < size.height } ?? false
    if shouldResizeWidth || shouldResizeHeight {
      let scale = min(
        (maxWidth ?? size.width) / size.width,
        (maxHeight ?? size.height) / size.height
      )
      output = draw(
        output,
        size: CGSize(width: floor(size.width * scale), height: floor(size.height * scale))
      )
    }

    let quality = min(max(config.compressImageQuality ?? defaultCompressQuality, 0), 1)
    guard let data = output.jpegData(compressionQuality: CGFloat(quality)) else {
      throw ImageCropPickerError.cannotSaveImage
    }

    let fileURL = try temporaryDirectory()
      .appendingPathComponent(UUID().uuidString)
      .appendingPathExtension("jpg")
    do {
      try data.write(to: fileURL, options: .atomic)
    } catch {
      throw ImageCropPickerError.cannotSaveImage
    }

    let outputSize = pixelSize(of: output)
    return PickedImage(
      path: fileURL.absoluteString,
      size: Double(data.count),
      width: Double(outputSize.width),
      height: Double(outputSize.height),
      mime: "image/jpeg",
      data: config.includeBase64 ? data.base64EncodedString() : nil,
      cropRect: cropRect.map {
        CropRect(
          x: Double($0.origin.x),
          y: Double($0.origin.y),
          width: Double($0.width),
          height: Double($0.height)
        )
      },
      filename: filename
    )
  }

  static func temporaryDirectory() throws -> URL {
    let directory = URL(fileURLWithPath: NSTemporaryDirectory(), isDirectory: true)
      .appendingPathComponent(temporaryDirectoryName, isDirectory: true)
    do {
      try FileManager.default.createDirectory(
        at: directory,
        withIntermediateDirectories: true
      )
    } catch {
      throw ImageCropPickerError.cannotSaveImage
    }
    return directory
  }

  static func cleanTemporaryDirectory() throws {
    let directory = try temporaryDirectory()
    let fileManager = FileManager.default
    do {
      for item in try fileManager.contentsOfDirectory(
        at: directory,
        includingPropertiesForKeys: nil
      ) {
        try fileManager.removeItem(at: item)
      }
    } catch {
      throw ImageCropPickerError.cleanupFailed
    }
  }

  static func removeFile(atPath path: String) throws {
    guard let fileURL = fileURL(fromPath: path) else {
      throw ImageCropPickerError.cleanupFailed
    }
    do {
      try FileManager.default.removeItem(at: fileURL)
    } catch {
      throw ImageCropPickerError.cleanupFailed
    }
  }

  // Parses CSS hex colors: #RGB, #RRGGBB and #RRGGBBAA.
  static func color(fromHex hex: String?) -> UIColor? {
    guard var value = hex?.trimmingCharacters(in: .whitespacesAndNewlines), !value.isEmpty else {
      return nil
    }
    if value.hasPrefix("#") {
      value.removeFirst()
    }
    if value.count == 3 {
      value = value.map { "\($0)\($0)" }.joined()
    }
    guard value.count == 6 || value.count == 8, let rgba = UInt64(value, radix: 16) else {
      return nil
    }
    let hasAlpha = value.count == 8
    let red = CGFloat((rgba >> (hasAlpha ? 24 : 16)) & 0xff) / 255
    let green = CGFloat((rgba >> (hasAlpha ? 16 : 8)) & 0xff) / 255
    let blue = CGFloat((rgba >> (hasAlpha ? 8 : 0)) & 0xff) / 255
    let alpha = hasAlpha ? CGFloat(rgba & 0xff) / 255 : 1
    return UIColor(red: red, green: green, blue: blue, alpha: alpha)
  }

  private static func pixelSize(of image: UIImage) -> CGSize {
    return CGSize(width: image.size.width * image.scale, height: image.size.height * image.scale)
  }

  private static func draw(_ image: UIImage, size: CGSize) -> UIImage {
    guard size.width >= 1, size.height >= 1, size != pixelSize(of: image) else {
      return image
    }
    let format = UIGraphicsImageRendererFormat()
    format.scale = 1
    format.opaque = false
    format.preferredRange = .standard
    let renderer = UIGraphicsImageRenderer(size: size, format: format)
    return renderer.image { _ in
      image.draw(in: CGRect(origin: .zero, size: size))
    }
  }
}
