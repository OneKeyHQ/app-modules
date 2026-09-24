package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NativeListSelectorBackgroundColorTest {
  @Test
  fun walletGroupUsesOnlyTheContainerStyleColor() {
    assertNull(
      resolveNativeListSelectorBackgroundColor(
        rowType = "walletGroup",
        styleBackgroundColor = null,
        rowBackgroundColor = "#112233",
      ),
    )
    assertEquals(
      "#445566",
      resolveNativeListSelectorBackgroundColor(
        rowType = "walletGroup",
        styleBackgroundColor = "#445566",
        rowBackgroundColor = "#112233",
      ),
    )
    assertEquals(
      "#112233",
      resolveNativeListSelectorBackgroundColor(
        rowType = "identity",
        styleBackgroundColor = null,
        rowBackgroundColor = "#112233",
      ),
    )
  }
}
