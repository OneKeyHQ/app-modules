package com.margelo.nitro.onekeyimage

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.bumptech.glide.request.RequestOptions
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException
import java.util.concurrent.CancellationException

// Only source PNGs are persisted; transformed resources can have other dimensions.
internal fun oneKeyImageMemoryDiskStrategy(uri: String): DiskCacheStrategy =
  if (OneKeyBlockieAvatar.isAvatarUri(uri)) DiskCacheStrategy.DATA else DiskCacheStrategy.AUTOMATIC

internal val oneKeyAvatarCacheFileOption: Option<Boolean> =
  Option.memory("onekey-image.blockie-source-cache-v1", false)

internal fun RequestOptions.withOneKeyAvatarCache(uri: String): RequestOptions =
  if (OneKeyBlockieAvatar.isAvatarUri(uri)) set(oneKeyAvatarCacheFileOption, true) else this

internal class OneKeyAvatarCacheFileLoaderFactory : ModelLoaderFactory<File, ByteBuffer> {
  override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<File, ByteBuffer> =
    OneKeyAvatarCacheFileLoader()

  override fun teardown() = Unit
}

private class OneKeyAvatarCacheFileLoader : ModelLoader<File, ByteBuffer> {
  override fun handles(model: File): Boolean = true

  override fun buildLoadData(
    model: File,
    width: Int,
    height: Int,
    options: Options,
  ): ModelLoader.LoadData<ByteBuffer>? =
    if (options.get(oneKeyAvatarCacheFileOption) == true) {
      ModelLoader.LoadData(ObjectKey(model), OneKeyAvatarCacheFileFetcher(model))
    } else null
}

// Serialize validation/removal so two failed reads cannot delete a newly repaired file.
private val avatarCacheFileLock = Any()

private class OneKeyAvatarCacheFileFetcher(private val file: File) : DataFetcher<ByteBuffer> {
  @Volatile
  private var cancelled = false

  override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
    val bytes = try {
      synchronized(avatarCacheFileLock) {
        checkCancelled()
        val bytes = readBounded()
        if (bytes == null || !OneKeyBlockieAvatar.isValidPng(bytes) { cancelled }) {
          checkCancelled()
          if (file.exists() && !file.delete()) throw IOException("Cannot remove invalid avatar cache entry")
          // Glide's journal sees the missing clean file; SOURCE can write it again.
          throw IOException("Invalid avatar cache entry removed")
        }
        bytes
      }
    } catch (error: Exception) {
      if (!cancelled) callback.onLoadFailed(error)
      return
    }
    if (!cancelled) callback.onDataReady(ByteBuffer.wrap(bytes))
  }

  private fun readBounded(): ByteArray? {
    if (file.length() > OneKeyBlockieAvatar.MAX_PNG_BYTES) return null
    return file.inputStream().use { input ->
      val output = ByteArrayOutputStream()
      val buffer = ByteArray(1024)
      while (true) {
        checkCancelled()
        val count = input.read(buffer)
        if (count < 0) break
        if (output.size() + count > OneKeyBlockieAvatar.MAX_PNG_BYTES) return null
        output.write(buffer, 0, count)
      }
      output.toByteArray()
    }
  }

  private fun checkCancelled() {
    if (cancelled) throw CancellationException("Avatar cache read cancelled")
  }

  override fun cancel() { cancelled = true }
  override fun cleanup() = cancel()
  override fun getDataClass(): Class<ByteBuffer> = ByteBuffer::class.java
  override fun getDataSource(): DataSource = DataSource.LOCAL
}

internal class OneKeyBlockieAvatarLoaderFactory : ModelLoaderFactory<OneKeyBlockieAvatarModel, ByteBuffer> {
  override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<OneKeyBlockieAvatarModel, ByteBuffer> =
    OneKeyBlockieAvatarLoader()

  override fun teardown() = Unit
}

private val avatarRequests = OneKeyAvatarInFlight { uri, isCancelled ->
  OneKeyImageSafety.requireEncodedLength(uri.length.toLong(), OneKeyImageSafety.MAX_DATA_URI_DECODED_BYTES)
  val png = OneKeyBlockieAvatar.png(uri, isCancelled)
  OneKeyImageSafety.requireEncodedLength(png.size.toLong(), OneKeyImageSafety.MAX_DATA_URI_DECODED_BYTES)
  OneKeyEncodedImageInspector.inspect(ByteArrayInputStream(png))
  png
}

private class OneKeyBlockieAvatarLoader : ModelLoader<OneKeyBlockieAvatarModel, ByteBuffer> {
  override fun handles(model: OneKeyBlockieAvatarModel): Boolean = true

  override fun buildLoadData(
    model: OneKeyBlockieAvatarModel,
    width: Int,
    height: Int,
    options: Options,
  ): ModelLoader.LoadData<ByteBuffer> = ModelLoader.LoadData(
    OneKeyImageSafetyVersionedKey(ObjectKey(model)),
    OneKeyBlockieAvatarFetcher(model.uri),
  )
}

internal class OneKeyBlockieAvatarFetcher(
  private val uri: String,
  private val requests: OneKeyAvatarInFlight = avatarRequests,
) : DataFetcher<ByteBuffer> {
  @Volatile
  private var cancelled = false
  private var lease: OneKeyAvatarInFlight.Lease? = null

  override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
    val request = synchronized(this) {
      if (cancelled) return
      requests.acquire(uri).also { lease = it }
    }
    // Glide invokes loadData on its source executor, never on the UI thread.
    val png = try {
      request.bytes()
    } catch (error: Exception) {
      val cause = if (error is ExecutionException) error.cause else error
      if (!cancelled) callback.onLoadFailed(cause as? Exception ?: IOException("Avatar generation failed"))
      return
    }
    // Glide may cancel while holding its EngineJob lock; do not call back under ours.
    if (!cancelled) callback.onDataReady(ByteBuffer.wrap(png))
  }

  override fun cancel() = release()
  override fun cleanup() = release()

  private fun release() {
    val request = synchronized(this) {
      cancelled = true
      lease.also { lease = null }
    }
    request?.release()
  }

  override fun getDataClass(): Class<ByteBuffer> = ByteBuffer::class.java
  override fun getDataSource(): DataSource = DataSource.LOCAL
}
