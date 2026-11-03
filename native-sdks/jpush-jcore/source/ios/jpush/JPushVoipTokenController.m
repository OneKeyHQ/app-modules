#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#import "JCORENetworkReachabilityManager.h"
#import "JPUSHHelper.h"
#import "JPushVoipTokenController.h"
#import "RecoveredCoreService.h"
@implementation JPushVoipTokenController
@synthesize voipToken = _voipToken;
+ (instancetype)shared {
  static id instance;
  static dispatch_once_t once;
  dispatch_once(&once, ^{
    instance = [[super allocWithZone:NULL] init];
  });
  return instance;
}
+ (id)allocWithZone:(NSZone *)zone {
  return [JPushVoipTokenController shared];
}
- (id)copyWithZone:(NSZone *)zone {
  return [JPushVoipTokenController shared];
}
- (instancetype)init {
  if ((self = [super init]))
    [NSNotificationCenter.defaultCenter
        addObserver:self
           selector:@selector(networkingReachabilityDidChange:)
               name:JCORENetworkingReachabilityDidChangeNotification
             object:nil];
  return self;
}
- (void)dealloc {
  [NSNotificationCenter.defaultCenter removeObserver:self];
}
- (void)networkingReachabilityDidChange:(id)notification {
  if ([JCORENetworkReachabilityManager sharedManager].isReachable &&
      ![JCOREClientInfo voipTokenUpdated])
    [self reportVoipToken];
}
- (void)uploadVoipToken:(id)token {
  self.voipToken = [JPUSHHelper convertTokenDataToString:token];
  [self reportVoipToken];
}
- (void)reportVoipToken {
  if ([JCOREService respondsToSelector:@selector(reportVoipToken:invalid:)])
    [JCOREService performSelector:@selector(reportVoipToken:invalid:)
                       withObject:self.voipToken
                       withObject:@NO];
  else
    [JCORELog log:NO
            level:0x3ff
             flag:1
          context:0
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
                  "ios-jpush/JPush/JPush/Controller/JPushVoipTokenController.m"
         function:__PRETTY_FUNCTION__
             line:0x46
              tag:nil
           format:@"[ Voip feature ]JCore must be at least 2.2.4"];
}
@end
#endif
