package com.onekey.bottomtabs

import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import androidx.annotation.RequiresApi
import kotlin.math.ceil
import kotlin.math.max

/** A GPU-only backdrop. The source callback must draw scenes, never this overlay. */
@RequiresApi(31)
internal class BottomBarBlurRenderer(private val density: Float) {
  private val node = RenderNode("TabBarBackdrop")
  private val tintPaint = Paint()
  private val maskPaint = Paint().apply { blendMode = BlendMode.DST_IN }
  private var lastWidth = 0
  private var lastBarTop = -1
  private var lastBottom = 0
  private var lastTint = Color.TRANSPARENT
  private var fadeTop = 0f
  private var sampleTop = 0f

  init {
    node.setRenderEffect(RenderEffect.createBlurEffect(
      20f * density / SCALE,
      20f * density / SCALE,
      Shader.TileMode.CLAMP,
    ))
  }

  fun draw(
    canvas: Canvas,
    width: Int,
    barTop: Int,
    bottom: Int,
    tint: Int,
    drawScenes: (Canvas) -> Unit,
  ) {
    if (!canvas.isHardwareAccelerated || width <= 0 || bottom <= barTop) return

    if (width != lastWidth || barTop != lastBarTop || bottom != lastBottom || tint != lastTint) {
      fadeTop = max(0f, barTop - 24f * density)
      sampleTop = max(0f, fadeTop - 40f * density)
      node.setPosition(0, 0, ceil(width / SCALE).toInt(), ceil((bottom - sampleTop) / SCALE).toInt())
      maskPaint.shader = LinearGradient(
        0f, fadeTop, 0f, barTop.toFloat(),
        Color.TRANSPARENT, Color.WHITE, Shader.TileMode.CLAMP,
      )
      val surface = (tint and 0x00ffffff) or (minOf(Color.alpha(tint), 199) shl 24)
      tintPaint.color = surface
      lastWidth = width
      lastBarTop = barTop
      lastBottom = bottom
      lastTint = tint
    }

    val recording = node.beginRecording()
    try {
      // Reuse child View display lists. No bitmap capture or full-screen blur.
      recording.scale(1f / SCALE, 1f / SCALE)
      recording.translate(0f, -sampleTop)
      recording.drawColor(tint or (0xff shl 24))
      drawScenes(recording)
    } finally {
      node.endRecording()
    }

    val layer = canvas.saveLayer(0f, fadeTop, width.toFloat(), bottom.toFloat(), null)
    val transform = canvas.save()
    canvas.translate(0f, sampleTop)
    canvas.scale(SCALE, SCALE)
    canvas.drawRenderNode(node)
    canvas.restoreToCount(transform)
    canvas.drawRect(0f, fadeTop, width.toFloat(), bottom.toFloat(), tintPaint)
    // Fade the combined blur and tint, keeping the transition free of a hard seam.
    canvas.drawRect(0f, fadeTop, width.toFloat(), bottom.toFloat(), maskPaint)
    canvas.restoreToCount(layer)
  }

  fun release() {
    node.discardDisplayList()
  }

  private companion object {
    const val SCALE = 4f
  }
}
