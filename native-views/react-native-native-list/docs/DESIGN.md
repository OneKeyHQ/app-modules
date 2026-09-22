# Native List Design

## Scope

`@onekeyfe/react-native-native-list` is a Nitro View, template-driven list for
React Native 0.86. The native view owns cell creation, recycling, layout,
selection feedback, image request cancellation, and scrolling. JavaScript sends
normalized plain data; rows never contain a `ReactNode`, function, or arbitrary
style object.

The component supports 12 row types: `identity`, `walletGroup`, `rail`, `activity`,
`message`, `dataRow`, `market`, `mediaTile`, `metricCard`, `sectionHeader`, `action`,
and `system`. [ROW_TEMPLATES.md](ROW_TEMPLATES.md) defines each with an illustration.
`metricCard` supports a compact KPI template and the fixed `activity` and
`performance` Portfolio & PnL composites. Their bounded metric arrays and
progress value have dedicated native binders on both mobile platforms.

## Public data boundary

The exported TypeScript model is a discriminated union. Every row has a stable
`key`, bounded text and accessory arrays, and a fixed `size`/`density` variant.
Business concepts such as asset, account, network, wallet, and settings map to
the same presentation templates instead of introducing business-specific row
types.

Each row also accepts a bounded `style`, whose keys name model fields rather than
views. [STYLE_SPEC.md](STYLE_SPEC.md) is the shared vocabulary: the design tokens,
the per-template style surface with each platform's current values, the template
isolation rules, and the cross-platform divergences that are registered rather
than fixed. Its container capability contract keeps header/footer placement,
sections, scrolling and the indexed bar independent of content templates. A local
style must preserve the template skeleton and allocated row frame. Developers
integrating the list own overflow prevention and choose fitting content, style
values and row heights; the component does not implement combination-fit checks
or automatic size correction.

The wrapper validates and normalizes data before serializing it. A structural
change is one snapshot payload. Frequently changing fields use one batch patch
payload keyed by row key. Neither API performs one native call per row. The
native side rejects malformed or duplicate-key payloads without partially
applying them.

Section headers and scrolling footer/status rows use the flattened row stream.
`fixedFooter` is a separate descriptor outside that stream, and `emptyState` is
used when content is empty; both reuse row binders and currently accept only
`action` or `system` through the public API. A generic public list-header slot is
not yet exposed. `groupId` and `groupPosition` preserve continuous card geometry
without nesting lists inside cells. These placement/grouping rules belong to the
container even when the displayed content uses an existing row template.

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

### Incremental renderer migration

Template dispatch is a closed-set lookup, not a state machine. The target
ownership is container → template registry → renderer → shared primitives:

- The container owns data, placement, sections, header/footer, scrolling,
  indexed bar, selection and reorder.
- A renderer owns its view structure, template defaults, semantic text/image
  targets, measurement, binding and supported partial updates.
- A row host owns shared container appearance, events and binding invalidation.
  Image/text primitives do not branch on business keys or template names.
- `row.key` identifies data. A structural reuse key identifies a compatible
  renderer/view tree; style values and item keys must not enter that key.
- Resolved style must come from template defaults, theme, model and explicit
  style, never from the previous bound view. Rendering and measurement consume
  the same resolved inputs. Height precedence remains `style.container.height`
  → `row.height` → template measurement/default. There is no automatic fit pass.
- Full binding must be correct without a preceding recycle callback. Image
  descriptors that did not change should retain their bindings. Async work and
  action anchors must still be invalidated by the host's binding epoch.

Migration begins with `message`, one platform per commit. Its current boundary:

| Platform | Extracted | Still owned by the existing host |
| --- | --- | --- |
| Web | DOM structure, semantic text roles, estimated measurement in `src/web/templates/MessageRowRenderer.ts` | Shared style/asset primitives; wrapper pooling and body replacement |
| Android | Text subtree allocation, default typography/reset, content binding and direct title/body/time style targets in `NativeListMessageRenderer.kt` | Legacy host allocation, image slots, box styles, measurement and style restoration |
| iOS | Text subtree allocation, default typography/reset, content binding, semantic text targets and existing measured height in `NativeListMessageRenderer.swift` | Legacy host allocation, image slots, box styles, style restoration and image lifecycle |

The migration still keeps existing platform defaults and the native monolithic
hosts. It does **not** yet establish a renderer registry, lightweight native
hosts or complete resolved-style pipeline. Native Message text views are now
allocated lazily by the renderer and no longer borrow the legacy title/body/time
slots. Their text primitives are shared with the legacy templates; line-gap
styling targets the renderer's own column. The legacy host still allocates its
unused text views, so this intermediate extraction makes no allocation savings
claim. [SPEC.md](SPEC.md#conformance-and-six-stage-migration) tracks all six stages.
The callback parameters are temporary adapters to the existing image/text
primitives, not a public plugin API. Shared legacy slot maps still serve
unmigrated templates and will be retired as ownership moves.

Structural reuse is now partitioned into two internal families on all platforms:
`message` and `legacy`. Unmigrated templates retain their existing shared tree.
Neither `row.key`, content, style, height nor placement changes the family.
Web selects a compatible wrapper pool even when the row at a mounted index
changes family. iOS registers separate reuse identifiers and reloads retained
keys whose family changed; compatible updates still reconfigure, and newly
inserted keys are not marked for reload/reconfigure. Android uses the family as
`getItemViewType`; DiffUtil identifies data by `row.key`, allowing RecyclerView
to replace an incompatible holder. Market quote and selection payloads retain
their existing full/partial update decisions. Footer and nested-row hosts remain
outside these scrolling pools.

Both native families still allocate the legacy cell/row class. The next Message
step extracts the remaining shared image primitives and replaces that class with
a lightweight host for the renderer-owned tree, connecting its factory to the
existing reuse family. Resolved styles and update classification still need to
move into the renderer. Preserve Market quote and selection update paths
throughout migration.
Move the remaining simple templates only after this lifecycle works on all
three platforms; migrate Market/Identity/Header and composed wallet groups last.

Acceptance for every step includes style set/change/clear, binding A → B without
recycle, same-key template changes, scrolling away/back, image add/remove,
explicit height restoration and shared footer placement. Keep geometry and
typography changes in separate commits from structural extraction.

The first extraction was checked on 2026-09-22:

- Web: 149 tests, typecheck and lint (zero errors); actual Chrome desktop and
  390px RTL rendering, rapid style/template switching, image decoding and
  scrolling through 41 messages. Styled height 192 returns to model height 136.
- Android: Kotlin build and seven unit tests; an isolated RN app using the real
  NativeList/Image/Logger/Skeleton/Nitro packages ran on API 36. Style clearing,
  same-key Message/Identity switching, content updates, image add/remove,
  scroll-to-end/back and the fixed footer were checked. At the test device's
  density, legacy model height 136 rendered as 122px through the existing scale;
  explicit style height rendered as 192px and clearing restored 122px. The
  styled three-line body measured 66px; clearing restored its one-line box.
- iOS: the isolated RN app compiled and linked with the real
  NativeList/Image/Logger/Skeleton/Nitro packages, then ran on a dedicated
  iPhone 17 Pro simulator with iOS 26.5. Its data volume is an APFS sparsebundle
  stored on the external drive and mounted at the simulator's standard data
  directory. Style set/clear restored row height 136 → 192 → 136pt and body
  height 20 → 66 → 20pt. Same-key Message/Identity switching, content updates,
  leading image/thumbnail add/remove and scrolling to row 39/back passed;
  fixed-footer placement stayed unchanged. Screenshots, accessibility trees
  and a screen recording were retained. The build used the example app's iOS
  16.4 deployment target for all pods because Xcode 27 rejects older pod targets.
  Sampled gallery scrolling and style off/on checks also covered Message
  appearance reset and top/center/bottom container alignment with explicit height.

These results cover the Message extraction, not native acceptance of every
template or completion of the target architecture.

The structural reuse follow-up on 2026-09-22 passed 150 package tests, typecheck,
lint (zero errors), the Android build/seven unit tests and a full iOS build.
Chrome, Android API 36 and iOS 26.5 ran 41 alternating Message/Identity rows,
swapped every template while retaining keys, emptied/repopulated the list and
scrolled end/back. Style reset, image add/remove and fixed-footer placement were
checked again. Web additionally verifies wrapper identity in regression tests;
runtime checks included ten Web and five iOS mixed-template switching cycles.
No browser page errors, Android crashes or iOS runtime exceptions were observed.

The Message text ownership follow-up on 2026-09-22 merged main `52849b86f`,
regenerated the ignored Image/List bindings and rebuilt both real native apps.
The 150 package tests, typecheck, lint (zero errors), Android build/seven unit
tests and full iOS build passed. Both native runtimes preserved the sampled
pre-extraction title/body/time geometry. They passed style and height clearing,
same-key template switching, content replacement, image add/remove, scrolling
end/back, mixed-family replacement and empty/repopulation with the fixed footer.
Additional cases set font size/weight, lineGap, horizontal/vertical alignment,
optical offset and line limits, then cleared them or rebound empty body/time
without recycling; no stale text remained and default geometry was restored.
Chrome desktop and 390px RTL passed the same applicable Message cases. Runtime
screenshots, accessibility trees and an iOS recording were retained outside the
repository. These checks cover this text-subtree increment, not completion of
stage 2 or full native acceptance of every template.

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

`style.container.height` defines an explicit row height before legacy `row.height`.
Without either, templates use their existing preset or measured sizes. Styling
can change measured sizes; explicit heights remain exact, and clearing the style
restores the model/template height. `message` body text is bounded to three lines
and uses a deterministic measurement. System spacers
are explicit and bounded. Images have explicit sizes, and grid columns are fixed
per snapshot. This keeps layout work predictable for 1,000- and 5,000-row
workloads without an unbounded self-sizing pass.

The native list runs in the main UI runtime. OneKey's background and main
JavaScript heaps are not shared, so this API accepts normalized serializable data
and never depends on background-runtime object identity.

### Template versus style

A row template (`type` and declared variant/presentation) defines structure,
semantic fields and interactions. `row.style` only changes allowlisted local
properties of those fields; it does not add children or rearrange the template.
The [shared style matrix](STYLE_SPEC.md#41-shared-configurable-properties) defines
the same configurable properties and semantics for Web, iOS and Android.
Header/footer placement, sections, scrolling and indexed bar belong to the
container. The integrating developer owns content fitting and row-height choices.
