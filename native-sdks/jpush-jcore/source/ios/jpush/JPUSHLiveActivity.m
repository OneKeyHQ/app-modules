#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREClientInfo.h"
#import "JCOREDecoder.h"
#import "JCORELog.h"
#import "JPUSHHelper.h"
#import "JPUSHLiveActivity.h"
#import "JPUSHPropertyModels.h"
#import <objc/message.h>
#define LIVE_LOG(asyncValue, flagValue, lineValue, formatValue, ...)           \
  [JCORELog                                                                    \
           log:asyncValue                                                      \
         level:0x3ff                                                           \
          flag:flagValue                                                       \
       context:0                                                               \
          file:                                                                \
              "/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/" \
              "JPush/JPush/Controller/JPushLiveActivityController.m"           \
      function:__PRETTY_FUNCTION__                                             \
          line:lineValue                                                       \
           tag:nil                                                             \
        format:formatValue, ##__VA_ARGS__]
#define LIVE_REQUEST_LOG(lineValue, formatValue)                               \
  [JCORELog                                                                    \
           log:YES                                                             \
         level:0x3ff                                                           \
          flag:0x100                                                           \
       context:0                                                               \
          file:                                                                \
              "/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/" \
              "JPush/JPush/Network/Request/JPUSHLiveActivityTokenReport.m"     \
      function:__PRETTY_FUNCTION__                                             \
          line:lineValue                                                       \
           tag:nil                                                             \
        format:formatValue]
static NSString *const TokenKey = @"JPushLiveActivity_PushTokenKey";
static NSString *const UploadedKey = @"JPushLiveActivity_IsUploadKey";
@implementation JPushLiveActivityController
@synthesize tokensDic = _tokensDic, requestQueryQueue = _requestQueryQueue;
+ (instancetype)shared {
  static id instance;
  static dispatch_once_t once;
  dispatch_once(&once, ^{
    instance = [[super allocWithZone:NULL] init];
  });
  return instance;
}
+ (id)allocWithZone:(NSZone *)zone {
  return [JPushLiveActivityController shared];
}
- (id)copyWithZone:(NSZone *)zone {
  return [JPushLiveActivityController shared];
}
- (instancetype)init {
  if ((self = [super init])) {
    _tokensDic = [NSMutableDictionary dictionary];
    _requestQueryQueue = [NSMutableArray array];
  }
  return self;
}
- (BOOL)isTokenUpload:(id)alias pushToken:(id)token {
  id info = [_tokensDic valueForKey:alias];
  if (info && [info isKindOfClass:NSDictionary.class]) {
    id old = [info valueForKey:TokenKey],
       uploaded = [info valueForKey:UploadedKey];
    if ([old isEqualToString:token])
      return [uploaded boolValue];
  }
  return NO;
}
- (void)updateLiveActivity:(id)alias
                 pushToken:(id)token
                completion:(JPUSHLiveActivityTokenCompletion)completion
                       seq:(long long)seq {
  if (!alias || ![alias isKindOfClass:NSString.class] || ![alias length]) {
    LIVE_LOG(NO, 1, 0x5c, @"liveActivityId is invalid");
    [self callBackCode:6040
        liveActivityAlias:alias
                pushToken:token
               completion:completion
                      seq:seq];
    return;
  }
  NSString *text = [JPUSHHelper convertTokenDataToString:token];
  if ([self isTokenUpload:alias pushToken:text]) {
    LIVE_LOG(YES, 0x100, 0x65, @"%@ %@ has already uploaded, return", alias,
             token);
    [self callBackCode:0
        liveActivityAlias:alias
                pushToken:token
               completion:completion
                      seq:seq];
    return;
  }
  [_tokensDic setValue:@{TokenKey : text ?: @"", UploadedKey : @NO}
                forKey:alias];
  JPushLiveActivityTokenReqItem *item = [JPushLiveActivityTokenReqItem new];
  item.liveActivityAlias = alias;
  item.pushToken = token;
  item.completion = completion;
  item.seq = seq;
  JPUSHLiveActivityTokenReport *request =
      [[JPUSHLiveActivityTokenReport alloc] initWithType:0];
  [request beginTimeoutTimer];
  item.request = request;
  [request reportLiveActivity:alias pushToken:text];
  [self.requestQueryQueue addObject:item];
  [request send];
}
- (void)updateLiveActivity:(id)type
          pushToStartToken:(id)token
                completion:(JPUSHLiveActivityTokenCompletion)completion
                       seq:(long long)seq {
  if (!type || ![type isKindOfClass:NSString.class] || ![type length]) {
    LIVE_LOG(NO, 1, 0x7f, @"attributesType is invalid");
    [self callBackCode:6040
        liveActivityAlias:type
                pushToken:token
               completion:completion
                      seq:seq];
    return;
  }
  NSString *text = [JPUSHHelper convertTokenDataToString:token];
  JPushLiveActivityTokenReqItem *item = [JPushLiveActivityTokenReqItem new];
  item.reportType = 1;
  item.attributeType = type;
  item.pushToStartToken = token;
  item.completion = completion;
  item.seq = seq;
  JPUSHLiveActivityTokenReport *request =
      [[JPUSHLiveActivityTokenReport alloc] initWithType:1];
  [request beginTimeoutTimer];
  item.request = request;
  [request reportLiveActivity:type pushToStartToken:text];
  [self.requestQueryQueue addObject:item];
  [request send];
}
- (void)doCallBacksForResContent:(id)content andRid:(unsigned long long)rid {
  long long code = [content[@"code"] integerValue];
  __block JPushLiveActivityTokenReqItem *item = nil;
  [self.requestQueryQueue
      enumerateObjectsUsingBlock:^(JPushLiveActivityTokenReqItem *candidate,
                                   NSUInteger index, BOOL *stop) {
        if ((unsigned int)candidate.request.rid == rid) {
          item = candidate;
          *stop = YES;
        }
      }];
  if (!item || ![item isKindOfClass:JPushLiveActivityTokenReqItem.class]) {
    LIVE_LOG(NO, 0x20, 0xad,
             @"a unknown error happend, liveactivity request is missed");
    return;
  }
  if (item.isTimeout) {
    LIVE_LOG(NO, 0x20, 0xb3, @"request is timeout,discard seq:%ld",
             (long)item.seq);
    return;
  }
  if (code == 0) {
    if (item.liveActivityAlias.length) {
      NSString *text = [JPUSHHelper convertTokenDataToString:item.pushToken];
      if (text)
        [_tokensDic setValue:@{TokenKey : text, UploadedKey : @YES}
                      forKey:item.liveActivityAlias];
      else
        [_tokensDic setValue:nil forKey:item.liveActivityAlias];
    }
  } else if (code == 3900)
    code = 6039;
  else if (code == 3901)
    code = 6040;
  else if (code == 3902)
    code = 6042;
  else if (code == 3950)
    code = 6041;
  dispatch_async(dispatch_get_main_queue(), ^{
    id completion =
        ((id (*)(id, SEL))objc_msgSend)(item, @selector(completion));
    if (!completion || [item isKindOfClass:NSNull.class])
      return;
    id alias = content[@"alias"];
    if (!alias || ![alias isKindOfClass:NSString.class] || ![alias length])
      alias = item.liveActivityAlias;
    completion = ((id (*)(id, SEL))objc_msgSend)(item, @selector(completion));
    ((JPUSHLiveActivityTokenCompletion)completion)(code, alias, item.pushToken,
                                                   item.seq);
  });
  [item.request stopTimeoutTimer];
  item.isTimeout = YES;
  item.request = nil;
}
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid {
  __block JPushLiveActivityTokenReqItem *item = nil;
  [self.requestQueryQueue
      enumerateObjectsUsingBlock:^(JPushLiveActivityTokenReqItem *candidate,
                                   NSUInteger index, BOOL *stop) {
        if ((unsigned int)candidate.request.rid == rid) {
          item = candidate;
          *stop = YES;
        }
      }];
  if (!item || ![item isKindOfClass:JPushLiveActivityTokenReqItem.class]) {
    LIVE_LOG(NO, 0x20, 0xf3,
             @"a unknown error happend, liveactivity request is missed");
    return;
  }
  dispatch_async(dispatch_get_main_queue(), ^{
    id completion =
        ((id (*)(id, SEL))objc_msgSend)(item, @selector(completion));
    if (completion && ![item isKindOfClass:NSNull.class]) {
      id alias = item.liveActivityAlias;
      completion = ((id (*)(id, SEL))objc_msgSend)(item, @selector(completion));
      ((JPUSHLiveActivityTokenCompletion)completion)(6002, alias,
                                                     item.pushToken, item.seq);
    }
  });
  [item.request stopTimeoutTimer];
  item.isTimeout = YES;
  item.request = nil;
}
- (void)callBackCode:(long long)code
    liveActivityAlias:(id)alias
            pushToken:(id)token
           completion:(JPUSHLiveActivityTokenCompletion)completion
                  seq:(long long)seq {
  dispatch_async(dispatch_get_main_queue(), ^{
    if (completion)
      completion(code, alias, token, seq);
  });
}
@end
@implementation JPUSHLiveActivityTokenReport
@synthesize liveActivityAlias = _liveActivityAlias, pushToken = _pushToken,
            attributesType = _attributesType,
            pushToStartToken = _pushToStartToken, timeoutTimer = _timeoutTimer,
            resendTimer = _resendTimer, type = _type;
- (instancetype)initWithType:(unsigned long long)type {
  if ((self = [super init])) {
    self.controlFlag = 21;
    self.protocolType = 39;
    self.version = 1;
    self.type = type;
  }
  return self;
}
- (void)reportLiveActivity:(id)alias pushToken:(id)token {
  self.liveActivityAlias = alias;
  self.pushToken = token;
}
- (void)reportLiveActivity:(id)type pushToStartToken:(id)token {
  self.attributesType = type;
  self.pushToStartToken = token;
}
- (NSData *)bodyData {
  NSMutableData *data = [NSMutableData new];
  if (_type == 0) {
    [data jcore_appendString:self.pushToken];
    [data jcore_appendString:self.liveActivityAlias];
    [data jcore_appendInt8:1];
  } else if (_type == 1) {
    [data jcore_appendString:self.pushToStartToken];
    [data jcore_appendString:self.attributesType];
    [data jcore_appendInt8:0];
  }
  [data jcore_appendInt8:(unsigned char)[JCOREClientInfo buildType]];
  [data jcore_appendInt8:(unsigned char)[JCOREClientInfo apsType]];
  [data jcore_appendInt8:1];
  return [data copy];
}
- (void)beginTimeoutTimer {
  LIVE_REQUEST_LOG(0x4b, @"liveActivityToken timeout timer fire");
  [self stopTimeoutTimer];
  _timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:20
                                                   target:self
                                                 selector:@selector(onTimeout:)
                                                 userInfo:nil
                                                  repeats:NO];
  [self beginResendTimer];
}
- (void)beginResendTimer {
  LIVE_REQUEST_LOG(0x56, @"liveActivityToken resend timer fire");
  [self stopResendTimer];
  _resendTimer =
      [NSTimer scheduledTimerWithTimeInterval:10
                                       target:self
                                     selector:@selector(resendTimeout:)
                                     userInfo:nil
                                      repeats:NO];
}
- (void)stopTimeoutTimer {
  LIVE_REQUEST_LOG(0x60, @"liveactivityToken timeout timer end");
  if (self.timeoutTimer.valid)
    [self.timeoutTimer invalidate];
  self.timeoutTimer = nil;
  [self stopResendTimer];
}
- (void)stopResendTimer {
  if (self.resendTimer.valid)
    [self.resendTimer invalidate];
  self.resendTimer = nil;
}
- (void)onTimeout:(NSTimer *)timer {
  if (self.timeoutTimer != timer)
    return;
  [self stopTimeoutTimer];
  [[JPushLiveActivityController shared]
      doTimeoutCallBacksForResSeq:(unsigned int)self.rid];
}
- (void)resendTimeout:(NSTimer *)timer {
  if (self.resendTimer != timer)
    return;
  [self stopResendTimer];
  [self send];
}
@end
@implementation JPUSHLiveActivityTokenResponse
@synthesize recvContent = _recvContent;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
  if ((self = [super initWithResponse:response])) {
    unsigned short offset = 0;
    unsigned short code = [JCOREDecoder uInt16FromBuffer:self.bodyData
                                                  offset:&offset];
    offset = 2;
    NSString *alias = [JCOREDecoder stringFromBuffer:self.bodyData
                                              offset:&offset];
    _recvContent = @{@"code" : @(code), @"alias" : alias ?: @""};
  }
  return self;
}
- (void)handleReceipt {
  if (self.recvContent)
    [[JPushLiveActivityController shared]
        doCallBacksForResContent:self.recvContent
                          andRid:self.rid];
}
@end
#endif
