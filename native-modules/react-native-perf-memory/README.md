<div align="center">

# @onekeyfe/react-native-perf-memory

**Sample the app process's current memory footprint on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-perf-memory?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-perf-memory)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-perf-memory?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

A small Nitro HybridObject whose only method, `getMemoryUsage()`, returns the
current native process memory as `{ rss }` in bytes. iOS reads Mach
`phys_footprint` with a resident-size fallback; Android reads `VmRSS` from
`/proc/self/status` with a total-PSS fallback. The number covers the whole
process, not just the JavaScript heap, and the package never samples on a timer:
the caller owns frequency, aggregation and presentation.

## Features

- **On-demand native sample** — one promise per measurement, with no background
  timers, no uploads and no retained history.
- **Closest available metric per platform** — `phys_footprint` on iOS, `VmRSS`
  on Android, each with a defined fallback.
- **Byte units** — one `number` in bytes under the historical `rss` field;
  convert at the display layer.
- **Process-wide** — the result reflects the whole app process regardless of
  which JS runtime asks.
- **Minimal surface** — a single method and a single exported type to wrap.

## Installation

```sh
yarn add @onekeyfe/react-native-perf-memory react-native-nitro-modules
```

The measurement is native, so install the application's iOS pods and rebuild the
native binary; a JavaScript-only update does not register the module. No platform
permission is required.

## Usage

```ts
import { ReactNativePerfMemory } from '@onekeyfe/react-native-perf-memory';

const { rss } = await ReactNativePerfMemory.getMemoryUsage();
const memoryMiB = rss / (1024 * 1024);

// Record time in the caller if a time series is needed.
const sample = { at: Date.now(), memoryMiB };
```

Sample from a bounded diagnostic interval rather than on every render, and label
the platform and measurement kind alongside stored values.

## API

| Export | Signature | Platforms | Description |
| --- | --- | --- | --- |
| `ReactNativePerfMemory.getMemoryUsage()` | `() => Promise<MemoryUsage>` | iOS, Android | Reads the current process memory in bytes. |
| `MemoryUsage` | `{ rss: number }` | iOS, Android | Result type; the value is bytes, not MiB. |

The measurement source differs by platform:

| Platform | Preferred sample | Fallback |
| --- | --- | --- |
| iOS | Mach `phys_footprint` | Mach resident size, then `0`. |
| Android | `/proc/self/status` `VmRSS` | `ActivityManager` total PSS (KiB converted to bytes), then `0`. |

There is no JavaScript-heap breakdown, allocation count or sampling timestamp.
See [`src/ReactNativePerfMemory.nitro.ts`](src/ReactNativePerfMemory.nitro.ts)
for the interface.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| On-demand process memory sample | ✅ | ✅ | — |
| `phys_footprint` source | ✅ | — | — |
| `VmRSS` / PSS source | — | ✅ | — |
| Background sampling or reporting | — | — | — |

## Caveats

> [!WARNING]
> A `0` result means the measurement could not be obtained, not that the app uses
> no memory. Treat it as an unavailable sample and exclude it from averages.

> [!IMPORTANT]
> iOS and Android report different metrics (`phys_footprint` versus `VmRSS`).
> Do not compare their values as if they used identical accounting; record the
> platform and source with every sample.

> [!NOTE]
> The sample is process-wide and has no timestamp. Keep sampling cadence,
> aggregation and any time-series key in the caller, and avoid reading it on
> every render.

## References

- [Native interface](src/ReactNativePerfMemory.nitro.ts)
- [Example page](../../example/react-native/pages/PerfMemoryTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
