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

Thank you again to Callstack and everyone who contributes to
`react-native-pager-view` 💙
