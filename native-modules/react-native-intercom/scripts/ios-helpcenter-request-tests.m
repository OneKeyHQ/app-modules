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
        FixtureDefaults *defaults = [FixtureDefaults new];
        __block NSUInteger defaultsCalls = 0, uuidCalls = 0;
        Method defaultsMethod = class_getClassMethod(NSUserDefaults.class, @selector(standardUserDefaults));
        IMP oldDefaults = method_setImplementation(defaultsMethod, imp_implementationWithBlock(^id(id cls) { (void)cls; defaultsCalls++; return defaults; }));
        NSUUID *fixedUUID = [[NSUUID alloc] initWithUUIDString:@"11111111-2222-3333-4444-555555555555"];
        Method uuidMethod = class_getClassMethod(NSUUID.class, @selector(UUID));
        IMP oldUUID = method_setImplementation(uuidMethod, imp_implementationWithBlock(^id(id cls) { (void)cls; uuidCalls++; return fixedUUID; }));
        NSString *generated = [ICMDeviceData deviceIdentifier];
        check([generated isEqual:fixedUUID.UUIDString] && defaults.reads == 1 && defaults.writes == 1 && defaultsCalls == 1 && uuidCalls == 1, @"nil identifier generates UUID and persists with one defaults lookup");
        check([ICMDeviceData deviceIdentifier] == generated && defaults.writes == 1 && uuidCalls == 1 && defaultsCalls == 2, @"subsequent read returns persisted instance without regeneration");
        defaults.value = @"";
        check([[ICMDeviceData deviceIdentifier] isEqual:@""] && defaults.writes == 1 && uuidCalls == 1, @"empty stored string is preserved");
        defaults.value = @"synthetic-device";
        FixtureEngine *engine = [FixtureEngine new];
        ICMIdentityStore *store = [ICMIdentityStore new]; fixtureStore = store;
        store.appIdentity = [FixtureIdentity new]; store.appIdentity.engine = engine; store.appIdentity.exists = YES;
        store.userIdentity = [FixtureIdentity new]; store.userIdentity.exists = YES;
        NSMutableDictionary *identity = [@{@"user_id":@"synthetic-user", @"device_identifier":@"old", @"phrase":@"old phrase", @"article_source":@"old source"} mutableCopy];
        store.userIdentity.dictionary = identity;
        __block id received; __block NSUInteger callbacks = 0;
        void (^success)(id) = ^(id value) { received = value; callbacks++; };
        [events removeAllObjects];
        [ICMHTTPClient getHelpCenterCollections:success error:nil];
        check([engine.path isEqual:@"help_center/collections"] && [engine.parameters[@"device_identifier"] isEqual:@"synthetic-device"] && [engine.parameters[@"user_id"] isEqual:@"synthetic-user"], @"collections POST contains actual identity dictionary with device overwrite");
        check([identity[@"device_identifier"] isEqual:@"old"] && engine.parameters != identity, @"request mutable copy leaves original identity dictionary untouched");
        check([events isEqual:@[@"current", @"app.exists", @"current", @"user.exists", @"current", @"dictionary", @"intercom-deviceIdentifier", @"current", @"POST"]], @"admission dictionary device and second engine singleton ordering matches original");
        NSMutableDictionary *response = [@{@"collections":@[]} mutableCopy]; engine.success(nil, response);
        check(received == response && callbacks == 1, @"collections success directly delivers original response instance synchronously");
        [ICMHTTPClient getHelpCenterCollection:@"a/b ?&中" success:success error:nil];
        check([engine.path isEqual:@"help_center/collections/a/b ?&中"], @"collection ID is literal interpolated without URL encoding");
        [ICMHTTPClient getHelpCenterCollection:nil success:success error:nil];
        check([engine.path isEqual:@"help_center/collections/(null)"], @"nil collection ID uses original stringWithFormat representation");
        [ICMHTTPClient getHelpCenterSearchResults:@"new phrase" source:@"new source" success:success error:nil];
        check([engine.path isEqual:@"help_center/search"] && [engine.parameters[@"phrase"] isEqual:@"new phrase"] && [engine.parameters[@"article_source"] isEqual:@"new source"], @"search POST overwrites phrase and source fields");
        [ICMHTTPClient getHelpCenterSearchResults:nil source:nil success:success error:nil];
        check(!engine.parameters[@"phrase"] && !engine.parameters[@"article_source"] && identity[@"phrase"] && identity[@"article_source"], @"nil search inputs remove copied fields without mutating original");
        dispatch_semaphore_t background = dispatch_semaphore_create(0);
        __block BOOL offMain = NO;
        [ICMHTTPClient getHelpCenterCollections:^(id value) { (void)value; offMain = !NSThread.isMainThread; dispatch_semaphore_signal(background); } error:nil];
        dispatch_async(dispatch_get_global_queue(0, 0), ^{ engine.success(nil, response); });
        check(dispatch_semaphore_wait(background, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC)) == 0 && offMain, @"success remains on engine background callback thread");
        NSError *raw = [NSError errorWithDomain:@"fixture" code:-1009 userInfo:nil]; __block NSError *failure;
        [ICMHTTPClient getHelpCenterCollections:success error:^(NSError *error) { failure = error; }]; engine.failure(nil, raw);
        check(failure == raw, @"network failure traverses real helper preserving original NSError");
        NSUInteger before = engine.calls, beforeReads = defaults.reads;
        store.appIdentity.exists = NO;
        [ICMHTTPClient getHelpCenterCollections:success error:^(NSError *error) { failure = error; }];
        check(failure.code == 1000 && engine.calls == before && defaults.reads == beforeReads, @"missing app identity bails before device access or request");
        store.appIdentity.exists = YES; store.userIdentity.exists = NO;
        [ICMHTTPClient getHelpCenterSearchResults:@"term" source:nil success:success error:^(NSError *error) { failure = error; }];
        check(failure.code == 1001 && [failure.localizedDescription containsString:@"get help center collections for search term"] && engine.calls == before, @"search missing user preserves original action and failure code");
        store.userIdentity.exists = YES; store.userIdentity.dictionary = nil;
        [ICMHTTPClient getHelpCenterCollections:success error:nil];
        check(engine.parameters == nil, @"nil user dictionary remains nil without fabricated payload");
        method_setImplementation(defaultsMethod, oldDefaults); method_setImplementation(uuidMethod, oldUUID);
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"scope":@"Actual request mapping and device identifier source; diagnostic identity/engine/defaults/UUID; no network or default preferences"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
