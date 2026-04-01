#import "BackgroundThread.h"
#import "BackgroundThreadManager.h"
#import "BTLogger.h"


@implementation BackgroundThread


- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    [BTLogger info:@"BackgroundThread module initialized"];
    return std::make_shared<facebook::react::NativeBackgroundThreadSpecJSI>(params);
}

- (void)startBackgroundRunner {
    [[BackgroundThreadManager sharedInstance] startBackgroundRunner];
}

- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL {
    BackgroundThreadManager *manager = [BackgroundThreadManager sharedInstance];
    [manager startBackgroundRunnerWithEntryURL:entryURL];
}

- (void)installSharedBridge {
    // On iOS, SharedBridge is installed from AppDelegate's hostDidStart: callback
    // via +[BackgroundThreadManager installSharedBridgeInMainRuntime:].
    // This is a no-op here — kept for API parity with Android.
    [BTLogger info:@"installSharedBridge called (no-op on iOS, installed from AppDelegate)"];
}

- (void)loadSegmentInBackground:(double)segmentId
                           path:(NSString *)path
                        resolve:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject {
    BackgroundThreadManager *manager = [BackgroundThreadManager sharedInstance];
    [manager registerSegmentInBackground:@((int)segmentId)
                                    path:path
                              completion:^(NSError * _Nullable error) {
        if (error) {
            reject(@"BG_SEGMENT_LOAD_ERROR", error.localizedDescription, error);
        } else {
            resolve(nil);
        }
    }];
}

+ (NSString *)moduleName
{
  return @"BackgroundThread";
}

@end
