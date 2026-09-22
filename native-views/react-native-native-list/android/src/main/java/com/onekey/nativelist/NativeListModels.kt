package com.margelo.nitro.nativelist

import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Locale

internal fun isNativeListRowPressEnabled(
  type: String,
  variant: String,
  disabled: Boolean,
  pressDisabled: Boolean,
): Boolean = !disabled && !pressDisabled && (type != "system" || variant == "retry")

internal fun isNativeListWholeRowInteractive(
  type: String,
  variant: String,
  disabled: Boolean,
  pressDisabled: Boolean,
): Boolean = type != "walletGroup" && isNativeListRowPressEnabled(
  type = type,
  variant = variant,
  disabled = disabled,
  pressDisabled = pressDisabled,
)

/**
 * Maps a style key - which names a model field - to the view slot that renders it.
 * The view pool is shared across templates and the mapping is not one to one:
 * metricCard renders `value` through the title view and its own `title` through
 * the subtitle view, and one status view carries rail.status, activity.status,
 * message.time and metricCard.trend. See docs/STYLE_SPEC.md section 4.
 *
 * Returns null when the template does not render that field, so an unmapped key
 * is ignored rather than reaching an unrelated view.
 */
internal fun nativeListStyleSlot(type: String, variant: String, field: String): String? =
  when (type) {
    "identity" -> when (field) {
      "title", "subtitle", "tertiary", "badge", "value", "valueSecondary" -> field
      else -> null
    }
    "rail" -> when (field) {
      "title", "badge", "status" -> field
      else -> null
    }
    "activity" -> when (field) {
      "title", "status" -> field
      "description" -> "subtitle"
      "primaryAmount" -> "value"
      "secondaryAmount" -> "valueSecondary"
      else -> null
    }
    "message" -> when (field) {
      "title" -> "title"
      "body" -> "subtitle"
      "time" -> "status"
      else -> null
    }
    "dataRow" -> when (field) {
      "columns" -> "dataPrimary"
      "columnSecondary" -> "dataSecondary"
      "index" -> "value"
      else -> null
    }
    "mediaTile" -> when (field) {
      "title", "subtitle" -> field
      "badge" -> "mediaBadge"
      else -> null
    }
    // The large number and the small label sit in swapped views.
    "metricCard" -> when (field) {
      "value" -> "title"
      "title" -> "subtitle"
      "subtitle" -> "metricSubtitle"
      "trend" -> "status"
      else -> null
    }
    "sectionHeader" -> when (field) {
      "title", "subtitle", "value" -> field
      else -> null
    }
    "action" -> when (field) {
      "title", "value" -> field
      else -> null
    }
    "system" -> when (field) {
      "title" -> if (variant == "warning") "title" else null
      // Only the warning variant renders a separate title; every other variant
      // puts its message in the title view.
      "message" -> if (variant == "warning") "subtitle" else "title"
      "actionText" -> "value"
      else -> null
    }
    // Market owns its own richer style path.
    else -> null
  }

// The image fallback-state cache is process-wide, so it is keyed by a digest of the request
// identity instead of the raw headers, which can carry credentials such as Authorization.
internal fun nativeListSourceFallbackStateKey(uri: String, headers: Map<String, String>): String? {
  val trimmedUri = uri.trim().takeIf(String::isNotEmpty) ?: return null
  val digest = MessageDigest.getInstance("SHA-256")
  updateLengthPrefixed(digest, trimmedUri)
  headers.entries
    .map { it.key.lowercase(Locale.ROOT) to it.value }
    .sortedWith(compareBy<Pair<String, String>>({ it.first }, { it.second }))
    .forEach { (name, value) ->
      updateLengthPrefixed(digest, name)
      updateLengthPrefixed(digest, value)
    }
  return digest.digest().joinToString("") { "%02x".format(Locale.ROOT, it.toInt() and 0xff) }
}

private fun updateLengthPrefixed(digest: MessageDigest, value: String) {
  val bytes = value.toByteArray(Charsets.UTF_8)
  digest.update(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(bytes.size).array())
  digest.update(bytes)
}

internal data class NativeListItem(
  val key: String,
  val type: String,
  val sectionKey: String?,
  val revision: Int,
  val json: JSONObject,
) {
  val content: String = json.toString()
  // OneKey patch: only a host-validated stable snapshot can request a lightweight diff payload.
  var selectionUpdateFromContent: String? = null

  // OneKey patch: migrated selectors keep source dimensions on narrow Android screens.
  val usesSelectorSourceScale: Boolean
    get() = if (type == "walletGroup") json.optJSONObject("parent")?.has("height") == true else json.has("height") && json.optString("presentation") in setOf("accountSelector", "networkSelector", "walletSidebar")

  val isSelectable: Boolean
    get() = !json.optBoolean("disabled", false) && type in SELECTABLE_TYPES

  val isRowPressEnabled: Boolean
    get() = isNativeListRowPressEnabled(
      type = type,
      variant = json.optString("variant"),
      disabled = json.optBoolean("disabled", false),
      pressDisabled = json.optBoolean("pressDisabled", false),
    )

  val isWholeRowInteractive: Boolean
    get() = isNativeListWholeRowInteractive(
      type = type,
      variant = json.optString("variant"),
      disabled = json.optBoolean("disabled", false),
      pressDisabled = json.optBoolean("pressDisabled", false),
    )

  val isReorderable: Boolean
    get() {
      if (json.optBoolean("disabled", false)) return false
      if (type == "rail" && json.optBoolean("draggable", false)) return true
      if (type == "identity" && json.optBoolean("draggable", false)) return true
      if (type == "walletGroup" && json.optBoolean("draggable", false)) return true
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
  val sectionIndexCenteredInWindow: Boolean,
  val theme: JSONObject?,
  val listStyle: JSONObject?,
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
        sectionIndexCenteredInWindow = sectionIndex?.optBoolean("centeredInWindow", false) ?: false,
        theme = root.optJSONObject("theme"),
        listStyle = root.optJSONObject("listStyle"),
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
