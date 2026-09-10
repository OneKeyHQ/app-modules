package com.margelo.nitro.nativelist

import android.app.Activity
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.onekeyimage.OneKeyImageReusableView
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [PngOnlyAvifDecoder::class])
class NativeListRowBindingTest {
  private val controller = Robolectric.buildActivity(Activity::class.java)
  private lateinit var row: NativeListRowView

  @Before
  fun setUp() {
    val activity = controller.setup().visible().get()
    val context = ThemedReactContext(BridgeReactContext(activity.application), activity, "test", 1)
    row = NativeListRowView(context)
    val container = FrameLayout(activity)
    activity.setContentView(container)
    container.addView(row, FrameLayout.LayoutParams(400, 64))
  }

  @After
  fun tearDown() {
    row.dispose()
    controller.pause().stop().destroy()
  }

  private fun bind(item: NativeListItem, theme: JSONObject? = null) {
    row.bind(item, theme, "linear", "vertical", 0, false, { _, _, fallback -> fallback })
    row.measure(View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(64, View.MeasureSpec.EXACTLY))
    row.layout(0, 0, 400, 64)
  }

  private fun descendants(view: View): List<View> = buildList {
    add(view)
    if (view is ViewGroup) for (index in 0 until view.childCount) addAll(descendants(view.getChildAt(index)))
  }

  private fun avatar(): ImageView {
    val reusable = descendants(row).filterIsInstance<OneKeyImageReusableView>()
      .first { it.visibility == View.VISIBLE }
    return reusable.getChildAt(0) as ImageView
  }

  private fun awaitAvatar() {
    val deadline = System.nanoTime() + 10_000_000_000L
    while (avatar().drawable == null && System.nanoTime() < deadline) {
      shadowOf(Looper.getMainLooper()).idle()
      Thread.sleep(10)
    }
    assertNotNull("The avatar must finish decoding", avatar().drawable)
  }

  @Test
  fun repeatedBalancePatchesKeepTheLoadedAvatarAndExistingTextViews() {
    val theme = JSONObject("""{"disabledText":"#777777","secondaryText":"#999999"}""")
    bind(accountRow(), theme)
    awaitAvatar()
    val imageView = avatar()
    val drawable = imageView.drawable
    val subtitle = descendants(row).filterIsInstance<TextView>().first { it.text.toString() == "--" }
    for (value in listOf("12.34", "12.56", "--", "1.25")) {
      bind(accountRow(value), theme)
      assertSame(imageView, avatar())
      assertSame("No placeholder between balance updates", drawable, imageView.drawable)
      assertEquals(value, subtitle.text.toString())
      assertEquals(android.graphics.Color.parseColor(if (value == "--") "#777777" else "#999999"), subtitle.currentTextColor)
      assertEquals("Account #1, $value", row.contentDescription)
      shadowOf(Looper.getMainLooper()).idle()
      assertSame("No asynchronous avatar reload", drawable, imageView.drawable)
    }
  }

  @Test
  fun subtitlePatchWhileTheAvatarIsLoadingKeepsItsCompletionBindingValid() {
    bind(accountRow())
    val epoch = row.bindingEpoch
    bind(accountRow("12.34"))
    assertEquals(epoch, row.bindingEpoch)
    awaitAvatar()
    assertEquals("Account #1, 12.34", row.contentDescription)
  }

  @Test
  fun changedAvatarAndRecycledRowCannotKeepStaleImages() {
    bind(accountRow())
    awaitAvatar()
    val next = JSONObject(accountRow("12.34").content)
    next.getJSONObject("leading").getJSONObject("image")
      .put("uri", "onekey-avatar://blockie/v1/0x5678")
    bind(NativeListItem.parse(next))
    assertNull(avatar().drawable)
    awaitAvatar()
    row.recycle()
    assertNull(avatar().drawable)
    bind(NativeListItem.parse(next))
    awaitAvatar()
  }

  @Test
  fun themeChangesStillPerformFullBinding() {
    bind(accountRow())
    val epoch = row.bindingEpoch
    bind(accountRow("12.34"), JSONObject("""{"primaryText":"#FFFFFF","secondaryText":"#AAAAAA"}"""))
    assertTrue(row.bindingEpoch > epoch)
    val title = descendants(row).filterIsInstance<TextView>().first { it.text.toString() == "Account #1" }
    assertEquals(android.graphics.Color.WHITE, title.currentTextColor)
  }
}
