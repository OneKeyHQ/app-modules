#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class BackgroundReactNativeDelegate;
@class RCTReactNativeFactory;

@interface BackgroundThreadManager : NSObject

/// Shared instance for singleton pattern
+ (instancetype)sharedInstance;

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

/// Check if background runner is started
@property (nonatomic, readonly) BOOL isStarted;

@end

NS_ASSUME_NONNULL_END
