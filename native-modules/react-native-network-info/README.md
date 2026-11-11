<div align="center">

# @onekeyfe/react-native-network-info

**Read-only network and Wi-Fi information for React Native on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-network-info?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-network-info)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-network-info?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

A TurboModule that exposes the device's current network addresses and Wi-Fi
details through promise-based getters: IP, IPv4, IPv6, Wi-Fi IPv4, gateway,
subnet, broadcast, SSID, BSSID and channel frequency. It reads OS state and
nothing more — it opens no connection, monitors no reachability and emits no
network-change events. Availability depends on the platform, the active network
and permission state, so callers must treat nullable values and rejections as
normal outcomes.

## Features

- **Separate address readings** — full IP, IPv4, IPv6, Wi-Fi IPv4, gateway and
  subnet getters instead of one ambiguous "IP" value.
- **Wi-Fi detail** — `getSSID`, `getBSSID`, `getBroadcast` and `getFrequency`
  return the current interface state when the OS allows reading it.
- **Null over guessing** — Wi-Fi names and frequency resolve `null` when the OS
  withholds them, rather than a sentinel string.
- **No side effects** — no socket, no request, no camera and no view mount; each
  call is a single native query.
- **Mac Catalyst compatible** — the IPv4 gateway helper compiles against the
  Catalyst SDK's routing structures.

## Installation

```sh
yarn add @onekeyfe/react-native-network-info
```

The package ships native code. Install the application's iOS pods and rebuild the
native binary; a JavaScript-only update does not register the TurboModule.
Declare the platform permissions your consumer needs — reading Wi-Fi names on
both platforms requires location permission and enabled location services, and
the application owns those declarations and their user-facing explanation.

## Usage

```ts
import { NetworkInfo } from '@onekeyfe/react-native-network-info';

const ipAddress = await NetworkInfo.getIPAddress();
const gateway = await NetworkInfo.getGatewayIPAddress();
const ssid = await NetworkInfo.getSSID(); // string or null

if (ssid === null) {
  // Wi-Fi name unavailable: no Wi-Fi, or location permission/state missing.
}
```

Refresh the values yourself when needed; the module holds no cache and offers no
change callback.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `getIPAddress()` / `getIPV4Address()` / `getIPV6Address()` | `Promise<string>` | iOS, Android | Active interface, IPv4 and IPv6 addresses. |
| `getWIFIIPV4Address()` | `Promise<string>` | iOS, Android | Wi-Fi interface IPv4 address. |
| `getGatewayIPAddress()` / `getSubnet()` | `Promise<string>` | iOS, Android | Default gateway and subnet mask. |
| `getBroadcast()` | `Promise<string \| null>` | iOS, Android | Broadcast address, when known. |
| `getSSID()` | `Promise<string \| null>` | iOS, Android | Current Wi-Fi network name. |
| `getBSSID()` | `Promise<string \| null>` | iOS, Android | Current Wi-Fi access point BSSID. |
| `getFrequency()` | `Promise<number \| null>` | iOS, Android | Current Wi-Fi channel frequency. |
| `NetworkInfoSpec` | type | iOS, Android | The native module interface type. |

The package exports the native object as `NetworkInfo` and its interface as
`NetworkInfoSpec`. There is no network-change event API or continuous
reachability monitor: the application owns refresh frequency and interpretation.
See [`src/NativeNetworkInfo.ts`](src/NativeNetworkInfo.ts) for exact signatures.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| IP / IPv4 / IPv6 / Wi-Fi IPv4 addresses | ✅ | ✅ | — |
| Gateway, subnet and broadcast | ✅ | ✅ | — |
| SSID / BSSID | ✅ with location permission | ✅ with location permission | — |
| Channel frequency | ✅ | ✅ | — |

Mac Catalyst builds support the IPv4 gateway helper; the package's Web
implementation is not provided.

## Caveats

> [!NOTE]
> A `null` SSID, BSSID, broadcast or frequency is a valid reading, not an error.
> Keep it distinct from a known value and do not substitute an empty string.

> [!WARNING]
> Address getters resolve a value instead of rejecting when unavailable: iOS
> returns `""` from `getIPAddress` and `"0.0.0.0"` from `getIPV4Address` /
> `getWIFIIPV4Address`, and Android may resolve `null` despite the
> `Promise<string>` type. Validate the value before use. The module neither
> connects nor checks reachability, and an address or SSID is not evidence of
> Internet access: validate connectivity separately and re-read after network
> changes.

> [!IMPORTANT]
> Wi-Fi names require location permission and enabled location services on both
> platforms. Without them the guarded getters resolve `null`; the application
> owns the permission request and its explanation.

## References

- [Package spec](docs/SPEC.md)
- [Native interface](src/NativeNetworkInfo.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-network-info)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Forked from <code>pusherman/react-native-network-info</code> (MIT)</summary>

First, a sincere thank-you to the
`react-native-network-info` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[pusherman/react-native-network-info](https://github.com/pusherman/react-native-network-info).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-network-info`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-network-info` to keep
development and delivery stable.

### Upstream Project

- Repository: [pusherman/react-native-network-info](https://github.com/pusherman/react-native-network-info)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-network-info` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
