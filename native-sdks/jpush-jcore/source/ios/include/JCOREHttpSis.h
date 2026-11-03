#pragma once
#import <Foundation/Foundation.h>
typedef void (^JCOREHttpSisResult)(BOOL success, NSData *data);
typedef void (^JCOREHttpSisCompletionHandler)(BOOL success);
@interface JCOREHttpSisRequest : NSObject {
    JCOREHttpSisResult _resultHandler;
    NSString *_reportAddress;
    NSString *_aesKey;
}
@property(copy, nonatomic) JCOREHttpSisResult resultHandler;
@property(copy, nonatomic) NSString *reportAddress;
@property(copy, nonatomic) NSString *aesKey;
- (instancetype)init;
- (NSMutableData *)bodyData;
- (NSDictionary *)sisRequestInfo;
- (NSMutableURLRequest *)httpRequest;
- (NSString *)authorizationString;
@end
@interface JCOREHttpSis : NSObject {
    BOOL _requesting;
    BOOL _expired;
    NSArray *_normalReportAddressList;
}
@property(nonatomic) double nextRequestTime;
@property(copy, nonatomic) NSArray *sisIpsFromServer;
@property(copy, nonatomic) NSDictionary *reportAddressDict;
@property(copy, nonatomic) NSArray *normalReportAddressList;
@property(nonatomic) BOOL requesting;
@property(nonatomic) BOOL expired;
+ (NSArray *)httpSisDefaultAddress;
- (void)requst:(id)address result:(JCOREHttpSisCompletionHandler)completion;
@end
