# @onekeyfe/react-native-lite-card

OneKey Lite NFC card operations for React Native on iOS and Android. The package
wraps native callbacks in promises for card inspection, PIN changes, mnemonic
backup/retrieval and reset. It also exposes native NFC connection UI events.
The application owns the user flow and handling sensitive card data.

## Installation

```sh
yarn add @onekeyfe/react-native-lite-card
```

Install the application's iOS pods and rebuild the native application. NFC
hardware, platform permissions and host configuration are required; card
operations cannot be validated through a JavaScript-only development session.

## Usage

```ts
import onekeyLite from '@onekeyfe/react-native-lite-card';

const result = await onekeyLite.checkNFCPermission();
if (result.error) {
  // Handle the native error code and message in the application's NFC flow.
} else {
  const permissionAvailable = result.data;
}
```

## API

- `getLiteInfo()` returns card information including serial number, backup
  state, new-card status and remaining PIN retries.
- `setMnemonic(mnemonic, pwd, overwrite)` and `getMnemonicWithPin(pwd)` manage
  the backup; `overwrite` defaults to `false`.
- `changePin(oldPin, newPin)` and `reset()` alter the card's state.
- `addConnectListener(listener)` subscribes to NFC UI events and returns the
  native subscription. Keep it and call `remove()` when the owner finishes.
- `cancel()` invokes native cancellation on Android; `intoSetting()` opens
  the platform settings path.

Operation promises resolve an object containing `error`, `data` and `cardInfo`.
Check `error` before consuming data: a resolved promise does not imply a successful
card operation. `CardErrors` provides the exported native error codes. PIN retry
and destructive reset behavior belong in the application's explicit card flow.

See [src/index.tsx](src/index.tsx) and
[src/NativeReactNativeLiteCard.ts](src/NativeReactNativeLiteCard.ts) for the wrapper
and callback/result types.

## License

MIT.
