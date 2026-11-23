<div align="center">

# @onekeyfe/react-native-native-logger

**Native file logging with redaction, repeat suppression and bounded retention on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-native-logger?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-native-logger)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-native-logger?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Writes application log messages to rotating files inside the app sandbox and
exposes the log directory and file list for an application-owned export. iOS uses
CocoaLumberjack through a Swift hybrid object; Android uses Logback through a
Kotlin hybrid object. Both platforms share the same redaction, deduplication,
rate-limiting and retention behavior. The package neither uploads logs nor
provides a Web implementation.

## Features

- **Redaction built in** — private keys, mnemonics, tokens and newlines are
  stripped or replaced before anything reaches disk on either platform.
- **Repeat suppression** — identical consecutive messages collapse into a single
  `[N repeat]` line; flush the pending summary before exporting.
- **Rate limiting with error priority** — DEBUG and INFO are token-bucket
  limited, WARN has a higher rate, ERROR is never dropped.
- **Bounded retention** — one active `app-latest.log` plus six dated archives,
  each capped at 20 MiB and rolled daily.
- **Portable listing** — `getLogFilePaths()` returns names only; the caller joins
  them with `getLogDirectory()`.
- **Nitro hybrid object** — Swift and Kotlin implementations behind one
  generated TypeScript spec.

## Installation

```sh
yarn add @onekeyfe/react-native-native-logger react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application; the
JavaScript package alone does not register the hybrid object. On iOS the podspec
brings in `CocoaLumberjack/Swift ~> 3.8`; Android resolves its logging
dependencies through autolinking. No permissions or `Info.plist` keys are
required: log files live in the app's own cache directory.

## Usage

```ts
import { NativeLogger } from '@onekeyfe/react-native-native-logger';

NativeLogger.write(1, 'Application started'); // 0 debug, 1 info, 2 warn, 3 error

// Before exporting, flush the pending repeat summary and list the files.
NativeLogger.flushPendingRepeat();
const directory = NativeLogger.getLogDirectory();
const files = await NativeLogger.getLogFilePaths();
```

`write()` and `flushPendingRepeat()` return synchronously; enumeration and
deletion return promises. The application owns reading, uploading and presenting
the exported snapshot.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `write(level, msg)` | `void` | iOS, Android | Writes one message; levels `0` debug, `1` info, `2` warn, `3` error, other values fall back to info. |
| `flushPendingRepeat()` | `void` | iOS, Android | Flushes a pending `[N repeat]` summary; call before export. |
| `getLogDirectory()` | `string` | iOS, Android | Returns the absolute log directory. |
| `getLogFilePaths()` | `Promise<string[]>` | iOS, Android | Flushes the native buffer and returns sorted `.log` file names. |
| `deleteLogFiles()` | `Promise<void>` | iOS, Android | Removes archived files and truncates the active `app-latest.log`. |

`NativeLogger` is the Nitro hybrid object created at import time; the matching
interface is available for `import type`. The bridge is defined in
[`src/NativeLogger.nitro.ts`](src/NativeLogger.nitro.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Write log messages | ✅ | ✅ | — |
| Sensitive-data redaction | ✅ | ✅ | — |
| Repeat suppression / `flushPendingRepeat()` | ✅ | ✅ | — |
| Log directory and file listing | ✅ | ✅ | — |
| Delete/truncate log files | ✅ | ✅ | — |
| Daily roll, 20 MiB cap, 7 files | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> `getLogFilePaths()` returns file names rather than absolute paths on both
> platforms. Join them with `getLogDirectory()` when reading or exporting.

> [!WARNING]
> DEBUG and INFO are rate limited to roughly 400 messages per second (burst
> 2000), WARN to 1000 per second (burst 2000). Messages over the limit are dropped
> and summarized in a rate-limit line; ERROR is never rate limited.

> [!NOTE]
> `deleteLogFiles()` truncates the active `app-latest.log` instead of deleting it,
> and each message is truncated at 4096 characters.

## References

- [Example page](../../example/react-native/pages/NativeLoggerTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
