package com.margelo.nitro.reactnativedeviceutils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val store = InMemoryStore
    check(store.get("startup") == null) { "A fresh process must start empty" }
    check(store.setIfAbsent("startup", "first"))
    check(!InMemoryStore.setIfAbsent("startup", "reload"))
    check(store.get("startup") == "first")
    store.set("other", "")
    check(store.get("other") == "")
    check(!store.setIfAbsent("other", "replacement"))
    check(store.get("startup") == "first")
    store.set("other", "updated")
    check(store.get("other") == "updated")
    check(store.remove("other"))
    check(!store.remove("other"))
    check(store.setIfAbsent("other", "retry"))

    val pool = Executors.newFixedThreadPool(16)
    val start = CountDownLatch(1)
    val done = CountDownLatch(256)
    val winners = AtomicInteger()
    repeat(256) {
        pool.submit {
            try {
                start.await()
                if (InMemoryStore.setIfAbsent("race", "winner")) winners.incrementAndGet()
            } finally {
                done.countDown()
            }
        }
    }
    start.countDown()
    done.await()
    pool.shutdown()
    check(winners.get() == 1) { "Concurrent runtimes must have one winner" }
    check(store.get("race") == "winner")

    fun assertRejected(expectedMessage: String, operation: () -> Unit) {
        val failure = runCatching(operation).exceptionOrNull()
        check(failure is IllegalArgumentException || failure is IllegalStateException)
        check(failure.message == expectedMessage)
    }
    val keyError = "InMemoryStore key exceeds 256 UTF-8 bytes"
    val valueError = "InMemoryStore value exceeds 4096 UTF-8 bytes"
    val capacityError = "InMemoryStore capacity exceeds 128 entries"
    val maxKey = "é".repeat(128)
    val maxValue = "😀".repeat(1024)
    store.set(maxKey, maxValue)
    check(store.get(maxKey) == maxValue)
    assertRejected(keyError) { store.set(maxKey + "x", "value") }
    assertRejected(keyError) { store.setIfAbsent(maxKey + "x", "value") }
    assertRejected(keyError) { store.get(maxKey + "x") }
    assertRejected(keyError) { store.remove(maxKey + "x") }
    assertRejected(valueError) { store.set(maxKey, maxValue + "x") }
    assertRejected(valueError) { store.setIfAbsent(maxKey, maxValue + "x") }
    assertRejected(valueError) { store.setIfAbsent("oversized", maxValue + "x") }
    check(store.get(maxKey) == maxValue)
    check(store.get("oversized") == null)
    val asciiKey = "k".repeat(256)
    val asciiValue = "v".repeat(4096)
    store.set(asciiKey, asciiValue)
    assertRejected(keyError) { store.set(asciiKey + "x", "value") }
    assertRejected(valueError) { store.set(asciiKey, asciiValue + "x") }
    check(store.get(asciiKey) == asciiValue)
    store.set("", "")
    check(store.get("") == "")
    store.set("\u00e9", "composed")
    check(store.setIfAbsent("e\u0301", "decomposed"))
    check(store.get("\u00e9") == "composed")
    check(store.get("e\u0301") == "decomposed")
    for (key in listOf("startup", "other", "race", maxKey, asciiKey, "", "\u00e9", "e\u0301")) {
        check(store.remove(key))
    }
    repeat(128) { check(store.setIfAbsent("slot:$it", "first")) }
    assertRejected(capacityError) { store.set("overflow", "value") }
    assertRejected(capacityError) { store.setIfAbsent("overflow", "value") }
    check(store.get("overflow") == null)
    store.set("slot:0", "updated")
    check(store.get("slot:0") == "updated")
    check(!store.setIfAbsent("slot:0", "replacement"))
    check(store.remove("slot:0"))
    check(store.setIfAbsent("overflow", "retry"))
    for (index in 1 until 128) check(store.remove("slot:$index"))
    check(store.remove("overflow"))

    val capacityPool = Executors.newFixedThreadPool(16)
    val capacityStart = CountDownLatch(1)
    val capacityWinners = AtomicInteger()
    val rejectedClaims = AtomicInteger()
    val tasks = (0 until 256).map { index ->
        capacityPool.submit {
            capacityStart.await()
            try {
                check(store.setIfAbsent("capacity:$index", "winner"))
                capacityWinners.incrementAndGet()
            } catch (error: IllegalStateException) {
                check(error.message == capacityError)
                rejectedClaims.incrementAndGet()
            }
        }
    }
    capacityStart.countDown()
    try {
        tasks.forEach { it.get() }
    } finally {
        capacityPool.shutdown()
    }
    check(capacityWinners.get() == 128 && rejectedClaims.get() == 128)
    println("Kotlin in-memory store: passed")
}
