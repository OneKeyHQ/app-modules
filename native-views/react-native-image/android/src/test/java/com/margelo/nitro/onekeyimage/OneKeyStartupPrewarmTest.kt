package com.margelo.nitro.onekeyimage

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.nio.file.Files
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.zip.CRC32
import java.util.zip.DeflaterOutputStream
import kotlin.concurrent.thread

class OneKeyStartupPrewarmTest {
  private lateinit var tempDir: File

  private fun dummyBitmap(): Bitmap {
    val unsafeClass = Class.forName("sun.misc.Unsafe")
    val theUnsafe = unsafeClass.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null)
    val allocateInstance = unsafeClass.getMethod("allocateInstance", Class::class.java)
    return allocateInstance.invoke(theUnsafe, Bitmap::class.java) as Bitmap
  }

  private fun dummyAssetManager(): AssetManager {
    val unsafeClass = Class.forName("sun.misc.Unsafe")
    val theUnsafe = unsafeClass.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null)
    val allocateInstance = unsafeClass.getMethod("allocateInstance", Class::class.java)
    return allocateInstance.invoke(theUnsafe, AssetManager::class.java) as AssetManager
  }

  private fun createValidPng(width: Int, height: Int): ByteArray {
    val out = ByteArrayOutputStream()
    out.write(byteArrayOf(0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a))
    fun writeChunk(type: String, payload: ByteArray) {
      val dos = DataOutputStream(out)
      val typeBytes = type.toByteArray(Charsets.US_ASCII)
      dos.writeInt(payload.size)
      dos.write(typeBytes)
      dos.write(payload)
      val crc = CRC32().apply {
        update(typeBytes)
        update(payload)
      }
      dos.writeInt(crc.value.toInt())
    }
    val ihdr = ByteArrayOutputStream().also {
      DataOutputStream(it).apply {
        writeInt(width)
        writeInt(height)
        writeByte(8) // bit depth
        writeByte(6) // RGBA
        writeByte(0)
        writeByte(0)
        writeByte(0)
      }
    }.toByteArray()
    writeChunk("IHDR", ihdr)
    val idat = ByteArrayOutputStream().also { raw ->
      DeflaterOutputStream(raw).use { deflater ->
        deflater.write(ByteArray(height * (1 + minOf(width, 4) * 4)))
      }
    }.toByteArray()
    writeChunk("IDAT", idat)
    writeChunk("IEND", ByteArray(0))
    return out.toByteArray()
  }

  @Before
  fun setUp() {
    tempDir = Files.createTempDirectory("onekey-android-startup-test").toFile()
    OneKeyMainThreadDecode.isEnabled = true
    OneKeyMainThreadDecode.mainThreadCheckOverrideForTesting = { true }
    OneKeyMainThreadDecode.resetForTesting()
    OneKeyImageStartupPrewarm.bitmapByteCountOverrideForTesting = { 4096L }
    OneKeyImageStartupPrewarm.bitmapDecoderOverrideForTesting = { _, _, _, _, _, _ ->
      dummyBitmap()
    }
    OneKeyImageStartupPrewarm.resetForTesting(
      file = File(tempDir, "onekey-image-startup-v1.json"),
      cacheDir = tempDir,
    )
    OneKeyNamedSVG.bitmapByteCountOverrideForTesting = { 4096L }
    OneKeyNamedSVG.diskPngDecoderOverrideForTesting = { file, _, _ ->
      if (file.isFile && file.length() > 0L) dummyBitmap() else null
    }
    OneKeyNamedSVG.clearMemoryCacheForTesting()
    OneKeyNamedSVG.setManifestFallbackForTesting(
      mapOf(
        "DotVerSolid" to OneKeyNamedSVG.ManifestEntry(
          digest = "digest-dot",
          mode = OneKeyNamedSVG.RecolorMode.SINGLE,
        ),
        "HomeOutline" to OneKeyNamedSVG.ManifestEntry(
          digest = "digest-home",
          mode = OneKeyNamedSVG.RecolorMode.SINGLE,
        ),
        "ChartPositionOutline" to OneKeyNamedSVG.ManifestEntry(
          digest = "digest-chart",
          mode = OneKeyNamedSVG.RecolorMode.MULTIPLE,
        ),
        "BotIllus" to OneKeyNamedSVG.ManifestEntry(
          digest = "digest-bot",
          mode = OneKeyNamedSVG.RecolorMode.FIXED,
        ),
      ),
    )
    OneKeyNamedSVG.resetStartupForTesting(
      file = File(tempDir, "onekey-named-svg-startup-v1.json"),
      cacheDir = tempDir,
    )
  }

  @After
  fun tearDown() {
    OneKeyMainThreadDecode.mainThreadCheckOverrideForTesting = null
    OneKeyMainThreadDecode.resetForTesting()
    OneKeyImageStartupPrewarm.bitmapDecoderOverrideForTesting = null
    OneKeyImageStartupPrewarm.bitmapByteCountOverrideForTesting = null
    OneKeyImageStartupPrewarm.resetForTesting()
    OneKeyNamedSVG.diskPngDecoderOverrideForTesting = null
    OneKeyNamedSVG.bitmapByteCountOverrideForTesting = null
    OneKeyNamedSVG.setManifestFallbackForTesting(null)
    OneKeyNamedSVG.resetStartupForTesting()
    tempDir.deleteRecursively()
  }

  @Test
  fun mainThreadDecodeEnforcesBudgetAndTracksMetrics() {
    // 1. Hit and miss tracking
    val hit = OneKeyMainThreadDecode.run { 42 }
    assertEquals(42, hit)
    val miss = OneKeyMainThreadDecode.run<Int> { null }
    assertNull(miss)

    var s = OneKeyMainThreadDecode.metricsSnapshot()
    assertEquals(2, s.attempts)
    assertEquals(1, s.hits)
    assertEquals(1, s.misses)
    assertEquals(0, s.budgetRejections)

    // 2. Exhaust budget within a 1s window
    OneKeyMainThreadDecode.setBudgetForTesting(budgetNs = 1_000_000L, windowNs = 1_000_000_000L)
    val slowHit = OneKeyMainThreadDecode.run {
      val deadline = System.nanoTime() + 2_000_000L
      while (System.nanoTime() < deadline) {
        // Busy wait 2ms
      }
      true
    }
    assertEquals(true, slowHit)

    val rejected = OneKeyMainThreadDecode.run { true }
    assertNull(rejected)
    s = OneKeyMainThreadDecode.metricsSnapshot()
    assertEquals(1, s.budgetRejections)
  }

  @Test
  fun mainThreadDecodePromotesSmallLocalAndCachedImagesAndRejectsOversized() {
    OneKeyMainThreadDecode.setBudgetForTesting(budgetNs = 1_000_000_000L, windowNs = 10_000_000_000L)
    val smallPng = OneKeyBlockieAvatar.png("onekey-avatar://blockie/v1/0xabc123")
    val fileImg = File(tempDir, "small.png").apply { writeBytes(smallPng) }
    val fileUrl = "file://${fileImg.absolutePath}"

    // 1. Small local file:// promotes within budget
    val promotedFile = OneKeyMainThreadDecode.run {
      if (OneKeyMainThreadDecode.promoteToMemory(tempDir, fileUrl, fileUrl, 64, 64, false)) true else null
    }
    assertEquals(true, promotedFile)
    assertNotNull(OneKeyImageStartupPrewarm.cachedBitmap(fileUrl, 64, 64, false))

    // 2. Small data: URI promotes within budget
    val dataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(smallPng)
    val promotedData = OneKeyMainThreadDecode.run {
      if (OneKeyMainThreadDecode.promoteToMemory(tempDir, dataUri, dataUri, 48, 48, true)) true else null
    }
    assertEquals(true, promotedData)
    assertNotNull(OneKeyImageStartupPrewarm.cachedBitmap(dataUri, 48, 48, true))

    // 3. Target > 256px rejected
    assertFalse(OneKeyMainThreadDecode.promoteToMemory(tempDir, fileUrl, fileUrl, 300, 300, false))

    // 4. Source > 512px rejected
    val largeSourcePng = createValidPng(600, 600)
    val largeSourceFile = File(tempDir, "large-source.png").apply { writeBytes(largeSourcePng) }
    val largeSourceUrl = "file://${largeSourceFile.absolutePath}"
    assertFalse(OneKeyMainThreadDecode.promoteToMemory(tempDir, largeSourceUrl, largeSourceUrl, 64, 64, false))

    // 5. File > 32KiB rejected
    val hugeBytes = smallPng + ByteArray(33 * 1024)
    val hugeFile = File(tempDir, "huge.png").apply { writeBytes(hugeBytes) }
    val hugeUrl = "file://${hugeFile.absolutePath}"
    assertFalse(OneKeyMainThreadDecode.promoteToMemory(tempDir, hugeUrl, hugeUrl, 64, 64, false))
    assertTrue(OneKeyMainThreadDecode.metricsSnapshot().limitRejections >= 3)
  }

  @Test
  fun imageStartupPrewarmHashesKeysEvictsByLaunchAndRespectsQuietWindow() {
    val startupJsonFile = File(tempDir, "onekey-image-startup-v1.json")
    val glideCacheDir = File(tempDir, OneKeyImageStartupPrewarm.GLIDE_DISK_CACHE_DIR).apply { mkdirs() }

    val secretAvatarUrl = "onekey-avatar://blockie/v1/0xdeadbeefcafebabe1234567890"
    val remoteUrl = "https://cdn.onekey.so/banner-secret-token.png"
    val pngBytes = OneKeyBlockieAvatar.png(secretAvatarUrl)

    // Seed Glide disk cache for remoteUrl
    val remoteSafeKey = requireNotNull(OneKeyImageStartupPrewarm.glideDataSafeKey(remoteUrl))
    File(glideCacheDir, "$remoteSafeKey.0").writeBytes(pngBytes)

    // 1. Single entry (below MIN_SETTLED_COUNT = 2) keeps window open on non-forced debounce
    OneKeyImageStartupPrewarm.note(
      cacheDir = tempDir,
      requestUrl = secretAvatarUrl,
      rawUrl = secretAvatarUrl,
      headersJson = null,
      cachePolicy = OneKeyImageCachePolicy.MEMORY_DISK,
      width = 96,
      height = 96,
      round = true,
    )
    OneKeyImageStartupPrewarm.flushForTesting(forceClose = false)
    assertFalse("Window must remain open while < 2 images seen (App Lock)", startupJsonFile.exists())

    // 2. Second entry after unlocking settles and persists Launch 1
    OneKeyImageStartupPrewarm.note(
      cacheDir = tempDir,
      requestUrl = remoteUrl,
      rawUrl = remoteUrl,
      headersJson = null,
      cachePolicy = OneKeyImageCachePolicy.MEMORY_DISK,
      width = 120,
      height = 60,
      round = false,
    )
    OneKeyImageStartupPrewarm.flushForTesting(forceClose = true)
    assertTrue(startupJsonFile.isFile)

    // 3. Privacy check: persisted JSON must contain zero plaintext URLs or wallet addresses
    val rawJson = startupJsonFile.readText(Charsets.UTF_8)
    assertFalse(rawJson.contains("0xdeadbeef"))
    assertFalse(rawJson.contains("onekey-avatar"))
    assertFalse(rawJson.contains("banner-secret-token"))
    assertFalse(rawJson.contains("https://"))

    val launch1Entries = OneKeyImageStartupPrewarm.readEntries(startupJsonFile)
    assertEquals(2, launch1Entries.size)
    assertTrue(launch1Entries.all { it.launch == 1 })

    // 4. Prewarm loads both entries from disk into memory cache by keyHash
    OneKeyImageStartupPrewarm.clearMemoryCache()
    OneKeyImageStartupPrewarm.prewarmStartupSet(tempDir)
    val metrics = OneKeyImageStartupPrewarm.metricsSnapshot()
    assertEquals(2, metrics.entryCount)
    assertEquals(2, metrics.decodedCount)
    assertNotNull(OneKeyImageStartupPrewarm.cachedBitmap(secretAvatarUrl, 96, 96, true))
    assertNotNull(OneKeyImageStartupPrewarm.cachedBitmap(remoteUrl, 120, 60, false))
    assertNull(
      "Custom headers must bypass unauthenticated prewarmed bitmaps",
      OneKeyImageStartupPrewarm.cachedBitmap(
        remoteUrl,
        120,
        60,
        false,
        headersJson = """{"Authorization":"Bearer secret"}""",
      ),
    )

    // 5. Launch-generation eviction: after 3 launches of only remoteUrl, secretAvatarUrl is evicted
    val avatarDiskSafeKey = requireNotNull(OneKeyImageStartupPrewarm.glideDataSafeKey(secretAvatarUrl))
    val avatarFallbackFile = File(File(tempDir, OneKeyImageStartupPrewarm.FALLBACK_DISK_CACHE_DIR), avatarDiskSafeKey)
    assertTrue("Avatar fallback file should exist after Launch 1", avatarFallbackFile.isFile)
    for (expectedLaunch in 2..4) {
      OneKeyImageStartupPrewarm.resetForTesting(file = startupJsonFile, cacheDir = tempDir)
      OneKeyImageStartupPrewarm.note(
        cacheDir = tempDir,
        requestUrl = remoteUrl,
        rawUrl = remoteUrl,
        headersJson = null,
        cachePolicy = OneKeyImageCachePolicy.MEMORY_DISK,
        width = 120,
        height = 60,
        round = false,
      )
      OneKeyImageStartupPrewarm.flushForTesting(forceClose = true)
      val entries = OneKeyImageStartupPrewarm.readEntries(startupJsonFile)
      if (expectedLaunch < 4) {
        assertEquals(2, entries.size)
        assertTrue(avatarFallbackFile.isFile)
      } else {
        assertEquals("Stale avatar must be evicted at launch 4 (age 3)", 1, entries.size)
        assertEquals(
          OneKeyImageStartupPrewarm.keyHash(remoteUrl, 120, 60, false),
          entries[0].keyHash,
        )
        assertFalse("Evicted fallback file must be cleaned from disk", avatarFallbackFile.exists())
      }
    }

    // 6. clearDiskCache removes startup JSON and fallback disk cache directory,
    // and invalidates any stale pre-clear fallback write generation.
    val preClearGeneration = OneKeyImageStartupPrewarm.currentDiskClearGenerationForTesting()
    OneKeyImageStartupPrewarm.clearDiskCache(tempDir)
    OneKeyImageStartupPrewarm.writeFallbackDiskBytes(
      cacheDir = tempDir,
      safeKey = avatarDiskSafeKey,
      bytes = pngBytes,
      expectedGeneration = preClearGeneration,
    )
    assertFalse(startupJsonFile.exists())
    assertFalse(File(tempDir, OneKeyImageStartupPrewarm.FALLBACK_DISK_CACHE_DIR).exists())
  }

  @Test
  fun namedSvgDiskRenderBridgeSyncCallbackAndStartupEvictionWork() {
    val startupJsonFile = File(tempDir, "onekey-named-svg-startup-v1.json")
    val assets = dummyAssetManager()

    // Seed disk raster PNG for DotVerSolid (key = "DotVerSolid|digest-dot|48|48|2.0|SINGLE|")
    val dotKey = "DotVerSolid|digest-dot|48|48|2.0|SINGLE|"
    val dotRasterFile = requireNotNull(OneKeyNamedSVG.diskRasterFile(dotKey))
    dotRasterFile.parentFile?.mkdirs()
    dotRasterFile.writeBytes(createValidPng(48, 48))

    // 1. diskRender hits synchronously on main thread and populates memory cache
    val diskHit = OneKeyNamedSVG.diskRender(assets, "DotVerSolid", "rgba(0,0,0,1)", 48, 48, 2f)
    assertNotNull(diskHit)
    val memHit = OneKeyNamedSVG.cachedRender("DotVerSolid", "rgba(0,0,0,1)", 48, 48, 2f)
    assertSame(diskHit?.bitmap, memHit?.bitmap)

    // 2. OneKeyNamedSVG.request invokes callback synchronously on main thread when cached or on disk
    OneKeyNamedSVG.clearMemoryCacheForTesting()
    OneKeyMainThreadDecode.resetForTesting()
    var syncCallbackRan = false
    var syncBitmap: Bitmap? = null
    OneKeyNamedSVG.request(Any(), assets, "DotVerSolid", "rgba(0,0,0,1)", 48, 48, 2f) { bmp ->
      syncCallbackRan = true
      syncBitmap = bmp
    }
    assertTrue("OneKeyNamedSVG.request must complete synchronously on main thread", syncCallbackRan)
    assertNotNull(syncBitmap)

    // 3. Manifest lock contention makes diskRender return null without blocking and increments metric
    OneKeyNamedSVG.clearMemoryCacheForTesting()
    OneKeyMainThreadDecode.resetForTesting()
    val locked = CountDownLatch(1)
    val release = CountDownLatch(1)
    val holder = thread {
      assertTrue(OneKeyNamedSVG.tryAcquireManifestAccessLockForTesting())
      locked.countDown()
      release.await()
      OneKeyNamedSVG.unlockManifestAccessLockForTesting()
    }
    locked.await()
    val contendedResult = try {
      OneKeyNamedSVG.diskRender(assets, "DotVerSolid", "rgba(0,0,0,1)", 48, 48, 2f)
    } finally {
      release.countDown()
      holder.join()
    }
    assertNull(contendedResult)
    val contendedMetrics = OneKeyMainThreadDecode.metricsSnapshot()
    assertEquals(1, contendedMetrics.lockContentionMisses)
    assertEquals(1, contendedMetrics.attempts)
    assertEquals(1, contendedMetrics.misses)

    // 4. Startup quiet window (< 8 icons stays open on non-forced flush) and 3-launch eviction
    OneKeyNamedSVG.resetStartupForTesting(file = startupJsonFile, cacheDir = tempDir)
    OneKeyNamedSVG.cachedRender("DotVerSolid", "rgba(0,0,0,1)", 48, 48, 2f)
    OneKeyNamedSVG.flushStartupForTesting(forceClose = false)
    assertFalse("Icon window must stay open when < 8 icons seen", startupJsonFile.exists())

    OneKeyNamedSVG.cachedRender("HomeOutline", "rgba(0,0,0,1)", 48, 48, 2f)
    OneKeyNamedSVG.flushStartupForTesting(forceClose = true)
    assertEquals(2, OneKeyNamedSVG.readStartupEntries(startupJsonFile).size)

    for (launch in 2..4) {
      OneKeyNamedSVG.resetStartupForTesting(file = startupJsonFile, cacheDir = tempDir)
      OneKeyNamedSVG.cachedRender("DotVerSolid", "rgba(0,0,0,1)", 48, 48, 2f)
      OneKeyNamedSVG.flushStartupForTesting(forceClose = true)
      val entries = OneKeyNamedSVG.readStartupEntries(startupJsonFile)
      if (launch < 4) {
        assertEquals(2, entries.size)
      } else {
        assertEquals(1, entries.size)
        assertEquals("DotVerSolid", entries[0].name)
      }
    }
  }
}
