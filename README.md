<div align="center">

# OneKey App Modules

**The native layer of the OneKey wallet: 32 Nitro modules and 13 Fabric views for iOS, Android and Web, shipped in one synchronized version.**

[![Stable](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-native-list/latest?label=stable&color=4D6BFE&logo=npm)](https://www.npmjs.com/package/@onekeyfe/react-native-native-list?activeTab=versions)
[![Test](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-native-list/next?label=test&color=4D6BFE&logo=npm)](https://www.npmjs.com/package/@onekeyfe/react-native-native-list?activeTab=versions)
![license](https://img.shields.io/badge/license-MIT-4D6BFE)
![React Native](https://img.shields.io/badge/React_Native-0.88-61DAFB?logo=react&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-0.37-4D6BFE)
![packages](https://img.shields.io/badge/packages-45-4D6BFE)

[Packages](#packages) · [Getting started](#getting-started) · [Development](#development) · [Releases](docs/RELEASE.md) · [Changelog](CHANGELOG.md)

</div>

## What is this?

`@onekeyfe/app-modules` is the native package monorepo behind the OneKey wallet.
It wraps the platform capabilities the app depends on — cryptography, secure
storage, networking, push, OTA bundle updates, NFC cards, cloud backup and the
wallet's list, pager, tab bar, image and overlay views — as [Nitro
Modules](https://nitro.margelo.com) and Fabric views.

Every package is published to npm as `@onekeyfe/react-native-*` with **one
synchronized version** (currently `6.7.0-46`), so the app upgrades the whole
native layer at once. Packages are built for OneKey's own app; the ones with an
upstream project keep an *Upstream attribution* section in their README and the
upstream license notice in their `LICENSE`.

The stable and test badges above follow the npm `latest` and `next` dist-tags of
`@onekeyfe/react-native-native-list`, which shares the synchronized version.

## Packages

### Native modules (32)

| Package | What it does | Spec |
| --- | --- | :-: |
| [react-native-native-logger](native-modules/native-logger/README.md) | Native file logging with redaction, repeat suppression, bounded rotation and log export. | — |
| [react-native-aes-crypto](native-modules/react-native-aes-crypto/README.md) | Native AES-CBC/CTR/GCM, PBKDF2, HMAC, hashing and secure random values. | — |
| [react-native-app-update](native-modules/react-native-app-update/README.md) | Android APK download, signature verification and installation (iOS stubs). | [✓](native-modules/react-native-app-update/docs/SPEC.md) |
| [react-native-async-storage](native-modules/react-native-async-storage/README.md) | AsyncStorage-compatible string storage with cross-runtime write forwarding. | — |
| [react-native-background-thread](native-modules/react-native-background-thread/README.md) | A second JS runtime plus a shared store/RPC bridge and targeted restarts. | — |
| [react-native-bundle-crypto](native-modules/react-native-bundle-crypto/README.md) | Update signature verification, file/directory hashing and safe file operations. | [✓](native-modules/react-native-bundle-crypto/docs/SPEC.md) |
| [react-native-bundle-update](native-modules/react-native-bundle-update/README.md) | OTA bundle download, verification, install/switch and runtime bundle paths. | [✓](native-modules/react-native-bundle-update/docs/SPEC.md) |
| [react-native-capture-protection](native-modules/react-native-capture-protection/README.md) | Screenshot, recording and app-switcher protection without media permissions. | [✓](native-modules/react-native-capture-protection/docs/SPEC.md) |
| [react-native-check-biometric-auth-changed](native-modules/react-native-check-biometric-auth-changed/README.md) | Detects changes to the stored iOS biometric authentication state. | [✓](native-modules/react-native-check-biometric-auth-changed/docs/SPEC.md) |
| [react-native-cloud-fs](native-modules/react-native-cloud-fs/README.md) | iCloud (iOS) and Google Drive (Android) file access through one API. | — |
| [react-native-cloud-kit-module](native-modules/react-native-cloud-kit-module/README.md) | CloudKit account status and private-record storage (iOS). | — |
| [react-native-device-utils](native-modules/react-native-device-utils/README.md) | Host integration helpers: launch options, device tokens, dual-screen/iPadOS windows, nav-bar styling, boot recovery and a bounded in-memory store. | [✓](native-modules/react-native-device-utils/docs/SPEC.md) |
| [react-native-dns-lookup](native-modules/react-native-dns-lookup/README.md) | Native hostname resolution returning resolver addresses only, with no transport decision. | — |
| [react-native-get-random-values](native-modules/react-native-get-random-values/README.md) | Native-backed `crypto.getRandomValues` polyfill plus `getRandomBase64`. | — |
| [react-native-image-crop-picker](native-modules/react-native-image-crop-picker/README.md) | System single-photo picker and shared cropper without photo-library permission. | [✓](native-modules/react-native-image-crop-picker/docs/SPEC.md) |
| [react-native-jpush](native-modules/react-native-jpush/README.md) | Clean-room JPush/JCore client with a `jpush-react-native`-compatible JS API. | [✓](native-modules/react-native-jpush/docs/SPEC.md) |
| [react-native-keychain-module](native-modules/react-native-keychain-module/README.md) | iOS Keychain string storage with optional iCloud synchronization. | — |
| [react-native-lite-card](native-modules/react-native-lite-card/README.md) | OneKey Lite NFC card info, mnemonic backup/restore, PIN change and reset. | [✓](native-modules/react-native-lite-card/docs/SPEC.md) |
| [react-native-network-info](native-modules/react-native-network-info/README.md) | Read-only IP/gateway/subnet and Wi-Fi query getters. | [✓](native-modules/react-native-network-info/docs/SPEC.md) |
| [react-native-network-throttle](native-modules/react-native-network-throttle/README.md) | Latency/throughput throttling for React Native's own HTTP stack, for diagnostics. | — |
| [react-native-pbkdf2](native-modules/react-native-pbkdf2/README.md) | Native PBKDF2 derivation (CommonCrypto / SpongyCastle) with Base64 inputs and output. | — |
| [react-native-perf-memory](native-modules/react-native-perf-memory/README.md) | On-demand native process memory sample, in bytes. | — |
| [react-native-perf-stats](native-modules/react-native-perf-stats/README.md) | Native CPU/RAM/UI-FPS/JS-FPS sampling with a draggable overlay and memory-pressure hooks. | — |
| [react-native-photo-library](native-modules/react-native-photo-library/README.md) | Add-only photo saving without whole-library read access. | [✓](native-modules/react-native-photo-library/docs/SPEC.md) |
| [react-native-ping](native-modules/react-native-ping/README.md) | One-shot ICMP ping that resolves the round-trip time in milliseconds. | — |
| [react-native-range-downloader](native-modules/react-native-range-downloader/README.md) | Resumable multi-connection HTTP range downloads with typed fallback outcomes and firmware artifact staging. | [✓](native-modules/react-native-range-downloader/docs/SPEC.md) |
| [react-native-sni-connect](native-modules/react-native-sni-connect/README.md) | HTTPS requests to a pinned IP that keep real-hostname SNI and certificate validation. | [✓](native-modules/react-native-sni-connect/docs/SPEC.md) |
| [react-native-splash-screen](native-modules/react-native-splash-screen/README.md) | Retains and hides the legacy Android splash overlay until startup is ready; iOS is a no-op. | — |
| [react-native-split-bundle-loader](native-modules/react-native-split-bundle-loader/README.md) | Resolve, verify and evaluate split JS bundle segments in the running runtime. | — |
| [react-native-tcp-socket](native-modules/react-native-tcp-socket/README.md) | One-shot TCP connection probes with a measured connect time. | — |
| [react-native-webview-cleaner](native-modules/react-native-webview-cleaner/README.md) | Fire-and-forget clearing of app-wide WebView website data. | [✓](native-modules/react-native-webview-cleaner/docs/SPEC.md) |
| [react-native-zip-archive](native-modules/react-native-zip-archive/README.md) | Native ZIP extraction/creation with size inspection and iOS password support. | — |

### Native views (13)

| Package | What it does | Spec |
| --- | --- | :-: |
| [react-native-auto-size-input](native-views/react-native-auto-size-input/README.md) | Auto-sizing text input with native font fitting, prefix/suffix slots and focus methods. | — |
| [react-native-bottom-tabs](native-views/react-native-bottom-tabs/README.md) | Controlled native bottom-tab navigation with lazy/frozen scenes, a native drawer and Android floating/blur options. | [✓](native-views/react-native-bottom-tabs/docs/SPEC.md) |
| [react-native-chart-webview](native-views/react-native-chart-webview/README.md) | Chart WebView host with offline bundles, a document-start message bridge and optional WebView pooling. | [✓](native-views/react-native-chart-webview/SPEC.md) |
| [react-native-image](native-views/react-native-image/README.md) | SDWebImage/Glide-powered native image view with native loading states and cache control, plus named-SVG icons (`OneKeyIcon` also on Web). | [✓](native-views/react-native-image/docs/SPEC.md) |
| [react-native-native-list](native-views/react-native-native-list/README.md) | Template-driven native list: one serializable snapshot/patch stream and 13 row types drive UICollectionView, RecyclerView and a windowed DOM engine. | [✓](native-views/react-native-native-list/docs/SPEC.md) |
| [react-native-native-overlay](native-views/react-native-native-overlay/README.md) | Sheet/dialog/popover overlay hosts with ordered levels, page scope and Fabric measurement. | [✓](native-views/react-native-native-overlay/docs/SPEC.md) |
| [react-native-pager-view](native-views/react-native-pager-view/README.md) | Swipeable pager plus a collapsible shared header, native tab bar and non-virtualized `NativeScroller`. | [✓](native-views/react-native-pager-view/docs/SPEC.md) |
| [react-native-perp-depth-bar](native-views/react-native-perp-depth-bar/README.md) | Native order-book depth bars plus the bid/ask ratio track. | [✓](native-views/react-native-perp-depth-bar/docs/SPEC.md) |
| [react-native-scroll-guard](native-views/react-native-scroll-guard/README.md) | Gesture guard that keeps a nested scroller's drags away from the surrounding pager. | — |
| [react-native-segment-slider](native-views/react-native-segment-slider/README.md) | Fully native segmented slider with marks, thumb, optional value bubble and imperative `setValue`. | [✓](native-views/react-native-segment-slider/docs/SPEC.md) |
| [react-native-skeleton](native-views/react-native-skeleton/README.md) | Native shimmer loading placeholders. | — |
| [react-native-text](native-views/react-native-text/README.md) | Opt-in `Text` with an Android text-clipping guard; iOS and Web use React Native's `Text`. | — |
| [react-native-text-input](native-views/react-native-text-input/README.md) | `TextInput` with native paste events for text and image clipboard items. | [✓](native-views/react-native-text-input/docs/SPEC.md) |

Each package README follows the [README guide](docs/README_GUIDE.md) and links
its behavior contract (`docs/SPEC.md`) where one exists. The spec is authoritative;
the README is the quick path in.

## Requirements

| Requirement | Version |
| --- | --- |
| React Native | 0.88, new architecture |
| `react-native-nitro-modules` | 0.37.0 |
| Node.js | >= 20 |
| Yarn | 4.1.0 |
| iOS | 16.4+ |
| Android | minSdk 24, compile/target SDK 36 |
| Build tools | Xcode, CocoaPods, the Ruby in `example/react-native/.ruby-version` |

## Getting started

Install one package together with the Nitro runtime, then rebuild the native app:

```sh
yarn add @onekeyfe/react-native-skeleton react-native-nitro-modules
(cd ios && pod install)   # iOS
```

```tsx
import { SkeletonView } from '@onekeyfe/react-native-skeleton';

<SkeletonView style={{ width: 160, height: 24, borderRadius: 6 }} />;
```

Every package needs the new architecture and a native rebuild — installing the
JavaScript package alone does not register its native object or view. Android
links packages through autolinking; iOS needs `pod install`.

## Repository layout

```text
app-modules/
├── native-modules/        # 32 Nitro modules (headless native APIs)
├── native-views/          # 13 Fabric views (native UI)
├── chain-runtimes/        # Rust Zcash and Monero runtimes, built separately
├── example/react-native/  # example app consuming every package via workspace:*
├── docs/                  # development requirements, release process, README guide
├── scripts/               # generators, versioning and publishing tooling
└── CHANGELOG.md
```

## Development

```sh
yarn install                 # install the workspace
yarn lint:test-integrity     # repository test-integrity check

# Example app (iOS also needs the gems and pods first)
(cd example/react-native && bundle install && bundle exec pod install)
yarn workspace @onekeyfe/app-modules-example start
yarn workspace @onekeyfe/app-modules-example ios     # or: android
```

Each package exposes its own scripts; run one through the workspace name:

```sh
yarn workspace @onekeyfe/react-native-native-list typecheck
yarn workspace @onekeyfe/react-native-native-list lint
yarn workspace @onekeyfe/react-native-native-list test
```

Native work must follow the
[Native Module Development Requirements](docs/NATIVE_MODULE_DEVELOPMENT.md):
initialize new packages from the checked-in template (`yarn create:module` /
`yarn create:view`), define the public contract in the package `docs/SPEC.md`
before implementing it, and keep the spec and code in the same change.

## Releases

All publishable packages share one version tied to the target App baseline:

- A merged PR to `main` publishes a stable `latest` release automatically.
- Development branches publish `alpha.N` previews with `next` only when the
  `package-publish` workflow is run manually.

The current stable release is **6.7.0-46** (2026-11-11). The full process —
version commands, revision selection, dist-tag validation and single-workspace
recovery — lives in [docs/RELEASE.md](docs/RELEASE.md); per-release notes live in
[CHANGELOG.md](CHANGELOG.md).

## Documentation

| Document | Contents |
| --- | --- |
| [Native Module Development](docs/NATIVE_MODULE_DEVELOPMENT.md) | Required workflow for modules and views, including the spec-first contract. |
| [Releases](docs/RELEASE.md) | Synchronized versioning, publish automation and recovery. |
| [README guide](docs/README_GUIDE.md) | Required structure for package READMEs. |
| [Changelog](CHANGELOG.md) | Notable changes for each published release. |
| `docs/SPEC.md` (per package) | Authoritative behavioral contract, limits and platform differences. |

## Chain runtimes

Rust chain libraries for Zcash and Monero live in
[chain-runtimes](chain-runtimes/README.md), separately from the React Native
workspaces; see that README for build and test commands.

## License

MIT. Each package ships its own [LICENSE](native-modules/react-native-pbkdf2/LICENSE)
file; forked packages keep their upstream notices and attribution.
