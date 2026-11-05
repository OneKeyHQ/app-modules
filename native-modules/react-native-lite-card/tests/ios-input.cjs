const { spawnSync } = require('node:child_process');
const { mkdtempSync, rmSync } = require('node:fs');
const { tmpdir } = require('node:os');
const { join, resolve } = require('node:path');

const root = resolve(__dirname, '..');
const output = mkdtempSync(join(tmpdir(), 'lite-card-input-'));
function run(command, args) {
  const result = spawnSync(command, args, { stdio: 'inherit' });
  if (result.error) throw result.error;
  if (result.status !== 0) throw new Error(`${command} exited ${result.status}`);
}
try {
  // Compile the actual manager; only CoreNFC and its card/transport adapters are fakes.
  run('xcrun', ['clang', '-fobjc-arc', '-fblocks', '-framework', 'Foundation',
    '-Wno-nullability-completeness', '-Wno-protocol', '-Wno-incomplete-implementation',
    '-I', join(__dirname, 'objc'),
    ...['', '/OKNFTLite', '/OKLiteCommand', '/Utils'].flatMap((directory) =>
      ['-I', join(root, `ios/Classes${directory}`)]),
    join(root, 'ios/Classes/OKNFTLite/OKNFCManager.m'),
    join(__dirname, 'objc/ManagerInputTest.m'), '-o', join(output, 'input-tests')]);
  run(join(output, 'input-tests'), []);
} finally {
  rmSync(output, { recursive: true, force: true });
}
