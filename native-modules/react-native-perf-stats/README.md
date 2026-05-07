# @onekeyfe/react-native-perf-stats

Native CPU, RAM, UI-FPS and JS-FPS sampler for React Native, with an optional draggable on-screen overlay. Built on [Nitro Modules](https://nitro.margelo.com/) — the sampler runs on a dedicated background thread so the overlay keeps updating even when the JS thread is frozen.

- **Android**: HandlerThread + `/proc/self/stat` (CPU ticks) + `/proc/self/statm` (RSS) + `Choreographer.FrameCallback` (UI FPS); overlay attached via `WindowManager` to the current Activity (no `SYSTEM_ALERT_WINDOW` permission).
- **iOS**: GCD dispatch source + `task_info` (`phys_footprint`) + `CADisplayLink` (UI FPS); overlay added as a `UILabel` on the key `UIWindow`.
- **JS FPS**: opt-in JS-side `requestAnimationFrame` ticker that pushes its per-second count to native via `setJsFpsHint`. Started by `startJsFpsTracker()`.

## Installation

```sh
yarn add @onekeyfe/react-native-perf-stats react-native-nitro-modules
```

`react-native-nitro-modules` is a required peer dependency.

## Usage

```ts
import { ReactNativePerfStats } from '@onekeyfe/react-native-perf-stats';

// Start sampling at 1 Hz. Idempotent — calling again just updates the interval.
// Also kicks off the JS-side rAF tracker so PerfSample.jsFps is populated.
ReactNativePerfStats.start(1000);

// Show the floating overlay (drag to reposition).
ReactNativePerfStats.showOverlay();

// One-shot read without touching the overlay timer.
const sample = await ReactNativePerfStats.sample();
console.log(sample);
// { cpu: 12.3, rss: 187654144, uiFps: 59, jsFps: 60, timestamp: 1730000000000 }

// Tear down. Also stops the JS-side rAF tracker.
ReactNativePerfStats.hideOverlay();
ReactNativePerfStats.stop();
```

### `PerfSample`

| field       | unit                  | notes                                                                                                  |
| ----------- | --------------------- | ------------------------------------------------------------------------------------------------------ |
| `cpu`       | percent of one core   | `(Δcpu / Δwall) * 100`. May exceed 100 on multi-core saturation. First sample after launch is `0`.     |
| `rss`       | bytes                 | iOS `phys_footprint`, Android `VmRSS`.                                                                 |
| `uiFps`     | frames per second     | UI thread frame rate over the last sampling window. `0` until at least one window has elapsed.         |
| `jsFps`     | frames per second     | JS thread rAF count. `0` unless `startJsFpsTracker()` has been called and reported at least once.      |
| `timestamp` | ms since unix epoch   | Wall-clock at sample time.                                                                             |

### API

- `start(intervalMs: number): void` — minimum interval is clamped to 200 ms. Also starts the JS-side rAF tracker (matched to `intervalMs`) so `jsFps` flows automatically.
- `stop(): void` — stops the JS-side tracker, the native sampler, and hides the overlay.
- `showOverlay(): void` / `hideOverlay(): void` — overlay shows `--` until `start` runs.
- `sample(): Promise<PerfSample>` — runs off the JS thread, shares baseline with the overlay sampler.
- `setJsFpsHint(fps: number): void` — low-level escape hatch; normally driven by the auto-managed tracker.
- `startJsFpsTracker(reportIntervalMs?: number): void` / `stopJsFpsTracker(): void` — manual control of the JS-side rAF tracker. Useful if you want JS FPS without enabling the native sampler. Calling `startJsFpsTracker` with a different `reportIntervalMs` while running restarts the loop with the new interval.

### Anomaly logging

While the sampler is running, the native side emits a warn to `@onekeyfe/react-native-native-logger` (`OneKeyLog.warn`, tag `PerfStats`) whenever a metric stays over its threshold for **5 consecutive samples**. Each category has an independent **30 s cooldown** so a sustained spike produces one log every 30 s rather than one per sample.

| metric | threshold        |
| ------ | ---------------- |
| CPU    | ≥ 150 %          |
| RSS    | ≥ 800 MB         |
| UI FPS | ≤ 45 fps (and > 0) |
| JS FPS | ≤ 30 fps (and > 0) |

One-off `sample()` calls do **not** trip this path — only the periodic timer started by `start()` does.

## License

MIT
