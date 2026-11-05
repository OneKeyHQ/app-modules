import UIKit

private let elevationOneShadowColor = UIColor { traits in
    UIColor(red: 0.059, green: 0.059, blue: 0.059, alpha: traits.userInterfaceStyle == .dark ? 0.54 : 0.06)
}

extension UIView {
    // The reached original Shadow tag is 2; its private case spelling remains unproved.
    func applyElevationOneShadow(cornerRadius: CGFloat) {
        layer.cornerRadius = cornerRadius
        layer.masksToBounds = false
        layer.shadowColor = elevationOneShadowColor.cgColor
        layer.shadowOffset = CGSize(width: 0, height: 1)
        layer.shadowRadius = 4
        layer.shadowOpacity = 1
        layer.shadowPath = UIBezierPath(roundedRect: bounds, cornerRadius: cornerRadius).cgPath
    }
}
