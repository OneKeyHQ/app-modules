import UIKit

extension UICollectionView {
    func scrollToBottom(animated: Bool) {
        let section = numberOfSections - 1
        if section < 0 { return }
        let item = numberOfItems(inSection: section) - 1
        if item < 0 { return }
        scrollToItem(at: IndexPath(item: item, section: section), at: .bottom, animated: animated)
    }
    func hasScrolledPastItem(at indexPath: IndexPath) -> Bool {
        guard let attributes = layoutAttributesForItem(at: indexPath) else { return false }
        let frame = convert(attributes.frame, to: self as any UICoordinateSpace)
        let offset = contentOffset
        let bounds = self.bounds
        let visible = CGRect(origin: offset, size: bounds.size)
        if visible.contains(frame) { return false }
        return frame.maxY < visible.minY
    }
}
