package com.margelo.nitro.onekeyimage

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.test.InstrumentationTestCase
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Suppress("DEPRECATION")
class OneKeyMediaPreviewCacheTest : InstrumentationTestCase() {
  private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
  private fun await(latch: CountDownLatch) { assertTrue("Timed out", latch.await(5, TimeUnit.SECONDS)) }
  private fun still(): Bitmap = Bitmap.createBitmap(32, 16, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.RED) }

  fun testConfiguredOrdinaryDiskBudgetIsDiscoveredFromMergedManifest() {
    val context = instrumentation.targetContext
    val info = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    assertEquals("GlideModule", info.metaData.getString("com.margelo.nitro.onekeyimage.OneKeyImageGlideModule"))
    fun field(owner: Any, name: String): Any = owner.javaClass.getDeclaredField(name).apply {
      isAccessible = true
    }.get(owner)!!
    val glide = Glide.get(context)
    val provider = field(field(glide, "engine"), "diskCacheProvider")
    val disk = provider.javaClass.getDeclaredMethod("getDiskCache").apply {
      isAccessible = true
    }.invoke(provider)!!
    assertEquals(320L * 1024 * 1024, field(disk, "maxSize"))
    assertEquals(File(context.cacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR), field(disk, "directory"))
  }

  fun testSameKeyDedupMemoryAndDiskReuse() {
    val directory = File(instrumentation.targetContext.cacheDir, "media-test-${UUID.randomUUID()}")
    val cache = OneKeyMediaPreviewCache.isolated(instrumentation.targetContext, directory)
    val delivered = CountDownLatch(2)
    var extracts = 0
    main {
      repeat(2) {
        cache.request("test://video", emptyMap(), 128, "contain", "memory-disk",
          onImage = { delivered.countDown() }, onExtract = { lease -> extracts++; cache.complete(lease, still()) },
          onRevoked = {}, onFailure = { fail("Unexpected failure") })
      }
    }
    await(delivered); assertEquals(1, extracts)
    val warm = CountDownLatch(1)
    main {
      cache.request("test://video", emptyMap(), 128, "contain", "memory-disk",
        onImage = { warm.countDown() }, onExtract = { fail("Memory miss") }, onRevoked = {}, onFailure = { fail() })
    }
    await(warm)
    val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
    while (directory.listFiles()?.none { it.name.endsWith(".0") } != false && System.nanoTime() < deadline) Thread.sleep(10)
    main { cache.clearMemory() }
    val disk = CountDownLatch(1)
    main {
      cache.request("test://video", emptyMap(), 128, "contain", "disk", onImage = {
        assertEquals(32, it.width); assertEquals(16, it.height); disk.countDown()
      }, onExtract = { fail("Disk miss") }, onRevoked = {}, onFailure = { fail() })
    }
    await(disk)
    val cleared = CountDownLatch(1); main { cache.clearDisk { cleared.countDown() } }; await(cleared)
  }

  fun testRealGlideEmbeddedSVGMemoryAndDataDiskHit() {
    val context = instrumentation.targetContext
    val encoded = ByteArrayOutputStream()
    val sample = still(); sample.compress(Bitmap.CompressFormat.PNG, 100, encoded); sample.recycle()
    val raster = Base64.encodeToString(encoded.toByteArray(), Base64.NO_WRAP)
    val svg = "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'>" +
      "<foreignObject width='24' height='24'><img xmlns='http://www.w3.org/1999/xhtml' " +
      "src='data:image/png;base64,$raster'/></foreignObject></svg>"
    val uri = "data:image/svg+xml;base64," + Base64.encodeToString(svg.toByteArray(), Base64.NO_WRAP)
    OneKeyImageGlideRegistry.ensureRegistered(context)
    Glide.get(context).clearDiskCache(); main { Glide.get(context).clearMemory() }
    fun load(diskOnly: Boolean): DataSource {
      var actual: DataSource? = null
      val target = Glide.with(context).asDrawable().load(OneKeyImageModel.build(uri, null))
        .dontTransform().override(64, 64).diskCacheStrategy(oneKeyImageMemoryDiskStrategy(uri))
        .onlyRetrieveFromCache(diskOnly)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(error: GlideException?, model: Any?, target: Target<Drawable>, first: Boolean): Boolean = false
          override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, source: DataSource, first: Boolean): Boolean {
            actual = source; return false
          }
        }).submit()
      try {
        val drawable = target.get(5, TimeUnit.SECONDS)
        val pixels = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        main { drawable.setBounds(0, 0, 64, 64); drawable.draw(Canvas(pixels)) }
        assertEquals(Color.RED, pixels.getPixel(32, 32)); pixels.recycle()
      }
      finally { main { Glide.with(context).clear(target) } }
      return actual ?: error("No datasource")
    }
    assertEquals(DataSource.LOCAL, load(false))
    assertEquals(DataSource.MEMORY_CACHE, load(false))
    main { Glide.get(context).clearMemory() }
    assertEquals(DataSource.DATA_DISK_CACHE, load(true))
  }

  fun testLastCancellationRemovesQueuedDiskLookupButKeepsSharedWaiter() {
    val directory = File(instrumentation.targetContext.cacheDir, "queued-preview-${UUID.randomUUID()}")
    val cache = OneKeyMediaPreviewCache.isolated(instrumentation.targetContext, directory)
    val ioField = cache.javaClass.getDeclaredField("io").apply { isAccessible = true }
    val io = ioField.get(cache) as java.util.concurrent.ThreadPoolExecutor
    val entered = CountDownLatch(1); val release = CountDownLatch(1); val drained = CountDownLatch(1)
    val callbacks = AtomicInteger()
    io.execute { entered.countDown(); release.await(5, TimeUnit.SECONDS) }; await(entered)
    main {
      fun request() = cache.request("test://cancel-before-lookup", emptyMap(), 128, "contain", "disk",
        onImage = { callbacks.incrementAndGet() }, onExtract = { callbacks.incrementAndGet() },
        onRevoked = {}, onFailure = { callbacks.incrementAndGet() })
      val first = request(); val second = request()
      assertEquals(1, io.queue.size)
      cache.cancel(first); assertEquals(1, io.queue.size)
      cache.cancel(second); assertEquals(0, io.queue.size)
      cache.clearDisk { drained.countDown() }
    }
    release.countDown(); await(drained); assertEquals(0, callbacks.get())
  }
}
