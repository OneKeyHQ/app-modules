import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import AdmZip from 'adm-zip';
import semver from 'semver';

import { downloadNodeFile, stripSensitiveHeaders } from './download.js';
import {
  EUpdateErrorCode,
  createUpdateError,
  getUpdateError,
  isRetryableUpdateError,
} from './errors.js';
import { assertSafeBundleVersion, resolveBundlePath } from './paths.js';
import {
  calculateSHA256,
  checkFileSha512,
  ArchiveSizeError,
  hashArchive,
  readSignedMetadataSha256,
  SignedMetadataError,
  verifySha256,
} from './verification.js';
import type { TUpdateErrorStep } from './errors.js';
import type { ISignedMetadataIdentity } from './verification.js';
import type {
  IBundleUpdateIdentity,
  IBundleUpdateNodeOptions,
  IDownloadPackageParams,
  IInstalledBundle,
  IUpdateDownloadedEvent,
  IUpdateOperationParams,
} from './types.js';

export const MAX_BUNDLE_UNCOMPRESSED_BYTES = 512 * 1024 * 1024;
export const MAX_BUNDLE_ENTRY_COUNT = 4096;
export const MAX_CONTROL_JSON_BYTES = 16 * 1024 * 1024;
export const DIFF_DOWNLOAD_DIR_NAME = '.diff';
export const DIFF_STAGING_DIR_NAME = '.diff-staging';

// Keep the download boundary limited to the shared updater payload.
function wrapDownloadError(error: unknown) {
  return createUpdateError(getUpdateError(error, 'download'));
}

function throwIfCancelled(
  signal: AbortSignal | undefined,
  step: TUpdateErrorStep
) {
  if (signal?.aborted) {
    throw createUpdateError({ code: EUpdateErrorCode.cancelled, step });
  }
}

function assertUpdateBundleVersion(
  value: unknown,
  step: TUpdateErrorStep
): asserts value is string {
  try {
    assertSafeBundleVersion(value);
  } catch {
    throw createUpdateError({ code: EUpdateErrorCode.invalidArgument, step });
  }
}

function wrapBundleError(error: unknown, step: TUpdateErrorStep) {
  const known = getUpdateError(error, step);
  if (known.code !== EUpdateErrorCode.unknown) return createUpdateError(known);
  const errno = (error as NodeJS.ErrnoException | undefined)?.code;
  if (errno === 'ENOENT' || errno === 'ENOTDIR') {
    return createUpdateError({ code: EUpdateErrorCode.packageMissing, step });
  }
  if (['EACCES', 'EPERM', 'ENOSPC', 'EIO', 'EROFS'].includes(errno ?? '')) {
    return createUpdateError({ code: EUpdateErrorCode.storageError, step });
  }
  if (error instanceof SyntaxError) {
    return createUpdateError({ code: EUpdateErrorCode.integrityFailed, step });
  }
  return createUpdateError({ code: EUpdateErrorCode.unknown, step });
}

function wrapSignedMetadataError(error: unknown, step: TUpdateErrorStep) {
  if (
    error instanceof SignedMetadataError &&
    getUpdateError(error, step).code === EUpdateErrorCode.unknown
  ) {
    return createUpdateError({ code: EUpdateErrorCode.integrityFailed, step });
  }
  return wrapBundleError(error, step);
}

function isValidHttpsUrl(url: unknown): url is string {
  if (typeof url !== 'string' || !url.startsWith('https://')) {
    return false;
  }
  try {
    return new URL(url).protocol === 'https:';
  } catch {
    return false;
  }
}

function isValidSha256Hex(hash: unknown): hash is string {
  return typeof hash === 'string' && /^[a-fA-F0-9]{64}$/.test(hash.trim());
}

function isValidSha512Hex(hash: unknown): hash is string {
  return typeof hash === 'string' && /^[a-fA-F0-9]{128}$/.test(hash.trim());
}

function normalizeSha256Hex(hash: unknown): string {
  return isValidSha256Hex(hash) ? hash.trim().toLowerCase() : '';
}

function isSafeRelativeManifestPath(relPath: unknown): relPath is string {
  if (typeof relPath !== 'string' || !relPath || relPath.startsWith('/')) {
    return false;
  }
  if (
    relPath.includes('\\') ||
    relPath.includes('\0') ||
    path.isAbsolute(relPath)
  ) {
    return false;
  }
  const segments = relPath.split('/');
  return segments.every((seg) => seg.length > 0 && seg !== '.' && seg !== '..');
}

function hasSymlinkOnPathWithinRoot(
  rootDir: string,
  targetPath: string
): boolean {
  const resolvedRoot = path.resolve(rootDir);
  const resolvedTarget = path.resolve(targetPath);
  if (
    resolvedTarget !== resolvedRoot &&
    !resolvedTarget.startsWith(`${resolvedRoot}${path.sep}`)
  ) {
    return true;
  }
  try {
    if (
      fs.lstatSync(resolvedRoot, { throwIfNoEntry: false })?.isSymbolicLink()
    ) {
      return true;
    }
  } catch {
    // ignore virtual path lstat error (e.g. inside app.asar)
  }
  const rel = path.relative(resolvedRoot, resolvedTarget);
  if (!rel) {
    return false;
  }
  const parts = rel.split(path.sep);
  let current = resolvedRoot;
  for (const part of parts) {
    current = path.join(current, part);
    try {
      const stat = fs.lstatSync(current, { throwIfNoEntry: false });
      if (!stat) {
        break;
      }
      if (stat.isSymbolicLink()) {
        return true;
      }
    } catch {
      // ignore virtual path lstat error (e.g. inside app.asar)
    }
  }
  try {
    if (fs.existsSync(resolvedRoot) && fs.existsSync(resolvedTarget)) {
      const realRoot = fs.realpathSync(resolvedRoot);
      const realTarget = fs.realpathSync(resolvedTarget);
      if (
        realTarget !== realRoot &&
        !realTarget.startsWith(`${realRoot}${path.sep}`)
      ) {
        return true;
      }
    }
  } catch {
    // ignore realpath error on virtual asar paths
  }
  return false;
}

function normalizePositiveInt(value: unknown): number | undefined {
  return typeof value === 'number' && Number.isSafeInteger(value) && value > 0
    ? value
    : undefined;
}

interface IDiffManifest {
  schemaVersion: number;
  appType: string;
  appVersion: string;
  targetBundleVersion: string;
  hashAlgorithm: string;
  targetMetadataSha256: string;
  changedFiles: Record<string, string>;
  reusedFiles: Record<string, string>;
  deletedFiles?: string[];
}

interface IDiffArchiveDescriptor {
  appVersion: string;
  bundleVersion: string;
  downloadUrl: string;
  fileSize?: number;
  sha256: string;
  diffSha256: string;
  signature?: string;
  fallbackRequired?: boolean;
}

interface IDownloadRun {
  releaseKey: string;
  controller: AbortController;
  fullWriter: boolean;
  run: Promise<IUpdateDownloadedEvent>;
}

const diffGpgErrors = new WeakSet<object>();

export class BundleUpdateNode {
  private diffFallbackTargets = new Set<string>();

  private activeVerifications = new Map<
    string,
    {
      releaseKey: string;
      verificationKey: string;
      controller: AbortController;
      run: Promise<void>;
    }
  >();

  private inflightDownloads = new Map<string, IDownloadRun>();

  private activePackageVerifications = new Set<{
    controller: AbortController;
    run: Promise<void>;
  }>();

  private activeArchiveDownloads = 0;

  private cacheClearPromise: Promise<void> | undefined;

  get isDownloading(): boolean {
    return this.activeArchiveDownloads > 0;
  }

  constructor(private readonly options: IBundleUpdateNodeOptions) {}

  private isSkipGPGAllowed() {
    return this.options.isDevelopmentVerificationOverrideEnabled?.() === true;
  }

  private getBundleDirName() {
    const bundleDir = path.join(this.options.userDataDir(), 'onekey-bundle');
    fs.mkdirSync(bundleDir, { recursive: true });
    return bundleDir;
  }

  private getBundleExtractDir({
    appVersion,
    bundleVersion,
  }: IBundleUpdateIdentity) {
    assertSafeBundleVersion(appVersion);
    assertSafeBundleVersion(bundleVersion);
    return resolveBundlePath(
      this.getBundleDirName(),
      `${appVersion}-${bundleVersion}`
    );
  }

  private readMetadataFileSha256(
    signature: string,
    expected: ISignedMetadataIdentity
  ) {
    return readSignedMetadataSha256(
      signature,
      this.options.publicKey,
      expected
    );
  }

  private async verifySignedMetadata(
    { appVersion, bundleVersion, signature }: IInstalledBundle,
    step: TUpdateErrorStep
  ) {
    try {
      const metadataFilePath = this.getMetadataFilePath({
        appVersion,
        bundleVersion,
      });
      this.assertExtractedMetadataPath(metadataFilePath, step);
      const hash = await this.readMetadataFileSha256(signature, {
        appVersion,
        bundleVersion,
      });
      if (!verifySha256(metadataFilePath, hash, this.options.logger)) {
        throw new SignedMetadataError('Invalid metadata hash');
      }
    } catch (error) {
      throw wrapSignedMetadataError(error, step);
    }
  }

  getDownloadDir() {
    const tempDir = path.join(
      this.options.userDataDir(),
      'onekey-bundle-download'
    );
    if (!fs.existsSync(tempDir)) {
      fs.mkdirSync(tempDir, { recursive: true });
    }
    this.options.logger?.info('bundle-download-getDownloadDir', tempDir);
    return tempDir;
  }

  private getReleaseKey(
    appVersion: string,
    bundleVersion: string,
    sha256: string
  ): string {
    return JSON.stringify([appVersion, bundleVersion, sha256]);
  }

  // The destination zip path for a (appVersion, bundleVersion) pair.
  private getDestZipPath(params: {
    latestVersion: string;
    bundleVersion: string;
  }): string {
    const { latestVersion: appVersion, bundleVersion } = params;
    assertSafeBundleVersion(appVersion);
    assertSafeBundleVersion(bundleVersion);
    return resolveBundlePath(
      this.getDownloadDir(),
      `${appVersion}-${bundleVersion}.zip`
    );
  }

  private getDiffDownloadDir() {
    const diffDir = path.join(this.getDownloadDir(), DIFF_DOWNLOAD_DIR_NAME);
    if (!fs.existsSync(diffDir)) {
      fs.mkdirSync(diffDir, { recursive: true });
    }
    return diffDir;
  }

  private getDestDiffZipPath(params: {
    latestVersion: string;
    bundleVersion: string;
  }): string {
    const { latestVersion: appVersion, bundleVersion } = params;
    assertSafeBundleVersion(appVersion);
    assertSafeBundleVersion(bundleVersion);
    return resolveBundlePath(
      this.getDiffDownloadDir(),
      `${appVersion}-${bundleVersion}.diff.zip`
    );
  }

  private getDestDiffDescriptorPath(diffZipPath: string): string {
    return `${diffZipPath}.meta.json`;
  }

  private readDiffDescriptor(
    diffZipPath: string
  ): IDiffArchiveDescriptor | null {
    const metaPath = this.getDestDiffDescriptorPath(diffZipPath);
    try {
      const stat = fs.lstatSync(metaPath, { throwIfNoEntry: false });
      if (!stat || !stat.isFile() || stat.size <= 0 || stat.size > 65_536) {
        return null;
      }
      const raw = JSON.parse(
        fs.readFileSync(metaPath, 'utf8')
      ) as Partial<IDiffArchiveDescriptor>;
      const sha256 = normalizeSha256Hex(raw?.sha256);
      const diffSha256 = normalizeSha256Hex(raw?.diffSha256);
      if (
        !raw ||
        typeof raw.appVersion !== 'string' ||
        !raw.appVersion ||
        typeof raw.bundleVersion !== 'string' ||
        !raw.bundleVersion ||
        !isValidHttpsUrl(raw.downloadUrl) ||
        !sha256 ||
        !diffSha256
      ) {
        return null;
      }
      return {
        appVersion: raw.appVersion,
        bundleVersion: raw.bundleVersion,
        downloadUrl: raw.downloadUrl,
        fileSize: normalizePositiveInt(raw.fileSize),
        sha256,
        diffSha256,
        signature:
          typeof raw.signature === 'string' && raw.signature.trim().length > 0
            ? raw.signature
            : undefined,
        fallbackRequired: Boolean(raw.fallbackRequired),
      };
    } catch {
      return null;
    }
  }

  private readMatchingDiffDescriptor(
    diffZipPath: string,
    appVersion: string,
    bundleVersion: string,
    sha256: string
  ): IDiffArchiveDescriptor | undefined {
    const descriptor = this.readDiffDescriptor(diffZipPath);
    if (
      descriptor?.appVersion === appVersion &&
      descriptor.bundleVersion === bundleVersion &&
      descriptor.sha256 === sha256
    ) {
      return descriptor;
    }
    return undefined;
  }

  private matchesDiffDigest(
    descriptor: IDiffArchiveDescriptor,
    suppliedDigest: string | undefined
  ) {
    return (
      suppliedDigest === undefined ||
      normalizeSha256Hex(suppliedDigest) === descriptor.diffSha256
    );
  }

  private assertManagedArchive(filePath: string, step: TUpdateErrorStep) {
    if (
      hasSymlinkOnPathWithinRoot(this.getDownloadDir(), filePath) ||
      !fs.lstatSync(filePath).isFile()
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step,
      });
    }
  }

  private writeDiffDescriptor(
    diffZipPath: string,
    descriptor: IDiffArchiveDescriptor,
    step: 'download' | 'verifyASC'
  ) {
    try {
      const existing = this.readDiffDescriptor(diffZipPath);
      const preservedSignature =
        descriptor.signature ??
        (existing &&
        existing.appVersion === descriptor.appVersion &&
        existing.bundleVersion === descriptor.bundleVersion &&
        existing.sha256 === descriptor.sha256 &&
        existing.diffSha256 === descriptor.diffSha256
          ? existing.signature
          : undefined);
      const payload: IDiffArchiveDescriptor = {
        ...descriptor,
        signature: preservedSignature,
      };
      const metaPath = this.getDestDiffDescriptorPath(diffZipPath);
      const tmpPath = `${metaPath}.tmp`;
      fs.mkdirSync(path.dirname(metaPath), { recursive: true });
      fs.writeFileSync(tmpPath, JSON.stringify(payload), 'utf8');
      fs.renameSync(tmpPath, metaPath);
    } catch {
      throw createUpdateError({ code: EUpdateErrorCode.storageError, step });
    }
  }

  private createDiffStagingDir(
    appVersion: string,
    bundleVersion: string
  ): string {
    assertSafeBundleVersion(appVersion);
    assertSafeBundleVersion(bundleVersion);
    const stagingRoot = path.join(
      this.getBundleDirName(),
      DIFF_STAGING_DIR_NAME
    );
    if (
      fs.lstatSync(stagingRoot, { throwIfNoEntry: false })?.isSymbolicLink()
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step: 'verifyASC',
      });
    }
    if (!fs.existsSync(stagingRoot)) {
      fs.mkdirSync(stagingRoot, { recursive: true });
    }
    const runId = crypto.randomUUID().replace(/-/g, '');
    return resolveBundlePath(
      stagingRoot,
      `${appVersion}-${bundleVersion}-${runId}`
    );
  }

  private cleanupDiffArtifacts(diffZipPath: string, keepDescriptor = false) {
    const targets = [
      diffZipPath,
      `${diffZipPath}.partial`,
      `${diffZipPath}.partial.progress.json`,
    ];
    if (!keepDescriptor) {
      targets.push(
        this.getDestDiffDescriptorPath(diffZipPath),
        `${this.getDestDiffDescriptorPath(diffZipPath)}.tmp`
      );
    }
    for (const target of targets) {
      try {
        fs.rmSync(target, { force: true });
      } catch {
        // Ignore cleanup failure.
      }
    }
  }

  async downloadBundle(
    params: IDownloadPackageParams
  ): Promise<IUpdateDownloadedEvent> {
    try {
      while (this.cacheClearPromise) await this.cacheClearPromise;
      return await this.coordinateDownload(params);
    } catch (error) {
      throw wrapBundleError(error, 'download');
    }
  }

  private coordinateDownload(
    params: IDownloadPackageParams,
    fallback?: { signal: AbortSignal; prepare: () => void }
  ): Promise<IUpdateDownloadedEvent> {
    const { latestVersion, bundleVersion, downloadUrl } = params ?? {};
    assertUpdateBundleVersion(latestVersion, 'download');
    assertUpdateBundleVersion(bundleVersion, 'download');
    const sha256 = normalizeSha256Hex(params?.sha256);
    if (!sha256 || !isValidHttpsUrl(downloadUrl)) {
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'download',
      });
    }
    const destination = this.getDestZipPath({ latestVersion, bundleVersion });
    const releaseKey = this.getReleaseKey(latestVersion, bundleVersion, sha256);
    const existing = this.inflightDownloads.get(destination);
    const verification = this.activeVerifications.get(destination);
    if (
      (verification && verification.releaseKey !== releaseKey) ||
      (existing && existing.releaseKey !== releaseKey)
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.incompatible,
        step: 'download',
      });
    }
    throwIfCancelled(fallback?.signal, 'download');
    if (existing) {
      if (!fallback) return existing.run;
      // A full writer never touches the diff archive or descriptor again.
      if (existing.fullWriter) {
        fallback.prepare();
        return existing.run;
      }
      return existing.run.then(
        (result) =>
          result?.diffSha256
            ? this.coordinateDownload(params, fallback)
            : result,
        (error: unknown) => {
          if (
            !fallback.signal.aborted &&
            !this.inflightDownloads.has(destination)
          ) {
            fallback.prepare();
          }
          throw error;
        }
      );
    }

    const controller = new AbortController();
    let resolveRun!: (value: Promise<IUpdateDownloadedEvent>) => void;
    let rejectRun!: (reason: unknown) => void;
    const run = new Promise<IUpdateDownloadedEvent>((resolve, reject) => {
      resolveRun = resolve;
      rejectRun = reject;
    }).finally(() => {
      this.inflightDownloads.delete(destination);
    });
    const operation: IDownloadRun = {
      releaseKey,
      controller,
      fullWriter: false,
      run,
    };
    this.inflightDownloads.set(destination, operation);

    try {
      throwIfCancelled(fallback?.signal, 'download');
      fallback?.prepare();
      resolveRun(
        this.runDownloadBundle(
          { ...params, latestVersion, bundleVersion, downloadUrl, sha256 },
          destination,
          controller.signal,
          Boolean(fallback),
          () => {
            operation.fullWriter = true;
          }
        )
      );
    } catch (error) {
      rejectRun(error);
    }
    return run;
  }

  private async downloadArchiveFile({
    url,
    targetPath,
    identity,
    headers,
    expectedBytes,
    expectedSha256,
    latestVersion,
    bundleVersion,
    signal,
  }: {
    url: string;
    targetPath: string;
    identity: string;
    headers?: Record<string, string>;
    expectedBytes?: number;
    expectedSha256: string;
    latestVersion: string;
    bundleVersion: string;
    signal: AbortSignal;
  }) {
    throwIfCancelled(signal, 'download');
    this.activeArchiveDownloads += 1;
    try {
      if (this.activeArchiveDownloads === 1) {
        this.options.onDownloadStateChange?.(true);
      }
      const download = this.options.download ?? downloadNodeFile;
      await download({
        url,
        targetPath,
        identity,
        headers: stripSensitiveHeaders(headers ?? {}),
        expectedBytes: normalizePositiveInt(expectedBytes),
        expectedSha256,
        signal,
        onProgress: (progress) => {
          this.options.onProgress?.({
            ...progress,
            latestVersion,
            bundleVersion,
          });
        },
      });
      throwIfCancelled(signal, 'download');
    } finally {
      this.activeArchiveDownloads -= 1;
      if (!this.isDownloading) {
        this.options.onDownloadStateChange?.(false);
      }
    }
  }

  // The shared downloader owns range selection, resume, and integrity checks.
  private async runDownloadBundle(
    params: IDownloadPackageParams & {
      latestVersion: string;
      bundleVersion: string;
      downloadUrl: string;
      sha256: string;
    },
    filePath: string,
    signal: AbortSignal,
    forceFull: boolean,
    markFullWriter: () => void
  ): Promise<IUpdateDownloadedEvent> {
    const {
      latestVersion,
      bundleVersion,
      downloadUrl,
      sha256: normalizedSha256,
      signature,
      fileSize,
      diffDownloadUrl,
      diffFileSize,
      diffSha256,
      diffSignature,
      headers,
    } = params;
    const hasFullArchive = fs.lstatSync(filePath, { throwIfNoEntry: false });
    if (hasFullArchive) this.assertManagedArchive(filePath, 'download');
    const allowSkipGPG = this.isSkipGPGAllowed();
    const releaseKey = this.getReleaseKey(
      latestVersion,
      bundleVersion,
      normalizedSha256
    );
    const normalizedDiffSha256 = normalizeSha256Hex(diffSha256);
    const hasValidDiffSignature =
      (typeof diffSignature === 'string' && diffSignature.trim().length > 0) ||
      allowSkipGPG;
    const diffZipPath = this.getDestDiffZipPath(params);
    const existingDescriptor = this.readMatchingDiffDescriptor(
      diffZipPath,
      latestVersion,
      bundleVersion,
      normalizedSha256
    );
    const isDiffEligible =
      !forceFull &&
      !hasFullArchive &&
      isValidHttpsUrl(diffDownloadUrl) &&
      Boolean(normalizedDiffSha256) &&
      hasValidDiffSignature &&
      !this.diffFallbackTargets.has(releaseKey) &&
      !existingDescriptor?.fallbackRequired;

    if (isDiffEligible) {
      try {
        await this.downloadArchiveFile({
          url: diffDownloadUrl,
          targetPath: diffZipPath,
          identity: `bundle-diff:${latestVersion}:${bundleVersion}:${normalizedDiffSha256}`,
          headers,
          expectedBytes: diffFileSize,
          expectedSha256: normalizedDiffSha256,
          latestVersion,
          bundleVersion,
          signal,
        });
        throwIfCancelled(signal, 'download');
        this.writeDiffDescriptor(
          diffZipPath,
          {
            appVersion: latestVersion,
            bundleVersion,
            downloadUrl,
            fileSize: normalizePositiveInt(fileSize),
            sha256: normalizedSha256,
            diffSha256: normalizedDiffSha256,
            fallbackRequired: false,
          },
          'download'
        );
        return {
          downloadUrl,
          latestVersion,
          bundleVersion,
          sha256: normalizedSha256,
          signature,
          diffDownloadUrl,
          diffFileSize,
          diffSha256: normalizedDiffSha256,
          diffSignature,
        };
      } catch (diffError) {
        const wrappedDiffError = wrapDownloadError(diffError);
        const updateError = getUpdateError(wrappedDiffError, 'download');
        if (
          signal.aborted ||
          updateError.code === EUpdateErrorCode.cancelled ||
          updateError.code === EUpdateErrorCode.storageError ||
          isRetryableUpdateError(updateError)
        ) {
          throw wrappedDiffError;
        }
        this.options.logger?.warn(
          'bundle-download',
          'Diff bundle download failed, falling back to full bundle',
          {
            latestVersion,
            bundleVersion,
            error:
              diffError instanceof Error
                ? diffError.message
                : String(diffError),
          }
        );
        this.diffFallbackTargets.add(releaseKey);
        this.cleanupDiffArtifacts(diffZipPath);
      }
    }

    try {
      markFullWriter();
      await this.downloadArchiveFile({
        url: downloadUrl,
        targetPath: filePath,
        identity: `bundle:${latestVersion}:${bundleVersion}:${normalizedSha256}`,
        headers,
        expectedBytes: fileSize,
        expectedSha256: normalizedSha256,
        latestVersion,
        bundleVersion,
        signal,
      });
      throwIfCancelled(signal, 'download');
      return {
        downloadUrl,
        latestVersion,
        bundleVersion,
        sha256: normalizedSha256,
        signature,
      };
    } catch (error) {
      throw wrapDownloadError(error);
    }
  }

  getBundleBuildPath({
    appVersion,
    bundleVersion,
  }: {
    appVersion: string;
    bundleVersion: string;
  }) {
    return path.join(
      this.getBundleExtractDir({ appVersion, bundleVersion }),
      'build'
    );
  }

  getMetadataFilePath({
    appVersion,
    bundleVersion,
  }: {
    appVersion: string;
    bundleVersion: string;
  }) {
    return path.join(
      this.getBundleExtractDir({ appVersion, bundleVersion }),
      'metadata.json'
    );
  }

  async verifyBundle(params: IUpdateOperationParams) {
    try {
      while (this.cacheClearPromise) await this.cacheClearPromise;
      const controller = new AbortController();
      const verification = {
        controller,
        run: this.verifyBundleInternal(params, controller.signal),
      };
      this.activePackageVerifications.add(verification);
      try {
        await verification.run;
      } finally {
        this.activePackageVerifications.delete(verification);
      }
    } catch (error) {
      throw wrapBundleError(error, 'verifyPackage');
    }
  }

  private async verifyArchiveSha256(
    filePath: string,
    expected: string,
    step: 'verifyPackage' | 'verifyASC',
    signal: AbortSignal | undefined
  ): Promise<void> {
    const verifyFullASC =
      step === 'verifyASC' && path.dirname(filePath) === this.getDownloadDir();
    let actual: Awaited<ReturnType<typeof hashArchive>>;
    try {
      actual = await hashArchive(filePath, 'sha256', signal);
    } catch (error) {
      throwIfCancelled(signal, step);
      const errno = (error as NodeJS.ErrnoException | undefined)?.code;
      const code =
        error instanceof ArchiveSizeError
          ? EUpdateErrorCode.securityRejected
          : verifyFullASC
            ? errno === 'ENOENT'
              ? EUpdateErrorCode.packageMissing
              : EUpdateErrorCode.storageError
            : EUpdateErrorCode.integrityFailed;
      throw createUpdateError({ code, step });
    }
    throwIfCancelled(signal, step);
    this.options.logger?.info(
      'bundle-download-verifySha256',
      expected,
      actual.digest
    );
    if (!actual.size || actual.digest !== expected) {
      throw createUpdateError({ code: EUpdateErrorCode.integrityFailed, step });
    }
  }

  private resolvePersistedTrustAnchorSignature(
    descriptor: IDiffArchiveDescriptor,
    explicitSignature?: string
  ): string | undefined {
    const { appVersion, bundleVersion } = descriptor;
    if (typeof explicitSignature === 'string' && explicitSignature.trim()) {
      return explicitSignature;
    }
    if (descriptor.signature) {
      return descriptor.signature;
    }
    const activeBundle = this.options.store.getUpdateBundleData();
    if (
      activeBundle &&
      activeBundle.appVersion === appVersion &&
      activeBundle.bundleVersion === bundleVersion &&
      typeof activeBundle.signature === 'string' &&
      activeBundle.signature.trim()
    ) {
      return activeBundle.signature;
    }
    const fallbackBundles = this.options.store.getFallbackUpdateBundleData();
    if (Array.isArray(fallbackBundles)) {
      const matched = fallbackBundles.find(
        (entry) =>
          entry?.appVersion === appVersion &&
          entry.bundleVersion === bundleVersion &&
          typeof entry.signature === 'string' &&
          entry.signature.trim().length > 0
      );
      if (matched?.signature) {
        return matched.signature;
      }
    }
    return undefined;
  }

  private async verifyBundleInternal(
    params: IUpdateOperationParams,
    signal: AbortSignal
  ) {
    const step = 'verifyPackage';
    const {
      sha256,
      latestVersion: appVersion,
      bundleVersion,
      signature,
      diffSha256,
    } = params ?? {};
    assertUpdateBundleVersion(appVersion, step);
    assertUpdateBundleVersion(bundleVersion, step);
    const normalizedSha256 = normalizeSha256Hex(sha256);
    if (!normalizedSha256) {
      throw createUpdateError({ code: EUpdateErrorCode.invalidArgument, step });
    }

    const identity = { latestVersion: appVersion, bundleVersion };
    const downloadedFile = this.getDestZipPath(identity);
    if (fs.lstatSync(downloadedFile, { throwIfNoEntry: false })) {
      this.assertManagedArchive(downloadedFile, step);
      await this.verifyArchiveSha256(
        downloadedFile,
        normalizedSha256,
        step,
        signal
      );
      return;
    }

    const diffZipPath = this.getDestDiffZipPath(identity);
    this.assertManagedArchive(diffZipPath, step);
    const descriptor = this.readMatchingDiffDescriptor(
      diffZipPath,
      appVersion,
      bundleVersion,
      normalizedSha256
    );
    if (!descriptor || !this.matchesDiffDigest(descriptor, diffSha256)) {
      throw createUpdateError({ code: EUpdateErrorCode.integrityFailed, step });
    }
    const extractDir = this.getBundleExtractDir({ appVersion, bundleVersion });
    const target = fs.lstatSync(extractDir, { throwIfNoEntry: false });
    if (target) {
      if (!target.isDirectory()) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step,
        });
      }
      this.assertExtractedMetadataPath(
        path.join(extractDir, 'metadata.json'),
        step
      );
      if (!this.isSkipGPGAllowed()) {
        const effectiveSignature = this.resolvePersistedTrustAnchorSignature(
          descriptor,
          signature
        );
        if (!effectiveSignature) {
          throw createUpdateError({
            code: EUpdateErrorCode.integrityFailed,
            step,
          });
        }
        await this.verifySignedMetadata(
          { appVersion, bundleVersion, signature: effectiveSignature },
          step
        );
      }
      throwIfCancelled(signal, step);
      this.verifyExtractedFiles(extractDir, step);
      return;
    }

    if (descriptor.fallbackRequired) {
      throw createUpdateError({ code: EUpdateErrorCode.integrityFailed, step });
    }
    await this.verifyArchiveSha256(
      diffZipPath,
      descriptor.diffSha256,
      step,
      signal
    );
  }

  /**
   * Verify the bundle using ASC (Apple Software Certificate) signature
   * This method validates the digital signature of the downloaded bundle
   * to ensure it comes from a trusted source and hasn't been tampered with
   *
   * @param params - Bundle release identity and signature info
   * @returns Promise that resolves when verification is complete
   */
  async downloadBundleASC(params: IUpdateOperationParams) {
    const {
      sha256,
      latestVersion: appVersion,
      bundleVersion,
      signature,
    } = params || {};
    assertUpdateBundleVersion(appVersion, 'downloadASC');
    assertUpdateBundleVersion(bundleVersion, 'downloadASC');
    const allowSkipGPG = this.isSkipGPGAllowed();
    if (
      !sha256 ||
      !appVersion ||
      !bundleVersion ||
      (!signature && !allowSkipGPG)
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'downloadASC',
      });
    }
  }

  async verifyBundleASC(params: IUpdateOperationParams) {
    try {
      await this.verifyBundleASCInternal(params);
    } catch (error) {
      throw wrapBundleError(error, 'verifyASC');
    }
  }

  private validateAndGetZipEntries(
    zip: AdmZip,
    targetDir: string,
    step: TUpdateErrorStep
  ): AdmZip.IZipEntry[] {
    if (zip.getEntryCount() > MAX_BUNDLE_ENTRY_COUNT) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step,
      });
    }
    const entries = zip.getEntries();
    if (entries.length > MAX_BUNDLE_ENTRY_COUNT) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step,
      });
    }
    const resolvedTargetDir = path.resolve(targetDir);
    let totalUncompressedBytes = 0;
    // Validate all zip entries for path traversal and uncompressed size quotas before extraction
    for (const entry of entries) {
      const entryPath = path.resolve(resolvedTargetDir, entry.entryName);
      if (
        !entryPath.startsWith(resolvedTargetDir + path.sep) &&
        entryPath !== resolvedTargetDir
      ) {
        this.options.logger?.error(
          'bundle-verifyASC',
          `Path traversal detected in zip entry: ${entry.entryName}`
        );
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step,
        });
      }
      const entrySize = entry.header.size;
      const compressedSize = entry.header.compressedSize;
      if (
        !Number.isFinite(entrySize) ||
        entrySize < 0 ||
        entrySize > MAX_BUNDLE_UNCOMPRESSED_BYTES
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step,
        });
      }
      if (
        !Number.isFinite(compressedSize) ||
        compressedSize < 0 ||
        compressedSize > MAX_BUNDLE_UNCOMPRESSED_BYTES
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step,
        });
      }
      // adm-zip does not cap inflation when the advertised size is zero.
      if (!entry.isDirectory && entrySize === 0) {
        const compressed = entry.getCompressedData();
        const canonicalEmptyDeflate =
          entry.header.method === 8 &&
          compressed.length === 2 &&
          compressed[0] === 0x03 &&
          compressed[1] === 0x00;
        const storedEmpty =
          entry.header.method === 0 && compressed.length === 0;
        if (!canonicalEmptyDeflate && !storedEmpty) {
          throw createUpdateError({
            code: EUpdateErrorCode.securityRejected,
            step,
          });
        }
      }
      totalUncompressedBytes += entrySize;
      if (totalUncompressedBytes > MAX_BUNDLE_UNCOMPRESSED_BYTES) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step,
        });
      }
    }
    return entries;
  }

  private getDiffCandidateBuildDirs(
    appVersion: string,
    targetBundleVersion: string
  ): string[] {
    const candidateBuildDirs: string[] = [];
    const seenDirs = new Set<string>();
    const addCandidateBuildDir = (dir: string) => {
      const resolved = path.resolve(dir);
      if (!seenDirs.has(resolved)) {
        seenDirs.add(resolved);
        candidateBuildDirs.push(dir);
      }
    };

    const activeBundle = this.options.store.getUpdateBundleData();
    if (
      activeBundle?.appVersion === appVersion &&
      activeBundle.bundleVersion &&
      activeBundle.bundleVersion !== targetBundleVersion
    ) {
      try {
        addCandidateBuildDir(
          this.getBundleBuildPath({
            appVersion,
            bundleVersion: activeBundle.bundleVersion,
          })
        );
      } catch {
        // ignore invalid stored bundle version
      }
    }

    const bundleDir = this.getBundleDirName();
    if (bundleDir && fs.existsSync(bundleDir)) {
      const entries = fs.readdirSync(bundleDir, { withFileTypes: true });
      for (const entry of entries) {
        if (entry.isDirectory() && !entry.name.startsWith('.')) {
          const lastDash = entry.name.lastIndexOf('-');
          if (lastDash > 0) {
            const entryAppVersion = entry.name.substring(0, lastDash);
            const entryBundleVersion = entry.name.substring(lastDash + 1);
            if (
              entryAppVersion === appVersion &&
              entryBundleVersion &&
              entryBundleVersion !== targetBundleVersion
            ) {
              try {
                addCandidateBuildDir(
                  this.getBundleBuildPath({
                    appVersion: entryAppVersion,
                    bundleVersion: entryBundleVersion,
                  })
                );
              } catch {
                // ignore unsafe directory name
              }
            }
          }
        }
      }
    }

    const builtInBuildDir = this.options.builtInBuildDir?.();
    if (builtInBuildDir) addCandidateBuildDir(builtInBuildDir);

    return candidateBuildDirs;
  }

  private getExtraResourceCandidateRelPaths(relPath: string): string[] {
    const normalized = relPath.replace(/\\/g, '/');
    if (normalized === 'static/udev/99-onekey.rules') {
      return ['static/udev/99-onekey.rules'];
    }
    const bridgeMatch = normalized.match(
      /^static\/bin\/bridge\/[^/]+\/([^/]+)$/
    );
    if (bridgeMatch && bridgeMatch[1]) {
      return [`bin/bridge/${bridgeMatch[1]}`];
    }
    return [];
  }

  private findMatchingLocalFile(
    candidateBuildDirs: string[],
    relPath: string,
    expectedSha512: string,
    remainingBytes: number
  ): { sourcePath: string; size: number } | null {
    if (!isSafeRelativeManifestPath(relPath)) {
      return null;
    }
    const checkCandidate = (rootDir: string, candidateRelPath: string) => {
      const resolvedRoot = path.resolve(rootDir);
      const candidatePath = path.resolve(resolvedRoot, candidateRelPath);
      if (!candidatePath.startsWith(`${resolvedRoot}${path.sep}`)) {
        return null;
      }
      if (hasSymlinkOnPathWithinRoot(resolvedRoot, candidatePath)) {
        return null;
      }
      const stat = fs.statSync(candidatePath, { throwIfNoEntry: false });
      if (!stat?.isFile()) return null;
      // Verify SHA-512 match BEFORE enforcing the remaining byte budget so
      // an oversized unmatched candidate in an earlier directory is skipped
      // instead of blocking a smaller matching file in a later candidate.
      if (!checkFileSha512(candidatePath, expectedSha512)) {
        return null;
      }
      if (stat.size > remainingBytes) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      return { sourcePath: candidatePath, size: stat.size };
    };

    for (const buildDir of candidateBuildDirs) {
      const matched = checkCandidate(buildDir, relPath);
      if (matched) {
        return matched;
      }
    }
    const resourcesPath = this.options.resourcesDir?.() ?? '';
    if (resourcesPath) {
      for (const extraRel of this.getExtraResourceCandidateRelPaths(relPath)) {
        const matched = checkCandidate(resourcesPath, extraRel);
        if (matched) {
          return matched;
        }
      }
    }
    return null;
  }

  private async reconstructDiffBundle({
    appVersion,
    bundleVersion,
    diffZipPath,
    stagingDir,
    extractDir,
    allowSkipGPG,
    signature,
    diffSha256,
    diffSignature,
    isInstalled,
    signal,
  }: {
    appVersion: string;
    bundleVersion: string;
    diffZipPath: string;
    stagingDir: string;
    extractDir: string;
    allowSkipGPG: boolean;
    signature?: string;
    diffSha256?: string;
    diffSignature?: string;
    isInstalled?: boolean;
    signal?: AbortSignal;
  }) {
    const throwIfAborted = () => {
      if (signal?.aborted) {
        throw createUpdateError({
          code: EUpdateErrorCode.cancelled,
          step: 'verifyASC',
        });
      }
    };
    throwIfAborted();
    let signedDiffManifestSha256 = '';
    let signedMetadataSha256 = '';
    if (!allowSkipGPG) {
      if (!diffSignature) {
        throw createUpdateError({
          code: EUpdateErrorCode.invalidArgument,
          step: 'verifyASC',
        });
      }
      try {
        signedDiffManifestSha256 = await this.readMetadataFileSha256(
          diffSignature,
          {
            appVersion,
            bundleVersion,
            fileName: 'diff-manifest.json',
          }
        );
        if (!signedDiffManifestSha256) {
          throw new SignedMetadataError('Invalid signed diff-manifest sha256');
        }
      } catch (error) {
        const wrapped = wrapSignedMetadataError(error, 'verifyASC');
        diffGpgErrors.add(wrapped);
        throw wrapped;
      }
      try {
        signedMetadataSha256 = await this.readMetadataFileSha256(signature!, {
          appVersion,
          bundleVersion,
          fileName: 'metadata.json',
        });
        if (!signedMetadataSha256) {
          throw new SignedMetadataError('Invalid signed metadata sha256');
        }
      } catch (error) {
        const wrapped = wrapSignedMetadataError(error, 'verifyASC');
        diffGpgErrors.add(wrapped);
        throw wrapped;
      }
    }
    throwIfAborted();

    if (!diffSha256) {
      throw createUpdateError({
        code: EUpdateErrorCode.integrityFailed,
        step: 'verifyASC',
      });
    }
    await this.verifyArchiveSha256(
      diffZipPath,
      diffSha256,
      'verifyASC',
      signal
    );

    const zip = new AdmZip(diffZipPath);
    const entries = this.validateAndGetZipEntries(zip, stagingDir, 'verifyASC');
    const diffManifestEntry = entries.find(
      (e) =>
        !e.isDirectory &&
        e.entryName.replace(/\\/g, '/').replace(/^\.\//, '') ===
          'diff-manifest.json'
    );
    const metadataEntry = entries.find(
      (e) =>
        !e.isDirectory &&
        e.entryName.replace(/\\/g, '/').replace(/^\.\//, '') === 'metadata.json'
    );
    if (!diffManifestEntry || !metadataEntry) {
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'verifyASC',
      });
    }
    if (
      diffManifestEntry.header.size > MAX_CONTROL_JSON_BYTES ||
      metadataEntry.header.size > MAX_CONTROL_JSON_BYTES
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step: 'verifyASC',
      });
    }

    fs.rmSync(stagingDir, { recursive: true, force: true });
    fs.mkdirSync(stagingDir, { recursive: true });
    zip.extractAllTo(stagingDir, true);
    throwIfAborted();

    const diffManifestPath = path.join(stagingDir, 'diff-manifest.json');
    const metadataPath = path.join(stagingDir, 'metadata.json');
    if (
      !fs.existsSync(diffManifestPath) ||
      fs.lstatSync(diffManifestPath).isSymbolicLink() ||
      !fs.existsSync(metadataPath) ||
      fs.lstatSync(metadataPath).isSymbolicLink()
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'verifyASC',
      });
    }
    const diffManifestStat = fs.statSync(diffManifestPath);
    const metadataStat = fs.statSync(metadataPath);
    if (
      diffManifestStat.size > MAX_CONTROL_JSON_BYTES ||
      metadataStat.size > MAX_CONTROL_JSON_BYTES
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step: 'verifyASC',
      });
    }
    if (diffManifestStat.size <= 0 || metadataStat.size <= 0) {
      throw createUpdateError({
        code: EUpdateErrorCode.integrityFailed,
        step: 'verifyASC',
      });
    }
    if (!allowSkipGPG) {
      if (
        !verifySha256(diffManifestPath, signedDiffManifestSha256) ||
        !verifySha256(metadataPath, signedMetadataSha256)
      ) {
        const error = createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyASC',
        });
        diffGpgErrors.add(error);
        throw error;
      }
    }

    const diffManifest = JSON.parse(
      fs.readFileSync(diffManifestPath, 'utf8')
    ) as Partial<IDiffManifest>;
    const actualMetadataSha256 = calculateSHA256(metadataPath);
    if (
      diffManifest?.schemaVersion !== 1 ||
      diffManifest.appType !== 'electron' ||
      diffManifest.appVersion !== appVersion ||
      diffManifest.targetBundleVersion !== bundleVersion ||
      diffManifest.hashAlgorithm !== 'sha512' ||
      !isValidSha256Hex(diffManifest.targetMetadataSha256) ||
      diffManifest.targetMetadataSha256.trim().toLowerCase() !==
        actualMetadataSha256.toLowerCase() ||
      !diffManifest.changedFiles ||
      typeof diffManifest.changedFiles !== 'object' ||
      Array.isArray(diffManifest.changedFiles) ||
      !diffManifest.reusedFiles ||
      typeof diffManifest.reusedFiles !== 'object' ||
      Array.isArray(diffManifest.reusedFiles) ||
      (diffManifest.deletedFiles !== undefined &&
        !Array.isArray(diffManifest.deletedFiles))
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.integrityFailed,
        step: 'verifyASC',
      });
    }

    const targetMetadata = JSON.parse(
      fs.readFileSync(metadataPath, 'utf8')
    ) as Record<string, string>;
    if (
      !targetMetadata ||
      typeof targetMetadata !== 'object' ||
      Array.isArray(targetMetadata)
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.integrityFailed,
        step: 'verifyASC',
      });
    }

    if (isInstalled && fs.existsSync(extractDir)) {
      if (!allowSkipGPG) {
        const metadataFilePath = path.join(extractDir, 'metadata.json');
        this.assertExtractedMetadataPath(metadataFilePath, 'verifyASC');
        if (!verifySha256(metadataFilePath, signedMetadataSha256)) {
          const error = createUpdateError({
            code: EUpdateErrorCode.integrityFailed,
            step: 'verifyASC',
          });
          diffGpgErrors.add(error);
          throw error;
        }
      }
      this.verifyExtractedFiles(extractDir, 'verifyASC');
      return false;
    }

    const changedEntries = Object.entries(diffManifest.changedFiles);
    const reusedEntries = Object.entries(diffManifest.reusedFiles);
    const targetEntries = Object.entries(targetMetadata);
    if (changedEntries.length + reusedEntries.length > MAX_BUNDLE_ENTRY_COUNT) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step: 'verifyASC',
      });
    }
    if (changedEntries.length + reusedEntries.length !== targetEntries.length) {
      throw createUpdateError({
        code: EUpdateErrorCode.integrityFailed,
        step: 'verifyASC',
      });
    }
    if (Array.isArray(diffManifest.deletedFiles)) {
      for (const deletedPath of diffManifest.deletedFiles) {
        if (!isSafeRelativeManifestPath(deletedPath)) {
          throw createUpdateError({
            code: EUpdateErrorCode.securityRejected,
            step: 'verifyASC',
          });
        }
        if (
          Object.prototype.hasOwnProperty.call(
            diffManifest.changedFiles,
            deletedPath
          ) ||
          Object.prototype.hasOwnProperty.call(
            diffManifest.reusedFiles,
            deletedPath
          ) ||
          Object.prototype.hasOwnProperty.call(targetMetadata, deletedPath)
        ) {
          throw createUpdateError({
            code: EUpdateErrorCode.integrityFailed,
            step: 'verifyASC',
          });
        }
      }
    }
    for (const [relPath, expectedSha512] of changedEntries) {
      if (!isSafeRelativeManifestPath(relPath)) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      if (
        !isValidSha512Hex(expectedSha512) ||
        Object.prototype.hasOwnProperty.call(
          diffManifest.reusedFiles,
          relPath
        ) ||
        !Object.prototype.hasOwnProperty.call(targetMetadata, relPath) ||
        typeof targetMetadata[relPath] !== 'string' ||
        targetMetadata[relPath].trim().toLowerCase() !==
          expectedSha512.trim().toLowerCase()
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyASC',
        });
      }
    }
    for (const [relPath, expectedSha512] of reusedEntries) {
      if (!isSafeRelativeManifestPath(relPath)) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      if (
        !isValidSha512Hex(expectedSha512) ||
        !Object.prototype.hasOwnProperty.call(targetMetadata, relPath) ||
        typeof targetMetadata[relPath] !== 'string' ||
        targetMetadata[relPath].trim().toLowerCase() !==
          expectedSha512.trim().toLowerCase()
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyASC',
        });
      }
    }

    const stagingBuildDir = path.resolve(stagingDir, 'build');
    let totalReconstructedBytes = 0;
    for (const [relPath, expectedSha512] of changedEntries) {
      throwIfAborted();
      const filePath = path.resolve(stagingBuildDir, relPath);
      if (
        !isSafeRelativeManifestPath(relPath) ||
        !filePath.startsWith(`${stagingBuildDir}${path.sep}`) ||
        hasSymlinkOnPathWithinRoot(stagingDir, filePath)
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      if (
        !fs.existsSync(filePath) ||
        !checkFileSha512(filePath, expectedSha512)
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyASC',
        });
      }
      const stat = fs.statSync(filePath);
      totalReconstructedBytes += stat.size;
      if (totalReconstructedBytes > MAX_BUNDLE_UNCOMPRESSED_BYTES) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
    }

    const candidateBuildDirs = this.getDiffCandidateBuildDirs(
      appVersion,
      bundleVersion
    );
    for (const [relPath, expectedSha512] of reusedEntries) {
      throwIfAborted();
      const destFilePath = path.resolve(stagingBuildDir, relPath);
      if (
        !isSafeRelativeManifestPath(relPath) ||
        !destFilePath.startsWith(`${stagingBuildDir}${path.sep}`) ||
        hasSymlinkOnPathWithinRoot(stagingDir, destFilePath)
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      const remainingBytes =
        MAX_BUNDLE_UNCOMPRESSED_BYTES - totalReconstructedBytes;
      const matched = this.findMatchingLocalFile(
        candidateBuildDirs,
        relPath,
        expectedSha512,
        remainingBytes
      );
      if (!matched) {
        throw createUpdateError({
          code: EUpdateErrorCode.packageMissing,
          step: 'verifyASC',
        });
      }
      totalReconstructedBytes += matched.size;
      if (totalReconstructedBytes > MAX_BUNDLE_UNCOMPRESSED_BYTES) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      fs.mkdirSync(path.dirname(destFilePath), { recursive: true });
      if (hasSymlinkOnPathWithinRoot(stagingDir, destFilePath)) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      fs.copyFileSync(matched.sourcePath, destFilePath);
    }

    fs.rmSync(diffManifestPath, { force: true });
    this.verifyAllExtractedFiles(stagingDir, targetMetadata, 'verifyASC');
    throwIfAborted();
    return true;
  }

  private async verifyBundleASCInternal(params: IUpdateOperationParams) {
    const step = 'verifyASC';
    while (this.cacheClearPromise) await this.cacheClearPromise;
    const {
      latestVersion: appVersion,
      bundleVersion,
      signature,
      diffSignature,
    } = params ?? {};
    assertUpdateBundleVersion(appVersion, step);
    assertUpdateBundleVersion(bundleVersion, step);
    const sha256 = normalizeSha256Hex(params?.sha256);
    const allowSkipGPG = this.isSkipGPGAllowed();
    const diffSha256 = params?.diffSha256;
    if (
      !sha256 ||
      (!signature && !allowSkipGPG) ||
      (diffSha256 !== undefined && !normalizeSha256Hex(diffSha256))
    ) {
      throw createUpdateError({ code: EUpdateErrorCode.invalidArgument, step });
    }
    const identity = { latestVersion: appVersion, bundleVersion };
    const downloadedFile = this.getDestZipPath(identity);
    const releaseKey = this.getReleaseKey(appVersion, bundleVersion, sha256);
    const verificationKey = JSON.stringify([
      releaseKey,
      normalizeSha256Hex(diffSha256),
      signature,
      diffSignature,
      params?.downloadUrl,
      params?.fileSize,
      allowSkipGPG,
    ]);
    const existingDownload = this.inflightDownloads.get(downloadedFile);
    const existingVerification = this.activeVerifications.get(downloadedFile);
    if (
      (existingDownload && existingDownload.releaseKey !== releaseKey) ||
      (existingVerification &&
        existingVerification.verificationKey !== verificationKey)
    ) {
      throw createUpdateError({ code: EUpdateErrorCode.incompatible, step });
    }
    if (existingVerification) return existingVerification.run;

    if (this.activeVerifications.size === 0) this.cleanupDiffStagingDirs();
    const controller = new AbortController();
    const run = Promise.resolve()
      .then(() =>
        this.runVerifyBundleASC({
          appVersion,
          bundleVersion,
          sha256,
          downloadedFile,
          params,
          releaseKey,
          allowSkipGPG,
          signal: controller.signal,
        })
      )
      .finally(() => this.activeVerifications.delete(downloadedFile));
    this.activeVerifications.set(downloadedFile, {
      releaseKey,
      verificationKey,
      controller,
      run,
    });
    return run;
  }

  private async runVerifyBundleASC({
    appVersion,
    bundleVersion,
    sha256,
    downloadedFile,
    params,
    releaseKey,
    allowSkipGPG,
    signal,
  }: IBundleUpdateIdentity & {
    sha256: string;
    downloadedFile: string;
    params: IUpdateOperationParams;
    releaseKey: string;
    allowSkipGPG: boolean;
    signal: AbortSignal;
  }) {
    const step = 'verifyASC';
    throwIfCancelled(signal, step);
    const identity = { latestVersion: appVersion, bundleVersion };
    const { signature, diffSignature, diffSha256 } = params ?? {};
    const diffZipPath = this.getDestDiffZipPath(identity);
    const activeBundle = this.options.store.getUpdateBundleData();
    const fallbackBundles = this.options.store.getFallbackUpdateBundleData();
    const isInstalled =
      (activeBundle?.appVersion === appVersion &&
        activeBundle.bundleVersion === bundleVersion) ||
      (Array.isArray(fallbackBundles) &&
        fallbackBundles.some(
          (entry) =>
            entry?.appVersion === appVersion &&
            entry.bundleVersion === bundleVersion
        ));
    const resolveExtractionDirectory = () => {
      const directory = this.getBundleExtractDir({ appVersion, bundleVersion });
      if (
        fs.lstatSync(directory, { throwIfNoEntry: false })?.isSymbolicLink()
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step,
        });
      }
      return directory;
    };

    if (!fs.lstatSync(downloadedFile, { throwIfNoEntry: false })) {
      const descriptor = this.readMatchingDiffDescriptor(
        diffZipPath,
        appVersion,
        bundleVersion,
        sha256
      );
      const hasDiffZip = fs.lstatSync(diffZipPath, { throwIfNoEntry: false });
      if (hasDiffZip) this.assertManagedArchive(diffZipPath, step);
      if (
        (hasDiffZip && !descriptor) ||
        (descriptor && !this.matchesDiffDigest(descriptor, diffSha256))
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step,
        });
      }

      const fallbackRequired =
        descriptor?.fallbackRequired ||
        this.diffFallbackTargets.has(releaseKey);
      if (hasDiffZip && descriptor && !fallbackRequired) {
        if (!allowSkipGPG && !diffSignature) {
          throw createUpdateError({
            code: EUpdateErrorCode.invalidArgument,
            step,
          });
        }
        const extractDir = resolveExtractionDirectory();
        const stagingDir = this.createDiffStagingDir(appVersion, bundleVersion);
        try {
          const shouldPromote = await this.reconstructDiffBundle({
            appVersion,
            bundleVersion,
            diffZipPath,
            stagingDir,
            extractDir,
            allowSkipGPG,
            signature,
            diffSha256: descriptor.diffSha256,
            diffSignature,
            isInstalled,
            signal,
          });
          throwIfCancelled(signal, step);
          if (!allowSkipGPG) {
            // A normal download may have refreshed the descriptor during GPG IO.
            const current = this.readMatchingDiffDescriptor(
              diffZipPath,
              appVersion,
              bundleVersion,
              sha256
            );
            if (
              current?.diffSha256 !== descriptor.diffSha256 ||
              current.fallbackRequired
            ) {
              throw createUpdateError({
                code: EUpdateErrorCode.incompatible,
                step,
              });
            }
            this.writeDiffDescriptor(
              diffZipPath,
              { ...current, signature },
              step
            );
          }
          if (shouldPromote) this.promoteBundle(stagingDir, extractDir);
          return;
        } catch (error) {
          throwIfCancelled(signal, step);
          const code = getUpdateError(error, step).code;
          if (
            code === EUpdateErrorCode.storageError ||
            code === EUpdateErrorCode.incompatible
          ) {
            throw error;
          }
          if (
            (error && typeof error === 'object' && diffGpgErrors.has(error)) ||
            code === EUpdateErrorCode.securityRejected ||
            code === EUpdateErrorCode.cancelled
          ) {
            const writer = this.inflightDownloads.get(downloadedFile);
            if (
              code !== EUpdateErrorCode.cancelled &&
              (!writer || writer.fullWriter)
            ) {
              this.cleanupDiffArtifacts(diffZipPath);
            }
            throw error;
          }
          this.options.logger?.warn(
            'bundle-verifyASC',
            'Diff bundle reconstruction failed, falling back to full bundle',
            { appVersion, bundleVersion }
          );
        } finally {
          fs.rmSync(stagingDir, { recursive: true, force: true });
        }
      }

      const downloadUrl = isValidHttpsUrl(params?.downloadUrl)
        ? params.downloadUrl
        : descriptor?.downloadUrl;
      if ((fallbackRequired || (hasDiffZip && descriptor)) && downloadUrl) {
        const fileSize =
          normalizePositiveInt(params?.fileSize) ?? descriptor?.fileSize;
        await this.coordinateDownload(
          { ...identity, downloadUrl, fileSize, sha256, signature },
          {
            signal,
            prepare: () => {
              if (descriptor) {
                this.writeDiffDescriptor(
                  diffZipPath,
                  {
                    ...descriptor,
                    downloadUrl,
                    fileSize,
                    fallbackRequired: true,
                  },
                  step
                );
                this.cleanupDiffArtifacts(diffZipPath, true);
              }
              this.diffFallbackTargets.add(releaseKey);
            },
          }
        );
      }
    }

    throwIfCancelled(signal, step);
    this.assertManagedArchive(downloadedFile, step);
    await this.verifyArchiveSha256(downloadedFile, sha256, step, signal);
    let signedMetadataSha256: string | undefined;
    if (!allowSkipGPG) {
      try {
        signedMetadataSha256 = await this.readMetadataFileSha256(signature!, {
          appVersion,
          bundleVersion,
        });
      } catch (error) {
        throw wrapSignedMetadataError(error, step);
      }
    }
    throwIfCancelled(signal, step);

    const extractDir = resolveExtractionDirectory();
    const verifyMetadata = (directory: string) => {
      if (signedMetadataSha256) {
        const metadataFilePath = path.join(directory, 'metadata.json');
        this.assertExtractedMetadataPath(metadataFilePath, step);
        if (!verifySha256(metadataFilePath, signedMetadataSha256)) {
          throw createUpdateError({
            code: EUpdateErrorCode.integrityFailed,
            step,
          });
        }
      }
    };
    if (isInstalled) {
      verifyMetadata(extractDir);
      this.verifyExtractedFiles(extractDir, step);
      return;
    }

    const stagingDir = this.createDiffStagingDir(appVersion, bundleVersion);
    try {
      const zip = new AdmZip(downloadedFile);
      const entries = this.validateAndGetZipEntries(zip, stagingDir, step);
      const metadataEntry = entries.find(
        (entry) =>
          !entry.isDirectory &&
          entry.entryName.replace(/\\/g, '/').replace(/^\.\//, '') ===
            'metadata.json'
      );
      if (!metadataEntry || metadataEntry.header.size <= 0) {
        throw createUpdateError({
          code: EUpdateErrorCode.packageMissing,
          step,
        });
      }
      if (metadataEntry.header.size > MAX_CONTROL_JSON_BYTES) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step,
        });
      }
      if (!allowSkipGPG) {
        zip.extractEntryTo(metadataEntry, stagingDir, false, true);
        verifyMetadata(stagingDir);
      }
      zip.extractAllTo(stagingDir, true);
      verifyMetadata(stagingDir);
      this.verifyExtractedFiles(stagingDir, step);
      throwIfCancelled(signal, step);
      this.promoteBundle(stagingDir, extractDir);
    } catch (error) {
      this.options.logger?.error('Failed to verify extracted bundle:', error);
      throw error;
    } finally {
      fs.rmSync(stagingDir, { recursive: true, force: true });
    }
  }

  private promoteBundle(stagingDir: string, extractDir: string) {
    const backup = fs.existsSync(extractDir)
      ? `${stagingDir}.backup`
      : undefined;
    if (backup) fs.renameSync(extractDir, backup);
    try {
      fs.renameSync(stagingDir, extractDir);
    } catch (error) {
      if (backup) fs.renameSync(backup, extractDir);
      throw error;
    }
    if (backup) {
      try {
        fs.rmSync(backup, { recursive: true, force: true });
      } catch (error) {
        this.options.logger?.warn('Failed to remove previous bundle:', error);
      }
    }
  }

  private assertExtractedMetadataPath(
    metadataPath: string,
    step: TUpdateErrorStep
  ) {
    const extractDir = path.dirname(metadataPath);
    const stat = fs.lstatSync(metadataPath, { throwIfNoEntry: false });
    if (!stat) {
      throw createUpdateError({ code: EUpdateErrorCode.packageMissing, step });
    }
    if (
      fs.lstatSync(extractDir).isSymbolicLink() ||
      !stat.isFile() ||
      stat.size > MAX_CONTROL_JSON_BYTES
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step,
      });
    }
    if (stat.size <= 0) {
      throw createUpdateError({ code: EUpdateErrorCode.integrityFailed, step });
    }
  }

  private verifyExtractedFiles(extractDir: string, step: TUpdateErrorStep) {
    const metadataPath = path.join(extractDir, 'metadata.json');
    this.assertExtractedMetadataPath(metadataPath, step);
    const metadata: unknown = JSON.parse(fs.readFileSync(metadataPath, 'utf8'));
    this.verifyAllExtractedFiles(extractDir, metadata, step);
  }

  private verifyAllExtractedFiles(
    extractDir: string,
    metadata: unknown,
    step: TUpdateErrorStep
  ) {
    if (!metadata || typeof metadata !== 'object' || Array.isArray(metadata)) {
      throw createUpdateError({ code: EUpdateErrorCode.integrityFailed, step });
    }
    const verifiedFiles = new Set<string>();
    const buildDir = path.join(extractDir, 'build');
    const metadataRecord = metadata as Record<string, unknown>;
    const walk = (dirPath: string) => {
      const entries = fs.readdirSync(dirPath, { withFileTypes: true });
      for (const entry of entries) {
        const fullPath = path.join(dirPath, entry.name);
        if (entry.isSymbolicLink()) {
          throw createUpdateError({
            code: EUpdateErrorCode.securityRejected,
            step,
          });
        }
        if (entry.isDirectory()) {
          walk(fullPath);
          continue;
        }
        if (!entry.isFile()) {
          throw createUpdateError({
            code: EUpdateErrorCode.securityRejected,
            step,
          });
        }
        if (dirPath === extractDir && entry.name === 'metadata.json') continue;

        const relativePath = path
          .relative(buildDir, fullPath)
          .split(path.sep)
          .join('/');
        const isListed = Object.prototype.hasOwnProperty.call(
          metadataRecord,
          relativePath
        );
        if (entry.name === '.DS_Store' && !isListed) continue;
        const expectedSha512 = metadataRecord[relativePath];
        if (
          !isSafeRelativeManifestPath(relativePath) ||
          !isListed ||
          !isValidSha512Hex(expectedSha512) ||
          !checkFileSha512(fullPath, expectedSha512)
        ) {
          this.options.logger?.error(
            'bundle-verify',
            `File verification failed: ${relativePath}`
          );
          throw createUpdateError({
            code: EUpdateErrorCode.integrityFailed,
            step,
          });
        }
        verifiedFiles.add(relativePath);
      }
    };
    walk(extractDir);

    for (const key of Object.keys(metadataRecord)) {
      if (!verifiedFiles.has(key)) {
        this.options.logger?.error(
          'bundle-verify',
          `File listed in metadata but missing on disk: ${key}`
        );
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step,
        });
      }
    }
  }

  async isBundleExists(
    appVersion: string,
    bundleVersion: string
  ): Promise<boolean> {
    const extractDir = this.getBundleExtractDir({ appVersion, bundleVersion });
    return fs.existsSync(extractDir);
  }

  async listLocalBundles(): Promise<
    { appVersion: string; bundleVersion: string }[]
  > {
    const bundleDir = this.getBundleDirName();
    if (!fs.existsSync(bundleDir)) {
      return [];
    }
    const appVersion = this.options.appVersion();
    const entries = fs.readdirSync(bundleDir, { withFileTypes: true });
    const results: { appVersion: string; bundleVersion: string }[] = [];
    for (const entry of entries) {
      if (entry.isDirectory()) {
        const bundleVersion = this.getLocalBundleVersion(
          entry.name,
          appVersion
        );
        if (bundleVersion) results.push({ appVersion, bundleVersion });
      }
    }
    return results;
  }

  private getLocalBundleVersion(name: string, appVersion: string): string {
    const prefix = `${appVersion}-`;
    return !name.startsWith('.') && name.startsWith(prefix)
      ? name.slice(prefix.length)
      : '';
  }

  private isVersionedBundleName(name: string): boolean {
    const separator = name.lastIndexOf('-');
    return (
      !name.startsWith('.') && separator > 0 && separator < name.length - 1
    );
  }

  private getBundleNameFromDownloadArtifact(name: string): string {
    let base = name;
    for (const suffix of [
      '.meta.json.tmp',
      '.meta.json',
      '.progress.json',
      '.partial',
      '.diff.zip',
      '.zip',
    ] as const) {
      if (base.endsWith(suffix)) {
        base = base.slice(0, -suffix.length);
      }
    }
    return base;
  }

  /**
   * Prune downloaded OTA artifacts whose appVersion != the running native
   * binary version (this.options.appVersion()). KEEP everything matching the current
   * appVersion (current + same-version fallbacks + pending install — OTA never
   * crosses native version). Cleans:
   *   - onekey-bundle/{appV}-{bV}/                         (extract dirs)
   *   - onekey-bundle/.diff-staging/                       (stale diff staging)
   *   - onekey-bundle-download/{appV}-{bV}.zip(.partial)(.progress.json)
   *   - onekey-bundle-download/.diff/{appV}-{bV}.diff.zip(.partial)(.progress.json)(.meta.json)
   *   - store fallback entries with appV != currentAppV   (disk<->store sync)
   *
   * Safety net: never deletes the current appVersion's artifacts, and never
   * the active currentBundleVersion from getUpdateBundleData(). Tolerates
   * already-missing files (fs.rmSync force:true).
   *
   * @returns count of deleted version directories.
   */
  async pruneStaleAppVersionBundles(): Promise<number> {
    const currentAppV = this.options.appVersion();
    // The active install — its dir must survive even if it somehow shared an
    // appVersion mismatch (defense in depth; it will normally match currentAppV).
    const activeBundleData = this.options.store.getUpdateBundleData();
    const activeAppV = activeBundleData?.appVersion;
    const activeBundleV = activeBundleData?.bundleVersion;

    let deletedDirCount = 0;

    // 1) Extract dirs: onekey-bundle/{appV}-{bV}/
    const bundleDir = this.getBundleDirName();
    if (bundleDir && fs.existsSync(bundleDir)) {
      const entries = fs.readdirSync(bundleDir, { withFileTypes: true });
      for (const entry of entries) {
        if (entry.isDirectory() && entry.name === DIFF_STAGING_DIR_NAME) {
          if (this.activeVerifications.size === 0) {
            try {
              fs.rmSync(path.join(bundleDir, entry.name), {
                recursive: true,
                force: true,
              });
            } catch {
              // ignore
            }
          }
        } else {
          // Safety net: keep current appVersion AND the active install dir.
          const isActiveInstall = Boolean(
            activeAppV &&
            activeBundleV &&
            entry.name === `${activeAppV}-${activeBundleV}`
          );
          if (
            entry.isDirectory() &&
            this.isVersionedBundleName(entry.name) &&
            !this.getLocalBundleVersion(entry.name, currentAppV) &&
            !isActiveInstall
          ) {
            const dirPath = path.join(bundleDir, entry.name);
            try {
              fs.rmSync(dirPath, { recursive: true, force: true });
              deletedDirCount += 1;
              this.options.logger?.info(
                'bundle-prune',
                `Deleted stale extract dir: ${entry.name}`
              );
            } catch (error) {
              this.options.logger?.error(
                'bundle-prune',
                `Failed to delete stale extract dir ${entry.name}:`,
                error
              );
            }
          }
        }
      }
    }

    // 2) Download artifacts: onekey-bundle-download/ and onekey-bundle-download/.diff/
    const pruneDownloadDirFiles = (dir: string) => {
      if (!fs.existsSync(dir)) {
        return;
      }
      const downloadEntries = fs.readdirSync(dir, {
        withFileTypes: true,
      });
      for (const entry of downloadEntries) {
        const name = this.getBundleNameFromDownloadArtifact(entry.name);
        // Safety net: never delete current appVersion's download artifacts.
        if (
          entry.isFile() &&
          this.isVersionedBundleName(name) &&
          !this.getLocalBundleVersion(name, currentAppV)
        ) {
          const filePath = path.join(dir, entry.name);
          try {
            fs.rmSync(filePath, { force: true });
            this.options.logger?.info(
              'bundle-prune',
              `Deleted stale download artifact: ${entry.name}`
            );
          } catch (error) {
            this.options.logger?.error(
              'bundle-prune',
              `Failed to delete stale download artifact ${entry.name}:`,
              error
            );
          }
        }
      }
    };
    const downloadDir = this.getDownloadDir();
    pruneDownloadDirFiles(downloadDir);
    pruneDownloadDirFiles(path.join(downloadDir, DIFF_DOWNLOAD_DIR_NAME));

    // 3) Store fallback entries: drop appVersion != currentAppV so the store
    // stays consistent with disk (no ghost dev-switcher entries, no orphan asc
    // bookkeeping). Keeps the current appVersion's fallbacks untouched.
    try {
      const fallbackUpdateBundleData =
        this.options.store.getFallbackUpdateBundleData();
      const keptFallback = fallbackUpdateBundleData.filter(
        (item) => item?.appVersion === currentAppV
      );
      if (keptFallback.length !== fallbackUpdateBundleData.length) {
        this.options.store.setFallbackUpdateBundleData(keptFallback);
        this.options.logger?.info(
          'bundle-prune',
          `Pruned ${
            fallbackUpdateBundleData.length - keptFallback.length
          } stale fallback entries`
        );
      }
    } catch (error) {
      this.options.logger?.error(
        'bundle-prune',
        'Failed to prune fallback store data:',
        error
      );
    }

    this.options.logger?.info(
      'bundle-prune',
      `pruneStaleAppVersionBundles done, currentAppV=${currentAppV}, deletedDirCount=${deletedDirCount}`
    );
    return deletedDirCount;
  }

  async verifyExtractedBundle(
    appVersion: string,
    bundleVersion: string
  ): Promise<void> {
    try {
      await this.verifyExtractedBundleInternal(appVersion, bundleVersion);
    } catch (error) {
      throw wrapBundleError(error, 'verifyPackage');
    }
  }

  private async verifyExtractedBundleInternal(
    appVersion: string,
    bundleVersion: string
  ): Promise<void> {
    const extractDir = this.getBundleExtractDir({ appVersion, bundleVersion });
    if (!fs.existsSync(extractDir)) {
      this.options.logger?.error(
        'bundle-verify',
        `verifyExtractedBundle: directory not found: ${extractDir}`
      );
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'verifyPackage',
      });
    }
    this.verifyExtractedFiles(extractDir, 'verifyPackage');
  }

  async installBundle(params: IUpdateOperationParams) {
    try {
      await this.installBundleInternal(params);
    } catch (error) {
      throw wrapBundleError(error, 'install');
    }
  }

  private async installBundleInternal(params: IUpdateOperationParams) {
    const {
      latestVersion: appVersion,
      bundleVersion,
      signature,
    } = params || {};
    assertUpdateBundleVersion(appVersion, 'install');
    assertUpdateBundleVersion(bundleVersion, 'install');
    const allowSkipGPG = this.isSkipGPGAllowed();
    if (!appVersion || !bundleVersion || (!signature && !allowSkipGPG)) {
      this.options.logger?.error('bundle-install', 'Invalid parameters', {
        appVersion,
        bundleVersion,
        hasSignature: !!signature,
        allowSkipGPG,
      });
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'install',
      });
    }
    if (!semver.eq(appVersion, this.options.appVersion())) {
      this.options.logger?.error(
        'bundle-install',
        'Bundle appVersion mismatch',
        {
          appVersion,
          currentAppVersion: this.options.appVersion(),
        }
      );
      throw createUpdateError({
        code: EUpdateErrorCode.incompatible,
        step: 'install',
      });
    }
    const isValidStoredBundle = (entry: IInstalledBundle | undefined) => {
      try {
        assertSafeBundleVersion(entry?.appVersion);
        assertSafeBundleVersion(entry?.bundleVersion);
        return true;
      } catch {
        this.options.logger?.warn(
          'bundle-install',
          'Discarding invalid stored bundle'
        );
        return false;
      }
    };
    const storedCurrentBundle = this.options.store.getUpdateBundleData();
    const currentUpdateBundleData =
      storedCurrentBundle && isValidStoredBundle(storedCurrentBundle)
        ? storedCurrentBundle
        : undefined;
    const storedFallbacks = this.options.store.getFallbackUpdateBundleData();
    const fallbackUpdateBundleData = Array.isArray(storedFallbacks)
      ? storedFallbacks.filter(
          (entry) =>
            isValidStoredBundle(entry) &&
            !(
              entry.appVersion === appVersion &&
              entry.bundleVersion === bundleVersion
            ) &&
            !(
              currentUpdateBundleData &&
              entry.appVersion === currentUpdateBundleData.appVersion &&
              entry.bundleVersion === currentUpdateBundleData.bundleVersion
            )
        )
      : [];

    // Security: Verify bundle directory and integrity before updating store
    const extractDir = this.getBundleExtractDir({ appVersion, bundleVersion });
    if (!fs.existsSync(extractDir)) {
      this.options.logger?.error(
        'bundle-install',
        `Bundle directory not found: ${appVersion}-${bundleVersion}`
      );
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'install',
      });
    }
    const indexHtmlPath = path.join(extractDir, 'build', 'index.html');
    if (!fs.existsSync(indexHtmlPath)) {
      this.options.logger?.error(
        'bundle-install',
        `Bundle index.html not found: ${indexHtmlPath}`
      );
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'install',
      });
    }
    const metadataFilePath = this.getMetadataFilePath({
      appVersion,
      bundleVersion,
    });
    if (!fs.existsSync(metadataFilePath)) {
      this.options.logger?.error(
        'bundle-install',
        `metadata.json not found in bundle: ${metadataFilePath}`
      );
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'install',
      });
    }
    if (!allowSkipGPG) {
      await this.verifySignedMetadata(
        {
          appVersion,
          bundleVersion,
          signature: signature!,
        },
        'install'
      );
    }
    this.verifyExtractedFiles(extractDir, 'install');

    if (
      currentUpdateBundleData &&
      currentUpdateBundleData.appVersion &&
      currentUpdateBundleData.bundleVersion &&
      currentUpdateBundleData.signature &&
      (currentUpdateBundleData.appVersion !== appVersion ||
        currentUpdateBundleData.bundleVersion !== bundleVersion)
    ) {
      fallbackUpdateBundleData.push(currentUpdateBundleData);
    }

    const evictedBundle =
      fallbackUpdateBundleData.length > 3
        ? fallbackUpdateBundleData.shift()
        : undefined;

    this.options.store.setUpdateBundleData({
      appVersion,
      bundleVersion,
      signature: signature ?? '',
    });
    this.options.store.setNativeVersion(this.options.appVersion());
    this.options.store.setNativeBuildNumber(this.options.buildNumber());
    this.options.store.setFallbackUpdateBundleData(fallbackUpdateBundleData);
    this.options.logger?.info('installBundle', { appVersion, bundleVersion });

    if (evictedBundle) {
      try {
        fs.rmSync(this.getBundleExtractDir(evictedBundle), {
          recursive: true,
          force: true,
        });
      } catch (error) {
        this.options.logger?.warn('Failed to remove evicted bundle:', error);
      }
    }
    await this.options.restart();
  }

  async clearDownload() {
    try {
      await this.clearDownloadInternal();
    } catch (error) {
      throw wrapBundleError(error, 'cleanup');
    }
  }

  private cleanupDiffStagingDirs() {
    try {
      const stagingRoot = path.join(
        this.options.userDataDir(),
        'onekey-bundle',
        DIFF_STAGING_DIR_NAME
      );
      if (fs.existsSync(stagingRoot)) {
        fs.rmSync(stagingRoot, {
          recursive: true,
          force: true,
        });
      }
    } catch (error) {
      this.options.logger?.error(
        'Failed to clean up diff staging dirs:',
        error
      );
    }
  }

  private async clearDownloadInternal(clearExtract = false) {
    const pending = this.cacheClearPromise;
    if (pending && !clearExtract) return pending;
    // Publish the barrier before cancellation callbacks can start another run.
    const clear = (
      pending ??
      Promise.resolve().then(async () => {
        const downloads = [...this.inflightDownloads.values()];
        const verifications = [
          ...this.activeVerifications.values(),
          ...this.activePackageVerifications,
        ];
        for (const operation of [...downloads, ...verifications]) {
          operation.controller.abort();
        }
        await Promise.allSettled([
          ...downloads.map(({ run }) => run),
          ...verifications.map(({ run }) => run),
        ]);
        fs.rmSync(this.getDownloadDir(), { recursive: true, force: true });
        this.cleanupDiffStagingDirs();
      })
    )
      .then(() => {
        if (clearExtract) this.removeBundleExtract();
      })
      .finally(() => {
        if (this.cacheClearPromise === clear)
          this.cacheClearPromise = undefined;
      });
    this.cacheClearPromise = clear;
    return clear;
  }

  async getFallbackUpdateBundleData() {
    return this.options.store.getFallbackUpdateBundleData();
  }

  async setCurrentUpdateBundleData(updateBundleData: IInstalledBundle) {
    try {
      await this.setCurrentUpdateBundleDataInternal(updateBundleData);
    } catch (error) {
      throw wrapBundleError(error, 'install');
    }
  }

  private async setCurrentUpdateBundleDataInternal(
    updateBundleData: IInstalledBundle
  ) {
    const { appVersion, bundleVersion, signature } = updateBundleData || {};
    assertUpdateBundleVersion(appVersion, 'install');
    assertUpdateBundleVersion(bundleVersion, 'install');
    if (
      !semver.valid(appVersion) ||
      !semver.eq(appVersion, this.options.appVersion())
    ) {
      throw createUpdateError({
        code: EUpdateErrorCode.incompatible,
        step: 'install',
      });
    }
    const allowSkipGPG = this.isSkipGPGAllowed();
    if (!allowSkipGPG && !signature) {
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'install',
      });
    }
    const extractDir = this.getBundleExtractDir({ appVersion, bundleVersion });
    if (!fs.existsSync(extractDir)) {
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'install',
      });
    }
    const indexHtmlPath = path.join(extractDir, 'build', 'index.html');
    if (!fs.existsSync(indexHtmlPath)) {
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'install',
      });
    }
    const metadataFilePath = this.getMetadataFilePath({
      appVersion,
      bundleVersion,
    });
    if (!fs.existsSync(metadataFilePath)) {
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'install',
      });
    }
    if (!allowSkipGPG) {
      await this.verifySignedMetadata(
        {
          appVersion,
          bundleVersion,
          signature: signature!,
        },
        'install'
      );
    }
    this.verifyExtractedFiles(extractDir, 'install');

    this.options.store.setUpdateBundleData(updateBundleData);
    this.options.store.setNativeVersion(this.options.appVersion());
    this.options.store.setNativeBuildNumber(this.options.buildNumber());
    if (appVersion && bundleVersion) {
      await this.options.restart();
    }
  }

  async clearBundleExtract() {
    this.removeBundleExtract();
  }

  private removeBundleExtract() {
    try {
      fs.rmSync(path.join(this.options.userDataDir(), 'onekey-bundle'), {
        recursive: true,
        force: true,
      });
    } catch (error) {
      this.options.logger?.error('Failed to clear bundle extract:', error);
      throw createUpdateError({
        code: EUpdateErrorCode.storageError,
        step: 'cleanup',
      });
    }
  }

  async clearBundle() {
    try {
      await this.clearDownloadInternal(true);
    } catch (error) {
      throw wrapBundleError(error, 'cleanup');
    }
    return new Promise<void>((resolve) => {
      setTimeout(() => {
        resolve();
      }, 300);
    });
  }

  async resetToBuiltInBundle() {
    try {
      this.options.store.clearUpdateBundleData();
      this.options.logger?.info(
        'resetToBuiltInBundle: cleared update bundle data, app will use built-in bundle on next restart'
      );
    } catch (error) {
      throw wrapBundleError(error, 'cleanup');
    }
  }

  async clearAllJSBundleData() {
    try {
      await this.clearDownloadInternal(true);
      this.diffFallbackTargets.clear();
      this.options.store.clearUpdateBundleData();
      return await new Promise<{ success: boolean; message: string }>(
        (resolve) => {
          setTimeout(() => {
            resolve({
              success: true,
              message: 'Successfully cleared all JS bundle data',
            });
          }, 300);
        }
      );
    } catch (error) {
      throw wrapBundleError(error, 'cleanup');
    }
  }
}
