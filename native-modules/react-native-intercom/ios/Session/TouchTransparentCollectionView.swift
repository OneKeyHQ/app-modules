import UIKit

@objc(_TtC18IntercomSDKPrivate30TouchTransparentCollectionView)
class TouchTransparentCollectionView: UICollectionView {
    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        let children = subviews
        for child in children {
            if child.isHidden { continue }
            if !(child.alpha > 0) { continue }
            if !child.isUserInteractionEnabled { continue }
            let converted = convert(point, to: child as UICoordinateSpace)
            if child.point(inside: converted, with: event) { return true }
        }
        return false
    }

    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout) {
        super.init(frame: frame, collectionViewLayout: layout)
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
}
