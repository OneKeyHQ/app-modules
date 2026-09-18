import NitroModules
import PhotosUI
import ReactNativeNativeLogger
import UIKit
import UniformTypeIdentifiers

// One picker or cropper flow. It owns the JS promise until the presented UI
// reports back, and settles it exactly once.
final class ImageCropPickerSession: NSObject {
  enum Mode {
    case picker
    case cropper(path: String)
  }

  private let mode: Mode
  private let config: ImageCropPickerConfig
  private let promise: Promise<PickedImage>
  private let onFinish: (ImageCropPickerSession) -> Void

  // PHPickerViewController connects to its out-of-process service before it
  // appears, so a requested presentation can stay pending for a moment.
  private static let pendingPresentationTimeout: TimeInterval = 10

  // Main thread state.
  private var pickerController: PHPickerViewController?
  private var cropController: ImageCropperViewController?
  private var loadingView: UIView?
  private var pickedFilename: String?
  private var pickedAssetIdentifier: String?
  private var sourceScale: CGFloat = 1
  private var presentationRequestedAt: Date?
  private var hasPresented = false
  private var isBusy = false
  private var isFinished = false

  init(
    mode: Mode,
    config: ImageCropPickerConfig,
    promise: Promise<PickedImage>,
    onFinish: @escaping (ImageCropPickerSession) -> Void
  ) {
    self.mode = mode
    self.config = config
    self.promise = promise
    self.onFinish = onFinish
    super.init()
  }

  // Whether the session still has UI on screen or work in flight.
  var isActive: Bool {
    guard !isFinished else {
      return false
    }
    if isBusy {
      return true
    }
    guard let rootController = rootController else {
      return false
    }
    if rootController.presentingViewController != nil || rootController.isBeingPresented {
      return true
    }
    if !hasPresented, let presentationRequestedAt {
      return Date().timeIntervalSince(presentationRequestedAt) < Self.pendingPresentationTimeout
    }
    return false
  }

  func start() {
    switch mode {
    case .picker:
      presentPicker()
    case .cropper(let path):
      loadCropperImage(path: path)
    }
  }

  // Settles a session whose UI disappeared without calling back.
  func abandon() {
    finish(.failure(ImageCropPickerError.cancelled))
  }

  private var rootController: UIViewController? {
    return pickerController ?? cropController
  }

  private var isPickerMode: Bool {
    if case .picker = mode {
      return true
    }
    return false
  }

  // MARK: - Picker

  private func presentPicker() {
    guard let presenter = Self.topViewController() else {
      finish(.failure(ImageCropPickerError.noViewController))
      return
    }

    // PHPickerViewController runs out of process and needs no photo library
    // permission, so a previously denied permission can't block the picker.
    // Passing the library only adds asset identifiers to the results; it
    // doesn't ask for access either.
    var configuration = PHPickerConfiguration(photoLibrary: .shared())
    configuration.filter = .images
    configuration.selectionLimit = 1
    configuration.preferredAssetRepresentationMode = .current

    let picker = PHPickerViewController(configuration: configuration)
    picker.delegate = self
    picker.modalPresentationStyle = .fullScreen
    switch config.appearance.colorScheme {
    case .dark:
      picker.overrideUserInterfaceStyle = .dark
    case .light:
      picker.overrideUserInterfaceStyle = .light
    case nil:
      break
    }
    pickerController = picker
    present(picker, from: presenter)
  }

  private func loadPickedImage(_ result: PHPickerResult, in picker: PHPickerViewController) {
    let provider = result.itemProvider
    guard let typeIdentifier = Self.imageTypeIdentifier(of: provider) else {
      dismissAll { self.finish(.failure(ImageCropPickerError.noImageData)) }
      return
    }

    isBusy = true
    let filename = provider.suggestedName
    pickedFilename = filename
    showLoading(in: picker.view)

    provider.loadFileRepresentation(forTypeIdentifier: typeIdentifier) { [self] url, _ in
      // The file is removed once this handler returns, so decode it right here.
      if let url, let decoded = ImageCropPickerImageProcessor.decodeImage(at: url) {
        handlePickedImage(decoded, filename: filename)
        return
      }
      provider.loadDataRepresentation(forTypeIdentifier: typeIdentifier) { [self] data, _ in
        handlePickedImage(
          data.flatMap { ImageCropPickerImageProcessor.decodeImage(data: $0) },
          filename: filename
        )
      }
    }
  }

  // Runs on the item provider's background queue.
  private func handlePickedImage(_ decoded: DecodedImage?, filename: String?) {
    guard let decoded else {
      DispatchQueue.main.async { [self] in
        isBusy = false
        hideLoading()
        dismissAll { self.finish(.failure(ImageCropPickerError.noImageData)) }
      }
      return
    }

    guard !config.cropping else {
      DispatchQueue.main.async { [self] in
        isBusy = false
        hideLoading()
        guard !isFinished, let picker = pickerController else {
          return
        }
        sourceScale = decoded.sourceScale
        presentCropper(image: decoded.image, from: picker)
      }
      return
    }

    let result = Result {
      try ImageCropPickerImageProcessor.makeResult(
        image: decoded.image,
        config: config,
        cropRect: nil,
        filename: filename
      )
    }
    DispatchQueue.main.async { [self] in
      isBusy = false
      hideLoading()
      dismissAll { self.finish(result) }
    }
  }

  // MARK: - Cropper

  private func loadCropperImage(path: String) {
    isBusy = true
    DispatchQueue.global(qos: .userInitiated).async { [self] in
      let loaded = Result { try ImageCropPickerImageProcessor.loadImage(fromPath: path) }
      DispatchQueue.main.async { [self] in
        isBusy = false
        switch loaded {
        case .success(let decoded):
          guard let presenter = Self.topViewController() else {
            finish(.failure(ImageCropPickerError.noViewController))
            return
          }
          sourceScale = decoded.sourceScale
          presentCropper(image: decoded.image, from: presenter)
        case .failure(let error):
          finish(.failure(error))
        }
      }
    }
  }

  private func presentCropper(image: UIImage, from presenter: UIViewController) {
    let theme = ImageCropperTheme(
      config.appearance,
      systemIsDark: presenter.traitCollection.userInterfaceStyle == .dark
    )
    let controller = ImageCropperViewController(image: image, config: config, theme: theme)
    controller.delegate = self
    cropController = controller
    present(controller, from: presenter)
  }

  // MARK: - Completion

  private func present(_ controller: UIViewController, from presenter: UIViewController) {
    // UIKit silently ignores a presentation from a controller that is off
    // screen or already presenting, and the promise would never settle.
    guard presenter.viewIfLoaded?.window != nil, presenter.presentedViewController == nil else {
      if controller === cropController {
        cropController = nil
      }
      dismissAll { self.finish(.failure(ImageCropPickerError.noViewController)) }
      return
    }
    presentationRequestedAt = Date()
    presenter.present(controller, animated: true) { [weak self] in
      self?.hasPresented = true
    }
  }

  private func dismissAll(completion: @escaping () -> Void) {
    guard let presenting = rootController?.presentingViewController else {
      completion()
      return
    }
    presenting.dismiss(animated: true, completion: completion)
  }

  private func finish(_ result: Result<PickedImage, Error>) {
    guard !isFinished else {
      return
    }
    isFinished = true
    hideLoading()
    pickerController = nil
    cropController = nil

    switch result {
    case .success(let image):
      promise.resolve(withResult: image)
    case .failure(let error):
      if let pickerError = error as? ImageCropPickerError,
         pickerError.code != .pickerCancelled {
        OneKeyLog.warn("ImageCropPicker", "\(pickerError)")
      }
      promise.reject(withError: error)
    }
    onFinish(self)
  }

  private func showLoading(in view: UIView) {
    hideLoading()
    let overlay = UIView(frame: view.bounds)
    overlay.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    overlay.backgroundColor = UIColor.black.withAlphaComponent(0.4)

    let indicator = UIActivityIndicatorView(style: .large)
    indicator.color = .white
    indicator.center = CGPoint(x: overlay.bounds.midX, y: overlay.bounds.midY)
    indicator.autoresizingMask = [
      .flexibleLeftMargin, .flexibleRightMargin, .flexibleTopMargin, .flexibleBottomMargin,
    ]
    indicator.startAnimating()
    overlay.addSubview(indicator)

    view.addSubview(overlay)
    loadingView = overlay
  }

  private func hideLoading() {
    loadingView?.removeFromSuperview()
    loadingView = nil
  }

  // MARK: - Helpers

  private static func imageTypeIdentifier(of provider: NSItemProvider) -> String? {
    let identifier = provider.registeredTypeIdentifiers.first { identifier in
      UTType(identifier)?.conforms(to: .image) ?? false
    }
    if let identifier {
      return identifier
    }
    return provider.hasItemConformingToTypeIdentifier(UTType.image.identifier)
      ? UTType.image.identifier
      : nil
  }

  static func topViewController() -> UIViewController? {
    let sceneWindows = UIApplication.shared.connectedScenes
      .compactMap { $0 as? UIWindowScene }
      .filter { $0.activationState != .unattached && $0.activationState != .background }
      .flatMap { $0.windows }
    let appDelegateWindow = UIApplication.shared.delegate?.window ?? nil
    let window = sceneWindows.first { $0.isKeyWindow && $0.windowLevel == .normal && $0.rootViewController != nil }
      ?? appDelegateWindow
      ?? sceneWindows.first { $0.rootViewController != nil }

    var controller = window?.rootViewController
    while let presented = controller?.presentedViewController, !presented.isBeingDismissed {
      controller = presented
    }
    return controller
  }
}

extension ImageCropPickerSession: PHPickerViewControllerDelegate {
  func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
    guard !isFinished, !isBusy, cropController == nil else {
      return
    }
    guard let result = results.first else {
      dismissAll { self.finish(.failure(ImageCropPickerError.cancelled)) }
      return
    }
    pickedAssetIdentifier = result.assetIdentifier
    loadPickedImage(result, in: picker)
  }
}

extension ImageCropPickerSession: ImageCropperViewControllerDelegate {
  func imageCropperViewController(
    _ controller: ImageCropperViewController,
    didCropWithFrame cropFrame: CGRect,
    angle: Int
  ) {
    guard !isFinished, !isBusy else {
      return
    }
    isBusy = true
    controller.isProcessing = true

    let image = controller.image
    let config = self.config
    let filename = pickedFilename
    // Report the crop rect in the coordinates of the original, undownsampled image.
    let sourceCropRect = CGRect(
      x: (cropFrame.origin.x * sourceScale).rounded(),
      y: (cropFrame.origin.y * sourceScale).rounded(),
      width: (cropFrame.width * sourceScale).rounded(),
      height: (cropFrame.height * sourceScale).rounded()
    )

    DispatchQueue.global(qos: .userInitiated).async { [self] in
      let result = Result {
        let isWholeImage = angle == 0 && cropFrame == CGRect(origin: .zero, size: image.size)
        let cropped = isWholeImage
          ? image
          : image.croppedImage(withFrame: cropFrame, angle: angle, circularClip: false)
        return try ImageCropPickerImageProcessor.makeResult(
          image: ImageCropPickerImageProcessor.resizeCroppedImage(cropped, config: config),
          config: config,
          cropRect: sourceCropRect,
          filename: filename
        )
      }
      DispatchQueue.main.async { [self] in
        isBusy = false
        dismissAll { self.finish(result) }
      }
    }
  }

  func imageCropperViewControllerDidCancel(_ controller: ImageCropperViewController) {
    guard !isFinished, !isBusy else {
      return
    }
    if isPickerMode, let picker = pickerController, picker.presentingViewController != nil {
      // Go back to the photo picker, like react-native-image-crop-picker.
      // The picker keeps the photo selected, so without this, tapping the
      // same photo again would only deselect it.
      if #available(iOS 17.0, *), let identifier = pickedAssetIdentifier {
        picker.deselectAssets(withIdentifiers: [identifier])
      }
      cropController = nil
      controller.dismiss(animated: true)
      return
    }
    dismissAll { self.finish(.failure(ImageCropPickerError.cancelled)) }
  }
}
