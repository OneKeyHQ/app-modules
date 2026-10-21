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
  with the blur-controlled scene underlap described below.
- `floating`: a capsule bar modeled on the iOS 26 tab bar. Scene content extends
  underneath it.

Floating geometry (dp): bar height 62, side margin 20 plus the navigation-bar
side insets, bottom gap `max(bottomInset, 20) + 4`, fully rounded corners. The
bottom inset is the navigation-bar inset (0 when `ignoreBottomInsets`) or the
keyboard inset when `ignoreKeyboardInsets` is false, whichever is larger. Icons
are 26, labels 10 sp in both states unless `fontSize` is set, and item content is
vertically centered. The reported tab bar height is the bar height plus its
bottom gap, or 0 while hidden.

Floating appearance with background blur disabled: the bar surface is `barTintColor` (or the theme background)
at 95% opacity (lightened slightly for dark colors), with
a hairline rim and an outline shadow. When background blur is enabled (see below), the bottom strip extends from
24 dp above the capsule to the bottom of the scene, including the gap beneath
the capsule. The existing capsule keeps its rim/shadow and uses a 12 percent
surface fill over the blurred strip; its lens and navigation controls remain
sharp. The selected tab sits on a capsule pill inset 4 dp
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
   Release displacement is also checked against touch slop, including when no
   MOVE was delivered or the last MOVE remained within slop. If release crosses
   slop, it starts a drag and updates the candidate from the release position.
   Release after a drag selects the candidate once, only if it differs from the
   current tab. Cancel selects nothing.
4. The lens then settles back into the pill on the selected tab, which stays on
   the old tab for cancelled or `preventsDefault` presses.

JS-driven selection changes slide the pill with a spring and velocity stretch.
Switching style at runtime rebuilds the bar and restores Material's label sizes,
icon size, ripple, padding, and gravity when returning to `default`.

## Android bottom background blur

Status: implemented in module source. The renamed package passed TypeScript
checking, Android compilation with regenerated Fabric bindings, eight React
wrapper tests, and ten direct-native API 36 tests (five each for docked and
floating styles). Style switching with blur active received focused regression
coverage. Complete consumer-app and physical-device acceptance remain pending;
API 31-35 and visual dark-mode acceptance have not been reverified for this port.

`androidBottomBlurEnabled?: boolean` controls the native Android tab bar's live
background blur, translucent surface tint, and 24 dp fade into the page above it.
Omitting it (including `undefined`) MUST enable the effect only on Android 16
(API 36) and newer. `false` MUST restore the existing appearance and layout for the current
`androidTabBarStyle`: docked scenes reserve bar height; floating scenes retain
their existing underlap and lens gestures. `true` MAY enable it on Android 12 (API 31) and newer; older
Android versions and software-rendered windows MUST retain the existing layout
and appearance. iOS and custom JS tab bars MUST ignore this option.

When active, the selected scene MUST fill the native container behind the tab
bar; icons, labels, badges, and the style's press feedback MUST stay sharp and interactive.
The reported scene dimensions MUST include this underlap, while tab-bar height
MUST retain its existing meaning (including system/IME padding unless ignored).
Callers MUST use `useBottomTabBarHeight` to pad scrollable content when its final
row must scroll clear of the bar. Hiding the bar MUST remove the effect and
report zero bar height. Toggling, resizing, theme changes, and system/keyboard
insets MUST refresh geometry and appearance without remounting scenes.

The module owns the GPU resources on the UI thread. It MUST sample only the
scene layer, excluding the tab bar and its own output. It MUST use hardware
RenderNode/RenderEffect rendering of a bottom strip at one-quarter resolution
in each dimension, with a 20 dp blur radius and 40 dp sampling margin above the
fade. It MUST NOT capture bitmaps, read pixels back to the CPU, emit scroll
events to JS, or run a recurring frame callback for this effect. Rendering MUST
follow the normal view invalidation cycle, using the scene's existing display
lists. The translucent tint MUST follow `tabBarStyle.backgroundColor` when set,
otherwise the docked Material surface color or floating theme background,
with opacity capped at 78 percent.

Only one blur RenderNode and cached paints/shaders MAY be retained per mounted
tab view. Its offscreen dimensions MUST be bounded by one-quarter of the view
width and the smaller of the view height or reported bar footprint plus 64 dp (rounded up).
Disabling, hiding, detaching, and dropping the native view MUST discard the
display list; no scene snapshots, disk cache, timers, or background workers are
owned. SurfaceView content is outside the normal View display list and is not
supported as a blur source. Liquid Glass refraction and shape morphing are out
of scope.

Acceptance MUST cover API 35/36 default behavior and explicit overrides, live
scrolling under the bar with sharp controls, on/off restoration, hidden bars,
tab changes, resize, dark/light themes, ignored bottom insets, and both keyboard
inset modes. A representative scroll comparison MUST distinguish UI/RenderThread
timings and missed frames from visual correctness. Source/type checks and
emulator measurements MUST NOT be reported as physical-device performance.

Implementation: `src/BottomTabs.tsx` owns the public default and custom-bar guard;
`src/BottomTabsNativeComponent.ts` and `BottomTabsViewManager.kt` define the Fabric
boundary; `BottomTabsView.kt` owns scene geometry, appearance, and disposal;
`BottomBarBlurRenderer.kt` owns the bounded GPU pipeline. Focused native tests
and runner instructions are in `native-tests/android`. The example's settings
page exposes the switch and its scrolling scene reserves the reported bar height.

## Threading and ownership

All gesture handling, animations, and timers run on the platform UI thread in
the main-runtime native view. The view owns no background-runtime or shared JS
objects. Retained state is bounded to the current touch sequence, the last
pressed tab and timestamp, and at most two running animators.

## Conformance and acceptance

Implementation:
- iOS: `ios/RCTBottomTabsContainerView.swift` (`shouldSelectTab`,
  `emitDoublePressIfNeeded`), `ios/RCTBottomTabsComponentView.mm` event forwarding.
- Android: `android/src/main/java/com/onekey/bottomtabs/BottomTabsView.kt` (floating
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
