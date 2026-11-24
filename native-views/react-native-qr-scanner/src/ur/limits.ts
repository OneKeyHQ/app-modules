// Not re-exported by the package root; pinned with the dependency version.
import bytewords from '@ngraveio/bc-ur/dist/bytewords.js';

import type { UrLimits } from './types';

// Native Node ESM exposes the CommonJS module object as its default.
const urWords = (bytewords as { default?: typeof bytewords }).default ?? bytewords;

export const DEFAULT_UR_LIMITS: Readonly<UrLimits> = Object.freeze({
  maxMessageBytes: 1024 * 1024,
  // The fountain decoder's work per mixed frame grows with the square of the
  // part count; a real code with this many parts would take an hour to scan.
  maxSeqLength: 32_768,
  maxCborDepth: 64,
  maxDistinctParts: 65_536,
  maxRetainedBytes: 8 * 1024 * 1024,
});

export function resolveLimits(limits?: Partial<UrLimits>): UrLimits {
  const resolved = { ...DEFAULT_UR_LIMITS, ...limits };
  for (const [key, value] of Object.entries(resolved)) {
    if (!Number.isSafeInteger(value) || value < 1) {
      throw new RangeError(`UR limit ${key} must be a positive integer`);
    }
  }
  return resolved;
}

/**
 * Largest CBOR payload one fountain frame may carry. A version 40 QR symbol
 * holds under 3 KB, so 16 KiB leaves room for any decoder without letting a
 * single frame grow unbounded.
 */
const MAX_FRAME_PAYLOAD_BYTES = 16 * 1024;

/** Argument bytes for additional info 24, 25, 26 and 27. */
const ARGUMENT_BYTES = [1, 2, 4, 8] as const;

export class MalformedPayloadError extends Error {
  override name = 'MalformedPayloadError';
}

function malformed(reason: string): MalformedPayloadError {
  return new MalformedPayloadError(`Malformed QR payload: ${reason}`);
}

function argumentSize(info: number): number {
  const size = ARGUMENT_BYTES[info - 24];
  if (size === undefined) throw malformed('reserved length');
  return size;
}

/**
 * Walks CBOR before any vendor decoder sees it. Vendor decoders read past the
 * end as zero and trust declared lengths, so one indefinite-length header or a
 * huge declared count exhausts memory. Accepts only definite lengths that fit
 * the bytes present, bounded nesting, and no trailing bytes.
 */
export function assertBoundedCbor(
  input: Uint8Array,
  limits: Pick<UrLimits, 'maxMessageBytes' | 'maxCborDepth'>
): void {
  if (input.length === 0) throw malformed('empty');
  if (input.length > limits.maxMessageBytes) throw malformed('too large');
  let offset = 0;

  const readArgument = (info: number): number => {
    if (info < 24) return info;
    const size = argumentSize(info);
    if (offset + size > input.length) throw malformed('truncated length');
    let value = 0;
    for (let i = 0; i < size; i += 1) value = value * 256 + input[offset + i]!;
    offset += size;
    return value;
  };

  const walkItem = (depth: number): void => {
    if (depth > limits.maxCborDepth) throw malformed('nested too deeply');
    if (offset >= input.length) throw malformed('truncated item');
    const initial = input[offset]!;
    offset += 1;
    const major = initial >> 5;
    const info = initial & 0x1f;
    // 31 is an indefinite length (or a stray break); 28-30 are reserved.
    if (info >= 28) throw malformed('indefinite or reserved length');
    const argument = readArgument(info);
    const remaining = input.length - offset;
    if (major === 2 || major === 3) {
      if (argument > remaining) throw malformed('string longer than payload');
      offset += argument;
    } else if (major === 4 || major === 5) {
      // Every element takes at least one byte; a map holds two per entry.
      const items = major === 5 ? argument * 2 : argument;
      if (items > remaining) throw malformed('container longer than payload');
      for (let i = 0; i < items; i += 1) walkItem(depth + 1);
    } else if (major === 6) {
      walkItem(depth + 1);
    }
    // Major 0/1 integers and 7 simple values carry only the argument read.
  };

  walkItem(0);
  if (offset !== input.length) throw malformed('trailing bytes');
}

export interface FountainHeader {
  seqNum: number;
  seqLength: number;
  messageLength: number;
  checksum: number;
  fragmentLength: number;
}

/**
 * Reads `[seqNum, seqLen, messageLen, checksum, fragment]`. The input has
 * passed `assertBoundedCbor`, so every declared length fits in it.
 */
function readFountainHeader(input: Uint8Array): FountainHeader & { fragmentOffset: number } {
  let offset = 0;
  const readHead = (expectedMajor: number): number => {
    const initial = input[offset]!;
    offset += 1;
    if (initial >> 5 !== expectedMajor) throw malformed('not a fountain part');
    const info = initial & 0x1f;
    if (info < 24) return info;
    const size = argumentSize(info);
    let value = 0;
    for (let i = 0; i < size; i += 1) value = value * 256 + input[offset + i]!;
    offset += size;
    return value;
  };
  if (readHead(4) !== 5) throw malformed('not a fountain part');
  const seqNum = readHead(0);
  const seqLength = readHead(0);
  const messageLength = readHead(0);
  const checksum = readHead(0);
  const fragmentLength = readHead(2);
  return { seqNum, seqLength, messageLength, checksum, fragmentLength, fragmentOffset: offset };
}

export type ParsedUrPart =
  | { verdict: 'malformed' }
  | { verdict: 'too-large'; type: string; header?: FountainHeader }
  | { verdict: 'single'; type: string; payload: Uint8Array }
  | { verdict: 'part'; type: string; header: FountainHeader; fragment: Uint8Array };

const UR_PART_PATTERN =
  /^ur:([a-z0-9-]+)\/(?:(\d{1,10})-(\d{1,9})\/)?([a-z]+)$/;

/**
 * Judges one UR part (already trimmed and lower-cased) before the fountain
 * decoder parses it. A malformed frame (misread, bad CRC, forged header) is
 * dropped; a too-large one belongs to a code beyond the limits.
 */
export function parseUrPart(part: string, limits: UrLimits): ParsedUrPart {
  const match = UR_PART_PATTERN.exec(part);
  if (!match) return { verdict: 'malformed' };
  const [, type, textSeqNum, textSeqLength, words] = match;
  if (textSeqNum !== undefined && words!.length > (MAX_FRAME_PAYLOAD_BYTES + 4) * 2) {
    return { verdict: 'malformed' };
  }
  let payload: Uint8Array;
  try {
    // Minimal-style UR words carry their own CRC32; decode throws on mismatch.
    payload = hexToBytes(urWords.decode(words!, urWords.STYLES.MINIMAL));
  } catch {
    return { verdict: 'malformed' };
  }
  if (textSeqNum === undefined) {
    if (payload.length > limits.maxMessageBytes) return { verdict: 'too-large', type: type! };
    try {
      assertBoundedCbor(payload, limits);
    } catch {
      return { verdict: 'malformed' };
    }
    return { verdict: 'single', type: type!, payload };
  }
  let header: ReturnType<typeof readFountainHeader>;
  try {
    // A fountain part is one frame, not the message; its own size is bounded
    // by what a QR symbol can hold, and the message size by its header below.
    assertBoundedCbor(payload, {
      maxMessageBytes: MAX_FRAME_PAYLOAD_BYTES,
      maxCborDepth: limits.maxCborDepth,
    });
    header = readFountainHeader(payload);
  } catch {
    return { verdict: 'malformed' };
  }
  // The text header, the CBOR header and the real sizes must agree, so the
  // part count a frame declares is tied to the data it actually carries.
  if (
    header.seqNum !== Number(textSeqNum) ||
    header.seqLength !== Number(textSeqLength) ||
    header.seqNum < 1 ||
    header.seqNum > 0xffff_ffff ||
    header.checksum > 0xffff_ffff ||
    header.fragmentLength < 1 ||
    header.messageLength < 1 ||
    header.seqLength !== Math.ceil(header.messageLength / header.fragmentLength)
  ) {
    return { verdict: 'malformed' };
  }
  if (
    header.messageLength > limits.maxMessageBytes ||
    header.seqLength > limits.maxSeqLength
  ) {
    return { verdict: 'too-large', type: type!, header };
  }
  return { verdict: 'part', type: type!, header, fragment: payload.subarray(header.fragmentOffset) };
}

function hexToBytes(hex: string): Uint8Array {
  if (hex.length % 2 !== 0 || /[^0-9a-f]/i.test(hex)) {
    throw malformed('bad hex');
  }
  const bytes = new Uint8Array(hex.length / 2);
  for (let i = 0; i < bytes.length; i += 1) {
    bytes[i] = parseInt(hex.slice(i * 2, i * 2 + 2), 16);
  }
  return bytes;
}
