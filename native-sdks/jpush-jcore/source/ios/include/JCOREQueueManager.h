#pragma once
#import <Foundation/Foundation.h>
#import <dispatch/dispatch.h>

@interface JCOREQueueManager : NSObject
+ (NSThread *)residentThread;
+ (void)threadEntryPoint:(id)object;
+ (NSOperationQueue *)syncOperationQueue;
+ (NSOperationQueue *)asyncOperationQueue;
+ (dispatch_queue_t)syncQueue;
+ (dispatch_queue_t)asyncQueue;
@end
