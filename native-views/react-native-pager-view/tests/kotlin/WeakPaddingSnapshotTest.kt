package com.reactnativepagerview

import java.util.WeakHashMap

// Deterministically model GC clearing weak keys after collection size is read.
private class ShrinkingPaddingMap(count: Int) : WeakHashMap<Any, Int>() {
  private val liveKeys = List(count) { Any() }
  private var shrinkOnSize = true

  init {
    liveKeys.forEachIndexed { index, key -> put(key, index) }
  }

  override val size: Int
    get() {
      val count = super.size
      if (shrinkOnSize) {
        shrinkOnSize = false
        clear()
      }
      return count
    }
}

fun main() {
  var assertions = 0
  for (count in listOf(1, 8)) {
    check(ShrinkingPaddingMap(count).snapshotPaddingKeys().isEmpty())
    assertions++
  }

  val keys = List(8) { Any() }
  val padding = WeakHashMap<Any, Int>()
  keys.forEachIndexed { index, key -> padding[key] = index }
  val restored = mutableListOf<Int>()
  for (key in padding.snapshotPaddingKeys()) {
    restored.add(checkNotNull(padding.remove(key)))
  }
  check(restored.sorted() == (0..7).toList())
  assertions++
  check(padding.isEmpty())
  assertions++
  println("WeakPaddingSnapshot: $assertions assertions passed")
}
