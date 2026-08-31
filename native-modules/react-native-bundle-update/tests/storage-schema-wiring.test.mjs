import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) =>
  readFileSync(new URL(`../${path}`, import.meta.url), 'utf8');
const android = read(
  'android/src/main/java/com/margelo/nitro/reactnativebundleupdate/ReactNativeBundleUpdate.kt'
);
const ios = read('ios/ReactNativeBundleUpdate.swift');

test('Android preserves the legacy native layout-validator signature', () => {
  assert.match(
    android,
    /fun validateBundlePairCompatibility\(bundleDir: String, metadata: Map<String, String>\): Boolean/
  );
  assert.match(
    android,
    /return validateStorageSchemaCompatibility\(context, metadata\) &&\s+validateBundlePairCompatibility\(bundleDir, metadata\)/
  );
});

test('all Android bundle validation paths pass native context to the storage gate', () => {
  for (const directory of [
    'bundleDir',
    'destination',
    'bundlePath.absolutePath',
  ]) {
    assert.ok(
      android.includes(
        `validateBundlePairCompatibility(context, ${directory}, metadata)`
      )
    );
  }
});

test('all iOS bundle validation paths share the storage gate', () => {
  assert.match(
    ios,
    /static func validateBundlePairCompatibility\([\s\S]*?\) -> Bool \{\s+if !validateStorageSchemaCompatibility\(metadata\)/
  );
  for (const directory of ['folderName', 'destination', 'bundlePath']) {
    assert.ok(
      ios.includes(
        `validateBundlePairCompatibility(bundleDirPath: ${directory}, metadata: metadata)`
      )
    );
  }
});

test('cached bundle validation rechecks the live migration ledger on both platforms', () => {
  assert.match(
    android,
    /if \(cached.currentBundleVersion == currentBundleVersion\) \{[\s\S]*?if \(!validateStorageSchemaCompatibility\(context, cached.metadata\)\) \{\s+invalidateValidatedBundleInfoCache\(\)\s+return null\s+\}\s+return cached/
  );
  assert.match(
    ios,
    /if let cached = cachedValidatedBundleInfo,[\s\S]*?if !validateStorageSchemaCompatibility\(cached.metadata\) \{\s+invalidateValidatedBundleInfoCache\(\)\s+return nil\s+\}\s+return cached/
  );
});

test('the schema marker is metadata, not a file hash', () => {
  assert.match(
    android,
    /private fun isReservedMetadataKey\([\s\S]*?key == BundleStorageSchemaPolicy.METADATA_KEY/
  );
  assert.match(
    ios,
    /private static func isReservedMetadataKey\([\s\S]*?key == BundleStorageSchemaPolicy.metadataKey/
  );
});

test('the guard reads only native migration preferences, never AsyncStorage or MMKV', () => {
  const androidGate = android.slice(
    android.indexOf('private fun validateStorageSchemaCompatibility'),
    android.indexOf('fun readMetadataFileSha256')
  );
  const iosGate = ios.slice(
    ios.indexOf('private static func validateStorageSchemaCompatibility'),
    ios.indexOf('private static func fileMetadataEntries')
  );
  assert.match(
    androidGate,
    /getSharedPreferences\(BundleStorageSchemaPolicy.LEDGER_PREFS_NAME, Context.MODE_PRIVATE\)/
  );
  assert.match(iosGate, /UserDefaults.standard.string\(forKey: key\)/);
  assert.doesNotMatch(androidGate + iosGate, /MMKV|AsyncStorage/);
});
