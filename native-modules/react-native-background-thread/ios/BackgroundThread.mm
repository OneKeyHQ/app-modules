#import "BackgroundThread.h"
#import "BackgroundThreadManager.h"


@implementation BackgroundThread


- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeBackgroundThreadSpecJSI>(params);
}

- (void)startBackgroundRunner {
    [[BackgroundThreadManager sharedInstance] startBackgroundRunner];
}

- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL {
    BackgroundThreadManager *manager = [BackgroundThreadManager sharedInstance];
    
    // Set up message callback to bridge it back through this instance
    __weak __typeof__(self) weakSelf = self;
    [manager setOnMessageCallback:^(NSString *message) {
        [weakSelf emitOnBackgroundMessage:message];
    }];
    
    [manager startBackgroundRunnerWithEntryURL:entryURL];
}

- (void)postBackgroundMessage:(nonnull NSString *)message {
    [[BackgroundThreadManager sharedInstance] postBackgroundMessage:message];
}

+ (NSString *)moduleName
{
  return @"BackgroundThread";
}

@end
