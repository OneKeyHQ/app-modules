#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

// Keep optional coder modules out of the Swift compilation unit so SDWebImage
// Objective-C types are imported under a single Swift module identity.
@interface OneKeyImageCoderBridge : NSObject

+ (void)addCodersToManager:(id)manager NS_SWIFT_NAME(addCoders(to:));
+ (void)ensureWebPCoderRegistered;

@end

NS_ASSUME_NONNULL_END
