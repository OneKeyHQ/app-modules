package com.margelo.nitro.nativelist

import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import kotlin.math.roundToInt
import org.json.JSONObject

internal object NativeListMessageRenderer {
  data class Resolved(
    val title: NativeListResolvedText,
    val body: NativeListResolvedText,
    val time: NativeListResolvedText,
    val horizontalPadding: Int,
    val verticalPadding: Int,
    val bodyGap: Int,
    val timeGap: Int,
    val leadingGap: Int,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageStyle: JSONObject,
    val leading: JSONObject?,
    val thumbnail: JSONObject?,
    val unread: Boolean,
    val gravity: Int,
  )

  fun resolve(
    context: ThemedReactContext,
    item: NativeListItem,
    theme: JSONObject?,
    sourceScale: Boolean,
  ): Resolved {
    val style = item.json.optJSONObject("style") ?: JSONObject()
    val density = context.resources.displayMetrics.density
    fun dp(value: Int) =
      if (sourceScale) (value * density).roundToInt()
      else NativeListScale.dp(context.resources, value)
    fun dimension(source: JSONObject, name: String, default: Int) =
      if (source.has(name)) (source.optDouble(name) * density).roundToInt() else dp(default)
    fun color(name: String, fallback: String) =
      parseNativeListColor(theme?.optString(name, fallback) ?: fallback)
    fun text(name: String, size: Float, weight: String, color: Int, height: Int, lines: Int) =
      NativeListResolvedText.resolve(
        context,
        item.json.optString(name),
        style.optJSONObject(name),
        size,
        weight,
        color,
        height,
        lines,
        sourceScale,
      )
    val image = style.optJSONObject("image") ?: JSONObject()
    return Resolved(
      text("title", 14f, "semibold", color("primaryText", "#000000DF"), 20, 2),
      text(
        "body",
        14f,
        "regular",
        color("secondaryText", "#0000009B"),
        20,
        item.json.optInt("bodyLines", 3),
      ),
      text("time", 12f, "regular", color("disabledText", "#00000072"), 16, 1),
      dimension(style, "horizontalPadding", 12),
      dimension(style, "verticalPadding", 16),
      dimension(style, "lineGap", 2),
      dimension(style, "lineGap", 4),
      dimension(style, "leadingGap", 12),
      dimension(image, "width", 28),
      dimension(image, "height", 28),
      image,
      item.json.optJSONObject("leading"),
      item.json.optJSONObject("thumbnail"),
      item.json.optBoolean("unread"),
      when (style.optJSONObject("container")?.optString("contentVerticalAlignment")) {
        "bottom" -> Gravity.BOTTOM
        "center" -> Gravity.CENTER_VERTICAL
        else -> Gravity.TOP
      },
    )
  }

  class Views(private val context: ThemedReactContext) {
    val column = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
    val title = NativeListTextView(context)
    val body = NativeListTextView(context)
    val time = NativeListTextView(context)
    private var leading: NativeListLeadingVisual? = null
    private var thumbnail: NativeListImageSlot? = null

    init {
      listOf(title, body, time).forEach {
        column.addView(
          it,
          LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
          ),
        )
      }
    }

    fun bind(
      root: LinearLayout,
      resolved: Resolved,
      item: NativeListItem,
      theme: JSONObject?,
      sourceScale: Boolean,
    ) {
      resolved.title.bind(title)
      resolved.body.bind(body)
      resolved.time.bind(time)
      root.gravity = resolved.gravity
      root.clipChildren =
        !(resolved.leading?.optString("kind") == "token" && resolved.leading.has("networkImage"))
      root.setPadding(
        resolved.horizontalPadding,
        resolved.verticalPadding,
        resolved.horizontalPadding,
        resolved.verticalPadding,
      )
      resolved.leading?.let { visual ->
        val host = leading ?: NativeListLeadingVisual(context).also { leading = it }
        host.bind(visual, resolved.imageStyle, item.key, theme, resolved.unread, sourceScale)
        val params =
          LinearLayout.LayoutParams(resolved.imageWidth, resolved.imageHeight).apply {
            marginEnd = resolved.leadingGap
          }
        if (host.parent == null) root.addView(host, 0, params) else host.layoutParams = params
      }
        ?: leading?.let {
          it.recycle()
          root.removeView(it)
        }
      if (column.parent == null)
        root.addView(
          column,
          LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
        )
      body.layoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
          )
          .apply { topMargin = resolved.bodyGap }
      time.layoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
          )
          .apply { topMargin = resolved.timeGap }
      resolved.thumbnail?.let { image ->
        val slot = thumbnail ?: NativeListImageSlot(context).also { thumbnail = it }
        val dp: (Int) -> Int = {
          if (sourceScale) (it * context.resources.displayMetrics.density).roundToInt()
          else NativeListScale.dp(context.resources, it)
        }
        slot.view.outlineProvider =
          object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
              outline.setRoundRect(0, 0, view.width, view.height, dp(6).toFloat())
            }
          }
        slot.view.clipToOutline = true
        slot.view.foreground =
          GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            cornerRadius = dp(6).toFloat()
            setStroke(
              1,
              parseNativeListColor(theme?.optString("strongBackground", "#0000000F") ?: "#0000000F"),
            )
          }
        val params = LinearLayout.LayoutParams(dp(64), dp(64)).apply { marginStart = dp(12) }
        if (slot.view.parent == null) root.addView(slot.view, params)
        else slot.view.layoutParams = params
        slot.bind(
          image,
          "${item.key}:thumbnail",
          placeholder = theme?.optString("strongBackground", "#0000000F") ?: "#0000000F",
        )
      }
        ?: thumbnail?.let {
          it.recycle()
          root.removeView(it.view)
        }
    }

    fun recycle() {
      leading?.recycle()
      thumbnail?.recycle()
    }

    fun dispose() {
      leading?.dispose()
      thumbnail?.dispose()
    }
  }
}

internal class NativeListMessageRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  override val assetFields = listOf("leading", "thumbnail")
  private val views = NativeListMessageRenderer.Views(context)

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    views.bind(
      this,
      NativeListMessageRenderer.resolve(reactContext, item, theme, sourceScale),
      item,
      theme,
      sourceScale,
    )
  }

  override fun recycleContent() = views.recycle()

  override fun disposeContent() = views.dispose()
}
