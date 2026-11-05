import UIKit

@objc(_TtC18IntercomSDKPrivate16ComposerTextView)
class ComposerTextView: UITextView {
    var minContentHeight: CGFloat = 0
    var maxContentHeight: CGFloat = .greatestFiniteMagnitude

    override init(frame: CGRect, textContainer: NSTextContainer?) { super.init(frame: frame, textContainer: textContainer) }
    required init?(coder: NSCoder) { super.init(coder: coder) }

    override var intrinsicContentSize: CGSize {
        let height: CGFloat
        if bounds.width > 0 {
            let fit = sizeThatFits(CGSize(width: bounds.width, height: .greatestFiniteMagnitude)).height
            let lower = fit <= minContentHeight ? minContentHeight : fit
            height = maxContentHeight < lower ? maxContentHeight : lower
        } else { height = minContentHeight }
        return CGSize(width: UIView.noIntrinsicMetric, height: height)
    }

    func recomputeContentBounds() {
        let oldMax = maxContentHeight
        let oldMin = minContentHeight
        if #available(iOS 16, *) {
            let lineHeight = (font ?? UIFont.preferredFont(forTextStyle: .body)).lineHeight
            let accessibility = traitCollection.preferredContentSizeCategory.isAccessibilityCategory
            maxContentHeight = lineHeight * (accessibility ? 3 : 5)
            minContentHeight = 0
        } else {
            let accessibility = traitCollection.preferredContentSizeCategory.isAccessibilityCategory
            minContentHeight = 36
            maxContentHeight = accessibility ? 72 : 120
        }
        if oldMax != maxContentHeight || oldMin != minContentHeight { invalidateIntrinsicContentSize() }
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if previousTraitCollection?.preferredContentSizeCategory == traitCollection.preferredContentSizeCategory { return }
        recomputeContentBounds()
        let width = bounds.width > 0 ? bounds.width : .greatestFiniteMagnitude
        let size = sizeThatFits(CGSize(width: width, height: .greatestFiniteMagnitude))
        let scroll = size.height > maxContentHeight
        if isScrollEnabled != scroll { isScrollEnabled = scroll; invalidateIntrinsicContentSize() }
    }
}
