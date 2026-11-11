<div align="center">

# @onekeyfe/react-native-perf-stats

**CPU, memory, UI-FPS and JS-FPS sampling for OneKey apps, even when JavaScript is frozen.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-perf-stats?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-perf-stats)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-perf-stats?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Samples process CPU, resident memory, UI-thread frame rate and JS-thread frame
rate on a dedicated native thread. The native sampler and its optional draggable
overlay keep updating when the JS thread is blocked. CPU time and RSS come from
platform process counters, UI FPS from `CADisplayLink` / `Choreographer`, and JS
FPS from a `requestAnimationFrame` ticker owned by the JavaScript wrapper. There
is no Web implementation.

## Features

- **Sampling that survives a busy JS thread** — the timer runs on a dedicated
  background thread (GCD source on iOS, `HandlerThread` on Android).
- **One sample, four metrics** — `sample()` resolves a `PerfSample` with `cpu`,
  `rss`, `uiFps`, `jsFps` and `timestamp`.
- **Native overlay without floating-window permission** — a draggable readout
  attached to the current Activity window on Android and a passthrough
  `UIWindow` on iOS.
- **Cheap manual reads** — one-shot `sample()` runs off the JS thread and shares
  the periodic sampler's CPU baseline.
- **Memory-pressure awareness** — subscribe to OS memory warnings and trigger
  the same native cache reclaim on demand.
- **Sustained-anomaly logging** — a metric that stays over its threshold for
  five samples is logged once per 30 s per metric through the native logger.

## Installation

```sh
yarn add @onekeyfe/react-native-perf-stats react-native-nitro-modules @onekeyfe/react-native-native-logger
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking. The native code logs anomalies through `OneKeyLog` from
`@onekeyfe/react-native-native-logger`, so that package must be installed too.

No runtime permission is needed. The Android overlay is attached to the current
Activity's window (window type `TYPE_APPLICATION_ABOVE_SUB_PANEL`) rather than
through `SYSTEM_ALERT_WINDOW`, and the iOS overlay is a dedicated passthrough
window above the app's key window.

## Usage

```ts
import { ReactNativePerfStats } from '@onekeyfe/react-native-perf-stats';

// 1 Hz sampling. Also starts the JS rAF tracker, so `jsFps` is populated.
ReactNativePerfStats.start(1000);

// Optional: show the floating overlay (drag to reposition).
ReactNativePerfStats.showOverlay();

const sample = await ReactNativePerfStats.sample();
// { cpu: 12.3, rss: 187654144, uiFps: 59, jsFps: 60, timestamp: 1730000000000 }

ReactNativePerfStats.hideOverlay();
ReactNativePerfStats.stop();
```

`start()` is idempotent and only updates the interval; `stop()` also stops the
JS tracker and hides the overlay. The JavaScript wrapper owns the tracker, so
call `start()` / `stop()` rather than managing it by hand.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `start` | `(intervalMs: number) => void` | iOS, Android | Starts the periodic sampler and JS FPS tracker; interval clamped to 200 ms – 24 h. |
| `stop` | `() => void` | iOS, Android | Stops the sampler and JS tracker and hides the overlay. |
| `showOverlay` | `() => void` | iOS, Android | Shows the draggable CPU/RAM overlay; shows `--` until `start()`. |
| `hideOverlay` | `() => void` | iOS, Android | Hides the overlay; sampling continues. |
| `sample` | `() => Promise<PerfSample>` | iOS, Android | One-shot CPU + RSS read without touching the overlay timer. |
| `setJsFpsHint` | `(fps: number) => void` | iOS, Android | Low-level push of the latest JS frame rate; prefer `startJsFpsTracker()`. |
| `startJsFpsTracker` | `(reportIntervalMs?: number) => void` | iOS, Android | Starts the JS rAF ticker alone (default 1000 ms); managed automatically by `start()`. |
| `stopJsFpsTracker` | `() => void` | iOS, Android | Stops the JS rAF ticker. |
| `addMemoryWarningListener` | `(callback: (event: MemoryWarningEvent) => void) => number` | iOS, Android | Subscribes to OS memory pressure; returns an id. |
| `removeMemoryWarningListener` | `(id: number) => void` | iOS, Android | Unsubscribes; no-op for unknown ids. |
| `cleanupNativeCaches` | `() => void` | iOS, Android | Runs the native reclaim path on demand (iOS: URLCache/WK caches; Android: GC hint). |
| `forceGarbageCollection` | `() => boolean` | iOS, Android | Best-effort Hermes GC hint; `true` only when a GC binding was invoked. |

`PerfSample` fields: `cpu` (percent of one core, may exceed 100 and is `0` for
the first sample), `rss` (bytes; iOS `phys_footprint`, Android `VmRSS`), `uiFps`
and `jsFps` (frames per second, `0` until a window has elapsed), and `timestamp`
(ms since Unix epoch). `MemoryWarningEvent` carries `level` (`low` or
`critical`), `rss` and `timestamp`.

The native interface lives in
[`src/ReactNativePerfStats.nitro.ts`](src/ReactNativePerfStats.nitro.ts) and the
JS wrapper in [`src/index.tsx`](src/index.tsx).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| CPU and RSS sampling | ✅ | ✅ | — |
| UI FPS | ✅ | ✅ | — |
| JS FPS (rAF ticker) | ✅ | ✅ | — |
| Draggable overlay | ✅ | ✅ | — |
| Memory-warning events | ✅ `critical` only | ✅ `low` and `critical` | — |
| Native cache cleanup | ✅ | ✅ | — |
| JS garbage-collection hint | ⚠️ best effort | ⚠️ best effort | — |

## Caveats

> [!IMPORTANT]
> `uiFps` and `jsFps` are only populated while the sampler is running. One-shot
> `sample()` calls made before `start()` or after `stop()` report `0` for both,
> and the first sample after process launch reports `0` CPU because there is no
> baseline yet.

> [!WARNING]
> Always pair `addMemoryWarningListener()` with `removeMemoryWarningListener()`
> in effect cleanup. The native listener table is process-wide and survives
> `stop()` and Fast Refresh, so a dropped subscription keeps the old callback
> (and everything it captures) alive.

> [!NOTE]
> Memory warnings also run the native reclaim path: on iOS this clears
> `URLCache.shared` and the WK HTTP / AppCache / Service-Worker stores before the
> JS callback fires. Do not subscribe if that cached state is load-bearing.

## References

- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-perf-stats)
- [Native interface](src/ReactNativePerfStats.nitro.ts)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
