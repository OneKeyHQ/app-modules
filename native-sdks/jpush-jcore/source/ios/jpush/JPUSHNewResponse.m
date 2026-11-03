#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCORELog.h"
#import "JPUSHNewResponse.h"
@interface JPUSHNewResponse () {
    unsigned char algorithm;
    unsigned char protocolType;
    unsigned char _version;
    unsigned short _statusCode;
    unsigned short _rid;
    unsigned long long uid;
    NSData *_bodyData;
    NSString *_errorDescription;
    NSString *_msgData;
}
@end
@implementation JPUSHNewResponse
@dynamic hash, superclass, description, debugDescription;
@synthesize statusCode = _statusCode, rid = _rid, version = _version, bodyData = _bodyData,
            errorDescription = _errorDescription, msgData = _msgData, protocolType = protocolType,
            algorithm = algorithm, uid = uid;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
    if ((self = [super init])) {
        if (!response)
            [JCORELog log:NO
                    level:0x3ff
                     flag:0x20
                  context:0
                     file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"
                          "Request/JPUSHNewResponse.m"
                 function:__PRETTY_FUNCTION__
                     line:0x1b
                      tag:nil
                   format:@"tcpResponse shouldn't be nil"];
        else {
            self.uid = response.uid;
            self.rid = response.rid;
            self.version = response.version;
            self.protocolType = response.protocolType;
            self.bodyData = response.bodyData;
        }
    }
    return self;
}
// The original base response hook is intentionally empty; subclasses process receipts.
- (void)handleReceipt {
}
@end
#endif
