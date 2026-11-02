import NitroModules
import UIKit

private final class OneKeyIconHost: UIImageView {
  var changed: (() -> Void)?
  override func layoutSubviews() { super.layoutSubviews(); changed?() }
  override func didMoveToWindow() { super.didMoveToWindow(); changed?() }
  override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
    super.traitCollectionDidChange(previousTraitCollection)
    if previousTraitCollection?.displayScale != traitCollection.displayScale { changed?() }
  }
}

final class HybridOneKeyIcon: HybridOneKeyIconSpec, RecyclableView {
  private let host = OneKeyIconHost()
  private let owner = NSObject()
  private var generation: UInt64 = 0
  private var identity: String?
  private var displayedGeometry: String?
  private var displayed: OneKeyNamedSVG.RenderResult?
  var iconName: String?
  var resolvedColor: String?
  var view: UIView { host }

  override init() {
    super.init()
    host.contentMode = .scaleAspectFit
    host.changed = { [weak self] in self?.refresh() }
  }

  func afterUpdate() { refresh() }

  private func clearImage() {
    displayed = nil; displayedGeometry = nil; host.image = nil
  }

  private func apply(_ result: OneKeyNamedSVG.RenderResult, color: String) {
    displayed = result
    if let tint = result.tintColor(color) {
      host.image = result.image.withRenderingMode(.alwaysTemplate)
      host.tintColor = tint
    } else {
      host.image = result.image.withRenderingMode(.alwaysOriginal)
    }
  }

  private func refresh() {
    let scale = host.window?.screen.scale ?? 1
    let width = ceil(host.bounds.width * scale), height = ceil(host.bounds.height * scale)
    let attached = host.window != nil
    let next = "\(iconName ?? "")|\(resolvedColor ?? "")|\(width)|\(height)|\(scale)|\(attached)"
    guard next != identity else { return }
    identity = next; generation &+= 1
    let revision = generation
    OneKeyNamedSVG.shared.cancel(owner: owner)
    guard attached, width.isFinite, height.isFinite,
          width > 0, height > 0, width <= 4096, height <= 4096, width * height <= 4_194_304,
          let name = iconName, !name.isEmpty,
          let color = resolvedColor, OneKeyNamedSVG.isValidColor(color) else { clearImage(); return }
    let geometry = "\(name)|\(width)|\(height)|\(scale)"
    if displayedGeometry == geometry, let result = displayed, host.image != nil {
      if result.canTint(color) {
        if let tint = result.tintColor(color) { host.tintColor = tint }
        return
      }
      // Keep only a successful same-source/geometry raster while new color decodes.
    } else { clearImage() }
    OneKeyNamedSVG.shared.requestRender(owner: owner, name: name, color: color,
      width: Int(width), height: Int(height), scale: scale) { [weak self] result in
      guard let self, self.generation == revision, self.host.window != nil else { return }
      guard let result else { self.clearImage(); return }
      self.displayedGeometry = geometry
      self.apply(result, color: color)
    }
  }

  func prepareForRecycle() {
    iconName = nil; resolvedColor = nil; refresh()
  }

  func onDropView() { prepareForRecycle() }

  deinit { OneKeyNamedSVG.shared.cancel(owner: owner) }
}
