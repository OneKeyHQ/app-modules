#import <BackgroundThreadSpec/BackgroundThreadSpec.h>

@interface BackgroundThread : NativeBackgroundThreadSpecBase <NativeBackgroundThreadSpec>

+ (instancetype)sharedInstance;
- (void)startBackgroundRunner;

@end
