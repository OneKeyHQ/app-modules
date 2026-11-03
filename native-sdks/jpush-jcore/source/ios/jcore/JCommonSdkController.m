#import "JCommonSdkController.h"
#import "JCommonReportManager.h"
#import "JCOREClientInfo.h"
#import "JCOREFCCache.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#define JCommonSdkFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/JCommonSdkController.m"
@implementation JCommonSdkController
+ (void)reportSdkInfo {
    NSDictionary *content = [self content];
    id cached = [JCOREFCCache objectForFile:@"JCOMMONLastSdkInfo"];
    if (![cached isKindOfClass:NSDictionary.class] || ![cached count] || [self isSdkInfoChange:cached newContent:content]) {
        [JCommonReportManager sendSdkInfoReport:content completionHandler:^(id response, NSError *error) {
            if (!error) {
                NSMutableDictionary *saved = [[NSMutableDictionary alloc] initWithDictionary:content];
                [saved addEntriesFromDictionary:@{@"JDeviceAppKey": [JCOREClientInfo appKey], @"JAppVersion": [JCOREUtilities appVersion]}];
                [JCOREFCCache setObject:saved forFile:@"JCOMMONLastSdkInfo"];
                [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonSdkFile function:"+[JCommonSdkController reportSdkInfo]_block_invoke" line:39 tag:nil format:@"sendAppSdkInfoReport success"];
            }
        }];
    } else [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonSdkFile function:__PRETTY_FUNCTION__ line:31 tag:nil format:@"sdk info not change"];
}
+ (NSDictionary *)content {
    return @{@"itime": @((long)[JCOREUtilities currentTimestamp]), @"type": @"sdk_type", @"sdk": @{@"core": @[@((NSInteger)0), @0]}};
}
+ (BOOL)isSdkInfoChange:(NSDictionary *)oldContent newContent:(NSDictionary *)newContent {
    NSString *oldKey = [oldContent objectForKey:@"JDeviceAppKey"], *version = [oldContent objectForKey:@"JAppVersion"];
    NSString *appKey = [JCOREClientInfo appKey], *currentVersion = [JCOREUtilities appVersion];
    if (![oldKey isEqualToString:appKey] || ![version isEqualToString:currentVersion]) return YES;
    NSMutableDictionary *oldDictionary = [NSMutableDictionary dictionaryWithDictionary:oldContent];
    NSMutableDictionary *newDictionary = [NSMutableDictionary dictionaryWithDictionary:newContent];
    [newDictionary addEntriesFromDictionary:@{@"JAppVersion": version, @"JDeviceAppKey": appKey}];
    [oldDictionary removeObjectForKey:@"itime"];
    [newDictionary removeObjectForKey:@"itime"];
    BOOL equal = [oldDictionary isEqualToDictionary:newDictionary];
    if (!equal) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonSdkFile function:__PRETTY_FUNCTION__ line:79 tag:nil format:@"oldDict:%@,newDict:%@", oldDictionary, newDictionary];
    return !equal;
}
@end
