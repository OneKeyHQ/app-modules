#import <BackgroundThreadSpec/BackgroundThreadSpec.h>

@interface BackgroundThread : NativeBackgroundThreadSpecBase <NativeBackgroundThreadSpec>

- (void)startBackgroundRunner;
- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL;
- (void)installSharedBridge;

@end
