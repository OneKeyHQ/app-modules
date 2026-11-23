package com.reactnativepagerview

/** Standalone JVM regression: compile together with the production helper. */
fun main() {
  val outer = Any()
  val inner = Any()
  val firstChild = NativeScrollCoordinatorContributions()
  check(firstChild.update(outer, 120))
  check(firstChild.update(inner, 60))
  check(firstChild.values.sum() == 180)
  check(!firstChild.update(outer, 120))
  check(firstChild.update(outer, 30))
  check(firstChild.values.sum() == 90)
  check(firstChild.clearOwner(outer))
  check(firstChild.values.sum() == 60)
  check(!firstChild.clearOwner(outer))
  val secondChild = NativeScrollCoordinatorContributions()
  check(secondChild.update(outer, 30))
  check(secondChild.values.sum() == 30)
  check(firstChild.values.sum() == 60)
  check(firstChild.clearOwner(inner))
  check(firstChild.isEmpty())
  check(secondChild.update(outer, -1))
  check(secondChild.values.sum() == 0)
  println("Native coordinator contributions: nested owners, duplicate updates, cleanup, and replacement passed")
}
