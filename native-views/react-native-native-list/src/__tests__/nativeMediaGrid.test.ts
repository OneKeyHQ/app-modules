import type { MediaTileRow, NativeListSnapshot } from '../models';
import { validateSnapshot, validatePatches } from '../validation';

const media: MediaTileRow = {
  type: 'mediaTile',
  key: 'preview',
  variant: 'gallery',
  title: 'Preview',
  media: {
    source: { uri: 'https://example.com/asset', width: 160, height: 160 },
    probeOrder: ['image', 'video'],
  },
};
const snapshot = (
  gridColumns: NativeListSnapshot['layout']['gridColumns']
): NativeListSnapshot => ({
  schemaVersion: 1,
  generation: 1,
  layout: { kind: 'grid', gridColumns },
  rows: [media],
});

describe('native tablet grid and media preview contract', () => {
  test.each([2, 3, 4, 5, 6, 7] as const)(
    'accepts %i grid columns',
    (columns) => {
      expect(() => validateSnapshot(snapshot(columns))).not.toThrow();
    }
  );
  test.each([0, 1, 2.5, 8, NaN, Infinity])(
    'rejects invalid grid count %s',
    (columns) => {
      expect(() => validateSnapshot(snapshot(columns as 2))).toThrow();
    }
  );
  test('allows reversed explicit fallback order and media patches', () => {
    expect(() =>
      validatePatches([
        {
          type: 'mediaTile',
          key: 'preview',
          changes: {
            media: {
              source: {
                uri: 'https://example.com/video.mp4',
                width: 160,
                height: 160,
              },
              probeOrder: ['video', 'image'],
            },
          },
        },
      ])
    ).not.toThrow();
  });
  test.each([
    { order: [] },
    { order: ['image', 'image'] },
    { order: ['video', 'image', 'video'] },
    { order: ['unsupported'] },
  ])('rejects invalid probe order $order', ({ order }) => {
    const probeOrder = order as NonNullable<
      MediaTileRow['media']
    >['probeOrder'];
    const value = snapshot(2);
    expect(() =>
      validateSnapshot({
        ...value,
        rows: [
          {
            ...media,
            media: {
              source: {
                uri: 'https://example.com/asset',
                width: 160,
                height: 160,
              },
              probeOrder,
            },
          },
        ],
      })
    ).toThrow(/probeOrder/);
  });
});
