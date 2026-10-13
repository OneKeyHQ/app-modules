package com.reactnativepagerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ScrollView
// OneKey patch: FrameLayout is inherited through NestedScrollableHost.
// import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.facebook.react.R as ReactR
import com.facebook.react.uimanager.events.NativeGestureUtil
import com.facebook.react.views.scroll.ReactScrollView
import com.margelo.nitro.nativelogger.OneKeyLog
import java.lang.ref.WeakReference
import java.util.WeakHashMap
import java.util.function.BiConsumer
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
  private enum class HeaderGestureOwner {
    NONE,
    LIST,
    HORIZONTAL_CHILD,
    HEADER_GUARD,
  }

  private data class RecyclerPadding(
    var left: Int,
    var top: Int,
    var right: Int,
    var bottom: Int,
    var clipToPadding: Boolean,
    var appliedTopInset: Int = 0,
  )

  private data class ScrollerPadding(
    var left: Int,
    var top: Int,
    var right: Int,
    var bottom: Int,
    var clipToPadding: Boolean,
    var appliedTopInset: Int = 0,
  )

  val pager = ViewPager2(context)
  internal val adapter = CollapsiblePagerAdapter()
  private val nativeTabBarView = CollapsiblePagerNativeTabBarView(context)
  private val nativeSubHeaderView = CollapsiblePagerNativeSubHeaderView(context)
  private val logicalChildren = ArrayList<View>()
  private val nestedScrollingParentHelper = NestedScrollingParentHelper(this)
  private val originalRecyclerPadding = WeakHashMap<RecyclerView, RecyclerPadding>()
  private val restoredRecyclerKeys = WeakHashMap<RecyclerView, String>()
  private val originalScrollerPadding = WeakHashMap<ScrollView, ScrollerPadding>()
  private val restoredScrollerKeys = WeakHashMap<ScrollView, String>()
  private val ownedScrollerKeys = WeakHashMap<NativeScrollerViewport, String>()
  private val ownedScrollerRestoreRequests = HashMap<String, Pair<WeakReference<NativeScrollerViewport>, Int?>>()
  private var foregroundRefreshIndicator: WeakReference<ImageView>? = null
  private var refreshForegroundWasVisible = false
  private val refreshForegroundMatrix = Matrix()
  private val refreshForegroundOrigin = FloatArray(2)
  private val refreshForegroundAncestors = ArrayList<View>()
  private var refreshForegroundAncestorAlpha = 1f
  private val pageOffsets = HashMap<String, Int>()
  private val pageHeaderOffsets = HashMap<String, Int>()
  private class RecyclerReadingAnchor(view: RecyclerView, val adapter: RecyclerView.Adapter<*>, val position: Int, val top: Int) {
    val view = WeakReference(view)
  }
  private val recyclerReadingAnchors = HashMap<String, RecyclerReadingAnchor>()
  private val displayRecyclerAnchors = HashMap<String, RecyclerReadingAnchor>()
  private var transitionSourceRecyclerAnchor: RecyclerReadingAnchor? = null
  private val commandBodyKeys = HashSet<String>()
  private var explicitTopCommand: Pair<String, PageRestoreRequest>? = null
  private class PagerTopRequest(view: View, val key: String, val pageIndex: Int, val generation: Int, val target: Int, val token: Int) {
    val view = WeakReference(view)
  }
  private var pagerTopRequest: PagerTopRequest? = null
  private var pagerTopToken = 0
  private var dispatchingPagerTop = false
  private val suspendedBodyKeys = HashSet<String>()
  private val alignedBodyKeys = HashSet<String>()
  private val transitionAlignedViews = WeakHashMap<View, String>()
  private var transitionSourceDisplayBody = 0
  private val displayBodyOverrides = HashMap<String, Int>()
  private val deferredRecyclerGeometry = WeakHashMap<RecyclerView, Triple<Int, Int, Int>>()
  private var transitionHeaderOffsets: Map<String, Int>? = null
  private var transitionPageOffsets: Map<String, Int>? = null
  private var transitionSourcePage = 0
  private var transitionTargetPage: Int? = null
  private var syntheticPageRestore = false
  private var resumeTransitionSource: Int? = null
  private val pendingPageRestores = HashSet<String>()
  private class PageRestoreRequest(view: View, val pageIndex: Int, val generation: Int) {
    val view = WeakReference(view)
  }
  private val pageRestoreRequests = HashMap<String, PageRestoreRequest>()

  private fun registerPageRestore(key: String, view: View, pageIndex: Int): PageRestoreRequest? {
    if (!nativeSmoothHeaderScrollEnabled) return null
    val previous = pageRestoreRequests[key]?.view?.get()
    if (previous is NativeScrollerViewport && previous !== view) previous.host.cancelPendingScrollRestore()
    ownedScrollerRestoreRequests.remove(key)
    return PageRestoreRequest(view, pageIndex, attachmentGeneration).also {
      pageRestoreRequests[key] = it
      pendingPageRestores.add(key)
      offsetDiag("request-register", view, key, pageIndex, it)
    }
  }

  private fun isCurrentPageRestore(key: String, request: PageRestoreRequest?): Boolean =
    !nativeSmoothHeaderScrollEnabled || (request != null && pageRestoreRequests[key] === request &&
      key in pendingPageRestores && request.generation == attachmentGeneration &&
      pageKey(request.pageIndex) == key)

  private fun clearPageRestoreForView(key: String, view: View, request: PageRestoreRequest? = null) {
    if (request != null && pageRestoreRequests[key] !== request) return
    if (pageRestoreRequests[key]?.view?.get() !== view) return
    offsetDiag("request-clear-before", view, key, request = request ?: pageRestoreRequests[key])
    pageRestoreRequests.remove(key)
    pendingPageRestores.remove(key)
    ownedScrollerRestoreRequests.remove(key)
    offsetDiag("request-clear-after", view, key, request = request)
  }
  private var observedRecyclerView: RecyclerView? = null
  private var observedRecyclerKey: String? = null
  private var observedScrollListener: RecyclerView.OnScrollListener? = null
  private var observedOwnedScroller: NativeScrollerViewport? = null
  private var observedOwnedScrollerKey: String? = null
  private var observedOwnedScrollerListener: ViewTreeObserver.OnScrollChangedListener? = null
  private var observedOwnedScrollerTreeObserver: ViewTreeObserver? = null
  private var observedNativeScroller: ScrollView? = null
  private var observedNativeScrollerKey: String? = null
  private var observedNativeScrollerListener: ViewTreeObserver.OnScrollChangedListener? = null
  private var observedNativeScrollerTreeObserver: ViewTreeObserver? = null
  private var observedPageRemoved = false
  private var attachmentGeneration = 0
  internal var headerSettledAtTop = false
    private set
  var onHeaderSettledStateChanged: (() -> Unit)? = null
  private var headerSettledOwner: WeakReference<View>? = null
  private var headerSettledKey: String? = null
  private var headerSettledGeneration = -1
  private var headerSettledGeometry: String? = null
  private var headerSettledSince = 0L
  private var headerSettledWasBusy = true
  private var headerSettledCheck: Runnable? = null
  private var headerCommandOwner: WeakReference<View>? = null

  private val touchSlopPx = ViewConfiguration.get(context).scaledTouchSlop
  private val hostIdentity = System.identityHashCode(this)
  private var headerView: View? = null
  private var stickyHeaderView: View? = null
  private var headerTouchActive = false
  private var headerTouchRegion = "none"
  private var headerGestureOwner = HeaderGestureOwner.NONE
  private var headerDownX = 0f
  private var headerDownY = 0f
  private var headerDownEvent: MotionEvent? = null
  private var headerHasHorizontalChild = false
  private var forwardedScrollable: View? = null
  private var pendingRefreshPull: WeakReference<SwipeRefreshLayout>? = null
  private var nativeGestureStarted = false
  private var pressCancelled = false
  private val pageContentLayoutListener = ViewTreeObserver.OnPreDrawListener {
    if (width > 0 && height > 0 && isShown) layoutPagerIfRequested()
    // Fabric can mount a retained list without another Android layout pass.
    // Apply its header insets before that list first draws.
    prepareAdjacentPages()
    attachPrimaryScrollObserver()
    finishPagerTopIfSettled()
    // OneKey patch: a filtered short page can reduce the existing collapse range.
    setHeaderOffset(headerOffsetPx)
    updateHeaderSettledState()
    if (offsetDiagEnabled() && offsetDiagNextDraw.isNotEmpty()) {
      val samples = offsetDiagNextDraw.entries.map { it.key to it.value }
      offsetDiagNextDraw.clear()
      samples.forEach { (view, owner) -> offsetDiag("restore-next-predraw", view, owner.first, owner.second) }
    }
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
      reapplyScrollableInsets()
      updateHeaderLayout()
    }
  var stickyHeaderHeightPx = 0
    set(value) {
      if (field == value) return
      field = max(0, value)
      reapplyScrollableInsets()
      updateHeaderLayout()
    }
  var headerOffsetPx = 0
    private set
  var pageKeys: List<String> = emptyList()
    set(value) {
      if (field == value) return
      pageRestoreRequests.entries.toList().forEach { (key, request) ->
        if ((value.getOrNull(request.pageIndex) ?: "page-${request.pageIndex}") != key) {
          (request.view.get() as? NativeScrollerViewport)?.host?.cancelPendingScrollRestore()
          pageRestoreRequests.remove(key)
          pendingPageRestores.remove(key)
          ownedScrollerRestoreRequests.remove(key)
        }
      }
      recyclerReadingAnchors.keys.retainAll(value.toSet())
      displayRecyclerAnchors.keys.retainAll(value.toSet())
      field = value
    }
  var retainedPages: String = "[]"
  var onHeaderOffsetChanged: (() -> Unit)? = null
  var nativeSmoothHeaderScrollEnabled = false
  var onNativeTabPress: ((Int, String) -> Unit)? = null
  var onNativeSubHeaderPress: ((Int, String) -> Unit)? = null

  var nativeTabBarHeight = 44.0
  var nativeTabBarContentPaddingHorizontal = 20.0
  var nativeTabBarItemSpacing = 8.0
  var nativeTabBarFontSize = 16.0
  var nativeTabBarFontFamily: String? = null
  var nativeTabBarBackgroundColor = android.graphics.Color.TRANSPARENT
  var nativeTabBarActiveTextColor = android.graphics.Color.BLACK
  var nativeTabBarInactiveTextColor = android.graphics.Color.GRAY
  var nativeTabBarIndicatorColor = android.graphics.Color.BLACK
  var nativeTabBarIndicatorHeight = 2.0
  var nativeTabBarIndicatorBottom = 0.0
  var nativeSubHeaderSelectedBackgroundColor = android.graphics.Color.TRANSPARENT

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
    nativeTabBarView.onItemPress = { index, key -> onNativeTabPress?.invoke(index, key) }
    nativeSubHeaderView.onItemPress = { index, key -> onNativeSubHeaderPress?.invoke(index, key) }
    super.addView(nativeTabBarView)
    super.addView(nativeSubHeaderView)
    applyNativeHeaderStyle()
    log("host-init generation=$attachmentGeneration")
  }

  private fun log(message: String) {
    OneKeyLog.debug("CollapsiblePager", "host=$hostIdentity $message")
  }

  private var offsetDiagSequence = 0L
  private val offsetDiagNextDraw = WeakHashMap<View, Pair<String, Int>>()

  private fun offsetDiagEnabled(): Boolean =
    (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

  private fun offsetDiagAfterDraw(view: View, key: String, pageIndex: Int) {
    if (offsetDiagEnabled()) offsetDiagNextDraw[view] = key to pageIndex
  }

  private fun offsetDiag(
    phase: String,
    view: View? = null,
    key: String = currentPageKey(),
    pageIndex: Int = pageKeys.indexOf(key),
    request: PageRestoreRequest? = pageRestoreRequests[key],
  ) {
    if (!offsetDiagEnabled()) return
    val safeKey = if (key in setOf("portfolio", "perps", "defi", "nft", "history")) key else "other"
    val observed = observedOwnedScroller ?: observedNativeScroller ?: observedRecyclerView
    val observedKey = observedOwnedScrollerKey ?: observedNativeScrollerKey ?: observedRecyclerKey
    val recycler = view as? RecyclerView
    val manager = recycler?.layoutManager as? LinearLayoutManager
    val first = manager?.findFirstVisibleItemPosition()
    val last = manager?.findLastVisibleItemPosition()
    val anchor = first?.takeIf { it != RecyclerView.NO_POSITION }?.let { manager?.findViewByPosition(it) }
    val child = (view as? ViewGroup)?.getChildAt(0)
    OneKeyLog.debug("CollapsiblePager", "[HOME-OFFSET-DIAG] phase=$phase seq=${++offsetDiagSequence} " +
      "wallMs=${System.currentTimeMillis()} monoMs=${SystemClock.uptimeMillis()} host=$hostIdentity " +
      "generation=$attachmentGeneration selectedPage=$selectedPage pagerPage=${pager.currentItem} targetPage=$pageIndex key=$safeKey " +
      "ownerPage=${pageKeys.indexOf(observedKey)} header=$headerOffsetPx headerHeight=$headerHeightPx stickyHeight=$stickyHeaderHeightPx " +
      "transition=${transitionHeaderOffsets != null} synthetic=$syntheticPageRestore pending=${key in pendingPageRestores} " +
      "requestId=${request?.let { System.identityHashCode(it) }} requestGeneration=${request?.generation} " +
      "requestPage=${request?.pageIndex} requestCurrent=${request != null && pageRestoreRequests[key] === request} " +
      "view=${view?.let { System.identityHashCode(it) }} tag=${view?.id} observed=${observed?.let { System.identityHashCode(it) }} " +
      "attached=${view?.isAttachedToWindow} estimatedOffset=${view?.let(::verticalScrollOffset)} " +
      "cachePresent=${pageOffsets.containsKey(key)} cache=${pageOffsets[key]} snapshot=${transitionPageOffsets?.get(key)} " +
      "boundsHeight=${view?.height} measuredHeight=${view?.measuredHeight} childHeight=${child?.height} childTop=${child?.top} " +
      "paddingTop=${view?.paddingTop} paddingBottom=${view?.paddingBottom} childCount=${(view as? ViewGroup)?.childCount} " +
      "itemCount=${recycler?.adapter?.itemCount} first=$first last=$last anchorTag=${anchor?.id} " +
      "anchorTop=${anchor?.let { manager?.getDecoratedTop(it) }} anchorRelative=${anchor?.let { manager?.getDecoratedTop(it)?.minus(recycler?.paddingTop ?: 0) }} " +
      "range=${recycler?.computeVerticalScrollRange()} extent=${recycler?.computeVerticalScrollExtent()} " +
      "canUp=${view?.canScrollVertically(-1)} canDown=${view?.canScrollVertically(1)} scrollState=${recycler?.scrollState} computing=${recycler?.isComputingLayout}")
  }

  private fun outerPagerIndex(): Int {
    var ancestor = parent as? View
    while (ancestor != null) {
      if (ancestor is ViewPager2) return ancestor.currentItem
      ancestor = ancestor.parent as? View
    }
    return -1
  }

  fun updateNativeTabBarItems(value: String?) {
    nativeTabBarView.updateItemsJSON(value)
    bringNativeHeadersToFront()
    updateHeaderLayout()
  }

  fun updateNativeSubHeader(value: String?) {
    nativeSubHeaderView.updateConfigJSON(value)
    applyNativeHeaderStyle()
    bringNativeHeadersToFront()
    updateHeaderLayout()
  }

  fun updateNativeTabProgress(position: Int, offset: Float) {
    nativeTabBarView.setProgress(position + offset)
  }

  fun logPagerState(reason: String) {
    log(
      "pager-state reason=$reason inner=$selectedPage outer=${outerPagerIndex()} " +
        "nativePages=${adapter.itemCount} attachedPages=${attachedPageCount()} " +
        "headerOffset=$headerOffsetPx retained=$retainedPages",
    )
  }

  fun setPagerLayoutDirection(layoutDirection: Int) {
    pager.layoutDirection = layoutDirection
    nativeTabBarView.layoutDirection = layoutDirection
    nativeSubHeaderView.layoutDirection = layoutDirection
    nativeSubHeaderView.requestLayout()
  }

  fun applyNativeHeaderStyle() {
    nativeTabBarView.updateStyle(
      nativeTabBarHeight,
      nativeTabBarContentPaddingHorizontal,
      nativeTabBarItemSpacing,
      nativeTabBarFontSize,
      nativeTabBarFontFamily,
      nativeTabBarIndicatorHeight,
      nativeTabBarIndicatorBottom,
    )
    nativeTabBarView.updateColors(
      nativeTabBarBackgroundColor,
      nativeTabBarActiveTextColor,
      nativeTabBarInactiveTextColor,
      nativeTabBarIndicatorColor,
    )
    nativeSubHeaderView.updateColors(
      nativeTabBarBackgroundColor,
      nativeTabBarActiveTextColor,
      nativeTabBarInactiveTextColor,
      nativeSubHeaderSelectedBackgroundColor,
      nativeTabBarFontFamily,
    )
    updateHeaderLayout()
  }

  private fun bringNativeHeadersToFront() {
    nativeTabBarView.bringToFront()
    nativeSubHeaderView.bringToFront()
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
    bringNativeHeadersToFront()
    requestLayout()
    if (safeIndex >= PAGE_SLOT_OFFSET) {
      log("page-attach slot=${safeIndex - PAGE_SLOT_OFFSET} nativePages=${adapter.itemCount}")
    }
  }

  fun removeReactChild(child: View) {
    releaseRefreshForeground()
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
        val observed = observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller
        if (observed != null && isDescendant(observed, child)) {
          // The page detaches only later; its scroller must not keep writing this key.
          detachPrimaryScrollObserver(recordOffset = false)
          observedPageRemoved = true
        }
        when (val scrollable =
          findExplicitNativeScrollerInView(child) ?: findVerticalScrollableView(child)) {
          is RecyclerView -> restoreRecyclerInsets(scrollable)
          is ScrollView -> restoreNativeScrollerInsets(scrollable)
          is NativeScrollerViewport -> restoreOwnedScrollerInsets(scrollable)
        }
        adapter.removePage(child)
        log("page-detach slot=${index - PAGE_SLOT_OFFSET} nativePages=${adapter.itemCount}")
      }
    }
  }

  fun removeAllReactChildren() {
    releaseRefreshForeground()
    detachPrimaryScrollObserver()
    pageRestoreRequests.clear()
    pendingPageRestores.clear()
    commandBodyKeys.clear()
    explicitTopCommand = null
    suspendedBodyKeys.clear()
    alignedBodyKeys.clear()
    transitionAlignedViews.clear()
    deferredRecyclerGeometry.clear()
    displayBodyOverrides.clear()
    displayRecyclerAnchors.clear()
    ownedScrollerRestoreRequests.clear()
    recyclerReadingAnchors.clear()
    transitionSourceRecyclerAnchor = null
    // Weak keys may disappear after size is read; Collection.toArray tolerates this.
    for (recycler in originalRecyclerPadding.keys.toTypedArray()) {
      restoreRecyclerInsets(recycler)
    }
    for (scroller in originalScrollerPadding.keys.toTypedArray()) {
      restoreNativeScrollerInsets(scroller)
    }
    for (scroller in ownedScrollerKeys.keys.toTypedArray()) restoreOwnedScrollerInsets(scroller)
    headerView?.let { view -> super.removeView(view) }
    stickyHeaderView?.let { view -> super.removeView(view) }
    headerView = null
    stickyHeaderView = null
    logicalChildren.clear()
    adapter.clear()
    hasAppliedInitialPage = false
    log("pages-clear")
  }

  fun reactChildCount() = logicalChildren.size

  fun reactChildAt(index: Int) = logicalChildren[index]

  internal fun beginPageTransition(target: Int? = null) {
    cancelPagerTop()
    if (!nativeSmoothHeaderScrollEnabled || headerHeightPx <= 0) return
    if (transitionHeaderOffsets == null) {
      transitionSourceDisplayBody = primaryScrollableForPage(selectedPage)?.let(::verticalScrollOffset) ?: 0
      transitionSourceRecyclerAnchor = (primaryScrollableForPage(selectedPage) as? RecyclerView)?.let(::captureRecyclerAnchor)
      pageHeaderOffsets[currentPageKey()] = headerOffsetPx
      detachPrimaryScrollObserver()
      transitionSourcePage = selectedPage
      transitionHeaderOffsets = pageHeaderOffsets.toMap()
      transitionPageOffsets = pageOffsets.toMap()
    }
    if (target != null) {
      transitionTargetPage = target
      alignPageBody(target)
    }
    offsetDiag("transition-begin-source=$transitionSourcePage", primaryScrollableForPage(selectedPage), pageKey(target ?: selectedPage), target ?: selectedPage)
  }

  internal fun onPagerScrolled(position: Int, offset: Float) {
    if (transitionHeaderOffsets == null) return
    alignPageBody(position)
    if (position + 1 < adapter.itemCount) alignPageBody(position + 1)
    updateNativeTabProgress(position, offset)
  }

  internal fun onPagerScrollStateChanged(state: Int) {
    updateHeaderSettledState(forceBusy = state != ViewPager2.SCROLL_STATE_IDLE)
    if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
      beginPageTransition()
    } else if (state == ViewPager2.SCROLL_STATE_IDLE && transitionHeaderOffsets != null) {
      finishPageTransition(pager.currentItem)
    }
  }

  private fun finishPageTransition(position: Int) {
    val headers = transitionHeaderOffsets ?: return
    detachPrimaryScrollObserver(recordOffset = false)
    releaseRefreshForeground()
    selectedPage = position.coerceIn(0, adapter.itemCount - 1)
    syntheticPageRestore = true
    setHeaderOffset(headers[pageKey(transitionSourcePage)] ?: headerOffsetPx)
    transitionHeaderOffsets = null
    transitionPageOffsets = null
    transitionTargetPage = null
    syntheticPageRestore = false
    transitionAlignedViews.clear()
    if (selectedPage == transitionSourcePage) {
      displayBodyOverrides[currentPageKey()] = transitionSourceDisplayBody
      transitionSourceRecyclerAnchor?.let { displayRecyclerAnchors[currentPageKey()] = it }
    } else {
      displayBodyOverrides.remove(currentPageKey())
      displayRecyclerAnchors.remove(currentPageKey())
    }
    transitionSourceRecyclerAnchor = null
    if (headerOffsetPx == headerHeightPx) suspendedBodyKeys.remove(currentPageKey())
    else if ((pageOffsets[currentPageKey()] ?: 0) > 0) suspendedBodyKeys.add(currentPageKey())
    alignPageBody(selectedPage, force = true)
    offsetDiag("transition-finish", primaryScrollableForPage(selectedPage), currentPageKey(), selectedPage)
    prepareAdjacentPages()
    attachPrimaryScrollObserver()
  }

  fun selectPage(position: Int) {
    offsetDiag("select-begin", observedOwnedScroller ?: observedNativeScroller ?: observedRecyclerView, pageKey(position), position)
    pendingRefreshPull = null
    if (adapter.itemCount == 0) return
    if (nativeSmoothHeaderScrollEnabled && headerHeightPx > 0) {
      if (transitionHeaderOffsets != null) return
      beginPageTransition(position)
      finishPageTransition(position)
      return
    }
    detachPrimaryScrollObserver()
    releaseRefreshForeground()
    selectedPage = position.coerceIn(0, adapter.itemCount - 1)
    val savedOffset = pageOffsets[currentPageKey()]
      ?: primaryScrollableForPage(selectedPage)?.let(::verticalScrollOffset)
      ?: 0
    offsetDiag("select-offset=$savedOffset-source=${if (pageOffsets[currentPageKey()] != null) "saved" else "live"}")
    if (savedOffset > 0) {
      offsetDiag("select-force-collapse-before")
      setHeaderOffset(headerHeightPx)
      offsetDiag("select-force-collapse-after")
    }
    postForCurrentAttachment {
      prepareAdjacentPages()
      attachPrimaryScrollObserver()
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

  fun observedScrollableCount(): Int =
    originalRecyclerPadding.size + originalScrollerPadding.size + ownedScrollerKeys.size

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
    val nativeTabHeight = if (nativeTabBarView.visibility == View.VISIBLE) {
      nativeTabBarView.preferredHeightPx()
    } else 0
    nativeTabBarView.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(nativeTabHeight, MeasureSpec.EXACTLY),
    )
    val nativeSubHeaderHeight = if (nativeSubHeaderView.visibility == View.VISIBLE) {
      nativeSubHeaderView.preferredHeightPx()
    } else 0
    nativeSubHeaderView.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(nativeSubHeaderHeight, MeasureSpec.EXACTLY),
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
    val nativeTabHeight = nativeTabBarView.measuredHeight
    nativeTabBarView.layout(0, headerHeightPx, width, headerHeightPx + nativeTabHeight)
    nativeSubHeaderView.layout(
      0,
      headerHeightPx + nativeTabHeight,
      width,
      headerHeightPx + nativeTabHeight + nativeSubHeaderView.measuredHeight,
    )
    applyHeaderOffset()
    postForCurrentAttachment {
      prepareAdjacentPages()
      attachPrimaryScrollObserver()
    }
  }

  private fun restorationBodyOffset(key: String): Int {
    val saved = if (transitionPageOffsets != null && key == pageKey(transitionSourcePage)) transitionSourceDisplayBody
      else displayBodyOverrides[key] ?: transitionPageOffsets?.get(key) ?: pageOffsets[key] ?: 0
    return if (nativeSmoothHeaderScrollEnabled && headerOffsetPx < headerHeightPx) 0 else saved
  }

  private fun captureRecyclerAnchor(view: RecyclerView): RecyclerReadingAnchor? {
    val manager = view.layoutManager as? LinearLayoutManager ?: return null
    if (manager.orientation != RecyclerView.VERTICAL) return null
    val position = manager.findFirstVisibleItemPosition()
    val child = manager.findViewByPosition(position) ?: return null
    val adapter = view.adapter ?: return null
    return RecyclerReadingAnchor(view, adapter, position, manager.getDecoratedTop(child) - view.paddingTop)
  }

  private fun saveRecyclerReadingAnchor(key: String, view: RecyclerView) {
    captureRecyclerAnchor(view)?.let { recyclerReadingAnchors[key] = it }
  }

  private fun restoreRecyclerAnchor(view: RecyclerView, key: String, index: Int, request: PageRestoreRequest?): Boolean {
    if (!nativeSmoothHeaderScrollEnabled || pageKey(index) != key ||
        primaryScrollableForPage(index) !== view || !isCurrentPageRestore(key, request) ||
        !isAttachedToWindow || !view.isAttachedToWindow || view.width <= 0 || view.height <= 0 ||
        isInLayout || view.isInLayout || view.isComputingLayout) return false
    val manager = view.layoutManager as? LinearLayoutManager
    val reading = recyclerReadingAnchors[key]?.takeIf { it.view.get() === view && it.adapter === view.adapter }
    val display = (if (transitionPageOffsets != null && index == transitionSourcePage) transitionSourceRecyclerAnchor
      else displayRecyclerAnchors[key])?.takeIf { it.view.get() === view && it.adapter === view.adapter }
    if (manager == null || manager.orientation != RecyclerView.VERTICAL ||
      (headerOffsetPx == headerHeightPx && reading == null && display == null && restorationBodyOffset(key) > 0)) {
      val saved = restorationBodyOffset(key)
      val current = view.computeVerticalScrollOffset()
      val previousSynthetic = syntheticPageRestore
      syntheticPageRestore = true
      try { if (saved != current) view.scrollBy(0, saved - current) }
      finally { syntheticPageRestore = previousSynthetic }
      return saved <= view.computeVerticalScrollOffset() || view.canScrollVertically(1)
    }
    val target = if (headerOffsetPx < headerHeightPx) null else display ?: reading
    val position = target?.position ?: 0
    val top = target?.top ?: 0
    if (position !in 0 until (view.adapter?.itemCount ?: 0)) return false
    fun reached(): Boolean {
      val actual = captureRecyclerAnchor(view) ?: return false
      return actual.position == position && actual.top == top
    }
    if (!reached()) {
      if (!isAttachedToWindow || !view.isAttachedToWindow || width <= 0 || height <= 0 ||
          view.width <= 0 || view.height <= 0 || isInLayout || view.isInLayout || view.isComputingLayout ||
          pageKey(index) != key || primaryScrollableForPage(index) !== view || !isCurrentPageRestore(key, request)) return false
      val previousSynthetic = syntheticPageRestore
      syntheticPageRestore = true
      try {
        manager.scrollToPositionWithOffset(position, top)
        // Use the existing inset commit boundary once, before the target's first frame.
        view.forceLayout()
        view.measure(MeasureSpec.makeMeasureSpec(view.width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(view.height, MeasureSpec.EXACTLY))
        view.layout(view.left, view.top, view.right, view.bottom)
      } finally {
        syntheticPageRestore = previousSynthetic
      }
      offsetDiag("recycler-anchor-restore-position=$position-top=$top", view, key, index, request)
    }
    val readingStillUnreachable = display != null && reading != null &&
      (reading.position != position || reading.top != top) && !view.canScrollVertically(1)
    return reached() && !readingStillUnreachable
  }

  private fun alignPageBody(index: Int, force: Boolean = false) {
    if (!nativeSmoothHeaderScrollEnabled || index !in 0 until adapter.itemCount) return
    val view = primaryScrollableForPage(index) ?: return
    val key = pageKey(index)
    if (!force && transitionAlignedViews[view] == key) return
    transitionAlignedViews[view] = key
    if (transitionPageOffsets != null && index != transitionSourcePage) {
      displayBodyOverrides.remove(key)
      displayRecyclerAnchors.remove(key)
    }
    alignedBodyKeys.add(key)
    if (headerOffsetPx < headerHeightPx && (pageOffsets[key] ?: 0) > 0) suspendedBodyKeys.add(key)
    val previousSynthetic = syntheticPageRestore
    syntheticPageRestore = true
    when (view) {
      is NativeScrollerViewport -> {
        ownedScrollerKeys.remove(view)
        applyOwnedScrollerInsets(view, index)
        view.host.restoreScrollOffset(restorationBodyOffset(key))
      }
      is RecyclerView -> {
        restoredRecyclerKeys.remove(view)
        applyRecyclerInsets(view, index)
        restoreRecyclerAnchor(view, key, index, pageRestoreRequests[key])
      }
      is ScrollView -> {
        restoredScrollerKeys.remove(view)
        applyNativeScrollerInsets(view, index)
        if (view.height > 0 && view.childCount > 0) view.scrollTo(view.scrollX, restorationBodyOffset(key))
      }
    }
    syntheticPageRestore = previousSynthetic
  }

  private fun takeOverBodyForVerticalConsumption() {
    cancelPagerTop()
    if (!nativeSmoothHeaderScrollEnabled || transitionHeaderOffsets != null || syntheticPageRestore) return
    val key = currentPageKey()
    commandBodyKeys.remove(key)
    explicitTopCommand = null
    suspendedBodyKeys.remove(key)
    alignedBodyKeys.remove(key)
    displayBodyOverrides.remove(key)
    displayRecyclerAnchors.remove(key)
    pageOffsets[key] = primaryScrollableForPage(selectedPage)?.let(::verticalScrollOffset) ?: 0
    (primaryScrollableForPage(selectedPage) as? RecyclerView)?.let { saveRecyclerReadingAnchor(key, it) }
  }

  internal fun acceptObservedScrollCommand(view: View, explicitTop: Boolean, phase: Int = 0) {
    if (dispatchingPagerTop && matchesPagerTop(view)) return
    if (phase == 0 && view === (observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller)) cancelPagerTop()
    if (phase != 0 && matchesPagerTop(view)) { finishPagerTopIfSettled(); return }
    val owner = observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller
    val stateOwner = observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller
    val stateKey = observedRecyclerKey ?: observedOwnedScrollerKey ?: observedNativeScrollerKey
    if (view === stateOwner && stateKey == currentPageKey() && view.isAttachedToWindow && isAttachedToWindow && transitionHeaderOffsets == null && !syntheticPageRestore) {
      updateHeaderSettledState()
      if (phase == 1) headerCommandOwner = WeakReference(view)
      else if (phase == 0 || phase == 2) headerCommandOwner = null
      updateHeaderSettledState(forceBusy = phase != 2)
    }
    if (!nativeSmoothHeaderScrollEnabled || view !== owner || transitionHeaderOffsets != null || syntheticPageRestore ||
      !isAttachedToWindow || !view.isAttachedToWindow) return
    val key = currentPageKey()
    val observedKey = observedRecyclerKey ?: observedOwnedScrollerKey ?: observedNativeScrollerKey
    if (observedKey != key) return
    if (phase == 1) { commandBodyKeys.add(key); return }
    if (phase == 2) {
      if (!commandBodyKeys.remove(key)) return
      val completingExplicitTop = hasExplicitTopCommand(view, key)
      val manager = (view as? RecyclerView)?.layoutManager as? LinearLayoutManager
      val first = manager?.findViewByPosition(0)
      val atExplicitTop = completingExplicitTop && manager != null && manager.orientation == RecyclerView.VERTICAL &&
        manager.findFirstVisibleItemPosition() == 0 && first != null &&
        manager.getDecoratedTop(first) - view.paddingTop == 0
      explicitTopCommand = null
      pageOffsets[key] = if (atExplicitTop) 0 else verticalScrollOffset(view)
      if (view is RecyclerView) saveRecyclerReadingAnchor(key, view)
      if (atExplicitTop) setHeaderOffset(0)
      else if ((pageOffsets[key] ?: 0) > 0) setHeaderOffset(headerHeightPx)
      offsetDiag("command-result-complete", view, key)
      return
    }
    commandBodyKeys.remove(key)
    explicitTopCommand = if (explicitTop && view is RecyclerView) {
      key to PageRestoreRequest(view, selectedPage, attachmentGeneration)
    } else null
    pageRestoreRequests.remove(key)
    pendingPageRestores.remove(key)
    ownedScrollerRestoreRequests.remove(key)
    if (view is RecyclerView) deferredRecyclerGeometry.remove(view)
    (view as? NativeScrollerViewport)?.host?.cancelPendingScrollRestore()
    suspendedBodyKeys.remove(key)
    alignedBodyKeys.remove(key)
    displayBodyOverrides.remove(key)
    displayRecyclerAnchors.remove(key)
    if (view is RecyclerView) saveRecyclerReadingAnchor(key, view)
    pageOffsets[key] = if (explicitTop) 0 else verticalScrollOffset(view)
    if (explicitTop) setHeaderOffset(0)
    offsetDiag("command-body-takeover-top=$explicitTop", view, key)
  }

  private fun matchesPagerTop(view: View): Boolean {
    val request = pagerTopRequest ?: return false
    val owner = observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller
    val observedKey = observedRecyclerKey ?: observedOwnedScrollerKey ?: observedNativeScrollerKey
    return request.view.get() === view && owner === view && observedKey == request.key &&
      request.key == currentPageKey() && request.pageIndex == selectedPage &&
      request.generation == attachmentGeneration && view.isAttachedToWindow && isAttachedToWindow
  }

  private fun cancelPagerTop() {
    pagerTopRequest?.let { commandBodyKeys.remove(it.key) }
    pagerTopRequest = null
  }

  private fun recyclerIsAtBodyTop(view: RecyclerView): Boolean {
    val manager = view.layoutManager as? LinearLayoutManager ?: return !view.canScrollVertically(-1)
    val first = manager.findViewByPosition(0) ?: return view.adapter?.itemCount == 0
    return manager.orientation == RecyclerView.VERTICAL && manager.findFirstVisibleItemPosition() == 0 &&
      manager.getDecoratedTop(first) - view.paddingTop == 0
  }

  private fun finishPagerTopIfSettled() {
    val request = pagerTopRequest ?: return
    val view = request.view.get() ?: run { cancelPagerTop(); return }
    if (!matchesPagerTop(view)) { cancelPagerTop(); return }
    if (dispatchingPagerTop) return
    val settled = when (view) {
      is RecyclerView -> !view.isComputingLayout && view.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
        view.layoutManager?.isSmoothScrolling != true && recyclerIsAtBodyTop(view)
      is NativeScrollerViewport -> !view.isReadingMotionActive() && view.scrollY == 0
      is ScrollView -> view.scrollY == 0
      else -> false
    }
    if (!settled) return
    pageOffsets[request.key] = 0
    if (view is RecyclerView) saveRecyclerReadingAnchor(request.key, view)
    setHeaderOffset(if (request.target == 1) headerHeightPx else 0)
    offsetDiag("pager-top-complete-target=${request.target}-token=${request.token}", view, request.key)
    cancelPagerTop()
  }

  internal fun scrollToTop(target: Int, animated: Boolean) {
    if (target !in 0..1 || pager.scrollState != ViewPager2.SCROLL_STATE_IDLE || transitionHeaderOffsets != null ||
      syntheticPageRestore || selectedPage !in 0 until adapter.itemCount || !isAttachedToWindow) return
    val view = observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller ?: return
    val key = currentPageKey()
    val ownerKey = observedRecyclerKey ?: observedOwnedScrollerKey ?: observedNativeScrollerKey
    if (ownerKey != key || !view.isAttachedToWindow || primaryScrollableForPage(selectedPage) !== view) return
    // Finish old child notifications before installing the new Pager-owned target.
    cancelPagerTop()
    if (view is RecyclerView) {
      (view.getTag(R.id.onekey_native_scroll_coordinator_command_cancel_listener) as? Runnable)?.run()
      view.stopScroll()
    } else if (view is NativeScrollerViewport) {
      view.stopMotion()
      view.host.cancelPendingScrollRestore()
    }
    explicitTopCommand = null
    pageRestoreRequests.remove(key)
    pendingPageRestores.remove(key)
    ownedScrollerRestoreRequests.remove(key)
    suspendedBodyKeys.remove(key)
    alignedBodyKeys.remove(key)
    displayBodyOverrides.remove(key)
    displayRecyclerAnchors.remove(key)
    recyclerReadingAnchors.remove(key)
    if (view is RecyclerView) deferredRecyclerGeometry.remove(view)
    pageOffsets[key] = 0
    val request = PagerTopRequest(view, key, selectedPage, attachmentGeneration, target, ++pagerTopToken)
    pagerTopRequest = request
    commandBodyKeys.add(key)
    setHeaderOffset(if (target == 1) headerHeightPx else 0)
    offsetDiag("pager-top-begin-target=$target-animated=$animated-token=${request.token}", view, key)
    dispatchingPagerTop = true
    try {
      when (view) {
        is RecyclerView -> {
          val manager = view.layoutManager as? LinearLayoutManager
          if (animated && !recyclerIsAtBodyTop(view)) view.smoothScrollToPosition(0)
          else if (manager != null && manager.orientation == RecyclerView.VERTICAL) {
            manager.scrollToPositionWithOffset(0, 0)
            if (view.width > 0 && view.height > 0 && !isInLayout && !view.isInLayout && !view.isComputingLayout) {
              view.measure(MeasureSpec.makeMeasureSpec(view.width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(view.height, MeasureSpec.EXACTLY))
              view.layout(view.left, view.top, view.right, view.bottom)
            }
          } else view.scrollToPosition(0)
        }
        is NativeScrollerViewport -> view.host.scrollToPosition(0, 0, animated)
        is ScrollView -> if (animated) view.smoothScrollTo(0, 0) else view.scrollTo(0, 0)
      }
    } finally { dispatchingPagerTop = false }
    finishPagerTopIfSettled()
  }

  private fun hasExplicitTopCommand(view: View, key: String): Boolean {
    val command = explicitTopCommand ?: return false
    val request = command.second
    return view is RecyclerView && observedRecyclerView === view && observedRecyclerKey == key &&
      key == currentPageKey() && command.first == key && request.view.get() === view &&
      request.pageIndex == selectedPage && request.generation == attachmentGeneration
  }

  private fun publishScrollCommandListener(recycler: RecyclerView, remove: Boolean = false) {
    val tag = R.id.onekey_native_scroll_coordinator_command_listener
    @Suppress("UNCHECKED_CAST")
    val existing = recycler.getTag(tag) as? MutableMap<Any, BiConsumer<Boolean, Int>>
    val listeners = existing ?: if (remove) return else mutableMapOf<Any, BiConsumer<Boolean, Int>>().also { recycler.setTag(tag, it) }
    if (remove) listeners.remove(this)
    else {
      val generation = attachmentGeneration
      val key = currentPageKey()
      listeners[this] = BiConsumer { explicitTop, phase ->
        if (generation == attachmentGeneration && key == currentPageKey()) acceptObservedScrollCommand(recycler, explicitTop, phase)
      }
    }
  }

  private fun maximumHeaderOffset(): Int {
    if (nativeSmoothHeaderScrollEnabled) return headerHeightPx
    val scrollable = observedRecyclerView
      ?: observedOwnedScroller
      ?: observedNativeScroller
      ?: primaryScrollableForPage(selectedPage)
      ?: return headerHeightPx
    val childCount = (scrollable as? ViewGroup)?.childCount ?: 0
    if (childCount == 0 || scrollable.canScrollVertically(-1) || scrollable.canScrollVertically(1)) {
      return headerHeightPx
    }
    // OneKey patch: short pages use the original rounded-DIP minimum content height.
    val density = resources.displayMetrics.density
    val contentHeight = ((height / density).roundToInt() + (headerHeightPx / density).roundToInt()) * density
    return (contentHeight.roundToInt() - height).coerceIn(0, headerHeightPx)
  }

  internal fun headerStatePageKey(): String = currentPageKey()

  private fun publishHeaderSettledState(settled: Boolean, identityChanged: Boolean = false) {
    if (headerSettledAtTop == settled && !identityChanged) return
    headerSettledAtTop = settled
    log("header-settled-state page=$selectedPage generation=$attachmentGeneration settled=$settled")
    onHeaderSettledStateChanged?.invoke()
  }

  private fun cancelHeaderSettledCheck() {
    headerSettledCheck?.let(::removeCallbacks)
    headerSettledCheck = null
  }

  private fun updateHeaderSettledState(forceBusy: Boolean = false) {
    val owner = observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller
    val ownerKey = observedRecyclerKey ?: observedOwnedScrollerKey ?: observedNativeScrollerKey
    val valid = isAttachedToWindow && width > 0 && height > 0 && headerHeightPx > 0 &&
      headerView?.isAttachedToWindow == true && owner != null && owner.isAttachedToWindow &&
      owner.width > 0 && owner.height > 0 && ownerKey == currentPageKey() &&
      selectedPage in 0 until adapter.itemCount && isDescendant(owner, adapter.pageAt(selectedPage))
    val identityChanged = headerSettledOwner?.get() !== owner || headerSettledKey != currentPageKey() ||
      headerSettledGeneration != attachmentGeneration
    if (identityChanged) {
      cancelHeaderSettledCheck()
      headerCommandOwner = null
      headerSettledOwner = owner?.let(::WeakReference)
      headerSettledKey = currentPageKey()
      headerSettledGeneration = attachmentGeneration
      headerSettledGeometry = null
      headerSettledWasBusy = true
      publishHeaderSettledState(false, true)
    }
    if (!valid || owner == null || headerOffsetPx != 0) {
      cancelHeaderSettledCheck()
      headerSettledGeometry = null
      headerSettledWasBusy = true
      publishHeaderSettledState(false)
      return
    }
    val windowPoint = IntArray(2)
    owner.getLocationInWindow(windowPoint)
    val recycler = owner as? RecyclerView
    val manager = recycler?.layoutManager as? LinearLayoutManager
    val first = manager?.findFirstVisibleItemPosition() ?: -1
    val firstTop = manager?.let { layoutManager ->
      layoutManager.findViewByPosition(first)?.let(layoutManager::getDecoratedTop)
    } ?: 0
    val content = (owner as? ViewGroup)?.getChildAt(0)
    val geometry = listOf(headerOffsetPx, headerHeightPx, stickyHeaderHeightPx, width, height,
      owner.width, owner.height, owner.paddingTop, owner.paddingBottom, owner.scrollY,
      windowPoint[0], windowPoint[1], first, firstTop, recycler?.adapter?.itemCount ?: 0,
      content?.height ?: 0, headerView?.height ?: 0).joinToString(":")
    val busy = forceBusy || pager.isFakeDragging || pager.scrollState != ViewPager2.SCROLL_STATE_IDLE ||
      transitionHeaderOffsets != null || syntheticPageRestore || pagerTopRequest != null ||
      headerCommandOwner?.get() === owner ||
      nestedScrollingParentHelper.nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0 ||
      (recycler != null && (recycler.scrollState != RecyclerView.SCROLL_STATE_IDLE ||
        recycler.isComputingLayout || recycler.layoutManager?.isSmoothScrolling == true)) ||
      (owner is NativeScrollerViewport && owner.isReadingMotionActive())
    val changed = geometry != headerSettledGeometry
    val now = SystemClock.uptimeMillis()
    if (changed || busy || headerSettledWasBusy) {
      headerSettledSince = now
      headerSettledGeometry = geometry
      cancelHeaderSettledCheck()
      publishHeaderSettledState(false)
    }
    headerSettledWasBusy = busy
    if (!busy && now - headerSettledSince >= 150L) {
      cancelHeaderSettledCheck()
      publishHeaderSettledState(true)
      return
    }
    if (headerSettledCheck == null) {
      val weakOwner = WeakReference(owner)
      val key = currentPageKey()
      val generation = attachmentGeneration
      val check = Runnable {
        headerSettledCheck = null
        if (generation == attachmentGeneration && key == currentPageKey() && weakOwner.get() ===
          (observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller)) updateHeaderSettledState()
      }
      headerSettledCheck = check
      postDelayed(check, 150L)
    }
  }

  private fun setHeaderOffset(offset: Int) {
    // val next = offset.coerceIn(0, headerHeightPx)
    val pending = nativeSmoothHeaderScrollEnabled && currentPageKey() in pendingPageRestores
    val maximum = if (transitionHeaderOffsets != null || pending || pagerTopRequest != null) headerHeightPx else maximumHeaderOffset()
    val next = offset.coerceIn(0, maximum)
    if (headerOffsetPx == next) return
    headerOffsetPx = next
    updateHeaderSettledState(forceBusy = true)
    if (nativeSmoothHeaderScrollEnabled && transitionHeaderOffsets == null && !syntheticPageRestore && !pending) {
      pageHeaderOffsets[currentPageKey()] = next
    }
    applyHeaderOffset()
  }

  private fun publishConsumedOffset(recycler: View, remove: Boolean = false) {
    val key = R.id.onekey_native_scroll_coordinator_consumed_offsets
    val existing = recycler.getTag(key) as? NativeScrollCoordinatorContributions
    val contributions = existing ?: if (remove) return else NativeScrollCoordinatorContributions().also {
      recycler.setTag(key, it)
    }
    val changed = if (remove) contributions.clearOwner(this) else contributions.update(this, headerOffsetPx)
    if (changed) {
      (recycler.getTag(R.id.onekey_native_scroll_coordinator_offset_listener) as? Runnable)?.run()
    }
    if (contributions.isEmpty()) recycler.setTag(key, null)
  }

  private fun applyHeaderOffset() {
    val offset = headerOffsetPx.toFloat()
    pager.translationY = -offset
    headerView?.translationY = -offset
    stickyHeaderView?.translationY = -offset
    nativeTabBarView.translationY = -offset
    nativeSubHeaderView.translationY = -offset
    observedRecyclerView?.let { publishConsumedOffset(it) }
    // Adjacent owned pages are translated too; unchanged values are not republished.
    for (scroller in ownedScrollerKeys.keys) publishConsumedOffset(scroller)
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

  private fun isVerticalRecyclerView(view: RecyclerView): Boolean {
    val manager = view.layoutManager
    return manager !is LinearLayoutManager || manager.orientation == RecyclerView.VERTICAL
  }

  private fun isExplicitNativeScroller(view: View): Boolean {
    val nativeId = view.getTag(ReactR.id.view_tag_native_id) as? String ?: return false
    return nativeId.startsWith(NATIVE_SCROLLER_ID_PREFIX)
  }

  private fun findExplicitNativeScrollerInView(view: View): View? {
    if (view is NativeScrollerView) return view.viewport
    if (view !== this && view is CollapsiblePagerHost) {
      return view.currentPrimaryScrollableForParent()
    }
    if (isExplicitNativeScroller(view) &&
      (view is ScrollView || view is RecyclerView && isVerticalRecyclerView(view))) {
      return view
    }
    if (view is ViewGroup) {
      for (index in 0 until view.childCount) {
        findExplicitNativeScrollerInView(view.getChildAt(index))?.let { return it }
      }
    }
    return null
  }

  private fun findVerticalScrollableView(view: View): View? {
    if (view is NativeScrollerView) return view.viewport
    if (view !== this && view is CollapsiblePagerHost) {
      return view.currentPrimaryScrollableForParent()
    }
    if (view is RecyclerView && isVerticalRecyclerView(view)) return view
    if (view is ScrollView) return view
    if (view is ViewGroup) {
      for (index in 0 until view.childCount) {
        findVerticalScrollableView(view.getChildAt(index))?.let { return it }
      }
    }
    return null
  }

  private fun primaryScrollableForPage(index: Int): View? {
    if (index !in 0 until adapter.itemCount) return null
    val page = adapter.pageAt(index)
    return findExplicitNativeScrollerInView(page) ?: findVerticalScrollableView(page)
  }

  internal fun currentPrimaryScrollableForParent(): View? =
    primaryScrollableForPage(selectedPage)

  private fun recyclerViewForPage(index: Int): RecyclerView? =
    primaryScrollableForPage(index) as? RecyclerView

  private fun verticalScrollOffset(view: View): Int = when (view) {
    is RecyclerView -> view.computeVerticalScrollOffset()
    is ScrollView -> view.scrollY
    is NativeScrollerViewport -> view.scrollY
    else -> 0
  }

  private fun prepareAdjacentPages() {
    val first = max(0, selectedPage - 1)
    val last = min(adapter.itemCount - 1, selectedPage + 1)
    for (index in first..last) {
      when (val scrollable = primaryScrollableForPage(index)) {
        is RecyclerView -> applyRecyclerInsets(scrollable, index)
        is ScrollView -> applyNativeScrollerInsets(scrollable, index)
        is NativeScrollerViewport -> applyOwnedScrollerInsets(scrollable, index)
      }
    }
    updateRefreshForeground()
  }

  private fun attachPrimaryScrollObserver() {
    if (transitionHeaderOffsets != null) return
    if (selectedPage !in 0 until adapter.itemCount) return
    val previous = observedRecyclerView ?: observedOwnedScroller ?: observedNativeScroller
    if (observedPageRemoved || previous != null && !isDescendant(previous, adapter.pageAt(selectedPage))) {
      offsetDiag("primary-replacement-before-removed=$observedPageRemoved", previous)
      observedPageRemoved = false
      detachPrimaryScrollObserver(recordOffset = !nativeSmoothHeaderScrollEnabled)
      // A replacement scroller starts at the beginning while retaining header collapse.
      offsetDiag("primary-replacement-cache-before", previous)
      if (!nativeSmoothHeaderScrollEnabled) {
        offsetDiag("cache-write-replacement-new=0", previous)
        pageOffsets[currentPageKey()] = 0
      }
      offsetDiag("primary-replacement-cache-after", previous)
    }
    when (val scrollable = primaryScrollableForPage(selectedPage)) {
      is RecyclerView -> attachRecyclerObserver(scrollable)
      is ScrollView -> attachNativeScrollerObserver(scrollable)
      is NativeScrollerViewport -> attachOwnedScrollerObserver(scrollable)
    }
  }

  private fun attachRecyclerObserver(recycler: RecyclerView) {
    if (observedRecyclerView === recycler && observedRecyclerKey == currentPageKey()) {
      applyRecyclerInsets(recycler, selectedPage)
      publishConsumedOffset(recycler)
      return
    }

    detachPrimaryScrollObserver()
    offsetDiag("recycler-attach-before", recycler)
    observedRecyclerView = recycler
    observedRecyclerKey = currentPageKey()
    publishScrollCommandListener(recycler)
    applyRecyclerInsets(recycler, selectedPage)
    publishConsumedOffset(recycler)
    val key = currentPageKey()
    val listener = object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (observedRecyclerView === recyclerView) updateHeaderSettledState(forceBusy = newState != RecyclerView.SCROLL_STATE_IDLE)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) finishPagerTopIfSettled()
      }
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (observedRecyclerView === recyclerView && (dx != 0 || dy != 0)) updateHeaderSettledState(forceBusy = true)
        if (observedScrollListener !== this || observedRecyclerKey != key ||
          key != currentPageKey() || !isAttachedToWindow ||
          transitionHeaderOffsets != null || syntheticPageRestore || key in pendingPageRestores || key in alignedBodyKeys) {
          if (offsetDiagEnabled() && (dx != 0 || dy != 0 || pageOffsets[key] != recyclerView.computeVerticalScrollOffset())) {
            offsetDiag("recycler-observer-skip-dx=$dx-dy=$dy-listenerCurrent=${observedScrollListener === this}", recyclerView, key)
          }
          return
        }
        if (nativeSmoothHeaderScrollEnabled && key !in commandBodyKeys &&
          (dy == 0 || recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE)) return
        val contentOffset = recyclerView.computeVerticalScrollOffset()
        if (pageOffsets[key] != contentOffset) offsetDiag("cache-write-recycler-observer-new=$contentOffset-dx=$dx-dy=$dy", recyclerView, key)
        pageOffsets[key] = contentOffset
        if (nativeSmoothHeaderScrollEnabled) saveRecyclerReadingAnchor(key, recyclerView)
        // Nested touch scrolling moves list content only after the header has
        // collapsed. Programmatic list jumps bypass those parent callbacks.
        if (contentOffset > 0 && headerOffsetPx < headerHeightPx && !hasExplicitTopCommand(recyclerView, key) && !matchesPagerTop(recyclerView)) {
          offsetDiag("recycler-positive-collapse-before", recyclerView)
          setHeaderOffset(headerHeightPx)
          offsetDiag("recycler-positive-collapse-after", recyclerView)
        }
      }
    }
    observedScrollListener = listener
    recycler.addOnScrollListener(listener)
    log(
      "list-observer-attach inner=$selectedPage key=${currentPageKey()} " +
        "recycler=${System.identityHashCode(recycler)}",
    )
  }

  private fun detachRecyclerObserver(recordOffset: Boolean) {
    cancelPagerTop()
    explicitTopCommand = null
    val recycler = observedRecyclerView
    val listener = observedScrollListener
    if (recycler != null) {
      offsetDiag("recycler-detach-save-before-record=$recordOffset", recycler, observedRecyclerKey ?: currentPageKey())
      if (recordOffset && !nativeSmoothHeaderScrollEnabled && transitionPageOffsets == null) observedRecyclerKey?.let {
        if (it !in pendingPageRestores) {
          offsetDiag("cache-write-recycler-detach-new=${recycler.computeVerticalScrollOffset()}", recycler, it)
          pageOffsets[it] = recycler.computeVerticalScrollOffset()
        }
      }
      offsetDiag("recycler-detach-save-after", recycler, observedRecyclerKey ?: currentPageKey())
      publishConsumedOffset(recycler, remove = true)
      publishScrollCommandListener(recycler, remove = true)
    }
    if (recycler != null && listener != null) recycler.removeOnScrollListener(listener)
    if (recycler != null) {
      log(
        "list-observer-detach inner=$selectedPage key=${currentPageKey()} " +
          "recycler=${System.identityHashCode(recycler)}",
      )
    }
    observedRecyclerView = null
    observedRecyclerKey = null
    observedScrollListener = null
  }

  private fun attachNativeScrollerObserver(scroller: ScrollView) {
    if (observedNativeScroller === scroller && observedNativeScrollerKey == currentPageKey()) {
      applyNativeScrollerInsets(scroller, selectedPage)
      return
    }

    detachPrimaryScrollObserver()
    offsetDiag("scrollview-attach-before", scroller)
    observedNativeScroller = scroller
    observedNativeScrollerKey = currentPageKey()
    applyNativeScrollerInsets(scroller, selectedPage)
    val key = currentPageKey()
    val listener = object : ViewTreeObserver.OnScrollChangedListener {
      override fun onScrollChanged() {
        if (observedNativeScrollerListener !== this) {
          removeScrollChangedListener(null, scroller, this)
          return
        }
        if (key != currentPageKey() || !isAttachedToWindow ||
          transitionHeaderOffsets != null || syntheticPageRestore || key in pendingPageRestores || key in alignedBodyKeys) return
        if (nativeSmoothHeaderScrollEnabled && nestedScrollingParentHelper.nestedScrollAxes == 0 && key !in commandBodyKeys) return
        val contentOffset = scroller.scrollY
        if (pageOffsets[key] != contentOffset) offsetDiag("cache-write-scrollview-observer-new=$contentOffset", scroller, key)
        pageOffsets[key] = contentOffset
        if (contentOffset > 0 && headerOffsetPx < headerHeightPx && !matchesPagerTop(scroller)) {
          offsetDiag("scrollview-positive-collapse-before", scroller)
          setHeaderOffset(headerHeightPx)
          offsetDiag("scrollview-positive-collapse-after", scroller)
        }
      }
    }
    observedNativeScrollerListener = listener
    observedNativeScrollerTreeObserver = scroller.viewTreeObserver.also {
      it.addOnScrollChangedListener(listener)
    }
    log(
      "scroller-observer-attach inner=$selectedPage key=${currentPageKey()} " +
        "scroller=${System.identityHashCode(scroller)}",
    )
  }

  private fun detachNativeScrollerObserver(recordOffset: Boolean) {
    cancelPagerTop()
    val scroller = observedNativeScroller
    val listener = observedNativeScrollerListener
    offsetDiag("scrollview-detach-save-before-record=$recordOffset", scroller, observedNativeScrollerKey ?: currentPageKey())
    if (scroller != null && recordOffset && !nativeSmoothHeaderScrollEnabled && transitionPageOffsets == null) observedNativeScrollerKey?.let {
      if (it !in pendingPageRestores) {
        offsetDiag("cache-write-scrollview-detach-new=${scroller.scrollY}", scroller, it)
        pageOffsets[it] = scroller.scrollY
      }
    }
    offsetDiag("scrollview-detach-save-after", scroller, observedNativeScrollerKey ?: currentPageKey())
    if (scroller != null && listener != null) {
      removeScrollChangedListener(observedNativeScrollerTreeObserver, scroller, listener)
    }
    if (scroller != null) {
      log(
        "scroller-observer-detach inner=$selectedPage key=${currentPageKey()} " +
          "scroller=${System.identityHashCode(scroller)}",
      )
    }
    observedNativeScroller = null
    observedNativeScrollerKey = null
    observedNativeScrollerListener = null
    observedNativeScrollerTreeObserver = null
  }

  private fun detachPrimaryScrollObserver(recordOffset: Boolean = true) {
    cancelHeaderSettledCheck()
    headerCommandOwner = null
    headerSettledGeometry = null
    headerSettledWasBusy = true
    publishHeaderSettledState(false)
    detachOwnedScrollerObserver(recordOffset)
    detachRecyclerObserver(recordOffset)
    detachNativeScrollerObserver(recordOffset)
  }

  private fun removeScrollChangedListener(
    registered: ViewTreeObserver?,
    scroller: View,
    listener: ViewTreeObserver.OnScrollChangedListener,
  ) {
    // A detached scroller returns a new floating observer, while the listener is
    // still registered on the window observer. Try every instance that can own it.
    for (observer in arrayOf(registered, scroller.viewTreeObserver, viewTreeObserver)) {
      if (observer != null && observer.isAlive) observer.removeOnScrollChangedListener(listener)
    }
  }

  private fun applyOwnedScrollerInsets(scroller: NativeScrollerViewport, pageIndex: Int) {
    scroller.host.setPagerInset(this, headerHeightPx + stickyHeaderHeightPx)
    publishConsumedOffset(scroller)
    val key = pageKey(pageIndex)
    if (ownedScrollerKeys[scroller] == key) return
    ownedScrollerKeys[scroller] = key
    val restoreRequest = registerPageRestore(key, scroller, pageIndex)
    if (nativeSmoothHeaderScrollEnabled) ownedScrollerRestoreRequests[key] = WeakReference(scroller) to null
    val generation = attachmentGeneration
    offsetDiag("owned-enqueue", scroller, key, pageIndex, restoreRequest)
    postForCurrentAttachment {
      offsetDiag("owned-execute", scroller, key, pageIndex, restoreRequest)
      if (isCurrentPageRestore(key, restoreRequest) && ownedScrollerKeys[scroller] == key && generation == attachmentGeneration &&
        scroller.isAttachedToWindow && (!nativeSmoothHeaderScrollEnabled ||
          (key in pendingPageRestores && ownedScrollerRestoreRequests[key]?.first?.get() === scroller))) {
        offsetDiag("owned-restore-before", scroller, key, pageIndex, restoreRequest)
        val saved = restorationBodyOffset(key)
        scroller.host.restoreScrollOffset(saved)
        if (nativeSmoothHeaderScrollEnabled) {
          ownedScrollerRestoreRequests[key] = WeakReference(scroller) to generation
          recordOwnedScrollerOffset(scroller, key)
        }
        offsetDiag("owned-restore-after-saved=$saved", scroller, key, pageIndex, restoreRequest)
        offsetDiagAfterDraw(scroller, key, pageIndex)
      }
    }
  }

  private fun restoreOwnedScrollerInsets(scroller: NativeScrollerViewport) {
    scroller.stopMotion()
    scroller.host.cancelPendingScrollRestore()
    ownedScrollerKeys.remove(scroller)?.let { key ->
      clearPageRestoreForView(key, scroller)
      if (ownedScrollerRestoreRequests[key]?.first?.get() === scroller) ownedScrollerRestoreRequests.remove(key)
    }
    publishConsumedOffset(scroller, remove = true)
    scroller.host.setPagerInset(this, null)
  }

  private fun attachOwnedScrollerObserver(scroller: NativeScrollerViewport) {
    if (observedOwnedScroller === scroller && observedOwnedScrollerKey == currentPageKey()) {
      applyOwnedScrollerInsets(scroller, selectedPage)
      if (nativeSmoothHeaderScrollEnabled) recordOwnedScrollerOffset(scroller, currentPageKey())
      return
    }
    detachPrimaryScrollObserver()
    offsetDiag("owned-attach-before", scroller)
    observedOwnedScroller = scroller
    observedOwnedScrollerKey = currentPageKey()
    val key = currentPageKey()
    applyOwnedScrollerInsets(scroller, selectedPage)
    val listener = object : ViewTreeObserver.OnScrollChangedListener {
      override fun onScrollChanged() {
        if (observedOwnedScrollerListener !== this) {
          removeScrollChangedListener(null, scroller, this)
          return
        }
        recordOwnedScrollerOffset(scroller, key)
      }
    }
    observedOwnedScrollerListener = listener
    observedOwnedScrollerTreeObserver = scroller.viewTreeObserver.also {
      it.addOnScrollChangedListener(listener)
    }
  }

  private fun recordOwnedScrollerOffset(scroller: NativeScrollerViewport, key: String) {
    if (ownedScrollerKeys[scroller] != key || !isAttachedToWindow || !scroller.isAttachedToWindow) return
    if (key in pendingPageRestores) {
      // A false native pending flag before our posted request executes is not cancellation.
      val request = ownedScrollerRestoreRequests[key]
      if (request == null || request.first.get() !== scroller || request.second != attachmentGeneration ||
        scroller.width <= 0 || scroller.height <= 0 || scroller.host.hasPendingScrollRestore()) return
      clearPageRestoreForView(key, scroller)
    }
    if (observedOwnedScroller !== scroller || observedOwnedScrollerKey != key ||
      key != currentPageKey() || transitionHeaderOffsets != null || syntheticPageRestore || key in alignedBodyKeys) return
    if (nativeSmoothHeaderScrollEnabled && !scroller.isReadingMotionActive() && key !in commandBodyKeys) return
    val contentOffset = scroller.scrollY
    if (pageOffsets[key] != contentOffset) offsetDiag("cache-write-owned-observer-new=$contentOffset", scroller, key)
    pageOffsets[key] = contentOffset
    if (contentOffset > 0 && headerOffsetPx < headerHeightPx && !matchesPagerTop(scroller)) {
      offsetDiag("owned-positive-collapse-before", scroller, key)
      setHeaderOffset(headerHeightPx)
      offsetDiag("owned-positive-collapse-after", scroller, key)
    }
  }

  private fun detachOwnedScrollerObserver(recordOffset: Boolean) {
    cancelPagerTop()
    val scroller = observedOwnedScroller ?: return
    scroller.stopMotion()
    publishConsumedOffset(scroller, remove = true)
    offsetDiag("owned-detach-save-before-record=$recordOffset", scroller, observedOwnedScrollerKey ?: currentPageKey())
    if (recordOffset && !nativeSmoothHeaderScrollEnabled && transitionPageOffsets == null) observedOwnedScrollerKey?.let {
      if (it !in pendingPageRestores) {
        offsetDiag("cache-write-owned-detach-new=${scroller.scrollY}", scroller, it)
        pageOffsets[it] = scroller.scrollY
      }
    }
    offsetDiag("owned-detach-save-after", scroller, observedOwnedScrollerKey ?: currentPageKey())
    observedOwnedScrollerListener?.let {
      removeScrollChangedListener(observedOwnedScrollerTreeObserver, scroller, it)
    }
    observedOwnedScroller = null
    observedOwnedScrollerKey = null
    observedOwnedScrollerListener = null
    observedOwnedScrollerTreeObserver = null
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
      offsetDiag("recycler-inset-before-nextTop=$top-anchorPosition=$first-anchorOffset=$anchorOffset", recycler, pageKey(pageIndex), pageIndex)
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
    if (original.appliedTopInset != topInset) offsetDiag("recycler-inset-after-old=${original.appliedTopInset}-new=$topInset", recycler, pageKey(pageIndex), pageIndex)
    original.appliedTopInset = topInset
    val key = pageKey(pageIndex)
    val geometry = Triple(recycler.computeVerticalScrollRange(), recycler.computeVerticalScrollExtent(), recycler.childCount)
    deferredRecyclerGeometry[recycler]?.let { deferred ->
      if (geometry != deferred) {
        restoredRecyclerKeys.remove(recycler)
        deferredRecyclerGeometry.remove(recycler)
        if (nativeSmoothHeaderScrollEnabled && headerOffsetPx == headerHeightPx) {
          displayBodyOverrides.remove(key)
          displayRecyclerAnchors.remove(key)
        }
      }
    }
    if (restoredRecyclerKeys[recycler] != key) {
      restoredRecyclerKeys[recycler] = key
      val restoreRequest = registerPageRestore(key, recycler, pageIndex)
      val generation = attachmentGeneration
      offsetDiag("recycler-enqueue", recycler, key, pageIndex, restoreRequest)
      if (nativeSmoothHeaderScrollEnabled && recycler.height > 0 && recycler.childCount > 0) {
        // Consume an available reading anchor before this pre-draw reaches the first frame.
        restoreRecyclerAnchor(recycler, key, pageIndex, restoreRequest)
      }
      recycler.post {
        offsetDiag("recycler-execute", recycler, key, pageIndex, restoreRequest)
        if (!isCurrentPageRestore(key, restoreRequest)) {
          offsetDiag("recycler-post-skip-cancelled", recycler, key, pageIndex, restoreRequest)
          return@post
        }
        if (!isAttachedToWindow || generation != attachmentGeneration ||
          restoredRecyclerKeys[recycler] != key ||
          originalRecyclerPadding[recycler] !== original) {
          if (restoredRecyclerKeys[recycler] == key) restoredRecyclerKeys.remove(recycler)
          clearPageRestoreForView(key, recycler, restoreRequest)
          return@post
        }
        val saved = restorationBodyOffset(key)
        val current = recycler.computeVerticalScrollOffset()
        offsetDiag("recycler-restore-before-saved=$saved-current=${current}-requestedDelta=${saved - current}", recycler, key, pageIndex, restoreRequest)
        if (nativeSmoothHeaderScrollEnabled && (recycler.height <= 0 || recycler.childCount == 0)) {
          restoredRecyclerKeys.remove(recycler)
          if (pageRestoreRequests[key] === restoreRequest) pageRestoreRequests.remove(key)
          return@post
        }
        if (nativeSmoothHeaderScrollEnabled) {
          if (!restoreRecyclerAnchor(recycler, key, pageIndex, restoreRequest)) {
            if (isInLayout || recycler.isInLayout || recycler.isComputingLayout) {
              restoredRecyclerKeys.remove(recycler)
              if (pageRestoreRequests[key] === restoreRequest) pageRestoreRequests.remove(key)
              return@post
            }
            deferredRecyclerGeometry[recycler] = Triple(recycler.computeVerticalScrollRange(), recycler.computeVerticalScrollExtent(), recycler.childCount)
            offsetDiag("recycler-anchor-pending-saved=$saved", recycler, key, pageIndex, restoreRequest)
            return@post
          }
        } else if (saved != current) recycler.scrollBy(0, saved - current)
        deferredRecyclerGeometry.remove(recycler)
        if (nativeSmoothHeaderScrollEnabled) clearPageRestoreForView(key, recycler, restoreRequest)
        else pendingPageRestores.remove(key)
        offsetDiag("recycler-restore-after-saved=$saved", recycler, key, pageIndex, restoreRequest)
        offsetDiagAfterDraw(recycler, key, pageIndex)
      }
    }
  }

  private fun applyNativeScrollerInsets(scroller: ScrollView, pageIndex: Int) {
    val original = originalScrollerPadding.getOrPut(scroller) {
      ScrollerPadding(
        scroller.paddingLeft,
        scroller.paddingTop,
        scroller.paddingRight,
        scroller.paddingBottom,
        scroller.clipToPadding,
      )
    }
    updateScrollerPaddingOwnership(scroller, original)
    val topInset = headerHeightPx + stickyHeaderHeightPx
    scroller.clipToPadding = false
    if (scroller is ReactScrollView) {
      // Fabric owns the content child's layout, so ScrollView padding alone
      // cannot move it. RN's native ScrollAway API also updates Fabric culling.
      val state = scroller.reactScrollViewScrollState
      val scrollAwayTop = nativeScrollerScrollAwayTop(
        state.scrollAwayPaddingTop, original.appliedTopInset, topInset,
      )
      if (state.scrollAwayPaddingTop != scrollAwayTop ||
        scroller.getChildAt(0)?.translationY != scrollAwayTop.toFloat()) {
        scroller.setScrollAwayPaddingEnabledUnstable(scrollAwayTop, state.scrollAwayPaddingBottom)
      }
      val bottom = original.bottom + topInset
      if (scroller.paddingLeft != original.left || scroller.paddingTop != original.top ||
        scroller.paddingRight != original.right || scroller.paddingBottom != bottom) {
        scroller.setPadding(original.left, original.top, original.right, bottom)
      }
    } else {
      val top = original.top + topInset
      if (scroller.paddingLeft != original.left || scroller.paddingTop != top ||
        scroller.paddingRight != original.right || scroller.paddingBottom != original.bottom) {
        scroller.setPadding(original.left, top, original.right, original.bottom)
      }
    }
    original.appliedTopInset = topInset

    val key = pageKey(pageIndex)
    if (restoredScrollerKeys[scroller] != key) {
      restoredScrollerKeys[scroller] = key
      val restoreRequest = registerPageRestore(key, scroller, pageIndex)
      val generation = attachmentGeneration
      offsetDiag("scrollview-enqueue", scroller, key, pageIndex, restoreRequest)
      scroller.post {
        offsetDiag("scrollview-execute", scroller, key, pageIndex, restoreRequest)
        if (!isCurrentPageRestore(key, restoreRequest)) {
          offsetDiag("scrollview-post-skip-cancelled", scroller, key, pageIndex, restoreRequest)
          return@post
        }
        if (!isAttachedToWindow || generation != attachmentGeneration ||
          restoredScrollerKeys[scroller] != key ||
          originalScrollerPadding[scroller] !== original) {
          if (restoredScrollerKeys[scroller] == key) restoredScrollerKeys.remove(scroller)
          clearPageRestoreForView(key, scroller, restoreRequest)
          return@post
        }
        val saved = restorationBodyOffset(key)
        offsetDiag("scrollview-restore-before-saved=$saved-current=${scroller.scrollY}-requestedDelta=${saved - scroller.scrollY}", scroller, key, pageIndex, restoreRequest)
        if (nativeSmoothHeaderScrollEnabled &&
          (scroller.height <= 0 || (scroller.getChildAt(0)?.height ?: 0) <= 0)) {
          restoredScrollerKeys.remove(scroller)
          if (pageRestoreRequests[key] === restoreRequest) pageRestoreRequests.remove(key)
          return@post
        }
        if (scroller.scrollY != saved) scroller.scrollTo(scroller.scrollX, saved)
        if (nativeSmoothHeaderScrollEnabled) clearPageRestoreForView(key, scroller, restoreRequest)
        else pendingPageRestores.remove(key)
        offsetDiag("scrollview-restore-after-saved=$saved", scroller, key, pageIndex, restoreRequest)
        offsetDiagAfterDraw(scroller, key, pageIndex)
      }
    }
  }

  private fun updateScrollerPaddingOwnership(
    scroller: ScrollView,
    original: ScrollerPadding,
  ) {
    val isReactScroller = scroller is ReactScrollView
    if (scroller is ReactScrollView) {
      val state = scroller.reactScrollViewScrollState
      if (nativeScrollerPaddingWasResetByScrollAway(
          original.appliedTopInset,
          scroller.paddingLeft, scroller.paddingTop, scroller.paddingRight, scroller.paddingBottom,
          state.scrollAwayPaddingTop, state.scrollAwayPaddingBottom,
        )) {
        // RN may replay ScrollAway state before this pass. Its setter clears
        // caller padding; that native reset must not become our new baseline.
        return
      }
    }
    val ownedTop = if (isReactScroller) 0 else original.appliedTopInset
    val ownedBottom = if (isReactScroller) original.appliedTopInset else 0
    if (scroller.paddingLeft != original.left) original.left = scroller.paddingLeft
    if (scroller.paddingTop != original.top + ownedTop) {
      original.top = (scroller.paddingTop - ownedTop).coerceAtLeast(0)
    }
    if (scroller.paddingRight != original.right) original.right = scroller.paddingRight
    if (scroller.paddingBottom != original.bottom + ownedBottom) {
      original.bottom = (scroller.paddingBottom - ownedBottom).coerceAtLeast(0)
    }
    if (scroller.clipToPadding) original.clipToPadding = true
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

  private fun reapplyScrollableInsets() {
    prepareAdjacentPages()
  }

  private fun restoreRecyclerInsets(recycler: RecyclerView) {
    publishConsumedOffset(recycler, remove = true)
    val original = originalRecyclerPadding.remove(recycler) ?: return
    updateRecyclerPaddingOwnership(recycler, original)
    restoredRecyclerKeys.remove(recycler)?.let { clearPageRestoreForView(it, recycler) }
    recycler.clipToPadding = original.clipToPadding
    recycler.setPadding(original.left, original.top, original.right, original.bottom)
  }

  private fun restoreNativeScrollerInsets(scroller: ScrollView) {
    val original = originalScrollerPadding.remove(scroller) ?: return
    updateScrollerPaddingOwnership(scroller, original)
    restoredScrollerKeys.remove(scroller)?.let { clearPageRestoreForView(it, scroller) }
    if (scroller is ReactScrollView) {
      val state = scroller.reactScrollViewScrollState
      scroller.setScrollAwayPaddingEnabledUnstable(
        nativeScrollerScrollAwayTop(state.scrollAwayPaddingTop, original.appliedTopInset, 0),
        state.scrollAwayPaddingBottom,
      )
    }
    scroller.clipToPadding = original.clipToPadding
    scroller.setPadding(original.left, original.top, original.right, original.bottom)
  }

  private fun refreshLayoutFor(scrollable: View): SwipeRefreshLayout? {
    if (scrollable !is ScrollView && scrollable !is RecyclerView && scrollable !is NativeScrollerViewport) return null
    var ancestor = scrollable.parent
    while (ancestor != null && ancestor !is SwipeRefreshLayout) ancestor = ancestor.parent
    return ancestor as? SwipeRefreshLayout
  }

  private fun refreshIndicatorFor(refresh: SwipeRefreshLayout): ImageView? {
    for (index in 0 until refresh.childCount) {
      val child = refresh.getChildAt(index)
      if (child is ImageView && child.drawable is CircularProgressDrawable) return child
    }
    return null
  }

  private fun refreshForegroundOwner(scrollable: View): CollapsiblePagerHost? {
    var owner: CollapsiblePagerHost? = null
    var ancestor = scrollable.parent
    while (ancestor != null) {
      if (ancestor is CollapsiblePagerHost && ancestor.nativeSmoothHeaderScrollEnabled &&
        ancestor.isAttachedToWindow && ancestor.isShown &&
        ancestor.primaryScrollableForPage(ancestor.selectedPage) === scrollable) {
        owner = ancestor
      }
      ancestor = ancestor.parent
    }
    return owner
  }

  private fun updateRefreshForeground() {
    val scrollable = primaryScrollableForPage(selectedPage)
    val refresh = scrollable?.takeIf { refreshForegroundOwner(it) === this }?.let(::refreshLayoutFor)
    val indicator = refresh?.let(::refreshIndicatorFor)
    if (foregroundRefreshIndicator?.get() !== indicator) releaseRefreshForeground()
    if (indicator != null) {
      if (foregroundRefreshIndicator?.get() !== indicator) {
        foregroundRefreshIndicator = WeakReference(indicator)
      }
      val state = refreshIndicatorAlphas.getOrPut(indicator) {
        NativeRefreshForegroundAlpha(indicator.alpha)
      }
      val alpha = state.update(indicator.alpha, this, acquire = true)
      if (indicator.alpha != alpha) indicator.alpha = alpha
    }
    val visible = indicator?.isShown == true &&
      (refreshIndicatorAlphas[indicator]?.drawingAlpha ?: 0f) > 0f
    // The foreground lies outside the original child's invalidation rectangle.
    // Repaint once on disappearance as well as throughout the native animation.
    if (visible || refreshForegroundWasVisible) postInvalidateOnAnimation()
    refreshForegroundWasVisible = visible
  }

  private fun releaseRefreshForeground() {
    val indicator = foregroundRefreshIndicator?.get()
    foregroundRefreshIndicator = null
    if (indicator != null) {
      refreshIndicatorAlphas[indicator]?.let { state ->
        indicator.alpha = state.update(indicator.alpha, this, acquire = false)
        if (state.isEmpty) refreshIndicatorAlphas.remove(indicator)
      }
    }
    if (indicator != null || refreshForegroundWasVisible) invalidate()
    refreshForegroundWasVisible = false
  }

  private fun refreshMatrixInHost(refresh: View): Boolean {
    refreshForegroundMatrix.reset()
    refreshForegroundAncestorAlpha = 1f
    refreshForegroundAncestors.clear()
    try {
      var current: View? = refresh
      while (current != null && current !== this) {
        refreshForegroundAncestors.add(current)
        current = current.parent as? View
      }
      if (current !== this) return false
      for (index in refreshForegroundAncestors.lastIndex downTo 0) {
        val child = refreshForegroundAncestors[index]
        val parent = child.parent as? View ?: return false
        refreshForegroundMatrix.preTranslate(
          (child.left - parent.scrollX).toFloat(), (child.top - parent.scrollY).toFloat(),
        )
        refreshForegroundMatrix.preConcat(child.matrix)
        refreshForegroundAncestorAlpha *= child.alpha
      }
      return true
    } finally {
      refreshForegroundAncestors.clear()
    }
  }

  override fun dispatchDraw(canvas: Canvas) {
    // The original parent advances native refresh animations and their listeners.
    super.dispatchDraw(canvas)
    val indicator = foregroundRefreshIndicator?.get() ?: return
    val scrollable = primaryScrollableForPage(selectedPage) ?: return
    if (refreshForegroundOwner(scrollable) !== this) return
    val refresh = refreshLayoutFor(scrollable) ?: return
    if (refreshIndicatorFor(refresh) !== indicator || !indicator.isShown ||
      indicator.width <= 0 || indicator.height <= 0 || !refreshMatrixInHost(refresh)) return
    val alpha = (refreshIndicatorAlphas[indicator]?.drawingAlpha ?: return) *
      refreshForegroundAncestorAlpha
    if (alpha <= 0f) return
    refreshForegroundOrigin[0] = 0f
    refreshForegroundOrigin[1] = 0f
    refreshForegroundMatrix.mapPoints(refreshForegroundOrigin)
    val appliedInset = (refresh.getTag(R.id.onekey_native_scroll_coordinator_refresh_inset) as? Number)
      ?.toFloat() ?: 0f
    val save = canvas.save()
    try {
      // Pin the refresh container's origin to this Host top while retaining its
      // horizontal page position, native scale/pivot, and caller translation.
      canvas.translate(0f, -refreshForegroundOrigin[1])
      canvas.concat(refreshForegroundMatrix)
      canvas.translate(indicator.left.toFloat(), nativeRefreshForegroundTop(indicator.top, appliedInset))
      canvas.concat(indicator.matrix)
      if (alpha < 1f) {
        canvas.saveLayerAlpha(
          0f, 0f, indicator.width.toFloat(), indicator.height.toFloat(),
          (alpha * 255f).roundToInt().coerceIn(0, 255),
        )
      }
      // Direct draw deliberately bypasses the alpha used to hide the original
      // RenderNode. Do not advance Animation.getTransformation a second time.
      indicator.draw(canvas)
    } finally {
      canvas.restoreToCount(save)
    }
  }

  private fun headerRegionAt(y: Float): String? {
    val headerBottom = headerHeightPx - headerOffsetPx
    val stickyBottom = headerHeightPx + stickyHeaderHeightPx - headerOffsetPx
    return when {
      y < 0 || y >= stickyBottom -> null
      y < headerBottom -> "header"
      y < headerBottom + nativeTabBarView.measuredHeight &&
        nativeTabBarView.visibility == View.VISIBLE -> "primary-tab"
      y < headerBottom + nativeTabBarView.measuredHeight + nativeSubHeaderView.measuredHeight &&
        nativeSubHeaderView.visibility == View.VISIBLE -> "secondary-header"
      else -> "sticky-controls"
    }
  }

  private fun deepestChildAt(group: ViewGroup, x: Float, y: Float): View {
    for (index in group.childCount - 1 downTo 0) {
      val child = group.getChildAt(index)
      if (child.visibility != View.VISIBLE || child.alpha <= 0f) continue
      val localX = x + group.scrollX - child.left - child.translationX
      val localY = y + group.scrollY - child.top - child.translationY
      if (localX < 0 || localY < 0 || localX >= child.width || localY >= child.height) continue
      return if (child is ViewGroup) deepestChildAt(child, localX, localY) else child
    }
    return group
  }

  private fun hasHorizontalScrollOwner(x: Float, y: Float): Boolean {
    var target: View? = deepestChildAt(this, x, y)
    while (target != null && target !== this) {
      if (
        target is HorizontalScrollView ||
        target.canScrollHorizontally(-1) ||
        target.canScrollHorizontally(1) ||
        (target is RecyclerView &&
          (target.layoutManager as? LinearLayoutManager)?.orientation == RecyclerView.HORIZONTAL)
      ) {
        return true
      }
      target = target.parent as? View
    }
    return false
  }

  private fun cancelHeaderPress(event: MotionEvent, owner: String) {
    if (pressCancelled) return
    pressCancelled = true
    val cancel = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
    super.dispatchTouchEvent(cancel)
    cancel.recycle()
    if (!nativeGestureStarted) {
      NativeGestureUtil.notifyNativeGestureStarted(this, event)
      nativeGestureStarted = true
    }
    log(
      "press-cancel owner=$owner region=$headerTouchRegion " +
        "inner=$selectedPage outer=${outerPagerIndex()}",
    )
  }

  private fun dispatchToScrollable(scrollable: View, event: MotionEvent): Boolean {
    val hostLocation = IntArray(2)
    val scrollableLocation = IntArray(2)
    getLocationOnScreen(hostLocation)
    scrollable.getLocationOnScreen(scrollableLocation)
    val copy = MotionEvent.obtain(event)
    copy.offsetLocation(
      (hostLocation[0] - scrollableLocation[0]).toFloat(),
      (hostLocation[1] - scrollableLocation[1]).toFloat(),
    )
    val handled = scrollable.dispatchTouchEvent(copy)
    copy.recycle()
    return handled
  }

  private fun beginForwardingToPrimaryScrollable(event: MotionEvent): Boolean {
    val scrollable = primaryScrollableForPage(selectedPage) ?: return false
    forwardedScrollable = scrollable
    headerDownEvent?.let { down -> dispatchToScrollable(scrollable, down) }
    return dispatchToScrollable(scrollable, event)
  }

  private fun finishHeaderGesture(event: MotionEvent) {
    val owner = headerGestureOwner.name.lowercase()
    if (nativeGestureStarted || nestedNativeGestureStarted) {
      NativeGestureUtil.notifyNativeGestureEnded(this, event)
      nativeGestureStarted = false
      nestedNativeGestureStarted = false
    }
    log(
      "gesture-end action=${event.actionMasked} owner=$owner region=$headerTouchRegion " +
        "inner=$selectedPage outer=${outerPagerIndex()} durationMs=" +
        "${SystemClock.uptimeMillis() - event.downTime}",
    )
    headerDownEvent?.recycle()
    headerDownEvent = null
    headerTouchActive = false
    headerTouchRegion = "none"
    headerGestureOwner = HeaderGestureOwner.NONE
    headerHasHorizontalChild = false
    forwardedScrollable = null
    pressCancelled = false
  }

  override fun dispatchTouchEvent(e: MotionEvent): Boolean {
    val event = e
    if (!nativeSmoothHeaderScrollEnabled) return super.dispatchTouchEvent(event)

    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        headerDownEvent?.recycle()
        headerDownEvent = null
        headerGestureOwner = HeaderGestureOwner.NONE
        forwardedScrollable = null
        pressCancelled = false
        nativeGestureStarted = false
        nestedNativeGestureStarted = false
        headerTouchRegion = headerRegionAt(event.y) ?: "none"
        headerTouchActive = headerTouchRegion != "none"
        if (!headerTouchActive) return super.dispatchTouchEvent(event)
        headerDownX = event.x
        headerDownY = event.y
        headerDownEvent = MotionEvent.obtain(event)
        headerHasHorizontalChild = hasHorizontalScrollOwner(event.x, event.y)
        parent.requestDisallowInterceptTouchEvent(true)
        log(
          "gesture-begin region=$headerTouchRegion owner=pending " +
            "horizontalChild=$headerHasHorizontalChild inner=$selectedPage " +
            "outer=${outerPagerIndex()} headerOffset=$headerOffsetPx",
        )
        return super.dispatchTouchEvent(event)
      }

      MotionEvent.ACTION_MOVE -> {
        if (!headerTouchActive) return super.dispatchTouchEvent(event)
        val dx = event.x - headerDownX
        val dy = event.y - headerDownY
        // A horizontal header guard must not consume a later vertical drag.
        if (headerGestureOwner != HeaderGestureOwner.LIST &&
          kotlin.math.abs(dy) > touchSlopPx && kotlin.math.abs(dy) > kotlin.math.abs(dx)) {
          headerGestureOwner = HeaderGestureOwner.LIST
          log("direction-recover owner=list region=$headerTouchRegion dx=${dx.roundToInt()} dy=${dy.roundToInt()}")
        }
        if (headerGestureOwner == HeaderGestureOwner.NONE) {
          if (kotlin.math.abs(dx) <= touchSlopPx && kotlin.math.abs(dy) <= touchSlopPx) {
            return super.dispatchTouchEvent(event)
          }
          headerGestureOwner = when {
            kotlin.math.abs(dy) > kotlin.math.abs(dx) -> HeaderGestureOwner.LIST
            headerHasHorizontalChild -> HeaderGestureOwner.HORIZONTAL_CHILD
            else -> HeaderGestureOwner.HEADER_GUARD
          }
          log(
            "direction-lock owner=${headerGestureOwner.name.lowercase()} " +
              "region=$headerTouchRegion dx=${dx.roundToInt()} dy=${dy.roundToInt()} " +
              "inner=$selectedPage outer=${outerPagerIndex()}",
          )
        }
        parent.requestDisallowInterceptTouchEvent(true)
        return when (headerGestureOwner) {
          HeaderGestureOwner.LIST -> {
            cancelHeaderPress(event, "list")
            val scrollable = forwardedScrollable
            if (scrollable != null) {
              dispatchToScrollable(scrollable, event)
            } else if (beginForwardingToPrimaryScrollable(event)) {
              true
            } else {
              headerGestureOwner = HeaderGestureOwner.HEADER_GUARD
              log("gesture-list-unavailable region=$headerTouchRegion inner=$selectedPage")
              true
            }
          }
          HeaderGestureOwner.HORIZONTAL_CHILD -> super.dispatchTouchEvent(event)
          HeaderGestureOwner.HEADER_GUARD -> {
            cancelHeaderPress(event, "header-guard")
            true
          }
          HeaderGestureOwner.NONE -> super.dispatchTouchEvent(event)
        }
      }

      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        if (!headerTouchActive) return super.dispatchTouchEvent(event)
        val handled = when (headerGestureOwner) {
          HeaderGestureOwner.LIST -> forwardedScrollable?.let { scrollable ->
            dispatchToScrollable(scrollable, event)
          } ?: true
          HeaderGestureOwner.HEADER_GUARD -> true
          else -> super.dispatchTouchEvent(event)
        }
        finishHeaderGesture(event)
        return handled
      }
    }
    return super.dispatchTouchEvent(event)
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
    if (type == ViewCompat.TYPE_TOUCH) pendingRefreshPull = null
    nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
    updateHeaderSettledState(forceBusy = true)
    if (type == ViewCompat.TYPE_TOUCH) {
      val request = pageRestoreRequests[currentPageKey()]
      offsetDiag("touch-cancel-before", target, request = request)
      observedRecyclerView?.let { deferredRecyclerGeometry.remove(it) }
      pageRestoreRequests.remove(currentPageKey())
      pendingPageRestores.remove(currentPageKey())
      ownedScrollerRestoreRequests.remove(currentPageKey())
      offsetDiag("touch-cancel-after", target, request = request)
    }
  }

  override fun onStopNestedScroll(target: View, type: Int) {
    if (pendingRefreshPull?.get() === target) pendingRefreshPull = null
    nestedScrollingParentHelper.onStopNestedScroll(target, type)
    updateHeaderSettledState()
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
      if (type == ViewCompat.TYPE_TOUCH && used != 0) takeOverBodyForVerticalConsumption()
      setHeaderOffset(headerOffsetPx + used)
      consumed[1] += used
    } else if (dy < 0 && headerOffsetPx > 0) {
      // Refresh wrappers forward nested scrolls but do not scroll themselves.
      val canScrollUp = if (target is SwipeRefreshLayout) {
        target.canChildScrollUp()
      } else {
        target.canScrollVertically(-1)
      }
      if (!canScrollUp) {
        val used = max(dy, -headerOffsetPx)
        if (type == ViewCompat.TYPE_TOUCH && used != 0) takeOverBodyForVerticalConsumption()
        setHeaderOffset(headerOffsetPx + used)
        consumed[1] += used
      }
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
    if (!isCurrentPageTarget(target)) return
    if (type == ViewCompat.TYPE_TOUCH && dyConsumed != 0) takeOverBodyForVerticalConsumption()
    if (dyUnconsumed >= 0) return
    val used = max(dyUnconsumed, -headerOffsetPx)
    if (type == ViewCompat.TYPE_TOUCH && used != 0) takeOverBodyForVerticalConsumption()
    setHeaderOffset(headerOffsetPx + used)
    consumed[1] += used
    if (type == ViewCompat.TYPE_TOUCH && dyUnconsumed < used &&
      target is SwipeRefreshLayout && target.isEnabled) {
      pendingRefreshPull = WeakReference(target)
    }
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

  override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
    val refresh = pendingRefreshPull?.get()
    val scrollable = primaryScrollableForPage(selectedPage)
    if (nativeSmoothHeaderScrollEnabled && refresh != null && refresh === target && isCurrentPageTarget(target) &&
      headerOffsetPx == 0 && scrollable != null && !scrollable.canScrollVertically(-1) &&
      refresh.isEnabled && !refresh.isRefreshing &&
      refreshIndicatorFor(refresh)?.visibility == View.VISIBLE) {
      // A small release reversal must settle the current refresh pull, not fling
      // content upward and make its first positive offset collapse the header.
      return true
    }
    return super.onNestedPreFling(target, velocityX, velocityY)
  }

  override fun getNestedScrollAxes() = nestedScrollingParentHelper.nestedScrollAxes

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    attachmentGeneration += 1
    restoredRecyclerKeys.clear()
    restoredScrollerKeys.clear()
    viewTreeObserver.addOnPreDrawListener(pageContentLayoutListener)
    resumeTransitionSource?.let { source ->
      resumeTransitionSource = null
      if (source in 0 until adapter.itemCount) {
        pager.setCurrentItem(source, false)
        selectPage(source)
      }
    }
    log(
      "host-attach generation=$attachmentGeneration inner=$selectedPage " +
        "outer=${outerPagerIndex()} nativePages=${adapter.itemCount}",
    )
  }

  override fun onDetachedFromWindow() {
    cancelHeaderSettledCheck()
    headerCommandOwner = null
    publishHeaderSettledState(false)
    pendingRefreshPull = null
    releaseRefreshForeground()
    attachmentGeneration += 1
    headerDownEvent?.recycle()
    headerDownEvent = null
    headerTouchActive = false
    forwardedScrollable = null
    pressCancelled = false
    nativeGestureStarted = false
    nestedNativeGestureStarted = false
    viewTreeObserver.removeOnPreDrawListener(pageContentLayoutListener)
    detachPrimaryScrollObserver()
    pageRestoreRequests.clear()
    pendingPageRestores.clear()
    commandBodyKeys.clear()
    explicitTopCommand = null
    suspendedBodyKeys.clear()
    alignedBodyKeys.clear()
    transitionAlignedViews.clear()
    deferredRecyclerGeometry.clear()
    displayBodyOverrides.clear()
    displayRecyclerAnchors.clear()
    ownedScrollerRestoreRequests.clear()
    transitionHeaderOffsets?.let { headers ->
      resumeTransitionSource = transitionSourcePage
      syntheticPageRestore = true
      setHeaderOffset(headers[pageKey(transitionSourcePage)] ?: 0)
      syntheticPageRestore = false
    }
    transitionHeaderOffsets = null
    transitionPageOffsets = null
    transitionTargetPage = null
    // Snapshot live keys before restoration removes their padding records.
    for (recycler in originalRecyclerPadding.keys.toTypedArray()) {
      restoreRecyclerInsets(recycler)
    }
    for (scroller in originalScrollerPadding.keys.toTypedArray()) {
      restoreNativeScrollerInsets(scroller)
    }
    for (scroller in ownedScrollerKeys.keys.toTypedArray()) restoreOwnedScrollerInsets(scroller)
    log(
      "host-detach generation=$attachmentGeneration inner=$selectedPage " +
        "outer=${outerPagerIndex()} nativePages=${adapter.itemCount}",
    )
    super.onDetachedFromWindow()
  }

  companion object {
    private val refreshIndicatorAlphas = WeakHashMap<ImageView, NativeRefreshForegroundAlpha>()
    const val NATIVE_SCROLLER_ID_PREFIX = "rnc-collapsible-pager-native-scroller:"
    const val PAGE_SLOT_OFFSET = 2
  }
}
