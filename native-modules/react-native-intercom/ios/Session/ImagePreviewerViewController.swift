import UIKit

@objc(_TtC18IntercomSDKPrivate28ImagePreviewerViewController)
class ImagePreviewerViewController: UIViewController, UIScrollViewDelegate {
    private let imageLoader: ITBImageLoader
    let imageView: ITBAnimatedImageView
    private let scrollView: UIScrollView

    init(imageLoader: ITBImageLoader) {
        scrollView = UIScrollView()
        self.imageLoader = imageLoader
        imageView = ITBAnimatedImageView(imageLoader: imageLoader)!
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { scrollView = UIScrollView(); fatalError("") }
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupNavigation()
    }
    private func setupUI() {
        view.backgroundColor = .secondarySystemBackground
        scrollView.delegate = self
        scrollView.minimumZoomScale = 1
        scrollView.maximumZoomScale = 4
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        view.addSubview(scrollView)
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        let safe = view.safeAreaLayoutGuide
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: safe.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: safe.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: safe.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: safe.bottomAnchor),
        ])
        imageView.contentMode = .scaleAspectFit
        scrollView.addSubview(imageView)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            imageView.widthAnchor.constraint(equalTo: scrollView.widthAnchor),
            imageView.heightAnchor.constraint(equalTo: scrollView.heightAnchor),
        ])
        let doubleTap = UITapGestureRecognizer(target: self, action: #selector(handleDoubleTap(_:)))
        doubleTap.numberOfTapsRequired = 2
        scrollView.addGestureRecognizer(doubleTap)
    }
    private func setupNavigation() {
        navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(dismissViewController))
        navigationItem.backButtonDisplayMode = .minimal
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if #available(iOS 26.0, *) { navigationController?.setNavigationBarHidden(false, animated: false) }
        else { navigationController?.setNavigationBarHidden(false, animated: animated) }
    }
    @objc private func handleDoubleTap(_ gesture: UITapGestureRecognizer) {
        if scrollView.zoomScale <= scrollView.minimumZoomScale {
            guard CGRectGetWidth(scrollView.bounds) > 0, CGRectGetHeight(scrollView.bounds) > 0 else { return }
            let point = gesture.location(in: imageView)
            let width = CGRectGetWidth(scrollView.bounds) / 3
            let height = CGRectGetHeight(scrollView.bounds) / 3
            scrollView.zoom(to: CGRect(x: point.x - width / 2, y: point.y - height / 2, width: width, height: height), animated: true)
        } else { scrollView.setZoomScale(scrollView.minimumZoomScale, animated: true) }
    }
    @objc private func dismissViewController() { dismiss(animated: true, completion: nil) }
    func viewForZooming(in scrollView: UIScrollView) -> UIView? { imageView }
}
