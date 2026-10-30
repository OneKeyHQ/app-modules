package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListActivityHostClipPolicyTest {
  // One Activity row view serves legacy and rich rows and is reused across
  // binds. A legacy row with an overhanging network badge switches the host's
  // clipping off (OK-66071); the next bind must derive clipping afresh, so a
  // rich row reusing that view clips again instead of drawing its details over
  // a neighbouring row (PR #185 review).
  @Test
  fun rebindSequenceDerivesClippingFromEachBoundItem() {
    data class Bind(val name: String, val rich: Boolean, val leadingOverhangs: Boolean, val clips: Boolean)

    val binds = listOf(
      Bind("legacy row, plain token", rich = false, leadingOverhangs = false, clips = true),
      Bind("legacy row, network badge", rich = false, leadingOverhangs = true, clips = false),
      Bind("rich row reusing the badge row's view", rich = true, leadingOverhangs = false, clips = true),
      Bind("legacy row, badge again", rich = false, leadingOverhangs = true, clips = false),
      Bind("legacy row, hidden leading", rich = false, leadingOverhangs = false, clips = true),
    )

    assertEquals(
      binds.map(Bind::clips),
      binds.map { nativeListActivityHostClips(rich = it.rich, leadingOverhangs = it.leadingOverhangs) },
    )
  }
}
