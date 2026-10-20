# @onekeyfe/react-native-background-thread

A separate JavaScript runtime and shared native bridge for OneKey React Native
applications on iOS and Android. The package exposes background runner startup,
segment evaluation and runtime restart, together with primitive shared storage
and inline RPC notifications.

## Installation

```sh
yarn add @onekeyfe/react-native-background-thread
```

Install the application's iOS pods and rebuild the native application. Background
startup also requires the application's native host and background entry bundle;
this package does not create that entry or its module registry.

## Usage

```ts
import {
  BackgroundThread,
  getSharedStore,
} from '@onekeyfe/react-native-background-thread';

BackgroundThread.installSharedBridge();
const store = getSharedStore();
store?.set('worker.status', 'ready');
const status = store?.get('worker.status');
```

`getSharedStore()` and `getSharedRPC()` can return `undefined` before the native
bridge has been installed in the calling runtime. Shared values are strings,
numbers or booleans; callers must serialize structured data themselves.

## Runtime and bridge API

- `startBackgroundRunnerWithEntryURL(entryURL)` starts the background entry.
- `loadSegmentInBackground(segmentId, path)` evaluates a background segment.
- `restart('ui', reason)` restarts only the UI JavaScript runtime.
- `restart('all', reason)` restarts both JavaScript runtimes.
- SharedStore exposes `set`, `get`, `has`, `delete`, `keys`, `clear` and `size`.
- SharedRPC exposes `write`, `onWrite` and `registerReadinessKey`; notifications
  include both the call ID and primitive payload, without a read-back operation.

An all-runtime restart preserves the iOS process but replaces the Android
process. Do not assume native singleton or service state survives on both
platforms. Bundle switches must restart both runtimes so their module tables
remain consistent. These differences and restart validation are documented in
[src/NativeBackgroundThread.ts](src/NativeBackgroundThread.ts).

## Shared bridge readiness

SharedStore is a process bridge, not a JavaScript object shared between heaps.
`get(key)` may return `undefined`; distinguish that from stored `false`, zero
or an empty string. `keys()` returns strings and `size` is a read-only property.
The exported `ISharedStore` and `ISharedRPC` types describe those surfaces.

SharedRPC delivers the call ID and payload together through `onWrite`.
There is no `read`, `has` or pending-count API on that RPC object.
`registerReadinessKey(key)` identifies the SharedStore key native teardown should
clear for the calling runtime. Register and publish readiness in each runtime;
a UI runtime's initialization does not establish background-runtime readiness.

## License

MIT.
