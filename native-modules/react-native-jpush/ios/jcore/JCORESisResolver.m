#import "JCORESisResolver.h"
#import "JCOREConfiguration.h"
#import "JCORECrypto.h"
#import <Network/Network.h>
#import <arpa/inet.h>

static NSString *const kJCoreSisRsaPublicKey =
    @"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi0v4VEUhOdfIEfFCrPC72pcDsQF/"
    @"luTmr4q34NY0EZYGKzfQuTrUAm916P52HCgF+342gjZ/Nvijts5543qYNyoLvgtu4NRcHJmuGI/"
    @"w7qifhfsivYeoEj9wYphXOyB9HUjzwn1BtAih+1RyUrcErCi249yabUOIKQygPZ9OXXQIDAQAB";

static const char *const kJCoreHttpSisIv = "0102030405060708";

@implementation JCORESisEndpoint

+ (instancetype)endpointFromString:(NSString *)addressString useSSL:(BOOL)useSSL {
    if (addressString.length == 0) {
        return nil;
    }
    NSRange lastColon = [addressString rangeOfString:@":" options:NSBackwardsSearch];
    if (lastColon.location == NSNotFound || lastColon.location == 0 || lastColon.location + 1 >= addressString.length) {
        return nil;
    }
    NSString *hostPart = [addressString substringToIndex:lastColon.location];
    hostPart = [hostPart stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"[] "]];
    NSInteger portVal = [[addressString substringFromIndex:lastColon.location + 1] integerValue];
    if (hostPart.length == 0 || portVal <= 0 || portVal > UINT16_MAX) {
        return nil;
    }
    JCORESisEndpoint *endpoint = [[JCORESisEndpoint alloc] init];
    endpoint.host = hostPart;
    endpoint.port = (uint16_t)portVal;
    endpoint.useSSL = useSSL;
    return endpoint;
}

- (NSString *)addressString {
    return [NSString stringWithFormat:@"%@:%u", self.host ?: @"", self.port];
}

@end

@interface JCORESisResolver ()
@property (nonatomic, strong) NSURLSession *urlSession;
@end

@implementation JCORESisResolver

+ (instancetype)shared {
    static JCORESisResolver *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[JCORESisResolver alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        NSURLSessionConfiguration *config = [NSURLSessionConfiguration ephemeralSessionConfiguration];
        config.timeoutIntervalForRequest = 6.0;
        config.timeoutIntervalForResource = 8.0;
        _urlSession = [NSURLSession sessionWithConfiguration:config];
    }
    return self;
}

- (void)setCustomConnectHost:(NSString *)host
                        port:(uint16_t)port
                      useSSL:(BOOL)useSSL {
    [[JCOREConfiguration shared] setCustomConnectHost:host port:port useSSL:useSSL];
}

- (void)setCustomConnectAddresses:(NSArray<NSString *> *)addresses {
    [JCOREConfiguration shared].customConnectAddresses = addresses;
}

- (void)clearCustomConnectEndpoint {
    [[JCOREConfiguration shared] clearCustomConnectEndpoint];
    self.customSisHost = nil;
    self.customSisPort = 0;
}

+ (NSData *)assembleUdpSisPacketWithPayload:(NSDictionary *)payload
                                      magic:(char)magic
                                       seed:(uint32_t)seed {
    if (![payload isKindOfClass:[NSDictionary class]] || ![NSJSONSerialization isValidJSONObject:payload]) {
        return nil;
    }
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:payload options:0 error:nil];
    if (jsonData.length == 0) {
        return nil;
    }
    NSData *compressed = [JCORECrypto gzipCompress:jsonData];
    if (compressed.length == 0 || compressed.length > UINT16_MAX) {
        return nil;
    }
    uint32_t seed24 = seed & 0x00FFFFFFU;
    if (seed24 == 0) {
        seed24 = 1U;
    }
    NSString *secret = [JCORECrypto generateSeedKey:seed24];
    if (secret.length < 16) {
        return nil;
    }
    NSString *iv = [secret substringToIndex:16];
    NSData *encrypted = [JCORECrypto aesCbcEncrypt:compressed
                                               key:secret
                                                iv:iv.UTF8String
                                           keySize:32];
    if (encrypted.length == 0 || encrypted.length + 10 > UINT16_MAX) {
        return nil;
    }

    uint16_t totalLengthBE = htons((uint16_t)(10 + encrypted.length));
    uint8_t magic0 = (uint8_t)(magic == 'M' ? 'M' : 'U');
    uint8_t magic1 = (uint8_t)'G';
    uint32_t keyFieldBE = htonl(seed24 | 0x11000000U);
    uint16_t declaredLengthBE = htons((uint16_t)compressed.length);

    NSMutableData *packet = [NSMutableData dataWithCapacity:10 + encrypted.length];
    [packet appendBytes:&totalLengthBE length:sizeof(uint16_t)];
    [packet appendBytes:&magic0 length:sizeof(uint8_t)];
    [packet appendBytes:&magic1 length:sizeof(uint8_t)];
    [packet appendBytes:&keyFieldBE length:sizeof(uint32_t)];
    [packet appendBytes:&declaredLengthBE length:sizeof(uint16_t)];
    [packet appendData:encrypted];
    return packet;
}

+ (NSDictionary *)parseUdpSisPacket:(NSData *)packetData {
    if (packetData.length < 10) {
        return nil;
    }
    const uint8_t *bytes = packetData.bytes;
    uint16_t rawTotalLen = 0;
    memcpy(&rawTotalLen, bytes, sizeof(uint16_t));
    uint16_t totalLen = ntohs(rawTotalLen);
    if (totalLen < 10 || totalLen > packetData.length) {
        return nil;
    }
    uint8_t magic0 = bytes[2];
    uint8_t magic1 = bytes[3];
    if ((magic0 != 'U' && magic0 != 'M') || magic1 != 'G') {
        return nil;
    }
    uint32_t rawKey = 0;
    memcpy(&rawKey, bytes + 4, sizeof(uint32_t));
    uint32_t hostKey = ntohl(rawKey);
    uint8_t flags = (uint8_t)((hostKey >> 24) & 0xFF);
    uint32_t seed24 = hostKey & 0x00FFFFFFU;

    uint16_t rawDeclaredLen = 0;
    memcpy(&rawDeclaredLen, bytes + 8, sizeof(uint16_t));
    uint16_t declaredLen = ntohs(rawDeclaredLen);
    if (declaredLen == 0) {
        return nil;
    }
    NSData *encrypted = [packetData subdataWithRange:NSMakeRange(10, totalLen - 10)];
    if (encrypted.length == 0) {
        return nil;
    }

    NSString *secret = [JCORECrypto generateSeedKey:seed24];
    if (secret.length < 16) {
        return nil;
    }
    NSString *iv = [secret substringToIndex:16];
    NSData *decrypted = [JCORECrypto aesCbcDecrypt:encrypted
                                               key:secret
                                                iv:iv.UTF8String
                                           keySize:32];
    if (decrypted.length == 0) {
        return nil;
    }
    NSUInteger sliceLen = MIN(decrypted.length, (NSUInteger)declaredLen);
    decrypted = [decrypted subdataWithRange:NSMakeRange(0, sliceLen)];

    NSData *plainData = decrypted;
    const uint8_t *decBytes = decrypted.bytes;
    BOOL hasGzipMagic = (decrypted.length >= 2 && decBytes[0] == 0x1F && decBytes[1] == 0x8B);
    if ((flags & 0x01) != 0 || (rawKey & 0x01) != 0 || hasGzipMagic) {
        plainData = [JCORECrypto gzipDecompress:decrypted] ?: decrypted;
    }
    if (plainData.length == 0) {
        return nil;
    }
    id json = [NSJSONSerialization JSONObjectWithData:plainData options:0 error:nil];
    if ([json isKindOfClass:[NSDictionary class]]) {
        [[JCOREConfiguration shared] updateWithSisDictionary:json];
        return json;
    }
    return nil;
}

+ (NSData *)encryptHttpSisPayload:(NSDictionary *)payload
                           aesKey:(NSString *)aesKey
                    outAuthHeader:(NSString **)outAuthHeader {
    if (![payload isKindOfClass:[NSDictionary class]] || aesKey.length == 0) {
        return nil;
    }
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:payload options:0 error:nil];
    NSData *compressed = [JCORECrypto gzipCompress:jsonData];
    NSData *encrypted = [JCORECrypto aesCbcEncrypt:compressed key:aesKey iv:kJCoreHttpSisIv keySize:32];
    NSString *authHeader = [JCORECrypto rsaEncryptString:aesKey publicKeyBase64:kJCoreSisRsaPublicKey];
    if (outAuthHeader != NULL) {
        *outAuthHeader = authHeader;
    }
    return encrypted;
}

+ (NSDictionary *)decryptHttpSisResponse:(NSData *)responseData
                                  aesKey:(NSString *)aesKey {
    if (responseData.length == 0 || aesKey.length == 0) {
        return nil;
    }
    NSData *decrypted = [JCORECrypto aesCbcDecrypt:responseData key:aesKey iv:kJCoreHttpSisIv keySize:32];
    NSData *plain = [JCORECrypto gzipDecompress:decrypted] ?: decrypted;
    if (plain.length == 0) {
        return nil;
    }
    id json = [NSJSONSerialization JSONObjectWithData:plain options:0 error:nil];
    if ([json isKindOfClass:[NSDictionary class]]) {
        [[JCOREConfiguration shared] updateWithSisDictionary:json];
        return json;
    }
    return nil;
}

- (NSDictionary *)buildSisRequestPayload {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    return @{
        @"type": @1,
        @"opera": @"",
        @"uid": @(config.uid),
        @"appkey": config.appKey ?: @"",
        @"sdkver": @"5.2.0",
        @"lat": @0,
        @"lng": @0,
        @"time": @0,
        @"platform": @1,
        @"fail_ips": config.failedIps ?: @[],
        @"ips_flag": @0,
        @"report_flag": @0,
        @"cert_ver": @(config.certVer > 0 ? config.certVer : 1),
    };
}

- (NSArray<JCORESisEndpoint *> *)defaultFallbackEndpoints {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    if (config.customConnectAddresses.count > 0) {
        NSMutableArray<JCORESisEndpoint *> *customList = [NSMutableArray array];
        for (NSString *addr in config.customConnectAddresses) {
            JCORESisEndpoint *ep = [JCORESisEndpoint endpointFromString:addr useSSL:config.customConnectUseSSL];
            if (ep != nil) {
                [customList addObject:ep];
            }
        }
        if (customList.count > 0) {
            return customList;
        }
    }
    if (config.customConnectHost.length > 0 && config.customConnectPort > 0) {
        JCORESisEndpoint *custom = [[JCORESisEndpoint alloc] init];
        custom.host = config.customConnectHost;
        custom.port = config.customConnectPort;
        custom.useSSL = config.customConnectUseSSL;
        return @[ custom ];
    }
    NSMutableArray<JCORESisEndpoint *> *endpoints = [NSMutableArray array];
    NSMutableArray<JCORESisEndpoint *> *deprioritizedFailed = [NSMutableArray array];
    NSSet<NSString *> *failedSet = [NSSet setWithArray:(config.failedIps ?: @[])];
    for (NSString *addr in config.ips) {
        JCORESisEndpoint *ep = [JCORESisEndpoint endpointFromString:addr useSSL:NO];
        if (ep != nil) {
            if ([failedSet containsObject:ep.addressString]) {
                [deprioritizedFailed addObject:ep];
            } else {
                [endpoints addObject:ep];
            }
        }
    }
    for (NSString *addr in config.sslIps) {
        JCORESisEndpoint *ep = [JCORESisEndpoint endpointFromString:addr useSSL:YES];
        if (ep != nil) {
            if ([failedSet containsObject:ep.addressString]) {
                [deprioritizedFailed addObject:ep];
            } else {
                [endpoints addObject:ep];
            }
        }
    }
    [endpoints addObjectsFromArray:deprioritizedFailed];
    for (NSNumber *portNum in config.defaultConnectPorts) {
        JCORESisEndpoint *ep = [[JCORESisEndpoint alloc] init];
        ep.host = config.defaultConnectHost;
        ep.port = portNum.unsignedShortValue;
        ep.useSSL = YES;
        [endpoints addObject:ep];
    }
    return endpoints;
}

- (NSArray<JCORESisEndpoint *> *)parseSisResponseDictionary:(NSDictionary *)dict {
    if (![dict isKindOfClass:[NSDictionary class]]) {
        return @[];
    }
    [[JCOREConfiguration shared] updateWithSisDictionary:dict];

    NSMutableArray<JCORESisEndpoint *> *results = [NSMutableArray array];
    NSArray<NSString *> *ips = [dict[@"ips"] isKindOfClass:[NSArray class]] ? dict[@"ips"] : @[];
    for (NSString *addr in ips) {
        if ([addr isKindOfClass:[NSString class]]) {
            JCORESisEndpoint *ep = [JCORESisEndpoint endpointFromString:addr useSSL:NO];
            if (ep != nil) {
                [results addObject:ep];
            }
        }
    }
    NSArray<NSString *> *sslIps = [dict[@"ssl_ips"] isKindOfClass:[NSArray class]] ? dict[@"ssl_ips"] : @[];
    for (NSString *addr in sslIps) {
        if ([addr isKindOfClass:[NSString class]]) {
            JCORESisEndpoint *ep = [JCORESisEndpoint endpointFromString:addr useSSL:YES];
            if (ep != nil) {
                [results addObject:ep];
            }
        }
    }
    return results;
}

- (void)resolveUdpSisWithHost:(NSString *)host
                         port:(uint16_t)port
                      timeout:(NSTimeInterval)timeout
                   completion:(void (^)(NSArray<JCORESisEndpoint *> *endpoints))completion {
    if (host.length == 0) {
        completion(@[]);
        return;
    }
    uint16_t actualPort = port > 0 ? port : 19000;
    uint32_t seed = arc4random() & 0x00FFFFFFU;
    if (seed == 0) {
        seed = 1U;
    }
    NSData *packet = [JCORESisResolver assembleUdpSisPacketWithPayload:[self buildSisRequestPayload]
                                                                 magic:'U'
                                                                  seed:seed];
    if (packet.length == 0) {
        completion(@[]);
        return;
    }

    dispatch_queue_t udpQueue = dispatch_queue_create("cn.jiguang.jcore.udp.sis", DISPATCH_QUEUE_SERIAL);
    NSString *portStr = [NSString stringWithFormat:@"%u", actualPort];
    nw_endpoint_t endpoint = nw_endpoint_create_host(host.UTF8String, portStr.UTF8String);
    nw_parameters_t params = nw_parameters_create_secure_udp(NW_PARAMETERS_DISABLE_PROTOCOL,
                                                             NW_PARAMETERS_DEFAULT_CONFIGURATION);
    nw_connection_t conn = nw_connection_create(endpoint, params);
    nw_connection_set_queue(conn, udpQueue);

    __block BOOL finished = NO;
    void (^finishOnce)(NSArray<JCORESisEndpoint *> *) = ^(NSArray<JCORESisEndpoint *> *eps) {
        if (finished) {
            return;
        }
        finished = YES;
        nw_connection_cancel(conn);
        completion(eps ?: @[]);
    };

    NSTimeInterval effectiveTimeout = timeout > 0 ? timeout : 3.0;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(effectiveTimeout * NSEC_PER_SEC)), udpQueue, ^{
        finishOnce(@[]);
    });

    nw_connection_set_state_changed_handler(conn, ^(nw_connection_state_t state, nw_error_t error) {
        if (state == nw_connection_state_ready) {
            dispatch_data_t ddata = dispatch_data_create(packet.bytes,
                                                         packet.length,
                                                         udpQueue,
                                                         DISPATCH_DATA_DESTRUCTOR_DEFAULT);
            nw_connection_send(conn, ddata, NW_CONNECTION_DEFAULT_MESSAGE_CONTEXT, true, ^(nw_error_t sendErr) {
                if (sendErr != nil) {
                    finishOnce(@[]);
                }
            });
            nw_connection_receive_message(conn, ^(dispatch_data_t content, nw_content_context_t context, bool is_complete, nw_error_t recvErr) {
                if (recvErr == nil && content != nil) {
                    NSData *respData = (NSData *)content;
                    NSDictionary *dict = [JCORESisResolver parseUdpSisPacket:respData];
                    NSArray<JCORESisEndpoint *> *parsed = [self parseSisResponseDictionary:dict];
                    if (parsed.count > 0) {
                        [[JCOREConfiguration shared] addLastSuccessSisIp:[NSString stringWithFormat:@"%@:%u", host, actualPort]];
                        [[JCOREConfiguration shared] clearFailedIps];
                    }
                    finishOnce(parsed);
                } else {
                    finishOnce(@[]);
                }
            });
        } else if (state == nw_connection_state_failed || state == nw_connection_state_cancelled) {
            finishOnce(@[]);
        }
    });

    nw_connection_start(conn);
}

- (void)resolveEndpointsWithCompletion:(void (^)(NSArray<JCORESisEndpoint *> *endpoints))completion {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    if (config.customConnectAddresses.count > 0) {
        completion([self defaultFallbackEndpoints]);
        return;
    }
    if (config.customConnectHost.length > 0 && config.customConnectPort > 0) {
        JCORESisEndpoint *custom = [[JCORESisEndpoint alloc] init];
        custom.host = config.customConnectHost;
        custom.port = config.customConnectPort;
        custom.useSSL = config.customConnectUseSSL;
        completion(@[ custom ]);
        return;
    }

    if (self.customSisHost.length > 0 && self.customSisPort > 0) {
        [self resolveUdpSisWithHost:self.customSisHost
                               port:self.customSisPort
                            timeout:3.0
                         completion:^(NSArray<JCORESisEndpoint *> *udpEndpoints) {
            if (udpEndpoints.count > 0) {
                completion(udpEndpoints);
            } else {
                completion([self defaultFallbackEndpoints]);
            }
        }];
        return;
    }

    if (config.ips.count > 0 || config.sslIps.count > 0) {
        completion([self defaultFallbackEndpoints]);
        return;
    }

    NSString *firstHttpsUrl = config.httpsSisUrls.firstObject;
    if (firstHttpsUrl.length == 0 || config.appKey.length == 0) {
        completion([self defaultFallbackEndpoints]);
        return;
    }

    NSString *aesKey = [JCORECrypto httpSeedKeyWithOutSeed:NULL];
    NSString *authHeader = nil;
    NSData *encrypted = [JCORESisResolver encryptHttpSisPayload:[self buildSisRequestPayload]
                                                         aesKey:aesKey
                                                  outAuthHeader:&authHeader];

    NSURL *url = [NSURL URLWithString:[firstHttpsUrl stringByAppendingString:@"/v3/srv"]];
    if (url == nil || encrypted.length == 0 || authHeader.length == 0) {
        completion([self defaultFallbackEndpoints]);
        return;
    }

    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    request.HTTPBody = encrypted;
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setValue:authHeader forHTTPHeaderField:@"Authorization"];
    request.timeoutInterval = 6.0;

    NSURLSessionDataTask *task = [self.urlSession dataTaskWithRequest:request
                                                    completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        if (error == nil && data.length > 0) {
            NSDictionary *json = [JCORESisResolver decryptHttpSisResponse:data aesKey:aesKey];
            NSArray<JCORESisEndpoint *> *parsed = [self parseSisResponseDictionary:json];
            if (parsed.count > 0) {
                completion(parsed);
                return;
            }
        }
        NSString *udpHost = config.sisDefaultIps.firstObject ?: config.sisHostDomains.firstObject;
        if (udpHost.length > 0) {
            [self resolveUdpSisWithHost:udpHost port:19000 timeout:2.5 completion:^(NSArray<JCORESisEndpoint *> *udpEndpoints) {
                if (udpEndpoints.count > 0) {
                    completion(udpEndpoints);
                } else {
                    completion([self defaultFallbackEndpoints]);
                }
            }];
            return;
        }
        completion([self defaultFallbackEndpoints]);
    }];
    [task resume];
}

@end
