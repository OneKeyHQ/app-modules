import UIKit

@objc(_TtC18IntercomSDKPrivate16QuickReplyButton)
class QuickReplyButton: ICMButton {
    let replyOption: ReplyOption
    private let themeProvider: ThemeProvider
    private let maxWidth: CGFloat
    lazy var calculatedSize: CGSize = {
        let text = replyOption.replyText as NSString
        let width = maxWidth
        let attributes: [NSAttributedString.Key: Any] = [.font: Self.font]
        let rect = text.boundingRect(with: CGSize(width: width - 32, height: .greatestFiniteMagnitude), options: .usesLineFragmentOrigin, attributes: attributes, context: nil)
        return CGSize(width: ceil(rect.size.width) + 32, height: (((rect.size.height + 2) + 2) + 10) + 10)
    }()
    private static let font = UIFontMetrics.default.scaledFont(for: .systemFont(ofSize: 14, weight: .regular))

    init(replyOption: ReplyOption, maxWidth: CGFloat, themeProvider: ThemeProvider) {
        self.replyOption = replyOption
        self.maxWidth = maxWidth
        self.themeProvider = themeProvider
        super.init(title: replyOption.replyText)
        cornerRadius = 40
        configuration = updatedConfig
        applyElevationOneShadow(cornerRadius: 0)
        let size = calculatedSize
        translatesAutoresizingMaskIntoConstraints = false
        let width = widthAnchor.constraint(equalToConstant: size.width)
        width.priority = .required
        width.isActive = true
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(title: String!) { fatalError("init(title:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    override var intrinsicContentSize: CGSize { calculatedSize }

    private var updatedConfig: UIButton.Configuration? {
        guard var config = configuration else { return nil }
        config.cornerStyle = .fixed
        config.attributedTitle = buttonTitle
        config.background = backgroundConfig(from: config.background)
        config.contentInsets = NSDirectionalEdgeInsets(top: 10, leading: 14, bottom: 10, trailing: 14)
        return config
    }
    private var buttonTitle: AttributedString {
        let text = replyOption.replyText
        let font = Self.font
        let color = themeProvider.theme.primaryForeground
        let attributes = AttributeContainer([NSAttributedString.Key.font: font, .foregroundColor: color])
        return AttributedString(text, attributes: attributes)
    }
    private func backgroundConfig(from currentConfig: UIBackgroundConfiguration) -> UIBackgroundConfiguration {
        var background = currentConfig
        background.cornerRadius = 40
        background.strokeWidth = 1
        background.strokeColor = themeProvider.theme.neutralBorderEmphasis
        background.backgroundColor = themeProvider.theme.base
        return background
    }
}
