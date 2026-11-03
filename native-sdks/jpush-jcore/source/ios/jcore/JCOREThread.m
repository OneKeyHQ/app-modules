#import "JCOREThread.h"
#import "JCOREQueueManager.h"
#import "RecoveredTransportDependencies.h"
#import <dispatch/dispatch.h>

@implementation JCOREThread
+ (instancetype)instance {
    static JCOREThread *instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[JCOREThread alloc] init];
    });
    return instance;
}

+ (NSThread *)mainThread {
    return [JCOREQueueManager residentThread];
}

+ (BOOL)isMainThread {
    return [[NSThread currentThread] isEqual:[self mainThread]];
}

+ (void)performSel:(SEL)selector target:(id)target withObject:(id)object {
    if (!target) {
        [JCORELog log:NO level:0x3ff flag:0x20 context:0
                 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREThread.m"
             function:"+[JCOREThread performSel:target:withObject:]" line:0x33 tag:nil
               format:@"%@ cancel, target nil", NSStringFromSelector(_cmd)];
    } else if (![target respondsToSelector:selector]) {
        [JCORELog log:NO level:0x3ff flag:0x20 context:0
                 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREThread.m"
             function:"+[JCOREThread performSel:target:withObject:]" line:0x37 tag:nil
               format:@"%@ cancel, %@ not found", NSStringFromSelector(_cmd), NSStringFromSelector(selector)];
    } else {
        [target performSelector:selector onThread:[self mainThread] withObject:object waitUntilDone:NO];
    }
}

+ (void)performBlock:(void (^ __unsafe_unretained)(void))block {
    // Both original device and simulator implementations return without invoking the block.
}

- (instancetype)init {
    return [super init];
}
@end
