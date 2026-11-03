#import "JCommonSupersetMoniter.h"
@implementation JCommonSupersetMoniter
@synthesize uuid = _uuid, moniter_id = _moniter_id, timestamp = _timestamp, moniter_type = _moniter_type;
@synthesize type = _type, network_type = _network_type, operate_type = _operate_type, method = _method, url = _url;
@synthesize request_byte = _request_byte, cost_time = _cost_time, response_byte = _response_byte, is_foreground = _is_foreground;
@synthesize tcp_id = _tcp_id, host = _host, port = _port, error_code = _error_code, status_code = _status_code;
- (NSMutableDictionary *)requestInfo {
    NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
    information[@"moniter_type"] = self.moniter_type;
    information[@"uuid"] = self.uuid;
    information[@"moniter_id"] = self.moniter_id;
    information[@"timestamp"] = @((long)(self.timestamp * 1000));
    NSMutableDictionary *extra = [[NSMutableDictionary alloc] init];
    extra[@"cost_time"] = [@"NET_TCP_DISCONNECT" isEqualToString:self.moniter_type] ? @0 : @((long)((unsigned long long)self.cost_time * 1000));
    extra[@"error_code"] = @((NSInteger)self.error_code);
    extra[@"status_code"] = @((NSInteger)self.status_code);
    extra[@"network_type"] = self.network_type;
    extra[@"is_foreground"] = @((NSInteger)self.is_foreground);
    extra[@"operate_type"] = self.operate_type;
    if (self.type == 0) {
        extra[@"method"] = self.method;
        extra[@"url"] = self.url;
        extra[@"request_byte"] = @((NSInteger)self.request_byte);
        extra[@"response_byte"] = @((NSInteger)self.response_byte);
    } else {
        extra[@"tcp_id"] = self.tcp_id ?: @"";
        extra[@"host"] = self.host;
        extra[@"port"] = @((NSInteger)self.port);
    }
    information[@"extra"] = extra;
    return information;
}
@end
