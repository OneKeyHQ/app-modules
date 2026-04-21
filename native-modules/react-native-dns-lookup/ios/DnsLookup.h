#import <RNDnsLookupSpec/RNDnsLookupSpec.h>

@interface DnsLookup : NativeDnsLookupSpecBase <NativeDnsLookupSpec>

- (void)getIpAddresses:(NSString *)hostname
               resolve:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject;

@end
