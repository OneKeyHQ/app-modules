#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHCache.h"
#import "JPUSHTagAliasManagerController.h"
#import "JPUSHTagAliasRequest.h"
#import "JPUSHTagAliasValidation.h"
#import <objc/message.h>
@interface JCORENetUtilities : NSObject
+ (int)networkingStatus;
@end
extern NSString *const kJCORENetworkDidLoginNotification;
extern NSString *const kJCORETcpChannelDisconnectNotification;
#define TAG_MANAGER_LOG(asyncValue, flagValue, lineNo, formatValue, ...)                                     \
    [JCORELog log:asyncValue                                                                                 \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Controller/"    \
                  "JPUSHTagAliasManagerController.m"                                                         \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
static NSString *const FreezeCacheKey = @"_JPUSH_SERVER_UNAVAIABLE_FREEZE_KEY";
@implementation JPUSHTagAliasManagerController
@synthesize requestQueryQueue = _requestQueryQueue, operateTimeQueue = _operateTimeQueue,
            tagsInUse = _tagsInUse, aliasInUse = _aliasInUse, isLogin = _isLogin;
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)init {
    if ((self = [super init])) {
        self.requestQueryQueue = [[NSMutableArray alloc] init];
        self.operateTimeQueue = [[NSMutableArray alloc] init];
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
    static JPUSHTagAliasManagerController *instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[JPUSHTagAliasManagerController alloc] init];
    });
    return instance;
}
- (void)didLogin {
    self.isLogin = YES;
}
- (void)handleTcpClose {
    self.isLogin = NO;
}
- (void)tagOperate:(id)tags type:(long long)type andCompletion:(id)completion andSeq:(long long)seq {
    double now = [JCOREUtilities currentTimestamp];
    long long error;
    if (![self shouldSendTagOrAliasReq:now])
        error = 6011;
    else if (self.tagsInUse)
        error = 6021;
    else {
        self.tagsInUse = YES;
        if ([self isFreezeReport])
            error = 6020;
        else if ((unsigned long long)type < 6) {
            int checked = 0;
            if ((type & ~1LL) != 2)
                checked = [JPUSHTagAliasValidation checkTags:tags];
            if (!checked) {
                JPUSHTagsRequest *request = [[JPUSHTagsRequest alloc] init];
                [request beginTimeoutTimer];
                request.operateType = type;
                request.tags = tags;
                JPUSHTagOrAliasReqItem *item = [[JPUSHTagOrAliasReqItem alloc] init];
                item.ifTag = YES;
                item.request = request;
                if (type == 5)
                    item.tagValidCompletion = completion;
                else
                    item.tagsCompletion = completion;
                item.seq = seq;
                item.createTime = now;
                [self.requestQueryQueue addObject:item];
                [request send];
                return;
            }
            error = checked;
        } else
            error = 6009;
    }
    [self invokeTagsBlockInMainThread:tags Seq:seq andCode:error Block:completion type:type];
}
- (void)aliasOperate:(NSString *)alias
                type:(long long)type
       andCompletion:(JPUSHAliasOperationCompletion)completion
              andSeq:(long long)seq {
    double now = [JCOREUtilities currentTimestamp];
    long long error;
    
    if (![self shouldSendTagOrAliasReq:now])
        error = 6011;
    else if (self.aliasInUse)
        error = 6022;
    else {
        self.aliasInUse = YES;
        if ([self isFreezeReport])
            error = 6020;
        else if ((unsigned long long)type < 5 && (0x1aULL & (1ULL << type))) {
            int checked = 0;
            if ((type & ~2LL) != 1)
                checked = [JPUSHTagAliasValidation checkAlias:alias];
            if (!checked) {
                JPUSHAliasRequest *request = [[JPUSHAliasRequest alloc] init];
                [request beginTimeoutTimer];
                request.operateType = type;
                request.alias = alias;
                JPUSHTagOrAliasReqItem *item = [[JPUSHTagOrAliasReqItem alloc] init];
                item.request = request;
                item.aliasCompletion = completion;
                item.seq = seq;
                item.createTime = now;
                [self.requestQueryQueue addObject:item];
                [request send];
                return;
            }
            error = checked;
        } else
            error = 6009;
    }
    [self invokeAliasBlockInMainThread:alias Seq:seq andCode:error Block:completion];
}
- (void)doCallBacksForResContent:(id)content andRid:(unsigned long long)rid {
    long long code = [content[@"code"] integerValue];
    __block JPUSHTagOrAliasReqItem *found = nil;
    [self.requestQueryQueue
        enumerateObjectsUsingBlock:^(JPUSHTagOrAliasReqItem *item, NSUInteger index, BOOL *stop) {
            if (rid == item.request.rid) {
                found = item;
                *stop = YES;
            }
        }];
    if (!found) {
        TAG_MANAGER_LOG(NO, 0x20, 0xc0, @"a unknown error happend, tags/alias request is missed");
        return;
    }
    if (found.isTimeout) {
        TAG_MANAGER_LOG(NO, 0x20, 0xc5, @"request is timeout,discard seq:%ld", (long)found.seq);
        return;
    }
    if (code == 100)
        [self handleServerUnavaiableResponseWith:found andContent:content];
    else if (code == 1)
        [self handleInternalErrorResponseWith:found];
    else if (!code)
        [self handleSuccessResponseWith:found andContent:content];
    else
        [self handleOtherResponseWith:found andNetworkCode:code];
}
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid {
    [self doErrorCallBackWithSeq:rid andCode:6002];
    if (![JCORENetUtilities networkingStatus])
        TAG_MANAGER_LOG(YES, 8, 0xe2, @"tagAlias operate fail - current network is not reachable");
    else if (!self.isLogin)
        TAG_MANAGER_LOG(YES, 8, 0xe4, @"tagAlias operate fail - current user did not login success");
    else
        TAG_MANAGER_LOG(YES, 8, 0xe6, @"tagAlias operate fail - current network is not good");
}
- (void)doOversizeCallBacksForResSeq:(unsigned long long)rid {
    [self doErrorCallBackWithSeq:rid andCode:6008];
}
- (void)doErrorCallBackWithSeq:(unsigned long long)rid andCode:(int)code {
    __block JPUSHTagOrAliasReqItem *found = nil;
    [self.requestQueryQueue
        enumerateObjectsUsingBlock:^(JPUSHTagOrAliasReqItem *item, NSUInteger index, BOOL *stop) {
            if ([item isKindOfClass:JPUSHTagOrAliasReqItem.class] && rid == item.request.rid) {
                found = item;
                *stop = YES;
            }
        }];
    if ([found isKindOfClass:JPUSHTagOrAliasReqItem.class]) {
        found.isTimeout = YES;
        if (found.ifTag)
            [self invokeTagsBlockInMainThread:((JPUSHTagsRequest *)found.request).tags
                                         item:found
                                      andCode:(unsigned int)code];
        else
            [self invokeAliasBlockInMainThread:((JPUSHAliasRequest *)found.request).alias
                                           Seq:found.seq
                                       andCode:(unsigned int)code
                                         Block:found.aliasCompletion];
    }
}
+ (NSString *)getTAOpearteString:(long long)type {
    if ((unsigned long long)type < 6) {
        static NSString *const names[] = {@"add", @"del", @"clean", @"get", @"set", @"valid"};
        return names[type];
    }
    TAG_MANAGER_LOG(YES, 0x100, 0x11a, @"error tag/alias operate type:%ld!!!", (long)type);
    return nil;
}
- (void)handleSuccessResponseWith:(JPUSHTagOrAliasReqItem *)item andContent:(id)content {
    TAG_MANAGER_LOG(YES, 0x100, 0x125, @"tag/alias request is success, seq:%ld", (long)item.seq);
    JPUSHTagsRequest *request = (id)item.request;
    if (!item.ifTag) {
        JPUSHAliasRequest *aliasRequest = (id)request;
        id alias = aliasRequest.alias;
        if (aliasRequest.operateType == 3)
            alias = content[@"alias"];
        [self invokeAliasBlockInMainThread:alias Seq:item.seq andCode:0 Block:item.aliasCompletion];
    } else {
        NSSet *tags = request.tags;
        if (request.operateType == 3) {
            id serverTags = content[@"tags"];
            tags = [serverTags isKindOfClass:NSArray.class] && [serverTags count]
                       ? [NSSet setWithArray:serverTags]
                       : [[NSSet alloc] init];
            NSInteger total = [content[@"total"] integerValue];
            if (total == 0) {
                [self invokeTagsBlockInMainThread:[[NSMutableSet alloc] init] item:item andCode:0];
                [request stopTimeoutTimer];
                item.request = nil;
                return;
            }
            NSInteger current = [content[@"curr"] integerValue];
            if (total > 0 && current > 0 && current < total) {
                if (!item.tagsBufSet)
                    item.tagsBufSet = [[NSMutableSet alloc] init];
                [item.tagsBufSet unionSet:tags];
                request.currentPage = current + 1;
                [request send];
                [request beginTimeoutTimer];
                return;
            }
            if (total < 1 || current != total) {
                [self handleFailReq:item andCode:6009];
                return;
            }
            if (!item.tagsBufSet)
                item.tagsBufSet = [[NSMutableSet alloc] init];
            [item.tagsBufSet unionSet:tags];
            [self invokeTagsBlockInMainThread:[item.tagsBufSet copy] item:item andCode:0];
            [request stopTimeoutTimer];
            item.request = nil;
            item.tagsBufSet = nil;
            return;
        }
        item.isBind = [content[@"validated"] boolValue];
        [self invokeTagsBlockInMainThread:tags item:item andCode:0];
    }
    [request stopTimeoutTimer];
    item.request = nil;
}
- (void)handleInternalErrorResponseWith:(JPUSHTagOrAliasReqItem *)item {
    TAG_MANAGER_LOG(YES, 0x100, 0x169, @"tag/alias request has a internal error happend, seq:%ld",
                    (long)item.seq);
    if (!item.hasResend) {
        item.hasResend = YES;
        [item.request send];
        (void)item.ifTag;
        [(JPUSHTagsRequest *)item.request beginTimeoutTimer];
    } else
        [self handleFailReq:item andCode:6014];
}
- (void)handleServerUnavaiableResponseWith:(JPUSHTagOrAliasReqItem *)item andContent:(id)content {
    TAG_MANAGER_LOG(YES, 0x100, 0x17b, @"tag/alias request has a server unavaialable error happend, seq:%ld",
                    (long)item.seq);
    double wait = [content[@"wait"] doubleValue];
    if (wait <= 0)
        wait = 0;
    [JPUSHCache setObject:@([JCOREUtilities currentTimestamp] + wait) forKey:FreezeCacheKey];
    [self handleFailReq:item andCode:6020];
}
- (void)handleOtherResponseWith:(JPUSHTagOrAliasReqItem *)item andNetworkCode:(long long)code {
    TAG_MANAGER_LOG(YES, 0x100, 0x18b, @"tag/alias request has a error happend,code:%ld seq:%ld", (long)code,
                    (long)item.seq);
    int mapped = 6009;
    switch (code) {
    case 2:
        mapped = 6014;
        break;
    case 3:
        mapped = 6015;
        break;
    case 4:
        mapped = 6016;
        break;
    case 5:
        mapped = 6017;
        break;
    case 6:
        mapped = 6018;
        break;
    case 7:
    case 8:
        item.tagsBufSet = nil;
        mapped = 6019;
        break;
    case 17:
        mapped = 6027;
        break;
    }
    [self handleFailReq:item andCode:mapped];
}
- (void)handleFailReq:(JPUSHTagOrAliasReqItem *)item andCode:(int)code {
    if (item.ifTag)
        [self invokeTagsBlockInMainThread:((JPUSHTagsRequest *)item.request).tags
                                     item:item
                                  andCode:(unsigned int)code];
    else
        [self invokeAliasBlockInMainThread:((JPUSHAliasRequest *)item.request).alias
                                       Seq:item.seq
                                   andCode:(unsigned int)code
                                     Block:item.aliasCompletion];
    [(JPUSHTagsRequest *)item.request stopTimeoutTimer];
    item.request = nil;
}
- (void)invokeTagsBlockInMainThread:(id)tags
                                Seq:(unsigned long long)seq
                            andCode:(long long)code
                              Block:(id)completion
                               type:(long long)type {
    JPUSHTagOrAliasReqItem *item = [[JPUSHTagOrAliasReqItem alloc] init];
    if (type == 5)
        item.tagValidCompletion = completion;
    else
        item.tagsCompletion = completion;
    item.seq = seq;
    [self invokeTagsBlockInMainThread:tags item:item andCode:code];
}
- (void)invokeTagsBlockInMainThread:(id)tags item:(JPUSHTagOrAliasReqItem *)item andCode:(long long)code {
    if (code != 6021)
        self.tagsInUse = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        id callback = ((id (*)(id, SEL))objc_msgSend)(item, @selector(tagsCompletion));
        if (callback) {
            callback = ((id (*)(id, SEL))objc_msgSend)(item, @selector(tagsCompletion));
            if (![callback isKindOfClass:NSNull.class]) {
                callback = ((id (*)(id, SEL))objc_msgSend)(item, @selector(tagsCompletion));
                ((JPUSHTagsOperationCompletion)callback)(code, tags, item.seq);
                return;
            }
        }
        callback = ((id (*)(id, SEL))objc_msgSend)(item, @selector(tagValidCompletion));
        if (callback) {
            callback = ((id (*)(id, SEL))objc_msgSend)(item, @selector(tagValidCompletion));
            if (![callback isKindOfClass:NSNull.class]) {
                callback = ((id (*)(id, SEL))objc_msgSend)(item, @selector(tagValidCompletion));
                ((JPUSHTagValidOperationCompletion)callback)(code, tags, item.seq, item.isBind);
            }
        }
    });
}
- (void)invokeAliasBlockInMainThread:(id)alias
                                 Seq:(long long)seq
                             andCode:(long long)code
                               Block:(JPUSHAliasOperationCompletion)completion {
    if (code != 6022)
        self.aliasInUse = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        if (completion && ![(id)completion isKindOfClass:NSNull.class])
            completion(code, alias, seq);
    });
}
- (BOOL)shouldSendTagOrAliasReq:(double)timestamp {
    if (self.operateTimeQueue.count > 9) {
        NSInteger previous = [self.operateTimeQueue[self.operateTimeQueue.count - 10] integerValue];
        if (timestamp - (double)previous <= 10) {
            TAG_MANAGER_LOG(YES, 0x40, 0x1dd, @"curent tag/alias request is over 10 times in 10 second");
            return NO;
        }
    }
    [self.operateTimeQueue addObject:@(timestamp)];
    if (self.operateTimeQueue.count > 10)
        [self.operateTimeQueue removeObjectAtIndex:0];
    return YES;
}
- (BOOL)isFreezeReport {
    double until = [[JCORECacheChannel serverUnavailableFreeze] doubleValue];
    if (until <= 0)
        until = [[JPUSHCache objectForKey:FreezeCacheKey] doubleValue];
    double now = [JCOREUtilities currentTimestamp];
    if (until <= now) {
        [JCORECacheChannel setServerUnavailableFreeze:nil];
        [JPUSHCache removeObjectForKey:FreezeCacheKey];
    }
    return now < until;
}
@end
#endif
