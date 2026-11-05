#import "IntercomNexusSocket.h"
#import "IntercomNexusEvent.h"
#import <UIKit/UIKit.h>
#include <math.h>
@implementation IntercomNexusSocket
@synthesize isReconnecting = _isReconnecting, websocket = _websocket, url = _url, delegate = _delegate, reconnectionTimer = _reconnectionTimer, timeoutTimer = _timeoutTimer, reconnectionAttempts = _reconnectionAttempts, timeoutInterval = _timeoutInterval, session = _session;
- (instancetype)initWithURL:(NSURL *)url timeout:(NSNumber *)timeout delegate:(id<IntercomNexusSocketDelegate>)delegate {
    self = [super init];
    if (self) {
        _delegate = delegate;
        _url = url;
        _timeoutInterval = timeout.floatValue > 0 ? timeout : @70;
        [self createWebsocket];
    }
    return self;
}
- (void)createWebsocket {
    @synchronized (self) {
        self.session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration] delegate:self delegateQueue:nil];
        self.websocket = [self.session webSocketTaskWithURL:_url];
        [self.websocket resume];
    }
}
- (void)sendWebSocketMessage:(NSDictionary *)message {
    @synchronized (self) {
        if ([self isConnected]) {
            IntercomNexusEvent *event = [IntercomNexusEvent instanceFromDictionary:message];
            if (event.type != 3 && event.type != 5) (void)event.type;
            NSError *error = nil;
            NSData *data = [NSJSONSerialization dataWithJSONObject:message options:0 error:&error];
            if (!error && data) {
                NSURLSessionWebSocketMessage *socketMessage = [[NSURLSessionWebSocketMessage alloc] initWithData:data];
                [self.websocket sendMessage:socketMessage completionHandler:^(NSError *sendError) {
                    if (sendError) [self handleSendingMessageError:sendError];
                }];
            }
        }
    }
}
- (void)handleSendingMessageError:(NSError *)error {
    __block __weak IntercomNexusSocket *weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        if (error.code >= 500 && error.code <= 599) {
            [self.timeoutTimer invalidate];
            [weakSelf scheduleReconnect];
        }
        [weakSelf.delegate websocketDidDisconnect:self];
    });
}
- (void)listenForWebSocketMessages {
    __block __weak IntercomNexusSocket *weakSelf = self;
    [self.websocket receiveMessageWithCompletionHandler:^(NSURLSessionWebSocketMessage *message, NSError *error) {
        if (error) return;
        [weakSelf listenForWebSocketMessages];
        [weakSelf handleSocketMessageReceived:message];
    }];
}
- (void)handleSocketMessageReceived:(NSURLSessionWebSocketMessage *)message {
    if (![self.delegate respondsToSelector:@selector(websocket:didReceiveMessage:)]) return;
    NSData *data = message.data;
    if (message.string) data = [message.string dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:NO];
    if (data && data.length) {
        NSError *error = nil;
        id decoded = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
        NSString *eventName = decoded[@"eventName"];
        if (!error && decoded && ![eventName isEqualToString:@"ACK"]) {
            [self.delegate websocket:self didReceiveMessage:decoded];
        }
    }
    [self resetConnectionTimeout];
}
- (BOOL)isConnected { return self.websocket && self.websocket.state == NSURLSessionTaskStateRunning; }
- (void)disconnect {
    @synchronized (self) {
        NSData *reason = [@"purposeful_disconnection" dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:NO];
        [self.websocket cancelWithCloseCode:NSURLSessionWebSocketCloseCodeNormalClosure reason:reason];
        self.websocket = nil;
        [self resetSocket];
        [self.session finishTasksAndInvalidate];
    }
}
- (void)resetSocket {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.timeoutTimer invalidate];
        [self.reconnectionTimer invalidate];
    });
}
- (void)reconnect {
    if (![self isConnected] && !self.isReconnecting) {
        self.isReconnecting = YES;
        [self resetSocket];
        [self createWebsocket];
    }
}
- (void)scheduleReconnect {
    __block __weak IntercomNexusSocket *weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.reconnectionTimer invalidate];
        IntercomNexusSocket *current = weakSelf;
        current.reconnectionAttempts = current.reconnectionAttempts + 1;
        uint32_t range = (uint32_t)fmin(exp2((double)self.reconnectionAttempts), 256);
        NSTimeInterval interval = (uint32_t)(range + arc4random_uniform(range));
        self.reconnectionTimer = [NSTimer scheduledTimerWithTimeInterval:interval repeats:YES block:^(NSTimer *timer) {
            (void)timer;
            [weakSelf reconnect];
        }];
    });
}
- (void)resetConnectionTimeout {
    __block __weak IntercomNexusSocket *weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.timeoutTimer invalidate];
        self.timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:(double)self.timeoutInterval.floatValue repeats:NO block:^(NSTimer *timer) {
            (void)timer;
            [weakSelf connectionTimedOut];
        }];
    });
}
- (void)connectionTimedOut {
    NSData *reason = [@"Connection timed out" dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:NO];
    [self.websocket cancelWithCloseCode:NSURLSessionWebSocketCloseCodeGoingAway reason:reason];
}
- (void)URLSession:(NSURLSession *)session didBecomeInvalidWithError:(NSError *)error {
    (void)session;
    if (error) [self handleSocketDisconnection:@"socket became invalid"];
}
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    (void)session; (void)task;
    if (error) [self handleSocketDisconnection:[NSString stringWithFormat:@"completed with error : %@", error]];
}
- (void)URLSession:(NSURLSession *)session webSocketTask:(NSURLSessionWebSocketTask *)task didOpenWithProtocol:(NSString *)protocol {
    (void)session; (void)task; (void)protocol;
    self.isReconnecting = NO;
    [self listenForWebSocketMessages];
    self.reconnectionAttempts = 0;
    [self resetConnectionTimeout];
    [self.delegate websocketDidConnect:self];
}
- (void)URLSession:(NSURLSession *)session webSocketTask:(NSURLSessionWebSocketTask *)task didCloseWithCode:(NSURLSessionWebSocketCloseCode)code reason:(NSData *)reason {
    (void)session; (void)task; (void)code;
    [self handleSocketDisconnection:[[NSString alloc] initWithData:reason encoding:NSUTF8StringEncoding]];
}
- (void)handleSocketDisconnection:(NSString *)reason {
    self.isReconnecting = NO;
    [self resetSocket];
    if (![reason isEqualToString:@"purposeful_disconnection"]) {
        __block __weak IntercomNexusSocket *weakSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([UIApplication sharedApplication].applicationState != UIApplicationStateBackground) {
                IntercomNexusSocket *current = weakSelf;
                if (current) [current scheduleReconnect];
            }
        });
    }
    [self.delegate websocketDidDisconnect:self];
}
@end
