#pragma once
#import "JCOREPropertyModels.h"
@interface JCORESupersetMoniter : JCORESupersetBaseMoniter {
    long long _type;
    NSString *_network_type;
    NSString *_operate_type;
    NSString *_method;
    NSString *_url;
    long long _request_byte;
    long long _cost_time;
    long long _response_byte;
    long long _is_foreground;
    NSString *_tcp_id;
    NSString *_host;
    long long _port;
    long long _error_code;
    long long _status_code;
}
@property (nonatomic) long long type;
@property (copy, nonatomic) NSString *network_type;
@property (copy, nonatomic) NSString *operate_type;
@property (copy, nonatomic) NSString *method;
@property (copy, nonatomic) NSString *url;
@property (nonatomic) long long request_byte;
@property (nonatomic) long long cost_time;
@property (nonatomic) long long response_byte;
@property (nonatomic) long long is_foreground;
@property (copy, nonatomic) NSString *tcp_id;
@property (copy, nonatomic) NSString *host;
@property (nonatomic) long long port;
@property (nonatomic) long long error_code;
@property (nonatomic) long long status_code;
- (NSMutableDictionary *)requestInfo;
@end
