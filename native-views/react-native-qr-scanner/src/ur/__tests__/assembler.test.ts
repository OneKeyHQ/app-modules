import { UR, UREncoder } from '@ngraveio/bc-ur';

import { createUrAssembler } from '../assembler';
import { DEFAULT_UR_LIMITS } from '../limits';

// A definite-length CBOR byte string of `length` bytes.
function cborBytes(length: number, seed = 1): Buffer {
  const body = Buffer.alloc(length);
  for (let i = 0; i < length; i += 1) body[i] = (i * 31 + seed) & 0xff;
  const head =
    length < 24
      ? Buffer.from([0x40 + length])
      : length < 256
      ? Buffer.from([0x58, length])
      : Buffer.from([0x59, length >> 8, length & 0xff]);
  return Buffer.concat([head, body]);
}

function fountainParts(
  cbor: Buffer,
  fragmentLength: number,
  count: number,
  type = 'bytes'
): string[] {
  const encoder = new UREncoder(new UR(cbor, type), fragmentLength);
  return Array.from({ length: count }, () => encoder.nextPart());
}

describe('createUrAssembler', () => {
  it('completes a single-part UR with the exact CBOR', () => {
    const cbor = cborBytes(20);
    const part = UREncoder.encodeSinglePart(new UR(cbor, 'bytes'));
    const assembler = createUrAssembler();

    const result = assembler.receive(part);

    expect(result).toEqual({
      status: 'complete',
      result: { kind: 'ur', type: 'bytes', cbor: Uint8Array.from(cbor) },
    });
    expect(assembler.receive(part)).toEqual({
      status: 'ignored',
      reason: 'finished',
    });
  });

  it('assembles parts out of order, case-insensitively, deduping repeats', () => {
    const cbor = cborBytes(300);
    const parts = fountainParts(cbor, 50, 7);
    const seqLength = Number(/\/\d+-(\d+)\//.exec(parts[0]!)![1]);
    const assembler = createUrAssembler();
    // Every original part once, in reverse, with one repeat early on.
    const order = Array.from({ length: seqLength }, (_, i) => seqLength - 1 - i);
    order.splice(2, 0, order[1]!);
    const statuses: string[] = [];
    let completed: Uint8Array | undefined;

    for (const index of order) {
      // QR alphanumeric mode delivers upper-case text.
      const text = index % 2 ? parts[index]!.toUpperCase() : parts[index]!;
      const result = assembler.receive(` ${text} `);
      statuses.push(result.status);
      if (result.status === 'progress') {
        expect(result.progress).toBeLessThan(1);
        expect(result.expectedParts).toBe(seqLength);
      }
      if (result.status === 'complete' && result.result.kind === 'ur') {
        completed = result.result.cbor;
        break;
      }
    }

    expect(statuses).toContain('ignored');
    expect(statuses[statuses.length - 1]).toBe('complete');
    expect(completed).toEqual(Uint8Array.from(cbor));
    expect(assembler.stats().duplicate).toBe(1);
  });

  it('completes from fountain redundancy parts beyond the declared count', () => {
    const cbor = cborBytes(200);
    const parts = fountainParts(cbor, 40, 40);
    const assembler = createUrAssembler();
    let done = false;

    // Skip the first original part so the mixes must recover it.
    for (const part of parts.slice(1)) {
      const result = assembler.receive(part);
      if (result.status === 'complete') {
        done = true;
        expect(result.result).toEqual({
          kind: 'ur',
          type: 'bytes',
          cbor: Uint8Array.from(cbor),
        });
        break;
      }
    }
    expect(done).toBe(true);
  });

  it('ignores parts of another message once bound', () => {
    const first = fountainParts(cborBytes(200, 1), 50, 2);
    const other = fountainParts(cborBytes(200, 2), 50, 2);
    const single = UREncoder.encodeSinglePart(new UR(cborBytes(5), 'bytes'));
    const assembler = createUrAssembler();

    expect(assembler.receive(first[0]!).status).toBe('progress');
    expect(assembler.receive(other[1]!)).toEqual({
      status: 'ignored',
      reason: 'foreign',
    });
    expect(assembler.receive(single)).toEqual({
      status: 'ignored',
      reason: 'foreign',
    });
    expect(assembler.stats().foreign).toBe(2);
  });

  it('drops a part with a bad checksum or a forged text header', () => {
    const parts = fountainParts(cborBytes(200), 50, 2);
    const original = parts[0]!;
    const words = original.slice(original.lastIndexOf('/') + 1);
    const flipped = words.startsWith('ae') ? `ad${words.slice(2)}` : `ae${words.slice(2)}`;
    const badCrc = original.replace(words, flipped);
    // Claim sequence number 2 while the CBOR header still says 1.
    const forgedHeader = original.replace(/\/1-(\d+)\//, '/2-$1/');
    expect(forgedHeader).not.toBe(original);
    const assembler = createUrAssembler();

    expect(assembler.receive(badCrc)).toEqual({
      status: 'ignored',
      reason: 'malformed',
    });
    expect(assembler.receive(forgedHeader)).toEqual({
      status: 'ignored',
      reason: 'malformed',
    });
    expect(assembler.receive('ur:bytes/not-words')).toEqual({
      status: 'ignored',
      reason: 'malformed',
    });
    expect(assembler.receive('hello')).toEqual({
      status: 'ignored',
      reason: 'not-ur',
    });
    // The session is still usable after malformed frames.
    expect(assembler.receive(original).status).toBe('progress');
  });

  it('rejects a code beyond the declared-size limits', () => {
    const parts = fountainParts(cborBytes(300), 50, 1);
    expect(
      createUrAssembler({ limits: { maxMessageBytes: 100 } }).receive(parts[0]!)
    ).toMatchObject({ status: 'rejected', error: { code: 'too-large' } });
    expect(
      createUrAssembler({ limits: { maxSeqLength: 3 } }).receive(parts[0]!)
    ).toMatchObject({ status: 'rejected', error: { code: 'too-large' } });
  });

  it('rejects when retained parts exceed the session budget', () => {
    const parts = fountainParts(cborBytes(300), 50, 3);
    const assembler = createUrAssembler({ limits: { maxRetainedBytes: 100 } });

    expect(assembler.receive(parts[0]!).status).toBe('progress');
    expect(assembler.receive(parts[1]!).status).toBe('progress');
    expect(assembler.receive(parts[2]!)).toMatchObject({
      status: 'rejected',
      error: { code: 'resource-limit' },
    });
    expect(assembler.receive(parts[0]!)).toEqual({
      status: 'ignored',
      reason: 'finished',
    });
  });

  it('rejects a completed message whose CBOR is unbounded', () => {
    // 0x5f opens an indefinite-length byte string.
    const unbounded = Buffer.concat([Buffer.from([0x5f]), Buffer.alloc(120, 1)]);
    const parts = fountainParts(unbounded, 40, 20);
    const assembler = createUrAssembler();
    let last;
    for (const part of parts) {
      last = assembler.receive(part);
      if (last.status !== 'progress' && last.status !== 'ignored') break;
    }
    expect(last).toMatchObject({
      status: 'rejected',
      error: { code: 'invalid-payload' },
    });
  });

  it('starts over after reset', () => {
    const parts = fountainParts(cborBytes(200, 1), 50, 1);
    const other = fountainParts(cborBytes(200, 2), 50, 1);
    const assembler = createUrAssembler();

    assembler.receive(parts[0]!);
    assembler.reset();

    expect(assembler.receive(other[0]!).status).toBe('progress');
    expect(assembler.stats()).toEqual({
      accepted: 1,
      duplicate: 0,
      malformed: 0,
      foreign: 0,
    });
  });

  it('refuses invalid limits', () => {
    expect(() => createUrAssembler({ limits: { maxSeqLength: 0 } })).toThrow(
      RangeError
    );
    expect(DEFAULT_UR_LIMITS.maxMessageBytes).toBe(1024 * 1024);
  });
});
