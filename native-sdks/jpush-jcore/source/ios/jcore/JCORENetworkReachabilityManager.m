#import "JCORENetworkReachabilityManager.h"
#import <Block.h>
#import <dispatch/dispatch.h>
#import <netinet/in.h>

NSString *const JCORENetworkingReachabilityDidChangeNotification = @"com.hxhg.networking.reachability.change";
NSString *const JCORENetworkingReachabilityNotificationStatusItem = @"JCORENetworkingReachabilityNotificationStatusItem";

static void JCOREPostReachabilityStatusChange(SCNetworkReachabilityFlags flags, JCORENetworkReachabilityStatusChangeBlock block) {
    BOOL reachable = (flags & kSCNetworkReachabilityFlagsReachable) != 0;
    BOOL connectionRequired = (flags & kSCNetworkReachabilityFlagsConnectionRequired) != 0;
    BOOL automatic = (flags & (kSCNetworkReachabilityFlagsConnectionOnDemand | kSCNetworkReachabilityFlagsConnectionOnTraffic)) != 0;
    BOOL interventionRequired = (flags & kSCNetworkReachabilityFlagsInterventionRequired) != 0;
    NSInteger status = 0;
    if (reachable && (!connectionRequired || (automatic && !interventionRequired))) {
        // The original checks the WWAN bit on every target, including the simulator.
        status = (flags & (1U << 18)) != 0 ? 1 : 2;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        if (block) block(status);
        [[NSNotificationCenter defaultCenter] postNotificationName:JCORENetworkingReachabilityDidChangeNotification
                                                          object:nil
                                                        userInfo:@{JCORENetworkingReachabilityNotificationStatusItem: @(status)}];
    });
}

static const void *JCORENetworkReachabilityRetainCallback(const void *info) {
    return _Block_copy(info);
}

static void JCORENetworkReachabilityReleaseCallback(const void *info) {
    if (info) _Block_release(info);
}

static void JCORENetworkReachabilityCallback(SCNetworkReachabilityRef reachability, SCNetworkReachabilityFlags flags, void *info) {
    JCOREPostReachabilityStatusChange(flags, (__bridge JCORENetworkReachabilityStatusChangeBlock)info);
}

@implementation JCORENetworkReachabilityManager
@synthesize networkReachability = _networkReachability;
@synthesize networkReachabilityStatus = _networkReachabilityStatus;
@synthesize networkReachabilityStatusBlock = _networkReachabilityStatusBlock;
@synthesize networkReachabilityStatusBlockArr = _networkReachabilityStatusBlockArr;

+ (instancetype)sharedManager {
    static JCORENetworkReachabilityManager *manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [self manager];
    });
    return manager;
}

+ (instancetype)manager {
    struct sockaddr_in address = {0};
    address.sin_len = sizeof(address);
    address.sin_family = AF_INET;
    return [self managerForAddress:&address];
}

+ (instancetype)managerForAddress:(const void *)address {
    SCNetworkReachabilityRef reachability = SCNetworkReachabilityCreateWithAddress(kCFAllocatorDefault, address);
    JCORENetworkReachabilityManager *manager = [[self alloc] initWithReachability:reachability];
    CFRelease(reachability);
    return manager;
}

+ (instancetype)managerForDomain:(NSString *)domain {
    SCNetworkReachabilityRef reachability = SCNetworkReachabilityCreateWithName(kCFAllocatorDefault, domain.UTF8String);
    JCORENetworkReachabilityManager *manager = [[self alloc] initWithReachability:reachability];
    CFRelease(reachability);
    return manager;
}

- (instancetype)initWithReachability:(SCNetworkReachabilityRef)reachability {
    self = [super init];
    if (self) {
        _networkReachability = (SCNetworkReachabilityRef)CFRetain(reachability);
        self.networkReachabilityStatus = -1;
    }
    return self;
}

- (void)dealloc {
    if (_networkReachability) CFRelease(_networkReachability);
}

- (BOOL)isReachable {
    return self.isReachableViaWWAN || self.isReachableViaWiFi;
}

- (BOOL)isReachableViaWWAN {
    return self.networkReachabilityStatus == 1;
}

- (BOOL)isReachableViaWiFi {
    return self.networkReachabilityStatus == 2;
}

- (void)startMonitoring {
    [self stopMonitoring];
    if (!self.networkReachability) return;

    __weak JCORENetworkReachabilityManager *weakSelf = self;
    JCORENetworkReachabilityStatusChangeBlock block = ^(NSInteger status) {
        JCORENetworkReachabilityManager *strongSelf = weakSelf;
        strongSelf.networkReachabilityStatus = status;
        if (strongSelf.networkReachabilityStatusBlock) strongSelf.networkReachabilityStatusBlock(status);
        if (status == 1 || status == 2) {
            @synchronized (strongSelf.networkReachabilityStatusBlockArr) {
                if (strongSelf.networkReachabilityStatusBlockArr.count) {
                    NSArray *blocks = [strongSelf.networkReachabilityStatusBlockArr copy];
                    [strongSelf.networkReachabilityStatusBlockArr removeAllObjects];
                    for (JCORENetworkReachabilityStatusChangeBlock callback in blocks) {
                        if (callback) callback(status);
                    }
                }
            }
        }
    };
    SCNetworkReachabilityContext context = {0, (__bridge void *)block, JCORENetworkReachabilityRetainCallback, JCORENetworkReachabilityReleaseCallback, NULL};
    SCNetworkReachabilitySetCallback(self.networkReachability, JCORENetworkReachabilityCallback, &context);
    SCNetworkReachabilityScheduleWithRunLoop(self.networkReachability, CFRunLoopGetMain(), kCFRunLoopCommonModes);
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        SCNetworkReachabilityFlags flags;
        if (SCNetworkReachabilityGetFlags(self.networkReachability, &flags)) {
            JCOREPostReachabilityStatusChange(flags, block);
        }
    });
}

- (void)stopMonitoring {
    if (self.networkReachability) {
        SCNetworkReachabilityUnscheduleFromRunLoop(self.networkReachability, CFRunLoopGetMain(), kCFRunLoopCommonModes);
    }
}

- (void)setReachabilityStatusChangeBlock:(JCORENetworkReachabilityStatusChangeBlock)block {
    self.networkReachabilityStatusBlock = block;
}

- (void)addHttpReachablityStatusChangeBlock:(JCORENetworkReachabilityStatusChangeBlock)block {
    @synchronized (self.networkReachabilityStatusBlockArr) {
        if (self.networkReachabilityStatusBlockArr.count < 100 && block) {
            [self.networkReachabilityStatusBlockArr addObject:[block copy]];
        }
    }
}

- (NSMutableArray *)networkReachabilityStatusBlockArr {
    if (!_networkReachabilityStatusBlockArr) _networkReachabilityStatusBlockArr = [NSMutableArray array];
    return _networkReachabilityStatusBlockArr;
}
@end
