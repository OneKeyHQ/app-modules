#import "JCORESDKControl.h"
#import "JCOREClientInfo.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
@implementation JCORESDKControl
@synthesize statusCode = _statusCode, requestID = _requestID, cmdCode = _cmdCode;
- (instancetype)init { if ((self = [super init])) { self.controlFlag = 13; self.protocolType = 25; self.version = 1; } return self; }
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data jcore_appendInt16:_statusCode];
    [data jcore_appendInt64:_requestID];
    if (_cmdCode == 53) {
        id keys[] = {@"cmd", @"content"};
        id values[] = {@((unsigned char)53), [JCOREClientInfo registrationInfo]};
        NSDictionary *info = [NSDictionary dictionaryWithObjects:values forKeys:keys count:2];
        [JCORELog log:YES level:0x3ff flag:0x80 context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCORESDKControl.m" function:__PRETTY_FUNCTION__ line:38 tag:nil format:@"upload systemInfo:%@", info];
        [data jcore_appendDataWithLength:[JCOREUtilities jsonToData:info]];
    }
    return [data copy];
}
@end
