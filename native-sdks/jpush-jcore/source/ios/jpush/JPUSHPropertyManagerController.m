#import "JCOREUtilities.h"
#import "JPUSHPropertyRequest.h"
#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCORELog.h"
#import "JPUSHPropertyManagerController.h"
#import <objc/message.h>

@interface JCORENetUtilities : NSObject
+ (int)networkingStatus;
@end

extern NSString *const kJCORENetworkDidLoginNotification;
extern NSString *const kJCORETcpChannelDisconnectNotification;
#define PROPERTY_LOG(asyncValue, flagValue, lineNo, formatValue, ...)                                        \
    [JCORELog log:asyncValue                                                                                 \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Controller/"    \
                  "JPUSHPropertyManagerController.m"                                                         \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
@implementation JPUSHPropertyManagerController
@synthesize requestQueryQueue = _requestQueryQueue, operateTimeQueue = _operateTimeQueue,
            isRequesting = _isRequesting, isLogin = _isLogin;
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)init {
    if ((self = [super init])) {
        _requestQueryQueue = [NSMutableArray array];
        _operateTimeQueue = [NSMutableArray array];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(didLogin)
                                                     name:kJCORENetworkDidLoginNotification
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleTcpClose)
                                                     name:kJCORETcpChannelDisconnectNotification
                                                   object:nil];
    }
    return self;
}
+ (instancetype)sharedInstance {
    static JPUSHPropertyManagerController *instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[JPUSHPropertyManagerController alloc] init];
    });
    return instance;
}
- (void)didLogin {
    self.isLogin = YES;
}
- (void)handleTcpClose {
    self.isLogin = NO;
}
- (void)propertyOperate:(id)properties
                   type:(long long)type
          andCompletion:(id)completion
                 andSeq:(long long)seq {
    long long error;
    if (![self shouldSendPropertyOperate:[JCOREUtilities currentTimestamp]])
        error = 6011;
    else if (self.isRequesting)
        error = 6038;
    else {
        self.isRequesting = YES;
        if ((unsigned long long)type < 3) {
            error = [self checkProperty:properties opearteType:type];
            if (error) {
                [self invokePropertyBlockInMainThread:seq
                                              andCode:error
                                                Block:completion
                                         propertyInfo:[self getCallBackPropertyList:properties
                                                                        opearteType:type]];
                return;
            }
            id converted = [self changePropertyValueToString:properties];
            JPUSHPropertyRequest *request = [[JPUSHPropertyRequest alloc] init];
            [request beginTimeoutTimer];
            request.operateType = type;
            request.propertyInfo = converted;
            JPUSHPropertyReqItem *item = [[JPUSHPropertyReqItem alloc] init];
            item.request = (id)request;
            item.seq = seq;
            item.propertyCompletion = completion;
            [self.requestQueryQueue addObject:item];
            [request send];
            return;
        }
        error = 6009;
    }
    [self invokePropertyBlockInMainThread:seq andCode:error Block:completion propertyInfo:nil];
}
- (void)doCallBacksForResContent:(id)content andRid:(unsigned long long)rid {
    long long code = [content[@"code"] integerValue];
    id message = content[@"msg"];
    __block JPUSHPropertyReqItem *found = nil;
    [self.requestQueryQueue
        enumerateObjectsUsingBlock:^(JPUSHPropertyReqItem *item, NSUInteger index, BOOL *stop) {
            if (rid == ((JPUSHPropertyRequest *)(id)item.request).rid) {
                found = item;
                *stop = YES;
            }
        }];
    if (!found) {
        PROPERTY_LOG(NO, 0x20, 0x86, @"a unknown error happend, property request is missed");
        return;
    }
    if (found.isTimeout) {
        PROPERTY_LOG(NO, 0x20, 0x8b, @"request is timeout,discard seq:%ld", (long)found.seq);
        return;
    }
    if (code == 1)
        [self handleInternalErrorResponseWith:found];
    else if (!code)
        [self handleSuccessResponseWith:found andNetworkCode:0 msgDic:message];
    else
        [self handleOtherResponseWith:found andNetworkCode:code msgDic:message];
}
- (void)handleSuccessResponseWith:(JPUSHPropertyReqItem *)item
                   andNetworkCode:(long long)code
                           msgDic:(id)message {
    PROPERTY_LOG(YES, 0x100, 0xa0, @"property request success,code:%ld seq:%ld", (long)code, (long)item.seq);
    [self invokePropertyBlockInMainThread:item andCode:code msgDic:message];
    [(JPUSHPropertyRequest *)(id)item.request stopTimeoutTimer];
    item.isTimeout = YES;
    item.request = nil;
}
- (void)handleOtherResponseWith:(JPUSHPropertyReqItem *)item
                 andNetworkCode:(long long)code
                         msgDic:(id)message {
    PROPERTY_LOG(YES, 0x100, 0xaa, @"property request has a error happend,code:%ld seq:%ld", (long)code,
                 (long)item.seq);
    [self invokePropertyBlockInMainThread:item
                                  andCode:code == 1013 ? 6036 : (code == 1014 ? 6037 : 6009)
                                   msgDic:message];
    [(JPUSHPropertyRequest *)(id)item.request stopTimeoutTimer];
    item.isTimeout = YES;
    item.request = nil;
}
- (void)handleInternalErrorResponseWith:(JPUSHPropertyReqItem *)item {
    PROPERTY_LOG(YES, 0x100, 0xc2, @"property request has a internal error happend, seq:%ld", (long)item.seq);
    if (!item.hasResend) {
        item.hasResend = YES;
        [(JPUSHPropertyRequest *)(id)item.request send];
        [(JPUSHPropertyRequest *)(id)item.request beginTimeoutTimer];
    } else
        [self invokePropertyBlockInMainThread:item andCode:6014];
}
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid {
    [self doErrorCallBackWithSeq:rid andCode:6002];
    if (![JCORENetUtilities networkingStatus])
        PROPERTY_LOG(YES, 8, 0xd5, @"properties operate fail - current network is not reachable");
    else if (!self.isLogin)
        PROPERTY_LOG(YES, 8, 0xd7, @"properties operate fail - current user did not login success");
    else
        PROPERTY_LOG(YES, 8, 0xd9, @"properties operate fail - current network is not good");
}
- (void)doOversizeCallBacksForResSeq:(unsigned long long)rid {
    [self doErrorCallBackWithSeq:rid andCode:6026];
}
- (void)doErrorCallBackWithSeq:(unsigned long long)rid andCode:(long long)code {
    __block JPUSHPropertyReqItem *found = nil;
    [self.requestQueryQueue
        enumerateObjectsUsingBlock:^(JPUSHPropertyReqItem *item, NSUInteger index, BOOL *stop) {
            if ([item isKindOfClass:JPUSHPropertyReqItem.class] &&
                rid == ((JPUSHPropertyRequest *)(id)item.request).rid) {
                found = item;
                *stop = YES;
            }
        }];
    if ([found isKindOfClass:JPUSHPropertyReqItem.class]) {
        [self invokePropertyBlockInMainThread:found andCode:code];
        found.isTimeout = YES;
        [(JPUSHPropertyRequest *)(id)found.request stopTimeoutTimer];
        found.request = nil;
    }
}
- (id)getCallBackPropertyList:(id)properties opearteType:(long long)type {
    if ([properties isKindOfClass:NSDictionary.class] && (unsigned long long)type < 2) {
        NSMutableDictionary *invalid = [NSMutableDictionary dictionary];
        for (id key in [properties allKeys]) {
            id value = properties[key];
            if (![value isKindOfClass:NSString.class] && ![value isKindOfClass:NSNumber.class] &&
                ![value isKindOfClass:NSDate.class])
                [invalid setValue:value forKey:key];
        }
        return [invalid copy];
    }
    return properties;
}
- (long long)checkProperty:(id)properties opearteType:(long long)type {
    if ((unsigned long long)type < 2 && properties && [properties isKindOfClass:NSDictionary.class]) {
        for (id key in [properties allKeys]) {
            long long result = [self checkKey:key];
            if (result)
                return result;
        }
        for (id value in [properties allValues])
            if (![value isKindOfClass:NSString.class] && ![value isKindOfClass:NSNumber.class] &&
                ![value isKindOfClass:NSDate.class])
                return 6037;
    }
    return 0;
}
- (id)changePropertyValueToString:(id)properties {
    if (![properties isKindOfClass:NSDictionary.class])
        return properties;
    NSMutableDictionary *converted = [NSMutableDictionary dictionary];
    for (id key in [properties allKeys]) {
        id value = properties[key];
        if ([value isKindOfClass:NSString.class])
            [converted setValue:value forKey:key];
        else if ([value isKindOfClass:NSNumber.class])
            [converted setValue:[NSString stringWithFormat:@"%@", value] forKey:key];
        else if ([value isKindOfClass:NSDate.class])
            [converted setValue:[NSString stringWithFormat:@"%.f", [value timeIntervalSince1970] * 1000.0]
                         forKey:key];
    }
    return [converted copy];
}
- (long long)checkKey:(id)key {
    if (!key || [key isKindOfClass:NSNull.class])
        return 6001;
    return [key isKindOfClass:NSString.class] ? 0 : 6037;
}
- (BOOL)shouldSendPropertyOperate:(double)timestamp {
    if (self.operateTimeQueue.count > 9) {
        NSInteger previous = [self.operateTimeQueue[self.operateTimeQueue.count - 10] integerValue];
        if (timestamp - (double)previous <= 10) {
            PROPERTY_LOG(YES, 0x40, 0x13f, @"curent property request is over 10 times in 10 second");
            return NO;
        }
    }
    [self.operateTimeQueue addObject:@(timestamp)];
    if (self.operateTimeQueue.count > 10)
        [self.operateTimeQueue removeObjectAtIndex:0];
    return YES;
}
- (void)invokePropertyBlockInMainThread:(unsigned long long)seq
                                andCode:(long long)code
                                  Block:(id)completion
                           propertyInfo:(id)properties {
    JPUSHPropertyReqItem *item = [[JPUSHPropertyReqItem alloc] init];
    item.propertyCompletion = completion;
    item.seq = seq;
    JPUSHPropertyRequest *request = [[JPUSHPropertyRequest alloc] init];
    request.propertyInfo = properties;
    item.request = (id)request;
    [self invokePropertyBlockInMainThread:item andCode:code];
}
- (void)invokePropertyBlockInMainThread:(JPUSHPropertyReqItem *)item andCode:(long long)code {
    [self invokePropertyBlockInMainThread:item
                                  andCode:code
                                   msgDic:((JPUSHPropertyRequest *)(id)item.request).propertyInfo];
}
- (void)invokePropertyBlockInMainThread:(JPUSHPropertyReqItem *)item
                                andCode:(long long)code
                                 msgDic:(id)message {
    if (code != 6038)
        self.isRequesting = NO;
    id properties = ((JPUSHPropertyRequest *)(id)item.request).propertyInfo;
    if (message && [message isKindOfClass:NSDictionary.class] && [[message allKeys] count])
        properties = message;
    dispatch_async(dispatch_get_main_queue(), ^{
        // Read as an Objective-C object until the original NSNull guard has run.
        // A block-typed ARC temporary would call _Block_copy on NSNull before checking it.
        id completion = ((id (*)(id, SEL))objc_msgSend)(item, @selector(propertyCompletion));
        if (completion) {
            completion = ((id (*)(id, SEL))objc_msgSend)(item, @selector(propertyCompletion));
            if (![completion isKindOfClass:NSNull.class]) {
                completion = ((id (*)(id, SEL))objc_msgSend)(item, @selector(propertyCompletion));
                ((JPUSHPropertiesOperationCompletion)completion)(code, properties, item.seq);
            }
        }
    });
}
@end
#endif
