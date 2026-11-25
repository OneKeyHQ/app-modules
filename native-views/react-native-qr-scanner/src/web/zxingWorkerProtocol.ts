import { prepareZXingModule, readBarcodes } from 'zxing-wasm/reader';

import type { QrDetectedSymbol } from '../symbols';

/** Messages between the page and the zxing worker. */
export type ZXingWorkerRequest =
  | { id: number; type: 'init'; wasm: ArrayBuffer }
  | {
      id: number;
      type: 'decode';
      data: Uint8ClampedArray<ArrayBuffer>;
      width: number;
      height: number;
      multiSymbol?: boolean;
    };

export type ZXingWorkerResponse =
  | { id: number; ok: true; text: string | null; symbols?: QrDetectedSymbol[] }
  | { id: number; ok: false; error: string };

/** zxing reader options shared by the page-thread and worker engines. */
export const ZXING_READER_OPTIONS = {
  formats: ['QRCode' as const],
  tryHarder: true,
  // The image pipeline has its own inverted pass.
  tryInvert: false,
  maxNumberOfSymbols: 1,
};

export const ZXING_MULTI_SYMBOL_LIMIT = 8;

export async function prepareZXingFromBinary(binary: ArrayBuffer): Promise<void> {
  await prepareZXingModule({
    overrides: { wasmBinary: binary },
    equalityFn: () => false,
    fireImmediately: true,
  });
}

function positionToSymbol(
  text: string,
  position:
    | {
        topLeft: { x: number; y: number };
        topRight: { x: number; y: number };
        bottomRight: { x: number; y: number };
        bottomLeft: { x: number; y: number };
      }
    | undefined,
  width: number,
  height: number
): QrDetectedSymbol {
  if (!position || width <= 0 || height <= 0) {
    return { text, centerX: 0.5, centerY: 0.5 };
  }
  const corners = [
    position.topLeft,
    position.topRight,
    position.bottomRight,
    position.bottomLeft,
  ];
  const xs = corners.map((p) => p.x);
  const ys = corners.map((p) => p.y);
  const minX = Math.min(...xs);
  const maxX = Math.max(...xs);
  const minY = Math.min(...ys);
  const maxY = Math.max(...ys);
  const centerX = xs.reduce((sum, x) => sum + x, 0) / (4 * width);
  const centerY = ys.reduce((sum, y) => sum + y, 0) / (4 * height);
  return {
    text,
    centerX,
    centerY,
    bounds: {
      x: minX / width,
      y: minY / height,
      width: Math.max(0, (maxX - minX) / width),
      height: Math.max(0, (maxY - minY) / height),
    },
  };
}

export async function readQrSymbols(
  data: Uint8ClampedArray<ArrayBuffer>,
  width: number,
  height: number,
  multiSymbol = false
): Promise<QrDetectedSymbol[]> {
  const results = await readBarcodes(new ImageData(data, width, height), {
    ...ZXING_READER_OPTIONS,
    maxNumberOfSymbols: multiSymbol ? ZXING_MULTI_SYMBOL_LIMIT : 1,
  });
  const symbols: QrDetectedSymbol[] = [];
  for (const result of results) {
    if (result.isValid && result.text) {
      symbols.push(positionToSymbol(result.text, result.position, width, height));
    }
  }
  return symbols;
}

export async function readQrText(
  data: Uint8ClampedArray<ArrayBuffer>,
  width: number,
  height: number
): Promise<string | null> {
  const symbols = await readQrSymbols(data, width, height, false);
  return symbols[0]?.text ?? null;
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
      if (request.multiSymbol === true) {
        const symbols = await readQrSymbols(
          request.data,
          request.width,
          request.height,
          true
        );
        return {
          id: request.id,
          ok: true,
          text: symbols[0]?.text ?? null,
          symbols,
        };
      }
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
