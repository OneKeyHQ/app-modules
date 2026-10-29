package com.onekey.bottomtabs

import android.content.Intent
import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Choreographer
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.activity.ComponentActivity
import com.facebook.react.uimanager.RootView
import com.facebook.react.uimanager.PointerEvents
import com.facebook.react.uimanager.ReactPointerEventsView
import com.facebook.react.views.view.ReactViewGroup
import com.facebook.react.internal.ChoreographerProvider
import com.facebook.react.modules.core.ReactChoreographer
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader

class BottomTabsDrawerTestActivity : ComponentActivity()

private class DrawerTestRoot(context: Context) : ReactViewGroup(context), RootView {
  var started = 0
  var ended = 0
  var layouts = 0
  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    // Fabric owns direct-child bounds. Perform its initial/resize layout only;
    // retain ReactViewGroup's real no-op requestLayout for descendant updates.
    layouts++
    for (index in 0 until childCount) {
      val child = getChildAt(index)
      child.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
      child.layout(0, 0, width, height)
    }
  }
  override fun onChildStartedNativeGesture(childView: View?, ev: MotionEvent) { started++ }
  override fun onChildEndedNativeGesture(childView: View, ev: MotionEvent) { ended++ }
  override fun handleException(t: Throwable) { throw t }
}

private class DrawerPointerOverlay(context: Context, override var pointerEvents: PointerEvents) :
  FrameLayout(context), ReactPointerEventsView

/** Executes the shipping native host; consumer React hit-testing needs app acceptance. */
@Suppress("DEPRECATION")
class BottomTabsDrawerTest : InstrumentationTestCase() {
  private lateinit var activity: BottomTabsDrawerTestActivity
  private lateinit var drawer: BottomTabsDrawerView
  private lateinit var main: FrameLayout
  private lateinit var root: DrawerTestRoot
  private val events = mutableListOf<Boolean>()
  private var clicks = 0

  override fun setUp() {
    super.setUp()
    activity = instrumentation.startActivitySync(Intent(instrumentation.context, BottomTabsDrawerTestActivity::class.java)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as BottomTabsDrawerTestActivity
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      drawer = BottomTabsDrawerView(activity)
      drawer.setDrawerEnabled(true)
      drawer.swipeEnabled = true
      drawer.addReactChild(View(activity).apply { setBackgroundColor(Color.DKGRAY) }, 0)
      main = FrameLayout(activity).apply {
        setBackgroundColor(Color.WHITE)
        addView(Button(activity).apply { text = "Foreground"; setOnClickListener { clicks++ } }, FrameLayout.LayoutParams(-1, -1))
      }
      drawer.addReactChild(main, 1)
      drawer.onDrawerOpenChange = { events.add(it); drawer.setDrawerOpen(it) }
      mountDrawer()
    }
    settle()
  }

  override fun tearDown() {
    onMain { drawer.dispose(); activity.finish() }
    super.tearDown()
  }

  fun testDragMovesFullForegroundAndSettlesOnceWithoutClickingContent() {
    onMain { drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f) }
    settle()
    onMain {
      assertEquals(listOf(true), events)
      assertEquals(0, clicks)
      assertEquals(1, root.started)
      assertEquals(1, root.ended)
      assertEquals(drawer.width, main.width)
      assertEquals(drawer.height, main.height)
      assertEquals(drawer.width * .78f, drawer.getChildAt(1).translationX, 1f)
      assertEquals(drawer.width * .78f, drawer.getReactChild(0).width.toFloat(), 1f)
      val dim = (drawer.getChildAt(1) as FrameLayout).getChildAt(1)
      assertEquals(drawer.width, dim.width)
      assertEquals(drawer.height, dim.height)
      assertEquals(View.VISIBLE, dim.visibility)
      assertEquals(.7f, dim.alpha)
      // A prop echo has no second event or animation.
      drawer.setDrawerOpen(true)
      assertEquals(listOf(true), events)
      tap(drawer.width * .9f, 150f)
    }
    settle()
    onMain {
      assertEquals(listOf(true, false), events)
      assertEquals(0f, drawer.getChildAt(1).translationX)
      assertEquals(0, clicks)
    }
  }

  fun testVerticalBottomAndDisabledOpeningDoNotStealGestures() {
    onMain {
      drawer.bottomBarHeight = 100f
      drag(drawer.width * .3f, 150f, drawer.width * .3f + 2f, 500f)
      drag(drawer.width * .3f, drawer.height - 10f, drawer.width * .95f, drawer.height - 10f)
      drawer.swipeEnabled = false
      drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f)
    }
    settle()
    onMain { assertTrue(events.isEmpty()); assertEquals(0f, drawer.getChildAt(1).translationX) }
  }

  fun testCancellationAndNewPropCancelStaleUserCompletion() {
    onMain { drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f, cancel = true) }
    settle()
    onMain { assertTrue(events.isEmpty()); assertEquals(0f, drawer.getChildAt(1).translationX) }
    onMain {
      drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f)
      drawer.setDrawerOpen(true)
      drawer.setDrawerOpen(false)
    }
    settle()
    onMain { assertTrue(events.isEmpty()); assertEquals(0f, drawer.getChildAt(1).translationX) }
  }

  fun testUserCanOverrideAnUnfinishedExternalTarget() {
    onMain { drawer.setDrawerOpen(true) }
    SystemClock.sleep(100)
    onMain { tap(drawer.width * .9f, 150f) }
    settle()
    onMain { assertEquals(listOf(false), events); assertEquals(0f, drawer.getChildAt(1).translationX) }
    onMain { drawer.setDrawerOpen(true) }
    SystemClock.sleep(100)
    onMain { drag(drawer.width * .9f, 150f, drawer.width * .55f, 150f, cancel = true) }
    settle()
    onMain {
      assertEquals(listOf(false), events)
      assertEquals(drawer.width * .78f, drawer.getChildAt(1).translationX, 1f)
    }
  }

  fun testBackClosingWithoutOpeningSwipesAndDetachCleanup() {
    onMain { drawer.swipeEnabled = false; drawer.setDrawerOpen(true) }
    settle()
    onMain { activity.onBackPressedDispatcher.onBackPressed() }
    settle()
    onMain {
      assertEquals(listOf(false), events)
      drawer.setDrawerOpen(true)
    }
    SystemClock.sleep(80)
    onMain { activity.setContentView(View(activity)) }
    settle()
    onMain {
      assertEquals(listOf(false), events)
      mountDrawer()
    }
    settle()
    onMain {
      assertEquals(drawer.width * .78f, drawer.getChildAt(1).translationX, 1f)
      drawer.setDrawerEnabled(false)
      assertEquals(0f, drawer.getChildAt(1).translationX)
      assertSame(main, drawer.getReactChild(1))
    }
  }

  fun testResizeCompletesUserSettlementAndPreservesSlots() {
    onMain {
      drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f)
      drawer.layout(0, 0, drawer.width - 40, drawer.height - 20)
      assertEquals(listOf(true), events)
      assertSame(main, drawer.getReactChild(1))
      assertEquals(drawer.width * .78f, drawer.getChildAt(1).translationX, 1f)
    }
    settle()
    onMain { assertEquals(listOf(true), events) }
  }

  fun testBothShippingNativeBarsMoveWithTheForeground() {
    lateinit var tabs: BottomTabsView
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      ReactChoreographer.initialize(object : ChoreographerProvider {
        override fun getChoreographer() = object : ChoreographerProvider.Choreographer {
          private val delegate = Choreographer.getInstance()
          override fun postFrameCallback(callback: Choreographer.FrameCallback) = delegate.postFrameCallback(callback)
          override fun removeFrameCallback(callback: Choreographer.FrameCallback) = delegate.removeFrameCallback(callback)
        }
      })
      tabs = BottomTabsView(activity)
      tabs.addView(View(activity), 0, FrameLayout.LayoutParams(-1, -1))
      tabs.updateItems(mutableListOf(TabInfo("home", "Home")))
      tabs.setSelectedItem("home")
      main.removeAllViews()
      main.addView(tabs, FrameLayout.LayoutParams(-1, -1))
      layoutFixtureContent()
    }
    for (style in listOf("default", "floating")) {
      onMain { tabs.setAndroidTabBarStyle(style); drawer.setDrawerOpen(false) }
      settle()
      val before = IntArray(2)
      onMain { tabs.getLocationOnScreen(before); drawer.setDrawerOpen(true) }
      settle()
      onMain {
        val after = IntArray(2)
        tabs.getLocationOnScreen(after)
        assertEquals(drawer.width * .78f, (after[0] - before[0]).toFloat(), 1f)
        assertEquals(drawer.width, tabs.width)
        assertEquals(drawer.height, tabs.height)
        assertTrue(tabs.layoutHolder.height > 0)
      }
    }
    onMain { tabs.onDropViewInstance() }
  }

  fun testHorizontalChildKeepsItsGestureAtBoundaryThroughPointerOverlays() {
    lateinit var scroll: HorizontalScrollView
    onMain {
      main.removeAllViews()
      scroll = HorizontalScrollView(activity).apply {
        addView(View(activity).apply { minimumWidth = drawer.width * 3; minimumHeight = 300 }, FrameLayout.LayoutParams(drawer.width * 3, 300))
      }
      main.addView(scroll, FrameLayout.LayoutParams(-1, 300))
      val overlay = DrawerPointerOverlay(activity, PointerEvents.BOX_NONE).apply { z = 20f }
      overlay.addView(DrawerPointerOverlay(activity, PointerEvents.NONE), FrameLayout.LayoutParams(-1, -1))
      main.addView(overlay, FrameLayout.LayoutParams(-1, -1))
      layoutFixtureContent()
    }
    settle()
    onMain {
      scroll.scrollTo((drawer.width * .3f).toInt(), 0)
      assertTrue("Horizontal fixture must consume right: x=${scroll.scrollX}, childWidth=${scroll.getChildAt(0).width}, width=${scroll.width}", scroll.canScrollHorizontally(-1))
      drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f)
    }
    settle()
    onMain {
      assertTrue(events.isEmpty())
      assertEquals(0, scroll.scrollX)
      assertEquals(0f, drawer.getChildAt(1).translationX)
      drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f)
    }
    settle()
    onMain { assertEquals(listOf(true), events) }
  }

  fun testRealViewPagerConsumesRightSwipeUntilItsFirstPage() {
    lateinit var pager: ViewPager2
    onMain {
      main.removeAllViews()
      pager = ViewPager2(activity)
      pager.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = 3
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
          object : RecyclerView.ViewHolder(FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
          }) {}
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
      }
      main.addView(pager, FrameLayout.LayoutParams(-1, -1))
      pager.setCurrentItem(1, false)
      layoutFixtureContent()
    }
    settle()
    onMain { drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f) }
    settle()
    onMain {
      assertTrue(events.isEmpty())
      assertEquals(0, pager.currentItem)
      drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f)
    }
    settle()
    onMain {
      assertEquals(listOf(true), events)
      drawer.setDrawerOpen(false)
      pager.setCurrentItem(1, false)
      pager.isUserInputEnabled = false
      layoutFixtureContent()
    }
    settle()
    onMain {
      assertTrue("Disabled pager still reports a range, which must not claim the gesture", pager.canScrollHorizontally(-1))
      drag(drawer.width * .3f, 150f, drawer.width * .95f, 150f)
    }
    settle()
    onMain { assertEquals(listOf(true, true), events) }
  }

  fun testMountedSlotGetsBoundsWithoutAnotherReactAncestorLayout() {
    onMain {
      val previousRootLayouts = root.layouts
      val replacement = ReactViewGroup(activity)
      drawer.removeReactChild(1)
      drawer.addReactChild(replacement, 1)
      assertEquals(drawer.width, replacement.measuredWidth)
      assertEquals(drawer.width, replacement.width)
      assertEquals(drawer.height, replacement.height)
      assertEquals(previousRootLayouts, root.layouts)
      drawer.setDrawerOpen(true)
    }
    settle()
    onMain {
      val dim = (drawer.getChildAt(1) as FrameLayout).getChildAt(1)
      assertEquals(drawer.width, dim.width)
      assertEquals(drawer.height, dim.height)
      tap(drawer.width * .9f, 150f)
    }
    settle()
    onMain { assertEquals(listOf(false), events) }
  }

  private fun tap(x: Float, y: Float) {
    val start = SystemClock.uptimeMillis()
    for ((action, offset) in listOf(MotionEvent.ACTION_DOWN to 0L, MotionEvent.ACTION_UP to 32L)) {
      val event = MotionEvent.obtain(start, start + offset, action, x, y, 0)
      root.dispatchTouchEvent(event)
      event.recycle()
    }
  }

  private fun layoutFixtureContent() {
    // Raw native test fixtures have no Fabric mount transaction to provide
    // their initial bounds after addView. Simulate that transaction locally;
    // do not re-layout the React ancestor or mask later descendant requests.
    main.measure(View.MeasureSpec.makeMeasureSpec(drawer.width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(drawer.height, View.MeasureSpec.EXACTLY))
    main.layout(0, 0, drawer.width, drawer.height)
  }

  private fun drag(x0: Float, y0: Float, x1: Float, y1: Float, cancel: Boolean = false) {
    val start = SystemClock.uptimeMillis()
    fun send(action: Int, x: Float, y: Float, offset: Long) {
      val event = MotionEvent.obtain(start, start + offset, action, x, y, 0)
      drawer.dispatchTouchEvent(event)
      event.recycle()
    }
    send(MotionEvent.ACTION_DOWN, x0, y0, 0)
    for (step in 1..8) {
      send(MotionEvent.ACTION_MOVE, x0 + (x1 - x0) * step / 8, y0 + (y1 - y0) * step / 8, step * 50L)
    }
    send(if (cancel) MotionEvent.ACTION_CANCEL else MotionEvent.ACTION_UP, x1, y1, 450)
  }
  private fun onMain(action: () -> Unit) {
    var failure: Throwable? = null
    instrumentation.runOnMainSync { try { action() } catch (error: Throwable) { failure = error } }
    failure?.let { throw it }
  }
  private fun mountDrawer() {
    (drawer.parent as? ViewGroup)?.removeView(drawer)
    root = DrawerTestRoot(activity)
    root.addView(drawer, FrameLayout.LayoutParams(-1, -1))
    activity.setContentView(root)
  }
  private fun settle() { instrumentation.waitForIdleSync(); SystemClock.sleep(650); instrumentation.waitForIdleSync() }
}
