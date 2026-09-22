package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.widget.TextView

internal open class NativeListTextView(context: android.content.Context) : TextView(context) {
  var rowHorizontallyScrolling = false
    private set

  override fun setHorizontallyScrolling(whether: Boolean) {
    rowHorizontallyScrolling = whether
    super.setHorizontallyScrolling(whether)
  }

  var opticalOffsetY = 0f
    set(value) { field = value; invalidate() }

  override fun onDraw(canvas: Canvas) {
    if (opticalOffsetY == 0f) { super.onDraw(canvas); return }
    val checkpoint = canvas.save()
    canvas.translate(0f, opticalOffsetY)
    super.onDraw(canvas)
    canvas.restoreToCount(checkpoint)
  }
}

