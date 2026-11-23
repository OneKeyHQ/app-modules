#import "RCTJPushEventQueue.h"

@implementation RCTJPushEventQueue

+ (instancetype)sharedInstance {
    static RCTJPushEventQueue *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        self._notificationQueue = [NSMutableArray array];
        self._localNotificationQueue = [NSMutableArray array];
    }
    return self;
}

@end
