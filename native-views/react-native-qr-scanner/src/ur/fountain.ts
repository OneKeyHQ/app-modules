import { chooseFragments } from '@ngraveio/bc-ur/dist/fountainUtils.js';
import { getCRC } from '@ngraveio/bc-ur/dist/utils.js';

import type { FountainHeader } from './limits';

const MAX_FRAME_WORK = 2_000_000;
const MAX_INDEX_BYTES = 8 * 1024 * 1024;
const PIVOT_SLOT_BYTES = 8;

interface Equation {
  data: Uint32Array;
  /** Singleton rows need no bitset. Mixed rows include their pivot bit. */
  indexes?: Uint32Array;
}

export class FountainLimitError extends Error {}
export class FountainPayloadError extends Error {}

export class FountainAssembly {
  private readonly rows: (Equation | undefined)[];
  private readonly indexWords: number;
  private readonly fragmentWords: number;
  private indexBytes: number;
  rank = 0;

  constructor(
    private readonly header: FountainHeader,
    private readonly maxRetainedBytes: number
  ) {
    this.indexWords = Math.ceil(header.seqLength / 32);
    this.fragmentWords = Math.ceil(header.fragmentLength / 4);
    this.indexBytes = header.seqLength * PIVOT_SLOT_BYTES;
    if (this.indexBytes > MAX_INDEX_BYTES) {
      throw new FountainLimitError('UR fountain decoder exceeds its index budget');
    }
    this.rows = new Array<Equation | undefined>(header.seqLength);
  }

  receive(seqNum: number, fragment: Uint8Array): Uint8Array | undefined {
    const { seqLength, checksum } = this.header;
    let work = MAX_FRAME_WORK;
    const spend = (operations: number) => {
      work -= operations;
      if (work < 0) throw new FountainLimitError('UR fountain decoder exceeds its work budget');
    };

    // The pinned index selector removes entries from an array while shuffling.
    // Bound that work before calling it. Original parts need only one index.
    const mixed = seqNum > seqLength;
    if (mixed) spend(seqLength * (seqLength + 4));
    const indexes = chooseFragments(seqNum, seqLength, checksum);
    spend(this.fragmentWords);
    const data = new Uint32Array(this.fragmentWords);
    new Uint8Array(data.buffer).set(fragment);
    let pivot = indexes[0]!;
    let coefficients: Uint32Array | undefined;
    if (indexes.length > 1) {
      spend(this.indexWords + indexes.length);
      coefficients = new Uint32Array(this.indexWords);
      for (const index of indexes) {
        coefficients[index >>> 5]! |= 1 << (index & 31);
        pivot = Math.max(pivot, index);
      }
    }

    // Eliminate the highest index first. Each inserted row has a unique pivot
    // and depends only on lower indexes, so rank never exceeds seqLength.
    let existing: Equation | undefined;
    while (pivot >= 0 && (existing = this.rows[pivot])) {
      spend(this.fragmentWords);
      for (let word = 0; word < data.length; word++) data[word]! ^= existing.data[word]!;

      if (!coefficients && existing.indexes) {
        spend(existing.indexes.length);
        coefficients = existing.indexes.slice();
        coefficients[pivot >>> 5]! ^= 1 << (pivot & 31);
      } else if (coefficients && existing.indexes) {
        spend((pivot >>> 5) + 1);
        for (let word = 0; word <= (pivot >>> 5); word++) {
          coefficients[word]! ^= existing.indexes[word]!;
        }
      } else if (coefficients) {
        coefficients[pivot >>> 5]! ^= 1 << (pivot & 31);
      }
      if (!coefficients) {
        pivot = -1;
      } else {
        let word = pivot >>> 5;
        while (word >= 0 && coefficients[word] === 0) {
          spend(1);
          word--;
        }
        pivot = word < 0 ? -1 : word * 32 + 31 - Math.clz32(coefficients[word]!);
      }
    }

    if (pivot < 0) {
      spend(data.length);
      if (data.some((word) => word !== 0)) {
        throw new FountainPayloadError('UR message contains inconsistent fragments');
      }
      return undefined;
    }

    if (coefficients) {
      const pivotWord = pivot >>> 5;
      spend(pivotWord + 1);
      if (coefficients[pivotWord] === ((1 << (pivot & 31)) >>> 0) &&
          !coefficients.subarray(0, pivotWord).some((word) => word !== 0)) {
        coefficients = undefined;
      } else if (pivotWord + 1 < coefficients.length) {
        coefficients = coefficients.slice(0, pivotWord + 1);
      }
    }
    const fragmentBytes = this.header.fragmentLength;
    const indexBytes = this.indexBytes + (coefficients?.byteLength ?? 0) +
      (data.byteLength - fragmentBytes);
    if (indexBytes > MAX_INDEX_BYTES ||
        (this.rank + 1) * fragmentBytes > this.maxRetainedBytes) {
      throw new FountainLimitError('UR parts exceed the retained size limit');
    }
    this.indexBytes = indexBytes;
    this.rows[pivot] = { data, indexes: coefficients };
    this.rank++;
    if (this.rank !== seqLength) return undefined;

    const { messageLength, fragmentLength } = this.header;
    spend(seqLength + messageLength + Math.ceil(messageLength / 4));
    const message = Buffer.alloc(messageLength);
    for (let index = 0; index < seqLength; index++) {
      const row = this.rows[index]!;
      if (row.indexes) {
        spend(row.indexes.length);
        for (let word = 0; word < row.indexes.length; word++) {
          let mask = row.indexes[word]!;
          if (word === (index >>> 5)) mask ^= 1 << (index & 31);
          while (mask !== 0) {
            const bit = 31 - Math.clz32(mask);
            const source = this.rows[word * 32 + bit]!.data;
            spend(this.fragmentWords + 1);
            for (let i = 0; i < row.data.length; i++) row.data[i]! ^= source[i]!;
            mask ^= 1 << bit;
          }
        }
      }
      const offset = index * fragmentLength;
      message.set(new Uint8Array(row.data.buffer, 0, Math.min(fragmentLength, messageLength - offset)), offset);
    }
    if (getCRC(message) !== checksum) throw new FountainPayloadError('UR message failed its checksum');
    return message;
  }
}
