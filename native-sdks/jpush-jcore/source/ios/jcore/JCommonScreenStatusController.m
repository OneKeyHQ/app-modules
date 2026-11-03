#import "JCommonScreenStatusController.h"
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import <UIKit/UIKit.h>
#define ScreenFile                                                                                           \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonScreenStatusController.m"
@implementation JCommonScreenStatusController
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)init {
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(protectedDataWillBecomeUnavailable:)
                                                     name:UIApplicationProtectedDataWillBecomeUnavailable
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(protectedDataDidBecomeAvailable:)
                                                     name:UIApplicationProtectedDataDidBecomeAvailable
                                                   object:nil];
    }
    return self;
}
- (void)protectedDataWillBecomeUnavailable:(id)notification {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:ScreenFile
         function:__PRETTY_FUNCTION__
             line:35
              tag:nil
           format:@"锁屏"];
    [JCOREThread performSel:@selector(reportSCInfo:) target:self withObject:@NO];
}
- (void)protectedDataDidBecomeAvailable:(id)notification {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:ScreenFile
         function:__PRETTY_FUNCTION__
             line:40
              tag:nil
           format:@"解锁"];
    [JCOREThread performSel:@selector(reportSCInfo:) target:self withObject:@YES];
}
- (NSDictionary *)statusInfo:(BOOL)available {
    double time = [JCOREUtilities currentTimestamp];
    id account = [JCOREClientInfo accountID];
    if (!account || ![account isKindOfClass:NSString.class])
        account = @"";
    id advertising = [JCOREClientInfo advertisingId];
    NSString *device = @"";
    if (advertising && [advertising isKindOfClass:NSString.class] && [advertising length])
        device = [NSString stringWithFormat:@"%@|", advertising];
    return @{
        @"itime" : @((long)time),
        @"type" : @"sc_status",
        @"state" : @(available ? 1 : 3),
        @"account_id" : account,
        @"dev_id" : device ?: @""
    };
}
- (void)reportSCInfo:(id)available {
    [JCommonReportManager sendSCStatus:[self statusInfo:[available boolValue]]
                     completionHandler:^(id response, NSError *error) {
                       if (!error)
                           [JCORELog log:YES
                                   level:0x3ff
                                    flag:0x100
                                 context:0
                                    file:ScreenFile
                                function:"-[JCommonScreenStatusController reportSCInfo:]_block_invoke"
                                    line:70
                                     tag:nil
                                  format:@"reportSCInfo success"];
                     }];
}
@end
