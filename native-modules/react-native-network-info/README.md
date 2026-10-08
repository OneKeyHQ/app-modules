# @onekeyfe/react-native-network-info

Native network information queries for OneKey on iOS and Android. The
TurboModule exposes device network addresses and Wi-Fi-related values; it does
not initiate a connection or monitor reachability continuously.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-network-info
```

Install the application's iOS pods and rebuild the native application. Configure
any platform permissions needed for the network information your consumer uses.

```ts
import { NetworkInfo } from '@onekeyfe/react-native-network-info';

const address = await NetworkInfo.getIPAddress();
const ssid = await NetworkInfo.getSSID();
```

## API and results

Address methods include `getIPAddress`, `getIPV4Address`, `getIPV6Address`,
`getWIFIIPV4Address`, `getGatewayIPAddress` and `getSubnet`.
`getSSID`, `getBSSID` and `getBroadcast` return nullable strings;
`getFrequency` returns a nullable number.

Availability depends on the platform, network and permission state. Handle
missing values and promise failures rather than assuming Wi-Fi data is always
present. An address or SSID is not evidence that Internet access is available.
The application owns refresh frequency and interpretation of returned values.

See [src/NativeNetworkInfo.ts](src/NativeNetworkInfo.ts) for the full interface.
The package exports the native object as `NetworkInfo` and its type as
`NetworkInfoSpec`.

## Upstream attribution

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

## Upstream Project

- Repository: [pusherman/react-native-network-info](https://github.com/pusherman/react-native-network-info)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-network-info` 💙
