#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class BackgroundReactNativeDelegate;
@class RCTReactNativeFactory;
@class RCTHost;

@interface BackgroundThreadManager : NSObject

/// Shared instance for singleton pattern
+ (instancetype)sharedInstance;

/// Install SharedBridge HostObject into the main (UI) runtime.
/// Call this from your AppDelegate's ReactNativeDelegate hostDidStart: callback.
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

@end

NS_ASSUME_NONNULL_END
