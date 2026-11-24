package com.margelo.nitro.nativelist

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeListIdentitySubtitleBindingTest {
  private val controller = Robolectric.buildActivity(Activity::class.java)
  private lateinit var row: NativeListIdentityRowView
  private val checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String =
    { _, _, fallback -> fallback }

  @Before
  fun setUp() {
    val activity = controller.setup().visible().get()
    val context = ThemedReactContext(BridgeReactContext(activity.application), activity, "test", 1)
    row = NativeListIdentityRowView(context, NativeListFonts())
    activity.setContentView(FrameLayout(activity).apply { addView(row) })
  }

  @After
  fun tearDown() {
    row.dispose()
    controller.pause().stop().destroy()
  }

  @Test
  fun longerPlainBalanceRemeasuresTheExistingLabelWithoutDetachingAvatar() {
    bind(account("--"))
    measureRow()
    val balance = requireNotNull(label("--"))
    val originalWidth = balance.measuredWidth
    assertTrue(originalWidth > 0)
    assertFalse(balance.isLayoutRequested)
    val originalEpoch = row.bindingEpoch
    val avatarDetachments = watchAvatarDetachments()

    val updated = account("12345678.90")
    bind(updated)
    assertTrue(balance.isLayoutRequested)
    measureRow()

    assertSame(balance, label("12345678.90"))
    assertTrue(balance.measuredWidth > originalWidth)
    assertSame(updated, row.tag)
    assertEquals("Account, 12345678.90", row.contentDescription)
    assertEquals(originalEpoch, row.bindingEpoch)
    assertEquals(0, avatarDetachments())
  }

  @Test
  fun selectionEchoThenBalanceKeepsTheSubtitleAndAvatarAttached() {
    bind(account("--", checkbox = "unchecked"))
    val balance = requireNotNull(label("--"))
    val originalEpoch = row.bindingEpoch
    val avatarDetachments = watchAvatarDetachments()
    val checkbox = descendants(row).filterIsInstance<OneKeyCheckboxView>().single()

    row.bindSelection(
      account("--", selected = true, checkbox = "checked"), null, "linear", 0, true, checkboxState,
    )
    assertEquals("CheckboxCheckedCustom", checkbox.iconName)
    bind(account("12.34", selected = true, checkbox = "checked"))

    assertSame(balance, label("12.34"))
    assertEquals("CheckboxCheckedCustom", checkbox.iconName)
    assertEquals(originalEpoch, row.bindingEpoch)
    assertEquals(0, avatarDetachments())
  }

  @Test
  fun subtitleUpdateUsesRenderedContentAfterSelectionHasAdvancedTheModel() {
    bind(account("--", showAction = true))
    val originalEpoch = row.bindingEpoch
    val previousAction = requireNotNull(label("Manage"))

    row.bindSelection(account("--", selected = true), null, "linear", 0, true, checkboxState)
    assertSame(previousAction, label("Manage"))
    bind(account("12.34", selected = true))

    assertNull(label("Manage"))
    assertEquals("12.34", label("12.34")?.text.toString())
    assertTrue(row.bindingEpoch > originalEpoch)
  }

  @Test
  fun formattedBalanceUpdateReusesTheLabelAndClearsOldSpansOnPlainText() {
    bind(account("--"))
    val balance = requireNotNull(label("--"))
    val originalEpoch = row.bindingEpoch
    bind(account("0.0004", formatted = true))
    assertSame(balance, label("0.04"))
    bind(account("1.23"))
    assertSame(balance, label("1.23"))
    assertEquals(originalEpoch, row.bindingEpoch)
  }

  private fun bind(item: NativeListItem) {
    row.bind(
      item, null, "linear", "vertical", 0, item.json.optBoolean("selected"), checkboxState,
      useSourceScale = false,
    )
  }

  private fun measureRow() {
    row.measure(
      View.MeasureSpec.makeMeasureSpec(640, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
    )
    row.layout(0, 0, row.measuredWidth, row.measuredHeight)
  }

  private fun account(
    balance: String,
    showAction: Boolean = false,
    formatted: Boolean = false,
    selected: Boolean = false,
    checkbox: String? = null,
  ): NativeListItem {
    val json = JSONObject("""
      {"key":"account","type":"identity","presentation":"accountSelector","title":"Account",
       "leading":{"kind":"account","fallbackText":"AC"},
       "subtitleSegments":[{"text":"$balance"},{"text":"0x1234","separatorBefore":true}]}
    """).put("accessibilityLabel", "Account, $balance").put("selected", selected)
    if (showAction) {
      json.put("trailing", JSONArray("""[{"kind":"button","label":"Manage","actionKey":"manage"}]"""))
    }
    if (checkbox != null) {
      json.put("trailing", JSONArray().put(JSONObject().put("kind", "checkbox").put("state", checkbox)))
    }
    if (formatted) {
      json.getJSONArray("subtitleSegments").getJSONObject(0)
        .put("textSegments", JSONArray("""[{"text":"0.0"},{"text":"4","style":"subscript"}]"""))
    }
    return NativeListItem.parse(json)
  }

  private fun label(text: String): TextView? =
    descendants(row).filterIsInstance<TextView>().singleOrNull { it.text.toString() == text && it.visibility == View.VISIBLE }

  private fun watchAvatarDetachments(): () -> Int {
    val avatar = descendants(row).single { it is NativeListLeadingVisual }
    var detachments = 0
    avatar.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(view: View) = Unit
      override fun onViewDetachedFromWindow(view: View) { detachments++ }
    })
    return { detachments }
  }

  private fun descendants(view: View): Sequence<View> = sequence {
    yield(view)
    if (view is ViewGroup) {
      for (index in 0 until view.childCount) yieldAll(descendants(view.getChildAt(index)))
    }
  }
}
