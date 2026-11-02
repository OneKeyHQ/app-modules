package com.margelo.nitro.segmentslider

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject
import java.io.File
import kotlin.math.abs

/** Runs the production View/Canvas code; only the RN context and Nitro base are stubbed. */
class BubbleLayoutActivity : Activity() {
  private val sliders = mutableListOf<HybridSegmentSlider>()
  private val failures = mutableListOf<String>()
  private var checks = 0
  private val handler = Handler(Looper.getMainLooper())
  private val density get() = resources.displayMetrics.density
  private fun px(dp: Int) = (dp * density).toInt()
  private fun check(ok: Boolean, message: String) { checks++; if (!ok) failures.add(message) }
  private fun field(slider: HybridSegmentSlider, name: String): Any =
    slider.javaClass.getDeclaredField(name).apply { isAccessible = true }.get(slider)!!

  private fun touch(slider: HybridSegmentSlider, action: Int, x: Float) {
    val now = SystemClock.uptimeMillis()
    val event = MotionEvent.obtain(now, now, action, x, slider.view.height / 2f, 0)
    slider.view.dispatchTouchEvent(event)
    event.recycle()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val root = FrameLayout(this).apply { setBackgroundColor(Color.WHITE) }
    val title = TextView(this).apply { text = "Android native slider: 0 / 50 / 100%"; textSize = 16f }
    root.addView(title, FrameLayout.LayoutParams(-1, px(40)))
    val availableWidth = resources.displayMetrics.widthPixels - px(48)
    for ((index, value) in listOf(0.0, 50.0, 100.0).withIndex()) {
      // The outer sheet clips; its RN-like inner wrapper permits the value
      // bubble to render above the 24dp touch view without clipping children.
      val sheet = FrameLayout(this).apply { clipChildren = true; clipToPadding = true }
      root.addView(sheet, FrameLayout.LayoutParams(availableWidth, px(100)).apply {
        leftMargin = px(24); topMargin = px(60 + index * 110)
      })
      val wrapper = FrameLayout(this).apply { clipChildren = false; clipToPadding = false }
      sheet.addView(wrapper, FrameLayout.LayoutParams(-1, -1))
      val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
        defaultValue = value; segments = 4.0; showBubble = true
        fillColor = "#111111"; trackColor = "#dddddd"
        thumbColor = "#bbbbbb"; thumbBorderColor = "#777777"
        markActiveColor = "#111111"; markInactiveColor = "#ffffff"
        markBorderColor = "#999999"; bubbleColor = "#111111"; bubbleTextColor = "#ffffff"
      }
      wrapper.addView(slider.view, FrameLayout.LayoutParams(-1, px(24)).apply { topMargin = px(60) })
      sliders.add(slider)
    }
    setContentView(root)
    root.post {
      for ((index, slider) in sliders.withIndex()) {
        val x = when (index) { 0 -> 0f; 1 -> slider.view.width / 2f; else -> slider.view.width.toFloat() }
        touch(slider, MotionEvent.ACTION_DOWN, x)
      }
      handler.postDelayed({
        for ((index, slider) in sliders.withIndex()) verify(slider, index * 50.0)
        runInteractionCases()
        runLayoutCases {
          File(filesDir, "result.json").writeText(JSONObject().apply {
            put("checks", checks); put("failures", org.json.JSONArray(failures))
          }.toString(2))
          title.text = "${checks - failures.size}/$checks native Android checks passed"
          android.util.Log.i("SliderBubbleTest", title.text.toString())
        }
      }, 250)
    }
  }

  private fun verify(slider: HybridSegmentSlider, value: Double) {
    val width = slider.view.width.toFloat()
    val thumbScale = field(slider, "thumbScale") as Float
    val inset = field(slider, "insetPx") as Float
    val thumbX = inset + ((value - slider.min) / (slider.max - slider.min)).toFloat() * (width - 2 * inset)
    val radius = 8 * density * thumbScale
    check(thumbX - radius >= -0.01f, "$value: thumb left clipped")
    check(thumbX + radius <= width + 0.01f, "$value: thumb right clipped")
    check(slider.view.height / 2f - radius >= 0, "$value: thumb top clipped")
    check(slider.view.height / 2f + radius <= slider.view.height, "$value: thumb bottom clipped")
    check(thumbX - 9 * density >= -0.01f, "$value: halo left clipped")
    check(thumbX + 9 * density <= width + 0.01f, "$value: halo right clipped")

    val bitmap = Bitmap.createBitmap(slider.view.width, px(100), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap).apply { translate(0f, px(60).toFloat()) }
    slider.view.draw(canvas)
    val bubble = field(slider, "rect") as RectF
    check(bubble.left >= -0.01f, "$value: bubble left clipped")
    check(bubble.right <= width + 0.01f, "$value: bubble right clipped")
    check(bubble.top + px(60) >= 0, "$value: bubble above sheet")
    check(bubble.bottom <= slider.view.height / 2f - radius,
      "$value: bubble overlaps thumb area")
    val text = "${Math.round(value)}%"
    val textPaint = field(slider, "bubbleTextPaint") as Paint
    val halfText = textPaint.measureText(text) / 2
    // Yellow text in a second software draw makes its real raster bounds
    // distinguishable from the black bubble and verifies centering after clamp.
    val originalTextColor = textPaint.color
    textPaint.color = Color.YELLOW
    bitmap.eraseColor(Color.TRANSPARENT)
    slider.view.draw(canvas)
    var minX = bitmap.width
    var maxX = -1
    for (y in 0 until px(60)) for (x in 0 until bitmap.width) {
      val color = bitmap.getPixel(x, y)
      if (Color.red(color) > 180 && Color.green(color) > 180 && Color.blue(color) < 80) {
        minX = minOf(minX, x); maxX = maxOf(maxX, x)
      }
    }
    check(maxX >= minX, "$value: bubble text missing")
    check(abs((minX + maxX) / 2f - bubble.centerX()) < 2 * density, "$value: bubble text not centered")
    check(bubble.centerX() - halfText >= 0 && bubble.centerX() + halfText <= width,
      "$value: bubble text clipped")
    textPaint.color = originalTextColor
    bitmap.recycle()
  }

  private fun runLayoutCases(onComplete: () -> Unit) {
    val cases = mutableListOf<Pair<HybridSegmentSlider, Double>>()
    for (dark in listOf(false, true)) for (width in listOf(160, 320)) {
      for ((minimum, values) in listOf(0.0 to listOf(0.0, 1.0, 50.0, 99.0, 100.0),
        -100.0 to listOf(-100.0, 0.0, 100.0))) for (value in values) {
        val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
          min = minimum; max = 100.0; defaultValue = value; segments = 4.0
          centerOrigin = minimum < 0
          bubbleColor = if (dark) "#ffffff" else "#111111"
          bubbleTextColor = if (dark) "#111111" else "#ffffff"
          view.layout(0, 0, px(width), px(24))
        }
        val inset = field(slider, "insetPx") as Float
        val x = inset + ((value - minimum) / (100 - minimum)).toFloat() * (slider.view.width - 2 * inset)
        touch(slider, MotionEvent.ACTION_DOWN, x)
        cases.add(slider to value)
      }
    }
    handler.postDelayed({
      for ((slider, value) in cases) {
        verify(slider, value)
        touch(slider, MotionEvent.ACTION_UP, 0f)
        slider.dispose()
      }
      onComplete()
    }, 250)
  }

  private fun runInteractionCases() {
    val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
      segments = 4.0; snapTapToSegment = true; defaultValue = 50.0
      view.layout(0, 0, px(320), px(24))
    }
    val changes = mutableListOf<Double>()
    var starts = 0
    var completes = 0
    slider.onChange = { changes.add(it) }
    slider.onSlideStart = { starts++ }
    slider.onSlideComplete = { completes++ }
    val inset = field(slider, "insetPx") as Float
    touch(slider, MotionEvent.ACTION_DOWN, slider.view.width / 2f)
    for (value in listOf(0.0, 50.0, 100.0)) {
      touch(slider, MotionEvent.ACTION_MOVE, inset + (value / 100).toFloat() * (slider.view.width - 2 * inset))
    }
    touch(slider, MotionEvent.ACTION_UP, slider.view.width.toFloat())
    check(changes == listOf(0.0, 50.0, 100.0), "drag values changed: $changes")
    check(starts == 1 && completes == 1, "interaction callbacks changed")
    for ((x, value) in listOf(0f to 0.0, slider.view.width.toFloat() to 100.0,
      (inset + 0.3f * (slider.view.width - 2 * inset)) to 25.0)) {
      touch(slider, MotionEvent.ACTION_DOWN, x)
      touch(slider, MotionEvent.ACTION_UP, x)
      check(changes.last() == value, "tap $x no longer selects $value")
    }
    slider.dispose()
  }
}
