<div align="center">

# @onekeyfe/react-native-lite-card

**OneKey Lite NFC card operations — inspect, back up and restore, change the PIN — from React Native.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-lite-card?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-lite-card)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-lite-card?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

A TurboModule that drives the OneKey Lite NFC card: card information, mnemonic
backup and retrieval, PIN changes and reset. Native callbacks are wrapped in
promises and every result carries the native error, data and card state instead
of throwing on card-level failures. The recovered GPChannel core is compiled from
source in [`sdk/`](sdk/README.md) — no precompiled framework or JNI library ships
in the package. The application owns the user flow and the handling of mnemonic
and PIN values.

## Features

- **Card inspection** — `getLiteInfo()` reports the serial, backup state, new-card
  state and remaining PIN retries.
- **Full backup lifecycle** — write a mnemonic with an overwrite choice, read it
  back with the PIN, change the PIN and reset the card.
- **Optional write identity guard** — pass `expectedSerial` to `setMnemonic` or
  `changePin` and a mismatch with the freshly read card returns `DeviceMismatch`
  before any PIN or write phase.
- **Connection UI events** — `addConnectListener` streams the native NFC prompt
  state and returns a subscription with `remove()`.
- **Auditable native source** — the SDK, iOS bridge and Android JNI build from
  checked-in C/C++/Objective-C/Kotlin, with debug logging disabled in recovered
  builds.

## Installation

```sh
yarn add @onekeyfe/react-native-lite-card
```

Install the native peer packages this package links as well:

```sh
yarn add @onekeyfe/react-native-native-logger
```

The package ships native code and changes the TurboModule ABI, so install the
application's iOS pods and rebuild the native binary; a JavaScript-only update
cannot add it.

### iOS

Add `NFCReaderUsageDescription` to `Info.plist` and enable the Near Field
Communication Tag Reading capability (the reader-session entitlement) for the
app target. The package compiles its SDK sources through CocoaPods.

### Android

The host application declares `<uses-permission android:name="android.permission.NFC" />`
and an NFC-capable device is required. The package builds its JNI library from
the same recovered sources with CMake.

NFC hardware, permissions and host configuration cannot be exercised from a
JavaScript-only development session.

## Usage

```ts
import onekeyLite from '@onekeyfe/react-native-lite-card';

const subscription = onekeyLite.addConnectListener(({ code, message }) => {
  // Render the native NFC prompt state in the app's sheet.
});

const permission = await onekeyLite.checkNFCPermission();
if (permission.error) {
  // Compare permission.error.code against CardErrors.
} else if (permission.data) {
  const { error, data: cardInfo } = await onekeyLite.getLiteInfo();
  if (!error && cardInfo) {
    // cardInfo.serialNum / hasBackup / isNewCard / pinRetryCount
  }
}

subscription.remove(); // when the owner finishes
```

Never log mnemonics or PINs.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `getLiteInfo()` | `Promise<PromiseResult<CardInfo>>` | iOS, Android | Reads serial, backup, new-card and PIN-retry state. |
| `checkNFCPermission()` | `Promise<PromiseResult<boolean>>` | iOS, Android | Whether NFC is available and authorized. |
| `setMnemonic(mnemonic, pwd, overwrite?, expectedSerial?)` | `Promise<PromiseResult<boolean>>` | iOS, Android | Writes the backup; `overwrite` defaults to `false`. |
| `getMnemonicWithPin(pwd)` | `Promise<PromiseResult<string>>` | iOS, Android | Reads the backup with the card PIN. |
| `changePin(oldPin, newPin, expectedSerial?)` | `Promise<PromiseResult<boolean>>` | iOS, Android | Changes the PIN, optionally guarded by card serial. |
| `reset()` | `Promise<PromiseResult<boolean>>` | iOS, Android | Resets the card to its initial state. |
| `addConnectListener(listener)` | subscription with `remove()` | iOS, Android | NFC connection UI events (`code`, `message`). |
| `addAccordListener()` | subscription | iOS, Android | Native NFC active-connection event. |
| `cancel()` | `void` | Android | Cancels the current native operation. |
| `intoSetting()` | `void` | iOS, Android | Opens platform settings (Android native NFC settings, iOS app settings). |
| `CardErrors` | enum | iOS, Android | Native error codes (`1000`–`4002`). |

`PromiseResult<T>` is `{ error, data, cardInfo }`, each nullable. `CardInfo`
carries `serialNum`, `isNewCard`, `pinRetryCount` and `hasBackup`
(`boolean | null`; iOS returns `null` when the state is unknown). PIN and
destructive-reset rules belong to the application's explicit card flow. See
[`src/index.tsx`](src/index.tsx) and
[`src/NativeReactNativeLiteCard.ts`](src/NativeReactNativeLiteCard.ts) for the
wrapper and result types.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Card info and NFC permission check | ✅ | ✅ | — |
| Mnemonic backup / retrieval | ✅ | ✅ | — |
| PIN change and card reset | ✅ | ✅ | — |
| Write identity guard (`expectedSerial`) | ✅ | ✅ | — |
| NFC connection events | ✅ | ✅ | — |
| `cancel()` native operation | — | ✅ | — |

The package targets iOS and Android only; desktop and web consumers are
unchanged and receive no native implementation.

## Caveats

> [!IMPORTANT]
> A resolved promise does not mean the card operation succeeded. Inspect
> `result.error` against `CardErrors` before reading `data`, and treat a null
> `cardInfo` as an unknown card rather than an empty one.

> [!WARNING]
> Serialize whole card operations. The native GPChannel library owns one
> process-wide session and SCP03 counter, so interleaved operations across JS
> runtimes are not safe even though each call is individually guarded.

> [!NOTE]
> `removeConnectListeners()` is an empty wrapper. Keep the subscription returned
> by `addConnectListener()` and call its `remove()` when the owner finishes.

## References

- [Package spec](docs/SPEC.md)
- [Recovered SDK notes](sdk/README.md)
- [Example page](../../example/react-native/pages/LiteCardTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

The React Native wrapper is MIT licensed. See [LICENSE](LICENSE). Vendored SDK
sources retain their upstream notices and are not relicensed by the wrapper.
