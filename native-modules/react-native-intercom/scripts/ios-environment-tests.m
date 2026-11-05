#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "ICMAppEnvironmentChecker.h"
static NSMutableArray *passed;
static BOOL simulator, embedded, sandbox;
static NSUInteger provisionEnvironment, detectionCalls;
static void check(BOOL value, NSString *name) {
    if (!value) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
@interface FixtureEnvironment : ICMAppEnvironmentChecker
@end
@implementation FixtureEnvironment
+ (BOOL)isRunningOnSimulator { return simulator; }
+ (BOOL)hasEmbeddedMobileProvision { return embedded; }
+ (BOOL)isAppStoreReceiptSandbox { return sandbox; }
+ (NSUInteger)environmentFromEmbeddedMobileProvision { return provisionEnvironment; }
+ (NSUInteger)findCurrentAppEnvironment { detectionCalls++; return [super findCurrentAppEnvironment]; }
@end
@interface FixtureBundle : NSObject
@property (nonatomic, strong) NSString *path;
@property (nonatomic, strong) NSURL *receipt;
@end
@implementation FixtureBundle
- (NSString *)pathForResource:(NSString *)resource ofType:(NSString *)type {
    if (![resource isEqual:@"embedded"] || ![type isEqual:@"mobileprovision"]) abort();
    return self.path;
}
- (NSURL *)appStoreReceiptURL { return self.receipt; }
@end
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        NSArray *names = @[@"appstore", @"testflight", @"enterprise", @"adhoc", @"development", @"simulator"];
        for (NSUInteger index = 0; index < names.count; index++) check([[ICMAppEnvironmentChecker stringForAppEnvironment:index] isEqual:names[index]], [NSString stringWithFormat:@"exact environment name %lu", (unsigned long)index]);
        check([[ICMAppEnvironmentChecker stringForAppEnvironment:NSUIntegerMax] isEqual:@"unknown"] && [[ICMAppEnvironmentChecker stringForAppEnvironment:6] isEqual:@"unknown"], @"unsigned out-of-range environment names are unknown");
        for (NSNumber *value in @[@0,@1,@2,@3,@4,@5,@6,@99,@(NSUIntegerMax)]) {
            NSUInteger environment = value.unsignedIntegerValue; BOOL development = environment == 4 || environment == 5;
            check([ICMAppEnvironmentChecker isDevelopmentEnvironment:environment] == development && [[ICMAppEnvironmentChecker stringForDeviceTokenEnvironment:environment] isEqual:development ? @"development" : @"production"], [NSString stringWithFormat:@"exact development and token classification %@", value]);
        }
        simulator = YES; embedded = YES; sandbox = YES; provisionEnvironment = 2;
        check([FixtureEnvironment findCurrentAppEnvironment] == 5, @"simulator takes precedence over provision and receipt");
        simulator = NO;
        check([FixtureEnvironment findCurrentAppEnvironment] == 2, @"provision result takes precedence over receipt");
        embedded = NO;
        check([FixtureEnvironment findCurrentAppEnvironment] == 1, @"sandbox receipt maps directly to environment one");
        sandbox = NO;
        check([FixtureEnvironment findCurrentAppEnvironment] == 0, @"non-sandbox receipt maps to environment zero");
        FixtureBundle *bundle = [FixtureBundle new];
        Method method = class_getClassMethod(NSBundle.class, @selector(mainBundle));
        IMP previous = method_setImplementation(method, imp_implementationWithBlock(^id(id cls) { (void)cls; return bundle; }));
        NSString *root = @(getenv("ICM_TEST_DIRECTORY"));
        [[NSFileManager defaultManager] createDirectoryAtPath:root withIntermediateDirectories:YES attributes:nil error:nil];
        bundle.path = [root stringByAppendingPathComponent:@"synthetic.mobileprovision"];
        check([ICMAppEnvironmentChecker hasEmbeddedMobileProvision], @"provision existence checks path nonnil without file existence check");
        NSArray *fixtures = @[@[@"signed-prefix <key> get-task-allow </key> \n <true /> trailing",@4], @[@"<key>ProvisionsAllDevices</key><true/>",@2], @[@"<key>ProvisionedDevices</key> <array>ignored",@3], @[@"<key>get-task-allow</key><false/><key>ProvisionedDevices</key><array>",@3], @[@"<key>ProvisionsAllDevices</key><true/><key>get-task-allow</key><true/>",@4], @[@"<key>ProvisionedDevices</key><array><key>ProvisionsAllDevices</key><true/>",@2], @[@"<key>GET-TASK-ALLOW</key><true/>",@99], @[@"<key>get-task-allow</key><true></true>",@99], @[@"",@99]];
        NSUInteger index = 0;
        for (NSArray *fixture in fixtures) {
            [(NSString *)fixture[0] writeToFile:bundle.path atomically:YES encoding:NSUTF8StringEncoding error:nil];
            check([ICMAppEnvironmentChecker environmentFromEmbeddedMobileProvision] == [fixture[1] unsignedIntegerValue], [NSString stringWithFormat:@"literal provision scan and priority fixture %lu", (unsigned long)index++]);
        }
        bundle.path = [root stringByAppendingPathComponent:@"missing.synthetic"];
        check([ICMAppEnvironmentChecker environmentFromEmbeddedMobileProvision] == 99, @"unreadable provision returns original unknown numeric value 99");
        bundle.path = nil;
        check(![ICMAppEnvironmentChecker hasEmbeddedMobileProvision], @"missing bundle resource has no embedded provision");
        bundle.receipt = [NSURL URLWithString:@"file:///synthetic/sandboxReceipt"];
        check([ICMAppEnvironmentChecker isAppStoreReceiptSandbox], @"receipt last path exact sandboxReceipt enables sandbox");
        bundle.receipt = [NSURL URLWithString:@"file:///synthetic/SandboxReceipt"];
        check(![ICMAppEnvironmentChecker isAppStoreReceiptSandbox], @"receipt comparison is case sensitive");
        bundle.receipt = nil;
        check(![ICMAppEnvironmentChecker isAppStoreReceiptSandbox], @"nil receipt is not sandbox");
        simulator = NO; embedded = YES; provisionEnvironment = 3; NSUInteger before = detectionCalls;
        check([FixtureEnvironment appEnvironment] == 3 && detectionCalls == before + 1, @"cached environment uses first receiving subclass detector");
        provisionEnvironment = 4;
        check([ICMAppEnvironmentChecker appEnvironment] == 3 && [FixtureEnvironment appEnvironment] == 3 && detectionCalls == before + 1, @"single implementation cache is shared across receiving subclasses");
        method_setImplementation(method, previous);
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"scope":@"Actual environment detector with test-only bundle/provision/receipt and detector subclass; no real provisioning"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
