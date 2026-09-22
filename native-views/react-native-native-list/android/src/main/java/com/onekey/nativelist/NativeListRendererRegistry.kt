package com.margelo.nitro.nativelist

import com.facebook.react.uimanager.ThemedReactContext

internal object NativeListRendererRegistry {
  private val factories: Map<NativeListRendererKey, (ThemedReactContext) -> NativeListRowHost> =
    mapOf(
      NativeListRendererKey.LEGACY to ::NativeListRowView,
      NativeListRendererKey.MESSAGE to ::NativeListMessageRowView,
    )

  fun key(type: String) =
    if (type == "message") NativeListRendererKey.MESSAGE else NativeListRendererKey.LEGACY

  fun create(context: ThemedReactContext, viewType: Int): NativeListRowHost =
    factories.getValue(NativeListRendererKey.entries[viewType])(context)
}

// Compatibility belongs to list chrome, never to Message style resolution.
// docs/MIGRATION_BASELINE.md records the callers that must migrate before removal.
internal fun nativeListLegacyShowsSeparator(item: NativeListItem): Boolean =
  item.json.optBoolean("separator") &&
    !item.key.startsWith("token-") &&
    !item.key.startsWith("balance-token-") &&
    item.key != "linear-custom-token"
