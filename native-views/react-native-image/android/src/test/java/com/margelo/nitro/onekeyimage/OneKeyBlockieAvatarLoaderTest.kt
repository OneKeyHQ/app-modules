package com.margelo.nitro.onekeyimage

import androidx.core.util.Pools
import com.bumptech.glide.Priority
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.EncodeStrategy
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.io.File
import java.nio.file.Files
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class OneKeyBlockieAvatarLoaderTest {
  private val uri = "onekey-avatar://blockie/v1/0x1234"

  private class Callback : DataFetcher.DataCallback<ByteBuffer> {
    var data: ByteBuffer? = null
    var error: Exception? = null
    override fun onDataReady(data: ByteBuffer?) { this.data = data }
    override fun onLoadFailed(error: Exception) { this.error = error }
  }

  @Test
  fun memoryDiskAvatarRequestsCacheTheOriginalLocalPngAcrossSizes() {
    val strategy = oneKeyImageMemoryDiskStrategy(uri)
    assertTrue(strategy.isDataCacheable(DataSource.LOCAL))
    assertTrue(strategy.decodeCachedData())
    assertFalse(strategy.decodeCachedResource())
    assertFalse(strategy.isResourceCacheable(false, DataSource.LOCAL, EncodeStrategy.TRANSFORMED))
    assertFalse(strategy.isDataCacheable(DataSource.DATA_DISK_CACHE))
    assertFalse(DiskCacheStrategy.ALL.isDataCacheable(DataSource.LOCAL))
    assertEquals(DiskCacheStrategy.AUTOMATIC, oneKeyImageMemoryDiskStrategy("https://example.com/image.png"))
    assertEquals(DiskCacheStrategy.AUTOMATIC, oneKeyImageMemoryDiskStrategy("data:image/png;base64,AA=="))
    assertFalse(DiskCacheStrategy.AUTOMATIC.isDataCacheable(DataSource.LOCAL))
  }

  @Test
  fun originalPngCacheKeyIsStableAcrossRequestedDecodeDimensions() {
    val factory = MultiModelLoaderFactory(Pools.SynchronizedPool<List<Throwable>>(1))
    val loader = OneKeyBlockieAvatarLoaderFactory().build(factory)
    val first = loader.buildLoadData(OneKeyBlockieAvatarModel(uri), 96, 96, Options())!!
    val second = loader.buildLoadData(OneKeyBlockieAvatarModel(uri), 128, 128, Options())!!
    val other = loader.buildLoadData(OneKeyBlockieAvatarModel(uri + "0"), 96, 96, Options())!!
    assertEquals(first.sourceKey, second.sourceKey)
    assertNotEquals(first.sourceKey, other.sourceKey)
    val firstDigest = MessageDigest.getInstance("SHA-256").also(first.sourceKey::updateDiskCacheKey).digest()
    val secondDigest = MessageDigest.getInstance("SHA-256").also(second.sourceKey::updateDiskCacheKey).digest()
    assertArrayEquals(firstDigest, secondDigest)
  }

  private fun cacheFetcher(file: File): DataFetcher<ByteBuffer> {
    val factory = MultiModelLoaderFactory(Pools.SynchronizedPool<List<Throwable>>(1))
    val loader = OneKeyAvatarCacheFileLoaderFactory().build(factory)
    return loader.buildLoadData(file, 96, 96, Options().set(oneKeyAvatarCacheFileOption, true))!!.fetcher
  }

  @Test
  fun cacheValidationIsNotRegisteredForOrdinaryImageRequests() {
    val factory = MultiModelLoaderFactory(Pools.SynchronizedPool<List<Throwable>>(1))
    val loader = OneKeyAvatarCacheFileLoaderFactory().build(factory)
    assertNull(loader.buildLoadData(File("ordinary-network-image"), 96, 96, Options()))
  }

  @Test
  fun validCachedSourceIsReturnedWithoutRemovingOrRewritingIt() {
    val directory = Files.createTempDirectory("avatar-cache-valid").toFile()
    val file = File(directory, "avatar.0")
    val bytes = OneKeyBlockieAvatar.png(uri)
    try {
      file.writeBytes(bytes)
      val modified = file.lastModified()
      val fetcher = cacheFetcher(file)
      val callback = Callback()
      fetcher.loadData(Priority.NORMAL, callback)
      fetcher.cleanup()
      assertNull(callback.error)
      assertArrayEquals(bytes, callback.data!!.array())
      assertEquals(modified, file.lastModified())
      assertArrayEquals(bytes, file.readBytes())
    } finally {
      directory.deleteRecursively()
    }
  }

  @Test
  fun corruptEntryRemovalAllowsRealGlideJournalRewriteAndRestartReuse() {
    val directory = Files.createTempDirectory("avatar-cache-journal").toFile()
    val bytes = OneKeyBlockieAvatar.png(uri)
    try {
      DiskLruCache.open(directory, 1, 1, 1024L * 1024).use { disk ->
        disk.edit("avatar").also { editor ->
          editor.getFile(0).writeBytes(bytes.copyOf(bytes.size - 1))
          editor.commit()
        }
        val file = disk.get("avatar")!!.getFile(0)
        val fetcher = cacheFetcher(file)
        val callback = Callback()
        fetcher.loadData(Priority.NORMAL, callback)
        fetcher.cleanup()
        assertTrue(callback.error is IOException)
        assertNull(callback.data)
        assertFalse(file.exists())
        // DiskLruCacheWrapper.put uses this same journal lookup before deciding to skip a write.
        assertNull(disk.get("avatar"))
        disk.edit("avatar").also { editor ->
          editor.getFile(0).writeBytes(bytes)
          editor.commit()
        }
        assertArrayEquals(bytes, disk.get("avatar")!!.getFile(0).readBytes())
      }
      DiskLruCache.open(directory, 1, 1, 1024L * 1024).use { reopened ->
        val fetcher = cacheFetcher(reopened.get("avatar")!!.getFile(0))
        val callback = Callback()
        fetcher.loadData(Priority.NORMAL, callback)
        fetcher.cleanup()
        assertNull(callback.error)
        assertArrayEquals(bytes, callback.data!!.array())
      }
    } finally {
      directory.deleteRecursively()
    }
  }

  @Test
  fun oversizedCacheIsRejectedAndRemovedBeforeUnboundedRead() {
    val directory = Files.createTempDirectory("avatar-cache-large").toFile()
    val file = File(directory, "avatar.0")
    try {
      file.writeBytes(ByteArray(OneKeyBlockieAvatar.MAX_PNG_BYTES + 1))
      val fetcher = cacheFetcher(file)
      val callback = Callback()
      fetcher.loadData(Priority.NORMAL, callback)
      fetcher.cleanup()
      assertTrue(callback.error is IOException)
      assertFalse(file.exists())
    } finally {
      directory.deleteRecursively()
    }
  }

  @Test
  fun cancelledCacheReadDoesNotDeleteTheFileOrCallBack() {
    val directory = Files.createTempDirectory("avatar-cache-cancel").toFile()
    val file = File(directory, "avatar.0")
    try {
      file.writeBytes(byteArrayOf(1, 2, 3))
      val fetcher = cacheFetcher(file)
      val callback = Callback()
      fetcher.cancel()
      fetcher.loadData(Priority.NORMAL, callback)
      fetcher.cleanup()
      assertNull(callback.data)
      assertNull(callback.error)
      assertTrue(file.exists())
    } finally {
      directory.deleteRecursively()
    }
  }

  @Test
  fun modelDispatchDoesNotDecodeTheUriOrParseHeadersOnMain() {
    val model = OneKeyImageModel.build("onekey-avatar://blockie/v1/%INVALID", "invalid JSON")
    assertEquals(OneKeyBlockieAvatarModel("onekey-avatar://blockie/v1/%INVALID"), model)
  }

  @Test
  fun defaultFetcherGeneratesAValidatedPngAndReportsLocalData() {
    val fetcher = OneKeyBlockieAvatarFetcher(uri)
    val callback = Callback()
    try {
      fetcher.loadData(Priority.NORMAL, callback)
      assertNull(callback.error)
      assertEquals(DataSource.LOCAL, fetcher.dataSource)
      assertArrayEquals(OneKeyBlockieAvatar.png(uri), callback.data!!.array())
    } finally {
      fetcher.cleanup()
    }
  }

  @Test
  fun malformedUriReportsFailureWithoutReturningImageData() {
    val fetcher = OneKeyBlockieAvatarFetcher("onekey-avatar://blockie/v2/seed")
    val callback = Callback()
    try {
      fetcher.loadData(Priority.NORMAL, callback)
      assertNull(callback.data)
      assertTrue(callback.error is IllegalArgumentException)
    } finally {
      fetcher.cleanup()
    }
  }

  @Test
  fun cancelledBeforeLoadingDoesNotGenerateOrCallBack() {
    val generated = AtomicInteger()
    val fetcher = OneKeyBlockieAvatarFetcher(uri, OneKeyAvatarInFlight { _, _ ->
      generated.incrementAndGet()
      byteArrayOf(1)
    })
    val callback = Callback()
    fetcher.cancel()
    fetcher.loadData(Priority.NORMAL, callback)
    fetcher.cleanup()
    assertEquals(0, generated.get())
    assertNull(callback.data)
    assertNull(callback.error)
  }

  @Test
  fun cancellationSuppressesLateCallbacksAndReleasesTheWorkForAFreshRequest() {
    val started = CountDownLatch(1)
    val complete = CountDownLatch(1)
    val generated = AtomicInteger()
    val requests = OneKeyAvatarInFlight { _, cancelled ->
      if (generated.incrementAndGet() == 1) {
        started.countDown()
        check(complete.await(3, TimeUnit.SECONDS))
        assertTrue(cancelled())
      }
      byteArrayOf(1)
    }
    val fetcher = OneKeyBlockieAvatarFetcher(uri, requests)
    val callback = Callback()
    val executor = Executors.newSingleThreadExecutor()
    try {
      val loaded = executor.submit { fetcher.loadData(Priority.NORMAL, callback) }
      assertTrue(started.await(3, TimeUnit.SECONDS))
      fetcher.cancel()
      fetcher.cleanup()
      complete.countDown()
      loaded.get(3, TimeUnit.SECONDS)
      assertNull(callback.data)
      assertNull(callback.error)
      val fresh = OneKeyBlockieAvatarFetcher(uri, requests)
      val next = Callback()
      fresh.loadData(Priority.NORMAL, next)
      fresh.cleanup()
      assertArrayEquals(byteArrayOf(1), next.data!!.array())
      assertEquals(2, generated.get())
    } finally {
      fetcher.cleanup()
      complete.countDown()
      executor.shutdownNow()
    }
  }

  @Test
  fun cancellationNeverWaitsForAGlideCallbackHoldingItsOwnLock() {
    val callbackStarted = CountDownLatch(1)
    val callbackComplete = CountDownLatch(1)
    val fetcher = OneKeyBlockieAvatarFetcher(uri, OneKeyAvatarInFlight { _, _ -> byteArrayOf(1) })
    val executor = Executors.newFixedThreadPool(2)
    val callback = object : DataFetcher.DataCallback<ByteBuffer> {
      override fun onDataReady(data: ByteBuffer?) {
        callbackStarted.countDown()
        check(callbackComplete.await(3, TimeUnit.SECONDS))
      }
      override fun onLoadFailed(error: Exception) { throw error }
    }
    try {
      val loaded = executor.submit { fetcher.loadData(Priority.NORMAL, callback) }
      assertTrue(callbackStarted.await(3, TimeUnit.SECONDS))
      executor.submit { fetcher.cancel() }.get(1, TimeUnit.SECONDS)
      callbackComplete.countDown()
      loaded.get(3, TimeUnit.SECONDS)
    } finally {
      callbackComplete.countDown()
      fetcher.cleanup()
      executor.shutdownNow()
    }
  }

  @Test
  fun glideCallbackFailureIsNotReportedAsASecondGenerationFailure() {
    val failures = AtomicInteger()
    val expected = IllegalStateException("Synthetic callback failure")
    val fetcher = OneKeyBlockieAvatarFetcher(uri, OneKeyAvatarInFlight { _, _ -> byteArrayOf(1) })
    val callback = object : DataFetcher.DataCallback<ByteBuffer> {
      override fun onDataReady(data: ByteBuffer?) { throw expected }
      override fun onLoadFailed(error: Exception) { failures.incrementAndGet() }
    }
    try {
      fetcher.loadData(Priority.NORMAL, callback)
      org.junit.Assert.fail("Callback exception must propagate")
    } catch (error: IllegalStateException) {
      assertEquals(expected, error)
      assertEquals(0, failures.get())
    } finally {
      fetcher.cleanup()
    }
  }

  @Test
  fun failedGenerationIsReleasedSoRetryCanSucceed() {
    val generated = AtomicInteger()
    val requests = OneKeyAvatarInFlight { _, _ ->
      if (generated.incrementAndGet() == 1) throw IOException("Synthetic generation failure")
      byteArrayOf(1)
    }
    val failed = OneKeyBlockieAvatarFetcher(uri, requests)
    val first = Callback()
    failed.loadData(Priority.NORMAL, first)
    failed.cleanup()
    assertTrue(first.error is IOException)
    val retried = OneKeyBlockieAvatarFetcher(uri, requests)
    val second = Callback()
    retried.loadData(Priority.NORMAL, second)
    retried.cleanup()
    assertArrayEquals(byteArrayOf(1), second.data!!.array())
  }
}
