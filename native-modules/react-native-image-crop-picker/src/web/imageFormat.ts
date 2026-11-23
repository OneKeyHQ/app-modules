// Identifies an image format from its leading bytes, so a preserved original
// reports the MIME type of the data it returns rather than what the browser
// guessed from the file name (or nothing, when `File.type` is empty).

// Enough for every signature below; ISO BMFF brands sit at bytes 8...11.
export const IMAGE_HEADER_LENGTH = 32;

function startsWith(bytes: Uint8Array, signature: number[], offset = 0) {
  if (bytes.length < offset + signature.length) {
    return false;
  }
  return signature.every((byte, index) => bytes[offset + index] === byte);
}

function ascii(bytes: Uint8Array, start: number, end: number): string {
  return String.fromCharCode(...bytes.subarray(start, end));
}

// HEIF-family brands, from the ISO BMFF `ftyp` box.
const HEIC_BRANDS = new Set(['heic', 'heix', 'heim', 'heis', 'hevc', 'hevx']);
const HEIF_BRANDS = new Set(['mif1', 'msf1', 'hevm', 'hevs']);
const AVIF_BRANDS = new Set(['avif', 'avis']);

export function detectImageMime(bytes: Uint8Array): string | undefined {
  if (startsWith(bytes, [0xff, 0xd8, 0xff])) {
    return 'image/jpeg';
  }
  if (startsWith(bytes, [0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a])) {
    return 'image/png';
  }
  if (ascii(bytes, 0, 6) === 'GIF87a' || ascii(bytes, 0, 6) === 'GIF89a') {
    return 'image/gif';
  }
  if (ascii(bytes, 0, 4) === 'RIFF' && ascii(bytes, 8, 12) === 'WEBP') {
    return 'image/webp';
  }
  if (ascii(bytes, 4, 8) === 'ftyp') {
    const brand = ascii(bytes, 8, 12);
    if (HEIC_BRANDS.has(brand)) {
      return 'image/heic';
    }
    if (HEIF_BRANDS.has(brand)) {
      return 'image/heif';
    }
    if (AVIF_BRANDS.has(brand)) {
      return 'image/avif';
    }
    return undefined;
  }
  if (ascii(bytes, 0, 2) === 'BM') {
    return 'image/bmp';
  }
  if (
    startsWith(bytes, [0x49, 0x49, 0x2a, 0x00]) ||
    startsWith(bytes, [0x4d, 0x4d, 0x00, 0x2a])
  ) {
    return 'image/tiff';
  }
  if (startsWith(bytes, [0x00, 0x00, 0x01, 0x00])) {
    return 'image/x-icon';
  }
  return undefined;
}
