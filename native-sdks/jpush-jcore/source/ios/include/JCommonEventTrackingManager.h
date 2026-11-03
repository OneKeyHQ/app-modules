#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
@interface JCommonEventTrackingManager : NSObject {
    BOOL _status;
    BOOL _hasDecide;
    NSMutableArray *_trackingData;
    JCommonReportTimerManager *_timerManager;
}
@property (strong, nonatomic) NSMutableArray *trackingData;
@property (strong, nonatomic) JCommonReportTimerManager *timerManager;
@property (nonatomic) BOOL status;
@property (nonatomic) BOOL hasDecide;
+ (instancetype)sharedInstance;
+ (void)trackUAIDWithCode:(long long)code;
+ (void)trackAppListWithCode:(long long)code;
+ (void)trackLocInfoWithCode:(long long)code;
+ (void)trackGPSWithCode:(long long)code;
+ (void)trackWiFiWithCode:(long long)code;
+ (void)trackCellWithCode:(long long)code;
+ (void)setEventTrackingOn;
+ (void)setEventTrackingOff;
- (void)startReporting;
- (void)stopReporting;
- (void)trackEventWithType:(long long)type code:(long long)code;
- (void)loadDataFromCache;
- (void)saveDataToCache;
- (void)clearDataCache;
- (NSDictionary *)getExceptionTypeDictionary:(long long)type;
- (void)updateExceptionTypeDictionary:(NSDictionary *)dictionary;
- (void)reportTrackingData;
@end
