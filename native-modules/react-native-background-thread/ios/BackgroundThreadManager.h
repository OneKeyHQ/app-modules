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

/// Post message to background runner
/// @param message The message to post
- (void)postBackgroundMessage:(NSString *)message;

/// Set callback for handling background messages
/// @param callback The callback block to handle messages
- (void)setOnMessageCallback:(void (^)(NSString *message))callback;

/// Check if message callback is set
/// @return YES if message callback is set, NO otherwise
- (BOOL)checkMessageCallback;

/// Check if background runner is started
@property (nonatomic, readonly) BOOL isStarted;

@end

NS_ASSUME_NONNULL_END
