# @onekeyfe/react-native-network-throttle

Native network throttling configuration for OneKey diagnostics on iOS and
Android. The wrapper exposes the current configuration and partial updates,
including latency, bandwidth and the host list used by native throttling.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-network-throttle
```

Install the application's iOS pods and rebuild the native application so
`OneKeyNetworkThrottle` is linked.

```ts
import { NetworkThrottle } from '@onekeyfe/react-native-network-throttle';

const config = await NetworkThrottle.getConfig();
// Integrate changes through the application's existing diagnostic controls.
```

## Configuration API

`getConfig()` returns `enabled`, `profile`, `latencyMs`, `downloadBps`,
`uploadBps` and `throttleUrlHosts`. The public profile type currently contains
`slow4g`. Exported constants provide its latency and bandwidth presets.

`setConfig(partialConfig)` reads the current native configuration, merges
omitted fields and returns the applied configuration. Bandwidth fields use
bytes per second. An omitted host list from native is normalized to an empty
array. The interface does not expose individual request progress or completion.

The application owns enabling/disabling diagnostic throttling and should
validate effects on the actual native request path it uses. A returned config
is not a measurement of achieved throughput. See [src/index.tsx](src/index.tsx)
for the public types, constants and native-linking failure behavior.

## Upstream attribution

React Native native network throttle for OneKey iOS and Android development settings.

Current scope is RN HTTP(S) latency and upload/download throughput. It does not
emulate offline mode, WebView traffic, or third-party native networking stacks.

`throttleUrlHosts` is an allowlist: when it is non-empty, only requests whose
host matches are throttled, and everything else is left untouched. An entry is
either an exact host or `*.example.com`, which matches sub-domains at any depth
but not the bare apex. An empty allowlist throttles nothing.

Hosts are registered additively for the lifetime of the native process, so
independently initialized React Native runtimes cannot clear each other's
configuration.

This package only owns native request throttling. Product settings, persistence, and UI controls should remain in the host app.
