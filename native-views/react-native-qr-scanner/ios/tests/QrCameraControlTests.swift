import AVFoundation
import UIKit
import XCTest

@testable import QrScanner

final class QrCameraControlTests: XCTestCase {
  @MainActor
  func testPreviewRotationMatchesInterfaceOrientation() {
    XCTAssertEqual(QrPreviewView.rotationAngle(for: .portrait, isExternalCamera: false), 90)
    XCTAssertEqual(QrPreviewView.rotationAngle(for: .portraitUpsideDown, isExternalCamera: false), 270)
    XCTAssertEqual(QrPreviewView.rotationAngle(for: .landscapeLeft, isExternalCamera: false), 180)
    XCTAssertEqual(QrPreviewView.rotationAngle(for: .landscapeRight, isExternalCamera: false), 0)
    XCTAssertNil(QrPreviewView.rotationAngle(for: .unknown, isExternalCamera: false))
  }

  @MainActor
  func testExternalCameraUsesNativeOrientationAfterSwitchingInputs() {
    let angles = [false, true, false].map { isExternalCamera in
      QrPreviewView.rotationAngle(for: .portrait, isExternalCamera: isExternalCamera)
    }
    XCTAssertEqual(angles, [90, 0, 90])

    let orientations: [UIInterfaceOrientation] = [
      .landscapeLeft, .landscapeRight, .portraitUpsideDown, .unknown,
    ]
    for orientation in orientations {
      XCTAssertEqual(QrPreviewView.rotationAngle(for: orientation, isExternalCamera: true), 0)
    }
  }

  func testVirtualDevicesArePreferredOverTheWideLens() {
    XCTAssertEqual(
      QrCameraControl.preferredBackDeviceTypes,
      [.builtInTripleCamera, .builtInDualWideCamera, .builtInDualCamera, .builtInWideAngleCamera])
  }

  func testTripleCameraStartsAtTheWideLensSwitchOver() {
    // iPhone Pro: ultra-wide, wide, telephoto; wide takes over at 2x.
    let factor = QrCameraControl.wideAngleZoomFactor(
      constituentTypes: [.builtInUltraWideCamera, .builtInWideAngleCamera, .builtInTelephotoCamera],
      switchOverFactors: [2, 6])
    XCTAssertEqual(factor, 2)
  }

  func testDualWideCameraStartsAtTheWideLensSwitchOver() {
    let factor = QrCameraControl.wideAngleZoomFactor(
      constituentTypes: [.builtInUltraWideCamera, .builtInWideAngleCamera],
      switchOverFactors: [2])
    XCTAssertEqual(factor, 2)
  }

  func testWideFirstAndPhysicalDevicesStayAtOne() {
    // builtInDualCamera: wide, telephoto.
    XCTAssertEqual(
      QrCameraControl.wideAngleZoomFactor(
        constituentTypes: [.builtInWideAngleCamera, .builtInTelephotoCamera],
        switchOverFactors: [2]),
      1)
    // A physical device has no constituents.
    XCTAssertEqual(
      QrCameraControl.wideAngleZoomFactor(constituentTypes: [], switchOverFactors: []), 1)
    // Inconsistent device facts fall back to 1x instead of trapping.
    XCTAssertEqual(
      QrCameraControl.wideAngleZoomFactor(
        constituentTypes: [.builtInUltraWideCamera, .builtInWideAngleCamera],
        switchOverFactors: []),
      1)
  }

  func testPinchZoomIsClampedToTheDeviceAndTheEightTimesCap() {
    // Triple camera: 1x shown at factor 2, device allows up to 123.75.
    XCTAssertEqual(
      QrCameraControl.clampZoom(500, minFactor: 1, maxFactor: 123.75, wideFactor: 2), 16)
    XCTAssertEqual(
      QrCameraControl.clampZoom(0.2, minFactor: 1, maxFactor: 123.75, wideFactor: 2), 1)
    XCTAssertEqual(
      QrCameraControl.clampZoom(3, minFactor: 1, maxFactor: 123.75, wideFactor: 2), 3)
    // A device whose maximum is below the cap.
    XCTAssertEqual(QrCameraControl.clampZoom(10, minFactor: 1, maxFactor: 4, wideFactor: 1), 4)
  }

  func testCameraListDropsRepeatedIdsKeepingTheFirst() {
    let cameras = QrCameraControl.uniqueById(
      [("back", "back"), ("front", "front"), ("back", "external"), ("usb", "external")],
      id: { $0 })
    XCTAssertEqual(cameras.map(\.0), ["back", "front", "usb"])
    XCTAssertEqual(cameras.map(\.1), ["back", "front", "external"])
  }
}
