package com.margelo.nitro.nativelist

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject
import java.util.Collections
import java.util.WeakHashMap
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class NativeListAdapter(
  private val context: ThemedReactContext,
) : RecyclerView.Adapter<NativeListViewHolder>() {
  private var suppressDifferUpdates = false
  private var needsThemeRebind = false
  private var reorderItems: MutableList<NativeListItem>? = null
  private val differ = AsyncListDiffer(
    object : ListUpdateCallback {
      override fun onInserted(position: Int, count: Int) {
        if (!suppressDifferUpdates) notifyItemRangeInserted(position, count)
      }

      override fun onRemoved(position: Int, count: Int) {
        if (!suppressDifferUpdates) notifyItemRangeRemoved(position, count)
      }

      override fun onMoved(fromPosition: Int, toPosition: Int) {
        if (!suppressDifferUpdates) notifyItemMoved(fromPosition, toPosition)
      }

      override fun onChanged(position: Int, count: Int, payload: Any?) {
        if (!suppressDifferUpdates) notifyItemRangeChanged(position, count, payload)
      }
    },
    AsyncDifferConfig.Builder(DIFF).build(),
  )
  val currentList: List<NativeListItem>
    get() = differ.currentList
  private var marketSourceItems: List<NativeListItem>? = null
  private var marketSourceEdges = DoubleArray(0)
  private val createdRows = Collections.newSetFromMap(
    WeakHashMap<NativeListRowView, Boolean>(),
  )
  var usesSelectorSourceScale = false
  var theme: JSONObject? = null
    set(value) {
      if (field?.toString() != value?.toString()) needsThemeRebind = true
      field = value
    }
  var layout: String = "linear"
  var orientation: String = "vertical"
  var selectedKeys: Set<String> = emptySet()
  var checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String = { _, _, fallback -> fallback }
  var onRowPress: ((NativeListItem, NativeListActionOrigin) -> Unit)? = null
  var onAction: ((NativeListItem, String, NativeSelectionTarget?, NativeListActionOrigin?) -> Unit)? = null
  var onBindingInvalidated: ((NativeListRowView, Long) -> Unit)? = null

  override fun getItemCount(): Int = reorderItems?.size ?: differ.currentList.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NativeListViewHolder {
    val view = NativeListRowView(context)
    view.layoutParams = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    view.onRowPress = { item, origin -> onRowPress?.invoke(item, origin) }
    view.onAction = { item, actionKey, target, origin ->
      onAction?.invoke(item, actionKey, target, origin)
    }
    view.onBindingInvalidated = { row, epoch -> onBindingInvalidated?.invoke(row, epoch) }
    createdRows.add(view)
    return NativeListViewHolder(view)
  }

  override fun onBindViewHolder(holder: NativeListViewHolder, position: Int) {
    val item = itemAt(position) ?: return
    holder.rowView.bind(
      item,
      theme,
      layout,
      orientation,
      position,
      item.json.optBoolean("selected", false) || selectedKeys.contains(item.key),
      checkboxState,
      useSourceScale = usesSelectorSourceScale,
    )
  }

  override fun onBindViewHolder(
    holder: NativeListViewHolder,
    position: Int,
    payloads: MutableList<Any>,
  ) {
    if (payloads.isNotEmpty() && payloads.all { it == MARKET_QUOTE_PAYLOAD }) {
      itemAt(position)?.let { holder.rowView.bindMarketQuote(it, theme) }
      return
    }
    // OneKey patch: an unknown payload must retain full binding; validated echoes keep images alive.
    // if (payloads.contains(SELECTION_PAYLOAD)) {
    if (payloads.isNotEmpty() && payloads.all { it == SELECTION_PAYLOAD || it == SELECTION_ECHO_PAYLOAD }) {
      val item = itemAt(position) ?: return
      if (payloads.contains(SELECTION_ECHO_PAYLOAD)) holder.rowView.bindStableSummary(item)
      holder.rowView.bindSelection(
        item,
        theme,
        layout,
        position,
        item.json.optBoolean("selected", false) || selectedKeys.contains(item.key),
        checkboxState,
      )
      return
    }
    onBindViewHolder(holder, position)
  }

  override fun onViewRecycled(holder: NativeListViewHolder) {
    holder.rowView.recycle()
    super.onViewRecycled(holder)
  }

  fun itemAt(position: Int): NativeListItem? =
    reorderItems?.getOrNull(position) ?: differ.currentList.getOrNull(position)

  // OneKey patch: Yoga rounds badge dimensions from unrounded cumulative row edges.
  fun marketSourceEdgePx(position: Int): Double {
    val items = reorderItems ?: differ.currentList
    if (marketSourceItems !== items) {
      val density = context.resources.displayMetrics.density.toDouble()
      val edges = DoubleArray(items.size + 1)
      items.forEachIndexed { index, item ->
        val style = item.json.optJSONObject("style")
        val height = item.json.optDouble("height", 0.0)
        val sourceHeight = if (item.type == "market" && style != null) {
          val titleHeight = ceil((style.optJSONObject("title")?.optDouble("lineHeight", 24.0) ?: 24.0) * density) / density
          val subtitleHeight = if (item.json.optString("subtitle").isNotEmpty() || item.json.optJSONObject("subtitlePrefix") != null) {
            ceil((style.optJSONObject("subtitle")?.optDouble("lineHeight", 20.0) ?: 20.0) * density) / density + style.optDouble("lineGap", 0.0)
          } else 0.0
          maxOf(height.roundToInt().toDouble(), titleHeight + subtitleHeight + style.optDouble("verticalPadding", 12.0) * 2).toFloat().toDouble()
        } else height
        edges[index + 1] = edges[index] + sourceHeight * density
      }
      marketSourceItems = items
      marketSourceEdges = edges
    }
    return marketSourceEdges.getOrElse(position) { 0.0 }
  }

  fun positionOfKey(key: String): Int =
    (reorderItems ?: differ.currentList).indexOfFirst { it.key == key }

  fun submitList(items: List<NativeListItem>, commitCallback: (() -> Unit)? = null) {
    // A newer submission can discard the reorder diff and its completion callback.
    suppressDifferUpdates = false
    if (reorderItems != null) {
      reorderItems = null
      notifyDataSetChanged()
    }
    differ.submitList(items.toList()) {
      // Retain this invalidation across superseded submissions, even when rows are unchanged.
      if (needsThemeRebind) {
        needsThemeRebind = false
        notifyItemRangeChanged(0, itemCount)
      }
      commitCallback?.invoke()
    }
  }

  fun moveReordered(from: Int, to: Int): List<NativeListItem>? {
    val items = reorderItems ?: differ.currentList.toMutableList().also { reorderItems = it }
    if (from !in items.indices || to !in items.indices) return null
    val moved = items.removeAt(from)
    items.add(to, moved)
    notifyItemMoved(from, to)
    return items
  }

  fun commitReordered(items: List<NativeListItem>, commitCallback: () -> Unit) {
    val committed = items.toList()
    suppressDifferUpdates = true
    differ.submitList(committed) {
      reorderItems = null
      suppressDifferUpdates = false
      commitCallback()
    }
  }

  fun cancelReorder() {
    suppressDifferUpdates = false
    if (reorderItems == null) return
    reorderItems = null
    notifyDataSetChanged()
  }

  fun dispose() {
    marketSourceItems = null
    marketSourceEdges = DoubleArray(0)
    reorderItems = null
    suppressDifferUpdates = false
    createdRows.forEach(NativeListRowView::dispose)
    createdRows.clear()
  }

  companion object {
    private val DIFF = object : DiffUtil.ItemCallback<NativeListItem>() {
      override fun areItemsTheSame(oldItem: NativeListItem, newItem: NativeListItem): Boolean =
        oldItem.key == newItem.key && oldItem.type == newItem.type

      override fun areContentsTheSame(oldItem: NativeListItem, newItem: NativeListItem): Boolean =
        oldItem.revision == newItem.revision && oldItem.content == newItem.content

      // OneKey patch: a theme/layout update or a diff spanning another baseline stays a full bind.
      override fun getChangePayload(oldItem: NativeListItem, newItem: NativeListItem): Any? =
        if (oldItem.key == newItem.key && oldItem.type == newItem.type &&
          newItem.selectionUpdateFromContent == oldItem.content
        ) SELECTION_ECHO_PAYLOAD else if (isMarketQuoteUpdate(oldItem, newItem)) MARKET_QUOTE_PAYLOAD else null

      private fun isMarketQuoteUpdate(oldItem: NativeListItem, newItem: NativeListItem): Boolean {
        if (oldItem.type != "market" || newItem.type != "market") return false
        fun stableContent(item: NativeListItem): String {
          val value = JSONObject(item.json.toString())
          listOf("revision", "price", "priceSegments", "change", "accessibilityLabel")
            .forEach(value::remove)
          return value.toString()
        }
        return stableContent(oldItem) == stableContent(newItem)
      }
    }
  }
}

internal const val SELECTION_PAYLOAD = "selection"
// OneKey patch: internal payload, never exposed through the serialized row contract.
internal const val SELECTION_ECHO_PAYLOAD = "selectionEcho"
internal const val MARKET_QUOTE_PAYLOAD = "marketQuote"
