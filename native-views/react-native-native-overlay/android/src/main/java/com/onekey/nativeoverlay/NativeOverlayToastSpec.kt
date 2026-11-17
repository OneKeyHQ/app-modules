package com.onekey.nativeoverlay

import android.graphics.Color
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.roundToInt

/** Mirrors `src/toast/types.ts`: everything a toast card paints, as data. */
internal object ToastColor {
  /** Parses the CSS colour forms the JS theme resolves to. */
  fun parse(value: String?): Int? {
    val raw = value?.trim()?.lowercase()?.takeIf { it.isNotEmpty() } ?: return null
    if (raw == "transparent") return Color.TRANSPARENT
    if (raw.startsWith("#")) return hex(raw.substring(1))
    val open = raw.indexOf('(')
    val close = raw.lastIndexOf(')')
    if (open < 0 || close < open) return null
    val name = raw.substring(0, open)
    val parts = raw.substring(open + 1, close).split(',', ' ', '/').filter { it.isNotEmpty() }
    fun number(text: String, scale: Float): Float? =
      if (text.endsWith("%")) text.dropLast(1).toFloatOrNull()?.div(100f)
      else text.toFloatOrNull()?.div(scale)
    if ((name == "rgb" || name == "rgba") && parts.size >= 3) {
      val r = number(parts[0], 255f) ?: return null
      val g = number(parts[1], 255f) ?: return null
      val b = number(parts[2], 255f) ?: return null
      val a = if (parts.size > 3) number(parts[3], 1f) ?: 1f else 1f
      return argb(a, r, g, b)
    }
    if ((name == "hsl" || name == "hsla") && parts.size >= 3) {
      val h = parts[0].removeSuffix("deg").toFloatOrNull() ?: return null
      val s = number(parts[1], 100f) ?: return null
      val l = number(parts[2], 100f) ?: return null
      val a = if (parts.size > 3) number(parts[3], 1f) ?: 1f else 1f
      val chroma = (1 - abs(2 * l - 1)) * s
      val sector = (((h % 360) + 360) % 360) / 60f
      val x = chroma * (1 - abs(sector % 2 - 1))
      val (r, g, b) = when (sector.toInt()) {
        0 -> Triple(chroma, x, 0f)
        1 -> Triple(x, chroma, 0f)
        2 -> Triple(0f, chroma, x)
        3 -> Triple(0f, x, chroma)
        4 -> Triple(x, 0f, chroma)
        else -> Triple(chroma, 0f, x)
      }
      val m = l - chroma / 2
      return argb(a, r + m, g + m, b + m)
    }
    return null
  }

  private fun argb(a: Float, r: Float, g: Float, b: Float): Int = Color.argb(
    (a.coerceIn(0f, 1f) * 255).roundToInt(),
    (r.coerceIn(0f, 1f) * 255).roundToInt(),
    (g.coerceIn(0f, 1f) * 255).roundToInt(),
    (b.coerceIn(0f, 1f) * 255).roundToInt(),
  )

  private fun hex(text: String): Int? {
    val digits = if (text.length == 3 || text.length == 4) {
      text.map { "$it$it" }.joinToString("")
    } else {
      text
    }
    val value = digits.toLongOrNull(16) ?: return null
    return when (digits.length) {
      6 -> Color.rgb((value shr 16 and 0xff).toInt(), (value shr 8 and 0xff).toInt(), (value and 0xff).toInt())
      8 -> Color.argb(
        (value and 0xff).toInt(),
        (value shr 24 and 0xff).toInt(),
        (value shr 16 and 0xff).toInt(),
        (value shr 8 and 0xff).toInt(),
      )
      else -> null
    }
  }

  /** Formats a colour into the strict `rgba(r,g,b,a)` string the named-icon registry accepts. */
  fun rgba(color: Int): String {
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)
    val a = Color.alpha(color)
    val alpha = when (a) {
      255 -> "1"
      0 -> "0"
      else -> String.format(java.util.Locale.US, "%.4f", a / 255.0).trimEnd('0').trimEnd('.')
    }
    return "rgba($r,$g,$b,$alpha)"
  }
}

private fun JSONObject.float(key: String, fallback: Float): Float =
  if (has(key) && !isNull(key)) optDouble(key, fallback.toDouble()).toFloat() else fallback

private fun JSONObject.stringOrNull(key: String): String? =
  if (has(key) && !isNull(key)) optString(key) else null

private fun JSONObject.faces(key: String): Map<String, String> {
  val faces = optJSONObject(key) ?: return emptyMap()
  return faces.keys().asSequence().associateWith { faces.optString(it) }
}

private fun <T> JSONArray?.mapObjects(transform: (JSONObject) -> T?): List<T> {
  this ?: return emptyList()
  return (0 until length()).mapNotNull { optJSONObject(it)?.let(transform) }
}

internal data class ToastTextRun(
  val text: String,
  val color: Int?,
  val fontWeight: String?,
  val fontSize: Float?,
  val underline: Boolean,
  val link: String?,
) {
  companion object {
    fun parse(json: JSONObject): ToastTextRun = ToastTextRun(
      text = json.optString("text"),
      color = ToastColor.parse(json.stringOrNull("color")),
      fontWeight = json.stringOrNull("fontWeight"),
      fontSize = if (json.has("fontSize")) json.float("fontSize", 14f) else null,
      underline = json.optBoolean("underline", false),
      link = json.stringOrNull("link"),
    )
  }
}

internal data class ToastText(
  val paragraphs: List<List<ToastTextRun>>,
  val color: Int,
  val fontSize: Float,
  val lineHeight: Float,
  val fontWeight: String,
  val letterSpacing: Float,
  val fontFamily: String?,
  /** Font name per weight, for typefaces that ship one face per weight. */
  val fontFaces: Map<String, String>,
  val tabularNumbers: Boolean,
  val numberOfLines: Int,
) {
  companion object {
    fun parse(json: JSONObject?): ToastText? {
      json ?: return null
      val paragraphs = json.optJSONArray("paragraphs")
      return ToastText(
        paragraphs = (0 until (paragraphs?.length() ?: 0)).map { index ->
          paragraphs?.optJSONArray(index).mapObjects(ToastTextRun::parse)
        },
        color = ToastColor.parse(json.stringOrNull("color")) ?: Color.BLACK,
        fontSize = json.float("fontSize", 14f),
        lineHeight = json.float("lineHeight", 20f),
        fontWeight = json.optString("fontWeight", "400"),
        letterSpacing = json.float("letterSpacing", 0f),
        fontFamily = json.stringOrNull("fontFamily"),
        fontFaces = json.faces("fontFaces"),
        tabularNumbers = json.optBoolean("tabularNumbers", false),
        numberOfLines = json.optInt("numberOfLines", 0),
      )
    }
  }
}

internal sealed class ToastIcon {
  abstract val size: Float

  data class Named(val name: String, val colorString: String, val color: Int, override val size: Float) :
    ToastIcon()

  data class Spinner(val color: Int, override val size: Float) : ToastIcon()

  data class Image(val uri: String, override val size: Float, val radius: Float) : ToastIcon()

  companion object {
    fun parse(json: JSONObject?): ToastIcon? {
      json ?: return null
      val size = json.float("size", 20f)
      return when (json.optString("kind")) {
        "icon" -> {
          val color = ToastColor.parse(json.stringOrNull("color")) ?: Color.BLACK
          Named(json.optString("name"), ToastColor.rgba(color), color, size)
        }
        "spinner" -> Spinner(ToastColor.parse(json.stringOrNull("color")) ?: Color.GRAY, size)
        "image" -> Image(json.optString("uri"), size, json.float("borderRadius", 0f))
        else -> null
      }
    }
  }
}

internal data class ToastAction(
  val id: String,
  val label: String,
  val disabled: Boolean,
  val testID: String?,
  val icon: ToastIcon?,
  val iconGap: Float,
  val color: Int,
  val backgroundColor: Int,
  val pressedBackgroundColor: Int?,
  val fontSize: Float,
  val lineHeight: Float,
  val fontWeight: String,
  val fontFamily: String?,
  val fontFaces: Map<String, String>,
  val paddingHorizontal: Float,
  val paddingVertical: Float,
  val marginHorizontal: Float,
  val marginVertical: Float,
  val borderWidth: Float,
  val borderRadius: Float,
  val disabledOpacity: Float,
) {
  companion object {
    fun parse(json: JSONObject): ToastAction = ToastAction(
      id = json.optString("id"),
      label = json.optString("label"),
      disabled = json.optBoolean("disabled", false),
      testID = json.stringOrNull("testID"),
      icon = ToastIcon.parse(json.optJSONObject("icon")),
      iconGap = json.float("iconGap", 0f),
      color = ToastColor.parse(json.stringOrNull("color")) ?: Color.BLACK,
      backgroundColor = ToastColor.parse(json.stringOrNull("backgroundColor")) ?: Color.TRANSPARENT,
      pressedBackgroundColor = ToastColor.parse(json.stringOrNull("pressedBackgroundColor")),
      fontSize = json.float("fontSize", 14f),
      lineHeight = json.float("lineHeight", 20f),
      fontWeight = json.optString("fontWeight", "500"),
      fontFamily = json.stringOrNull("fontFamily"),
      fontFaces = json.faces("fontFaces"),
      paddingHorizontal = json.float("paddingHorizontal", 0f),
      paddingVertical = json.float("paddingVertical", 0f),
      marginHorizontal = json.float("marginHorizontal", 0f),
      marginVertical = json.float("marginVertical", 0f),
      borderWidth = json.float("borderWidth", 0f),
      borderRadius = json.float("borderRadius", 0f),
      disabledOpacity = json.float("disabledOpacity", 1f),
    )
  }
}

internal data class ToastSpec(
  val id: String,
  val isNotification: Boolean,
  /** Milliseconds; negative never times out. */
  val duration: Double,
  val testID: String?,
  val icon: ToastIcon?,
  val title: ToastText?,
  val message: ToastText?,
  val actions: List<ToastAction>,
  val actionsAlignLeft: Boolean,
  val badgeColor: Int,
  val badgeSize: Float,
  val image: ToastIcon.Image?,
  val pressable: Boolean,
) {
  companion object {
    fun parse(json: JSONObject): ToastSpec = ToastSpec(
      id = json.optString("id"),
      isNotification = json.optString("layout") == "notification",
      duration = json.optDouble("duration", 5000.0),
      testID = json.stringOrNull("testID"),
      icon = ToastIcon.parse(json.optJSONObject("icon")),
      title = ToastText.parse(json.optJSONObject("title")),
      message = ToastText.parse(json.optJSONObject("message")),
      actions = json.optJSONArray("actions").mapObjects(ToastAction::parse),
      actionsAlignLeft = json.optString("actionsAlign") == "left",
      badgeColor = ToastColor.parse(json.stringOrNull("badgeColor")) ?: Color.TRANSPARENT,
      badgeSize = json.float("badgeSize", 28f),
      image = json.optJSONObject("image")?.let {
        ToastIcon.Image(it.optString("uri"), it.float("size", 48f), it.float("borderRadius", 0f))
      },
      pressable = json.optBoolean("pressable", false),
    )
  }
}

internal data class ToastItem(
  val spec: ToastSpec,
  val visible: Boolean,
  val revision: Int,
  val generation: Int,
) {
  companion object {
    fun parseList(json: String?): List<ToastItem> {
      if (json.isNullOrEmpty()) return emptyList()
      val list = runCatching { JSONArray(json) }.getOrNull() ?: return emptyList()
      return list.mapObjects { entry ->
        val spec = entry.optJSONObject("spec") ?: return@mapObjects null
        ToastItem(
          spec = ToastSpec.parse(spec),
          visible = entry.optBoolean("visible", true),
          revision = entry.optInt("revision", 0),
          generation = entry.optInt("generation", 0),
        )
      }
    }
  }
}

internal data class ToasterConfig(
  val cardColor: Int = Color.WHITE,
  /** Width of the card's content box in dp; null derives it from the window. */
  val contentWidth: Float? = null,
  val contentMaxHeight: Float? = null,
  val topObstruction: Float = 0f,
  val topObstructionGap: Float = 8f,
  val topObstructionRoom: Float = 88f,
  val respectReducedMotion: Boolean = true,
) {
  companion object {
    fun parse(json: String?): ToasterConfig {
      if (json.isNullOrEmpty()) return ToasterConfig()
      val dict = runCatching { JSONObject(json) }.getOrNull() ?: return ToasterConfig()
      return ToasterConfig(
        cardColor = ToastColor.parse(dict.stringOrNull("cardColor")) ?: Color.WHITE,
        contentWidth = if (dict.has("contentWidth")) dict.float("contentWidth", 0f) else null,
        contentMaxHeight = if (dict.has("contentMaxHeight")) dict.float("contentMaxHeight", 0f) else null,
        topObstruction = dict.float("topObstruction", 0f),
        topObstructionGap = dict.float("topObstructionGap", 8f),
        topObstructionRoom = dict.float("topObstructionRoom", 88f),
        respectReducedMotion = dict.optBoolean("respectReducedMotion", true),
      )
    }
  }
}
