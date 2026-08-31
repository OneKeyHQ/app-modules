package com.margelo.nitro.reactnativebundleupdate

// Dependency-free JVM tests compile the same policy file shipped on Android.
fun main() {
    val ledgerKeys = listOf("app-storage-v1", "jotai-storage-v1")
    val states = listOf("migrating-v1", "complete-v1", "resetting-v1", "future-state")
    var assertions = 0
    fun expect(actual: Boolean, expected: Boolean, scenario: String) {
        check(actual == expected) { "$scenario: expected $expected, got $actual" }
        assertions += 1
    }
    fun compatible(version: String?, ledger: Map<String, String> = emptyMap()): Boolean {
        return BundleStorageSchemaPolicy.isCompatible(version) { ledger[it] }
    }

    expect(compatible(null), true, "legacy app with missing marker")
    expect(compatible(""), true, "legacy app with empty marker")
    expect(compatible(null, ledgerKeys.associateWith { "" }), true, "empty ledgers")
    expect(compatible("mmkv-v1"), true, "MMKV bundle before migration")
    for (key in ledgerKeys) {
        for (state in states) {
            expect(compatible(null, mapOf(key to state)), false, "missing marker: $key=$state")
            expect(compatible("", mapOf(key to state)), false, "empty marker: $key=$state")
            expect(compatible("mmkv-v1", mapOf(key to state)), true, "MMKV marker: $key=$state")
        }
    }
    for (version in listOf("mmkv-v2", "MMKV-V1", " mmkv-v1 ", "mmkv-v1-preview", "asyncstorage-v1")) {
        expect(compatible(version), false, "unknown schema before migration: $version")
        expect(compatible(version, mapOf(ledgerKeys[0] to "complete-v1")), false, "unknown schema after migration: $version")
    }
    expect(compatible(null, mapOf("other-storage-v1" to "complete-v1")), true, "unrelated preferences")

    for (key in ledgerKeys) {
        val ledger = mutableMapOf<String, String>()
        val read: (String) -> String? = { ledger[it] }
        expect(BundleStorageSchemaPolicy.isCompatible(null, read), true, "before $key changes")
        for (state in listOf("migrating-v1", "complete-v1", "resetting-v1")) {
            ledger[key] = state
            expect(BundleStorageSchemaPolicy.isCompatible(null, read), false, "legacy bundle after $key changes to $state")
            expect(BundleStorageSchemaPolicy.isCompatible("mmkv-v1", read), true, "MMKV bundle after $key changes to $state")
        }
    }

    val requestedKeys = mutableSetOf<String>()
    expect(BundleStorageSchemaPolicy.isCompatible(null) { key ->
        requestedKeys.add(key)
        null
    }, true, "missing marker with no ledger")
    expect(requestedKeys == ledgerKeys.toSet(), true, "both host ledger keys are consulted")

    val readFailure = IllegalStateException("ledger unavailable")
    val result = runCatching {
        BundleStorageSchemaPolicy.isCompatible(null) { throw readFailure }
    }
    expect(result.exceptionOrNull() === readFailure, true, "ledger read errors must not permit a legacy bundle")

    for (version in listOf("mmkv-v1", "mmkv-v2")) {
        expect(BundleStorageSchemaPolicy.isCompatible(version) {
            error("A declared schema should not need a ledger read")
        }, version == "mmkv-v1", "marked bundle does not read the ledger: $version")
    }
    println("BundleStorageSchemaPolicy: $assertions assertions passed")
}
