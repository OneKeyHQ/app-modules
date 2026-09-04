package com.margelo.nitro.nativelist

import org.json.JSONArray
import org.json.JSONObject

internal data class NativeListItem(
  val key: String,
  val type: String,
  val sectionKey: String?,
  val revision: Int,
  val json: JSONObject,
) {
  val content: String = json.toString()

  val isSelectable: Boolean
    get() = !json.optBoolean("disabled", false) && type in SELECTABLE_TYPES

  val isReorderable: Boolean
    get() {
      if (json.optBoolean("disabled", false)) return false
      if (type == "rail" && json.optBoolean("draggable", false)) return true
      val trailing = json.optJSONArray("trailing") ?: return false
      return (0 until trailing.length()).any { index ->
        trailing.optJSONObject(index)?.optString("kind") == "drag"
      }
    }

  companion object {
    private val SELECTABLE_TYPES = setOf(
      "identity",
      "rail",
      "activity",
      "message",
      "dataRow",
      "mediaTile",
      "metricCard",
    )

    fun parse(json: JSONObject): NativeListItem {
      val key = json.getString("key")
      val type = json.getString("type")
      return NativeListItem(
        key = key,
        type = type,
        sectionKey = json.optString("sectionKey").takeIf { it.isNotEmpty() },
        revision = json.optInt("revision", 0),
        json = json,
      )
    }
  }
}

internal data class NativeListConfig(
  val generation: Int,
  val layout: String,
  val orientation: String,
  val gridColumns: Int,
  val stickyHeaders: Boolean,
  val contentPadding: Int,
  val contentPaddingHorizontal: Int?,
  val contentPaddingTop: Int?,
  val contentPaddingBottom: Int?,
  val itemSpacing: Int,
  val selectionMode: String,
  val rowPressToggles: Boolean,
  val selectedKeys: LinkedHashSet<String>,
  val reorderable: Boolean,
  val pullToRefresh: Boolean,
  val refreshing: Boolean,
  val loadMore: Boolean,
  val endReachedThreshold: Double,
  val sectionIndexEnabled: Boolean,
  val sectionIndexHapticsEnabled: Boolean,
  val theme: JSONObject?,
  val fixedFooter: NativeListItem?,
  val items: List<NativeListItem>,
) {
  companion object {
    fun parse(snapshotJson: String): NativeListConfig {
      val root = JSONObject(snapshotJson)
      require(root.getInt("schemaVersion") == 1) { "Unsupported schemaVersion" }
      val layout = root.getJSONObject("layout")
      val selection = root.optJSONObject("selection")
      val capabilities = root.optJSONObject("capabilities")
      val sectionIndex = capabilities?.optJSONObject("sectionIndex")
      val rowArray = root.getJSONArray("rows")
      val items = ArrayList<NativeListItem>(rowArray.length())
      val keys = HashSet<String>(rowArray.length())
      for (index in 0 until rowArray.length()) {
        val item = NativeListItem.parse(rowArray.getJSONObject(index))
        require(keys.add(item.key)) { "Duplicate row key: ${item.key}" }
        items.add(item)
      }
      if (items.isEmpty()) {
        root.optJSONObject("emptyState")?.let { items.add(NativeListItem.parse(it)) }
      }
      val selectedKeys = LinkedHashSet<String>()
      selection?.optJSONArray("selectedKeys")?.forEachString { selectedKeys.add(it) }
      require(selectedKeys.all(keys::contains)) { "Selection contains an unknown row key" }
      val fixedFooter = root.optJSONObject("fixedFooter")?.let(NativeListItem::parse)
      return NativeListConfig(
        generation = root.getInt("generation"),
        layout = layout.getString("kind"),
        orientation = layout.optString("orientation", "vertical"),
        gridColumns = layout.optInt("gridColumns", 1).coerceIn(1, 4),
        stickyHeaders = layout.optBoolean("stickyHeaders", false),
        contentPadding = layout.optInt("contentPadding", 0).coerceAtLeast(0),
        contentPaddingHorizontal = layout.optionalNonNegativeInt("contentPaddingHorizontal"),
        contentPaddingTop = layout.optionalNonNegativeInt("contentPaddingTop"),
        contentPaddingBottom = layout.optionalNonNegativeInt("contentPaddingBottom"),
        itemSpacing = layout.optInt("itemSpacing", 0).coerceAtLeast(0),
        selectionMode = selection?.optString("mode", "none") ?: "none",
        rowPressToggles = selection?.optBoolean("rowPressToggles", false) ?: false,
        selectedKeys = selectedKeys,
        reorderable = capabilities?.optBoolean("reorderable", false) ?: false,
        pullToRefresh = capabilities?.optBoolean("pullToRefresh", false) ?: false,
        refreshing = capabilities?.optBoolean("refreshing", false) ?: false,
        loadMore = capabilities?.optBoolean("loadMore", false) ?: false,
        endReachedThreshold = capabilities?.optDouble("endReachedThreshold", 0.2) ?: 0.2,
        sectionIndexEnabled = sectionIndex?.optBoolean("enabled", false) ?: false,
        sectionIndexHapticsEnabled = sectionIndex?.optBoolean("hapticsEnabled", true) ?: true,
        theme = root.optJSONObject("theme"),
        fixedFooter = fixedFooter,
        items = items,
      )
    }
  }
}

private fun JSONObject.optionalNonNegativeInt(key: String): Int? =
  if (has(key) && !isNull(key)) optInt(key).coerceAtLeast(0) else null

internal inline fun JSONArray.forEachString(block: (String) -> Unit) {
  for (index in 0 until length()) block(getString(index))
}

internal fun mergeRow(original: JSONObject, changes: JSONObject): JSONObject {
  val result = JSONObject(original.toString())
  val keys = changes.keys()
  while (keys.hasNext()) {
    val key = keys.next()
    if (key != "key" && key != "type") result.put(key, changes.get(key))
  }
  return result
}
