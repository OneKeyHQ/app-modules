package com.margelo.nitro.reactnativeimagecroppicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.react.common.assets.ReactFontManager
import com.yalantis.ucrop.callback.BitmapCropCallback
import com.yalantis.ucrop.view.CropImageView
import com.yalantis.ucrop.view.GestureCropImageView
import com.yalantis.ucrop.view.OverlayView
import com.yalantis.ucrop.view.TransformImageView
import com.yalantis.ucrop.view.UCropView
import kotlin.math.roundToInt

// Full-screen cropper laid out like a OneKey page: a header with the title and
// a rotate button, the crop area, and a Cancel / Confirm footer. It draws the
// same screen, with the same metrics, as the iOS ImageCropperViewController.
class ImageCropperActivity : AppCompatActivity() {
  private lateinit var cropperTheme: ImageCropperTheme
  private lateinit var cropView: UCropView
  private lateinit var cropImageView: GestureCropImageView
  private lateinit var overlayView: OverlayView
  private lateinit var rotateButton: IconButton
  private lateinit var confirmButton: CapsuleButton

  private var showsGrid = false
  private var gridAlpha = 0f
  private var gridAnimator: ValueAnimator? = null
  private var rotationAnimator: ValueAnimator? = null
  private var isImageLoaded = false
  private var isProcessing = false
  private val hideGrid = Runnable { animateGrid(visible = false) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    cropperTheme = ImageCropperTheme.readFrom(intent)
    val barStyle = if (cropperTheme.isDark) {
      SystemBarStyle.dark(Color.TRANSPARENT)
    } else {
      SystemBarStyle.light(Color.TRANSPARENT, DARK_SCRIM)
    }
    enableEdgeToEdge(barStyle, barStyle)
    requestedOrientation = intent.getIntExtra(
      EXTRA_REQUESTED_ORIENTATION,
      ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
    )

    val inputUri = intent.uriExtra(EXTRA_INPUT_URI)
    val outputUri = intent.uriExtra(EXTRA_OUTPUT_URI)
    if (inputUri == null || outputUri == null) {
      finishWithError("Missing image")
      return
    }

    window.setBackgroundDrawable(ColorDrawable(cropperTheme.backgroundColor))
    setContentView(buildContent())
    configureCropView()

    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = cancel()
      },
    )

    cropView.alpha = 0f
    cropImageView.setTransformImageListener(
      object : TransformImageView.TransformImageListener {
        override fun onLoadComplete() {
          isImageLoaded = true
          cropView.animate().alpha(1f).setDuration(FADE_IN_DURATION).start()
        }

        override fun onLoadFailure(error: Exception) {
          finishWithError(error.message ?: "Cannot load image")
        }

        override fun onRotate(currentAngle: Float) = Unit

        override fun onScale(currentScale: Float) = Unit
      },
    )
    try {
      cropImageView.setImageUri(inputUri, outputUri)
    } catch (error: Exception) {
      finishWithError(error.message ?: "Cannot load image")
    }
  }

  override fun onStop() {
    super.onStop()
    if (::cropImageView.isInitialized) {
      cropImageView.cancelAllAnimations()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    gridAnimator?.cancel()
    rotationAnimator?.cancel()
    if (::cropView.isInitialized) {
      cropView.removeCallbacks(hideGrid)
    }
  }

  private fun buildContent(): View {
    val theme = cropperTheme
    val spacing = dp(20f)
    val headerHeight = dp(56f)
    val iconButtonSize = dp(40f)
    val buttonHeight = dp(50f)

    val column = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      setBackgroundColor(theme.backgroundColor)
    }

    val header = FrameLayout(this)
    val title = intent.getStringExtra(EXTRA_TITLE)
    if (!title.isNullOrEmpty()) {
      val titleView = TextView(this).apply {
        text = title
        setTextColor(theme.titleColor)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, (18 * theme.scale).roundToInt().toFloat())
        typeface = loadTypeface(theme.titleFontFamily, fallbackWeight = 600)
        gravity = Gravity.CENTER
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        includeFontPadding = false
        ViewCompat.setAccessibilityHeading(this, true)
      }
      // Centered, clear of the rotate button on both sides.
      header.addView(
        titleView,
        FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        ).apply {
          marginStart = spacing + iconButtonSize
          marginEnd = spacing + iconButtonSize
        },
      )
    }
    rotateButton = IconButton(
      this,
      iconSize = dp(24f),
      iconColor = theme.iconColor,
      pressedColor = theme.cancelButtonColor,
    ).apply {
      contentDescription = "Rotate"
      visibility = if (intent.getBooleanExtra(EXTRA_ROTATE_BUTTON_HIDDEN, false)) {
        View.GONE
      } else {
        View.VISIBLE
      }
      setOnClickListener { rotate() }
    }
    header.addView(
      rotateButton,
      FrameLayout.LayoutParams(iconButtonSize, iconButtonSize, Gravity.END or Gravity.CENTER_VERTICAL)
        .apply { marginEnd = spacing - dp(8f) },
    )
    column.addView(
      header,
      LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight),
    )

    cropView = UCropView(this, null)
    cropImageView = cropView.cropImageView
    overlayView = cropView.overlayView
    // The crop box keeps `spacing` from every edge of the crop area.
    cropImageView.setPadding(spacing, spacing, spacing, spacing)
    overlayView.setPadding(spacing, spacing, spacing, spacing)
    val cropContainer = CropContainer(this).apply { addView(cropView) }
    column.addView(
      cropContainer,
      LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f),
    )

    val footer = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
    val buttonPadding = dp(20f) + dp(1f)
    val buttonTextSize = (16 * theme.scale).roundToInt().toFloat()
    val buttonTypeface = loadTypeface(theme.buttonFontFamily, fallbackWeight = 500)
    val cancelButton = CapsuleButton(
      this,
      title = intent.getStringExtra(EXTRA_CANCEL_TEXT)?.takeIf { it.isNotEmpty() }
        ?: getString(android.R.string.cancel),
      textSize = buttonTextSize,
      typeface = buttonTypeface,
      textColor = theme.cancelButtonTextColor,
      backgroundColor = theme.cancelButtonColor,
      pressedColor = theme.cancelButtonPressedColor,
      horizontalPadding = buttonPadding,
      spinnerSize = dp(20f),
      spinnerSpacing = dp(8f),
    ).apply { setOnClickListener { cancel() } }
    confirmButton = CapsuleButton(
      this,
      title = intent.getStringExtra(EXTRA_CONFIRM_TEXT)?.takeIf { it.isNotEmpty() }
        ?: getString(android.R.string.ok),
      textSize = buttonTextSize,
      typeface = buttonTypeface,
      textColor = theme.confirmButtonTextColor,
      backgroundColor = theme.confirmButtonColor,
      pressedColor = theme.confirmButtonPressedColor,
      horizontalPadding = buttonPadding,
      spinnerSize = dp(20f),
      spinnerSpacing = dp(8f),
    ).apply { setOnClickListener { confirm() } }
    footer.addView(cancelButton, LinearLayout.LayoutParams(0, buttonHeight, 1f))
    footer.addView(
      confirmButton,
      LinearLayout.LayoutParams(0, buttonHeight, 1f).apply { marginStart = dp(10f) },
    )
    column.addView(
      footer,
      LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
      ),
    )

    val navigationBarReduction = (10 * resources.displayMetrics.density).roundToInt()
    ViewCompat.setOnApplyWindowInsetsListener(column) { _, windowInsets ->
      val insets = windowInsets.getInsets(
        WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
      )
      column.setPadding(insets.left, 0, insets.right, 0)
      header.setPadding(0, insets.top, 0, 0)
      header.layoutParams = header.layoutParams.apply { height = headerHeight + insets.top }
      // Same as OneKey's page footer: 20dp of padding, plus the navigation bar
      // inset less 10dp.
      val bottomInset = if (insets.bottom > navigationBarReduction) {
        insets.bottom - navigationBarReduction
      } else {
        insets.bottom
      }
      footer.setPadding(spacing, 0, spacing, spacing + bottomInset)
      WindowInsetsCompat.CONSUMED
    }
    return column
  }

  private fun configureCropView() {
    val isCircular = intent.getBooleanExtra(EXTRA_CIRCLE_OVERLAY, false)
    val isFreeStyle = intent.getBooleanExtra(EXTRA_FREE_STYLE, false) && !isCircular
    val aspectRatioX = intent.getFloatExtra(EXTRA_ASPECT_RATIO_X, 0f)
    val aspectRatioY = intent.getFloatExtra(EXTRA_ASPECT_RATIO_Y, 0f)

    cropImageView.isRotateEnabled = false
    cropImageView.isScaleEnabled = true
    cropImageView.targetAspectRatio = when {
      isCircular -> 1f
      aspectRatioX > 0f && aspectRatioY > 0f -> aspectRatioX / aspectRatioY
      else -> CropImageView.SOURCE_IMAGE_ASPECT_RATIO
    }

    val density = resources.displayMetrics.density
    overlayView.freestyleCropMode = if (isFreeStyle) {
      OverlayView.FREESTYLE_CROP_MODE_ENABLE
    } else {
      OverlayView.FREESTYLE_CROP_MODE_DISABLE
    }
    overlayView.setCircleDimmedLayer(isCircular)
    overlayView.setDimmedColor(withAlpha(cropperTheme.backgroundColor, DIMMED_ALPHA))
    overlayView.setShowCropFrame(!isCircular)
    overlayView.setCropFrameColor(cropperTheme.titleColor)
    overlayView.setCropGridCornerColor(cropperTheme.titleColor)
    overlayView.setCropFrameStrokeWidth(density.roundToInt().coerceAtLeast(1))
    // A hairline grid that only shows while the image is moved, like iOS.
    showsGrid = intent.getBooleanExtra(EXTRA_SHOW_GRID, true) && !isCircular
    overlayView.setShowCropGrid(showsGrid)
    overlayView.setCropGridStrokeWidth(1)
    overlayView.setCropGridColor(withAlpha(GRID_COLOR, 0f))
  }

  private fun onCropTouchStart() {
    cropView.removeCallbacks(hideGrid)
    animateGrid(visible = true)
  }

  private fun onCropTouchEnd() {
    cropView.removeCallbacks(hideGrid)
    cropView.postDelayed(hideGrid, GRID_HIDE_DELAY)
  }

  private fun animateGrid(visible: Boolean) {
    if (!showsGrid) {
      return
    }
    val target = if (visible) 1f else 0f
    gridAnimator?.cancel()
    if (gridAlpha == target) {
      return
    }
    gridAnimator = ValueAnimator.ofFloat(gridAlpha, target).apply {
      duration = if (visible) GRID_FADE_IN_DURATION else GRID_FADE_OUT_DURATION
      addUpdateListener { animator ->
        gridAlpha = animator.animatedValue as Float
        overlayView.setCropGridColor(withAlpha(GRID_COLOR, gridAlpha))
        overlayView.invalidate()
      }
      start()
    }
  }

  private fun rotate() {
    if (!isImageLoaded || isProcessing || rotationAnimator != null) {
      return
    }
    cropImageView.cancelAllAnimations()
    var appliedAngle = 0f
    rotationAnimator = ValueAnimator.ofFloat(0f, -90f).apply {
      duration = ROTATION_DURATION
      interpolator = DecelerateInterpolator()
      addUpdateListener { animator ->
        val angle = animator.animatedValue as Float
        cropImageView.postRotate(angle - appliedAngle)
        appliedAngle = angle
      }
      addListener(
        object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            rotationAnimator = null
            cropImageView.setImageToWrapCropBounds()
          }
        },
      )
      start()
    }
  }

  private fun cancel() {
    if (isProcessing) {
      return
    }
    setResult(RESULT_CANCELED)
    finish()
  }

  private fun confirm() {
    if (!isImageLoaded || isProcessing || rotationAnimator != null) {
      return
    }
    isProcessing = true
    confirmButton.isLoading = true
    rotateButton.alpha = DISABLED_ALPHA
    cropImageView.cropAndSaveImage(
      Bitmap.CompressFormat.JPEG,
      100,
      object : BitmapCropCallback {
        override fun onBitmapCropped(
          resultUri: Uri,
          offsetX: Int,
          offsetY: Int,
          imageWidth: Int,
          imageHeight: Int,
        ) {
          setResult(
            RESULT_OK,
            Intent()
              .putExtra(EXTRA_OUTPUT_URI, resultUri)
              .putExtra(EXTRA_OUTPUT_OFFSET_X, offsetX)
              .putExtra(EXTRA_OUTPUT_OFFSET_Y, offsetY)
              .putExtra(EXTRA_OUTPUT_WIDTH, imageWidth)
              .putExtra(EXTRA_OUTPUT_HEIGHT, imageHeight),
          )
          finish()
        }

        override fun onCropFailure(error: Throwable) {
          finishWithError(error.message ?: "Cannot crop image")
        }
      },
    )
  }

  private fun finishWithError(message: String) {
    setResult(RESULT_ERROR, Intent().putExtra(EXTRA_ERROR_MESSAGE, message))
    finish()
  }

  private fun dp(value: Float): Int =
    (value * cropperTheme.scale * resources.displayMetrics.density).roundToInt()

  private fun loadTypeface(family: String?, fallbackWeight: Int): Typeface {
    if (family != null) {
      return ReactFontManager.getInstance().getTypeface(family, Typeface.NORMAL, assets)
    }
    return when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
        Typeface.create(Typeface.DEFAULT, fallbackWeight, false)
      fallbackWeight >= 600 -> Typeface.DEFAULT_BOLD
      else -> Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
  }

  // Shows the grid while the image is being moved, and blocks gestures while
  // the image is cropped.
  @SuppressLint("ViewConstructor")
  private inner class CropContainer(context: Context) : FrameLayout(context) {
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> onCropTouchStart()
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onCropTouchEnd()
      }
      return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean =
      isProcessing || rotationAnimator != null || super.onInterceptTouchEvent(event)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
      isProcessing || rotationAnimator != null || super.onTouchEvent(event)
  }

  // OneKey's large Button: a capsule with a 16dp medium label, a pressed
  // color, and a spinner next to the label while loading.
  @SuppressLint("ViewConstructor")
  private class CapsuleButton(
    context: Context,
    title: String,
    textSize: Float,
    typeface: Typeface,
    textColor: Int,
    backgroundColor: Int,
    pressedColor: Int,
    horizontalPadding: Int,
    spinnerSize: Int,
    spinnerSpacing: Int,
  ) : LinearLayout(context) {
    private val spinner = ProgressBar(context).apply {
      isIndeterminate = true
      indeterminateTintList = ColorStateList.valueOf(textColor)
      visibility = View.GONE
    }

    var isLoading = false
      set(value) {
        field = value
        spinner.visibility = if (value) View.VISIBLE else View.GONE
        // OneKey dims disabled and loading buttons alike.
        alpha = if (value) DISABLED_ALPHA else 1f
      }

    init {
      orientation = HORIZONTAL
      gravity = Gravity.CENTER
      setPadding(horizontalPadding, 0, horizontalPadding, 0)
      background = pressable(capsule(backgroundColor), capsule(pressedColor))
      contentDescription = title
      addView(
        spinner,
        LayoutParams(spinnerSize, spinnerSize).apply { marginEnd = spinnerSpacing },
      )
      addView(
        TextView(context).apply {
          text = title
          setTextColor(textColor)
          setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize)
          this.typeface = typeface
          maxLines = 1
          ellipsize = TextUtils.TruncateAt.END
          includeFontPadding = false
          importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        },
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT),
      )
    }

    override fun getAccessibilityClassName(): CharSequence = Button::class.java.name

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
      super.onInitializeAccessibilityNodeInfo(info)
      info.isEnabled = !isLoading
    }
  }

  // OneKey's header icon button: a 24dp icon with a round pressed background.
  @SuppressLint("ViewConstructor")
  private class IconButton(
    context: Context,
    iconSize: Int,
    iconColor: Int,
    pressedColor: Int,
  ) : FrameLayout(context) {
    init {
      background = pressable(
        ColorDrawable(Color.TRANSPARENT),
        GradientDrawable().apply {
          shape = GradientDrawable.OVAL
          setColor(pressedColor)
        },
      )
      addView(
        ImageView(context).apply {
          setImageResource(R.drawable.image_crop_picker_rotate)
          imageTintList = ColorStateList.valueOf(iconColor)
          scaleType = ImageView.ScaleType.FIT_CENTER
          importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        },
        LayoutParams(iconSize, iconSize, Gravity.CENTER),
      )
    }

    override fun getAccessibilityClassName(): CharSequence = Button::class.java.name
  }

  companion object {
    private const val PREFIX = "com.margelo.nitro.reactnativeimagecroppicker.cropper."
    private const val EXTRA_INPUT_URI = "${PREFIX}inputUri"
    private const val EXTRA_ASPECT_RATIO_X = "${PREFIX}aspectRatioX"
    private const val EXTRA_ASPECT_RATIO_Y = "${PREFIX}aspectRatioY"
    private const val EXTRA_FREE_STYLE = "${PREFIX}freeStyle"
    private const val EXTRA_CIRCLE_OVERLAY = "${PREFIX}circleOverlay"
    private const val EXTRA_SHOW_GRID = "${PREFIX}showGrid"
    private const val EXTRA_ROTATE_BUTTON_HIDDEN = "${PREFIX}rotateButtonHidden"
    private const val EXTRA_TITLE = "${PREFIX}title"
    private const val EXTRA_CANCEL_TEXT = "${PREFIX}cancelText"
    private const val EXTRA_CONFIRM_TEXT = "${PREFIX}confirmText"
    private const val EXTRA_REQUESTED_ORIENTATION = "${PREFIX}requestedOrientation"

    const val EXTRA_OUTPUT_URI = "${PREFIX}outputUri"
    const val EXTRA_OUTPUT_OFFSET_X = "${PREFIX}outputOffsetX"
    const val EXTRA_OUTPUT_OFFSET_Y = "${PREFIX}outputOffsetY"
    const val EXTRA_OUTPUT_WIDTH = "${PREFIX}outputWidth"
    const val EXTRA_OUTPUT_HEIGHT = "${PREFIX}outputHeight"
    const val EXTRA_ERROR_MESSAGE = "${PREFIX}errorMessage"
    const val RESULT_ERROR = RESULT_FIRST_USER + 1

    // The area outside the crop box shows the page background at this opacity.
    private const val DIMMED_ALPHA = 0.7f
    private const val DISABLED_ALPHA = 0.4f
    private const val GRID_COLOR = Color.WHITE
    private const val GRID_HIDE_DELAY = 800L
    private const val GRID_FADE_IN_DURATION = 200L
    private const val GRID_FADE_OUT_DURATION = 350L
    private const val ROTATION_DURATION = 250L
    private const val FADE_IN_DURATION = 300L
    private val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

    internal fun createIntent(
      context: Context,
      source: Uri,
      destination: Uri,
      config: ImageCropPickerConfig,
      theme: ImageCropperTheme,
      requestedOrientation: Int,
    ): Intent {
      val intent = Intent(context, ImageCropperActivity::class.java)
        .putExtra(EXTRA_INPUT_URI, source)
        .putExtra(EXTRA_OUTPUT_URI, destination)
        .putExtra(EXTRA_FREE_STYLE, config.freeStyleCropEnabled)
        .putExtra(EXTRA_CIRCLE_OVERLAY, config.cropperCircleOverlay)
        .putExtra(EXTRA_SHOW_GRID, config.showCropGuidelines)
        .putExtra(EXTRA_ROTATE_BUTTON_HIDDEN, config.cropperRotateButtonsHidden)
        .putExtra(EXTRA_TITLE, config.cropperToolbarTitle)
        .putExtra(EXTRA_CANCEL_TEXT, config.cropperCancelText)
        .putExtra(EXTRA_CONFIRM_TEXT, config.cropperChooseText)
        .putExtra(EXTRA_REQUESTED_ORIENTATION, requestedOrientation)
      if (config.width != null && config.height != null) {
        intent.putExtra(EXTRA_ASPECT_RATIO_X, config.width.toFloat())
          .putExtra(EXTRA_ASPECT_RATIO_Y, config.height.toFloat())
      }
      theme.writeTo(intent)
      return intent
    }

    internal fun getOutputUri(data: Intent): Uri? = data.uriExtra(EXTRA_OUTPUT_URI)

    private fun Intent.uriExtra(name: String): Uri? =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, Uri::class.java)
      } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(name)
      }

    private fun withAlpha(color: Int, alpha: Float): Int =
      Color.argb(
        (Color.alpha(color) * alpha).roundToInt(),
        Color.red(color),
        Color.green(color),
        Color.blue(color),
      )

    private fun capsule(color: Int): Drawable =
      GradientDrawable().apply {
        // Clamped to half the height, which makes a capsule.
        cornerRadius = 10_000f
        setColor(color)
      }

    private fun pressable(normal: Drawable, pressed: Drawable): Drawable =
      StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), pressed)
        addState(intArrayOf(), normal)
      }
  }
}
