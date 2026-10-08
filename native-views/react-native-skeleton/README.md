# @onekeyfe/react-native-skeleton

A native shimmer loading placeholder for OneKey React Native applications on
iOS and Android. The Nitro view animates its own native rendering surface;
the application decides when to display it and when loaded content replaces it.

## Installation

```sh
yarn add @onekeyfe/react-native-skeleton react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application after
adding the package. A JavaScript-only install does not register the Nitro view.

## Usage

```tsx
import { SkeletonView } from '@onekeyfe/react-native-skeleton';

<SkeletonView
  style={{ width: 160, height: 24, borderRadius: 6 }}
  shimmerSpeed={3}
  shimmerGradientColors={['#E5E7EB', '#F3F4F6']}
/>;
```

Give the view a width and height through its layout style. The example uses the
actual exported package name and supported shimmer props; there is no public
`color` prop on this view.

## Props and lifecycle

| Prop | Meaning |
| --- | --- |
| `shimmerSpeed` | Animation duration in seconds; defaults to 3 and is clamped to at least 0.1. |
| `shimmerGradientColors` | Two gradient colors; native uses the first two entries and falls back to its defaults when fewer are provided. |

Use six-digit hex colors for consistent iOS and Android input. The view's native
renderer handles attachment and animation lifecycle; it does not track network
requests or emit a loading-completion event. Consumers control whether the
placeholder remains mounted and should verify the resulting layout with their
actual content.

The current interface is in [src/Skeleton.nitro.ts](src/Skeleton.nitro.ts), and
the public component is exported from [src/index.tsx](src/index.tsx).

## License

MIT.
