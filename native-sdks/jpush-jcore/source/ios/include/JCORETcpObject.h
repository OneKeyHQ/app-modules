#pragma once
#import "RecoveredProtocols.h"

@protocol JCORETcpProtocol <JCORENetChannelProtocol>
@property (nonatomic) unsigned char version;
@property (nonatomic) unsigned char protocolType;
@optional
- (NSData *)headData;
- (NSData *)bodyData;
@property (nonatomic) unsigned char algorithm;
@property (nonatomic) unsigned short rid;
@property (nonatomic) unsigned long long uid;
@property (nonatomic) unsigned long long seed;
@property (nonatomic) unsigned char controlFlag;
@property (nonatomic) int autoRetryCount;
@property (copy, nonatomic) NSString *moniter_id;
@end

@interface JCORETcpObject : NSObject <JCORETcpProtocol>
@property (nonatomic) unsigned long long seed;
@property (nonatomic) unsigned int sessionID;
@property (copy, nonatomic) NSString *moniter_id;
@property (nonatomic) unsigned char version;
@property (nonatomic) unsigned char protocolType;
@property (nonatomic) unsigned char algorithm;
@property (nonatomic) unsigned short rid;
@property (nonatomic) unsigned long long uid;
@property (nonatomic) unsigned char controlFlag;
@property (nonatomic) int autoRetryCount;
@property (copy, nonatomic) RecoveryOpaqueCallback completionHandler;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (NSData *)headData;
- (NSData *)bodyData;
@end

@interface NSMutableData (JCOREMakePacket)
- (void)jcore_appendInt8:(unsigned char)value;
- (void)jcore_appendInt16:(unsigned short)value;
- (void)jcore_appendInt32:(unsigned int)value;
- (void)jcore_appendInt64:(unsigned long long)value;
- (void)jcore_appendString:(NSString *)string;
- (void)jcore_appendDataWithLength:(NSData *)data;
- (BOOL)jcore_setPacketLen;
@end
