import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import { spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import test from 'node:test';
import { generateOutputs, resolveRecords } from './generate-icons.mjs';
const scripts = path.dirname(fileURLToPath(import.meta.url));
const packageRoot = path.dirname(scripts);
const fixtureRoot = path.resolve(
  packageRoot,
  '../../example/react-native/icon-fixtures/native/icons'
);
const manifest = JSON.parse(
  fs.readFileSync(path.join(scripts, 'native-list-icons.json'), 'utf8')
);
const catalogFile = path.join(fixtureRoot, 'manifest.json');
const catalog = JSON.parse(fs.readFileSync(catalogFile, 'utf8'));
// Frozen migration coverage, independent from the production manifest.
const migrationCoverage = {
  ios: [
    'AccountErrorCustom',
    'AllNetworksSolid',
    'AppleBrand',
    'ArrowBottomOutline',
    'ArrowTopOutline',
    'BadgeVerifiedSolid',
    'BotIllus',
    'ChartTrendingUpOutline',
    'CheckboxCheckedCustom',
    'CheckboxIndeterminateCustom',
    'ChevronBottomOutline',
    'ChevronGrabberVerOutline',
    'ChevronRightSmallOutline',
    'ChevronTopOutline',
    'CrossedSmallSolid',
    'CryptoCoinOutline',
    'Document2Outline',
    'DotHorOutline',
    'DotVerSolid',
    'DragOutline',
    'ErrorSolid',
    'GasSolid',
    'GlobusOutline',
    'GoogleIllus',
    'ImageSquareWavesOutline',
    'InfoCircleOutline',
    'LockSolid',
    'MinusCircleOutline',
    'MinusCircleSolid',
    'PencilOutline',
    'PlusCircleOutline',
    'PlusLargeOutline',
    'PlusSmallOutline',
    'QuestionmarkSolid',
    'RefreshCcwOutline',
    'ShieldExclamationOutline',
    'SpeakerPromoteOutline',
    'StarOutline',
    'StarSolid',
    'SwapHorOutline',
    'TicketOutline',
    'WalletOutline',
  ],
  android: [
    'AccountErrorCustom',
    'AllNetworksSolid',
    'AppleBrand',
    'ArrowBottomOutline',
    'ArrowTopOutline',
    'BadgeVerifiedSolid',
    'BotIllus',
    'ChartTrendingUpOutline',
    'ChevronBottomOutline',
    'ChevronGrabberVerOutline',
    'ChevronRightSmallOutline',
    'ChevronTopOutline',
    'Circle',
    'CrossedSmallSolid',
    'CryptoCoinOutline',
    'Document2Outline',
    'DotHorOutline',
    'DotVerSolid',
    'DragOutline',
    'ErrorSolid',
    'GasSolid',
    'GlobusOutline',
    'GoogleIllus',
    'ImageSquareWavesOutline',
    'InfoCircleOutline',
    'LockSolid',
    'MinusCircleOutline',
    'MinusCircleSolid',
    'PencilOutline',
    'PlusCircleOutline',
    'PlusLargeOutline',
    'PlusSmallOutline',
    'QuestionmarkSolid',
    'RefreshCcwOutline',
    'ShieldExclamationOutline',
    'SpeakerPromoteOutline',
    'StarOutline',
    'StarSolid',
    'SwapHorOutline',
    'TicketOutline',
    'WalletOutline',
  ],
  web: [
    'AccountErrorCustom',
    'AllNetworksSolid',
    'AppleBrand',
    'BadgeVerifiedSolid',
    'BotIllus',
    'CheckboxCheckedCustom',
    'CheckboxIndeterminateCustom',
    'ChevronRightSmallOutline',
    'Circle',
    'CrossedSmallSolid',
    'CryptoCoinOutline',
    'Document2Outline',
    'DotHorOutline',
    'DotVerSolid',
    'DragOutline',
    'GasSolid',
    'GlobusOutline',
    'GoogleIllus',
    'LockSolid',
    'PencilOutline',
    'PlusLargeOutline',
    'PlusSmallOutline',
    'RefreshCcwOutline',
    'TicketOutline',
    'WalletOutline',
  ],
};
function assertRegistryCoverage(entries) {
  for (const platform of Object.keys(migrationCoverage))
    assert.deepEqual(
      entries
        .filter((entry) => entry.platforms.includes(platform))
        .map((entry) => entry.name)
        .sort(),
      migrationCoverage[platform]
    );
}

test('preserves frozen coverage, canonical aliases and intrinsic metadata without path copies', () => {
  assertRegistryCoverage(manifest);
  assert.throws(() =>
    assertRegistryCoverage(
      manifest.map((e) =>
        e.name === 'DotVerSolid' ? { ...e, name: 'RenamedDotVerSolid' } : e
      )
    )
  );
  const records = resolveRecords(manifest, catalog, fixtureRoot);
  assert.equal(
    records.find((e) => e.name === 'BadgeVerifiedSolid').canonical,
    'BadgeRecognizedSolid'
  );
  for (const [name, w, h] of [
    ['AppleBrand', 16, 20],
    ['CheckboxCheckedCustom', 16, 16],
    ['AccountErrorCustom', 18, 18],
  ]) {
    const record = records.find((e) => e.name === name);
    assert.deepEqual([record.width, record.height], [w, h]);
  }
  const outputs = generateOutputs(fixtureRoot);
  assert.equal(outputs.size, 3);
  assert.deepEqual([...outputs], [...generateOutputs(fixtureRoot)]);
  const contents = [...outputs.values()];
  assert.ok(
    contents.every(
      (text) => !text.includes(fixtureRoot) && !text.includes('<path')
    )
  );
  const keys = [
    [...contents[0].matchAll(/^    "([^"]+)":/gm)].map((m) => m[1]),
    [...contents[1].matchAll(/^  "([^"]+)" to /gm)].map((m) => m[1]),
    [...contents[2].matchAll(/^  ([A-Za-z0-9]+):/gm)].map((m) => m[1]),
  ];
  assert.deepEqual(keys, [
    migrationCoverage.ios,
    migrationCoverage.android,
    migrationCoverage.web,
  ]);
});
test('rejects invalid coverage metadata and missing, ambiguous or stale canonical sources', () => {
  for (const invalid of [
    { ...manifest[0], name: undefined },
    { ...manifest[0], platforms: ['other'] },
    { ...manifest[0], platforms: ['ios', 'ios'] },
  ])
    assert.throws(
      () => resolveRecords([invalid], catalog, fixtureRoot),
      /Invalid icon manifest/
    );
  assert.throws(
    () => resolveRecords([manifest[0], manifest[0]], catalog, fixtureRoot),
    /Invalid icon manifest/
  );
  assert.throws(
    () => resolveRecords(manifest, {}, fixtureRoot),
    /Missing shared SVG/
  );
  assert.throws(
    () =>
      resolveRecords(
        manifest,
        {
          ...catalog,
          Duplicate:
            catalog[
              Object.keys(catalog).find(
                (k) => catalog[k].source === manifest[0].source
              )
            ],
        },
        fixtureRoot
      ),
    /Ambiguous shared SVG/
  );
  const changed = structuredClone(catalog);
  changed[
    Object.keys(changed).find((k) => changed[k].source === manifest[0].source)
  ].digest = '0';
  assert.throws(() => resolveRecords(manifest, changed, fixtureRoot), /stale/);
});
test('generation is idempotent, check is read-only and invalid late input cannot partially write', () => {
  const tempRoot = path.join(packageRoot, '.tmp');
  fs.mkdirSync(tempRoot, { recursive: true });
  const temp = fs.mkdtempSync(path.join(tempRoot, 'metadata-'));
  try {
    const sdk = path.join(temp, 'native-views/react-native-native-list');
    fs.mkdirSync(path.join(sdk, 'scripts'), { recursive: true });
    for (const name of ['generate-icons.mjs', 'native-list-icons.json'])
      fs.copyFileSync(
        path.join(scripts, name),
        path.join(sdk, 'scripts', name)
      );
    fs.copyFileSync(
      path.join(packageRoot, 'package.json'),
      path.join(sdk, 'package.json')
    );
    fs.symlinkSync(
      path.resolve(packageRoot, '../../node_modules'),
      path.join(temp, 'node_modules'),
      'dir'
    );
    const targetCatalog = path.join(
      temp,
      'example/react-native/icon-fixtures/native/icons/manifest.json'
    );
    fs.mkdirSync(path.dirname(targetCatalog), { recursive: true });
    fs.cpSync(fixtureRoot, path.dirname(targetCatalog), { recursive: true });
    const run = (check = false) =>
      spawnSync(
        process.execPath,
        [
          path.join(sdk, 'scripts/generate-icons.mjs'),
          ...(check ? ['--check'] : []),
        ],
        { encoding: 'utf8' }
      );
    assert.equal(run().status, 0);
    assert.equal(JSON.parse(run().stdout).changed, 0);
    assert.equal(run(true).status, 0);
    const files = [...generateOutputs(fixtureRoot, sdk).keys()];
    const snapshot = () => files.map((file) => fs.readFileSync(file, 'utf8'));
    fs.appendFileSync(files[0], '// stale\n');
    const stale = snapshot();
    assert.notEqual(run(true).status, 0);
    assert.deepEqual(snapshot(), stale);
    assert.equal(run().status, 0);
    const before = snapshot();
    const changed = structuredClone(catalog);
    changed[
      Object.keys(changed).find((k) => changed[k].source === manifest[0].source)
    ].viewBox = [0, 0, 12, 12];
    delete changed.DotVerSolid;
    fs.writeFileSync(targetCatalog, JSON.stringify(changed));
    assert.notEqual(run().status, 0);
    assert.deepEqual(snapshot(), before);
  } finally {
    fs.rmSync(temp, { recursive: true, force: true });
  }
});
