# @onekeyfe/react-native-ping

Native ping diagnostics for OneKey React Native applications on iOS and
Android. The module exposes one asynchronous operation for a supplied IP
address with optional timeout and payload-size settings.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-ping
```

Install the application's iOS pods and rebuild the native application before
calling the TurboModule.

```ts
import { Ping } from '@onekeyfe/react-native-ping';

const result = await Ping.start('1.1.1.1', {});
```

## API and integration

`start(ipAddress, { timeout?, payloadSize? })` returns `Promise<number>`.
`PingSpec` exports its TypeScript interface. The application supplies the target
IP address and owns retry, cancellation of its own surrounding workflow and
presentation of the diagnostic result.

Handle rejected promises and test network conditions on the target devices.
A ping result should not be used as proof that a particular HTTP endpoint,
TLS handshake or authenticated service request succeeds. Some networks restrict
ping independently of application traffic.

The wrapper is in [src/index.tsx](src/index.tsx), with the bridge defined in
[src/NativePing.ts](src/NativePing.ts). Native behavior remains platform-specific;
this wrapper does not add a continuous network monitor or a Web implementation.

## Upstream attribution

First, a sincere thank-you to the
`react-native-ping` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[nicola/react-native-ping](https://github.com/nicola/react-native-ping).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-ping`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-ping` to keep
development and delivery stable.

## Upstream Project

- Repository: [nicola/react-native-ping](https://github.com/nicola/react-native-ping)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-ping` 💙
