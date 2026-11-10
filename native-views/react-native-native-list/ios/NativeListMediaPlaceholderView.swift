import UIKit

/// Loading sheen and failed hatch drawn over a media tile while it has no picture.
/// Geometry mirrors the Web renderer (`MediaTileRowRenderer.ts`) and Android
/// (`NativeListMediaPlaceholderView.kt`) so every platform draws the same placeholder.
final class NativeListMediaPlaceholderView: UIView {
  enum State { case hidden, loading, failed }

  private static let sweepPeriod: CFTimeInterval = 1.6
  private static let sweepKey = "nativeListMediaSweep"

  private let sheen = CAGradientLayer()
  private let hatch = CAShapeLayer()
  private let hatchMask = CAGradientLayer()
  private let picture = CAShapeLayer()
  private let pictureCut = CAShapeLayer()
  private let slash = CAShapeLayer()
  private(set) var state: State = .hidden
  private var laidOutSize: CGSize = .zero
  private var loadingFill: UIColor = .clear

  override init(frame: CGRect) {
    super.init(frame: frame)
    isUserInteractionEnabled = false
    isHidden = true
    clipsToBounds = true
    layer.borderWidth = 1 / UIScreen.main.scale
    sheen.startPoint = CGPoint(x: 0, y: 0.5)
    sheen.endPoint = CGPoint(x: 1, y: 0.5)
    sheen.locations = [0, 0.5, 1]
    hatch.lineWidth = 1
    hatch.fillColor = nil
    hatchMask.type = .radial
    hatchMask.startPoint = CGPoint(x: 0.5, y: 0.5)
    // CSS `radial-gradient(circle ...)` defaults to the farthest corner.
    hatchMask.endPoint = CGPoint(x: 0.5 + 0.7071, y: 0.5 + 0.7071)
    hatchMask.colors = [UIColor.clear.cgColor, UIColor.clear.cgColor, UIColor.black.cgColor, UIColor.black.cgColor]
    hatchMask.locations = [0, 0.22, 0.7, 1]
    hatch.mask = hatchMask
    for shape in [picture, slash] {
      shape.fillColor = nil
      shape.lineCap = .round
      shape.lineJoin = .round
    }
    pictureCut.fillRule = .evenOdd
    pictureCut.fillColor = UIColor.black.cgColor
    [hatch, sheen, picture, slash].forEach(layer.addSublayer)
    NotificationCenter.default.addObserver(
      self, selector: #selector(restartSweep),
      name: UIApplication.willEnterForegroundNotification, object: nil)
  }
  required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

  func applyTheme(_ theme: [String: Any]?) {
    loadingFill = nativeListColor(theme, "strongBackground", "#0000000F")
    if state == .loading { backgroundColor = loadingFill }
    let glyph = nativeListColor(theme, "disabledText", "#8C8C8C")
    let glyphColor = glyph.withAlphaComponent(glyph.cgAlpha * 0.7).cgColor
    picture.strokeColor = glyphColor
    slash.strokeColor = glyphColor
    let separator = nativeListColor(theme, "separator", "#E5E7EB")
    layer.borderColor = separator.withAlphaComponent(separator.cgAlpha * 0.6).cgColor
    hatch.strokeColor = nativeListColor(theme, "primaryText", "#202020").withAlphaComponent(0.045).cgColor
    let dark = nativeListIsDark(nativeListColor(theme, "rowBackground", "#FFFFFF"))
    let shine = UIColor.white.withAlphaComponent(dark ? 0.07 : 0.75)
    sheen.colors = [shine.withAlphaComponent(0).cgColor, shine.cgColor, shine.withAlphaComponent(0).cgColor]
  }

  func setState(_ next: State) {
    guard next != state else { return }
    state = next
    isHidden = next == .hidden
    // The failed state sits on the cell's own fill; while loading nothing else paints the slot.
    backgroundColor = next == .loading ? loadingFill : .clear
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    sheen.isHidden = next != .loading
    hatch.isHidden = next != .failed
    slash.isHidden = next != .failed
    picture.mask = next == .failed ? pictureCut : nil
    CATransaction.commit()
    restartSweep()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    guard bounds.size != laidOutSize else { return }
    laidOutSize = bounds.size
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    layoutHatch()
    layoutGlyph()
    let width = bounds.width, height = bounds.height
    sheen.bounds = CGRect(x: 0, y: 0, width: width * 0.6, height: height * 2)
    sheen.position = CGPoint(x: width * 0.6, y: height / 2)
    // CSS skewX(-12deg).
    sheen.setAffineTransform(CGAffineTransform(a: 1, b: 0, c: -tan(12 * .pi / 180), d: 1, tx: 0, ty: 0))
    CATransaction.commit()
    restartSweep()
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    restartSweep()
  }

  @objc private func restartSweep() {
    sheen.removeAnimation(forKey: Self.sweepKey)
    guard state == .loading, window != nil, bounds.width > 0 else { return }
    let width = bounds.width
    guard !UIAccessibility.isReduceMotionEnabled else {
      sheen.opacity = 0.6
      return
    }
    sheen.opacity = 1
    let sweep = CAKeyframeAnimation(keyPath: "position.x")
    // Band left edge travels from -80% to 130% of the tile, then rests for 30% of the period.
    sweep.values = [width * -0.5, width * 1.6, width * 1.6]
    sweep.keyTimes = [0, 0.7, 1]
    sweep.timingFunctions = [
      CAMediaTimingFunction(controlPoints: 0.4, 0, 0.2, 1), CAMediaTimingFunction(name: .linear),
    ]
    sweep.duration = Self.sweepPeriod
    sweep.repeatCount = .infinity
    sweep.isRemovedOnCompletion = false
    // Every tile derives its phase from the shared media clock so the grid sweeps in step,
    // including cells that are recycled mid-period.
    sweep.timeOffset = CACurrentMediaTime().truncatingRemainder(dividingBy: Self.sweepPeriod)
    sheen.add(sweep, forKey: Self.sweepKey)
  }

  private func layoutHatch() {
    hatch.frame = bounds
    hatchMask.frame = bounds
    let path = UIBezierPath()
    // Lines along x + y = c, 7pt apart measured perpendicular to the stripes.
    let step = 7 * CGFloat(2).squareRoot()
    var c: CGFloat = 0
    while c <= bounds.width + bounds.height {
      path.move(to: CGPoint(x: c, y: 0))
      path.addLine(to: CGPoint(x: 0, y: c))
      c += step
    }
    hatch.path = path.cgPath
  }

  private func layoutGlyph() {
    let size = min(bounds.width * 0.28, 44)
    let scale = size / 24
    let origin = CGPoint(x: (bounds.width - size) / 2, y: (bounds.height - size) / 2)
    var transform = CGAffineTransform(translationX: origin.x, y: origin.y).scaledBy(x: scale, y: scale)
    let frame = UIBezierPath(roundedRect: CGRect(x: 3, y: 3, width: 18, height: 18), cornerRadius: 4)
    frame.append(UIBezierPath(ovalIn: CGRect(x: 7.4, y: 7.4, width: 3.2, height: 3.2)))
    let ridge = UIBezierPath()
    ridge.move(to: CGPoint(x: 5, y: 20.5))
    ridge.addLine(to: CGPoint(x: 14.5, y: 11))
    ridge.addLine(to: CGPoint(x: 21, y: 17.5))
    frame.append(ridge)
    picture.frame = bounds
    picture.lineWidth = 1.5 * scale
    picture.path = frame.cgPath.copy(using: &transform)
    let line = UIBezierPath()
    line.move(to: CGPoint(x: 3, y: 3))
    line.addLine(to: CGPoint(x: 21, y: 21))
    slash.frame = bounds
    slash.lineWidth = 1.5 * scale
    slash.path = line.cgPath.copy(using: &transform)
    let band = UIBezierPath()
    band.move(to: CGPoint(x: 2.6, y: -0.6))
    band.addLine(to: CGPoint(x: 24.6, y: 21.4))
    band.addLine(to: CGPoint(x: 21.4, y: 24.6))
    band.addLine(to: CGPoint(x: -0.6, y: 2.6))
    band.close()
    let cut = UIBezierPath(rect: bounds)
    if let bandPath = band.cgPath.copy(using: &transform) { cut.append(UIBezierPath(cgPath: bandPath)) }
    pictureCut.frame = bounds
    pictureCut.path = cut.cgPath
  }
}

private extension UIColor {
  var cgAlpha: CGFloat { cgColor.alpha }
}

func nativeListIsDark(_ color: UIColor) -> Bool {
  var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0, alpha: CGFloat = 0
  guard color.getRed(&red, green: &green, blue: &blue, alpha: &alpha) else { return false }
  return red * 299 + green * 587 + blue * 114 < 500
}
