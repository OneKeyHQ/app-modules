// SOURCE_RESTORED_UNLINKED: full native identity/Bridge/session closure is incomplete.
#import "ICMOreoEngine.h"
#import "ICMURLSessionTaskOperation.h"
#import "ICMOreoUserJwtReplay.h"
#import "ICMUserJwtRefresher.h"
#import "ICMIdentityStore.h"
#import "ICMIdentityStoreDependencies.h"
#import "../Identity/ICMErrorStatus.h"
#import "../Identity/ICMLogger.h"
@implementation ICMOreoEngine (Operations)
- (void)ignoreAllPendingRequests {
    self.ignoredRequestsGeneration = self.ignoredRequestsGeneration + 1;
    for (NSOperation *operation in self.oreoOperationQueue.operations) {
        if ([operation isKindOfClass:[ICMURLSessionTaskOperation class]]) {
            ((ICMURLSessionTaskOperation *)operation).ignoreResponse = YES;
        }
    }
}
- (ICMURLSessionTaskOperation *)operationWithRequest:(NSURLRequest *)request retries:(NSInteger)retries success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    return [self operationWithRequest:request retries:retries replay:nil success:success failure:failure];
}
- (ICMURLSessionTaskOperation *)operationWithRequest:(NSURLRequest *)request retries:(NSInteger)retries replay:(ICMOreoUserJwtReplay *)replay success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    self.oreoOperationQueue.maxConcurrentOperationCount = [ICMIdentityStore currentIdentity].userIdentity.intercomId.length ? -1 : 1;
    __block ICMURLSessionTaskOperation *operation;
    __block NSURLSessionDataTask *task;
    __weak ICMOreoEngine *weakSelf = self;
    task = [self dataTaskWithRequest:request completionHandler:^(NSURLResponse *response, id object, NSError *error) {
        (void)response;
        ICMOreoEngine *engine = weakSelf;
        operation.taskCompleted = YES;
        if (operation.ignoreResponse) {
            if (failure) failure(task, error ?: [ICMOreoEngine ignoredResponseError]);
            return;
        }
        if (!error) { if (success) success(task, object); return; }
        void (^fallback)(void) = ^{
            if ([ICMRecoveredErrorProcessor isUserJwtExpired:error]) {
                [ICMLogger publicLog:@"[Intercom] Intercom rejected a request because the user JWT has expired. Call setUserJwt: with a fresh token, or register a provider with setUserJwtProvider:." type:16];
            }
            if ([ICMOreoEngine shouldRetryRequestURL:request.URL.path]) {
                NSInteger next = (NSInteger)((NSUInteger)retries + 1);
                NSInteger code = task.response ? [(NSHTTPURLResponse *)task.response statusCode] : error.code;
                NSError *classified = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:code userInfo:nil];
                if (next <= 3 && [ICMErrorStatus isRetryable:classified]) {
                    [engine retryDataTask:task numberOfRetries:next replay:replay success:success failure:failure];
                } else if (failure) failure(task, error);
            } else if (failure) failure(task, error);
        };
        if (replay && engine && [ICMRecoveredErrorProcessor isUserJwtExpired:error]) {
            [engine replayRequestTo:request.URL.path replay:replay success:success failure:failure otherwise:fallback ignored:^{ if (failure) failure(task, error); }];
        } else fallback();
    }];
    operation = [[ICMURLSessionTaskOperation alloc] initWithURLSessionTask:task];
    return operation;
}
- (NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request retries:(NSInteger)retries success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    ICMURLSessionTaskOperation *operation = [self operationWithRequest:request retries:retries success:success failure:failure];
    if (retries == 0) [self.oreoOperationQueue addOperation:operation];
    return (NSURLSessionDataTask *)operation.task;
}
- (NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request retries:(NSInteger)retries replay:(ICMOreoUserJwtReplay *)replay success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    ICMURLSessionTaskOperation *operation = [self operationWithRequest:request retries:retries replay:replay success:success failure:failure];
    if (retries == 0) [self.oreoOperationQueue addOperation:operation];
    return (NSURLSessionDataTask *)operation.task;
}
- (void)retryDataTask:(NSURLSessionDataTask *)task numberOfRetries:(NSInteger)retries replay:(ICMOreoUserJwtReplay *)replay success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    __block ICMURLSessionTaskOperation *operation = [self operationWithRequest:task.originalRequest retries:retries replay:replay success:success failure:failure];
    dispatch_time_t deadline = dispatch_time(DISPATCH_TIME_NOW, (int64_t)([ICMOreoEngine backOffTimeForReplyNumber:retries] * NSEC_PER_SEC));
    dispatch_after(deadline, dispatch_get_main_queue(), ^{ [self.oreoOperationQueue addOperation:operation]; });
}
- (void)replayRequestTo:(NSString *)path replay:(ICMOreoUserJwtReplay *)replay success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure otherwise:(void (^)(void))otherwise ignored:(void (^)(void))ignored {
    NSUInteger generation = self.ignoredRequestsGeneration;
    __weak ICMOreoEngine *weakSelf = self;
    [self.userJwtRefresher refreshWithStaleJwt:replay.userJwt fingerprint:replay.fingerprint completion:^(NSString *jwt) {
        ICMOreoEngine *engine = weakSelf;
        if (!engine || engine.ignoredRequestsGeneration != generation) { ignored(); return; }
        if (jwt) {
            NSURLRequest *request = replay.buildRequest(jwt);
            if (request) {
                [ICMLogger debugLog:[NSString stringWithFormat:@"[Intercom] Replaying a request to %@ with a refreshed user JWT", path]];
                [engine.oreoOperationQueue addOperation:[engine operationWithRequest:request retries:0 replay:nil success:success failure:failure]];
                return;
            }
        }
        otherwise();
    }];
}
@end
