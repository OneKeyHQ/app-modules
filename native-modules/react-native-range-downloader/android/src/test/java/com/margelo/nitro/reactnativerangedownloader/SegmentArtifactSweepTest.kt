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
 * Unit coverage for the extracted "segmentArtifactPaths + sweepPartialArtifacts
 * (.segN glob / delete)" behavior plus the two adapter invariants it ships with
 * (single-flight run-key, monotonic/de-duped progress gate).
 *
 * IMPORTANT — drives the REAL extracted code, never re-implements it:
 *  - The Android extraction lives in [RangeDownloadLogic] (the dependency-free
 *    pieces of `ReactNativeRangeDownloader`). On Android the path computation and
 *    the delete side-effect are NOT two separate public functions — the verbatim
 *    glob predicate `f.name.startsWith(partial.name + ".seg")` and the
 *    `.forEach { it.delete() }` live INSIDE [RangeDownloadLogic.sweepPartialArtifacts]
 *    (RangeDownloadLogic.kt:46-52, mirroring adapter:209-213). So the glob is
 *    asserted the only way it is observable here: by which sibling files survive
 *    vs. vanish on disk after the REAL sweep runs. We deliberately do not
 *    reproduce the predicate in the test.
 *  - The "RunRegistry / single-flight" key is [RangeDownloadLogic.runKey]; the
 *    `activeDownloads` map that dedupes by it is a `ConcurrentHashMap` in the
 *    adapter, so the single-flight / cancel-removes-only-matching / no-co-exist
 *    semantics are exercised against the REAL key over that same map type.
 *  - The "MonotonicProgressGate" is [RangeDownloadLogic.progressPercent] feeding
 *    the adapter's verbatim `p > prev && compareAndSet` loop
 *    (ReactNativeRangeDownloader.kt:102-108). We drive the REAL percent fn and the
 *    REAL `AtomicInteger` CAS, never a hand-rolled monotone helper.
 *
 * Pure JVM JUnit4 — no Nitro / HybridObject / Context — matching the established
 * convention in this module (see RangeDownloadLogicTest / IsPermanentHttpStatusTest).
 */
class SegmentArtifactSweepTest {

  private lateinit var tmpDir: File

  @Before
  fun setUp() {
    tmpDir = File.createTempFile("crd-sweep", "").let {
      it.delete(); it.mkdirs(); it
    }
  }

  @After
  fun tearDown() {
    tmpDir.deleteRecursively()
  }

  // --- helpers -------------------------------------------------------------

  private fun destPath(name: String = "asset.bin") = File(tmpDir, name).absolutePath
  private fun partial(dest: String) = File("$dest.partial")
  private fun seg(dest: String, index: Int) = File("$dest.partial.seg$index")

  // ------------------------------------------------------------------------
  // segmentArtifactPaths + sweepPartialArtifacts (.segN glob / delete)
  // ------------------------------------------------------------------------

  // Idea 1: <dest>.partial.seg0..seg11 (custom count > shipped 8) + <dest>.partial
  // + an unrelated sibling → the sweep removes exactly the 12 seg files and the
  // .partial, and leaves the unrelated sibling (proving the glob set = every
  // per-segment file regardless of segmentCount, PLUS the .partial, and nothing
  // else).
  @Test
  fun sweepGlobsEverySegRegardlessOfCountAndThePartialButNotUnrelated() {
    val dest = destPath()
    val p = partial(dest).apply { writeText("concatenated") }
    // 12 > the shipped default of 8 — the prefix glob must catch all of them.
    val segs = (0 until 12).map { seg(dest, it).apply { writeText("s$it") } }
    // A sibling that shares the <dest> stem but is NOT a .partial / .segN artifact.
    val unrelated = File(tmpDir, "asset.bin.keep").apply { writeText("keep") }
    // A sibling that does not share the stem at all.
    val foreign = File(tmpDir, "other.txt").apply { writeText("other") }

    RangeDownloadLogic.sweepPartialArtifacts(dest)

    assertFalse(".partial must be deleted", p.exists())
    segs.forEach { assertFalse("${it.name} must be deleted", it.exists()) }
    assertTrue("unrelated <dest>.keep sibling must survive", unrelated.exists())
    assertTrue("foreign sibling must survive", foreign.exists())
  }

  // Idea 2: no parent dir / parentFile resolvable to null → the safe-call `?.`
  // on listFiles means the seg-glob phase is skipped, and the bare `partial.delete()`
  // still runs without throwing (mirrors the `?.listFiles` null-guard). We assert
  // on the observable contract: no crash, and the call is a no-op for a path whose
  // parent holds nothing.
  @Test
  fun sweepDoesNotCrashWhenNoSegArtifactsExist() {
    // A real path with a real parent dir but zero artifacts: the listFiles glob
    // returns an empty array and partial.delete() is a no-op — no throw.
    val dest = destPath("nothing-here.bin")
    RangeDownloadLogic.sweepPartialArtifacts(dest)
    assertFalse(partial(dest).exists())

    // A relative bare filename ("foo.bin") makes File("foo.bin.partial").parentFile
    // null; the `?.` short-circuits the glob and only the .partial delete is
    // attempted. Must not throw (the load-bearing null-safety assertion).
    RangeDownloadLogic.sweepPartialArtifacts("crd-sweep-nonexistent-bare-name.bin")
  }

  // Idea 3: the sweep deletes all .segN and the .partial; the final <dest> and
  // unrelated siblings are untouched (the sweep never touches the promoted final).
  @Test
  fun sweepLeavesFinalDestAndSiblingsUntouched() {
    val dest = destPath()
    val finalFile = File(dest).apply { writeText("final promoted bytes") }
    val p = partial(dest).apply { writeText("p") }
    val segs = (0 until 4).map { seg(dest, it).apply { writeText("$it") } }
    val sibling = File(tmpDir, "asset.bin.metadata").apply { writeText("m") }

    RangeDownloadLogic.sweepPartialArtifacts(dest)

    assertFalse(".partial must be gone", p.exists())
    segs.forEach { assertFalse("${it.name} must be gone", it.exists()) }
    assertTrue("the final <dest> must NOT be swept", finalFile.exists())
    assertEquals("final <dest> content must be intact", "final promoted bytes", finalFile.readText())
    assertTrue("unrelated sibling must survive", sibling.exists())
  }

  // Idea 4: prefix-boundary — a file named "<dest>.partial.segment-notes" still
  // matches because the predicate is prefix-only (startsWith ".partial.seg"), not
  // "<dest>.partial.seg<digits>". This documents (behavior-preserving) that today's
  // glob would sweep it too.
  @Test
  fun sweepPrefixIsBoundaryLooseAndAlsoMatchesSegmentNotes() {
    val dest = destPath()
    partial(dest).writeText("p")
    val numericSeg = seg(dest, 0).apply { writeText("0") }
    // Shares the "<partial>.seg" prefix but is not a numeric segment.
    val prefixLookalike = File("$dest.partial.segment-notes").apply { writeText("notes") }
    // Does NOT start with "<basename>.partial.seg" (the stem is "...partialX...")
    // → must survive, proving the match is anchored on that exact prefix.
    val nearMiss = File("$dest.partialX.seg0").apply { writeText("x") }

    RangeDownloadLogic.sweepPartialArtifacts(dest)

    assertFalse("numeric .seg0 must be swept", numericSeg.exists())
    assertFalse(
      "prefix-only predicate also sweeps '<partial>.segment-notes' (behavior-preserving)",
      prefixLookalike.exists(),
    )
    assertTrue("a sibling not sharing the '.partial.seg' prefix must survive", nearMiss.exists())
  }

  // Idea 5: idempotent — sweeping twice (artifacts already gone) does not throw and
  // is a no-op.
  @Test
  fun sweepIsIdempotentAndSafeToRepeat() {
    val dest = destPath()
    partial(dest).writeText("p")
    (0 until 3).forEach { seg(dest, it).writeText("$it") }

    RangeDownloadLogic.sweepPartialArtifacts(dest)
    // Everything is already gone; a second sweep must be a clean no-op.
    RangeDownloadLogic.sweepPartialArtifacts(dest)
    RangeDownloadLogic.sweepPartialArtifacts(dest)

    assertFalse(partial(dest).exists())
    (0 until 3).forEach { assertFalse(seg(dest, it).exists()) }
  }

  // ------------------------------------------------------------------------
  // RunRegistry / single-flight (RangeDownloadLogic.runKey over the adapter's
  // ConcurrentHashMap<String, CancelHandle> activeDownloads map).
  // ------------------------------------------------------------------------

  // runKey is stable + collision-free across channel/taskId.
  @Test
  fun runKeyIsStableAndCollisionFreeAcrossChannelAndTask() {
    // Stable: same inputs → same key, every call.
    assertEquals(
      RangeDownloadLogic.runKey("MAIN", "task-1"),
      RangeDownloadLogic.runKey("MAIN", "task-1"),
    )
    assertEquals("MAIN|task-1", RangeDownloadLogic.runKey("MAIN", "task-1"))
    // Collision-free across channel.
    assertNotEquals(
      RangeDownloadLogic.runKey("MAIN", "t"),
      RangeDownloadLogic.runKey("OTHER", "t"),
    )
    // Collision-free across taskId.
    assertNotEquals(
      RangeDownloadLogic.runKey("MAIN", "a"),
      RangeDownloadLogic.runKey("MAIN", "b"),
    )
  }

  // A second register for the same key dedupes (the map holds a single handle per
  // key); cancel/discard removes only the matching key; concurrent registers for
  // distinct keys co-exist while the same key never does.
  @Test
  fun singleFlightRegistryDedupesPerKeyAndRemovesOnlyTheMatchingKey() {
    // Mirror the adapter's registry type exactly (ReactNativeRangeDownloader.kt:41-42).
    val active = ConcurrentHashMap<String, ConcurrentRangeDownloader.CancelHandle>()

    val keyA = RangeDownloadLogic.runKey("MAIN", "task-A")
    val keyB = RangeDownloadLogic.runKey("OTHER", "task-A")

    val h1 = ConcurrentRangeDownloader.CancelHandle()
    val h2 = ConcurrentRangeDownloader.CancelHandle()
    active[keyA] = h1
    // A second register for the SAME key replaces (does not co-exist) — only ever
    // one live handle per "channel|taskId".
    active[keyA] = h2
    assertEquals("same-key register must not co-exist; map holds exactly one", 1, active.keys.count { it == keyA })
    assertEquals("the latest handle wins for the same key", h2, active[keyA])

    // A distinct key (different channel, same taskId) co-exists.
    val hB = ConcurrentRangeDownloader.CancelHandle()
    active[keyB] = hB
    assertEquals("distinct keys co-exist", 2, active.size)

    // cancel/discard for keyA removes ONLY keyA, leaving keyB intact.
    active.remove(keyA)
    assertNull("cancelled key must be gone", active[keyA])
    assertEquals("only the matching key is removed", hB, active[keyB])
    assertEquals(1, active.size)
  }

  // The adapter deregisters with the value-checked remove(key, value) so a
  // concurrent cancel that already replaced the handle is NOT clobbered
  // (ReactNativeRangeDownloader.kt:112). Drive that exact 2-arg remove contract.
  @Test
  fun valueCheckedRemoveOnlyDeregistersOwnHandle() {
    val active = ConcurrentHashMap<String, ConcurrentRangeDownloader.CancelHandle>()
    val key = RangeDownloadLogic.runKey("MAIN", "task-X")
    val mine = ConcurrentRangeDownloader.CancelHandle()
    active[key] = mine
    // A concurrent cancel replaced our handle with a newer one.
    val newer = ConcurrentRangeDownloader.CancelHandle()
    active[key] = newer
    // Our finally-block remove(key, mine) must be a no-op because the value moved on.
    assertFalse("value-checked remove must not evict the replacement", active.remove(key, mine))
    assertEquals("the newer handle survives the stale deregister", newer, active[key])
    // The owner of `newer` removes its own handle successfully.
    assertTrue(active.remove(key, newer))
    assertNull(active[key])
  }

  // ------------------------------------------------------------------------
  // MonotonicProgressGate (RangeDownloadLogic.progressPercent + the adapter's
  // verbatim AtomicInteger `p > prev && compareAndSet` loop).
  // ------------------------------------------------------------------------

  // progressPercent floors, clamps to [0,100], and returns null for a non-positive
  // total (the gate's input contract).
  @Test
  fun progressPercentFloorsClampsAndNullsNonPositiveTotal() {
    assertEquals(0, RangeDownloadLogic.progressPercent(0, 100))
    assertEquals(33, RangeDownloadLogic.progressPercent(1, 3)) // floor, never rounds up
    assertEquals(100, RangeDownloadLogic.progressPercent(100, 100))
    assertEquals(100, RangeDownloadLogic.progressPercent(150, 100)) // clamp overshoot
    assertNull(RangeDownloadLogic.progressPercent(10, 0))
    assertNull(RangeDownloadLogic.progressPercent(10, -1))
  }

  // Progress only advances (CAS), never emits below the prior max even with
  // out-of-order callbacks, and equal values do not re-emit. Single-threaded
  // reproduction of the adapter's gate loop.
  @Test
  fun progressGateIsMonotonicAndDeduped() {
    val last = AtomicInteger(-1)
    val emitted = mutableListOf<Int>()
    // total fixed at 100; transferred arrives out of order with repeats + a regress.
    for (transferred in listOf(10L, 10L, 25L, 25L, 20L, 50L, 50L, 49L, 100L, 100L)) {
      val p = RangeDownloadLogic.progressPercent(transferred, 100) ?: continue
      val prev = last.get()
      if (p > prev && last.compareAndSet(prev, p)) {
        emitted.add(p)
      }
    }
    // 20 (<25) and 49 (<50) never emit; repeats are de-duped; sequence is monotone.
    assertEquals(listOf(10, 25, 50, 100), emitted)
  }

  // Under genuine concurrency the CAS gate still never emits a value below the
  // running max, and emits each percentage at most once. Many threads race the
  // same gate driven by the REAL progressPercent + AtomicInteger CAS.
  @Test
  fun progressGateNeverGoesBackwardUnderConcurrency() {
    val last = AtomicInteger(-1)
    val emissions = java.util.concurrent.ConcurrentLinkedQueue<Int>()
    val total = 1_000L
    val threads = 16
    val pool = Executors.newFixedThreadPool(threads)
    val start = CountDownLatch(1)
    val done = CountDownLatch(threads)
    val regress = AtomicInteger(0)

    repeat(threads) { t ->
      pool.submit {
        start.await()
        // Each thread fires the full 0..1000 transferred sweep (heavy overlap), so
        // the gate sees the same percentages racing from many threads at once.
        for (transferred in 0L..total) {
          val p = RangeDownloadLogic.progressPercent(transferred, total) ?: continue
          while (true) {
            val prev = last.get()
            if (p <= prev) break // not an advance → no emit (de-dup / monotone)
            if (last.compareAndSet(prev, p)) {
              emissions.add(p)
              break
            }
          }
          // A reader must NEVER observe the shared published max regress.
          if (prevMaxRegressed(last)) regress.incrementAndGet()
        }
        done.countDown()
      }
    }
    start.countDown()
    assertTrue("workers must finish", done.await(20, TimeUnit.SECONDS))
    pool.shutdownNow()

    // The published max ends at exactly 100 and never regressed.
    assertEquals("gate must settle at 100%", 100, last.get())
    assertEquals("published max must never regress", 0, regress.get())
    // Each emitted percentage is unique (CAS de-dup) and the stream is sorted
    // ascending (monotone): emissions are appended only on a successful advance.
    val list = emissions.toList()
    assertEquals("every emission must be unique (no re-emit of an equal value)", list.toSet().size, list.size)
    assertEquals("emissions must be monotone non-decreasing", list.sorted(), list)
    assertTrue("the terminal 100% must be emitted exactly once", list.count { it == 100 } == 1)
  }

  // Tracks the running max in a thread-confined way to detect any regression of the
  // shared published value. Returns true if the AtomicInteger's value ever dropped
  // below a previously observed value on this thread.
  private val perThreadSeenMax = ThreadLocal.withInitial { -1 }
  private fun prevMaxRegressed(last: AtomicInteger): Boolean {
    val now = last.get()
    val seen = perThreadSeenMax.get()
    if (now < seen) return true
    if (now > seen) perThreadSeenMax.set(now)
    return false
  }
}
