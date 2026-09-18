package com.margelo.nitro.reactnativeimagecroppicker

import android.content.Intent
import android.graphics.Color

// Resolved colors, fonts and scale of the cropper screen. The layout matches
// the iOS ImageCropperViewController. Colors are ARGB ints.
internal data class ImageCropperTheme(
  val isDark: Boolean,
  val backgroundColor: Int,
  val titleColor: Int,
  val iconColor: Int,
  val cancelButtonColor: Int,
  val cancelButtonPressedColor: Int,
  val cancelButtonTextColor: Int,
  val confirmButtonColor: Int,
  val confirmButtonPressedColor: Int,
  val confirmButtonTextColor: Int,
  val titleFontFamily: String?,
  val buttonFontFamily: String?,
  val scale: Float,
) {
  fun writeTo(intent: Intent) {
    intent.putExtra(EXTRA_IS_DARK, isDark)
      .putExtra(EXTRA_BACKGROUND_COLOR, backgroundColor)
      .putExtra(EXTRA_TITLE_COLOR, titleColor)
      .putExtra(EXTRA_ICON_COLOR, iconColor)
      .putExtra(EXTRA_CANCEL_BUTTON_COLOR, cancelButtonColor)
      .putExtra(EXTRA_CANCEL_BUTTON_PRESSED_COLOR, cancelButtonPressedColor)
      .putExtra(EXTRA_CANCEL_BUTTON_TEXT_COLOR, cancelButtonTextColor)
      .putExtra(EXTRA_CONFIRM_BUTTON_COLOR, confirmButtonColor)
      .putExtra(EXTRA_CONFIRM_BUTTON_PRESSED_COLOR, confirmButtonPressedColor)
      .putExtra(EXTRA_CONFIRM_BUTTON_TEXT_COLOR, confirmButtonTextColor)
      .putExtra(EXTRA_TITLE_FONT_FAMILY, titleFontFamily)
      .putExtra(EXTRA_BUTTON_FONT_FAMILY, buttonFontFamily)
      .putExtra(EXTRA_SCALE, scale)
  }

  // OneKey's tokens: $bgApp, $text, $icon, $bgStrong, $bgStrongActive,
  // $bgPrimary, $bgPrimaryActive and $textInverse.
  private class Palette(
    val background: String,
    val title: String,
    val icon: String,
    val cancelButton: String,
    val cancelButtonPressed: String,
    val cancelButtonText: String,
    val confirmButton: String,
    val confirmButtonPressed: String,
    val confirmButtonText: String,
  )

  companion object {
    private const val PREFIX = "com.margelo.nitro.reactnativeimagecroppicker.theme."
    private const val EXTRA_IS_DARK = "${PREFIX}isDark"
    private const val EXTRA_BACKGROUND_COLOR = "${PREFIX}backgroundColor"
    private const val EXTRA_TITLE_COLOR = "${PREFIX}titleColor"
    private const val EXTRA_ICON_COLOR = "${PREFIX}iconColor"
    private const val EXTRA_CANCEL_BUTTON_COLOR = "${PREFIX}cancelButtonColor"
    private const val EXTRA_CANCEL_BUTTON_PRESSED_COLOR = "${PREFIX}cancelButtonPressedColor"
    private const val EXTRA_CANCEL_BUTTON_TEXT_COLOR = "${PREFIX}cancelButtonTextColor"
    private const val EXTRA_CONFIRM_BUTTON_COLOR = "${PREFIX}confirmButtonColor"
    private const val EXTRA_CONFIRM_BUTTON_PRESSED_COLOR = "${PREFIX}confirmButtonPressedColor"
    private const val EXTRA_CONFIRM_BUTTON_TEXT_COLOR = "${PREFIX}confirmButtonTextColor"
    private const val EXTRA_TITLE_FONT_FAMILY = "${PREFIX}titleFontFamily"
    private const val EXTRA_BUTTON_FONT_FAMILY = "${PREFIX}buttonFontFamily"
    private const val EXTRA_SCALE = "${PREFIX}scale"

    private val lightPalette = Palette(
      background = "#FFFFFF",
      title = "#000000DF",
      icon = "#0000009B",
      cancelButton = "#0000000F",
      cancelButtonPressed = "#0000001F",
      cancelButtonText = "#000000DF",
      confirmButton = "#000000DF",
      confirmButtonPressed = "#0000009B",
      confirmButtonText = "#FFFFFFED",
    )

    private val darkPalette = Palette(
      background = "#0F0F0F",
      title = "#FFFFFFED",
      icon = "#FFFFFFAF",
      cancelButton = "#FFFFFF12",
      cancelButtonPressed = "#FFFFFF22",
      cancelButtonText = "#FFFFFFED",
      confirmButton = "#FFFFFFED",
      confirmButtonPressed = "#FFFFFF72",
      confirmButtonText = "#000000DF",
    )

    fun resolve(appearance: ImageCropperAppearance?, systemIsDark: Boolean): ImageCropperTheme {
      val isDark = when (appearance?.colorScheme) {
        ImageCropperColorScheme.DARK -> true
        ImageCropperColorScheme.LIGHT -> false
        null -> systemIsDark
      }
      val palette = if (isDark) darkPalette else lightPalette
      fun color(value: String?, fallback: String): Int =
        parseCssColor(value) ?: parseCssColor(fallback) ?: Color.TRANSPARENT

      val scale = appearance?.scale?.toFloat()?.takeIf { it.isFinite() && it > 0f } ?: 1f
      return ImageCropperTheme(
        isDark = isDark,
        backgroundColor = color(appearance?.backgroundColor, palette.background),
        titleColor = color(appearance?.titleColor, palette.title),
        iconColor = color(appearance?.iconColor, palette.icon),
        cancelButtonColor = color(appearance?.cancelButtonColor, palette.cancelButton),
        cancelButtonPressedColor = color(
          appearance?.cancelButtonPressedColor,
          palette.cancelButtonPressed,
        ),
        cancelButtonTextColor = color(appearance?.cancelButtonTextColor, palette.cancelButtonText),
        confirmButtonColor = color(appearance?.confirmButtonColor, palette.confirmButton),
        confirmButtonPressedColor = color(
          appearance?.confirmButtonPressedColor,
          palette.confirmButtonPressed,
        ),
        confirmButtonTextColor = color(
          appearance?.confirmButtonTextColor,
          palette.confirmButtonText,
        ),
        titleFontFamily = appearance?.titleFontFamily?.takeIf { it.isNotEmpty() },
        buttonFontFamily = appearance?.buttonFontFamily?.takeIf { it.isNotEmpty() },
        scale = scale,
      )
    }

    fun readFrom(intent: Intent): ImageCropperTheme {
      val fallback = resolve(null, systemIsDark = intent.getBooleanExtra(EXTRA_IS_DARK, false))
      return ImageCropperTheme(
        isDark = fallback.isDark,
        backgroundColor = intent.getIntExtra(EXTRA_BACKGROUND_COLOR, fallback.backgroundColor),
        titleColor = intent.getIntExtra(EXTRA_TITLE_COLOR, fallback.titleColor),
        iconColor = intent.getIntExtra(EXTRA_ICON_COLOR, fallback.iconColor),
        cancelButtonColor = intent.getIntExtra(EXTRA_CANCEL_BUTTON_COLOR, fallback.cancelButtonColor),
        cancelButtonPressedColor = intent.getIntExtra(
          EXTRA_CANCEL_BUTTON_PRESSED_COLOR,
          fallback.cancelButtonPressedColor,
        ),
        cancelButtonTextColor = intent.getIntExtra(
          EXTRA_CANCEL_BUTTON_TEXT_COLOR,
          fallback.cancelButtonTextColor,
        ),
        confirmButtonColor = intent.getIntExtra(EXTRA_CONFIRM_BUTTON_COLOR, fallback.confirmButtonColor),
        confirmButtonPressedColor = intent.getIntExtra(
          EXTRA_CONFIRM_BUTTON_PRESSED_COLOR,
          fallback.confirmButtonPressedColor,
        ),
        confirmButtonTextColor = intent.getIntExtra(
          EXTRA_CONFIRM_BUTTON_TEXT_COLOR,
          fallback.confirmButtonTextColor,
        ),
        titleFontFamily = intent.getStringExtra(EXTRA_TITLE_FONT_FAMILY),
        buttonFontFamily = intent.getStringExtra(EXTRA_BUTTON_FONT_FAMILY),
        scale = intent.getFloatExtra(EXTRA_SCALE, fallback.scale),
      )
    }

    // Parses CSS hex colors: #RGB, #RRGGBB and #RRGGBBAA. Color.parseColor
    // reads 8 digits as #AARRGGBB, so it cannot be used here.
    fun parseCssColor(value: String?): Int? {
      var hex = value?.trim()?.removePrefix("#") ?: return null
      if (hex.length == 3) {
        hex = hex.map { "$it$it" }.joinToString("")
      }
      if (hex.length != 6 && hex.length != 8) {
        return null
      }
      val rgba = hex.toLongOrNull(16) ?: return null
      return if (hex.length == 8) {
        Color.argb(
          (rgba and 0xff).toInt(),
          ((rgba shr 24) and 0xff).toInt(),
          ((rgba shr 16) and 0xff).toInt(),
          ((rgba shr 8) and 0xff).toInt(),
        )
      } else {
        Color.rgb(
          ((rgba shr 16) and 0xff).toInt(),
          ((rgba shr 8) and 0xff).toInt(),
          (rgba and 0xff).toInt(),
        )
      }
    }
  }
}
