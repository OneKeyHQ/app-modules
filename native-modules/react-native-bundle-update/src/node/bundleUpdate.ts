import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import AdmZip from 'adm-zip';
import semver from 'semver';

import { downloadNodeFile, stripSensitiveHeaders } from './download';
import { EUpdateErrorCode, createUpdateError, getUpdateError } from './errors';
import { assertSafeBundleVersion, resolveBundlePath } from './paths';
import {
  calculateSHA256,
  checkFileSha512,
  lastSHA256FailureReason,
  readSignedMetadataSha256,
  SignedMetadataError,
  verifySha256,
} from './verification';
import type { TUpdateErrorStep } from './errors';
import type { ISignedMetadataIdentity } from './verification';
import type {
  IBundleUpdateIdentity,
  IBundleUpdateNodeOptions,
  IDownloadPackageParams,
  IInstalledBundle,
  IUpdateDownloadedEvent,
  IUpdateOperationParams,
} from './types';

export const MAX_BUNDLE_UNCOMPRESSED_BYTES = 512 * 1024 * 1024;
export const MAX_BUNDLE_ENTRY_COUNT = 4096;
export const MAX_CONTROL_JSON_BYTES = 16 * 1024 * 1024;
export const DIFF_DOWNLOAD_DIR_NAME = '.diff';
export const DIFF_STAGING_DIR_NAME = '.diff-staging';

// Keep the download boundary limited to the shared updater payload.
function wrapDownloadError(error: unknown) {
  return createUpdateError(getUpdateError(error, 'download'));
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

const diffGpgErrors = new WeakSet<object>();

export class BundleUpdateNode {
  private diffFallbackTargets = new Set<string>();

  private activeVerifications = new Set<{
    cancel: () => void;
    promise: Promise<void>;
  }>();

  get isDownloading(): boolean {
    return this.cancelByDest.size > 0;
  }

  // OCDS §5.8 per-destination single-flight. A second request with the same
  // content hash joins the writer; a different hash cannot write that ZIP.
  // The entry is always deleted on settle (finally)
  // so a crashed/rejected run never permanently wedges a destination — the
  // in-memory map is the reclaimable lock (the process restarting clears it
  // entirely, which is the desktop equivalent of stale-lock recovery).
  private inflightDownloads = new Map<
    string,
    { sha256: string; run: Promise<IUpdateDownloadedEvent> }
  >();

  // OCDS §5.8: cancellation wired per-destination so cancelling one dest does
  // not tear down an unrelated concurrent run.
  private cancelByDest = new Map<string, () => void>();

  private cacheClearPromise: Promise<void> | undefined;

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
    params: IInstalledBundle & {
      expectedAppVersion?: string;
      metadataFilePath?: string;
    },
    step: TUpdateErrorStep
  ) {
    try {
      const metadataFilePath =
        params.metadataFilePath ?? this.getMetadataFilePath(params);
      if (!fs.existsSync(metadataFilePath)) {
        throw createUpdateError({
          code: EUpdateErrorCode.packageMissing,
          step,
        });
      }
      const hash = await this.readMetadataFileSha256(params.signature, {
        appVersion: params.expectedAppVersion ?? params.appVersion,
        bundleVersion: params.bundleVersion,
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

  private getBundleTargetKey(appVersion: string, bundleVersion: string) {
    return `${appVersion}-${bundleVersion}`;
  }

  // The destination zip path for a (appVersion, bundleVersion) pair.
  private getDestZipPath(
    params?: Pick<IDownloadPackageParams, 'latestVersion' | 'bundleVersion'>
  ): string | null {
    const { latestVersion: appVersion, bundleVersion } = params || {};
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

  private getDestDiffZipPath(
    params?: Pick<IDownloadPackageParams, 'latestVersion' | 'bundleVersion'>
  ): string | null {
    const { latestVersion: appVersion, bundleVersion } = params || {};
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
    diffZipPath: string | null
  ): IDiffArchiveDescriptor | null {
    if (!diffZipPath) {
      return null;
    }
    const metaPath = this.getDestDiffDescriptorPath(diffZipPath);
    if (!fs.existsSync(metaPath)) {
      return null;
    }
    try {
      const stat = fs.lstatSync(metaPath, { throwIfNoEntry: false });
      if (
        !stat ||
        stat.isSymbolicLink() ||
        stat.size <= 0 ||
        stat.size > 65_536
      ) {
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

  private writeDiffDescriptor(
    diffZipPath: string | null,
    descriptor: IDiffArchiveDescriptor
  ) {
    if (!diffZipPath) {
      return;
    }
    try {
      const existing = this.readDiffDescriptor(diffZipPath);
      const preservedSignature =
        descriptor.signature ??
        (existing &&
        existing.appVersion === descriptor.appVersion &&
        existing.bundleVersion === descriptor.bundleVersion &&
        existing.sha256 === descriptor.sha256
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
      // ignore descriptor write failure
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

  private cleanupDiffArtifacts(
    diffZipPath: string | null,
    stagingDir?: string,
    options?: { keepDescriptor?: boolean }
  ) {
    if (diffZipPath) {
      const targets = [
        diffZipPath,
        `${diffZipPath}.partial`,
        `${diffZipPath}.partial.progress.json`,
      ];
      if (!options?.keepDescriptor) {
        targets.push(
          this.getDestDiffDescriptorPath(diffZipPath),
          `${this.getDestDiffDescriptorPath(diffZipPath)}.tmp`
        );
      }
      for (const target of targets) {
        try {
          fs.rmSync(target, { force: true });
        } catch {
          // ignore cleanup failure
        }
      }
    }
    if (stagingDir) {
      try {
        fs.rmSync(stagingDir, { recursive: true, force: true });
      } catch {
        // ignore cleanup failure
      }
    }
  }

  // Public entry point. OCDS §5.8 per-destination single-flight: a second
  // download() for the same destination JOINS the in-flight run instead of
  // co-writing the same artifacts; a different destination proceeds
  // independently. The map entry is removed on settle so a crashed/rejected run
  // never wedges the destination (in-memory map = reclaimable lock).
  async downloadBundle(
    params: IDownloadPackageParams
  ): Promise<IUpdateDownloadedEvent> {
    try {
      return await this.downloadBundleInternal(params);
    } catch (error) {
      throw wrapBundleError(error, 'download');
    }
  }

  private async downloadBundleInternal(
    params: IDownloadPackageParams
  ): Promise<IUpdateDownloadedEvent> {
    while (this.cacheClearPromise) await this.cacheClearPromise;
    const safeParams = params || ({} as IDownloadPackageParams);
    assertUpdateBundleVersion(safeParams.latestVersion, 'download');
    assertUpdateBundleVersion(safeParams.bundleVersion, 'download');
    const destKey = this.getDestZipPath(safeParams);
    const validSha256 =
      typeof safeParams.sha256 === 'string' &&
      /^[a-fA-F0-9]{64}$/.test(safeParams.sha256)
        ? safeParams.sha256.toLowerCase()
        : null;
    // No usable key (missing version/sha256 params): skip single-flight bookkeeping
    // and let the downstream validation produce the canonical error.
    if (!destKey || !validSha256) {
      return this.runDownloadBundle(safeParams);
    }
    const existing = this.inflightDownloads.get(destKey);
    if (existing) {
      if (existing.sha256 !== validSha256) {
        throw createUpdateError({
          code: EUpdateErrorCode.incompatible,
          step: 'download',
        });
      }
      this.options.logger?.info(
        'bundle-download',
        'Joining in-flight download for same destination'
      );
      return existing.run;
    }
    const run = this.runDownloadBundle(safeParams).finally(() => {
      this.inflightDownloads.delete(destKey);
      this.cancelByDest.delete(destKey);
    });
    this.inflightDownloads.set(destKey, { sha256: validSha256, run });
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
    controller,
  }: {
    url: string;
    targetPath: string;
    identity: string;
    headers?: Record<string, string>;
    expectedBytes?: number;
    expectedSha256: string;
    latestVersion: string;
    bundleVersion: string;
    controller: AbortController;
  }) {
    const cancel = () => controller.abort();
    this.cancelByDest.set(targetPath, cancel);
    try {
      if (this.cancelByDest.size === 1) {
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
        signal: controller.signal,
        onProgress: (progress) => {
          this.options.onProgress?.({
            ...progress,
            latestVersion,
            bundleVersion,
          });
        },
      });
    } finally {
      this.cancelByDest.delete(targetPath);
      if (!this.isDownloading) {
        this.options.onDownloadStateChange?.(false);
      }
    }
  }

  // The shared downloader owns range selection, resume, and integrity checks.
  private async runDownloadBundle(
    params: IDownloadPackageParams
  ): Promise<IUpdateDownloadedEvent> {
    const {
      latestVersion,
      bundleVersion,
      downloadUrl,
      sha256,
      signature,
      fileSize,
      diffDownloadUrl,
      diffFileSize,
      diffSha256,
      diffSignature,
      headers,
    } = params || {};
    const normalizedSha256 = normalizeSha256Hex(sha256);
    if (!latestVersion || !bundleVersion || !downloadUrl || !normalizedSha256) {
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'download',
      });
    }
    const filePath = this.getDestZipPath(params);
    if (!filePath) {
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'download',
      });
    }
    const allowSkipGPG = this.isSkipGPGAllowed();
    const targetKey = this.getBundleTargetKey(latestVersion, bundleVersion);
    const normalizedDiffSha256 = normalizeSha256Hex(diffSha256);
    const hasValidDiffSignature =
      (typeof diffSignature === 'string' && diffSignature.trim().length > 0) ||
      allowSkipGPG;
    const diffZipPath = this.getDestDiffZipPath(params);
    const existingDescriptor = this.readDiffDescriptor(diffZipPath);
    const isFallbackRequiredForCurrentRelease = Boolean(
      existingDescriptor?.fallbackRequired &&
        existingDescriptor.appVersion === latestVersion &&
        existingDescriptor.bundleVersion === bundleVersion &&
        existingDescriptor.sha256 === normalizedSha256
    );
    const isDiffEligible =
      isValidHttpsUrl(diffDownloadUrl) &&
      Boolean(normalizedDiffSha256) &&
      hasValidDiffSignature &&
      !this.diffFallbackTargets.has(targetKey) &&
      !isFallbackRequiredForCurrentRelease;

    if (isDiffEligible && diffZipPath) {
      const diffController = new AbortController();
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
          controller: diffController,
        });
        this.writeDiffDescriptor(diffZipPath, {
          appVersion: latestVersion,
          bundleVersion,
          downloadUrl,
          fileSize: normalizePositiveInt(fileSize),
          sha256: normalizedSha256,
          diffSha256: normalizedDiffSha256,
          signature:
            typeof signature === 'string' && signature.trim().length > 0
              ? signature
              : undefined,
          fallbackRequired: false,
        });
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
        const errorCode = getUpdateError(wrappedDiffError, 'download').code;
        if (
          diffController.signal.aborted ||
          errorCode === EUpdateErrorCode.cancelled ||
          errorCode === EUpdateErrorCode.networkError ||
          errorCode === EUpdateErrorCode.timeout
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
        this.diffFallbackTargets.add(targetKey);
        this.cleanupDiffArtifacts(diffZipPath);
      }
    }

    const controller = new AbortController();
    try {
      await this.downloadArchiveFile({
        url: downloadUrl,
        targetPath: filePath,
        identity: `bundle:${latestVersion}:${bundleVersion}:${normalizedSha256}`,
        headers,
        expectedBytes: fileSize,
        expectedSha256: normalizedSha256,
        latestVersion,
        bundleVersion,
        controller,
      });
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
      await this.verifyBundleInternal(params);
    } catch (error) {
      throw wrapBundleError(error, 'verifyPackage');
    }
  }

  private resolvePersistedTrustAnchorSignature(
    appVersion: string,
    bundleVersion: string,
    normalizedSha256: string,
    explicitSignature?: string
  ): string | undefined {
    if (typeof explicitSignature === 'string' && explicitSignature.trim()) {
      return explicitSignature;
    }
    const diffZipPath = this.getDestDiffZipPath({
      latestVersion: appVersion,
      bundleVersion,
    });
    const descriptor = this.readDiffDescriptor(diffZipPath);
    if (
      descriptor &&
      descriptor.appVersion === appVersion &&
      descriptor.bundleVersion === bundleVersion &&
      descriptor.sha256 === normalizedSha256 &&
      descriptor.signature
    ) {
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

  private async verifyBundleInternal(params: IUpdateOperationParams) {
    const {
      sha256,
      latestVersion: appVersion,
      bundleVersion,
      signature,
      diffSha256,
    } = params || {};
    assertUpdateBundleVersion(appVersion, 'verifyPackage');
    assertUpdateBundleVersion(bundleVersion, 'verifyPackage');
    const allowSkipGPG = this.isSkipGPGAllowed();
    const normalizedSha256 = normalizeSha256Hex(sha256);
    if (!normalizedSha256 || !appVersion || !bundleVersion) {
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'verifyPackage',
      });
    }

    const extractDir = this.getBundleExtractDir({ appVersion, bundleVersion });
    const metadataFilePath = path.join(extractDir, 'metadata.json');
    if (fs.existsSync(extractDir) && fs.existsSync(metadataFilePath)) {
      if (
        fs.lstatSync(extractDir, { throwIfNoEntry: false })?.isSymbolicLink() ||
        fs
          .lstatSync(metadataFilePath, { throwIfNoEntry: false })
          ?.isSymbolicLink()
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyPackage',
        });
      }
      const metadataStat = fs.statSync(metadataFilePath);
      if (
        metadataStat.size <= 0 ||
        metadataStat.size > MAX_CONTROL_JSON_BYTES
      ) {
        throw createUpdateError({
          code:
            metadataStat.size > MAX_CONTROL_JSON_BYTES
              ? EUpdateErrorCode.securityRejected
              : EUpdateErrorCode.integrityFailed,
          step: 'verifyPackage',
        });
      }
      const effectiveSignature = this.resolvePersistedTrustAnchorSignature(
        appVersion,
        bundleVersion,
        normalizedSha256,
        signature
      );
      if (!allowSkipGPG) {
        if (!effectiveSignature) {
          throw createUpdateError({
            code: EUpdateErrorCode.invalidArgument,
            step: 'verifyPackage',
          });
        }
        await this.verifySignedMetadata(
          {
            appVersion,
            expectedAppVersion: appVersion,
            bundleVersion,
            signature: effectiveSignature,
          },
          'verifyPackage'
        );
      }
      const metadata = JSON.parse(
        fs.readFileSync(metadataFilePath, 'utf8')
      ) as Record<string, string>;
      if (
        !metadata ||
        typeof metadata !== 'object' ||
        Array.isArray(metadata)
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyPackage',
        });
      }
      this.verifyAllExtractedFiles(extractDir, metadata, extractDir);
      return;
    }

    const downloadedFile = this.getDestZipPath({
      latestVersion: appVersion,
      bundleVersion,
    });
    if (downloadedFile && fs.existsSync(downloadedFile)) {
      const resolvedRoot = fs.realpathSync(this.getDownloadDir());
      const resolvedFile = fs.realpathSync(downloadedFile);
      if (!resolvedFile.startsWith(`${resolvedRoot}${path.sep}`)) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyPackage',
        });
      }
      if (
        !verifySha256(downloadedFile, normalizedSha256, this.options.logger)
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyPackage',
        });
      }
      return;
    }

    const diffZipPath = this.getDestDiffZipPath({
      latestVersion: appVersion,
      bundleVersion,
    });
    if (diffZipPath && fs.existsSync(diffZipPath)) {
      const resolvedRoot = fs.realpathSync(this.getDownloadDir());
      const resolvedDiffFile = fs.realpathSync(diffZipPath);
      if (!resolvedDiffFile.startsWith(`${resolvedRoot}${path.sep}`)) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyPackage',
        });
      }
      const descriptor = this.readDiffDescriptor(diffZipPath);
      const expectedDiffSha256 =
        normalizeSha256Hex(diffSha256) ||
        (descriptor &&
        descriptor.appVersion === appVersion &&
        descriptor.bundleVersion === bundleVersion &&
        descriptor.sha256 === normalizedSha256
          ? descriptor.diffSha256
          : '');
      if (
        !expectedDiffSha256 ||
        !verifySha256(diffZipPath, expectedDiffSha256)
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyPackage',
        });
      }
      return;
    }

    if (!signature && !allowSkipGPG) {
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'verifyPackage',
      });
    }
    throw createUpdateError({
      code: EUpdateErrorCode.packageMissing,
      step: 'verifyPackage',
    });
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
      if (!fs.existsSync(candidatePath)) {
        return null;
      }
      // Verify SHA-512 match BEFORE enforcing the remaining byte budget so
      // an oversized unmatched candidate in an earlier directory is skipped
      // instead of blocking a smaller matching file in a later candidate.
      if (!checkFileSha512(candidatePath, expectedSha512)) {
        return null;
      }
      const stat = fs.statSync(candidatePath);
      if (
        !stat.isFile() ||
        !Number.isFinite(stat.size) ||
        stat.size < 0 ||
        stat.size > remainingBytes
      ) {
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
    const normalizedDiffSha256 = normalizeSha256Hex(diffSha256);
    if (!allowSkipGPG || normalizedDiffSha256) {
      if (
        !normalizedDiffSha256 ||
        !verifySha256(diffZipPath, normalizedDiffSha256)
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyASC',
        });
      }
    }
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

    if (isInstalled && fs.existsSync(extractDir)) {
      const metadataFilePath = this.getMetadataFilePath({
        appVersion,
        bundleVersion,
      });
      if (!fs.existsSync(metadataFilePath)) {
        throw createUpdateError({
          code: EUpdateErrorCode.packageMissing,
          step: 'verifyASC',
        });
      }
      if (!allowSkipGPG) {
        await this.verifySignedMetadata(
          {
            appVersion,
            expectedAppVersion: appVersion,
            bundleVersion,
            signature: signature!,
          },
          'verifyASC'
        );
      }
      const metadata = JSON.parse(
        fs.readFileSync(metadataFilePath, 'utf8')
      ) as Record<string, string>;
      this.verifyAllExtractedFiles(extractDir, metadata, extractDir);
      return;
    }

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
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyASC',
        });
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
      fs.writeFileSync(destFilePath, fs.readFileSync(matched.sourcePath));
    }

    fs.rmSync(diffManifestPath, { force: true });
    this.verifyAllExtractedFiles(stagingDir, targetMetadata, stagingDir);
    throwIfAborted();

    fs.rmSync(extractDir, { recursive: true, force: true });
    fs.renameSync(stagingDir, extractDir);
  }

  private async verifyBundleASCInternal(params: IUpdateOperationParams) {
    while (this.cacheClearPromise) await this.cacheClearPromise;
    const verifyController = new AbortController();
    let resolveVerifyDone!: () => void;
    const verifyPromise = new Promise<void>((resolve) => {
      resolveVerifyDone = resolve;
    });
    const verificationHandle = {
      cancel: () => verifyController.abort(),
      promise: verifyPromise,
    };
    this.activeVerifications.add(verificationHandle);
    try {
      await this.runVerifyBundleASC(params, verifyController);
    } finally {
      this.activeVerifications.delete(verificationHandle);
      resolveVerifyDone();
    }
  }

  private async runVerifyBundleASC(
    params: IUpdateOperationParams,
    verifyController: AbortController
  ) {
    const throwIfAborted = () => {
      if (verifyController.signal.aborted) {
        throw createUpdateError({
          code: EUpdateErrorCode.cancelled,
          step: 'verifyASC',
        });
      }
    };
    const {
      sha256,
      latestVersion: appVersion,
      bundleVersion,
      signature,
      diffSha256,
      diffSignature,
    } = params || {};
    assertUpdateBundleVersion(appVersion, 'verifyASC');
    assertUpdateBundleVersion(bundleVersion, 'verifyASC');
    const downloadedFile = this.getDestZipPath({
      latestVersion: appVersion,
      bundleVersion,
    });
    const diffZipPath = this.getDestDiffZipPath({
      latestVersion: appVersion,
      bundleVersion,
    });
    const allowSkipGPG = this.isSkipGPGAllowed();
    const normalizedSha256 = normalizeSha256Hex(sha256);
    if (
      !downloadedFile ||
      !normalizedSha256 ||
      !appVersion ||
      !bundleVersion ||
      (!signature && !allowSkipGPG)
    ) {
      this.options.logger?.error('bundle-verifyASC', 'Invalid parameters', {
        downloadedFile,
        sha256,
        appVersion,
        bundleVersion,
        hasSignature: !!signature,
        allowSkipGPG,
      });
      throw createUpdateError({
        code: EUpdateErrorCode.invalidArgument,
        step: 'verifyASC',
      });
    }
    if (this.activeVerifications.size === 1) this.cleanupDiffStagingDirs();
    const targetKey = this.getBundleTargetKey(appVersion, bundleVersion);
    const persistedDescriptor = this.readDiffDescriptor(diffZipPath);
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

    const resolveFullFallbackDownloadParams = ():
      | (IDownloadPackageParams & { downloadUrl: string })
      | null => {
      let fallbackUrl = '';
      if (isValidHttpsUrl(params?.downloadUrl)) {
        fallbackUrl = params.downloadUrl;
      } else if (
        persistedDescriptor &&
        persistedDescriptor.appVersion === appVersion &&
        persistedDescriptor.bundleVersion === bundleVersion &&
        persistedDescriptor.sha256 === normalizedSha256 &&
        isValidHttpsUrl(persistedDescriptor.downloadUrl)
      ) {
        fallbackUrl = persistedDescriptor.downloadUrl;
      }
      if (!fallbackUrl) {
        return null;
      }
      const fallbackFileSize =
        normalizePositiveInt(params?.fileSize) ?? persistedDescriptor?.fileSize;
      return {
        ...params,
        latestVersion: appVersion,
        bundleVersion,
        downloadUrl: fallbackUrl,
        fileSize: fallbackFileSize,
        sha256: normalizedSha256,
        diffDownloadUrl: undefined,
        diffFileSize: undefined,
        diffSha256: undefined,
        diffSignature: undefined,
      };
    };

    const isFallbackRequiredForRelease = Boolean(
      persistedDescriptor?.fallbackRequired &&
        persistedDescriptor.appVersion === appVersion &&
        persistedDescriptor.bundleVersion === bundleVersion &&
        persistedDescriptor.sha256 === normalizedSha256
    );

    if (
      !fs.existsSync(downloadedFile) &&
      diffZipPath &&
      fs.existsSync(diffZipPath) &&
      !this.diffFallbackTargets.has(targetKey) &&
      !isFallbackRequiredForRelease
    ) {
      const resolvedRoot = fs.realpathSync(this.getDownloadDir());
      const resolvedDiffFile = fs.realpathSync(diffZipPath);
      if (!resolvedDiffFile.startsWith(`${resolvedRoot}${path.sep}`)) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      const extractDir = this.getBundleExtractDir({
        appVersion,
        bundleVersion,
      });
      if (
        fs.lstatSync(extractDir, { throwIfNoEntry: false })?.isSymbolicLink()
      ) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      const stagingDir = this.createDiffStagingDir(appVersion, bundleVersion);
      const effectiveDiffSha256 =
        normalizeSha256Hex(diffSha256) ||
        (persistedDescriptor &&
        persistedDescriptor.appVersion === appVersion &&
        persistedDescriptor.bundleVersion === bundleVersion &&
        persistedDescriptor.sha256 === normalizedSha256
          ? persistedDescriptor.diffSha256
          : '');
      try {
        await this.reconstructDiffBundle({
          appVersion,
          bundleVersion,
          diffZipPath,
          stagingDir,
          extractDir,
          allowSkipGPG,
          signature,
          diffSha256: effectiveDiffSha256,
          diffSignature,
          isInstalled,
          signal: verifyController.signal,
        });
        fs.rmSync(stagingDir, { recursive: true, force: true });
        if (diffZipPath) {
          this.writeDiffDescriptor(diffZipPath, {
            appVersion,
            bundleVersion,
            downloadUrl:
              (isValidHttpsUrl(params?.downloadUrl)
                ? params.downloadUrl
                : persistedDescriptor?.downloadUrl) || 'https://onekey.so',
            fileSize:
              normalizePositiveInt(params?.fileSize) ??
              persistedDescriptor?.fileSize,
            sha256: normalizedSha256,
            diffSha256: effectiveDiffSha256 || normalizedSha256,
            signature:
              typeof signature === 'string' && signature.trim().length > 0
                ? signature
                : persistedDescriptor?.signature,
            fallbackRequired: false,
          });
        }
        return;
      } catch (error) {
        fs.rmSync(stagingDir, { recursive: true, force: true });
        const errCode = getUpdateError(error, 'verifyASC').code;
        if (
          verifyController.signal.aborted ||
          errCode === EUpdateErrorCode.cancelled
        ) {
          throw createUpdateError({
            code: EUpdateErrorCode.cancelled,
            step: 'verifyASC',
          });
        }
        if (
          (error && typeof error === 'object' && diffGpgErrors.has(error)) ||
          errCode === EUpdateErrorCode.securityRejected
        ) {
          this.cleanupDiffArtifacts(diffZipPath, stagingDir);
          this.diffFallbackTargets.add(targetKey);
          throw error;
        }
        this.options.logger?.warn(
          'bundle-verifyASC',
          'Diff bundle reconstruction failed, falling back to full bundle',
          {
            appVersion,
            bundleVersion,
            error: error instanceof Error ? error.message : String(error),
          }
        );
        this.diffFallbackTargets.add(targetKey);
        const fallbackParams = resolveFullFallbackDownloadParams();
        if (diffZipPath && fallbackParams) {
          this.writeDiffDescriptor(diffZipPath, {
            appVersion,
            bundleVersion,
            downloadUrl: fallbackParams.downloadUrl,
            fileSize: fallbackParams.fileSize,
            sha256: normalizedSha256,
            diffSha256: effectiveDiffSha256 || normalizedSha256,
            signature:
              typeof signature === 'string' && signature.trim().length > 0
                ? signature
                : persistedDescriptor?.signature,
            fallbackRequired: true,
          });
        }
        this.cleanupDiffArtifacts(diffZipPath, stagingDir, {
          keepDescriptor: Boolean(fallbackParams),
        });
        if (!fallbackParams) {
          throw createUpdateError({
            code: EUpdateErrorCode.packageMissing,
            step: 'verifyASC',
          });
        }
        throwIfAborted();
        await this.runDownloadBundle(fallbackParams);
        throwIfAborted();
      }
    } else if (
      !fs.existsSync(downloadedFile) &&
      (isFallbackRequiredForRelease || this.diffFallbackTargets.has(targetKey))
    ) {
      const fallbackParams = resolveFullFallbackDownloadParams();
      if (fallbackParams) {
        this.cleanupDiffArtifacts(diffZipPath, undefined, {
          keepDescriptor: true,
        });
        throwIfAborted();
        await this.runDownloadBundle(fallbackParams);
        throwIfAborted();
      }
    }
    throwIfAborted();
    if (fs.existsSync(downloadedFile)) {
      const resolvedRoot = fs.realpathSync(this.getDownloadDir());
      const resolvedFile = fs.realpathSync(downloadedFile);
      if (!resolvedFile.startsWith(`${resolvedRoot}${path.sep}`)) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
    }
    if (!fs.existsSync(downloadedFile)) {
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'verifyASC',
      });
    }
    if (!allowSkipGPG) {
      const isBundleVerified = verifySha256(
        downloadedFile,
        normalizedSha256,
        this.options.logger
      );
      if (!isBundleVerified) {
        // Keep detailed SHA failures local; the bridge reports one fixed code.
        const reason = lastSHA256FailureReason() ?? 'MISMATCH';
        this.options.logger?.error(
          'bundle-verifyASC',
          `SHA256 verification failed (reason=${reason})`
        );
        let code = EUpdateErrorCode.storageError;
        if (reason === 'FILE_NOT_FOUND') {
          code = EUpdateErrorCode.packageMissing;
        } else if (reason === 'MISMATCH' || reason === 'FILE_EMPTY') {
          code = EUpdateErrorCode.integrityFailed;
        }
        throw createUpdateError({
          code,
          step: 'verifyASC',
        });
      }
      try {
        await this.readMetadataFileSha256(signature!, {
          appVersion,
          bundleVersion,
        });
      } catch (error) {
        throw wrapSignedMetadataError(error, 'verifyASC');
      }
    }
    throwIfAborted();
    const extractDir = this.getBundleExtractDir({
      appVersion,
      bundleVersion,
    });
    if (fs.lstatSync(extractDir, { throwIfNoEntry: false })?.isSymbolicLink()) {
      throw createUpdateError({
        code: EUpdateErrorCode.securityRejected,
        step: 'verifyASC',
      });
    }
    if (isInstalled) {
      const metadataFilePath = this.getMetadataFilePath({
        appVersion,
        bundleVersion,
      });
      if (!allowSkipGPG) {
        await this.verifySignedMetadata(
          {
            appVersion,
            expectedAppVersion: appVersion,
            bundleVersion,
            signature: signature!,
          },
          'verifyASC'
        );
      }
      const metadata = JSON.parse(
        fs.readFileSync(metadataFilePath, 'utf8')
      ) as Record<string, string>;
      this.verifyAllExtractedFiles(extractDir, metadata, extractDir);
      if (diffZipPath) {
        this.writeDiffDescriptor(diffZipPath, {
          appVersion,
          bundleVersion,
          downloadUrl:
            (isValidHttpsUrl(params?.downloadUrl)
              ? params.downloadUrl
              : persistedDescriptor?.downloadUrl) || 'https://onekey.so',
          fileSize:
            normalizePositiveInt(params?.fileSize) ??
            persistedDescriptor?.fileSize,
          sha256: normalizedSha256,
          diffSha256:
            normalizeSha256Hex(diffSha256) ||
            persistedDescriptor?.diffSha256 ||
            normalizedSha256,
          signature:
            typeof signature === 'string' && signature.trim().length > 0
              ? signature
              : persistedDescriptor?.signature,
          fallbackRequired: false,
        });
      }
      return;
    }

    try {
      const zip = new AdmZip(downloadedFile);
      const entries = this.validateAndGetZipEntries(
        zip,
        extractDir,
        'verifyASC'
      );
      const metadataEntry = entries.find(
        (e) =>
          !e.isDirectory &&
          e.entryName.replace(/\\/g, '/').replace(/^\.\//, '') ===
            'metadata.json'
      );
      if (!metadataEntry || !(metadataEntry.header.size > 0)) {
        throw createUpdateError({
          code: EUpdateErrorCode.packageMissing,
          step: 'verifyASC',
        });
      }
      if (metadataEntry.header.size > MAX_CONTROL_JSON_BYTES) {
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyASC',
        });
      }
      fs.rmSync(extractDir, { recursive: true, force: true });
      if (!allowSkipGPG) {
        if (typeof zip.extractEntryTo === 'function') {
          zip.extractEntryTo(metadataEntry, extractDir, false, true);
        } else if (typeof metadataEntry.getData === 'function') {
          fs.mkdirSync(extractDir, { recursive: true });
          fs.writeFileSync(
            this.getMetadataFilePath({ appVersion, bundleVersion }),
            metadataEntry.getData()
          );
        }
        await this.verifySignedMetadata(
          {
            appVersion,
            expectedAppVersion: appVersion,
            bundleVersion,
            signature: signature!,
          },
          'verifyASC'
        );
      }
      zip.extractAllTo(extractDir, true);
    } catch (error) {
      this.options.logger?.error('Failed to extract bundle zip file:', error);
      // Cleanup partially extracted directory
      if (fs.existsSync(extractDir)) {
        fs.rmSync(extractDir, { recursive: true, force: true });
      }
      throw error;
    }

    try {
      const metadataFilePath = this.getMetadataFilePath({
        appVersion,
        bundleVersion,
      });
      this.options.logger?.info(
        'bundle-verifyBundleASC',
        metadataFilePath,
        allowSkipGPG
      );
      if (!allowSkipGPG) {
        await this.verifySignedMetadata(
          {
            appVersion,
            expectedAppVersion: appVersion,
            bundleVersion,
            signature: signature!,
          },
          'verifyASC'
        );
      }

      // Verify all extracted files against metadata SHA-512 hashes
      if (!fs.existsSync(metadataFilePath)) {
        throw createUpdateError({
          code: EUpdateErrorCode.packageMissing,
          step: 'verifyASC',
        });
      }
      const metadataContent = fs.readFileSync(metadataFilePath, 'utf8');
      const metadata = JSON.parse(metadataContent) as Record<string, string>;
      this.verifyAllExtractedFiles(extractDir, metadata, extractDir);
      if (diffZipPath) {
        this.writeDiffDescriptor(diffZipPath, {
          appVersion,
          bundleVersion,
          downloadUrl:
            (isValidHttpsUrl(params?.downloadUrl)
              ? params.downloadUrl
              : persistedDescriptor?.downloadUrl) || 'https://onekey.so',
          fileSize:
            normalizePositiveInt(params?.fileSize) ??
            persistedDescriptor?.fileSize,
          sha256: normalizedSha256,
          diffSha256:
            normalizeSha256Hex(diffSha256) ||
            persistedDescriptor?.diffSha256 ||
            normalizedSha256,
          signature:
            typeof signature === 'string' && signature.trim().length > 0
              ? signature
              : persistedDescriptor?.signature,
          fallbackRequired: false,
        });
      }
    } catch (error) {
      // Cleanup extracted directory on verification failure
      if (fs.existsSync(extractDir)) {
        fs.rmSync(extractDir, { recursive: true, force: true });
      }
      throw error;
    }
  }

  private verifyAllExtractedFiles(
    dirPath: string,
    metadata: Record<string, string>,
    baseDir: string
  ) {
    const verifiedFiles = new Set<string>();
    this.walkAndVerifyFiles(dirPath, metadata, baseDir, verifiedFiles);

    // Security: Verify completeness — every file in metadata must exist on disk
    const metadataKeys = Object.keys(metadata);
    for (const key of metadataKeys) {
      if (!verifiedFiles.has(key)) {
        this.options.logger?.error(
          'bundle-verify',
          `File listed in metadata but missing on disk: ${key}`
        );
        throw createUpdateError({
          code: EUpdateErrorCode.integrityFailed,
          step: 'verifyPackage',
        });
      }
    }
  }

  private walkAndVerifyFiles(
    dirPath: string,
    metadata: Record<string, string>,
    baseDir: string,
    verifiedFiles: Set<string>
  ) {
    const entries = fs.readdirSync(dirPath, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dirPath, entry.name);
      // Security: Reject symbolic links to prevent symlink attacks
      if (entry.isSymbolicLink()) {
        this.options.logger?.error(
          'bundle-verify',
          `Symbolic link detected: ${entry.name}`
        );
        throw createUpdateError({
          code: EUpdateErrorCode.securityRejected,
          step: 'verifyPackage',
        });
      }
      if (entry.isDirectory()) {
        this.walkAndVerifyFiles(fullPath, metadata, baseDir, verifiedFiles);
      } else if (entry.name !== 'metadata.json' && entry.name !== '.DS_Store') {
        // Strict contract: only files under "build/" are allowed to be hashed
        // by metadata. Any extra root-level file is treated as verification failure.
        const relativePath = path
          .relative(path.join(baseDir, 'build'), fullPath)
          .split(path.sep)
          .join('/');
        const expectedSha512 = metadata[relativePath];
        if (!expectedSha512) {
          this.options.logger?.error(
            'bundle-verify',
            `File on disk not found in metadata: ${relativePath}`
          );
          throw createUpdateError({
            code: EUpdateErrorCode.integrityFailed,
            step: 'verifyPackage',
          });
        }
        const isSha512Matched = checkFileSha512(fullPath, expectedSha512);
        if (!isSha512Matched) {
          this.options.logger?.error(
            'bundle-verify',
            `SHA512 mismatch for ${relativePath}`
          );
          throw createUpdateError({
            code: EUpdateErrorCode.integrityFailed,
            step: 'verifyPackage',
          });
        }
        verifiedFiles.add(relativePath);
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
    const entries = fs.readdirSync(bundleDir, { withFileTypes: true });
    const results: { appVersion: string; bundleVersion: string }[] = [];
    for (const entry of entries) {
      if (entry.isDirectory() && !entry.name.startsWith('.')) {
        const lastDash = entry.name.lastIndexOf('-');
        if (lastDash > 0) {
          const appVersion = entry.name.substring(0, lastDash);
          const bundleVersion = entry.name.substring(lastDash + 1);
          if (appVersion && bundleVersion) {
            results.push({ appVersion, bundleVersion });
          }
        }
      }
    }
    return results;
  }

  // Parse "{appVersion}-{bundleVersion}" for installed extract directories in
  // onekey-bundle/ without stripping ".diff" or ".diff-staging" so bundleVersion
  // values like "42.diff" or "42.diff-staging" remain intact.
  private parseAppVersionFromExtractDirName(name: string): string | null {
    if (!name || name.startsWith('.')) {
      return null;
    }
    const lastDash = name.lastIndexOf('-');
    if (lastDash <= 0) {
      return null;
    }
    const appVersion = name.substring(0, lastDash);
    const bundleVersion = name.substring(lastDash + 1);
    if (!appVersion || !bundleVersion) {
      return null;
    }
    return appVersion;
  }

  // Parse "{appVersion}-{bundleVersion}" for download artifacts in
  // onekey-bundle-download/ and onekey-bundle-download/.diff/.
  private parseAppVersionFromName(name: string): string | null {
    if (!name || name.startsWith('.')) {
      return null;
    }
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
    const lastDash = base.lastIndexOf('-');
    if (lastDash <= 0) {
      return null;
    }
    const appVersion = base.substring(0, lastDash);
    const bundleVersion = base.substring(lastDash + 1);
    if (!appVersion || !bundleVersion) {
      return null;
    }
    return appVersion;
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
          const appV = entry.isDirectory()
            ? this.parseAppVersionFromExtractDirName(entry.name)
            : null;
          // Safety net: keep current appVersion AND the active install dir.
          const isActiveInstall = Boolean(
            activeAppV &&
              activeBundleV &&
              entry.name === `${activeAppV}-${activeBundleV}`
          );
          if (appV && appV !== currentAppV && !isActiveInstall) {
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
        const appV = entry.isFile()
          ? this.parseAppVersionFromName(entry.name)
          : null;
        // Safety net: never delete current appVersion's download artifacts.
        if (appV && appV !== currentAppV) {
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
    const metadataFilePath = path.join(extractDir, 'metadata.json');
    if (!fs.existsSync(metadataFilePath)) {
      this.options.logger?.error(
        'bundle-verify',
        `verifyExtractedBundle: metadata.json not found in ${extractDir}`
      );
      throw createUpdateError({
        code: EUpdateErrorCode.packageMissing,
        step: 'verifyPackage',
      });
    }
    const metadataContent = fs.readFileSync(metadataFilePath, 'utf8');
    const metadata = JSON.parse(metadataContent) as Record<string, string>;
    this.verifyAllExtractedFiles(extractDir, metadata, extractDir);
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
          expectedAppVersion: appVersion,
          bundleVersion,
          signature: signature!,
        },
        'install'
      );
    }
    const metadataContent = fs.readFileSync(metadataFilePath, 'utf8');
    const metadata = JSON.parse(metadataContent) as Record<string, string>;
    this.verifyAllExtractedFiles(extractDir, metadata, extractDir);

    this.options.store.setUpdateBundleData({
      appVersion,
      bundleVersion,
      signature: signature ?? '',
    });
    this.options.logger?.info('installBundle', {
      appVersion,
      bundleVersion,
    });
    this.options.store.setNativeVersion(this.options.appVersion());
    const buildNumber = this.options.buildNumber();
    this.options.store.setNativeBuildNumber(buildNumber);
    this.options.logger?.info('installBundle setNativeVersion', {
      nativeVersion: this.options.appVersion(),
      buildNumber,
    });
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

    if (fallbackUpdateBundleData.length > 3) {
      const shiftUpdateBundleData = fallbackUpdateBundleData.shift();
      if (
        shiftUpdateBundleData &&
        !(
          shiftUpdateBundleData.appVersion === appVersion &&
          shiftUpdateBundleData.bundleVersion === bundleVersion
        )
      ) {
        const bundleDirPath = this.getBundleExtractDir(shiftUpdateBundleData);
        if (fs.existsSync(bundleDirPath)) {
          fs.rmSync(bundleDirPath, { recursive: true, force: true });
        }
      }
    }
    this.options.store.setFallbackUpdateBundleData(fallbackUpdateBundleData);
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

  private async clearDownloadInternal() {
    if (this.cacheClearPromise) return this.cacheClearPromise;
    // Publish the barrier before cancellation callbacks can start another run.
    const clear = Promise.resolve()
      .then(async () => {
        for (const cancel of this.cancelByDest.values()) cancel();
        for (const active of this.activeVerifications) active.cancel();
        await Promise.allSettled([
          ...[...this.inflightDownloads.values()].map(({ run }) => run),
          ...[...this.activeVerifications].map(({ promise }) => promise),
        ]);
        fs.rmSync(this.getDownloadDir(), { recursive: true, force: true });
        this.cleanupDiffStagingDirs();
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
          expectedAppVersion: appVersion,
          bundleVersion,
          signature: signature!,
        },
        'install'
      );
    }
    const metadataContent = fs.readFileSync(metadataFilePath, 'utf8');
    const metadata = JSON.parse(metadataContent) as Record<string, string>;
    this.verifyAllExtractedFiles(extractDir, metadata, extractDir);

    this.options.store.setUpdateBundleData(updateBundleData);
    this.options.store.setNativeVersion(this.options.appVersion());
    this.options.store.setNativeBuildNumber(this.options.buildNumber());
    if (appVersion && bundleVersion) {
      await this.options.restart();
    }
  }

  async clearBundleExtract() {
    const bundleDir = this.getBundleDirName();
    try {
      fs.rmSync(bundleDir, { recursive: true, force: true });
    } catch (error) {
      this.options.logger?.error('Failed to clear bundle extract:', error);
    }
  }

  async clearBundle() {
    await this.clearDownload();
    await this.clearBundleExtract();
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
      this.diffFallbackTargets.clear();
      await this.clearDownload();
      await this.clearBundleExtract();
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
