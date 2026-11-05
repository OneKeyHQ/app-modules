import UIKit

extension UIView {
    func asTextAttachmentImage() -> UIImage {
        UIGraphicsImageRenderer(bounds: bounds).image { context in
            self.layer.render(in: context.cgContext)
        }
    }
}
