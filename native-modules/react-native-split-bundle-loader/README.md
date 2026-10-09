# @onekeyfe/react-native-split-bundle-loader

Native loading of OneKey split JavaScript bundles on iOS and Android. The module
reports the current runtime's bundle context, resolves a segment path and loads
the segment into that runtime. It is intended for applications with the matching
OneKey split-bundle packaging and native host integration.

## Installation

```sh
yarn add @onekeyfe/react-native-split-bundle-loader
```

Install the application's iOS pods and rebuild the native application. The
package declares `@onekeyfe/react-native-bundle-update` as a peer dependency.
The native binary must include the segment loader; adding JavaScript alone cannot
enable native segment evaluation.

## Usage

```ts
import { SplitBundleLoader } from '@onekeyfe/react-native-split-bundle-loader';

const context = await SplitBundleLoader.getRuntimeBundleContext();
// Use this context to select the matching application-owned segment metadata.
const bundleRoot = context.bundleRoot;
```

## API

| Method | Purpose |
| --- | --- |
| `getRuntimeBundleContext()` | Return runtime kind, source kind, bundle root and native/bundle version information. |
| `resolveSegmentPath(relativePath, sha256)` | Resolve a segment using its expected SHA-256. |
| `loadSegment(segmentId, segmentKey, relativePath, sha256)` | Load a segment into the calling runtime. |

The application supplies IDs, keys, relative paths and expected hashes from its
own split-bundle metadata. Keep them aligned with the running native and bundle
versions. The module does not generate manifests or choose compatible module ID
tables for the caller.

Handle rejected load promises before requiring modules from the segment. A
resolved path alone does not mean segment code has been evaluated. UI and
background JavaScript runtimes have separate loading contexts; initialize and
load in the runtime that will consume the modules.

See [src/NativeSplitBundleLoader.ts](src/NativeSplitBundleLoader.ts) for the
complete TypeScript interface and the native source directories for platform
loading and failure handling.

## License

MIT.
