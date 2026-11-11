<div align="center">

# @onekeyfe/react-native-network-throttle

**Simulate slow network conditions for React Native's own HTTP stack, on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-network-throttle?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-network-throttle)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-network-throttle?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

A diagnostics helper that adds latency and caps download/upload throughput on
React Native's fetch/XHR traffic: on iOS through a custom `NSURLProtocol` in the
RN session configuration, on Android through an OkHttp interceptor installed via
`OkHttpClientProvider`. Configuration is read and written at runtime through
`getConfig`/`setConfig`; this package owns only native request throttling, while
product settings, persistence and UI controls belong to the host app. It does not
emulate offline mode, WebView traffic or third-party native networking stacks.

## Features

- **Latency and throughput control** — set `latencyMs`, `downloadBps` and
  `uploadBps` independently, in bytes per second.
- **`slow4g` preset** — exported constants for the built-in profile: 562.5 ms
  latency and 102 KiB/s in both directions.
- **Host allowlist** — throttle only matching hosts with exact names or
  `*.example.com` patterns, so unrelated traffic keeps its normal speed.
- **Partial updates** — `setConfig` merges omitted fields with the current native
  configuration and returns the applied values.
- **Runtime-toggleable** — flip `enabled` without reinstalling or rebuilding.

## Installation

```sh
yarn add @onekeyfe/react-native-network-throttle
```

The package ships native code and hooks React Native's HTTP stack when the
native module loads. Install the application's iOS pods and rebuild the native
binary; a JavaScript-only update does not link `OneKeyNetworkThrottle`.
No platform permission is required.

## Usage

```ts
import { NetworkThrottle } from '@onekeyfe/react-native-network-throttle';

await NetworkThrottle.setConfig({
  enabled: true,
  profile: 'slow4g',
  latencyMs: 562.5,
  downloadBps: 102 * 1024,
  uploadBps: 102 * 1024,
  throttleUrlHosts: ['api.example.com', '*.cdn.example.com'],
});

const config = await NetworkThrottle.getConfig();
// config.throttleUrlHosts is always an array, never undefined.
```

Keep the enabling, persistence and presentation of these values in the app's
diagnostic settings; the package stores config in native process memory only.

## API

| Export | Signature | Platforms | Description |
| --- | --- | --- | --- |
| `NetworkThrottle.getConfig()` | `() => Promise<NetworkThrottleConfig>` | iOS, Android | Returns the current native configuration. |
| `NetworkThrottle.setConfig(config)` | `(config: Partial<NetworkThrottleConfig>) => Promise<NetworkThrottleConfig>` | iOS, Android | Merges the update into the current config and returns what was applied. |
| `NetworkThrottleConfig` | type | iOS, Android | `{ enabled, profile, latencyMs, downloadBps, uploadBps, throttleUrlHosts }`. |
| `NetworkThrottleProfile` | type | iOS, Android | Currently only `'slow4g'`. |
| `NETWORK_THROTTLE_SLOW_4G_LATENCY_MS` | `562.5` | iOS, Android | Preset latency in milliseconds. |
| `NETWORK_THROTTLE_SLOW_4G_DOWNLOAD_BPS` / `_UPLOAD_BPS` | `104448` | iOS, Android | Preset throughput in bytes per second (102 KiB/s). |

Bandwidth values are bytes per second, not bits. The default export is the same
object as the named `NetworkThrottle` export. `getConfig()` normalizes a missing
native `throttleUrlHosts` to `[]`, so consumers can iterate it directly.
The module exposes no per-request progress or completion events. If the native
module is not linked, accessing `NetworkThrottle` throws an integration error
with rebuild guidance.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Latency injection | ✅ `NSURLProtocol` | ✅ OkHttp interceptor | — |
| Download / upload throttling | ✅ | ✅ | — |
| Host allowlist | ✅ | ✅ | — |
| Runtime `enabled` toggle | ✅ | ✅ | — |
| WebView or third-party native traffic | — | — | — |

Web, desktop and extension targets receive no implementation; the proxy thrown
on native-linking failure is a runtime error, not a fallback.

## Caveats

> [!WARNING]
> Throttling covers React Native's own fetch/XHR stack only. WebView traffic,
> third-party native networking and offline mode are unaffected, so a green
> result here does not validate every network path in the app.

> [!NOTE]
> Hosts are registered additively for the lifetime of the native process and
> cannot be removed. An empty `throttleUrlHosts` list throttles nothing, but it
> does not clear hosts registered earlier in the same process.

> [!IMPORTANT]
> `setConfig` performs a read followed by a write; it is not an atomic
> compare-and-set across independent callers. Coordinate concurrent
> configuration changes in the application and re-read the config after writing.

## References

- [Public wrapper and types](src/index.tsx)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-network-throttle)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
