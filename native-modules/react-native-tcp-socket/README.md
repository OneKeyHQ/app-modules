# @onekeyfe/react-native-tcp-socket

Native TCP connection diagnostics for OneKey on iOS and Android. The package
provides a promise-based connection attempt and a small legacy-style wrapper;
it is not a general-purpose streaming socket implementation.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-tcp-socket
```

Install the application's iOS pods and rebuild the native application.

```ts
import { NativeTcpSocket } from '@onekeyfe/react-native-tcp-socket';

const connectionMs = await NativeTcpSocket.connectWithTimeout('example.com', 443, 5000);
```

## API and wrapper limits

`connectWithTimeout(host, port, timeoutMs)` resolves with connection time in
milliseconds and rejects on connection failure. Callers own retry and how the
measurement is interpreted. A TCP connection does not verify TLS or an
application-level protocol.

The default export offers `createConnection(options, connectCallback)` with
`on('error', handler)`, `on('timeout', handler)` and `destroy()`. Its default
timeout is 5000 ms. `destroy()` suppresses later wrapper callbacks; it does not
expose native cancellation or a readable/writable socket stream.

Do not assume Node.js socket methods are available on that wrapper. See
[src/index.tsx](src/index.tsx) for its behavior and
[src/NativeTcpSocket.ts](src/NativeTcpSocket.ts) for the native signature.

## Registering wrapper callbacks

The legacy wrapper accepts only `error` and `timeout` event handlers, each
stored as one current callback. Register them immediately on the returned shim
and call `destroy()` when the caller no longer wants completion callbacks.
No `data`, `write`, `end` or native socket handle is exported by this shim.

For a direct measurement use the named `NativeTcpSocket` promise API.
The wrapper's connection callback signals successful connection, without a
stream object or connection-time argument. Use the promise result when the
elapsed connection time is needed.

## Upstream attribution

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

## Upstream Project

- Repository: [Rapsssito/react-native-tcp-socket](https://github.com/Rapsssito/react-native-tcp-socket)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Rapsssito and everyone who contributes to
`react-native-tcp-socket` 💙
