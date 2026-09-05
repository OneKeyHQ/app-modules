package com.margelo.nitro.nativelist

import android.os.Looper
import android.view.View
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext

@DoNotStrip
class HybridNativeList(context: ThemedReactContext) : HybridNativeListSpec() {
  private val hostView = NativeListView(context)

  override val view: View = hostView

  override var snapshotJson: String = ""
    get() = field
    set(value) {
      if (field == value) return
      field = value
      dispatchToUi { hostView.applySnapshot(value) }
    }

  override var onRowAction: ((payloadJson: String) -> Unit)? = null
    set(value) { field = value; hostView.onRowAction = value }
  override var onSelectionDelta: ((payloadJson: String) -> Unit)? = null
    set(value) { field = value; hostView.onSelectionDelta = value }
  override var onReorder: ((payloadJson: String) -> Unit)? = null
    set(value) { field = value; hostView.onReorder = value }
  override var onEndReached: ((payloadJson: String) -> Unit)? = null
    set(value) { field = value; hostView.onEndReached = value }
  override var onVisibleRangeChanged: ((payloadJson: String) -> Unit)? = null
    set(value) { field = value; hostView.onVisibleRangeChanged = value }

  override fun applySnapshot(snapshotJson: String) {
    dispatchToUi { hostView.applySnapshot(snapshotJson) }
  }

  override fun applyPatches(patchesJson: String) {
    dispatchToUi { hostView.applyPatches(patchesJson) }
  }

  override fun reconcileSelection(selectedKeysJson: String) {
    dispatchToUi { hostView.reconcileSelection(selectedKeysJson) }
  }

  override fun scrollToKey(
    key: String,
    animated: Boolean,
    alignment: NativeListScrollAlignment,
    viewPosition: Double,
    viewOffset: Double,
  ) {
    dispatchToUi {
      hostView.scrollToKey(
        key,
        animated,
        alignment.stringValue(),
        viewPosition,
        viewOffset,
      )
    }
  }

  override fun scrollToIndex(
    index: Double,
    animated: Boolean,
    alignment: NativeListScrollAlignment,
    viewPosition: Double,
    viewOffset: Double,
  ) {
    if (!index.isFinite() || index < 0 || index % 1.0 != 0.0 || index > Int.MAX_VALUE) return
    dispatchToUi {
      hostView.scrollToIndex(
        index.toInt(),
        animated,
        alignment.stringValue(),
        viewPosition,
        viewOffset,
      )
    }
  }

  override fun scrollToOffset(offset: Double, animated: Boolean) {
    if (!offset.isFinite() || offset < 0) return
    dispatchToUi { hostView.scrollToOffset(offset, animated) }
  }

  override fun scrollToEnd(animated: Boolean) {
    dispatchToUi { hostView.scrollToEnd(animated) }
  }

  override fun setRefreshing(refreshing: Boolean) {
    dispatchToUi { hostView.setRefreshing(refreshing) }
  }

  override fun onDropView() {
    hostView.dispose()
    super.onDropView()
  }

  override fun dispose() {
    hostView.dispose()
    super.dispose()
  }

  /**
   * Nitro prop setters can run off the Android UI thread. Apply synchronously
   * during a UI commit; only off-main calls need to be posted to this host.
   */
  private fun dispatchToUi(action: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) action() else hostView.post(action)
  }

  private fun NativeListScrollAlignment.stringValue(): String = when (this) {
    NativeListScrollAlignment.START -> "start"
    NativeListScrollAlignment.CENTER -> "center"
    NativeListScrollAlignment.END -> "end"
    NativeListScrollAlignment.NEAREST -> "nearest"
  }
}
