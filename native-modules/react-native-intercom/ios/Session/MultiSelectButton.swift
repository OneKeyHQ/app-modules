import UIKit

@objc(_TtC18IntercomSDKPrivate17MultiSelectButton)
class MultiSelectButton: UIView {
    let option: String
    var isSelected = false { didSet { selectionDidSet() } }
    private let themeProvider: any ThemeProvider
    private var selectedBorderColor: CGColor
    private var selectedTextColor: UIColor
    private let optionTapped: (String) -> Void
    private lazy var button: UIButton = {
        let button = UIButton()
        let color = themeProvider.theme.base
        UIGraphicsBeginImageContext(CGSize(width: 1, height: 1))
        UIGraphicsGetCurrentContext()!.setFillColor(color.cgColor)
        UIGraphicsGetCurrentContext()!.fill(CGRect(x: 0, y: 0, width: 1, height: 1))
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        button.setBackgroundImage(image, for: .normal)
        button.addTarget(self, action: #selector(buttonTapped(sender:)), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()
    private lazy var label: UILabel = {
        let label = UILabel()
        label.text = option
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var checkmarkImageView: UIImageView = {
        let view = UIImageView()
        view.image = ICMAssets.screenActionCheckmark()
        view.contentMode = .scaleAspectFit
        view.setContentHuggingPriority(UILayoutPriority(500), for: .horizontal)
        view.isHidden = true
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    init(withOption option: String, themeProvider: any ThemeProvider, optionTapped: @escaping (String) -> Void) {
        self.option = option
        self.optionTapped = optionTapped
        self.themeProvider = themeProvider
        selectedBorderColor = themeProvider.theme.neutralBorder.cgColor
        selectedTextColor = themeProvider.theme.textDefault
        super.init(frame: .zero)
        backgroundColor = .clear
        layer.cornerRadius = 8
        clipsToBounds = true
        isSelected = false
        selectionDidSet()
        configure(selectedTintColor: themeProvider.theme.primaryBackground)
        addSubviews()
        isAccessibilityElement = true
        accessibilityTraits = .button
        accessibilityLabel = option
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func selectionDidSet() {
        if isSelected {
            checkmarkImageView.isHidden = false
            let size = UIFont.preferredFont(forTextStyle: .subheadline).fontDescriptor.pointSize
            label.font = UIFont.systemFont(ofSize: size, weight: .bold)
            label.textColor = selectedTextColor
            button.isSelected = true
            layer.borderColor = selectedBorderColor
            layer.borderWidth = 2
        } else {
            checkmarkImageView.isHidden = true
            label.font = .preferredFont(forTextStyle: .subheadline)
            label.textColor = themeProvider.theme.textMuted
            button.isSelected = false
            layer.borderColor = themeProvider.theme.neutralBorder.cgColor
            layer.borderWidth = 1
        }
    }
    func configure(selectedTintColor: UIColor) {
        let color = selectedTintColor.isWhite ? themeProvider.theme.textAlwaysDark : selectedTintColor
        UIGraphicsBeginImageContext(CGSize(width: 1, height: 1))
        UIGraphicsGetCurrentContext()!.setFillColor(color.cgColor)
        UIGraphicsGetCurrentContext()!.fill(CGRect(x: 0, y: 0, width: 1, height: 1))
        let selected = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        button.setBackgroundImage(selected, for: .selected)
        let highlightedColor = color.withAlphaComponent(0.2)
        UIGraphicsBeginImageContext(CGSize(width: 1, height: 1))
        UIGraphicsGetCurrentContext()!.setFillColor(highlightedColor.cgColor)
        UIGraphicsGetCurrentContext()!.fill(CGRect(x: 0, y: 0, width: 1, height: 1))
        let highlighted = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        button.setBackgroundImage(highlighted, for: .highlighted)
        selectedTextColor = ColorHelper.suitableForegroundColor(for: color, theme: themeProvider.theme)
        selectedBorderColor = themeProvider.theme.neutralBorder.cgColor
        checkmarkImageView.tintColor = selectedTextColor
    }
    private func addSubviews() {
        addSubview(button)
        addSubview(label)
        addSubview(checkmarkImageView)
        button.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        button.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        button.topAnchor.constraint(equalTo: topAnchor).isActive = true
        button.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        label.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 12).isActive = true
        label.topAnchor.constraint(equalTo: topAnchor, constant: 12).isActive = true
        label.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -12).isActive = true
        checkmarkImageView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -12).isActive = true
        checkmarkImageView.leadingAnchor.constraint(equalTo: label.trailingAnchor, constant: 12).isActive = true
        checkmarkImageView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        checkmarkImageView.heightAnchor.constraint(equalToConstant: 12).isActive = true
    }
    @objc(buttonTapped:)
    private func buttonTapped(sender: UIButton) {
        optionTapped(option)
        accessibilityTraits = sender.isSelected ? [.button, .selected] : .button
    }
}
