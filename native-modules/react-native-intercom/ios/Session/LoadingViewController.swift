import UIKit

@objc(_TtC18IntercomSDKPrivate21LoadingViewController)
class LoadingViewController: UIViewController {
    private let loadingImage: UIImage?
    private let accessibilityText: String?
    private var spinner: LoadingSpinnerView?

    @objc init(image: UIImage?, accessibilityText: String?) {
        loadingImage = image
        self.accessibilityText = accessibilityText
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    @available(*, unavailable)
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        if let loadingImage { setupShimmerView(image: loadingImage) }
        else { setupSpinner() }
    }

    private func setupShimmerView(image: UIImage) {
        let shimmer = ShimmeringView(frame: view.bounds)
        let imageView = UIImageView(image: image.imageFlippedForRightToLeftLayoutDirection())
        imageView.isAccessibilityElement = true
        imageView.accessibilityLabel = accessibilityText
        imageView.accessibilityTraits = .image
        imageView.layer.masksToBounds = true
        shimmer.contentView = imageView
        shimmer.isShimmering = true
        view.addSubview(shimmer)
        shimmer.translatesAutoresizingMaskIntoConstraints = false
        let width = shimmer.widthAnchor.constraint(equalToConstant: 400)
        width.priority = UILayoutPriority(998)
        let leading = shimmer.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 24)
        leading.priority = UILayoutPriority(999)
        let aspect = image.size.height == 0 ? 1 : image.size.width / image.size.height
        NSLayoutConstraint.activate([
            shimmer.widthAnchor.constraint(lessThanOrEqualToConstant: 400),
            width,
            leading,
            shimmer.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            shimmer.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 24),
            shimmer.widthAnchor.constraint(equalTo: shimmer.heightAnchor, multiplier: aspect)
        ])
        shimmer.shimmeringSpeed = image.size.width * 0.6
    }

    private func setupSpinner() {
        let spinner = LoadingSpinnerView()
        spinner.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(spinner)
        NSLayoutConstraint.activate([
            spinner.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            spinner.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
        self.spinner = spinner
    }

    override func viewIsAppearing(_ animated: Bool) {
        super.viewIsAppearing(animated)
        spinner?.startSpinning()
    }

    @objc func hideLoadingSpinner() {
        if let spinner {
            spinner.spinnerImageView.layer.removeAnimation(forKey: "spin")
            spinner.isHidden = true
            self.spinner?.removeFromSuperview()
        }
    }
}
