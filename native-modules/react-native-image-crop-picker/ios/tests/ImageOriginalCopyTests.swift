import UIKit
import XCTest

@testable import ReactNativeImageCropPicker

final class ImageOriginalCopyTests: XCTestCase {
  func testOriginalCopyPreservesPNGBytesAndDimensions() throws {
    let renderer = UIGraphicsImageRenderer(size: CGSize(width: 12, height: 8), format: {
      let format = UIGraphicsImageRendererFormat()
      format.scale = 1
      return format
    }())
    let bytes = renderer.pngData { context in
      UIColor.red.setFill()
      context.fill(CGRect(x: 0, y: 0, width: 12, height: 8))
    }
    let source = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString + ".png")
    try bytes.write(to: source)
    defer { try? FileManager.default.removeItem(at: source) }
    let config = ImageCropPickerConfig(ImageCropPickerOptions(
      width: nil, height: nil, cropping: false, includeBase64: true, preserveOriginal: true,
      compressImageQuality: 0.1, compressImageMaxWidth: 1, compressImageMaxHeight: 1,
      freeStyleCropEnabled: nil, cropperCircleOverlay: nil, cropperToolbarTitle: nil,
      cropperChooseText: nil, cropperCancelText: nil, cropperRotateButtonsHidden: nil,
      showCropGuidelines: nil, cropperAppearance: nil
    ))
    let result = try ImageCropPickerImageProcessor.copyOriginal(at: source, config: config, filename: "source.png")
    defer { try? ImageCropPickerImageProcessor.removeFile(atPath: result.path) }
    let copied = try XCTUnwrap(URL(string: result.path))
    XCTAssertEqual(try Data(contentsOf: copied), bytes)
    XCTAssertEqual(result.data, bytes.base64EncodedString())
    XCTAssertEqual(result.mime, "image/png")
    XCTAssertEqual(result.width, 12)
    XCTAssertEqual(result.height, 8)
  }

  func testMetadataRejectsNonImageAndOversizedFile() throws {
    let file = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString)
    defer { try? FileManager.default.removeItem(at: file) }
    try Data("not an image".utf8).write(to: file)
    XCTAssertThrowsError(try ImageCropPickerImageProcessor.imageMetadata(at: file))
    let handle = try FileHandle(forWritingTo: file)
    try handle.truncate(atOffset: UInt64(ImageCropPickerImageProcessor.maxOriginalBytes + 1))
    try handle.close()
    XCTAssertThrowsError(try ImageCropPickerImageProcessor.imageMetadata(at: file))
  }

  func testMissingSourceUsesTheDocumentedError() {
    let missing = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString)
    XCTAssertThrowsError(try ImageCropPickerImageProcessor.imageMetadata(at: missing)) { error in
      XCTAssertEqual((error as? ImageCropPickerError)?.code, ImageCropPickerError.noImageData.code)
    }
  }
}
