// Test-only identity singleton and directory injection; no product replacement.
#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "ICMAppInstallationFingerprint.h"
#import "ICMOreoShutdownState.h"
#import "ICMOreoErrorResponse.h"
#import "../ios/Identity/ICMUtils.h"

@interface FixtureApp : NSObject
@property (nonatomic, strong) NSString *appId;
@end
@implementation FixtureApp
@end
@interface FixtureStore : NSObject
@property (nonatomic, strong) FixtureApp *appIdentity;
@end
@implementation FixtureStore
@end
static FixtureStore *store;
@interface ICMIdentityStore : NSObject
+ (id)currentIdentity;
@end
@implementation ICMIdentityStore
+ (id)currentIdentity { return store; }
@end

NSArray<NSString *> *NSSearchPathForDirectoriesInDomains(NSSearchPathDirectory directory, NSSearchPathDomainMask mask, BOOL expand) {
    (void)mask; (void)expand;
    NSString *root = [NSProcessInfo processInfo].environment[@"ICM_TEST_DIRECTORY"];
    NSCAssert(root.length, @"Fixture directory required");
    return @[[root stringByAppendingPathComponent:[NSString stringWithFormat:@"search-%lu", (unsigned long)directory]]];
}
static NSDate *fixtureTime;
static NSDate *fixedTime(id cls, SEL selector) { (void)cls; (void)selector; return fixtureTime; }
static NSMutableArray *passed;
static void check(BOOL condition, NSString *name) {
    if (!condition) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        store = [FixtureStore new]; store.appIdentity = [FixtureApp new];
        NSMutableString *identifier = [NSMutableString stringWithString:@"synthetic-app"];
        store.appIdentity.appId = identifier;
        ICMAppInstallationFingerprint *fingerprint = [ICMAppInstallationFingerprint new];
        check(fingerprint.appId == identifier, @"fingerprint retains original app id despite copy property");
        [identifier appendString:@"-changed"];
        check([fingerprint.appId isEqual:@"synthetic-app-changed"], @"fingerprint observes retained mutable app id");
        check([fingerprint.intercomVersion isEqual:@"19.9.1"] && [[ICMUtils intercomAgent] isEqual:@"intercom-ios-sdk/19.9.1"], @"version and native agent match original");
        ICMAppInstallationFingerprint *same = [ICMAppInstallationFingerprint new];
        check([same isEqual:fingerprint] && same.hash == (same.appId.hash ^ same.appVersion.hash ^ same.intercomVersion.hash ^ same.bundleIdentifier.hash), @"fingerprint equality and XOR hash");
        store.appIdentity.appId = @"different";
        ICMAppInstallationFingerprint *different = [ICMAppInstallationFingerprint new];
        check(![different isEqual:fingerprint] && ![fingerprint isEqual:@"invalid"], @"fingerprint requires same class and fields");
        NSData *archive = [NSKeyedArchiver archivedDataWithRootObject:fingerprint requiringSecureCoding:YES error:nil];
        ICMAppInstallationFingerprint *decoded = [NSKeyedUnarchiver unarchivedObjectOfClass:[ICMAppInstallationFingerprint class] fromData:archive error:nil];
        check([decoded isEqual:fingerprint], @"fingerprint real secure archive round trip");
        fixtureTime = [NSDate dateWithTimeIntervalSince1970:1000];
        Method clockMethod = class_getClassMethod([ICMOreoShutdownState class], @selector(currentTime));
        IMP originalClock = method_setImplementation(clockMethod, (IMP)fixedTime);
        NSMutableString *reason = [NSMutableString stringWithString:@"fixture shutdown"];
        ICMOreoShutdownState *state = [[ICMOreoShutdownState alloc] initWithShutdownPeriod:60 shutdownReason:reason];
        check(state.shutdownReason == reason && state.endTime.timeIntervalSince1970 == 1060, @"shutdown retains reason and computes end time");
        check(![state canSendOreoNetworkRequestsWithFingerprint:different], @"matching fingerprint blocked before deadline");
        check([state canSendOreoNetworkRequestsWithFingerprint:fingerprint], @"changed fingerprint bypasses active shutdown");
        fixtureTime = state.endTime;
        check([state canSendOreoNetworkRequestsWithFingerprint:different], @"shutdown deadline is inclusive for resuming sends");
        check([state.shutdownError.domain isEqual:@"io.intercom.ios.error.network.shutdown"] && state.shutdownError.code == 0 && [state.shutdownError.userInfo[NSLocalizedDescriptionKey] isEqual:reason], @"shutdown error exact domain and reason");
        archive = [NSKeyedArchiver archivedDataWithRootObject:state requiringSecureCoding:YES error:nil];
        ICMOreoShutdownState *decodedState = [NSKeyedUnarchiver unarchivedObjectOfClass:[ICMOreoShutdownState class] fromData:archive error:nil];
        check([decodedState isEqual:state] && decodedState.hash == (decodedState.endTime.hash ^ decodedState.appFingerprint.hash ^ decodedState.shutdownReason.hash), @"shutdown secure archive and XOR hash");
        check([[ICMOreoShutdownState archivePath] hasSuffix:@"/PrivateDocuments/io.intercom.ios/shutdown.icm"], @"original shutdown archive filename");
        [ICMOreoShutdownState saveShutdownState:state];
        check([[ICMOreoShutdownState loadFromDisk] isEqual:state], @"shutdown actual atomic archive file round trip");
        [[NSFileManager defaultManager] removeItemAtPath:[ICMOreoShutdownState archivePath] error:nil];
        ICMOreoShutdownState *missing = [ICMOreoShutdownState loadFromDisk];
        check([missing.shutdownReason isEqual:@""] && [missing.endTime isEqual:fixtureTime], @"missing archive returns zero period state");
        NSError *network = [NSError errorWithDomain:@"fixture.transport" code:401 userInfo:@{NSLocalizedDescriptionKey:@"transport", NSLocalizedRecoverySuggestionErrorKey:@"retry"}];
        ICMOreoErrorResponse *response = [ICMOreoErrorResponse instanceFromDictionary:nil networkError:network];
        check([response.type isEqual:@""] && [response.code isEqual:@""] && [response.message isEqual:@""] && !response.oreoShutdownState, @"non-dictionary response uses empty fields");
        check([response.error.domain isEqual:@"io.intercom.ios.error.network"] && response.error.code == 401 && response.error.userInfo[NSUnderlyingErrorKey] == network && [response.error.userInfo[@"kIntercomHTTPStatusCode"] isEqual:@401] && [response.error.userInfo[NSLocalizedDescriptionKey] isEqual:@"transport"], @"network error wrapping and description fallback");
        response = [ICMOreoErrorResponse instanceFromDictionary:@{@"error":@{@"type":@"singular", @"message":@"message", @"code":@"code"}, @"errors":@[@{@"type":@"ignored"}]} networkError:network];
        check([response.type isEqual:@"singular"] && [response.message isEqual:@"message"] && [response.code isEqual:@"code"] && [response.error.userInfo[NSLocalizedDescriptionKey] isEqual:@"message"], @"singular error takes precedence and overrides description");
        response = [ICMOreoErrorResponse instanceFromDictionary:@{@"errors":@[@{@"type":@"a", @"message":@"first", @"code":@"first-code"}, @42, @{@"type":@"b", @"message":@"second", @"code":@"ignored-code"}]} networkError:nil];
        check([response.type isEqual:@"a - b"] && [response.message isEqual:@"first - second"] && [response.code isEqual:@"first-code"], @"array errors join string fields and keep first code");
        response = [ICMOreoErrorResponse instanceFromDictionary:@{@"errors":@[@42, @{@"code":@"not-first", @"message":@"m"}]} networkError:nil];
        check([response.code isEqual:@""] && [response.message isEqual:@"m"], @"array code is not searched beyond first item");
        response = [ICMOreoErrorResponse instanceFromDictionary:@{@"error":@{@"type":@42, @"message":[NSNull null], @"code":@4}} networkError:nil];
        check([response.type isEqual:@""] && [response.message isEqual:@""] && [response.code isEqual:@""] && response.error.userInfo[NSUnderlyingErrorKey] == [NSNull null] && response.error.userInfo[NSLocalizedRecoverySuggestionErrorKey] == [NSNull null], @"wrong types ignored and nil error fields use NSNull");
        response = [ICMOreoErrorResponse instanceFromDictionary:@{@"errors":@[@{@"type":@"prefix_messenger_shutdown_response_suffix", @"shutdown_period":@10}, @{@"shutdown_period":@(-5), @"message":@"negative"}]} networkError:network];
        check(response.oreoShutdownState.endTime.timeIntervalSince1970 == fixtureTime.timeIntervalSince1970 - 5 && [response.oreoShutdownState.shutdownReason isEqual:@"negative"], @"last numeric period wins and negative period remains valid");
        response = [ICMOreoErrorResponse instanceFromDictionary:@{@"error":@{@"type":@"messenger_shutdown_response", @"shutdown_period":@0}} networkError:nil];
        check(response.oreoShutdownState == nil, @"zero shutdown period omitted");
        response = [ICMOreoErrorResponse instanceFromDictionary:@{@"error":@{@"type":@"messenger_shutdown_response", @"shutdown_period":@YES}} networkError:nil];
        check(response.oreoShutdownState.endTime.timeIntervalSince1970 == fixtureTime.timeIntervalSince1970 + 1, @"NSNumber boolean period preserves original numeric treatment");
        check([ICMOreoErrorResponse errorWithErrorMesssage:nil networkError:nil].userInfo[NSLocalizedDescriptionKey] == [NSNull null], @"nil description has explicit NSNull sentinel");
        method_setImplementation(clockMethod, originalClock);
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"scope":@"Network models with fixture-only identity singleton; product session remains unlinked"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
