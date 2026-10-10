# @onekeyfe/react-native-native-logger

Native file logging for OneKey React Native applications on iOS and Android.
The JavaScript interface writes messages, enumerates log files and clears stored
logs. Native code owns log formatting, rotation and repeat-message handling.

## Installation

```sh
yarn add @onekeyfe/react-native-native-logger react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application after
adding the package. This module requires the Nitro native runtime; installing
the JavaScript package alone does not register its native implementation.

## Usage

```ts
import { NativeLogger } from '@onekeyfe/react-native-native-logger';

NativeLogger.write(1, 'Application started');
NativeLogger.flushPendingRepeat();

const directory = NativeLogger.getLogDirectory();
const files = await NativeLogger.getLogFilePaths();
```

## API

| Method | Purpose |
| --- | --- |
| `write(level, msg)` | Write a message: 0 debug, 1 info, 2 warning, 3 error. |
| `flushPendingRepeat()` | Flush a pending repeated-message summary before export. |
| `getLogDirectory()` | Return the native log directory. |
| `getLogFilePaths()` | List native log files asynchronously. |
| `deleteLogFiles()` | Clear the native log files. |

On iOS, `getLogFilePaths()` returns filenames, rather than absolute paths; combine
them with the directory when exporting. Clearing logs can truncate the active
file instead of removing it. Callers own export and upload, and should avoid
writing credentials or other private values into messages.

The current bridge is defined in [src/NativeLogger.nitro.ts](src/NativeLogger.nitro.ts).
Native behavior is implemented in the package's `ios` and `android` directories.

## Exporting a log snapshot

Flush pending repeated-message summaries before listing files for an export.
`write` and `flushPendingRepeat` return synchronously; enumeration and deletion
return promises. Await deletion before starting an application-owned export
that would otherwise race cleanup. The package does not expose an upload API.

The named `NativeLogger` export is created when this module is imported, so
import it only in a native runtime with the matching registration. The exported
TypeScript `NativeLogger` interface can be imported with `import type` without
creating that native object.

## License

MIT.
