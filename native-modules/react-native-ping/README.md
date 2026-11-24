<div align="center">

# @onekeyfe/react-native-ping

**One-shot ICMP ping diagnostics for OneKey apps on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-ping?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-ping)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-ping?label=license&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-ping)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

A TurboModule that sends a single ICMP echo to a host and resolves the
round-trip time in milliseconds. The caller supplies the address and optional
timeout/payload settings; the module does not retry, cancel, monitor or report
intermediate events. iOS uses the vendored `GBPing`, Android uses
`InetAddress.isReachable`. There is no Web implementation.

## Features

- **One call, one answer** — `start()` resolves the round-trip time in
  milliseconds or rejects; there is no event stream to subscribe to.
- **Tunable attempt** — a `timeout` in milliseconds and, on iOS, an ICMP
  `payloadSize` in bytes.
- **Off the JS thread** — iOS pings on a serial queue, Android on its own
  background thread.
- **Explicit failure, no retained state** — unreachable hosts and timeouts reject
  the promise, and there is nothing to stop or dispose afterwards.

## Installation

```sh
yarn add @onekeyfe/react-native-ping
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking. The TurboModule is resolved at import time, so a JavaScript-only
install fails at `import` instead of at the first call. On Android the host check
runs through `InetAddress`, so the application must declare the normal
`INTERNET` permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

No additional iOS entitlement is required.

## Usage

```ts
import { Ping } from '@onekeyfe/react-native-ping';

// `option` is required by the bridge signature; pass {} for defaults.
const rttMs = await Ping.start('1.1.1.1', { timeout: 3000 });
```

Keep each result associated with the address and network conditions of that
call. Retry policy, cancellation of the surrounding workflow and presentation of
the result belong to the application.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `Ping.start` | `(ipAddress: string, option: { timeout?: number; payloadSize?: number }) => Promise<number>` | iOS, Android | Sends one ICMP echo; resolves round-trip time in milliseconds. |

`option` fields:

| Field | Type | Default | Description |
| --- | --- | --- | --- |
| `timeout` | `number` | iOS `1000`, Android `3000` | Maximum wait in milliseconds; on expiry the promise rejects. |
| `payloadSize` | `number` | iOS `56` | ICMP payload size in bytes; ignored on Android. |

`PingSpec` is the exported TypeScript interface. The wrapper is
[`src/index.tsx`](src/index.tsx); the bridge is [`src/NativePing.ts`](src/NativePing.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Send one ICMP echo and resolve RTT | ✅ | ✅ | — |
| `timeout` option | ✅ | ✅ | — |
| `payloadSize` option | ✅ | — | — |
| Continuous monitoring, retry or cancellation | — | — | — |

## Caveats

> [!IMPORTANT]
> Platform defaults differ: `timeout` defaults to `1000` ms on iOS and `3000` ms
> on Android, and `payloadSize` is only honoured on iOS. Pass `timeout`
> explicitly when behaviour must match across platforms.

> [!NOTE]
> Android uses `InetAddress.isReachable`, which may fall back to a TCP echo on
> port 7 when ICMP is blocked. A successful Android result therefore does not
> prove that ICMP works, and its timing is not comparable with iOS ICMP RTT.

> [!WARNING]
> Importing the package resolves the native module eagerly; without the rebuilt
> pods or Android native build, the import throws before any call is made.

## References

- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-ping)
- [Native bridge definition](src/NativePing.ts)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Fork history and bundled third-party notices</summary>

First, a sincere thank-you to the `react-native-ping` maintainers for their
excellent work 🙏

This package is built on, and inspired by,
[RoJoHub/react-native-ping](https://github.com/RoJoHub/react-native-ping).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-ping`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-ping` to keep development
and delivery stable.

### Upstream Project

- Repository: [RoJoHub/react-native-ping](https://github.com/RoJoHub/react-native-ping)
- The RoJoHub package manifest declares MIT; that repository does not provide a
  full MIT grant file. This limitation is retained in the package audit.

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to `react-native-ping` 💙

### Bundled third-party notices

The `ios/GBPing` implementation derives from
[lmirosevic/GBPing](https://github.com/lmirosevic/GBPing/tree/bb1156f1c4425981f4cf495952623935d6e124d2)
and is covered by [LICENSE.GBPing](LICENSE.GBPing), the upstream Apache 2.0
license. Upstream attributes GBPing to Luka Mirosevic (2015); the retained
`ICMPHeader.h` also names Goonbee (2012). OneKey's native integration contains
local modifications. There is no upstream NOTICE file at that revision.

`ios/LHNetwork/LHDefinition.h` retains its Pomato (2019) source notice and
matches the RoJoHub tree. The package manifest records `MIT AND Apache-2.0` to
distinguish the declared primary license from the known vendored license. No
missing upstream MIT copyright or full grant is fabricated.

</details>

## License

The package manifest declares `MIT AND Apache-2.0`: the wrapper is MIT, and the
vendored iOS `GBPing` sources are Apache-2.0, retained in
[LICENSE.GBPing](LICENSE.GBPing). This package ships no standalone `LICENSE`
file; see the [npm page](https://www.npmjs.com/package/@onekeyfe/react-native-ping)
for the published manifest.
