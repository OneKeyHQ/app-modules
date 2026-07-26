package com.margelo.nitro.reactnativerangedownloader

import android.system.Os
import android.system.OsConstants
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.StandardCopyOption
import java.util.Properties
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class FirmwareArtifactActivityToken(
  private val id: UUID,
  val artifactRef: String,
  val kind: ArtifactActivityKind,
  private val store: FirmwareArtifactStore,
) : AutoCloseable {
  private val releaseLock = Any()
  private var didRelease = false

  override fun close() {
    val shouldRelease = synchronized(releaseLock) {
      if (didRelease) {
        false
      } else {
        didRelease = true
        true
      }
    }
    if (shouldRelease) {
      store.releaseActivity(id, artifactRef)
    }
  }
}

internal class FirmwareArtifactStore(
  private val rootDirectory: File,
) {
  internal object RetentionPolicy {
    const val GRACE_PERIOD_MILLIS = 60L * 60L * 1000L
    const val PARTIAL_TTL_MILLIS = 7L * 24L * 60L * 60L * 1000L
    const val STAGING_TTL_MILLIS = 24L * 60L * 60L * 1000L
    const val VERIFIED_FINAL_TTL_MILLIS = 30L * 24L * 60L * 60L * 1000L
    const val ARCHIVE_TTL_MILLIS = 7L * 24L * 60L * 60L * 1000L
    const val MATERIALIZED_CHILD_TTL_MILLIS = 30L * 24L * 60L * 60L * 1000L
    const val BYTE_BUDGET = 512L * 1024L * 1024L
    const val WORKER_CANCELLATION_TIMEOUT_MILLIS = 5_000L

    fun ttlMillis(retentionClass: ArtifactRetentionClass): Long =
      when (retentionClass) {
        ArtifactRetentionClass.PARTIAL -> PARTIAL_TTL_MILLIS
        ArtifactRetentionClass.STAGING -> STAGING_TTL_MILLIS
        ArtifactRetentionClass.VERIFIED_FINAL -> VERIFIED_FINAL_TTL_MILLIS
        ArtifactRetentionClass.ARCHIVE -> ARCHIVE_TTL_MILLIS
        ArtifactRetentionClass.MATERIALIZED_CHILD -> MATERIALIZED_CHILD_TTL_MILLIS
      }
  }

  private data class ActiveActivity(
    val kind: ArtifactActivityKind,
    val cancel: (() -> Unit)?,
  )

  private val artifactsDirectory = File(rootDirectory, "artifacts")
  private val leasesDirectory = File(rootDirectory, "leases")
  private val ioLock = Any()
  private val activityLock = ReentrantLock()
  private val activityChanged = activityLock.newCondition()
  private val activitiesByArtifact =
    mutableMapOf<String, MutableMap<UUID, ActiveActivity>>()
  private var didReconcileLeases = false

  fun createArtifact(
    artifactId: String,
    canonicalUrl: String,
    hostname: String,
    route: StoredArtifactRoute,
    expectedSize: Long,
    expectedSha256: String,
    retentionClass: ArtifactRetentionClass,
    nowMillis: Long = System.currentTimeMillis(),
  ): StoredArtifactMetadata =
    synchronized(ioLock) {
      ensureDirectories()
      if (artifactId.isBlank() ||
        canonicalUrl.isBlank() ||
        hostname.isBlank() ||
        expectedSize <= 0 ||
        !StoredArtifactMetadata.isTrustedSha256(expectedSha256)
      ) {
        throw FirmwareArtifactStoreException.invalidMetadata()
      }

      val artifactRef = UUID.randomUUID().toString().lowercase()
      val directory = artifactDirectory(artifactRef)
      try {
        if (!directory.mkdir()) {
          throw FirmwareArtifactStoreException.storageFailure()
        }
        val metadata = StoredArtifactMetadata.create(
          artifactRef = artifactRef,
          artifactId = artifactId,
          canonicalUrl = canonicalUrl,
          hostname = hostname,
          route = route,
          expectedSize = expectedSize,
          expectedSha256 = expectedSha256,
          retentionClass = retentionClass,
          nowMillis = nowMillis,
        )
        writeMetadata(metadata)
        metadata
      } catch (error: Exception) {
        directory.deleteRecursively()
        throw mapStorageError(error)
      }
    }

  fun loadArtifact(artifactRef: String): StoredArtifactMetadata =
    synchronized(ioLock) {
      ensureDirectories()
      readMetadata(artifactRef)
    }

  fun updateArtifact(
    artifactRef: String,
    mutate: (StoredArtifactMetadata) -> Unit,
  ): StoredArtifactMetadata =
    synchronized(ioLock) {
      ensureDirectories()
      val metadata = readMetadata(artifactRef)
      mutate(metadata)
      if (metadata.artifactRef != validateReference(artifactRef) ||
        metadata.schemaVersion != StoredArtifactMetadata.CURRENT_SCHEMA_VERSION
      ) {
        throw FirmwareArtifactStoreException.invalidMetadata()
      }
      writeMetadata(metadata)
      metadata
    }

  fun beginTransferAttempt(
    leaseRef: String,
    taskId: String,
    artifactId: String,
    canonicalUrl: String,
    hostname: String,
    route: StoredArtifactRoute,
    expectedSize: Long,
    expectedSha256: String,
    initialDeadlineAtMillis: Long,
    maxRunAttempts: Int,
    nowMillis: Long = System.currentTimeMillis(),
  ): StoredArtifactMetadata =
    synchronized(ioLock) {
      ensureDirectories()
      val lease = readLease(leaseRef)
      if (!lease.isActive) {
        throw FirmwareArtifactStoreException.leaseReleased()
      }
      if (maxRunAttempts <= 0 || initialDeadlineAtMillis <= 0) {
        throw FirmwareArtifactStoreException.deadlineExceeded()
      }
      val taskIdHash = StoredArtifactMetadata.sha256(taskId)
      val metadata = loadAllMetadata()
        .asSequence()
        .filter {
          it.matchesTransferIdentity(
            artifactId = artifactId,
            canonicalUrl = canonicalUrl,
            hostname = hostname,
            expectedSize = expectedSize,
            expectedSha256 = expectedSha256,
          )
        }
        .maxByOrNull { it.updatedAtMillis }
        ?: createArtifact(
          artifactId = artifactId,
          canonicalUrl = canonicalUrl,
          hostname = hostname,
          route = route,
          expectedSize = expectedSize,
          expectedSha256 = expectedSha256,
          retentionClass = ArtifactRetentionClass.PARTIAL,
          nowMillis = nowMillis,
        )

      val hasTransferBudget = metadata.transferDeadlineAtMillis != null
      if (!hasTransferBudget && metadata.transferRunAttempts != 0) {
        throw FirmwareArtifactStoreException.invalidMetadata()
      }
      val deadlineAtMillis =
        metadata.transferDeadlineAtMillis ?: initialDeadlineAtMillis
      if (deadlineAtMillis <= nowMillis) {
        throw FirmwareArtifactStoreException.deadlineExceeded()
      }
      if (metadata.transferRunAttempts >= maxRunAttempts) {
        throw FirmwareArtifactStoreException.attemptLimitExceeded()
      }
      val runAttempts = metadata.transferRunAttempts + 1

      metadata.taskIdHash = taskIdHash
      metadata.route = route
      metadata.transferDeadlineAtMillis = deadlineAtMillis
      metadata.transferRunAttempts = runAttempts
      metadata.retentionClass = ArtifactRetentionClass.PARTIAL
      metadata.updatedAtMillis = nowMillis
      if (metadata.artifactRef !in lease.artifactRefs) {
        lease.artifactRefs = (lease.artifactRefs + metadata.artifactRef).sorted()
        lease.updatedAtMillis = nowMillis
        writeLease(lease)
      }
      if (leaseRef !in metadata.leaseRefs) {
        metadata.leaseRefs = (metadata.leaseRefs + leaseRef).sorted()
      }
      writeMetadata(metadata)
      metadata
    }

  fun prepareArtifactForDownload(
    artifactRef: String,
    leaseRef: String,
    taskId: String,
    artifactId: String,
    canonicalUrl: String,
    hostname: String,
    route: StoredArtifactRoute,
    expectedSize: Long,
    expectedSha256: String,
    strongETag: String?,
    lastModified: String?,
    ranges: List<LongRange>,
    nowMillis: Long = System.currentTimeMillis(),
  ): StoredArtifactMetadata =
    synchronized(ioLock) {
      ensureDirectories()
      val lease = readLease(leaseRef)
      if (!lease.isActive) {
        throw FirmwareArtifactStoreException.leaseReleased()
      }
      val metadata = readMetadata(artifactRef)
      if (!metadata.matchesTransferIdentity(
          artifactId = artifactId,
          canonicalUrl = canonicalUrl,
          hostname = hostname,
          expectedSize = expectedSize,
          expectedSha256 = expectedSha256,
        ) ||
        metadata.taskIdHash != StoredArtifactMetadata.sha256(taskId) ||
        artifactRef !in lease.artifactRefs ||
        leaseRef !in metadata.leaseRefs
      ) {
        throw FirmwareArtifactStoreException.invalidMetadata()
      }
      if (!metadata.canReusePartial(
          canonicalUrl = canonicalUrl,
          hostname = hostname,
          route = route,
          expectedSize = expectedSize,
          expectedSha256 = expectedSha256,
          strongETag = strongETag,
          lastModified = lastModified,
        )
      ) {
        partialFile(artifactRef).delete()
        metadata.segments.forEach { segment ->
          segmentFile(artifactRef, segment.index).delete()
        }
        metadata.segments = emptyList()
      }
      metadata.route = route
      metadata.strongETag = StoredArtifactMetadata.normalizeStrongETag(strongETag)
      metadata.lastModified = lastModified?.trim()?.takeIf { it.isNotEmpty() }
      metadata.segments = ranges.mapIndexed { index, range ->
        val onDisk = segmentFile(metadata.artifactRef, index).length()
        StoredArtifactSegment(
          index = index,
          start = range.first,
          end = range.last,
          downloadedBytes = onDisk.coerceAtMost(range.last - range.first + 1),
        )
      }
      metadata.retentionClass = ArtifactRetentionClass.PARTIAL
      metadata.updatedAtMillis = nowMillis
      writeMetadata(metadata)
      metadata
    }

  fun findArtifactForTask(
    leaseRef: String,
    taskId: String,
  ): StoredArtifactMetadata? =
    synchronized(ioLock) {
      ensureDirectories()
      val lease = readLease(leaseRef)
      val taskIdHash = StoredArtifactMetadata.sha256(taskId)
      lease.artifactRefs
        .asSequence()
        .mapNotNull { artifactRef ->
          runCatching { readMetadata(artifactRef) }.getOrNull()
        }
        .filter { it.taskIdHash == taskIdHash && it.tombstonedAtMillis == null }
        .maxByOrNull { it.updatedAtMillis }
    }

  fun clearTransferStaging(
    artifactRef: String,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    synchronized(ioLock) {
      ensureDirectories()
      val metadata = readMetadata(artifactRef)
      partialFile(artifactRef).delete()
      metadata.segments.forEach { segment ->
        segmentFile(artifactRef, segment.index).delete()
      }
      metadata.segments = emptyList()
      metadata.retentionClass = ArtifactRetentionClass.STAGING
      metadata.updatedAtMillis = nowMillis
      writeMetadata(metadata)
    }
  }

  fun completeTransferCleanup(
    artifactRef: String,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    synchronized(ioLock) {
      ensureDirectories()
      val metadata = readMetadata(artifactRef)
      metadata.segments.forEach { segment ->
        segmentFile(artifactRef, segment.index).delete()
      }
      metadata.segments = emptyList()
      metadata.updatedAtMillis = nowMillis
      writeMetadata(metadata)
    }
  }

  fun downloadedBytes(artifactRef: String): Long =
    synchronized(ioLock) {
      val metadata = readMetadata(artifactRef)
      val actualSize = metadata.actualSize
      if (finalFile(artifactRef).isFile && actualSize != null) {
        actualSize
      } else {
        metadata.segments.sumOf { segment ->
          segmentFile(artifactRef, segment.index)
            .length()
            .coerceAtMost(segment.end - segment.start + 1)
        }
      }
    }

  fun createLease(
    transactionId: String,
    nowMillis: Long = System.currentTimeMillis(),
  ): StoredArtifactLease =
    synchronized(ioLock) {
      ensureDirectories()
      if (transactionId.isBlank() || transactionId.toByteArray(Charsets.UTF_8).size > 256) {
        throw FirmwareArtifactStoreException.invalidMetadata()
      }
      val lease = StoredArtifactLease.create(
        leaseRef = UUID.randomUUID().toString().lowercase(),
        transactionId = transactionId,
        nowMillis = nowMillis,
      )
      writeLease(lease)
      lease
    }

  fun retainArtifact(
    leaseRef: String,
    artifactRef: String,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    synchronized(ioLock) {
      ensureDirectories()
      val lease = readLease(leaseRef)
      if (!lease.isActive) {
        throw FirmwareArtifactStoreException.leaseReleased()
      }
      val artifact = readMetadata(artifactRef)
      if (artifact.tombstonedAtMillis != null) {
        throw FirmwareArtifactStoreException.artifactNotFound()
      }

      if (artifactRef !in lease.artifactRefs) {
        lease.artifactRefs = (lease.artifactRefs + artifactRef).sorted()
        lease.updatedAtMillis = nowMillis
        writeLease(lease)
      }
      if (leaseRef !in artifact.leaseRefs) {
        artifact.leaseRefs = (artifact.leaseRefs + leaseRef).sorted()
        artifact.updatedAtMillis = nowMillis
        writeMetadata(artifact)
      }
    }
  }

  fun releaseLease(
    leaseRef: String,
    disposition: StoredLeaseDisposition,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    synchronized(ioLock) {
      ensureDirectories()
      val lease = readLease(leaseRef)
      if (!lease.isActive) {
        return@synchronized
      }
      lease.releasedAtMillis = nowMillis
      lease.disposition = disposition
      lease.updatedAtMillis = nowMillis
      writeLease(lease)

      lease.artifactRefs.forEach { artifactRef ->
        val artifact = runCatching { readMetadata(artifactRef) }.getOrNull()
          ?: return@forEach
        artifact.leaseRefs = artifact.leaseRefs.filterNot { it == leaseRef }
        artifact.updatedAtMillis = nowMillis
        writeMetadata(artifact)
      }
    }
  }

  fun reconcileLeases(
    activeLeaseRefs: List<String>,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    synchronized(ioLock) {
      ensureDirectories()
      val normalizedActiveRefs = activeLeaseRefs.map(::validateReference).toSet()
      val leases = loadAllLeases()
      leases.filter { it.isActive && it.leaseRef !in normalizedActiveRefs }
        .forEach { lease ->
          lease.releasedAtMillis = nowMillis
          lease.disposition = StoredLeaseDisposition.SAFE_ABANDONED
          lease.updatedAtMillis = nowMillis
          writeLease(lease)
          lease.artifactRefs.forEach { artifactRef ->
            val artifact = runCatching { readMetadata(artifactRef) }.getOrNull()
              ?: return@forEach
            artifact.leaseRefs = artifact.leaseRefs.filterNot { it == lease.leaseRef }
            artifact.updatedAtMillis = nowMillis
            writeMetadata(artifact)
          }
        }

      val knownRefs = leases.map { it.leaseRef }.toSet()
      normalizedActiveRefs.minus(knownRefs).forEach { missingRef ->
        val recovered = StoredArtifactLease.create(
          leaseRef = missingRef,
          transactionId = "reconciled:$missingRef",
          nowMillis = nowMillis,
        )
        recovered.transactionIdHash = StoredArtifactMetadata.sha256("reconciled")
        writeLease(recovered)
      }
      didReconcileLeases = true
    }
  }

  fun beginActivity(
    artifactRef: String,
    kind: ArtifactActivityKind,
    cancel: (() -> Unit)? = null,
  ): FirmwareArtifactActivityToken {
    val normalizedRef = validateReference(artifactRef)
    val id = UUID.randomUUID()
    activityLock.withLock {
      activitiesByArtifact.getOrPut(normalizedRef) { mutableMapOf() }[id] =
        ActiveActivity(kind = kind, cancel = cancel)
    }
    val token = FirmwareArtifactActivityToken(
      id = id,
      artifactRef = normalizedRef,
      kind = kind,
      store = this,
    )
    try {
      loadArtifact(normalizedRef)
      return token
    } catch (error: Exception) {
      token.close()
      throw error
    }
  }

  fun discardArtifact(
    artifactRef: String,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    synchronized(ioLock) {
      ensureDirectories()
      val metadata = readMetadata(artifactRef)
      if (isArtifactLeased(artifactRef) || hasAnyActivity(artifactRef)) {
        throw FirmwareArtifactStoreException.artifactLeased()
      }
      metadata.tombstonedAtMillis = nowMillis
      metadata.updatedAtMillis = nowMillis
      writeMetadata(metadata)
      removeArtifactDirectory(artifactRef)
    }
  }

  fun quarantineArtifact(
    artifactRef: String,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    synchronized(ioLock) {
      ensureDirectories()
      val normalizedRef = validateReference(artifactRef)
      val metadata = readMetadata(normalizedRef)
      if (hasAnyActivity(normalizedRef)) {
        throw FirmwareArtifactStoreException.artifactLeased()
      }

      metadata.tombstonedAtMillis = nowMillis
      metadata.updatedAtMillis = nowMillis
      writeMetadata(metadata)

      metadata.leaseRefs.toList().forEach { leaseRef ->
        val lease = runCatching { readLease(leaseRef) }.getOrNull()
          ?: return@forEach
        lease.artifactRefs = lease.artifactRefs.filterNot { it == normalizedRef }
        lease.updatedAtMillis = nowMillis
        writeLease(lease)
      }
      metadata.leaseRefs = emptyList()
      writeMetadata(metadata)
      removeArtifactDirectory(normalizedRef)
    }
  }

  fun sweepOrphans(
    nowMillis: Long = System.currentTimeMillis(),
    byteBudget: Long = RetentionPolicy.BYTE_BUDGET,
  ): StoredArtifactSweepSummary =
    synchronized(ioLock) {
      ensureDirectories()
      if (!didReconcileLeases) {
        throw FirmwareArtifactStoreException.reconciliationRequired()
      }

      val protectedRefs = loadAllLeases()
        .filter { it.isActive }
        .flatMapTo(mutableSetOf()) { it.artifactRefs }
      val metadataByRef = loadAllMetadata().associateBy { it.artifactRef }.toMutableMap()
      val storedByRef = metadataByRef.keys.associateWith(::storedBytes)
      val candidateRefs = mutableListOf<String>()

      metadataByRef.values.forEach { metadata ->
        if (metadata.artifactRef in protectedRefs) {
          return@forEach
        }
        if (metadata.tombstonedAtMillis != null) {
          candidateRefs += metadata.artifactRef
          return@forEach
        }
        val age = (nowMillis - maxOf(metadata.updatedAtMillis, metadata.lastAccessedAtMillis))
          .coerceAtLeast(0)
        if (age >= RetentionPolicy.GRACE_PERIOD_MILLIS &&
          age >= RetentionPolicy.ttlMillis(metadata.retentionClass)
        ) {
          candidateRefs += metadata.artifactRef
        }
      }

      var projectedBytes = storedByRef.values.sumOf { it.bytes }
      candidateRefs.forEach { artifactRef ->
        projectedBytes -= storedByRef[artifactRef]?.bytes ?: 0
      }
      val budgetCandidates = metadataByRef.values
        .filter { metadata ->
          metadata.artifactRef !in candidateRefs &&
            metadata.artifactRef !in protectedRefs &&
            nowMillis - maxOf(metadata.updatedAtMillis, metadata.lastAccessedAtMillis) >=
            RetentionPolicy.GRACE_PERIOD_MILLIS
        }
        .sortedBy { maxOf(it.updatedAtMillis, it.lastAccessedAtMillis) }
      budgetCandidates.forEach { metadata ->
        if (projectedBytes > byteBudget) {
          candidateRefs += metadata.artifactRef
          projectedBytes -= storedByRef[metadata.artifactRef]?.bytes ?: 0
        }
      }

      var deletedFiles = 0
      var deletedBytes = 0L
      candidateRefs.forEach { artifactRef ->
        if (artifactRef in protectedRefs || !prepareForDeletion(artifactRef)) {
          return@forEach
        }
        if (isArtifactLeased(artifactRef)) {
          return@forEach
        }
        val metadata = metadataByRef[artifactRef] ?: return@forEach
        metadata.tombstonedAtMillis = metadata.tombstonedAtMillis ?: nowMillis
        metadata.updatedAtMillis = nowMillis
        writeMetadata(metadata)
        val stored = storedBytes(artifactRef)
        if (runCatching { removeArtifactDirectory(artifactRef) }.isSuccess) {
          deletedFiles += stored.files
          deletedBytes += stored.bytes
          metadataByRef.remove(artifactRef)
        }
      }

      val knownRefs = metadataByRef.keys
      artifactsDirectory.listFiles()?.forEach { directory ->
        val artifactRef = directory.name.lowercase()
        if (artifactRef in knownRefs ||
          artifactRef in protectedRefs ||
          runCatching { validateReference(artifactRef) }.isFailure ||
          !prepareForDeletion(artifactRef) ||
          nowMillis - directory.lastModified() < RetentionPolicy.GRACE_PERIOD_MILLIS
        ) {
          return@forEach
        }
        val stored = storedBytes(artifactRef)
        if (runCatching { removeArtifactDirectory(artifactRef) }.isSuccess) {
          deletedFiles += stored.files
          deletedBytes += stored.bytes
        }
      }

      StoredArtifactSweepSummary(
        deletedFiles = deletedFiles,
        deletedBytes = deletedBytes,
      )
    }

  fun finalFile(artifactRef: String): File =
    File(artifactDirectory(artifactRef), "payload.final")

  fun partialFile(artifactRef: String): File =
    File(artifactDirectory(artifactRef), "payload.partial")

  fun segmentFile(artifactRef: String, index: Int): File {
    if (index < 0) {
      throw FirmwareArtifactStoreException.invalidReference()
    }
    val directory = File(artifactDirectory(artifactRef), "segments")
    if (!directory.exists() && !directory.mkdirs()) {
      throw FirmwareArtifactStoreException.storageFailure()
    }
    return File(directory, "segment-$index.partial")
  }

  fun promoteVerifiedStaging(
    artifactRef: String,
    maxBytes: Long,
    nowMillis: Long = System.currentTimeMillis(),
  ): StoredArtifactMetadata =
    synchronized(ioLock) {
      ensureDirectories()
      val metadata = readMetadata(artifactRef)
      if (maxBytes < metadata.expectedSize) {
        throw FirmwareArtifactStoreException.invalidMetadata()
      }
      val staging = partialFile(artifactRef)
      if (!staging.isFile) {
        throw FirmwareArtifactStoreException.storageFailure()
      }
      val actualSize = staging.length()
      if (actualSize > maxBytes) {
        quarantineStaging(staging, artifactRef)
        throw FirmwareArtifactStoreException.sizeLimitExceeded()
      }
      if (actualSize != metadata.expectedSize) {
        quarantineStaging(staging, artifactRef)
        throw FirmwareArtifactStoreException.sizeMismatch()
      }
      val actualSha256 = RangeDownloadLogic.calculateSHA256(staging)
      if (actualSha256 == null ||
        !RangeDownloadLogic.secureHexEquals(actualSha256, metadata.expectedSha256)
      ) {
        quarantineStaging(staging, artifactRef)
        throw FirmwareArtifactStoreException.digestMismatch()
      }

      FileOutputStream(staging, true).use { output ->
        output.fd.sync()
      }
      val finalFile = finalFile(artifactRef)
      try {
        atomicReplace(staging, finalFile)
        synchronizeDirectory(finalFile.parentFile)
      } catch (error: Exception) {
        throw mapStorageError(error)
      }

      metadata.actualSize = actualSize
      metadata.actualSha256 = actualSha256.lowercase()
      metadata.immutableToken = UUID.randomUUID().toString().lowercase()
      metadata.retentionClass = ArtifactRetentionClass.VERIFIED_FINAL
      metadata.segments = emptyList()
      metadata.updatedAtMillis = nowMillis
      metadata.lastAccessedAtMillis = nowMillis
      writeMetadata(metadata)
      metadata
    }

  internal fun releaseActivity(id: UUID, artifactRef: String) {
    activityLock.withLock {
      activitiesByArtifact[artifactRef]?.remove(id)
      if (activitiesByArtifact[artifactRef]?.isEmpty() == true) {
        activitiesByArtifact.remove(artifactRef)
      }
      activityChanged.signalAll()
    }
  }

  private fun ensureDirectories() {
    if ((!rootDirectory.exists() && !rootDirectory.mkdirs()) ||
      (!artifactsDirectory.exists() && !artifactsDirectory.mkdirs()) ||
      (!leasesDirectory.exists() && !leasesDirectory.mkdirs())
    ) {
      throw FirmwareArtifactStoreException.storageFailure()
    }
  }

  private fun validateReference(reference: String): String {
    val normalized = runCatching { UUID.fromString(reference).toString().lowercase() }
      .getOrElse { throw FirmwareArtifactStoreException.invalidReference() }
    if (!normalized.equals(reference, ignoreCase = true)) {
      throw FirmwareArtifactStoreException.invalidReference()
    }
    return normalized
  }

  private fun artifactDirectory(artifactRef: String): File =
    File(artifactsDirectory, validateReference(artifactRef))

  private fun metadataFile(artifactRef: String): File =
    File(artifactDirectory(artifactRef), "metadata.properties")

  private fun leaseFile(leaseRef: String): File =
    File(leasesDirectory, "${validateReference(leaseRef)}.properties")

  private fun writeMetadata(metadata: StoredArtifactMetadata) {
    if (metadata.schemaVersion != StoredArtifactMetadata.CURRENT_SCHEMA_VERSION ||
      metadata.artifactRef != validateReference(metadata.artifactRef) ||
      (metadata.transferDeadlineAtMillis == null && metadata.transferRunAttempts != 0) ||
      metadata.transferDeadlineAtMillis?.let {
        it <= 0 || metadata.transferRunAttempts <= 0
      } == true
    ) {
      throw FirmwareArtifactStoreException.invalidMetadata()
    }
    atomicWriteProperties(metadataFile(metadata.artifactRef), metadata.toProperties())
  }

  private fun readMetadata(artifactRef: String): StoredArtifactMetadata {
    val file = metadataFile(artifactRef)
    if (!file.isFile) {
      throw FirmwareArtifactStoreException.artifactNotFound()
    }
    return try {
      StoredArtifactMetadata.fromProperties(readProperties(file)).also { metadata ->
        if (metadata.schemaVersion != StoredArtifactMetadata.CURRENT_SCHEMA_VERSION ||
          metadata.artifactRef != validateReference(artifactRef)
        ) {
          throw FirmwareArtifactStoreException.invalidMetadata()
        }
      }
    } catch (error: FirmwareArtifactStoreException) {
      throw error
    } catch (_: Exception) {
      throw FirmwareArtifactStoreException.invalidMetadata()
    }
  }

  private fun writeLease(lease: StoredArtifactLease) {
    if (lease.schemaVersion != StoredArtifactLease.CURRENT_SCHEMA_VERSION ||
      lease.leaseRef != validateReference(lease.leaseRef)
    ) {
      throw FirmwareArtifactStoreException.invalidMetadata()
    }
    atomicWriteProperties(leaseFile(lease.leaseRef), lease.toProperties())
  }

  private fun readLease(leaseRef: String): StoredArtifactLease {
    val file = leaseFile(leaseRef)
    if (!file.isFile) {
      throw FirmwareArtifactStoreException.leaseNotFound()
    }
    return try {
      StoredArtifactLease.fromProperties(readProperties(file)).also { lease ->
        if (lease.schemaVersion != StoredArtifactLease.CURRENT_SCHEMA_VERSION ||
          lease.leaseRef != validateReference(leaseRef)
        ) {
          throw FirmwareArtifactStoreException.invalidMetadata()
        }
      }
    } catch (error: FirmwareArtifactStoreException) {
      throw error
    } catch (_: Exception) {
      throw FirmwareArtifactStoreException.invalidMetadata()
    }
  }

  private fun loadAllMetadata(): List<StoredArtifactMetadata> =
    artifactsDirectory.listFiles()
      ?.mapNotNull { directory ->
        runCatching { readMetadata(directory.name.lowercase()) }.getOrNull()
      }
      .orEmpty()

  private fun loadAllLeases(): List<StoredArtifactLease> =
    leasesDirectory.listFiles()
      ?.mapNotNull { file ->
        runCatching { readLease(file.nameWithoutExtension.lowercase()) }.getOrNull()
      }
      .orEmpty()

  private fun isArtifactLeased(artifactRef: String): Boolean =
    loadAllLeases().any { lease ->
      lease.isActive && artifactRef in lease.artifactRefs
    }

  private fun hasAnyActivity(artifactRef: String): Boolean =
    activityLock.withLock {
      activitiesByArtifact[artifactRef]?.isNotEmpty() == true
    }

  private fun prepareForDeletion(artifactRef: String): Boolean {
    val callbacks = activityLock.withLock {
      val activities = activitiesByArtifact[artifactRef].orEmpty()
      if (activities.values.any { it.kind != ArtifactActivityKind.WORKER }) {
        return false
      }
      activities.values.mapNotNull { it.cancel }
    }
    callbacks.forEach { cancel -> cancel() }

    val deadlineNanos =
      System.nanoTime() + RetentionPolicy.WORKER_CANCELLATION_TIMEOUT_MILLIS * 1_000_000
    activityLock.withLock {
      while (activitiesByArtifact[artifactRef]?.isNotEmpty() == true) {
        val remaining = deadlineNanos - System.nanoTime()
        if (remaining <= 0) {
          return false
        }
        activityChanged.awaitNanos(remaining)
      }
    }
    return true
  }

  private fun removeArtifactDirectory(artifactRef: String) {
    val directory = artifactDirectory(artifactRef)
    if (directory.exists() && !directory.deleteRecursively()) {
      throw FirmwareArtifactStoreException.storageFailure()
    }
  }

  private data class StoredBytes(val files: Int, val bytes: Long)

  private fun storedBytes(artifactRef: String): StoredBytes {
    val directory = runCatching { artifactDirectory(artifactRef) }.getOrNull()
      ?: return StoredBytes(0, 0)
    var files = 0
    var bytes = 0L
    directory.walkTopDown().filter { it.isFile }.forEach { file ->
      files += 1
      bytes += file.length()
    }
    return StoredBytes(files, bytes)
  }

  private fun atomicWriteProperties(file: File, properties: Properties) {
    val parent = file.parentFile ?: throw FirmwareArtifactStoreException.storageFailure()
    if (!parent.exists() && !parent.mkdirs()) {
      throw FirmwareArtifactStoreException.storageFailure()
    }
    val temporary = File(parent, ".${file.name}.${UUID.randomUUID()}.tmp")
    try {
      FileOutputStream(temporary).use { output ->
        properties.store(output, null)
        output.fd.sync()
      }
      atomicReplace(temporary, file)
    } catch (error: Exception) {
      temporary.delete()
      throw mapStorageError(error)
    }
  }

  private fun atomicReplace(source: File, destination: File) {
    if (System.getProperty("java.vm.name") == "Dalvik") {
      Os.rename(source.absolutePath, destination.absolutePath)
    } else {
      Files.move(
        source.toPath(),
        destination.toPath(),
        StandardCopyOption.ATOMIC_MOVE,
        StandardCopyOption.REPLACE_EXISTING,
      )
    }
  }

  private fun quarantineStaging(staging: File, artifactRef: String) {
    if (!staging.exists()) return
    val rejected = File(
      artifactDirectory(artifactRef),
      "payload.rejected-${UUID.randomUUID().toString().lowercase()}",
    )
    if (!staging.renameTo(rejected)) {
      throw FirmwareArtifactStoreException.storageFailure()
    }
    synchronizeDirectory(rejected.parentFile)
  }

  private fun synchronizeDirectory(directory: File?) {
    val target = directory ?: throw FirmwareArtifactStoreException.storageFailure()
    if (System.getProperty("java.vm.name") == "Dalvik") {
      val descriptor = Os.open(target.absolutePath, OsConstants.O_RDONLY, 0)
      try {
        Os.fsync(descriptor)
      } finally {
        Os.close(descriptor)
      }
    } else {
      Files.newByteChannel(target.toPath(), StandardOpenOption.READ).use { channel ->
        if (channel is java.nio.channels.FileChannel) {
          channel.force(true)
        }
      }
    }
  }

  private fun readProperties(file: File): Properties =
    Properties().apply {
      FileInputStream(file).use(::load)
    }

  private fun mapStorageError(error: Exception): FirmwareArtifactStoreException =
    if (error is FirmwareArtifactStoreException) {
      error
    } else {
      FirmwareArtifactStoreException.storageFailure()
    }

  companion object {
    @Volatile
    private var processStore: FirmwareArtifactStore? = null

    fun processInstance(rootDirectory: File): FirmwareArtifactStore {
      val canonicalRoot = rootDirectory.canonicalFile
      processStore?.let { existing ->
        if (existing.rootDirectory.canonicalFile != canonicalRoot) {
          throw FirmwareArtifactStoreException.storageFailure()
        }
        return existing
      }
      return synchronized(this) {
        processStore?.also { existing ->
          if (existing.rootDirectory.canonicalFile != canonicalRoot) {
            throw FirmwareArtifactStoreException.storageFailure()
          }
        } ?: FirmwareArtifactStore(canonicalRoot).also { processStore = it }
      }
    }

    fun resetProcessInstanceForTests() {
      synchronized(this) {
        processStore = null
      }
    }
  }
}

private fun StoredArtifactMetadata.toProperties(): Properties =
  Properties().apply {
    setProperty("schemaVersion", schemaVersion.toString())
    setProperty("artifactRef", artifactRef)
    setProperty("artifactId", artifactId)
    setOptionalProperty("taskIdHash", taskIdHash)
    setProperty("canonicalUrlHash", canonicalUrlHash)
    setProperty("hostname", hostname)
    setProperty("route", route.wireName)
    setProperty("expectedSize", expectedSize.toString())
    setProperty("expectedSha256", expectedSha256)
    setOptionalProperty("actualSize", actualSize?.toString())
    setOptionalProperty("actualSha256", actualSha256)
    setOptionalProperty("immutableToken", immutableToken)
    setOptionalProperty("strongETag", strongETag)
    setOptionalProperty("lastModified", lastModified)
    setOptionalProperty("transferDeadlineAtMillis", transferDeadlineAtMillis?.toString())
    setProperty("transferRunAttempts", transferRunAttempts.toString())
    setProperty("retentionClass", retentionClass.wireName)
    setProperty("leaseRefs", leaseRefs.joinToString(","))
    setProperty("createdAtMillis", createdAtMillis.toString())
    setProperty("updatedAtMillis", updatedAtMillis.toString())
    setProperty("lastAccessedAtMillis", lastAccessedAtMillis.toString())
    setOptionalProperty("tombstonedAtMillis", tombstonedAtMillis?.toString())
    setProperty("segmentCount", segments.size.toString())
    segments.forEachIndexed { position, segment ->
      setProperty("segment.$position.index", segment.index.toString())
      setProperty("segment.$position.start", segment.start.toString())
      setProperty("segment.$position.end", segment.end.toString())
      setProperty("segment.$position.downloadedBytes", segment.downloadedBytes.toString())
    }
  }

private fun StoredArtifactLease.toProperties(): Properties =
  Properties().apply {
    setProperty("schemaVersion", schemaVersion.toString())
    setProperty("leaseRef", leaseRef)
    setProperty("transactionIdHash", transactionIdHash)
    setProperty("artifactRefs", artifactRefs.joinToString(","))
    setProperty("createdAtMillis", createdAtMillis.toString())
    setProperty("updatedAtMillis", updatedAtMillis.toString())
    setOptionalProperty("releasedAtMillis", releasedAtMillis?.toString())
    setOptionalProperty("disposition", disposition?.wireName)
  }

private fun StoredArtifactMetadata.Companion.fromProperties(
  properties: Properties,
): StoredArtifactMetadata {
  val segmentCount = properties.requiredInt("segmentCount")
  if (segmentCount < 0 || segmentCount > 1024) {
    throw FirmwareArtifactStoreException.invalidMetadata()
  }
  val segments = (0 until segmentCount).map { position ->
    StoredArtifactSegment(
      index = properties.requiredInt("segment.$position.index"),
      start = properties.requiredLong("segment.$position.start"),
      end = properties.requiredLong("segment.$position.end"),
      downloadedBytes = properties.requiredLong("segment.$position.downloadedBytes"),
    )
  }
  val transferDeadlineAtMillis = properties.optionalLong("transferDeadlineAtMillis")
  val transferRunAttempts = properties.requiredInt("transferRunAttempts")
  if ((transferDeadlineAtMillis == null && transferRunAttempts != 0) ||
    (transferDeadlineAtMillis != null &&
      (transferDeadlineAtMillis <= 0 || transferRunAttempts <= 0))
  ) {
    throw FirmwareArtifactStoreException.invalidMetadata()
  }
  return StoredArtifactMetadata(
    schemaVersion = properties.requiredInt("schemaVersion"),
    artifactRef = properties.required("artifactRef"),
    artifactId = properties.required("artifactId"),
    taskIdHash = properties.optional("taskIdHash"),
    canonicalUrlHash = properties.required("canonicalUrlHash"),
    hostname = properties.required("hostname"),
    route = StoredArtifactRoute.fromWireName(properties.required("route")),
    expectedSize = properties.requiredLong("expectedSize"),
    expectedSha256 = properties.required("expectedSha256"),
    actualSize = properties.optionalLong("actualSize"),
    actualSha256 = properties.optional("actualSha256"),
    immutableToken = properties.optional("immutableToken"),
    strongETag = properties.optional("strongETag"),
    lastModified = properties.optional("lastModified"),
    segments = segments,
    transferDeadlineAtMillis = transferDeadlineAtMillis,
    transferRunAttempts = transferRunAttempts,
    retentionClass = ArtifactRetentionClass.fromWireName(
      properties.required("retentionClass"),
    ),
    leaseRefs = properties.csv("leaseRefs"),
    createdAtMillis = properties.requiredLong("createdAtMillis"),
    updatedAtMillis = properties.requiredLong("updatedAtMillis"),
    lastAccessedAtMillis = properties.requiredLong("lastAccessedAtMillis"),
    tombstonedAtMillis = properties.optionalLong("tombstonedAtMillis"),
  )
}

private fun StoredArtifactLease.Companion.fromProperties(
  properties: Properties,
): StoredArtifactLease =
  StoredArtifactLease(
    schemaVersion = properties.requiredInt("schemaVersion"),
    leaseRef = properties.required("leaseRef"),
    transactionIdHash = properties.required("transactionIdHash"),
    artifactRefs = properties.csv("artifactRefs"),
    createdAtMillis = properties.requiredLong("createdAtMillis"),
    updatedAtMillis = properties.requiredLong("updatedAtMillis"),
    releasedAtMillis = properties.optionalLong("releasedAtMillis"),
    disposition = properties.optional("disposition")?.let(
      StoredLeaseDisposition::fromWireName,
    ),
  )

private fun Properties.setOptionalProperty(key: String, value: String?) {
  if (value == null) {
    remove(key)
  } else {
    setProperty(key, value)
  }
}

private fun Properties.required(key: String): String =
  getProperty(key)?.takeIf { it.isNotEmpty() }
    ?: throw FirmwareArtifactStoreException.invalidMetadata()

private fun Properties.optional(key: String): String? =
  getProperty(key)?.takeIf { it.isNotEmpty() }

private fun Properties.requiredInt(key: String): Int =
  required(key).toIntOrNull() ?: throw FirmwareArtifactStoreException.invalidMetadata()

private fun Properties.requiredLong(key: String): Long =
  required(key).toLongOrNull() ?: throw FirmwareArtifactStoreException.invalidMetadata()

private fun Properties.optionalLong(key: String): Long? =
  optional(key)?.toLongOrNull() ?: optional(key)?.let {
    throw FirmwareArtifactStoreException.invalidMetadata()
  }

private fun Properties.csv(key: String): List<String> =
  optional(key)?.split(",")?.filter { it.isNotEmpty() }.orEmpty()
