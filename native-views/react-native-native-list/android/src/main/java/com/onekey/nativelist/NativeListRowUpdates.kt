package com.margelo.nitro.nativelist

import org.json.JSONObject

/** Compare account-selector content that the subtitle and selection binders cannot refresh. */
internal fun canUpdateIdentitySubtitle(previous: NativeListItem, next: NativeListItem): Boolean {
  if (previous.key != next.key || previous.type != "identity" || next.type != "identity") return false

  fun structure(item: NativeListItem): String? {
    val data = JSONObject(item.content)
    if (data.optString("presentation") != "accountSelector") return null
    val segments = data.optJSONArray("subtitleSegments") ?: return null
    if (segments.length() == 0) return null
    for (index in 0 until segments.length()) {
      val segment = segments.optJSONObject(index) ?: return null
      segment.remove("text")
      segment.remove("textSegments")
      segment.remove("tone")
    }
    data.remove("accessibilityLabel")
    // Selection chrome and the existing checkbox are refreshed on every bind.
    data.remove("selected")
    data.optJSONArray("trailing")?.let { accessories ->
      for (index in 0 until accessories.length()) {
        val accessory = accessories.optJSONObject(index) ?: continue
        if (accessory.optString("kind") == "checkbox") accessory.remove("state")
      }
    }
    return nativeListCanonical(data)
  }

  val previousStructure = structure(previous) ?: return false
  return previousStructure == structure(next)
}
