package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * OCDS §4 conformance: the HTTP-status → permanent/transient classification.
 * Mirrors the JS taxonomy matrix (updateErrorTaxonomy.test.ts) so the four
 * platform classifiers stay in lockstep. `true` = permanent, `false` = transient.
 */
class IsPermanentHttpStatusTest {

  @Test
  fun permanentStatuses() {
    // auth / expired signed URL / gone / not found
    for (code in listOf(401, 403, 404, 410)) {
      assertTrue("HTTP $code should be permanent", isPermanentHttpStatus(code))
    }
    // not-implemented / version-not-supported are permanent even though 5xx
    assertTrue("HTTP 501 should be permanent", isPermanentHttpStatus(501))
    assertTrue("HTTP 505 should be permanent", isPermanentHttpStatus(505))
    // every other 4xx → permanent (catch-all default)
    for (code in listOf(400, 402, 405, 409, 411, 418, 451, 499)) {
      assertTrue("HTTP $code (other 4xx) should be permanent", isPermanentHttpStatus(code))
    }
    // unknown / non-HTTP → permanent
    for (code in listOf(0, -1, 100, 300, 600, 999)) {
      assertTrue("HTTP $code (unknown) should be permanent", isPermanentHttpStatus(code))
    }
  }

  @Test
  fun transientStatuses() {
    // request timeout / throttling
    assertFalse("HTTP 408 should be transient", isPermanentHttpStatus(408))
    assertFalse("HTTP 429 should be transient", isPermanentHttpStatus(429))
    // server errors except 501/505 → transient (back off and retry)
    for (code in listOf(500, 502, 503, 504, 506, 599)) {
      assertFalse("HTTP $code (5xx) should be transient", isPermanentHttpStatus(code))
    }
  }

  /**
   * The regression this guards: 416 is a member of `400..499`, so a naive
   * catch-all would mark it permanent and discard resumable `.segN` bytes.
   * It MUST resolve transient; its 4xx neighbours must stay permanent.
   */
  @Test
  fun rangeNotSatisfiableIsTransientNotPermanent() {
    assertFalse("HTTP 416 must be transient", isPermanentHttpStatus(416))
    assertTrue("HTTP 415 (neighbour) should be permanent", isPermanentHttpStatus(415))
    assertTrue("HTTP 417 (neighbour) should be permanent", isPermanentHttpStatus(417))
  }

  /** Exactly-one-class is total: every status in 100..599 returns a Boolean. */
  @Test
  fun everyStatusResolvesWithoutThrowing() {
    for (code in 100..599) {
      isPermanentHttpStatus(code)
    }
  }
}
