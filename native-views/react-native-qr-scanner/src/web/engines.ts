import { prepareZXingModule, readBarcodes } from 'zxing-wasm/reader';

import { QrScannerError } from '../errors';

import type { QrImageEngine, RgbaImage } from './imageOps';

export interface WebQrScannerConfig {
  /**
   * Loads zxing-wasm's reader binary (`zxing-wasm/reader/zxing_reader.wasm`,
   * version 3.1.3). Required where the browser has no QR-capable
   * BarcodeDetector; the SDK never downloads the binary itself.
   */
  loadZXingWasm?: () => Promise<ArrayBuffer>;
  /** Skip the native BarcodeDetector even when present (tests, comparisons). */
  disableNativeDetector?: boolean;
}

let config: WebQrScannerConfig = {};
let zxingReady: Promise<void> | undefined;
let nativeDetector: Promise<QrImageEngine | null> | undefined;

/** Call before the first decode. Later calls replace the config. */
export function configureWebQrScanner(next: WebQrScannerConfig): void {
  config = { ...next };
  zxingReady = undefined;
  nativeDetector = undefined;
}

interface DetectedBarcodeLike {
  rawValue: string;
}
interface BarcodeDetectorLike {
  detect(source: ImageData): Promise<DetectedBarcodeLike[]>;
}
interface BarcodeDetectorClassLike {
  new (options: { formats: string[] }): BarcodeDetectorLike;
  getSupportedFormats?: () => Promise<string[]>;
}

function toImageData(image: RgbaImage): ImageData {
  return new ImageData(image.data, image.width, image.height);
}

async function createNativeEngine(): Promise<QrImageEngine | null> {
  if (config.disableNativeDetector) return null;
  const Detector = (globalThis as { BarcodeDetector?: BarcodeDetectorClassLike })
    .BarcodeDetector;
  if (!Detector) return null;
  try {
    const formats = (await Detector.getSupportedFormats?.()) ?? [];
    if (!formats.includes('qr_code')) return null;
    const detector = new Detector({ formats: ['qr_code'] });
    return {
      name: 'native',
      async decode(image) {
        const results = await detector.detect(toImageData(image));
        return results.find((result) => result.rawValue)?.rawValue ?? null;
      },
    };
  } catch {
    return null;
  }
}

function prepareZXing(): Promise<void> {
  const { loadZXingWasm } = config;
  if (!loadZXingWasm) {
    return Promise.reject(
      new QrScannerError(
        'decoder-unavailable',
        'No QR-capable BarcodeDetector and no zxing-wasm loader configured'
      )
    );
  }
  zxingReady ??= loadZXingWasm().then(async (binary) => {
    await prepareZXingModule({
      overrides: {
        instantiateWasm(imports, receiveInstance) {
          void WebAssembly.instantiate(binary, imports).then(({ instance }) =>
            receiveInstance(instance)
          );
          return {};
        },
      },
      fireImmediately: true,
    });
  });
  return zxingReady.catch((error: unknown) => {
    zxingReady = undefined;
    throw new QrScannerError(
      'decoder-unavailable',
      `zxing-wasm failed to load: ${error instanceof Error ? error.message : String(error)}`
    );
  });
}

export const zxingEngine: QrImageEngine = {
  name: 'zxing',
  async decode(image) {
    await prepareZXing();
    const results = await readBarcodes(toImageData(image), {
      formats: ['QRCode'],
      tryHarder: true,
      // The pipeline has its own inverted pass.
      tryInvert: false,
      maxNumberOfSymbols: 1,
    });
    return results.find((result) => result.isValid && result.text)?.text ?? null;
  },
};

/** Native detector first when available, zxing-wasm as fallback. */
export async function getEngines(): Promise<QrImageEngine[]> {
  nativeDetector ??= createNativeEngine();
  const native = await nativeDetector;
  if (native) return config.loadZXingWasm ? [native, zxingEngine] : [native];
  return [zxingEngine];
}
