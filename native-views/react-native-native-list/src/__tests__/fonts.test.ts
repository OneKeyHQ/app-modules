import { useSyncExternalStore } from 'react';
import {
  configureNativeListFonts,
  useNativeListFontFamiliesJson,
  type NativeListFontFamilies,
} from '../fonts';

jest.mock('react', () => ({
  useSyncExternalStore: jest.fn((subscribe, getSnapshot) => getSnapshot()),
}));

function NativeListFontProbe() {
  return useNativeListFontFamiliesJson();
}

beforeEach(() => {
  configureNativeListFonts({});
  jest.clearAllMocks();
});

test('normalizes a complete replacement and resets omitted faces to system', () => {
  const input = { regular: ' Host Regular ', bold: 'Host Bold' };
  configureNativeListFonts(input);
  input.regular = 'Mutated';
  expect(NativeListFontProbe()).toBe(
    '{"regular":"Host Regular","bold":"Host Bold"}'
  );
  configureNativeListFonts({ medium: 'Host Medium' });
  expect(NativeListFontProbe()).toBe('{"medium":"Host Medium"}');
  configureNativeListFonts({});
  expect(NativeListFontProbe()).toBe('{}');
});

test('notifies all subscribed lists once, with idempotent mappings and cleanup', () => {
  NativeListFontProbe();
  const subscribe = jest.mocked(useSyncExternalStore).mock.calls[0][0];
  const first = jest.fn();
  const second = jest.fn();
  const removeFirst = subscribe(first);
  const removeSecond = subscribe(second);
  configureNativeListFonts({ bold: 'Bold', regular: 'Regular' });
  configureNativeListFonts({ regular: ' Regular ', bold: 'Bold' });
  expect(first).toHaveBeenCalledTimes(1);
  expect(second).toHaveBeenCalledTimes(1);
  removeFirst();
  configureNativeListFonts({});
  expect(first).toHaveBeenCalledTimes(1);
  expect(second).toHaveBeenCalledTimes(2);
  removeSecond();
});

test.each([
  null,
  [],
  { regular: '' },
  { regular: '\nBad' },
  { regular: 'x'.repeat(129) },
  { bold: 4 },
  { other: 'Bad' },
])('rejects invalid input atomically: %p', (input) => {
  configureNativeListFonts({ regular: 'Valid' });
  expect(() =>
    configureNativeListFonts(input as unknown as NativeListFontFamilies)
  ).toThrow(TypeError);
  expect(NativeListFontProbe()).toBe('{"regular":"Valid"}');
});

test('isolated module instances have independent runtime configurations', () => {
  configureNativeListFonts({ regular: 'First runtime' });
  jest.isolateModules(() => {
    const isolated = require('../fonts') as typeof import('../fonts');
    expect(isolated.useNativeListFontFamiliesJson()).toBe('{}');
    isolated.configureNativeListFonts({ bold: 'Second runtime' });
    expect(isolated.useNativeListFontFamiliesJson()).toBe(
      '{"bold":"Second runtime"}'
    );
  });
  expect(NativeListFontProbe()).toBe('{"regular":"First runtime"}');
});
