# PagerView native scrolling contract

Existing PagerView and CollapsiblePagerView APIs are documented in README.md.
The sections below define native coordination boundaries without new React props.

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
a separate contract in react-native-tab-view/docs/SPEC.md.

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

iOS nested horizontal ownership MUST include enabled nonpaging horizontal-only
ScrollViews, with content wider than bounds and no vertical content overflow.
Both pan holding and failure dependencies MUST use the same eligibility. A
second touch in the pending Pager guard fails recognition; an already active
Pager guard cancels and restores each held pan's original minimum touch count.

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
deep offsets and the existing lower-bound/header alignment rules are unchanged.
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
