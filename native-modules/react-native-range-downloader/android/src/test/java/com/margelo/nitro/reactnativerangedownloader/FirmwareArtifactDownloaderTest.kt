package com.margelo.nitro.reactnativerangedownloader

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.createTempDirectory

class FirmwareArtifactDownloaderTest {
  private lateinit var root: File
  private lateinit var store: FirmwareArtifactStore
  private lateinit var server: MockWebServer
  private lateinit var payload: ByteArray
  private val requestedRanges = mutableListOf<String>()

  @Before
  fun setUp() {
    root = createTempDirectory("firmware-downloader-").toFile()
    store = FirmwareArtifactStore(root)
    payload = ByteArray(512 * 1024) { index -> (index % 251).toByte() }
    server = MockWebServer()
    server.dispatcher = rangeDispatcher()
    server.start()
  }

  @After
  fun tearDown() {
    server.shutdown()
    root.deleteRecursively()
  }

  @Test
  fun rangeBodiesStreamIntoVerifiedFinal() {
    val lease = store.createLease("transaction-${UUID.randomUUID()}")
    val request = request(lease.leaseRef)
    val downloader = FirmwareArtifactDownloader(
      store = store,
      domainClient = OkHttpClient(),
    )

    val metadata = downloader.download(request)

    assertEquals(payload.size.toLong(), metadata.actualSize)
    assertNotNull(metadata.immutableToken)
    assertArrayEquals(payload, store.finalFile(metadata.artifactRef).readBytes())
    assertFalse(store.partialFile(metadata.artifactRef).exists())
    assertEquals(5, server.requestCount) // one probe + four planned ranges
    assertEquals("bytes=0-0", requestedRanges.first())
  }

  @Test
  fun expiredOverallDeadlineStopsBeforeTheFirstNetworkRequest() {
    val lease = store.createLease("transaction-${UUID.randomUUID()}")
    val request = request(lease.leaseRef).copy(
      deadlineAtMillis = System.currentTimeMillis() - 1,
      deadlineAtNanos = System.nanoTime() - 1,
    )
    val downloader = FirmwareArtifactDownloader(
      store = store,
      domainClient = OkHttpClient(),
    )

    val error = assertThrows(FirmwareArtifactStoreException::class.java) {
      downloader.download(request)
    }

    assertEquals("ARTIFACT_DEADLINE_EXCEEDED", error.code)
    assertEquals(0, server.requestCount)
  }

  @Test
  fun range200OnResumeReprobesAndRebuildsFromZero() {
    val lease = store.createLease("transaction-${UUID.randomUUID()}")
    val taskId = "task-${UUID.randomUUID()}"
    val request = request(lease.leaseRef, taskId = taskId, segmentCount = 2)
    val ranges = listOf(
      0L..(payload.size / 2L - 1),
      payload.size / 2L..(payload.size.toLong() - 1),
    )
    val claimed = store.beginTransferAttempt(
      leaseRef = lease.leaseRef,
      taskId = taskId,
      artifactId = request.artifactId,
      canonicalUrl = request.url.toString(),
      hostname = request.url.host,
      route = request.route,
      expectedSize = request.expectedSize,
      expectedSha256 = request.expectedSha256,
      initialDeadlineAtMillis = request.deadlineAtMillis,
      maxRunAttempts = 8,
    )
    val seeded = store.prepareArtifactForDownload(
      artifactRef = claimed.artifactRef,
      leaseRef = lease.leaseRef,
      taskId = taskId,
      artifactId = request.artifactId,
      canonicalUrl = request.url.toString(),
      hostname = request.url.host,
      route = request.route,
      expectedSize = request.expectedSize,
      expectedSha256 = request.expectedSha256,
      strongETag = "\"firmware-v1\"",
      lastModified = null,
      ranges = ranges,
    )
    store.segmentFile(seeded.artifactRef, 0)
      .writeBytes(payload.copyOfRange(0, 32 * 1024))

    val objectChanged = AtomicBoolean(false)
    val probeCount = AtomicInteger(0)
    val rebuiltFirstSegment = AtomicBoolean(false)
    server.dispatcher = objectChangeDispatcher(
      objectChanged = objectChanged,
      probeCount = probeCount,
      rebuiltFirstSegment = rebuiltFirstSegment,
    )

    val metadata = FirmwareArtifactDownloader(
      store = store,
      domainClient = OkHttpClient(),
    ).download(request)

    assertArrayEquals(payload, store.finalFile(metadata.artifactRef).readBytes())
    assertTrue("the resumed range must receive a full response", objectChanged.get())
    assertTrue("a full range response must trigger a fresh probe", probeCount.get() >= 2)
    assertTrue(
      "the incompatible partial must be rebuilt from the first byte",
      rebuiltFirstSegment.get(),
    )
  }

  @Test
  fun range416ReprobesAndRetainsStableCompletedSegments() {
    val lease = store.createLease("transaction-${UUID.randomUUID()}")
    val taskId = "task-${UUID.randomUUID()}"
    val request = request(lease.leaseRef, taskId = taskId, segmentCount = 2)
    val split = payload.size / 2
    val ranges = listOf(
      0L..(split.toLong() - 1),
      split.toLong()..(payload.size.toLong() - 1),
    )
    val claimed = store.beginTransferAttempt(
      leaseRef = lease.leaseRef,
      taskId = taskId,
      artifactId = request.artifactId,
      canonicalUrl = request.url.toString(),
      hostname = request.url.host,
      route = request.route,
      expectedSize = request.expectedSize,
      expectedSha256 = request.expectedSha256,
      initialDeadlineAtMillis = request.deadlineAtMillis,
      maxRunAttempts = 8,
    )
    val seeded = store.prepareArtifactForDownload(
      artifactRef = claimed.artifactRef,
      leaseRef = lease.leaseRef,
      taskId = taskId,
      artifactId = request.artifactId,
      canonicalUrl = request.url.toString(),
      hostname = request.url.host,
      route = request.route,
      expectedSize = request.expectedSize,
      expectedSha256 = request.expectedSha256,
      strongETag = "\"firmware-v1\"",
      lastModified = null,
      ranges = ranges,
    )
    store.segmentFile(seeded.artifactRef, 0)
      .writeBytes(payload.copyOfRange(0, split))

    val sent416 = AtomicBoolean(false)
    val probeCount = AtomicInteger(0)
    val firstSegmentRequests = AtomicInteger(0)
    server.dispatcher = stable416Dispatcher(
      split = split,
      sent416 = sent416,
      probeCount = probeCount,
      firstSegmentRequests = firstSegmentRequests,
    )

    val metadata = FirmwareArtifactDownloader(
      store = store,
      domainClient = OkHttpClient(),
    ).download(request)

    assertArrayEquals(payload, store.finalFile(metadata.artifactRef).readBytes())
    assertTrue("the range fault must be exercised", sent416.get())
    assertTrue("HTTP 416 must trigger a fresh probe", probeCount.get() >= 2)
    assertEquals(
      "a stable validator must preserve completed segment bytes",
      0,
      firstSegmentRequests.get(),
    )
  }

  @Test
  fun cancellationInterruptsAStalledResponseBody() {
    val lease = store.createLease("transaction-${UUID.randomUUID()}")
    val taskId = "task-${UUID.randomUUID()}"
    val request = request(lease.leaseRef, taskId = taskId, segmentCount = 1)
    server.dispatcher = object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        val range = requireNotNull(request.getHeader("Range"))
        if (range == "bytes=0-0") {
          return rangeResponse(0, 0, "\"firmware-v1\"")
        }
        return rangeResponse(0, payload.size - 1, "\"firmware-v1\"")
          .throttleBody(1, 1, TimeUnit.SECONDS)
      }
    }
    val downloader = FirmwareArtifactDownloader(
      store = store,
      registry = FirmwareArtifactRegistry(),
      domainClient = OkHttpClient(),
    )
    val executor = Executors.newSingleThreadExecutor()
    val future = executor.submit<StoredArtifactMetadata> {
      downloader.download(request)
    }
    assertNotNull(server.takeRequest(5, TimeUnit.SECONDS))
    assertNotNull(server.takeRequest(5, TimeUnit.SECONDS))
    Thread.sleep(100)

    val startedAt = System.nanoTime()
    downloader.cancel(lease.leaseRef, taskId)
    assertThrows(Exception::class.java) {
      future.get(2, TimeUnit.SECONDS)
    }
    val elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)

    assertTrue("cancel took ${elapsedMillis}ms", elapsedMillis < 500)
    executor.shutdownNow()
  }

  private fun request(
    leaseRef: String,
    taskId: String = "task-${UUID.randomUUID()}",
    segmentCount: Int = 4,
  ): FirmwareArtifactValidatedRequest =
    FirmwareArtifactValidatedRequest(
      taskId = taskId,
      leaseRef = leaseRef,
      artifactId = "firmware-main",
      url = server.url("/firmware.bin").toString().toHttpUrl(),
      route = StoredArtifactRoute.DOMAIN,
      resolvedIp = null,
      expectedSize = payload.size.toLong(),
      expectedSha256 = sha256(payload),
      maxBytes = payload.size.toLong(),
      segmentCount = segmentCount,
    )

  private fun rangeDispatcher(): Dispatcher =
    object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        val range = requireNotNull(request.getHeader("Range"))
        synchronized(requestedRanges) {
          requestedRanges += range
        }
        val match = requireNotNull(
          Regex("""bytes=(\d+)-(\d+)""").matchEntire(range)
        )
        val first = match.groupValues[1].toInt()
        val last = match.groupValues[2].toInt()
        val body = Buffer().write(payload, first, last - first + 1)
        return MockResponse()
          .setResponseCode(206)
          .setHeader("Content-Range", "bytes $first-$last/${payload.size}")
          .setHeader("Content-Length", last - first + 1)
          .setHeader("ETag", "\"firmware-v1\"")
          .setBody(body)
      }
    }

  private fun objectChangeDispatcher(
    objectChanged: AtomicBoolean,
    probeCount: AtomicInteger,
    rebuiltFirstSegment: AtomicBoolean,
  ): Dispatcher =
    object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        val range = requireNotNull(request.getHeader("Range"))
        val match = requireNotNull(
          Regex("""bytes=(\d+)-(\d+)""").matchEntire(range)
        )
        val first = match.groupValues[1].toInt()
        val last = match.groupValues[2].toInt()
        if (first == 0 && last == 0) {
          probeCount.incrementAndGet()
          return rangeResponse(first, last, if (objectChanged.get()) {
            "\"firmware-v2\""
          } else {
            "\"firmware-v1\""
          })
        }
        if (objectChanged.compareAndSet(false, true)) {
          return MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Length", payload.size)
            .setHeader("ETag", "\"firmware-v2\"")
            .setBody(Buffer().write(payload))
        }
        if (first == 0) {
          rebuiltFirstSegment.set(true)
        }
        return rangeResponse(first, last, "\"firmware-v2\"")
      }
    }

  private fun stable416Dispatcher(
    split: Int,
    sent416: AtomicBoolean,
    probeCount: AtomicInteger,
    firstSegmentRequests: AtomicInteger,
  ): Dispatcher =
    object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        val range = requireNotNull(request.getHeader("Range"))
        val match = requireNotNull(
          Regex("""bytes=(\d+)-(\d+)""").matchEntire(range)
        )
        val first = match.groupValues[1].toInt()
        val last = match.groupValues[2].toInt()
        if (first == 0 && last == 0) {
          probeCount.incrementAndGet()
          return rangeResponse(first, last, "\"firmware-v1\"")
        }
        if (first < split) {
          firstSegmentRequests.incrementAndGet()
        }
        if (first >= split && sent416.compareAndSet(false, true)) {
          return MockResponse()
            .setResponseCode(416)
            .setHeader("Content-Range", "bytes */${payload.size}")
        }
        return rangeResponse(first, last, "\"firmware-v1\"")
      }
    }

  private fun rangeResponse(
    first: Int,
    last: Int,
    etag: String,
  ): MockResponse =
    MockResponse()
      .setResponseCode(206)
      .setHeader("Content-Range", "bytes $first-$last/${payload.size}")
      .setHeader("Content-Length", last - first + 1)
      .setHeader("ETag", etag)
      .setBody(Buffer().write(payload, first, last - first + 1))

  private fun sha256(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256")
      .digest(bytes)
      .joinToString("") { byte -> "%02x".format(byte) }
}
