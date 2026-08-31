package com.margelo.nitro.reactnativebundleupdate

internal object BundleStorageSchemaPolicy {
    const val METADATA_KEY = "storageSchemaVersion"
    const val SUPPORTED_VERSION = "mmkv-v1"
    const val LEDGER_PREFS_NAME = "onekey_native_storage_migration"
    private val ledgerKeys = listOf("app-storage-v1", "jotai-storage-v1")

    fun isCompatible(
        declaredVersion: String?,
        readMigrationLedger: (String) -> String?,
    ): Boolean {
        if (!declaredVersion.isNullOrEmpty()) {
            return declaredVersion == SUPPORTED_VERSION
        }

        // Old apps do not write this ledger and may keep using unmarked bundles.
        // Any non-empty state (including an interrupted migration or reset)
        // forbids returning to the retained AsyncStorage database.
        return ledgerKeys.none { key -> !readMigrationLedger(key).isNullOrEmpty() }
    }
}
