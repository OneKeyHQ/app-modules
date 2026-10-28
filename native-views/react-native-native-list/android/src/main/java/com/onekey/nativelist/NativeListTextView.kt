package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.widget.TextView

internal open class NativeListTextView(context: android.content.Context) : TextView(context) {
  var helpBindingGeneration = 0
  var guardsHelpTouches = false
  private var helpDownGeneration = -1
  var primaryHelpOnly = false
  var showsHelpUnderline = false
    set(value) { field = value; invalidate() }
  private val helpPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    strokeWidth = resources.displayMetrics.density
    pathEffect = DashPathEffect(floatArrayOf(3 * strokeWidth, 2 * strokeWidth), 0f)
  }

  val helpAnchorBounds: RectF?
    get() {
      if (!primaryHelpOnly) return null
      val textLayout = layout ?: return null
      if (textLayout.lineCount == 0) return null
      return RectF(
        totalPaddingLeft + textLayout.getLineLeft(0),
        (totalPaddingTop + textLayout.getLineTop(0)).toFloat() + opticalOffsetY,
        totalPaddingLeft + textLayout.getLineRight(0),
        (totalPaddingTop + textLayout.getLineBottom(0)).toFloat() + opticalOffsetY,
      )
    }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (primaryHelpOnly && event.actionMasked == MotionEvent.ACTION_DOWN &&
      helpAnchorBounds?.contains(event.x, event.y) != true) return false
    if (guardsHelpTouches) {
      if (event.actionMasked == MotionEvent.ACTION_DOWN) helpDownGeneration = helpBindingGeneration
      if (event.actionMasked == MotionEvent.ACTION_UP && helpDownGeneration != helpBindingGeneration) {
        isPressed = false
        return true
      }
    }
    return super.onTouchEvent(event)
  }

  private fun drawHelpUnderline(canvas: Canvas) {
    if (!showsHelpUnderline) return
    val textLayout = layout ?: return
    if (textLayout.lineCount == 0) return
    helpPaint.color = currentTextColor
    val left = totalPaddingLeft + textLayout.getLineLeft(0)
    val right = totalPaddingLeft + textLayout.getLineRight(0)
    val bottom = totalPaddingTop + textLayout.getLineBottom(0) - resources.displayMetrics.density
    canvas.drawLine(left, bottom, right, bottom, helpPaint)
  }

  var rowHorizontallyScrolling = false
    private set

  override fun setHorizontallyScrolling(whether: Boolean) {
    rowHorizontallyScrolling = whether
    super.setHorizontallyScrolling(whether)
  }

  var opticalOffsetY = 0f
    set(value) { field = value; invalidate() }

  override fun onDraw(canvas: Canvas) {
    if (opticalOffsetY == 0f) { super.onDraw(canvas); drawHelpUnderline(canvas); return }
    val checkpoint = canvas.save()
    canvas.translate(0f, opticalOffsetY)
    super.onDraw(canvas)
    drawHelpUnderline(canvas)
    canvas.restoreToCount(checkpoint)
  }
}
