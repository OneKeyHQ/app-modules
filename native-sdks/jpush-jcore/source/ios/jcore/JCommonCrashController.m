#import "JCommonCrashController.h"
#import "JCORECacheChannel.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#define CrashControllerFile                                                                                  \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonCrashController.m"
@implementation JCommonCrashController
- (instancetype)init {
    return [super init];
}
- (void)dealloc {
    _timerManager = nil;
}
+ (id)content {
    NSMutableArray *list = [NSMutableArray new];
    NSInteger now = (NSInteger)[JCOREUtilities currentTimestamp];
    id stored = [JCORECacheChannel crashLog];
    if (!stored)
        stored = [JCOREFCCache objectForFile:@"__JCOMMONCrashLog"];
    else {
        [JCOREFCCache setObject:stored forKey:@"__JCOMMONCrashLog"];
        [JCORECacheChannel setCrashLog:nil];
    }
    if (![stored isKindOfClass:[NSArray class]])
        return nil;
    [stored enumerateObjectsUsingBlock:^(id item, NSUInteger index, BOOL *stop) {
      if ([item isKindOfClass:[NSArray class]] && [item count])
          [list addObject:item[0]];
    }];
    if (!list.count)
        return nil;
    NSDictionary *content = @{@"type" : @"crash_log", @"itime" : @(now), @"crashlogs" : list};
    [JCORELog log:YES
            level:0x3ff
             flag:8
          context:0
             file:CrashControllerFile
         function:__PRETTY_FUNCTION__
             line:314
              tag:nil
           format:@"CrashReport content %@", content];
    return content;
}
- (void)startCrashCheckStrategy {
    if ([JCommonCrashController content]) {
        [JCommonCrashController reportCrash];
        _isReporting = YES;
    }
    [self startTimer];
}
- (void)stopCrashCheckStrategy {
    [self stopTimer];
}
// Original block descriptor captures self strongly; preserve its ownership.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-retain-cycles"
- (void)startTimer {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        _timerManager.timerFuncBlock = ^{
          if (self->_isReporting) {
              self->_isReporting = NO;
              return;
          }
          [JCommonCrashController reportCrash];
        };
        id previous = [JCORECacheChannel crashCheckTime];
        if ([previous isKindOfClass:[NSNumber class]]) {
            _lastCrashCheckTime = (double)[previous longLongValue];
            [JCORECacheChannel setCrashCheckTime:nil];
        }
        _timerManager.frequency = 3600;
        _timerManager.saveKey = @"__JCOMMONCrashTimer";
        _timerManager.lastSaveTimeStamp = _lastCrashCheckTime;
        _timerManager.firstLoadReport = NO;
    }
    [_timerManager startTimerCheck];
}
#pragma clang diagnostic pop
- (void)stopTimer {
    [_timerManager stopTimerCheck];
}
+ (void)reportCrash {
    id content = [JCommonCrashController content];
    [JCommonReportManager sendAppCrashReport:content
                           completionHandler:^(id response, NSError *error) {
                             if (error)
                                 return;
                             [JCORELog log:YES
                                     level:0x3ff
                                      flag:0x100
                                   context:0
                                      file:CrashControllerFile
                                  function:__PRETTY_FUNCTION__
                                      line:366
                                       tag:nil
                                    format:@"sendAppCrashReport success"];
                             [JCOREFCCache removeObjectForFile:@"__JCOMMONCrashLog"];
                           }];
}
@end
