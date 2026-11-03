#pragma once
#import "JPUSHNewRequest.h"
#import "JPUSHNewResponse.h"
@interface JPUSHPropertyRequest : JPUSHNewRequest {
    long long _operateType;
    NSDictionary *_propertyInfo;
    NSTimer *_timeoutTimer;
    NSTimer *_resendTimer;
}
@property(nonatomic) long long operateType;
@property(nonatomic, strong) NSDictionary *propertyInfo;
@property(nonatomic, strong) NSTimer *timeoutTimer;
@property(nonatomic, strong) NSTimer *resendTimer;
- (void)beginTimeoutTimer;
- (void)beginResendTimer;
- (void)stopTimeoutTimer;
- (void)stopResendTimer;
- (void)onTimeout:(NSTimer *)timer;
- (void)resendTimeout:(NSTimer *)timer;
@end
@interface JPUSHPropertyResponse : JPUSHNewResponse {
    NSDictionary *_recvContent;
}
@property(nonatomic, strong) NSDictionary *recvContent;
@end
