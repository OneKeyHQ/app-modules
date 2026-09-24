package com.margelo.nitro.nativelist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject

/** Composite only; each keyed member owns an independent Identity binding. */
internal class NativeListWalletGroupRowView(context: ThemedReactContext) :
  NativeListRendererRowView(context) {
  private val membersColumn = LinearLayout(context).apply { orientation = VERTICAL }
  private val memberViews = linkedMapOf<String, NativeListIdentityRowView>()
  private var members = emptyList<NativeListItem>()
  private val compactContainer =
    FrameLayout(context).apply {
      visibility = GONE
      importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }
  private var compactParent: NativeListIdentityRowView? = null
  private val dragBadge = TextView(context)
  private var compact = false
  private var expandedHeight = 0
  private var animatedHeight: Int? = null
  private var expandAnimator: ValueAnimator? = null
  private var currentTheme: JSONObject? = null
  private var currentLayout = "linear"
  override val defaultCornerRadius = 12
  override val pressChangesBackground = false
  private var containerStyle = JSONObject()
  private var compactHeight = 0
  override val showsSelection = false

  override fun usesSourceScale(item: NativeListItem, provided: Boolean) =
    item.usesSelectorSourceScale || provided

  override fun unselectedBackground(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
  ) = parseNativeListColor(theme?.optString("subduedBackground", "#F9F9F9") ?: "#F9F9F9")

  override fun resolvedMeasureHeight(explicit: Int?): Int? =
    if (compact) compactHeight else animatedHeight ?: explicit ?: expandedHeight

  override fun accessibilityText(item: NativeListItem) =
    item.json.optString(
      "accessibilityLabel",
      item.json.optJSONObject("parent")?.optString("title") ?: "",
    )

  private fun memberItems(item: NativeListItem): List<NativeListItem> {
    val children = item.json.getJSONArray("children")
    return listOf(NativeListItem.parse(item.json.getJSONObject("parent"))) +
      (0 until children.length()).map { NativeListItem.parse(children.getJSONObject(it)) }
  }

  private fun defaultMemberHeight(item: NativeListItem) =
    item.json.optInt("height", if ((item.json.optJSONArray("badges")?.length() ?: 0) > 0) 92 else 68)

  private fun memberHeight(item: NativeListItem) =
    item.styledHeight?.let(::stylePx) ?: dp(defaultMemberHeight(item))

  init {
    orientation = VERTICAL
    addView(membersColumn, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    addView(compactContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    dragBadge.gravity = Gravity.CENTER
    dragBadge.typeface = NativeListFonts.semibold(context)
    compactContainer.addView(dragBadge)
  }

  override fun bindContent(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    currentTheme = theme
    currentLayout = layout
    members = memberItems(item)
    val keys = members.map { it.key }.toSet()
    memberViews.keys
      .toList()
      .filter { it !in keys }
      .forEach { key ->
        memberViews.remove(key)?.let {
          it.dispose()
          membersColumn.removeView(it)
        }
      }
    val style = item.json.optJSONObject("style")
    containerStyle = style?.optJSONObject("container") ?: JSONObject()
    val inset = if (members.first().json.has("height")) dp(1) else 0
    val horizontal =
      style
        ?.takeIf { it.has("horizontalPadding") }
        ?.let { stylePx(it.optDouble("horizontalPadding")) } ?: inset
    val styledVertical =
      style?.takeIf { it.has("verticalPadding") }?.let { stylePx(it.optDouble("verticalPadding")) }
    val vertical = styledVertical ?: inset
    membersColumn.setPadding(horizontal, vertical, horizontal, vertical)
    setPadding(0, 0, 0, 0)
    gravity =
      Gravity.FILL_HORIZONTAL or
        when (style?.optJSONObject("container")?.optString("contentVerticalAlignment")) {
          "bottom" -> Gravity.BOTTOM
          "center" -> Gravity.CENTER_VERTICAL
          else -> Gravity.TOP
        }
    expandedHeight =
      item.styledHeight?.let(::stylePx)
        ?: modelHeight(item, layout)
        ?: if (styledVertical == null && members.none { it.styledHeight != null }) {
          // Legacy default: one rounding pass over the whole unstyled group; the
          // row size modifier can only raise the minimum.
          val base =
            members.sumOf(::defaultMemberHeight) + (members.size - 1) * 12 + if (inset > 0) 2 else 0
          val sizeDelta =
            when (item.json.optString("size")) {
              "small" -> -8
              "large" -> 12
              else -> 0
            }
          maxOf(dp(base), dp((base + sizeDelta).coerceAtLeast(0)))
        } else members.sumOf(::memberHeight) + dp((members.size - 1) * 12) + vertical * 2
    members.forEachIndexed { index, member ->
      val view =
        memberViews.getOrPut(member.key) {
          NativeListIdentityRowView(reactContext).also { view ->
            // Legacy: members own their disabled state; the group never gates them.
            view.onRowPress = { source, origin -> onRowPress?.invoke(source, origin) }
            view.onAction = { source, key, target, origin ->
              onAction?.invoke(source, key, target, origin)
            }
            view.onBindingInvalidated = { source, epoch ->
              onBindingInvalidated?.invoke(source, epoch)
            }
          }
        }
      view.listStyle = listStyle
      view.bind(
        member,
        theme,
        layout,
        "vertical",
        null,
        member.json.optBoolean("selected"),
        checkboxState,
        sourceScale,
      )
      if (membersColumn.indexOfChild(view) != index) {
        membersColumn.removeView(view)
        membersColumn.addView(view, index)
      }
      view.layoutParams =
        LayoutParams(LayoutParams.MATCH_PARENT, memberHeight(member)).apply {
          if (index > 0) topMargin = dp(12)
        }
    }
    if (compactParent == null) {
      compactParent =
        NativeListIdentityRowView(reactContext).also {
          compactContainer.addView(
            it,
            0,
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
          )
        }
    }
    compactParent!!.listStyle = listStyle
    val parent = members.first()
    compactParent!!.bind(
      parent,
      theme,
      layout,
      "vertical",
      null,
      parent.json.optBoolean("selected"),
      checkboxState,
      sourceScale,
    )
    // Legacy compact drag: the parent keeps its own height (92 with badges) plus
    // the group inset, never less than the 68dp allocation.
    compactHeight = maxOf(dp(68), memberHeight(parent) + vertical * 2)
    compactContainer.setPadding(horizontal, vertical, horizontal, vertical)
    compactContainer.layoutParams.height = compactHeight
    val count = members.drop(1).count { it.json.optBoolean("draggable", true) }
    dragBadge.text = "+$count"
    dragBadge.visibility = if (count == 0) GONE else VISIBLE
    dragBadge.textSize = if (sourceScale) 12f else NativeListScale.font(resources, 12f)
    dragBadge.setPadding(dp(6), 0, dp(6), 0)
    dragBadge.minWidth = dp(24)
    dragBadge.setTextColor(
      parseNativeListColor(theme?.optString("inverseText", "#FCFCFC") ?: "#FCFCFC")
    )
    dragBadge.background =
      GradientDrawable().apply {
        setColor(
          parseNativeListColor(theme?.optString("inverseBackground", "#000000DF") ?: "#000000DF")
        )
        setStroke(
          dp(1),
          parseNativeListColor(theme?.optString("rowBackground", "#FFFFFF") ?: "#FFFFFF"),
        )
        cornerRadius = dp(12).toFloat()
      }
    dragBadge.layoutParams =
      FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, dp(24), Gravity.END or Gravity.BOTTOM)
        .apply {
          // Legacy: the badge is inset 4dp from the group's own edges.
          rightMargin = dp(4) - horizontal
          bottomMargin = dp(4) - vertical
        }
  }

  override fun bindSelectionContent(
    item: NativeListItem,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  ) {
    members = memberItems(item)
    members.forEach { member ->
      memberViews[member.key]?.bindSelection(
        member,
        currentTheme,
        currentLayout,
        null,
        member.json.optBoolean("selected"),
        checkboxState,
      )
    }
    members.firstOrNull()?.let {
      compactParent?.bindSelection(
        it,
        currentTheme,
        currentLayout,
        null,
        it.json.optBoolean("selected"),
        checkboxState,
      )
    }
  }

  override fun canStartWalletGroupReorder(localY: Float): Boolean {
    val y = localY - membersColumn.top
    members.forEach { member ->
      val view = memberViews[member.key] ?: return@forEach
      if (y >= view.top && y < view.bottom)
        // Legacy: only `draggable: false` excludes a member from starting a group drag.
        return member.json.optBoolean("draggable", true)
    }
    return true
  }

  private var expansionCompletion: (() -> Unit)? = null

  // Legacy group chrome: subdued fill with the separator border drawn beneath the
  // members (not as a foreground); row-level backgroundColor does not apply.
  override fun appearance() {
    super.appearance()
    val theme = currentTheme
    fun parse(value: String, fallback: String) =
      runCatching { parseNativeListColor(value) }.getOrElse { parseNativeListColor(fallback) }
    val fill =
      if (containerStyle.has("backgroundColor"))
        parse(containerStyle.optString("backgroundColor"), "#F9F9F9")
      else parse(theme?.optString("subduedBackground", "#F9F9F9") ?: "#F9F9F9", "#F9F9F9")
    val stroke =
      if (containerStyle.has("borderColor")) parse(containerStyle.optString("borderColor"), "#0000001F")
      else parse(theme?.optString("separator", "#0000001F") ?: "#0000001F", "#0000001F")
    background =
      GradientDrawable().apply {
        setColor(fill)
        cornerRadius =
          if (containerStyle.has("cornerRadius")) stylePx(containerStyle.optDouble("cornerRadius")).toFloat()
          else dp(12).toFloat()
        setStroke(
          if (containerStyle.has("borderWidth")) stylePx(containerStyle.optDouble("borderWidth")) else dp(1),
          stroke,
        )
      }
    foreground = null
  }

  private fun showExpandedMembers(restAlpha: Float) {
    membersColumn.visibility = VISIBLE
    membersColumn.alpha = 1f
    for (index in 0 until membersColumn.childCount) {
      membersColumn.getChildAt(index).apply {
        visibility = VISIBLE
        alpha = if (index == 0) 1f else restAlpha
      }
    }
  }

  // Cancelling still settles the expanded state and runs the pending completion,
  // so the container's post-drop relayout is never lost.
  private fun cancelExpansion() {
    val completion = expansionCompletion
    expansionCompletion = null
    val animator = expandAnimator
    expandAnimator = null
    animatedHeight = null
    if (animator != null) {
      animator.removeAllListeners()
      animator.cancel()
      showExpandedMembers(1f)
      requestLayout()
    }
    completion?.invoke()
  }

  override fun setReorderActive(active: Boolean) {
    if (compact == active && expandAnimator == null) return
    cancelExpansion()
    compact = active
    if (active) {
      membersColumn.visibility = GONE
    } else {
      showExpandedMembers(1f)
    }
    compactContainer.visibility = if (active) VISIBLE else GONE
    compactContainer.alpha = 1f
    compactParent?.setReorderActive(active)
    appearance()
    requestLayout()
  }

  override fun finishWalletGroupReorder(
    durationMs: Long,
    interpolator: TimeInterpolator,
    completion: (() -> Unit)?,
  ) {
    cancelExpansion()
    val startHeight = height.coerceAtLeast(dp(68))
    compact = false
    compactParent?.setReorderActive(false)
    compactContainer.visibility = GONE
    compactContainer.alpha = 1f
    // Legacy: the first member stays opaque while the remaining members fade in.
    showExpandedMembers(0f)
    appearance()
    if (expandedHeight <= startHeight) {
      showExpandedMembers(1f)
      requestLayout()
      completion?.invoke()
      return
    }
    animatedHeight = startHeight
    requestLayout()
    expansionCompletion = completion
    expandAnimator =
      ValueAnimator.ofInt(startHeight, expandedHeight).apply {
        duration = durationMs
        this.interpolator = interpolator
        addUpdateListener {
          animatedHeight = it.animatedValue as Int
          val fraction = it.animatedFraction
          for (index in 1 until membersColumn.childCount) membersColumn.getChildAt(index).alpha = fraction
          requestLayout()
        }
        addListener(
          object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
              if (expandAnimator !== animation) return
              expandAnimator = null
              animatedHeight = null
              showExpandedMembers(1f)
              requestLayout()
              val pending = expansionCompletion
              expansionCompletion = null
              pending?.invoke()
            }
          }
        )
        start()
      }
  }

  override fun recycleContent() {
    cancelExpansion()
    memberViews.values.forEach { it.dispose() }
    memberViews.clear()
    membersColumn.removeAllViews()
    membersColumn.alpha = 1f
    membersColumn.visibility = VISIBLE
    members = emptyList()
    compactParent?.dispose()
    compactParent?.let(compactContainer::removeView)
    compactParent = null
    compactContainer.visibility = GONE
    compactContainer.alpha = 1f
    compact = false
    expandedHeight = 0
    currentTheme = null
  }

  override fun disposeContent() {}
}
