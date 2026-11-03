#import "JCORENetDataMoniter.h"
#pragma clang diagnostic push
// The original count getter computes a sum and ignores the setter-backed slot.
#pragma clang diagnostic ignored "-Wunused-property-ivar"
@implementation JCORENetDataMoniter
@synthesize url = _url, sortType = _sortType, request_byte = _request_byte, response_byte = _response_byte;
@synthesize request_count = _request_count, response_count = _response_count, count = _count;
- (NSMutableDictionary *)requestInfo {
    NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
    information[@"moniter_type"] = self.moniter_type;
    information[@"uuid"] = self.uuid;
    information[@"moniter_id"] = self.moniter_id;
    information[@"timestamp"] = @((long)(self.timestamp * 1000));
    NSMutableDictionary *extra = [[NSMutableDictionary alloc] init];
    extra[@"url"] = self.url;
    extra[@"req"] = @((NSInteger)self.request_byte);
    extra[@"res"] = @((NSInteger)self.response_byte);
    long long responses = self.response_count;
    extra[@"count"] = @((long)((unsigned long long)self.request_count + (unsigned long long)responses));
    information[@"extra"] = extra;
    return information;
}
- (NSMutableDictionary *)saveInfo {
    NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
    information[@"moniter_type"] = self.moniter_type;
    information[@"uuid"] = self.uuid;
    information[@"moniter_id"] = self.moniter_id;
    information[@"timestamp"] = @((long)self.timestamp);
    NSMutableDictionary *extra = [[NSMutableDictionary alloc] init];
    extra[@"url"] = self.url;
    extra[@"req"] = @((NSInteger)self.request_byte);
    extra[@"res"] = @((NSInteger)self.response_byte);
    long long responses = self.response_count;
    extra[@"count"] = @((long)((unsigned long long)self.request_count + (unsigned long long)responses));
    extra[@"req_count"] = @((NSInteger)self.request_count);
    extra[@"res_count"] = @((NSInteger)self.response_count);
    extra[@"sortType"] = @((NSInteger)self.sortType);
    information[@"extra"] = extra;
    return information;
}
- (instancetype)initWithDic:(NSDictionary *)dictionary {
    JCORENetDataMoniter *monitor = [[JCORENetDataMoniter alloc] init];
    monitor.moniter_type = @"NET_DATA";
    monitor.uuid = [dictionary objectForKey:@"uuid"];
    monitor.moniter_id = [dictionary objectForKey:@"moniter_id"];
    monitor.timestamp = [[dictionary objectForKey:@"timestamp"] doubleValue];
    NSDictionary *extra = dictionary[@"extra"];
    monitor.url = [extra objectForKey:@"url"];
    monitor.request_byte = (long)[[extra objectForKey:@"req"] doubleValue];
    monitor.response_byte = (long)[[extra objectForKey:@"res"] doubleValue];
    monitor.request_count = [[extra objectForKey:@"req_count"] integerValue];
    monitor.response_count = [[extra objectForKey:@"res_count"] integerValue];
    monitor.count = [[extra objectForKey:@"count"] integerValue];
    monitor.sortType = [[extra objectForKey:@"sortType"] integerValue];
    return monitor;
}
- (long long)count {
    long long requests = self.request_count;
    return (long long)((unsigned long long)self.response_count + (unsigned long long)requests);
}
@end
#pragma clang diagnostic pop
