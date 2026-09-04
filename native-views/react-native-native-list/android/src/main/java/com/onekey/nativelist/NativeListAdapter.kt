package com.margelo.nitro.nativelist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject

internal class NativeListAdapter(
  private val context: ThemedReactContext,
) : ListAdapter<NativeListItem, NativeListViewHolder>(DIFF) {
  private val createdRows = LinkedHashSet<NativeListRowView>()
  var theme: JSONObject? = null
  var layout: String = "linear"
  var orientation: String = "vertical"
  var selectedKeys: Set<String> = emptySet()
  var checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String = { _, _, fallback -> fallback }
  var onRowPress: ((NativeListItem) -> Unit)? = null
  var onAction: ((NativeListItem, String, NativeSelectionTarget?) -> Unit)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NativeListViewHolder {
    val view = NativeListRowView(context)
    view.layoutParams = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    view.onRowPress = { onRowPress?.invoke(it) }
    view.onAction = { item, actionKey, target -> onAction?.invoke(item, actionKey, target) }
    createdRows.add(view)
    return NativeListViewHolder(view)
  }

  override fun onBindViewHolder(holder: NativeListViewHolder, position: Int) {
    val item = getItem(position)
    holder.rowView.bind(
      item,
      theme,
      layout,
      orientation,
      position,
      selectedKeys.contains(item.key),
      checkboxState,
    )
  }

  override fun onBindViewHolder(
    holder: NativeListViewHolder,
    position: Int,
    payloads: MutableList<Any>,
  ) {
    if (payloads.contains(SELECTION_PAYLOAD)) {
      val item = getItem(position)
      holder.rowView.bindSelection(
        item,
        theme,
        layout,
        position,
        selectedKeys.contains(item.key),
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

  fun itemAt(position: Int): NativeListItem? = currentList.getOrNull(position)

  fun positionOfKey(key: String): Int = currentList.indexOfFirst { it.key == key }

  fun move(from: Int, to: Int): List<NativeListItem>? {
    if (from !in currentList.indices || to !in currentList.indices) return null
    val next = currentList.toMutableList()
    val moved = next.removeAt(from)
    next.add(to, moved)
    submitList(next)
    return next
  }

  fun submitReordered(items: List<NativeListItem>) {
    submitList(items)
  }

  fun dispose() {
    createdRows.forEach(NativeListRowView::dispose)
    createdRows.clear()
  }

  companion object {
    private val DIFF = object : DiffUtil.ItemCallback<NativeListItem>() {
      override fun areItemsTheSame(oldItem: NativeListItem, newItem: NativeListItem): Boolean =
        oldItem.key == newItem.key && oldItem.type == newItem.type

      override fun areContentsTheSame(oldItem: NativeListItem, newItem: NativeListItem): Boolean =
        oldItem.revision == newItem.revision && oldItem.content == newItem.content
    }
  }
}

internal const val SELECTION_PAYLOAD = "selection"
