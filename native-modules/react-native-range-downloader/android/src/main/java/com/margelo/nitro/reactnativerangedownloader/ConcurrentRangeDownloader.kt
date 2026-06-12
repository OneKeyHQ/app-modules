package com.margelo.nitro.reactnativerangedownloader

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Splits a Range-capable download into [segmentCount] byte ranges fetched in
 * parallel, each STREAMED INTO ITS OWN sibling file `<partial>.segN` with plain
 * sequential `FileOutputStream` appends (O_WRONLY). Once every segment file is
 * fully present, the segments are concatenated in order into the `.partial`
 * (and freed as they are consumed, so the peak footprint stays ~1x the file
 * plus one segment). Mirrors the iOS RangeDownloader segment-file model.
 *
 * Why segment files instead of one pre-allocated `.partial` with positioned
 * writes: the previous design pre-allocated the full size up front via
 * `RandomAccessFile(partial, "rw").setLength(total)`. That O_RDWR open +
 * large reservation fails with EROFS/ENOSPC on near-full f2fs devices (and any
 * storage that rejects a large up-front reservation), aborting the WHOLE
 * download before a byte is fetched. This design only ever does plain O_WRONLY
 * sequential writes (segment fetch + concat) and O_RDONLY reads — the same I/O
 * shape as the caller's proven single-stream path — so it grows incrementally
 * up to the real space limit instead of reserving everything at once.
 *
 * Resume across kill/suspend is simply "which `<partial>.segN` already exist
 * and how big": a full-sized segment is kept; a short one resumes from its
 * current length via `Range` + `If-Range`; with no strong validator (ETag)
 * leftover segments can't be pinned to the server object and are wiped. The
 * caller's whole-file SHA256 + GPG verify after promotion remains the final
 * correctness backstop.
 *
 * This class is intentionally free of Android/OneKey dependencies (logging is
 * injected) so it can be unit/type-checked standalone.
 */
class ConcurrentRangeDownloader(
    private val httpClient: OkHttpClient,
    private val segmentCount: Int = 8,
    private val minConcurrentBytes: Long = 2L * 1024 * 1024,
    private val maxPartRetry: Int = 3,
    private val log: (String) -> Unit = {},
) {
    enum class Outcome {
        /** `.partial` is fully on disk; caller should promote (rename) + verify. */
        COMPLETED,

        /** Concurrency unusable — caller should use its single-stream path. */
        FALLBACK,
    }

    /** Thrown internally when a segment proves concurrency can't be used. */
    private class FallbackException(message: String) : Exception(message)

    /**
     * Cooperative-cancel handle the caller can register a download against. The
     * adapter keeps these in a per-taskId registry so `cancel`/`discardArtifacts`
     * can flip [aborted] and `shutdownNow()` the worker pool BEFORE deleting the
     * segment files, so no in-flight worker resurrects a deleted file.
     */
    class CancelHandle {
        val aborted = AtomicBoolean(false)

        @Volatile
        private var pool: java.util.concurrent.ExecutorService? = null

        internal fun attach(pool: java.util.concurrent.ExecutorService) {
            this.pool = pool
            // If cancel() already raced in before the pool was attached, honor it.
            if (aborted.get()) pool.shutdownNow()
        }

        /** Flip the abort flag and stop the worker pool. Idempotent. */
        fun cancel() {
            aborted.set(true)
            pool?.shutdownNow()
        }
    }

    private class Part(val index: Int, val start: Long, val end: Long) {
        val length: Long get() = end - start + 1
    }

    private class Probe(val totalSize: Long, val etag: String?, val supportsRange: Boolean)

    /**
     * Fills [partialFilePath] completely with the resource at [url] using
     * concurrent ranges. See [Outcome]. Throws on a transient/IO error after
     * per-segment retries, leaving the segment files in place so a later attempt
     * resumes.
     */
    fun download(
        url: String,
        partialFilePath: String,
        cancelHandle: CancelHandle? = null,
        onProgress: (transferred: Long, total: Long) -> Unit,
    ): Outcome {
        val partialFile = File(partialFilePath)
        val segFile: (Int) -> File = { index -> File("$partialFilePath.seg$index") }

        // A bare `.partial` with NO segment files is a single-stream leftover;
        // let the caller's single-stream path resume it instead of touching it.
        val anyLeftoverSeg = (0 until segmentCount).any { segFile(it).exists() }
        if (partialFile.exists() && !anyLeftoverSeg) {
            log("concurrent: single-stream partial present, deferring to single-stream")
            return Outcome.FALLBACK
        }

        val probe = probe(url) ?: return Outcome.FALLBACK
        if (!probe.supportsRange || probe.totalSize < minConcurrentBytes) {
            log("concurrent: not eligible (supportsRange=${probe.supportsRange}, size=${probe.totalSize})")
            return Outcome.FALLBACK
        }
        val total = probe.totalSize
        val etag = probe.etag
        // A strong validator (ETag) is what lets If-Range pin a resumed segment
        // to the exact object the segments were started against. Without it,
        // leftover segments are untrustworthy — start fresh.
        val hasValidator = !etag.isNullOrEmpty()

        partialFile.parentFile?.let { if (!it.exists()) it.mkdirs() }

        val parts = planRanges(total)

        if (!hasValidator) {
            wipeArtifacts(partialFile, segFile)
        }
        // Discard any leftover segment that can't belong to this plan (wrong
        // length = different object/range, or an index beyond the plan).
        for (i in 0 until segmentCount) {
            val f = segFile(i)
            if (!f.exists()) continue
            val expected = parts.getOrNull(i)?.length
            if (expected == null || f.length() > expected) {
                log("concurrent: discarding stale/oversized segment $i")
                f.delete()
            }
        }

        val transferred = AtomicLong(parts.sumOf { segFile(it.index).length() })
        onProgress(transferred.get(), total)

        // Share the abort flag with the cancel handle so an external cancel() is
        // observed by the per-segment loops; default to a private flag otherwise.
        val aborted = cancelHandle?.aborted ?: AtomicBoolean(false)
        val fallback = AtomicBoolean(false)
        val firstError = AtomicReference<Exception?>(null)

        // Only segments not yet fully on disk need fetching.
        val pending = parts.filter { segFile(it.index).length() < it.length }
        if (pending.isNotEmpty()) {
            val pool = Executors.newFixedThreadPool(minOf(segmentCount, pending.size))
            cancelHandle?.attach(pool)
            try {
                val futures = pending.map { part ->
                    pool.submit {
                        try {
                            downloadSegment(url, etag, segFile(part.index), part, aborted) { delta ->
                                onProgress(transferred.addAndGet(delta), total)
                            }
                        } catch (e: FallbackException) {
                            fallback.set(true)
                            aborted.set(true)
                            firstError.compareAndSet(null, e)
                        } catch (e: Exception) {
                            aborted.set(true)
                            firstError.compareAndSet(null, e)
                        }
                    }
                }
                futures.forEach { it.get() }
            } finally {
                pool.shutdownNow()
            }
        }

        if (fallback.get()) {
            // Stale/unusable bytes — clear before the caller falls back.
            wipeArtifacts(partialFile, segFile)
            return Outcome.FALLBACK
        }
        val err = firstError.get()
        if (err != null) {
            // Transient. Keep the segment files so the next attempt resumes when
            // we have a validator; otherwise they can't be trusted — wipe them.
            if (!hasValidator) wipeArtifacts(partialFile, segFile)
            throw err
        }
        val incomplete = parts.firstOrNull { segFile(it.index).length() != it.length }
        if (incomplete != null) {
            if (!hasValidator) wipeArtifacts(partialFile, segFile)
            throw java.io.IOException("Concurrent download incomplete (segment ${incomplete.index})")
        }

        // All segments complete → assemble the `.partial`.
        concatenate(partialFile, parts, segFile, total)
        log("concurrent: completed ($total bytes)")
        return Outcome.COMPLETED
    }

    private fun planRanges(total: Long): List<Part> {
        val parts = ArrayList<Part>()
        val chunk = (total + segmentCount - 1) / segmentCount
        var i = 0
        while (i < segmentCount) {
            val start = i * chunk
            if (start >= total) break
            val end = minOf(start + chunk - 1, total - 1)
            parts.add(Part(parts.size, start, end))
            i += 1
        }
        return parts
    }

    // Single round-trip probe: a one-byte Range request that confirms Range
    // support and captures total size + ETag. OkHttp follows redirects (the
    // caller's client enforces HTTPS on each hop).
    private fun probe(url: String): Probe? {
        return try {
            val req = Request.Builder().url(url).addHeader("Range", "bytes=0-0").build()
            httpClient.newCall(req).execute().use { response ->
                val etag = response.header("ETag")
                when (response.code) {
                    206 -> {
                        val total = response.header("Content-Range")
                            ?.let { Regex("""bytes \d+-\d+/(\d+)""").find(it)?.groupValues?.getOrNull(1)?.toLongOrNull() }
                        if (total != null) Probe(total, etag, true) else Probe(0, etag, false)
                    }
                    200 -> {
                        // Server ignored Range — single-stream only.
                        val len = response.body?.contentLength() ?: -1L
                        Probe(if (len > 0) len else 0, etag, false)
                    }
                    else -> null
                }
            }
        } catch (e: Exception) {
            log("concurrent: probe failed: ${e.javaClass.simpleName}")
            null
        }
    }

    private fun wipeArtifacts(partialFile: File, segFile: (Int) -> File) {
        partialFile.delete()
        for (i in 0 until segmentCount) segFile(i).delete()
    }

    // Concatenate the completed segment files, in order, into the `.partial`.
    // Append-mode + the `.partial`'s current length as the resume cursor make
    // this idempotent and crash-safe: an interrupted concat resumes where it
    // left off, and each segment is deleted only after it has been fully
    // appended, so the peak footprint stays ~1x the file plus one segment
    // (critical on near-full devices — a 2x "all segs + full copy" peak would
    // re-introduce the out-of-space failure this design exists to avoid).
    private fun concatenate(
        partialFile: File,
        parts: List<Part>,
        segFile: (Int) -> File,
        total: Long,
    ) {
        var written = if (partialFile.exists()) partialFile.length() else 0L
        if (written > total) {
            // Corrupt/over-long prior concat — restart clean.
            partialFile.delete()
            written = 0L
        }
        FileOutputStream(partialFile, /* append = */ true).use { out ->
            var cursor = 0L
            for (part in parts) {
                val segEndInFinal = cursor + part.length
                if (written < segEndInFinal) {
                    val seg = segFile(part.index)
                    // Skip the prefix of this segment that a prior interrupted
                    // concat already appended (append always writes at EOF).
                    val skip = (written - cursor).coerceAtLeast(0L)
                    FileInputStream(seg).use { input ->
                        var toSkip = skip
                        while (toSkip > 0) {
                            val s = input.skip(toSkip)
                            if (s <= 0) break
                            toSkip -= s
                        }
                        input.copyTo(out)
                    }
                    out.flush()
                    written = segEndInFinal
                }
                cursor = segEndInFinal
                segFile(part.index).delete()
            }
        }
        if (partialFile.length() != total) {
            partialFile.delete()
            throw java.io.IOException("Concat size mismatch (${partialFile.length()}/$total)")
        }
    }

    // Fetch [start+have, end] of [part] into its OWN segment file via plain
    // sequential O_WRONLY appends (no positioned writes, no pre-allocation),
    // resuming from the segment file's current length and retrying transient
    // failures in place.
    private fun downloadSegment(
        url: String,
        etag: String?,
        segFile: File,
        part: Part,
        aborted: AtomicBoolean,
        onBytes: (delta: Long) -> Unit,
    ) {
        var retry = 0
        while (true) {
            if (aborted.get()) throw java.io.IOException("aborted")
            val have = segFile.length()
            if (have >= part.length) return
            val rangeStart = part.start + have
            try {
                fetchSegment(url, etag, segFile, part, rangeStart, aborted, onBytes)
                return
            } catch (e: FallbackException) {
                throw e
            } catch (e: Exception) {
                if (aborted.get() || retry >= maxPartRetry) throw e
                retry += 1
                log("concurrent: segment ${part.index} retry $retry: ${e.javaClass.simpleName}")
            }
        }
    }

    private fun fetchSegment(
        url: String,
        etag: String?,
        segFile: File,
        part: Part,
        rangeStart: Long,
        aborted: AtomicBoolean,
        onBytes: (delta: Long) -> Unit,
    ) {
        val builder = Request.Builder().url(url)
            .addHeader("Range", "bytes=$rangeStart-${part.end}")
        // If-Range: a mismatched ETag makes the CDN reply 200 (full body)
        // instead of 206, which we treat as a fallback signal — appending a
        // from-zero body onto a partially-filled segment would corrupt it.
        if (etag != null) builder.addHeader("If-Range", etag)
        httpClient.newCall(builder.build()).execute().use { response ->
            if (response.code == 200) {
                throw FallbackException("server returned 200 to a Range request")
            }
            if (response.code != 206) {
                throw java.io.IOException("HTTP ${response.code}")
            }
            val body = response.body ?: throw java.io.IOException("Empty segment body")
            // Append the fetched tail to the segment file. Append mode keeps
            // resume correct: we only ever request the bytes not yet on disk.
            FileOutputStream(segFile, /* append = */ true).use { out ->
                body.byteStream().use { input ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        if (aborted.get()) throw java.io.IOException("aborted")
                        val read = input.read(buffer)
                        if (read == -1) break
                        out.write(buffer, 0, read)
                        onBytes(read.toLong())
                    }
                }
            }
        }
    }
}
