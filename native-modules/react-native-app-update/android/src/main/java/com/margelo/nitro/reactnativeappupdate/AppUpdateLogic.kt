package com.margelo.nitro.reactnativeappupdate

/**
 * Dependency-free pure logic extracted from ReactNativeAppUpdate.
 *
 * This object intentionally pulls in NO Android / Nitro / OkHttp / File-I/O
 * dependencies: it holds only constants and pure string-derivation helpers so
 * the values can be unit-tested under plain JVM JUnit (the adapter itself
 * cannot, because it needs a live Context / FileProvider / network).
 */
object AppUpdateLogic {
    // Must match ConcurrentRangeDownloader's default segmentCount: the
    // concurrent downloader writes sibling segment files
    // "<partial>.seg0".."<partial>.seg${N-1}", and Phase 2 below scans this
    // range to detect an in-flight concurrent download.
    const val CONCURRENT_SEGMENT_COUNT = 8

    /**
     * Derive the sibling segment-file name the concurrent downloader writes for
     * a given partial path and segment index, i.e. "<partial>.seg<index>".
     * This MUST match exactly what ConcurrentRangeDownloader writes.
     */
    fun segmentFileName(partialFilePath: String, index: Int): String =
        "$partialFilePath.seg$index"

    /**
     * All [CONCURRENT_SEGMENT_COUNT] segment-file names for a given partial
     * path, ordered seg0..seg${N-1}.
     */
    fun segmentFileNames(partialFilePath: String): List<String> =
        (0 until CONCURRENT_SEGMENT_COUNT).map { segmentFileName(partialFilePath, it) }

    /**
     * Parse the total size out of a 416 (Range Not Satisfiable)
     * `Content-Range: bytes * /<total>` header. Returns null when the header is
     * absent or is not the "unsatisfiable range" form (e.g. a normal
     * `bytes start-end/total`).
     *
     * Regex body copied VERBATIM from ReactNativeAppUpdate.downloadAPK (the 416
     * branch); wrapped here with no behavior change so the parse step is pure
     * and unit-testable. The adapter's `total == partialBytes` comparison stays
     * in the adapter (it needs runtime `partialBytes`); see [is416Complete].
     */
    fun parse416Total(contentRange: String?): Long? =
        contentRange
            ?.let { Regex("""bytes\s+\*\s*/\s*(\d+)""").find(it)?.groupValues?.getOrNull(1)?.toLongOrNull() }

    /**
     * Parse a 206 `Content-Range: bytes start-end/total` header into
     * (start, end, total). `total` is null when the server sends `*`
     * (unknown total). Returns null when the header is absent or malformed.
     *
     * `rangeRegex` body copied VERBATIM from ReactNativeAppUpdate.downloadAPK
     * (the 206 sanity-check). The adapter's `start != partialBytes`
     * CDN-misalignment guard stays in the adapter; see [is206StartAligned].
     */
    fun parse206ContentRange(contentRange: String?): Triple<Long?, Long?, Long?>? {
        val match = contentRange
            ?.let { Regex("""bytes\s+(\d+)\s*-\s*(\d+)\s*/\s*(\d+|\*)""").find(it) }
            ?: return null
        val start = match.groupValues.getOrNull(1)?.toLongOrNull()
        val end = match.groupValues.getOrNull(2)?.toLongOrNull()
        // group 3 is either digits or literal "*"; "*" → unknown total → null.
        val total = match.groupValues.getOrNull(3)?.toLongOrNull()
        return Triple(start, end, total)
    }

    /**
     * 416 recovery predicate: the server-reported total exactly equals the
     * bytes we already have on disk, meaning our `.partial` IS the whole APK
     * and just needs verify + rename rather than a wipe. Pure mirror of the
     * adapter's `totalFromHeader != null && totalFromHeader == partialBytes`.
     */
    fun is416Complete(total: Long?, partialBytes: Long): Boolean =
        total != null && total == partialBytes

    /**
     * 206 start-alignment guard: the server's body starts exactly where we
     * asked it to resume from. When false (CDN/proxy rewrote the range), the
     * 206 body is a mis-aligned slice and must NOT be appended. Pure mirror of
     * the adapter's `rangeStart != null && rangeStart == partialBytes` check.
     */
    fun is206StartAligned(start: Long?, partialBytes: Long): Boolean =
        start != null && start == partialBytes
}
