package com.margelo.nitro.nativelist

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeListMarketMetricStructureTest {
  private fun item(metrics: JSONArray? = null): NativeListItem {
    val json = JSONObject()
      .put("type", "market")
      .put("key", "market-eth")
      .put("variant", "token")
      .put("title", "ETH")
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

  @Test
  fun textOnlyQuoteUpdateKeepsStructureWhileColumnChangesRequireRebind() {
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
}
