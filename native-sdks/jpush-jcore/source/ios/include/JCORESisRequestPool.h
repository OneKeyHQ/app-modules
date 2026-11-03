#pragma once
#import <Foundation/Foundation.h>
@class JCOREUdpSis;
typedef void (^JCORESisPoolCompletion)(NSArray *addresses, BOOL completed, NSError *error);
@interface JCORESisRequestPoolModel : NSObject {
    BOOL _isDomainString;
    NSString *_domain;
    NSString *_ip;
}
@property(nonatomic) BOOL isDomainString;
@property(copy, nonatomic) NSString *domain;
@property(copy, nonatomic) NSString *ip;
- (NSString *)description;
@end
@interface JCORESisRequestPool : NSObject {
    NSMutableArray *_addressSisArray;
    NSMutableArray *_poolModelArray;
    NSTimer *_complexTimer;
    JCORESisPoolCompletion _Completion;
    unsigned long long _requestingNum;
}
@property(strong, nonatomic) NSMutableArray *addressSisArray;
@property(strong, nonatomic) NSMutableArray *poolModelArray;
@property(strong, nonatomic) NSTimer *complexTimer;
@property(copy, nonatomic) JCORESisPoolCompletion Completion;
@property(nonatomic) unsigned long long requestingNum;
- (instancetype)init;
- (void)requestWithDomains:(id)domains completion:(JCORESisPoolCompletion)completion;
- (void)endCurrentRequest;
- (void)sisRequest;
- (void)callBackSisRequestCompletionSuccess:(id)addresses;
- (void)callBackSisRequestCompletionFailure:(JCOREUdpSis *)sis poolModel:(id)model;
- (void)startComplexTimer;
- (void)stopComplexTimer;
- (NSArray *)getAddressFromDNS:(id)domain;
- (void)removeDNSCacheWithDomain:(id)domain;
- (NSArray *)randamArray:(id)addresses;
@end
