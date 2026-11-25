package com.reactnativepagerview

import java.util.WeakHashMap

// Copy the live keys without assuming the weak map's reported size stays valid.
internal inline fun <reified K> WeakHashMap<K, *>.snapshotPaddingKeys(): Array<K> =
  keys.toTypedArray()
