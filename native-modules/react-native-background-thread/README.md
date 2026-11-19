<div align="center">

# @onekeyfe/react-native-background-thread

**A second JavaScript runtime plus a shared native bridge for OneKey apps on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-background-thread?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-background-thread)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-background-thread?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

Starts a background JavaScript runtime, evaluates code segments inside it, and
connects the two runtimes through a process-wide C++ shared store and one-way RPC
notifications. The package also replaces `react-native-restart` with a `restart()`
call that reloads one or both runtimes. The application hosts the background entry
bundle and wires the iOS delegate; this package does not create the entry bundle or
its module registry.

## Features

- **Dedicated background runtime** — start it from a custom entry URL and keep
  background work off the UI runtime.
- **Shared store across runtimes** — a process bridge for `string`, `number` and
  `boolean` values, readable from either runtime.
- **Value-inline RPC** — `write(callId, value)` delivers the call ID and payload
  together through `onWrite`, with no read-back and no pending-count bookkeeping.
- **Segment evaluation** — `loadSegmentInBackground` evaluates a segment by ID and
  rejects with stable retryable/fatal `SPLIT_BUNDLE_*` codes.
- **Targeted restarts** — `restart('ui', reason)` keeps the background runtime hot;
  `restart('all', reason)` reloads both after an OTA bundle change.
- **Typed surface** — `RestartMode`, `ISharedStore` and `ISharedRPC` are exported
  for consumers.

## Installation

```sh
yarn add @onekeyfe/react-native-background-thread \
  @onekeyfe/react-native-bundle-update \
  @onekeyfe/react-native-native-logger
```

Install the application's iOS pods and rebuild the native application. The package
is a TurboModule and declares `@onekeyfe/react-native-bundle-update` as a peer
dependency; the iOS pod also links `ReactNativeNativeLogger`, so the application's
pods must include that package. No permissions or `Info.plist` keys are required.

### iOS host wiring

The iOS host must install the shared bridge on every main `RCTHost` start —
including after a restart — and start the background runner. The example app's
[`ReactNativeDelegate.mm`](../../example/react-native/ios/example/ReactNativeDelegate.mm)
does exactly this:

```objc
- (void)hostDidStart:(RCTHost *)host
{
  [super hostDidStart:host];
  [BackgroundThreadManager installSharedBridgeInMainRuntime:host];
  [[BackgroundThreadManager sharedInstance] startBackgroundRunnerWithEntryURL:bgURL];
}
```

On Android the JavaScript `installSharedBridge()` call performs the same
installation; on iOS that call is a no-op because the delegate owns it.

## Usage

```ts
import {
  BackgroundThread,
  getSharedStore,
  getSharedRPC,
} from '@onekeyfe/react-native-background-thread';

BackgroundThread.installSharedBridge();

const store = getSharedStore();
store?.set('locale', 'zh-CN');
store?.set('networkId', 42);

const rpc = getSharedRPC();
rpc?.write('main-ready', 1);
rpc?.onWrite((callId, value) => {
  console.log(callId, value);
});

await BackgroundThread.restart('ui', 'language-change');
```

`getSharedStore()` and `getSharedRPC()` return `undefined` when the native bridge
has not been installed in the calling runtime. The application owns which runtime
publishes readiness and which one consumes it.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `startBackgroundRunnerWithEntryURL` | `(entryURL: string) => void` | iOS, Android | Starts the background runtime from a bundle URL. |
| `installSharedBridge` | `() => void` | Android; iOS no-op | Installs the shared bridge in the calling runtime. |
| `loadSegmentInBackground` | `(segmentId: number, path: string) => Promise<void>` | iOS, Android | Evaluates a segment in the background runtime. |
| `restart` | `(mode: 'ui' \| 'all', reason: string) => Promise<void>` | iOS, Android | Reloads the UI runtime, or both runtimes. |
| `getSharedStore` | `() => ISharedStore \| undefined` | iOS, Android | Returns the shared store for this runtime. |
| `SharedStore` | `set`, `get`, `has`, `delete`, `keys`, `clear`, `size` | iOS, Android | Primitive value store shared across runtimes. |
| `getSharedRPC` | `() => ISharedRPC \| undefined` | iOS, Android | Returns the one-way RPC object for this runtime. |
| `SharedRPC` | `write`, `onWrite`, `registerReadinessKey` | iOS, Android | Value-inline notifications without read-back. |

`RestartMode` is `'ui' | 'all'`; any other value rejects. The native interface is
in [`src/NativeBackgroundThread.ts`](src/NativeBackgroundThread.ts), the shared
surfaces in [`src/SharedStore.ts`](src/SharedStore.ts) and [`src/SharedRPC.ts`](src/SharedRPC.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Background JS runtime | ✅ | ✅ | — |
| Shared store | ✅ | ✅ | — |
| One-way shared RPC | ✅ | ✅ | — |
| Segment evaluation | ✅ | ✅ | — |
| `restart('ui')` | ✅ | ✅ | — |
| `restart('all')` | ✅ soft reload | ✅ process restart | — |

## Caveats

> [!IMPORTANT]
> On iOS the host delegate owns shared-bridge installation. Call
> `installSharedBridgeInMainRuntime:` and restart the background runner from
> `hostDidStart:` on every reload; the JavaScript `installSharedBridge()` no-op is
> not enough, and skipping it breaks main-to-background RPC after a restart.

> [!WARNING]
> `restart('all')` has different process semantics: iOS soft-reloads both runtimes
> inside the surviving process, while Android kills and relaunches the process.
> Native singletons, timers and services cannot be assumed to survive on either
> platform. Use `restart('ui')` when only UI state should be reset.

> [!NOTE]
> The shared store is a process bridge, not a shared JavaScript heap: `get()` can
> return `undefined`, and values are limited to `string`, `number` and `boolean`.
> The RPC object has no `read` or `has` operation.

## References

- [Example page](../../example/react-native/pages/BackgroundThreadTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
