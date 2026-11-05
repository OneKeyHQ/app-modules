// Diagnostic session/identity/path injection only; every URL is intercepted.
#import <Foundation/Foundation.h>
#import "ICMOreoEngine.h"
#import "ICMOreoShutdownState.h"
#import "ICMURLSessionTaskOperation.h"
#import "ICMOreoUserJwtReplay.h"

@interface TransportFixtureApp : NSObject
@property (nonatomic, strong) NSString *appId;
@end
@implementation TransportFixtureApp
@end
@interface ICMIdentityStore : NSObject
@property (nonatomic, strong) TransportFixtureApp *appIdentity;
- (id)userIdentity;
+ (instancetype)currentIdentity;
@end
@implementation ICMIdentityStore
- (id)userIdentity { return nil; }
+ (instancetype)currentIdentity {
    static ICMIdentityStore *value;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ value = [self new]; value.appIdentity = [TransportFixtureApp new]; value.appIdentity.appId = @"synthetic-app"; });
    return value;
}
@end
NSArray<NSString *> *NSSearchPathForDirectoriesInDomains(NSSearchPathDirectory directory, NSSearchPathDomainMask mask, BOOL expand) {
    (void)mask; (void)expand;
    NSString *root = [NSProcessInfo processInfo].environment[@"ICM_TEST_DIRECTORY"];
    NSCAssert(root.length, @"Fixture directory required");
    return @[[root stringByAppendingPathComponent:[NSString stringWithFormat:@"search-%lu", (unsigned long)directory]]];
}
static NSUInteger requests;
static NSUInteger retryRequests;
@interface SyntheticProtocol : NSURLProtocol
@end
@implementation SyntheticProtocol
+ (BOOL)canInitWithRequest:(NSURLRequest *)request { (void)request; return YES; }
+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request { return request; }
- (void)startLoading {
    @synchronized ([SyntheticProtocol class]) { requests++; }
    NSString *path = self.request.URL.path;
    if ([path isEqual:@"/messenger/mobile/users"]) {
        retryRequests++;
        if (retryRequests == 1) {
            [self.client URLProtocol:self didFailWithError:[NSError errorWithDomain:@"fixture.transport" code:-1009 userInfo:nil]];
            return;
        }
    }
    if ([path isEqual:@"/offline"]) {
        [self.client URLProtocol:self didFailWithError:[NSError errorWithDomain:@"fixture.transport" code:-1009 userInfo:@{NSLocalizedDescriptionKey:@"synthetic offline"}]];
        return;
    }
    NSDictionary *cases = @{
        @"/ok": @[@200, @"{\"fixture\":1}"],
        @"/messenger/mobile/users": @[@200, @"{\"retried\":true}"],
        @"/array": @[@299, @"[1,2]"],
        @"/empty": @[@204, @""],
        @"/redirect-status": @[@300, @"{}"],
        @"/invalid-json": @[@400, @"{"],
        @"/expired": @[@400, @"{\"error\":{\"code\":\"intercom_user_jwt_expired\",\"message\":\"server message\"}}"],
        @"/success-error-shape": @[@200, @"{\"error\":{\"code\":\"intercom_user_jwt_expired\"}}"],
        @"/shutdown": @[@503, @"{\"error\":{\"type\":\"messenger_shutdown_response\",\"message\":\"fixture pause\",\"shutdown_period\":60}}"]
    };
    NSArray *fixture = cases[path];
    if (!fixture) {
        [self.client URLProtocol:self didFailWithError:[NSError errorWithDomain:@"fixture.unknown-url" code:1 userInfo:nil]];
        return;
    }
    NSHTTPURLResponse *response = [[NSHTTPURLResponse alloc] initWithURL:self.request.URL statusCode:[fixture[0] integerValue] HTTPVersion:@"HTTP/1.1" headerFields:@{@"Content-Type":@"application/json"}];
    [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
    [self.client URLProtocol:self didLoadData:[fixture[1] dataUsingEncoding:NSUTF8StringEncoding]];
    [self.client URLProtocolDidFinishLoading:self];
}
- (void)stopLoading {}
@end
@interface FixtureRefresher : NSObject
@property (nonatomic, copy) void (^completion)(NSString *);
@end
@implementation FixtureRefresher
- (void)refreshWithStaleJwt:(NSString *)jwt fingerprint:(NSArray *)fingerprint completion:(void (^)(NSString *))completion {
    (void)jwt; (void)fingerprint; self.completion = completion;
}
@end
@interface TransportFixtureEngine : ICMOreoEngine
@property (nonatomic, strong) NSURLSession *fixtureSession;
@end
@implementation TransportFixtureEngine
- (NSURLSession *)session { return self.fixtureSession; }
@end
static NSMutableArray *passed;
static void check(BOOL condition, NSString *name) {
    if (!condition) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
static NSDictionary *perform(TransportFixtureEngine *engine, NSString *path) {
    dispatch_semaphore_t finished = dispatch_semaphore_create(0);
    __block NSURLResponse *receivedResponse;
    __block id receivedObject;
    __block NSError *receivedError;
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:[@"https://synthetic.invalid" stringByAppendingString:path]]];
    NSURLSessionDataTask *task = [engine dataTaskWithRequest:request completionHandler:^(NSURLResponse *response, id object, NSError *error) {
        receivedResponse = response; receivedObject = object; receivedError = error;
        dispatch_semaphore_signal(finished);
    }];
    if (task) {
        check(task.state == NSURLSessionTaskStateSuspended, @"transport returns suspended actual data task");
        [task resume];
    }
    if (dispatch_semaphore_wait(finished, dispatch_time(DISPATCH_TIME_NOW, 5 * NSEC_PER_SEC))) {
        fprintf(stderr, "FAIL: transport fixture timed out\n"); exit(1);
    }
    return @{@"response":receivedResponse ?: [NSNull null], @"object":receivedObject ?: [NSNull null], @"error":receivedError ?: [NSNull null], @"createdTask":@(task != nil)};
}
static NSDictionary *performOperation(TransportFixtureEngine *engine, NSString *path, BOOL ignore) {
    dispatch_semaphore_t finished = dispatch_semaphore_create(0);
    __block id receivedObject;
    __block NSError *receivedError;
    __block NSURLSessionDataTask *callbackTask;
    if (ignore) engine.oreoOperationQueue.suspended = YES;
    NSURLSessionDataTask *task = [engine dataTaskWithRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:[@"https://synthetic.invalid" stringByAppendingString:path]]] retries:0 success:^(NSURLSessionDataTask *value, id object) {
        callbackTask = value; receivedObject = object; dispatch_semaphore_signal(finished);
    } failure:^(NSURLSessionDataTask *value, NSError *error) {
        callbackTask = value; receivedError = error; dispatch_semaphore_signal(finished);
    }];
    if (ignore) { [engine ignoreAllPendingRequests]; engine.oreoOperationQueue.suspended = NO; }
    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:5];
    while (dispatch_semaphore_wait(finished, DISPATCH_TIME_NOW)) {
        if (deadline.timeIntervalSinceNow < 0) { fprintf(stderr, "FAIL: operation fixture timeout\n"); exit(1); }
        [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
    }
    return @{@"object":receivedObject ?: [NSNull null], @"error":receivedError ?: [NSNull null], @"callbackTask":callbackTask ?: [NSNull null], @"initialTask":task ?: [NSNull null]};
}
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration ephemeralSessionConfiguration];
        configuration.protocolClasses = @[[SyntheticProtocol class]];
        TransportFixtureEngine *engine = [TransportFixtureEngine new];
        engine.fixtureSession = [NSURLSession sessionWithConfiguration:configuration];
        engine.appFingerprint = [ICMAppInstallationFingerprint new];
        engine.shutdownState = [[ICMOreoShutdownState alloc] initWithShutdownPeriod:0 shutdownReason:@""];
        engine.oreoOperationQueue = [NSOperationQueue new];
        engine.oreoOperationQueue.suspended = YES;
        ICMURLSessionTaskOperation *pending = [[ICMURLSessionTaskOperation alloc] initWithURLSessionTask:nil];
        NSBlockOperation *unrelated = [NSBlockOperation blockOperationWithBlock:^{}];
        [engine.oreoOperationQueue addOperations:@[pending, unrelated] waitUntilFinished:NO];
        engine.ignoredRequestsGeneration = NSUIntegerMax;
        [engine ignoreAllPendingRequests];
        check(engine.ignoredRequestsGeneration == 0 && pending.ignoreResponse && !pending.isCancelled && !unrelated.isCancelled, @"ignore increments wrapping generation and marks only task operations without cancelling");
        [engine ignoreAllPendingRequests];
        check(engine.ignoredRequestsGeneration == 1 && pending.ignoreResponse, @"repeated ignore increments generation and retains pending mark");
        [engine.oreoOperationQueue cancelAllOperations];
        engine.oreoOperationQueue.suspended = NO;
        [engine.oreoOperationQueue waitUntilAllOperationsAreFinished];
        NSDictionary *result = perform(engine, @"/ok");
        check([result[@"object"] isEqual:@{@"fixture":@1}] && result[@"error"] == [NSNull null] && [result[@"response"] statusCode] == 200, @"real data task parses successful JSON dictionary");
        result = perform(engine, @"/array");
        check([result[@"object"] isEqual:@[@1,@2]] && result[@"error"] == [NSNull null], @"299 success accepts array JSON without dictionary coercion");
        result = perform(engine, @"/empty");
        check(result[@"object"] == [NSNull null] && result[@"error"] == [NSNull null], @"204 empty data returns nil object without parsing");
        result = perform(engine, @"/redirect-status");
        NSError *error = result[@"error"];
        check(error.code == 300 && [error.domain isEqual:@"io.intercom.ios.error.network"] && result[@"object"] == [NSNull null], @"300 is HTTP error and suppresses parsed object");
        result = perform(engine, @"/invalid-json"); error = result[@"error"];
        check([error.domain isEqual:NSCocoaErrorDomain] && error.code == 3840, @"JSON parsing error takes precedence over HTTP status error");
        result = perform(engine, @"/expired"); error = result[@"error"];
        check(error.code == 400 && [error.userInfo[@"kIntercomErrorCode"] isEqual:@"intercom_user_jwt_expired"] && [error.localizedDescription hasPrefix:@"HTTP Error: 400 "], @"server error code augments original HTTP error without replacing description");
        result = perform(engine, @"/success-error-shape");
        check(result[@"error"] == [NSNull null] && [result[@"object"] isKindOfClass:[NSDictionary class]], @"error-shaped JSON in 200 response remains successful JSON");
        result = perform(engine, @"/offline"); error = result[@"error"];
        check(error.code == -1009 && [error.domain isEqual:@"fixture.transport"] && result[@"object"] == [NSNull null], @"actual session propagates transport error first");
        result = perform(engine, @"/shutdown"); error = result[@"error"];
        check(error.code == 503 && [engine.shutdownState.shutdownReason isEqual:@"fixture pause"] && ![engine.shutdownState canSendOreoNetworkRequestsWithFingerprint:engine.appFingerprint], @"shutdown response updates actual engine state");
        NSUInteger before = requests;
        result = perform(engine, @"/ok"); error = result[@"error"];
        check(![result[@"createdTask"] boolValue] && requests == before && [error.domain isEqual:@"io.intercom.ios.error.network.shutdown"] && result[@"response"] == [NSNull null], @"active shutdown synchronously rejects without making session task");
        [engine setValue:nil forKey:@"appFingerprint"];
        result = perform(engine, @"/ok");
        check([result[@"createdTask"] boolValue] && result[@"error"] == [NSNull null], @"changed nil fingerprint preserves original send eligibility");
        engine.shutdownState = [[ICMOreoShutdownState alloc] initWithShutdownPeriod:0 shutdownReason:@""];
        result = performOperation(engine, @"/ok", NO);
        check([result[@"object"] isEqual:@{@"fixture":@1}] && result[@"callbackTask"] == result[@"initialTask"] && engine.oreoOperationQueue.maxConcurrentOperationCount == 1, @"real operation enqueues/resumes task and returns task in success callback");
        result = performOperation(engine, @"/ok", YES); error = result[@"error"];
        check([error.domain isEqual:@"Network request succeeded but the response was ignored"] && error.code == 0 && result[@"object"] == [NSNull null], @"ignored successful operation routes original ignored error to failure");
        result = performOperation(engine, @"/messenger/mobile/users", NO);
        check([result[@"object"] isEqual:@{@"retried":@YES}] && retryRequests == 2 && result[@"callbackTask"] != result[@"initialTask"], @"retryable eligible transport failure creates delayed operation and reports retry task");
        FixtureRefresher *refresher = [FixtureRefresher new];
        engine.userJwtRefresher = (id)refresher;
        __block NSUInteger otherwise = 0, ignored = 0;
        ICMOreoUserJwtReplay *replay = [[ICMOreoUserJwtReplay alloc] initWithUserJwt:@"synthetic-old" fingerprint:@[@"fixture"] buildRequest:^NSURLRequest *(NSString *jwt) { (void)jwt; return nil; }];
        [engine replayRequestTo:@"/fixture" replay:replay success:nil failure:nil otherwise:^{ otherwise++; } ignored:^{ ignored++; }];
        refresher.completion(@"synthetic-new");
        check(otherwise == 1 && ignored == 0, @"replay nil-built request invokes otherwise");
        [engine replayRequestTo:@"/fixture" replay:replay success:nil failure:nil otherwise:^{ otherwise++; } ignored:^{ ignored++; }];
        [engine ignoreAllPendingRequests]; refresher.completion(@"synthetic-new");
        check(otherwise == 1 && ignored == 1, @"replay generation change invokes ignored without rebuild");
        [engine.fixtureSession invalidateAndCancel];
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"interceptedRequests":@(requests), @"scope":@"Actual Foundation transport/parser slice; fixture-only identity and session; whole SDK remains unlinked"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
