package com.margelo.nitro.nativelist

import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONObject

/** Fixed visual slots; no row-template or business-key branching. */
internal class NativeListLeadingVisual(private val reactContext: ThemedReactContext) :
  FrameLayout(reactContext) {
  private val fallback =
    TextView(context).apply {
      gravity = Gravity.CENTER
      typeface = NativeListFonts.bold(context)
    }
  private val icon = OneKeyIconView(context)
  private val unread = View(context)
  private val networkBackdrop = View(context)
  private val corner = OneKeyIconView(context)
  private val cornerBackdrop = View(context)
  private val slots = mutableListOf<NativeListImageSlot>()
  private val overlaySlots = mutableListOf<NativeListImageSlot>()
  private val overlays = mutableListOf<Pair<FrameLayout, JSONObject>>()
  private var visual = JSONObject()
  private var style = JSONObject()
  private var sources = emptyList<Pair<JSONObject, String>>()
  val fallbackTextView: TextView
    get() = fallback

  var bitmapBorderWidth = 0

  /**
   * Legacy wallet-sidebar source-scale leading visual: text-only overlays are 16dp
   * tall pills with 12sp text on a 16dp line box, and a dashed frame border is 1dp.
   */
  var walletTextOverlays = false

  private fun isWalletText(data: JSONObject) =
    walletTextOverlays &&
      data.optString("text").isNotEmpty() &&
      data.optJSONObject("image") == null &&
      data.optString("name").isEmpty()

  private fun overlayHeight(data: JSONObject) =
    data.optInt("height", if (isWalletText(data)) 16 else data.optInt("size", 20))

  fun layoutNetworkBackdrop(top: Int, height: Int) {
    networkBackdrop.layout(networkBackdrop.left, top, networkBackdrop.right, top + height)
  }

  // Legacy: loaded images in a "rounded" slot clip at a fixed radius (10dp;
  // 8dp for explicit-height account selectors), independent of slot size.
  var roundedImageRadius = 10

  /** Market resolves its own radius; legacy still decoded circular bitmaps by shape. */
  var roundFollowsVisualShape = false
  var glyphSize = 18
  var roundedGlyphOrigin = false
  var iconBorder = true
  private var sourceScale = false

  private fun dp(value: Int) =
    if (sourceScale) (value * resources.displayMetrics.density).roundToInt()
    else NativeListScale.dp(resources, value)

  private fun color(value: String, fallback: Int = Color.TRANSPARENT) =
    runCatching { parseNativeListColor(value) }.getOrDefault(fallback)

  private fun fill(value: Int, radius: Float) =
    GradientDrawable().apply {
      setColor(value)
      cornerRadius = radius
    }

  init {
    listOf(fallback, icon, networkBackdrop, cornerBackdrop, corner, unread).forEach { addView(it) }
    clipChildren = false
    clipToPadding = false
  }

  fun bind(
    visual: JSONObject,
    style: JSONObject,
    key: String,
    theme: JSONObject?,
    isUnread: Boolean,
    sourceScale: Boolean,
    secondaryVisual: JSONObject? = null,
  ) {
    this.visual = visual
    this.style = style
    this.sourceScale = sourceScale
    icon.useSourceScale = sourceScale
    corner.useSourceScale = sourceScale
    val kind = visual.optString("kind")
    val variant =
      when (kind) {
        "token" -> "token"
        "network" -> "network"
        "account",
        "wallet" -> "avatar"
        else -> "generic"
      }
    sources =
      if (kind == "stackedImages")
        visual
          .optJSONArray("images")
          ?.let { images ->
            (0 until minOf(3, images.length())).mapNotNull {
              images.optJSONObject(it)?.let { image -> image to "generic" }
            }
          }
          .orEmpty()
      else if (kind == "icon") emptyList()
      else visual.optJSONObject("image")?.let { listOf(it to variant) }.orEmpty()
    if (kind == "token" && sources.isNotEmpty())
      visual.optJSONObject("networkImage")?.let { sources = sources + (it to "network") }
    secondaryVisual?.let { second ->
      val kind = second.optString("kind")
      val source =
        when (kind) {
          "stackedImages" -> second.optJSONArray("images")?.optJSONObject(0)
          "icon" -> null
          else -> second.optJSONObject("image")
        }
      if (source != null)
        sources =
          sources +
            (source to
              when (kind) {
                "token" -> "token"
                "network" -> "network"
                "account",
                "wallet" -> "avatar"
                else -> "generic"
              })
    }
    while (slots.size < sources.size) {
      val slot = NativeListImageSlot(reactContext)
      slots.add(slot)
      addView(slot.view, indexOfChild(if (slots.size == 1) networkBackdrop else cornerBackdrop))
    }
    val placeholder = theme?.optString("strongBackground", "#0000000F") ?: "#0000000F"
    val fallbackIcon = visual.optJSONObject("fallbackIcon")
    val sourceFallback =
      sources.firstOrNull()?.first?.optString("loadingStrategy", "none")?.let { it != "none" } ==
        true && (visual.has("fallbackText") || fallbackIcon != null)
    val fallbackKey =
      if (sourceFallback)
        sources.firstOrNull()?.first?.let { source ->
          nativeListSourceFallbackStateKey(
            source.optString("uri"),
            source
              .optJSONObject("headers")
              ?.let { headers ->
                headers.keys().asSequence().associateWith { headers.optString(it) }
              }
              .orEmpty(),
          )
        }
      else null
    val restored = fallbackKey?.let(NativeListSourceFallbackState::has) == true
    fallback.textSize = if (sourceScale) 13f else NativeListScale.font(resources, 13f)
    fallback.text = visual.optString("fallbackText").take(2)
    fallback.setTextColor(color("#00000072"))
    fallback.visibility =
      if (kind != "icon" && fallbackIcon == null && (sources.isEmpty() || restored)) VISIBLE
      else GONE
    val glyph = if (kind == "icon") visual else fallbackIcon
    icon.iconName = glyph?.optString("name") ?: ""
    icon.tintColor = color(glyph?.optString("tintColor", "#0000009B") ?: "#0000009B")
    icon.visibility =
      if (kind == "icon" || (sources.isEmpty() || restored) && fallbackIcon != null) VISIBLE
      else GONE
    val visualBackground =
      color(
        visual.optString("backgroundColor", if (sources.isEmpty()) placeholder else "#00000000")
      )
    val backgroundColor = if (sourceFallback) color(placeholder) else visualBackground
    background =
      fill(backgroundColor, 0f).apply {
        if (kind == "icon" && iconBorder) setStroke(1, color("#0000001F"))
        if (visual.optString("borderStyle") == "dashed")
          setStroke(
            dp(if (walletTextOverlays) 1 else 2),
            color(visual.optString("borderColor", "#00000072")),
            dp(4).toFloat(),
            dp(4).toFloat(),
          )
      }
    slots.forEachIndexed { index, slot ->
      if (index >= sources.size) {
        slot.recycle()
        slot.view.visibility = GONE
      } else {
        slot.view.visibility = if (index > 0 || sourceFallback) INVISIBLE else VISIBLE
        slot.bind(
          sources[index].first,
          "$key:leading:$index",
          sources[index].second,
          if (index == 0) style.optString("contentFit").takeIf { it.isNotEmpty() } else null,
          placeholder,
          round =
            visual.optString("shape", if (kind == "image") "rounded" else "circle") == "circle" &&
              (roundFollowsVisualShape || !style.has("shape") && !style.has("cornerRadius")),
        ) { loaded ->
          slot.view.visibility =
            if (!loaded && (index > 0 || sourceFallback)) INVISIBLE else VISIBLE
          if (index == 0 && sourceFallback) {
            fallbackKey?.let {
              if (loaded) NativeListSourceFallbackState.forget(it)
              else NativeListSourceFallbackState.remember(it)
            }
            (background as? GradientDrawable)?.setColor(
              if (loaded) visualBackground else color(placeholder)
            )
            fallback.visibility = if (!loaded && fallbackIcon == null) VISIBLE else GONE
            icon.visibility = if (!loaded && fallbackIcon != null) VISIBLE else GONE
          }
        }
      }
    }
    networkBackdrop.visibility = if (kind == "token" && sources.size > 1) VISIBLE else GONE
    networkBackdrop.background =
      fill(color(theme?.optString("rowBackground", "#FFFFFF") ?: "#FFFFFF"), dp(10).toFloat())
    val badge = visual.optJSONObject("cornerIcon")
    corner.visibility = if (badge == null) GONE else VISIBLE
    cornerBackdrop.visibility = corner.visibility
    corner.iconName = badge?.optString("name") ?: ""
    corner.tintColor = color(badge?.optString("tintColor", "#0000009B") ?: "#0000009B")
    cornerBackdrop.background =
      fill(
        color(
          badge?.optString(
            "backgroundColor",
            theme?.optString("rowBackground", "#FFFFFF") ?: "#FFFFFF",
          ) ?: "#FFFFFF"
        ),
        dp(10).toFloat(),
      )
    unread.visibility = if (isUnread) VISIBLE else GONE
    unread.background = fill(color("#E5484D"), dp(4).toFloat())
    val previousOverlays = overlays.toList()
    overlays.clear()
    val descriptors =
      visual
        .optJSONArray("overlays")
        ?.let { values -> (0 until minOf(2, values.length())).map { values.getJSONObject(it) } }
        .orEmpty()
    while (overlaySlots.size < descriptors.size) overlaySlots.add(NativeListImageSlot(reactContext))
    descriptors.forEachIndexed { index, data ->
      val frame = previousOverlays.getOrNull(index)?.first ?: FrameLayout(context)
      frame.background =
        fill(
          color(data.optString("backgroundColor", "#00000000")),
          minOf(
            dp(data.optInt("width", data.optInt("size", 20))),
            dp(overlayHeight(data)),
          ) / 2f,
        )
      frame.outlineProvider = ViewOutlineProvider.BACKGROUND
      frame.clipToOutline = true
      val child: View =
        data.optJSONObject("image")?.let { image ->
          val slot = overlaySlots[index]
          slot.view.visibility = INVISIBLE
          slot.bind(image, "$key:overlay:$index", placeholder = placeholder) {
            slot.view.visibility = if (it) VISIBLE else INVISIBLE
          }
          slot.view
        }
          ?: if (data.optString("text").isNotEmpty()) {
            overlaySlots[index].recycle()
            ((frame.getChildAt(0) as? TextView) ?: TextView(context)).apply {
              text = data.optString("text")
              gravity = Gravity.CENTER
              includeFontPadding = false
              val walletText = isWalletText(data)
              val size = if (walletText) 12f else 10f
              textSize = if (sourceScale) size else NativeListScale.font(resources, size)
              // The overlay TextView is reused: reset the line box for non-wallet text.
              if (walletText) androidx.core.widget.TextViewCompat.setLineHeight(this, dp(16))
              else setLineSpacing(0f, 1f)
              typeface =
                if (isWalletText(data)) NativeListFonts.regular(context)
                else NativeListFonts.medium(context)
              setTextColor(color(data.optString("tintColor", "#0000009B")))
            }
          } else {
            overlaySlots[index].recycle()
            ((frame.getChildAt(0) as? OneKeyIconView) ?: OneKeyIconView(context)).apply {
              useSourceScale = sourceScale
              iconName = data.optString("name")
              tintColor = color(data.optString("tintColor", "#0000009B"))
            }
          }
      val inset = data.optInt("padding", 0)
      if (isWalletText(data)) frame.setPadding(dp(2), 0, dp(2), 0)
      else frame.setPadding(dp(inset), dp(inset), dp(inset), dp(inset))
      if (child.parent !== frame) {
        frame.removeAllViews()
        // A reused slot view may still belong to a frame dropped by an earlier bind.
        (child.parent as? android.view.ViewGroup)?.removeView(child)
        frame.addView(child, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
      }
      if (frame.parent == null) addView(frame)
      overlays.add(frame to data)
    }
    previousOverlays.drop(descriptors.size).forEach { (frame, _) ->
      // Release the overlay child so its image slot can join a later frame.
      frame.removeAllViews()
      removeView(frame)
    }
    overlaySlots.drop(descriptors.size).forEach { it.recycle() }
    unread.bringToFront()
    requestLayout()
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    fun place(view: View, x: Int, y: Int, w: Int, h: Int) {
      val left = if (layoutDirection == LAYOUT_DIRECTION_RTL) width - x - w else x
      view.measure(
        MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY),
      )
      view.layout(left, y, left + w, y + h)
    }
    val w = width
    val h = height
    val kind = visual.optString("kind")
    val shape =
      style.optString(
        "shape",
        visual.optString("shape", if (kind == "image") "rounded" else "circle"),
      )
    val radius =
      if (style.has("cornerRadius"))
        (style.optDouble("cornerRadius") * resources.displayMetrics.density).toFloat()
      else
        when (shape) {
          "square" -> 0f
          "rounded" ->
            if (style.has("shape")) 10 * resources.displayMetrics.density
            else minOf(dp(10).toFloat(), h / 4f)
          else -> minOf(w, h) / 2f
        }
    (background as? GradientDrawable)?.apply {
      this.shape =
        if (style.optString("shape") == "circle" && !style.has("cornerRadius"))
          GradientDrawable.OVAL
        else GradientDrawable.RECTANGLE
      cornerRadius = radius
    }
    if (bitmapBorderWidth > 0) {
      (background as? GradientDrawable)?.setStroke(
        bitmapBorderWidth,
        color(visual.optString("borderColor")),
      )
    }
    place(fallback, 0, 0, w, h)
    val iconSize =
      if (sources.isNotEmpty() && visual.optJSONObject("fallbackIcon") != null)
        (minOf(w, h) *
            if (visual.optJSONObject("fallbackIcon")?.optString("name") == "GlobusOutline") 1.2f
            else 1f)
          .roundToInt()
      else dp(glyphSize)
    place(
      icon,
      (w - iconSize + if (roundedGlyphOrigin) 1 else 0) / 2,
      (h - iconSize + if (roundedGlyphOrigin) 1 else 0) / 2,
      iconSize,
      iconSize,
    )
    val tokenPair = kind == "token" && sources.size > 1
    slots.take(sources.size).forEachIndexed { index, slot ->
      val size =
        if (tokenPair && index == 1) dp(16)
        else if (sources.size == 1 || tokenPair) h else (h * 0.72f).roundToInt()
      val x =
        if (tokenPair && index == 1) w - size + dp(2)
        else if (sources.size == 1 || tokenPair) 0
        else (index * (w - size).toFloat() / maxOf(1, sources.size - 1)).roundToInt()
      val y = if (tokenPair && index == 1) h - size + dp(2) else (h - size) / 2
      val imageWidth = if (sources.size == 1 || tokenPair && index == 0) w else size
      slot.view.measure(
        MeasureSpec.makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
      )
      val inset = if (index == 0) bitmapBorderWidth else 0
      place(slot.view, x + inset, y + inset, imageWidth - 2 * inset, size - 2 * inset)
      val imageRadius =
        if (shape == "rounded" && !style.has("shape") && !style.has("cornerRadius"))
          dp(roundedImageRadius).toFloat()
        else radius
      val r = if (index == 0 && (sources.size == 1 || tokenPair)) imageRadius else size / 2f
      slot.view.outlineProvider =
        object : ViewOutlineProvider() {
          override fun getOutline(view: View, outline: Outline) {
            if (index == 0 && style.optString("shape") == "circle" && !style.has("cornerRadius"))
              outline.setOval(0, 0, view.width, view.height)
            else outline.setRoundRect(-inset, -inset, view.width + inset, view.height + inset, r)
          }
        }
      slot.view.clipToOutline = true
    }
    place(networkBackdrop, w - dp(16), h - dp(16), dp(20), dp(20))
    place(cornerBackdrop, w - dp(16), h - dp(16), dp(20), dp(20))
    place(corner, w - dp(15), h - dp(15), dp(18), dp(18))
    place(unread, w - dp(8), 0, dp(8), dp(8))
    overlays.forEach { (frame, data) ->
      val size = data.optInt("size", 20)
      val oh = dp(overlayHeight(data))
      val ow =
        if (isWalletText(data) && !data.has("width")) {
          // Legacy: wallet text pills wrap their label plus horizontal padding.
          val child = frame.getChildAt(0)
          child?.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(oh, MeasureSpec.EXACTLY),
          )
          (child?.measuredWidth ?: 0) + frame.paddingLeft + frame.paddingRight
        } else dp(data.optInt("width", size))
      val x = dp(data.optInt("offsetX", data.optInt("offset", 2)))
      val y = dp(data.optInt("offsetY", data.optInt("offset", 2)))
      val ox = if (data.optString("position") == "topLeft") -x else w - ow + x
      val oy = if (data.optString("position") == "topLeft") -y else h - oh + y
      frame.measure(
        MeasureSpec.makeMeasureSpec(ow, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(oh, MeasureSpec.EXACTLY),
      )
      place(frame, ox, oy, ow, oh)
    }
  }

  fun recycle() {
    slots.forEach { it.recycle() }
    overlaySlots.forEach { it.recycle() }
  }

  fun dispose() {
    slots.forEach { it.dispose() }
    overlaySlots.forEach { it.dispose() }
  }
}
