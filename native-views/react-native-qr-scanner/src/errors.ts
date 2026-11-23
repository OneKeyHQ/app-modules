export type QrScannerErrorCode =
  | 'permission-denied'
  | 'no-camera'
  | 'camera-busy'
  | 'decoder-unavailable'
  | 'invalid-image'
  | 'resource-limit';

const KNOWN_CODES: readonly QrScannerErrorCode[] = [
  'permission-denied',
  'no-camera',
  'camera-busy',
  'decoder-unavailable',
  'invalid-image',
  'resource-limit',
];

export class QrScannerError extends Error {
  override name = 'QrScannerError';

  constructor(
    readonly code: QrScannerErrorCode,
    message: string
  ) {
    super(message);
  }
}

export function isQrScannerErrorCode(
  value: string
): value is QrScannerErrorCode {
  return (KNOWN_CODES as readonly string[]).includes(value);
}

/**
 * Native code rejects with `<code>: <reason>` because Nitro errors carry only
 * a message. Unknown shapes map to `decoder-unavailable`.
 */
export function toQrScannerError(error: unknown): QrScannerError {
  if (error instanceof QrScannerError) return error;
  // Android's Nitro errors append the Java stack trace; keep the first line.
  const message = (error instanceof Error ? error.message : String(error))
    .split('\n')[0]!
    .trim();
  const match = /(?:^|\s)([a-z-]+):\s*(.*)$/.exec(message);
  if (match && isQrScannerErrorCode(match[1]!)) {
    return new QrScannerError(match[1], match[2] || message);
  }
  return new QrScannerError('decoder-unavailable', message);
}
