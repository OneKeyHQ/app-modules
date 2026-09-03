package com.margelo.nitro.onekeyimage

import android.net.Uri
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import org.json.JSONObject
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Locale

internal object OneKeyImageModel {
  fun headers(json: String?): Map<String, String> {
    if (json.isNullOrBlank()) return emptyMap()
    return try {
      val objectValue = JSONObject(json)
      buildMap {
        objectValue.keys().forEach { key ->
          val value = objectValue.opt(key)
          if (value is String) put(key, value)
        }
      }
    } catch (_: Exception) {
      emptyMap()
    }
  }

  fun build(uri: String, headersJson: String?): Any {
    if (uri.startsWith("data:")) return OneKeyImageDataUriModel(uri)
    if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
      return OneKeyImageLocalModel(Uri.parse(uri))
    }
    val parsedHeaders = headers(headersJson)
    val headerBuilder = LazyHeaders.Builder()
    parsedHeaders.forEach { (name, value) -> headerBuilder.addHeader(name, value) }
    val headers = headerBuilder.build()
    return OneKeyImageRemoteModel(
      glideUrl = GlideUrl(uri, headers),
      headersDigest = remoteHeadersDigest(parsedHeaders),
    )
  }

  internal fun remoteHeadersDigest(headers: Map<String, String>): String? {
    if (headers.isEmpty()) return null
    val digest = MessageDigest.getInstance("SHA-256")
    headers.entries
      .map { it.key.lowercase(Locale.ROOT) to it.value }
      .sortedWith(compareBy<Pair<String, String>>({ it.first }, { it.second }))
      .forEach { (name, value) ->
        updateLengthPrefixed(digest, name.toByteArray(Charsets.UTF_8))
        updateLengthPrefixed(digest, value.toByteArray(Charsets.UTF_8))
      }
    return digest.digest().joinToString("") { "%02x".format(Locale.ROOT, it.toInt() and 0xff) }
  }

  private fun updateLengthPrefixed(digest: MessageDigest, value: ByteArray) {
    digest.update(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(value.size).array())
    digest.update(value)
  }
}

internal data class OneKeyImageRemoteModel(
  val glideUrl: GlideUrl,
  val headersDigest: String?,
)

internal data class OneKeyImageLocalModel(val uri: Uri)

internal data class OneKeyImageDataUriModel(val dataUri: String)
