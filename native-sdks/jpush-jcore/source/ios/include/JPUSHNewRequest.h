#pragma once
#import "JCORETcpObject.h"
@interface JPUSHNewRequest : JCORETcpObject <JCORETcpProtocol>
@property(nonatomic) unsigned char version;
@property(nonatomic) unsigned char protocolType;
@property(nonatomic) unsigned char algorithm;
@property(nonatomic) unsigned short rid;
@property(nonatomic) unsigned long long uid;
@property(nonatomic) unsigned long long seed;
@property(nonatomic) unsigned char controlFlag;
@property(nonatomic) int autoRetryCount;
@property(nonatomic, copy) NSString *moniter_id;
@property(nonatomic, copy) RecoveryOpaqueCallback completionHandler;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (void)send;
@end
