# Native Tab View behavioral contract

## Native left drawer

Status: implemented in module source. Package TypeScript, iOS/Android Fabric
code generation, iOS Objective-C++ Fabric bridge compilation against RN 0.86.2,
13 React wrapper checks, twelve Android API 36 native tests and four real-Fabric
iOS review cases passed (shared-header/body arbitration and bar-height switches).
The static Home example also passed a focused TypeScript check.
Detailed iOS runtime evidence is recorded separately; physical-device and exact
reference-video acceptance remain pending.

`renderDrawerContent?: () => React.ReactNode` supplies an optional menu slot.
`drawerOpen?: boolean` defaults to false and requests its stable open state.
`onDrawerOpenChange?: (open: boolean) => void` reports a completed user transition;
controlled consumers MUST mirror it into `drawerOpen`. Prop-driven transitions,
initial layout and cancellation emit no event. A user transition returning to
the previous stable state emits only when it overrides an in-flight external
target, so the consumer can reconcile that target. `drawerSwipeEnabled?: boolean`
defaults to false; callers MUST enable it only where drawer navigation is appropriate (normally a root
screen). Existing `hapticFeedbackEnabled` enables one light native haptic after
a completed user transition changes the stable state. Programmatic updates and
cancelled gestures do not vibrate.

The drawer is physically left on both iOS and Android, including RTL. Its width
is 78 percent of the host. The foreground is the full tab container, including
the native tab bar and an optional custom JS bar. It keeps its full width and
height while moving right, without scaling, scene reparenting or remounting.
The native host and two slots remain mounted when the drawer is disabled or its
content changes. Missing content disables the drawer, resets it closed without
an event, and passes foreground input through. Web omits the drawer host/menu
and passes the existing tab content through; it implements no drawer gestures.
The existing tab view's other Web support is unchanged.

For normalized progress `p` in [0, 1], foreground translation is `.78 * width * p`,
its left circular corner radius is `64 * p` points/dp and its neutral gray
dim surface (RGB 28/28/28) has opacity `.7 * p`.
The menu translates from `-.12 * drawerWidth` to zero; a black menu shade fades
from .12 to zero. These are initial reference-video tuning values, not a claim
of pixel-perfect parity. Closed menus are hidden from touch/accessibility;
while visible, the foreground is blocked by a native dim surface. Tapping that
surface closes the drawer. iOS accessibility escape and Android Back close an
open drawer; the dim surface exposes an accessible close action.
On Android the dim surface MUST retain full foreground bounds even while hidden
and after React/Fabric ancestors suppress `requestLayout`. Internal containers
and both slots MUST be measured and laid out on host layout and slot changes,
without relying on a later ancestor layout pass. Visible dim input MUST prevent
native foreground controls from receiving the same touch.
While closed, that retained dim surface MUST also be excluded from React Native
touch-target lookup, so JS Pressable taps reach the foreground. Visibility alone
is insufficient for React Native's native target traversal.

The reported native tab-bar height MUST become zero when hidden and restore the
current height when shown. The context and drawer exclusion use one effective
height: a rendered custom bar's own measured height takes precedence; otherwise
`tabBarHidden` forces zero. Native hide measurements MUST NOT overwrite a custom
bar's height, including across native/custom presentation switches.

With opening swipes enabled, a one-finger rightward gesture may start anywhere
in the foreground content. At the first confirmed direction, the actual native
touch target and its ancestor chain determine ownership. A horizontal scroll
view, list or pager which can consume that physical direction takes priority;
only when every such ancestor is at its boundary may the drawer open. Vertical
scroll views do not block horizontal drawer intent. Once selected, the owner is
locked until up/cancel: reaching a boundary mid-gesture does not hand off.
Disabled pager input does not claim a gesture based on its retained scroll
range; an independently enabled horizontal child inside that page still can.
An ancestor pager which explicitly excludes the touched shared header MUST NOT
claim that header gesture solely because its horizontal offset is nonzero.
The header's own nested horizontal content retains its normal boundary priority.
On iOS this uses the repository's existing `excludedHeaderView` Objective-C
getter on the native smooth pager and the real touch target's ancestry. It does
not invoke another scroll view's UIKit recognition lifecycle hook, add a Pager
API, or infer a header exclusion from a pager's selected page.
The iOS UIPageViewController boundary uses its public data-source query once per
decision, skipping its internally centered scroll offset. Android observes
pending direction even when a nested pager initially disallows interception,
but stops overriding that request after the child wins. Sliders and editing controls,
web/graphics surfaces, system back gestures and starts in the bottom tab-bar
footprint take priority. When open, closing gestures
remain enabled even if opening swipes are disabled. Only the exposed
foreground accepts a closing pan; menu scrolling remains the consumer's own.
Vertical gestures fail before taking ownership. Active touches interrupted by
a second finger, system cancellation, disabling, detach or a newer prop update
cannot later emit an event. Cancellation returns to the previous stable state,
or resumes an external target whose programmatic animation the gesture interrupted.
No gesture progress crosses the JS bridge.

Both platforms settle on their UI thread with the same critically damped curve:
`p(t) = target + (a + b*t) * exp(-22*t)`, where `a = p0-target`,
`b = v0 + 22*a`, and normalized velocity `v0` is clamped to [-8, 8] per second.
Presentation is clamped to [0, 1], and the transition ends at or before 500 ms.
Release targets open when `p + v0*.12 >= .5`. A newer animation or touch starts
from the current displayed progress and cancels the previous completion.
Resize snaps a pending settlement to its target, emitting its user event once
if appropriate; an active drag follows the same cancellation rule. Detach cancels
animations and callbacks; reattachment reapplies the latest external target.
The host retains at most one display-link/animator, one touch
sequence, two React child slots, and native dim surfaces. It owns no snapshots,
workers, persistence, JS animation objects, RNGH or Reanimated dependency.

Implementation boundaries: `src/BottomTabs.tsx` owns slots and settled callbacks;
`src/BottomTabsDrawerNativeComponent.ts` defines the Fabric boundary;
`ios/RCTBottomTabsDrawerContainerView.swift` and
`android/src/main/java/com/onekey/bottomtabs/BottomTabsDrawerView.kt` own native
input, layout, animation and cleanup. The iOS Fabric component and Android manager only forward children,
props and the settled event. Required acceptance includes both bar styles,
custom bars, content/vertical gestures, dim close, rapid reversal, cancellation,
disable/remove-content, resize, detach, accessibility, RTL and bottom-bar
gesture ownership, including a real pager, nested horizontal list and vertical
scrolling in the complete Fabric example. Source checks and emulator checks do not establish physical
device smoothness or exact reference-video fidelity.
The Android native suite also exercises generated drawer prop forwarding in RTL,
physical-left placement, and closing pans while opening swipes are disabled.

The standalone `example/react-native/pages/BottomTabsHomeDrawerExample.tsx`
reproduces Home’s wallet toolbar, balance/actions/banner shared header and
Spot/Perps/Earn/NFT/History hierarchy using static data, native CollapsiblePager
and standard React Native scroll views. It is an equivalent interaction fixture;
the application’s existing JS collapsible-header implementation and business
NativeList pages are not migrated or exercised by this fixture.

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

## iOS vertical tab bar placement and side accessory

Status: Implemented. Runtime results are in Conformance.

On iOS 27.1+, devices such as iPhone Duo can place the `UITabBarController`
bar vertically in a side strip next to the status bar. The view reports that
placement and can draw one round accessory button above the vertical bar.

- **Placement:** `onTabBarPlacementChange({ placement })` fires with `bottom`,
  `leading`, or `trailing`, read from `traitCollection.verticalBarEdge` of the
  tab bar controller. It fires once after the bar is created and again on every
  change (rotation, folding, window resizing). Builds without the iOS 27.1 SDK
  and earlier systems always report `bottom`. Android never fires it.
- **Side accessory:** `sideAccessoryIcon` (an image source, loaded with the
  React image loader and drawn as a template tinted with `inactiveTintColor`),
  `sideAccessoryLabel` (accessibility label), and `onSideAccessoryPress`. The
  button is 48 pt, a Liquid Glass capsule on iOS 26+, centered horizontally on
  the vertical bar's item platter and 12 pt above it.
- Android MUST accept `sideAccessoryIcon` and `sideAccessoryLabel`, including
  null resets, as no-ops through its generated ViewManager interface. It MUST
  NOT create a side accessory or emit placement/accessory events. Shared Fabric
  props still require Android setters even when the feature is iOS-only.
- The button shows only when all hold: the placement is `leading` or
  `trailing`, the tab bar is not hidden, the icon has loaded, and the platter
  is found. Otherwise it is hidden; a bottom bar never shows it.
- UIKit does not expose the platter frame. The view finds the platter by its
  view class name below the tab bar controller (skipping tab content views),
  reads its frame, and calls no private API. If a later system renames or
  restructures it, the accessory hides; no other behavior depends on it.
- Layout re-runs, coalesced on the next main-queue turn, after container
  layout, tab rebuilds, tab bar hiding, icon loads, and changes to the traits
  affecting `verticalBarEdge`.

## Threading and ownership

All gesture handling, animations, and timers run on the platform UI thread in
the main-runtime native view. The view owns no background-runtime or shared JS
objects. The tab bar retains the current touch sequence, the last pressed tab
and timestamp, and at most two running animators. The optional drawer's additional
single animation and touch ownership are bounded in the Native left drawer section.

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

Vertical tab bar: `RCTBottomTabsContainerView.swift` (`currentTabBarPlacement`,
`updateTabBarPlacementAndSideAccessory`, `verticalTabBarPlatterFrame`).
Required iOS runtime cases on iPhone Duo: the outer display and landscape inner
display report `trailing` and show the accessory above the platter; the
portrait inner display reports `bottom` and hides it; hiding the tab bar hides
it; a press emits `onSideAccessoryPress` once.
