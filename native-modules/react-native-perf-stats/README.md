# @onekeyfe/react-native-perf-stats

Native CPU and RAM sampler for React Native, with an optional draggable on-screen overlay. Built on [Nitro Modules](https://nitro.margelo.com/) — the sampler runs on a dedicated background thread so the overlay keeps updating even when the JS thread is frozen.

- **Android**: HandlerThread + `/proc/self/stat` (CPU ticks) + `/proc/self/statm` (RSS); overlay attached via `WindowManager` to the current Activity (no `SYSTEM_ALERT_WINDOW` permission).
- **iOS**: GCD dispatch source + `task_info` (`phys_footprint`); overlay added as a `UILabel` on the key `UIWindow`.

## Installation

```sh
yarn add @onekeyfe/react-native-perf-stats react-native-nitro-modules
```

`react-native-nitro-modules` is a required peer dependency.

## Usage

```ts
import { ReactNativePerfStats } from '@onekeyfe/react-native-perf-stats';

// Start sampling at 1 Hz. Idempotent — calling again just updates the interval.
ReactNativePerfStats.start(1000);

// Show the floating overlay (drag to reposition).
ReactNativePerfStats.showOverlay();

// One-shot read without touching the overlay timer.
const sample = await ReactNativePerfStats.sample();
console.log(sample); // { cpu: 12.3, rss: 187654144, timestamp: 1730000000000 }

// Tear down.
ReactNativePerfStats.hideOverlay();
ReactNativePerfStats.stop();
```

### `PerfSample`

| field       | unit                       | notes                                                                                                  |
| ----------- | -------------------------- | ------------------------------------------------------------------------------------------------------ |
| `cpu`       | percent of one core        | `(Δcpu / Δwall) * 100`. May exceed 100 on multi-core saturation. First sample after launch is `0`.     |
| `rss`       | bytes                      | iOS `phys_footprint`, Android `VmRSS`.                                                                 |
| `timestamp` | ms since unix epoch        | Wall-clock at sample time.                                                                             |

### API

- `start(intervalMs: number): void` — minimum interval is clamped to 200 ms.
- `stop(): void` — also hides the overlay.
- `showOverlay(): void` / `hideOverlay(): void` — overlay shows `--` until `start` runs.
- `sample(): Promise<PerfSample>` — runs off the JS thread, shares baseline with the overlay sampler.

## License

MIT
