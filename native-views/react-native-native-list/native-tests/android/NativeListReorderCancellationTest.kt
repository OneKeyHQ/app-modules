package com.margelo.nitro.nativelist

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.TouchTargetHelper
import com.facebook.soloader.SoLoader
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

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

  fun testStructuralTailUsesOneBoundedNativeViewportAndPreservesAcceptedPatches() {
    onMain {
      repeat(3) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(50))
    }
    awaitAdapterCount(50)
    onMain { list.scrollToIndex(36, false, "start") }
    awaitScrollDistanceAtLeast((1000 * activity.resources.displayMetrics.density).roundToInt())
    onMain { list.applySnapshot(tailSnapshot(6, "collapse-a", 1200)) }
    awaitTailMidframe(6)
    onMain {
      val tail = field<Any>(list, "structuralTail")
      val layer = field<FrameLayout>(list, "structuralTailLayer")
      val animator = field<Any>(list, "structuralAnimator")
      assertEquals(6, field<NativeListConfig>(list, "config").items.size)
      assertEquals(44, field<List<*>>(tail, "items").size)
      assertTrue(layer.childCount in 1..128)
      assertTrue(layer.height > 0)
      assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS,
        layer.importantForAccessibility)
      assertEquals(1f, layer.getChildAt(0).alpha)
      assertEquals(NativeListScale.dp(activity.resources, 60),
        layer.getChildAt(0).height)
      assertTrue((0 until layer.childCount).any { index ->
        val host = layer.getChildAt(index)
        layer.top + host.bottom > recycler.top + recycler.paddingTop &&
          layer.top + host.top < recycler.bottom - recycler.paddingBottom
      })
      val spacerBeforeFooter = field<Int>(list, "structuralSpacerHeightPx")
      val reconcileFooter = list.javaClass.getDeclaredMethod(
        "reconcileStructuralFooterHeight", Int::class.javaPrimitiveType).apply { isAccessible = true }
      val nativeMeasuredHeight = (72 * activity.resources.displayMetrics.density).roundToInt()
      reconcileFooter.invoke(list, nativeMeasuredHeight)
      val compensated = field<Int>(list, "structuralSpacerHeightPx")
      assertEquals(spacerBeforeFooter - (16 * activity.resources.displayMetrics.density).roundToInt(),
        compensated)
      reconcileFooter.invoke(list, nativeMeasuredHeight)
      assertEquals(compensated, field<Int>(list, "structuralSpacerHeightPx"))
      list.setContainerSlotHeightsJson("[0,0,72,0]")
      assertEquals(compensated, field<Int>(list, "structuralSpacerHeightPx"))
      assertSame(animator, field<Any>(list, "structuralAnimator"))
      list.applyPatches(JSONArray().put(JSONObject().put("key", "row-2")
        .put("type", "identity").put("changes", JSONObject().put("title", "patched"))).toString())
      assertSame(animator, field<Any>(list, "structuralAnimator"))
      list.applySnapshot(tailSnapshot(6, "collapse-a", 1200, title = "same-id"))
      assertSame(animator, field<Any>(list, "structuralAnimator"))
    }
    awaitTailFinished(6)
    onMain {
      val footerPosition = field<androidx.recyclerview.widget.ConcatAdapter>(list,
        "scrollContentAdapter").itemCount - 1
      val footer = recycler.findViewHolderForAdapterPosition(footerPosition)?.itemView
      assertNotNull("Final Show more footer must be mounted", footer)
      assertTrue(footer!!.top >= recycler.paddingTop)
      assertTrue(footer.bottom <= recycler.height - recycler.paddingBottom)
    }
    onMain { list.applySnapshot(tailSnapshot(50, "expand-a", 1200)) }
    awaitTailMidframe(6)
    onMain {
      val tail = field<Any>(list, "structuralTail")
      val animator = field<Any>(list, "structuralAnimator")
      assertEquals(50, field<NativeListConfig>(list, "config").items.size)
      assertTrue(field<Map<*, *>>(tail, "hosts").size <= 128)
      list.applyPatches(JSONArray().put(JSONObject().put("key", "row-8")
        .put("type", "identity").put("changes", JSONObject().put("title", "new quote"))).toString())
      assertSame(animator, field<Any>(list, "structuralAnimator"))
      assertEquals("new quote", field<List<NativeListItem>>(tail, "items")[2].json.optString("title"))
      list.applySnapshot(tailSnapshot(6, "collapse-b", 600))
      assertEquals(6, field<NativeListConfig>(list, "config").items.size)
    }
    awaitTailFinished(6)
    onMain {
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
    }
  }

  fun testDisclosureKeepsMountedContentUntilLatestCollapseSettles() {
    val settlements = mutableListOf<JSONObject>()
    onMain {
      repeat(6) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0,100,48]")
      list.onStructuralTransitionSettled = { settlements.add(JSONObject(it)) }
      list.applySnapshot(disclosureSnapshot(6, false))
    }
    awaitAdapterCount(6)
    onMain { list.applySnapshot(disclosureSnapshot(6, false, "same-rows")) }
    awaitDisclosureSettlement(settlements, "same-rows")
    onMain {
      assertEquals("completed", settlements.single().getString("status"))
      assertFalse(settlements.single().getBoolean("settledExpanded"))
      list.applySnapshot(disclosureSnapshot(50, true, "expand", 1000))
    }
    awaitTailMidframe(6)
    onMain {
      val content = field<FrameLayout>(list, "expandedContentHost")
      assertSame(list.containerSlotAt(4), content.getChildAt(0))
      assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS,
        content.importantForAccessibility)
      val animator = field<ValueAnimator>(list, "structuralAnimator")
      val displayed = field<Int>(list, "disclosurePresentedContentPx")
      list.setContainerSlotHeightsJson("[0,0,56,0,140,48]")
      assertSame("C growth must retain the R/I deadline", animator,
        field<ValueAnimator>(list, "structuralAnimator"))
      assertEquals(displayed, field<Int>(list, "disclosurePresentedContentPx"))
      list.applySnapshot(disclosureSnapshot(6, false, "collapse", 1000))
    }
    awaitTailMidframe(6)
    onMain {
      assertNotNull("A new disclosure ID must reverse the active native tail",
        field<Any?>(list, "structuralTail"))
      assertFalse("The reverse must not enter a held full-list fallback",
        field<Boolean>(list, "structuralTailFinalizing"))
      assertNotNull(field<Any?>(list, "structuralAnimator"))
      assertEquals(listOf("same-rows"), settlements.map { it.getString("transitionId") })
    }
    awaitTailFinished(6)
    awaitDisclosureSettlement(settlements, "collapse")
    onMain {
      assertEquals(listOf("same-rows", "collapse"),
        settlements.map { it.getString("transitionId") })
      assertFalse(settlements.last().getBoolean("settledExpanded"))
      assertSame("C React instance is retained until the app receives ACK",
        list.containerSlotAt(4), field<FrameLayout>(list, "expandedContentHost").getChildAt(0))
    }
  }

  fun testDisclosureFixedFooterChangeInterruptsWithoutTailDebt() {
    val settlements = mutableListOf<JSONObject>()
    onMain {
      repeat(6) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0,100,48]")
      list.onStructuralTransitionSettled = { settlements.add(JSONObject(it)) }
      list.applySnapshot(disclosureSnapshot(6, false))
    }
    awaitAdapterCount(6)
    onMain { list.applySnapshot(disclosureSnapshot(50, true, "expand-footer", 1000)) }
    awaitTailMidframe(6)
    onMain { list.setContainerSlotHeightsJson("[0,0,72,0,100,48]") }
    awaitTailFinished(50)
    awaitDisclosureSettlement(settlements, "expand-footer")
    onMain {
      assertEquals("interrupted", settlements.single().getString("status"))
      assertTrue(settlements.single().getBoolean("settledExpanded"))
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
      assertEquals((100 * activity.resources.displayMetrics.density).roundToInt(),
        field<Int>(list, "disclosurePresentedContentPx"))
    }
  }

  fun testDisclosureSameIdFooterBatchInterruptsAndAcknowledgesOnce() {
    val settlements = mutableListOf<JSONObject>()
    onMain {
      repeat(6) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0,100,48]")
      list.onStructuralTransitionSettled = { settlements.add(JSONObject(it)) }
      list.applySnapshot(disclosureSnapshot(6, false))
    }
    awaitAdapterCount(6)
    onMain { list.applySnapshot(disclosureSnapshot(50, true, "same-id-footer", 1000)) }
    awaitTailMidframe(6)
    val sameIdSnapshot = disclosureSnapshot(50, true, "same-id-footer", 1000)
    onMain {
      list.setContainerSlotHeightsJson("[0,0,72,0,100,48]", sameIdSnapshot)
      assertTrue(field<Boolean>(list, "batchedDisclosureGeometryInvalid"))
      list.applySnapshot(sameIdSnapshot)
    }
    awaitTailFinished(50)
    awaitDisclosureSettlement(settlements, "same-id-footer")
    onMain {
      assertEquals(1, settlements.size)
      assertEquals("interrupted", settlements.single().getString("status"))
      assertTrue(settlements.single().getBoolean("settledExpanded"))
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
    }
  }

  fun testDisclosureContentIsInertWhileToggleRemainsTouchable() {
    val contentButtonId = 41041
    val toggleButtonId = 41042
    val contentButton = View(activity).apply { id = contentButtonId }
    val toggleButton = View(activity).apply { id = toggleButtonId }
    val content = FrameLayout(activity).apply {
      addView(contentButton, FrameLayout.LayoutParams(80, 40))
    }
    val toggle = FrameLayout(activity).apply {
      addView(toggleButton, FrameLayout.LayoutParams(80, 40))
    }
    onMain {
      repeat(4) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(content, 4)
      list.mountContainerSlot(toggle, 5)
      list.setContainerSlotHeightsJson("[0,0,56,0,100,48]")
      val width = View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY)
      content.measure(width, View.MeasureSpec.makeMeasureSpec(
        NativeListScale.dp(activity.resources, 100), View.MeasureSpec.EXACTLY))
      content.layout(0, 0, recycler.width, NativeListScale.dp(activity.resources, 100))
      toggle.measure(width, View.MeasureSpec.makeMeasureSpec(
        NativeListScale.dp(activity.resources, 48), View.MeasureSpec.EXACTLY))
      toggle.layout(0, 0, recycler.width, NativeListScale.dp(activity.resources, 48))
      list.applySnapshot(disclosureSnapshot(50, true))
    }
    awaitAdapterCount(50)
    onMain {
      val contentPosition = field<NativeListContainerSlotAdapter>(list, "headerSlotAdapter").itemCount +
        field<NativeListAdapter>(list, "adapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "emptySlotAdapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "structuralSpacerAdapter").itemCount
      (recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager)
        .scrollToPositionWithOffset(contentPosition, recycler.paddingTop)
    }
    instrumentation.waitForIdleSync()
    onMain {
      fun targetAt(button: View): Int {
        val listLocation = IntArray(2)
        val buttonLocation = IntArray(2)
        list.getLocationOnScreen(listLocation)
        button.getLocationOnScreen(buttonLocation)
        return TouchTargetHelper.findTargetTagForTouch(
          (buttonLocation[0] - listLocation[0] + button.width / 2).toFloat(),
          (buttonLocation[1] - listLocation[1] + button.height / 2).toFloat(), list)
      }
      assertTrue(contentButton.width > 0 && contentButton.height > 0)
      assertTrue(toggleButton.width > 0 && toggleButton.height > 0)
      assertEquals(contentButtonId, targetAt(contentButton))
      assertEquals(toggleButtonId, targetAt(toggleButton))
      list.applySnapshot(disclosureSnapshot(6, false, "inert-collapse", 1000))
    }
    awaitTailMidframe(6)
    onMain {
      val wrapper = field<FrameLayout>(list, "expandedContentHost")
      val target = TouchTargetHelper.findTargetTagForTouch(
        (contentButton.left + contentButton.width / 2).toFloat(),
        (contentButton.top + contentButton.height / 2).toFloat(), wrapper)
      assertTrue("The mounted C subtree must be inert during collapse",
        target != contentButtonId)
      assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS,
        wrapper.importantForAccessibility)
      assertEquals("The fixed toggle remains a React touch target",
        toggleButtonId, TouchTargetHelper.findTargetTagForTouch(
          (toggleButton.left + toggleButton.width / 2).toFloat(),
          (toggleButton.top + toggleButton.height / 2).toFloat(), toggle))
    }
  }

  fun testDirectDisclosureCollapseKeepsToggleVisibleWithTallFooterAndSidecar() {
    val settlements = mutableListOf<JSONObject>()
    onMain {
      repeat(6) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,4000,4000,100,48]")
      list.setSidecarLayoutJson("{\"width\":80,\"gap\":8,\"maxContentWidth\":500}")
      list.onStructuralTransitionSettled = { settlements.add(JSONObject(it)) }
      list.applySnapshot(disclosureSnapshot(50, true))
    }
    awaitAdapterCount(50)
    onMain {
      val footerPosition = field<androidx.recyclerview.widget.ConcatAdapter>(list,
        "scrollContentAdapter").itemCount - 1 -
        field<NativeListContainerSlotAdapter>(list, "sidecarSpacerAdapter").itemCount
      recycler.scrollToPosition(footerPosition)
    }
    instrumentation.waitForIdleSync()
    onMain { list.applySnapshot(disclosureSnapshot(6, false, "direct-collapse", 0)) }
    awaitAdapterCount(6)
    awaitDisclosureSettlement(settlements, "direct-collapse")
    instrumentation.waitForIdleSync()
    onMain {
      assertEquals("completed", settlements.single().getString("status"))
      val togglePosition = field<NativeListContainerSlotAdapter>(list, "headerSlotAdapter").itemCount +
        field<NativeListAdapter>(list, "adapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "emptySlotAdapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "structuralSpacerAdapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "expandedContentAdapter").itemCount
      assertEquals(1, field<NativeListContainerSlotAdapter>(list,
        "structureToggleAdapter").itemCount)
      assertTrue(togglePosition < field<androidx.recyclerview.widget.ConcatAdapter>(list,
        "scrollContentAdapter").itemCount)
      val toggle = recycler.findViewHolderForAdapterPosition(togglePosition)?.itemView
      assertNotNull("The settled Show more toggle at $togglePosition must be attached; " +
        "visible=${(recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()}.." +
        (recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition(),
        toggle)
      assertTrue("The settled Show more toggle must fit below the sticky header",
        toggle!!.top >= recycler.paddingTop + field<View>(list, "stickyHeaderHost").height)
      assertTrue("The settled Show more toggle must fit above the viewport bottom",
        toggle.bottom <= recycler.height - recycler.paddingBottom)
    }
  }

  fun testDisclosureLastSpacerRemovalKeepsToggleAtItsDrawnPosition() {
    val density = activity.resources.displayMetrics.density
    lateinit var testParent: ViewGroup
    val header = View(activity)
    val footer = FrameLayout(activity)
    val content = View(activity)
    val toggleMarker = View(activity)
    val toggle = FrameLayout(activity).apply {
      addView(toggleMarker, FrameLayout.LayoutParams(8, 8))
    }
    onMain {
      list.mountContainerSlot(header, 0)
      list.mountContainerSlot(View(activity), 1)
      list.mountContainerSlot(footer, 2)
      list.mountContainerSlot(View(activity), 3)
      list.mountContainerSlot(content, 4)
      list.mountContainerSlot(toggle, 5)
      list.setContainerSlotHeightsJson("[64,0,1259,0,184,50]")
      list.applySnapshot(disclosureSnapshot(48, true))
    }
    awaitAdapterCount(48)
    onMain {
      testParent = list.parent as ViewGroup
      testParent.suppressLayout(true)
      val width = recycler.width.takeIf { it > 0 } ?: 1080
      list.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(3017, View.MeasureSpec.EXACTLY))
      list.layout(0, 0, width, 3017)
      list.translationY = -617f
      recycler.setPadding(0, 1097, 0, 0)
      recycler.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(3017, View.MeasureSpec.EXACTLY))
      recycler.layout(0, 0, width, 3017)
      val togglePosition = field<NativeListContainerSlotAdapter>(list,
        "headerSlotAdapter").itemCount + field<NativeListAdapter>(list, "adapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "emptySlotAdapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "structuralSpacerAdapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "expandedContentAdapter").itemCount
      (recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager)
        .scrollToPositionWithOffset(togglePosition, 13)
      list.javaClass.getDeclaredMethod("relayoutRecyclerViewImmediately")
        .apply { isAccessible = true }.invoke(list)
    }
    try {
    instrumentation.waitForIdleSync()
    onMain {
      assertEquals(3017, recycler.height)
      assertEquals(1097, recycler.paddingTop)
      assertEquals((64 * density).roundToInt(), field<IntArray>(list, "slotHeights")[0])
      assertEquals((1259 * density).roundToInt(), field<IntArray>(list, "slotHeights")[2])
      assertEquals((50 * density).roundToInt(), field<IntArray>(list, "slotHeights")[5])
      assertTrue("Expanded toggle must have a real measured hit area", toggle.height > 0)
      val togglePosition = field<NativeListContainerSlotAdapter>(list,
        "headerSlotAdapter").itemCount + field<NativeListAdapter>(list, "adapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "emptySlotAdapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "structuralSpacerAdapter").itemCount +
        field<NativeListContainerSlotAdapter>(list, "expandedContentAdapter").itemCount
      val toggleHolder = recycler.layoutManager?.findViewByPosition(togglePosition)
      assertNotNull("Initial toggle must be attached: position=$togglePosition " +
        "first=${(recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()} " +
        "last=${(recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()} " +
        "total=${field<androidx.recyclerview.widget.ConcatAdapter>(list, "scrollContentAdapter").itemCount}",
        toggleHolder)
      assertTrue("Initial toggle must be 13px below the padded edge: " +
        "top=${toggleHolder?.let { recycler.layoutManager!!.getDecoratedTop(it) }} " +
        "padding=${recycler.paddingTop}",
        kotlin.math.abs(recycler.layoutManager!!.getDecoratedTop(toggleHolder!!) -
          recycler.paddingTop - 13) <= 2)
      assertTrue("Initial toggle marker must be visible",
        toggleMarker.isAttachedToWindow &&
          toggleMarker.getGlobalVisibleRect(android.graphics.Rect()))
      list.applySnapshot(disclosureSnapshot(6, false, "last-spacer", 1200))
    }
    awaitTailMidframe(6)
    onMain {
      val start = field<Int>(list, "structuralTailStartOffset")
      val target = field<Int>(list, "structuralTailTargetOffset")
      assertTrue("App-shaped owner start must be near 8222px, got $start",
        kotlin.math.abs(start - 8222) <= 2)
      assertEquals("App-shaped collapsed toggle target", 1116, target)
    }
    var before = 0
    var after = 0
    var beforeScroll = 0
    var afterScroll = 0
    var beforeSpacer = 0
    var afterSpacer = 0
    var targetOffset = 0
    var startOffset = 0
    var beforeVisible = false
    var afterVisible = false
    onMain {
      val animator = field<ValueAnimator>(list, "structuralAnimator")
      animator.pause()
      val tail = field<Any>(list, "structuralTail")
      val epoch = field<Int>(list, "structuralTransitionEpoch")
      val frame = list.javaClass.getDeclaredMethod("applyStructuralTailFrame", tail.javaClass,
        Int::class.javaPrimitiveType, Float::class.javaPrimitiveType).apply { isAccessible = true }
      val startExtent = field<Int>(list, "structuralTailStartExtentPx")
      val startProgress = field<Float>(list, "structuralTailStartProgress")
      val nearEnd = 1f - 3f / startExtent * (1f - startProgress)
      frame.invoke(list, tail, epoch, nearEnd)
      beforeSpacer = field(list, "structuralSpacerHeightPx")
      targetOffset = field(list, "structuralTailTargetOffset")
      startOffset = field(list, "structuralTailStartOffset")
      val location = IntArray(2)
      toggleMarker.getLocationOnScreen(location)
      before = location[1]
      beforeVisible = toggleMarker.isAttachedToWindow &&
        toggleMarker.getGlobalVisibleRect(android.graphics.Rect())
      beforeScroll = list.javaClass.getDeclaredMethod("tailScrollExtentPx")
        .apply { isAccessible = true }.invoke(list) as Int
    }
    instrumentation.waitForIdleSync()
    onMain {
      val tail = field<Any>(list, "structuralTail")
      val epoch = field<Int>(list, "structuralTransitionEpoch")
      val frame = list.javaClass.getDeclaredMethod("applyStructuralTailFrame", tail.javaClass,
        Int::class.javaPrimitiveType, Float::class.javaPrimitiveType).apply { isAccessible = true }
      frame.invoke(list, tail, epoch, 1f)
      afterSpacer = field(list, "structuralSpacerHeightPx")
      val location = IntArray(2)
      toggleMarker.getLocationOnScreen(location)
      after = location[1]
      afterVisible = toggleMarker.isAttachedToWindow &&
        toggleMarker.getGlobalVisibleRect(android.graphics.Rect())
      afterScroll = list.javaClass.getDeclaredMethod("tailScrollExtentPx")
        .apply { isAccessible = true }.invoke(list) as Int
    }
    assertTrue("The prior native frame must have a positive final spacer, got $beforeSpacer",
      beforeSpacer in 1..5)
    assertEquals(0, afterSpacer)
    assertTrue("The actual toggle marker must remain on screen", beforeVisible && afterVisible)
    assertTrue("Removing the last spacer must not move the native toggle: before=$before " +
      "after=$after offsets=$beforeScroll->$afterScroll start=$startOffset target=$targetOffset " +
      "visible=$beforeVisible->$afterVisible spacer=$beforeSpacer->$afterSpacer",
      kotlin.math.abs(after - before) <= beforeSpacer + 1)
    onMain { field<ValueAnimator>(list, "structuralAnimator").end() }
    awaitTailFinished(6)
    onMain {
      val finalLocation = IntArray(2)
      toggleMarker.getLocationOnScreen(finalLocation)
      val finalOffset = list.javaClass.getDeclaredMethod("tailScrollExtentPx")
        .apply { isAccessible = true }.invoke(list) as Int
      assertTrue("The settled toggle must remain attached and visible",
        toggleMarker.isAttachedToWindow &&
          toggleMarker.getGlobalVisibleRect(android.graphics.Rect()))
      assertEquals("The full-list handoff must reach the same physical owner offset",
        targetOffset, finalOffset)
      assertTrue("The final full-list handoff must preserve the last drawn toggle: " +
        "last=$after settled=${finalLocation[1]}",
        kotlin.math.abs(finalLocation[1] - after) <= 1)
    }
    onMain { list.setContainerSlotHeightsJson("[64,0,1259,0,0,50]") }
    instrumentation.waitForIdleSync()
    onMain {
      val afterUnmount = IntArray(2)
      toggleMarker.getLocationOnScreen(afterUnmount)
      assertTrue("Unmounting settled expanded content must keep the toggle visible",
        toggleMarker.isAttachedToWindow &&
          toggleMarker.getGlobalVisibleRect(android.graphics.Rect()))
      assertTrue("Unmounting settled expanded content must not move the toggle: " +
        "settled=$after afterUnmount=${afterUnmount[1]}",
        kotlin.math.abs(afterUnmount[1] - after) <= 1)
      assertEquals(1, field<NativeListContainerSlotAdapter>(list,
        "structureToggleAdapter").itemCount)
    }
    onMain {
      list.setContainerSlotHeightsJson("[64,0,1259,0,192,50]")
      list.applySnapshot(disclosureSnapshot(48, true, "reexpand-after-unmount", 1200))
    }
    awaitTailMidframe(6)
    onMain {
      assertEquals((192 * density).roundToInt(), field<IntArray>(list, "slotHeights")[4])
      assertNotNull("A later expansion must accept the newly measured C height",
        field<Any?>(list, "structuralTail"))
    }
    } finally {
      onMain { testParent.suppressLayout(false) }
    }
  }

  private fun disclosureSnapshot(
    count: Int,
    expanded: Boolean,
    transitionId: String? = null,
    durationMs: Int = 220,
  ): String = JSONObject(tailSnapshot(count, transitionId, durationMs, itemSpacing = 0))
    .put("disclosure", JSONObject().put("expanded", expanded)
      .put("expandedTrailingInset", 20)).toString()

  private fun awaitDisclosureSettlement(events: List<JSONObject>, id: String) {
    repeat(100) {
      var ready = false
      onMain { ready = events.any { event -> event.optString("transitionId") == id } }
      if (ready) return
      SystemClock.sleep(20)
    }
    fail("Disclosure command $id did not settle")
  }

  fun testExpandingTailSurvivesFooterGrowthBeforeItsFirstFrame() {
    val density = activity.resources.displayMetrics.density
    val oldFooterHeight = (56 * density).roundToInt()
    val newFooterHeight = (160 * density).roundToInt()
    val footerGrowth = newFooterHeight - oldFooterHeight
    val marketMarker = View(activity)
    val footer = FrameLayout(activity).apply {
      addView(marketMarker, FrameLayout.LayoutParams(8, 8).apply {
        topMargin = (32 * density).roundToInt()
      })
    }
    onMain {
      repeat(2) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(footer, 2)
      list.mountContainerSlot(View(activity), 3)
      list.setContainerSlotHeightsJson("[0,0,56,4000]")
      list.setSidecarLayoutJson("{\"width\":80,\"gap\":8,\"maxContentWidth\":500}")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    var oldMarketTop = 0
    var oldLeftExtent = 0
    onMain {
      val location = IntArray(2)
      marketMarker.getLocationOnScreen(location)
      oldMarketTop = location[1]
      assertTrue(oldMarketTop > 0)
      assertSame(footer, list.containerSlotAt(2))
      val extentAt = list.javaClass.getDeclaredMethod("structuralTailRowOrSlotExtentPx",
        Int::class.javaPrimitiveType).apply { isAccessible = true }
      val contentCount = field<androidx.recyclerview.widget.ConcatAdapter>(list,
        "scrollContentAdapter").itemCount -
        field<NativeListContainerSlotAdapter>(list, "sidecarSpacerAdapter").itemCount
      oldLeftExtent = (0 until contentCount).sumOf { extentAt.invoke(list, it) as Int } +
        recycler.paddingTop + recycler.paddingBottom
    }
    onMain {
      val parent = list.parent as ViewGroup
      parent.suppressLayout(true)
      try {
        list.applySnapshot(tailSnapshot(48, "expand-footer-growth", 3000))
        assertNotNull(field<Any?>(list, "structuralTail"))
        assertEquals(6, field<NativeListAdapter>(list, "adapter").itemCount)
        (marketMarker.layoutParams as FrameLayout.LayoutParams).topMargin += footerGrowth
        footer.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(newFooterHeight, View.MeasureSpec.EXACTLY))
        footer.layout(0, 0, recycler.width, newFooterHeight)
        field<android.view.ViewTreeObserver.OnPreDrawListener>(list, "stickyPreDraw")
          .onPreDraw()
        assertEquals("Native footer measurement is accepted before a JS slot report",
          newFooterHeight, field<Int>(list, "acceptedFooterNaturalHeightPx"))
        assertEquals(-footerGrowth, field<Int>(list, "structuralTailSignedExtentPx"))
        list.setContainerSlotHeightsJson("[0,0,56,4000]")
        assertEquals("A stale JS height must not reverse a native measurement",
          -footerGrowth, field<Int>(list, "structuralTailSignedExtentPx"))
        list.setContainerSlotHeightsJson("[0,0,160,4000]")
        assertFalse("Footer growth must not finalize the empty starting spacer",
          field<Boolean>(list, "structuralTailFinalizing"))
        assertEquals(6, field<NativeListAdapter>(list, "adapter").itemCount)
        assertEquals(-footerGrowth, field<Int>(list, "structuralTailSignedExtentPx"))
        val wrapper = field<FrameLayout>(list, "footerPresentationHost")
        assertEquals(footerGrowth, field<Int>(wrapper, "hiddenTopPx"))
        assertEquals(newFooterHeight, field<Int>(wrapper, "naturalHeightPx"))
        assertEquals(newFooterHeight, footer.measuredHeight)
        assertEquals("The native debt must move the Fabric-laid-out footer in this UI turn",
          -footerGrowth, footer.top)
        assertEquals(oldFooterHeight, footer.bottom)
        val footerPosition = field<androidx.recyclerview.widget.ConcatAdapter>(list,
          "scrollContentAdapter").itemCount - 1 -
          field<NativeListContainerSlotAdapter>(list, "sidecarSpacerAdapter").itemCount
        val displayedFooterHeight = list.javaClass.getDeclaredMethod("estimatedScrollExtentPx",
          Int::class.javaPrimitiveType).apply { isAccessible = true }
          .invoke(list, footerPosition) as Int
        assertEquals(oldFooterHeight, displayedFooterHeight)
        assertEquals(oldLeftExtent, list.javaClass.getDeclaredMethod("sidecarLeftExtentPx")
          .apply { isAccessible = true }.invoke(list) as Int)
        list.setContainerSlotHeightsJson("[0,0,160,4000]")
        assertEquals(-footerGrowth, field<Int>(list, "structuralTailSignedExtentPx"))
      } finally {
        parent.suppressLayout(false)
      }
    }
    awaitTailMidframe(6)
    onMain {
      val tail = field<Any>(list, "structuralTail")
      assertTrue(field<Int>(tail, "peakHosts") > 0)
      assertTrue(field<FrameLayout>(list, "structuralTailLayer").childCount in 1..128)
      val signedExtent = field<Int>(list, "structuralTailSignedExtentPx")
      val markerLocation = IntArray(2)
      marketMarker.getLocationOnScreen(markerLocation)
      assertTrue("Market marker must follow the same signed extent as the tail",
        kotlin.math.abs(markerLocation[1] - oldMarketTop - signedExtent - footerGrowth) <=
          (2 * density).roundToInt())
      assertEquals(newFooterHeight, footer.measuredHeight)
    }
    awaitTailFinished(48)
    onMain {
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
      assertEquals(0, field<Int>(field<FrameLayout>(list, "footerPresentationHost"), "hiddenTopPx"))
      assertSame(footer, list.containerSlotAt(2))
    }
  }

  fun testCollapsingTailPreservesItsStartExtentUntilFooterShrinkAndPrefixCommit() {
    verifyCollapsingTailFrame(0)
  }

  fun testPendingNativeFooterHeightSurvivesNewIdFullCommit() {
    val oldFooterHeight = NativeListScale.dp(activity.resources, 160)
    val newFooterHeight = NativeListScale.dp(activity.resources, 56)
    val footer = FrameLayout(activity)
    onMain {
      repeat(2) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(footer, 2)
      list.setContainerSlotHeightsJson("[0,0,160,0]")
      list.applySnapshot(tailSnapshot(48))
    }
    awaitAdapterCount(48)
    onMain {
      list.applySnapshot(tailSnapshot(6, "pending-footer-collapse", 1200))
      assertTrue(field<Int>(list, "structuralTailRowsInAdapterPx") > 0)
      val manager = field<Any>(list, "layoutManager")
      val callbackField = manager.javaClass.getDeclaredField("onLayoutPassCompleted")
        .apply { isAccessible = true }
      @Suppress("UNCHECKED_CAST")
      val previousCallback = callbackField.get(manager) as? (() -> Unit)
      var reportedDuringLayout = false
      callbackField.set(manager, {
        previousCallback?.invoke()
        if (!reportedDuringLayout && recycler.isComputingLayout &&
          field<Int>(list, "structuralTailRowsInAdapterPx") > 0) {
          reportedDuringLayout = true
          assertFalse(field<android.view.ViewTreeObserver.OnPreDrawListener>(list,
            "stickyPreDraw").onPreDraw())
          list.setContainerSlotHeightsJson("[0,0,160,0]")
          assertEquals(oldFooterHeight, field<Int>(list, "acceptedFooterNaturalHeightPx"))
          assertEquals("The old JS height cannot replace the pending native measurement",
            newFooterHeight, field<Any>(list, "pendingPreparingFooterFrame")
              .let { field<Int>(it, "naturalHeight") })
        }
      } as () -> Unit)
      try {
        footer.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(newFooterHeight, View.MeasureSpec.EXACTLY))
        recycler.forceLayout()
        recycler.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(recycler.height, View.MeasureSpec.EXACTLY))
        recycler.layout(recycler.left, recycler.top, recycler.right, recycler.bottom)
      } finally {
        callbackField.set(manager, previousCallback)
      }
      assertTrue(reportedDuringLayout)
      var requeuedDuringCommit = false
      callbackField.set(manager, {
        previousCallback?.invoke()
        if (!requeuedDuringCommit && recycler.isComputingLayout &&
          field<Int>(list, "acceptedFooterNaturalHeightPx") == newFooterHeight) {
          requeuedDuringCommit = true
          list.setContainerSlotHeightsJson("[0,0,56,0]")
        }
      } as () -> Unit)
      try {
        assertFalse("A report queued during layout keeps the frame gated",
          field<android.view.ViewTreeObserver.OnPreDrawListener>(list,
            "stickyPreDraw").onPreDraw())
        assertTrue(requeuedDuringCommit)
        assertNotNull("The previous commit must not erase a new pending report",
          field<Any?>(list, "pendingPreparingFooterFrame"))
      } finally {
        callbackField.set(manager, previousCallback)
      }
      list.applySnapshot(tailSnapshot(50, "new-id-during-footer-pending", 600))
      list.setContainerSlotHeightsJson("[10,0,160,0]")
      assertEquals(50, field<NativeListConfig>(list, "config").items.size)
    }
    awaitTailFinished(50)
    onMain {
      assertNull(field<Any?>(list, "pendingPreparingFooterFrame"))
      assertEquals(newFooterHeight, field<Int>(list, "acceptedFooterNaturalHeightPx"))
      assertEquals(newFooterHeight, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "naturalHeightPx"))
      assertEquals(NativeListScale.dp(activity.resources, 10),
        field<IntArray>(list, "slotHeights")[0])
      assertEquals(NativeListScale.dp(activity.resources, 10),
        field<Int>(field<Any>(list, "headerSlotAdapter"), "height"))
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
    }
  }

  fun testCollapsingTailFrameIncludesSpacingForUnmeasuredRows() {
    verifyCollapsingTailFrame(4)
  }

  fun testCollapsingTailFinalFrameKeepsLargeFooterOnProgressPath() {
    verifyCollapsingTailFrame(0, appShape = true)
  }

  private fun verifyCollapsingTailFrame(itemSpacing: Int, appShape: Boolean = false) {
    val density = activity.resources.displayMetrics.density
    val oldFooterDp = if (appShape) 1493 else 160
    val newFooterDp = if (appShape) 1309 else 56
    val headerDp = if (appShape) 482 else 0
    val oldFooterHeight = (oldFooterDp * density).roundToInt()
    val newFooterHeight = (newFooterDp * density).roundToInt()
    val marker = View(activity)
    val footer = FrameLayout(activity).apply {
      addView(marker, FrameLayout.LayoutParams(8, 8))
    }
    onMain {
      repeat(2) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(footer, 2)
      list.setContainerSlotHeightsJson("[$headerDp,0,$oldFooterDp,0]")
      list.applySnapshot(tailSnapshot(48, itemSpacing = itemSpacing,
        walletSidebar = itemSpacing > 0))
    }
    awaitAdapterCount(48)
    val footerPosition = field<androidx.recyclerview.widget.ConcatAdapter>(list,
      "scrollContentAdapter").itemCount - 1
    onMain {
      if (appShape) {
        (recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager)
          .scrollToPositionWithOffset(footerPosition, 0)
      } else {
        recycler.scrollToPosition(footerPosition)
      }
    }
    instrumentation.waitForIdleSync()
    var oldMarkerTop = 0
    var firstPrefixOffset: Int? = null
    var firstPrefixProgress = 0f
    var firstPrefixMarkerTop: Int? = null
    var firstPrefixSignedExtent = 0
    var firstPrefixGeometry = ""
    var oldTailExtentPx = 0
    var oldStartOffset = 0
    var markerBeforeClear: Int? = null
    val lateFooterFrames = mutableListOf<String>()
    val lateFooterErrors = mutableListOf<Int>()
    val lateFooterExtents = mutableListOf<Int>()
    val lateFooterVisibility = mutableListOf<Boolean>()
    val lateFooterRebased = mutableListOf<Boolean>()
    var lateStartExtent: Int? = null
    var lateStartOffset: Int? = null
    var lateTargetOffset: Int? = null
    var lateStartProgress: Float? = null
    val tailLayer = field<FrameLayout>(list, "structuralTailLayer")
    onMain {
      tailLayer.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewAdded(parent: View, child: View) = Unit

        override fun onChildViewRemoved(parent: View, child: View) {
          if (markerBeforeClear != null || !field<Boolean>(list, "structuralTailFinalizing") ||
            !marker.isAttachedToWindow) return
          val location = IntArray(2)
          marker.getLocationOnScreen(location)
          markerBeforeClear = location[1]
        }
      })
    }
    lateinit var observer: ViewTreeObserver
    val firstPrefixListener = ViewTreeObserver.OnPreDrawListener {
      if (firstPrefixOffset == null && field<NativeListAdapter>(list, "adapter").itemCount == 6 &&
        field<Any?>(list, "structuralTail") != null &&
        field<Int>(list, "structuralTailRowsInAdapterPx") == 0) {
        firstPrefixOffset = list.javaClass.getDeclaredMethod("tailScrollExtentPx")
          .apply { isAccessible = true }.invoke(list) as Int
        firstPrefixProgress = (field<Any?>(list, "structuralAnimator") as? ValueAnimator)
          ?.animatedValue as? Float ?: 0f
        val location = IntArray(2)
        marker.getLocationOnScreen(location)
        firstPrefixMarkerTop = location[1]
        firstPrefixSignedExtent = field(list, "structuralTailSignedExtentPx")
        val first = field<androidx.recyclerview.widget.GridLayoutManager>(list, "layoutManager")
          .findFirstVisibleItemPosition()
        val measured = list.javaClass.getDeclaredMethod("measuredScrollDistancePx")
          .apply { isAccessible = true }.invoke(list)
        firstPrefixGeometry = "markerAttached=${marker.isAttachedToWindow} " +
          "markerVisible=${marker.getGlobalVisibleRect(android.graphics.Rect())} " +
          "footerAttached=${footer.isAttachedToWindow} recyclerChildren=${recycler.childCount} " +
          "first=$first measured=$measured spacer=${field<Int>(list, "structuralSpacerHeightPx")}"
      }
      if (appShape && lateStartExtent != null &&
        field<NativeListAdapter>(list, "adapter").itemCount == 6 &&
        field<Any?>(list, "structuralTail") != null) {
        val animator = field<Any?>(list, "structuralAnimator") as? ValueAnimator
        val progress = animator?.animatedValue as? Float
        if (progress != null && progress >= 0.75f) {
          val startExtent = lateStartExtent!!
          val startOffset = lateStartOffset!!
          val targetOffset = lateTargetOffset!!
          val startProgress = lateStartProgress!!
          val segmentProgress = if (startProgress >= 1f) 1f else
            ((progress - startProgress) / (1f - startProgress)).coerceIn(0f, 1f)
          val expectedTop = oldMarkerTop + startExtent - oldTailExtentPx +
            ((-startExtent - (targetOffset - startOffset)) * segmentProgress).roundToInt()
          val location = IntArray(2)
          marker.getLocationOnScreen(location)
          val attached = marker.isAttachedToWindow
          val visible = attached && marker.getGlobalVisibleRect(android.graphics.Rect())
          val signedExtent = field<Int>(list, "structuralTailSignedExtentPx")
          val first = (recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager)
            .findFirstVisibleItemPosition()
          val firstView = recycler.layoutManager?.findViewByPosition(first)
          val firstTop = firstView?.let { recycler.layoutManager!!.getDecoratedTop(it) }
          val actualOffset = list.javaClass.getDeclaredMethod("tailScrollExtentPx")
            .apply { isAccessible = true }.invoke(list)
          lateFooterErrors.add(kotlin.math.abs(location[1] - expectedTop))
          lateFooterExtents.add(signedExtent)
          lateFooterVisibility.add(visible)
          lateFooterRebased.add(startExtent != field<Int>(list, "structuralTailStartExtentPx") ||
            startOffset != field<Int>(list, "structuralTailStartOffset") ||
            targetOffset != field<Int>(list, "structuralTailTargetOffset") ||
            startProgress != field<Float>(list, "structuralTailStartProgress"))
          lateFooterFrames.add("p=$progress expected=$expectedTop actual=${location[1]} " +
            "S=$signedExtent " +
            "start=$startOffset target=$targetOffset " +
            "first=$first/$firstTop offset=$actualOffset attached=$attached visible=$visible")
        }
      }
      true
    }
    onMain {
      footer.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(oldFooterHeight, View.MeasureSpec.EXACTLY))
      footer.layout(0, 0, recycler.width, oldFooterHeight)
      val markerLocation = IntArray(2)
      marker.getLocationOnScreen(markerLocation)
      oldMarkerTop = markerLocation[1]
      assertTrue("The old footer must be attached before comparing its screen position",
        footer.isAttachedToWindow && marker.isAttachedToWindow)
      val initialVisible = android.graphics.Rect()
      val initialFooterView = recycler.layoutManager?.findViewByPosition(footerPosition)
      assertTrue("The old footer marker must be on screen: markerY=${markerLocation[1]} " +
        "footerTop=${initialFooterView?.top} footerH=${initialFooterView?.height} " +
        "recyclerY=${recycler.top} recyclerH=${recycler.height}",
        marker.getGlobalVisibleRect(initialVisible))
      observer = recycler.viewTreeObserver
      observer.addOnPreDrawListener(firstPrefixListener)
      val parent = list.parent as ViewGroup
      parent.suppressLayout(true)
      try {
        list.applySnapshot(tailSnapshot(6, "collapse-shrinking-footer", 1200,
          itemSpacing = itemSpacing, walletSidebar = itemSpacing > 0))
        val tail = field<Any>(list, "structuralTail")
        val oldTailExtent = field<Int>(tail, "extent")
        val startOffset = field<Int>(list, "structuralTailStartOffset")
        oldTailExtentPx = oldTailExtent
        oldStartOffset = startOffset
        assertEquals("The old full adapter remains until its prefix commit", 48,
          field<NativeListAdapter>(list, "adapter").itemCount)
        assertEquals(oldTailExtent, field<Int>(list, "structuralTailStartExtentPx"))
        footer.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(newFooterHeight, View.MeasureSpec.EXACTLY))
        footer.layout(0, 0, recycler.width, newFooterHeight)
        list.setContainerSlotHeightsJson("[$headerDp,0,$newFooterDp,0]")
        assertEquals("Old rows remain until the asynchronous prefix commit", 48,
          field<NativeListAdapter>(list, "adapter").itemCount)
        assertEquals("Footer shrink adds to the pending natural tail extent",
          oldTailExtent + oldFooterHeight - newFooterHeight,
          field<Int>(list, "structuralTailStartExtentPx"))
        val pendingSpacer = field<Int>(list, "structuralSpacerHeightPx")
        assertEquals("Only footer shrink is added beside old rows",
          oldFooterHeight - newFooterHeight, pendingSpacer)
        val displayedFooter = list.javaClass.getDeclaredMethod("footerPresentedHeightPx")
          .apply { isAccessible = true }.invoke(list) as Int
        assertEquals("Old rows, provisional spacer and footer retain their total height",
          oldTailExtent + oldFooterHeight, oldTailExtent + pendingSpacer + displayedFooter)
        assertEquals("Footer compensation does not rebase the owner's starting offset",
          startOffset, field<Int>(list, "structuralTailStartOffset"))
        marker.getLocationOnScreen(markerLocation)
        assertTrue("The first preparation frame must preserve the footer successor position: " +
          "old=$oldMarkerTop new=${markerLocation[1]} delta=${oldFooterHeight - newFooterHeight}",
          kotlin.math.abs(markerLocation[1] - oldMarkerTop -
            (oldFooterHeight - newFooterHeight)) <= 2)
        assertEquals("Do not draw a second tail beside the old full adapter", 0,
          field<FrameLayout>(list, "structuralTailLayer").childCount)
      } finally {
        parent.suppressLayout(false)
      }
    }
    awaitTailMidframe(6)
    onMain {
      val firstOffset = firstPrefixOffset
      assertNotNull("The first committed prefix must reach pre-draw", firstOffset)
      val firstExpected = (field<Int>(list, "structuralTailStartOffset") +
        (field<Int>(list, "structuralTailTargetOffset") -
          field<Int>(list, "structuralTailStartOffset")) * firstPrefixProgress).roundToInt()
      assertTrue("The first prefix frame must retain its owner offset: expected=$firstExpected actual=$firstOffset",
        kotlin.math.abs(firstOffset!! - firstExpected) <= NativeListScale.dp(activity.resources, 60))
      val expectedMarkerTop = oldMarkerTop + firstPrefixSignedExtent - oldTailExtentPx -
        (firstOffset - oldStartOffset)
      assertTrue(
        "The first prefix frame must place the footer marker at its native-progress position: " +
          "old=$oldMarkerTop new=$firstPrefixMarkerTop progress=$firstPrefixProgress " +
          "scroll=$firstOffset expectedScroll=$firstExpected markerExpected=$expectedMarkerTop " +
          firstPrefixGeometry,
        firstPrefixMarkerTop != null && footer.isAttachedToWindow &&
          kotlin.math.abs(firstPrefixMarkerTop!! - expectedMarkerTop) <= 2)
      val animator = field<ValueAnimator>(list, "structuralAnimator")
      val progress = animator.animatedValue as Float
      val start = field<Int>(list, "structuralTailStartOffset")
      val target = field<Int>(list, "structuralTailTargetOffset")
      val expected = (start + (target - start) * progress).roundToInt()
      val actual = list.javaClass.getDeclaredMethod("tailScrollExtentPx")
        .apply { isAccessible = true }.invoke(list) as Int
      assertTrue("Owner offset must follow the same native progress: expected=$expected actual=$actual progress=$progress",
        kotlin.math.abs(actual - expected) <= NativeListScale.dp(activity.resources, 60))
    }
    if (appShape) {
      onMain {
        val animator = field<ValueAnimator>(list, "structuralAnimator")
        lateStartExtent = field(list, "structuralTailStartExtentPx")
        lateStartOffset = field(list, "structuralTailStartOffset")
        lateTargetOffset = field(list, "structuralTailTargetOffset")
        lateStartProgress = field(list, "structuralTailStartProgress")
        animator.pause()
        animator.currentPlayTime = 1170L
        recycler.invalidate()
      }
      instrumentation.waitForIdleSync()
      onMain {
        field<ValueAnimator>(list, "structuralAnimator").currentPlayTime = 1199L
        recycler.invalidate()
      }
      instrumentation.waitForIdleSync()
      onMain { field<ValueAnimator>(list, "structuralAnimator").end() }
    }
    awaitTailFinished(6)
    onMain {
      if (observer.isAlive) observer.removeOnPreDrawListener(firstPrefixListener)
      tailLayer.setOnHierarchyChangeListener(null)
      val beforeClear = markerBeforeClear
      assertNotNull("A visible native tail host must be removed only at final handoff", beforeClear)
      assertTrue("The final footer marker must be attached", marker.isAttachedToWindow)
      val finalLocation = IntArray(2)
      marker.getLocationOnScreen(finalLocation)
      assertTrue("Clearing the old tail must not move the footer marker: " +
        "before=$beforeClear after=${finalLocation[1]}",
        kotlin.math.abs(finalLocation[1] - beforeClear!!) <= 1)
      if (appShape) {
        assertTrue("The final native segment must draw at least two real frames: $lateFooterFrames",
          lateFooterFrames.size >= 2)
        assertTrue("The final native segment must draw both a positive tail and its zero extent: " +
          lateFooterFrames, lateFooterExtents.any { it > 0 } && lateFooterExtents.any { it == 0 })
        assertTrue("The large footer marker must remain visible in each terminal frame: " +
          lateFooterFrames, lateFooterVisibility.all { it })
        assertTrue("No input may rebase the final native segment: $lateFooterFrames",
          lateFooterRebased.none { it })
        val maximumError = lateFooterErrors.maxOrNull() ?: 0
        assertTrue("The footer marker must follow the same native progress without a final jump: " +
          "maxError=$maximumError frames=$lateFooterFrames", maximumError <= 2)
      }
    }
  }

  fun testPatchDuringInitialCollapseCommitsAcceptedRowsWithoutOldCallback() {
    onMain { list.applySnapshot(tailSnapshot(48)) }
    awaitAdapterCount(48)
    onMain {
      list.applySnapshot(tailSnapshot(6, "collapse-before-patch", 1200))
      assertTrue("Old rows still own the tail before the prefix callback",
        field<Int>(list, "structuralTailRowsInAdapterPx") > 0)
      list.applyPatches(JSONArray().put(JSONObject().put("key", "row-2")
        .put("type", "identity").put("changes", JSONObject().put("title", "updated quote"))).toString())
    }
    awaitTailFinished(6)
    onMain {
      assertEquals("updated quote", field<NativeListConfig>(list, "config").items[2].json
        .optString("title"))
      assertEquals(0, field<Int>(list, "structuralTailRowsInAdapterPx"))
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
    }
  }

  fun testFooterReplacementDuringInitialCollapseDoesNotTurnOldRowsIntoDebt() {
    onMain {
      repeat(2) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(FrameLayout(activity), 2)
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(48))
    }
    awaitAdapterCount(48)
    onMain {
      list.applySnapshot(tailSnapshot(6, "collapse-before-footer-replace", 1200))
      val oldRowsExtent = field<Int>(list, "structuralTailRowsInAdapterPx")
      assertTrue(oldRowsExtent > 0)
      list.unmountContainerSlot(2)
      assertEquals("Geometry invalidation retains the old rows' logical extent",
        oldRowsExtent + field<Int>(list, "structuralSpacerHeightPx"),
        field<Int>(list, "structuralTailSignedExtentPx"))
      assertEquals("Old rows must not become footer clipping debt", 0,
        list.javaClass.getDeclaredMethod("footerHiddenTopPx")
          .apply { isAccessible = true }.invoke(list) as Int)
    }
    awaitTailFinished(6)
    onMain { assertEquals(0, field<Int>(list, "structuralTailRowsInAdapterPx")) }
  }

  fun testFooterDebtReversalAndBareDetachReleaseOldAnimation() {
    onMain {
      repeat(3) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    onMain {
      list.applySnapshot(tailSnapshot(48, "expand-with-debt", 1200))
      list.setContainerSlotHeightsJson("[0,0,160,0]")
      assertTrue(field<Int>(list, "structuralTailSignedExtentPx") < 0)
      list.applySnapshot(tailSnapshot(6, "reverse-with-debt", 600))
      assertEquals(6, field<NativeListConfig>(list, "config").items.size)
    }
    awaitTailFinished(6)
    onMain {
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
      assertEquals(0, field<Int>(field<FrameLayout>(list, "footerPresentationHost"),
        "hiddenTopPx"))
      list.applySnapshot(tailSnapshot(48, "expand-before-detach", 1200))
      list.setContainerSlotHeightsJson("[0,0,220,0]")
      assertTrue(field<Int>(list, "structuralTailSignedExtentPx") < 0)
      activity.setContentView(View(activity))
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
      assertEquals(0, field<Int>(field<FrameLayout>(list, "footerPresentationHost"),
        "hiddenTopPx"))
    }
    SystemClock.sleep(1300)
    onMain {
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
    }
  }

  fun testNativeFooterMeasurementPersistsWithoutJsHeightThroughReverseAndDetach() {
    val density = activity.resources.displayMetrics.density
    val initialHeight = (56 * density).roundToInt()
    val firstNativeHeight = (160 * density).roundToInt()
    val preparingNativeHeight = (200 * density).roundToInt()
    val acceptedPreparingNativeHeight =
      (kotlin.math.ceil(preparingNativeHeight / density.toDouble()) * density).roundToInt()
    val secondNativeHeight = (220 * density).roundToInt()
    val acceptedSecondNativeHeight =
      (kotlin.math.ceil(secondNativeHeight / density.toDouble()) * density).roundToInt()
    val footer = View(activity)
    onMain {
      repeat(2) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(footer, 2)
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    onMain {
      val width = View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY)
      val preDraw = field<android.view.ViewTreeObserver.OnPreDrawListener>(list, "stickyPreDraw")
      list.applySnapshot(tailSnapshot(48, "native-measure-expand", 1200))
      footer.measure(width, View.MeasureSpec.makeMeasureSpec(firstNativeHeight,
        View.MeasureSpec.EXACTLY))
      preDraw.onPreDraw()
      assertEquals(initialHeight, field<IntArray>(list, "slotHeights")[2])
      assertEquals(firstNativeHeight, field<Int>(list, "acceptedFooterNaturalHeightPx"))
      assertEquals(firstNativeHeight, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "naturalHeightPx"))
      assertEquals(initialHeight - firstNativeHeight,
        field<Int>(list, "structuralTailSignedExtentPx"))
      list.applySnapshot(tailSnapshot(6, "native-measure-reverse", 600))
      assertEquals(firstNativeHeight, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "naturalHeightPx"))
      assertTrue(field<Boolean>(list, "structuralTailPreparing"))
      footer.measure(width, View.MeasureSpec.makeMeasureSpec(preparingNativeHeight,
        View.MeasureSpec.EXACTLY))
      preDraw.onPreDraw()
      assertTrue(field<Boolean>(list, "structuralTailFinalizing"))
      assertEquals(acceptedPreparingNativeHeight,
        field<Int>(list, "acceptedFooterNaturalHeightPx"))
      assertEquals(0, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "hiddenTopPx"))
    }
    awaitTailFinished(6)
    onMain {
      assertEquals(initialHeight, field<IntArray>(list, "slotHeights")[2])
      assertEquals(acceptedPreparingNativeHeight, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "naturalHeightPx"))
      assertEquals(0, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "hiddenTopPx"))
      list.applySnapshot(tailSnapshot(48, "native-measure-detach", 1200))
      footer.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(secondNativeHeight, View.MeasureSpec.EXACTLY))
      field<android.view.ViewTreeObserver.OnPreDrawListener>(list, "stickyPreDraw").onPreDraw()
      assertEquals(acceptedSecondNativeHeight, field<Int>(list, "acceptedFooterNaturalHeightPx"))
      activity.setContentView(View(activity))
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
      assertEquals(acceptedSecondNativeHeight, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "naturalHeightPx"))
      assertEquals(0, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "hiddenTopPx"))
    }
  }

  fun testReplacingFooterDuringNativeDebtDoesNotReuseOldMeasurement() {
    val density = activity.resources.displayMetrics.density
    val oldFooter = View(activity)
    val newFooter = View(activity)
    onMain {
      repeat(2) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(oldFooter, 2)
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    onMain {
      list.applySnapshot(tailSnapshot(48, "replace-footer", 1200))
      oldFooter.measure(View.MeasureSpec.makeMeasureSpec(recycler.width,
        View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
          (160 * density).roundToInt(), View.MeasureSpec.EXACTLY))
      field<android.view.ViewTreeObserver.OnPreDrawListener>(list, "stickyPreDraw").onPreDraw()
      assertTrue(field<Int>(list, "structuralTailSignedExtentPx") < 0)
      assertEquals((56 * density).roundToInt(), field<IntArray>(list, "slotHeights")[2])
      list.unmountContainerSlot(2)
      list.mountContainerSlot(newFooter, 2)
      assertSame(newFooter, list.containerSlotAt(2))
      assertTrue(field<Boolean>(list, "structuralTailFinalizing"))
      assertNull(field<Int?>(list, "structuralFooterCompensatedHeightPx"))
      assertNull(field<Int?>(list, "acceptedFooterNaturalHeightPx"))
      val wrapper = field<FrameLayout>(list, "footerPresentationHost")
      assertSame(newFooter, wrapper.getChildAt(0))
      assertEquals(field<IntArray>(list, "slotHeights")[2], field<Int>(wrapper,
        "naturalHeightPx"))
      assertEquals(0, field<Int>(wrapper, "hiddenTopPx"))
    }
    awaitTailFinished(48)
    onMain {
      assertEquals(48, field<NativeListAdapter>(list, "adapter").itemCount)
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
      assertSame(newFooter, list.containerSlotAt(2))
    }
  }

  fun testAttachedDisposeSynchronouslyClearsNativeFooterDebt() {
    onMain {
      repeat(3) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    onMain {
      list.applySnapshot(tailSnapshot(48, "dispose-with-debt", 1200))
      list.setContainerSlotHeightsJson("[0,0,160,0]")
      assertTrue(list.isAttachedToWindow)
      assertTrue(field<Int>(list, "structuralTailSignedExtentPx") < 0)
      list.dispose()
      assertTrue(list.isAttachedToWindow)
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
      assertEquals(0, field<Int>(field<FrameLayout>(list,
        "footerPresentationHost"), "hiddenTopPx"))
    }
    SystemClock.sleep(1300)
    onMain {
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<Int>(list, "structuralTailSignedExtentPx"))
    }
  }

  fun testStructuralTailExplicitScrollCancelsOldCompletion() {
    onMain { list.applySnapshot(tailSnapshot(6)) }
    awaitAdapterCount(6)
    onMain { list.applySnapshot(tailSnapshot(50, "expand-scroll", 1200)) }
    awaitTailMidframe(6)
    onMain { list.scrollToIndex(40, false, "start") }
    awaitTailFinished(50)
    awaitFirstVisibleRowAtLeast(35)
    SystemClock.sleep(1300)
    onMain {
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
      assertNull(field<Any?>(list, "structuralAnimator"))
      assertTrue("Old animation callback must not reclaim the scroll owner",
        firstVisibleRow() >= 35)
    }
  }

  fun testStructuralTailUsesSidecarFinalRangeAndClearsOnBareDetach() {
    onMain {
      repeat(4) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,4000]")
      list.setSidecarLayoutJson("{\"width\":80,\"gap\":8,\"maxContentWidth\":500}")
      list.applySnapshot(tailSnapshot(50))
    }
    awaitAdapterCount(50)
    onMain { list.scrollToIndex(36, false, "start") }
    awaitScrollDistanceAtLeast((1000 * activity.resources.displayMetrics.density).roundToInt())
    onMain { list.applySnapshot(tailSnapshot(6, "collapse-sidecar", 900)) }
    awaitTailMidframe(6)
    var target = 0
    onMain { target = field(list, "structuralTailTargetOffset") }
    awaitTailFinished(6)
    onMain {
      val sidecarSpacer = field<Int>(list, "sidecarSpacerHeightPx")
      assertTrue(sidecarSpacer > 0)
      assertTrue(field<NativeListContainerSlotAdapter>(list,
        "sidecarSpacerAdapter").itemCount > 0)
      val measuredDistance = list.javaClass.getDeclaredMethod("structuralTailMeasuredScrollDistancePx")
        .apply { isAccessible = true }.invoke(list) as Int
      val footerPosition = field<androidx.recyclerview.widget.ConcatAdapter>(list,
        "scrollContentAdapter").itemCount - 2
      val footer = recycler.findViewHolderForAdapterPosition(footerPosition)?.itemView
      assertNotNull("Footer must be mounted at the final sidecar offset", footer)
      val top = recycler.layoutManager!!.getDecoratedTop(footer!!)
      val bottom = recycler.layoutManager!!.getDecoratedBottom(footer)
      val stickyBottom = field<View>(list, "stickyHeaderHost").bottom
      val tolerance = (2 * activity.resources.displayMetrics.density).roundToInt()
      assertTrue("Footer $top..$bottom must be fully visible below sticky $stickyBottom; " +
        "viewport ${recycler.paddingTop}..${recycler.height - recycler.paddingBottom}, " +
        "measured=$measuredDistance target=$target sidecarSpacer=$sidecarSpacer",
        top >= maxOf(stickyBottom, recycler.paddingTop) - tolerance &&
          bottom <= recycler.height - recycler.paddingBottom + tolerance)
      assertTrue("Measured native offset $measuredDistance must reach target $target",
        kotlin.math.abs(measuredDistance - target) <=
          (8 * activity.resources.displayMetrics.density).roundToInt())
      list.applySnapshot(tailSnapshot(50, "expand-detach", 1200))
    }
    awaitTailMidframe(6)
    onMain {
      activity.setContentView(View(activity))
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
    }
    SystemClock.sleep(1300)
    onMain {
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
      list.dispose()
    }
  }

  fun testStructuralTailResourceLimitCommitsAcceptedRows() {
    onMain { list.applySnapshot(tailSnapshot(6)) }
    awaitAdapterCount(6)
    onMain {
      list.applySnapshot(tailSnapshot(300, "cap", 1200, tailHeight = 1))
      assertEquals(300, field<NativeListConfig>(list, "config").items.size)
      assertEquals("The full old adapter remains until the accepted list commits", 6,
        field<NativeListAdapter>(list, "adapter").itemCount)
      assertNull(field<Any?>(list, "structuralTail"))
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
    }
    awaitTailFinished(300)
    onMain {
      assertEquals(300, field<NativeListConfig>(list, "config").items.size)
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
    }
  }

  fun testStructuralTailHeaderGeometryChangeInvalidatesTemporaryLayer() {
    onMain {
      list.mountContainerSlot(View(activity), 0)
      list.setContainerSlotHeightsJson("[0,0,0,0]")
      list.applySnapshot(tailSnapshot(50))
    }
    awaitAdapterCount(50)
    onMain { list.applySnapshot(tailSnapshot(6, "header-change", 1200)) }
    awaitTailMidframe(6)
    onMain { list.setContainerSlotHeightsJson("[20,0,0,0]") }
    awaitTailFinished(6)
    onMain {
      assertEquals(1, field<NativeListContainerSlotAdapter>(list, "headerSlotAdapter").itemCount)
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
    }
  }

  fun testStructuralTailUnsupportedSnapshotKeepsViewportUntilFullCommit() {
    onMain { list.applySnapshot(tailSnapshot(50)) }
    awaitAdapterCount(50)
    onMain { list.applySnapshot(tailSnapshot(6, "unsupported-change", 1200)) }
    awaitTailMidframe(6)
    onMain {
      val changed = JSONObject(tailSnapshot(6))
      changed.getJSONArray("rows").getJSONObject(0).put("height", 61)
      list.applySnapshot(changed.toString())
      assertEquals(61, field<NativeListConfig>(list, "config").items[0].json.optInt("height"))
      assertTrue("The old viewport must remain until DiffUtil commits the changed prefix",
        field<FrameLayout>(list, "structuralTailLayer").childCount > 0 &&
          field<Int>(list, "structuralSpacerHeightPx") > 0)
    }
    awaitTailFinished(6)
    onMain {
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
    }
  }

  fun testStructuralTailNewIdDuringFinalCommitOwnsAcceptedRows() {
    onMain { list.applySnapshot(tailSnapshot(50)) }
    awaitAdapterCount(50)
    onMain { list.applySnapshot(tailSnapshot(6, "first-collapse", 1200)) }
    awaitTailMidframe(6)
    onMain {
      list.javaClass.getDeclaredMethod("finishStructuralTail", Boolean::class.javaPrimitiveType)
        .apply { isAccessible = true }.invoke(list, false)
      assertTrue(field(list, "structuralTailFinalizing"))
      list.applySnapshot(tailSnapshot(50, "new-expand", 900))
      assertEquals(50, field<NativeListConfig>(list, "config").items.size)
    }
    awaitTailFinished(50)
    SystemClock.sleep(1300)
    onMain {
      assertEquals(50, field<NativeListAdapter>(list, "adapter").itemCount)
      assertEquals(0, field<FrameLayout>(list, "structuralTailLayer").childCount)
      assertEquals(0, field<Int>(list, "structuralSpacerHeightPx"))
      assertNull(field<Any?>(list, "structuralAnimator"))
    }
  }

  fun testStructuralTailLaysOutHostsWhenParentDefersLayout() {
    val naturalHeight = NativeListScale.dp(activity.resources, 60)
    onMain {
      repeat(3) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    val parent = field<FrameLayout>(list, "contentContainer")
    try {
      onMain {
        parent.suppressLayout(true)
        list.applySnapshot(tailSnapshot(48, "expand-deferred-layout", 1200))
      }
      var reachedTailWindow = false
      for (attempt in 0 until 100) {
        onMain {
          val layer = field<FrameLayout>(list, "structuralTailLayer")
          reachedTailWindow = field<Int>(list, "structuralSpacerHeightPx") > naturalHeight &&
            layer.childCount > 1
        }
        if (reachedTailWindow) break
        SystemClock.sleep(10)
      }
      assertTrue("The native tail must enter a visible midframe", reachedTailWindow)
      onMain {
        val layer = field<FrameLayout>(list, "structuralTailLayer")
        val spacer = field<Int>(list, "structuralSpacerHeightPx")
        assertEquals("Tail layer must measure without its parent layout pass",
          spacer, layer.measuredHeight)
        assertEquals("Tail layer must occupy its measured extent", spacer, layer.height)
        assertTrue("A newly bound native row must be measured", (0 until layer.childCount).any { index ->
          layer.getChildAt(index).measuredHeight == naturalHeight
        })
        assertTrue("A native row must intersect the visible RecyclerView", (0 until layer.childCount).any { index ->
          val host = layer.getChildAt(index)
          val top = layer.top + host.top
          val bottom = layer.top + host.bottom
          host.height == naturalHeight && top < recycler.bottom - recycler.paddingBottom &&
            bottom > recycler.top + recycler.paddingTop
        })
      }
    } finally {
      onMain { parent.suppressLayout(false) }
    }
    awaitTailFinished(48)
  }

  fun testFinalTailHandoffMountsSuffixBeforeRemovingHosts() {
    onMain {
      repeat(3) { index -> list.mountContainerSlot(View(activity), index) }
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    val layer = field<FrameLayout>(list, "structuralTailLayer")
    var sawFinalRemoval = false
    var suffixReadyAtRemoval = false
    var blankPreDraw = false
    val observer = list.viewTreeObserver
    val hierarchyListener = object : ViewGroup.OnHierarchyChangeListener {
      override fun onChildViewAdded(parent: View?, child: View?) = Unit

      override fun onChildViewRemoved(parent: View?, child: View?) {
        if (sawFinalRemoval || !field<Boolean>(list, "structuralTailFinalizing") ||
          field<NativeListAdapter>(list, "adapter").itemCount != 48) return
        sawFinalRemoval = true
        val headerCount = field<NativeListContainerSlotAdapter>(list, "headerSlotAdapter").itemCount
        val holder = recycler.findViewHolderForAdapterPosition(headerCount + 6)
        val row = holder?.itemView
        suffixReadyAtRemoval = holder?.bindingAdapter === field<NativeListAdapter>(list, "adapter") &&
          row != null && row.height == NativeListScale.dp(activity.resources, 60) &&
          row.top < recycler.height - recycler.paddingBottom && row.bottom > recycler.paddingTop
      }
    }
    val preDrawListener = ViewTreeObserver.OnPreDrawListener {
      if (field<NativeListAdapter>(list, "adapter").itemCount == 48 &&
        field<Any?>(list, "structuralTail") == null) {
        val headerCount = field<NativeListContainerSlotAdapter>(list, "headerSlotAdapter").itemCount
        val holder = recycler.findViewHolderForAdapterPosition(headerCount + 6)
        if (holder?.bindingAdapter !== field<NativeListAdapter>(list, "adapter") ||
          holder.itemView.height <= 0) blankPreDraw = true
      }
      true
    }
    onMain {
      layer.setOnHierarchyChangeListener(hierarchyListener)
      observer.addOnPreDrawListener(preDrawListener)
      list.applySnapshot(tailSnapshot(48, "expand-atomic-handoff", 1000))
    }
    try {
      awaitTailFinished(48)
      onMain {
        assertTrue("The bounded tail hosts must be released", sawFinalRemoval)
        assertTrue("A visible suffix holder must exist before the first tail host is removed",
          suffixReadyAtRemoval)
        assertFalse("A pre-draw frame must not lose both suffix presentations", blankPreDraw)
      }
    } finally {
      onMain {
        layer.setOnHierarchyChangeListener(null)
        if (observer.isAlive) observer.removeOnPreDrawListener(preDrawListener)
      }
    }
  }

  fun testCompletedTailLayerDoesNotStealFooterTouch() {
    val buttonId = 0x6f001
    val button = View(activity).apply { id = buttonId }
    val footer = FrameLayout(activity).apply {
      addView(button, FrameLayout.LayoutParams(240, NativeListScale.dp(activity.resources, 40)).apply {
        gravity = android.view.Gravity.CENTER
      })
    }
    onMain {
      repeat(2) { index -> list.mountContainerSlot(View(activity), index) }
      list.mountContainerSlot(footer, 2)
      list.setContainerSlotHeightsJson("[0,0,56,0]")
      list.applySnapshot(tailSnapshot(6))
    }
    awaitAdapterCount(6)
    onMain {
      val footerHeight = NativeListScale.dp(activity.resources, 56)
      footer.measure(View.MeasureSpec.makeMeasureSpec(recycler.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(footerHeight, View.MeasureSpec.EXACTLY))
      footer.layout(0, 0, recycler.width, footerHeight)
      assertTrue("Fabric-like footer child must have real geometry", button.width > 0 && button.height > 0)
    }
    val initialFooterPosition = field<androidx.recyclerview.widget.ConcatAdapter>(list,
      "scrollContentAdapter").itemCount - 1
    onMain { recycler.scrollToPosition(initialFooterPosition) }
    instrumentation.waitForIdleSync()
    onMain {
      val listLocation = IntArray(2)
      val buttonLocation = IntArray(2)
      list.getLocationOnScreen(listLocation)
      button.getLocationOnScreen(buttonLocation)
      val x = (buttonLocation[0] - listLocation[0] + button.width / 2).toFloat()
      val y = (buttonLocation[1] - listLocation[1] + button.height / 2).toFloat()
      val visible = android.graphics.Rect()
      assertTrue("Baseline footer button must be visible", button.getGlobalVisibleRect(visible))
      assertTrue("Baseline footer center must be visible",
        visible.contains(buttonLocation[0] + button.width / 2,
          buttonLocation[1] + button.height / 2))
      assertTrue("Baseline footer center must be in the list", x in 0f..list.width.toFloat() &&
        y in 0f..list.height.toFloat())
      val wrapper = field<FrameLayout>(list, "footerPresentationHost")
      val wrapperLocation = IntArray(2)
      wrapper.getLocationOnScreen(wrapperLocation)
      assertEquals("Baseline wrapper must reach the tagged footer child", buttonId,
        TouchTargetHelper.findTargetTagForTouch(
          (buttonLocation[0] - wrapperLocation[0] + button.width / 2).toFloat(),
          (buttonLocation[1] - wrapperLocation[1] + button.height / 2).toFloat(), wrapper))
      assertEquals("Baseline RN footer target must reach its tagged child", buttonId,
        TouchTargetHelper.findTargetTagForTouch(x, y, list))
    }
    onMain { recycler.scrollToPosition(0) }
    instrumentation.waitForIdleSync()
    onMain { list.applySnapshot(tailSnapshot(48, "footer-touch", 1000)) }
    awaitTailFinished(48)
    val footerPosition = field<androidx.recyclerview.widget.ConcatAdapter>(list,
      "scrollContentAdapter").itemCount - 1
    onMain { recycler.scrollToPosition(footerPosition) }
    instrumentation.waitForIdleSync()
    onMain {
      val layer = field<FrameLayout>(list, "structuralTailLayer")
      assertEquals(View.GONE, layer.visibility)
      val holder = recycler.findViewHolderForAdapterPosition(footerPosition)
      assertNotNull("Footer must be mounted for a real target search", holder)
      val listLocation = IntArray(2)
      val buttonLocation = IntArray(2)
      list.getLocationOnScreen(listLocation)
      button.getLocationOnScreen(buttonLocation)
      val x = (buttonLocation[0] - listLocation[0] + button.width / 2).toFloat()
      val y = (buttonLocation[1] - listLocation[1] + button.height / 2).toFloat()
      val visible = android.graphics.Rect()
      assertTrue("Footer button must be visible", button.getGlobalVisibleRect(visible))
      assertTrue("Footer button center must be visible",
        visible.contains(buttonLocation[0] + button.width / 2,
          buttonLocation[1] + button.height / 2))
      assertTrue("Footer button center must be in the list: x=$x y=$y layer=${layer.top}..${layer.bottom}",
        x in 0f..list.width.toFloat() && y in 0f..list.height.toFloat())
      val path = TouchTargetHelper.findTargetPathAndCoordinatesForTouch(x, y, list,
        floatArrayOf(0f, 0f)).take(9).joinToString(" > ") { target ->
        val view = target.getView()
        "${view?.javaClass?.simpleName}:${view?.id}:${view?.left},${view?.top},${view?.right},${view?.bottom}"
      }
      val wrapper = field<FrameLayout>(list, "footerPresentationHost")
      val wrapperLocation = IntArray(2)
      wrapper.getLocationOnScreen(wrapperLocation)
      val wrapperTarget = TouchTargetHelper.findTargetTagForTouch(
        (buttonLocation[0] - wrapperLocation[0] + button.width / 2).toFloat(),
        (buttonLocation[1] - wrapperLocation[1] + button.height / 2).toFloat(), wrapper)
      assertEquals("Wrapper must still reach the tagged footer child", buttonId, wrapperTarget)
      assertEquals("RN touch target must reach footer button despite completed tail layer " +
        "${layer.left},${layer.top},${layer.right},${layer.bottom} visibility=${layer.visibility} " +
        "wrapperTarget=$wrapperTarget path=$path",
        buttonId, TouchTargetHelper.findTargetTagForTouch(x, y, list))
      assertEquals("Completed tail layer must have zero width", 0, layer.width)
      assertEquals("Completed tail layer must have zero height", 0, layer.height)
    }
  }

  private fun tailSnapshot(
    count: Int,
    transitionId: String? = null,
    durationMs: Int = 220,
    title: String = "asset",
    tailHeight: Int = 60,
    itemSpacing: Int = 4,
    walletSidebar: Boolean = false,
  ): String {
    val rows = JSONArray()
    repeat(count) { index ->
      val row = JSONObject().put("key", "row-$index")
        .put("type", "identity").put("title", "$title $index")
        .put("height", if (index < 6) 60 else tailHeight)
      if (walletSidebar) row.put("presentation", "walletSidebar")
      rows.put(row)
    }
    val layout = JSONObject().put("kind", "linear").put("itemSpacing", itemSpacing)
    val root = JSONObject().put("schemaVersion", 1).put("generation", count)
      .put("layout", layout)
      .put("rows", rows)
    if (transitionId != null) root.put("structuralTransition",
      JSONObject().put("id", transitionId).put("durationMs", durationMs))
    return root.toString()
  }

  private fun awaitAdapterCount(expected: Int) {
    repeat(100) {
      var ready = false
      onMain { ready = field<NativeListAdapter>(list, "adapter").itemCount == expected &&
        !recycler.isLayoutRequested }
      if (ready) return
      SystemClock.sleep(20)
    }
    fail("Native adapter did not commit $expected rows")
  }

  private fun awaitScrollDistanceAtLeast(minimumPx: Int) {
    repeat(100) {
      var ready = false
      onMain { ready = recycler.computeVerticalScrollOffset() >= minimumPx }
      if (ready) return
      SystemClock.sleep(20)
    }
    fail("Native RecyclerView did not reach the tail viewport")
  }

  private fun firstVisibleRow(): Int =
    (recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager)
      .findFirstVisibleItemPosition().coerceAtLeast(0)

  private fun awaitFirstVisibleRowAtLeast(minimum: Int) {
    repeat(100) {
      var ready = false
      onMain { ready = firstVisibleRow() >= minimum }
      if (ready) return
      SystemClock.sleep(20)
    }
    fail("Native command did not reach row $minimum")
  }

  private fun awaitTailMidframe(expectedAdapterCount: Int) {
    repeat(100) {
      var ready = false
      onMain {
        val layer = field<FrameLayout>(list, "structuralTailLayer")
        ready = field<Any?>(list, "structuralTail") != null &&
          field<Any?>(list, "structuralAnimator") != null &&
          field<NativeListAdapter>(list, "adapter").itemCount == expectedAdapterCount &&
          field<Int>(list, "structuralSpacerHeightPx") > 0 && layer.childCount > 0 &&
          layer.getChildAt(0).height > 0
      }
      if (ready) return
      SystemClock.sleep(10)
    }
    fail("Native structural tail did not draw a bounded midframe")
  }

  private fun awaitTailFinished(expectedAdapterCount: Int) {
    repeat(200) {
      var ready = false
      onMain {
        ready = field<Any?>(list, "structuralTail") == null &&
          !field<Boolean>(list, "structuralTailCommandActive") &&
          field<NativeListAdapter>(list, "adapter").itemCount == expectedAdapterCount &&
          !recycler.isLayoutRequested
      }
      if (ready) return
      SystemClock.sleep(20)
    }
    fail("Native structural tail did not commit its accepted rows")
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
