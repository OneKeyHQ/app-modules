#pragma once
#import "RecoveredProtocols.h"
#import <dispatch/dispatch.h>
@interface JCOREUaidTokenGetRequest : NSObject {
    NSString *_tokenGetUrl;
    NSString *_method;
    NSDictionary *_headers;
    NSString *_requestBody;
}
@property(copy, nonatomic) NSString *tokenGetUrl;
@property(copy, nonatomic) NSString *method;
@property(strong, nonatomic) NSDictionary *headers;
@property(copy, nonatomic) NSString *requestBody;
- (instancetype)initWithUrl:(id)url method:(id)method headers:(id)headers requestBody:(id)body;
- (void)sendRequest:(RecoveryOpaqueCallback)completion;
- (NSMutableURLRequest *)request;
@end
