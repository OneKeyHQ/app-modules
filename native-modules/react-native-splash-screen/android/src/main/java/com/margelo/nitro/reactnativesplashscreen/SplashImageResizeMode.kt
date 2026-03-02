package com.margelo.nitro.reactnativesplashscreen

import android.widget.ImageView

internal enum class SplashImageResizeMode(
    val scaleType: ImageView.ScaleType,
    val value: String
) {
    CONTAIN(ImageView.ScaleType.FIT_CENTER, "contain"),
    COVER(ImageView.ScaleType.CENTER_CROP, "cover"),
    NATIVE(ImageView.ScaleType.CENTER, "native");

    companion object {
        fun fromString(value: String?): SplashImageResizeMode {
            if (value != null) {
                for (mode in values()) {
                    if (mode.value == value) return mode
                }
            }
            return CONTAIN
        }
    }
}
