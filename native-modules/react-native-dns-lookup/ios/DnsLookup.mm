#import "DnsLookup.h"

#import <CFNetwork/CFNetwork.h>
#import <netinet/in.h>
#import <netdb.h>
#import <ifaddrs.h>
#import <arpa/inet.h>

@implementation DnsLookup

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeDnsLookupSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"RNDnsLookup";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

// MARK: - getIpAddresses

- (void)getIpAddresses:(NSString *)hostname
               resolve:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error = nil;
        NSArray *addresses = [self performDnsLookup:hostname error:&error];

        if (addresses == nil) {
            NSString *errorCode = [NSString stringWithFormat:@"%ld", (long)error.code];
            reject(errorCode, error.userInfo[NSDebugDescriptionErrorKey], error);
        } else {
            resolve(addresses);
        }
    });
}

// MARK: - DNS lookup helper

- (NSArray *)performDnsLookup:(NSString *)hostname
                        error:(NSError **)error
{
    if (hostname == nil) {
        *error = [NSError errorWithDomain:NSGenericException
                                     code:kCFHostErrorUnknown
                                 userInfo:@{NSDebugDescriptionErrorKey: @"Hostname cannot be null."}];
        return nil;
    }

    CFHostRef hostRef = CFHostCreateWithName(kCFAllocatorDefault, (__bridge CFStringRef)hostname);
    if (hostRef == nil) {
        *error = [NSError errorWithDomain:NSGenericException
                                     code:kCFHostErrorUnknown
                                 userInfo:@{NSDebugDescriptionErrorKey: @"Failed to create host."}];
        return nil;
    }

    BOOL didStart = CFHostStartInfoResolution(hostRef, kCFHostAddresses, nil);
    if (!didStart) {
        *error = [NSError errorWithDomain:NSGenericException
                                     code:kCFHostErrorUnknown
                                 userInfo:@{NSDebugDescriptionErrorKey: @"Failed to start."}];
        CFRelease(hostRef);
        return nil;
    }

    CFArrayRef addressesRef = CFHostGetAddressing(hostRef, nil);
    if (addressesRef == nil) {
        *error = [NSError errorWithDomain:NSGenericException
                                     code:kCFHostErrorUnknown
                                 userInfo:@{NSDebugDescriptionErrorKey: @"Failed to get addresses."}];
        CFRelease(hostRef);
        return nil;
    }

    NSMutableArray *addresses = [NSMutableArray array];
    char ipAddress[INET6_ADDRSTRLEN];
    CFIndex numAddresses = CFArrayGetCount(addressesRef);

    for (CFIndex currentIndex = 0; currentIndex < numAddresses; currentIndex++) {
        struct sockaddr *address = (struct sockaddr *)CFDataGetBytePtr(
            (CFDataRef)CFArrayGetValueAtIndex(addressesRef, currentIndex));
        getnameinfo(address, address->sa_len, ipAddress, INET6_ADDRSTRLEN, nil, 0, NI_NUMERICHOST);
        [addresses addObject:[NSString stringWithCString:ipAddress encoding:NSASCIIStringEncoding]];
    }

    CFRelease(hostRef);
    return addresses;
}

@end
