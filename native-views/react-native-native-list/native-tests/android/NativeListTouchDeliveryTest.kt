package com.margelo.nitro.nativelist

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.JSTouchDispatcher
import com.facebook.react.uimanager.RootView
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.BatchEventDispatchedListener
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.uimanager.events.EventDispatcherListener
import com.facebook.soloader.SoLoader
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject

/** Real Android dispatch and RN touch cancellation, without a JavaScript runtime. */
@Suppress("DEPRECATION")
class NativeListTouchDeliveryTest : InstrumentationTestCase() {
  private lateinit var activity: Activity
  private lateinit var root: TouchRoot
  private lateinit var list: NativeListView
  private lateinit var slot: View
  private lateinit var recycler: RecyclerView
  private var clicks = 0
  private var downTime = 0L
  private var eventTime = 0L
  private var x = 0f
  private var y = 0f

  override fun setUp() {
    super.setUp()
    activity = instrumentation.startActivitySync(
      Intent(instrumentation.context, NativeListReorderTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      OneKeyLog.init(activity)
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "TouchTest", 1)
      root = TouchRoot(context).apply { id = 10 }
      list = NativeListView(context).apply { id = 20 }
      slot = View(context).apply {
        id = 30
        setOnClickListener { clicks++ }
      }
      root.addView(list, FrameLayout.LayoutParams(-1, -1))
      activity.setContentView(root)
      val recyclerField = NativeListView::class.java.getDeclaredField("recyclerView")
      recyclerField.isAccessible = true
      recycler = recyclerField.get(list) as RecyclerView
      list.mountContainerSlot(slot, 0)
      list.setContainerSlotHeightsJson("[200,0,0,0]")
      val rows = JSONArray()
      repeat(30) { rows.put(JSONObject().put("key", "row-$it")
        .put("type", "action").put("title", "Row $it").put("height", 60)) }
      list.applySnapshot(JSONObject().put("schemaVersion", 1).put("generation", 1)
        .put("layout", JSONObject().put("kind", "linear"))
        .put("rows", rows).toString())
    }
    awaitSlot()
  }

  override fun tearDown() {
    onMain {
      activity.setContentView(View(activity))
      list.dispose()
      activity.finish()
    }
    super.tearDown()
  }

  fun testVerticalDragCancelsReactTouchOnceAndEndsOnRelease() {
    onMain {
      beginTouch()
      val slop = ViewConfiguration.get(activity).scaledTouchSlop.toFloat()
      touch(MotionEvent.ACTION_MOVE, y - slop / 2)
      assertEquals(listOf("topTouchStart", "topTouchMove"), root.touches)
      assertTrue(root.starts.isEmpty())
      touch(MotionEvent.ACTION_MOVE, y - slop * 3)
      touch(MotionEvent.ACTION_MOVE, y - slop * 6)
      assertEquals(1, root.touches.count { it == "topTouchCancel" })
      assertEquals(listOf(list), root.starts)
      assertTrue(recycler.computeVerticalScrollOffset() > 0)
      touch(MotionEvent.ACTION_UP, y - slop * 6)
      assertEquals(listOf(MotionEvent.ACTION_UP), root.ends)
      assertFalse(root.touches.contains("topTouchEnd"))
    }
    instrumentation.waitForIdleSync()
    assertEquals(0, clicks)
  }

  fun testOrdinaryTapRetainsReactTouchEnd() {
    onMain {
      beginTouch()
      touch(MotionEvent.ACTION_UP, y)
      assertEquals(listOf("topTouchStart", "topTouchEnd"), root.touches)
      assertTrue(root.starts.isEmpty())
      assertTrue(root.ends.isEmpty())
    }
    instrumentation.waitForIdleSync()
    assertEquals(1, clicks)
  }

  fun testCancellationReleasesGestureBeforeNextTap() {
    onMain {
      beginTouch()
      touch(MotionEvent.ACTION_MOVE, y - ViewConfiguration.get(activity).scaledTouchSlop * 3)
      touch(MotionEvent.ACTION_CANCEL, y)
      assertEquals(listOf(list), root.starts)
      assertEquals(listOf(MotionEvent.ACTION_CANCEL), root.ends)
      assertEquals(1, root.touches.count { it == "topTouchCancel" })
      beginTouch()
      touch(MotionEvent.ACTION_UP, y)
      assertEquals(listOf("topTouchStart", "topTouchEnd"), root.touches.takeLast(2))
      assertEquals(1, root.starts.size)
      assertEquals(1, root.ends.size)
    }
    instrumentation.waitForIdleSync()
    assertEquals(1, clicks)
  }

  fun testRemovalCancelsDragBeforeRetainedListAndSlotReuse() {
    onMain {
      beginTouch()
      touch(MotionEvent.ACTION_MOVE, y - ViewConfiguration.get(activity).scaledTouchSlop * 3)
      root.removeView(list)
      assertEquals(listOf(MotionEvent.ACTION_CANCEL), root.ends)
      root.addView(list, FrameLayout.LayoutParams(-1, -1))
    }
    awaitSlot()
    onMain {
      beginTouch()
      touch(MotionEvent.ACTION_UP, y)
      assertEquals(listOf("topTouchStart", "topTouchEnd"), root.touches.takeLast(2))
      assertEquals(1, root.starts.size)
      assertEquals(1, root.ends.size)
    }
    instrumentation.waitForIdleSync()
    assertEquals(1, clicks)
  }

  private fun beginTouch() {
    val bounds = Rect()
    assertTrue(slot.getGlobalVisibleRect(bounds))
    val origin = IntArray(2)
    root.getLocationOnScreen(origin)
    x = bounds.exactCenterX() - origin[0]
    y = bounds.exactCenterY() - origin[1]
    downTime = SystemClock.uptimeMillis()
    eventTime = downTime
    touch(MotionEvent.ACTION_DOWN, y)
    assertEquals(slot.id, root.lastTarget)
  }

  private fun touch(action: Int, targetY: Float) {
    val event = MotionEvent.obtain(downTime, eventTime, action, x, targetY, 0)
    try {
      root.dispatchTouchEvent(event)
    } finally {
      event.recycle()
    }
    eventTime += 16
  }

  private fun awaitSlot() {
    repeat(100) {
      var ready = false
      onMain { ready = slot.isAttachedToWindow && slot.width > 0 && slot.height > 0 }
      if (ready) return
      SystemClock.sleep(50)
    }
    fail("NativeList container slot did not lay out")
  }

  private fun onMain(block: () -> Unit) {
    var failure: Throwable? = null
    instrumentation.runOnMainSync {
      try { block() } catch (error: Throwable) { failure = error }
    }
    failure?.let { throw it }
  }

  private class TouchRoot(context: ThemedReactContext) : FrameLayout(context), RootView {
    val touches = mutableListOf<String>()
    val starts = mutableListOf<View?>()
    val ends = mutableListOf<Int>()
    var lastTarget = View.NO_ID
    private val dispatcher = JSTouchDispatcher(this)
    private val events = object : EventDispatcher {
      override fun dispatchEvent(event: Event<*>) {
        touches.add(event.eventName)
        lastTarget = event.viewTag
        event.dispose()
      }
      override fun dispatchAllEvents() = Unit
      override fun addListener(listener: EventDispatcherListener) = Unit
      override fun removeListener(listener: EventDispatcherListener) = Unit
      override fun addBatchEventDispatchedListener(listener: BatchEventDispatchedListener) = Unit
      override fun removeBatchEventDispatchedListener(listener: BatchEventDispatchedListener) = Unit
      override fun onCatalystInstanceDestroyed() = Unit
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
      dispatcher.handleTouchEvent(event, events)
      return super.dispatchTouchEvent(event)
    }

    override fun onChildStartedNativeGesture(childView: View?, ev: MotionEvent) {
      starts.add(childView)
      dispatcher.onChildStartedNativeGesture(ev, events)
    }

    override fun onChildEndedNativeGesture(childView: View, ev: MotionEvent) {
      ends.add(ev.actionMasked)
      dispatcher.onChildEndedNativeGesture(ev, events)
    }

    override fun handleException(t: Throwable) { throw t }
  }
}
