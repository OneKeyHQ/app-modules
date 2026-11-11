<div align="center">

# @onekeyfe/react-native-skeleton

**Native shimmer placeholders for OneKey's iOS and Android surfaces.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-skeleton?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-skeleton)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-skeleton?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

A Nitro view that renders a shimmering placeholder while content loads. The
native renderer owns the animation; the application owns the loading state and
decides when the placeholder is mounted and removed.

## Features

- **Native animation** — the shimmer runs on the native rendering surface, not
  in JavaScript frames.
- **Per-instance styling** — speed and a two-color gradient are props, so one
  package serves every loading surface.
- **Pure placeholder** — no load/error events, no data fetching and no global
  state to clean up.
- **New architecture** — a Nitro view registered through Fabric.

## Installation

```sh
yarn add @onekeyfe/react-native-skeleton
```

The package registers a native view, so install the app's iOS pods and rebuild
the native binary; a JavaScript-only install does not register it. Android links
through autolinking.

## Usage

```tsx
import { SkeletonView } from '@onekeyfe/react-native-skeleton';

<SkeletonView
  style={{ width: 160, height: 24, borderRadius: 6 }}
  shimmerSpeed={3}
  shimmerGradientColors={['#E5E7EB', '#F3F4F6']}
/>;
```

Give the view explicit layout dimensions, and reuse the loaded content's
dimensions if a layout shift would be visible.

## API

| Prop | Type | Default | Description |
| --- | --- | --- | --- |
| `shimmerSpeed` | `number` | `3` | Animation duration in seconds; clamped to at least `0.1`. |
| `shimmerGradientColors` | `string[]` | native defaults | Two gradient colors; only the first two entries are used. |

The component accepts a standard React Native `style` for size, radius and
placement. Use six-digit hex strings for the gradient so iOS and Android agree.
See [`src/Skeleton.nitro.ts`](src/Skeleton.nitro.ts) for the native interface and
[`src/index.tsx`](src/index.tsx) for the exported component.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| `SkeletonView` | ✅ | ✅ | — |
| Native shimmer | ✅ | ✅ | — |
| `shimmerSpeed`, `shimmerGradientColors` | ✅ | ✅ | — |

## Caveats

> [!NOTE]
> The view tracks no loading state and emits no completion event. The
> application removes the placeholder when its own loading state finishes.

> [!IMPORTANT]
> `shimmerGradientColors` is a string array, not React Native color objects, and
> `shimmerSpeed` is a duration in seconds, not frames per second.

## References

- [Public interface](src/Skeleton.nitro.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-skeleton)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
