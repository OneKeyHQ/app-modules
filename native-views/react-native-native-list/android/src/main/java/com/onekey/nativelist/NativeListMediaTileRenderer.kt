package com.margelo.nitro.nativelist

import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject

internal class NativeListMediaTileRowView(context: ThemedReactContext) :
    NativeListRendererRowView(context) {
    private val picture = FrameLayout(context)
    private val image = NativeListMediaPreviewSlot(context)
    private val network = NativeListImageSlot(context)
    private val errorIcon = OneKeyIconView(context)
    private val title = NativeListTextView(context)
    private val subtitle = NativeListTextView(context)
    private val badge = NativeListTextView(context)
    private val metadata = LinearLayout(context).apply { orientation = VERTICAL }
    private val subtitleLine =
        LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
    private val close = NativeListTextView(context)
    private var imageStyle = JSONObject()
    private var closeKey = ""
    override val defaultCornerRadius = 16
    override val showsSelection = false
    override val pressChangesBackground = false
    override val assetFields = listOf("image", "media", "networkImage")

    override fun horizontalWidth(item: NativeListItem) = dp(200)

    override fun pressContent(pressed: Boolean) {
        picture.alpha = if (pressed) 0.8f else 1f
    }

    init {
        orientation = VERTICAL
        picture.addView(image.view, FrameLayout.LayoutParams(-1, -1))
        picture.addView(errorIcon)
        picture.addView(badge)
        subtitleLine.addView(subtitle, LayoutParams(0, -2, 1f))
        subtitleLine.addView(network.view)
        metadata.addView(subtitleLine, LayoutParams(-1, -2))
        metadata.addView(title, LayoutParams(-1, -2))
        addView(picture)
        addView(metadata, LayoutParams(-1, -2))
        addView(close, LayoutParams(-1, -2))
        close.setOnClickListener { emitAction(closeKey, close, "mediaClose") }
        close.gravity = Gravity.END
    }

    override fun bindContent(
        item: NativeListItem,
        theme: JSONObject?,
        layout: String,
        checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
    ) {
        val style = item.json.optJSONObject("style") ?: JSONObject()
        imageStyle = style.optJSONObject("image") ?: JSONObject()
        fun size(name: String, fallback: Int) =
            if (style.has(name)) stylePx(style.optDouble(name)) else dp(fallback)
        fun tint(name: String, fallback: String) =
            parseNativeListColor(theme?.optString(name, fallback) ?: fallback)
        setPadding(
            size("horizontalPadding", 10),
            size("verticalPadding", 10),
            size("horizontalPadding", 10),
            size("verticalPadding", 10),
        )
        gravity =
            Gravity.START or
                when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
                    "bottom" -> Gravity.BOTTOM
                    "center" -> Gravity.CENTER_VERTICAL
                    else -> Gravity.TOP
                }
        NativeListResolvedText.resolve(
                reactContext,
                item.json.optString("title"),
                style.optJSONObject("title"),
                16f,
                "medium",
                tint("primaryText", "#000000DF"),
                24,
                1,
                sourceScale,
            )
            .bind(title)
        NativeListResolvedText.resolve(
                reactContext,
                item.json.optString("subtitle"),
                style.optJSONObject("subtitle"),
                12f,
                "regular",
                tint("secondaryText", "#0000009B"),
                16,
                1,
                sourceScale,
            )
            .bind(subtitle)
        NativeListResolvedText.resolve(
                reactContext,
                item.json.optJSONObject("badge")?.optString("text") ?: "",
                style.optJSONObject("badge"),
                14f,
                "regular",
                parseNativeListColor("#FCFCFC"),
                0,
                1,
                sourceScale,
            )
            .bind(badge)
        if (style.optJSONObject("badge") == null) {
            // Legacy badge text keeps the platform font padding and proportional digits.
            badge.includeFontPadding = true
            badge.fontFeatureSettings = null
        }
        if (!style.optJSONObject("badge").let { it?.has("alignment") == true })
            badge.gravity = Gravity.CENTER
        badge.setPadding(dp(8), 0, dp(8), 0)
        badge.background =
            GradientDrawable().apply {
                setColor(parseNativeListColor("#000000DF"))
                cornerRadius = dp(10).toFloat()
                setStroke(dp(2), Color.WHITE)
            }
        badge.layoutParams = FrameLayout.LayoutParams(-2, dp(24), Gravity.END or Gravity.BOTTOM)
        title.layoutParams = LayoutParams(-1, -2).apply { topMargin = size("lineGap", 0) }
        metadata.layoutParams = LayoutParams(-1, -2).apply { topMargin = size("leadingGap", 8) }
        (subtitle.layoutParams as LayoutParams).marginEnd = dp(8)
        val state = item.json.optString("imageState")
        errorIcon.visibility = if (state == "error") VISIBLE else GONE
        errorIcon.iconName = "ImageSquareWavesOutline"
        errorIcon.tintColor = parseNativeListColor("#00000044")
        errorIcon.useSourceScale = sourceScale
        errorIcon.layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER)
        val media = item.json.optJSONObject("media")
        val source = media?.optJSONObject("source") ?: item.json.optJSONObject("image")
        val order = media?.optJSONArray("probeOrder")?.let { values ->
            (0 until values.length()).map { values.optString(it) }
        } ?: listOf("image")
        if (state != "empty" && state != "error" && source != null)
            image.bind(
                source,
                "${item.key}:media",
                fit = imageStyle.optString("contentFit").takeIf { it.isNotEmpty() },
                placeholder =
                    theme?.optString("strongBackground", "#0000000F")?.takeIf(String::isNotEmpty)
                        ?: "#0000000F",
                probeOrder = order,
            ) { loaded ->
                errorIcon.visibility = if (loaded) GONE else VISIBLE
                if (!loaded) image.view.setBackgroundColor(tint("strongBackground", "#0000000F"))
            }
        else image.recycle()
        // Legacy: only the empty/error states paint a slot fill; loaded images show
        // their own placeholder without a white backing.
        image.view.background =
            when (state) {
                "error" -> GradientDrawable().apply { setColor(tint("strongBackground", "#0000000F")) }
                "empty" -> GradientDrawable().apply { setColor(Color.WHITE) }
                else -> if (errorIcon.visibility == VISIBLE) GradientDrawable().apply { setColor(tint("strongBackground", "#0000000F")) } else null
            }
        image.view.outlineProvider =
            object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    if (
                        imageStyle.optString("shape") == "circle" && !imageStyle.has("cornerRadius")
                    )
                        outline.setOval(0, 0, view.width, view.height)
                    else
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            if (imageStyle.has("cornerRadius"))
                                stylePx(imageStyle.optDouble("cornerRadius")).toFloat()
                            else if (imageStyle.optString("shape") == "square") 0f
                            else dp(10).toFloat(),
                        )
                }
            }
        image.view.clipToOutline = true
        network.view.layoutParams = LayoutParams(dp(14), dp(14))
        network.view.outlineProvider =
            object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
        network.view.clipToOutline = true
        item.json.optJSONObject("networkImage")?.let { source ->
            network.view.visibility = VISIBLE
            network.view.alpha = 0f
            network.bind(source, "${item.key}:network", variant = "network", round = true) { loaded
                ->
                network.view.alpha = if (loaded) 1f else 0f
            }
        }
            ?: run {
                network.recycle()
                network.view.visibility = GONE
            }
        closeKey = item.json.optString("closeActionKey")
        close.text = "×"
        close.textSize = if (sourceScale) 22f else NativeListScale.font(resources, 22f)
        close.setTextColor(tint("primaryText", "#000000DF"))
        close.visibility = if (closeKey.isEmpty()) GONE else VISIBLE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width =
            if (imageStyle.has("width")) stylePx(imageStyle.optDouble("width"))
            else
                (MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight).coerceAtLeast(
                    0
                )
        val height =
            if (imageStyle.has("height")) stylePx(imageStyle.optDouble("height")) else width
        picture.layoutParams = LayoutParams(width, height)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun recycleContent() {
        image.recycle()
        network.recycle()
        closeKey = ""
        picture.alpha = 1f
    }

    override fun disposeContent() {
        image.dispose()
        network.dispose()
    }
}
