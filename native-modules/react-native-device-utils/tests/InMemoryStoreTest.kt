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
    println("Kotlin in-memory store: passed")
}
