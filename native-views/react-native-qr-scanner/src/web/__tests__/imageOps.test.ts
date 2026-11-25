import {
  decodePixels,
  decodePixelsSymbols,
  fitToPixelBudget,
  grayscale,
  padded,
} from '../imageOps';

import type { QrImageEngine, RgbaImage } from '../imageOps';

function solid(width: number, height: number, rgb: [number, number, number]): RgbaImage {
  const data = new Uint8ClampedArray(width * height * 4);
  for (let i = 0; i < data.length; i += 4) {
    data[i] = rgb[0];
    data[i + 1] = rgb[1];
    data[i + 2] = rgb[2];
    data[i + 3] = 255;
  }
  return { data, width, height };
}

describe('fitToPixelBudget', () => {
  it('keeps images within budget and the aspect ratio otherwise', () => {
    expect(fitToPixelBudget(400, 300, 1_000_000)).toEqual({ width: 400, height: 300 });
    const fitted = fitToPixelBudget(4000, 3000, 10_000);
    expect(fitted.width * fitted.height).toBeLessThanOrEqual(10_000);
    expect(fitted.width / fitted.height).toBeCloseTo(4 / 3, 1);
  });
});

describe('padded', () => {
  it('adds a white border of 10% of the longer side (at least 16 px)', () => {
    const black = solid(400, 300, [0, 0, 0]);
    const out = padded(black);
    expect(out.width).toBe(480);
    expect(out.height).toBe(380);
    expect(Array.from(out.data.subarray(0, 4))).toEqual([255, 255, 255, 255]);
    const center = (190 * 480 + 240) * 4;
    expect(Array.from(out.data.subarray(center, center + 3))).toEqual([0, 0, 0]);
    expect(padded(solid(20, 20, [0, 0, 0])).width).toBe(52);
  });
});

describe('grayscale', () => {
  it('maps to luma with extra contrast, and inverts on request', () => {
    const dark = grayscale(solid(1, 1, [40, 40, 40]));
    const light = grayscale(solid(1, 1, [220, 220, 220]));
    expect(dark.data[0]).toBeLessThan(40);
    expect(light.data[0]).toBeGreaterThan(220);
    expect(dark.data[0]).toBe(dark.data[2]);
    const inverted = grayscale(solid(1, 1, [40, 40, 40]), true);
    expect(inverted.data[0]).toBe(255 - dark.data[0]!);
  });
});

describe('decodePixels', () => {
  it('runs passes in order, every engine per pass, and stops at the first hit', async () => {
    const calls: string[] = [];
    const engine = (name: string, hitOn?: string): QrImageEngine => ({
      name,
      async decode(image) {
        const pass = calls.length < 2 ? 'original' : calls.length < 4 ? 'padded' : 'grayscale';
        calls.push(`${name}:${pass}:${image.width}`);
        return pass === hitOn && name === 'zxing' ? 'found' : null;
      },
    });
    const hit = await decodePixels(solid(100, 50, [0, 0, 0]), [
      engine('native'),
      engine('zxing', 'padded'),
    ]);
    expect(hit).toEqual({ text: 'found', engine: 'zxing', pass: 'padded' });
    expect(calls).toEqual([
      'native:original:100',
      'zxing:original:100',
      'native:padded:132',
      'zxing:padded:132',
    ]);
  });

  it('tries the fallback engine after a primary decoder error', async () => {
    const failure = { name: 'native', decode: async () => { throw new Error('failed'); } };
    const fallback = { name: 'zxing', decode: async () => 'hello' };
    const image = solid(10, 10, [0, 0, 0]);
    await expect(decodePixels(image, [failure, fallback])).resolves.toEqual({
      text: 'hello', engine: 'zxing', pass: 'original',
    });
    await expect(decodePixels(image, [failure])).rejects.toMatchObject({
      code: 'decoder-unavailable',
    });
  });

  it('runs a padded pass that fits the budget and keeps a white border', async () => {
    const image = solid(1000, 4, [0, 0, 0]);
    const paddedImage = padded(image, 4_000);
    expect(paddedImage.width * paddedImage.height).toBeLessThanOrEqual(4_000);
    expect(Array.from(paddedImage.data.subarray(0, 4))).toEqual([255, 255, 255, 255]);
    expect(paddedImage.data).toContain(0);
  });

  it('returns null when nothing is found', async () => {
    const none: QrImageEngine = { name: 'x', decode: async () => null };
    expect(await decodePixels(solid(4, 4, [255, 255, 255]), [none])).toBeNull();
  });

  it('accumulates unique symbols across passes in decodePixelsSymbols', async () => {
    const engine: QrImageEngine = {
      name: 'zxing',
      decode: async () => null,
      decodeSymbols: async (image) => {
        if (image.width > 100) {
          // Padded pass discovers an edge-to-edge QR
          return [{ text: 'edge-qr', centerX: 0.5, centerY: 0.5 }];
        }
        // Original pass has pixel 0, inverted pass has pixel 255
        if (image.data[0] === 0) {
          return [{ text: 'normal-qr', centerX: 0.25, centerY: 0.5 }];
        }
        if (image.data[0] === 255) {
          return [
            { text: 'normal-qr', centerX: 0.25, centerY: 0.5 },
            { text: 'inverted-qr', centerX: 0.75, centerY: 0.5 },
          ];
        }
        return [];
      },
    };
    const hit = await decodePixelsSymbols(solid(100, 100, [0, 0, 0]), [engine]);
    expect(hit?.symbols.map((s) => s.text)).toEqual([
      'normal-qr',
      'edge-qr',
      'inverted-qr',
    ]);
  });

  it('bounds fallback work with a shared deadline after first hit in decodePixelsSymbols', async () => {
    const fast: QrImageEngine = {
      name: 'native',
      decode: async () => null,
      decodeSymbols: async () => [{ text: 'fast-qr', centerX: 0.5, centerY: 0.5 }],
    };
    const stalled: QrImageEngine = {
      name: 'zxing',
      decode: async () => new Promise(() => {}),
      decodeSymbols: async () => new Promise(() => {}),
    };
    const hit = await decodePixelsSymbols(solid(32, 32, [0, 0, 0]), [fast, stalled], undefined, 25);
    expect(hit?.symbols.map((s) => s.text)).toEqual(['fast-qr']);
    expect(hit?.engine).toBe('native');
  });
});
