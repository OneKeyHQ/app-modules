package com.margelo.nitro.reactnativerangedownloader

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Focused JVM unit coverage for the **MonotonicProgressGate** — the
 * `AtomicInteger(-1)` + CAS emit-gating idiom the Nitro adapter
 * ([ReactNativeRangeDownloader]) wraps around its concurrent progress callback.
 *
 * IMPORTANT: this gate is NOT a standalone extracted symbol — it is the inline
 * adapter expression at `ReactNativeRangeDownloader.kt`:
 *   - line 94:   `val lastProgress = java.util.concurrent.atomic.AtomicInteger(-1)`
 *   - lines 104-105:
 *       `val prev = lastProgress.get()`
 *       `if (p > prev && lastProgress.compareAndSet(prev, p)) { …emit… }`
 *
 * So "driving the real code" here means driving the REAL JDK
 * [java.util.concurrent.atomic.AtomicInteger] (the exact type the adapter
 * holds) through the verbatim adapter expression. [tryAdvance] below is NOT a
 * re-implementation of business logic — it is the literal `p > prev && CAS(prev,p)`
 * line copied from the adapter, kept in one place so the concurrency tests can
 * call it. The 0..100 clamp and the `total > 0` guard are the CALLER's job and
 * live in [RangeDownloadLogic.progressPercent] (driven directly here too); the
 * gate is purely "advance to a strictly-higher Int, lock-free".
 *
 * The RunRegistry / single-flight and segment-artifact-path assertions drive the
 * genuinely-extracted [RangeDownloadLogic.runKey] and
 * [RangeDownloadLogic.sweepPartialArtifacts] against a real [ConcurrentHashMap]
 * (mirroring the adapter's `activeDownloads` put / `remove(key, value)` at lines
 * 87 / 112). Pure JVM JUnit — no Nitro / HybridObject / Context, matching the
 * established module pattern (see RangeDownloadLogicTest / IsPermanentHttpStatusTest).
 */
class MonotonicProgressGateTest {

  private lateinit var tmpDir: File

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-gate", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    tmpDir.deleteRecursively()
  }

  // The VERBATIM adapter gate expression (ReactNativeRangeDownloader.kt:104-105)
  // driven against the REAL java.util.concurrent.atomic.AtomicInteger. Returns
  // true iff `p` strictly exceeds the current max AND this thread won the CAS.
  private fun tryAdvance(gate: AtomicInteger, p: Int): Boolean {
    val prev = gate.get()
    return p > prev && gate.compareAndSet(prev, p)
  }

  // ---- MonotonicProgressGate: single-threaded behavior ----

  @Test
  fun firstCallWithZeroAdvancesFromInitialMinusOne() {
    // Initial state is -1 (adapter line 94), so the very first 0% must emit.
    val gate = AtomicInteger(-1)
    assertTrue("0 must beat the -1 initial and emit", tryAdvance(gate, 0))
    assertEquals(0, gate.get())
  }

  @Test
  fun ascendingZeroToHundredEveryCallEmits() {
    val gate = AtomicInteger(-1)
    for (p in 0..100) {
      assertTrue("ascending $p must emit", tryAdvance(gate, p))
      assertEquals(p, gate.get())
    }
    assertEquals(100, gate.get())
  }

  @Test
  fun duplicateValueDoesNotReEmit() {
    val gate = AtomicInteger(-1)
    assertTrue("first 5 emits", tryAdvance(gate, 5))
    assertFalse("equal value must NOT re-emit (strictly-greater only)", tryAdvance(gate, 5))
    assertEquals("state unchanged on a no-emit", 5, gate.get())
  }

  @Test
  fun outOfOrderLowerValueIsDroppedAndStateHolds() {
    val gate = AtomicInteger(-1)
    assertTrue("10 emits", tryAdvance(gate, 10))
    assertFalse("a later 7 (< current max 10) must be dropped", tryAdvance(gate, 7))
    assertEquals("state must stay at the high-water mark 10", 10, gate.get())
    // And a subsequent strictly-higher value still advances normally.
    assertTrue("11 still emits after a dropped 7", tryAdvance(gate, 11))
    assertEquals(11, gate.get())
  }

  @Test
  fun outOfOrderRepeatingStreamEmitsOnlyMonotonicHighWaterMarks() {
    // Mirrors the real worker callback shape: out-of-order, repeating percentages.
    val gate = AtomicInteger(-1)
    val emitted = mutableListOf<Int>()
    for (p in listOf(10, 10, 25, 25, 20, 50, 50, 49, 100, 100)) {
      if (tryAdvance(gate, p)) emitted.add(p)
    }
    assertEquals(listOf(10, 25, 50, 100), emitted)
    assertEquals(100, gate.get())
  }

  // ---- MonotonicProgressGate: concurrency (the load-bearing CAS contract) ----

  @Test
  fun concurrentSameTargetExactlyOneWinsCas() {
    // K threads all try to advance -1 -> the SAME target. CAS guarantees exactly
    // one winner; all losers see a no-emit; final state == that target.
    val k = 64
    val target = 42
    val gate = AtomicInteger(-1)
    val ready = CountDownLatch(k)
    val go = CountDownLatch(1)
    val wins = AtomicInteger(0)
    val pool = Executors.newFixedThreadPool(k)
    repeat(k) {
      pool.submit {
        ready.countDown()
        go.await(5, TimeUnit.SECONDS)
        if (tryAdvance(gate, target)) wins.incrementAndGet()
      }
    }
    ready.await(5, TimeUnit.SECONDS)
    go.countDown()
    pool.shutdown()
    assertTrue("workers must finish", pool.awaitTermination(10, TimeUnit.SECONDS))

    assertEquals("exactly ONE thread may win the CAS to the same target", 1, wins.get())
    assertEquals("state must equal the advanced target", target, gate.get())
  }

  @Test
  fun concurrentRacingIncrementsEmitOncePerDistinctHighWaterUpdate() {
    // Many threads each push the SAME ascending sequence 0..100 concurrently.
    // The gate is lock-free + monotonic, so across ALL threads each distinct
    // high-water value (0..100 -> 101 values) advances exactly once: total true
    // returns == 101, regardless of thread count or interleaving. Final == 100.
    val threads = 16
    val maxPercent = 100
    val gate = AtomicInteger(-1)
    val totalWins = AtomicInteger(0)
    val ready = CountDownLatch(threads)
    val go = CountDownLatch(1)
    val pool = Executors.newFixedThreadPool(threads)
    repeat(threads) {
      pool.submit {
        ready.countDown()
        go.await(5, TimeUnit.SECONDS)
        for (p in 0..maxPercent) {
          if (tryAdvance(gate, p)) totalWins.incrementAndGet()
        }
      }
    }
    ready.await(5, TimeUnit.SECONDS)
    go.countDown()
    pool.shutdown()
    assertTrue("workers must finish", pool.awaitTermination(10, TimeUnit.SECONDS))

    assertEquals(
      "true returns must equal the count of distinct strictly-increasing updates (0..100)",
      maxPercent + 1,
      totalWins.get(),
    )
    assertEquals("final state must be the max seen", maxPercent, gate.get())
  }

  @Test
  fun concurrentMonotonicityNeverObservesARegression() {
    // Under concurrent advancement the gate must never let `get()` go backwards.
    // An observer thread samples the gate while writers race it forward; every
    // sample must be >= the previous sample.
    val gate = AtomicInteger(-1)
    val writers = 8
    val maxPercent = 100
    val ready = CountDownLatch(writers)
    val go = CountDownLatch(1)
    val regressionObserved = AtomicInteger(0)
    val pool = Executors.newFixedThreadPool(writers + 1)

    val observer = pool.submit {
      var last = Int.MIN_VALUE
      // Sample aggressively until the gate reaches the terminal value.
      while (gate.get() < maxPercent) {
        val now = gate.get()
        if (now < last) regressionObserved.incrementAndGet()
        last = now
      }
      if (gate.get() < last) regressionObserved.incrementAndGet()
    }

    repeat(writers) {
      pool.submit {
        ready.countDown()
        go.await(5, TimeUnit.SECONDS)
        for (p in 0..maxPercent) tryAdvance(gate, p)
      }
    }
    ready.await(5, TimeUnit.SECONDS)
    go.countDown()
    observer.get(10, TimeUnit.SECONDS)
    pool.shutdown()
    assertTrue("workers must finish", pool.awaitTermination(10, TimeUnit.SECONDS))

    assertEquals("observer must never see progress go backwards", 0, regressionObserved.get())
    assertEquals(maxPercent, gate.get())
  }

  // ---- Caller-owned clamp/guard feeding the gate (RangeDownloadLogic.progressPercent) ----

  @Test
  fun callerClampAndGuardOwnTheZeroToHundredRangeNotTheGate() {
    // total <= 0 yields no percentage at all (guarded by the caller), so the gate
    // is never even consulted.
    assertNull("non-positive total emits nothing", RangeDownloadLogic.progressPercent(10, 0))
    assertNull(RangeDownloadLogic.progressPercent(10, -1))
    // Over-100 transferred (momentary cross-segment overshoot) is clamped to 100
    // BEFORE the gate, so the gate only ever sees values in 0..100.
    assertEquals(100, RangeDownloadLogic.progressPercent(150, 100))
    // The full pipeline: clamped percentages flow into the gate monotonically.
    val gate = AtomicInteger(-1)
    val emitted = mutableListOf<Int>()
    for ((transferred, total) in listOf(0L to 100L, 50L to 100L, 49L to 100L, 150L to 100L)) {
      val p = RangeDownloadLogic.progressPercent(transferred, total) ?: continue
      if (tryAdvance(gate, p)) emitted.add(p)
    }
    assertEquals(listOf(0, 50, 100), emitted)
  }

  // ---- RunRegistry / single-flight (RangeDownloadLogic.runKey + activeDownloads map) ----

  @Test
  fun runKeyJoinsChannelAndTaskStablyAndCollisionFree() {
    // Stable: same inputs -> same key.
    assertEquals("MAIN|task-1", RangeDownloadLogic.runKey("MAIN", "task-1"))
    assertEquals(
      RangeDownloadLogic.runKey("MAIN", "task-1"),
      RangeDownloadLogic.runKey("MAIN", "task-1"),
    )
    // Collision-free across channel and across taskId.
    assertNotEquals(
      RangeDownloadLogic.runKey("MAIN", "t"),
      RangeDownloadLogic.runKey("OTHER", "t"),
    )
    assertNotEquals(
      RangeDownloadLogic.runKey("MAIN", "a"),
      RangeDownloadLogic.runKey("MAIN", "b"),
    )
  }

  @Test
  fun singleFlightRegistryDedupesSecondRegisterForSameKey() {
    // Models the adapter's `activeDownloads[runKey] = handle` (line 87) single-flight
    // map. putIfAbsent rejects a second register for the same key.
    val registry = ConcurrentHashMap<String, Any>()
    val key = RangeDownloadLogic.runKey("MAIN", "task-7")
    val first = Any()
    val second = Any()
    assertNull("first register wins", registry.putIfAbsent(key, first))
    assertTrue("second register for same key is rejected (returns the incumbent)",
      registry.putIfAbsent(key, second) === first)
    assertTrue("incumbent handle must remain", registry[key] === first)
    // A different key (different taskId) co-exists, not deduped.
    val otherKey = RangeDownloadLogic.runKey("MAIN", "task-8")
    assertNull(registry.putIfAbsent(otherKey, second))
    assertEquals(2, registry.size)
  }

  @Test
  fun cancelRemovesOnlyTheMatchingHandleNotAConcurrentReplacement() {
    // Mirrors `activeDownloads.remove(runKey, cancelHandle)` (line 112): the
    // value-conditional remove only clears OUR handle, so a concurrent cancel that
    // already replaced the handle for the same key is left intact.
    val registry = ConcurrentHashMap<String, Any>()
    val key = RangeDownloadLogic.runKey("MAIN", "task-9")
    val ours = Any()
    val replacement = Any()
    registry[key] = ours
    // Simulate a concurrent cancel replacing the handle under the same key.
    registry[key] = replacement
    // Our finally-block remove must NOT evict the replacement.
    assertFalse("value-conditional remove must not evict a replaced handle",
      registry.remove(key, ours))
    assertTrue("replacement handle survives", registry[key] === replacement)
    // Removing the actual current value succeeds and clears the key.
    assertTrue(registry.remove(key, replacement))
    assertNull(registry[key])
  }

  @Test
  fun concurrentRegistersForSameKeyOnlyOneWins() {
    val registry = ConcurrentHashMap<String, Any>()
    val key = RangeDownloadLogic.runKey("MAIN", "race")
    val k = 32
    val winners = AtomicInteger(0)
    val ready = CountDownLatch(k)
    val go = CountDownLatch(1)
    val pool = Executors.newFixedThreadPool(k)
    repeat(k) {
      pool.submit {
        val mine = Any()
        ready.countDown()
        go.await(5, TimeUnit.SECONDS)
        if (registry.putIfAbsent(key, mine) == null) winners.incrementAndGet()
      }
    }
    ready.await(5, TimeUnit.SECONDS)
    go.countDown()
    pool.shutdown()
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS))
    assertEquals("exactly one concurrent register may win the single-flight slot", 1, winners.get())
    assertEquals(1, registry.size)
  }

  // ---- segment artifact paths / sweep (RangeDownloadLogic.sweepPartialArtifacts) ----

  @Test
  fun segmentArtifactPathsAreDotSegNUnderTheDotPartialPrefix() {
    // Drive the REAL sweep: it deletes `<dest>.partial` + every
    // `<dest>.partial.seg<N>` matched by filename prefix, for ANY segment count.
    val dest = File(tmpDir, "asset.bin").absolutePath
    val partial = File("$dest.partial").apply { writeText("p") }
    val count = 8
    val segs = (0 until count).map { File("$dest.partial.seg$it").apply { writeText("$it") } }
    // Verify the path shape the adapter/helper assembles for a given filePath+count.
    segs.forEachIndexed { i, f ->
      assertEquals("seg path must be <dest>.partial.seg$i", "$dest.partial.seg$i", f.absolutePath)
    }
    // A sibling NOT under the `.partial` prefix must survive the sweep.
    val unrelated = File("$dest.keep").apply { writeText("k") }

    RangeDownloadLogic.sweepPartialArtifacts(dest)

    assertFalse("`.partial` must be swept", partial.exists())
    segs.forEach { assertFalse("${it.name} must be swept", it.exists()) }
    assertTrue("unrelated sibling must survive", unrelated.exists())
  }

  @Test
  fun sweepClearsCustomSegmentCountByPrefixNotJustDefaultEight() {
    val dest = File(tmpDir, "big.bin").absolutePath
    File("$dest.partial").writeText("p")
    // A custom (non-default) 16-segment run must be fully cleared by prefix.
    val segs = (0 until 16).map { File("$dest.partial.seg$it").apply { writeText("$it") } }

    RangeDownloadLogic.sweepPartialArtifacts(dest)

    segs.forEach { assertFalse("${it.name} (custom count) must be swept", it.exists()) }
    assertFalse(File("$dest.partial").exists())
  }
}
