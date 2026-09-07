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

internal class NativeListAdapter(
  private val context: ThemedReactContext,
) : RecyclerView.Adapter<NativeListViewHolder>() {
  private var suppressDifferUpdates = false
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
  private val createdRows = Collections.newSetFromMap(
    WeakHashMap<NativeListRowView, Boolean>(),
  )
  var usesSelectorSourceScale = false
  var theme: JSONObject? = null
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

  fun positionOfKey(key: String): Int =
    (reorderItems ?: differ.currentList).indexOfFirst { it.key == key }

  fun submitList(items: List<NativeListItem>, commitCallback: (() -> Unit)? = null) {
    if (reorderItems != null) {
      reorderItems = null
      notifyDataSetChanged()
    }
    differ.submitList(items.toList()) { commitCallback?.invoke() }
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
    if (reorderItems == null) return
    reorderItems = null
    notifyDataSetChanged()
  }

  fun dispose() {
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
        ) SELECTION_ECHO_PAYLOAD else null
    }
  }
}

internal const val SELECTION_PAYLOAD = "selection"
// OneKey patch: internal payload, never exposed through the serialized row contract.
internal const val SELECTION_ECHO_PAYLOAD = "selectionEcho"
