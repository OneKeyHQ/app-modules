import UIKit

extension UIStackView {
    @objc func addArrangedSubviews(_ views: [UIView]) {
        for view in views { addArrangedSubview(view) }
    }
}
