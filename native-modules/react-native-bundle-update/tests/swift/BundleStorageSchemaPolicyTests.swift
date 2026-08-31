import XCTest
@testable import BundleStorageSchemaPolicy

final class BundleStorageSchemaPolicyTests: XCTestCase {
    private let ledgerKeys = [
        "onekey_native_storage_migration_app-storage-v1",
        "onekey_native_storage_migration_jotai-storage-v1",
    ]
    private let states = ["migrating-v1", "complete-v1", "resetting-v1", "future-state"]

    private func isCompatible(_ version: String?, ledger: [String: String] = [:]) -> Bool {
        BundleStorageSchemaPolicy.isCompatible(declaredVersion: version) { ledger[$0] }
    }

    func testLegacyAppsAcceptMissingAndEmptyMarkers() {
        XCTAssertTrue(isCompatible(nil))
        XCTAssertTrue(isCompatible(""))
        XCTAssertTrue(isCompatible(nil, ledger: Dictionary(uniqueKeysWithValues: ledgerKeys.map { ($0, "") })))
    }

    func testEitherStorageMigrationRejectsLegacyBundlesInEveryState() {
        for key in ledgerKeys {
            for state in states {
                XCTAssertFalse(isCompatible(nil, ledger: [key: state]), "\(key):\(state)")
                XCTAssertFalse(isCompatible("", ledger: [key: state]), "\(key):\(state)")
            }
        }
    }

    func testMMKVBundlesWorkBeforeDuringAndAfterMigration() {
        XCTAssertTrue(isCompatible("mmkv-v1"))
        for key in ledgerKeys {
            for state in states {
                XCTAssertTrue(isCompatible("mmkv-v1", ledger: [key: state]))
            }
        }
    }

    func testUnsupportedSchemaIsRejectedEvenWithoutMigration() {
        for version in ["mmkv-v2", "MMKV-V1", " mmkv-v1 ", "mmkv-v1-preview", "asyncstorage-v1"] {
            XCTAssertFalse(isCompatible(version))
            XCTAssertFalse(isCompatible(version, ledger: [ledgerKeys[0]: "complete-v1"]))
        }
    }

    func testUnrelatedPreferencesDoNotOptOldAppsIntoMigration() {
        XCTAssertTrue(isCompatible(nil, ledger: ["other-storage-v1": "complete-v1"]))
    }

    func testLedgerChangesAreObservedBetweenPolicyEvaluations() {
        for key in ledgerKeys {
            var ledger: [String: String] = [:]
            let read = { (key: String) in ledger[key] }
            XCTAssertTrue(BundleStorageSchemaPolicy.isCompatible(declaredVersion: nil, readMigrationLedger: read))
            for state in ["migrating-v1", "complete-v1", "resetting-v1"] {
                ledger[key] = state
                XCTAssertFalse(BundleStorageSchemaPolicy.isCompatible(declaredVersion: nil, readMigrationLedger: read))
                XCTAssertTrue(BundleStorageSchemaPolicy.isCompatible(declaredVersion: "mmkv-v1", readMigrationLedger: read))
            }
        }
    }

    func testMissingMarkerReadsBothHostLedgerKeys() {
        var requestedKeys: Set<String> = []
        let compatible = BundleStorageSchemaPolicy.isCompatible(declaredVersion: nil) { key in
            requestedKeys.insert(key)
            return nil
        }
        XCTAssertTrue(compatible)
        XCTAssertEqual(requestedKeys, Set(ledgerKeys))
    }

    func testMarkedBundlesDoNotReadStorage() {
        for version in ["mmkv-v1", "mmkv-v2"] {
            let compatible = BundleStorageSchemaPolicy.isCompatible(declaredVersion: version) { _ in
                XCTFail("A declared schema should not need a ledger read")
                return nil
            }
            XCTAssertEqual(compatible, version == "mmkv-v1")
        }
    }
}
