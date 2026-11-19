package com.margelo.nitro.nativelist

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.graphics.RegionIterator
import android.text.Layout
import android.view.MotionEvent
import android.widget.TextView
import kotlin.math.ceil
import kotlin.math.floor

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
      // Lines past the line cap are laid out but never drawn.
      val count = minOf(textLayout.lineCount, maxLines.takeIf { it > 0 } ?: Int.MAX_VALUE)
      if (count == 0) return emptyList()
      val originX = totalPaddingLeft.toFloat()
      val originY = totalPaddingTop + opticalOffsetY
      val lines = (0 until count).map { line ->
        val lineStart = textLayout.getLineStart(line)
        val lineEnd = textLayout.getLineEnd(line)
        // An ellipsis hides the rest of its line. Once it hides the touchable
        // text as well it also stands for plain text, so nothing is left to touch.
        val visibleEnd =
          if (textLayout.getEllipsisCount(line) > 0) lineStart + textLayout.getEllipsisStart(line) else lineEnd
        val touchableStart = maxOf(textStart, lineStart)
        NativeListTextLine(
          // The outer lines also take the view's own vertical padding.
          top = if (line == 0) 0f else originY + textLayout.getLineTop(line),
          bottom = if (line == count - 1) height.toFloat() else originY + textLayout.getLineBottom(line),
          touchable =
            if (touchableStart < visibleEnd) textRuns(textLayout, line, touchableStart, lineEnd, originX)
            else emptyList(),
          plain = textRuns(textLayout, line, lineStart, minOf(textStart, lineEnd), originX),
        )
      }
      return nativeListTouchableTextRegions(lines, TOUCH_SLOP_DP * resources.displayMetrics.density)
    }

  /**
   * Horizontal extents the layout gives characters `[from, to)` of [line]: the
   * layout's own selection, so mixed-direction text yields one run per
   * direction run instead of one box around all of them.
   */
  private fun textRuns(textLayout: Layout, line: Int, from: Int, to: Int, originX: Float): List<NativeListTextRun> {
    if (from >= to) return emptyList()
    val selection = Path()
    textLayout.getSelectionPath(from, to, selection)
    // A selection that reaches the line end also covers the blank up to the
    // layout edge and starts the next line; only this line's text counts.
    val lineBounds = Region(
      floor(textLayout.getLineLeft(line)).toInt(), textLayout.getLineTop(line),
      ceil(textLayout.getLineRight(line)).toInt(), textLayout.getLineBottom(line),
    )
    val runs = mutableListOf<NativeListTextRun>()
    val iterator = RegionIterator(Region().apply { setPath(selection, lineBounds) })
    val run = Rect()
    while (iterator.next(run)) runs += NativeListTextRun(originX + run.left, originX + run.right)
    return runs
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
