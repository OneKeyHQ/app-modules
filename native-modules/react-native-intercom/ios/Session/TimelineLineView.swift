import UIKit

@objc(_TtC18IntercomSDKPrivate16TimelineLineView)
class TimelineLineView: UIView {
    enum State: Hashable { case notStarted, inProgress, done }
    var state: State = .notStarted {
        didSet {
            let currentState = state
            let gradient = gradientView
            switch currentState {
            case .notStarted:
                gradient.isHidden = true
                gradient.layer.removeAnimation(forKey: progressAnimationKey)
                backgroundColor = baseColor
            case .inProgress:
                gradient.isHidden = false
                gradient.layer.add(animation, forKey: progressAnimationKey)
            case .done:
                gradient.isHidden = true
                gradient.layer.removeAnimation(forKey: progressAnimationKey)
                backgroundColor = fillColor
            }
        }
    }
    private let fillColor: UIColor
    private let baseColor: UIColor
    private var progressAnimationKey = "ICMTimelineItemProgressAnimationKey"
    private lazy var gradientView: ICMGradientView = {
        let view = ICMGradientView()
        view.layer.colors = [fillColor.cgColor, baseColor.cgColor]
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.startPoint = effectiveUserInterfaceLayoutDirection == .leftToRight ? CGPoint(x: 0, y: 0.5) : CGPoint(x: 1, y: 0.5)
        view.layer.endPoint = effectiveUserInterfaceLayoutDirection == .leftToRight ? CGPoint(x: 1, y: 0.5) : CGPoint(x: 0, y: 0.5)
        view.layer.locations = [NSNumber(floatLiteral: 0), NSNumber(floatLiteral: 0)]
        return view
    }()
    private lazy var animation: CAAnimationGroup = {
        let first = CABasicAnimation(keyPath: "locations")
        first.fromValue = [Double(0), 0]
        first.toValue = [Double(1), 1]
        first.duration = 0.75
        let second = CABasicAnimation(keyPath: "locations")
        second.fromValue = [Double(1), 1]
        second.toValue = [Double(1), 1]
        second.beginTime = 0.75
        second.duration = 0.5
        let third = CABasicAnimation(keyPath: "colors")
        third.fromValue = [fillColor.cgColor, fillColor.cgColor]
        third.toValue = [baseColor.cgColor, baseColor.cgColor]
        third.beginTime = 1.25
        third.duration = 0.75
        let fourth = CABasicAnimation(keyPath: "locations")
        fourth.fromValue = [Int(0), 0]
        fourth.toValue = [Int(0), 0]
        fourth.beginTime = 2
        fourth.duration = 0.5
        let group = CAAnimationGroup()
        group.animations = [first, second, third, fourth]
        group.duration = 2.5
        group.repeatCount = .infinity
        group.timingFunction = CAMediaTimingFunction(name: .easeInEaseOut)
        group.isRemovedOnCompletion = false
        return group
    }()
    init(fillColor: UIColor, baseColor: UIColor) {
        self.fillColor = fillColor
        self.baseColor = baseColor
        super.init(frame: .zero)
        addSubview(gradientView)
        pinEdges(gradientView)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    override func layoutSubviews() {
        super.layoutSubviews()
        addSubview(gradientView)
        pinEdges(gradientView)
    }
    private func pinEdges(_ view: UIView) {
        let topParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.topAnchor.constraint(equalTo: topParent.topAnchor).isActive = true
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        let bottomParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
}
