#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JCORESisEndpoint : NSObject

@property (nonatomic, copy) NSString *host;
@property (nonatomic, assign) uint16_t port;
@property (nonatomic, assign) BOOL useSSL;

+ (nullable instancetype)endpointFromString:(NSString *)addressString useSSL:(BOOL)useSSL;
- (NSString *)addressString;

@end

@interface JCORESisResolver : NSObject

+ (instancetype)shared;

@property (nonatomic, copy, nullable) NSString *customSisHost;
@property (nonatomic, assign) uint16_t customSisPort;

- (void)setCustomConnectHost:(nullable NSString *)host
                        port:(uint16_t)port
                      useSSL:(BOOL)useSSL;
- (void)setCustomConnectAddresses:(nullable NSArray<NSString *> *)addresses;
- (void)clearCustomConnectEndpoint;

- (NSDictionary *)buildSisRequestPayload;

+ (nullable NSData *)assembleUdpSisPacketWithPayload:(NSDictionary *)payload
                                               magic:(char)magic
                                                seed:(uint32_t)seed;

+ (nullable NSDictionary *)parseUdpSisPacket:(NSData *)packetData;

+ (nullable NSData *)encryptHttpSisPayload:(NSDictionary *)payload
                                    aesKey:(NSString *)aesKey
                             outAuthHeader:(NSString *_Nullable *_Nullable)outAuthHeader;

+ (nullable NSDictionary *)decryptHttpSisResponse:(NSData *)responseData
                                           aesKey:(NSString *)aesKey;

- (NSArray<JCORESisEndpoint *> *)parseSisResponseDictionary:(NSDictionary *)dict;
- (NSArray<JCORESisEndpoint *> *)defaultFallbackEndpoints;

- (void)resolveUdpSisWithHost:(NSString *)host
                         port:(uint16_t)port
                      timeout:(NSTimeInterval)timeout
                   completion:(void (^)(NSArray<JCORESisEndpoint *> *endpoints))completion;

- (void)resolveEndpointsWithCompletion:(void (^)(NSArray<JCORESisEndpoint *> *endpoints))completion;

@end

NS_ASSUME_NONNULL_END
