#import <BackgroundThreadSpec/BackgroundThreadSpec.h>

@interface BackgroundThread : NativeBackgroundThreadSpecBase <NativeBackgroundThreadSpec>

- (void)startBackgroundRunner;
- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL;
- (void)installSharedBridge;
- (void)loadSegmentInBackground:(double)segmentId
                           path:(NSString *)path
                        resolve:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject;

@end
