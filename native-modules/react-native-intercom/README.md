# @onekeyfe/react-native-intercom

Source-built Intercom native SDK targeting React Native **0.88 New Architecture** / React **19.3**. The public transport follows Intercom React Native 10.8.1, with Android SDK 18.10.0, Nexus 6.4.0, Twig 1.3.0 and iOS SDK 19.9.1 behavior.

Local React Native 0.88.0 / React 19.3.0 consumer builds pass: Android assembleDebug with native JSI linking, and iOS normal static/dynamic Pod and Codegen builds/linking for arm64/x86_64 simulators. Selected iOS API compilation probes and resource smoke checks pass. XCTest was built, not executed. Business sessions, backend, complete UI, cards/sheets, push, security and private ABI remain unverified. See the [compatibility contract](docs/SPEC.md).

## Integration

Add this workspace package to the consumer's dependencies and use React Native autolinking/codegen. Import the API from `@onekeyfe/react-native-intercom`; declarations are in `src/index.tsx` and `src/NativeIntercomSpec.ts`.

- Android: minimum SDK 24 for React Native 0.88 (native source AAR minimum 23). Consume the provided two AARs and two POMs under `android/maven`. The consumer retains ownership of its React Native and Android toolchain.
- iOS: minimum iOS 15.1 and Xcode 16.1 for React Native 0.88. Run the consumer's normal `pod install`; `ReactNativeIntercom.podspec` compiles the checked-in native sources and packages `Intercom.bundle` and `IntercomTranslations.bundle`. Retain the initialization, permissions and push/notification-extension configuration required by the features used.

The package must not link vendor Intercom pods, XCFrameworks or Android SDK artifacts alongside its source-built SDK. Native SDK state is shared within the process; separate mobile JavaScript runtimes do not isolate native sessions.

## Rebuild Android artifacts

After modifying native SDK sources, rebuild the Android artifacts from this package directory:

```sh
python3 scripts/android-build-artifacts.py --gradle /absolute/path/to/gradle-8.13/bin/gradle
```

Use `--offline` after pinned dependencies are cached. The independent build requires Python 3, Gradle 8.13, JDK 17, JDK 24 for recovered Java sources, Android SDK platforms 35/36 and build tools 35.0.0. Current compiler paths are declared in the retained scripts. It compiles checked-in Java/Kotlin/resources and text metadata, then generates package-local AARs under `android/maven`; it does not publish remotely. Original SDK binaries and local audit directories are not build inputs.

Build logs and receipts remain local under `.build`. `LICENSE`, `LICENSES` and `NOTICE` retain the applicable license and attribution terms.
