#pragma once
#import "RecoveredProtocols.h"
#import <dispatch/dispatch.h>
@interface JCOREUaidTokenPushRequest : NSObject <JCOREHttpProtocol> {
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
    NSString *_channelId;
    NSString *_respBody;
    NSDictionary *_respHeader;
    NSString *_aesKey;
}
@property(copy, nonatomic) NSString *aesKey;
@property(nonatomic) long long reportTimeStamp;
@property(copy, nonatomic) NSString *appkey;
@property(copy, nonatomic) NSString *channelId;
@property(copy, nonatomic) NSString *respBody;
@property(strong, nonatomic) NSDictionary *respHeader;
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
- (void)send;
- (void)sendRequest:(RecoveryOpaqueCallback)completion;
- (id)bodyData;
- (id)authorizationString;
@end
