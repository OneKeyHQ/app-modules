#pragma once
#import "RecoveredProtocols.h"
@interface JCOREReport : NSObject <JCOREHttpProtocol>
@property (copy, nonatomic) NSString *aesKey;
@property (strong, nonatomic) NSMutableURLRequest *request;
@property (strong, nonatomic) id requestInfo;
@property (copy, nonatomic) RecoveryOpaqueCallback completionHandler;
@property (nonatomic) unsigned char controlFlag;
@property (nonatomic) BOOL accountIDRequired;
@property (nonatomic) BOOL cacheRequired;
@property (copy, nonatomic) NSString *reportFlag;
@property (strong, nonatomic) NSArray *addressList;
@property (readonly, nonatomic) NSArray *types;
@property (readonly, nonatomic) NSData *packetData;
@property (readonly, nonatomic) unsigned int seed;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (NSString *)parseTypeFromContent:(id)content;
- (BOOL)isCancelReport;
- (NSDictionary *)peripheralData;
- (NSMutableURLRequest *)generateUrlRequest;
- (NSString *)authorizationString:(NSData *)data;
@end
