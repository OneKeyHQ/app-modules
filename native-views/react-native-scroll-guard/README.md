<div align="center">

# @onekeyfe/react-native-scroll-guard

**Keeps a nested scroller's gestures from being stolen by the surrounding pager.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-scroll-guard?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-scroll-guard)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-scroll-guard?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

A native wrapper that sits around scrollable content inside a pager and
coordinates the gesture with the platform. The guarded direction stays with the
child, and on Android the other axis is released so ancestor scrolling still
works. It does not provide a pager, render scroll content, or emit gesture
callbacks, and it has no Web implementation.

## Features

- **Direction-aware guarding** — guard `horizontal`, `vertical` or `both`; on
  Android the unguarded axis is released so ancestor scrolling can continue.
- **Native coordination only** — iOS attaches gesture recognizers to the found
  child scroller, Android re-asserts touch interception rules; no JavaScript
  frames or gesture math.
- **No public methods or callbacks** — the wrapper is purely declarative, so
  there is no state to synchronize with the application.
- **Composes with existing pagers** — it wraps the scrollable content and
  coordinates with the surrounding pager instead of replacing either one.

## Installation

```sh
yarn add @onekeyfe/react-native-scroll-guard react-native-nitro-modules
```

`react-native-nitro-modules` is a required peer dependency at the version pinned
by the package. Install the app's iOS pods and rebuild the native application
after adding the package; Android links through autolinking.

## Usage

```tsx
import { ScrollView, Text } from 'react-native';
import {
  ScrollGuardDirection,
  ScrollGuardView,
} from '@onekeyfe/react-native-scroll-guard';

<ScrollGuardView direction={ScrollGuardDirection.HORIZONTAL}>
  <ScrollView horizontal>
    <Text>Scrollable content inside the pager</Text>
  </ScrollView>
</ScrollGuardView>;
```

Place the guard between the pager and the scrollable content, and keep the
scroll content in its own child `ScrollView`/pager view.

## API

| Export | Type | Default | Description | Platforms |
| --- | --- | --- | --- | :-: |
| `ScrollGuardView` | component | — | Wraps scrollable content and guards the given axis. | iOS, Android |
| `direction` | `ScrollGuardDirection` | `ScrollGuardDirection.HORIZONTAL` | `HORIZONTAL`, `VERTICAL` or `BOTH`; the string values are `horizontal`, `vertical` and `both`. | iOS, Android |
| `ScrollGuardDirection` | enum | — | Direction constants for the `direction` prop. | iOS, Android |

The view exposes no imperative methods. See
[`src/ScrollGuard.nitro.ts`](src/ScrollGuard.nitro.ts) for the prop definition.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Guard horizontal gestures | ✅ | ✅ | — |
| Guard vertical gestures | ✅ | ✅ | — |
| Release the unguarded axis during a gesture | — | ✅ | — |

## Caveats

> [!IMPORTANT]
> On iOS the guard only connects when it can find an ancestor paging
> `UIScrollView` and a sibling child `UIScrollView`. A standalone guard outside
> that hierarchy has nothing to coordinate with.

> [!NOTE]
> Android blocks interception at its `FrameLayout` and releases the unguarded
> direction during the gesture. Verify diagonal drags, page changes and child
> taps in the real pager hierarchy; setting a direction does not by itself make
> an arbitrary third-party pager integration supported.

## References

- [Public interface](src/ScrollGuard.nitro.ts)
- [Example page](../../example/react-native/pages/ScrollGuardTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
