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
 * and how big" plus "how far the `.partial` concat already committed": a
 * full-sized segment is kept; a short one resumes from its current length via
 * `Range`; a segment whose extent is already inside the committed `.partial`
 * prefix is done even if its `.segN` was deleted mid-concat. Object identity is
 * intentionally NOT pinned (no ETag/If-Range) — resume is unconditional, and a
 * mid-flight object swap that slips through is caught by the caller's whole-file
 * SHA256 + GPG verify after promotion, which then drives a clean full
 * re-download (concat deletes every `.segN` on success). That verify is the sole
 * correctness backstop. The only validator-free safety nets kept inline are: a
 * 200 to a Range request → FallbackException, an over-long/oversized segment →
 * discard, and a per-segment Content-Range bounds check against mis-aligned 206s.
 *
 * This class is intentionally free of Android/OneKey dependencies (logging is
 * injected) so it can be unit/type-checked standalone.
 */
class ConcurrentRangeDownloader(
    private val httpClient: OkHttpClient,
    private val segmentCount: Int = 8,
    private val minConcurrentBytes: Long = 2L * 1024 * 1024,
    private val maxPartRetry: Int = 3,
    // Per-segment retry backoff (OCDS §5.4). Doubles each attempt up to a cap,
    // with full jitter so N segments do not retry in lockstep. A server
    // `Retry-After` overrides these. Caller-tunable; defaults are config, not
    // part of the standard.
    private val retryBaseDelayMillis: Long = 500L,
    private val retryMaxDelayMillis: Long = 8_000L,
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
     * Thrown for an HTTP status that is permanently unrecoverable for this URL
     * (auth/expired-signed-URL `401`/`403`, gone `404`/`410`, `501`/`505`, and
     * any other non-retryable 4xx per OCDS §4's catch-all). Unlike a generic
     * transient [java.io.IOException], this MUST bypass the per-segment retry
     * loop ([downloadSegment]) — retrying a dead URL only wastes the attempt
     * budget. The `HTTP <code>` message shape is preserved so the JS error
     * taxonomy ([updateErrorTaxonomy.ts]) maps it to `HTTP_<code>`.
     */
    private class PermanentHttpException(val code: Int) :
        Exception("HTTP $code")

    /**
     * Typed transient carrying an optional `Retry-After` delay (milliseconds)
     * the server asked us to wait before retrying (`429`/`503`). The retry loop
     * in [downloadSegment] prefers this over its computed backoff. Keeps the
     * `HTTP <code>` message shape for the JS taxonomy.
     */
    private class TransientHttpException(val code: Int, val retryAfterMillis: Long?) :
        java.io.IOException("HTTP $code")

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

        /**
         * Flip the abort flag and stop the worker pool, then wait (bounded) for
         * in-flight workers to actually terminate. `shutdownNow()` only
         * *interrupts*; a worker blocked in a native `write()` may run a moment
         * longer, so without this wait the caller's subsequent `.segN` delete
         * could be resurrected by that straggler — the exact race §5.8 forbids.
         * Idempotent.
         */
        fun cancel() {
            aborted.set(true)
            pool?.let { p ->
                p.shutdownNow()
                try {
                    p.awaitTermination(AWAIT_TERMINATION_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
                } catch (e: InterruptedException) {
                    // Preserve the interrupt status; the bounded wait is best-effort.
                    Thread.currentThread().interrupt()
                }
            }
        }

        private companion object {
            /** Bounded wait for worker termination on cancel (cancel-then-delete). */
            const val AWAIT_TERMINATION_SECONDS = 3L
        }
    }

    private class Part(val index: Int, val start: Long, val end: Long) {
        val length: Long get() = end - start + 1
    }

    private class Probe(val totalSize: Long, val supportsRange: Boolean)

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

        partialFile.parentFile?.let { if (!it.exists()) it.mkdirs() }

        val parts = planRanges(total)

        // Resume is unconditional: object identity is NOT pinned (no ETag) — any
        // mid-flight object swap that survives this far is caught by the caller's
        // whole-file SHA256/GPG verify, which then drives a clean full re-download
        // (concat deletes every `.segN` on success, so the retry starts fresh).
        // We therefore never wipe `.segN` for "no/changed validator" reasons.

        // Discard any leftover segment that can't belong to this plan (wrong
        // length = different object/range, or an index beyond the plan). This is
        // a pure size check, independent of any validator.
        for (i in 0 until segmentCount) {
            val f = segFile(i)
            if (!f.exists()) continue
            val expected = parts.getOrNull(i)?.length
            if (expected == null || f.length() > expected) {
                log("concurrent: discarding stale/oversized segment $i")
                f.delete()
            }
        }

        // `.partial`'s current length is the committed-concat cursor: every byte
        // below it has already been appended into `.partial` and its source
        // `.segN` may have been deleted by an interrupted concat. Such prefix
        // segments are DONE — they must not be re-fetched (that would waste the
        // network and break the "~1x + one segment" footprint target).
        val committedBytes = if (partialFile.exists()) partialFile.length() else 0L
        // A segment is "committed" when `.partial` already covers its full extent.
        val isCommitted: (Part) -> Boolean = { committedBytes >= it.start + it.length }

        // Progress baseline: committed bytes already in `.partial`, plus the
        // current length of every not-yet-committed segment file (committed
        // segments are already accounted for by `committedBytes`, so adding their
        // `.segN` length — if it still exists — would double-count).
        val transferred = AtomicLong(
            committedBytes + parts.filterNot(isCommitted).sumOf { segFile(it.index).length() }
        )
        onProgress(transferred.get(), total)

        // Share the abort flag with the cancel handle so an external cancel() is
        // observed by the per-segment loops; default to a private flag otherwise.
        val aborted = cancelHandle?.aborted ?: AtomicBoolean(false)
        val fallback = AtomicBoolean(false)
        val firstError = AtomicReference<Exception?>(null)

        // Only segments not yet fully on disk AND not already committed into
        // `.partial` need fetching. A committed prefix segment whose `.segN` was
        // deleted by an interrupted concat must NOT be treated as missing.
        val pending = parts.filterNot(isCommitted).filter { segFile(it.index).length() < it.length }
        if (pending.isNotEmpty()) {
            val pool = Executors.newFixedThreadPool(minOf(segmentCount, pending.size))
            cancelHandle?.attach(pool)
            try {
                val futures = pending.map { part ->
                    pool.submit {
                        try {
                            downloadSegment(url, segFile(part.index), part, total, aborted) { delta ->
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
            // Transient. Always keep the segment files so the next attempt
            // resumes — resume is unconditional now (no validator gate).
            throw err
        }
        // A committed prefix segment is complete even though its `.segN` is gone;
        // only not-yet-committed segments must have a full-length `.segN`.
        val incomplete = parts.filterNot(isCommitted)
            .firstOrNull { segFile(it.index).length() != it.length }
        if (incomplete != null) {
            // Keep the segment files for the next attempt to resume.
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
    // support and captures total size. Object identity is intentionally NOT
    // validated here (no ETag/If-Range) — the caller's whole-file SHA256 + GPG
    // verify after promotion is the sole correctness backstop, so resume is
    // always allowed. OkHttp follows redirects (the caller's client enforces
    // HTTPS on each hop).
    private fun probe(url: String): Probe? {
        return try {
            val req = Request.Builder().url(url).addHeader("Range", "bytes=0-0").build()
            httpClient.newCall(req).execute().use { response ->
                when (response.code) {
                    206 -> {
                        val total = response.header("Content-Range")
                            ?.let { Regex("""bytes \d+-\d+/(\d+)""").find(it)?.groupValues?.getOrNull(1)?.toLongOrNull() }
                        if (total != null) Probe(total, true) else Probe(0, false)
                    }
                    200 -> {
                        // Server ignored Range — single-stream only.
                        val len = response.body?.contentLength() ?: -1L
                        Probe(if (len > 0) len else 0, false)
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
            // Force the assembled `.partial` durable before the caller renames it
            // to the final path (OCDS §5.2: assemble → durable flush → atomic
            // rename). `out.flush()` above only pushes the JVM/libc buffers to the
            // kernel; without this fsync a power loss between the rename and the
            // kernel's writeback could promote a `.partial` whose tail bytes never
            // reached the platter, yielding a final file that fails SHA256.
            out.fd.sync()
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
        segFile: File,
        part: Part,
        total: Long,
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
                fetchSegment(url, segFile, part, total, rangeStart, aborted, onBytes)
                return
            } catch (e: FallbackException) {
                throw e
            } catch (e: PermanentHttpException) {
                // Dead URL (auth/gone/non-retryable) — retrying only burns the
                // attempt budget. Surface immediately; the caller maps the
                // `HTTP <code>` message to a permanent JS taxonomy bucket.
                throw e
            } catch (e: Exception) {
                if (aborted.get() || retry >= maxPartRetry) throw e
                retry += 1
                // Prefer the server's Retry-After; otherwise exponential backoff
                // with full jitter so the 8 segments do not retry in lockstep.
                val retryAfter = (e as? TransientHttpException)?.retryAfterMillis
                val delay = retryAfter ?: computeBackoffMillis(retry)
                log("concurrent: segment ${part.index} retry $retry in ${delay}ms: ${e.javaClass.simpleName}")
                sleepAbortable(delay, aborted)
            }
        }
    }

    // Exponential backoff (base * 2^(attempt-1), capped) with full jitter:
    // a uniformly random delay in [0, ceiling]. Full jitter is what actually
    // de-correlates the N segments — without it they would retry in lockstep.
    private fun computeBackoffMillis(attempt: Int): Long {
        val exp = retryBaseDelayMillis shl (attempt - 1).coerceIn(0, 16)
        val ceiling = exp.coerceAtMost(retryMaxDelayMillis).coerceAtLeast(1L)
        return (Math.random() * ceiling).toLong().coerceAtLeast(0L)
    }

    // Sleep in short slices so an external cancel() (which flips `aborted` and
    // shutdownNow()s the pool) is observed promptly instead of after a multi-
    // second backoff. Throws on abort so the loop bails immediately.
    private fun sleepAbortable(totalMillis: Long, aborted: AtomicBoolean) {
        var remaining = totalMillis
        while (remaining > 0) {
            if (aborted.get()) throw java.io.IOException("aborted")
            val slice = minOf(remaining, 100L)
            try {
                Thread.sleep(slice)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw java.io.IOException("aborted")
            }
            remaining -= slice
        }
    }

    private fun fetchSegment(
        url: String,
        segFile: File,
        part: Part,
        total: Long,
        rangeStart: Long,
        aborted: AtomicBoolean,
        onBytes: (delta: Long) -> Unit,
    ) {
        val request = Request.Builder().url(url)
            .addHeader("Range", "bytes=$rangeStart-${part.end}")
            .build()
        httpClient.newCall(request).execute().use { response ->
            // A 200 (full body) to a Range request is the one validator-free
            // safety net we keep: appending a from-zero body onto a partially
            // filled segment would corrupt it, so bail to the single-stream path.
            if (response.code == 200) {
                throw FallbackException("server returned 200 to a Range request")
            }
            if (response.code != 206) {
                // OCDS §4 classification. Anything that is not a usable 206 is
                // either a permanently-dead URL (bypass retries) or a transient
                // condition (retry with backoff). The `HTTP <code>` message shape
                // is preserved on both so the JS taxonomy maps it to HTTP_<code>.
                throw classifyHttpFailure(response)
            }
            // Reject a multipart/byteranges body: it carries range delimiters and
            // (potentially) more than the single window we asked for, so streaming
            // it raw into the segment file would splice in boundary bytes. This is
            // not a usable single-range 206 — fall back to single-stream.
            val contentType = response.header("Content-Type")?.lowercase()
            if (contentType != null && contentType.startsWith("multipart/byteranges")) {
                throw FallbackException("server returned multipart/byteranges to a single Range request")
            }
            // Verify the 206 covers exactly the slice we asked for. This guards
            // against a proxy/CDN returning a mis-aligned 206 (wrong window),
            // which would otherwise silently corrupt the assembled file. A
            // missing/mismatched Content-Range is treated as transient (retry).
            // It canNOT detect an object swapped behind an identical window —
            // that is the caller's whole-file SHA256/GPG verify's job.
            val contentRange = response.header("Content-Range")
                ?: throw java.io.IOException("206 without Content-Range")
            val bounds = parseContentRangeBounds(contentRange)
                ?: throw java.io.IOException("unparseable Content-Range: $contentRange")
            if (bounds.first != rangeStart || bounds.second != part.end) {
                throw java.io.IOException(
                    "Content-Range mismatch: got ${bounds.first}-${bounds.second}, " +
                        "expected $rangeStart-${part.end}"
                )
            }
            // The 206's `Content-Range` total (the `/<total>` tail) MUST agree
            // with the probe total. A disagreeing concrete total means the object
            // changed size behind us (different build) — that is permanent for
            // this window; an unknown `*` total cannot be reconciled either. Both
            // make concurrency unusable → fall back to single-stream.
            val parsedTotal = bounds.third
            if (parsedTotal == null || parsedTotal != total) {
                throw FallbackException(
                    "Content-Range total mismatch: got ${parsedTotal ?: "*"}, expected $total"
                )
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
        // A 206 can still over-deliver (server ignored our end bound). A segment
        // longer than its planned length is unusable — drop it so the next
        // attempt re-fetches cleanly rather than concatenating misaligned bytes.
        if (segFile.length() > part.length) {
            segFile.delete()
            throw java.io.IOException(
                "Segment ${part.index} overran (${segFile.length()}/${part.length})"
            )
        }
    }

    // Parse "bytes <start>-<end>/<total>" → (start, end, total). Returns null
    // when the header lacks concrete bounds (e.g. "bytes */1234") or is otherwise
    // unparseable. `total` is null when the total is the unknown `*` form
    // ("bytes <start>-<end>/*"), which the caller treats as a disagreeing total.
    private fun parseContentRangeBounds(value: String): Triple<Long, Long, Long?>? {
        val m = Regex("""bytes\s+(\d+)-(\d+)/(\d+|\*)""").find(value) ?: return null
        val start = m.groupValues[1].toLongOrNull() ?: return null
        val end = m.groupValues[2].toLongOrNull() ?: return null
        val total = m.groupValues[3].let { if (it == "*") null else it.toLongOrNull() }
        return Triple(start, end, total)
    }

    // OCDS §4 status classifier. Returns the exception to throw for a non-206,
    // non-200 status: a [PermanentHttpException] (bypasses the per-segment retry
    // loop) or a [TransientHttpException] (retried with backoff/Retry-After).
    //   - 401/403/404/410      → permanent (auth / expired signed URL / gone)
    //   - 408/429              → transient (timeout / throttling)
    //   - 416                  → transient (resume size re-evaluated by caller)
    //   - other 4xx            → permanent (catch-all default)
    //   - 501/505              → permanent
    //   - other 5xx            → transient (back off and retry)
    //   - anything else        → permanent (unknown → permanent, per §4)
    // The 206 (proceed) and 200 (fallback) cases are handled by the caller before
    // this is reached.
    private fun classifyHttpFailure(response: okhttp3.Response): Exception {
        val code = response.code
        val permanent = when (code) {
            401, 403, 404, 410 -> true
            408, 429 -> false
            // 416 (Range Not Satisfiable) is TRANSIENT, not permanent. It happens
            // when a resume request's `have` offset is at/past the object's end —
            // typically because the object changed size behind us. Treating it as
            // permanent (via the `in 400..499` catch-all below) would discard the
            // already-downloaded `.segN` artifacts and restart from byte 0, the
            // §4 "most damaging mistake". Instead keep the bytes; the per-segment
            // retry loop re-requests from the same offset, and the single-stream
            // 416 recovery in the caller re-evaluates total size. This case MUST
            // stay above `in 400..499` — `when` matches top-to-bottom and 416 is a
            // member of that range.
            416 -> false
            in 400..499 -> true
            501, 505 -> true
            in 500..599 -> false
            else -> true
        }
        if (permanent) return PermanentHttpException(code)
        return TransientHttpException(code, parseRetryAfterMillis(response.header("Retry-After")))
    }

    // Parse a `Retry-After` header into milliseconds. Only the delta-seconds form
    // is honored (the absolute HTTP-date form is rarely sent for 429/503 and not
    // worth a date parser here); an unparseable/absent value yields null so the
    // caller falls back to its computed backoff.
    private fun parseRetryAfterMillis(value: String?): Long? {
        val seconds = value?.trim()?.toLongOrNull() ?: return null
        if (seconds < 0) return null
        return (seconds * 1000L).coerceAtMost(retryMaxDelayMillis)
    }
}
