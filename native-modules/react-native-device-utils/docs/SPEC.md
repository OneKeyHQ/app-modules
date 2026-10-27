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
DeviceUtils bindings and runs this runner with Swift, Kotlin 2.3.21, and Java 17.

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

## Conformance and acceptance

- Android: `ReactNativeDeviceUtils.kt` (`applyNavigationBarAppearance`,
  `installNavigationBarProtection`).
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
the model identifier, on both the outer and the inner display. Unverified: the
hinge path (status values, spanning callbacks, detection of an unlisted model),
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
   non-nil hinge marks the device as dual-screen; a nil hinge keeps the last
   known state.
3. A positive result is written to `UserDefaults` key
   `1k_is_dual_screen_device` and read synchronously on later launches. It is
   never written as `false` and has no invalidation; reinstalling the app clears
   it.

Builds without the iOS 27.1 SDK compile only steps 1 and 3. An iOS 27.0 SDK
toolchain that also reports Swift 6.4 is not supported by this guard.

## Lifecycle and callbacks

`isDualScreenDevice()` and `isSpanning()` are synchronous and lock-protected.
`isSpanning()` is `false` until the first hinge update. Listeners fire on the
main thread when spanning changes, and once with the current spanning value
when the hinge first confirms a device that steps 1 and 3 did not. Callers
must re-read `isDualScreenDevice()` in the callback instead of assuming
spanning implies detection. Removing an unknown ID is a no-op. The hinge
observer lives for the process.

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
   on the main thread; if no key window exists yet it waits for
   `UIWindow.didBecomeKeyNotification`.
2. It inserts an invisible, non-interactive view spanning the key window,
   behind the root view controller's view. That view recomputes the insets on
   every `layoutSubviews` and `safeAreaInsetsDidChange`, which covers window
   resizes and entering or leaving full screen.
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
