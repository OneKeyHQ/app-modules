package com.margelo.nitro.reactnativerangedownloader

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Splits a Range-capable download into [segmentCount] byte ranges fetched in
 * parallel, each written directly into its own offset of ONE pre-allocated
 * `.partial` file (no merge pass, 1x disk). A sidecar `<partial>.progress`
 * manifest records each segment's durably-written cursor so an interrupted
 * download resumes by re-requesting only the unfinished tail of each segment.
 *
 * Mirrors the desktop DesktopApiBundleUpdate concurrent path. This class is
 * intentionally free of Android/OneKey dependencies (logging is injected) so
 * it can be unit/type-checked standalone; the whole-file SHA256 check the
 * caller already performs after promotion is the final correctness backstop.
 *
 * Invariant: the manifest is only meaningful as metadata for an existing
 * `.partial`. Either both exist (resume) or neither does (fresh) — any other
 * combination is treated as "no resumable state".
 */
class ConcurrentRangeDownloader(
    private val httpClient: OkHttpClient,
    private val segmentCount: Int = 8,
    private val minConcurrentBytes: Long = 2L * 1024 * 1024,
    private val maxPartRetry: Int = 3,
    private val manifestFlushBytes: Long = 4L * 1024 * 1024,
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

    private class Part(val index: Int, val start: Long, val end: Long, @Volatile var done: Long) {
        val length: Long get() = end - start + 1
    }

    private class Probe(val totalSize: Long, val etag: String?, val supportsRange: Boolean)

    /**
     * Fills [partialFilePath] completely with the resource at [url] using
     * concurrent ranges. See [Outcome]. Throws on a transient/IO error after
     * per-segment retries, leaving the partial + manifest in place so a later
     * attempt resumes.
     */
    fun download(
        url: String,
        partialFilePath: String,
        onProgress: (transferred: Long, total: Long) -> Unit,
    ): Outcome {
        val partialFile = File(partialFilePath)
        val manifestFile = File("$partialFilePath.progress")

        // A bare `.partial` with no manifest is a single-stream leftover; let
        // the caller's single-stream path resume it instead of discarding it.
        if (partialFile.exists() && !manifestFile.exists()) {
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

        partialFile.parentFile?.let { if (!it.exists()) it.mkdirs() }
        dropOrphanManifest(partialFile, manifestFile)
        val parts = loadOrInitManifest(manifestFile, partialFile, total, etag)

        val transferred = AtomicLong(parts.sumOf { it.done })
        onProgress(transferred.get(), total)

        val aborted = AtomicBoolean(false)
        val fallback = AtomicBoolean(false)
        val firstError = AtomicReference<Exception?>(null)
        val lastFlushed = LongArray(parts.size) { parts[it].done }

        val pool = Executors.newFixedThreadPool(minOf(segmentCount, parts.size))
        try {
            val futures = parts.map { part ->
                pool.submit {
                    try {
                        downloadPart(url, etag, partialFile, part, aborted) { delta ->
                            val t = transferred.addAndGet(delta)
                            synchronized(lastFlushed) {
                                if (part.done - lastFlushed[part.index] >= manifestFlushBytes) {
                                    lastFlushed[part.index] = part.done
                                    flushManifest(manifestFile, total, etag, parts)
                                }
                            }
                            onProgress(t, total)
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

        if (fallback.get()) {
            // Stale/unusable bytes — clear before the caller falls back.
            discard(partialFile, manifestFile)
            return Outcome.FALLBACK
        }
        val err = firstError.get()
        if (err != null) {
            // Transient — persist progress so the next attempt resumes, then bubble up.
            flushManifest(manifestFile, total, etag, parts)
            throw err
        }
        val got = parts.sumOf { it.done }
        if (got < total) {
            flushManifest(manifestFile, total, etag, parts)
            throw java.io.IOException("Concurrent download incomplete ($got/$total)")
        }

        // Success: `.partial` is fully filled. The manifest's job is done and it
        // must never outlive the `.partial` it describes (caller is about to
        // promote it), so drop it now.
        manifestFile.delete()
        log("concurrent: completed ($total bytes)")
        return Outcome.COMPLETED
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

    private fun dropOrphanManifest(partialFile: File, manifestFile: File) {
        if (!partialFile.exists() && manifestFile.exists()) {
            log("concurrent: dropping orphan manifest")
            manifestFile.delete()
        }
    }

    private fun discard(partialFile: File, manifestFile: File) {
        // Manifest first so it never outlives the partial it describes.
        manifestFile.delete()
        partialFile.delete()
    }

    // Resume from a manifest whose size/ETag still match, else (re)create a
    // fresh pre-allocated partial + manifest. Manifest is removed before the
    // partial is (re)created, and written only after the partial exists.
    private fun loadOrInitManifest(
        manifestFile: File,
        partialFile: File,
        total: Long,
        etag: String?,
    ): List<Part> {
        if (manifestFile.exists() && partialFile.exists()) {
            val parsed = parseManifest(manifestFile, total, etag, partialFile.length())
            if (parsed != null) {
                log("concurrent: resuming, transferred=${parsed.sumOf { it.done }}/$total")
                return parsed
            }
        }
        discard(partialFile, manifestFile)
        RandomAccessFile(partialFile, "rw").use { it.setLength(total) }
        val parts = ArrayList<Part>()
        val chunk = (total + segmentCount - 1) / segmentCount
        var i = 0
        while (i < segmentCount) {
            val start = i * chunk
            if (start >= total) break
            val end = minOf(start + chunk - 1, total - 1)
            parts.add(Part(parts.size, start, end, 0))
            i += 1
        }
        writeManifest(manifestFile, total, etag, parts)
        return parts
    }

    // Manifest format (dependency-free, internal): line 0 "<size>|<etag>",
    // then one "<index>,<start>,<end>,<done>" line per segment.
    private fun writeManifest(manifestFile: File, total: Long, etag: String?, parts: List<Part>) {
        val sb = StringBuilder()
        sb.append(total).append('|').append(etag ?: "").append('\n')
        for (p in parts) {
            sb.append(p.index).append(',').append(p.start).append(',')
                .append(p.end).append(',').append(p.done).append('\n')
        }
        manifestFile.writeText(sb.toString())
    }

    @Synchronized
    private fun flushManifest(manifestFile: File, total: Long, etag: String?, parts: List<Part>) {
        try {
            writeManifest(manifestFile, total, etag, parts)
        } catch (e: Exception) {
            log("concurrent: manifest flush failed: ${e.javaClass.simpleName}")
        }
    }

    private fun parseManifest(manifestFile: File, total: Long, etag: String?, partialSize: Long): List<Part>? {
        return try {
            val lines = manifestFile.readText().trim().split('\n')
            if (lines.isEmpty()) return null
            val head = lines[0].split('|')
            val savedSize = head.getOrNull(0)?.toLongOrNull() ?: return null
            val savedEtag = head.getOrNull(1)?.takeIf { it.isNotEmpty() }
            // Object must be identical to what's on disk and on the CDN.
            if (savedSize != total || partialSize != total) return null
            if (etag != null && savedEtag != null && etag != savedEtag) return null
            val parts = ArrayList<Part>()
            for (idx in 1 until lines.size) {
                val cols = lines[idx].split(',')
                if (cols.size != 4) return null
                val i = cols[0].toIntOrNull() ?: return null
                val s = cols[1].toLongOrNull() ?: return null
                val e = cols[2].toLongOrNull() ?: return null
                var d = cols[3].toLongOrNull() ?: return null
                val segLen = e - s + 1
                if (d < 0) d = 0
                if (d > segLen) d = segLen
                parts.add(Part(i, s, e, d))
            }
            if (parts.isEmpty()) null else parts
        } catch (e: Exception) {
            log("concurrent: manifest parse failed: ${e.javaClass.simpleName}")
            null
        }
    }

    // Download [start+done, end] of [part] into its own RandomAccessFile handle
    // (each segment gets its own fd so concurrent positioned writes don't race),
    // resuming from part.done and retrying transient failures in place.
    private fun downloadPart(
        url: String,
        etag: String?,
        partialFile: File,
        part: Part,
        aborted: AtomicBoolean,
        onBytes: (delta: Long) -> Unit,
    ) {
        var retry = 0
        while (true) {
            if (aborted.get()) throw java.io.IOException("aborted")
            val rangeStart = part.start + part.done
            if (rangeStart > part.end) return
            try {
                fetchSegment(url, etag, partialFile, part, rangeStart, aborted, onBytes)
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
        partialFile: File,
        part: Part,
        rangeStart: Long,
        aborted: AtomicBoolean,
        onBytes: (delta: Long) -> Unit,
    ) {
        val builder = Request.Builder().url(url)
            .addHeader("Range", "bytes=$rangeStart-${part.end}")
        // If-Range: a mismatched ETag makes the CDN reply 200 (full body)
        // instead of 206, which we treat as a fallback signal.
        if (etag != null) builder.addHeader("If-Range", etag)
        httpClient.newCall(builder.build()).execute().use { response ->
            if (response.code == 200) {
                throw FallbackException("server returned 200 to a Range request")
            }
            if (response.code != 206) {
                throw java.io.IOException("HTTP ${response.code}")
            }
            val body = response.body ?: throw java.io.IOException("Empty segment body")
            RandomAccessFile(partialFile, "rw").use { raf ->
                raf.seek(rangeStart)
                body.byteStream().use { input ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        if (aborted.get()) throw java.io.IOException("aborted")
                        val read = input.read(buffer)
                        if (read == -1) break
                        raf.write(buffer, 0, read)
                        part.done += read
                        onBytes(read.toLong())
                    }
                }
            }
        }
    }
}
