#pragma once
#import "JPUSHNewResponse.h"
@interface JPUSHMessage : JPUSHNewResponse
@property(nonatomic, readonly) unsigned char messageType;
@property(nonatomic, readonly) unsigned long long messageID;
@property(nonatomic, readonly, retain) NSString *messageContent;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response;
- (void)sendReceipt;
@end
@interface JPUSHMessageReceipt : JPUSHMessage <JCORETcpReceiptProtocol> {
  unsigned short statusCode;
}
@property(nonatomic) unsigned short statusCode;
@property(nonatomic) unsigned char algorithm;
@property(readonly, copy) NSString *debugDescription;
@property(readonly, copy) NSString *description;
@property(readonly) NSUInteger hash;
@property(nonatomic, copy) NSString *msgData;
@property(nonatomic) unsigned char protocolType;
@property(nonatomic) unsigned short rid;
@property(readonly) Class superclass;
@property(nonatomic) unsigned long long uid;
@property(nonatomic) unsigned char version;
- (instancetype)initWith:(JPUSHMessage *)message;
@end
