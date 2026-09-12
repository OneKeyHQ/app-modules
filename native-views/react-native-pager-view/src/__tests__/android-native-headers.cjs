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

test('native tab presses navigate by default and release interrupted targets at idle', () => {
  const source = read('src/CollapsiblePagerView.tsx');
  assert.match(
    source,
    /private onNativeTabPress[\s\S]*?this\.dispatchNativeTabPageCommand\(position, true\);[\s\S]*?this\.props\.onNativeTabPress\?\.\(event\);/
  );
  assert.match(
    source,
    /private onPageSelected[\s\S]*?this\.latestNativeSelectedPage = position;/
  );
  assert.match(
    source,
    /scrollState === "idle"[\s\S]*?this\.latestNativeTabTarget !== this\.latestNativeSelectedPage[\s\S]*?!queuedDuringPreviousScroll[\s\S]*?this\.latestNativeTabTarget = null;[\s\S]*?transientRetainedPages: \[\]/
  );
  assert.match(
    source,
    /this\.nativeTabCommandQueuedDuringScroll =\s+this\.nativePagerScrollState !== "idle";/
  );
  assert.match(
    source,
    /onPageScrollStateChanged=\{this\.onPageScrollStateChanged\}/
  );
});

test('iOS standalone sub-headers use no hidden tab space and pager pans reject vertical intent', () => {
  const source = read('ios/RNCCollapsiblePagerViewComponentView.mm');
  assert.match(
    source,
    /CGFloat nativeTabBarVisibleHeight = _nativeTabBarView\.hidden\s+\? 0\s+: MIN\(_stickyHeaderHeight, _nativeTabBarHeight\);/
  );
  assert.match(
    source,
    /_nativeSubHeaderView\.frame = CGRectMake\(\s+0,\s+stickyY \+ nativeTabBarVisibleHeight/
  );
  assert.match(
    source,
    /gestureRecognizer == self\.panGestureRecognizer[\s\S]*?hasMotion && fabs\(intent\.y\) > fabs\(intent\.x\)[\s\S]*?return NO;/
  );
});

test('registers every native header prop with the Paper renderer', () => {
  const manager = read(
    'android/src/main/java/com/reactnativepagerview/CollapsiblePagerViewManager.kt'
  );
  const setters = {
    nativeSmoothHeaderScrollEnabled: 'setNativeSmoothHeaderScrollEnabled',
    nativeTabBarItems: 'setNativeTabBarItems',
    nativeTabBarHeight: 'setNativeTabBarHeight',
    nativeTabBarContentPaddingHorizontal:
      'setNativeTabBarContentPaddingHorizontal',
    nativeTabBarItemSpacing: 'setNativeTabBarItemSpacing',
    nativeTabBarFontSize: 'setNativeTabBarFontSize',
    nativeTabBarFontFamily: 'setNativeTabBarFontFamily',
    nativeTabBarBackgroundColor: 'setNativeTabBarBackgroundColor',
    nativeTabBarActiveTextColor: 'setNativeTabBarActiveTextColor',
    nativeTabBarInactiveTextColor: 'setNativeTabBarInactiveTextColor',
    nativeTabBarIndicatorColor: 'setNativeTabBarIndicatorColor',
    nativeTabBarIndicatorHeight: 'setNativeTabBarIndicatorHeight',
    nativeTabBarIndicatorBottom: 'setNativeTabBarIndicatorBottom',
    nativeSubHeaderConfig: 'setNativeSubHeaderConfig',
    nativeSubHeaderSelectedBackgroundColor:
      'setNativeSubHeaderSelectedBackgroundColor',
  };

  for (const [prop, setter] of Object.entries(setters)) {
    assert.match(
      manager,
      new RegExp(
        `@ReactProp\\(name = "${prop}"(?:, [^)]*)?\\)\\s+override fun ${setter}\\(`
      )
    );
  }
  assert.match(
    manager,
    /@ReactProp\(name = "nativeSmoothHeaderScrollEnabled", defaultBoolean = false\)/
  );
  for (const prop of [
    'nativeTabBarBackgroundColor',
    'nativeTabBarActiveTextColor',
    'nativeTabBarInactiveTextColor',
    'nativeTabBarIndicatorColor',
    'nativeSubHeaderSelectedBackgroundColor',
  ]) {
    assert.match(
      manager,
      new RegExp(
        `@ReactProp\\(name = "${prop}", customType = "Color"\\)`
      )
    );
  }
});

test('connects Android props, direct press events, gesture bridge, and file logger', () => {
  const manager = read(
    'android/src/main/java/com/reactnativepagerview/CollapsiblePagerViewManager.kt'
  );
  const host = read(
    'android/src/main/java/com/reactnativepagerview/CollapsiblePagerHost.kt'
  );
  const headers = read(
    'android/src/main/java/com/reactnativepagerview/CollapsiblePagerNativeHeaders.kt'
  );
  const gradle = read('android/build.gradle');
  const manifest = JSON.parse(read('package.json'));

  assert.match(manager, /updateNativeTabBarItems\(value\)/);
  assert.match(manager, /updateNativeSubHeader\(value\)/);
  assert.match(manager, /NativeHeaderPressEvent\.TAB_EVENT_NAME/);
  assert.match(manager, /host\.updateNativeTabProgress\(position, offset\)/);
  assert.match(host, /beginForwardingToRecycler\(event\)/);
  assert.match(host, /NativeGestureUtil\.notifyNativeGestureStarted/);
  assert.match(host, /nativeGestureStarted \|\| nestedNativeGestureStarted/);
  assert.match(host, /OneKeyLog\.debug\("CollapsiblePager"/);
  assert.match(headers, /indicator\.layout\(/);
  assert.doesNotMatch(headers, /indicator\.layoutParams =/);
  assert.match(
    headers,
    /scrollTargetCenterX = interpolatedIndicatorX \+ interpolatedIndicatorWidth \/ 2f/
  );
  assert.match(
    headers,
    /\(scrollTargetCenterX - width \/ 2f\)\.roundToInt\(\)/
  );
  assert.match(gradle, /project\(":onekeyfe_react-native-native-logger"\)/);
  assert.equal(
    manifest.peerDependencies['@onekeyfe/react-native-native-logger'],
    manifest.version
  );
});
