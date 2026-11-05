package com.onekeyfe.reactnativelitecard.onekeyLite

import android.nfc.tech.IsoDep
import com.onekeyfe.reactnativelitecard.nfc.NFCExceptions
import com.onekeyfe.reactnativelitecard.onekeyLite.nfc.Probe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

private var assertions = 0
private fun expect(value: Boolean, scenario: String) {
    check(value) { scenario }
    assertions++
}
private fun CountDownLatch.awaitBounded() {
    check(await(5, TimeUnit.SECONDS)) { "Controlled operation did not advance" }
}
private fun write(session: OneKeyLiteCard.CardSession, expected: String? = session.cardState.serialNum) =
    OneKeyLiteCard.setMnemonic(session, "synthetic-backup", "synthetic-pin", false, expected)
private fun phases(tag: IsoDep) = Probe.events.filter { it.connection.isoDep === tag }.map { it.phase }

fun main() = runBlocking {
    withTimeout(20_000) {
        Probe.reset()
        val active = IsoDep("active")
        val passive = PendingTag(IsoDep("passive"))
        val otherClient = IsoDep("other-client")
        val cancelledClient = IsoDep("cancelled-client")
        val phaseEntered = CountDownLatch(1)
        val phaseRelease = CountDownLatch(1)
        Probe.hook = { connection, phase ->
            if (connection.isoDep === active && phase == "setup") {
                phaseEntered.countDown()
                phaseRelease.awaitBounded()
            }
        }
        val firstClient = async(Dispatchers.IO) { OneKeyLiteCard.withCardSession(active) { write(it) } }
        withContext(Dispatchers.IO) { phaseEntered.awaitBounded() }
        try {
            expect(OneKeyLiteCard.tryDiscover(passive) == null, "Busy passive discovery must skip")
            expect(phases(passive.isoDep).isEmpty(), "Busy discovery must not construct a connection")
            expect(!passive.isoDep.isConnected, "Skipped fresh tag remains unconnected")
            // Separate callers share the process singleton, like separate module/runtime instances.
            val secondClient = async(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
                OneKeyLiteCard.withCardSession(otherClient) { write(it) }
            }
            val cancelled = launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
                OneKeyLiteCard.withCardSession(cancelledClient) { write(it) }
            }
            cancelled.cancelAndJoin()
            expect(phases(cancelledClient).isEmpty(), "Cancelled gate waiter must not initialize or write")
            expect(phases(otherClient).isEmpty(), "Second active client waits for full operation")
            phaseRelease.countDown()
            expect(firstClient.await() && secondClient.await(), "Both serialized active clients complete")
        } finally {
            phaseRelease.countDown()
        }
        expect(phases(active) == listOf("construct", "info", "setup", "verify", "backup", "finalize"), "Full write phases belong to one connection")
        val firstOwner = Probe.events.first().connection
        expect(Probe.events.filter { it.connection.isoDep === active }.all { it.connection === firstOwner }, "Every active phase uses the checked instance")
        expect(Probe.events.indexOfFirst { it.connection.isoDep === active && it.phase == "finalize" } <
            Probe.events.indexOfFirst { it.connection.isoDep === otherClient }, "Finalize precedes next client construction")
        expect(Probe.finalized.get() == 2, "Each active owner finalizes once")
        expect(OneKeyLiteCard.withCardSession(passive.claim()) { write(it) }, "Busy-skipped unconnected tag remains available to active acquisition")
        expect(passive.isClaimed(), "Acquisition marks the exact discovery token consumed")
        expect(OneKeyLiteCard.tryDiscover(passive) == null, "Consumed discovery must not run later")
        expect(Probe.finalized.get() == 3, "Skipped discovery does not finalize another owner")
        println("PASS shared operation ownership, passive skip and cancelled waiter")

        for ((expected, serial) in listOf(
            "different" to "matching", "" to "matching", "" to "", "matching" to "",
            NfcConstant.NOT_MATCH_DEVICE to NfcConstant.NOT_MATCH_DEVICE
        )) {
            Probe.reset()
            val tag = IsoDep("guard-case", serial)
            val result = runCatching { OneKeyLiteCard.withCardSession(tag) { write(it, expected) } }
            expect((result.exceptionOrNull() as? NFCExceptions)?.code == 2003, "Mismatch and unavailable identity return existing error")
            expect(phases(tag) == listOf("construct", "info", "finalize"), "Mismatch performs no PIN or backup phase")
            expect(Probe.finalized.get() == 1, "Mismatch finalizes once")
        }
        Probe.reset()
        val unguarded = IsoDep("unguarded", "")
        expect(OneKeyLiteCard.withCardSession(unguarded) { write(it, null) }, "Omitted guard retains existing write behavior")
        val overwritten = IsoDep("overwrite", newCard = false, backedUp = true)
        expect(OneKeyLiteCard.withCardSession(overwritten) {
            OneKeyLiteCard.setMnemonic(it, "synthetic-backup", "synthetic-pin", true, "overwrite")
        }, "Overwrite retains existing phases")
        expect(phases(overwritten) == listOf("construct", "info", "verify", "setup", "verify", "backup", "finalize"), "Overwrite verifies and writes through the owned connection")
        println("PASS identity guards and existing write modes")

        for (failure in listOf("construct", "info", "setup", "verify", "backup")) {
            Probe.reset()
            val failed = IsoDep("failure-case", failStage = failure)
            val result = runCatching { OneKeyLiteCard.withCardSession(failed) { write(it) } }
            expect((result.exceptionOrNull() as? NFCExceptions)?.code == 2001, "Native failure is preserved")
            expect(Probe.finalized.get() == 1 && Probe.current == null, "Every failure releases and finalizes once")
            val next = IsoDep("after-failure")
            expect(OneKeyLiteCard.withCardSession(next) { write(it) }, "A subsequent operation succeeds after failure")
            expect(Probe.finalized.get() == 2, "Subsequent owner also finalizes once")
        }
        Probe.reset()
        val thrown = IsoDep("execution-failure")
        expect(runCatching {
            OneKeyLiteCard.withCardSession(thrown) { throw NFCExceptions.ExecFailureException() }
        }.exceptionOrNull() is NFCExceptions.ExecFailureException, "Execution exception is preserved")
        expect(Probe.finalized.get() == 1 && Probe.current == null, "Execution failure releases once")
        val afterThrown = IsoDep("after-execution-failure")
        expect(OneKeyLiteCard.withCardSession(afterThrown) { write(it) }, "Reentry after execution failure succeeds")
        println("PASS initialization and command failure cleanup")

        Probe.reset()
        val staleBefore = PendingTag(IsoDep("consumed-before-discovery"))
        staleBefore.claim()
        expect(OneKeyLiteCard.tryDiscover(staleBefore) == null && Probe.events.isEmpty(), "Consumed token is ignored before inspection")
        val staleDuring = PendingTag(IsoDep("consumed-during-discovery"))
        Probe.hook = { connection, phase ->
            if (connection.isoDep === staleDuring.isoDep && phase == "construct") staleDuring.claim()
        }
        expect(OneKeyLiteCard.tryDiscover(staleDuring) == null, "Consumed passive inspection discards its result")
        expect(staleDuring.isClaimed(), "A token may be consumed during passive initialization")
        expect(Probe.current == null && Probe.finalized.get() == 1, "Consumed passive owner still releases")
        Probe.hook = { _, _ -> }

        val channel = Channel<PendingTag?>(1)
        val queued = PendingTag(IsoDep("queued"))
        val overflow = PendingTag(IsoDep("overflow"))
        expect(channel.trySend(queued).isSuccess, "Fresh discovery can be queued")
        expect(channel.trySend(overflow).isFailure, "Full channel rejects another passive candidate")
        expect(phases(overflow.isoDep).isEmpty(), "Rejected candidate has no transport work")
        val acquired = channel.receive()!!.claim()
        expect(acquired === queued.isoDep && queued.isClaimed(), "Consumption claims the queued token")
        expect(OneKeyLiteCard.tryDiscover(queued) == null, "Queued discovery becomes stale after consumption")
        val waiting = async(start = CoroutineStart.UNDISPATCHED) {
            channel.receive()?.claim()?.let { tag -> OneKeyLiteCard.withCardSession(tag) { write(it) } }
        }
        val delivered = PendingTag(IsoDep("delivered-to-waiter"))
        expect(channel.trySend(delivered).isSuccess, "Waiting for a tag does not block discovery delivery")
        expect(waiting.await() == true, "Delivered unconnected tag completes active operation")
        channel.send(null)
        expect(channel.receive() == null && Probe.current == null, "Null acquisition cancellation holds no session")
        println("PASS consumed discovery and channel delivery")

        Probe.reset()
        val callbackTag = IsoDep("callback-owner")
        val callbackState = OneKeyLiteCard.withCardSession(callbackTag) { write(it); it.cardState }
        expect(Probe.current == null, "Ownership releases before caller completion callback")
        val callbackResult = OneKeyLiteCard.withCardSession(IsoDep("callback-reentry")) { write(it) }
        expect(callbackResult && callbackState.serialNum == "callback-owner", "Callback reentry uses a new owner and preserves earlier state")
        expect(Probe.finalized.get() == 2, "Reentrant completions each finalize once")
        println("PASS completion reentry with local state")
    }
    println("CardSession: $assertions assertions passed")
}
