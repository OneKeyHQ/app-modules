# @onekeyfe/react-native-intercom

Source-built Intercom native SDK targeting React Native **0.88 New Architecture** / React **19.3**. The public transport follows Intercom React Native 10.8.1, with Android SDK 18.10.0, Nexus 6.4.0, Twig 1.3.0 and iOS SDK 19.9.1 behavior.

Local React Native 0.88.0 / React 19.3.0 consumer builds pass: Android assembleDebug with native JSI linking, and iOS normal static/dynamic Pod and Codegen builds/linking for arm64/x86_64 simulators. Selected iOS API compilation probes and resource smoke checks pass. XCTest was built, not executed. Business sessions, backend, complete UI, cards/sheets, push, security and private ABI remain unverified. See the [compatibility contract](docs/SPEC.md).

## Integration

Add this workspace package to the consumer's dependencies and use React Native autolinking/codegen. Import the API from `@onekeyfe/react-native-intercom`; declarations are in `src/index.tsx` and `src/NativeIntercomSpec.ts`.

- Android: minimum SDK 24 for React Native 0.88 (native source AAR minimum 23). The packed SDK contains two generated AARs and two POMs under `android/maven`. Installing a registry/tgz package does not run the native SDK producer or require Python, JDK 24 or Gradle 8.13. The consumer retains ownership of its normal React Native and Android toolchain.
- iOS: minimum iOS 15.1 and Xcode 16.1 for React Native 0.88. Run the consumer's normal `pod install`; `ReactNativeIntercom.podspec` compiles the checked-in native sources and packages `Intercom.bundle` and `IntercomTranslations.bundle`. Retain the initialization, permissions and push/notification-extension configuration required by the features used.

The package must not link vendor Intercom pods, XCFrameworks or Android SDK artifacts alongside its source-built SDK. Native SDK state is shared within the process; separate mobile JavaScript runtimes do not isolate native sessions.

## Produce the SDK package

The four Android artifacts are generated and ignored by Git. SDK package production requires Python 3.9+, Gradle 8.13, JDK 17 and 24, Android SDK platforms 35/36 and build tools 35.0.0. Configure the production machine before packing:

```sh
export JAVA17_HOME=/absolute/path/to/jdk-17
export JAVA24_HOME=/absolute/path/to/jdk-24
export ANDROID_HOME=/absolute/path/to/android-sdk
export INTERCOM_GRADLE_BIN=/absolute/path/to/gradle-8.13/bin/gradle
yarn workspace @onekeyfe/react-native-intercom pack --out /absolute/path/to/intercom.tgz
```

`npm pack` also runs the same `prepack` hook: build JavaScript/declarations, then unconditionally rebuild all native source producers from checked-in Java/Kotlin/resources and text metadata. Packing requires lifecycle scripts to be enabled. Raw Git/source installations that invoke packing also require the production toolchain. `prepare` only builds JavaScript/declarations. No install/postinstall native build is added. The package remains private; existing shared publication CI has not been configured with this SDK production toolchain.

`ANDROID_SDK_ROOT` may replace `ANDROID_HOME`; if both are set they must resolve to the same SDK. Gradle selection is `--gradle`, then `INTERCOM_GRADLE_BIN`, then PATH. External downloads are SHA-pinned and use `GRADLE_USER_HOME` (default `~/.gradle`) when available. A dependency cache does not skip native source compilation. Original SDK binaries and local audit directories are not build inputs.

For a local workspace Android build, generate the same artifacts first:

```sh
yarn workspace @onekeyfe/react-native-intercom build:android-source
# For cached external dependencies only:
python3 scripts/android-build-artifacts.py --offline
```

Python production invalidates old `android/maven` output before checking tools. It verifies the complete source/scripts/tools inventory before promoting exactly four staged files; missing tools, changed inputs or compilation failures leave no old or partial consumer set. A failed pack exits without creating a new package. Consumer Gradle reports missing artifacts and the local production command; it never invokes the producer.

Build logs and receipts remain local under `.build`. `LICENSE`, `LICENSES` and `NOTICE` retain the applicable license and attribution terms.
