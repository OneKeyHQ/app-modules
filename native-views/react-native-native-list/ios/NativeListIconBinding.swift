import OneKeyImage
import UIKit

/// One request owner per glyph slot; metadata also supplies detached measurement.
private final class NativeListIconBinding {
  private weak var view: UIView?
  private let owner = NSObject()
  private let apply: (UIImage?) -> Void
  private var descriptor: NativeListIconMetadata?
  private var pointSize = CGSize.zero
  private var template = true
  private var sizeOverride: CGSize?
  private var currentImage: UIImage?
  private var placeholder: UIImage?
  private var updateDepth = 0
  private var pendingClear = false
  private var revision: UInt64 = 0
  private var identity: String?

  init(view: UIView, apply: @escaping (UIImage?) -> Void) { self.view = view; self.apply = apply }

  func bind(_ name: String?, size: CGSize? = nil) {
    guard let name, let metadata = nativeListIconMetadata(named: name) else {
      clearNow(); return
    }
    if descriptor?.name == metadata.name, descriptor?.original == metadata.original,
      descriptor?.size == metadata.size, sizeOverride == size {
      pendingClear = false
      apply(currentImage)
      refresh(); return
    }
    clearNow()
    descriptor = metadata
    pointSize = size ?? metadata.size
    sizeOverride = size
    template = size != nil || !metadata.original
    let format = UIGraphicsImageRendererFormat(); format.scale = 1
    placeholder = UIGraphicsImageRenderer(size: pointSize, format: format).image { _ in }
      .withRenderingMode(template ? .alwaysTemplate : .alwaysOriginal)
    currentImage = placeholder
    apply(currentImage)
    refresh()
  }

  func clear(display: Bool = true) {
    if display, updateDepth > 0 {
      pendingClear = true; apply(nil); return
    }
    clearNow(display: display)
  }

  private func clearNow(display: Bool = true) {
    revision &+= 1; identity = nil; descriptor = nil; currentImage = nil; placeholder = nil
    pendingClear = false
    OneKeyNamedSVG.shared.cancel(owner: owner)
    if display { apply(nil) }
  }

  func beginUpdate() { updateDepth += 1 }
  func endUpdate() {
    updateDepth -= 1
    guard updateDepth == 0 else { return }
    if pendingClear { clearNow() } else { refresh() }
  }

  func retint() { if descriptor != nil && !pendingClear { apply(currentImage) } }

  func refresh(force: Bool = false) {
    guard updateDepth == 0, let view, let descriptor else { return }
    let attached = view.window != nil && !view.isHidden && !view.bounds.isEmpty
    let screenScale = max(1, view.window?.screen.scale ?? view.traitCollection.displayScale)
    let fit = min(view.bounds.width / pointSize.width, view.bounds.height / pointSize.height)
    let rasterScale = ceil(screenScale * max(1, fit))
    let next = "\(descriptor.name)|\(pointSize)|\(rasterScale)|\(screenScale)|\(attached)|\(template)"
    guard force || next != identity else { return }
    identity = next; revision &+= 1
    let generation = revision
    OneKeyNamedSVG.shared.cancel(owner: owner)
    currentImage = placeholder
    apply(currentImage)
    guard attached else { return }
    if descriptor.name == "Circle" {
      let image = UIGraphicsImageRenderer(size: pointSize).image { _ in
        UIColor.black.setFill(); UIBezierPath(ovalIn: CGRect(origin: .zero, size: pointSize)).fill()
      }
      currentImage = image.withRenderingMode(.alwaysTemplate); apply(currentImage); return
    }
    let sourceSize = descriptor.size
    let sourceScale = sizeOverride == nil ? rasterScale : ceil(rasterScale * max(pointSize.width / sourceSize.width, pointSize.height / sourceSize.height))
    let pixelWidth = ceil(sourceSize.width * sourceScale), pixelHeight = ceil(sourceSize.height * sourceScale)
    guard pixelWidth.isFinite, pixelHeight.isFinite, pixelWidth > 0, pixelHeight > 0,
      pixelWidth <= 4096, pixelHeight <= 4096, pixelWidth * pixelHeight <= 4_194_304 else { return }
    OneKeyNamedSVG.shared.request(owner: owner, name: descriptor.name, color: "rgba(0,0,0,1)",
      width: Int(pixelWidth), height: Int(pixelHeight),
      scale: screenScale) { [weak self] image in
      guard let self, self.revision == generation, let view = self.view,
        view.window != nil, !view.isHidden else { return }
      guard let cgImage = image?.cgImage else { return }
      var result = UIImage(cgImage: cgImage, scale: sourceScale, orientation: .up)
      if let size = self.sizeOverride {
        let format = UIGraphicsImageRendererFormat(); format.scale = rasterScale
        result = UIGraphicsImageRenderer(size: size, format: format).image { _ in
          result.draw(in: CGRect(origin: .zero, size: size))
        }
      }
      self.currentImage = result.withRenderingMode(self.template ? .alwaysTemplate : .alwaysOriginal)
      self.apply(self.currentImage)
    }
  }

  deinit { OneKeyNamedSVG.shared.cancel(owner: owner) }
}

final class NativeListIconImageView: UIImageView {
  private var applying = false
  private lazy var iconBinding = NativeListIconBinding(view: self) { [weak self] image in
    guard let self else { return }
    self.applying = true; self.image = image; self.applying = false
  }
  func bindIcon(_ name: String?, size: CGSize? = nil) { iconBinding.bind(name, size: size) }
  func beginIconUpdate() { iconBinding.beginUpdate() }
  func endIconUpdate() { iconBinding.endUpdate() }
  override var image: UIImage? { didSet { if !applying { iconBinding.clear(display: image == nil) } } }
  override var isHidden: Bool { didSet { iconBinding.refresh() } }
  override func layoutSubviews() { super.layoutSubviews(); iconBinding.refresh() }
  override func didMoveToWindow() { super.didMoveToWindow(); iconBinding.refresh() }
  override func tintColorDidChange() { super.tintColorDidChange(); iconBinding.retint() }
  override func traitCollectionDidChange(_ previous: UITraitCollection?) {
    super.traitCollectionDidChange(previous); iconBinding.refresh()
  }
}

class NativeListIconButton: UIButton {
  private var applying = false
  private lazy var iconBinding = NativeListIconBinding(view: self) { [weak self] image in
    guard let self else { return }
    self.applying = true
    self.setImage(image, for: .normal)
    self.setImage(image?.withTintColor(self.tintColor, renderingMode: .alwaysOriginal), for: .disabled)
    self.applying = false
  }
  func bindIcon(_ name: String?, size: CGSize? = nil) { iconBinding.bind(name, size: size) }
  func beginIconUpdate() { iconBinding.beginUpdate() }
  func endIconUpdate() { iconBinding.endUpdate() }
  override func setImage(_ image: UIImage?, for state: UIControl.State) {
    if !applying && state == .normal { iconBinding.clear(display: image == nil) }
    super.setImage(image, for: state)
  }
  override var isHidden: Bool { didSet { iconBinding.refresh() } }
  override func layoutSubviews() { super.layoutSubviews(); iconBinding.refresh() }
  override func didMoveToWindow() { super.didMoveToWindow(); iconBinding.refresh() }
  override func tintColorDidChange() { super.tintColorDidChange(); iconBinding.retint() }
  override func traitCollectionDidChange(_ previous: UITraitCollection?) {
    super.traitCollectionDidChange(previous); iconBinding.refresh()
  }
}

/// A synchronous renderer bind may clear then restore the same glyph descriptor.
final class NativeListIconUpdate {
  private var images: [NativeListIconImageView] = []
  private var buttons: [NativeListIconButton] = []
  init(_ root: UIView) {
    func collect(_ view: UIView) {
      if let image = view as? NativeListIconImageView { images.append(image); image.beginIconUpdate() }
      if let button = view as? NativeListIconButton { buttons.append(button); button.beginIconUpdate() }
      view.subviews.forEach(collect)
    }
    collect(root)
  }
  func finish() { images.forEach { $0.endIconUpdate() }; buttons.forEach { $0.endIconUpdate() } }
}
