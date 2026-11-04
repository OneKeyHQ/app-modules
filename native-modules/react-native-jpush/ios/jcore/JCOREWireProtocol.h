#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(uint8_t, JCOREWireCommand) {
    JCOREWireCommandRegister = 0,
    JCOREWireCommandLogin = 1,
    JCOREWireCommandHeartbeat = 2,
    JCOREWireCommandPushMessage = 3,
    JCOREWireCommandMessageAck = 4,
    JCOREWireCommandIdcRedirect = 10,
    JCOREWireCommandDeviceToken = 13,
    JCOREWireCommandCtrlCommand = 25,
    JCOREWireCommandMobileNumber = 26,
    JCOREWireCommandTagOperation = 28,
    JCOREWireCommandAliasOperation = 29,
    JCOREWireCommandPushCtrl = 30,
    JCOREWireCommandVoipToken = 33,
    JCOREWireCommandPropertyOperation = 38,
    JCOREWireCommandLiveActivity = 39,
};

@interface JCOREWireFrame : NSObject

@property (nonatomic, assign) uint8_t version;
@property (nonatomic, assign) uint8_t command;
@property (nonatomic, assign) uint8_t algorithm;
@property (nonatomic, assign) uint8_t flags;
@property (nonatomic, assign) uint16_t requestId;
@property (nonatomic, assign) uint32_t sessionId;
@property (nonatomic, assign) uint64_t uid;
@property (nonatomic, assign) uint16_t statusCode;
@property (nonatomic, strong, nullable) NSData *bodyData;

@end

@interface JCORERegisterResponse : NSObject

@property (nonatomic, assign) uint16_t statusCode;
@property (nonatomic, assign) uint64_t uid;
@property (nonatomic, copy, nullable) NSString *password;
@property (nonatomic, copy, nullable) NSString *registrationID;
@property (nonatomic, copy, nullable) NSString *deviceID;

@end

@interface JCORELoginResponse : NSObject

@property (nonatomic, assign) uint16_t statusCode;
@property (nonatomic, assign) uint32_t sessionId;
@property (nonatomic, assign) uint16_t serverVersion;
@property (nonatomic, copy, nullable) NSString *errorMessage;
@property (nonatomic, assign) uint32_t serverTimestamp;
@property (nonatomic, assign) int8_t idc;

@end

@interface JCOREPushEnvelope : NSObject

@property (nonatomic, assign) uint8_t messageType;
@property (nonatomic, assign) uint64_t messageId;
@property (nonatomic, copy, nullable) NSString *content;
@property (nonatomic, copy, nullable) NSDictionary *jsonPayload;

@end

@interface JCORECtrlEnvelope : NSObject

@property (nonatomic, assign) uint64_t requestId;
@property (nonatomic, copy, nullable) NSString *rawJson;
@property (nonatomic, copy, nullable) NSDictionary *jsonPayload;

@end

@interface JCORELiveActivityResponse : NSObject

@property (nonatomic, assign) uint16_t statusCode;
@property (nonatomic, assign) NSInteger mappedErrorCode;
@property (nonatomic, copy, nullable) NSString *alias;

@end

@interface JCOREWireProtocol : NSObject

// Outbound Client -> Server (24-byte header)
+ (nullable NSData *)encodeClientFrameWithCommand:(uint8_t)command
                                          version:(uint8_t)version
                                        requestId:(uint16_t)requestId
                                        sessionId:(uint32_t)sessionId
                                              uid:(uint64_t)uid
                                        algorithm:(uint8_t)algorithm
                                         bodyData:(nullable NSData *)bodyData;

+ (nullable NSData *)encodeFrameWithCommand:(uint8_t)command
                                    version:(uint8_t)version
                                  requestId:(uint16_t)requestId
                                  sessionId:(uint32_t)sessionId
                                        uid:(uint64_t)uid
                                   bodyData:(nullable NSData *)bodyData;

// Inbound Server -> Client (20-byte header)
+ (nullable NSData *)encodeServerFrameWithCommand:(uint8_t)command
                                          version:(uint8_t)version
                                        requestId:(uint16_t)requestId
                                              uid:(uint64_t)uid
                                        algorithm:(uint8_t)algorithm
                                       statusCode:(uint16_t)statusCode
                                         bodyData:(nullable NSData *)bodyData;

+ (nullable NSData *)encodeFrameWithCommand:(uint8_t)command
                                    version:(uint8_t)version
                                  requestId:(uint16_t)requestId
                                  sessionId:(uint32_t)sessionId
                                        uid:(uint64_t)uid
                                 statusCode:(uint16_t)statusCode
                                   bodyData:(nullable NSData *)bodyData;

// Stream Decoders
+ (NSArray<JCOREWireFrame *> *)decodeServerFramesFromStreamBuffer:(NSMutableData *)streamBuffer
                                                       defaultUid:(uint64_t)defaultUid;

+ (NSArray<JCOREWireFrame *> *)decodeClientFramesFromStreamBuffer:(NSMutableData *)streamBuffer;

+ (NSArray<JCOREWireFrame *> *)decodeFramesFromStreamBuffer:(NSMutableData *)streamBuffer
                                                        uid:(uint64_t)uid;

// Register (cmd = 0, ver = 30)
+ (NSData *)buildRegisterPayloadWithAppKey:(NSString *)appKey
                                   channel:(NSString *)channel
                              bundleIdHash:(NSString *)bundleId;

+ (NSData *)buildRegisterResponsePayloadWithStatusCode:(uint16_t)statusCode
                                                   uid:(uint64_t)uid
                                              password:(nullable NSString *)password
                                        registrationID:(nullable NSString *)registrationID
                                              deviceID:(nullable NSString *)deviceID;

+ (NSData *)buildRegisterResponsePayloadWithUid:(uint64_t)uid
                                       password:(NSString *)password
                                 registrationID:(NSString *)registrationID;

+ (nullable JCORERegisterResponse *)parseRegisterResponse:(JCOREWireFrame *)frame;

// Login (cmd = 1, ver = 27)
+ (NSData *)buildLoginPayloadWithPassword:(NSString *)password
                                   appKey:(NSString *)appKey;

+ (NSData *)buildLoginResponsePayloadWithStatusCode:(uint16_t)statusCode
                                          sessionId:(uint32_t)sessionId
                                      serverVersion:(uint16_t)serverVersion
                                       errorMessage:(nullable NSString *)errorMessage
                                    serverTimestamp:(uint32_t)serverTimestamp
                                                idc:(int8_t)idc;

+ (NSData *)buildLoginResponsePayloadWithSessionId:(uint32_t)sessionId
                                     serverVersion:(uint16_t)serverVersion
                                   serverTimestamp:(uint32_t)serverTimestamp;

+ (nullable JCORELoginResponse *)parseLoginResponse:(JCOREWireFrame *)frame;

// Heartbeat (cmd = 2, ver = 8)
+ (NSData *)buildHeartbeatPayloadWithPushStopped:(BOOL)pushStopped;

// PushMessage (cmd = 3, ver = 4) & MessageReceipt (cmd = 4, ver = 3)
+ (NSData *)buildPushMessagePayloadWithMessageType:(uint8_t)messageType
                                         messageId:(uint64_t)messageId
                                           content:(NSString *)content;

+ (nullable JCOREPushEnvelope *)parsePushEnvelope:(JCOREWireFrame *)frame;

+ (NSData *)buildMessageAckPayloadWithMessageType:(uint8_t)messageType
                                        messageId:(uint64_t)messageId;

+ (NSData *)buildMessageAckPayloadWithStatusCode:(uint16_t)statusCode
                                     messageType:(uint8_t)messageType
                                       messageId:(uint64_t)messageId
                                         msgData:(nullable NSString *)msgData;

+ (BOOL)parseMessageAckFrame:(JCOREWireFrame *)frame
              outMessageType:(uint8_t *_Nullable)outMessageType
                outMessageId:(uint64_t *_Nullable)outMessageId;

// DeviceToken (cmd = 13, ver = 3)
+ (NSData *)buildDeviceTokenPayloadWithTokenHex:(NSString *)tokenHex;
+ (nullable NSString *)parseDeviceTokenFromFrame:(JCOREWireFrame *)frame;

// VoIP Token (cmd = 33, ver = 2)
+ (NSData *)buildVoipTokenPayloadWithTokenHex:(NSString *)tokenHex;
+ (nullable NSString *)parseVoipTokenFromFrame:(JCOREWireFrame *)frame;

// LiveActivity Token (cmd = 39, ver = 1)
+ (NSData *)buildLiveActivityPayloadWithTokenHex:(NSString *)tokenHex
                           aliasOrAttributesType:(NSString *)aliasOrAttributesType
                                     isPushToken:(BOOL)isPushToken;

+ (NSData *)buildLiveActivityResponsePayloadWithStatusCode:(uint16_t)statusCode
                                                     alias:(nullable NSString *)alias;

+ (nullable JCORELiveActivityResponse *)parseLiveActivityResponse:(JCOREWireFrame *)frame;
+ (NSInteger)mapLiveActivityStatusCode:(uint16_t)statusCode;

// CtrlCommand (cmd = 25, ver = 1)
+ (NSData *)buildCtrlCommandPayloadWithRequestId:(uint64_t)requestId
                                     jsonPayload:(NSDictionary *)jsonPayload;
+ (nullable JCORECtrlEnvelope *)parseCtrlEnvelope:(JCOREWireFrame *)frame;
+ (NSData *)buildCtrlAckPayloadWithStatusCode:(uint16_t)statusCode
                                    requestId:(uint64_t)requestId;

// User Property / MobileNumber / SDKVersion (cmd = 26, ver = 0)
+ (NSData *)buildMobileNumberPayload:(NSString *)mobileNumber;
+ (nullable NSString *)parseMobileNumberFromFrame:(JCOREWireFrame *)frame;
+ (NSData *)buildSdkVersionPropertyPayload:(NSString *)sdkVersion;

// Tags (cmd = 28, ver = 1) & Alias (cmd = 29, ver = 1)
+ (NSData *)buildTagOperationPayloadWithOperator:(NSString *)opString
                                            tags:(nullable NSArray<NSString *> *)tags
                                        currPage:(NSInteger)currPage;

+ (NSData *)buildAliasOperationPayloadWithOperator:(NSString *)opString
                                             alias:(nullable NSString *)alias;

// User Properties (cmd = 38, ver = 1)
+ (NSData *)buildUserPropertiesPayloadWithOperator:(NSString *)opString
                                        properties:(nullable NSDictionary *)properties;

+ (NSData *)buildUserPropertiesResponsePayloadWithStatusCode:(uint16_t)statusCode
                                                 jsonPayload:(nullable NSDictionary *)jsonPayload;

+ (NSData *)buildJsonCommandPayload:(NSDictionary *)payload;
+ (nullable NSDictionary *)parseJsonBodyFromFrame:(JCOREWireFrame *)frame;

// Push Control / Notification State (cmd = 30, ver = 1)
+ (NSData *)buildPushControlPayloadWithStopped:(BOOL)stopped;

@end

@interface JCOREHttpReporter : NSObject

+ (instancetype)shared;

+ (NSDictionary *)buildReportEnvelopeWithContentItems:(NSArray<NSDictionary *> *)items;

+ (nullable NSURLRequest *)buildHttpReportRequestWithURL:(NSURL *)url
                                            contentItems:(NSArray<NSDictionary *> *)items
                                                     uid:(uint64_t)uid
                                                password:(NSString *)password
                                                  appKey:(NSString *)appKey
                                               outAesKey:(NSString *_Nullable *_Nullable)outAesKey;

+ (nullable NSDictionary *)verifyAndDecryptHttpReportBody:(NSData *)bodyData
                                      authorizationHeader:(NSString *)authHeader
                                              expectedUid:(uint64_t)expectedUid
                                                 password:(NSString *)password;

+ (nullable NSURLRequest *)buildBadgeReportRequestWithBaseURL:(nullable NSURL *)baseURL
                                                   badgeValue:(NSInteger)badgeValue
                                                          uid:(uint64_t)uid
                                                       appKey:(NSString *)appKey
                                                  outAesKey16:(NSString *_Nullable *_Nullable)outAesKey16;

+ (nullable NSDictionary *)verifyAndDecryptBadgeReportHeaders:(NSDictionary<NSString *, NSString *> *)headers
                                                   bodyString:(nullable NSString *)bodyString
                                                       appKey:(NSString *)appKey
                                                     aesKey16:(nullable NSString *)aesKey16;

- (void)sendHttpReportItems:(NSArray<NSDictionary *> *)items
                 completion:(nullable void (^)(BOOL success, NSInteger statusCode))completion;

- (void)reportBadgeValue:(NSInteger)badgeValue
              completion:(nullable void (^)(BOOL success, NSInteger statusCode))completion;

@end

NS_ASSUME_NONNULL_END
