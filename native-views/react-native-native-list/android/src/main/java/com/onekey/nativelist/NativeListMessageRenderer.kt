package com.margelo.nitro.nativelist

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.margelo.nitro.onekeyimage.OneKeyImageReusableView
import org.json.JSONObject

// Stateless template binding. The legacy row host still owns allocation and reset
// during migration; asset and text primitives retain their existing lifecycle.
internal object NativeListMessageRenderer {
  data class Views(
    val column: LinearLayout,
    val title: TextView,
    val body: TextView,
    val time: TextView,
    val unread: View,
    val thumbnail: OneKeyImageReusableView,
  )

  fun bind(
    root: LinearLayout,
    views: Views,
    item: NativeListItem,
    theme: JSONObject?,
    dp: (Int) -> Int,
    color: (JSONObject?, String, String) -> Int,
    showText: (TextView, String, Int) -> Unit,
    leading: (JSONObject, Int) -> Unit,
    thumbnailBorder: (Int, Float) -> Drawable,
    image: (JSONObject, OneKeyImageReusableView) -> Unit,
  ) = with(views) {
    root.gravity = Gravity.TOP
    root.setPadding(dp(12), dp(16), dp(12), dp(16))
    item.json.optJSONObject("leading")?.let { leading(it, 28) }
    unread.visibility = if (item.json.optBoolean("unread", false)) View.VISIBLE else View.GONE
    root.addView(column, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    showText(title, item.json.optString("title"), 2)
    showText(body, item.json.optString("body"), item.json.optInt("bodyLines", 3).coerceIn(1, 3))
    showText(time, item.json.optString("time"), 1)
    TextViewCompat.setLineHeight(title, dp(20))
    TextViewCompat.setLineHeight(body, dp(20))
    TextViewCompat.setLineHeight(time, dp(16))
    body.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) }
    time.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) }
    time.setTextColor(color(theme, "disabledText", "#00000072"))
    item.json.optJSONObject("thumbnail")?.let { source ->
      thumbnail.visibility = View.VISIBLE
      thumbnail.foreground = thumbnailBorder(color(theme, "strongBackground", "#0000000F"), 6f)
      root.addView(thumbnail, LayoutParams(dp(64), dp(64)).apply { marginStart = dp(12) })
      image(source, thumbnail)
    }
  }

  fun applyTextStyles(views: Views, style: JSONObject, apply: (TextView, JSONObject) -> Unit) {
    style.optJSONObject("title")?.let { apply(views.title, it) }
    style.optJSONObject("body")?.let { apply(views.body, it) }
    style.optJSONObject("time")?.let { apply(views.time, it) }
  }
}
