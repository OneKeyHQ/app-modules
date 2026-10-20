package com.onekey.bottomtabs

import android.view.animation.Interpolator
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

// Geometry of the floating tab bar, modeled on the iOS 26 Liquid Glass tab bar (dp).
internal object FloatingTabBarMetrics {
  const val BAR_HEIGHT = 62f
  const val BAR_SIDE_MARGIN = 20f
  const val BAR_MIN_BOTTOM_GAP = 20f
  const val BAR_BOTTOM_EXTRA = 4f
  const val BAR_CONTENT_PADDING = 4f
  const val INDICATOR_INSET = 4f
  const val ICON_SIZE = 26f
  const val LABEL_SIZE_SP = 10f
  const val SHADOW_ELEVATION = 14f

  // Lens growth while a tab is pressed or dragged.
  const val LENS_SCALE_X = 1.14f
  const val LENS_SCALE_Y = 1.24f
  const val LENS_MAX_STRETCH = 0.16f
  const val ITEM_PRESSED_SCALE = 1.1f
  const val DRAG_EDGE_RESISTANCE = 0.22f
}

/**
 * Damped spring response normalized to [0, 1] input time. A damping ratio
 * below 1 overshoots slightly before settling, matching UIKit spring motion.
 */
internal class SpringInterpolator(
  private val dampingRatio: Float = 0.72f,
  private val angularFrequency: Float = 10f,
) : Interpolator {
  private val dampedFrequency =
    angularFrequency * sqrt(1f - dampingRatio * dampingRatio)

  override fun getInterpolation(input: Float): Float {
    if (input >= 1f) {
      return 1f
    }
    val decay = exp(-dampingRatio * angularFrequency * input)
    val phase = dampedFrequency * input
    return 1f - decay * (
      cos(phase) + (dampingRatio * angularFrequency / dampedFrequency) * sin(phase)
    )
  }
}
