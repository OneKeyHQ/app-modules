#import <Foundation/Foundation.h>

/// Bridges to OneKeyLog.debug() without importing the ReactNativeNativeLogger Swift module
/// (which pulls in NitroModules C++ headers that fail on Xcode 26).
@interface RCTTabViewLog : NSObject
+ (void)debug:(NSString * _Nonnull)tag message:(NSString * _Nonnull)message;
@end
