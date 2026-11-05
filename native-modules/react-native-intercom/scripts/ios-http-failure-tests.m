#import <Foundation/Foundation.h>
#import "ICMHTTPClient.h"
@interface FixtureAdmissionIdentity : NSObject
@property (nonatomic) BOOL exists;
@property (nonatomic) NSUInteger reads;
- (BOOL)identityExists;
@end
@implementation FixtureAdmissionIdentity
- (BOOL)identityExists { self.reads++; return self.exists; }
@end
static NSUInteger singletonReads;
static id currentStore;
@interface ICMIdentityStore : NSObject
@property (nonatomic, strong) FixtureAdmissionIdentity *appIdentity;
@property (nonatomic, strong) FixtureAdmissionIdentity *userIdentity;
+ (id)currentIdentity;
@end
@implementation ICMIdentityStore
+ (id)currentIdentity { singletonReads++; return currentStore; }
@end
static NSMutableArray *passed;
static void check(BOOL value, NSString *name) {
    if (!value) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
static void waitFor(BOOL (^finished)(void)) {
    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:3];
    while (!finished()) {
        if (deadline.timeIntervalSinceNow < 0) { fprintf(stderr, "FAIL: main callback timeout\n"); exit(1); }
        [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
    }
}
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        NSError *raw = [NSError errorWithDomain:@"fixture.transport" code:-1009 userInfo:@{NSLocalizedDescriptionKey:@"synthetic offline"}];
        __block NSError *received;
        __block NSUInteger calls = 0;
        [ICMHTTPClient failWithNetworkError:raw whenTryingTo:@"fixture" failureBlock:^(NSError *error) { received = error; calls++; }];
        check(received == raw && calls == 1, @"main network failure synchronously preserves original error when HTTP status absent");
        NSError *zero = [NSError errorWithDomain:@"fixture.transport" code:400 userInfo:@{@"kIntercomHTTPStatusCode":@0}];
        [ICMHTTPClient failWithNetworkError:zero whenTryingTo:@"fixture" failureBlock:^(NSError *error) { received = error; }];
        check(received == zero, @"zero HTTP status preserves original error even with nonzero NSError code");
        NSError *stringStatus = [NSError errorWithDomain:@"fixture.transport" code:-1 userInfo:@{NSLocalizedDescriptionKey:@"fixture description", @"kIntercomHTTPStatusCode":@"401suffix", @"kIntercomErrorCode":[NSNull null], @"discarded":@1}];
        [ICMHTTPClient failWithNetworkError:stringStatus whenTryingTo:@"fixture" failureBlock:^(NSError *error) { received = error; }];
        check(received.code == 401 && [received.domain isEqual:@"IntercomSDKErrorDomain"] && [received.localizedDescription isEqual:@"ERROR - [HTTP 401] - fixture description"], @"string integerValue HTTP status drives original generated HTTP error");
        check(received.userInfo[@"kIntercomErrorCode"] == [NSNull null] && received.userInfo[@"discarded"] == nil && received.userInfo[NSUnderlyingErrorKey] == nil, @"HTTP conversion retains server-code sentinel and discards unrelated original info");
        NSError *boolStatus = [NSError errorWithDomain:@"fixture" code:0 userInfo:@{@"kIntercomHTTPStatusCode":@YES}];
        [ICMHTTPClient failWithNetworkError:boolStatus whenTryingTo:@"fixture" failureBlock:^(NSError *error) { received = error; }];
        check(received.code == 1, @"NSNumber boolean status remains numeric without added validation");
        NSError *serverCode = [NSError errorWithDomain:@"fixture" code:0 userInfo:@{@"kIntercomErrorCode":@42}];
        NSError *converted = [ICMHTTPClient httpErrorWithCode:503 fromNetworkError:serverCode];
        check(converted.code == 503 && [converted.userInfo[@"kIntercomErrorCode"] isEqual:@42], @"server code preserves original non-string values");
        BOOL threw = NO;
        @try { [ICMHTTPClient failWithNetworkError:[NSError errorWithDomain:@"fixture" code:0 userInfo:@{@"kIntercomHTTPStatusCode":[NSNull null]}] whenTryingTo:@"fixture" failureBlock:nil]; }
        @catch (NSException *exception) { threw = [exception.name isEqual:NSInvalidArgumentException]; }
        check(threw, @"NSNull HTTP status preserves original integerValue exception even without callback");
        [ICMHTTPClient failWithNetworkError:nil whenTryingTo:nil failureBlock:^(NSError *error) { received = error; calls++; }];
        check(received == nil && calls == 2, @"nil network error preserves nil callback argument");
        [ICMHTTPClient failWithErrorMessage:nil recoverySuggestion:nil errorCode:123 underlyingError:nil failureBlock:^(NSError *error) { received = error; calls++; }];
        check(received.code == 123 && [received.domain isEqual:@"IntercomSDKErrorDomain"] && [received.userInfo[NSLocalizedDescriptionKey] isEqual:@""] && [received.userInfo[NSLocalizedRecoverySuggestionErrorKey] isEqual:@""] && received.userInfo[NSUnderlyingErrorKey] == [NSNull null] && calls == 3, @"explicit error uses empty strings and NSNull underlying with synchronous main callback");
        [ICMHTTPClient failWithErrorMessage:@"message" recoverySuggestion:@"suggestion" errorCode:321 underlyingError:raw failureBlock:^(NSError *error) { received = error; }];
        check([received.localizedDescription isEqual:@"message"] && [received.localizedRecoverySuggestion isEqual:@"suggestion"] && received.userInfo[NSUnderlyingErrorKey] == raw, @"explicit error retains original underlying instance and suggestion");
        dispatch_semaphore_t returned = dispatch_semaphore_create(0);
        __block BOOL callbackRan = NO, mainThread = NO;
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            [ICMHTTPClient failWithNetworkError:raw whenTryingTo:@"background fixture" failureBlock:^(NSError *error) { received = error; mainThread = [NSThread isMainThread]; callbackRan = YES; }];
            dispatch_semaphore_signal(returned);
        });
        check(dispatch_semaphore_wait(returned, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC)) == 0 && !callbackRan, @"background network failure returns without blocking on main callback");
        waitFor(^BOOL { return callbackRan; });
        check(mainThread && received == raw, @"background network failure dispatches original error to main");
        ICMIdentityStore *store = [ICMIdentityStore new]; currentStore = store;
        store.appIdentity = [FixtureAdmissionIdentity new]; store.userIdentity = [FixtureAdmissionIdentity new]; singletonReads = 0;
        calls = 0;
        BOOL bail = [ICMHTTPClient bailIfMissingIdentityWhenTryingTo:@"fixture" withFailureBlock:^(NSError *error) { received = error; calls++; }];
        check(bail && received.code == 1000 && calls == 1 && singletonReads == 1 && store.appIdentity.reads == 1 && store.userIdentity.reads == 0, @"missing app identity fails first without reading user identity");
        store.appIdentity.exists = YES; singletonReads = 0;
        bail = [ICMHTTPClient bailIfMissingIdentityWhenTryingTo:@"fixture" withFailureBlock:^(NSError *error) { received = error; calls++; }];
        check(bail && received.code == 1001 && calls == 2 && singletonReads == 2 && [received.localizedDescription containsString:@"no user has been registered yet"], @"missing user identity fails after separate real singleton read");
        store.userIdentity.exists = YES;
        bail = [ICMHTTPClient bailIfMissingIdentityWhenTryingTo:@"fixture" withFailureBlock:^(NSError *error) { (void)error; calls++; }];
        check(!bail && calls == 2, @"existing app and user identities do not emit failure");
        currentStore = nil;
        check([ICMHTTPClient bailIfMissingIdentityWhenTryingTo:nil withFailureBlock:nil], @"nil identity singleton preserves missing-app bail without callback");
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"scope":@"Four HTTPClient methods with diagnostic identity conditions; real product session unlinked; no network"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
