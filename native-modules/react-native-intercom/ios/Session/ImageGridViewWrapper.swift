import SwiftUI

@objc(_TtC18IntercomSDKPrivate20ImageGridViewWrapper)
class ImageGridViewWrapper: UIView {
    private let hostingController: UIHostingController<ImageGridView>
    init(rootView: ImageGridView) {
        hostingController = UIHostingController(rootView: rootView)
        super.init(frame: .zero)
        setupHostingController()
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func setupHostingController() {
        hostingController.view!.backgroundColor = .clear
        hostingController.view!.translatesAutoresizingMaskIntoConstraints = false
        addSubview(hostingController.view!)
        NSLayoutConstraint.activate([
            hostingController.view!.topAnchor.constraint(equalTo: topAnchor),
            hostingController.view!.leadingAnchor.constraint(equalTo: leadingAnchor),
            hostingController.view!.trailingAnchor.constraint(equalTo: trailingAnchor),
            hostingController.view!.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }
}
