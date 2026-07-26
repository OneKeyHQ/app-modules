package com.margelo.nitro.reactnativerangedownloader

import java.security.MessageDigest

internal enum class StoredArtifactRoute(val wireName: String) {
  DOMAIN("domain"),
  PINNED_IP("pinnedIp");

  companion object {
    fun fromWireName(value: String): StoredArtifactRoute =
      entries.firstOrNull { it.wireName == value }
        ?: throw FirmwareArtifactStoreException.invalidMetadata()
  }
}

internal enum class ArtifactRetentionClass(val wireName: String) {
  PARTIAL("partial"),
  STAGING("staging"),
  VERIFIED_FINAL("verifiedFinal"),
  ARCHIVE("archive"),
  MATERIALIZED_CHILD("materializedChild");

  companion object {
    fun fromWireName(value: String): ArtifactRetentionClass =
      entries.firstOrNull { it.wireName == value }
        ?: throw FirmwareArtifactStoreException.invalidMetadata()
  }
}

internal enum class ArtifactActivityKind {
  WORKER,
  READER,
  MATERIALIZER,
}

internal enum class StoredLeaseDisposition(val wireName: String) {
  COMPLETED("completed"),
  SAFE_CANCELLED("safeCancelled"),
  SAFE_ABANDONED("safeAbandoned");

  companion object {
    fun fromWireName(value: String): StoredLeaseDisposition =
      entries.firstOrNull { it.wireName == value }
        ?: throw FirmwareArtifactStoreException.invalidMetadata()
  }
}

internal data class StoredArtifactSegment(
  var index: Int,
  var start: Long,
  var end: Long,
  var downloadedBytes: Long,
)

internal data class StoredArtifactMetadata(
  var schemaVersion: Int = CURRENT_SCHEMA_VERSION,
  var artifactRef: String,
  var artifactId: String,
  var taskIdHash: String? = null,
  var canonicalUrlHash: String,
  var hostname: String,
  var route: StoredArtifactRoute,
  var expectedSize: Long,
  var expectedSha256: String,
  var actualSize: Long? = null,
  var actualSha256: String? = null,
  var immutableToken: String? = null,
  var strongETag: String? = null,
  var lastModified: String? = null,
  var segments: List<StoredArtifactSegment> = emptyList(),
  var transferDeadlineAtMillis: Long? = null,
  var transferRunAttempts: Int = 0,
  var retentionClass: ArtifactRetentionClass,
  var leaseRefs: List<String> = emptyList(),
  var createdAtMillis: Long,
  var updatedAtMillis: Long,
  var lastAccessedAtMillis: Long,
  var tombstonedAtMillis: Long? = null,
) {
  fun canReusePartial(
    canonicalUrl: String,
    hostname: String,
    route: StoredArtifactRoute,
    expectedSize: Long,
    expectedSha256: String,
    strongETag: String?,
    lastModified: String?,
  ): Boolean {
    if (tombstonedAtMillis != null ||
      schemaVersion != CURRENT_SCHEMA_VERSION ||
      actualSize != null ||
      actualSha256 != null ||
      immutableToken != null ||
      retentionClass !in setOf(
        ArtifactRetentionClass.PARTIAL,
        ArtifactRetentionClass.STAGING,
      ) ||
      canonicalUrlHash != sha256(canonicalUrl) ||
      this.hostname != hostname.lowercase() ||
      this.expectedSize != expectedSize ||
      this.expectedSha256 != expectedSha256.lowercase()
    ) {
      return false
    }

    val currentETag = normalizeStrongETag(this.strongETag)
    val proposedETag = normalizeStrongETag(strongETag)
    val currentLastModified = normalizeValidator(this.lastModified)
    val proposedLastModified = normalizeValidator(lastModified)
    if (currentETag != null || proposedETag != null) {
      return currentETag == proposedETag
    }
    if (currentLastModified != null || proposedLastModified != null) {
      return currentLastModified == proposedLastModified
    }
    return isTrustedSha256(expectedSha256)
  }

  fun matchesTransferIdentity(
    artifactId: String,
    canonicalUrl: String,
    hostname: String,
    expectedSize: Long,
    expectedSha256: String,
  ): Boolean =
    tombstonedAtMillis == null &&
      schemaVersion == CURRENT_SCHEMA_VERSION &&
      actualSize == null &&
      actualSha256 == null &&
      immutableToken == null &&
      retentionClass in setOf(
        ArtifactRetentionClass.PARTIAL,
        ArtifactRetentionClass.STAGING,
      ) &&
      this.artifactId == artifactId &&
      canonicalUrlHash == sha256(canonicalUrl) &&
      this.hostname == hostname.lowercase() &&
      this.expectedSize == expectedSize &&
      this.expectedSha256 == expectedSha256.lowercase()

  companion object {
    const val CURRENT_SCHEMA_VERSION = 2

    fun create(
      artifactRef: String,
      artifactId: String,
      canonicalUrl: String,
      hostname: String,
      route: StoredArtifactRoute,
      expectedSize: Long,
      expectedSha256: String,
      retentionClass: ArtifactRetentionClass,
      nowMillis: Long,
    ): StoredArtifactMetadata =
      StoredArtifactMetadata(
        artifactRef = artifactRef,
        artifactId = artifactId,
        canonicalUrlHash = sha256(canonicalUrl),
        hostname = hostname.lowercase(),
        route = route,
        expectedSize = expectedSize,
        expectedSha256 = expectedSha256.lowercase(),
        retentionClass = retentionClass,
        createdAtMillis = nowMillis,
        updatedAtMillis = nowMillis,
        lastAccessedAtMillis = nowMillis,
      )

    fun normalizeStrongETag(value: String?): String? {
      val normalized = normalizeValidator(value) ?: return null
      return normalized.takeUnless { it.startsWith("w/", ignoreCase = true) }
    }

    fun isTrustedSha256(value: String): Boolean =
      value.length == 64 && value.all { character ->
        character in '0'..'9' || character in 'a'..'f' || character in 'A'..'F'
      }

    fun sha256(value: String): String =
      MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray(Charsets.UTF_8))
        .joinToString("") { byte -> "%02x".format(byte) }

    private fun normalizeValidator(value: String?): String? =
      value?.trim()?.takeIf { it.isNotEmpty() }
  }
}

internal data class StoredArtifactLease(
  var schemaVersion: Int = CURRENT_SCHEMA_VERSION,
  var leaseRef: String,
  var transactionIdHash: String,
  var artifactRefs: List<String> = emptyList(),
  var createdAtMillis: Long,
  var updatedAtMillis: Long,
  var releasedAtMillis: Long? = null,
  var disposition: StoredLeaseDisposition? = null,
) {
  val isActive: Boolean
    get() = releasedAtMillis == null

  companion object {
    const val CURRENT_SCHEMA_VERSION = 1

    fun create(leaseRef: String, transactionId: String, nowMillis: Long): StoredArtifactLease =
      StoredArtifactLease(
        leaseRef = leaseRef,
        transactionIdHash = StoredArtifactMetadata.sha256(transactionId),
        createdAtMillis = nowMillis,
        updatedAtMillis = nowMillis,
      )
  }
}

internal data class StoredArtifactSweepSummary(
  val deletedFiles: Int,
  val deletedBytes: Long,
)

internal class FirmwareArtifactStoreException private constructor(
  val code: String,
) : Exception(code) {
  companion object {
    fun invalidReference() = FirmwareArtifactStoreException("ARTIFACT_INVALID_REF")
    fun invalidRequest() = FirmwareArtifactStoreException("ARTIFACT_INVALID_REQUEST")
    fun invalidMetadata() = FirmwareArtifactStoreException("ARTIFACT_INVALID_METADATA")
    fun artifactNotFound() = FirmwareArtifactStoreException("ARTIFACT_NOT_FOUND")
    fun leaseNotFound() = FirmwareArtifactStoreException("ARTIFACT_LEASE_NOT_FOUND")
    fun leaseReleased() = FirmwareArtifactStoreException("ARTIFACT_LEASE_RELEASED")
    fun artifactLeased() = FirmwareArtifactStoreException("ARTIFACT_LEASED")
    fun deadlineExceeded() =
      FirmwareArtifactStoreException("ARTIFACT_DEADLINE_EXCEEDED")

    fun attemptLimitExceeded() =
      FirmwareArtifactStoreException("ARTIFACT_ATTEMPT_LIMIT_EXCEEDED")

    fun reconciliationRequired() =
      FirmwareArtifactStoreException("ARTIFACT_RECONCILIATION_REQUIRED")

    fun sizeLimitExceeded() =
      FirmwareArtifactStoreException("ARTIFACT_SIZE_LIMIT_EXCEEDED")

    fun sizeMismatch() = FirmwareArtifactStoreException("ARTIFACT_SIZE_MISMATCH")
    fun digestMismatch() = FirmwareArtifactStoreException("ARTIFACT_DIGEST_MISMATCH")
    fun storageFailure() = FirmwareArtifactStoreException("ARTIFACT_STORAGE_FAILED")
  }

  val retryable: Boolean?
    get() = when (code) {
      "ARTIFACT_DEADLINE_EXCEEDED",
      "ARTIFACT_ATTEMPT_LIMIT_EXCEEDED",
      -> false
      else -> null
    }
}
