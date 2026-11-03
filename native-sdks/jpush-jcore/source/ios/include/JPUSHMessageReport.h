#pragma once
#import "JPUSHNewReport.h"
@interface JPUSHMessageReportRequest : JPUSHNewReport {
    int _flag;
    long long _messageStateType;
    NSString *_messageID;
    NSString *_msgData;
}
@property(nonatomic, readonly) long long messageStateType;
@property(nonatomic, readonly) NSString *messageID;
@property(nonatomic, readonly) int flag;
@property(nonatomic, copy) NSString *msgData;
- (void)setActiveType:(int)type messageID:(NSString *)messageID flag:(int)flag;
- (void)setCustomPushMsgStatus:(int)type messageID:(NSString *)messageID flag:(int)flag;
- (void)setNotiInMessageType:(int)type messageID:(NSString *)messageID;
- (void)setInAppType:(int)type messageID:(NSString *)messageID;
- (void)handleResponse;
@end
@interface JPUSHMessageoperationReportRequest : JPUSHNewReport
- (void)handleResponse;
@end
@interface JPUSHVoipMessageReport : JPUSHNewReport {
    NSString *_messageID;
}
@property(nonatomic, copy) NSString *messageID;
- (void)handleResponse;
@end
