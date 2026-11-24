import { Blob as NodeBlob } from 'node:buffer';

import { decodeQrImage } from '../decodeQrImage';
import { configureWebQrScanner } from '../engines';
import { readImageDimensions } from '../imageDimensions';

function blob(bytes: number[]): Blob {
  return new NodeBlob([Uint8Array.from(bytes)]) as unknown as Blob;
}

/** A JPEG header with an optional orientation and one frame header. */
function jpeg(width: number, height: number, orientation = 1): Blob {
  return blob([
    0xff, 0xd8,
    // APP1, Exif, little-endian TIFF, one inline Orientation tag.
    0xff, 0xe1, 0, 34, 0x45, 0x78, 0x69, 0x66, 0, 0,
    0x49, 0x49, 42, 0, 8, 0, 0, 0,
    1, 0, 0x12, 1, 3, 0, 1, 0, 0, 0, orientation, 0, 0, 0,
    0, 0, 0, 0,
    0xff, 0xc0, 0, 11, 8, height >>> 8, height & 0xff,
    width >>> 8, width & 0xff, 1, 1, 0x11, 0,
    0xff, 0xda,
  ]);
}

afterEach(() => {
  Reflect.deleteProperty(globalThis, 'createImageBitmap');
  Reflect.deleteProperty(globalThis, 'OffscreenCanvas');
  Reflect.deleteProperty(globalThis, 'BarcodeDetector');
  Reflect.deleteProperty(globalThis, 'ImageData');
  configureWebQrScanner({});
});

describe('Web image dimension preflight', () => {
  it('reads JPEG size and EXIF orientation without decoding pixels', async () => {
    await expect(readImageDimensions(jpeg(800, 600, 6))).resolves.toEqual({
      width: 800, height: 600, swapAxes: true,
    });
  });

  it.each([
    { format: 'PNG', bytes: [
      0x89, 0x50, 0x4e, 0x47, 13, 10, 26, 10,
      0, 0, 0, 13, 0x49, 0x48, 0x44, 0x52,
      0, 0, 0, 16, 0, 0, 0, 24, 8, 2, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0x49, 0x45, 0x4e, 0x44, 0, 0, 0, 0,
    ] },
    { format: 'GIF', bytes: [0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 16, 0, 24, 0, 0, 0] },
    { format: 'BMP', bytes: [
      0x42, 0x4d, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      12, 0, 0, 0, 16, 0, 24, 0, 1, 0, 24, 0,
    ] },
    { format: 'WebP', bytes: [
      0x52, 0x49, 0x46, 0x46, 22, 0, 0, 0, 0x57, 0x45, 0x42, 0x50,
      0x56, 0x50, 0x38, 0x58, 10, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0, 23, 0, 0,
    ] },
  ])('reads $format dimensions from its headers', async ({ bytes }) => {
    await expect(readImageDimensions(blob(bytes))).resolves.toMatchObject({ width: 16, height: 24 });
  });

  it('stops scanning PNG metadata before a long run of image data chunks', async () => {
    const header = [
      0x89, 0x50, 0x4e, 0x47, 13, 10, 26, 10,
      0, 0, 0, 13, 0x49, 0x48, 0x44, 0x52,
      0, 0, 0, 16, 0, 0, 0, 24, 8, 2, 0, 0, 0, 0, 0, 0, 0,
    ];
    const idat = [0, 0, 0, 0, 0x49, 0x44, 0x41, 0x54, 0x35, 0xaf, 6, 0x1e];
    const end = [0, 0, 0, 0, 0x49, 0x45, 0x4e, 0x44, 0xae, 0x42, 0x60, 0x82];
    const input = blob([...header, ...Array.from({ length: 600 }, () => idat).flat(), ...end]);
    await expect(readImageDimensions(input)).resolves.toMatchObject({ width: 16, height: 24 });
  });

  it('keeps WebP aspect ratio when the browser ignores orientation metadata', async () => {
    const bytes = Buffer.from(
      'UklGRk4AAABXRUJQVlA4WAoAAAAIAAAAAQAAAAAAVlA4TA4AAAAvAQAAAAcQEf0PRET/A0VYSUYaAAAATU0AKgAAAAgAAQESAAMAAAABAAYAAAAAAAA=',
      'base64'
    );
    // Set canvas dimensions in the fixture header. Pixel decoding is mocked.
    bytes.writeUIntLE(799, 24, 3);
    bytes.writeUIntLE(599, 27, 3);
    const input = blob([...bytes]);
    const closeProbe = jest.fn();
    const create = jest.fn()
      .mockResolvedValueOnce({ width: 2, height: 1, close: closeProbe })
      .mockResolvedValue({ width: 400, height: 300, close: jest.fn() });
    Object.assign(globalThis, {
      createImageBitmap: create,
      Blob: NodeBlob,
      ImageData: class {},
      OffscreenCanvas: jest.fn(() => ({
        getContext: () => ({
          drawImage: jest.fn(),
          getImageData: () => ({ data: new Uint8ClampedArray(4) }),
        }),
      })),
      BarcodeDetector: class {
        static async getSupportedFormats() { return ['qr_code']; }
        async detect() { return [{ rawValue: 'test QR' }]; }
      },
    });
    await expect(decodeQrImage(input, { maxPixels: 120_000 })).resolves.toBe('test QR');
    await expect(decodeQrImage(input, { maxPixels: 120_000 })).resolves.toBe('test QR');
    expect(create).toHaveBeenCalledTimes(3);
    expect(create).toHaveBeenLastCalledWith(input, {
      imageOrientation: 'from-image', resizeWidth: 400, resizeHeight: 300, resizeQuality: 'high',
    });
    expect(closeProbe).toHaveBeenCalledTimes(1);
  });

  it('rejects oversized or unreadable headers before invoking createImageBitmap', async () => {
    const create = jest.fn();
    Object.assign(globalThis, { createImageBitmap: create });
    await expect(decodeQrImage(jpeg(20_000, 15_000))).rejects.toMatchObject({ code: 'resource-limit' });
    await expect(decodeQrImage(blob([0xff, 0xd8]))).rejects.toMatchObject({ code: 'invalid-image' });
    expect(create).not.toHaveBeenCalled();
  });

  it('requests budgeted bitmap output in EXIF-oriented dimensions', async () => {
    const close = jest.fn();
    const create = jest.fn().mockResolvedValue({ width: 300, height: 400, close });
    Object.assign(globalThis, {
      createImageBitmap: create,
      ImageData: class {},
      OffscreenCanvas: jest.fn(() => ({
        getContext: () => ({
          drawImage: jest.fn(),
          getImageData: () => ({ data: new Uint8ClampedArray(4) }),
        }),
      })),
      BarcodeDetector: class {
        static async getSupportedFormats() { return ['qr_code']; }
        async detect() { return [{ rawValue: 'test QR' }]; }
      },
    });
    const image = jpeg(800, 600, 6);
    await expect(decodeQrImage(image, { maxPixels: 120_000 })).resolves.toBe('test QR');
    expect(create).toHaveBeenCalledWith(image, {
      imageOrientation: 'from-image', resizeWidth: 300, resizeHeight: 400, resizeQuality: 'high',
    });
    expect(close).toHaveBeenCalledTimes(1);
  });
});
