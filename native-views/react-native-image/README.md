<div align="center">

# @onekeyfe/react-native-image

**Native image loading for OneKey apps — SDWebImage on iOS, Glide on Android, and a Nitro image view with cache control.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-image?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-image)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-image?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Renders remote and bundled images through a Nitro view backed by SDWebImage on
iOS and Glide on Android. The native view owns loading and terminal presentation,
cache control, animated images, request cancellation and safe reuse of recycled
cells; the application owns sizing, source selection and business fallbacks. A
separate `OneKeyIcon` entry renders host-provided named SVG icons on native and
Web. The package has no Web renderer for `OneKeyImage` itself.

## Features

- **Native image pipeline** — SDWebImage on iOS and Glide on Android with
  `memory-disk`, `memory`, `disk` or `none` cache policies.
- **Native loading and fallback states** — static placeholders, opt-in skeleton
  loading, and terminal error/fallback presentation drawn by the native view.
- **Explicit cache control** — `preload` and `clearMemory`/`clearDisk`/`clearAll`
  resolve only after the requested native cache work completes.
- **Bounded TOS rendition** — `optimizeTos` may swap eligible OneKey asset URLs
  for a rendition sized from `resizeWidth`/`resizeHeight` and `overscan`.
- **Recycled-cell safety** — `recyclingKey` and `reload()`/`cancel()` keep stale
  work out of reused native views.
- **Named SVG icons** — a separate `OneKeyIcon` entry renders host-owned artwork
  with no network request, on native and on Web.

## Installation

```sh
yarn add @onekeyfe/react-native-image \
  @onekeyfe/react-native-skeleton \
  react-native-nitro-modules@0.37.0
```

Peer dependencies: `react`, `react-native`, `react-native-nitro-modules` 0.37.0
and `@onekeyfe/react-native-skeleton` (the native skeleton renderer). Install the
app's iOS pods and rebuild the native binary; a JavaScript-only install does not
register the views:

```sh
cd ios && pod install
```

Android links through autolinking; deployment floors are iOS 15.1 and Android
API 24. For a CocoaPods static-library integration, declare the SDWebImage coders
as modular headers in the application `Podfile`: SDWebImage `5.21.7`,
SDWebImageWebPCoder `0.14.6` and SDWebImageSVGCoder `1.7.0`.

## Usage

```tsx
import {
  OneKeyImage,
  OneKeyImageLoadingStrategy,
  OneKeyImageVariant,
} from '@onekeyfe/react-native-image';

<OneKeyImage
  source={{ uri: avatarUrl }}
  variant={OneKeyImageVariant.AVATAR}
  loadingStrategy={OneKeyImageLoadingStrategy.SKELETON}
  style={{ width: 48, height: 48, borderRadius: 24 }}
/>;
```

Give the view explicit dimensions: it does not derive its own size from the
image, and a reused list cell must receive a new `recyclingKey`.

### Named icons

`OneKeyIcon` lives in the separate `icon` entry and renders artwork the host
supplies at build time:

```tsx
import { OneKeyIcon } from '@onekeyfe/react-native-image/icon';

<OneKeyIcon name="StarOutline" color="#202020" style={{ width: 24, height: 24 }} />;
```

Names match `^[A-Z][A-Za-z0-9]*$`; the default size is 24 × 24 and the default
color is opaque black. `color` only resolves `currentColor` fills and strokes, it
is not a whole-image tint. The SDK ships no artwork: native builds provide the
icon bundle and Web builds must alias `#onekey-icon-assets` to the host's
generated icon table, otherwise named icons render blank.

## API

### `OneKeyImage`

| Prop | Type | Default | Platforms | Description |
| --- | --- | --- | :-: | --- |
| `source` | URI source or numeric asset | — | iOS, Android | `null`, blank or unresolved input shows fallback without `onError`. |
| `variant`, `contentFit` | `generic` \| `token` \| `network` \| `avatar`; `cover` \| `contain` \| `fill` \| `center` | `generic`, `cover` | iOS, Android | Native placeholder/fallback presentation and display/decode sizing. |
| `cachePolicy`, `loadingStrategy` | `memory-disk` \| `memory` \| `disk` \| `none`; `static` \| `skeleton` \| `none` | `memory-disk`, `static` | iOS, Android | Cache policy (prop wins over source value) and native loading surface; a React `placeholder` disables the native surface. |
| `placeholder`, `fallback`, `placeholderColor` | `ReactNode`, color | — | iOS, Android | Overlay content clipped to the same rounded bounds (no pointer events) and theme color for native states. |
| `autoplay`, `recyclingKey`, `round`, `optimizeTos` | `boolean`, `string`, `boolean`, `boolean` | `false` Android / `true` iOS, —, `false`, `true` | iOS, Android | Animated images, request-generation identity, oval clip and bounded OneKey rendition URLs. |
| `resizeWidth`, `resizeHeight`, `overscan` | `number` | —, —, `1.1` | iOS, Android | Logical-size hints used by preload, TOS selection and memory probing. |
| `onLoadStart`, `onLoad`, `onLoadEnd`, `onDisplay`, `onError` | event callbacks | — | iOS, Android | `onLoad` receives `{ source: { url, width, height }, cacheType }`. |
| `hybridRef` | callback ref | — | iOS, Android | Exposes `reload()` and `cancel()`. |

### `OneKeyImageCache`

| Method | Result | Platforms | Description |
| --- | --- | --- | :-: |
| `preload(sources)` | `Promise<boolean>` | iOS, Android | Resolves `true` only when every valid source succeeds. |
| `clearMemory()`, `clearDisk()`, `clearAll()` | `Promise<void>` | iOS, Android | Resolve after the requested native cache work, including cross-size hints. |

Enums exported by the package: `OneKeyImageVariant`, `OneKeyImageContentFit`,
`OneKeyImageCachePolicy`, `OneKeyImageLoadingStrategy` and
`OneKeyImageCacheType` (`none`, `memory`, `disk`).

### `OneKeyIcon`

| Prop | Type | Platforms | Description |
| --- | --- | :-: | --- |
| `name`, `color`, `style` | `string`, `string`, view props | iOS, Android, Web | Required host icon name; native accepts `#hex`/`rgb()`/`rgba()`, Web follows CSS. Explicit dimensions beat the 24 × 24 default. |

The package also exports `createNamedIcon(document, name)` from
`@onekeyfe/react-native-image/icon/dom` for non-React Web surfaces.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| `OneKeyImage` rendering | ✅ | ✅ | — |
| Native loading, skeleton and fallback states | ✅ | ✅ | — |
| Cache preload and clearing | ✅ | ✅ | — |
| Animated images | ✅ | ✅ | — |
| `OneKeyIcon` named SVG | ✅ | ✅ | ✅ host-supplied assets |

## Caveats

> [!IMPORTANT]
> `OneKeyImage` is a native view for iOS and Android only. The Web entry of this
> package provides `OneKeyIcon` and `createNamedIcon`; it never renders
> `OneKeyImage`.

> [!NOTE]
> Both platforms reject oversized or malformed input: 32 MiB encoded images,
> 8 MiB decoded data URIs, 16 MiB animated files, 1,000 frames and 60 seconds.

> [!WARNING]
> A reused native view must receive a new `recyclingKey` (or an explicit
> `reload()`/`cancel()`) when its logical content changes; otherwise stale work
> from the previous source owns the view.

## References

- [Package spec](docs/SPEC.md)
- [Example image page](../../example/react-native/pages/OneKeyImageTestPage.tsx)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-image)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
