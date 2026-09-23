# Renderer migration baseline

Inventory captured at `a75666930` on 2026-09-22. This records existing behavior,
not a new public API. Keep each rule until its owning migration explicitly
replaces the caller contract. Renaming keys must not be an accidental side effect
of the Message extraction.

## Complete implicit key inventory

The audit covered the TS wrapper/validation/selection/scrolling, Web engine,
Swift models/cell/container, and Kotlin models/row/adapter/container. Searches
included key/sectionKey/column-key comparisons, prefix/substring tests and named
sets. Generated bindings and examples do not define renderer semantics.

| Rule | Existing behavior and source | Disposition |
| --- | --- | --- |
| `history-` prefix | iOS `NativeListCell.bindSectionHeader` and `RNCNativeListView.rowHeight`: row key **or** section key implies history title/height. Web `estimateWebRowHeight`: same two keys imply 16-unit header height; its rendering still follows the explicit variant. Android `bindSectionHeader`/`applySize`: only **section key** implies uppercase 12/16 title, 0 padding and 16-unit minimum height. Explicit `variant: history` works independently. | Stage 4 moved these defaults into the SectionHeader renderers. Both example history headers and the validation fixture already set `variant: history`; no implicit history-header caller remains in this repository. Stage 6 audited the app-monorepo callers and removes the compatibility fallback; only the explicit variant selects history styling. |
| `token-`, `balance-token-`, exact `linear-custom-token` | Android `NativeListRowView.bind` suppresses `separator: true` for these keys across row types. iOS/Web have no equivalent key exception. | Removed in stage 6 after auditing examples and app-monorepo: no matching caller sets `separator: true`. Separators now obey the explicit field on every platform. |
| exact section keys `linear-tokens`, `action-tokens` | Android `bindSectionHeader`/`applySize` applies token-manager 14/20 regular typography, horizontal 12/top 10/bottom 0 padding and 30-unit minimum height (subject to earlier explicit presentation/variant branches). No matching iOS/Web rule. | Stage 4 migrated both example callers to an explicit 30-unit container height, bottom alignment, 12-unit horizontal / zero vertical padding and regular 14/20 title. Stage 6 verified external consumers and removes the fallback. Renaming either key no longer changes typography or geometry. |
| exact tail keys `market-loading-more`, `market-load-more-retry`, `market-end` | Android `NativeListView.isMarketPaginationUpdate` drops those trailing rows before checking a stable Market prefix when either snapshot has loadMore; this keeps the scroll anchor during pagination. It is a container update optimization, not a row renderer. | Preserve in the container. Replace with explicit structural-row recognition in a separately validated pagination change. |
| exact column key `asset` | Web `createDataRow` adds row badges only to that table column. Native table binders attach row badges to the first column instead. | Retained as a documented DataRow compatibility rule. No app-monorepo DataRow caller was found in the stage 6 audit; changing badge placement requires a separate explicit API/caller migration. |

There are no other business-key-dependent decisions in the audited production
paths. Equality for identity/diffing, selection targets, section membership,
scroll targets, reorder membership, action lookup, image recycling keys and
stale-callback guards is intentional identity usage, not a business-key rule.
Keyboard event keys and style-property whitelist keys are also unrelated.

## Stage 6 consumer audit

Audited on 2026-09-23: local app-monorepo `67fcc71204` and `origin/x`
`62c645f906`. NativeList imports/builders in AccountSelector, UnifiedNetworkSelector
and Market have no history/token key styling dependencies. Repository token rows
omit `separator` (false); history headers specify `variant: history`; token
headers already specify their height, alignment, spacing and typography.
No external checkout changes were needed. This is a source audit of these refs,
not a claim about all published consumers.

The two retained rules above are the container pagination optimization and
DataRow badge-column ownership. Neither chooses a renderer or maps `row.style`
fields. List event/selection/reorder identities continue to use keys normally.

## Default geometry and precedence

[STYLE_SPEC.md sections 8–9](STYLE_SPEC.md) inventory all twelve templates' row
heights, typography, spacing, image sizes and existing platform differences.
Preserve those unstyled defaults when extracting view ownership. Explicit
`style.container.height` wins over `row.height`; compact WalletGroup reorder
height remains a temporary container override. Style removal restores defaults.
No overflow fitting or font shrinking is introduced.

Message baseline: native 28-unit leading/64-unit thumbnail, title/body 14/20,
time 12/16; iOS column gaps 2 plus a 2-unit timestamp inset, Android body/time
margins 2/4. Web retains its existing horizontal timestamp, 40-unit leading,
64-unit thumbnail, 15/20 title, 13/18 body, 11/16 timestamp and 8/12 padding.
Explicit native style values use logical units; Android legacy defaults/model
heights retain NativeListScale, while explicit styles use display density.

The old iOS/Web Message estimators used different fallback metrics from their
views (including 20 horizontal padding and 14/20 time). Stage 2 must correct this
as an explicit measurement change, separate from view extraction; explicit
heights and the unstyled visual defaults above stay unchanged.

## Special update and lifecycle inventory

| Path | Required invariant |
| --- | --- |
| Full snapshot/patch | Stable key identifies data; incompatible renderer family replaces the view. Bind A → B is correct without recycling. |
| Message | Renderer classifies no-op, content/style, asset and replacement work. Unchanged image descriptors retain requests/views; changed or removed assets cancel old callbacks/retries. |
| Market quote | Preserve price/priceSegments/change/accessibility partial updates and explicit rich text styles. Theme/layout/non-quote changes still require full binding. |
| Selection echo/summary | Preserve native immediate feedback, latest descriptor/action context and fixed-height summary updates without resetting images. Unknown/mixed payloads keep full binding. |
| Async image completion | Request identity and binding lifetime reject stale writes; bounded retries/fallbacks cannot cross recycle/disposal. |
| Action anchors | Rebind/recycle invalidates old epoch-owned source anchors. Selection-only presentation updates retain their current owner. |
| WalletGroup/reorder | Keep member events, compact 68-unit drag appearance, expansion restoration and bounded member-host retention. Message extraction does not change these paths. |
| Containers | Header/footer, sections/index, scrolling, table/grid/horizontal allocation and pagination remain list-owned. |

## Acceptance baseline

The existing 150 package tests and seven Android tests, together with the dated
Message runtime cases in [DESIGN.md](DESIGN.md), establish the pre-migration
baseline. Native title/body/time bounds were captured before text extraction.
The Message fixture covers explicit/model height, style set/change/clear, blank
body/time, same-key Message/Identity changes, images, end/top scrolling, 41-row
mixed swaps, empty/repopulate and the fixed footer on all three platforms.
Full native acceptance of the later templates remains a gate for stages 3–5,
not an implied result of finishing this inventory.

## Rail extraction baseline (stage 3)

Rail keeps iOS title/badge gap 8 versus Android/Web 6. Android's default status
margin is 0 even though its historical width reserves 6; iOS/Web use 6. Default
height remains iOS/Web 40 and Android's scaled minimum 28. Native text is 12,
with 16-unit title/badge line metrics; Android status keeps its natural line
height. Web retains 12/11/13 for title/badge/status. Leading size is 20 and
padding is 4. These are existing differences, not different style property APIs.
Horizontal sizing retains the legacy allowances (including iOS's old 6-unit
badge-gap estimate inside its trailing budget and Android's status allowance)
and now applies explicit resolved style metrics. Style removal restores the
baseline dimensions. Rail does not show persistent row selection; press and
reorder feedback remain container-owned.

## Stage 4 caller audit

The 2026-09-23 search covered all tracked source and examples.
`example/react-native/pages/NativeListExamplePage.tsx` has two history headers;
both already supply `variant: history`. Other `history-` occurrences identify
Activity section membership or explicitly styled validation fixtures. The two
Token Manager headers now declare their geometry and typography through the
shared rowStyle contract. Bottom alignment puts the 20-unit title at the bottom
of its 30-unit row without requiring a new asymmetric-padding property.
Those explicit style values use logical units on all platforms, replacing
Android's old scaled default at these caller sites.

Stage 6 completed the external-consumer source audit described above and removed
the native/Web header key fallbacks. Stage 5 also migrated WalletGroup members
to independent Identity hosts. The historical defaults in this baseline remain
useful when reviewing explicitly styled caller behavior.
