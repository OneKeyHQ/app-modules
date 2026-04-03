#import <RNPingSpec/RNPingSpec.h>

@interface Ping : NativePingSpecBase <NativePingSpec>

- (void)start:(NSString *)ipAddress
       option:(JS::NativePing::SpecStartOption &)option
      resolve:(RCTPromiseResolveBlock)resolve
       reject:(RCTPromiseRejectBlock)reject;

@end
