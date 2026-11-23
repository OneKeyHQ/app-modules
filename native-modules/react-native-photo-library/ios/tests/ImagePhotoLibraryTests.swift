import Photos
import UIKit
import XCTest

@testable import ReactNativePhotoLibrary

final class ImagePhotoLibraryTests: XCTestCase {
  func testSaveAuthorizationDoesNotTreatDenialAsRequestable() {
    XCTAssertEqual(ImagePhotoLibrary.permission(for: .notDetermined).status, .undetermined)
    XCTAssertTrue(ImagePhotoLibrary.permission(for: .notDetermined).canAskAgain)
    XCTAssertEqual(ImagePhotoLibrary.permission(for: .authorized).status, .granted)
    for status in [PHAuthorizationStatus.denied, .restricted] {
      let permission = ImagePhotoLibrary.permission(for: status)
      XCTAssertEqual(permission.status, .denied)
      XCTAssertFalse(permission.canAskAgain)
    }
  }

  func testSaveInputValidationRejectsMissingNonImageAndOversizedFiles() throws {
    let url = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString)
    defer { try? FileManager.default.removeItem(at: url) }
    XCTAssertThrowsError(try ImagePhotoLibrary.validateImage(at: url))
    try Data("not an image".utf8).write(to: url)
    XCTAssertThrowsError(try ImagePhotoLibrary.validateImage(at: url))
    let handle = try FileHandle(forWritingTo: url)
    try handle.truncate(atOffset: 64 * 1024 * 1024 + 1)
    try handle.close()
    XCTAssertThrowsError(try ImagePhotoLibrary.validateImage(at: url))
  }

  func testSaveInputValidationAcceptsPNGAndRejectsRemoteURIs() throws {
    let url = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString + ".png")
    defer { try? FileManager.default.removeItem(at: url) }
    let data = UIGraphicsImageRenderer(size: CGSize(width: 8, height: 8)).pngData { context in
      UIColor.red.setFill()
      context.fill(CGRect(x: 0, y: 0, width: 8, height: 8))
    }
    try data.write(to: url)
    XCTAssertNoThrow(try ImagePhotoLibrary.validateImage(at: url))
    XCTAssertEqual(ImagePhotoLibrary.fileURL(url.absoluteString), url)
    XCTAssertNil(ImagePhotoLibrary.fileURL("https://example.com/image.png"))
    XCTAssertNil(ImagePhotoLibrary.fileURL("file://remote/path/image.png"))
  }
}
