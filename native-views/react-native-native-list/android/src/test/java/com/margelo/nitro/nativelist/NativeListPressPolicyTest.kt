package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListPressPolicyTest {
  @Test
  fun wholeRowInteractionMatchesPassiveAndAccessoryOnlyRows() {
    data class Case(
      val type: String,
      val variant: String = "",
      val disabled: Boolean = false,
      val pressDisabled: Boolean = false,
      val expected: Boolean,
    )

    val cases = listOf(
      Case(type = "identity", expected = true),
      Case(type = "identity", pressDisabled = true, expected = false),
      Case(type = "identity", disabled = true, expected = false),
      Case(type = "system", variant = "retry", expected = true),
      Case(type = "system", variant = "retry", pressDisabled = true, expected = false),
      Case(type = "system", variant = "loading", expected = false),
      Case(type = "system", variant = "noMatch", expected = false),
      Case(type = "system", variant = "warning", expected = false),
      Case(type = "system", variant = "end", expected = false),
      Case(type = "system", variant = "spacer", expected = false),
      Case(type = "walletGroup", expected = false),
    )

    assertEquals(
      cases.map(Case::expected),
      cases.map { case ->
        isNativeListWholeRowInteractive(
          type = case.type,
          variant = case.variant,
          disabled = case.disabled,
          pressDisabled = case.pressDisabled,
        )
      },
    )
  }
}
