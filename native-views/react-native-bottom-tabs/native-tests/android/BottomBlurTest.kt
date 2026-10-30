package com.onekey.bottomtabs

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.test.InstrumentationTestCase
import android.view.Choreographer
import android.view.FrameMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.react.internal.ChoreographerProvider
import com.facebook.react.modules.core.ReactChoreographer
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader
import org.json.JSONObject
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class BottomBlurTestActivity : Activity()

/** Exercises shipping views and GPU output; not a physical-device performance test. */
@Suppress("DEPRECATION")
open class BottomBlurTest : InstrumentationTestCase() {
  protected open val floatingStyle = false
  private lateinit var activity: Activity
  private lateinit var tabs: BottomTabsView
  private lateinit var scroll: ScrollView
  private val sceneSizes = mutableListOf<Pair<Double, Double>>()
  private val barHeights = mutableListOf<Int>()

  override fun setUp() {
    super.setUp()
    activity = instrumentation.startActivitySync(
      Intent(instrumentation.context, BottomBlurTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
    onMain {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      ReactChoreographer.initialize(object : ChoreographerProvider {
        override fun getChoreographer() = object : ChoreographerProvider.Choreographer {
          private val delegate = Choreographer.getInstance()
          override fun postFrameCallback(callback: Choreographer.FrameCallback) = delegate.postFrameCallback(callback)
          override fun removeFrameCallback(callback: Choreographer.FrameCallback) = delegate.removeFrameCallback(callback)
        }
      })
      tabs = BottomTabsView(activity)
      if (floatingStyle) tabs.setAndroidTabBarStyle("floating")
      tabs.setActiveTintColor(Color.BLUE)
      tabs.setInactiveTintColor(Color.DKGRAY)
      tabs.onNativeLayoutListener = { w, h -> sceneSizes.add(w to h) }
      tabs.onTabBarMeasuredListener = { barHeights.add(it) }
      scroll = ScrollView(activity).apply {
        addView(Stripes(activity), FrameLayout.LayoutParams(-1, dp(4000)))
      }
      tabs.addView(scroll, 0, FrameLayout.LayoutParams(-1, -1))
      tabs.updateItems(mutableListOf(TabInfo("home", "Home")))
      tabs.disablePageAnimations = true
      tabs.setSelectedItem("home")
      activity.setContentView(tabs)
    }
    settle()
    onMain {
      val container = tabs.layoutHolder.getChildAt(0)
      assertTrue("Scroll source must be laid out: host=${tabs.height}, holder=${tabs.layoutHolder.height}, container=${container.height}/${container.visibility}, scroll=${scroll.height}/${scroll.visibility}, source=${scroll.getChildAt(0).height}, params=${scroll.layoutParams.width}x${scroll.layoutParams.height}", scroll.height > 0 && scroll.getChildAt(0).height > tabs.height)
    }
  }

  override fun tearDown() {
    onMain {
      activity.setContentView(View(activity))
      tabs.onDropViewInstance()
      activity.finish()
    }
    super.tearDown()
  }

  fun testToggleGeometryHiddenBarAndRelease() {
    if (Build.VERSION.SDK_INT < 31) return
    onMain {
      assertTrue(tabs.isHardwareAccelerated)
      assertEquals(floatingStyle, tabs.layoutHolder.height == tabs.height)
      tabs.setAndroidBottomBlurEnabled(true)
    }
    settle()
    onMain {
      assertEquals(tabs.height, tabs.layoutHolder.height)
      assertEquals(Utils.convertPixelsToDp(activity, tabs.height), sceneSizes.last().second)
      assertTrue(barHeights.last() > 0)
      tabs.setTabBarHidden(true)
    }
    settle()
    onMain {
      assertEquals(0, barHeights.last())
      assertNull(blurRenderer())
      assertEquals(tabs.height, tabs.layoutHolder.height)
      tabs.setTabBarHidden(false)
    }
    settle()
    onMain { tabs.setAndroidBottomBlurEnabled(false) }
    settle()
    onMain {
      assertEquals(floatingStyle, tabs.layoutHolder.height == tabs.height)
      assertEquals(Utils.convertPixelsToDp(activity, tabs.layoutHolder.height), sceneSizes.last().second)
      assertNull(blurRenderer())
      tabs.setAndroidBottomBlurEnabled(true)
    }
    settle()
    onMain {
      assertNotNull(blurRenderer())
      tabs.onDropViewInstance()
      tabs.invalidate()
    }
    settle()
    onMain {
      assertNull(blurRenderer())
      activity.setContentView(View(activity))
      assertNull(blurRenderer())
    }
  }

  fun testLiveBackdropReducesContrastAndUpdatesDuringScroll() {
    if (Build.VERSION.SDK_INT < 31) return
    var sampleY = 0
    var aboveBarY = 0
    var sampleStartX = 0
    var sampleEndX = 0
    onMain {
      tabs.setAndroidBottomBlurEnabled(true)
      scroll.scrollTo(0, dp(30))
    }
    settle()
    onMain {
      val position = IntArray(2)
      tabs.getLocationOnScreen(position)
      val bar = backdropBar()
      val cornerInset = if (floatingStyle) bar.height / 2 else dp(8)
      sampleY = position[1] + bar.top + dp(2)
      aboveBarY = position[1] + bar.top - dp(12)
      sampleStartX = position[0] + bar.left + cornerInset
      sampleEndX = position[0] + bar.right - cornerInset
    }
    val blurred = screenshot()
    onMain { tabs.setTabBarHidden(true) }
    settle()
    val raw = screenshot()
    for ((name, bitmap) in listOf("blur-on" to blurred, "blur-hidden" to raw)) {
      instrumentation.context.getExternalFilesDir(null)!!.resolve("$name.png").outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
      }
    }
    onMain {
      instrumentation.sendStatus(0, Bundle().apply {
        putString("geometry", "host=${tabs.width}x${tabs.height}, scene=${tabs.layoutHolder.width}x${tabs.layoutHolder.height}, scroll=${scroll.width}x${scroll.height}, source=${scroll.getChildAt(0).width}x${scroll.getChildAt(0).height}, sampleY=$sampleY, sampleX=$sampleStartX..$sampleEndX")
      })
    }
    val rawContrast = rowContrast(raw, sampleY, sampleStartX, sampleEndX)
    val blurredContrast = rowContrast(blurred, sampleY, sampleStartX, sampleEndX)
    val aboveRawContrast = rowContrast(raw, aboveBarY, sampleStartX, sampleEndX)
    val aboveBlurredContrast = rowContrast(blurred, aboveBarY, sampleStartX, sampleEndX)
    assertTrue("Expected visible source stripes: $rawContrast", rawContrast > 60)
    assertTrue("Backdrop must reduce stripe contrast: raw=$rawContrast blur=$blurredContrast", blurredContrast < rawContrast / 2)
    assertEquals("Scene above tab bar must remain sharp", aboveRawContrast, aboveBlurredContrast)
    val firstColor = rowColor(blurred, sampleY, sampleStartX, sampleEndX)
    onMain {
      tabs.setTabBarHidden(false)
      scroll.scrollBy(0, dp(160))
    }
    settle()
    val next = screenshot()
    val nextColor = rowColor(next, sampleY, sampleStartX, sampleEndX)
    assertTrue("Backdrop must track scrolling: $firstColor -> $nextColor",
      abs(firstColor.first - nextColor.first) + abs(firstColor.second - nextColor.second) > 8)
    raw.recycle()
    blurred.recycle()
    next.recycle()
  }

  fun testInsetsResizeAndSoftwareFallback() {
    onMain { tabs.setAndroidBottomBlurEnabled(true) }
    settle()
    onMain {
      val bar = navigationBar()
      val system = WindowInsetsCompat.Builder()
        .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(0, 0, 0, dp(24)))
        .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, dp(200)))
        .build()
      ViewCompat.dispatchApplyWindowInsets(tabs, system)
      bar.requestLayout()
    }
    settle()
    var systemHeight = 0
    onMain {
      val bar = navigationBar()
      systemHeight = barHeights.last()
      assertEquals(if (floatingStyle) 0 else dp(24), bar.paddingBottom)
      tabs.setIgnoreKeyboardInsets(false)
    }
    // Let the real window's requested insets finish before injecting the IME.
    settle()
    onMain {
      val bar = navigationBar()
      ViewCompat.dispatchApplyWindowInsets(tabs, WindowInsetsCompat.Builder()
        .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(0, 0, 0, dp(24)))
        .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, dp(200)))
        .build())
    }
    settle()
    onMain {
      assertTrue("IME must increase bar footprint: ${barHeights.last()} <= $systemHeight", barHeights.last() > systemHeight)
      tabs.setIgnoreBottomInsets(true)
      tabs.setIgnoreKeyboardInsets(true)
    }
    settle()
    onMain {
      val bar = navigationBar()
      ViewCompat.dispatchApplyWindowInsets(tabs, WindowInsetsCompat.Builder()
        .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(0, 0, 0, dp(24)))
        .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, dp(200)))
        .build())
      assertEquals(0, bar.paddingBottom)
      tabs.layoutParams = tabs.layoutParams.apply { height = dp(400) }
    }
    settle()
    onMain {
      assertEquals(dp(400), tabs.height)
      if (Build.VERSION.SDK_INT >= 31) assertEquals(tabs.height, tabs.layoutHolder.height)
      val detached = BottomTabsView(activity)
      if (floatingStyle) detached.setAndroidTabBarStyle("floating")
      detached.setAndroidBottomBlurEnabled(true)
      val spec = View.MeasureSpec.makeMeasureSpec(dp(300), View.MeasureSpec.EXACTLY)
      detached.measure(spec, spec)
      detached.layout(0, 0, dp(300), dp(300))
      assertEquals(floatingStyle, detached.layoutHolder.height == detached.height)
      assertNull(rendererOf(detached))
      detached.onDropViewInstance()
    }
  }

  fun testScrollFrameTimings() {
    if (Build.VERSION.SDK_INT < 31) return
    val thread = HandlerThread("BottomBlurFrameMetrics").apply { start() }
    try {
      // Repeat both modes after warm-up rather than comparing a cold and warm run.
      for (enabled in listOf(false, true, true, false)) {
        onMain { tabs.setAndroidBottomBlurEnabled(enabled) }
        settle()
        scrollFrames(30)
        val metrics = Collections.synchronizedList(mutableListOf<LongArray>())
        val listener = android.view.Window.OnFrameMetricsAvailableListener { _, frame, _ ->
          metrics.add(longArrayOf(
            frame.getMetric(FrameMetrics.TOTAL_DURATION),
            frame.getMetric(FrameMetrics.DRAW_DURATION),
            frame.getMetric(FrameMetrics.SYNC_DURATION),
            frame.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION),
            frame.getMetric(FrameMetrics.GPU_DURATION),
          ))
        }
        onMain { activity.window.addOnFrameMetricsAvailableListener(listener, Handler(thread.looper)) }
        val vsyncs = scrollFrames(240)
        onMain { activity.window.removeOnFrameMetricsAvailableListener(listener) }
        val samples = synchronized(metrics) { metrics.toList() }
        assertTrue("Insufficient rendered frames: ${samples.size}", samples.size >= 180)
        fun percentile(index: Int, fraction: Double): Double {
          val values = samples.map { it[index] }.sorted()
          return values[((values.size - 1) * fraction).toInt()] / 1_000_000.0
        }
        val report = JSONObject()
          .put("blur", enabled)
          .put("frames", samples.size)
          .put("totalP50Ms", percentile(0, 0.5))
          .put("totalP95Ms", percentile(0, 0.95))
          .put("uiDrawP95Ms", percentile(1, 0.95))
          .put("syncP95Ms", percentile(2, 0.95))
          .put("renderCommandP95Ms", percentile(3, 0.95))
          .put("gpuP95Ms", percentile(4, 0.95))
          .put("vsyncGapsOver25ms", vsyncs.zipWithNext().count { (a, b) -> b - a > 25_000_000 })
        instrumentation.sendStatus(0, Bundle().apply { putString("bottomBlurFrames", report.toString()) })
      }
    } finally {
      thread.quitSafely()
    }
  }

  fun testTabSwitchConfigurationAndTintRestoration() {
    if (Build.VERSION.SDK_INT < 31) return
    var firstBar: View? = null
    onMain {
      tabs.setAndroidBottomBlurEnabled(true)
      firstBar = navigationBar()
      tabs.dispatchConfigurationChanged(Configuration(tabs.resources.configuration).apply {
        uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
      })
    }
    settle()
    onMain {
      assertNotSame(firstBar, navigationBar())
      assertEquals(tabs.height, tabs.layoutHolder.height)
      tabs.addView(View(activity).apply { setBackgroundColor(Color.MAGENTA) }, 1, FrameLayout.LayoutParams(-1, -1))
      tabs.updateItems(mutableListOf(TabInfo("home", "Home"), TabInfo("other", "Other")))
      tabs.setSelectedItem("other")
      tabs.setBarTintColor(Color.DKGRAY)
      tabs.setRippleColor(ColorStateList.valueOf(Color.CYAN))
    }
    settle()
    onMain {
      assertEquals(View.INVISIBLE, tabs.layoutHolder.getChildAt(0).visibility)
      assertEquals(View.VISIBLE, tabs.layoutHolder.getChildAt(1).visibility)
      var pressed: String? = null
      var doublePressed: String? = null
      tabs.onTabSelectedListener = { pressed = it }
      tabs.onTabDoublePressedListener = { doublePressed = it }
      assertTrue(navigationBar().findViewById<View>(0).performClick())
      assertEquals("home", pressed)
      assertTrue(navigationBar().findViewById<View>(0).performClick())
      assertEquals("home", doublePressed)
      assertEquals(View.VISIBLE, tabs.layoutHolder.getChildAt(0).visibility)
      if (floatingStyle) tabs.setAndroidTabBarStyle("default")
    }
    settle()
    onMain {
      assertEquals(tabs.height, tabs.layoutHolder.height)
      assertEquals(Color.TRANSPARENT, navigationBar().backgroundTintList!!.defaultColor)
      if (floatingStyle) tabs.setAndroidTabBarStyle("floating")
    }
    settle()
    onMain {
      tabs.setAndroidBottomBlurEnabled(false)
    }
    settle()
    onMain {
      if (floatingStyle) {
        val background = field<View>("floatingBarBackground").background as android.graphics.drawable.GradientDrawable
        assertEquals(242, Color.alpha(background.color!!.defaultColor))
        tabs.setAndroidTabBarStyle("default")
      }
      val bar = navigationBar()
      assertEquals(Color.DKGRAY, bar.backgroundTintList!!.defaultColor)
      assertEquals(Color.CYAN, bar.itemRippleColor!!.defaultColor)
    }
    settle()
    onMain {
      assertTrue(tabs.layoutHolder.height < tabs.height)
    }
  }

  private fun scrollFrames(count: Int): List<Long> {
    val done = CountDownLatch(1)
    val vsyncs = mutableListOf<Long>()
    onMain {
      var frame = 0
      Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
          vsyncs.add(frameTimeNanos)
          scroll.scrollTo(0, dp(30 + frame * 3))
          frame++
          if (frame < count) Choreographer.getInstance().postFrameCallback(this) else done.countDown()
        }
      })
    }
    assertTrue("Scroll workload timed out", done.await(15, TimeUnit.SECONDS))
    settle()
    return vsyncs.toList()
  }

  private fun <T> field(name: String): T = tabs.javaClass.getDeclaredField(name).run {
    isAccessible = true
    @Suppress("UNCHECKED_CAST")
    get(tabs) as T
  }

  private fun navigationBar(): ExtendedBottomNavigationView = field("bottomNavigation")
  private fun backdropBar(): View = if (floatingStyle) field("floatingBar") else navigationBar()
  private fun blurRenderer(): Any? = rendererOf(tabs)
  private fun rendererOf(view: BottomTabsView): Any? = view.javaClass.getDeclaredField("bottomBarBlur").run {
    isAccessible = true
    get(view)
  }

  private fun screenshot(): Bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())

  private fun rowContrast(bitmap: Bitmap, y: Int, startX: Int = dp(8), endX: Int = bitmap.width - dp(8)): Int {
    val values = (startX until endX).map { Color.green(bitmap.getPixel(it, y)) }
    return values.max() - values.min()
  }

  private fun rowColor(bitmap: Bitmap, y: Int, startX: Int = dp(8), endX: Int = bitmap.width - dp(8)): Pair<Int, Int> {
    val values = (startX until endX).map { bitmap.getPixel(it, y) }
    return values.map { Color.red(it) }.average().toInt() to values.map { Color.blue(it) }.average().toInt()
  }

  private fun dp(value: Int) = (value * activity.resources.displayMetrics.density).toInt()
  private fun onMain(block: () -> Unit) {
    var failure: Throwable? = null
    instrumentation.runOnMainSync { try { block() } catch (error: Throwable) { failure = error } }
    failure?.let { throw it }
  }
  private fun settle() {
    instrumentation.waitForIdleSync()
    SystemClock.sleep(180)
    instrumentation.waitForIdleSync()
  }

  private class Stripes(context: android.content.Context) : View(context) {
    private val paint = Paint()
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (4000 * resources.displayMetrics.density).toInt())
    }

    override fun onDraw(canvas: Canvas) {
      val density = resources.displayMetrics.density
      val stripe = 4f * density
      var x = 0f
      while (x < width) {
        paint.color = if ((x / stripe).toInt() % 2 == 0) Color.WHITE else Color.BLACK
        canvas.drawRect(x, 0f, x + stripe, height.toFloat(), paint)
        x += stripe
      }
      var y = 0f
      while (y < height) {
        paint.color = if ((y / (160f * density)).toInt() % 2 == 0) 0x80ff0000.toInt() else 0x800000ff.toInt()
        canvas.drawRect(0f, y, width.toFloat(), y + 160f * density, paint)
        y += 160f * density
      }
    }
  }
}

class FloatingBottomBlurTest : BottomBlurTest() {
  override val floatingStyle = true
}
