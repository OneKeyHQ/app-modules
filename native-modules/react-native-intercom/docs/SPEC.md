# Intercom native source SDK compatibility contract

Status: **Approved original compatibility baseline; recovery in progress; full SDK behavior not runtime verified.** The generated Nitro template is scaffolding, not an Intercom implementation. A successful template build does not satisfy this contract.

## 1. Purpose, scope, and non-goals

Restore an independently source-built native Intercom SDK, with the behavior of React Native SDK 10.8.1, Android SDK 18.10.0 and iOS SDK 19.9.1. The package must include the native implementation, not wrap a vendor AAR/XCFramework or replace native messenger UI with a simpler chat/web client. Original conversations, help center, cards/sheets, media, recording, push, proactive content, authentication and identity semantics remain in scope.

This recovery does not modify app-monorepo consumers, publish, merge, or implement the separate hardening proposal. Origin allowlists, extra native approval, profile isolation, and modified POST protocols are not authorized changes in this baseline.

## 2. Definitions and ownership

The SDK owns its native process singleton, identity models, HTTP/realtime clients, UI, native storage/cache, media and push integration. Main and background JavaScript heaps are separate in mobile consumers; this does not isolate shared native SDK resources. Native UI work stays on the platform UI thread. The pinned RCT/TurboModule/EventEmitter transport owns JavaScript/native conversion and lifecycle only; it cannot stand in for native feature implementation. The transport exception below records why the generated Nitro scaffold is not the final bridge.

Original artifacts may be used offline for provenance, disassembly, ABI and comparison. They must not be linked or loaded by the recovered SDK/build, copied into build output, supplied as SDK compile dependencies, or used to fill missing methods/classes. A separate isolated original-SDK comparison harness may load reference artifacts for differential diagnosis; it must never become an input or dependency of the recovered product build. Third-party external dependencies retain their actual upstream implementations and are recorded separately.

## 3. Public API and defaults

The 31 original `NativeIntercomSpec` methods must be preserved: initialize; loginUnidentifiedUser; loginUserWithUserAttributes; logout; setUserHash; updateUser; isUserLoggedIn; fetchLoggedInUserAttributes; logEvent; presentIntercom; presentIntercomSpace; presentContent; presentMessageComposer; fetchHelpCenterCollections; fetchHelpCenterCollection; searchHelpCenter; getUnreadConversationCount; hideIntercom; setBottomPadding; setInAppMessageVisibility; suppressProactiveContent; setLauncherVisibility; setNeedsStatusBarAppearanceUpdate; sendTokenToIntercom; setLogLevel; setThemeMode; setUserJwt; setUserJwtProvider; removeUserJwtProvider; completeUserJwtRequest; setAuthTokens.

Public TypeScript factories, event subscriptions/bootstrap, JWT request completion, content types, user/company attributes and native platform SDK APIs are also part of the compatibility inventory; matching only the 31 method names is insufficient. Exact signatures, constants, null/empty behavior, validation errors and observable defaults are taken from the pinned artifacts and RN source, and must be inventoried before marking any API restored. No success-returning fallback is permitted for absent behavior.

## 4. Lifecycle and concurrency

Preserve original initialization, login/logout, JWT/authentication callbacks, visibility, push registration and native singleton ownership. Do not weaken JWT/hash/auth validation or replace failures with success. Preserve original callback ordering, pending work and cancellation behavior where established by source/binary evidence. Missing ordering/resource limits remain explicitly unverified rather than invented as compatibility guarantees.

## 5. Data, cache, identity and remote content

Preserve the original native identity models and serialization, disk format and cache keys. Cards/sheets retain original remote Web JavaScript, native bridge action schemas, navigation, storage and POST semantics. Original Android default CookieManager and card cache identity are baseline behavior for this recovery. The separate security design must not silently change them in this branch.

No user/account secrets, credentials, JWTs, wallet data or production captures are included in source recovery materials. Tests use synthetic values only.

## 6. Platform contract

| Target | Native baseline | Status |
| --- | --- | --- |
| Android | Five Intercom 18.10.0 AARs; Nexus 6.4.0; Twig 1.3.0; original native UI/resources | Source-built; full native/RN runtime acceptance pending |
| iOS | Intercom 19.9.1; iOS 15+; original UIKit/Swift UI, media and native behavior | Frozen 290 ObjC / 395 Swift snapshot strictly links on three targets; normal static/dynamic consumer builds, selected API compiler probes and 63-resource Host smoke pass; full runtime acceptance pending |
| Web/desktop/extension | Not supplied by this native package | Not applicable |

Platform differences must match original behavior and be listed as such. Existing app-specific iOS recording/video suppression patches are not part of this upstream baseline; do not remove these features from the recovered SDK.

## 7. Failure, fallback and safety

Every unresolved method, class, renderer, coroutine/state machine, resource, ABI entry and external integration is recorded in RECOVERY.md/machine receipts. Stub, empty, simplified or always-success code does not count as restored. Template example methods must not be represented as SDK behavior. Builds fail if restored source closure is incomplete for their claimed module. No vendor bytecode fallback or opaque binary payload is allowed.

Source aliases may legalize JVM names for Java compilation. A transparent postcompile pass may restore names and ABI metadata only; it must not copy original method Code attributes. Full input/output class sets, ABI differences and method provenance remain reviewable.

## 8. Performance and resource budget

No performance claim is currently made. Restored threading, cache sizes, I/O and media behavior follow the original baseline. Source-only or ABI checks do not establish startup, frame rate, memory or device behavior. Platform runtime acceptance is required for those claims.

## 9. Conformance and acceptance

A restored module requires: source-only compilation without original SDK on classpath; complete expected class/resource inventory; explicit ABI comparison; no decompiler error/unsupported placeholders in packaged runtime; behavioral tests of real restored code; and per-feature provenance. Android first validates the complete UI module, then base/realtime/FCM/media and package assembly. iOS requires actual compiled implementation and original feature-by-feature rendering/identity/media/network behavior comparison, not merely API headers.

Final acceptance includes initialized authenticated/unidentified sessions, JWT failure/refresh, message rendering/realtime updates, unread state, help center, cards/sheets actions/navigation/POST, attachment upload/file chooser, recording/playback/video, push and logout/reuse. This branch has not performed those cases. The independent later audit may harden behavior in a separate change.

## RN transport exception and source SDK integration (Approved baseline; integration in progress)

This package was created with the repository module template. Under Native Module Development Requirements section 3, it intentionally retains the pinned Intercom RN 10.8.1 public TypeScript facade and original RCT/TurboModule/EventEmitter transport instead of translating that surface into Nitro 0.37 generated types. This exception preserves original promise rejection data, platform dictionary conversions, native initialization entry points and runtime-bound JWT/event lifecycle. It does not introduce a second SDK, a JSON-string wire format, or a vendor AAR/XCFramework dependency. The existing Nitro `hello` scaffold is not a public Intercom feature and must not ship as a substitute for missing behavior. Shared repository templates and consumer App code are outside this change.

The 31 public native methods, TS exports/five content factories, defaults, error codes and event/bootstrap behavior follow the recorded RN baseline. Native wire payloads remain original even where the old TypeScript declarations disagree: Help Center collection content contains `articles` and `collections`; the baseline `sections` declaration must not cause fields to be discarded or synthesized. This stage does not silently change public declaration shapes.

Native Intercom identity/network/UI resources remain process-shared. Mobile main/background JavaScript runtimes have separate JS module state and subscriptions. The bridge preserves original per-platform provider ownership: iOS module-local pending completions on main queue; Android process-static pending callback map and provider captured React context. Removal/invalidation retains original native effects; no new cross-runtime security policy is introduced. Real native `Intercom` facades and public pre-JS initialization/push helpers must be source-linked; method declarations or successful template builds are insufficient.

Package builds select original old/new RN architecture adapters and generate the original `IntercomReactNativeSpec` contract with `com.intercom.reactnative` Android namespace. Wrapper sources may depend on the consumer's React Native and already-required upstream external dependencies (including the existing Firebase messaging dependency), but all Intercom native SDK dependencies must resolve to this package's recovered source-built artifacts. No vendor `Intercom` pod or `io.intercom.android:*` dependency may re-enter the build. Do not copy the original package's floating `react-native:+`, standalone Gradle/toolchain overrides or unrelated package build scripts into this monorepo; use its existing supported dependency/build ownership without upgrading consumers.

The original wrapper's RN-version reporting is distinct from this package's release version: Android BuildConfig.INTERCOM_VERSION_NAME originates from the original package version `10.8.1`; do not accidentally replace it with the OneKey package release version. iOS reads IntercomFramework bundle CFBundleShortVersionString, otherwise main-bundle IntercomRnVersion, otherwise string `0`; preserve that lookup/fallback rather than hardcoding Android behavior. The recorded tarball has no ios/assets directory even though the podspec resource glob mentions it, so no original version bundle can be presumed present or fabricated. Consumer packaging must verify the actual selected iOS value and account for any required metadata resource explicitly. Packaging may change resource location only according to the documented source-SDK resource plan, not by silently dropping version metadata.

Acceptance requires source-only native closure, real RN codegen/consumer compilation, exact module registration, representative successful/failed calls with original reject payloads and null/numeric conversion, JWT pending/replacement/reload cleanup and unread/visibility events on the correct runtime. Original and recovered native binaries must never co-link in that consumer. Android source-only native closure, real RN codegen/autolinking and old/new Java wrapper compilation with consumer resource/duplicate-class checks pass. The frozen iOS 290 ObjC / 395 Swift source snapshot strictly links on three targets. Normal static/dynamic iOS consumer builds, selected public/private API compiler probes and 63-resource Host smoke checks pass. Complete Android APK/C++/JSI linking and the remaining behavioral/session/runtime gates are pending. Backend/session, cards/sheets, complete UI behavior, security and private ABI remain unverified; no complete RN SDK or native feature readiness is asserted.


## Survey native source restoration

Survey questions retain the original native view/controller and concrete input implementations. Required indicators, response/error state, character counts, form validation, keyboard scrolling and accessibility strings follow the pinned native behavior, including known original quirks recorded in the Survey contracts. No generic form/UIView fallback, new validation policy or API success placeholder may replace missing native dependencies. Survey source work is not linked/runtime verified until its actual model, input, block-renderer, controller and service dependencies close in the normal SDK build.

Survey Step restoration preserves original native constraints, asynchronous state delivery, repeated successful submit notifications, per-question validation and keyboard/accessibility scrolling. Where WMO removed private protocol witness signatures, the actual sole Step/Survey controller and view-model types express the observed direct calls; this changes private existential storage/injection ABI and is recorded in SURVEY_RECOVERY.md rather than represented as a recovered protocol. No invented delegate forwarding API is introduced.

## Conversation native source restoration

Conversation recovery retains the original native controller/interactor/renderer route, eighteen VM dependencies, independent schedulers, Date.distantPast scroll threshold, weak ownership and post-super typing/streaming/state-sync installation. Lifecycle preserves six observers, independent weak loads and real request/interactor routing. Concrete default-path types may express original WMO calls where private protocols were deleted; original private existential/injection ABI remains explicitly different. No substitute chat/WebView, fabricated requirement, empty callback or success stub closes a dependency. Missing true bodies remain build/link failures, separately recorded from restored sources.
