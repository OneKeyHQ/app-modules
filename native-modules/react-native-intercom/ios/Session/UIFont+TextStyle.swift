import UIKit

extension UIFont {
    var textStyle: UIFont.TextStyle {
        guard let name = fontDescriptor.object(forKey: .textStyle) as? String else { return .body }
        return UIFont.TextStyle(rawValue: name)
    }
}

extension ICMUtils {
    @objc(disableDynamicFontFor:)
    static func disableDynamicFont(for views: [UIView]) {
        for view in views {
            if !view.subviews.isEmpty {
                disableDynamicFont(for: view.subviews)
            } else if let label = view as? UILabel {
                let traits = UITraitCollection(preferredContentSizeCategory: .large)
                let size = UIFont.preferredFont(forTextStyle: label.font!.textStyle, compatibleWith: traits).pointSize
                let attributes = label.font!.fontDescriptor.object(forKey: .traits) as? [UIFontDescriptor.TraitKey: Any] ?? [:]
                let weight = attributes[.weight] as? CGFloat ?? UIFont.Weight.regular.rawValue
                label.font = UIFont.systemFont(ofSize: size, weight: UIFont.Weight(rawValue: weight))
            }
        }
    }
}
