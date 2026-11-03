#pragma once
#import "RecoveredProtocols.h"

@interface JCORELogTokenRequest : NSObject <JCOREHttpProtocol> {
    NSData *_bodyData;
    NSMutableDictionary *_badgeBody;
    BOOL accountIDRequired;
    BOOL cacheRequired;
    RecoveryOpaqueCallback completionHandler;
    NSString *reportFlag;
    NSMutableURLRequest *request;
    id requestInfo;
    NSArray *addressList;
    long _reportTimeStamp;
    NSString *_filename;
}
@property (nonatomic) long reportTimeStamp;
@property (strong, nonatomic) NSArray *addressList;
@property (strong, nonatomic) NSString *filename;
@property (strong, nonatomic) NSMutableURLRequest *request;
@property (strong, nonatomic) id requestInfo;
@property (nonatomic) BOOL accountIDRequired;
@property (nonatomic) BOOL cacheRequired;
@property (copy, nonatomic) NSString *reportFlag;
@property (nonatomic) unsigned char controlFlag;
@property (copy, nonatomic) RecoveryOpaqueCallback completionHandler;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (void)send;
- (void)sendRequest:(RecoveryOpaqueCallback)completion;
- (NSMutableDictionary *)badgeBody;
- (NSMutableURLRequest *)generateReportRequest;
- (NSString *)authorizationString;
@end

@interface JCORELogFileUploadRequest : NSObject <JCOREHttpProtocol> {
    NSData *_fileData;
    BOOL accountIDRequired;
    BOOL cacheRequired;
    RecoveryOpaqueCallback completionHandler;
    NSString *reportFlag;
    NSMutableURLRequest *request;
    id requestInfo;
    NSArray *addressList;
}
@property (strong, nonatomic) NSMutableURLRequest *request;
@property (strong, nonatomic) id requestInfo;
@property (nonatomic) BOOL accountIDRequired;
@property (nonatomic) BOOL cacheRequired;
@property (copy, nonatomic) NSString *reportFlag;
@property (nonatomic) unsigned char controlFlag;
@property (strong, nonatomic) NSArray *addressList;
@property (copy, nonatomic) RecoveryOpaqueCallback completionHandler;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithUrl:(NSString *)url fileData:(NSData *)data;
- (void)sendRequest:(RecoveryOpaqueCallback)completion;
@end
