#pragma once
#import "JCOREPropertyModels.h"
@interface JCORENetDataMoniter : JCORESupersetBaseMoniter {
    NSString *_url;
    long long _sortType;
    long long _request_byte;
    long long _response_byte;
    long long _request_count;
    long long _response_count;
    long long _count;
}
@property (copy, nonatomic) NSString *url;
@property (nonatomic) long long sortType;
@property (nonatomic) long long request_byte;
@property (nonatomic) long long response_byte;
@property (nonatomic) long long request_count;
@property (nonatomic) long long response_count;
@property (nonatomic) long long count;
- (NSMutableDictionary *)requestInfo;
- (NSMutableDictionary *)saveInfo;
- (instancetype)initWithDic:(NSDictionary *)dictionary;
@end
