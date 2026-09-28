package com.reactnativepagerview

/**
 * A pager-owned offset restoration that NestedScrollView clamped because the
 * React content height had not reached native yet. It is reapplied on content
 * layout until reached or cancelled by user/command/content replacement.
 */
internal class NativeScrollerPendingRestore {
  var target: Int? = null
    private set

  fun request(target: Int, reached: Int) {
    this.target = if (reached == target) null else target
  }

  /** The offset to reapply after a content layout pass. */
  fun anchor(current: Int): Int = target ?: current

  fun settle(reached: Int) {
    if (target == reached) target = null
  }

  fun cancel() {
    target = null
  }
}
