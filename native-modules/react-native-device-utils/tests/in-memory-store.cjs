const { spawnSync } = require('node:child_process');
const { mkdtempSync } = require('node:fs');
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
run('kotlinc', [
  join(root, 'android/src/main/java/com/margelo/nitro/reactnativedeviceutils/InMemoryStore.kt'),
  join(__dirname, 'InMemoryStoreTest.kt'),
  '-include-runtime',
  '-d',
  kotlinJar,
]);
run('java', ['-jar', kotlinJar]);
run('java', ['-jar', kotlinJar]);
