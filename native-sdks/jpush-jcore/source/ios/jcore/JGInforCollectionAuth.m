#import "JGInforCollectionAuth.h"
#import "RecoveredCoreService.h"
#import "JCORELog.h"

@implementation JGInforCollectionAuth
+ (void)JCollectionAuth:(void (^)(JGInforCollectionAuthItems *authInfo))authBlock {
    if (authBlock) {
        [JCOREService resetDeviceInforCollectionAuth:authBlock];
        return;
    }
    [JCORELog log:YES level:0x3ff flag:0x100 context:0
        file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JGInforCollectionAuth.m"
        function:"+[JGInforCollectionAuth JCollectionAuth:]" line:28 tag:nil format:@"authBlock is nil"];
}
@end
