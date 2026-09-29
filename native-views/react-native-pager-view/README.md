# @onekeyfe/react-native-pager-view

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

## Upstream Project

- Repository: [callstack/react-native-pager-view](https://github.com/callstack/react-native-pager-view)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

## Collapsible pager

`CollapsiblePagerView` is an opt-in container. The default `PagerView` export
keeps its existing page lifetime and native implementation.

```tsx
<CollapsiblePagerView
  header={<Header />}
  stickyHeader={<Tabs />}
  headerHeight={measuredHeaderHeight}
  stickyHeaderHeight={measuredTabsHeight}
  pageRetentionDistance={1}
  onMountedPagesChanged={({ mountedPages }) => {
    // Selected page plus one neighbour on each side by default.
  }}
  onCollapsibleStateChanged={({ nativeEvent }) => {
    // Settled diagnostics: headerOffset, native/attached page counts, etc.
  }}
>
  {pages.map((page) => (
    <View key={page.key}>{page.content}</View>
  ))}
</CollapsiblePagerView>
```

Page keys must remain stable. The selected page and configured neighbours stay
mounted for preheating; distant page slots remain in the pager while their
React/native heavy subtrees are unmounted. iOS and Android retain a per-key
scroll offset and preserve the shared header collapse when pages change. A
NativeList can additionally restore its own stable item-key anchor when it is
remounted; the pager does not replace that list-level anchor protocol or its
cell reuse.

On iOS the component observes the current `UIScrollView` content offset without
replacing its delegate. Android coordinates nested scroll with the active
`RecyclerView`. Web uses the active NativeList viewport automatically; another
Web scroll child can opt in by exposing a DOM element with
`data-collapsible-pager-scroll`. CSS scroll timelines drive header/sticky
movement, so React is not updated on each vertical scroll frame.

On iOS and Android, `nativeTabBar` renders the tab bar natively and
`onNativeTabPress` reports presses. By default, adjacent tab presses animate and
non-adjacent presses jump directly to the target without passing intermediate
pages. Set `nativeTabPressAnimationEnabled={true}` to always animate, or `false`
to always jump. Swipe gestures and imperative page commands are unchanged.

> **Behavior change:** earlier versions animated every native tab press when
> `nativeTabPressAnimationEnabled` was omitted. Pass
> `nativeTabPressAnimationEnabled={true}` to keep that behavior.

Thank you again to Callstack and everyone who contributes to
`react-native-pager-view` 💙

The native ancestor-scroll contribution protocol is specified in [docs/SPEC.md](docs/SPEC.md).

## NativeScroller compatibility

The independent iOS/Android `NativeScroller` has passing native builds and
focused simulator checks. The complete acceptance matrix remains open; see
[docs/SPEC.md](docs/SPEC.md) for the verified cases and limits. It is a
non-virtualized vertical scroller with one internal React content View. Fabric/Yoga lays out React children and content padding; size
changes are sent to native through layout props, not a JavaScript scroll loop.
Shared Fabric native state tracks local scroll displacement and ancestor header
travel separately, without per-frame JavaScript coordination. Descendant hit
testing and `measureInWindow` use that state; device acceptance must compare the
reported coordinates with visible React content after scrolling and page return.
The automatic flexGrow minimum uses a native-reported usable content height,
with static pager reservations accounted for once. This internal size event is
not public API and does not run on scroll frames. Before it arrives the automatic
minimum is zero; a larger explicit numeric minHeight remains authoritative.
Refresh insets and content-dependent bottom fill do not change this minimum.
Web continues using its existing ScrollView implementation.

The supported ScrollView subset is `contentContainerStyle` (including padding,
height, numeric minHeight and flexGrow), `onContentSizeChange`, `scrollEnabled`,
`nestedScrollEnabled`, indicators, `bounces`, `alwaysBounceVertical`,
`contentInset`, keyboard dismissal/persist-tap modes, `scrollEventThrottle`, and
the scroll/drag/momentum callbacks. Standard View props remain available.
`decelerationRate` applies on iOS; Android uses AndroidX's native fling physics.
`bounces`/`alwaysBounceVertical` and refresh tint/title are iOS controls; Android
uses refresh colors/background/size and does not expose a bounce policy here.
Tap-to-dismiss uses RN 0.86's JavaScript TextInput registry to preserve taps on
another input and child-handled presses; keyboard behavior requires device checks.
Horizontal scrolling, sticky rows, snapping, zoom, automatic keyboard insets and
the full ScrollView responder API are not provided.

Pass a standard `<RefreshControl refreshing={value} onRefresh={callback} />` as
`refreshControl`. Its enabled, tint/colors, background, offset, title/title color
and size props map to the component's native refresh controller; the RN refresh
element is not mounted. Refreshing remains controlled even if the callback does
not change the value. Arbitrary custom refresh components are not supported.

`NativeScrollerRef` exposes `scrollTo`, `scrollToEnd`, `flashScrollIndicators`,
host measurement/focus methods, `getNativeScrollRef` and `getScrollableNode`.
`pagerScrollKey` remains the stable primary-scroller identity. Native command
events use the ScrollView event shape; UIKit/AndroidX continue owning gestures
and momentum. Animated commands whose clamped target differs from the current
position emit a momentum begin/end pair; a no-op emits no new pair. A new command,
new touch, content-root replacement or detach ends the previous active sequence.
The focused JS tests verify wrapper routing, not native scrolling,
Yoga rendering, keyboard interaction or refresh placement on a device.
