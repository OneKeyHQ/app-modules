package com.onekey.nativeoverlay

import android.view.View
import android.view.ViewGroup

/** Restores the caller's flags when a blocking overlay no longer covers it. */
internal class NativeOverlayAccessibilityIsolation {
  private val hidden = mutableMapOf<View, Int>()

  fun update(
    root: ViewGroup,
    activeContainer: View?,
    entriesBelow: List<View>,
    globalHost: View,
  ) {
    val below = mutableSetOf<View>()
    if (activeContainer != null) {
      var node: View = activeContainer
      while (node !== root && node.parent is ViewGroup) {
        val parent = node.parent as ViewGroup
        for (index in 0 until parent.childCount) {
          val sibling = parent.getChildAt(index)
          if (sibling !== node && sibling !== globalHost) below.add(sibling)
        }
        node = parent
      }
      if (node === root) below.addAll(entriesBelow) else below.clear()
    }
    hidden.keys.filter { it !in below }.forEach { view ->
      view.importantForAccessibility = hidden.remove(view) ?: View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
    }
    below.forEach { view ->
      if (view !in hidden) hidden[view] = view.importantForAccessibility
      view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }
  }
}
