package com.margelo.nitro.nativelist

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

/** A persistent React container slot; never a template row or a selectable item. */
internal class NativeListContainerSlotAdapter : RecyclerView.Adapter<NativeListContainerSlotAdapter.Holder>() {
  class Holder(val container: FrameLayout) : RecyclerView.ViewHolder(container)
  private var content: View? = null
  private var height = 0
  private var enabled = true

  fun update(view: View?, heightPx: Int, visible: Boolean) {
    val previousCount = itemCount
    val changed = content !== view || height != heightPx || enabled != visible
    content = view
    height = heightPx.coerceAtLeast(0)
    enabled = visible
    val nextCount = itemCount
    if (previousCount == 0 && nextCount == 1) notifyItemInserted(0)
    else if (previousCount == 1 && nextCount == 0) notifyItemRemoved(0)
    else if (nextCount == 1 && changed) notifyItemChanged(0)
  }

  override fun getItemCount(): Int = if (enabled && content != null && height > 0) 1 else 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    val container = object : FrameLayout(parent.context) {
      override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (index in 0 until childCount) {
          getChildAt(index).layout(0, 0, right - left, bottom - top)
        }
      }
    }
    container.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
    return Holder(container)
  }

  override fun onBindViewHolder(holder: Holder, position: Int) {
    holder.container.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
    holder.container.removeAllViews()
    content?.let { view ->
      (view.parent as? ViewGroup)?.removeView(view)
      holder.container.addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height))
    }
  }

  override fun onViewRecycled(holder: Holder) {
    holder.container.removeAllViews()
  }
}
