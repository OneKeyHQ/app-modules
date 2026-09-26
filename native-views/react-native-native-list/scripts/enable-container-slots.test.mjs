import assert from 'node:assert/strict';
import { execFileSync, spawnSync } from 'node:child_process';
import { mkdtempSync, mkdirSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';
import { test } from 'node:test';
import { fileURLToPath } from 'node:url';

const script = readFileSync(join(dirname(fileURLToPath(import.meta.url)), 'enable-container-slots.mjs'), 'utf8');
const androidPath = 'nitrogen/generated/android/kotlin/com/margelo/nitro/nativelist/views/HybridNativeListManager.kt';
const iosPath = 'nitrogen/generated/ios/c++/views/HybridNativeListComponent.mm';

function fixture(android, ios) {
  const root = mkdtempSync(join(tmpdir(), 'native-list-slots-'));
  for (const [relative, content] of [['scripts/enable-container-slots.mjs', script], [androidPath, android], [iosPath, ios]]) {
    const path = join(root, relative);
    mkdirSync(dirname(path), { recursive: true });
    writeFileSync(path, content);
  }
  return root;
}

test('post-nitrogen child ownership transformation is idempotent', () => {
  const root = fixture(`import android.view.View
import com.facebook.react.uimanager.SimpleViewManager
public class HybridNativeListManager: SimpleViewManager<View>() {
  override fun getName(): String { return "NativeList" }
  override fun createViewInstance(reactContext: ThemedReactContext): View {
    val view = hybridView.view
    return view
  }
}`, `@implementation HybridNativeListComponent {
}
- (void) updateView {
}`);
  try {
    const path = join(root, 'scripts/enable-container-slots.mjs');
    execFileSync(process.execPath, [path]);
    const firstAndroid = readFileSync(join(root, androidPath), 'utf8');
    const firstIOS = readFileSync(join(root, iosPath), 'utf8');
    assert.match(firstAndroid, /ViewGroupManager<ViewGroup>/);
    assert.match(firstAndroid, /unmountContainerSlot\(index\)/);
    assert.match(firstIOS, /mountChildComponentView/);
    assert.match(firstIOS, /unmountContainerSlot:child/);
    execFileSync(process.execPath, [path]);
    assert.equal(readFileSync(join(root, androidPath), 'utf8'), firstAndroid);
    assert.equal(readFileSync(join(root, iosPath), 'utf8'), firstIOS);
  } finally { rmSync(root, { recursive: true, force: true }); }
});

test('unexpected nitrogen output fails instead of silently losing child mounting', () => {
  const root = fixture('unrecognized generator output', '- (void) updateView {');
  try {
    const result = spawnSync(process.execPath, [join(root, 'scripts/enable-container-slots.mjs')]);
    assert.notEqual(result.status, 0);
    assert.match(result.stderr.toString(), /Unexpected Nitro Android manager shape/);
  } finally { rmSync(root, { recursive: true, force: true }); }
});
