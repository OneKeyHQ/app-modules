#import "ICMEngine.h"
#import "ICMMultipartTaskBuilder.h"
@interface NSObject (OriginalEngineConfiguration)
- (NSTimeInterval)timeoutInterval;
@end
@implementation ICMEngine
- (void)requestWithoutRedirects:(NSURLRequest *)request success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    ICMEngineSessionDelegate *delegate = [ICMEngineSessionDelegate new];
    delegate.emptySuccessBlock = success;
    delegate.failureBlock = failure;
    delegate.ignoreRedirects = YES;
    NSURLSession *session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration] delegate:delegate delegateQueue:nil];
    [[session dataTaskWithRequest:request] resume];
}
- (NSURLSessionUploadTask *)uploadFileWithFileName:(NSString *)name data:(NSData *)data uploadConfig:(id<ICMFileUploadConfig>)config success:(void (^)(void))success progress:(void (^)(double))progress failure:(void (^)(NSError *))failure {
    ICMEngineSessionDelegate *delegate = [ICMEngineSessionDelegate new];
    delegate.emptySuccessBlock = success;
    delegate.failureBlock = failure;
    delegate.progressBlock = progress;
    NSURLSession *session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration] delegate:delegate delegateQueue:nil];
    NSURL *url = [NSURL URLWithString:[config uploadDestination]];
    NSDictionary *parameters = [config postParametersDictionary];
    NSString *contentType = [config contentType];
    NSURLSessionUploadTask *task = [ICMMultipartTaskBuilder createUploadTaskWith:session url:url parameters:parameters fileData:data fileName:name mimeType:contentType];
    [task resume];
    return task;
}
- (NSURLSessionDownloadTask *)downloadFileFrom:(NSURL *)url downloadConfig:(id)config success:(void (^)(NSURL *, NSURLResponse *))success failure:(void (^)(NSError *))failure {
    NSURLRequest *request = [NSURLRequest requestWithURL:url cachePolicy:0 timeoutInterval:[config timeoutInterval]];
    ICMEngineSessionDelegate *delegate = [ICMEngineSessionDelegate new];
    delegate.downloadContentSuccessBlock = success;
    delegate.failureBlock = failure;
    NSURLSession *session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration] delegate:delegate delegateQueue:nil];
    NSURLSessionDownloadTask *task = [session downloadTaskWithRequest:request];
    [task resume];
    return task;
}
- (void)requestURL:(NSString *)string success:(void (^)(NSURLResponse *))success failure:(void (^)(NSError *))failure {
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:string]];
    [[NSURLSession.sharedSession dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        (void)data;
        if (error) failure(error);
        else success(response);
    }] resume];
}
@end
@implementation ICMEngineSessionDelegate
@synthesize ignoreRedirects = _ignoreRedirects, emptySuccessBlock = _emptySuccessBlock, downloadContentSuccessBlock = _downloadContentSuccessBlock, failureBlock = _failureBlock, progressBlock = _progressBlock;
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didSendBodyData:(int64_t)bytesSent totalBytesSent:(int64_t)totalBytesSent totalBytesExpectedToSend:(int64_t)expected {
    (void)session; (void)task; (void)bytesSent;
    if (self.progressBlock) self.progressBlock((double)totalBytesSent / (double)expected);
}
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    (void)task;
    if (error) {
        if (self.failureBlock) self.failureBlock(error);
    } else {
        if (self.emptySuccessBlock) self.emptySuccessBlock();
    }
    [session finishTasksAndInvalidate];
}
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task willPerformHTTPRedirection:(NSHTTPURLResponse *)response newRequest:(NSURLRequest *)request completionHandler:(void (^)(NSURLRequest *))completion {
    (void)session; (void)task; (void)response;
    if (completion) completion(self.ignoreRedirects ? nil : request);
}
- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)task didFinishDownloadingToURL:(NSURL *)url {
    (void)session;
    NSHTTPURLResponse *response = (NSHTTPURLResponse *)task.response;
    if (response.statusCode != 200 || task.error) {
        if (self.failureBlock) self.failureBlock(task.error);
    } else {
        if (self.downloadContentSuccessBlock) self.downloadContentSuccessBlock(url, task.response);
    }
}
@end
