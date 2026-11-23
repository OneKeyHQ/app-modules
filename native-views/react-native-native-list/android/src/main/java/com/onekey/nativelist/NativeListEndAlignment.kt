package com.margelo.nitro.nativelist

// Negative offsets are required when a container slot is taller than the viewport.
internal fun nativeListEndAlignmentOffset(viewportEnd: Int, contentEnd: Int): Int =
  viewportEnd - contentEnd
