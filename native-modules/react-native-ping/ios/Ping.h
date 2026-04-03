#import <RNPingSpec/RNPingSpec.h>

@interface Ping : NativePingSpecBase <NativePingSpec>

- (void)start:(NSString *)ipAddress
       option:(JS::NativeRNReactNativePing::SpecStartOption &)option
      resolve:(RCTPromiseResolveBlock)resolve
       reject:(RCTPromiseRejectBlock)reject;

@end
