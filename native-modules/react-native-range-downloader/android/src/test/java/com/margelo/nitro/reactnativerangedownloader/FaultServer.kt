package com.margelo.nitro.reactnativerangedownloader

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import okio.Buffer
import java.io.File
import java.security.MessageDigest
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Reusable JVM test harness for [ConcurrentRangeDownloader] — the analogue of
 * the desktop e2e "fault server", implemented as an OkHttp [MockWebServer]
 * [Dispatcher] rather than a real HTTP server process.
 *
 * Threading: a custom [Dispatcher.dispatch] is invoked on MockWebServer's
 * internal per-connection reader threads. The core opens up to 8 sockets in
 * parallel (one per pending segment) plus the probe, so [dispatch] is called
 * CONCURRENTLY. Every piece of mutable state here (request log, fault-mode hit
 * counters) is therefore thread-safe (synchronized list / atomic counters).
 * MockWebServer serves each connection on its own thread, so 8 parallel range
 * requests are genuinely concurrent — there is no per-request serialization to
 * mask a concurrency bug in the core.
 */
class RangeFaultServer(private val content: ByteArray) {

  private val total: Long = content.size.toLong()
  private val etag: String = "\"" + sha256(content).substring(0, 16) + "\""

  /** Every [start,end] (inclusive) the server was asked to serve. Thread-safe. */
  val requestedRanges: MutableList<Pair<Long, Long>> =
    Collections.synchronizedList(ArrayList())

  /** Count of every request received (incl. probe + retries). Thread-safe. */
  private val faults = ConcurrentHashMap<FaultMode, Fault>()

  /**
   * A fault that targets exactly one byte range. [times] auto-expires: after it
   * has fired [times] times the matching request succeeds normally, so a
   * scenario can fail one segment N times then let the retry through.
   */
  private class Fault(
    val targetStart: Long,
    val targetEnd: Long,
    val times: Int,
  ) {
    var fired: Int = 0
  }

  enum class FaultMode {
    /** SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY — drop mid-body. */
    DROP_DURING_BODY,

    /** Ignore Range, return 200 + full body (core → FALLBACK). */
    STATUS_200,

    /** 429 with Retry-After: 0, then a normal 206 on the retry. */
    STATUS_429_THEN_OK,

    /** 5xx transient (retried in place). */
    STATUS_5XX,

    /** 416 Range Not Satisfiable (transient per OCDS §4). */
    STATUS_416,

    /** 206 with a Content-Range window that does NOT match the request. */
    MISALIGNED_CONTENT_RANGE,

    /** 206 whose body has MORE bytes than the requested range. */
    OVER_LONG_BODY,

    /** 206 whose body has FEWER bytes than the requested range. */
    SHORT_BODY,

    /** Content-Type: multipart/byteranges (core → FALLBACK). */
    MULTIPART_BYTERANGES,

    /** Throttle the body so the read stalls (NO_RESPONSE-like slow trickle). */
    STALL,
  }

  /**
   * Arm a fault on the request whose Range is exactly [start]-[end] (inclusive).
   * For segment-level faults pass the [Part] window from [planParts]. [times]
   * lets the fault auto-expire so the retried request succeeds.
   */
  fun arm(mode: FaultMode, start: Long, end: Long, times: Int = 1): RangeFaultServer {
    faults[mode] = Fault(start, end, times)
    return this
  }

  /** Default status code for the [STATUS_5XX] fault (overridable per-arm). */
  var status5xxCode: Int = 503

  val dispatcher: Dispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
      val range = parseRequestRange(request.getHeader("Range"))
        ?: return errorResponse(400) // tests always send a Range
      val (start, end) = range
      requestedRanges.add(start to end)

      // Find an armed-and-unexpired fault that targets this exact window.
      val firingMode = faults.entries.firstOrNull { (_, f) ->
        f.targetStart == start && f.targetEnd == end && f.fired < f.times
      }?.also { it.value.fired++ }?.key

      if (firingMode == null) {
        return normal206(start, end)
      }

      return when (firingMode) {
        FaultMode.DROP_DURING_BODY ->
          ok206Headers(start, end)
            .setBody(bufferFor(start, end))
            .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY)

        FaultMode.STATUS_200 ->
          MockResponse()
            .setResponseCode(200)
            .addHeader("ETag", etag)
            .addHeader("Content-Length", total.toString())
            .setBody(Buffer().write(content))

        FaultMode.STATUS_429_THEN_OK ->
          MockResponse()
            .setResponseCode(429)
            .addHeader("Retry-After", "0")

        FaultMode.STATUS_5XX ->
          MockResponse().setResponseCode(status5xxCode)

        FaultMode.STATUS_416 ->
          MockResponse()
            .setResponseCode(416)
            .addHeader("Content-Range", "bytes */$total")

        FaultMode.MISALIGNED_CONTENT_RANGE -> {
          // Serve the correct slice but advertise a shifted window.
          val badStart = start + 1
          val badEnd = end + 1
          MockResponse()
            .setResponseCode(206)
            .addHeader("ETag", etag)
            .addHeader("Content-Range", "bytes $badStart-$badEnd/$total")
            .setBody(bufferFor(start, end))
        }

        FaultMode.OVER_LONG_BODY -> {
          // Append one extra byte beyond the requested window.
          val extraEnd = minOf(end + 1, total - 1)
          ok206Headers(start, end).setBody(bufferFor(start, extraEnd))
        }

        FaultMode.SHORT_BODY -> {
          // One byte fewer than requested (read ends early; segment < length).
          val shortEnd = (end - 1).coerceAtLeast(start)
          ok206Headers(start, end).setBody(bufferFor(start, shortEnd))
        }

        FaultMode.MULTIPART_BYTERANGES ->
          ok206Headers(start, end)
            .removeHeader("Content-Type")
            .addHeader("Content-Type", "multipart/byteranges; boundary=THISISABOUNDARY")
            .setBody(bufferFor(start, end))

        FaultMode.STALL ->
          ok206Headers(start, end)
            .setBody(bufferFor(start, end))
            // Trickle 1 byte/sec so the read stalls well past any client read
            // timeout; the socket policy keeps the connection open meanwhile.
            .throttleBody(1, 1, TimeUnit.SECONDS)
      }
    }
  }

  private fun normal206(start: Long, end: Long): MockResponse =
    ok206Headers(start, end).setBody(bufferFor(start, end))

  private fun ok206Headers(start: Long, end: Long): MockResponse =
    MockResponse()
      .setResponseCode(206)
      .addHeader("ETag", etag)
      .addHeader("Content-Type", "application/octet-stream")
      .addHeader("Content-Range", "bytes $start-$end/$total")

  private fun errorResponse(code: Int): MockResponse =
    MockResponse().setResponseCode(code)

  /** Inclusive slice [start,end] of [content] as an okio Buffer. */
  private fun bufferFor(start: Long, end: Long): Buffer {
    val from = start.toInt()
    val to = (end + 1).coerceAtMost(total).toInt()
    return Buffer().write(content, from, to - from)
  }

  companion object {
    /** Parse a request `Range: bytes=start-end` header → inclusive (start,end). */
    fun parseRequestRange(header: String?): Pair<Long, Long>? {
      if (header == null) return null
      val m = Regex("""bytes=(\d+)-(\d+)""").find(header) ?: return null
      val start = m.groupValues[1].toLongOrNull() ?: return null
      val end = m.groupValues[2].toLongOrNull() ?: return null
      return start to end
    }
  }
}

// ---------------------------------------------------------------------------
// Shared helpers (mirror the core so tests assert against the same plan).
// ---------------------------------------------------------------------------

/** A planned byte range, mirroring [ConcurrentRangeDownloader]'s private Part. */
data class PlannedPart(val index: Int, val start: Long, val end: Long) {
  val length: Long get() = end - start + 1
}

/**
 * Mirror of the core's `planRanges`: ceil-chunk the total into [segments]
 * inclusive windows. Trailing empty parts are dropped exactly as the core does.
 */
fun planParts(total: Long, segments: Int = 8): List<PlannedPart> {
  val parts = ArrayList<PlannedPart>()
  val chunk = (total + segments - 1) / segments
  var i = 0
  while (i < segments) {
    val start = i * chunk
    if (start >= total) break
    val end = minOf(start + chunk - 1, total - 1)
    parts.add(PlannedPart(parts.size, start, end))
    i += 1
  }
  return parts
}

/** SHA-256 hex digest of a byte array. */
fun sha256(bytes: ByteArray): String =
  MessageDigest.getInstance("SHA-256").digest(bytes)
    .joinToString("") { "%02x".format(it) }

/** SHA-256 hex digest of a file's contents. */
fun sha256(file: File): String = sha256(file.readBytes())

/**
 * Pre-write the correct `.segN` files for parts 0..[completedThrough] (inclusive)
 * so a resume run re-fetches only the tail. Mirrors the core's segment-file
 * resume model: a full-length `.segN` is treated as done and skipped.
 */
fun seedSegments(
  partialFilePath: String,
  content: ByteArray,
  completedThrough: Int,
  segments: Int = 8,
) {
  val parts = planParts(content.size.toLong(), segments)
  for (part in parts) {
    if (part.index > completedThrough) continue
    val segFile = File("$partialFilePath.seg${part.index}")
    segFile.parentFile?.let { if (!it.exists()) it.mkdirs() }
    segFile.writeBytes(content.copyOfRange(part.start.toInt(), (part.end + 1).toInt()))
  }
}

/** A real [ConcurrentRangeDownloader] over a plain OkHttpClient (no app deps). */
fun newDownloader(segments: Int = 8): ConcurrentRangeDownloader =
  ConcurrentRangeDownloader(OkHttpClient(), segments)
