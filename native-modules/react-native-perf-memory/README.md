# @onekeyfe/react-native-perf-memory

Native process memory sampling for OneKey React Native applications on iOS and
Android. The Nitro module returns one numeric byte count under the historical
`rss` field. Callers own sampling frequency, aggregation and presentation;
the package does not start a recurring sampler or upload measurements.

## Installation

```sh
yarn add @onekeyfe/react-native-perf-memory react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application. The
measurement is performed by native code and requires the Nitro runtime.

## Usage

```ts
import { ReactNativePerfMemory } from '@onekeyfe/react-native-perf-memory';

const { rss } = await ReactNativePerfMemory.getMemoryUsage();
const memoryMiB = rss / (1024 * 1024);
```

## Measurement semantics

`getMemoryUsage()` returns `Promise<{ rss: number }>` with the value in bytes.
Despite the shared field name, the preferred measurements differ:

| Platform | Preferred sample | Fallback |
| --- | --- | --- |
| iOS | Mach `phys_footprint` | Mach resident size, then zero. |
| Android | `/proc/self/status` VmRSS | ActivityManager total PSS converted from KiB, then zero. |

A zero result can indicate that a measurement could not be obtained. It is not
proof that the application has no memory usage. The number covers native process
memory rather than only the JavaScript heap, and should not be compared across
platforms as if both use an identical accounting method.

Use bounded sampling intervals suited to diagnostics rather than requesting a
sample on every render. The exported interface is defined in
[src/ReactNativePerfMemory.nitro.ts](src/ReactNativePerfMemory.nitro.ts).

## License

MIT.
