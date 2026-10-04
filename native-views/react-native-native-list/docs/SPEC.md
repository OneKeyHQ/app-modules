# NativeList scrolling contract

The snapshot, events and imperative API are documented in [README](../README.md).
Keys identify outer rows; a wallet group member is not a separate scroll key.

## Position alignment (implemented on Web)

Web explicit `viewPosition`/start/center/end alignment uses the signed difference
`viewportLength - itemLength`, then applies `viewOffset` and clamps the final
offset to the content bounds. This also applies to horizontal lists. Positive
`viewOffset` leaves space before the aligned target. An oversized row cannot
be fully visible; center aligns its center and end aligns its trailing edge.
`nearest` retains its existing oversized-row behavior for compatibility.

Imperative scroll requests run once at the current geometry. Consumers own
repositioning after layout changes and cancellation when users take control.

## Platform status and validation

The signed explicit-position change runs in the Web engine's TypeScript
calculation. Native refs share normalization, then use Swift/Kotlin alignment.
iOS/Android keep their existing oversized-row alignment; parity remains unverified
and is outside this desktop fix. No bridge API changes.
Runtime verified: explicit alignment in complete Electron/Web applications and
focused engine tests. Unit checks do not constitute native-device verification.
