# NativeList contract

This is the entry point for the package's behavioral contract. The linked
documents are part of this spec; implementation status is separate from target
architecture and runtime evidence.

## Purpose and non-goals

NativeList renders bounded data with fixed row templates on iOS, Android and
Web. Data rows do not render arbitrary React children. Named container slots
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

### iOS 26 fixed top scroll-edge overlay

Standalone `NativeList` accepts optional
`topScrollEdgeEffect={ScrollEdgeEffect.Hard}` or `ScrollEdgeEffect.Soft` and
`topOverlay` React content. Both are disabled by default. On iOS 26 or newer,
the fixed overlay is a fourth native container slot, outside the row stream and
outside the existing scrolling `listHeader`/`listEmpty`/`listFooter` slots.
Fabric measures its height; the list owns an equal top inset on its collection
view and binds UIKit's `UIScrollEdgeElementContainerInteraction` to that view,
using the requested top edge style. The list restores its original inset and effect
on removal, disposal and reuse. The UI thread owns all view and UIKit state;
there is no persistence, background-runtime state or per-frame JS callback.
An invisible, noninteractive native label fills the overlay host so UIKit
recognizes React-drawn content as a scroll-edge element.
Inside a CollapsiblePagerView, the pager owns this effect and inset, and child
lists do not opt in separately. iOS before 26, Android and Web retain their
existing layout and scrolling. Missing overlay or style changes nothing.
Acceptance requires actual iOS 26 initial layout, long/short/empty scrolling,
refresh and overlay replacement/removal, including dark mode.

`ScrollEdgeEffect` is an exported string enum (`Hard = 'hard'`, `Soft = 'soft'`);
the corresponding string literals remain valid prop values. Hard includes
UIKit's dividing line; Soft uses the gradual blurred transition. Nitro receives
an enum (`none | hard | soft`, default `none`) rather than a hard-effect boolean.
Changing Hard ↔ Soft preserves the overlay, inset, logical scroll offset and
saved original effect. Clearing the prop, removing the overlay, detaching or
disposing restores the original effect. This bridge change requires rebuilding
the matching native host. Soft selection is implemented in the wrapper and
native host. Focused JS tests cover enum forwarding and disabling; UIKit tests
cover style switching, overlay geometry and restoring the previous style.
Visual runtime acceptance remains separate from source, type and build checks.

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

### Android reorder cancellation

Status: implemented; three real Android View/callback regressions verified on
the task's API 34 emulator. Full MotionEvent long-press/edge-scroll gestures and
physical-device acceptance remain pending.
Replacing the reorder helper for a structural snapshot or changed font mapping
MUST invalidate pending drag indices and transaction state before detaching the old
helper, and restore the adapter's latest committed/submitted snapshot order
during cancellation. An active helper's selection and auto-scroll ownership must
end before detach, including disposal; ordinary non-reorder touch input is not
force-cancelled.
Cancellation emits no reorder event. It restores the active row's normal
appearance, including wallet-group expansion and its declared opacity, removes
the placeholder, and clears drag/displacement translation and temporary elevation.
Callbacks and deferred displacement/drop animations owned by the detached helper
MUST NOT change rows or commit a later gesture. A subsequent unmoved gesture
emits no reorder event; a subsequent valid move retains the normal single commit
with its own source/destination indices. This does not change the font API or
ordinary successful-drop animation contract.
In a repository source checkout, the opt-in platform-runner test and exact
build/run commands are in `native-tests/android/README.md`; these tests
drive the shipping NativeListView and ItemTouchHelper callbacks, not a copy of
the cancellation implementation.

### Native section title loading indicator

Status: implemented source; simulator rendering acceptance pending.
On iOS and Android, `sectionHeader.titleLoading` defaults to `false`. When
`true`, a 20-point native indeterminate indicator precedes the section heading
with an 8-point gap and inherits the resolved title color. It emits no action
and does not create a separate accessibility target. Clearing the field, full
rebind, recycling and disposal stop and hide the indicator; detached native views do
not keep a visible animation. The History template's minimum content height
increases from 16 to 20 points while loading. Explicit caller row/container
heights still take precedence and must fit their padding. Web History also
consumes titleLoading, using its existing section-slot geometry and the Web
History presentation described below.

Acceptance requires loading → idle → loading reuse, title color updates and
detachment on each native platform. A title's localized confirming text remains
caller-owned; the renderer does not infer transaction status.


### First-fill position and imperative command ownership

Status: implemented in source and verified in the consuming app's local native
builds; acceptance of the newly published package remains pending.

On Android, empty-to-populated data MUST preserve the actual body-start anchor
through the first committed layout. This applies only when the old body was
empty and the view was actually at its start; it MUST NOT reset a non-empty
refresh, pagination, deep reading position, or a newer accepted command.
Continuous snapshots retain this intent until the first populated layout.
A real drag or valid command invalidates the intent. Container-slot positions
and scrollbar estimates MUST NOT be mistaken for data-row reading anchors.

An accepted imperative command owns its request, execution, pending-layout and
posted-alignment work. Invalid commands MUST NOT cancel a valid request.
On iOS, known-configuration validation MUST precede changing accepted, pending
or animated-command state; invalid input MUST preserve the existing terminal
notification, while configuration-unavailable requests retain deferred validation.
Completion on Android requires the current request to have finished dispatch,
its own pending layout/alignment barriers to have completed, and native scrolling
to be idle without an active layout-manager animation. A true animated top
no-op completes without starting redundant native motion. Non-animated key and
end alignment complete after their requested layout/alignment, not beforehand.
An unrelated layout request MUST NOT indefinitely postpone command completion.
Replacement, actual drag, detach and disposal invalidate old callbacks; a stale
callback MUST NOT move the view or clear a newer command's barriers.

A valid command can privately notify the owning Pager before execution, including
a same-position no-op; this does not add a public NativeList API. A Pager-owned
page-top or sticky-top command can privately cancel only its owned list's old
pending command and native animation before acquiring its own target. On iOS
the notification identifies the collection view; on Android the listener belongs
to the actual owned RecyclerView. No notification may take over another owner.

On Web, the owned viewport's private cancellation event clears pending list
commands and stops old browser smooth motion. A newly accepted valid list
command invalidates a queued Pager target; invalid commands do not. Destroy
removes the exact listeners and clears pending work. Other list viewports and
rendering work remain independent.

Conformance: NativeListView.kt, RNCNativeListView.swift, NativeListWebEngine.ts,
and the related Pager owner coordination. Runtime acceptance must exercise
first populated layout, command replacement, animated/non-animated completion,
true top no-op, and short pending content followed by usable geometry on the
actual installed package; source or helper-only checks are not runtime evidence.

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

### Font resource ownership and runtime configuration

Status: implemented; JavaScript regression tests, Android compilation/JVM tests
and iOS test-target compilation verified. Device rendering acceptance pending.
NativeList distributes no font files and registers no fonts. Hosts own font
licensing, provisioning and registration. `configureNativeListFonts(families)`
sets four optional face names (`regular`, `medium`, `semibold`, `bold`) for the
current JavaScript runtime. The initial mapping is empty; `{}` resets every face
to system fallback. Each call replaces the entire mapping. Names are trimmed,
nonempty strings of at most 128 UTF-16 code units without control characters; unknown
keys or invalid values throw before changing any state. Omitted/undefined faces
use system fallback. Identical normalized mappings are idempotent.

Hosts configure once during UI-runtime startup, after provisioning fonts and
before rendering lists. Independent UI/background JavaScript runtimes have
independent stores; configuring one MUST NOT change another. There is no public
instance override and no mutable native-global font mapping. An internal Nitro
`fontFamiliesJson` prop delivers each list's current mapping to its own resolver.
Mounted lists subscribe to their runtime store. A changed mapping rebinds text,
invalidates measured geometry and requests layout without replacing row keys,
selection or snapshots. An active reorder is cancelled through the existing
structural-update cleanup before changed font geometry is applied. The same mapping MUST NOT invalidate layout. Changing
which font bytes an already configured name resolves to is outside this API;
hosts load/register fonts before configuration, and no late-font-loading event or
automatic font-file registration is added.

iOS resolves each configured PostScript face with `UIFont(name:size:)`, then
falls back to the requested system weight. Tabular-number features remain enabled
where requested. Android delegates configured family names to ReactFontManager
with `Typeface.NORMAL` and host assets, including Expo's `setTypeface` path;
missing configured families use React Native's system fallback, whose weight
need not match the requested semantic weight. An omitted face
uses the requested system weight on Android API 28+, with the nearest
regular/medium/bold system face on API 24–27. Web treats each configured name as a literal
CSS font-family name with the existing semantic weight and platform font stack;
the same resolver covers row text, sticky headers, fixed footers, section indexes
and reorder previews. Native measurement and binding MUST use the same instance
mapping. `fontFamily` remains absent from the public row-style surface.

System/custom fallback changes text metrics. Consumers must check fixed-height
rows, truncation and numeric alignment on each platform; source/unit checks do
not establish visual acceptance. Acceptance includes two independent runtimes,
two list instances, changed/reset mappings, invalid configuration without partial
state, all templates and container text, and retained selection/scroll behavior.

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
  accumulates consumed child scroll distance and, after every layout pass or
  zero-delta scroll, re-derives it from the measured extents above the first laid-out
  item (see Android ancestor scroll distance). Parent-pager header contributions are
  tracked separately.
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
explicitly empty/error. Web gallery media probing follows the separate Web migration contract below;
legacy Web image-only tiles retain their existing behavior.

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
preserve the current request. On Android, terminal image/player completion is
posted to the next UI queue turn before advancing probes or notifying the row;
it MUST NOT clear or rebind a Glide request inside its terminal listener.
Posted work rechecks binding/candidate generations, and player callbacks also
recheck the player generation. Recycling removes queued completion work before
releasing resources. This repairs the image-to-video fallback crash without
changing probe order or the terminal result contract; native runtime regression
acceptance is still required. A tile owns at most one native player. On iOS,
eligibility requires intersection with the window and every clipping/scroll
ancestor viewport; native scroll, bounds, layer transform/position and visibility
observations release retained offscreen pager pages. A player lives only until
its first rendered frame is copied (Android TextureView bitmap, iOS
AVPlayerItemVideoOutput pixel buffer); the tile then shows that still frame with the
same fit and letterbox and releases the player and its viewport observers. If no frame
can be copied after a few frames, the paused player stays attached. Detach,
background, invisibility and recycle release a player still awaiting its first frame;
reattachment rebuilds its retained source while remaining paused. The still frame is
kept across detach and same-identity rebinding and dropped on recycle or identity
change. Android observes native global scroll/layout and
pre-draw geometry (including property animations), checking ancestor alpha and
getGlobalVisibleRect before allocating a player. These observers do not schedule
frames or emit JavaScript events.
Live player count is bounded by intersecting native video tiles that have not yet
captured a frame, including partially visible tiles; there is no numeric cap that
blanks visible iPad cells. Per-draw (Android) and ancestor-layer KVO (iOS) viewport
observation exists only while a tile awaits its first frame. iOS requests
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
rendering. Horizontal orientation with any supplied slot is rejected. Web container slots follow the separate Web parent-scrolling contract below;
the native placement and lifecycle described in this section remain unchanged.

The order is header, data rows (or empty slot when there are no data rows), footer.
On iOS, after the laid-out vertical content shrinks, an offset beyond the new
bottom is corrected to the current adjusted-inset-aware maximum. In-range offsets
and negative pull overscroll remain unchanged. Correction waits until active
dragging, deceleration and refresh finish; it is not an unconditional layout or
snapshot reset to the top. The native content-offset change remains observable to
ancestor pager coordinators so their saved logical offsets stay synchronized.
Recycling/disposal cancels deferred correction. Runtime acceptance includes a
scrolled retained-page round trip followed by empty content with scrolling slots.

Slots scroll with the native list. `fixedFooter` remains a separate fixed native
row descriptor. `scrollToEnd` aligns the final scrolling slot bottom with the
viewport end, including footer/empty/header-only slots taller than the viewport. Callers using `listEmpty` MUST omit `snapshot.emptyState`, whose
existing native descriptor becomes a data item. Horizontal content padding applies
to slots as well as data rows: every platform places a slot root exactly
`contentPaddingHorizontal ?? contentPadding` logical units from each edge, the same
width React lays it out at. Android's narrow-screen row scale and section-index gutter
apply to data rows only. Slot React trees own their internal layout and
business actions; NativeList owns their scroll placement and lifecycle.

The wrapper mounts exactly three non-collapsible slot roots and reports measured
heights through `containerSlotHeightsJson` in header/empty/footer order. Native
validates three finite, non-negative heights. Slot updates MUST NOT alter data-row
keys or public row indices. No per-scroll-frame JS position updates are used.
iOS mounts each slot root inside a list-owned host view in UICollectionView and
reserves native layout space. The host owns the slot's vertical position and clips to
the reported height, so React frame updates cannot move a slot out of place and a slot
whose height has not been reported yet (or has just grown) never draws over rows.
Android uses ConcatAdapter with three single-item slot adapters around the existing
data adapter; adapter binding indices stay local while layout-manager positions
are explicitly translated. Slots span all grid columns, do not select/reorder,
and do not emit row actions, visible-row indices, or image-prefetch work.

On Android, mounting/unmounting a slot or changing its actual rounded native
height MUST request one coalesced RecyclerView measurement/layout repair. The
repair runs after the current layout completes and only with a positive viewport
size. A zero-sized or currently computing viewport retains the pending repair
for the next layout/attachment opportunity rather than posting an unbounded
retry loop. Disposal cancels the pending work. Equal height reports MUST NOT
request another repair. This leaves Fabric-owned slot content and row indices
unchanged; source checks alone do not establish first-layout visibility.

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
On native, the row-less `nativeList.refresh` bridge envelope is container-owned:
the JS wrapper consumes it and invokes only `onRefresh`, when supplied. It MUST
NOT also invoke the public `onRowAction`, even when `onRefresh` is absent.
An actual row action carrying a `rowKey` remains a row action, including one
whose caller-defined action key matches this internal name. Web retains its
legacy dual notification behavior; this native correction does not change Web.
The container does not emit a second haptic for the release fallback. The fallback is
inactive unless pull-to-refresh is enabled; no product-specific threshold is
hardcoded into the module.

## Android refresh foreground geometry

Status: implemented; NFT/History Home placement verified on Android on 2026-09-28.
The refresh container publishes its actual coordinator-added indicator inset in
physical pixels under the native View tag
`onekey_native_scroll_coordinator_refresh_inset`. Zero means no added inset;
the value MUST match the offset actually applied by the existing controller,
including all ancestor padding contributions. A coordinating outer Pager can
subtract this metadata once when drawing the native indicator above shared
headers. This metadata does not change refresh trigger distance, callback,
refreshing state, ordinary-list placement or row templates. It introduces no
JavaScript API or dependency on Pager classes. The value follows changes to the
applied inset and remains valid while that inset is retained across detach.

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
truncation. iOS automatic height mirrors the rendered stacks: `lineGap` between the
visible title/description/status lines, the description measured at the identity
column's actual width (stacked or table, including the fee column), fee height and the
footer-action button height. Android sizes the row to its content. The existing height override contract still applies. RTL follows
semantic leading/trailing anchors. Clearing rich fields returns to the legacy
renderer, and removing amount lines recycles their visual slots. Web rich presentation follows the separate Web migration contract below;
legacy Activity rows without rich fields retain their existing behavior.

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

Child distance uses actual onScrolled dy while scrolling continuously. Programmatic
jumps (scrollToIndex/Key/End/Offset), anchor restores and inserts or height changes
above the viewport report no dy, so every layout pass and zero-delta scroll re-derives
the distance as the extent of all grid lines before the first laid-out item plus its
offset from the padded top. Extents are those actually laid out (recorded per row key,
including rows a jump lays out and leaves offscreen, pruned to the current rows); a row
that was never laid out uses its explicit height, else the mean laid-out row extent.
Slots use their reported heights. The result is exact once every item above the
viewport has been laid out and an estimate only across never-laid-out rows; it does not
use RecyclerView's average-height scrollbar estimate. Runtime acceptance for distant
jumps remains pending.


### Container follow-up verification

The pure Android end-alignment policy has four focused JVM tests: tall footer,
tall empty slot with viewport padding, animated decorated-bottom correction,
and short footer. These validate geometry, not actual RecyclerView animation or
Fabric lifecycle. Native simulator acceptance remains required. The iOS fallback
retains one refresh action per drag and leaves haptic feedback to its caller.


## Explicit scroll alignment (OK-64041)

Status: implemented on Web, Android and iOS. This change ports the 3.0.154
maintenance fix to the current main protocol and layout. The maintenance
application recordings below do not establish runtime acceptance of this port.

Keys identify outer rows; a wallet group member is not a separate scroll key.
Explicit `viewPosition`/start/center/end alignment MUST use the signed difference
`viewportLength - itemLength`, then apply `viewOffset` and the existing content
bounds. This applies to both axes. Positive `viewOffset` leaves space before the
aligned target. An oversized row cannot be fully visible; center aligns its
center and end aligns its trailing edge. `nearest` MUST retain its existing
oversized-row behavior, defaults and request normalization.

Android MUST pass the alignment mode through visible, deferred and smooth
scrolling. For an unmounted explicit nonanimated target, the provisional leading
edge stays inside the viewport until the existing deferred pass measures the
real item. Existing container-slot row-position translation remains in force;
this change adds no retries or bridge API. iOS uses UICollectionView layout
attributes for mounted and unmounted targets and keeps its adjusted-inset-aware
content clamp. Web uses the TypeScript calculation. Imperative requests run once
at the current geometry; consumers own repositioning after layout changes and
cancellation when the user takes control.

Focused TypeScript, Kotlin/JUnit and SwiftPM/XCTest regressions exercise the
shipping calculations: oversized, short and equal rows, signed offsets, nearest,
content bounds and Android provisional boundaries. SwiftPM runs the Foundation
helper on macOS; it does not instantiate UIKit. Geometry tests do not prove
RecyclerView mounting, deferred callback timing or consumer cancellation.

Historical maintenance-base acceptance recorded in PR #138 includes full-app
Electron/Web 36/36 cases each, Android 27/27 visible-target cases and iOS 15/15
primary cases. Parent/middle/last targets changed from 0/3 visible to 3/3 on both
native platforms. Those results use NativeList 3.0.154 plus the patch. Current
main combined-package runtime acceptance and native horizontal runtime remain
pending; pixel-perfect wallet-member centering is not claimed.

Port verification (2026-10-08): all 236 tests in seven NativeList suites,
package typecheck, focused TypeScript lint and repository test-integrity pass.
The consumer Gradle project compiles the actual NativeListView and passes all
seven Kotlin alignment tests; SwiftPM compiles the shipping helper and passes
all seven geometry tests. These are local unit/build checks, not a new
combined-package application or physical-device acceptance run.


## Web parent scrolling and React container slots

Status: implemented. Focused DOM/wrapper tests and controlled consuming
Home/Market Web and Electron checks cover parent ownership, slots, host
replacement, hidden tabs, and the migrated touch/reorder interactions. These
checks do not establish native runtime or complete performance acceptance.

`webScrollHost` is Web-only: omitted preserves the existing owned viewport;
`null` defers initialization until the parent host is ready; an element opts
into that explicit parent as the sole vertical scroll owner. `webStickyInset`
is a non-negative finite CSS-pixel inset, default zero. Native ignores both.
External horizontal list orientation is rejected. The host is never inferred
from class names. Its replacement disposes the old engine and exact observers,
listeners, pending commands and anchors before the next host is attached.

On Web, listHeader/listEmpty/listFooter are React-owned siblings around the
engine-owned row host. They remain normal scrolling content, never row models;
empty replaces the body only when there are no rows. listEmpty takes precedence
over snapshot.emptyState. Grid slots span the list's complete width and reserve
their actual intrinsic height. The wrapper owns an internal scroll viewport when
slots are used without an external parent; the slot-free internal path retains
its existing structure. No ReactDOM dependency or arbitrary row children exist.

The engine reads list-local width, row-host origin, host viewport intersection,
sticky inset and complete wrapper bounds together. Row-index/key scrolling is
relative to the data body; scrollToOffset starts at the complete container,
including its header, and scrollToEnd includes the final footer. End reached
is based on visible data-row indices, before footer content, as existing lists.
Hidden/zero-sized or fully offscreen lists emit no end-reached event. Resize,
slot and ancestor layout changes remeasure origin. Increasing content before a
visible row preserves that row's screen position while the parent is scrolled.
No React state updates run on scroll frames. External refresh gestures remain
owned by the parent control; only internal mode handles its own pull gesture.

The explicit external host captures scroll events from itself and descendant
scroll containers that are ancestors of the list root. Horizontal pager movement
therefore invalidates cached viewport intersection and restores visible rows on
the next scheduled frame. Scrolls within unrelated slots or list descendants do
not invalidate this list. Destroy removes the same capture listener; no second
vertical owner or scroll-position correction is introduced.

Required acceptance: unchanged slot-free internal behavior; parent content on
both sides and sticky inset; dynamic header/footer; empty to populated; grid
last row; all imperative alignments; null/ready/replaced host; hidden/visible;
unmount cleanup, mouse/touch scroll and drag autoscroll. Type/helper results do
not establish rendered product, native or performance acceptance.


## Web Market table presentation

Status: implemented on the 6.7.0-1 source baseline. Package unit/type/build
checks and controlled Web/shared consumer backfill checks passed; consuming
Home/Market Web and Electron checks cover the migrated presentation. No native
runtime or completed performance acceptance is claimed.

MarketRow.webTable is an optional Web-only serializable presentation. It does
not change DataRow's 2..4-column contract or add a row type/native renderer.
Native parsers retain row dictionaries/JSON and read known fields; they ignore
webTable. Mobile callers continue emitting their existing Market snapshots
without this field, preserving their serialized shape. Shared validation checks
the complete declared extension on both platforms. No arbitrary React cells,
HTML, scripts or product formatting runs inside NativeList.

The presentation declares 1..12 unique keyed columns and one matching keyed
cell per column. Width/minWidth (0..4096 CSS px), flex (0..16), semantic alignment
and leading/trailing padding (0..64) are explicit. Presentation cell kinds are
favorite, asset, text and sparkline. Favorite has its own action/accessible
label/pressed state; asset uses existing leading image primitives, title/badges
and a secondary line optionally replaced on row hover/focus; text accepts at
most eight caller-formatted primary/secondary text parts with existing small
number segments and typography; sparkline accepts at most256 finite numeric
samples, explicit size and colors. Unknown fields are rejected. Table rows use
an explicit row/container height. Text is single-line with ellipsis; no automatic
multiline geometry or business numeric interpretation exists.

The consumer derives mobile fields and table cells from the same domain row.
A quote transaction changes its formatted scalar fields and table cells together.
A legacy scalar-only price/change patch on a table row is rejected unless it
also supplies its corresponding table presentation, preventing hidden/visible
values from diverging. Stable-column/same-image patches update only changed
cells, preserving body/avatar identity and avoiding geometry reads/layout.
Changes to column structure, image/badge/action identity invalidate anchors and
use normal rebind/layout. Header and body take widths from the same consumer
column calculation; React retains sorting/header/filter portals.

The Web registry separately pools market and marketTable presentations while
both retain type:market. Row keys, revisions, anchor epochs and event dispatch
remain common. Cell actions use data-native-list-action; favorite/copy/badge
buttons do not activate the row. Recycling clears actions, title/aria, hover
and styles and disposes image leases. The SDK emits copy action data; application
copyText/logging remains consumer-owned. Sparklines use bounded numeric drawing,
not arbitrary SVG/HTML input.

Acceptance: every cell kind, dynamic formatting, hover/focus/keyboard actions,
row/presentation recycling, strict validation, native dictionary safe-ignore,
header/body geometry, 100 quote ticks preserving nodes with zero geometry reads,
then a real complex Market table before expanding to all five families.


### Web grid fractional column geometry

Status: implemented; fractional geometry verified in focused DOM/browser and
controlled consuming NFT backfill checks.
Web grid columns divide
available CSS width without flooring each cell. With non-integral division,
all column edges and width-derived tile heights retain the fractional value;
the final column reaches the content edge without accumulating discarded
pixels. Item spacing, horizontal padding and structural full-span rows retain
their existing meanings. Native rounding is unchanged. Known app Web consumers
are the migrated NFT grid and developer gallery; external SDK consumers are not
claimed audited. Required geometry coverage: 2/3/4/6/7 columns, divisible and
fractional widths, spacing, padding, row height and structural/footer boundaries.

Table column width is the CSS base width. When flex is supplied, it grows with
`flex-basis:auto`, matching the consumer's existing fixed-base React column;
without flex, width remains fixed. Pausing a Web parent-host list retains only
its last row-host height in normal flow while releasing row DOM/avatar leases.
The wrapper preserves the engine's successfully patched snapshot for host
reconstruction; inactive patches merge into one latest snapshot, with no
retained batch queue. A rejected imperative snapshot never replaces that state.

Stock asset cells additionally declare lineGap (0...8, default4), up to three
secondaryBadges limited to the existing Market badge key/text/style subset, and up to four hoverImages
using ImageSource. Secondary badges precede the company name. Hover logos follow
the variant-count text at16 CSS px with a1px page-background ring and4px overlap,
matching the consumer's existing finite logo group. Geometry/badge/image changes
use normal rebind and release replaced image leases; price-only updates retain
these nodes. No native caller emits these Web-only fields.

Web gallery media follows the shared input precedence: media wins over image,
unless an explicit empty/error imageState suppresses media probing. An empty/error
gallery without either source retains the gallery fallback surface when
imageState explicitly selects that state.

Web gallery media preserves the consuming NFT card presentation: healthy image
and exhausted fallback surfaces have a10px radius, while a healthy video surface
has square corners. The image/video frame uses the list page background; only
the exhausted fallback surface uses the strong background. Rebinding the same
video key retains square corners, and failed-video fallback to an image restores
the image radius. This is Web renderer behavior; native media presentation and
probe-order contracts are unchanged.

Gallery pointer activation follows the actual media-card body; extra grid row
height reserved between cards does not enlarge its pointer hit area. Keyboard
row activation remains supported through the normal row keyboard handler.

When React listEmpty owns an empty list, content bottom padding follows that
empty slot instead of advancing its origin. Header/top padding retain their
positions, and populated row/footer padding semantics remain unchanged.

Text cells may declare partGap (0...8 CSS px, default2) for their caller-formatted
inline parts. Favorite cells may declare hoverActionKey; hover/focus emits this
anchored help action, while click/Enter/Space emits actionKey. No browser title
bubble is added. Pointer leave/focus departure invalidate help anchors. Existing
Market badge styles apply to table title pills and their title gap is4px.

Web table title badges accept only declared key/text/iconName/icon/colors/action/
accessibility/style fields. The unused native tone field is not accepted in this
presentation. Inline icon sources use image primitives and release on recycling.


### Web Market watchlist actions and reorder

Only a market row with `webTable.draggable:true` and list capability
`reorderable:true` joins existing Web reorder. Other Market rows and native
reorder semantics are unchanged. `webTable.contextMenuActionKey` declares a
bounded row context-menu action; right click is prevented only for such rows,
and nested cell actions retain ownership. Context menu and Market long press
may emit `RowActionEvent.windowPoint`, finite client/window CSS pixel coordinates.
Keyboard-triggered context menus use the row anchor rectangle when pointer
coordinates are unavailable. Anchors retain binding epoch validation and are
invalidated on scroll/rebind/layout/destroy; consumers close their menu on that
invalidation. Compact Market mouse/pen long press cancels after 3px of pointer
movement; touch follows actual touchmove as specified below. Table rows retain
the 10px pointer threshold.
Gallery, wallet and other row reorder eligibility stays unchanged. A gesture
that activates row reorder cancels pending Market long press and suppresses its
subsequent row navigation.

Compact Web Market network corners honor the declared ImageSource dimensions
(default 16px), with the existing corner padding outside the image. Native image
rendering is unchanged. Consumer Web presentation permits its original SVG image
sources without changing default native mapper output.


### Web ordered image fallback sources

ImageSource.fallbackUris is an optional Web-only ordered continuation after uri.
It retains the complete caller-provided sequence (no truncation or deduplication).
An empty/unusable continuation terminates in the declared visual fallback, like
an empty current source in the existing Web Image.WithFallbackSources. Each URI
uses its declared retryTimes before proceeding; Market Web callers pass the
existing ImageV2 default of one retry per source. This array path does not change
legacy fallbackUri switching/retry order when fallbackUris is absent. Native
callers do not emit this field. Validation requires an array of serializable
strings. Rebind identity includes the complete array; quote-only changes retain
the image node, while a changed chain rebinds and cancels previous retries.
Recycling/destroy disposes listeners, retry timers and avatar subscriptions.
Required coverage: one/two/three+ sources, first two fail/third succeeds, terminal
failure, empty continuation, chain replacement/cancel and legacy fallbackUri.

Enhanced Web History section titles retain the consumer headingXs 0.8px letter
spacing; hover uses rowPressedBackground and pointer-active enhanced History
uses rowSelectedBackground. Other Activity presentations are unchanged.

Parent-scroll compact Market rows retain the consumer's pointer states: ordinary
token/perp cards use active opacity0.8, stock cards active selected background.
Consumers supply the page background as hover color. New table image fallbacks
use a28px icon in the40px image frame; compact32px frames retain24px icons.
These styles do not change legacy internally scrolling Market rows.

The optional theme.imageFallbackBackground is consumed by Web only when a
declared visual fallback is displayed; unset leaves the existing transparent
fallback over its original frame. It never changes healthy image backgrounds,
Stock tags or hover logo slots. Native callers do not emit it. HoverImages may
contain explicit null entries to preserve the original16px empty round logo
slots, including their ring/overlap/order; non-null entries remain ImageSource
and the existing four-slot limit stays unchanged.

Compact Market subtitlePrefix preserves intrinsic text width with flex-shrink
and its existing maxWidth cap; it does not grow short names to that cap.
Its optional Web-only hoverActionKey requests anchored help only when the
actual prefix text is truncated (scrollWidth > clientWidth + 1). The consumer
owns the existing tooltip content/presentation. Pointer/focus departure, scroll,
rebind and destroy invalidate its existing anchor. Text, cap or action identity
changes use normal rebind; unchanged quote updates preserve that node/focus.
Native callers do not emit this field and native renderers ignore it.


### Host-provided Web fonts

configureNativeListFonts and NativeListFontFamilies retain the source baseline
public API and validation. In the historical controlled 3.0.177 consumer
backfill, only Web was backported:
empty configuration uses system families; each supplied regular/medium/semibold/
bold name belongs to fonts already loaded by the UI host. Hosts load fonts
before configuring the mapping; the historical consumer supplied Roobert for
all four weights. The SDK registers no fonts. That controlled backfill retained
the 3.0.177 native wrapper and native platform font behavior.
Web roots, section rails, reorder previews and all finite row text presentations
select the family matching their existing numeric weight; layout and numeric
weights are unchanged. A live configuration change invalidates active anchors,
cancels reorder, clears measurement caches and remeasures; the same configuration
is a no-op. A paused/null host initializes with the latest configuration.
The 6.7.0-1 source integration preserves its existing native font policy/API.

Market Web badge text uses tabular numeric glyphs, matching the consuming
Market text style for leverage/provider labels. This rule is limited to the
Market badge renderer class; generic Activity/time badge typography is unchanged.

React prop snapshots commit in the layout phase alongside React-owned slots.
For an external host with cached viewport dimensions, recomputation calculates
and expands the normal-flow row extent before geometry reads, avoiding an append
plus footer-removal intermediate clamp. The calculated layout is reused when
viewport dimensions are unchanged; changed dimensions receive a fresh layout.
Final measured layout owns legitimate shrinking; no scroll restoration offset
or retained minimum-height state is added. First creation without cached geometry
and imperative snapshot acceptance retain their existing contracts.

Web gallery media fit consumes the existing ImageSource.contentFit values:
cover/contain/fill map directly to CSS object-fit and center maps to none.
When omitted, a video uses contain and an image uses cover, preserving the
consuming Web NFT defaults. Failed-video transitions resolve the image default
again rather than retaining video fit. Native fit/probe contracts are unchanged.


### Source integration status (2026-10-16)

The Web migration was integrated onto source 655789b01 (6.7.0-1), preserving
its native font resolver, iOS 26 overlay, package metadata and native implementation.
The PR rebases that source onto main dd0d77af3, whose independent desktop Windows
release changes do not overlap this package. The consuming PR targets 3.0.180;
its reproducible patch and dependency verification are separate from the
historical 3.0.177 backfill evidence below.
The prior 3.0.178 source integration was an intermediate validated checkpoint.
Controlled Web/shared consumer backfill was verified against 3.0.177 while
retaining that version's native wrapper, binary boundary and native type
declarations. This historical backfill is separate from consumer dependency
updates.
Merged package tests/typecheck/build and controlled consumer backfill smoke
passed. The final Web-focused suite passed 89 checks; broader package checks
and consuming Web/Electron presentation checks are recorded separately. Pending
remote-row display and the real business hook/controller navigation contract
were verified with explicit auxiliary fixtures/stub boundaries; no real eligible
account SendReplaceTx confirmation page, signing, or broadcast was exercised.
The consumer backfill does not establish native runtime acceptance of this
merged package. Final performance sampling was stopped at the user's request
and is partial evidence, not a completed performance signoff.

### Desktop/Web Market asset parity

The Web table asset renderer consumes the existing leading visual backgroundColor
and borderColor descriptors without changing the schema or native primitives.
Its token frame remains40 CSS px including an optional1px solid border; the main
bitmap fills the content box (38px with border,40px without). Overlay visuals
remain outside that frame and fallback surfaces retain their existing color/icon
contract. Theme/leading changes rebind the visual and clear old border styles.
The address-copy revealed line uses a2px text-to-action gap, while Stock hover
logo groups retain their4px gap and overlap geometry. No other text-part gaps
change. Regression acceptance includes Dark/Light recycled visuals, preserved
network overlays, independent copy actions and Stock hover group spacing.

Gallery collection subtitles retain an 8px trailing text inset whether or not
a network thumbnail is present. The subtitle uses border-box sizing and its row
has no extra gap, preserving the same 8px separation with a thumbnail and the
legacy truncation width without one. Legacy MediaTile/native rendering is
unchanged.

Compact Market touch long-press cancellation follows actual `touchmove`
events and valid touch coordinates beyond the existing 3px stationary limit.
Pointer-only micro-movement within browser tap tolerance does not prematurely
cancel a touch long-press. Pointer cancellation and real ancestor scrolling
still cancel it; mouse, pen and table pointer thresholds remain unchanged.
The passive touch listener is removed when the engine is destroyed.

Table secondary-line pointer hover is enabled only when the primary input
reports `(hover: hover)`, matching the consuming Web row's hover media rule.
Touch capability alone does not disable hover. Existing focus-within reveal
remains available to keyboard navigation; no input-modality configuration or
native behavior is introduced.

The declared table asset background remains on its outer token frame during
source loading, matching the consumer's Token wrapper. The existing image
placeholder/retry path and fallback span are unchanged; only the fallback span
uses the fallback background. A solid frame border does not move external
overlays: their primitive-generated edge positions are shifted outward by the
1px border once per new visual, preserving the caller's unbordered outer-anchor
geometry. No shift is accumulated during same-visual quote patches.

Table secondary-line clipping uses `overflow: clip` so keyboard focus cannot
scroll the two-line reveal viewport in addition to its existing transform.
Focus-within and primary-hover reveal behavior remain unchanged; no scroll
position writes or global overflow policy are introduced.

Web history section headers consume the existing titleLoading flag with the
original20px spinner,8px title gap and heading casing. The declared32/52px
section slots remain unchanged; the pending title content retains its original
4px intrinsic overflow. Explicit60px stacked history content fills its stated
row height; ordinary history rows retain their existing trailing spacing.

Web enhanced History badges preserve the existing small Badge geometry (8px horizontal padding, 4px radius, medium text). Warning backgrounds consume the declared cautionBackground theme value with the prior fallback when absent. Native rendering is unchanged.


### Web external parent-scroll commands

When a caller supplies webScrollHost, scroll commands use the DOM scrollTo method from that element's document realm. React Native Web may decorate a NativeScroller node with a differently shaped React Native scrollTo method; the DOM host contract must not invoke that method with DOM options. The native/ref APIs and internally owned NativeList viewport keep their existing behavior.

Existing Pager command-accepted/cancel notifications use the actual external scroll owner when supplied, otherwise the internally owned viewport. A list command cancels queued Pager restoration/top work; a Pager top command cancels a pending list command and stops motion on that same owner. Listener target guards and destruction removal must match. No new public method, event name or native behavior is introduced.


### Web Pager inset notifications

An externally hosted list listens for the existing Pager inset notification on
its actual scroll owner, matching the notification target and removing the same
listener on destroy. It measures the new content origin before Pager restores
its C+B offset; a later ResizeObserver must not add that inset a second time.
Ordinary subsequent slot-height changes retain the existing content-anchor
compensation. Standalone lists continue listening on their own viewport.
