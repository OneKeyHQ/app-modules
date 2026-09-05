# @onekeyfe/react-native-native-list

Nitro View, template-driven native list for React Native 0.86. iOS renders a
`UICollectionView`; Android renders a `RecyclerView`. JavaScript sends one
serializable snapshot or batch patch instead of mounting a React subtree for
every row.

## Install

Keep NativeList and OneKeyImage on the same release because recycled image
slots use the native host exported by OneKeyImage.

```sh
yarn add @onekeyfe/react-native-native-list@3.0.101 \
  @onekeyfe/react-native-image@3.0.101 \
  react-native-nitro-modules@0.37.0
cd ios && pod install
```

The package requires the React Native new architecture. Its deployment floors
are iOS 15.5 and Android API 24.

## Basic usage

```tsx
import { useMemo, useRef, useState } from 'react';
import {
  NativeList,
  type NativeListRef,
  type NativeListSnapshot,
  type SelectionDeltaEvent,
} from '@onekeyfe/react-native-native-list';

export function AssetList() {
  const listRef = useRef<NativeListRef>(null);
  const [selectedKeys, setSelectedKeys] = useState<readonly string[]>([]);
  const snapshot = useMemo<NativeListSnapshot>(
    () => ({
      schemaVersion: 1,
      generation: 1,
      layout: { kind: 'sectioned', stickyHeaders: true, itemSpacing: 1 },
      selection: {
        mode: 'multiple',
        selectedKeys,
        rowPressToggles: true,
      },
      rows: [
        {
          type: 'identity',
          key: 'btc',
          sectionKey: 'assets',
          leading: {
            kind: 'token',
            image: {
              uri: 'https://example.com/btc.png',
              width: 40,
              height: 40,
              contentFit: 'cover',
              cachePolicy: 'memory-disk',
            },
            fallbackText: 'BT',
          },
          title: 'Bitcoin',
          subtitle: 'BTC · Bitcoin',
          trailing: [
            { kind: 'value', text: '$64,230' },
            { kind: 'checkbox', state: 'unchecked', target: { scope: 'row' } },
          ],
        },
      ],
    }),
    [selectedKeys]
  );

  const handleSelection = (event: SelectionDeltaEvent) => {
    setSelectedKeys((current) => {
      const next = new Set(current);
      event.removedKeys.forEach((key) => next.delete(key));
      event.addedKeys.forEach((key) => next.add(key));
      return [...next];
    });
  };

  return (
    <NativeList
      ref={listRef}
      style={{ flex: 1 }}
      snapshot={snapshot}
      onSelectionDelta={handleSelection}
      onRowAction={({ rowKey, actionKey }) => {
        console.log(rowKey, actionKey);
      }}
    />
  );
}
```

Native selection is authoritative for immediate feedback. Persist the emitted
delta in application state, then pass that state back in a later snapshot or
call `reconcileSelection(selectedKeys)` once. Reconciliation does not emit a
second delta.

## Row and layout model

`RowModel` is a strict discriminated union. It intentionally accepts no
`ReactNode`, render function, arbitrary style object, or native object handle.

| Row type        | Intended template                                          |
| --------------- | ---------------------------------------------------------- |
| `identity`      | Asset, account, wallet, network, or settings identity      |
| `rail`          | Compact horizontal item with optional native drag support  |
| `activity`      | Transaction/activity with amounts and up to three actions  |
| `message`       | Notification/message with bounded body lines and thumbnail |
| `dataRow`       | Two to four fixed table columns, optional index/checkbox   |
| `mediaTile`     | Gallery or browser-preview tile                            |
| `metricCard`    | KPI or fixed Activity/Performance portfolio card            |
| `sectionHeader` | Sticky/group, tri-state, or fixed list-summary header       |
| `action`        | Fixed action row or fixed footer                           |
| `system`        | Loading, retry, end marker, or bounded spacer              |

Snapshots support `linear`, `sectioned`, `grid`, and `table` layout semantics.
`linear` is a full-width row stream; `sectioned` gives section-header rows a
native visual break and optional pinning; `grid` uses two to four native spans
with structural rows spanning every column; and `table` uses compact,
alternating rows with stable weighted columns and column alignment. Vertical or
horizontal orientation, stable keys, grouped card geometry, empty state, and a
fixed footer remain common capabilities.

The required theme colors cover list, row, selection, text, separator, accent,
and semantic positive/negative states. The optional `subduedBackground`,
`strongBackground`, `disabledText`, `icon`, `iconSubdued`,
`criticalBackground`, `inverseBackground`, and `inverseText` tokens let native
templates match the OneKey palette without embedding platform-specific style
objects. Identity-row trailing accessories also accept semantic native icons,
for example `{ kind: 'icon', name: 'PlusCircleOutline' }`. A `valuePair` can
independently set `primaryTone` and `secondaryTone` to `primary`, `secondary`,
`positive`, or `negative`; omitted tones keep the original primary/secondary
defaults. Token visuals can provide `networkImage` or `cornerIcon`, while media
tiles can provide `networkImage`, to reproduce OneKey's bottom-right network
and token-state indicators. `dataRow` supports a two-line asset/volume and
price/change layout plus fixed leverage/venue badges. `metricCard` supports the
fixed `activity` and `performance` composite variants used by Portfolio & PnL;
their metrics and progress remain bounded serializable data.

Set a section header's `variant` to `summary` for the fixed 56-point list
summary template. Its optional `valueActionKey` renders a 16-point secondary
action beside the 16-point medium primary title; summary headers do not accept
a checkbox and do not become sticky.

For a native trailing section index, use a vertical `sectioned` layout, set
`capabilities.sectionIndex` to `{ enabled: true }`, and add an `indexTitle` to
each section header that should appear in the index. The index follows snapshot
order, skips headers without `indexTitle`, and scrolls to the header's existing
row `key`. Titles must be unique after Unicode NFC normalization, contain no
leading/trailing whitespace, and be at most eight Unicode code points. Set
`hapticsEnabled: false` to follow an application-level reduced-haptics setting.
The web fallback accepts the same model but does not render the mobile index.

The native templates bundle OneKey's Roobert Regular, Medium, SemiBold, and
Bold faces and use them before the platform-font fallback. Their semantic
icons and checkbox marks are the same vector paths as the OneKey component
library, rendered by Core Graphics/UIKit on iOS and Canvas on Android.

For native reorder, set `capabilities.reorderable` and mark eligible rail rows
with `draggable: true`, or add a `drag` trailing accessory to an identity row.
Reordering never crosses a `sectionKey` boundary.

## OneKeyImage inside cells

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

Each cell owns a fixed pool of native `OneKeyImageReusableView` slots. On bind,
NativeList forwards the URI, headers, content fit, cache policy, autoplay, TOS
options, overscan, and loading strategy. A stable `rowKey:slot` recycling key
is assigned automatically. Reuse cancels stale work before accepting the next
source, while retaining the same SDWebImage cache/pipeline on iOS and Glide
cache/pipeline on Android as `@onekeyfe/react-native-image`.

The fixed slots cover token/network overlays, two activity visuals, a message
thumbnail, and stacks of two or three images. Image width and height are
required and bounded to 1–4096 logical pixels.

## Batch updates and events

```ts
listRef.current?.applyPatches([
  {
    type: 'identity',
    key: 'btc',
    changes: { revision: 2, subtitle: 'Updated in one native command' },
  },
]);

listRef.current?.scrollToKey('btc', true, 'center');
listRef.current?.scrollToIndex({
  index: 24,
  animated: true,
  viewPosition: 0.5,
  viewOffset: 12,
});
listRef.current?.scrollToItem({ item: snapshot.rows[24] });
listRef.current?.scrollToOffset({ offset: 320, animated: false });
listRef.current?.scrollToEnd({ animated: true });
listRef.current?.scrollToLocation({ sectionIndex: 2, itemIndex: 4 });
listRef.current?.setRefreshing(false);
```

The ref exposes `applySnapshot`, `applyPatches`, `reconcileSelection`,
`scrollToKey`, `scrollToIndex`, `scrollToItem`, `scrollToOffset`, `scrollToEnd`,
`scrollToLocation`, and `setRefreshing`. The legacy
`scrollToIndex(index, animated, alignment)` and
`scrollToKey(key, animated, alignment)` signatures remain supported. Object
parameters follow React Native list semantics: `viewPosition` is from `0`
(start) through `1` (end), and a positive `viewOffset` leaves space before the
target row.

Use `initialScrollIndex` or the stable-key extension `initialScrollKey` to set
the first position. They are mutually exclusive, run once after the first
snapshot and native layout are ready, and can be combined with
`initialScrollViewPosition` and `initialScrollViewOffset`. Invalid index and
location requests call `onScrollToIndexFailed`; native lists do not fail merely
because a target row has not been mounted yet. `scrollToLocation` counts
non-summary section headers, then rows carrying that header's `sectionKey`.

Patches are validated as a batch, keyed by row key, must preserve the row
discriminator, and are applied atomically. Prefer a new snapshot for insertions,
removals, layout changes, and section changes; use patches for frequently
changing fields.

Events are `onRowAction`, `onSelectionDelta`, `onReorder`, `onEndReached`,
`onVisibleRangeChanged`, and `onRefresh`. Visible-range events are coalesced to
one per display frame and only fire when the first or last visible item changes.
End-reached fires once per snapshot `generation`.

## Examples and performance checks

The React Native example application contains:

- **Native List**: a categorized entry page. Each of the four containers, ten
  row templates, and checkbox selection opens its own source-labeled page; no
  container or row paradigm is hidden behind tabs. The scenarios mirror the
  corresponding list surfaces in the OneKey application.
- **Native List Benchmark**: reproducible 1,000/5,000-row load, fast scroll,
  one-command 500-row patch, and one-command select-all paths.
- **Native List Account Selector**: two independently scrolling native lists
  with a fixed Add Wallet footer. The stress fixture exposes 1,000 wallets x
  1,000 accounts while materializing only the selected wallet's 1,000 account
  rows and retaining at most three wallets / 3,000 account rows in an LRU cache.

### Account Selector performance snapshot (2026-09-05)

The current acceptance run exercised five page opens, two 101-switch Wallet 1 /
Wallet 2 batches, 25 downward plus 20 upward account-list drags, a repeated 15
downward account-list run, and 15 downward plus 15 upward wallet-list drags. It
also sampled process CPU and memory and verified, after momentum settled, that
the two lists scroll independently and Add Wallet remains fixed at the bottom.
Android additionally covered ART `speed` compilation and ten short fast flings.

| Metric                    | iOS Debug Simulator                                                                                           | Android Release-mode emulator                                                                                                                    |
| ------------------------- | ------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| Environment               | iPhone 16 Pro, iOS 18.6                                                                                       | API 35 arm64, 1080 x 2400, fixed 60 Hz, non-debuggable Hermes                                                                                    |
| Page open, 5 runs         | External tap-to-visible median 589 ms; the initial native visible-range callback did not emit                 | 5 / 5 native callbacks; mount-to-first-visible p50 110 ms; external observation p50 202 ms                                                       |
| Wallet switching          | 202 / 202; mean 135.3 ms, p50 134 ms, p95 143 ms, max 159 ms                                                  | 202 / 202; mean 124.4 ms, p50 118 ms, p95 155 ms, max 213 ms                                                                                     |
| Account-list frame signal | 113.3 effective FPS; p50/p95 interval 6.67/18.33 ms; 6.6% over 17.5 ms                                        | Repeated drag runs reported 92.09-93.06% deadline-missed frames and p50 450 ms                                                                   |
| Wallet-list frame signal  | 78.7 effective FPS; p50/p95 interval 13.33/20.00 ms; 17.8% over 17.5 ms                                       | Down/up runs reported 89.39-100% deadline-missed frames and p50 400 ms                                                                           |
| Process CPU               | Wallet switching mean 48.9%; account scrolling mean 22.9%                                                     | Wallet switching mean 66.8%; account scrolling mean 67.1% on Android's per-core scale                                                            |
| Retained memory           | Physical footprint 286 MB before, 275 MB after the first batch, 287 MB after the second; lifetime peak 335 MB | PSS 138,459 KiB with one wallet cached, 201,668 KiB after warming three wallets, 192,700 KiB after the second batch, 193,976 KiB after scrolling |
| Functional checks         | Independent lists and fixed footer pass                                                                       | Independent lists and fixed footer pass, including zero-difference stable screenshot crops                                                       |

Across the tested environments, Android Release is faster for page opening and
median wallet switching: external page-open p50 is 202 ms versus 589 ms on iOS,
and wallet-switch p50 is 118 ms versus 134 ms. iOS has the tighter switch tail
at p95 143 ms versus 155 ms, clearly leads both account-list and wallet-list
frame results, and uses less process CPU in the measured switch and scroll runs.
Both platforms pass correctness, independent scrolling, the fixed footer,
switch responsiveness, and the short-run retained-memory check. The current
cross-platform performance priority is Android continuous scrolling, followed
by the iOS wallet list's 120 Hz tail behavior.

### Test Suite source map

| Entry               | OneKey application source                                                                    |
| ------------------- | -------------------------------------------------------------------------------------------- |
| Linear container    | `AssetList/components/TokenManager/TokenManagerList.tsx`                                     |
| Sectioned container | `ChainSelector/components/AllNetworksManager/NetworksSectionList.tsx`                        |
| Grid container      | `Home/components/NFTListView/index.tsx`                                                      |
| Table container     | `Perp/components/TokenSelector/PerpTokenSelectorRow.tsx`                                     |
| Identity rows       | `Developer/pages/Gallery/Components/stories/ListItem.tsx`                                    |
| Rail rows           | `Perp/components/FavoritesBar/FavoriteTokenItem.tsx`                                         |
| Activity rows       | `components/TxHistoryListView/TxHistoryListItem.tsx`                                         |
| Message rows        | `Notifications/components/NotificationListView.tsx`                                          |
| Data rows           | `Perp/components/TokenSelector/PerpTokenSelectorRow.tsx`                                     |
| Media tile rows     | `Home/components/NFTListView/NFTListItem.tsx`                                               |
| Metric card rows    | `Perp/components/Portfolio/PerpPortfolioContent.tsx`                                         |
| Section header rows | `AllNetworksManager/NetworksSectionList.tsx`, `TxHistoryListView/TxHistorySectionHeader.tsx` |
| Action rows         | `TokenManager/TokenManagerList.tsx`, `Discovery/pages/BookmarkListModal/index.tsx`           |
| System rows         | `components/Loading/ListLoading.tsx`, `TokenListView/CrossNetworkSearchRows.tsx`             |
| Checkbox selection  | `components/forms/Checkbox/index.tsx`, `AllNetworksManager/NetworksSectionList.tsx`          |

The checkbox page exercises list, section, and row targets plus checked,
unchecked, indeterminate, loading, and disabled presentation. The rail page
keeps horizontal selection and native reorder isolated from the other pages.

Measure a release build on a physical device. Run each scenario at least three
times, record cold and warm first-visible latency, JS batch-dispatch latency,
UI-thread frame time/dropped frames, and peak memory. During repeated fast
scrolls verify that no stale image from a recycled row becomes visible. The
benchmark page reports deterministic JS timings and visible-range milestones;
use Instruments or Android Studio Profiler for native frame and memory data.

## Limits

- Nitro View/new architecture only; no Paper component.
- Fixed templates only. Arbitrary children, per-row render functions, and
  arbitrary style dictionaries are rejected by the public model.
- Template heights and text line counts are bounded. There is no unbounded
  self-sizing content, nested list, or React component embedded in a cell.
- A snapshot prop is the declarative baseline. Re-rendering with an older
  snapshot after imperative patches or reorder will intentionally restore the
  older state.
- The web implementation is a basic compatibility fallback, not the native
  performance path and not a replacement for the mobile benchmark.

See [docs/DESIGN.md](docs/DESIGN.md) for the architecture and ownership model.
