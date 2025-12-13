#import <BackgroundThreadSpec/BackgroundThreadSpec.h>

@interface BackgroundThread : NativeBackgroundThreadSpecBase <NativeBackgroundThreadSpec>

+ (instancetype)sharedInstance;

- (void)startBackgroundRunner;
- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL;

@end
