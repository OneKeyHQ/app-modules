#pragma once
#import "RecoveredProtocols.h"
@interface JCOREConfigRequest : NSObject <JCOREHttpProtocol> {
    BOOL accountIDRequired;
    BOOL cacheRequired;
    unsigned char controlFlag;
    RecoveryOpaqueCallback completionHandler;
    id requestInfo;
    NSMutableURLRequest *request;
    NSString *reportFlag;
    NSArray *addressList;
    long long _reportTimeStamp;
    NSString *_appkey;
    NSString *_aesKey;
    NSString *_authorizationString;
    NSMutableURLRequest *_req;
}
@property(strong, nonatomic) NSMutableURLRequest *req;
@property(nonatomic) long long reportTimeStamp;
@property(copy, nonatomic) NSString *appkey;
@property(copy, nonatomic) NSString *aesKey;
@property(copy, nonatomic) NSString *authorizationString;
@property(strong, nonatomic) NSMutableURLRequest *request;
@property(strong, nonatomic) id requestInfo;
@property(nonatomic) BOOL accountIDRequired;
@property(nonatomic) BOOL cacheRequired;
@property(copy, nonatomic) NSString *reportFlag;
@property(nonatomic) unsigned char controlFlag;
@property(strong, nonatomic) NSArray *addressList;
@property(copy, nonatomic) RecoveryOpaqueCallback completionHandler;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
+ (long long)getProtocolVersion;
- (void)send;
- (void)sendRequest:(RecoveryOpaqueCallback)completion;
- (id)bodyData;
- (id)sisRequestInfo;
@end
