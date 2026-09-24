# NativeList contract

This is the entry point for the package's behavioral contract. The linked
documents are part of this spec; implementation status is separate from target
architecture and runtime evidence.

## Purpose and non-goals

NativeList renders bounded data with fixed row templates on iOS, Android and
Web. It does not render arbitrary React children. Integrators own overflow
prevention; there is no automatic fitting or shrinking pass.

## Definitions and ownership

- `rowTemplate` defines structure, semantic fields and supported variants.
  [ROW_TEMPLATES.md](ROW_TEMPLATES.md) defines and illustrates every template.
- `rowStyle` adjusts the template's named text, image, spacing and container
  properties. It can set row height but cannot replace the template structure.
  [STYLE_SPEC.md](STYLE_SPEC.md) defines the common property vocabulary.
- The list owns placement, sections, scrolling, indexed bar, selection and
  reorder. These capabilities do not belong to individual row templates.
- The renderer owns template views, defaults, binding and measurement. A shared
  host owns row appearance, events and binding invalidation. This ownership is a
  migration target, not a claim that the current native hosts are already split.

## Public API and defaults

[README.md](../README.md) documents the snapshot, patch, selection, events and
imperative API; [STYLE_SPEC.md](STYLE_SPEC.md) defines normalization, omitted
style defaults and platform differences. Height precedence is
`style.container.height` → `row.height` → template measurement/default.
Removing a style restores the current template's defaults, not the previous
row's appearance. Selector presentations (`accountSelector`, `networkSelector`,
`walletSidebar`) use their explicit-height geometry whenever either
`style.container.height` or `row.height` is set.

The row style surface (`row.style`, including `style.container`) and
`snapshot.listStyle` are **new public TypeScript/JSON protocol in this change**;
they travel inside the existing `snapshotJson` string, so the Nitro schema is
unchanged. Validation of that surface is strict: undeclared or unknown nested
keys are rejected (see Failures below and STYLE_SPEC.md §2). The renderer
migration itself does not change any other protocol field.

## Lifecycle and concurrency

Full binding must work without a preceding recycle callback, including
style set/change/clear and content A → B. A binding epoch invalidates stale
image callbacks and action anchors. Recycling/disposal releases pending work
through the existing image/host lifecycle. Image loading visibility belongs to
the image lifecycle: restoring template defaults on rebind must never hide an
image that already loaded. Common container styling clears what it wrote on the
next bind, including properties it set on template children. UI mutation remains on the platform
UI thread; [DESIGN.md](DESIGN.md) describes update and event ownership.

## Data, cache and identity

`row.key` identifies data; renderer reuse identity describes compatible view
structure. Content, styles, height and placement must not enter reuse identity.
All twelve templates have their own renderer family: Message, Rail, MediaTile,
Action, System, Activity, DataRow, MetricCard, Market, Identity, SectionHeader and
WalletGroup. Same-key family changes must replace the incompatible host. OneKeyImage owns image caches; NativeList owns row/slot binding identity.

## Platform contract

All platforms expose the same bounded style properties and public semantics.
Existing geometry/typography differences are recorded in
[STYLE_SPEC.md](STYLE_SPEC.md); structural extraction must not silently change
them. iOS uses UICollectionView, Android RecyclerView, and Web a windowed DOM
engine. See [DESIGN.md](DESIGN.md) for their current ownership boundaries.

## Failures, fallback and limits

Snapshot validation rejects malformed/duplicate-key input without partial
application. Template field/array bounds and image fallback behavior are defined
in [ROW_TEMPLATES.md](ROW_TEMPLATES.md), [README.md](../README.md) and
[DESIGN.md](DESIGN.md). This change adds the style surface's limits, defined in
[STYLE_SPEC.md](STYLE_SPEC.md) §3.3/§4/§5: numeric bounds, `#RRGGBB`/`#RRGGBBAA`
colors (including `listStyle.separator.color`), `groupCornerRadius` `0…40`, and
rejection of any undeclared or unknown nested style key by `validateSnapshot`
and `validatePatches`, where extra keys used to be ignored. It adds no retry
policy or automatic layout correction.

## Performance and resources

Snapshots and patches cross the bridge in batches. Visible rows use platform
recycling/windowing; shared primitives own image cancellation. All twelve templates use dedicated lightweight native hosts and persistent Web
bodies. WalletGroup composes the migrated Identity member hosts.
None allocates the legacy native tree. This structural change is
not a measured scrolling-performance result; performance claims still need a
separate workload and measurements.

## Conformance and six-stage migration

| Stage | Current status | Exit condition |
| --- | --- | --- |
| 1. Baseline | Complete: [implicit keys, defaults, special updates and acceptance baseline](MIGRATION_BASELINE.md) inventoried | Preserve each existing rule or explicitly migrate its caller |
| 2. Message pilot | Complete: registered lightweight native hosts, persistent Web body, resolved styling/measurement and lifecycle acceptance | Lightweight native hosts, resolved styling/measurement, update classification and lifecycle acceptance |
| 3. Simple templates | Complete (7/7): Rail, MediaTile, Action, System, Activity, DataRow and MetricCard migrated on all three platforms | Migrate mediaTile, rail, action, system, activity, dataRow and metricCard; remove each old dispatch/reset path |
| 4. Complex templates | Complete (3/3): Market, Identity and SectionHeader migrated on all three platforms | Migrate Market, Identity and SectionHeader while preserving quote, selection and header behavior |
| 5. WalletGroup | Complete: registered composites and independent keyed Identity members on all three platforms | Independent member renderers; verify member events/styles, compact dragging and height restoration |
| 6. Cleanup | Complete: obsolete hosts/maps/fallback pools and audited key styling removed | Remove migrated key inference and global style maps; container no longer manipulates template internals |

The closed internal registry selects all twelve renderers by template type.
No supported template dispatches to the legacy native tree. WalletGroup owns
composition and compact appearance; its members use independent Identity
renderers. Legacy native classes, global style maps and fallback registry entries
are removed, including footer initialization. Web preserves compatible bodies and
unchanged member image elements across rebinding.

Message resolves text/spacing/image inputs from data/theme/styles, classifies
updates as unchanged, content/style, assets or replacement, and binds/measures
using those resolved values. Image slots retain an unchanged effective request;
source/row/slot or image-request-option changes invalidate old callbacks and
retries. Image request epochs are independent of row action-anchor epochs, so
text-only updates preserve in-flight images. Legacy Message binding, slot-map
and measurement branches have been removed. Stage 6 also removes the audited Android separator-key
compatibility policy; the explicit separator field controls visibility.

Message intrinsic measurement now uses its actual resolved view metrics rather
than the historical estimator-only defaults listed in MIGRATION_BASELINE.md.
This can change **automatic** Message heights; explicit row/container heights
and unstyled drawing defaults remain unchanged. Native measurement accounts
for the allocated column width, line limits, gaps and optional images. Web uses
the same resolved geometry with a conservative text estimate, then corrects the
visible intrinsic height from its rendered DOM; it does not auto-fit content.

Source boundaries and dated runtime evidence are in the incremental migration
section of [DESIGN.md](DESIGN.md). Acceptance covers style set/change/clear,
binding without recycle, same-key template changes, image add/remove, scrolling
away/back, explicit height restoration and fixed-footer placement. Unit/type
checks are not native runtime verification, and Message acceptance does not
complete the remaining stages.

Stage 3 extends the closed registry one template at a time. Migrated templates
own their view trees and semantic styles; native templates share the Message
host's appearance/event lifecycle and bounded image primitives, not a legacy
view tree. Preserve existing default geometry, variant behavior, actions,
selection echo and platform differences. Horizontal sizing belongs to each
renderer and must use its resolved text/image/spacing inputs. A style change
must invalidate horizontal placement when its measured width changes. Fixed
template height defaults remain defaults, not automatic content fitting.
DataRow keeps the inventoried `asset`-column Web badge behavior until a separate
caller migration; this extraction does not change which column owns badges.

Rail owns its title/badge/status views and leading visual. Shared native hosts
own appearance, action-anchor epochs, selection routing and reuse invalidation;
Web uses the same primitive interface for Message and Rail. Legacy Rail bind,
style-slot, reset and width branches have been removed. Horizontal measurement
now responds to explicit fonts, image dimensions, padding and gaps while
retaining the existing default width allowances and platform limits. Text-only
rebinding preserves unchanged image requests; clearing styles restores defaults.
All seven stage 3 templates are now migrated; dated acceptance for each is recorded in DESIGN.md.

MediaTile owns its picture, network indicator, title/subtitle, badge and close
control. Its legacy bind/style/reset allocation paths are removed. Images retain
unchanged requests across content/style updates; close events use the shared
host epoch with `mediaClose` source. Grid and horizontal allocation remain
container-owned. Default iOS grid height stays width + 48, including existing
compression when a close control is present; explicit container heights let the
caller allocate more room. No overflow fitting is added.

Action owns its title, optional icon and bounded accessory controls. Native
checkbox presentation can update without rebinding the title/icon; all actions
carry the shared host epoch and preserve the original account-control anchor
inset. Fixed footers now also resolve their host through the closed registry
and replace it when their renderer family changes. Placement and gesture
routing remain list-owned.

MetricCard owns standard, activity and performance variants. Its existing
per-platform measurement, badge colors, composite spacing and progress layout
remain template defaults. Standard-card styles address title/value/subtitle/trend;
composite cards expose the heading slot and keep metric internals fixed as
defined in STYLE_SPEC.md. Image slots retain unchanged requests across rebinding
and reset when a source disappears or its slot identity changes.

All six stages are complete. Stages 4–6 and their acceptance scopes are recorded
below; the baseline inventory explicitly identifies two retained compatibility
policies outside renderer selection/style mapping.
No migrated simple template dispatches through a legacy binder.

### Stage 4 acceptance scope

Market, Identity and SectionHeader move to dedicated registered renderers on all
three platforms. This stage preserves existing defaults and implicit-key
compatibility recorded in MIGRATION_BASELINE.md. It adds no style fields beyond
the style surface this change introduces.
Market owns quote-only binding and its platform row interactions; Identity owns
selection presentation; SectionHeader owns stable summary updates and measurement.
Sticky placement and header/footer routing stay in the list container.
Acceptance includes variant changes on the same key, styled → unstyled rebinding,
unchanged image requests, partial updates with current action context, selection,
scroll/recycle, and shared footer/sticky-header behavior.

Each complex renderer now owns its view tree, defaults, semantic style slots and
measurement. Native hosts share bounded leading/image/accessory primitives. Web
retains the compatible body and unchanged image elements; loaded image state is
not reset when geometry is restored. Quote and summary updates keep the latest
row model for subsequent action/selection dispatch. Stage 4 introduces no public
API or Nitro schema beyond the style surface described above. Stage 5 below also replaces the native WalletGroup composite tree. Stage 6 below removes the
obsolete native classes and maps.

Stage 4 validation: package typecheck and all 161 tests in five suites pass;
focused lint has zero errors and two existing shadowing warnings. iOS Debug
build/link and Android Debug APK plus seven unit tests pass. Both dedicated
native simulators and headed Chrome exercised the acceptance cases above.
The section fixture additionally verified imperative scroll pinning and active
index updates after the container fixes. Stages 5 and 6 acceptance follow below. This is not a performance signoff.

### Stage 5 ownership and acceptance

WalletGroup owns only member composition, measurement and compact presentation.
Each member uses the registered Identity renderer and retains its own key, style,
selection, action origin and binding lifetime. Group styles never cascade into
members. Rebinding reconciles by member key; removed members are recycled
immediately, so retention is bounded by the current member count (plus one
compact parent on native). Recycling releases every member and compact parent.

Acceptance covers member press/accessory routing, disabled and pressDisabled
members, selection-only changes, style set/change/clear, member reorder/removal,
family replacement and reuse. The compact drag allocation is 68 on iOS and Web;
Android keeps its legacy `max(68, parent member height + 2 × group vertical
inset)` (for example 94 for a badged parent with an explicit height).
`+N` counts children whose draggable is not false. Only `draggable: false`
prevents a member from initiating group drag; a disabled member can still start
it (legacy behavior on all three platforms). Drop/cancel restore the
resolved group height and all configured member/container styles. Reorder
orchestration stays in the list container. Existing platform defaults stay
unchanged. Verify on the dedicated external-drive simulators and headed Web.

Stage 5 runtime verification: iOS and Android member press/menu actions carry
the member key and current action origin, including after content updates.
Both platforms verified selected/disabled members, style set/clear, member
removal/reordering, same-key family replacement, empty/repopulation, scrolling
and the shared fixed footer. Real member/parent drags move the group atomically;
excluded members do not start dragging. Default 274-point and styled 340-point
fixture heights restore after drop. iOS also verified a no-op long press;
Android verified touch cancellation; Web verified Escape cancellation.

All 163 package tests and typecheck pass; focused lint has zero errors (two
existing warnings). iOS Debug build/link, Android Debug APK and seven Android
unit tests pass. Headed Chrome includes a 390px viewport and in-drag captures.
Native simulator captures, action payloads and iOS recordings are stored with
the stage 5 harness in the external validation runtime. These are standalone
NativeList checks, not a consumer-app release or scrolling-performance signoff.

### Stage 6 cleanup and acceptance

The native monoliths and global style-slot maps are deleted. Their still-used
fonts, icons, layout helpers, action origins and controls remain shared primitives.
All twelve registry families are explicit. JavaScript validation rejects an
unknown row type before serialization. If one still reaches native code, iOS
keeps its legacy behavior: an empty 56-point placeholder row with shared chrome
and a single log message. Android rejects the snapshot while parsing it. Footer initialization uses a registered Action host.
Web has no legacy pool or warning-specific container measurement path; template
renderers own selector typography and WalletGroup compact-preview lookup.

The consumer audit in MIGRATION_BASELINE.md allows removal of history-key header
inference, Android token-section typography inference, and separator-key exceptions.
Callers choose `variant`, `style` and `separator` explicitly. Market pagination-tail
recognition and the existing Web DataRow `asset` badge column are deliberately
retained under the separately documented migration boundaries.

Validation covers same-key cycling through twelve templates, style set/clear,
ordinary/history-named/explicit-history headers, explicit 30-unit token headers,
Action/System footer replacement and callbacks, scrolling/reuse and WalletGroup
member actions and compact dragging. Source/type checks and runtime evidence
are recorded separately in DESIGN.md. This completes the architecture migration;
it does not add automatic overflow fitting or claim a performance benchmark.

### Web legacy-default audit (2026-09-24)

The renderer structure is retained; these legacy Web defaults are restored
inside it. MetricCard/System default restoration keeps image loading visibility
(a loaded image stays visible after content or style rebinding). WalletGroup
measurement adds the group's `style.verticalPadding` to the legacy member sum;
walletSidebar members keep their legacy 68/92 presets, which never applied the
`size` adjustment. The shared container pass records and clears its own inline
writes, so alignment set on template children does not leak into a reused,
unstyled row. DataRow measurement is layout-aware again (table rows without
secondary text are 48) and linear columns keep the legacy structure: secondary
leading text inline before the primary text, secondary text directly below.
Selector geometry follows either explicit height field. A disabled WalletGroup
member can start the group drag and emit its own actions again, and a
non-Market System retry is message-only with a row-press action, as in the
legacy engine. Text styling no longer
reads computed style, so attached and detached rows style identically.
Validation: package typecheck and 173 tests pass (jsdom/unit level); this is
not new rendered browser or native device evidence.

### Platform interaction and appearance differences (legacy, retained)

These are pre-existing platform behaviors recorded after the 2026-09-24 native
audit; they are not new in this change.

- **Disabled rows and actions.** iOS blocks every action on a disabled row
  (cell interaction off plus an action-emission guard). Android blocks text
  accessories (radio, switch, `⋮` menu), checkboxes, Activity footer buttons and
  System Retry, but still emits trailing/leading icon actions, Market badges,
  media close, title help and WalletGroup member actions; a disabled group does
  not block its members' actions on Android. Web (legacy) blocks row press and
  actions on a disabled row or disabled WalletGroup, but not on a disabled
  member inside an enabled group.
- **System retry.** Android and iOS show a literal "Retry" button on non-Market
  retry rows and read `actionText` only for the Market retry. Web keeps its
  legacy rendering: a non-Market retry shows only its message and the whole row
  press emits `actionKey`; only the Market retry draws a button
  (`actionText ?? 'Retry'`). `style.actionText` therefore has no target on a
  non-Market Web retry row; it is accepted by validation and ignored there.
- **Pressed corners.** Native pressed feedback uses 12 on all corners (Rail 8,
  MediaTile 16, wallet sidebar 20) regardless of `groupPosition`. Web changes
  only the pressed background and keeps the resting (group or template) radius.
- **Text direction.** `start`/`end` text alignment follows the layout direction
  on all platforms.
- **Separator inset.** Identity rows default to a 60-unit inset and other
  templates to 12 on native; Web defaults to 0 (STYLE_SPEC §6.2).
- **DataRow linear layout.** See ROW_TEMPLATES.md: native unstyled linear rows
  keep the legacy single-label column.
