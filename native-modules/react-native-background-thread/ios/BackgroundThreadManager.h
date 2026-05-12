#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class BackgroundReactNativeDelegate;
@class RCTReactNativeFactory;
@class RCTHost;

@interface BackgroundThreadManager : NSObject

/// Shared instance for singleton pattern
+ (instancetype)sharedInstance;

/// Install SharedBridge HostObject into the main (UI) runtime.
///
/// MUST be invoked from the host app's RCTReactNativeFactoryDelegate
/// hostDidStart: callback on EVERY main RCTHost lifecycle start — including
/// after a `BackgroundThread.restart` (both modes) reloads the main bridge.
/// The module cannot self-invoke this because it does not own the RCTHost
/// reference; restartWithMode: emits a loud error log if it detects this
/// contract was violated post-reload.
///
/// @param host The RCTHost for the main runtime
+ (void)installSharedBridgeInMainRuntime:(RCTHost *)host;

/// Start background runner with default entry URL
- (void)startBackgroundRunner;

/// Start background runner with custom entry URL
/// @param entryURL The custom entry URL for the background runner
- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL;

/// Check if background runner is started
@property (nonatomic, readonly) BOOL isStarted;

/// Register a HBC segment in the background runtime (Phase 2.5 spike)
/// @param segmentId The segment ID to register
/// @param path Absolute file path to the .seg.hbc file
/// @param completion Callback with nil error on success, or NSError on failure
- (void)registerSegmentInBackground:(NSNumber *)segmentId
                               path:(NSString *)path
                         completion:(void (^)(NSError * _Nullable error))completion;

/// Restart the JS runtime(s). Replaces direct use of `react-native-restart`.
/// Coordinates SharedRPC quiesce + RCTReloadCommand so cross-runtime traffic
/// in flight during reload is silently dropped instead of crashing on a
/// torn-down RCTInstance / dangling jsi::Function callback.
///
/// ## Post-reload contract (host responsibility)
///
/// After `RCTTriggerReloadCommandListeners` rebuilds the main RCTHost, the
/// host app's `RCTReactNativeFactoryDelegate.hostDidStart:` is responsible
/// for re-arming BOTH halves of the integration on the new host:
///   1. `+[BackgroundThreadManager installSharedBridgeInMainRuntime:newHost]`
///      — re-arms the "main" SharedRPC listener; without this main→bg RPC
///      silently breaks even though both runtimes are alive.
///   2. (mode='all' only) `[BackgroundThreadManager.sharedInstance
///      startBackgroundRunner]` — recreates the bg RCTHost since the
///      previous one was released and `isStarted` was reset to NO.
///
/// The module defends against host integration omissions with a two-stage
/// post-reload health-check (`dispatch_after` on the main queue scheduled
/// before the reload is triggered — does NOT depend on
/// `RCTJavaScriptDidLoadNotification`, which is unreliable in bridgeless /
/// NewArch). Stage 1 fires ~3s after `restartWithMode:` returns; stage 2
/// (if needed) ~3s later. The check decides:
///   - Both halves healthy at stage 1 → log success, done.
///   - Anything missing at stage 1 → reschedule stage 2 unconditionally.
///     Earlier designs short-circuited the "main ready, bg not ready"
///     case at stage 1 as a stable signal, but hosts that gate
///     startBackgroundRunner on async work (feature flag, login, network)
///     can be mainReady=YES while the real start call is still inflight;
///     short-circuiting would race them and the host's late start would
///     be silently dropped (now warn-logged on the early-return path).
///   - Stage 2 → final verdict; whatever's missing is logged and self-
///     healed where possible.
///
/// For mode='all' self-respawn, the module replays the host's last entry
/// URL — cached in `startBackgroundRunnerWithEntryURL:` — instead of the
/// default `background.bundle`. This is critical on OTA-equipped hosts:
/// without the cache, self-respawn would load the bundled bg bundle from
/// the IPA while main runs the OTA-updated main bundle, and the next
/// cross-runtime RPC would moduleId-mismatch and crash. The cache makes
/// self-respawn a true safety net for the broken-AppDelegate-wiring case.
/// The bundled default name `"background.bundle"` is intentionally treated
/// as "no real cache" by the self-respawn fallback so the OTA-mismatch
/// warning still fires for hosts that initially bootstrapped with the
/// default URL and never swapped to an OTA-resolved path.
///
/// Concurrent restart() calls are made safe via a monotonic generation
/// counter: each invocation captures its own generation and each health-
/// check stage bails if a newer restart() has superseded it.
///
/// @param mode `@"ui"` to reload only the main runtime (bg stays hot);
///             `@"all"` to reload both. Any other value invokes completion
///             with NSError domain `BackgroundThread` code 10.
/// @param reason Free-form attribution string forwarded to
///               RCTTriggerReloadCommandListeners and host logs.
/// @param completion Invoked once the reload has been triggered. nil error
///                   on success.
- (void)restartWithMode:(NSString *)mode
                 reason:(NSString *)reason
             completion:(void (^)(NSError * _Nullable error))completion;

@end

NS_ASSUME_NONNULL_END
