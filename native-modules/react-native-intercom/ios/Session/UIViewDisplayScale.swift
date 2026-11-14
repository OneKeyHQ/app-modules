import UIKit

extension UIView {
    @objc var icmDisplayScale: CGFloat {
        let scale = traitCollection.displayScale
        return scale.isFinite && scale > 0 ? scale : 1
    }
}
