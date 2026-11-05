// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMLogger.h"
#import "ICMUtils.h"
@implementation ICMLogger
+ (os_log_t)getIntercomLog { return os_log_create("com.intercom.framework", "Intercom"); }
+ (void)log:(NSString *)message type:(os_log_type_t)type {
    if (type == OS_LOG_TYPE_DEBUG && ![ICMUtils debugLoggingEnabled]) return;
    os_log_with_type([self getIntercomLog], type, "%s", message.UTF8String);
}
+ (void)debugLog:(NSString *)message { [self log:message type:OS_LOG_TYPE_DEBUG]; }
+ (void)publicLog:(NSString *)message type:(os_log_type_t)type {
    os_log_with_type([self getIntercomLog], type, "%{public}s", message.UTF8String);
}
+ (void)privateLog:(NSString *)message type:(os_log_type_t)type {
    // The official 19.9.1 release implementation at 0xc70a8 returns immediately.
    (void)message; (void)type;
}
+ (void)privateDebugLog:(NSString *)message { [self privateLog:message type:OS_LOG_TYPE_DEBUG]; }
@end
