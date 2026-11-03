#import "JCORETcpReports.h"
#import "JCOREClientInfo.h"
#import "JCORELog.h"

@implementation JCOREMessageReceipt
@synthesize messageType = _messageType, messageID = _messageID, msgData = _msgData;
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] initWithData:[super bodyData]];
    [data jcore_appendInt8:_messageType];
    [data jcore_appendInt64:_messageID];
    if (self.msgData.length) [data jcore_appendString:self.msgData];
    return [data copy];
}
@end

@implementation JCORESDKVersion
@synthesize sdkVersion = _sdkVersion, sdkProperty = _sdkProperty;
- (instancetype)init {
    self = [super init];
    if (self) { self.controlFlag = 15; self.protocolType = 26; self.version = 0; }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data jcore_appendInt8:_sdkProperty];
    [data jcore_appendInt8:1];
    [data jcore_appendString:_sdkVersion];
    [JCORELog log:YES level:0x3ff flag:0x80 context:0
        file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCORESDKVersion.m"
        function:"-[JCORESDKVersion bodyData]" line:35 tag:nil format:@"try to upload sdk,property:%d version:%@", _sdkProperty, _sdkVersion];
    return [data copy];
}
@end

@implementation JCOREDeviceTokenReport
@synthesize invalidDeviceToken = _invalidDeviceToken;
- (instancetype)init {
    self = [super init];
    if (self) { self.controlFlag = 21; self.protocolType = 13; self.version = 3; }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    if (!_invalidDeviceToken.length) [data jcore_appendString:[JCOREClientInfo deviceToken]];
    else [data jcore_appendString:_invalidDeviceToken];
    [data jcore_appendInt8:(unsigned char)[JCOREClientInfo buildType]];
    [data jcore_appendInt8:(unsigned char)[JCOREClientInfo apsType]];
    const char *function = "-[JCOREDeviceTokenReport bodyData]";
    if (!_invalidDeviceToken.length) {
        [data jcore_appendInt8:1];
        [JCORELog log:YES level:0x3ff flag:4 context:0
            file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCOREDeviceTokenReport.m"
            function:function line:44 tag:nil format:@"try to upload device token:%@", [JCOREClientInfo deviceToken]];
    } else {
        [data jcore_appendInt8:2];
        [JCORELog log:YES level:0x3ff flag:4 context:0
            file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCOREDeviceTokenReport.m"
            function:function line:47 tag:nil format:@"try to upload and handle invaild device token:%@", _invalidDeviceToken];
    }
    return [data copy];
}
@end

@implementation JCOREVoipTokenReport
- (instancetype)init {
    self = [super init];
    if (self) { self.controlFlag = 21; self.protocolType = 33; self.version = 2; }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    if ([JCOREClientInfo voipToken]) [data jcore_appendString:[JCOREClientInfo voipToken]];
    [data jcore_appendInt8:1];
    [data jcore_appendInt8:(unsigned char)[JCOREClientInfo buildType]];
    [data jcore_appendInt8:(unsigned char)[JCOREClientInfo apsType]];
    return [data copy];
}
@end

@implementation JCOREIDFAReport
- (instancetype)init {
    self = [super init];
    if (self) { self.controlFlag = 5; self.protocolType = 22; self.version = 0; }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    id identifier = [JCOREClientInfo advertisingId];
    if ([identifier isKindOfClass:NSString.class] && [identifier length]) {
        [JCORELog log:YES level:0x3ff flag:4 context:0
            file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCOREIDFAReport.m"
            function:"-[JCOREIDFAReport bodyData]" line:35 tag:nil format:@"upload advertisingIdentifier  %@", identifier];
        [data jcore_appendString:identifier];
    }
    return [data copy];
}
@end
