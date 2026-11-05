// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import UIKit
import QuartzCore

@objc(_TtC18IntercomSDKPrivate14ShimmeringView)
class ShimmeringView: UIView {
    @objc var shimmeringSpeed: CGFloat = 230
    @objc var shimmeringHighlightLength: CGFloat = 1
    @objc var shimmeringBeginFadeDuration: Double = 0.1
    @objc var shimmeringEndFadeDuration: Double = 0.3
    @objc var shimmeringPauseDuration: Double = 0.4
    @objc var shimmeringOpacity: CGFloat = 1 { didSet { updateMaskColors() } }
    @objc var shimmeringAnimationOpacity: CGFloat = 0.5 { didSet { updateMaskColors() } }
    @objc var contentView: UIView? {
        didSet {
            if contentView === oldValue { return }
            clearMask(from: oldValue)
            oldValue?.removeFromSuperview()
            if let contentView { addSubview(contentView) }
            setNeedsLayout()
            createMaskIfNeeded()
            if isShimmering { startShimmering() }
            else if maskLayer != nil { layoutIfNeeded(); stopShimmering() }
        }
    }
    @objc var isShimmering = false {
        didSet {
            if isShimmering == oldValue { return }
            createMaskIfNeeded()
            if isShimmering { startShimmering() }
            else if maskLayer != nil { layoutIfNeeded(); stopShimmering() }
        }
    }
    private var maskLayer: ShimmerMaskLayer?
    private var laidOutSize = CGSize.zero
    private lazy var endFadeDelegate = EndFadeDelegate(owner: self)
    private static let midLocation = NSNumber(value: 0.5)
    override init(frame: CGRect) { super.init(frame: frame) }
    required init?(coder: NSCoder) { super.init(coder: coder) }

    private func updateMaskColors() {
        guard let maskLayer else { return }
        let base = UIColor(white: 1, alpha: shimmeringOpacity).cgColor
        let highlight = UIColor(white: 1, alpha: shimmeringAnimationOpacity).cgColor
        maskLayer.colors = [base, highlight, base]
    }
    private func clearMask(from view: UIView?) {
        guard maskLayer != nil else { return }
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        (view ?? contentView)?.layer.mask = nil
        maskLayer = nil
        laidOutSize = .zero
        CATransaction.commit()
    }
    private func createMaskIfNeeded() {
        guard isShimmering, maskLayer == nil, let contentView else { return }
        let mask = ShimmerMaskLayer()
        contentView.layer.mask = mask
        maskLayer = mask
        laidOutSize = .zero
        updateMaskColors()
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        contentView?.frame = bounds
        guard let maskLayer, let contentLayer = contentView?.layer, contentLayer.bounds.width > 0 else { return }
        layoutMask()
        if isShimmering {
            let size = contentLayer.bounds.size
            if maskLayer.animation(forKey: "shimmer.slide") == nil || size != laidOutSize {
                laidOutSize = size
                addSlideAnimation()
            }
        }
    }
    private func layoutMask() {
        guard let maskLayer, let contentLayer = contentView?.layer else { return }
        let width = contentLayer.bounds.width
        guard width > 0 else { return }
        let pauseDistance = shimmeringSpeed * shimmeringPauseDuration
        let travel = width + pauseDistance
        let fullWidth = 3 * width + travel
        let endDistance = (width + width) + travel
        let edge = (1 - shimmeringHighlightLength) * 0.5
        let start = (width + travel) / fullWidth
        let end = endDistance / fullWidth
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        maskLayer.anchorPoint = .zero
        maskLayer.locations = [NSNumber(value: Double(edge)), ShimmeringView.midLocation, NSNumber(value: Double(1 - edge))]
        maskLayer.startPoint = CGPoint(x: start, y: 0)
        maskLayer.endPoint = CGPoint(x: end, y: 0)
        maskLayer.position = CGPoint(x: -endDistance, y: 0)
        maskLayer.bounds = CGRect(x: 0, y: 0, width: fullWidth, height: contentLayer.bounds.height)
        CATransaction.commit()
    }
    private func addSlideAnimation() {
        guard let maskLayer, let contentLayer = contentView?.layer else { return }
        let width = contentLayer.bounds.width
        guard width > 0, shimmeringSpeed > 0 else { return }
        let duration = width / shimmeringSpeed + shimmeringPauseDuration
        if let existing = maskLayer.animation(forKey: "shimmer.slide")?.copy() as? CABasicAnimation {
            existing.repeatCount = Float.greatestFiniteMagnitude
            existing.duration = duration
            maskLayer.add(existing, forKey: "shimmer.slide")
        } else {
            let animation = CABasicAnimation(keyPath: "position")
            animation.toValue = NSValue(cgPoint: .zero)
            animation.duration = duration
            animation.repeatCount = Float.greatestFiniteMagnitude
            animation.fillMode = .forwards
            animation.isRemovedOnCompletion = false
            animation.beginTime = CACurrentMediaTime() + shimmeringBeginFadeDuration
            maskLayer.add(animation, forKey: "shimmer.slide")
        }
    }
    private func fadeAnimation(on layer: CALayer, to opacity: Float, duration: Double) -> CABasicAnimation {
        let animation = CABasicAnimation(keyPath: "opacity")
        animation.fromValue = (layer.presentation() ?? layer).opacity
        animation.toValue = opacity
        animation.fillMode = .both
        animation.isRemovedOnCompletion = false
        animation.duration = duration
        return animation
    }
    private func startShimmering() {
        guard let maskLayer else { return }
        let fade = maskLayer.fadeLayer
        if shimmeringBeginFadeDuration > 0 && !CATransaction.disableActions() {
            fade.add(fadeAnimation(on: fade, to: 0, duration: shimmeringBeginFadeDuration), forKey: "shimmer.fade")
        } else {
            CATransaction.begin()
            CATransaction.setDisableActions(true)
            fade.opacity = 0
            fade.removeAnimation(forKey: "shimmer.fade")
            CATransaction.commit()
        }
        laidOutSize = .zero
        setNeedsLayout()
    }
    private func stopShimmering() {
        guard let maskLayer else { return }
        if CATransaction.disableActions() { clearMask(from: nil); return }
        var fadeStart: CFTimeInterval = 0
        if let slide = maskLayer.animation(forKey: "shimmer.slide") {
            let now = CACurrentMediaTime()
            let begin = slide.beginTime
            let elapsed = slide.duration > 0 ? fmod(now - begin, slide.duration) : 0
            if let finishing = slide.copy() as? CAAnimation {
                finishing.repeatCount = 0
                finishing.beginTime = now - elapsed
                fadeStart = finishing.beginTime + slide.duration
                maskLayer.add(finishing, forKey: "shimmer.slide")
            }
        }
        let animation = fadeAnimation(on: maskLayer.fadeLayer, to: 1, duration: shimmeringEndFadeDuration)
        animation.beginTime = fadeStart
        animation.delegate = endFadeDelegate
        maskLayer.fadeLayer.add(animation, forKey: "shimmer.fade")
    }
    fileprivate func endFadeDidStop(finished: Bool) {
        if finished && !isShimmering && maskLayer != nil { clearMask(from: nil) }
    }
}

@objc(_TtC18IntercomSDKPrivateP33_54135CACCBC0BDAAC37404BB23A0601E16ShimmerMaskLayer)
private class ShimmerMaskLayer: CAGradientLayer {
    let fadeLayer: CALayer
    override init() {
        fadeLayer = CALayer()
        super.init()
        fadeLayer.backgroundColor = UIColor.white.cgColor
        addSublayer(fadeLayer)
    }
    override init(layer: Any) {
        if let original = layer as? Self { fadeLayer = original.fadeLayer }
        else { fadeLayer = CALayer() }
        super.init(layer: layer)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override func layoutSublayers() { super.layoutSublayers(); fadeLayer.frame = bounds }
}

@objc(_TtC18IntercomSDKPrivateP33_54135CACCBC0BDAAC37404BB23A0601E15EndFadeDelegate)
private class EndFadeDelegate: NSObject, CAAnimationDelegate {
    private weak var owner: ShimmeringView?
    init(owner: ShimmeringView) { self.owner = owner; super.init() }
    override init() { fatalError("init() has not been implemented") }
    func animationDidStop(_ anim: CAAnimation, finished flag: Bool) { owner?.endFadeDidStop(finished: flag) }
}
