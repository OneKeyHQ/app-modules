const assert = require('node:assert/strict');
const path = require('node:path');
const { after, before, test } = require('node:test');
const { build } = require('esbuild');
const { chromium } = require('playwright-core');

let browser;
let bundle;

before(async () => {
  const result = await build({
    bundle: true,
    write: false,
    stdin: {
      resolveDir: path.join(__dirname, '../../src/toast'),
      contents: `
        import { OverlayToastStore } from './OverlayToastStore';
        import { createToastDomRenderer } from './toastDomRenderer.web';

        const store = new OverlayToastStore();
        const renderer = createToastDomRenderer((event) => store.handleEvent(event));
        store.attachRenderer();
        store.subscribe(() => {
          const { items, config } = store.getSnapshot();
          renderer.sync(items, config);
        });
        const text = (value) => ({
          paragraphs: [[{ text: value }]], color: '#111', fontSize: 14,
          lineHeight: 20, fontWeight: '600',
        });
        window.callbacks = [];
        const statusSpec = (status, colors = {}) => ({
          id: 'status', layout: 'message', duration: Infinity,
          title: text(status), position: 'top-center',
          icon: { kind: 'icon', name: 'Fallback', size: 20, color: '#111' },
          statusIcon: {
            kind: 'status', status, size: 36, color: status === 'loading' ? '#655' : '#fff',
            backgroundColor: status === 'loading' ? '#bbb' : '#2a5',
            ...colors,
          },
        });
        window.showStatus = (status, extra = {}) =>
          store.show({ ...statusSpec(status), ...extra }, {
            onAction: (id) => window.callbacks.push(id),
          });
        window.redrawStatus = (status, colors) => store.update(statusSpec(status, colors));
        window.dismissStatus = () => store.dismiss('status');
        window.configure = (config) => store.configure(config);
        window.showToast = (duration, position) => {
          store.show({
            id: 'notification', layout: 'notification',
            title: text('Notification title'), message: text('Notification body'),
            position, duration, pressable: true,
            badgeSize: 40, badgeColor: '#eee',
            icon: { kind: 'icon', name: 'BellOutline', size: 20, color: '#111' },
          }, {
            onPress: () => window.callbacks.push('press'),
            onClose: (reason) => window.callbacks.push(reason),
          });
        };
      `,
    },
  });
  bundle = result.outputFiles[0].text;
  browser = await chromium.launch({
    executablePath: process.env.CHROMIUM_EXECUTABLE_PATH,
  });
});

after(async () => browser?.close());

async function showToast(
  t,
  { touch = false, duration = 3000, position = 'top-center' } = {}
) {
  const context = await browser.newContext({
    viewport:
      position === 'bottom-right'
        ? { width: 1280, height: 800 }
        : { width: 390, height: 844 },
    hasTouch: touch,
    isMobile: touch,
    reducedMotion: 'reduce',
  });
  t.after(() => context.close());
  const page = await context.newPage();
  await page.setContent(
    '<!doctype html><meta name="viewport" content="width=device-width, initial-scale=1"><main>Page content</main>'
  );
  await page.addScriptTag({ content: bundle });
  await page.evaluate(
    ({ duration, position }) => window.showToast(duration, position),
    { duration, position }
  );
  return { context, page, card: page.locator('[data-onekey-toast]') };
}

function callbacks(page) {
  return page.evaluate(() => window.callbacks);
}

async function statusPage(t, reducedMotion = 'no-preference') {
  const context = await browser.newContext({ reducedMotion });
  t.after(() => context.close());
  const page = await context.newPage();
  await page.setContent(
    '<!doctype html><meta name="viewport" content="width=device-width, initial-scale=1"><main>Page content</main>'
  );
  await page.addScriptTag({ content: bundle });
  return page;
}

test('status motion stages the tile, pop and directed strokes independently', async (t) => {
  const page = await statusPage(t);
  const sample = await page.evaluate(() => {
    const glyph = '[data-okt-status-glyph]';
    const tile = '[data-okt-status-tile]';
    const stroke = '[data-okt-status-stroke]';
    const style = (selector) =>
      getComputedStyle(document.querySelector(selector));
    const pointAt = (path, length) => {
      const { x, y } = path.getPointAtLength(length);
      return { x, y };
    };
    const sampleAt = (ms) => {
      document.getAnimations().forEach((animation) => {
        animation.pause();
        animation.currentTime = ms;
      });
      return {
        tileOpacity: Number(style(tile).opacity),
        glyphOpacity: Number(style(glyph).opacity),
        scale: new DOMMatrix(style(glyph).transform).a,
        strokes: Array.from(document.querySelectorAll(stroke), (path) => ({
          offset: parseFloat(getComputedStyle(path).strokeDashoffset),
          length: path.getTotalLength(),
          start: pointAt(path, 0),
          end: pointAt(path, path.getTotalLength()),
        })),
      };
    };
    window.showStatus('success');
    const entrance = sampleAt(90);
    const tileOnly = sampleAt(135);
    const glyphPop = sampleAt(195);
    const halfDraw = sampleAt(265);
    const peak = sampleAt(293);
    const result = sampleAt(410);
    const tileSize = document.querySelector(tile).offsetWidth;
    const glyphSize = Number(style(`${glyph} svg`).width.replace('px', ''));
    const tileDurations = style(tile).animationDuration;
    const glyphDurations = style(glyph).animationDuration;
    const drawEase = style(stroke).animationTimingFunction;
    window.dismissStatus();
    window.showStatus('error');
    return {
      entrance,
      tileOnly,
      glyphPop,
      halfDraw,
      peak,
      result,
      tileSize,
      glyphSize,
      tileDurations,
      glyphDurations,
      drawEase,
      error: sampleAt(265),
    };
  });
  assert.equal(sample.entrance.tileOpacity, 0);
  assert.ok(sample.tileOnly.tileOpacity > 0);
  assert.equal(sample.tileOnly.glyphOpacity, 0);
  assert.ok(sample.glyphPop.glyphOpacity > 0 && sample.glyphPop.scale < 1);
  assert.ok(Math.abs(sample.peak.scale - 1.0385) < 0.001);
  assert.equal(sample.glyphPop.strokes[0].offset, 1);
  assert.ok(
    sample.halfDraw.strokes[0].offset > 0 &&
      sample.halfDraw.strokes[0].offset < 1
  );
  assert.equal(sample.result.strokes[0].offset, 0);
  assert.equal(sample.tileSize, 36);
  assert.equal(sample.glyphSize, 18);
  assert.equal(sample.tileDurations, '0.376s, 0.376s');
  assert.equal(sample.glyphDurations, '0.342s, 0.342s');
  assert.equal(sample.drawEase, 'cubic-bezier(0, 0, 0.58, 1)');
  assert.equal(sample.result.strokes.length, 1);
  assert.equal(sample.result.strokes[0].start.x, 6);
  assert.equal(sample.result.strokes[0].end.x, 18);
  assert.equal(sample.error.strokes.length, 2);
  assert.deepEqual(
    sample.error.strokes.map(({ start, end }) => [start.y, end.y]),
    [
      [7.5, 16.5],
      [7.5, 16.5],
    ]
  );
  assert.equal(sample.error.strokes[0].offset, sample.error.strokes[1].offset);
});

test('loading settles within the same card and tile and keeps motion on redraw', async (t) => {
  const page = await statusPage(t);
  const sample = await page.evaluate(() => {
    const tileSelector = '[data-okt-status-tile]';
    const glyph = '[data-okt-status-glyph]:not([data-okt-status-outgoing])';
    const tile = () => document.querySelector(tileSelector);
    const seek = (ms) => {
      document
        .querySelector('[data-onekey-toast]')
        .getAnimations({ subtree: true })
        .forEach((animation) => {
          animation.pause();
          animation.currentTime = ms;
        });
    };
    window.showStatus('loading');
    const card = document.querySelector('[data-onekey-toast]');
    const originalTile = tile();
    seek(205);
    const loading = originalTile.querySelector(glyph);
    const expectedOpacity = getComputedStyle(loading).opacity;
    const expectedTransform = getComputedStyle(loading).transform;
    const spinDuration = getComputedStyle(
      loading.querySelector('svg')
    ).animationDuration;
    window.showStatus('success');
    const outgoing = tile().querySelector('[data-okt-status-outgoing]');
    const startingOpacity = outgoing?.style.getPropertyValue(
      '--okt-glyph-from-opacity'
    );
    const startingTransform = outgoing?.style.getPropertyValue(
      '--okt-glyph-from-transform'
    );
    seek(45);
    const incoming = tile().querySelector(glyph);
    const incomingBeforeDeparture = Number(getComputedStyle(incoming).opacity);
    const fadingOpacity = Number(getComputedStyle(outgoing).opacity);
    const tint = getComputedStyle(tile()).backgroundColor;
    const glyphColor = getComputedStyle(outgoing).color;
    const tintEase = getComputedStyle(tile()).transitionTimingFunction;
    const leaveEase = getComputedStyle(outgoing).animationTimingFunction;
    const bumpEase = getComputedStyle(tile()).animationTimingFunction;
    const pop = incoming.getAnimations()[0];
    window.redrawStatus('success', { color: '#ffe' });
    const redrawnColor = tile().style.color;
    const keptPop = pop === incoming.getAnimations()[0];
    seek(185);
    const drawing = parseFloat(
      getComputedStyle(incoming.querySelector('path')).strokeDashoffset
    );
    const sameCard = document.querySelector('[data-onekey-toast]') === card;
    const sameTile = tile() === originalTile;
    window.dismissStatus();
    return {
      sameCard,
      sameTile,
      expectedOpacity,
      startingOpacity,
      expectedTransform,
      startingTransform,
      incomingBeforeDeparture,
      fadingOpacity,
      tint,
      glyphColor,
      spinDuration,
      tintEase,
      leaveEase,
      bumpEase,
      keptPop,
      redrawnColor,
      drawing,
      stopped:
        !tile().hasAttribute('data-okt-status-motion') &&
        !tile().contains(outgoing),
    };
  });
  assert.ok(
    sample.sameCard && sample.sameTile && sample.keptPop && sample.stopped
  );
  assert.equal(sample.redrawnColor, 'rgb(255, 255, 238)');
  assert.equal(sample.startingOpacity, sample.expectedOpacity);
  assert.equal(sample.startingTransform, sample.expectedTransform);
  assert.equal(sample.incomingBeforeDeparture, 0);
  assert.ok(
    sample.fadingOpacity > 0 &&
      sample.fadingOpacity < Number(sample.expectedOpacity)
  );
  assert.notEqual(sample.tint, 'rgb(187, 187, 187)');
  assert.notEqual(sample.tint, 'rgb(34, 170, 85)');
  assert.notEqual(sample.glyphColor, 'rgb(102, 85, 85)');
  assert.notEqual(sample.glyphColor, 'rgb(255, 255, 255)');
  assert.equal(sample.spinDuration, '0.85s');
  assert.equal(
    sample.tintEase,
    'cubic-bezier(0.4, 0, 0.2, 1), cubic-bezier(0.4, 0, 0.2, 1)'
  );
  assert.equal(sample.leaveEase, 'cubic-bezier(0.42, 0, 1, 1)');
  assert.equal(sample.bumpEase, 'cubic-bezier(0, 0, 0.58, 1)');
  assert.ok(sample.drawing > 0 && sample.drawing < 1);
});

test('immediate completion skips the unshown loading glyph; reduced motion remains static', async (t) => {
  const page = await statusPage(t);
  const immediate = await page.evaluate(() => {
    window.showStatus('loading');
    const tile = document.querySelector('[data-okt-status-tile]');
    window.showStatus('error');
    return [
      tile === document.querySelector('[data-okt-status-tile]'),
      tile.getAttribute('data-okt-status-motion'),
      !!tile.querySelector('[data-okt-status-outgoing]'),
    ];
  });
  assert.deepEqual(immediate, [true, 'immediate', false]);
  const reduced = await statusPage(t, 'reduce');
  const staticGlyphs = await reduced.evaluate(() => {
    window.showStatus('loading');
    const tile = document.querySelector('[data-okt-status-tile]');
    const initial = tile.getAnimations({ subtree: true }).length;
    window.showStatus('error');
    const strokes = Array.from(
      tile.querySelectorAll('path'),
      (path) => getComputedStyle(path).strokeDasharray
    );
    return [initial, tile.getAnimations({ subtree: true }).length, strokes];
  });
  assert.deepEqual(staticGlyphs, [0, 0, ['none', 'none']]);
});

test('warning and info keep their dots static while the stems draw downward', async (t) => {
  const page = await statusPage(t, 'reduce');
  const sample = await page.evaluate(() => {
    window.configure({ respectReducedMotion: false });
    return ['warning', 'info'].map((status) => {
      window.dismissStatus();
      window.showStatus(status);
      const tile = document.querySelector('[data-okt-status-tile]');
      const stroke = tile.querySelector('[data-okt-status-stroke]');
      const dot = tile.querySelector('[data-okt-status-dot]');
      const initial = stroke.getPointAtLength(0);
      const final = stroke.getPointAtLength(stroke.getTotalLength());
      const dotY = Number(dot.getAttribute('cy'));
      return {
        stem: [initial.y, final.y],
        dotY,
        dotAnimations: dot.getAnimations().length,
        strokeAnimations: stroke.getAnimations().length,
      };
    });
  });
  assert.deepEqual(sample[0], {
    stem: [6, 13],
    dotY: 18,
    dotAnimations: 0,
    strokeAnimations: 1,
  });
  assert.deepEqual(sample[1], {
    stem: [11, 18],
    dotY: 6.25,
    dotAnimations: 0,
    strokeAnimations: 1,
  });
});

async function touchInput(context, page) {
  const session = await context.newCDPSession(page);
  return (type, touchPoints = []) =>
    session.send('Input.dispatchTouchEvent', { type, touchPoints });
}

test('browser clicks and taps activate the pressable notification row once', async (t) => {
  const { page } = await showToast(t, { touch: true });
  const title = page.getByText('Notification title');
  await title.click();
  assert.deepEqual(await callbacks(page), ['press']);
  await title.tap();
  assert.deepEqual(await callbacks(page), ['press', 'press']);
});

test('a touch drag ending within the card resumes its close timer', async (t) => {
  const { context, page, card } = await showToast(t, {
    touch: true,
    duration: 850,
  });
  const send = await touchInput(context, page);
  const box = await card.boundingBox();
  const x = Math.round(box.x + box.width / 2);
  const y = Math.round(box.y + 8);
  const drag = Math.max(24, Math.round(box.height - 16));
  await send('touchStart', [{ x, y }]);
  await send('touchMove', [{ x, y: y + drag }]);
  assert.ok(y + drag < box.y + box.height);
  await send('touchEnd');
  await card.waitFor({ state: 'detached', timeout: 2000 });
  assert.deepEqual(await callbacks(page), ['timeout']);
});

test('an outside mouse click cannot end a held touch press', async (t) => {
  const duration = 850;
  const { context, page, card } = await showToast(t, { touch: true, duration });
  const send = await touchInput(context, page);
  const box = await card.boundingBox();
  const x = Math.round(box.x + box.width / 2);
  const y = Math.round(box.y + box.height / 2);
  await send('touchStart', [{ x, y }]);
  await page.mouse.click(20, 500);
  await page.waitForTimeout(duration + 200);
  assert.equal(await card.getAttribute('data-removed'), 'false');
  assert.deepEqual(await callbacks(page), []);
  await send('touchCancel');
  await card.waitFor({ state: 'detached', timeout: 2000 });
  assert.deepEqual(await callbacks(page), ['timeout']);
});

for (const [position, direction] of [
  ['top-center', -1],
  ['bottom-right', 1],
]) {
  test(`a ${position} swipe stays horizontal after vertical drift`, async (t) => {
    const { context, page, card } = await showToast(t, {
      touch: true,
      position,
    });
    const send = await touchInput(context, page);
    const box = await card.boundingBox();
    const x = Math.round(box.x + box.width / 2);
    const y = Math.round(box.y + box.height / 2);
    await send('touchStart', [{ x, y }]);
    await send('touchMove', [{ x: x + 48 * direction, y }]);
    await page.waitForFunction(
      (amount) =>
        document
          .querySelector('[data-onekey-toast]')
          .style.getPropertyValue('--swipe-amount') === amount,
      `${48 * direction}px`,
      { timeout: 1000 }
    );
    await send('touchMove', [{ x: x + 72 * direction, y: y + 41 }]);
    await page.waitForFunction(
      (amount) =>
        document
          .querySelector('[data-onekey-toast]')
          .style.getPropertyValue('--swipe-amount') === amount,
      `${72 * direction}px`,
      { timeout: 1000 }
    );
    await send('touchEnd');
    await card.waitFor({ state: 'detached', timeout: 1000 });
    assert.deepEqual(await callbacks(page), ['swipe']);
  });
}

test('card adopts concentric squircle chrome, a hover close button and an inline right action', async (t) => {
  const page = await statusPage(t);
  await page.evaluate(() => {
    window.configure({
      theme: 'dark',
      cardColor: '#1b1b1b',
      cardBorderColor: 'rgba(255, 255, 255, 0.1)',
    });
    window.showStatus('success', {
      actions: [
        {
          id: 'view',
          label: 'View',
          color: '#fff',
          backgroundColor: '#2c2c2e',
          fontSize: 13,
          lineHeight: 18,
          fontWeight: '600',
          paddingHorizontal: 14,
          paddingVertical: 7,
          borderRadius: 11,
        },
      ],
    });
  });

  const card = page.locator('[data-onekey-toast]');
  const closeButton = page.locator('[data-close-button]');
  const metrics = await page.evaluate(() => {
    const surface = document.querySelector('[data-okt-surface]');
    const tile = document.querySelector('[data-okt-status-tile]');
    const action = document.querySelector('[data-okt-inline-action]');
    const close = document.querySelector('[data-close-button]');
    const surfaceStyle = getComputedStyle(surface);
    const actionBox = action.getBoundingClientRect();
    const tileBox = tile.getBoundingClientRect();
    return {
      padding: surfaceStyle.padding,
      radius: surfaceStyle.borderRadius,
      boxShadow: surfaceStyle.boxShadow,
      tileClip: tile.style.clipPath,
      bg: surfaceStyle.backgroundColor,
      closeOpacityIdle: Number(getComputedStyle(close).opacity),
      actionHeight: action.offsetHeight,
      actionRadius: getComputedStyle(action).borderRadius,
      alignedVertically:
        Math.abs(
          tileBox.top +
            tileBox.height / 2 -
            (actionBox.top + actionBox.height / 2)
        ) <= 1,
    };
  });

  assert.equal(metrics.padding, '10px');
  assert.equal(metrics.radius, '21px');
  assert.match(metrics.boxShadow, /0px 0px 0px 1px/);
  assert.match(metrics.tileClip, /^path\(["']M/);
  assert.equal(metrics.bg, 'rgb(27, 27, 27)');
  assert.equal(metrics.closeOpacityIdle, 0);
  assert.equal(metrics.actionHeight, 36);
  assert.equal(metrics.actionRadius, '11px');
  assert.equal(metrics.alignedVertically, true);

  await card.hover();
  await page.waitForFunction(
    () =>
      Number(
        getComputedStyle(document.querySelector('[data-close-button]')).opacity
      ) > 0.9,
    null,
    { timeout: 1000 }
  );
  assert.ok(await closeButton.isVisible());
});

test('a wide icon action falls back on a narrow viewport and returns inline on resize', async (t) => {
  const page = await statusPage(t, 'reduce');
  await page.setViewportSize({ width: 320, height: 800 });
  await page.evaluate(() =>
    window.showStatus('success', {
      actions: [
        {
          id: 'view',
          label: 'MMMMMMMM',
          borderRadius: 28,
          color: '#111',
          backgroundColor: '#eee',
          fontSize: 14,
          lineHeight: 20,
          fontWeight: '600',
          paddingHorizontal: 10,
          paddingVertical: 4,
          icon: { kind: 'icon', name: 'View', size: 20, color: '#111' },
          iconGap: 4,
        },
      ],
    })
  );
  const action = page.getByRole('button', { name: 'MMMMMMMM' });
  assert.equal(await action.getAttribute('data-okt-inline-action'), null);
  assert.equal(
    await action.evaluate((node) => getComputedStyle(node).borderRadius),
    '28px'
  );
  assert.ok(
    await page
      .locator('[data-okt-message-col]')
      .evaluate((node) => node.clientWidth >= 96)
  );
  await page.setViewportSize({ width: 820, height: 800 });
  await page.waitForSelector('[data-okt-inline-action]', { timeout: 2000 });
  assert.equal(
    await action.evaluate((node) => getComputedStyle(node).borderRadius),
    '11px'
  );
});

test('touch browsers keep close available to the keyboard and the idle corner passes through touch', async (t) => {
  for (const isMobile of [true, false]) {
    const context = await browser.newContext({
      hasTouch: true,
      isMobile,
      reducedMotion: 'reduce',
    });
    t.after(() => context.close());
    const page = await context.newPage();
    await page.setContent(
      '<!doctype html><meta name="viewport" content="width=device-width, initial-scale=1">'
    );
    await page.addScriptTag({ content: bundle });
    await page.evaluate(() => window.showStatus('success'));
    const close = page.getByRole('button', { name: 'Close toast' });
    const box = await close.boundingBox();
    assert.ok(box);
    assert.equal(
      await close.evaluate((node) => getComputedStyle(node).pointerEvents),
      'none'
    );
    await page.touchscreen.tap(box.x + 10, box.y + 10);
    assert.equal(
      await page.locator('[data-onekey-toast]').getAttribute('data-removed'),
      'false'
    );
    await page.locator('[data-onekey-toast]').focus();
    await page.keyboard.press('Tab');
    assert.ok(await close.evaluate((node) => node === document.activeElement));
    assert.equal(
      await close.evaluate((node) => getComputedStyle(node).opacity),
      '1'
    );
    await page.keyboard.press('Enter');
    await close.waitFor({ state: 'detached' });
  }
});

test('restoring motion resumes the ring without replaying a mounted status animation', async (t) => {
  const page = await statusPage(t);
  await page.evaluate(() => window.showStatus('loading'));
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.waitForFunction(
    () => !document.querySelector('[data-okt-status-motion]')
  );
  const ring = page.locator('[data-okt-status-ring]');
  assert.equal(
    await ring.evaluate((node) => getComputedStyle(node).animationName),
    'none'
  );
  await page.emulateMedia({ reducedMotion: 'no-preference' });
  await page.waitForFunction(
    () =>
      document.querySelector('[data-okt-status-ring]').getAnimations()
        .length === 1
  );
  assert.equal(await page.locator('[data-okt-status-motion]').count(), 0);
  await page.evaluate(() => window.showStatus('success'));
  await page.waitForSelector('[data-okt-status-outgoing]', {
    state: 'attached',
  });
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.waitForSelector('[data-okt-status-outgoing]', {
    state: 'detached',
  });
  await page.emulateMedia({ reducedMotion: 'no-preference' });
  assert.equal(await page.locator('[data-okt-status-motion]').count(), 0);
});

test('a compact action stays reachable when lowering it would exceed the content height cap', async (t) => {
  const page = await statusPage(t, 'reduce');
  await page.setViewportSize({ width: 232, height: 800 });
  await page.evaluate(() =>
    window.showStatus('success', {
      title: {
        paragraphs: [[{ text: 'Done' }]],
        color: '#111',
        fontSize: 16,
        lineHeight: 24,
        fontWeight: '600',
      },
      actions: [
        {
          id: 'view',
          label: 'View',
          borderRadius: 28,
          color: '#111',
          backgroundColor: '#eee',
          fontSize: 14,
          lineHeight: 20,
          fontWeight: '600',
          paddingHorizontal: 10,
          paddingVertical: 4,
        },
      ],
    })
  );
  const action = page.getByRole('button', { name: 'View', exact: true });
  assert.equal(await action.getAttribute('data-okt-inline-action'), null);
  await action.focus();

  await page.evaluate(() => window.configure({ webContentMaxHeight: 36 }));
  assert.equal(await action.getAttribute('data-okt-inline-action'), '');
  const geometry = await action.evaluate((node) => {
    const root = node.closest('[data-okt-root]').getBoundingClientRect();
    const button = node.getBoundingClientRect();
    const column = node.parentElement.querySelector('[data-okt-message-col]');
    return {
      cap: root.height,
      fits: button.top >= root.top && button.bottom <= root.bottom,
      textWidth: column.clientWidth,
      focused: node === document.activeElement,
    };
  });
  assert.equal(geometry.cap, 36);
  assert.ok(geometry.fits && geometry.focused);
  assert.ok(geometry.textWidth > 0 && geometry.textWidth < 96);

  await page.evaluate(() => window.configure({ webContentMaxHeight: 160 }));
  assert.equal(await action.getAttribute('data-okt-inline-action'), null);
});

test('toasts beyond the display limit regain keyboard access only when visible', async (t) => {
  const context = await browser.newContext({
    hasTouch: true,
    isMobile: true,
    reducedMotion: 'reduce',
  });
  t.after(() => context.close());
  const page = await context.newPage();
  await page.setContent(
    '<!doctype html><meta name="viewport" content="width=device-width, initial-scale=1">'
  );
  await page.addScriptTag({ content: bundle });
  await page.evaluate(() => {
    for (let index = 0; index < 4; index += 1) {
      window.showStatus('success', {
        id: `status-${index}`,
        testID: `status-${index}`,
      });
    }
  });

  const oldest = page.getByTestId('status-0');
  assert.equal(await oldest.getAttribute('data-visible'), 'false');
  const cdp = await context.newCDPSession(page);
  const { nodes } = await cdp.send('Accessibility.getFullAXTree');
  const accessibleCloseButtons = nodes.filter(
    (node) =>
      !node.ignored &&
      node.role?.value === 'button' &&
      node.name?.value === 'Close toast'
  );
  assert.equal(accessibleCloseButtons.length, 3);
  await oldest.locator('[data-close-button]').focus();
  assert.equal(
    await oldest.evaluate((node) => node.contains(document.activeElement)),
    false
  );

  const frontClose = page
    .getByTestId('status-3')
    .getByRole('button', { name: 'Close toast' });
  await frontClose.press('Enter');
  await frontClose.waitFor({ state: 'detached', timeout: 2000 });
  const nowVisibleClose = oldest.getByRole('button', { name: 'Close toast' });
  assert.equal(await oldest.getAttribute('data-visible'), 'true');
  await nowVisibleClose.focus();
  assert.equal(
    await nowVisibleClose.evaluate((node) => node === document.activeElement),
    true
  );
});

test('collapsed back toast actions regain keyboard access after expansion or promotion', async (t) => {
  const page = await statusPage(t, 'reduce');
  await page.evaluate(() => {
    window.showStatus('success', {
      id: 'back',
      testID: 'back',
      actions: [
        {
          id: 'view',
          label: 'View',
          testID: 'view',
          borderRadius: 11,
          color: '#111',
          backgroundColor: '#eee',
          fontSize: 14,
          lineHeight: 20,
          fontWeight: '600',
          paddingHorizontal: 14,
          paddingVertical: 4,
        },
      ],
    });
    window.showStatus('success', { id: 'front', testID: 'front' });
  });

  const back = page.getByTestId('back');
  const action = back.getByTestId('view');
  const actionHasFocus = () =>
    action.evaluate((node) => node === document.activeElement);
  const backClose = back.getByRole('button', { name: 'Close toast' });
  await backClose.focus();
  assert.ok(
    await backClose.evaluate((node) => node === document.activeElement)
  );
  await action.focus();
  assert.equal(await actionHasFocus(), false);

  await page.keyboard.press('Alt+t');
  assert.equal(await back.getAttribute('data-expanded'), 'true');
  await action.focus();
  assert.ok(await actionHasFocus());
  await page.keyboard.press('Enter');
  assert.deepEqual(await callbacks(page), ['view']);

  await page.keyboard.press('Escape');
  assert.equal(await back.getAttribute('data-expanded'), 'false');
  assert.ok(await back.evaluate((node) => node === document.activeElement));
  await action.focus();
  assert.equal(await actionHasFocus(), false);
  await page.keyboard.press('Enter');
  assert.deepEqual(await callbacks(page), ['view']);

  const frontClose = page
    .getByTestId('front')
    .getByRole('button', { name: 'Close toast' });
  await frontClose.press('Enter');
  await frontClose.waitFor({ state: 'detached', timeout: 2000 });
  assert.equal(await back.getAttribute('data-front'), 'true');
  await action.focus();
  assert.ok(await actionHasFocus());
  await page.keyboard.press('Enter');
  assert.deepEqual(await callbacks(page), ['view', 'view']);
});

test('collapse focus handoff preserves layout when a focus listener shows another toast', async (t) => {
  const page = await statusPage(t, 'reduce');
  await page.evaluate(() => {
    window.showStatus('success', {
      id: 'back',
      testID: 'back',
      actions: [
        {
          id: 'view',
          label: 'View',
          testID: 'view',
          borderRadius: 11,
          color: '#111',
          backgroundColor: '#eee',
          fontSize: 14,
          lineHeight: 20,
          fontWeight: '600',
          paddingHorizontal: 14,
          paddingVertical: 4,
        },
      ],
    });
    window.showStatus('success', { id: 'front', testID: 'front' });

    function showOnShellFocus(event) {
      if (event.target !== document.querySelector('[data-testid="back"]'))
        return;
      document.removeEventListener('focusin', showOnShellFocus);
      window.showStatus('success', { id: 'new', testID: 'new' });
    }
    document.addEventListener('focusin', showOnShellFocus);
  });

  await page.keyboard.press('Alt+t');
  const back = page.getByTestId('back');
  const action = back.getByTestId('view');
  await action.focus();
  await page.keyboard.press('Escape');

  assert.ok(await back.evaluate((node) => node === document.activeElement));
  assert.deepEqual(
    await page
      .locator('[data-onekey-toast]')
      .evaluateAll((cards) =>
        cards.map((card) => [
          card.dataset.testid,
          card.dataset.index,
          card.style.getPropertyValue('--offset'),
        ])
      ),
    [
      ['new', '0', '0px'],
      ['front', '1', '66px'],
      ['back', '2', '132px'],
    ]
  );

  await page.keyboard.press('Alt+t');
  await action.focus();
  await page.evaluate(() => {
    const outside = document.createElement('button');
    outside.id = 'outside-focus';
    document.body.append(outside);
    document.activeElement.dispatchEvent(
      new KeyboardEvent('keydown', {
        code: 'Escape',
        key: 'Escape',
        bubbles: true,
      })
    );
    outside.focus();
  });
  assert.equal(await back.getAttribute('data-expanded'), 'false');
  assert.equal(
    await page.evaluate(() => document.activeElement.id),
    'outside-focus'
  );
});

test('narrow toast lanes and all three cards share the viewport center', async (t) => {
  const page = await statusPage(t, 'reduce');
  for (const width of [320, 390, 600]) {
    await page.setViewportSize({ width, height: 800 });
    for (const direction of ['ltr', 'rtl']) {
      await page.evaluate((dir) => {
        document.documentElement.dir = dir;
      }, direction);
      for (const position of ['top-left', 'top-center', 'bottom-right']) {
        await page.evaluate((anchor) => {
          for (const id of ['back', 'middle', 'front']) {
            window.showStatus('success', { id, position: anchor });
          }
        }, position);

        const lane = page.locator('[data-onekey-toast-toaster]');
        const laneBox = await lane.boundingBox();
        const label = `${width}px ${direction} ${position}`;
        const expectedWidth = Math.min(356, width - 32);
        assert.equal(laneBox.x, (width - expectedWidth) / 2, label);
        assert.equal(laneBox.width, expectedWidth, label);

        const cards = lane.locator('[data-onekey-toast]');
        const assertCardCenters = async () => {
          const centers = await cards.evaluateAll((nodes) =>
            nodes.map((node) => {
              const { left, right } = node.getBoundingClientRect();
              return (left + right) / 2;
            })
          );
          assert.equal(centers.length, 3, label);
          for (const center of centers) {
            assert.ok(Math.abs(center - width / 2) < 0.1, label);
          }
        };
        await assertCardCenters();
        await page.keyboard.press('Alt+t');
        assert.equal(await cards.first().getAttribute('data-expanded'), 'true');
        await assertCardCenters();
        await page.keyboard.press('Escape');
        assert.equal(
          await cards.first().getAttribute('data-expanded'),
          'false'
        );
      }
    }
  }

  await page.setViewportSize({ width: 601, height: 800 });
  const desktopLane = await page
    .locator('[data-onekey-toast-toaster]')
    .boundingBox();
  assert.equal(desktopLane.width, 356);
  assert.equal(601 - desktopLane.x - desktopLane.width, 32);
});

test('a separating fold keeps a toast stack centered inside its selected pane', async (t) => {
  const page = await statusPage(t, 'reduce');
  await page.setViewportSize({ width: 740, height: 800 });
  const client = await page.context().newCDPSession(page);
  const emulateFold = (displayFeature) =>
    client.send('Emulation.setDeviceMetricsOverride', {
      width: 740,
      height: 800,
      screenWidth: 740,
      screenHeight: 800,
      deviceScaleFactor: 1,
      mobile: true,
      displayFeature,
    });
  await emulateFold({ orientation: 'vertical', offset: 340, maskLength: 30 });

  const lane = page.locator('[data-onekey-toast-toaster]');
  const showStack = (position, dir = 'ltr') =>
    page.evaluate(
      ({ position, dir }) => {
        document.documentElement.dir = dir;
        for (const id of ['back', 'middle', 'front']) {
          window.showStatus('success', { id, position });
        }
      },
      { position, dir }
    );

  await page.waitForFunction(
    () => matchMedia('(horizontal-viewport-segments: 2)').matches
  );
  for (const [position, dir, left, width] of [
    ['top-center', 'ltr', 16, 308],
    ['top-center', 'rtl', 386, 338],
    ['top-left', 'rtl', 16, 308],
    ['bottom-right', 'ltr', 386, 338],
  ]) {
    await showStack(position, dir);
    const bounds = await lane.boundingBox();
    assert.equal(bounds.x, left);
    assert.equal(bounds.width, width);
    await page.keyboard.press('Alt+t');
    const boundsByCard = await lane
      .locator('[data-onekey-toast]')
      .evaluateAll((nodes) =>
        nodes.map((node) => {
          const bounds = node.getBoundingClientRect();
          return [bounds.x, bounds.width];
        })
      );
    assert.deepEqual(boundsByCard, Array(3).fill([left, width]));
  }

  await emulateFold({ orientation: 'horizontal', offset: 370, maskLength: 30 });
  await page.waitForFunction(
    () => matchMedia('(vertical-viewport-segments: 2)').matches
  );
  await showStack('bottom-center');
  assert.equal((await lane.boundingBox()).y, 768);

  await emulateFold();
  await page.waitForFunction(
    () => !matchMedia('(vertical-viewport-segments: 2)').matches
  );
  assert.equal((await lane.boundingBox()).x, 192);
  assert.equal((await lane.boundingBox()).width, 356);
  assert.deepEqual(await callbacks(page), []);
});
