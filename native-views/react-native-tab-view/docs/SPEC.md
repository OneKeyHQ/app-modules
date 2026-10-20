# Native Tab View behavioral contract

## Tab presses and double press

Status: implemented in module source. Runtime verification status is recorded
under Conformance and acceptance.

Every tab press that the native tab bar accepts emits `onPageSelected` with the
tab key, including a press on the already selected tab (reselection). A
reselection never changes the selected page. A tab with `preventsDefault` emits
`onPageSelected` but keeps the current page selected.

`onTabDoublePress` fires when two consecutive accepted presses target the same
tab within the platform double-tap timeout (iOS 0.3 s; Android
`ViewConfiguration.getDoubleTapTimeout()`, 300 ms by default). Both presses still
emit `onPageSelected` first; the double press is an additional event, emitted
after the second press. After it fires, tracking resets, so a third quick press
starts a new pair. Double press is derived from accepted selections only: no
extra gesture recognizer is attached and single-press latency is unchanged.
JS-driven `selectedPage` changes do not count as presses.

Tab long press is not supported. The former `onTabLongPress` prop/event and the
native long-press recognizers were removed.

## iOS native tab bar gestures

The iOS tab bar keeps UIKit's own gesture handling. On iOS 26 this includes the
Liquid Glass press-and-drag selection: pressing a tab lifts the lens and dragging
scrubs across tabs, selecting on release through the normal
`tabBarController(_:shouldSelect:)` path. The module MUST NOT install recognizers
that delay, cancel, or require failure of the system tab bar gestures.

The previous drag-cancellation guard (which refused selection for any touch that
moved more than 10 points) was removed because it disabled drag-to-switch.

## Android tab bar style

`androidTabBarStyle` selects the Android presentation; iOS ignores it.

- `default` (default): Material `BottomNavigationView` docked at the bottom,
  unchanged behavior.
- `floating`: a capsule bar modeled on the iOS 26 tab bar. Scene content extends
  underneath it.

Floating geometry (dp): bar height 62, side margin 20 plus the navigation-bar
side insets, bottom gap `max(bottomInset, 20) + 4`, fully rounded corners. The
bottom inset is the navigation-bar inset (0 when `ignoreBottomInsets`) or the
keyboard inset when `ignoreKeyboardInsets` is false, whichever is larger. Icons
are 26, labels 10 sp in both states unless `fontSize` is set, and item content is
vertically centered. The reported tab bar height is the bar height plus its
bottom gap, or 0 while hidden.

Floating appearance: the bar surface is `barTintColor` (or the theme background)
at 95% opacity (lightened slightly for dark colors), with
a hairline rim and an outline shadow. Android has no system backdrop material,
so no blur is applied. The selected tab sits on a capsule pill inset 4 dp
(`activeIndicatorColor` when provided). The Material ripple is disabled; the
lens provides press feedback.

Floating interaction (single finger; motion splitting is disabled):

1. Touch down on any tab moves the pill to that tab and lifts it into a lens
   (grows past the bar edges, gains a rim) and magnifies that tab's content.
2. Movement beyond touch slop starts a drag: the lens follows the finger with
   edge resistance and stretches with velocity; the tab nearest the lens center
   becomes the candidate, is magnified, and ticks haptically when it changes.
   No page change or event happens during the drag.
3. Release without drag performs the tab's click (selection or reselection).
   Release after a drag selects the candidate once, only if it differs from the
   current tab. Cancel selects nothing.
4. The lens then settles back into the pill on the selected tab, which stays on
   the old tab for cancelled or `preventsDefault` presses.

JS-driven selection changes slide the pill with a spring and velocity stretch.
Switching style at runtime rebuilds the bar and restores Material's label sizes,
icon size, ripple, padding, and gravity when returning to `default`.

## Threading and ownership

All gesture handling, animations, and timers run on the platform UI thread in
the main-runtime native view. The view owns no background-runtime or shared JS
objects. Retained state is bounded to the current touch sequence, the last
pressed tab and timestamp, and at most two running animators.

## Conformance and acceptance

Implementation:
- iOS: `ios/RCTTabViewContainerView.swift` (`shouldSelectTab`,
  `emitDoublePressIfNeeded`), `ios/RCTTabViewComponentView.mm` event forwarding.
- Android: `android/src/main/java/com/rcttabview/RCTTabView.kt` (floating
  layout, `handleFloatingTabTouch`, `emitDoublePressIfNeeded`),
  `FloatingTabBarStyle.kt`, `events/TabDoublePressEvent.kt`.

Required runtime cases per platform: stationary tap selects; reselect emits
`onPageSelected` without page change; two quick taps on one tab emit
`onTabDoublePress` exactly once; slow taps do not. iOS 26: press-and-drag across
tabs selects the tab under the finger on release. Android floating: press on a
non-selected tab lifts the lens there; drag crossing tab centers does not change
pages; release selects the nearest tab once; cancel restores; hidden bar, dark
mode, and 3-button navigation insets lay out correctly. Source compilation is
recorded separately from runtime acceptance.
