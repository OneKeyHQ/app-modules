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
    width: Math.max(1, Math.floor(width * scale)),
    height: Math.max(1, Math.floor(height * scale)),
  };
}

/** Adds a white quiet zone around the image. */
export function padded(image: RgbaImage): RgbaImage {
  const margin = Math.max(
    MIN_PADDING,
    Math.floor(Math.max(image.width, image.height) * PADDING_RATIO)
  );
  const width = image.width + margin * 2;
  const height = image.height + margin * 2;
  const data = new Uint8ClampedArray(width * height * 4).fill(255);
  for (let y = 0; y < image.height; y += 1) {
    const from = y * image.width * 4;
    data.set(
      image.data.subarray(from, from + image.width * 4),
      ((y + margin) * width + margin) * 4
    );
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
}

export interface QrImageHit {
  text: string;
  engine: string;
  pass: DecodePass;
}

/**
 * The same pass order as iOS and macOS: original, padded, grayscale,
 * inverted; within a pass every engine in order; stop at the first hit.
 */
export async function decodePixels(
  image: RgbaImage,
  engines: readonly QrImageEngine[]
): Promise<QrImageHit | null> {
  const passes: [DecodePass, () => RgbaImage][] = [
    ['original', () => image],
    ['padded', () => padded(image)],
    ['grayscale', () => grayscale(image)],
    ['inverted', () => grayscale(image, true)],
  ];
  for (const [pass, make] of passes) {
    const variant = make();
    for (const engine of engines) {
      const text = await engine.decode(variant);
      if (text) return { text, engine: engine.name, pass };
    }
  }
  return null;
}
