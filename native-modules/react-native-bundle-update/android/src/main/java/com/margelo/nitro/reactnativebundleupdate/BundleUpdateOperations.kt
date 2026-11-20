package com.margelo.nitro.reactnativebundleupdate

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class BundleUpdateOperations {
    enum class Refusal { BUSY, CANCELLED }

    class AdmissionException(val reason: Refusal) : Exception()

    class Operation internal constructor(internal val key: String, internal val cancel: () -> Unit) {
        @Volatile
        var cancelled = false
            internal set

        internal var published = false
        internal val stopped = CountDownLatch(1)

        fun ensureActive() {
            if (cancelled) throw AdmissionException(Refusal.CANCELLED)
        }
    }

    private val lock = Any()
    private val cleanupLock = Any()
    private val active = mutableMapOf<String, Operation>()
    private var clearing = false

    fun start(key: String, cancel: () -> Unit = {}): Operation = synchronized(lock) {
        if (clearing) throw AdmissionException(Refusal.CANCELLED)
        if (key in active) throw AdmissionException(Refusal.BUSY)
        Operation(key, cancel).also { active[key] = it }
    }

    fun publish(operation: Operation, publish: () -> Unit = {}) = synchronized(lock) {
        operation.ensureActive()
        check(active[operation.key] === operation)
        publish()
        // Publication and cleanup cancellation contend for the same lock. Once
        // published, the run can succeed, but its slot is held until work exits.
        operation.published = true
    }

    fun finish(operation: Operation) = synchronized(lock) {
        active.remove(operation.key, operation)
        operation.stopped.countDown()
    }

    fun cleanup(timeout: Long, unit: TimeUnit, delete: () -> Unit): Boolean = synchronized(cleanupLock) {
        val operations = synchronized(lock) {
            clearing = true
            active.values.toList().also { runs ->
                for (run in runs) if (!run.published) run.cancelled = true
            }
        }
        try {
            val deadline = System.nanoTime() + unit.toNanos(timeout)
            operations.filter { it.cancelled }.forEach { it.cancel() }
            for (run in operations) {
                val remaining = (deadline - System.nanoTime()).coerceAtLeast(0L)
                val stopped = try {
                    run.stopped.await(remaining, TimeUnit.NANOSECONDS)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    false
                }
                if (!stopped) return@synchronized false
            }
            delete()
            true
        } finally {
            synchronized(lock) { clearing = false }
        }
    }
}
