#import "JCommonDeviceController.h"
#import "JCORECacheChannel.h"
#import "JCOREClientInfo.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#define DeviceFile                                                                                           \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonDeviceController.m"
@implementation JCommonDeviceController
- (void)startDeviceCheckStrategy {
    [self startTimer];
}
- (void)stopDeviceCheckStrategy {
    [self stopTimer];
}
- (void)dealloc {
    _timerManager = nil;
}
- (void)startTimer {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        __weak typeof(self) weakSelf = self;
        _timerManager.timerFuncBlock = ^{
          [weakSelf reportDeviceInfo];
        };
        id previous = [JCORECacheChannel deviceInfoCheckTime];
        if ([previous isKindOfClass:NSNumber.class]) {
            [JCORECacheChannel setDeviceInfoCheckTime:nil];
            _timerManager.lastSaveTimeStamp = (double)[previous longLongValue];
        }
        _timerManager.frequency = 86400;
        _timerManager.saveKey = @"__JCOMMONCheckTime";
        _timerManager.firstLoadReport = YES;
    }
    [_timerManager startTimerCheck];
}
- (void)stopTimer {
    [_timerManager stopTimerCheck];
}
- (NSDictionary *)content {
    double time = [JCOREUtilities currentTimestamp];
    return @{
        @"itime" : @((long)time),
        @"type" : @"device_info",
        @"os_version" : [JCOREUtilities systemVersion],
        @"timezone" : [JCOREUtilities currentTimeZone],
        @"language" : [JCOREUtilities systemLanguage],
        @"resolution" : [JCOREUtilities currentResolution],
        @"model" : [JCOREUtilities modelName] ?: @"",
        @"adid" : [JCOREClientInfo advertisingId] ?: @"",
        @"did" : [JCOREUtilities devicePID:[JCOREClientInfo appKey]] ?: @"",
        @"mac" : [JCOREUtilities deviceMacAddress] ?: @"",
        @"font" : @([JCOREUtilities systemFontSize]),
        @"bt" : [JCOREUtilities bt] ?: @"",
        @"cc" : [JCOREUtilities countryCode] ?: @"",
        @"dn" : [JCOREUtilities deviceName] ?: @"",
        @"ca" : [JCOREUtilities ctCarrierName] ?: @"",
        @"mm" : [JCOREUtilities memory_size] ?: @"",
        @"ds" : [NSString stringWithFormat:@"%@", @([[JCOREUtilities storage_size] doubleValue])],
        @"sf" : [JCOREUtilities sysFileTime] ?: @"",
        @"md" : [JCOREUtilities model_id] ?: @"",
        @"mnt" : [JCOREUtilities mntId] ?: @"",
        @"fi" : [JCOREUtilities fileInitTime] ?: @""
    };
}
- (void)reportDeviceInfo {
    id content = [self content];
    id previous = [JCORECacheChannel lastDeviceInfo];
    if (!previous)
        previous = [JCOREFCCache objectForFile:@"JCOMMONLastDeviceInfo"];
    else
        [JCORECacheChannel setLastDeviceInfo:nil];
    if (![previous isKindOfClass:NSDictionary.class] || ![previous count] ||
        [self isDeviceInfoChange:previous newContent:content]) {
        [JCommonReportManager
            sendAppDeviceInfoReport:content
                  completionHandler:^(id response, NSError *error) {
                    if (!error) {
                        NSMutableDictionary *saved = [[NSMutableDictionary alloc] initWithDictionary:content];
                        [saved addEntriesFromDictionary:@{
                            @"JDeviceAppKey" : [JCOREClientInfo appKey],
                            @"JAppVersion" : [JCOREUtilities appVersion]
                        }];
                        [JCOREFCCache setObject:saved forFile:@"JCOMMONLastDeviceInfo"];
                        [JCORELog log:YES
                                level:0x3ff
                                 flag:0x100
                              context:0
                                 file:DeviceFile
                             function:"-[JCommonDeviceController reportDeviceInfo]_block_invoke"
                                 line:119
                                  tag:nil
                               format:@"sendAppDeviceInfoReport success"];
                    }
                  }];
    } else
        [JCORELog log:YES
                level:0x3ff
                 flag:8
              context:0
                 file:DeviceFile
             function:__PRETTY_FUNCTION__
                 line:111
                  tag:nil
               format:@"device info not change"];
}
- (BOOL)isDeviceInfoChange:(id)oldContent newContent:(id)newContent {
    id oldKey = [oldContent objectForKey:@"JDeviceAppKey"];
    id oldVersion = [oldContent objectForKey:@"JAppVersion"];
    id appKey = [JCOREClientInfo appKey];
    id appVersion = [JCOREUtilities appVersion];
    if (![oldKey isEqualToString:appKey] || ![oldVersion isEqualToString:appVersion])
        return YES;
    NSMutableDictionary *old = [NSMutableDictionary dictionaryWithDictionary:oldContent];
    NSMutableDictionary *current = [NSMutableDictionary dictionaryWithDictionary:newContent];
    [current addEntriesFromDictionary:@{@"JAppVersion" : oldVersion, @"JDeviceAppKey" : appKey}];
    [old removeObjectForKey:@"itime"];
    [current removeObjectForKey:@"itime"];
    BOOL equal = [old isEqualToDictionary:current];
    if (!equal)
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:DeviceFile
             function:__PRETTY_FUNCTION__
                 line:141
                  tag:nil
               format:@"oldDict:%@,newDict:%@", old, current];
    return !equal;
}
@end
