// Original dependency interfaces; complete product linkage remains unresolved.
#import <Foundation/Foundation.h>
#import "../Identity/ICMUtils.h"
@class ICMUserIdentity;
#import "ICMConfig.h"
#import "ICMHTTPBridge.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate14ErrorProcessor")))
@interface ICMRecoveredErrorProcessor : NSObject
+ (NSError *)generateInvalidDeviceTokenError;
+ (BOOL)isUserJwtExpired:(NSError *)error;
+ (NSError *)generateHTTPErrorWithCode:(NSInteger)code message:(NSString *)message;
@end
