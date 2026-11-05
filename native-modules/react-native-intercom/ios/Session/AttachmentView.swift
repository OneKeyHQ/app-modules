// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate14AttachmentView)
class AttachmentView: UIView, ICMInputViewProtocol, ITBUploadProgressDelegate {
    weak var delegate: (any AttachmentViewDelegate)?
    @objc var inputValue: Any?
    @objc var isRequired = false
    var dataSource: any AttachmentViewDataSource
    let themeProvider: any ThemeProvider
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var fileTypeIconView: UIImageView = {
        let view = UIImageView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 16).isActive = true
        view.heightAnchor.constraint(equalTo: view.widthAnchor).isActive = true
        return view
    }()
    private lazy var fileNameLabel: UILabel = {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.lineBreakMode = .byTruncatingMiddle
        label.setContentHuggingPriority(.defaultLow, for: .horizontal)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var progressView: ITBUploadProgressButton = {
        let view = ITBUploadProgressButton(stroke: themeProvider.theme.primaryBackground, showCancelIcon: false, size: 14)!
        view.isUserInteractionEnabled = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 14).isActive = true
        view.heightAnchor.constraint(equalTo: view.widthAnchor).isActive = true
        return view
    }()
    enum State: Equatable { case uploading(progress: CGFloat), uploaded, error }
    init(dataSource: any AttachmentViewDataSource, themeProvider: any ThemeProvider) {
        self.dataSource = dataSource
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        setUpView()
        fileNameLabel.text = self.dataSource.fileName
        isAccessibilityElement = true
        accessibilityLabel = fileNameLabel.accessibilityLabel
        accessibilityTraits = .button
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func setUpView() {
        layer.borderWidth = 1
        layer.cornerRadius = 8
        backgroundColor = DefaultThemeProvider.shared.theme.base
        addSubview(stackView)
        stackView.addArrangedSubview(fileTypeIconView)
        stackView.addArrangedSubview(fileNameLabel)
        stackView.addArrangedSubview(progressView)
        NSLayoutConstraint.activate([
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 10),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -10),
            stackView.topAnchor.constraint(equalTo: topAnchor, constant: 10),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -10)
        ])
        renderCurrentState()
        dataSource.upload?.progressDelegate = self
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(viewTapped)))
    }
    func renderCurrentState() {
        let state = dataSource.viewState
        let progress = progressView
        switch state {
        case .uploading: progress.isHidden = false
        case .uploaded, .error: progress.isHidden = true
        }
        if state == .error {
            layer.borderColor = themeProvider.theme.errorBorder.cgColor
            fileNameLabel.textColor = themeProvider.theme.textError
            fileTypeIconView.image = ICMAssets.infoIcon()
        } else {
            layer.borderColor = themeProvider.theme.neutralContainerEmphasis.cgColor
            fileNameLabel.textColor = themeProvider.theme.textDefault
            switch dataSource.mediaType {
            case .image: fileTypeIconView.image = ICMAssets.photoInputIcon()
            case .video: fileTypeIconView.image = ICMAssets.videoInputIcon()
            case .attachment: fileTypeIconView.image = ICMAssets.paperclip()
            }
        }
        if case .uploading(let value) = state { progress.progress = Double(value) }
    }
    @objc func configureErrorState() {
        dataSource.viewState = .error
        inputValue = dataSource.id
        renderCurrentState()
    }
    @objc func configureDefaultState() {
        dataSource.viewState = .uploaded
        inputValue = dataSource.id
        renderCurrentState()
    }
    @objc func isUserInputValid() throws {
        if let error = dataSource.attachmentError { throw error }
    }
    @objc func viewTapped() {
        if let delegate { delegate.attachmentViewTapped(dataSource: dataSource, sourceView: self) }
    }
    @objc func uploadProgressUpdated(_ progress: CGFloat) {
        DispatchQueue.main.async {
            self.dataSource.attachmentUploadDidFail = false
            self.inputValue = self.dataSource.id
            if self.dataSource.attachmentError != nil { self.configureErrorState(); return }
            if progress <= 0 {
                self.dataSource.viewState = .uploaded
                self.inputValue = self.dataSource.id
            } else if progress >= 1 {
                self.dataSource.viewState = .uploaded
                self.inputValue = self.dataSource.id
                if let delegate = self.delegate { delegate.attachmentViewUploadCompleted(uploadId: self.dataSource.id) }
            } else {
                self.dataSource.viewState = .uploading(progress: progress)
                self.inputValue = self.dataSource.id
            }
            self.renderCurrentState()
        }
    }
    @objc func uploadFailed() {
        dataSource.attachmentUploadDidFail = true
        inputValue = dataSource.id
        renderCurrentState()
        delegate?.attachmentUploadFailed()
    }
}
