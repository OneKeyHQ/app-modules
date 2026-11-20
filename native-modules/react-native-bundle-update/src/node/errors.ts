const UPDATE_ERROR_WIRE_PREFIX = 'ONEKEY_UPDATE_ERROR:';

export enum EUpdateErrorCode {
  networkError = 'NETWORK_ERROR',
  timeout = 'TIMEOUT',
  httpError = 'HTTP_ERROR',
  packageMissing = 'PACKAGE_MISSING',
  storageError = 'STORAGE_ERROR',
  integrityFailed = 'INTEGRITY_FAILED',
  incompatible = 'INCOMPATIBLE',
  installFailed = 'INSTALL_FAILED',
  installLocation = 'INSTALL_LOCATION',
  cancelled = 'CANCELLED',
  unknown = 'UNKNOWN',
  invalidArgument = 'INVALID_ARGUMENT',
  securityRejected = 'SECURITY_REJECTED',
}

export type TUpdateErrorStep =
  | 'download'
  | 'downloadASC'
  | 'verifyASC'
  | 'verifyPackage'
  | 'install'
  | 'cleanup';

export interface IUpdateErrorDTO {
  code: EUpdateErrorCode;
  step: TUpdateErrorStep;
  httpStatus?: number;
}

const validCodes = new Set<string>(Object.values(EUpdateErrorCode));
const validSteps = new Set<string>([
  'download',
  'downloadASC',
  'verifyASC',
  'verifyPackage',
  'install',
  'cleanup',
]);

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

export function normalizeUpdateErrorDTO(
  value: unknown,
  step: TUpdateErrorStep
): IUpdateErrorDTO {
  try {
    if (
      !isRecord(value) ||
      typeof value.code !== 'string' ||
      !validCodes.has(value.code)
    ) {
      return { code: EUpdateErrorCode.unknown, step };
    }
    if (typeof value.step !== 'string' || !validSteps.has(value.step)) {
      return { code: EUpdateErrorCode.unknown, step };
    }
    const code = value.code as EUpdateErrorCode;
    if (
      code === EUpdateErrorCode.httpError &&
      typeof value.httpStatus === 'number' &&
      Number.isInteger(value.httpStatus) &&
      value.httpStatus >= 100 &&
      value.httpStatus <= 599
    ) {
      return { code, step, httpStatus: value.httpStatus };
    }
    return { code, step };
  } catch {
    return { code: EUpdateErrorCode.unknown, step };
  }
}

export function decodeUpdateErrorWire(
  message: unknown,
  step: TUpdateErrorStep
): IUpdateErrorDTO {
  if (
    typeof message !== 'string' ||
    !message.startsWith(UPDATE_ERROR_WIRE_PREFIX) ||
    message.length > 512
  ) {
    return { code: EUpdateErrorCode.unknown, step };
  }
  try {
    const payload: unknown = JSON.parse(
      message.slice(UPDATE_ERROR_WIRE_PREFIX.length)
    );
    if (!isRecord(payload) || payload.step !== step) {
      return { code: EUpdateErrorCode.unknown, step };
    }
    return normalizeUpdateErrorDTO(payload, step);
  } catch {
    return { code: EUpdateErrorCode.unknown, step };
  }
}

export function getUpdateError(
  error: unknown,
  step: TUpdateErrorStep
): IUpdateErrorDTO {
  try {
    if (isRecord(error)) {
      const data = error.data;
      if (isRecord(data) && 'updateError' in data) {
        return normalizeUpdateErrorDTO(data.updateError, step);
      }
      const decoded = decodeUpdateErrorWire(error.message, step);
      if (decoded.code !== EUpdateErrorCode.unknown) return decoded;
      if (
        ['maxAttempts', 'deadline', 'fallbackFailed', 'urlDead'].includes(
          error.reason as string
        ) &&
        error.downloadCause !== error
      ) {
        const cause = error.downloadCause;
        if (isRecord(cause)) {
          const causeData = cause.data;
          if (isRecord(causeData) && 'updateError' in causeData) {
            return normalizeUpdateErrorDTO(causeData.updateError, step);
          }
          return decodeUpdateErrorWire(cause.message, step);
        }
        return decodeUpdateErrorWire(cause, step);
      }
      return decoded;
    }
    return decodeUpdateErrorWire(error, step);
  } catch {
    return { code: EUpdateErrorCode.unknown, step };
  }
}

export function isRetryableUpdateError(dto: IUpdateErrorDTO): boolean {
  if (
    dto.code === EUpdateErrorCode.networkError ||
    dto.code === EUpdateErrorCode.timeout
  ) {
    return true;
  }
  if (dto.code !== EUpdateErrorCode.httpError) return false;
  const status = dto.httpStatus;
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

export class BundleUpdateNodeError extends Error {
  readonly data: { updateError: IUpdateErrorDTO };

  constructor(dto: IUpdateErrorDTO) {
    const safe = normalizeUpdateErrorDTO(dto, dto.step);
    super(`${UPDATE_ERROR_WIRE_PREFIX}${JSON.stringify(safe)}`);
    this.name = 'BundleUpdateNodeError';
    this.data = { updateError: safe };
  }
}

export function createUpdateError(dto: IUpdateErrorDTO) {
  return new BundleUpdateNodeError(dto);
}
