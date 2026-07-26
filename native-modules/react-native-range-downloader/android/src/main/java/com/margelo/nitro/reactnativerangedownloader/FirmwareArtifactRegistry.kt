package com.margelo.nitro.reactnativerangedownloader

import okhttp3.Call
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

internal enum class FirmwareArtifactRegistryState {
  NOT_FOUND,
  QUEUED,
  DOWNLOADING,
  VERIFYING,
  COMPLETED,
  CANCELLED,
  FAILED,
}

internal data class FirmwareArtifactRegistrySnapshot(
  val state: FirmwareArtifactRegistryState,
  val downloadedBytes: Long,
  val expectedSize: Long,
  val artifact: StoredArtifactMetadata? = null,
  val errorCode: String? = null,
  val errorMessage: String? = null,
  val retryable: Boolean? = null,
)

internal class FirmwareArtifactCancellation {
  val cancelled = AtomicBoolean(false)
  private val calls = ConcurrentHashMap.newKeySet<Call>()

  @Volatile
  private var executor: ExecutorService? = null

  fun attach(call: Call) {
    calls += call
    if (cancelled.get()) {
      call.cancel()
    }
  }

  fun detach(call: Call) {
    calls -= call
  }

  fun attach(executor: ExecutorService) {
    this.executor = executor
    if (cancelled.get()) {
      executor.shutdownNow()
    }
  }

  fun cancelAndWait() {
    cancelled.set(true)
    calls.forEach(Call::cancel)
    executor?.let { pool ->
      pool.shutdownNow()
      try {
        pool.awaitTermination(5, TimeUnit.SECONDS)
      } catch (_: InterruptedException) {
        Thread.currentThread().interrupt()
      }
    }
  }
}

internal class FirmwareArtifactRegistry {
  private data class TaskKey(
    val leaseRef: String,
    val taskId: String,
  )

  private class Entry(
    val fingerprint: String,
    val expectedSize: Long,
  ) {
    val cancellation = FirmwareArtifactCancellation()
    val future = CompletableFuture<StoredArtifactMetadata>()
    val downloadedBytes = AtomicLong(0)
    val state = AtomicReference(FirmwareArtifactRegistryState.QUEUED)
    val errorCode = AtomicReference<String?>(null)
    val errorMessage = AtomicReference<String?>(null)
    val retryable = AtomicReference<Boolean?>(null)
  }

  private val entries = ConcurrentHashMap<TaskKey, Entry>()
  private val activeArtifacts = ConcurrentHashMap<String, TaskKey>()

  fun runOrAttach(
    leaseRef: String,
    taskId: String,
    artifactId: String,
    fingerprint: String,
    expectedSize: Long,
    operation: (
      cancellation: FirmwareArtifactCancellation,
      update: (FirmwareArtifactRegistryState, Long) -> Unit,
    ) -> StoredArtifactMetadata,
  ): StoredArtifactMetadata {
    val key = TaskKey(leaseRef, taskId)
    val mine = Entry(fingerprint, expectedSize)
    var incumbent = entries.putIfAbsent(key, mine)
    if (incumbent?.future?.isCompletedExceptionally == true &&
      entries.replace(key, incumbent, mine)
    ) {
      incumbent = null
    }
    val entry = incumbent ?: mine
    if (entry.fingerprint != fingerprint) {
      throw FirmwareArtifactStoreException.invalidMetadata()
    }
    if (incumbent != null) {
      return entry.future.get()
    }

    val activeOwner = activeArtifacts.putIfAbsent(artifactId, key)
    if (activeOwner != null && activeOwner != key) {
      val error = FirmwareArtifactTransferException(
        code = "ARTIFACT_BUSY",
        retryable = true,
        discardStaging = false,
        message = "Another firmware task owns this artifact",
      )
      fail(entry, error)
      throw error
    }
    try {
      val result = operation(entry.cancellation) { state, downloadedBytes ->
        entry.downloadedBytes.set(downloadedBytes.coerceAtLeast(0))
        entry.state.set(state)
      }
      entry.downloadedBytes.set(result.actualSize ?: expectedSize)
      entry.state.set(FirmwareArtifactRegistryState.COMPLETED)
      entry.future.complete(result)
      pruneCompletedEntries()
      return result
    } catch (error: Exception) {
      fail(entry, error)
      throw error
    } finally {
      activeArtifacts.remove(artifactId, key)
    }
  }

  fun status(leaseRef: String, taskId: String): FirmwareArtifactRegistrySnapshot? {
    val entry = entries[TaskKey(leaseRef, taskId)] ?: return null
    return FirmwareArtifactRegistrySnapshot(
      state = entry.state.get(),
      downloadedBytes = entry.downloadedBytes.get(),
      expectedSize = entry.expectedSize,
      artifact = if (entry.future.isDone && !entry.future.isCompletedExceptionally) {
        entry.future.getNow(null)
      } else {
        null
      },
      errorCode = entry.errorCode.get(),
      errorMessage = entry.errorMessage.get(),
      retryable = entry.retryable.get(),
    )
  }

  fun cancel(leaseRef: String, taskId: String) {
    entries[TaskKey(leaseRef, taskId)]?.cancellation?.cancelAndWait()
  }

  private fun fail(entry: Entry, error: Exception) {
    val transferError = error as? FirmwareArtifactTransferException
    val storeError = error as? FirmwareArtifactStoreException
    entry.errorCode.set(
      transferError?.code
        ?: storeError?.code
        ?: "ARTIFACT_DOWNLOAD_FAILED"
    )
    entry.errorMessage.set(error.message)
    entry.retryable.set(transferError?.retryable ?: storeError?.retryable)
    entry.state.set(
      if (entry.cancellation.cancelled.get()) {
        FirmwareArtifactRegistryState.CANCELLED
      } else {
        FirmwareArtifactRegistryState.FAILED
      }
    )
    entry.future.completeExceptionally(error)
    pruneCompletedEntries()
  }

  private fun pruneCompletedEntries() {
    if (entries.size <= MAX_RETAINED_TASKS) return
    entries.entries
      .asSequence()
      .filter { it.value.future.isDone }
      .take(entries.size - MAX_RETAINED_TASKS)
      .forEach { entries.remove(it.key, it.value) }
  }

  companion object {
    private const val MAX_RETAINED_TASKS = 128
    val processInstance = FirmwareArtifactRegistry()
  }
}
