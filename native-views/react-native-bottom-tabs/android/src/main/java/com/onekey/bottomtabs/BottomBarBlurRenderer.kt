package com.onekey.bottomtabs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import androidx.annotation.RequiresApi
import kotlin.math.ceil

/** A GPU-only backdrop confined to the tab bar bounds. The source callback must draw scenes, never this overlay. */
@RequiresApi(31)
internal class BottomBarBlurRenderer(private val density: Float) {
  private val node = RenderNode("TabBarBackdrop")
  private val tintPaint = Paint()
  private val clipPath = Path()
  private var lastLeft = 0
  private var lastTop = -1
  private var lastRight = 0
  private var lastBottom = 0
  private var lastCornerRadius = -1f
  private var lastTint = Color.TRANSPARENT

  init {
    // X's frosted bars blur at 32 dp (the Haze default of 20 dp is overridden);
    // the wider radius turns the scene behind the bar into an even haze instead
    // of readable shapes, which is what made the capsule read as a gray slab.
    node.setRenderEffect(RenderEffect.createBlurEffect(
      BLUR_RADIUS_DP * density / SCALE,
      BLUR_RADIUS_DP * density / SCALE,
      Shader.TileMode.CLAMP,
    ))
  }

  fun draw(
    canvas: Canvas,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int,
    cornerRadius: Float,
    tint: Int,
    drawScenes: (Canvas) -> Unit,
  ) {
    val width = right - left
    val height = bottom - top
    if (!canvas.isHardwareAccelerated || width <= 0 || height <= 0) return

    if (
      left != lastLeft ||
      top != lastTop ||
      right != lastRight ||
      bottom != lastBottom ||
      cornerRadius != lastCornerRadius ||
      tint != lastTint
    ) {
      node.setPosition(0, 0, ceil(width / SCALE).toInt(), ceil(height / SCALE).toInt())
      clipPath.reset()
      if (cornerRadius > 0f) {
        clipPath.addRoundRect(
          left.toFloat(),
          top.toFloat(),
          right.toFloat(),
          bottom.toFloat(),
          cornerRadius,
          cornerRadius,
          Path.Direction.CW,
        )
      }
      val surface = (tint and 0x00ffffff) or (minOf(Color.alpha(tint), MAX_TINT_ALPHA) shl 24)
      tintPaint.color = surface
      lastLeft = left
      lastTop = top
      lastRight = right
      lastBottom = bottom
      lastCornerRadius = cornerRadius
      lastTint = tint
    }

    val recording = node.beginRecording()
    try {
      // Reuse child View display lists. Only sample the scene region directly behind the bar.
      recording.scale(1f / SCALE, 1f / SCALE)
      recording.translate(-left.toFloat(), -top.toFloat())
      recording.drawColor(tint or (0xff shl 24))
      drawScenes(recording)
    } finally {
      node.endRecording()
    }

    val saveCount = canvas.save()
    if (cornerRadius > 0f) {
      canvas.clipPath(clipPath)
    } else {
      canvas.clipRect(left, top, right, bottom)
    }
    val transform = canvas.save()
    canvas.translate(left.toFloat(), top.toFloat())
    canvas.scale(SCALE, SCALE)
    canvas.drawRenderNode(node)
    canvas.restoreToCount(transform)
    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), tintPaint)
    canvas.restoreToCount(saveCount)
  }

  fun release() {
    node.discardDisplayList()
  }

  private companion object {
    const val SCALE = 4f

    // X blurs its glass bars at 32 dp, wider than the previous 20 dp.
    const val BLUR_RADIUS_DP = 32f

    // Ceiling for the surface tint: X keeps the tint at 0.75 alpha over the
    // blur (255 * 0.75 = 191) instead of stacking an opaque surface on top.
    const val MAX_TINT_ALPHA = 191
  }
}
