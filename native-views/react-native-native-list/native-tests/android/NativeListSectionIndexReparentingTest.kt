package com.margelo.nitro.nativelist

import android.app.Activity
import android.content.Intent
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.soloader.SoLoader
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject

/** Exercises the shipping NativeListView's pre-draw rail attachment on real Android views. */
@Suppress("DEPRECATION")
class NativeListSectionIndexReparentingTest : InstrumentationTestCase() {
  private lateinit var activity: Activity
  private lateinit var list: NativeListView

  override fun setUp() {
    super.setUp()
    val intent = Intent(instrumentation.context, NativeListReorderTestActivity::class.java)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    activity = instrumentation.startActivitySync(intent)
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      OneKeyLog.init(activity)
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "SectionIndexTest", 1)
      list = NativeListView(context)
      activity.setContentView(list)
    }
    awaitLayout()
    onMain {
      list.visibility = View.INVISIBLE
      list.applySnapshot(snapshot())
      assertSame(content(), rail().parent)
      assertTrue(rail().visibility == View.VISIBLE)
    }
  }

  override fun tearDown() {
    onMain {
      activity.setContentView(View(activity))
      list.dispose()
      activity.finish()
    }
    super.tearDown()
  }

  fun testListToWindowFinishesPreviousTransition() {
    onMain {
      val rail = rail()
      val content = content()
      val window = window()
      content.startViewTransition(rail)
      list.visibility = View.VISIBLE
      list.viewTreeObserver.dispatchOnPreDraw()
      assertSingleParent(rail, window, content)
      content.endViewTransition(rail)
      assertSingleParent(rail, window, content)

      list.visibility = View.INVISIBLE
      list.viewTreeObserver.dispatchOnPreDraw()
      assertSingleParent(rail, content, window)
      list.visibility = View.VISIBLE
      list.viewTreeObserver.dispatchOnPreDraw()
      assertSingleParent(rail, window, content)
    }
  }

  fun testWindowToListFinishesPreviousTransition() {
    onMain {
      val rail = rail()
      val content = content()
      val window = window()
      list.visibility = View.VISIBLE
      list.viewTreeObserver.dispatchOnPreDraw()
      assertSingleParent(rail, window, content)

      window.startViewTransition(rail)
      list.visibility = View.INVISIBLE
      list.viewTreeObserver.dispatchOnPreDraw()
      assertSingleParent(rail, content, window)
      window.endViewTransition(rail)
      assertSingleParent(rail, content, window)

      list.applySnapshot(snapshot(enabled = false, generation = 2))
      assertEquals(View.GONE, rail.visibility)
      list.applySnapshot(snapshot(enabled = true, generation = 3))
      assertEquals(View.VISIBLE, rail.visibility)
      assertSingleParent(rail, content, window)
    }
  }

  fun testDetachReturnAndQuickReattachKeepTheSameRail() {
    lateinit var rail: View
    lateinit var content: FrameLayout
    lateinit var window: FrameLayout
    onMain {
      rail = rail()
      content = content()
      window = window()
      list.layoutDirection = View.LAYOUT_DIRECTION_RTL
      list.visibility = View.VISIBLE
      list.viewTreeObserver.dispatchOnPreDraw()
      assertSingleParent(rail, window, content)
      activity.setContentView(View(activity))
    }
    instrumentation.waitForIdleSync()
    onMain {
      assertSingleParent(rail, content, window)
      activity.setContentView(list)
    }
    instrumentation.waitForIdleSync()
    onMain {
      assertSingleParent(rail, window, content)
      activity.setContentView(View(activity))
      activity.setContentView(list)
    }
    instrumentation.waitForIdleSync()
    onMain { assertSingleParent(rail, window, content) }
  }

  private fun assertSingleParent(rail: View, expected: ViewGroup, previous: ViewGroup) {
    assertSame(expected, rail.parent)
    assertEquals(1, (0 until expected.childCount).count { expected.getChildAt(it) === rail })
    assertEquals(-1, previous.indexOfChild(rail))
  }

  private fun awaitLayout() {
    repeat(100) {
      var ready = false
      onMain { ready = list.isAttachedToWindow && list.width > 0 && list.height > 0 && window().width > 0 }
      if (ready) return
      SystemClock.sleep(50)
    }
    fail("NativeListView and window host did not lay out")
  }

  private fun snapshot(enabled: Boolean = true, generation: Int = 1): String =
    JSONObject().put("schemaVersion", 1).put("generation", generation)
      .put("layout", JSONObject().put("kind", "sectioned"))
      .put("capabilities", JSONObject().put("sectionIndex", JSONObject()
        .put("enabled", enabled).put("centeredInWindow", true)))
      .put("rows", JSONArray().put(JSONObject()
        .put("key", "section-a").put("type", "sectionHeader")
        .put("title", "A").put("indexTitle", "A").put("height", 44)))
      .toString()

  private fun rail(): View = field(list, "sectionIndexView")
  private fun content(): FrameLayout = field(list, "contentContainer")
  private fun window(): FrameLayout = list.rootView as FrameLayout

  private fun onMain(block: () -> Unit) {
    var failure: Throwable? = null
    instrumentation.runOnMainSync {
      try {
        block()
      } catch (error: Throwable) {
        failure = error
      }
    }
    failure?.let { throw it }
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> field(owner: Any, name: String): T {
    var type: Class<*>? = owner.javaClass
    while (type != null) {
      try {
        return type.getDeclaredField(name).apply { isAccessible = true }.get(owner) as T
      } catch (_: NoSuchFieldException) {
        type = type.superclass
      }
    }
    error("Missing production field $name")
  }
}
