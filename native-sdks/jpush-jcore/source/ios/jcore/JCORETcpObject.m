#import "JCORETcpObject.h"
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#import <CoreFoundation/CoreFoundation.h>
#include <stdlib.h>

@interface JCORETcpObject () {
    NSNumber *_rid;
    unsigned char version;
    unsigned char protocolType;
    unsigned char algorithm;
    unsigned char controlFlag;
    unsigned short rid;
    int autoRetryCount;
    unsigned int _sessionID;
    unsigned long long uid;
    RecoveryOpaqueCallback completionHandler;
    unsigned long long _seed;
    NSString *_moniter_id;
}
@end

#define JCORETcpFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCORETcpObject.m"
@implementation NSMutableData (JCOREMakePacket)
- (void)jcore_appendInt8:(unsigned char)value { [self appendBytes:&value length:1]; }
- (void)jcore_appendInt16:(unsigned short)value { uint16_t network = CFSwapInt16HostToBig(value); [self appendBytes:&network length:2]; }
- (void)jcore_appendInt32:(unsigned int)value { uint32_t network = CFSwapInt32HostToBig(value); [self appendBytes:&network length:4]; }
- (void)jcore_appendInt64:(unsigned long long)value { uint64_t network = CFSwapInt64HostToBig(value); [self appendBytes:&network length:8]; }
- (void)jcore_appendString:(NSString *)string {
    NSData *data = [string dataUsingEncoding:NSUTF8StringEncoding];
    unsigned short length = (unsigned short)data.length;
    [self jcore_appendInt16:length];
    if (length) [self appendData:data];
}
- (void)jcore_appendDataWithLength:(NSData *)data {
    unsigned short length = (unsigned short)data.length;
    [self jcore_appendInt16:length];
    if (length) [self appendData:data];
}
- (BOOL)jcore_setPacketLen {
    NSUInteger length = self.length;
    const char *function = "-[NSMutableData(JCOREMakePacket) jcore_setPacketLen]";
    if (length >= 65536) {
        [JCORELog log:NO level:0x3ff flag:1 context:0 file:JCORETcpFile function:function line:61 tag:nil format:@"packet too long, %lu : %@", (unsigned long)self.length, self];
        [JCORELog log:YES level:0x3ff flag:4 context:0 file:JCORETcpFile function:function line:62 tag:nil format:@"give up this request"];
        return NO;
    }
    if (length >= 8128) {
        [JCORELog log:NO level:0x3ff flag:1 context:0 file:JCORETcpFile function:function line:67 tag:nil format:@"package length beyond limit package length"];
        [JCORELog log:YES level:0x3ff flag:4 context:0 file:JCORETcpFile function:function line:68 tag:nil format:@"give up this request"];
        return NO;
    }
    if (length < 2) {
        [JCORELog log:NO level:0x3ff flag:1 context:0 file:JCORETcpFile function:function line:74 tag:nil format:@"packet too short, %lu : %@", (unsigned long)self.length, self];
        return NO;
    }
    uint16_t network = CFSwapInt16HostToBig((uint16_t)length);
    [self replaceBytesInRange:NSMakeRange(0, 2) withBytes:&network];
    return YES;
}
@end

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
// Original setters preserve backing fields even when getters use shared ClientInfo.
@implementation JCORETcpObject
@synthesize seed = _seed, sessionID = _sessionID, moniter_id = _moniter_id;
@synthesize version = version, protocolType = protocolType, algorithm = algorithm, rid = rid, uid = uid;
@synthesize controlFlag = controlFlag, autoRetryCount = autoRetryCount, completionHandler = completionHandler;
@dynamic hash, superclass, description, debugDescription;
- (unsigned long long)seed { return self.protocolType ? self.uid : _seed; }
- (unsigned long long)uid { return [JCOREClientInfo uid]; }
- (unsigned short)rid {
    if (!_rid) _rid = @([JCOREClientInfo rid]);
    return _rid.unsignedShortValue;
}
- (void)setRid:(unsigned short)value { _rid = @(value); }
- (unsigned int)sessionID { return [JCOREClientInfo sessionID]; }
- (NSData *)headData {
    NSMutableData *data = [[NSMutableData alloc] initWithCapacity:128];
    [data jcore_appendInt16:0];
    [data jcore_appendInt8:self.version];
    [data jcore_appendInt8:self.protocolType];
    unsigned char encryption = [NSUserDefaults.standardUserDefaults integerForKey:@"kJIGUANGEncryptType"] == 1 ? 2 : 1;
    [data jcore_appendInt8:encryption];
    [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORETcpFile function:"-[JCORETcpObject headData]" line:140 tag:nil format:@"tcp headData, algorithm:%d", encryption];
    [data jcore_appendInt8:0];
    [data jcore_appendInt32:0];
    [data jcore_appendInt16:self.rid];
    unsigned int random = arc4random();
    if (self.protocolType == 0) { [data jcore_appendInt32:random]; _seed = random; }
    else [data jcore_appendInt32:self.sessionID];
    [data jcore_appendInt64:self.uid];
    return [data copy];
}
- (NSData *)bodyData { return [self headData]; }
@end
#pragma clang diagnostic pop
