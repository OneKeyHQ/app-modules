import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import test from 'node:test';
import { fileURLToPath } from 'node:url';

const packageRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '..'
);
const fixtureRoot = path.resolve(
  packageRoot,
  '../../example/react-native/icon-fixtures'
);
const nativeRoot = path.join(fixtureRoot, 'native/icons');
const webRoot = path.join(fixtureRoot, 'web/assets');
const manifest = JSON.parse(
  fs.readFileSync(path.join(nativeRoot, 'manifest.json'), 'utf8')
);
const names = Object.keys(manifest).sort();

test('SDK fixtures have paired raw and external symbol resources with verified identities', () => {
  assert.equal(names.length, 58);
  for (const root of [nativeRoot, webRoot])
    assert.deepEqual(
      fs
        .readdirSync(root)
        .filter((name) => name.endsWith('.svg'))
        .sort(),
      names.map((name) => name + '.svg')
    );
  const urls = fs.readFileSync(
    path.join(fixtureRoot, 'web/assetUrls.ts'),
    'utf8'
  );
  assert.deepEqual(
    [...urls.matchAll(/^  ([A-Z][A-Za-z0-9]+):/gm)].map((match) => match[1]),
    names
  );
  const modes = new Set();
  for (const name of names) {
    const entry = manifest[name];
    assert.match(name, /^[A-Z][A-Za-z0-9]+$/);
    assert.match(
      entry.source,
      /^(?:brand|colored|custom|illus|outline|solid)\/[a-z0-9-]+\.svg$/
    );
    assert.equal(entry.viewBox.length, 4);
    assert.ok(entry.viewBox.every(Number.isFinite));
    assert.ok(entry.viewBox[2] > 0 && entry.viewBox[3] > 0);
    assert.ok(
      ['fixed', 'single', 'multiple', 'raster'].includes(entry.recolorMode)
    );
    modes.add(entry.recolorMode);
    const raw = fs.readFileSync(path.join(nativeRoot, name + '.svg'), 'utf8');
    assert.equal(
      crypto.createHash('sha256').update(raw).digest('hex'),
      entry.digest
    );
    const symbol = fs.readFileSync(path.join(webRoot, name + '.svg'), 'utf8');
    assert.ok(symbol.includes(`id="onekey-${name}"`));
    for (const match of symbol.matchAll(/(?:id="|url\(#)([^"\)]+)/g))
      assert.ok(match[1].startsWith(`onekey-${name}`));
  }
  assert.deepEqual([...modes].sort(), [
    'fixed',
    'multiple',
    'raster',
    'single',
  ]);
  assert.notEqual(
    manifest.BadgeVerifiedSolid.digest,
    manifest.BadgeRecognizedSolid.digest
  );
  assert.ok(manifest.EditOutline);
});

test('production resource inputs contain no named SVG or example fixture dependency', () => {
  assert.equal(fs.existsSync(path.join(packageRoot, 'resources/icons')), false);
  assert.equal(fs.existsSync(path.join(packageRoot, 'src/icon/assets')), false);
  const pod = fs.readFileSync(
    path.join(packageRoot, 'OneKeyImage.podspec'),
    'utf8'
  );
  assert.doesNotMatch(pod, /s\.resource_bundles|s\.resources\s*=/);
  const gradle = fs.readFileSync(
    path.join(packageRoot, 'android/build.gradle'),
    'utf8'
  );
  assert.doesNotMatch(gradle, /assets\.srcDirs|icon-fixtures/);
  const stub = fs.readFileSync(
    path.join(packageRoot, 'src/icon/assetUrls.ts'),
    'utf8'
  );
  assert.doesNotMatch(stub, /new URL|\.svg/);
});

test('cross-module native consumers share OneKeyNamedSVG metadata and onekey-icon:// scheme', () => {
  const iosSvg = fs.readFileSync(
    path.join(packageRoot, 'ios/OneKeyNamedSVG.swift'),
    'utf8'
  );
  assert.match(iosSvg, /public func iconMetadata\(forName name: String\)/);
  assert.match(iosSvg, /@objc\(OneKeyNamedSVGBridge\)/);

  const androidSvg = fs.readFileSync(
    path.join(
      packageRoot,
      'android/src/main/java/com/margelo/nitro/onekeyimage/OneKeyNamedSVG.kt'
    ),
    'utf8'
  );
  assert.match(
    androidSvg,
    /fun iconMetadata\(assets: AssetManager, name: String\)/
  );

  const nativeListIos = fs.readFileSync(
    path.resolve(
      packageRoot,
      '../react-native-native-list/ios/NativeListDesignAssets.swift'
    ),
    'utf8'
  );
  assert.match(
    nativeListIos,
    /OneKeyNamedSVG\.shared\.iconMetadata\(forName: name\)/
  );

  const nativeListAndroid = fs.readFileSync(
    path.resolve(
      packageRoot,
      '../react-native-native-list/android/src/main/java/com/onekey/nativelist/NativeListPrimitives.kt'
    ),
    'utf8'
  );
  assert.match(
    nativeListAndroid,
    /OneKeyNamedSVG\.iconMetadata\(context\.applicationContext\.assets, iconName\)/
  );

  const bottomTabsJs = fs.readFileSync(
    path.resolve(
      packageRoot,
      '../react-native-bottom-tabs/lib/module/BottomTabs.js'
    ),
    'utf8'
  );
  assert.match(bottomTabsJs, /onekey-icon:\/\//);

  const bottomTabsIos = fs.readFileSync(
    path.resolve(
      packageRoot,
      '../react-native-bottom-tabs/ios/RCTBottomTabsContainerView.swift'
    ),
    'utf8'
  );
  assert.match(bottomTabsIos, /onekey-icon:\/\//);
  assert.match(bottomTabsIos, /OneKeyNamedSVGLoader\.request/);

  const bottomTabsAndroid = fs.readFileSync(
    path.resolve(
      packageRoot,
      '../react-native-bottom-tabs/android/src/main/java/com/onekey/bottomtabs/BottomTabsView.kt'
    ),
    'utf8'
  );
  assert.match(bottomTabsAndroid, /onekey-icon:\/\//);
  assert.match(bottomTabsAndroid, /OneKeyNamedSVG\.request/);
});
