#import <Foundation/Foundation.h>
#import "JCOREWireProtocol.h"

NS_ASSUME_NONNULL_BEGIN

typedef void (^JCORETcpResponseHandler)(JCOREWireFrame *_Nullable responseFrame, NSError *_Nullable error);

@interface JCORETcpSession : NSObject

+ (instancetype)shared;

@property (nonatomic, assign, readonly, getter=isConnected) BOOL connected;
@property (nonatomic, assign, readonly, getter=isLoggedIn) BOOL loggedIn;
@property (nonatomic, assign, readonly) uint32_t sessionId;
@property (nonatomic, assign, readonly) uint16_t serverVersion;
@property (nonatomic, assign, readonly) uint32_t serverTimestamp;
@property (nonatomic, assign, readonly) NSUInteger reconnectAttemptCount;

- (void)setCustomConnectHost:(nullable NSString *)host
                        port:(uint16_t)port
                      useSSL:(BOOL)useSSL;

- (void)startIfNeeded;
- (void)reconnect;
- (void)disconnect;

- (void)reportDeviceTokenIfNeeded;
- (void)reportVoipTokenIfNeeded;
- (void)sendHeartbeatNowWithCompletion:(nullable JCORETcpResponseHandler)completion;
- (void)sendReceiptForMessageType:(uint8_t)messageType messageId:(uint64_t)messageId;

- (void)sendCommand:(uint8_t)command
            version:(uint8_t)version
           bodyData:(NSData *)bodyData
         completion:(nullable JCORETcpResponseHandler)completion;

@end

@interface JCORETcpClient : JCORETcpSession
@end

NS_ASSUME_NONNULL_END
