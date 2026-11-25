import type { HybridObject } from 'react-native-nitro-modules';
import type { QrDetectedSymbol } from './symbols';

/** Which engine produced a hit. */
export type QrDecodeEngine = 'vision' | 'mlkit' | 'zxing';

/** Which image variant produced a hit, in the order they are tried. */
export type QrDecodePass = 'original' | 'padded' | 'grayscale' | 'inverted';

export interface QrImageDecodeResult {
  /** Raw QR text; absent when no pass found a QR symbol. */
  text?: string;
  engine?: QrDecodeEngine;
  pass?: QrDecodePass;
}

export interface QrImageSymbolsDecodeResult {
  /** EXIF-oriented decoded image dimensions. */
  width: number;
  height: number;
  symbols: QrDetectedSymbol[];
  engine?: QrDecodeEngine;
  pass?: QrDecodePass;
}

/**
 * Decodes one local image. Rejects with a message starting with
 * `invalid-image:`, `resource-limit:` or `decoder-unavailable:`.
 */
export interface QrImageDecoder
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  /**
   * @param uri `file://` URL or absolute path of a local image.
   * @param maxPixels Images above this pixel count are downscaled first.
   */
  decodeImage(uri: string, maxPixels: number): Promise<QrImageDecodeResult>;
  /**
   * Decodes all QR symbols in a local image with normalized top-left
   * coordinates in EXIF-oriented image space.
   */
  decodeImageSymbols(
    uri: string,
    maxPixels: number
  ): Promise<QrImageSymbolsDecodeResult>;
}
