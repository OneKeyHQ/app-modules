package com.reactnativepagerview

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.test.InstrumentationTestCase
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import com.facebook.react.uimanager.PointerEvents
import com.facebook.react.views.view.ReactViewGroup
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

class CollapsiblePagerHitTestActivity : Activity()

class CollapsiblePagerTabReservationTest : InstrumentationTestCase() {
  fun testTabViewportToolbarHitAndResetInBothDirections() {
    val context = instrumentation.targetContext
    assertTrue(context.applicationInfo.flags and ApplicationInfo.FLAG_SUPPORTS_RTL != 0)
    val activity = instrumentation.startActivitySync(
      Intent(context, CollapsiblePagerHitTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    ) as CollapsiblePagerHitTestActivity
    val toolbarPresses = AtomicInteger()
    val tabPresses = AtomicInteger()
    try {
      instrumentation.runOnMainSync {
      val density = context.resources.displayMetrics.density
      fun dp(value: Int) = (value * density).roundToInt()
      val width = dp(320)
      val height = dp(500)
      val reserved = dp(64)
      val tabHeight = dp(44)
      val host = CollapsiblePagerHost(context)
      activity.setContentView(host)
      assertTrue(host.isAttachedToWindow)
      val sticky = ReactViewGroup(context).apply { pointerEvents = PointerEvents.BOX_NONE }
      val fade = View(context).apply { setBackgroundColor(Color.RED) }
      val toolbar = View(context).apply {
        setBackgroundColor(Color.BLUE)
        isClickable = true
      }
      sticky.addView(fade)
      sticky.addView(toolbar)
      host.addReactChild(View(context), 0)
      host.addReactChild(sticky, 1)
      host.headerHeightPx = 0
      host.stickyHeaderHeightPx = tabHeight
      host.nativeTabBarBackgroundColor = Color.GREEN
      host.applyNativeHeaderStyle()
      host.updateNativeTabBarItems(
        """[{"key":"a","title":"Overview"},{"key":"b","title":"Ethereum"},""" +
          """{"key":"c","title":"BNB Chain"},{"key":"d","title":"Polygon"}]""",
      )
      val tab = (0 until host.childCount).map(host::getChildAt)
        .first { it.javaClass.simpleName == "CollapsiblePagerNativeTabBarView" } as ViewGroup
      val row = tab.getChildAt(0) as HorizontalScrollView
      toolbar.setOnClickListener { toolbarPresses.incrementAndGet() }
      host.onNativeTabPress = { _, _ -> tabPresses.incrementAndGet() }

      fun layout() {
        host.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        host.layout(0, 0, width, height)
      }
      fun tap(x: Int) {
        val time = android.os.SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN,
          x.toFloat(), (tabHeight / 2).toFloat(), 0)
        val up = MotionEvent.obtain(time, time + 20, MotionEvent.ACTION_UP,
          x.toFloat(), (tabHeight / 2).toFloat(), 0)
        host.dispatchTouchEvent(down)
        host.dispatchTouchEvent(up)
        down.recycle()
        up.recycle()
      }
      fun colorAt(x: Int): Int {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        host.draw(Canvas(bitmap))
        val color = bitmap.getPixel(x, tabHeight / 2)
        bitmap.recycle()
        return color
      }

      host.nativeTabBarTrailingReservedWidth = 64.0
      layout()
      fade.layout(width - reserved - dp(20), 0, width - reserved, tabHeight)
      toolbar.layout(width - reserved, 0, width, tabHeight)
      assertEquals(width - reserved, tab.width)
      assertEquals(0, tab.left)
      assertEquals(width, sticky.width)
      assertEquals(Color.RED, colorAt(width - reserved - dp(10)))
      tap(width - dp(10))
      tap(dp(28))
      host.updateNativeTabProgress(3, 0f)
      val content = row.getChildAt(0) as ViewGroup
      val lastTab = (0 until content.childCount).map(content::getChildAt)
        .filterIsInstance<TextView>().last()
      assertTrue(lastTab.left >= row.scrollX - dp(1))
      assertTrue(lastTab.right <= row.scrollX + row.width + dp(1))

      host.setPagerLayoutDirection(View.LAYOUT_DIRECTION_RTL)
      host.nativeTabBarTrailingReservedWidth = 64.0
      layout()
      assertEquals(View.LAYOUT_DIRECTION_RTL, tab.layoutDirection)
      toolbar.layout(0, 0, reserved, tabHeight)
      fade.layout(reserved, 0, reserved + dp(20), tabHeight)
      assertEquals(reserved, tab.left)
      assertEquals(width, tab.right)
      assertEquals(Color.RED, colorAt(reserved + dp(10)))
      tap(dp(10))

      host.nativeTabBarTrailingReservedWidth = 500.0
      layout()
      assertEquals(0, tab.width)
      host.nativeTabBarTrailingReservedWidth = 0.0
      layout()
      assertEquals(0, tab.left)
      assertEquals(width, tab.width)
      assertTrue(host.indexOfChild(sticky) < host.indexOfChild(tab))
      }
      instrumentation.waitForIdleSync()
      assertEquals(2, toolbarPresses.get())
      assertEquals(1, tabPresses.get())
    } finally {
      instrumentation.runOnMainSync { activity.finish() }
      instrumentation.waitForIdleSync()
    }
  }
}
