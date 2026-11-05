#import <Foundation/Foundation.h>
#import "ICMURLSessionTaskOperation.h"
#import "ICMOreoUserJwtReplay.h"
@interface FixtureTask : NSObject
@property (nonatomic) NSUInteger resumes;
@property (nonatomic) NSUInteger cancels;
@property (nonatomic, copy) void (^onResume)(void);
- (void)resume;
- (void)cancel;
@end
@implementation FixtureTask
- (void)resume { self.resumes++; if (self.onResume) self.onResume(); }
- (void)cancel { self.cancels++; }
@end
@interface FixtureObserver : NSObject
@property (nonatomic, strong) NSMutableArray *changes;
@end
@implementation FixtureObserver
- (void)observeValueForKeyPath:(NSString *)key ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    (void)object; (void)context;
    [self.changes addObject:@{@"key":key, @"old":change[NSKeyValueChangeOldKey] ?: [NSNull null], @"new":change[NSKeyValueChangeNewKey] ?: [NSNull null]}];
}
@end
static NSMutableArray *passed;
static void check(BOOL condition, NSString *name) {
    if (!condition) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        FixtureTask *task = [FixtureTask new];
        ICMURLSessionTaskOperation *operation = [[ICMURLSessionTaskOperation alloc] initWithURLSessionTask:(id)task];
        check(operation.task == (id)task && operation.isReady && !operation.isExecuting && !operation.isFinished && operation.isAsynchronous && !operation.ignoreResponse && !operation.taskCompleted, @"initial asynchronous operation retains task and original flags");
        [operation finish];
        check(!operation.isFinished && operation.isReady, @"finish before start leaves state unchanged");
        FixtureObserver *observer = [FixtureObserver new]; observer.changes = [NSMutableArray new];
        NSArray *keys = @[@"isReady", @"isExecuting", @"isFinished"];
        for (NSString *key in keys) [operation addObserver:observer forKeyPath:key options:NSKeyValueObservingOptionOld | NSKeyValueObservingOptionNew context:NULL];
        [operation start];
        check(!operation.isReady && operation.isExecuting && !operation.isFinished && task.resumes == 1, @"start resumes once and enters executing");
        NSUInteger afterStart = observer.changes.count;
        [operation start];
        check(task.resumes == 1 && observer.changes.count == afterStart, @"repeated executing start does not resume or notify");
        operation.taskCompleted = YES;
        check(operation.isFinished && !operation.isExecuting && !operation.isReady, @"task completion finishes executing operation");
        NSUInteger afterFinish = observer.changes.count;
        operation.taskCompleted = YES; [operation finish];
        check(observer.changes.count == afterFinish, @"repeated completion and finish do not notify");
        check([observer.changes isEqual:@[@{@"key":@"isReady", @"old":@YES, @"new":@NO}, @{@"key":@"isExecuting", @"old":@NO, @"new":@YES}, @{@"key":@"isExecuting", @"old":@YES, @"new":@NO}, @{@"key":@"isFinished", @"old":@NO, @"new":@YES}]], @"actual KVO order matches ready/executing then executing/finished");
        for (NSString *key in keys) [operation removeObserver:observer forKeyPath:key];
        FixtureTask *cancelTask = [FixtureTask new];
        ICMURLSessionTaskOperation *cancelled = [[ICMURLSessionTaskOperation alloc] initWithURLSessionTask:(id)cancelTask];
        [cancelled cancel]; [cancelled start];
        check(cancelled.isCancelled && cancelled.isFinished && cancelled.isReady && !cancelled.isExecuting && cancelTask.cancels == 1 && cancelTask.resumes == 0, @"cancelled start sets finished without clearing ready or resuming");
        ICMURLSessionTaskOperation *early = [[ICMURLSessionTaskOperation alloc] initWithURLSessionTask:(id)[FixtureTask new]];
        early.taskCompleted = YES; [early start]; early.taskCompleted = YES;
        check(early.isExecuting && !early.isFinished && early.taskCompleted, @"pre-start completion true preserves original stalled executing state");
        early.taskCompleted = NO; early.taskCompleted = YES;
        check(early.isFinished && !early.isExecuting, @"false-to-true completion transition finishes early-completed operation");
        ICMURLSessionTaskOperation *active = [[ICMURLSessionTaskOperation alloc] initWithURLSessionTask:(id)[FixtureTask new]];
        [active start]; [active cancel];
        check(active.isCancelled && active.isExecuting && !active.isFinished, @"cancelling active operation waits for completion to finish");
        active.taskCompleted = YES;
        FixtureTask *queuedTask = [FixtureTask new];
        ICMURLSessionTaskOperation *queued = [[ICMURLSessionTaskOperation alloc] initWithURLSessionTask:(id)queuedTask];
        __weak ICMURLSessionTaskOperation *weakQueued = queued;
        queuedTask.onResume = ^{ dispatch_async(dispatch_get_global_queue(0, 0), ^{ weakQueued.taskCompleted = YES; }); };
        NSOperationQueue *queue = [NSOperationQueue new];
        queue.maxConcurrentOperationCount = 1; [queue addOperation:queued]; [queue waitUntilAllOperationsAreFinished];
        check(queuedTask.resumes == 1 && queued.isFinished && !queued.isExecuting, @"actual NSOperationQueue observes asynchronous finish and drains");
        NSMutableString *jwt = [NSMutableString stringWithString:@"synthetic-old"];
        NSMutableString *element = [NSMutableString stringWithString:@"user"];
        NSMutableArray *fingerprint = [NSMutableArray arrayWithObject:element];
        __block NSString *captured;
        ICMOreoUserJwtReplay *replay = [[ICMOreoUserJwtReplay alloc] initWithUserJwt:jwt fingerprint:fingerprint buildRequest:^NSURLRequest *(NSString *newJwt) { captured = newJwt; return [NSURLRequest requestWithURL:[NSURL URLWithString:@"https://synthetic.invalid/replay"]]; }];
        [jwt appendString:@"-changed"]; [fingerprint addObject:@"other"]; [element appendString:@"-changed"];
        check([replay.userJwt isEqual:@"synthetic-old"] && replay.fingerprint.count == 1 && [replay.fingerprint[0] isEqual:@"user-changed"], @"replay copies JWT and shallow-copies fingerprint array");
        NSURLRequest *request = replay.buildRequest(@"synthetic-refreshed");
        check([captured isEqual:@"synthetic-refreshed"] && [request.URL.path isEqual:@"/replay"], @"copied replay block remains callable with refreshed JWT");
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(passed.count), @"tests":passed, @"observedKVO":observer.changes, @"scope":@"Operation and replay helpers only; synthetic task, real KVO and operation queue"} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
