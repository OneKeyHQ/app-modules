package com.margelo.nitro.onekeyimage

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.avif.AvifGlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.signature.ObjectKey
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.facebook.react.modules.network.OkHttpClientProvider
import com.github.penfeizhou.animation.decode.FrameSeqDecoder
import com.github.penfeizhou.animation.glide.ByteBufferAnimationDecoder
import com.github.penfeizhou.animation.glide.GlideAnimationModule
import com.github.penfeizhou.animation.glide.StreamAnimationDecoder
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Registers the small set of Glide integrations required by OneKeyImage.
 *
 * Registration is explicit instead of declaring another AppGlideModule. Android
 * applications may only contain one AppGlideModule, and expo-image owns it while
 * the two image modules coexist during migration.
 */
internal object OneKeyImageGlideRegistry {
  @Volatile
  private var registered = false

  fun ensureRegistered(context: Context) {
    if (registered) return
    synchronized(this) {
      if (registered) return
      val appContext = context.applicationContext
      val glide = Glide.get(appContext)
      val registry = glide.registry

      registry.prepend(
        OneKeyImageDataUriModel::class.java,
        ByteBuffer::class.java,
        OneKeyDataUriModelLoaderFactory(),
      )
      registry.prepend(
        OneKeyImageLocalModel::class.java,
        InputStream::class.java,
        OneKeyImageLocalModelLoaderFactory(appContext),
      )
      val imageHttpClient = OkHttpClientProvider.createClient()
        .newBuilder()
        .addInterceptor(OneKeyImageEncodedDataInterceptor())
        .build()
      registry.prepend(
        OneKeyImageRemoteModel::class.java,
        InputStream::class.java,
        OneKeyImageRemoteModelLoaderFactory(imageHttpClient),
      )
      registry.append(
        InputStream::class.java,
        SVG::class.java,
        OneKeySvgDecoder(),
      )
      registry.register(
        SVG::class.java,
        Drawable::class.java,
        OneKeySvgDrawableTranscoder(),
      )

      // These dependencies also ship LibraryGlideModules. Calling them directly
      // keeps the feature available after expo-image (and its AppGlideModule) is
      // removed, while remaining harmless when Expo registered them already.
      AvifGlideModule().registerComponents(appContext, glide, registry)
      GlideAnimationModule().registerComponents(appContext, glide, registry)
      val animationDecoder = OneKeySafeAnimationDecoder()
      registry.prepend(
        InputStream::class.java,
        FrameSeqDecoder::class.java,
        OneKeySafeStreamAnimationDecoder(animationDecoder),
      )
      registry.prepend(
        ByteBuffer::class.java,
        FrameSeqDecoder::class.java,
        animationDecoder,
      )
      registered = true
    }
  }
}

private class OneKeySafeAnimationDecoder : ResourceDecoder<ByteBuffer, FrameSeqDecoder<*, *>> {
  private val delegate = ByteBufferAnimationDecoder()

  override fun handles(source: ByteBuffer, options: Options): Boolean =
    delegate.handles(source, options)

  override fun decode(
    source: ByteBuffer,
    width: Int,
    height: Int,
    options: Options,
  ): Resource<FrameSeqDecoder<*, *>>? {
    val resource = delegate.decode(source, width, height, options) ?: return null
    return OneKeyResourceCleanup.recycleOnException(resource::recycle) {
      val decoder = resource.get()
      val bounds = decoder.bounds
      val desired = OneKeyImageDecodeDimensions.forAnimatedDecoder(
        bounds.width(),
        bounds.height(),
        width,
        height,
      ) ?: throw OneKeyImageSafetyException(
        "Animated image cannot fit the 16 MiB target decode limit",
      )
      decoder.setDesiredSize(desired.width, desired.height)
      @Suppress("UNCHECKED_CAST")
      resource as Resource<FrameSeqDecoder<*, *>>
    }
  }
}

internal object OneKeyResourceCleanup {
  fun <T> recycleOnException(recycle: () -> Unit, block: () -> T): T = try {
    block()
  } catch (error: Exception) {
    try {
      recycle()
    } catch (cleanupError: Exception) {
      error.addSuppressed(cleanupError)
    }
    throw error
  }
}

/** Penfeizhou's stream decoder swallows IOExceptions while buffering the input. */
private class OneKeySafeStreamAnimationDecoder(
  private val byteBufferDecoder: ResourceDecoder<ByteBuffer, FrameSeqDecoder<*, *>>,
) : ResourceDecoder<InputStream, FrameSeqDecoder<*, *>> {
  private val handlesDelegate = StreamAnimationDecoder(byteBufferDecoder)

  override fun handles(source: InputStream, options: Options): Boolean =
    handlesDelegate.handles(source, options)

  override fun decode(
    source: InputStream,
    width: Int,
    height: Int,
    options: Options,
  ): Resource<FrameSeqDecoder<*, *>>? {
    val output = ByteArrayOutputStream(16 * 1024)
    val buffer = ByteArray(16 * 1024)
    while (true) {
      val count = source.read(buffer)
      if (count < 0) break
      if (count == 0) continue
      OneKeyImageSafety.requireEncodedLength(
        output.size().toLong() + count.toLong(),
        OneKeyImageSafety.MAX_ANIMATED_ENCODED_BYTES,
      )
      output.write(buffer, 0, count)
    }
    return byteBufferDecoder.decode(ByteBuffer.wrap(output.toByteArray()), width, height, options)
  }
}

private class OneKeyDataUriModelLoader : ModelLoader<OneKeyImageDataUriModel, ByteBuffer> {
  override fun handles(model: OneKeyImageDataUriModel): Boolean = true

  override fun buildLoadData(
    model: OneKeyImageDataUriModel,
    width: Int,
    height: Int,
    options: Options,
  ): ModelLoader.LoadData<ByteBuffer> =
    ModelLoader.LoadData(
      OneKeyImageSafetyVersionedKey(ObjectKey(model)),
      OneKeyDataUriFetcher(model.dataUri),
    )
}

private class OneKeyDataUriModelLoaderFactory :
  ModelLoaderFactory<OneKeyImageDataUriModel, ByteBuffer> {
  override fun build(
    multiFactory: MultiModelLoaderFactory,
  ): ModelLoader<OneKeyImageDataUriModel, ByteBuffer> =
    OneKeyDataUriModelLoader()

  override fun teardown() = Unit
}

private class OneKeyDataUriFetcher(private val dataUri: String) : DataFetcher<ByteBuffer> {
  override fun cleanup() = Unit
  override fun cancel() = Unit
  override fun getDataClass(): Class<ByteBuffer> = ByteBuffer::class.java
  override fun getDataSource(): DataSource = DataSource.LOCAL

  override fun loadData(
    priority: Priority,
    callback: DataFetcher.DataCallback<in ByteBuffer>,
  ) {
    try {
      val comma = dataUri.indexOf(',')
      require(comma >= 0 && dataUri.substring(0, comma).contains(";base64")) {
        "Only base64 image data URIs are supported"
      }
      val payloadStart = comma + 1
      val decodedByteCount = OneKeyImageSafety.dataUriDecodedByteCount(dataUri, payloadStart)
      OneKeyImageSafety.requireEncodedLength(
        decodedByteCount,
        OneKeyImageSafety.MAX_DATA_URI_DECODED_BYTES,
      )
      val decoded = Base64.decode(dataUri.substring(payloadStart), Base64.DEFAULT)
      OneKeyImageSafety.requireEncodedLength(
        decoded.size.toLong(),
        OneKeyImageSafety.MAX_DATA_URI_DECODED_BYTES,
      )
      val metadata = OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(decoded))
      if (metadata.animated) {
        OneKeyImageSafety.requireEncodedLength(
          decoded.size.toLong(),
          OneKeyImageSafety.MAX_ANIMATED_ENCODED_BYTES,
        )
        val format = metadata.animatedFormat
          ?: throw OneKeyImageSafetyException("Animated image format is unavailable")
        OneKeyAnimationTimelineInspector.validate(format, decoded)
      }
      callback.onDataReady(ByteBuffer.wrap(decoded))
    } catch (error: Exception) {
      callback.onLoadFailed(error)
    }
  }
}

private class OneKeySvgDecoder : ResourceDecoder<InputStream, SVG> {
  override fun handles(source: InputStream, options: Options): Boolean = true

  @Throws(IOException::class)
  override fun decode(
    source: InputStream,
    width: Int,
    height: Int,
    options: Options,
  ): Resource<SVG> {
    val svg = try {
      SVG.getFromInputStream(source)
    } catch (error: SVGParseException) {
      throw IOException("Cannot decode SVG", error)
    }
    if (svg.documentViewBox == null) {
      val documentWidth = svg.documentWidth
      val documentHeight = svg.documentHeight
      if (documentWidth > 0f && documentHeight > 0f) {
        svg.setDocumentViewBox(0f, 0f, documentWidth, documentHeight)
      }
    }

    val viewBox = svg.documentViewBox
    if (viewBox != null && viewBox.width() > 0f && viewBox.height() > 0f) {
      val scale = minOf(width.toFloat() / viewBox.width(), height.toFloat() / viewBox.height())
      if (scale.isFinite() && scale > 0f) {
        svg.documentWidth = viewBox.width() * scale
        svg.documentHeight = viewBox.height() * scale
      }
    }
    return SimpleResource(svg)
  }
}

private class OneKeySvgDrawableTranscoder : ResourceTranscoder<SVG, Drawable> {
  override fun transcode(toTranscode: Resource<SVG>, options: Options): Resource<Drawable> =
    SimpleResource(PictureDrawable(toTranscode.get().renderToPicture()))
}
