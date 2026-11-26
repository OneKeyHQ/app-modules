# DeviceUtils: InMemoryStore contract

## Status and scope

This specification covers the new InMemoryStore API in the existing DeviceUtils
package. Other DeviceUtils APIs retain their existing TypeScript/native
contracts. The bounded primitive store is Implemented. Production Swift/Kotlin
stores and generated union types passed standalone compiled tests in two fresh
processes per platform, including type preservation, special numeric values,
byte limits, and concurrent capacity claims. Native-host integration and device
reload acceptance remain unverified.

The store holds small string, boolean, and number values for Android/iOS callers.
It does not persist data, deliver analytics, identify devices, or implement
desktop/web/extension
storage. Consumers own key namespaces and their business-specific stage rules.

## Ownership and boundaries

One native singleton owns the Map in each OS process. Main and background JS
runtimes have separate heaps and initialize independently; their DeviceUtils
HybridObjects access that shared singleton. Values are native primitive copies,
never retained JS objects. Reads return a value copy to the calling runtime.
Separate OS processes have separate stores.

## Public API and defaults

All four methods are synchronous:

```ts
type InMemoryValue = string | boolean | number;

getInMemoryValue(key: string): InMemoryValue | undefined;
setInMemoryValue(key: string, value: InMemoryValue): void;
removeInMemoryValue(key: string): boolean;
setInMemoryValueIfAbsent(key: string, value: InMemoryValue): boolean;
```

Keys and string values may be empty strings. Reads preserve the stored primitive
type; false, 0, and empty string are present values, not missing keys. Number
values use IEEE-754 doubles, including negative zero, NaN, and infinities.
Objects, arrays, null, undefined, bigint, and functions are outside the Nitro
contract and are rejected by the bridge before storage mutation. Get returns
undefined for a missing key; set replaces an existing value; remove returns whether a key was present.
Set-if-absent returns true only when it inserts, otherwise false. It validates
both arguments before checking whether the key exists.

## Lifecycle and concurrency

All operations use the same native lock. Set-if-absent checks and inserts under
that lock, so concurrent claims on a missing key have exactly one winner.
JS reload, React remount, HybridObject disposal, and runtime recreation MUST NOT
clear the store. Explicit removal releases a key; OS process exit releases all
state. There are no callbacks, async tasks, cancellation, or implicit eviction.

## Data and identity

Keys use the exact UTF-8 bytes of the native Unicode string; neither platform
normalizes or case-folds them. For example, U+00E9 and U+0065 U+0301 are distinct
keys. There is no disk cache, TTL, migration, or automatic invalidation.

## Platform contract

| Concern | iOS | Android |
| --- | --- | --- |
| Process owner | Static singleton with private initializer | Kotlin object |
| Serialization | NSLock | Synchronized methods |
| Exact key identity | UTF-8 Data dictionary keys | Kotlin String keys |
| Retention and rejection | Shared contract below | Shared contract below |

The API requires a rebuilt native host containing these bindings. Installing
JS code onto an older host is unsupported; this is not an OTA-only API.

## Failure and retention budget

The same fixed limits apply on both platforms:

- At most 128 entries.
- Every key: at most 256 UTF-8 bytes, including keys passed to get/remove.
- Every string value: at most 4096 UTF-8 bytes. Boolean and number values use
  fixed-size native primitive payloads and are not subject to string byte limits.

An overlong key or value throws a synchronous native error. At capacity, writes
to a new key throw; replacing an existing key is allowed, and a valid
set-if-absent call on an existing key returns false. Rejected operations leave
the Map unchanged. Remove frees a slot. No entry is evicted to satisfy a write.
Error messages identify key length, value length, or entry capacity; native
exception types are platform-specific. General allocation/bridge failures may
also propagate to callers.

## Performance and resource budget

Calls perform only bounded key/value validation, a Map operation, and locking;
they do no I/O. Maximum retained logical UTF-8 payload is 557056 bytes
(128 × (256 + 4096)); native string, dictionary, and allocator overhead is extra.
Validation rejects oversized inputs without constructing an unbounded UTF-8
buffer. Bridge conversion and caller-side string allocation occur before these
native limits and are outside the retained-store budget. There is no measured
latency claim.

## Consumer handoff

Startup consumers claim independent namespaced stages only in the main UI
runtime. Native claim failure must not abort subsequent application work.
Release only a successfully claimed stage whose synchronous local enqueue
failed. Retain the claim after the logger accepts the entry; network delivery
is outside this contract. A pending JS logger queue is not durable across
runtime loss, eviction, or later processing errors.

## Conformance and acceptance

- TypeScript: `src/ReactNativeDeviceUtils.nitro.ts` and generated Nitro bindings.
- iOS: `ios/InMemoryStore.swift`, forwarded by `ios/ReactNativeDeviceUtils.swift`.
- Android: `android/src/main/java/com/margelo/nitro/reactnativedeviceutils/`
  `InMemoryStore.kt` and `ReactNativeDeviceUtils.kt`.
- After `yarn nitrogen`, `node tests/in-memory-store.cjs` compiles the production
  stores with the generated union types and exercises primitive type
  preservation, empty/missing values, removal/retry, Unicode key identity, byte limits,
  capacity/rejection atomicity, concurrent claims, and fresh processes.

The Kotlin runner supplies only a marker annotation for the generated shrinker
metadata; it does not substitute the union or storage implementation.
The `device-utils-store` job in `.github/workflows/native-tests.yml` generates
DeviceUtils bindings and runs the Swift store regression. The `android-unit` job
generates the same bindings and runs the Kotlin store regression with Kotlin
2.3.21 and Java 17.

Standalone store tests cannot establish Nitro host integration or real JS
reload behavior. Remaining device acceptance on both Android and iOS: rebuild
the host, claim from main/bg HybridObjects, reload/recreate each runtime,
confirm retained state and independent keys, exercise a rejected write through
the bridge, then restart the OS process and confirm an empty store.

# DeviceUtils: Android navigation bar appearance

## Status and scope

This section covers `setNavigationBarAppearance(r, g, b, a, useDarkIcons)` on
Android. iOS treats the call as a no-op. The behavior below is Implemented.
Emulator acceptance: Android 14 (API 34) with three-button, gesture, and no
navigation bar, and Android 16 (API 36) with three-button and gesture, each in
light and dark themes, including repeated runtime theme switches. Side bars were
checked on an Android 14 phone in landscape: a portrait-locked activity is
letterboxed away from the bar, so the protection receives no inset there.
Physical devices and side bars beside a full-width window remain unverified.

## Platform contract

The call always sets the light navigation bar appearance to `useDarkIcons` and,
on Android 10+, disables the platform contrast scrim. On Android 11+ it writes
`APPEARANCE_LIGHT_NAVIGATION_BARS` through the platform `WindowInsetsController`:
on Android 11-14, `WindowInsetsControllerCompat` left the previous value in place
once the window was appearance-controlled, so runtime theme switches kept stale
button colors. Android 8-10 use the compat controller. How the requested color is
drawn depends on whether the window is edge-to-edge:

| Window | Platform bar color | Requested color |
| --- | --- | --- |
| Edge-to-edge: Android 15+ (enforced), or React Native's edge-to-edge flag on below it | Transparent | Protection view behind three-button navigation only |
| Not edge-to-edge | The requested color | Not drawn separately |

Edge-to-edge below Android 15 is detected through React Native's
`isEdgeToEdgeFeatureFlagOn`, so hosts that opt in with `edgeToEdgeEnabled` get
the same layout as Android 15+. Without this, the opaque platform bar would
cover app content drawn behind it, such as a floating tab bar shadow.

The protection view covers `min(navigationBars, tappableElement)` on the bar's
side: three-button navigation shows it, while gesture navigation and devices
without a navigation bar hide it. It fades from transparent at the content edge
to the requested color over its first third and stays solid to the screen edge,
so it never draws a hard line against the page while the buttons sit on a solid
ground that hides scrolled content.
A later call replaces the color; window inset changes re-measure the side and
size.

The protection view is inserted directly above the React root inside
`android.R.id.content`, below every overlay host that is added later. A dimmed
native overlay therefore covers the bar together with the rest of the screen
instead of leaving the transparent third of the ramp visible over the dim
(OK-66103), and an overlay sheet that paints to the screen edge keeps its own
surface color there. While the React root does not exist yet, the view falls back
to the decor view and is moved into place on the next call. This layering is
Implemented in source and build-verified; rendered-pixel re-acceptance with a
dimmed overlay and an edge-painted sheet is pending on device.

## Conformance and acceptance

- Android: `ReactNativeDeviceUtils.kt` (`applyNavigationBarAppearance`,
  `installNavigationBarProtection`, `placeNavigationBarProtection`,
  `findReactRootIndex`).
- Acceptance is rendered-pixel inspection in the OneKey app with
  `edgeToEdgeEnabled`; see Status for the covered cases. Android 15+ without a
  navigation bar could not be produced on stock emulators.

# DeviceUtils: iOS dual-screen detection

## Status and scope

This section covers the iOS implementation of the existing dual-screen API:
`isDualScreenDevice()`, `isSpanning()`, `addSpanningChangedListener()`, and
`removeSpanningChangedListener()`. It targets foldable iPhones (iPhone Duo).
The TypeScript contract is unchanged; before this change iOS returned `false`
and `0` and ignored listeners. The behavior below is Implemented.

Runtime verified on the iPhone Duo simulator (iOS 27.1, app built with
Xcode 27.1): `isDualScreenDevice()` returns `true` synchronously at launch via
the model identifier, on both the outer and the inner display. On an iPhone 19,2
simulator (iOS 27.3 beta 2) a regular iPhone stays `false` after the
`unknown` hinge update. Unverified: the
hinge path for real fold states (spanning callbacks, detection of an
unlisted model),
behavior on hardware, and builds with an iOS 27.0 SDK.

`getWindowRects()` and `getHingeBounds()` keep their existing iOS results (an
empty array and a zero rect). Detecting the fold region is a non-goal here.

## Definitions and ownership

- **Dual-screen device:** hardware with a hinge between displays.
- **Spanning (iOS):** the hinge is partially or fully open, so the app runs on
  the inner display. This differs from Android, where spanning means a window
  across both screens of a dual-screen device.

One process-wide native detector owns the result, the hinge observer, and the
listener table. The main and background JS runtimes share it through their
DeviceUtils HybridObjects; listener IDs are unique per process.

## Detection and persistence

1. At first use, the model identifier is matched against known foldable
   iPhones (`iPhone19,4`). The simulator reads `SIMULATOR_MODEL_IDENTIFIER`;
   devices read `utsname.machine`.
2. On iOS 27.1+ in builds with the iOS 27.1 SDK (Swift 6.4), a
   `UIHingeInteraction` is attached to the key window on the main thread. If no
   key window exists yet, it waits for `UIWindow.didBecomeKeyNotification`. A
   hinge whose status is `closed`, `partiallyOpen` or `fullyOpen` marks the
   device as dual-screen. A hinge with `unknown` status is not evidence:
   iOS 27.3 beta 2 reports one (status `unknown`, angle 0) on regular iPhones
   once the key window changes. A nil or `unknown` hinge clears spanning
   without clearing the detected device identity.
3. A positive result stores the current model identifier as a string in
   `UserDefaults` key `1k_dual_screen_device_model_v2`. Later launches accept
   the cached positive only when that identifier equals the current device
   model. A different model identifier is ignored, so restoring preferences
   onto a regular iPhone cannot carry over foldable detection. Known foldable
   models rewrite mismatched entries immediately; an unlisted model must be
   confirmed by its hinge before writing a new entry. The previous key
   `1k_is_dual_screen_device` is never read: it accepted `unknown` hinges, so
   its entries may mark regular iPhones as foldable.

Builds without the iOS 27.1 SDK compile only steps 1 and 3. An iOS 27.0 SDK
toolchain that also reports Swift 6.4 is not supported by this guard.

## Lifecycle and callbacks

`isDualScreenDevice()` and `isSpanning()` are synchronous and lock-protected.
`isSpanning()` is `false` until the first hinge update. Listeners fire on the
main thread when spanning changes, and once with the current spanning value
when the hinge first confirms a device that steps 1 and 3 did not. Callers
must re-read `isDualScreenDevice()` in the callback instead of assuming
spanning implies detection. Removing an unknown ID is a no-op. Key-window
notifications are observed for the process lifetime. The hinge interaction
moves to each new key window and detaches when its window resigns key status.
Losing observation or switching windows resets spanning to `false` until the
new window supplies a hinge update; listeners notify only if that changes the
stored value. Callbacks from a previous interaction are ignored.

## Platform contract

| Concern | iOS | Android |
| --- | --- | --- |
| Device detection | Model identifier, then `UIHingeInteraction` | `ReactNativeDeviceUtils.kt` (existing) |
| Spanning meaning | Hinge partially or fully open | Window spans both screens |
| Persistence | `UserDefaults`, positive results only | None |
| Window rects / hinge bounds | Empty / zero (unchanged) | Implemented (existing) |

## Conformance and acceptance

- iOS: `ios/DualScreenDetector.swift`, forwarded by
  `ios/ReactNativeDeviceUtils.swift`.
- No automated native test covers this API; the standalone Swift runner cannot
  link UIKit. Remaining acceptance: a hinge-only detection with the model list
  emptied, spanning callbacks on fold and unfold, the cached result on the next
  launch, and a regular iPhone returning `false`.

# DeviceUtils: window controls insets

## Status and scope

This section covers `getWindowControlsInsets()`,
`addWindowControlsInsetsChangedListener()`, and
`removeWindowControlsInsetsChangedListener()`. They tell JS how much room the
system window controls take in the app's main window, so a custom header row
can keep its content beside them. The behavior below is Implemented.

Runtime verified on an iPad Pro 11-inch (M5) simulator (iOS 27.0, app built
with Xcode 27.1) in a resizable window at 375 x 486 pt and 743 x 1146 pt: the
tracking view is installed, and the consumer header clears the window controls
after resizes. Unverified: listener timing during rapid resizes, iOS 26,
Stage Manager on external displays, multiple windows of one app, iPhone (zero
expected), and builds with an iOS 18 SDK.

Non-goals: positioning content for the controls (consumers own layout), the
vertical corner adaptation, and Android desktop windowing.

## Definitions and ownership

- **Window controls:** system controls drawn inside the app window, such as
  the iPadOS close, minimize, and tile buttons in the top-leading corner of a
  resizable window.
- **Window controls insets:** per edge, UIKit's safe area with horizontal
  corner adaptation minus the plain safe area, clamped at zero. The value is
  in points and excludes the plain safe area, which callers already apply.

One process-wide native observer owns the value, the tracking view, and the
listener table. The main and background JS runtimes share it through their
DeviceUtils HybridObjects; listener IDs are unique per process.

## Public API and defaults

```ts
interface WindowControlsInsets { top: number; left: number; bottom: number; right: number }

getWindowControlsInsets(): WindowControlsInsets;
addWindowControlsInsetsChangedListener(callback: (insets: WindowControlsInsets) => void): number;
removeWindowControlsInsetsChangedListener(id: number): void;
```

- All edges are zero until the observer measures the window, when no controls
  are shown, on iOS before 26, and on Android.
- The getter is synchronous and returns the last measured value.
- Removing an unknown ID is a no-op.

## Lifecycle and callbacks

1. The first call to any of the three methods creates the observer. It installs
   on the main thread and observes `UIWindow.didBecomeKeyNotification` for
   the process lifetime, including when no key window exists yet.
2. It inserts an invisible, non-interactive view spanning the key window,
   behind the root view controller's view. That view recomputes the insets on
   every `layoutSubviews` and `safeAreaInsetsDidChange`, which covers window
   resizes and entering or leaving full screen. When another window becomes
   key, the previous tracking view is removed and tracking attaches to that
   window before recomputing the insets. Callbacks from a detached tracking
   view are ignored.
3. Listeners fire on the main thread, once per changed value, after the stored
   value is updated. Equal values do not notify.

The observer, the tracking view, and the listeners live for the process.
Callers must read the getter after subscribing, because the first measurement
can land before a listener is added.

## Platform contract

| Concern | iOS 26+ | iOS < 26 | Android |
| --- | --- | --- | --- |
| Source | `UIView.edgeInsets(for: .safeArea(cornerAdaptation: .horizontal))` minus `safeAreaInsets` | None | None |
| Value | Measured | All zero | All zero |
| Listeners | Fire on change | Never fire | Accepted, never fire |

The UIKit API requires the iOS 26 SDK (Swift 6.2); older toolchains compile the
all-zero path.

## Performance and resource budget

The work per layout pass is two inset reads and a comparison on the main
thread. Retained state is one view, one value, and the listener table.

## Conformance and acceptance

- iOS: `ios/WindowControlsInsetsObserver.swift`, forwarded by
  `ios/ReactNativeDeviceUtils.swift`.
- Android: `ReactNativeDeviceUtils.kt` (`getWindowControlsInsets`).
- Measured in the simulator: a 375 x 486 pt window has safe area
  `{10, 0, 10, 0}` and horizontally adapted `{10, 66, 10, 9.5}`; a 743 x 1146 pt
  window has `{32, 0, 10, 0}` and `{32, 66, 10, 9.5}`. Both give insets of
  `{0, 66, 0, 9.5}`.
- No automated native test covers this API; the standalone Swift runner cannot
  host a UIKit window. Remaining acceptance: values after rapid window resizes,
  iOS 26, and an iPhone returning zero.

# DeviceUtils: interface orientation

## Status and scope

This section covers `getInterfaceOrientation()`,
`addInterfaceOrientationChangedListener()`, and
`removeInterfaceOrientationChangedListener()`. They tell JS which way the app's
window scene is turned, so landscape layout can tell the side that holds an
iPhone's sensor housing from the side that does not. The behavior below is
Implemented.

Runtime verified on an iPhone 16 Pro simulator (iOS 26.5, app built with
Xcode 27.1) in OneKey's native chart fullscreen, a presented screen that forces
landscape over a portrait-locked root: the value is `landscapeRight` when the
forced rotation starts from a portrait device and `landscapeLeft` when the
device is already turned the other way; turning the device 180 degrees in
landscape notifies `landscapeLeft` and back `landscapeRight`. In the same app,
`expo-screen-orientation` reported `unknown` for the forced rotation, because
its root view controller does not take part in it. The Android stubs compile
in the OneKey app (`compileDebugKotlin`). Unverified: hardware, iOS 15
returning `unknown`, and multiple scenes.

Non-goals: device (physical) orientation, orientation locks, and layout. The
consumer decides what each orientation means for its content.

## Definitions and ownership

- **Interface orientation:** the orientation of the key window's
  `UIWindowScene`, as `UIWindowScene.effectiveGeometry.interfaceOrientation`
  reports it. It follows every rotation the scene takes, including one a
  presented view controller forces while the root view controller keeps its
  own orientation. The device orientation cannot stand in for it: a window can
  turn to landscape while the device lies flat or is held upright.
- Names follow UIKit. `landscapeRight` turns the top of the device to the left
  edge of the screen, so an iPhone's sensor housing is on the left;
  `landscapeLeft` puts it on the right. In landscape, iPhones report equal
  left and right safe-area insets, so the insets do not tell the sides apart.

One process-wide native observer owns the value, the scene observation, and
the listener table. The main and background JS runtimes share it through their
DeviceUtils HybridObjects; listener IDs are unique per process. Each listener
belongs to the HybridObject that added it: when that object is released, for
example when its JS runtime is torn down without removing its listeners, they
are removed with it.

## Public API and defaults

```ts
type InterfaceOrientation =
  | 'portrait'
  | 'portraitUpsideDown'
  | 'landscapeLeft'
  | 'landscapeRight'
  | 'unknown';

getInterfaceOrientation(): InterfaceOrientation;
addInterfaceOrientationChangedListener(callback: (orientation: InterfaceOrientation) => void): number;
removeInterfaceOrientationChangedListener(id: number): void;
```

- The value is `unknown` until the observer reads a scene, on iOS before 16,
  and on Android.
- The getter is synchronous and returns the last observed value.
- Removing an unknown ID is a no-op.

## Lifecycle and callbacks

1. The first call to any of the three methods creates the observer. On iOS
   16+ it installs on the main thread and observes
   `UIWindow.didBecomeKeyNotification` for the process lifetime, including when
   no key window exists yet.
2. It observes `effectiveGeometry` of the key window's scene with key-value
   observing, starting with the current value. When a window of another scene
   becomes key, observation moves to that scene; a window of the same scene
   becoming key keeps the existing observation. Callbacks from a previously
   observed scene are ignored. When the observed scene disconnects, observation
   moves to the key window of another scene, because that window may stay key
   without posting the notification again; with no such window the value
   becomes `unknown`.
3. Listeners fire on the main thread, once per changed value, after the stored
   value is updated. Equal values do not notify. A rotation by 180 degrees
   (landscape left to landscape right) notifies even though the window size
   does not change.

The observer and the observation live for the process. Listeners live until
they are removed or their HybridObject is released. Callers must read the
getter after subscribing, because the first reading can land before a listener
is added.

## Platform contract

| Concern | iOS 16+ | iOS < 16 | Android |
| --- | --- | --- | --- |
| Source | `UIWindowScene.effectiveGeometry.interfaceOrientation` (KVO) | None | None |
| Value | Observed | `unknown` | `unknown` |
| Listeners | Fire on change | Never fire | Accepted, never fire |

Android needs no equivalent today: its window insets report a display cutout
only on the side that has it.

## Performance and resource budget

The work per scene geometry change is one enum mapping and a comparison on the
main thread. Retained state is one key-value observation, one value, and the
listener table.

## Conformance and acceptance

- iOS: `ios/InterfaceOrientationObserver.swift`, forwarded by
  `ios/ReactNativeDeviceUtils.swift`.
- Android: `ReactNativeDeviceUtils.kt` (`getInterfaceOrientation`).
- `ios/tests/InterfaceOrientationObserverTests.swift` (pod test target) drives
  the value through `receive(_:)`: each landscape side notifies once, a
  repeated value does not notify, every UIKit orientation maps to its value,
  and removed listeners and the listeners of a released owner are not called.
  It also releases a `ReactNativeDeviceUtils` that still has a listener and
  checks that the listener's callback is released with it. It does not cover
  the scene observation, which needs a UIKit window scene.
- Runtime verified: the value after a presented controller forces landscape
  from a portrait-locked root, from either device side, and 180-degree
  rotations while in landscape. Remaining acceptance: the return to portrait on
  hardware, iOS 15 returning `unknown`, a scene disconnect with multiple
  scenes, and a JS reload releasing DeviceUtils on a device.

# DeviceUtils: main menu

## Status and scope

This section covers `setMainMenuConfig()`, `addMainMenuCommandListener()`, and
`removeMainMenuCommandListener()`, which let JS give the iPadOS menu bar
localized app commands and Help links. The behavior below is Implemented;
runtime status is in Conformance.

Non-goals: Edit, View, and Window commands (the system owns them), menus of
secondary windows, and context menus.

## Definitions and ownership

- **Main menu:** the `UIMenuSystem.main` menu, shown as the iPadOS menu bar
  when the app runs in a window with one.
- **App command:** an item JS performs: `about`, `preferences`, or `lockNow`.

One process-wide controller owns the last config and the listener table. The
host app's delegate MUST forward `buildMenu(with:)` to
`MainMenuController.buildMenu(withBuilder:)` (Objective-C selector
`buildMenuWithBuilder:`); app targets that cannot import Nitro modules reach
it through `NSClassFromString("ReactNativeDeviceUtils.MainMenuController")`.

## Public API and defaults

```ts
type MainMenuCommand = 'about' | 'preferences' | 'lockNow';
interface MainMenuLink { title: string; url: string }
interface MainMenuConfig {
  aboutTitle: string; preferencesTitle: string; lockNowTitle: string;
  helpLinks: MainMenuLink[];
}

setMainMenuConfig(config: MainMenuConfig): void;
addMainMenuCommandListener(callback: (command: MainMenuCommand) => void): number;
removeMainMenuCommandListener(id: number): void;
```

The built main menu:

| Menu | Content |
| --- | --- |
| App | About (`aboutTitle`; removed when empty), then Preferences (`preferencesTitle`, Command-Comma) and Lock Now (`lockNowTitle`, Command-Shift-L). Preferences replaces the system item that opens the app's page in the Settings app. System Hide items stay. |
| File, Format | Removed. |
| Edit, View, Window | System content, unchanged. |
| Help | One item per help link, in order; each opens its URL with `UIApplication.open`. Removed when `helpLinks` is empty. |

- Before the first config, File and Format are removed and the app and Help
  menus keep their system content.
- A later config replaces the previous one and rebuilds the menu.
- A help link whose URL does not parse is dropped and logged; an empty link
  list keeps the system Help menu.
- Removing an unknown listener ID is a no-op.

## Lifecycle and callbacks

`setMainMenuConfig` stores the config under a lock and asks
`UIMenuSystem.main` to rebuild on the main thread. App commands are
`UICommand`/`UIKeyCommand`s whose action, `onekeyPerformMainMenuCommand:`, is
added to `UIApplication` (always in the responder chain); it notifies every
listener on the main thread with the command. Commands picked while no
listener is registered are dropped.

## Platform contract

| Concern | iOS | Android |
| --- | --- | --- |
| Menu | `UIMenuBuilder` in `buildMenu(with:)` | None |
| `setMainMenuConfig` | Stores and rebuilds | Ignored |
| Listeners | Fire on app commands | Accepted, never fire |

The menu bar is visible only where iPadOS shows one (resizable windows); the
config is harmless elsewhere.

## Conformance and acceptance

- iOS: `ios/MainMenuController.swift`, forwarded by
  `ios/ReactNativeDeviceUtils.swift`; the host app forwards `buildMenu(with:)`.
- Android: `ReactNativeDeviceUtils.kt` (`setMainMenuConfig`).
- No automated native test covers this API. Runtime acceptance: see the
  consumer verification; remaining: localization switches at runtime,
  keyboard shortcuts with a hardware keyboard, and iOS 26.

# DeviceUtils: Apple Ads attribution token

## Scope and platform contract

`getAppleAdsAttributionToken(): Promise<string | undefined>` requests an Apple
Ads token from AdServices on iOS 14.3 and later. It resolves to a non-empty
string or rejects if AdServices fails or returns an empty token. Android and
older iOS versions resolve to `undefined`.

Each call runs independently. The iOS request runs off the main thread and
returns only to the calling JS runtime. DeviceUtils does not retain or log the
token. The OneKey app owns main-runtime and App Store eligibility, the server
exchange, retries, and attribution reporting.

## Conformance and acceptance

The contract is defined in `src/ReactNativeDeviceUtils.nitro.ts` and implemented
in `ios/ReactNativeDeviceUtils.swift` and `ReactNativeDeviceUtils.kt`. The iOS
pod's XCTest suite injects a provider and exercises the Nitro Promise with a
non-empty string, an empty string, and a thrown error. Android native CI builds
the Kotlin implementation and generated JNI bindings. The consumer's Android
native harness verifies the result is JavaScript `undefined`. Real AdServices
token issuance requires an on-device check.

# DeviceUtils: SKAdNetwork conversion value

## Scope and platform contract

`markSkanMilestones(bits, coarseRule): Promise<SkanUpdateResult | undefined>`
ORs `bits` (an integer from 0 to 63), pending bits, and the last accepted
SKAdNetwork fine conversion value. On iOS 16.1 and later it then calls
`SKAdNetwork.updatePostbackConversionValue(_:coarseValue:lockWindow:completionHandler:)`
with `lockWindow: false`. Android and older iOS resolve to `undefined`.

The coarse value is `high` when the merged value shares a bit with
`coarseRule.highMask`. Otherwise it is `medium` when it shares a bit with
`coarseRule.mediumMask`, and `low` in every other case. Both masks are integers
from 0 to 63. The OneKey app owns the milestone bit schema and the masks, and
must keep them in line with the conversion value schema configured for the ad
network. DeviceUtils assigns no meaning to individual bits.

Supported iOS calls submit to StoreKit even with `bits` set to 0 or with the
same fine and coarse values as the last report, subject to the validation and
in-flight rules below.
SKAdNetwork 4 requires an update during each conversion window for multiple
postbacks. StoreKit manages the windows, and the app must call this API during
each window in which it wants to submit an update.

The result carries the submitted `fineValue`, its `coarseValue`, and
`updated: true` after StoreKit reports success. StoreKit determines which
values appear in a postback. SKAdNetwork 4 uses only coarse values in the
second and third windows.

## Lifecycle and concurrency

The main and background React runtimes each create their own HybridObject.
Every call goes through one process-wide store with a serial queue, so the
read, merge, report and persist steps run as one unit. Calls from either
runtime are applied in arrival order, and the fine value includes every bit
from prior accepted submissions. The queue waits for the SKAdNetwork
completion handler for up to 30 seconds. A timed-out submission remains in
flight until StoreKit completes it. While it is pending, subsequent valid
calls retain their new bits and reject without making another submission.
A late success persists the accepted fine value and clears the bits it
reports from pending storage before submissions resume. A late failure
leaves all unreported bits pending. If StoreKit never completes a timed-out
submission, only a process restart resets this guard.

## Data and retention

The last accepted fine value lives in `UserDefaults.standard` under
`so.onekey.deviceutils.skan.fineValue`. It is written only after SKAdNetwork
accepts the update. Valid milestone bits are retained separately under
`so.onekey.deviceutils.skan.pendingBits` before submission. An accepted value
is written before its bits are cleared from pending storage. Reports merge
the two values with bitwise OR, so replaying any leftover bits is safe.
Pending bits survive a process restart. The coarse value is derived on every
call from the merged fine value and the caller's current masks. Deleting the
app clears both stored values. The values hold no user or wallet identifiers
and are not logged.

## Failure

The Promise rejects for invalid inputs, StoreKit errors, a 30-second timeout,
or a pending timed-out submission. The last accepted fine value stays in
storage until StoreKit confirms a successful update, including a late
success. The next submission includes the pending bits, even when the caller
passes `bits: 0`. Invalid calls leave storage unchanged.

## Conformance and acceptance

The contract is defined in `src/ReactNativeDeviceUtils.nitro.ts` and implemented
in `ios/SkanConversionStore.swift`, `ios/ReactNativeDeviceUtils.swift` and
`ReactNativeDeviceUtils.kt`. The iOS pod's XCTest suite injects a reporter and
an isolated `UserDefaults` suite. It covers bit merging, coarse mapping, the
same fine and coarse values submitted in consecutive simulated windows, zero
values, a coarse rule change, retry after a rejected report and store
recreation, input validation, a timeout with late completion, and concurrent
callers. Android native CI builds the Kotlin stub. Confirming real postbacks
across windows requires an eligible ad attribution on a test device.
