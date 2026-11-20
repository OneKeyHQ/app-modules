import * as fs from 'fs';
import * as fsPromises from 'fs/promises';
import type { ClientRequest } from 'http';
import https, { type RequestOptions } from 'https';
import * as nodePath from 'path';
import { fileURLToPath } from 'url';

const MAX_REQUEST_ID_BYTES = 128;
const MAX_TIMEOUT_MS = 120_000;
const MAX_PATH_BYTES = 8 * 1024;
const MAX_REQUEST_BODY_BYTES = 1024 * 1024;
const MAX_UPLOAD_FILE_BYTES = 20 * 1024 * 1024;
const MAX_RESPONSE_BODY_BYTES = 10 * 1024 * 1024;
const MAX_HEADER_COUNT = 64;
const MAX_HEADER_NAME_BYTES = 128;
const MAX_HEADER_VALUE_BYTES = 8 * 1024;
const MAX_TOTAL_HEADER_BYTES = 32 * 1024;
const MAX_TOTAL_SOCKETS = 64;
const MAX_SOCKETS_PER_PAIR = 16;
const MAX_FREE_SOCKETS = 32;
const MAX_ACTIVE_REQUESTS = 64;
const MAX_ACTIVE_REQUESTS_PER_PAIR = 16;
const MAX_PENDING_REQUESTS = 256;
const MAX_DIAGNOSTIC_TEXT_LENGTH = 120;

const UPLOAD_FILE_METHODS = new Set(['POST', 'PUT']);
export const DEFAULT_LOGGER_UPLOAD_PATH = '/wallet/v1/client/log';
export const DEFAULT_LOGGER_UPLOAD_ORIGINS = new Set([
  'https://wallet.onekeycn.com',
  'https://wallet.onekeytest.com',
]);

const ALLOWED_METHODS = new Set([
  'GET',
  'POST',
  'PUT',
  'PATCH',
  'DELETE',
  'HEAD',
  'OPTIONS',
]);

const MODULE_OWNED_HEADERS = new Set([
  'host',
  'content-length',
  'accept-encoding',
  'x-emascurl-config-id',
]);

const UNSAFE_HEADERS = new Set([
  'connection',
  'keep-alive',
  'te',
  'trailer',
  'transfer-encoding',
  'upgrade',
  'expect',
]);

const HOSTNAME_RE =
  /^(?=.{1,253}$)([A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/;
const SCHEME_RE = /^[A-Za-z][A-Za-z0-9+.-]*:/;
const HEADER_TOKEN_RE = /^[!#$%&'*+.^_`|~0-9A-Za-z-]+$/;
const IPV4_DOTTED_RE = /^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/;
const HEX_GROUP_RE = /^[0-9A-Fa-f]{1,4}$/;

const IPV4_LOG_RE =
  /(^|[^\d.])((?:25[0-5]|2[0-4]\d|1?\d?\d)(?:\.(?:25[0-5]|2[0-4]\d|1?\d?\d)){3})(?=$|[^\d.])/g;
const IPV4_LITERAL_LOG_RE =
  /^(?:25[0-5]|2[0-4]\d|1?\d?\d)(?:\.(?:25[0-5]|2[0-4]\d|1?\d?\d)){3}$/;
// An IPv6 literal with an embedded IPv4 address is at most 45 characters.
// Bound both scans before validating candidate addresses for log redaction.
const BRACKETED_IPV6_LOG_RE =
  /\[([0-9A-Fa-f:.]{1,45})\](?=$|:\d{1,5}\b|[^0-9A-Fa-f:.\]])/g;
const IPV6_CANDIDATE_LOG_RE =
  /(^|[^0-9A-Fa-f:.[])(\[?[0-9A-Fa-f:.]{1,45}\]?)(?=$|[^0-9A-Fa-f:.\]])/g;

const SNI_FAIL_CLOSED_ERROR_CODES = new Set([
  'SNI_INVALID_CONFIG',
  'SNI_SECURITY_POLICY_FAILED',
  'SNI_TLS_FAILED',
  'SNI_CERT_FAILED',
  'SNI_RESPONSE_FAILED',
  'SNI_RESOURCE_LIMIT',
  'SNI_CANCELLED',
]);

const SNI_FAIL_CLOSED_MESSAGE_RE =
  /\bSNI_(INVALID_CONFIG|SECURITY_POLICY_FAILED|TLS_FAILED|CERT_FAILED|RESPONSE_FAILED|RESOURCE_LIMIT|CANCELLED)\b/;

export interface SniNodeRequestConfig {
  requestId?: string;
  ip: string;
  hostname: string;
  path: string;
  headers: Record<string, string>;
  method: string;
  body: string | null;
  timeout: number;
  port?: number | null;
}

export interface SniNodeUploadFileConfig {
  requestId?: string;
  ip: string;
  hostname: string;
  path: string;
  headers: Record<string, string>;
  method: 'POST' | 'PUT';
  filePath: string;
  timeout: number;
}

export interface SniNodeResponse {
  data: string;
  status: number;
  statusText: string;
  statusCode: number;
  headers: Record<string, string>;
  multiValueHeaders?: Record<string, string[]>;
  body: string;
}

export interface SniNodeDebugSnapshot {
  activeRequests: number;
  activeRequestsForPair: number;
  pendingRequests: number;
  pendingRequestsForPair: number;
}

export interface SniNodeLogSink {
  info?: (message: string) => void;
  warn?: (message: string) => void;
  error?: (message: string) => void;
}

export interface SniNodeUploadProgress {
  bytesSent: number;
  totalBytes: number;
}

export interface SniNodeUploadProgressEvent extends SniNodeUploadProgress {
  requestId: string;
  progressPercent: number;
}

export interface SniNodeUploadValidationOptions {
  allowedUploadOrigins?: ReadonlySet<string> | readonly string[];
  allowedUploadPath?: string | null;
  requiredFileExtension?: string | null;
  requireDirectChildOfRoot?: boolean;
  getAllowedUploadRoots?: () => Promise<readonly string[]> | readonly string[];
  verifyUploadFile?: (
    realPath: string,
    sizeBytes: number
  ) => Promise<boolean> | boolean;
}

export interface SniNodeClientOptions extends SniNodeUploadValidationOptions {
  logger?: SniNodeLogSink;
  logPrefix?: string;
  resolveProxy?: (url: string) => Promise<string>;
  onUploadProgress?: (event: SniNodeUploadProgressEvent) => void;
}

export type NormalizedSniRequestConfig = Omit<
  SniNodeRequestConfig,
  'body' | 'headers' | 'method' | 'path' | 'port'
> & {
  body: string | null;
  // Set only by uploadFile(); the file is streamed instead of `body`.
  bodyFile?: { filePath: string; sizeBytes: number };
  headers: Record<string, string>;
  method: string;
  path: string;
};

export type SniAgentState = {
  agent: https.Agent;
  activeRequests: Set<ClientRequest>;
  destroyed: boolean;
};

type SniRequestLimiterWaiter = {
  key: string;
  signal: AbortSignal | undefined;
  abortListener: (() => void) | undefined;
  resolve: (release: () => void) => void;
};

type SniRequestState = {
  requestId: string | undefined;
  settled: boolean;
  uploadProgress: SniNodeUploadProgress | undefined;
  signal: AbortSignal;
  checkDeadline: () => void;
  clearDeadline: () => void;
  cancel: (error: SniRequestError) => void;
};

export class SniRequestError extends Error {
  code: string;
  failClosed: boolean;

  constructor(code: string, message: string, failClosed = false) {
    super(`${code}: ${message}`);
    this.name = 'SniRequestError';
    this.code = code;
    this.failClosed = failClosed;
  }
}

export function isSniFailClosedError(error: unknown): boolean {
  const code =
    error && typeof error === 'object' && 'code' in error
      ? String((error as { code?: unknown }).code)
      : '';
  if (SNI_FAIL_CLOSED_ERROR_CODES.has(code)) {
    return true;
  }

  const message = error instanceof Error ? error.message : String(error);
  return SNI_FAIL_CLOSED_MESSAGE_RE.test(message);
}

function isLikelyIpv6Literal(candidate: string): boolean {
  const value =
    candidate.startsWith('[') && candidate.endsWith(']')
      ? candidate.slice(1, -1)
      : candidate;
  if (!value.includes(':')) return false;
  const parts = value.split(':');
  const doubleColonCount = value.split('::').length - 1;

  if (
    doubleColonCount > 1 ||
    value.includes(':::') ||
    !/^[0-9A-Fa-f:.]+$/.test(value)
  ) {
    return false;
  }

  const hasDoubleColon = value.includes('::');
  const hasEmbeddedIpv4 = value.includes('.');
  const groupCount = parts.reduce((count, part, index) => {
    if (part === '') return count;
    if (part.includes('.')) {
      return index === parts.length - 1 && IPV4_LITERAL_LOG_RE.test(part)
        ? count + 2
        : Number.POSITIVE_INFINITY;
    }
    return HEX_GROUP_RE.test(part) ? count + 1 : Number.POSITIVE_INFINITY;
  }, 0);

  if (!Number.isFinite(groupCount)) return false;
  const lastPart = parts[parts.length - 1] ?? '';
  if (hasEmbeddedIpv4 && !IPV4_LITERAL_LOG_RE.test(lastPart)) {
    return false;
  }
  return hasDoubleColon ? groupCount < 8 : groupCount === 8;
}

export function redactIpLiterals(value: string): string {
  return value
    .replace(BRACKETED_IPV6_LOG_RE, (match, candidate: string) =>
      isLikelyIpv6Literal(candidate) ? '<ip6>' : match
    )
    .replace(
      IPV6_CANDIDATE_LOG_RE,
      (_match, prefix: string, candidate: string) =>
        isLikelyIpv6Literal(candidate)
          ? `${prefix}<ip6>`
          : `${prefix}${candidate}`
    )
    .replace(IPV4_LOG_RE, '$1<ip>');
}

export function sanitizeSniDiagnosticText(
  value: string,
  maxLength = MAX_DIAGNOSTIC_TEXT_LENGTH
): string {
  // eslint-disable-next-line no-control-regex
  const singleLine = value.replace(/[\u0000-\u001f\u007f]+/g, ' ').trim();
  const redacted = redactIpLiterals(singleLine);
  return redacted.length > maxLength ? redacted.slice(0, maxLength) : redacted;
}

export function safeSniLogValue(value: unknown): string {
  if (value === null || value === undefined) return 'none';
  return redactIpLiterals(String(value)).replace(/[\r\n\s]+/g, '_');
}

export function formatNativeLogEvent(
  event: string,
  fields: Record<string, unknown>
): string {
  return Object.entries({ event, ...fields })
    .map(([key, value]) => {
      const logValue =
        key === 'requestId' || key === 'ip' || key === 'requestedHost'
          ? String(value ?? 'none').replace(/[\r\n\s]+/g, '_')
          : safeSniLogValue(value);
      // Keep long diagnostic IDs recoverable without matching the logger's private-key filter.
      let displayedValue = logValue;
      let formatSuffix = '';
      if (
        key === 'requestId' &&
        typeof value === 'string' &&
        (value !== logValue || /[0-9a-fA-F]{64}/.test(value))
      ) {
        displayedValue = encodeURIComponent(
          Buffer.from(value, 'utf8').toString('utf8')
        )
          .replace(
            /[!'()*~-]/g,
            (character) =>
              `%${character.charCodeAt(0).toString(16).toUpperCase()}`
          )
          .replace(/([0-9a-fA-F]{32})(?=[0-9a-fA-F]{32})/g, '$1-');
        formatSuffix = ' requestIdFormat=uri-hex32';
      }
      return `${key}=${displayedValue}${formatSuffix}`;
    })
    .join(' ');
}

function emitNativeLog(
  logger: SniNodeLogSink | undefined,
  logPrefix: string,
  level: 'info' | 'warn' | 'error',
  event: string,
  fields: Record<string, unknown>
): void {
  if (!logger) return;
  const message = `${logPrefix} ${formatNativeLogEvent(event, fields)}`;
  if (level === 'error') {
    logger.error?.(message);
  } else if (level === 'warn') {
    logger.warn?.(message);
  } else {
    logger.info?.(message);
  }
}

function getErrorCode(error: unknown): string | undefined {
  if (error && typeof error === 'object' && 'code' in error) {
    const code = (error as { code?: unknown }).code;
    if (typeof code === 'string') return code;
  }
  return undefined;
}

function getErrorMessage(error: unknown): string {
  return error instanceof Error ? error.message : String(error);
}

function getIpFamily(ip: string): string {
  return ip.includes(':') ? 'ipv6' : 'ipv4';
}

/* eslint-disable no-bitwise */
function parseIpv4Octets(ip: string): [number, number, number, number] | null {
  const match = IPV4_DOTTED_RE.exec(ip);
  if (!match) return null;
  const rawOctets = [match[1], match[2], match[3], match[4]];
  const octets: number[] = [];
  for (const part of rawOctets) {
    if (!part || (part.length > 1 && part.startsWith('0'))) {
      return null;
    }
    const value = Number(part);
    if (!Number.isInteger(value) || value < 0 || value > 255) {
      return null;
    }
    octets.push(value);
  }
  return [octets[0] ?? 0, octets[1] ?? 0, octets[2] ?? 0, octets[3] ?? 0];
}

function parseHexGroups(groups: readonly string[]): number[] | null {
  const result: number[] = [];
  for (const group of groups) {
    if (!HEX_GROUP_RE.test(group)) {
      return null;
    }
    result.push(parseInt(group, 16));
  }
  return result;
}

function parseIpv6Parts(ip: string): number[] | null {
  if (
    !ip ||
    !ip.includes(':') ||
    ip.includes('[') ||
    ip.includes(']') ||
    ip.includes('%') ||
    ip.trim() !== ip
  ) {
    return null;
  }

  let normalizedIp = ip;
  if (normalizedIp.includes('.')) {
    const lastColon = normalizedIp.lastIndexOf(':');
    if (lastColon < 0) return null;
    const ipv4Octets = parseIpv4Octets(normalizedIp.slice(lastColon + 1));
    if (!ipv4Octets) return null;
    const high = ((ipv4Octets[0] << 8) | ipv4Octets[1]).toString(16);
    const low = ((ipv4Octets[2] << 8) | ipv4Octets[3]).toString(16);
    normalizedIp = `${normalizedIp.slice(0, lastColon + 1)}${high}:${low}`;
  }

  const doubleColonSides = normalizedIp.split('::');
  if (doubleColonSides.length > 2) {
    return null;
  }

  if (doubleColonSides.length === 1) {
    const groups = normalizedIp.split(':');
    if (groups.length !== 8) return null;
    return parseHexGroups(groups);
  }

  const leftRaw = doubleColonSides[0] ?? '';
  const rightRaw = doubleColonSides[1] ?? '';
  const leftGroups = leftRaw === '' ? [] : leftRaw.split(':');
  const rightGroups = rightRaw === '' ? [] : rightRaw.split(':');
  if (leftGroups.length + rightGroups.length >= 8) {
    return null;
  }
  const leftNums = parseHexGroups(leftGroups);
  const rightNums = parseHexGroups(rightGroups);
  if (!leftNums || !rightNums) {
    return null;
  }
  const zeroFill = new Array<number>(
    8 - leftNums.length - rightNums.length
  ).fill(0);
  return [...leftNums, ...zeroFill, ...rightNums];
}

function isIpv4MappedParts(parts: readonly number[]): boolean {
  return (
    parts.length === 8 &&
    parts[0] === 0 &&
    parts[1] === 0 &&
    parts[2] === 0 &&
    parts[3] === 0 &&
    parts[4] === 0 &&
    parts[5] === 0xffff
  );
}

function isIpLiteral(value: string): boolean {
  return parseIpv4Octets(value) !== null || parseIpv6Parts(value) !== null;
}

export function canonicalizeSniDestinationIp(ip: string): string {
  const ipv4 = parseIpv4Octets(ip);
  if (ipv4) {
    return `${ipv4[0]}.${ipv4[1]}.${ipv4[2]}.${ipv4[3]}`;
  }
  const ipv6Parts = parseIpv6Parts(ip);
  if (!ipv6Parts) {
    return ip;
  }
  if (isIpv4MappedParts(ipv6Parts)) {
    const part6 = ipv6Parts[6] ?? 0;
    const part7 = ipv6Parts[7] ?? 0;
    return `${(part6 >> 8) & 0xff}.${part6 & 0xff}.${(part7 >> 8) & 0xff}.${
      part7 & 0xff
    }`;
  }
  return ipv6Parts.map((part) => part.toString(16)).join(':');
}

export function getProxyRuleType(
  proxyRules: string
): 'direct' | 'proxy' | 'mixed' | 'unknown' {
  const rules = proxyRules
    .split(';')
    .map((rule) => rule.trim())
    .filter(Boolean)
    .map((rule) => rule.toUpperCase());
  if (rules.length === 0) return 'unknown';
  const directCount = rules.filter((rule) => rule === 'DIRECT').length;
  if (directCount === rules.length) return 'direct';
  if (directCount === 0) return 'proxy';
  return 'mixed';
}

function getHostnameForLog(url: string): string {
  try {
    return new URL(url).hostname;
  } catch {
    return 'unknown';
  }
}

function invalidConfig(message: string): never {
  throw new SniRequestError('SNI_INVALID_CONFIG', message, true);
}

export function classifyTransportError(error: Error, isUpload = false): Error {
  if (error instanceof SniRequestError) {
    return error;
  }

  const code =
    error && typeof error === 'object' && 'code' in error
      ? String((error as { code?: unknown }).code)
      : '';
  const message = `${code} ${error.message}`;
  if (
    /CERT|certificate|altname|hostname|self[- ]?signed|unable to verify|expired/i.test(
      message
    )
  ) {
    return new SniRequestError('SNI_CERT_FAILED', error.message, true);
  }
  if (/TLS|SSL|handshake|EPROTO/i.test(message)) {
    return new SniRequestError('SNI_TLS_FAILED', error.message, true);
  }
  if (isUpload) {
    if (/\bECONNREFUSED\b/i.test(message)) {
      return new SniRequestError('SNI_CONNECTION_REFUSED', error.message);
    }
    if (/\b(?:ENOTFOUND|EAI_AGAIN)\b/i.test(message)) {
      return new SniRequestError('SNI_DNS_FAILED', error.message);
    }
    if (/\b(?:ENETUNREACH|EHOSTUNREACH)\b/i.test(message)) {
      return new SniRequestError('SNI_NETWORK_UNREACHABLE', error.message);
    }
  }
  return error;
}

function byteSize(value: string): number {
  return Buffer.byteLength(value, 'utf8');
}

function hasControlChar(value: string): boolean {
  for (let index = 0; index < value.length; index += 1) {
    const code = value.charCodeAt(index);
    if (code <= 0x1f || code === 0x7f) return true;
  }
  return false;
}

function normalizeMethod(method: string): string {
  if (hasControlChar(method)) {
    invalidConfig(`Invalid method: ${method}`);
  }
  const normalized = method.trim().toUpperCase();
  if (!ALLOWED_METHODS.has(normalized)) {
    invalidConfig(`Invalid method: ${method}`);
  }
  return normalized;
}

function normalizePath(path: string): string {
  const trimmed = path.trim();
  if (byteSize(trimmed) > MAX_PATH_BYTES) {
    invalidConfig('Path too large');
  }
  if (
    hasControlChar(trimmed) ||
    trimmed.startsWith('//') ||
    trimmed.includes('://') ||
    SCHEME_RE.test(trimmed.split('/')[0] ?? '')
  ) {
    invalidConfig('Invalid path');
  }
  if (!trimmed) return '/';
  return trimmed.startsWith('/') ? trimmed : `/${trimmed}`;
}

function validateHostname(hostname: string): void {
  if (
    !HOSTNAME_RE.test(hostname) ||
    /^\d+\.\d+\.\d+\.\d+$/.test(hostname) ||
    isIpLiteral(hostname)
  ) {
    invalidConfig(`Invalid hostname: ${hostname}`);
  }
}

function validateRequestId(requestId: string | undefined): void {
  if (requestId === undefined) return;
  if (
    !requestId ||
    hasControlChar(requestId) ||
    byteSize(requestId) > MAX_REQUEST_ID_BYTES
  ) {
    invalidConfig('Invalid requestId');
  }
}

function validateTimeout(timeout: number): void {
  if (!Number.isFinite(timeout) || timeout < 1 || timeout > MAX_TIMEOUT_MS) {
    invalidConfig(`Invalid timeout: ${timeout}`);
  }
}

function validateBody(body: string | null): void {
  if (body !== null) {
    if (typeof body !== 'string') invalidConfig('Invalid request body');
    if (byteSize(body) > MAX_REQUEST_BODY_BYTES) {
      invalidConfig('Request body too large');
    }
  }
}

function getBodyBytes(config: NormalizedSniRequestConfig): number {
  if (config.bodyFile) return config.bodyFile.sizeBytes;
  return config.body ? byteSize(config.body) : 0;
}

function isPathInside(root: string, target: string): boolean {
  const relative = nodePath.relative(root, target);
  return (
    !!relative && !relative.startsWith('..') && !nodePath.isAbsolute(relative)
  );
}

async function resolveAllowedUploadRoots(
  getAllowedUploadRoots?: () => Promise<readonly string[]> | readonly string[],
  signal?: AbortSignal
): Promise<string[]> {
  if (!getAllowedUploadRoots) {
    return [];
  }
  let candidates: readonly string[] = [];
  try {
    candidates = await getAllowedUploadRoots();
  } catch {
    return [];
  }
  signal?.throwIfAborted();
  const roots = await Promise.all(
    candidates.map(async (candidate) => {
      try {
        return await fsPromises.realpath(candidate);
      } catch {
        return undefined;
      }
    })
  );
  return roots.filter((root): root is string => !!root);
}

async function resolveUploadFile(
  filePath: unknown,
  options?: SniNodeUploadValidationOptions,
  signal?: AbortSignal
): Promise<{ filePath: string; sizeBytes: number }> {
  if (typeof filePath !== 'string' || !filePath || hasControlChar(filePath)) {
    invalidConfig('Invalid filePath');
  }
  let absolutePath = filePath;
  if (filePath.startsWith('file://')) {
    try {
      absolutePath = fileURLToPath(filePath);
    } catch {
      invalidConfig('Invalid filePath');
    }
  }
  if (!nodePath.isAbsolute(absolutePath)) {
    invalidConfig('filePath must be absolute');
  }

  let realPath: string;
  try {
    realPath = await fsPromises.realpath(nodePath.resolve(absolutePath));
  } catch {
    invalidConfig('Upload file is not readable');
  }

  signal?.throwIfAborted();
  const requiredExtension =
    options?.requiredFileExtension === undefined
      ? '.zip'
      : options.requiredFileExtension;
  const requireDirectChild = options?.requireDirectChildOfRoot ?? true;
  const roots = await resolveAllowedUploadRoots(
    options?.getAllowedUploadRoots,
    signal
  );
  signal?.throwIfAborted();

  const hasValidExtension =
    !requiredExtension || realPath.endsWith(requiredExtension);
  const isInsideAllowedRoot = roots.some((root) =>
    requireDirectChild
      ? nodePath.dirname(realPath) === root && isPathInside(root, realPath)
      : isPathInside(root, realPath)
  );

  if (!hasValidExtension || !isInsideAllowedRoot) {
    invalidConfig('Upload file is outside app storage');
  }

  let stat: fs.Stats;
  try {
    stat = await fsPromises.stat(realPath);
    await fsPromises.access(realPath, fs.constants.R_OK);
  } catch {
    invalidConfig('Upload file is not readable');
  }
  if (!stat.isFile()) {
    invalidConfig('Upload file is not a regular file');
  }
  if (stat.size < 1 || stat.size > MAX_UPLOAD_FILE_BYTES) {
    invalidConfig(`Invalid upload file size: ${stat.size}`);
  }
  signal?.throwIfAborted();
  if (options?.verifyUploadFile) {
    let verified: boolean;
    try {
      verified = await options.verifyUploadFile(realPath, stat.size);
    } catch {
      invalidConfig('Upload file verification failed');
    }
    signal?.throwIfAborted();
    if (!verified) {
      invalidConfig('Upload file was not collected by the main process');
    }
  }
  return { filePath: realPath, sizeBytes: stat.size };
}

export async function validateSniUploadFileConfig(
  config: SniNodeUploadFileConfig,
  options?: SniNodeUploadValidationOptions,
  signal?: AbortSignal
): Promise<NormalizedSniRequestConfig> {
  if ((config as { body?: unknown }).body !== undefined) {
    invalidConfig('uploadFile does not accept a body');
  }
  const method =
    typeof config.method === 'string' ? config.method.trim().toUpperCase() : '';
  if (!UPLOAD_FILE_METHODS.has(method)) {
    invalidConfig(`Invalid upload method: ${String(config.method)}`);
  }
  const normalized = normalizeSniRequestConfig({
    requestId: config.requestId,
    ip: config.ip,
    hostname: config.hostname,
    path: config.path,
    headers: config.headers,
    method,
    body: null,
    timeout: config.timeout,
  });

  const allowedOriginsInput =
    options?.allowedUploadOrigins === undefined
      ? DEFAULT_LOGGER_UPLOAD_ORIGINS
      : options.allowedUploadOrigins;
  const allowedOrigins =
    allowedOriginsInput instanceof Set
      ? allowedOriginsInput
      : new Set(allowedOriginsInput);
  const allowedPath =
    options?.allowedUploadPath === undefined
      ? DEFAULT_LOGGER_UPLOAD_PATH
      : options.allowedUploadPath;

  if (
    !allowedOrigins.has(`https://${normalized.hostname.toLowerCase()}`) ||
    (allowedPath !== null && normalized.path !== allowedPath)
  ) {
    invalidConfig('Upload destination is not allowed');
  }
  return {
    ...normalized,
    bodyFile: await resolveUploadFile(config.filePath, options, signal),
  };
}

function ipv6BytesFromParts(parts: readonly number[]): number[] {
  return parts.flatMap((part) => [(part >> 8) & 0xff, part & 0xff]);
}

function isForbiddenIpv4(octets: readonly number[]): boolean {
  const a = octets[0] ?? 0;
  const b = octets[1] ?? 0;
  const c = octets[2] ?? 0;
  return (
    a === 0 ||
    a === 10 ||
    a === 127 ||
    (a === 100 && (b & 0xc0) === 0x40) ||
    (a === 169 && b === 254) ||
    (a === 172 && b >= 16 && b <= 31) ||
    (a === 192 && b === 168) ||
    (a === 192 && b === 0 && c === 0) ||
    (a === 192 && b === 0 && c === 2) ||
    (a === 198 && (b === 18 || b === 19)) ||
    (a === 198 && b === 51 && c === 100) ||
    (a === 203 && b === 0 && c === 113) ||
    a >= 224
  );
}

function embeddedIpv4(bytes: readonly number[], offset: number): number[] {
  return [
    bytes[offset] ?? 0,
    bytes[offset + 1] ?? 0,
    bytes[offset + 2] ?? 0,
    bytes[offset + 3] ?? 0,
  ];
}

function isForbiddenIpv6(parts: readonly number[]): boolean {
  const bytes = ipv6BytesFromParts(parts);
  if (isIpv4MappedParts(parts)) {
    return isForbiddenIpv4(embeddedIpv4(bytes, 12));
  }

  const b0 = bytes[0] ?? 0;
  const b1 = bytes[1] ?? 0;
  const b2 = bytes[2] ?? 0;
  const b3 = bytes[3] ?? 0;
  const b4 = bytes[4] ?? 0;
  const b5 = bytes[5] ?? 0;

  if (b0 === 0x00 && b1 === 0x64 && b2 === 0xff && b3 === 0x9b) {
    if (b4 === 0x00 && b5 === 0x01) {
      return true;
    }
    if (bytes.slice(4, 12).every((b) => b === 0)) {
      return isForbiddenIpv4(embeddedIpv4(bytes, 12));
    }
    return true;
  }

  if (
    (b0 & 0xe0) !== 0x20 ||
    (b0 & 0xfe) === 0xfc ||
    (b0 === 0x01 && b1 === 0x00 && bytes.slice(2, 8).every((b) => b === 0)) ||
    (b0 === 0x20 && b1 === 0x02) ||
    bytes.slice(0, 12).every((b) => b === 0)
  ) {
    return true;
  }
  if (b0 === 0x20 && b1 === 0x01) {
    if (b2 === 0x00 && b3 === 0x00) return true;
    if (b2 === 0x00 && (b3 & 0xf0) === 0x10) return true;
    if (b2 === 0x00 && b3 === 0x02) return true;
    if (b2 === 0x0d && b3 === 0xb8) return true;
  }
  return false;
}
/* eslint-enable no-bitwise */

export function validatePublicIp(ip: string): void {
  if (
    !ip ||
    ip.trim() !== ip ||
    ip.includes('[') ||
    ip.includes(']') ||
    ip.includes('%')
  ) {
    invalidConfig(`Invalid IP: ${ip}`);
  }

  const ipv4Octets = parseIpv4Octets(ip);
  if (ipv4Octets) {
    if (isForbiddenIpv4(ipv4Octets)) {
      invalidConfig(`Forbidden IP: ${ip}`);
    }
    return;
  }

  const ipv6Parts = parseIpv6Parts(ip);
  if (!ipv6Parts) {
    invalidConfig(`Invalid IP: ${ip}`);
  }
  if (isForbiddenIpv6(ipv6Parts)) {
    invalidConfig(`Forbidden IP: ${ip}`);
  }
}

function normalizeHeaders(
  headers: Record<string, string>
): Record<string, string> {
  const entries = Object.entries(headers ?? {});
  if (entries.length > MAX_HEADER_COUNT) {
    invalidConfig('Too many headers');
  }

  let totalBytes = 0;
  const normalizedHeaders = new Map<string, string>();
  for (const [name, rawValue] of entries) {
    const value = String(rawValue ?? '');
    const nameBytes = byteSize(name);
    const valueBytes = byteSize(value);
    totalBytes += nameBytes + valueBytes;

    if (
      !name ||
      hasControlChar(name) ||
      hasControlChar(value) ||
      nameBytes > MAX_HEADER_NAME_BYTES ||
      valueBytes > MAX_HEADER_VALUE_BYTES ||
      !HEADER_TOKEN_RE.test(name)
    ) {
      invalidConfig(`Invalid header: ${name}`);
    }

    const lowerName = name.toLowerCase();
    if (
      lowerName.startsWith(':') ||
      lowerName.startsWith('proxy-') ||
      UNSAFE_HEADERS.has(lowerName)
    ) {
      invalidConfig(`Unsafe header: ${name}`);
    }
    if (!MODULE_OWNED_HEADERS.has(lowerName)) {
      normalizedHeaders.set(name, value);
    }
  }

  if (totalBytes > MAX_TOTAL_HEADER_BYTES) {
    invalidConfig('Headers too large');
  }
  return Object.fromEntries(normalizedHeaders);
}

export function validateSniRequestConfig(
  config: SniNodeRequestConfig
): NormalizedSniRequestConfig {
  if ((config as { bodyFile?: unknown }).bodyFile !== undefined) {
    invalidConfig('request does not accept bodyFile');
  }
  const normalized = normalizeSniRequestConfig(config);
  const { method, body } = normalized;
  if ((method === 'GET' || method === 'HEAD') && body !== null) {
    invalidConfig(`${method} requests must not have a body`);
  }
  if (
    (method === 'POST' || method === 'PUT' || method === 'PATCH') &&
    body === null
  ) {
    invalidConfig(`${method} requests require an explicit body`);
  }
  return normalized;
}

function normalizeSniRequestConfig(
  config: SniNodeRequestConfig
): NormalizedSniRequestConfig {
  validateRequestId(config.requestId);
  validatePublicIp(config.ip);
  validateHostname(config.hostname);
  validateTimeout(config.timeout);
  const body = config.body ?? null;
  validateBody(body);

  if (config.port !== null && config.port !== undefined) {
    invalidConfig('Caller-controlled ports are not allowed');
  }

  return {
    requestId: config.requestId,
    ip: config.ip,
    hostname: config.hostname,
    body,
    headers: normalizeHeaders(config.headers ?? {}),
    method: normalizeMethod(config.method ?? 'GET'),
    path: normalizePath(config.path ?? '/'),
    timeout: Math.trunc(config.timeout),
  };
}

function createCustomAgent(): https.Agent {
  const agent = new https.Agent({
    keepAlive: true,
    keepAliveMsecs: 30_000,
    maxSockets: MAX_SOCKETS_PER_PAIR,
    maxTotalSockets: MAX_TOTAL_SOCKETS,
    maxFreeSockets: MAX_FREE_SOCKETS,
    timeout: MAX_TIMEOUT_MS,
    scheduling: 'lifo',
  });

  (
    agent as https.Agent & {
      getName(options: RequestOptions): string;
    }
  ).getName = (options: RequestOptions): string => {
    const ip = options.host || '';
    const hostname = options.servername || '';
    const port = options.port || 443;
    return `${hostname}:${canonicalizeSniDestinationIp(ip)}:${port}`;
  };
  return agent;
}

function createAgentState(): SniAgentState {
  return {
    agent: createCustomAgent(),
    activeRequests: new Set<ClientRequest>(),
    destroyed: false,
  };
}

export function isProxyRouteActive(proxyRules: string): boolean {
  return proxyRules
    .split(';')
    .map((rule) => rule.trim())
    .filter(Boolean)
    .some((rule) => rule.toUpperCase() !== 'DIRECT');
}

export function buildSniRequestOptions(
  config: NormalizedSniRequestConfig,
  agent: https.Agent
): RequestOptions {
  const headers: Record<string, string> = {
    ...config.headers,
    'Host': config.hostname,
    'Accept-Encoding': 'identity',
  };
  if (config.bodyFile) {
    headers['Content-Length'] = String(config.bodyFile.sizeBytes);
  } else if (config.body !== null) {
    headers['Content-Length'] = String(Buffer.byteLength(config.body, 'utf8'));
  }

  return {
    method: config.method,
    host: config.ip,
    port: 443,
    path: config.path,
    servername: config.hostname,
    headers,
    agent,
    rejectUnauthorized: true,
  };
}

export function headersToMaps(rawHeaders: string[]): {
  headers: Record<string, string>;
  multiValueHeaders: Record<string, string[]>;
} {
  const headers = new Map<string, string>();
  const multiValueHeaders = new Map<string, string[]>();
  for (let index = 0; index < rawHeaders.length; index += 2) {
    const name = String(rawHeaders[index] ?? '').toLowerCase();
    const value = String(rawHeaders[index + 1] ?? '');
    if (name) {
      headers.set(name, value);
      const values = multiValueHeaders.get(name);
      if (values) {
        values.push(value);
      } else {
        multiValueHeaders.set(name, [value]);
      }
    }
  }
  return {
    headers: Object.fromEntries(headers),
    multiValueHeaders: Object.fromEntries(multiValueHeaders),
  };
}

type SniLimiterLog = (
  level: 'info' | 'warn' | 'error',
  event: string,
  fields: Record<string, unknown>
) => void;

export class SniRequestLimiter {
  private maxActiveRequests: number;
  private maxActiveRequestsPerPair: number;
  private maxPendingRequests: number;
  private onLog: SniLimiterLog | undefined;

  constructor(
    maxActiveRequests = MAX_ACTIVE_REQUESTS,
    maxActiveRequestsPerPair = MAX_ACTIVE_REQUESTS_PER_PAIR,
    maxPendingRequests = MAX_PENDING_REQUESTS,
    onLog?: SniLimiterLog
  ) {
    this.maxActiveRequests = maxActiveRequests;
    this.maxActiveRequestsPerPair = maxActiveRequestsPerPair;
    this.maxPendingRequests = maxPendingRequests;
    this.onLog = onLog;
  }

  private activeRequests = 0;

  private activeRequestsByPair = new Map<string, number>();

  private pendingRequests: SniRequestLimiterWaiter[] = [];

  private getKey(hostname: string, ip: string): string {
    return `${hostname.toLowerCase()}|${canonicalizeSniDestinationIp(ip)}`;
  }

  private hasCapacity(key: string): boolean {
    return (
      this.activeRequests < this.maxActiveRequests &&
      (this.activeRequestsByPair.get(key) ?? 0) < this.maxActiveRequestsPerPair
    );
  }

  private reserveSlot(key: string): () => void {
    this.activeRequests += 1;
    this.activeRequestsByPair.set(
      key,
      (this.activeRequestsByPair.get(key) ?? 0) + 1
    );

    let released = false;
    return () => {
      if (released) return;
      released = true;
      this.activeRequests = Math.max(0, this.activeRequests - 1);
      const nextPairCount = this.activeRequestsByPair.get(key) ?? 0;
      if (nextPairCount <= 1) {
        this.activeRequestsByPair.delete(key);
      } else {
        this.activeRequestsByPair.set(key, nextPairCount - 1);
      }
      this.dispatchPendingRequests();
    };
  }

  private dispatchPendingRequests(): void {
    while (this.activeRequests < this.maxActiveRequests) {
      const index = this.pendingRequests.findIndex((waiter) =>
        this.hasCapacity(waiter.key)
      );
      if (index < 0) return;

      const [waiter] = this.pendingRequests.splice(index, 1);
      if (!waiter) return;
      if (waiter.signal && waiter.abortListener) {
        waiter.signal.removeEventListener('abort', waiter.abortListener);
      }
      waiter.resolve(this.reserveSlot(waiter.key));
    }
  }

  private getCancellationError(signal: AbortSignal): Error {
    return signal.reason instanceof Error
      ? signal.reason
      : new SniRequestError('SNI_CANCELLED', 'Request cancelled', true);
  }

  snapshot(hostname?: string, ip?: string): SniNodeDebugSnapshot {
    const key = hostname && ip ? this.getKey(hostname, ip) : undefined;
    return {
      activeRequests: this.activeRequests,
      activeRequestsForPair: key ? this.activeRequestsByPair.get(key) ?? 0 : 0,
      pendingRequests: this.pendingRequests.length,
      pendingRequestsForPair: key
        ? this.pendingRequests.filter((waiter) => waiter.key === key).length
        : 0,
    };
  }

  acquire(
    hostname: string,
    ip: string,
    options: { signal?: AbortSignal; onLog?: SniLimiterLog } = {}
  ): Promise<() => void> {
    const key = this.getKey(hostname, ip);
    if (options.signal?.aborted) {
      return Promise.reject(this.getCancellationError(options.signal));
    }
    if (this.hasCapacity(key)) {
      return Promise.resolve(this.reserveSlot(key));
    }
    if (this.pendingRequests.length >= this.maxPendingRequests) {
      const stats = this.snapshot(hostname, ip);
      const onLog = options.onLog ?? this.onLog;
      onLog?.('warn', 'desktop_sni_resource_limit', {
        hostname: hostname.toLowerCase(),
        ip,
        ipFamily: getIpFamily(ip),
        activeRequests: stats.activeRequests,
        activeRequestsForPair: stats.activeRequestsForPair,
        pendingRequests: stats.pendingRequests,
        pendingRequestsForPair: stats.pendingRequestsForPair,
        limit: this.maxPendingRequests,
        reason: 'max_pending_requests',
      });
      return Promise.reject(
        new SniRequestError(
          'SNI_RESOURCE_LIMIT',
          'Too many pending SNI requests',
          true
        )
      );
    }

    return new Promise((resolve, reject) => {
      const waiter: SniRequestLimiterWaiter = {
        key,
        signal: options.signal,
        abortListener: undefined,
        resolve,
      };
      if (options.signal) {
        waiter.abortListener = () => {
          const index = this.pendingRequests.indexOf(waiter);
          if (index < 0) return;
          this.pendingRequests.splice(index, 1);
          reject(this.getCancellationError(options.signal as AbortSignal));
        };
        options.signal.addEventListener('abort', waiter.abortListener, {
          once: true,
        });
      }
      this.pendingRequests.push(waiter);
    });
  }
}

// Both package entry points must use the same limiter in the main process.
const limiterKey = Symbol.for(
  '@onekeyfe/react-native-sni-connect.requestLimiter'
);
const sniProcess = process as typeof process & {
  [limiterKey]?: SniRequestLimiter;
};
const sharedRequestLimiter = (sniProcess[limiterKey] ??=
  new SniRequestLimiter());

export class SniNodeClient {
  private options: SniNodeClientOptions;

  private logPrefix: string;

  private agentState: SniAgentState;

  private requestsById = new Map<string, SniRequestState>();

  private allRequests = new Set<SniRequestState>();

  private allActiveRequests = new Set<ClientRequest>();

  private requestAgentStates = new Map<ClientRequest, SniAgentState>();

  private requestLimiter = sharedRequestLimiter;

  constructor(options: SniNodeClientOptions = {}) {
    this.options = options;
    this.logPrefix = options.logPrefix ?? '[DesktopApiSniRequest]';
    this.agentState = createAgentState();
  }

  getRequestLimiter(): SniRequestLimiter {
    return this.requestLimiter;
  }

  setRequestLimiter(limiter: SniRequestLimiter): void {
    this.requestLimiter = limiter;
  }

  getAgentState(): SniAgentState {
    return this.agentState;
  }

  private writeNativeLog(
    level: 'info' | 'warn' | 'error',
    event: string,
    fields: Record<string, unknown>
  ): void {
    emitNativeLog(this.options.logger, this.logPrefix, level, event, fields);
  }

  private maybeDestroyIdleAgentState(agentState: SniAgentState): void {
    if (
      agentState !== this.agentState &&
      agentState.activeRequests.size === 0 &&
      !agentState.destroyed
    ) {
      agentState.destroyed = true;
      agentState.agent.destroy();
    }
  }

  private removeRequestState(
    requestState: SniRequestState,
    request: ClientRequest | undefined,
    agentState: SniAgentState | undefined
  ): void {
    requestState.clearDeadline();
    requestState.settled = true;
    requestState.uploadProgress = undefined;
    let cleanupAgentState = agentState;
    if (
      requestState.requestId &&
      this.requestsById.get(requestState.requestId) === requestState
    ) {
      this.requestsById.delete(requestState.requestId);
    }
    this.allRequests.delete(requestState);
    if (request) {
      const state = agentState ?? this.requestAgentStates.get(request);
      this.allActiveRequests.delete(request);
      state?.activeRequests.delete(request);
      this.requestAgentStates.delete(request);
      cleanupAgentState = state;
    }
    if (cleanupAgentState) {
      this.maybeDestroyIdleAgentState(cleanupAgentState);
    }
  }

  async isProxyActiveForUrl(url: string): Promise<boolean> {
    const startedAt = Date.now();
    const hostname = getHostnameForLog(url);
    try {
      const proxyRules = await this.options.resolveProxy?.(url);
      const proxyRuleType = getProxyRuleType(proxyRules ?? '');
      if (proxyRuleType === 'unknown') {
        throw new SniRequestError(
          'SNI_UNSUPPORTED',
          'Proxy preflight is unavailable'
        );
      }
      const proxyActive = proxyRuleType !== 'direct';
      this.writeNativeLog('info', 'desktop_sni_proxy_preflight', {
        hostname,
        proxyActive,
        proxyRuleType,
        durationMs: Date.now() - startedAt,
      });
      return proxyActive;
    } catch (error) {
      this.writeNativeLog('error', 'desktop_sni_proxy_preflight', {
        hostname,
        proxyActive: 'unknown',
        proxyRuleType: 'unknown',
        durationMs: Date.now() - startedAt,
        errorCode: getErrorCode(error) ?? 'none',
        errorMessage: getErrorMessage(error),
      });
      throw error;
    }
  }

  private logInvalidConfig(
    config: Pick<
      SniNodeRequestConfig,
      'hostname' | 'ip' | 'method' | 'timeout' | 'path' | 'headers'
    >,
    bodyBytes: number,
    error: unknown,
    startedAt: number
  ): void {
    this.writeNativeLog('error', 'desktop_sni_request_failed', {
      code: getErrorCode(error) ?? 'SNI_INVALID_CONFIG',
      failClosed: isSniFailClosedError(error),
      hostname: config.hostname ?? 'unknown',
      ip: config.ip,
      ipFamily: config.ip ? getIpFamily(config.ip) : 'unknown',
      method: config.method ?? 'GET',
      timeout: config.timeout ?? 'unknown',
      pathBytes: config.path ? byteSize(config.path) : 0,
      bodyBytes,
      headerCount: Object.keys(config.headers ?? {}).length,
      activeRequests: this.requestLimiter.snapshot().activeRequests,
      activeRequestsForPair: 0,
      pendingRequests: this.requestLimiter.snapshot().pendingRequests,
      pendingRequestsForPair: 0,
      durationMs: Date.now() - startedAt,
      errorMessage: getErrorMessage(error),
    });
  }

  private createRequestState(
    requestId: string | undefined,
    isUpload: boolean,
    timeout: number,
    startedAt: number
  ): SniRequestState {
    const abortController = new AbortController();
    const deadline = startedAt + timeout;
    const cancelForTimeout = () =>
      requestState.cancel(
        new SniRequestError(
          'SNI_TIMEOUT',
          `SNI request timeout after ${timeout}ms`
        )
      );
    const timer = setTimeout(
      cancelForTimeout,
      Math.max(0, deadline - Date.now())
    );
    const requestState: SniRequestState = {
      requestId,
      settled: false,
      uploadProgress:
        requestId && isUpload ? { bytesSent: 0, totalBytes: 0 } : undefined,
      signal: abortController.signal,
      checkDeadline: () => {
        if (Date.now() >= deadline) cancelForTimeout();
        abortController.signal.throwIfAborted();
      },
      clearDeadline: () => clearTimeout(timer),
      cancel: (error) => {
        requestState.settled = true;
        requestState.uploadProgress = undefined;
        abortController.abort(error);
      },
    };

    if (requestId) {
      const previous = this.requestsById.get(requestId);
      if (previous) {
        this.writeNativeLog('warn', 'desktop_sni_lifecycle', {
          action: 'duplicate_request_id',
          requestId,
          success: true,
          activeCount: this.allActiveRequests.size,
          pendingCount: this.requestLimiter.snapshot().pendingRequests,
        });
        previous.cancel(
          new SniRequestError('SNI_CANCELLED', 'Request cancelled', true)
        );
      }
      this.requestsById.set(requestId, requestState);
    }
    this.allRequests.add(requestState);

    return requestState;
  }

  async request(config: SniNodeRequestConfig): Promise<SniNodeResponse> {
    const startedAt = Date.now();
    let normalizedConfig: NormalizedSniRequestConfig;
    try {
      normalizedConfig = validateSniRequestConfig(config);
    } catch (error) {
      this.logInvalidConfig(
        config,
        config.body ? byteSize(config.body) : 0,
        error,
        startedAt
      );
      return Promise.reject(error);
    }
    const requestState = this.createRequestState(
      normalizedConfig.requestId,
      false,
      normalizedConfig.timeout,
      startedAt
    );
    return this.execute(normalizedConfig, startedAt, requestState);
  }

  async uploadFile(config: SniNodeUploadFileConfig): Promise<SniNodeResponse> {
    const startedAt = Date.now();
    try {
      validateRequestId(config.requestId);
      validateTimeout(config.timeout);
    } catch (error) {
      this.logInvalidConfig(config, 0, error, startedAt);
      throw error;
    }

    const requestState = this.createRequestState(
      config.requestId,
      true,
      Math.trunc(config.timeout),
      startedAt
    );
    const { signal } = requestState;
    let rejectOnAbort: (() => void) | undefined;
    let normalizedConfig: NormalizedSniRequestConfig;
    try {
      requestState.checkDeadline();
      normalizedConfig = await new Promise<NormalizedSniRequestConfig>(
        (resolve, reject) => {
          rejectOnAbort = () => reject(signal.reason);
          signal.addEventListener('abort', rejectOnAbort, { once: true });
          validateSniUploadFileConfig(config, this.options, signal).then(
            resolve,
            reject
          );
        }
      );
      requestState.checkDeadline();
    } catch (error) {
      this.removeRequestState(requestState, undefined, undefined);
      this.logInvalidConfig(config, 0, error, startedAt);
      throw error;
    } finally {
      if (rejectOnAbort) signal.removeEventListener('abort', rejectOnAbort);
    }

    if (requestState.uploadProgress && normalizedConfig.bodyFile) {
      requestState.uploadProgress.totalBytes =
        normalizedConfig.bodyFile.sizeBytes;
    }
    return this.execute(normalizedConfig, startedAt, requestState);
  }

  async getUploadProgress(
    requestId: string
  ): Promise<SniNodeUploadProgress | null> {
    if (!requestId) {
      return null;
    }
    const requestState = this.requestsById.get(requestId);
    if (!requestState || requestState.settled || !requestState.uploadProgress) {
      return null;
    }
    return {
      bytesSent: requestState.uploadProgress.bytesSent,
      totalBytes: requestState.uploadProgress.totalBytes,
    };
  }

  private async execute(
    normalizedConfig: NormalizedSniRequestConfig,
    startedAt: number,
    requestState: SniRequestState
  ): Promise<SniNodeResponse> {
    let releaseRequestSlot: (() => void) | undefined;
    let activeRequest: ClientRequest | undefined;
    let activeAgentState: SniAgentState | undefined;
    let activeFileStream: fs.ReadStream | undefined;
    const { signal } = requestState;
    const limiter = this.requestLimiter;

    try {
      requestState.checkDeadline();
      releaseRequestSlot = await limiter.acquire(
        normalizedConfig.hostname,
        normalizedConfig.ip,
        {
          signal,
          onLog: (level, event, fields) =>
            this.writeNativeLog(level, event, fields),
        }
      );
      requestState.checkDeadline();
    } catch (error) {
      releaseRequestSlot?.();
      releaseRequestSlot = undefined;
      this.removeRequestState(requestState, undefined, undefined);
      const stats = this.requestLimiter.snapshot(
        normalizedConfig.hostname,
        normalizedConfig.ip
      );
      this.writeNativeLog('error', 'desktop_sni_request_failed', {
        code: getErrorCode(error) ?? 'SNI_RESOURCE_LIMIT',
        failClosed: isSniFailClosedError(error),
        hostname: normalizedConfig.hostname,
        ip: normalizedConfig.ip,
        ipFamily: getIpFamily(normalizedConfig.ip),
        method: normalizedConfig.method,
        timeout: normalizedConfig.timeout,
        pathBytes: byteSize(normalizedConfig.path),
        bodyBytes: getBodyBytes(normalizedConfig),
        headerCount: Object.keys(normalizedConfig.headers).length,
        activeRequests: stats.activeRequests,
        activeRequestsForPair: stats.activeRequestsForPair,
        pendingRequests: stats.pendingRequests,
        pendingRequestsForPair: stats.pendingRequestsForPair,
        durationMs: Date.now() - startedAt,
        errorMessage: getErrorMessage(error),
      });
      return Promise.reject(error);
    }

    return new Promise((resolve, reject) => {
      let settled = false;
      let responseStarted = false;
      const cleanup = () => {
        signal.removeEventListener('abort', onAbort);
        activeFileStream?.destroy();
        this.removeRequestState(requestState, activeRequest, activeAgentState);
        releaseRequestSlot?.();
      };

      const settleReject = (error: Error) => {
        if (settled) return;
        settled = true;
        activeRequest?.destroy();
        cleanup();
        const stats = limiter.snapshot(
          normalizedConfig.hostname,
          normalizedConfig.ip
        );
        this.writeNativeLog('error', 'desktop_sni_request_failed', {
          code: getErrorCode(error) ?? 'SNI_REQUEST_FAILED',
          failClosed: isSniFailClosedError(error),
          hostname: normalizedConfig.hostname,
          ip: normalizedConfig.ip,
          ipFamily: getIpFamily(normalizedConfig.ip),
          method: normalizedConfig.method,
          timeout: normalizedConfig.timeout,
          pathBytes: byteSize(normalizedConfig.path),
          bodyBytes: getBodyBytes(normalizedConfig),
          headerCount: Object.keys(normalizedConfig.headers).length,
          activeRequests: stats.activeRequests,
          activeRequestsForPair: stats.activeRequestsForPair,
          pendingRequests: stats.pendingRequests,
          pendingRequestsForPair: stats.pendingRequestsForPair,
          durationMs: Date.now() - startedAt,
          errorMessage: error.message,
        });
        reject(error);
      };

      const settleResolve = (response: SniNodeResponse) => {
        if (settled) return;
        settled = true;
        if (
          normalizedConfig.bodyFile &&
          activeRequest &&
          !activeRequest.writableEnded
        ) {
          activeRequest.destroy();
        }
        cleanup();
        this.writeNativeLog('info', 'desktop_sni_request_result', {
          result: 'response',
          status: response.statusCode,
          hostname: normalizedConfig.hostname,
          ip: normalizedConfig.ip,
          ipFamily: getIpFamily(normalizedConfig.ip),
          method: normalizedConfig.method,
          timeout: normalizedConfig.timeout,
          responseBytes: response.body ? byteSize(response.body) : 0,
          durationMs: Date.now() - startedAt,
        });
        resolve(response);
      };

      const onAbort = () => settleReject(signal.reason);
      signal.addEventListener('abort', onAbort, { once: true });

      try {
        activeAgentState = this.agentState;
        const requestOptions = buildSniRequestOptions(
          normalizedConfig,
          activeAgentState.agent
        );

        const request = https.request(requestOptions, (response) => {
          responseStarted = true;
          response.on('error', (error: Error) => {
            settleReject(
              error instanceof SniRequestError
                ? error
                : new SniRequestError(
                    'SNI_RESPONSE_FAILED',
                    `Response stream error: ${error.message}`,
                    true
                  )
            );
          });

          if (settled) {
            response.destroy();
            return;
          }
          const encoding = response.headers['content-encoding'];
          if (encoding && encoding !== 'identity') {
            settleReject(
              new SniRequestError(
                'SNI_RESPONSE_FAILED',
                `Unsupported content encoding: ${String(encoding)}`,
                true
              )
            );
            return;
          }

          const chunks: Buffer[] = [];
          let totalBytes = 0;
          response.on('data', (chunk: Buffer | string) => {
            if (settled) return;
            const buffer = Buffer.isBuffer(chunk)
              ? chunk
              : Buffer.from(chunk, 'utf8');
            totalBytes += buffer.length;
            if (totalBytes > MAX_RESPONSE_BODY_BYTES) {
              settleReject(
                new SniRequestError(
                  'SNI_RESPONSE_FAILED',
                  'Response body too large',
                  true
                )
              );
              return;
            }
            chunks.push(buffer);
          });

          response.on('end', () => {
            if (settled) return;
            const headerMaps = headersToMaps(response.rawHeaders ?? []);
            const body = Buffer.concat(chunks).toString('utf8');
            const statusCode = response.statusCode || 0;
            settleResolve({
              statusCode,
              status: statusCode,
              statusText: response.statusMessage || '',
              headers: headerMaps.headers,
              multiValueHeaders: headerMaps.multiValueHeaders,
              body,
              data: body,
            });
          });
        });
        activeRequest = request;
        this.allActiveRequests.add(request);
        activeAgentState.activeRequests.add(request);
        this.requestAgentStates.set(request, activeAgentState);
        request.on('error', (error: Error) => {
          settleReject(
            classifyTransportError(error, Boolean(normalizedConfig.bodyFile))
          );
        });

        request.once('upgrade', (_response, socket) => {
          socket.destroy();
          settleReject(
            new SniRequestError(
              'SNI_SECURITY_POLICY_FAILED',
              'Protocol upgrades are not supported',
              true
            )
          );
        });
        request.once('close', () => {
          if (settled) return;
          settleReject(
            new SniRequestError(
              responseStarted ? 'SNI_RESPONSE_FAILED' : 'SNI_REQUEST_FAILED',
              'Request closed before completion',
              responseStarted
            )
          );
        });

        const stats = limiter.snapshot(
          normalizedConfig.hostname,
          normalizedConfig.ip
        );
        this.writeNativeLog('info', 'desktop_sni_request_start', {
          requestId: normalizedConfig.requestId,
          hostname: normalizedConfig.hostname,
          ip: normalizedConfig.ip,
          ipFamily: getIpFamily(normalizedConfig.ip),
          method: normalizedConfig.method,
          timeout: normalizedConfig.timeout,
          pathBytes: byteSize(normalizedConfig.path),
          bodyBytes: getBodyBytes(normalizedConfig),
          headerCount: Object.keys(normalizedConfig.headers).length,
          activeRequests: stats.activeRequests,
          activeRequestsForPair: stats.activeRequestsForPair,
          pendingRequests: stats.pendingRequests,
          pendingRequestsForPair: stats.pendingRequestsForPair,
        });

        if (settled) return;
        const { bodyFile } = normalizedConfig;
        if (bodyFile) {
          // Bounded to the validated size so a growing file cannot exceed
          // the Content-Length that was already sent.
          activeFileStream = fs.createReadStream(bodyFile.filePath, {
            start: 0,
            end: bodyFile.sizeBytes - 1,
          });
          activeFileStream.on('error', () => {
            settleReject(
              new SniRequestError(
                'SNI_FILE_READ_FAILED',
                'Upload file read failed',
                true
              )
            );
          });
          let bytesSent = 0;
          let lastProgressAt = 0;
          const sendProgress = () => {
            if (
              !normalizedConfig.requestId ||
              settled ||
              requestState.settled
            ) {
              return;
            }
            const now = Date.now();
            if (bytesSent < bodyFile.sizeBytes && now - lastProgressAt < 100) {
              return;
            }
            lastProgressAt = now;
            this.options.onUploadProgress?.({
              requestId: normalizedConfig.requestId,
              bytesSent,
              totalBytes: bodyFile.sizeBytes,
              progressPercent: Math.min(
                99,
                Math.floor((bytesSent / bodyFile.sizeBytes) * 100)
              ),
            });
          };
          activeFileStream.on('data', (chunk: Buffer | string) => {
            const bytes = Buffer.isBuffer(chunk) ? chunk : Buffer.from(chunk);
            activeFileStream?.pause();
            request.write(bytes, (error?: Error | null) => {
              if (settled || requestState.settled) return;
              if (error) {
                settleReject(classifyTransportError(error, true));
                return;
              }
              bytesSent = Math.min(
                bodyFile.sizeBytes,
                bytesSent + bytes.length
              );
              if (requestState.uploadProgress) {
                requestState.uploadProgress = {
                  bytesSent,
                  totalBytes: bodyFile.sizeBytes,
                };
              }
              try {
                sendProgress();
              } catch {
                /* Progress must not fail upload. */
              }
              activeFileStream?.resume();
            });
          });
          activeFileStream.on('end', () => request.end());
          return;
        }

        if (normalizedConfig.body) {
          request.write(normalizedConfig.body);
        }

        request.end();
      } catch (error) {
        settleReject(error instanceof Error ? error : new Error(String(error)));
      }
    });
  }

  async cancelRequest(requestId: string): Promise<{ success: boolean }> {
    const requestState = this.requestsById.get(requestId);
    if (!requestState) {
      this.writeNativeLog('warn', 'desktop_sni_lifecycle', {
        action: 'cancel_request',
        requestId,
        success: false,
        activeCount: this.allActiveRequests.size,
        pendingCount: this.requestLimiter.snapshot().pendingRequests,
      });
      return { success: false };
    }
    this.requestsById.delete(requestId);
    requestState.cancel(
      new SniRequestError('SNI_CANCELLED', 'Request cancelled', true)
    );
    this.writeNativeLog('info', 'desktop_sni_lifecycle', {
      action: 'cancel_request',
      requestId,
      success: true,
      activeCount: this.allActiveRequests.size,
      pendingCount: this.requestLimiter.snapshot().pendingRequests,
    });
    return { success: true };
  }

  async getDebugSnapshot({
    hostname,
    ip,
  }: Pick<
    SniNodeRequestConfig,
    'hostname' | 'ip'
  >): Promise<SniNodeDebugSnapshot> {
    validateHostname(hostname);
    validatePublicIp(ip);
    return this.requestLimiter.snapshot(hostname, ip);
  }

  async cancelAllRequests(): Promise<{ success: boolean }> {
    const requests = Array.from(this.allRequests);
    this.requestsById.clear();
    this.allRequests.clear();
    for (const request of requests) {
      request.cancel(
        new SniRequestError('SNI_CANCELLED', 'Request cancelled', true)
      );
    }
    this.writeNativeLog('info', 'desktop_sni_lifecycle', {
      action: 'cancel_all_requests',
      success: true,
      requestCount: requests.length,
      activeCount: this.allActiveRequests.size,
      pendingCount: this.requestLimiter.snapshot().pendingRequests,
    });
    return { success: true };
  }

  async clearDNSCache(): Promise<{ success: boolean }> {
    const previousAgentState = this.agentState;
    this.agentState = createAgentState();
    const previousAgent = previousAgentState.agent;
    previousAgent.keepSocketAlive = () => false;
    for (const sockets of Object.values(previousAgent.freeSockets)) {
      sockets?.forEach((socket) => socket.destroy());
    }
    this.maybeDestroyIdleAgentState(previousAgentState);
    this.writeNativeLog('info', 'desktop_sni_lifecycle', {
      action: 'clear_dns_cache',
      success: true,
      activeCount: previousAgentState.activeRequests.size,
    });
    return { success: true };
  }

  async isSupported(): Promise<boolean> {
    return true;
  }
}

export default SniNodeClient;
