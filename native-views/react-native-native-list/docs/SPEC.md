# NativeList contract

This is the entry point for the package's behavioral contract. The linked
documents are part of this spec; implementation status is separate from target
architecture and runtime evidence.

## Purpose and non-goals

NativeList renders bounded data with fixed row templates on iOS, Android and
Web. Data rows do not render arbitrary React children. Native-only named container slots
may render React chrome outside the data-row stream. Integrators own overflow
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

### Native section title loading indicator

Status: implemented source; simulator rendering acceptance pending.
On iOS and Android, `sectionHeader.titleLoading` defaults to `false`. When
`true`, a 20-point native indeterminate indicator precedes the section heading
with an 8-point gap and inherits the resolved title color. It emits no action
and does not create a separate accessibility target. Clearing the field, full
rebind, recycling and disposal stop and hide the indicator; detached native views do
not keep a visible animation. The History template's minimum content height
increases from 16 to 20 points while loading. Explicit caller row/container
heights still take precedence and must fit their padding. Web does not render
this new native-only indicator; its existing section presentation is unchanged.

Acceptance requires loading → idle → loading reuse, title color updates and
detachment on each native platform. A title's localized confirming text remains
caller-owned; the renderer does not infer transaction status.

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
renderers own selector typography, System warning DOM measurement and
WalletGroup compact-preview lookup.

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
measurement keeps the legacy member sum, 12-unit gaps and the 1px border pair
(budgeted only when the parent member carries `height`, like native's 1-unit
inset); `style.verticalPadding` replaces that inset instead of adding to it, and
the rendered padding matches the measurement (the border supplies the first
unit). walletSidebar members keep their legacy 68/92 presets, which never
applied the `size` adjustment. The shared container pass records its own inline
writes and the engine (and WalletGroup, per member) undoes them before the
renderer rebinds, so alignment set on template children does not leak into a
reused, unstyled row and a renderer value equal to the previous container value
is never reverted. DataRow measurement is layout-aware again (table rows
without secondary text are 48) and linear columns keep the legacy structure:
secondary leading text inline before the primary text, secondary text directly
below. Selector geometry follows either explicit height field. A disabled
WalletGroup member can start the group drag and emit its own actions again
while its member press stays blocked; title-help hover checks only the resolved
member. Every System retry (Market or not) is message-only with a row-press
action, as in the legacy engine. Text styling no longer reads computed style, so
attached and detached rows style identically.

Review round 3 (2026-09-24). Web: a Market subscript run keeps the legacy
`ceil(0.6 × fontSize)` size on a `fontSize` line box when the field has an
explicit `fontSize`, and iOS/Android again draw it at `ceil(0.6 × fontSize)` on the
field's own line height (legacy on each platform);
an automatic-height System warning is laid out at its rendered border-box
height again; System and MetricCard rebuild their text when a text style
changes, so clearing `lines`/`offsetY` restores the original text and removes
the layout wrapper; a reused visual restores `margin-bottom`, so a cleared
walletSidebar `leadingGap` leaves no residue. iOS and Android gate every
selector explicit-height geometry on `style.container.height ?? row.height`
(Android's list-wide source scale stays keyed on `row.height` only); Android
rebinds the fixed footer on a `listStyle`-only snapshot.
Validation: package typecheck and 180 tests pass (jsdom/unit level); this is
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
  click actions on a disabled row or disabled WalletGroup. A disabled member
  inside an enabled group still emits its own actions, but its member press is
  blocked.
- **Web hover actions.** Web only: a title marked `titleActionOnHover` or an icon
  accessory with `hoverActionKey` emits that action with an anchor on
  `pointerover` (not on moves inside the element) and invalidates the anchor
  with reason `pointerLeave` on `pointerout`. The check is the resolved source's
  `disabled` only: a WalletGroup member's own flag, never the group's, so a
  member of a disabled group still emits its hover action (legacy). Native has
  no hover; the same keys fire on tap.
- **System retry.** Android and iOS show a literal "Retry" button on non-Market
  retry rows and read `actionText` only for the Market retry. Web keeps its
  legacy rendering for every presentation: a retry row shows only its message
  and the row press (click, Enter or Space) emits `actionKey` with a `row`
  anchor; no retry button is drawn. `actionText` and `style.actionText` are
  accepted by validation and have no Web target (native only).
- **Pressed corners.** Native pressed feedback ignores `groupPosition` and uses
  12 on all corners; templates with their own resting radius keep it (Rail 8,
  MediaTile 16). iOS walletSidebar identity rows use 20; Android has no
  wallet-sidebar exception (12). Web changes only the pressed background and
  keeps the resting (group or template) radius.
- **WalletGroup row appearance.** The group's row-level `backgroundColor` is
  ignored on iOS and Android (legacy), in both the resting and the
  `backgroundFullWidth` fill: the group fill is
  `style.container.backgroundColor` or the theme `subduedBackground`. Web
  (legacy) applies it inline over the group fill. Use
  `style.container.backgroundColor` for a cross-platform group fill.
- **WalletGroup height.** iOS and Android (legacy) ignore the group's
  `row.height`: `style.container.height` wins, otherwise the height comes from
  the members. Web (legacy) honors `row.height` for a WalletGroup like any other
  row.
- **Text direction.** `start`/`end` text alignment follows the layout direction
  on all platforms. On iOS a runtime layout-direction change re-resolves it:
  visible rows and the fixed footer rebind immediately, offscreen rows on reuse.
- **Separator inset.** Identity rows default to a 60-unit inset and other
  templates to 12 on native; Web defaults to 0 (STYLE_SPEC §6.2).
- **DataRow linear layout.** See ROW_TEMPLATES.md: native unstyled linear rows
  keep the legacy single-label column.

## Native scroll-position thresholds and tablet grids

Status: Source implemented; native runtime acceptance pending. These additions are native
only. Existing Web rendering and caller behavior are unchanged.

- `scrollPositionThresholds?: { start: number; end: number; enabled?: boolean }`
  observes the list's logical distance from its content start, in logical pixels.
  `start` and `end` must be finite, nonnegative, and `start < end`. Omitted or
  `enabled: false` disables observation.
- `onScrollPositionThresholdChange({ isBeyondThreshold })` reports initial state
  after enabling/configuration changes, then only hysteresis transitions: enter
  when distance >= end, leave when distance <= start. No per-frame JS events.
  Replacing the JS callback takes effect on the next event; disabling emits no event.
  Configuration updates reset hysteresis before recomputing current position.
- iOS uses contentOffset + contentInset, excluding overscroll below zero. Android
  accumulates consumed child scroll distance and rebases from first-item geometry.
  Parent-pager header contributions and arbitrary index-jump acceptance are tracked
  separately; no estimate-based parity claim is made for unverified jumps.
- `layout.gridColumns` accepts integers 2 through 7 on native. Native parsers
  clamp malformed external JSON to 1 through 7 as before; public JS validation
  rejects invalid requested counts. Grid geometry remains container-owned.

Required acceptance: disabled/enable lifecycle, both equality boundaries,
oscillation inside the band, programmatic scroll, page/identity replacement,
refresh and header insets, grid 6/7 layout/rotation, and no stale callbacks after
disposal. Unit checks prove transition semantics, not rendered behavior.

## Native media preview probing

Status: Source implemented; native runtime acceptance pending. `mediaTile.media`
is an opt-in native-only preview descriptor: `{ source: ImageSource, probeOrder:
('image' | 'video')[] }`. The order contains one or two distinct candidates.
The caller supplies the order; the renderer never guesses asset semantics.
When provided, media takes precedence over `image`, unless `imageState` is
explicitly empty/error. Web keeps its existing `image` behavior; native callers
that also target Web must supply that existing image independently.

Image uses the shared OneKeyImage lifecycle. Video uses a paused, muted AVPlayer
on iOS and Media3 ExoPlayer on Android, preserving the native Video source
families including streaming media instead of approximating them with a frame
extractor. Video defaults to contain; an explicit image fit may select cover.
There are no controls or video touch targets; the row owns press interaction.
Android preserves a black letterbox and iOS a transparent one, matching the
existing native Video views. No code calls play or requests audio focus.

A failed candidate advances once; exhaustion displays the existing error
placeholder. New source/row identity resets probing; binding, candidate and
player generations reject stale callbacks, including A→B→A. Text-only updates
preserve the current request. A tile owns at most one native player. On iOS,
eligibility requires intersection with the window and every clipping/scroll
ancestor viewport; native scroll, bounds, layer transform/position and visibility
observations release retained offscreen pager pages. Detach, background,
invisibility and recycle release the player; reattachment rebuilds its retained
source while remaining paused. Android observes native global scroll/layout and
pre-draw geometry (including property animations), checking ancestor alpha and
getGlobalVisibleRect before allocating a player. These observers do not schedule
frames or emit JavaScript events.
Player count is bounded by intersecting native video tiles, including partially
visible tiles; there is no numeric cap that blanks visible iPad cells. iOS requests
one second of forward buffer; AVFoundation treats this as an advisory preference,
not a hard byte limit. Android uses a 250–1000 ms forward buffer, zero back buffer
and a 2 MiB target allocator; Media3 may exceed a target by allocation granularity.
These are per-player buffer targets, not hard total-process memory limits.
Platform decoder/surface overhead is additional. No
process-wide player cache or NativeList media disk cache is introduced. Source headers are never
logged. Android uses Media3 1.4.1 (matching native Video's default), including
HLS/DASH, UI and OkHttp data source; the existing root media3Version override is
honored. Cookie handling uses the React Native cookie store without replacing
the global HTTP client's cookie jar.

Required runtime cases: image→video, video→image, exhaustion, file/HTTP/HLS,
non-square fit, silent paused initial frame, same-source text update, quick
A→B→A reuse, detach/background and return, row tap, grid resize, and absence of
leaked audio/player resources. Static API checks do not prove these cases.

## Native container slots (source implementation, runtime verification pending)

`listHeader`, `listEmpty` and `listFooter` accept React nodes on iOS and Android
vertical lists only. They are optional and default to absent. They are common
container capabilities, not row templates, and MUST NOT be used for per-item
rendering. Horizontal orientation with any supplied slot is rejected. Web ignores
these new native-only props; existing Web lists and callers retain their behavior.

The order is header, data rows (or empty slot when there are no data rows), footer.
Slots scroll with the native list. `fixedFooter` remains a separate fixed native
row descriptor. `scrollToEnd` aligns the final scrolling slot bottom with the
viewport end, including footer/empty/header-only slots taller than the viewport. Callers using `listEmpty` MUST omit `snapshot.emptyState`, whose
existing native descriptor becomes a data item. Horizontal content padding applies
to slots as well as data rows. Slot React trees own their internal layout and
business actions; NativeList owns their scroll placement and lifecycle.

The wrapper mounts exactly three non-collapsible slot roots and reports measured
heights through `containerSlotHeightsJson` in header/empty/footer order. Native
validates three finite, non-negative heights. Slot updates MUST NOT alter data-row
keys or public row indices. No per-scroll-frame JS position updates are used.
iOS mounts slot roots inside UICollectionView and reserves native layout space.
Android uses ConcatAdapter with three single-item slot adapters around the existing
data adapter; adapter binding indices stay local while layout-manager positions
are explicitly translated. Slots span all grid columns, do not select/reorder,
and do not emit row actions, visible-row indices, or image-prefetch work.

Fabric mount/unmount ownership remains explicit even when RecyclerView detaches a
slot holder. The native manager retains at most three roots until Fabric unmount;
recycling detaches slot content without disposing its React tree. Unmount removes
that exact view. Native list disposal MUST NOT dispatch pending row/slot events.
The generated Nitro 0.37 leaf managers require deterministic child-forwarding
customization: `scripts/enable-container-slots.mjs` runs after nitrogen and rejects
unexpected generator output. Generated files are never maintained by hand.

Acceptance requires both platforms: dynamic header height, empty/nonempty changes,
scrolling footer with pagination, grid full-span slots, scrolling from interactive
slot content, refresh from header/empty, row-index scroll APIs, reuse/unmount, and
rapid account changes. Source/type checks do not establish this runtime acceptance.


## Refresh trigger distance

Optional `snapshot.capabilities.refreshTriggerDistance` is a positive finite
logical-pixel distance. Omission preserves the platform control's default.
iOS adds a release-trigger fallback based on the deepest normalized overscroll
(contentOffset plus contentInset.top); the fallback and UIRefreshControl emit
at most one refresh action per drag. Android passes the distance to
SwipeRefreshLayout's native trigger setting. These native controls differ in
rubber-band/drag scaling, so equal values do not promise equal finger travel.
The caller continues to own refreshing state, completion, and optional haptic feedback.
The container does not emit a second haptic for the release fallback. The fallback is
inactive unless pull-to-refresh is enabled; no product-specific threshold is
hardcoded into the module.

## Native rich activity presentation

Status: Source implemented; native runtime acceptance pending. Optional `amounts`
supersedes legacy `primaryAmount`/`secondaryAmount` only on native. It accepts at
most 32 keyed lines, each with formatted text, optional leading visual, tone,
secondary text and subscript text segments. Segments are caller-formatted and
never interpreted as numbers by NativeList. Omission preserves the legacy tree.

`presentation: stacked` places the amount lines after the identity column and
right-aligns them; `table` uses equal identity/amount columns, left-aligns amount
lines and adds an optional trailing fee column. Fee supports label, primary and
secondary text with their own segments; `hidden` preserves its column width
while hiding its contents. Up to four uniquely keyed title badges use declared
tones. `descriptionActionKey` makes only the description emit that action with
source `description`; footer actions retain their existing source/keys and
disabled behavior. All actions use the current host binding epoch.

Rich rows measure the declared line/visual metrics; there is no six-line native
truncation. The existing height override contract still applies. RTL follows
semantic leading/trailing anchors. Clearing rich fields returns to the legacy
renderer, and removing amount lines recycles their visual slots. Web rendering
remains unchanged; rich fields are native-only until a separate Web migration.

Acceptance: 1/2/6/32 lines, changed keys/order, paired icons and network overlays,
small-number segments in amounts/fiat/fee (ceil(0.6 × parent font size), unchanged
baseline, matching NumberSizeableText), badge tone/clear, hidden fee, disabled
actions, description anchors, dark/RTL/font scaling and explicit/automatic height.


### Android ancestor scroll distance

Threshold hysteresis combines the primary RecyclerView's actual consumed scroll
with all native ancestor header contributions. CollapsiblePagerView publishes
weak-owner contributions through the namespaced View-tag protocol documented in
its SPEC.md; no pager dependency or product semantics are required. Absence is
zero. NativeList owns one native listener, registers it on attach, and removes
only its own listener on detach. Header-only scrolling therefore crosses the
same logical Home threshold as iOS normalized content scrolling, without a JS
scroll-frame event. Changes to the listener or public callbacks do not replace
native resource ownership.

Child distance uses actual onScrolled dy and rebases from first-item geometry at
top; scrollToOffset seeds a known explicit offset. It does not use RecyclerView's
average-height scrollbar estimate. Continuous scrolling and return-to-top are
covered by this policy; exact classification immediately after arbitrary distant
scrollToIndex jumps remains pending measured-prefix correction and MUST NOT be
claimed as verified. A later top anchor restores exact distance.


### Container follow-up verification

The pure Android end-alignment policy has four focused JVM tests: tall footer,
tall empty slot with viewport padding, animated decorated-bottom correction,
and short footer. These validate geometry, not actual RecyclerView animation or
Fabric lifecycle. Native simulator acceptance remains required. The iOS fallback
retains one refresh action per drag and leaves haptic feedback to its caller.
