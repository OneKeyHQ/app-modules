package com.margelo.nitro.reactnativebundleupdate

// Dependency-free JVM tests compile the same policy file shipped on Android.
fun main() {
    val ledgerKeys = listOf("app-storage-v1", "jotai-storage-v1")
    val states = listOf("migrating-v1", "complete-v1", "resetting-v1", "future-state")
    var assertions = 0
    fun expect(actual: Boolean, expected: Boolean) {
        check(actual == expected) { "Expected $expected, got $actual" }
        assertions += 1
    }
    fun compatible(version: String?, ledger: Map<String, String> = emptyMap()): Boolean {
        return BundleStorageSchemaPolicy.isCompatible(version) { ledger[it] }
    }

    expect(compatible(null), true)
    expect(compatible(""), true)
    expect(compatible(null, ledgerKeys.associateWith { "" }), true)
    expect(compatible("mmkv-v1"), true)
    for (key in ledgerKeys) {
        for (state in states) {
            expect(compatible(null, mapOf(key to state)), false)
            expect(compatible("", mapOf(key to state)), false)
            expect(compatible("mmkv-v1", mapOf(key to state)), true)
        }
    }
    for (version in listOf("mmkv-v2", "MMKV-V1", " mmkv-v1 ", "asyncstorage-v1")) {
        expect(compatible(version), false)
        expect(compatible(version, mapOf(ledgerKeys[0] to "complete-v1")), false)
    }
    expect(compatible(null, mapOf("other-storage-v1" to "complete-v1")), true)

    val ledger = mutableMapOf<String, String>()
    val read: (String) -> String? = { ledger[it] }
    expect(BundleStorageSchemaPolicy.isCompatible(null, read), true)
    ledger[ledgerKeys[1]] = "migrating-v1"
    expect(BundleStorageSchemaPolicy.isCompatible(null, read), false)
    ledger[ledgerKeys[1]] = "resetting-v1"
    expect(BundleStorageSchemaPolicy.isCompatible(null, read), false)

    for (version in listOf("mmkv-v1", "mmkv-v2")) {
        expect(BundleStorageSchemaPolicy.isCompatible(version) {
            error("A declared schema should not need a ledger read")
        }, version == "mmkv-v1")
    }
    println("BundleStorageSchemaPolicy: $assertions assertions passed")
}
