# PagerView native scrolling contract

Existing PagerView and CollapsiblePagerView APIs are documented in README.md.
The sections below define native coordination boundaries without new React props.

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
