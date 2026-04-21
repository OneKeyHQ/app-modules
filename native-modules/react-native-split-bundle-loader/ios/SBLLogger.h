#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// Lightweight logging wrapper that dynamically dispatches to OneKeyLog.
/// Avoids `@import ReactNativeNativeLogger` which fails in .mm (Objective-C++) files.
@interface SBLLogger : NSObject

+ (void)debug:(NSString *)message;
+ (void)info:(NSString *)message;
+ (void)warn:(NSString *)message;
+ (void)error:(NSString *)message;

@end

NS_ASSUME_NONNULL_END
