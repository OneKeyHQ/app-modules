#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "ICMHTTPClient.h"
#import "ICMDeviceData.h"
static NSMutableArray *passed, *events;
static void check(BOOL value, NSString *name) {
    if (!value) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
@interface FixtureDefaults : NSObject
@property (nonatomic, strong) NSString *value;
@property (nonatomic) NSUInteger reads, writes;
@end
@implementation FixtureDefaults
- (NSString *)stringForKey:(NSString *)key { [events addObject:key]; self.reads++; return self.value; }
- (void)setObject:(id)value forKey:(NSString *)key { [events addObject:key]; self.writes++; self.value = value; }
@end
@interface FixtureEngine : NSObject
@property (nonatomic, strong) NSString *path;
@property (nonatomic, strong) NSDictionary *parameters;
@property (nonatomic, copy) void (^success)(id, id);
@property (nonatomic, copy) void (^failure)(id, NSError *);
@property (nonatomic) NSUInteger calls;
@end
@implementation FixtureEngine
- (id)POST:(NSString *)path parameters:(NSDictionary *)parameters success:(void (^)(id, id))success failure:(void (^)(id, NSError *))failure {
    [events addObject:@"POST"]; self.calls++; self.path = path; self.parameters = parameters; self.success = success; self.failure = failure; return nil;
}
@end
@interface FixtureIdentity : NSObject
@property (nonatomic) BOOL exists;
@property (nonatomic, strong) NSDictionary *dictionary;
@property (nonatomic, strong) NSString *email, *userId, *anonymousId;
@property (nonatomic, strong) FixtureEngine *engine;
@end
@implementation FixtureIdentity
- (BOOL)identityExists { [events addObject:self.engine ? @"app.exists" : @"user.exists"]; return self.exists; }
- (NSDictionary *)dictionaryRepresentation { [events addObject:@"dictionary"]; return self.dictionary; }
@end
static id fixtureStore;
@interface ICMIdentityStore : NSObject
@property (nonatomic, strong) FixtureIdentity *appIdentity, *userIdentity;
@end
@implementation ICMIdentityStore
+ (id)currentIdentity { [events addObject:@"current"]; return fixtureStore; }
@end
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new]; events = [NSMutableArray new];
        FixtureDefaults *defaults = [FixtureDefaults new]; defaults.value = @"synthetic-device";
        Method method = class_getClassMethod(NSUserDefaults.class, @selector(standardUserDefaults));
        IMP previous = method_setImplementation(method, imp_implementationWithBlock(^id(id cls) { (void)cls; return defaults; }));
        FixtureEngine *engine = [FixtureEngine new]; ICMIdentityStore *store = [ICMIdentityStore new]; fixtureStore = store;
        store.appIdentity = [FixtureIdentity new]; store.appIdentity.engine = engine; store.appIdentity.exists = YES;
        FixtureIdentity *user = [FixtureIdentity new]; store.userIdentity = user; user.exists = YES;
        user.email = @"synthetic@example.invalid"; user.userId = @"synthetic-user"; user.anonymousId = @"synthetic-anon";
        NSDictionary *hmac = @{@"synthetic":@1}; NSArray *jwt = @[@"synthetic"]; user.dictionary = @{@"app_id":@123, @"hmac":hmac, @"intercom_user_jwt":jwt, @"ignored":@1, @"auth_tokens":@"old"};
        NSMutableDictionary *parameters = [ICMHTTPClient buildMultipartParametersForCurrentIdentity];
        check([parameters[@"email"] isEqual:user.email] && [parameters[@"user_id"] isEqual:user.userId] && !parameters[@"anonymous_id"], @"multipart includes nonempty identified fields and suppresses anonymous fallback");
        check([parameters[@"device_identifier"] isEqual:defaults.value] && [parameters[@"app_id"] isEqual:@"123"] && !parameters[@"ignored"], @"multipart includes device and stringifies only original app ID field");
        check(parameters[@"hmac"] == hmac && parameters[@"intercom_user_jwt"] == jwt, @"multipart preserves nonstring nonnull secure values without added validation");
        user.email = @""; user.userId = nil;
        check(![ICMHTTPClient buildMultipartParametersForCurrentIdentity][@"anonymous_id"], @"nonnull empty email suppresses anonymous fallback despite omitted email field");
        user.email = nil; user.userId = @"";
        check(![ICMHTTPClient buildMultipartParametersForCurrentIdentity][@"anonymous_id"], @"nonnull empty user ID also suppresses anonymous fallback");
        user.userId = nil;
        check([[ICMHTTPClient buildMultipartParametersForCurrentIdentity][@"anonymous_id"] isEqual:user.anonymousId], @"anonymous fallback applies only when both identified pointers are nil");
        user.anonymousId = @""; user.dictionary = @{@"hmac":[NSNull null], @"intercom_user_jwt":[NSNull null], @"app_id":[NSNull null]};
        parameters = [ICMHTTPClient buildMultipartParametersForCurrentIdentity];
        check(!parameters[@"anonymous_id"] && !parameters[@"hmac"] && !parameters[@"intercom_user_jwt"] && [parameters[@"app_id"] isEqual:[[NSNull null] description]], @"empty anonymous and null secure values omitted while null app ID is described");
        user.dictionary = nil;
        check([[ICMHTTPClient buildMultipartParametersForCurrentIdentity] isEqual:@{@"device_identifier":@"synthetic-device"}], @"missing user representation still yields genuine device field");
        fixtureStore = nil;
        check([[ICMHTTPClient buildMultipartParametersForCurrentIdentity] isEqual:@{@"device_identifier":@"synthetic-device"}], @"multipart builder performs no identity admission");
        fixtureStore = store; user.dictionary = @{@"user_id":@"native", @"auth_tokens":@"old"};
        NSDictionary *tokens = @{@"synthetic-token-name":@"synthetic-token-value"}; __block NSUInteger calls = 0;
        [ICMHTTPClient setAuthTokens:tokens success:^{ calls++; } failure:nil];
        check([engine.path isEqual:@"user_auth_tokens"] && engine.parameters[@"auth_tokens"] == tokens && [user.dictionary[@"auth_tokens"] isEqual:@"old"] && [engine.parameters[@"user_id"] isEqual:@"native"], @"auth mapping copies identity and retains raw token dictionary");
        engine.success(nil, @{}); check(calls == 1, @"auth success synchronously invokes no-argument callback on main");
        [ICMHTTPClient setAuthTokens:nil success:^{ calls++; } failure:nil];
        check(!engine.parameters[@"auth_tokens"], @"nil auth tokens remove preexisting copied field");
        dispatch_semaphore_t returned = dispatch_semaphore_create(0); __block BOOL main = NO;
        [ICMHTTPClient setAuthTokens:tokens success:^{ main = NSThread.isMainThread; calls++; } failure:nil];
        dispatch_async(dispatch_get_global_queue(0, 0), ^{ engine.success(nil, @{}); dispatch_semaphore_signal(returned); });
        check(dispatch_semaphore_wait(returned, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC)) == 0 && calls == 1, @"background auth success returns before main delivery");
        NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:3];
        while (calls != 2 && deadline.timeIntervalSinceNow > 0) [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
        check(calls == 2 && main, @"auth success is dispatched to main from background");
        __block NSError *failure;
        [ICMHTTPClient setAuthTokens:tokens success:^{ calls++; } failure:^(NSError *error) { failure = error; }];
        NSError *raw = [NSError errorWithDomain:@"fixture" code:-1009 userInfo:@{NSLocalizedDescriptionKey:@"synthetic offline"}]; engine.failure(nil, raw);
        check(failure != raw && failure.code == 0 && [failure.domain isEqual:@"IntercomSDKErrorDomain"] && [failure.localizedDescription isEqual:@"ERROR - [HTTP 0] - synthetic offline"], @"auth failure without HTTP status still wraps transport error as original code-zero HTTP error");
        raw = [NSError errorWithDomain:@"fixture" code:-1 userInfo:@{NSLocalizedDescriptionKey:@"synthetic denied", @"kIntercomHTTPStatusCode":@"401suffix", @"kIntercomErrorCode":@42}]; engine.failure(nil, raw);
        check(failure.code == 401 && [failure.userInfo[@"kIntercomErrorCode"] isEqual:@42] && [failure.localizedDescription isEqual:@"ERROR - [HTTP 401] - synthetic denied"], @"auth failure converts string HTTP status once and preserves server code");
        NSUInteger before = engine.calls; user.exists = NO;
        [ICMHTTPClient setAuthTokens:tokens success:^{ calls++; } failure:^(NSError *error) { failure = error; }];
        check(engine.calls == before && failure.code == 1001 && [failure.localizedDescription containsString:@"set user auth tokens"], @"auth request retains real identity denial and original context");
        method_setImplementation(method, previous);
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"scope":@"Actual token request mapping and multipart fields with synthetic diagnostic identity/engine/defaults; no authentication or network"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
