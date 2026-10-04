# NativeList scrolling contract

The snapshot, events and imperative API are documented in [README](../README.md).
Keys identify outer rows; a wallet group member is not a separate scroll key.

## Position alignment (implemented on Web, Android and iOS)

Web, Android and iOS explicit `viewPosition`/start/center/end alignment uses the
signed difference `viewportLength - itemLength`, then applies `viewOffset` and clamps the final
offset to the content bounds. This also applies to horizontal lists. Positive
`viewOffset` leaves space before the aligned target. An oversized row cannot
be fully visible; center aligns its center and end aligns its trailing edge.
`nearest` retains its existing oversized-row behavior for compatibility.

Imperative scroll requests run once at the current geometry. Consumers own
repositioning after layout changes and cancellation when users take control.

## Platform status and validation

The Web engine uses the TypeScript calculation. Android threads the existing
alignment mode through visible-item, deferred-layout and smooth-scroll paths,
using signed space for explicit alignment and legacy nonnegative space for
nearest. For an unmounted explicit target, the provisional nonanimated offset
keeps its leading edge inside the viewport so the existing deferred pass can
measure and align the real item. No extra retries or bridge API changes.
iOS uses UICollectionView layout attributes, including unmounted targets, and
signed space for explicit alignment while preserving nearest. Its complete-app
baseline reproduces all three parent/middle/last oversized-group targets being
offscreen (usable viewport 691.667pt, group 1750pt). After the Swift change,
all three focused targets are fully visible. Its complete-app primary matrix
passes 15/15 across ordinary 1/15/25 and hidden 1/9/20 wallets, with repeated
reopen, real scene rotation (691.667/295.667pt usable viewport), queued-close
cancellation and real touch cancellation verified separately. UIKit attributes
are available for the unmounted group; the Android provisional workaround is
not added to iOS. Native horizontal runtime coverage remains pending.

Runtime verified: complete Electron/Web applications and focused engine tests.
Android's complete application reproduces an offscreen selected child in a
20-hidden-wallet group at a 540.6dp viewport before the Kotlin change. After
the change, ordinary 1/15/25 and hidden 1/9/20 selectors, 358/540.6/898.3dp
viewports, repeated reopen, and the vertical animated/unmounted and nearest
paths pass in the complete Android app (27/27 visible-target runs, including
three repeated smoke cases). A refreshed 79-package Android autolinking graph
with native/JS package versions matched independently corroborates these cases.
Close-after-RAF-queue cancellation passes 3/3;
touch cancellation preserves user scrolling. Horizontal native runtime
validation is still pending. Unit checks do not constitute native-device
verification.

## Native regression coverage

Android's existing JUnit setup exercises the production alignment helper used
by visible, deferred and smooth paths, including oversized explicit targets,
short/equal-size rows, padding, signed offsets and nearest compatibility. A
second production helper keeps an unmounted explicit target's provisional
start within the viewport, including zero/one-pixel boundaries. These are
native geometry unit tests; they do not prove RecyclerView mounting, deferred
callback timing, or application-owned layout/input cancellation. Those require
the complete application checks above.

SwiftPM/XCTest compiles a symlink to the shipping Foundation geometry helper,
covering measured oversized groups, explicit start/center/end, signed offsets,
nearest, short/equal rows and final content bounds. These run as macOS native
Swift unit tests; they do not instantiate UIKit or prove layout-attribute
availability for unmounted cells. That is established separately in the full
iOS application, not by substituting a duplicated JavaScript calculation.
