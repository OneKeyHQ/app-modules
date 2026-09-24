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
  descriptors that did not change should retain their bindings. Image requests have their own
  slot epoch; source changes/recycle invalidate them. Row rebind/recycle still
  invalidates action anchors through the host's binding epoch.

Message completes the first renderer pilot, with one platform per commit:

| Platform | Renderer and host ownership | Container ownership |
| --- | --- | --- |
| Web | `MessageRowRenderer`: persistent body/text/image nodes, resolved styles, update classification and intrinsic estimate/DOM measurement | Registered family pooling, placement, shared row appearance/events and layout correction |
| Android | `NativeListMessageRowView`: lightweight host; `NativeListMessageRenderer`: owned column, resolved text/spacing/images and update classification; native `onMeasure` consumes the bound views | Registered holder factories, DiffUtil, selection/reorder routing and viewport allocation |
| iOS | `NativeListMessageCell`: lightweight host; `NativeListMessageRenderer`: owned column, resolved text/spacing/images and width-aware measurement | Registered cell factories, data-source reload/reconfigure, selection/reorder routing and viewport allocation |

`NativeListResolvedText`, `NativeListLeadingVisual` and `NativeListImageSlot`
are internal primitives. The image slot owns request identity, bounded retries,
completion-once guards and stale-write invalidation. A text/style-only update
retains the existing image slot and, when its effective image request is
unchanged, the request. Geometry/content-fit changes may require the image
module to decode/request again. The existing bounded source-fallback cache is
shared by image primitives. Image slots are lazy and bounded to the template's
visible assets; Message never allocates the unused legacy Market/Wallet/table
subtrees. This is source-level ownership evidence, not a performance benchmark.

The renderer resolves defaults from current data/theme/style rather than
capturing a previous row's view state. Global cross-template slot maps and legacy hosts are removed after all twelve
renderer migrations. No public plugin API or Nitro schema is added. See
[SPEC.md](SPEC.md#conformance-and-six-stage-migration) for all six stages.

Structural reuse now has twelve dedicated families on all platforms; see the
current registry inventory in SPEC.md. WalletGroup composes keyed Identity
hosts. The old native classes and fallback registry entries are deleted in stage 6.
Neither `row.key`, content, style, height nor placement changes the family.
Web selects a compatible wrapper pool even when the row at a mounted index
changes family. iOS registers separate reuse identifiers and reloads retained
keys whose family changed; compatible updates still reconfigure, and newly
inserted keys are not marked for reload/reconfigure. Android uses the family as
`getItemViewType`; DiffUtil identifies data by `row.key`, allowing RecyclerView
to replace an incompatible holder. Market quote and selection payloads retain
their existing full/partial update decisions. Footer and nested-row hosts remain
outside these scrolling pools.

The native reuse families allocate different classes through the closed
registry. The list only uses `NativeListRowHost` lifecycle operations; Message
binds and measures its own resolved model. Market quote, selection echo,
WalletGroup compact/expanded reorder and fixed-footer behavior remain on their
existing paths. Stage 3 migrates simple templates one at a time.

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

The Message host completion on 2026-09-22 adds the implicit-key inventory and
finishes stage 2. Package regression tests cover persistent text/image identity,
style clearing, removed-image cleanup and incompatible-family reuse. Both native
apps were rebuilt and installed on the dedicated external-drive iOS 26.5/API 36
simulators. Runtime cases cover style/height set-clear, intrinsic height with
missing fields, narrow grid columns, local/HTTP images, text-only image updates,
failed-image fallback/recovery, delayed-source removal, mixed-family swaps,
empty/repopulation and scrolling with the common fixed footer. Chrome also runs
these flows at desktop width and 390px RTL. The baseline explicit row heights
remain iOS 136 → 192 → 136pt and Android 122 → 192 → 122px on the narrow device;
Web remains 136 → 192 → 136px. Automatic measurement now follows actual resolved
metrics (the title/body/time intrinsic example is iOS 114pt and Web 74px),
including the allocated iOS grid column width. A grid text-only patch shrinks
the Message from 114 to 94pt; the same full-width text would occupy one line,
so patch invalidation is checked against the actual column. The Web primary
image style leaves network/corner decorations at their original dimensions. Runtime evidence is retained in
the task's external validation directory. The dated earlier paragraphs describe
intermediate commits, not the current stage-2 boundary.

`linear`, `sectioned`, `grid`, and `table` are observable native container
semantics. Linear is a full-width stream. Sectioned adds a visual break before
section-header rows and can pin those rows. Grid allocates native spans and
makes structural rows full-span. Table uses compact alternating rows and fixed
weighted/aligned columns. Orientation, refresh, load-more, visibility events,
empty state, fixed footer, and native reordering are capabilities, not row
types.


### Stage 3: Rail increment (2026-09-22)

Rail joins the closed registry on Web, Android and iOS. Its title, badge, status
and leading visual belong to the Rail renderer; it no longer allocates the
legacy composite tree. Message and Rail share `NativeListRendererCell` /
`NativeListRendererRowView` for appearance, binding classification, action-anchor
invalidation and recycling. Each declares its own asset fields. Web uses a
shared primitive contract and visual-style restoration without touching async
image visibility. No public API or generated bridge change is involved.

Unstyled typography, gaps, platform height defaults and horizontal width
allowances are preserved. Explicit row styles participate in horizontal sizing;
this repairs the previous fixed-metric estimator. The Android resolved-text
primitive also fixes an omitted `offsetY` being read as NaN when another text
style was provided; it now resolves to zero. Explicit line heights without an
offset visibly render in both Rail and Message.

The increment passes 153 package tests, TypeScript, lint (zero errors), Android
build/seven unit tests, and a full iOS build. Chrome, Android API 36 and iOS 26.5
ran style set/clear, local images and corner badges, text/image replacement,
badge/status removal, horizontal sizing, same-key Rail/Message/Identity swaps,
end/top scrolling, empty/repopulate and the shared fixed footer. iOS default and
styled text bounds match the captured legacy Rail baseline. Chrome additionally
ran a 390px RTL viewport. All three platforms verified row clicks, disabled clicks and drag reorder.
iOS reorder used XCTest long-press-and-drag with a 600ms hold; a synthesized
swipe alone did not exercise the long-press recognizer.

This is stage 3's first template, not completion of the seven-template stage.
mediaTile, action, system, activity, dataRow and metricCard remain in the legacy
family. Their variants, embedded actions and table semantics require their own
increments and runtime acceptance.

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
all JavaScript rows. On iOS a cell also rebinds when its list-level inputs
change: layout, layout direction, or `theme`/`listStyle` under type-aware
structural equality (`true` differs from `1`, `1` equals `1.0`, an explicit
`null` inside an object differs from a missing key, and a missing
`theme`/`listStyle` equals `{}`).

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

### Stage 3: MediaTile acceptance (2026-09-23)

Dedicated Swift/Kotlin hosts and a persistent Web body now own MediaTile. Old
MediaTile binders, native-only view allocations and semantic style-map branches
were removed. Shared hosts provide vertical container alignment and epoch-owned
action dispatch without allocating other templates.

Validation used the external-drive runtime and its dedicated iOS 26 simulator
and Android API 36 emulator, plus headed Chrome. Native before/after captures
confirmed the grid geometry; styled height 260 restores to the iOS 233-point
grid default on clear. iOS retains its legacy title compression with a close
control at the default grid height. Style set/clear, empty/error images, close
action counter, end/top scroll, empty/repopulate and fixed footer were exercised
on all three platforms. Web also verifies same-key incompatible-family reuse,
retained image elements and source contentFit restoration in regression tests.
Evidence: external validation runtime `ios-media-*`, `android-media-*` and
`web-media-*`; build logs retained there. iOS and Android Debug builds and
Android unit tests pass; package typecheck and all 154 tests pass. Focused ESLint
has no errors (six pre-existing engine warnings). Remaining stage 3 templates:
Action, System, Activity, DataRow and MetricCard.

### Stage 3: Action acceptance (2026-09-23)

Action uses a dedicated renderer on all three platforms. Native accessories
are a bounded primitive (two accessory slots plus checkbox/spinner), shared
through the host action epoch and a selection-only presentation hook. Legacy
Action dispatch, typography, style-slot and default-height branches are removed.
Fixed-footer creation was migrated to the same registry after runtime QA found
that the old direct legacy allocation no longer rendered Action. Footer family
replacement invalidates the previous host and keeps list-owned gestures/events.

On the task's iOS and Android simulators, default and value-pair/menu bounds
match the pre-migration captures. Style set/clear, same-key accessory variants,
menu and select-all checkbox actions, end/top, empty/repopulate, Media/Action
family replacement and fixed-footer rendering/clicks were exercised. The iOS
event counter reached 3 after menu, checkbox and footer; Web checked select-all
state and footer dispatch in headed Chrome. Evidence is `ios-action-*`,
`android-action-*` and `web-action-*` in the external validation runtime.
Build/type/unit checks pass; 155 package tests include style restoration, stable
Action title/icon nodes, selection echoes and incompatible-family reuse.
An Android input timeout also affected Launcher; its captured main stack was in the display event loop. After restarting the task emulator, menu, checkbox, scroll, repopulate and footer checks passed (action counter 3). This does not establish a library-level ANR cause.
Remaining stage 3 templates: System, Activity, DataRow and MetricCard.

### Stage 3: System acceptance (2026-09-23)

System owns loading, retry, warning, noMatch, end and spacer rendering and
warning measurement on all three platforms. Its skeleton views and legacy
binders are removed from the universal host. Native source pixel rounding,
warning wrapping and existing per-platform default geometries remain intact.
The shared footer factory can reuse System and Action through the same registry.

The dedicated iOS and Android runtimes exercised default retry, style set/clear,
warning, skeleton, spinner, Market retry/noMatch/end, spacer, retry action,
empty/repopulate and end/top scrolling. Default retry captures match the old
geometry. Headed Chrome exercised the same variant cycle and retry action;
regression tests cover style clearing, retained warning nodes and variant reuse.
Evidence: external runtime `ios-system-*`, `android-system-*`, `web-system-*`.
iOS/Android Debug builds, Android unit tests and 156 package tests pass.
Remaining stage 3 templates: Activity, DataRow and MetricCard.

### Stage 3: Activity acceptance (2026-09-23)

Activity owns its leading visuals, text, amounts and three bounded footer-action
buttons. Its legacy dispatch and exclusive native allocations are removed; Web
Identity no longer dispatches Activity internally. Secondary image descriptors
use the shared bounded image primitive. Action's Web visual now also applies
explicit image width/height and clears them back to its defaults.

The dedicated native runtimes and headed Chrome exercised default captures,
style set/clear, secondary images, row footer actions, end/top scrolling and
empty/repopulate with incompatible-family reuse. iOS preserves its pre-existing
text compression for content exceeding the default 60-point height. Native
builds and Android unit tests pass; 157 package tests cover image retention,
amount/style clearing, action replacement and Action image dimensions.
Evidence is retained as `ios-activity-*`, `android-activity-*`, `web-activity-*`
in the external runtime. Remaining stage 3 templates: DataRow and MetricCard.

### Stage 3: DataRow acceptance (2026-09-23)

DataRow owns four bounded column views, leading visual, favorite, index and
checkbox controls. Its table striping retains the effective row index and
platform defaults. Dedicated native hosts and the persistent Web body replace
the legacy table allocations, dispatch and style paths.

The dedicated iOS/Android simulators and headed Chrome exercised selection
(callback counter increment), linear/table switching, style set/clear,
end/top scrolling and empty/repopulate with family replacement. Native default
captures preserve column geometry and existing truncation for the **table**
layout only; the 2026-09-24 audit found that native linear DataRows had been
moved to the table column structure (pill badges, separate secondary lines) instead of
the legacy single label with inline badges. The same audit restored the legacy
Web linear structure (secondary leading text inline before the primary text,
secondary text directly below) and the 48-unit Web table height for rows
without secondary text. The Web regression
changes three columns to two and back, clearing secondary labels and styles
while retaining the unchanged image. Native builds and Android unit tests
pass; all 158 package tests and typecheck pass. Evidence: external runtime
`ios-data-*`, `android-data-*`; MetricCard is the last stage 3 template.

### Stage 3: MetricCard acceptance and closeout (2026-09-23)

MetricCard completes the seven-template migration. Standard, activity and
performance layouts use dedicated native hosts and a persistent Web body.
Legacy metric allocations, composite builders, slot remapping and height
dispatch are removed. Shared iOS asset classification also accepts arrays;
metric visuals use independent slot identities even when metric keys repeat.
Text changes retain unchanged image requests. Removing an image invalidates
its pending work before a different visual is bound.

The dedicated iOS/Android runtimes exercised all three variants, style
set/clear, explicit height restoration, end/top scroll, empty/repopulate,
incompatible-family replacement and fixed-footer display. Their default
geometry was compared with pre-migration captures; standard badge color and
Android subtitle sizing retain their original defaults. Final DataRow audit
also restored native table padding to 20 horizontal / 10 vertical.
Headed Chrome exercised the same transitions. The regression changes content
while styled, clears styles, cycles the three variants, and verifies stable
image elements and restored 40px standard / 16px composite visual dimensions.

Validation: iOS Debug build/link, Android Debug APK and seven Android unit tests
pass; package typecheck and all 159 tests in five suites pass. Focused lint has
zero errors. Evidence remains in the external-drive validation runtime as
`ios-metric-*`, `android-metric-*`, `web-metric-*`, `ios-data-*`,
`android-data-*` and native build logs. These are functional/visual checks,
not scrolling-performance measurements.

Stage 3 is complete: Rail, MediaTile, Action, System, Activity, DataRow and
MetricCard (7/7). With the earlier Message pilot, eight template types now use
the closed renderer registry at that checkpoint. Stage 4 completion is recorded
below; stage 5 owns WalletGroup, and stage 6 removes remaining legacy policies.
Historical “remaining stage 3” lists above describe each incremental checkpoint.

The final iOS ownership pass moves measurement and size-preset policy into each
registered renderer host. The registry now only forwards through its type table.
After rebuilding and reinstalling, default → styled → cleared heights were
verified as MediaTile 233 → 260 → 233, Action 60 → 100 → 60, System retry
44 → 160 → 44, Activity 60 → 160 → 60, DataRow 60 → 120 → 60 and MetricCard
132 → 240 → 132 points. The runtime stores `ios-renderer-measurement-final.json`.

## Stage 4: Market, Identity and SectionHeader (2026-09-23)

The closed registries now dispatch eleven templates to dedicated native hosts
and Web bodies. Market owns its leading visual, bounded badge slots, quote
controls and quote-only binding. Identity owns title/subtitle/tertiary, selector
presentation and trailing controls. SectionHeader owns title/value/checkbox,
summary updates and the platform-specific header measurement. iOS container
measurement no longer contains branches for these three templates. Android's
sticky header uses the same registered SectionHeader host as ordinary rows.
WalletGroup's nested native Identity implementation remains scoped to stage 5;
the unused legacy Market/Header branches and global style maps are stage 6
cleanup, not an alternative dispatch path for these migrated rows.

Shared primitives accept only the geometry/typography needed by these renderers.
They retain unchanged image requests and reset style defaults before each full
bind. Web restores image geometry without restoring stale async opacity.
Accessory orientation/spacing starts from the renderer's defaults on every bind,
including style removal without recycling. Selector rounding, native idle corner
radii and explicit-height precedence remain intact. Header partial updates
retain resolved font metrics, so repeated updates do not rescale Android text.
The template-local iOS helpers no longer branch on other template types.

The baseline comparison included token/stock/perp Market, standard/account/
network/sidebar Identity, and standard/summary/gallery/history/network headers.
All were exercised with style set/clear and same-key family changes. Fresh legacy
iOS Identity/Header hosts matched the extracted text layouts; the original
mixed-family baseline also exposed stale text attributes inherited from Market.
Dedicated reuse families prevent that cross-template state from being reused.

Interaction acceptance covers quote patches with preserved style/image state,
badge actions, press-in and long-press anchors with a window point, checkbox
selection, summary title/value patches and subsequent value actions, end/top
scroll, empty/repopulation and the common fixed footer. The real section fixture
contains three indexed headers and ten Identity members per section. It exposed
two container problems: iOS selected pinned headers from the previous visible-cell
set during imperative scrolling; Android's pre-draw overlay could remain at zero
size beneath a React Native parent. The iOS container now uses original layout
positions for pinning/index highlighting, and Android lays out its measured
header overlay when needed. These policies remain outside row renderers.

The repository caller audit is recorded in MIGRATION_BASELINE.md. History
examples already specify their variant; both Token Manager example headers now
specify rowStyle height/typography/spacing. External-consumer compatibility
fallbacks remain until the stage 6 audit.

Validation artifacts are in the task's external-drive validation runtime:
`ios-stage4-*` and `android-stage4-*` screenshots/accessibility trees, with Web
captures under the stage 4 harness. These are native simulator and headed Chrome
functional/visual checks, not a consumer-app release acceptance or a scrolling
performance measurement. Web also exercised narrow/RTL sections, index clicks
and image-node retention after a quote patch.

Stage 4 completion: package typecheck and 161 tests across five suites pass,
with zero focused lint errors (two existing shadowing warnings). iOS Debug
build/link passes; Android Debug APK and seven unit tests across three suites
pass. Both native runtimes passed the interaction sequence after the container
fixes. Stage 5 WalletGroup/member dragging and stage 6 legacy/key cleanup remain.

## Stage 5: WalletGroup (2026-09-23)

All twelve templates now enter through the closed registry. The new native
WalletGroup hosts and Web renderer reconcile member views by member key, bind
each through the migrated Identity renderer, and recycle removed members
immediately. Retention is bounded by the current member count; native holds one
additional compact parent. There is no spare-member high-water pool. Recycle
releases member images, action bindings and the compact parent. Group appearance
and padding stay on the composite; member text/image/container styles stay local.

iOS and Web expanded measurement moved from the list container into WalletGroup.
Android uses its registered composite host to resolve expanded, compact and
animated heights. Explicit group/member style heights retain precedence, and
member defaults/badges keep the previous measurement. The list continues to own
reorder activation, destination selection, scroll and final order events. Native
compact rendering uses a separate Identity parent and `+N` badge; Web uses the
existing leased preview. Only children whose draggable is not false count toward
`+N`; non-draggable members cannot initiate a drag. (Correction, 2026-09-24:
this stage also blocked disabled members; the audit restored the legacy rule on
all platforms, so only `draggable: false` excludes a member.) Drop restoration
uses the current configured height/appearance rather than default chrome.
The compact allocation is 68 on iOS and Web; Android keeps its legacy
`max(68, parent member height + 2 × group vertical inset)`.

Member event origins retain the nested Identity host and its binding epoch.
Removing/rebinding a member invalidates its previous action anchor. The iOS
member tap recognizer ignores UIControls so menu actions do not also emit a row
press. Native WalletSidebar now binds optional trailing controls, matching Web;
callers allocate sufficient height for these controls. Member selection visuals
continue to follow each Identity's selected field. This migration does not add
nested members to the list's existing top-level selection domain. Group-level
disabled state gates member actions on Web. (Correction, 2026-09-24: this stage
also gated disabled members on Web; the audit removed that non-legacy gate, so a
disabled member in an enabled group keeps its actions.) Android keeps its
legacy rule that a disabled group does not block member actions; the resulting
per-platform differences are listed in SPEC.md.

Acceptance ran on the task's independent external-drive iOS 26.5 simulator,
Android API 36 emulator and headed Chrome. Member press/menu and updated action
keys/anchors, selected/disabled state, member removal/swap, style set/reset,
same-key Identity/composite replacement, empty/refill, recycle and the shared
footer passed. Child-initiated drags reorder the whole group; excluded members
do not reorder. Captures show the 68-point preview and +1 badge (one draggable
child and one excluded add-wallet row). The fixture restores 274-point default
and 340-point styled heights. iOS no-op drag, Android touch cancel and Web Escape
cancel also restore the expanded appearance. A fast iOS QA scroll initially
moved the group outside the viewport; returning to Top verified the full settled
340-point group, without a product-code change.

Validation: TypeScript and 163 tests in five suites; focused lint zero errors
with two pre-existing warnings; iOS Debug build/link; Android Debug APK plus
seven tests in three suites. Web tests additionally assert keyed member/image
retention, removed-member disposal, style clearing and disabled menu dispatch.
The validation runtime's `stage5` directory contains fixture, scripts, screenshots,
accessibility trees and iOS drag recordings. This is functional/visual simulator
acceptance, not a performance benchmark. Stage 6 completion and retained compatibility boundaries are recorded below.

### Stage 6: remove migration scaffolding (2026-09-23)

Deleted `NativeListCell.swift` and `NativeListRowView.kt`, their global style-slot
maps, and the obsolete slot-map tests. Still-used fonts, icons, controls and
layout helpers keep their existing behavior in shared primitive/host files.
The registry has only the twelve supported families; native parsing rejects
unknown types. Footer initialization starts with the lightweight Action host.

Web removes the legacy pool and warning-specific container measurement cache;
the System renderer reports a warning's rendered height through the registry.
Selector tabular typography belongs to Identity, Header and Action. WalletGroup
provides its compact parent preview through the internal renderer registration;
the container no longer selects a parent DOM node from the composite internals.
Common event hit-testing, selection primitives and cloned-image leases remain
container responsibilities.

The source audit at the app-monorepo refs in MIGRATION_BASELINE.md found no
callers dependent on history/token key styling or separator suppression.
Removed those three inference rules. Explicit variant/style/separator fields
now determine the behavior. The audited Market pagination-tail optimization and
Web DataRow `asset` badge placement remain documented compatibility boundaries;
this cleanup does not alter pagination or introduce a new column-badge API.

Acceptance ran on the same task-owned external-drive iOS 26.5 and Android API 36
simulators and headed Chrome. The same row key cycles through all twelve families;
each survives an explicit styled height/background and clearing back to its
original geometry. Ordinary/history-named headers match; explicit history uses
16 units, and explicit token-header styles use 30. Action → System retry → Action
footers render and emit their current action. Scrolling/reuse and member/menu
routing pass. WalletGroup drag previews remain 68 high with +1 for one draggable
child, excluded add-wallet members do not reorder, and dropping restores default
274 or explicit 340 heights. Web also checks a 390px viewport and changing family
while scrolled. Screenshots show controls/footer within the viewport; list content
scrolls normally. No stale prior-template content or cleared-style leakage was
observed in these scenarios.

Validation: TypeScript, 164 tests in five suites, focused ESLint (zero errors,
two pre-existing shadowing warnings), iOS Debug build/link and Android Debug APK
plus five unit tests in two suites. The two deleted tests only exercised the
removed global slot mapper. The source audit finds no legacy host/registry/global
slot references in production code and no header/separator business-key styling.

Fixtures, scripts, build/test logs, screenshots, accessibility trees, event/geometry
results and iOS drag recordings are saved under the validation runtime's `stage6`
directory. This completes all six architecture stages, with the two explicit
compatibility boundaries above. It is functional/visual simulator acceptance,
not a scrolling-performance benchmark or a full app-monorepo release signoff.

### Legacy-default audit (2026-09-24)

A review of the completed migration compared every renderer with the
pre-migration engines and restored legacy unstyled rendering inside the
renderer structure; implicit key-based styling stays removed. Notable outcomes:

- Native DataRow linear rows use the legacy single-label column again (see
  ROW_TEMPLATES.md); Web restored its own legacy linear structure.
- Pressed feedback, WalletGroup appearance, separator insets, disabled-row
  action rules and the System retry label are documented as retained platform
  differences in SPEC.md.
- Android's pinned-header pre-draw listener is registered only while the list
  is attached, on the captured view-tree observer, and removed on detach, so
  detached lists no longer leak it.
- iOS caches measured Message text heights per resolved text/width input in a
  process-wide `NSCache` bounded to 4,096 entries (system eviction), which
  avoids repeating text layout for unchanged rows during scrolling. This is a
  cost reduction, not a measured scrolling benchmark.
- Web: MetricCard keeps loaded images visible across rebinding, WalletGroup
  `verticalPadding` replaces the legacy border inset in both measurement and
  rendering, container writes are undone before each renderer rebind (no leaked
  alignment, no reverted renderer values), every System retry is message-only
  again, and selector geometry follows either explicit height field.
- Review round 3: Web restores the legacy Market subscript ratio under an
  explicit `fontSize` (iOS/Android too), System warning DOM-height layout, a
  text-style rebuild for System/MetricCard so cleared `lines`/`offsetY` leave
  no wrapper or rewritten text, and `margin-bottom` restoration on reused
  visuals. iOS/Android selector geometry now follows either explicit height
  field (Android source scale still keys on `row.height`), Android rebinds the
  fixed footer on a `listStyle`-only change, and iOS WalletGroup ignores the
  row-level `backgroundColor` in its full-width fill too.

Web validation is jsdom/unit level (package typecheck and tests); native
validation for this round is reported with the native changes.
