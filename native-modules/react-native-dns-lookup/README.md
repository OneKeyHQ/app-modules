# @onekeyfe/react-native-dns-lookup

Native hostname resolution for OneKey React Native applications on iOS and
Android. The wrapper returns the addresses resolved by the device's native
network stack, without choosing an application connection or transport.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-dns-lookup
```

Install the application's iOS pods and rebuild the native application.

```ts
import { getIpAddressesForHostname } from '@onekeyfe/react-native-dns-lookup';

const addresses = await getIpAddressesForHostname('example.com');
```

## API

`getIpAddressesForHostname(hostname)` returns `Promise<string[]>`. The named
`DnsLookup` export exposes the underlying `getIpAddresses(hostname)` method,
and `DnsLookupSpec` provides the interface type.

Treat the returned addresses as resolver output rather than a guarantee that a
service is reachable. Resolution can fail, and a network or DNS change can alter
subsequent results. The application owns retry, address selection and any cache
it builds above this API; the wrapper does not expose those policies.

The JavaScript wrapper is in [src/index.tsx](src/index.tsx), and the native
interface is in [src/NativeDnsLookup.ts](src/NativeDnsLookup.ts). Integrations
should handle rejection and validate actual connections independently of DNS.

## Upstream attribution

First, a sincere thank-you to the
`react-native-dns-lookup` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[nicola/react-native-dns-lookup](https://github.com/nicola/react-native-dns-lookup).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-dns-lookup`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-dns-lookup` to keep
development and delivery stable.

## Upstream Project

- Repository: [nicola/react-native-dns-lookup](https://github.com/nicola/react-native-dns-lookup)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-dns-lookup` 💙
