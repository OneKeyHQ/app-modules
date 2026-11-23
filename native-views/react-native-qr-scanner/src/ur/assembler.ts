import { URDecoder } from '@ngraveio/bc-ur';

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

interface MessageBinding {
  type: string;
  seqLength: number;
  messageLength: number;
  checksum: number;
}

// Progress below 1 until the message completes; the fountain decoder's own
// estimate can reach 1 before the final mix resolves.
const MAX_PENDING_PROGRESS = 0.99;

function sameBinding(
  binding: MessageBinding,
  type: string,
  header: FountainHeader
): boolean {
  return (
    binding.type === type &&
    binding.seqLength === header.seqLength &&
    binding.messageLength === header.messageLength &&
    binding.checksum === header.checksum
  );
}

export function createUrAssembler(options?: {
  limits?: Partial<UrLimits>;
}): UrAssembler {
  const limits = resolveLimits(options?.limits);

  let binding: MessageBinding | undefined;
  let decoder: URDecoder | undefined;
  let seenSeqNums = new Set<number>();
  let retainedBytes = 0;
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
    if (parsed.verdict === 'too-large') {
      return reject(
        'too-large',
        `QR code is too large to scan (over ${limits.maxMessageBytes} bytes or ${limits.maxSeqLength} parts)`
      );
    }
    if (parsed.verdict === 'single') {
      if (binding) return ignore('foreign');
      stats.accepted += 1;
      return complete(parsed.type, parsed.payload);
    }

    const { type, header } = parsed;
    if (binding && !sameBinding(binding, type, header)) return ignore('foreign');
    if (seenSeqNums.has(header.seqNum)) return ignore('duplicate');
    if (seenSeqNums.size + 1 > limits.maxDistinctParts) {
      return reject('resource-limit', 'Too many distinct UR parts');
    }
    if (retainedBytes + header.fragmentLength > limits.maxRetainedBytes) {
      return reject('resource-limit', 'UR parts exceed the retained size limit');
    }

    const activeDecoder = decoder ?? new URDecoder();
    let received: boolean;
    try {
      received = activeDecoder.receivePart(part);
    } catch {
      received = false;
    }
    if (!received) return ignore('malformed');

    if (!binding) {
      binding = {
        type,
        seqLength: header.seqLength,
        messageLength: header.messageLength,
        checksum: header.checksum,
      };
      decoder = activeDecoder;
    }
    seenSeqNums.add(header.seqNum);
    retainedBytes += header.fragmentLength;
    stats.accepted += 1;

    if (activeDecoder.isComplete()) {
      if (!activeDecoder.isSuccess()) {
        return reject('invalid-payload', 'UR message failed its checksum');
      }
      const ur = activeDecoder.resultUR();
      if (ur.type !== binding.type) {
        return reject('invalid-payload', 'UR type changed during assembly');
      }
      return complete(ur.type, ur.cbor);
    }

    return {
      status: 'progress',
      progress: Math.min(
        activeDecoder.estimatedPercentComplete(),
        MAX_PENDING_PROGRESS
      ),
      receivedParts: seenSeqNums.size,
      expectedParts: binding.seqLength,
    };
  };

  return {
    receive,
    isBound: () => binding !== undefined && !finished,
    reset: () => {
      binding = undefined;
      decoder = undefined;
      seenSeqNums = new Set();
      retainedBytes = 0;
      finished = false;
      stats = { accepted: 0, duplicate: 0, malformed: 0, foreign: 0 };
    },
    stats: () => ({ ...stats }),
  };
}
