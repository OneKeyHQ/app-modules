import { QrScannerError } from '../errors';
import { prepareZXingFromBinary, readQrText } from './zxingWorkerProtocol';

import type { QrImageEngine, RgbaImage } from './imageOps';
import type { ZXingWorkerRequest, ZXingWorkerResponse } from './zxingWorkerProtocol';

/** The part of `Worker` the SDK uses. */
export interface ZXingWorkerLike {
  postMessage(message: ZXingWorkerRequest, transfer: Transferable[]): void;
  onmessage: ((event: MessageEvent<ZXingWorkerResponse>) => void) | null;
  onerror: ((event: ErrorEvent) => void) | null;
  onmessageerror?: Worker['onmessageerror'];
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
const WORKER_DEADLINE_MS = 10_000;

class ZXingWorkerClient {
  private worker: ZXingWorkerLike;
  private nextId = 1;
  private pending = new Map<number, (response: ZXingWorkerResponse) => void>();
  private ready: Promise<void>;
  private deadlines = new Set<(reason: string) => void>();
  broken = false;

  constructor(create: () => ZXingWorkerLike, loadWasm: () => Promise<ArrayBuffer>) {
    this.worker = create();
    this.worker.onmessage = ({ data }: MessageEvent<ZXingWorkerResponse>) => {
      this.pending.get(data.id)?.(data);
      this.pending.delete(data.id);
    };
    this.worker.onerror = () => this.fail('zxing worker crashed');
    this.worker.onmessageerror = () => this.fail('zxing worker response could not be read');
    const initialize = Promise.resolve()
      .then(loadWasm)
      .then(async (wasm) => {
        if (this.broken) return;
        // The loader may cache its ArrayBuffer. Transfer only our own copy.
        const bytes = wasm.slice(0);
        await this.request({ id: 0, type: 'init', wasm: bytes }, [bytes]);
      });
    this.ready = this.withDeadline(initialize, 'zxing worker initialization timed out')
      .catch((error: unknown) => {
        this.dispose();
        throw error;
      });
  }

  private withDeadline<T>(operation: Promise<T>, message: string): Promise<T> {
    let timer: ReturnType<typeof setTimeout>;
    let cancel: (reason: string) => void;
    const settled = new Promise<T>((resolve, reject) => {
      cancel = (reason) => reject(new Error(reason));
      this.deadlines.add(cancel);
      timer = setTimeout(() => this.fail(message), WORKER_DEADLINE_MS);
      void operation.then(resolve, reject);
    });
    return settled.finally(() => {
      clearTimeout(timer);
      this.deadlines.delete(cancel);
    });
  }

  private fail(message: string) {
    if (this.broken) return;
    this.broken = true;
    for (const cancel of this.deadlines) cancel(message);
    this.deadlines.clear();
    for (const resolve of this.pending.values()) resolve({ id: 0, ok: false, error: message });
    this.pending.clear();
    this.worker.terminate();
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
      try {
        this.worker.postMessage({ ...message, id }, transfer);
      } catch (error) {
        this.pending.delete(id);
        reject(error);
      }
    });
  }

  async decode(image: RgbaImage): Promise<string | null> {
    await this.ready;
    // The same image feeds several passes and engines; send a copy.
    const data = image.data.slice();
    return this.withDeadline(
      this.request(
        { id: 0, type: 'decode', data, width: image.width, height: image.height },
        [data.buffer]
      ),
      'zxing worker decode timed out'
    );
  }

  dispose() {
    this.fail('zxing worker disposed');
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
      const ready = (pageZXingReady ??= loadWasm().then(prepareZXingFromBinary));
      try {
        await ready;
      } catch (error) {
        if (pageZXingReady === ready) pageZXingReady = undefined;
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
  requireLoader();
  return [zxingEngine];
}
