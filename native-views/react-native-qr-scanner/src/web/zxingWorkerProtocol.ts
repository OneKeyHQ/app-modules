import { prepareZXingModule, readBarcodes } from 'zxing-wasm/reader';

/** Messages between the page and the zxing worker. */
export type ZXingWorkerRequest =
  | { id: number; type: 'init'; wasm: ArrayBuffer }
  | {
      id: number;
      type: 'decode';
      data: Uint8ClampedArray<ArrayBuffer>;
      width: number;
      height: number;
    };

export type ZXingWorkerResponse =
  | { id: number; ok: true; text: string | null }
  | { id: number; ok: false; error: string };

/** zxing reader options shared by the page-thread and worker engines. */
export const ZXING_READER_OPTIONS = {
  formats: ['QRCode' as const],
  tryHarder: true,
  // The image pipeline has its own inverted pass.
  tryInvert: false,
  maxNumberOfSymbols: 1,
};

export async function prepareZXingFromBinary(binary: ArrayBuffer): Promise<void> {
  await prepareZXingModule({
    overrides: { wasmBinary: binary },
    equalityFn: () => false,
    fireImmediately: true,
  });
}

export async function readQrText(
  data: Uint8ClampedArray<ArrayBuffer>,
  width: number,
  height: number
): Promise<string | null> {
  const results = await readBarcodes(new ImageData(data, width, height), ZXING_READER_OPTIONS);
  return results.find((result) => result.isValid && result.text)?.text ?? null;
}

/**
 * Worker-side message handling, kept free of `self` so it can be tested
 * directly. `decode` before a successful `init` is an error.
 */
export function createZXingWorkerHandler() {
  let ready: Promise<void> | undefined;
  return async (request: ZXingWorkerRequest): Promise<ZXingWorkerResponse> => {
    try {
      if (request.type === 'init') {
        ready = prepareZXingFromBinary(request.wasm);
        await ready;
        return { id: request.id, ok: true, text: null };
      }
      if (!ready) throw new Error('zxing worker used before init');
      await ready;
      return {
        id: request.id,
        ok: true,
        text: await readQrText(request.data, request.width, request.height),
      };
    } catch (error) {
      if (request.type === 'init') ready = undefined;
      return {
        id: request.id,
        ok: false,
        error: error instanceof Error ? error.message : String(error),
      };
    }
  };
}
