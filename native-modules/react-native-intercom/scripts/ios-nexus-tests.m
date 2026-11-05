#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "IntercomNexusConfiguration.h"
static NSMutableArray *passed;
static void check(BOOL condition, NSString *name) {
    if (!condition) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        IntercomNexusConfiguration *configuration = [IntercomNexusConfiguration new];
        check(configuration.endpoints.count == 0 && [configuration.connectionTimeout isEqual:@0] && configuration.connectionHeartbeatInterval == configuration.connectionTimeout && configuration.backgroundConnectionTimeout == configuration.connectionTimeout, @"empty endpoints and shared zero numeric defaults");
        IntercomNexusConfiguration *parsed = [IntercomNexusConfiguration instanceFromDictionary:@{@"endpoints":@[@"https://synthetic.invalid/path?existing=1", @"http://synthetic.invalid/", @"HTTP://synthetic.invalid/", @"prefix-http://synthetic.invalid", @"", @42, [NSNull null]], @"connection_timeout":@(-1), @"presence_heartbeat_interval":@YES, @"backgroundConnectionTimeout":@3, @"unknown":@1}];
        NSArray *urls = [parsed.endpoints valueForKey:@"absoluteString"];
        check([urls isEqual:@[@"wss://synthetic.invalid/path?existing=1?X-Nexus-Version=ios.5.5.0", @"ws://synthetic.invalid/?X-Nexus-Version=ios.5.5.0", @"HTTP://synthetic.invalid/?X-Nexus-Version=ios.5.5.0", @"prefix-http://synthetic.invalid?X-Nexus-Version=ios.5.5.0", @"?X-Nexus-Version=ios.5.5.0"]], @"literal suffix and case-sensitive offset-zero scheme replacement");
        check([parsed.connectionTimeout isEqual:@(-1)] && [parsed.connectionHeartbeatInterval isEqual:@YES] && [parsed.backgroundConnectionTimeout isEqual:@3], @"numeric snake keys and direct KVC camel field preserve values");
        NSArray *before = parsed.endpoints;
        [parsed setValue:@"wrong" forKey:@"endpoints"];
        check(parsed.endpoints == before, @"wrong endpoints type preserves previous value");
        [parsed setValue:@"wrong" forKey:@"connection_timeout"];
        [parsed setValue:[NSNull null] forKey:@"presence_heartbeat_interval"];
        check([parsed.connectionTimeout isEqual:@(-1)] && [parsed.connectionHeartbeatInterval isEqual:@YES], @"wrong snake key values preserve previous numbers");
        [parsed setValue:@"unvalidated" forKey:@"connectionTimeout"];
        check([(id)parsed.connectionTimeout isEqual:@"unvalidated"], @"direct camel KVC preserves original lack of type validation");
        NSMutableArray *alias = [NSMutableArray arrayWithObject:[NSURL URLWithString:@"fixture://one"]];
        configuration.endpoints = alias; [alias addObject:[NSURL URLWithString:@"fixture://two"]];
        check(configuration.endpoints == alias && configuration.endpoints.count == 2, @"direct endpoint setter retains mutable array");
        [configuration setAttributesFromDictionary:nil];
        [configuration setAttributesFromDictionary:@[]];
        check(configuration.endpoints == alias, @"non-dictionary attribute update ignored");
        IntercomNexusConfiguration *defaults = [IntercomNexusConfiguration instanceFromDictionary:nil];
        check([defaults isEqual:[IntercomNexusConfiguration new]], @"same fields compare equal across separate instances");
        defaults.backgroundConnectionTimeout = nil;
        check(![defaults isEqual:[IntercomNexusConfiguration new]], @"different nil numeric field compares unequal");
        defaults.endpoints = nil; defaults.connectionTimeout = nil; defaults.connectionHeartbeatInterval = nil;
        check([defaults isEqual:nil], @"all nil fields preserve original equality with nil");
        BOOL threw = NO;
        @try { [configuration isEqual:(id)@42]; } @catch (NSException *exception) { threw = [exception.name isEqual:NSInvalidArgumentException]; }
        check(threw, @"wrong equality receiver type preserves unrecognized-selector exception");
        check(method_getImplementation(class_getInstanceMethod([IntercomNexusConfiguration class], @selector(hash))) == method_getImplementation(class_getInstanceMethod([NSObject class], @selector(hash))), @"original class does not override NSObject hash");
        NSDictionary *receipt = @{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"urls":urls, @"scope":@"Nexus configuration Foundation model only; no WebSocket session"};
        NSData *json = [NSJSONSerialization dataWithJSONObject:receipt options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
