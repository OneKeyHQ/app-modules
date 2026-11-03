#import "JCOREDevidBanConfig.h"
#import "JCOREDevidBanRequest.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
#define DevidFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREDevidBanConfig.m"
#define DevidLog(lineValue, fmt, ...)                                                                        \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:DevidFile                                                                                  \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
@implementation JCOREDevidBanConfig
@synthesize completionArray = _completionArray;
- (instancetype)init {
    self = [super init];
    if (self) {
        _isBan = NO;
        _isFinish = NO;
    }
    return self;
}
- (void)pullStatusSwitchWithCompletionWithAppkey:(id)appkey andHandle:(void (^)(BOOL))completion {
    if (!completion) {
        DevidLog(33, @"[%s] completion is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (self.isFinish)
        completion(_isBan);
    else
        [JCOREThread performSel:@selector(addBlockArrar:) target:self withObject:completion];
}
- (void)obtainDevidBanSwitchStatusWithAppkey:(id)appkey {
    [self obtainDevidBanSwitchStatusWithArgs:@[ appkey ?: @"", @NO ]];
}
- (void)obtainDevidBanSwitchStatusWithArgs:(id)args {
    // Original takes the appkey only for a one-element array, despite its two-element wrapper.
    id appkey = [args isKindOfClass:[NSArray class]] && [args count] == 1 ? args[0] : nil;
    BOOL force = NO;
    if ([args isKindOfClass:[NSArray class]] && [args count] >= 2 && [args[1] isKindOfClass:[NSNumber class]])
        force = [args[1] boolValue];
    if (![appkey length]) {
        DevidLog(68, @"[%s] appkey is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        DevidLog(72, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    id cached = [JCORECacheUtilities cacheObjectForKey:@"devidBanInfo"];
    if (!force && cached && [cached isKindOfClass:[NSDictionary class]]) {
        if ([[cached allKeys] containsObject:@"code"])
            _isBan = [cached[@"code"] intValue] == 1;
        if ([[cached allKeys] containsObject:@"nexttime"]) {
            NSDate *date = [JCOREUtilities stringToDate:cached[@"nexttime"]];
            if (date && [[NSDate date] compare:date] != NSOrderedDescending) {
                _isFinish = YES;
                return;
            }
        }
    }
    [self pullCofigContentWithAppkey:appkey];
}
- (void)pullCofigContentWithAppkey:(id)appkey {
    if (![appkey length]) {
        DevidLog(110, @"[%s]devid ban appkey is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        DevidLog(114, @"[%s]devid ban thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    JCOREDevidBanRequest *request = [JCOREDevidBanRequest new];
    request.appkey = appkey;
    __weak JCOREDevidBanConfig *weakSelf = self;
    request.completionHandler = ^(id response, NSError *error) {
      JCOREDevidBanConfig *strongSelf = weakSelf;
      if (strongSelf)
          [strongSelf handleConfigContentWithResponse:response];
    };
    [request send];
}
- (void)handleConfigContentWithResponse:(id)response {
    if (!response) {
        DevidLog(139, @"[%s]devid ban reponse is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        DevidLog(143, @"[%s]devid ban thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    id content = [JCOREUtilities dataToJSON:response];
    DevidLog(147, @"[%s]devid ban devid config content is %@", __PRETTY_FUNCTION__, content);
    if (!content || ![content isKindOfClass:[NSDictionary class]]) {
        DevidLog(149, @"[%s]devid ban content is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![[content allKeys] containsObject:@"code"] || ![[content allKeys] containsObject:@"nexttime"]) {
        DevidLog(153, @"[%s]devid ban Response is bad", __PRETTY_FUNCTION__);
        return;
    }
    _isBan = [content[@"code"] integerValue] == 1;
    [JCORECacheUtilities setCacheObject:nil forKey:@"devidBanInfo"];
    NSDate *date = [NSDate dateWithTimeIntervalSinceNow:(double)[content[@"nexttime"] floatValue]];
    NSString *next = [JCOREUtilities dateToString:date];
    if (!next.length) {
        DevidLog(167, @"[%s]devid ban nexttime is not error", __PRETTY_FUNCTION__);
        return;
    }
    [JCORECacheUtilities setCacheObject:@{@"code" : @(_isBan), @"nexttime" : next} forKey:@"devidBanInfo"];
    _isFinish = YES;
    [self traverseBlockArr];
}
- (void)traverseBlockArr {
    if (![JCOREThread isMainThread]) {
        DevidLog(181, @"[%s] thread is not JCorethread", __PRETTY_FUNCTION__);
        return;
    }
    if (!self.completionArray.count) {
        DevidLog(185, @"[%s] block count is 0", __PRETTY_FUNCTION__);
        return;
    }
    for (void (^completion)(BOOL) in _completionArray)
        if (completion)
            completion(_isBan);
    [_completionArray removeAllObjects];
}
- (void)addBlockArrar:(void (^)(BOOL))completion {
    if (![JCOREThread isMainThread]) {
        DevidLog(216, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    if (completion)
        [self.completionArray addObject:completion];
}
- (NSMutableArray *)completionArray {
    if (!_completionArray)
        _completionArray = [NSMutableArray array];
    return _completionArray;
}
@end
