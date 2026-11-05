import UIKit

@objc(_TtC18IntercomSDKPrivate15VideoPlayButton)
class VideoPlayButton: UIView {
    private let symbolPointSize: CGFloat
    private let inset: CGFloat
    private let onTap: (() -> Void)?
    private lazy var symbolImageView: UIImageView = {
        let configuration = UIImage.SymbolConfiguration(pointSize: symbolPointSize)
        let image = UIImage(systemName: "play.fill", withConfiguration: configuration)
        let view = UIImageView(image: image)
        view.contentMode = .scaleAspectFill
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    init(symbolPointSize: CGFloat, inset: CGFloat, onTap: (() -> Void)?) {
        self.symbolPointSize = symbolPointSize
        self.inset = inset
        self.onTap = onTap
        super.init(frame: .zero)
        setupView()
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(didTouchUpInside)))
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupView() {
        addSubview(symbolImageView)
        NSLayoutConstraint.activate([
            symbolImageView.topAnchor.constraint(equalTo: topAnchor, constant: inset),
            symbolImageView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: inset),
            symbolImageView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -inset),
            symbolImageView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -inset),
            symbolImageView.widthAnchor.constraint(equalTo: symbolImageView.heightAnchor),
        ])
        backgroundColor = DefaultThemeProvider.shared.theme.base
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        let layer = layer
        let width = bounds.width
        let height = bounds.height
        layer.cornerRadius = (height < width ? height : width) * 0.5
    }
    @objc private func didTouchUpInside() { onTap?() }
}
