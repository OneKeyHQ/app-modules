#import "JPUSHUserPropertyRequest.h"
#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCORELog.h"
#import "JPUSHCache.h"
#import "JPUSHNewResponse.h"
#import "JPUSHPropertyModels.h"
#import "JPUSHUserPropertyController.h"
#import <CoreFoundation/CoreFoundation.h>
// External request selectors; its implementation belongs to the TCP request family.

#define USER_PROPERTY_LOG(asyncValue, flagValue, lineNo, formatValue, ...)                                   \
    [JCORELog log:asyncValue                                                                                 \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Controller/"    \
                  "JPUSHUserPropertyController.m"                                                            \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
static NSString *const UserPropertyCacheKey = @"__JPUSHUserPropertyKey";
@implementation JPUSHUserPropertyController
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)init {
    if ((self = [super init]))
        _ReqItemQueue = [NSMutableDictionary dictionaryWithCapacity:5];
    return self;
}
- (id)findRequest:(unsigned long long)seq {
    return ((JPUSHUserPropetyReqItem *)_ReqItemQueue[@(seq)]).request;
}
- (void)handleUserPropertyResponse:(id)value {
    JPUSHNewResponse *response = value;
    JPUSHUserPropetyReqItem *item = _ReqItemQueue[@([response rid])];
    if (!item)
        return;
    id description = [response errorDescription];
    NSInteger code = [response statusCode];
    if (code == 11) {
        description = @"Server error";
        code = 6024;
    } else if (code == 10) {
        description = @"Param error";
        code = 6023;
    }
    NSError *error = nil;
    if ([description isKindOfClass:NSString.class])
        error = [NSError errorWithDomain:@"JPUSHErrorDomain"
                                    code:code
                                userInfo:@{NSLocalizedDescriptionKey : description}];
    if (code && !error)
        error = [NSError errorWithDomain:@"JPUSHErrorDomain"
                                    code:code
                                userInfo:@{NSLocalizedDescriptionKey : @"An error occurred, failed"}];
    item.completion(error);
    [_ReqItemQueue removeObjectForKey:@([response rid])];
}
- (void)setUserProperty:(NSDictionary *)property {
    if (!property)
        return;
    id type = property[@"type"];
    if (!type) {
        USER_PROPERTY_LOG(NO, 0x20, 0x61, @"set user property type error");
        return;
    }
    id content = property[@"content"];
    if (!content) {
        USER_PROPERTY_LOG(NO, 0x20, 0x66, @"set user property content error");
        return;
    }
    id block = property[@"completion"];
    if (!block || [block isKindOfClass:NSNull.class])
        return;
    NSDictionary *value = @{@"type" : type, @"content" : content};
    void (^completion)(NSError *) = block;
    if (![self isUserProperyUpload:value])
        [self sendUserProperty:value completion:completion];
    else
        completion(nil);
}
- (BOOL)isUserProperyUpload:(NSDictionary *)property {
    id stored = [JCORECacheChannel userProperty];
    if (![stored isKindOfClass:NSArray.class]) {
        stored = [JPUSHCache objectForFile:UserPropertyCacheKey];
        if (![stored isKindOfClass:NSArray.class])
            return NO;
    } else
        [JCORECacheChannel setUserProperty:nil];
    NSArray *snapshot = [[NSMutableArray arrayWithArray:stored] copy];
    for (id entry in snapshot) {
        if (![entry isKindOfClass:NSDictionary.class] || ![property[@"type"] isEqual:entry[@"type"]])
            continue;
        if (![property[@"content"] isEqualToString:entry[@"content"]])
            return NO;
        // The original checks the incoming dictionary's upload flag, not the stored entry's flag.
        id uploaded = property[@"upload"];
        if (![uploaded isKindOfClass:NSNumber.class] || [uploaded intValue] < 1)
            return NO;
        USER_PROPERTY_LOG(YES, 8, 0x98, @"user info has upload");
        return YES;
    }
    return NO;
}
- (void)saveUserProperty:(NSDictionary *)property {
    if (property)
        [JPUSHCache setObject:@[ property ] forFile:UserPropertyCacheKey];
}
- (void)sendUserProperty:(NSDictionary *)property completion:(void (^)(NSError *))completion {
    if (!property)
        return;
    [self saveUserProperty:property];
    double now = CFAbsoluteTimeGetCurrent();
    double previous = _uploadPropertyTime;
    _uploadPropertyTime = now;
    if (now - previous <= 10) {
        _uploadPropertyTimes++;
        if (_uploadPropertyTimes > 2) {
            completion([NSError
                errorWithDomain:@"JPUSHErrorDomain"
                           code:6011
                       userInfo:@{NSLocalizedDescriptionKey : @"The operation is too frequent"}]);
            return;
        }
    } else
        _uploadPropertyTimes = 0;
    id type = property[@"type"], content = property[@"content"];
    JPUSHUserPropertyRequest *request = [[JPUSHUserPropertyRequest alloc] init];
    [request setType:(unsigned char)[type unsignedShortValue]];
    [request setContent:content];
    [request send];
    JPUSHUserPropetyReqItem *item = [[JPUSHUserPropetyReqItem alloc] init];
    item.request = (id)request;
    item.seq = request.rid;
    item.completion = completion;
    _ReqItemQueue[@(item.seq)] = item;
}
@end
#endif
