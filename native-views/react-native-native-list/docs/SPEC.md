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
row's appearance. This migration does not change the TypeScript/Nitro protocol.

## Lifecycle and concurrency

Full binding must work without a preceding recycle callback, including
style set/change/clear and content A → B. A binding epoch invalidates stale
image callbacks and action anchors. Recycling/disposal releases pending work
through the existing image/host lifecycle. UI mutation remains on the platform
UI thread; [DESIGN.md](DESIGN.md) describes update and event ownership.

## Data, cache and identity

`row.key` identifies data; renderer reuse identity describes compatible view
structure. Content, styles, height and placement must not enter reuse identity.
The implemented families are `message`, `rail`, `mediaTile`, `action` and `legacy`; the latter serves all
unmigrated templates. Same-key family changes must replace the incompatible
host. OneKeyImage owns image caches; NativeList owns row/slot binding identity.

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
[DESIGN.md](DESIGN.md). Existing normalization remains authoritative; this
migration adds no new input limits, retry policy or automatic layout correction.

## Performance and resources

Snapshots and patches cross the bridge in batches. Visible rows use platform
recycling/windowing; shared primitives own image cancellation. Message, Rail, MediaTile and Action use dedicated lightweight native hosts and persistent Web bodies.
None allocates the legacy native tree. This structural change is
not a measured scrolling-performance result; performance claims still need a
separate workload and measurements.

## Conformance and six-stage migration

| Stage | Current status | Exit condition |
| --- | --- | --- |
| 1. Baseline | Complete: [implicit keys, defaults, special updates and acceptance baseline](MIGRATION_BASELINE.md) inventoried | Preserve each existing rule or explicitly migrate its caller |
| 2. Message pilot | Complete: registered lightweight native hosts, persistent Web body, resolved styling/measurement and lifecycle acceptance | Lightweight native hosts, resolved styling/measurement, update classification and lifecycle acceptance |
| 3. Simple templates | In progress (4/7): Rail, MediaTile, Action and System migrated on all three platforms; three templates remain | Migrate mediaTile, rail, action, system, activity, dataRow and metricCard; remove each old dispatch/reset path |
| 4. Complex templates | Not started | Migrate Market, Identity and SectionHeader while preserving quote, selection and header behavior |
| 5. WalletGroup | Not started | Independent member renderers; verify member events/styles, compact dragging and height restoration |
| 6. Cleanup | Not started | Remove migrated key inference and global style maps; container no longer manipulates template internals |

The closed internal registry selects Message, Rail, MediaTile, Action or the legacy family. Message owns
its text column, optional leading visual and thumbnail; its native host does not
allocate Market, WalletGroup or table views. Web preserves the Message body,
text nodes and unchanged image elements when rebinding. Remaining templates
continue through the legacy family.

Message resolves text/spacing/image inputs from data/theme/styles, classifies
updates as unchanged, content/style, assets or replacement, and binds/measures
using those resolved values. Image slots retain an unchanged effective request;
source/row/slot or image-request-option changes invalidate old callbacks and
retries. Image request epochs are independent of row action-anchor epochs, so
text-only updates preserve in-flight images. Legacy Message binding, slot-map
and measurement branches have been removed. The named Android separator-key
compatibility policy is retained outside the renderer's style resolver.

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
Stage 3 still requires mediaTile, action, system, activity, dataRow and metricCard.

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
