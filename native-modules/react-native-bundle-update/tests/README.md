# Storage schema compatibility

This native OTA guard replaces the bundle-update patch in
[app-monorepo#13012](https://github.com/OneKeyHQ/app-monorepo/pull/13012).
It prevents old JavaScript bundles from returning to retained AsyncStorage data
once either AppStorage or Jotai has started migrating to MMKV.

## Backward compatibility

| Native migration ledger                                | OTA `storageSchemaVersion`       | Result |
| ------------------------------------------------------ | -------------------------------- | ------ |
| Neither ledger started                                 | Missing or empty (legacy bundle) | Accept |
| Either ledger non-empty, including migrating/resetting | Missing or empty                 | Reject |
| Any                                                    | `mmkv-v1`                        | Accept |
| Any                                                    | Unknown non-empty schema         | Reject |

Existing JS/Nitro methods, required parameters, and generated bindings are
unchanged. Android's original two-argument layout validator remains available;
startup and extracted-bundle validation use the context-aware overload.
The migration decision is rechecked even when entry-bundle hashes are cached.
No storage data is deleted or migrated here, and the guard never opens MMKV or
AsyncStorage. It reads the host app's native preferences, shared by main/bg.

This does **not** retrofit migration support into an already installed old native
binary. MMKV-migration OTA bundles must target a host binary that includes the
migration native module; the existing app-version/build constraints still apply.
Rejecting legacy OTA bundles after migration is intentional data protection, not
an API-compatibility break. Shipping the module in an old, unmigrated app leaves
its legacy calls and unmarked OTA bundles supported.

## Run the tests

From this module directory:

```sh
swift test --scratch-path /tmp/bundle-storage-schema-swift
node --test tests/storage-schema-wiring.test.mjs

# Kotlin CLI and a JDK are required; no Android SDK, JSI, or Gradle host is needed.
kotlinc android/src/main/java/com/margelo/nitro/reactnativebundleupdate/BundleStorageSchemaPolicy.kt \
  tests/kotlin/BundleStorageSchemaPolicyTest.kt \
  -include-runtime -d /tmp/bundle-storage-schema-tests.jar
java -jar /tmp/bundle-storage-schema-tests.jar
```

Swift and Kotlin tests exercise the shipping policy sources. Node tests check
native call-site wiring, cached revalidation, metadata filtering, and the retained
Android signature. They are not a substitute for a signed-OTA install/restart
test on iOS and Android.

## Rollout

Merge and publish a new bundle-update package first. Then update app-monorepo's
dependency/lockfiles and remove its `@onekeyfe+react-native-bundle-update` patch
in the same change. Keep the signed `storageSchemaVersion` metadata emitted by
app-monorepo. Do not remove the existing patch while still depending on 3.0.95.
