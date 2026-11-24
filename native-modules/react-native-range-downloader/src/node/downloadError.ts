export const NodeRangeDownloadCode = {
  networkError: 'NETWORK_ERROR',
  timeout: 'TIMEOUT',
  httpError: 'HTTP_ERROR',
  packageMissing: 'PACKAGE_MISSING',
  storageError: 'STORAGE_ERROR',
  integrityFailed: 'INTEGRITY_FAILED',
  cancelled: 'CANCELLED',
  unknown: 'UNKNOWN',
  invalidArgument: 'INVALID_ARGUMENT',
  securityRejected: 'SECURITY_REJECTED',
} as const;

export type TNodeRangeDownloadCode =
  (typeof NodeRangeDownloadCode)[keyof typeof NodeRangeDownloadCode];

export class NodeRangeDownloadError extends Error {
  readonly httpStatus?: number;

  constructor(
    readonly code: TNodeRangeDownloadCode,
    httpStatus?: number,
    options?: ErrorOptions
  ) {
    super(code, options);
    this.name = 'NodeRangeDownloadError';
    if (
      code === NodeRangeDownloadCode.httpError &&
      httpStatus !== undefined &&
      Number.isInteger(httpStatus) &&
      httpStatus >= 100 &&
      httpStatus <= 599
    ) {
      this.httpStatus = httpStatus;
    }
  }
}

export function isRetryableRangeDownloadError(error: NodeRangeDownloadError): boolean {
  if (
    error.code === NodeRangeDownloadCode.networkError ||
    error.code === NodeRangeDownloadCode.timeout
  ) {
    return true;
  }
  if (error.code !== NodeRangeDownloadCode.httpError) return false;
  const status = error.httpStatus;
  return (
    status === 408 ||
    status === 416 ||
    status === 429 ||
    (status !== undefined &&
      status >= 500 &&
      status <= 599 &&
      status !== 501 &&
      status !== 505)
  );
}
