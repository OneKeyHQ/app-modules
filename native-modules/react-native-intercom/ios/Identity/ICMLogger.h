// Internal interface recovered from Intercom 19.9.1 Objective-C metadata.
#import <Foundation/Foundation.h>
#import <os/log.h>
@interface ICMLogger : NSObject
+ (os_log_t)getIntercomLog;
+ (void)log:(NSString *)message type:(os_log_type_t)type;
+ (void)debugLog:(NSString *)message;
+ (void)publicLog:(NSString *)message type:(os_log_type_t)type;
+ (void)privateLog:(NSString *)message type:(os_log_type_t)type;
+ (void)privateDebugLog:(NSString *)message;
@end
