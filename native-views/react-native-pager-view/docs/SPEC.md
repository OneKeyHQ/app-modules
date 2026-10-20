# PagerView native scrolling contract

Existing PagerView and CollapsiblePagerView APIs are documented in README.md.
The sections below define native coordination boundaries and the optional
scroll-edge props.

## iOS 26 top scroll edge overlay

`CollapsiblePagerView` and standalone `NativeScroller` accept the optional
`topScrollEdgeEffect={ScrollEdgeEffect.Hard}` or `ScrollEdgeEffect.Soft` with a
fixed `topOverlay` React child. `ScrollEdgeEffect` is an exported string enum
(`Hard = "hard"`, `Soft = "soft"`); the corresponding string literals remain
valid prop values. Both the style and overlay are
disabled by default. On iOS 26 or newer, a local overlay remains fixed at the
top of the viewport while the owned vertical `UIScrollView` can draw behind it.
The component measures the overlay's Fabric layout height and owns the
corresponding top content inset.
For callers with a known initial height, `topOverlayHeightHint` reserves that
height on the first frame after the overlay is enabled, before Fabric delivers
`onLayout`. The measured height replaces the hint, and disabling the overlay
clears the measurement so a later enablement can use a new hint. The hint does
not enable the effect or reserve an inset without an active local overlay.
This uses the existing native `topOverlayHeight` prop without changing the
native interface. For native smooth local Hard mode, a complete Fabric mount
attaches the overlay slot synchronously before `didMount` returns; incomplete
slots retain the deferred fallback. It binds UIKit's
`UIScrollEdgeElementContainerInteraction` to that exact scroll view and selects
the requested top edge style. Hard has UIKit's dividing line; Soft has its
gradual blurred transition. A pager owns the effect and inset for its current page;
its nested NativeList or NativeScroller must not opt into another top overlay.
Invisible, noninteractive native labels let UIKit recognize the React-drawn
overlay and sticky header as scroll-edge elements. The pager does not add an
auxiliary full-area glass. It clips scrolling assets above
the fixed overlay boundary; during horizontal transitions it clips its owned
visible page hosts below the current sticky boundary so text cannot pass in
front of the fixed controls. Masked assets must not intercept their hit targets.
On iOS 27 or newer, a local smooth Hard pager with a valid shared-header mask
fades a bottommost overlay layer using the resolved opaque pager or ancestor
theme color as the real header collapses, without hard-coded colors or changing
UIKit's Hard style, owner or interactions. During temporary horizontal owner
transitions the layer holds its last opacity until geometry is valid again;
disabling the local overlay, changing style or recycling removes it.

For an iOS 26 pager whose fixed header is rendered by a sibling in the same
React surface, `externalTopOverlayTag` may identify that header's native view.
The pager weakly observes its geometry in the same window, retaining its own
noninteractive native proxy for UIKit recognition without reparenting or
modifying the React header. The measured external header bottom supplies the
top inset and sticky anchor. A local `topOverlay` takes precedence. The tag
alone never enables the effect: either `topScrollEdgeEffect` enum value controls the
interaction, while valid external geometry can reserve the top inset before
the effect is enabled. Before the tag and height are ready, the caller keeps
its existing layout; an unresolved native tag has zero external height. An
outer PagerView transition
releases the current edge interaction before moving its shared headers and
applying visible-page masks; completion or cancellation reattaches the actual
settled owner. An inactive inner pager retains the external header inset while
its edge interaction is released. Without an outer PagerView, a standalone
external-overlay pager owns its edge interaction directly; page-activity and
transition gates apply only while an outer PagerView is registered.

`topOverlayColorScheme` is optional (`"light" | "dark"`) and coordinates the
fixed overlay's effective appearance with React theme commits. It does not
change the requested style, scroll owner, content inset or page identity.
When a smooth shared header reaches the overlay boundary, the existing sticky
React and native tab views move together into a view pinned to the active
scroll view. Their identities, scroll-view gesture ancestry and hit targets
remain intact; the collapsible header stays in the scrolling header host.
Without a local or external overlay, either scroll-edge style may protect
only the genuinely pinned sticky header; it must not consume a page as a
synthetic overlay slot or change the collapsible banner distance.
Changing pages, replacing the active list or recycling returns those views to
the shared host before their old scroll-edge interaction is released.
The measured overlay height is excluded from the collapsible header distance:
`header` remains collapsible and `stickyHeader` pins immediately below the
overlay. Selection, cancellation, reuse and disposal rebind or release the
interaction and restore any prior scroll-edge style. Native scroll/refresh
coordination continues on the main thread without per-frame JavaScript work.
Refresh retains the pager-owned `overlay + header + sticky` top inset but offsets
the indicator by `header + sticky`, so its spinner begins below the fixed overlay.

On iOS before 26, the external tag is not forwarded to native. On iOS before
26, Android and Web the new props have no scroll-edge effect.
The caller retains its existing layout on those platforms. Android generated
view managers accept the optional props as no-op setters without changing
their scroll or gesture behavior. Without a fixed overlay or pinned sticky
header, the current scroll/inset behavior is unchanged. A valid external tag
can reserve its measured geometry even while `topScrollEdgeEffect` is off.
Acceptance requires real iOS 26 scrolling, partial collapse, short/empty page,
refresh, page transition/cancellation, dynamic tabs and light/dark checks;
source and build checks alone do not establish visual correctness.

`CollapsiblePagerView` paints a cover at each owned
fixed-overlay or pinned-sticky boundary with one physical pixel of the nearest
opaque ancestor background color (system background if none is available).
These native layers do not receive touches, change insets, or clip glass shadows.
They are removed for Soft, disabled/detached effects, and recycling. This is a
visual workaround using public layer APIs, not a UIKit separator setting. While
the sticky header is pinned, only the top cover is hidden; it reappears on unpin,
and the sticky cover and both Hard interactions remain active. Standalone
NativeScroller and NativeList retain UIKit's unmodified Hard appearance.

Style selection crosses Fabric as the enum `none | hard | soft`, defaulting to
`none`, rather than a hard-effect boolean. Switching Hard ↔ Soft on an attached
owner updates UIKit without replacing the overlay or changing its inset,
logical scroll offset, or saved original style. Clearing the prop, detaching,
or recycling releases the interaction and restores that original style.
This bridge contract requires a matching native rebuild. Soft selection is
implemented in the public wrappers and native hosts. Focused JS tests cover
style forwarding, overlay retention and disabling; UIKit tests cover native
style switching and restoring the previous style. Rendered iOS acceptance must
be reported separately from type, codegen and build checks.

## Smooth shared-header and per-page body memory

With nativeSmoothHeaderScrollEnabled, the header collapse C is shared across all
pages and the available collapse distance is D. Each page separately remembers
its sticky-body reading distance B (unvisited pages default to zero). Outside
pull overscroll, the rendered whole-page distance is Q = C + effectiveB.
B denotes the saved reading position. For an owned vertical LinearLayoutManager
RecyclerView, restoration uses the first visible adapter position and its
decorated top relative to padding; scrollbar offset estimates MUST NOT decide
completion of that anchor restoration.
When C = D, selection restores the destination's saved B. When C < D, effectiveB
is zero while the previous saved B remains suspended. Horizontal selection by
tap, swipe or an existing programmatic command MUST preserve C throughout.

Alignment, prefetch, replacement, inset changes and pending restoration MUST NOT
commit new B. Actual consumed vertical scrolling takes over the visible body
position; reaching full collapse during that gesture MUST NOT resurrect the old
suspended B. Selecting a page anew at full collapse may restore its saved B.
Valid explicit native scroll commands, including same-offset commands, cancel
only their current owned restoration and take over body memory. Invalid commands
and stale owners do not affect another page. Existing command coordinate systems
are unchanged; in particular NativeScroller iOS uses physical offsets whereas
NativeList offset commands exclude the content-start inset.

Transitions use immutable destination memory and the source's actual displayed
position. Cancellation restores that source presentation, even when a cached B
was temporarily unreachable. Loading or zero geometry preserves pending intent;
a genuinely short page clamps its display without silently replacing saved B.
Cancelling a horizontal gesture invalidates queued callbacks, but preserves
unreachable reading intent until real vertical consumption or a valid command
takes over; later usable geometry may resume it.
Late callbacks cannot revive cancelled restoration or erase a newer request.
Observers and commands must match page key, view identity and generation.
Removed or recycled owners cannot apply callbacks to another page; recycling
resets owned memory.
The Web backend shares C and stores per-page B separately from absolute DOM
scrollTop. Selection aligns to C while expanded and C + B while fully collapsed;
programmatic alignment and geometry-only scrollend events MUST NOT erase the
suspended reading position. Real vertical input or a valid List command takes
over the current display. PageTop/StickyTop clear only the current B.

Legacy mode and pagers without a collapsible header retain their existing
behavior. Vertical scrolling, refresh, snapping and repeated-current-tab behavior
are unchanged. This state belongs to native UI instances, with no new React prop
or per-frame JavaScript synchronization.

## Current-page top commands

`scrollToPageTop(options?: Readonly<{ animated?: boolean }>)` restores the current
page to C = 0 and B = 0. `scrollToStickyTop(options?: Readonly<{ animated?: boolean }>)`
restores it to C = D and B = 0, including from a partially expanded header.
Animation defaults to true. Neither command clears another page's memory.
Both commands require a stable, attached current owner; horizontal transitions
or missing owners reject them rather than queueing a command for another page.
Acceptance cancels older owned list commands and pending restoration before the
new target is established. Same-target commands also take over old reading
intent. Real vertical consumption, a newer valid command, or owner detachment
releases the target. Existing NativeList and NativeScroller public coordinates
are unchanged. The same page-top/sticky-top destinations are available without smooth-header mode.

## Independent NativeScroller migration

Status: implemented in source. Both native builds pass. Android focused emulator
cases below and the iOS same-source UIKit harness pass; the full acceptance
matrix remains incomplete. Device evidence is scoped below.
NativeScroller keeps React children and their Fabric/Yoga layout, but owns its
vertical native scrolling implementation: AndroidX NestedScrollView on Android
and UIScrollView on iOS. It MUST NOT inherit ReactScrollView/RCTScrollView or
require rebuilding ReactAndroid. Web retains its existing implementation.

The initial compatibility contract covers existing Home callers: viewport style,
contentContainerStyle including padding and flexGrow, dynamically mounted React
children, vertical scroll enablement/indicator, stable pagerScrollKey, scroll
throttling, onScroll/onScrollBeginDrag/onScrollEndDrag and momentum callbacks,
content-size notification, and scrollTo/scrollToEnd commands. Standard RN
RefreshControl JSX remains a supported input; its props and onRefresh callback
are adapted to owned native refresh controls rather than mounting an RN-private
scroll-view implementation. Supported properties and intentional platform limits
MUST be explicit in the exported types. Unknown RN ScrollView features MUST NOT
be silently advertised as supported. No business data or Home-specific geometry
belongs in the native component.

This is a vertical, non-virtualized subset, not a drop-in implementation of every
ScrollView prop. Horizontal scrolling, sticky rows, snapping, zoom and automatic
keyboard insets are outside this contract. iOS owns bounces and decelerationRate;
Android retains AndroidX fling physics without a public friction override.
Keyboard persist-tap behavior is coordinated by the JS responder wrapper using
RN 0.86's TextInput registry, while native owns drag-to-dismiss. The RN-private
registry dependency and keyboard behavior need validation on RN upgrades.
Arbitrary custom refresh components are not supported.

Native views, gestures, insets and refresh controllers belong to the UI thread.
React content layout remains Fabric-owned. Layout-size updates may cross the
bridge when dimensions change; native scrolling and header coordination MUST NOT
depend on per-frame JS updates. This is a main/UI component, with no background
runtime, persistent storage or cross-runtime heap state.

The wrapper mounts one non-collapsible React content View. Native MUST preserve
its Fabric-assigned frame and transform. A shared Fabric ShadowNode reads native
state describing local content displacement and separate ancestor displacement;
it MUST apply the ancestor displacement outside the content transform. UIKit
updates this state from contentOffset; Android updates it from content origin,
scroll position and pager consumption. Equal values are deduplicated, and a
replacement state wrapper is synchronized with the current native position.
These native state updates do not require per-frame JavaScript callbacks.
Fabric hit testing and descendant measureInWindow MUST include the displacement,
so measurement follows visible content after scroll, inset changes and page
return. Geometry acceptance MUST compare a measured React child with its visible
position, including the last child after scrollToEnd.

The automatic flexGrow minimum MUST use the native usable content viewport,
not the raw host height. An internal onContentViewportChange({height}) event
reports logical units only when static geometry changes; it is not a public
NativeScroller prop. Android reports viewport height minus caller/pager top
reservation and caller bottom inset. Its pager viewport already includes the
extra collapsible-header height. iOS reports bounds height minus caller top and
bottom insets and the sum of pager-owned sticky reservations. Both describe the
React area available with the shared header collapsed. The height MUST NOT
depend on contentSize, current header offset, refresh temporary insets or iOS
synthetic bottom fill, which would create a layout feedback loop. Owners MUST
remove their reservation on release. The current value MUST be delivered when
the view first becomes able to dispatch its event and after reattachment. An
event queued before its initial emitter is ready MUST be delivered when that
emitter becomes available. Replacing the emitter on the same attached view does
not require another event when the usable height is unchanged.

Before the first native viewport event, the wrapper contributes no positive
automatic minimum. It combines valid native height with an explicit numeric
caller minHeight using the greater value; nonnumeric explicit minima remain
untouched. Content padding remains part of Yoga layout. Long-to-short replacement
MUST clamp the local offset without leaving a pager-inset-sized artificial scroll
range or hiding the first React child. Tests and device acceptance MUST cover
long-to-empty/restore, page return, header/sticky resizing, refresh completion
without minimum-height changes, and an intentionally larger caller minimum.

Android MUST dispatch touch and non-touch nested scrolling continuously across
the content/header boundary. Content returns to its start before downward travel
expands the header; upward travel collapses the header before scrolling content.
Consumed and remaining distance MUST be accounted for once. Only touch pull can
trigger refresh; inertial movement MUST NOT trigger refresh. iOS MUST preserve a
single UIScrollView trajectory through the inset/header range. Existing Pager
refresh foreground placement and shared-header press cancellation remain in
force. NativeList and unrelated RN ScrollViews retain their implementations.

New touch, page deactivation, replacement of the mounted React content root and
detach MUST stop stale inertia. Every started drag/momentum sequence has one
terminal notification. An animated scrollTo/scrollToEnd command with a different
clamped target emits one momentum begin/end pair, including AndroidX's immediate
execution branch for closely spaced commands. A target equal to the current
position emits no new pair. A new command ends any previous active sequence
before starting its own, and non-animated movement does not start momentum;
recycled views MUST NOT emit into an old owner. Content shrink clamps unreachable
offsets without a blank tail; resize/inset changes MUST NOT accumulate offsets.
On Android, a pager offset restoration that is clamped because React content has
not reached native yet is reapplied on content layout until reached; a new touch,
drag, scroll command, content-root replacement or pager release cancels it.
Stopping an idle scroller MUST NOT start or end a non-touch nested scroll.
Refresh is controlled by the caller and emits once per qualifying gesture.
Android applies a refreshing value when it changes (or at first layout); later
layout passes do not reassert it over a gesture-started indicator.

Acceptance requires both native builds, focused contract checks and iOS/Android
recordings: expanded/collapsed Home Spot/Perps/DeFi, short/empty and dynamically
resized content, fast return through the header boundary, new-touch interruption,
adjacent/distant page changes, horizontal gestures versus row presses, pull from
header/content, refresh completion, retained-page return, imperative
animation/no-op/interruption, and React child measureInWindow versus visible
geometry. History/NFT and Market/Discover are regression controls. Source/type/build success alone is not
device acceptance. Implementation findings and remaining limits are recorded
after validation, before this section is promoted to runtime verified.

Android acceptance checkpoint (2026-09-28): the official local-shell/local-vendor
DevSession build passed, with custom Fabric descriptor registration verified.
On the dedicated emulator, native field inspection confirmed NativeScrollerView
and its AndroidX viewport. Home Spot/Perps/DeFi return gestures crossed the
content/header boundary. In the final short-fill binary, Perps content offset
1389 reached zero and the same non-touch momentum continued header offset
755 -> 648 -> 441 -> 217 -> 56 before natural exhaustion. This proves continuity,
not that every gesture must fully expand the header.

The first valid binary exposed an artificial 284 px short-content range. After the
usable-viewport correction, Gallery long-to-empty replacement had local offset 0
with short text visible; restoring rows and scrollToEnd reached the final child.
measureInWindow y746.7/h72.0 matched native visible bounds y1960/h189px at
pixel density2.625. Short-content refresh advanced the fixture counter once and
controlled completion removed the indicator. Home three-page refresh visibility,
History/NFT vertical scrolling and Market/Browser horizontal controls passed on
the preceding valid binary; these control implementations were unchanged by the
short-fill correction. Evidence is recorded in the app task's ignored
`ignore/independent-native-scroller/android-acceptance.md`, alongside screenshots,
recording chunks and native field samples.

This checkpoint does not cover a physical handset, keyboard/multi-touch/accessory
interactions, exhaustive event counts and command interruption, explicit larger
caller minHeight, header/sticky resizing or the full refresh matrix. Existing
development warnings were observed. Android evidence does not establish iOS
acceptance, and source/build success is not a substitute for its runtime cases.

iOS acceptance checkpoint (2026-09-28): the final official local-shell/local-vendor
build passed and launched on the dedicated external-image simulator. Nine
same-source UIKit harness methods and 36 assertions pass, including negative-pull
preservation during shrink, caller inset ownership, static usable height and
refresh/recycle interruption. This is UIKit execution, not a formal XCTest run.

On the final binary, Home Spot/Perps/DeFi horizontal transitions and top-positioned
refresh feedback passed. Gallery scrollToEnd reached the last React row;
measureInWindow y786/h72 matched its native frame. Long-to-empty replacement,
restoring rows, controlled refresh counter 0 -> 1 with indicator dismissal,
normal row press and retained-page return passed. History/NFT scrolling and
media, Discover Browser -> DeFi and Market vertical scrolling plus Trending ->
Stocks horizontal transition passed without unintended detail navigation.
The final device is left on expanded Home Spot. Evidence is in the app task's
`ignore/independent-native-scroller/ios-acceptance.md` and associated recordings.

These iOS gestures use public XCTest coordinate drags. Earlier private synthesized
pan attempts had inconsistent timing and are excluded; their cause is not proven.
Gallery's floating application tab bar overlaps part of the last row, so matching
coordinates do not establish an unobscured bottom viewport. Existing development
warnings were observed. Keyboard, multi-touch, physical-device performance and
the full lifecycle/interaction matrix remain unverified. These are local source
builds; the currently installed npm version does not yet contain this migration.

## iOS vertical-scroll press cancellation

Status: implemented in source. A source-identical `node_modules` build was
runtime verified on a dedicated iOS 26.5 HomePager simulator on 2026-09-30:
a slow drag from the Show more button did not activate it, a fling from the
same button scrolled the list, and a stationary tap activated it. The Pager
XCTest suite passed 47/47 tests on a separate iOS 26.5 simulator with its data
on the external drive on 2026-09-30.

In `CollapsiblePagerView` smooth-header mode, a single-finger
vertical drag beginning on React page content MUST cancel that Surface's active
React press before finger-up once vertical displacement reaches 10 points and
exceeds horizontal displacement. Cancellation MUST leave the native vertical
scroller's pan and momentum intact. Stationary taps, movement below the threshold,
horizontal drags, and multi-touch sequences MUST retain their existing behavior.
The iOS content press bridge owns cancellation on the UI thread; the pager's
existing horizontal-drag cancellation remains independent. The bridge MUST clear
per-touch state on end, cancellation, and reuse. Android and Web behavior is
unchanged. Acceptance requires a real iOS button-origin drag that does not fire
the button, a button-origin fling that scrolls, an ordinary tap that fires, and
focused native touch-delivery coverage.

## Smooth-header touch direction and press retention


Status: implemented in module source. Equivalent native gesture behavior was
independently verified in the Home consumer's recorded round3 simulator cases.
The module transplant preserves those decisions and excludes application-specific
acceptance probes. Package compilation and packaging are checked separately;
consumer evidence does not establish acceptance of a subsequently installed
release. Physical-device feel and semantic accessibility activation remain
unverified. No public API is added.

Existing smooth-header callers MUST receive this behavior automatically, with no
additional caller options.

On iOS and Android, smooth-header mode MUST distinguish pending intent from a
confirmed vertical or horizontal drag. A small horizontal start followed by
vertical movement MUST be allowed to become a vertical scroll while intent is
pending. A confirmed vertical drag MUST retain vertical ownership until end or
cancellation, even if later movement becomes horizontal. This applies to page
content, the shared header, and touches beginning on an inner horizontal banner.
Normal taps MUST remain functional. Press-retention behavior in application
controls belongs to the consuming responder implementation; the Pager package
does not contain or publish Tamagui helpers. The native application tab bar has
a separate contract in react-native-bottom-tabs/docs/SPEC.md.

The current native implementation confirms horizontal intent after at least
32 points on iOS or 32 dp on Android, with horizontal displacement exceeding
vertical displacement. Vertical intent uses the native vertical scroll threshold
(Android touch slop; iOS guard 10 points) and vertical-dominant displacement.
There is no timed wait and no percentage-of-page-width threshold. After horizontal
intent is confirmed, page content may retain horizontal ownership: the contract
does not require transferring an already established page swipe to a vertical
scroller. Header forwarding can recover to the vertical list when vertical
movement becomes dominant. An inner horizontal viewport that can consume a
confirmed horizontal drag MUST have priority over the Home page pager.

On iOS, when a nested CollapsiblePagerView has an actual outer PagerView
ancestor, its existing outer-pager protector also covers touches that begin in
the inner page body. It may protect only a horizontal direction with another
inner page available. At the first or last inner page, an outward drag remains
available to the outer pager; vertical drags and ordinary taps are unchanged.
This body protection must not activate merely because nested scrolling is
enabled, since Home also enables nested scrolling without an outer PagerView.
The direction guard and its 32-point threshold remain unchanged. Android keeps
its existing nested gesture implementation.

This intent confirmation MUST NOT add a second lazy-mount progress threshold.
Once native horizontal dragging begins, the first nonzero page progress MUST
prepare the target page on both platforms, as specified below.

Acceptance MUST cover an initial small horizontal movement followed by vertical
scrolling from ordinary content, header buttons, Prime/support controls and inner
banners; vertical-first gestures that later turn horizontal; stationary taps;
normal page swipes; inner banner swipes; and iOS fixed-control drag cancellation.
Native logger traces and before/after frames MUST distinguish scrolling, unintended
page changes and unintended control activation. Simulator acceptance establishes only the recorded touch sequences; it does not
establish physical-device feel or untested multi-touch combinations.

On Smooth Home, Android NativeScrollerViewport MUST use the same pending /
vertical / horizontal direction decision on interception and on its own touch
path. It MUST deliver the original DOWN to NestedScrollView, defer pending and
horizontal MOVE scrolling, and retain confirmed vertical ownership until the
sequence ends. Header-forwarded DOWN MUST initialize this decision independently
of the refresh boundary. Dispatch MUST still observe child-owned movement; only
a confirmed vertical Smooth Home gesture may release the viewport's child
interception prohibition. Pending and horizontal movement MUST preserve it.

In smooth-header mode, Android's NativeScroller refresh boundary MUST retain a
confirmed horizontal or vertical direction until the touch sequence ends or is
cancelled. It MUST classify MOVE direction only while neither direction is confirmed,
and horizontal confirmation MUST include the 32 dp boundary, matching the
viewport. Later vertical movement after confirmed horizontal intent MUST NOT
start a refresh gesture. Non-smooth refresh direction behavior remains unchanged.

iOS nested horizontal ownership MUST include enabled nonpaging horizontal-only
ScrollViews, with content wider than bounds and no vertical content overflow.
Both pan holding and failure dependencies MUST use the same eligibility. A
failure dependency on the Pager guard MUST apply only to the horizontal pan,
both on the root Pager ScrollView and on eligible nested horizontal ScrollViews;
independent non-pan recognizers, including stationary native long presses, MUST
remain able to deliver their target actions before finger-up while Pager intent
is pending. A
second touch in the pending Pager guard fails recognition; an already active
Pager guard cancels and restores each held pan's original minimum touch count.

In iOS smooth-header mode, a confirmed horizontal guard displacement of at
least 32 points, exceeding vertical displacement, MUST cancel the active React
press on its one eligible content Surface before finger-up even when the pager
pan has not begun. The content observer stays non-recognizing and resets only
that Surface's already Began/Changed touch handler. Shared-header native controls,
stationary taps, pending small-horizontal-to-vertical gestures, and multi-touch
sequences retain their existing ownership. Cancellation is emitted at most once
for the tracked touch; end, cancellation, and reuse clear that touch's state.
The attached UIKit regression intentionally constrains the real pager pan to two
required touches to prove the no-pan path; it is distinct from natural Home OS
input, coalesced-event delivery, and physical-device acceptance.

## Native tab-press animation default

Behavior change: previously an omitted `nativeTabPressAnimationEnabled` animated
every native tab press. Callers that relied on that default MUST pass `true`.

On iOS and Android, an omitted `nativeTabPressAnimationEnabled` MUST animate a
press to an adjacent page and jump directly to a non-adjacent page. Adjacency is
the absolute difference between the target index and the current page. While a
tab command that native has already received is in flight, its target is the
current page; otherwise it is the selected index. A request superseded before it
reached native is ignored. Explicit `true` continues to animate all
tab presses, and explicit `false` continues to jump for all tab presses.
Page retention, selection callbacks, swipe gestures, imperative `setPage` /
`setPageWithoutAnimation` commands and Web behavior retain their contracts.
Callers rendering their own JS tab bar choose the matching imperative command.

Status: implemented; focused native-wrapper tests cover both directions,
explicit overrides and in-flight/superseded command targets. Home applies this policy to its JS tab bar, with iOS and
Android simulator recordings verifying distant jumps and adjacent transitions.

## Android refresh foreground coordination

Status: implemented; Android Home simulator placement verified on 2026-09-28.
The previous below-tab placement is not accepted: the Home header can place the
indicator in the middle of asset rows. In smooth-header mode the outermost
coordinator MUST draw the active primary scroller's native refresh indicator
above the shared headers at the coordinator's top. SwipeRefreshLayout retains
trigger, animation, native visibility and refresh-state ownership. The indicator
MUST NOT be reparented, and the coordinator MUST NOT reset progress offsets.

NativeScroller and NativeList use the same foreground drawing boundary. A native
View tag, `onekey_native_scroll_coordinator_refresh_inset`, reports only the
refresh controller's actual coordinator-applied inset. An absent tag means zero.
The outer drawer subtracts that inset once, preserving caller geometry and the
native indicator matrix. This is native drawing metadata, with no JS API or row
semantics. Nested owners MUST NOT draw duplicate indicators.

The coordinator suppresses only the indicator's original drawing and restores
its prior alpha when ownership ends, the target changes, or the pager detaches.
An inactive page MUST NOT appear in the foreground. The foreground must clear
when the indicator disappears. Native animation transformations MUST NOT be
advanced a second time during drawing. Focused policy checks and device evidence
must cover alpha restoration, nested ownership, page changes and dismissal.
All five Home tabs require top-positioned feedback and stable header position
through pull, release and refresh completion. Source/compile success is not
runtime acceptance. Existing iOS/Web behavior is unchanged.

The five Home tabs were recorded from both header and content origins: the
foreground arc appears at the coordinator top and dismisses normally. The fast
release reversal that previously changed header offset from 0 to 767 px now
consumes the pending pull fling and keeps the header expanded. These recordings
verify visual feedback, not downstream network completion or new callback counts.
Production-helper tests cover alpha restoration and combined native insets;
real nested-owner replacement, dynamic caller geometry and detach remain separate
acceptance work. Direct foreground View drawing preserves the native circle and
arc but does not reproduce the original RenderNode elevation shadow.

## Android refresh release settlement

Status: implemented; the captured release reversal passes a device A/B check.
A downward touch gesture that leaves unconsumed travel for the active
SwipeRefreshLayout is a pending native refresh pull. While that same gesture's
indicator is visible, the shared header is expanded, and primary content remains
at the top, the coordinator MUST consume nested pre-fling on release. A small
upward reversal before lifting must settle the pull instead of starting a
content fling and abruptly collapsing the header. SwipeRefreshLayout still owns
whether the release triggers or cancels refresh.

The pending target is weak, scoped to the current touch nested-scroll gesture,
and cleared at its stop or replacement. An old indicator's exit animation alone
is not a pending pull. Ordinary upward swipes, content scrolled away from the
top, inactive pages and non-smooth mode retain existing fling behavior. A pull
whose indicator is already hidden no longer qualifies. AndroidX can leave the
indicator visible when reverse travel exactly exhausts the pull; that same
gesture may still be settled instead of flung. Required regression cases include immediate reversal release,
a stationary release, below-threshold cancellation, ordinary upward fling and
header expansion before refresh. No JS timeout or data-refresh suppression is
introduced.

## Android downward scroll ownership

Status: implemented; Android Home History runtime verified on 2026-09-28.
Downward scrolling MUST move the active content back to its top before expanding
the shared header. A refresh wrapper forwarding nested scrolling MUST report its
child's ability to scroll upward; the wrapper's own scroll range is not evidence
that the content is at the top. SwipeRefreshLayout's public canChildScrollUp()
contract, including caller overrides, supplies this check. Other nested-scroll
targets retain their own canScrollVertically(-1) check.

The correction changes only the downward pre-scroll eligibility check. Existing
delta consumption, content-observer alignment, refresh ownership and release
settlement remain unchanged. Runtime comparison captured premature expansion
with content offset 1960 px before the fix. Afterward the header stayed collapsed
while content returned to zero, and expanded only at the top, without an observer
forced collapse. Debugger state traces prove ownership, not frame timing;
recordings with the debugger detached cover History rapid direction reversal
and top refresh.
Spot's scroll-then-refresh release-reversal sequence still jumps in both the
baseline and corrected builds; that existing NativeScroller case remains open.

## Superseded Android indicator placement

The alpha.257 approach translated the RN indicator below the shared tabs.
Its ten Home checks established visibility and event dispatch only. Subsequent
2026-09-28 feedback showed the indicator over asset rows; those checks do not
establish correct placement. The foreground coordination contract above replaces
that translation policy. NativeList keeps its controller offset internally and
publishes only its actual native inset for foreground drawing.

## iOS refresh indicator placement

Status: implemented; iOS Home pull-gesture visibility verified on 2026-09-27.
Spot, Perps and DeFi (RN NativeScroller), plus NFT and History (NativeList),
each pass header-origin and content-origin pulls: ten recordings show a visible
indicator above the shared header, one refresh event and normal disappearance.
This verifies UI feedback and event dispatch, not downstream request completion.
The Pager UIKit suite passes 14 tests, including six refresh-control cases
(native-tests run 36318243869). Rapid nested gestures and iPad geometry remain
separate acceptance work.
In smooth-header mode, the pager shifts the refresh control's sublayers upward
by its header-plus-sticky height. It does not change the refresh control frame,
bounds, scroll offset, refresh state or JavaScript callback. RN's caller-owned
`progressViewOffset` remains untouched, including dynamic updates whose bounds
origin could otherwise collide with a coordinator-applied value.

One internal state per UIRefreshControl combines weak pager-owner contributions.
It preserves the original sublayer transform and clipping setting. Repeated
attachment and header resizing replace an owner's contribution. Layer clipping
is disabled while coordinating so it cannot hide the translated indicator.
A control replacement, scroll-view release, pager recycling or destruction
removes only that owner's contribution and restores the baseline when none remain.
Controls installed after the scroller is attached are also coordinated. Native
UICollectionView controls and RN ScrollView controls use this same UIKit boundary.

Native layer observations reapply the contribution after an external sublayer
transform change, without observing or interpreting UIKit/RN bounds writes.
There is no per-frame JavaScript work. Acceptance must cover real UIKit pulling
and refresh completion, caller offset changes, late installation/replacement,
retained-page switching, clipping and nested-owner cleanup. Programmatic UIKit
unit tests do not substitute for real pull-gesture visibility acceptance.

When a smooth shared header is attached to the active iOS scroll view, UIKit's
temporary refresh top inset MUST keep the header and content aligned. Both are
children of that scroll view and move together with its content offset; the
pager MUST NOT apply a separate refresh translation to the header. The scroll
view's inset and offset, the refresh indicator, and refresh callback ownership
remain unchanged. Removing the refresh inset or control preserves this alignment
even without a scroll-offset event. Caller inset changes update the pager
baseline. The sticky tab row MUST pin against the baseline top inset; the
refresh control's temporary inset MUST NOT move it below the content, including
rapid direction reversals while the spinner is active. Detaching the header
restores its normal transform. This applies to RN NativeScroller and NativeList
pages; non-smooth and detached headers keep
their existing positioning. The 2026-09-30 Home Spot simulator checkpoint covers
shared header/content alignment and inset removal. A separate source-identical
local package build verified the sticky row during active-refresh vertical
reversals; published-package, other Home pages and physical-device acceptance
remain open.

## iOS shared-header hit testing

Smooth-header hit forwarding is restricted to the pager's own interactive
viewport. A collapsed header clipped above that viewport MUST NOT intercept
touches intended for a sibling toolbar. Hidden, disabled and effectively
transparent pagers do not forward touches. Fabric `pointerEvents="none"` and
`"box-only"` retain their superclass behavior; `"auto"` and `"box-none"` may
forward to visible shared-header children inside the pager.

The shared header's internal hit testing still supports sticky children that
have moved outside the header host's original bounds while remaining inside the
pager. Focused UIKit tests cover sibling-toolbar hit delivery, pinned-child
delivery, pointer-event modes and interaction flags. Device acceptance includes
toolbar taps with expanded and collapsed headers on retained pages.

## iOS cached-offset restoration after content shrink

When restoring a retained page's cached vertical offset, the pager limits only
the upper bound to the current content height, viewport and applied insets. A
cache captured before rows were removed MUST NOT restore an offset below the new
content bottom, including when removal occurs during a page transition. Valid
deep offsets and pull-overscroll behavior are preserved; smooth-header selection
follows the shared-header and per-page body-memory contract above.
This check runs only on explicit restoration, not on scroll frames; UIKit pull
overscroll and refresh settlement retain their existing ownership. An unmeasured
zero-height viewport defers this upper-bound limit to a later restoration.

The focused policy test covers shrink after the list has already corrected its
offset, valid deep offsets, negative pull offsets and an unmeasured viewport.
Runtime acceptance requires clearing a retained page during a page transition,
then returning without an extra drag to expose its header/empty/footer slots.


## Ancestor consumed-scroll contributions

Status: source implementation; real nested-pager runtime verification pending.
Android collapsible pagers consume header travel before the primary RecyclerView
receives content-scroll deltas. A leaf threshold observer needs both distances.
The coordinator publishes a non-negative physical-pixel contribution under its
own weak owner identity on the active primary RecyclerView. Nested coordinators
contribute independently; summing their values gives ancestor-consumed travel.

The native View tag named `onekey_native_scroll_coordinator_consumed_offsets`
stores a WeakHashMap with owner-view keys and integer pixel values. The optional
`onekey_native_scroll_coordinator_offset_listener` tag holds a Runnable owned by
the leaf. The publisher updates only its own entry and invokes the listener only
when that entry changes. An absent map/listener means zero/no observer.
This protocol depends only on Android View/resources and Java collections; the
pager MUST NOT depend on NativeList or infer Home/business semantics.

On switching the primary child, restoring insets, or detaching, the coordinator
removes only its own contribution. It MUST NOT retain an old RecyclerView or
another coordinator. Zero contributions may remain while the owner is attached;
weak keys prevent abandoned owners from becoming resource roots. The leaf owns
listener registration and cleanup. All operations occur on the UI thread; there
are no JavaScript scroll-frame callbacks in this protocol.

A new child receives current header travel before threshold evaluation. Native
list offsets remain local to the leaf, while consumers of this protocol add all
ancestor contributions exactly once. Programmatic scrolling/header restoration
must publish the resulting header offset through the same path.
Every NativeScroller viewport the coordinator owns, including adjacent pages it
translates, receives each header offset change, not only the observed page.
Content-offset observers are removed from the observer instance they were
registered on, and a removed page's scroller stops writing offsets immediately.

## Acceptance

The focused JVM helper test covers independent ancestor contributions and removal.
Required Android runtime cases are one/nested collapsible pagers, header-only
scrolling, reversal to expanded top, switching pages, native child replacement,
and detach/remount without stale offsets. iOS uses its existing normalized
UIScrollView offset and requires no equivalent tag protocol. Source/helper
checks do not establish the Android runtime acceptance above.


## Android Fabric NativeScroller content insets

A ReactScrollView's content root is laid out by Fabric. Native `setPadding(top)`
alone MUST NOT be used to reserve a collapsible header: ReactScrollView does not
perform the platform ScrollView child layout, so that leaves rows under the
header. For the supported RN 0.86 runtime, the coordinator uses
`setScrollAwayPaddingEnabledUnstable` to offset the content root and synchronize
Fabric content-origin/culling state. The equivalent inset is added to the native
scroll range at the bottom; it is not added a second time at the top. Ordinary
Android ScrollView and RecyclerView retain their existing inset paths.

Each coordinator replaces only its previous header-plus-sticky contribution.
Measured-height changes do not accumulate insets; detach restores the remaining
ScrollAway state and caller padding, including another native owner's inset.
Content-root replacement reapplies the native offset. No Home layout values,
new React props, or per-frame JS updates are introduced; iOS/Web are unchanged.

The focused JVM policy regression covers an existing inset, repeated application,
measured-header resize, nested-owner detach, and Fabric state reset. The API call
is compile-checked against the installed React Android 0.86.2 artifact. Runtime
acceptance requires first content below the expanded sticky tabs, collapse and
expand without overlap, retained-page switching, header height changes, and
reaching the actual final content without an extra header-sized blank footer.

RN's ScrollAway setter (including native state replay when Fabric culling flags
are enabled) resets physical padding to `(0, 0, 0, scrollAwayTop + scrollAwayBottom)`.
While owning an inset, the coordinator recognizes that exact native reset and
retains saved caller padding; other physical-padding updates remain observable.
The focused policy regression includes nonzero caller left/right/bottom padding,
nonzero prior native insets, explicit caller padding changes/clear, and initial
discovery before ownership. Current Home zero-padding behavior is unchanged by
this ownership guard.

## Weak padding-record cleanup

Status: Implemented. Android detach/reattach acceptance passed on the
pre-migration host described below. Remove-all and owned NativeScroller cleanup
have source and focused regression coverage; current-host device acceptance is
pending.

### Scope and ownership

This specification covers restoring child scroll insets when a collapsible pager
detaches or removes all React children. Other pager behavior remains documented
in the package README. Navigation animations belong to the consuming app.

The native host owns its temporary inset changes and original-padding records.
Android lifecycle callbacks and restoration run on the UI thread. Garbage
collection can clear weak child references independently of that thread.

### API, lifecycle, and identity

No public props, events, defaults, or serialization formats change. Detaching
and removing all children must restore the original insets of every still-live
tracked RecyclerView, native ScrollView, and owned NativeScroller viewport.
Restoration may remove entries from the tracking maps. Reattaching must remain supported.

Padding records are keyed by native child identity using weak references. The
host must not retain removed scroll children solely for later cleanup. A cleanup
snapshot temporarily holds surviving children strongly until restoration ends.

### Platforms and failure handling

Android uses WeakHashMap padding records. Its snapshot must tolerate keys being
collected between reading the collection size and enumerating it, including
single-entry and multiple-entry maps. Collected children require no restoration;
they must not cause NoSuchElementException. Live children must not be skipped
because restoration changes the map.

iOS uses weak-key NSMapTable records and keyEnumerator.allObjects snapshots in
restoreDetachedScrollInsets. Its implementation is unchanged. Web has no Android
native-padding records and is unaffected.

### Resource budget

Cleanup performs one snapshot and one restoration per surviving tracked child
on the UI thread. Temporary storage is proportional to the existing padding
records; no new persistent cache, retry, background task, or bridge traffic is
introduced. This change makes no frame-rate claim.

### Conformance and acceptance

- Source: android/src/main/java/com/reactnativepagerview/CollapsiblePagerHost.kt,
  removeAllReactChildren and onDetachedFromWindow.
- Focused JVM regression: tests/kotlin/WeakPaddingSnapshotTest.kt checks a
  shrinking weak-key collection and restoration that removes snapshot entries.
- The same regression passed seven assertions on Android 36 ART using Kotlin
  2.1.20. Compile it without additional dependencies, then run its main function.
- Android acceptance: build the native host, repeatedly navigate from a populated
  Market pager into stock/token details and back, and exercise modal dismissal.
  Check native fatal logs and pager scroll/header state after returning.
- Runtime evidence on 2026-10-08 against the pre-migration host (1e1809509):
  four NVDA detail visits and gesture returns,
  BTC detail, quick settings Sheet and chart settings modal dismissal, Market
  Perps tab entry/return, and header collapse/expand passed. The app retained its
  PID and produced no app-native fatal crash during this run.
- Source and JVM checks do not establish native device acceptance or iOS visual
  parity. Update runtime status only after the actual app has been exercised.

The newly merged NativeScroller viewport tracking uses the same weak-key
snapshot rule. Its cleanup has source and focused regression coverage; the
pre-migration runtime checks above do not establish its device acceptance.

## iOS lazy-page first-frame preparation

Status: implemented and runtime verified in the consuming app's local native
build on iOS 26.5 (2026-10-10). This package change preserves the same functional
source; app-specific diagnostic logs remain in that local build.

In smooth-header mode, a newly mounted, owned NativeScroller or live NativeList
on the current page, either adjacent page, or a programmatic destination MUST
receive its pager insets and restored offset in Fabric's did-mount callback,
before the next frame. This also applies during a horizontal drag or animation.
Preparation MUST NOT change the selected page, move the shared header out of its
transition owner, attach an offscreen page's observer, or repeatedly restore an
already managed scroll view.

A scroll view removed in a Fabric transaction MUST be excluded from discovery
until that transaction finishes. The exclusion uses weak native identities,
is cleared after did-mount and on recycle, and allows a moved view to be found
again in its next owner. A NativeList collection is usable only while its nearest
Fabric component owns it through contentView, has a registered tag, and retains
that content view as collection delegate. A disposed or unregistered NativeList
MUST NOT become a generic collection fallback.

Verified NativeList collections bypass the existing 0.5-second generic collection
fallback debounce, including replacement-state restoration. Other collection
fallbacks keep that safety delay. Caller insets and retained logical offsets
remain governed by the existing restoration policy.

This is a UI-thread, main-runtime native coordination change with no new props,
events, background-runtime work, persistence or retained strong view cache.
Android already prepares discovered scroll children before draw and is unchanged;
Web is unchanged. Consumers own the decision to lazily mount or prepare a target
page; this package does not import scene code or start business requests.

The Home consumer uses the same lazy-mount trigger on iOS and Android: after
entering the horizontal dragging state, the first received page-scroll progress
with nonzero displacement relative to the starting page MUST trigger early
mounting of the adjacent target page in that direction. There is no fixed
distance or percentage threshold. Measured trigger percentages, such as 0.65%,
are event samples rather than configured thresholds. Early mounting MUST NOT
change the selected page; selection follows the existing native selection event.

Conformance: RNCCollapsiblePagerViewComponentView.mm and the focused ownership /
first-frame XCTest cases. Consumer acceptance confirmed a cold Spot-to-Perps
swipe: the native skeleton received inset 340 and offset -340 at t=1791603349124
while transition=1, 427 ms before native drag settlement. Its window y=514 was
below the header bottom y=502. These values are measured evidence, not package
constants. Native view geometry is confirmed. Later finite consumer round3 simulator
cases additionally verified recorded refresh, header, restoration and gesture
paths; they do not establish every possible sequence or runtime acceptance of a
newly installed module release. Physical-device feel and semantic accessibility
activation remain unverified.


### Native Header stability snapshot

`onCollapsibleStateChanged` retains its existing diagnostic fields and reasons and adds native-only `pageKey: string` and `headerSettledAtTop: boolean`. The key is the actual mounted React child key, not an application tab identifier. Unattached, unknown, replaced or transitioning owners MUST report false. A mounted current owner emits an initial identity snapshot without requiring a first user scroll.

The Header is settled only when C is at its expanded top, the current attached view/key/index/generation still matches, and there is no actual drag, deceleration, scrolling command or horizontal transition. Offset/inset/content/header geometry MUST remain stable for 150ms before true is emitted. Motion or geometry changes revoke true promptly. Deferred reading memory alone MUST NOT be treated as active motion. Pull-to-refresh may settle at a normalized negative offset after drag, bounce and inset geometry stop; network refreshing alone is not a motion gate. Checks stop for collapsed, detached and recycled owners; delayed callbacks revalidate their captured identity. State changes are deduplicated; identity changes still publish an unknown/false snapshot.

The native Home Spotlight adapter consumes this snapshot in the main UI runtime and keeps unknown states paused. It MUST use the mounted page-key mapping and reject old page/source callbacks. Native snapshots already include the 150ms stabilization delay: consumers MUST NOT add another such delay. Legacy Tabs keep their existing normalized-offset and debounce behavior. Anchor measurement, guide qualification and the existing display delay remain independent gates.

### Android page mutation during horizontal transitions

Removing a page or clearing the page collection MUST cancel the active horizontal transition and its temporary display snapshots before notifying the adapter. Cancellation MUST NOT rewrite the shared Header collapse or surviving pages' saved body reading memory. The previous observed owner MUST be detached without committing an offset under a shifted page key.

An empty adapter MUST reject transition starts and transition completion without coercing an index into an empty range. The selected index uses zero as an empty-state sentinel; nonempty mutations clamp it to the remaining valid range. Readding pages MUST allow a fresh selection and MUST NOT resume a removed transition source. Conformance requires actual Android active-transition → zero-pages → readd/selection regression and remaining-page mutation checks; structural checks and compilation alone do not establish runtime behavior.


## Web parent-scroll owner identity

Web Header and sticky overlays use the named scroll timeline only when the
browser supports every CSS property needed for that linkage. Otherwise, the
active scroll owner's offset drives the same clamped Header collapse in JS:
`C = min(max(scrollTop, 0), max(headerHeight, 0))`. The fallback keeps the
existing page owner, insets, body offset memory, and sticky anchor. It updates
when scrolling, selecting or restoring a page, replacing an owner, or changing
Header height, and releases its listener when that owner detaches. An inactive
page cannot move the visible Header. This is a Web-only compatibility path; no
native behavior or public prop changes.
When an overlay forwards a vertical wheel or touch scroll to the active owner,
the owner receives the same internal user-input notification used by route
restoration to cancel an obsolete pending offset.

On Web, an explicitly marked NativeScroller is selected before an implicit NativeList viewport. There is one vertical owner per page; callers using parent-owned NativeList pass that same scroll element as webScrollHost.

The existing NativeScroller pagerScrollKey identifies a Web page scroll owner. Changing the key invalidates that page body offset without remounting its React business content. The active owner also resets the shared header collapse and cancels queued old restore/top commands. Inactive owners invalidate their saved body offset without imperative scrolling; selection checks the current identity before restoring. A→B→A identity changes never revive a prior owner offset. Stable keys preserve ordinary page C+B restoration. Current-page top commands continue to affect only the selected page.

Identity notifications and DOM owner listeners are internal implementation details, removed when the pager root or owner is replaced/unmounted. No native behavior or public configuration is added.

### Web overlay input and keyed-page updates

- Header and sticky slots remain overlays: overflow outside the sticky slot may remain visible without adding its portal height to page insets twice.
- Vertical wheel or touch input over those overlays transfers only to the active page scroll owner when no nested scrollable consumes it. Horizontal controls, editable inputs, and ordinary button/tap behavior retain their own events. Input originating inside the page owner follows native scrolling once.
- Reordering or removing keyed pages must save the previously configured key and owner, rather than resolving the old selected index against new children. Caller index synchronization preserves keyed body offsets and shared header collapse.
- For an explicit NativeScroller, Pager chrome insets apply to the existing ScrollView content element, not its viewport. Insets taller than the available window cannot enlarge the scroll viewport. Existing owner/content padding is preserved and restored separately from Pager insets; direct NativeList/page-host fallback retains its prior layout path.
- The Pager root clips without becoming a programmatically scrollable overflow box. Browser focus and descendant scrollIntoView cannot add a second horizontal page offset outside the selected-page transform.

## Web page geometry and route restoration

Status: Implemented in Web source and focused tests; browser and native runtime acceptance remain separate.

`WebCollapsiblePagerView` is the existing Web `CollapsiblePagerView` class under an explicit root export. Its `isActive` prop defaults to true and represents route visibility, independently of the selected page. The Web-only `useWebCollapsiblePagerPage()` hook returns null outside a Web pager and otherwise exposes `isFocused`, `isRouteActive`, `owner`, `content`, optional `contentHeight`, `width`, `height`, content-relative `scrollTop`, `registerContent`, and read-only `pendingRouteScrollTop`. Native `CollapsiblePagerView`, its bridge, and its props are unchanged. The Web component rejects mounting without a DOM; importing the root or Web module alone must not access DOM globals.

Web callers may supply finite nonnegative `headerHeight` and `stickyHeaderHeight`. When omitted or invalid (negative, NaN, or infinite), the SDK measures the natural inner Header and sticky content with `ResizeObserver`; explicit valid values take precedence, including zero. An initial unmeasured zero is not ready for route restoration; a measured empty chrome element may legitimately have zero height. Measurement must not use the constrained slot height as its own input. Height changes update the owned insets before route restoration. Existing explicit-height callers retain their layout path.

Each page has one SDK-discovered vertical owner: explicit `NativeScroller`, then implicit NativeList viewport, then page host. Registration accepts a connected content element and optional finite nonnegative explicit height; invalid height metadata is ignored. It does not change DOM layout or make a pending target reachable. A later registration of the same element supersedes an earlier one, and stale cleanup cannot remove it. An explicit-height change schedules geometry measurement even if the element border box is unchanged. Metrics use owner client width; `pinnedBottom = ownerRect.top + max(0, headerHeight - owner.scrollTop) + stickyHeaderHeight`; `visibleTop = max(ownerRect.top, pinnedBottom, contentRect.top if present)`; visible height is `max(0, ownerRect.bottom - visibleTop)` and content-relative scroll top is `max(0, pinnedBottom - contentRect.top)`. Unregistered content falls back to the first owner child. Detached content and callbacks from old owners are ignored. One animation-frame measurement per page coalesces scroll, resize, registration, owner configuration, and active-page changes. Disposal releases observers, listeners, frames, and subscriptions.

On active-to-inactive transition, the Web pager freezes the selected logical React page key, `pagerScrollKey` identity, actual owner generation, and last valid raw DOM top. Inactive scroll/geometry changes cannot replace the snapshot. A second blur of the same pending identity preserves the original target. On focus, owner, content, chrome, or inset changes, the pager retries without a timer only when the route is active, that exact key/identity/owner remains selected and connected, the viewport has positive height, the pager has configured the expected insets, and real `scrollHeight - clientHeight` reaches the target. Short content keeps the target pending. Restoration clears pending before one instant scroll and synchronizes the existing shared Header C and per-page body B memory. An idempotent Web `restoreScrollPosition()` returns false while inactive or not ready and true when no target remains or it applies; normal callers need no polling.

Valid vertical wheel, scroll keys, scrollbar drag, overlay-forwarded input, accepted NativeList commands, and explicit page-top/sticky-top commands cancel pending route intent through the existing SDK takeover path. As in the prior Web app adapter, any owner `pointerdown` or `touchstart`, including a child target, cancels only route pending; this does not newly commit or clear SDK C+B memory. Geometry-only scroll/resize/scrollend cannot cancel intent. Valid same-offset commands cancel it; invalid or stale commands do not. A matching route target takes priority over automatic tab C+B restoration. Selection of a different key, owner identity reset (including A→B→A), genuine DOM owner replacement, or removal cancels pending; a same-key index reorder preserves it. A same-commit blur and Activity/Offscreen effect disconnect captures the current route target before releasing the owner; repeated disconnects preserve an existing pending target. A temporary React ref null or Activity/Offscreen effect disconnect followed by the identical connected owner preserves the logical route target and owner generation while releasing all observers, listeners, frames, and DOM configuration during disconnect. Restoration is unavailable while disconnected; reconnect remeasures automatic chrome before applying pending. A truly removed pager instance cannot transfer its pending target to a new instance. Older queued tab/top callbacks must validate generation before writing after route restoration or cancellation. The app owns navigation focus and business tab names; the SDK never infers them from escaped React child keys.

Platform matrix: Web owns this DOM geometry, registration, route pending, and explicit Web export. iOS and Android keep native scrolling, restoration, and page coordination documented above, with no new prop, native bridge call, JS heap sharing, or process resource. The UI main runtime owns Web state; background runtimes do not deserialize it. Source/type tests establish only Web contract implementation, not browser rendering or native device behavior. Acceptance requires real Web/Desktop route blur, short content then growth, user takeover, dynamic chrome, key reset, and Home/Market scroll regressions.

## Native package entry isolation

The Native package entry exports the existing Native PagerView,
CollapsiblePagerView, NativeScroller, usePagerView and ScrollEdgeEffect APIs.
Native package resolution MUST NOT evaluate the Web pager or Web page-context
modules. The react-native field and conditional export resolve to a real Native
entry; the browser entry retains the Web runtime APIs. The generic declaration
entry preserves the existing shared Web value/type API for TypeScript callers.
This separates package initialization in the Native UI main runtime; it does not
add a bridge, share JS heaps with background, or change Native scroll commands.
Acceptance includes the actual Native dependency graph and unchanged startup
budgets, Web runtime/type exports, and package source/module/declaration entries.
