package com.margelo.nitro.onekeyimage

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.max

class HybridOneKeyImageCache : HybridOneKeyImageCacheSpec() {
  override fun preload(sources: Array<OneKeyImagePreloadSource>): Promise<Boolean> = Promise.async {
    val context = NitroModules.applicationContext
      ?: throw IllegalStateException("React application context is unavailable")
    OneKeyImageGlideRegistry.ensureRegistered(context)
    var allSucceeded = true
    sources.forEach { source ->
      if (source.uri.isBlank()) {
        allSucceeded = false
        return@forEach
      }
      val baseOptions = RequestOptions()
        .dontTransform()
        .downsample(OneKeyImageSafeDownsampleStrategy)
      val options = when (source.cachePolicy ?: OneKeyImageCachePolicy.MEMORY_DISK) {
        OneKeyImageCachePolicy.MEMORY_DISK -> baseOptions
          .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        OneKeyImageCachePolicy.MEMORY -> baseOptions
          .diskCacheStrategy(DiskCacheStrategy.NONE)
        OneKeyImageCachePolicy.DISK -> baseOptions
          .diskCacheStrategy(DiskCacheStrategy.DATA)
          .skipMemoryCache(true)
        OneKeyImageCachePolicy.NONE -> baseOptions
          .diskCacheStrategy(DiskCacheStrategy.NONE)
          .skipMemoryCache(true)
      }
      val displayWidth = source.resizeWidth?.takeIf { it.isFinite() && it > 0.0 }
      val displayHeight = source.resizeHeight?.takeIf { it.isFinite() && it > 0.0 }
      val displaySize = when {
        displayWidth != null && displayHeight != null -> max(displayWidth, displayHeight)
        displayWidth != null -> displayWidth
        else -> displayHeight
      }
      val density = (source.pixelRatio ?: context.resources.displayMetrics.density.toDouble())
        .takeIf { it.isFinite() }
        ?.coerceAtLeast(1.0)
        ?: 1.0
      val decodeDimensions = OneKeyImageDecodeDimensions.forPreload(
        displayWidth,
        displayHeight,
        density,
      )
      val hasCustomIdentity = OneKeyImageModel.headers(source.headersJson).isNotEmpty()
      val requestUrl = if (source.optimizeTos != false && displaySize != null) {
        OneKeyTosUrl.optimized(
          source.uri,
          ceil(displaySize).toInt(),
          density.toFloat(),
          source.overscan ?: 1.1,
          hasCustomIdentity,
        )
      } else source.uri

      val succeeded = try {
        load(context, requestUrl, source, options, decodeDimensions)
        true
      } catch (optimizedError: Exception) {
        if (
          requestUrl == source.uri ||
          OneKeyImageSafety.isSafetyFailure(optimizedError)
        ) {
          false
        } else {
          try {
            load(context, source.uri, source, options, decodeDimensions)
            true
          } catch (_: Exception) {
            false
          }
        }
      }
      allSucceeded = allSucceeded && succeeded
    }
    allSucceeded
  }

  override fun clearMemory(): Promise<Unit> = Promise.async {
    val context = NitroModules.applicationContext
      ?: throw IllegalStateException("React application context is unavailable")
    withContext(Dispatchers.Main) {
      Glide.get(context).clearMemory()
    }
  }

  override fun clearDisk(): Promise<Unit> = Promise.async {
    val context = NitroModules.applicationContext
      ?: throw IllegalStateException("React application context is unavailable")
    Glide.get(context).clearDiskCache()
  }

  override fun clearAll(): Promise<Unit> = Promise.async {
    val context = NitroModules.applicationContext
      ?: throw IllegalStateException("React application context is unavailable")
    withContext(Dispatchers.Main) {
      Glide.get(context).clearMemory()
    }
    Glide.get(context).clearDiskCache()
  }

  private fun load(
    context: android.content.Context,
    url: String,
    source: OneKeyImagePreloadSource,
    options: RequestOptions,
    decodeDimensions: OneKeyImageDecodeDimensions,
  ) {
    val request = Glide.with(context)
      .asDrawable()
      .load(OneKeyImageModel.build(url, source.headersJson))
      .apply(options)
      .override(decodeDimensions.width, decodeDimensions.height)
    val future = request.submit()
    try {
      future.get()
    } finally {
      Glide.with(context).clear(future)
    }
  }
}
