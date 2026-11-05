// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate7GIFCell)
class GIFCell: UICollectionViewCell {
    let imageView: ITBAnimatedImageView
    let activityIndicator: UIActivityIndicatorView
    override init(frame: CGRect) {
        imageView = ITBAnimatedImageView(imageLoader: ITBImageLoader.default())!
        activityIndicator = UIActivityIndicatorView(style: .medium)
        super.init(frame: frame)
        configureImage()
        configureLoading()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func configureImage() {
        addSubview(imageView)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.topAnchor.constraint(equalTo: imageView.superview!.topAnchor).isActive = true
        let rtl = imageView.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() { imageView.leftAnchor.constraint(equalTo: imageView.superview!.leftAnchor).isActive = true }
        func right() { imageView.rightAnchor.constraint(equalTo: imageView.superview!.rightAnchor, constant: -CGFloat.zero).isActive = true }
        if rtl { right(); left() } else { left(); right() }
        imageView.bottomAnchor.constraint(equalTo: imageView.superview!.bottomAnchor, constant: -CGFloat.zero).isActive = true
        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true
        imageView.layer.cornerRadius = 1.5
        imageView.backgroundColor = .secondarySystemBackground
    }
    private func configureLoading() {
        contentView.addSubview(activityIndicator)
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        activityIndicator.centerXAnchor.constraint(equalTo: activityIndicator.superview!.centerXAnchor).isActive = true
        activityIndicator.centerYAnchor.constraint(equalTo: activityIndicator.superview!.centerYAnchor).isActive = true
    }
    func configure(url: String) {
        activityIndicator.startAnimating()
        // The original pre-network callback is the genuine bare return at 0x9188.
        imageView.loadImage(withURLString: url, actionsBeforeNetworkLoad: {}, completion: { [weak self] in
            let indicator: UIActivityIndicatorView
            do { guard let current = self else { return }; indicator = current.activityIndicator }
            indicator.stopAnimating()
        })
    }
}
