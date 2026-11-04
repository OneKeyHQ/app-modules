#import "JCORETcpSession.h"
#import "JCOREConfiguration.h"
#import "JCORESisResolver.h"
#if __has_include("JPUSHService.h")
#import "JPUSHService.h"
#else
#import "../include/JPUSHService.h"
#endif
#import <Network/Network.h>
#if __has_include(<UIKit/UIKit.h>)
#import <UIKit/UIKit.h>
#endif

NSString *const kJPFNetworkIsConnectingNotification = @"kJPFNetworkIsConnectingNotification";
NSString *const kJPFNetworkDidSetupNotification = @"kJPFNetworkDidSetupNotification";
NSString *const kJPFNetworkDidCloseNotification = @"kJPFNetworkDidCloseNotification";
NSString *const kJPFNetworkDidRegisterNotification = @"kJPFNetworkDidRegisterNotification";
NSString *const kJPFNetworkFailedRegisterNotification = @"kJPFNetworkFailedRegisterNotification";
NSString *const kJPFNetworkDidLoginNotification = @"kJPFNetworkDidLoginNotification";
NSString *const kJPFNetworkDidReceiveMessageNotification = @"kJPFNetworkDidReceiveMessageNotification";
NSString *const kJPFServiceErrorNotification = @"kJPFServiceErrorNotification";

@interface JCOREQueuedWireRequest : NSObject
@property (nonatomic, assign) uint8_t command;
@property (nonatomic, assign) uint8_t version;
@property (nonatomic, strong) NSData *bodyData;
@property (nonatomic, copy, nullable) JCORETcpResponseHandler completion;
@end

@implementation JCOREQueuedWireRequest
@end

@interface JCOREInFlightRequest : NSObject
@property (nonatomic, assign) uint16_t requestId;
@property (nonatomic, assign) uint8_t command;
@property (nonatomic, assign) uint8_t version;
@property (nonatomic, strong) NSData *packetData;
@property (nonatomic, copy, nullable) JCORETcpResponseHandler completion;
@property (nonatomic, strong, nullable) dispatch_source_t resendTimer;
@property (nonatomic, strong, nullable) dispatch_source_t timeoutTimer;
- (void)cancelTimers;
@end

@implementation JCOREInFlightRequest
- (void)cancelTimers {
    if (_resendTimer != nil) {
        dispatch_source_cancel(_resendTimer);
        _resendTimer = nil;
    }
    if (_timeoutTimer != nil) {
        dispatch_source_cancel(_timeoutTimer);
        _timeoutTimer = nil;
    }
}
@end

@interface JCORETcpSession () {
    dispatch_queue_t _sessionQueue;
    nw_connection_t _connection;
    nw_path_monitor_t _pathMonitor;
    dispatch_source_t _heartbeatTimer;
    dispatch_source_t _connectTimeoutTimer;
    dispatch_source_t _reconnectTimer;
    NSMutableData *_receiveBuffer;
    NSMutableDictionary<NSNumber *, JCOREInFlightRequest *> *_inFlightRequests;
    NSMutableArray<JCOREQueuedWireRequest *> *_queuedRequests;
    NSArray<JCORESisEndpoint *> *_candidateEndpoints;
    NSUInteger _currentEndpointIndex;
    NSUInteger _reconnectAttempt;
    uint32_t _sessionId;
    uint16_t _serverVersion;
    uint32_t _serverTimestamp;
    BOOL _connecting;
    BOOL _connected;
    BOOL _loggedIn;
    BOOL _userDisconnected;
    NSString *_lastReportedDeviceTokenHex;
    NSString *_lastReportedVoipTokenHex;
}
@end

@implementation JCORETcpSession

+ (instancetype)shared {
    static JCORETcpSession *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[JCORETcpSession alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _sessionQueue = dispatch_queue_create("cn.jiguang.jcore.tcp.session", DISPATCH_QUEUE_SERIAL);
        _receiveBuffer = [NSMutableData data];
        _inFlightRequests = [NSMutableDictionary dictionary];
        _queuedRequests = [NSMutableArray array];
        [self setupNetworkAndLifecycleMonitors];
    }
    return self;
}

- (void)setupNetworkAndLifecycleMonitors {
    nw_path_monitor_t monitor = nw_path_monitor_create();
    if (monitor != nil) {
        _pathMonitor = monitor;
        nw_path_monitor_set_queue(monitor, _sessionQueue);
        __weak typeof(self) weakSelf = self;
        nw_path_monitor_set_update_handler(monitor, ^(nw_path_t path) {
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if (!strongSelf) {
                return;
            }
            if (nw_path_get_status(path) == nw_path_status_satisfied) {
                if (!strongSelf->_userDisconnected &&
                    !strongSelf->_connected &&
                    !strongSelf->_connecting &&
                    [JCOREConfiguration shared].appKey.length > 0 &&
                    ![JCOREConfiguration shared].pushStopped) {
                    strongSelf->_reconnectAttempt = 0;
                    [strongSelf startLockedForceReconnect:NO];
                }
            }
        });
        nw_path_monitor_start(monitor);
    }

#if __has_include(<UIKit/UIKit.h>)
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleAppWillEnterForeground:)
                                                 name:UIApplicationWillEnterForegroundNotification
                                               object:nil];
#endif
}

- (void)handleAppWillEnterForeground:(NSNotification *)note {
    (void)note;
    dispatch_async(_sessionQueue, ^{
        if (self->_userDisconnected || [JCOREConfiguration shared].pushStopped) {
            return;
        }
        if (!self->_connected && !self->_connecting) {
            self->_reconnectAttempt = 0;
            [self startLockedForceReconnect:NO];
        } else if (self->_connected && self->_loggedIn) {
            NSData *hb = [JCOREWireProtocol buildHeartbeatPayloadWithPushStopped:[JCOREConfiguration shared].pushStopped];
            [self sendRawCommandLocked:JCOREWireCommandHeartbeat version:8 bodyData:hb completion:nil];
        }
    });
}

- (BOOL)isConnected {
    return _connected;
}

- (BOOL)isLoggedIn {
    return _loggedIn;
}

- (uint32_t)sessionId {
    return _sessionId;
}

- (uint16_t)serverVersion {
    return _serverVersion;
}

- (uint32_t)serverTimestamp {
    return _serverTimestamp;
}

- (NSUInteger)reconnectAttemptCount {
    return _reconnectAttempt;
}

- (void)setCustomConnectHost:(NSString *)host
                        port:(uint16_t)port
                      useSSL:(BOOL)useSSL {
    [[JCOREConfiguration shared] setCustomConnectHost:host port:port useSSL:useSSL];
}

- (void)postNotificationOnMain:(NSString *)name userInfo:(nullable NSDictionary *)userInfo {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter] postNotificationName:name object:userInfo userInfo:userInfo];
    });
}

- (void)startIfNeeded {
    dispatch_async(_sessionQueue, ^{
        self->_userDisconnected = NO;
        [self startLockedForceReconnect:NO];
    });
}

- (void)reconnect {
    dispatch_async(_sessionQueue, ^{
        self->_userDisconnected = NO;
        self->_reconnectAttempt = 0;
        [self startLockedForceReconnect:YES];
    });
}

- (void)startLockedForceReconnect:(BOOL)forceReconnect {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    if (config.appKey.length == 0 || config.pushStopped) {
        return;
    }
    [self cancelReconnectTimerLocked];
    if (forceReconnect) {
        [self tearDownConnectionLocked];
        _connecting = NO;
    }
    if (_connected || _connecting) {
        return;
    }
    _connecting = YES;
    _lastReportedDeviceTokenHex = nil;
    _lastReportedVoipTokenHex = nil;
    [self postNotificationOnMain:kJPFNetworkIsConnectingNotification userInfo:nil];

    [[JCORESisResolver shared] resolveEndpointsWithCompletion:^(NSArray<JCORESisEndpoint *> *endpoints) {
        dispatch_async(self->_sessionQueue, ^{
            if (self->_userDisconnected || [JCOREConfiguration shared].pushStopped) {
                self->_connecting = NO;
                return;
            }
            if (endpoints.count == 0) {
                self->_connecting = NO;
                [self scheduleReconnectLocked];
                return;
            }
            self->_candidateEndpoints = [endpoints copy];
            self->_currentEndpointIndex = 0;
            [self connectToCurrentCandidateLocked];
        });
    }];
}

- (void)connectToCurrentCandidateLocked {
    if (_currentEndpointIndex >= _candidateEndpoints.count) {
        _connecting = NO;
        [self tearDownConnectionLocked];
        [self scheduleReconnectLocked];
        return;
    }
    JCORESisEndpoint *endpoint = _candidateEndpoints[_currentEndpointIndex];
    [self connectToEndpoint:endpoint];
}

- (void)advanceToNextCandidateAfterFailureOfEndpoint:(JCORESisEndpoint *)failedEndpoint {
    if (failedEndpoint.host.length > 0 && failedEndpoint.port > 0) {
        NSString *ipPort = [NSString stringWithFormat:@"%@:%u", failedEndpoint.host, failedEndpoint.port];
        [[JCOREConfiguration shared] addFailedIp:ipPort];
    }
    [self tearDownConnectionLocked];

    _currentEndpointIndex++;
    if (_currentEndpointIndex < _candidateEndpoints.count) {
        _connecting = YES;
        [self connectToCurrentCandidateLocked];
    } else {
        _connecting = NO;
        [self scheduleReconnectLocked];
    }
}

- (void)connectToEndpoint:(JCORESisEndpoint *)endpoint {
    [self tearDownConnectionLocked];
    _connecting = YES;

    const char *hostStr = endpoint.host.UTF8String;
    NSString *portString = [NSString stringWithFormat:@"%u", endpoint.port];
    const char *portStr = portString.UTF8String;
    if (hostStr == NULL || portStr == NULL) {
        [self advanceToNextCandidateAfterFailureOfEndpoint:endpoint];
        return;
    }

    nw_endpoint_t nwEndpoint = nw_endpoint_create_host(hostStr, portStr);
    nw_parameters_configure_protocol_block_t tlsConfig = endpoint.useSSL
        ? NW_PARAMETERS_DEFAULT_CONFIGURATION
        : NW_PARAMETERS_DISABLE_PROTOCOL;
    nw_parameters_t parameters = nw_parameters_create_secure_tcp(tlsConfig, NW_PARAMETERS_DEFAULT_CONFIGURATION);
    nw_connection_t connection = nw_connection_create(nwEndpoint, parameters);
    _connection = connection;
    nw_connection_set_queue(connection, _sessionQueue);

    [self startConnectTimeoutTimerLockedForConnection:connection endpoint:endpoint];

    __weak typeof(self) weakSelf = self;
    nw_connection_set_state_changed_handler(connection, ^(nw_connection_state_t state, nw_error_t error) {
        (void)error;
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || strongSelf->_connection != connection) {
            return;
        }
        if (state == nw_connection_state_ready) {
            [strongSelf cancelConnectTimeoutTimerLocked];
            strongSelf->_connecting = NO;
            strongSelf->_connected = YES;
            [strongSelf postNotificationOnMain:kJPFNetworkDidSetupNotification userInfo:nil];
            [strongSelf receiveLoopForConnection:connection];
            [strongSelf performHandshakeLocked];
        } else if (state == nw_connection_state_waiting || state == nw_connection_state_failed) {
            BOOL wasConnected = strongSelf->_connected;
            if (!wasConnected) {
                [strongSelf advanceToNextCandidateAfterFailureOfEndpoint:endpoint];
            } else {
                strongSelf->_connecting = NO;
                [strongSelf tearDownConnectionLocked];
                [strongSelf postNotificationOnMain:kJPFNetworkDidCloseNotification userInfo:nil];
                if (!strongSelf->_userDisconnected && ![JCOREConfiguration shared].pushStopped && !strongSelf->_connecting) {
                    [strongSelf scheduleReconnectLocked];
                }
            }
        } else if (state == nw_connection_state_cancelled) {
            BOOL wasConnected = strongSelf->_connected;
            strongSelf->_connected = NO;
            strongSelf->_loggedIn = NO;
            [strongSelf stopHeartbeatLocked];
            if (strongSelf->_inFlightRequests.count > 0) {
                NSError *closeErr = [NSError errorWithDomain:@"cn.jiguang.jpush"
                                                        code:6002
                                                    userInfo:@{NSLocalizedDescriptionKey: @"Connection closed"}];
                [strongSelf failAllInFlightRequestsLockedWithError:closeErr];
            }
            if (wasConnected && strongSelf->_connection == connection) {
                [strongSelf postNotificationOnMain:kJPFNetworkDidCloseNotification userInfo:nil];
            }
        }
    });

    nw_connection_start(connection);
}

- (void)startConnectTimeoutTimerLockedForConnection:(nw_connection_t)connection
                                           endpoint:(JCORESisEndpoint *)endpoint {
    [self cancelConnectTimeoutTimerLocked];
    NSTimeInterval timeout = [JCOREConfiguration shared].connectTimeoutInterval;
    if (timeout <= 0) {
        timeout = (_candidateEndpoints.count > 1) ? 1.5 : 5.0;
    } else if (_candidateEndpoints.count > 1 && timeout > 1.5) {
        timeout = 1.5;
    }
    uint64_t timeoutNs = (uint64_t)(timeout * (double)NSEC_PER_SEC);
    dispatch_source_t timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, _sessionQueue);
    dispatch_source_set_timer(timer,
                              dispatch_time(DISPATCH_TIME_NOW, (int64_t)timeoutNs),
                              DISPATCH_TIME_FOREVER,
                              (uint64_t)(0.05 * NSEC_PER_SEC));
    __weak typeof(self) weakSelf = self;
    dispatch_source_set_event_handler(timer, ^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || strongSelf->_connection != connection || strongSelf->_connected) {
            return;
        }
        [strongSelf advanceToNextCandidateAfterFailureOfEndpoint:endpoint];
    });
    _connectTimeoutTimer = timer;
    dispatch_resume(timer);
}

- (void)cancelConnectTimeoutTimerLocked {
    if (_connectTimeoutTimer != nil) {
        dispatch_source_cancel(_connectTimeoutTimer);
        _connectTimeoutTimer = nil;
    }
}

- (void)scheduleReconnectLocked {
    [self cancelReconnectTimerLocked];
    if (_userDisconnected || [JCOREConfiguration shared].pushStopped || [JCOREConfiguration shared].appKey.length == 0) {
        return;
    }
    NSTimeInterval base = [JCOREConfiguration shared].reconnectBaseInterval;
    if (base <= 0) {
        base = 3.0;
    }
    double multiplier = pow(2.0, (double)MIN(_reconnectAttempt, (NSUInteger)6));
    NSTimeInterval delay = MIN(base * multiplier, 120.0);
    _reconnectAttempt++;

    uint64_t delayNs = (uint64_t)(delay * (double)NSEC_PER_SEC);
    dispatch_source_t timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, _sessionQueue);
    dispatch_source_set_timer(timer,
                              dispatch_time(DISPATCH_TIME_NOW, (int64_t)delayNs),
                              DISPATCH_TIME_FOREVER,
                              (uint64_t)(0.05 * NSEC_PER_SEC));
    __weak typeof(self) weakSelf = self;
    dispatch_source_set_event_handler(timer, ^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) {
            return;
        }
        strongSelf->_reconnectTimer = nil;
        if (!strongSelf->_userDisconnected && !strongSelf->_connected && !strongSelf->_connecting) {
            [strongSelf startLockedForceReconnect:NO];
        }
    });
    _reconnectTimer = timer;
    dispatch_resume(timer);
}

- (void)cancelReconnectTimerLocked {
    if (_reconnectTimer != nil) {
        dispatch_source_cancel(_reconnectTimer);
        _reconnectTimer = nil;
    }
}

- (void)performHandshakeLocked {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    if (config.uid == 0 || config.password.length == 0 || config.registrationID.length == 0) {
        [self sendRegisterRequestLocked];
    } else {
        // Registered Device Fast-Login: skip Register (cmd=0) and send Login (cmd=1) directly
        [self sendLoginRequestLocked];
    }
}

- (void)sendRegisterRequestLocked {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSString *bundleId = [config bundleIdentifierString];
    NSData *regBody = [JCOREWireProtocol buildRegisterPayloadWithAppKey:config.appKey ?: @""
                                                                channel:config.channel
                                                           bundleIdHash:bundleId];
    nw_connection_t activeConnection = _connection;
    __weak typeof(self) weakSelf = self;
    [self sendRawCommandLocked:JCOREWireCommandRegister
                       version:30
                      bodyData:regBody
                    completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || strongSelf->_connection != activeConnection) {
            return;
        }
        JCORERegisterResponse *parsed = [JCOREWireProtocol parseRegisterResponse:responseFrame];
        if (error == nil && parsed != nil && parsed.statusCode == 0 && parsed.uid > 0 && parsed.registrationID.length > 0) {
            [config updateRegistrationWithUid:parsed.uid
                                     password:parsed.password ?: @""
                               registrationID:parsed.registrationID
                                     deviceID:parsed.deviceID];
            [JCOREConfiguration logInfo:[NSString stringWithFormat:@"TCP register success uid=%llu registrationId=%@",
                                         parsed.uid, parsed.registrationID]];
            [strongSelf postNotificationOnMain:kJPFNetworkDidRegisterNotification
                                      userInfo:@{
                                          @"registrationID": parsed.registrationID,
                                          @"uid": @(parsed.uid),
                                      }];
            [strongSelf sendLoginRequestLocked];
        } else {
            uint16_t errCode = parsed != nil ? parsed.statusCode : 1011;
            [JCOREConfiguration logWarn:[NSString stringWithFormat:@"TCP register failed code=%u", errCode]];
            [strongSelf postNotificationOnMain:kJPFNetworkFailedRegisterNotification
                                      userInfo:@{@"code": @(errCode)}];
            JCORESisEndpoint *currentEndpoint = (strongSelf->_currentEndpointIndex < strongSelf->_candidateEndpoints.count)
                ? strongSelf->_candidateEndpoints[strongSelf->_currentEndpointIndex]
                : nil;
            [strongSelf advanceToNextCandidateAfterFailureOfEndpoint:currentEndpoint];
        }
    }];
}

- (void)sendLoginRequestLocked {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSData *loginBody = [JCOREWireProtocol buildLoginPayloadWithPassword:config.password ?: @""
                                                                  appKey:config.appKey ?: @""];
    nw_connection_t activeConnection = _connection;
    __weak typeof(self) weakSelf = self;
    [self sendRawCommandLocked:JCOREWireCommandLogin
                       version:27
                      bodyData:loginBody
                    completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || strongSelf->_connection != activeConnection) {
            return;
        }
        JCORESisEndpoint *currentEndpoint = (strongSelf->_currentEndpointIndex < strongSelf->_candidateEndpoints.count)
            ? strongSelf->_candidateEndpoints[strongSelf->_currentEndpointIndex]
            : nil;
        if (error != nil || responseFrame == nil) {
            [strongSelf advanceToNextCandidateAfterFailureOfEndpoint:currentEndpoint];
            return;
        }
        JCORELoginResponse *login = [JCOREWireProtocol parseLoginResponse:responseFrame];
        if (login != nil && login.statusCode == 108) {
            // Status 108: uid unregistered/invalid on server -> clear credentials and re-register immediately
            [config resetRegistration];
            [strongSelf sendRegisterRequestLocked];
            return;
        }
        if (login != nil && login.statusCode == 1012) {
            // Status 1012: server requests SIS cache reset and re-resolution
            [config resetSisCache];
            [strongSelf startLockedForceReconnect:YES];
            return;
        }
        if (login != nil && login.statusCode != 0) {
            [strongSelf postNotificationOnMain:kJPFServiceErrorNotification
                                      userInfo:@{@"code": @(login.statusCode)}];
            [strongSelf advanceToNextCandidateAfterFailureOfEndpoint:currentEndpoint];
            return;
        }

        if (login != nil) {
            strongSelf->_sessionId = login.sessionId;
            strongSelf->_serverVersion = login.serverVersion;
            strongSelf->_serverTimestamp = login.serverTimestamp;
        }
        strongSelf->_loggedIn = YES;
        strongSelf->_reconnectAttempt = 0;
        [config clearFailedIps];
        [JCOREConfiguration logInfo:[NSString stringWithFormat:@"TCP login success uid=%llu sid=%u registrationId=%@",
                                     config.uid,
                                     strongSelf->_sessionId,
                                     config.registrationID ?: @""]];
        [strongSelf startHeartbeatLocked];
        [strongSelf reportDeviceTokenLocked];
        [strongSelf reportVoipTokenLocked];
        [strongSelf flushQueuedRequestsLocked];
        [strongSelf postNotificationOnMain:kJPFNetworkDidLoginNotification
                                  userInfo:@{
                                      @"registrationID": config.registrationID ?: @"",
                                      @"sessionId": @(strongSelf->_sessionId),
                                      @"serverVersion": @(strongSelf->_serverVersion),
                                      @"serverTimestamp": @(strongSelf->_serverTimestamp),
                                  }];
    }];
}

- (void)startHeartbeatLocked {
    [self stopHeartbeatLocked];
    NSTimeInterval interval = [JCOREConfiguration shared].heartbeatInterval;
    if (interval <= 0) {
        interval = 240.0;
    }
    uint64_t intervalNs = (uint64_t)(interval * (double)NSEC_PER_SEC);
    dispatch_source_t timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, _sessionQueue);
    dispatch_source_set_timer(timer,
                              dispatch_time(DISPATCH_TIME_NOW, (int64_t)intervalNs),
                              intervalNs,
                              (uint64_t)(1 * NSEC_PER_SEC));
    __weak typeof(self) weakSelf = self;
    dispatch_source_set_event_handler(timer, ^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || !strongSelf->_connected || !strongSelf->_loggedIn) {
            return;
        }
        NSData *hb = [JCOREWireProtocol buildHeartbeatPayloadWithPushStopped:[JCOREConfiguration shared].pushStopped];
        [strongSelf sendRawCommandLocked:JCOREWireCommandHeartbeat version:8 bodyData:hb completion:nil];
    });
    _heartbeatTimer = timer;
    dispatch_resume(timer);
}

- (void)stopHeartbeatLocked {
    if (_heartbeatTimer != nil) {
        dispatch_source_cancel(_heartbeatTimer);
        _heartbeatTimer = nil;
    }
}

- (void)sendHeartbeatNowWithCompletion:(JCORETcpResponseHandler)completion {
    dispatch_async(_sessionQueue, ^{
        NSData *hb = [JCOREWireProtocol buildHeartbeatPayloadWithPushStopped:[JCOREConfiguration shared].pushStopped];
        [self sendRawCommandLocked:JCOREWireCommandHeartbeat version:8 bodyData:hb completion:completion];
    });
}

- (void)receiveLoopForConnection:(nw_connection_t)connection {
    __weak typeof(self) weakSelf = self;
    nw_connection_receive(connection, 1, 65536, ^(dispatch_data_t content, nw_content_context_t context, bool is_complete, nw_error_t error) {
        (void)context;
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || strongSelf->_connection != connection) {
            return;
        }
        if (content != nil) {
            NSData *chunk = (NSData *)content;
            if (chunk.length > 0) {
                [strongSelf->_receiveBuffer appendData:chunk];
                NSArray<JCOREWireFrame *> *frames = [JCOREWireProtocol decodeServerFramesFromStreamBuffer:strongSelf->_receiveBuffer
                                                                                               defaultUid:[JCOREConfiguration shared].uid];
                for (JCOREWireFrame *frame in frames) {
                    [strongSelf handleDecodedFrameLocked:frame];
                }
            }
        }
        if (error == nil && !is_complete) {
            [strongSelf receiveLoopForConnection:connection];
        } else if (is_complete || error != nil) {
            BOOL wasConnected = strongSelf->_connected;
            strongSelf->_connecting = NO;
            strongSelf->_connected = NO;
            strongSelf->_loggedIn = NO;
            [strongSelf stopHeartbeatLocked];
            if (strongSelf->_inFlightRequests.count > 0) {
                NSError *closeErr = [NSError errorWithDomain:@"cn.jiguang.jpush"
                                                        code:6002
                                                    userInfo:@{NSLocalizedDescriptionKey: @"Connection closed"}];
                [strongSelf failAllInFlightRequestsLockedWithError:closeErr];
            }
            if (wasConnected && strongSelf->_connection == connection) {
                [strongSelf postNotificationOnMain:kJPFNetworkDidCloseNotification userInfo:nil];
                if (!strongSelf->_userDisconnected && ![JCOREConfiguration shared].pushStopped && !strongSelf->_connecting) {
                    [strongSelf scheduleReconnectLocked];
                }
            }
        }
    });
}

- (void)handleDecodedFrameLocked:(JCOREWireFrame *)frame {
    if (frame.command == JCOREWireCommandPushMessage) {
        JCOREPushEnvelope *envelope = [JCOREWireProtocol parsePushEnvelope:frame];
        if (envelope != nil) {
            [JCOREConfiguration logInfo:[NSString stringWithFormat:@"TCP push message received msgType=%u msgId=%llu",
                                         envelope.messageType, envelope.messageId]];
            [self sendReceiptLockedForMessageType:envelope.messageType
                                        messageId:envelope.messageId
                                        requestId:frame.requestId];
            NSMutableDictionary *payload = [NSMutableDictionary dictionary];
            if ([envelope.jsonPayload isKindOfClass:[NSDictionary class]]) {
                [payload addEntriesFromDictionary:envelope.jsonPayload];
            }
            if (payload[@"_j_msgid"] == nil) {
                payload[@"_j_msgid"] = @(envelope.messageId);
            }
            if (payload[@"content"] == nil && envelope.content != nil) {
                payload[@"content"] = envelope.content;
            }
            [self postNotificationOnMain:kJPFNetworkDidReceiveMessageNotification userInfo:[payload copy]];
        }
        return;
    }

    if (frame.command == JCOREWireCommandCtrlCommand) {
        JCORECtrlEnvelope *ctrl = [JCOREWireProtocol parseCtrlEnvelope:frame];
        if (ctrl != nil) {
            NSData *ackBody = [JCOREWireProtocol buildCtrlAckPayloadWithStatusCode:0 requestId:ctrl.requestId];
            [self sendPacketWithExplicitRequestIdLocked:(frame.requestId != 0 ? frame.requestId : [[JCOREConfiguration shared] nextRequestId])
                                                command:JCOREWireCommandCtrlCommand
                                                version:1
                                               bodyData:ackBody];
            NSMutableDictionary *info = [NSMutableDictionary dictionary];
            info[@"requestId"] = @(ctrl.requestId);
            if (ctrl.jsonPayload != nil) {
                info[@"payload"] = ctrl.jsonPayload;
            }
            [self postNotificationOnMain:@"kJPFNetworkDidReceiveCtrlCommandNotification" userInfo:info];
        }
        return;
    }

    NSNumber *key = @(frame.requestId);
    JCOREInFlightRequest *inFlight = _inFlightRequests[key];
    if (inFlight != nil) {
        [inFlight cancelTimers];
        [_inFlightRequests removeObjectForKey:key];
        if (inFlight.completion != nil) {
            inFlight.completion(frame, nil);
        }
    }
}

- (void)reportDeviceTokenIfNeeded {
    dispatch_async(_sessionQueue, ^{
        [self reportDeviceTokenLocked];
    });
}

- (void)reportDeviceTokenLocked {
    NSString *tokenHex = [JCOREConfiguration shared].deviceTokenHex;
    if (tokenHex.length == 0) {
        return;
    }
    if (!_connected || !_loggedIn) {
        return;
    }
    if ([_lastReportedDeviceTokenHex isEqualToString:tokenHex]) {
        return;
    }
    _lastReportedDeviceTokenHex = [tokenHex copy];
    NSData *body = [JCOREWireProtocol buildDeviceTokenPayloadWithTokenHex:tokenHex];
    [self sendRawCommandLocked:JCOREWireCommandDeviceToken version:3 bodyData:body completion:nil];
}

- (void)reportVoipTokenIfNeeded {
    dispatch_async(_sessionQueue, ^{
        [self reportVoipTokenLocked];
    });
}

- (void)reportVoipTokenLocked {
    NSString *voipHex = [JCOREConfiguration shared].voipTokenHex;
    if (voipHex.length == 0) {
        return;
    }
    if (!_connected || !_loggedIn) {
        return;
    }
    if ([_lastReportedVoipTokenHex isEqualToString:voipHex]) {
        return;
    }
    _lastReportedVoipTokenHex = [voipHex copy];
    NSData *body = [JCOREWireProtocol buildVoipTokenPayloadWithTokenHex:voipHex];
    [self sendRawCommandLocked:JCOREWireCommandVoipToken version:2 bodyData:body completion:nil];
}

- (void)sendReceiptForMessageType:(uint8_t)messageType messageId:(uint64_t)messageId {
    dispatch_async(_sessionQueue, ^{
        [self sendReceiptLockedForMessageType:messageType messageId:messageId requestId:0];
    });
}

- (void)sendReceiptLockedForMessageType:(uint8_t)messageType
                              messageId:(uint64_t)messageId
                              requestId:(uint16_t)requestId {
    if (!_connected || messageId == 0) {
        return;
    }
    NSData *body = [JCOREWireProtocol buildMessageAckPayloadWithMessageType:messageType messageId:messageId];
    uint16_t rid = (requestId != 0) ? requestId : [[JCOREConfiguration shared] nextRequestId];
    [self sendPacketWithExplicitRequestIdLocked:rid
                                        command:JCOREWireCommandMessageAck
                                        version:3
                                       bodyData:body];
}

- (void)sendPacketWithExplicitRequestIdLocked:(uint16_t)rid
                                      command:(uint8_t)command
                                      version:(uint8_t)version
                                     bodyData:(NSData *)bodyData {
    if (!_connected || _connection == nil) {
        return;
    }
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSData *packet = [JCOREWireProtocol encodeFrameWithCommand:command
                                                       version:version
                                                     requestId:rid
                                                     sessionId:_sessionId
                                                           uid:config.uid
                                                      bodyData:bodyData];
    if (packet.length == 0) {
        return;
    }
    dispatch_data_t dispatchPacket = dispatch_data_create(packet.bytes,
                                                          packet.length,
                                                          _sessionQueue,
                                                          DISPATCH_DATA_DESTRUCTOR_DEFAULT);
    nw_connection_send(_connection, dispatchPacket, NW_CONNECTION_DEFAULT_MESSAGE_CONTEXT, true, ^(nw_error_t error) {
        (void)error;
    });
}

- (void)sendCommand:(uint8_t)command
            version:(uint8_t)version
           bodyData:(NSData *)bodyData
         completion:(JCORETcpResponseHandler)completion {
    dispatch_async(_sessionQueue, ^{
        if (command != JCOREWireCommandRegister &&
            command != JCOREWireCommandLogin &&
            (!self->_connected || !self->_loggedIn)) {
            JCOREQueuedWireRequest *queued = [[JCOREQueuedWireRequest alloc] init];
            queued.command = command;
            queued.version = version;
            queued.bodyData = bodyData ?: [NSData data];
            queued.completion = completion;
            [self->_queuedRequests addObject:queued];
            if (!self->_connected && !self->_connecting && !self->_userDisconnected) {
                [self startLockedForceReconnect:NO];
            }
            return;
        }
        [self sendRawCommandLocked:command version:version bodyData:bodyData completion:completion];
    });
}

- (void)flushQueuedRequestsLocked {
    if (_queuedRequests.count == 0) {
        return;
    }
    NSArray<JCOREQueuedWireRequest *> *toSend = [_queuedRequests copy];
    [_queuedRequests removeAllObjects];
    for (JCOREQueuedWireRequest *item in toSend) {
        [self sendRawCommandLocked:item.command
                           version:item.version
                          bodyData:item.bodyData
                        completion:item.completion];
    }
}

- (void)sendRawCommandLocked:(uint8_t)command
                     version:(uint8_t)version
                    bodyData:(NSData *)bodyData
                  completion:(JCORETcpResponseHandler)completion {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    if (!_connected || _connection == nil) {
        if (completion != nil) {
            NSError *err = [NSError errorWithDomain:@"cn.jiguang.jpush"
                                               code:6002
                                           userInfo:@{NSLocalizedDescriptionKey: @"Not connected"}];
            completion(nil, err);
        }
        return;
    }

    uint16_t rid = [config nextRequestId];
    uint64_t packetUid = (command == JCOREWireCommandRegister) ? 0ULL : config.uid;
    NSData *packet = [JCOREWireProtocol encodeFrameWithCommand:command
                                                       version:version
                                                     requestId:rid
                                                     sessionId:_sessionId
                                                           uid:packetUid
                                                      bodyData:bodyData];
    if (packet.length == 0) {
        if (completion != nil) {
            NSError *err = [NSError errorWithDomain:@"cn.jiguang.jcore" code:-1002 userInfo:nil];
            completion(nil, err);
        }
        return;
    }

    if (completion != nil) {
        NSNumber *ridKey = @(rid);
        JCOREInFlightRequest *inFlight = [[JCOREInFlightRequest alloc] init];
        inFlight.requestId = rid;
        inFlight.command = command;
        inFlight.version = version;
        inFlight.packetData = packet;
        inFlight.completion = completion;

        NSTimeInterval timeoutSec = config.requestTimeoutInterval > 0 ? config.requestTimeoutInterval : 20.0;
        NSTimeInterval resendSec = config.requestResendInterval > 0 ? config.requestResendInterval : 10.0;

        if (resendSec > 0 && resendSec < timeoutSec) {
            uint64_t resendNs = (uint64_t)(resendSec * (double)NSEC_PER_SEC);
            dispatch_source_t rTimer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, _sessionQueue);
            dispatch_source_set_timer(rTimer,
                                      dispatch_time(DISPATCH_TIME_NOW, (int64_t)resendNs),
                                      DISPATCH_TIME_FOREVER,
                                      (uint64_t)(0.05 * NSEC_PER_SEC));
            __weak typeof(self) weakSelf = self;
            dispatch_source_set_event_handler(rTimer, ^{
                __strong typeof(weakSelf) strongSelf = weakSelf;
                if (!strongSelf) {
                    return;
                }
                JCOREInFlightRequest *req = strongSelf->_inFlightRequests[ridKey];
                if (req != nil && strongSelf->_connected && strongSelf->_connection != nil) {
                    dispatch_data_t resendData = dispatch_data_create(req.packetData.bytes,
                                                                      req.packetData.length,
                                                                      strongSelf->_sessionQueue,
                                                                      DISPATCH_DATA_DESTRUCTOR_DEFAULT);
                    nw_connection_send(strongSelf->_connection, resendData, NW_CONNECTION_DEFAULT_MESSAGE_CONTEXT, true, ^(nw_error_t error) {
                        (void)error;
                    });
                }
            });
            inFlight.resendTimer = rTimer;
            dispatch_resume(rTimer);
        }

        uint64_t timeoutNs = (uint64_t)(timeoutSec * (double)NSEC_PER_SEC);
        dispatch_source_t tTimer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, _sessionQueue);
        dispatch_source_set_timer(tTimer,
                                  dispatch_time(DISPATCH_TIME_NOW, (int64_t)timeoutNs),
                                  DISPATCH_TIME_FOREVER,
                                  (uint64_t)(0.05 * NSEC_PER_SEC));
        __weak typeof(self) weakSelf = self;
        dispatch_source_set_event_handler(tTimer, ^{
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if (!strongSelf) {
                return;
            }
            JCOREInFlightRequest *timedOut = strongSelf->_inFlightRequests[ridKey];
            if (timedOut != nil) {
                [timedOut cancelTimers];
                [strongSelf->_inFlightRequests removeObjectForKey:ridKey];
                if (timedOut.completion != nil) {
                    NSError *timeoutErr = [NSError errorWithDomain:@"cn.jiguang.jpush"
                                                              code:6002
                                                          userInfo:@{NSLocalizedDescriptionKey: @"Request timed out"}];
                    timedOut.completion(nil, timeoutErr);
                }
            }
        });
        inFlight.timeoutTimer = tTimer;
        dispatch_resume(tTimer);

        _inFlightRequests[ridKey] = inFlight;
    }

    dispatch_data_t dispatchPacket = dispatch_data_create(packet.bytes,
                                                          packet.length,
                                                          _sessionQueue,
                                                          DISPATCH_DATA_DESTRUCTOR_DEFAULT);
    nw_connection_send(_connection, dispatchPacket, NW_CONNECTION_DEFAULT_MESSAGE_CONTEXT, true, ^(nw_error_t error) {
        (void)error;
    });
}

- (void)failAllInFlightRequestsLockedWithError:(NSError *)error {
    NSDictionary<NSNumber *, JCOREInFlightRequest *> *snapshot = [_inFlightRequests copy];
    [_inFlightRequests removeAllObjects];
    [snapshot enumerateKeysAndObjectsUsingBlock:^(NSNumber *key, JCOREInFlightRequest *req, BOOL *stop) {
        (void)key;
        (void)stop;
        [req cancelTimers];
        if (req.completion != nil) {
            req.completion(nil, error);
        }
    }];
}

- (void)tearDownConnectionLocked {
    [self cancelConnectTimeoutTimerLocked];
    [self stopHeartbeatLocked];
    if (_connection != nil) {
        nw_connection_cancel(_connection);
        _connection = nil;
    }
    [_receiveBuffer setLength:0];
    _connected = NO;
    _loggedIn = NO;
    if (_inFlightRequests.count > 0) {
        NSError *err = [NSError errorWithDomain:@"cn.jiguang.jpush"
                                           code:6002
                                       userInfo:@{NSLocalizedDescriptionKey: @"Connection closed"}];
        [self failAllInFlightRequestsLockedWithError:err];
    }
}

- (void)disconnect {
    dispatch_async(_sessionQueue, ^{
        self->_userDisconnected = YES;
        [self cancelReconnectTimerLocked];
        BOOL wasConnected = self->_connected;
        self->_connecting = NO;
        [self tearDownConnectionLocked];
        NSError *err = [NSError errorWithDomain:@"cn.jiguang.jpush"
                                           code:6002
                                       userInfo:@{NSLocalizedDescriptionKey: @"Disconnected"}];
        [self failAllInFlightRequestsLockedWithError:err];
        if (wasConnected) {
            [self postNotificationOnMain:kJPFNetworkDidCloseNotification userInfo:nil];
        }
    });
}

@end

@implementation JCORETcpClient
@end
