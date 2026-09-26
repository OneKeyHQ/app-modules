package com.reactnativepagerview

import java.util.WeakHashMap

/** Native tag payload; leaf observers can read it as Map without a pager dependency. */
internal class NativeScrollCoordinatorContributions : WeakHashMap<Any, Int>() {
  fun update(owner: Any, pixels: Int): Boolean {
    val value = pixels.coerceAtLeast(0)
    if (get(owner) == value) return false
    put(owner, value)
    return true
  }

  fun clearOwner(owner: Any): Boolean = remove(owner) != null
}
