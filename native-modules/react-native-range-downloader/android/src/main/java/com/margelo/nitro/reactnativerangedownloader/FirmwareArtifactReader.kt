package com.margelo.nitro.reactnativerangedownloader

import java.io.RandomAccessFile
import java.util.UUID

internal data class FirmwareArtifactReaderOpenResult(
  val readerId: String,
  val size: Long,
  val immutableToken: String,
)

internal class FirmwareArtifactReaderException private constructor(
  val code: String,
) : Exception(code) {
  companion object {
    fun notFinal() = FirmwareArtifactReaderException("ARTIFACT_NOT_FINAL")
    fun immutableTokenMismatch() =
      FirmwareArtifactReaderException("ARTIFACT_IMMUTABLE_TOKEN_MISMATCH")
    fun readerNotFound() = FirmwareArtifactReaderException("ARTIFACT_READER_NOT_FOUND")
    fun readerBusy() = FirmwareArtifactReaderException("ARTIFACT_READER_BUSY")
    fun invalidRange() = FirmwareArtifactReaderException("ARTIFACT_READ_INVALID_RANGE")
    fun readLimitExceeded() =
      FirmwareArtifactReaderException("ARTIFACT_READ_LIMIT_EXCEEDED")
    fun emptyRead() = FirmwareArtifactReaderException("ARTIFACT_READ_EMPTY")
    fun shortRead() = FirmwareArtifactReaderException("ARTIFACT_READ_SHORT")
    fun storageFailure() = FirmwareArtifactReaderException("ARTIFACT_STORAGE_FAILED")
  }
}

internal class FirmwareArtifactReader(
  private val store: FirmwareArtifactStore,
) : AutoCloseable {
  private class ReaderState(
    val readerId: String,
    val artifactRef: String,
    val immutableToken: String,
    val size: Long,
    val file: RandomAccessFile,
    val activityToken: FirmwareArtifactActivityToken,
  ) {
    var isReading = false
    var closeRequested = false

    fun close() {
      runCatching { file.close() }
      activityToken.close()
    }
  }

  private val lock = Any()
  private val readers = mutableMapOf<String, ReaderState>()

  fun open(
    artifactRef: String,
    immutableToken: String,
  ): FirmwareArtifactReaderOpenResult {
    val activityToken = store.beginActivity(
      artifactRef = artifactRef,
      kind = ArtifactActivityKind.READER,
    )
    try {
      val metadata = store.loadArtifact(activityToken.artifactRef)
      val actualSize = metadata.actualSize
      val actualSha256 = metadata.actualSha256
      val storedToken = metadata.immutableToken
      val isFinalClass = metadata.retentionClass in setOf(
        ArtifactRetentionClass.VERIFIED_FINAL,
        ArtifactRetentionClass.ARCHIVE,
        ArtifactRetentionClass.MATERIALIZED_CHILD,
      )
      if (metadata.tombstonedAtMillis != null ||
        !isFinalClass ||
        actualSize == null ||
        actualSize <= 0 ||
        actualSize != metadata.expectedSize ||
        actualSha256 == null ||
        !RangeDownloadLogic.secureHexEquals(actualSha256, metadata.expectedSha256) ||
        storedToken == null
      ) {
        throw FirmwareArtifactReaderException.notFinal()
      }
      if (immutableToken != storedToken) {
        throw FirmwareArtifactReaderException.immutableTokenMismatch()
      }

      val finalFile = store.finalFile(metadata.artifactRef)
      if (!finalFile.isFile || finalFile.length() != actualSize) {
        throw FirmwareArtifactReaderException.notFinal()
      }
      val randomAccessFile = RandomAccessFile(finalFile, "r")
      val readerId = UUID.randomUUID().toString().lowercase()
      val state = ReaderState(
        readerId = readerId,
        artifactRef = metadata.artifactRef,
        immutableToken = storedToken,
        size = actualSize,
        file = randomAccessFile,
        activityToken = activityToken,
      )
      synchronized(lock) {
        readers[readerId] = state
      }
      store.updateArtifact(metadata.artifactRef) { artifact ->
        artifact.lastAccessedAtMillis = System.currentTimeMillis()
      }
      return FirmwareArtifactReaderOpenResult(
        readerId = readerId,
        size = actualSize,
        immutableToken = storedToken,
      )
    } catch (error: Exception) {
      activityToken.close()
      throw mapOpenError(error)
    }
  }

  fun read(
    readerId: String,
    offset: Double,
    length: Double,
  ): ByteArray {
    val normalizedReaderId = validateReaderId(readerId)
    val validatedOffset = validateSafeInteger(offset)
    val validatedLength = validateSafeInteger(length)
    if (validatedLength > MAX_READ_BYTES) {
      throw FirmwareArtifactReaderException.readLimitExceeded()
    }

    val state = synchronized(lock) {
      val current = readers[normalizedReaderId]
        ?: throw FirmwareArtifactReaderException.readerNotFound()
      if (current.closeRequested) {
        throw FirmwareArtifactReaderException.readerNotFound()
      }
      if (current.isReading) {
        throw FirmwareArtifactReaderException.readerBusy()
      }
      if (validatedOffset > current.size ||
        validatedLength > current.size - validatedOffset
      ) {
        throw FirmwareArtifactReaderException.invalidRange()
      }
      if (validatedLength == 0L && validatedOffset != current.size) {
        throw FirmwareArtifactReaderException.emptyRead()
      }
      current.isReading = true
      current
    }

    try {
      if (validatedLength == 0L) {
        return ByteArray(0)
      }
      val bytes = ByteArray(validatedLength.toInt())
      state.file.seek(validatedOffset)
      val read = state.file.read(bytes)
      if (read != bytes.size) {
        throw FirmwareArtifactReaderException.shortRead()
      }
      return bytes
    } catch (error: FirmwareArtifactReaderException) {
      throw error
    } catch (error: Exception) {
      throw FirmwareArtifactReaderException.storageFailure()
    } finally {
      finishRead(state)
    }
  }

  fun close(readerId: String) {
    val normalizedReaderId = validateReaderId(readerId)
    val stateToClose = synchronized(lock) {
      val state = readers[normalizedReaderId] ?: return
      if (state.isReading) {
        state.closeRequested = true
        null
      } else {
        readers.remove(normalizedReaderId)
        state
      }
    }
    stateToClose?.close()
  }

  override fun close() {
    val statesToClose = synchronized(lock) {
      val idle = readers.values.filterNot { it.isReading }
      readers.values.filter { it.isReading }.forEach { it.closeRequested = true }
      idle.forEach { readers.remove(it.readerId) }
      idle
    }
    statesToClose.forEach { it.close() }
  }

  private fun finishRead(state: ReaderState) {
    val shouldClose = synchronized(lock) {
      state.isReading = false
      if (state.closeRequested) {
        readers.remove(state.readerId)
        true
      } else {
        false
      }
    }
    if (shouldClose) {
      state.close()
    }
  }

  private fun validateReaderId(readerId: String): String {
    val normalized = runCatching { UUID.fromString(readerId).toString().lowercase() }
      .getOrElse { throw FirmwareArtifactReaderException.readerNotFound() }
    if (!normalized.equals(readerId, ignoreCase = true)) {
      throw FirmwareArtifactReaderException.readerNotFound()
    }
    return normalized
  }

  private fun validateSafeInteger(value: Double): Long {
    if (!value.isFinite() ||
      value < 0 ||
      value > MAXIMUM_SAFE_INTEGER ||
      value % 1.0 != 0.0
    ) {
      throw FirmwareArtifactReaderException.invalidRange()
    }
    return value.toLong()
  }

  private fun mapOpenError(error: Exception): Exception =
    when (error) {
      is FirmwareArtifactReaderException -> error
      is FirmwareArtifactStoreException -> when (error.code) {
        "ARTIFACT_INVALID_REF", "ARTIFACT_NOT_FOUND" -> error
        else -> FirmwareArtifactReaderException.storageFailure()
      }
      else -> FirmwareArtifactReaderException.storageFailure()
    }

  companion object {
    const val MAX_READ_BYTES = 256L * 1024L
    private const val MAXIMUM_SAFE_INTEGER = 9_007_199_254_740_991.0

    @Volatile
    private var processReader: FirmwareArtifactReader? = null

    fun processInstance(store: FirmwareArtifactStore): FirmwareArtifactReader =
      processReader ?: synchronized(this) {
        processReader ?: FirmwareArtifactReader(store).also {
          processReader = it
        }
      }
  }
}
