package com.margelo.nitro.segmentslider

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ScrollView
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject
import java.io.File

/**
 * Renders every row of `scenarios.json` (bundled as an APK asset) with the
 * production View/Canvas slider, then writes `files/gallery.png` and
 * `files/result.json`. Only the RN context and Nitro base are stubbed.
 */
class StyleGalleryActivity : Activity() {
  private val sliders = mutableListOf<HybridSegmentSlider>()
  private val handler = Handler(Looper.getMainLooper())
  private val density get() = resources.displayMetrics.density
  private fun px(dp: Double) = Math.round(dp * density).toInt()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val spec = JSONObject(assets.open("scenarios.json").bufferedReader().readText())
    val sliderWidth = spec.getDouble("sliderWidth")
    val sideInset = spec.getDouble("sideInset")
    val rowHeight = spec.getDouble("rowHeight")
    val sliderTop = spec.getDouble("sliderTop")
    val palettes = spec.getJSONObject("palettes")
    val rows = spec.getJSONArray("rows")

    val gallery = FrameLayout(this)
    val galleryWidth = px(sliderWidth + sideInset * 2)
    val galleryHeight = px(rowHeight * rows.length())
    val scroll = ScrollView(this)
    scroll.addView(gallery, FrameLayout.LayoutParams(galleryWidth, galleryHeight))
    setContentView(scroll)

    val pressed = mutableListOf<Pair<HybridSegmentSlider, Float>>()
    for (i in 0 until rows.length()) {
      val row = rows.getJSONObject(i)
      val palette = palettes.getJSONObject(row.getString("palette"))
      val rowView = FrameLayout(this).apply {
        setBackgroundColor(SegmentSliderColorParser.parse(palette.getString("background")))
        clipChildren = false
      }
      gallery.addView(rowView, FrameLayout.LayoutParams(galleryWidth, px(rowHeight)).apply {
        topMargin = px(rowHeight * i)
      })
      val value = row.getDouble("value")
      val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
        fillColor = palette.getString("fillColor")
        trackColor = palette.getString("trackColor")
        thumbColor = palette.getString("thumbColor")
        thumbBorderColor = palette.getString("thumbBorderColor")
        markActiveColor = palette.getString("markActiveColor")
        markInactiveColor = palette.getString("markInactiveColor")
        markBorderColor = palette.getString("markBorderColor")
        bubbleColor = palette.getString("bubbleColor")
        bubbleTextColor = palette.getString("bubbleTextColor")
        showBubble = true
        segments = row.getDouble("segments")
        defaultValue = value
      }
      rowView.addView(slider.view, FrameLayout.LayoutParams(px(sliderWidth), px(24.0)).apply {
        leftMargin = px(sideInset)
        topMargin = px(sliderTop)
      })
      sliders.add(slider)
      if (row.optBoolean("pressed")) {
        // Same 12-dp endpoint inset the production view maps touches with.
        val x = (12 + value / 100 * (sliderWidth - 24)) * density
        pressed.add(slider to x.toFloat())
      }
    }

    handler.postDelayed({
      for ((slider, x) in pressed) {
        val now = SystemClock.uptimeMillis()
        val event = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x,
          slider.view.height / 2f, 0)
        slider.view.dispatchTouchEvent(event)
        event.recycle()
      }
      // Let the 120-ms thumb scale animation settle before drawing.
      handler.postDelayed({
        val bitmap = Bitmap.createBitmap(gallery.width, gallery.height, Bitmap.Config.ARGB_8888)
        gallery.draw(Canvas(bitmap))
        File(filesDir, "gallery.png").outputStream().use {
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        File(filesDir, "result.json").writeText(JSONObject().apply {
          put("rows", rows.length())
          put("density", density.toDouble())
          put("failures", org.json.JSONArray())
        }.toString())
      }, 600)
    }, 300)
  }
}
