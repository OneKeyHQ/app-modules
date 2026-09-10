package com.margelo.nitro.nativelist

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

internal fun accountRow(value: String = "--"): NativeListItem = NativeListItem.parse(JSONObject("""
  {"key":"account-1","type":"identity","presentation":"accountSelector","height":64,
   "title":"Account #1","accessibilityLabel":"Account #1, $value",
   "leading":{"kind":"account","image":{"uri":"onekey-avatar://blockie/v1/0x1234"}},
   "subtitleSegments":[{"text":"$value","tone":"${if (value == "--") "disabled" else "secondary"}"},{"text":"0x1234","separatorBefore":true}]}
"""))

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NativeListRowUpdatesTest {
  @Test
  fun balanceAndRichTextChangesPreserveTheSameSubtitleSlotsWithoutMutatingRows() {
    val previous = accountRow()
    val next = accountRow("12.34")
    next.json.getJSONArray("subtitleSegments").getJSONObject(0)
      .put("textSegments", JSONArray("""[{"text":"0.0"},{"text":"4","style":"subscript"}]"""))
    val rich = NativeListItem.parse(next.json)
    assertTrue(canUpdateIdentitySubtitle(previous, rich))
    assertTrue(canUpdateIdentitySubtitle(rich, accountRow("12.56")))
    assertEquals("--", previous.json.getJSONArray("subtitleSegments").getJSONObject(0).getString("text"))
    assertTrue(rich.json.getJSONArray("subtitleSegments").getJSONObject(0).has("textSegments"))
  }

  @Test
  fun identityImageActionsThemeRelatedFieldsAndUnknownChangesRequireFullBinding() {
    val previous = accountRow()
    val changes = listOf(
      """{"key":"account-2"}""", """{"type":"action"}""",
      """{"leading":{"kind":"account","image":{"uri":"different"}}}""",
      """{"trailing":[{"kind":"icon","name":"PlusSmallOutline"}]}""",
      """{"height":80}""", """{"presentation":"networkSelector"}""",
      """{"revision":1}""", """{"disabled":true}""", """{"unknownField":1}""",
      """{"subtitleSegments":[]}""",
      """{"subtitleSegments":[{"text":"1"},{"text":"address","separatorBefore":false}]}""",
    )
    for (change in changes) {
      val json = JSONObject(previous.content)
      val patch = JSONObject(change)
      patch.keys().forEach { json.put(it, patch.get(it)) }
      assertFalse(change, canUpdateIdentitySubtitle(previous, NativeListItem.parse(json)))
    }
  }
}
