package com.onekey.nativeoverlay

import android.graphics.Color
import android.view.View
import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NativeOverlayToastCardLayoutTest {
  @Test
  fun eligibleActionRemainsVisibleWhenHeightCapWouldClipLowerRow() {
    val card = NativeOverlayToastCardView(RuntimeEnvironment.getApplication())
    val spec = ToastSpec.parse(JSONObject("""
      {
        "id": "capped-toast",
        "statusIcon": {"kind": "status", "status": "success", "size": 36, "glyph": {"contours": [[[6, 12], [10, 16], [18, 8]]]}},
        "title": {"paragraphs": [[{"text": "Done"}]], "fontSize": 16, "lineHeight": 24},
        "actions": [{"id": "view", "testID": "view", "label": "View", "lineHeight": 20, "paddingVertical": 4}]
      }
    """.trimIndent()))
    card.contentWidth = 180f
    card.apply(spec, Color.WHITE)
    card.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    card.layout(0, 0, card.measuredWidth, card.measuredHeight)
    val action = checkNotNull(card.findViewWithTag<View>("view"))
    val lowerTop = action.top

    card.contentMaxHeight = 36f
    card.requestLayout()
    card.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    card.layout(0, 0, card.measuredWidth, card.measuredHeight)

    val padding = (10f * card.resources.displayMetrics.density).roundToInt()
    assertTrue(lowerTop > action.top)
    assertTrue(action.top >= padding && action.bottom <= card.height - padding)
  }
}
