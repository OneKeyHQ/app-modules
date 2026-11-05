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

# Resolve JUnit dependencies and run the core/FCM module suites
(cd example/react-native/android && ./gradlew :onekeyfe_react-native-jpush:testDebugUnitTest :onekey-jpush-fcm:testDebugUnitTest)

# macOS: native host build/protocol regressions (loopback servers and isolated RN/Android shims)
python3 native-modules/react-native-jpush/scripts/verify_clean_sdk.py
```

Java and Objective-C host fixtures live in `tests/android` and `tests/ios`; Python only builds, runs and checks artifacts. Android host tests use real JUnit 4.13.2 from the Gradle dependency cache. The FCM Gradle suite uses Robolectric and real Firebase message/task types, with mocked external token requests. These checks do not establish physical-device or cloud delivery.

The external Jiguang sandbox register/login test is explicitly skipped by default. Set `JPUSH_LIVE_TESTS=1` when running either Android test entry to enable it; then all network/protocol/assertion failures fail the test. CI runs both host suites plus Gradle FCM tests. A manual `native-tests` workflow can enable the same cloud check with `jpush_live_cloud`.

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

The host retains its existing `google-services.json` and Google Services Gradle plugin. Firebase auto-init stays disabled; JPush initialization requests the token after authorization. Granting authorization after a previously unauthorized init also requests registration without reconnecting. Cloud acceptance requires the matching Firebase credentials configured in the Jiguang sender.

### Required sender contract for stopped/authorization controls

FCM notifications governed by `stopPush()` or collection authorization **must be delivered as data-only FCM messages**. Put the notification metadata in `message.data.JMessageExtra`; omit both FCM `message.notification` and `message.android.notification`. The guarded native path parses the metadata, displays the system notification and dispatches arrival/opened events.

Firebase automatically displays background notification/combined payloads before `onMessageReceived`. Their display cannot obey the guards inside that callback. A notification click action only routes taps; it does not fix that display boundary. Such payloads are not an accepted delivery mode for notifications governed by JPush state.

The Jiguang REST `notification.android` object is not the same field as a Firebase HTTP v1 `message.notification`. When sending through Jiguang, verify the vendor channel's resulting FCM payload/configuration rather than deleting Jiguang's notification object. The sender/conversion configuration lives outside this SDK.

Device acceptance must cover data-only delivery in the background: one display/arrival/tap while authorized, no display/callback after stop or authorization withdrawal, and token registration after authorization is granted again. Host tests and direct callback injection do not establish this cloud/device acceptance.
