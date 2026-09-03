package com.margelo.nitro.skeleton

import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

private class SkeletonHostView(context: ThemedReactContext) : View(context) {
  private val renderer = OneKeySkeletonRenderer { postInvalidateOnAnimation() }
  var disposed = false
  private var aggregatedVisible = true

  fun updateRenderer(colors: IntArray?, durationSeconds: Double) {
    setBackgroundColor(colors?.firstOrNull() ?: ONEKEY_SKELETON_DEFAULT_COLORS[0])
    renderer.updateStyle(colors, durationSeconds)
    renderer.updateBounds(width, height)
    syncAnimationState()
  }

  fun stopRenderer() {
    renderer.stop()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    renderer.updateBounds(w, h)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    renderer.draw(canvas)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    syncAnimationState()
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    syncAnimationState()
  }

  override fun onVisibilityAggregated(isVisible: Boolean) {
    super.onVisibilityAggregated(isVisible)
    aggregatedVisible = isVisible
    syncAnimationState()
  }

  private fun syncAnimationState() {
    if (!disposed && isAttachedToWindow && aggregatedVisible) renderer.start() else renderer.stop()
  }
}

/** Thin Nitro adapter around the reusable view-independent renderer. */
@DoNotStrip
class HybridSkeleton(context: ThemedReactContext) : HybridSkeletonSpec() {
  private val hostView = SkeletonHostView(context)
  private var colors: IntArray? = null
  private var durationSeconds = 3.0

  override val view: View = hostView

  override var shimmerSpeed: Double?
    get() = durationSeconds
    set(value) {
      if (hostView.disposed) return
      durationSeconds = (value ?: 3.0).coerceAtLeast(0.1)
      updateRenderer()
    }

  override var shimmerGradientColors: Array<String>?
    get() = colors?.map { String.format("#%06X", 0xFFFFFF and it) }?.toTypedArray()
    set(value) {
      if (hostView.disposed) return
      colors = value?.takeIf { it.size >= 2 }?.take(2)?.map(::parseColor)?.toIntArray()
      updateRenderer()
    }

  override fun afterUpdate() {
    super.afterUpdate()
    if (!hostView.disposed) updateRenderer()
  }

  override fun dispose() {
    hostView.disposed = true
    hostView.stopRenderer()
    super.dispose()
  }

  private fun updateRenderer() {
    hostView.updateRenderer(colors, durationSeconds)
  }

  private fun parseColor(value: String): Int = try {
    Color.parseColor(value)
  } catch (_: IllegalArgumentException) {
    ONEKEY_SKELETON_DEFAULT_COLORS[0]
  }
}
