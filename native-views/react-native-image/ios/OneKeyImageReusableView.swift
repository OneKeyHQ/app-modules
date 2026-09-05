import UIKit

/// A native-only OneKeyImage host for reusable container views such as list cells.
public final class OneKeyImageReusableView: UIView {
  private let image = HybridOneKeyImage()

  public override init(frame: CGRect) {
    super.init(frame: frame)
    let imageView = image.view
    imageView.frame = bounds
    imageView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    addSubview(imageView)
    clipsToBounds = true
  }

  public required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public func configure(
    sourceUri: String?,
    sourceHeadersJson: String?,
    variant: String,
    contentFit: String,
    cachePolicy: String,
    autoplay: Bool,
    recyclingKey: String,
    optimizeTos: Bool,
    overscan: Double,
    loadingStrategy: String
  ) {
    image.sourceHeadersJson = sourceHeadersJson
    image.variant = OneKeyImageVariant(fromString: variant) ?? .generic
    image.contentFit = OneKeyImageContentFit(fromString: contentFit) ?? .cover
    image.cachePolicy = OneKeyImageCachePolicy(fromString: cachePolicy) ?? .memoryDisk
    image.autoplay = autoplay
    image.recyclingKey = recyclingKey
    image.optimizeTos = optimizeTos
    image.overscan = overscan
    image.loadingStrategy = OneKeyImageLoadingStrategy(fromString: loadingStrategy) ?? .static
    image.sourceUri = sourceUri
    image.afterUpdate()
  }

  public func prepareForReuse() {
    image.prepareForRecycle()
  }

  deinit {
    image.onDropView()
  }
}
