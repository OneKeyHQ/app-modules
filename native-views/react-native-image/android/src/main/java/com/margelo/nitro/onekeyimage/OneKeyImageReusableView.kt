package com.margelo.nitro.onekeyimage

import android.widget.FrameLayout
import com.facebook.react.uimanager.ThemedReactContext

/** A native-only OneKeyImage host for reusable container views such as list cells. */
class OneKeyImageReusableView(context: ThemedReactContext) : FrameLayout(context) {
  private val image = HybridOneKeyImage(context)

  init {
    addView(
      image.view,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
    )
    clipChildren = true
  }

  fun configure(
    sourceUri: String?,
    sourceHeadersJson: String?,
    variant: String,
    contentFit: String,
    cachePolicy: String,
    autoplay: Boolean,
    recyclingKey: String,
    optimizeTos: Boolean,
    overscan: Double,
    loadingStrategy: String,
  ) {
    image.sourceHeadersJson = sourceHeadersJson
    image.variant = when (variant) {
      "token" -> OneKeyImageVariant.TOKEN
      "network" -> OneKeyImageVariant.NETWORK
      "avatar" -> OneKeyImageVariant.AVATAR
      else -> OneKeyImageVariant.GENERIC
    }
    image.contentFit = when (contentFit) {
      "contain" -> OneKeyImageContentFit.CONTAIN
      "fill" -> OneKeyImageContentFit.FILL
      "center" -> OneKeyImageContentFit.CENTER
      else -> OneKeyImageContentFit.COVER
    }
    image.cachePolicy = when (cachePolicy) {
      "memory" -> OneKeyImageCachePolicy.MEMORY
      "disk" -> OneKeyImageCachePolicy.DISK
      "none" -> OneKeyImageCachePolicy.NONE
      else -> OneKeyImageCachePolicy.MEMORY_DISK
    }
    image.autoplay = autoplay
    image.recyclingKey = recyclingKey
    image.optimizeTos = optimizeTos
    image.overscan = overscan
    image.loadingStrategy = when (loadingStrategy) {
      "skeleton" -> OneKeyImageLoadingStrategy.SKELETON
      "none" -> OneKeyImageLoadingStrategy.NONE
      else -> OneKeyImageLoadingStrategy.STATIC
    }
    image.sourceUri = sourceUri
    image.afterUpdate()
  }

  fun prepareForReuse() {
    image.prepareForRecycle()
  }

  fun dispose() {
    image.onDropView()
  }
}
