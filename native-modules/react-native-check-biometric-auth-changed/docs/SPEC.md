# Biometric authentication state contract

Status: Implemented. Ten deterministic Swift core scenarios and the iOS Simulator
module build pass; real device Keychain/LocalAuthentication acceptance is pending.

## Purpose and ownership

`checkBiometricAuthChanged(): Promise<boolean>` compares the current iOS
LocalAuthentication domain state with a saved comparison baseline. The application
owns authentication challenges and the security policy applied to the result.
The package neither authenticates a user nor authorizes a password operation.
The Keychain baseline is shared within the native process, including independent
JavaScript runtimes. It is not a JavaScript-runtime-local store.

## API and platform behavior

- iOS resolves `true` when a previous Keychain or legacy baseline differs from
  the current domain state, and `false` when it matches or when a first baseline
  is successfully established. Every successful comparison persists the current
  state as the next baseline before resolving.
- `false` is not proof of authentication or biometric availability. Callers MUST
  distinguish promise rejection from a resolved `false` and MUST NOT continue a
  biometric-only path when the state check is unavailable.
- Android retains its existing stub: it resolves `false`, does not persist a
  baseline and does not detect enrollment changes. Web is unsupported.
- No new JavaScript return type, event or configuration API is introduced.

## Transaction, migration and concurrency

A synchronous process-wide lock serializes the complete iOS operation: obtain
current state, read the saved state, compare and persist. Work runs through Nitro `Promise.parallel` on its default global dispatch queue,
not on the UI thread or an inherited Swift task executor. Lock release is required on every exit.
The opaque domain state is neither returned to JavaScript nor logged.

Only `errSecItemNotFound` means the Keychain baseline is absent. In that case,
legacy `UserDefaults["biometricAuthState"]` participates in the comparison.
The legacy value is removed only after current-state persistence succeeds;
read/write failure MUST leave it intact. A first-use result can resolve `false`
only after persistence succeeds. Updating the Keychain item may fall back to add
only on `errSecItemNotFound`; other update errors MUST NOT attempt an add.

## Failure and safety

A missing current domain state, a Keychain read failure, successful read with
non-Data result, update failure or add failure rejects the promise. These cases
MUST NOT silently become an absent baseline or a resolved `false`. Keychain
failures include operation and OSStatus in the error message, without state data;
errors use the `BiometricAuthChanged` NSError domain. Unavailable current state
uses code 1; malformed successful reads use `errSecDecode`; Keychain operations
retain their OSStatus code. Native error transport need not expose NSError fields
as stable JavaScript properties.

Changing this failure contract does not add enrollment notification, cancellation,
late-loading recovery, or a new biometric prompt. Consumers can keep password or
passcode authentication available when the state check fails. Already-published
3.0.165 artifacts remain unchanged; the new native contract takes effect only
when the SDK is published, upgraded and rebuilt by the application.

## Conformance and acceptance

The iOS wrapper owns LocalAuthentication and Security calls. A small state core
uses injected current-state, Keychain and legacy operations so deterministic Swift
executable tests exercise the actual transaction and error handling without
altering a user's Keychain. Checks cover read errors/type mismatch, nil state,
update/add failures, successful absent baseline, changed/unchanged comparisons,
legacy preservation/removal and serialized concurrent checks.

A real package build and tarball must contain the core and this spec, preserve the
Nitro boolean signature, and resolve the existing native module name. App tests
must prove that rejection blocks automatic, manual and foreground biometric
attempts while password entry remains available, without falsely disabling the
persistent biometric switch or showing an enrollment-change warning.

Actual iOS device Keychain/LocalAuthentication behavior, protected-data states,
biometric enrollment changes and App authentication UI remain runtime acceptance
requirements; deterministic injected-operation tests do not establish those.
