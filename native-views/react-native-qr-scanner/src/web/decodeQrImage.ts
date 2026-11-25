import { DEFAULT_MAX_IMAGE_PIXELS } from '../decodeQrImage.constants';
import { QrScannerError } from '../errors';
import { getEngines } from './engines';
import { readImageDimensions } from './imageDimensions';
import { decodePixels, decodePixelsSymbols, fitToPixelBudget } from './imageOps';

import type { QrImageSymbolsResult } from '../symbols';
import type { RgbaImage } from './imageOps';

/** Sources above this pixel count are refused before decoding. */
const MAX_SOURCE_PIXELS = 200_000_000;
const MAX_PIXELS_CEILING = 50_000_000;

// Two-pixel fixtures with EXIF orientation 6. Bitmap implementations differ
// on which formats honor the tag. Probe only when an upload needs resizing.
const ORIENTATION_PROBES = {
  png: 'iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAIAAAB7QOjdAAAAGmVYSWZNTQAqAAAACAABARIAAwAAAAEABgAAAAAAANZnS2kAAAALSURBVHicY2AAAwAABwABsoas9AAAAABJRU5ErkJggg==',
  webp: 'UklGRk4AAABXRUJQVlA4WAoAAAAIAAAAAQAAAAAAVlA4TA4AAAAvAQAAAAcQEf0PRET/A0VYSUYaAAAATU0AKgAAAAgAAQESAAMAAAABAAYAAAAAAAA=',
};
type OrientationFormat = keyof typeof ORIENTATION_PROBES;
const orientationSupport = new Map<OrientationFormat, Promise<boolean>>();

function appliesBitmapOrientation(format: OrientationFormat): Promise<boolean> {
  let support = orientationSupport.get(format);
  if (!support) {
    const bytes = Uint8Array.from(atob(ORIENTATION_PROBES[format]), (byte) => byte.charCodeAt(0));
    support = createImageBitmap(new Blob([bytes], { type: `image/${format}` }), {
      imageOrientation: 'from-image',
    }).then((bitmap) => {
      try {
        return bitmap.width === 1 && bitmap.height === 2;
      } finally {
        bitmap.close();
      }
    }, () => false);
    orientationSupport.set(format, support);
  }
  return support;
}

async function blobToPixels(blob: Blob, maxPixels: number): Promise<RgbaImage> {
  let dimensions: Awaited<ReturnType<typeof readImageDimensions>>;
  try {
    dimensions = await readImageDimensions(blob);
  } catch {
    throw new QrScannerError('invalid-image', 'unreadable or unsupported image');
  }
  const { width: sourceWidth, height: sourceHeight, orientationFormat } = dimensions;
  if (sourceWidth < 1 || sourceHeight < 1) {
    throw new QrScannerError('invalid-image', 'invalid image dimensions');
  }
  if (sourceWidth > MAX_SOURCE_PIXELS / sourceHeight) {
    throw new QrScannerError('resource-limit', `image has ${sourceWidth}x${sourceHeight} pixels`);
  }

  let bitmap: ImageBitmap;
  try {
    const options: ImageBitmapOptions = { imageOrientation: 'from-image' };
    if (sourceWidth * sourceHeight > maxPixels) {
      const swapAxes = dimensions.swapAxes &&
        (!orientationFormat || await appliesBitmapOrientation(orientationFormat));
      const width = swapAxes ? sourceHeight : sourceWidth;
      const height = swapAxes ? sourceWidth : sourceHeight;
      const size = fitToPixelBudget(width, height, maxPixels);
      options.resizeWidth = size.width;
      options.resizeHeight = size.height;
      options.resizeQuality = 'high';
    }
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

/**
 * Decodes all QR symbols in an image Blob/File with normalized top-left
 * coordinates in EXIF-oriented image space.
 */
export async function decodeQrImageSymbols(
  image: Blob,
  options?: { maxPixels?: number }
): Promise<QrImageSymbolsResult> {
  const maxPixels = options?.maxPixels ?? DEFAULT_MAX_IMAGE_PIXELS;
  if (!Number.isFinite(maxPixels) || maxPixels < 1) {
    throw new QrScannerError('invalid-image', 'maxPixels must be at least 1');
  }
  const budget = Math.min(maxPixels, MAX_PIXELS_CEILING);
  const pixels = await blobToPixels(image, budget);
  const hit = await decodePixelsSymbols(pixels, await getEngines(), budget);
  return {
    width: pixels.width,
    height: pixels.height,
    symbols: hit?.symbols ?? [],
  };
}
