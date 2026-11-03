#pragma once
#import "RecoveredProtocols.h"
@interface JCommonDataConfigRequest : NSObject <JCOREHttpProtocol> {
    BOOL accountIDRequired;
    BOOL cacheRequired;
    unsigned char controlFlag;
    RecoveryOpaqueCallback completionHandler;
    id requestInfo;
    NSMutableURLRequest *request;
    NSString *reportFlag;
    NSArray *addressList;
    NSString *_sign;
}
@property(copy, nonatomic) NSString *sign;
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
- (instancetype)init;
- (void)send;
- (void)sendRequest:(RecoveryOpaqueCallback)completion;
- (NSMutableDictionary *)content;
- (NSMutableURLRequest *)generateReportRequest;
@end
