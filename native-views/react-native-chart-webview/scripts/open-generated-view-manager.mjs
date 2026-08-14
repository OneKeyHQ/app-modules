// Post-nitrogen step: mark the generated Android view manager `open`.
//
// nitrogen >= 0.35 emits `HybridChartWebviewManager` as a final class, but this
// package ships `TeardownChartWebviewManager` (ChartWebviewPackage.kt) which
// subclasses it to dispose the non-pooled WebView on drop — the generated
// manager never tears the WebView down, leaking a Chromium renderer per
// mount/unmount. Runs as part of the `nitrogen` script so every codegen pass
// (bob build / release) reapplies it; idempotent.
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const file = join(
  dirname(fileURLToPath(import.meta.url)),
  '..',
  'nitrogen',
  'generated',
  'android',
  'kotlin',
  'com',
  'margelo',
  'nitro',
  'chartwebview',
  'views',
  'HybridChartWebviewManager.kt',
);

const source = readFileSync(file, 'utf8');
const FINAL_DECL = 'public class HybridChartWebviewManager';
const OPEN_DECL = 'public open class HybridChartWebviewManager';

if (source.includes(OPEN_DECL)) {
  process.exit(0);
}
if (!source.includes(FINAL_DECL)) {
  throw new Error(
    `Expected "${FINAL_DECL}" in ${file} — nitrogen output changed, update this script.`,
  );
}
writeFileSync(file, source.replace(FINAL_DECL, OPEN_DECL));
console.log('open-generated-view-manager: marked HybridChartWebviewManager open');
