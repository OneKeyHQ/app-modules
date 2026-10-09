package com.margelo.nitro.reactnativedeviceutils

// Process-owned storage, independent of HybridObjects and React runtimes.
internal object ProcessMemoryStore {
    private val values = mutableMapOf<String, String>()

    @Synchronized
    fun get(key: String): String? = values[key]

    @Synchronized
    fun set(key: String, value: String) {
        values[key] = value
    }

    @Synchronized
    fun remove(key: String): Boolean = values.remove(key) != null

    @Synchronized
    fun setIfAbsent(key: String, value: String): Boolean {
        if (values.containsKey(key)) return false
        values[key] = value
        return true
    }
}
