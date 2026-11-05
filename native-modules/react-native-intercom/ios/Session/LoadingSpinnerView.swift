import UIKit

@objc(_TtC18IntercomSDKPrivate18LoadingSpinnerView)
class LoadingSpinnerView: UIView {
    let spinnerImageView: UIImageView

    override init(frame: CGRect) {
        spinnerImageView = UIImageView(image: ICMAssets.spinnerImage())
        super.init(frame: frame)
        backgroundColor = .clear
        isHidden = true
        isUserInteractionEnabled = false
        addSubview(spinnerImageView)
        translatesAutoresizingMaskIntoConstraints = false
        widthAnchor.constraint(equalToConstant: 28).isActive = true
        heightAnchor.constraint(equalToConstant: 28).isActive = true
    }

    required init?(coder: NSCoder) {
        spinnerImageView = UIImageView(image: ICMAssets.spinnerImage())
        fatalError("init(coder:) has not been implemented")
    }

    @objc func startSpinning() {
        isHidden = false
        let animation = CABasicAnimation(keyPath: "transform.rotation")
        animation.isCumulative = true
        animation.duration = 0.4
        animation.repeatCount = .infinity
        animation.fromValue = NSNumber(value: 0)
        animation.toValue = NSNumber(value: Double.pi)
        spinnerImageView.layer.add(animation, forKey: "spin")
    }

    @objc func stopSpinning() {
        spinnerImageView.layer.removeAnimation(forKey: "spin")
        isHidden = true
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        spinnerImageView.center = CGPoint(x: bounds.midX, y: bounds.midY)
    }
}
