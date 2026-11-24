import { readFileSync } from 'fs';
import { join } from 'path';

type IEntryTarget = string | { source?: string; default?: string };

const pkg = JSON.parse(
  readFileSync(join(__dirname, '../../package.json'), 'utf8')
) as {
  'main': string;
  'react-native'?: string;
  'browser'?: string;
  'exports': Record<string, Record<string, IEntryTarget>>;
};

const NATIVE_ENTRY = './lib/module/index.js';
const WEB_ENTRY = './lib/module/index.web.js';

function target(value: IEntryTarget) {
  return typeof value === 'string' ? value : value.default;
}

// Metro with package exports disabled reads these top-level fields in order.
const METRO_MAIN_FIELDS = ['react-native', 'browser', 'main'] as const;

it('resolves the Nitro entry for Metro without package exports', () => {
  const field = METRO_MAIN_FIELDS.find((name) => pkg[name]);
  expect(field && pkg[field]).toBe(NATIVE_ENTRY);
});

it('resolves the Nitro entry for the react-native export condition', () => {
  const conditions = Object.keys(pkg.exports['.']!);
  // Export conditions match in object order, so react-native must come first.
  expect(conditions.indexOf('react-native')).toBeGreaterThanOrEqual(0);
  expect(conditions.indexOf('react-native')).toBeLessThan(
    conditions.indexOf('browser')
  );
  expect(target(pkg.exports['.']!['react-native']!)).toBe(NATIVE_ENTRY);
});

it('resolves the DOM entry for browser bundlers', () => {
  expect(pkg.browser).toBe(WEB_ENTRY);
  expect(target(pkg.exports['.']!.browser!)).toBe(WEB_ENTRY);
});
