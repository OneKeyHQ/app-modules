#!/usr/bin/env node
/**
 * Mirror the OneKey TradingView chart web app's static assets for offline use.
 *
 * The chart at tradingview.onekey.so is a Vite SPA whose entry HTML loads a single
 * main bundle, which then dynamically imports more chunks AND the TradingView
 * charting_library's many runtime-loaded `bundles/*` files. A plain `wget` can't
 * follow those JS-driven imports, so this script drives a headless browser, loads
 * the real chart URL, and saves EVERY response served from the chart's own origin —
 * preserving its URL path — into an output folder.
 *
 * The result lets `react-native-chart-webview` load the chart fully offline (no
 * network), the same way OKX ships `assets/OKTradingView/` inside its app.
 *
 * Usage:
 *   node scripts/fetch-tradingview-assets.mjs [chartUrl] [outDir]
 *
 * Requires (one-time):
 *   yarn add -D playwright && npx playwright install chromium
 *
 * NOTE: the saved index.html references assets by absolute URL
 * (https://tradingview.onekey.so/<hash>/assets/...). To serve these offline,
 * chart-webview should map a virtual origin to this folder (Android
 * WebViewAssetLoader / iOS WKURLSchemeHandler) or rewrite the origin — the paths
 * are preserved exactly so either approach works.
 */

import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const DEFAULT_URL =
  'https://tradingview.onekey.so/?timezone=Asia%2FShanghai&locale=zh-CN&platform=web&theme=dark&appVersion=6.4.0&decimal=8&networkId=btc--0&address=&symbol=BTC&type=market&storageNamespace=market-hyperliquid&scene=market-hyperliquid';

const chartUrl = process.argv[2] || DEFAULT_URL;
const repoRoot = path.resolve(fileURLToPath(import.meta.url), '../..');
const outDir = path.resolve(
  process.argv[3] || path.join(repoRoot, 'example/react-native/tradingview-assets'),
);
const origin = new URL(chartUrl).origin;

let chromium;
try {
  ({ chromium } = await import('playwright'));
} catch {
  console.error(
    'Missing playwright. Install it once:\n' +
      '  yarn add -D playwright && npx playwright install chromium',
  );
  process.exit(1);
}

fs.rmSync(outDir, { recursive: true, force: true });
fs.mkdirSync(outDir, { recursive: true });

const saved = new Map();
function save(pathname, buf) {
  const rel = pathname === '/' || pathname === '' ? '/index.html' : pathname;
  const dest = path.join(outDir, rel);
  fs.mkdirSync(path.dirname(dest), { recursive: true });
  fs.writeFileSync(dest, buf);
  saved.set(rel, buf.length);
}

const browser = await chromium.launch();
const page = await browser.newPage();

page.on('response', async (res) => {
  try {
    const u = new URL(res.url());
    if (u.origin !== origin) return; // only the chart app's own static material
    // Skip API/data endpoints (e.g. an encoded-URL path like
    // `/https:/.../<uuid>`); static asset paths never contain a colon.
    if (u.pathname.includes(':')) return;
    const buf = await res.body();
    save(u.pathname, buf);
  } catch {
    /* response with no retrievable body (redirect, etc.) — ignore */
  }
});

console.log(`Loading ${chartUrl}`);
await page.goto(chartUrl, { waitUntil: 'networkidle', timeout: 120_000 }).catch((e) => {
  console.warn(`navigation warning: ${e.message}`);
});
// Give the charting_library time to lazy-load its bundles after first paint.
await page.waitForTimeout(12_000);
await browser.close();

// Rewrite the chart origin to a ROOT-relative path so the bundle is
// self-contained and loads under any virtual origin (iOS onekey-chart:// /
// Android appassets https), at any file depth. Root-relative `/x` is
// depth-independent; a plain relative rewrite would break deep references.
function rewriteOriginToRoot(dir) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    const stat = fs.statSync(p);
    if (stat.isDirectory()) {
      rewriteOriginToRoot(p);
    } else if (/\.(html|js|mjs|css|json|map)$/.test(name)) {
      const before = fs.readFileSync(p, 'utf8');
      const after = before.split(`${origin}/`).join('/');
      if (after !== before) fs.writeFileSync(p, after);
    }
  }
}
rewriteOriginToRoot(outDir);

const totalBytes = [...saved.values()].reduce((a, b) => a + b, 0);
console.log(`\n✓ Saved ${saved.size} files (${(totalBytes / 1024 / 1024).toFixed(1)} MB) → ${outDir}`);
if (!saved.has('/index.html')) {
  console.warn('⚠ index.html was not captured — check the URL / network.');
}
// Surface the top-level layout so it's easy to eyeball what was mirrored.
for (const entry of fs.readdirSync(outDir).slice(0, 20)) {
  console.log(`  ${entry}`);
}
