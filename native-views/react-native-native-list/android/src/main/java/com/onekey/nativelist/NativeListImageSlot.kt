package com.margelo.nitro.nativelist

import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.onekeyimage.OneKeyImageReusableView
import org.json.JSONArray
import org.json.JSONObject

internal fun nativeListCanonical(value: Any?): String =
  when (value) {
    is JSONObject ->
      value.keys().asSequence().sorted().joinToString(",", "{", "}") {
        JSONObject.quote(it) + ":" + nativeListCanonical(value.opt(it))
      }
    is JSONArray ->
      (0 until value.length()).joinToString(",", "[", "]") { nativeListCanonical(value.opt(it)) }
    is String -> JSONObject.quote(value)
    else -> value?.toString() ?: "null"
  }

/** A request survives text-only binding; source/row/slot changes invalidate it. */
internal class NativeListImageSlot(context: ThemedReactContext) {
  val view = OneKeyImageReusableView(context)
  private var identity: String? = null
  private var epoch = 0
  private var retry: Runnable? = null
  private var completion: ((Boolean) -> Unit)? = null
  private var result: Boolean? = null

  fun bind(
    source: JSONObject,
    key: String,
    variant: String = "generic",
    fit: String? = null,
    placeholder: String = "#0000000F",
    round: Boolean = false,
    completion: ((Boolean) -> Unit)? = null,
  ) {
    this.completion = completion
    val next =
      nativeListCanonical(
        JSONObject()
          .put("source", source)
          .put("key", key)
          .put("variant", variant)
          .put("fit", fit ?: source.optString("contentFit", "cover"))
          .put("placeholder", placeholder)
          .put("round", round)
      )
    if (identity == next) {
      result?.let { completion?.invoke(it) }
      return
    }
    recycle()
    this.completion = completion
    identity = next
    configure(source, key, variant, fit, placeholder, round, 0, epoch)
  }

  private fun configure(
    source: JSONObject,
    key: String,
    variant: String,
    fit: String?,
    placeholder: String,
    round: Boolean,
    attempt: Int,
    token: Int,
  ) {
    if (epoch != token) return
    var completed = false
    view.configure(
      source.optString("uri").trim(),
      source.optJSONObject("headers")?.toString(),
      variant,
      fit ?: source.optString("contentFit", "cover"),
      source.optString("cachePolicy", "memory-disk"),
      source.optBoolean("autoplay"),
      if (attempt == 0) key else "$key:retry:$attempt",
      attempt == 0 && source.optBoolean("optimizeTos", true),
      source.optDouble("overscan", 1.1),
      source.optString("loadingStrategy", "none"),
      placeholder,
      round = round,
      onLoad = {
        if (epoch == token && !completed) {
          completed = true
          retry?.let(view::removeCallbacks)
          retry = null
          result = true
          completion?.invoke(true)
        }
      },
      onError = {
        if (epoch == token && !completed) {
          completed = true
          result = false
          completion?.invoke(false)
          if (attempt < source.optInt("retryTimes") && retry == null) {
            val task = Runnable {
              if (epoch == token) {
                retry = null
                configure(source, key, variant, fit, placeholder, round, attempt + 1, token)
              }
            }
            retry = task
            view.postDelayed(task, (0..2).random() * 1000L)
          }
        }
      },
    )
  }

  fun recycle() {
    epoch++
    retry?.let(view::removeCallbacks)
    retry = null
    identity = null
    completion = null
    result = null
    view.prepareForReuse()
  }

  fun dispose() {
    recycle()
    view.dispose()
  }
}

internal object NativeListSourceFallbackState {
  private const val CACHE_LIMIT = 128
  private val sources = LinkedHashMap<String, Unit>(CACHE_LIMIT, 0.75f, true)

  fun has(key: String): Boolean = synchronized(sources) { sources[key] != null }

  fun remember(key: String) {
    synchronized(sources) {
      sources[key] = Unit
      while (sources.size > CACHE_LIMIT) {
        sources.remove(sources.entries.first().key)
      }
    }
  }

  fun forget(key: String) {
    synchronized(sources) { sources.remove(key) }
  }
}
