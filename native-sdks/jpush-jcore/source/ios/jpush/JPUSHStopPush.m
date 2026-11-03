#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREDecoder.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHCache.h"
#import "JPUSHStopPush.h"
extern NSString *const kJCORENetworkDidLoginNotification;
#define STOP_LOG(flagValue, lineValue, formatValue, ...)                       \
  [JCORELog log:YES                                                            \
          level:0x3ff                                                          \
           flag:flagValue                                                      \
        context:0                                                              \
           file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"         \
                "ios-jpush/JPush/JPush/Controller/JPUSHStopPushController.m"   \
       function:__PRETTY_FUNCTION__                                            \
           line:lineValue                                                      \
            tag:nil                                                            \
         format:formatValue, ##__VA_ARGS__]
#define STOP_REQUEST_LOG(lineValue, formatValue)                               \
  [JCORELog                                                                    \
           log:YES                                                             \
         level:0x3ff                                                           \
          flag:0x100                                                           \
       context:0                                                               \
          file:                                                                \
              "/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/" \
              "JPush/JPush/Network/Request/JPUSHStopPushOperation.m"           \
      function:__PRETTY_FUNCTION__                                             \
          line:lineValue                                                       \
           tag:nil                                                             \
        format:formatValue]
static NSString *const StopPushStatusKey = @"kJPUSHStopPushStatusKey";
static NSString *const StopPushTimeKey = @"kJPUSHStopPushStatusSetTimeKey";
@implementation JPUSHStopPushController
@synthesize isRequest = _isRequest, operation = _operation,
            innerStopPushOperation = _innerStopPushOperation,
            completion = _completion;
+ (instancetype)shared {
  static JPUSHStopPushController *instance;
  static dispatch_once_t once;
  dispatch_once(&once, ^{
    instance = [[super allocWithZone:NULL] init];
  });
  return instance;
}
+ (id)allocWithZone:(NSZone *)zone {
  return [JPUSHStopPushController shared];
}
- (id)copyWithZone:(NSZone *)zone {
  return [JPUSHStopPushController shared];
}
- (instancetype)init {
  if ((self = [super init])) {
    _isRequest = NO;
    [NSNotificationCenter.defaultCenter
        addObserver:self
           selector:@selector(didLogin)
               name:kJCORENetworkDidLoginNotification
             object:nil];
  }
  return self;
}
- (void)didLogin {
  static BOOL hasCheck;
  if (!hasCheck) {
    [self checkStopPushStatus];
    hasCheck = YES;
  }
}
- (void)setPushEnable:(BOOL)value
           completion:(JPUSHStopPushCompletion)completion {
  if (_isRequest) {
    STOP_LOG(2, 0x4c, @"setPushEnable is request, please try it later");
    return;
  }
  _completion = [completion copy];
  _isRequest = YES;
  JPUSHStopPushOperation *operation = [JPUSHStopPushOperation new];
  self.operation = operation;
  [operation beginTimeoutTimer];
  operation.stopPush = !value;
  [operation send];
}
- (void)checkStopPushStatus {
  id status = [JPUSHCache objectForKey:StopPushStatusKey];
  id timestamp = [JPUSHCache objectForKey:StopPushTimeKey];
  if (status && [status isKindOfClass:NSNumber.class] && timestamp &&
      [timestamp isKindOfClass:NSString.class] && [timestamp length] &&
      !self.isRequest && [status boolValue]) {
    if ([JCOREUtilities currentTimestamp] - [timestamp doubleValue] >
        2592000.0) {
      STOP_LOG(0x100, 100,
               @"stopPushOperate one month ago with stopPush is yes, sdk auto "
               @"send a report");
      JPUSHStopPushOperation *operation = [JPUSHStopPushOperation new];
      self.innerStopPushOperation = operation;
      [operation beginTimeoutTimer];
      operation.stopPush = YES;
      [operation send];
    }
  }
}
- (void)doCallBacksForResContent:(unsigned long long)code
                          andRid:(unsigned long long)rid {
  if (self.innerStopPushOperation &&
      (unsigned int)self.innerStopPushOperation.rid == rid) {
    [self.innerStopPushOperation stopTimeoutTimer];
    if (!code) {
      [JPUSHCache
          setObject:[NSString
                        stringWithFormat:@"%f",
                                         [JCOREUtilities currentTimestamp]]
             forKey:StopPushTimeKey];
      [JPUSHCache setObject:@(self.innerStopPushOperation.stopPush)
                     forKey:StopPushStatusKey];
    }
    self.innerStopPushOperation = nil;
    return;
  }
  if (self.operation && (unsigned int)self.operation.rid == rid) {
    [self.operation stopTimeoutTimer];
    if (!code) {
      [JPUSHCache
          setObject:[NSString
                        stringWithFormat:@"%f",
                                         [JCOREUtilities currentTimestamp]]
             forKey:StopPushTimeKey];
      [JPUSHCache setObject:@(self.operation.stopPush)
                     forKey:StopPushStatusKey];
    }
    self.operation = nil;
    _isRequest = NO;
    [self callBackCompletion:(long long)code completion:self.completion];
  }
}
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid {
  if (self.innerStopPushOperation &&
      (unsigned int)self.innerStopPushOperation.rid == rid) {
    [self.innerStopPushOperation stopTimeoutTimer];
    self.innerStopPushOperation = nil;
    return;
  }
  if (self.operation && (unsigned int)self.operation.rid == rid) {
    [self.operation stopTimeoutTimer];
    self.operation = nil;
    _isRequest = NO;
    [self callBackCompletion:6002 completion:self.completion];
  }
}
- (void)callBackCompletion:(long long)code
                completion:(JPUSHStopPushCompletion)completion {
  dispatch_async(dispatch_get_main_queue(), ^{
    if (completion)
      completion(code);
  });
}
@end
@implementation JPUSHStopPushOperation
@synthesize stopPush = _stopPush, timeoutTimer = _timeoutTimer;
- (instancetype)init {
  if ((self = [super init])) {
    self.controlFlag = 21;
    self.protocolType = 40;
    self.version = 1;
  }
  return self;
}
- (NSData *)bodyData {
  NSMutableData *data = [NSMutableData new];
  [data jcore_appendInt8:(_stopPush ^ 1)];
  return [data copy];
}
- (void)beginTimeoutTimer {
  STOP_REQUEST_LOG(0x2f, @"setPushEnable timeout timer fire");
  [self stopTimeoutTimer];
  _timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:20
                                                   target:self
                                                 selector:@selector(onTimeout:)
                                                 userInfo:nil
                                                  repeats:NO];
}
- (void)stopTimeoutTimer {
  STOP_REQUEST_LOG(0x39, @"setPushEnable timeout timer end");
  if (self.timeoutTimer.valid)
    [self.timeoutTimer invalidate];
  self.timeoutTimer = nil;
}
- (void)onTimeout:(NSTimer *)timer {
  if (self.timeoutTimer != timer)
    return;
  [self stopTimeoutTimer];
  [[JPUSHStopPushController shared]
      doTimeoutCallBacksForResSeq:(unsigned int)self.rid];
}
@end
@implementation JPUSHStopPushOperationResponse
@synthesize code = _code;
- (void)handleReceipt {
  unsigned short offset = 2;
  self.statusCode = [JCOREDecoder uInt16FromBuffer:self.bodyData
                                            offset:&offset];
  [[JPUSHStopPushController shared] doCallBacksForResContent:self.statusCode
                                                      andRid:self.rid];
}
@end
#endif
