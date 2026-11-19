<div align="center">

# @onekeyfe/react-native-split-bundle-loader

**Load OneKey split JavaScript segments into the runtime that is already running.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-split-bundle-loader?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-split-bundle-loader)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-split-bundle-loader?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

Reports the current runtime's bundle context, resolves a segment file with its
expected SHA-256, and evaluates that segment into the running React Native
runtime. It is the native half of OneKey split-bundle delivery: the application
still owns the manifest, segment ids, keys and hashes, and supplies them from its
own metadata. The module discovers the active OTA bundle through
`@onekeyfe/react-native-bundle-update` and falls back to built-in resources.
There is no Web implementation.

## Features

- **Runtime-aware context** — `getRuntimeBundleContext()` reports `runtimeKind`,
  `sourceKind`, `bundleRoot`, `nativeVersion` and optional
  `builtinExtractRoot` / `bundleVersion`.
- **Verified resolution** — `resolveSegmentPath()` returns only a path that
  exists and matches the expected SHA-256; traversal outside the bundle root is
  rejected.
- **Evaluate-then-resolve** — `loadSegment()` resolves after the segment's module
  definitions have executed, so requiring a module immediately afterwards does
  not race the loader.
- **OTA or built-in** — the active OTA bundle wins when `bundle-update` reports
  one; otherwise built-in resources are used.
- **Bounded eval watchdog** — a segment that cannot be evaluated within the
  watchdog budget rejects with a retryable `SPLIT_BUNDLE_TIMEOUT`.
- **Stable error codes** — `SPLIT_BUNDLE_*` codes let the JS loader distinguish
  retryable from fatal failures.

## Installation

```sh
yarn add @onekeyfe/react-native-split-bundle-loader @onekeyfe/react-native-bundle-update
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking. `@onekeyfe/react-native-bundle-update` is a required peer dependency:
it supplies the active OTA bundle path and whether the running manifest requires
per-segment hashes. The podspec also links `OneKeyLog` from
`@onekeyfe/react-native-native-logger`. A JavaScript-only install cannot evaluate
segments.

### iOS

The New Architecture host is looked up through the app delegate's `reactHost`
accessor. An app with a different host setup rejects `loadSegment()` with
`SPLIT_BUNDLE_NO_RUNTIME` instead of loading the segment.

## Usage

```ts
import { SplitBundleLoader } from '@onekeyfe/react-native-split-bundle-loader';

const context = await SplitBundleLoader.getRuntimeBundleContext();
// Select segment metadata that matches context.sourceKind and versions.

// Resolves only after the segment's module definitions have run.
await SplitBundleLoader.loadSegment(
  segmentId, // numeric id from the split-bundle manifest
  segmentKey, // caller-defined key, used in diagnostics
  relativePath, // path inside the active bundle root
  expectedSha256,
);

// The segment's modules can now be required.
```

The application supplies ids, keys, relative paths and expected hashes from its
own split-bundle metadata, keeps them aligned with the running native and bundle
versions, and handles rejected load promises before consuming segment modules.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `SplitBundleLoader.getRuntimeBundleContext` | `() => Promise<RuntimeBundleContext>` | iOS, Android | Reports the calling runtime's bundle source, root and versions. |
| `SplitBundleLoader.resolveSegmentPath` | `(relativePath: string, sha256: string) => Promise<string>` | iOS, Android | Resolves an existing segment file and verifies its SHA-256. |
| `SplitBundleLoader.loadSegment` | `(segmentId: number, segmentKey: string, relativePath: string, sha256: string) => Promise<void>` | iOS, Android | Loads and evaluates the segment in the running runtime. |

`RuntimeBundleContext` fields:

| Field | Type | Notes |
| --- | --- | --- |
| `runtimeKind` | `string` | Currently `main` for the runtime that loads the module. |
| `sourceKind` | `string` | `builtin` or `ota`. |
| `bundleRoot` | `string` | Active bundle directory; empty on Android for `builtin`. |
| `builtinExtractRoot` | `string?` | Android only: directory of extracted built-in segments. |
| `nativeVersion` | `string` | App version (`CFBundleShortVersionString` / `versionName`). |
| `bundleVersion` | `string?` | Reserved; currently empty on both platforms. |

Error codes: `SPLIT_BUNDLE_TIMEOUT` and `SPLIT_BUNDLE_NO_RUNTIME` are retryable;
`SPLIT_BUNDLE_EVAL_ERROR`, `SPLIT_BUNDLE_IO_ERROR`,
`SPLIT_BUNDLE_NATIVE_UNAVAILABLE`, `SPLIT_BUNDLE_NOT_FOUND` and
`SPLIT_BUNDLE_SHA256_MISMATCH` are not. See
[`src/NativeSplitBundleLoader.ts`](src/NativeSplitBundleLoader.ts) for the full
interface.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Report runtime bundle context | ✅ | ✅ | — |
| Resolve and SHA-256 check a segment | ✅ | ✅ | — |
| Evaluate a segment into the runtime | ✅ | ✅ | — |
| Read the active OTA bundle path | ✅ via `bundle-update` | ✅ via `bundle-update` | — |
| Built-in segments | ✅ app resources | ✅ extracted to files dir | — |
| `builtinExtractRoot` field | — | ✅ | — |

## Caveats

> [!IMPORTANT]
> A resolved path is not an evaluated segment. `resolveSegmentPath()` only
> checks existence and hash; call `loadSegment()` and await it before requiring
> any module defined by that segment.

> [!WARNING]
> On iOS the active OTA bundle is discovered only through
> `@onekeyfe/react-native-bundle-update`. Without that peer, or before it reports
> an OTA bundle, segments resolve from built-in resources; on Android built-in
> segments are extracted into the app files directory and re-extracted after an
> install change.

> [!NOTE]
> An empty `sha256` is accepted only for an active OTA bundle whose manifest does
> not require per-segment hashes (older split-bundle formats). Built-in bundles
> and three-bundle manifests fail closed with `SPLIT_BUNDLE_SHA256_MISMATCH`.
> Segment loading happens in the runtime that loaded the module; there is no
> cross-runtime loading API.

## References

- [Native interface](src/NativeSplitBundleLoader.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-split-bundle-loader)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
