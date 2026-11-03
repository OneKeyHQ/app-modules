#import "JCOREAddressAnalysis.h"

NSString *const JCOREAddressAnalysisType_sis_defaultDomain = @"JCOREAddressAnalysisType_sis_defaultDomain";
NSString *const JCOREAddressAnalysisType_sis_lastSuccess = @"JCOREAddressAnalysisType_sis_lastSuccess";
NSString *const JCOREAddressAnalysisType_sis_srvRecord = @"JCOREAddressAnalysisType_sis_srvRecord";
NSString *const JCOREAddressAnalysisType_sis_lastResult = @"JCOREAddressAnalysisType_sis_lastResult";
NSString *const JCOREAddressAnalysisType_sis_defaultSisIp = @"JCOREAddressAnalysisType_sis_defaultSisIp";
NSString *const JCOREAddressAnalysisType_sisResult = @"JCOREAddressAnalysisType_sisResult";
NSString *const JCOREAddressAnalysisType_lastSuccess = @"JCOREAddressAnalysisType_lastSuccess";
NSString *const JCOREAddressAnalysisType_defaultDomainAddress = @"JCOREAddressAnalysisType_defaultDomainAddress";
NSString *const JCOREAddressAnalysisType_defaultConnAddress = @"JCOREAddressAnalysisType_defaultConnAddress";
NSString *const JCOREAddressAnalysisDefaultType = @"default";
NSString *const JCOREAddressAnalysisTimeTypeDic = @"jcore_aa_time_type_dic";
NSString *const JCOREAddressAnalysisType = @"jcore_aa_type";
NSString *const JCOREAddressAnalysisAddress = @"jcore_aa_address";
NSString *const JCOREAddressAnalysisStatus = @"jcore_aa_status";
NSString *const JCOREAddressAnalysisRequestTime = @"jcore_aa_request_time";
NSString *const JCOREAddressAnalysisCostTime = @"jcore_aa_cost_time";
NSString *const JCOREAddressAnalysisNetType = @"jcore_aa_net_type";
NSString *const JCOREAddressAnalysisFreshTime = @"jcore_aa_fresh_time";
NSString *const JCOREAddressAnalysisExpireTime = @"jcore_aa_expire_time";
NSString *const JCORE_IPV4_Key = @"ipv4";
NSString *const JCORE_IPV6_Key = @"ipv6";

@implementation JCOREDNSResult
@synthesize domain = _domain, ipDic = _ipDic, requestTime = _requestTime, netType = _netType, time_fresh = _time_fresh, time_expire = _time_expire;
- (instancetype)init { self = [super init]; if (self) { _time_fresh = 900.0; _time_expire = 36000.0; } return self; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _domain = [coder decodeObjectForKey:@"jcore_dns_domain"];
        _requestTime = [coder decodeDoubleForKey:@"jcore_dns_request_time"];
        _ipDic = [coder decodeObjectForKey:@"jcore_dns_ips"];
        _time_fresh = [coder decodeDoubleForKey:@"jcore_dns_fresh_time"];
        _time_expire = [coder decodeDoubleForKey:@"jcore_dns_expire_time"];
        _netType = [coder decodeObjectForKey:@"jcore_dns_net_type"];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_domain forKey:@"jcore_dns_domain"];
    [coder encodeDouble:_requestTime forKey:@"jcore_dns_request_time"];
    [coder encodeDouble:_time_fresh forKey:@"jcore_dns_fresh_time"];
    [coder encodeDouble:_time_expire forKey:@"jcore_dns_expire_time"];
    [coder encodeObject:_ipDic forKey:@"jcore_dns_ips"];
    [coder encodeObject:_netType forKey:@"jcore_dns_net_type"];
}
- (id)copyWithZone:(NSZone *)zone {
    JCOREDNSResult *copy = [[[self class] allocWithZone:zone] init];
    copy->_ipDic = [_ipDic copy];
    copy->_domain = [_domain copy];
    copy->_requestTime = _requestTime;
    copy->_netType = [_netType copy];
    copy->_time_expire = _time_expire;
    copy->_time_fresh = _time_fresh;
    return copy;
}
- (NSString *)description { return @{@"domain":_domain, @"ip":_ipDic}.description; }
@end

@implementation JCORESRVResult
@synthesize domain = _domain, ips = _ips, requestTime = _requestTime, netType = _netType, time_fresh = _time_fresh, time_expire = _time_expire;
- (instancetype)init { self = [super init]; if (self) { _time_fresh = 1800.0; _time_expire = 86400.0; } return self; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _domain = [coder decodeObjectForKey:@"jcore_srv_domain"];
        _requestTime = [coder decodeDoubleForKey:@"jcore_srv_request_time"];
        _ips = [coder decodeObjectForKey:@"jcore_srv_ips"];
        _time_fresh = [coder decodeDoubleForKey:@"jcore_srv_fresh_time"];
        _time_expire = [coder decodeDoubleForKey:@"jcore_srv_expire_time"];
        _netType = [coder decodeObjectForKey:@"jcore_srv_net_type"];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_domain forKey:@"jcore_srv_domain"];
    [coder encodeDouble:_requestTime forKey:@"jcore_srv_request_time"];
    [coder encodeDouble:_time_fresh forKey:@"jcore_srv_fresh_time"];
    [coder encodeDouble:_time_expire forKey:@"jcore_srv_expire_time"];
    [coder encodeObject:_ips forKey:@"jcore_srv_ips"];
    [coder encodeObject:_netType forKey:@"jcore_srv_net_type"];
}
- (id)copyWithZone:(NSZone *)zone {
    JCORESRVResult *copy = [[[self class] allocWithZone:zone] init];
    copy->_ips = [_ips copy];
    copy->_domain = [_domain copy];
    copy->_requestTime = _requestTime;
    copy->_netType = [_netType copy];
    copy->_time_expire = _time_expire;
    copy->_time_fresh = _time_fresh;
    return copy;
}
@end

@implementation JCOREAddressAnalysis
@synthesize time_type_Dic = _time_type_Dic, type = _type, address = _address, statusCode = _statusCode;
@synthesize requestTime = _requestTime, costTime = _costTime, netType = _netType, time_fresh = _time_fresh, time_expire = _time_expire;
- (instancetype)init {
    self = [super init];
    if (self) { _type = @"default"; _statusCode = 2; _time_fresh = 1800.0; _time_expire = 86400.0; _time_type_Dic = [NSDictionary dictionary]; }
    return self;
}
- (instancetype)initWithType:(NSString *)type address:(NSString *)address statusCode:(long long)status requestTime:(double)request costTime:(double)cost netType:(NSString *)net timeTypeDic:(NSDictionary *)times {
    JCOREAddressAnalysis *result = [[JCOREAddressAnalysis alloc] init];
    if (result) {
        result->_type = type; result->_address = address; result->_statusCode = status;
        result->_requestTime = request; result->_costTime = cost; result->_netType = net; result->_time_type_Dic = [times copy];
    }
    return result;
}
- (id)copyWithZone:(NSZone *)zone {
    JCOREAddressAnalysis *copy = [[[self class] allocWithZone:zone] init];
    copy->_type = [_type copy]; copy->_address = [_address copy]; copy->_requestTime = _requestTime; copy->_costTime = _costTime;
    copy->_statusCode = _statusCode; copy->_netType = [_netType copy]; copy->_time_expire = _time_expire; copy->_time_fresh = _time_fresh;
    copy->_time_type_Dic = [_time_type_Dic mutableCopy];
    return copy;
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"jcore_aa_cost_time"]) self.costTime = [value doubleValue];
    else if ([key isEqualToString:@"jcore_aa_status"]) self.statusCode = [value integerValue];
    else if ([key isEqualToString:@"jcore_aa_net_type"]) self.netType = value;
    else if ([key isEqualToString:@"jcore_aa_request_time"]) self.requestTime = [value doubleValue];
    else if ([key isEqualToString:@"jcore_aa_type"]) self.type = value;
    else if ([key isEqualToString:@"jcore_aa_address"]) self.address = value;
    else if ([key isEqualToString:@"jcore_aa_expire_time"]) self.time_expire = [value doubleValue];
    else if ([key isEqualToString:@"jcore_aa_fresh_time"]) self.time_fresh = [value doubleValue];
    else if ([key isEqualToString:@"jcore_aa_time_type_dic"]) self.time_type_Dic = value;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _type = [coder decodeObjectForKey:@"jcore_aa_type"];
        _address = [coder decodeObjectForKey:@"jcore_aa_address"];
        _requestTime = [coder decodeDoubleForKey:@"jcore_aa_request_time"];
        _costTime = [coder decodeDoubleForKey:@"jcore_aa_cost_time"];
        _statusCode = [coder decodeIntegerForKey:@"jcore_aa_status"];
        _netType = [coder decodeObjectForKey:@"jcore_aa_net_type"];
        _time_fresh = [coder decodeDoubleForKey:@"jcore_aa_fresh_time"];
        _time_expire = [coder decodeDoubleForKey:@"jcore_aa_expire_time"];
        _time_type_Dic = [coder decodeObjectForKey:@"jcore_aa_time_type_dic"];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_type forKey:@"jcore_aa_type"];
    [coder encodeObject:_address forKey:@"jcore_aa_address"];
    [coder encodeDouble:_requestTime forKey:@"jcore_aa_request_time"];
    [coder encodeDouble:_costTime forKey:@"jcore_aa_cost_time"];
    [coder encodeInteger:_statusCode forKey:@"jcore_aa_status"];
    [coder encodeObject:_netType forKey:@"jcore_aa_net_type"];
    [coder encodeDouble:_time_fresh forKey:@"jcore_aa_fresh_time"];
    [coder encodeDouble:_time_expire forKey:@"jcore_aa_expire_time"];
    [coder encodeObject:_time_type_Dic forKey:@"jcore_aa_time_type_dic"];
}
- (NSDictionary *)json {
    NSMutableDictionary *result = [NSMutableDictionary dictionary];
    [result setObject:self.type ?: @"default" forKey:@"jcore_aa_type"];
    [result setObject:self.address ?: @"" forKey:@"jcore_aa_address"];
    [result setObject:@(self.requestTime) forKey:@"jcore_aa_request_time"];
    [result setObject:@(self.costTime * 1000.0) forKey:@"jcore_aa_cost_time"];
    [result setObject:@(self.statusCode) forKey:@"jcore_aa_status"];
    [result setObject:self.netType ?: @"" forKey:@"jcore_aa_net_type"];
    [result setObject:@(self.time_fresh) forKey:@"jcore_aa_fresh_time"];
    [result setObject:@(self.time_expire) forKey:@"jcore_aa_expire_time"];
    [result setObject:self.time_type_Dic forKey:@"jcore_aa_time_type_dic"];
    return [result copy];
}
- (NSString *)description { return [self json].description; }
- (BOOL)isEqual:(id)object {
    if (![object isKindOfClass:JCOREAddressAnalysis.class]) return NO;
    JCOREAddressAnalysis *other = object;
    if (![self.type isEqualToString:other.type] || ![self.address isEqualToString:other.address]) return NO;
    if (self.costTime != other.costTime || self.statusCode != other.statusCode || ![self.netType isEqualToString:other.netType]) return NO;
    if (self.requestTime != other.requestTime || self.time_fresh != other.time_fresh || self.time_expire != other.time_expire) return NO;
    // The original final comparison intentionally returns the inverse dictionary equality.
    return ![self.time_type_Dic isEqual:other.time_type_Dic];
}
@end
