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
  val failure = runCatching { ShrinkingPaddingMap(1).keys.toList() }.exceptionOrNull()
  check(failure is NoSuchElementException) { "Old single-key snapshot must reproduce the race" }
  assertions++
  for (count in listOf(1, 2, 8)) {
    check(ShrinkingPaddingMap(count).keys.toTypedArray().isEmpty())
    assertions++
  }

  val keys = List(8) { Any() }
  val padding = WeakHashMap<Any, Int>()
  keys.forEachIndexed { index, key -> padding[key] = index }
  val restored = mutableListOf<Int>()
  for (key in padding.keys.toTypedArray()) {
    restored.add(checkNotNull(padding.remove(key)))
  }
  check(restored.sorted() == (0..7).toList())
  assertions++
  check(padding.isEmpty())
  assertions++
  check(WeakHashMap<Any, Int>().keys.toTypedArray().isEmpty())
  assertions++
  println("WeakPaddingSnapshot: $assertions assertions passed")
}
