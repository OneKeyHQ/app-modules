import UIKit
import XCTest
@testable import NativeListModule

@MainActor
final class NativeListAccessoryStackTests: XCTestCase {
  private func makeStack(trailing: [[String: Any]]) throws -> NativeListAccessoryStack {
    let stack = NativeListAccessoryStack(frame: CGRect(x: 0, y: 0, width: 160, height: 48))
    let item = try NativeListItem(data: [
      "key": "doge--0",
      "type": "identity",
      "presentation": "networkSelector",
      "height": 48,
      "title": "Dogecoin",
      "trailing": trailing,
    ])
    stack.bind(
      item,
      descriptors: trailing,
      theme: nil,
      style: [:],
      checkboxState: { _, _, fallback in fallback },
      fonts: NativeListFontFamilies()
    )
    stack.layoutIfNeeded()
    return stack
  }

  private func visibleControl(_ stack: NativeListAccessoryStack) throws -> UIView {
    let control = try XCTUnwrap(stack.firstVisibleControl)
    XCTAssertGreaterThan(control.bounds.width, 0)
    XCTAssertGreaterThan(control.bounds.height, 0)
    return control
  }

  // OK-65494: a network balance is display only, so a tap on it must reach the
  // cell and select the row instead of stopping at the accessory button.
  func testValueAccessoryLeavesTouchesToTheRow() throws {
    let stack = try makeStack(trailing: [["kind": "value", "text": "¥110.42"]])
    let value = try visibleControl(stack)
    XCTAssertFalse(value.isUserInteractionEnabled)
    XCTAssertTrue(stack.hitTest(value.center, with: nil) === stack)
  }

  func testValuePairAccessoryLeavesTouchesToTheRow() throws {
    let stack = try makeStack(trailing: [
      ["kind": "valuePair", "primary": "$15.60", "secondary": "0.002 BTC"],
    ])
    let value = try visibleControl(stack)
    XCTAssertFalse(value.isUserInteractionEnabled)
    XCTAssertTrue(stack.hitTest(value.center, with: nil) === stack)
  }

  func testTextAccessoryWithActionKeepsItsOwnTouches() throws {
    let stack = try makeStack(trailing: [
      ["kind": "radio", "checked": true, "actionKey": "network.select"],
    ])
    let radio = try visibleControl(stack)
    XCTAssertTrue(radio.isUserInteractionEnabled)
    XCTAssertTrue(stack.hitTest(radio.center, with: nil) === radio)
  }

  func testRebindRestoresTouchOwnershipOfTheReusedButton() throws {
    let stack = try makeStack(trailing: [["kind": "value", "text": "¥110.42"]])
    let value = try visibleControl(stack)
    XCTAssertFalse(value.isUserInteractionEnabled)
    let radioItem = try NativeListItem(data: [
      "key": "doge--0",
      "type": "identity",
      "presentation": "networkSelector",
      "height": 48,
      "title": "Dogecoin",
    ])
    let radio: [[String: Any]] = [["kind": "radio", "checked": false, "actionKey": "network.select"]]
    stack.bind(
      radioItem,
      descriptors: radio,
      theme: nil,
      style: [:],
      checkboxState: { _, _, fallback in fallback },
      fonts: NativeListFontFamilies()
    )
    stack.layoutIfNeeded()
    let reused = try visibleControl(stack)
    XCTAssertTrue(reused === value)
    XCTAssertTrue(reused.isUserInteractionEnabled)
    XCTAssertTrue(stack.hitTest(reused.center, with: nil) === reused)
  }
}
