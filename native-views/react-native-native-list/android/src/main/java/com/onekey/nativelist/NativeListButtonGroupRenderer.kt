package com.margelo.nitro.nativelist

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.widget.TextViewCompat
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject

internal class NativeListButtonGroupRowView(context: ThemedReactContext, fonts: NativeListFonts) :
  NativeListRendererRowView(context, fonts) {
  private var generation = 0
  private var rowOpacity = 1f

  override fun appearance() {
    super.appearance()
    alpha = rowOpacity
  }
  override fun defaultHeight(item: NativeListItem, layout: String) = 64

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    recycleContent()
    rowOpacity = (item.json.optJSONObject("style")?.optJSONObject("container")?.optDouble("opacity", item.json.optDouble("opacity", 1.0)) ?: item.json.optDouble("opacity", 1.0)).toFloat()
    orientation = HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
    setPadding(0, dp(8), 0, dp(8))
    val info = parseNativeListColor(theme?.optString("info", "#0F69F6") ?: "#0F69F6")
    val descriptors = item.json.optJSONArray("buttons") ?: return
    val epoch = generation
    for (index in 0 until descriptors.length()) {
      val descriptor = descriptors.getJSONObject(index)
      val enabled = !item.json.optBoolean("disabled") && !descriptor.optBoolean("disabled") && !descriptor.optBoolean("loading")
      val button = FrameLayout(context).apply {
        isEnabled = enabled
        alpha = if (enabled) 1f else 0.4f
        contentDescription = descriptor.optString("accessibilityLabel", descriptor.optString("label"))
        setTag(com.facebook.react.R.id.react_test_id, descriptor.optString("testID").takeIf { it.isNotEmpty() })
        background = StateListDrawable().apply {
          fun fill(pressed: Boolean) = GradientDrawable().apply {
            setColor(if (pressed) (info and 0x00FFFFFF) or (43 shl 24) else Color.TRANSPARENT)
            cornerRadius = dp(12).toFloat()
            setStroke(dp(1), (info and 0x00FFFFFF) or (89 shl 24))
          }
          addState(intArrayOf(android.R.attr.state_pressed), fill(true))
          addState(intArrayOf(), fill(false))
        }
      }
      val label = NativeListTextView(context).apply {
        text = descriptor.optString("label")
        textSize = NativeListScale.font(resources, 14f)
        typeface = fonts.medium(context)
        setTextColor(info)
        gravity = Gravity.CENTER
        includeFontPadding = false
        maxLines = 1
        ellipsize = android.text.TextUtils.TruncateAt.END
        TextViewCompat.setLineHeight(this, dp(20))
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
      }
      val content = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setPadding(dp(8), dp(12), dp(8), dp(12))
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
      }
      button.addView(content, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
      if (descriptor.optBoolean("loading")) {
        val spinner = ProgressBar(context, null, android.R.attr.progressBarStyleSmall).apply {
          isIndeterminate = true
          indeterminateTintList = ColorStateList.valueOf(info)
          importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
        content.addView(spinner, LayoutParams(dp(20), dp(20)).apply { marginEnd = dp(8) })
      }
      content.addView(label, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
      button.setOnClickListener {
        if (generation == epoch && enabled) emitAction(descriptor.optString("actionKey"), button, "buttonGroup", index)
      }
      button.isClickable = enabled
      addView(button, LayoutParams(0, dp(48), 1f).apply { if (index > 0) marginStart = dp(10) })
    }
  }

  override fun recycleContent() {
    generation += 1
    for (index in 0 until childCount) getChildAt(index).setOnClickListener(null)
    removeAllViews()
  }

  override fun disposeContent() = recycleContent()
}
