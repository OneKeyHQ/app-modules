# NativeList Style Spec

Status: **draft**. Line numbers are as of `d91262e98`; symbol names are the stable
anchor and should be preferred when they disagree.

This document is the shared design vocabulary for `@onekeyfe/react-native-native-list`.
iOS, Android, and Web each own a separate renderer, and nothing in the build forces
them to agree. This spec is therefore **authoritative by review, not by codegen**.
It records what a value is called, what it should be, and what each platform
currently does — so a divergence is visible in review instead of on a device.

See [DESIGN.md](DESIGN.md) for the architecture and ownership model.

## 1. Why this exists

The list serves 13 row templates from one shared view pool per platform. Every
template hard-codes its own typography and geometry in three places, and the three
vocabularies have drifted:

- The package's theme keys and the application's design tokens are **two different
  names for the same colors**. Every consumer writes a mapping table by hand.
- The application has a named type scale (`$bodyMd`, `$headingSm`, …). The list has
  ten-plus bare font sizes and references the scale nowhere. "One step smaller" has
  no shared meaning.
- The same concept resolves to different numbers per platform (see §6).

Only `market` rows accept a `style` object today. Every other template is tuned by
adding a `presentation` variant plus a branch in each renderer.

## 2. How this spec is enforced

The three renderers cannot constrain each other, but they all read the same
`snapshotJson`. That boundary is the single enforcement point.

| Layer | Enforced by | Covers |
| --- | --- | --- |
| Token names and values | `src/validation.ts` resolves tokens to numbers before serialization | §3 |
| Which keys a template accepts | `validateSnapshot` rejects keys not declared for that row type | §4 |
| Numeric bounds | `validateSnapshot` throws on out-of-range values | §3, §4 |
| Per-platform default values | **This document + PR review** | §4, §5 |
| Known divergences | **This document + PR review** | §6 |

A style token is resolved on the JavaScript side. `{ token: '$bodyLg' }` becomes
`{ fontSize: 16, lineHeight: 24, fontWeight: 'medium' }` before it crosses the
bridge. Native renderers never learn the token vocabulary and therefore cannot
drift from it. Raw numeric overrides stay available for pixel-parity work.

## 3. T1 — Design tokens

### 3.1 Color tokens

`NativeListTheme` keys and the application's tokens are aliases. New code should
use the application name; the theme key is retained for compatibility.

| Application token | `NativeListTheme` key | Role |
| --- | --- | --- |
| `text` | `primaryText` | Primary label |
| `textSubdued` | `secondaryText` | Secondary label |
| `textDisabled` | `disabledText` | Disabled / timestamp |
| `textInverse` | `inverseText` | Text on inverted ground |
| `textSuccess` | `positive` | Positive amount |
| `textCritical` | `negative` | Negative amount |
| `textCaution` | `caution` | Warning text |
| `textInfo` | `info` | Informational text, search highlight |
| `bgApp` | `background` | List ground |
| `bg` | `rowBackground` | Row ground |
| `bgActive` | `rowSelectedBackground`, `rowPressedBackground` | Selected / pressed row |
| `bgSubdued` | `subduedBackground` | Card and group ground |
| `bgStrong` | `strongBackground` | Chip / badge ground |
| `bgInverse` | `inverseBackground` | Inverted ground |
| `bgCriticalSubdued` | `criticalBackground` | Failed-state chip |
| `bgCautionSubdued` | `cautionBackground` | Caution chip |
| `borderSubdued` | `separator` | Separator, group border |
| `icon` | `icon` | Icon default |
| `iconSubdued` | `iconSubdued` | Secondary icon |
| `iconActive` | `accent` | Accent — **name mismatch, see §6** |

### 3.2 Typography scale

Sizes below are the application's scale. The "used by" column records which list
slots already sit on that step; slots that do not are listed in §4.

| Token | Size / line height | Weight | Used by |
| --- | --- | --- | --- |
| `$headingXl` | 24 / 32 | semibold | `metricCard.value` (size `large`) |
| `$headingLg` | 20 / 28 | semibold | — |
| `$headingMd` | 18 / 24 | semibold | `sectionHeader` gallery, `metricCard.value` |
| `$headingSm` | 16 / 24 | medium | `identity.title`, `market.title`, `market.price` |
| `$headingXs` | 14 / 20 | semibold | `message.title`, `sectionHeader` default |
| `$bodyLg` | 16 / 24 | regular | `action.title`, `sectionHeader` summary |
| `$bodyMd` | 14 / 20 | regular | `identity.subtitle`, `message.body`, `market.change` |
| `$bodySm` | 12 / 16 | regular | `rail.title`, `market.subtitle`, `identity.badge` |
| `$bodyXs` | 11 / 16 | regular | `metricCard` labels, `market` badges |

Weight names map to the bundled Roobert faces: `regular`, `medium`, `semibold`,
`bold`. No other family is available; `fontFamily` is not part of the style surface.

### 3.3 Spacing, radius, bounds

| Kind | Allowed values | Bound |
| --- | --- | --- |
| Padding / gap | any integer | `0…64` |
| `lineGap` (title to subtitle) | any integer | `0…16` |
| Font size | any integer | `8…48` |
| Line height | any integer | `8…64` |
| Corner radius | any integer | `0…80` |
| Image edge | any integer | `1…160` |

Radius steps in use: `4` (chip), `8` (rail, small visual), `10` (visual `rounded`),
`12` (row, card), `16` (media tile), `20` (wallet sidebar row on iOS only — §6).

## 4. T2 — Template style surface

`style.X` modifies the rendering of the **model field named `X`**, regardless of
which physical view carries it. This matters because the view pool is shared: on
every platform `metricCard` renders its *value* through the title label and its
*title* through the subtitle label, and the status label carries `rail.status`,
`activity.status`, `message.time`, and `metricCard.trend`. Naming style keys after
views would therefore mis-target. `market` already follows this rule with
`style.price` / `style.change`.

Legend: **=** all three platforms agree; **≠** registered divergence, see §6.

### identity

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$headingSm` | 16 medium / 24 | sp(16) medium / dp(24) | 16px / 20px / 600 **≠** |
| `subtitle` | `$bodyMd` | 14 regular / 20 | sp(14) regular / dp(20) | 14px / 20px **=** |
| `tertiary` | `$bodyMd` | 14 regular / 20 | sp(14) / dp(20) | 14px / 20px **=** |
| `badge` | `$bodySm` | 12 medium / 16, pad 8/2, r4 | sp(12) / dp(16), pad 8/2, r4 | 11px / 18px, pad 0 5, r5 **≠** |
| `value` | `$headingSm` | 16 medium | sp(16) medium / dp(24) | 14px / 20px / 500 **≠** |
| `valueSecondary` | `$bodyMd` | 14 regular | sp(14) regular / dp(20) | 14px **=** |
| box | pad 12/8, gap 12 | 12 / -12 / 8 / -8, spacing 12 | dp(12), dp(8) | `padding:8px 12px`, `gap:12px` **=** |
| `image` | 40 | 40×40 (32 for `network`) | 40 (32 for `network`) | 40px (32px for account) **=** |

### identity · presentation `walletSidebar`

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$bodySm` | 12 regular / 16, centered | sp(12) / dp(16), centered | 12px / 16px / 400 **=** |
| `badge` | `$bodyXs` | 11 / 14, pad 6/2, r4 | sp(11) / dp(14), pad 6/2, r4 | 11px / 14px, pad 2 6, r4 **=** |
| `image` | 40 | 40×40, fallback 28 | 40, fallback sp(28) | 40px **=** |
| box | pad 4 | 4 / -4 / 4 / -4 | dp(4) × 4 | `padding:4px 8px` **≠** |
| row radius | 12 | 20 | 12 | 12 **≠** |

### identity · presentation `accountSelector`

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$bodyLg` | 16 regular | sp(16) regular | 16px / 20px / 400 **=** |
| `subtitle` | `$bodyMd` | 14 regular / 20 | sp(14) / dp(20) | 14px / 20px / 400 **=** |
| `image` | 32 | 32×32 | 32 | 32px **=** |
| trailing icon | 38 | 38, margin −7 | dp(38), margin −dp(7) | 38px, margin −7px **=** |

### identity · presentation `networkSelector`

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$headingSm` | 16 medium / 24 | sp(16) / dp(24) | 16px / 24px / 500 **=** |
| `image` | 32 | 32×32, fallback 19/27 semibold | 32, fallback sp(19)/dp(27) | 32px, fallback 19/27/600 **=** |
| checkbox | 20, r4, border 2 | 20×20, r4, border 2 | dp(20), r4f, stroke dp(2) | 20px, r4, border 2px **=** |

### walletGroup

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| member gap | 12 | stack spacing 12 | topMargin dp(12) | `gap:12px` **=** |
| container radius | 12 | 20 | dp(12) | 12px **≠** |
| container border | 1, `borderSubdued` | 1 | dp(1) | 1px **=** |

### rail

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$bodySm` | 12 medium / 16 | sp(12) / dp(16) | 12px / 500 **=** |
| `badge` | `$bodySm` | 12 medium / 16 | sp(12) medium / dp(16) | — |
| `status` | `$bodySm` | 12 regular / 16 | sp(12) | — |
| box | pad 4, gap 6 | 4 / -4, spacing 6 | dp(4), spacing 6 | `padding:4px;gap:6px` **=** |
| `image` | 20 | 20×20 | 20 | 20px **=** |
| row radius | 8 | 8 | 8 | 8px **=** |

### activity

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$headingSm` | lineHeight 24 | dp(24) | 16px / 20px **≠** |
| `description` | `$bodyMd` | lineHeight 20, 2 lines | dp(20), 2 lines | 14px / 20px **=** |
| `primaryAmount` | `$headingSm` | 16 medium / 24, tabular | sp(16) medium / dp(24) | 16px / 20px **≠** |
| `secondaryAmount` | `$bodyMd` | 14 regular / 20, tabular | sp(14) / dp(20) | 14px **=** |
| footer action | `$bodyMd` | 12 medium, r8, pad 4/8 | sp(14) semibold, r8f, pad 8/4 | 12px / 16px / 600 **≠** |

### message

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$headingXs` | 14 semibold / 20, 2 lines | sp(14) semibold / dp(20) | 15px / 20px / 600 **≠** |
| `body` | `$bodyMd` | 14 regular / 20 | sp(14) / dp(20) | 13px / 18px **≠** |
| `time` | `$bodySm` | 12 / 16, `textDisabled` | sp(12) / dp(16), `disabledText` | 11px / 16px **≠** |
| `image` | 28 | 28×28 | 28 | 40px **≠** |
| thumbnail | 64, r6 | 64×64, r6 | dp(64), r6f | 64px, r10 **≠** |
| box | pad 12/16 | 16 / -16 | dp(12), dp(16) | `padding:8px 12px` **≠** |

### dataRow

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `columns` (linear) | `$bodyMd` | 14 medium | sp(16) medium | 16px / 500 **≠** |
| `columns` (table) | `$bodyMd` | 14 medium / 20 | sp(14) medium / dp(20) | 16px / 500 **≠** |
| column secondary | `$bodySm` | 12 regular / 16 | sp(12) / dp(16) | — |
| `index` | `$bodySm` | — | dp(32) slot | 13px, 28px slot **≠** |
| favorite icon | 20 | 20×20 | dp(20) | 24px / 22px glyph **≠** |
| box | pad 12/6 | table 20/10 | table dp(20)/dp(10) | `padding:6px 12px` **=** |

### market

Already fully data-driven through `MarketRowStyle`. The values below are the
defaults applied when `style` is absent.

| Key | Default | All platforms |
| --- | --- | --- |
| `title` | `$headingSm` | 16 / 24 / medium |
| `subtitle` | `$bodyMd` | 14 / 20 / regular |
| `price` | `$headingSm` | 16 / 24 / medium |
| `change` | `$bodyMd` | 14 / 20 / medium, block 80×32 r8 |
| box | pad 20/12, gap 14 | perp 16, gap 8 |
| `image` | 32 (stock 40) | circle |

### mediaTile

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$headingSm` | 16 medium / 24 | sp(16) / dp(24) | 16px / 500 **=** |
| `subtitle` | `$bodySm` | 12 regular / 16 | sp(12) / dp(16) | 12px **=** |
| `badge` | `$bodyMd` | 14 medium, h24, r10 | sp(14), h dp(24), r10f | — |
| image radius | 10 | 10 | dp(10) | 10px **=** |
| tile radius | 16 | 16 | 16 | 16px **=** |
| network badge | 14 | 14×14, r7 | dp(14), circle | 14px, r50% **=** |

### metricCard

`title` is the small label; `value` is the large number. They are rendered through
the subtitle and title views respectively — do not follow the view names.

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$bodyXs` | 11 regular, `textDisabled` | sp(11), `disabledText` | 14px **≠** |
| `value` | `$headingMd` | 18 semibold (24 when `size: large`) | sp(18) / sp(24) semibold | 22px / 28px / 700 **≠** |
| `trend` | `$bodySm` | 12, tone colored | sp(12), tone colored | — |
| metric label | `$bodyXs` | 11 regular / 14 | sp(11) / dp(14) | — |
| metric value | `$bodyMd` | 14 medium / 20 | sp(14) medium / dp(20) | 18px / 600 **≠** |
| card padding | 14 | 14 | dp(14) | 12px **≠** |
| card radius | 12 | 12 | 12 | 12px **=** |
| progress bar | 4, r2 | 4, r2 | dp(4), r dp(2) | 4px, r2 **=** |

### sectionHeader

| Variant | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| default | `$headingXs` | 14 semibold / 20 | sp(14) semibold / dp(20) | 13px / 18px / 700 **≠** |
| `summary` | `$bodyLg` | 16 medium / 24 | sp(16) medium / dp(24) | 16px / 500 **=** |
| `gallery` | `$headingMd` | 18 semibold / 24 | sp(18) semibold / dp(24) | 16px **≠** |
| `history` | `$bodySm` | 12 semibold / 16, tracking 0.8 | sp(12) semibold / dp(16), tracking 0.8 | 12px / 16px **=** |
| table layout | `$bodyXs` | 11 regular / 14 | sp(11) regular / dp(14) | — |
| `value` | `$bodyLg` | 16 / 24 | sp(16) medium / dp(24) | 14px / 20px / 500 **≠** |

### action

| Key | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `title` | `$bodyLg` | 16 / 24 | sp(16) / dp(24) | 15px / 600 **≠** |
| `title` (accountSelector) | `$bodyLg` | 16 medium / regular | medium / regular | 16px / 24px / 400 **=** |
| icon slot | 40 (32 accountSelector) | 40 / 32, glyph 24 | 40 / 32, glyph dp(24) | 40px / 32px **=** |
| box | pad 16 | — | — | `padding:0 16px` **=** |

### system

| Variant | Default | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| `loading` skeleton | — | marks 32 / 80×16 / 60×12 / 80×18 | same | same **=** |
| `loading` spinner | 20 | 20, market 32 | dp(20), market 32 | 20px, market 32px **=** |
| `retry` | `$bodyMd` | 14 medium / 20, r14 | sp(14) / dp(20), r16f | 12px **≠** |
| `noMatch` / `end` | `$bodyMd` | 14 regular / 20 | sp(14) / dp(20) | 14px **=** |
| `warning` | `$bodyMd` | 14 medium + regular / 20 | sp(14) / dp(20) | 14px / 20px **=** |
| `spacer` | — | `height` only | `height` only | `height` only **=** |

### Carrier coverage

`sectionHeader`, `fixedFooter`, and `emptyState` are ordinary rows on all three
platforms — iOS binds the footer through `NativeListCell`, Android through
`NativeListRowView.bind`, Web through `renderElement`, and an empty snapshot's
`emptyState` is spliced into `rows` during parsing. One `style` surface therefore
covers all four carriers. The only exception is the Android sticky header (§6).

## 5. T3 — List chrome

Chrome is not part of a row and needs its own surface (`snapshot.listStyle`,
not yet implemented).

| Item | Data-driven today | iOS | Android | Web |
| --- | --- | --- | --- | --- |
| Content padding | yes (`layout.contentPadding*`) | `contentInset` | `setPadding` | `paddingValues` |
| Item spacing | yes (`layout.itemSpacing`) | flow layout spacing | `ItemSpacingDecoration` | layout gap |
| Separator | partial (`row.separator`) | 1/scale, inset 60/12 | 1px, inset dp(60)/dp(12) | `1px`, class rule |
| Group card radius | no | 20 / 12 | dp(12) | 12px |
| Section index rail | partial (`capabilities.sectionIndex`) | static constants, label 10 | `SECTION_INDEX_*_DP`, raw density | `SECTION_INDEX_*` + CSS |
| Section index preview | no | 48×48 r14, 22 semibold | preview w/h/margin constants | 48px r14, 22px |
| Pull to refresh | partial (`capabilities.pullToRefresh`) | `UIRefreshControl` | `SwipeRefreshLayout` | custom pill, 12px |
| Reorder preview | no | — | — | r12, shadow `0 4px 24px` |
| Reorder count badge | no | 24 h, r12, 12 semibold | dp(24), r dp(12), sp(12) semibold | 24px, r12, 12/22/600 |

The section index rail is three separate constant sets for one control; Android's
`sectionIndexDp()` deliberately never follows the source-scale switch.

## 6. T4 — Known divergences

Registered, **not** fixed by this document. Changing any of these requires a PR
that cites this table and carries device verification.

### 6.1 Row heights

Row height is computed from data by three independent implementations —
`RNCNativeListView.rowHeight()`, `NativeListRowView.applySize()`, and
`estimateWebRowHeight()`. They disagree:

| Template | iOS | Android | Web |
| --- | --- | --- | --- |
| `rail` | 40 | 28 | 40 |
| `activity` + footer actions | 100 | 104 | 100 |
| `dataRow` + secondary text | 60 | 64 | 60 |
| `sectionHeader` `summary` | 68 | 80 | 68 |
| `sectionHeader` value + checkbox | 56 | 40 | 56 |
| `message`, `mediaTile`, composite `metricCard` | computed / 244 / 160+ | 0 (wrap content) | computed / 244 / 161 |

Within iOS, `NativeListCell.bindMessage()` sizes the leading slot at 28 while
`rowHeight()` estimates text width against 40; `bindSystem()` uses 52/120 for a
market retry row where `rowHeight()` uses 44, and 88 for `noMatch` where
`rowHeight()` uses 44.

These values may have been tuned per platform on real devices. They are recorded
here and left alone.

### 6.2 Typography and geometry

| Concept | iOS | Android | Web |
| --- | --- | --- | --- |
| `identity.title` | 16 medium / 24 | sp(16) medium / dp(24) | 16px / **20px** / **600** |
| `identity.badge` | 12 / 16, pad 8/2, r4 | sp(12) / dp(16), pad 8/2, r4 | **11 / 18, pad 0 5, r5** |
| `message.body` | 14 / 20 | sp(14) / dp(20) | **13 / 18** |
| `message` leading | 28 | 28 | **40** |
| `sectionHeader` default | 14 semibold / 20 | sp(14) semibold / dp(20) | **13 / 18 / 700** |
| `action.title` | 16 / 24 | sp(16) / dp(24) | **15 / 600** |
| `metricCard.value` | 18 semibold | sp(18) semibold | **22 / 28 / 700** |
| visual `rounded` radius | `min(10, h/4)` | `min(10, size/4)`; 8 for accountSelector | **10px**; 8 `!important` for account action; 8 for market |
| `walletSidebar` row radius | **20** | 12 | 12 |

### 6.3 Behavioral

- **Android sticky headers are a second renderer.**
  `StickySectionHeaderDecoration` draws the pinned header with `canvas.drawText`,
  carrying its own `Paint`, font size (14 / 12), hard-coded semibold typeface,
  height (36 / 16), horizontal inset (20 / 8), manual letter spacing (× 0.8), and
  baseline math. iOS pins the real cell through layout attributes and Web reuses
  `renderElement`, so both inherit row styling for free; Android will need the
  style applied in two places.
- **Android silently drops stickiness for complex headers.**
  `isSimpleStickySectionHeader()` requires no `value`, no `checkbox`, and
  `variant !== 'summary'`. A section header carrying a total or a select-all box
  does not stick on Android, but does on iOS and Web. Needs a product decision.
- **Source scale is list-wide on Android.**
  `usesSelectorSourceScale` is computed with `items.any { … }`, so one selector row
  switches the metric system (the sub-400dp 0.9 factor) for the entire list.
  `NativeListTableColumnView`, the market skeleton, and the section index never
  follow it.
- **`accent` is not an accent.** The consumer maps the application's `iconActive`
  onto the theme key `accent`. The name should be retired in favour of an alias.

### 6.4 Load-bearing oddities (do not "clean up")

- `.ok-native-list-account-action-row .ok-native-list-visual{border-radius:8px!important}`
  overrides an **inline** radius written by `createVisual`. Removing `!important`
  regresses the account action row to 10px.
- `.ok-native-list-market-change{color:#fff;background:#8d8d8d}` are literals, not
  tokens, because `--nl-inverse-text` defaults to `#fcfcfc` and `--nl-secondary` to
  `#6b7280`. Swapping them changes untinted rendering.

## 7. Isolation rules

The view pool is shared across templates, so a styled row must not be able to
affect any other row. Four rules:

1. **Style types are per template.** `IdentityRowStyle` carries only identity's
   fields; `MetricCardRowStyle` only metricCard's. A key that the row type does not
   declare fails `validateSnapshot` instead of silently hitting a shared view.
2. **Every property gets an explicit default.** Android's `resetViews()` restores
   visibility, gravity, maxLines, layout params, background, and padding — but
   **not** `textSize`, `typeface`, or `lineHeight`. A style pass that writes those
   only when a style is present will leak a font size into the next row that reuses
   the view. The pass must write the template default when the style omits a value.
3. **No list-wide side effects.** Any style-dependent decision is made per row.
   `usesSelectorSourceScale` (§6.3) is the counter-example to avoid.
4. **Bounded and fail-soft.** Bounds are validated in JavaScript; native readers use
   `opt*(key, default)` and fall back to the template default. Bad data degrades to
   "this row is unstyled", never to a crash or to a neighbouring row changing.

## 8. Where style is applied

Each renderer already runs a pass after the per-template binder. The style pass
belongs there, so none of the 13 binders change.

| Platform | Anchor | Note |
| --- | --- | --- |
| iOS | `NativeListCell.bind()`, after the `switch item.type` | Also covers box metrics: the four root constraints, stack spacings, leading size |
| Android | `NativeListRowView.bind()`, **after** `applySize(item)` | `applySize` re-dispatches font size and typeface by row type and would otherwise overwrite the style |
| Web | `renderElement()`, after `createRowBody()` | The selector inline overrides already live here |

The existing market helpers generalize rather than being rewritten:
`applyMarketTextStyle` / `applyMarketButtonStyle` / `marketAttributedText` (iOS),
`applyMarketTextStyle` / `applyMarketTextMetrics` (Android),
`applyMarketTextStyle` / `resolveWebMarketLayoutStyle` (Web).

## 9. Review checklist

For any PR that touches list typography or geometry:

- [ ] Does the change introduce a new bare font size or radius? If so, which token
      in §3 should it be, or does §3 need a new step?
- [ ] Is the value applied identically on all three platforms? If not, is the
      divergence recorded in §6?
- [ ] Does a new style key modify a **model field** name (§4), not a view name?
- [ ] Does the style pass write an explicit default for every property it can set
      (§7 rule 2)?
- [ ] Is any decision derived from the whole row list rather than one row
      (§7 rule 3)?
- [ ] For a section header: does the Android sticky decoration need the same change
      (§6.3)?
