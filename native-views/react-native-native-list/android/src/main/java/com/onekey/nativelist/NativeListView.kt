package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

class NativeListView(
  private val reactContext: ThemedReactContext,
) : LinearLayout(reactContext) {
  var onRowAction: ((String) -> Unit)? = null
  var onSelectionDelta: ((String) -> Unit)? = null
  var onReorder: ((String) -> Unit)? = null
  var onEndReached: ((String) -> Unit)? = null
  var onVisibleRangeChanged: ((String) -> Unit)? = null
  private val density = resources.displayMetrics.density
  private val recyclerView = RecyclerView(context)
  private val refreshLayout = SwipeRefreshLayout(context)
  private val contentContainer = FrameLayout(context)
  private val adapter = NativeListAdapter(reactContext)
  private val layoutManager = GridLayoutManager(context, 1)
  private val footerView = NativeListRowView(reactContext)
  private val sectionIndexView = NativeListSectionIndexView(context)
  private val sectionIndexPreview = TextView(context)
  private var config: NativeListConfig? = null
  private var stickyDecoration: StickySectionHeaderDecoration? = null
  private var spacingDecoration: ItemSpacingDecoration? = null
  private var itemTouchHelper: ItemTouchHelper? = null
  private var endReachedGeneration: Int? = null
  private var lastVisibleRangeSignature: String? = null
  private var visibleEventScheduled = false
  private var dragFrom = RecyclerView.NO_POSITION
  private var dragTo = RecyclerView.NO_POSITION
  private var pendingReorder: List<NativeListItem>? = null
  private var sectionIndexEntries: List<NativeListSectionIndexEntry> = emptyList()
  private var sectionIndexScrubbing = false
  private var sectionIndexProgrammaticScroll = false
  private var sectionIndexHapticsEnabled = true
  private var disposed = false

  init {
    orientation = VERTICAL
    recyclerView.adapter = adapter
    recyclerView.layoutManager = layoutManager
    recyclerView.itemAnimator = null
    recyclerView.setHasFixedSize(false)
    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        if (config?.layout != "grid") return 1
        val type = adapter.itemAt(position)?.type
        return if (type == "sectionHeader" || type == "system" || type == "action") {
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
    contentContainer.addView(
      sectionIndexView,
      FrameLayout.LayoutParams(dp(48), FrameLayout.LayoutParams.MATCH_PARENT, Gravity.END),
    )
    sectionIndexPreview.apply {
      gravity = Gravity.CENTER
      textSize = NativeListScale.font(resources, 28f)
      typeface = NativeListFonts.semibold(context)
      visibility = GONE
      alpha = 0f
      importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }
    contentContainer.addView(
      sectionIndexPreview,
      FrameLayout.LayoutParams(dp(72), dp(72), Gravity.CENTER),
    )
    addView(contentContainer, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))
    footerView.visibility = GONE
    addView(footerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

    adapter.onRowPress = ::handleRowPress
    adapter.onAction = ::handleAction
    adapter.checkboxState = ::resolveCheckboxState
    footerView.onRowPress = ::handleRowPress
    footerView.onAction = ::handleAction
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

    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        when (newState) {
          RecyclerView.SCROLL_STATE_DRAGGING -> sectionIndexProgrammaticScroll = false
          RecyclerView.SCROLL_STATE_IDLE -> sectionIndexProgrammaticScroll = false
        }
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        syncSectionIndexToVisibleRows()
        scheduleVisibleEvent()
        checkEndReached()
      }
    })
  }

  fun applySnapshot(snapshotJson: String) {
    val next = try {
      NativeListConfig.parse(snapshotJson)
    } catch (_: Exception) {
      return
    }
    val previous = config
    if (previous != null && canApplyStableContentUpdate(previous, next)) {
      val changedSummaryKeys = previous.items.indices.mapNotNull { index ->
        previous.items[index].key.takeIf {
          previous.items[index].content != next.items[index].content
        }
      }.toSet()
      config = next
      adapter.theme = next.theme
      adapter.layout = next.layout
      adapter.orientation = next.orientation
      adapter.selectedKeys = next.selectedKeys
      adapter.submitList(next.items) {
        recyclerView.post {
          bindVisibleSelection(changedSummaryKeys)
          bindFooterSelection()
        }
      }
      return
    }
    config = next
    endReachedGeneration = null
    pendingReorder = null
    adapter.theme = next.theme
    adapter.layout = next.layout
    adapter.orientation = next.orientation
    adapter.selectedKeys = next.selectedKeys
    configureSectionIndex(next)
    updateLayout(next)
    adapter.submitList(next.items) {
      relayoutContents()
      syncSectionIndexToVisibleRows()
      scheduleVisibleEvent()
      if (next.items.isNotEmpty()) checkEndReached()
    }
    refreshLayout.isEnabled = next.pullToRefresh
    refreshLayout.isRefreshing = next.refreshing
    bindFooter(next)
    updateReordering(next)
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
      previous.refreshing != next.refreshing ||
      previous.loadMore != next.loadMore ||
      previous.endReachedThreshold != next.endReachedThreshold ||
      previous.sectionIndexEnabled != next.sectionIndexEnabled ||
      previous.sectionIndexHapticsEnabled != next.sectionIndexHapticsEnabled ||
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
        (oldItem.content == newItem.content || isStableSummaryUpdate(oldItem, newItem))
    }
  }

  private fun isStableSummaryUpdate(
    previous: NativeListItem,
    next: NativeListItem,
  ): Boolean {
    if (
      previous.type != "sectionHeader" ||
      previous.json.optString("variant") != "summary" ||
      next.json.optString("variant") != "summary"
    ) {
      return false
    }
    val previousStructure = JSONObject(previous.content).apply {
      remove("title")
      remove("value")
    }
    val nextStructure = JSONObject(next.content).apply {
      remove("title")
      remove("value")
    }
    return previousStructure.toString() == nextStructure.toString()
  }

  fun applyPatches(patchesJson: String) {
    val current = config ?: return
    val patches = try {
      JSONArray(patchesJson)
    } catch (_: Exception) {
      return
    }
    val indexByKey = current.items.withIndex().associate { it.value.key to it.index }
    val pending = ArrayList<Pair<Int, JSONObject>>(patches.length())
    val seen = HashSet<String>()
    for (index in 0 until patches.length()) {
      val patch = patches.optJSONObject(index) ?: return
      val key = patch.optString("key")
      val itemIndex = indexByKey[key] ?: return
      if (!seen.add(key)) return
      if (patch.optString("type") != current.items[itemIndex].type) return
      val changes = patch.optJSONObject("changes") ?: return
      pending.add(itemIndex to changes)
    }
    val nextItems = current.items.toMutableList()
    val selected = LinkedHashSet(current.selectedKeys)
    try {
      pending.forEach { (index, changes) ->
        val previous = nextItems[index]
        val merged = NativeListItem.parse(mergeRow(previous.json, changes))
        nextItems[index] = merged
        if (changes.has("selected")) {
          if (changes.optBoolean("selected")) selected.add(merged.key) else selected.remove(merged.key)
        }
      }
    } catch (_: Exception) {
      return
    }
    val next = current.copy(items = nextItems, selectedKeys = selected)
    config = next
    adapter.selectedKeys = selected
    adapter.submitList(nextItems) { relayoutContents() }
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

  fun scrollToKey(key: String, animated: Boolean, alignment: String) {
    val index = adapter.positionOfKey(key)
    if (index >= 0) scrollToIndex(index, animated, alignment)
  }

  fun scrollToIndex(index: Int, animated: Boolean, alignment: String) {
    if (index !in 0 until adapter.itemCount) return
    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
    if (animated && alignment == "nearest") {
      recyclerView.smoothScrollToPosition(index)
      return
    }
    val viewport = if (layoutManager.orientation == RecyclerView.VERTICAL) recyclerView.height else recyclerView.width
    val itemSize = layoutManager.findViewByPosition(index)?.let {
      if (layoutManager.orientation == RecyclerView.VERTICAL) it.height else it.width
    } ?: 0
    val offset = when (alignment) {
      "center" -> max(0, (viewport - itemSize) / 2)
      "end" -> max(0, viewport - itemSize)
      else -> 0
    }
    layoutManager.scrollToPositionWithOffset(index, offset)
  }

  fun setRefreshing(refreshing: Boolean) {
    refreshLayout.isRefreshing = refreshing
    config = config?.copy(refreshing = refreshing)
  }

  fun dispose() {
    if (disposed) return
    disposed = true
    visibleEventScheduled = false
    footerView.recycle()
    footerView.dispose()
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
    val indexGutter = if (sectionIndexEntries.isEmpty()) 0 else 48
    recyclerView.setPaddingRelative(
      dp(horizontalPadding),
      dp(topPadding),
      dp(horizontalPadding + indexGutter),
      dp(bottomPadding),
    )
    recyclerView.clipToPadding = false
    recyclerView.isVerticalScrollBarEnabled = sectionIndexEntries.isEmpty()

    spacingDecoration?.let(recyclerView::removeItemDecoration)
    spacingDecoration = ItemSpacingDecoration(dp(next.itemSpacing)).also(recyclerView::addItemDecoration)
    stickyDecoration?.let(recyclerView::removeItemDecoration)
    stickyDecoration = if (next.stickyHeaders && orientation == RecyclerView.VERTICAL) {
      StickySectionHeaderDecoration(adapter, context, next.theme, density).also(recyclerView::addItemDecoration)
    } else null
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
      themeColor(next.theme, "secondaryText", "#646464"),
      themeColor(next.theme, "accent", "#108303"),
    )
    sectionIndexView.visibility = if (sectionIndexEntries.isEmpty()) GONE else VISIBLE
    sectionIndexPreview.setTextColor(themeColor(next.theme, "inverseText", "#FCFCFC"))
    sectionIndexPreview.background = GradientDrawable().apply {
      setColor(themeColor(next.theme, "inverseBackground", "#202020"))
      cornerRadius = dp(16).toFloat()
    }
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
    recyclerView.post {
      recyclerView.smoothScrollToPosition(entry.position)
    }
    if (interacting) {
      sectionIndexPreview.animate().cancel()
      sectionIndexPreview.text = entry.title
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
    sectionIndexPreview.animate().cancel()
    if (immediately) {
      sectionIndexPreview.alpha = 0f
      sectionIndexPreview.visibility = GONE
    } else {
      sectionIndexPreview.animate()
        .alpha(0f)
        .setDuration(150)
        .withEndAction { sectionIndexPreview.visibility = GONE }
        .start()
    }
  }

  private fun syncSectionIndexToVisibleRows() {
    if (sectionIndexScrubbing || sectionIndexProgrammaticScroll || sectionIndexEntries.isEmpty()) return
    val firstVisible = layoutManager.findFirstVisibleItemPosition()
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
      footerView.visibility = VISIBLE
      footerView.bind(
        footer,
        next.theme,
        next.layout,
        "vertical",
        null,
        next.selectedKeys.contains(footer.key),
        ::resolveCheckboxState,
      )
    }
  }

  private fun handleRowPress(item: NativeListItem) {
    val current = config ?: return
    if (current.rowPressToggles && item.isSelectable && current.selectionMode != "none") {
      updateSelection(NativeSelectionTarget("row", item.key), item.key)
    } else {
      val actionKey = when {
        item.type == "action" -> item.json.optString("actionKey")
        item.type == "system" && item.json.optString("variant") == "retry" -> item.json.optString("actionKey")
        else -> "press"
      }
      val payload = JSONObject()
        .put("rowKey", item.key)
        .put("actionKey", actionKey)
      item.sectionKey?.let { payload.put("sectionKey", it) }
      emit(ROW_ACTION, payload)
    }
  }

  private fun handleAction(
    item: NativeListItem,
    actionKey: String,
    target: NativeSelectionTarget?,
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
    emit(ROW_ACTION, payload)
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
        current.selectedKeys.contains(item.key),
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
    itemTouchHelper?.attachToRecyclerView(null)
    itemTouchHelper = null
    if (!next.reorderable) return
    val callback = object : ItemTouchHelper.SimpleCallback(
      ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
      0,
    ) {
      override fun isLongPressDragEnabled(): Boolean = true

      override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
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
        val from = source.bindingAdapterPosition
        val to = target.bindingAdapterPosition
        val base = pendingReorder ?: adapter.currentList
        val fromItem = base.getOrNull(from) ?: return false
        val toItem = base.getOrNull(to) ?: return false
        if (!fromItem.isReorderable || !toItem.isReorderable || fromItem.sectionKey != toItem.sectionKey) return false
        if (dragFrom == RecyclerView.NO_POSITION) dragFrom = from
        dragTo = to
        val reordered = base.toMutableList()
        val moved = reordered.removeAt(from)
        reordered.add(to, moved)
        pendingReorder = reordered
        adapter.submitReordered(reordered)
        return true
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

      override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val from = dragFrom
        val to = dragTo
        val reordered = pendingReorder
        if (from != RecyclerView.NO_POSITION && to != RecyclerView.NO_POSITION && from != to && reordered != null) {
          val moved = reordered.getOrNull(to)
          if (moved != null) {
            config = config?.copy(items = reordered)
            val payload = JSONObject()
              .put("key", moved.key)
              .put("fromIndex", from)
              .put("toIndex", to)
            reordered.getOrNull(to - 1)?.let { payload.put("beforeKey", it.key) }
            reordered.getOrNull(to + 1)?.let { payload.put("afterKey", it.key) }
            emit(REORDER, payload)
          }
        }
        dragFrom = RecyclerView.NO_POSITION
        dragTo = RecyclerView.NO_POSITION
        pendingReorder = null
      }
    }
    itemTouchHelper = ItemTouchHelper(callback).also { it.attachToRecyclerView(recyclerView) }
  }

  private fun scheduleVisibleEvent() {
    if (visibleEventScheduled) return
    visibleEventScheduled = true
    Choreographer.getInstance().postFrameCallback {
      visibleEventScheduled = false
      val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return@postFrameCallback
      val first = manager.findFirstVisibleItemPosition()
      val last = manager.findLastVisibleItemPosition()
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
    val current = config ?: return
    if (!current.loadMore || current.generation == endReachedGeneration || adapter.itemCount == 0) return
    val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
    val lastVisible = manager.findLastVisibleItemPosition()
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
  private fun relayoutContents() {
    post {
      if (disposed || width <= 0 || height <= 0) return@post
      forceLayout()
      measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
      )
      layout(left, top, right, bottom)
    }
  }

  private fun dp(value: Int): Int = NativeListScale.dp(resources, value)

  companion object {
    private const val ROW_ACTION = "rowAction"
    private const val SELECTION_DELTA = "selectionDelta"
    private const val REORDER = "reorder"
    private const val END_REACHED = "endReached"
    private const val VISIBLE_RANGE_CHANGED = "visibleRangeChanged"
  }
}

private data class NativeListSectionIndexEntry(
  val key: String,
  val title: String,
  val position: Int,
)

private class NativeListSectionIndexView(
  context: android.content.Context,
) : View(context) {
  var onSelect: ((Int, Boolean) -> Unit)? = null
  var onInteractionEnded: (() -> Unit)? = null
  var activeIndex: Int? = null
    private set
  private var titles: List<String> = emptyList()
  private var normalColor = Color.GRAY
  private var activeColor = Color.BLACK
  private var lastTouchIndex: Int? = null
  private val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    typeface = NativeListFonts.medium(context)
  }
  private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    typeface = NativeListFonts.semibold(context)
  }

  init {
    isClickable = true
    isFocusable = true
    importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    contentDescription = ACCESSIBILITY_LABEL
  }

  fun configure(titles: List<String>, normalColor: Int, activeColor: Int) {
    this.titles = titles
    this.normalColor = normalColor
    this.activeColor = activeColor
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
    val cellHeight = cellHeight()
    val originY = (height - cellHeight * titles.size) / 2f
    val textSize = NativeListScale.font(resources, 10f) * resources.displayMetrics.scaledDensity
    normalPaint.color = normalColor
    normalPaint.textSize = textSize
    activePaint.color = activeColor
    activePaint.textSize = textSize
    titles.forEachIndexed { index, title ->
      val paint = if (index == activeIndex) activePaint else normalPaint
      val centerY = originY + cellHeight * (index + 0.5f)
      val baseline = centerY - (paint.descent() + paint.ascent()) / 2f
      canvas.drawText(title, width / 2f, baseline, paint)
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
    val cellHeight = cellHeight()
    val originY = (height - cellHeight * titles.size) / 2f
    val index = ((y - originY) / cellHeight).toInt().coerceIn(0, titles.lastIndex)
    if (interacting && lastTouchIndex == index) return
    lastTouchIndex = index.takeIf { interacting }
    select(index, interacting)
  }

  private fun select(index: Int, interacting: Boolean) {
    onSelect?.invoke(index, interacting)
    setActiveIndex(index)
  }

  private fun cellHeight(): Float =
    (height.toFloat() / titles.size.coerceAtLeast(1))
      .coerceAtMost(NativeListScale.dp(resources, 16f))
      .coerceAtLeast(1f)

  private fun updateContentDescription() {
    contentDescription = activeIndex?.let { titles.getOrNull(it) }
      ?.let { "$ACCESSIBILITY_LABEL, $it" }
      ?: ACCESSIBILITY_LABEL
  }

  companion object {
    private const val ACCESSIBILITY_LABEL = "Section index"
  }
}

private class ItemSpacingDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
  override fun getItemOffsets(
    outRect: android.graphics.Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State,
  ) {
    if (spacing <= 0) return
    val horizontal = (parent.layoutManager as? LinearLayoutManager)?.orientation == RecyclerView.HORIZONTAL
    if (horizontal) outRect.right = spacing else outRect.bottom = spacing
  }
}

private class StickySectionHeaderDecoration(
  private val adapter: NativeListAdapter,
  private val context: android.content.Context,
  theme: JSONObject?,
  private val density: Float,
) : RecyclerView.ItemDecoration() {
  private val backgroundPaint = Paint().apply {
    color = parseColor(theme?.optString("rowBackground"), "#FFFFFF")
  }
  private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = parseColor(theme?.optString("secondaryText"), "#0000009B")
    textSize = NativeListScale.font(context.resources, 14f) * density
    typeface = NativeListFonts.semibold(context)
  }

  override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    val manager = parent.layoutManager as? LinearLayoutManager ?: return
    val first = manager.findFirstVisibleItemPosition()
    if (first == RecyclerView.NO_POSITION) return
    var header: NativeListItem? = null
    for (index in first downTo 0) {
      val candidate = adapter.itemAt(index)
      if (candidate?.type == "sectionHeader") {
        if (candidate.json.optString("variant") == "summary") continue
        header = candidate.takeIf(::isSimpleStickySectionHeader)
        break
      }
    }
    val item = header ?: return
    val isHistory = item.json.optString("variant") == "history" ||
      item.sectionKey?.startsWith("history-") == true
    val height = NativeListScale.dp(context.resources, if (isHistory) 16 else 36)
    val textSize = NativeListScale.font(context.resources, if (isHistory) 12f else 14f)
    textPaint.textSize = textSize * density
    val horizontalInset = NativeListScale.dp(context.resources, if (isHistory) 8 else 20).toFloat()
    val left = parent.paddingLeft.toFloat()
    val right = (parent.width - parent.paddingRight).toFloat()
    canvas.drawRect(left, 0f, right, height.toFloat(), backgroundPaint)
    val baseline = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
    val value = item.json.optString("title").let { if (isHistory) it.uppercase() else it }
    val isRightToLeft = parent.layoutDirection == View.LAYOUT_DIRECTION_RTL
    val textWidth = if (isHistory) {
      spacedTextWidth(value, NativeListScale.dp(context.resources, 1).toFloat())
    } else {
      textPaint.measureText(value)
    }
    val x = if (isRightToLeft) right - horizontalInset - textWidth else left + horizontalInset
    if (isHistory) {
      drawSpacedText(canvas, value, x, baseline, NativeListScale.dp(context.resources, 1).toFloat())
    } else {
      canvas.drawText(value, x, baseline, textPaint)
    }
  }

  private fun drawSpacedText(canvas: Canvas, value: String, x: Float, baseline: Float, spacing: Float) {
    var cursor = x
    value.forEachIndexed { index, character ->
      val glyph = character.toString()
      canvas.drawText(glyph, cursor, baseline, textPaint)
      cursor += textPaint.measureText(glyph)
      if (index < value.lastIndex) cursor += spacing * 0.8f
    }
  }

  private fun spacedTextWidth(value: String, spacing: Float): Float =
    value.sumOf { textPaint.measureText(it.toString()).toDouble() }.toFloat() +
      max(0, value.length - 1) * spacing * 0.8f

  companion object {
    private fun parseColor(value: String?, fallback: String): Int = try {
      parseNativeListColor(if (value.isNullOrEmpty()) fallback else value)
    } catch (_: IllegalArgumentException) {
      parseNativeListColor(fallback)
    }
  }
}

internal fun isSimpleStickySectionHeader(item: NativeListItem): Boolean =
  item.type == "sectionHeader" &&
    item.json.optString("variant") != "summary" &&
    item.json.optString("value").isEmpty() &&
    item.json.optJSONObject("checkbox") == null
