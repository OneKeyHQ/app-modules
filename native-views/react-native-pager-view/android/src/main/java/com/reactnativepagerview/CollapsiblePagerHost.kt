package com.reactnativepagerview

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
// OneKey patch: FrameLayout is inherited through NestedScrollableHost.
// import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.util.WeakHashMap
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class CollapsiblePagerAdapter : RecyclerView.Adapter<ViewPagerViewHolder>() {
  private val pages = ArrayList<View>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    ViewPagerViewHolder.create(parent)

  override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
    val page = pages[position]
    val container = holder.container
    container.removeAllViews()
    (page.parent as? ViewGroup)?.removeView(page)
    container.addView(page)
  }

  override fun onViewRecycled(holder: ViewPagerViewHolder) {
    holder.container.removeAllViews()
    super.onViewRecycled(holder)
  }

  override fun getItemCount() = pages.size

  fun addPage(page: View, index: Int) {
    val safeIndex = index.coerceIn(0, pages.size)
    pages.add(safeIndex, page)
    notifyItemInserted(safeIndex)
  }

  fun removePage(page: View) {
    val index = pages.indexOf(page)
    if (index >= 0) {
      pages.removeAt(index)
      notifyItemRemoved(index)
    }
  }

  fun pageAt(index: Int) = pages[index]

  fun clear() {
    val count = pages.size
    pages.forEach { page -> (page.parent as? ViewGroup)?.removeView(page) }
    pages.clear()
    if (count > 0) notifyItemRangeRemoved(0, count)
  }
}

/**
 * Native vertical coordinator for the optional collapsible PagerView variant.
 * It consumes only the header part of vertical nested scroll. The active page's
 * vertical scroll view remains the content scroll source and horizontal gestures
 * remain owned by ViewPager2.
 */
// OneKey patch: Match the standard pager's nested horizontal gesture host.
// Original: class CollapsiblePagerHost(context: Context) : FrameLayout(context), NestedScrollingParent3 {
class CollapsiblePagerHost(context: Context) : NestedScrollableHost(context), NestedScrollingParent3 {
  private data class RecyclerPadding(
    var left: Int,
    var top: Int,
    var right: Int,
    var bottom: Int,
    var clipToPadding: Boolean,
    var appliedTopInset: Int = 0,
  )

  val pager = ViewPager2(context)
  internal val adapter = CollapsiblePagerAdapter()
  private val logicalChildren = ArrayList<View>()
  private val nestedScrollingParentHelper = NestedScrollingParentHelper(this)
  private val originalRecyclerPadding = WeakHashMap<RecyclerView, RecyclerPadding>()
  private val restoredRecyclerKeys = WeakHashMap<RecyclerView, String>()
  private val pageOffsets = HashMap<String, Int>()
  private var observedRecyclerView: RecyclerView? = null
  private var observedScrollListener: RecyclerView.OnScrollListener? = null
  private var attachmentGeneration = 0
  private var headerView: View? = null
  private var stickyHeaderView: View? = null
  private val pageContentLayoutListener = ViewTreeObserver.OnPreDrawListener {
    if (width > 0 && height > 0 && isShown) layoutPagerIfRequested()
    // Fabric can mount a retained list without another Android layout pass.
    // Apply its header insets before that list first draws.
    prepareAdjacentPages()
    attachRecyclerObserver()
    // OneKey patch: a filtered short page can reduce the existing collapse range.
    setHeaderOffset(headerOffsetPx)
    true
  }

  var selectedPage = 0
    private set
  var pendingInitialPage = 0
  private var hasAppliedInitialPage = false
  var headerHeightPx = 0
    set(value) {
      if (field == value) return
      field = max(0, value)
      headerOffsetPx = min(headerOffsetPx, field)
      reapplyRecyclerInsets()
      updateHeaderLayout()
    }
  var stickyHeaderHeightPx = 0
    set(value) {
      if (field == value) return
      field = max(0, value)
      reapplyRecyclerInsets()
      updateHeaderLayout()
    }
  var headerOffsetPx = 0
    private set
  var pageKeys: List<String> = emptyList()
  var retainedPages: String = "[]"
  var onHeaderOffsetChanged: (() -> Unit)? = null

  init {
    isSaveEnabled = false
    clipChildren = true
    pager.id = View.generateViewId()
    pager.adapter = adapter
    pager.isSaveEnabled = false
    super.addView(
      pager,
      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
    )
  }

  fun addReactChild(child: View, index: Int) {
    val safeIndex = index.coerceIn(0, logicalChildren.size)
    logicalChildren.add(safeIndex, child)
    when (safeIndex) {
      0 -> {
        headerView = child
        (child.parent as? ViewGroup)?.removeView(child)
        super.addView(child)
      }
      1 -> {
        stickyHeaderView = child
        (child.parent as? ViewGroup)?.removeView(child)
        super.addView(child)
      }
      else -> adapter.addPage(child, safeIndex - PAGE_SLOT_OFFSET)
    }
    applyPendingInitialPage()
    headerView?.bringToFront()
    stickyHeaderView?.bringToFront()
    requestLayout()
  }

  fun removeReactChild(child: View) {
    val index = logicalChildren.indexOf(child)
    if (index < 0) return
    logicalChildren.removeAt(index)
    when (index) {
      0 -> {
        super.removeView(child)
        headerView = null
      }
      1 -> {
        super.removeView(child)
        stickyHeaderView = null
      }
      else -> {
        findVerticalRecyclerView(child)?.let(::restoreRecyclerInsets)
        adapter.removePage(child)
      }
    }
  }

  fun removeAllReactChildren() {
    detachRecyclerObserver()
    for (recycler in originalRecyclerPadding.keys.toList()) {
      restoreRecyclerInsets(recycler)
    }
    headerView?.let { view -> super.removeView(view) }
    stickyHeaderView?.let { view -> super.removeView(view) }
    headerView = null
    stickyHeaderView = null
    logicalChildren.clear()
    adapter.clear()
    hasAppliedInitialPage = false
  }

  fun reactChildCount() = logicalChildren.size

  fun reactChildAt(index: Int) = logicalChildren[index]

  fun selectPage(position: Int) {
    if (adapter.itemCount == 0) return
    detachRecyclerObserver()
    selectedPage = position.coerceIn(0, adapter.itemCount - 1)
    val savedOffset = pageOffsets[currentPageKey()]
      ?: recyclerViewForPage(selectedPage)?.computeVerticalScrollOffset()
      ?: 0
    if (savedOffset > 0) setHeaderOffset(headerHeightPx)
    postForCurrentAttachment {
      prepareAdjacentPages()
      attachRecyclerObserver()
    }
  }

  private fun postForCurrentAttachment(block: () -> Unit) {
    val generation = attachmentGeneration
    post {
      if (isAttachedToWindow && generation == attachmentGeneration) block()
    }
  }

  fun applyPendingInitialPage() {
    // OneKey patch: Fabric can resend initialPage while retained pages change.
    // Reapplying it interrupts the current gesture and can leave ViewPager2 settling.
    if (hasAppliedInitialPage) return
    if (pendingInitialPage !in 0 until adapter.itemCount) return
    hasAppliedInitialPage = true
    if (pager.currentItem != pendingInitialPage) {
      pager.setCurrentItem(pendingInitialPage, false)
    }
    selectPage(pendingInitialPage)
  }

  fun attachedPageCount(): Int = (pager.getChildAt(0) as? RecyclerView)?.childCount ?: 0

  fun observedScrollableCount(): Int = originalRecyclerPadding.size

  private fun layoutPagerIfRequested() {
    val recycler = pager.getChildAt(0) as? RecyclerView ?: return
    if (!recycler.isLayoutRequested || recycler.isComputingLayout || recycler.width <= 0 || recycler.height <= 0) return
    // OneKey patch: a non-animated page command queues RecyclerView layout, but RN
    // can stop that request after navigation. Drain it before showing the page.
    recycler.measure(
      MeasureSpec.makeMeasureSpec(recycler.width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(recycler.height, MeasureSpec.EXACTLY),
    )
    recycler.layout(recycler.left, recycler.top, recycler.right, recycler.bottom)
  }

  private fun updateHeaderLayout() {
    requestLayout()
    if (width <= 0 || height <= 0) return
    // OneKey patch: React Native ancestors can stop requestLayout after header props change.
    // Commit the custom native slots at their existing bounds before the next draw.
    forceLayout()
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
    )
    layout(left, top, right, bottom)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
    // OneKey patch: a detached Fabric page has a zero viewport. Measuring ViewPager2
    // at width zero spuriously selects page zero and replaces the retained JS pages.
    if (width <= 0 || height <= 0) {
      setMeasuredDimension(width, height)
      return
    }
    val pagerHeight = height + headerHeightPx
    pager.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(pagerHeight, MeasureSpec.EXACTLY),
    )
    headerView?.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(headerHeightPx, MeasureSpec.EXACTLY),
    )
    stickyHeaderView?.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(stickyHeaderHeightPx, MeasureSpec.EXACTLY),
    )
    setMeasuredDimension(width, height)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    val width = right - left
    val height = bottom - top
    // RN ancestors allow overflow; keep translated header content inside this viewport.
    clipBounds = Rect(0, 0, width, height)
    if (width <= 0 || height <= 0) return
    pager.layout(0, 0, width, height + headerHeightPx)
    headerView?.layout(0, 0, width, headerHeightPx)
    stickyHeaderView?.layout(
      0,
      headerHeightPx,
      width,
      headerHeightPx + stickyHeaderHeightPx,
    )
    applyHeaderOffset()
    postForCurrentAttachment {
      prepareAdjacentPages()
      attachRecyclerObserver()
    }
  }

  private fun maximumHeaderOffset(): Int {
    val recycler = observedRecyclerView ?: return headerHeightPx
    if (recycler.childCount == 0 || recycler.canScrollVertically(-1) || recycler.canScrollVertically(1)) {
      return headerHeightPx
    }
    // OneKey patch: short pages use the original rounded-DIP minimum content height.
    val density = resources.displayMetrics.density
    val contentHeight = ((height / density).roundToInt() + (headerHeightPx / density).roundToInt()) * density
    return (contentHeight.roundToInt() - height).coerceIn(0, headerHeightPx)
  }

  private fun setHeaderOffset(offset: Int) {
    // val next = offset.coerceIn(0, headerHeightPx)
    val next = offset.coerceIn(0, maximumHeaderOffset())
    if (headerOffsetPx == next) return
    headerOffsetPx = next
    applyHeaderOffset()
  }

  private fun applyHeaderOffset() {
    val offset = headerOffsetPx.toFloat()
    pager.translationY = -offset
    headerView?.translationY = -offset
    stickyHeaderView?.translationY = -offset
  }

  private fun pageKey(index: Int): String = pageKeys.getOrNull(index) ?: "page-$index"

  private fun currentPageKey(): String = pageKey(selectedPage)

  private fun isDescendant(view: View, ancestor: View): Boolean {
    var current: View? = view
    while (current != null) {
      if (current === ancestor) return true
      current = current.parent as? View
    }
    return false
  }

  private fun findVerticalRecyclerView(view: View): RecyclerView? {
    if (view is RecyclerView) {
      val manager = view.layoutManager
      if (manager !is LinearLayoutManager || manager.orientation == RecyclerView.VERTICAL) {
        return view
      }
    }
    if (view is ViewGroup) {
      for (index in 0 until view.childCount) {
        findVerticalRecyclerView(view.getChildAt(index))?.let { return it }
      }
    }
    return null
  }

  private fun recyclerViewForPage(index: Int): RecyclerView? {
    if (index !in 0 until adapter.itemCount) return null
    return findVerticalRecyclerView(adapter.pageAt(index))
  }

  private fun prepareAdjacentPages() {
    val first = max(0, selectedPage - 1)
    val last = min(adapter.itemCount - 1, selectedPage + 1)
    for (index in first..last) {
      recyclerViewForPage(index)?.let { recycler ->
        applyRecyclerInsets(recycler, index)
      }
    }
  }

  private fun attachRecyclerObserver() {
    if (selectedPage !in 0 until adapter.itemCount) return
    val previous = observedRecyclerView
    if (previous != null && !isDescendant(previous, adapter.pageAt(selectedPage))) {
      detachRecyclerObserver()
      // A replacement list starts at its first row, retaining only header collapse.
      pageOffsets[currentPageKey()] = 0
    }
    val recycler = recyclerViewForPage(selectedPage) ?: return
    if (observedRecyclerView === recycler) {
      applyRecyclerInsets(recycler, selectedPage)
      return
    }

    detachRecyclerObserver()
    observedRecyclerView = recycler
    applyRecyclerInsets(recycler, selectedPage)
    val listener = object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val contentOffset = recyclerView.computeVerticalScrollOffset()
        pageOffsets[currentPageKey()] = contentOffset
        // Nested touch scrolling moves list content only after the header has
        // collapsed. Programmatic list jumps bypass those parent callbacks.
        if (contentOffset > 0 && headerOffsetPx < headerHeightPx) {
          setHeaderOffset(headerHeightPx)
        }
      }
    }
    observedScrollListener = listener
    recycler.addOnScrollListener(listener)

  }

  private fun detachRecyclerObserver() {
    storeObservedPageOffset()
    val recycler = observedRecyclerView
    val listener = observedScrollListener
    if (recycler != null && listener != null) recycler.removeOnScrollListener(listener)
    observedRecyclerView = null
    observedScrollListener = null
  }

  private fun storeObservedPageOffset() {
    observedRecyclerView?.let { recycler ->
      pageOffsets[currentPageKey()] = recycler.computeVerticalScrollOffset()
    }
  }

  private fun applyRecyclerInsets(recycler: RecyclerView, pageIndex: Int) {
    val original = originalRecyclerPadding.getOrPut(recycler) {
      RecyclerPadding(
        recycler.paddingLeft,
        recycler.paddingTop,
        recycler.paddingRight,
        recycler.paddingBottom,
        recycler.clipToPadding,
      )
    }
    updateRecyclerPaddingOwnership(recycler, original)
    val topInset = headerHeightPx + stickyHeaderHeightPx
    recycler.clipToPadding = false
    val top = original.top + topInset
    // The taller pager already accounts for the translated header. Adding it
    // again leaves a full header-sized blank footer at the end of long lists.
    val bottom = original.bottom
    if (recycler.paddingLeft != original.left || recycler.paddingTop != top ||
      recycler.paddingRight != original.right || recycler.paddingBottom != bottom) {
      // OneKey patch: LinearLayoutManager otherwise anchors children at their old
      // screen coordinates when a page changes the sticky header height.
      val manager = recycler.layoutManager as? LinearLayoutManager
      val first = manager?.takeIf { it.orientation == RecyclerView.VERTICAL }
        ?.findFirstVisibleItemPosition() ?: RecyclerView.NO_POSITION
      val anchorOffset = if (first != RecyclerView.NO_POSITION) {
        manager?.findViewByPosition(first)?.let { manager.getDecoratedTop(it) - recycler.paddingTop }
      } else null
      recycler.setPadding(original.left, top, original.right, bottom)
      if (anchorOffset != null) manager?.scrollToPositionWithOffset(first, anchorOffset)
      // React Native can stop this nested requestLayout at its React-owned parent.
      // Commit the changed inset before drawing instead of waiting for a data update.
      if (recycler.width > 0 && recycler.height > 0 && !recycler.isComputingLayout) {
        recycler.forceLayout()
        recycler.measure(
          MeasureSpec.makeMeasureSpec(recycler.width, MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(recycler.height, MeasureSpec.EXACTLY),
        )
        recycler.layout(recycler.left, recycler.top, recycler.right, recycler.bottom)
      }
    }
    original.appliedTopInset = topInset
    val key = pageKey(pageIndex)
    if (restoredRecyclerKeys[recycler] != key) {
      restoredRecyclerKeys[recycler] = key
      val generation = attachmentGeneration
      recycler.post {
        if (!isAttachedToWindow || generation != attachmentGeneration ||
          restoredRecyclerKeys[recycler] != key ||
          originalRecyclerPadding[recycler] !== original) {
          if (restoredRecyclerKeys[recycler] == key) restoredRecyclerKeys.remove(recycler)
          return@post
        }
        val saved = pageOffsets[key] ?: 0
        val current = recycler.computeVerticalScrollOffset()
        if (saved != current) recycler.scrollBy(0, saved - current)
      }
    }
  }

  private fun updateRecyclerPaddingOwnership(
    recycler: RecyclerView,
    original: RecyclerPadding,
  ) {
    val expectedTop = original.top + original.appliedTopInset
    if (recycler.paddingLeft != original.left) original.left = recycler.paddingLeft
    if (recycler.paddingTop != expectedTop) {
      // A React-owned list can replace its content padding while it remains
      // attached. Remove our inset before retaining that value as the new base.
      original.top = (recycler.paddingTop - original.appliedTopInset).coerceAtLeast(0)
    }
    if (recycler.paddingRight != original.right) original.right = recycler.paddingRight
    if (recycler.paddingBottom != original.bottom) original.bottom = recycler.paddingBottom
    if (recycler.clipToPadding) original.clipToPadding = true
  }

  private fun reapplyRecyclerInsets() {
    prepareAdjacentPages()
  }

  private fun restoreRecyclerInsets(recycler: RecyclerView) {
    val original = originalRecyclerPadding.remove(recycler) ?: return
    updateRecyclerPaddingOwnership(recycler, original)
    restoredRecyclerKeys.remove(recycler)
    recycler.clipToPadding = original.clipToPadding
    recycler.setPadding(original.left, original.top, original.right, original.bottom)
  }

  private fun isCurrentPageTarget(target: View): Boolean {
    if (selectedPage !in 0 until adapter.itemCount) return false
    return isDescendant(target, adapter.pageAt(selectedPage))
  }

  // RN ScrollView uses the platform nested-scroll callbacks; RecyclerView
  // uses the AndroidX variants with an explicit input type.
  override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean =
    onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH)

  override fun onNestedScrollAccepted(child: View, target: View, axes: Int) =
    onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH)

  override fun onStopNestedScroll(target: View) =
    onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) =
    onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
  ) = onNestedScroll(
    target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH,
  )

  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean =
    axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0 && isCurrentPageTarget(target)

  override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
  }

  override fun onStopNestedScroll(target: View, type: Int) {
    nestedScrollingParentHelper.onStopNestedScroll(target, type)
    onHeaderOffsetChanged?.invoke()
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    if (!isCurrentPageTarget(target)) return
    // OneKey patch: consume only the distance the short-page header can actually move.
    val maximumOffset = maximumHeaderOffset()
    // if (dy > 0 && headerOffsetPx < headerHeightPx) {
    //   val used = min(dy, headerHeightPx - headerOffsetPx)
    if (dy > 0 && headerOffsetPx < maximumOffset) {
      val used = min(dy, maximumOffset - headerOffsetPx)
      setHeaderOffset(headerOffsetPx + used)
      consumed[1] += used
    } else if (dy < 0 && headerOffsetPx > 0 && !target.canScrollVertically(-1)) {
      val used = max(dy, -headerOffsetPx)
      setHeaderOffset(headerOffsetPx + used)
      consumed[1] += used
    }
  }

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
    consumed: IntArray,
  ) {
    if (!isCurrentPageTarget(target) || dyUnconsumed >= 0 || headerOffsetPx <= 0) return
    val used = max(dyUnconsumed, -headerOffsetPx)
    setHeaderOffset(headerOffsetPx + used)
    consumed[1] += used
  }

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
  ) {
    onNestedScroll(
      target,
      dxConsumed,
      dyConsumed,
      dxUnconsumed,
      dyUnconsumed,
      type,
      IntArray(2),
    )
  }

  override fun getNestedScrollAxes() = nestedScrollingParentHelper.nestedScrollAxes

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    attachmentGeneration += 1
    restoredRecyclerKeys.clear()
    viewTreeObserver.addOnPreDrawListener(pageContentLayoutListener)
  }

  override fun onDetachedFromWindow() {
    attachmentGeneration += 1
    viewTreeObserver.removeOnPreDrawListener(pageContentLayoutListener)
    detachRecyclerObserver()
    for (recycler in originalRecyclerPadding.keys.toList()) {
      restoreRecyclerInsets(recycler)
    }
    super.onDetachedFromWindow()
  }

  companion object {
    const val PAGE_SLOT_OFFSET = 2
  }
}
