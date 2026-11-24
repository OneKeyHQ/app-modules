import { FountainAssembly, FountainLimitError, FountainPayloadError } from './fountain';

import {
  MalformedPayloadError,
  assertBoundedCbor,
  parseUrPart,
  resolveLimits,
} from './limits';

import type { FountainHeader } from './limits';
import type {
  ScanErrorCode,
  ScanReceiveResult,
  ScanStats,
  UrAssembler,
  UrLimits,
} from './types';

type MessageBinding = FountainHeader & { type: string };

function sameBinding(binding: MessageBinding, type: string, header: FountainHeader): boolean {
  return binding.type === type &&
    binding.seqLength === header.seqLength &&
    binding.messageLength === header.messageLength &&
    binding.checksum === header.checksum;
}

export function createUrAssembler(options?: {
  limits?: Partial<UrLimits>;
}): UrAssembler {
  const limits = resolveLimits(options?.limits);

  let binding: MessageBinding | undefined;
  let decoder: FountainAssembly | undefined;
  let seenSeqNums = new Set<number>();
  let finished = false;
  let stats: ScanStats = { accepted: 0, duplicate: 0, malformed: 0, foreign: 0 };

  const ignore = (
    reason: 'duplicate' | 'malformed' | 'foreign'
  ): ScanReceiveResult => {
    stats[reason] += 1;
    return { status: 'ignored', reason };
  };

  const reject = (code: ScanErrorCode, message: string): ScanReceiveResult => {
    finished = true;
    decoder = undefined;
    seenSeqNums = new Set();
    return { status: 'rejected', error: { code, message } };
  };

  const complete = (type: string, cbor: Uint8Array): ScanReceiveResult => {
    try {
      assertBoundedCbor(cbor, limits);
    } catch (error) {
      return reject(
        'invalid-payload',
        error instanceof MalformedPayloadError ? error.message : 'Invalid UR'
      );
    }
    finished = true;
    decoder = undefined;
    seenSeqNums = new Set();
    // Copy so the result never aliases a vendor Buffer.
    return {
      status: 'complete',
      result: { kind: 'ur', type, cbor: Uint8Array.from(cbor) },
    };
  };

  const receive = (frameText: string): ScanReceiveResult => {
    if (finished) return { status: 'ignored', reason: 'finished' };
    const part = frameText.trim().toLowerCase();
    if (!part.startsWith('ur:')) return { status: 'ignored', reason: 'not-ur' };

    const parsed = parseUrPart(part, limits);
    if (parsed.verdict === 'malformed') return ignore('malformed');
    if (binding && (!('header' in parsed) || !parsed.header ||
        !sameBinding(binding, parsed.type, parsed.header))) return ignore('foreign');
    if (parsed.verdict === 'too-large') {
      return reject(
        'too-large',
        `QR code is too large to scan (over ${limits.maxMessageBytes} bytes or ${limits.maxSeqLength} parts)`
      );
    }
    if (parsed.verdict === 'single') {
      stats.accepted += 1;
      return complete(parsed.type, parsed.payload);
    }

    const { type, header, fragment } = parsed;
    if (binding && binding.fragmentLength !== header.fragmentLength) return ignore('malformed');
    if (seenSeqNums.has(header.seqNum)) return ignore('duplicate');
    if (seenSeqNums.size >= limits.maxDistinctParts) {
      return reject('resource-limit', 'Too many distinct UR parts');
    }

    try {
      decoder ??= new FountainAssembly(header, limits.maxRetainedBytes);
      const message = decoder.receive(header.seqNum, fragment);
      binding ??= { type, ...header };
      seenSeqNums.add(header.seqNum);
      stats.accepted += 1;
      if (message) return complete(type, message);
      return {
        status: 'progress',
        progress: Math.min(decoder.rank / header.seqLength, 0.99),
        receivedParts: seenSeqNums.size,
        expectedParts: header.seqLength,
      };
    } catch (error) {
      if (error instanceof FountainLimitError) return reject('resource-limit', error.message);
      if (error instanceof FountainPayloadError) return reject('invalid-payload', error.message);
      throw error;
    }
  };

  return {
    receive,
    isBound: () => binding !== undefined && !finished,
    reset: () => {
      binding = undefined;
      decoder = undefined;
      seenSeqNums = new Set();
      finished = false;
      stats = { accepted: 0, duplicate: 0, malformed: 0, foreign: 0 };
    },
    stats: () => ({ ...stats }),
  };
}
