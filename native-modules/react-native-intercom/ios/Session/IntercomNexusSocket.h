// Original NSURLSession WebSocket transport interface.
#import <Foundation/Foundation.h>
@class IntercomNexusSocket;
@protocol IntercomNexusSocketDelegate <NSObject>
- (void)websocket:(IntercomNexusSocket *)socket didReceiveMessage:(NSDictionary *)message;
- (void)websocketDidConnect:(IntercomNexusSocket *)socket;
- (void)websocketDidDisconnect:(IntercomNexusSocket *)socket;
@end
@interface IntercomNexusSocket : NSObject <NSURLSessionWebSocketDelegate> {
    BOOL _isReconnecting;
    NSURLSessionWebSocketTask *_websocket;
    NSURL *_url;
    __weak id<IntercomNexusSocketDelegate> _delegate;
    __weak NSTimer *_reconnectionTimer;
    __weak NSTimer *_timeoutTimer;
    NSUInteger _reconnectionAttempts;
    NSNumber *_timeoutInterval;
    NSURLSession *_session;
}
@property (nonatomic, strong) NSURLSessionWebSocketTask *websocket;
@property (nonatomic, strong) NSURL *url;
@property (nonatomic, readonly, weak) id<IntercomNexusSocketDelegate> delegate;
@property (nonatomic, weak) NSTimer *reconnectionTimer;
@property (nonatomic, weak) NSTimer *timeoutTimer;
@property (nonatomic) NSUInteger reconnectionAttempts;
@property (nonatomic, strong) NSNumber *timeoutInterval;
@property (nonatomic, strong) NSURLSession *session;
@property (nonatomic) BOOL isReconnecting;
- (instancetype)initWithURL:(NSURL *)url timeout:(NSNumber *)timeout delegate:(id<IntercomNexusSocketDelegate>)delegate;
- (void)createWebsocket;
- (void)sendWebSocketMessage:(NSDictionary *)message;
- (void)handleSendingMessageError:(NSError *)error;
- (void)listenForWebSocketMessages;
- (void)handleSocketMessageReceived:(NSURLSessionWebSocketMessage *)message;
- (BOOL)isConnected;
- (void)disconnect;
- (void)resetSocket;
- (void)reconnect;
- (void)scheduleReconnect;
- (void)resetConnectionTimeout;
- (void)connectionTimedOut;
- (void)handleSocketDisconnection:(NSString *)reason;
@end
