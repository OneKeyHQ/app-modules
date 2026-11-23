package com.margelo.nitro.nativelist

import android.content.Intent
import android.test.InstrumentationTestCase
import android.view.View
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.soloader.SoLoader
import com.margelo.nitro.nativelogger.OneKeyLog
import org.json.JSONArray
import org.json.JSONObject

@Suppress("DEPRECATION")
class NativeListMarketMetricStructureTest : InstrumentationTestCase() {
  private fun item(metrics: JSONArray? = null): NativeListItem {
    val json = JSONObject()
      .put("type", "market")
      .put("key", "market-eth")
      .put("variant", "token")
      .put("title", "ETH")
      .put("subtitle", "Ethereum")
      .put("leading", JSONObject().put("kind", "icon").put("name", "coin"))
      .put("price", "$42")
      .put("change", JSONObject().put("text", "+2%").put("tone", "positive"))
    if (metrics != null) json.put("metrics", metrics)
    return NativeListItem.parse(json)
  }

  private fun metrics(vararg columns: Triple<String, Int, String>): JSONArray =
    JSONArray().apply {
      columns.forEach { (key, weight, text) ->
        put(JSONObject().put("key", key).put("weight", weight).put("alignment", "end")
          .put("text", text))
      }
    }

  fun testTextOnlyQuoteUpdateKeepsStructureWhileColumnChangesRequireRebind() {
    val compact = item()
    val two = item(metrics(Triple("price", 1, "$42"), Triple("change", 1, "+2%")))
    val updated = item(metrics(Triple("price", 1, "$43"), Triple("change", 1, "+3%")))
    val three = item(metrics(
      Triple("price", 1, "$43"), Triple("change", 1, "+3%"), Triple("volume", 1, "$1B")))
    val weighted = item(metrics(Triple("price", 2, "$43"), Triple("change", 1, "+3%")))
    val reordered = item(metrics(Triple("change", 1, "+3%"), Triple("price", 1, "$43")))

    assertTrue(hasSameMarketMetricStructure(compact, compact))
    assertTrue(hasSameMarketMetricStructure(two, updated))
    assertFalse(hasSameMarketMetricStructure(compact, two))
    assertFalse(hasSameMarketMetricStructure(two, three))
    assertFalse(hasSameMarketMetricStructure(two, weighted))
    assertFalse(hasSameMarketMetricStructure(two, reordered))
  }

  fun testAccessibilityUsesDisplayedMetricLinesAndUpdatesWithQuotes() = withMarketRow { row ->
    val columns = metrics(
      Triple("price", 1, "$100"), Triple("change", 1, "+5%"), Triple("volume", 1, "$1B"))
    columns.getJSONObject(0).put("textSegments", JSONArray()
      .put(JSONObject().put("text", "$0.0"))
      .put(JSONObject().put("text", "3").put("style", "subscript")))
      .put("secondaryText", "price secondary fallback")
      .put("secondaryTextSegments", JSONArray().put(JSONObject().put("text", "per ETH")))
    columns.getJSONObject(1).put("secondaryText", "24 hours")
    bind(row, item(columns))
    assertEquals("ETH, Ethereum, $0.03, per ETH, +5%, 24 hours, $1B", row.contentDescription)

    val updated = metrics(
      Triple("price", 1, "$101"), Triple("change", 1, "+6%"), Triple("volume", 1, "$2B"))
    updated.getJSONObject(0).put("secondaryText", "Updated price")
    updated.getJSONObject(1).put("textSegments", JSONArray()
      .put(JSONObject().put("text", "+0.0"))
      .put(JSONObject().put("text", "7%").put("style", "subscript")))
      .put("secondaryText", "updated change fallback")
      .put("secondaryTextSegments", JSONArray().put(JSONObject().put("text", "7 days")))
    row.bindMarketQuote(item(updated), null)
    assertEquals("ETH, Ethereum, $101, Updated price, +0.07%, 7 days, $2B", row.contentDescription)

    bind(row, item(metrics(Triple("price", 1, "$102"), Triple("change", 1, "+8%"))))
    assertEquals("ETH, Ethereum, $102, +8%", row.contentDescription)
    bind(row, item())
    assertEquals("ETH, Ethereum, $42, +2%", row.contentDescription)
  }

  fun testExplicitAccessibilityLabelWinsOnFullBindAndQuote() = withMarketRow { row ->
    fun labeled(price: String): NativeListItem {
      val json = item(metrics(Triple("price", 1, price), Triple("change", 1, "+5%"))).json
      json.put("accessibilityLabel", "Custom quote")
      return NativeListItem.parse(json)
    }
    bind(row, labeled("$100"))
    assertEquals("Custom quote", row.contentDescription)
    row.bindMarketQuote(labeled("$101"), null)
    assertEquals("Custom quote", row.contentDescription)
    row.bindMarketQuote(item(metrics(Triple("price", 1, "$102"), Triple("change", 1, "+6%"))), null)
    assertEquals("ETH, Ethereum, $102, +6%", row.contentDescription)
  }

  private fun bind(row: NativeListMarketRowView, item: NativeListItem) {
    row.bind(item, null, "linear", "vertical", null, false, { _, _, fallback -> fallback }, true)
  }

  private fun withMarketRow(action: (NativeListMarketRowView) -> Unit) {
    val activity = instrumentation.startActivitySync(
      Intent(instrumentation.context, NativeListReorderTestActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    instrumentation.runOnMainSync {
      SoLoader.init(activity, OpenSourceMergedSoMapping)
      OneKeyLog.init(activity)
      val context = ThemedReactContext(BridgeReactContext(activity), activity, "MarketMetricsTest", 1)
      val row = NativeListMarketRowView(context, NativeListFonts())
      try {
        activity.setContentView(row)
        action(row)
      } finally {
        activity.setContentView(View(activity))
        row.dispose()
        activity.finish()
      }
    }
  }
}
