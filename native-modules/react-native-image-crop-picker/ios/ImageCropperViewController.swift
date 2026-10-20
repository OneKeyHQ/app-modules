import UIKit

protocol ImageCropperViewControllerDelegate: AnyObject {
  // `cropFrame` is in the coordinate space of `image`, rotated by `angle`.
  func imageCropperViewController(
    _ controller: ImageCropperViewController,
    didCropWithFrame cropFrame: CGRect,
    angle: Int
  )
  func imageCropperViewControllerDidCancel(_ controller: ImageCropperViewController)
}

// Full-screen cropper laid out like a OneKey page: a header with the title and
// a rotate button, the crop area, and a Cancel / Confirm footer. Android's
// ImageCropperActivity draws the same screen with the same metrics.
final class ImageCropperViewController: UIViewController {
  // The area outside the crop box shows the page background at this opacity.
  static let dimmedAlpha: CGFloat = 0.7

  weak var delegate: ImageCropperViewControllerDelegate?
  let image: UIImage

  private let config: ImageCropPickerConfig
  private let theme: ImageCropperTheme
  private let cropView: TOCropView
  private let titleLabel = UILabel()
  private let rotateButton: ImageCropperIconButton
  private let cancelButton: ImageCropperButton
  private let confirmButton: ImageCropperButton
  private var didPerformInitialSetup = false
  private var isRotating = false

  // Set while the session crops and saves the image.
  var isProcessing = false {
    didSet { updateControls() }
  }

  init(image: UIImage, config: ImageCropPickerConfig, theme: ImageCropperTheme) {
    self.image = image
    self.config = config
    self.theme = theme
    let cropView = TOCropView(
      croppingStyle: config.cropperCircleOverlay ? .circular : .default,
      image: image
    )
    self.cropView = cropView

    // TOCropViewController's own translations, for callers that pass no text.
    let strings = TO_CROP_VIEW_RESOURCE_BUNDLE_FOR_OBJECT(cropView) ?? .main
    func localized(_ key: String) -> String {
      return strings.localizedString(forKey: key, value: key, table: "TOCropViewControllerLocalizable")
    }
    // Large Button: 12pt + 1pt border vertically, 20pt + 1pt horizontally.
    let buttonPadding = theme.metric(20) + 1
    let spinnerSpacing = theme.metric(8)
    cancelButton = ImageCropperButton(
      title: Self.nonEmpty(config.cropperCancelText) ?? localized("Cancel"),
      horizontalPadding: buttonPadding,
      spinnerSpacing: spinnerSpacing,
      font: theme.buttonFont,
      textColor: theme.cancelButtonTextColor,
      backgroundColor: theme.cancelButtonColor,
      pressedBackgroundColor: theme.cancelButtonPressedColor
    )
    confirmButton = ImageCropperButton(
      title: Self.nonEmpty(config.cropperChooseText) ?? localized("Done"),
      horizontalPadding: buttonPadding,
      spinnerSpacing: spinnerSpacing,
      font: theme.buttonFont,
      textColor: theme.confirmButtonTextColor,
      backgroundColor: theme.confirmButtonColor,
      pressedBackgroundColor: theme.confirmButtonPressedColor
    )
    rotateButton = ImageCropperIconButton(
      image: ImageCropperIcons.rotateCounterclockwise(size: theme.metric(24)),
      tintColor: theme.iconColor,
      pressedBackgroundColor: theme.cancelButtonColor
    )

    super.init(nibName: nil, bundle: nil)
    modalPresentationStyle = .fullScreen
    modalTransitionStyle = .coverVertical
    overrideUserInterfaceStyle = theme.isDark ? .dark : .light
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return theme.isDark ? .lightContent : .darkContent
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = theme.backgroundColor

    configureCropView()
    view.addSubview(cropView)

    titleLabel.text = config.cropperToolbarTitle
    titleLabel.font = theme.titleFont
    titleLabel.textColor = theme.titleColor
    titleLabel.textAlignment = .center
    titleLabel.lineBreakMode = .byTruncatingTail
    titleLabel.accessibilityTraits = .header
    view.addSubview(titleLabel)

    rotateButton.isHidden = config.cropperRotateButtonsHidden
    rotateButton.accessibilityLabel = "Rotate"
    rotateButton.addTarget(self, action: #selector(rotateTapped), for: .touchUpInside)
    view.addSubview(rotateButton)

    cancelButton.addTarget(self, action: #selector(cancelTapped), for: .touchUpInside)
    confirmButton.addTarget(self, action: #selector(confirmTapped), for: .touchUpInside)
    view.addSubview(cancelButton)
    view.addSubview(confirmButton)
  }

  private func configureCropView() {
    cropView.backgroundColor = theme.backgroundColor
    cropView.overlayView.backgroundColor = theme.backgroundColor.withAlphaComponent(Self.dimmedAlpha)
    // The built-in dark blur ignores the theme, so rely on the dimmed overlay alone.
    cropView.translucencyAlwaysHidden = true
    cropView.cropViewPadding = theme.metric(20)

    let isCircular = config.cropperCircleOverlay
    let isFreeStyle = config.freeStyleCropEnabled && !isCircular
    if !isCircular, let targetSize = config.targetSize {
      // Applied by performInitialSetup, which sizes the first crop box from it.
      cropView.aspectRatio = targetSize
    }
    cropView.aspectRatioLockEnabled = !isFreeStyle
    cropView.resetAspectRatioEnabled = isFreeStyle
    cropView.cropBoxResizeEnabled = isFreeStyle

    if let overlay = cropView.gridOverlayView {
      overlay.frameColor = theme.titleColor
      overlay.gridColor = UIColor.white.withAlphaComponent(0.8)
      overlay.cornerHandlesHidden = !isFreeStyle
      if !config.showCropGuidelines {
        overlay.displayHorizontalGridLines = false
        overlay.displayVerticalGridLines = false
      }
    }
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    let cropFrame = layoutChrome(in: view.bounds.size)
    guard cropFrame.width > 0, cropFrame.height > 0 else {
      return
    }
    cropView.frame = cropFrame
    cropView.moveCroppedContentToCenter(animated: false)
    if !didPerformInitialSetup {
      didPerformInitialSetup = true
      cropView.performInitialSetup()
    }
  }

  override func viewWillTransition(
    to size: CGSize,
    with coordinator: UIViewControllerTransitionCoordinator
  ) {
    super.viewWillTransition(to: size, with: coordinator)
    guard didPerformInitialSetup, size != view.bounds.size else {
      return
    }
    // Same sequence as TOCropViewController, so the crop box keeps its content.
    cropView.prepareforRotation()
    cropView.simpleRenderMode = true
    cropView.internalLayoutDisabled = true
    coordinator.animate(alongsideTransition: { [self] _ in
      cropView.frame = layoutChrome(in: size)
      cropView.performRelayoutForRotation()
    }, completion: { [self] _ in
      cropView.setSimpleRenderMode(false, animated: true)
      cropView.internalLayoutDisabled = false
    })
  }

  // Lays out the header and footer, and returns the frame left for the crop view.
  @discardableResult
  private func layoutChrome(in size: CGSize) -> CGRect {
    let insets = view.safeAreaInsets
    let spacing = theme.metric(20)
    let headerHeight = theme.metric(56)
    let iconButtonSize = theme.metric(40)
    let buttonHeight = theme.metric(50)
    let buttonGap = theme.metric(10)
    // Same as OneKey's page footer: 20pt of padding, plus the home indicator
    // inset less 10pt.
    let bottomInset = insets.bottom > 10 ? insets.bottom - 10 : insets.bottom

    let contentMinX = insets.left
    let contentWidth = max(size.width - insets.left - insets.right, 0)

    let headerMidY = insets.top + headerHeight / 2
    rotateButton.frame = CGRect(
      x: contentMinX + contentWidth - spacing + theme.metric(8) - iconButtonSize,
      y: headerMidY - iconButtonSize / 2,
      width: iconButtonSize,
      height: iconButtonSize
    )
    // Centered, clear of the rotate button on both sides.
    let titleInset = spacing + iconButtonSize
    titleLabel.frame = CGRect(
      x: contentMinX + titleInset,
      y: insets.top,
      width: max(contentWidth - titleInset * 2, 0),
      height: headerHeight
    )

    let buttonY = size.height - bottomInset - spacing - buttonHeight
    let buttonWidth = max((contentWidth - spacing * 2 - buttonGap) / 2, 0)
    cancelButton.frame = CGRect(
      x: contentMinX + spacing,
      y: buttonY,
      width: buttonWidth,
      height: buttonHeight
    )
    confirmButton.frame = CGRect(
      x: cancelButton.frame.maxX + buttonGap,
      y: buttonY,
      width: buttonWidth,
      height: buttonHeight
    )

    // The crop view pads the crop box by `spacing` on every side.
    let cropMinY = insets.top + headerHeight
    return CGRect(
      x: contentMinX,
      y: cropMinY,
      width: contentWidth,
      height: max(buttonY - cropMinY, 0)
    )
  }

  private func updateControls() {
    cropView.isUserInteractionEnabled = !isProcessing
    rotateButton.isEnabled = !isProcessing
    confirmButton.isLoading = isProcessing
  }

  @objc private func rotateTapped() {
    guard !isRotating, !isProcessing else {
      return
    }
    isRotating = true
    cropView.rotateImageNinetyDegrees(animated: true, clockwise: false) { [weak self] _ in
      self?.isRotating = false
    }
  }

  @objc private func cancelTapped() {
    delegate?.imageCropperViewControllerDidCancel(self)
  }

  @objc private func confirmTapped() {
    guard !isProcessing, !isRotating else {
      return
    }
    delegate?.imageCropperViewController(
      self,
      didCropWithFrame: cropView.imageCropFrame,
      angle: cropView.angle
    )
  }

  private static func nonEmpty(_ value: String?) -> String? {
    guard let value, !value.isEmpty else {
      return nil
    }
    return value
  }
}

// OneKey's large Button: a capsule with a 16pt medium label, a pressed color,
// and a spinner next to the label while loading.
final class ImageCropperButton: UIControl {
  private let label = UILabel()
  private let spinner = UIActivityIndicatorView(style: .medium)
  private let horizontalPadding: CGFloat
  private let spinnerSpacing: CGFloat
  private let normalBackgroundColor: UIColor
  private let pressedBackgroundColor: UIColor

  var isLoading = false {
    didSet {
      guard isLoading != oldValue else {
        return
      }
      if isLoading {
        spinner.startAnimating()
      } else {
        spinner.stopAnimating()
      }
      updateAppearance()
      setNeedsLayout()
    }
  }

  override var isHighlighted: Bool {
    didSet { updateAppearance() }
  }

  override var isEnabled: Bool {
    didSet { updateAppearance() }
  }

  init(
    title: String,
    horizontalPadding: CGFloat,
    spinnerSpacing: CGFloat,
    font: UIFont,
    textColor: UIColor,
    backgroundColor: UIColor,
    pressedBackgroundColor: UIColor
  ) {
    self.horizontalPadding = horizontalPadding
    self.spinnerSpacing = spinnerSpacing
    normalBackgroundColor = backgroundColor
    self.pressedBackgroundColor = pressedBackgroundColor
    super.init(frame: .zero)

    layer.cornerCurve = .continuous
    label.text = title
    label.font = font
    label.textColor = textColor
    label.textAlignment = .center
    label.lineBreakMode = .byTruncatingTail
    label.isUserInteractionEnabled = false
    addSubview(label)

    spinner.color = textColor
    spinner.hidesWhenStopped = true
    spinner.isUserInteractionEnabled = false
    addSubview(spinner)

    isAccessibilityElement = true
    accessibilityTraits = .button
    accessibilityLabel = title
    updateAppearance()
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    layer.cornerRadius = bounds.height / 2

    let spinnerSize = spinner.intrinsicContentSize
    let leadingWidth = isLoading ? spinnerSize.width + spinnerSpacing : 0
    let maxLabelWidth = max(bounds.width - horizontalPadding * 2 - leadingWidth, 0)
    let labelWidth = min(ceil(label.intrinsicContentSize.width), maxLabelWidth)
    let startX = (bounds.width - labelWidth - leadingWidth) / 2

    spinner.frame = CGRect(
      x: startX,
      y: (bounds.height - spinnerSize.height) / 2,
      width: spinnerSize.width,
      height: spinnerSize.height
    )
    label.frame = CGRect(
      x: startX + leadingWidth,
      y: 0,
      width: labelWidth,
      height: bounds.height
    )
  }

  private func updateAppearance() {
    backgroundColor = isHighlighted ? pressedBackgroundColor : normalBackgroundColor
    // OneKey dims disabled and loading buttons alike.
    alpha = isEnabled && !isLoading ? 1 : 0.4
    accessibilityTraits = isEnabled ? .button : [.button, .notEnabled]
  }
}

// OneKey's header icon button: a 24pt icon with a round pressed background.
final class ImageCropperIconButton: UIControl {
  private let imageView = UIImageView()
  private let pressedBackgroundColor: UIColor

  override var isHighlighted: Bool {
    didSet { updateAppearance() }
  }

  override var isEnabled: Bool {
    didSet { updateAppearance() }
  }

  init(image: UIImage, tintColor: UIColor, pressedBackgroundColor: UIColor) {
    self.pressedBackgroundColor = pressedBackgroundColor
    super.init(frame: .zero)
    imageView.image = image
    imageView.tintColor = tintColor
    imageView.contentMode = .center
    imageView.isUserInteractionEnabled = false
    addSubview(imageView)
    isAccessibilityElement = true
    accessibilityTraits = .button
    updateAppearance()
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    layer.cornerRadius = bounds.height / 2
    imageView.frame = bounds
  }

  private func updateAppearance() {
    backgroundColor = isHighlighted ? pressedBackgroundColor : .clear
    imageView.alpha = isEnabled ? 1 : 0.4
  }
}

enum ImageCropperIcons {
  // OneKey's RotateCounterclockwise icon (24x24 viewBox). Android uses the
  // same path in res/drawable/image_crop_picker_rotate.xml.
  static func rotateCounterclockwise(size: CGFloat) -> UIImage {
    let path = UIBezierPath()
    path.move(to: CGPoint(x: 6, y: 5.426))
    path.addCurve(to: CGPoint(x: 12.028, y: 3), controlPoint1: CGPoint(x: 7.628, y: 3.919), controlPoint2: CGPoint(x: 9.484, y: 3))
    path.addCurve(to: CGPoint(x: 20.969, y: 10.984), controlPoint1: CGPoint(x: 16.605, y: 3.001), controlPoint2: CGPoint(x: 20.452, y: 6.436))
    path.addCurve(to: CGPoint(x: 14.047, y: 20.77), controlPoint1: CGPoint(x: 21.485, y: 15.531), controlPoint2: CGPoint(x: 18.507, y: 19.742))
    path.addCurve(to: CGPoint(x: 3.541, y: 15), controlPoint1: CGPoint(x: 9.588, y: 21.798), controlPoint2: CGPoint(x: 5.067, y: 19.315))
    path.addLine(to: CGPoint(x: 3.207, y: 14.057))
    path.addLine(to: CGPoint(x: 5.093, y: 13.391))
    path.addLine(to: CGPoint(x: 5.426, y: 14.333))
    path.addCurve(to: CGPoint(x: 13.597, y: 18.821), controlPoint1: CGPoint(x: 6.612, y: 17.689), controlPoint2: CGPoint(x: 10.128, y: 19.62))
    path.addCurve(to: CGPoint(x: 18.981, y: 11.211), controlPoint1: CGPoint(x: 17.066, y: 18.023), controlPoint2: CGPoint(x: 19.382, y: 14.748))
    path.addCurve(to: CGPoint(x: 12.029, y: 5), controlPoint1: CGPoint(x: 18.58, y: 7.674), controlPoint2: CGPoint(x: 15.588, y: 5.002))
    path.addCurve(to: CGPoint(x: 7.244, y: 7), controlPoint1: CGPoint(x: 10.047, y: 5), controlPoint2: CGPoint(x: 8.622, y: 5.686))
    path.addLine(to: CGPoint(x: 10, y: 7))
    path.addLine(to: CGPoint(x: 10, y: 9))
    path.addLine(to: CGPoint(x: 4, y: 9))
    path.addLine(to: CGPoint(x: 4, y: 3))
    path.addLine(to: CGPoint(x: 6, y: 3))
    path.close()
    path.apply(CGAffineTransform(scaleX: size / 24, y: size / 24))

    let format = UIGraphicsImageRendererFormat.preferred()
    format.opaque = false
    let image = UIGraphicsImageRenderer(size: CGSize(width: size, height: size), format: format)
      .image { _ in
        UIColor.black.setFill()
        path.fill()
      }
    return image.withRenderingMode(.alwaysTemplate)
  }
}
