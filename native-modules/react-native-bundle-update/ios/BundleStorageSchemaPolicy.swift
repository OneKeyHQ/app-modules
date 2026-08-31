enum BundleStorageSchemaPolicy {
    static let metadataKey = "storageSchemaVersion"
    static let supportedVersion = "mmkv-v1"
    static let ledgerPrefix = "onekey_native_storage_migration_"
    private static let ledgerKeys = ["app-storage-v1", "jotai-storage-v1"]

    static func isCompatible(
        declaredVersion: String?,
        readMigrationLedger: (String) -> String?
    ) -> Bool {
        if let declaredVersion, !declaredVersion.isEmpty {
            return declaredVersion == supportedVersion
        }

        // Old apps do not write this ledger and may keep using unmarked bundles.
        // Any non-empty state (including an interrupted migration or reset)
        // forbids returning to the retained AsyncStorage database.
        return !ledgerKeys.contains { key in
            !(readMigrationLedger(ledgerPrefix + key) ?? "").isEmpty
        }
    }
}
