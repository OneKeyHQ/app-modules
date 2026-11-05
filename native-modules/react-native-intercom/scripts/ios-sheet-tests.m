#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "ICMHTTPClient.h"
#import "ICMDeviceData.h"
#import "ICMSheet.h"
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
@interface FixtureSheetSubclass : ICMSheet
@end
@implementation FixtureSheetSubclass
@end
static void waitFor(BOOL (^finished)(void)) {
    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:3];
    while (!finished()) {
        if (deadline.timeIntervalSinceNow < 0) { fprintf(stderr, "FAIL: main callback timeout\n"); exit(1); }
        [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
    }
}
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new]; events = [NSMutableArray new];
        ICMSafeNetworkModel *base = [ICMSafeNetworkModel new];
        [base setValue:@1 forUndefinedKey:@"unknown"]; [base setNilValueForKey:@"scalar"];
        check(YES, @"original base class KVC nil and undefined handlers return without exception");
        ICMSheet *empty = [ICMSheet instanceFromDictionary:(id)@[]];
        check(empty && empty.requestBody == nil && empty.hash == 0, @"factory wrong type produces genuine empty sheet");
        check([empty isEqual:[ICMSheet new]] && ![empty isEqual:nil] && [empty isEqual:empty], @"empty sheets equality preserves nil bodies but rejects nil object");
        check(![empty isEqual:[FixtureSheetSubclass new]] && [empty isEqualToSheet:[FixtureSheetSubclass new]], @"isEqual requires exact class while isEqualToSheet does not");
        check([[FixtureSheetSubclass instanceFromDictionary:nil] class] == ICMSheet.class, @"subclass factory still allocates original ICMSheet class");
        NSMutableDictionary *body = [@{@"synthetic":@1} mutableCopy];
        ICMSheet *sheet = [ICMSheet instanceFromDictionary:@{@"sheet_request_body":body, @"ignored":@1}];
        check(sheet.requestBody == body, @"dictionary parser retains body without copy");
        body[@"later"] = @2;
        check([sheet.requestBody[@"later"] isEqual:@2] && sheet.hash == body.hash, @"mutable alias remains observable and hash derives from body");
        [sheet setValue:@{} forKey:@"requestBody"]; [sheet setValue:[NSNull null] forKey:@"sheet_request_body"]; [sheet setAttributesFromDictionary:(id)@"invalid"];
        check(sheet.requestBody == body, @"camel field wrong type and invalid dictionary preserve previous body");
        check([sheet isEqual:[ICMSheet instanceFromDictionary:@{@"sheet_request_body":[body copy]}]], @"sheet equality compares dictionary value after pointer mismatch");
        BOOL threw = NO; @try { [sheet isEqualToSheet:(id)[NSObject new]]; } @catch (NSException *exception) { threw = [exception.name isEqual:NSInvalidArgumentException]; }
        check(threw && ![sheet isEqual:[NSObject new]], @"typed equality preserves missing-selector exception while generic equality rejects class");
        FixtureDefaults *defaults = [FixtureDefaults new]; defaults.value = @"native-device";
        Method method = class_getClassMethod(NSUserDefaults.class, @selector(standardUserDefaults));
        IMP previous = method_setImplementation(method, imp_implementationWithBlock(^id(id cls) { (void)cls; return defaults; }));
        FixtureEngine *engine = [FixtureEngine new];
        ICMIdentityStore *store = [ICMIdentityStore new]; fixtureStore = store;
        store.appIdentity = [FixtureIdentity new]; store.appIdentity.engine = engine; store.appIdentity.exists = YES;
        store.userIdentity = [FixtureIdentity new]; store.userIdentity.exists = YES;
        NSDictionary *identity = @{@"user_id":@"native-user", @"sheet_values":@"old", @"uri":@"old"}; store.userIdentity.dictionary = identity;
        __block ICMSheet *received; __block NSUInteger calls = 0;
        [ICMHTTPClient fetchMessengerSheetData:@{@"device_identifier":@"override", @"user_id":@"override-user"} success:^(ICMSheet *value) { received = value; calls++; } failure:nil];
        check([engine.path isEqual:@"sheets/open"] && [engine.parameters[@"device_identifier"] isEqual:@"override"] && [engine.parameters[@"user_id"] isEqual:@"override-user"] && [identity[@"user_id"] isEqual:@"native-user"], @"fetch caller entries overwrite identity and device after copying original dictionary");
        engine.success(nil, @{@"sheet_request_body":body});
        check(calls == 1 && received.requestBody == body && NSThread.isMainThread, @"fetch main success synchronously parses real sheet retaining original body");
        [ICMHTTPClient fetchMessengerSheetData:nil success:nil failure:nil]; engine.success(nil, @{});
        check([engine.parameters[@"device_identifier"] isEqual:@"native-device"] && calls == 1, @"fetch nil parameters keeps native payload and nil success safely returns");
        __block BOOL mainCallback = NO;
        [ICMHTTPClient fetchMessengerSheetData:@{} success:^(ICMSheet *value) { received = value; mainCallback = NSThread.isMainThread; calls++; } failure:nil];
        dispatch_semaphore_t returned = dispatch_semaphore_create(0);
        dispatch_async(dispatch_get_global_queue(0, 0), ^{ engine.success(nil, @{@"sheet_request_body":body}); dispatch_semaphore_signal(returned); });
        check(dispatch_semaphore_wait(returned, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC)) == 0 && calls == 1, @"fetch background engine callback returns before main delivery");
        waitFor(^BOOL { return calls == 2; });
        check(mainCallback && received.requestBody == body, @"fetch background success dispatches parsed sheet to main");
        NSDictionary *values = @{@"fixture":@1};
        [ICMHTTPClient submitMessengerSheet:values uri:@"https://synthetic.invalid/raw" success:^{ calls++; } failure:nil];
        check([engine.path isEqual:@"sheets/submit"] && engine.parameters[@"sheet_values"] == values && [engine.parameters[@"uri"] isEqual:@"https://synthetic.invalid/raw"], @"submit retains raw sheet values and uri in native identity payload");
        engine.success(nil, @{@"discarded":@1});
        check(calls == 3, @"submit main success invokes no-argument callback synchronously");
        [ICMHTTPClient submitMessengerSheet:nil uri:nil success:nil failure:nil]; engine.success(nil, nil);
        check(!engine.parameters[@"sheet_values"] && !engine.parameters[@"uri"], @"nil submit fields remove old identity entries and nil callback is accepted");
        mainCallback = NO;
        [ICMHTTPClient submitMessengerSheet:values uri:nil success:^{ calls++; mainCallback = NSThread.isMainThread; } failure:nil];
        dispatch_async(dispatch_get_global_queue(0, 0), ^{ engine.success(nil, nil); dispatch_semaphore_signal(returned); });
        check(dispatch_semaphore_wait(returned, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC)) == 0 && calls == 3, @"submit background callback returns before main delivery");
        waitFor(^BOOL { return calls == 4; }); check(mainCallback, @"submit success is dispatched to main");
        __block NSError *failure; NSError *raw = [NSError errorWithDomain:@"fixture" code:-1009 userInfo:nil];
        [ICMHTTPClient fetchMessengerSheetData:nil success:nil failure:^(NSError *error) { failure = error; }]; engine.failure(nil, raw);
        check(failure == raw, @"fetch failure invokes actual HTTP error helper");
        NSUInteger before = engine.calls; store.userIdentity.exists = NO;
        [ICMHTTPClient submitMessengerSheet:nil uri:nil success:nil failure:^(NSError *error) { failure = error; }];
        check(engine.calls == before && failure.code == 1001 && [failure.localizedDescription containsString:@"submit messenger sheet"], @"submit missing identity denies request with original context");
        method_setImplementation(method, previous);
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"scope":@"Real Sheet model and HTTP request subset with diagnostic identity/engine/defaults; no network or product closure"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
