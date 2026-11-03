#pragma once
#import "JPUSHNewReport.h"
@class JPUSHBadgeNumberReportRequest;
@interface JPUSHBadgeNumberController : NSObject {
    BOOL _isSendingLock;
    BOOL _isClearOut;
    __weak NSTimer *_delaySendTimer;
    NSUInteger _delaySendIntervalIndex;
    BOOL _needAutoSend;
    NSNumber *_badgeValue;
    JPUSHBadgeNumberReportRequest *_badgeReport;
}
@property(nonatomic, strong) NSNumber *badgeValue;
@property(nonatomic, strong) JPUSHBadgeNumberReportRequest *badgeReport;
@property(nonatomic) BOOL isSendingLock;
- (void)checkAutoSend;
- (void)requestTimeout;
- (void)setBadge:(NSNumber *)value;
- (void)becomeActive;
- (void)willInactive;
- (void)clearBadgeInUserDefault;
- (void)setBadgeInUserDefault:(NSNumber *)value;
- (void)startDelaySendBadge:(JPUSHBadgeNumberReportRequest *)report;
- (void)startSendSelfToSpaceAddress:(JPUSHBadgeNumberReportRequest *)report;
- (void)stopDelayTimer:(BOOL)keep commandSource:(JPUSHBadgeNumberReportRequest *)report;
- (void)stopDelayTimer:(BOOL)keep;
- (void)doSendReport:(NSTimer *)timer;
@end
@interface JPUSHBadgeNumberReportRequest : JPUSHNewReport {
    NSMutableDictionary *_badgeBody;
    NSData *_bodyData;
    BOOL _isAgainSend;
    NSArray *addressList;
    RecoveryOpaqueCallback completionHandler;
    long long _badgeCount;
    NSString *_aesKey;
}
@property(nonatomic, copy) NSString *aesKey;
@property(nonatomic) long long badgeCount;
@property(nonatomic) BOOL isAgainSend;
- (id)badgeBody;
- (void)setBadgeNumber:(unsigned int)number;
- (void)handleResponse;
- (NSMutableURLRequest *)generateReportRequest;
- (NSString *)authorizationString;
- (NSString *)publicKey;
- (BOOL)isSendRequestLock;
- (void)setIsSendRequestLock:(BOOL)value;
- (BOOL)startDelaySend;
@end
