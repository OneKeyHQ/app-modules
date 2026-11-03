#import "JCOREConfig.h"
#import "JCOREConfigRequest.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
#define ConfigFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREBanConfig.m"
#define ConfigLog(sourceLine, messageFormat, ...)                                                            \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:ConfigFile                                                                                 \
         function:__PRETTY_FUNCTION__                                                                        \
             line:sourceLine                                                                                 \
              tag:nil                                                                                        \
           format:messageFormat, ##__VA_ARGS__]
@implementation JCOREConfig
@synthesize blockArray = _blockArray, regFingerSwitch = _regFingerSwitch, isFinish = _isFinish;
- (NSMutableArray *)blockArray {
    if (!_blockArray)
        _blockArray = [NSMutableArray array];
    return _blockArray;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _statusLock = dispatch_semaphore_create(1);
        [self setStatusSwitch:YES];
        _isFinish = NO;
        [self getFingerSwitchFromCache];
    }
    return self;
}
- (void)getFingerSwitchFromCache {
    id value = [JCORECacheUtilities persistentObjectForKey:@"JCoreBanRegFingerSwitch"];
    if (value && [value isKindOfClass:[NSNumber class]])
        self.regFingerSwitch = [value boolValue];
}
- (BOOL)statusSwitch {
    dispatch_semaphore_wait(_statusLock, DISPATCH_TIME_FOREVER);
    BOOL result = _statusSwitch;
    dispatch_semaphore_signal(_statusLock);
    return result;
}
- (void)setStatusSwitch:(BOOL)value {
    dispatch_semaphore_wait(_statusLock, DISPATCH_TIME_FOREVER);
    _statusSwitch = value;
    dispatch_semaphore_signal(_statusLock);
}
- (void)pullStatusSwitchWithCompletion:(JCOREConfigStatusCompletion)completion {
    JCOREConfigStatusCompletion block = [completion copy];
    if (!block) {
        ConfigLog(80, @"[%s] completion is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (self.isFinish)
        block(self.statusSwitch);
    else
        [JCOREThread performSel:@selector(addBlockArrar:) target:self withObject:block];
}
- (void)addBlockArrar:(JCOREConfigStatusCompletion)completion {
    JCOREConfigStatusCompletion block = [completion copy];
    if (![JCOREThread isMainThread]) {
        ConfigLog(93, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    if (block)
        [self.blockArray addObject:block];
}
- (void)obtainSwitchStatusWithAppkey:(id)appkey {
    [self obtainSwitchStatusWithArgs:@[ appkey ?: @"", @NO ]];
}
- (void)forceUpdateConfig:(id)args {
    if ([args isKindOfClass:[NSArray class]] && [args count] > 1) {
        id appkey = args[0];
        [self.blockArray addObject:args[1]];
        [self obtainSwitchStatusWithArgs:@[ appkey ?: @"", @YES ]];
    }
}
- (void)obtainSwitchStatusWithArgs:(id)args {
    id appkey = [args isKindOfClass:[NSArray class]] && [args count] ? args[0] : nil;
    BOOL force = NO;
    if ([args isKindOfClass:[NSArray class]] && [args count] >= 2) {
        id value = args[1];
        if ([value isKindOfClass:[NSNumber class]])
            force = [value boolValue];
    }
    if (![appkey length]) {
        ConfigLog(135, @"[%s] appkey is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        ConfigLog(139, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    id cached = [JCORECacheUtilities cacheObjectForKey:@"configStatus"];
    if (cached && [cached isKindOfClass:[NSString class]] && [cached isEqualToString:@"banned"])
        [self setStatusSwitch:NO];
    if (force || self.statusSwitch) {
        [self pullCofigContentWithAppkey:appkey];
        return;
    }
    id next = [JCORECacheUtilities cacheObjectForKey:@"confiNextTime"];
    if (![next length]) {
        [self pullCofigContentWithAppkey:appkey];
        return;
    }
    NSDate *date = [JCOREUtilities transformStrToDate:next];
    if ([[NSDate date] compare:date] == NSOrderedDescending) {
        [self pullCofigContentWithAppkey:appkey];
        return;
    }
    self.isFinish = YES;
}
- (void)pullCofigContentWithAppkey:(id)appkey {
    if (![appkey length]) {
        ConfigLog(175, @"[%s] appkey is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        ConfigLog(179, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    JCOREConfigRequest *request = [JCOREConfigRequest new];
    request.appkey = appkey;
    request.completionHandler = ^(id response, NSError *error) {
      [self handleConfigContentWithResponse:response];
    };
    [request send];
}
- (void)handleConfigContentWithResponse:(id)response {
    if (!response) {
        ConfigLog(200, @"[%s] reponse is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        ConfigLog(204, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    id content = [JCOREUtilities dataToJSON:response];
    ConfigLog(208, @"[%s] config content is %@", __PRETTY_FUNCTION__, content);
    if (!content || ![content isKindOfClass:[NSDictionary class]]) {
        ConfigLog(210, @"[%s] content is bad", __PRETTY_FUNCTION__);
        return;
    }
    id values = content[@"values"];
    if (!values || ![values isKindOfClass:[NSDictionary class]]) {
        ConfigLog(215, @"[%s] values is bad", __PRETTY_FUNCTION__);
        return;
    }
    id status = values[@"status"];
    if (!status || ![status isKindOfClass:[NSString class]]) {
        ConfigLog(220, @"[%s] statusNum is bad", __PRETTY_FUNCTION__);
        return;
    }
    id finger = content[@"reg_finger"];
    if (finger && [finger isKindOfClass:[NSNumber class]] && [finger intValue] == 1)
        self.regFingerSwitch = YES;
    [JCORECacheUtilities setPersistentObject:@(self.regFingerSwitch) forKey:@"JCoreBanRegFingerSwitch"];
    if (![status isEqualToString:@"banned"]) {
        [self setStatusSwitch:YES];
        [JCORECacheUtilities setCacheObject:@"" forKey:@"configStatus"];
        [JCORECacheUtilities setCacheObject:@"" forKey:@"confiNextTime"];
    } else {
        [self setStatusSwitch:NO];
        [JCORECacheUtilities setCacheObject:status forKey:@"configStatus"];
        id next = values[@"nexttime"];
        if (!next || ![next isKindOfClass:[NSNumber class]]) {
            ConfigLog(250, @"[%s] nextTimeNum is bad", __PRETTY_FUNCTION__);
        } else {
            NSInteger interval = [next integerValue];
            if ((NSUInteger)(interval - 1) >= 86399)
                interval = 86400;
            NSDate *date = [NSDate dateWithTimeIntervalSinceNow:interval];
            [JCORECacheUtilities setCacheObject:[JCOREUtilities transformDateToStr:date]
                                         forKey:@"confiNextTime"];
        }
    }
    self.isFinish = YES;
    [self traverseBlockArr];
}
- (void)traverseBlockArr {
    if (![JCOREThread isMainThread]) {
        ConfigLog(267, @"[%s] thread is not JCorethread", __PRETTY_FUNCTION__);
        return;
    }
    if (!self.blockArray.count) {
        ConfigLog(271, @"[%s] block count is 0", __PRETTY_FUNCTION__);
        return;
    }
    for (id block in self.blockArray)
        ((JCOREConfigStatusCompletion)block)(self.statusSwitch);
    [self.blockArray removeAllObjects];
}
@end
