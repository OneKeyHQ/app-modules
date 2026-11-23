import { NitroModules } from 'react-native-nitro-modules';

import { toQrScannerError } from './errors';

import type { QrImageDecoder } from './QrImageDecoder.nitro';

/** Default pixel budget: a 12 MP photo decodes without downscaling. */
export const DEFAULT_MAX_IMAGE_PIXELS = 12_000_000;

export interface DecodeQrImageOptions {
  /** Images above this many pixels are downscaled before decoding. */
  maxPixels?: number;
}

let decoder: QrImageDecoder | undefined;

function getDecoder(): QrImageDecoder {
  decoder ??= NitroModules.createHybridObject<QrImageDecoder>('QrImageDecoder');
  return decoder;
}

/**
 * Raw text of the first QR symbol in a local image, or `null` when none is
 * found. Rejects with `QrScannerError` (`invalid-image`, `resource-limit`,
 * `decoder-unavailable`).
 */
export async function decodeQrImage(
  uri: string,
  options?: DecodeQrImageOptions
): Promise<string | null> {
  const maxPixels = options?.maxPixels ?? DEFAULT_MAX_IMAGE_PIXELS;
  try {
    const result = await getDecoder().decodeImage(uri, maxPixels);
    return result.text ?? null;
  } catch (error) {
    throw toQrScannerError(error);
  }
}
