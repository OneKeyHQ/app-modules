package com.margelo.nitro.segmentslider

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject
import java.io.File
import kotlin.math.abs

class BubbleLayoutInstrumentation : Instrumentation() {
  override fun onCreate(arguments: Bundle?) {
    super.onCreate(arguments)
    start()
  }

  override fun onStart() {
    val automation = uiAutomation
    val result = File(targetContext.filesDir, "result.json")
    startActivitySync(
      Intent(targetContext, BubbleLayoutActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    val deadline = SystemClock.uptimeMillis() + 15_000
    while (!result.isFile && SystemClock.uptimeMillis() < deadline) {
      SystemClock.sleep(50)
    }
    if (!result.isFile) {
      finish(Activity.RESULT_CANCELED, Bundle().apply { putString("stream", "Slider harness timed out") })
      return
    }
    waitForIdleSync()
    automation.takeScreenshot()?.let { screenshot ->
      File(targetContext.filesDir, "screenshot.png").outputStream().use {
        screenshot.compress(Bitmap.CompressFormat.PNG, 100, it)
      }
    }
    finish(Activity.RESULT_OK, Bundle())
  }
}

/** Runs the production View/Canvas code; only the RN context and Nitro base are stubbed. */
class BubbleLayoutActivity : Activity() {
  private val sliders = mutableListOf<HybridSegmentSlider>()
  private val failures = mutableListOf<String>()
  private var checks = 0
  private val touchDownTimes = mutableMapOf<HybridSegmentSlider, Long>()
  private val handler = Handler(Looper.getMainLooper())
  private val density get() = resources.displayMetrics.density
  private fun px(dp: Int) = (dp * density).toInt()
  private fun check(ok: Boolean, message: String) { checks++; if (!ok) failures.add(message) }
  private fun field(slider: HybridSegmentSlider, name: String): Any =
    slider.javaClass.getDeclaredField(name).apply { isAccessible = true }.get(slider)!!

  private fun touch(slider: HybridSegmentSlider, action: Int, x: Float, elapsedMs: Long? = null) {
    val now = SystemClock.uptimeMillis()
    if (action == MotionEvent.ACTION_DOWN) touchDownTimes[slider] = now
    val downTime = touchDownTimes.getValue(slider)
    val eventTime = if (elapsedMs == null) now else downTime + elapsedMs
    val event = MotionEvent.obtain(downTime, eventTime, action, x, slider.view.height / 2f, 0)
    slider.view.dispatchTouchEvent(event)
    event.recycle()
    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
      touchDownTimes.remove(slider)
    }
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
        runHapticCases()
        runAdjustableCases(root)
        runFractionalEndpointCases()
        runAccessibilityNotificationCases(root) {
          runLayoutCases {
            runTapLingerCases(root) {
            verifyDelayedValueSync(root) {
              File(filesDir, "result.json").writeText(JSONObject().apply {
                put("checks", checks); put("failures", org.json.JSONArray(failures))
              }.toString(2))
              title.text = "${checks - failures.size}/$checks native Android checks passed"
              android.util.Log.i("SliderBubbleTest", title.text.toString())
            }
            }
          }
        }
      }, 250)
    }
  }

  // A tap keeps the bubble up for 800 ms (showing the landed value); a snap-mode
  // tap candidate shows no bubble; a drag or a system cancel hides it on
  // release; unrelated value updates end a tap's bubble. Checks the drawn
  // bubble, so a replacement implementation without the same private fields
  // still reports each case.
  private fun runTapLingerCases(root: FrameLayout, done: () -> Unit) {
    val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
      segments = 4.0; defaultValue = 0.0; showBubble = true; snapTapToSegment = true
      bubbleColor = "#000000"; bubbleTextColor = "#ffffff"
    }
    val changes = mutableListOf<Double>()
    var completions = 0
    var lastId = 0.0
    slider.onChange = { changes.add(it) }
    slider.onSlideComplete = { id, _ -> completions++; lastId = id }
    root.addView(slider.view, FrameLayout.LayoutParams(px(320), px(24)))
    slider.view.layout(0, 0, px(320), px(24))
    val inset = 12f * density
    fun x(fraction: Float) = inset + fraction * (slider.view.width - 2 * inset)
    fun press(action: Int, fraction: Float, elapsedMs: Long? = null) =
      touch(slider, action, x(fraction), elapsedMs)
    var completionsBeforeCancel = 0
    var changesBeforeHold = 0
    runSteps(listOf(
      0L to {
        press(MotionEvent.ACTION_DOWN, 0.3f)
      },
      200L to {
        check(!bubbleDrawn(slider), "snap tap candidate shows the old value")
        press(MotionEvent.ACTION_UP, 0.3f, elapsedMs = 100L)
        check(changes.lastOrNull() == 25.0, "tap no longer snaps to segment")
      },
      300L to { check(bubbleDrawn(slider), "bubble hidden right after a tap") },
      300L to { check(bubbleDrawn(slider), "bubble hidden before the tap linger ends") },
      600L to { check(!bubbleDrawn(slider), "bubble still visible after the tap linger") },
      // A new snap press hides the previous tap's bubble until it lands.
      0L to {
        press(MotionEvent.ACTION_DOWN, 0.3f)
        press(MotionEvent.ACTION_UP, 0.3f)
      },
      300L to {
        check(bubbleDrawn(slider), "bubble hidden right after a second tap")
        press(MotionEvent.ACTION_DOWN, 0.8f)
      },
      300L to {
        check(!bubbleDrawn(slider), "pending snap press kept the previous tap bubble")
        press(MotionEvent.ACTION_UP, 0.8f, elapsedMs = 200L)
      },
      300L to {
        check(bubbleDrawn(slider), "bubble hidden after the follow-up tap landed")
        changesBeforeHold = changes.size
        press(MotionEvent.ACTION_DOWN, 0.1f)
      },
      400L to {
        check(!bubbleDrawn(slider), "pending long press kept the previous tap bubble")
        press(MotionEvent.ACTION_UP, 0.1f, elapsedMs = 350L)
        check(changes.size == changesBeforeHold, "long press snapped to a segment")
      },
      300L to { check(!bubbleDrawn(slider), "bubble lingered after a long press") },
      // A drag that follows a tap hides on release.
      0L to {
        press(MotionEvent.ACTION_DOWN, 0.3f)
        press(MotionEvent.ACTION_MOVE, 0.6f)
      },
      300L to {
        check(bubbleDrawn(slider), "bubble hidden during drag")
        press(MotionEvent.ACTION_UP, 0.6f)
      },
      300L to { check(!bubbleDrawn(slider), "bubble lingers after a drag") },
      // A system cancel is not a tap: no linger, one completion.
      0L to {
        completionsBeforeCancel = completions
        press(MotionEvent.ACTION_DOWN, 0.3f)
        press(MotionEvent.ACTION_CANCEL, 0.3f)
      },
      300L to {
        check(!bubbleDrawn(slider), "cancelled press lingered the bubble")
        check(completions == completionsBeforeCancel + 1, "cancelled press completions changed")
        slider.snapTapToSegment = false
        press(MotionEvent.ACTION_DOWN, 0.5f)
      },
      300L to {
        check(bubbleDrawn(slider), "plain press hid the bubble")
        press(MotionEvent.ACTION_CANCEL, 0.5f)
      },
      300L to {
        check(!bubbleDrawn(slider), "cancelled plain press lingered the bubble")
        slider.snapTapToSegment = true
        // The lingering bubble belongs to its tap.
        press(MotionEvent.ACTION_DOWN, 0.3f)
        press(MotionEvent.ACTION_UP, 0.3f)
        slider.setValue(25.0, lastId)
      },
      300L to {
        check(bubbleDrawn(slider), "accepting the tap value ended its linger")
        slider.setValue(60.0, lastId)
      },
      100L to {
        check(!bubbleDrawn(slider), "controlled update kept the tap bubble")
        press(MotionEvent.ACTION_DOWN, 0.3f)
        press(MotionEvent.ACTION_UP, 0.3f)
      },
      300L to {
        check(bubbleDrawn(slider), "bubble hidden after a tap")
        slider.view.performAccessibilityAction(AccessibilityAction.ACTION_SCROLL_FORWARD.id, null)
        check(!bubbleDrawn(slider), "accessibility step kept the tap bubble")
        slider.showBubble = false
        slider.showBubble = true
        check(!bubbleDrawn(slider), "re-enabling showBubble resurrected the bubble")
        // Turning showBubble on during a drag shows the drag's bubble.
        slider.showBubble = false
        slider.snapTapToSegment = false
        press(MotionEvent.ACTION_DOWN, 0.2f)
        press(MotionEvent.ACTION_MOVE, 0.5f)
        slider.showBubble = true
      },
      300L to {
        check(bubbleDrawn(slider), "showBubble enabled mid-drag stayed hidden")
        press(MotionEvent.ACTION_UP, 0.5f)
      },
      300L to {
        check(!bubbleDrawn(slider), "drag bubble lingered after enabling showBubble")
        slider.snapTapToSegment = true
        press(MotionEvent.ACTION_DOWN, 0.3f)
        check(!bubbleDrawn(slider), "pending press showed the bubble before disabling snap")
        slider.snapTapToSegment = false
        press(MotionEvent.ACTION_MOVE, 0.6f)
      },
      300L to {
        check(changes.lastOrNull() == 60.0 && bubbleDrawn(slider),
          "disabling snap: continuous move did not show its value")
        press(MotionEvent.ACTION_UP, 0.6f)
      },
      300L to {
        check(!bubbleDrawn(slider), "drag bubble lingered after disabling snap")
        slider.snapTapToSegment = true
        press(MotionEvent.ACTION_DOWN, 0.3f)
        check(!bubbleDrawn(slider), "pending press showed the bubble before removing marks")
        slider.segments = 0.0
        press(MotionEvent.ACTION_MOVE, 0.7f)
      },
      300L to {
        check(changes.lastOrNull() == 70.0 && bubbleDrawn(slider),
          "removing marks: continuous move did not show its value")
        press(MotionEvent.ACTION_UP, 0.7f)
      },
      300L to {
        check(!bubbleDrawn(slider), "drag bubble lingered after removing marks")
        root.removeView(slider.view)
        slider.dispose()
      }
    ), done)
  }

  /** Runs each step after its delay, in order, then [done]. */
  private fun runSteps(steps: List<Pair<Long, () -> Unit>>, done: () -> Unit) {
    if (steps.isEmpty()) { done(); return }
    val (delay, step) = steps.first()
    handler.postDelayed({ step(); runSteps(steps.drop(1), done) }, delay)
  }

  /** True when the bubble paints anything in the headroom above the view. */
  private fun bubbleDrawn(slider: HybridSegmentSlider): Boolean {
    val headroom = px(60)
    val bitmap = Bitmap.createBitmap(slider.view.width, headroom + slider.view.height,
      Bitmap.Config.ARGB_8888)
    slider.view.draw(Canvas(bitmap).apply { translate(0f, headroom.toFloat()) })
    var drawn = false
    loop@ for (y in 0 until headroom) for (x in 0 until bitmap.width) {
      if (Color.alpha(bitmap.getPixel(x, y)) > 0) { drawn = true; break@loop }
    }
    bitmap.recycle()
    return drawn
  }

  private fun verifyDelayedValueSync(root: FrameLayout, done: () -> Unit) {
    val slider = HybridSegmentSlider(ThemedReactContext(this)).apply { defaultValue = 0.0 }
    root.addView(slider.view, FrameLayout.LayoutParams(px(320), px(24)))
    slider.view.layout(0, 0, px(320), px(24))
    val inset = field(slider, "insetPx") as Float
    fun x(fraction: Float) = inset + fraction * (slider.view.width - 2 * inset)
    val changes = mutableListOf<Double>()
    val completed = mutableListOf<Pair<Double, Double>>()
    slider.onChange = { changes.add(it) }
    slider.onSlideComplete = { id, value -> completed.add(id to value) }

    touch(slider, MotionEvent.ACTION_DOWN, x(0.6f))
    touch(slider, MotionEvent.ACTION_UP, x(0.6f))
    slider.setValue(40.0, 1.0)
    touch(slider, MotionEvent.ACTION_DOWN, x(0.7f))
    handler.post {
      check(field(slider, "currentValue") == 70.0, "delayed sync leaves newer drag alone")
      touch(slider, MotionEvent.ACTION_MOVE, x(0.8f))
      touch(slider, MotionEvent.ACTION_UP, x(0.8f))
      check(completed == listOf(1.0 to 60.0, 2.0 to 80.0), "final native values: $completed")
      slider.setValue(40.0, 1.0)
      handler.post {
        check(field(slider, "currentValue") == 80.0, "sync from old interaction stays stale after release")
        slider.setValue(50.0, 2.0)
        handler.post {
          check(field(slider, "currentValue") == 50.0, "sync applies for latest completed interaction")
          check(changes == listOf(60.0, 70.0, 80.0), "only touches emitted changes: $changes")
          root.removeView(slider.view)
          slider.dispose()
          done()
        }
      }
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

  // Reflection keeps `run.py --source` able to compile implementations that
  // predate the recorder; their haptic checks then fail instead of the build.
  private fun recordTicks(slider: HybridSegmentSlider, ticks: MutableList<String>) {
    val setter = slider.javaClass.methods.firstOrNull { it.name.startsWith("setHapticTickRecorder") }
    if (setter == null) {
      check(false, "hapticTickRecorder is missing")
      return
    }
    val sink: (String) -> Unit = { ticks.add(it) }
    setter.invoke(slider, sink)
  }

  // Per-step haptics: one tick per value bucket during a drag; entering a mark
  // fires the mark tick instead of the step tick; 0 keeps mark-only ticks.
  private fun runHapticCases() {
    fun ticks(step: Double): List<String> {
      val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
        segments = 4.0; defaultValue = 0.0; hapticStep = step
        view.layout(0, 0, px(320), px(24))
      }
      val ticks = mutableListOf<String>()
      recordTicks(slider, ticks)
      val inset = field(slider, "insetPx") as Float
      fun x(v: Double) = inset + (v / 100).toFloat() * (slider.view.width - 2 * inset)
      touch(slider, MotionEvent.ACTION_DOWN, x(0.0))
      for (v in listOf(1.0, 1.2, 2.0, 25.0, 26.0)) touch(slider, MotionEvent.ACTION_MOVE, x(v))
      touch(slider, MotionEvent.ACTION_UP, x(26.0))
      slider.dispose()
      return ticks
    }
    val stepTicks = ticks(1.0)
    check(stepTicks == listOf("mark", "step", "step", "mark", "step"), "hapticStep 1 ticks: $stepTicks")
    val markTicks = ticks(0.0)
    check(markTicks == listOf("mark", "mark"), "hapticStep 0 ticks: $markTicks")

    fun make(configure: HybridSegmentSlider.() -> Unit) =
      HybridSegmentSlider(ThemedReactContext(this)).apply {
        configure()
        view.layout(0, 0, px(320), px(24))
      }
    fun xAt(slider: HybridSegmentSlider, frac: Double): Float {
      val inset = field(slider, "insetPx") as Float
      return inset + frac.toFloat() * (slider.view.width - 2 * inset)
    }

    // A tap that never moves fires no step tick (20% is away from any mark).
    val tapTicks = mutableListOf<String>()
    val tap = make { segments = 4.0; defaultValue = 0.0; hapticStep = 1.0 }
    recordTicks(tap, tapTicks)
    touch(tap, MotionEvent.ACTION_DOWN, xAt(tap, 0.2))
    touch(tap, MotionEvent.ACTION_UP, xAt(tap, 0.2))
    check(tapTicks.isEmpty(), "tap without movement ticked: $tapTicks")
    tap.dispose()

    // Disabling mid-drag ends the interaction once and ignores later moves.
    val cancelChanges = mutableListOf<Double>()
    var cancelCompletes = 0
    val cancel = make { segments = 4.0; defaultValue = 0.0 }
    cancel.onChange = { cancelChanges.add(it) }
    cancel.onSlideComplete = { _, _ -> cancelCompletes++ }
    touch(cancel, MotionEvent.ACTION_DOWN, xAt(cancel, 0.3))
    cancel.disabled = true
    touch(cancel, MotionEvent.ACTION_MOVE, xAt(cancel, 0.8))
    touch(cancel, MotionEvent.ACTION_UP, xAt(cancel, 0.8))
    check(cancelCompletes == 1, "disable mid-drag completions: $cancelCompletes")
    check(cancelChanges == listOf(30.0), "disable mid-drag values: $cancelChanges")
    cancel.dispose()

    // Fractional bounds never emit a value past them.
    val boundChanges = mutableListOf<Double>()
    val bounded = make { min = 0.5; max = 10.5; segments = 0.0; defaultValue = 0.5 }
    bounded.onChange = { boundChanges.add(it) }
    touch(bounded, MotionEvent.ACTION_DOWN, bounded.view.width.toFloat())
    touch(bounded, MotionEvent.ACTION_MOVE, 0f)
    touch(bounded, MotionEvent.ACTION_UP, 0f)
    check(boundChanges == listOf(10.0, 1.0), "fractional bound values: $boundChanges")
    bounded.dispose()
  }

  private fun runAdjustableCases(root: FrameLayout) {
    val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
      min = 1.0
      max = 20.0
      defaultValue = 10.0
      segments = 4.0
      fillColor = "#ff0000"
      trackColor = "#dddddd"
      thumbColor = "#00ff00"
    }
    val view = slider.view
    root.addView(view, FrameLayout.LayoutParams(px(320), px(24)))
    view.layout(0, 0, px(320), px(24))

    val events = mutableListOf<String>()
    slider.onSlideStart = { events.add("start") }
    slider.onChange = { events.add("change:$it") }
    slider.onSlideComplete = { id, value -> events.add("complete:$id:$value") }
    val forward = AccessibilityAction.ACTION_SCROLL_FORWARD
    val backward = AccessibilityAction.ACTION_SCROLL_BACKWARD
    val setProgress = AccessibilityAction.ACTION_SET_PROGRESS
    fun press(key: Int) = view.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, key))
    fun range() = view.createAccessibilityNodeInfo().rangeInfo

    val node = view.createAccessibilityNodeInfo()
    check(node.className == "android.widget.SeekBar", "adjustable node uses SeekBar role")
    check(node.isFocusable && node.isEnabled, "enabled slider is focusable")
    check(range()?.let { it.min == 1f && it.max == 20f && it.current == 10f } == true,
      "adjustable node exposes range and current value")
    check(node.actionList.containsAll(listOf(forward, backward, setProgress)),
      "adjustable node exposes both steps and set progress")
    check(view.performAccessibilityAction(forward.id, null) && range()?.current == 11f,
      "accessibility increment changes the slider")
    check(view.performAccessibilityAction(backward.id, null) && range()?.current == 10f,
      "accessibility decrement changes the slider")
    check(!view.performAccessibilityAction(setProgress.id, Bundle()),
      "missing progress leaves the slider unchanged")
    val progress = Bundle().apply {
      putFloat(AccessibilityNodeInfo.ACTION_ARGUMENT_PROGRESS_VALUE, 17f)
    }
    check(view.performAccessibilityAction(setProgress.id, progress) && range()?.current == 17f,
      "accessibility set progress changes the slider")
    check(events == listOf("start", "change:11.0", "complete:1.0:11.0",
      "start", "change:10.0", "complete:2.0:10.0",
      "start", "change:17.0", "complete:3.0:17.0"), "discrete callback order: $events")
    check(press(KeyEvent.KEYCODE_DPAD_RIGHT) && range()?.current == 18f,
      "LTR right arrow increases the slider")

    view.layoutDirection = View.LAYOUT_DIRECTION_RTL
    check(press(KeyEvent.KEYCODE_DPAD_RIGHT) && range()?.current == 17f,
      "RTL right arrow decreases the slider")
    check(press(KeyEvent.KEYCODE_DPAD_LEFT) && range()?.current == 18f,
      "RTL left arrow increases the slider")
    check(press(KeyEvent.KEYCODE_MOVE_HOME) && range()?.current == 1f,
      "RTL Home selects minimum")
    check(!view.createAccessibilityNodeInfo().actionList.contains(backward) &&
      !view.performAccessibilityAction(backward.id, null), "minimum does not decrement")
    check(press(KeyEvent.KEYCODE_MOVE_END) && range()?.current == 20f,
      "RTL End selects maximum")

    for ((x, expected) in listOf(view.width.toFloat() to 1f, 0f to 20f)) {
      touch(slider, MotionEvent.ACTION_DOWN, x)
      touch(slider, MotionEvent.ACTION_UP, x)
      check(range()?.current == expected, "RTL tap $x selects $expected")
    }

    fun draw(direction: Int): IntArray {
      view.layoutDirection = direction
      val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
      view.draw(Canvas(bitmap))
      return IntArray(view.width) { bitmap.getPixel(it, view.height / 2) }
    }
    view.performAccessibilityAction(setProgress.id, progress)
    val ltr = draw(View.LAYOUT_DIRECTION_LTR)
    val rtl = draw(View.LAYOUT_DIRECTION_RTL)
    for (color in listOf(Color.RED, Color.GREEN)) {
      val ltrPixels = ltr.indices.filter { ltr[it] == color }
      val rtlPixels = rtl.indices.filter { rtl[it] == color }
      check(ltrPixels.isNotEmpty() && rtlPixels.isNotEmpty() &&
        abs(ltrPixels.first() + rtlPixels.last() - (view.width - 1)) <= 1 &&
        abs(ltrPixels.last() + rtlPixels.first() - (view.width - 1)) <= 1,
        "RTL drawing mirrors color $color with the touch mapping")
    }

    slider.disabled = true
    val disabled = view.createAccessibilityNodeInfo()
    check(!disabled.isEnabled && disabled.actionList.none { it in listOf(forward, backward, setProgress) },
      "disabled node exposes no adjustment actions")
    check(!view.performAccessibilityAction(backward.id, null) &&
      !press(KeyEvent.KEYCODE_DPAD_LEFT) && range()?.current == 17f,
      "disabled slider rejects accessibility and key adjustments")
    root.removeView(view)
    slider.dispose()
  }

  private fun runFractionalEndpointCases() {
    for ((endpoint, outsideKey, outsideAction) in listOf(
      Triple(0.5, KeyEvent.KEYCODE_DPAD_LEFT, AccessibilityAction.ACTION_SCROLL_BACKWARD),
      Triple(10.5, KeyEvent.KEYCODE_DPAD_RIGHT, AccessibilityAction.ACTION_SCROLL_FORWARD),
    )) {
      val slider = HybridSegmentSlider(ThemedReactContext(this)).apply {
        min = 0.5
        max = 10.5
        defaultValue = endpoint
      }
      val changes = mutableListOf<Double>()
      slider.onChange = { changes.add(it) }
      check(!slider.view.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, outsideKey)),
        "outward key is ignored at fractional endpoint $endpoint")
      check(!slider.view.performAccessibilityAction(outsideAction.id, null),
        "outward accessibility step is ignored at fractional endpoint $endpoint")
      check(field(slider, "currentValue") == endpoint && changes.isEmpty(),
        "outward steps preserve fractional endpoint $endpoint without callbacks")
      slider.dispose()
    }
  }

  private fun runAccessibilityNotificationCases(root: FrameLayout, done: () -> Unit) {
    data class ProgressEvent(val type: Int, val flags: Int, val min: Float, val max: Float, val value: Float)
    data class Case(val name: String, val expected: List<ProgressEvent>, val act: () -> Unit)

    val manager = getSystemService(AccessibilityManager::class.java)
    check(manager?.isEnabled == true, "accessibility is enabled for the notification tests")
    val slider = HybridSegmentSlider(ThemedReactContext(this)).apply { defaultValue = 25.0 }
    val view = slider.view
    view.layout(0, 0, px(320), px(24))
    root.addView(view, FrameLayout.LayoutParams(px(320), px(24)))
    val snapshots = mutableListOf<ProgressEvent>()
    view.accessibilityDelegate = object : View.AccessibilityDelegate() {
      override fun sendAccessibilityEventUnchecked(host: View, event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
          event.eventType == AccessibilityEvent.TYPE_VIEW_SELECTED
        ) {
          host.createAccessibilityNodeInfo().rangeInfo?.let {
            snapshots.add(ProgressEvent(event.eventType, event.contentChangeTypes, it.min, it.max, it.current))
          }
        }
        super.sendAccessibilityEventUnchecked(host, event)
      }
    }

    val modern = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    val expectedType = if (modern) AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED else AccessibilityEvent.TYPE_VIEW_SELECTED
    val expectedFlags = if (modern) AccessibilityEvent.CONTENT_CHANGE_TYPE_STATE_DESCRIPTION else 0
    fun event(value: Float, min: Float = 0f, max: Float = 100f) =
      listOf(ProgressEvent(expectedType, expectedFlags, min, max, value))
    val inset = field(slider, "insetPx") as Float
    fun x(value: Float) = inset + (view.width - 2 * inset) * value / 100f

    val cases = listOf(
      Case("drag coalesces into one progress event", event(75f)) {
        touch(slider, MotionEvent.ACTION_DOWN, x(30f))
        touch(slider, MotionEvent.ACTION_MOVE, x(60f))
        touch(slider, MotionEvent.ACTION_MOVE, x(75f))
        touch(slider, MotionEvent.ACTION_UP, x(75f))
      },
      Case("controlled update sends a progress event", event(50f)) {
        slider.setValue(50.0, 1.0)
      },
      Case("range changes coalesce into one progress event", event(50f, 20f, 80f)) {
        slider.min = 20.0
        slider.max = 80.0
      },
      Case("same-value update sends no progress event", emptyList()) {
        slider.setValue(50.0, 1.0)
      },
      Case("accessibility increment announces the new progress", event(51f, 20f, 80f)) {
        check(view.performAccessibilityAction(AccessibilityAction.ACTION_SCROLL_FORWARD.id, null),
          "accessibility increment is accepted")
      },
      Case("accessibility decrement announces the new progress", event(50f, 20f, 80f)) {
        check(view.performAccessibilityAction(AccessibilityAction.ACTION_SCROLL_BACKWARD.id, null),
          "accessibility decrement is accepted")
      },
    )

    fun runCase(index: Int) {
      if (index == cases.size) {
        root.removeView(view)
        slider.dispose()
        done()
        return
      }
      val case = cases[index]
      snapshots.clear()
      case.act()
      handler.postDelayed({
        check(snapshots == case.expected, "${case.name}: expected ${case.expected}, got $snapshots")
        runCase(index + 1)
      }, 200)
    }
    runCase(0)
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
    slider.onSlideComplete = { _, _ -> completes++ }
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
