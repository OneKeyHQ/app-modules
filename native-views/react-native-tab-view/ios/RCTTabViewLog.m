#import "RCTTabViewLog.h"

@implementation RCTTabViewLog

+ (void)debug:(NSString *)tag message:(NSString *)message {
  // Call OneKeyLog.debug(tag, message) via runtime to avoid importing
  // ReactNativeNativeLogger module (which has C++ headers incompatible with Xcode 26).
  Class logClass = NSClassFromString(@"ReactNativeNativeLogger.OneKeyLog");
  if (!logClass) {
    logClass = NSClassFromString(@"OneKeyLog");
  }
  if (logClass) {
    // OneKeyLog.debug(_ tag: String, _ message: String) → ObjC selector "debug::"
    SEL sel = NSSelectorFromString(@"debug::");
    if (!sel || ![logClass respondsToSelector:sel]) {
      sel = NSSelectorFromString(@"debugWithTag:message:");
    }
    if (sel && [logClass respondsToSelector:sel]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
      [logClass performSelector:sel withObject:tag withObject:message];
#pragma clang diagnostic pop
      return;
    }
  }
  // Fallback to NSLog if OneKeyLog is not available
  NSLog(@"[%@] %@", tag, message);
}

@end
