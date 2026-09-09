package com.reactnativepagerview

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.util.WeakHashMap
import kotlin.math.max
import kotlin.math.min

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
class CollapsiblePagerHost(context: Context) : FrameLayout(context), NestedScrollingParent3 {
  private data class RecyclerPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val clipToPadding: Boolean,
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
  private var headerView: View? = null
  private var stickyHeaderView: View? = null
  private val pageContentLayoutListener = ViewTreeObserver.OnPreDrawListener {
    // Fabric can mount a retained list without another Android layout pass.
    // Apply its header insets before that list first draws.
    prepareAdjacentPages()
    attachRecyclerObserver()
    true
  }

  var selectedPage = 0
    private set
  var pendingInitialPage = 0
  var headerHeightPx = 0
    set(value) {
      if (field == value) return
      field = max(0, value)
      headerOffsetPx = min(headerOffsetPx, field)
      reapplyRecyclerInsets()
      requestLayout()
    }
  var stickyHeaderHeightPx = 0
    set(value) {
      if (field == value) return
      field = max(0, value)
      reapplyRecyclerInsets()
      requestLayout()
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
  }

  fun reactChildCount() = logicalChildren.size

  fun reactChildAt(index: Int) = logicalChildren[index]

  fun selectPage(position: Int) {
    if (adapter.itemCount == 0) return
    detachRecyclerObserver()
    selectedPage = position.coerceIn(0, adapter.itemCount - 1)
    post {
      prepareAdjacentPages()
      attachRecyclerObserver()
    }
  }

  fun applyPendingInitialPage() {
    if (pendingInitialPage !in 0 until adapter.itemCount) return
    if (pager.currentItem != pendingInitialPage) {
      pager.setCurrentItem(pendingInitialPage, false)
    }
    selectPage(pendingInitialPage)
  }

  fun attachedPageCount(): Int = (pager.getChildAt(0) as? RecyclerView)?.childCount ?: 0

  fun observedScrollableCount(): Int = originalRecyclerPadding.size

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
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
    pager.layout(0, 0, width, height + headerHeightPx)
    headerView?.layout(0, 0, width, headerHeightPx)
    stickyHeaderView?.layout(
      0,
      headerHeightPx,
      width,
      headerHeightPx + stickyHeaderHeightPx,
    )
    applyHeaderOffset()
    post {
      prepareAdjacentPages()
      attachRecyclerObserver()
    }
  }

  private fun setHeaderOffset(offset: Int) {
    val next = offset.coerceIn(0, headerHeightPx)
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
        pageOffsets[currentPageKey()] = recyclerView.computeVerticalScrollOffset()
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
    val topInset = headerHeightPx + stickyHeaderHeightPx
    recycler.clipToPadding = false
    val top = original.top + topInset
    // The taller pager already accounts for the translated header. Adding it
    // again leaves a full header-sized blank footer at the end of long lists.
    val bottom = original.bottom
    if (recycler.paddingLeft != original.left || recycler.paddingTop != top ||
      recycler.paddingRight != original.right || recycler.paddingBottom != bottom) {
      recycler.setPadding(original.left, top, original.right, bottom)
    }
    val key = pageKey(pageIndex)
    if (restoredRecyclerKeys[recycler] != key) {
      restoredRecyclerKeys[recycler] = key
      recycler.post {
        if (restoredRecyclerKeys[recycler] != key) return@post
        val saved = pageOffsets[key] ?: 0
        val current = recycler.computeVerticalScrollOffset()
        if (saved != current) recycler.scrollBy(0, saved - current)
      }
    }
  }

  private fun reapplyRecyclerInsets() {
    prepareAdjacentPages()
  }

  private fun restoreRecyclerInsets(recycler: RecyclerView) {
    val original = originalRecyclerPadding.remove(recycler) ?: return
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
    if (dy > 0 && headerOffsetPx < headerHeightPx) {
      val used = min(dy, headerHeightPx - headerOffsetPx)
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
    viewTreeObserver.addOnPreDrawListener(pageContentLayoutListener)
  }

  override fun onDetachedFromWindow() {
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
