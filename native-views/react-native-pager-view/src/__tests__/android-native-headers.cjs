const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { test } = require('node:test');

const packageRoot = path.resolve(__dirname, '../..');
const read = (relativePath) =>
  fs.readFileSync(path.join(packageRoot, relativePath), 'utf8');

test('keeps the public native header path opt-in on Android', () => {
  const source = read('src/CollapsiblePagerView.tsx');
  assert.match(source, /Platform\.OS === "android"/);
  assert.match(source, /!!nativeTabBar\?\.items\.length/);
  assert.match(source, /!!nativeSubHeader\?\.items\.length/);
});

test('connects Android props, direct press events, gesture bridge, and file logger', () => {
  const manager = read(
    'android/src/main/java/com/reactnativepagerview/CollapsiblePagerViewManager.kt'
  );
  const host = read(
    'android/src/main/java/com/reactnativepagerview/CollapsiblePagerHost.kt'
  );
  const gradle = read('android/build.gradle');
  const manifest = JSON.parse(read('package.json'));

  assert.match(manager, /updateNativeTabBarItems\(value\)/);
  assert.match(manager, /updateNativeSubHeader\(value\)/);
  assert.match(manager, /NativeHeaderPressEvent\.TAB_EVENT_NAME/);
  assert.match(manager, /host\.updateNativeTabProgress\(position, offset\)/);
  assert.match(host, /beginForwardingToRecycler\(event\)/);
  assert.match(host, /NativeGestureUtil\.notifyNativeGestureStarted/);
  assert.match(host, /OneKeyLog\.debug\("CollapsiblePager"/);
  assert.match(gradle, /project\(":onekeyfe_react-native-native-logger"\)/);
  assert.equal(
    manifest.peerDependencies['@onekeyfe/react-native-native-logger'],
    manifest.version
  );
});
