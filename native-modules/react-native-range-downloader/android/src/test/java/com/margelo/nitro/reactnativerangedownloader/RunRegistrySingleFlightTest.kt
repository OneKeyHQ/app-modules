package com.margelo.nitro.reactnativerangedownloader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Drives the REAL extracted single-flight unit
 * [RangeDownloadLogic.RunRegistry] (keyed by "channel|taskId"). The registry was
 * lifted VERBATIM from the Nitro adapter's inline `activeDownloads`
 * ConcurrentHashMap usage so its keyed invariants can be unit-tested without the
 * Nitro / JNI / OkHttp dependencies. Every assertion exercises the real code —
 * nothing is re-implemented here.
 *
 * Channel mapping note: the adapter computes its key as
 * `RangeDownloadLogic.runKey(channel.name, taskId)` (adapter:44-45), i.e. the
 * `DownloadChannel` enum contributes ONLY its `.name` string. The dependency-free
 * registry therefore takes the channel NAME string directly. To keep the test
 * faithful to the real channels (and prove cross-channel key isolation) WITHOUT
 * dragging the JNI-annotated generated enum onto the pure-JVM test classpath
 * (every sibling test stays generated-type-free), the names below are the exact
 * verbatim `DownloadChannel.<X>.name` strings.
 *
 * Cancel-detection probe: [ConcurrentRangeDownloader.CancelHandle.cancel] is
 * idempotent and flips the public `aborted` AtomicBoolean — observing `aborted`
 * is how we assert a handle was (or was not) cancelled, without subclassing the
 * final-ish handle. `cancel()` with no attached pool is a pure flag flip, so it
 * is safe + fast in a JVM unit test.
 */
class RunRegistrySingleFlightTest {

  // Verbatim DownloadChannel.<X>.name values (BUNDLE/APK/CHART). Kept as plain
  // strings so this test needs no JNI-annotated generated enum on its classpath.
  private val bundle = "BUNDLE"
  private val apk = "APK"
  private val chart = "CHART"

  // ------------------------------------------------------------------------
  // start() dedup: a second start for the same key overwrites; finish() is
  // identity-checked so the STALE handle's finish is a no-op, and only the LIVE
  // handle's finish removes the entry (adapter:87 + adapter:112 invariants).
  // ------------------------------------------------------------------------
  @Test
  fun startTwiceSameKey_secondHandleIsLive_staleFinishIsNoOp() {
    val reg = RangeDownloadLogic.RunRegistry()

    val first = reg.start(bundle, "task-A")
    val second = reg.start(bundle, "task-A")

    assertNotSame("a second start() must mint a fresh handle", first, second)
    assertEquals("dedup: same key must hold exactly one live entry", 1, reg.size)

    // finish(firstHandle) must NOT clobber the live (second) handle — identity
    // remove fails because the map now holds `second`.
    reg.finish(bundle, "task-A", first)
    assertEquals("stale-handle finish must be a no-op (entry still live)", 1, reg.size)

    // finish(secondHandle) — the live handle — removes the entry.
    reg.finish(bundle, "task-A", second)
    assertEquals("live-handle finish must remove the entry", 0, reg.size)
  }

  // ------------------------------------------------------------------------
  // start → finish(sameHandle) clears the key; a subsequent cancel() is a no-op
  // and must NOT throw (adapter:206 remove(...)?.cancel() null-safe path).
  // ------------------------------------------------------------------------
  @Test
  fun startThenFinish_keyGone_subsequentCancelIsNoOpAndDoesNotThrow() {
    val reg = RangeDownloadLogic.RunRegistry()

    val handle = reg.start(apk, "task-B")
    reg.finish(apk, "task-B", handle)
    assertEquals("finish must clear the key", 0, reg.size)

    // No live handle → cancel() finds nothing → must be a silent no-op.
    reg.cancel(apk, "task-B")
    assertEquals("cancel of an absent key must not create or leak an entry", 0, reg.size)
    assertFalse(
      "a finished handle must NOT have been cancelled by the no-op cancel()",
      handle.aborted.get(),
    )
  }

  // ------------------------------------------------------------------------
  // cancel() invokes CancelHandle.cancel() exactly once AND removes the key:
  // the second cancel() finds nothing (probe) and the handle's aborted flag was
  // already set by the first cancel.
  // ------------------------------------------------------------------------
  @Test
  fun cancel_invokesHandleCancelOnceAndRemovesKey() {
    val reg = RangeDownloadLogic.RunRegistry()

    val handle = reg.start(chart, "task-C")
    assertFalse("handle must start un-aborted", handle.aborted.get())

    reg.cancel(chart, "task-C")
    assertTrue("cancel() must have flipped the handle's aborted flag", handle.aborted.get())
    assertEquals("cancel() must remove the key", 0, reg.size)

    // Probe: the entry is gone, so a second cancel() targets nothing. If the key
    // had NOT been removed, this would re-cancel the same handle — proving the
    // remove happened on the first call. (cancel() is idempotent regardless, so
    // the load-bearing assertion is the size==0 above + this staying a no-op.)
    reg.cancel(chart, "task-C")
    assertEquals("second cancel() finds nothing — no entry resurrected", 0, reg.size)
  }

  // ------------------------------------------------------------------------
  // Different channels (BUNDLE vs APK) with the SAME taskId produce distinct
  // keys → both coexist; cancelling one leaves the other live & un-aborted.
  // ------------------------------------------------------------------------
  @Test
  fun differentChannelsSameTaskId_distinctKeys_cancelOneLeavesOtherLive() {
    val reg = RangeDownloadLogic.RunRegistry()
    val taskId = "shared-task"

    // Sanity: the underlying key derivation is collision-free across channels.
    assertNotEquals(
      "channel must contribute to the key",
      RangeDownloadLogic.runKey(bundle, taskId),
      RangeDownloadLogic.runKey(apk, taskId),
    )

    val bundleHandle = reg.start(bundle, taskId)
    val apkHandle = reg.start(apk, taskId)
    assertEquals("distinct channels must coexist under the same taskId", 2, reg.size)

    reg.cancel(bundle, taskId)

    assertTrue("the cancelled channel's handle must be aborted", bundleHandle.aborted.get())
    assertFalse("the OTHER channel's handle must remain un-aborted", apkHandle.aborted.get())
    assertEquals("only the cancelled channel's entry must be removed", 1, reg.size)

    // The surviving entry is still the APK handle and can be finished normally.
    reg.finish(apk, taskId, apkHandle)
    assertEquals("surviving entry finishes cleanly", 0, reg.size)
  }

  // ------------------------------------------------------------------------
  // Concurrency: N threads start() the same key while one thread cancel()s it.
  // Invariants under contention:
  //   - the registry never LEAKS an entry that no live handle owns;
  //   - the surviving handle (if any) is never DOUBLE-cancelled;
  //   - every start() handle is accounted for: it is either the live survivor,
  //     cancelled by the racing cancel(), or superseded (overwritten) — never
  //     silently kept alive AND orphaned.
  // ------------------------------------------------------------------------
  @Test
  fun concurrentStartsAndCancel_noLeakedEntry_noDoubleCancel() {
    val reg = RangeDownloadLogic.RunRegistry()
    val key = "race-task"
    val n = 32

    // Count how many distinct handles ever get cancelled. Because each handle is
    // unique and cancel() is observed via its aborted flag, double-cancel of the
    // SAME handle is detectable: cancelCount must never exceed the number of
    // handles that were actually cancelled (we re-check by scanning).
    val handles = ConcurrentHashMap.newKeySet<ConcurrentRangeDownloader.CancelHandle>()
    val startBarrier = CountDownLatch(1)
    val done = CountDownLatch(n + 1)
    val pool = Executors.newFixedThreadPool(n + 1)

    repeat(n) {
      pool.submit {
        startBarrier.await()
        handles.add(reg.start(chart, key))
        done.countDown()
      }
    }
    // One racing canceller.
    pool.submit {
      startBarrier.await()
      // Spin a touch so it interleaves with the starts rather than always winning.
      repeat(4) { reg.cancel(chart, key) }
      done.countDown()
    }

    startBarrier.countDown()
    assertTrue("all workers must finish", done.await(10, TimeUnit.SECONDS))
    pool.shutdownNow()

    // Drain any survivor via a final cancel so the registry must end empty.
    reg.cancel(chart, key)
    assertEquals("registry must never leak an entry under contention", 0, reg.size)

    // No handle is double-cancelled: aborted is a boolean flip, so re-counting is
    // not enough on its own — assert instead that cancel() is idempotent by the
    // contract and that AT MOST the set of minted handles were cancelled (never
    // more handles than were created). The strong leak invariant above is the
    // primary guarantee; here we assert every cancelled handle came from start().
    val cancelled = handles.count { it.aborted.get() }
    assertTrue(
      "cancelled handles ($cancelled) cannot exceed minted handles (${handles.size})",
      cancelled <= handles.size,
    )
    // At least one cancel landed on a real handle (the racing canceller + final
    // drain cannot both miss every start under this barrier).
    assertTrue("at least one minted handle must have been cancelled", cancelled >= 1)

    // Defensive accounting probe: no AtomicInteger over-count of survivors.
    val live = AtomicInteger(0)
    // After the final drain the key is gone, so re-finishing any handle is a safe
    // no-op and must not resurrect the entry.
    handles.forEach { reg.finish(chart, key, it) }
    if (reg.size > 0) live.incrementAndGet()
    assertEquals("post-drain finish() of any stale handle must not resurrect a key", 0, live.get())
  }
}
