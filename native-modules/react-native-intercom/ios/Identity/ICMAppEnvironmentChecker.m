// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMAppEnvironmentChecker.h"
#import <TargetConditionals.h>
@implementation ICMAppEnvironmentChecker
+ (NSUInteger)appEnvironment {
    static NSUInteger environment;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ environment = [self findCurrentAppEnvironment]; });
    return environment;
}
+ (BOOL)isDevelopmentEnvironment:(NSUInteger)environment { return (environment & ~((NSUInteger)1)) == 4; }
+ (NSString *)stringForAppEnvironment:(NSUInteger)environment {
    switch (environment) {
        case 0: return @"appstore";
        case 1: return @"testflight";
        case 2: return @"enterprise";
        case 3: return @"adhoc";
        case 4: return @"development";
        case 5: return @"simulator";
        default: return @"unknown";
    }
}
+ (NSString *)stringForDeviceTokenEnvironment:(NSUInteger)environment { return (environment & ~((NSUInteger)1)) == 4 ? @"development" : @"production"; }
+ (NSUInteger)findCurrentAppEnvironment {
    if ([self isRunningOnSimulator]) return 5;
    if ([self hasEmbeddedMobileProvision]) return [self environmentFromEmbeddedMobileProvision];
    return [self isAppStoreReceiptSandbox];
}
+ (BOOL)isRunningOnSimulator { return TARGET_OS_SIMULATOR; }
+ (BOOL)hasEmbeddedMobileProvision { return [[NSBundle mainBundle] pathForResource:@"embedded" ofType:@"mobileprovision"] != nil; }
+ (NSUInteger)environmentFromEmbeddedMobileProvision {
    NSData *data = [NSData dataWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"embedded" ofType:@"mobileprovision"]];
    if (!data) return 99;
    const signed char *bytes = data.bytes;
    NSMutableString *contents = [[NSMutableString alloc] initWithCapacity:data.length];
    for (NSUInteger index = 0; index < data.length; index++) [contents appendFormat:@"%c", bytes[index]];
    NSString *compact = [[contents componentsSeparatedByCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet] componentsJoinedByString:@""];
    if ([compact rangeOfString:@"<key>get-task-allow</key><true/>"].length) return 4;
    if ([compact rangeOfString:@"<key>ProvisionsAllDevices</key><true/>"].length) return 2;
    if ([compact rangeOfString:@"<key>ProvisionedDevices</key><array>"].length) return 3;
    return 99;
}
+ (BOOL)isAppStoreReceiptSandbox {
    if (![[NSBundle mainBundle] respondsToSelector:@selector(appStoreReceiptURL)]) return NO;
    return [[[NSBundle mainBundle].appStoreReceiptURL lastPathComponent] isEqualToString:@"sandboxReceipt"];
}
@end
