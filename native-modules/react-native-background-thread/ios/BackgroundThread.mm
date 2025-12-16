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
    [manager startBackgroundRunnerWithEntryURL:entryURL];
}

- (void)postBackgroundMessage:(nonnull NSString *)message {
  BackgroundThreadManager *manager = [BackgroundThreadManager
                                      sharedInstance];
  if (!manager.checkMessageCallback) {
    __weak __typeof__(self) weakSelf = self;
    [manager setOnMessageCallback:^(NSString *message) {
        [weakSelf emitOnBackgroundMessage:message];
    }];
  }
  [[BackgroundThreadManager sharedInstance] postBackgroundMessage:message];
}

+ (NSString *)moduleName
{
  return @"BackgroundThread";
}

@end
