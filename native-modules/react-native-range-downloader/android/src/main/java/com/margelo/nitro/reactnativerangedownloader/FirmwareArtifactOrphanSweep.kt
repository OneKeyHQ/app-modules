package com.margelo.nitro.reactnativerangedownloader

import java.io.File

internal const val FIRMWARE_ARTIFACT_FINAL_GRACE_MS = 24L * 60 * 60 * 1000
internal const val FIRMWARE_ARTIFACT_PARTIAL_GRACE_MS = 7L * 24 * 60 * 60 * 1000
internal const val FIRMWARE_ARTIFACT_SCRATCH_GRACE_MS =
  FIRMWARE_ARTIFACT_PARTIAL_GRACE_MS

private val firmwareArtifactSha256Pattern = Regex("^[a-f0-9]{64}$")
private val firmwareArchiveScratchPattern = Regex(
  "^archive-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
    "[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
)
private val firmwarePromoteScratchPattern = Regex(
  "^\\.promote-[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
    "[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
)

private fun File.isSymbolicLinkEntry(): Boolean = try {
  val canonicalParent = parentFile?.canonicalFile
  val entryFromCanonicalParent = canonicalParent?.let { File(it, name) } ?: this
  entryFromCanonicalParent.canonicalFile != entryFromCanonicalParent.absoluteFile
} catch (_: Exception) {
  true
}

private fun firmwareArtifactEntrySize(entry: File): Long {
  if (entry.isSymbolicLinkEntry()) return 0
  if (entry.isFile) return entry.length()
  if (!entry.isDirectory) return 0
  return entry.listFiles()?.sumOf(::firmwareArtifactEntrySize) ?: 0
}

private fun deleteFirmwareArtifactEntry(entry: File): Boolean {
  if (entry.isSymbolicLinkEntry()) return entry.delete()
  if (entry.isDirectory) {
    val children = entry.listFiles() ?: return false
    if (children.any { !deleteFirmwareArtifactEntry(it) }) return false
  }
  return entry.delete()
}

private fun firmwareArtifactEntryExceededGrace(
  entry: File,
  nowMs: Long,
  graceMs: Long,
): Boolean {
  val modifiedAt = entry.lastModified()
  return modifiedAt > 0 && modifiedAt <= nowMs && nowMs - modifiedAt >= graceMs
}

internal fun sweepFirmwareArtifactOrphansAtRoot(
  root: File,
  retainedSha256: Set<String>,
  activeSha256: Set<String>,
  openPaths: Set<String>,
  nowMs: Long = System.currentTimeMillis(),
): Pair<Int, Long> {
  var deletedFiles = 0
  var deletedBytes = 0L
  root.listFiles()?.forEach { entry ->
    val isSymbolicLink = entry.isSymbolicLinkEntry()
    val isArchiveScratch = firmwareArchiveScratchPattern.matches(entry.name)
    val isPromoteScratch = firmwarePromoteScratchPattern.matches(entry.name)
    val isScratchCandidate = !isSymbolicLink &&
      ((isArchiveScratch && entry.isDirectory) ||
        (isPromoteScratch && entry.isFile))
    if (isScratchCandidate) {
      if (
        firmwareArtifactEntryExceededGrace(
          entry,
          nowMs,
          FIRMWARE_ARTIFACT_SCRATCH_GRACE_MS,
        )
      ) {
        val size = firmwareArtifactEntrySize(entry)
        if (deleteFirmwareArtifactEntry(entry)) {
          deletedFiles += 1
          deletedBytes += size
        }
      }
      return@forEach
    }

    if (!entry.isFile || isSymbolicLink || entry.name.length < 64) return@forEach
    val sha256 = entry.name.take(64)
    if (
      !firmwareArtifactSha256Pattern.matches(sha256) ||
      sha256 in retainedSha256 ||
      sha256 in activeSha256 ||
      entry.absolutePath in openPaths
    ) {
      return@forEach
    }
    val grace = if (entry.name.endsWith(".bin")) {
      FIRMWARE_ARTIFACT_FINAL_GRACE_MS
    } else if (entry.name.endsWith(".partial")) {
      FIRMWARE_ARTIFACT_PARTIAL_GRACE_MS
    } else {
      return@forEach
    }
    if (!firmwareArtifactEntryExceededGrace(entry, nowMs, grace)) return@forEach
    val size = entry.length()
    if (entry.delete()) {
      deletedFiles += 1
      deletedBytes += size
    }
  }
  return deletedFiles to deletedBytes
}
