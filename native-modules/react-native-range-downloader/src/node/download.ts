import fs from 'node:fs';
import https from 'node:https';
import path from 'node:path';

import {
  NodeRangeDownloadCode,
  NodeRangeDownloadError,
  isRetryableRangeDownloadError,
} from './downloadError.js';
import type { TNodeRangeDownloadCode } from './downloadError.js';
import { ArchiveSizeError, hashArchive, MAX_ARCHIVE_BYTES } from './fileHash.js';

import type { IncomingHttpHeaders } from 'node:http';

export interface IUpdateResponse {
  response: {
    statusCode?: number;
    headers: IncomingHttpHeaders;
    resume: () => void;
    destroy: (error?: Error) => void;
    [Symbol.asyncIterator]: () => AsyncIterator<Buffer>;
  };
  url: string;
}

const SEGMENTS = 8;
const MIN_PARALLEL_BYTES = 2 * 1024 * 1024;
const FLUSH_BYTES = 4 * 1024 * 1024;
const MAX_REDIRECTS = 5;
const MAX_RETRIES = 3;
const STALL_MS = 60_000;
const DOWNLOAD_DEADLINE_MS = 15 * 60_000;
const MAX_RETRY_DELAY_MS = 60_000;

export interface INodeDownloadProgress {
  transferred: number;
  total: number;
  delta: number;
  bytesPerSecond: number;
  percent: number;
}

export interface INodeDownloadOptions {
  url: string;
  targetPath: string;
  identity: string;
  headers?: Record<string, string>;
  expectedSha512?: string;
  expectedSha256?: string;
  expectedBytes?: number;
  transport?: (
    url: string,
    headers: Record<string, string>,
    signal?: AbortSignal
  ) => Promise<IUpdateResponse>;
  onProgress?: (progress: INodeDownloadProgress) => void;
  signal?: AbortSignal;
}

interface IPart {
  start: number;
  end: number;
  done: number;
}

interface IManifest {
  identity: string;
  url: string;
  size: number;
  etag: string | null;
  checksum: string;
  mode: 'parallel' | 'single';
  parts: IPart[];
}

type IResponse = IUpdateResponse;

class RangeFallback extends Error {
  constructor(
    message: string,
    readonly refreshMetadata = false,
    readonly retryAfter: number | null = null
  ) {
    super(message);
  }
}

class DownloadHttpError extends NodeRangeDownloadError {
  constructor(
    status: number,
    readonly retryAfter: number | null
  ) {
    super(NodeRangeDownloadCode.httpError, status);
  }
}

function failure(code: TNodeRangeDownloadCode, httpStatus?: number) {
  return new NodeRangeDownloadError(code, httpStatus);
}

export function classifyNodeError(error: unknown) {
  try {
    if (error instanceof ArchiveSizeError)
      return failure(NodeRangeDownloadCode.integrityFailed);
    if (error instanceof RangeFallback) {
      return error.refreshMetadata
        ? failure(NodeRangeDownloadCode.httpError, 416)
        : failure(NodeRangeDownloadCode.integrityFailed);
    }
    if (error instanceof NodeRangeDownloadError) return error;
    const properties = error as {
      code?: unknown;
      httpStatus?: unknown;
      name?: unknown;
    } | null | undefined;
    const code = properties?.code;
    const downloadCode = Object.values(NodeRangeDownloadCode).find(
      (value) => value === code
    );
    if (downloadCode !== undefined) {
      const status =
        downloadCode === NodeRangeDownloadCode.httpError
          ? properties?.httpStatus
          : undefined;
      return failure(
        downloadCode,
        typeof status === 'number' ? status : undefined
      );
    }
    const errnoCode = typeof code === 'string' ? code : '';
    if (code === 'ENOENT' || code === 'ENOTDIR')
      return failure(NodeRangeDownloadCode.packageMissing);
    if (
      ['ENOSPC', 'EACCES', 'EPERM', 'EIO', 'EBUSY', 'EROFS'].includes(
        errnoCode
      )
    )
      return failure(NodeRangeDownloadCode.storageError);
    if (['ETIMEDOUT', 'ESOCKETTIMEDOUT'].includes(errnoCode))
      return failure(NodeRangeDownloadCode.timeout);
    if (
      [
        'ECONNRESET',
        'ECONNREFUSED',
        'ENETUNREACH',
        'EHOSTUNREACH',
        'EAI_AGAIN',
        'ENOTFOUND',
      ].includes(errnoCode)
    )
      return failure(NodeRangeDownloadCode.networkError);
    if (
      [
        'CERT_HAS_EXPIRED',
        'UNABLE_TO_VERIFY_LEAF_SIGNATURE',
        'SELF_SIGNED_CERT_IN_CHAIN',
        'ERR_TLS_CERT_ALTNAME_INVALID',
      ].includes(errnoCode)
    )
      return failure(NodeRangeDownloadCode.securityRejected);
    if (properties?.name === 'AbortError')
      return failure(NodeRangeDownloadCode.cancelled);
    return failure(NodeRangeDownloadCode.unknown);
  } catch {
    return failure(NodeRangeDownloadCode.unknown);
  }
}

function cancelled(signal?: AbortSignal): void {
  if (signal?.aborted) throw failure(NodeRangeDownloadCode.cancelled);
}

function waitForRetry(delay: number, signal?: AbortSignal): Promise<void> {
  cancelled(signal);
  return new Promise((resolve, reject) => {
    const timer: { id?: ReturnType<typeof setTimeout> } = {};
    const onAbort = () => {
      clearTimeout(timer.id);
      signal?.removeEventListener('abort', onAbort);
      reject(failure(NodeRangeDownloadCode.cancelled));
    };
    timer.id = setTimeout(
      () => {
        signal?.removeEventListener('abort', onAbort);
        resolve();
      },
      Math.min(Math.max(delay, 0), MAX_RETRY_DELAY_MS)
    );
    signal?.addEventListener('abort', onAbort, { once: true });
  });
}

function retryDelay(error: unknown, retry: number): number {
  return (
    (error instanceof DownloadHttpError || error instanceof RangeFallback
      ? error.retryAfter
      : null) ?? Math.min(500 * 2 ** retry, 10_000)
  );
}

function rejectedResponse(response: IResponse['response']): DownloadHttpError {
  const error = new DownloadHttpError(
    response.statusCode ?? 0,
    retryAfterMs(response.headers['retry-after'])
  );
  response.destroy();
  return error;
}

function waitForAbortable<T>(
  pending: Promise<T>,
  signal?: AbortSignal,
  release?: (result: T) => void
): Promise<T> {
  if (!signal) return pending;
  return new Promise((resolve, reject) => {
    const onAbort = () => reject(failure(NodeRangeDownloadCode.cancelled));
    signal.addEventListener('abort', onAbort, { once: true });
    if (signal.aborted) onAbort();
    void pending.then(
      (result) => {
        signal.removeEventListener('abort', onAbort);
        if (signal.aborted) {
          release?.(result);
        } else {
          resolve(result);
        }
      },
      (error) => {
        signal.removeEventListener('abort', onAbort);
        reject(error);
      }
    );
  });
}

async function* readResponse(
  response: IResponse['response'],
  signal?: AbortSignal
): AsyncGenerator<Buffer> {
  const iterator = response[Symbol.asyncIterator]();
  let complete = false;
  try {
    while (true) {
      cancelled(signal);
      const next = await waitForAbortable(iterator.next(), signal);
      if (next.done) {
        complete = true;
        return;
      }
      yield next.value;
    }
  } finally {
    if (!complete) {
      response.destroy();
      void iterator.return?.().catch(() => {});
    }
  }
}

function isHttps(url: string): boolean {
  try {
    return new URL(url).protocol === 'https:';
  } catch {
    return false;
  }
}

function contentRange(value: string | string[] | undefined) {
  const raw = Array.isArray(value) ? value[0] : value;
  const match = raw?.match(/^bytes\s+(\d+)-(\d+)\/(\d+)$/i);
  if (!match) return null;
  return {
    start: Number(match[1]),
    end: Number(match[2]),
    total: Number(match[3]),
  };
}

export function stripSensitiveHeaders(headers: Record<string, string>) {
  const allowed = new Set([
    'user-agent',
    'accept',
    'accept-encoding',
    'range',
    'if-range',
  ]);
  return Object.fromEntries(
    Object.entries(headers).filter(([name]) => allowed.has(name.toLowerCase()))
  );
}

async function get(
  url: string,
  headers: Record<string, string>,
  signal?: AbortSignal,
  redirects = 0,
  transport?: INodeDownloadOptions['transport']
): Promise<IResponse> {
  cancelled(signal);
  if (!isHttps(url)) throw failure(NodeRangeDownloadCode.invalidArgument);
  if (transport) {
    return waitForAbortable(
      transport(url, headers, signal),
      signal,
      ({ response }) => response.destroy()
    );
  }
  return new Promise((resolve, reject) => {
    const req = https.get(url, { headers, signal }, (response) => {
      const status = response.statusCode ?? 0;
      if ([301, 302, 303, 307, 308].includes(status)) {
        response.destroy();
        if (!response.headers.location || redirects >= MAX_REDIRECTS) {
          reject(failure(NodeRangeDownloadCode.securityRejected));
          return;
        }
        try {
          const next = new URL(response.headers.location, url);
          if (next.protocol !== 'https:') {
            reject(failure(NodeRangeDownloadCode.securityRejected));
            return;
          }
          const nextHeaders =
            next.origin === new URL(url).origin
              ? headers
              : stripSensitiveHeaders(headers);
          void get(
            next.href,
            nextHeaders,
            signal,
            redirects + 1,
            transport
          ).then(resolve, reject);
        } catch {
          reject(failure(NodeRangeDownloadCode.securityRejected));
        }
        return;
      }
      resolve({ response, url });
    });
    req.on('error', reject);
    req.setTimeout(STALL_MS, () =>
      req.destroy(failure(NodeRangeDownloadCode.timeout))
    );
  });
}

async function probe(opts: INodeDownloadOptions) {
  const { response, url } = await get(
    opts.url,
    { ...opts.headers, Range: 'bytes=0-0' },
    opts.signal,
    0,
    opts.transport
  );
  const status = response.statusCode ?? 0;
  const range = contentRange(response.headers['content-range']);
  let size = Number(response.headers['content-length']) || 0;
  if (status === 206) {
    size = range?.start === 0 && range.end === 0 ? range.total : 0;
  }
  if (status !== 200 && status !== 206) throw rejectedResponse(response);
  response.destroy();
  if (!Number.isSafeInteger(size) || size < 0)
    throw failure(NodeRangeDownloadCode.integrityFailed);
  if (
    size > MAX_ARCHIVE_BYTES ||
    (opts.expectedBytes && size && size !== opts.expectedBytes)
  )
    throw failure(NodeRangeDownloadCode.integrityFailed);
  return {
    url,
    size,
    etag: response.headers.etag ?? null,
    parallel: status === 206 && !!range && size >= MIN_PARALLEL_BYTES,
  };
}

function probeFailureIsTransient(error: unknown): boolean {
  return isRetryableRangeDownloadError(
    classifyNodeError(error)
  );
}

async function probeWithRetry(opts: INodeDownloadOptions) {
  for (let retry = 0; retry <= MAX_RETRIES; retry += 1) {
    try {
      return await probe(opts);
    } catch (error) {
      if (
        opts.signal?.aborted ||
        !probeFailureIsTransient(error) ||
        retry === MAX_RETRIES
      )
        throw error;
      await waitForRetry(retryDelay(error, retry), opts.signal);
    }
  }
  throw failure(NodeRangeDownloadCode.unknown);
}

function isStrongEtag(etag: string | null | undefined): etag is string {
  return Boolean(
    etag && !etag.trim().startsWith('W/') && !etag.trim().startsWith('w/')
  );
}

function isRetryableFsError(error: unknown): boolean {
  const code = (error as NodeJS.ErrnoException | undefined)?.code;
  return code === 'EPERM' || code === 'EACCES' || code === 'EBUSY';
}

function renameWithRetrySync(fromPath: string, toPath: string): void {
  for (let attempt = 0; attempt <= 3; attempt += 1) {
    try {
      fs.renameSync(fromPath, toPath);
      return;
    } catch (error) {
      if (!isRetryableFsError(error) || attempt === 3) {
        throw error;
      }
    }
  }
}

function checksumKey(opts: INodeDownloadOptions): string {
  return `${opts.expectedSha512 ?? ''}:${opts.expectedSha256 ?? ''}`;
}

async function verify(
  filePath: string,
  opts: INodeDownloadOptions
): Promise<boolean> {
  const algorithms = [
    ['sha512', opts.expectedSha512],
    ['sha256', opts.expectedSha256],
  ] as const;
  for (const [algorithm, expected] of algorithms) {
    if (expected) {
      let actual: string;
      try {
        actual = (await hashArchive(filePath, algorithm, opts.signal)).digest;
      } catch (error) {
        cancelled(opts.signal);
        if ((error as NodeJS.ErrnoException | undefined)?.code === 'ENOENT') {
          return false;
        }
        throw error;
      }
      const normalized = expected.trim().toLowerCase();
      const expectedHex = /^[0-9a-f]+$/.test(normalized)
        ? normalized
        : Buffer.from(expected.trim(), 'base64').toString('hex');
      if (actual !== expectedHex) return false;
    }
  }
  return true;
}

function discard(partial: string, manifestPath: string): void {
  fs.rmSync(manifestPath, { force: true });
  fs.rmSync(partial, { force: true });
}

function saveManifest(
  manifestPath: string,
  manifest: IManifest,
  fd?: number
): void {
  if (fd !== undefined) fs.fsyncSync(fd);
  const tempPath = `${manifestPath}.tmp`;
  fs.writeFileSync(tempPath, JSON.stringify(manifest));
  try {
    renameWithRetrySync(tempPath, manifestPath);
  } catch (error) {
    if (!isRetryableFsError(error)) throw error;
    fs.copyFileSync(tempPath, manifestPath);
    fs.rmSync(tempPath, { force: true });
  }
}

function readManifest(
  opts: INodeDownloadOptions,
  partial: string,
  manifestPath: string,
  size: number,
  etag: string | null,
  mode: IManifest['mode']
): IManifest | null {
  if (!fs.existsSync(partial) || !fs.existsSync(manifestPath)) return null;
  try {
    const manifest = JSON.parse(
      fs.readFileSync(manifestPath, 'utf8')
    ) as IManifest;
    const matching =
      (opts.identity
        ? manifest.identity === opts.identity
        : manifest.url === opts.url) &&
      (manifest.size === size ||
        (mode === 'single' &&
          size === 0 &&
          Number.isSafeInteger(manifest.size) &&
          manifest.size >= 0 &&
          manifest.size <= MAX_ARCHIVE_BYTES)) &&
      manifest.etag === (etag ?? manifest.etag) &&
      manifest.checksum === checksumKey(opts) &&
      manifest.mode === mode &&
      Array.isArray(manifest.parts);
    if (!matching) return null;
    if (
      manifest.parts.some(
        (p) =>
          !Number.isSafeInteger(p.start) ||
          !Number.isSafeInteger(p.end) ||
          !Number.isSafeInteger(p.done) ||
          p.start < 0 ||
          p.end < p.start ||
          p.done < 0 ||
          ((mode === 'parallel' || manifest.size > 0) &&
            p.done > p.end - p.start + 1)
      )
    )
      return null;
    const stat = fs.statSync(partial);
    if (mode === 'parallel' && stat.size !== size) return null;
    if (mode === 'single') {
      if (manifest.parts.length !== 1) return null;
      const done = manifest.parts[0]?.done ?? 0;
      if (stat.size < done) return null;
      if (stat.size > done) fs.truncateSync(partial, done);
    }
    if (manifest.url !== opts.url) {
      manifest.url = opts.url;
    }
    return manifest;
  } catch {
    return null;
  }
}

const progressRates = new WeakMap<
  IManifest,
  { lastTime: number; lastBytes: number; bytesPerSecond: number }
>();

function emit(
  opts: INodeDownloadOptions,
  manifest: IManifest,
  delta: number
): void {
  const transferred = manifest.parts.reduce((sum, part) => sum + part.done, 0);
  const now = Date.now();
  const prior = progressRates.get(manifest) ?? {
    lastTime: now,
    lastBytes: transferred,
    bytesPerSecond: 0,
  };
  const elapsed = now - prior.lastTime;
  if (elapsed >= 250 || (manifest.size > 0 && transferred === manifest.size)) {
    prior.bytesPerSecond = Math.max(
      0,
      Math.round(
        ((transferred - prior.lastBytes) * 1000) / Math.max(1, elapsed)
      )
    );
    prior.lastTime = now;
    prior.lastBytes = transferred;
  }
  progressRates.set(manifest, prior);
  opts.onProgress?.({
    transferred,
    total: manifest.size,
    delta,
    bytesPerSecond: prior.bytesPerSecond,
    percent: manifest.size ? (transferred / manifest.size) * 100 : 0,
  });
}

function retryAfterMs(value: string | string[] | undefined): number | null {
  const raw = (Array.isArray(value) ? value[0] : value)?.trim();
  if (!raw) return null;
  if (/^\d+$/.test(raw)) return Number(raw) * 1000;
  if (!/^[A-Za-z]{3,9},? /.test(raw)) return null;
  const date = Date.parse(raw);
  return Number.isFinite(date) ? Math.max(0, date - Date.now()) : null;
}

async function downloadPart(
  opts: INodeDownloadOptions,
  manifest: IManifest,
  part: IPart,
  fd: number,
  finalUrl: string,
  flush: () => void
): Promise<void> {
  for (let retry = 0; retry <= MAX_RETRIES; retry += 1) {
    cancelled(opts.signal);
    const start = part.start + part.done;
    if (start > part.end) return;
    try {
      const headers = {
        ...opts.headers,
        Range: `bytes=${start}-${part.end}`,
        ...(isStrongEtag(manifest.etag) ? { 'If-Range': manifest.etag } : {}),
      };
      // A cross-origin probe redirect must not forward source credentials.
      const safeHeaders =
        new URL(finalUrl).origin === new URL(opts.url).origin
          ? headers
          : stripSensitiveHeaders(headers);
      const { response } = await get(
        finalUrl,
        safeHeaders,
        opts.signal,
        0,
        opts.transport
      );
      const status = response.statusCode ?? 0;
      if (status !== 200 && status !== 206) throw rejectedResponse(response);
      const range = contentRange(response.headers['content-range']);
      if (
        status !== 206 ||
        !range ||
        range.start !== start ||
        range.end !== part.end ||
        range.total !== manifest.size ||
        (manifest.etag &&
          response.headers.etag &&
          response.headers.etag !== manifest.etag) ||
        String(response.headers['content-type'] ?? '').includes(
          'multipart/byteranges'
        )
      ) {
        response.destroy();
        throw new RangeFallback('Range response changed');
      }
      let sinceFlush = 0;
      for await (const chunk of readResponse(response, opts.signal)) {
        cancelled(opts.signal);
        const bytes = chunk as Buffer;
        if (bytes.length > part.end - part.start + 1 - part.done)
          throw new RangeFallback('Range body exceeds segment');
        fs.writeSync(fd, bytes, 0, bytes.length, part.start + part.done);
        part.done += bytes.length;
        sinceFlush += bytes.length;
        emit(opts, manifest, bytes.length);
        if (sinceFlush >= FLUSH_BYTES) {
          flush();
          sinceFlush = 0;
        }
      }
      flush();
      if (part.done === part.end - part.start + 1) return;
      throw failure(NodeRangeDownloadCode.networkError);
    } catch (error) {
      try {
        flush();
      } catch {
        /* ignore manifest flush errors during unwind */
      }
      if (error instanceof RangeFallback) throw error;
      const updateError = classifyNodeError(error);
      if (updateError.httpStatus === 416) {
        const retryAfter =
          updateError instanceof DownloadHttpError ? updateError.retryAfter : null;
        throw new RangeFallback('HTTP 416', true, retryAfter);
      }
      if (
        opts.signal?.aborted ||
        retry === MAX_RETRIES ||
        !isRetryableRangeDownloadError(updateError)
      )
        throw updateError;
      await waitForRetry(retryDelay(error, retry), opts.signal);
    }
  }
}

async function parallel(
  opts: INodeDownloadOptions,
  finalUrl: string,
  size: number,
  etag: string | null,
  partial: string,
  manifestPath: string
): Promise<void> {
  let manifest = readManifest(
    opts,
    partial,
    manifestPath,
    size,
    etag,
    'parallel'
  );
  if (!manifest) {
    discard(partial, manifestPath);
    const chunk = Math.ceil(size / SEGMENTS);
    const parts = Array.from({ length: SEGMENTS }, (_, index) => {
      const start = index * chunk;
      return { start, end: Math.min(start + chunk - 1, size - 1), done: 0 };
    }).filter((part) => part.start < size);
    const fd = fs.openSync(partial, 'w');
    fs.ftruncateSync(fd, size);
    fs.closeSync(fd);
    manifest = {
      identity: opts.identity,
      url: opts.url,
      size,
      etag,
      checksum: checksumKey(opts),
      mode: 'parallel',
      parts,
    };
    saveManifest(manifestPath, manifest);
  }
  cancelled(opts.signal);
  const fd = fs.openSync(partial, 'r+');
  const flush = () => saveManifest(manifestPath, manifest, fd);
  const controller = new AbortController();
  const onAbort = () => controller.abort();
  opts.signal?.addEventListener('abort', onAbort, { once: true });
  if (opts.signal?.aborted) controller.abort();
  const terminalHttpError: { current?: Error } = {};
  const rangeFallback: { current?: RangeFallback } = {};
  try {
    emit(opts, manifest, 0);
    const outcomes = await Promise.allSettled(
      manifest.parts.map(async (part) => {
        try {
          await downloadPart(
            { ...opts, signal: controller.signal },
            manifest,
            part,
            fd,
            finalUrl,
            flush
          );
        } catch (error) {
          if (
            error instanceof RangeFallback &&
            !error.refreshMetadata &&
            !rangeFallback.current
          ) {
            rangeFallback.current = error;
            controller.abort();
          }
          const updateError = classifyNodeError(error);
          if (
            updateError.code === NodeRangeDownloadCode.httpError &&
            !isRetryableRangeDownloadError(updateError) &&
            !terminalHttpError.current
          ) {
            terminalHttpError.current =
              error instanceof Error
                ? error
                : failure(NodeRangeDownloadCode.httpError, updateError.httpStatus);
            controller.abort();
          }
          throw error;
        }
      })
    );
    cancelled(opts.signal);
    if (terminalHttpError.current) throw terminalHttpError.current;
    if (rangeFallback.current) throw rangeFallback.current;
    const rejected =
      outcomes.find(
        (outcome) =>
          outcome.status === 'rejected' &&
          outcome.reason instanceof RangeFallback &&
          outcome.reason.refreshMetadata
      ) ?? outcomes.find((outcome) => outcome.status === 'rejected');
    if (rejected?.status === 'rejected') throw rejected.reason;
    flush();
  } finally {
    opts.signal?.removeEventListener('abort', onAbort);
    fs.closeSync(fd);
  }
}

async function single(
  opts: INodeDownloadOptions,
  info: { size: number; etag: string | null },
  partial: string,
  manifestPath: string
): Promise<number> {
  const { size, etag } = info;
  let manifest = readManifest(
    opts,
    partial,
    manifestPath,
    size,
    etag,
    'single'
  );
  if (!manifest) {
    discard(partial, manifestPath);
    manifest = {
      identity: opts.identity,
      url: opts.url,
      size,
      etag,
      checksum: checksumKey(opts),
      mode: 'single',
      parts: [{ start: 0, end: Math.max(0, size - 1), done: 0 }],
    };
    fs.closeSync(fs.openSync(partial, 'w'));
    saveManifest(manifestPath, manifest);
  }
  const part = manifest.parts[0];
  if (!part) throw failure(NodeRangeDownloadCode.integrityFailed);
  const offset = part.done;
  const knownSize = manifest.size || size;
  const knownEtag = manifest.etag ?? etag;
  if (knownSize && offset === knownSize) return knownSize;
  const headers = {
    ...opts.headers,
    ...(offset ? { Range: `bytes=${offset}-` } : {}),
    ...(offset && isStrongEtag(knownEtag) ? { 'If-Range': knownEtag } : {}),
  };
  let response: IResponse['response'];
  try {
    ({ response } = await get(opts.url, headers, opts.signal, 0, opts.transport));
    const status = response.statusCode ?? 0;
    if (status !== 200 && status !== 206) throw rejectedResponse(response);
  } catch (error) {
    if (offset && classifyNodeError(error).httpStatus === 416) {
      discard(partial, manifestPath);
    }
    throw error;
  }
  const status = response.statusCode;
  const responseEtag = response.headers.etag;
  const contentLength = Number(response.headers['content-length'] ?? 0);
  if (status === 206) {
    const range = contentRange(response.headers['content-range']);
    if (
      !range ||
      range.start !== offset ||
      range.end !== range.total - 1 ||
      range.end < range.start ||
      (knownSize && range.total !== knownSize) ||
      (knownEtag && responseEtag && responseEtag !== knownEtag) ||
      (contentLength && contentLength !== range.end - range.start + 1) ||
      String(response.headers['content-type'] ?? '').includes(
        'multipart/byteranges'
      )
    ) {
      response.destroy();
      discard(partial, manifestPath);
      throw failure(NodeRangeDownloadCode.integrityFailed);
    }
    manifest.size = range.total;
    manifest.etag = responseEtag ?? knownEtag;
  } else {
    manifest.size = contentLength;
    manifest.etag = responseEtag ?? null;
  }
  part.end = Math.max(0, manifest.size - 1);
  if (
    !Number.isSafeInteger(contentLength) ||
    contentLength < 0 ||
    manifest.size > MAX_ARCHIVE_BYTES ||
    (opts.expectedBytes &&
      manifest.size &&
      manifest.size !== opts.expectedBytes)
  ) {
    response.destroy();
    throw failure(NodeRangeDownloadCode.integrityFailed);
  }
  const fd = fs.openSync(partial, 'r+');
  const flush = () => saveManifest(manifestPath, manifest, fd);
  let sinceFlush = 0;
  try {
    if (status === 200 && offset) {
      fs.ftruncateSync(fd, 0);
      part.done = 0;
    }
    info.size = manifest.size;
    info.etag = manifest.etag;
    emit(opts, manifest, 0);
    for await (const chunk of readResponse(response, opts.signal)) {
      cancelled(opts.signal);
      const bytes = chunk as Buffer;
      if (
        (manifest.size && part.done + bytes.length > manifest.size) ||
        part.done + bytes.length > MAX_ARCHIVE_BYTES ||
        (opts.expectedBytes && part.done + bytes.length > opts.expectedBytes)
      )
        throw failure(NodeRangeDownloadCode.integrityFailed);
      fs.writeSync(fd, bytes, 0, bytes.length, part.done);
      part.done += bytes.length;
      sinceFlush += bytes.length;
      emit(opts, manifest, bytes.length);
      if (sinceFlush >= FLUSH_BYTES) {
        flush();
        sinceFlush = 0;
      }
    }
    flush();
    if (manifest.size && part.done !== manifest.size)
      throw failure(NodeRangeDownloadCode.networkError);
    if (opts.expectedBytes && part.done !== opts.expectedBytes)
      throw failure(NodeRangeDownloadCode.integrityFailed);
    return part.done;
  } finally {
    try {
      flush();
    } catch {
      /* ignore manifest flush errors during unwind */
    }
    fs.closeSync(fd);
  }
}

async function singleWithRetry(
  opts: INodeDownloadOptions,
  size: number,
  etag: string | null,
  partial: string,
  manifestPath: string
): Promise<number> {
  let info = { size, etag };
  for (let retry = 0; retry <= MAX_RETRIES; retry += 1) {
    try {
      return await single(opts, info, partial, manifestPath);
    } catch (error) {
      const updateError = classifyNodeError(error);
      if (
        opts.signal?.aborted ||
        retry === MAX_RETRIES ||
        !isRetryableRangeDownloadError(updateError)
      )
        throw classifyNodeError(error);
      await waitForRetry(retryDelay(error, retry), opts.signal);
      if (updateError.httpStatus === 416) {
        info = await probeWithRetry(opts);
      }
    }
  }
  throw failure(NodeRangeDownloadCode.unknown);
}

async function parallelWithRecovery(
  opts: INodeDownloadOptions,
  initialInfo: Awaited<ReturnType<typeof probe>>,
  partial: string,
  manifestPath: string
): Promise<number> {
  let info = initialInfo;
  for (let retry = 0; retry <= MAX_RETRIES; retry += 1) {
    try {
      await parallel(
        opts,
        info.url,
        info.size,
        info.etag,
        partial,
        manifestPath
      );
      return info.size;
    } catch (error) {
      if (!(error instanceof RangeFallback)) throw error;
      if (error.refreshMetadata) {
        cancelled(opts.signal);
        if (retry === MAX_RETRIES) throw error;
        await waitForRetry(retryDelay(error, retry), opts.signal);
        info = await probeWithRetry(opts);
        // Matching metadata preserves all completed segments on the next run.
      } else {
        discard(partial, manifestPath);
      }
      if (!error.refreshMetadata || !info.parallel) {
        return await singleWithRetry(
          opts,
          info.size,
          info.etag,
          partial,
          manifestPath
        );
      }
    }
  }
  throw failure(NodeRangeDownloadCode.unknown);
}

async function downloadNodeFileInternal(
  input: INodeDownloadOptions
): Promise<{ filePath: string; totalBytes: number }> {
  let highestPercent = 0;
  const opts: INodeDownloadOptions = {
    ...input,
    onProgress: (progress) => {
      highestPercent = Math.max(highestPercent, progress.percent);
      input.onProgress?.({ ...progress, percent: highestPercent });
    },
  };
  cancelled(opts.signal);
  if (!opts.identity || !isHttps(opts.url))
    throw failure(NodeRangeDownloadCode.invalidArgument);
  if (!opts.expectedSha512 && !opts.expectedSha256)
    throw failure(NodeRangeDownloadCode.invalidArgument);
  if (
    opts.expectedBytes !== undefined &&
    (!Number.isSafeInteger(opts.expectedBytes) ||
      opts.expectedBytes <= 0 ||
      opts.expectedBytes > MAX_ARCHIVE_BYTES)
  )
    throw failure(NodeRangeDownloadCode.invalidArgument);
  fs.mkdirSync(path.dirname(opts.targetPath), { recursive: true });
  const partial = `${opts.targetPath}.partial`;
  const manifestPath = `${partial}.progress.json`;
  if (fs.existsSync(opts.targetPath)) {
    if (
      fs.statSync(opts.targetPath).size <= MAX_ARCHIVE_BYTES &&
      (!opts.expectedBytes ||
        fs.statSync(opts.targetPath).size === opts.expectedBytes) &&
      (await verify(opts.targetPath, opts))
    ) {
      cancelled(opts.signal);
      discard(partial, manifestPath);
      return {
        filePath: opts.targetPath,
        totalBytes: fs.statSync(opts.targetPath).size,
      };
    }
    fs.rmSync(opts.targetPath, { force: true });
  }
  let info: Awaited<ReturnType<typeof probe>>;
  try {
    info = await probeWithRetry(opts);
  } catch (error) {
    if (
      opts.signal?.aborted ||
      !probeFailureIsTransient(error) ||
      fs.existsSync(partial)
    )
      throw error;
    if (error instanceof DownloadHttpError && error.retryAfter !== null) {
      await waitForRetry(error.retryAfter, opts.signal);
    }
    info = { url: opts.url, size: 0, etag: null, parallel: false };
  }
  let size: number;
  if (
    info.parallel &&
    !readManifest(opts, partial, manifestPath, info.size, info.etag, 'single')
  ) {
    size = await parallelWithRecovery(opts, info, partial, manifestPath);
  } else {
    size = await singleWithRetry(
      opts,
      info.size,
      info.etag,
      partial,
      manifestPath
    );
  }
  cancelled(opts.signal);
  if (
    size > MAX_ARCHIVE_BYTES ||
    (opts.expectedBytes && size !== opts.expectedBytes)
  ) {
    throw failure(NodeRangeDownloadCode.integrityFailed);
  }
  if (!(await verify(partial, opts))) {
    discard(partial, manifestPath);
    throw failure(NodeRangeDownloadCode.integrityFailed);
  }
  cancelled(opts.signal);
  renameWithRetrySync(partial, opts.targetPath);
  fs.rmSync(manifestPath, { force: true });
  return { filePath: opts.targetPath, totalBytes: size };
}

export async function downloadNodeFile(
  input: INodeDownloadOptions
): Promise<{ filePath: string; totalBytes: number }> {
  const controller = new AbortController();
  const onAbort = () => controller.abort(failure(NodeRangeDownloadCode.cancelled));
  const timer = setTimeout(
    () => controller.abort(failure(NodeRangeDownloadCode.timeout)),
    DOWNLOAD_DEADLINE_MS
  );
  input.signal?.addEventListener('abort', onAbort, { once: true });
  if (input.signal?.aborted) onAbort();
  try {
    return await downloadNodeFileInternal({
      ...input,
      signal: controller.signal,
    });
  } catch (error) {
    throw classifyNodeError(
      controller.signal.aborted ? controller.signal.reason : error
    );
  } finally {
    clearTimeout(timer);
    input.signal?.removeEventListener('abort', onAbort);
  }
}
