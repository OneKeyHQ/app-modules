// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

@objc(_TtC18IntercomSDKPrivate27NumericRatingScaleInputView)
class NumericRatingScaleInputView: UIView, ICMInputViewProtocol, RatingScaleInputView {
    @objc var isRequired: Bool
    var ratingScaleOptionsViewController: RatingScaleOptionsViewController?
    private let scaleStart: Int
    private let scaleEnd: Int
    private let lowerLabel: String?
    private let upperLabel: String?
    private var delegate: (any ICMInputViewDelegate)?
    private var value: NSNumber?
    var numberOptionButtons: [UIButton]?

    init(inputViewDelegate: (any ICMInputViewDelegate)?, scaleStart: Int, scaleEnd: Int,
         lowerLabel: String?, upperLabel: String?, isRequired: Bool, selectedTintColor: UIColor) {
        self.scaleStart = scaleStart
        self.scaleEnd = scaleEnd
        self.lowerLabel = lowerLabel
        self.upperLabel = upperLabel
        self.isRequired = isRequired
        delegate = inputViewDelegate
        super.init(frame: .zero)
        accessibilityIdentifier = "intercom numeric question view"
        setupNumberOptionButtons(selectedTintColor: selectedTintColor)
        setupResponseOptionsView()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupNumberOptionButtons(selectedTintColor: UIColor) {
        numberOptionButtons = (scaleStart...scaleEnd).map { number in
            let button = NumericRatingButton(number: number, selectedTintColor: selectedTintColor, themeProvider: DefaultThemeProvider.shared)
            button.addTarget(self, action: #selector(tappedNumber(button:)), for: .touchUpInside)
            return button
        }
    }
    private func setupResponseOptionsView() {
        let controller = RatingScaleOptionsViewController(with: numberOptionButtons ?? [], lowerLabel: lowerLabel, upperLabel: upperLabel)
        addSubview(controller.view!)
        controller.view!.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            controller.view!.leadingAnchor.constraint(equalTo: leadingAnchor),
            controller.view!.trailingAnchor.constraint(equalTo: trailingAnchor),
            controller.view!.topAnchor.constraint(equalTo: topAnchor),
            controller.view!.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        ratingScaleOptionsViewController = controller
    }
    @objc(tappedNumberWithButton:)
    private func tappedNumber(button: UIButton) {
        let buttons = numberOptionButtons
        if let buttons {
            for option in buttons {
                let selected = button.tag
                option.isSelected = selected == option.tag
            }
        }
        value = NSNumber(value: button.tag)
        delegate?.valueUpdated(value)
    }
    @objc var inputValue: Any? {
        get { value }
        // The original setter deliberately discards its argument without selection or callbacks.
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

@objc(_TtC18IntercomSDKPrivateP33_D9C3FF5CD4B18F18F98809C455309D0F19NumericRatingButton)
private class NumericRatingButton: UIButton {
    private let selectedBorderColor: CGColor
    private let size: CGFloat = 44

    init(number: Int, selectedTintColor: UIColor, themeProvider: any ThemeProvider) {
        let workingColor = selectedTintColor.isWhite ? UIColor.black : selectedTintColor
        let foreground = ColorHelper.suitableForegroundColor(for: workingColor, theme: themeProvider.theme)
        selectedBorderColor = themeProvider.theme.neutralBorder.cgColor
        super.init(frame: .zero)
        tag = number
        setTitle(String(number), for: .normal)
        setTitleColor(themeProvider.theme.textMuted, for: .normal)
        setTitleColor(foreground, for: .selected)
        titleLabel?.adjustsFontForContentSizeCategory = true
        setBackgroundImage(Self.solidImage(themeProvider.theme.neutralContainerAlt), for: .normal)
        setBackgroundImage(Self.solidImage(workingColor), for: .selected)
        layer.cornerRadius = 8
        clipsToBounds = true
        super.isSelected = false
        selectionDidSet()
        translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([heightAnchor.constraint(equalToConstant: size), widthAnchor.constraint(equalToConstant: size)])
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private static func solidImage(_ color: UIColor) -> UIImage? {
        UIGraphicsBeginImageContext(CGSize(width: 1, height: 1))
        UIGraphicsGetCurrentContext()!.setFillColor(color.cgColor)
        UIGraphicsGetCurrentContext()!.fill(CGRect(x: 0, y: 0, width: 1, height: 1))
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
    override var isSelected: Bool {
        get { super.isSelected }
        set {
            super.isSelected = newValue
            selectionDidSet()
        }
    }
    private func selectionDidSet() {
        if super.isSelected {
            layer.borderWidth = 2
            layer.borderColor = selectedBorderColor
            if let titleLabel {
                let size = UIFont.preferredFont(forTextStyle: .headline).fontDescriptor.pointSize
                titleLabel.font = UIFont.systemFont(ofSize: size, weight: .bold)
            }
        } else {
            layer.borderWidth = 1
            layer.borderColor = CGColor(red: 0, green: 0, blue: 0, alpha: 0.1)
            if let titleLabel {
                let size = UIFont.preferredFont(forTextStyle: .headline).fontDescriptor.pointSize
                titleLabel.font = UIFont.systemFont(ofSize: size, weight: .regular)
            }
        }
    }
}
