#pragma once
#import "RecoveredProtocols.h"
@interface JPUSHNewResponse : NSObject <JCORETcpRespondProtocol>
@property(nonatomic) unsigned short statusCode;
@property(nonatomic) unsigned short rid;
@property(nonatomic) unsigned char version;
@property(nonatomic, strong) NSData *bodyData;
@property(nonatomic, copy) NSString *errorDescription;
@property(nonatomic, copy) NSString *msgData;
@property(nonatomic) unsigned char protocolType;
@property(nonatomic) unsigned char algorithm;
@property(nonatomic) unsigned long long uid;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response;
- (void)handleReceipt;
@end
