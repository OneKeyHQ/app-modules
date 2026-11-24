import { configureWebQrScanner, zxingEngine } from '../engines';
import { createZXingWorkerHandler } from '../zxingWorkerProtocol';

import type { ZXingWorkerLike } from '../engines';
import type { ZXingWorkerRequest, ZXingWorkerResponse } from '../zxingWorkerProtocol';

jest.mock('zxing-wasm/reader', () => ({
  prepareZXingModule: jest.fn(async () => ({})),
  readBarcodes: jest.fn(async () => [{ isValid: true, text: 'from-zxing' }]),
}));

beforeAll(() => {
  (globalThis as { ImageData?: unknown }).ImageData = class {
    constructor(
      readonly data: Uint8ClampedArray,
      readonly width: number,
      readonly height: number
    ) {}
  };
});

afterEach(() => {
  configureWebQrScanner({});
  jest.useRealTimers();
});

const image = () => ({ data: new Uint8ClampedArray(16), width: 2, height: 2 });

/** Runs the real worker handler in-process, like a Worker would. */
function inProcessWorker(options: { crashOnDecode?: boolean; silent?: ZXingWorkerRequest['type'] } = {}) {
  const handle = createZXingWorkerHandler();
  const sent: { message: ZXingWorkerRequest; transfer?: Transferable[] }[] = [];
  const worker: ZXingWorkerLike = {
    onmessage: null,
    onerror: null,
    terminate: jest.fn(),
    postMessage(message, transfer) {
      sent.push({ message, transfer });
      if (options.silent === message.type) return;
      if (options.crashOnDecode && message.type === 'decode') {
        setTimeout(() => worker.onerror?.(Object.assign(new Event('error'), { message: 'boom', filename: '', lineno: 0, colno: 0, error: null })));
        return;
      }
      void handle(message).then((response: ZXingWorkerResponse) =>
        worker.onmessage?.(new MessageEvent('message', { data: response }))
      );
    },
  };
  return { worker, sent };
}

// Compile the public factory with the same native Worker used in the README.
const nativeWorkerFactory: NonNullable<import('../engines').WebQrScannerConfig['createZXingWorker']> =
  () => new Worker('qr-worker.js');
void nativeWorkerFactory;

describe('zxing worker handler', () => {
  it('refuses to decode before init and decodes after it', async () => {
    const handle = createZXingWorkerHandler();
    const decode = { id: 1, type: 'decode' as const, data: new Uint8ClampedArray(4), width: 1, height: 1 };
    expect(await handle(decode)).toEqual({ id: 1, ok: false, error: 'zxing worker used before init' });
    expect(await handle({ id: 2, type: 'init', wasm: new ArrayBuffer(8) })).toEqual({ id: 2, ok: true, text: null });
    expect(await handle({ ...decode, id: 3 })).toEqual({ id: 3, ok: true, text: 'from-zxing' });
  });

  it('reports an init failure and allows a later init', async () => {
    const { prepareZXingModule } = jest.requireMock('zxing-wasm/reader');
    prepareZXingModule.mockRejectedValueOnce(new Error('bad wasm'));
    const handle = createZXingWorkerHandler();
    expect(await handle({ id: 1, type: 'init', wasm: new ArrayBuffer(8) })).toEqual({ id: 1, ok: false, error: 'bad wasm' });
    expect((await handle({ id: 2, type: 'init', wasm: new ArrayBuffer(8) })).ok).toBe(true);
  });
});

describe('worker-backed zxing engine', () => {
  it('decodes through the worker and transfers a copy of the pixels', async () => {
    const { worker, sent } = inProcessWorker();
    const createZXingWorker = jest.fn(() => worker);
    configureWebQrScanner({ loadZXingWasm: async () => new ArrayBuffer(8), createZXingWorker });
    const input = image();
    expect(await zxingEngine.decode(input)).toBe('from-zxing');
    expect(await zxingEngine.decode(input)).toBe('from-zxing');
    expect(createZXingWorker).toHaveBeenCalledTimes(1);
    expect(sent.map((entry) => entry.message.type)).toEqual(['init', 'decode', 'decode']);
    const decodeMessage = sent[1]!.message as Extract<ZXingWorkerRequest, { type: 'decode' }>;
    expect(sent[1]!.transfer).toEqual([decodeMessage.data.buffer]);
    // The caller's buffer is not the one transferred away.
    expect(decodeMessage.data.buffer).not.toBe(input.data.buffer);
    expect(input.data.byteLength).toBe(16);
  });

  it('retries an initialization failure with a new Worker and binary', async () => {
    const failed = inProcessWorker();
    const healthy = inProcessWorker();
    const createZXingWorker = jest.fn()
      .mockReturnValueOnce(failed.worker)
      .mockReturnValueOnce(healthy.worker);
    const loadZXingWasm = jest.fn()
      .mockRejectedValueOnce(new Error('asset temporarily unavailable'))
      .mockResolvedValueOnce(new ArrayBuffer(8));
    configureWebQrScanner({ loadZXingWasm, createZXingWorker });
    await expect(zxingEngine.decode(image())).rejects.toMatchObject({ code: 'decoder-unavailable' });
    await expect(zxingEngine.decode(image())).resolves.toBe('from-zxing');
    expect(loadZXingWasm).toHaveBeenCalledTimes(2);
    expect(failed.worker.terminate).toHaveBeenCalledTimes(1);
  });

  it.each(['init', 'decode'] as const)('replaces a Worker that stops answering %s requests', async (silent) => {
    jest.useFakeTimers();
    const failed = inProcessWorker({ silent });
    const healthy = inProcessWorker();
    const createZXingWorker = jest.fn()
      .mockReturnValueOnce(failed.worker)
      .mockReturnValueOnce(healthy.worker);
    configureWebQrScanner({ loadZXingWasm: async () => new ArrayBuffer(8), createZXingWorker });
    const pending = [zxingEngine.decode(image()), zxingEngine.decode(image())].map((promise) =>
      expect(promise).rejects.toMatchObject({ code: 'decoder-unavailable', message: expect.stringContaining('timed out') })
    );
    await jest.advanceTimersByTimeAsync(10_000);
    await Promise.all(pending);
    expect(failed.worker.terminate).toHaveBeenCalledTimes(1);
    await expect(zxingEngine.decode(image())).resolves.toBe('from-zxing');
    expect(jest.getTimerCount()).toBe(0);
  });

  it('includes the WASM loader in the initialization deadline', async () => {
    jest.useFakeTimers();
    const worker = inProcessWorker();
    configureWebQrScanner({
      loadZXingWasm: () => new Promise(() => {}),
      createZXingWorker: () => worker.worker,
    });
    const pending = expect(zxingEngine.decode(image())).rejects.toMatchObject({
      code: 'decoder-unavailable', message: expect.stringContaining('initialization timed out'),
    });
    await jest.advanceTimersByTimeAsync(10_000);
    await pending;
    expect(worker.sent).toEqual([]);
    expect(worker.worker.terminate).toHaveBeenCalledTimes(1);
  });

  it('turns a crashed worker into decoder-unavailable and starts a new one next time', async () => {
    const crashing = inProcessWorker({ crashOnDecode: true });
    const healthy = inProcessWorker();
    const createZXingWorker = jest
      .fn()
      .mockReturnValueOnce(crashing.worker)
      .mockReturnValueOnce(healthy.worker);
    configureWebQrScanner({ loadZXingWasm: async () => new ArrayBuffer(8), createZXingWorker });
    await expect(zxingEngine.decode(image())).rejects.toMatchObject({ code: 'decoder-unavailable' });
    expect(await zxingEngine.decode(image())).toBe('from-zxing');
    expect(createZXingWorker).toHaveBeenCalledTimes(2);
    expect(crashing.worker.terminate).toHaveBeenCalled();
  });
});
