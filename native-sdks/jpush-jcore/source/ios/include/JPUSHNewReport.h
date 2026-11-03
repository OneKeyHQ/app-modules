#pragma once
#import "RecoveredProtocols.h"
@interface JPUSHNewReport : NSObject <JCOREHttpProtocol>
@property(nonatomic) long long reportTimeStamp;
@property(nonatomic, strong) NSMutableURLRequest *request;
@property(nonatomic, strong) id requestInfo;
@property(nonatomic) BOOL accountIDRequired;
@property(nonatomic) BOOL cacheRequired;
@property(nonatomic, copy) NSString *reportFlag;
@property(nonatomic) unsigned char controlFlag;
@property(nonatomic, copy) RecoveryOpaqueCallback completionHandler;
@property(nonatomic, strong) NSArray *addressList;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (void)send;
- (void)sendRequest:(RecoveryOpaqueCallback)completion;
@end
