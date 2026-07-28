import type { HybridObject } from 'react-native-nitro-modules';

// A channel = one logical consumer (bundle update / apk update / chart webview).
// The channel decides the iOS background URLSession identifier suffix and the
// event-routing key, so different consumers never cross-talk. Closed set per
// design decision 10.1 (constant enum, not a bare string).
export type DownloadChannel = 'bundle' | 'apk' | 'chart' | 'firmware';

export interface RangeDownloadParams {
  channel: DownloadChannel;
  taskId: string; // caller-generated unique id (dedupe / resume locating within a channel)
  url: string; // must be HTTPS (enforced inside the module, mirrors current behavior)
  destFilePath: string; // final absolute path on disk (module does not own directory layout)
  expectedSha256?: string; // optional: in-module post-download self-check; else caller verifies later
  segmentCount?: number; // default 8 (mirrors current behavior)
  minConcurrentBytes?: number; // default 2MB (below this falls straight back to single stream)
  // OCDS §5.4: caller-tunable retry/backoff/deadline knobs. Defaults are platform
  // configuration (filled in by the native module when omitted), not part of the
  // standard. These let the OTA / APK / asset callers tune behavior per channel.
  maxSegmentAttempts?: number; // per-segment transient retry budget within a single run (default 4)
  requestTimeoutSeconds?: number; // connection/request timeout per segment task (default 60)
  stallTimeoutSeconds?: number; // bytes-stalled (no-progress) watchdog window (default 30)
  overallDeadlineSeconds?: number; // single-run wall-clock bound before giving up (default 0 = none)
}

// OCDS §4: the failure class is an EXPLICIT, typed outcome of the operation, never
// inferred from incidental on-disk side effects (e.g. whether `.segN` survives).
//   - `completed`         — destFilePath fully on disk (and expectedSha256, if passed, has passed)
//   - `fallbackTransient` — concurrent path interrupted but RESUMABLE; segments kept; caller
//                           should retry the concurrent path / keep its progress floor
//   - `fallbackPermanent` — concurrency fundamentally unusable for this object; segments are
//                           unsalvageable and have been discarded; caller restarts single-stream
//   - `fallback`          — DEPRECATED legacy alias kept for wire/back-compat during the
//                           transition; new callers consume the two typed classes above
export type RangeDownloadOutcome =
  | 'completed'
  | 'fallbackTransient'
  | 'fallbackPermanent'
  | 'fallback';

// OCDS §4: optional sub-classification of a fallback, so the caller (and analytics)
// can distinguish WHY the concurrent path was abandoned without parsing the reason
// string. Present only when outcome is a fallback class.
export type RangeFallbackKind =
  | 'serverIgnoredRange' // server answered 200 to a Range request (Permanent)
  | 'rangeUnsupported' // probe inconclusive / range not supported (Permanent)
  | 'authExpired' // 401/403: signed URL dead; caller must fetch a fresh URL (Permanent)
  | 'notFound' // 404/410: object gone (Permanent)
  | 'redirectRejected' // non-HTTPS redirect rejected per §5.9 (Permanent)
  | 'checksumMismatch' // whole-file checksum mismatch after assembly (Permanent)
  | 'multipartOrBadTotal' // multipart/byteranges or Content-Range total disagrees (Permanent)
  | 'transientNetwork' // connection lost / timeout / DNS / TLS / stall (Transient)
  | 'throttled' // 429 / 5xx (except 501/505) (Transient)
  | 'budgetExhausted'; // per-segment attempts / deadline exhausted while still resumable (Transient)

export interface RangeDownloadResult {
  outcome: RangeDownloadOutcome;
  filePath: string;
  fallbackReason?: string; // filled when outcome=fallback (range unsupported / 200 / too small ...)
  fallbackKind?: RangeFallbackKind; // typed sub-class of the fallback (see §4); omitted on completed
}

export interface RangeDownloadEvent {
  channel: DownloadChannel; // callers filter their own events by channel
  taskId: string;
  type: string; // 'start' | 'progress' | 'complete' | 'error' | 'fallback'
  progress: number; // 0-100
  message: string;
}

export interface FirmwareArtifactDownloadParams {
  taskId: string;
  transactionId: string;
  leaseRef: string;
  artifactId: string;
  url: string;
  routeType: string;
  resolvedIp?: string;
  expectedSize: number;
  expectedSha256: string;
  maxBytes: number;
  overallDeadlineSeconds?: number;
}

export interface FirmwareArtifactReceipt {
  artifactRef: string;
  size: number;
  sha256: string;
}

export interface FirmwareArtifactCapabilities {
  firmwareArtifactProtocolVersion: number;
  supportedRouteTypes: string[];
  supportsArchiveMaterialization: boolean;
  maxReadBytes: number;
}

export interface FirmwareArtifactRefParams {
  artifactRef: string;
}

export interface FirmwareArtifactCancelParams {
  transactionId: string;
}

export interface FirmwareArtifactReaderInfo {
  readerId: string;
  size: number;
}

export interface FirmwareArtifactReaderReadParams {
  readerId: string;
  offset: number;
  length: number;
}

export interface FirmwareArtifactReaderCloseParams {
  readerId: string;
}

export interface FirmwareArchiveMaterializeParams {
  leaseRef: string;
  archiveArtifactRef: string;
  expectedEntries: FirmwareArchiveExpectedEntry[];
}

export interface FirmwareArchiveExpectedEntry {
  artifactId: string;
  entryName: string;
  expectedSize: number;
  expectedSha256: string;
}

export interface FirmwareArchiveMaterializedArtifact {
  entryName: string;
  receipt: FirmwareArtifactReceipt;
}

export interface FirmwareArchiveMaterializeResult {
  artifacts: FirmwareArchiveMaterializedArtifact[];
}

export interface FirmwareArtifactLeaseCreateParams {
  transactionId: string;
}

export interface FirmwareArtifactLease {
  leaseRef: string;
}

export interface FirmwareArtifactLeaseRetainParams {
  leaseRef: string;
  artifactRef: string;
}

export interface FirmwareArtifactLeaseReleaseParams {
  leaseRef: string;
  disposition: string;
}

export interface FirmwareArtifactSweepResult {
  deletedFiles: number;
  deletedBytes: number;
}

export interface ReactNativeRangeDownloader
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // Main entry: concurrent ranged download (iOS background session / Android thread pool).
  // The promise resolves once the outcome is decided; on fallback the module has already
  // cleaned up its own segment artifacts.
  download(params: RangeDownloadParams): Promise<RangeDownloadResult>;

  // Clean the concurrent artifacts (.segN / .partial / .progress) for one task.
  // Caller invokes before falling back to its own single-stream path.
  // Cancel-then-delete: any in-flight tasks/workers for (channel,taskId) are
  // cancelled before the files are removed so they cannot be resurrected.
  discardArtifacts(
    channel: DownloadChannel,
    taskId: string,
    destFilePath: string
  ): Promise<void>;

  // Cancel an in-flight download for (channel,taskId) and remove its artifacts.
  // iOS cancels the matching background URLSession tasks; Android flips the
  // worker pool's abort flag and shuts it down — both before deleting files.
  cancel(
    channel: DownloadChannel,
    taskId: string,
    destFilePath: string
  ): Promise<void>;

  // Event listening: all consumers share one listener registry and filter by channel.
  addDownloadListener(callback: (event: RangeDownloadEvent) => void): number;
  removeDownloadListener(id: number): void;

  // App-owned writable directory (caches) for scratch downloads. The module does
  // not own directory layout for real consumers, but exposes this so demos/simple
  // callers have a valid absolute destination without hardcoding sandbox paths.
  getDownloadsDir(): string;

  getFirmwareArtifactCapabilities(): FirmwareArtifactCapabilities;
  downloadFirmwareArtifact(
    params: FirmwareArtifactDownloadParams
  ): Promise<FirmwareArtifactReceipt>;
  cancelFirmwareArtifactDownloads(
    params: FirmwareArtifactCancelParams
  ): Promise<void>;
  discardFirmwareArtifact(params: FirmwareArtifactRefParams): Promise<void>;
  openFirmwareArtifact(
    params: FirmwareArtifactRefParams
  ): Promise<FirmwareArtifactReaderInfo>;
  readFirmwareArtifact(
    params: FirmwareArtifactReaderReadParams
  ): Promise<ArrayBuffer>;
  closeFirmwareArtifact(
    params: FirmwareArtifactReaderCloseParams
  ): Promise<void>;
  materializeFirmwareArchive(
    params: FirmwareArchiveMaterializeParams
  ): Promise<FirmwareArchiveMaterializeResult>;
  createFirmwareArtifactLease(
    params: FirmwareArtifactLeaseCreateParams
  ): Promise<FirmwareArtifactLease>;
  retainFirmwareArtifact(
    params: FirmwareArtifactLeaseRetainParams
  ): Promise<void>;
  releaseFirmwareArtifactLease(
    params: FirmwareArtifactLeaseReleaseParams
  ): Promise<void>;
  sweepFirmwareArtifactOrphans(): Promise<FirmwareArtifactSweepResult>;
}
