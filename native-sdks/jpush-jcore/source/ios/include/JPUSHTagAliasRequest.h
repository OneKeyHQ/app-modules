#pragma once
#import "JPUSHNewRequest.h"
#import "JPUSHNewResponse.h"
@interface JPUSHTagsRequest : JPUSHNewRequest {
    NSSet *_tags;
    long long _operateType;
    long long _currentPage;
    NSTimer *_timeoutTimer;
    NSTimer *_resendTimer;
}
@property(nonatomic, strong) NSTimer *timeoutTimer;
@property(nonatomic, strong) NSTimer *resendTimer;
@property(nonatomic, strong) NSSet *tags;
@property(nonatomic) long long operateType;
@property(nonatomic) long long currentPage;
- (void)beginTimeoutTimer;
- (void)beginResendTimer;
- (void)stopTimeoutTimer;
- (void)stopResendTimer;
- (void)onTimeout:(NSTimer *)timer;
- (void)resendTimeout:(NSTimer *)timer;
@end
@interface JPUSHAliasRequest : JPUSHNewRequest {
    RecoveryOpaqueCallback _completion;
    NSString *_alias;
    long long _operateType;
    NSTimer *_timeoutTimer;
    NSTimer *_resendTimer;
}
@property(nonatomic, strong) NSTimer *timeoutTimer;
@property(nonatomic, strong) NSTimer *resendTimer;
@property(nonatomic, copy) NSString *alias;
@property(nonatomic) long long operateType;
- (void)beginTimeoutTimer;
- (void)beginResendTimer;
- (void)stopTimeoutTimer;
- (void)stopResendTimer;
- (void)onTimeout:(NSTimer *)timer;
- (void)resendTimeout:(NSTimer *)timer;
@end
@interface JPUSHTagsResponse : JPUSHNewResponse {
    NSDictionary *_recvContent;
}
@property(nonatomic, strong) NSDictionary *recvContent;
@end
@interface JPUSHAliasResponse : JPUSHNewResponse {
    NSDictionary *_recvContent;
}
@property(nonatomic, strong) NSDictionary *recvContent;
@end
