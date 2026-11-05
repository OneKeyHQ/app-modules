import UIKit

protocol HelpCenterBrowseTableViewCellDelegate: AnyObject {
    func didTapBrowseAllHelpTopicsButton()
}

@objc(_TtC18IntercomSDKPrivate29HelpCenterBrowseTableViewCell)
class HelpCenterBrowseTableViewCell: UITableViewCell {
    weak var delegate: (any HelpCenterBrowseTableViewCellDelegate)?
    private let themeProvider = DefaultThemeProvider.shared
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.numberOfLines = 0
        label.textColor = themeProvider.theme.textDefault
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .semibold)
        return label
    }()
    private lazy var chevronImageView: UIImageView = {
        let view = UIImageView()
        view.image = ICMAssets.helpCenterChevron()!.imageFlippedForRightToLeftLayoutDirection()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.accessibilityIdentifier = "intercom help center collection cell arrow"
        view.tintColor = themeProvider.theme.primaryForeground
        return view
    }()
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        accessibilityIdentifier = "intercom help center browse all topics button"
        setup()
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(buttonTapped)))
        backgroundColor = .clear
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func setup() {
        contentView.addSubview(titleLabel)
        contentView.addSubview(chevronImageView)
        NSLayoutConstraint.activate([
            chevronImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            chevronImageView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            chevronImageView.heightAnchor.constraint(equalToConstant: 16), chevronImageView.widthAnchor.constraint(equalToConstant: 16),
            titleLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            titleLabel.trailingAnchor.constraint(equalTo: chevronImageView.leadingAnchor, constant: 16),
            titleLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 16),
            titleLabel.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -16)
        ])
    }
    @objc private func buttonTapped() { delegate?.didTapBrowseAllHelpTopicsButton() }
}
