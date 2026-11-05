import UIKit

@objc(ICMDayTimestampDividerView)
class DayTimestampDividerView: UIView {
    @objc var timeLabel: UILabel?
    private let themeProvider: ThemeProvider

    override init(frame: CGRect) {
        themeProvider = DefaultThemeProvider.shared
        super.init(frame: frame)
        setupViews()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func setupViews() {
        timeLabel = UILabel()
        timeLabel!.backgroundColor = .clear
        timeLabel!.textColor = themeProvider.theme.textDefault
        timeLabel!.font = .preferredFont(forTextStyle: .footnote)
        timeLabel!.adjustsFontForContentSizeCategory = true
        timeLabel!.textAlignment = .center
        timeLabel!.translatesAutoresizingMaskIntoConstraints = false
        let container = UIView()
        container.backgroundColor = themeProvider.theme.neutralContainer
        container.layer.cornerRadius = 8
        container.translatesAutoresizingMaskIntoConstraints = false
        addSubview(container)
        container.addSubview(timeLabel!)
        container.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        container.topAnchor.constraint(equalTo: topAnchor, constant: 16).isActive = true
        container.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        timeLabel!.topAnchor.constraint(equalTo: container.topAnchor, constant: 4).isActive = true
        timeLabel!.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: 8).isActive = true
        timeLabel!.bottomAnchor.constraint(equalTo: container.bottomAnchor, constant: -4).isActive = true
        timeLabel!.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: -8).isActive = true
    }
}
