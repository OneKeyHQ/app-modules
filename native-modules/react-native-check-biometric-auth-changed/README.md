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
  stores that state as the baseline for subsequent calls.
- The first available state establishes a baseline unless legacy stored state
  exists. A missing current domain state returns `false`.
- Android currently returns `false` from a stub implementation. It does not
  detect enrollment changes.

A `false` result is not proof that biometrics are available or that the user has
authenticated: it can also mean no comparison was possible. This API does not
replace an authentication challenge, and has no Web implementation.

The wrapper is in [src/index.tsx](src/index.tsx); its native interface is
[src/ReactNativeCheckBiometricAuthChanged.nitro.ts](src/ReactNativeCheckBiometricAuthChanged.nitro.ts).
Review the platform sources when integrating this signal into an authentication
workflow.

## License

MIT.
