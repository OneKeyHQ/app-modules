#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREDecoder.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHPropertyManagerController.h"
#import "JPUSHPropertyRequest.h"
#import "JCORENetChannel.h"
#define PROPERTY_REQUEST_LOG(lineNo, formatValue, ...)                                                       \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"       \
                  "Request/JPUSHPropertyOperation.m"                                                         \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
@implementation JPUSHPropertyRequest
@synthesize operateType = _operateType, propertyInfo = _propertyInfo, timeoutTimer = _timeoutTimer,
            resendTimer = _resendTimer;
- (instancetype)init {
    if ((self = [super init])) {
        self.protocolType = 38;
        self.version = 0;
        self.controlFlag = 21;
    }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data jcore_appendInt8:(unsigned char)self.operateType];
    [data jcore_appendDataWithLength:[JCOREUtilities jsonToData:self.propertyInfo ?: @{}]];
    return data;
}
- (void)send {
    __weak JPUSHPropertyRequest *weakSelf = self;
    self.completionHandler = ^(id response, NSError *error) {
        if (error && error.code == 6026) {
            [[JPUSHPropertyManagerController sharedInstance] doOversizeCallBacksForResSeq:weakSelf.rid];
            [weakSelf stopTimeoutTimer];
        }
    };
    [super send];
}
- (void)beginTimeoutTimer {
    PROPERTY_REQUEST_LOG(0x37, @"property timeout timer begin");
    [self stopTimeoutTimer];
    _timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:20
                                                     target:self
                                                   selector:@selector(onTimeout:)
                                                   userInfo:nil
                                                    repeats:NO];
    [self beginResendTimer];
}
- (void)beginResendTimer {
    PROPERTY_REQUEST_LOG(0x42, @"property resend timer begin");
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
    PROPERTY_REQUEST_LOG(0x5b, @"property timeout timer fire");
    if (self.timeoutTimer != timer)
        return;
    [self stopTimeoutTimer];
    [[JPUSHPropertyManagerController sharedInstance] doTimeoutCallBacksForResSeq:self.rid];
}
- (void)resendTimeout:(NSTimer *)timer {
    PROPERTY_REQUEST_LOG(0x63, @"tags resend timer fire");
    if (self.resendTimer != timer)
        return;
    [self stopResendTimer];
    [JCORENetChannel send:self];
}
@end
@implementation JPUSHPropertyResponse
@synthesize recvContent = _recvContent;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
    if ((self = [super initWithResponse:response])) {
        unsigned short offset = 0;
        unsigned short code = [JCOREDecoder uInt16FromBuffer:self.bodyData offset:&offset];
        id message = @{};
        if (code) {
            offset = 2;
            id decoded = [JCOREUtilities dictionaryWithJsonString:[JCOREDecoder stringFromBuffer:self.bodyData
                                                                                          offset:&offset]];
            if (decoded && [decoded isKindOfClass:NSDictionary.class])
                message = decoded;
        }
        _recvContent = @{@"code" : @(code), @"msg" : message};
    }
    return self;
}
- (void)handleReceipt {
    if (self.recvContent)
        [[JPUSHPropertyManagerController sharedInstance] doCallBacksForResContent:self.recvContent
                                                                           andRid:self.rid];
}
@end
#endif
