package com.margelo.nitro.nativelist

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.soloader.SoLoader
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject

class NativeListReorderTestActivity : Activity()

/** Real Android views and shipping callbacks; this is not a touch-gesture test. */
@Suppress("DEPRECATION")
class NativeListReorderCancellationTest : InstrumentationTestCase() {
  private lateinit var activity: Activity
  private lateinit var list: NativeListView
  private lateinit var recycler: RecyclerView
  private val events = mutableListOf<JSONObject>()

  override fun setUp() {
    super.setUp()
    val intent = Intent(instrumentation.context, NativeListReorderTestActivity::class.java)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    activity = instrumentation.startActivitySync(intent)
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      OneKeyLog.init(activity)
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "ReorderTest", 1)
      list = NativeListView(context)
      list.onReorder = { events.add(JSONObject(it)) }
      activity.setContentView(list)
      recycler = field(list, "recyclerView")
      list.applySnapshot(snapshot())
    }
    awaitRows()
  }

  override fun tearDown() {
    onMain {
      activity.setContentView(View(activity))
      list.dispose()
      activity.finish()
    }
    super.tearDown()
  }

  fun testStructuralCancellationDoesNotCommitPreviousMove() {
    onMain {
      val old = helper()
      val a = holder(0)
      val displaced = holder(1)
      old.startDrag(a)
      assertTrue(callback(old).onMove(recycler, a, displaced))
      list.applySnapshot(snapshot(extraRow = true))
      assertCancelled()
      // The old one-shot layout listener must not reintroduce displacement.
      displaced.itemView.layout(0, 4, displaced.itemView.width, 4 + displaced.itemView.height)
      assertEquals(0f, displaced.itemView.translationY)
    }
    awaitRows(listOf("a", "b", "c", "d"))
    onMain {
      assertEquals(listOf("a", "b", "c", "d"), order())
      val next = helper()
      val b = holder(0)
      next.startDrag(b)
      release(next)
    }
    awaitRelease()
    onMain {
      assertTrue(events.isEmpty())
      assertEquals(listOf("a", "b", "c", "d"), order())
    }
  }

  fun testFontCancellationRestoresWalletAndIgnoresOldOwner() {
    lateinit var old: ItemTouchHelper
    lateinit var a: NativeListViewHolder
    onMain {
      old = helper()
      a = holder(0)
      a.itemView.elevation = 3f
      old.startDrag(a)
      callback(old).onChildDraw(Canvas(), recycler, a, 12f, 24f, ItemTouchHelper.ACTION_STATE_DRAG, true)
      assertTrue(field<Boolean>(a.rowView, "compact"))
      assertTrue(callback(old).onMove(recycler, a, holder(1)))
      list.setFontFamiliesJson("{\"regular\":\"sans-serif\"}")
      assertCancelled()
      assertNull(field<RecyclerView.ViewHolder?>(old, "mSelected"))
      field<Runnable>(old, "mScrollRunnable").run()
      assertFalse(field<Boolean>(a.rowView, "compact"))
      assertEquals(0.4f, a.rowView.alpha)
      assertEquals(0f, a.itemView.translationX)
      assertEquals(0f, a.itemView.translationY)
      assertEquals(3f, a.itemView.elevation)
      assertNull(field<NativeListViewHolder?>(list, "activeReorderHolder"))
    }
    awaitRows()
    onMain {
      assertEquals(listOf("a", "b", "c"), order())
      val next = helper()
      val current = holder(0)
      next.startDrag(current)
      assertTrue(field<Boolean>(current.rowView, "compact"))
      callback(old).clearView(recycler, current)
      callback(old).onSelectedChanged(null, ItemTouchHelper.ACTION_STATE_IDLE)
      assertTrue(field<Boolean>(current.rowView, "compact"))
      assertSame(current, field<NativeListViewHolder>(list, "activeReorderHolder"))
      release(next)
    }
    awaitRelease()
    onMain {
      assertTrue(events.isEmpty())
      val current = holder(0)
      val disposing = helper()
      disposing.startDrag(current)
      activity.setContentView(View(activity))
      list.dispose()
      assertNull(field<RecyclerView.ViewHolder?>(disposing, "mSelected"))
      assertNull(field<NativeListViewHolder?>(list, "activeReorderHolder"))
      field<Runnable>(disposing, "mScrollRunnable").run()
      assertTrue(events.isEmpty())
    }
  }

  fun testNewGestureCommitsOnlyItsOwnMove() {
    onMain {
      val old = helper()
      val a = holder(0)
      old.startDrag(a)
      assertTrue(callback(old).onMove(recycler, a, holder(1)))
      list.setFontFamiliesJson("{\"medium\":\"sans-serif-medium\"}")
    }
    awaitRows()
    onMain {
      val next = helper()
      val b = holder(1)
      next.startDrag(b)
      assertTrue(callback(next).onMove(recycler, b, holder(2)))
      release(next)
    }
    awaitRelease()
    awaitRows(listOf("a", "c", "b"))
    onMain {
      assertEquals(1, events.size)
      assertEquals("b", events.single().getString("key"))
      assertEquals(1, events.single().getInt("fromIndex"))
      assertEquals(2, events.single().getInt("toIndex"))
      assertEquals(listOf("a", "c", "b"), order())
      assertEquals(1, events.size)
    }
  }

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

  private fun assertCancelled() {
    assertEquals(RecyclerView.NO_POSITION, field<Int>(list, "dragFrom"))
    assertEquals(RecyclerView.NO_POSITION, field<Int>(list, "dragTo"))
    assertNull(field<List<NativeListItem>?>(list, "pendingReorder"))
    val placeholder = field<Any>(list, "reorderPlaceholderDecoration")
    assertEquals(RecyclerView.NO_POSITION, field<Int>(placeholder, "position"))
    assertFalse(field<Boolean>(list, "reorderRelayoutActive"))
    assertTrue(events.isEmpty())
  }

  private fun helper(): ItemTouchHelper = field(list, "itemTouchHelper")
  private fun callback(helper: ItemTouchHelper): ItemTouchHelper.Callback = field(helper, "mCallback")
  private fun release(helper: ItemTouchHelper) {
    ItemTouchHelper::class.java.getDeclaredMethod(
      "select", RecyclerView.ViewHolder::class.java, Int::class.javaPrimitiveType,
    ).apply { isAccessible = true }.invoke(helper, null, ItemTouchHelper.ACTION_STATE_IDLE)
  }
  private fun holder(index: Int): NativeListViewHolder =
    recycler.findViewHolderForAdapterPosition(index) as NativeListViewHolder
  private fun order(): List<String> = field<NativeListAdapter>(list, "adapter").currentList.map { it.key }

  private fun awaitRows(expectedKeys: List<String> = listOf("a", "b", "c")) {
    repeat(100) {
      var ready = false
      onMain {
        ready = order() == expectedKeys && !recycler.isLayoutRequested &&
          expectedKeys.indices.all { index ->
            val holder = recycler.findViewHolderForAdapterPosition(index) as? NativeListViewHolder
            (holder?.rowView?.tag as? NativeListItem)?.key == expectedKeys[index]
          }
      }
      if (ready) return
      SystemClock.sleep(50)
    }
    fail("Native RecyclerView did not lay out the fixture")
  }

  private fun awaitRelease() {
    repeat(100) {
      var ready = false
      onMain {
        ready = field<NativeListViewHolder?>(list, "activeReorderHolder") == null &&
          field<List<*>>(helper(), "mRecoverAnimations").isEmpty()
      }
      if (ready) return
      SystemClock.sleep(50)
    }
    fail("Native ItemTouchHelper did not complete release")
  }

  private fun snapshot(extraRow: Boolean = false): String {
    fun identity(key: String) = JSONObject().put("key", key).put("type", "identity")
      .put("title", key).put("height", 68).put("draggable", true)
    val group = JSONObject().put("key", "a").put("type", "walletGroup").put("draggable", true)
      .put("parent", identity("a-parent"))
      .put("children", JSONArray().put(identity("a-child")))
      .put("style", JSONObject().put("container", JSONObject().put("opacity", 0.4)))
    val rows = JSONArray().put(group).put(identity("b")).put(identity("c"))
    if (extraRow) rows.put(identity("d"))
    return JSONObject().put("schemaVersion", 1).put("generation", if (extraRow) 2 else 1)
      .put("layout", JSONObject().put("kind", "linear"))
      .put("capabilities", JSONObject().put("reorderable", true)).put("rows", rows).toString()
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
