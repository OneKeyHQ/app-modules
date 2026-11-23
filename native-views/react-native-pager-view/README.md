<div align="center">

# @onekeyfe/react-native-pager-view

**OneKey's Fabric fork of `react-native-pager-view`: swipeable pages, a collapsible shared header and a native vertical scroller.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-pager-view?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-pager-view)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-pager-view?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
![Fabric](https://img.shields.io/badge/Fabric-4D6BFE)

</div>

PagerView flips horizontally or vertically between keyed child pages, rendered by
a Fabric native view on iOS and Android and by a DOM fallback on Web. This fork
adds `CollapsiblePagerView` — a header that collapses into a pinned sticky bar
while pages keep their own offsets and retention window — and `NativeScroller`, a
non-virtualized vertical scroller that joins that collapse. Upstream remains the
reference for the original pager behavior.

## Features

- **Native paging** — horizontal/vertical pages with `setPage`,
  `setPageWithoutAnimation`, `setScrollEnabled` and page events.
- **Collapsible shared header** — native scroll coordination on mobile and CSS
  scroll timelines on Web via `WebCollapsiblePagerView`.
- **Bounded page retention** — the selected page and configured neighbours stay
  mounted while distant subtrees are unmounted and restored at their offset.
- **Native tab bar** — underline, segmented, text and pill variants, toolbar
  slots and controlled selection.

## Installation

```sh
yarn add @onekeyfe/react-native-pager-view \
  @onekeyfe/react-native-image \
  @onekeyfe/react-native-native-logger
```

Peer dependencies: `react`, `react-native`, `@onekeyfe/react-native-image` (Web
icon helper) and `@onekeyfe/react-native-native-logger` (Android logging). Install
the app's iOS pods and rebuild the native binary; JavaScript-only does not
register the views:

```sh
cd ios && pod install
```

Android links through autolinking; deployment floors are iOS 15.1 and Android
API 24.

## Usage

```tsx
import PagerView, {
  type PagerViewOnPageSelectedEvent,
} from '@onekeyfe/react-native-pager-view';
import { Text, View } from 'react-native';

<PagerView
  style={{ flex: 1 }}
  initialPage={0}
  onPageSelected={(event: PagerViewOnPageSelectedEvent) => {
    console.log('selected page', event.nativeEvent.position);
  }}
>
  <View key="welcome" style={{ flex: 1 }}>
    <Text>Welcome</Text>
  </View>
  <View key="wallet" style={{ flex: 1 }}>
    <Text>Wallet</Text>
  </View>
</PagerView>
```

The application owns page keys, content and any JS header chrome; the pager owns
page placement and gestures. Every child must be a keyed `<View>`.

## API

### Exports

| Export | Platforms | Description |
| --- | :-: | --- |
| `PagerView` (default) | iOS, Android, Web | Swipeable page container; native page host on mobile, DOM fallback on Web. |
| `CollapsiblePagerView` | iOS, Android, Web | Collapsible header + sticky bar wrapper. |
| `WebCollapsiblePagerView`, `useWebCollapsiblePagerPage()` | Web | Explicit Web collapse root that measures omitted heights, and the selected page's scroll owner. |
| `NativeScroller` | iOS, Android, Web | Non-virtualized vertical scroller; the Web export is a `ScrollView` implementation. |
| `usePagerView({ pagesAmount })` | iOS, Android, Web | Animated pager progress, scroll state and add/remove page helpers. |
| `ScrollEdgeEffect` | iOS | `hard` / `soft` values for iOS 26 top scroll-edge effects. |

### `PagerView` props

| Prop | Type | Default | Platforms | Description |
| --- | --- | --- | :-: | --- |
| `initialPage`, `scrollEnabled`, `orientation` | `number`, `boolean`, `'horizontal' \| 'vertical'` | `0`, `true`, `'horizontal'` | all | First page, swipe enablement and paging axis. |
| `overdrag`, `pageMargin`, `layoutDirection` | `boolean`, `number`, `'ltr' \| 'rtl' \| 'locale'` | `false`, `0`, `'ltr'` | all | Past-edge movement, gap between pages and LTR/RTL order. |
| `nestedScrollEnabled` | `boolean` | `false` | iOS | Coordinates gestures with a parent pager at sub-tab boundaries. |
| `scrollSensitivity`, `keyboardDismissMode`, `offscreenPageLimit` | platform props | `2`, `none`, native | Android | Gesture sensitivity (`2`–`8`), keyboard dismissal and offscreen pages. |
| `onPageScroll`, `onPageSelected`, `onPageScrollStateChanged` | event callbacks | — | all | `{ position, offset }`, `{ position }` and `idle`/`dragging`/`settling`. |

The ref exposes `setPage`, `setPageWithoutAnimation` and `setScrollEnabled`.

### `CollapsiblePagerView` props

| Prop | Type | Platforms | Description |
| --- | --- | :-: | --- |
| `header`, `stickyHeader`, `headerHeight`, `stickyHeaderHeight` | `ReactNode`, `number` | all | Collapsible content, the bar pinned after collapse and their measured heights. |
| `pageRetentionDistance` | `number` | all | Pages retained on each side of the selection. Defaults to `1`. |
| `nativeTabBar` | config object | iOS, Android, Web | Items, `controlled`/`selectedKey`, toolbar map and style variant. |
| `nativeTabPressAnimationEnabled` | `boolean` | iOS, Android | Per-press animation policy; see Caveats. |
| `onMountedPagesChanged`, `onCollapsibleStateChanged` | event callbacks | all | Mount state and settled diagnostics. |
| `topOverlay`, `topScrollEdgeEffect` | `ReactNode`, `'hard' \| 'soft'` | iOS 26+ | Fixed overlay above the header and UIKit's scroll-edge effect. |

The ref exposes `scrollHeaderBy(delta)` and `scrollHeaderToOffset(offset)` to move
the active page's collapse position.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| `PagerView` paging, events and imperative commands | ✅ | ✅ | ✅ DOM fallback |
| `CollapsiblePagerView` shared-header collapse | ✅ native | ✅ native | ✅ CSS scroll timelines |
| Page retention `pageRetentionDistance` | ✅ | ✅ | ✅ |
| Native tab bar (underline/segmented/text/pill) | ✅ | ✅ | ✅ text/pill |
| `NativeScroller` non-virtualized scroller | ✅ | ✅ | ⚠️ `ScrollView` implementation |
| iOS 26 top overlay / scroll-edge effect | ✅ | — | — |

## Caveats

> [!IMPORTANT]
> Keep page child keys stable. Retention unmounts distant pages but keeps their
> slots and offsets; changing a key resets that page's retained state.

> [!WARNING]
> Omitting `nativeTabPressAnimationEnabled` animates adjacent tab presses and
> jumps for non-adjacent pages. Earlier versions animated every press — pass
> `true` to restore that behavior.

> [!NOTE]
> `NativeScroller` is not a virtualized list. Horizontal scrolling, sticky rows,
> snapping, zoom, automatic keyboard insets and the full ScrollView responder
> API are not provided.

## References

- [Scroll and collapse contract](docs/SPEC.md)
- Example pages: [pager](../../example/react-native/pages/PagerViewTestPage.tsx) and [collapsible pager](../../example/react-native/pages/MarketNativePagerExamplePage.tsx)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-pager-view)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

First, a sincere thank-you to Callstack and the
`react-native-pager-view` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[callstack/react-native-pager-view](https://github.com/callstack/react-native-pager-view).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-pager-view`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-pager-view` to keep
development and delivery stable.

- Upstream repository:
  [callstack/react-native-pager-view](https://github.com/callstack/react-native-pager-view)
- License: MIT
- This fork includes OneKey-specific adaptations for our product requirements.
- For original behavior and full documentation, refer to the upstream repository.

Thank you again to Callstack and everyone who contributes to
`react-native-pager-view` 💙

## License

MIT. See [LICENSE](LICENSE).
