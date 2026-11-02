package com.margelo.nitro.nativelist

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.os.Bundle
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.HapticFeedbackConstants
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.PointerEvents
import com.facebook.react.uimanager.ReactPointerEventsView
import com.facebook.react.uimanager.events.NativeGestureUtil
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.function.BiConsumer
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private class NativeListGridLayoutManager(
  context: Context,
  spanCount: Int,
) : GridLayoutManager(context, spanCount) {
  var onChildLaidOut: ((View, Int) -> Unit)? = null
  var onLayoutPassCompleted: (() -> Unit)? = null
  var onBeforeDataLayout: (() -> Unit)? = null

  override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
    if (!state.isPreLayout) onBeforeDataLayout?.invoke()
    super.onLayoutChildren(recycler, state)
  }

  // Scroll fills and layout passes both place children here, including rows that a
  // positional jump lays out and immediately leaves offscreen.
  override fun layoutDecoratedWithMargins(child: View, left: Int, top: Int, right: Int, bottom: Int) {
    super.layoutDecoratedWithMargins(child, left, top, right, bottom)
    onChildLaidOut?.invoke(child, if (orientation == RecyclerView.VERTICAL) bottom - top else right - left)
  }

  override fun onLayoutCompleted(state: RecyclerView.State) {
    super.onLayoutCompleted(state)
    onLayoutPassCompleted?.invoke()
  }

  override fun removeAndRecycleViewAt(index: Int, recycler: RecyclerView.Recycler) {
    val child = getChildAt(index) ?: return
    removeViewAt(index)
    // An index-based removal can leave the selected child attached during rapid
    // nested scrolling. Detach that exact view before giving it to RecyclerView.
    if (child.parent != null) removeView(child)
    if (child.parent == null) {
      recycler.recycleView(child)
    } else {
      requestLayout()
    }
  }
}

class NativeListView(
  private val reactContext: ThemedReactContext,
) : LinearLayout(reactContext) {
  private val fonts = NativeListFonts()

  fun setFontFamiliesJson(json: String) {
    if (!fonts.configure(json)) return
    if (structuralTail != null) finishStructuralTail(interrupted = true)
    invalidateActionAnchor("layout")
    measuredRowExtentsPx.clear()
    measuredRowExtentSumPx = 0L
    config?.let { updateReordering(it) }
    adapter.rebindFonts()
    config?.let { bindFooter(it) }
    sectionIndexView.refreshFonts()
    sectionIndexPreview.typeface = fonts.regular(context)
    stickyHeaderSignature = null
    stickyHeaderConfig = null
    updateStickyHeader()
    recyclerView.requestLayout()
    requestLayout()
  }

  private data class VisibleMarketAnchor(
    val key: String,
    val offset: Int,
  )

  private class ActionAnchorRecord(
    val token: String,
    origin: NativeListActionOrigin,
  ) {
    val sourceView = WeakReference(origin.sourceView)
    val ownerRowView = WeakReference(origin.ownerRowView)
    val bindingEpoch = origin.bindingEpoch
    var open = false
    var invalidatedReason: String? = null
  }

  private sealed class ScrollRequest {
    data class Key(
      val key: String,
      val animated: Boolean,
      val alignment: String,
      val viewPosition: Double,
      val viewOffset: Double,
    ) : ScrollRequest()

    data class Index(
      val index: Int,
      val animated: Boolean,
      val alignment: String,
      val viewPosition: Double,
      val viewOffset: Double,
    ) : ScrollRequest()

    data class Offset(val offset: Double, val animated: Boolean) : ScrollRequest()
    data class End(val animated: Boolean) : ScrollRequest()
  }

  private val coordinatorOffsetListener = Runnable { emitScrollPositionThresholdIfNeeded() }
  private var logicalScrollDistancePx = 0
  private val scrollPositionTracker = NativeListScrollPositionTracker()
  private val measuredRowExtentsPx = HashMap<String, Int>()
  private var measuredRowExtentSumPx = 0L
  var onScrollPositionThresholdChange: ((Boolean) -> Unit)? = null
    set(value) {
      field = value
      scrollPositionTracker.resetDelivery()
      emitScrollPositionThresholdIfNeeded(resync = true)
    }

  fun setScrollPositionThresholdsJson(json: String) {
    val config = runCatching { JSONObject(json) }.getOrNull()
    scrollPositionTracker.configure(config?.optDouble("start"), config?.optDouble("end"))
    emitScrollPositionThresholdIfNeeded(resync = true)
  }

  /**
   * Continuous scrolling accumulates the exact consumed dy. Layout passes and positional
   * jumps (scrollToIndex/End, anchor restores, inserts above) report no dy, so they re-derive
   * the distance from the laid-out first item plus the extents of every item before it.
   */
  private fun emitScrollPositionThresholdIfNeeded(resync: Boolean = false) {
    if (disposed) return
    val callback = onScrollPositionThresholdChange ?: return
    if (!scrollPositionTracker.isConfigured) return
    if (resync || layoutManager.findFirstVisibleItemPosition() == 0) {
      measuredScrollDistancePx()?.let { logicalScrollDistancePx = it }
    }
    val contributions = recyclerView.getTag(R.id.onekey_native_scroll_coordinator_consumed_offsets) as? Map<*, *>
    val parentDistance = contributions?.values?.sumOf { (it as? Int)?.coerceAtLeast(0)?.toLong() ?: 0L } ?: 0L
    val distance = logicalScrollDistancePx.toLong() + parentDistance
    val next = scrollPositionTracker.update(distance.toDouble() / density) ?: return
    callback(next)
  }

  var onRowAction: ((String) -> Unit)? = null
  var onActionAnchorInvalidated: ((String) -> Unit)? = null
  var onSelectionDelta: ((String) -> Unit)? = null
  var onReorder: ((String) -> Unit)? = null
  var onEndReached: ((String) -> Unit)? = null
  var onVisibleRangeChanged: ((String) -> Unit)? = null
  var onStructuralTransitionSettled: ((String) -> Unit)? = null
  var keyboardDismissMode = NativeListKeyboardDismissMode.NONE
  var keyboardShouldPersistTaps = NativeListKeyboardShouldPersistTaps.NEVER
  private val density = resources.displayMetrics.density
  private val recyclerView = object : RecyclerView(context) {
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
      if (isLayoutSuppressed) {
        setMeasuredDimension(measuredWidth, measuredHeight)
        return
      }
      super.onMeasure(widthSpec, heightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      if (isLayoutSuppressed) return
      super.onLayout(changed, left, top, right, bottom)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
      val handled = super.dispatchTouchEvent(event)
      if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
        finishPagerNativeGesture(event)
      }
      return handled
    }
  }
  private var configuredTopPaddingPx = 0
  private var slotInsetPx = 0
  private var slotRightInsetPx = 0
  private var configuredBottomPaddingPx = 0
  private data class SidecarLayout(val width: Int, val gap: Int, val maxContentWidth: Int)
  private var sidecarLayout: SidecarLayout? = null
  private var sidecarSpacerHeightPx = 0
  private var sidecarSpacerScheduled = false
  private var consumedStructuralTransitionId: String? = null
  private var structuralAnimator: ValueAnimator? = null
  private var structuralSpacerHeightPx = 0
  private var structuralTailSignedExtentPx = 0
  private var structuralStartSpacerPx = 0
  private var structuralFooterVisualStartTop: Int? = null
  private var structuralFooterVisualEndTop: Int? = null
  private var structuralFooterVisualProgress = 0f
  private var structuralFooterCompensatedHeightPx: Int? = null
  private var structuralFooterObservedNativeHeightPx: Int? = null
  private var acceptedFooterNaturalHeightPx: Int? = null
  private data class PendingPreparingFooterFrame(
    val heights: IntArray,
    val naturalHeight: Int,
    val observedNativeHeight: Int?,
    val ownerOffset: Int,
    val epoch: Int,
    val footer: View?,
    val handoffOnly: Boolean,
  )
  private var pendingPreparingFooterFrame: PendingPreparingFooterFrame? = null
  private var preparingFooterFrameScheduled = false
  private var structuralScrollInterrupted = false
  private var structuralFooterHoldTop: Int? = null
  private var structuralTransitionEpoch = 0
  private class StructuralTail(
    var items: MutableList<NativeListItem>,
    val heights: IntArray,
    spacings: IntArray,
    val prefixCount: Int,
    val finalExtent: Int,
  ) {
    val starts = IntArray(items.size)
    val indexByKey = items.withIndex().associate { it.value.key to it.index }
    val hosts = mutableMapOf<Int, NativeListRowHost>()
    var peakHosts = 0
    val extent: Int
    init {
      var edge = 0
      for (index in items.indices) {
        starts[index] = edge
        edge += heights[index] + spacings[index]
      }
      extent = edge
    }
  }
  private val structuralTailLayer = object : FrameLayout(reactContext), ReactPointerEventsView {
    override val pointerEvents: PointerEvents = PointerEvents.NONE
    override fun dispatchTouchEvent(event: MotionEvent) = false
  }.apply {
    clipChildren = true
    clipToPadding = true
    isClickable = false
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    visibility = GONE
  }
  private var structuralTail: StructuralTail? = null
  private var structuralTailRowsInAdapterPx = 0
  private var structuralTailStartExtentPx = 0
  private var structuralTailStartProgress = 0f
  private var structuralTailStartContentY = 0
  private var structuralTailStartOffset = 0
  private var structuralTailTargetOffset = 0
  private var disclosureFraction = 0f
  private var disclosureStartFraction = 0f
  private var disclosurePresentedContentPx = 0
  private var disclosurePresentedInsetPx = 0
  private var disclosureContentStartPx = 0
  private var disclosureContentStartProgress = 0f
  private var disclosureTransitioning = false
  private var activeDisclosureTransitionId: String? = null
  private var settledDisclosureTransitionId: String? = null
  private var directDisclosureStatus = "interrupted"
  private var directDisclosureTogglePositionPending = false
  private var batchedDisclosureGeometryInvalid = false
  private var structuralTailCommandActive = false
  private var structuralTailFinalizing = false
  private var structuralTailPreparing = false
  private data class PendingStructuralTransition(
    val transition: NativeListConfig.StructuralTransition,
    val epoch: Int,
    val anchorKey: String?,
    val anchorTop: Int?,
    val footerStartTop: Int?,
    val heldHeightPx: Int,
    val collapsing: Boolean,
    val spacerInserted: Boolean = false,
    val footerAnchorAppliedTop: Int? = null,
    val rowAnchorAppliedTop: Int? = null,
  )
  private var pendingStructuralTransition: PendingStructuralTransition? = null
  private var pendingStructuralStartScheduled = false
  private val refreshLayout = SwipeRefreshLayout(context)
  private val refreshIndicatorTravelPx = refreshLayout.progressViewEndOffset
  private val defaultRefreshTriggerDistancePx = (64 * resources.displayMetrics.density).roundToInt()
  private var refreshIndicatorOffsetPx = 0
  private val contentContainer = FrameLayout(context)
  private val sidecarHost = object : FrameLayout(context) {
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var down: MotionEvent? = null
    private var routing = false

    private fun route(event: MotionEvent) {
      val copy = MotionEvent.obtain(event)
      copy.offsetLocation(x - refreshLayout.x, y - refreshLayout.y)
      refreshLayout.dispatchTouchEvent(copy)
      copy.recycle()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          down?.recycle()
          down = MotionEvent.obtain(event)
          routing = false
        }
        MotionEvent.ACTION_MOVE -> {
          if (routing) {
            route(event)
            return true
          }
          val start = down
          if (start != null && abs(event.y - start.y) > touchSlop &&
            abs(event.y - start.y) > abs(event.x - start.x)) {
            val cancel = MotionEvent.obtain(event)
            cancel.action = MotionEvent.ACTION_CANCEL
            super.dispatchTouchEvent(cancel)
            cancel.recycle()
            route(start)
            route(event)
            routing = true
            return true
          }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          if (routing) route(event)
          down?.recycle()
          down = null
          if (routing) {
            routing = false
            return true
          }
        }
        else -> if (routing) {
          route(event)
          return true
        }
      }
      return super.dispatchTouchEvent(event)
    }
  }
  private val adapter = NativeListAdapter(reactContext, fonts)
  private val containerSlots = mutableListOf<View>()
  private val slotHeights = IntArray(6)
  private val expandedContentHost = object : FrameLayout(context), ReactPointerEventsView {
    var naturalHeightPx = 0
    override val pointerEvents: PointerEvents
      get() = if (config?.disclosure?.expanded == true &&
        !disclosureTransitioning && disclosureFraction >= 1f) PointerEvents.AUTO
      else PointerEvents.NONE

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
        MeasureSpec.getSize(heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      getChildAt(0)?.layout(0, 0, right - left, naturalHeightPx)
    }
  }.apply {
    clipChildren = true
    clipToPadding = true
    isClickable = false
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
  }
  private val footerPresentationHost = object : FrameLayout(context) {
    var naturalHeightPx = 0
    var hiddenTopPx = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
        MeasureSpec.getSize(heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      val child = getChildAt(0) ?: return
      child.layout(0, -hiddenTopPx, right - left, naturalHeightPx - hiddenTopPx)
    }
  }.apply {
    clipChildren = true
    clipToPadding = true
  }
  private var initialHeaderInsertionHandled = false
  private var containerSlotRelayoutPending = false
  private var containerSlotRelayoutScheduled = false
  private val headerSlotAdapter = NativeListContainerSlotAdapter()
  private val emptySlotAdapter = NativeListContainerSlotAdapter()
  private val structuralSpacerView = View(context)
  private val structuralSpacerAdapter = NativeListContainerSlotAdapter()
  private val expandedContentAdapter = NativeListContainerSlotAdapter()
  private val structureToggleAdapter = NativeListContainerSlotAdapter()
  private val disclosureInsetView = View(context)
  private val disclosureInsetAdapter = NativeListContainerSlotAdapter()
  private val footerSlotAdapter = NativeListContainerSlotAdapter()
  private val sidecarSpacerView = View(context)
  private val sidecarSpacerAdapter = NativeListContainerSlotAdapter()
  private val scrollContentAdapter = ConcatAdapter(
    headerSlotAdapter, adapter, emptySlotAdapter, structuralSpacerAdapter,
    expandedContentAdapter, structureToggleAdapter, disclosureInsetAdapter,
    footerSlotAdapter, sidecarSpacerAdapter,
  )

  fun mountContainerSlot(view: View, index: Int) {
    containerSlots.add(index.coerceIn(0, containerSlots.size), view)
    updateContainerSlots()
    scheduleContainerSlotRelayout()
  }

  fun containerSlotCount(): Int = containerSlots.size
  fun containerSlotAt(index: Int): View = containerSlots[index]
  fun unmountContainerSlot(index: Int) {
    val view = containerSlots.removeAt(index)
    (view.parent as? ViewGroup)?.removeView(view)
    updateContainerSlots()
    scheduleContainerSlotRelayout()
  }

  fun setContainerSlotHeightsJson(json: String, upcomingSnapshotJson: String? = null) {
    val values = runCatching { JSONArray(json) }.getOrNull() ?: return
    if (values.length() != 4 && values.length() != 6) return
    val nextHeights = IntArray(6)
    for (index in 0 until values.length()) {
      val value = values.optDouble(index)
      if (!value.isFinite() || value < 0) return
      nextHeights[index] = (value * density).roundToInt()
    }
    val disclosureTail = structuralTail != null && config?.disclosure != null &&
      !structuralTailFinalizing
    if (disclosureTail && upcomingSnapshotJson != null &&
      (nextHeights[2] != slotHeights[2] || nextHeights[5] != slotHeights[5] ||
        nextHeights[4] < disclosurePresentedContentPx)) {
      // Nitro delivers both setters in this UI turn. Let the complete new snapshot
      // replace the old command before deciding whether its geometry is supported.
      batchedDisclosureGeometryInvalid = true
      for (index in 0..5) slotHeights[index] = nextHeights[index]
      acceptedFooterNaturalHeightPx = nextHeights[2]
      return
    }
    val unsupportedDisclosureGeometry = disclosureTail &&
      (nextHeights[2] != slotHeights[2] || nextHeights[5] != slotHeights[5] ||
        nextHeights[4] < disclosurePresentedContentPx)
    if (unsupportedDisclosureGeometry) {
      // A changing fixed footer/toggle or a shrinking mounted C cannot be represented
      // by the current visual segment. Commit the accepted endpoint before ACK.
      structuralFooterCompensatedHeightPx = null
      acceptedFooterNaturalHeightPx = nextHeights[2]
      for (index in 0..5) slotHeights[index] = nextHeights[index]
      updateContainerSlots()
      finishStructuralTail(interrupted = true)
      return
    }
    if (disclosureTail && nextHeights[4] != slotHeights[4]) {
      disclosureContentStartPx = disclosurePresentedContentPx
      disclosureContentStartProgress =
        (structuralAnimator?.animatedValue as? Float) ?: 0f
    }
    if (structuralTail != null && structuralTailRowsInAdapterPx > 0 &&
      listOf(0, 1, 3).all { slotHeights[it] == nextHeights[it] } &&
      (pendingPreparingFooterFrame != null ||
        (!structuralTailFinalizing && slotHeights[2] != nextHeights[2]))) {
      enqueuePreparingFooterFrame(nextHeights, nextHeights[2])
      return
    }
    pendingPreparingFooterFrame?.takeIf {
      structuralTailRowsInAdapterPx > 0 && it.footer === containerSlots.getOrNull(2)
    }?.let { pending ->
      val preservesNewerHeight = nextHeights[2] == slotHeights[2] &&
        pending.naturalHeight != nextHeights[2]
      pendingPreparingFooterFrame = pending.copy(
        heights = nextHeights.copyOf(),
        naturalHeight = if (preservesNewerHeight) pending.naturalHeight else nextHeights[2],
        observedNativeHeight = pending.observedNativeHeight.takeIf {
          preservesNewerHeight || pending.naturalHeight == nextHeights[2]
        },
      )
    }
    if (structuralTailPreparing && (0..3).any { index ->
        slotHeights[index] != nextHeights[index]
      }) invalidateTailForContainerGeometry()
    if (structuralTail != null && listOf(0, 1, 3).any { index ->
        slotHeights[index] != nextHeights[index]
      }) finishStructuralTail(interrupted = true)
    val footerHeightPx = nextHeights[2]
    if (footerHeightPx != slotHeights[2]) {
      containerSlots.getOrNull(2)?.measuredHeight?.takeIf { it > 0 && it != footerHeightPx }
        ?.let { structuralFooterObservedNativeHeightPx = it }
      reconcileStructuralFooterHeight(footerHeightPx)
      acceptedFooterNaturalHeightPx = footerHeightPx
    } else if (acceptedFooterNaturalHeightPx == null) {
      acceptedFooterNaturalHeightPx = footerHeightPx
    }
    val hiddenDisclosureContentOnlyChange = config?.disclosure?.expanded == false &&
      !disclosureTransitioning && structuralTail == null &&
      !structuralTailPreparing && !structuralTailFinalizing &&
      disclosurePresentedContentPx == 0 && disclosurePresentedInsetPx == 0 &&
      expandedContentAdapter.itemCount == 0 &&
      slotHeights[4] != nextHeights[4] &&
      (0..5).all { index -> index == 4 || slotHeights[index] == nextHeights[index] }
    var changed = false
    for (index in 0..5) {
      val height = nextHeights[index]
      if (slotHeights[index] != height) changed = true
      slotHeights[index] = height
    }
    if (hiddenDisclosureContentOnlyChange) {
      expandedContentHost.naturalHeightPx = slotHeights[4]
      layoutExpandedContentHostNow()
      return
    }
    updateContainerSlots()
    if (structuralTail != null) updateStructuralTailTarget()
    updateSidecarGeometry()
    scheduleSidecarSpacer()
    if (changed) scheduleContainerSlotRelayout()
  }

  private fun enqueuePreparingFooterFrame(
    heights: IntArray,
    naturalHeight: Int,
    observedNativeHeight: Int? = null,
  ) {
    val footer = containerSlots.getOrNull(2)
    val existing = pendingPreparingFooterFrame?.takeIf { it.footer === footer }
    val preservesNewerHeight = existing != null && observedNativeHeight == null &&
      naturalHeight == slotHeights[2] && existing.naturalHeight != naturalHeight
    val acceptedHeight = if (preservesNewerHeight) existing.naturalHeight else naturalHeight
    val observedHeight = observedNativeHeight ?:
      existing?.observedNativeHeight?.takeIf { preservesNewerHeight || it == acceptedHeight }
    pendingPreparingFooterFrame = PendingPreparingFooterFrame(
      heights.copyOf(), acceptedHeight, observedHeight,
      existing?.ownerOffset ?: structuralTailStartOffset,
      structuralTransitionEpoch, footer, existing?.handoffOnly == true,
    )
    if (!commitPreparingFooterFrame() && pendingPreparingFooterFrame?.handoffOnly != true) {
      schedulePreparingFooterFrame()
    }
  }

  private fun schedulePreparingFooterFrame() {
    if (preparingFooterFrameScheduled) return
    preparingFooterFrameScheduled = true
    recyclerView.post {
      preparingFooterFrameScheduled = false
      if (!commitPreparingFooterFrame()) recyclerView.postInvalidateOnAnimation()
    }
    recyclerView.postInvalidateOnAnimation()
  }

  private fun reconcileStructuralFooterHeight(nextHeightPx: Int) {
    if (structuralTail != null) {
      if (!structuralTailFinalizing) {
        val delta = (structuralFooterCompensatedHeightPx ?:
          acceptedFooterNaturalHeightPx ?: slotHeights[2]) - nextHeightPx
        if (delta != 0) {
          if (structuralTailPreparing) {
            invalidateTailForContainerGeometry()
            return
          }
          structuralFooterCompensatedHeightPx = nextHeightPx
          val compensated = structuralTailSignedExtentPx + delta
          structuralTailStartProgress = (structuralAnimator?.animatedValue as? Float) ?: 0f
          structuralTailStartExtentPx = compensated
          if (structuralTailRowsInAdapterPx == 0) structuralTailStartOffset = tailScrollExtentPx()
          setStructuralTailExtent(compensated)
          if (structuralTailRowsInAdapterPx == 0) updateStructuralTailWindow()
        }
      }
      return
    }
    val previousHeightPx = structuralFooterCompensatedHeightPx ?:
      acceptedFooterNaturalHeightPx ?: slotHeights[2]
    val footerDelta = previousHeightPx - nextHeightPx
    if (footerDelta == 0) return
    if (pendingStructuralTransition != null || structuralAnimator != null || structuralFooterHoldTop != null) {
      structuralFooterCompensatedHeightPx = nextHeightPx
    }
    if (pendingStructuralTransition != null) {
      pendingStructuralTransition = pendingStructuralTransition?.let {
        val updated = it.copy(
          footerStartTop = it.footerStartTop?.plus(footerDelta),
          heldHeightPx = (it.heldHeightPx + footerDelta).coerceAtLeast(0),
        )
        if (updated.spacerInserted) setStructuralSpacerHeight(updated.heldHeightPx)
        updated
      }
      structuralFooterHoldTop = structuralFooterHoldTop?.plus(footerDelta)
      updateStructuralFooterHoldPosition()
    } else if (structuralFooterHoldTop != null && structuralAnimator == null) {
      setStructuralSpacerHeight(structuralSpacerHeightPx + footerDelta)
      structuralFooterHoldTop = structuralFooterHoldTop?.plus(footerDelta)
      updateStructuralFooterHoldPosition()
    } else if (structuralAnimator != null) {
      structuralStartSpacerPx = (structuralStartSpacerPx + footerDelta).coerceAtLeast(0)
      structuralFooterVisualStartTop = structuralFooterVisualStartTop?.plus(footerDelta)
      val fraction = (structuralAnimator?.animatedValue as? Float) ?: 0f
      setStructuralSpacerHeight((structuralStartSpacerPx * (1f - fraction)).roundToInt())
      updateStructuralFooterVisualPosition()
    }
  }

  fun setSidecarLayoutJson(json: String) {
    val next = if (json.isEmpty()) null else {
      val value = runCatching { JSONObject(json) }.getOrNull() ?: return
      val width = value.optDouble("width")
      val gap = value.optDouble("gap")
      val maxWidth = value.optDouble("maxContentWidth")
      if (!width.isFinite() || width <= 0 || !gap.isFinite() || gap < 0 ||
        !maxWidth.isFinite() || maxWidth <= width + gap) return
      SidecarLayout((width * density).roundToInt(), (gap * density).roundToInt(),
        (maxWidth * density).roundToInt())
    }
    if (next == sidecarLayout) return
    if (structuralTail != null) finishStructuralTail(interrupted = true)
    sidecarLayout = next
    config?.let(::updateLayout)
    updateSidecarGeometry()
    scheduleSidecarSpacer()
  }

  private fun sidecarBaseInsetPx(): Int {
    val layout = sidecarLayout ?: return 0
    val width = recyclerView.width
    return max(0, (width - minOf(width, layout.maxContentWidth)) / 2)
  }

  private fun updateSidecarGeometry() {
    val layout = sidecarLayout
    val root = containerSlots.getOrNull(3)
    if (root !== sidecarHost.getChildAt(0)) {
      sidecarHost.removeAllViews()
      (root?.parent as? ViewGroup)?.removeView(root)
      if (root != null) sidecarHost.addView(root)
    }
    val params = sidecarHost.layoutParams as? FrameLayout.LayoutParams
      ?: FrameLayout.LayoutParams(0, 0, Gravity.TOP or Gravity.RIGHT)
    val nextWidth = layout?.width ?: 0
    val nextHeight = if (layout != null) slotHeights[3] else 0
    val isRtl = layoutDirection == LAYOUT_DIRECTION_RTL
    val nextGravity = Gravity.TOP or (if (isRtl) Gravity.LEFT else Gravity.RIGHT)
    val nextSideMargin = sidecarBaseInsetPx()
    if (params.width != nextWidth || params.height != nextHeight ||
      params.gravity != nextGravity ||
      params.leftMargin != (if (isRtl) nextSideMargin else 0) ||
      params.rightMargin != (if (isRtl) 0 else nextSideMargin)) {
      params.width = nextWidth
      params.height = nextHeight
      params.gravity = nextGravity
      params.leftMargin = if (isRtl) nextSideMargin else 0
      params.rightMargin = if (isRtl) 0 else nextSideMargin
      sidecarHost.layoutParams = params
    }
    sidecarHost.visibility = if (layout != null && root != null && slotHeights[3] > 0) VISIBLE else GONE
    updateSidecarPosition()
  }

  private fun updateSidecarPosition(resync: Boolean = false) {
    if (sidecarLayout == null) return
    if (resync) (if (structuralTail == null) measuredScrollDistancePx()
      else structuralTailMeasuredScrollDistancePx())?.let { logicalScrollDistancePx = it }
    sidecarHost.translationY = (recyclerView.paddingTop - logicalScrollDistancePx).toFloat()
  }

  private fun sidecarLeftExtentPx(): Int =
    (nativeListScrollPrefixPx(
      scrollContentAdapter.itemCount - sidecarSpacerAdapter.itemCount,
      layoutManager.spanCount,
      layoutManager.spanSizeLookup::getSpanSize,
      if (structuralTail == null) ::estimatedScrollExtentPx else ::structuralTailRowOrSlotExtentPx,
    ) + recyclerView.paddingTop + recyclerView.paddingBottom)
      .coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()

  private fun scheduleSidecarSpacer() {
    if (sidecarLayout == null && sidecarSpacerHeightPx == 0) return
    if (disposed || sidecarSpacerScheduled) return
    sidecarSpacerScheduled = true
    recyclerView.post {
      sidecarSpacerScheduled = false
      updateSidecarSpacerNow()
    }
  }

  private fun updateSidecarSpacerNow() {
    if (disposed || recyclerView.width <= 0 || recyclerView.height <= 0) return
    val desired = if (sidecarLayout != null) {
      recyclerView.paddingTop + slotHeights[3] + recyclerView.paddingBottom
    } else 0
    val leftExtent = sidecarLeftExtentPx()
    val next = (desired - leftExtent).coerceAtLeast(0)
    if (abs(next - sidecarSpacerHeightPx) <= 1) return
    sidecarSpacerHeightPx = next
    sidecarSpacerAdapter.update(sidecarSpacerView, next, next > 0)
    recyclerView.requestLayout()
  }

  private fun scheduleContainerSlotRelayout() {
    if (disposed) return
    containerSlotRelayoutPending = true
    if (containerSlotRelayoutScheduled) return
    containerSlotRelayoutScheduled = true
    recyclerView.post {
      containerSlotRelayoutScheduled = false
      if (disposed) {
        containerSlotRelayoutPending = false
        return@post
      }
      if (!containerSlotRelayoutPending || recyclerView.width <= 0 ||
        recyclerView.height <= 0 || recyclerView.isComputingLayout) return@post
      containerSlotRelayoutPending = false
      relayoutRecyclerViewImmediately()
    }
  }

  private fun updateContainerSlots() {
    val headerWasAbsent = headerSlotAdapter.itemCount == 0
    val firstVisible = layoutManager.findFirstVisibleItemPosition()
    val firstView = layoutManager.findViewByPosition(firstVisible)
    val restoreInitialHeaderTop = !initialHeaderInsertionHandled && headerWasAbsent &&
      layoutManager.orientation == RecyclerView.VERTICAL &&
      adapter.itemCount > 0 && readingIntentEpoch == 0L && pendingScrollRequest == null &&
      acceptedScrollRequest == null && !executingScrollCommand &&
      recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && firstVisible == 0 &&
      firstView != null && abs(layoutManager.getDecoratedTop(firstView) - recyclerView.paddingTop) <= 1 &&
      !recyclerView.canScrollVertically(-1)
    headerSlotAdapter.update(containerSlots.getOrNull(0), slotHeights[0], true)
    emptySlotAdapter.update(containerSlots.getOrNull(1), slotHeights[1], config?.items?.isEmpty() != false)
    syncDisclosurePresentation()
    syncFooterPresentation()
    updateSidecarGeometry()
    if (headerWasAbsent && headerSlotAdapter.itemCount > 0) {
      initialHeaderInsertionHandled = true
      if (restoreInitialHeaderTop) {
        // RecyclerView otherwise keeps the first body row at the top when this slot arrives late.
        layoutManager.scrollToPositionWithOffset(0, 0)
      }
    }
    layoutManager.spanSizeLookup.invalidateSpanIndexCache()
    recyclerView.requestLayout()
  }

  private fun disclosureInsetExtentPx(): Int =
    ((config?.disclosure?.expandedTrailingInset ?: 0.0) * density)
      .coerceIn(0.0, Int.MAX_VALUE.toDouble()).roundToInt()

  private fun settleDisclosureCommand(status: String) {
    val id = activeDisclosureTransitionId ?: return
    if (disposed || id == settledDisclosureTransitionId ||
      consumedStructuralTransitionId != id || structuralTail != null) return
    settledDisclosureTransitionId = id
    activeDisclosureTransitionId = null
    directDisclosureTogglePositionPending = false
    onStructuralTransitionSettled?.invoke(JSONObject().apply {
      put("transitionId", id)
      put("status", status)
      put("settledExpanded", config?.disclosure?.expanded == true)
    }.toString())
  }

  private fun settleCommittedDirectDisclosure(
    next: NativeListConfig,
    id: String?,
    positionToggle: Boolean,
  ) {
    if (id == null || disposed || config !== next ||
      activeDisclosureTransitionId != id || consumedStructuralTransitionId != id ||
      structuralTail != null || next.disclosure == null) return
    relayoutRecyclerViewImmediately()
    if (positionToggle && directDisclosureTogglePositionPending)
      positionSettledDisclosureToggle()
    settleDisclosureCommand(directDisclosureStatus)
  }

  private fun presentAcceptedDisclosureEndpoint(next: NativeListConfig) {
    val disclosure = next.disclosure ?: return
    disclosureFraction = if (disclosure.expanded) 1f else 0f
    disclosurePresentedContentPx = if (disclosure.expanded) slotHeights[4] else 0
    syncDisclosurePresentation()
  }

  private fun syncDisclosurePresentation() {
    val disclosure = config?.disclosure
    if (disclosure == null) {
      disclosurePresentedContentPx = 0
      disclosurePresentedInsetPx = 0
      expandedContentAdapter.update(null, 0, false)
      structureToggleAdapter.update(null, 0, false)
      disclosureInsetAdapter.update(null, 0, false)
      return
    }
    val content = containerSlots.getOrNull(4)
    if (expandedContentHost.getChildAt(0) !== content) {
      expandedContentHost.removeAllViews()
      if (content != null) {
        (content.parent as? ViewGroup)?.removeView(content)
        expandedContentHost.addView(content, FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
      }
    }
    expandedContentHost.naturalHeightPx = slotHeights[4]
    val fraction = disclosureFraction.coerceIn(0f, 1f)
    val contentHeight = if (disclosureTransitioning) disclosurePresentedContentPx else
      if (disclosure.expanded) slotHeights[4] else 0
    disclosurePresentedContentPx = contentHeight
    disclosurePresentedInsetPx = (disclosureInsetExtentPx() * fraction).roundToInt()
    val interactive = disclosure.expanded && !disclosureTransitioning && fraction >= 1f
    expandedContentHost.importantForAccessibility = if (interactive)
      View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
    else View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    expandedContentAdapter.update(if (content == null) null else expandedContentHost,
      contentHeight, true)
    structureToggleAdapter.update(containerSlots.getOrNull(5), slotHeights[5], true)
    disclosureInsetAdapter.update(disclosureInsetView,
      disclosurePresentedInsetPx, true)
    if (content != null) layoutExpandedContentHostNow()
  }

  private fun layoutExpandedContentHostNow() {
    val wrapper = expandedContentHost
    val child = wrapper.getChildAt(0) ?: return
    val width = wrapper.width.takeIf { it > 0 } ?: wrapper.measuredWidth.takeIf { it > 0 } ?: return
    val height = disclosurePresentedContentPx
    if (!wrapper.isLayoutRequested && wrapper.measuredWidth == width &&
      wrapper.measuredHeight == height && wrapper.height == height &&
      child.left == 0 && child.top == 0 && child.right == width &&
      child.bottom == wrapper.naturalHeightPx) return
    wrapper.forceLayout()
    wrapper.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
    wrapper.layout(wrapper.left, wrapper.top, wrapper.left + width, wrapper.top + height)
  }

  private fun syncFooterPresentation() {
    val footer = containerSlots.getOrNull(2)
    if (footerPresentationHost.getChildAt(0) !== footer) {
      invalidateTailForContainerGeometry()
      structuralFooterCompensatedHeightPx = null
      structuralFooterObservedNativeHeightPx = null
      acceptedFooterNaturalHeightPx = null
      footerPresentationHost.removeAllViews()
      if (footer != null) {
        (footer.parent as? ViewGroup)?.removeView(footer)
        footerPresentationHost.addView(footer, FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
      }
    }
    val naturalHeight = footerNaturalHeightPx()
    val hiddenTop = footerHiddenTopPx()
    if (footerPresentationHost.naturalHeightPx != naturalHeight ||
      footerPresentationHost.hiddenTopPx != hiddenTop) {
      footerPresentationHost.naturalHeightPx = naturalHeight
      footerPresentationHost.hiddenTopPx = hiddenTop
      footerPresentationHost.requestLayout()
    }
    footerSlotAdapter.update(if (footer == null) null else footerPresentationHost,
      naturalHeight - hiddenTop, true)
    if (footer != null) layoutFooterPresentationHostNow()
  }

  private fun layoutFooterPresentationHostNow() {
    val wrapper = footerPresentationHost
    val child = wrapper.getChildAt(0) ?: return
    val width = wrapper.width.takeIf { it > 0 } ?: wrapper.measuredWidth.takeIf { it > 0 } ?: return
    val height = footerPresentedHeightPx()
    val childTop = -wrapper.hiddenTopPx
    val childBottom = wrapper.naturalHeightPx - wrapper.hiddenTopPx
    if (!wrapper.isLayoutRequested && wrapper.measuredWidth == width &&
      wrapper.measuredHeight == height && wrapper.height == height &&
      child.left == 0 && child.top == childTop && child.right == width &&
      child.bottom == childBottom) return
    wrapper.forceLayout()
    wrapper.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
    wrapper.layout(wrapper.left, wrapper.top, wrapper.left + width, wrapper.top + height)
  }

  private fun invalidateTailForContainerGeometry() {
    pendingPreparingFooterFrame = null
    if (structuralTail == null) return
    if (!structuralTailFinalizing) finishStructuralTail(interrupted = true)
    structuralFooterCompensatedHeightPx = null
    structuralTailSignedExtentPx = structuralTailRowsInAdapterPx + structuralSpacerHeightPx
  }

  private fun footerNaturalHeightPx(): Int = if (structuralTail == null)
    acceptedFooterNaturalHeightPx ?: slotHeights[2] else
    structuralFooterCompensatedHeightPx ?: acceptedFooterNaturalHeightPx ?: slotHeights[2]

  private fun footerHiddenTopPx(): Int = if (structuralTail == null) 0 else
    (structuralTailRowsInAdapterPx - structuralTailSignedExtentPx)
      .coerceIn(0, footerNaturalHeightPx())

  private fun footerPresentedHeightPx(): Int = footerNaturalHeightPx() - footerHiddenTopPx()

  private fun scrollPositionForRow(index: Int): Int = index + headerSlotAdapter.itemCount

  private fun recordLaidOutRowExtent(child: View, extentPx: Int) {
    val holder = recyclerView.getChildViewHolder(child) as? NativeListViewHolder ?: return
    if (holder.bindingAdapter !== adapter) return
    val key = adapter.itemAt(holder.bindingAdapterPosition)?.key ?: return
    val previous = measuredRowExtentsPx.put(key, extentPx)
    measuredRowExtentSumPx += extentPx - (previous ?: 0)
  }

  private fun pruneMeasuredRowExtents(items: List<NativeListItem>) {
    if (measuredRowExtentsPx.isEmpty()) return
    val keys = items.mapTo(HashSet(items.size)) { it.key }
    measuredRowExtentsPx.keys.retainAll(keys)
    measuredRowExtentSumPx = measuredRowExtentsPx.values.sumOf { it.toLong() }
  }

  /** Rows skipped by a jump have never been laid out; explicit heights beat the average. */
  private fun estimatedScrollExtentPx(position: Int): Int {
    val headerCount = headerSlotAdapter.itemCount
    if (position < headerCount) return slotHeights[0]
    val row = position - headerCount
    if (row >= adapter.itemCount) {
      val slot = row - adapter.itemCount
      return when {
        slot < emptySlotAdapter.itemCount -> slotHeights[1]
        slot < emptySlotAdapter.itemCount + structuralSpacerAdapter.itemCount -> structuralSpacerHeightPx
        slot < emptySlotAdapter.itemCount + structuralSpacerAdapter.itemCount +
          expandedContentAdapter.itemCount -> disclosurePresentedContentPx
        slot < emptySlotAdapter.itemCount + structuralSpacerAdapter.itemCount +
          expandedContentAdapter.itemCount + structureToggleAdapter.itemCount -> slotHeights[5]
        slot < emptySlotAdapter.itemCount + structuralSpacerAdapter.itemCount +
          expandedContentAdapter.itemCount + structureToggleAdapter.itemCount +
          disclosureInsetAdapter.itemCount ->
          disclosurePresentedInsetPx
        slot < emptySlotAdapter.itemCount + structuralSpacerAdapter.itemCount +
          expandedContentAdapter.itemCount + structureToggleAdapter.itemCount +
          disclosureInsetAdapter.itemCount + footerSlotAdapter.itemCount -> footerPresentedHeightPx()
        else -> sidecarSpacerHeightPx
      }
    }
    val item = adapter.itemAt(row)
    item?.key?.let(measuredRowExtentsPx::get)?.let { return it }
    val explicit = item?.styledHeight ?: item?.json?.optDouble("height")?.takeIf { it.isFinite() }
    if (explicit != null) return (explicit * density).roundToInt()
    return if (measuredRowExtentsPx.isEmpty()) 0 else (measuredRowExtentSumPx / measuredRowExtentsPx.size).toInt()
  }

  private fun estimatedItemExtentPx(item: NativeListItem): Int {
    measuredRowExtentsPx[item.key]?.let { return it }
    val explicit = item.styledHeight ?: item.json.optDouble("height").takeIf { it.isFinite() }
    if (explicit != null) return (explicit * density).roundToInt()
    return if (measuredRowExtentsPx.isEmpty()) dp(56)
      else (measuredRowExtentSumPx / measuredRowExtentsPx.size).toInt()
  }

  private fun setStructuralSpacerHeight(heightPx: Int) {
    val next = heightPx.coerceAtLeast(0)
    if (next == structuralSpacerHeightPx) return
    structuralSpacerHeightPx = next
    structuralSpacerAdapter.update(structuralSpacerView, next, true)
    layoutManager.spanSizeLookup.invalidateSpanIndexCache()
  }

  private fun setStructuralTailExtent(extentPx: Int) {
    structuralTailSignedExtentPx = extentPx
    setStructuralSpacerHeight((extentPx - structuralTailRowsInAdapterPx).coerceAtLeast(0))
    syncFooterPresentation()
  }

  private fun prefixTailCount(old: NativeListConfig, next: NativeListConfig): Int? {
    if (old.orientation != "vertical" || next.orientation != "vertical" ||
      old.layout != next.layout || next.layout !in setOf("linear", "table") ||
      old.itemSpacing != next.itemSpacing ||
      old.contentPadding != next.contentPadding ||
      old.contentPaddingTop != next.contentPaddingTop ||
      old.contentPaddingBottom != next.contentPaddingBottom ||
      old.contentPaddingHorizontal != next.contentPaddingHorizontal ||
      old.theme?.toString() != next.theme?.toString() ||
      old.listStyle?.toString() != next.listStyle?.toString() ||
      old.items.size == next.items.size ||
      old.reorderable || next.reorderable ||
      structuralTailFinalizing || structuralTailRowsInAdapterPx > 0 ||
      old.items.any { it.usesSelectorSourceScale } !=
        next.items.any { it.usesSelectorSourceScale } ||
      (structuralAnimator != null && structuralTail == null) ||
      old.items.any { !it.hasExplicitHeight } ||
      next.items.any { !it.hasExplicitHeight } ||
      old.items.any { it.type !in setOf("identity", "dataRow") } ||
      next.items.any { it.type !in setOf("identity", "dataRow") } ||
      old.items.any { it.type == "sectionHeader" } ||
      next.items.any { it.type == "sectionHeader" }) return null
    val prefix = minOf(old.items.size, next.items.size)
    if (prefix == 0) return null
    if (structuralTail != null && prefix != structuralTail?.prefixCount) return null
    for (index in 0 until prefix) {
      val before = old.items[index]
      val after = next.items[index]
      if (before.key != after.key || before.rendererKey != after.rendererKey ||
        explicitTailRowHeight(before) != explicitTailRowHeight(after) ||
        explicitTailRowSpacingPx(before, old.itemSpacing) !=
          explicitTailRowSpacingPx(after, next.itemSpacing)) return null
    }
    return prefix
  }

  private fun explicitTailRowHeight(item: NativeListItem): Int =
    item.styledHeight?.let { (it * density).roundToInt() } ?: run {
      val value = item.json.optDouble("height")
      when (item.json.optString("heightRounding")) {
        "floor" -> (value * density).toInt()
        "nearest" -> (value * density).roundToInt()
        else -> if (usesSelectorSourceScale || item.usesSelectorSourceScale)
          (item.json.optInt("height") * density).roundToInt()
        else NativeListScale.dp(resources, item.json.optInt("height"))
      }
    }

  private fun explicitTailRowSpacingPx(item: NativeListItem, spacing: Int): Int {
    if (item.type == "identity" &&
      item.json.optString("presentation") == "walletSidebar") {
      val height = item.styledHeight ?: item.json.optDouble("height")
      return ((height + spacing) * density).roundToInt() -
        (height * density).roundToInt()
    }
    return dp(spacing)
  }

  private fun exceedsStructuralTailHostLimit(
    old: NativeListConfig,
    next: NativeListConfig,
    prefixCount: Int,
  ): Boolean {
    val items = (if (old.items.size > next.items.size) old.items else next.items)
      .drop(prefixCount)
    if (items.size <= 128) return false
    val starts = LongArray(items.size)
    val heights = IntArray(items.size)
    var cursor = 0L
    items.forEachIndexed { index, item ->
      starts[index] = cursor
      heights[index] = explicitTailRowHeight(item)
      cursor += heights[index] + explicitTailRowSpacingPx(item, next.itemSpacing)
    }
    val window = 3L * recyclerView.height
    return (0..(items.size - 129)).any { first ->
      starts[first + 128] - (starts[first] + heights[first]) <= window
    }
  }

  private fun clearStructuralTail() {
    pendingPreparingFooterFrame = null
    structuralTail?.hosts?.values?.forEach { host ->
      structuralTailLayer.removeView(host)
      host.recycle()
      host.dispose()
    }
    structuralTail = null
    structuralTailFinalizing = false
    structuralTailPreparing = false
    structuralFooterCompensatedHeightPx = null
    structuralTailRowsInAdapterPx = 0
    setStructuralTailExtent(0)
    structuralTailLayer.removeAllViews()
    structuralTailLayer.visibility = GONE
    structuralTailLayer.layoutParams?.let { params ->
      params.width = 0
      params.height = 0
      structuralTailLayer.layoutParams = params
    }
    structuralTailLayer.forceLayout()
    structuralTailLayer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY))
    structuralTailLayer.layout(0, 0, 0, 0)
  }

  private fun structuralTailRowOrSlotExtentPx(position: Int): Int {
    val row = position - headerSlotAdapter.itemCount
    val item = adapter.itemAt(row)
    if (row < 0 || item == null || !item.hasExplicitHeight) return estimatedScrollExtentPx(position)
    return explicitTailRowHeight(item) +
      explicitTailRowSpacingPx(item, config?.itemSpacing ?: 0)
  }

  private fun structuralTailMeasuredScrollDistancePx(): Int? {
    if (layoutManager.orientation != RecyclerView.VERTICAL) return null
    val first = layoutManager.findFirstVisibleItemPosition()
    if (first == RecyclerView.NO_POSITION) return null
    val view = layoutManager.findViewByPosition(first) ?: return null
    val prefix = nativeListScrollPrefixPx(first, layoutManager.spanCount,
      layoutManager.spanSizeLookup::getSpanSize, ::structuralTailRowOrSlotExtentPx)
    val topMargin = (view.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
    val top = layoutManager.getDecoratedTop(view) - topMargin
    return (prefix + recyclerView.paddingTop - top).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
  }

  private fun tailScrollExtentPx(): Int =
    (if (structuralTail != null) structuralTailMeasuredScrollDistancePx()
    else measuredScrollDistancePx()) ?: logicalScrollDistancePx

  private fun updateStructuralTailWindow() {
    val tail = structuralTail ?: return
    if (structuralTailFinalizing || structuralTailRowsInAdapterPx > 0) return
    val scroll = tailScrollExtentPx()
    val viewport = recyclerView.height
    if (viewport <= 0) return
    val y = recyclerView.top + recyclerView.paddingTop + structuralTailStartContentY - scroll
    val width = (recyclerView.width - recyclerView.paddingLeft - recyclerView.paddingRight).coerceAtLeast(0)
    val params = structuralTailLayer.layoutParams as FrameLayout.LayoutParams
    val left = recyclerView.left + recyclerView.paddingLeft
    if (params.width != width || params.height != structuralSpacerHeightPx ||
      params.leftMargin != left || params.topMargin != y) {
      params.width = width
      params.height = structuralSpacerHeightPx
      params.leftMargin = left
      params.topMargin = y
      structuralTailLayer.layoutParams = params
    }
    structuralTailLayer.visibility = VISIBLE
    val low = (-y - viewport).coerceAtLeast(0)
    val high = minOf(structuralSpacerHeightPx, -y + 2 * viewport)
    var first = 0
    var end = tail.items.size
    while (first < end) {
      val middle = (first + end) / 2
      if (tail.starts[middle] + tail.heights[middle] < low) first = middle + 1
      else end = middle
    }
    var last = first
    end = tail.items.size
    while (last < end) {
      val middle = (last + end) / 2
      if (tail.starts[middle] <= high) last = middle + 1
      else end = middle
    }
    if (last - first > 128) {
      finishStructuralTail(interrupted = false)
      return
    }
    tail.hosts.keys.filter { it < first || it >= last }.forEach { index ->
      val host = tail.hosts.remove(index) ?: return@forEach
      structuralTailLayer.removeView(host)
      host.recycle()
      host.dispose()
    }
    for (index in first until last) {
      if (tail.hosts.containsKey(index)) continue
      val item = tail.items[index]
      val host = NativeListRendererRegistry.create(reactContext, item.rendererKey.ordinal, fonts)
      host.listStyle = config?.listStyle
      host.bind(item, config?.theme, config?.layout ?: "linear", "vertical",
        tail.prefixCount + index, item.json.optBoolean("selected") ||
          (config?.selectedKeys?.contains(item.key) == true), adapter.checkboxState,
        useSourceScale = usesSelectorSourceScale)
      host.isClickable = false
      host.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
      structuralTailLayer.addView(host, FrameLayout.LayoutParams(width, tail.heights[index],
        Gravity.TOP or Gravity.LEFT).apply { topMargin = tail.starts[index] })
      tail.hosts[index] = host
      tail.peakHosts = maxOf(tail.peakHosts, tail.hosts.size)
    }
    val height = structuralSpacerHeightPx
    val needsMeasure = structuralTailLayer.isLayoutRequested ||
      structuralTailLayer.measuredWidth != width ||
      structuralTailLayer.measuredHeight != height ||
      tail.hosts.values.any { it.isLayoutRequested }
    if (needsMeasure) {
      structuralTailLayer.measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
      )
    }
    if (needsMeasure || structuralTailLayer.left != left || structuralTailLayer.top != y ||
      structuralTailLayer.right != left + width || structuralTailLayer.bottom != y + height) {
      structuralTailLayer.layout(left, y, left + width, y + height)
    }
  }

  private fun updateStructuralTailTarget() {
    val tail = structuralTail ?: return
    val disclosure = config?.disclosure
    val finalChrome = if (disclosure == null) 0 else
      slotHeights[5] + if (disclosure.expanded) slotHeights[4] + disclosureInsetExtentPx() else 0
    val leftHeight = recyclerView.paddingTop + structuralTailStartContentY + tail.finalExtent +
      finalChrome + footerNaturalHeightPx() + recyclerView.paddingBottom
    val sidecarHeight = if (sidecarLayout == null) 0 else
      recyclerView.paddingTop + slotHeights[3] + recyclerView.paddingBottom
    val maximum = (maxOf(leftHeight, sidecarHeight) - recyclerView.height).coerceAtLeast(0)
    var target = structuralTailStartOffset.coerceIn(0, maximum)
    if (tail.finalExtent == 0 && disclosure != null && slotHeights[5] > 0) {
      val toggleTop = recyclerView.paddingTop + structuralTailStartContentY
      val toggleBottom = toggleTop + slotHeights[5]
      val sticky = stickyHeaderHost.height
      val lower = toggleBottom - recyclerView.height + recyclerView.paddingBottom
      val upper = toggleTop - recyclerView.paddingTop - sticky
      target = if (lower <= upper) target.coerceIn(lower, upper) else upper
    } else if (tail.finalExtent == 0 && footerNaturalHeightPx() > 0) {
      val footerTop = recyclerView.paddingTop + structuralTailStartContentY
      val footerBottom = footerTop + footerNaturalHeightPx()
      val sticky = stickyHeaderHost.height
      val lower = footerBottom - recyclerView.height + recyclerView.paddingBottom
      val upper = footerTop - recyclerView.paddingTop - sticky
      target = if (lower <= upper) target.coerceIn(lower, upper) else upper
    }
    structuralTailTargetOffset = target.coerceIn(0, maximum)
  }

  private fun positionSettledDisclosureToggle() {
    if (recyclerView.width <= 0 || recyclerView.height <= 0 ||
      structureToggleAdapter.itemCount == 0 || slotHeights[5] <= 0 ||
      recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE ||
      pendingScrollRequest != null || acceptedScrollRequest != null ||
      executingScrollCommand) return
    val togglePosition = headerSlotAdapter.itemCount + adapter.itemCount +
      emptySlotAdapter.itemCount + structuralSpacerAdapter.itemCount +
      expandedContentAdapter.itemCount
    val toggleTop = (recyclerView.paddingTop.toLong() +
      nativeListScrollPrefixPx(togglePosition, layoutManager.spanCount,
        layoutManager.spanSizeLookup::getSpanSize, ::structuralTailRowOrSlotExtentPx))
      .coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
    val stickyTop = recyclerView.paddingTop + stickyHeaderHost.height
    val lower = toggleTop + slotHeights[5] - recyclerView.height + recyclerView.paddingBottom
    val upper = toggleTop - stickyTop
    val sidecarBottom = if (sidecarLayout == null) 0 else
      recyclerView.paddingTop + slotHeights[3] + recyclerView.paddingBottom
    val leftExtent = (nativeListScrollPrefixPx(
      scrollContentAdapter.itemCount - sidecarSpacerAdapter.itemCount,
      layoutManager.spanCount, layoutManager.spanSizeLookup::getSpanSize,
      ::structuralTailRowOrSlotExtentPx) +
      recyclerView.paddingTop + recyclerView.paddingBottom)
      .coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
    val maximum = (maxOf(leftExtent, sidecarBottom) - recyclerView.height).coerceAtLeast(0)
    val current = structuralTailMeasuredScrollDistancePx() ?: tailScrollExtentPx()
    val target = (if (lower <= upper) current.coerceIn(lower, upper) else upper)
      .coerceIn(0, maximum)
    val ownsCommand = !structuralTailCommandActive
    if (ownsCommand) {
      notifyScrollCommand(false, 0)
      notifyScrollCommand(false, 1)
    }
    try {
      recyclerView.scrollBy(0, target - current)
      relayoutRecyclerViewImmediately()
    } finally {
      if (ownsCommand) notifyScrollCommand(false, 2)
    }
  }

  private fun finishStructuralTail(interrupted: Boolean) {
    val next = config ?: return
    if (structuralTail == null || structuralTailFinalizing) return
    structuralTailFinalizing = true
    val epoch = ++structuralTransitionEpoch
    pendingPreparingFooterFrame = pendingPreparingFooterFrame?.takeIf {
      it.footer === containerSlots.getOrNull(2)
    }?.copy(epoch = epoch, handoffOnly = true)
    structuralAnimator?.cancel()
    structuralAnimator = null
    recyclerView.itemAnimator = null
    structuralFooterHoldTop = null
    structuralFooterVisualStartTop = null
    structuralFooterVisualEndTop = null
    footerSlotAdapter.setVisualTranslationY(0f)
    if (interrupted && structuralTailCommandActive) {
      structuralTailCommandActive = false
      notifyScrollCommand(false, 2)
    }
    adapter.submitList(next.items) {
      if (epoch != structuralTransitionEpoch || disposed) return@submitList
      fun handoff() {
        if (epoch != structuralTransitionEpoch || disposed) return
        if (recyclerView.isComputingLayout) {
          recyclerView.post { handoff() }
          return
        }
        val acceptedPendingFooter = acceptPendingFooterValues(adjustTailStart = false)
        presentAcceptedDisclosureEndpoint(next)
        if (recyclerView.width <= 0 || recyclerView.height <= 0) {
          clearStructuralTail()
          if (acceptedPendingFooter) updateContainerSlots()
          if (structuralTailCommandActive) {
            structuralTailCommandActive = false
            notifyScrollCommand(false, 2)
          }
          if (next.disclosure != null) {
            disclosureTransitioning = false
            syncDisclosurePresentation()
            settleDisclosureCommand(if (interrupted) "interrupted" else "completed")
          }
          scheduleVisibleEvent()
          checkEndReached()
          return
        }
        structuralTailRowsInAdapterPx = 0
        setStructuralTailExtent(0)
        updateSidecarSpacerNow()
        relayoutRecyclerViewImmediately()
        if (epoch != structuralTransitionEpoch || disposed) return
        if (!interrupted && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
          val sidecarBottom = if (sidecarLayout == null) 0 else
            recyclerView.paddingTop + slotHeights[3] + recyclerView.paddingBottom
          val maximum = (maxOf(sidecarLeftExtentPx(), sidecarBottom) -
            recyclerView.height).coerceAtLeast(0)
          val target = structuralTailTargetOffset.coerceIn(0, maximum)
          recyclerView.scrollBy(0, target - tailScrollExtentPx())
        }
        if (epoch != structuralTransitionEpoch || disposed) return
        clearStructuralTail()
        if (acceptedPendingFooter) updateContainerSlots()
        performPendingScrollIfNeeded()
        if (structuralTailCommandActive) {
          structuralTailCommandActive = false
          notifyScrollCommand(false, 2)
        }
        if (next.disclosure != null) {
          disclosureTransitioning = false
          syncDisclosurePresentation()
          settleDisclosureCommand(if (interrupted) "interrupted" else "completed")
        }
        scheduleVisibleEvent()
        checkEndReached()
      }
      handoff()
    }
  }

  private fun discardStructuralTail() {
    if (structuralTail == null) return
    structuralTransitionEpoch += 1
    structuralAnimator?.cancel()
    structuralAnimator = null
    if (structuralTailCommandActive) {
      structuralTailCommandActive = false
      notifyScrollCommand(false, 2)
    }
    clearStructuralTail()
    setStructuralSpacerHeight(0)
  }

  private fun holdStructuralTailUntilListCommit(): Int {
    val epoch = ++structuralTransitionEpoch
    pendingPreparingFooterFrame = pendingPreparingFooterFrame?.takeIf {
      it.footer === containerSlots.getOrNull(2)
    }?.copy(epoch = epoch, handoffOnly = true)
    structuralAnimator?.cancel()
    structuralAnimator = null
    structuralTailFinalizing = true
    if (structuralTailCommandActive) {
      structuralTailCommandActive = false
      notifyScrollCommand(false, 2)
    }
    return epoch
  }

  private fun completeHeldTailCommit(
    next: NativeListConfig,
    epoch: Int,
    completion: () -> Unit,
    positionToggle: Boolean,
  ) {
    if (epoch != structuralTransitionEpoch || disposed) return
    if (recyclerView.isComputingLayout) {
      recyclerView.post { completeHeldTailCommit(next, epoch, completion, positionToggle) }
      return
    }
    val acceptedPendingFooter = acceptPendingFooterValues(adjustTailStart = false)
    presentAcceptedDisclosureEndpoint(next)
    structuralTailRowsInAdapterPx = 0
    setStructuralTailExtent(0)
    if (acceptedPendingFooter) updateContainerSlots()
    updateSidecarSpacerNow()
    relayoutRecyclerViewImmediately()
    if (positionToggle) positionSettledDisclosureToggle()
    if (epoch != structuralTransitionEpoch || disposed) return
    clearStructuralTail()
    disclosureTransitioning = false
    syncDisclosurePresentation()
    completion()
  }

  private fun applyStructuralTailFrame(tail: StructuralTail, epoch: Int, progress: Float): Boolean {
    if (epoch != structuralTransitionEpoch || disposed || structuralTail !== tail ||
      structuralTailFinalizing) return true
    if (recyclerView.isComputingLayout) return false
    val segmentProgress = if (structuralTailStartProgress >= 1f) 1f else
      ((progress - structuralTailStartProgress) /
        (1f - structuralTailStartProgress)).coerceIn(0f, 1f)
    config?.disclosure?.let { disclosure ->
      val target = if (disclosure.expanded) 1f else 0f
      disclosureFraction = disclosureStartFraction +
        (target - disclosureStartFraction) * segmentProgress
      val contentProgress = if (disclosureContentStartProgress >= 1f) 1f else
        ((progress - disclosureContentStartProgress) /
          (1f - disclosureContentStartProgress)).coerceIn(0f, 1f)
      val contentTarget = if (disclosure.expanded) slotHeights[4] else 0
      disclosurePresentedContentPx = (disclosureContentStartPx +
        (contentTarget - disclosureContentStartPx) * contentProgress).roundToInt()
      syncDisclosurePresentation()
    }
    setStructuralTailExtent((structuralTailStartExtentPx +
      (tail.finalExtent - structuralTailStartExtentPx) * segmentProgress).roundToInt())
    updateSidecarSpacerNow()
    relayoutRecyclerViewImmediately()
    if (epoch != structuralTransitionEpoch || disposed || structuralTail !== tail) return true
    if (!structuralScrollInterrupted && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
      val sidecarBottom = if (sidecarLayout == null) 0 else
        recyclerView.paddingTop + slotHeights[3] + recyclerView.paddingBottom
      val maximum = (maxOf(sidecarLeftExtentPx(), sidecarBottom) -
        recyclerView.height).coerceAtLeast(0)
      val desired = (structuralTailStartOffset +
        (structuralTailTargetOffset - structuralTailStartOffset) * segmentProgress)
        .roundToInt().coerceIn(0, maximum)
      val requestedScroll = desired - tailScrollExtentPx()
      recyclerView.scrollBy(0, requestedScroll)
    }
    updateStructuralTailWindow()
    return true
  }

  private fun commitPreparingFooterFrame(): Boolean {
    val pending = pendingPreparingFooterFrame ?: return true
    if (structuralTail == null || structuralTailRowsInAdapterPx <= 0 ||
      pending.epoch != structuralTransitionEpoch || disposed ||
      pending.footer !== containerSlots.getOrNull(2)) {
      if (pendingPreparingFooterFrame === pending) pendingPreparingFooterFrame = null
      return true
    }
    if (pending.handoffOnly || recyclerView.isComputingLayout) return false
    pendingPreparingFooterFrame = null
    if (pending.observedNativeHeight != null) {
      structuralFooterObservedNativeHeightPx = pending.observedNativeHeight
    } else {
      containerSlots.getOrNull(2)?.measuredHeight
        ?.takeIf { it > 0 && it != pending.naturalHeight }
        ?.let { structuralFooterObservedNativeHeightPx = it }
    }
    reconcileStructuralFooterHeight(pending.naturalHeight)
    acceptedFooterNaturalHeightPx = pending.naturalHeight
    for (index in 0..5) slotHeights[index] = pending.heights[index]
    updateContainerSlots()
    updateStructuralTailTarget()
    updateSidecarGeometry()
    updateSidecarSpacerNow()
    relayoutRecyclerViewImmediately()
    if (pending.epoch != structuralTransitionEpoch || disposed ||
      pending.footer !== containerSlots.getOrNull(2)) {
      return pendingPreparingFooterFrame == null
    }
    val sidecarBottom = if (sidecarLayout == null) 0 else
      recyclerView.paddingTop + slotHeights[3] + recyclerView.paddingBottom
    val maximum = (maxOf(sidecarLeftExtentPx(), sidecarBottom) -
      recyclerView.height).coerceAtLeast(0)
    val desired = pending.ownerOffset.coerceIn(0, maximum)
    recyclerView.scrollBy(0, desired - tailScrollExtentPx())
    relayoutRecyclerViewImmediately()
    updateStructuralTailWindow()
    return pendingPreparingFooterFrame == null
  }

  private fun acceptPendingFooterValues(adjustTailStart: Boolean): Boolean {
    val pending = pendingPreparingFooterFrame ?: return false
    if (pending.footer !== containerSlots.getOrNull(2) || disposed ||
      pending.epoch != structuralTransitionEpoch) {
      pendingPreparingFooterFrame = null
      return false
    }
    val latestNativeHeight = if (pending.observedNativeHeight != null)
      pending.footer?.measuredHeight?.takeIf { it > 0 } else null
    val naturalHeight = if (latestNativeHeight != null &&
      latestNativeHeight != pending.observedNativeHeight) {
      (ceil(latestNativeHeight / density.toDouble()) * density).roundToInt()
    } else pending.naturalHeight
    if (adjustTailStart) {
      val previousHeight = structuralFooterCompensatedHeightPx ?:
        acceptedFooterNaturalHeightPx ?: slotHeights[2]
      val delta = previousHeight - naturalHeight
      structuralTailSignedExtentPx += delta
      structuralTailStartExtentPx += delta
    }
    structuralFooterObservedNativeHeightPx = latestNativeHeight ?:
      containerSlots.getOrNull(2)?.measuredHeight?.takeIf {
        it > 0 && it != naturalHeight
      }
    acceptedFooterNaturalHeightPx = naturalHeight
    structuralFooterCompensatedHeightPx = naturalHeight
    for (index in 0..5) slotHeights[index] = pending.heights[index]
    pendingPreparingFooterFrame = null
    return true
  }

  private fun startPrefixTailTransition(
    previous: NativeListConfig,
    next: NativeListConfig,
    transition: NativeListConfig.StructuralTransition,
    prefixCount: Int,
  ) {
    val previousTail = structuralTail
    val carriedExtent = previousTail?.let { structuralTailSignedExtentPx }
    val startOffset = structuralTailMeasuredScrollDistancePx() ?: tailScrollExtentPx()
    if (previousTail == null) {
      cancelStructuralTransition()
    } else {
      structuralTransitionEpoch += 1
      structuralAnimator?.cancel()
      structuralAnimator = null
      if (structuralTailCommandActive) {
        structuralTailCommandActive = false
        notifyScrollCommand(false, 2)
      }
      // The old presentation remains visible until the new prefix and spacer commit together.
      structuralTailPreparing = true
    }
    val epoch = structuralTransitionEpoch
    val collapsing = previous.items.size > next.items.size
    val items = (if (collapsing) previous.items else next.items)
      .drop(prefixCount).toMutableList()
    val heights = IntArray(items.size) { explicitTailRowHeight(items[it]) }
    val spacings = IntArray(items.size) {
      explicitTailRowSpacingPx(items[it], next.itemSpacing)
    }
    val tail = StructuralTail(items, heights, spacings, prefixCount,
      if (collapsing) 0 else heights.sum() + spacings.sum())
    val startExtent = carriedExtent ?: if (collapsing) tail.extent else 0
    disclosureStartFraction = disclosureFraction
    if (next.disclosure != null) {
      disclosureContentStartPx = disclosurePresentedContentPx
      disclosureContentStartProgress = 0f
      disclosureTransitioning = true
    }
    val startContentY = (if (headerSlotAdapter.itemCount > 0) slotHeights[0] else 0) +
      next.items.take(prefixCount).sumOf {
        explicitTailRowHeight(it) + explicitTailRowSpacingPx(it, next.itemSpacing)
      }
    if (previousTail == null) {
      structuralTail = tail
      structuralTailRowsInAdapterPx = if (collapsing) tail.extent else 0
      structuralTailStartExtentPx = startExtent
      structuralTailStartProgress = 0f
      structuralTailStartContentY = startContentY
      structuralTailStartOffset = startOffset
      structuralTailTargetOffset = startOffset
      setStructuralTailExtent(startExtent)
    }
    structuralScrollInterrupted = false
    structuralFooterCompensatedHeightPx = null
    recyclerView.itemAnimator = null
    acceptRowIndex(next, previous)
    config = next
    pruneMeasuredRowExtents(next.items)
    updateContainerSlots()
    usesSelectorSourceScale = next.items.any { it.usesSelectorSourceScale }
    adapter.usesSelectorSourceScale = usesSelectorSourceScale
    adapter.theme = next.theme
    adapter.listStyle = next.listStyle
    adapter.layout = next.layout
    adapter.orientation = next.orientation
    adapter.selectedKeys = next.selectedKeys
    if (previous.generation != next.generation) endReachedGeneration = null
    configureSectionIndex(next)
    updateLayout(next)
    bindFooter(next)
    updateReordering(next)
    refreshLayout.isEnabled = next.pullToRefresh
    refreshLayout.setDistanceToTriggerSync(
      next.refreshTriggerDistance?.let { (it * density).roundToInt() }
        ?: defaultRefreshTriggerDistancePx,
    )
    refreshLayout.isRefreshing = next.refreshing
    structuralTailCommandActive = true
    notifyScrollCommand(false, 0)
    notifyScrollCommand(false, 1)
    adapter.submitList(next.items.take(prefixCount)) {
      fun commitPrefix() {
        if (epoch != structuralTransitionEpoch || disposed ||
          structuralTail !== (previousTail ?: tail) || structuralTailFinalizing) return
        if (recyclerView.isComputingLayout) {
          recyclerView.post { commitPrefix() }
          return
        }
        if (previousTail != null) {
          clearStructuralTail()
          structuralTail = tail
          structuralTailStartExtentPx = startExtent
          structuralTailStartProgress = 0f
          structuralTailStartContentY = startContentY
          structuralTailStartOffset = startOffset
          structuralTailTargetOffset = startOffset
        }
        acceptPendingFooterValues(adjustTailStart = true)
        structuralTailPreparing = false
        structuralTailRowsInAdapterPx = 0
        setStructuralTailExtent(structuralTailStartExtentPx)
        updateStructuralTailTarget()
        if (recyclerView.width <= 0 || recyclerView.height <= 0) {
          finishStructuralTail(interrupted = false)
          return
        }
        applyStructuralTailFrame(tail, epoch, 0f)
        if (epoch != structuralTransitionEpoch || disposed || structuralTail !== tail ||
          structuralTailFinalizing) return
        var lastAppliedProgress = 0f
        var frameScheduled = false
        fun applyProgress(progress: Float) {
          if (epoch != structuralTransitionEpoch || disposed || structuralTail !== tail ||
            structuralTailFinalizing) return
          val nextProgress = maxOf(progress, lastAppliedProgress)
          if (!applyStructuralTailFrame(tail, epoch, nextProgress)) {
            if (!frameScheduled) {
              frameScheduled = true
              recyclerView.post {
                frameScheduled = false
                val current = structuralAnimator?.animatedValue as? Float ?: nextProgress
                applyProgress(current)
              }
            }
            return
          }
          lastAppliedProgress = nextProgress
        }
        structuralAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
          duration = transition.durationMs
          interpolator = android.view.animation.AccelerateDecelerateInterpolator()
          addUpdateListener { animation ->
            if (epoch != structuralTransitionEpoch || disposed || structuralTail !== tail) return@addUpdateListener
            if (Build.VERSION.SDK_INT >= 26 && !ValueAnimator.areAnimatorsEnabled()) {
              finishStructuralTail(interrupted = false)
              return@addUpdateListener
            }
            applyProgress(animation.animatedValue as Float)
          }
          addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
              if (epoch == structuralTransitionEpoch && structuralTail === tail)
                finishStructuralTail(interrupted = structuralScrollInterrupted)
            }
          })
          start()
        }
      }
      commitPrefix()
    }
  }

  private fun updatePrefixTailSnapshot(
    previous: NativeListConfig,
    next: NativeListConfig,
    acceptIndex: Boolean = false,
  ): Boolean {
    val tail = structuralTail ?: return false
    if (structuralTailPreparing || structuralTailFinalizing ||
      structuralTailRowsInAdapterPx > 0 ||
      previous.items.size != next.items.size ||
      previous.disclosure != next.disclosure ||
      previous.layout != next.layout || previous.orientation != next.orientation ||
      previous.theme?.toString() != next.theme?.toString() ||
      previous.listStyle?.toString() != next.listStyle?.toString() ||
      previous.itemSpacing != next.itemSpacing ||
      previous.contentPadding != next.contentPadding ||
      previous.contentPaddingTop != next.contentPaddingTop ||
      previous.contentPaddingBottom != next.contentPaddingBottom ||
      previous.contentPaddingHorizontal != next.contentPaddingHorizontal) return false
    for (index in next.items.indices) {
      val before = previous.items[index]
      val after = next.items[index]
      if (before.key != after.key || before.rendererKey != after.rendererKey ||
        !after.hasExplicitHeight || explicitTailRowHeight(before) != explicitTailRowHeight(after) ||
        explicitTailRowSpacingPx(before, previous.itemSpacing) !=
          explicitTailRowSpacingPx(after, next.itemSpacing))
        return false
    }
    if (acceptIndex) acceptRowIndex(next, previous)
    config = next
    adapter.selectedKeys = next.selectedKeys
    adapter.submitList(next.items.take(tail.prefixCount)) {
      if (structuralTail !== tail || disposed) return@submitList
      scheduleVisibleEvent()
    }
    if (tail.finalExtent > 0) {
      for (index in tail.items.indices) {
        val item = next.items[tail.prefixCount + index]
        if (tail.items[index].content == item.content) continue
        tail.items[index] = item
        tail.hosts[index]?.let { host ->
          host.bind(item, next.theme, next.layout, "vertical", tail.prefixCount + index,
            item.json.optBoolean("selected") || next.selectedKeys.contains(item.key),
            adapter.checkboxState, useSourceScale = usesSelectorSourceScale)
          host.isClickable = false
        }
      }
    }
    bindFooter(next)
    updateStructuralTailTarget()
    return true
  }

  private fun updateStructuralFooterHoldPosition() {
    val targetTop = structuralFooterHoldTop ?: return
    if (footerSlotAdapter.itemCount == 0) return
    val footerPosition = scrollContentAdapter.itemCount - 1 - sidecarSpacerAdapter.itemCount
    layoutManager.findViewByPosition(footerPosition)?.let { footer ->
      footerSlotAdapter.setVisualTranslationY((targetTop - layoutManager.getDecoratedTop(footer)).toFloat())
    }
  }

  private fun updateStructuralFooterVisualPosition() {
    val startTop = structuralFooterVisualStartTop ?: return
    val endTop = structuralFooterVisualEndTop ?: return
    if (footerSlotAdapter.itemCount == 0) return
    val footerPosition = scrollContentAdapter.itemCount - 1 - sidecarSpacerAdapter.itemCount
    layoutManager.findViewByPosition(footerPosition)?.let { footer ->
      val desiredTop = startTop + (endTop - startTop) * structuralFooterVisualProgress
      footerSlotAdapter.setVisualTranslationY(desiredTop - layoutManager.getDecoratedTop(footer))
    }
  }

  private fun cancelStructuralTransition() {
    if (structuralTail != null) discardStructuralTail()
    structuralTransitionEpoch += 1
    pendingStructuralTransition = null
    pendingStructuralStartScheduled = false
    structuralAnimator?.cancel()
    structuralAnimator = null
    recyclerView.itemAnimator = null
    setStructuralSpacerHeight(0)
    footerSlotAdapter.setVisualTranslationY(0f)
    structuralFooterHoldTop = null
    structuralFooterVisualStartTop = null
    structuralFooterVisualEndTop = null
    structuralFooterVisualProgress = 0f
    structuralFooterCompensatedHeightPx = null
    structuralStartSpacerPx = 0
  }

  private fun startPendingStructuralTransition() {
    val pending = pendingStructuralTransition ?: return
    if (pending.epoch != structuralTransitionEpoch || disposed || pendingStructuralStartScheduled) return
    val expected = config?.items?.map { it.key } ?: return
    if (adapter.currentList.map { it.key } != expected) return
    pendingStructuralStartScheduled = true
    recyclerView.postOnAnimation {
      if (pending.epoch != structuralTransitionEpoch) return@postOnAnimation
      pendingStructuralStartScheduled = false
      val latest = pendingStructuralTransition
      if (latest?.epoch == pending.epoch && !disposed) {
        if (recyclerView.isComputingLayout) {
          startPendingStructuralTransition()
          return@postOnAnimation
        }
        alignPendingStructuralAnchor(latest)
        if (latest.anchorTop != null || (latest.collapsing && latest.footerStartTop != null)) {
          relayoutRecyclerViewImmediately()
        }
        pendingStructuralTransition = null
        startStructuralTransition(
          latest.transition, latest.epoch, latest.anchorKey, latest.anchorTop,
          latest.footerStartTop,
        )
      }
    }
  }

  private fun alignPendingStructuralFooter(pending: PendingStructuralTransition): Boolean {
    val top = pending.footerStartTop ?: return false
    if (!pending.collapsing || !pending.spacerInserted || footerSlotAdapter.itemCount == 0 || structuralScrollInterrupted) return false
    if (pending.footerAnchorAppliedTop == top) return false
    val footerPosition = scrollContentAdapter.itemCount - 1 - sidecarSpacerAdapter.itemCount
    layoutManager.scrollToPositionWithOffset(footerPosition, top - recyclerView.paddingTop)
    pendingStructuralTransition = pending.copy(footerAnchorAppliedTop = top)
    return true
  }

  private fun alignPendingStructuralRow(pending: PendingStructuralTransition): Boolean {
    val key = pending.anchorKey ?: return false
    val top = pending.anchorTop ?: return false
    if (structuralScrollInterrupted || pending.rowAnchorAppliedTop == top) return false
    val desiredItems = config?.items ?: return false
    val rowIndex = desiredItems.indexOfFirst { it.key == key }
    if (rowIndex < 0 || adapter.itemCount != desiredItems.size ||
      adapter.currentList.getOrNull(rowIndex)?.key != key) return false
    layoutManager.scrollToPositionWithOffset(scrollPositionForRow(rowIndex), top - recyclerView.paddingTop)
    pendingStructuralTransition = pending.copy(rowAnchorAppliedTop = top)
    return true
  }

  private fun alignPendingStructuralAnchor(pending: PendingStructuralTransition) {
    if (pending.anchorTop != null) alignPendingStructuralRow(pending)
    else alignPendingStructuralFooter(pending)
  }

  private fun measuredScrollDistancePx(): Int? {
    if (layoutManager.orientation != RecyclerView.VERTICAL) return null
    val first = layoutManager.findFirstVisibleItemPosition()
    if (first == RecyclerView.NO_POSITION) return null
    val view = layoutManager.findViewByPosition(first) ?: return null
    val prefix = nativeListScrollPrefixPx(
      first,
      layoutManager.spanCount,
      layoutManager.spanSizeLookup::getSpanSize,
      ::estimatedScrollExtentPx,
    )
    val topMargin = (view.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
    val top = layoutManager.getDecoratedTop(view) - topMargin
    return (prefix + recyclerView.paddingTop - top).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
  }
  private fun rowPositionForScroll(index: Int): Int =
    if (index == RecyclerView.NO_POSITION) RecyclerView.NO_POSITION else index - headerSlotAdapter.itemCount

  private val layoutManager = NativeListGridLayoutManager(context, 1)
  // OneKey patch: vertical lists retain vertical drags and leave horizontal drags to a parent pager.
  private val pagerGestureTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private var pagerGestureIsVertical = false
  private var pagerNativeGestureStarted = false
  private val pagerGestureTouchListener = object : RecyclerView.SimpleOnItemTouchListener() {
    private var downX = 0f
    private var downY = 0f
    private var directionResolved = false

    override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {
      if (layoutManager.orientation != RecyclerView.VERTICAL) return false
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          downX = event.x
          downY = event.y
          directionResolved = false
          pagerGestureIsVertical = false
          pagerNativeGestureStarted = false
        }
        MotionEvent.ACTION_MOVE -> {
          val deltaX = abs(event.x - downX)
          val deltaY = abs(event.y - downY)
          if (!directionResolved && max(deltaX, deltaY) > pagerGestureTouchSlop) {
            directionResolved = true
            pagerGestureIsVertical = deltaY >= deltaX
            recyclerView.parent?.requestDisallowInterceptTouchEvent(pagerGestureIsVertical)
          } else if (!pagerGestureIsVertical && deltaY > pagerGestureTouchSlop && deltaY >= deltaX) {
            pagerGestureIsVertical = true
            recyclerView.parent?.requestDisallowInterceptTouchEvent(true)
          }
          if (pagerGestureIsVertical && !pagerNativeGestureStarted) {
            NativeGestureUtil.notifyNativeGestureStarted(this@NativeListView, event)
            pagerNativeGestureStarted = true
            if (keyboardDismissMode == NativeListKeyboardDismissMode.ON_DRAG) {
              dismissKeyboard()
            }
          }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          finishPagerNativeGesture(event)
        }
      }
      return false
    }
  }

  private fun finishPagerNativeGesture(event: MotionEvent) {
    if (pagerNativeGestureStarted) {
      NativeGestureUtil.notifyNativeGestureEnded(this, event)
      pagerNativeGestureStarted = false
    }
    recyclerView.parent?.requestDisallowInterceptTouchEvent(false)
    pagerGestureIsVertical = false
  }
  // Intercept only a completed tap so RecyclerView keeps drag and reorder ownership.
  private val keyboardTapTouchListener = object : RecyclerView.SimpleOnItemTouchListener() {
    private var downX = 0f
    private var downY = 0f
    private var observingTap = false
    private var moved = false

    override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          downX = event.x
          downY = event.y
          moved = false
          observingTap =
            keyboardShouldPersistTaps != NativeListKeyboardShouldPersistTaps.ALWAYS &&
              focusedTextEditor() != null
        }
        MotionEvent.ACTION_MOVE -> if (
          observingTap && hypot(event.x - downX, event.y - downY) > pagerGestureTouchSlop
        ) {
          moved = true
        }
        MotionEvent.ACTION_UP -> {
          val shouldDismiss =
            observingTap &&
              !moved &&
              reorderPlaceholderDecoration.position == RecyclerView.NO_POSITION &&
              when (keyboardShouldPersistTaps) {
                NativeListKeyboardShouldPersistTaps.NEVER -> true
                NativeListKeyboardShouldPersistTaps.HANDLED ->
                  !keyboardTapIsHandled(recyclerView, event)
                NativeListKeyboardShouldPersistTaps.ALWAYS -> false
              }
          observingTap = false
          if (shouldDismiss) {
            dismissKeyboard()
            return true
          }
        }
        MotionEvent.ACTION_CANCEL -> observingTap = false
      }
      return false
    }

    override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) = Unit
  }
  private val reorderPlaceholderDecoration = ReorderPlaceholderDecoration(
    adapter = adapter,
    insetPx = dp(REORDER_PLACEHOLDER_INSET_DP),
    radiusPx = dp(REORDER_PLACEHOLDER_RADIUS_DP).toFloat(),
  )
  private val reorderSpringInterpolator = ReorderSpringInterpolator(
    damping = REORDER_SPRING_DAMPING,
    stiffness = REORDER_SPRING_STIFFNESS,
    mass = REORDER_SPRING_MASS,
    durationSeconds = REORDER_SPRING_DURATION_MS / 1_000.0,
  )
  private var footerView: NativeListRowHost = NativeListRendererRegistry.create(reactContext, NativeListRendererKey.ACTION.ordinal, fonts)
  private var footerRendererKey = NativeListRendererKey.ACTION
  private val sectionIndexView = NativeListSectionIndexView(context, fonts)
  private val sectionIndexPreview = TextView(context)
  private var config: NativeListConfig? = null
  private var usesSelectorSourceScale = false
  private val stickyHeaderView = NativeListRendererRegistry.create(reactContext, NativeListRendererKey.SECTION_HEADER.ordinal, fonts)
  private var stickyHeaderSignature: String? = null
  private var stickyHeaderConfig: NativeListConfig? = null
  private val stickyHeaderHost = object : FrameLayout(reactContext) {
    private var down: MotionEvent? = null
    private var forwarding = false

    private fun forward(event: MotionEvent) {
      val copy = MotionEvent.obtain(event)
      copy.offsetLocation(x - refreshLayout.x - recyclerView.x, y - refreshLayout.y - recyclerView.y)
      recyclerView.dispatchTouchEvent(copy)
      copy.recycle()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> { down?.recycle(); down = MotionEvent.obtain(event); forwarding = false }
        MotionEvent.ACTION_MOVE -> {
          val initial = down
          if (initial != null && kotlin.math.abs(event.y - initial.y) > pagerGestureTouchSlop) {
            startForwarding(initial, event)
            return true
          }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { down?.recycle(); down = null }
      }
      return false
    }

    // The intercepting MOVE is not redelivered to onTouchEvent, so forward it
    // together with the initial DOWN to keep the list's drag continuous.
    private fun startForwarding(initial: MotionEvent, event: MotionEvent) {
      forward(initial)
      forward(event)
      forwarding = true
      parent?.requestDisallowInterceptTouchEvent(true)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
      if (!forwarding) return super.onTouchEvent(event)
      forward(event)
      if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
        forwarding = false
        down?.recycle(); down = null
      }
      return true
    }

    override fun onDetachedFromWindow() { down?.recycle(); down = null; super.onDetachedFromWindow() }
  }
  private val stickyPreDraw = android.view.ViewTreeObserver.OnPreDrawListener {
    if (!commitPreparingFooterFrame()) {
      if (pendingPreparingFooterFrame?.handoffOnly != true) schedulePreparingFooterFrame()
      return@OnPreDrawListener false
    }
    if (pendingStructuralTransition != null || structuralAnimator != null || structuralTail != null) {
      val measuredHeightPx = containerSlots.getOrNull(2)?.measuredHeight ?: 0
      if (measuredHeightPx > 0 && measuredHeightPx != structuralFooterObservedNativeHeightPx) {
        // React layout can change the footer's contents before its onLayout height reaches JS.
        val roundedDip = ceil(measuredHeightPx / density.toDouble())
        val naturalHeight = (roundedDip * density).roundToInt()
        if (structuralTail != null && config?.disclosure != null &&
          naturalHeight != footerNaturalHeightPx()) {
          structuralFooterObservedNativeHeightPx = measuredHeightPx
          acceptedFooterNaturalHeightPx = naturalHeight
          structuralFooterCompensatedHeightPx = null
          finishStructuralTail(interrupted = true)
          return@OnPreDrawListener false
        }
        if (structuralTail != null && structuralTailRowsInAdapterPx > 0 &&
          !structuralTailFinalizing) {
          enqueuePreparingFooterFrame(slotHeights.copyOf(), naturalHeight,
            observedNativeHeight = measuredHeightPx)
          if (pendingPreparingFooterFrame != null) return@OnPreDrawListener false
        } else {
          structuralFooterObservedNativeHeightPx = measuredHeightPx
          reconcileStructuralFooterHeight(naturalHeight)
          acceptedFooterNaturalHeightPx = naturalHeight
          if (structuralTail == null || structuralTailFinalizing) syncFooterPresentation()
        }
      }
    }
    if (structuralTail != null && !structuralTailFinalizing) layoutFooterPresentationHostNow()
    if (config?.disclosure != null) layoutExpandedContentHostNow()
    updateStickyHeader()
    finishScrollCommandIfSettled()
    true
  }
  // Registered only while attached, on the captured window observer, so detach
  // removes the same instance the listener was merged into.
  private var stickyObservedTree: ViewTreeObserver? = null
  private var spacingDecoration: ItemSpacingDecoration? = null
  private var itemTouchHelper: ItemTouchHelper? = null
  private var activeReorderHolder: NativeListViewHolder? = null
  private var reorderTouchListener: RecyclerView.OnItemTouchListener? = null
  private var reorderTouchHandler: Handler? = null
  private var endReachedGeneration: Int? = null
  private var lastVisibleRangeSignature: String? = null
  private var visibleEventScheduled = false
  private var dragFrom = RecyclerView.NO_POSITION
  private var dragTo = RecyclerView.NO_POSITION
  private var pendingReorder: List<NativeListItem>? = null
  private var reorderRelayoutActive = false
  private var reorderRelayoutScheduled = false
  private var lastReorderRelayoutLogAtMs = 0L
  private var sectionIndexEntries: List<NativeListSectionIndexEntry> = emptyList()
  private var sectionIndexScrubbing = false
  private var sectionIndexProgrammaticScroll = false
  private var sectionIndexHapticsEnabled = true
  private val sectionIndexAttachmentHandler = Handler(Looper.getMainLooper())
  private var sectionIndexListAttachmentScheduled = false
  private val sectionIndexListAttachmentRunnable = Runnable {
    sectionIndexListAttachmentScheduled = false
    attachSectionIndexToList()
    if (!disposed && isAttachedToWindow) updateSectionIndexAttachment()
  }
  private val sectionIndexLocationOnScreen = IntArray(2)
  private val sectionIndexHostLocationOnScreen = IntArray(2)
  private var sectionIndexObservedTree: ViewTreeObserver? = null
  private val sectionIndexPreDrawListener = ViewTreeObserver.OnPreDrawListener {
    updateSectionIndexAttachment()
    true
  }
  private var pendingScrollRequest: ScrollRequest? = null
  private var acceptedScrollRequest: ScrollRequest? = null
  private var executingScrollCommand = false
  private var dispatchingScrollRequest: ScrollRequest? = null
  private var commandLayoutRequest: ScrollRequest? = null
  private var commandAlignmentRequest: ScrollRequest? = null
  private var readingIntentEpoch = 0L
  private var pendingBodyStartEpoch: Long? = null
  private val actionAnchorInstanceId = UUID.randomUUID().toString()
  private var actionAnchorCounter = 0L
  private var actionAnchor: ActionAnchorRecord? = null
  private var lastLayoutWidth = -1
  private var lastLayoutHeight = -1
  private var lastLayoutDirection = layoutDirection
  private var lastMarketPaginationAnchorLogAtMs = 0L
  private var disposed = false
  private var resourcesDisposed = false
  private var acceptedKeyIndex: Map<String, Int>? = null

  private fun acceptRowIndex(next: NativeListConfig, previous: NativeListConfig?) {
    if (previous != null && acceptedKeyIndex != null &&
      previous.generation == next.generation && previous.items.size == next.items.size &&
      previous.items.indices.all { index ->
        previous.items[index].key == next.items[index].key &&
          previous.items[index].type == next.items[index].type
      }) return
    acceptedKeyIndex = next.items.withIndex().associate { it.value.key to it.index }
  }

  init {
    orientation = VERTICAL
    recyclerView.adapter = scrollContentAdapter
    recyclerView.layoutManager = layoutManager
    recyclerView.itemAnimator = null
    recyclerView.addOnItemTouchListener(keyboardTapTouchListener)
    recyclerView.addOnItemTouchListener(pagerGestureTouchListener)
    recyclerView.addItemDecoration(reorderPlaceholderDecoration)
    // OneKey patch: full-width selector header backgrounds do not change row content insets.
    recyclerView.addItemDecoration(SelectorBackgroundDecoration(adapter))
    recyclerView.addItemDecoration(ContainerSlotInsetDecoration { slotInsetPx to slotRightInsetPx })
    layoutManager.onBeforeDataLayout = {
      if (pendingBodyStartEpoch == readingIntentEpoch && adapter.itemCount > 0 && pendingScrollRequest == null) {
        layoutManager.scrollToPositionWithOffset(0, 0)
        pendingBodyStartEpoch = null
      }
      pendingStructuralTransition?.let(::alignPendingStructuralAnchor)
    }
    layoutManager.onChildLaidOut = ::recordLaidOutRowExtent
    layoutManager.onLayoutPassCompleted = {
      if (structuralFooterHoldTop != null) updateStructuralFooterHoldPosition()
      else if (structuralAnimator != null) updateStructuralFooterVisualPosition()
      emitScrollPositionThresholdIfNeeded(resync = true)
      updateSidecarPosition(resync = true)
      if (containerSlotRelayoutPending) scheduleContainerSlotRelayout()
      if (commandLayoutRequest === acceptedScrollRequest) commandLayoutRequest = null
      finishScrollCommandIfSettled()
    }
    recyclerView.setHasFixedSize(false)
    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        if (config?.layout != "grid") return 1
        val rowPosition = rowPositionForScroll(position)
        if (rowPosition !in 0 until adapter.itemCount) return layoutManager.spanCount
        val type = adapter.itemAt(rowPosition)?.type
        return if (type == "sectionHeader" || type == "system" || type == "action" || type == "buttonGroup") {
          layoutManager.spanCount
        } else {
          1
        }
      }
    }
    refreshLayout.addView(
      recyclerView,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
    )
    contentContainer.addView(
      refreshLayout,
      FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      ),
    )
    contentContainer.addView(structuralTailLayer, FrameLayout.LayoutParams(0, 0, Gravity.TOP or Gravity.LEFT))
    contentContainer.addView(sidecarHost,
      FrameLayout.LayoutParams(0, 0, Gravity.TOP or Gravity.RIGHT))
    stickyHeaderHost.visibility = GONE
    stickyHeaderHost.addView(stickyHeaderView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT))
    contentContainer.addView(stickyHeaderHost, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP))
    contentContainer.addView(
      sectionIndexView,
      FrameLayout.LayoutParams(
        sectionIndexDp(SECTION_INDEX_RAIL_WIDTH_DP),
        FrameLayout.LayoutParams.MATCH_PARENT,
        Gravity.END,
      ),
    )
    sectionIndexPreview.apply {
      gravity = Gravity.CENTER
      textSize = 30f
      typeface = fonts.regular(context)
      setPadding(0, 0, sectionIndexDp(10), 0)
      visibility = INVISIBLE
      alpha = 0f
      importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }
    contentContainer.addView(
      sectionIndexPreview,
      FrameLayout.LayoutParams(
        sectionIndexDp(SECTION_INDEX_PREVIEW_WIDTH_DP),
        sectionIndexDp(SECTION_INDEX_PREVIEW_HEIGHT_DP),
        Gravity.CENTER_VERTICAL or Gravity.END,
      ).apply {
        marginEnd = sectionIndexDp(SECTION_INDEX_PREVIEW_END_MARGIN_DP)
      },
    )
    addView(contentContainer, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))
    footerView.visibility = GONE
    addView(footerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

    adapter.onRowPress = ::handleRowPress
    adapter.onAction = ::handleAction
    adapter.onBindingInvalidated = ::handleBindingInvalidated
    adapter.checkboxState = ::resolveCheckboxState
    stickyHeaderView.onRowPress = ::handleRowPress
    stickyHeaderView.onAction = ::handleAction
    stickyHeaderView.onBindingInvalidated = ::handleBindingInvalidated
    footerView.onRowPress = ::handleRowPress
    footerView.onAction = ::handleAction
    footerView.onBindingInvalidated = ::handleBindingInvalidated
    sectionIndexView.onSelect = ::selectSectionIndex
    sectionIndexView.onInteractionEnded = { finishSectionIndexInteraction() }
    sectionIndexView.visibility = GONE

    refreshLayout.isEnabled = false
    refreshLayout.setOnRefreshListener {
      emit(
        ROW_ACTION,
        JSONObject().put("actionKey", "nativeList.refresh"),
      )
    }
    recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
      // OneKey patch: a collapsible pager owns the extra padding above the rows.
      // Keep the refresh control below that header without moving ordinary lists.
      updateRefreshIndicatorOffset((recyclerView.paddingTop - configuredTopPaddingPx).coerceAtLeast(0))
    }

    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
          if (structuralTail != null) finishStructuralTail(interrupted = true)
          if (structuralAnimator != null || pendingStructuralTransition != null)
            structuralScrollInterrupted = true
          cancelExecutingScrollCommand()
          readingIntentEpoch++
          pendingBodyStartEpoch = null
        }
        when (newState) {
          RecyclerView.SCROLL_STATE_DRAGGING -> {
            sectionIndexProgrammaticScroll = false
            invalidateActionAnchor("scroll")
            if (
              keyboardDismissMode == NativeListKeyboardDismissMode.ON_DRAG &&
              (layoutManager.orientation == RecyclerView.HORIZONTAL || pagerGestureIsVertical)
            ) {
              dismissKeyboard()
            }
          }
          RecyclerView.SCROLL_STATE_IDLE -> {
            sectionIndexProgrammaticScroll = false
            finishScrollCommandIfSettled()
          }
        }
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        logicalScrollDistancePx = (logicalScrollDistancePx + dy).coerceAtLeast(0)
        if (structuralScrollInterrupted && dy != 0) {
          if (pendingStructuralTransition != null) {
            pendingStructuralTransition = pendingStructuralTransition?.let {
              it.copy(footerStartTop = it.footerStartTop?.minus(dy))
            }
            structuralFooterHoldTop = structuralFooterHoldTop?.minus(dy)
            updateStructuralFooterHoldPosition()
          } else if (structuralAnimator != null) {
            structuralFooterVisualStartTop = structuralFooterVisualStartTop?.minus(dy)
            structuralFooterVisualEndTop = structuralFooterVisualEndTop?.minus(dy)
            updateStructuralFooterVisualPosition()
          }
        }
        updateSidecarPosition()
        if (structuralTail != null) updateStructuralTailWindow()
        emitScrollPositionThresholdIfNeeded(resync = dx == 0 && dy == 0)
        if (dx != 0 || dy != 0) invalidateActionAnchor("scroll")
        syncSectionIndexToVisibleRows()
        scheduleVisibleEvent()
        checkEndReached()
      }
    })
  }

  private fun focusedTextEditor(): View? {
    val insets = ViewCompat.getRootWindowInsets(this)
    if (insets != null && !insets.isVisible(WindowInsetsCompat.Type.ime())) return null
    val focused = rootView.findFocus() ?: reactContext.currentActivity?.currentFocus
    return focused?.takeIf(View::onCheckIsTextEditor)
  }

  private fun dismissKeyboard(): Boolean {
    val focused = focusedTextEditor() ?: return false
    val windowToken = focused.windowToken
    focused.clearFocus()
    val inputMethodManager =
      context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
  }

  private fun keyboardTapIsHandled(recyclerView: RecyclerView, event: MotionEvent): Boolean {
    val child = recyclerView.findChildViewUnder(event.x, event.y) ?: return false
    val position = rowPositionForScroll(recyclerView.getChildAdapterPosition(child))
    val item = adapter.itemAt(position) ?: return false
    val clickable = clickableDescendantAt(child, event.rawX.toInt(), event.rawY.toInt())
    val accessoryHandlesTap = clickable != null && clickable !== child
    return accessoryHandlesTap || item.isRowPressEnabled
  }

  private fun clickableDescendantAt(view: View, rawX: Int, rawY: Int): View? {
    if (!view.isShown) return null
    val bounds = Rect()
    if (!view.getGlobalVisibleRect(bounds) || !bounds.contains(rawX, rawY)) return null
    if (view is ViewGroup) {
      for (index in view.childCount - 1 downTo 0) {
        clickableDescendantAt(view.getChildAt(index), rawX, rawY)?.let { return it }
      }
    }
    return view.takeIf { it.isEnabled && it.isClickable }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (containerSlotRelayoutPending) scheduleContainerSlotRelayout()
    val first = config?.items?.firstOrNull()
    if (recyclerView.isLayoutRequested && (first?.type == "market" || first?.json?.optString("presentation") == "market")) {
      // A retained Market page can keep cached parent measurements after its diff.
      // Drain the child's pending layout so its committed rows replace the old cells.
      relayoutRecyclerViewImmediately()
    }
    val nextWidth = right - left
    val nextHeight = bottom - top
    val widthChanged = lastLayoutWidth != nextWidth
    val directionChanged = layoutDirection != lastLayoutDirection
    if (structuralTail != null && lastLayoutWidth >= 0 &&
      (widthChanged || lastLayoutHeight != nextHeight || directionChanged))
      finishStructuralTail(interrupted = true)
    if (
      lastLayoutWidth >= 0 &&
      (nextWidth != lastLayoutWidth || nextHeight != lastLayoutHeight || layoutDirection != lastLayoutDirection)
    ) {
      invalidateActionAnchor("layout")
    }
    lastLayoutWidth = nextWidth
    lastLayoutHeight = nextHeight
    lastLayoutDirection = layoutDirection
    if ((widthChanged || directionChanged) && sidecarLayout != null) config?.let(::updateLayout)
    updateSidecarGeometry()
    scheduleSidecarSpacer()
    updateSectionIndexAttachment()
    performPendingScrollIfNeeded()
  }

  private fun updateRefreshIndicatorOffset(offset: Int) {
    // Let an ancestor render above its shared header without guessing caller offsets.
    refreshLayout.setTag(R.id.onekey_native_scroll_coordinator_refresh_inset, offset)
    if (refreshIndicatorOffsetPx == offset) return
    refreshIndicatorOffsetPx = offset
    val refreshing = refreshLayout.isRefreshing
    val start = offset - refreshLayout.progressCircleDiameter
    refreshLayout.setProgressViewOffset(false, start, start + refreshIndicatorTravelPx)
    refreshLayout.isRefreshing = refreshing
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (containerSlotRelayoutPending) scheduleContainerSlotRelayout()
    recyclerView.setTag(R.id.onekey_native_scroll_coordinator_offset_listener, coordinatorOffsetListener)
    val cancelTag = R.id.onekey_native_scroll_coordinator_command_cancel_listener
    if (recyclerView.getTag(cancelTag) == null) recyclerView.setTag(cancelTag, coordinatorCommandCancelListener)
    emitScrollPositionThresholdIfNeeded()
    startStickyHeaderTracking()
    updateSectionIndexAttachment()
    val first = config?.items?.firstOrNull()
    if (recyclerView.isLayoutRequested && (first?.type == "market" || first?.json?.optString("presentation") == "market")) {
      relayoutContents()
    }
  }

  override fun onDetachedFromWindow() {
    val disclosureId = activeDisclosureTransitionId
    if (structuralTail != null) {
      discardStructuralTail()
      val epoch = structuralTransitionEpoch
      config?.let { accepted ->
        adapter.submitList(accepted.items) {
          if (!disposed && epoch == structuralTransitionEpoch &&
            activeDisclosureTransitionId == disclosureId && disclosureId != null) {
            disclosureFraction = if (accepted.disclosure?.expanded == true) 1f else 0f
            disclosureTransitioning = false
            syncDisclosurePresentation()
            settleDisclosureCommand("interrupted")
          }
        }
      }
    } else if (disclosureId != null && !disposed) {
      disclosureFraction = if (config?.disclosure?.expanded == true) 1f else 0f
      disclosureTransitioning = false
      syncDisclosurePresentation()
      settleDisclosureCommand("interrupted")
    }
    val cancelTag = R.id.onekey_native_scroll_coordinator_command_cancel_listener
    if (recyclerView.getTag(cancelTag) === coordinatorCommandCancelListener) recyclerView.setTag(cancelTag, null)
    cancelExecutingScrollCommand()
    if (recyclerView.getTag(R.id.onekey_native_scroll_coordinator_offset_listener) === coordinatorOffsetListener) {
      recyclerView.setTag(R.id.onekey_native_scroll_coordinator_offset_listener, null)
    }
    stopStickyHeaderTracking()
    stopSectionIndexAttachmentTracking()
    scheduleSectionIndexAttachmentToList()
    updateRefreshIndicatorOffset(0)
    if (disposed) disposeResources()
    super.onDetachedFromWindow()
  }

  fun applySnapshot(snapshotJson: String) {
    val next = try {
      NativeListConfig.parse(snapshotJson)
    } catch (_: Exception) {
      return
    }
    invalidateActionAnchor("snapshot")
    val previous = config
    val transition = next.structuralTransition
    val isNewTransition = transition != null && transition.id != consumedStructuralTransitionId
    if (transition != null) consumedStructuralTransitionId = transition.id
    val disclosureGeometryInvalid = batchedDisclosureGeometryInvalid ||
      (!isNewTransition && structuralTail != null &&
        previous?.disclosure != next.disclosure)
    batchedDisclosureGeometryInvalid = false
    if (!isNewTransition &&
      (next.disclosure == null || previous?.disclosure?.expanded != next.disclosure.expanded)) {
      activeDisclosureTransitionId = null
      directDisclosureTogglePositionPending = false
    }
    val oldKeys = previous?.items?.map { it.key } ?: emptyList()
    val nextKeys = next.items.map { it.key }
    if (next.disclosure != null && isNewTransition && previous != null) {
      activeDisclosureTransitionId = transition?.id
      directDisclosureStatus = if (oldKeys == nextKeys || nextKeys.isEmpty() ||
        transition?.durationMs == 0L ||
        (Build.VERSION.SDK_INT >= 26 && !ValueAnimator.areAnimatorsEnabled()))
        "completed" else "interrupted"
      directDisclosureTogglePositionPending = !next.disclosure.expanded &&
        !disclosureGeometryInvalid && directDisclosureStatus == "completed"
    } else if (next.disclosure == null) {
      activeDisclosureTransitionId = null
      directDisclosureTogglePositionPending = false
    }
    if (disclosureGeometryInvalid && activeDisclosureTransitionId != null) {
      directDisclosureStatus = "interrupted"
      directDisclosureTogglePositionPending = false
    }
    val positionDirectCollapse = directDisclosureTogglePositionPending
    var animateStructure = !disclosureGeometryInvalid && previous != null &&
      isNewTransition && oldKeys != nextKeys &&
      previous.orientation == "vertical" &&
      (previous.layout == "linear" || previous.layout == "table") &&
      next.orientation == "vertical" && (next.layout == "linear" || next.layout == "table") &&
      (transition?.durationMs ?: 0) > 0 &&
      (if (Build.VERSION.SDK_INT >= 26) ValueAnimator.areAnimatorsEnabled() else
        android.provider.Settings.Global.getFloat(context.contentResolver, "animator_duration_scale", 1f) > 0f)
    val prefixCount = if (animateStructure && previous != null && recyclerView.width > 0 &&
      recyclerView.height > 0) prefixTailCount(previous, next) else null
    val exceedsTailHostLimit = prefixCount != null && previous != null &&
      exceedsStructuralTailHostLimit(previous, next, prefixCount)
    if (exceedsTailHostLimit) animateStructure = false
    if (next.disclosure != null && prefixCount == null) animateStructure = false
    if (prefixCount != null && !exceedsTailHostLimit && previous != null && transition != null) {
      startPrefixTailTransition(previous, next, transition, prefixCount)
      return
    }
    var heldTailEpoch: Int? = null
    if (structuralTail != null) {
      if (!disclosureGeometryInvalid && !animateStructure && oldKeys == nextKeys &&
        (Build.VERSION.SDK_INT < 26 || ValueAnimator.areAnimatorsEnabled()) &&
        previous != null && updatePrefixTailSnapshot(previous, next, acceptIndex = true)) {
        return
      }
      heldTailEpoch = holdStructuralTailUntilListCommit()
      // Keep the accepted presentation until the full adapter list has committed.
      animateStructure = false
    }
    val nextKeySet = nextKeys.toHashSet()
    val visibleAnchor = if (animateStructure) {
      val first = layoutManager.findFirstVisibleItemPosition()
      val last = layoutManager.findLastVisibleItemPosition()
      (first..last).firstNotNullOfOrNull { position ->
        val rowIndex = rowPositionForScroll(position)
        oldKeys.getOrNull(rowIndex)?.takeIf(nextKeySet::contains)?.let { key ->
          layoutManager.findViewByPosition(position)?.let { key to layoutManager.getDecoratedTop(it) }
        }
      }
    } else null
    val anchorKey = visibleAnchor?.first ?: transition?.anchorKey?.takeIf(nextKeySet::contains)
    val anchorTop = visibleAnchor?.second
    val footerStartTop = if (animateStructure && footerSlotAdapter.itemCount > 0) {
      layoutManager.findViewByPosition(scrollContentAdapter.itemCount - 1 - sidecarSpacerAdapter.itemCount)?.let {
        layoutManager.getDecoratedTop(it) + it.translationY.roundToInt()
      }
    } else null
    val oldKeySet = oldKeys.toHashSet()
    val removedExtent = if (animateStructure) previous?.items?.sumOf { item ->
      if (nextKeySet.contains(item.key)) 0 else estimatedItemExtentPx(item)
    } ?: 0 else 0
    val addedExtent = if (animateStructure) next.items.sumOf { item ->
      if (oldKeySet.contains(item.key)) 0 else estimatedItemExtentPx(item)
    } else 0
    if (!animateStructure && oldKeys != nextKeys && heldTailEpoch == null) cancelStructuralTransition()
    if (previous?.items?.isEmpty() == true && next.items.isNotEmpty() && adapter.itemCount == 0 &&
      pendingScrollRequest == null && recyclerView.scrollState != RecyclerView.SCROLL_STATE_DRAGGING &&
      !recyclerView.canScrollVertically(-1) && layoutManager.findFirstVisibleItemPosition() == 0) {
      val first = layoutManager.findViewByPosition(0)
      if (first != null && kotlin.math.abs(layoutManager.getDecoratedTop(first) - recyclerView.paddingTop) <= 1) {
        pendingBodyStartEpoch = readingIntentEpoch
      }
    }
    if (previous != null && canApplyStableContentUpdate(previous, next)) {
      // OneKey patch: authorize a diff payload only against this exact validated baseline.
      next.items.forEachIndexed { index, item ->
        if (item.content != previous.items[index].content &&
          adapter.currentList.getOrNull(index) === previous.items[index]
        ) {
          item.selectionUpdateFromContent = previous.items[index].content
        }
      }
      val changedSummaryKeys = previous.items.indices.mapNotNull { index ->
        previous.items[index].key.takeIf {
          previous.items[index].content != next.items[index].content
        }
      }.toSet()
      acceptRowIndex(next, previous)
      config = next
      if (heldTailEpoch == null) {
        disclosureFraction = if (next.disclosure?.expanded == true) 1f else 0f
        disclosureTransitioning = false
      }
    updateContainerSlots()
    usesSelectorSourceScale = next.items.any { it.usesSelectorSourceScale }
    adapter.usesSelectorSourceScale = usesSelectorSourceScale
      adapter.theme = next.theme
      adapter.listStyle = next.listStyle
      adapter.layout = next.layout
      adapter.orientation = next.orientation
      adapter.selectedKeys = next.selectedKeys
      // Rows rebind through the adapter's listStyle invalidation; the fixed footer is bound
      // outside the adapter, so a listStyle-only snapshot must rebind it here.
      if (previous.listStyle?.toString() != next.listStyle?.toString()) bindFooter(next)
      val directDisclosureId = activeDisclosureTransitionId
      adapter.submitList(next.items) {
        val committed: () -> Unit = {
          // OneKey patch: DiffUtil has dispatched its payloads; release the old serialized rows.
          next.items.forEach { it.selectionUpdateFromContent = null }
          recyclerView.post {
            scheduleSidecarSpacer()
            startPendingStructuralTransition()
            bindVisibleSelection(changedSummaryKeys)
            bindFooterSelection()
            performPendingScrollIfNeeded()
            settleCommittedDirectDisclosure(next, directDisclosureId, heldTailEpoch == null)
          }
        }
        if (heldTailEpoch != null) completeHeldTailCommit(next, heldTailEpoch, committed,
          positionDirectCollapse)
        else committed()
      }
      return
    }
    val preserveMarketPaginationAnchor = previous?.let {
      isMarketPaginationUpdate(it, next)
    } ?: false
    // Keep the first Market row at its current pixel offset when it is
    // reordered. RecyclerView otherwise follows the previous first key.
    val marketStartOffset = if (
      previous?.items?.firstOrNull()?.type == "market" &&
      next.items.firstOrNull()?.type == "market" &&
      previous.items.firstOrNull()?.key != next.items.firstOrNull()?.key &&
      rowPositionForScroll(layoutManager.findFirstVisibleItemPosition()) == 0
    ) {
      layoutManager.findViewByPosition(scrollPositionForRow(0))?.let {
        layoutManager.getDecoratedTop(it) - recyclerView.paddingTop
      }
    } else null
    acceptRowIndex(next, previous)
    config = next
    if (heldTailEpoch == null) {
      disclosureFraction = if (next.disclosure?.expanded == true) 1f else 0f
      disclosureTransitioning = false
    }
    pruneMeasuredRowExtents(next.items)
    updateContainerSlots()
    usesSelectorSourceScale = next.items.any { it.usesSelectorSourceScale }
    adapter.usesSelectorSourceScale = usesSelectorSourceScale
    if (previous?.generation != next.generation) endReachedGeneration = null
    pendingReorder = null
    adapter.theme = next.theme
    adapter.listStyle = next.listStyle
    adapter.layout = next.layout
    adapter.orientation = next.orientation
    adapter.selectedKeys = next.selectedKeys
    configureSectionIndex(next)
    updateLayout(next)
    if (animateStructure && transition != null) {
      structuralTransitionEpoch += 1
      pendingStructuralStartScheduled = false
      structuralAnimator?.cancel()
      structuralAnimator = null
      structuralScrollInterrupted = false
      structuralFooterHoldTop = footerStartTop
      structuralFooterCompensatedHeightPx = slotHeights[2]
      val held = (structuralSpacerHeightPx + removedExtent - addedExtent).coerceAtLeast(0)
      pendingStructuralTransition = PendingStructuralTransition(
        transition, structuralTransitionEpoch, anchorKey, anchorTop, footerStartTop, held,
        removedExtent > addedExtent,
      )
      recyclerView.itemAnimator = DefaultItemAnimator().apply {
        addDuration = transition.durationMs
        removeDuration = transition.durationMs
        moveDuration = 0
        changeDuration = 0
        supportsChangeAnimations = false
      }
    }
    val directDisclosureId = activeDisclosureTransitionId
    adapter.submitList(next.items) {
      val committed: () -> Unit = {
        pendingStructuralTransition?.takeIf { it.epoch == structuralTransitionEpoch }?.let {
          setStructuralSpacerHeight(it.heldHeightPx)
          pendingStructuralTransition = it.copy(spacerInserted = true)
          pendingStructuralTransition?.let(::alignPendingStructuralAnchor)
        }
        if (marketStartOffset != null) {
          layoutManager.scrollToPositionWithOffset(scrollPositionForRow(0), marketStartOffset)
        }
        relayoutContents(preserveMarketPaginationAnchor)
        scheduleSidecarSpacer()
        startPendingStructuralTransition()
        performPendingScrollIfNeeded()
        syncSectionIndexToVisibleRows()
        scheduleVisibleEvent()
        if (next.items.isNotEmpty()) checkEndReached()
        if (directDisclosureId != null) recyclerView.post {
          settleCommittedDirectDisclosure(next, directDisclosureId, heldTailEpoch == null)
        }
      }
      if (heldTailEpoch != null) completeHeldTailCommit(next, heldTailEpoch, committed,
        positionDirectCollapse)
      else committed()
    }
    refreshLayout.isEnabled = next.pullToRefresh
    refreshLayout.setDistanceToTriggerSync(next.refreshTriggerDistance?.let { (it * density).roundToInt() } ?: defaultRefreshTriggerDistancePx)
    refreshLayout.isRefreshing = next.refreshing
    bindFooter(next)
    updateReordering(next)
  }

  private fun startStructuralTransition(
    transition: NativeListConfig.StructuralTransition,
    epoch: Int,
    anchorKey: String?,
    anchorTop: Int?,
    footerStartTop: Int?,
  ) {
    structuralFooterHoldTop = null
    val startSpacer = structuralSpacerHeightPx
    structuralStartSpacerPx = startSpacer
    structuralFooterVisualStartTop = footerStartTop
    structuralFooterVisualProgress = 0f
    val startOffset = measuredScrollDistancePx()?.also { logicalScrollDistancePx = it }
      ?: logicalScrollDistancePx
    val anchorIndex = anchorKey?.let { key -> adapter.currentList.indexOfFirst { it.key == key } }
    val currentAnchorTop = anchorIndex?.takeIf { it >= 0 }?.let { index ->
      layoutManager.findViewByPosition(scrollPositionForRow(index))?.let(layoutManager::getDecoratedTop)
    }
    val anchorDelta = if (anchorTop != null && currentAnchorTop != null) currentAnchorTop - anchorTop else 0
    val leftFinal = (sidecarLeftExtentPx() - startSpacer).coerceAtLeast(0)
    val sidecarBottom = if (sidecarLayout != null)
      recyclerView.paddingTop + slotHeights[3] + recyclerView.paddingBottom else 0
    val finalMax = (max(leftFinal, sidecarBottom) - recyclerView.height).coerceAtLeast(0)
    var targetOffset = (startOffset + anchorDelta).coerceIn(0, finalMax)
    if (anchorTop == null && anchorIndex != null && anchorIndex >= 0) {
      val fallbackTop = nativeListScrollPrefixPx(
        scrollPositionForRow(anchorIndex),
        layoutManager.spanCount,
        layoutManager.spanSizeLookup::getSpanSize,
        ::estimatedScrollExtentPx,
      ).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
      targetOffset = minOf(targetOffset, fallbackTop)
    }
    structuralFooterVisualEndTop = if (footerStartTop != null && footerSlotAdapter.itemCount > 0) {
      val footerPosition = scrollContentAdapter.itemCount - 1 - sidecarSpacerAdapter.itemCount
      val finalPrefix = nativeListScrollPrefixPx(
        footerPosition,
        layoutManager.spanCount,
        layoutManager.spanSizeLookup::getSpanSize,
        ::estimatedScrollExtentPx,
      ) - startSpacer
      (finalPrefix + recyclerView.paddingTop - targetOffset)
        .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
    } else null
    structuralAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
      duration = transition.durationMs
      interpolator = android.view.animation.AccelerateDecelerateInterpolator()
      addUpdateListener { animation ->
        if (epoch != structuralTransitionEpoch || disposed) return@addUpdateListener
        val fraction = animation.animatedValue as Float
        structuralFooterVisualProgress = fraction
        setStructuralSpacerHeight((structuralStartSpacerPx * (1f - fraction)).roundToInt())
        measuredScrollDistancePx()?.let { logicalScrollDistancePx = it }
        val finalLeftExtent = (sidecarLeftExtentPx() - structuralSpacerHeightPx).coerceAtLeast(0)
        val finalMaximum = (max(finalLeftExtent, sidecarBottom) - recyclerView.height).coerceAtLeast(0)
        val desired = (startOffset + (minOf(targetOffset, finalMaximum) - startOffset) * fraction).roundToInt()
        if (!structuralScrollInterrupted && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE)
          recyclerView.scrollBy(0, desired - logicalScrollDistancePx)
        updateStructuralFooterVisualPosition()
      }
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          if (epoch != structuralTransitionEpoch) return
          structuralAnimator = null
          recyclerView.itemAnimator = null
          setStructuralSpacerHeight(0)
          scheduleSidecarSpacer()
          footerSlotAdapter.setVisualTranslationY(0f)
          structuralFooterVisualStartTop = null
          structuralFooterVisualEndTop = null
          structuralFooterVisualProgress = 0f
          structuralFooterCompensatedHeightPx = null
          structuralStartSpacerPx = 0
        }
      })
      start()
    }
  }

  private fun captureVisibleMarketAnchor(): VisibleMarketAnchor? {
    val position = rowPositionForScroll(layoutManager.findFirstVisibleItemPosition()).coerceAtLeast(0)
    if (position == RecyclerView.NO_POSITION) return null
    val item = adapter.itemAt(position) ?: return null
    val view = layoutManager.findViewByPosition(scrollPositionForRow(position)) ?: return null
    return VisibleMarketAnchor(
      key = item.key,
      offset = layoutManager.getDecoratedTop(view) - recyclerView.paddingTop,
    )
  }

  private fun isMarketPaginationUpdate(
    previous: NativeListConfig,
    next: NativeListConfig,
  ): Boolean {
    if (!previous.loadMore && !next.loadMore) return false
    val previousRows = previous.items.dropLastWhile {
      it.key in MARKET_PAGINATION_KEYS
    }
    val nextRows = next.items.dropLastWhile {
      it.key in MARKET_PAGINATION_KEYS
    }
    if (previousRows.isEmpty() || previousRows.size > nextRows.size) return false
    if (
      previousRows.firstOrNull()?.type != "market" ||
      nextRows.firstOrNull()?.type != "market"
    ) {
      return false
    }
    return previousRows.indices.all { index ->
      previousRows[index].key == nextRows[index].key
    }
  }

  /**
   * A controlled selection update sends the snapshot back after the native
   * selection delta. Its structure is unchanged; only selectedKeys and the
   * fixed-height summary copy may differ. Keep RecyclerView's current layout
   * and scroll anchor for that echo instead of rebuilding decorations and
   * forcing the host through another layout pass.
   */
  private fun canApplyStableContentUpdate(
    previous: NativeListConfig,
    next: NativeListConfig,
  ): Boolean {
    if (
      previous.generation != next.generation ||
      previous.layout != next.layout ||
      previous.orientation != next.orientation ||
      previous.gridColumns != next.gridColumns ||
      previous.stickyHeaders != next.stickyHeaders ||
      previous.contentPadding != next.contentPadding ||
      previous.contentPaddingHorizontal != next.contentPaddingHorizontal ||
      previous.contentPaddingTop != next.contentPaddingTop ||
      previous.contentPaddingBottom != next.contentPaddingBottom ||
      previous.itemSpacing != next.itemSpacing ||
      previous.selectionMode != next.selectionMode ||
      previous.rowPressToggles != next.rowPressToggles ||
      previous.reorderable != next.reorderable ||
      previous.pullToRefresh != next.pullToRefresh ||
      previous.refreshTriggerDistance != next.refreshTriggerDistance ||
      previous.refreshing != next.refreshing ||
      previous.loadMore != next.loadMore ||
      previous.endReachedThreshold != next.endReachedThreshold ||
      previous.sectionIndexEnabled != next.sectionIndexEnabled ||
      previous.sectionIndexHapticsEnabled != next.sectionIndexHapticsEnabled ||
      previous.sectionIndexCenteredInWindow != next.sectionIndexCenteredInWindow ||
      previous.theme?.toString() != next.theme?.toString() ||
      previous.fixedFooter?.content != next.fixedFooter?.content ||
      previous.items.size != next.items.size
    ) {
      return false
    }
    return previous.items.indices.all { index ->
      val oldItem = previous.items[index]
      val newItem = next.items[index]
      oldItem.key == newItem.key &&
        oldItem.type == newItem.type &&
        // OneKey patch: controlled echoes can also carry row and checkbox selection state.
        // (oldItem.content == newItem.content || isStableSummaryUpdate(oldItem, newItem))
        (oldItem.content == newItem.content || isStableSelectionUpdate(
          oldItem,
          newItem,
          next.selectionMode == "single" || next.selectionMode == "multiple",
        ))
    }
  }

  // OneKey patch: preserve the original summary-only comparison for upstream reference.
  // private fun isStableSummaryUpdate(
  //   previous: NativeListItem,
  //   next: NativeListItem,
  // ): Boolean {
  //   if (
  //     previous.type != "sectionHeader" ||
  //     previous.json.optString("variant") != "summary" ||
  //     next.json.optString("variant") != "summary"
  //   ) {
  //     return false
  //   }
  //   val previousStructure = JSONObject(previous.content).apply {
  //     remove("title")
  //     remove("value")
  //   }
  //   val nextStructure = JSONObject(next.content).apply {
  //     remove("title")
  //     remove("value")
  //   }
  //   return previousStructure.toString() == nextStructure.toString()
  // }

  private fun isStableSelectionUpdate(
    previous: NativeListItem,
    next: NativeListItem,
    controlled: Boolean,
  ): Boolean {
    val previousStructure = selectionComparisonData(previous.json, controlled) ?: return false
    val nextStructure = selectionComparisonData(next.json, controlled) ?: return false
    return previousStructure.toString() == nextStructure.toString()
  }

  // OneKey patch: ignore only fields refreshed by the lightweight selection binder.
  private fun selectionComparisonData(data: JSONObject, controlled: Boolean): JSONObject? {
    val type = data.opt("type") as? String ?: return null
    val result = JSONObject(data.toString())
    if (result.has("selected")) {
      if (result.opt("selected") !is Boolean) return null
      result.remove("selected")
    }
    if (type == "walletGroup") {
      val parent = data.optJSONObject("parent") ?: return null
      if (parent.optString("type") != "identity") return null
      val children = data.optJSONArray("children") ?: return null
      result.put("parent", selectionComparisonData(parent, false) ?: return null)
      val normalizedChildren = JSONArray()
      for (index in 0 until children.length()) {
        val child = children.optJSONObject(index) ?: return null
        if (child.optString("type") != "identity") return null
        normalizedChildren.put(selectionComparisonData(child, false) ?: return null)
      }
      result.put("children", normalizedChildren)
    }
    if (type == "sectionHeader" && data.optString("variant") == "summary") {
      result.remove("title")
      result.remove("value")
    }
    if (!controlled) return result
    fun checkboxData(value: Any?): JSONObject? {
      val checkbox = value as? JSONObject ?: return null
      if (checkbox.optString("kind") != "checkbox") return null
      if (checkbox.has("state") && checkbox.opt("state") !in setOf("checked", "unchecked", "indeterminate")) return null
      checkbox.remove("state")
      return checkbox
    }
    if (type in setOf("dataRow", "sectionHeader", "action") && result.has("checkbox")) {
      result.put("checkbox", checkboxData(result.opt("checkbox")) ?: return null)
    }
    if (type == "identity" && result.has("trailing")) {
      val accessories = result.optJSONArray("trailing") ?: return null
      if (accessories.length() > 2) return null
      var checkboxCount = 0
      for (index in 0 until accessories.length()) {
        val accessory = accessories.optJSONObject(index) ?: return null
        if (accessory.optString("kind") == "checkbox") {
          if (++checkboxCount > 1) return null
          accessories.put(index, checkboxData(accessory) ?: return null)
        }
      }
    }
    return result
  }

  fun applyPatches(patchesJson: String) {
    val current = config ?: return
    val indexByKey = acceptedKeyIndex ?: return
    val patches = try {
      JSONArray(patchesJson)
    } catch (_: Exception) {
      return
    }
    val pending = ArrayList<Pair<Int, JSONObject>>(patches.length())
    val seen = HashSet<String>()
    for (index in 0 until patches.length()) {
      val patch = patches.optJSONObject(index) ?: return
      val key = patch.optString("key")
      val itemIndex = indexByKey[key] ?: return
      val item = current.items.getOrNull(itemIndex) ?: return
      if (item.key != key) return
      if (!seen.add(key)) return
      if (patch.optString("type") != item.type) return
      val changes = patch.optJSONObject("changes") ?: return
      pending.add(itemIndex to changes)
    }
    val nextItems = current.items.toMutableList()
    val selected = LinkedHashSet(current.selectedKeys)
    var marketQuoteOnly = pending.isNotEmpty()
    try {
      pending.forEach { (index, changes) ->
        val previous = nextItems[index]
        marketQuoteOnly = marketQuoteOnly &&
          previous.type == "market" &&
          changes.keys().asSequence().all(MARKET_QUOTE_FIELDS::contains)
        val merged = NativeListItem.parse(mergeRow(previous.json, changes))
        nextItems[index] = merged
        if (changes.has("selected")) {
          if (changes.optBoolean("selected")) selected.add(merged.key) else selected.remove(merged.key)
        }
      }
    } catch (_: Exception) {
      return
    }
    if (!marketQuoteOnly) invalidateActionAnchor("snapshot")
    val next = current.copy(items = nextItems, selectedKeys = selected)
    if (structuralTail != null && updatePrefixTailSnapshot(current, next)) return
    val heldTailEpoch = if (structuralTail != null) holdStructuralTailUntilListCommit() else null
    config = next
    updateContainerSlots()
    usesSelectorSourceScale = next.items.any { it.usesSelectorSourceScale }
    adapter.usesSelectorSourceScale = usesSelectorSourceScale
    adapter.selectedKeys = selected
    val directDisclosureId = activeDisclosureTransitionId
    adapter.submitList(nextItems) {
      if (heldTailEpoch != null) {
        if (heldTailEpoch != structuralTransitionEpoch || disposed) return@submitList
        val acceptedPendingFooter = acceptPendingFooterValues(adjustTailStart = false)
        clearStructuralTail()
        if (acceptedPendingFooter) updateContainerSlots()
        setStructuralSpacerHeight(0)
      }
      relayoutContents()
      if (directDisclosureId != null) recyclerView.post {
        settleCommittedDirectDisclosure(next, directDisclosureId, heldTailEpoch == null)
      }
    }
    bindFooter(next)
  }

  fun reconcileSelection(selectedKeysJson: String) {
    val current = config ?: return
    val array = try {
      JSONArray(selectedKeysJson)
    } catch (_: Exception) {
      return
    }
    val known = current.items.filter { it.isSelectable }.mapTo(HashSet()) { it.key }
    val next = LinkedHashSet<String>()
    for (index in 0 until array.length()) {
      val key = array.optString(index)
      if (!known.contains(key)) return
      next.add(key)
    }
    if (current.selectionMode == "single" && next.size > 1) return
    config = current.copy(selectedKeys = next)
    adapter.selectedKeys = next
    notifySelectionChanged()
  }

  fun scrollToKey(
    key: String,
    animated: Boolean,
    alignment: String,
    viewPosition: Double = 0.0,
    viewOffset: Double = 0.0,
  ) {
    requestScroll(ScrollRequest.Key(key, animated, alignment, viewPosition, viewOffset))
  }

  fun scrollToIndex(
    index: Int,
    animated: Boolean,
    alignment: String,
    viewPosition: Double = 0.0,
    viewOffset: Double = 0.0,
  ) {
    requestScroll(ScrollRequest.Index(index, animated, alignment, viewPosition, viewOffset))
  }

  fun scrollToOffset(offset: Double, animated: Boolean) {
    requestScroll(ScrollRequest.Offset(offset, animated))
  }

  fun scrollToEnd(animated: Boolean) {
    requestScroll(ScrollRequest.End(animated))
  }

  private fun acceptScrollRequest(request: ScrollRequest) {
    if (acceptedScrollRequest === request) return
    val current = config ?: return
    val valid = when (request) {
      is ScrollRequest.Key -> current.items.any { it.key == request.key } && request.viewPosition.isFinite() && request.viewOffset.isFinite()
      is ScrollRequest.Index -> request.index in current.items.indices && request.viewPosition.isFinite() && request.viewOffset.isFinite()
      is ScrollRequest.Offset -> request.offset.isFinite()
      is ScrollRequest.End -> true
    }
    if (!valid) return
    if (structuralTail != null) finishStructuralTail(interrupted = true)
    executingScrollCommand = false
    dispatchingScrollRequest = null
    commandLayoutRequest = null
    commandAlignmentRequest = null
    acceptedScrollRequest = request
    readingIntentEpoch++
    pendingBodyStartEpoch = null
    val explicitTop = when (request) {
      is ScrollRequest.Offset -> request.offset <= 0
      is ScrollRequest.Index -> request.index == 0 && request.alignment == "start" && request.viewOffset == 0.0 && request.viewPosition == 0.0
      is ScrollRequest.Key -> current.items.firstOrNull()?.key == request.key && request.alignment == "start" && request.viewOffset == 0.0 && request.viewPosition == 0.0
      is ScrollRequest.End -> false
    }
    notifyScrollCommand(explicitTop, 0)
  }

  private fun notifyScrollCommand(explicitTop: Boolean, phase: Int) {
    @Suppress("UNCHECKED_CAST")
    val listeners = recyclerView.getTag(R.id.onekey_native_scroll_coordinator_command_listener) as? Map<Any, BiConsumer<Boolean, Int>>
    listeners?.values?.toList()?.forEach { it.accept(explicitTop, phase) }
  }

  private val coordinatorCommandCancelListener = Runnable {
    if (disposed) return@Runnable
    if (structuralTail != null) finishStructuralTail(interrupted = true)
    pendingScrollRequest = null
    readingIntentEpoch++
    pendingBodyStartEpoch = null
    cancelExecutingScrollCommand()
    recyclerView.stopScroll()
  }

  private fun cancelExecutingScrollCommand() {
    val request = acceptedScrollRequest
    val wasExecuting = executingScrollCommand
    executingScrollCommand = false
    acceptedScrollRequest = null
    dispatchingScrollRequest = null
    commandLayoutRequest = null
    commandAlignmentRequest = null
    if (request != null && pendingScrollRequest === request) pendingScrollRequest = null
    if (wasExecuting) notifyScrollCommand(false, 2)
  }

  private fun finishScrollCommandIfSettled() {
    val request = acceptedScrollRequest ?: return
    if (
      !executingScrollCommand || pendingScrollRequest === request ||
      dispatchingScrollRequest === request || commandLayoutRequest === request ||
      commandAlignmentRequest === request || recyclerView.isComputingLayout ||
      recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE || layoutManager.isSmoothScrolling
    ) return
    executingScrollCommand = false
    commandLayoutRequest = null
    commandAlignmentRequest = null
    notifyScrollCommand(false, 2)
  }

  private fun requestScroll(request: ScrollRequest) {
    acceptScrollRequest(request)
    if (config != null && acceptedScrollRequest !== request) return
    if (performScroll(request)) {
      pendingScrollRequest = null
      finishScrollCommandIfSettled()
    } else {
      pendingScrollRequest = request
    }
  }

  private fun performPendingScrollIfNeeded() {
    val request = pendingScrollRequest ?: return
    acceptScrollRequest(request)
    if (config != null && acceptedScrollRequest !== request) {
      if (pendingScrollRequest === request) pendingScrollRequest = null
      return
    }
    if (performScroll(request)) {
      pendingScrollRequest = null
      finishScrollCommandIfSettled()
    }
  }

  private fun performScroll(request: ScrollRequest): Boolean {
    val current = config ?: return false
    if (adapter.itemCount != current.items.size) return false
    if (recyclerView.width <= 0 || recyclerView.height <= 0) return false
    if (acceptedScrollRequest === request) {
      dispatchingScrollRequest = request
      executingScrollCommand = true
      notifyScrollCommand(false, 1)
    }
    try {
      return when (request) {
        is ScrollRequest.Key -> {
          val index = current.items.indexOfFirst { it.key == request.key }
          if (index >= 0) {
            performIndexScroll(
              request,
              index,
              request.animated,
              request.alignment,
              request.viewPosition,
              request.viewOffset,
            )
          }
          true
        }
        is ScrollRequest.Index -> {
          if (request.index in current.items.indices) {
            performIndexScroll(
              request,
              request.index,
              request.animated,
              request.alignment,
              request.viewPosition,
              request.viewOffset,
            )
          }
          true
        }
        is ScrollRequest.Offset -> {
          performOffsetScroll(request, request.offset, request.animated)
          true
        }
        is ScrollRequest.End -> {
          val finalSlotHeight = when {
            sidecarSpacerAdapter.itemCount > 0 -> sidecarSpacerHeightPx
            footerSlotAdapter.itemCount > 0 -> footerPresentedHeightPx()
            emptySlotAdapter.itemCount > 0 -> slotHeights[1]
            adapter.itemCount == 0 && headerSlotAdapter.itemCount > 0 -> slotHeights[0]
            else -> null
          }
          if (finalSlotHeight != null) {
            val target = scrollContentAdapter.itemCount - 1
            if (request.animated) {
              val manager = layoutManager
              val scroller = object : LinearSmoothScroller(context) {
                override fun calculateDyToMakeVisible(view: View, snapPreference: Int): Int =
                  nativeListEndAlignmentOffset(
                    manager.height - manager.paddingBottom,
                    manager.getDecoratedBottom(view),
                  )
              }
              scroller.targetPosition = target
              layoutManager.startSmoothScroll(scroller)
            } else {
              if (acceptedScrollRequest === request) commandLayoutRequest = request
              layoutManager.scrollToPositionWithOffset(
                target,
                nativeListEndAlignmentOffset(viewportLength(layoutManager), finalSlotHeight),
              )
              relayoutRecyclerView()
            }
          } else if (current.items.isNotEmpty()) {
            performIndexScroll(
              request,
              current.items.lastIndex,
              request.animated,
              "end",
              1.0,
              0.0,
            )
          }
          true
        }
      }
    } finally {
      if (dispatchingScrollRequest === request) dispatchingScrollRequest = null
    }
  }

  private fun performIndexScroll(
    request: ScrollRequest,
    index: Int,
    animated: Boolean,
    alignment: String,
    requestedViewPosition: Double,
    viewOffset: Double,
  ) {
    val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
    val visibleView = manager.findViewByPosition(scrollPositionForRow(index))
    val viewPosition = if (alignment == "nearest") {
      val viewportStart = if (manager.orientation == RecyclerView.VERTICAL) {
        manager.paddingTop
      } else {
        manager.paddingLeft
      }
      val viewportEnd = if (manager.orientation == RecyclerView.VERTICAL) {
        manager.height - manager.paddingBottom
      } else {
        manager.width - manager.paddingRight
      }
      if (visibleView != null) {
        val itemStart = decoratedStart(manager, visibleView)
        val itemEnd = decoratedEnd(manager, visibleView)
        when {
          itemStart < viewportStart -> 0.0
          itemEnd > viewportEnd -> 1.0
          else -> return
        }
      } else {
        val firstVisible = rowPositionForScroll(manager.findFirstVisibleItemPosition())
        if (firstVisible != RecyclerView.NO_POSITION && index < firstVisible) 0.0 else 1.0
      }
    } else {
      requestedViewPosition.coerceIn(0.0, 1.0)
    }
    val offsetPx = (viewOffset * density).roundToInt()
    val nearest = alignment == "nearest"

    if (visibleView != null) {
      alignVisibleView(manager, visibleView, viewPosition, offsetPx, animated, nearest)
      return
    }
    if (animated) {
      val smoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        override fun getHorizontalSnapPreference(): Int = SNAP_TO_START

        override fun calculateDyToMakeVisible(view: View, snapPreference: Int): Int {
          if (manager.orientation != RecyclerView.VERTICAL) return 0
          return smoothAlignmentDelta(manager, view, viewPosition, offsetPx, nearest)
        }

        override fun calculateDxToMakeVisible(view: View, snapPreference: Int): Int {
          if (manager.orientation != RecyclerView.HORIZONTAL) return 0
          return smoothAlignmentDelta(manager, view, viewPosition, offsetPx, nearest)
        }
      }
      smoothScroller.targetPosition = scrollPositionForRow(index)
      manager.startSmoothScroll(smoothScroller)
      return
    }

    val layoutOffset = nativeListProvisionalStart(
      viewportLength(manager), viewPosition, offsetPx, nearest,
    )
    if (acceptedScrollRequest === request) {
      commandLayoutRequest = request
      commandAlignmentRequest = request
    }
    manager.scrollToPositionWithOffset(scrollPositionForRow(index), layoutOffset)
    relayoutRecyclerView()
    alignAfterLayout(request, index, viewPosition, offsetPx, nearest)
  }

  private fun performOffsetScroll(request: ScrollRequest, offset: Double, animated: Boolean) {
    val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
    val target = (offset * density).roundToInt().coerceAtLeast(0)
    if (!animated) {
      logicalScrollDistancePx = target
    }
    if (!animated) {
      recyclerView.stopScroll()
      if (acceptedScrollRequest === request) commandLayoutRequest = request
      manager.scrollToPositionWithOffset(0, -target)
      relayoutRecyclerView()
      return
    }
    if (target == 0) {
      val startView = manager.findViewByPosition(0)
      val paddingStart = if (manager.orientation == RecyclerView.VERTICAL) manager.paddingTop else manager.paddingLeft
      val relativeStart = startView?.let { decoratedStart(manager, it) - paddingStart }
      if (
        startView != null && manager.findFirstVisibleItemPosition() == 0 && relativeStart == 0 &&
        recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
        !recyclerView.isComputingLayout && !manager.isSmoothScrolling
      ) {
        return
      }
      recyclerView.smoothScrollToPosition(0)
      return
    }
    val currentOffset = if (manager.orientation == RecyclerView.VERTICAL) {
      recyclerView.computeVerticalScrollOffset()
    } else {
      recyclerView.computeHorizontalScrollOffset()
    }
    val delta = target - currentOffset
    if (manager.orientation == RecyclerView.VERTICAL) {
      recyclerView.smoothScrollBy(0, delta)
    } else {
      recyclerView.smoothScrollBy(delta, 0)
    }
  }

  private fun alignAfterLayout(
    request: ScrollRequest,
    index: Int,
    viewPosition: Double,
    viewOffset: Int,
    nearest: Boolean,
    attemptsRemaining: Int = 2,
  ) {
    recyclerView.post {
      if (acceptedScrollRequest !== request || !executingScrollCommand || disposed || !isAttachedToWindow) return@post
      val manager = recyclerView.layoutManager as? LinearLayoutManager
      val view = manager?.findViewByPosition(scrollPositionForRow(index))
      if (view == null || recyclerView.isComputingLayout) {
        if (attemptsRemaining > 0) {
          alignAfterLayout(request, index, viewPosition, viewOffset, nearest, attemptsRemaining - 1)
          return@post
        }
      } else if (manager != null) {
        dispatchingScrollRequest = request
        try {
          alignVisibleView(manager, view, viewPosition, viewOffset, false, nearest)
        } finally {
          if (dispatchingScrollRequest === request) dispatchingScrollRequest = null
        }
      }
      if (acceptedScrollRequest === request && commandAlignmentRequest === request) {
        commandAlignmentRequest = null
        finishScrollCommandIfSettled()
      }
    }
  }

  private fun alignVisibleView(
    manager: LinearLayoutManager,
    view: View,
    viewPosition: Double,
    viewOffset: Int,
    animated: Boolean,
    nearest: Boolean,
  ) {
    val itemStart = decoratedStart(manager, view)
    val itemLength = decoratedEnd(manager, view) - itemStart
    val viewportStart = if (manager.orientation == RecyclerView.VERTICAL) {
      manager.paddingTop
    } else {
      manager.paddingLeft
    }
    val targetStart = nativeListAlignedStart(
      viewportStart, viewportLength(manager), itemLength, viewPosition, viewOffset, nearest,
    )
    val delta = itemStart - targetStart
    if (manager.orientation == RecyclerView.VERTICAL) {
      if (animated) recyclerView.smoothScrollBy(0, delta) else recyclerView.scrollBy(0, delta)
    } else {
      if (animated) recyclerView.smoothScrollBy(delta, 0) else recyclerView.scrollBy(delta, 0)
    }
  }

  private fun smoothAlignmentDelta(
    manager: LinearLayoutManager,
    view: View,
    viewPosition: Double,
    viewOffset: Int,
    nearest: Boolean,
  ): Int {
    val itemStart = decoratedStart(manager, view)
    val itemLength = decoratedEnd(manager, view) - itemStart
    val viewportStart = if (manager.orientation == RecyclerView.VERTICAL) {
      manager.paddingTop
    } else {
      manager.paddingLeft
    }
    val targetStart = nativeListAlignedStart(
      viewportStart, viewportLength(manager), itemLength, viewPosition, viewOffset, nearest,
    )
    return targetStart - itemStart
  }

  private fun decoratedStart(manager: LinearLayoutManager, view: View): Int =
    if (manager.orientation == RecyclerView.VERTICAL) {
      manager.getDecoratedTop(view)
    } else {
      manager.getDecoratedLeft(view)
    }

  private fun decoratedEnd(manager: LinearLayoutManager, view: View): Int =
    if (manager.orientation == RecyclerView.VERTICAL) {
      manager.getDecoratedBottom(view)
    } else {
      manager.getDecoratedRight(view)
    }

  private fun viewportLength(manager: LinearLayoutManager): Int =
    if (manager.orientation == RecyclerView.VERTICAL) {
      manager.height - manager.paddingTop - manager.paddingBottom
    } else {
      manager.width - manager.paddingLeft - manager.paddingRight
    }

  fun setRefreshing(refreshing: Boolean) {
    refreshLayout.isRefreshing = refreshing
    config = config?.copy(refreshing = refreshing)
  }

  fun setActionAnchorState(stateJson: String) {
    val state = try {
      JSONObject(stateJson)
    } catch (_: Exception) {
      return
    }
    val anchor = actionAnchor ?: return
    if (state.optString("token") != anchor.token || !state.has("open")) return
    if (!state.optBoolean("open")) {
      val sourceView = anchor.sourceView.get()
      if (state.optBoolean("restoreFocus") && isActionAnchorValid(anchor) && sourceView != null) {
        sourceView.requestFocus()
        sourceView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
      }
      actionAnchor = null
      return
    }
    val invalidatedReason = anchor.invalidatedReason
    if (invalidatedReason != null) {
      emitActionAnchorInvalidated(anchor, invalidatedReason)
    } else if (isActionAnchorValid(anchor)) {
      anchor.open = true
    } else {
      emitActionAnchorInvalidated(anchor, "rebind")
    }
  }

  fun dispose() {
    if (disposed) return
    disposed = true
    acceptedKeyIndex = null
    if (structuralTail != null) discardStructuralTail()
    val cancelTag = R.id.onekey_native_scroll_coordinator_command_cancel_listener
    if (recyclerView.getTag(cancelTag) === coordinatorCommandCancelListener) recyclerView.setTag(cancelTag, null)
    containerSlotRelayoutPending = false
    onScrollPositionThresholdChange = null
    if (!isAttachedToWindow) disposeResources()
  }

  private fun disposeResources() {
    if (resourcesDisposed) return
    resourcesDisposed = true
    cancelStructuralTransition()
    recyclerView.itemAnimator = null
    stopSectionIndexAttachmentTracking()
    scheduleSectionIndexAttachmentToList()
    stopReorderRelayoutLoop()
    invalidateActionAnchor("destroy")
    actionAnchor = null
    pendingScrollRequest = null
    visibleEventScheduled = false
    layoutManager.onChildLaidOut = null
    layoutManager.onLayoutPassCompleted = null
    layoutManager.onBeforeDataLayout = null
    pendingBodyStartEpoch = null
    executingScrollCommand = false
    acceptedScrollRequest = null
    dispatchingScrollRequest = null
    commandLayoutRequest = null
    commandAlignmentRequest = null
    measuredRowExtentsPx.clear()
    measuredRowExtentSumPx = 0L
    reorderTouchHandler?.removeCallbacksAndMessages(null)
    reorderTouchHandler = null
    reorderTouchListener?.let(recyclerView::removeOnItemTouchListener)
    reorderTouchListener = null
    recyclerView.removeOnItemTouchListener(keyboardTapTouchListener)
    recyclerView.removeOnItemTouchListener(pagerGestureTouchListener)
    val previousHelper = itemTouchHelper
    itemTouchHelper = null
    if (previousHelper != null && activeReorderHolder != null) {
      val now = SystemClock.uptimeMillis()
      val cancel = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
      try {
        recyclerView.onInterceptTouchEvent(cancel)
      } finally {
        cancel.recycle()
      }
    }
    previousHelper?.attachToRecyclerView(null)
    activeReorderHolder = null
    footerView.recycle()
    footerView.dispose()
    stopStickyHeaderTracking()
    stickyHeaderView.dispose()
    stickyHeaderHost.visibility = GONE
    recyclerView.swapAdapter(null, false)
    adapter.dispose()
  }

  private fun updateLayout(next: NativeListConfig) {
    val orientation = if (next.orientation == "horizontal") RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
    layoutManager.orientation = orientation
    layoutManager.spanCount = if (next.layout == "grid") next.gridColumns else 1
    layoutManager.spanSizeLookup.invalidateSpanIndexCache()
    val defaultPadding = next.contentPadding
    val horizontalPadding = next.contentPaddingHorizontal ?: defaultPadding
    val topPadding = next.contentPaddingTop ?: defaultPadding
    val bottomPadding = next.contentPaddingBottom ?: defaultPadding
    // OneKey patch: the section index overlays rows and keeps only an accessory-safe inset.
    val indexGutter = if (sectionIndexEntries.isEmpty()) 0 else SECTION_INDEX_CONTENT_INSET_DP
    // A native scroll coordinator may add header insets to this RecyclerView.
    // Content/theme snapshots must replace only the padding owned by NativeList.
    val coordinatorTopPadding = (recyclerView.paddingTop - configuredTopPaddingPx).coerceAtLeast(0)
    val coordinatorBottomPadding = (recyclerView.paddingBottom - configuredBottomPaddingPx).coerceAtLeast(0)
    // Slot roots are laid out by React at the unscaled JS inset, without the index gutter.
    slotInsetPx = (horizontalPadding * density).roundToInt()
    val sidecar = sidecarLayout
    val contentWidth = recyclerView.width
    if (sidecar != null && contentWidth > 0) {
      slotInsetPx = (contentWidth - minOf(contentWidth, sidecar.maxContentWidth)) / 2
    }
    val sidecarSpace = (sidecar?.width ?: 0) + (sidecar?.gap ?: 0)
    val baseSlotInset = slotInsetPx
    if (sidecar != null && layoutDirection == LAYOUT_DIRECTION_RTL) {
      slotInsetPx += sidecarSpace
      slotRightInsetPx = baseSlotInset
    } else {
      slotRightInsetPx = baseSlotInset + sidecarSpace
    }
    configuredTopPaddingPx = dp(topPadding)
    configuredBottomPaddingPx = dp(bottomPadding)
    recyclerView.setPaddingRelative(
      if (sidecar == null) dp(horizontalPadding) else baseSlotInset + dp(horizontalPadding),
      configuredTopPaddingPx + coordinatorTopPadding,
      if (sidecar == null) dp(horizontalPadding + indexGutter)
      else baseSlotInset + sidecarSpace + dp(horizontalPadding + indexGutter),
      configuredBottomPaddingPx + coordinatorBottomPadding,
    )
    recyclerView.clipToPadding = false
    recyclerView.isVerticalScrollBarEnabled = sectionIndexEntries.isEmpty()

    spacingDecoration?.let(recyclerView::removeItemDecoration)
    spacingDecoration = ItemSpacingDecoration(dp(next.itemSpacing), next.itemSpacing, density).also(recyclerView::addItemDecoration)
    stickyHeaderSignature = null
    if (!next.stickyHeaders || orientation != RecyclerView.VERTICAL) stickyHeaderHost.visibility = GONE
  }

  private fun updateStickyHeader() {
    val current = config
    if (current == null || !current.stickyHeaders || layoutManager.orientation != RecyclerView.VERTICAL || recyclerView.width <= 0) {
      stickyHeaderHost.visibility = GONE
      return
    }
    val first = rowPositionForScroll(layoutManager.findFirstVisibleItemPosition())
    var position = first
    while (position >= 0 && adapter.itemAt(position)?.let(::isStickySectionHeader) != true) position--
    val item = adapter.itemAt(position)
    if (item == null || first == RecyclerView.NO_POSITION || (layoutManager.findViewByPosition(scrollPositionForRow(position))?.top ?: -1) > 0) {
      stickyHeaderHost.visibility = GONE
      return
    }
    val width = recyclerView.width - recyclerView.paddingLeft - recyclerView.paddingRight
    if (width <= 0) {
      stickyHeaderHost.visibility = GONE
      return
    }
    val signature = "${item.content}:$width:${recyclerView.layoutDirection}:$usesSelectorSourceScale"
    if (stickyHeaderConfig !== current || stickyHeaderSignature != signature) {
      stickyHeaderConfig = current
      stickyHeaderSignature = signature
      stickyHeaderView.listStyle = current.listStyle
      stickyHeaderView.bind(item, current.theme, current.layout, current.orientation, position,
        item.json.optBoolean("selected", false) || current.selectedKeys.contains(item.key), ::resolveCheckboxState,
        useSourceScale = usesSelectorSourceScale)
      stickyHeaderView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
      stickyHeaderHost.layoutParams = (stickyHeaderHost.layoutParams as FrameLayout.LayoutParams).apply {
        this.width = width
        height = stickyHeaderView.measuredHeight
        leftMargin = recyclerView.paddingLeft
        rightMargin = recyclerView.paddingRight
      }
    }
    val height = stickyHeaderView.measuredHeight
    var nextTop = Int.MAX_VALUE
    for (index in 0 until recyclerView.childCount) {
      val child = recyclerView.getChildAt(index)
      val childPosition = rowPositionForScroll(recyclerView.getChildAdapterPosition(child))
      if (childPosition > position && adapter.itemAt(childPosition)?.let(::isStickySectionHeader) == true) nextTop = minOf(nextTop, child.top)
    }
    stickyHeaderHost.translationY = minOf(0, nextTop - height).toFloat()
    stickyHeaderHost.visibility = VISIBLE
    // React Native can retain the measured parent while this overlay becomes
    // visible in pre-draw. Lay out the container-owned header immediately.
    if (stickyHeaderHost.isLayoutRequested || stickyHeaderHost.width != width || stickyHeaderHost.height != height) {
      stickyHeaderHost.measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
      )
      stickyHeaderHost.layout(recyclerView.paddingLeft, 0, recyclerView.paddingLeft + width, height)
    }
  }

  private fun configureSectionIndex(next: NativeListConfig) {
    val previousKey = sectionIndexView.activeIndex?.let { sectionIndexEntries.getOrNull(it) }?.key
    finishSectionIndexInteraction(immediately = true)
    val enabled = next.sectionIndexEnabled &&
      next.layout == "sectioned" &&
      next.orientation != "horizontal"
    sectionIndexEntries = if (enabled) {
      next.items.mapIndexedNotNull { position, item ->
        if (item.type != "sectionHeader") return@mapIndexedNotNull null
        val title = item.json.optString("indexTitle")
        if (title.isEmpty()) null else NativeListSectionIndexEntry(item.key, title, position)
      }
    } else {
      emptyList()
    }
    sectionIndexHapticsEnabled = next.sectionIndexHapticsEnabled
    sectionIndexView.configure(
      sectionIndexEntries.map { it.title },
      themeColor(next.theme, "disabledText", "#8D8D8D"),
      themeColor(next.theme, "positive", "#218358"),
      themeColor(next.theme, "inverseText", "#FCFCFC"),
    )
    sectionIndexView.visibility = if (sectionIndexEntries.isEmpty()) GONE else VISIBLE
    updateSectionIndexAttachment()
    sectionIndexPreview.setTextColor(Color.WHITE)
    sectionIndexPreview.background = sectionIndexPreviewBackground()
    sectionIndexView.setActiveIndex(
      previousKey?.let { key -> sectionIndexEntries.indexOfFirst { it.key == key }.takeIf { it >= 0 } },
    )
  }

  private fun selectSectionIndex(index: Int, interacting: Boolean) {
    val entry = sectionIndexEntries.getOrNull(index) ?: return
    val changed = sectionIndexView.activeIndex != index
    sectionIndexScrubbing = interacting
    sectionIndexProgrammaticScroll = true
    sectionIndexView.setActiveIndex(index)
    recyclerView.stopScroll()
    scrollToIndex(entry.position, animated = false, alignment = "start")
    recyclerView.post {
      sectionIndexProgrammaticScroll = false
      syncSectionIndexToVisibleRows()
    }
    if (interacting) {
      sectionIndexPreview.animate().cancel()
      sectionIndexPreview.text = entry.title
      positionSectionIndexPreview(index)
      sectionIndexPreview.visibility = VISIBLE
      sectionIndexPreview.alpha = 1f
      if (changed && sectionIndexHapticsEnabled) {
        sectionIndexView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
      }
    }
  }

  private fun finishSectionIndexInteraction(immediately: Boolean = false) {
    sectionIndexScrubbing = false
    if (immediately) sectionIndexProgrammaticScroll = false
    syncSectionIndexToVisibleRows()
    sectionIndexPreview.animate().cancel()
    if (immediately) {
      sectionIndexPreview.alpha = 0f
      sectionIndexPreview.visibility = INVISIBLE
    } else {
      sectionIndexPreview.animate()
        .alpha(0f)
        .setDuration(150)
        .withEndAction { sectionIndexPreview.visibility = INVISIBLE }
        .start()
    }
  }

  private fun positionSectionIndexPreview(index: Int) {
    val halfHeight = sectionIndexDp(SECTION_INDEX_PREVIEW_HEIGHT_DP) / 2f
    val maximumY = (contentContainer.height - halfHeight).coerceAtLeast(halfHeight)
    sectionIndexView.getLocationOnScreen(sectionIndexLocationOnScreen)
    contentContainer.getLocationOnScreen(sectionIndexHostLocationOnScreen)
    val targetY = sectionIndexLocationOnScreen[1] - sectionIndexHostLocationOnScreen[1] +
      sectionIndexView.centerYForIndex(index)
    val clampedY = targetY.coerceIn(halfHeight, maximumY)
    sectionIndexPreview.translationY = clampedY - contentContainer.height / 2f
  }

  private fun updateSectionIndexAttachment() {
    val windowHost = rootView as? FrameLayout
    val canUseWindowHost = config?.sectionIndexCenteredInWindow == true &&
      sectionIndexEntries.isNotEmpty() &&
      isAttachedToWindow &&
      width > 0 &&
      height > 0 &&
      windowHost != null &&
      windowHost.width > 0 &&
      windowHost.height > 0 &&
      windowHost !== contentContainer
    if (canUseWindowHost) {
      startSectionIndexAttachmentTracking()
    } else {
      stopSectionIndexAttachmentTracking()
    }
    val useWindowHost = canUseWindowHost && isShown
    if (useWindowHost) {
      attachSectionIndexToWindow(windowHost)
    } else {
      attachSectionIndexToList()
    }
  }

  private fun startStickyHeaderTracking() {
    if (resourcesDisposed) return
    val observer = viewTreeObserver
    if (stickyObservedTree === observer) return
    stopStickyHeaderTracking()
    observer.addOnPreDrawListener(stickyPreDraw)
    stickyObservedTree = observer
  }

  private fun stopStickyHeaderTracking() {
    stickyObservedTree?.takeIf(ViewTreeObserver::isAlive)?.removeOnPreDrawListener(stickyPreDraw)
    stickyObservedTree = null
  }

  private fun startSectionIndexAttachmentTracking() {
    val observer = viewTreeObserver
    if (sectionIndexObservedTree === observer) return
    sectionIndexObservedTree?.takeIf(ViewTreeObserver::isAlive)
      ?.removeOnPreDrawListener(sectionIndexPreDrawListener)
    observer.addOnPreDrawListener(sectionIndexPreDrawListener)
    sectionIndexObservedTree = observer
  }

  private fun stopSectionIndexAttachmentTracking() {
    sectionIndexObservedTree?.takeIf(ViewTreeObserver::isAlive)
      ?.removeOnPreDrawListener(sectionIndexPreDrawListener)
    sectionIndexObservedTree = null
  }

  private fun scheduleSectionIndexAttachmentToList() {
    if (sectionIndexView.parent === contentContainer) return
    if (sectionIndexListAttachmentScheduled) return
    sectionIndexListAttachmentScheduled = true
    sectionIndexAttachmentHandler.post(sectionIndexListAttachmentRunnable)
  }

  private fun attachSectionIndexToList() {
    if (sectionIndexView.parent === contentContainer) return
    (sectionIndexView.parent as? ViewGroup)?.let { previous ->
      previous.removeView(sectionIndexView)
      previous.endViewTransition(sectionIndexView)
    }
    sectionIndexView.translationX = 0f
    contentContainer.addView(
      sectionIndexView,
      FrameLayout.LayoutParams(
        sectionIndexDp(SECTION_INDEX_RAIL_WIDTH_DP),
        FrameLayout.LayoutParams.MATCH_PARENT,
        Gravity.END,
      ),
    )
  }

  private fun attachSectionIndexToWindow(windowHost: FrameLayout) {
    val railWidth = sectionIndexDp(SECTION_INDEX_RAIL_WIDTH_DP)
    val railHeight = sectionIndexView.preferredHeight(windowHost.height)
    windowHost.getLocationOnScreen(sectionIndexHostLocationOnScreen)
    getLocationOnScreen(sectionIndexLocationOnScreen)
    val listLeft = sectionIndexLocationOnScreen[0] - sectionIndexHostLocationOnScreen[0]
    val targetRailLeft = if (layoutDirection == LAYOUT_DIRECTION_RTL) {
      listLeft
    } else {
      listLeft + width - railWidth
    }
    val restingRailLeft = if (layoutDirection == LAYOUT_DIRECTION_RTL) {
      0
    } else {
      windowHost.width - railWidth
    }
    val layoutParams = FrameLayout.LayoutParams(
      railWidth,
      railHeight,
      Gravity.TOP or Gravity.END,
    ).apply {
      topMargin = (windowHost.height - railHeight) / 2
    }
    if (sectionIndexView.parent !== windowHost) {
      (sectionIndexView.parent as? ViewGroup)?.let { previous ->
        previous.removeView(sectionIndexView)
        previous.endViewTransition(sectionIndexView)
      }
      windowHost.addView(sectionIndexView, layoutParams)
      sectionIndexView.bringToFront()
    } else if (!sectionIndexView.hasSameWindowLayout(layoutParams)) {
      sectionIndexView.layoutParams = layoutParams
    }
    sectionIndexView.translationX = (targetRailLeft - restingRailLeft).toFloat()
  }

  private fun View.hasSameWindowLayout(next: FrameLayout.LayoutParams): Boolean {
    val current = layoutParams as? FrameLayout.LayoutParams ?: return false
    return current.width == next.width &&
      current.height == next.height &&
      current.gravity == next.gravity &&
      current.marginEnd == next.marginEnd &&
      current.topMargin == next.topMargin
  }

  private fun sectionIndexPreviewBackground(): ShapeDrawable {
    val path = Path().apply {
      moveTo(25f, 0f)
      cubicTo(11f, 0f, 0f, 11f, 0f, 25f)
      cubicTo(0f, 39f, 11f, 50f, 25f, 50f)
      cubicTo(36f, 50f, 43f, 44f, 46f, 37.5f)
      lineTo(60f, 25f)
      lineTo(46f, 12.5f)
      cubicTo(43f, 6f, 36f, 0f, 25f, 0f)
      close()
    }
    return ShapeDrawable(PathShape(path, 60f, 50f)).apply {
      paint.color = Color.rgb(194, 194, 194)
      paint.isAntiAlias = true
    }
  }

  private fun syncSectionIndexToVisibleRows() {
    if (sectionIndexScrubbing || sectionIndexProgrammaticScroll || sectionIndexEntries.isEmpty()) return
    val firstVisible = rowPositionForScroll(layoutManager.findFirstVisibleItemPosition())
    if (firstVisible == RecyclerView.NO_POSITION) return
    sectionIndexView.setActiveIndex(
      sectionIndexEntries.indexOfLast { it.position <= firstVisible }.takeIf { it >= 0 },
    )
  }

  private fun themeColor(theme: JSONObject?, key: String, fallback: String): Int = try {
    parseNativeListColor(theme?.optString(key)?.takeIf(String::isNotEmpty) ?: fallback)
  } catch (_: IllegalArgumentException) {
    parseNativeListColor(fallback)
  }

  private fun bindFooter(next: NativeListConfig) {
    val footer = next.fixedFooter
    if (footer == null) {
      footerView.visibility = GONE
      footerView.recycle()
    } else {
      if (footer.rendererKey != footerRendererKey) {
        val previous = footerView
        val position = indexOfChild(previous)
        previous.recycle()
        previous.dispose()
        footerView = NativeListRendererRegistry.create(reactContext, footer.rendererKey.ordinal, fonts)
        footerView.onRowPress = ::handleRowPress
        footerView.onAction = ::handleAction
        footerView.onBindingInvalidated = ::handleBindingInvalidated
        removeView(previous)
        addView(footerView, position, previous.layoutParams)
        footerRendererKey = footer.rendererKey
      }
      footerView.visibility = VISIBLE
      footerView.listStyle = next.listStyle
      footerView.bind(
        footer,
        next.theme,
        next.layout,
        "vertical",
        null,
        next.selectedKeys.contains(footer.key),
        ::resolveCheckboxState,
        useSourceScale = usesSelectorSourceScale,
      )
    }
  }

  private fun handleRowPress(item: NativeListItem, origin: NativeListActionOrigin) {
    val current = config ?: return
    if (!item.isRowPressEnabled) return
    if (current.rowPressToggles && item.isSelectable && current.selectionMode != "none") {
      updateSelection(NativeSelectionTarget("row", item.key), item.key)
    } else {
      val actionKey = when {
        item.type == "market" && item.json.optString("pressActionKey").isNotEmpty() -> item.json.optString("pressActionKey")
        item.type == "action" -> item.json.optString("actionKey")
        item.type == "system" && item.json.optString("variant") == "retry" -> item.json.optString("actionKey")
        else -> "press"
      }
      val payload = JSONObject()
        .put("rowKey", item.key)
        .put("actionKey", actionKey)
      item.sectionKey?.let { payload.put("sectionKey", it) }
      createActionAnchor(origin)?.let { payload.put("anchor", it) }
      emit(ROW_ACTION, payload)
    }
  }

  private fun handleAction(
    item: NativeListItem,
    actionKey: String,
    target: NativeSelectionTarget?,
    origin: NativeListActionOrigin?,
  ) {
    if (item.json.optBoolean("disabled", false)) return
    if (target != null && config?.selectionMode != "none") {
      updateSelection(target, item.key)
      return
    }
    val payload = JSONObject()
      .put("rowKey", item.key)
      .put("actionKey", actionKey)
    item.sectionKey?.let { payload.put("sectionKey", it) }
    origin?.let(::createActionAnchor)?.let { payload.put("anchor", it) }
    emit(ROW_ACTION, payload)
  }

  private fun createActionAnchor(origin: NativeListActionOrigin): JSONObject? {
    if (!isOriginValid(origin)) return null
    invalidateActionAnchor("rebind")
    actionAnchorCounter += 1
    val generation = config?.generation ?: 0
    val token = "$actionAnchorInstanceId:$generation:$actionAnchorCounter:${origin.bindingEpoch}"
    val location = IntArray(2)
    origin.sourceView.getLocationInWindow(location)
    val record = ActionAnchorRecord(token, origin)
    actionAnchor = record
    // OneKey patch: selector menus anchor to the glyph slot, not its expanded press target.
    val anchorInset = origin.anchorInsetPixels
    val helpBounds = (origin.sourceView as? NativeListTextView)?.helpAnchorBounds
    return JSONObject()
      .put("token", token)
      .put(
        "windowRect",
        JSONObject()
          .put("x", (location[0] + (helpBounds?.left ?: anchorInset.toFloat())) / density)
          .put("y", (location[1] + (helpBounds?.top ?: anchorInset.toFloat())) / density)
          .put("width", (helpBounds?.width() ?: (origin.sourceView.width - anchorInset * 2).toFloat()) / density)
          .put("height", (helpBounds?.height() ?: (origin.sourceView.height - anchorInset * 2).toFloat()) / density),
      )
      .put("source", origin.source)
      .put("generation", generation)
      .put("layoutDirection", if (origin.sourceView.layoutDirection == LAYOUT_DIRECTION_RTL) "rtl" else "ltr")
      .also { anchor ->
        origin.slot?.let { anchor.put("slot", it) }
        origin.windowPointPixels?.let { point ->
          anchor.put("windowPoint", JSONObject().put("x", point.x / density).put("y", point.y / density))
        }
      }
  }

  private fun isOriginValid(origin: NativeListActionOrigin): Boolean =
    origin.ownerRowView.bindingEpoch == origin.bindingEpoch &&
      origin.sourceView.isAttachedToWindow &&
      isDescendantOf(origin.sourceView, origin.ownerRowView)

  private fun isActionAnchorValid(anchor: ActionAnchorRecord): Boolean {
    if (anchor.invalidatedReason != null) return false
    val sourceView = anchor.sourceView.get() ?: return false
    val ownerRowView = anchor.ownerRowView.get() ?: return false
    return ownerRowView.bindingEpoch == anchor.bindingEpoch &&
      sourceView.isAttachedToWindow &&
      isDescendantOf(sourceView, ownerRowView)
  }

  private fun isDescendantOf(view: View, ancestor: View): Boolean {
    var current: Any? = view
    while (current is View) {
      if (current === ancestor) return true
      current = current.parent
    }
    return false
  }

  private fun handleBindingInvalidated(row: NativeListRowHost, epoch: Long) {
    val anchor = actionAnchor ?: return
    if (anchor.ownerRowView.get() === row && anchor.bindingEpoch == epoch) {
      invalidateActionAnchor("rebind")
    }
  }

  private fun invalidateActionAnchor(reason: String) {
    val anchor = actionAnchor ?: return
    if (anchor.invalidatedReason != null) return
    anchor.invalidatedReason = reason
    if (anchor.open) emitActionAnchorInvalidated(anchor, reason)
  }

  private fun emitActionAnchorInvalidated(anchor: ActionAnchorRecord, reason: String) {
    if (actionAnchor !== anchor) return
    actionAnchor = null
    emit(
      ACTION_ANCHOR_INVALIDATED,
      JSONObject().put("token", anchor.token).put("reason", reason),
    )
  }

  private fun updateSelection(target: NativeSelectionTarget, sourceKey: String) {
    val current = config ?: return
    if (current.selectionMode == "none") return
    val targets = selectionKeys(target, current.items)
    if (targets.isEmpty()) return
    val before = LinkedHashSet(current.selectedKeys)
    val after = LinkedHashSet(before)
    if (current.selectionMode == "single") {
      val key = targets.first()
      after.clear()
      if (!before.contains(key)) after.add(key)
    } else {
      val allSelected = targets.all(before::contains)
      targets.forEach { if (allSelected) after.remove(it) else after.add(it) }
    }
    if (before == after) return
    config = current.copy(selectedKeys = after)
    adapter.selectedKeys = after
    notifySelectionChanged()

    val added = after.filterNot(before::contains)
    val removed = before.filterNot(after::contains)
    val payload = JSONObject()
      .put("addedKeys", JSONArray(added))
      .put("removedKeys", JSONArray(removed))
      .put("source", target.scope)
      .put("sourceKey", target.key ?: sourceKey)
    emit(SELECTION_DELTA, payload)
  }

  private fun selectionKeys(
    target: NativeSelectionTarget,
    items: List<NativeListItem>,
  ): List<String> = when (target.scope) {
    "row" -> items.filter { it.key == target.key && it.isSelectable }.map { it.key }
    "section" -> items.filter { it.sectionKey == target.key && it.isSelectable }.map { it.key }
    "list" -> items.filter { it.isSelectable }.map { it.key }
    else -> emptyList()
  }

  private fun resolveCheckboxState(
    item: NativeListItem,
    target: NativeSelectionTarget?,
    fallback: String,
  ): String {
    val current = config ?: return fallback
    val resolvedTarget = target ?: NativeSelectionTarget("row", item.key)
    val keys = selectionKeys(resolvedTarget, current.items)
    if (keys.isEmpty()) return fallback
    val count = keys.count(current.selectedKeys::contains)
    return when {
      count == 0 -> "unchecked"
      count == keys.size -> "checked"
      else -> "indeterminate"
    }
  }

  private fun notifySelectionChanged() {
    adapter.notifyItemRangeChanged(0, adapter.itemCount, SELECTION_PAYLOAD)
    recyclerView.post { bindVisibleSelection() }
    bindFooterSelection()
  }

  private fun bindVisibleSelection(changedSummaryKeys: Set<String> = emptySet()) {
    val current = config ?: return
    for (index in 0 until recyclerView.childCount) {
      val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(index)) as? NativeListViewHolder
        ?: continue
      val boundKey = (holder.rowView.tag as? NativeListItem)?.key ?: continue
      val position = adapter.positionOfKey(boundKey)
      val item = adapter.itemAt(position) ?: continue
      if (item.key in changedSummaryKeys) holder.rowView.bindStableSummary(item)
      holder.rowView.bindSelection(
        item,
        current.theme,
        current.layout,
        position,
        // OneKey patch: match the full binder when the snapshot carries explicit row selection.
        // current.selectedKeys.contains(item.key),
        item.json.optBoolean("selected", false) || current.selectedKeys.contains(item.key),
        ::resolveCheckboxState,
      )
    }
  }

  private fun bindFooterSelection() {
    config?.let { current ->
      current.fixedFooter?.let { footer ->
        footerView.bindSelection(
          footer,
          current.theme,
          current.layout,
          null,
          current.selectedKeys.contains(footer.key),
          ::resolveCheckboxState,
        )
      }
    }
  }

  private fun updateReordering(next: NativeListConfig) {
    dragFrom = RecyclerView.NO_POSITION
    dragTo = RecyclerView.NO_POSITION
    pendingReorder = null
    reorderPlaceholderDecoration.position = RecyclerView.NO_POSITION
    stopReorderRelayoutLoop()
    reorderTouchHandler?.removeCallbacksAndMessages(null)
    reorderTouchHandler = null
    reorderTouchListener?.let(recyclerView::removeOnItemTouchListener)
    reorderTouchListener = null
    val previousHelper = itemTouchHelper
    itemTouchHelper = null
    if (previousHelper != null && activeReorderHolder != null) {
      // Detach alone leaves ItemTouchHelper's selection/auto-scroll runnable alive.
      val now = SystemClock.uptimeMillis()
      val cancel = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
      try {
        recyclerView.onInterceptTouchEvent(cancel)
      } finally {
        cancel.recycle()
      }
    }
    previousHelper?.attachToRecyclerView(null)
    val rows = mutableSetOf<NativeListViewHolder>()
    activeReorderHolder?.let(rows::add)
    activeReorderHolder = null
    for (index in 0 until recyclerView.childCount) {
      (recyclerView.getChildViewHolder(recyclerView.getChildAt(index)) as? NativeListViewHolder)
        ?.let(rows::add)
    }
    rows.forEach { holder ->
      holder.itemView.animate().cancel()
      ItemTouchHelper.Callback.getDefaultUIUtil().clearView(holder.itemView)
      holder.rowView.setReorderActive(false)
    }
    recyclerView.invalidate()
    adapter.cancelReorder()
    if (!next.reorderable) return
    lateinit var helper: ItemTouchHelper
    val callback = object : ItemTouchHelper.SimpleCallback(
      ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
      0,
    ) {
      private var compactWalletGroupDrag = false
      private var compactWalletGroupTop = Float.NaN
      private var dragType: String? = null
      private var dragStartedAtMs = 0L
      private var lastAutoScrollLogAtMs = 0L
      private var lastDragFrameLogAtMs = 0L
      private var lastMoveAttemptLogAtMs = 0L
      private var lastMoveAttemptSignature = ""
      private var latestDragDx = 0f
      private var latestDragDy = 0f

      private fun logMoveAttempt(
        recyclerView: RecyclerView,
        from: Int,
        to: Int,
        fromItem: NativeListItem?,
        toItem: NativeListItem?,
        outcome: String,
        targetTop: Int,
        targetBottom: Int,
      ) {
        val now = SystemClock.uptimeMillis()
        val signature = "$from:$to:$outcome"
        if (
          signature == lastMoveAttemptSignature &&
          now - lastMoveAttemptLogAtMs < REORDER_MOVE_ATTEMPT_LOG_INTERVAL_MS
        ) {
          return
        }
        lastMoveAttemptSignature = signature
        lastMoveAttemptLogAtMs = now
        OneKeyLog.info(
          REORDER_LOG_TAG,
          "moveAttempt outcome=$outcome from=$from to=$to " +
            "fromType=${fromItem?.type.orEmpty()} toType=${toItem?.type.orEmpty()} " +
            "fromReorderable=${fromItem?.isReorderable} toReorderable=${toItem?.isReorderable} " +
            "sameSection=${fromItem != null && toItem != null && fromItem.sectionKey == toItem.sectionKey} " +
            "elapsedDragMs=${now - dragStartedAtMs} " +
            "scrollOffset=${recyclerView.computeVerticalScrollOffset()} " +
            "targetTop=$targetTop targetBottom=$targetBottom " +
            "viewportTop=${recyclerView.paddingTop} " +
            "viewportBottom=${recyclerView.height - recyclerView.paddingBottom}",
        )
      }

      override fun isLongPressDragEnabled(): Boolean = false

      override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (itemTouchHelper !== helper) return
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
          activeReorderHolder = viewHolder as? NativeListViewHolder
          val position = viewHolder?.bindingAdapterPosition ?: RecyclerView.NO_POSITION
          val item = adapter.itemAt(position)
          reorderPlaceholderDecoration.position = position
          reorderPlaceholderDecoration.color = reorderActiveBackground(next.theme)
          compactWalletGroupDrag = item?.type == "walletGroup" && viewHolder != null
          dragType = item?.type
          dragStartedAtMs = SystemClock.uptimeMillis()
          lastAutoScrollLogAtMs = 0L
          lastDragFrameLogAtMs = 0L
          lastMoveAttemptLogAtMs = 0L
          lastMoveAttemptSignature = ""
          latestDragDx = 0f
          latestDragDy = 0f
          startReorderRelayoutLoop()
          OneKeyLog.info(
            REORDER_LOG_TAG,
            "start type=${dragType.orEmpty()} position=$position " +
              "compact=$compactWalletGroupDrag itemHeight=${viewHolder?.itemView?.height ?: -1} " +
              "viewport=${recyclerView.width}x${recyclerView.height} " +
              "padding=${recyclerView.paddingLeft},${recyclerView.paddingTop}," +
              "${recyclerView.paddingRight},${recyclerView.paddingBottom} density=$density",
          )
          recyclerView.invalidate()
          (viewHolder as? NativeListViewHolder)?.rowView?.setReorderActive(true)
          if (compactWalletGroupDrag && viewHolder != null) {
            recyclerView.postOnAnimation {
              if (itemTouchHelper === helper && viewHolder.itemView.isAttachedToWindow) {
                relayoutRecyclerViewImmediately()
              }
            }
          }
        }
      }

      override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (itemTouchHelper !== helper) return makeMovementFlags(0, 0)
        if (viewHolder !is NativeListViewHolder) return makeMovementFlags(0, 0)
        val item = adapter.itemAt(viewHolder.bindingAdapterPosition)
        if (item == null || !item.isReorderable) return makeMovementFlags(0, 0)
        val dragFlags = if (next.orientation == "horizontal") ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT else ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
      }

      override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
      ): Boolean {
        if (itemTouchHelper !== helper) return false
        if (source !is NativeListViewHolder || target !is NativeListViewHolder) return false
        val from = source.bindingAdapterPosition
        val to = target.bindingAdapterPosition
        val base = pendingReorder ?: adapter.currentList
        val fromItem = base.getOrNull(from)
        val toItem = base.getOrNull(to)
        val targetTop = layoutManager.getDecoratedTop(target.itemView)
        val targetBottom = layoutManager.getDecoratedBottom(target.itemView)
        val targetClipped =
          next.orientation != "horizontal" &&
            from != RecyclerView.NO_POSITION &&
            to != RecyclerView.NO_POSITION &&
            when {
              to > from -> targetBottom > recyclerView.height - recyclerView.paddingBottom
              to < from -> targetTop < recyclerView.paddingTop
              else -> false
            }
        val outcome = when {
          fromItem == null -> "missingSource"
          toItem == null -> "missingTarget"
          !fromItem.isReorderable -> "sourceNotReorderable"
          !toItem.isReorderable -> "targetNotReorderable"
          fromItem.sectionKey != toItem.sectionKey -> "sectionMismatch"
          targetClipped -> "targetClipped"
          else -> "accepted"
        }
        logMoveAttempt(recyclerView, from, to, fromItem, toItem, outcome, targetTop, targetBottom)
        if (outcome != "accepted" || fromItem == null || toItem == null) return false
        val crossedPosition = dragTo != to
        if (dragFrom == RecyclerView.NO_POSITION) dragFrom = from
        dragTo = to
        if (crossedPosition) {
          recyclerView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
          OneKeyLog.info(
            REORDER_LOG_TAG,
            "move type=${dragType.orEmpty()} from=$from to=$to " +
              "scrollOffset=${recyclerView.computeVerticalScrollOffset()} " +
              "dragDx=$latestDragDx dragDy=$latestDragDy",
          )
        }
        val displacedView = target.itemView
        prepareDisplacedReorderAnimation(displacedView, helper)
        val reordered = adapter.moveReordered(from, to)
        if (reordered == null) {
          logMoveAttempt(
            recyclerView,
            from,
            to,
            fromItem,
            toItem,
            "adapterRejected",
            targetTop,
            targetBottom,
          )
          return false
        }
        pendingReorder = reordered
        reorderPlaceholderDecoration.position = to
        recyclerView.invalidate()
        // OneKey patch: the drag-scoped loop handles deferred nested RecyclerView layouts.
        // recyclerView.postOnAnimation(::relayoutRecyclerViewImmediately)
        return true
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

      override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
      ) {
        if (itemTouchHelper !== helper) return
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
          latestDragDx = dX
          latestDragDy = dY
          if (compactWalletGroupDrag) {
            compactWalletGroupTop = viewHolder.itemView.top + dY
          }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
          val now = SystemClock.uptimeMillis()
          if (
            lastDragFrameLogAtMs == 0L ||
            now - lastDragFrameLogAtMs >= REORDER_DRAG_FRAME_LOG_INTERVAL_MS
          ) {
            lastDragFrameLogAtMs = now
            val itemView = viewHolder.itemView
            val visualTop = itemView.top + dY
            val visualBottom = itemView.bottom + dY
            OneKeyLog.info(
              REORDER_LOG_TAG,
              "frame position=${viewHolder.bindingAdapterPosition} active=$isCurrentlyActive " +
                "dragDx=$dX dragDy=$dY itemTop=${itemView.top} itemBottom=${itemView.bottom} " +
                "translationY=${itemView.translationY} visualTop=$visualTop visualBottom=$visualBottom " +
                "viewportTop=${recyclerView.paddingTop} " +
                "viewportBottom=${recyclerView.height - recyclerView.paddingBottom} " +
                "firstVisible=${layoutManager.findFirstVisibleItemPosition()} " +
                "lastVisible=${layoutManager.findLastVisibleItemPosition()} " +
                "canScrollUp=${recyclerView.canScrollVertically(-1)} " +
                "canScrollDown=${recyclerView.canScrollVertically(1)} " +
                "scrollOffset=${recyclerView.computeVerticalScrollOffset()} " +
                "layoutRequested=${recyclerView.isLayoutRequested} " +
                "computingLayout=${recyclerView.isComputingLayout}",
            )
          }
        }
      }

      override fun interpolateOutOfBoundsScroll(
        recyclerView: RecyclerView,
        viewSize: Int,
        viewSizeOutOfBounds: Int,
        totalSize: Int,
        msSinceStartScroll: Long,
      ): Int {
        if (itemTouchHelper !== helper) return 0
        val effectiveViewSize: Int
        val effectiveOutOfBounds: Int
        if (compactWalletGroupDrag) {
          if (compactWalletGroupTop.isNaN()) return 0
          effectiveViewSize = dp(68)
          val compactTop = compactWalletGroupTop.roundToInt()
          effectiveOutOfBounds = when {
            compactTop < recyclerView.paddingTop -> compactTop - recyclerView.paddingTop
            compactTop + effectiveViewSize > recyclerView.height - recyclerView.paddingBottom ->
              compactTop + effectiveViewSize - (recyclerView.height - recyclerView.paddingBottom)
            else -> 0
          }
          if (effectiveOutOfBounds == 0) return 0
        } else {
          effectiveViewSize = viewSize
          effectiveOutOfBounds = viewSizeOutOfBounds
        }
        // OneKey patch: warm-start AndroidX's two-second quintic edge-scroll ramp.
        val acceleratedElapsedOutMs =
          msSinceStartScroll + REORDER_AUTOSCROLL_ACCELERATION_OFFSET_MS
        val result = super.interpolateOutOfBoundsScroll(
          recyclerView,
          effectiveViewSize,
          effectiveOutOfBounds,
          totalSize,
          // msSinceStartScroll,
          acceleratedElapsedOutMs,
        )
        val now = SystemClock.uptimeMillis()
        if (
          lastAutoScrollLogAtMs == 0L ||
          now - lastAutoScrollLogAtMs >= REORDER_AUTOSCROLL_LOG_INTERVAL_MS
        ) {
          lastAutoScrollLogAtMs = now
          OneKeyLog.info(
            REORDER_LOG_TAG,
            "autoScroll type=${dragType.orEmpty()} " +
              "compact=$compactWalletGroupDrag rawViewSize=$viewSize " +
              "effectiveViewSize=$effectiveViewSize rawOut=$viewSizeOutOfBounds " +
              "effectiveOut=$effectiveOutOfBounds totalSize=$totalSize " +
              "elapsedOutMs=$msSinceStartScroll " +
              "acceleratedElapsedOutMs=$acceleratedElapsedOutMs " +
              "elapsedDragMs=${now - dragStartedAtMs} " +
              "resultPx=$result scrollOffset=${recyclerView.computeVerticalScrollOffset()} " +
              "canScrollUp=${recyclerView.canScrollVertically(-1)} " +
              "canScrollDown=${recyclerView.canScrollVertically(1)} " +
              "dragDx=$latestDragDx dragDy=$latestDragDy compactTop=$compactWalletGroupTop",
          )
        }
        return result
      }

      override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // Cancellation already restored rows before a replacement helper takes ownership.
        if (itemTouchHelper !== helper) return
        if (activeReorderHolder === viewHolder) activeReorderHolder = null
        stopReorderRelayoutLoop()
        val rowView = (viewHolder as? NativeListViewHolder)?.rowView
        val draggedGroupKey = (rowView?.tag as? NativeListItem)?.key
        super.clearView(recyclerView, viewHolder)
        val wasCompactWalletGroupDrag = compactWalletGroupDrag
        if (!wasCompactWalletGroupDrag) rowView?.setReorderActive(false)
        reorderPlaceholderDecoration.position = RecyclerView.NO_POSITION
        compactWalletGroupDrag = false
        compactWalletGroupTop = Float.NaN
        recyclerView.invalidate()
        val from = dragFrom
        val to = dragTo
        val reordered = pendingReorder
        OneKeyLog.info(
          REORDER_LOG_TAG,
          "end type=${dragType.orEmpty()} from=$from to=$to " +
            "compact=$wasCompactWalletGroupDrag elapsedDragMs=" +
            "${SystemClock.uptimeMillis() - dragStartedAtMs} " +
            "scrollOffset=${recyclerView.computeVerticalScrollOffset()} " +
            "dragDx=$latestDragDx dragDy=$latestDragDy " +
            "firstVisible=${layoutManager.findFirstVisibleItemPosition()} " +
            "lastVisible=${layoutManager.findLastVisibleItemPosition()} " +
            "canScrollUp=${recyclerView.canScrollVertically(-1)} " +
            "canScrollDown=${recyclerView.canScrollVertically(1)}",
        )
        var reorderPayload: JSONObject? = null
        val destinationPosition = if (to != RecyclerView.NO_POSITION) {
          to
        } else {
          viewHolder.bindingAdapterPosition
        }
        val finishCompactWalletGroup = {
          if (wasCompactWalletGroupDrag) {
            recyclerView.postOnAnimation {
              if (itemTouchHelper !== helper) return@postOnAnimation
              relayoutRecyclerViewImmediately()
              val resolvedPosition = draggedGroupKey
                ?.let(adapter::positionOfKey)
                ?.takeIf { it != RecyclerView.NO_POSITION }
                ?: destinationPosition
              val destinationRow = recyclerView
                .findViewHolderForAdapterPosition(scrollPositionForRow(resolvedPosition))
                ?.let { it as? NativeListViewHolder }
                ?.rowView
              val groupRow = destinationRow?.takeIf {
                (it.tag as? NativeListItem)?.type == "walletGroup"
              } ?: rowView
              groupRow?.finishWalletGroupReorder(
                REORDER_SPRING_DURATION_MS,
                reorderSpringInterpolator,
              ) {
                recyclerView.postOnAnimation {
                  if (itemTouchHelper === helper) relayoutRecyclerViewImmediately()
                }
              }
            }
          }
        }
        if (from != RecyclerView.NO_POSITION && to != RecyclerView.NO_POSITION && from != to && reordered != null) {
          val moved = reordered.getOrNull(to)
          if (moved != null) {
            acceptedKeyIndex = reordered.withIndex().associate { it.value.key to it.index }
            config = config?.copy(items = reordered)
            val payload = JSONObject()
              .put("key", moved.key)
              .put("fromIndex", from)
              .put("toIndex", to)
            reordered.getOrNull(to - 1)?.let { payload.put("beforeKey", it.key) }
            reordered.getOrNull(to + 1)?.let { payload.put("afterKey", it.key) }
            reorderPayload = payload
            adapter.commitReordered(reordered) {
              if (itemTouchHelper !== helper) return@commitReordered
              finishCompactWalletGroup()
              if (!wasCompactWalletGroupDrag) {
                recyclerView.postOnAnimation {
                  if (itemTouchHelper === helper) relayoutRecyclerViewImmediately()
                }
              }
            }
          }
        } else {
          adapter.cancelReorder()
          finishCompactWalletGroup()
        }
        dragFrom = RecyclerView.NO_POSITION
        dragTo = RecyclerView.NO_POSITION
        pendingReorder = null
        dragType = null
        // The gesture is committed now; a later snapshot may supersede its async diff.
        reorderPayload?.let { emit(REORDER, it) }
      }
    }
    helper = ItemTouchHelper(callback)
    itemTouchHelper = helper
    helper.attachToRecyclerView(recyclerView)
    val handler = Handler(Looper.getMainLooper())
    reorderTouchHandler = handler
    val movementLimit = dp(REORDER_ALLOWABLE_MOVEMENT_DP).toFloat()
    reorderTouchListener = object : RecyclerView.SimpleOnItemTouchListener() {
      private var candidate: RecyclerView.ViewHolder? = null
      private var downX = 0f
      private var downY = 0f
      private var dragStarted = false
      private val startDrag = Runnable {
        val holder = candidate ?: return@Runnable
        val position = holder.bindingAdapterPosition
        val item = adapter.itemAt(position) ?: return@Runnable
        if (!item.isReorderable) return@Runnable
        if (item.type != "walletGroup" && recyclerView.isLayoutRequested) {
          relayoutRecyclerViewImmediately()
        }
        dragStarted = true
        beginDrag(holder)
      }

      private fun beginDrag(holder: RecyclerView.ViewHolder) {
        if (itemTouchHelper !== helper || candidate !== holder || !holder.itemView.isAttachedToWindow) return
        helper.startDrag(holder)
        holder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
      }

      private fun cancelPending() {
        handler.removeCallbacks(startDrag)
        candidate = null
      }

      override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {
        if (itemTouchHelper !== helper) return false
        when (event.actionMasked) {
          MotionEvent.ACTION_DOWN -> {
            cancelPending()
            dragStarted = false
            downX = event.x
            downY = event.y
            val child = recyclerView.findChildViewUnder(event.x, event.y)
            candidate = child
              ?.let(recyclerView::getChildViewHolder)
              // Container-slot holders report binding position 0 in their own adapter.
              ?.takeIf { holder -> holder is NativeListViewHolder && holder.bindingAdapter === adapter }
              ?.takeIf { holder ->
                adapter.itemAt(holder.bindingAdapterPosition)?.let { item ->
                  // OneKey patch: any wallet group member can initiate the group drag.
                  // val holderLocation = IntArray(2)
                  // holder.itemView.getLocationOnScreen(holderLocation)
                  // item.isReorderable && (
                  // item.type != "walletGroup" ||
                  // event.rawY in holderLocation[1].toFloat()..(holderLocation[1] + dp(68)).toFloat()
                  // )
                  item.isReorderable &&
                    (holder as? NativeListViewHolder)?.rowView?.canStartWalletGroupReorder(
                      event.y - holder.itemView.top,
                    ) != false
                } == true
              }
            if (candidate != null) handler.postDelayed(startDrag, REORDER_LONG_PRESS_MS)
          }
          MotionEvent.ACTION_MOVE -> if (
            !dragStarted && hypot(event.x - downX, event.y - downY) > movementLimit
          ) {
            cancelPending()
          }
          MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> cancelPending()
        }
        return false
      }

    }.also(recyclerView::addOnItemTouchListener)
  }

  private fun prepareDisplacedReorderAnimation(view: View, helper: ItemTouchHelper) {
    val previousX = view.x
    val previousY = view.y
    view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
      override fun onLayoutChange(
        changedView: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int,
      ) {
        changedView.removeOnLayoutChangeListener(this)
        if (itemTouchHelper !== helper) return
        val deltaX = previousX - changedView.x
        val deltaY = previousY - changedView.y
        if (deltaX == 0f && deltaY == 0f) return
        changedView.animate().cancel()
        changedView.translationX = deltaX
        changedView.translationY = deltaY
        changedView.animate()
          .translationX(0f)
          .translationY(0f)
          .setDuration(REORDER_SPRING_DURATION_MS)
          .setInterpolator(reorderSpringInterpolator)
          .start()
      }
    })
  }

  private fun reorderActiveBackground(theme: JSONObject?): Int = try {
    parseNativeListColor(
      theme?.optString("rowPressedBackground", "#00000017") ?: "#00000017",
    )
  } catch (_: IllegalArgumentException) {
    parseNativeListColor("#00000017")
  }

  private fun scheduleVisibleEvent() {
    if (visibleEventScheduled) return
    visibleEventScheduled = true
    Choreographer.getInstance().postFrameCallback {
      visibleEventScheduled = false
      val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return@postFrameCallback
      val visibleRows = (0 until recyclerView.childCount).mapNotNull { index ->
        val child = recyclerView.getChildAt(index)
        val holder = recyclerView.getChildViewHolder(child) as? NativeListViewHolder ?: return@mapNotNull null
        val visible = if (manager.orientation == RecyclerView.VERTICAL) {
          manager.getDecoratedBottom(child) > recyclerView.paddingTop &&
            manager.getDecoratedTop(child) < recyclerView.height - recyclerView.paddingBottom
        } else {
          manager.getDecoratedRight(child) > recyclerView.paddingLeft &&
            manager.getDecoratedLeft(child) < recyclerView.width - recyclerView.paddingRight
        }
        holder.bindingAdapterPosition.takeIf { visible && it != RecyclerView.NO_POSITION }
      }
      val first = visibleRows.minOrNull() ?: RecyclerView.NO_POSITION
      val last = visibleRows.maxOrNull() ?: RecyclerView.NO_POSITION
      val firstKey = adapter.itemAt(first)?.key
      val lastKey = adapter.itemAt(last)?.key
      val signature = "$first:$last:${firstKey.orEmpty()}:${lastKey.orEmpty()}"
      if (signature == lastVisibleRangeSignature) return@postFrameCallback
      lastVisibleRangeSignature = signature
      val payload = JSONObject()
        .put("firstIndex", first)
        .put("lastIndex", last)
      firstKey?.let { payload.put("firstKey", it) }
      lastKey?.let { payload.put("lastKey", it) }
      emit(VISIBLE_RANGE_CHANGED, payload)
    }
  }

  private fun checkEndReached() {
    if (structuralTail != null) return
    val current = config ?: return
    if (!current.loadMore || current.generation == endReachedGeneration || adapter.itemCount == 0) return
    val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
    val lastVisible = rowPositionForScroll(manager.findLastVisibleItemPosition())
    val thresholdItems = max(1, ceil(adapter.itemCount * current.endReachedThreshold).toInt())
    if (lastVisible >= adapter.itemCount - thresholdItems) {
      endReachedGeneration = current.generation
      val payload = JSONObject().put("generation", current.generation)
      adapter.itemAt(adapter.itemCount - 1)?.let { payload.put("lastKey", it.key) }
      emit(END_REACHED, payload)
    }
  }

  private fun emit(eventName: String, payload: JSONObject) {
    val json = payload.toString()
    when (eventName) {
      ROW_ACTION -> onRowAction?.invoke(json)
      ACTION_ANCHOR_INVALIDATED -> onActionAnchorInvalidated?.invoke(json)
      SELECTION_DELTA -> onSelectionDelta?.invoke(json)
      REORDER -> onReorder?.invoke(json)
      END_REACHED -> onEndReached?.invoke(json)
      VISIBLE_RANGE_CHANGED -> onVisibleRangeChanged?.invoke(json)
    }
  }

  /**
   * React Native owns the outer view's layout pass and does not always honor a
   * nested RecyclerView's requestLayout after the first mount. Re-run this
   * already-sized host once after a committed data update so the native rows
   * are measured and rebound without rebuilding the adapter.
   */
  private fun relayoutContents(preserveMarketPaginationAnchor: Boolean = false) {
    post {
      if (disposed || width <= 0 || height <= 0) return@post
      val anchor = if (preserveMarketPaginationAnchor) {
        captureVisibleMarketAnchor()
      } else {
        null
      }
      val anchorIndex = anchor?.let { currentAnchor ->
        adapter.currentList.indexOfFirst { it.key == currentAnchor.key }
      } ?: RecyclerView.NO_POSITION
      if (anchor != null && anchorIndex >= 0) {
        layoutManager.scrollToPositionWithOffset(scrollPositionForRow(anchorIndex), anchor.offset)
      }
      forceLayout()
      measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
      )
      layout(left, top, right, bottom)
      if (anchorIndex >= 0) {
        val now = SystemClock.uptimeMillis()
        if (now - lastMarketPaginationAnchorLogAtMs >= 1_000L) {
          lastMarketPaginationAnchorLogAtMs = now
          OneKeyLog.debug(
            "NativeList",
            "market-pagination-anchor-restored generation=${config?.generation} index=$anchorIndex",
          )
        }
      }
    }
  }

  private fun relayoutRecyclerView() {
    recyclerView.post {
      if (disposed || recyclerView.width <= 0 || recyclerView.height <= 0) return@post
      recyclerView.forceLayout()
      recyclerView.measure(
        MeasureSpec.makeMeasureSpec(recyclerView.width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(recyclerView.height, MeasureSpec.EXACTLY),
      )
      recyclerView.layout(
        recyclerView.left,
        recyclerView.top,
        recyclerView.right,
        recyclerView.bottom,
      )
    }
  }

  private fun startReorderRelayoutLoop() {
    reorderRelayoutActive = true
    lastReorderRelayoutLogAtMs = 0L
    scheduleReorderRelayout()
  }

  private fun stopReorderRelayoutLoop() {
    reorderRelayoutActive = false
  }

  private fun scheduleReorderRelayout() {
    if (!reorderRelayoutActive || reorderRelayoutScheduled) return
    reorderRelayoutScheduled = true
    recyclerView.postOnAnimation {
      reorderRelayoutScheduled = false
      if (
        !reorderRelayoutActive ||
        disposed ||
        recyclerView.width <= 0 ||
        recyclerView.height <= 0
      ) {
        return@postOnAnimation
      }
      val requestedBefore = recyclerView.isLayoutRequested
      val computingBefore = recyclerView.isComputingLayout
      if (requestedBefore && !computingBefore) {
        relayoutRecyclerViewImmediately()
      }
      val requestedAfter = recyclerView.isLayoutRequested
      val now = SystemClock.uptimeMillis()
      if (
        (requestedBefore || computingBefore || requestedAfter) &&
        (lastReorderRelayoutLogAtMs == 0L ||
          now - lastReorderRelayoutLogAtMs >= REORDER_RELAYOUT_LOG_INTERVAL_MS)
      ) {
        lastReorderRelayoutLogAtMs = now
        OneKeyLog.info(
          REORDER_LOG_TAG,
          "relayout requestedBefore=$requestedBefore computingBefore=$computingBefore " +
            "requestedAfter=$requestedAfter computingAfter=${recyclerView.isComputingLayout} " +
            "scrollOffset=${recyclerView.computeVerticalScrollOffset()}",
        )
      }
      scheduleReorderRelayout()
    }
  }

  private fun relayoutRecyclerViewImmediately() {
    if (disposed || recyclerView.isComputingLayout || recyclerView.width <= 0 || recyclerView.height <= 0) {
      return
    }
    recyclerView.forceLayout()
    recyclerView.measure(
      MeasureSpec.makeMeasureSpec(recyclerView.width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(recyclerView.height, MeasureSpec.EXACTLY),
    )
    recyclerView.layout(
      recyclerView.left,
      recyclerView.top,
      recyclerView.right,
      recyclerView.bottom,
    )
  }

  private fun dp(value: Int): Int = if (usesSelectorSourceScale) (value * resources.displayMetrics.density).roundToInt() else NativeListScale.dp(resources, value)

  private fun sectionIndexDp(value: Int): Int = (value * density).roundToInt()

  companion object {
    private val MARKET_PAGINATION_KEYS = setOf(
      "market-loading-more",
      "market-load-more-retry",
      "market-end",
    )
    private val MARKET_QUOTE_FIELDS = setOf(
      "revision",
      "price",
      "priceSegments",
      "change",
      "accessibilityLabel",
    )
    private const val SECTION_INDEX_CONTENT_INSET_DP = 16
    private const val SECTION_INDEX_RAIL_WIDTH_DP = 32
    private const val SECTION_INDEX_PREVIEW_WIDTH_DP = 60
    private const val SECTION_INDEX_PREVIEW_HEIGHT_DP = 50
    private const val SECTION_INDEX_PREVIEW_END_MARGIN_DP = 40
    private const val REORDER_LONG_PRESS_MS = 200L
    private const val REORDER_ALLOWABLE_MOVEMENT_DP = 10
    private const val REORDER_PLACEHOLDER_INSET_DP = 8
    private const val REORDER_PLACEHOLDER_RADIUS_DP = 12
    private const val REORDER_SPRING_DAMPING = 25.0
    private const val REORDER_SPRING_STIFFNESS = 400.0
    private const val REORDER_SPRING_MASS = 0.4
    private const val REORDER_SPRING_DURATION_MS = 300L
    private const val REORDER_AUTOSCROLL_ACCELERATION_OFFSET_MS = 1_500L
    private const val REORDER_AUTOSCROLL_LOG_INTERVAL_MS = 100L
    private const val REORDER_DRAG_FRAME_LOG_INTERVAL_MS = 100L
    private const val REORDER_MOVE_ATTEMPT_LOG_INTERVAL_MS = 250L
    private const val REORDER_RELAYOUT_LOG_INTERVAL_MS = 100L
    private const val REORDER_LOG_TAG = "NativeListReorder"
    private const val ROW_ACTION = "rowAction"
    private const val ACTION_ANCHOR_INVALIDATED = "actionAnchorInvalidated"
    private const val SELECTION_DELTA = "selectionDelta"
    private const val REORDER = "reorder"
    private const val END_REACHED = "endReached"
    private const val VISIBLE_RANGE_CHANGED = "visibleRangeChanged"
  }
}

private class ReorderPlaceholderDecoration(
  private val adapter: NativeListAdapter,
  private val insetPx: Int,
  private val radiusPx: Float,
) : RecyclerView.ItemDecoration() {
  var position: Int = RecyclerView.NO_POSITION
  var color: Int = Color.TRANSPARENT
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val bounds = RectF()

  override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    val item = adapter.itemAt(position) ?: return
    val isWalletSidebar =
      item.type == "identity" && item.json.optString("presentation") == "walletSidebar"
    if (!isWalletSidebar && item.type != "walletGroup") return
    val view = (0 until parent.childCount).asSequence().map(parent::getChildAt)
      .firstOrNull { child ->
        val holder = parent.getChildViewHolder(child)
        holder is NativeListViewHolder && holder.bindingAdapterPosition == position
      } ?: return
    bounds.set(
      (view.left + insetPx).toFloat(),
      view.top.toFloat(),
      (view.right - insetPx).toFloat(),
      view.bottom.toFloat(),
    )
    paint.color = color
    canvas.drawRoundRect(bounds, radiusPx, radiusPx, paint)
  }
}

private class ReorderSpringInterpolator(
  damping: Double,
  stiffness: Double,
  mass: Double,
  private val durationSeconds: Double,
) : TimeInterpolator {
  private val naturalFrequency = sqrt(stiffness / mass)
  private val dampingRatio = damping / (2 * sqrt(stiffness * mass))
  private val dampedFrequency = naturalFrequency * sqrt(1 - dampingRatio * dampingRatio)

  override fun getInterpolation(input: Float): Float {
    if (input <= 0f) return 0f
    if (input >= 1f) return 1f
    val time = input * durationSeconds
    val decay = exp(-dampingRatio * naturalFrequency * time)
    val displacement = decay * (
      cos(dampedFrequency * time) +
        dampingRatio * naturalFrequency / dampedFrequency * sin(dampedFrequency * time)
      )
    return (1 - displacement).toFloat()
  }
}

private data class NativeListSectionIndexEntry(
  val key: String,
  val title: String,
  val position: Int,
)

private class NativeListSectionIndexView(
  context: android.content.Context,
  private val fonts: NativeListFonts,
) : View(context) {
  var onSelect: ((Int, Boolean) -> Unit)? = null
  var onInteractionEnded: (() -> Unit)? = null
  var activeIndex: Int? = null
    private set
  private var titles: List<String> = emptyList()
  private var normalColor = Color.GRAY
  private var activeColor = Color.BLACK
  private var activeTextColor = Color.WHITE
  private var lastTouchIndex: Int? = null
  fun refreshFonts() {
    normalPaint.typeface = fonts.regular(context)
    activePaint.typeface = fonts.medium(context)
    invalidate()
  }

  private val activeBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    typeface = fonts.regular(context)
  }
  private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    typeface = fonts.medium(context)
  }

  init {
    isClickable = true
    isFocusable = true
    importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    contentDescription = ACCESSIBILITY_LABEL
  }

  fun configure(
    titles: List<String>,
    normalColor: Int,
    activeColor: Int,
    activeTextColor: Int,
  ) {
    this.titles = titles
    this.normalColor = normalColor
    this.activeColor = activeColor
    this.activeTextColor = activeTextColor
    activeIndex = null
    updateContentDescription()
    invalidate()
  }

  fun setActiveIndex(index: Int?) {
    if (activeIndex == index) return
    activeIndex = index
    updateContentDescription()
    invalidate()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (titles.isEmpty()) return
    val metrics = indexMetrics()
    val visibleIndices = visibleLabelIndices(metrics.trackHeight)
    val textSize = 10f * resources.displayMetrics.scaledDensity
    val centerX = width - dp(10f)
    val badgeRadius = dp(7f)
    normalPaint.color = normalColor
    normalPaint.textSize = textSize
    activePaint.color = activeColor
    activePaint.textSize = textSize
    visibleIndices.forEachIndexed { visibleIndex, index ->
      val title = titles[index]
      val active = index == activeIndex
      val paint = if (active) activePaint else normalPaint
      val centerY = visibleLabelCenterY(visibleIndex, visibleIndices.size, metrics)
      if (active) {
        activeBackgroundPaint.color = activeColor
        canvas.drawCircle(centerX, centerY, badgeRadius, activeBackgroundPaint)
      }
      paint.color = if (active) activeTextColor else normalColor
      val baseline = centerY - (paint.descent() + paint.ascent()) / 2f
      canvas.drawText(title, centerX, baseline, paint)
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (titles.isEmpty() || !isEnabled) return false
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        parent?.requestDisallowInterceptTouchEvent(true)
        lastTouchIndex = null
        selectAt(event.y, interacting = true)
        return true
      }
      MotionEvent.ACTION_MOVE -> {
        selectAt(event.y, interacting = true)
        return true
      }
      MotionEvent.ACTION_UP -> {
        selectAt(event.y, interacting = true)
        lastTouchIndex = null
        parent?.requestDisallowInterceptTouchEvent(false)
        performClick()
        onInteractionEnded?.invoke()
        return true
      }
      MotionEvent.ACTION_CANCEL -> {
        lastTouchIndex = null
        parent?.requestDisallowInterceptTouchEvent(false)
        onInteractionEnded?.invoke()
        return true
      }
    }
    return super.onTouchEvent(event)
  }

  override fun performClick(): Boolean {
    super.performClick()
    return true
  }

  override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
    super.onInitializeAccessibilityNodeInfo(info)
    info.className = SeekBar::class.java.name
    info.isScrollable = titles.size > 1
    if (titles.isNotEmpty()) {
      info.rangeInfo = AccessibilityNodeInfo.RangeInfo.obtain(
        AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_INT,
        0f,
        (titles.size - 1).toFloat(),
        (activeIndex ?: 0).toFloat(),
      )
      info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
      info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
      info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS)
    }
  }

  override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
    if (titles.isEmpty()) return super.performAccessibilityAction(action, arguments)
    val next = when (action) {
      AccessibilityNodeInfo.ACTION_SCROLL_FORWARD ->
        ((activeIndex ?: -1) + 1).coerceAtMost(titles.lastIndex)
      AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD ->
        ((activeIndex ?: 1) - 1).coerceAtLeast(0)
      AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS.id ->
        arguments?.getFloat(AccessibilityNodeInfo.ACTION_ARGUMENT_PROGRESS_VALUE)?.roundToInt()
          ?.coerceIn(0, titles.lastIndex)
      else -> null
    } ?: return super.performAccessibilityAction(action, arguments)
    select(next, interacting = false)
    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
    return true
  }

  private fun selectAt(y: Float, interacting: Boolean) {
    val metrics = indexMetrics()
    if (metrics.trackHeight <= 0f) return
    val visibleIndices = visibleLabelIndices(metrics.trackHeight).sorted()
    val visibleTrackHeight = minOf(metrics.trackHeight, dp(16f) * visibleIndices.size)
    val visibleOriginY = metrics.originY + (metrics.trackHeight - visibleTrackHeight) / 2f
    val progress = ((y - visibleOriginY) / visibleTrackHeight).coerceIn(0f, 1f)
    val slot = (progress * visibleIndices.size).toInt().coerceIn(0, visibleIndices.lastIndex)
    val index = visibleIndices[slot]
    if (interacting && lastTouchIndex == index) return
    lastTouchIndex = index.takeIf { interacting }
    select(index, interacting)
  }

  private fun select(index: Int, interacting: Boolean) {
    onSelect?.invoke(index, interacting)
    setActiveIndex(index)
  }

  fun centerYForIndex(index: Int): Float = entryCenterY(index, indexMetrics())

  fun preferredHeight(maximumHeight: Int): Int = minOf(
    maximumHeight,
    (dp(8f) * 2f + dp(16f) * titles.size).roundToInt(),
  )

  private data class Metrics(val originY: Float, val trackHeight: Float)

  private fun indexMetrics(): Metrics {
    if (titles.isEmpty()) return Metrics(height / 2f, 0f)
    val edgePadding = dp(8f)
    val labelSpacing = dp(16f)
    val availableHeight = (height - edgePadding * 2f).coerceAtLeast(0f)
    val trackHeight = minOf(availableHeight, labelSpacing * titles.size)
    return Metrics((height - trackHeight) / 2f, trackHeight)
  }

  private fun entryCenterY(index: Int, metrics: Metrics): Float {
    if (titles.isEmpty()) return height / 2f
    return metrics.originY + metrics.trackHeight * (index + 0.5f) / titles.size
  }

  private fun visibleLabelCenterY(visibleIndex: Int, visibleCount: Int, metrics: Metrics): Float {
    val visibleTrackHeight = minOf(metrics.trackHeight, dp(16f) * visibleCount)
    val visibleOriginY = metrics.originY + (metrics.trackHeight - visibleTrackHeight) / 2f
    return visibleOriginY + visibleTrackHeight * (visibleIndex + 0.5f) / visibleCount.coerceAtLeast(1)
  }

  private fun visibleLabelIndices(trackHeight: Float): Set<Int> {
    if (titles.isEmpty()) return emptySet()
    val maxVisible = max(1, (trackHeight / dp(16f)).toInt())
    if (titles.size <= maxVisible) return titles.indices.toSet()
    if (maxVisible == 1) return setOf(0)
    return (0 until maxVisible).mapTo(mutableSetOf()) { slot ->
      (slot.toFloat() * titles.lastIndex / (maxVisible - 1)).roundToInt()
    }
  }

  private fun dp(value: Float): Float = value * resources.displayMetrics.density

  private fun updateContentDescription() {
    contentDescription = activeIndex?.let { titles.getOrNull(it) }
      ?.let { "$ACCESSIBILITY_LABEL, $it" }
      ?: ACCESSIBILITY_LABEL
  }

  companion object {
    private const val ACCESSIBILITY_LABEL = "Section index"
  }
}

private class ItemSpacingDecoration(
  private val spacing: Int,
  private val sourceSpacing: Int,
  private val density: Float,
) : RecyclerView.ItemDecoration() {
  override fun getItemOffsets(
    outRect: android.graphics.Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State,
  ) {
    if (spacing <= 0 || parent.getChildViewHolder(view) !is NativeListViewHolder) return
    val horizontal = (parent.layoutManager as? LinearLayoutManager)?.orientation == RecyclerView.HORIZONTAL
    val item = view.tag as? NativeListItem
    val sourceWallet = item?.type == "identity" && item.json.optString("presentation") == "walletSidebar" && item.hasExplicitHeight
    // OneKey patch: V1 measures the wallet and its bottom padding as one sortable row.
    val itemSpacing = if (!horizontal && sourceWallet) {
      val sourceHeight = item!!.styledHeight ?: item.json.optDouble("height")
      ((sourceHeight + sourceSpacing) * density).roundToInt() - (sourceHeight * density).roundToInt()
    } else spacing
    if (horizontal) outRect.right = itemSpacing else outRect.bottom = itemSpacing
  }
}

/**
 * React lays out slot content at `contentPaddingHorizontal ?? contentPadding` logical units
 * from each edge. Row padding may be scaled or widened by the section-index gutter, so slot
 * holders offset themselves (possibly negatively) back to the JavaScript inset.
 */
private class ContainerSlotInsetDecoration(private val insetsPx: () -> Pair<Int, Int>) : RecyclerView.ItemDecoration() {
  override fun getItemOffsets(
    outRect: android.graphics.Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State,
  ) {
    if (parent.getChildViewHolder(view) !is NativeListContainerSlotAdapter.Holder) return
    val (leftInset, rightInset) = insetsPx()
    outRect.left = leftInset - parent.paddingLeft
    outRect.right = rightInset - parent.paddingRight
  }
}

internal fun isStickySectionHeader(item: NativeListItem): Boolean =
  item.type == "sectionHeader" && item.json.optBoolean("sticky", true) && item.json.optString("variant") != "summary"

// OneKey patch: paint only explicitly requested backgrounds into list side padding.
private class SelectorBackgroundDecoration(private val adapter: NativeListAdapter) : RecyclerView.ItemDecoration() {
  private val paint = Paint()
  override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    for (index in 0 until parent.childCount) {
      val child = parent.getChildAt(index)
      val item = adapter.itemAt(parent.getChildViewHolder(child).takeIf { it is NativeListViewHolder }?.bindingAdapterPosition ?: RecyclerView.NO_POSITION) ?: continue
      if (!item.json.optBoolean("backgroundFullWidth", false)) continue
      val color = resolveNativeListSelectorBackgroundColor(
        rowType = item.type,
        styleBackgroundColor = item.json.optJSONObject("style")
          ?.optJSONObject("container")
          ?.optString("backgroundColor"),
        rowBackgroundColor = item.json.optString("backgroundColor"),
      ) ?: continue
      paint.color = try { parseNativeListColor(color) } catch (_: IllegalArgumentException) { Color.TRANSPARENT }
      val opacity = item.json.optJSONObject("style")?.optJSONObject("container")?.optDouble("opacity", item.json.optDouble("opacity", 1.0)) ?: item.json.optDouble("opacity", 1.0)
      paint.alpha = (Color.alpha(paint.color) * opacity * (if (item.json.optBoolean("disabled", false)) 0.5 else 1.0)).toInt()
      val top = child.y
      canvas.drawRect(0f, top, parent.width.toFloat(), top + child.height, paint)
      if (item.type == "system" && item.json.optString("variant") == "warning") {
        paint.color = try { parseNativeListColor(item.json.optString("borderColor", "#E0E0E0")) } catch (_: IllegalArgumentException) { Color.TRANSPARENT }
        canvas.drawRect(0f, top, parent.width.toFloat(), top + 1, paint)
        canvas.drawRect(0f, top + child.height - 1, parent.width.toFloat(), top + child.height, paint)
      }
    }
  }
}
