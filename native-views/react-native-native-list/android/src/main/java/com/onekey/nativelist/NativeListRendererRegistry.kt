package com.margelo.nitro.nativelist

import com.facebook.react.uimanager.ThemedReactContext

internal object NativeListRendererRegistry {
  private val factories: Map<NativeListRendererKey, (ThemedReactContext) -> NativeListRowHost> =
    mapOf(
      NativeListRendererKey.SECTION_HEADER to ::NativeListSectionHeaderRowView,
      NativeListRendererKey.WALLET_GROUP to ::NativeListWalletGroupRowView,
      NativeListRendererKey.IDENTITY to ::NativeListIdentityRowView,
      NativeListRendererKey.MARKET to ::NativeListMarketRowView,
      NativeListRendererKey.MESSAGE to ::NativeListMessageRowView,
      NativeListRendererKey.RAIL to ::NativeListRailRowView,
      NativeListRendererKey.MEDIA_TILE to ::NativeListMediaTileRowView,
      NativeListRendererKey.METRIC_CARD to ::NativeListMetricCardRowView,
      NativeListRendererKey.DATA_ROW to ::NativeListDataRowView,
      NativeListRendererKey.ACTIVITY to ::NativeListActivityRowView,
      NativeListRendererKey.SYSTEM to ::NativeListSystemRowView,
      NativeListRendererKey.ACTION to ::NativeListActionRowView,
    )

  private val keys =
    mapOf(
      "sectionHeader" to NativeListRendererKey.SECTION_HEADER,
      "walletGroup" to NativeListRendererKey.WALLET_GROUP,
      "identity" to NativeListRendererKey.IDENTITY,
      "market" to NativeListRendererKey.MARKET,
      "message" to NativeListRendererKey.MESSAGE,
      "rail" to NativeListRendererKey.RAIL,
      "mediaTile" to NativeListRendererKey.MEDIA_TILE,
      "metricCard" to NativeListRendererKey.METRIC_CARD,
      "dataRow" to NativeListRendererKey.DATA_ROW,
      "activity" to NativeListRendererKey.ACTIVITY,
      "system" to NativeListRendererKey.SYSTEM,
      "action" to NativeListRendererKey.ACTION,
    )

  fun key(type: String) = keys.getValue(type)

  fun create(context: ThemedReactContext, viewType: Int): NativeListRowHost =
    factories.getValue(NativeListRendererKey.entries[viewType])(context)
}
