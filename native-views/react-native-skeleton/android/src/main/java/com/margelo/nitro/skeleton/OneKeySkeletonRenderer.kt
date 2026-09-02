package com.margelo.nitro.skeleton

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.view.Choreographer
import java.lang.ref.WeakReference

public val ONEKEY_SKELETON_DEFAULT_COLORS: IntArray = intArrayOf(
  Color.rgb(210, 210, 210),
  Color.rgb(235, 235, 235),
)

/**
 * One process-wide frame clock for every active skeleton renderer. The clock
 * owns no Views and keeps listeners weak, avoiding one ValueAnimator per image.
 */
private object OneKeySkeletonClock : Choreographer.FrameCallback {
  interface Listener {
    fun onSkeletonFrame(frameTimeNanos: Long)
  }

  private val listeners = ArrayList<WeakReference<Listener>>()
  private var scheduled = false

  fun register(listener: Listener) {
    var index = listeners.size - 1
    while (index >= 0) {
      val current = listeners[index].get()
      if (current == null) {
        listeners.removeAt(index)
      } else if (current === listener) {
        return
      }
      index--
    }
    listeners.add(WeakReference(listener))
    if (!scheduled) {
      scheduled = true
      Choreographer.getInstance().postFrameCallback(this)
    }
  }

  fun unregister(listener: Listener) {
    var index = listeners.size - 1
    while (index >= 0) {
      val current = listeners[index].get()
      if (current == null || current === listener) listeners.removeAt(index)
      index--
    }
  }

  override fun doFrame(frameTimeNanos: Long) {
    scheduled = false
    if (listeners.isEmpty()) return
    var index = 0
    while (index < listeners.size) {
      val listener = listeners[index].get()
      if (listener == null) {
        listeners.removeAt(index)
      } else {
        listener.onSkeletonFrame(frameTimeNanos)
        index++
      }
    }
    if (listeners.isNotEmpty()) {
      scheduled = true
      Choreographer.getInstance().postFrameCallback(this)
    }
  }
}

/**
 * View-independent shimmer renderer shared by Skeleton and OneKeyImage.
 * Paint, Matrix and LinearGradient are retained and only rebuilt when bounds or
 * colors change; draw() performs no shader/paint/matrix allocation.
 */
public class OneKeySkeletonRenderer(
  private val invalidate: () -> Unit,
) {
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val shaderMatrix = Matrix()
  private val drawBounds = RectF()
  private var colors = ONEKEY_SKELETON_DEFAULT_COLORS.copyOf()
  private var shader: LinearGradient? = null
  private var shaderWidth = -1f
  private var durationNanos = 3_000_000_000L
  private var startedAtNanos = 0L
  private var phase = 0f
  private var running = false
  private val frameListener = object : OneKeySkeletonClock.Listener {
    override fun onSkeletonFrame(frameTimeNanos: Long) {
      handleFrame(frameTimeNanos)
    }
  }

  public fun updateStyle(colors: IntArray?, durationSeconds: Double?) {
    val nextColors = colors?.takeIf { it.size >= 2 }?.copyOfRange(0, 2)
      ?: ONEKEY_SKELETON_DEFAULT_COLORS.copyOf()
    val colorsChanged = !this.colors.contentEquals(nextColors)
    this.colors = nextColors
    durationNanos = ((durationSeconds ?: 3.0).coerceAtLeast(0.1) * 1_000_000_000L).toLong()
    if (colorsChanged) invalidateShader()
  }

  public fun updateBounds(width: Int, height: Int) {
    val nextWidth = width.coerceAtLeast(0).toFloat()
    val nextHeight = height.coerceAtLeast(0).toFloat()
    if (drawBounds.width() != nextWidth || drawBounds.height() != nextHeight) {
      drawBounds.set(0f, 0f, nextWidth, nextHeight)
      invalidateShader()
    }
  }

  public fun start() {
    if (running) return
    running = true
    startedAtNanos = 0L
    OneKeySkeletonClock.register(frameListener)
  }

  public fun stop() {
    if (!running) return
    running = false
    OneKeySkeletonClock.unregister(frameListener)
  }

  public fun draw(canvas: Canvas) {
    if (drawBounds.isEmpty) return
    ensureShader()
    val width = drawBounds.width()
    shaderMatrix.setTranslate(-width + (phase * width * 3f), 0f)
    shader?.setLocalMatrix(shaderMatrix)
    canvas.drawRect(drawBounds, paint)
  }

  private fun handleFrame(frameTimeNanos: Long) {
    if (!running) return
    if (startedAtNanos == 0L) startedAtNanos = frameTimeNanos
    phase = ((frameTimeNanos - startedAtNanos) % durationNanos).toFloat() / durationNanos.toFloat()
    invalidate()
  }

  private fun ensureShader() {
    val width = drawBounds.width()
    if (shader != null && shaderWidth == width) return
    shader = LinearGradient(
      0f,
      0f,
      width,
      0f,
      intArrayOf(colors[0], colors[1], colors[0]),
      SHADER_STOPS,
      Shader.TileMode.CLAMP,
    )
    shaderWidth = width
    paint.shader = shader
  }

  private fun invalidateShader() {
    shader = null
    shaderWidth = -1f
    paint.shader = null
  }

  private companion object {
    val SHADER_STOPS = floatArrayOf(0f, 0.5f, 1f)
  }
}
