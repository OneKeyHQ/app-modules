<div align="center">

# @onekeyfe/react-native-check-biometric-auth-changed

**Detect changes to the stored iOS biometric authentication state before trusting an existing biometric setting.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-check-biometric-auth-changed?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-check-biometric-auth-changed)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-check-biometric-auth-changed?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Compares the current iOS LocalAuthentication domain state with a persisted
Keychain baseline and resolves whether it changed. OneKey uses the signal to
decide whether a previously accepted biometric setting needs to be reconsidered.
The package never authenticates a user, prompts for biometrics or authorizes a
password operation — the application owns the authentication challenge and the
policy applied to the result. This is an iOS-only signal; Android is a stub and
Web is unsupported.

## Features

- **One boolean signal** — `checkBiometricAuthChanged()` answers whether the
  stored biometric domain state differs from the saved baseline.
- **Keychain-backed baseline** — the baseline is persisted in the Keychain and
  shared process-wide, including across independent JavaScript runtimes.
- **Consumptive comparison** — a successful check always advances the baseline,
  so the same changed state reports `false` on the next check.
- **Fail-closed errors** — missing domain state or Keychain read/write failures
  reject instead of silently resolving `false`.
- **Serialized transaction** — one process-wide lock covers read, compare and
  persist, and the work runs off the UI thread.
- **Legacy-safe migration** — a legacy `UserDefaults` baseline participates in
  comparison and is removed only after the new baseline is saved.

## Installation

```sh
yarn add @onekeyfe/react-native-check-biometric-auth-changed react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application; this is a
Nitro module, so a JavaScript-only install does not register it. On iOS the pod
also depends on `ReactNativeNativeLogger`, so include
`@onekeyfe/react-native-native-logger` in the application's pods. No permission or
`Info.plist` usage-description key is required because the package never prompts
for biometrics.

## Usage

```ts
import { checkBiometricAuthChanged } from '@onekeyfe/react-native-check-biometric-auth-changed';

try {
  const changed = await checkBiometricAuthChanged();
  if (changed) {
    // Apply the application's existing biometric re-enrollment policy.
  }
} catch {
  // The check is unavailable; keep password authentication available and do not
  // treat this as "unchanged".
}
```

Persist an application checkpoint before checking, and resolve a changed or
unavailable result with password/passcode verification.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `checkBiometricAuthChanged()` | `() => Promise<boolean>` | iOS | Resolves `true` when the domain state differs from the stored baseline, `false` when it matches or a first baseline was established; rejects on missing state or Keychain failure. |

There is no enrollment count, credential, event subscription or configuration
option. The native interface is in
[`src/ReactNativeCheckBiometricAuthChanged.nitro.ts`](src/ReactNativeCheckBiometricAuthChanged.nitro.ts)
and [`docs/SPEC.md`](docs/SPEC.md) defines the full failure and transaction
contract.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Detect biometric domain-state change | ✅ | — | — |
| Persisted Keychain baseline | ✅ | — | — |
| Authenticate or prompt the user | — | — | — |

## Caveats

> [!IMPORTANT]
> `false` is not proof that biometrics are available or that the user
> authenticated — on iOS it can also mean a first baseline was just established.
> A rejection means the check is unavailable; never substitute `false` for it.

> [!WARNING]
> The comparison is consumptive: a `true` result advances the baseline, so the
> change is reported once. A security-sensitive caller must durably checkpoint
> that a check is in progress and require password/passcode recovery after an
> interrupted check; JavaScript component state alone is not sufficient.

> [!NOTE]
> Android ships a stub that always resolves `false` and detects no enrollment
> changes, and there is no Web implementation. The package only reports a state
> change; it does not disable biometrics or change the application's switch.

## References

- [Package spec](docs/SPEC.md)
- [Example page](../../example/react-native/pages/BiometricAuthTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
