/// <reference lib="dom" />
import { ImageCropPickerError } from '../shared';
import { scaleOf } from './geometry';
import { IMAGE_HEADER_LENGTH } from './imageFormat';

import type { CropState, Size } from './geometry';

export const RESULT_MIME = 'image/jpeg';
// iOS default; react-native-image-crop-picker used it on the web too.
export const DEFAULT_QUALITY = 0.8;
// Same limit as native original selection.
export const MAX_INPUT_BYTES = 64 * 1024 * 1024;

// Result paths are object URLs, so clean/cleanSingle can release them the way
// native deletes its temporary files.
const resultUrls = new Set<string>();

export function createResultUrl(blob: Blob): string {
  const url = URL.createObjectURL(blob);
  resultUrls.add(url);
  return url;
}

export function releaseResultUrl(url: string): void {
  if (resultUrls.delete(url)) {
    URL.revokeObjectURL(url);
  }
}

export function releaseAllResultUrls(): void {
  resultUrls.forEach((url) => URL.revokeObjectURL(url));
  resultUrls.clear();
}

export function imageSizeOf(image: HTMLImageElement): Size {
  return { width: image.naturalWidth, height: image.naturalHeight };
}

// Resolves once the image is decoded. Browsers apply EXIF orientation to
// both <img> and canvas drawing, so natural sizes are already upright.
export function loadImage(src: string): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const image = new Image();
    if (/^https?:/i.test(src)) {
      // Without CORS the canvas is tainted and the crop cannot be read back.
      image.crossOrigin = 'anonymous';
    }
    image.decoding = 'async';
    image.onload = () => {
      if (image.naturalWidth > 0 && image.naturalHeight > 0) {
        resolve(image);
      } else {
        reject(new Error('Image has no pixels'));
      }
    };
    image.onerror = () => reject(new Error('Image failed to load'));
    image.src = src;
  });
}

function canvasToBlob(
  canvas: HTMLCanvasElement,
  quality: number
): Promise<Blob> {
  return new Promise((resolve, reject) => {
    try {
      canvas.toBlob(
        (blob) => {
          if (blob) {
            resolve(blob);
          } else {
            reject(new Error('Canvas produced no data'));
          }
        },
        RESULT_MIME,
        quality
      );
    } catch (error) {
      // SecurityError for a tainted canvas.
      reject(error);
    }
  });
}

function createCanvas(size: Size): {
  canvas: HTMLCanvasElement;
  context: CanvasRenderingContext2D;
} {
  const canvas = document.createElement('canvas');
  canvas.width = size.width;
  canvas.height = size.height;
  const context = canvas.getContext('2d');
  if (!context) {
    throw new ImageCropPickerError(
      'E_CANNOT_SAVE_IMAGE',
      'Cannot create a canvas to save the image'
    );
  }
  context.imageSmoothingEnabled = true;
  context.imageSmoothingQuality = 'high';
  return { canvas, context };
}

async function encode(
  canvas: HTMLCanvasElement,
  quality: number
): Promise<Blob> {
  try {
    return await canvasToBlob(canvas, quality);
  } catch {
    throw new ImageCropPickerError(
      'E_CANNOT_SAVE_IMAGE',
      'Cannot save the cropped image'
    );
  }
}

// Draws what the crop box shows into an `output`-sized JPEG. Each axis is
// scaled on its own so a box a pixel off the target ratio still fills it.
export function renderCrop(
  image: HTMLImageElement,
  state: CropState,
  box: Size,
  output: Size,
  quality: number
): Promise<Blob> {
  const { canvas, context } = createCanvas(output);
  const imageSize = imageSizeOf(image);
  const scale = scaleOf(state, imageSize, box);
  context.translate(output.width / 2, output.height / 2);
  context.scale(output.width / box.width, output.height / box.height);
  context.translate(state.offsetX, state.offsetY);
  context.rotate((-state.rotation * Math.PI) / 180);
  context.scale(scale, scale);
  context.drawImage(image, -imageSize.width / 2, -imageSize.height / 2);
  return encode(canvas, quality);
}

// Re-encodes the whole image, for openPicker without cropping.
export function renderWhole(
  image: HTMLImageElement,
  output: Size,
  quality: number
): Promise<Blob> {
  const { canvas, context } = createCanvas(output);
  context.drawImage(image, 0, 0, output.width, output.height);
  return encode(canvas, quality);
}

// The leading bytes of a file, for detectImageMime.
export function readImageHeader(blob: Blob): Promise<Uint8Array> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(new Uint8Array(reader.result as ArrayBuffer));
    reader.onerror = () => reject(reader.error);
    reader.readAsArrayBuffer(blob.slice(0, IMAGE_HEADER_LENGTH));
  });
}

// Base64 without the data: prefix, like native `data`.
export function blobToBase64(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = String(reader.result ?? '');
      resolve(result.slice(result.indexOf(',') + 1));
    };
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(blob);
  });
}

export function normalizeQuality(quality: number | undefined): number {
  if (quality === undefined || Number.isNaN(quality)) {
    return DEFAULT_QUALITY;
  }
  return Math.min(Math.max(quality, 0), 1);
}
