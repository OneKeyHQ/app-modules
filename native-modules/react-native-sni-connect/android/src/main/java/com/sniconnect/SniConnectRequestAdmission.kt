package com.sniconnect

import java.util.ArrayDeque
import java.util.Locale
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal data class SniConnectAdmissionSnapshot(
  val activeRequests: Int,
  val activeRequestsForPair: Int,
  val pendingRequests: Int,
  val pendingRequestsForPair: Int,
  val activeRequestIdsForPair: List<String> = emptyList(),
  val pendingRequestIdsForPair: List<String> = emptyList(),
)

internal class SniConnectRequestAdmission(
  private val maxActiveRequests: Int = SniConnectValidation.MAX_ACTIVE_REQUESTS,
  private val maxActiveRequestsPerPair: Int = SniConnectValidation.MAX_ACTIVE_REQUESTS_PER_PAIR,
  private val maxPendingRequests: Int = SniConnectValidation.MAX_PENDING_REQUESTS,
  private val scheduler: ScheduledExecutorService = createAdmissionScheduler(),
  private val nanoTime: () -> Long = System::nanoTime,
) {
  internal data class PairKey(
    val hostname: String,
    val ip: String,
  )

  internal enum class State {
    CREATED,
    PENDING,
    ACTIVE,
    TERMINAL,
  }

  private data class Dispatch(
    val ticket: Ticket,
    val remainingTimeoutMillis: Long?,
  )

  inner class Ticket internal constructor(
    internal val pair: PairKey,
    internal val requestId: String?,
    internal val deadlineNanos: Long,
    internal val onAdmitted: (remainingTimeoutMillis: Long) -> Unit,
    internal val onPendingFailure: (code: String, message: String) -> Unit,
  ) {
    internal var state = State.CREATED
    internal var timeoutFuture: ScheduledFuture<*>? = null

    fun submit() {
      this@SniConnectRequestAdmission.submit(this)
    }

    fun cancelPending(): Boolean =
      this@SniConnectRequestAdmission.cancelPending(this)

    fun release() {
      this@SniConnectRequestAdmission.release(this)
    }
  }

  private val lock = Any()
  private var activeRequests = 0
  private val activeRequestsByPair = mutableMapOf<PairKey, Int>()
  private val activeTickets = mutableSetOf<Ticket>()
  private val pendingRequests = ArrayDeque<Ticket>()

  fun createTicket(
    hostname: String,
    ip: String,
    requestId: String? = null,
    timeoutMillis: Long,
    onAdmitted: (remainingTimeoutMillis: Long) -> Unit,
    onPendingFailure: (code: String, message: String) -> Unit,
  ): Ticket = Ticket(
    pair = pairKey(hostname, ip),
    requestId = requestId?.takeIf { it.isNotEmpty() },
    deadlineNanos = nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis),
    onAdmitted = onAdmitted,
    onPendingFailure = onPendingFailure,
  )

  fun snapshot(hostname: String, ip: String): SniConnectAdmissionSnapshot {
    val pair = pairKey(hostname, ip)
    return synchronized(lock) {
      val activeForPair = activeTickets.filter { ticket -> ticket.pair == pair }
      val pendingForPair = pendingRequests.filter { ticket -> ticket.pair == pair }
      SniConnectAdmissionSnapshot(
        activeRequests = activeRequests,
        activeRequestsForPair = activeForPair.size,
        pendingRequests = pendingRequests.size,
        pendingRequestsForPair = pendingForPair.size,
        activeRequestIdsForPair = activeForPair.mapNotNull { ticket -> ticket.requestId }.sorted(),
        pendingRequestIdsForPair = pendingForPair.mapNotNull { ticket -> ticket.requestId }.sorted(),
      )
    }
  }

  internal fun shutdownForTests() {
    scheduler.shutdownNow()
  }

  private fun submit(ticket: Ticket) {
    var remainingTimeoutMillis: Long? = null
    var timedOut = false
    synchronized(lock) {
      // A runtime can cancel immediately after registering the handle but
      // before submit() reaches this lock. Cancellation already settled the
      // ticket, so submission becomes an idempotent no-op.
      if (ticket.state == State.TERMINAL) return
      check(ticket.state == State.CREATED) { "Admission ticket already submitted" }
      if (ticket.deadlineNanos <= nanoTime()) {
        ticket.state = State.TERMINAL
        timedOut = true
      } else if (canActivateLocked(ticket.pair)) {
        activateLocked(ticket)
        remainingTimeoutMillis = remainingMillis(ticket.deadlineNanos)
      } else {
        if (pendingRequests.size >= maxPendingRequests) {
          ticket.state = State.TERMINAL
          throw SniConnectValidation.ValidationException("Too many pending SNI requests")
        }
        ticket.state = State.PENDING
        pendingRequests.addLast(ticket)
        val delayNanos = (ticket.deadlineNanos - nanoTime()).coerceAtLeast(0L)
        ticket.timeoutFuture = scheduler.schedule(
          { timeoutPending(ticket) },
          delayNanos,
          TimeUnit.NANOSECONDS,
        )
      }
    }

    if (timedOut) {
      ticket.onPendingFailure(
        "SNI_REQUEST_TIMEOUT",
        "Request timed out while waiting for admission",
      )
    } else {
      remainingTimeoutMillis?.let(ticket.onAdmitted)
    }
  }

  private fun cancelPending(ticket: Ticket): Boolean {
    val cancelled = synchronized(lock) {
      when (ticket.state) {
        State.CREATED -> {
          ticket.state = State.TERMINAL
          true
        }
        State.PENDING -> {
          pendingRequests.remove(ticket)
          ticket.timeoutFuture?.cancel(false)
          ticket.timeoutFuture = null
          ticket.state = State.TERMINAL
          true
        }
        State.ACTIVE, State.TERMINAL -> false
      }
    }
    if (cancelled) {
      ticket.onPendingFailure("SNI_CANCELLED", "Request cancelled")
    }
    return cancelled
  }

  private fun timeoutPending(ticket: Ticket) {
    val timedOut = synchronized(lock) {
      if (ticket.state != State.PENDING) {
        false
      } else {
        pendingRequests.remove(ticket)
        ticket.timeoutFuture = null
        ticket.state = State.TERMINAL
        true
      }
    }
    if (timedOut) {
      ticket.onPendingFailure(
        "SNI_REQUEST_TIMEOUT",
        "Request timed out while waiting for admission",
      )
    }
  }

  private fun release(ticket: Ticket) {
    val admissions = synchronized(lock) {
      if (ticket.state != State.ACTIVE) return
      ticket.state = State.TERMINAL
      activeTickets.remove(ticket)
      activeRequests -= 1
      decrementPairLocked(ticket.pair)
      collectAdmissionsLocked()
    }
    admissions.forEach { dispatch ->
      val timeoutMillis = dispatch.remainingTimeoutMillis
      if (timeoutMillis == null) {
        dispatch.ticket.onPendingFailure(
          "SNI_REQUEST_TIMEOUT",
          "Request timed out while waiting for admission",
        )
      } else {
        dispatch.ticket.onAdmitted(timeoutMillis)
      }
    }
  }

  private fun collectAdmissionsLocked(): List<Dispatch> {
    val admissions = mutableListOf<Dispatch>()
    while (activeRequests < maxActiveRequests) {
      val iterator = pendingRequests.iterator()
      var next: Ticket? = null
      while (iterator.hasNext()) {
        val candidate = iterator.next()
        if (canActivateLocked(candidate.pair)) {
          iterator.remove()
          next = candidate
          break
        }
      }
      val ticket = next ?: break
      ticket.timeoutFuture?.cancel(false)
      ticket.timeoutFuture = null
      if (ticket.deadlineNanos <= nanoTime()) {
        ticket.state = State.TERMINAL
        admissions += Dispatch(ticket, null)
      } else {
        activateLocked(ticket)
        admissions += Dispatch(ticket, remainingMillis(ticket.deadlineNanos))
      }
    }
    return admissions
  }

  private fun canActivateLocked(pair: PairKey): Boolean =
    activeRequests < maxActiveRequests &&
      (activeRequestsByPair[pair] ?: 0) < maxActiveRequestsPerPair

  private fun pairKey(hostname: String, ip: String): PairKey = PairKey(
    hostname = hostname.lowercase(Locale.US),
    ip = SniConnectValidation.canonicalizePublicIp(ip),
  )

  private fun activateLocked(ticket: Ticket) {
    ticket.state = State.ACTIVE
    activeTickets.add(ticket)
    activeRequests += 1
    activeRequestsByPair[ticket.pair] = (activeRequestsByPair[ticket.pair] ?: 0) + 1
  }

  private fun decrementPairLocked(pair: PairKey) {
    val pairCount = activeRequestsByPair[pair] ?: return
    if (pairCount == 1) {
      activeRequestsByPair.remove(pair)
    } else {
      activeRequestsByPair[pair] = pairCount - 1
    }
  }

  private fun remainingMillis(deadlineNanos: Long): Long {
    val remainingNanos = (deadlineNanos - nanoTime()).coerceAtLeast(1L)
    return ((remainingNanos + 999_999L) / 1_000_000L).coerceAtLeast(1L)
  }

  private companion object {
    fun createAdmissionScheduler(): ScheduledExecutorService =
      ScheduledThreadPoolExecutor(1) { runnable ->
        Thread(runnable, "SniConnectAdmission").apply { isDaemon = true }
      }.apply {
        removeOnCancelPolicy = true
      }
  }
}
