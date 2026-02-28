#import "OneKeyLogBridge.h"

// OneKeyLog is an @objc public Swift class in ReactNativeNativeLogger pod.
// We use forward declaration + performSelector to avoid importing the
// ReactNativeNativeLogger module (which fails due to C++ headers in NitroModules).
@implementation OneKeyLogBridge

+ (void)info:(NSString *)tag message:(NSString *)message {
    Class cls = NSClassFromString(@"ReactNativeNativeLogger.OneKeyLog");
    if (!cls) {
        cls = NSClassFromString(@"OneKeyLog");
    }
    if (cls) {
        SEL sel = NSSelectorFromString(@"info::");
        if ([cls respondsToSelector:sel]) {
            typedef void (*InfoFunc)(id, SEL, NSString *, NSString *);
            InfoFunc func = (InfoFunc)[cls methodForSelector:sel];
            func(cls, sel, tag, message);
        }
    }
}

@end
