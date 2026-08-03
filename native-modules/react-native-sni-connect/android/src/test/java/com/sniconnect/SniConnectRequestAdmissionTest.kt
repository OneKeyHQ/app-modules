package com.sniconnect

import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SniConnectRequestAdmissionTest {
  @Test
  fun twentySamePairRequestsProduceSixteenActiveAndFourPending() {
    val admission = SniConnectRequestAdmission()
    val admitted = mutableListOf<String>()
    val failures = mutableListOf<Pair<String, String>>()
    try {
      val tickets = (0 until 20).map { index ->
        val requestId = "req-${index.toString().padStart(2, '0')}"
        admission.createTicket(
          hostname = "Example.com",
          ip = if (index % 2 == 0) "093.184.216.034" else "93.184.216.34",
          requestId = requestId,
          timeoutMillis = 10_000,
          onAdmitted = { admitted += requestId },
          onPendingFailure = { code, message -> failures += code to message },
        ).also { it.submit() }
      }

      assertEquals((0 until 16).map { "req-${it.toString().padStart(2, '0')}" }, admitted)
      assertEquals(
        SniConnectAdmissionSnapshot(
          activeRequests = 16,
          activeRequestsForPair = 16,
          pendingRequests = 4,
          pendingRequestsForPair = 4,
          activeRequestIdsForPair = (0 until 16).map { "req-${it.toString().padStart(2, '0')}" },
          pendingRequestIdsForPair = (16 until 20).map { "req-${it.toString().padStart(2, '0')}" },
        ),
        admission.snapshot("EXAMPLE.COM", "93.184.216.34"),
      )

      tickets.drop(16).forEach { pending -> assertTrue(pending.cancelPending()) }
      assertEquals(4, failures.size)
      assertTrue(failures.all { it.first == "SNI_CANCELLED" })
      assertEquals(0, admission.snapshot("example.com", "093.184.216.034").pendingRequests)

      tickets.take(16).forEach { active -> active.release() }
      assertEquals(
        SniConnectAdmissionSnapshot(0, 0, 0, 0),
        admission.snapshot("example.com", "93.184.216.34"),
      )

      val recovery = ticket(admission, "example.com", "93.184.216.34")
      recovery.submit()
      assertEquals(1, admission.snapshot("example.com", "93.184.216.34").activeRequests)
      recovery.release()
    } finally {
      admission.shutdownForTests()
    }
  }

  @Test
  fun queuesAtGlobalLimitAndDispatchesAfterRelease() {
    val admission = admission(maxActive = 2, maxPerPair = 2)
    val admitted = mutableListOf<String>()
    try {
      val first = submit(admission, "first", "one.example", "93.184.216.34", admitted)
      val second = submit(admission, "second", "two.example", "93.184.216.35", admitted)
      val pending = submit(admission, "pending", "three.example", "93.184.216.36", admitted)

      assertEquals(listOf("first", "second"), admitted)
      assertEquals(2, admission.snapshot("three.example", "93.184.216.36").activeRequests)
      assertEquals(1, admission.snapshot("three.example", "93.184.216.36").pendingRequests)

      first.release()
      assertEquals(listOf("first", "second", "pending"), admitted)
      first.release()
      second.release()
      pending.release()
      assertEquals(0, admission.snapshot("three.example", "93.184.216.36").activeRequests)
    } finally {
      admission.shutdownForTests()
    }
  }

  @Test
  fun sameHostnameDifferentIpsHaveIndependentPairLimits() {
    val admission = admission(maxActive = 4, maxPerPair = 2)
    val admitted = mutableListOf<String>()
    try {
      val firstA = submit(admission, "a1", "Example.com", "93.184.216.34", admitted)
      val secondA = submit(admission, "a2", "example.com", "93.184.216.34", admitted)
      val pendingA = submit(admission, "a3", "example.com", "93.184.216.34", admitted)
      val firstB = submit(admission, "b1", "example.com", "93.184.216.35", admitted)
      val secondB = submit(admission, "b2", "example.com", "93.184.216.35", admitted)

      assertEquals(listOf("a1", "a2", "b1", "b2"), admitted)
      assertEquals(
        SniConnectAdmissionSnapshot(
          activeRequests = 4,
          activeRequestsForPair = 2,
          pendingRequests = 1,
          pendingRequestsForPair = 1,
          activeRequestIdsForPair = listOf("a1", "a2"),
          pendingRequestIdsForPair = listOf("a3"),
        ),
        admission.snapshot("EXAMPLE.COM", "93.184.216.34"),
      )

      firstA.release()
      assertEquals(listOf("a1", "a2", "b1", "b2", "a3"), admitted)
      secondA.release()
      pendingA.release()
      firstB.release()
      secondB.release()
      assertEquals(
        SniConnectAdmissionSnapshot(0, 0, 0, 0),
        admission.snapshot("example.com", "93.184.216.34"),
      )
    } finally {
      admission.shutdownForTests()
    }
  }

  @Test
  fun rejectsTheTwoHundredFiftySeventhPendingRequest() {
    val admission = admission(maxActive = 1, maxPerPair = 1, maxPending = 256)
    try {
      val active = ticket(admission, "example.com", "93.184.216.34").also { it.submit() }
      val pending = (0 until 256).map {
        ticket(admission, "example.com", "93.184.216.34").also { request -> request.submit() }
      }
      val overflow = ticket(admission, "example.com", "93.184.216.34")

      assertValidationFails { overflow.submit() }
      assertEquals(256, admission.snapshot("example.com", "93.184.216.34").pendingRequests)

      pending.forEach { request -> assertTrue(request.cancelPending()) }
      active.release()
      assertEquals(
        SniConnectAdmissionSnapshot(0, 0, 0, 0),
        admission.snapshot("example.com", "93.184.216.34"),
      )
    } finally {
      admission.shutdownForTests()
    }
  }

  @Test
  fun cancellingPendingRequestRemovesItAndSettlesImmediately() {
    val admission = admission(maxActive = 1, maxPerPair = 1)
    val failures = mutableListOf<Pair<String, String>>()
    try {
      val active = ticket(admission, "example.com", "93.184.216.34").also { it.submit() }
      val pending = admission.createTicket(
        hostname = "example.com",
        ip = "93.184.216.34",
        timeoutMillis = 10_000,
        onAdmitted = { throw AssertionError("Cancelled request must not be admitted") },
        onPendingFailure = { code, message -> failures += code to message },
      ).also { it.submit() }

      assertTrue(pending.cancelPending())
      assertEquals(listOf("SNI_CANCELLED" to "Request cancelled"), failures)
      assertEquals(0, admission.snapshot("example.com", "93.184.216.34").pendingRequests)
      assertFalse(pending.cancelPending())
      active.release()
    } finally {
      admission.shutdownForTests()
    }
  }

  @Test
  fun cancellationBeforeSubmitSettlesOnceAndSubmitBecomesNoOp() {
    val admission = admission(maxActive = 1, maxPerPair = 1)
    val failures = mutableListOf<String>()
    try {
      val request = admission.createTicket(
        hostname = "example.com",
        ip = "93.184.216.34",
        timeoutMillis = 10_000,
        onAdmitted = { throw AssertionError("Cancelled request must not be admitted") },
        onPendingFailure = { code, _ -> failures += code },
      )

      assertTrue(request.cancelPending())
      request.submit()
      assertEquals(listOf("SNI_CANCELLED"), failures)
      assertEquals(
        SniConnectAdmissionSnapshot(0, 0, 0, 0),
        admission.snapshot("example.com", "93.184.216.34"),
      )
    } finally {
      admission.shutdownForTests()
    }
  }

  @Test
  fun timeoutIncludesTimeSpentWaitingForAdmission() {
    val admission = admission(maxActive = 1, maxPerPair = 1)
    val timedOut = CountDownLatch(1)
    val failureCodes = Collections.synchronizedList(mutableListOf<String>())
    try {
      val active = ticket(admission, "example.com", "93.184.216.34").also { it.submit() }
      admission.createTicket(
        hostname = "example.com",
        ip = "93.184.216.34",
        timeoutMillis = 40,
        onAdmitted = { throw AssertionError("Timed-out request must not be admitted") },
        onPendingFailure = { code, _ ->
          failureCodes += code
          timedOut.countDown()
        },
      ).submit()

      assertTrue(timedOut.await(2, TimeUnit.SECONDS))
      assertEquals(listOf("SNI_REQUEST_TIMEOUT"), failureCodes.toList())
      assertEquals(0, admission.snapshot("example.com", "93.184.216.34").pendingRequests)
      active.release()
    } finally {
      admission.shutdownForTests()
    }
  }

  private fun admission(
    maxActive: Int,
    maxPerPair: Int,
    maxPending: Int = 10,
  ) = SniConnectRequestAdmission(
    maxActiveRequests = maxActive,
    maxActiveRequestsPerPair = maxPerPair,
    maxPendingRequests = maxPending,
  )

  private fun submit(
    admission: SniConnectRequestAdmission,
    id: String,
    hostname: String,
    ip: String,
    admitted: MutableList<String>,
  ): SniConnectRequestAdmission.Ticket =
    ticket(admission, hostname, ip, requestId = id, onAdmitted = { admitted += id }).also { it.submit() }

  private fun ticket(
    admission: SniConnectRequestAdmission,
    hostname: String,
    ip: String,
    requestId: String? = null,
    onAdmitted: (Long) -> Unit = {},
  ): SniConnectRequestAdmission.Ticket = admission.createTicket(
    hostname = hostname,
    ip = ip,
    requestId = requestId,
    timeoutMillis = 60_000,
    onAdmitted = onAdmitted,
    onPendingFailure = { _, _ -> },
  )

  private fun assertValidationFails(block: () -> Unit) {
    try {
      block()
    } catch (_: SniConnectValidation.ValidationException) {
      return
    }
    throw AssertionError("Expected SNI validation failure")
  }
}
