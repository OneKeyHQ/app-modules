#pragma once
#import <Foundation/Foundation.h>
#import <SystemConfiguration/SystemConfiguration.h>

FOUNDATION_EXPORT NSString *const JCORENetworkingReachabilityDidChangeNotification;
FOUNDATION_EXPORT NSString *const JCORENetworkingReachabilityNotificationStatusItem;

typedef void (^JCORENetworkReachabilityStatusChangeBlock)(NSInteger status);

@interface JCORENetworkReachabilityManager : NSObject {
    NSInteger _networkReachabilityStatus;
    SCNetworkReachabilityRef _networkReachability;
    JCORENetworkReachabilityStatusChangeBlock _networkReachabilityStatusBlock;
    NSMutableArray *_networkReachabilityStatusBlockArr;
}
@property (readonly, nonatomic) SCNetworkReachabilityRef networkReachability;
@property (nonatomic) NSInteger networkReachabilityStatus;
@property (copy, nonatomic) JCORENetworkReachabilityStatusChangeBlock networkReachabilityStatusBlock;
@property (strong, nonatomic) NSMutableArray *networkReachabilityStatusBlockArr;
@property (readonly, nonatomic, getter=isReachable) BOOL reachable;
+ (instancetype)sharedManager;
+ (instancetype)manager;
+ (instancetype)managerForAddress:(const void *)address;
+ (instancetype)managerForDomain:(NSString *)domain;
- (instancetype)initWithReachability:(SCNetworkReachabilityRef)reachability;
- (BOOL)isReachableViaWWAN;
- (BOOL)isReachableViaWiFi;
- (void)startMonitoring;
- (void)stopMonitoring;
- (void)setReachabilityStatusChangeBlock:(JCORENetworkReachabilityStatusChangeBlock)block;
- (void)addHttpReachablityStatusChangeBlock:(JCORENetworkReachabilityStatusChangeBlock)block;
@end
