# Perp Depth Bar Native Contract

Status: **Implemented** for iOS imperative-update ownership and threading.
Native XCTest has passed on an iOS simulator for depth/text publication; the
review follow-up cases below still require runtime validation. Physical-device
behavior remains unverified.

## Purpose and scope

`PerpDepthBarsView` draws one side of an order book as native depth bars with
price and size text. `PerpSideRatioView` draws the separate bid/ask ratio. Both
are used by React Native on iOS and Android. This package does not fetch market
data, format prices, or decide how many order-book rows the application needs.
The threading rule below covers iOS depth, text, and ratio imperative updates.
Android behavior remains unchanged.

## Ownership and public API

The application owns the source rows and formatted text. The Nitro boundary
passes `setDepth(ArrayBuffer)` a packed little-endian Float32 percentage for
each row and passes `setText(prices, sizes)` two string arrays. Percentages use
the same 0–100 scale as the `percents` prop. The methods have no return value
or callback. An empty depth buffer clears the real depth rows; the existing
placeholder props may then render placeholder rows. Extra bytes after the
last complete Float32 are ignored, as before.

The native depth view owns its current row arrays, `CALayer`/`CATextLayer`
objects, and display link. It must not retain the caller's ArrayBuffer or raw
pointer beyond the synchronous `setDepth` call. It decodes all complete floats
into a Swift-owned array before crossing to the main thread. Text arrays are
captured as Swift values for publication. No JS heap or buffer is shared with
the native layout after the call returns.

The prop API, colors, row geometry, `epoch` snap behavior, `reducedMotion`,
`onRowPress` row index, and continuous easing retain their existing meanings.
Depth and text have independent, one-way imperative modes for the lifetime of
the view. The first published `setDepth` update makes subsequent `percents`
prop writes ineffective; the first published `setText` update does the same
for both `prices` and `sizes`. This includes empty and nonempty prop writes.
Callers that never use the corresponding method continue to use props. Empty
imperative updates still clear the corresponding data, without restoring prop
ownership. This matches the existing TypeScript contract and Android behavior.

## Lifecycle and concurrency

The generated Nitro imperative methods call into Swift synchronously on the
calling thread, which may be the React Native JavaScript thread. `setDepth`
must finish decoding while that call owns the buffer. Both depth-array
publication and the price/size pair publication occur on the main thread:
directly if already there, or asynchronously in one main-queue closure per
method if called elsewhere. The imperative-mode flags change together with
their data on the main thread, so a later Fabric prop transaction cannot clear
published imperative data. The closures retain decoded Swift values and use a
weak view reference so a disposed view is not kept alive by queued updates.

Updates submitted serially from one calling thread retain their dispatch
order. The two imperative methods remain separate calls; callers should send
text in the same source frame as depth. Each text call publishes its prices
and sizes together, then requests layout. UIKit layer mutation, layout, and
display-link animation run on the main thread. Empty, growing, and shrinking
row sets must leave the rendered layer count and text consistent with the
latest published arrays. Existing animation targets are retargeted rather
than creating a new animation per update. `setRatio(bidPercentage, askPercentage)`
publishes both values together on the main thread before requesting layout;
main-thread calls are synchronous and off-main calls enqueue one weakly owned
closure. The ratio prop API and clamping/easing rules are unchanged.

## Data and platform behavior

The view keeps only its latest native row arrays and layer state. It has no
persistent cache, disk state, or stable row key. `epoch` identifies a new
dataset for the existing snap decision; it does not change buffer ownership.

| Platform | Imperative depth/text behavior |
| --- | --- |
| iOS | Synchronously snapshot Float32 buffer bytes, then publish depth/text arrays and their imperative modes on the main thread before layout. Publish the ratio pair together on the main thread. |
| Android | Existing native implementation and scheduling remain unchanged by this fix. |
| Web | No native depth view is supplied by this package. |

## Failure and resource limits

The methods retain their current input semantics and do not introduce new
clamping, row-count limits, or retries. The layout path still clamps scale for
rendering. A disposed view does not apply an already queued update. A
zero-length depth buffer clears native data on the main thread. The caller is
responsible for providing aligned prices and sizes; missing text for a row is
drawn as an empty string as before.

Depth decoding is proportional to the number of complete floats and runs on
the caller thread; only Swift-owned values cross to the main queue. Main-thread
work remains the existing layer synchronization, layout, and animation work.
No performance claim is made until measured under production row counts.

## Conformance and acceptance

- `ios/HybridPerpDepthBars.swift` implements buffer decoding, main-thread
  publication, layer layout, and animation.
- `ios/HybridPerpSideRatio.swift` implements main-thread ratio publication and
  split-layer layout.
- `ios/tests/HybridPerpDepthBarsTests.swift` must instantiate the real pod
  class with a real Nitro ArrayBuffer and verify off-main update deferral,
  snapshot ownership, empty/grow/shrink transitions, ordered rapid updates,
  and actual `CALayer`/`CATextLayer` output.
- Review regression cases must verify that prop-only updates still work,
  depth/text ownership switches independently, later empty or nonempty Fabric
  prop writes cannot overwrite imperative data, empty imperative calls clear
  data, and off-main ratio calls defer the entire pair without retaining a
  disposed view.
- The existing Android implementation is outside this change; its behavior
  needs separate runtime evidence before claiming Android parity.
- Live crash reports motivate the ownership fix but do not identify an exact
  source line or prove that every reported crash has this cause.

The real pod XCTest target first failed three off-main ownership tests against
the prior implementation (five tests run, nine expected assertions failed),
then passed all five tests after the iOS change on an iPhone 17 simulator with
iOS 26.5. The passing tests cover snapshot ownership, empty and changing row
counts, text-layer output, synchronous main-thread calls, and disposal before
queued publication. This is simulator evidence; no physical-device run or
production crash-rate result is claimed.

For the review follow-up, the real pod and all ten XCTest cases compile and
link successfully for arm64 iOS Simulator. The five added ownership/ratio
regressions have not run locally: CoreSimulatorService denied creation of the
worktree's isolated external-drive device set (`NSCocoaErrorDomain 513`,
`Operation not permitted`). CI simulator execution remains required.
