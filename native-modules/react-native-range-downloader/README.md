<div align="center">

# @onekeyfe/react-native-range-downloader

**Resumable multi-connection HTTP range downloads for OneKey OTA, APK and asset updates.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-range-downloader?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-range-downloader)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-range-downloader?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Fetches one file over parallel HTTP byte-range connections to improve throughput
on high-latency mobile networks, then assembles and promotes it atomically. Each
segment is persisted on disk, so a new run resumes from completed bytes instead
of starting over. The concurrent path is an accelerator: when a server or
network cannot support ranges, `download()` reports a typed fallback and the
caller keeps ownership of its single-stream path. iOS transfers through
background `URLSession`s, Android through a thread pool.

## Features

- **Parallel segments, bounded default** — an 8-segment plan for files at or
  above the 2 MB concurrency threshold.
- **Resume instead of restart** — completed `.segN` artifacts survive process
  restarts; a re-run fetches only missing or incomplete segments.
- **Typed outcomes, no prose parsing** — `completed`, `fallbackTransient`,
  `fallbackPermanent` and a `fallbackKind` such as `authExpired`.
- **Filterable progress events** — one shared registry routes `start`,
  `progress`, `complete`, `error` and `fallback` events by channel and `taskId`.
- **Explicit cancel and discard** — in-flight work stops before artifacts are
  deleted, so a running task cannot resurrect them.
- **Integrity and security** — HTTPS on every redirect hop, optional whole-file
  SHA-256 before the atomic rename, and rejection of short or mis-aligned `206`.

## Installation

```sh
yarn add @onekeyfe/react-native-range-downloader react-native-nitro-modules
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking. `react-native-nitro-modules` is a required peer dependency, and the
native code also links `@onekeyfe/react-native-native-logger`. The Android app
needs the normal `INTERNET` permission; the package declares no permissions.

For background transfers to complete after suspension, the app delegate must
forward background `URLSession` events to the module:

```swift
func application(_ app: UIApplication,
                 handleEventsForBackgroundURLSession id: String,
                 completionHandler: @escaping () -> Void) {
  NotificationCenter.default.post(
    name: Notification.Name("RangeDownloaderBackgroundEvents"),
    object: nil,
    userInfo: ["identifier": id, "completionHandler": completionHandler]
  )
}
```

Android has no background-session concept: a run continues while the process
lives and resumes from persisted segments on the next launch.

## Usage

```ts
import { ReactNativeRangeDownloader, RangeDownloadChannel } from '@onekeyfe/react-native-range-downloader';

const taskId = 'bundle-2026-11';
const destFilePath = `${ReactNativeRangeDownloader.getDownloadsDir()}/${taskId}.zip`;
const listenerId = ReactNativeRangeDownloader.addDownloadListener((event) => {
  if (event.channel === RangeDownloadChannel.Bundle && event.taskId === taskId) {
    console.log(event.type, event.progress, event.message);
  }
});

try {
  const result = await ReactNativeRangeDownloader.download({
    channel: RangeDownloadChannel.Bundle,
    taskId,
    url: 'https://example.com/bundle.zip',
    destFilePath,
  });
  console.log(result.outcome, result.fallbackKind, result.fallbackReason);
} finally {
  ReactNativeRangeDownloader.removeDownloadListener(listenerId);
}
```

The caller supplies `destFilePath` and owns directory layout, install flow and
signature checks; the module owns segment transfer, resume, retry, assembly and
the checksum it was given.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `download` | `(params: RangeDownloadParams) => Promise<RangeDownloadResult>` | iOS, Android | Runs the concurrent download and resolves once the outcome is decided. |
| `cancel` | `(channel, taskId, destFilePath) => Promise<void>` | iOS, Android | Cancels the matching run and removes its artifacts. |
| `discardArtifacts` | `(channel, taskId, destFilePath) => Promise<void>` | iOS, Android | Removes `.segN` / `.partial` / `.progress` artifacts for one task. |
| `addDownloadListener` | `(callback: (event: RangeDownloadEvent) => void) => number` | iOS, Android | Registers a shared progress listener; returns an id. |
| `removeDownloadListener` | `(id: number) => void` | iOS, Android | Unregisters a listener. |
| `getDownloadsDir` | `() => string` | iOS, Android | App-owned writable cache directory for scratch destinations. |
| `RangeDownloadChannel` | `{ Bundle, Apk, Chart, Firmware }` | iOS, Android | Closed channel set used for event routing and session identity. |
| Firmware staging | `downloadFirmwareArtifact`, `openFirmwareArtifact`, `readFirmwareArtifact`, `closeFirmwareArtifact`, `discardFirmwareArtifact`, `cancelFirmwareArtifactDownloads`, `materializeFirmwareArchive`, `createFirmwareArtifactLease`, `retainFirmwareArtifact`, `releaseFirmwareArtifactLease`, `sweepFirmwareArtifactOrphans`, `getFirmwareArtifactCapabilities` | iOS, Android | Staged firmware artifact download, bounded reader, archive materialization, lease and orphan sweep. |

`RangeDownloadParams` needs `channel`, `taskId`, an HTTPS `url` and an absolute
`destFilePath`; optional knobs include `expectedSha256`, `segmentCount` (8),
`minConcurrentBytes` (2 MB), `maxSegmentAttempts` (4), `requestTimeoutSeconds`
(60), `stallTimeoutSeconds` (30) and `overallDeadlineSeconds` (0 = none). The
retry, stall and deadline knobs are consumed by the iOS implementation; the
Android concurrent path keeps its own fixed retry tuning.

The normative behavior is the
[OneKey Concurrent Download Standard](SPEC.md); the package's own native contract
is [`docs/SPEC.md`](docs/SPEC.md), and the public types are in
[`src/ReactNativeRangeDownloader.nitro.ts`](src/ReactNativeRangeDownloader.nitro.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Concurrent range download | ✅ | ✅ | — |
| Resume from on-disk segments | ✅ | ✅ | — |
| OS background transfer | ✅ background `URLSession` | — in-process only | — |
| Progress event stream | ✅ | ✅ | — |
| Typed fallback classification | ✅ | ✅ | — |
| Cancel / discard artifacts | ✅ | ✅ | — |
| Firmware artifact staging | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> Treat the concurrent path as an accelerator. Keep a single-stream fallback and
> branch on `outcome`: `fallbackTransient` keeps its segments and can be retried
> on the concurrent path; `fallbackPermanent` discards them and must restart
> through the caller's own downloader.

> [!WARNING]
> All consumers share one listener registry. Filter events by `channel` (and
> usually `taskId`), and always remove the listener when done.

> [!NOTE]
> `authExpired` and `notFound` are permanent for the URL used. Fetch a fresh
> signed URL before retrying instead of re-running the same request.

## References

- [OCDS standard](SPEC.md)
- [Native contract](docs/SPEC.md)
- [Example page](../../example/react-native/pages/RangeDownloaderTestPage.tsx)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-range-downloader)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
