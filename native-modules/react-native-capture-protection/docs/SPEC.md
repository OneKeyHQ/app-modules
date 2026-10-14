# Capture protection contract

Status: Implemented. Native runtime acceptance is pending.

## Purpose and scope

Protect wallet-sensitive screens without photo-library or file access. Support
OneKey iOS/Android callers of `prevent()`, `allow()` and `addListener()`. This is
not the full upstream API: providers, hooks, custom text/image overlays,
permission requests and media-library screenshot detection are not exported.
Web/desktop callers use their existing platform hooks and do not load this module.

## Ownership and boundaries

The OS window and protection state belong to a process-wide native coordinator.
Each Nitro object has its own owner identifier and callback; main and background
JS runtimes have separate objects/heaps and initialize independently. One owner's
`allow()` must not release another owner's protection. JS callbacks remain scoped
to their owning object/runtime. No image bytes, album entries, file paths or
product secrets enter this module. Only the UI runtime uses it in the app.

## Public API and defaults

- `CaptureProtection.prevent(): Promise<void>` enables screenshot, recording and
  app-switcher protection together. Repeated calls by one object are idempotent.
- `CaptureProtection.allow(): Promise<void>` releases only that object's protection.
- `CaptureProtection.addListener(callback): { remove(): void }` registers a local
  subscriber; removal is idempotent. At most 128 JS subscribers per runtime.
- `CaptureEventType`: NONE=0, RECORDING=1, END_RECORDING=2, CAPTURED=3,
  APP_SWITCHING=4, UNKNOWN=5, ALLOW=8, PREVENT_SCREEN_CAPTURE=16,
  PREVENT_SCREEN_RECORDING=32, PREVENT_SCREEN_APP_SWITCHING=64.
- Nitro methods are `prevent`, `allow`, `setListener(callback | undefined)`.
  Protection failures reject; missing native registration throws instead of
  silently pretending sensitive screens are protected.

## Lifecycle and concurrency

All native state and window operations run on the UI thread in dispatch order.
There is at most one native subscription per object; JS fans events out to its
current subscribers. Removing the final subscriber releases its native callback.
Native observers start on first owner/subscriber and stop on the last. Callbacks
are weakly connected to objects; disposal releases that owner's state and listener.
Android resolves the foreground activity through the application UI ReactHost
and lifecycle tracking, never Nitro's last-initialized global React context.
Android reapplies FLAG_SECURE on activity resume while protection is requested,
unregisters screenshot callbacks on pause and unregisters activity/display
observers when no owners/subscribers remain. iOS reload notification resets
obsolete owners and observers. Protection is fail-closed across activity pause;
activity destruction does not clear the owners of still-running JS runtimes.
Events reflect OS notifications, not screenshot image contents. No deduplication
or 1-second artificial event reset is added; the existing app hook debounces warnings.

## Data, cache and identity

Only in-memory owner IDs, listeners, observer registrations and overlay windows
are retained. No storage, persistence, cache, migrations or network access.

## Platform contract

| Behavior              | iOS                                                 | Android                                               |
| --------------------- | --------------------------------------------------- | ----------------------------------------------------- |
| Screenshot protection | Upstream-derived secure UITextField layer technique | FLAG_SECURE                                           |
| Recording protection  | Secure layer plus opaque window while captured      | FLAG_SECURE                                           |
| App switcher          | Opaque window before resign/background              | FLAG_SECURE                                           |
| Screenshot event      | UIApplication notification                          | Android 14+ system callback                           |
| Recording event       | UIScreen capture change notification                | Upstream-derived virtual-display heuristic            |
| Permissions           | None                                                | DETECT_SCREEN_CAPTURE, normal permission, Android 14+ |

Android below 14 intentionally has no screenshot notification; blocking remains
available. Android virtual-display events are a heuristic and may also represent
casting/external displays; they are not proof that a recording was made.
iOS secure-text-field wrapping is inherited from upstream, is not a documented
system-wide screenshot prevention API, and requires OS-version/device acceptance.
Existing recordings when subscribing emit RECORDING when identifiable. Existing
Android private virtual displays may not be visible to DisplayManager enumeration.

## Failure, fallback and safety

Missing foreground activity/window or unavailable secure layer rejects prevent.
Never fall back to MediaStore queries, filesystem scanning or storage permissions.
Screenshot observer errors must not disable FLAG_SECURE. Only FLAG_SECURE flags
introduced by this coordinator may be cleared; preserve pre-existing flags.
A native coordinator admits at most 32 owner/subscriber identities, rejecting
excess registration. No custom overlays, bytes or unbounded event queues.

## Performance and resources

UI work is limited to flags, notifications, layer configuration and at most two
opaque windows on iOS. No polling, I/O, image processing or detached coroutines.
Recording/display changes are event-driven. Listener registration synchronously marshals to the UI thread; Android rejects
a wait exceeding 5 seconds. Protection operations return promises. No performance
claim is made.

## Conformance and acceptance

Source: `src/index.tsx`, `ios/ReactNativeCaptureProtection.swift` and Android
`ReactNativeCaptureProtection.kt`. JS tests cover listener fan-out/removal and
missing registration. iOS XCTest is provided for independent object subscriptions,
disposal and secure-layer owner isolation; physical capture acceptance remains
separate.
Required runtime acceptance: prevent/allow, nested sensitive pages, repeated
navigation, recording started before/after subscription, background snapshots,
activity recreation, subscription cleanup, and main/background object isolation.
Inspect merged Android manifest and packaged iOS plist for media-read permissions.
Build/type/unit results do not prove screenshot prevention on physical devices.

## Template deviations and attribution

Generated from `yarn create:module react-native-capture-protection`. Remove the
hello example and unused Babel module-resolver alias only; retain template build
conventions. The alias is not used by this package and the template does not
declare its Babel plugin. Match the repository React Native patched dependency. Use the repository
FlatCompat ESLint pattern because the template calls an unavailable
createEslintConfig export. The XCTest spec requires an app host for UIKit tests.
Add SPEC, focused tests,
ACKNOWLEDGEMENTS and the upstream MIT notice. See ACKNOWLEDGEMENTS.md for source
provenance and the precise subset adapted.
