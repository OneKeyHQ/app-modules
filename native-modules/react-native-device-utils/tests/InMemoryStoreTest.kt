package com.margelo.nitro.reactnativedeviceutils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val store = InMemoryStore
    check(store.get("startup")?.asSecondOrNull() == null) { "A fresh process must start empty" }
    check(store.setIfAbsent("startup", InMemoryValue.Second("first")))
    check(!InMemoryStore.setIfAbsent("startup", InMemoryValue.Second("reload")))
    check(store.get("startup")?.asSecondOrNull() == "first")
    store.set("other", InMemoryValue.Second(""))
    check(store.get("other")?.asSecondOrNull() == "")
    check(!store.setIfAbsent("other", InMemoryValue.Second("replacement")))
    check(store.get("startup")?.asSecondOrNull() == "first")
    store.set("other", InMemoryValue.Second("updated"))
    check(store.get("other")?.asSecondOrNull() == "updated")
    check(store.remove("other"))
    check(!store.remove("other"))
    check(store.setIfAbsent("other", InMemoryValue.Second("retry")))

    val pool = Executors.newFixedThreadPool(16)
    val start = CountDownLatch(1)
    val claims = (0 until 256).map {
        pool.submit<Boolean> {
            start.await()
            InMemoryStore.setIfAbsent("race", InMemoryValue.Second("winner"))
        }
    }
    start.countDown()
    val results = try {
        claims.map { it.get() }
    } finally {
        pool.shutdown()
    }
    check(results.count { it } == 1) { "Concurrent runtimes must have one winner" }
    check(results.count { !it } == 255) { "Other claims must return false without throwing" }
    check(store.get("race")?.asSecondOrNull() == "winner")

    // Keep false and zero distinct from missing keys and preserve overwrite types.
    check(store.setIfAbsent("primitive", InMemoryValue.First(false)))
    check(store.get("primitive")?.asFirstOrNull() == false)
    check(store.get("primitive")?.asThirdOrNull() == null)
    check(!store.setIfAbsent("primitive", InMemoryValue.Third(0.0)))
    store.set("primitive", InMemoryValue.First(true))
    check(store.get("primitive")?.asFirstOrNull() == true)
    store.set("primitive", InMemoryValue.Third(0.0))
    check(store.get("primitive")?.asThirdOrNull() == 0.0)
    check(store.get("primitive")?.asFirstOrNull() == null)
    check(!store.setIfAbsent("primitive", InMemoryValue.First(false)))
    store.set("primitive", InMemoryValue.Third(-0.0))
    check(store.get("primitive")?.asThirdOrNull()?.toRawBits() == (-0.0).toRawBits())
    for (number in listOf(-42.0, 1.5, Double.NaN, Double.POSITIVE_INFINITY,
                         Double.NEGATIVE_INFINITY, Double.MAX_VALUE, Double.MIN_VALUE)) {
        store.set("primitive", InMemoryValue.Third(number))
        val actual = store.get("primitive")!!.asThirdOrNull()!!
        check(if (number.isNaN()) actual.isNaN() else actual == number)
    }
    store.set("primitive", InMemoryValue.Second("false"))
    check(store.get("primitive")?.asSecondOrNull() == "false")
    check(store.get("primitive")?.asFirstOrNull() == null)
    check(store.remove("primitive"))
    check(store.get("primitive") == null)
    check(store.setIfAbsent("primitive", InMemoryValue.Third(0.0)))
    check(store.remove("primitive"))
    check(store.setIfAbsent("primitive", InMemoryValue.First(false)))
    check(store.remove("primitive"))

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
    store.set(maxKey, InMemoryValue.Second(maxValue))
    check(store.get(maxKey)?.asSecondOrNull() == maxValue)
    assertRejected(keyError) { store.set(maxKey + "x", InMemoryValue.Second("value")) }
    assertRejected(keyError) { store.setIfAbsent(maxKey + "x", InMemoryValue.Second("value")) }
    assertRejected(keyError) { store.get(maxKey + "x")?.asSecondOrNull() }
    assertRejected(keyError) { store.remove(maxKey + "x") }
    assertRejected(valueError) { store.set(maxKey, InMemoryValue.Second(maxValue + "x")) }
    assertRejected(valueError) { store.setIfAbsent(maxKey, InMemoryValue.Second(maxValue + "x")) }
    assertRejected(valueError) { store.setIfAbsent("oversized", InMemoryValue.Second(maxValue + "x")) }
    check(store.get(maxKey)?.asSecondOrNull() == maxValue)
    check(store.get("oversized")?.asSecondOrNull() == null)
    val asciiKey = "k".repeat(256)
    val asciiValue = "v".repeat(4096)
    store.set(asciiKey, InMemoryValue.Second(asciiValue))
    assertRejected(keyError) { store.set(asciiKey + "x", InMemoryValue.Second("value")) }
    assertRejected(valueError) { store.set(asciiKey, InMemoryValue.Second(asciiValue + "x")) }
    check(store.get(asciiKey)?.asSecondOrNull() == asciiValue)
    store.set("", InMemoryValue.Second(""))
    check(store.get("")?.asSecondOrNull() == "")
    store.set("\u00e9", InMemoryValue.Second("composed"))
    check(store.setIfAbsent("e\u0301", InMemoryValue.Second("decomposed")))
    check(store.get("\u00e9")?.asSecondOrNull() == "composed")
    check(store.get("e\u0301")?.asSecondOrNull() == "decomposed")
    for (key in listOf("startup", "other", "race", maxKey, asciiKey, "", "\u00e9", "e\u0301")) {
        check(store.remove(key))
    }
    repeat(128) { check(store.setIfAbsent("slot:$it", InMemoryValue.Second("first"))) }
    assertRejected(capacityError) { store.set("overflow", InMemoryValue.Second("value")) }
    assertRejected(capacityError) { store.setIfAbsent("overflow", InMemoryValue.Second("value")) }
    check(store.get("overflow")?.asSecondOrNull() == null)
    store.set("slot:0", InMemoryValue.Second("updated"))
    check(store.get("slot:0")?.asSecondOrNull() == "updated")
    check(!store.setIfAbsent("slot:0", InMemoryValue.Second("replacement")))
    check(store.remove("slot:0"))
    check(store.setIfAbsent("overflow", InMemoryValue.Second("retry")))
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
                check(store.setIfAbsent("capacity:$index", InMemoryValue.Second("winner")))
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

    val directory = kotlin.io.path.createTempDirectory("device-utils-first-launch-").toFile()
    try {
        fun launch(version: String, previous: String? = null) =
            FirstLaunchHistory(directory, version, previous, hasPreviousLaunch = previous != null)

        val fresh = launch("6.5.0")
        check(fresh.isFirstLaunch && fresh.isFirstLaunchForCurrentAppVersion)
        check(!fresh.isFirstLaunchForBundle(null))
        check(fresh.isFirstLaunchForBundle("1000") && fresh.isFirstLaunchForBundle("1000"))
        check(fresh.isFirstLaunchForBundle(null))
        // Explicit lookup of another bundle must not overwrite zero-arg startup bundle result.
        check(!fresh.isFirstLaunchForBundle("999"))
        check(fresh.isFirstLaunchForBundle(null))
        // Reject non-numeric version core and traversal strings.
        check(!fresh.isFirstLaunchForBundle("v1000"))
        check(!fresh.isFirstLaunchForBundle("../escape"))

        val restart = launch("6.5.0")
        check(!restart.isFirstLaunch && !restart.isFirstLaunchForCurrentAppVersion)
        check(!restart.isFirstLaunchForBundle("1000"))
        check(!restart.isFirstLaunchForBundle(null))
        check(restart.isFirstLaunchForBundle("1001") && restart.isFirstLaunchForBundle("1001"))
        check(!restart.isFirstLaunchForBundle(null))

        val rollback = launch("6.5.0")
        check(!rollback.isFirstLaunch && !rollback.isFirstLaunchForCurrentAppVersion)
        check(!rollback.isFirstLaunchForBundle("1000"))
        check(!rollback.isFirstLaunchForBundle("999"))

        val upgrade = launch("6.10.0", previous = "6.5.0")
        check(!upgrade.isFirstLaunch && upgrade.isFirstLaunchForCurrentAppVersion)
        check(!upgrade.isFirstLaunchForBundle("1001"))
        check(upgrade.isFirstLaunchForBundle("1002"))

        val downgrade = launch("6.9.0", previous = "6.10.0")
        check(!downgrade.isFirstLaunch && !downgrade.isFirstLaunchForCurrentAppVersion)
        check(!downgrade.isFirstLaunchForBundle("1001"))

        val legacySame = FirstLaunchHistory(directory.resolve("legacy-same"), "6.5.0", "6.5.0", hasPreviousLaunch = true)
        check(!legacySame.isFirstLaunch && !legacySame.isFirstLaunchForCurrentAppVersion)

        val legacyUpgraded = FirstLaunchHistory(directory.resolve("legacy-upgraded"), "6.6.0", "6.5.0", hasPreviousLaunch = true)
        check(!legacyUpgraded.isFirstLaunch && legacyUpgraded.isFirstLaunchForCurrentAppVersion)

        // Corrupted individual files must fail closed without overwriting or breaking healthy files.
        val corruptPayloads = listOf("", "6.6.0!", "v1000")
        corruptPayloads.forEachIndexed { idx, bad ->
            val corruptBase = directory.resolve("corrupt-$idx")
            val corruptStore = corruptBase.resolve(FirstLaunchHistory.DIRECTORY_NAME).apply { mkdirs() }
            corruptStore.resolve(FirstLaunchHistory.LAUNCHED_FILE_NAME).writeText("1")
            val badAppFile = corruptStore.resolve(FirstLaunchHistory.APP_VERSION_FILE_NAME).apply { writeText(bad) }
            val corrupted = FirstLaunchHistory(corruptBase, "6.5.0", null, hasPreviousLaunch = false)
            check(!corrupted.isFirstLaunch && !corrupted.isFirstLaunchForCurrentAppVersion)
            check(badAppFile.readText() == bad)
            check(corrupted.isFirstLaunchForBundle("1000"))
        }

        val file = directory.resolve("file").apply { writeText("") }
        val failed = FirstLaunchHistory(file, "6.5.0", null, hasPreviousLaunch = false)
        check(!failed.isFirstLaunch && !failed.isFirstLaunchForCurrentAppVersion && !failed.isFirstLaunchForBundle("1000"))
    } finally {
        directory.deleteRecursively()
    }
    println("Kotlin first launch history: passed")
}
