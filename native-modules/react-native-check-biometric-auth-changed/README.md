# @onekeyfe/react-native-check-biometric-auth-changed

Detect changes to the stored biometric authentication domain state on iOS.
OneKey uses this native signal to decide whether previously accepted biometric
state needs to be reconsidered. The package reports a change; the application
owns any authentication prompt and its subsequent security policy.

## Installation

```sh
yarn add @onekeyfe/react-native-check-biometric-auth-changed react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application after
adding the package. The exported function requires its Nitro native module.

## Usage

```ts
import { checkBiometricAuthChanged } from '@onekeyfe/react-native-check-biometric-auth-changed';

const changed = await checkBiometricAuthChanged();
if (changed) {
  // Apply the application's existing biometric re-enrollment policy.
}
```

## Platform behavior

- iOS reads LocalAuthentication's evaluated policy domain state and compares it
  with the previous value stored in Keychain. A check with available state
  successfully saves that state as the baseline before resolving the current call.
- The first available state establishes a baseline unless legacy stored state
  exists. Missing current domain state or failed Keychain reads/writes reject.
  Only `errSecItemNotFound` means an absent stored baseline; legacy state is
  deleted only after the new baseline has been saved successfully.
- Android currently returns `false` from a stub implementation. It does not
  detect enrollment changes.

A `false` result is not proof that biometrics are available or that the user has
authenticated: on iOS it can also mean a first baseline was established. This API does not
replace an authentication challenge, and has no Web implementation.

The wrapper is in [src/index.tsx](src/index.tsx); its native interface is
[src/ReactNativeCheckBiometricAuthChanged.nitro.ts](src/ReactNativeCheckBiometricAuthChanged.nitro.ts).
Review the platform sources when integrating this signal into an authentication
workflow.

## Interpreting repeated checks

`checkBiometricAuthChanged()` returns only a boolean: there is no enrollment
count, authentication credential or event subscription in this API. On iOS the
check persists an available comparison baseline before resolving. A process-wide
synchronous transaction lock serializes checks on the native background queue;
callers still own authentication and UI policy.

A changed result is consumed by that baseline update. Persist a checkpoint before
checking, and retain it if the application cannot finish disabling biometrics.
After an interrupted check, require password/passcode recovery rather than
trusting a fresh `false`; see the transaction requirements in the contract.

Handle native-operation rejection separately from a resolved `false`. Neither
an Android stub result nor an iOS rejection establishes that enrollment
has remained unchanged. Applications must handle rejection as unavailable, rather
than substituting `false`. See [the contract](docs/SPEC.md) for error and migration
boundaries. These iOS changes require a new SDK release and an application rebuild. The package exports the Nitro interface as a type for
adapters; the public JavaScript call remains `checkBiometricAuthChanged()`.

## License

MIT.

## Focused state tests

From this package directory in a repository source checkout on macOS with the
Xcode command-line tools, run the actual state transaction core
with deterministic provider failures (without accessing a real Keychain):

```sh
xcrun swiftc ios/BiometricAuthStateStore.swift tests/BiometricAuthStateStoreTests.swift -o /tmp/biometric-state-tests
/tmp/biometric-state-tests
```

The runner checks eleven scenarios, including concurrent transactions. It does not
replace device enrollment, locked-Keychain or native bridge acceptance tests.
