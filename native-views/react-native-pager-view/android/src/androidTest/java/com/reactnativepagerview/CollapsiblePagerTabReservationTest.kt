package com.reactnativepagerview

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.test.InstrumentationTestCase
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.react.uimanager.PointerEvents
import com.facebook.react.views.view.ReactViewGroup
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

class CollapsiblePagerHitTestActivity : Activity()

class CollapsiblePagerTabReservationTest : InstrumentationTestCase() {
  fun testSegmentedNativeTabsTrackFractionalProgressAndReset() {
    val context = instrumentation.targetContext
    assertTrue(context.applicationInfo.flags and ApplicationInfo.FLAG_SUPPORTS_RTL != 0)
    val activity = instrumentation.startActivitySync(
      Intent(context, CollapsiblePagerHitTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    ) as CollapsiblePagerHitTestActivity
    val density = context.resources.displayMetrics.density
    fun dp(value: Int) = (value * density).roundToInt()
    lateinit var tab: CollapsiblePagerNativeTabBarView
    lateinit var row: HorizontalScrollView
    lateinit var content: ViewGroup
    lateinit var track: View
    lateinit var thumb: View
    lateinit var buttons: List<TextView>
    fun layout(width: Int = 320, height: Int = 56) {
      tab.measure(View.MeasureSpec.makeMeasureSpec(dp(width), View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(dp(height), View.MeasureSpec.EXACTLY))
      tab.layout(0, 0, dp(width), dp(height))
    }
    try {
      instrumentation.runOnMainSync {
      tab = CollapsiblePagerNativeTabBarView(context)
      activity.setContentView(tab)
      val presses = mutableListOf<String>()
      tab.onItemPress = { _, key -> presses.add(key) }
      tab.updateItemsJSON("""[{"key":"recent","title":"Recents"},""" +
        """{"key":"account","title":"Accounts"},""" +
        """{"key":"book","title":"Address Book"}]""")
      tab.updateStyle(56.0, "segmented", 20.0, 12.0, 0.0, 14.0, null, 2.0, 0.0)
      tab.updateColors(Color.LTGRAY, Color.BLACK, Color.GRAY, Color.WHITE)
      layout()
      row = tab.getChildAt(0) as HorizontalScrollView
      content = row.getChildAt(0) as ViewGroup
      track = content.getChildAt(0)
      thumb = content.getChildAt(1)
      buttons = (0 until content.childCount).map(content::getChildAt).filterIsInstance<TextView>()
      assertEquals(3, buttons.size)
      assertEquals(dp(20), track.left)
      assertTrue(kotlin.math.abs(dp(32) - track.height) <= 1)
      assertEquals(0, row.scrollX)
      assertTrue(kotlin.math.abs(buttons[0].width - buttons[1].width) <= 1)
      tab.setProgress(0f)
      val firstX = thumb.left
      tab.setProgress(1f)
      val secondX = thumb.left
      tab.setProgress(0.25f)
      assertTrue(kotlin.math.abs(thumb.left - (firstX + (secondX - firstX) * 0.25f)) <= 1)
      tab.setProgress(0.5f)
      assertTrue(kotlin.math.abs(thumb.left - (firstX + secondX) / 2f) <= 1)
      tab.setProgress(0f)
      assertEquals(firstX, thumb.left)
      buttons[1].performClick()
      assertEquals(listOf("account"), presses)

      tab.layoutDirection = View.LAYOUT_DIRECTION_RTL
      }
      instrumentation.waitForIdleSync()
      instrumentation.runOnMainSync {
      layout()
      assertEquals(View.LAYOUT_DIRECTION_RTL, row.layoutDirection)
      val rtlFirst = buttons[0].layoutParams as android.widget.FrameLayout.LayoutParams
      val rtlSecond = buttons[1].layoutParams as android.widget.FrameLayout.LayoutParams
      assertTrue(rtlFirst.leftMargin > rtlSecond.leftMargin)
      val rtlGeometry = "rowDir=${row.layoutDirection} contentDir=${content.layoutDirection} " +
        buttons.mapIndexed { index, button ->
          val params = button.layoutParams as android.widget.FrameLayout.LayoutParams
          "$index:${button.left}-${button.right} margin=${params.leftMargin}/${params.rightMargin} gravity=${params.gravity}"
        }.joinToString(" ")
      assertTrue(rtlGeometry, buttons[0].left >= buttons[1].right)
      assertEquals(dp(20), track.left)
      tab.setProgress(0f)
      val rtlFirstX = thumb.left
      tab.setProgress(1f)
      val rtlSecondX = thumb.left
      assertTrue(rtlFirstX > rtlSecondX)
      tab.setProgress(0.5f)
      assertTrue(kotlin.math.abs(thumb.left - (rtlFirstX + rtlSecondX) / 2f) <= 1)
      tab.layoutDirection = View.LAYOUT_DIRECTION_LTR
      }
      instrumentation.waitForIdleSync()
      instrumentation.runOnMainSync {
      layout()
      assertTrue(buttons[0].right <= buttons[1].left)
      tab.updateItemsJSON("""[{"key":"recent","title":"Recents (8)"},""" +
        """{"key":"account","title":"Accounts"}]""")
      layout()
      val twoButtons = (0 until content.childCount).map(content::getChildAt).filterIsInstance<TextView>()
      assertEquals(2, twoButtons.size)
      assertTrue(kotlin.math.abs(twoButtons[0].width - track.width / 2) <= 1)
      layout(width = 30)
      assertEquals(dp(15), track.left)
      assertEquals(0, track.width)
      tab.updateStyle(56.0, "segmented", 20.0, 40.0, 0.0, 14.0, null, 2.0, 0.0)
      layout(width = 30)
      assertTrue(kotlin.math.abs(dp(28) - track.top) <= 1)
      assertEquals(0, track.height)
      assertEquals(View.GONE, thumb.visibility)
      tab.updateItemsJSON("[]")
      assertEquals(View.GONE, tab.visibility)
      tab.updateItemsJSON("""[{"key":"recent","title":"Recents"}]""")
      tab.updateStyle(44.0, "underline", 20.0, 0.0, 8.0, 16.0, null, 2.0, 0.0)
      layout(height = 44)
      assertEquals(View.GONE, track.visibility)
      assertEquals(dp(2), thumb.height)
      }
    } finally {
      instrumentation.runOnMainSync { activity.finish() }
    }
  }

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

  fun testUnmountWithTransitioningPageHoldersSkipsEmptyRecyclerLayout() {
    val context = instrumentation.targetContext
    val activity = instrumentation.startActivitySync(
      Intent(context, CollapsiblePagerHitTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    ) as CollapsiblePagerHitTestActivity
    try {
      instrumentation.runOnMainSync {
        val density = context.resources.displayMetrics.density
        fun dp(value: Int) = (value * density).roundToInt()
        val width = dp(320)
        val height = dp(500)
        val host = CollapsiblePagerHost(context)
        activity.setContentView(host)
        val header = View(context)
        val sticky = View(context)
        val page0 = View(context)
        val page1 = View(context)
        host.addReactChild(header, 0)
        host.addReactChild(sticky, 1)
        host.addReactChild(page0, 2)
        host.addReactChild(page1, 3)
        host.measure(
          View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
        )
        host.layout(0, 0, width, height)
        val pager = (0 until host.childCount).map(host::getChildAt).filterIsInstance<ViewPager2>().first()
        val recycler = pager.getChildAt(0) as RecyclerView
        assertNull(recycler.itemAnimator)
        assertTrue(host.attachedPageCount() > 0)

        val transitioningHolders = (0 until recycler.childCount).map(recycler::getChildAt)
        transitioningHolders.forEach { recycler.startViewTransition(it) }

        host.removeReactChild(page1)
        host.removeReactChild(page0)
        host.removeReactChild(sticky)
        host.removeReactChild(header)

        host.viewTreeObserver.dispatchOnPreDraw()
        host.measure(
          View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
        )
        host.layout(0, 0, width, height)
        assertTrue(host.attachedPageCount() > 0)

        transitioningHolders.forEach { recycler.endViewTransition(it) }
      }
      instrumentation.waitForIdleSync()
    } finally {
      instrumentation.runOnMainSync { activity.finish() }
      instrumentation.waitForIdleSync()
    }
  }
  fun testControlledPagerSettlesWhenOnlyParentLayoutRequestRemains() {
    val context = instrumentation.targetContext
    val activity = instrumentation.startActivitySync(
      Intent(context, CollapsiblePagerHitTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    ) as CollapsiblePagerHitTestActivity
    lateinit var host: CollapsiblePagerHost
    lateinit var tab: CollapsiblePagerNativeTabBarView
    val drain = CollapsiblePagerHost::class.java.getDeclaredMethod("layoutPagerIfRequested").apply { isAccessible = true }
    try {
      instrumentation.runOnMainSync {
        val density = context.resources.displayMetrics.density
        val width = (320 * density).roundToInt()
        val height = (500 * density).roundToInt()
        host = CollapsiblePagerHost(context)
        host.pendingInitialPage = 1
        activity.setContentView(host)
        repeat(4) { host.addReactChild(View(context), it) }
        host.applyPendingInitialPage()
        host.nativeTabBarVariant = "text"
        host.applyNativeHeaderStyle()
        host.updateNativeTabBarItems("""[{"key":"a","title":"A"},{"key":"b","title":"B"}]""")
        host.updateNativeTabBarConfig("""{"controlled":true,"selectedKey":"b","pageKeys":["a","b"],"toolbar":{}}""")
        host.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        host.layout(0, 0, width, height)
        tab = (0 until host.childCount).map(host::getChildAt)
          .filterIsInstance<CollapsiblePagerNativeTabBarView>().single()
        host.pager.setCurrentItem(1, false)
        val recycler = host.pager.getChildAt(0) as RecyclerView
        host.pager.requestLayout()
        recycler.requestLayout()
        recycler.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(recycler.height, View.MeasureSpec.EXACTLY))
        recycler.layout(recycler.left, recycler.top, recycler.right, recycler.bottom)
        assertFalse(recycler.isLayoutRequested)
        assertTrue(host.pager.isLayoutRequested)
        assertTrue(tab.needsToolbarPagerSettlement(1))
        drain.invoke(host)
        assertFalse(host.pager.isLayoutRequested)
      }
      instrumentation.waitForIdleSync()
      instrumentation.runOnMainSync {
        assertFalse(tab.needsToolbarPagerSettlement(1))
        val posted = CollapsiblePagerHost::class.java.getDeclaredField("toolbarSettlePostedGeneration").apply { isAccessible = true }
        repeat(50) { drain.invoke(host) }
        assertNull(posted.get(host))
        assertFalse(host.pager.isLayoutRequested)
      }
      instrumentation.runOnMainSync {
        val parent = host.parent as ViewGroup
        val index = parent.indexOfChild(host)
        parent.removeView(host)
        parent.addView(host, index)
        assertTrue(host.isAttachedToWindow)
        assertTrue(host.isShown)
        assertTrue(host.width > 0 && host.height > 0)
        assertFalse(host.isLaidOut)
        assertEquals(1, host.pager.currentItem)
        drain.invoke(host)
        assertTrue(host.isLaidOut)
      }
      instrumentation.waitForIdleSync()
      instrumentation.runOnMainSync {
        assertEquals(1, host.pager.currentItem)
        assertFalse(tab.needsToolbarPagerSettlement(1))
      }
    } finally {
      instrumentation.runOnMainSync { activity.finish() }
      instrumentation.waitForIdleSync()
    }
  }

  fun testTextToolbarBackgroundCoversSlotWithSingleTranslucentLayer() {
    val context = instrumentation.targetContext
    val activity = instrumentation.startActivitySync(
      Intent(context, CollapsiblePagerHitTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    ) as CollapsiblePagerHitTestActivity
    try {
      instrumentation.runOnMainSync {
        val density = context.resources.displayMetrics.density
        fun dp(value: Int) = (value * density).roundToInt()
        val width = dp(320)
        val height = dp(44)
        val tab = CollapsiblePagerNativeTabBarView(context)
        tab.layoutDirection = View.LAYOUT_DIRECTION_LTR
        activity.setContentView(tab)
        tab.updateItemsJSON("""[{"key":"a","title":"A"}]""")
        fun configure(variant: String, background: Int, entry: Boolean) {
          tab.updateStyle(44.0, variant, 20.0, 0.0, 8.0, 18.0, null, 2.0, 0.0)
          val toolbar = if (entry) """{"a":{"iconName":"SliderHorOutline"}}""" else "{}"
          tab.updateConfigJSON("""{"controlled":true,"selectedKey":"a","pageKeys":["a"],"toolbar":$toolbar}""")
          tab.updateToolbarPagerState(0, false)
          tab.updateColors(background, Color.BLACK, Color.GRAY, Color.BLACK)
          tab.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
          tab.layout(0, 0, width, height)
        }
        for (background in listOf(0xff204060.toInt(), 0x80204060.toInt(), Color.TRANSPARENT)) {
          for (entry in listOf(false, true)) {
            configure("text", background, entry)
            val row = tab.getChildAt(0) as HorizontalScrollView
            assertEquals(width - dp(44), row.width)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            tab.draw(Canvas(bitmap))
            assertEquals(Color.alpha(background), Color.alpha(bitmap.getPixel(1, height - 1)))
            assertEquals(Color.alpha(background), Color.alpha(bitmap.getPixel(width - 1, height - 1)))
            assertEquals(Color.TRANSPARENT, (row.background as ColorDrawable).color)
            assertEquals(Color.TRANSPARENT, ((row.getChildAt(0) as ViewGroup).background as ColorDrawable).color)
            bitmap.recycle()
          }
        }
        for (variant in listOf("underline", "text", "segmented", "text")) {
          val background = 0xff204060.toInt()
          configure(variant, background, false)
          val row = tab.getChildAt(0) as HorizontalScrollView
          assertEquals(if (variant == "text") background else Color.TRANSPARENT,
            (tab.background as ColorDrawable).color)
          if (variant == "underline") assertEquals(background, (row.background as ColorDrawable).color)
          if (variant == "segmented") {
            val content = row.getChildAt(0) as ViewGroup
            val track = content.getChildAt(0)
            assertEquals(background, (track.background as GradientDrawable).color?.defaultColor)
          }
        }
      }
    } finally {
      instrumentation.runOnMainSync { activity.finish() }
      instrumentation.waitForIdleSync()
    }
  }

  fun testTextActiveTypefaceInheritsNormalFamilyAndKeepsExplicitFace() {
    instrumentation.runOnMainSync {
      val tab = CollapsiblePagerNativeTabBarView(instrumentation.targetContext)
      tab.updateItemsJSON("""[{"key":"a","title":"Wide glyphs pygy"}]""")
      tab.updateStyle(44.0, "text", 0.0, 0.0, 0.0, 18.0, "serif", 0.0, 0.0)
      val row = tab.getChildAt(0) as HorizontalScrollView
      val content = row.getChildAt(0) as ViewGroup
      val button = (0 until content.childCount).map(content::getChildAt).filterIsInstance<TextView>().single()
      val normal = com.facebook.react.common.assets.ReactFontManager.getInstance()
        .getTypeface("serif", Typeface.NORMAL, instrumentation.targetContext.assets)
      val expected = if (android.os.Build.VERSION.SDK_INT >= 28) Typeface.create(normal, 600, normal.isItalic)
        else Typeface.create(normal, Typeface.BOLD)
      for (config in listOf("{}", """{"activeFontFamily":""}""")) {
        tab.updateConfigJSON(config)
        tab.measure(View.MeasureSpec.makeMeasureSpec(320, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(44, View.MeasureSpec.EXACTLY))
        tab.layout(0, 0, 320, 44)
        assertEquals(expected, button.typeface)
        if (android.os.Build.VERSION.SDK_INT >= 28) assertEquals(600, button.typeface.weight)
      }
      tab.updateConfigJSON("""{"activeFontFamily":"monospace"}""")
      tab.setProgress(0f)
      val explicit = com.facebook.react.common.assets.ReactFontManager.getInstance()
        .getTypeface("monospace", Typeface.NORMAL, instrumentation.targetContext.assets)
      assertEquals(explicit, button.typeface)
      tab.updateStyle(44.0, "text", 0.0, 0.0, 0.0, 18.0, null, 0.0, 0.0)
      tab.updateConfigJSON("{}")
      tab.setProgress(0f)
      val system = if (android.os.Build.VERSION.SDK_INT >= 28) Typeface.create(Typeface.create("sans-serif", Typeface.NORMAL), 600, false)
        else Typeface.create("sans-serif", Typeface.BOLD)
      assertEquals(system, button.typeface)
    }
  }

}
