import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import crypto from 'node:crypto';
import fs from 'node:fs';
import os from 'node:os';
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
  assert.equal(names.length, 63);
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
  const nativeListWebBuiltins = [
    'ArrowBottomOutline',
    'ArrowTopOutline',
    'CoinsOutline',
    'Copy3Outline',
    'CryptoCoinOutline',
    'Document2Outline',
    'DotHorOutline',
    'ImageSquareWavesOutline',
    'ShieldCheckDoneOutline',
    'SwitchHorOutline',
    'UnlockedOutline',
  ];
  for (const name of nativeListWebBuiltins) assert.ok(manifest[name], name);
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

  if (process.platform === 'darwin') {
    const tempDir = fs.mkdtempSync(
      path.join(os.tmpdir(), 'bottom-tabs-icon-ordering-')
    );
    try {
      const sdkPath = execFileSync(
        'xcrun',
        ['--sdk', 'macosx', '--show-sdk-path'],
        { encoding: 'utf8' }
      ).trim();
      const harnessPath = path.join(tempDir, 'test_harness.swift');
      const binPath = path.join(tempDir, 'bottom-tabs-ordering');
      const bottomTabsIosDir = path.resolve(
        packageRoot,
        '../react-native-bottom-tabs/ios'
      );
      fs.writeFileSync(
        harnessPath,
        `import Foundation
import UIKit

public typealias RCTDirectEventBlock = ([AnyHashable: Any]?) -> Void

@objc public enum RCTResizeMode: Int {
  case cover = 1
  case contain = 2
}

@objc public protocol RCTImageLoaderProtocol: NSObjectProtocol {
  @discardableResult
  func loadImage(
    with imageURL: URLRequest?,
    size: CGSize,
    scale: CGFloat,
    clipped: Bool,
    resizeMode: RCTResizeMode,
    progressBlock: @escaping (Int64, Int64) -> Void,
    partialLoad: @escaping (UIImage?) -> Void,
    completionBlock: @escaping (Error?, UIImage?) -> Void
  ) -> (() -> Void)?
}

public enum RCTFont {
  public static func update(
    _ font: UIFont?,
    withFamily family: String?,
    size: NSNumber?,
    weight: String?,
    style: String?,
    variant: [Any]?,
    scaleMultiplier: CGFloat
  ) -> UIFont {
    UIFont.systemFont(ofSize: CGFloat(size?.doubleValue ?? 12))
  }
}

extension UIView {
  public func reactViewController() -> UIViewController? { nil }
}

@objc(OneKeyNamedSVGBridge)
final class OneKeyNamedSVGBridge: NSObject {
  static var pending: [(owner: AnyObject, name: String, complete: (UIImage?) -> Void)] = []
  static var cancelledOwners: [ObjectIdentifier] = []

  @objc(cancelWithOwner:)
  static func cancel(owner: AnyObject) {
    cancelledOwners.append(ObjectIdentifier(owner))
    pending.removeAll { $0.owner === owner }
  }

  @objc(requestWithOwner:name:color:width:height:scale:complete:)
  static func request(
    owner: AnyObject,
    name: String,
    color: String,
    width: Int,
    height: Int,
    scale: Double,
    complete: @escaping (UIImage?) -> Void
  ) {
    pending.append((owner: owner, name: name, complete: complete))
  }
}

final class ControlledImageLoader: NSObject, RCTImageLoaderProtocol {
  var pending: [(url: String, complete: (Error?, UIImage?) -> Void)] = []

  func loadImage(
    with imageURL: URLRequest?,
    size: CGSize,
    scale: CGFloat,
    clipped: Bool,
    resizeMode: RCTResizeMode,
    progressBlock: @escaping (Int64, Int64) -> Void,
    partialLoad: @escaping (UIImage?) -> Void,
    completionBlock: @escaping (Error?, UIImage?) -> Void
  ) -> (() -> Void)? {
    pending.append((url: imageURL?.url?.absoluteString ?? "", complete: completionBlock))
    return nil
  }
}

func solidImage(_ color: UIColor, width: CGFloat) -> UIImage {
  let renderer = UIGraphicsImageRenderer(size: CGSize(width: width, height: 27))
  return renderer.image { ctx in
    color.setFill()
    ctx.fill(CGRect(x: 0, y: 0, width: width, height: 27))
  }
}

func inspectLoadedMap(_ view: RCTBottomTabsContainerView, label: String) -> [Int: UIImage] {
  for child in Mirror(reflecting: view).children where child.label == label {
    if let dict = child.value as? [Int: UIImage] {
      return dict
    }
  }
  fatalError("Missing field \\(label)")
}

@main
struct TestRunner {
  static func main() {
    let view = RCTBottomTabsContainerView(frame: CGRect(x: 0, y: 0, width: 320, height: 80))
    let loader = ControlledImageLoader()
    view.setImageLoader(loader)

    // Case 1: Ordinary URL starts first, switches to named icon, named finishes first, stale ordinary finishes second.
    view.icons = [["uri": "https://example.com/old.png", "width": 27.0, "height": 27.0, "scale": 1.0]]
    view.selectedIcons = [["uri": "https://example.com/old-selected.png", "width": 27.0, "height": 27.0, "scale": 1.0]]
    precondition(loader.pending.count == 2)

    view.icons = [["uri": "onekey-icon://Wallet4Outline", "width": 27.0, "height": 27.0, "scale": 1.0]]
    view.selectedIcons = [["uri": "onekey-icon://Wallet4Solid", "width": 27.0, "height": 27.0, "scale": 1.0]]
    precondition(OneKeyNamedSVGBridge.pending.count == 2)

    OneKeyNamedSVGBridge.pending[0].complete(solidImage(.blue, width: 41))
    OneKeyNamedSVGBridge.pending[1].complete(solidImage(.green, width: 43))
    OneKeyNamedSVGBridge.pending.removeAll()

    precondition(inspectLoadedMap(view, label: "loadedIcons")[0]?.size.width == 41)
    precondition(inspectLoadedMap(view, label: "loadedSelectedIcons")[0]?.size.width == 43)

    let staleOrdinary = solidImage(.red, width: 27)
    for item in loader.pending {
      item.complete(nil, staleOrdinary)
    }
    loader.pending.removeAll()
    RunLoop.main.run(until: Date().addingTimeInterval(0.05))

    precondition(inspectLoadedMap(view, label: "loadedIcons")[0]?.size.width == 41)
    precondition(inspectLoadedMap(view, label: "loadedSelectedIcons")[0]?.size.width == 43)

    // Case 2: Named icon starts first, switches to ordinary URL, ordinary finishes first, stale named callback fires second.
    view.icons = [["uri": "onekey-icon://CoinsOutline", "width": 27.0, "height": 27.0, "scale": 1.0]]
    view.selectedIcons = [["uri": "onekey-icon://CoinsSolid", "width": 27.0, "height": 27.0, "scale": 1.0]]
    precondition(OneKeyNamedSVGBridge.pending.count == 2)
    let staleNamedCallbacks = OneKeyNamedSVGBridge.pending

    view.icons = [["uri": "https://example.com/new.png", "width": 27.0, "height": 27.0, "scale": 1.0]]
    view.selectedIcons = [["uri": "https://example.com/new-selected.png", "width": 27.0, "height": 27.0, "scale": 1.0]]
    precondition(loader.pending.count == 2)

    for item in loader.pending {
      item.complete(nil, solidImage(.yellow, width: 27))
    }
    loader.pending.removeAll()
    RunLoop.main.run(until: Date().addingTimeInterval(0.05))
    precondition(inspectLoadedMap(view, label: "loadedIcons")[0]?.size.width == 27)
    precondition(inspectLoadedMap(view, label: "loadedSelectedIcons")[0]?.size.width == 27)

    staleNamedCallbacks[0].complete(solidImage(.purple, width: 51))
    staleNamedCallbacks[1].complete(solidImage(.orange, width: 53))
    precondition(inspectLoadedMap(view, label: "loadedIcons")[0]?.size.width == 27)
    precondition(inspectLoadedMap(view, label: "loadedSelectedIcons")[0]?.size.width == 27)

    print("RCTBottomTabsContainerViewOrdering: all assertions passed")
  }
}
`
      );
      execFileSync('xcrun', [
        '--sdk',
        'macosx',
        'swiftc',
        '-target',
        'arm64-apple-ios16.4-macabi',
        '-I',
        `${sdkPath}/System/iOSSupport/usr/lib/swift`,
        '-F',
        `${sdkPath}/System/iOSSupport/System/Library/Frameworks`,
        '-module-cache-path',
        path.join(tempDir, 'module-cache'),
        harnessPath,
        path.join(bottomTabsIosDir, 'Extensions.swift'),
        path.join(bottomTabsIosDir, 'TabBarFontSize.swift'),
        path.join(bottomTabsIosDir, 'RCTBottomAccessoryContainerView.swift'),
        path.join(bottomTabsIosDir, 'RCTBottomTabsContainerView.swift'),
        '-o',
        binPath,
      ]);
      const stdout = execFileSync(binPath, [], { encoding: 'utf8' });
      assert.match(
        stdout,
        /RCTBottomTabsContainerViewOrdering: all assertions passed/
      );
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  }
});
