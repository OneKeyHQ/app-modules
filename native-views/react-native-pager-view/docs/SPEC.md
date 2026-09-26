# PagerView native scrolling contract

Existing PagerView and CollapsiblePagerView APIs are documented in README.md.
This section defines the added Android coordination boundary; it does not change
React props, iOS/Web behavior, page retention, or row ownership.

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
