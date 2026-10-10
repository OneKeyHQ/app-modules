const { spawnSync } = require('node:child_process');
const { mkdtempSync, writeFileSync } = require('node:fs');
const { tmpdir } = require('node:os');
const { join, resolve } = require('node:path');

const root = resolve(__dirname, '..');
const output = mkdtempSync(join(tmpdir(), 'device-utils-in-memory-store-'));
function run(command, args) {
  const result = spawnSync(command, args, { stdio: 'inherit' });
  if (result.error) throw result.error;
  if (result.status !== 0) throw new Error(`${command} exited ${result.status}`);
}

const swiftBinary = join(output, 'in-memory-store-swift');
run('swiftc', [
  join(root, 'nitrogen/generated/ios/swift/InMemoryValue.swift'),
  join(root, 'ios/InMemoryStore.swift'),
  join(__dirname, 'main.swift'),
  '-module-cache-path',
  join(output, 'swift-module-cache'),
  '-o',
  swiftBinary,
]);
run(swiftBinary, []);
run(swiftBinary, []);

const kotlinJar = join(output, 'in-memory-store-kotlin.jar');
// Only the shrinker marker is stubbed; compile the actual generated union type.
const annotation = join(output, 'DoNotStrip.kt');
writeFileSync(
  annotation,
  'package com.facebook.proguard.annotations\nannotation class DoNotStrip\n'
);
run('kotlinc', [
  annotation,
  join(root, 'nitrogen/generated/android/kotlin/com/margelo/nitro/reactnativedeviceutils/InMemoryValue.kt'),
  join(root, 'android/src/main/java/com/margelo/nitro/reactnativedeviceutils/InMemoryStore.kt'),
  join(__dirname, 'InMemoryStoreTest.kt'),
  '-include-runtime',
  '-d',
  kotlinJar,
]);
run('java', ['-jar', kotlinJar]);
run('java', ['-jar', kotlinJar]);
