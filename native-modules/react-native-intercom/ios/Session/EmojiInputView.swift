import UIKit

@objc(_TtC18IntercomSDKPrivate14EmojiInputView)
class EmojiInputView: UIView, ICMInputViewProtocol, RatingScaleInputView {
    @objc var isRequired: Bool
    private weak var delegate: (any ICMInputViewDelegate)?
    var ratingScaleOptionsViewController: RatingScaleOptionsViewController?
    private let emojiOptions: [EmojiOption]
    var emojiButtons: [EmojiImageButton]?
    private var value: NSNumber?
    private let lowerLabel: String?
    private let upperLabel: String?
    private let normalFont = UIFont.systemFont(ofSize: 28)
    private let selectedFont = UIFont.systemFont(ofSize: 34)

    init(inputViewDelegate: (any ICMInputViewDelegate)?, options: [EmojiOption],
         lowerLabel: String?, upperLabel: String?, isRequired: Bool) {
        emojiOptions = options
        self.lowerLabel = lowerLabel
        self.upperLabel = upperLabel
        self.isRequired = isRequired
        delegate = inputViewDelegate
        super.init(frame: .zero)
        setupEmojiButtons()
        setupResponseOptionsView()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupEmojiButtons() {
        emojiButtons = emojiOptions.map { option in
            let button = EmojiImageButton(normalSize: 28, selectedSize: 34, imageUrl: option.imageUrl)
            button.tag = option.value
            button.addTarget(self, action: #selector(tappedEmoji(button:)), for: .touchUpInside)
            return button
        }
    }
    private func setupResponseOptionsView() {
        let buttons: [UIButton] = emojiButtons ?? []
        let controller = RatingScaleOptionsViewController(with: buttons, lowerLabel: lowerLabel, upperLabel: upperLabel)
        addSubview(controller.view!)
        controller.view!.translatesAutoresizingMaskIntoConstraints = false
        controller.view!.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        controller.view!.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        controller.view!.topAnchor.constraint(equalTo: topAnchor).isActive = true
        controller.view!.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        ratingScaleOptionsViewController = controller
    }
    @objc(tappedEmojiWithButton:)
    private func tappedEmoji(button: UIButton) {
        value = NSNumber(value: button.tag)
        delegate?.valueUpdated(value)
        let buttons = emojiButtons
        if let buttons {
            for option in buttons {
                let selected = button.tag
                option.toggle(selected: selected == option.tag)
            }
        }
    }
    @objc var inputValue: Any? {
        get { value }
        // Original setter only bridges and discards the incoming value.
        set {}
    }
    @objc func isUserInputValid() throws {
        var validators: [any ICMValidatorProtocol] = []
        if isRequired { validators.append(ICMEmptyInputValidator()) }
        else if value == nil { return }
        try ICMValidator.validate(value, validators: validators)
    }
    @objc func configureErrorState() {}
    @objc func configureDefaultState() {}
}

@objc(_TtC18IntercomSDKPrivate16EmojiImageButton)
class EmojiImageButton: UIButton {
    private let imageUrl: String
    private let normalSize: Double
    private let selectedSize: Double
    private var rawImage: UIImage?

    init(normalSize: Double, selectedSize: Double, imageUrl: String) {
        self.normalSize = normalSize
        self.selectedSize = selectedSize
        self.imageUrl = imageUrl
        super.init(frame: .zero)
        let loader = ITBImageLoader.default()!
        _ = loader.loadImage(withURLString: imageUrl) { [self] object in
            self.rawImage = object as? UIImage
            self.setImage(self.rawImage, for: .normal)
            let firstHeight = self.rawImage?.size.height ?? CGFloat(self.selectedSize)
            let secondHeight = self.rawImage?.size.height ?? CGFloat(self.selectedSize)
            self.transform = CGAffineTransform.identity.scaledBy(
                x: CGFloat(self.normalSize) / firstHeight, y: CGFloat(self.normalSize) / secondHeight)
        }
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    func toggle(selected: Bool) {
        guard let image = rawImage else { return }
        let factor = CGFloat(selected ? selectedSize : normalSize) / image.size.height
        UIView.animate(withDuration: 0.3, delay: 0, usingSpringWithDamping: 0.3, initialSpringVelocity: 7, options: [], animations: {
            self.setImage(selected ? image : image.icm_grayscale(), for: .normal)
            self.transform = CGAffineTransform.identity.scaledBy(x: factor, y: factor)
        }, completion: nil)
    }
}
