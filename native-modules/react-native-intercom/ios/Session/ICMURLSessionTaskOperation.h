// Internal interface recovered from Intercom 19.9.1 metadata.
#import <Foundation/Foundation.h>
@interface ICMURLSessionTaskOperation : NSOperation {
    BOOL _ready;
    BOOL _executing;
    BOOL _finished;
    BOOL _ignoreResponse;
    BOOL _taskCompleted;
    NSURLSessionTask *_task;
}
@property (nonatomic, readonly) NSURLSessionTask *task;
@property (nonatomic) BOOL ignoreResponse;
@property (nonatomic) BOOL taskCompleted;
- (instancetype)initWithURLSessionTask:(NSURLSessionTask *)task;
- (void)finish;
- (void)setReady:(BOOL)value;
- (void)setExecuting:(BOOL)value;
- (void)setFinished:(BOOL)value;
@end
