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
  /** True when remaining optional passes stopped early due to the post-hit deadline. */
  partial?: boolean;
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

const MAX_IMAGE_SYMBOLS = 8;
const DEFAULT_POST_HIT_DEADLINE_MS = 3000;

function raceWithDeadline<T>(
  promise: Promise<T>,
  remainingMs: number
): Promise<{ timedOut: true } | { timedOut: false; value: T }> {
  if (remainingMs <= 0) {
    // Observe rejections from already-started work so the browser never sees
    // an unhandled rejection after the fallback budget expires.
    promise.catch(() => {});
    return Promise.resolve({ timedOut: true });
  }
  return new Promise((resolve, reject) => {
    let settled = false;
    const timer = setTimeout(() => {
      if (settled) return;
      settled = true;
      resolve({ timedOut: true });
    }, remainingMs);
    promise.then(
      (value) => {
        if (settled) return;
        settled = true;
        clearTimeout(timer);
        resolve({ timedOut: false, value });
      },
      (error) => {
        if (settled) return;
        settled = true;
        clearTimeout(timer);
        reject(error);
      }
    );
  });
}

/**
 * The same pass order as `decodePixels`, accumulating unique QR symbols across
 * passes and engines (up to 8) so mixed normal/inverted or multi-engine codes
 * all reach the selection UI, mapped back to original image space. Once the
 * first hit arrives, remaining optional passes share a bounded deadline so a
 * slow or stalled fallback engine cannot block returning already-found codes.
 */
export async function decodePixelsSymbols(
  image: RgbaImage,
  engines: readonly QrImageEngine[],
  maxPixels?: number,
  postHitDeadlineMs = DEFAULT_POST_HIT_DEADLINE_MS
): Promise<QrImageSymbolsHit | null> {
  const passes: [DecodePass, () => RgbaImage][] = [
    ['original', () => image],
    ['padded', () => padded(image, maxPixels)],
    ['grayscale', () => grayscale(image)],
    ['inverted', () => grayscale(image, true)],
  ];
  let attempted = false;
  let decoded = false;
  const merged: QrDetectedSymbol[] = [];
  const seen = new Set<string>();
  let firstEngine: string | undefined;
  let firstPass: DecodePass | undefined;
  let postHitDeadline: number | undefined;
  let partial = false;

  for (const [pass, make] of passes) {
    if (merged.length >= MAX_IMAGE_SYMBOLS) break;
    if (postHitDeadline !== undefined && Date.now() >= postHitDeadline) {
      partial = true;
      break;
    }
    const variant = make();
    for (const engine of engines) {
      if (merged.length >= MAX_IMAGE_SYMBOLS) break;
      if (postHitDeadline !== undefined && Date.now() >= postHitDeadline) {
        partial = true;
        break;
      }
      attempted = true;
      try {
        const work = engine.decodeSymbols
          ? engine.decodeSymbols(variant, true)
          : engine.decode(variant).then((text) =>
              text ? [{ text, centerX: 0.5, centerY: 0.5 }] : []
            );
        let symbols: QrDetectedSymbol[];
        if (postHitDeadline !== undefined) {
          const raced = await raceWithDeadline(work, postHitDeadline - Date.now());
          if (raced.timedOut) {
            partial = true;
            break;
          }
          symbols = raced.value;
        } else {
          symbols = await work;
        }
        decoded = true;
        if (symbols.length > 0) {
          const normalized =
            pass === 'padded'
              ? symbols.map((sym) => unpadSymbol(sym, image, variant))
              : symbols;
          for (const sym of normalized) {
            if (!sym.text || seen.has(sym.text)) continue;
            seen.add(sym.text);
            merged.push(sym);
            if (!firstEngine) {
              firstEngine = engine.name;
              firstPass = pass;
              postHitDeadline = Date.now() + postHitDeadlineMs;
            }
            if (merged.length >= MAX_IMAGE_SYMBOLS) break;
          }
        }
      } catch {
        // A failed pass leaves the remaining engines and passes available.
      }
    }
  }
  if (merged.length > 0 && firstEngine && firstPass) {
    return {
      symbols: merged,
      engine: firstEngine,
      pass: firstPass,
      ...(partial ? { partial: true } : {}),
    };
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
