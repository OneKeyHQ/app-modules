package com.margelo.nitro.autosizeinput

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

@DoNotStrip
class HybridAutoSizeInput(val context: ThemedReactContext) : HybridAutoSizeInputSpec() {

  // Subviews
  private val prefixView = TextView(context)
  private val inputView = EditText(context)
  private val suffixView = TextView(context)

  // State
  private var isUpdatingFromJS = false
  private var isRecalculating = false
  private var currentFontSize = 48f
  private var isDisposed = false
  private var maxFontSizeProp: Double? = null
  private var minFontSizeProp: Double? = null

  // Container view
  override val view: View = object : ViewGroup(context) {
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
      performLayout(r - l, b - t)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
      super.onSizeChanged(w, h, oldw, oldh)
      if (w > 0 && h > 0) {
        post { recalculateFontSize() }
      }
    }
  }

  // TextWatcher
  private val textWatcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
      if (isUpdatingFromJS || isDisposed) return
      recalculateFontSize()
      onChangeText?.invoke(s?.toString() ?: "")
    }
  }

  // Props
  override var text: String?
    get() = inputView.text?.toString()
    set(value) {
      if (isDisposed) return
      isUpdatingFromJS = true
      inputView.removeTextChangedListener(textWatcher)
      inputView.setText(value ?: "")
      inputView.setSelection(inputView.text?.length ?: 0)
      inputView.addTextChangedListener(textWatcher)
      isUpdatingFromJS = false
      recalculateFontSize()
    }

  override var prefix: String?
    get() = prefixView.text?.toString()
    set(value) {
      if (isDisposed) return
      prefixView.text = value ?: ""
      prefixView.visibility = if ((value ?: "").isEmpty()) View.GONE else View.VISIBLE
      view.requestLayout()
      recalculateFontSize()
    }

  override var suffix: String?
    get() = suffixView.text?.toString()
    set(value) {
      if (isDisposed) return
      suffixView.text = value ?: ""
      suffixView.visibility = if ((value ?: "").isEmpty()) View.GONE else View.VISIBLE
      view.requestLayout()
      recalculateFontSize()
    }

  override var placeholder: String?
    get() = inputView.hint?.toString()
    set(value) {
      if (isDisposed) return
      inputView.hint = value ?: ""
    }

  override var fontSize: Double?
    get() = maxFontSizeProp
    set(value) {
      if (isDisposed) return
      maxFontSizeProp = value
      recalculateFontSize()
    }

  override var minFontSize: Double?
    get() = minFontSizeProp
    set(value) {
      if (isDisposed) return
      minFontSizeProp = value
      recalculateFontSize()
    }

  override var multiline: Boolean? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      updateInputMode()
    }

  override var maxNumberOfLines: Double? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      recalculateFontSize()
    }

  override var textColor: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      val color = parseColor(value) ?: Color.BLACK
      inputView.setTextColor(color)
      if (prefixColor == null) prefixView.setTextColor(color)
      if (suffixColor == null) suffixView.setTextColor(color)
    }

  override var prefixColor: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      prefixView.setTextColor(parseColor(value) ?: parseColor(textColor) ?: Color.BLACK)
    }

  override var suffixColor: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      suffixView.setTextColor(parseColor(value) ?: parseColor(textColor) ?: Color.BLACK)
    }

  override var placeholderColor: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      inputView.setHintTextColor(parseColor(value) ?: Color.GRAY)
    }

  override var textAlign: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      val gravity = when (value) {
        "center" -> Gravity.CENTER
        "right" -> Gravity.CENTER_VERTICAL or Gravity.END
        else -> Gravity.CENTER_VERTICAL or Gravity.START
      }
      inputView.gravity = gravity
    }

  override var fontFamily: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      recalculateFontSize()
    }

  override var fontWeight: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      recalculateFontSize()
    }

  override var editable: Boolean? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      val isEditable = value ?: true
      inputView.isFocusable = isEditable
      inputView.isFocusableInTouchMode = isEditable
      inputView.isCursorVisible = isEditable
    }

  override var keyboardType: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      inputView.inputType = when (value) {
        "numberPad" -> InputType.TYPE_CLASS_NUMBER
        "decimalPad" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        "emailAddress" -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        "phonePad" -> InputType.TYPE_CLASS_PHONE
        else -> InputType.TYPE_CLASS_TEXT
      }
    }

  override var returnKeyType: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      inputView.imeOptions = when (value) {
        "done" -> EditorInfo.IME_ACTION_DONE
        "go" -> EditorInfo.IME_ACTION_GO
        "next" -> EditorInfo.IME_ACTION_NEXT
        "search" -> EditorInfo.IME_ACTION_SEARCH
        "send" -> EditorInfo.IME_ACTION_SEND
        else -> EditorInfo.IME_ACTION_UNSPECIFIED
      }
    }

  override var autoCorrect: Boolean? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      val currentType = inputView.inputType
      inputView.inputType = if (value == true) {
        currentType or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
      } else {
        currentType and InputType.TYPE_TEXT_FLAG_AUTO_CORRECT.inv()
      }
    }

  override var autoCapitalize: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      val baseType = inputView.inputType and InputType.TYPE_MASK_CLASS
      val capFlag = when (value) {
        "characters" -> InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        "words" -> InputType.TYPE_TEXT_FLAG_CAP_WORDS
        "sentences" -> InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        else -> 0
      }
      // Clear existing cap flags, then apply new one
      val cleared = inputView.inputType and
        (InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or
         InputType.TYPE_TEXT_FLAG_CAP_WORDS or
         InputType.TYPE_TEXT_FLAG_CAP_SENTENCES).inv()
      inputView.inputType = cleared or capFlag
    }

  override var selectionColor: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      val color = parseColor(value)
      if (color != null) {
        inputView.highlightColor = color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
          inputView.textCursorDrawable?.setTint(color)
          inputView.textSelectHandle?.setTint(color)
          inputView.textSelectHandleLeft?.setTint(color)
          inputView.textSelectHandleRight?.setTint(color)
        }
      }
    }

  override var prefixMarginRight: Double? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      view.requestLayout()
    }

  override var suffixMarginLeft: Double? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      view.requestLayout()
    }

  override var showBorder: Boolean? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      updateInputAppearance()
    }

  override var inputBackgroundColor: String? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      updateInputAppearance()
    }

  override var contentAutoWidth: Boolean? = null
    get() = field
    set(value) {
      if (isDisposed) return
      field = value
      view.requestLayout()
    }


  override var onChangeText: ((String) -> Unit)? = null
  override var onFocus: (() -> Unit)? = null
  override var onBlur: (() -> Unit)? = null

  init {
    setupViews()
  }

  private fun setupViews() {
    // Configure prefix
    prefixView.visibility = View.GONE
    prefixView.includeFontPadding = false
    prefixView.isSingleLine = true
    prefixView.maxLines = 1
    prefixView.setHorizontallyScrolling(true)
    prefixView.gravity = Gravity.CENTER_VERTICAL

    // Configure suffix
    suffixView.visibility = View.GONE
    suffixView.includeFontPadding = false
    suffixView.isSingleLine = true
    suffixView.maxLines = 1
    suffixView.setHorizontallyScrolling(true)
    suffixView.gravity = Gravity.CENTER_VERTICAL

    // Configure input
    inputView.background = null
    inputView.setPadding(0, 0, 0, 0)
    inputView.includeFontPadding = false
    inputView.isSingleLine = true
    inputView.maxLines = 1
    inputView.setHorizontallyScrolling(true)
    inputView.isVerticalScrollBarEnabled = false
    inputView.overScrollMode = View.OVER_SCROLL_NEVER
    inputView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
    inputView.addTextChangedListener(textWatcher)

    inputView.setOnFocusChangeListener { _, hasFocus ->
      if (isDisposed) return@setOnFocusChangeListener
      if (hasFocus) onFocus?.invoke() else onBlur?.invoke()
    }

    // Make the whole composed area (prefix + input + suffix) tappable for focus.
    val focusFromContainer = View.OnClickListener {
      if (isDisposed || editable == false) return@OnClickListener
      requestInputFocus()
    }
    view.isClickable = true
    view.setOnClickListener(focusFromContainer)
    prefixView.setOnClickListener(focusFromContainer)
    inputView.setOnClickListener(focusFromContainer)
    suffixView.setOnClickListener(focusFromContainer)

    // Add to container
    (view as ViewGroup).addView(prefixView)
    (view as ViewGroup).addView(inputView)
    (view as ViewGroup).addView(suffixView)
    updateInputAppearance()
  }

  private fun updateInputMode() {
    val isMulti = multiline == true
    if (isMulti) {
      inputView.isSingleLine = false
      inputView.maxLines = (maxNumberOfLines ?: 1.0).toInt()
      inputView.inputType = inputView.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    } else {
      inputView.isSingleLine = true
      inputView.maxLines = 1
    }
    view.requestLayout()
    recalculateFontSize()
  }

  private fun performLayout(width: Int, height: Int) {
    if (width <= 0 || height <= 0) return

    val density = context.resources.displayMetrics.density
    val edgeInset = (2f * density).toInt()

    // Measure prefix
    val prefixW = if (prefixView.visibility == View.VISIBLE) {
      measureTextViewWidthPx(prefixView)
    } else 0

    // Measure suffix
    val suffixW = if (suffixView.visibility == View.VISIBLE) {
      measureTextViewWidthPx(suffixView)
    } else 0

    val prefixGap = if (prefixView.visibility == View.VISIBLE) ((prefixMarginRight ?: 0.0) * density).toInt() else 0
    val suffixGap = if (suffixView.visibility == View.VISIBLE) ((suffixMarginLeft ?: 0.0) * density).toInt() else 0

    val inputX = edgeInset + prefixW + prefixGap
    val isContentAutoWidthEnabled = contentAutoWidth == true && multiline != true
    val inputW: Int
    val suffixX: Int

    if (isContentAutoWidthEnabled) {
      val typedText = inputView.text?.toString() ?: ""
      val minInputWidth = (24f * density).toInt()
      val desiredInputWidth = maxOf(measureSingleLineTextWidthPx(typedText), minInputWidth)
      val suffixSegment = if (suffixView.visibility == View.VISIBLE) suffixGap + suffixW else 0
      val maxInputWidth = maxOf(width - edgeInset - inputX - suffixSegment, 0)
      inputW = minOf(desiredInputWidth, maxInputWidth)
      val desiredSuffixX = if (suffixView.visibility == View.VISIBLE) inputX + inputW + suffixGap else width - edgeInset
      suffixX = minOf(desiredSuffixX, width - edgeInset - suffixW)
    } else {
      inputW = maxOf(width - edgeInset - inputX - suffixW - suffixGap, 0)
      suffixX = width - edgeInset - suffixW
    }

    // Re-measure with the exact final slot size before layout.
    if (prefixView.visibility == View.VISIBLE) {
      prefixView.measure(
        View.MeasureSpec.makeMeasureSpec(prefixW, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
      )
    }
    if (suffixView.visibility == View.VISIBLE) {
      suffixView.measure(
        View.MeasureSpec.makeMeasureSpec(suffixW, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
      )
    }

    // Layout input
    val inputHeightSpec = if (multiline == true) {
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    } else {
      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
    }
    inputView.measure(
      View.MeasureSpec.makeMeasureSpec(inputW, View.MeasureSpec.EXACTLY),
      inputHeightSpec
    )
    val inputH = if (multiline == true) height else inputView.measuredHeight.coerceAtMost(height)
    val inputTop = if (multiline == true) 0 else ((height - inputH) / 2).coerceAtLeast(0)
    inputView.layout(inputX, inputTop, inputX + inputW, inputTop + inputH)
    resetSingleLineVerticalOffset()

    val prefixTop = ((height - prefixView.measuredHeight) / 2).coerceAtLeast(0)
    val suffixTop = ((height - suffixView.measuredHeight) / 2).coerceAtLeast(0)

    // Layout prefix
    prefixView.layout(edgeInset, prefixTop, edgeInset + prefixW, prefixTop + prefixView.measuredHeight)

    // Layout suffix
    suffixView.layout(suffixX, suffixTop, suffixX + suffixW, suffixTop + suffixView.measuredHeight)

  }

  // MARK: - Font Size Calculation

  private fun recalculateFontSize() {
    if (isDisposed || isRecalculating) return
    isRecalculating = true
    try {
      val maxSize = (maxFontSizeProp ?: 48.0).toFloat()
      val minSize = (minFontSizeProp ?: 16.0).toFloat()
      val isContentAutoWidthEnabled = contentAutoWidth == true && multiline != true
      val width = view.width
      val height = view.height
      val inputText = inputView.text?.toString() ?: ""
      if (width <= 0 || height <= 0) {
        // Keep text size in sync even before first valid layout pass.
        applyFontSize(maxSize)
        return
      }

      // In contentAutoWidth mode, prioritize width expansion instead of shrinking text.
      if (isContentAutoWidthEnabled) {
        val density = context.resources.displayMetrics.density
        val edgeInset = (2f * density).toInt()
        val prefixW = if (prefixView.visibility == View.VISIBLE) measureTextViewWidthPx(prefixView) else 0
        val suffixW = if (suffixView.visibility == View.VISIBLE) measureTextViewWidthPx(suffixView) else 0
        val prefixGap = if (prefixView.visibility == View.VISIBLE) ((prefixMarginRight ?: 0.0) * density).toInt() else 0
        val suffixGap = if (suffixView.visibility == View.VISIBLE) ((suffixMarginLeft ?: 0.0) * density).toInt() else 0
        val inputX = edgeInset + prefixW + prefixGap
        val suffixSegment = if (suffixView.visibility == View.VISIBLE) suffixGap + suffixW else 0
        val maxInputWidth = maxOf(width - edgeInset - inputX - suffixSegment, 0)
        val textForSizing = if (inputText.isEmpty()) (placeholder ?: "") else inputText

        // Expand width first; once width hits max, shrink font to keep full text visible.
        val targetSize = if (maxInputWidth <= 0) {
          minSize
        } else if (textForSizing.isEmpty()) {
          maxSize
        } else {
          findOptimalFontSizeSingleLine(
            fullText = textForSizing,
            availableWidth = maxInputWidth.toFloat(),
            minSize = minSize,
            maxSize = maxSize
          )
        }
        applyFontSize(targetSize)
        return
      }

      val density = context.resources.displayMetrics.density

      val prefixText = prefix ?: ""
      val suffixText = suffix ?: ""
      val displayText = if (inputText.isEmpty()) (placeholder ?: "") else inputText
      val fullText = prefixText + displayText + suffixText

      if (fullText.isEmpty()) {
        applyFontSize(maxSize)
        return
      }

      val prefixGap = if (prefixView.visibility == View.VISIBLE) ((prefixMarginRight ?: 0.0) * density) else 0.0
      val suffixGap = if (suffixView.visibility == View.VISIBLE) ((suffixMarginLeft ?: 0.0) * density) else 0.0
      val availableWidth = width - prefixGap.toFloat() - suffixGap.toFloat()

      val optimalSize = if (multiline == true) {
        val maxLines = (maxNumberOfLines ?: 1.0).toInt()
        findOptimalFontSizeMultiline(fullText, availableWidth, height.toFloat(), maxLines, minSize, maxSize)
      } else {
        findOptimalFontSizeSingleLine(fullText, availableWidth, minSize, maxSize)
      }

      applyFontSize(optimalSize)
    } finally {
      isRecalculating = false
    }
  }

  private fun findOptimalFontSizeSingleLine(
    fullText: String,
    availableWidth: Float,
    minSize: Float,
    maxSize: Float
  ): Float {
    var low = minSize
    var high = maxSize
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    while (high - low > 0.5f) {
      val mid = (low + high) / 2f
      paint.textSize = mid * context.resources.displayMetrics.scaledDensity
      paint.typeface = makeTypeface()
      val textWidth = paint.measureText(fullText)
      if (textWidth <= availableWidth) {
        low = mid
      } else {
        high = mid
      }
    }

    return low
  }

  private fun findOptimalFontSizeMultiline(
    fullText: String,
    availableWidth: Float,
    availableHeight: Float,
    maxLines: Int,
    minSize: Float,
    maxSize: Float
  ): Float {
    var low = minSize
    var high = maxSize
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    while (high - low > 0.5f) {
      val mid = (low + high) / 2f
      paint.textSize = mid * context.resources.displayMetrics.scaledDensity
      paint.typeface = makeTypeface()

      val layout = StaticLayout.Builder
        .obtain(fullText, 0, fullText.length, paint, availableWidth.toInt())
        .build()

      if (layout.lineCount <= maxLines && layout.height <= availableHeight) {
        low = mid
      } else {
        high = mid
      }
    }

    return low
  }

  private fun applyFontSize(size: Float) {
    currentFontSize = size
    val typeface = makeTypeface()
    inputView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    inputView.typeface = typeface
    prefixView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    prefixView.typeface = typeface
    suffixView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    suffixView.typeface = typeface
    resetSingleLineVerticalOffset()
    if (contentAutoWidth == true && multiline != true && view.width > 0 && view.height > 0) {
      performLayout(view.width, view.height)
    } else {
      view.requestLayout()
    }
  }

  private fun makeTypeface(): Typeface {
    val style = when (fontWeight) {
      "bold" -> Typeface.BOLD
      else -> Typeface.NORMAL
    }

    return if (fontFamily != null && fontFamily!!.isNotEmpty()) {
      try {
        Typeface.create(fontFamily, style)
      } catch (e: Exception) {
        Typeface.defaultFromStyle(style)
      }
    } else {
      Typeface.defaultFromStyle(style)
    }
  }

  // MARK: - Methods

  override fun focus() {
    if (isDisposed) return
    requestInputFocus()
  }

  override fun blur() {
    if (isDisposed) return
    inputView.clearFocus()
    val imm = context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
    imm?.hideSoftInputFromWindow(inputView.windowToken, 0)
  }

  // MARK: - Helpers

  private fun parseColor(hex: String?): Int? {
    if (hex.isNullOrEmpty()) return null
    return try {
      Color.parseColor(if (hex.startsWith("#")) hex else "#$hex")
    } catch (e: Exception) {
      null
    }
  }

  private fun updateInputAppearance() {
    val drawable = GradientDrawable()
    drawable.setColor(parseColor(inputBackgroundColor) ?: Color.TRANSPARENT)
    if (showBorder == true) {
      val borderWidthPx = context.resources.displayMetrics.density.toInt().coerceAtLeast(1)
      drawable.setStroke(borderWidthPx, parseColor(textColor) ?: Color.parseColor("#D1D5DB"))
    } else {
      drawable.setStroke(0, Color.TRANSPARENT)
    }
    inputView.background = drawable
  }

  private fun measureSingleLineTextWidthPx(text: String): Int {
    if (text.isEmpty()) return 0
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = currentFontSize * context.resources.displayMetrics.scaledDensity
    paint.typeface = makeTypeface()
    return kotlin.math.ceil(paint.measureText(text).toDouble()).toInt()
  }

  private fun measureTextViewWidthPx(textView: TextView): Int {
    val content = textView.text?.toString().orEmpty()
    if (content.isEmpty()) return 0
    val bounds = Rect()
    textView.paint.getTextBounds(content, 0, content.length, bounds)
    val advanceWidth = kotlin.math.ceil(textView.paint.measureText(content).toDouble()).toInt()
    val glyphWidth = bounds.width()
    textView.measure(
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val desiredWidth = textView.measuredWidth
    val density = context.resources.displayMetrics.density
    // Keep extra room for side bearings/kerning to avoid clipping on some glyphs/fonts.
    val safetyPadding = maxOf((4f * density).toInt(), kotlin.math.ceil(textView.textSize * 0.5f).toInt())
    val measured = maxOf(maxOf(advanceWidth, glyphWidth), desiredWidth) + safetyPadding
    return measured
  }

  private fun requestInputFocus() {
    inputView.requestFocus()
    val imm = context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
    imm?.showSoftInput(inputView, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
  }

  private fun resetSingleLineVerticalOffset() {
    if (multiline == true) return
    if (inputView.scrollY != 0) {
      inputView.scrollTo(inputView.scrollX, 0)
    }
  }

  override fun afterUpdate() {
    super.afterUpdate()
    if (!isDisposed) {
      view.requestLayout()
    }
  }

  override fun dispose() {
    isDisposed = true
    inputView.removeTextChangedListener(textWatcher)
    inputView.onFocusChangeListener = null
  }
}
