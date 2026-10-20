# Native Tab View behavioral contract

## iOS touch-driven tab selection

Status: implemented in module source. Equivalent native behavior passed recorded
Home consumer simulator cases: five stationary tab taps, four nonselected-tab
drags, and active two-finger drags in both release orders followed by normal
selection. These are consumer runtime results, not runtime verification of a new
published package. Sparse final-UP without MOVE has method-level branch coverage
only: the current XCTest injector interpolates MOVE. Semantic accessibility
activation, callback delivery for stationary long presses, and physical-device
feel remain unverified. Android behavior is unchanged by this implementation.

The native iOS tab bar MUST NOT select or reselect a tab, or emit its tab-press
navigation event, for a touch that becomes a drag. Its guard MUST keep UIKit
selection pending and cancel the touch once either displacement exceeds 10
points, including the final release position. A stationary single tap MUST
release UIKit's existing selection path. Pending stationary multi-touch retains
UIKit's existing selection semantics.

Once drag cancellation is active, overlapping touches MUST remain guarded until
the final live touch ends or is cancelled. Touch identities are tracked only for
the active native sequence; reset MUST clear the set and selection tracking.
Cancellation MUST NOT prevent the following stationary tap from selecting a tab.
Existing long-press recognizers remain independent. Accessibility activation and
JS-driven selection MUST remain usable without a touch sequence. No caller prop,
event shape, default, persistence, or background-runtime behavior changes.

The recognizer and tab selection delegate operate on the iOS UI thread in the
main-runtime native view. They own no background-runtime or shared JS objects.
The only added retained state is the live touch identity set, bounded by touches
in the current sequence; no timers, retry loops, or strong view cache are added.
Debug-only NativeLogger messages contain direction/recognizer lifecycle and tab
indices, with no account data or business-specific acceptance probes.

## Conformance and acceptance

Implementation: ios/RCTTabViewContainerView.swift,
TabBarDragCancellationGestureRecognizer and shouldSelectTab.

Acceptance MUST distinguish stationary taps from drags on each rendered tab,
observe the actual selected page and navigation events, exercise both lift orders
for an overlapping active drag, and verify the next normal tap. Final release
position without MOVE requires a trustworthy sparse-input channel; method-level
coverage MUST NOT be reported as touchscreen acceptance. Long-press callback
delivery and semantic accessibility activation require independent observable
evidence. Source compilation and package-file checks MUST be recorded separately
from consumer runtime acceptance.
