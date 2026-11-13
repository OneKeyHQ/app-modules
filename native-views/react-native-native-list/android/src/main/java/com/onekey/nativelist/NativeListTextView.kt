package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
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

  /**
   * Character offset the touchable text starts at. `null` keeps the view's own
   * hit testing, its whole bounds. With an offset only the drawn text from
   * there on takes touches, plus a small horizontal slop, so text before the
   * offset and the unused width of a stretched view fall through to the row
   * behind it: `0` covers all of the text, the text length leaves nothing to
   * touch.
   */
  var touchableTextStart: Int? = null

  val touchableTextRegions: List<NativeListTouchRegion>
    get() {
      val textStart = touchableTextStart ?: return emptyList()
      val textLayout = layout ?: return emptyList()
      val count = textLayout.lineCount
      if (count == 0) return emptyList()
      val originX = totalPaddingLeft.toFloat()
      val originY = totalPaddingTop + opticalOffsetY
      val lines = (0 until count).map { line ->
        NativeListTextLine(
          start = textLayout.getLineStart(line),
          end = textLayout.getLineEnd(line),
          left = originX + textLayout.getLineLeft(line),
          // The outer lines also take the view's own vertical padding.
          top = if (line == 0) 0f else originY + textLayout.getLineTop(line),
          right = originX + textLayout.getLineRight(line),
          bottom = if (line == count - 1) height.toFloat() else originY + textLayout.getLineBottom(line),
          isRtl = textLayout.getParagraphDirection(line) == Layout.DIR_RIGHT_TO_LEFT,
        )
      }
      val startsInside = textStart > 0 && textStart < textLayout.text.length
      return nativeListTouchableTextRegions(
        lines = lines,
        textStart = textStart,
        textStartX = if (startsInside) originX + textLayout.getPrimaryHorizontal(textStart) else 0f,
        slop = TOUCH_SLOP_DP * resources.displayMetrics.density,
      )
    }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (primaryHelpOnly && event.actionMasked == MotionEvent.ACTION_DOWN &&
      helpAnchorBounds?.contains(event.x, event.y) != true) return false
    if (touchableTextStart != null && event.actionMasked == MotionEvent.ACTION_DOWN &&
      touchableTextRegions.none { it.contains(event.x, event.y) }) return false
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

  private companion object {
    const val TOUCH_SLOP_DP = 8f
  }
}
