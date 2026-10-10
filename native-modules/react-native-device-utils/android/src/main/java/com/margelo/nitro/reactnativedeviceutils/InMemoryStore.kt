package com.margelo.nitro.reactnativedeviceutils

// Process-owned storage, independent of HybridObjects and React runtimes.
internal object InMemoryStore {
    private const val MAX_ENTRIES = 128
    private const val MAX_KEY_BYTES = 256
    private const val MAX_VALUE_BYTES = 4096
    private val values = mutableMapOf<String, String>()

    private fun validateKey(key: String) {
        require(key.length <= MAX_KEY_BYTES && key.toByteArray(Charsets.UTF_8).size <= MAX_KEY_BYTES) {
            "InMemoryStore key exceeds 256 UTF-8 bytes"
        }
    }

    private fun validateValue(value: String) {
        require(value.length <= MAX_VALUE_BYTES && value.toByteArray(Charsets.UTF_8).size <= MAX_VALUE_BYTES) {
            "InMemoryStore value exceeds 4096 UTF-8 bytes"
        }
    }

    @Synchronized
    fun get(key: String): String? {
        validateKey(key)
        return values[key]
    }

    @Synchronized
    fun set(key: String, value: String) {
        validateKey(key)
        validateValue(value)
        check(values.containsKey(key) || values.size < MAX_ENTRIES) {
            "InMemoryStore capacity exceeds 128 entries"
        }
        values[key] = value
    }

    @Synchronized
    fun remove(key: String): Boolean {
        validateKey(key)
        return values.remove(key) != null
    }

    @Synchronized
    fun setIfAbsent(key: String, value: String): Boolean {
        validateKey(key)
        validateValue(value)
        if (values.containsKey(key)) return false
        check(values.size < MAX_ENTRIES) { "InMemoryStore capacity exceeds 128 entries" }
        values[key] = value
        return true
    }
}
