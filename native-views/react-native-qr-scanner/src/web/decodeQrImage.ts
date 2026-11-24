import { DEFAULT_MAX_IMAGE_PIXELS } from '../decodeQrImage.constants';
import { QrScannerError } from '../errors';
import { getEngines } from './engines';
import { readImageDimensions } from './imageDimensions';
import { decodePixels, fitToPixelBudget } from './imageOps';

import type { RgbaImage } from './imageOps';

/** Sources above this pixel count are refused before decoding. */
const MAX_SOURCE_PIXELS = 200_000_000;
const MAX_PIXELS_CEILING = 50_000_000;

async function blobToPixels(blob: Blob, maxPixels: number): Promise<RgbaImage> {
  let dimensions: Awaited<ReturnType<typeof readImageDimensions>>;
  try {
    dimensions = await readImageDimensions(blob);
  } catch {
    throw new QrScannerError('invalid-image', 'unreadable or unsupported image');
  }
  const { width: sourceWidth, height: sourceHeight, swapAxes } = dimensions;
  if (sourceWidth < 1 || sourceHeight < 1) {
    throw new QrScannerError('invalid-image', 'invalid image dimensions');
  }
  if (sourceWidth > MAX_SOURCE_PIXELS / sourceHeight) {
    throw new QrScannerError('resource-limit', `image has ${sourceWidth}x${sourceHeight} pixels`);
  }

  const orientedWidth = swapAxes ? sourceHeight : sourceWidth;
  const orientedHeight = swapAxes ? sourceWidth : sourceHeight;
  const size = fitToPixelBudget(orientedWidth, orientedHeight, maxPixels);
  const options: ImageBitmapOptions = { imageOrientation: 'from-image' };
  if (size.width !== orientedWidth || size.height !== orientedHeight) {
    options.resizeWidth = size.width;
    options.resizeHeight = size.height;
    options.resizeQuality = 'high';
  }
  let bitmap: ImageBitmap;
  try {
    bitmap = await createImageBitmap(blob, options);
  } catch {
    throw new QrScannerError('invalid-image', 'unreadable or unsupported image');
  }
  try {
    const { width, height } = fitToPixelBudget(bitmap.width, bitmap.height, maxPixels);
    const canvas =
      typeof OffscreenCanvas === 'function'
        ? new OffscreenCanvas(width, height)
        : Object.assign(document.createElement('canvas'), { width, height });
    const context = canvas.getContext('2d', { willReadFrequently: true }) as
      | CanvasRenderingContext2D
      | OffscreenCanvasRenderingContext2D
      | null;
    if (!context) throw new QrScannerError('decoder-unavailable', 'no 2D canvas');
    context.drawImage(bitmap, 0, 0, width, height);
    const { data } = context.getImageData(0, 0, width, height);
    return { data, width, height };
  } finally {
    bitmap.close();
  }
}

/**
 * Web counterpart of the native `decodeQrImage`: raw text of the first QR
 * symbol in an image Blob/File, or null. Same passes as iOS and macOS.
 */
export async function decodeQrImage(
  image: Blob,
  options?: { maxPixels?: number }
): Promise<string | null> {
  const maxPixels = options?.maxPixels ?? DEFAULT_MAX_IMAGE_PIXELS;
  if (!Number.isFinite(maxPixels) || maxPixels < 1) {
    throw new QrScannerError('invalid-image', 'maxPixels must be at least 1');
  }
  const budget = Math.min(maxPixels, MAX_PIXELS_CEILING);
  const pixels = await blobToPixels(image, budget);
  const hit = await decodePixels(pixels, await getEngines(), budget);
  return hit?.text ?? null;
}
