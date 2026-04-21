#import <RNNetworkInfoSpec/RNNetworkInfoSpec.h>

@interface NetworkInfo : NativeNetworkInfoSpecBase <NativeNetworkInfoSpec>

- (void)getSSID:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject;
- (void)getBSSID:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject;
- (void)getBroadcast:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject;
- (void)getIPAddress:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject;
- (void)getIPV6Address:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject;
- (void)getGatewayIPAddress:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject;
- (void)getIPV4Address:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject;
- (void)getWIFIIPV4Address:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject;
- (void)getSubnet:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject;
- (void)getFrequency:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject;

@end
