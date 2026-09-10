package com.margelo.nitro.nativelist

import org.json.JSONObject

/** Only text and tone inside existing subtitle slots may change without rebuilding an identity row. */
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
    return data.toString()
  }

  val previousStructure = structure(previous) ?: return false
  return previousStructure == structure(next)
}
