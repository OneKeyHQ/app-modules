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
    Class logClass = NSClassFromString(@"OneKeyLog");
    if (!logClass) {
        return;
    }
    SEL sel = NSSelectorFromString(selectorName);
    if (![logClass respondsToSelector:sel]) {
        return;
    }
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
    [logClass performSelector:sel withObject:kTag withObject:message];
#pragma clang diagnostic pop
}

@end
