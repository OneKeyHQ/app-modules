import UIKit

@objc(_TtC18IntercomSDKPrivate30ConversationCollectionViewCell)
class ConversationCollectionViewCell: UICollectionViewCell {
    var cellBottomPoint: CGFloat = 0
    private var widthConstraint: NSLayoutConstraint?

    override init(frame: CGRect) { super.init(frame: frame) }
    required init?(coder: NSCoder) { super.init(coder: coder) }

    override func prepareForReuse() {
        super.prepareForReuse()
        for view in contentView.subviews { view.removeFromSuperview() }
    }
    // Concrete default consumer preserves identity while the original inputtable requirement remains unrecovered.
    func configure(with type: ConversationCellType, availableWidth: CGFloat, delegate: ConversationViewModel?) {
        configureContentView(with: type, availableWidth: availableWidth, delegate: delegate)
    }
    private func configureContentView(with type: ConversationCellType, availableWidth: CGFloat, delegate: ConversationViewModel?) {
        guard let renderer = ConversationCellViewFactory.getView(for: type, delegate: delegate, maxWidth: availableWidth) else { return }
        let padding: CGFloat
        switch type {
        case .message(let part), .standaloneBlock(let part):
            if part.groupingPosition == 0 { padding = 0 }
            else if part.groupingPosition == 1 { padding = 0 }
            else { padding = part.hasSegmentBelow ? 2 : 12 }
        default: padding = 12
        }
        contentView.addSubview(renderer)
        if let widthConstraint { widthConstraint.constant = availableWidth }
        else {
            let container = contentView
            container.translatesAutoresizingMaskIntoConstraints = false
            let width = container.widthAnchor.constraint(equalToConstant: availableWidth)
            width.priority = .required
            width.isActive = true
            widthConstraint = width
        }
        let container = contentView
        container.translatesAutoresizingMaskIntoConstraints = false
        let parent = container.superview!
        for constraint in [
            container.topAnchor.constraint(equalTo: parent.topAnchor),
            container.leadingAnchor.constraint(equalTo: parent.leadingAnchor),
            container.bottomAnchor.constraint(equalTo: parent.bottomAnchor),
            container.trailingAnchor.constraint(equalTo: parent.trailingAnchor)
        ] {
            constraint.priority = UILayoutPriority(750)
            constraint.isActive = true
        }
        let rendererParent = renderer.superview!
        renderer.translatesAutoresizingMaskIntoConstraints = false
        for constraint in [
            renderer.topAnchor.constraint(equalTo: rendererParent.topAnchor),
            renderer.leadingAnchor.constraint(equalTo: rendererParent.leadingAnchor),
            renderer.bottomAnchor.constraint(equalTo: rendererParent.bottomAnchor, constant: -padding),
            renderer.trailingAnchor.constraint(equalTo: rendererParent.trailingAnchor)
        ] {
            constraint.priority = .required
            constraint.isActive = true
        }
    }
}
