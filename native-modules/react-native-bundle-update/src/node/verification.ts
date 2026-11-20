import crypto from 'node:crypto';
import fs from 'node:fs';
import { readCleartextMessage, readKey } from 'openpgp';

export const MAX_ARCHIVE_BYTES = 2 * 1024 * 1024 * 1024;

export class ArchiveSizeError extends Error {}

export async function hashArchive(
  filePath: string,
  algorithm: 'sha256' | 'sha512',
  signal?: AbortSignal
): Promise<{ digest: string; size: number }> {
  signal?.throwIfAborted();
  const stat = await fs.promises.stat(filePath);
  signal?.throwIfAborted();
  if (stat.size > MAX_ARCHIVE_BYTES) throw new ArchiveSizeError();
  const hash = crypto.createHash(algorithm);
  const stream = fs.createReadStream(filePath, {
    highWaterMark: 64 * 1024,
    signal,
  });
  let size = 0;
  for await (const chunk of stream) {
    signal?.throwIfAborted();
    size += chunk.length;
    if (size > MAX_ARCHIVE_BYTES) throw new ArchiveSizeError();
    hash.update(chunk);
  }
  signal?.throwIfAborted();
  return { digest: hash.digest('hex'), size };
}

export class SignedMetadataError extends Error {}

export interface ISignedMetadataIdentity {
  fileName?: string;
  appVersion?: string;
  bundleVersion?: string;
}

export async function readSignedMetadataSha256(
  signature: string,
  armoredPublicKey: string,
  expected: ISignedMetadataIdentity = {}
): Promise<string> {
  let json: {
    fileName?: unknown;
    appType?: unknown;
    appVersion?: unknown;
    bundleVersion?: unknown;
    sha256?: unknown;
  };
  try {
    const message = await readCleartextMessage({ cleartextMessage: signature });
    const key = await readKey({ armoredKey: armoredPublicKey });
    const [result] = await message.verify([key]);
    if (!(await result?.verified)) {
      throw new SignedMetadataError('Invalid metadata signature');
    }
    json = JSON.parse(message.getText()) as typeof json;
  } catch {
    throw new SignedMetadataError('Invalid metadata signature');
  }

  if (typeof json?.sha256 !== 'string' || !json.sha256) {
    throw new SignedMetadataError('Invalid signed metadata sha256');
  }
  if (json.fileName !== (expected.fileName ?? 'metadata.json')) {
    throw new SignedMetadataError('Invalid signed metadata fileName');
  }
  if (json.appType !== 'electron') {
    throw new SignedMetadataError('Invalid signed metadata appType');
  }
  if (
    expected.appVersion &&
    String(json.appVersion ?? '') !== expected.appVersion
  ) {
    throw new SignedMetadataError('Signed metadata appVersion mismatch');
  }
  if (
    expected.bundleVersion &&
    String(json.bundleVersion ?? '') !== expected.bundleVersion
  ) {
    throw new SignedMetadataError('Signed metadata bundleVersion mismatch');
  }
  return json.sha256;
}

let _lastSHA256FailureReason: string | undefined;

export const lastSHA256FailureReason = (): string | undefined =>
  _lastSHA256FailureReason;

const stampSHA256Failure = (reason: string | undefined) => {
  _lastSHA256FailureReason = reason;
};

const classifySHA256Error = (error: unknown): string => {
  const errno = (error as NodeJS.ErrnoException | null)?.code;
  switch (errno) {
    case 'ENOENT':
      return 'FILE_NOT_FOUND';
    case 'EACCES':
    case 'EPERM':
      return 'PERMISSION_DENIED';
    case 'EISDIR':
      return 'IS_DIRECTORY';
    case 'ENOSPC':
      return 'DISK_FULL';
    case 'EMFILE':
    case 'ENFILE':
      return 'TOO_MANY_OPEN_FILES';
    default:
      break;
  }
  if (error instanceof RangeError) {
    // V8 buffer allocation overflow on multi-GB files / heap exhaustion.
    return 'OOM';
  }
  if (errno) return `IO_${errno}`;
  const ctor = (error as { constructor?: { name?: string } } | null)
    ?.constructor?.name;
  return ctor ? `IO_${ctor}` : 'IO_UNKNOWN';
};

function hashFile(filePath: string, algorithm: 'sha256' | 'sha512'): string {
  const hash = crypto.createHash(algorithm);
  const fd = fs.openSync(filePath, 'r');
  try {
    const buffer = Buffer.allocUnsafe(64 * 1024);
    let bytesRead = fs.readSync(fd, buffer, 0, buffer.length, null);
    while (bytesRead > 0) {
      hash.update(buffer.subarray(0, bytesRead));
      bytesRead = fs.readSync(fd, buffer, 0, buffer.length, null);
    }
    return hash.digest('hex');
  } finally {
    fs.closeSync(fd);
  }
}

export const calculateSHA256 = (filePath: string) => {
  stampSHA256Failure(undefined);
  if (!filePath) {
    stampSHA256Failure('EMPTY_PATH');
    return '';
  }
  try {
    // Reject empty files before hashing so the SHA-of-empty digest
    // (e3b0c44…b855) doesn't silently propagate as a clean "MISMATCH"
    // when the real cause is a truncated / zero-byte download. Mirrors
    // the explicit FILE_EMPTY checks in the iOS / Android calculators.
    const stat = fs.statSync(filePath);
    if (stat.size === 0) {
      stampSHA256Failure('FILE_EMPTY');
      return '';
    }
    return hashFile(filePath, 'sha256');
  } catch (error) {
    stampSHA256Failure(classifySHA256Error(error));
    throw error;
  }
};

export const verifySha256 = (
  filePath: string,
  sha256: string,
  log?: Pick<Console, 'info'>
) => {
  stampSHA256Failure(undefined);
  if (!filePath) {
    stampSHA256Failure('EMPTY_PATH');
    return false;
  }
  const normalizedExpected = (sha256 ?? '').trim().toLowerCase();
  if (!normalizedExpected) {
    stampSHA256Failure('EMPTY_EXPECTED_HASH');
    return false;
  }
  // calculateSHA256 stamps its own reason before throwing; preserve it by
  // catching here so the upstream Promise-based callers (verifyAndResolve)
  // can still read lastSHA256FailureReason() on a false result.
  let fileSha256: string;
  try {
    fileSha256 = calculateSHA256(filePath);
  } catch {
    return false;
  }
  if (!fileSha256) {
    return false;
  }
  log?.info('bundle-download-verifySha256', normalizedExpected, fileSha256);
  const actualBuf = Buffer.from(fileSha256, 'utf8');
  const expectedBuf = Buffer.from(normalizedExpected, 'utf8');
  if (
    actualBuf.length !== expectedBuf.length ||
    !crypto.timingSafeEqual(actualBuf, expectedBuf)
  ) {
    stampSHA256Failure('MISMATCH');
    return false;
  }
  return true;
};

export const checkFileSha512 = (filePath: string, sha512: string) => {
  try {
    const normalizedExpected = (sha512 ?? '').trim().toLowerCase();
    if (!normalizedExpected) {
      return false;
    }
    const actualBuf = Buffer.from(hashFile(filePath, 'sha512'), 'utf8');
    const expectedBuf = Buffer.from(normalizedExpected, 'utf8');
    return (
      actualBuf.length === expectedBuf.length &&
      crypto.timingSafeEqual(actualBuf, expectedBuf)
    );
  } catch {
    return false;
  }
};
