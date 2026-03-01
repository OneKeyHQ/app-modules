#import "LCLogger.h"

static NSString *const kTag = @"LiteCard";

@implementation LCLogger

+ (void)debug:(NSString *)message {
    [self _log:@"debug::" message:message];
}

+ (void)info:(NSString *)message {
    [self _log:@"info::" message:message];
}

+ (void)warn:(NSString *)message {
    [self _log:@"warn::" message:message];
}

+ (void)error:(NSString *)message {
    [self _log:@"error::" message:message];
}

#pragma mark - Private

+ (void)_log:(NSString *)selectorName message:(NSString *)message {
    Class logClass = NSClassFromString(@"ReactNativeNativeLogger.OneKeyLog");
    if (!logClass) {
        logClass = NSClassFromString(@"OneKeyLog");
    }
    if (!logClass) {
        return;
    }
    SEL sel = NSSelectorFromString(selectorName);
    if (![logClass respondsToSelector:sel]) {
        return;
    }
    typedef void (*LogFunc)(id, SEL, NSString *, NSString *);
    LogFunc func = (LogFunc)[logClass methodForSelector:sel];
    func(logClass, sel, kTag, message);
}

@end
