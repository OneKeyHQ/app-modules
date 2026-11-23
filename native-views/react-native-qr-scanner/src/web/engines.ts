import { QrScannerError } from '../errors';
import { prepareZXingFromBinary, readQrText } from './zxingWorkerProtocol';

import type { QrImageEngine, RgbaImage } from './imageOps';
import type { ZXingWorkerRequest, ZXingWorkerResponse } from './zxingWorkerProtocol';

/** The part of `Worker` the SDK uses. */
export interface ZXingWorkerLike {
  postMessage(message: ZXingWorkerRequest, transfer?: Transferable[]): void;
  onmessage: ((event: { data: ZXingWorkerResponse }) => void) | null;
  onerror: ((event: unknown) => void) | null;
  terminate(): void;
}

export interface WebQrScannerConfig {
  /**
   * Loads zxing-wasm's reader binary (`zxing-wasm/reader/zxing_reader.wasm`,
   * version 3.1.3). Required where the browser has no QR-capable
   * BarcodeDetector; the SDK never downloads the binary itself.
   */
  loadZXingWasm?: () => Promise<ArrayBuffer>;
  /**
   * Creates a Worker running `@onekeyfe/react-native-qr-scanner/web/zxing-worker`.
   * When set, zxing decodes off the page thread; otherwise on it.
   */
  createZXingWorker?: () => ZXingWorkerLike;
  /** Skip the native BarcodeDetector even when present (tests, comparisons). */
  disableNativeDetector?: boolean;
}

let config: WebQrScannerConfig = {};
let pageZXingReady: Promise<void> | undefined;
let nativeDetector: Promise<QrImageEngine | null> | undefined;
let workerClient: ZXingWorkerClient | undefined;

/** Call before the first decode. Later calls replace the config. */
export function configureWebQrScanner(next: WebQrScannerConfig): void {
  config = { ...next };
  pageZXingReady = undefined;
  nativeDetector = undefined;
  workerClient?.dispose();
  workerClient = undefined;
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
        const results = await detector.detect(
          new ImageData(image.data, image.width, image.height)
        );
        return results.find((result) => result.rawValue)?.rawValue ?? null;
      },
    };
  } catch {
    return null;
  }
}

function requireLoader(): () => Promise<ArrayBuffer> {
  const { loadZXingWasm } = config;
  if (!loadZXingWasm) {
    throw new QrScannerError(
      'decoder-unavailable',
      'No QR-capable BarcodeDetector and no zxing-wasm loader configured'
    );
  }
  return loadZXingWasm;
}

function zxingUnavailable(error: unknown): QrScannerError {
  if (error instanceof QrScannerError) return error;
  return new QrScannerError(
    'decoder-unavailable',
    `zxing-wasm failed: ${error instanceof Error ? error.message : String(error)}`
  );
}

/**
 * One worker, requests answered in order. A worker that errors is dropped;
 * the next request starts a fresh one.
 */
class ZXingWorkerClient {
  private worker: ZXingWorkerLike;
  private nextId = 1;
  private pending = new Map<number, (response: ZXingWorkerResponse) => void>();
  private ready: Promise<void>;
  broken = false;

  constructor(create: () => ZXingWorkerLike, loadWasm: () => Promise<ArrayBuffer>) {
    this.worker = create();
    this.worker.onmessage = ({ data }) => {
      this.pending.get(data.id)?.(data);
      this.pending.delete(data.id);
    };
    this.worker.onerror = () => this.fail('zxing worker crashed');
    this.ready = loadWasm().then((wasm) =>
      this.request({ id: 0, type: 'init', wasm }, [wasm]).then(() => undefined)
    );
  }

  private fail(message: string) {
    this.broken = true;
    for (const resolve of this.pending.values()) resolve({ id: 0, ok: false, error: message });
    this.pending.clear();
  }

  private request(
    message: ZXingWorkerRequest,
    transfer: Transferable[]
  ): Promise<string | null> {
    const id = this.nextId++;
    return new Promise((resolve, reject) => {
      if (this.broken) {
        reject(new Error('zxing worker is unavailable'));
        return;
      }
      this.pending.set(id, (response) =>
        response.ok ? resolve(response.text) : reject(new Error(response.error))
      );
      this.worker.postMessage({ ...message, id }, transfer);
    });
  }

  async decode(image: RgbaImage): Promise<string | null> {
    await this.ready;
    // The same image feeds several passes and engines; send a copy.
    const data = image.data.slice();
    return this.request(
      { id: 0, type: 'decode', data, width: image.width, height: image.height },
      [data.buffer]
    );
  }

  dispose() {
    this.fail('zxing worker disposed');
    this.worker.terminate();
  }
}

export const zxingEngine: QrImageEngine = {
  name: 'zxing',
  async decode(image) {
    try {
      const loadWasm = requireLoader();
      if (config.createZXingWorker) {
        if (!workerClient || workerClient.broken) {
          workerClient?.dispose();
          workerClient = new ZXingWorkerClient(config.createZXingWorker, loadWasm);
        }
        return await workerClient.decode(image);
      }
      pageZXingReady ??= loadWasm().then(prepareZXingFromBinary);
      try {
        await pageZXingReady;
      } catch (error) {
        pageZXingReady = undefined;
        throw error;
      }
      return await readQrText(image.data, image.width, image.height);
    } catch (error) {
      throw zxingUnavailable(error);
    }
  },
};

/** Native detector first when available, zxing-wasm as fallback. */
export async function getEngines(): Promise<QrImageEngine[]> {
  nativeDetector ??= createNativeEngine();
  const native = await nativeDetector;
  if (native) return config.loadZXingWasm ? [native, zxingEngine] : [native];
  return [zxingEngine];
}
