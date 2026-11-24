package com.margelo.nitro.reactnativebundleupdate

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    var assertions = 0
    fun expect(condition: Boolean, scenario: String) {
        check(condition) { scenario }
        assertions++
    }

    val admission = BundleUpdateOperations()
    val worker = Executors.newSingleThreadExecutor()
    fun refusal(block: () -> Unit): BundleUpdateOperations.Refusal? =
        (runCatching(block).exceptionOrNull() as? BundleUpdateOperations.AdmissionException)?.reason

    try {
        // Two module instances share a registry even when they use different handles.
        val cancelled = CountDownLatch(1)
        val verify = admission.start("release-A") { cancelled.countDown() }
        expect(refusal { admission.start("release-A") } == BundleUpdateOperations.Refusal.BUSY, "same destination already owned")
        admission.finish(admission.start("release-B"))

        var deleted = false
        val cleanup = worker.submit<Boolean> {
            admission.cleanup(5, TimeUnit.SECONDS) {
                deleted = true
                expect(refusal { admission.start("release-B") } == BundleUpdateOperations.Refusal.CANCELLED, "cleanup holds admission through deletion")
            }
        }
        expect(cancelled.await(2, TimeUnit.SECONDS), "cleanup cancels verification from another module instance")
        expect(refusal { admission.start("release-B") } == BundleUpdateOperations.Refusal.CANCELLED, "cannot admit work during cleanup")
        expect(!cleanup.isDone, "cleanup must wait for the verification body to exit")
        var promoted = false
        expect(
            refusal { admission.publish(verify) { promoted = true } } == BundleUpdateOperations.Refusal.CANCELLED,
            "cancellation wins at the publication boundary",
        )
        expect(!promoted, "cancelled verification cannot promote its staging directory")
        admission.finish(verify)
        expect(cleanup.get(2, TimeUnit.SECONDS) && deleted, "cleanup deletes after the verification exits")

        val timedOut = admission.start("release-A")
        expect(!admission.cleanup(0, TimeUnit.NANOSECONDS) { error("deleted before the run stopped") }, "cleanup timeout preserves files")
        expect(timedOut.cancelled, "cleanup timeout still cancels the writer")
        expect(refusal { admission.start("release-A") } == BundleUpdateOperations.Refusal.BUSY, "timed-out writer retains its destination")
        admission.finish(admission.start("release-B"))
        admission.finish(timedOut)

        val success = admission.start("release-A") { error("already published operation must not be cancelled") }
        admission.publish(success) { promoted = true }
        expect(promoted, "an active operation can publish")
        expect(!admission.cleanup(0, TimeUnit.NANOSECONDS) { error("deleted before the run stopped") }, "cleanup also waits for a published operation to exit")
        expect(!success.cancelled, "successful publication wins before cleanup")
        admission.finish(success)
        expect(admission.cleanup(0, TimeUnit.NANOSECONDS) {}, "cleanup succeeds when no owners remain")
    } finally {
        worker.shutdownNow()
    }
    fun record(version: String) = mapOf("appVersion" to "6.8.0", "bundleVersion" to version, "signature" to version)
    val (afterB, evictedByB) = planFallbackBundles(listOf("B", "C", "D").map(::record), "6.8.0", "B", record("A"))
    expect(afterB == listOf("C", "D", "A").map(::record), "activating oldest fallback retains the previous active release")
    expect(evictedByB.isEmpty(), "activating oldest fallback cannot evict its files")
    val (afterE, evictedByE) = planFallbackBundles(afterB, "6.8.0", "E", record("B"))
    expect(afterE == listOf("D", "A", "B").map(::record), "next activation records the latest active bundle")
    expect(evictedByE == listOf(record("C")), "only the oldest retained release is evicted")
    println("BundleUpdateOperations: $assertions assertions passed")
}
