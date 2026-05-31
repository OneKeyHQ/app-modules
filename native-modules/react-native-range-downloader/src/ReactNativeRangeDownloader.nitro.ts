import type { HybridObject } from 'react-native-nitro-modules';

// A channel = one logical consumer (bundle update / apk update / chart webview).
// The channel decides the iOS background URLSession identifier suffix and the
// event-routing key, so different consumers never cross-talk. Closed set per
// design decision 10.1 (constant enum, not a bare string).
export type DownloadChannel = 'bundle' | 'apk' | 'chart';

export interface RangeDownloadParams {
  channel: DownloadChannel;
  taskId: string; // caller-generated unique id (dedupe / resume locating within a channel)
  url: string; // must be HTTPS (enforced inside the module, mirrors current behavior)
  destFilePath: string; // final absolute path on disk (module does not own directory layout)
  expectedSha256?: string; // optional: in-module post-download self-check; else caller verifies later
  segmentCount?: number; // default 8 (mirrors current behavior)
  minConcurrentBytes?: number; // default 2MB (below this falls straight back to single stream)
}

export type RangeDownloadOutcome =
  | 'completed' // destFilePath fully on disk (and expectedSha256, if passed, has passed)
  | 'fallback'; // concurrent unavailable, caller should run its own single-stream path

export interface RangeDownloadResult {
  outcome: RangeDownloadOutcome;
  filePath: string;
  fallbackReason?: string; // filled when outcome=fallback (range unsupported / 200 / too small ...)
}

export interface RangeDownloadEvent {
  channel: DownloadChannel; // callers filter their own events by channel
  taskId: string;
  type: string; // 'start' | 'progress' | 'complete' | 'error' | 'fallback'
  progress: number; // 0-100
  message: string;
}

export interface ReactNativeRangeDownloader
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // Main entry: concurrent ranged download (iOS background session / Android thread pool).
  // The promise resolves once the outcome is decided; on fallback the module has already
  // cleaned up its own segment artifacts.
  download(params: RangeDownloadParams): Promise<RangeDownloadResult>;

  // Clean the concurrent artifacts (.segN / .partial / .progress) for one task.
  // Caller invokes before falling back to its own single-stream path.
  discardArtifacts(
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
}
