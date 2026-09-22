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
The implemented families are `message` and `legacy`; the latter serves all
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
recycling/windowing; shared primitives own image cancellation. The Message
pilot still allocates the legacy native host, so renderer extraction alone is
not evidence of lower allocations or faster scrolling. Performance claims need
separate workload and measurements.

## Conformance and six-stage migration

| Stage | Current status | Exit condition |
| --- | --- | --- |
| 1. Baseline | Partial: representative Message/runtime cases exist; implicit key/default/update inventory is incomplete | Preserve each existing rule or explicitly migrate its caller |
| 2. Message pilot | In progress: three-platform binding extraction and structural reuse families implemented | Renderer-owned lightweight views, resolved styling/measurement, update classification and lifecycle acceptance |
| 3. Simple templates | Not started | Migrate mediaTile, rail, action, system, activity, dataRow and metricCard; remove each old dispatch/reset path |
| 4. Complex templates | Not started | Migrate Market, Identity and SectionHeader while preserving quote, selection and header behavior |
| 5. WalletGroup | Not started | Independent member renderers; verify member events/styles, compact dragging and height restoration |
| 6. Cleanup | Not started | Remove migrated key inference and global style maps; container no longer manipulates template internals |

The next Message increment moves its text subtree and typography into the
renderer. Shared leading/thumbnail image slots and generic host lifecycle stay
in place until their primitives are extracted. The legacy host remains allocated.

Source boundaries and dated runtime evidence are in the incremental migration
section of [DESIGN.md](DESIGN.md). Acceptance covers style set/change/clear,
binding without recycle, same-key template changes, image add/remove, scrolling
away/back, explicit height restoration and fixed-footer placement. Unit/type
checks are not native runtime verification, and Message acceptance does not
complete the remaining stages.
