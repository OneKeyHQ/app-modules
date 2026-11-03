#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREDecoder.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHTagAliasManagerController.h"
#import "JPUSHTagAliasRequest.h"
#import "JCORENetChannel.h"
#define TAG_REQUEST_LOG(lineNo, formatValue, ...)                                                            \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"       \
                  "Request/JPUSHTagsOperation.m"                                                             \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
#define ALIAS_REQUEST_LOG(lineNo, formatValue, ...)                                                          \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"       \
                  "Request/JPUSHAliasOperation.m"                                                            \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
@implementation JPUSHTagsRequest
@synthesize tags = _tags, operateType = _operateType, currentPage = _currentPage,
            timeoutTimer = _timeoutTimer, resendTimer = _resendTimer;
- (instancetype)init {
    if ((self = [super init])) {
        self.protocolType = 28;
        self.version = 1;
        self.controlFlag = 21;
    }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    NSMutableDictionary *body =
        [@{@"platform" : @"i", @"op" : [JPUSHTagAliasManagerController getTAOpearteString:self.operateType]}
            mutableCopy];
    if (self.operateType != 2 && self.operateType != 3 && self.operateType != 5)
        [body setObject:self.tags.allObjects forKey:@"tags"];
    else if (self.operateType == 5 && self.tags.count)
        [body setObject:self.tags.anyObject forKey:@"tags"];
    if (self.operateType == 3)
        [body setObject:@(self.currentPage ?: 1) forKey:@"curr"];
    [data jcore_appendDataWithLength:[JCOREUtilities jsonToData:body]];
    TAG_REQUEST_LOG(0x34, @"operate tags:%@", body);
    return data;
}
- (void)send {
    __weak JPUSHTagsRequest *weakSelf = self;
    self.completionHandler = ^(id response, NSError *error) {
        if (error && error.code == 6026) {
            [[JPUSHTagAliasManagerController sharedInstance] doOversizeCallBacksForResSeq:weakSelf.rid];
            [weakSelf stopTimeoutTimer];
        }
    };
    [super send];
}
- (void)beginTimeoutTimer {
    TAG_REQUEST_LOG(68, @"tags timeout timer begin");
    [self stopTimeoutTimer];
    _timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:20
                                                     target:self
                                                   selector:@selector(onTimeout:)
                                                   userInfo:nil
                                                    repeats:NO];
    [self beginResendTimer];
}
- (void)beginResendTimer {
    TAG_REQUEST_LOG(79, @"tags resend timer begin");
    [self stopResendTimer];
    _resendTimer = [NSTimer scheduledTimerWithTimeInterval:10
                                                    target:self
                                                  selector:@selector(resendTimeout:)
                                                  userInfo:nil
                                                   repeats:NO];
}
- (void)stopTimeoutTimer {
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
    TAG_REQUEST_LOG(104, @"tags timeout timer fire");
    if (self.timeoutTimer != timer)
        return;
    [self stopTimeoutTimer];
    [[JPUSHTagAliasManagerController sharedInstance] doTimeoutCallBacksForResSeq:self.rid];
}
- (void)resendTimeout:(NSTimer *)timer {
    TAG_REQUEST_LOG(112, @"tags resend timer fire");
    if (self.resendTimer != timer)
        return;
    [self stopResendTimer];
    [JCORENetChannel send:self];
}
@end
@implementation JPUSHAliasRequest
@synthesize alias = _alias, operateType = _operateType, timeoutTimer = _timeoutTimer,
            resendTimer = _resendTimer;
- (instancetype)init {
    if ((self = [super init])) {
        self.protocolType = 29;
        self.version = 2;
        self.controlFlag = 21;
    }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    NSMutableDictionary *body =
        [@{@"platform" : @"i", @"op" : [JPUSHTagAliasManagerController getTAOpearteString:self.operateType]}
            mutableCopy];
    if (self.operateType == 4)
        [body setObject:self.alias forKey:@"alias"];
    [data jcore_appendDataWithLength:[JCOREUtilities jsonToData:body]];
    ALIAS_REQUEST_LOG(0x2d, @"operate alias:%@", body);
    return data;
}
- (void)send {
    __weak JPUSHAliasRequest *weakSelf = self;
    self.completionHandler = ^(id response, NSError *error) {
        if (error && error.code == 6026) {
            [[JPUSHTagAliasManagerController sharedInstance] doOversizeCallBacksForResSeq:weakSelf.rid];
            [weakSelf stopTimeoutTimer];
        }
    };
    [super send];
}
- (void)beginTimeoutTimer {
    ALIAS_REQUEST_LOG(61, @"tags timeout timer fire");
    [self stopTimeoutTimer];
    _timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:20
                                                     target:self
                                                   selector:@selector(onTimeout:)
                                                   userInfo:nil
                                                    repeats:NO];
    [self beginResendTimer];
}
- (void)beginResendTimer {
    ALIAS_REQUEST_LOG(72, @"alias resend timer fire");
    [self stopResendTimer];
    _resendTimer = [NSTimer scheduledTimerWithTimeInterval:10
                                                    target:self
                                                  selector:@selector(resendTimeout:)
                                                  userInfo:nil
                                                   repeats:NO];
}
- (void)stopTimeoutTimer {
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
    [[JPUSHTagAliasManagerController sharedInstance] doTimeoutCallBacksForResSeq:self.rid];
}
- (void)resendTimeout:(NSTimer *)timer {
    if (self.resendTimer != timer)
        return;
    [self stopResendTimer];
    [self send];
}
@end
@implementation JPUSHTagsResponse
@synthesize recvContent = _recvContent;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
    if ((self = [super initWithResponse:response])) {
        unsigned short offset = 0;
        [self setRecvContent:[JCOREUtilities dataToJSON:[JCOREDecoder dataFromBuffer:self.bodyData
                                                                              offset:&offset]]];
        TAG_REQUEST_LOG(0x87, @"tags response content:%@", self.recvContent);
    }
    return self;
}
- (void)handleReceipt {
    [[JPUSHTagAliasManagerController sharedInstance] doCallBacksForResContent:self.recvContent
                                                                       andRid:self.rid];
}
@end
@implementation JPUSHAliasResponse
@synthesize recvContent = _recvContent;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
    if ((self = [super initWithResponse:response])) {
        unsigned short offset = 0;
        [self setRecvContent:[JCOREUtilities dataToJSON:[JCOREDecoder dataFromBuffer:self.bodyData
                                                                              offset:&offset]]];
    }
    return self;
}
- (void)handleReceipt {
    if (self.recvContent)
        [[JPUSHTagAliasManagerController sharedInstance] doCallBacksForResContent:self.recvContent
                                                                           andRid:self.rid];
}
@end
#endif
