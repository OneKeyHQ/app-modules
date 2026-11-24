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
    alias: { 'react-native': 'react-native-web' },
    stdin: {
      resolveDir: path.join(__dirname, '../../src'),
      contents: `
        import { createElement, Fragment } from 'react';
        import { flushSync } from 'react-dom';
        import { createRoot } from 'react-dom/client';
        import { OverlayPageHost } from './OverlayPageHost.web';
        import { OverlayView } from './OverlayView.web';
        import { overlayStore } from './OverlayStore';

        const root = createRoot(document.getElementById('root'));
        window.focusHost = (key) => overlayStore.setFocusedPageHost(key);
        window.renderPage = ({ withHost = true, visible = true } = {}) =>
          flushSync(() => root.render(
            createElement(Fragment, null,
              withHost
                ? createElement(OverlayPageHost, { hostKey: 'main', key: 'host' })
                : createElement('button', {
                    key: 'replacement',
                    onClick: (event) => { event.currentTarget.textContent = 'Clicked'; },
                  }, 'Replacement'),
              createElement(OverlayView, {
                key: 'overlay', visible, scope: 'page', hostKey: 'main',
                ownerKey: 'screen',
              }, 'Loading'),
              createElement('button', { key: 'foreground' }, 'Foreground'),
              createElement('input', { key: 'first', 'aria-label': 'First input' }),
              createElement('input', { key: 'second', 'aria-label': 'Second input' })
            )
          ));
        window.getEntryCount = () => overlayStore.getSnapshot().entries.length;
        window.resolveBack = () => overlayStore.resolveBack().kind;
        const timing = (durationMs) => ({
          type: 'timing', timing: { durationMs, easing: [0, 0, 1, 1] },
        });
        const stackAnimation = {
          enter: { type: 'none' },
          exit: { type: 'fade', motion: timing(2000) },
          backdrop: { motion: timing(10) },
        };
        window.renderStack = ({ lower = false, upper = false, lowerScope = 'global' } = {}) =>
          flushSync(() => root.render(
            createElement(Fragment, null,
              createElement('button', null, 'Stack opener'),
              createElement(OverlayPageHost, { hostKey: 'main' }),
              createElement(OverlayView, {
                visible: lower, scope: lowerScope, hostKey: 'main',
                ownerKey: 'screen', testID: 'lower', animation: stackAnimation,
              }, createElement('button', null, 'Lower action')),
              createElement(OverlayView, {
                visible: upper, level: 'secure', testID: 'upper', animation: stackAnimation,
              }, createElement('button', null, 'Upper action'))
            )
          ));
        window.dismissGlobalStack = () =>
          flushSync(() => overlayStore.dismissAll({ scope: 'global', reason: 'system' }));
      `,
    },
  });
  bundle = result.outputFiles[0].text;
  browser = await chromium.launch({
    executablePath: process.env.CHROMIUM_EXECUTABLE_PATH,
  });
});

after(async () => browser?.close());

async function createPage(t) {
  const page = await browser.newPage({ reducedMotion: 'reduce' });
  t.after(() => page.close());
  await page.setContent('<!doctype html><div id="root"></div>');
  await page.addScriptTag({ content: bundle });
  return page;
}

test('removing a host restores pointer and keyboard input while its dialog stays requested', async (t) => {
  const page = await createPage(t);
  await page.evaluate(() => window.renderPage());
  await page.getByRole('dialog').waitFor({ timeout: 2000 });
  assert.equal(await page.evaluate(() => window.getEntryCount()), 1);

  await page.evaluate(() => window.renderPage({ withHost: false }));
  assert.equal(await page.evaluate(() => window.getEntryCount()), 1);
  assert.equal(await page.locator('#root').evaluate((root) => root.inert), false);
  assert.equal(await page.evaluate(() => window.resolveBack()), 'pass');
  const replacement = page.getByRole('button', { name: 'Replacement' });
  await page.keyboard.press('Tab');
  assert.equal(
    await replacement.evaluate((node) => node === document.activeElement),
    true
  );
  await replacement.click({ timeout: 2000 });
  assert.equal(await page.getByRole('button', { name: 'Clicked' }).count(), 1);
});

test('a dialog opened on a covered route takes focus and traps Tab when its route returns', async (t) => {
  const page = await createPage(t);
  await page.evaluate(() => {
    window.focusHost('modal');
    window.renderPage();
  });
  const dialog = page.locator('[role="dialog"]');
  await dialog.waitFor({ timeout: 2000 });
  await page.getByRole('button', { name: 'Foreground' }).focus();
  await dialog.evaluate((node) => node.focus());
  const hasFocus = () =>
    dialog.evaluate((node) => node === document.activeElement);
  assert.equal(await hasFocus(), false);

  await page.evaluate(() => window.focusHost('main'));
  await page.evaluate(() => new Promise(requestAnimationFrame));
  assert.equal(await hasFocus(), true);
  for (const key of ['Tab', 'Shift+Tab']) {
    await page.keyboard.press(key);
    assert.equal(await hasFocus(), true);
  }
});

test('closing a covered dialog leaves keyboard input in the current foreground field', async (t) => {
  const page = await createPage(t);
  await page.evaluate(() => {
    window.focusHost('modal');
    window.renderPage({ visible: false });
  });
  const first = page.getByRole('textbox', { name: 'First input' });
  const second = page.getByRole('textbox', { name: 'Second input' });
  await first.focus();
  await page.evaluate(() => window.renderPage());
  const dialog = page.locator('[role="dialog"]');
  await dialog.waitFor({ timeout: 2000 });
  assert.equal(
    await first.evaluate((node) => node === document.activeElement),
    true
  );

  await second.focus();
  await page.evaluate(() => window.renderPage({ visible: false }));
  await dialog.waitFor({ state: 'detached', timeout: 2000 });
  await page.keyboard.type('foreground edit');
  assert.equal(await second.inputValue(), 'foreground edit');
  assert.equal(await first.inputValue(), '');
});

test('closing a resumed dialog restores the focus displaced on route return', async (t) => {
  const page = await createPage(t);
  await page.evaluate(() => {
    // Keep this control outside the app root so it is eligible for focus
    // while the page host becomes interactive.
    const control = document.createElement('button');
    control.textContent = 'Outside control';
    document.body.append(control);
    window.focusHost('modal');
    window.renderPage({ visible: false });
  });
  await page.getByRole('textbox', { name: 'First input' }).focus();
  await page.evaluate(() => window.renderPage());
  const dialog = page.locator('[role="dialog"]');
  await dialog.waitFor({ timeout: 2000 });

  const outside = page.getByRole('button', { name: 'Outside control' });
  await outside.focus();
  await page.evaluate(() => window.focusHost('main'));
  await page.evaluate(() => new Promise(requestAnimationFrame));
  assert.equal(
    await dialog.evaluate((node) => node === document.activeElement),
    true
  );

  await page.evaluate(() => window.renderPage({ visible: false }));
  await dialog.waitFor({ state: 'detached', timeout: 2000 });
  assert.equal(
    await outside.evaluate((node) => node === document.activeElement),
    true
  );
});

async function hasFocus(locator) {
  return locator.evaluate((node) => node === document.activeElement);
}

async function finishExit(dialog) {
  await dialog.evaluate((node) =>
    node.getAnimations({ subtree: true }).forEach((animation) => animation.finish())
  );
  await dialog.waitFor({ state: 'detached', timeout: 2000 });
}

test('promoting a page dialog during a nested exit preserves its original opener', async (t) => {
  const page = await createPage(t);
  const opener = page.getByRole('button', { name: 'Stack opener' });
  const lower = page.getByTestId('lower');
  const upper = page.getByTestId('upper');
  await page.evaluate(() => window.renderStack());
  await opener.focus();
  await page.evaluate(() => window.renderStack({ lower: true, lowerScope: 'page' }));
  assert.equal(await hasFocus(lower), true);
  await page.evaluate(() =>
    window.renderStack({ lower: true, upper: true, lowerScope: 'page' })
  );
  await upper.getByRole('button', { name: 'Upper action' }).focus();

  await page.evaluate(() => window.renderStack({ lower: true, lowerScope: 'page' }));
  await page.evaluate(() => new Promise(requestAnimationFrame));
  assert.equal(await hasFocus(lower), true);
  await finishExit(upper);
  await page.evaluate(() => window.renderStack({ lowerScope: 'page' }));
  await finishExit(lower);
  assert.equal(await hasFocus(opener), true);
});

for (const exitTiming of ['before', 'after']) {
  test(`a promoted global dialog takes focus when the upper exit finishes ${exitTiming} the handoff frame`, async (t) => {
    const page = await createPage(t);
    const opener = page.getByRole('button', { name: 'Stack opener' });
    const lower = page.getByTestId('lower');
    const upper = page.getByTestId('upper');
    await page.evaluate(() => window.renderStack());
    await opener.focus();
    await page.evaluate(() => window.renderStack({ upper: true }));
    assert.equal(await hasFocus(upper), true);
    await page.evaluate(() => window.renderStack({ lower: true, upper: true }));
    assert.equal(await hasFocus(upper), true);

    await page.evaluate((timing) => {
      window.renderStack({ lower: true });
      if (timing === 'before') {
        document
          .querySelector('[data-testid="upper"]')
          .getAnimations({ subtree: true })
          .forEach((animation) => animation.finish());
      }
    }, exitTiming);
    await page.evaluate(() => new Promise(requestAnimationFrame));
    assert.equal(await hasFocus(lower), true);
    await page.keyboard.press('Tab');
    assert.equal(await hasFocus(lower.getByRole('button')), true);
    if (exitTiming === 'after') await finishExit(upper);
    await page.evaluate(() => window.renderStack());
    await finishExit(lower);
    assert.equal(await hasFocus(opener), true);
  });
}

for (const firstExit of ['lower', 'upper']) {
  test(`concurrent global exits preserve the stack opener when the ${firstExit} dialog finishes first`, async (t) => {
    const page = await createPage(t);
    const opener = page.getByRole('button', { name: 'Stack opener' });
    const lower = page.getByTestId('lower');
    const upper = page.getByTestId('upper');
    await page.evaluate(() => window.renderStack());
    await opener.focus();
    await page.evaluate(() => window.renderStack({ lower: true }));
    await lower.getByRole('button', { name: 'Lower action' }).focus();
    await page.evaluate(() => window.renderStack({ lower: true, upper: true }));
    assert.equal(await hasFocus(upper), true);

    await page.evaluate(() => window.dismissGlobalStack());
    const exitOrder = firstExit === 'lower' ? [lower, upper] : [upper, lower];
    for (const dialog of exitOrder) await finishExit(dialog);
    assert.equal(await hasFocus(opener), true);
  });
}
