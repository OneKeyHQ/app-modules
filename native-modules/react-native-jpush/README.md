# `@onekeyfe/react-native-jpush`

Clean-room JPush (`6.0.0` iOS / `5.9.0` Android) and JCore (`5.2.0`) React Native Nitro module for OneKey.

See the authoritative behavioral and wire-protocol specification in [`docs/SPEC.md`](./docs/SPEC.md).

## Features

- **Nitro JSI + React Native Bridge**: Implements `HybridReactNativeJpushSpec` (Nitro Modules) and exports `JPush` (default) + `JCore` (named) 100% compatible with `jpush-react-native` and `jcore-react-native` in `app-monorepo`.
- **Clean-Room Native Source**: Full Objective-C/Swift (`ios/`) and Java/Kotlin (`android/`) source implementation with zero obfuscated `.a` or `.jar` binaries.
- **Production Wire Protocol**: 10-byte UDP SIS (`'U','G'`), asymmetric 24-byte Client->Server and 20-byte Server->Client TCP framing, AES-256-CBC & SM4-CBC encryption, multi-IP waterfall failover, registered fast-login, exponential backoff auto-reconnect, HTTP `/v3/report` & iOS Badge report, and Android `:pushcore` AIDL IPC.

## Verification

```sh
# TypeScript, ESLint, Jest, and Nitro codegen
yarn workspace @onekeyfe/react-native-jpush nitrogen
yarn workspace @onekeyfe/react-native-jpush typecheck
yarn workspace @onekeyfe/react-native-jpush lint
yarn workspace @onekeyfe/react-native-jpush test

# Native iOS & Android E2E protocol verification (uses non-production demo key 1f2027d3cfb1b51dac664c42)
python3 native-modules/react-native-jpush/scripts/verify_clean_sdk.py
```

## Android Google / FCM source transport

The optional Android source library in `android/fcm` replaces the opaque Jiguang FCM plugin. It delegates the Google transport to the official Firebase Messaging SDK and forwards tokens and messages into this package's JCore/JPush pipeline. Link it alongside the project's autolinked JPush module (the alias below matches app-monorepo):

```groovy
// settings.gradle, after React Native autolinking
include ':onekey-jpush-fcm'
project(':onekey-jpush-fcm').projectDir = new File(project(':jpush-react-native').projectDir, 'fcm')

// app/build.gradle: replace cn.jiguang.sdk.plugin:fcm
// Keep this dependency scoped to the Google distribution.
googleImplementation project(':onekey-jpush-fcm')
```

The host retains its existing `google-services.json` and Google Services Gradle plugin. Firebase auto-init stays disabled; JPush initialization requests the token after authorization. Cloud acceptance requires the matching Firebase credentials configured in the Jiguang sender. For Firebase-managed background notification payloads, use `cn.jpush.android.intent.NOTIFICATION_OPENED` as the click action so the private JPush activity receives `JMessageExtra`.
