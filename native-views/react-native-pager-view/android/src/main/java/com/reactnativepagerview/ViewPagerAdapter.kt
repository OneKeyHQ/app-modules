package com.reactnativepagerview

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import android.util.Log
import java.util.*


class ViewPagerAdapter() : Adapter<ViewPagerViewHolder>() {
  private companion object {
    private const val TAG = "PagerView"
  }
  private val childrenViews: ArrayList<View> = ArrayList()
  private var recyclerView: RecyclerView? = null
  private val handler = Handler(Looper.getMainLooper())

  override fun onAttachedToRecyclerView(rv: RecyclerView) {
    super.onAttachedToRecyclerView(rv)
    recyclerView = rv
  }

  override fun onDetachedFromRecyclerView(rv: RecyclerView) {
    super.onDetachedFromRecyclerView(rv)
    handler.removeCallbacksAndMessages(null)
    recyclerView = null
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
    return ViewPagerViewHolder.create(parent)
  }

  override fun onBindViewHolder(holder: ViewPagerViewHolder, index: Int) {
    val container: FrameLayout = holder.container
    val child = getChildAt(index)
    holder.setIsRecyclable(false)

    if (container.childCount > 0) {
      container.removeAllViews()
    }

    if (child.parent != null) {
      (child.parent as FrameLayout).removeView(child)
    }

    container.addView(child)
  }

  override fun getItemCount(): Int {
    return childrenViews.size
  }

  private fun isComputingLayout(): Boolean {
    val rv = recyclerView
    return rv != null && rv.isComputingLayout
  }

  fun addChild(child: View, index: Int) {
    if (isComputingLayout()) {
      handler.post { addChild(child, index) }
      return
    }
    val safeIndex = index.coerceIn(0, childrenViews.size)
    if (safeIndex != index) {
      Log.w(TAG, "addChild index $index out of bounds (size=${childrenViews.size}), clamped to $safeIndex")
    }
    childrenViews.add(safeIndex, child)
    notifyItemInserted(safeIndex)
  }

  fun getChildAt(index: Int): View {
    return childrenViews[index]
  }

  fun removeChild(child: View) {
    if (isComputingLayout()) {
      handler.post { removeChild(child) }
      return
    }
    val index = childrenViews.indexOf(child)
    if (index >= 0) {
      childrenViews.removeAt(index)
      notifyItemRemoved(index)
    }
  }

  fun removeAll() {
    if (isComputingLayout()) {
      handler.post { removeAll() }
      return
    }
    for (index in 1..childrenViews.size) {
      val child = childrenViews[index-1]
      if (child.parent?.parent != null) {
        (child.parent.parent as ViewGroup).removeView(child.parent as View)
      }
    }
    val removedChildrenCount = childrenViews.size
    childrenViews.clear()
    notifyItemRangeRemoved(0, removedChildrenCount)
  }

  fun removeChildAt(index: Int) {
    if (isComputingLayout()) {
      // Capture child by identity to avoid stale-index removal after deferral
      if (index >= 0 && index < childrenViews.size) {
        val child = childrenViews[index]
        handler.post { removeChild(child) }
      }
      return
    }
    if (index >= 0 && index < childrenViews.size) { 
      childrenViews.removeAt(index)
      notifyItemRemoved(index)
    }
  }
}
