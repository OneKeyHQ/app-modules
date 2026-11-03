#pragma once
#import "JPUSHNewRequest.h"
#import "JPUSHNewResponse.h"
#import "JPUSHService.h"
@interface JPUSHLiveActivityTokenReport : JPUSHNewRequest {
  NSString *_liveActivityAlias, *_pushToken, *_attributesType,
      *_pushToStartToken;
  NSTimer *_timeoutTimer, *_resendTimer;
  unsigned long long _type;
}
@property(nonatomic, copy) NSString *liveActivityAlias, *pushToken,
    *attributesType, *pushToStartToken;
@property(nonatomic, strong) NSTimer *timeoutTimer, *resendTimer;
@property(nonatomic) unsigned long long type;
- (instancetype)initWithType:(unsigned long long)type;
- (void)reportLiveActivity:(id)alias pushToken:(id)token;
- (void)reportLiveActivity:(id)type pushToStartToken:(id)token;
- (void)beginTimeoutTimer;
- (void)beginResendTimer;
- (void)stopTimeoutTimer;
- (void)stopResendTimer;
- (void)onTimeout:(NSTimer *)timer;
- (void)resendTimeout:(NSTimer *)timer;
@end
@interface JPUSHLiveActivityTokenResponse : JPUSHNewResponse {
  NSDictionary *_recvContent;
}
@property(nonatomic, strong) NSDictionary *recvContent;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response;
- (void)handleReceipt;
@end
@interface JPushLiveActivityController : NSObject {
  NSMutableDictionary *_tokensDic;
  NSMutableArray *_requestQueryQueue;
}
@property(nonatomic, strong) NSMutableDictionary *tokensDic;
@property(nonatomic, strong) NSMutableArray *requestQueryQueue;
+ (instancetype)shared;
+ (id)allocWithZone:(NSZone *)zone;
- (id)copyWithZone:(NSZone *)zone;
- (BOOL)isTokenUpload:(id)alias pushToken:(id)token;
- (void)updateLiveActivity:(id)alias
                 pushToken:(id)token
                completion:(JPUSHLiveActivityTokenCompletion)completion
                       seq:(long long)seq;
- (void)updateLiveActivity:(id)type
          pushToStartToken:(id)token
                completion:(JPUSHLiveActivityTokenCompletion)completion
                       seq:(long long)seq;
- (void)doCallBacksForResContent:(id)content andRid:(unsigned long long)rid;
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid;
- (void)callBackCode:(long long)code
    liveActivityAlias:(id)alias
            pushToken:(id)token
           completion:(JPUSHLiveActivityTokenCompletion)completion
                  seq:(long long)seq;
@end
