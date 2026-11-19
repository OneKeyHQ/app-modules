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
  await send('touchStart', [{ x, y }]);
  await send('touchMove', [{ x, y: y + 64 }]);
  assert.ok(y + 64 < box.y + box.height);
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
  test(`a ${position} swipe stays vertical after sideways drift`, async (t) => {
    const { context, page, card } = await showToast(t, {
      touch: true,
      position,
    });
    const send = await touchInput(context, page);
    const box = await card.boundingBox();
    const x = Math.round(box.x + box.width / 2);
    const y = Math.round(box.y + box.height / 2);
    await send('touchStart', [{ x, y }]);
    await send('touchMove', [{ x, y: y + 28 * direction }]);
    await page.waitForFunction(
      (amount) =>
        document
          .querySelector('[data-onekey-toast]')
          .style.getPropertyValue('--swipe-amount') === amount,
      `${28 * direction}px`,
      { timeout: 1000 }
    );
    await send('touchMove', [{ x: x + 41, y: y + 30 * direction }]);
    await page.waitForFunction(
      (amount) =>
        document
          .querySelector('[data-onekey-toast]')
          .style.getPropertyValue('--swipe-amount') === amount,
      `${30 * direction}px`,
      { timeout: 1000 }
    );
    await send('touchEnd');
    await card.waitFor({ state: 'detached', timeout: 1000 });
    assert.deepEqual(await callbacks(page), ['swipe']);
  });
}
