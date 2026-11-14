import UIKit

@objc(_TtC18IntercomSDKPrivate23GIFPickerViewController)
class GIFPickerViewController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UISearchBarDelegate {
    // The sole original GIF delegate requirement was WMO-deleted.
    weak var delegate: ConversationViewController?
    let themeProvider: any ThemeProvider = DefaultThemeProvider.shared
    let spinner = LoadingSpinnerView()
    private lazy var loadingView: UIView = {
        let view = UIView()
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var errorView: ICMErrorView? = {
        let title = TranslationManager.messenger.translation(forKey: "gifs_load_error")
        let message = TranslationManager.messenger.translation(forKey: "try_again_minute")
        let error = ICMErrorView(title: title, message: message, showActionButton: false, actionButtonTitle: nil)
        error?.translatesAutoresizingMaskIntoConstraints = false
        return error
    }()
    var gifBlocks: [[AnyHashable: Any]] = []
    var collectionView: UICollectionView!
    var searchBar: UISearchBar!
    let fetchGIFsClosure: (String, @escaping (Result<[[AnyHashable: Any]], Error>) -> Void) -> Void
    var closeButton: UIButton!

    init(fetchGIFs: @escaping (String, @escaping (Result<[[AnyHashable: Any]], Error>) -> Void) -> Void) {
        fetchGIFsClosure = fetchGIFs
        super.init(nibName: nil, bundle: nil)
    }
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        view!.backgroundColor = themeProvider.theme.base
        navigationItem.backButtonDisplayMode = .minimal
        setupSearchBar()
        setupCollectionView()
        fetchGIFs(query: "")
        setupLoadingView()
        setupErrorView()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: animated)
    }
    override func viewDidAppear(_ animated: Bool) { super.viewDidAppear(animated); spinner.startSpinning() }
    @objc private func closeButtonTapped() { dismiss(animated: true, completion: nil) }
    private func setupSearchBar() {
        searchBar = UISearchBar()
        searchBar.backgroundImage = UIImage()
        searchBar.delegate = self
        searchBar.placeholder = TranslationManager.messenger.translation(forKey: "search_gif")
        closeButton = UIButton(type: .close)
        closeButton.addTarget(self, action: #selector(closeButtonTapped), for: .touchUpInside)
        let stack = UIStackView(arrangedSubviews: [searchBar, closeButton])
        stack.axis = .horizontal
        stack.spacing = 8
        stack.alignment = .center
        view!.addSubview(stack)
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.topAnchor.constraint(equalTo: stack.superview!.topAnchor).isActive = true
        pinHorizontal(stack, left: 8, right: 16)
        closeButton.setContentHuggingPriority(.defaultHigh, for: .horizontal)
    }
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        guard let text = searchBar.text, !text.isEmpty else { return }
        fetchGIFs(query: text)
        searchBar.resignFirstResponder()
    }
    private func setupCollectionView() {
        collectionView = UICollectionView(frame: .zero, collectionViewLayout: createCompositionalLayout())
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.register(GIFCell.self, forCellWithReuseIdentifier: "GIFCellReuseIdentifier")
        view!.addSubview(collectionView)
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        collectionView.topAnchor.constraint(equalTo: searchBar.bottomAnchor).isActive = true
        pinHorizontal(collectionView)
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        collectionView.bottomAnchor.constraint(equalTo: collectionView.superview!.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
    private func createCompositionalLayout() -> UICollectionViewLayout {
        let size = NSCollectionLayoutSize(widthDimension: .fractionalWidth(1), heightDimension: .fractionalWidth(1.0 / 3.0))
        let item = NSCollectionLayoutItem(layoutSize: size)
        item.contentInsets = NSDirectionalEdgeInsets(top: 5, leading: 5, bottom: 5, trailing: 5)
        let groupSize = NSCollectionLayoutSize(widthDimension: .fractionalWidth(1), heightDimension: .fractionalWidth(1.0 / 3.0))
        let group = NSCollectionLayoutGroup.horizontal(layoutSize: groupSize, subitem: item, count: 3)
        let section = NSCollectionLayoutSection(group: group)
        return UICollectionViewCompositionalLayout(section: section)
    }
    private func setupLoadingView() {
        view!.addSubview(loadingView)
        loadingView.translatesAutoresizingMaskIntoConstraints = false
        loadingView.topAnchor.constraint(equalTo: loadingView.superview!.topAnchor).isActive = true
        pinHorizontal(loadingView)
        loadingView.translatesAutoresizingMaskIntoConstraints = false
        loadingView.bottomAnchor.constraint(equalTo: loadingView.superview!.bottomAnchor, constant: -CGFloat.zero).isActive = true
        loadingView.addSubview(spinner)
        spinner.translatesAutoresizingMaskIntoConstraints = false
        spinner.centerXAnchor.constraint(equalTo: spinner.superview!.centerXAnchor).isActive = true
        spinner.translatesAutoresizingMaskIntoConstraints = false
        spinner.centerYAnchor.constraint(equalTo: spinner.superview!.centerYAnchor).isActive = true
    }
    private func setupErrorView() {
        guard let error = errorView else { return }
        error.isHidden = true
        view!.addSubview(error)
        error.translatesAutoresizingMaskIntoConstraints = false
        error.centerXAnchor.constraint(equalTo: error.superview!.centerXAnchor).isActive = true
        error.translatesAutoresizingMaskIntoConstraints = false
        error.centerYAnchor.constraint(equalTo: error.superview!.centerYAnchor).isActive = true
    }
    private func fetchGIFs(query: String) {
        if let errorView { GIFPickerViewController.animateVisibility(errorView, to: false) }
        GIFPickerViewController.animateVisibility(loadingView, to: true)
        fetchGIFsClosure(query) { [weak self] result in
            switch result {
            case .success(let blocks):
                self?.gifBlocks = blocks
                let collection: UICollectionView
                do { guard let current = self else { break }; collection = current.collectionView! }
                collection.reloadData()
            case .failure(let error):
                let errorView = self?.errorView
                if let errorView { GIFPickerViewController.animateVisibility(errorView, to: true) }
                ICMLogger.log(error.localizedDescription, type: .error)
            }
            let loading = self?.loadingView
            if let loading { GIFPickerViewController.animateVisibility(loading, to: false) }
        }
    }
    // Source expression of the original shared UIView helper at 0x3263b4.
    private static func animateVisibility(_ target: UIView, to visible: Bool) {
        if visible != target.isHidden { return }
        if visible { target.alpha = 0; target.isHidden = false }
        let animation = UIViewPropertyAnimator(duration: 0.3, curve: .easeInOut) { target.alpha = visible ? 1 : 0 }
        animation.addCompletion { position in
            if position == .end, !visible { target.isHidden = true }
        }
        animation.startAnimation()
    }
    private var gifs: [String] { gifBlocks.compactMap { $0["previewUrl"] as? String } }
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int { gifs.count }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "GIFCellReuseIdentifier", for: indexPath) as? GIFCell
        let url = gifs[indexPath.item]
        if let cell { cell.configure(url: url); return cell }
        return UICollectionViewCell()
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let block = gifBlocks[indexPath.item]
        guard let preview = block["previewUrl"] as? String, let url = URL(string: preview) else { return }
        let queue = AsyncQueue.main
        let fileManager = FileManager.default
        let session = URLSession.shared
        let callbackQueue = DispatchQueue.main
        let manager = AttachmentFileManager(fileManager: fileManager, urlSession: session, callbackQueue: callbackQueue)
        let helper = PreviewHelper(mainQueue: queue, attachmentFileManager: manager, onFailure: nil)
        let title = TranslationManager.messenger.translation(forKey: "send")
        let action = PreviewAction(mode: .view, buttonType: .text(title: title), handler: { [weak captured = delegate, block] _ in
            guard let captured else { return }
            let model = captured.viewModel!
            model.didSelectGIF(block: block)
        })
        helper.preview(urls: [url], action: action, from: navigationController)
    }
    // Source expression of the original inlined physical horizontal constraints.
    private func pinHorizontal(_ child: UIView, left: CGFloat = 0, right: CGFloat = 0) {
        let rtl = child.effectiveUserInterfaceLayoutDirection != .leftToRight
        func leading() {
            let parent = child.superview!
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.leftAnchor.constraint(equalTo: parent.leftAnchor, constant: left).isActive = true
        }
        func trailing() {
            let parent = child.superview!
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -right).isActive = true
        }
        if rtl { trailing(); leading() } else { leading(); trailing() }
    }
}
