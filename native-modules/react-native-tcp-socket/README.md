<div align="center">

# @onekeyfe/react-native-tcp-socket

**One-shot TCP connection probes with a measured connect time.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-tcp-socket?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-tcp-socket)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-tcp-socket?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

Opens a TCP connection to a host and port, then closes it and resolves the
elapsed connection time in milliseconds. A small `createConnection`
compatibility shim is included for call sites written against the upstream
`react-native-tcp-socket` API. This is a diagnostic probe, not a streaming
socket: no `data`, `write`, `end`, TLS or socket handle is exposed. There is no
Web implementation.

## Features

- **One call, one measurement** — `connectWithTimeout(host, port, timeoutMs)`
  resolves the connect time in milliseconds or rejects with the failure.
- **Bounded connect wait** — the wait is cut off at `timeoutMs` and rejects
  instead of hanging the caller.
- **No retained connection** — the socket is opened and closed inside the call;
  there is nothing to keep alive or dispose.
- **Compatibility shim** — `createConnection(options, callback)` supports
  `on('error')`, `on('timeout')` and `destroy()` for simple call sites.
- **Typed native failures on iOS** — `DNS_ERROR`, `CONNECT_ERROR`, `ETIMEDOUT`,
  `SOCKET_ERROR` and `SELECT_ERROR` distinguish the failure stage.

## Installation

```sh
yarn add @onekeyfe/react-native-tcp-socket
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking. The application must declare the normal `INTERNET` permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

No additional iOS permission or entitlement is required.

## Usage

```ts
import { NativeTcpSocket } from '@onekeyfe/react-native-tcp-socket';

// Resolves the time spent resolving and connecting, in milliseconds.
const connectMs = await NativeTcpSocket.connectWithTimeout('1.1.1.1', 443, 5000);
```

Keep the result associated with the host, port and network conditions of that
call. Retry policy and the meaning of the measurement belong to the caller.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `NativeTcpSocket.connectWithTimeout` | `(host: string, port: number, timeoutMs: number) => Promise<number>` | iOS, Android | Attempts one TCP connection and resolves its elapsed time in ms. |
| default `createConnection` | `(options: { host, port, timeout? }, connectCallback: () => void) => TcpSocketShim` | iOS, Android | Compatibility shim over the promise API; default timeout `5000` ms. |
| `TcpSocketShim.on` | `(event, handler) => this` with event `error` or `timeout` | iOS, Android | Registers the current error or timeout callback. |
| `TcpSocketShim.destroy` | `() => void` | iOS, Android | Suppresses later shim callbacks; it does not cancel the native attempt. |

The TypeScript interface is in
[`src/NativeTcpSocket.ts`](src/NativeTcpSocket.ts); the shim is in
[`src/index.tsx`](src/index.tsx).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| One-shot TCP connect probe | ✅ | ✅ | — |
| Measured connect time in ms | ✅ | ✅ | — |
| Connect timeout | ✅ `select()` deadline | ✅ `Socket.connect` timeout | — |
| Streaming read/write API | — | — | — |
| Native cancellation | — | — | — |
| `createConnection` shim | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> This package is a connectivity diagnostic, not a socket implementation. A
> successful connect does not verify TLS, an application protocol or an
> authenticated service, and `destroy()` suppresses callbacks without cancelling
> the native attempt.

> [!WARNING]
> On Android every native failure rejects with the single code
> `TCP_SOCKET_ERROR`. The shim classifies timeouts by message text, so Android
> `connect timed out` rejections arrive through the `error` handler rather than
> the `timeout` handler.

> [!NOTE]
> `timeoutMs` bounds the connection wait only. DNS resolution happens before the
> timed wait on both platforms, and iOS attempts only the first address returned
> by `getaddrinfo` (over an IPv4 socket), so an IPv6-first host can reject with
> `CONNECT_ERROR`.

## References

- [Native interface](src/NativeTcpSocket.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-tcp-socket)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Fork history and upstream project</summary>

First, a sincere thank-you to Rapsssito and the
`react-native-tcp-socket` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[Rapsssito/react-native-tcp-socket](https://github.com/Rapsssito/react-native-tcp-socket).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-tcp-socket`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-tcp-socket` to keep
development and delivery stable.

### Upstream Project

- Repository: [Rapsssito/react-native-tcp-socket](https://github.com/Rapsssito/react-native-tcp-socket)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Rapsssito and everyone who contributes to
`react-native-tcp-socket` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
