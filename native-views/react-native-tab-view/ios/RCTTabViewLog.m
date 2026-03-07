#import "RCTTabViewLog.h"

static NSString *const kTag = @"RCTTabView";

@implementation RCTTabViewLog

+ (void)debug:(NSString *)tag message:(NSString *)message {
  [self _log:@"debug::" tag:tag message:message];
}

#pragma mark - Private

+ (void)_log:(NSString *)selectorName tag:(NSString *)tag message:(NSString *)message {
  Class logClass = NSClassFromString(@"ReactNativeNativeLogger.OneKeyLog");
  if (!logClass) {
    logClass = NSClassFromString(@"OneKeyLog");
  }
  if (!logClass) {
    NSLog(@"[%@] %@", tag, message);
    return;
  }
  SEL sel = NSSelectorFromString(selectorName);
  if (!sel || ![logClass respondsToSelector:sel]) {
    NSLog(@"[%@] %@", tag, message);
    return;
  }
  typedef void (*LogFunc)(id, SEL, NSString *, NSString *);
  LogFunc func = (LogFunc)[logClass methodForSelector:sel];
  func(logClass, sel, tag, message);
}

@end
