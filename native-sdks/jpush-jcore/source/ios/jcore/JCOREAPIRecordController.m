#import "JCOREAPIRecordController.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "RecoveredProtocols.h"
#define APIFile                                                                                              \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/ApiRecord/JCOREAPIRecordController.m"
@implementation JCOREAPIRecordController
- (void)tryReport {
    id previous = [JCOREFCCache objectForKey:@"JCOMMON_API_LAST_REPORT_DATE"];
    id stored = [JCOREFCCache objectForFile:@"JCommonAPIRecord"];
    NSMutableDictionary *records =
        stored ? [[NSMutableDictionary alloc] initWithDictionary:stored] : [NSMutableDictionary new];
    NSInteger now = (NSInteger)[JCOREUtilities currentTimestamp];
    if (previous && [previous integerValue] + 86400 > now) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:APIFile
             function:__PRETTY_FUNCTION__
                 line:34
                  tag:nil
               format:@"last api report date:%ld, now: %ld, offset:%ld", (long)[previous integerValue],
                      (long)now, (long)(now - [previous integerValue])];
        return;
    }
    if (!records.allKeys.count) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:APIFile
             function:__PRETTY_FUNCTION__
                 line:60
                  tag:nil
               format:@"api record is empty"];
        return;
    }
    [JCOREFCCache setObject:@(now) forKey:@"JCOMMON_API_LAST_REPORT_DATE"];
    [JCOREFCCache setObject:nil forFile:@"JCommonAPIRecord"];
    NSMutableArray *list = [NSMutableArray new];
    for (id method in records.allKeys)
        if (![method isEqualToString:@"JCOMMON_API_LAST_REPORT_DATE"])
            [list addObject:@{@"method" : method, @"count" : records[method]}];
    NSDictionary *content = @{@"itime" : @(now), @"type" : @"api_record", @"record_list" : list};
    [JCommonReportManager sendAPIReport:content
                      completionHandler:^(id response, NSError *error) {
                        if (!error)
                            [JCORELog log:YES
                                    level:0x3ff
                                     flag:0x100
                                  context:0
                                     file:APIFile
                                 function:__PRETTY_FUNCTION__
                                     line:56
                                      tag:nil
                                   format:@"api record success"];
                      }];
}
+ (void)apiRecord:(id)type methodName:(id)method {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:APIFile
         function:__PRETTY_FUNCTION__
             line:65
              tag:nil
           format:@"apiRecord: %@  , method: %@", type, method];
    id records = [JCOREFCCache objectForFile:@"JCommonAPIRecord"];
    if (!records)
        records = [NSMutableDictionary new];
    NSString *key = [NSString stringWithFormat:@"%@#%@", type, method];
    NSInteger count = records[key] ? [records[key] integerValue] + 1 : 1;
    records[key] = @(count);
    [JCOREFCCache setObject:records forFile:@"JCommonAPIRecord"];
}
@end
