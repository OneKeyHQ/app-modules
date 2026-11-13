package com.margelo.nitro.nativelist

import android.app.Activity
import android.content.Intent
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.soloader.SoLoader
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject

/**
 * Real pointer dispatch for the Activity description action. Its text, from
 * `descriptionActionStart` on, opens the action; the text before it and the
 * unused width of the description line reach the row press.
 */
@Suppress("DEPRECATION")
class NativeListDescriptionActionTouchTest : InstrumentationTestCase() {
  private lateinit var activity: Activity
  private lateinit var list: NativeListView
  private lateinit var recycler: RecyclerView
  private val events = mutableListOf<JSONObject>()
  private var generation = 0

  override fun setUp() {
    super.setUp()
    activity = instrumentation.startActivitySync(
      Intent(instrumentation.context, NativeListReorderTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      OneKeyLog.init(activity)
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "DescriptionActionTest", 1)
      list = NativeListView(context)
      list.onRowAction = { events.add(JSONObject(it)) }
      activity.setContentView(list)
      recycler = field(list, "recyclerView")
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

  fun testWholeTextOpensTheActionWithoutAStart() {
    // A label holding the separator a time prefix would use is one action.
    val description = bind("Router • V2", "address-info")
    val text = textRegion(description)
    assertEquals(0, onMainValue { description.touchableTextStart })
    assertEquals(listOf("address-info:description"), tap(description, text.left + slop() + dp(4)))
    assertEquals(listOf("address-info:description"), tap(description, text.right - slop() - dp(4)))
    assertEquals(listOf("press"), tap(description, blankX(description, text)))
  }

  fun testTextBeforeTheStartReachesTheRowPress() {
    val prefix = "08:48 • "
    val description = bind("${prefix}Router • V2", "address-info", prefix.length)
    val text = textRegion(description)
    assertTrue(text.left + slop() > dp(20))
    assertEquals(listOf("press"), tap(description, dp(4)))
    assertEquals(listOf("address-info:description"), tap(description, text.left + slop() + dp(4)))
    assertEquals(listOf("address-info:description"), tap(description, text.right - slop() - dp(4)))
    assertEquals(listOf("press"), tap(description, blankX(description, text)))
  }

  fun testStartCountsUtf16UnitsOfTheDescription() {
    val prefix = "🕘 08:48 • "
    assertEquals(11, prefix.length)
    val description = bind("${prefix}Router • V2", "address-info", prefix.length)
    val text = textRegion(description)
    assertEquals(listOf("press"), tap(description, dp(4)))
    assertEquals(listOf("address-info:description"), tap(description, text.left + slop() + dp(4)))
    assertEquals(listOf("address-info:description"), tap(description, text.right - slop() - dp(4)))
  }

  fun testStartAtTheEndLeavesNoAction() {
    // 4294967296 is a safe integer past Int range: narrowed it would wrap to
    // 0 and make the whole text the action instead of clamping to the end.
    for (start in listOf("08:48 • ".length.toLong(), 99L, 4_294_967_296L)) {
      val description = bind("08:48 • ", "address-info", start)
      assertTrue(onMainValue { description.touchableTextRegions.isEmpty() })
      assertEquals(listOf("press"), tap(description, dp(4)))
    }
  }

  fun testRebindingAndRemovingTheActionLeavesNoStaleStart() {
    val prefix = "08:48 • "
    var description = bind("${prefix}Router • V2", "address-info", prefix.length)
    assertEquals(listOf("press"), tap(description, dp(4)))

    // The same row without a start: the whole text again.
    description = bind("${prefix}Router • V2", "address-info")
    assertEquals(0, onMainValue { description.touchableTextStart })
    assertEquals(listOf("address-info:description"), tap(description, dp(4)))

    // Without the action a start means nothing and every touch is the row's.
    description = bind("${prefix}Router • V2", null, prefix.length)
    assertNull(onMainValue { description.touchableTextStart })
    assertFalse(onMainValue { description.isClickable })
    assertEquals(listOf("press"), tap(description, dp(4)))
    assertEquals(listOf("press"), tap(description, onMainValue { description.width } - dp(4)))

    description = bind("${prefix}Router • V2", "address-info", prefix.length)
    assertEquals(prefix.length, onMainValue { description.touchableTextStart })
    onMain { field<NativeListActivityDetailsView>(holder().rowView, "details").recycle() }
    assertNull(onMainValue { description.touchableTextStart })
  }

  private fun bind(description: String, actionKey: String?, actionStart: Number? = null): NativeListTextView {
    val row = JSONObject().put("key", "tx").put("type", "activity").put("title", "Swap")
      .put("description", description)
      .put("leading", JSONObject().put("kind", "icon").put("name", "ArrowTopOutline"))
      .put("amounts", JSONArray().put(JSONObject().put("key", "send").put("text", "-1")))
    actionKey?.let { row.put("descriptionActionKey", it) }
    actionStart?.let { row.put("descriptionActionStart", it.toLong()) }
    generation += 1
    val revision = generation
    row.put("revision", revision)
    onMain {
      list.applySnapshot(JSONObject()
        .put("schemaVersion", 1).put("generation", revision)
        .put("layout", JSONObject().put("kind", "linear"))
        .put("rows", JSONArray().put(row)).toString())
    }
    repeat(100) {
      val view = onMainValue {
        val current = (recycler.findViewHolderForAdapterPosition(0) as? NativeListViewHolder)?.rowView
        val item = current?.tag as? NativeListItem
        if (current == null || recycler.isLayoutRequested || item?.json?.optInt("revision") != revision) null
        else field<NativeListTextView>(field<View>(current, "details"), "description")
          .takeIf { it.width > 0 && it.layout != null }
      }
      if (view != null) return view
      SystemClock.sleep(50)
    }
    fail("Activity row did not bind: $description")
    throw IllegalStateException()
  }

  private fun holder(): NativeListViewHolder =
    recycler.findViewHolderForAdapterPosition(0) as NativeListViewHolder

  /** The single touchable line of the bound description, in view coordinates and including the slop. */
  private fun textRegion(description: NativeListTextView): NativeListTouchRegion =
    onMainValue { description.touchableTextRegions }.also { assertEquals(1, it.size) }.single()

  /** An x on the description line well clear of its text, inside the view. */
  private fun blankX(description: NativeListTextView, text: NativeListTouchRegion): Float {
    val width = onMainValue { description.width }
    assertTrue("the description needs blank width beside its text", width > text.right + dp(40))
    return width - dp(4)
  }

  /** Taps the view at view-local [x] through the window; returns `actionKey[:source]` of each emitted action. */
  private fun tap(view: View, x: Float): List<String> {
    events.clear()
    val point = onMainValue {
      val location = IntArray(2)
      view.getLocationOnScreen(location)
      Pair(location[0] + x, location[1] + view.height / 2f)
    }
    val downTime = SystemClock.uptimeMillis()
    instrumentation.sendPointerSync(MotionEvent.obtain(
      downTime, downTime, MotionEvent.ACTION_DOWN, point.first, point.second, 0))
    instrumentation.sendPointerSync(MotionEvent.obtain(
      downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, point.first, point.second, 0))
    instrumentation.waitForIdleSync()
    return events.map { event ->
      val source = event.optJSONObject("anchor")?.optString("source").orEmpty()
      event.getString("actionKey") + if (source == "description") ":description" else ""
    }
  }

  private fun dp(value: Int): Float = value * activity.resources.displayMetrics.density

  private fun slop(): Float = dp(8)

  private fun onMain(block: () -> Unit) {
    var failure: Throwable? = null
    instrumentation.runOnMainSync {
      try { block() } catch (error: Throwable) { failure = error }
    }
    failure?.let { throw it }
  }

  private fun <T> onMainValue(block: () -> T): T {
    var value: T? = null
    onMain { value = block() }
    @Suppress("UNCHECKED_CAST")
    return value as T
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
