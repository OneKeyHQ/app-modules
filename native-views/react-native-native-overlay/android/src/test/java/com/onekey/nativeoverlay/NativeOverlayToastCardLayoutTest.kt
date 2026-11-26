package com.onekey.nativeoverlay

import android.graphics.Color
import android.graphics.Matrix
import android.view.MotionEvent
import android.view.View
import org.json.JSONObject
import org.junit.Assert.assertEquals
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

  @Test
  fun shortDragVelocityStaysInWindowCoordinatesAcrossSlotScales() {
    val slot = toastSlot()
    val density = slot.resources.displayMetrics.density
    var releaseX = 0f
    var releaseVelocity = 0f
    slot.onRelease = { x, _, velocity, _, cancelled ->
      assertTrue(!cancelled)
      releaseX = x / density
      releaseVelocity = velocity / density
    }
    sendTouch(slot, MotionEvent.ACTION_DOWN, 0L, 250f)
    sendTouch(slot, MotionEvent.ACTION_MOVE, 40L, 262f)
    slot.scaleX = 0.5f
    slot.scaleY = 0.5f
    sendTouch(slot, MotionEvent.ACTION_MOVE, 70L, 271f)
    sendTouch(slot, MotionEvent.ACTION_MOVE, 90L, 277f)
    sendTouch(slot, MotionEvent.ACTION_UP, 100L, 280f)

    assertEquals(30f, releaseX, 0.01f)
    assertEquals(300f, releaseVelocity, 20f)
  }

  @Test
  fun heldDragKeepsItsPhysicalOffsetAndOpacityWhenTheSlotShrinks() {
    val slot = toastSlot()
    val density = slot.resources.displayMetrics.density
    sendTouch(slot, MotionEvent.ACTION_DOWN, 0L, 250f)
    sendTouch(slot, MotionEvent.ACTION_MOVE, 80L, 290f)
    val opacity = slot.card.alpha
    assertEquals(40f, slot.card.translationX / density, 0.01f)

    slot.scaleX = 0.5f
    slot.scaleY = 0.5f
    slot.reprojectDrag()

    assertEquals(40f, slot.card.translationX * slot.scaleX / density, 0.01f)
    assertEquals(opacity, slot.card.alpha, 0f)
    slot.clearTouch()
  }

  @Test
  fun verticalDragLocksItsAxisAndLeavesTheCardToThePresenter() {
    val slot = toastSlot()
    val density = slot.resources.displayMetrics.density
    var startedVertical: Boolean? = null
    var lastDy = 0f
    var releaseY = 0f
    var releaseVelocityY = 0f
    slot.onDragStart = { vertical, _, _ -> startedVertical = vertical }
    slot.onVerticalDrag = { dy -> lastDy = dy / density }
    slot.onRelease = { _, y, _, velocityY, cancelled ->
      assertTrue(!cancelled)
      releaseY = y / density
      releaseVelocityY = velocityY / density
    }
    sendTouch(slot, MotionEvent.ACTION_DOWN, 0L, 250f, 70f)
    sendTouch(slot, MotionEvent.ACTION_MOVE, 40L, 252f, 56f)
    sendTouch(slot, MotionEvent.ACTION_MOVE, 80L, 254f, 30f)

    assertEquals(true, startedVertical)
    assertEquals(-40f, lastDy, 0.01f)
    // The presenter moves cards on a vertical drag; the slot leaves X alone.
    assertEquals(0f, slot.card.translationX, 0f)

    sendTouch(slot, MotionEvent.ACTION_UP, 100L, 254f, 20f)
    assertEquals(-50f, releaseY, 0.01f)
    assertTrue(releaseVelocityY < -300f)
  }

  private fun toastSlot(): ToastSlot {
    val context = RuntimeEnvironment.getApplication()
    return ToastSlot(context, NativeOverlayToastCardView(context))
  }

  private fun sendTouch(slot: ToastSlot, action: Int, time: Long, xDp: Float, yDp: Float = 70f) {
    val density = slot.resources.displayMetrics.density
    val event = MotionEvent.obtain(1000L, 1000L + time, action, xDp * density, yDp * density, 0)
    val windowToSlot = Matrix().apply {
      setTranslate(-100f * density, -40f * density)
      postScale(1f / slot.scaleX, 1f / slot.scaleY)
    }
    event.transform(windowToSlot)
    assertEquals(xDp, event.rawX / density, 0.01f)
    if (action == MotionEvent.ACTION_DOWN) {
      slot.onInterceptTouchEvent(event)
    } else {
      slot.onTouchEvent(event)
    }
    event.recycle()
  }
}
