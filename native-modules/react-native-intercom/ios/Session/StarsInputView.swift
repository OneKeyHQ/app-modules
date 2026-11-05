// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

@objc(_TtC18IntercomSDKPrivate14StarsInputView)
class StarsInputView: UIView, ICMInputViewProtocol, RatingScaleInputView {
    @objc var isRequired: Bool
    var ratingScaleOptionsViewController: RatingScaleOptionsViewController?
    private let scaleStart: Int
    private let scaleEnd: Int
    private let lowerLabel: String?
    private let upperLabel: String?
    private var delegate: (any ICMInputViewDelegate)?
    var starOptionButtons: [UIButton]?
    private var value: NSNumber?
    private let starWidth: CGFloat = 33

    init(inputViewDelegate: (any ICMInputViewDelegate)?, scaleStart: Int, scaleEnd: Int,
         lowerLabel: String?, upperLabel: String?, isRequired: Bool, selectedTintColor: UIColor) {
        self.scaleStart = scaleStart
        self.scaleEnd = scaleEnd
        self.lowerLabel = lowerLabel
        self.upperLabel = upperLabel
        self.isRequired = isRequired
        delegate = inputViewDelegate
        super.init(frame: .zero)
        accessibilityIdentifier = "intercom star question view"
        setupStarOptionButtons(selectedTintColor: selectedTintColor)
        setupResponseOptionsView()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupStarOptionButtons(selectedTintColor: UIColor) {
        let color = selectedTintColor.isWhite ? UIColor.black : selectedTintColor
        starOptionButtons = (scaleStart...scaleEnd).map { number in
            let button = UIButton()
            button.tag = number
            button.setImage(ICMAssets.starUnselectedImage(), for: .normal)
            button.setImage(ICMAssets.starSelectedImage(), for: .selected)
            button.setBackgroundImage(ICMAssets.starFillImage(), for: .normal)
            button.setBackgroundImage(ICMAssets.starFillImage()?.withTintColor(color), for: .selected)
            button.widthAnchor.constraint(equalToConstant: starWidth).isActive = true
            button.addTarget(self, action: #selector(tappedStar(button:)), for: .touchUpInside)
            if number == 1 {
                button.accessibilityLabel = TranslationManager.messenger.translation(forKey: "surveys_one_star_rating")
            } else {
                button.accessibilityLabel = TranslationManager.messenger.translation(forKey: "surveys_multi_star_rating", params: ["count": String(number)])
            }
            return button
        }
    }
    private func setupResponseOptionsView() {
        let controller = RatingScaleOptionsViewController(with: starOptionButtons ?? [], lowerLabel: lowerLabel, upperLabel: upperLabel)
        addSubview(controller.view!)
        controller.view!.translatesAutoresizingMaskIntoConstraints = false
        controller.view!.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        controller.view!.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        controller.view!.topAnchor.constraint(equalTo: topAnchor).isActive = true
        controller.view!.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        ratingScaleOptionsViewController = controller
    }
    @objc(tappedStarWithButton:)
    private func tappedStar(button: UIButton) {
        let buttons = starOptionButtons
        if let buttons {
            for option in buttons { option.isSelected = option.tag <= button.tag }
        }
        value = NSNumber(value: button.tag)
        delegate?.valueUpdated(value)
    }
    @objc var inputValue: Any? {
        get { value }
        // Original assignment is discarded without updating selection, state or delegates.
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
