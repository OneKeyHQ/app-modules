package com.margelo.nitro.nativelist

import org.junit.Assert.assertEquals
import org.junit.Test

class NativeListIdentityTrailingAxisTest {
  // Identity trailing accessories stack vertically except for the pairs iOS
  // lays out side by side. The Swap token selector's `valuePair` + DotVerSolid
  // menu icon was missing from the Android list, so the balance was pushed
  // above the menu and clipped inside the 60dp row.
  @Test
  fun matchesTheSideBySidePairsOfIos() {
    fun d(kind: String, name: String = "", actionKey: String = "") =
      NativeListTrailingDescriptor(kind, name, actionKey)

    val cases: List<Pair<List<NativeListTrailingDescriptor>, Boolean>> = listOf(
      listOf(d("valuePair")) to false,
      listOf(d("valuePair"), d("menu", actionKey = "menu")) to true,
      listOf(d("valuePair"), d("icon", "DotVerSolid", "menu")) to true,
      listOf(d("valuePair"), d("icon", "DotVerSolid")) to false,
      listOf(d("valuePair"), d("icon", "InfoCircleOutline", "menu")) to false,
      listOf(d("icon", "DotVerSolid", "menu"), d("valuePair")) to false,
      listOf(d("valuePair"), d("chevron")) to true,
      listOf(d("icon", "RefreshCcwOutline"), d("chevron")) to true,
      listOf(d("icon", "RefreshCcwOutline", "refresh"), d("chevron")) to false,
      listOf(d("icon", "PencilOutline", "edit"), d("icon", "DragOutline")) to true,
      listOf(d("value"), d("checkbox")) to true,
      listOf(d("valuePair"), d("icon", "DotVerSolid", "menu"), d("chevron")) to false,
    )

    assertEquals(
      cases.map { it.second },
      cases.map { nativeListIdentityTrailingIsHorizontal(it.first) },
    )
  }
}
