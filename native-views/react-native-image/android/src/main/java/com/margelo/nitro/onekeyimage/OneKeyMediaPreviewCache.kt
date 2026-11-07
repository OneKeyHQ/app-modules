package com.margelo.nitro.onekeyimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.signature.ObjectKey
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.Future

/** Native-only process cache. View slots retain their players and surfaces. */
class OneKeyMediaPreviewCache private constructor(
  context: Context,
  directory: File,
  private val cookieState: () -> Boolean? = { CookieManager.getInstance().hasCookies() },
) {
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
      val values = listOf("onekey-media-preview-v2", uri, edge.toString(), fit) + fields.flatMap { listOf(it.first, it.second) }
      val canonical = values.joinToString("") { "${it.toByteArray(Charsets.UTF_8).size}:$it" }
      return MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    }
    internal fun isolated(context: Context, directory: File, cookieState: () -> Boolean? = { false }) =
      OneKeyMediaPreviewCache(context, directory, cookieState)
  }
  class Subscription internal constructor(internal var key: String,
    internal val image: (Bitmap) -> Unit, internal val extract: (UUID) -> Unit,
    internal val revoked: () -> Unit, internal val failure: () -> Unit) {
    internal var cancelled = false
  }
  private class Flight(var key: String, val storageKey: String, val policy: String,
    val memoryGeneration: Int, val diskGeneration: Int, val network: Boolean) {
    val subscribers = mutableListOf<Subscription>()
    var lease: UUID? = null
    var timeout: Runnable? = null
    var ready = false
    @Volatile var reusable = false
    var authGeneration = 0L
    var isolated = false
    var admissionGeneration = 0L
    var authPending = false
    var authTask: Future<*>? = null
    var lookupTask: Future<*>? = null
    var authAction: (() -> Unit)? = null
    var authLease: UUID? = null
    val tainted = AtomicBoolean(false)
  }
  private data class AuthSnapshot(val eligible: Boolean, val generation: Long)
  private val authGeneration = AtomicLong(0)
  private val authLock = Any()
  private var lastCookieState: Boolean? = null
  private fun authSnapshot(): AuthSnapshot {
    // CookieManager can load Chromium on its first access; callers use the I/O worker.
    val present = runCatching { cookieState() }.getOrNull()
    synchronized(authLock) {
      if (present != lastCookieState) authGeneration.incrementAndGet()
      lastCookieState = present
      return AuthSnapshot(present == false, authGeneration.get())
    }
  }
  private fun eligible(flight: Flight, snapshot: AuthSnapshot): Boolean =
    flight.reusable && !flight.tainted.get() && (!flight.network ||
      (snapshot.eligible && snapshot.generation == flight.authGeneration && authGeneration.get() == snapshot.generation))

  /** Preserve the original jar while observing every redirect/range cookie access. */
  fun trackCookies(lease: UUID, delegate: CookieJar): CookieJar {
    checkMain()
    val flight = flights.values.firstOrNull { it.lease == lease }
    fun taint() {
      if (flight?.tainted?.compareAndSet(false, true) == true) authGeneration.incrementAndGet()
    }
    return object : CookieJar {
      override fun loadForRequest(url: HttpUrl): List<Cookie> = try {
        delegate.loadForRequest(url).also { if (it.isNotEmpty()) taint() }
      } catch (error: Exception) { taint(); throw error }
      override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) taint()
        delegate.saveFromResponse(url, cookies)
      }
    }
  }
  private val main = Handler(Looper.getMainLooper())
  private val io = ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>()) { task ->
    Thread(task, "onekey-media-preview-io").apply { isDaemon = true }
  }
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
    val existing = flights[key]
    if (existing != null && !existing.isolated) {
      if (existing.tainted.get()) makePrivate(existing)
      else {
        existing.subscribers.add(subscriber); existing.admissionGeneration++
        return subscriber
      }
    }
    if (flights.size >= 130) { main.post { if (!subscriber.cancelled) subscriber.failure() }; return subscriber }
    val network = uri.startsWith("http://", true) || uri.startsWith("https://", true)
    val flight = Flight(key, storageKey, policy, memoryGeneration, diskGeneration, network)
    flight.subscribers.add(subscriber); flights[key] = flight
    inspectAdmission(flight)
    return subscriber
  }
  private fun inspectAdmission(flight: Flight) {
    val generation = flight.admissionGeneration
    if (!flight.network) { admit(flight, AuthSnapshot(true, 0)); return }
    flight.authPending = true
    flight.authTask = io.submit {
      val snapshot = authSnapshot()
      main.post {
        if (flights[flight.key] !== flight) return@post
        flight.authPending = false; flight.authTask = null
        if (generation != flight.admissionGeneration) inspectAdmission(flight) else admit(flight, snapshot)
      }
    }
  }
  private fun admit(flight: Flight, snapshot: AuthSnapshot) {
    if (flights[flight.key] !== flight) return
    flight.reusable = snapshot.eligible; flight.authGeneration = snapshot.generation
    if (!flight.reusable) makePrivate(flight)
    lookup(flight)
  }
  private fun validate(flight: Flight, lease: UUID? = null, action: () -> Unit) {
    if (flights[flight.key] !== flight || (lease != null && flight.lease != lease)) return
    if (!flight.network || flight.isolated) {
      if (!eligible(flight, AuthSnapshot(true, 0))) makePrivate(flight)
      if (flights[flight.key] === flight && (lease == null || flight.lease == lease)) action()
      return
    }
    flight.authAction = action; flight.authLease = lease
    if (!flight.authPending) inspectValidation(flight)
  }
  private fun inspectValidation(flight: Flight) {
    val generation = flight.admissionGeneration; val lease = flight.authLease
    flight.authPending = true
    flight.authTask = io.submit {
      val snapshot = authSnapshot()
      main.post {
        if (flights[flight.key] !== flight) return@post
        flight.authPending = false; flight.authTask = null
        if (generation != flight.admissionGeneration || lease != flight.authLease) {
          inspectValidation(flight); return@post
        }
        val action = flight.authAction; flight.authAction = null
        if (lease != null && flight.lease != lease) return@post
        if (!eligible(flight, snapshot)) makePrivate(flight)
        action?.invoke()
      }
    }
  }
  private fun ready(flight: Flight) {
    if (flights[flight.key] !== flight) return
    if (!flight.ready) { flight.ready = true; queue.addLast(flight.key) }
    pump()
  }
  private fun lookup(flight: Flight) {
    val storageKey = flight.storageKey; val policy = flight.policy
    if (!flight.reusable) { main.post { ready(flight) }; return }
    if (memoryPolicy(policy)) memory.get(storageKey)?.let { bitmap ->
      main.post { validate(flight) { if (flight.reusable) finish(flight, bitmap) else ready(flight) } }; return
    }
    flight.lookupTask = io.submit {
      val bitmap = if (flight.reusable && diskPolicy(policy)) runCatching {
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
        validate(flight) {
          if (bitmap != null && flight.reusable && flight.diskGeneration == diskGeneration) {
            if (memoryPolicy(policy) && flight.memoryGeneration == memoryGeneration) storeMemory(storageKey, bitmap)
            finish(flight, bitmap)
          } else ready(flight)
        }
      }
    }
  }
  private fun makePrivate(flight: Flight) {
    if (flight.isolated) return
    flight.reusable = false; flight.isolated = true
    val oldKey = flight.key; val privateKey = UUID.randomUUID().toString()
    flights.remove(oldKey); flight.key = privateKey; flights[privateKey] = flight
    val ordered = queue.map { if (it == oldKey) privateKey else it }
    queue.clear(); queue.addAll(ordered)
    flight.subscribers.firstOrNull()?.key = privateKey
    if (flight.lease == null && !flight.ready) {
      flight.ready = true; queue.addLast(flight.key)
    }
    val waiting = flight.subscribers.drop(1)
    while (flight.subscribers.size > 1) flight.subscribers.removeAt(flight.subscribers.lastIndex)
    waiting.forEach { subscriber ->
      if (subscriber.cancelled) return@forEach
      if (flights.size >= 130) { main.post { if (!subscriber.cancelled) subscriber.failure() }; return@forEach }
      val key = UUID.randomUUID().toString()
      val isolated = Flight(key, flight.storageKey, flight.policy, memoryGeneration, diskGeneration, flight.network)
      subscriber.key = key; isolated.subscribers.add(subscriber); isolated.ready = true; isolated.isolated = true
      flights[key] = isolated; queue.addLast(key)
    }
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
      cancelAuth(flight)
      cancelLookup(flight)
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
    validate(flight, lease) {
      if (flight.reusable && memoryPolicy(flight.policy) && flight.memoryGeneration == memoryGeneration) storeMemory(flight.storageKey, bitmap)
      finish(flight, bitmap)
      persist(flight, bitmap)
    }
  }
  private fun persist(flight: Flight, bitmap: Bitmap) {
    val cost = bitmap.allocationByteCount
    if (!flight.reusable || !diskPolicy(flight.policy) || pendingPersistenceBytes + cost > 16 * 1024 * 1024) return
    pendingPersistenceBytes += cost
    io.execute {
      val data = runCatching { ByteArrayOutputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it); it.toByteArray()
      } }.getOrNull()
      main.post {
        if (flight.diskGeneration != diskGeneration || data == null || data.size > 8 * 1024 * 1024) {
          pendingPersistenceBytes -= cost; return@post
        }
        // Cookie checks stay on this worker, including the actual disk-write boundary.
        io.execute {
          val before = if (flight.network) authSnapshot() else AuthSnapshot(true, 0)
          if (eligible(flight, before)) {
            runCatching { disk.put(ObjectKey(flight.storageKey)) { file ->
              file.outputStream().use { it.write(data) }; true
            } }
            val after = if (flight.network) authSnapshot() else AuthSnapshot(true, 0)
            if (!eligible(flight, after)) disk.delete(ObjectKey(flight.storageKey))
          }
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
      validate(flight, lease) {
        val timeout = Runnable { if (flight.lease == lease) finish(flight, null) }
        flight.timeout = timeout; main.postDelayed(timeout, 10_000)
        flight.subscribers.first().extract(lease)
      }
    }
  }
  private fun cancelAuth(flight: Flight) {
    flight.authTask?.let { task -> task.cancel(false); (task as? Runnable)?.let(io::remove) }
    flight.authTask = null; flight.authAction = null
  }
  private fun cancelLookup(flight: Flight) {
    flight.lookupTask?.let { it.cancel(false); io.remove(it as Runnable) }
    flight.lookupTask = null
  }
  private fun finish(flight: Flight, bitmap: Bitmap?) {
    if (flights[flight.key] !== flight) return
    cancelAuth(flight)
    cancelLookup(flight)
    flights.remove(flight.key); queue.removeAll { it == flight.key }
    if (flight.lease != null) { active--; flight.subscribers.firstOrNull()?.revoked?.invoke() }
    flight.timeout?.let(main::removeCallbacks); flight.lease = null
    val subscribers = flight.subscribers.toList(); flight.subscribers.clear()
    subscribers.filterNot { it.cancelled }.forEach { if (bitmap != null) it.image(bitmap) else it.failure() }
    pump()
  }
}
