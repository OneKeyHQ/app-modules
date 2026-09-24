package com.margelo.nitro.nativelist

import android.animation.TimeInterpolator
import android.view.View
import android.widget.LinearLayout
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject

internal data class NativeListActionOrigin(
  val sourceView: View,
  val ownerRowView: NativeListRowHost,
  val bindingEpoch: Long,
  val source: String,
  val slot: Int? = null,
  val anchorInsetPixels: Int = 0,
  val windowPointPixels: android.graphics.PointF? = null,
)

/** List-facing lifecycle shared by template renderers. */
internal abstract class NativeListRowHost(context: ThemedReactContext) : LinearLayout(context) {
  var bindingEpoch: Long = 0
    protected set

  var listStyle: JSONObject? = null
  var onRowPress: ((NativeListItem, NativeListActionOrigin) -> Unit)? = null
  var onAction:
    ((NativeListItem, String, NativeSelectionTarget?, NativeListActionOrigin?) -> Unit)? =
    null
  var onBindingInvalidated: ((NativeListRowHost, Long) -> Unit)? = null

  abstract fun bind(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    listOrientation: String,
    itemIndex: Int?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
    useSourceScale: Boolean = false,
  )

  abstract fun bindSelection(
    item: NativeListItem,
    theme: JSONObject?,
    layout: String,
    itemIndex: Int?,
    selected: Boolean,
    checkboxState: (NativeListItem, NativeSelectionTarget?, String) -> String,
  )

  open fun bindMarketQuote(item: NativeListItem, theme: JSONObject?) {}

  open fun bindStableSummary(item: NativeListItem) {}

  abstract fun recycle()

  abstract fun dispose()

  open fun setReorderActive(active: Boolean) {}

  open fun canStartWalletGroupReorder(localY: Float) = true

  open fun finishWalletGroupReorder(
    durationMs: Long,
    interpolator: TimeInterpolator,
    completion: (() -> Unit)? = null,
  ) {
    completion?.invoke()
  }
}
