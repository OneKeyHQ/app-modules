#import <TargetConditionals.h>
#import <Foundation/Foundation.h>
#if !TARGET_OS_SIMULATOR
NSString *const kJPUSHBadgeNumberUserDefault = @"jpush_user_default_badge_number";
NSString *const kJPUSHCheckNotiAuthNotification = @"kJPUSHCheckNotiAuthNotification";
#endif
