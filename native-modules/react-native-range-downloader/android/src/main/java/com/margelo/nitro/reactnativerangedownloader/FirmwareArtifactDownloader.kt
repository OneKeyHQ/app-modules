package com.margelo.nitro.reactnativerangedownloader

import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.net.ssl.SSLException

internal class FirmwareArtifactTransferException(
  val code: String,
  val retryable: Boolean,
  val discardStaging: Boolean,
  message: String,
  cause: Throwable? = null,
) : IOException(message, cause)

internal fun classifyFirmwareArtifactTransportFailure(
  error: IOException,
): FirmwareArtifactTransferException {
  var current: Throwable? = error
  while (current != null) {
    if (current is SSLException) {
      return FirmwareArtifactTransferException(
        code = "ARTIFACT_TLS_FAILED",
        retryable = false,
        discardStaging = false,
        message = "Firmware TLS or certificate verification failed",
        cause = error,
      )
    }
    current = current.cause
  }
  return FirmwareArtifactTransferException(
    code = "ARTIFACT_NETWORK_FAILED",
    retryable = true,
    discardStaging = false,
    message = "Firmware network request failed",
    cause = error,
  )
}

internal fun firmwareArtifactStorageFailure(
  message: String,
  error: IOException,
): FirmwareArtifactTransferException =
  FirmwareArtifactTransferException(
    code = "ARTIFACT_STORAGE_FAILED",
    retryable = false,
    discardStaging = false,
    message = message,
    cause = error,
  )

internal fun isRetryableFirmwareArtifactHttpStatus(statusCode: Int): Boolean =
  statusCode == 408 ||
    statusCode == 416 ||
    statusCode == 429 ||
    (statusCode in 500..599 && statusCode != 501 && statusCode != 505)

private class FirmwareArtifactReprobeException(
  val statusCode: Int,
  val discardPartial: Boolean,
) : IOException("Firmware range response requires a fresh object probe")

private class FirmwareArtifactAttachedResponse(
  val response: Response,
  private val call: Call,
  private val cancellation: FirmwareArtifactCancellation,
) : Closeable {
  val code: Int
    get() = response.code

  val body: ResponseBody?
    get() = response.body

  fun header(name: String): String? = response.header(name)

  override fun close() {
    try {
      response.close()
    } finally {
      cancellation.detach(call)
    }
  }
}

internal data class FirmwareArtifactValidatedRequest(
  val taskId: String,
  val leaseRef: String,
  val artifactId: String,
  val url: HttpUrl,
  val route: StoredArtifactRoute,
  val resolvedIp: String?,
  val expectedSize: Long,
  val expectedSha256: String,
  val maxBytes: Long,
  val segmentCount: Int,
  val deadlineAtMillis: Long = Long.MAX_VALUE,
  val deadlineAtNanos: Long = Long.MAX_VALUE,
)

internal class FirmwareArtifactDownloader(
  private val store: FirmwareArtifactStore,
  private val registry: FirmwareArtifactRegistry =
    FirmwareArtifactRegistry.processInstance,
  private val domainClient: OkHttpClient = OkHttpClient.Builder()
    .followRedirects(false)
    .followSslRedirects(false)
    .build(),
) {
  private data class Probe(
    val supportsRange: Boolean,
    val total: Long,
    val strongETag: String?,
    val lastModified: String?,
  )

  fun download(request: FirmwareArtifactValidatedRequest): StoredArtifactMetadata {
    val fingerprint = StoredArtifactMetadata.sha256(
      listOf(
        request.artifactId,
        request.url.toString(),
        request.route.wireName,
        request.expectedSize.toString(),
        request.expectedSha256,
        request.maxBytes.toString(),
      ).joinToString("\u0000")
    )
    return registry.runOrAttach(
      leaseRef = request.leaseRef,
      taskId = request.taskId,
      artifactId = request.artifactId,
      fingerprint = fingerprint,
      expectedSize = request.expectedSize,
    ) { cancellation, update ->
      performDownload(request, cancellation, update)
    }
  }

  fun status(leaseRef: String, taskId: String): FirmwareArtifactRegistrySnapshot {
    registry.status(leaseRef, taskId)?.let { return it }
    val metadata = store.findArtifactForTask(leaseRef, taskId)
      ?: return FirmwareArtifactRegistrySnapshot(
        state = FirmwareArtifactRegistryState.NOT_FOUND,
        downloadedBytes = 0,
        expectedSize = 0,
        errorCode = "ARTIFACT_NOT_FOUND",
        retryable = false,
      )
    val completed =
      metadata.actualSize == metadata.expectedSize &&
        metadata.immutableToken != null &&
        store.finalFile(metadata.artifactRef).isFile
    return FirmwareArtifactRegistrySnapshot(
      state = if (completed) {
        FirmwareArtifactRegistryState.COMPLETED
      } else {
        FirmwareArtifactRegistryState.FAILED
      },
      downloadedBytes = store.downloadedBytes(metadata.artifactRef),
      expectedSize = metadata.expectedSize,
      artifact = if (completed) metadata else null,
      errorCode = if (completed) null else "ARTIFACT_RESUME_REQUIRED",
      errorMessage = if (completed) null else {
        "Firmware artifact is durable but has no active worker"
      },
      retryable = if (completed) null else true,
    )
  }

  fun cancel(leaseRef: String, taskId: String) {
    registry.cancel(leaseRef, taskId)
  }

  private fun performDownload(
    initialRequest: FirmwareArtifactValidatedRequest,
    cancellation: FirmwareArtifactCancellation,
    update: (FirmwareArtifactRegistryState, Long) -> Unit,
  ): StoredArtifactMetadata {
    val attempt = store.beginTransferAttempt(
      leaseRef = initialRequest.leaseRef,
      taskId = initialRequest.taskId,
      artifactId = initialRequest.artifactId,
      canonicalUrl = initialRequest.url.toString(),
      hostname = initialRequest.url.host,
      route = initialRequest.route,
      expectedSize = initialRequest.expectedSize,
      expectedSha256 = initialRequest.expectedSha256,
      initialDeadlineAtMillis = initialRequest.deadlineAtMillis,
      maxRunAttempts = MAX_RUN_ATTEMPTS,
    )
    val request = initialRequest.withPersistedDeadline(
      requireNotNull(attempt.transferDeadlineAtMillis)
    )
    checkCancelled(request, cancellation)
    val client = when (request.route) {
      StoredArtifactRoute.DOMAIN -> domainClient
      StoredArtifactRoute.PINNED_IP -> FirmwarePinnedTransport.createClient(
        request.url.host,
        requireNotNull(request.resolvedIp),
      )
    }
    var prefetchedProbe: Probe? = null
    var reprobeCount = 0
    while (true) {
      checkCancelled(request, cancellation)
      val probe = prefetchedProbe ?: probe(request, client, cancellation)
      prefetchedProbe = null
      if (probe.total != request.expectedSize) {
        throw FirmwareArtifactTransferException(
          code = "ARTIFACT_SIZE_MISMATCH",
          retryable = false,
          discardStaging = true,
          message = "Probe total does not match the expected artifact size",
        )
      }
      val ranges = if (probe.supportsRange) {
        planRanges(request.expectedSize, request.segmentCount)
      } else {
        listOf(0L until request.expectedSize)
      }
      val metadata = store.prepareArtifactForDownload(
        artifactRef = attempt.artifactRef,
        leaseRef = request.leaseRef,
        taskId = request.taskId,
        artifactId = request.artifactId,
        canonicalUrl = request.url.toString(),
        hostname = request.url.host,
        route = request.route,
        expectedSize = request.expectedSize,
        expectedSha256 = request.expectedSha256,
        strongETag = probe.strongETag,
        lastModified = probe.lastModified,
        ranges = ranges,
      )
      if (metadata.actualSize == request.expectedSize &&
        metadata.actualSha256?.let {
          RangeDownloadLogic.secureHexEquals(it, request.expectedSha256)
        } == true &&
        metadata.immutableToken != null &&
        store.finalFile(metadata.artifactRef).isFile
      ) {
        return metadata
      }

      val activity = store.beginActivity(
        metadata.artifactRef,
        ArtifactActivityKind.WORKER,
        cancellation::cancelAndWait,
      )
      try {
        val initialBytes = store.downloadedBytes(metadata.artifactRef)
        update(FirmwareArtifactRegistryState.DOWNLOADING, initialBytes)
        if (probe.supportsRange) {
          downloadRanges(
            request = request,
            metadata = metadata,
            probe = probe,
            client = client,
            cancellation = cancellation,
            update = update,
          )
          assembleSegments(request, metadata, cancellation)
        } else {
          store.clearTransferStaging(metadata.artifactRef)
          streamWholeArtifact(
            request = request,
            metadata = metadata,
            client = client,
            cancellation = cancellation,
            update = update,
          )
        }
        checkCancelled(request, cancellation)
        update(FirmwareArtifactRegistryState.VERIFYING, request.expectedSize)
        val promoted = store.promoteVerifiedStaging(
          artifactRef = metadata.artifactRef,
          maxBytes = request.maxBytes,
        )
        store.completeTransferCleanup(metadata.artifactRef)
        return promoted
      } catch (error: FirmwareArtifactReprobeException) {
        reprobeCount += 1
        if (reprobeCount > MAX_OBJECT_REPROBES) {
          if (error.discardPartial) {
            store.clearTransferStaging(metadata.artifactRef)
          }
          throw exhaustedReprobe(error)
        }
        val refreshedProbe = probe(request, client, cancellation)
        if (error.discardPartial ||
          !sameObjectIdentity(probe, refreshedProbe)
        ) {
          store.clearTransferStaging(metadata.artifactRef)
        }
        prefetchedProbe = refreshedProbe
      } catch (error: FirmwareArtifactTransferException) {
        if (error.discardStaging) {
          store.clearTransferStaging(metadata.artifactRef)
        }
        throw error
      } finally {
        activity.close()
      }
    }
  }

  private fun probe(
    request: FirmwareArtifactValidatedRequest,
    client: OkHttpClient,
    cancellation: FirmwareArtifactCancellation,
  ): Probe {
    val httpRequest = Request.Builder()
      .url(request.url)
      .header("Accept-Encoding", "identity")
      .header("Range", "bytes=0-0")
      .get()
      .build()
    execute(request, client, httpRequest, cancellation).use { response ->
      return when (response.code) {
        206 -> {
          rejectMultipart(response)
          val range = parseContentRange(response.header("Content-Range"))
            ?: protocolFailure("Probe returned an invalid Content-Range")
          if (range.first != 0L || range.last != 0L) {
            protocolFailure("Probe returned a mismatched Content-Range")
          }
          consumeProbeByte(response.body)
          Probe(
            supportsRange = true,
            total = range.total,
            strongETag = StoredArtifactMetadata.normalizeStrongETag(
              response.header("ETag")
            ),
            lastModified = response.header("Last-Modified"),
          )
        }
        200 -> {
          val total = response.body?.contentLength()
            ?.takeIf { it >= 0 }
            ?: response.header("Content-Length")?.toLongOrNull()
            ?: protocolFailure("Non-range probe omitted Content-Length")
          Probe(
            supportsRange = false,
            total = total,
            strongETag = StoredArtifactMetadata.normalizeStrongETag(
              response.header("ETag")
            ),
            lastModified = response.header("Last-Modified"),
          )
        }
        else -> throw statusFailure(response.code)
      }
    }
  }

  private fun downloadRanges(
    request: FirmwareArtifactValidatedRequest,
    metadata: StoredArtifactMetadata,
    probe: Probe,
    client: OkHttpClient,
    cancellation: FirmwareArtifactCancellation,
    update: (FirmwareArtifactRegistryState, Long) -> Unit,
  ) {
    val transferred = AtomicLong(store.downloadedBytes(metadata.artifactRef))
    val ranges = metadata.segments.sortedBy(StoredArtifactSegment::index)
    val pool = Executors.newFixedThreadPool(minOf(ranges.size, MAX_PARALLEL_SEGMENTS))
    cancellation.attach(pool)
    try {
      val futures = ranges.map { segment ->
        pool.submit {
          val file = store.segmentFile(metadata.artifactRef, segment.index)
          val length = segment.end - segment.start + 1
          if (file.length() > length) {
            file.delete()
          }
          if (file.length() == length) {
            return@submit
          }
          downloadSegment(
            request = request,
            probe = probe,
            client = client,
            segment = segment,
            file = file,
            cancellation = cancellation,
          ) { delta ->
            val total = transferred.addAndGet(delta)
            if (total > request.maxBytes) {
              cancellation.cancelAndWait()
              throw FirmwareArtifactTransferException(
                code = "ARTIFACT_SIZE_LIMIT_EXCEEDED",
                retryable = false,
                discardStaging = true,
                message = "Downloaded bytes exceeded maxBytes",
              )
            }
            update(FirmwareArtifactRegistryState.DOWNLOADING, total)
          }
        }
      }
      futures.forEach { future ->
        try {
          future.get()
        } catch (error: ExecutionException) {
          throw (error.cause as? Exception ?: error)
        }
      }
    } finally {
      pool.shutdownNow()
      try {
        if (!pool.awaitTermination(WORKER_STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
          throw FirmwareArtifactTransferException(
            code = "ARTIFACT_WORKER_STOP_TIMEOUT",
            retryable = true,
            discardStaging = false,
            message = "Firmware segment workers did not stop before cleanup",
          )
        }
      } catch (_: InterruptedException) {
        Thread.currentThread().interrupt()
        checkCancelled(request, cancellation)
        throw FirmwareArtifactTransferException(
          code = "ARTIFACT_WORKER_STOP_TIMEOUT",
          retryable = true,
          discardStaging = false,
          message = "Interrupted while waiting for firmware segment workers",
        )
      }
    }
  }

  private fun downloadSegment(
    request: FirmwareArtifactValidatedRequest,
    probe: Probe,
    client: OkHttpClient,
    segment: StoredArtifactSegment,
    file: File,
    cancellation: FirmwareArtifactCancellation,
    onBytes: (Long) -> Unit,
  ) {
    var attempt = 0
    while (true) {
      checkCancelled(request, cancellation)
      val have = file.length()
      val expectedLength = segment.end - segment.start + 1
      if (have == expectedLength) return
      val start = segment.start + have
      val requestBuilder = Request.Builder()
        .url(request.url)
        .header("Accept-Encoding", "identity")
        .header("Range", "bytes=$start-${segment.end}")
        .get()
      (probe.strongETag ?: probe.lastModified)?.let { validator ->
        requestBuilder.header("If-Range", validator)
      }
      try {
        execute(
          request,
          client,
          requestBuilder.build(),
          cancellation,
        ).use { response ->
          if (response.code == 200 || response.code == 416) {
            throw FirmwareArtifactReprobeException(
              statusCode = response.code,
              discardPartial = response.code == 200,
            )
          }
          if (response.code != 206) {
            throw statusFailure(response.code)
          }
          rejectMultipart(response)
          val actualRange = parseContentRange(response.header("Content-Range"))
            ?: protocolFailure("Segment returned an invalid Content-Range")
          if (actualRange.first != start ||
            actualRange.last != segment.end ||
            actualRange.total != request.expectedSize
          ) {
            protocolFailure("Segment returned a mismatched Content-Range")
          }
          streamBody(
            body = response.body ?: protocolFailure("Segment response had no body"),
            destination = file,
            append = true,
            maxBytes = expectedLength,
            request = request,
            cancellation = cancellation,
            onBytes = onBytes,
          )
        }
        if (file.length() != expectedLength) {
          throw FirmwareArtifactTransferException(
            code = "ARTIFACT_SHORT_BODY",
            retryable = true,
            discardStaging = false,
            message = "Segment body ended before the requested range",
          )
        }
        return
      } catch (error: FirmwareArtifactTransferException) {
        if (!error.retryable || error.discardStaging || attempt >= MAX_SEGMENT_RETRIES) {
          throw error
        }
        attempt += 1
        sleepForRetry(request, attempt, cancellation)
      } catch (error: FirmwareArtifactReprobeException) {
        throw error
      } catch (error: IOException) {
        if (attempt >= MAX_SEGMENT_RETRIES) {
          throw FirmwareArtifactTransferException(
            code = "ARTIFACT_NETWORK_FAILED",
            retryable = true,
            discardStaging = false,
            message = "Firmware range request failed",
            cause = error,
          )
        }
        attempt += 1
        sleepForRetry(request, attempt, cancellation)
      }
    }
  }

  private fun assembleSegments(
    request: FirmwareArtifactValidatedRequest,
    metadata: StoredArtifactMetadata,
    cancellation: FirmwareArtifactCancellation,
  ) {
    val staging = store.partialFile(metadata.artifactRef)
    FileOutputStream(staging, false).use { output ->
      val buffer = ByteArray(STREAM_BUFFER_BYTES)
      var written = 0L
      metadata.segments.sortedBy(StoredArtifactSegment::index).forEach { segment ->
        val segmentFile = store.segmentFile(metadata.artifactRef, segment.index)
        FileInputStream(segmentFile).use { input ->
          while (true) {
            checkCancelled(request, cancellation)
            val read = input.read(buffer)
            if (read < 0) break
            written += read
            if (written > request.maxBytes || written > request.expectedSize) {
              throw FirmwareArtifactTransferException(
                code = "ARTIFACT_SIZE_LIMIT_EXCEEDED",
                retryable = false,
                discardStaging = true,
                message = "Assembled bytes exceeded the declared limit",
              )
            }
            output.write(buffer, 0, read)
          }
        }
      }
      output.fd.sync()
      if (written != request.expectedSize) {
        throw FirmwareArtifactTransferException(
          code = "ARTIFACT_SIZE_MISMATCH",
          retryable = true,
          discardStaging = false,
          message = "Assembled artifact size did not match expectedSize",
        )
      }
    }
  }

  private fun streamWholeArtifact(
    request: FirmwareArtifactValidatedRequest,
    metadata: StoredArtifactMetadata,
    client: OkHttpClient,
    cancellation: FirmwareArtifactCancellation,
    update: (FirmwareArtifactRegistryState, Long) -> Unit,
  ) {
    val httpRequest = Request.Builder()
      .url(request.url)
      .header("Accept-Encoding", "identity")
      .get()
      .build()
    execute(request, client, httpRequest, cancellation).use { response ->
      if (response.code != 200) {
        throw statusFailure(response.code)
      }
      val transferred = AtomicLong(0)
      streamBody(
        body = response.body ?: protocolFailure("Artifact response had no body"),
        destination = store.partialFile(metadata.artifactRef),
        append = false,
        maxBytes = request.maxBytes,
        request = request,
        cancellation = cancellation,
        onBytes = { delta ->
          update(
            FirmwareArtifactRegistryState.DOWNLOADING,
            transferred.addAndGet(delta),
          )
        },
      )
    }
  }

  private fun streamBody(
    body: ResponseBody,
    destination: File,
    append: Boolean,
    maxBytes: Long,
    request: FirmwareArtifactValidatedRequest,
    cancellation: FirmwareArtifactCancellation,
    onBytes: (Long) -> Unit,
  ) {
    var written = if (append) destination.length() else 0L
    val output = try {
      FileOutputStream(destination, append)
    } catch (error: IOException) {
      throw firmwareArtifactStorageFailure(
        "Could not open firmware staging file",
        error,
      )
    }
    val input = body.source().inputStream()
    var primaryFailure: Throwable? = null
    try {
      try {
        val buffer = ByteArray(STREAM_BUFFER_BYTES)
        while (true) {
          checkCancelled(request, cancellation)
          val read = try {
            input.read(buffer)
          } catch (error: IOException) {
            throw classifyFirmwareArtifactTransportFailure(error)
          }
          if (read < 0) break
          written += read
          if (written > maxBytes) {
            throw FirmwareArtifactTransferException(
              code = "ARTIFACT_SIZE_LIMIT_EXCEEDED",
              retryable = false,
              discardStaging = true,
              message = "Response body exceeded the declared byte limit",
            )
          }
          try {
            output.write(buffer, 0, read)
          } catch (error: IOException) {
            throw firmwareArtifactStorageFailure(
              "Could not write firmware staging file",
              error,
            )
          }
          onBytes(read.toLong())
        }
        try {
          output.fd.sync()
        } catch (error: IOException) {
          throw firmwareArtifactStorageFailure(
            "Could not synchronize firmware staging file",
            error,
          )
        }
      } catch (error: Throwable) {
        primaryFailure = error
        throw error
      }
    } finally {
      var closeFailure: FirmwareArtifactTransferException? = null
      try {
        input.close()
      } catch (error: IOException) {
        closeFailure = classifyFirmwareArtifactTransportFailure(error)
      }
      try {
        output.close()
      } catch (error: IOException) {
        val storageFailure = firmwareArtifactStorageFailure(
          "Could not close firmware staging file",
          error,
        )
        if (closeFailure == null) {
          closeFailure = storageFailure
        } else {
          closeFailure.addSuppressed(storageFailure)
        }
      }
      if (primaryFailure != null && closeFailure != null) {
        primaryFailure.addSuppressed(closeFailure)
      } else if (closeFailure != null) {
        throw closeFailure
      }
    }
  }

  private fun execute(
    transferRequest: FirmwareArtifactValidatedRequest,
    client: OkHttpClient,
    initialRequest: Request,
    cancellation: FirmwareArtifactCancellation,
  ): FirmwareArtifactAttachedResponse {
    var request = initialRequest
    repeat(MAX_REDIRECTS + 1) { redirectCount ->
      checkCancelled(transferRequest, cancellation)
      val call = client.newCall(request)
      cancellation.attach(call)
      call.timeout().timeout(
        remainingDeadlineNanos(transferRequest),
        TimeUnit.NANOSECONDS,
      )
      val response = try {
        call.execute()
      } catch (error: IOException) {
        cancellation.detach(call)
        checkCancelled(transferRequest, cancellation)
        throw classifyFirmwareArtifactTransportFailure(error)
      }
      if (response.code !in 300..399) {
        return FirmwareArtifactAttachedResponse(
          response = response,
          call = call,
          cancellation = cancellation,
        )
      }
      val location = response.header("Location")
      val redirectedUrl = location?.let(request.url::resolve)
      response.close()
      cancellation.detach(call)
      if (redirectCount >= MAX_REDIRECTS ||
        redirectedUrl == null ||
        !redirectedUrl.isHttps ||
        redirectedUrl.port != 443 ||
        !redirectedUrl.host.equals(
          transferRequest.url.host,
          ignoreCase = true,
        )
      ) {
        throw FirmwareArtifactTransferException(
          code = "ARTIFACT_REDIRECT_REJECTED",
          retryable = false,
          discardStaging = true,
          message = "Firmware redirect changed the canonical HTTPS origin",
        )
      }
      request = request.newBuilder().url(redirectedUrl).build()
    }
    error("unreachable")
  }

  private fun consumeProbeByte(body: ResponseBody?) {
    val source = body?.source() ?: protocolFailure("Probe response had no body")
    val buffer = okio.Buffer()
    val firstRead = source.read(buffer, 2)
    if (firstRead != 1L || source.read(buffer, 1) != -1L) {
      protocolFailure("Probe body length did not match bytes=0-0")
    }
  }

  private fun rejectMultipart(response: FirmwareArtifactAttachedResponse) {
    if (response.header("Content-Type")
        ?.lowercase()
        ?.startsWith("multipart/byteranges") == true
    ) {
      protocolFailure("Multipart range responses are not accepted")
    }
  }

  private data class ParsedContentRange(
    val first: Long,
    val last: Long,
    val total: Long,
  )

  private fun parseContentRange(value: String?): ParsedContentRange? {
    val match = value?.let {
      Regex("""^bytes\s+(\d+)-(\d+)/(\d+)$""").matchEntire(it.trim())
    } ?: return null
    val first = match.groupValues[1].toLongOrNull() ?: return null
    val last = match.groupValues[2].toLongOrNull() ?: return null
    val total = match.groupValues[3].toLongOrNull() ?: return null
    if (first > last || last >= total) return null
    return ParsedContentRange(first, last, total)
  }

  private fun planRanges(total: Long, segmentCount: Int): List<LongRange> {
    val chunk = (total + segmentCount - 1) / segmentCount
    return (0 until segmentCount).mapNotNull { index ->
      val start = index * chunk
      if (start >= total) {
        null
      } else {
        start..minOf(total - 1, start + chunk - 1)
      }
    }
  }

  private fun statusFailure(statusCode: Int): FirmwareArtifactTransferException {
    val retryable = isRetryableFirmwareArtifactHttpStatus(statusCode)
    return FirmwareArtifactTransferException(
      code = "ARTIFACT_HTTP_$statusCode",
      retryable = retryable,
      discardStaging = !retryable,
      message = "Firmware server returned HTTP $statusCode",
    )
  }

  private fun sameObjectIdentity(left: Probe, right: Probe): Boolean {
    return RangeDownloadLogic.sameArtifactObjectIdentity(
      leftSupportsRange = left.supportsRange,
      leftTotal = left.total,
      leftStrongETag = left.strongETag,
      leftLastModified = left.lastModified,
      rightSupportsRange = right.supportsRange,
      rightTotal = right.total,
      rightStrongETag = right.strongETag,
      rightLastModified = right.lastModified,
    )
  }

  private fun exhaustedReprobe(
    error: FirmwareArtifactReprobeException,
  ): FirmwareArtifactTransferException =
    if (error.statusCode == 416) {
      statusFailure(416)
    } else {
      FirmwareArtifactTransferException(
        code = "ARTIFACT_OBJECT_CHANGED",
        retryable = true,
        discardStaging = true,
        message = "Range response repeatedly changed object semantics",
      )
    }

  private fun protocolFailure(message: String): Nothing {
    throw FirmwareArtifactTransferException(
      code = "ARTIFACT_PROTOCOL_INVALID",
      retryable = false,
      discardStaging = true,
      message = message,
    )
  }

  private fun remainingDeadlineNanos(
    request: FirmwareArtifactValidatedRequest,
  ): Long =
    (request.deadlineAtNanos - System.nanoTime()).coerceAtLeast(1)

  private fun FirmwareArtifactValidatedRequest.withPersistedDeadline(
    persistedDeadlineAtMillis: Long,
  ): FirmwareArtifactValidatedRequest {
    val remainingMillis = persistedDeadlineAtMillis - System.currentTimeMillis()
    if (remainingMillis <= 0) {
      throw FirmwareArtifactStoreException.deadlineExceeded()
    }
    val durationNanos = if (remainingMillis > Long.MAX_VALUE / NANOS_PER_MILLISECOND) {
      Long.MAX_VALUE
    } else {
      remainingMillis * NANOS_PER_MILLISECOND
    }
    val startedAtNanos = System.nanoTime()
    return copy(
      deadlineAtMillis = persistedDeadlineAtMillis,
      deadlineAtNanos = if (startedAtNanos > Long.MAX_VALUE - durationNanos) {
        Long.MAX_VALUE
      } else {
        startedAtNanos + durationNanos
      },
    )
  }

  private fun checkCancelled(
    request: FirmwareArtifactValidatedRequest,
    cancellation: FirmwareArtifactCancellation,
  ) {
    if (cancellation.cancelled.get() || Thread.currentThread().isInterrupted) {
      throw FirmwareArtifactTransferException(
        code = "ARTIFACT_CANCELLED",
        retryable = true,
        discardStaging = false,
        message = "Firmware download was cancelled",
      )
    }
    if (request.deadlineAtNanos - System.nanoTime() <= 0) {
      throw FirmwareArtifactTransferException(
        code = "ARTIFACT_DEADLINE_EXCEEDED",
        retryable = false,
        discardStaging = false,
        message = "Firmware download exceeded its overall deadline",
      )
    }
  }

  private fun sleepForRetry(
    request: FirmwareArtifactValidatedRequest,
    attempt: Int,
    cancellation: FirmwareArtifactCancellation,
  ) {
    var remaining = (250L shl (attempt - 1).coerceAtMost(4)).coerceAtMost(4_000L)
    while (remaining > 0) {
      checkCancelled(request, cancellation)
      val slice = minOf(remaining, 100L)
      try {
        Thread.sleep(slice)
      } catch (_: InterruptedException) {
        Thread.currentThread().interrupt()
        checkCancelled(request, cancellation)
      }
      remaining -= slice
    }
  }

  companion object {
    private const val STREAM_BUFFER_BYTES = 64 * 1024
    private const val MAX_PARALLEL_SEGMENTS = 8
    private const val MAX_SEGMENT_RETRIES = 3
    private const val MAX_OBJECT_REPROBES = 2
    private const val MAX_RUN_ATTEMPTS = 8
    private const val MAX_REDIRECTS = 5
    private const val WORKER_STOP_TIMEOUT_SECONDS = 5L
    private const val NANOS_PER_MILLISECOND = 1_000_000L
    private const val MAXIMUM_ARTIFACT_BYTES = 512L * 1024L * 1024L

    fun validate(params: FirmwareArtifactDownloadParams): FirmwareArtifactValidatedRequest {
      val url = params.url.toHttpUrlOrNull()
        ?: throw FirmwareArtifactStoreException.invalidRequest()
      if (!url.isHttps ||
        url.port != 443 ||
        url.username.isNotEmpty() ||
        url.password.isNotEmpty()
      ) {
        throw FirmwareArtifactStoreException.invalidRequest()
      }
      val route = StoredArtifactRoute.fromWireName(params.routeType)
      when (route) {
        StoredArtifactRoute.DOMAIN -> {
          if (params.resolvedIp != null) {
            throw FirmwareArtifactStoreException.invalidRequest()
          }
        }
        StoredArtifactRoute.PINNED_IP -> {
          if (params.resolvedIp.isNullOrBlank() ||
            !FirmwarePinnedRouteValidation.isValid(
              url.host,
              params.resolvedIp,
            )
          ) {
            throw FirmwareArtifactStoreException.invalidRequest()
          }
        }
      }
      val expectedSize = exactPositiveLong(params.expectedSize)
      val maxBytes = exactPositiveLong(params.maxBytes)
      if (maxBytes < expectedSize ||
        expectedSize > MAXIMUM_ARTIFACT_BYTES ||
        maxBytes > MAXIMUM_ARTIFACT_BYTES ||
        !StoredArtifactMetadata.isTrustedSha256(params.expectedSha256) ||
        params.taskId.isBlank() ||
        params.artifactId.isBlank() ||
        params.artifactId.toByteArray(Charsets.UTF_8).size > 256
      ) {
        throw FirmwareArtifactStoreException.invalidRequest()
      }
      val segmentCount = params.segmentCount?.let { value ->
        val exact = exactPositiveLong(value)
        if (exact !in 1L..32L) {
          throw FirmwareArtifactStoreException.invalidRequest()
        }
        exact.toInt()
      } ?: DEFAULT_SEGMENT_COUNT
      val deadlineSeconds =
        params.overallDeadlineSeconds ?: DEFAULT_DEADLINE_SECONDS
      if (!deadlineSeconds.isFinite() ||
        deadlineSeconds <= 0 ||
        deadlineSeconds > MAXIMUM_DEADLINE_SECONDS
      ) {
        throw FirmwareArtifactStoreException.invalidRequest()
      }
      val durationNanos = (deadlineSeconds * NANOS_PER_SECOND).toLong()
      val startedAtNanos = System.nanoTime()
      val deadlineAtNanos =
        if (startedAtNanos > Long.MAX_VALUE - durationNanos) {
          Long.MAX_VALUE
        } else {
          startedAtNanos + durationNanos
        }
      val durationMillis = (deadlineSeconds * MILLIS_PER_SECOND).toLong()
      val startedAtMillis = System.currentTimeMillis()
      val deadlineAtMillis =
        if (startedAtMillis > Long.MAX_VALUE - durationMillis) {
          Long.MAX_VALUE
        } else {
          startedAtMillis + durationMillis
        }
      return FirmwareArtifactValidatedRequest(
        taskId = params.taskId,
        leaseRef = params.leaseRef,
        artifactId = params.artifactId,
        url = url,
        route = route,
        resolvedIp = params.resolvedIp,
        expectedSize = expectedSize,
        expectedSha256 = params.expectedSha256.lowercase(),
        maxBytes = maxBytes,
        segmentCount = segmentCount,
        deadlineAtMillis = deadlineAtMillis,
        deadlineAtNanos = deadlineAtNanos,
      )
    }

    private fun exactPositiveLong(value: Double): Long {
      if (!value.isFinite() ||
        value <= 0 ||
        value > MAX_SAFE_INTEGER.toDouble() ||
        value % 1.0 != 0.0
      ) {
        throw FirmwareArtifactStoreException.invalidRequest()
      }
      return value.toLong()
    }

    private const val DEFAULT_SEGMENT_COUNT = 8
    private const val DEFAULT_DEADLINE_SECONDS = 30.0 * 60.0
    private const val MAXIMUM_DEADLINE_SECONDS = 24.0 * 60.0 * 60.0
    private const val NANOS_PER_SECOND = 1_000_000_000.0
    private const val MILLIS_PER_SECOND = 1_000.0
    private const val MAX_SAFE_INTEGER = 9_007_199_254_740_991L
  }
}
