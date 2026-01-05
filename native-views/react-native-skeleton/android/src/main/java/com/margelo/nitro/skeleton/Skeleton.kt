package com.margelo.nitro.skeleton

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.view.View
import android.view.animation.LinearInterpolator
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import androidx.core.graphics.toColorInt

// Animation constants
val DEFAULT_GRADIENT_COLORS = intArrayOf(
    Color.rgb(210, 210, 210),
    Color.rgb(235, 235, 235)
)

@DoNotStrip
class HybridSkeleton(val context: ThemedReactContext) : HybridSkeletonSpec() {

  // Shimmer animation
  private var shimmerAnimator: ValueAnimator? = null
  private var shimmerPaint: Paint = Paint()
  private var translateX: Float = 0f

  // Animation properties
  private var customGradientColors: IntArray? = null
  private var animationSpeed: Long = 3000L

  // Memory safety flags
  private var isDisposed: Boolean = false
  private var retryCount: Int = 0
  private val maxRetryCount: Int = 10
  private var pendingStartRunnable: Runnable? = null

  // View with shimmer effect
  override val view: View = object : View(context) {
    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)

      if (width > 0 && height > 0) {
        val colors = customGradientColors ?: DEFAULT_GRADIENT_COLORS
        val backgroundColor = colors[0]
        val highlightColor = colors[1]

        val gradient = LinearGradient(
          translateX - width,
          0f,
          translateX,
          0f,
          intArrayOf(backgroundColor, highlightColor, backgroundColor),
          floatArrayOf(0f, 0.5f, 1f),
          Shader.TileMode.CLAMP
        )

        shimmerPaint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), shimmerPaint)
      }
    }
  }

  override var shimmerSpeed: Double?
    get() = animationSpeed.toDouble() / 1000.0
    set(value) {
      if (isDisposed) return
      animationSpeed = ((value ?: 3.0) * 1000).toLong()
      restartShimmer()
    }

  override var shimmerGradientColors: Array<String>?
    get() = customGradientColors?.map { String.format("#%06X", 0xFFFFFF and it) }?.toTypedArray()
    set(value) {
      if (isDisposed) return
      if (value != null) {
        customGradientColors = value.map { hexStringToColor(it) }.toIntArray()
        restartShimmer()
      }
    }

  init {
    setupView()
  }

  private fun setupView() {
    view.post {
      startShimmer()
    }
  }

  private fun startShimmer() {
    if (isDisposed) return

    stopShimmer()
    retryCount = 0 // Reset retry count
    startShimmerInternal()
  }

  private fun startShimmerInternal() {
    if (isDisposed) return

    if (view.width == 0 || view.height == 0) {
      // Check if we have exceeded max retry count
      if (retryCount >= maxRetryCount) {
        android.util.Log.w("HybridSkeleton", "Max retry count reached. View bounds are still empty.")
        return
      }

      retryCount++
      // Clean up previous pending runnable
      pendingStartRunnable?.let { view.removeCallbacks(it) }

      // Create new runnable and schedule
      val runnable = Runnable {
        if (!isDisposed) {
          startShimmerInternal()
        }
      }
      pendingStartRunnable = runnable
      view.postDelayed(runnable, 100)
      return
    }

    val width = view.width.toFloat()

    shimmerAnimator = ValueAnimator.ofFloat(-width, width * 2).apply {
      duration = animationSpeed
      repeatCount = ValueAnimator.INFINITE
      interpolator = LinearInterpolator()
      addUpdateListener { animation ->
        if (!isDisposed) {
          translateX = animation.animatedValue as Float
          view.invalidate()
        }
      }
      start()
    }
  }

  private fun stopShimmer() {
    // Clean up pending runnable
    pendingStartRunnable?.let {
      view.removeCallbacks(it)
      pendingStartRunnable = null
    }

    // Clean up animator and its listeners
    shimmerAnimator?.apply {
      removeAllUpdateListeners()
      cancel()
    }
    shimmerAnimator = null
  }

  override fun afterUpdate() {
    super.afterUpdate()
    if (!isDisposed) {
      restartShimmer()
    }
  }

  private fun restartShimmer() {
    if (isDisposed) return
    stopShimmer()
    retryCount = 0 // Reset retry count on restart
    startShimmerInternal()
  }

  private fun hexStringToColor(hexColor: String): Int {
    var hexSanitized = hexColor.trim()

    if (hexSanitized.startsWith("#")) {
      hexSanitized = hexSanitized.substring(1)
    }

    return try {
      Color.parseColor("#$hexSanitized")
    } catch (e: Exception) {
      DEFAULT_GRADIENT_COLORS[0]
    }
  }

  override fun dispose() {
    // Mark as disposed to prevent any new operations
    isDisposed = true

    // Stop all animations and clean up
    stopShimmer()

    // Clear view references to prevent memory leaks
    view.invalidate()
  }
}
