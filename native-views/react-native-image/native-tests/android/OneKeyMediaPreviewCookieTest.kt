package com.margelo.nitro.onekeyimage

import android.graphics.Bitmap
import android.graphics.Color
import android.test.InstrumentationTestCase
import android.webkit.CookieManager
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.signature.ObjectKey
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@Suppress("DEPRECATION")
class OneKeyMediaPreviewCookieTest : InstrumentationTestCase() {
  private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
  private fun await(latch: CountDownLatch) = assertTrue("Timed out", latch.await(5, TimeUnit.SECONDS))
  private fun bitmap(color: Int = Color.RED) = Bitmap.createBitmap(32, 16, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }
  private fun directory() = File(instrumentation.targetContext.cacheDir, "cookie-preview-${UUID.randomUUID()}")
  private fun cache(directory: File, state: () -> Boolean?) =
    OneKeyMediaPreviewCache.isolated(instrumentation.targetContext, directory, state)

  fun testPresentAndUnreadableCookiesUsePrivateFlightsIncludingNonePolicy() {
    for (initial in listOf(true, null)) for (policy in listOf("memory-disk", "none")) {
      val state = AtomicReference<Boolean?>(initial)
      val directory = directory(); val cache = cache(directory) { state.get() }
      val grants = CountDownLatch(2); val loaded = CountDownLatch(2)
      val leases = arrayOfNulls<UUID>(2); val delivered = mutableListOf<Int>()
      val uri = "https://preview.test/private-$policy.mp4"
      main {
        repeat(2) { index -> cache.request(uri, emptyMap(), 128, "contain", policy,
          onImage = { delivered.add(it.getPixel(0, 0)); loaded.countDown() },
          onExtract = { leases[index] = it; grants.countDown() }, onRevoked = {}, onFailure = { fail("private failed") }) }
      }
      await(grants); assertTrue(leases[0] != leases[1])
      main { cache.complete(leases[0]!!, bitmap(Color.RED)); cache.complete(leases[1]!!, bitmap(Color.BLUE)) }
      await(loaded); assertEquals(listOf(Color.RED, Color.BLUE), delivered)
      assertTrue(directory.listFiles()?.none { it.name.endsWith(".0") } != false)
      state.set(false)
      val retry = CountDownLatch(1); val hits = AtomicInteger(); var subscription: OneKeyMediaPreviewCache.Subscription? = null
      main { subscription = cache.request(uri, emptyMap(), 128, "contain", "memory-disk",
        onImage = { hits.incrementAndGet(); retry.countDown() }, onExtract = { retry.countDown() },
        onRevoked = {}, onFailure = { fail("anonymous retry failed") }) }
      await(retry); assertEquals(0, hits.get()); main { cache.cancel(subscription) }
    }
  }

  fun testActualAmbientCookieStoreIsPrivate() {
    val manager = CookieManager.getInstance()
    val url = "https://ambient-preview.test/"
    val installed = CountDownLatch(1)
    main { manager.setCookie(url, "preview-audit=fixture; Path=/; Secure") { installed.countDown() } }
    await(installed)
    try {
      assertTrue(manager.hasCookies())
      val cache = cache(directory()) { manager.hasCookies() }
      val grants = CountDownLatch(2); val leases = mutableListOf<UUID>(); val subscriptions = mutableListOf<OneKeyMediaPreviewCache.Subscription>()
      main { repeat(2) { subscriptions.add(cache.request("https://public-preview.test/movie.mp4", emptyMap(), 128, "contain", "none",
        onImage = { fail("unexpected cache hit") }, onExtract = { leases.add(it); grants.countDown() },
        onRevoked = {}, onFailure = { fail("private grant failed") })) } }
      await(grants); assertEquals(2, leases.toSet().size)
      main { subscriptions.forEach(cache::cancel) }
    } finally {
      val removed = CountDownLatch(1)
      main { manager.setCookie(url, "preview-audit=; Max-Age=0; Path=/; Secure") { removed.countDown() } }
      await(removed)
    }
  }

  fun testRedirectReadsAndSetCookieWritesRemainTaintedAfterCookieABA() {
    for (fileSource in listOf(false, true)) for (responseCookie in listOf(false, true)) {
      val state = AtomicReference<Boolean?>(false); val cache = cache(directory()) { state.get() }
      val uri = if (fileSource) "file:///cookie-preview.m3u8" else "https://preview.test/movie.mp4"
      val firstGrant = CountDownLatch(1); val privateGrants = CountDownLatch(2)
      val leases = arrayOfNulls<UUID>(3); val subscriptions = arrayOfNulls<OneKeyMediaPreviewCache.Subscription>(3)
      val images = Array(3) { AtomicInteger() }; val firstImage = CountDownLatch(1); val secondImage = CountDownLatch(1)
      main { repeat(3) { index -> subscriptions[index] = cache.request(uri, emptyMap(), 128, "contain", "memory",
        onImage = { images[index].incrementAndGet(); if (index == 0) firstImage.countDown() else if (index == 1) secondImage.countDown() },
        onExtract = { leases[index] = it; if (index == 0) firstGrant.countDown() else privateGrants.countDown() },
        onRevoked = {}, onFailure = { fail("tainted request failed") }) } }
      await(firstGrant); assertNull(leases[1]); assertNull(leases[2])
      val cookie = Cookie.Builder().domain("redirect-preview.test").name("fixture").value("cookie-b").build()
      val reads = AtomicInteger(); val writes = AtomicInteger(); var tracked: CookieJar? = null
      main { tracked = cache.trackCookies(leases[0]!!, object : CookieJar {
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
          reads.incrementAndGet(); return if (url.host == "redirect-preview.test") listOf(cookie) else emptyList()
        }
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) { writes.incrementAndGet() }
      }) }
      assertTrue(tracked!!.loadForRequest("https://preview.test/movie.mp4".toHttpUrl()).isEmpty())
      state.set(true)
      if (responseCookie) tracked!!.saveFromResponse("https://redirect-preview.test/range".toHttpUrl(), listOf(cookie))
      else assertEquals(1, tracked!!.loadForRequest("https://redirect-preview.test/range".toHttpUrl()).size)
      // The source snapshot is empty again; the actual jar observation must retain the taint.
      state.set(false)
      assertEquals(if (responseCookie) 1 else 2, reads.get()); assertEquals(if (responseCookie) 1 else 0, writes.get())
      main { cache.complete(leases[0]!!, bitmap(Color.RED)) }
      await(firstImage); await(privateGrants)
      assertEquals(0, images[1].get()); assertEquals(0, images[2].get())
      assertTrue(leases[0] != leases[1]); assertTrue(leases[1] != leases[2])
      main { cache.cancel(subscriptions[2]); cache.complete(leases[2]!!, bitmap(Color.RED)); cache.complete(leases[1]!!, bitmap(Color.BLUE)) }
      await(secondImage); assertEquals(0, images[2].get())
      val retry = CountDownLatch(1); val hits = AtomicInteger(); var retrySub: OneKeyMediaPreviewCache.Subscription? = null
      main { retrySub = cache.request(uri, emptyMap(), 128, "contain", "memory",
        onImage = { hits.incrementAndGet(); retry.countDown() }, onExtract = { retry.countDown() },
        onRevoked = {}, onFailure = { fail("post-taint retry failed") }) }
      await(retry); assertEquals(0, hits.get()); main { cache.cancel(retrySub) }
    }
  }

  fun testAnonymousEmptyCookieAccessStillSharesAndCaches() {
    val cache = cache(directory()) { false }; val loaded = CountDownLatch(2); val extracts = AtomicInteger()
    val uri = "https://anonymous-preview.test/movie.mp4"
    main { repeat(2) { cache.request(uri, emptyMap(), 128, "contain", "memory",
      onImage = { loaded.countDown() }, onExtract = { lease ->
        extracts.incrementAndGet()
        val tracked = cache.trackCookies(lease, object : CookieJar {
          override fun loadForRequest(url: HttpUrl): List<Cookie> = emptyList()
          override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) = Unit
        })
        assertTrue(tracked.loadForRequest(uri.toHttpUrl()).isEmpty())
        tracked.saveFromResponse(uri.toHttpUrl(), emptyList()); cache.complete(lease, bitmap(Color.GREEN))
      }, onRevoked = {}, onFailure = { fail("anonymous failed") }) } }
    await(loaded); assertEquals(1, extracts.get())
    val hit = CountDownLatch(1); val color = AtomicInteger()
    main { cache.request(uri, emptyMap(), 128, "contain", "memory",
      onImage = { color.set(it.getPixel(0, 0)); hit.countDown() }, onExtract = { extracts.incrementAndGet(); hit.countDown() },
      onRevoked = {}, onFailure = { fail("memory failed") }) }
    await(hit); assertEquals(Color.GREEN, color.get()); assertEquals(1, extracts.get())
  }

  fun testUnreadableCookieStateBypassesExistingMemoryAndDiskEntries() {
    val state = AtomicReference<Boolean?>(false); val directory = directory(); val cache = cache(directory) { state.get() }
    val uri = "https://unreadable-preview.test/movie.mp4"; val primed = CountDownLatch(1)
    main { cache.request(uri, emptyMap(), 128, "contain", "memory-disk",
      onImage = { primed.countDown() }, onExtract = { cache.complete(it, bitmap()) }, onRevoked = {}, onFailure = { fail("prime failed") }) }
    await(primed)
    val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
    while (directory.listFiles()?.none { it.name.endsWith(".0") } != false && System.nanoTime() < deadline) Thread.sleep(10)
    assertTrue(directory.listFiles()?.any { it.name.endsWith(".0") } == true)
    state.set(null)
    repeat(2) { diskOnly ->
      val event = CountDownLatch(1); val hits = AtomicInteger(); var subscription: OneKeyMediaPreviewCache.Subscription? = null
      main {
        if (diskOnly == 1) cache.clearMemory()
        subscription = cache.request(uri, emptyMap(), 128, "contain", "memory-disk",
          onImage = { hits.incrementAndGet(); event.countDown() }, onExtract = { event.countDown() },
          onRevoked = {}, onFailure = { fail("unreadable failed") })
      }
      await(event); assertEquals(0, hits.get()); main { cache.cancel(subscription) }
    }
  }

  fun testLegacyV1PersistentFrameCannotBecomeAnonymousAfterLogout() {
    val directory = directory(); val uri = "https://legacy.preview.test/movie.mp4"
    // This is the production v1 storage identity for this fixed source, size and fit.
    val legacyKey = "40043315a6e6831a273afab7001f8e53d1e5e8292cdd998716f1f3e78e5c9c9a"
    assertTrue(legacyKey != OneKeyMediaPreviewCache.storageKey(uri, emptyMap(), 128, "contain"))
    val digest = MessageDigest.getInstance("SHA-256"); ObjectKey(legacyKey).updateDiskCacheKey(digest)
    val safeKey = digest.digest().joinToString("") { "%02x".format(it.toInt() and 255) }
    DiskLruCache.open(directory, 1, 1, 64L * 1024 * 1024).use { disk ->
      val edit = disk.edit(safeKey)!!
      edit.getFile(0).outputStream().use { bitmap(Color.RED).compress(Bitmap.CompressFormat.PNG, 100, it) }
      edit.commit(); disk.flush()
    }
    val cache = cache(directory) { false }; val event = CountDownLatch(1); val hits = AtomicInteger()
    var subscription: OneKeyMediaPreviewCache.Subscription? = null
    main { subscription = cache.request(uri, emptyMap(), 128, "contain", "disk",
      onImage = { hits.incrementAndGet(); event.countDown() }, onExtract = { event.countDown() },
      onRevoked = {}, onFailure = { fail("legacy retry failed") }) }
    await(event); assertEquals(0, hits.get()); main { cache.cancel(subscription) }
  }

  fun testLastCancellationRemovesQueuedDiskLookupButKeepsSharedWaiter() {
    val cache = cache(directory()) { false }
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

  fun testNewSubscriberCannotReuseOlderAnonymousAdmissionSnapshot() {
    val state = AtomicReference<Boolean?>(false)
    val captured = CountDownLatch(1); val release = CountDownLatch(1); val probes = AtomicInteger()
    val cache = cache(directory()) {
      val snapshot = state.get()
      if (probes.incrementAndGet() == 1) {
        captured.countDown(); assertTrue(release.await(5, TimeUnit.SECONDS))
      }
      snapshot
    }
    val granted = CountDownLatch(2); val loaded = CountDownLatch(2)
    val leases = mutableListOf<UUID>(); val colors = mutableListOf<Int>()
    fun request() = cache.request("https://admission-preview.test/movie.mp4", emptyMap(), 128, "contain", "memory-disk",
      onImage = { colors.add(it.getPixel(0, 0)); loaded.countDown() },
      onExtract = { leases.add(it); granted.countDown() }, onRevoked = {}, onFailure = { fail("admission failed") })
    main {
      request(); assertTrue(captured.await(5, TimeUnit.SECONDS))
      state.set(true); request(); release.countDown()
    }
    await(granted); assertTrue(leases[0] != leases[1]); assertTrue(probes.get() >= 2)
    main { cache.complete(leases[0], bitmap(Color.RED)); cache.complete(leases[1], bitmap(Color.BLUE)) }
    await(loaded); assertEquals(listOf(Color.RED, Color.BLUE), colors)
  }

  fun testHttpBurstCoalescesBeforeAdmissionCapacityAndOverflowCanCancel() {
    val gate = CountDownLatch(1); val cache = cache(directory()) { gate.await(5, TimeUnit.SECONDS); false }
    val loaded = CountDownLatch(131); val rejected = AtomicInteger(); val extracts = AtomicInteger(); val overflow = CountDownLatch(1)
    fun request(uri: String, overflowRequest: Boolean = false) = cache.request(uri, emptyMap(), 128, "contain", "none",
      onImage = { loaded.countDown() }, onExtract = { extracts.incrementAndGet(); cache.complete(it, bitmap()) },
      onRevoked = {}, onFailure = { rejected.incrementAndGet(); if (overflowRequest) overflow.countDown() })
    main {
      repeat(130) { request("https://capacity-preview.test/$it.mp4") }
      request("https://capacity-preview.test/0.mp4")
      request("https://capacity-preview.test/overflow.mp4", true)
      val cancelled = request("https://capacity-preview.test/cancelled.mp4", true); cache.cancel(cancelled)
    }
    gate.countDown(); await(overflow); await(loaded)
    assertEquals(1, rejected.get()); assertEquals(130, extracts.get())
    val burstGate = CountDownLatch(1); val burst = cache(directory()) { burstGate.await(5, TimeUnit.SECONDS); false }
    val all = CountDownLatch(140); val burstExtracts = AtomicInteger(); val burstRejected = AtomicInteger()
    main { repeat(140) { burst.request("https://burst-preview.test/movie.mp4", emptyMap(), 128, "contain", "none",
      onImage = { all.countDown() }, onExtract = { burstExtracts.incrementAndGet(); burst.complete(it, bitmap()) },
      onRevoked = {}, onFailure = { burstRejected.incrementAndGet(); all.countDown() }) } }
    burstGate.countDown(); await(all); assertEquals(0, burstRejected.get()); assertEquals(1, burstExtracts.get())
  }
}
