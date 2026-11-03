#pragma once
#import "JPUSHNewRequest.h"
#import "JPUSHNewResponse.h"
typedef void (^JPUSHStopPushCompletion)(NSInteger code);
@interface JPUSHStopPushOperation : JPUSHNewRequest {
  BOOL _stopPush;
  NSTimer *_timeoutTimer;
}
@property(nonatomic, strong) NSTimer *timeoutTimer;
@property(nonatomic) BOOL stopPush;
- (void)beginTimeoutTimer;
- (void)stopTimeoutTimer;
- (void)onTimeout:(NSTimer *)timer;
@end
@interface JPUSHStopPushOperationResponse : JPUSHNewResponse {
  unsigned short _code;
}
@property(nonatomic) unsigned short code;
- (void)handleReceipt;
@end
@interface JPUSHStopPushController : NSObject {
  BOOL _isRequest;
  JPUSHStopPushOperation *_operation;
  JPUSHStopPushOperation *_innerStopPushOperation;
  JPUSHStopPushCompletion _completion;
}
@property(nonatomic) BOOL isRequest;
@property(nonatomic, strong) JPUSHStopPushOperation *operation;
@property(nonatomic, strong) JPUSHStopPushOperation *innerStopPushOperation;
@property(nonatomic, copy) JPUSHStopPushCompletion completion;
+ (instancetype)shared;
+ (id)allocWithZone:(NSZone *)zone;
- (id)copyWithZone:(NSZone *)zone;
- (void)didLogin;
- (void)setPushEnable:(BOOL)value
           completion:(JPUSHStopPushCompletion)completion;
- (void)checkStopPushStatus;
- (void)doCallBacksForResContent:(unsigned long long)code
                          andRid:(unsigned long long)rid;
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid;
- (void)callBackCompletion:(long long)code
                completion:(JPUSHStopPushCompletion)completion;
@end
