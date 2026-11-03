#import "JCOREQueueManager.h"

@implementation JCOREQueueManager
+ (NSThread *)residentThread {
    static NSThread *thread;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        thread = [[NSThread alloc] initWithTarget:self selector:@selector(threadEntryPoint:) object:nil];
        [thread start];
    });
    return thread;
}

+ (void)threadEntryPoint:(id)object {
    @autoreleasepool {
        [NSThread currentThread].name = @"org.hxhg.jiguang.thread";
        NSRunLoop *runLoop = [NSRunLoop currentRunLoop];
        [runLoop addPort:[NSMachPort port] forMode:NSDefaultRunLoopMode];
        [runLoop run];
    }
}

+ (NSOperationQueue *)syncOperationQueue {
    static NSOperationQueue *queue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        queue = [[NSOperationQueue alloc] init];
        queue.maxConcurrentOperationCount = 1;
        queue.name = @"org.hxhg.jcore.serial.operationQueue";
    });
    return queue;
}

+ (NSOperationQueue *)asyncOperationQueue {
    static NSOperationQueue *queue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        queue = [[NSOperationQueue alloc] init];
        queue.name = @"org.hxhg.jcore.concurrent.operationQueue";
    });
    return queue;
}

+ (dispatch_queue_t)syncQueue {
    static dispatch_queue_t queue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        queue = dispatch_queue_create("org.hxhg.jcore.serial.thread", DISPATCH_QUEUE_SERIAL);
    });
    return queue;
}

+ (dispatch_queue_t)asyncQueue {
    static dispatch_queue_t queue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        queue = dispatch_queue_create("org.hxhg.jcore.concurrent.thread", DISPATCH_QUEUE_CONCURRENT);
    });
    return queue;
}
@end
