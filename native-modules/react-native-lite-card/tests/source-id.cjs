const assert = require('node:assert/strict');
const { spawnSync } = require('node:child_process');
const { mkdtempSync, mkdirSync, readFileSync, writeFileSync, appendFileSync, cpSync, rmSync } = require('node:fs');
const { tmpdir } = require('node:os');
const { join, resolve, dirname } = require('node:path');

const root = resolve(__dirname, '..');
const output = mkdtempSync(join(tmpdir(), 'lite-source-id-'));
let assertions = 0;
function expect(value, scenario) { assert.ok(value, scenario); assertions++; }
function run(command, args, env = {}) {
  const result = spawnSync(command, args, { encoding: 'utf8', env: { ...process.env, ...env } });
  if (result.error) throw result.error;
  if (result.status !== 0) throw new Error(`${command} exited ${result.status}\n${result.stderr}`);
  return result.stdout.trim();
}
function copySource(destination) {
  const entries = readFileSync(join(root, 'sdk/source-id-manifest.txt'), 'utf8').trim().split('\n');
  for (const entry of [...entries, 'package.json']) {
    const target = join(destination, entry);
    mkdirSync(dirname(target), { recursive: true });
    cpSync(join(root, entry), target, { recursive: true });
  }
}
function header(directory) { return readFileSync(join(directory, 'GPChannelSourceId.h'), 'utf8'); }
function rubyHeader(source, destination) {
  run('ruby', [join(source, 'sdk/scripts/generate-source-id.rb'), join(destination, 'GPChannelSourceId.h')]);
  return header(destination);
}
function binaryId(library) {
  return run('python3', ['-c', 'import ctypes,sys; print((ctypes.c_char*75).in_dll(ctypes.CDLL(sys.argv[1]), "GPChannelSDKCoreVersionString").value.decode())', library]);
}
try {
  run('ninja', ['--version']);
  const first = join(output, 'first');
  const second = join(output, 'different-location');
  copySource(first);
  copySource(second);
  const ruby = join(output, 'ruby');
  const baseline = rubyHeader(first, ruby);
  expect(/^#define GPCHANNEL_NATIVE_SOURCE_ID "source-v1:[a-f0-9]{64}"\n$/.test(baseline), 'Identity has a fixed source format');
  expect(rubyHeader(second, join(output, 'other-ruby')) === baseline, 'Absolute source location does not affect identity');
  for (const relative of ['sdk/src/private.log', 'sdk/src/.DS_Store', 'sdk/src/.cache/private.c', 'sdk/src/build/private.c', 'keys/private.c']) {
    mkdirSync(dirname(join(first, relative)), { recursive: true });
    writeFileSync(join(first, relative), 'PRIVATE-USER-DATA-PIN-SEED\n/path/user@host');
  }
  expect(rubyHeader(first, ruby) === baseline, 'Logs, hidden/cache/build files and keys are excluded');
  const build = join(output, 'cmake');
  // GNU Make 3.81 can miss same-second archive updates during this rapid rebuild.
  run('cmake', ['-G', 'Ninja', '-S', join(first, 'sdk'), '-B', build, '-DBUILD_TESTING=OFF']);
  expect(header(join(build, 'generated')) === baseline, 'CMake and CocoaPods generator use identical content rules');
  run('cmake', ['--build', build, '--target', 'GPChannelSDKCore', '--parallel', '4']);
  const library = join(build, process.platform === 'darwin' ? 'libGPChannelSDKCore.dylib' : 'libGPChannelSDKCore.so');
  expect(baseline.includes(`"${binaryId(library)}"`), 'Native core contains its generated identity');

  // Execute the production podspec DSL with only CocoaPods/RN declaration adapters stubbed.
  const podAdapter = `require 'json'
module Pod
  class Spec
    attr_accessor :script_phase
    def self.new
      spec = allocate
      yield spec
      STDOUT.write(JSON.generate(spec.script_phase))
      spec
    end
    def method_missing(*)
    end
  end
end
def min_ios_version_supported; '15.0'; end
def install_modules_dependencies(*); end
load ARGV.fetch(0)`;
  const phase = JSON.parse(run('ruby', ['-e', podAdapter, join(first, 'ReactNativeLiteCard.podspec')]));
  expect(phase.execution_position === 'before_compile', 'Native identity phase runs before compilation');
  expect(phase.show_env_vars_in_log === '0', 'Source identity phase must not dump build environment values');
  const derived = join(output, 'pod-derived');
  const phaseEnv = { PODS_TARGET_SRCROOT: first, DERIVED_FILE_DIR: derived };
  run('sh', ['-c', phase.script], phaseEnv);
  expect(header(derived) === baseline, 'Actual pod phase generates the compiler header');
  const podLibrary = join(output, 'pod-version.dylib');
  run('xcrun', ['clang', '-dynamiclib', '-I', derived, join(first, 'sdk/src/version.c'), '-o', podLibrary]);
  expect(binaryId(podLibrary) === binaryId(library), 'Pod phase header compiles into the existing native version symbol');

  appendFileSync(join(first, 'sdk/src/version.c'), '\n// Synthetic source identity regression.\n');
  run('cmake', ['--build', build, '--target', 'GPChannelSDKCore', '--parallel', '4']);
  const changed = header(join(build, 'generated'));
  expect(changed !== baseline, 'Incremental native build reconfigures after source content changes');
  expect(changed.includes(`"${binaryId(library)}"`), 'Incremental build updates the compiled native identity');
  run('sh', ['-c', phase.script], phaseEnv);
  expect(header(derived) === changed && rubyHeader(first, ruby) === changed, 'Subsequent pod builds regenerate changed content identity');
  const envHeader = join(output, 'environment');
  run('ruby', [join(first, 'sdk/scripts/generate-source-id.rb'), join(envHeader, 'GPChannelSourceId.h')], { PRIVATE_PIN: 'PRIVATE-PIN', HOSTNAME: 'PRIVATE-HOST' });
  expect(header(envHeader) === changed, 'Environment and host values do not affect identity');
  console.log(`SourceIdentity: ${assertions} assertions passed`);
} finally {
  rmSync(output, { recursive: true, force: true });
}
