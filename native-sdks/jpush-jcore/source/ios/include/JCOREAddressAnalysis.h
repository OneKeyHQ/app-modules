#pragma once
#import <Foundation/Foundation.h>

FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_sis_defaultDomain;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_sis_lastSuccess;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_sis_srvRecord;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_sis_lastResult;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_sis_defaultSisIp;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_sisResult;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_lastSuccess;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_defaultDomainAddress;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType_defaultConnAddress;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisDefaultType;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisTimeTypeDic;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisType;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisAddress;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisStatus;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisRequestTime;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisCostTime;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisNetType;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisFreshTime;
FOUNDATION_EXPORT NSString *const JCOREAddressAnalysisExpireTime;
FOUNDATION_EXPORT NSString *const JCORE_IPV4_Key;
FOUNDATION_EXPORT NSString *const JCORE_IPV6_Key;

@interface JCOREAddressAnalysis : NSObject <NSCoding, NSCopying> {
    NSDictionary *_time_type_Dic;
    NSString *_type;
    NSString *_address;
    long long _statusCode;
    double _requestTime;
    double _costTime;
    NSString *_netType;
    double _time_fresh;
    double _time_expire;
}
@property (copy, nonatomic) NSDictionary *time_type_Dic;
@property (copy, nonatomic) NSString *type;
@property (copy, nonatomic) NSString *address;
@property (nonatomic) long long statusCode;
@property (nonatomic) double requestTime;
@property (nonatomic) double costTime;
@property (copy, nonatomic) NSString *netType;
@property (nonatomic) double time_fresh;
@property (nonatomic) double time_expire;
- (instancetype)initWithType:(NSString *)type address:(NSString *)address statusCode:(long long)status requestTime:(double)request costTime:(double)cost netType:(NSString *)net timeTypeDic:(NSDictionary *)times;
- (NSDictionary *)json;
@end

@interface JCOREDNSResult : NSObject <NSCoding, NSCopying> {
    NSString *_domain;
    NSDictionary *_ipDic;
    double _requestTime;
    NSString *_netType;
    double _time_fresh;
    double _time_expire;
}
@property (copy, nonatomic) NSString *domain;
@property (copy, nonatomic) NSDictionary *ipDic;
@property (nonatomic) double requestTime;
@property (copy, nonatomic) NSString *netType;
@property (nonatomic) double time_fresh;
@property (nonatomic) double time_expire;
@end

@interface JCORESRVResult : NSObject <NSCoding, NSCopying> {
    NSString *_domain;
    NSArray *_ips;
    double _requestTime;
    NSString *_netType;
    double _time_fresh;
    double _time_expire;
}
@property (copy, nonatomic) NSString *domain;
@property (copy, nonatomic) NSArray *ips;
@property (nonatomic) double requestTime;
@property (copy, nonatomic) NSString *netType;
@property (nonatomic) double time_fresh;
@property (nonatomic) double time_expire;
@end

