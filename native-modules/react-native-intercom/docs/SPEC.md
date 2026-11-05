# Intercom native source SDK contract

Status: **React Native 0.88 New Architecture / React 19.3 local consumer builds and selected iOS resource smoke checks passed; full runtime acceptance remains unverified.**

## 1. Purpose and scope

Provide a source-built native Intercom SDK targeting React Native 0.88 while preserving the public behavior of Intercom React Native 10.8.1, Android SDK 18.10.0, Nexus 6.4.0, Twig 1.3.0 and iOS SDK 19.9.1. Original messenger UI, conversations, help center, cards/sheets, media, recording, push, proactive content, authentication and identity remain in scope.

The package was created with the repository module template. It retains the original RCT/TurboModule/EventEmitter transport and TypeScript facade instead of translating the API into generated Nitro types. This explicit template exception preserves original promises, errors, dictionary conversions, events and JWT lifecycle. The generated example scaffold is not SDK functionality.

App migration, remote publishing and independent security hardening are outside this change. No simplified client, additional origin allowlist, approval protocol or profile-isolation policy is introduced.

## 2. Ownership and boundaries

The SDK owns its native process singleton, identity models, HTTP/realtime clients, UI, storage/cache, media and push integration. The bridge owns JavaScript/native conversion and event delivery. iOS/Android `main` and `bg` initialize independently with separate JavaScript module state, subscriptions and heaps; the underlying native SDK remains shared. One runtime's readiness does not establish the other's. No new per-runtime deserialization or native session ownership is introduced.

Original SDK binaries must never be compiler, linker or runtime inputs, copied into output, or used to fill missing implementation. External dependencies retain their upstream implementations. Android name/metadata transformations may use checked-in text metadata; they must preserve source-compiled method bodies.

## 3. Public API and defaults

`src/index.tsx`, `src/NativeIntercomSpec.ts` and `src/types.ts` define the JavaScript contract. Preserve the 31 native methods: initialize; loginUnidentifiedUser; loginUserWithUserAttributes; logout; setUserHash; updateUser; isUserLoggedIn; fetchLoggedInUserAttributes; logEvent; presentIntercom; presentIntercomSpace; presentContent; presentMessageComposer; fetchHelpCenterCollections; fetchHelpCenterCollection; searchHelpCenter; getUnreadConversationCount; hideIntercom; setBottomPadding; setInAppMessageVisibility; suppressProactiveContent; setLauncherVisibility; setNeedsStatusBarAppearanceUpdate; sendTokenToIntercom; setLogLevel; setThemeMode; setUserJwt; setUserJwtProvider; removeUserJwtProvider; completeUserJwtRequest; setAuthTokens.

Content factories, event subscriptions/bootstrap, JWT completion, user/company attributes, native pre-JS initialization and push helpers remain part of the API. Preserve original signatures, constants, null/empty handling, defaults, validation errors and reject payloads. Help Center wire collection content contains `articles` and `collections`; the baseline TypeScript `sections` declaration must not cause fields to be discarded or synthesized. An absent feature must not return placeholder success.

## 4. Lifecycle and concurrency

Preserve initialization, login/logout, JWT/hash/authentication checks, visibility, push registration, callback ordering, cancellation and pending work. Native UI work remains on the platform UI thread. iOS JWT pending completions belong to the module on the main queue; Android uses the original process-static pending callback map and provider's captured React context. Provider replacement, removal, invalidation and reload retain original effects.

Preserve weak delegate ownership, view reuse, attachment upload cancellation and asynchronous state delivery. Original request and UI owners determine cleanup and callback timing; the bridge must not introduce extra queues, retries, callbacks or success fallbacks.

## 5. Data, cache and remote content

Preserve native identity serialization, disk format, cache keys and login/logout effects. Cards/sheets retain original remote Web JavaScript, bridge actions, navigation, storage and POST semantics. Android retains its original default CookieManager and card cache identity. No hardening or new identity partition is introduced here. Never commit credentials, JWTs, secrets, wallet data or production captures.

## 6. Platform integration

| Target | Contract |
| --- | --- |
| React Native | 0.88 New Architecture, React 19.3; original module registration, codegen and event transport |
| Android | React Native 0.88 consumer minimum SDK 24; native source AAR minimum 23; source-built base/UI artifacts, original resources and required external dependencies |
| iOS consumer | Minimum iOS 15.1 and Xcode 16.1 under React Native 0.88; checked-in Objective-C/Swift through ReactNativeIntercom.podspec; Intercom.bundle and IntercomTranslations.bundle |
| Web/desktop/extension | Not supplied by this native package |

Use the consumer's React Native/toolchain and required Firebase messaging dependency. No vendor `Intercom` pod or `io.intercom.android:*` dependency may enter the native graph. The SDK package contains two source-built AARs and two dependency POMs under `android/maven`; these generated files are not tracked in Git. Registry/tgz consumers install the package and build normally, without running the native SDK producer or requiring its Python/JDK 24/Gradle 8.13 toolchain. Raw Git/source installations that invoke packing require the production toolchain, and package production requires lifecycle scripts to be enabled.

SDK `prepack` builds JavaScript/declarations and unconditionally rebuilds all six native source producers through `scripts/android-build-artifacts.py`. `prepare` only builds JavaScript/declarations; no install or postinstall native build is added. Production requires Python 3.9+, Gradle 8.13, JDK 17 and 24, Android SDK platforms 35/36 and build tools 35.0.0. Resolve JDKs through `JAVA17_HOME`/`JAVA24_HOME`, the SDK through matching `ANDROID_HOME`/`ANDROID_SDK_ROOT`, and Gradle through `--gradle`, then `INTERCOM_GRADLE_BIN`, then PATH. External dependencies remain pinned by SHA and may use `GRADLE_USER_HOME` caches; a valid dependency cache never substitutes for source compilation.

When Python starts production, invalidate the previous consumer Maven output before tool preflight. Clear producer packaging inputs and outputs, rebuild every native source producer, and verify the complete source/android, scripts and tools file inventory and hashes before publishing a staged set of exactly two AARs and two POMs. Added, deleted or changed inputs fail the build. No old or partially generated consumer artifacts may remain after a Python producer failure. A missing Python executable or failed JavaScript build must make packing fail without producing a new package. Intermediate artifacts and evidence remain local under `.build`; POM dependencies preserve the effective Gradle dependency graph when module metadata is omitted. Building an unpacked source checkout without generated artifacts reports the required SDK production command rather than building them inside the consumer's Gradle invocation.

Android BuildConfig.INTERCOM_VERSION_NAME retains the original wrapper version `10.8.1`, independently of this package version. iOS preserves the IntercomFramework bundle CFBundleShortVersionString lookup, then main-bundle IntercomRnVersion, then `0`; do not fabricate a missing metadata bundle. Host permissions, pre-JS initialization and push/notification-extension setup remain consumer responsibilities.

## 7. Failure and compatibility limits

Authentication, validation, transaction-independent native risk checks and original error paths must not be bypassed. Missing features, constructors or private protocol metadata are not closed by stubs, empty callbacks or guessed requirements. Where WMO erased original private Swift protocols, concrete source types express observed calls; private existential/injection ABI parity is not claimed.

No new resource limits or fallback guarantees are asserted. Preserve original I/O, cancellation, failure and cleanup behavior; unsupported behavior remains an explicit acceptance gap.

## 8. Performance

No startup, frame-rate, memory or device performance claim is made. Native threading, cache sizes, I/O and media behavior follow the baseline. Source compilation and metadata checks do not establish runtime performance.

## 9. Conformance and acceptance

Acceptance requires source-only native compilation/linking; normal React Native 0.88 autolinking/codegen and consumer builds; resource packaging; successful and failed API calls with original errors/null conversions; JWT pending/replacement/reload cleanup; and unread/visibility events in the correct JavaScript runtime.

Verified locally with React Native 0.88.0 / React 19.3.0 New Architecture: fresh source AAR rebuilding and Android assembleDebug passed, including 31-method Codegen and arm64 native C++/JSI linking. Fresh iOS Pod/Codegen static and dynamic consumer builds/linking passed for arm64/x86_64 simulators, together with selected API compilation probes and independent Host smoke checks for 63 resource hashes, two same-name asset isolation cases, actual getters, missing-resource nil behavior and React Native classes. XCTest was built, not executed.

Runtime acceptance still requires authenticated/unidentified sessions, JWT failure/refresh, conversation/realtime rendering, help center, cards/sheets actions/navigation/POST, attachment upload/file selection, recording/playback/video, push, and logout/reuse on each supported platform. Business sessions, backend, complete UI, cards/sheets, push, security and private ABI remain unverified. Compiler, resource smoke and source metadata checks must not be reported as full feature, security or private ABI acceptance.
