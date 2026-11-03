#pragma once
#import <Foundation/Foundation.h>
@interface JPushVoipTokenController : NSObject {
  NSString *_voipToken;
}
@property(nonatomic, strong) NSString *voipToken;
+ (instancetype)shared;
+ (id)allocWithZone:(NSZone *)zone;
- (id)copyWithZone:(NSZone *)zone;
- (void)networkingReachabilityDidChange:(id)notification;
- (void)uploadVoipToken:(id)token;
- (void)reportVoipToken;
@end
