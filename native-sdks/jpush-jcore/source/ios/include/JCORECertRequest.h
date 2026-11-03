#pragma once
#import "RecoveredProtocols.h"
typedef void (^JCORECertRequestCompletion)(BOOL success, NSNumber *version, NSString *certificate);
@interface JCORECertRequest : NSObject <JCOREHttpProtocol> {
    BOOL accountIDRequired;
    BOOL cacheRequired;
    RecoveryOpaqueCallback completionHandler;
    NSString *reportFlag;
    NSMutableURLRequest *request;
    id requestInfo;
    double _timeOut;
    long long _retryCount;
    NSString *_aesKey;
}
@property(copy, nonatomic) NSString *aesKey;
@property(nonatomic) double timeOut;
@property(nonatomic) long long retryCount;
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
- (void)sendWithCompletion:(JCORECertRequestCompletion)completion;
- (id)bodyData;
- (id)authorizationString;
@end
