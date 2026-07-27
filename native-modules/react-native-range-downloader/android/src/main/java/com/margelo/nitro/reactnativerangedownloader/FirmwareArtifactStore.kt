package com.margelo.nitro.reactnativerangedownloader

import android.system.Os
import com.margelo.nitro.NitroModules
import com.sniconnect.SniPinnedTransport
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import org.json.JSONObject

internal data class StoredFirmwareArtifact(
  val artifactRef: String,
  val size: Long,
  val sha256: String,
  val file: File,
)

internal data class StoredFirmwareArchiveEntry(
  val entryName: String,
  val artifact: StoredFirmwareArtifact,
)

private data class StagedFirmwareArchiveEntry(
  val entryName: String,
  val size: Long,
  val sha256: String,
  val file: File,
)

private data class FirmwareDownloadKey(
  val transactionId: String,
  val expectedSha256: String,
)

internal object FirmwareArtifactStore {
  const val MAX_READ_BYTES = 256 * 1024

  private const val MAX_ARTIFACT_BYTES = 512L * 1024 * 1024
  private const val MAX_LEASE_METADATA_BYTES = 1024L * 1024
  private const val MAX_TOTAL_LEASE_REFS = 8192
  private const val FINAL_ARTIFACT_GRACE_MS = 24L * 60 * 60 * 1000
  private const val PARTIAL_ARTIFACT_GRACE_MS = 7L * 24 * 60 * 60 * 1000
  private val sha256Pattern = Regex("^[a-fA-F0-9]{64}$")
  private val artifactRefPattern = Regex("^fw:[a-f0-9]{64}$")
  private val leaseRefPattern = Regex("^fwlease:[a-f0-9-]{36}$")
  private val identifierPattern = Regex("^[A-Za-z0-9._:-]{1,160}$")
  private val downloadLocks = ConcurrentHashMap<FirmwareDownloadKey, Any>()
  private val activeCalls =
    ConcurrentHashMap<String, MutableSet<Call>>()
  private val cancelledTransactions =
    ConcurrentHashMap.newKeySet<String>()
  private val activeDownloadLock = Any()
  private val activeDownloadCounts = mutableMapOf<String, Int>()
  private val leaseLock = Any()
  private val readerLock = Any()
  private val readers = mutableMapOf<String, OpenReader>()

  private data class LeaseState(
    val transactionId: String,
    val artifactRefs: MutableSet<String>,
  )

  private data class OpenReader(
    val file: File,
    val handle: RandomAccessFile,
    val size: Long,
  )

  private val root: File
    get() {
      val context = NitroModules.applicationContext
        ?: error("Application context is not available")
      return File(context.filesDir, "onekey-firmware-artifacts").also {
        check(it.exists() || it.mkdirs()) {
          "Firmware artifact directory cannot be created"
        }
      }
    }

  fun download(params: FirmwareArtifactDownloadParams): StoredFirmwareArtifact {
    val validated = validateDownloadParams(params)
    check(!cancelledTransactions.contains(params.transactionId)) {
      "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
    }
    retainExpectedArtifact(
      leaseRef = params.leaseRef,
      transactionId = params.transactionId,
      artifactRef = "fw:${validated.expectedSha256}",
    )
    val lockKey = FirmwareDownloadKey(
      params.transactionId,
      validated.expectedSha256,
    )
    val lock = downloadLocks.computeIfAbsent(lockKey) { Any() }
    markDownloadActive(validated.expectedSha256, 1)
    try {
      return synchronized(lock) {
        check(!cancelledTransactions.contains(params.transactionId)) {
          "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
        }
        downloadLocked(params, validated)
      }
    } finally {
      markDownloadActive(validated.expectedSha256, -1)
    }
  }

  fun cancelDownloads(transactionId: String) {
    require(identifierPattern.matches(transactionId)) {
      "Invalid firmware transactionId"
    }
    cancelledTransactions.add(transactionId)
    activeCalls[transactionId]
      ?.toList()
      ?.forEach { it.cancel() }
  }

  fun discard(artifactRef: String) {
    val file = resolveArtifactFile(artifactRef)
    synchronized(leaseLock) {
      require(loadLeasesLocked().values.none { artifactRef in it.artifactRefs }) {
        "ARTIFACT_LEASED: firmware artifact is retained"
      }
    }
    synchronized(readerLock) {
      require(readers.none { it.value.file == file }) {
        "ARTIFACT_BUSY: firmware artifact has an open reader"
      }
    }
    if (file.exists() && !file.delete()) {
      error("Firmware artifact cannot be discarded")
    }
  }

  fun open(artifactRef: String): Pair<String, Long> {
    val file = resolveArtifactFile(artifactRef)
    val size = file.length()
    require(size > 0) { "Firmware artifact is empty" }
    require(hashFile(file) == artifactRef.removePrefix("fw:")) {
      "Firmware artifact SHA-256 mismatch"
    }
    val readerId = UUID.randomUUID().toString()
    synchronized(readerLock) {
      readers[readerId] = OpenReader(file, RandomAccessFile(file, "r"), size)
    }
    return readerId to size
  }

  fun read(readerId: String, offset: Long, length: Int): ByteArray {
    require(offset >= 0 && length in 1..MAX_READ_BYTES) {
      "Invalid firmware artifact read"
    }
    return synchronized(readerLock) {
      val reader = readers[readerId] ?: error("Firmware artifact reader is invalid")
      require(offset <= reader.size - length) {
        "Firmware artifact read is out of bounds"
      }
      val data = ByteArray(length)
      reader.handle.seek(offset)
      reader.handle.readFully(data)
      data
    }
  }

  fun close(readerId: String) {
    synchronized(readerLock) {
      readers.remove(readerId)?.handle?.close()
    }
  }

  fun materializeArchive(
    leaseRef: String,
    artifactRef: String,
    expectedEntries: Array<FirmwareArchiveExpectedEntry>,
  ): List<StoredFirmwareArchiveEntry> {
    requireLease(leaseRef)
    val archiveFile = resolveArtifactFile(artifactRef)
    val requirements = FirmwareArchiveRules.validateRequirements(expectedEntries)
    val centralEntries = FirmwareArchiveRules.validateCentralDirectory(
      archiveFile,
      requirements,
    )
    val requirementsByName = requirements.associateBy { it.entryName }
    val centralNames = centralEntries.mapTo(mutableSetOf()) { it.name }
    val scratchDir = File(root, "archive-${UUID.randomUUID()}")
    check(scratchDir.mkdirs()) { "Firmware archive scratch directory cannot be created" }
    try {
      val staged = mutableListOf<StagedFirmwareArchiveEntry>()
      val entryNames = mutableSetOf<String>()
      ZipInputStream(BufferedInputStream(FileInputStream(archiveFile))).use { zip ->
        while (true) {
          val zipEntry = zip.nextEntry ?: break
          require(!zipEntry.isDirectory) {
            "Firmware archive contains an unexpected directory"
          }
          val requirement = requirementsByName[zipEntry.name]
            ?: error("Firmware archive contains an unexpected entry")
          require(
            centralNames.contains(zipEntry.name) &&
              entryNames.add(zipEntry.name)
          ) {
            "Firmware archive contains a duplicate or mismatched entry"
          }
          val expectedSize = requirement.expectedSize.toLong()
          val expectedSha256 = requirement.expectedSha256.lowercase()
          val scratchFile = File(scratchDir, "${staged.size}.entry")
          val digest = MessageDigest.getInstance("SHA-256")
          var entrySize = 0L
          RandomAccessFile(scratchFile, "rw").use { output ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
              val count = zip.read(buffer)
              if (count < 0) break
              if (count == 0) continue
              entrySize = Math.addExact(entrySize, count.toLong())
              require(entrySize <= expectedSize) {
                "Firmware archive entry exceeds its expected size"
              }
              output.write(buffer, 0, count)
              digest.update(buffer, 0, count)
            }
            output.fd.sync()
          }
          val sha256 = digest.digest().toHex()
          require(entrySize == expectedSize && sha256 == expectedSha256) {
            "Firmware archive entry integrity mismatch"
          }
          staged += StagedFirmwareArchiveEntry(
            entryName = requirement.entryName,
            size = entrySize,
            sha256 = sha256,
            file = scratchFile,
          )
          zip.closeEntry()
        }
      }
      require(entryNames == centralNames) {
        "Firmware archive has missing or extra entries"
      }
      return staged.map { entry ->
        val destination = artifactFile(entry.sha256)
        val stored = validateStoredArtifactOrNull(
          destination,
          entry.size,
          entry.sha256,
        ) ?: run {
          promoteAtomically(entry.file, destination)
          StoredFirmwareArtifact(
            "fw:${entry.sha256}",
            entry.size,
            entry.sha256,
            destination,
          )
        }
        retainExpectedArtifact(
          leaseRef = leaseRef,
          transactionId = null,
          artifactRef = stored.artifactRef,
        )
        StoredFirmwareArchiveEntry(entry.entryName, stored)
      }
    } finally {
      scratchDir.deleteRecursively()
    }
  }

  private data class ValidatedDownload(
    val expectedSize: Long,
    val maxBytes: Long,
    val expectedSha256: String,
    val hostname: String,
  )

  private fun validateDownloadParams(
    params: FirmwareArtifactDownloadParams,
  ): ValidatedDownload {
    require(
      params.taskId.isNotEmpty() &&
        params.taskId.length <= 100 &&
        params.taskId.all { it.isLetterOrDigit() || it in "._-" }
    ) {
      "Invalid firmware taskId"
    }
    require(identifierPattern.matches(params.transactionId)) {
      "Invalid firmware transactionId"
    }
    require(identifierPattern.matches(params.artifactId)) {
      "Invalid firmware artifactId"
    }
    require(leaseRefPattern.matches(params.leaseRef)) {
      "Invalid firmware leaseRef"
    }
    val url = params.url.toHttpUrlOrNull()
      ?: throw IllegalArgumentException("Invalid firmware URL")
    require(
      url.isHttps &&
        url.port == 443 &&
        url.username.isEmpty() &&
        url.password.isEmpty() &&
        url.fragment == null
    ) {
      "Firmware URL must use HTTPS port 443"
    }
    val expectedSize = params.expectedSize.toExactPositiveLong("expectedSize")
    val maxBytes = params.maxBytes.toExactPositiveLong("maxBytes")
    require(maxBytes == expectedSize && maxBytes <= MAX_ARTIFACT_BYTES) {
      "Invalid firmware maxBytes"
    }
    require(sha256Pattern.matches(params.expectedSha256)) {
      "Invalid firmware artifact SHA-256"
    }
    require(params.routeType == "domain" || params.routeType == "pinnedIp") {
      "Invalid firmware route type"
    }
    if (params.routeType == "pinnedIp") {
      require(!params.resolvedIp.isNullOrEmpty()) {
        "Pinned route requires resolvedIp"
      }
    } else {
      require(params.resolvedIp == null) {
        "Domain route must not include resolvedIp"
      }
    }
    return ValidatedDownload(
      expectedSize = expectedSize,
      maxBytes = maxBytes,
      expectedSha256 = params.expectedSha256.lowercase(),
      hostname = url.host,
    )
  }

  private fun downloadLocked(
    params: FirmwareArtifactDownloadParams,
    validated: ValidatedDownload,
  ): StoredFirmwareArtifact {
    val finalFile = artifactFile(validated.expectedSha256)
    validateStoredArtifactOrNull(
      finalFile,
      validated.expectedSize,
      validated.expectedSha256,
    )?.let { return it }

    val partialFile = File(
      root,
      "${validated.expectedSha256}.${params.taskId}.partial",
    )
    if (partialFile.length() > validated.expectedSize) {
      check(partialFile.delete()) { "Invalid firmware partial cannot be removed" }
    }
    if (partialFile.length() == validated.expectedSize) {
      validateStoredArtifactOrNull(
        partialFile,
        validated.expectedSize,
        validated.expectedSha256,
      )?.let {
        promoteAtomically(partialFile, finalFile)
        return StoredFirmwareArtifact(
          it.artifactRef,
          it.size,
          it.sha256,
          finalFile,
        )
      }
      check(partialFile.delete()) { "Invalid firmware partial cannot be removed" }
    }

    val resumeOffset = partialFile.length()
    val requestBuilder = Request.Builder()
      .url(params.url)
      .header("Accept-Encoding", "identity")
    if (resumeOffset > 0) {
      requestBuilder.header("Range", "bytes=$resumeOffset-")
    }

    val client = if (params.routeType == "pinnedIp") {
      SniPinnedTransport.createClient(
        ip = checkNotNull(params.resolvedIp),
        hostname = validated.hostname,
      )
    } else {
      OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        .followRedirects(false)
        .followSslRedirects(false)
        .build()
    }
    val call = client.newCall(requestBuilder.build())
    params.overallDeadlineSeconds?.let { deadline ->
      require(deadline.isFinite() && deadline > 0) {
        "Invalid firmware download deadline"
      }
      call.timeout().timeout(deadline.toLong().coerceAtLeast(1), TimeUnit.SECONDS)
    }

    registerCall(params.transactionId, call)
    try {
      call.execute().use {
        writeResponseToPartial(
          it,
          partialFile,
          resumeOffset,
          validated.expectedSize,
          validated.maxBytes,
        )
      }
    } catch (error: IOException) {
      if (
        call.isCanceled() ||
        cancelledTransactions.contains(params.transactionId)
      ) {
        throw IllegalStateException(
          "ARTIFACT_CANCELLED: firmware artifact download was cancelled",
          error,
        )
      }
      throw IllegalStateException(
        "ARTIFACT_NETWORK_FAILED: firmware request failed",
        error,
      )
    } finally {
      unregisterCall(params.transactionId, call)
    }
    check(!cancelledTransactions.contains(params.transactionId)) {
      "ARTIFACT_CANCELLED: firmware artifact download was cancelled"
    }

    val artifact = try {
      validateStoredArtifact(
        partialFile,
        validated.expectedSize,
        validated.expectedSha256,
      )
    } catch (error: Throwable) {
      partialFile.delete()
      throw error
    }
    promoteAtomically(partialFile, finalFile)
    return StoredFirmwareArtifact(
      artifact.artifactRef,
      artifact.size,
      artifact.sha256,
      finalFile,
    )
  }

  private fun writeResponseToPartial(
    response: Response,
    partialFile: File,
    resumeOffset: Long,
    expectedSize: Long,
    maxBytes: Long,
  ) {
    require(response.code == 200 || response.code == 206) {
      "ARTIFACT_HTTP_${response.code}: firmware request failed"
    }
    val append = resumeOffset > 0 && response.code == 206
    if (response.code == 206) {
      require(
        validateContentRange(
          response.header("Content-Range"),
          if (append) resumeOffset else 0,
          expectedSize,
        )
      ) {
        "ARTIFACT_PROTOCOL_INVALID: firmware resume Content-Range is invalid"
      }
    }
    val body = response.body ?: error("Firmware response has no body")
    RandomAccessFile(partialFile, "rw").use { output ->
      if (append) {
        output.seek(resumeOffset)
      } else {
        output.setLength(0)
      }
      var written = if (append) resumeOffset else 0L
      val source = body.source()
      val buffer = ByteArray(64 * 1024)
      while (true) {
        val count = source.read(buffer)
        if (count < 0) break
        if (count == 0) continue
        written += count
        require(written <= maxBytes) {
          "ARTIFACT_PROTOCOL_INVALID: firmware artifact exceeds maxBytes"
        }
        output.write(buffer, 0, count)
      }
      output.fd.sync()
    }
  }

  private fun validateContentRange(
    value: String?,
    expectedStart: Long,
    expectedTotal: Long,
  ): Boolean {
    val match = value
      ?.lowercase()
      ?.let { Regex("^bytes ([0-9]+)-([0-9]+)/([0-9]+)$").matchEntire(it) }
      ?: return false
    val start = match.groupValues[1].toLongOrNull() ?: return false
    val end = match.groupValues[2].toLongOrNull() ?: return false
    val total = match.groupValues[3].toLongOrNull() ?: return false
    return start == expectedStart &&
      end >= start &&
      end < total &&
      total == expectedTotal
  }

  private fun validateStoredArtifactOrNull(
    file: File,
    expectedSize: Long,
    expectedSha256: String,
  ): StoredFirmwareArtifact? = try {
    validateStoredArtifact(file, expectedSize, expectedSha256)
  } catch (_: Throwable) {
    null
  }

  private fun validateStoredArtifact(
    file: File,
    expectedSize: Long,
    expectedSha256: String,
  ): StoredFirmwareArtifact {
    require(file.isFile && file.length() == expectedSize) {
      "ARTIFACT_INTEGRITY_FAILED: firmware artifact size mismatch"
    }
    val sha256 = hashFile(file)
    require(sha256 == expectedSha256) {
      "ARTIFACT_INTEGRITY_FAILED: firmware artifact SHA-256 mismatch"
    }
    return StoredFirmwareArtifact("fw:$sha256", expectedSize, sha256, file)
  }

  private fun resolveArtifactFile(artifactRef: String): File {
    require(artifactRefPattern.matches(artifactRef)) {
      "Invalid firmware artifactRef"
    }
    val file = artifactFile(artifactRef.removePrefix("fw:"))
    require(file.isFile) { "Firmware artifact not found" }
    return file
  }

  private fun artifactFile(sha256: String): File = File(root, "$sha256.bin")

  fun createLease(transactionId: String): String {
    require(identifierPattern.matches(transactionId)) {
      "Invalid firmware transactionId"
    }
    return synchronized(leaseLock) {
      val leases = loadLeasesLocked()
      require(leases.size < 32) {
        "Too many firmware artifact leases"
      }
      val leaseRef = "fwlease:${UUID.randomUUID()}"
      leases[leaseRef] = LeaseState(transactionId, mutableSetOf())
      saveLeasesLocked(leases)
      leaseRef
    }
  }

  fun retain(leaseRef: String, artifactRef: String) {
    resolveArtifactFile(artifactRef)
    retainExpectedArtifact(
      leaseRef = leaseRef,
      transactionId = null,
      artifactRef = artifactRef,
    )
  }

  fun releaseLease(leaseRef: String, disposition: String) {
    require(
      disposition == "completed" ||
        disposition == "safeCancelled" ||
        disposition == "safeAbandoned"
    ) {
      "Invalid firmware lease disposition"
    }
    val transactionId = synchronized(leaseLock) {
      val leases = loadLeasesLocked()
      val removed = leases.remove(validateLeaseRef(leaseRef))
      require(removed != null) {
        "Firmware artifact lease is unavailable"
      }
      saveLeasesLocked(leases)
      removed.transactionId
    }
    cancelledTransactions.remove(transactionId)
    downloadLocks.keys.removeIf { it.transactionId == transactionId }
  }

  fun reconcileLeases(activeLeaseRefs: Array<String>) {
    require(activeLeaseRefs.size <= 32) {
      "Too many active firmware artifact leases"
    }
    val active = activeLeaseRefs.mapTo(mutableSetOf()) {
      validateLeaseRef(it)
    }
    require(active.size == activeLeaseRefs.size) {
      "Duplicate active firmware artifact lease"
    }
    synchronized(leaseLock) {
      val leases = loadLeasesLocked()
      require(active.all { leases.containsKey(it) }) {
        "Firmware artifact lease reconciliation is incomplete"
      }
      leases.keys.retainAll(active)
      saveLeasesLocked(leases)
    }
  }

  fun sweepOrphans(): Pair<Int, Long> {
    val retainedSha256 = synchronized(leaseLock) {
      loadLeasesLocked().values
        .flatMap { it.artifactRefs }
        .mapTo(mutableSetOf()) { it.removePrefix("fw:") }
    }
    val activeSha256 = synchronized(activeDownloadLock) {
      activeDownloadCounts.filterValues { it > 0 }.keys.toSet()
    }
    val openFiles = synchronized(readerLock) {
      readers.values.mapTo(mutableSetOf()) { it.file.absolutePath }
    }
    val now = System.currentTimeMillis()
    var deletedFiles = 0
    var deletedBytes = 0L
    root.listFiles()?.forEach { file ->
      if (!file.isFile || file.name == "leases.json") return@forEach
      val sha256 = file.name.take(64)
      if (
        !sha256Pattern.matches(sha256) ||
        sha256 in retainedSha256 ||
        sha256 in activeSha256 ||
        file.absolutePath in openFiles
      ) {
        return@forEach
      }
      val grace = if (file.name.endsWith(".bin")) {
        FINAL_ARTIFACT_GRACE_MS
      } else if (file.name.endsWith(".partial")) {
        PARTIAL_ARTIFACT_GRACE_MS
      } else {
        return@forEach
      }
      if (now - file.lastModified() < grace) return@forEach
      val size = file.length()
      if (file.delete()) {
        deletedFiles += 1
        deletedBytes += size
      }
    }
    return deletedFiles to deletedBytes
  }

  private fun requireLease(leaseRef: String) {
    synchronized(leaseLock) {
      require(loadLeasesLocked().containsKey(validateLeaseRef(leaseRef))) {
        "Firmware artifact lease is unavailable"
      }
    }
  }

  private fun retainExpectedArtifact(
    leaseRef: String,
    transactionId: String?,
    artifactRef: String,
  ) {
    require(artifactRefPattern.matches(artifactRef)) {
      "Invalid firmware artifactRef"
    }
    synchronized(leaseLock) {
      val leases = loadLeasesLocked()
      val lease = leases[validateLeaseRef(leaseRef)]
        ?: error("Firmware artifact lease is unavailable")
      if (transactionId != null) {
        require(lease.transactionId == transactionId) {
          "Firmware artifact lease transaction mismatch"
        }
      }
      if (lease.artifactRefs.add(artifactRef)) {
        saveLeasesLocked(leases)
      }
    }
  }

  private fun validateLeaseRef(leaseRef: String): String {
    require(leaseRefPattern.matches(leaseRef)) {
      "Invalid firmware leaseRef"
    }
    return leaseRef
  }

  private fun loadLeasesLocked(): MutableMap<String, LeaseState> {
    val file = File(root, "leases.json")
    if (!file.exists()) return mutableMapOf()
    require(file.length() in 1..MAX_LEASE_METADATA_BYTES) {
      "Firmware lease metadata is too large"
    }
    val envelope = JSONObject(file.readText(Charsets.UTF_8))
    require(envelope.optInt("schemaVersion") == 1) {
      "Unsupported firmware lease schema"
    }
    val result = mutableMapOf<String, LeaseState>()
    val jsonLeases = envelope.getJSONObject("leases")
    val keys = jsonLeases.keys()
    while (keys.hasNext()) {
      val leaseRef = validateLeaseRef(keys.next())
      val jsonLease = jsonLeases.getJSONObject(leaseRef)
      val transactionId = jsonLease.getString("transactionId")
      require(identifierPattern.matches(transactionId)) {
        "Invalid persisted firmware transactionId"
      }
      val jsonRefs = jsonLease.getJSONArray("artifactRefs")
      require(jsonRefs.length() <= 4096) {
        "Too many persisted firmware artifact refs"
      }
      val refs = mutableSetOf<String>()
      for (index in 0 until jsonRefs.length()) {
        val artifactRef = jsonRefs.getString(index)
        require(artifactRefPattern.matches(artifactRef) && refs.add(artifactRef)) {
          "Invalid persisted firmware artifact ref"
        }
      }
      result[leaseRef] = LeaseState(transactionId, refs)
    }
    require(result.size <= 32) {
      "Too many persisted firmware artifact leases"
    }
    require(result.values.sumOf { it.artifactRefs.size } <= MAX_TOTAL_LEASE_REFS) {
      "Too many persisted firmware artifact refs"
    }
    return result
  }

  private fun saveLeasesLocked(leases: Map<String, LeaseState>) {
    require(
      leases.size <= 32 &&
        leases.values.sumOf { it.artifactRefs.size } <= MAX_TOTAL_LEASE_REFS
    ) {
      "Firmware lease metadata is too large"
    }
    val jsonLeases = JSONObject()
    leases.toSortedMap().forEach { (leaseRef, lease) ->
      jsonLeases.put(
        leaseRef,
        JSONObject()
          .put("transactionId", lease.transactionId)
          .put("artifactRefs", JSONArray(lease.artifactRefs.sorted())),
      )
    }
    val bytes = JSONObject()
      .put("schemaVersion", 1)
      .put("leases", jsonLeases)
      .toString()
      .toByteArray(Charsets.UTF_8)
    require(bytes.size.toLong() <= MAX_LEASE_METADATA_BYTES) {
      "Firmware lease metadata is too large"
    }
    val destination = File(root, "leases.json")
    val temporary = File(root, ".leases-${UUID.randomUUID()}.tmp")
    FileOutputStream(temporary).use { output ->
      output.write(bytes)
      output.fd.sync()
    }
    Os.rename(temporary.absolutePath, destination.absolutePath)
  }

  private fun markDownloadActive(sha256: String, delta: Int) {
    synchronized(activeDownloadLock) {
      val count = (activeDownloadCounts[sha256] ?: 0) + delta
      if (count <= 0) {
        activeDownloadCounts.remove(sha256)
      } else {
        activeDownloadCounts[sha256] = count
      }
    }
  }

  private fun hashFile(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    BufferedInputStream(FileInputStream(file)).use { input ->
      val buffer = ByteArray(MAX_READ_BYTES)
      while (true) {
        val count = input.read(buffer)
        if (count < 0) break
        if (count > 0) digest.update(buffer, 0, count)
      }
    }
    return digest.digest().toHex()
  }

  private fun promoteAtomically(source: File, destination: File) {
    destination.parentFile?.mkdirs()
    Os.rename(source.absolutePath, destination.absolutePath)
  }

  private fun Double.toExactPositiveLong(label: String): Long {
    require(isFinite() && this > 0 && this <= Long.MAX_VALUE.toDouble()) {
      "Invalid firmware $label"
    }
    val converted = toLong()
    require(converted.toDouble() == this) { "Invalid firmware $label" }
    return converted
  }

  private fun ByteArray.toHex(): String = joinToString("") {
    "%02x".format(it)
  }

  private fun registerCall(transactionId: String, call: Call) {
    val calls = activeCalls.computeIfAbsent(transactionId) {
      ConcurrentHashMap.newKeySet()
    }
    calls.add(call)
    if (cancelledTransactions.contains(transactionId)) {
      call.cancel()
    }
  }

  private fun unregisterCall(transactionId: String, call: Call) {
    val calls = activeCalls[transactionId] ?: return
    calls.remove(call)
    if (calls.isEmpty()) {
      activeCalls.remove(transactionId, calls)
    }
  }
}
