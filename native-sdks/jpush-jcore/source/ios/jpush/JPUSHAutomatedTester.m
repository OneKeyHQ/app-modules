#import "JPUSHAutomatedTester.h"
#import <dispatch/dispatch.h>

@implementation JPUSHAutomatedTester

+ (void)PostAutomatedTestStatusNotification:(NSInteger)status {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter]
            postNotificationName:@"kAutomatedTestStatusNotification"
            object:nil
            userInfo:@{@"StatusCode": [NSNumber numberWithInteger:status]}];
    });
}

@end
