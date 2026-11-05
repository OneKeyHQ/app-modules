# React Native JPush & JCore Native Contract (`@onekeyfe/react-native-jpush`)

Status: `Implemented` in clean-room Objective-C/Swift (iOS) and Java/Kotlin (Android); `Runtime verified` for host compilation, Nitrogen JSI codegen, Jest JS contract tests, local Mock SIS/TCP/HTTP E2E socket tests, and live Jiguang UDP SIS (`s.jpush.cn:19000`) + TCP `Register` (`cmd=0`) / `Login` (`cmd=1`) / `Tag` (`cmd=28`) handshake (`120.46.76.213:7001`) using non-production demo test key `1f2027d3cfb1b51dac664c42` and sandbox package name `com.devstudio.msgbench`. Real-device APNs / vendor push delivery acceptance remains a device-level check.

---

## 5.1 Purpose, scope, and non-goals

### Purpose
`@onekeyfe/react-native-jpush` provides a clean-room, human-auditable source implementation of the JCore (`5.2.0`) and JPush (`6.0.0` iOS / `5.9.0` Android) push notification SDKs, integrated with React Native via **Nitro Modules** (`HybridReactNativeJpushSpec`) and legacy React Native bridge modules (`JPushModule`, `JCoreModule`).

### Supported callers and platforms
- **JavaScript / TypeScript callers**: OneKey mobile app (`@onekeyhq/kit-bg`, `@onekeyhq/kit`, `@onekeyhq/shared`) importing default `JPush`, named `JCore`, or `ReactNativeJpush` Nitro HybridObject.
- **Native host callers**:
  - iOS (`AppDelegate.swift` / Objective-C bridging header): `JPUSHService`, `JPUSHRegisterEntity`, `JPUSHRegisterDelegate`, `JGCOREAPI`, `JGInforCollectionAuth`, `RCTJPushEventQueue`.
  - Android (`BaseMainApplication`, ASM `JPushMethodVisitor`, Manifest components): `cn.jpush.android.api.JPushInterface`, `cn.jiguang.api.utils.JCollectionAuth`, `cn.jiguang.internal.JCoreInternalHelper`, `cn.jpush.android.service.*`, `cn.jpush.android.ui.*`.
- **Supported platforms**: iOS (`>= 13.4`) and Android (`minSdkVersion >= 23`). Web/Desktop are non-goals (callers guard with platform checks).

### Non-goals
- Decompiled or obfuscated third-party bytecode/binaries (`.a` / `.jar` blobs are not checked into source control; all native code is compiled from readable source).
- Dynamic remote code loading (`JDynamicLoadBase` / DEX loading is intentionally excluded for security).
- Geofence location tracking and WebView in-app HTML5 popups (interfaces are retained as safe no-op/passthrough stubs for ABI compatibility).

---

## 5.2 Definitions and ownership boundaries

- **JS / Nitro / Bridge Boundary**:
  - `src/ReactNativeJpush.nitro.ts` defines the JSI `HybridObject` interface (`ReactNativeJpush`).
  - `src/index.tsx` exposes the `JPush` default class and `JCore` named class matching `jpush-react-native` (`2.6.6+`) and `jcore-react-native` (`1.3.0+`), dispatching through `ReactNativeJpush` (Nitro JSI) when initialized and falling back to `NativeModules.JPushModule` / `NativeModules.JCoreModule`.
  - Asynchronous push events (`ConnectEvent`, `NotificationEvent`, `LocalNotificationEvent`, `CustomMessageEvent`, `TagAliasEvent`, `MobileNumberEvent`, `InappMessageEvent`, `CommandEvent`, `NotifyButtonClickEvent`, `PropertiesEvent`) cross from native to JS via `RCTDeviceEventEmitter`.
- **Native SDK Boundary**:
  - **iOS (`ios/jcore/`, `ios/jpush/`, `ios/bridge/`)**: Owns `JCOREConfiguration`, `JCORECrypto`, `JCOREWireProtocol`, `JCORESisResolver`, `JCORETcpSession`, `JPUSHNotificationManager`, `JPUSHService`, `RCTJPushModule`, `RCTJPushEventQueue`, `RCTJCoreModule`, and `ReactNativeJpush.swift`.
  - **Android (`android/src/main/java/`)**: Owns `cn.jiguang.core.*` (`JCoreStorage`, `JCoreCrypto`, `JCoreWireCodec`, `JCoreTcpEngine`), `cn.jiguang.api.*`, `cn.jiguang.internal.*`, `cn.jiguang.net.*`, `cn.jpush.android.*`, `cn.jiguang.plugins.*`, and `com.margelo.nitro.reactnativejpush.*`.
- **Demo Test Key & Sandbox Package Boundary**:
  - `DEMO_TEST_APP_KEY = "1f2027d3cfb1b51dac664c42"` and `DEMO_TEST_PACKAGE_NAME = "com.devstudio.msgbench"` are non-production sandbox test fixtures checked into test/spec constants solely for automated unit, protocol, and live cloud handshake testing. They intentionally avoid `onekey` and `jpush` identifiers so automated test runs never collide with production apps or trigger vendor keyword filters. Production apps supply their own AppKey via `JPush.init({ appKey, channel, production })` or `AndroidManifest.xml` `JPUSH_APPKEY`.

---

## 5.3 Public API and defaults

### Android Google / FCM source adapter (`Implemented`, FCM cloud acceptance pending)
- The Google host replaces `cn.jiguang.sdk.plugin:fcm` with the readable Android library bundled under this package's `android/fcm`. It uses the already-consumed official `com.google.firebase:firebase-messaging:25.0.1`; FCM transport/authentication remain Google's responsibility. Other hosts do not link this optional library.
- This is an Android-only auxiliary Gradle project of the existing Nitro package, not a new NPM package or HybridObject. The Nitro package template has no optional pure-Android transport module; this library deliberately uses the host's Android plugin/SDK versions and depends on the SDK project found by its source directory. It introduces no second JS/native registration and is shipped as source with nested build output excluded.
- `JPushInterface.init` / `resumePush` and successful login/re-registration discover only the fixed installed `com.onekey.jpush.fcm.FcmPushManager` class asynchronously after authorization and while push is enabled. Native registration is process-owned and shared by independent main/bg JS runtimes. No main/bg readiness order is assumed. Missing Google Play services/Firebase configuration leave the JCore TCP transport available.
- Token callbacks use platform `8` and the existing pre-login queue / `cmd=27, ver=2`. Only a successful Jiguang acknowledgement persists the bound token. Re-login re-submits the current token because Registration ID may change. Empty, stopped or unauthorized callbacks are ignored. Firebase auto-initialization remains disabled as it was in the replaced plugin.
- Authorization transition correction (`Implemented`, Robolectric verified): the shared native `JCollectionAuth.setAuth` entry used by Nitro and the legacy bridge requests guarded asynchronous FCM registration after authorization changes from false to true, without requiring reconnect, another init or resume. Repeated true and false values do not request registration; stopped state and authorization are rechecked by the existing FCM initializer before queued work executes. Three focused regressions verify late grant without reconnect, stopped grant, and revocation before queued initialization. Both JS bridges retain their existing shared entry; iOS behavior and the native logger contract are unchanged.
- The source `FcmMessagingService` handles data-only and foreground combined payloads through the existing native arrived/custom-message pipeline. `JMessageExtra` supports plain JSON and Base64/GZIP `jg_c`, bounded to 65,536 decoded bytes. Missing title/body fields are filled from Firebase notification fields even when Jiguang metadata exists; standard Firebase data is normalized when that metadata is absent. The SDK posts one system notification using its existing channels and PendingIntent handling.
- Foreground-display correction (`Implemented`, Robolectric verified): `n_foreground=0` suppresses SDK-controlled notification display and arrival callbacks only while the receiving Android process has foreground importance and the keyguard is unlocked. Background/service-only and locked-screen data-only messages still post a notification and dispatch one arrival callback. The native dispatcher samples OS state per message, owns no cached lifecycle state, and does not depend on either main or bg JS initialization. This foreground flag does not change custom messages or Firebase-owned automatic display; the separate receive gate enforces stopped/unauthorized state. The 33-case FCM suite covers foreground suppression, background data-only delivery, per-message state transitions and locked-screen delivery; actual cloud/device acceptance remains pending.
- Notification clicks retain IDs/extras, queue native-to-JS opened events, and open the host launcher on a cold tap. The private JNotifyActivity also accepts the JPush notification-opened click action. Firebase automatic background display of combined notification/data payloads remains Firebase-owned after the receive gate allows it; their server payload must select the JPush click action to deliver opened events through that private activity. Rich-media downloads and marketing/custom-button templates are outside the OneKey FCM contract.
- Diagnostics contain lifecycle stage/platform/result code/error type only, never tokens, notification content, extras or raw exceptions. SDK receive/event/display handlers respect stopped/unauthorized state; vendor cloud delivery and device notification behavior still need device acceptance.
- Client-owned receive gate (`Implemented`, Robolectric verified): vendor payloads remain unchanged. The optional FCM service checks stopped/collection-authorization state in `handleIntent` before delegating to Firebase Messaging 25.0.1, so background notification/combined display cannot bypass that check. Authorized intents still use Firebase's normal dispatch/deduplication path; existing callback guards remain in place. Blocked intents are discarded and are not replayed on resume/regrant. This override depends on the pinned Firebase receive entry and requires integration regression coverage when upgrading it.
- Deferred FCM interactions (`Implemented`, Robolectric verified): serialized platform-8 notification taps recheck stopped/authorization state immediately before opened-event/launcher dispatch, including notifications posted while previously authorized. Token rotation initializes JCore without scheduling another Firebase token fetch, then submits the callback token once. Existing post-login rebind semantics remain separate from this rotation callback. Regressions exercise post → stop/withdraw → tap with a real PendingIntent, and assert one platform-8 wire bind enqueue for one rotation while the core is already connected.
- Notification delegation (`Implemented`, Robolectric verified): the Google-only module disables Firebase notification delegation in manifest metadata. A private startup provider also clears an existing Google Play services notification delegate on Android 10+, including upgrades where Firebase has already initialized delegation. It runs without requesting a token or changing consent, before either JS runtime initializes; no JS heap/state is involved. Other notification delegates are preserved. The updated app must start once to revoke a previously installed delegate. The unguarded Firebase fallback service is removed, and direct-boot reception before user unlock is not enabled. Merged-manifest and startup tests verify the selected private service/provider and delegation policy. Actual vendor-cloud/device acceptance remains separate.
- Acceptance is separated into native token/arrival/click regression, source module/Google APK build, on-device synthetic callback verification, and actual cloud delivery to a dedicated test installation. Synthetic callbacks and loopback servers do not prove FCM/Jiguang cloud delivery.
- Post-change audit: initialization, token acknowledgement/rebind, payload bounds, notification/custom routing, click launch and diagnostic privacy are covered by this section; no JS/Nitro contract or iOS behavior changed. The earlier source-transport change passed host Android SDK tests, Google Java compilation and a GoogleDebug APK build. The later client receive gate passes core/FCM Java compilation and 33 FCM Robolectric cases, including real Firebase receive/display/deduplication, pre-dispatch stop/consent rejection, startup delegate cleanup and merged-manifest routing; the updated App APK and vendor-cloud/device acceptance remain pending. An unchanged-source native Android fixture under the sandbox package received console-directed TCP notification/custom messages, displayed the notification, and delivered one opened callback after a real tap with no FCM module linked. This validates the sandbox TCP path, not OneKey production or FCM cloud delivery.

### Android receive, click, and reconnect lifecycle (`Implemented`; Android 15 emulator runtime verified)
- Android content delivery uses the shared native authorization/stopped state for TCP and vendor notification/custom-message reception, system display, notification interactions, and native-to-JS content events. Register, connection, command, and tag/alias result events remain control events. A disabled receive or tap produces no content callback or launcher launch.
- Revocation and stop invalidate pending native content events, including snapshots already being flushed. Regrant/resume cannot replay invalidated content through either Nitro or legacy React contexts. This does not retract events already delivered to JavaScript or remove system notifications already displayed.
- Native state, content generations, and queues are process-owned and shared by independent Android main/bg JS runtimes; JS heaps remain separate and either runtime may initialize first. No cross-process or JS readiness order is assumed.
- A stopped `init` does not reserve an engine start. The first following `resumePush` can connect; an obsolete queued start cannot release ownership of a newer connection generation.
- Valid private notification activity taps for both TCP (platform 0) and FCM (platform 8) synchronously launch only the installed host package launcher before that activity finishes. The activity owns launcher dispatch; the asynchronous notification-opened receiver only delivers the opened event. Existing action/package/component validation is retained; payload URLs or target packages are not executed.
- Successful real TCP login, including login after a fresh Registration ID, requests guarded asynchronous FCM initialization after flushing queued requests. Failed login does not request it. Token rotation still submits its callback token once, and only successful bind acknowledgements persist tokens.
- Focused acceptance (`Runtime verified` at the host/Robolectric layers): the host SDK suite verifies stop/init/resume, obsolete queued start ownership, two real loopback Register/Login handshakes with changed RID and a bind ACK on the second UID, failed-login behavior, both native-to-JS queues/snapshot invalidation, and delayed native broadcast rejection. The 39-case FCM/manifest suite verifies TCP/vendor receive/tap gates, platform-0/8 single-launch clicks, foreign/invalid intents, and revocation/regrant during notification construction or an opened callback. Core Gradle tests pass eight deterministic cases; the external cloud test is explicitly skipped. The existing rotation and failed-bind acknowledgement regressions remain passing.
- Android emulator acceptance (`Runtime verified`, Android 15/API 35, target SDK 36, local-source APK): after authorization withdrawal, TCP delivery added zero system notifications and zero arrival callbacks; the first stop/init/resume sequence connected and logged in successfully; re-registration changed RID and increased stable FCM token requests and bind acknowledgements from 2 to 3 without explicit reinitialization.
- Six real system-notification taps covered platforms 0 and 8 in cold-process, background, and foreground states without target instrumentation. Each produced one opened event and one host launcher launch, left OneKey MainActivity in the foreground, and produced no ASM/BAL rejection. The platform-0 cold-process notification arrived through real loopback TCP; the other cases used synthetic messages posted by the SDK production notification builder.
- FCM token values and platform-8 payloads were synthetic. These checks do not establish real FCM/Jiguang cloud delivery, physical OEM-device or other-OS behavior, or notification-detail navigation. No SDK release or merge is implied by local acceptance.

### Shared native policy, request ownership, and cleanup (`Implemented`; `Runtime verified` at the listed host/emulator layers)
- On iOS/Android, main and bg have separate JS heaps but share the process-owned native SDK, authorization/stopped policy, connections and content queues. Policy changes invalidate native content snapshots synchronously. Disabled content is discarded; enabling cannot replay it. Data already handed to a JS scheduler or a consumer-owned JS buffer cannot be recalled by native cleanup.
- iOS authorization/stopped changes advance a synchronized native policy generation. Disabling prevents startup, stale SIS/connection/login completions, TCP/HTTP sends and APNs/local/custom event forwarding. Regrant resumes only a previously initialized session with an AppKey while push is enabled. Stop retains its existing control command when connected, then closes the owned connection; resume never overrides missing authorization. Blocked notification-center callbacks complete without presentation or host delegate forwarding.
- Android authorization withdrawal preserves the existing TCP connection and protocol/control traffic; business requests, tokens, HTTP reports and content delivery remain gated by authorization/stopped state and content generation. This is an intentional difference from the iOS network policy.
- Each Android connection attempt owns its generation, handshake latch, and socket through resolution, registration/login commits, heartbeat, pending-request drain, receive and teardown. Obsolete work can close/complete only its own resources. Response slots and retries stay with their original connection; cancellation completes once with existing code `6002`. Pre-login enqueue and login drain are atomic; one existing-executor drainer sends business requests and completes their acknowledgements in FIFO order, while protocol/control traffic remains separate.
- Android network-availability reconnect queue correction (`Implemented`; `Runtime verified` by host/Gradle checks only): a network callback rechecks login under the engine monitor and leaves a newly logged-in connection alone. Only this implicit pre-login reconnect preserves queued requests that are uncompleted, still unbound to a connection owner, and match the current authorized/enabled policy generation and AppKey/package identity, retaining their FIFO order. Explicit close/reconnect, stop, terminal errors and mismatched/revoked generations still cancel with `6002` exactly once. Once a drainer claims a request, it is permanently owner-bound and cannot replay after reconnect. Each drain run has its own revocable token; a stale worker may finish its claimed request but cannot poll the replacement queue or clear its new drainer token, including while its callback is blocked. Configuration identity excludes UID/RID, so normal registration changes do not discard valid pre-login work.
- Android public `stopPush` immediately disables local content/business delivery and cancels owned pending requests without waiting for the socket writer. On an established connection it asynchronously attempts the existing stopped control command (`cmd=40`) against the captured owner/socket/UID/session, then closes that socket; a private fixed 200 ms grace deadline closes it even if the writer or control send is blocked. The ordinary writable path sends control before close. Neither a delayed control task nor its deadline can send on or close a later resumed owner. Heartbeat timers dispatch writes to the existing network executor so they cannot block the close deadline. This best-effort grace adds no public setting or retry loop.
- On Android, alias, tags, properties and mobile-number operations complete from real acknowledgements or existing failure/timeout callbacks. Offline raw-send success is not synthesized. Local cached values commit only after success; an older failed operation cannot roll back later acknowledged state. Terminal AppKey/package errors (`1005`, `1008`, `1009`) halt reconnect/failover until that configuration changes; `108`/`1012` retain their registration/SIS recovery paths.
- iOS OS-current APNs/VoIP tokens are separate from acknowledged bindings. Binding identity includes the connection generation, UID and token. Only a current successful acknowledgement establishes deduplication; failed/timeout acknowledgements clear in-flight state so a later public registration can retry. Existing wire resend behavior remains; no new automatic token retry loop is added. New login rebinds the current token; stale acknowledgements cannot replace a newer token/connection binding. A newer APNs/VoIP token cancels the superseded command’s in-flight slot and resend timer with existing code `6002`, so an old retry cannot overwrite a newer successful binding.
- Native listeners belong to the registering module/runtime. Its actual native destruction/invalidation unregisters only its own listener IDs and legacy context. Snapshot delivery rechecks listener registration and content generation immediately before each callback. Destroying main leaves bg listeners and shared transport available, and vice versa.
- Pre-login queues accept at most 128 requests. The 129th is rejected with existing completion code `6002` and internal queue-full reason; existing FIFO entries are retained. GZIP decoding accepts at most 65,536 decoded bytes, including the exact limit; oversized expansion returns no partial result and cannot fall back to compressed bytes as an accepted payload. Existing non-GZIP plaintext compatibility is limited to the same byte count. Queue/buffer rejection does not alter cryptographic algorithms or transport validation. On Android, notification construction remains outside the policy lock; updating a notification PendingIntent, posting it, and updating/evicting SDK tracking form one final generation-checked policy transaction, so a rejected construction cannot change an existing notification’s click payload or tracking.
- App-requested local-notification scheduling is unchanged; these receive/content gates do not add a new scheduling-authorization policy.
- Android native validation: Kotlin/C++ compilation and arm64 linking passed before this network-reconnect correction. The current host suite runs 23 JUnit cases: 22 deterministic cases pass and one external-cloud case is explicitly skipped. Normal localhost/native fixtures exercise ownership/ACK, FIFO, terminal-configuration recovery, queue bounds and listener/context cleanup. The reconnect regression first failed on the previous implementation because four queued public operations were cancelled. With the correction, pre-login preservation/FIFO, sent or owner-bound non-replay, blocked-old-callback drainer handoff, policy/configuration and explicit-cancellation isolation, and terminal recovery pass five focused repetitions, totaling 25 case executions. The current full suite also passes all four public-stop cases once; their earlier five focused repetitions (20 case executions) belong to the historical `2b4a98` stage.
- Android Gradle validation for this network-reconnect correction: eight deterministic core cases pass and one external-cloud case is explicitly skipped; all 39 FCM/manifest Robolectric cases pass (38 adapter and one manifest case). Plain core local-notification checks establish Java scheduler/future completion and cancellation only. Robolectric separately verifies actual Android Intent behavior, scheduled/cancelled arrival callbacks, system notification display, PendingIntent payloads and SDK tracking, including preservation of an existing notification when replacement construction is revoked.
- iOS validation: universal device/simulator static archives and React Native bridge compilation pass, together with eight Mac Catalyst native host suites. These cover policy/content invalidation, object-only AppDelegate forwarding, connection ownership, acknowledged token binding/supersession, queue and decoded-size bounds, HTTP gates and native delegate completion.
- Historical Android runtime ownership and PendingIntent acceptance before this network-reconnect correction passed on the earlier local APK with SHA-256 prefix `830bf61d`: actual independent Hermes A/B runtimes were reset without explicit disposal, listener IDs changed from two to one to zero, B remained deliverable, and a gap event replayed once to C. A real framework PendingIntent retained its old payload and notification/tracking state after revoked replacement construction. These tests were not repeated on the later historical APK `51128956`; that build’s Android source delta was confined to the stop lifecycle in `JCoreTcpEngine`/`JPushInterface`, while listener/destructor/helper and PendingIntent implementation hashes remain unchanged. The iOS bridge source update is outside that Android APK. No APK, consumer-source synchronization or device acceptance was repeated for the current network-reconnect engine correction.
- Historical acceptance before this network-reconnect correction (the `2b4a98` stage): Android 15/API 35, target SDK 36 uses local APK SHA-256 `511289566069f3e5a9af54c594fa7d0c195a7ecd2998a341e658a85ea7d60df0`. All 19 frozen SDK files match the consumer source hashes, and the installed APK hash matches that build artifact. Authorization withdrawal produces zero content events and zero added notifications; the first stop/init/resume logs in; a changed RID increases stable synthetic token requests and bind acknowledgements from two to three. Six real system-notification taps cover platforms 0/8 in cold-process, background and foreground states, without target instrumentation at tap time. Each produces one opened event and one SDK StandardLauncher start, resumes MainActivity with visually rendered OneKey content, and has no ASM/BAL rejection. Platform-0 cold delivery uses loopback TCP; the other messages are synthetic platform-specific payloads posted by the installed SDK builder.
- Runtime artifacts retain unrelated nonfatal Billing-unavailable toasts and occasional Fast Refresh disconnection banners; additional clean screenshots use ordinary UI dismissal. Rendered onboarding and absence of a full-screen render error were verified. An incorrect FCM-cold preparation attempt posted no test notification and is retained as an excluded setup error; it is not counted among the six accepted taps or classified as a JPush failure.
- Acceptance uses normal localhost protocol/HTTP, native lifecycle, host and emulator checks. Actual FCM/Jiguang/APNs vendor-cloud delivery, physical devices/OEMs, other OS versions, consumer iOS execution, real React Native dual-ReactHost integration and notification-detail navigation remain unverified. Synthetic FCM tokens/payloads and native host fixtures do not establish those layers. Local acceptance does not imply an SDK release or merge.

### Nitro HybridObject (`ReactNativeJpush.nitro.ts`)
- Lifecycle & configuration:
  - `setDebugMode(enable: boolean): void`
  - `initPush(): void`
  - `setupWithConfig(appKey: string, channel: string, production: boolean): void`
  - `loadJS(): void`
  - `stopPush(): void`
  - `resumePush(): void`
  - `isPushStopped(): boolean`
  - `getRegistrationID(): string`
  - `isNotificationEnabled(): boolean`
- Tag / Alias / Properties / Mobile Number operations:
  - `addTags(sequence: number, tags: string[]): void`
  - `setTags(sequence: number, tags: string[]): void`
  - `deleteTags(sequence: number, tags: string[]): void`
  - `cleanTags(sequence: number): void`
  - `getAllTags(sequence: number): void`
  - `checkTagBindState(sequence: number, tag: string): void`
  - `setAlias(sequence: number, alias: string): void`
  - `deleteAlias(sequence: number): void`
  - `getAlias(sequence: number): void`
  - `setProperties(sequence: number, propertiesJson: string): void`
  - `deleteProperties(sequence: number, keys: string[]): void`
  - `cleanProperties(sequence: number): void`
  - `setMobileNumber(sequence: number, mobileNumber: string): void`
- Badge & Local Notifications:
  - `setBadge(badge: number, appBadge: number): void`
  - `addLocalNotification(messageID: string, title: string, content: string, broadcastTime: string, extrasJson: string): void`
  - `removeLocalNotification(messageID: string): void`
  - `clearLocalNotifications(): void`
  - `clearAllNotifications(): void`
  - `clearNotificationById(notificationId: number): void`
- JCore Compliance & Diagnostics:
  - `setAuth(auth: boolean): void`
  - `setCountryCode(code: string): void`
  - `enableAutoWakeup(enable: boolean): void`
  - `enableSDKLocalLog(enable: boolean, uploadJgToServer: boolean): void`
  - `readNewLogs(): string`

### JS Wrapper (`JPush` default export & `JCore` named export)
- Preserves 100% method and callback compatibility with `jpush-react-native` and `jcore-react-native` used in `app-monorepo`:
  - `JPush.setLoggerEnable(enable)`
  - `JPush.init({ appKey, channel, production })`
  - `JPush.getRegistrationID((res: { registerID: string }) => void)`
  - `JPush.addTags({ sequence, tags })`, `JPush.updateTags({ sequence, tags })`, `JPush.deleteTag({ sequence, tags | tag })`, `JPush.deleteTags({ sequence })`, `JPush.queryTag({ sequence, tag })`, `JPush.queryTags({ sequence })`
  - `JPush.setAlias({ sequence, alias })`, `JPush.deleteAlias({ sequence })`, `JPush.queryAlias({ sequence })`
  - `JPush.setProperties({ sequence, pros })`, `JPush.deleteProperties({ sequence, pros })`, `JPush.cleanProperties(params?)`
  - `JPush.setBadge({ badge, appBadge })`
  - `JPush.stopPush()`, `JPush.resumePush()`, `JPush.isPushStopped(callback)`
  - `JPush.addConnectEventListener(cb)`, `JPush.addNotificationListener(cb)`, `JPush.addLocalNotificationListener(cb)`, `JPush.addCustomMessageListener(cb)`, `JPush.addTagAliasListener(cb)`, `JPush.addMobileNumberListener(cb)`, `JPush.addInappMessageListener(cb)`, `JPush.addCommandEventListener(cb)`, `JPush.addNotifyButtonClickListener(cb)`, `JPush.removeListener(cb)`

---

## 5.4 Lifecycle and concurrency

1. **SIS Resolution (`JCORESisResolver` / `JCoreTcpEngine`)**:
   - Sends 10-byte UDP SIS packets (`0..1 totalLen`, `2..3 'U','G'`, `4..7 (0x11 << 24) | (seed & 0xffffff)`, `8..9 preEncryptLen`, followed by AES-256-CBC encrypted GZIP JSON) to configured SIS hosts/IPs (`s.jpush.cn:19000`, `sis.jpush.io:19000`, `175.25.48.206:19000`), with HTTPS SIS fallback.
   - Persists `ips`, `ssl_ips`, `sis_ips`, `http_report`, `https_report`, `user`, `ssl_user`, `tcp_report`, `cert_ver`, and `data_report`.
2. **Asymmetric TCP Wire Framing & Handshake**:
   - **Client -> Server (24-byte header)**: `totalLen | 0x8000` (2B), `version` (1B), `command` (1B), `algorithm` (1B: `1`=AES-256-CBC, `2`=SM4-CBC), `flag` (1B), `reserved` (4B), `rid` (2B), `seedOrSid` (4B), `uid` (8B).
   - **Server -> Client (20-byte header)**: `totalLen | 0x8000` (2B), `version` (1B), `command` (1B), `algorithm` (1B), `flag` (1B: bit `0x01` = GZIP after decryption), `reserved` (4B), `rid` (2B), `uid` (8B), with `uint16 statusCode` at `decryptedBody[0..2]`.
   - **Registered Fast-Login**: If valid `uid`, `password`, and `registrationID` are persisted, skips `Register (cmd=0)` and sends `Login (cmd=1, ver=27)` directly.
3. **Pre-Login Queue & Concurrency**:
   - All network I/O and crypto execute on dedicated background queues/executors (`com.onekey.jcore.tcp` on iOS, daemon network workers with synchronized connection ownership on Android).
   - Business requests (`Tag cmd=28`, `Alias cmd=29`, `Properties cmd=38`, `DeviceToken cmd=13`, `MobileNumber cmd=26`, `ThirdToken cmd=27`, `VoIPToken cmd=33`, `LiveActivity cmd=39`) issued before `Login` succeeds are buffered in a bounded FIFO queue and flushed immediately upon `Login` completion.

---

## 5.5 Data, cache, and identity

- **Persistence Keys**:
  - iOS `NSUserDefaults` (`cn.jiguang.jcore.config.*`): `uid`, `password`, `registrationID`, `deviceID`, `appKey`, `channel`, `sisCache`, `tags`, `alias`, `properties`, `mobileNumber`, `pushStopped`.
  - Android `SharedPreferences` (`cn.jiguang.sdk.user.profile` & `cn.jiguang.prefs`): `uid`, `password`, `registration_id`, `device_id`, `app_key`, `channel`, `sis_cache`, `fail_ips`, `tags`, `alias`, `properties`, `push_stopped`.
- **Cross-Process Identity (`:pushcore` on Android)**:
  - `DataShare` (`IDataShare.Stub`), `DataProvider`, `DownloadProvider`, and `MultiSpHelper` synchronize `registration_id`, `uid`, and event bundles between the main UI process and `:pushcore`.

---

## 5.6 Platform contract

| Capability | iOS (`ios/`) | Android (`android/`) | Intentional Divergence |
| --- | --- | --- | --- |
| UDP SIS (10B `'U','G'`) & HTTPS SIS | Implemented (`JCORESisResolver`) | Implemented (`JCoreTcpEngine`) | Same wire format (`platform: 1` on iOS, `platform: 0` on Android) |
| TCP `Register (cmd=0)` | `ver=30` (11 fields, `platform=1`) | `ver=29` (7 fields, `platform=0`) | Matches Jiguang platform-specific `Register` schema |
| TCP `Login (cmd=1, ver=27)` & `Heartbeat (cmd=2, ver=8)` | Implemented | Implemented | Identical wire layout |
| `Tag (cmd=28)` / `Alias (cmd=29)` / `Properties (cmd=38)` | Implemented | Implemented | Identical JSON body & status codes |
| Push Token Reporting | APNs (`cmd=13`), VoIP (`cmd=33`), LiveActivity (`cmd=39`) | DeviceToken (`cmd=13`), Vendor `ThirdToken` (`cmd=27`) | OS notification token mechanisms differ |
| HTTP `/v3/report` & Badge Report | `/v3/report` + `/v3/push/sdk/badge` (RSA+AES128+HMAC) | `/v3/report` (GZIP+AES256+Basic Auth) | Server-side badge endpoint is iOS-specific |

---

## 5.7 Failure, fallback, and safety

- **Server Status Recovery**:
  - `statusCode == 108`: Clears cached `uid`, `password`, and `registrationID` and immediately performs a fresh `Register (cmd=0)` -> `Login (cmd=1)` handshake.
  - `statusCode == 1012`: Clears cached SIS endpoints and `fail_ips`, re-runs SIS resolution, and reconnects.
  - Android `statusCode == 1005 / 1008 / 1009`: Treated as terminal AppKey/package configuration errors; halts auto-reconnect until configuration changes.
- **Multi-IP Waterfall & Reconnect Backoff**:
  - Iterates candidate TCP endpoints (`ips` -> `ssl_ips` -> `sis_ips` -> default fallback); records failed endpoints in `fail_ips` (bounded to 32 entries).
  - Unexpected disconnects trigger exponential backoff reconnect (`3s * 2^attempt`, capped at `120s`), reset on network reachability recovery (`NWPathMonitor` / `ConnectivityManager`) or foreground transition.
- **Request Timeouts**:
  - Each in-flight `rid` request starts a `10s` timeout timer (resolving with `6002` timeout if unacknowledged). Android `ThirdToken (cmd=27)` retries up to 3 times before reporting failure.
- **Native Logging & Privacy Redaction (`@onekeyfe/react-native-native-logger`)**:
  - Both iOS (`JCOREConfiguration` / `RCTJPushModule` / `JCORETcpSession` / `ReactNativeJpush`) and Android (`JLogger` / `JPushModuleReceiver` / `JPushHelper` / `JCoreTcpEngine` / `ReactNativeJpush`) emit diagnostic logs through `OneKeyLog` (`tag = "RN-JPush"`) by default.
  - Logs strictly redact sensitive payloads: notification `title`, `content` / `alert`, and `extras` are never logged (only non-sensitive metadata such as `notificationEventType` and `messageID` is emitted), and user-identifying fields (`alias`, `tags`, `properties`, `mobileNumber`, `password`, device tokens) are excluded from log output. Non-sensitive identifiers (`registrationID`, `uid`, `sequence`, `code`, `cmd`, `rid`, `statusCode`) are logged for diagnostics.

---

## 5.8 Performance and resource budget

- **Main Thread Budget**: Zero blocking socket I/O or cryptographic operations on the main/UI thread. Only `UNUserNotificationCenter` / `UIApplication.shared.applicationIconBadgeNumber` and React Native event dispatch run on the main queue.
- **Resource Bounds**:
  - Pre-login FIFO queue bounded to `128` pending frames.
  - TCP stream receive buffer bounded to `64 KiB` per frame (`frameLen <= 65535`).
  - `fail_ips` set bounded to `32` entries; local SDK diagnostic log buffer bounded to `200` lines.

---

## 5.9 Conformance and acceptance

### Native test sources and execution (`Implemented`)
- Java, Objective-C and header fixtures live in native source files under `tests/android` and `tests/ios`. Python remains a build/execution and artifact-validation launcher; it must not embed native source strings. Host Android tests use real JUnit with explicitly isolated Android/RN shims; Mac Catalyst tests use the existing Objective-C runner. Both host suites run in CI alongside the existing Gradle JUnit and Simulator XCTest suites.
- External Jiguang handshake testing is opt-in with `JPUSH_LIVE_TESTS=1`. The default JUnit result is explicitly skipped. Once enabled, network, malformed-response, nonzero status and assertion failures must fail the test; no broad exception catch may convert them to a pass. Deterministic regressions verify the handshake failure contract without external access.
- The optional `android/fcm` module has a separate Gradle JUnit/Robolectric suite linked by the example Gradle test host and required by CI report checks. It executes the production manager/service with real Firebase message/task types, mocks the external token provider, and verifies success/failure/rotation, stopped/auth guards, metadata/notification/custom conversion, bounded malformed payload handling, notification click intents and diagnostic privacy. Robolectric and mocked providers do not establish FCM cloud delivery; physical-device acceptance remains separate.
- Fixture extraction and CI wiring preserve production transport, JS/Nitro APIs and native logger behavior. The separate FCM foreground/authorization corrections follow section 5.3.
- Post-change audit: native fixture extraction preserves the Objective-C runner byte-for-byte and retains the Java host regression sequence. Local validation passed for the Java host suites, all six Mac Catalyst suites, eight deterministic Gradle core cases (one external test explicitly skipped), thirty-three FCM Robolectric cases, and an explicitly enabled real Jiguang register/login handshake. The extracted fixtures and CI wiring match their native source locations; FCM behavior corrections follow section 5.3, and the logging contract is unchanged. Firebase cloud delivery remains a separate physical-device acceptance gate.

### Source Locations
- Nitro Spec & JS API: `src/ReactNativeJpush.nitro.ts`, `src/index.tsx`
- iOS Implementation: `ios/ReactNativeJpush.swift`, `ios/include/*.h`, `ios/jcore/*.{h,m}`, `ios/jpush/*.{h,m}`, `ios/bridge/*.{h,m}`
- Android Implementation: `android/src/main/java/com/margelo/nitro/reactnativejpush/*.kt`, `android/src/main/java/cn/jiguang/**`, `android/src/main/java/cn/jpush/**`, `android/src/main/java/cn/android/service/JTransitActivity.java`

### Automated Acceptance Suites (using `DEMO_TEST_APP_KEY = "1f2027d3cfb1b51dac664c42"`)
1. **TypeScript & Jest Contract Suite (`src/__tests__/index.test.ts`)**:
   - Exercises `JPush` and `JCore` API calls, parameter normalization, Nitro/NativeModules dispatch, and `DeviceEventEmitter` listeners.
2. **Android JUnit Native Suite (`android/src/test/java/com/margelo/nitro/reactnativejpush/ReactNativeJpushProtocolTest.java`)**:
   - Executed via `./gradlew :onekeyfe_react-native-jpush:testDebugUnitTest`.
   - Verifies `JCoreCrypto` (MD5 seed key, AES-256-CBC, SM4-CBC GM/T 0002-2012 vector, RSA-1024, GZIP), `JCoreWireCodec` (10B UDP SIS, 24B/20B TCP framing, all command payloads with `DEMO_TEST_APP_KEY`), and `JCoreTcpEngine` Mock SIS/TCP/HTTP E2E handshake + `statusCode 108/1012` recovery + `:pushcore` `DataShare` AIDL IPC.
3. **iOS Native XCTest & Standalone E2E Suite (`ios/tests/ReactNativeJpushTests.m` & `scripts/verify_clean_sdk.py`)**:
   - Verifies `JCORECrypto`, `JCOREWireProtocol` (10B UDP SIS, 24B/20B TCP framing, `Register v30` 11-field layout, `Login v27`, `Tag/Alias/Properties`, `VoIP/LiveActivity`, `HTTP /v3/report`, `Badge Report`), and `JCORETcpSession` Mock SIS/TCP/HTTP E2E handshake with `DEMO_TEST_APP_KEY`.
