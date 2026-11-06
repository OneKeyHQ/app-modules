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
  const sdkBuild = join(output, 'sdk');
  run('cmake', ['-S', join(root, 'sdk'), '-B', sdkBuild, '-DBUILD_TESTING=OFF']);
  run('cmake', ['--build', sdkBuild, '--parallel', '4']);
  // Compile the actual manager, SDK bridge and LCLogger; only device/card adapters are fakes.
  run('xcrun', ['clang++', '-fobjc-arc', '-fblocks', '-framework', 'Foundation',
    '-Wno-nullability-completeness', '-Wno-protocol', '-Wno-incomplete-implementation',
    '-I', join(__dirname, 'objc'),
    '-I', join(root, 'sdk/include'),
    ...['', '/OKNFTLite', '/OKLiteCommand', '/Utils'].flatMap((directory) =>
      ['-I', join(root, `ios/Classes${directory}`)]),
    join(root, 'ios/Classes/OKNFTLite/OKNFCManager.m'),
    join(root, 'ios/Classes/OKNFCBridge.mm'),
    join(root, 'ios/Classes/Utils/LCLogger.m'),
    join(root, 'ios/Classes/Utils/NSString+OKNFCHexStr.m'),
    join(root, 'ios/Classes/Utils/NSData+OKNFCHexData.m'),
    join(__dirname, 'objc/ManagerInputTest.m'),
    '-L', sdkBuild, '-lGPChannelSDKCore', '-Wl,-rpath,' + sdkBuild,
    '-o', join(output, 'input-tests')]);
  run(join(output, 'input-tests'), process.env.ONEKEY_LITE_IOS_LOG_TEST_OUTPUT
    ? [process.env.ONEKEY_LITE_IOS_LOG_TEST_OUTPUT] : []);
} finally {
  rmSync(output, { recursive: true, force: true });
}
