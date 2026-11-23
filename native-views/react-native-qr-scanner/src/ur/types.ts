export type ScanResult =
  | { kind: 'text'; text: string }
  | { kind: 'ur'; type: string; cbor: Uint8Array };

export type ScanMode = 'static' | 'auto' | 'ur';

export interface UrLimits {
  /** Largest complete message, in bytes. */
  maxMessageBytes: number;
  /** Largest part count a fountain code may declare. */
  maxSeqLength: number;
  /** Deepest CBOR nesting accepted in a completed message. */
  maxCborDepth: number;
  /** Most distinct fountain parts one session keeps. */
  maxDistinctParts: number;
  /** Most fragment bytes one session keeps across accepted parts. */
  maxRetainedBytes: number;
}

export type ScanIgnoreReason =
  | 'not-ur'
  | 'duplicate'
  | 'malformed'
  | 'foreign'
  | 'finished';

export type ScanErrorCode = 'too-large' | 'invalid-payload' | 'resource-limit';

export interface ScanError {
  code: ScanErrorCode;
  message: string;
}

export type ScanReceiveResult =
  | { status: 'ignored'; reason: ScanIgnoreReason }
  | {
      status: 'progress';
      progress: number;
      receivedParts: number;
      expectedParts: number;
    }
  | { status: 'complete'; result: ScanResult }
  | { status: 'rejected'; error: ScanError };

export interface ScanStats {
  accepted: number;
  duplicate: number;
  malformed: number;
  foreign: number;
}

export interface UrAssembler {
  receive(frameText: string): ScanReceiveResult;
  /** Whether a fountain message is bound; later foreign frames are ignored. */
  isBound(): boolean;
  reset(): void;
  stats(): ScanStats;
}

export interface ScanSession {
  receive(frameText: string): ScanReceiveResult;
  reset(): void;
  stats(): ScanStats;
}
