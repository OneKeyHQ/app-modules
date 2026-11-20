// cspell:ignore fwlease

import { createHash, randomUUID } from 'node:crypto';
import {
  accessSync,
  createReadStream,
  createWriteStream,
  constants as fsConstants,
  mkdirSync,
} from 'node:fs';
import {
  type FileHandle,
  lstat,
  mkdir,
  open,
  readdir,
  rename,
  rm,
  stat,
} from 'node:fs/promises';
import path from 'node:path';
import { Readable, Transform } from 'node:stream';
import { pipeline } from 'node:stream/promises';

import yauzl from 'yauzl';

import { FirmwareArtifactNodeError } from './firmwareArtifactError.js';

import type {
  IFirmwareArtifactAdapter,
  IFirmwareArtifactReceipt,
} from './firmwareArtifactTypes.js';
import type { ReadableStream as NodeReadableStream } from 'node:stream/web';
import type { Entry, ZipFile } from 'yauzl';

const MAX_READ_BYTES = 256 * 1024;
const MAX_ARTIFACT_BYTES = 512 * 1024 * 1024;
const MAX_ARCHIVE_ENTRIES = 4096;
const MAX_ARCHIVE_ENTRY_BYTES = 128 * 1024 * 1024;
const UNIX_FILE_TYPE_MASK = 61_440;
const UNIX_DIRECTORY_FILE_TYPE = 16_384;
const UNIX_REGULAR_FILE_TYPE = 32_768;
const ARTIFACT_REF_PATTERN = /^fw:[a-f0-9]{64}$/u;
const ARCHIVE_SCRATCH_PATTERN =
  /^archive-[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$/u;
const SHA256_PATTERN = /^[a-fA-F0-9]{64}$/u;
const TASK_ID_PATTERN = /^[A-Za-z0-9._-]{1,100}$/u;
const IDENTIFIER_PATTERN = /^[A-Za-z0-9._:-]{1,160}$/u;
const LEASE_REF_PATTERN = /^fwlease:[a-f0-9-]{36}$/u;
const NESTED_ARCHIVE_PATTERN = /\.(?:zip|7z|rar|tar|gz|tgz)$/iu;
const FINAL_ARTIFACT_GRACE_MS = 24 * 60 * 60 * 1000;
const PARTIAL_ARTIFACT_GRACE_MS = 7 * 24 * 60 * 60 * 1000;
const ARCHIVE_SCRATCH_GRACE_MS = PARTIAL_ARTIFACT_GRACE_MS;
const FIRMWARE_ARTIFACT_HOSTNAMES = new Set([
  'common.onekey-asset.com',
  'web.onekey-asset.com',
]);

type IDownloadInput = Parameters<IFirmwareArtifactAdapter['download']>[0];
type IMaterializeInput = Parameters<IFirmwareArtifactAdapter['materialize']>[0];
type IArchiveRequirement = {
  entryName: string;
  expectedSize: number;
  expectedSha256?: string;
};

type IStagedEntry = {
  entryName: string;
  size: number;
  sha256: string;
  filePath: string;
};

type IFirmwareArtifactLease = {
  transactionId: string;
  artifactRefs: Set<string>;
};
type IDownloadReceipt = Omit<IFirmwareArtifactReceipt, 'expectedSha256Verified'>;
type IActiveDownload = {
  task: Promise<IDownloadReceipt>;
  transactionId: string;
  artifactSha256?: string;
  pendingCallers: number;
};

const hashFile = async (filePath: string): Promise<string> => {
  const digest = createHash('sha256');
  for await (const chunk of createReadStream(filePath)) {
    digest.update(chunk);
  }
  return digest.digest('hex');
};

const scratchDirectoryBytes = async (directory: string): Promise<number> => {
  const entries = await readdir(directory, { withFileTypes: true }).catch(
    () => [],
  );
  let size = 0;
  for (const entry of entries) {
    const entryPath = path.join(directory, entry.name);
    if (entry.isDirectory()) {
      size += await scratchDirectoryBytes(entryPath);
    } else if (entry.isFile()) {
      const fileStat = await lstat(entryPath).catch(() => undefined);
      if (fileStat?.isFile()) size += fileStat.size;
    }
  }
  return size;
};

type FirmwareArtifactErrorClass = new (
  code: string,
  message: string,
  options?: ErrorOptions
) => Error;

export interface IFirmwareArtifactNodeOptions {
  rootPath: string;
  fetch?: typeof globalThis.fetch;
  errorClass?: FirmwareArtifactErrorClass;
}

const assertSafeInteger = (
  value: number,
  label: string,
  ErrorClass: FirmwareArtifactErrorClass
): void => {
  if (!Number.isSafeInteger(value) || value <= 0) {
    throw new ErrorClass(
      'ARTIFACT_INVALID_INPUT',
      `${label} must be a positive safe integer`,
    );
  }
};

// allowPreReleaseHosts is only sent by bg while developer mode +
// "Use pre-release config" are on: pre-release artifacts live in
// developer-owned buckets whose hostnames cannot be pinned in advance. It only
// widens hostname admission; all other validation, including optional integrity
// metadata, stays identical to the production config path.
export const isFirmwareArtifactUrlAllowed = (
  url: URL,
  options?: { allowPreReleaseHosts?: boolean },
): boolean =>
  url.protocol === 'https:' &&
  url.port === '' &&
  !url.username &&
  !url.password &&
  !url.hash &&
  (options?.allowPreReleaseHosts === true ||
    FIRMWARE_ARTIFACT_HOSTNAMES.has(url.hostname.toLowerCase()));

const validatePortableEntryName = (
  name: string,
  canonicalNames: Set<string>,
  ErrorClass: FirmwareArtifactErrorClass,
): void => {
  const normalized = name.normalize('NFC');
  const folded = normalized.toLowerCase();
  const parts = name.split('/');
  if (
    !name ||
    name.length > 512 ||
    name !== normalized ||
    name.startsWith('/') ||
    name.startsWith('\\') ||
    name.includes('\\') ||
    name.includes(':') ||
    Array.from(name).some((character) => {
      const code = character.codePointAt(0) ?? 0;
      return code < 0x20 || code === 0x7f;
    }) ||
    parts.some(
      (part) =>
        !part ||
        part === '.' ||
        part === '..' ||
        part.endsWith('.') ||
        part.endsWith(' '),
    ) ||
    NESTED_ARCHIVE_PATTERN.test(name) ||
    canonicalNames.has(folded)
  ) {
    throw new ErrorClass(
      'ARTIFACT_ARCHIVE_INVALID',
      'Firmware archive entry name is not portable',
    );
  }
  canonicalNames.add(folded);
};

const openZip = (
  filePath: string,
  options: yauzl.Options,
  ErrorClass: FirmwareArtifactErrorClass,
): Promise<ZipFile> =>
  new Promise((resolve, reject) => {
    yauzl.open(filePath, options, (error, zipFile) => {
      if (error || !zipFile) {
        reject(
          new ErrorClass(
            'ARTIFACT_ARCHIVE_INVALID',
            'Firmware archive cannot be opened',
            { cause: error },
          ),
        );
      } else {
        resolve(zipFile);
      }
    });
  });

const openZipEntry = (
  zipFile: ZipFile,
  entry: Entry,
  ErrorClass: FirmwareArtifactErrorClass,
): Promise<Readable> =>
  new Promise((resolve, reject) => {
    zipFile.openReadStream(entry, (error, stream) => {
      if (error || !stream) {
        reject(
          new ErrorClass(
            'ARTIFACT_ARCHIVE_INVALID',
            'Firmware archive entry cannot be opened',
            { cause: error },
          ),
        );
      } else {
        resolve(stream);
      }
    });
  });

export class FirmwareArtifactNode implements IFirmwareArtifactAdapter {
  private readonly downloads = new Map<string, IActiveDownload>();

  private readonly cancelledTransactions = new Set<string>();

  private readonly cancellationCallbacks = new Map<string, Set<() => void>>();

  private readonly readers = new Map<
    string,
    { handle: FileHandle; size: number; filePath: string }
  >();

  private readonly artifactOperations = new Map<string, Promise<unknown>>();

  private readonly activeArchives = new Map<string, string>();

  private readonly rootPath: string;

  private readonly leases = new Map<string, IFirmwareArtifactLease>();

  private readonly fetch: typeof globalThis.fetch;

  private readonly ErrorClass: FirmwareArtifactErrorClass;

  constructor({ rootPath, fetch = globalThis.fetch, errorClass = FirmwareArtifactNodeError }: IFirmwareArtifactNodeOptions) {
    this.rootPath = rootPath;
    this.fetch = fetch;
    this.ErrorClass = errorClass;
    mkdirSync(this.rootPath, { recursive: true });
    accessSync(this.rootPath, fsConstants.R_OK | fsConstants.W_OK);
  }

  getCapabilities() {
    return {
      firmwareArtifactProtocolVersion: 4,
      supportedRouteTypes: ['domain'],
      supportsArchiveMaterialization: true,
      maxReadBytes: MAX_READ_BYTES,
    };
  }

  async download(input: IDownloadInput): Promise<IFirmwareArtifactReceipt> {
    this.validateDownloadInput(input);
    this.assertNotCancelled(input.transactionId);
    const expectedSha256 = input.expectedSha256?.toLowerCase();
    if (expectedSha256) {
      this.retainExpected({
        leaseRef: input.leaseRef,
        transactionId: input.transactionId,
        artifactRef: `fw:${expectedSha256}`,
      });
    } else {
      this.assertLeaseTransaction(input.leaseRef, input.transactionId);
    }
    const downloadToken =
      expectedSha256 ?? createHash('sha256').update(input.url).digest('hex');
    const key = `${downloadToken}:${input.taskId}:${input.transactionId}`;
    let download = this.downloads.get(key);
    if (!download) {
      const active: IActiveDownload = {
        task: this.downloadLocked(input, downloadToken, (sha256) => {
          active.artifactSha256 = sha256;
        }),
        transactionId: input.transactionId,
        pendingCallers: 0,
      };
      this.downloads.set(key, active);
      download = active;
    }
    download.pendingCallers += 1;
    try {
      const receipt = await download.task;
      this.assertNotCancelled(input.transactionId);
      if (
        receipt.size > input.maxBytes ||
        (input.expectedSize !== undefined && receipt.size !== input.expectedSize) ||
        (expectedSha256 !== undefined && receipt.sha256 !== expectedSha256)
      ) {
        throw new this.ErrorClass(
          'ARTIFACT_INTEGRITY_FAILED',
          'Firmware artifact size or SHA-256 is invalid',
        );
      }
      this.retainExpected({
        leaseRef: input.leaseRef,
        transactionId: input.transactionId,
        artifactRef: receipt.artifactRef,
      });
      return { ...receipt, expectedSha256Verified: expectedSha256 !== undefined };
    } finally {
      download.pendingCallers -= 1;
      if (!download.pendingCallers) {
        this.downloads.delete(key);
        this.clearCancellationIfIdle(download.transactionId);
      }
    }
  }

  async cancelDownloads(transactionId: string): Promise<void> {
    if (!IDENTIFIER_PATTERN.test(transactionId)) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware transactionId is invalid',
      );
    }
    this.cancelledTransactions.add(transactionId);
    for (const cancel of this.cancellationCallbacks.get(transactionId) ?? []) {
      cancel();
    }
  }

  async materialize(
    input: IMaterializeInput,
  ): Promise<
    readonly { entryName: string; receipt: IFirmwareArtifactReceipt }[]
  > {
    this.assertLease(input.leaseRef);
    const archiveSha256 = this.validateArtifactRef(input.archiveArtifactRef);
    const scratchPath = path.join(this.rootPath, `archive-${randomUUID()}`);
    this.activeArchives.set(scratchPath, archiveSha256);
    try {
      const archivePath = await this.runArtifactOperation(archiveSha256, () =>
        this.resolveArtifactPath(archiveSha256),
      );
      const expectedEntries = input.expectedEntries
        ? this.validateExpectedEntries(input.expectedEntries)
        : undefined;
      const requirements = await this.validateArchive(archivePath, expectedEntries);
      await mkdir(scratchPath, { recursive: false });
      try {
        const staged = await this.extractArchive(
          archivePath,
          scratchPath,
          requirements,
        );
        const result = [];
        for (const entry of staged) {
          const receipt = {
            artifactRef: `fw:${entry.sha256}`,
            size: entry.size,
            sha256: entry.sha256,
            expectedSha256Verified: input.expectedEntries !== undefined,
          };
          await this.runArtifactOperation(entry.sha256, async () => {
            await this.promoteArtifact({
              sourcePath: entry.filePath,
              size: entry.size,
              sha256: entry.sha256,
            });
            this.retainExpected({
              leaseRef: input.leaseRef,
              artifactRef: receipt.artifactRef,
            });
          });
          result.push({ entryName: entry.entryName, receipt });
        }
        return result;
      } finally {
        await rm(scratchPath, { recursive: true, force: true });
      }
    } finally {
      this.activeArchives.delete(scratchPath);
    }
  }

  async open(artifactRef: string) {
    const sha256 = this.validateArtifactRef(artifactRef);
    return this.runArtifactOperation(sha256, async () => {
      const filePath = await this.resolveArtifactPath(sha256);
      const fileStat = await stat(filePath);
      const readerId = randomUUID();
      this.readers.set(readerId, {
        handle: await open(filePath, 'r'),
        size: fileStat.size,
        filePath,
      });
      return { readerId, size: fileStat.size };
    });
  }

  async read({
    readerId,
    offset,
    length,
  }: {
    readerId: string;
    offset: number;
    length: number;
  }): Promise<ArrayBuffer> {
    if (
      !Number.isSafeInteger(offset) ||
      offset < 0 ||
      !Number.isSafeInteger(length) ||
      length <= 0 ||
      length > MAX_READ_BYTES
    ) {
      throw new this.ErrorClass(
        'ARTIFACT_READER_INVALID',
        'Firmware artifact read is invalid',
      );
    }
    const reader = this.readers.get(readerId);
    if (!reader || offset + length > reader.size) {
      throw new this.ErrorClass(
        'ARTIFACT_READER_INVALID',
        'Firmware artifact read is out of bounds',
      );
    }
    const buffer = Buffer.allocUnsafe(length);
    const { bytesRead } = await reader.handle.read(buffer, 0, length, offset);
    if (bytesRead !== length) {
      throw new this.ErrorClass(
        'ARTIFACT_READER_INVALID',
        'Firmware artifact returned a short read',
      );
    }
    return buffer.buffer.slice(
      buffer.byteOffset,
      buffer.byteOffset + buffer.byteLength,
    );
  }

  async close(readerId: string): Promise<void> {
    const reader = this.readers.get(readerId);
    this.readers.delete(readerId);
    await reader?.handle.close();
  }

  async createLease(transactionId: string): Promise<{ leaseRef: string }> {
    if (!IDENTIFIER_PATTERN.test(transactionId)) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware transactionId is invalid',
      );
    }
    if (this.leases.size >= 32) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Too many firmware artifact leases',
      );
    }
    const leaseRef = `fwlease:${randomUUID()}`;
    this.leases.set(leaseRef, {
      transactionId,
      artifactRefs: new Set(),
    });
    return { leaseRef };
  }

  async retain({
    leaseRef,
    artifactRef,
  }: {
    leaseRef: string;
    artifactRef: string;
  }): Promise<void> {
    const sha256 = this.validateArtifactRef(artifactRef);
    await this.runArtifactOperation(sha256, async () => {
      await this.resolveArtifactPath(sha256);
      this.retainExpected({ leaseRef, artifactRef });
    });
  }

  async releaseLease({
    leaseRef,
    disposition,
  }: Parameters<IFirmwareArtifactAdapter['releaseLease']>[0]): Promise<void> {
    if (
      disposition !== 'completed' &&
      disposition !== 'safeCancelled' &&
      disposition !== 'safeAbandoned'
    ) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware lease disposition is invalid',
      );
    }
    const lease = this.leases.get(this.validateLeaseRef(leaseRef));
    if (!lease) {
      throw new this.ErrorClass(
        'ARTIFACT_LEASE_UNAVAILABLE',
        'Firmware artifact lease is unavailable',
      );
    }
    this.leases.delete(leaseRef);
    this.clearCancellationIfIdle(lease.transactionId);
  }

  async sweepOrphans(): Promise<{
    deletedFiles: number;
    deletedBytes: number;
  }> {
    const files = await readdir(this.rootPath, { withFileTypes: true }).catch(
      () => [],
    );
    const now = Date.now();
    let deletedFiles = 0;
    let deletedBytes = 0;
    for (const entry of files) {
      const filePath = path.join(this.rootPath, entry.name);
      const sha256 = entry.name.slice(0, 64);
      let size: number | undefined;
      if (entry.isDirectory() && ARCHIVE_SCRATCH_PATTERN.test(entry.name)) {
        size = await this.runArtifactOperation(entry.name, async () => {
          if (this.activeArchives.has(filePath)) return undefined;
          const fileStat = await lstat(filePath).catch(() => undefined);
          if (
            !fileStat?.isDirectory() ||
            now - fileStat.mtimeMs < ARCHIVE_SCRATCH_GRACE_MS
          ) {
            return undefined;
          }
          const scratchBytes = await scratchDirectoryBytes(filePath);
          if (this.activeArchives.has(filePath)) return undefined;
          await rm(filePath, { recursive: true, force: true });
          return scratchBytes;
        });
      } else if (entry.isFile() && /^[a-f0-9]{64}$/u.test(sha256)) {
        const grace = entry.name.endsWith('.bin')
          ? FINAL_ARTIFACT_GRACE_MS
          : entry.name.endsWith('.partial')
            ? PARTIAL_ARTIFACT_GRACE_MS
            : undefined;
        if (grace === undefined) continue;
        size = await this.runArtifactOperation(sha256, async () => {
          const fileStat = await lstat(filePath).catch(() => undefined);
          if (
            !fileStat?.isFile() ||
            now - fileStat.mtimeMs < grace ||
            this.isProtectedArtifact(sha256, filePath)
          ) {
            return undefined;
          }
          await rm(filePath, { force: true });
          return fileStat.size;
        });
      }
      if (size !== undefined) {
        deletedFiles += 1;
        deletedBytes += size;
      }
    }
    return { deletedFiles, deletedBytes };
  }

  private isProtectedArtifact(sha256: string, filePath: string): boolean {
    const artifactRef = `fw:${sha256}`;
    for (const lease of this.leases.values()) {
      if (lease.artifactRefs.has(artifactRef)) return true;
    }
    for (const [key, download] of this.downloads) {
      if (key.startsWith(sha256) || download.artifactSha256 === sha256) {
        return true;
      }
    }
    for (const activeSha256 of this.activeArchives.values()) {
      if (activeSha256 === sha256) return true;
    }
    for (const reader of this.readers.values()) {
      if (reader.filePath === filePath) return true;
    }
    return false;
  }

  private validateDownloadInput(input: IDownloadInput): void {
    const url = new URL(input.url);
    const allowPreReleaseHosts = input.allowPreReleaseHosts === true;
    if (!isFirmwareArtifactUrlAllowed(url, { allowPreReleaseHosts })) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware URL is outside the reviewed artifact host allowlist',
      );
    }
    if (input.expectedSize !== undefined) {
      assertSafeInteger(input.expectedSize, 'expectedSize', this.ErrorClass);
    }
    assertSafeInteger(input.maxBytes, 'maxBytes', this.ErrorClass);
    if (
      input.maxBytes > MAX_ARTIFACT_BYTES ||
      (input.expectedSize !== undefined &&
        input.expectedSize > input.maxBytes) ||
      (input.expectedSha256 !== undefined &&
        !SHA256_PATTERN.test(input.expectedSha256)) ||
      !TASK_ID_PATTERN.test(input.taskId) ||
      !IDENTIFIER_PATTERN.test(input.transactionId) ||
      !IDENTIFIER_PATTERN.test(input.artifactId) ||
      !LEASE_REF_PATTERN.test(input.leaseRef) ||
      !Number.isFinite(input.overallDeadlineSeconds) ||
      input.overallDeadlineSeconds <= 0
    ) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware artifact constraints are invalid',
      );
    }
    if (input.route.routeType !== 'domain') {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware route constraints are invalid',
      );
    }
  }

  private async downloadLocked(
    input: IDownloadInput,
    downloadToken: string,
    markActiveArtifact: (sha256: string) => void,
  ): Promise<IDownloadReceipt> {
    this.assertNotCancelled(input.transactionId);
    await mkdir(this.rootPath, { recursive: true });
    // An orphan deletion already in progress must finish before reuse or resume.
    await this.artifactOperations.get(downloadToken)?.catch(() => undefined);
    this.assertNotCancelled(input.transactionId);
    const expectedSha256 = input.expectedSha256?.toLowerCase();
    if (expectedSha256) {
      const finalPath = this.artifactPath(expectedSha256);
      if (
        await this.isStoredArtifactValid(
          finalPath,
          input.expectedSize,
          expectedSha256,
          input.maxBytes,
        )
      ) {
        const fileStat = await stat(finalPath);
        return {
          artifactRef: `fw:${expectedSha256}`,
          size: fileStat.size,
          sha256: expectedSha256,
        };
      }
    }

    const partialPath = path.join(
      this.rootPath,
      `${downloadToken}.${input.taskId}.${createHash('sha256')
        .update(input.transactionId)
        .digest('hex')
        .slice(0, 16)}.partial`,
    );
    let partialSize = await stat(partialPath)
      .then((value) => value.size)
      .catch(() => 0);
    if (partialSize > 0) {
      if (
        expectedSha256 &&
        (input.expectedSize === undefined || partialSize === input.expectedSize) &&
        (await this.isStoredArtifactValid(
          partialPath,
          input.expectedSize,
          expectedSha256,
          input.maxBytes,
        ))
      ) {
        this.assertNotCancelled(input.transactionId);
        await this.runArtifactOperation(expectedSha256, () =>
          this.promoteArtifact({
            sourcePath: partialPath,
            size: partialSize,
            sha256: expectedSha256,
          }),
        );
        return {
          artifactRef: `fw:${expectedSha256}`,
          size: partialSize,
          sha256: expectedSha256,
        };
      }
      if (
        !expectedSha256 ||
        partialSize > input.maxBytes ||
        (input.expectedSize !== undefined && partialSize >= input.expectedSize)
      ) {
        await rm(partialPath, { force: true });
        partialSize = 0;
      }
    }
    await this.streamResponseToFile(input, partialPath, partialSize);
    this.assertNotCancelled(input.transactionId);
    const completedStat = await stat(partialPath);
    const actualSha256 = await hashFile(partialPath);
    if (
      completedStat.size <= 0 ||
      completedStat.size > input.maxBytes ||
      (input.expectedSize !== undefined &&
        completedStat.size !== input.expectedSize) ||
      (expectedSha256 !== undefined && actualSha256 !== expectedSha256)
    ) {
      await rm(partialPath, { force: true });
      throw new this.ErrorClass(
        'ARTIFACT_INTEGRITY_FAILED',
        'Firmware artifact size or SHA-256 is invalid',
      );
    }
    this.assertNotCancelled(input.transactionId);
    markActiveArtifact(actualSha256);
    await this.runArtifactOperation(actualSha256, () =>
      this.promoteArtifact({
        sourcePath: partialPath,
        size: completedStat.size,
        sha256: actualSha256,
      }),
    );
    return {
      artifactRef: `fw:${actualSha256}`,
      size: completedStat.size,
      sha256: actualSha256,
    };
  }

  private async streamResponseToFile(
    input: IDownloadInput,
    partialPath: string,
    resumeOffset: number,
  ): Promise<void> {
    const timeout = Math.min(
      Math.ceil(input.overallDeadlineSeconds * 1000),
      30 * 60 * 1000,
    );
    const abortController = new AbortController();
    const timeoutId = setTimeout(() => abortController.abort(), timeout);
    const unregister = this.registerCancellation(input.transactionId, () =>
      abortController.abort(),
    );
    const request = (offset: number) =>
      this.fetch(input.url, {
        method: 'GET',
        headers: {
          'Accept-Encoding': 'identity',
          ...(offset > 0 ? { Range: `bytes=${offset}-` } : {}),
        },
        redirect: 'error',
        credentials: 'omit',
        cache: 'no-store',
        signal: abortController.signal,
      });
    try {
      let response = await request(resumeOffset);
      if (resumeOffset > 0 && response.status === 416) {
        void response.body?.cancel().catch(() => undefined);
        await rm(partialPath, { force: true });
        abortController.signal.throwIfAborted();
        resumeOffset = 0;
        response = await request(resumeOffset);
      }
      if (response.status !== 200 && response.status !== 206) {
        void response.body?.cancel().catch(() => undefined);
        throw new this.ErrorClass(
          `ARTIFACT_HTTP_${response.status}`,
          'Firmware request failed',
        );
      }
      if (!response.body) {
        throw new this.ErrorClass(
          'ARTIFACT_PROTOCOL_INVALID',
          'Firmware response body is missing',
        );
      }
      await this.writeResponseBody({
        input,
        partialPath,
        resumeOffset,
        statusCode: response.status,
        contentRange: response.headers.get('content-range') ?? undefined,
        contentEncoding: response.headers.get('content-encoding') ?? undefined,
        body: Readable.fromWeb(response.body as unknown as NodeReadableStream),
        signal: abortController.signal,
      });
    } catch (error) {
      if (error instanceof this.ErrorClass) {
        throw error;
      }
      if (this.cancelledTransactions.has(input.transactionId)) {
        throw this.createCancelledError();
      }
      throw new this.ErrorClass(
        'ARTIFACT_NETWORK_FAILED',
        error instanceof Error ? error.message : String(error),
        { cause: error },
      );
    } finally {
      clearTimeout(timeoutId);
      unregister();
    }
  }

  private assertNotCancelled(transactionId: string): void {
    if (this.cancelledTransactions.has(transactionId)) {
      throw this.createCancelledError();
    }
  }

  private clearCancellationIfIdle(transactionId: string): void {
    if (!this.cancelledTransactions.has(transactionId)) return;
    for (const lease of this.leases.values()) {
      if (lease.transactionId === transactionId) return;
    }
    for (const download of this.downloads.values()) {
      if (download.transactionId === transactionId) return;
    }
    this.cancelledTransactions.delete(transactionId);
  }

  private createCancelledError(): Error {
    return new this.ErrorClass(
      'ARTIFACT_CANCELLED',
      'Firmware artifact download was cancelled',
    );
  }

  private registerCancellation(
    transactionId: string,
    cancel: () => void,
  ): () => void {
    const callbacks =
      this.cancellationCallbacks.get(transactionId) ?? new Set<() => void>();
    callbacks.add(cancel);
    this.cancellationCallbacks.set(transactionId, callbacks);
    if (this.cancelledTransactions.has(transactionId)) cancel();
    return () => {
      callbacks.delete(cancel);
      if (!callbacks.size) this.cancellationCallbacks.delete(transactionId);
    };
  }

  private async writeResponseBody({
    input,
    partialPath,
    resumeOffset,
    statusCode,
    contentRange,
    contentEncoding,
    body,
    signal,
  }: {
    input: IDownloadInput;
    partialPath: string;
    resumeOffset: number;
    statusCode: number;
    contentRange: string | undefined;
    contentEncoding: string | undefined;
    body: Readable;
    signal: AbortSignal;
  }): Promise<void> {
    const append = resumeOffset > 0 && statusCode === 206;
    if (
      statusCode === 206 &&
      !this.isValidContentRange(
        contentRange,
        append ? resumeOffset : 0,
        input.expectedSize,
        input.maxBytes,
      )
    ) {
      body.destroy();
      throw new this.ErrorClass(
        'ARTIFACT_PROTOCOL_INVALID',
        'Firmware Content-Range is invalid',
      );
    }
    if (contentEncoding && contentEncoding !== 'identity') {
      body.destroy();
      throw new this.ErrorClass(
        'ARTIFACT_PROTOCOL_INVALID',
        'Firmware response content encoding is invalid',
      );
    }
    let written = append ? resumeOffset : 0;
    const ErrorClass = this.ErrorClass;
    const limiter = new Transform({
      transform(chunk: Buffer, _encoding, callback) {
        written += chunk.byteLength;
        if (written > input.maxBytes) {
          callback(
            new ErrorClass(
              'ARTIFACT_PROTOCOL_INVALID',
              'Firmware response exceeds maxBytes',
            ),
          );
        } else {
          callback(null, chunk);
        }
      },
    });
    await pipeline(
      body,
      limiter,
      createWriteStream(partialPath, {
        flags: append ? 'a' : 'w',
      }),
      { signal },
    );
  }

  private isValidContentRange(
    value: string | undefined,
    expectedStart: number,
    expectedTotal: number | undefined,
    maxBytes: number,
  ): boolean {
    const match = /^bytes ([0-9]+)-([0-9]+)\/([0-9]+)$/iu.exec(value ?? '');
    if (!match) return false;
    const start = Number(match[1]);
    const end = Number(match[2]);
    const total = Number(match[3]);
    return (
      Number.isSafeInteger(start) &&
      Number.isSafeInteger(end) &&
      Number.isSafeInteger(total) &&
      start === expectedStart &&
      end >= start &&
      end < total &&
      (expectedTotal !== undefined
        ? total === expectedTotal
        : total > 0 && total <= maxBytes)
    );
  }

  private validateExpectedEntries(
    entries: NonNullable<IMaterializeInput['expectedEntries']>,
  ): Map<string, IArchiveRequirement> {
    if (!entries.length || entries.length > MAX_ARCHIVE_ENTRIES) {
      throw new this.ErrorClass(
        'ARTIFACT_ARCHIVE_INVALID',
        'Firmware archive expected entry count is invalid',
      );
    }
    const result = new Map<string, IArchiveRequirement>();
    const canonicalNames = new Set<string>();
    let totalSize = 0;
    for (const entry of entries) {
      validatePortableEntryName(entry.entryName, canonicalNames, this.ErrorClass);
      assertSafeInteger(entry.expectedSize, 'archive entry size', this.ErrorClass);
      totalSize += entry.expectedSize;
      if (
        entry.expectedSize > MAX_ARCHIVE_ENTRY_BYTES ||
        totalSize > MAX_ARTIFACT_BYTES ||
        !SHA256_PATTERN.test(entry.expectedSha256) ||
        result.has(entry.entryName)
      ) {
        throw new this.ErrorClass(
          'ARTIFACT_ARCHIVE_INVALID',
          'Firmware archive expected entry is invalid',
        );
      }
      result.set(entry.entryName, entry);
    }
    return result;
  }

  private async validateArchive(
    filePath: string,
    expectedRequirements: Map<string, IArchiveRequirement> | undefined,
  ): Promise<Map<string, IArchiveRequirement>> {
    const zipFile = await openZip(
      filePath,
      {
        autoClose: true,
        lazyEntries: true,
        strictFileNames: true,
        validateEntrySizes: true,
      },
      this.ErrorClass,
    );
    if (zipFile.entryCount > MAX_ARCHIVE_ENTRIES) {
      zipFile.close();
      throw new this.ErrorClass(
        'ARTIFACT_ARCHIVE_INVALID',
        'Firmware archive has too many entries',
      );
    }
    return new Promise<Map<string, IArchiveRequirement>>((resolve, reject) => {
      const names = new Set<string>();
      const canonicalNames = new Set<string>();
      const requirements = new Map<string, IArchiveRequirement>();
      let entryCount = 0;
      let totalSize = 0;
      zipFile.on('entry', (entry) => {
        try {
          entryCount += 1;
          if (entryCount > MAX_ARCHIVE_ENTRIES) {
            throw new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive has too many entries',
            );
          }
          const entryName = String(entry.fileName);
          const expectedRequirement = expectedRequirements?.get(entryName);
          const expectedSize =
            expectedRequirement?.expectedSize ?? entry.uncompressedSize;
          const hostSystem = entry.versionMadeBy >> 8;
          const fileType =
            (entry.externalFileAttributes >>> 16) & UNIX_FILE_TYPE_MASK;
          const usesUnixAttributes = hostSystem === 3 || hostSystem === 19;
          const isDirectoryEntry = entryName.endsWith('/');
          const isRegular = usesUnixAttributes
            ? fileType === 0 || fileType === UNIX_REGULAR_FILE_TYPE
            : (entry.externalFileAttributes & 0x10) === 0;
          const hasDirectoryAttributes = usesUnixAttributes
            ? fileType === UNIX_DIRECTORY_FILE_TYPE
            : !isRegular;
          const isEncrypted = (entry.generalPurposeBitFlag & 1) !== 0;
          validatePortableEntryName(
            isDirectoryEntry ? entryName.slice(0, -1) : entryName,
            canonicalNames,
            this.ErrorClass,
          );
          if (isDirectoryEntry) {
            if (
              !hasDirectoryAttributes ||
              entry.uncompressedSize !== 0 ||
              entry.compressedSize !== 0 ||
              isEncrypted ||
              entry.compressionMethod !== 0
            ) {
              throw new this.ErrorClass(
                'ARTIFACT_ARCHIVE_INVALID',
                'Firmware archive directory metadata is invalid',
              );
            }
            zipFile.readEntry();
            return;
          }
          totalSize += entry.uncompressedSize;
          if (
            (expectedRequirements && !expectedRequirement) ||
            names.has(entryName) ||
            entryName.endsWith('/') ||
            entry.uncompressedSize !== expectedSize ||
            entry.uncompressedSize <= 0 ||
            entry.uncompressedSize > MAX_ARCHIVE_ENTRY_BYTES ||
            totalSize > MAX_ARTIFACT_BYTES ||
            entry.compressedSize < 0 ||
            entry.uncompressedSize > Math.max(entry.compressedSize, 1) * 1000 ||
            isEncrypted ||
            (entry.compressionMethod !== 0 && entry.compressionMethod !== 8) ||
            !isRegular
          ) {
            throw new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive entry metadata does not match the approved entry set',
            );
          }
          names.add(entryName);
          requirements.set(entryName, {
            entryName,
            expectedSize,
            ...(expectedRequirement?.expectedSha256
              ? { expectedSha256: expectedRequirement.expectedSha256 }
              : {}),
          });
          zipFile.readEntry();
        } catch (error) {
          zipFile.close();
          reject(
            new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive entry metadata is invalid',
              { cause: error },
            ),
          );
        }
      });
      zipFile.once('error', reject);
      zipFile.once('end', () => {
        if (!names.size) {
          reject(
            new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive has no materializable files',
            ),
          );
        } else if (expectedRequirements && names.size !== expectedRequirements.size) {
          reject(
            new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive has missing or extra entries',
            ),
          );
        } else {
          resolve(requirements);
        }
      });
      zipFile.readEntry();
    });
  }

  private async extractArchive(
    archivePath: string,
    scratchPath: string,
    requirements: Map<string, IArchiveRequirement>,
  ): Promise<IStagedEntry[]> {
    const zipFile = await openZip(
      archivePath,
      {
        autoClose: false,
        lazyEntries: true,
        strictFileNames: true,
        validateEntrySizes: true,
      },
      this.ErrorClass,
    );
    return new Promise((resolve, reject) => {
      const staged: IStagedEntry[] = [];
      let settled = false;
      const fail = (error: unknown) => {
        if (settled) return;
        settled = true;
        zipFile.close();
        reject(error);
      };
      zipFile.on('error', fail);
      zipFile.on('end', () => {
        if (settled) return;
        settled = true;
        zipFile.close();
        if (staged.length !== requirements.size) {
          reject(
            new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive extraction is incomplete',
            ),
          );
        } else {
          resolve(staged);
        }
      });
      zipFile.on('entry', (entry) => {
        void (async () => {
          const entryName = String(entry.fileName);
          if (entryName.endsWith('/')) {
            zipFile.readEntry();
            return;
          }
          const requirement = requirements.get(entryName);
          if (!requirement) {
            throw new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive contains an unexpected entry',
            );
          }
          const filePath = path.join(scratchPath, `${staged.length}.entry`);
          const digest = createHash('sha256');
          let size = 0;
          const ErrorClass = this.ErrorClass;
          const verifier = new Transform({
            transform(chunk: Buffer, _encoding, callback) {
              size += chunk.byteLength;
              if (size > requirement.expectedSize) {
                callback(
                  new ErrorClass(
                    'ARTIFACT_ARCHIVE_INVALID',
                    'Firmware archive entry exceeds its expected size',
                  ),
                );
              } else {
                digest.update(chunk);
                callback(null, chunk);
              }
            },
          });
          await pipeline(
            await openZipEntry(zipFile, entry, this.ErrorClass),
            verifier,
            createWriteStream(filePath, { flags: 'wx' }),
          );
          const expectedSha256 = requirement.expectedSha256?.toLowerCase();
          const sha256 = digest.digest('hex');
          if (
            size !== requirement.expectedSize ||
            (expectedSha256 !== undefined && sha256 !== expectedSha256)
          ) {
            throw new this.ErrorClass(
              'ARTIFACT_ARCHIVE_INVALID',
              'Firmware archive entry integrity is invalid',
            );
          }
          staged.push({
            entryName,
            size,
            sha256,
            filePath,
          });
          zipFile.readEntry();
        })().catch(fail);
      });
      zipFile.readEntry();
    });
  }

  private artifactPath(sha256: string): string {
    return path.join(this.rootPath, `${sha256}.bin`);
  }

  private assertLease(leaseRef: string): void {
    if (!this.leases.has(this.validateLeaseRef(leaseRef))) {
      throw new this.ErrorClass(
        'ARTIFACT_LEASE_UNAVAILABLE',
        'Firmware artifact lease is unavailable',
      );
    }
  }

  private assertLeaseTransaction(
    leaseRef: string,
    transactionId: string,
  ): void {
    const lease = this.leases.get(this.validateLeaseRef(leaseRef));
    if (!lease || lease.transactionId !== transactionId) {
      throw new this.ErrorClass(
        'ARTIFACT_LEASE_MISMATCH',
        'Firmware artifact lease transaction does not match',
      );
    }
  }

  private async runArtifactOperation<T>(
    key: string,
    operation: () => Promise<T>,
  ): Promise<T> {
    const previous = this.artifactOperations.get(key) ?? Promise.resolve();
    const current = previous.catch(() => undefined).then(operation);
    this.artifactOperations.set(key, current);
    try {
      return await current;
    } finally {
      if (this.artifactOperations.get(key) === current) {
        this.artifactOperations.delete(key);
      }
    }
  }

  private async promoteArtifact({
    sourcePath,
    size,
    sha256,
  }: {
    sourcePath: string;
    size: number;
    sha256: string;
  }): Promise<void> {
    const destination = this.artifactPath(sha256);
    if (await this.isStoredArtifactValid(destination, size, sha256)) {
      await rm(sourcePath, { force: true });
      return;
    }
    await rm(destination, { force: true });
    await rename(sourcePath, destination);
  }

  private retainExpected({
    leaseRef,
    transactionId,
    artifactRef,
  }: {
    leaseRef: string;
    transactionId?: string;
    artifactRef: string;
  }): void {
    this.validateArtifactRef(artifactRef);
    const lease = this.leases.get(this.validateLeaseRef(leaseRef));
    if (!lease) {
      throw new this.ErrorClass(
        'ARTIFACT_LEASE_UNAVAILABLE',
        'Firmware artifact lease is unavailable',
      );
    }
    if (transactionId && lease.transactionId !== transactionId) {
      throw new this.ErrorClass(
        'ARTIFACT_LEASE_MISMATCH',
        'Firmware artifact lease transaction does not match',
      );
    }
    lease.artifactRefs.add(artifactRef);
  }

  private validateLeaseRef(leaseRef: string): string {
    if (!LEASE_REF_PATTERN.test(leaseRef)) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware leaseRef is invalid',
      );
    }
    return leaseRef;
  }

  private validateArtifactRef(artifactRef: string): string {
    if (!ARTIFACT_REF_PATTERN.test(artifactRef)) {
      throw new this.ErrorClass(
        'ARTIFACT_INVALID_INPUT',
        'Firmware artifactRef is invalid',
      );
    }
    return artifactRef.slice(3);
  }

  private async resolveArtifactPath(sha256: string): Promise<string> {
    const filePath = this.artifactPath(sha256);
    if ((await hashFile(filePath).catch(() => '')) !== sha256) {
      throw new this.ErrorClass(
        'ARTIFACT_INTEGRITY_FAILED',
        'Firmware artifact is missing or corrupt',
      );
    }
    return filePath;
  }

  private async isStoredArtifactValid(
    filePath: string,
    expectedSize: number | undefined,
    expectedSha256: string | undefined,
    maxBytes = MAX_ARTIFACT_BYTES,
  ): Promise<boolean> {
    try {
      const fileStat = await stat(filePath);
      return (
        fileStat.isFile() &&
        fileStat.size > 0 &&
        fileStat.size <= maxBytes &&
        (expectedSize === undefined || fileStat.size === expectedSize) &&
        (expectedSha256 === undefined ||
          (await hashFile(filePath)) === expectedSha256)
      );
    } catch {
      return false;
    }
  }
}
