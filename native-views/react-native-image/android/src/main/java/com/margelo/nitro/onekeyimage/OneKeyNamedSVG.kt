package com.margelo.nitro.onekeyimage

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONObject
import java.util.concurrent.Executors
import kotlin.math.roundToInt

/** Process-owned named resources; no React runtime or Activity is retained. */
object OneKeyNamedSVG {
  enum class RecolorMode { FIXED, SINGLE, MULTIPLE, RASTER }
  data class IconMetadata(
    val width: Float,
    val height: Float,
    val mode: RecolorMode,
  )
  data class RenderResult(val bitmap: Bitmap, val mode: RecolorMode,
    val sourceIdentity: String, val maskAlpha: Int) {
    fun canTint(color: String): Boolean {
      val value = parseColor(color) ?: return false
      return mode == RecolorMode.FIXED || mode == RecolorMode.SINGLE ||
        (mode == RecolorMode.MULTIPLE && Color.alpha(value) == maskAlpha)
    }
    fun tintColor(color: String): Int? {
      val value = parseColor(color) ?: return null
      return when(mode) {
        RecolorMode.SINGLE -> value
        RecolorMode.MULTIPLE -> value or (0xff shl 24)
        else -> null
      }
    }
    internal fun finalBitmap(color: String): Bitmap {
      val tint = tintColor(color) ?: return bitmap
      if (tint == Color.BLACK) return bitmap
      val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
      val paint = Paint().apply { colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN) }
      Canvas(output).drawBitmap(bitmap, 0f, 0f, paint)
      return output
    }
  }

  private data class Request(val assets: AssetManager, val name: String, val color: String,
    val width: Int, val height: Int, val scale: Float, val finalColor: Boolean, val complete: (RenderResult?) -> Unit)
  private val lock = Any()
  private val pending = LinkedHashMap<Any, Request>()
  private val cache = LinkedHashMap<String, RenderResult>(128, 0.75f, true)
  private var cacheBytes = 0L
  private var activeWorkers = 0
  private var manifest: JSONObject? = null
  private val manifestLock = Any()
  private val workers = Executors.newFixedThreadPool(2)
  private val main = Handler(Looper.getMainLooper())
  private val colorPattern = Regex("rgba\\(\\d{1,3},\\d{1,3},\\d{1,3},(?:0(?:\\.\\d+)?|1(?:\\.0+)?)\\)")

  private var manifestMissingLogged = false

  fun cancel(owner: Any) { synchronized(lock) { pending.remove(owner) } }

  /** Caller holds manifestLock. Hosts own the resources, so a miss is logged once. */
  private fun loadManifestLocked(assets: AssetManager): JSONObject? {
    manifest?.let { return it }
    return try {
      assets.open("icons/manifest.json").bufferedReader().use { JSONObject(it.readText()) }
        .also { manifest = it }
    } catch (error: Exception) {
      if (!manifestMissingLogged) {
        manifestMissingLogged = true
        Log.w("OneKeyIcon", "assets/icons/manifest.json is missing or invalid; named icons will not render", error)
      }
      null
    }
  }

  private fun modeOf(metadata: JSONObject): RecolorMode = try {
    RecolorMode.valueOf(metadata.optString("recolorMode", "raster").uppercase())
  } catch (_: IllegalArgumentException) {
    RecolorMode.RASTER
  }

  private data class RenderKey(val identity: String, val key: String, val maskAlpha: Int)

  private fun renderKey(name: String, digest: String, width: Int, height: Int, scale: Float,
    mode: RecolorMode, requestColor: String, color: Int): RenderKey {
    val identity = "$name|$digest|$width|$height|$scale"
    val maskAlpha = if (mode == RecolorMode.MULTIPLE) Color.alpha(color) else 255
    val colorKey = when(mode) {
      RecolorMode.RASTER -> requestColor
      RecolorMode.MULTIPLE -> maskAlpha.toString()
      else -> ""
    }
    return RenderKey(identity, "$identity|$mode|$colorKey", maskAlpha)
  }

  /**
   * Main-thread fast path: returns an already-decoded result without asset I/O or
   * manifest loading, so remounted and recycled views paint in the same frame.
   */
  fun cachedRender(name: String, color: String, width: Int, height: Int, scale: Float): RenderResult? {
    val value = parseColor(color) ?: return null
    val metadata = synchronized(manifestLock) { manifest?.optJSONObject(name) } ?: return null
    val digest = metadata.optString("digest").takeIf { it.isNotEmpty() } ?: return null
    val key = renderKey(name, digest, width, height, scale, modeOf(metadata), color, value).key
    return synchronized(lock) { cache[key] }
  }

  fun iconMetadata(assets: AssetManager, name: String): IconMetadata? {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return null
    val metadata = try {
      synchronized(manifestLock) {
        loadManifestLocked(assets)?.optJSONObject(trimmed)
      }
    } catch (_: Exception) {
      null
    } ?: return null
    val mode = modeOf(metadata)
    val viewBox = metadata.optJSONArray("viewBox")
    val vbWidth = if (viewBox != null && viewBox.length() >= 4) viewBox.optDouble(2, 24.0).toFloat() else 24f
    val vbHeight = if (viewBox != null && viewBox.length() >= 4) viewBox.optDouble(3, 24.0).toFloat() else 24f
    val width = if (vbWidth > 0f) vbWidth else 24f
    val height = if (vbHeight > 0f) vbHeight else 24f
    return IconMetadata(width = width, height = height, mode = mode)
  }

  fun request(owner: Any, assets: AssetManager, name: String, color: String,
    width: Int, height: Int, scale: Float, complete: (Bitmap?) -> Unit) {
    enqueue(owner, assets, name, color, width, height, scale, true) { complete(it?.bitmap) }
  }

  fun requestRender(owner: Any, assets: AssetManager, name: String, color: String,
    width: Int, height: Int, scale: Float, complete: (RenderResult?) -> Unit) {
    enqueue(owner, assets, name, color, width, height, scale, false, complete)
  }

  private fun enqueue(owner: Any, assets: AssetManager, name: String, color: String,
    width: Int, height: Int, scale: Float, finalColor: Boolean, complete: (RenderResult?) -> Unit) {
    synchronized(lock) {
      pending[owner] = Request(assets, name, color, width, height, scale, finalColor, complete)
      while (activeWorkers < 2 && activeWorkers < pending.size) {
        activeWorkers++
        workers.execute { drain() }
      }
    }
  }

  private fun parseColor(color: String): Int? {
    if (!colorPattern.matches(color)) return null
    val channels = color.removePrefix("rgba(").removeSuffix(")").split(',')
    val rgb = channels.take(3).map { it.toInt() }
    if (rgb.any { it !in 0..255 }) return null
    val alpha = (channels[3].toDouble() * 255).roundToInt()
    return Color.argb(alpha, rgb[0], rgb[1], rgb[2])
  }
  fun isValidColor(color: String): Boolean = parseColor(color) != null

  private fun drain() {
    while (true) {
      val request = synchronized(lock) {
        val first = pending.entries.firstOrNull()
        if (first == null) { activeWorkers--; null }
        else { pending.remove(first.key); first.value }
      } ?: return
      val bitmap = try {
        decode(request)?.let { result ->
          if (request.finalColor) result.copy(bitmap = result.finalBitmap(request.color), mode = RecolorMode.RASTER)
          else result
        }
      } catch (_: Throwable) { null } // OOM must not strand activeWorkers.
      main.post { request.complete(bitmap) }
    }
  }

  private fun decode(request: Request): RenderResult? {
    if (request.width !in 1..4096 || request.height !in 1..4096 ||
      request.width.toLong() * request.height > 4194304 || !request.scale.isFinite() ||
      request.scale <= 0) return null
    val color = parseColor(request.color) ?: return null
    val metadata = synchronized(manifestLock) {
      loadManifestLocked(request.assets)?.optJSONObject(request.name)
    } ?: return null
    val mode = modeOf(metadata)
    val (sourceIdentity, key, maskAlpha) = renderKey(request.name, metadata.getString("digest"),
      request.width, request.height, request.scale, mode, request.color, color)
    val paintColor = when(mode) {
      RecolorMode.FIXED, RecolorMode.SINGLE -> Color.BLACK
      RecolorMode.MULTIPLE -> Color.argb(maskAlpha, 0, 0, 0)
      RecolorMode.RASTER -> color
    }
    synchronized(lock) { cache[key]?.let { return it } }
    // AndroidSVG rgba quantizes with 256; SVG hex alpha preserves the exact CSS 8-bit channel.
    val svgColor = listOf(Color.red(paintColor), Color.green(paintColor), Color.blue(paintColor), Color.alpha(paintColor))
      .joinToString(separator = "", prefix = "#") { it.toString(16).padStart(2, '0') }
    val xml = request.assets.open("icons/${request.name}.svg").bufferedReader().use { it.readText() }
      .replace(Regex("((?:fill|stroke)\\s*=\\s*[\"'])currentColor([\"'])"), "$1${svgColor}$2")
    val bitmap = OneKeySVG.decode(xml.byteInputStream()).render(request.width, request.height)
    val result = RenderResult(bitmap, mode, sourceIdentity, maskAlpha)
    val bytes = bitmap.allocationByteCount.toLong()
    if (bytes <= 8388608) synchronized(lock) {
      cache.put(key, result)?.let { cacheBytes -= it.bitmap.allocationByteCount }
      cacheBytes += bytes
      while (cache.size > 128 || cacheBytes > 8388608) {
        val first = cache.entries.first()
        cacheBytes -= first.value.bitmap.allocationByteCount
        cache.remove(first.key)
      }
    }
    return result
  }
}
