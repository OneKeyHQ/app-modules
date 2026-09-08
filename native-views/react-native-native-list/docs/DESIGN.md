# Native List Design

## Scope

`@onekeyfe/react-native-native-list` is a Nitro View, template-driven list for
React Native 0.86. The native view owns cell creation, recycling, layout,
selection feedback, image request cancellation, and scrolling. JavaScript sends
normalized plain data; rows never contain a `ReactNode`, function, or arbitrary
style object.

The component supports `identity`, `rail`, `activity`, `message`, `dataRow`,
`mediaTile`, `metricCard`, `sectionHeader`, `action`, and `system` rows.
`metricCard` supports a compact KPI template and the fixed `activity` and
`performance` Portfolio & PnL composites. Their bounded metric arrays and
progress value have dedicated native binders on both mobile platforms.

## Public data boundary

The exported TypeScript model is a discriminated union. Every row has a stable
`key`, bounded text and accessory arrays, and a fixed `size`/`density` variant.
Business concepts such as asset, account, network, wallet, and settings map to
the same presentation templates instead of introducing business-specific row
types.

The wrapper validates and normalizes data before serializing it. A structural
change is one snapshot payload. Frequently changing fields use one batch patch
payload keyed by row key. Neither API performs one native call per row. The
native side rejects malformed or duplicate-key payloads without partially
applying them.

Section headers, footers, separators, empty rows, and fixed-footer descriptors
are represented as rows in one flattened native data source. `groupId` and
`groupPosition` preserve continuous card geometry without nesting lists inside
cells.

## Native architecture

- iOS 15.5+: `UICollectionView`, `UICollectionViewDiffableDataSource`, and a
  native flow layout.
- Android API 24+: `RecyclerView`, `ListAdapter`, and `DiffUtil.ItemCallback`.
- Web: a platform-resolved React host backed by a pure DOM engine. The engine
  owns layout, viewport windowing, recycled row elements, selection feedback,
  section-index navigation, and the same imperative API as mobile; React does
  not create a component subtree per row.
- Nitro View: the generated HybridView host receives the initial snapshot as
  one string and exposes HybridView methods for subsequent snapshots, patches,
  selection reconciliation, and scrolling. Nitro callback props carry bounded
  JSON payloads which the wrapper decodes into strict public event types.

`linear`, `sectioned`, `grid`, and `table` are observable native container
semantics. Linear is a full-width stream. Sectioned adds a visual break before
section-header rows and can pin those rows. Grid allocates native spans and
makes structural rows full-span. Table uses compact alternating rows and fixed
weighted/aligned columns. Orientation, refresh, load-more, visibility events,
empty state, fixed footer, and native reordering are capabilities, not row
types.

## Section index

The mobile section index is an opt-in capability for vertical `sectioned`
snapshots. Indexed section headers carry a bounded `indexTitle`; their stable
row `key` is also the scroll target, so the feature needs no extra bridge event
or parallel identity. Headers without `indexTitle` remain in the list but are
omitted from the rail, which supports sparse alphabets and dynamic search
results. An enabled snapshot with no indexed headers hides the rail.

iOS and Android render a native trailing overlay, reserve a trailing content
gutter, follow RTL layout direction, highlight the visible section, and expose
the rail as one adjustable accessibility control. Touch-drag navigation scrolls
immediately, shows a centered title preview, and emits selection feedback when
the active entry changes unless `hapticsEnabled` is false. Web renders the same
explicit index entries as a DOM overlay with click and pointer-drag navigation.
Snapshot order is authoritative; no platform sorts, uppercases, or synthesizes
missing entries.

## Selection ownership

Native state is authoritative for immediate visible feedback. `none`, `single`,
and `multiple` modes are supported. Row presses may toggle selection. Row,
section, and list actions update all affected keys in one native transaction and
emit one `selectionDelta` containing `addedKeys` and `removedKeys`.

Checkboxes support `checked`, `unchecked`, and `indeterminate`, plus `disabled`
and `loading`. Section and global three-state values are derived from selectable
descendants. JavaScript may later reconcile native state with one selection
snapshot or batch patch; reconciliation does not emit another delta.

## Images and OneKeyImage

A React `OneKeyImage` component cannot be mounted inside a recycled native cell
without reintroducing a React subtree per row. Instead, OneKeyImage source data
is normalized into the list's bounded `ImageSource` descriptor. Each fixed image
slot embeds `OneKeyImageReusableView`, the native-only host exported by
`@onekeyfe/react-native-image`. This means iOS uses the same SDWebImage pipeline
and Android uses the same Glide pipeline as `OneKeyImage`, including its cache,
SVG/WebP/AVIF support, TOS sizing, animated-image safety, placeholders, and
recycling behavior.

`ImageSource` mirrors the serializable OneKeyImage request options: URI,
dimensions, headers, content fit, cache policy, autoplay, TOS optimization,
overscan, and loading strategy. Every bind supplies a stable row/slot recycling
key. Rebinding or recycling calls OneKeyImage's recycle hook, which cancels stale
work before the slot accepts its next source. Token/network overlays, double
activity visuals, and stacks of up to three sources all use the same fixed slot
pool.

## Update and event policy

Snapshot application uses stable keys and platform diffing. A patch updates only
changed models and rebinds/reconfigures only the corresponding visible cells.
Selection-only changes use native payload updates and never require rebuilding
all JavaScript rows.

`visibleRangeChanged` is coalesced to at most once per animation frame and only
emitted when the first/last visible keys change. `endReached` is edge-triggered
per content generation. Other public events are `rowAction`, `selectionDelta`,
and `reorder`.

## Height, layout, and performance constraints

Template sizes are fixed to a small set of variants. `message` body text is
bounded to four lines and uses a deterministic template height. System spacers
are explicit and bounded. Images have explicit sizes, and grid columns are fixed
per snapshot. This keeps layout work predictable for 1,000- and 5,000-row
workloads without an unbounded self-sizing pass.

The native list runs in the main UI runtime. OneKey's background and main
JavaScript heaps are not shared, so this API accepts normalized serializable data
and never depends on background-runtime object identity.
