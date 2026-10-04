# NativeList scrolling contract

The snapshot, events and imperative API are documented in [README](../README.md).
Keys identify outer rows; a wallet group member is not a separate scroll key.

## Position alignment (implemented on Web and Android)

Web and Android explicit `viewPosition`/start/center/end alignment uses the
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
iOS retains the existing nonnegative calculation;
its device validation is blocked by external CoreSimulator device-set EPERM.

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
