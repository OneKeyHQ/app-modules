#import "JCORENetChannel.h"
#import "JCOREThread.h"
#import "JCORETcpObject.h"
#import "JCOREBusinessStorage.h"

#import "JCORETcpChannelController.h"
#import "JCOREHttpChannelController.h"
#import "JCORESisManager.h"

static JCORENetChannel *JCORESharedNetChannel;
@implementation JCORENetChannel
@synthesize tcpChannelController = _tcpChannelController, httpChannelController = _httpChannelController, rid = _rid, sisManager = _sisManager;
+ (void)setupNetService { [[self sharedChannel] setupNetService]; }
+ (void)send:(id)request { [JCOREThread performSel:@selector(send:) target:[self sharedChannel] withObject:request]; }
+ (BOOL)httpRequesting { return [self sharedChannel].httpChannelController.requesting; }
+ (void)reportAddressListWithType:(NSString *)type completion:(JCOREAddressListCompletion __unsafe_unretained)completion {
    // Preserve the original NSNull guard before ARC treats this value as a block.
    id input = (__bridge id)(__bridge void *)completion;
    if ([type isMemberOfClass:NSString.class] && input && ![input isKindOfClass:NSNull.class]) {
        JCOREAddressListCompletion handler = input;
        [[self sharedChannel] reportAddressListForKey:type completion:^(NSArray *addresses) { handler(addresses); }];
    }
}
+ (void)forceRequestReportAddress { [[JCORESisManager shared] forceRequestReportAddress]; }
+ (NSArray *)addressListWithType:(NSString *)type { return [type isEqualToString:@"badgeType"] ? [JCOREBusinessStorage getUserAddress] : @[]; }
+ (void)stopHttpService {
    if ([JCOREThread isMainThread]) [[self sharedChannel] stopHttpService];
    else [JCOREThread performSel:@selector(stopHttpService) target:[self sharedChannel] withObject:nil];
}
+ (void)stopTcpService {
    if ([JCOREThread isMainThread]) [[self sharedChannel] stopTcpService];
    else [JCOREThread performSel:@selector(stopTcpService) target:[self sharedChannel] withObject:nil];
}
+ (instancetype)sharedChannel {
    static dispatch_once_t once;
    dispatch_once(&once, ^{ if (!JCORESharedNetChannel) JCORESharedNetChannel = [[JCORENetChannel alloc] init]; });
    return JCORESharedNetChannel;
}
+ (id)allocWithZone:(NSZone *)zone {
    static dispatch_once_t once;
    dispatch_once(&once, ^{ if (!JCORESharedNetChannel) JCORESharedNetChannel = [super allocWithZone:zone]; });
    return JCORESharedNetChannel;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _tcpChannelController = [[JCORETcpChannelController alloc] init];
        _httpChannelController = [[JCOREHttpChannelController alloc] init];
        _sisManager = [JCORESisManager shared];
    }
    return self;
}
- (void)setupNetService { [self.tcpChannelController setupService]; }
- (void)send:(id)request {
    if ([request conformsToProtocol:@protocol(JCORETcpProtocol)]) [self.tcpChannelController send:request];
    else if ([request conformsToProtocol:@protocol(JCOREHttpProtocol)]) [self.httpChannelController send:request];
}
- (void)reportAddressListForKey:(NSString *)key completion:(JCOREAddressListCompletion)completion {
    [_sisManager reportAddressListForKey:key completion:^(NSArray *addresses) { completion(addresses); }];
}
- (void)stopTcpService { [self.tcpChannelController stopTcpService]; }
- (void)stopHttpService { [self.httpChannelController stopHttpService]; }
@end
