#import "JCommonPasteController.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#import <UIKit/UIKit.h>
extern NSString *const kJCORENetworkCmdCtrlNotification;
#define PasteFile                                                                                            \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonPasteController.m"
@implementation JCommonPasteController
@synthesize pasteDatas = _pasteDatas;
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)init {
    self = [super init];
    if (self) {
        [self configInitData];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleCmdControl:)
                                                     name:kJCORENetworkCmdCtrlNotification
                                                   object:nil];
    }
    return self;
}
- (void)configInitData {
    _collectInterval = 0;
    _reportInterval = 3600;
    _action = 0;
    _cacheLimit = 20;
    id cached = [JCOREFCCache objectForKey:@"jcommonPasteCollectInterval"];
    if ([cached isKindOfClass:NSNumber.class])
        _collectInterval = [cached longValue];
    cached = [JCOREFCCache objectForKey:@"jcommonPasteReportInterval"];
    if ([cached isKindOfClass:NSNumber.class])
        _reportInterval = [cached longValue];
    cached = [JCOREFCCache objectForKey:@"jcommonPasteCacheLimit"];
    if ([cached isKindOfClass:NSNumber.class])
        _cacheLimit = [cached intValue];
    cached = [JCOREFCCache objectForKey:@"jcommonPasteAction"];
    if ([cached isKindOfClass:NSNumber.class])
        _action = [cached intValue];
}
- (NSArray *)pasteDatas {
    if (!_pasteDatas) {
        id cached = [JCOREFCCache objectForFile:@"__JCOMMONPasteCollectDatas"];
        _pasteDatas = [[[NSMutableArray alloc] initWithArray:cached] copy];
    }
    return _pasteDatas;
}
- (void)startPasteStrategy {
    if (self.action < 1 || self.action > 3)
        return;
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:PasteFile
         function:__PRETTY_FUNCTION__
             line:105
              tag:nil
           format:@"start pb strategy"];
    if (self.action == 1) {
        [self endCollectAction];
        [self removeReportTimer];
        return;
    }
    [self startCollectAction];
    [self performSelector:@selector(addReportTimer) withObject:nil afterDelay:0];
}
- (void)stopPasteStrategy {
    if (self.action < 1 || self.action > 3)
        return;
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:PasteFile
         function:__PRETTY_FUNCTION__
             line:121
              tag:nil
           format:@"stop pb strategy"];
    [self endCollectAction];
    [self removeReportTimer];
}
- (void)startCollectAction {
    if (self.collectInterval != 0) {
        if (self.collectInterval > 0) {
            if (!_collectTimerManager) {
                _collectTimerManager = [JCommonReportTimerManager manager];
                __weak typeof(self) weakSelf = self;
                _collectTimerManager.timerFuncBlock = ^{
                  [weakSelf triggerCollectTimer:nil];
                };
                _collectTimerManager.lastSaveTimeStamp = 0;
                id saved = [JCOREFCCache objectForKey:@"__JCOMMONPasteCollectCheckTime"];
                if ([saved isKindOfClass:NSNumber.class])
                    _collectTimerManager.lastSaveTimeStamp = (double)[saved longValue];
                _collectTimerManager.saveKey = @"__JCOMMONPasteCollectCheckTime";
                _collectTimerManager.firstLoadReport = YES;
            }
            _collectTimerManager.frequency = (double)self.collectInterval;
            [_collectTimerManager startTimerCheck];
        }
        return;
    }
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handlePasteboardChanged:)
                                                 name:UIPasteboardChangedNotification
                                               object:nil];
    self.monitorPasteboardChange = YES;
    [self triggerCollectTimer:nil];
}
- (void)endCollectAction {
    if (self.monitorPasteboardChange) {
        [[NSNotificationCenter defaultCenter] removeObserver:self
                                                        name:UIPasteboardChangedNotification
                                                      object:nil];
        self.monitorPasteboardChange = NO;
    }
    if (_collectTimerManager) {
        [_collectTimerManager stopTimerCheck];
        _collectTimerManager = nil;
    }
}
- (void)addReportTimer {
    if (self.reportInterval > 0) {
        if (!_reportTimerManager) {
            _reportTimerManager = [JCommonReportTimerManager manager];
            __weak typeof(self) weakSelf = self;
            _reportTimerManager.timerFuncBlock = ^{
              [weakSelf triggerReportTimer:nil];
            };
            _reportTimerManager.lastSaveTimeStamp = 0;
            id saved = [JCOREFCCache objectForKey:@"__JCOMMONPasteReportCheckTime"];
            if ([saved isKindOfClass:NSNumber.class])
                _reportTimerManager.lastSaveTimeStamp = (double)[saved longValue];
            _reportTimerManager.saveKey = @"__JCOMMONPasteReportCheckTime";
            _reportTimerManager.firstLoadReport = YES;
        }
        _reportTimerManager.frequency = (double)self.reportInterval;
        [_reportTimerManager startTimerCheck];
    }
}
- (void)removeReportTimer {
    if (_reportTimerManager) {
        [_reportTimerManager stopTimerCheck];
        _reportTimerManager = nil;
    }
}
- (id)filterPasteArrayData:(id)data {
    if ([data isKindOfClass:NSArray.class]) {
        NSMutableArray *result = [[NSMutableArray alloc] init];
        NSUInteger count = [data count];
        for (NSUInteger i = 0; i < count; i++) {
            id value = data[i];
            if ([value isKindOfClass:NSDictionary.class])
                [result addObject:[self filterPasteDictData:value]];
            else if ([value isKindOfClass:NSArray.class])
                [result addObject:[self filterPasteArrayData:value]];
            else if ([value isKindOfClass:NSString.class] || [value isKindOfClass:NSNumber.class] ||
                     [value isKindOfClass:NSNull.class])
                [result addObject:value];
            else if ([value isKindOfClass:NSData.class]) {
                NSString *text = [[NSString alloc] initWithData:value encoding:NSUTF8StringEncoding];
                if (text)
                    [result addObject:text];
            }
        }
        return [result copy];
    }
    return data;
}
- (id)filterPasteDictData:(id)data {
    if ([data isKindOfClass:NSDictionary.class]) {
        NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
        NSUInteger count = [data count];
        for (NSUInteger i = 0; i < count; i++) {
            id key = [data allKeys][i];
            if ([key isKindOfClass:NSString.class]) {
                id value = [data valueForKey:key];
                if ([value isKindOfClass:NSDictionary.class])
                    [result setValue:[self filterPasteDictData:value] forKey:key];
                else if ([value isKindOfClass:NSArray.class])
                    [result setValue:[self filterPasteArrayData:value] forKey:key];
                else if ([value isKindOfClass:NSString.class] || [value isKindOfClass:NSNumber.class] ||
                         [value isKindOfClass:NSNull.class])
                    [result setValue:value forKey:key];
                else if ([value isKindOfClass:NSData.class]) {
                    NSString *text = [[NSString alloc] initWithData:value encoding:NSUTF8StringEncoding];
                    if (text)
                        [result setValue:text forKey:key];
                }
            }
        }
        return [result copy];
    }
    return data;
}
// The original implementation only emits this log; it does not read the pasteboard.
- (void)triggerCollectTimer:(id)timer {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:PasteFile
         function:__PRETTY_FUNCTION__
             line:262
              tag:nil
           format:@"[%s] Will not access the pboard ", __PRETTY_FUNCTION__];
}
- (void)cachePasteDatas:(id)data {
    [JCOREFCCache setObject:data forFile:@"__JCOMMONPasteCollectDatas"];
}
- (void)removeReportedPasteDatas:(id)data {
    NSMutableArray *remaining = [[NSMutableArray alloc] initWithArray:self.pasteDatas];
    for (NSUInteger i = 0; i < [data count]; i++) {
        id item = data[i];
        if ([remaining containsObject:item])
            [remaining removeObject:item];
    }
    self.pasteDatas = [remaining copy];
    [self cachePasteDatas:self.pasteDatas];
}
- (void)triggerReportTimer:(id)timer {
    if (self.pasteDatas && self.pasteDatas.count) {
        JCommonPasteReportManager *manager = [[JCommonPasteReportManager alloc] init];
        manager.pasteDatas = self.pasteDatas;
        [manager report];
        [self removeReportedPasteDatas:manager.pasteDatas];
        return;
    }
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:PasteFile
         function:__PRETTY_FUNCTION__
             line:338
              tag:nil
           format:@"no pb data, report canceled"];
}
- (NSString *)encryptKeyWithSeed:(long long)seed {
    return [NSString stringWithFormat:@"%016ld", (long)(seed % 4294901760LL)];
}
- (void)handlePasteboardChanged:(id)notification {
    [self triggerCollectTimer:nil];
}
- (void)handleCmdControl:(id)notification {
    if (!notification || ![notification userInfo])
        return;
    id user = [notification userInfo];
    id command = [user isKindOfClass:NSDictionary.class] ? [user objectForKey:@"cmd_content"] : nil;
    if (![command isKindOfClass:NSDictionary.class]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:PasteFile
             function:__PRETTY_FUNCTION__
                 line:370
                  tag:nil
               format:@"handle cmd control cancel, cmd content nil"];
        return;
    }
    id content = command[@"content"];
    if (![content isKindOfClass:NSDictionary.class]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x40
              context:0
                 file:PasteFile
             function:__PRETTY_FUNCTION__
                 line:375
                  tag:nil
               format:@"handle cmd control cancel, content error"];
        return;
    }
    if (![content count]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:PasteFile
             function:__PRETTY_FUNCTION__
                 line:380
                  tag:nil
               format:@"handle cmd control cancel, content nil"];
        return;
    }
    id code = command[@"cmd"];
    if (!code) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:PasteFile
             function:__PRETTY_FUNCTION__
                 line:385
                  tag:nil
               format:@"handle cmd control cancel, cmd nil"];
        return;
    }
    if ([code charValue] != 58)
        return;
    id action = content[@"action"];
    if (![action isKindOfClass:NSNumber.class]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x40
              context:0
                 file:PasteFile
             function:__PRETTY_FUNCTION__
                 line:394
                  tag:nil
               format:@"handle cmd control cancel, action error"];
        return;
    }
    int selected = [action intValue];
    if (selected < 1 || selected > 3) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x40
              context:0
                 file:PasteFile
             function:__PRETTY_FUNCTION__
                 line:399
                  tag:nil
               format:@"handle cmd control cancel, action value out of range"];
        return;
    }
    if (selected == 1) {
        [self endCollectAction];
        [self removeReportTimer];
    } else if (selected == 2) {
        [self triggerCollectTimer:nil];
        [self performSelector:@selector(triggerReportTimer:) withObject:nil afterDelay:0];
        return;
    } else {
        id interval = content[@"interval"];
        if (![interval isKindOfClass:NSNumber.class]) {
            [JCORELog log:YES
                    level:0x3ff
                     flag:0x40
                  context:0
                     file:PasteFile
                 function:__PRETTY_FUNCTION__
                     line:413
                      tag:nil
                   format:@"handle cmd control cancel, interval error"];
            return;
        }
        BOOL validInterval = [interval isKindOfClass:NSNumber.class];
        long intervalValue = validInterval ? [interval longValue] : 0;
        if (!validInterval || intervalValue < 0) {
            [JCORELog log:YES
                    level:0x3ff
                     flag:0x40
                  context:0
                     file:PasteFile
                 function:__PRETTY_FUNCTION__
                     line:421
                      tag:nil
                   format:@"handle cmd control cancel, interval less than 0"];
            return;
        }
        self.collectInterval = intervalValue;
        [JCOREFCCache setObject:@((long)self.collectInterval) forKey:@"jcommonPasteCollectInterval"];
        [self endCollectAction];
        [self startCollectAction];
        id report = content[@"r_interval"];
        long delay = [report isKindOfClass:NSNumber.class] ? [report longValue] : 0;
        if (delay < 1) delay = 3600;
        if (self.collectInterval > delay)
            delay = self.collectInterval;
        if (self.collectInterval <= delay || delay > 0) {
            self.reportInterval = delay;
            [JCOREFCCache setObject:@(delay) forKey:@"jcommonPasteReportInterval"];
            [self removeReportTimer];
            [self performSelector:@selector(addReportTimer) withObject:nil afterDelay:0];
        }
        id limit = content[@"limit"];
        int count = 20;
        if ([limit isKindOfClass:NSNumber.class]) {
            int value = [limit intValue];
            if (value != 0 && value > -2)
                count = value;
        }
        self.cacheLimit = count;
        [JCOREFCCache setObject:@(self.cacheLimit) forKey:@"jcommonPasteCacheLimit"];
    }
    self.action = selected;
    [JCOREFCCache setObject:@(self.action) forKey:@"jcommonPasteAction"];
}
@end
@implementation JCommonPasteReportManager
- (void)report {
    id content = [self pasteboardInfo];
    [JCommonReportManager sendBoardReport:content
                        completionHandler:^(id response, NSError *error) {
                          if (!error)
                              [JCORELog log:YES
                                      level:0x3ff
                                       flag:0x100
                                    context:0
                                       file:PasteFile
                                   function:"-[JCommonPasteReportManager report]_block_invoke"
                                       line:481
                                        tag:nil
                                     format:@"sendPbReport success"];
                          if (self.reportCompletion)
                              self.reportCompletion(error == nil, self.pasteDatas);
                        }];
}
- (NSDictionary *)pasteboardInfo {
    return [@{
        @"itime" : @((long)[JCOREUtilities currentTimestamp]),
        @"type" : @"board",
        @"list" : self.pasteDatas ?: @[]
    } copy];
}
@end
