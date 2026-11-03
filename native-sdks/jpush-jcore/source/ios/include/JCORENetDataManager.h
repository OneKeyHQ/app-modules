#pragma once
#import <Foundation/Foundation.h>
#import "RecoveredProtocols.h"
@class JCommonReportTimerManager, JCORENetDataMoniter;
@interface JCORENetDataManager : NSObject {
    NSMutableDictionary *_netDataMoniterContainer;
    NSString *_uuid;
    JCommonReportTimerManager *_timerManager;
    double _netByte;
    long long _netCount;
    double _netConnByte;
    long long _netConnCount;
}
@property (strong) NSMutableDictionary *netDataMoniterContainer;
@property (copy, nonatomic) NSString *uuid;
@property (strong, nonatomic) JCommonReportTimerManager *timerManager;
@property (nonatomic) double netByte;
@property (nonatomic) long long netCount;
@property (nonatomic) double netConnByte;
@property (nonatomic) long long netConnCount;
+ (instancetype)sharedManager;
+ (void)startHttpMoniter:(NSString *)url request_byte:(long long)bytes;
+ (void)endHttpMoniter:(NSString *)url response_byte:(long long)bytes;
+ (void)startSocketMoniter:(NSUInteger)type host:(NSString *)host port:(long long)port request_byte:(long long)bytes;
+ (void)endSocketMoniter:(NSUInteger)type host:(NSString *)host port:(long long)port response_byte:(long long)bytes;
+ (void)setDataMoniterOn;
+ (void)sendSupersetMoniter:(id)information completionHandler:(RecoveryOpaqueCallback)completion;
- (void)getDataFromCache;
- (void)saveData;
- (void)startTimer;
- (void)stopTimer;
- (void)startHttpMoniter:(NSString *)url request_byte:(long long)bytes;
- (void)endHttpMoniter:(NSString *)url response_byte:(long long)bytes;
- (void)startConnectionMoniter:(NSString *)host port:(long long)port request_byte:(long long)bytes;
- (void)endConnectionMoniter:(NSString *)host port:(long long)port response_byte:(long long)bytes;
- (JCORENetDataMoniter *)findProperMoniter:(NSString *)url;
- (NSDictionary *)reportDataUrlTypeList;
- (NSString *)changeUrl:(NSString *)url;
- (BOOL)getServiceSwitch;
- (void)checkTcpThreshold;
- (void)checkHTTPThreshold;
- (void)reportData;
- (BOOL)isToday:(double)timestamp;
- (BOOL)isSameDay:(double)timestamp otherDay:(double)other;
- (BOOL)isExpire:(double)timestamp otherTimeStamp:(double)other;
@end
