package com.margelo.nitro.onekeyimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.signature.ObjectKey
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.Executors

/** Native-only process cache. View slots retain their players and surfaces. */
class OneKeyMediaPreviewCache private constructor(context: Context, directory: File) {
  companion object {
    @Volatile private var instance: OneKeyMediaPreviewCache? = null
    fun get(context: Context): OneKeyMediaPreviewCache = instance ?: synchronized(this) {
      instance ?: OneKeyMediaPreviewCache(context.applicationContext,
        File(context.cacheDir, "onekey-media-preview-v1")).also { instance = it }
    }
    fun targetEdge(pixels: Int): Int = listOf(128, 256, 512, 1024).firstOrNull { it >= pixels } ?: 1024
    internal fun storageKey(uri: String, headers: Map<String, String>, edge: Int, fit: String): String {
      val fields = headers.entries.map { it.key.lowercase(java.util.Locale.ROOT) to it.value }
        .sortedWith(compareBy({ it.first }, { it.second }))
      val values = listOf("onekey-media-preview-v1", uri, edge.toString(), fit) + fields.flatMap { listOf(it.first, it.second) }
      val canonical = values.joinToString("") { "${it.toByteArray(Charsets.UTF_8).size}:$it" }
      return MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    }
    internal fun isolated(context: Context, directory: File) = OneKeyMediaPreviewCache(context, directory)
  }
  class Subscription internal constructor(internal val key: String,
    internal val image: (Bitmap) -> Unit, internal val extract: (UUID) -> Unit,
    internal val revoked: () -> Unit, internal val failure: () -> Unit) {
    internal var cancelled = false
  }
  private class Flight(val key: String, val storageKey: String, val policy: String,
    val memoryGeneration: Int, val diskGeneration: Int) {
    val subscribers = mutableListOf<Subscription>()
    var lease: UUID? = null
    var timeout: Runnable? = null
    var ready = false
  }
  private val main = Handler(Looper.getMainLooper())
  private val io = Executors.newSingleThreadExecutor { task -> Thread(task, "onekey-media-preview-io").apply { isDaemon = true } }
  private val disk = DiskLruCacheWrapper.create(directory, 64L * 1024 * 1024)
  private val memory = object : LruCache<String, Bitmap>(16 * 1024 * 1024) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount
  }
  private val flights = linkedMapOf<String, Flight>()
  private val queue = ArrayDeque<String>()
  private var active = 0
  private var pendingPersistenceBytes = 0
  private var memoryGeneration = 0
  private var diskGeneration = 0
  private fun checkMain() = check(Looper.myLooper() == Looper.getMainLooper())
  private fun memoryPolicy(policy: String) = policy == "memory" || policy == "memory-disk"
  private fun diskPolicy(policy: String) = policy == "disk" || policy == "memory-disk"

  fun request(uri: String, headers: Map<String, String>, edge: Int, fit: String, policy: String,
    onImage: (Bitmap) -> Unit, onExtract: (UUID) -> Unit, onRevoked: () -> Unit,
    onFailure: () -> Unit): Subscription {
    checkMain()
    val storageKey = storageKey(uri, headers, targetEdge(edge.coerceAtLeast(1)), fit)
    val key = "$storageKey|$policy"
    val subscriber = Subscription(key, onImage, onExtract, onRevoked, onFailure)
    flights[key]?.let { it.subscribers.add(subscriber); return subscriber }
    if (flights.size >= 130) { main.post { if (!subscriber.cancelled) subscriber.failure() }; return subscriber }
    val flight = Flight(key, storageKey, policy, memoryGeneration, diskGeneration)
    flight.subscribers.add(subscriber); flights[key] = flight
    if (memoryPolicy(policy)) memory.get(storageKey)?.let { bitmap ->
      main.post { finish(flight, bitmap) }; return subscriber
    }
    io.execute {
      val bitmap = if (diskPolicy(policy)) runCatching {
        disk.get(ObjectKey(storageKey))?.let { file ->
          if (file.length() > 8L * 1024 * 1024) null else {
            val data = file.inputStream().use { input ->
              val output = ByteArrayOutputStream()
              val buffer = ByteArray(8192)
              while (output.size() <= 8 * 1024 * 1024) {
                val count = input.read(buffer, 0, minOf(buffer.size, 8 * 1024 * 1024 + 1 - output.size()))
                if (count < 0) break
                output.write(buffer, 0, count)
              }
              output.toByteArray()
            }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            if (data.size > 8 * 1024 * 1024) null else {
              BitmapFactory.decodeByteArray(data, 0, data.size, bounds)
              if (bounds.outWidth in 1..1024 && bounds.outHeight in 1..1024)
                BitmapFactory.decodeByteArray(data, 0, data.size) else null
            }
          }
        }.also { if (it == null) disk.delete(ObjectKey(storageKey)) }
      }.getOrNull() else null
      main.post {
        if (flights[key] !== flight) return@post
        if (bitmap != null && flight.diskGeneration == diskGeneration) {
          if (memoryPolicy(policy) && flight.memoryGeneration == memoryGeneration) storeMemory(storageKey, bitmap)
          finish(flight, bitmap)
        } else { flight.ready = true; queue.addLast(key); pump() }
      }
    }
    return subscriber
  }
  fun cancel(subscription: Subscription?) {
    checkMain()
    if (subscription == null) return
    subscription.cancelled = true
    val flight = flights[subscription.key] ?: return
    val index = flight.subscribers.indexOf(subscription)
    if (index < 0) return
    val owned = index == 0 && flight.lease != null
    flight.subscribers.removeAt(index)
    if (owned) {
      flight.timeout?.let(main::removeCallbacks); flight.lease = null; active--
      subscription.revoked()
    }
    if (flight.subscribers.isEmpty()) {
      flights.remove(flight.key); queue.removeAll { it == flight.key }
    } else if (owned) queue.addFirst(flight.key)
    pump()
  }
  fun fail(lease: UUID) {
    checkMain(); flights.values.firstOrNull { it.lease == lease }?.let { finish(it, null) }
  }
  fun complete(lease: UUID, bitmap: Bitmap) {
    checkMain()
    val flight = flights.values.firstOrNull { it.lease == lease } ?: return
    if (bitmap.width !in 1..1024 || bitmap.height !in 1..1024) { finish(flight, null); return }
    if (memoryPolicy(flight.policy) && flight.memoryGeneration == memoryGeneration) storeMemory(flight.storageKey, bitmap)
    finish(flight, bitmap)
    val cost = bitmap.allocationByteCount
    if (!diskPolicy(flight.policy) || pendingPersistenceBytes + cost > 16 * 1024 * 1024) return
    pendingPersistenceBytes += cost
    io.execute {
      val data = runCatching { ByteArrayOutputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it); it.toByteArray()
      } }.getOrNull()
      main.post {
        if (flight.diskGeneration != diskGeneration || data == null || data.size > 8 * 1024 * 1024) {
          pendingPersistenceBytes -= cost; return@post
        }
        // Serial writes and clears seal the generation-check/write window.
        io.execute {
          runCatching { disk.put(ObjectKey(flight.storageKey)) { file ->
            file.outputStream().use { it.write(data) }; true
          } }
          main.post { pendingPersistenceBytes -= cost }
        }
      }
    }
  }
  fun clearMemory() { checkMain(); memoryGeneration++; memory.evictAll() }
  fun clearDisk(completion: () -> Unit) {
    checkMain(); diskGeneration++
    io.execute { runCatching { disk.clear() }; completion() }
  }
  private fun storeMemory(key: String, bitmap: Bitmap) {
    if (bitmap.allocationByteCount <= 16 * 1024 * 1024) memory.put(key, bitmap)
    while (memory.snapshot().size > 64) memory.remove(memory.snapshot().keys.first())
  }
  private fun pump() {
    while (active < 2 && queue.isNotEmpty()) {
      val flight = flights[queue.removeFirst()] ?: continue
      if (!flight.ready || flight.lease != null || flight.subscribers.isEmpty()) continue
      val lease = UUID.randomUUID(); flight.lease = lease; active++
      val timeout = Runnable { if (flight.lease == lease) finish(flight, null) }
      flight.timeout = timeout; main.postDelayed(timeout, 10_000)
      flight.subscribers.first().extract(lease)
    }
  }
  private fun finish(flight: Flight, bitmap: Bitmap?) {
    if (flights[flight.key] !== flight) return
    flights.remove(flight.key); queue.removeAll { it == flight.key }
    if (flight.lease != null) { active--; flight.subscribers.firstOrNull()?.revoked?.invoke() }
    flight.timeout?.let(main::removeCallbacks); flight.lease = null
    val subscribers = flight.subscribers.toList(); flight.subscribers.clear()
    subscribers.filterNot { it.cancelled }.forEach { if (bitmap != null) it.image(bitmap) else it.failure() }
    pump()
  }
}
