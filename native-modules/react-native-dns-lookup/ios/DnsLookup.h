#import <RNDnsLookupSpec/RNDnsLookupSpec.h>

@interface DnsLookup : NativeRNDnsLookupSpecBase <NativeRNDnsLookupSpec>

- (void)getIpAddresses:(NSString *)hostname
               resolve:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject;

@end
