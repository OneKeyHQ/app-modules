# @onekeyfe/react-native-scroll-guard

Native gesture coordination for scrollable content inside a React Native pager.
The Nitro view helps a nested scroller retain its intended scroll gestures
instead of allowing the parent pager to take them. It supports iOS and Android
with separate platform implementations.

## Installation

```sh
yarn add @onekeyfe/react-native-scroll-guard react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application. The
package requires a native Nitro view and a compatible scroll/pager hierarchy;
it does not provide a pager or render scroll content itself.

## API

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

`direction` accepts `HORIZONTAL`, `VERTICAL` or `BOTH`, and defaults to horizontal.
There are no additional imperative methods. The full prop definition is in
[src/ScrollGuard.nitro.ts](src/ScrollGuard.nitro.ts).

## Platform integration

On Android the native FrameLayout blocks ancestor touch interception for the
guarded direction and lets child views process the gesture. It releases the
unguarded direction so ancestor scrolling can continue.

On iOS the native view finds an ancestor paging UIScrollView and a sibling child
UIScrollView, then attaches gesture coordination to that child. The iOS guard
passes hit testing through rather than acting as a touch target. A standalone
guard outside that hierarchy has no compatible scroller to coordinate.

Place the guard according to the platform's native hierarchy and verify nested
scrolling, page changes and ordinary taps in the actual consumer. Setting a
direction does not establish that an arbitrary third-party pager integration is
supported, and this package has no Web gesture implementation.

## License

MIT.
