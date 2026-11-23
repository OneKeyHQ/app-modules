import { detectImageMime } from '../web/imageFormat';

const bytes = (...values: (number | string)[]) =>
  new Uint8Array(
    values.flatMap((value) =>
      typeof value === 'string'
        ? [...value].map((char) => char.charCodeAt(0))
        : [value]
    )
  );

it.each([
  ['image/jpeg', bytes(0xff, 0xd8, 0xff, 0xe0)],
  ['image/png', bytes(0x89, 'PNG', 0x0d, 0x0a, 0x1a, 0x0a)],
  ['image/gif', bytes('GIF89a')],
  ['image/webp', bytes('RIFF', 0, 0, 0, 0, 'WEBP')],
  ['image/heic', bytes(0, 0, 0, 0x18, 'ftypheic')],
  ['image/heif', bytes(0, 0, 0, 0x18, 'ftypmif1')],
  ['image/avif', bytes(0, 0, 0, 0x1c, 'ftypavif')],
  ['image/bmp', bytes('BM')],
  ['image/tiff', bytes('II', 0x2a, 0)],
  ['image/x-icon', bytes(0, 0, 1, 0)],
])('detects %s from its signature', (mime, header) => {
  expect(detectImageMime(header)).toBe(mime);
});

it('returns undefined for unknown or truncated data', () => {
  expect(detectImageMime(bytes('hello world'))).toBeUndefined();
  expect(detectImageMime(bytes(0, 0, 0, 0x18, 'ftypmp42'))).toBeUndefined();
  expect(detectImageMime(bytes(0x89, 'PN'))).toBeUndefined();
  expect(detectImageMime(new Uint8Array())).toBeUndefined();
});
