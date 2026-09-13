// OneKey patch: focused regression checks for the serialized selector adapter contract.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { test } = require('node:test');
const ts = require('typescript');
const { JSDOM } = require('jsdom');
const packageRoot = path.resolve(__dirname, '../..');
const originalLoader = require.extensions['.ts'];
require.extensions['.ts'] = (module, filename) => {
  if (!filename.startsWith(packageRoot)) return originalLoader?.(module, filename);
  const result = ts.transpileModule(fs.readFileSync(filename, 'utf8').replaceAll('import.meta.url', "'https://unit.test/NativeList.js'"), {
    compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS },
  });
  module._compile(result.outputText, filename);
};
const { validateSnapshot, serializePatches } = require('../validation.ts');
const { NativeListWebEngine, computeWebListLayout, estimateWebRowHeight } = require('../web/NativeListWebEngine.ts');
const identity = (key, fields = {}) => ({ type: 'identity', key, title: key, leading: { kind: 'network' }, ...fields });
const snapshot = (rows, fields = {}) => ({ schemaVersion: 1, generation: 1, layout: { kind: 'sectioned' }, rows, ...fields });

test('compact native indexes lay out and select retained labels by visible order', () => {
  const ios = fs.readFileSync(path.join(packageRoot, 'ios/RNCNativeListView.swift'), 'utf8');
  const android = fs.readFileSync(path.join(packageRoot, 'android/src/main/java/com/onekey/nativelist/NativeListView.kt'), 'utf8');
  assert.match(ios, /for \(visibleIndex, index\) in visibleIndices\.sorted\(\)\.enumerated\(\)/);
  assert.match(ios, /visibleLabelCenterY\(\s*visibleIndex: visibleIndex,/);
  assert.match(ios, /let index = visibleIndices\[slot\]/);
  assert.match(android, /visibleIndices\.forEachIndexed \{ visibleIndex, index ->/);
  assert.match(android, /val index = visibleIndices\[slot\]/);
});

test('window-centered native indexes use full-window interactive hosts', () => {
  const ios = fs.readFileSync(path.join(packageRoot, 'ios/RNCNativeListView.swift'), 'utf8');
  const android = fs.readFileSync(path.join(packageRoot, 'android/src/main/java/com/onekey/nativelist/NativeListView.kt'), 'utf8');
  assert.match(ios, /window\.addSubview\(sectionIndexView\)/);
  assert.match(ios, /sectionIndexView\.centerYAnchor\.constraint\(equalTo: window\.centerYAnchor\)/);
  assert.match(ios, /sectionIndexView\.preferredHeight\(constrainedTo: window\.bounds\.height\)/);
  assert.match(android, /windowHost\.addView\(sectionIndexView/);
  assert.match(android, /sectionIndexView\.preferredHeight\(windowHost\.height\)/);
  assert.match(android, /topMargin = \(windowHost\.height - railHeight\) \/ 2/);
});

test('native Market quote updates clear stale attributed text and preserve open anchors', () => {
  const ios = fs.readFileSync(path.join(packageRoot, 'ios/NativeListCell.swift'), 'utf8');
  const android = fs.readFileSync(path.join(packageRoot, 'android/src/main/java/com/onekey/nativelist/NativeListView.kt'), 'utf8');
  assert.match(ios, /price\.setAttributedTitle\(nil, for: \.normal\)\s+price\.setTitle/);
  assert.match(android, /if \(!marketQuoteOnly\) invalidateActionAnchor\("snapshot"\)/);
});

test('native Market patch parity retains layout, reuse, refresh, and action contracts', () => {
  const adapter = fs.readFileSync(
    path.join(
      packageRoot,
      'android/src/main/java/com/onekey/nativelist/NativeListAdapter.kt'
    ),
    'utf8'
  );
  const androidRow = fs.readFileSync(
    path.join(
      packageRoot,
      'android/src/main/java/com/onekey/nativelist/NativeListRowView.kt'
    ),
    'utf8'
  );
  const androidList = fs.readFileSync(
    path.join(
      packageRoot,
      'android/src/main/java/com/onekey/nativelist/NativeListView.kt'
    ),
    'utf8'
  );
  const iosCell = fs.readFileSync(
    path.join(packageRoot, 'ios/NativeListCell.swift'),
    'utf8'
  );
  const iosList = fs.readFileSync(
    path.join(packageRoot, 'ios/RNCNativeListView.swift'),
    'utf8'
  );
  const models = fs.readFileSync(
    path.join(packageRoot, 'src/models.ts'),
    'utf8'
  );
  const validation = fs.readFileSync(
    path.join(packageRoot, 'src/validation.ts'),
    'utf8'
  );

  assert.match(adapter, /fun marketSourceEdgePx\(position: Int\): Double/);
  assert.match(androidRow, /windowPointPixels: android\.graphics\.PointF\?/);
  assert.match(androidRow, /BackgroundStyleApplicator\.clipToPaddingBox/);
  assert.match(androidRow, /private val marketSubtitleLine/);
  assert.match(androidRow, /private fun applyMarketTextMetrics/);
  assert.match(androidRow, /optString\("actionText", "Retry"\)/);
  assert.match(androidList, /private val refreshIndicatorTravelPx/);
  assert.match(androidList, /private fun updateRefreshIndicatorOffset/);
  assert.match(androidList, /anchor\.put\("windowPoint"/);
  assert.match(iosCell, /var windowPoint: CGPoint\?/);
  assert.match(iosCell, /private let marketSubtitleStack/);
  assert.match(iosCell, /NativeListAccessoryButton\(type: \.system\)/);
  assert.match(iosCell, /string\("titleBadgeLayout"\) == "inline"/);
  assert.match(iosCell, /string\("actionText", default: "Retry"\)/);
  assert.match(iosList, /origin\?\.windowPoint = gesture\.location/);
  for (const field of [
    'titleBadgeLayout',
    'contentTrailingGap',
    'subtitleTrailingPadding',
    'subtitlePrefix',
    'actionText',
  ]) {
    assert.match(models, new RegExp(field));
    assert.match(validation, new RegExp(field));
  }
  assert.match(models, /windowPoint/);
});

test('iOS reorder defers compatible snapshots and commits against the current key order', () => {
  const ios = fs.readFileSync(
    path.join(packageRoot, 'ios/RNCNativeListView.swift'),
    'utf8'
  );
  assert.match(
    ios,
    /if let current = config, interactiveReorderSource != nil \{\s+if canDeferSnapshotDuringInteractiveReorder\(from: current, to: next\) \{\s+deferSnapshotDuringInteractiveReorder\(from: current, to: next\)\s+return\s+\}\s+cancelInteractiveReorderForStructuralUpdate\(\)/
  );
  assert.match(
    ios,
    /private func deferSnapshotDuringInteractiveReorder[\s\S]*?config = next\s+itemsByKey = Dictionary[\s\S]*?deferredReorderReconfigureKeys\.formUnion\(changedKeys\)/
  );
  assert.match(
    ios,
    /let keys = snapshot\.itemIdentifiers[\s\S]*?let sourceIndex = keys\.firstIndex\(of: source\.key\)[\s\S]*?let targetIndex = keys\.firstIndex\(of: targetKey\)[\s\S]*?snapshot\.moveItem\(source\.key, beforeItem: targetKey\)[\s\S]*?snapshot\.moveItem\(source\.key, afterItem: targetKey\)/
  );
  assert.match(
    ios,
    /private func completeInteractiveReorder[\s\S]*?let items = keys\.compactMap \{ itemsByKey\[\$0\] \}[\s\S]*?current\.items = items\s+config = current[\s\S]*?scheduleDeferredReorderRefresh\(\)/
  );
});

test('native source images default to no placeholder and restore explicit backgrounds after load', () => {
  const android = fs.readFileSync(
    path.join(
      packageRoot,
      'android/src/main/java/com/onekey/nativelist/NativeListRowView.kt'
    ),
    'utf8'
  );
  const ios = fs.readFileSync(
    path.join(packageRoot, 'ios/NativeListCell.swift'),
    'utf8'
  );
  assert.match(android, /optString\("loadingStrategy", "none"\)/);
  assert.match(android, /loadingStrategy = source\.optString\("loadingStrategy", "none"\)/);
  assert.match(android, /if \(!isIcon && sources\.isNotEmpty\(\)\) Color\.TRANSPARENT/);
  assert.match(ios, /string\("loadingStrategy", default: "none"\)/);
  assert.match(ios, /loadingStrategy: source\.string\("loadingStrategy", default: "none"\)/);
  assert.match(ios, /self\.leadingContainer\.backgroundColor = visualBackgroundColor/);
});

test('Android NativeList declares its native logger project as a peer dependency', () => {
  const manifest = require('../../package.json');
  const loggerManifest = require('../../../../native-modules/native-logger/package.json');
  const gradle = fs.readFileSync(path.join(packageRoot, 'android/build.gradle'), 'utf8');
  assert.equal(manifest.peerDependencies['@onekeyfe/react-native-native-logger'], loggerManifest.version);
  assert.match(gradle, /project\(":onekeyfe_react-native-native-logger"\)/);
});

function mount(rows, props = {}) {
  const dom = new JSDOM('<!doctype html><div id="host"></div>', { pretendToBeVisual: true });
  const view = dom.window;
  global.Element = view.Element;
  global.HTMLElement = view.HTMLElement;
  global.Node = view.Node;
  view.HTMLElement.prototype.getBoundingClientRect = () => ({ x: 20, y: 30, left: 20, top: 30, right: 100, bottom: 54, width: 80, height: 24 });
  view.HTMLElement.prototype.scrollTo = function ({ top = 0, left = 0 }) { this.scrollTop = top; this.scrollLeft = left; };
  const actions = [], invalidated = [], selections = [];
  const engine = new NativeListWebEngine(view.document.getElementById('host'), snapshot(rows, props), {
    onRowAction: (event) => actions.push(event),
    onActionAnchorInvalidated: (event) => invalidated.push(event),
    onSelectionDelta: (event) => selections.push(event),
  }, false);
  return { view, engine, document: view.document, actions, invalidated, selections, close() { engine.destroy(); view.close(); } };
}
test('index jumps highlight the section reached after an exact spacer boundary', async () => {
  const page = mount([
    { type: 'sectionHeader', key: 'A', sectionKey: 'A', title: 'A', indexTitle: 'A', height: 36 },
    identity('a', { sectionKey: 'A', height: 48 }),
    { type: 'system', variant: 'spacer', key: 'gap', height: 20 },
    { type: 'sectionHeader', key: 'B', sectionKey: 'B', title: 'B', indexTitle: 'B', height: 36 },
    identity('b', { sectionKey: 'B', height: 48 }),
    { type: 'system', variant: 'spacer', key: 'tail-1', height: 500 },
    { type: 'system', variant: 'spacer', key: 'tail-2', height: 500 },
  ], { capabilities: { sectionIndex: { enabled: true } } });
  try {
    const viewport = page.document.querySelector('.ok-native-list-viewport');
    Object.defineProperty(viewport, 'clientHeight', { value: 400 });
    Object.defineProperty(viewport, 'clientWidth', { value: 320 });
    const index = page.document.querySelector('[aria-label="Jump to B"]');
    index.click();
    await new Promise(resolve => page.view.requestAnimationFrame(() => page.view.requestAnimationFrame(resolve)));
    assert.equal(page.document.querySelector('.ok-native-list-viewport').scrollTop, 104);
    assert.equal(index.dataset.active, 'true');
    assert.equal(page.document.querySelector('[aria-label="Jump to A"]').dataset.active, 'false');
  } finally {
    page.close();
  }
});
test('web section index centers in the browser window outside a lower list viewport', () => {
  const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
  const page = mount(
    letters.map((letter) => ({
      type: 'sectionHeader',
      key: letter,
      sectionKey: letter,
      title: letter,
      indexTitle: letter,
      height: 36,
    })),
    {
      capabilities: {
        sectionIndex: { enabled: true, centeredInWindow: true },
      },
    },
  );
  try {
    const viewport = page.document.querySelector('.ok-native-list-viewport');
    const frame = page.document.querySelector(
      '.ok-native-list-viewport-frame',
    );
    const rail = page.document.querySelector('.ok-native-list-index-rail');
    Object.defineProperty(page.view, 'innerHeight', {
      configurable: true,
      value: 900,
    });
    Object.defineProperty(viewport, 'clientHeight', {
      configurable: true,
      value: 500,
    });
    Object.defineProperty(viewport, 'clientWidth', {
      configurable: true,
      value: 320,
    });
    frame.getBoundingClientRect = () => ({
      x: 0,
      y: 300,
      left: 0,
      top: 300,
      right: 320,
      bottom: 800,
      width: 320,
      height: 500,
    });
    page.engine.recomputeLayout();

    assert.equal(rail.parentElement, page.document.body);
    const buttons = [
      ...rail.querySelectorAll('[data-section-entry-index]'),
    ];
    const firstCenterY = Number.parseFloat(buttons[0].style.top);
    const lastCenterY = Number.parseFloat(buttons.at(-1).style.top);
    assert.equal(
      Number.parseFloat(rail.style.top) + (firstCenterY + lastCenterY) / 2,
      450,
    );
  } finally {
    page.close();
  }
});
test('compact web index keeps every section reachable without overflowing short viewports', () => {
  const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
  const rows = [
    ...letters.map((letter) => ({
      type: 'sectionHeader',
      key: letter,
      sectionKey: letter,
      title: letter,
      indexTitle: letter,
      height: 36,
    })),
    { type: 'system', variant: 'spacer', key: 'tail', height: 500 },
  ];
  const page = mount(rows, {
    capabilities: { sectionIndex: { enabled: true } },
  });
  try {
    const viewport = page.document.querySelector('.ok-native-list-viewport');
    const frame = page.document.querySelector('.ok-native-list-viewport-frame');
    const rail = page.document.querySelector('.ok-native-list-index-rail');
    Object.defineProperty(viewport, 'clientHeight', {
      configurable: true,
      value: 160,
    });
    Object.defineProperty(viewport, 'clientWidth', {
      configurable: true,
      value: 320,
    });
    rail.getBoundingClientRect = () => ({
      x: 288,
      y: 20,
      left: 288,
      top: 20,
      right: 320,
      bottom: 180,
      width: 32,
      height: 160,
    });
    frame.getBoundingClientRect = () => ({
      x: 0,
      y: 20,
      left: 0,
      top: 20,
      right: 320,
      bottom: 180,
      width: 320,
      height: 160,
    });
    page.engine.recomputeLayout();

    const buttons = [...rail.querySelectorAll('[data-section-entry-index]')];
    assert.equal(rail.dataset.compact, 'true');
    assert(buttons.length < letters.length);
    assert.equal(page.document.querySelector('[aria-label="Jump to H"]'), null);
    assert.equal(
      page.document.querySelector('.ok-native-list-index-preview'),
      null,
    );
    assert.equal(page.engine.layout.items[0].width, 304);
    const visibleTops = buttons.map(button => Number.parseFloat(button.style.top));
    assert(
      visibleTops.slice(1).every((top, index) => top - visibleTops[index] === 16),
    );

    const targetIndex = 7;
    const targetY = 20 + 8 + ((targetIndex + 0.5) / letters.length) * 144;
    const overlappingVisibleButton = buttons[0];
    assert(overlappingVisibleButton);
    overlappingVisibleButton.dispatchEvent(
      new page.view.MouseEvent('pointerdown', {
        bubbles: true,
        buttons: 1,
        clientX: 304,
        clientY: targetY,
      }),
    );
    overlappingVisibleButton.dispatchEvent(
      new page.view.MouseEvent('click', {
        bubbles: true,
        detail: 1,
      }),
    );
    overlappingVisibleButton.dispatchEvent(
      new page.view.MouseEvent('pointerup', {
        bubbles: true,
        buttons: 0,
        clientX: 304,
        clientY: targetY,
      }),
    );
    assert.equal(viewport.scrollTop, targetIndex * 36);

    Object.defineProperty(viewport, 'clientHeight', {
      configurable: true,
      value: 100,
    });
    page.engine.recomputeLayout();
    assert.equal(rail.hidden, true);
    assert.equal(page.engine.layout.items[0].width, 320);
  } finally {
    page.close();
  }
});
test('selector explicit dimensions override presets without changing existing defaults', () => {
  const normal = identity('n', { presentation: 'networkSelector' });
  assert.equal(estimateWebRowHeight(normal, snapshot([normal]), 400), 47);
  assert.equal(estimateWebRowHeight({ ...normal, height: 48 }, snapshot([normal]), 400), 48);
  const account = identity('a', { presentation: 'accountSelector', height: 60 });
  assert.equal(computeWebListLayout(snapshot([normal, account]), 400, 800).items[1].height, 60);
});
test('explicit native height rounding survives serialization and rejects incomplete policies', () => {
  const row = { type: 'sectionHeader', key: 'a', sectionKey: 'a', title: 'A', presentation: 'networkSelector', height: 36, heightRounding: 'nearest' };
  const accepted = validateSnapshot(JSON.parse(JSON.stringify(snapshot([row]))));
  assert.equal(accepted.rows[0].heightRounding, 'nearest');
  assert.equal(estimateWebRowHeight(row, accepted, 400), 36);
  assert.throws(() => validateSnapshot(snapshot([{ ...row, height: undefined }])), /heightRounding/);
  assert.throws(() => validateSnapshot(snapshot([{ ...row, heightRounding: 'ceil' }])), /heightRounding/);
  assert.doesNotThrow(() => serializePatches([{ type: 'sectionHeader', key: 'a', changes: { heightRounding: 'nearest' } }]));
  assert.throws(() => serializePatches([{ type: 'sectionHeader', key: 'a', changes: { heightRounding: 'ceil' } }]), /heightRounding/);
});
test('wallet badge heights propagate through grouped layout', () => {
  const group = { type: 'walletGroup', key: 'g', parent: identity('g', { presentation: 'walletSidebar' }), children: [identity('c', { presentation: 'walletSidebar', badges: [{ key: 'b', text: 'Bot' }] })] };
  assert.equal(estimateWebRowHeight(group, snapshot([group]), 96), 172);
});
test('invalid title ranges and overlays are rejected before native serialization', () => {
  assert.throws(() => validateSnapshot(snapshot([identity('x', { title: 'abc', titleMatch: [{ start: 1, end: 4 }] })])), /titleMatch/);
  assert.throws(() => validateSnapshot(snapshot([identity('x', { leading: { kind: 'wallet', overlays: [{ position: 'topLeft', size: 100 }] } })])), /size/);
  assert.throws(() => validateSnapshot(snapshot([identity('x', { opacity: 2 })])), /opacity/);
});
test('search matches use info color and retain unhighlighted title text', () => {
  const page = mount([identity('x', { title: 'Ethereum', titleMatch: [{ start: 2, end: 5 }], height: 48, presentation: 'networkSelector' })]);
  assert.equal(page.document.querySelector('.ok-native-list-title').textContent, 'Ethereum');
  assert.equal(page.document.querySelector('.ok-native-list-title .ok-native-list-info').textContent, 'her');
  assert.equal(page.document.querySelector('.ok-native-list-title').style.fontSize, '16px');
  page.close();
});
test('web Market rows render subtitle prefixes with independent metrics', () => {
  const row = {
    type: 'market',
    key: 'btc',
    variant: 'token',
    leading: { kind: 'token' },
    title: 'BTC',
    subtitle: '$1.23B',
    subtitlePrefix: {
      text: 'Bitcoin',
      gap: 6,
      maxWidth: 96,
      style: { fontSize: 11, lineHeight: 15, fontWeight: 'medium', color: '#123456' },
    },
    price: '$64,230.00',
    change: { text: '+2.40%', tone: 'positive' },
  };
  const page = mount([row]);
  try {
    const line = page.document.querySelector('.ok-native-list-market-subtitle-line');
    const prefix = line.querySelector('.ok-native-list-market-subtitle-prefix');
    assert.equal(line.style.gap, '6px');
    assert.equal(prefix.textContent, 'Bitcoin');
    assert.equal(prefix.style.maxWidth, '96px');
    assert.equal(prefix.style.fontSize, '11px');
    assert.equal(prefix.style.lineHeight, '15px');
    assert.equal(prefix.style.fontWeight, '500');
    assert.equal(prefix.style.color, 'rgb(18, 52, 86)');
    assert.equal(line.querySelector('.ok-native-list-market-subtitle').textContent, '$1.23B');
  } finally {
    page.close();
  }

  const prefixOnly = mount([{ ...row, key: 'eth', subtitle: undefined }]);
  try {
    assert.equal(
      prefixOnly.document.querySelector('.ok-native-list-market-subtitle-prefix').textContent,
      'Bitcoin',
    );
    assert.equal(
      prefixOnly.document.querySelector('.ok-native-list-market-subtitle'),
      null,
    );
  } finally {
    prefixOnly.close();
  }
});
test('subtitle supports a leading address separator and distinct caution tone', () => {
  const page = mount([identity('x', { subtitleSegments: [{ text: 'Create address', tone: 'caution', separatorBefore: true }] })]);
  assert.ok(page.document.querySelector('.ok-native-list-subtitle-segments').firstChild.classList.contains('ok-native-list-subtitle-dot'));
  assert.equal(page.document.querySelector('.ok-native-list-subtitle-segments .ok-native-list-secondary').dataset.tone, 'caution');
  page.close();
});
test('pressDisabled gates row clicks while preserving plus accessory actions', () => {
  const page = mount([identity('x', { presentation: 'accountSelector', height: 60, pressDisabled: true, trailing: [{ kind: 'icon', name: 'PlusSmallOutline', actionKey: 'create', testID: 'account-manager-plus-button-icon-btn' }] })]);
  page.document.querySelector('.ok-native-list-title').click();
  assert.equal(page.actions.length, 0);
  page.document.querySelector('[data-native-list-action="create"]').click();
  assert.equal(page.actions[0].actionKey, 'create');
  assert.ok(page.document.querySelector('[data-testid="account-manager-plus-button-icon-btn"]'));
  page.close();
});
test('section help is independent of checkbox selection and anchored to the title', () => {
  const rows = [{ type: 'sectionHeader', key: 'h', sectionKey: 'a', title: 'Assets', titleActionKey: 'help', titleActionOnHover: true, checkbox: { kind: 'checkbox', state: 'unchecked', target: { scope: 'section', sectionKey: 'a' } } }, identity('x', { sectionKey: 'a' })];
  const page = mount(rows, { selection: { mode: 'multiple', selectedKeys: [] } });
  const title = page.document.querySelector('[data-native-list-action="help"]');
  title.click();
  assert.equal(page.actions[0].actionKey, 'help');
  assert.equal(page.actions[0].anchor.source, 'leadingAction');
  assert.equal(page.selections.length, 0);
  title.dispatchEvent(new page.view.MouseEvent('pointerover', { bubbles: true }));
  const token = page.actions.at(-1).anchor.token;
  page.engine.setActionAnchorState({ token, open: true });
  title.dispatchEvent(new page.view.MouseEvent('pointerout', { bubbles: true }));
  assert.deepEqual(page.invalidated.at(-1), { token, reason: 'pointerLeave' });
  page.close();
});
test('failed network images render the official globe SVG fallback', () => {
  const page = mount([identity('x', { leading: { kind: 'network', image: { uri: 'https://example.invalid/missing.png', width: 32, height: 32, loadingStrategy: 'static' }, fallbackIcon: { name: 'GlobusOutline' } } })]);
  page.document.querySelector('.ok-native-list-visual-main').dispatchEvent(new page.view.Event('error'));
  assert.equal(page.document.querySelector('.ok-native-list-visual-main'), null);
  assert.ok(page.document.querySelector('.ok-native-list-visual-fallback svg path'));
  page.close();
});
function fakeImageRetryClock(view) {
  const timers = new Map();
  const cleared = [];
  let serial = 0;
  view.setTimeout = (callback, delay) => {
    assert.ok([0, 1000, 2000].includes(delay));
    timers.set(++serial, callback);
    return serial;
  };
  view.clearTimeout = id => { cleared.push(id); timers.delete(id); };
  return { timers, cleared, flush() { const callbacks = [...timers.values()]; timers.clear(); callbacks.forEach(callback => callback()); } };
}
test('optimized image failure falls back to raw, retries raw once, then shows the globe', () => {
  const page = mount([identity('image', { leading: { kind: 'network', image: { uri: 'https://images.test/optimized.png', fallbackUri: 'https://images.test/raw.png', retryTimes: 1, width: 32, height: 32, loadingStrategy: 'static' }, fallbackIcon: { name: 'GlobusOutline' } } })]);
  const clock = fakeImageRetryClock(page.view);
  const image = page.document.querySelector('.ok-native-list-visual-main');
  image.dispatchEvent(new page.view.Event('error'));
  assert.equal(image.src, 'https://images.test/raw.png');
  assert.equal(clock.timers.size, 0);
  image.dispatchEvent(new page.view.Event('error'));
  assert.equal(clock.timers.size, 1);
  image.dispatchEvent(new page.view.Event('error'));
  assert.equal(clock.timers.size, 1);
  assert.equal(page.document.querySelector('.ok-native-list-visual-main'), image);
  clock.flush();
  assert.equal(image.src, 'https://images.test/raw.png');
  image.dispatchEvent(new page.view.Event('error'));
  assert.equal(page.document.querySelector('.ok-native-list-visual-main'), null);
  assert.ok(page.document.querySelector('.ok-native-list-visual-fallback svg'));
  page.close();
});
test('raw image retry works without an optimized source and successful loads stop pending retries', () => {
  const page = mount([identity('image', { leading: { kind: 'network', image: { uri: 'https://images.test/raw.png', retryTimes: 1, width: 32, height: 32 } } })]);
  const clock = fakeImageRetryClock(page.view);
  const image = page.document.querySelector('.ok-native-list-visual-main');
  image.dispatchEvent(new page.view.Event('error'));
  assert.equal(clock.timers.size, 1);
  image.dispatchEvent(new page.view.Event('load'));
  assert.equal(clock.timers.size, 0);
  assert.equal(clock.cleared.length, 1);
  page.close();
});
test('row rebinding and list destruction cancel image retries without reviving detached images', () => {
  const row = uri => identity('image', { leading: { kind: 'network', image: { uri, retryTimes: 1, width: 32, height: 32 } } });
  const page = mount([row('https://images.test/old.png')]);
  const clock = fakeImageRetryClock(page.view);
  const old = page.document.querySelector('.ok-native-list-visual-main');
  old.dispatchEvent(new page.view.Event('error'));
  const staleCallback = [...clock.timers.values()][0];
  page.engine.applySnapshot(snapshot([row('https://images.test/new.png')], { generation: 2 }));
  assert.equal(clock.timers.size, 0);
  staleCallback();
  assert.equal(old.src, 'https://images.test/old.png');
  const current = page.document.querySelector('.ok-native-list-visual-main');
  assert.equal(current.src, 'https://images.test/new.png');
  current.dispatchEvent(new page.view.Event('error'));
  assert.equal(clock.timers.size, 1);
  page.engine.destroy();
  assert.equal(clock.timers.size, 0);
  page.view.close();
});
test('provider overlays preserve official colors and both corner positions', () => {
  const page = mount([identity('x', { leading: { kind: 'wallet', overlays: [{ position: 'topLeft', name: 'GoogleIllus' }, { position: 'bottomRight', text: '3' }] } })]);
  const corners = page.document.querySelectorAll('.ok-native-list-visual-overlay');
  assert.equal(corners.length, 2);
  assert.equal(corners[0].querySelector('path').getAttribute('fill'), '#4285F4');
  assert.equal(corners[1].textContent, '3');
  page.close();
});
test('wallet child taps retain child identity and original automation IDs', () => {
  const group = { type: 'walletGroup', key: 'g', parent: identity('g', { presentation: 'walletSidebar' }), children: [identity('c', { presentation: 'walletSidebar' })] };
  const page = mount([group, identity('x', { testID: 'original-network-id', backgroundColor: '#123456' })]);
  page.document.querySelector('[data-native-list-group-member-key="c"] .ok-native-list-title').click();
  assert.equal(page.actions[0].rowKey, 'c');
  assert.ok(page.document.querySelector('[data-testid="original-network-id"]'));
  page.close();
});
test('very small amounts preserve compact digits inside independent subtitle and value fragments', () => {
  const runs = [{ text: '0.0' }, { text: '7', style: 'subscript' }, { text: '123' }];
  const page = mount([identity('x', { subtitleSegments: [{ text: '0.00000000123 BTC', textSegments: runs }], trailing: [{ kind: 'value', text: '0.00000000123', textSegments: runs }] })]);
  const subtitle = page.document.querySelector('.ok-native-list-subtitle-segments .ok-native-list-secondary');
  const value = page.document.querySelector('.ok-native-list-accessory');
  assert.equal(subtitle.textContent, '0.07123');
  assert.equal(subtitle.children[1].style.fontSize, '9px');
  assert.equal(value.children[1].style.fontSize, '10px');
  page.close();
});
test('deprecated wallet warnings remain normal scroll content with source title and description', () => {
  const page = mount([{ type: 'system', variant: 'warning', key: 'warning', title: 'Upgrade required', message: 'This wallet needs an upgrade before creating more accounts.', backgroundColor: '#ffcc00', backgroundFullWidth: true, borderColor: '#cc9900' }, identity('x')], { layout: { kind: 'linear', contentPaddingHorizontal: 8 } });
  const warning = page.document.querySelector('.ok-native-list-warning');
  assert.ok(warning.closest('.ok-native-list-content'));
  assert.equal(warning.querySelector('.ok-native-list-warning-title').textContent, 'Upgrade required');
  assert.equal(warning.querySelector('.ok-native-list-warning-message').textContent, 'This wallet needs an upgrade before creating more accounts.');
  assert.equal(warning.parentElement.style.contain, 'layout style');
  page.close();
});
test('late image failures cannot replace the latest row after rapid snapshot rebinding', () => {
  const makeRow = (index) => identity('x', { title: 'Account ' + index, revision: index, leading: { kind: 'network', image: { uri: 'https://example.invalid/' + index + '.png', width: 32, height: 32 }, fallbackIcon: { name: 'GlobusOutline' } } });
  const page = mount([makeRow(0)]);
  for (let index = 1; index <= 50; index += 1) {
    const oldImage = page.document.querySelector('.ok-native-list-visual-main');
    page.engine.applySnapshot(snapshot([makeRow(index)], { generation: index + 1 }));
    oldImage.dispatchEvent(new page.view.Event('error'));
    assert.equal(page.document.querySelector('.ok-native-list-title').textContent, 'Account ' + index);
    assert.ok(page.document.querySelector('.ok-native-list-visual-main').src.endsWith('/' + index + '.png'));
  }
  page.close();
});
test('non-sticky asset help does not become a sticky title before alphabet sections', async () => {
  const page = mount([{ type: 'sectionHeader', key: 'help', sectionKey: 'assets', title: 'Asset help', height: 47, sticky: false }, identity('asset', { height: 80 }), { type: 'sectionHeader', key: 'a', sectionKey: 'a', title: 'A', height: 36 }, identity('alphabet', { height: 800 })], { layout: { kind: 'sectioned', stickyHeaders: true } });
  const viewport = page.document.querySelector('.ok-native-list-viewport');
  viewport.scrollTop = 60;
  viewport.dispatchEvent(new page.view.Event('scroll'));
  await new Promise((resolve) => setTimeout(resolve, 25));
  assert.equal(page.document.querySelector('.ok-native-list-sticky').hidden, true);
  viewport.scrollTop = 170;
  viewport.dispatchEvent(new page.view.Event('scroll'));
  await new Promise((resolve) => setTimeout(resolve, 25));
  assert.equal(page.document.querySelector('.ok-native-list-sticky').textContent, 'A');
  page.close();
});
test('explicit active account and wallet states update without checkbox selection', () => {
  for (const presentation of ['accountSelector', 'walletSidebar']) {
    const row = identity('selected', { presentation, selected: true, height: 60 });
    const page = mount([row]);
    const item = page.document.querySelector('[data-native-list-row-key="selected"]');
    assert.equal(item.dataset.nativeListSelected, 'true');
    page.engine.applySnapshot(snapshot([{ ...row, selected: false }], { generation: 2 }));
    assert.equal(item.dataset.nativeListSelected, 'false');
    page.engine.applySnapshot(snapshot([row], { generation: 3 }));
    assert.equal(item.dataset.nativeListSelected, 'true');
    page.close();
  }
});
test('account corner badge preserves its outer ring and inner image dimensions', () => {
  const row = identity('account', { presentation: 'accountSelector', height: 60, leading: { kind: 'account', shape: 'rounded', overlays: [{ position: 'bottomRight', size: 20, padding: 2, offset: 4, name: 'AllNetworksSolid' }] } });
  const page = mount([row]);
  const overlay = page.document.querySelector('.ok-native-list-visual-overlay');
  assert.equal(overlay.style.width, '20px');
  assert.equal(overlay.style.padding, '2px');
  assert.equal(overlay.style.right, '-4px');
  assert.equal(page.document.querySelector('.ok-native-list-account-row').style.borderRadius, '12px');
  assert.equal(page.document.querySelector('.ok-native-list-visual').style.borderRadius, '8px');
  assert.throws(() => validateSnapshot(snapshot([{ ...row, leading: { ...row.leading, overlays: [{ ...row.leading.overlays[0], padding: 10 }] } }])), /padding/);
  page.close();
});
test('network header action anchor includes its own unclipped source SVG underline', () => {
  const page = mount([{ type: 'sectionHeader', key: 'header', sectionKey: 'summary', presentation: 'networkSelector', height: 71, variant: 'summary', title: '15 networks selected', titleActionKey: 'help', value: 'Deselect all', valueActionKey: 'toggle' }]);
  const title = page.document.querySelector('[data-native-list-action="help"]');
  assert.equal(title.querySelector('.ok-native-list-section-title-text').textContent, '15 networks selected');
  assert.equal(title.querySelector('svg line').getAttribute('stroke-dasharray'), '0,4');
  assert.equal(title.style.textDecoration, '');
  assert.equal(title.querySelector('svg').style.position, 'absolute');
  assert.equal(title.style.paddingBottom, '3px');
  assert.equal(page.document.querySelector('.ok-native-list-section').style.padding, '24px 12px 20px');
  assert.equal(page.document.querySelector('[data-native-list-action="toggle"]').style.fontSize, '16px');
  page.close();
});
test('selector text enables tabular digits while generic rows retain their existing font features', () => {
  const generic = identity('generic');
  const account = identity('account', { presentation: 'accountSelector', title: 'Account 12', subtitleSegments: [{ text: '$5.70' }] });
  const network = identity('network', { presentation: 'networkSelector', trailing: [{ kind: 'value', text: '$5.70' }] });
  const wallet = identity('wallet', { presentation: 'walletSidebar', title: 'Wallet 12' });
  const group = { type: 'walletGroup', key: 'group', parent: { ...wallet, key: 'group' }, children: [{ ...wallet, key: 'child' }] };
  const header = { type: 'sectionHeader', key: 'header', sectionKey: 'summary', presentation: 'networkSelector', variant: 'summary', title: '12 networks selected', value: 'Deselect all', valueActionKey: 'toggle' };
  const page = mount([generic, account, network, group, header]);
  const genericBody = page.document.querySelector('[data-native-list-row-key="generic"]>.ok-native-list-row');
  assert.equal(genericBody.style.fontVariantNumeric, '');
  for (const body of page.document.querySelectorAll('.ok-native-list-account-row,.ok-native-list-network-row,.ok-native-list-wallet-row,.ok-native-list-section')) {
    assert.equal(body.style.fontVariantNumeric, 'tabular-nums');
    for (const text of body.querySelectorAll('span,button')) assert.equal(text.style.fontVariantNumeric, 'tabular-nums');
  }
  page.close();
});
test('touch-active rows keep the pressed background rule', () => {
  const page = mount([identity('network', { presentation: 'networkSelector' })]);
  const css = page.document.querySelector('style').textContent;
  assert.match(css, /native-list-disabled="true"\]\):active>\.ok-native-list-row\{background:var\(--nl-pressed\)\}/);
  page.close();
});
test('wallet hover anchors the complete child row without consuming normal selection taps', () => {
  const child = identity('child', { presentation: 'walletSidebar', height: 68, titleActionKey: 'wallet.help', titleActionOnHover: true });
  const group = { type: 'walletGroup', key: 'group', parent: identity('group', { presentation: 'walletSidebar', height: 68 }), children: [child] };
  const page = mount([group]);
  assert.equal(estimateWebRowHeight(group, snapshot([group]), 96), 150);
  const body = page.document.querySelector('[data-native-list-group-member-key="child"] .ok-native-list-wallet-row');
  body.querySelector('.ok-native-list-visual').dispatchEvent(new page.view.MouseEvent('pointerover', { bubbles: true }));
  assert.equal(page.actions.at(-1).rowKey, 'child');
  assert.equal(page.actions.at(-1).actionKey, 'wallet.help');
  assert.deepEqual(page.actions.at(-1).anchor.windowRect, { x: 20, y: 30, width: 80, height: 24 });
  const token = page.actions.at(-1).anchor.token;
  page.engine.setActionAnchorState({ token, open: true });
  body.dispatchEvent(new page.view.MouseEvent('pointerout', { bubbles: true }));
  assert.deepEqual(page.invalidated.at(-1), { token, reason: 'pointerLeave' });
  body.click();
  assert.equal(page.actions.at(-1).rowKey, 'child');
  assert.equal(page.actions.at(-1).actionKey, 'press');
  page.close();
});
test('account menu preserves its automation ID and returns the 24-point layout slot', () => {
  const page = mount([identity('account', { presentation: 'accountSelector', height: 60, trailing: [{ kind: 'icon', name: 'DotHorOutline', actionKey: 'menu', testID: 'account-edit' }] })]);
  const button = page.document.querySelector('[data-testid="account-edit"]');
  button.getBoundingClientRect = () => ({ x: 909, y: 312, left: 909, top: 312, right: 947, bottom: 350, width: 38, height: 38 });
  button.click();
  assert.deepEqual(page.actions.at(-1).anchor.windowRect, { x: 916, y: 319, width: 24, height: 24 });
  page.close();
});
test('network checkbox state changes retain the official checked and indeterminate glyphs', () => {
  const row = identity('network', { presentation: 'networkSelector', height: 48, trailing: [{ kind: 'checkbox', state: 'unchecked' }] });
  const page = mount([row], { theme: { checkboxBackground: '#fdfdfd', checkboxBorder: '#abcdef', checkboxIcon: '#151515' } });
  const checkbox = page.document.querySelector('.ok-native-list-checkbox');
  assert.equal(checkbox.dataset.selector, 'networkSelector');
  assert.equal(checkbox.querySelectorAll('svg').length, 2);
  assert.equal(checkbox.querySelector('svg[data-state="checked"]').getAttribute('viewBox'), '0 0 16 16');
  assert.equal(checkbox.dataset.state, 'unchecked');
  page.engine.applySnapshot(snapshot([row], { generation: 2, selection: { mode: 'multiple', selectedKeys: ['network'] } }));
  assert.equal(page.document.querySelector('.ok-native-list-checkbox').dataset.state, 'checked');
  page.close();
});
test('reorderable wallet taps do not capture the pointer until a drag crosses the threshold', () => {
  const row = identity('wallet', { presentation: 'walletSidebar', height: 68, draggable: true });
  const page = mount([row], { capabilities: { reorderable: true } });
  const viewport = page.document.querySelector('.ok-native-list-viewport');
  const body = page.document.querySelector('.ok-native-list-wallet-row');
  const captures = [];
  viewport.setPointerCapture = id => captures.push(id);
  const pointer = (type, x, y) => {
    const event = new page.view.MouseEvent(type, { bubbles: true, clientX: x, clientY: y });
    Object.defineProperties(event, { pointerId: { value: 1 }, pointerType: { value: 'mouse' }, isPrimary: { value: true } });
    body.dispatchEvent(event);
  };
  pointer('pointerdown', 30, 40);
  assert.deepEqual(captures, []);
  pointer('pointerup', 30, 40);
  body.click();
  assert.equal(page.actions.at(-1).actionKey, 'press');
  pointer('pointerdown', 30, 40);
  pointer('pointermove', 50, 60);
  assert.deepEqual(captures, [1]);
  pointer('pointercancel', 50, 60);
  page.close();
});

test('accessory help uses a separate hover action without replacing the edit click', () => {
  const page = mount([identity('custom', { presentation: 'networkSelector', height: 48, trailing: [{ kind: 'icon', name: 'PencilOutline', actionKey: 'edit', hoverActionKey: 'edit.help', accessibilityLabel: 'Edit' }] })]);
  const button = page.document.querySelector('[data-native-list-action="edit"]');
  assert.equal(button.getAttribute('aria-label'), 'Edit');
  button.dispatchEvent(new page.view.MouseEvent('pointerover', { bubbles: true }));
  assert.equal(page.actions.at(-1).actionKey, 'edit.help');
  assert.equal(page.actions.at(-1).anchor.source, 'trailingAccessory');
  button.click();
  assert.equal(page.actions.at(-1).actionKey, 'edit');
  page.close();
});
test('wallet overlays preserve rectangular firmware badges and naturally sized numeric labels', () => {
  const wallet = identity('wallet', { presentation: 'walletSidebar', height: 68, leading: { kind: 'wallet', overlays: [{ position: 'topLeft', width: 18, height: 16, offsetX: 0, offsetY: 4, padding: 1, image: { uri: 'https://images.test/btc.png', width: 14, height: 14, contentFit: 'contain' } }, { position: 'bottomRight', text: '12', height: 16, offsetX: 1, offsetY: 2 }] } });
  const page = mount([wallet]);
  const [firmware, number] = page.document.querySelectorAll('.ok-native-list-visual-overlay');
  assert.equal(firmware.style.width, '18px');
  assert.equal(firmware.style.height, '16px');
  assert.equal(firmware.style.left, '0px');
  assert.equal(firmware.style.top, '-4px');
  assert.equal(number.style.width, 'auto');
  assert.equal(number.style.padding, '0px 2px');
  assert.equal(number.style.fontSize, '12px');
  assert.equal(number.style.fontWeight, '400');
  assert.equal(number.style.right, '-1px');
  page.close();
});
test('hidden-wallet lock fills its avatar while the add-hidden plus keeps its original size', () => {
  const lock = identity('group', { presentation: 'walletSidebar', height: 68, testID: 'wallet-group', leading: { kind: 'wallet', fallbackIcon: { name: 'LockSolid' } } });
  const plus = identity('plus', { presentation: 'walletSidebar', height: 68, leading: { kind: 'wallet', borderStyle: 'dashed', fallbackIcon: { name: 'PlusSmallOutline' } } });
  const page = mount([{ type: 'walletGroup', key: 'group', parent: lock, children: [plus] }]);
  const parent = page.document.querySelector('[data-testid="wallet-group"]');
  assert.equal(parent.querySelector('svg').style.width, '40px');
  const child = page.document.querySelector('[data-native-list-group-member-key="plus"]');
  assert.equal(child.querySelector('svg').getAttribute('width'), '24');
  assert.equal(child.querySelector('.ok-native-list-visual').style.borderWidth, '1px');
  page.close();
});
test('account add actions retain ListItem medium text while empty-search text remains regular', () => {
  const page = mount([{ type: 'action', key: 'add', actionKey: 'add', title: 'Add account', height: 48, presentation: 'accountSelector', icon: { kind: 'icon', name: 'PlusSmallOutline' } }, { type: 'action', key: 'empty', actionKey: 'empty', title: 'No account', height: 60, presentation: 'accountSelector', tone: 'primary' }]);
  assert.equal(page.document.querySelector('[data-native-list-row-key="add"] .ok-native-list-action-title').style.fontWeight, '500');
  assert.equal(page.document.querySelector('[data-native-list-row-key="empty"] .ok-native-list-action-title').style.fontWeight, '');
  page.close();
});
test('source-backed visuals default to no background or fallback and allow explicit opt-in', () => {
  const makeRow = loadingStrategy => identity('wallet', { revision: loadingStrategy ? 2 : 1, height: 68, presentation: 'walletSidebar', leading: { kind: 'wallet', shape: 'square', backgroundColor: '#00000000', image: { uri: 'file:///wallet.png', width: 40, height: 40, ...(loadingStrategy ? { loadingStrategy } : {}) }, fallbackText: 'W' } });
  const page = mount([makeRow()]);
  let frame = page.document.querySelector('.ok-native-list-visual');
  let image = page.document.querySelector('.ok-native-list-visual-main');
  assert.equal(frame.style.background, 'rgba(0, 0, 0, 0)');
  image.dispatchEvent(new page.view.Event('error'));
  assert.ok(page.document.querySelector('.ok-native-list-visual-main'));
  assert.equal(page.document.querySelector('.ok-native-list-visual-fallback'), null);

  page.engine.applySnapshot(snapshot([makeRow('static')], { generation: 2 }));
  frame = page.document.querySelector('.ok-native-list-visual');
  image = page.document.querySelector('.ok-native-list-visual-main');
  assert.equal(frame.style.background, 'var(--nl-strong)');
  image.dispatchEvent(new page.view.Event('load'));
  assert.equal(frame.style.background, 'rgba(0, 0, 0, 0)');
  page.close();
});
test('selector background pixels follow successful image sources and disappear after terminal failure or rebinding', () => {
  const row = uri => identity('image', { height: 48, presentation: 'networkSelector', leading: { kind: 'network', image: { uri, fallbackUri: 'https://images.test/raw.png', width: 32, height: 32, contentFit: 'cover', loadingStrategy: 'static' }, fallbackIcon: { name: 'GlobusOutline' } } });
  const page = mount([row('https://images.test/optimized.png')]);
  const image = page.document.querySelector('img');
  const paint = page.document.querySelector('.ok-native-list-selector-image-background');
  assert.equal(paint.style.backgroundImage, '');
  image.dispatchEvent(new page.view.Event('error'));
  assert.equal(image.src, 'https://images.test/raw.png');
  image.dispatchEvent(new page.view.Event('load'));
  assert.ok(paint.style.backgroundImage.includes('https://images.test/raw.png'));
  page.engine.applySnapshot(snapshot([row('https://images.test/new.png')], { generation: 2 }));
  assert.equal(paint.isConnected, false);
  assert.equal(page.document.querySelector('.ok-native-list-selector-image-background').style.backgroundImage, '');
  const nextImage = page.document.querySelector('img');
  nextImage.dispatchEvent(new page.view.Event('error'));
  nextImage.dispatchEvent(new page.view.Event('error'));
  assert.equal(page.document.querySelector('img'), null);
  assert.equal(page.document.querySelector('.ok-native-list-selector-image-background').style.backgroundImage, 'none');
  assert.ok(page.document.querySelector('.ok-native-list-visual-fallback svg'));
  page.close();
});
test('URI prefetch extends beyond mounted DOM and releases leases when the engine is destroyed', () => {
  const previousWorker = global.Worker;
  const workers = [];
  global.Worker = class {
    constructor() { this.messages = []; workers.push(this); }
    addEventListener() {}
    postMessage(message) { this.messages.push(message); }
  };
  const dom = new JSDOM('<!doctype html><div id="host"></div>', { pretendToBeVisual: true });
  const view = dom.window;
  global.Element = view.Element; global.HTMLElement = view.HTMLElement; global.Node = view.Node;
  view.HTMLElement.prototype.scrollTo = function ({ top = 0, left = 0 }) { this.scrollTop = top; this.scrollLeft = left; };
  const rows = Array.from({ length: 1000 }, (_, index) => identity(String(index), {
    presentation: 'accountSelector', height: 60,
    leading: { kind: 'account', image: { uri: 'onekey-avatar://blockie/v1/' + index, width: 32, height: 32 } },
  }));
  const engine = new NativeListWebEngine(view.document.getElementById('host'), snapshot(rows), {}, true);
  try {
    const mountedKeys = [...view.document.querySelectorAll('[data-native-list-row-key]')].map((element) => Number(element.dataset.nativeListRowKey));
    const messages = workers.flatMap((worker) => worker.messages);
    const acquire = messages.filter((message) => message.type === 'acquire');
    assert(acquire.length < 80);
    assert(mountedKeys.length < 40);
    assert(acquire.some((message) => Number(message.uri.split('/').at(-1)) > Math.max(...mountedKeys)));
    assert.equal(acquire[0].priority, 0);
    engine.destroy();
    const released = new Set(workers.flatMap((worker) => worker.messages).filter((message) => message.type === 'release').map((message) => message.id));
    assert(acquire.every((message) => released.has(message.id)));
  } finally { engine.destroy(); view.close(); global.Worker = previousWorker; }
});
