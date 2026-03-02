package com.margelo.nitro.reactnativesplashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

@SuppressLint("ViewConstructor")
internal class SplashScreenView(context: Context) : RelativeLayout(context) {

    val imageView: ImageView = ImageView(context).also { iv ->
        iv.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(iv)
    }

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun configureResizeMode(resizeMode: SplashImageResizeMode) {
        imageView.scaleType = resizeMode.scaleType
        when (resizeMode) {
            SplashImageResizeMode.CONTAIN -> imageView.adjustViewBounds = true
            else -> imageView.adjustViewBounds = false
        }
    }
}
