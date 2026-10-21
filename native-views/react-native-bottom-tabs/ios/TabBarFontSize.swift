import React
import UIKit

enum TabBarFontSize {
  static let defaultSize: CGFloat = {
    if UIDevice.current.userInterfaceIdiom == .pad {
      return 13.0
    }
    return 10.0
  }()

  static func createFontAttributes(
    size: CGFloat,
    family: String? = nil,
    weight: String? = nil,
    color: UIColor? = nil
  ) -> [NSAttributedString.Key: Any] {
    var attributes: [NSAttributedString.Key: Any] = [:]

    if family != nil || weight != nil {
      attributes[.font] = RCTFont.update(
        nil,
        withFamily: family,
        size: NSNumber(value: size),
        weight: weight,
        style: nil,
        variant: nil,
        scaleMultiplier: 1.0
      )
    } else {
      attributes[.font] = UIFont.boldSystemFont(ofSize: size)
    }

    if let color {
      attributes[.foregroundColor] = color
    }

    return attributes
  }

  static func createNormalStateAttributes(
    fontSize: Int? = nil,
    fontFamily: String? = nil,
    fontWeight: String? = nil,
    inactiveColor: UIColor? = nil
  ) -> [NSAttributedString.Key: Any] {
    let size = fontSize.map(CGFloat.init) ?? defaultSize
    return createFontAttributes(
      size: size,
      family: fontFamily,
      weight: fontWeight,
      color: inactiveColor
    )
  }
}
