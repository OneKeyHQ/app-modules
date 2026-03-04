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

// Force register event callback during initialization
// This is mainly to handle the scenario of restarting in development environment
- (void)initBackgroundThread {
  [self bindMessageCallback];
}

- (void)bindMessageCallback {
    BackgroundThreadManager *manager = [BackgroundThreadManager sharedInstance];
    __weak __typeof__(self) weakSelf = self;
    [manager setOnMessageCallback:^(NSString *message) {
        [weakSelf emitOnBackgroundMessage:message];
    }];
}

- (void)postBackgroundMessage:(nonnull NSString *)message {
  BackgroundThreadManager *manager = [BackgroundThreadManager
                                      sharedInstance];
  if (!manager.checkMessageCallback) {
   [self bindMessageCallback];
  }
  [[BackgroundThreadManager sharedInstance] postBackgroundMessage:message];
}

+ (NSString *)moduleName
{
  return @"BackgroundThread";
}

@end
