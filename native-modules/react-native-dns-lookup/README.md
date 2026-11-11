<div align="center">

# @onekeyfe/react-native-dns-lookup

**Native hostname resolution on iOS and Android, without choosing a transport.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-dns-lookup?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-dns-lookup)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-dns-lookup?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

A small TurboModule that resolves a hostname with the device's native network
stack and returns the numeric addresses the resolver produced. It hands the
addresses back to JavaScript as strings and stops there: it opens no socket,
sends no request, validates no certificate and measures no latency. Choosing,
ordering, caching and retrying addresses stays with the application's network
policy.

## Features

- **Resolver output, nothing else** — returns IPv4 and/or IPv6 address strings;
  it neither connects to them nor guarantees reachability.
- **Off the JS thread** — iOS resolves through `CFHost`, Android through
  `InetAddress.getAllByName`; both run on a background queue/thread.
- **Promise-first API** — one call, one rejection path, no callbacks to wire.
- **No hidden cache or retry** — every call performs a fresh lookup, so the
  caller owns DNS caching policy.

## Installation

```sh
yarn add @onekeyfe/react-native-dns-lookup
```

The package ships native iOS and Android code. Install the application's iOS
pods and rebuild the native binary; a JavaScript-only update does not register
the TurboModule.

## Usage

```ts
import { getIpAddressesForHostname } from '@onekeyfe/react-native-dns-lookup';

try {
  const addresses = await getIpAddressesForHostname('example.com');
  // e.g. ['93.184.216.34', '2606:2800:220:1:248:1893:25c8:1946']
} catch (error) {
  // Resolution failed; the hostname may be invalid, offline or unresolvable.
}
```

Treat the result as resolver output. Validate the actual connection separately
and keep any address cache scoped to the application's network policy.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `getIpAddressesForHostname` | `(hostname: string) => Promise<string[]>` | iOS, Android | Resolves a hostname and returns its numeric addresses. |
| `DnsLookup` | `Spec` | iOS, Android | The underlying TurboModule; exposes `getIpAddresses(hostname)`. |
| `DnsLookupSpec` | type | iOS, Android | The native module interface type. |

`getIpAddressesForHostname()` and `DnsLookup.getIpAddresses()` are the same
native call. A missing native registration is an integration error at import
time; a rejected resolution promise is an operation failure at call time. iOS
rejects with the `CFHost` error code and Android with `DNS_LOOKUP_ERROR`. There is
no cancellation or cache-clear method.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Resolve a hostname to addresses | ✅ `CFHost` | ✅ `InetAddress` | — |
| IPv4 and IPv6 result strings | ✅ | ✅ | — |
| Cancellation, cache or retry policy | — | — | — |

## Caveats

> [!NOTE]
> A returned address is an input to a separate connection decision, not proof the
> service is reachable. A network or DNS change can make the next call return
> different addresses, and this package exposes no cache invalidation.

> [!WARNING]
> Importing the wrapper enforces TurboModule registration. Calling the API from a
> host binary that does not include the package rejects at import time rather
> than degrading silently.

## References

- [Public interface](src/NativeDnsLookup.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-dns-lookup)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Forked from <code>tableau/react-native-dns-lookup</code> (MIT)</summary>

First, a sincere thank-you to the
`react-native-dns-lookup` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[tableau/react-native-dns-lookup](https://github.com/tableau/react-native-dns-lookup).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-dns-lookup`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-dns-lookup` to keep
development and delivery stable.

### Upstream Project

- Repository: [tableau/react-native-dns-lookup](https://github.com/tableau/react-native-dns-lookup)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-dns-lookup` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
