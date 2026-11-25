import { QrScannerError } from '../errors';

import type { QrDetectedSymbol } from '../symbols';

/**
 * Pixel operations for the image passes, on plain RGBA buffers so they run
 * the same in browsers, workers and tests (no canvas needed).
 */
export interface RgbaImage {
  data: Uint8ClampedArray<ArrayBuffer>;
  width: number;
  height: number;
}

/** Share of the longer side added as a white border by the padded pass. */
export const PADDING_RATIO = 0.1;
const MIN_PADDING = 16;
const CONTRAST = 1.3;

/** Size scaled so width * height <= maxPixels, keeping the aspect ratio. */
export function fitToPixelBudget(
  width: number,
  height: number,
  maxPixels: number
): { width: number; height: number } {
  const pixels = width * height;
  if (pixels <= maxPixels) return { width, height };
  const scale = Math.sqrt(maxPixels / pixels);
  return {
    width: Math.max(1, Math.min(Math.floor(maxPixels), Math.floor(width * scale))),
    height: Math.max(1, Math.min(Math.floor(maxPixels), Math.floor(height * scale))),
  };
}

/** Adds a white quiet zone around the image. */
export function padded(image: RgbaImage, maxPixels = 50_000_000): RgbaImage {
  const margin = Math.max(
    MIN_PADDING,
    Math.floor(Math.max(image.width, image.height) * PADDING_RATIO)
  );
  const expandedWidth = image.width + margin * 2;
  const expandedHeight = image.height + margin * 2;
  const { width, height } = fitToPixelBudget(expandedWidth, expandedHeight, maxPixels);
  const data = new Uint8ClampedArray(width * height * 4).fill(255);
  const left = Math.min(Math.floor((margin * width) / expandedWidth), Math.floor((width - 1) / 2));
  const top = Math.min(Math.floor((margin * height) / expandedHeight), Math.floor((height - 1) / 2));
  const innerWidth = width - left * 2;
  const innerHeight = height - top * 2;
  for (let y = 0; y < innerHeight; y += 1) {
    const from = Math.floor((y * image.height) / innerHeight) * image.width * 4;
    const to = ((y + top) * width + left) * 4;
    if (innerWidth === image.width) {
      data.set(image.data.subarray(from, from + image.width * 4), to);
    } else {
      for (let x = 0; x < innerWidth; x += 1) {
        const offset = from + Math.floor((x * image.width) / innerWidth) * 4;
        const dest = to + x * 4;
        data[dest] = image.data[offset]!;
        data[dest + 1] = image.data[offset + 1]!;
        data[dest + 2] = image.data[offset + 2]!;
        data[dest + 3] = image.data[offset + 3]!;
      }
    }
  }
  return { data, width, height };
}

/** Luma with contrast 1.3 around mid-grey, optionally inverted. */
export function grayscale(image: RgbaImage, inverted = false): RgbaImage {
  const data = new Uint8ClampedArray(image.data.length);
  for (let i = 0; i < data.length; i += 4) {
    const luma =
      0.299 * image.data[i]! + 0.587 * image.data[i + 1]! + 0.114 * image.data[i + 2]!;
    let value = (luma - 128) * CONTRAST + 128;
    if (inverted) value = 255 - value;
    data[i] = value;
    data[i + 1] = value;
    data[i + 2] = value;
    data[i + 3] = 255;
  }
  return { data, width: image.width, height: image.height };
}

export type DecodePass = 'original' | 'padded' | 'grayscale' | 'inverted';

export interface QrImageEngine {
  name: string;
  /** Text of the first QR symbol, or null. */
  decode(image: RgbaImage): Promise<string | null>;
  /** All QR symbols with normalized top-left coordinates in `image`. */
  decodeSymbols?(image: RgbaImage, multiSymbol: boolean): Promise<QrDetectedSymbol[]>;
}

export interface QrImageHit {
  text: string;
  engine: string;
  pass: DecodePass;
}

export interface QrImageSymbolsHit {
  symbols: QrDetectedSymbol[];
  engine: string;
  pass: DecodePass;
}

function unpadSymbol(
  symbol: QrDetectedSymbol,
  original: RgbaImage,
  paddedImage: RgbaImage
): QrDetectedSymbol {
  const margin = Math.max(
    MIN_PADDING,
    Math.floor(Math.max(original.width, original.height) * PADDING_RATIO)
  );
  const expandedWidth = original.width + margin * 2;
  const expandedHeight = original.height + margin * 2;
  const left = Math.min(
    Math.floor((margin * paddedImage.width) / expandedWidth),
    Math.floor((paddedImage.width - 1) / 2)
  );
  const top = Math.min(
    Math.floor((margin * paddedImage.height) / expandedHeight),
    Math.floor((paddedImage.height - 1) / 2)
  );
  const innerWidth = paddedImage.width - left * 2;
  const innerHeight = paddedImage.height - top * 2;
  if (innerWidth <= 0 || innerHeight <= 0) return symbol;

  const mapX = (x: number) => (x * paddedImage.width - left) / innerWidth;
  const mapY = (y: number) => (y * paddedImage.height - top) / innerHeight;
  const bounds = symbol.bounds
    ? {
        x: mapX(symbol.bounds.x),
        y: mapY(symbol.bounds.y),
        width: (symbol.bounds.width * paddedImage.width) / innerWidth,
        height: (symbol.bounds.height * paddedImage.height) / innerHeight,
      }
    : undefined;
  return {
    text: symbol.text,
    centerX: mapX(symbol.centerX),
    centerY: mapY(symbol.centerY),
    bounds,
  };
}

/**
 * The same pass order as `decodePixels`, returning all symbols from the first
 * pass that detects at least one QR code, mapped back to original image space.
 */
export async function decodePixelsSymbols(
  image: RgbaImage,
  engines: readonly QrImageEngine[],
  maxPixels?: number
): Promise<QrImageSymbolsHit | null> {
  const passes: [DecodePass, () => RgbaImage][] = [
    ['original', () => image],
    ['padded', () => padded(image, maxPixels)],
    ['grayscale', () => grayscale(image)],
    ['inverted', () => grayscale(image, true)],
  ];
  let attempted = false;
  let decoded = false;
  for (const [pass, make] of passes) {
    const variant = make();
    for (const engine of engines) {
      attempted = true;
      try {
        const symbols = engine.decodeSymbols
          ? await engine.decodeSymbols(variant, true)
          : await engine.decode(variant).then((text) =>
              text ? [{ text, centerX: 0.5, centerY: 0.5 }] : []
            );
        decoded = true;
        if (symbols.length > 0) {
          const normalized =
            pass === 'padded'
              ? symbols.map((sym) => unpadSymbol(sym, image, variant))
              : symbols;
          return { symbols: normalized, engine: engine.name, pass };
        }
      } catch {
        // A failed pass leaves the remaining engines and passes available.
      }
    }
  }
  if (attempted && !decoded) throw new QrScannerError('decoder-unavailable', 'A QR decoder failed');
  return null;
}

/**
 * The same pass order as iOS and macOS: original, padded, grayscale,
 * inverted; within a pass every engine in order; stop at the first hit.
 */
export async function decodePixels(
  image: RgbaImage,
  engines: readonly QrImageEngine[],
  maxPixels?: number
): Promise<QrImageHit | null> {
  const passes: [DecodePass, () => RgbaImage][] = [
    ['original', () => image],
    ['padded', () => padded(image, maxPixels)],
    ['grayscale', () => grayscale(image)],
    ['inverted', () => grayscale(image, true)],
  ];
  let attempted = false;
  let decoded = false;
  for (const [pass, make] of passes) {
    const variant = make();
    for (const engine of engines) {
      attempted = true;
      try {
        const text = await engine.decode(variant);
        decoded = true;
        if (text) return { text, engine: engine.name, pass };
      } catch {
        // A failed pass leaves the remaining engines and passes available.
      }
    }
  }
  if (attempted && !decoded) throw new QrScannerError('decoder-unavailable', 'A QR decoder failed');
  return null;
}
