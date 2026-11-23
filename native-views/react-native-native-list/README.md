<div align="center">

# @onekeyfe/react-native-native-list

**Template-driven native lists for OneKey surfaces — UICollectionView on iOS, RecyclerView on Android and a windowed DOM engine on Web.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-native-list?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-native-list)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-native-list?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Renders bounded, high-volume row data without mounting a React subtree per row.
JavaScript sends one serializable snapshot — or an atomic batch of patches — to a
Nitro view, and the native host owns layout, recycling, selection, reorder and
scrolling. Rows are a strict discriminated union of thirteen fixed templates, so
there is no `renderItem`, arbitrary style dictionary or embedded React component
per cell. Web runs a separate windowed DOM engine behind the same component,
data model and imperative API.

## Features

- **No React subtree per row** — a snapshot or patch batch is serialized once,
  and native collection hosts recycle cells.
- **Thirteen fixed row templates** — identity, wallet group, rail, activity,
  message, data, market, media tile, metric card, section header, action, button
  group and system rows cover the OneKey list surfaces.
- **Four layouts on one model** — `linear`, `sectioned`, `grid` and `table`,
  with vertical/horizontal orientation, sticky headers, fixed footer, empty
  state and named container slots.
- **Native interaction** — single/multiple selection with tri-state checkboxes,
  reorder with a drag preview, a section index rail, pull to refresh and
  end-reached events.
- **Imperative updates and scrolling** — apply snapshots/patches, reconcile
  selection after native interaction, and scroll by key, index, item, offset or
  location.
- **OneKeyImage-backed cells** — row image slots bind through
  `@onekeyfe/react-native-image` reusable views and keep its shared cache
  pipeline.

## Installation

```sh
yarn add @onekeyfe/react-native-native-list \
  @onekeyfe/react-native-image \
  @onekeyfe/react-native-native-logger \
  react-native-nitro-modules@0.37.0
```

Peer dependencies: `react`, `react-native`, `react-native-nitro-modules`
0.37.0, `@onekeyfe/react-native-image` and `@onekeyfe/react-native-native-logger`.
Keep NativeList and OneKeyImage on the same release, because recycled image
slots use the image host's native view.

The package registers a native view, so install the app's iOS pods and rebuild
the native binary; a JavaScript-only install does not register it:

```sh
cd ios && pod install
```

Android links through autolinking. The package requires the React Native new
architecture; its deployment floors are iOS 15.5 and Android API 24.

## Usage

```tsx
import { useMemo } from 'react';
import {
  NativeList,
  type NativeListSnapshot,
} from '@onekeyfe/react-native-native-list';

const snapshot: NativeListSnapshot = {
  schemaVersion: 1,
  generation: 1,
  layout: { kind: 'sectioned', stickyHeaders: true, itemSpacing: 1 },
  rows: [
    {
      type: 'sectionHeader',
      key: 'assets',
      sectionKey: 'assets',
      title: 'Assets',
    },
    {
      type: 'identity',
      key: 'btc',
      sectionKey: 'assets',
      leading: { kind: 'icon', name: 'StarOutline' },
      title: 'Bitcoin',
      subtitle: 'BTC · Bitcoin',
      trailing: [{ kind: 'value', text: '$64,230' }],
    },
  ],
};

export function AssetList() {
  const stableSnapshot = useMemo(() => snapshot, []);
  return (
    <NativeList
      style={{ flex: 1 }}
      snapshot={stableSnapshot}
      onRowAction={({ rowKey, actionKey }) => {
        console.log(rowKey, actionKey);
      }}
    />
  );
}
```

The application owns the snapshot and the React chrome around the list; the
native view owns cells, recycling and gestures. Keep the snapshot referentially
stable between unrelated renders — a re-render with an older snapshot
intentionally restores that older state.

## API

### Component

| Prop | Type | Platforms | Description |
| --- | --- | :-: | --- |
| `snapshot` | `NativeListSnapshot` | iOS, Android, Web | Required declarative baseline: layout, rows, theme, selection, capabilities. |
| `listHeader`, `listFooter`, `listEmpty` | `ReactNode` | iOS, Android, Web | Container slots outside the data-row stream. |
| `listExpandedContent`, `listStructureToggle` | `ReactNode` | iOS, Android, Web | Native disclosure content and its toggle. |
| `listSidecar`, `sidecarLayout` | `ReactNode`, `NativeListSidecarLayout` | iOS, Android, Web | Scrolling right column for a vertical linear list; both are required together. |
| `topOverlay`, `topScrollEdgeEffect` | `ReactNode`, `'hard' \| 'soft'` | iOS 26+ | Fixed overlay above scrolling rows and UIKit's top scroll-edge effect; both are required together. |
| `initialScrollIndex` / `initialScrollKey` | `number` / `string` | iOS, Android, Web | One-shot first position; the two props are mutually exclusive. |
| `initialScrollViewPosition`, `initialScrollViewOffset` | `number` | iOS, Android, Web | Offset applied to the initial scroll target. |
| `keyboardDismissMode` | `'none' \| 'on-drag' \| 'interactive'` | iOS, Android | `interactive` behaves as `none` on Android. |
| `keyboardShouldPersistTaps` | `'never' \| 'always' \| 'handled'` | iOS, Android | Defaults to `never`. |
| `webVirtualizationEnabled` | `boolean` | Web | Defaults to `true`. |
| `webScrollHost` | host object or `null` | Web | `undefined` owns scrolling; `null` waits for a host element. |
| `webStickyInset`, `webSectionIndexContainerRef` | `number`, `RefObject` | Web | Visible sticky chrome inset and the section-index host container. |
| `scrollPositionThresholds` | `{ start, end, enabled? }` | iOS, Android | Emits initial state and hysteresis transitions, not scroll frames. |
| `onRowAction`, `onActionAnchorInvalidated` | event callbacks | iOS, Android, Web | Row/accessory actions with anchor metadata, and anchor invalidation. |
| `onSelectionDelta`, `onReorder` | event callbacks | iOS, Android, Web | Native selection delta and committed reorder range. |
| `onEndReached`, `onVisibleRangeChanged` | event callbacks | iOS, Android, Web | End-reached fires once per snapshot `generation`; visible-range events are coalesced to one per display frame. |
| `onRefresh`, `onScrollToIndexFailed` | `() => void`, `(info) => void` | iOS, Android, Web | Pull-to-refresh request and invalid explicit scroll targets. |
| `onStructuralTransitionSettled` | event callback | iOS, Android | Settled/interrupted structural row animation. |

### Ref methods

All ref methods are available on iOS, Android and Web.

| Method | Description |
| --- | --- |
| `applySnapshot(snapshot)` | Replaces the declarative baseline. |
| `applyPatches(patches)` | Applies a validated, keyed patch batch atomically. |
| `reconcileSelection(keys)` | Replaces native selection without emitting a second delta. |
| `scrollToKey`, `scrollToIndex`, `scrollToItem` | Scroll to a row by stable key, index or row model. |
| `scrollToOffset`, `scrollToEnd`, `scrollToLocation` | Scroll by offset, to the end or to a section/item location. |
| `setActionAnchorState(state)` | Opens/closes an anchored action for a row control. |
| `setRefreshing(refreshing)` | Synchronizes the native refresh control. |

See [`src/NativeList.types.ts`](src/NativeList.types.ts) for the full prop, ref
and scroll-option types.

### Other exports

| Export | Platforms | Description |
| --- | :-: | --- |
| `preloadNativeListAvatarImages(sources)` | iOS, Android, Web | Preloads avatar `ImageSource`s through the OneKeyImage cache. |
| `configureNativeListFonts(families)` | iOS, Android, Web | Maps the regular/medium/semibold/bold faces for mounted lists at runtime. |
| `ScrollEdgeEffect` | iOS | `hard` / `soft` values for `topScrollEdgeEffect`. |
| `validateSnapshot`, `validatePatches` | iOS, Android, Web | Validate a snapshot or patch batch before use. |
| `applyRowPatches`, `planNativeListPatches` | iOS, Android, Web | Apply a patch batch to a snapshot, or plan value-only patches between two snapshots. |
| `serializeSnapshot`, `serializePatches` | iOS, Android, Web | Produce the JSON payloads the native host consumes. |
| `reduceSelection`, `isSelectableRow`, `checkboxStateForKeys`, `checkboxStateForSection`, `selectionStateFromSnapshot` | iOS, Android, Web | Selection state helpers shared with the application store. |

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Snapshots, patches and the 13 row templates | ✅ | ✅ | ✅ |
| `linear`, `sectioned`, `grid` and `table` layouts | ✅ | ✅ | ✅ |
| Selection, row actions and tri-state checkboxes | ✅ | ✅ | ✅ |
| Reorder with a drag preview | ✅ | ✅ | ✅ |
| Section index rail | ✅ | ✅ | ✅ DOM overlay |
| Pull to refresh | ✅ | ✅ | ✅ |
| Container slots around the row stream | ✅ | ✅ | ✅ |
| Imperative scrolling and position thresholds | ✅ | ✅ | ✅ scrolling; — thresholds |
| Fixed top overlay / scroll-edge effect | ✅ iOS 26+ | — | — |

## Caveats

> [!IMPORTANT]
> Data rows are fixed templates. The public model rejects React children,
> per-row render functions, arbitrary style dictionaries, nested lists and
> unbounded self-sizing content. Use a named container slot for React chrome
> around the row stream.

> [!WARNING]
> `snapshot` is the declarative baseline. Re-rendering with an older snapshot
> after imperative patches or a reorder intentionally restores the older state;
> derive a new snapshot only when the data changes.

> [!NOTE]
> Web runs a windowed DOM engine behind the same component and data API. It
> mirrors the mobile data and imperative contracts, but its performance is
> independent of the native implementations and must be measured separately.

## References

- [Behavioral contract](docs/SPEC.md)
- [Row templates](docs/ROW_TEMPLATES.md)
- [Style vocabulary](docs/STYLE_SPEC.md)
- [Architecture and ownership](docs/DESIGN.md)
- [Example app page](../../example/react-native/pages/NativeListExamplePage.tsx)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-native-list)
- [Changelog](../../CHANGELOG.md)

## License

MIT. The avatar PRNG/HSL algorithms in `src/web/NativeListAvatarWorker.js` are
adapted from MyCrypto `ethereum-blockies-base64` (MIT); see [LICENSE](LICENSE).

## Row and layout model

`RowModel` is a strict discriminated union. It intentionally accepts no React
element, render function, arbitrary style object or native object handle, so the
same snapshot can be validated and rendered on all three platforms.

### Row templates

| Row type | Intended template |
| --- | --- |
| `identity` | Asset, account, wallet, network or settings identity row |
| `walletGroup` | Parent and child wallet-sidebar identity rows in one group |
| `rail` | Compact horizontal item with optional native drag support |
| `activity` | Transaction/activity with amounts and up to three actions |
| `message` | Notification/message with bounded body lines and thumbnail |
| `dataRow` | Two to four fixed table columns, optional index/checkbox |
| `market` | Recycled token, stock or perpetual quote row |
| `mediaTile` | Gallery or browser-preview tile |
| `metricCard` | KPI or fixed Activity/Performance portfolio card |
| `sectionHeader` | Sticky/group, tri-state or fixed list-summary header |
| `action` | Fixed action row or fixed footer |
| `buttonGroup` | Bounded horizontal button row |
| `system` | Loading, retry, end marker or bounded spacer |

[docs/ROW_TEMPLATES.md](docs/ROW_TEMPLATES.md) defines every template with
structural illustrations, required fields, variants and styling boundaries.

### Layout kinds

- `linear` is a full-width row stream.
- `sectioned` gives section-header rows a native visual break and optional
  pinning.
- `grid` uses two to seven native spans; structural rows span every column.
- `table` uses compact, alternating rows with stable weighted columns and column
  alignment.

Vertical or horizontal orientation, stable keys, grouped card geometry, an empty
state and a fixed footer are common capabilities. The required theme tokens
cover list, row, selection, text, separator, accent and semantic
positive/negative colors; optional tokens let native templates match the OneKey
palette without embedding platform-specific style objects. Set a section
header's `variant` to `summary` for the fixed list-summary template.

For a native trailing section index, use a vertical `sectioned` layout, set
`capabilities.sectionIndex` to `{ enabled: true }`, and add an `indexTitle` to
each section header that should appear in the index. The index follows snapshot
order and scrolls to the header's existing row `key`. Titles must be unique
after Unicode NFC normalization, contain no leading/trailing whitespace and stay
within eight Unicode code points. On Web the same entries render as a DOM
overlay with click and pointer-drag navigation.

### Fonts

Templates distribute and register no font files. Call
`configureNativeListFonts` once during UI-runtime startup, after the host loads
its fonts and before mounting lists:

```tsx
import { configureNativeListFonts } from '@onekeyfe/react-native-native-list';

configureNativeListFonts({
  regular: 'Roobert-Regular',
  medium: 'Roobert-Medium',
  semibold: 'Roobert-SemiBold',
  bold: 'Roobert-Bold',
});
```

The four optional faces replace the whole mapping; omitted faces and `{}` use
system fonts. On iOS these are registered PostScript names, on Android they are
React Native family keys or matching `fonts/<family>.ttf` host assets, and on Web
they are the host's CSS families. The host remains responsible for licensing and
provisioning. Changed mappings rebind text and invalidate list geometry without
replacing snapshots or selection; an active reorder is cancelled. Names must be
nonempty trimmed strings of at most 128 UTF-16 units without control
characters — invalid keys or values throw before any configuration changes.

### Market rows

`market` is a fixed native/DOM template, not a React `renderItem`; all displayed
values are already formatted strings. Its default token/perpetual geometry uses
a 32-point leading image with a 16-point network corner, while stock uses a
40-point image. Compact zero runs can use `priceSegments`, `subtitleSegments` or
`change.textSegments` with `{ style: 'subscript' }`.

Badges accept localized text, a bounded remote image or the built-in
`iconName: 'verified'` glyph, and a badge `actionKey` emits with an anchor whose
source is `marketBadge`. Market long press fires after 800 ms and is cancelled
when the pointer moves more than 10 logical pixels or the list scrolls.
Quote-only patches (`price`, `priceSegments`, `change`, `accessibilityLabel`,
`revision`) update visible labels without rebinding the image.

`style` is deliberately finite and uses the shared row-style vocabulary in
[docs/STYLE_SPEC.md](docs/STYLE_SPEC.md); unknown keys at any level are rejected
by `validateSnapshot`/`validatePatches` rather than ignored. Loading, empty,
retry and pagination states use the existing `system` rows with
`presentation: 'market'`.

For native reorder, set `capabilities.reorderable` and mark eligible rail rows
with `draggable: true`, or add a `drag` trailing accessory to an identity row.
Reordering never crosses a `sectionKey` boundary.

### Images inside cells

Pass an `ImageSource` descriptor in the row model; do not pass a
`<OneKeyImage />` element:

```ts
const avatar = {
  uri: 'https://example.com/avatar.webp',
  width: 48,
  height: 48,
  headers: { Authorization: 'Bearer token' },
  contentFit: 'cover',
  cachePolicy: 'memory-disk',
  autoplay: false,
  optimizeTos: true,
  overscan: 1.1,
  loadingStrategy: 'static',
} as const;
```

NativeList defaults `loadingStrategy` to `none`, so source-backed images render
without a loading background or terminal fallback; set it to `static` or
`skeleton` to opt into loading UI. Image width and height are required and
bounded to 1–4096 logical pixels. Each cell owns a fixed pool of reusable native
image slots covering token/network overlays, two activity visuals, a message
thumbnail and stacks of two or three images. On bind, NativeList forwards the
URI, headers, content fit, cache policy, autoplay, TOS options, overscan and
loading strategy, assigns a stable `rowKey:slot` recycling key, and keeps the
same SDWebImage (iOS) / Glide (Android) cache pipeline as
`@onekeyfe/react-native-image`.

### Patches, events and scrolling

Patches are validated as a batch, keyed by row key, must preserve the row
discriminator, and are applied atomically. Prefer a new snapshot for insertions,
removals, layout changes and section changes; use patches for frequently
changing fields.

`applyPatches` applies a validated batch to the current snapshot;
`reconcileSelection` replaces native selection without emitting a second delta. Object scroll parameters follow
React Native list semantics: `viewPosition` runs from `0` (start) through `1`
(end), and a positive `viewOffset` leaves space before the target row. Explicit
position alignment also centers/ends oversized rows on all three platforms.
`initialScrollIndex` or the stable-key extension `initialScrollKey` sets the
first position once, after the first snapshot and native layout are ready, and
can be combined with `initialScrollViewPosition` and
`initialScrollViewOffset`. `scrollToLocation` counts non-summary section headers,
then rows carrying that header's `sectionKey`. Invalid index and location
requests call `onScrollToIndexFailed`; native lists do not fail merely because a
target row has not been mounted yet.

### Example app and native checks

The React Native example application contains a categorized native-list entry
page, a reproducible benchmark page, and an account-selector stress page
(1,000 wallets × 1,000 accounts) that materializes only the selected wallet's
rows.

- [Native list entry page](../../example/react-native/pages/NativeListExamplePage.tsx)
- [Row style page](../../example/react-native/pages/NativeListRowStylePage.tsx)
- [Benchmark page](../../example/react-native/pages/NativeListBenchmarkPage.tsx)
- [Account selector page](../../example/react-native/pages/NativeListAccountSelectorPage.tsx)

Swift geometry regressions compile the shipping helper directly:

```sh
swift test --package-path tests/swiftpm
```

The Android scroll-alignment suite runs against the package's Kotlin helper in a
configured consumer project:

```sh
./gradlew :onekeyfe_react-native-native-list:testDebugUnitTest \
  --tests com.margelo.nitro.nativelist.NativeListScrollAlignmentTest
```

Both are geometry unit tests. Full-app visibility, deferred behavior and
consumer-owned layout/input cancellation require separate runtime validation;
see [docs/SPEC.md](docs/SPEC.md). [docs/DESIGN.md](docs/DESIGN.md) documents the
architecture and ownership model.
