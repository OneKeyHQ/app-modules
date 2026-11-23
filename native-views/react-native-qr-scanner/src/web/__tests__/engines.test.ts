import { configureWebQrScanner, getEngines, zxingEngine } from '../engines';

jest.mock('zxing-wasm/reader', () => ({
  prepareZXingModule: jest.fn(async () => ({})),
  readBarcodes: jest.fn(async () => [{ isValid: true, text: 'decoded' }]),
}));

const image = { data: new Uint8ClampedArray(4), width: 1, height: 1 };

beforeAll(() => {
  // jsdom has no ImageData.
  (globalThis as { ImageData?: unknown }).ImageData = class {
    constructor(
      readonly data: Uint8ClampedArray,
      readonly width: number,
      readonly height: number
    ) {}
  };
});

afterEach(() => {
  delete (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector;
  configureWebQrScanner({});
});

describe('web engines', () => {
  it('never loads zxing without a configured loader', async () => {
    configureWebQrScanner({});
    await expect(zxingEngine.decode(image)).rejects.toMatchObject({
      code: 'decoder-unavailable',
    });
  });

  it('uses the configured loader once and reads QR codes only', async () => {
    const { readBarcodes, prepareZXingModule } = jest.requireMock('zxing-wasm/reader');
    const loadZXingWasm = jest.fn(async () => new ArrayBuffer(8));
    configureWebQrScanner({ loadZXingWasm });
    expect(await zxingEngine.decode(image)).toBe('decoded');
    expect(await zxingEngine.decode(image)).toBe('decoded');
    expect(loadZXingWasm).toHaveBeenCalledTimes(1);
    expect(prepareZXingModule).toHaveBeenCalledTimes(1);
    expect(readBarcodes).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({ formats: ['QRCode'], tryInvert: false })
    );
  });

  it('prefers a QR-capable BarcodeDetector, with zxing as fallback', async () => {
    (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector = Object.assign(
      class {
        async detect() {
          return [{ rawValue: 'native' }];
        }
      },
      { getSupportedFormats: async () => ['qr_code'] }
    );
    configureWebQrScanner({ loadZXingWasm: async () => new ArrayBuffer(8) });
    const engines = await getEngines();
    expect(engines.map((engine) => engine.name)).toEqual(['native', 'zxing']);
    expect(await engines[0]!.decode(image)).toBe('native');
  });

  it('ignores a BarcodeDetector without qr_code and honours disableNativeDetector', async () => {
    (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector = Object.assign(
      class {},
      { getSupportedFormats: async () => ['ean_13'] }
    );
    configureWebQrScanner({ loadZXingWasm: async () => new ArrayBuffer(8) });
    expect((await getEngines()).map((engine) => engine.name)).toEqual(['zxing']);

    (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector = Object.assign(
      class {},
      { getSupportedFormats: async () => ['qr_code'] }
    );
    configureWebQrScanner({
      loadZXingWasm: async () => new ArrayBuffer(8),
      disableNativeDetector: true,
    });
    expect((await getEngines()).map((engine) => engine.name)).toEqual(['zxing']);
  });
});
