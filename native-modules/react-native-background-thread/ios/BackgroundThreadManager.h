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
/// The module defends against host integration omissions with a one-shot
/// `RCTJavaScriptDidLoadNotification` observer that fires after the new
/// main bridge loads:
///   - For mode='all': if `isStarted` is still NO ~1.5s after JS load,
///     self-respawns the bg runtime (idempotent — startBackgroundRunner's
///     internal guard makes a redundant host-side call a no-op).
///   - For both modes: if `installSharedBridgeInMainRuntime:` was not
///     called on the new host, logs `[BTLogger error:...]` so the
///     integration bug is visible in production logs instead of just
///     surfacing as silent RPC drop.
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
