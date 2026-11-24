interface Dimensions {
  width: number;
  height: number;
  swapAxes?: boolean;
  orientationFormat?: 'png' | 'webp';
}
type Read = (offset: number, count: number) => Promise<DataView>;
const MAX_HEADERS = 512;
const MAX_EXIF_BYTES = 64 * 1024;

function exifSwapsAxes(view: DataView): boolean {
  const tiff = view.byteLength >= 6 && view.getUint32(0) === 0x45786966 &&
    view.getUint16(4) === 0 ? 6 : 0;
  if (view.byteLength < tiff + 8) return false;
  const order = view.getUint16(tiff);
  if (order !== 0x4949 && order !== 0x4d4d) return false;
  const little = order === 0x4949;
  if (view.getUint16(tiff + 2, little) !== 42) return false;
  const ifd = tiff + view.getUint32(tiff + 4, little);
  if (ifd + 2 > view.byteLength) return false;
  const count = Math.min(view.getUint16(ifd, little), Math.floor((view.byteLength - ifd - 2) / 12));
  for (let i = 0; i < count; i++) {
    const offset = ifd + 2 + i * 12;
    if (view.getUint16(offset, little) === 0x0112 &&
        view.getUint16(offset + 2, little) === 3 &&
        view.getUint32(offset + 4, little) === 1) {
      const orientation = view.getUint16(offset + 8, little);
      return orientation >= 5 && orientation <= 8;
    }
  }
  return false;
}

async function jpegDimensions(read: Read): Promise<Dimensions> {
  let offset = 2;
  let dimensions: Dimensions | undefined;
  let swapAxes = false;
  for (let count = 0; count < MAX_HEADERS; count++) {
    const marker = await read(offset, 2);
    if (marker.getUint8(0) !== 0xff) break;
    const code = marker.getUint8(1);
    if (code === 0xff) {
      offset++;
      continue;
    }
    if (code === 0xda) {
      if (!dimensions) break;
      return { ...dimensions, swapAxes };
    }
    if (code === 0x00 || code === 0xd8 || code === 0xd9) break;
    offset += 2;
    const length = (await read(offset, 2)).getUint16(0);
    if (length < 2) break;
    if (code === 0xe1 && length >= 8) {
      swapAxes ||= exifSwapsAxes(await read(offset + 2, length - 2));
    }
    if (code >= 0xc0 && code <= 0xcf && ![0xc4, 0xc8, 0xcc].includes(code)) {
      if (length < 8 || dimensions) break;
      const size = await read(offset + 3, 4);
      dimensions = { width: size.getUint16(2), height: size.getUint16(0) };
    }
    offset += length;
  }
  throw new Error('invalid JPEG header');
}

async function chunkExif(read: Read, offset: number, end: number, png: boolean): Promise<boolean> {
  const exif = png ? 0x65584966 : 0x45584946;
  const headerSize = png ? 12 : 8;
  for (let count = 0; offset < end && count < MAX_HEADERS; count++) {
    const chunk = await read(offset, 8);
    const type = chunk.getUint32(png ? 4 : 0);
    const length = chunk.getUint32(png ? 0 : 4, !png);
    const next = offset + headerSize + length + (png ? 0 : length % 2);
    if (next > end) throw new Error('truncated image chunk');
    if (type === exif) {
      return exifSwapsAxes(await read(offset + 8, Math.min(length, MAX_EXIF_BYTES)));
    }
    // PNG metadata preceding IDAT contains the orientation. Image data can
    // be split across arbitrarily many IDAT chunks.
    if (png && (type === 0x49444154 || type === 0x49454e44)) return false;
    offset = next;
  }
  if (offset < end) throw new Error('too many image chunks');
  return false;
}

async function webpDimensions(read: Read, size: number): Promise<Dimensions> {
  const header = await read(0, 20);
  const end = header.getUint32(4, true) + 8;
  const length = header.getUint32(16, true);
  if (end > size || 20 + length + length % 2 > end) throw new Error('truncated WebP');
  switch (header.getUint32(12)) {
    case 0x56503858: { // VP8X
      if (length !== 10) break;
      const data = await read(20, 10);
      const width = 1 + data.getUint8(4) + data.getUint16(5, true) * 256;
      const height = 1 + data.getUint8(7) + data.getUint16(8, true) * 256;
      const swapAxes = (data.getUint8(0) & 0x08) !== 0 &&
        await chunkExif(read, 30, end, false);
      return { width, height, swapAxes, orientationFormat: 'webp' };
    }
    case 0x5650384c: { // VP8L
      if (length < 5) break;
      const data = await read(20, 5);
      if (data.getUint8(0) !== 0x2f) break;
      const bits = data.getUint32(1, true);
      return { width: 1 + (bits & 0x3fff), height: 1 + ((bits >>> 14) & 0x3fff) };
    }
    case 0x56503820: { // VP8
      if (length < 10) break;
      const data = await read(20, 10);
      if ((data.getUint8(0) & 1) !== 0 || data.getUint8(3) !== 0x9d || data.getUint16(4) !== 0x012a) break;
      return { width: data.getUint16(6, true) & 0x3fff, height: data.getUint16(8, true) & 0x3fff };
    }
  }
  throw new Error('invalid WebP header');
}

/** Read only image headers. Pixel decoding must wait until the caller checks the dimensions. */
export async function readImageDimensions(blob: Blob): Promise<Dimensions> {
  const read: Read = async (offset, count) => {
    if (offset + count > blob.size) throw new Error('truncated image header');
    return new DataView(await blob.slice(offset, offset + count).arrayBuffer());
  };
  const signature = await read(0, 12);
  if (signature.getUint16(0) === 0xffd8) return jpegDimensions(read);

  const type = signature.getUint32(0);
  if (type === 0x89504e47 && signature.getUint32(4) === 0x0d0a1a0a) {
    const header = await read(8, 25);
    if (header.getUint32(0) !== 13 || header.getUint32(4) !== 0x49484452) {
      throw new Error('invalid PNG header');
    }
    return {
      width: header.getUint32(8),
      height: header.getUint32(12),
      swapAxes: await chunkExif(read, 33, blob.size, true),
      orientationFormat: 'png',
    };
  }
  if (type === 0x52494646 && signature.getUint32(8) === 0x57454250) {
    return webpDimensions(read, blob.size);
  }
  if (type === 0x47494638 && [0x3761, 0x3961].includes(signature.getUint16(4))) {
    return { width: signature.getUint16(6, true), height: signature.getUint16(8, true) };
  }
  if (signature.getUint16(0) === 0x424d) {
    const dib = await read(14, 12);
    const length = dib.getUint32(0, true);
    if (length === 12) return { width: dib.getUint16(4, true), height: dib.getUint16(6, true) };
    if (length >= 40 && length <= blob.size - 14) {
      return { width: dib.getInt32(4, true), height: Math.abs(dib.getInt32(8, true)) };
    }
  }
  throw new Error('unsupported image header');
}
