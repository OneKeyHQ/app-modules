import UIKit

@objc(_TtC18IntercomSDKPrivate30HelpCenterArticleTableViewCell)
class HelpCenterArticleTableViewCell: UITableViewCell {
    let articleView: HelpCenterArticleView
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        articleView = HelpCenterArticleView(frame: .zero, themeProvider: DefaultThemeProvider.shared)
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addSubview(articleView)
        let view = articleView
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let top = view.topAnchor.constraint(equalTo: topAnchor)
        top.priority = .required
        top.isActive = true
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            let constraint = view.leftAnchor.constraint(equalTo: leftAnchor)
            constraint.priority = .required
            constraint.isActive = true
        }
        func right() {
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            let constraint = view.rightAnchor.constraint(equalTo: rightAnchor)
            constraint.priority = .required
            constraint.isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let bottom = view.bottomAnchor.constraint(equalTo: bottomAnchor)
        bottom.priority = .required
        bottom.isActive = true
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
}
