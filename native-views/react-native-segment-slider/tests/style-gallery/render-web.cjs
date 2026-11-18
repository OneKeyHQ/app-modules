/* eslint-disable */
// Renders web-gallery.tsx in headless Chromium at 2x and writes web.png.
// Pressed rows are captured one at a time while a real mouse button is held.
//
// esbuild, playwright-core, react and react-dom are resolved from NODE_PATH
// (this repository does not install esbuild or Playwright), e.g.:
//   NODE_PATH=<app-monorepo>/node_modules:<app-modules>/node_modules \
//     node tests/style-gallery/render-web.cjs <out-dir>
const fs = require('fs');
const path = require('path');
const esbuild = require('esbuild');
const { chromium } = require('playwright-core');

const outDir = path.resolve(process.argv[2] || '.tmp/segment-slider-gallery');
const spec = require('./scenarios.json');

async function main() {
  fs.mkdirSync(outDir, { recursive: true });
  const bundle = await esbuild.build({
    entryPoints: [path.join(__dirname, 'web-gallery.tsx')],
    bundle: true,
    write: false,
    minify: true,
    jsx: 'automatic',
    nodePaths: (process.env.NODE_PATH || '')
      .split(path.delimiter)
      .filter(Boolean),
    define: { 'process.env.NODE_ENV': '"production"' },
    logLevel: 'error',
  });
  const html = `<!doctype html><html><head><meta charset="utf-8"></head>
<body style="margin:0;font-family:-apple-system,system-ui,sans-serif"><div id="root"></div>
<script>${bundle.outputFiles[0].text.replace(
    /<\/script/g,
    '<\\/script'
  )}</script></body></html>`;

  const width = spec.sliderWidth + spec.sideInset * 2;
  const browser = await chromium.launch();
  const page = await browser.newPage({
    viewport: { width, height: spec.rowHeight * spec.rows.length },
    deviceScaleFactor: 2,
  });
  const errors = [];
  page.on('pageerror', (e) => errors.push(String(e)));
  page.on('console', (m) => m.type() === 'error' && errors.push(m.text()));
  await page.setContent(html);
  await page.waitForSelector('[data-row]');

  await page.screenshot({ path: path.join(outDir, 'web-base.png') });
  const pressed = [];
  for (const [index, row] of spec.rows.entries()) {
    if (!row.pressed) continue;
    // The web track spans the slider minus 8 px (half the thumb) on each side.
    const x = spec.sideInset + 8 + (row.value / 100) * (spec.sliderWidth - 16);
    const y = spec.rowHeight * index + spec.sliderTop + 12;
    await page.mouse.move(x, y);
    await page.mouse.down();
    await page.waitForTimeout(250);
    const file = path.join(outDir, `web-row-${index}.png`);
    await page.screenshot({
      path: file,
      clip: { x: 0, y: spec.rowHeight * index, width, height: spec.rowHeight },
    });
    await page.mouse.up();
    pressed.push({ index, file });
  }
  await browser.close();
  fs.writeFileSync(
    path.join(outDir, 'web-pressed.json'),
    JSON.stringify({ pressed, errors }, null, 2)
  );
  if (errors.length) {
    console.error(errors.join('\n'));
    process.exit(1);
  }
  console.log(`web: ${spec.rows.length} rows, ${pressed.length} pressed`);
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
