
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <UserNotifications/UserNotifications.h>
#import <sys/socket.h>
#import <netinet/in.h>
#import <arpa/inet.h>
#import <unistd.h>

#import "JGInforCollectionAuth.h"
#import "JGInforCollectionAuthItems.h"
#import "JGCOREAPI.h"
#import "JCOREService.h"
#import "JPUSHService.h"
#import "JGPushNotificationTrigger.h"
#import "JCOREConfiguration.h"
#import "JCORECrypto.h"
#import "JCORESisResolver.h"
#import "JCOREWireProtocol.h"
#import "JCORETcpSession.h"
#import "JPUSHNotificationManager.h"
#import "RCTJPushEventQueue.h"
#import "RCTJPushModule.h"
#import <React/RCTBridge.h>

#define ASSERT_TRUE(cond, msg, ...) do { \
    if (!(cond)) { \
        NSLog(@"[FAIL] %s:%d: " @msg, __FUNCTION__, __LINE__, ##__VA_ARGS__); \
        exit(1); \
    } \
} while (0)

static BOOL readExactBytes(int fd, uint8_t *buf, size_t len) {
    size_t offset = 0;
    while (offset < len) {
        ssize_t n = recv(fd, buf + offset, len - offset, 0);
        if (n <= 0) {
            return NO;
        }
        offset += (size_t)n;
    }
    return YES;
}

static BOOL writeExactBytes(int fd, const uint8_t *buf, size_t len) {
    size_t offset = 0;
    while (offset < len) {
        ssize_t n = send(fd, buf + offset, len - offset, 0);
        if (n <= 0) {
            return NO;
        }
        offset += (size_t)n;
    }
    return YES;
}

static JCOREWireFrame *readSingleClientFrameOnServer(int clientFd) {
    uint8_t lenBytes[2] = {0};
    if (!readExactBytes(clientFd, lenBytes, 2)) {
        return nil;
    }
    uint16_t rawLen = ((uint16_t)lenBytes[0] << 8) | (uint16_t)lenBytes[1];
    ASSERT_TRUE((rawLen & 0x8000) != 0, "Expected 0x8000 MSB set on client frame");
    uint16_t totalLen = rawLen & 0x7FFF;
    ASSERT_TRUE(totalLen >= 24 && ((totalLen - 24) % 16 == 0),
                "Client -> Server frame must have 24-byte header + 16*k body, got totalLen=%u", totalLen);
    NSMutableData *packet = [NSMutableData dataWithCapacity:totalLen];
    [packet appendBytes:lenBytes length:2];
    NSMutableData *rest = [NSMutableData dataWithLength:totalLen - 2];
    if (!readExactBytes(clientFd, rest.mutableBytes, totalLen - 2)) {
        return nil;
    }
    [packet appendData:rest];

    NSArray<JCOREWireFrame *> *frames = [JCOREWireProtocol decodeClientFramesFromStreamBuffer:packet];
    ASSERT_TRUE(frames.count == 1, "Expected 1 decoded client frame, got %lu", (unsigned long)frames.count);
    ASSERT_TRUE(packet.length == 0, "Expected entire frame buffer consumed, remaining=%lu", (unsigned long)packet.length);
    return frames.firstObject;
}

static void testCryptoPrimitives(void) {
    NSString *seedKey0 = [JCORECrypto generateSeedKey:123456789ULL];
    NSString *seedKey0Again = [JCORECrypto seedKeyForSeed:123456789ULL];
    ASSERT_TRUE(seedKey0.length == 32 && [seedKey0 isEqualToString:seedKey0Again], "seedKey must be deterministic 32-char hex");

    NSData *plain = [@"OneKey Clean JCore Crypto Verification Payload 2026" dataUsingEncoding:NSUTF8StringEncoding];
    const char *iv = "0102030405060708";
    NSData *enc128 = [JCORECrypto aesCbcEncrypt:plain key:seedKey0 iv:iv keySize:16];
    ASSERT_TRUE(enc128.length > 0, "AES-128-CBC encryption failed");
    NSData *dec128 = [JCORECrypto aesCbcDecrypt:enc128 key:seedKey0 iv:iv keySize:16];
    ASSERT_TRUE([dec128 isEqualToData:plain], "AES-128-CBC roundtrip mismatch");

    NSData *enc256 = [JCORECrypto aesCbcEncrypt:plain key:seedKey0 iv:iv keySize:32];
    ASSERT_TRUE(enc256.length > 0, "AES-256-CBC encryption failed");
    NSData *dec256 = [JCORECrypto aesCbcDecrypt:enc256 key:seedKey0 iv:iv keySize:32];
    ASSERT_TRUE([dec256 isEqualToData:plain], "AES-256-CBC roundtrip mismatch");

    NSData *httpEnc = [JCORECrypto aes256CBCEncryptWithFixedHttpIV:plain key:seedKey0];
    NSData *httpDec = [JCORECrypto aes256CBCDecryptWithFixedHttpIV:httpEnc key:seedKey0];
    ASSERT_TRUE([httpDec isEqualToData:plain], "AES-256-CBC fixed HTTP IV roundtrip mismatch");

    NSString *key16 = [seedKey0 substringToIndex:16];
    NSData *badgeEnc = [JCORECrypto aes128CBCEncryptData:plain key16:key16 iv16:@"0000000000000000"];
    NSData *badgeDec = [JCORECrypto aes128CBCDecryptData:badgeEnc key16:key16 iv16:@"0000000000000000"];
    ASSERT_TRUE([badgeDec isEqualToData:plain], "AES-128-CBC badge roundtrip mismatch");

    NSData *sm4CbcEnc = [JCORECrypto sm4CBCEncryptData:plain withKey:seedKey0 iv:iv];
    ASSERT_TRUE(sm4CbcEnc.length > 0 && (sm4CbcEnc.length % 16 == 0), "SM4-CBC encryption failed");
    NSData *sm4CbcDec = [JCORECrypto sm4CBCDecryptData:sm4CbcEnc withKey:seedKey0 iv:iv];
    ASSERT_TRUE([sm4CbcDec isEqualToData:plain], "SM4-CBC roundtrip mismatch");

    NSData *sm4EcbEnc = [JCORECrypto sm4ECBEncryptData:plain withKey:seedKey0];
    ASSERT_TRUE(sm4EcbEnc.length > 0 && (sm4EcbEnc.length % 16 == 0), "SM4-ECB encryption failed");
    NSData *sm4EcbDec = [JCORECrypto sm4ECBDecryptData:sm4EcbEnc withKey:seedKey0];
    ASSERT_TRUE([sm4EcbDec isEqualToData:plain], "SM4-ECB roundtrip mismatch");

    NSData *compressed = [JCORECrypto gzipCompress:plain];
    ASSERT_TRUE(compressed.length > 0, "GZIP compression failed");
    NSData *decompressed = [JCORECrypto gzipDecompress:compressed];
    ASSERT_TRUE([decompressed isEqualToData:plain], "GZIP roundtrip mismatch");

    for (NSNumber *length in @[@65535, @65536, @65537]) {
        NSMutableData *boundary = [NSMutableData dataWithLength:length.unsignedIntegerValue];
        arc4random_buf(boundary.mutableBytes, boundary.length);
        NSData *decoded = [JCORECrypto gzipDecompress:[JCORECrypto gzipCompress:boundary]];
        ASSERT_TRUE(length.unsignedIntegerValue <= 65536 ? [decoded isEqualToData:boundary] : decoded == nil,
                    "GZIP decoded limit mismatch at %@ bytes", length);
    }

    NSString *sisPubKey = @"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi0v4VEUhOdfIEfFCrPC72pcDsQF/"
                          @"luTmr4q34NY0EZYGKzfQuTrUAm916P52HCgF+342gjZ/Nvijts5543qYNyoLvgtu4NRcHJmuGI/"
                          @"w7qifhfsivYeoEj9wYphXOyB9HUjzwn1BtAih+1RyUrcErCi249yabUOIKQygPZ9OXXQIDAQAB";
    NSString *rsaB64 = [JCORECrypto rsaEncryptString:seedKey0 publicKeyBase64:sisPubKey];
    ASSERT_TRUE(rsaB64.length > 64, "RSA-PKCS1 encryption failed");

    ASSERT_TRUE([[JCORECrypto md5Hex:@"abc"] isEqualToString:@"900150983cd24fb0d6963f7d28e17f72"], "MD5 vector mismatch");
    ASSERT_TRUE([[JCORECrypto sha1Hex:[@"abc" dataUsingEncoding:NSUTF8StringEncoding]] isEqualToString:@"a9993e364706816aba3e25717850c26c9cd0d89d"], "SHA1 vector mismatch");
    ASSERT_TRUE([[JCORECrypto hmacSha256Hex:@"message" key:@"secret"] isEqualToString:@"8b5f48702995c1598c573db1e21866a9b825d4a794d169d7060a03605796360b"], "HMAC-SHA256 vector mismatch");

    printf("[PASS] Suite 1: JCORECrypto (AES-128/256, SM4-CBC/ECB, GZIP, RSA-1024, MD5, SHA1, HMAC-SHA256) verified\n");
}

static void testSisWireFormatAndUdpLoopback(void) {
    [[JCOREConfiguration shared] configureWithAppKey:@"sis_test_appkey" channel:@"App Store" isProduction:NO advertisingIdentifier:nil];
    [[JCOREConfiguration shared] clearFailedIps];
    [[JCOREConfiguration shared] addFailedIp:@"198.51.100.1:7000"];

    NSDictionary *sisReq = [[JCORESisResolver shared] buildSisRequestPayload];
    ASSERT_TRUE([sisReq[@"sdkver"] isEqualToString:@"5.2.0"], "SIS sdkver must be 5.2.0");
    ASSERT_TRUE([sisReq[@"platform"] integerValue] == 1, "SIS platform must be 1");
    ASSERT_TRUE([sisReq[@"fail_ips"] containsObject:@"198.51.100.1:7000"], "SIS fail_ips must include recorded failed IP");
    ASSERT_TRUE(sisReq[@"ips_flag"] != nil && sisReq[@"report_flag"] != nil && sisReq[@"cert_ver"] != nil,
                "SIS request missing required keys");
    [[JCOREConfiguration shared] clearFailedIps];

    NSDictionary *sisPayload = @{
        @"ips": @[@"120.46.136.110:7000", @"114.116.250.90:7001"],
        @"ssl_ips": @[@"120.46.136.110:7002"],
        @"sis_ips": @[@"123.196.118.22:19000"],
        @"http_report": @[@"stats.jpush.cn"],
        @"https_report": @[@"https://stats.jpush.cn/v3/report"],
        @"cert_ver": @2,
        @"data_report": @YES,
    };
    uint32_t seed = 0x00ABCDEF;
    NSData *udpPacket = [JCORESisResolver assembleUdpSisPacketWithPayload:sisPayload magic:'U' seed:seed];
    ASSERT_TRUE(udpPacket.length > 10, "UDP SIS packet assembly failed");

    const uint8_t *bytes = udpPacket.bytes;
    uint16_t hdrLen = ((uint16_t)bytes[0] << 8) | bytes[1];
    ASSERT_TRUE(hdrLen == udpPacket.length, "UDP SIS 10-byte header totalLen must equal packet length");
    ASSERT_TRUE(bytes[2] == 'U' && bytes[3] == 'G', "UDP SIS magic header mismatch");
    uint32_t encodedSeedWord = ((uint32_t)bytes[4] << 24) | ((uint32_t)bytes[5] << 16) | ((uint32_t)bytes[6] << 8) | bytes[7];
    ASSERT_TRUE((encodedSeedWord & 0xFF000000U) == 0x11000000U && (encodedSeedWord & 0x00FFFFFFU) == seed,
                "UDP SIS 4-byte seed word mismatch");

    NSDictionary *parsedDict = [JCORESisResolver parseUdpSisPacket:udpPacket];
    ASSERT_TRUE([parsedDict isKindOfClass:[NSDictionary class]], "UDP SIS packet parse failed");
    NSArray<JCORESisEndpoint *> *endpoints = [[JCORESisResolver shared] parseSisResponseDictionary:parsedDict];
    ASSERT_TRUE(endpoints.count == 3, "Expected 3 SIS endpoints, got %lu", (unsigned long)endpoints.count);
    ASSERT_TRUE([JCOREPersistence shared].certVer == 2 && [JCOREPersistence shared].dataReport == YES,
                "SIS fields not persisted to JCOREPersistence");

    NSString *aesKey = [JCORECrypto httpSeedKeyWithOutSeed:NULL];
    NSString *authHeader = nil;
    NSData *httpEncrypted = [JCORESisResolver encryptHttpSisPayload:sisPayload aesKey:aesKey outAuthHeader:&authHeader];
    ASSERT_TRUE(httpEncrypted.length > 0 && authHeader.length > 0, "HTTP SIS payload encryption failed");
    NSDictionary *httpDecrypted = [JCORESisResolver decryptHttpSisResponse:httpEncrypted aesKey:aesKey];
    ASSERT_TRUE([httpDecrypted[@"ssl_ips"] isEqualToArray:sisPayload[@"ssl_ips"]], "HTTP SIS payload decrypt roundtrip mismatch");

    // Loopback UDP SIS server test
    int udpFd = socket(AF_INET, SOCK_DGRAM, 0);
    ASSERT_TRUE(udpFd >= 0, "Failed to create UDP socket");
    struct sockaddr_in udpAddr;
    memset(&udpAddr, 0, sizeof(udpAddr));
    udpAddr.sin_family = AF_INET;
    udpAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    udpAddr.sin_port = 0;
    ASSERT_TRUE(bind(udpFd, (struct sockaddr *)&udpAddr, sizeof(udpAddr)) == 0, "UDP bind() failed");
    socklen_t udpLen = sizeof(udpAddr);
    ASSERT_TRUE(getsockname(udpFd, (struct sockaddr *)&udpAddr, &udpLen) == 0, "UDP getsockname() failed");
    uint16_t udpPort = ntohs(udpAddr.sin_port);

    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        uint8_t buf[2048];
        struct sockaddr_in clientAddr;
        socklen_t clientLen = sizeof(clientAddr);
        ssize_t n = recvfrom(udpFd, buf, sizeof(buf), 0, (struct sockaddr *)&clientAddr, &clientLen);
        if (n > 0) {
            NSData *reqData = [NSData dataWithBytes:buf length:(NSUInteger)n];
            NSDictionary *reqDict = [JCORESisResolver parseUdpSisPacket:reqData];
            if (reqDict != nil) {
                NSData *respData = [JCORESisResolver assembleUdpSisPacketWithPayload:@{@"ssl_ips": @[@"127.0.0.1:28888"]}
                                                                               magic:'U'
                                                                                seed:0x123456];
                sendto(udpFd, respData.bytes, respData.length, 0, (struct sockaddr *)&clientAddr, clientLen);
            }
        }
        close(udpFd);
    });

    dispatch_semaphore_t udpSem = dispatch_semaphore_create(0);
    __block NSArray<JCORESisEndpoint *> *resolvedUdp = nil;
    [[JCORESisResolver shared] resolveUdpSisWithHost:@"127.0.0.1"
                                                port:udpPort
                                             timeout:3.0
                                          completion:^(NSArray<JCORESisEndpoint *> *eps) {
        resolvedUdp = eps;
        dispatch_semaphore_signal(udpSem);
    }];
    ASSERT_TRUE(dispatch_semaphore_wait(udpSem, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC))) == 0,
                "UDP SIS loopback timed out");
    ASSERT_TRUE(resolvedUdp.count == 1 && [resolvedUdp[0].host isEqualToString:@"127.0.0.1"] && resolvedUdp[0].port == 28888,
                "UDP SIS loopback resolved unexpected endpoint");

    // Verify stale SIS cache fallback in resolveEndpointsWithCompletion: when all cached ips/ssl_ips are in failedIps
    int staleUdpFd = socket(AF_INET, SOCK_DGRAM, 0);
    ASSERT_TRUE(staleUdpFd >= 0, "Failed to create stale-SIS UDP socket");
    struct sockaddr_in staleUdpAddr;
    memset(&staleUdpAddr, 0, sizeof(staleUdpAddr));
    staleUdpAddr.sin_family = AF_INET;
    staleUdpAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    staleUdpAddr.sin_port = 0;
    ASSERT_TRUE(bind(staleUdpFd, (struct sockaddr *)&staleUdpAddr, sizeof(staleUdpAddr)) == 0, "stale UDP bind() failed");
    socklen_t staleUdpLen = sizeof(staleUdpAddr);
    ASSERT_TRUE(getsockname(staleUdpFd, (struct sockaddr *)&staleUdpAddr, &staleUdpLen) == 0, "stale UDP getsockname() failed");
    uint16_t staleUdpPort = ntohs(staleUdpAddr.sin_port);

    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        uint8_t buf[2048];
        struct sockaddr_in clientAddr;
        socklen_t clientLen = sizeof(clientAddr);
        ssize_t n = recvfrom(staleUdpFd, buf, sizeof(buf), 0, (struct sockaddr *)&clientAddr, &clientLen);
        if (n > 0) {
            NSData *respData = [JCORESisResolver assembleUdpSisPacketWithPayload:@{@"ips": @[@"127.0.0.1:29999"]}
                                                                           magic:'U'
                                                                            seed:0x654321];
            sendto(staleUdpFd, respData.bytes, respData.length, 0, (struct sockaddr *)&clientAddr, clientLen);
        }
        close(staleUdpFd);
    });

    JCOREConfiguration *sisConfig = [JCOREConfiguration shared];
    NSArray<NSString *> *savedHttpsSisUrls = sisConfig.httpsSisUrls;
    sisConfig.httpsSisUrls = @[];
    [sisConfig updateWithSisDictionary:@{
        @"ips": @[@"127.0.0.1:21111"],
        @"ssl_ips": @[],
        @"sis_ips": @[[NSString stringWithFormat:@"127.0.0.1:%u", staleUdpPort]],
    }];
    [sisConfig clearFailedIps];
    [sisConfig addFailedIp:@"127.0.0.1:21111"];

    dispatch_semaphore_t staleSem = dispatch_semaphore_create(0);
    __block NSArray<JCORESisEndpoint *> *refreshedEps = nil;
    [[JCORESisResolver shared] resolveEndpointsWithCompletion:^(NSArray<JCORESisEndpoint *> *endpoints) {
        refreshedEps = endpoints;
        dispatch_semaphore_signal(staleSem);
    }];
    ASSERT_TRUE(dispatch_semaphore_wait(staleSem, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC))) == 0,
                "Stale SIS cache refresh timed out");
    sisConfig.httpsSisUrls = savedHttpsSisUrls;
    ASSERT_TRUE(refreshedEps.count == 1 && [refreshedEps[0].host isEqualToString:@"127.0.0.1"] && refreshedEps[0].port == 29999,
                "resolveEndpointsWithCompletion must refresh via SIS when all cached ips are in failedIps");

    printf("[PASS] Suite 2: JCORESisResolver 10-byte UDP & HTTPS SIS wire format + persistence + loopback verified\n");
}

static void testWireProtocolFraming(void) {
    uint64_t uid = 888999000ULL;
    NSData *tagPayload = [JCOREWireProtocol buildTagOperationPayloadWithOperator:@"add"
                                                                            tags:@[@"alpha", @"beta"]
                                                                        currPage:1];

    // Test both AES-256-CBC (algorithm=1) and SM4-CBC (algorithm=2) for 24-byte Client and 20-byte Server headers
    for (uint8_t alg = 1; alg <= 2; alg++) {
        NSData *clientFrameBytes = [JCOREWireProtocol encodeClientFrameWithCommand:JCOREWireCommandTagOperation
                                                                           version:1
                                                                         requestId:77
                                                                         sessionId:0x11223344
                                                                               uid:uid
                                                                         algorithm:alg
                                                                          bodyData:tagPayload];
        ASSERT_TRUE(clientFrameBytes.length >= 40 && ((clientFrameBytes.length - 24) % 16 == 0),
                    "Client frame must have 24-byte header + 16*k body");
        const uint8_t *cb = clientFrameBytes.bytes;
        ASSERT_TRUE(cb[2] == 1 && cb[3] == JCOREWireCommandTagOperation && cb[4] == alg,
                    "Client 24-byte header fields mismatch");

        NSMutableData *clientBuf = [NSMutableData dataWithData:clientFrameBytes];
        NSArray<JCOREWireFrame *> *decodedClient = [JCOREWireProtocol decodeClientFramesFromStreamBuffer:clientBuf];
        ASSERT_TRUE(decodedClient.count == 1 && clientBuf.length == 0, "Failed decoding client frame (alg=%u)", alg);
        JCOREWireFrame *cf = decodedClient.firstObject;
        ASSERT_TRUE(cf.command == JCOREWireCommandTagOperation && cf.requestId == 77 && cf.sessionId == 0x11223344 && cf.uid == uid,
                    "Decoded client frame header mismatch");
        NSDictionary *parsedTag = [JCOREWireProtocol parseJsonBodyFromFrame:cf];
        ASSERT_TRUE([parsedTag[@"platform"] isEqualToString:@"i"] && [parsedTag[@"op"] isEqualToString:@"add"],
                    "Decoded tag JSON mismatch");

        NSData *serverFrameBytes = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandTagOperation
                                                                           version:1
                                                                         requestId:77
                                                                               uid:uid
                                                                         algorithm:alg
                                                                        statusCode:0
                                                                          bodyData:[JCOREWireProtocol buildJsonCommandPayload:@{@"code": @0, @"tags": @[@"alpha", @"beta"]}]];
        ASSERT_TRUE(serverFrameBytes.length >= 36 && ((serverFrameBytes.length - 20) % 16 == 0),
                    "Server frame must have 20-byte header + 16*k body");
        const uint8_t *sb = serverFrameBytes.bytes;
        ASSERT_TRUE(sb[2] == 1 && sb[3] == JCOREWireCommandTagOperation && sb[4] == alg,
                    "Server 20-byte header fields mismatch");

        NSMutableData *serverBuf = [NSMutableData dataWithData:serverFrameBytes];
        NSArray<JCOREWireFrame *> *decodedServer = [JCOREWireProtocol decodeServerFramesFromStreamBuffer:serverBuf defaultUid:uid];
        ASSERT_TRUE(decodedServer.count == 1 && serverBuf.length == 0, "Failed decoding server frame (alg=%u)", alg);
    }

    // Verify Login payload starts with 0x00, 0x00, 0x00, 0x69 (uint32(105))
    NSData *loginBody = [JCOREWireProtocol buildLoginPayloadWithPassword:@"pwd123" appKey:@"appkey123"];
    const uint8_t *lb = loginBody.bytes;
    ASSERT_TRUE(loginBody.length > 40 && lb[0] == 0x00 && lb[1] == 0x00 && lb[2] == 0x00 && lb[3] == 0x69,
                "Login payload must start with uint32(105) 0x00000069");

    // Verify Heartbeat payload is 8 bytes
    NSData *hbBody = [JCOREWireProtocol buildHeartbeatPayloadWithPushStopped:NO];
    ASSERT_TRUE(hbBody.length == 8, "Heartbeat payload must be 8 bytes, got %lu", (unsigned long)hbBody.length);

    // Verify LiveActivity error code mapping
    ASSERT_TRUE([JCOREWireProtocol mapLiveActivityStatusCode:3900] == 6039, "3900 -> 6039");
    ASSERT_TRUE([JCOREWireProtocol mapLiveActivityStatusCode:3901] == 6040, "3901 -> 6040");
    ASSERT_TRUE([JCOREWireProtocol mapLiveActivityStatusCode:3902] == 6042, "3902 -> 6042");
    ASSERT_TRUE([JCOREWireProtocol mapLiveActivityStatusCode:3950] == 6041, "3950 -> 6041");

    printf("[PASS] Suite 3: JCOREWireProtocol asymmetric 24B/20B framing (AES-256-CBC & SM4-CBC) & command layouts verified\n");
}

static void testEndToEndMockServerHandshake(void) {
    int listenFd = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_TRUE(listenFd >= 0, "Failed to create listen socket");

    int reuse = 1;
    setsockopt(listenFd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));

    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    addr.sin_port = 0;

    ASSERT_TRUE(bind(listenFd, (struct sockaddr *)&addr, sizeof(addr)) == 0, "bind() failed");
    ASSERT_TRUE(listen(listenFd, 4) == 0, "listen() failed");

    socklen_t addrLen = sizeof(addr);
    ASSERT_TRUE(getsockname(listenFd, (struct sockaddr *)&addr, &addrLen) == 0, "getsockname() failed");
    uint16_t serverPort = ntohs(addr.sin_port);
    ASSERT_TRUE(serverPort > 0, "Invalid ephemeral port");

    const uint64_t assignedUid = 900100200ULL;
    NSString *const assignedPassword = @"mock_srv_pwd_2026";
    NSString *const assignedRegId = @"1a0018970a900100200";
    const uint32_t assignedSessionId = 0x55667788;
    const uint64_t expectedMsgId = 7788990011ULL;
    const uint8_t expectedMsgType = 3;
    const uint64_t expectedCtrlReqId = 5544332211ULL;

    dispatch_semaphore_t serverDoneSem = dispatch_semaphore_create(0);
    __block BOOL serverRegistered = NO;
    __block BOOL serverLoggedIn = NO;
    __block NSString *serverDeviceToken = nil;
    __block NSString *serverVoipToken = nil;
    __block NSDictionary *serverTagJson = nil;
    __block NSDictionary *serverAliasJson = nil;
    __block NSDictionary *serverPropsJson = nil;
    __block NSString *serverMobileNumber = nil;
    __block BOOL serverSawLiveActivityStart = NO;
    __block BOOL serverSawLiveActivityUpdate = NO;
    __block BOOL serverSawPushCtrlResume = NO;
    __block uint64_t serverAckMsgId = 0;
    __block uint8_t serverAckMsgType = 0;
    __block uint64_t serverCtrlAckReqId = 0;

    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        struct sockaddr_in clientAddr;
        socklen_t clientLen = sizeof(clientAddr);
        int clientFd = accept(listenFd, (struct sockaddr *)&clientAddr, &clientLen);
        ASSERT_TRUE(clientFd >= 0, "Server accept() failed");

        struct timeval tv;
        tv.tv_sec = 10;
        tv.tv_usec = 0;
        setsockopt(clientFd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

        // 1. Expect Register (cmd=0, ver=30) with 24-byte header
        JCOREWireFrame *regFrame = readSingleClientFrameOnServer(clientFd);
        ASSERT_TRUE(regFrame != nil && regFrame.command == JCOREWireCommandRegister && regFrame.version == 30,
                    "Expected Register(cmd=0, ver=30), got cmd=%u ver=%u", regFrame.command, regFrame.version);
        ASSERT_TRUE(regFrame.sessionId > 0, "Expected non-zero random register seed in bytes 12..15");
        serverRegistered = YES;

        NSData *regRespBody = [JCOREWireProtocol buildRegisterResponsePayloadWithStatusCode:0
                                                                                        uid:assignedUid
                                                                                   password:assignedPassword
                                                                             registrationID:assignedRegId
                                                                                   deviceID:@"dev_e2e_001"];
        NSData *regRespFrame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandRegister
                                                                       version:30
                                                                     requestId:regFrame.requestId
                                                                           uid:assignedUid
                                                                     algorithm:regFrame.algorithm
                                                                    statusCode:0
                                                                      bodyData:regRespBody];
        ASSERT_TRUE(writeExactBytes(clientFd, regRespFrame.bytes, regRespFrame.length), "Failed writing Register response");

        // 2. Expect Login (cmd=1, ver=27)
        JCOREWireFrame *loginFrame = readSingleClientFrameOnServer(clientFd);
        ASSERT_TRUE(loginFrame != nil && loginFrame.command == JCOREWireCommandLogin && loginFrame.version == 27,
                    "Expected Login(cmd=1, ver=27), got cmd=%u ver=%u", loginFrame.command, loginFrame.version);
        ASSERT_TRUE(loginFrame.uid == assignedUid, "Login frame UID mismatch: %llu", loginFrame.uid);
        const uint8_t *loginBytes = loginFrame.bodyData.bytes;
        ASSERT_TRUE(loginFrame.bodyData.length > 20 && loginBytes[0] == 0 && loginBytes[1] == 0 && loginBytes[2] == 0 && loginBytes[3] == 0x69,
                    "Login payload missing 0x00000069 magic");
        serverLoggedIn = YES;

        NSData *loginRespBody = [JCOREWireProtocol buildLoginResponsePayloadWithSessionId:assignedSessionId
                                                                            serverVersion:600
                                                                          serverTimestamp:1700000123];
        NSData *loginRespFrame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandLogin
                                                                         version:27
                                                                       requestId:loginFrame.requestId
                                                                             uid:assignedUid
                                                                       algorithm:loginFrame.algorithm
                                                                      statusCode:0
                                                                        bodyData:loginRespBody];
        ASSERT_TRUE(writeExactBytes(clientFd, loginRespFrame.bytes, loginRespFrame.length), "Failed writing Login response");

        // 3. Process 9 client commands: DeviceToken(13), VoIP(33), Tag(28), Alias(29), Properties(38), MobileNumber(26), LiveActivity x2 (39), PushCtrl(18)
        for (int i = 0; i < 9; i++) {
            JCOREWireFrame *req = readSingleClientFrameOnServer(clientFd);
            ASSERT_TRUE(req != nil, "Failed reading client command #%d", i);
            ASSERT_TRUE(req.sessionId == assignedSessionId, "Expected sessionId=0x%x in client header, got 0x%x", assignedSessionId, req.sessionId);
            if (req.command == JCOREWireCommandDeviceToken) {
                ASSERT_TRUE(req.version == 3, "DeviceToken version must be 3");
                serverDeviceToken = [JCOREWireProtocol parseDeviceTokenFromFrame:req];
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandDeviceToken
                                                                      version:req.version
                                                                    requestId:req.requestId
                                                                          uid:assignedUid
                                                                    algorithm:req.algorithm
                                                                   statusCode:0
                                                                     bodyData:nil];
                ASSERT_TRUE(writeExactBytes(clientFd, ack.bytes, ack.length), "Failed writing DeviceToken ack");
            } else if (req.command == JCOREWireCommandVoipToken) {
                ASSERT_TRUE(req.version == 2, "VoipToken version must be 2");
                serverVoipToken = [JCOREWireProtocol parseVoipTokenFromFrame:req];
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandVoipToken
                                                                      version:req.version
                                                                    requestId:req.requestId
                                                                          uid:assignedUid
                                                                    algorithm:req.algorithm
                                                                   statusCode:0
                                                                     bodyData:nil];
                ASSERT_TRUE(writeExactBytes(clientFd, ack.bytes, ack.length), "Failed writing VoipToken ack");
            } else if (req.command == JCOREWireCommandTagOperation) {
                ASSERT_TRUE(req.version == 1, "Tag version must be 1");
                serverTagJson = [JCOREWireProtocol parseJsonBodyFromFrame:req];
                NSData *respBody = [JCOREWireProtocol buildJsonCommandPayload:@{@"code": @0, @"tags": serverTagJson[@"tags"] ?: @[]}];
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandTagOperation
                                                                      version:req.version
                                                                    requestId:req.requestId
                                                                          uid:assignedUid
                                                                    algorithm:req.algorithm
                                                                   statusCode:0
                                                                     bodyData:respBody];
                ASSERT_TRUE(writeExactBytes(clientFd, ack.bytes, ack.length), "Failed writing Tag ack");
            } else if (req.command == JCOREWireCommandAliasOperation) {
                ASSERT_TRUE(req.version == 1, "Alias version must be 1");
                serverAliasJson = [JCOREWireProtocol parseJsonBodyFromFrame:req];
                NSData *respBody = [JCOREWireProtocol buildJsonCommandPayload:@{@"code": @0, @"alias": serverAliasJson[@"alias"] ?: @""}];
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandAliasOperation
                                                                      version:req.version
                                                                    requestId:req.requestId
                                                                          uid:assignedUid
                                                                    algorithm:req.algorithm
                                                                   statusCode:0
                                                                     bodyData:respBody];
                ASSERT_TRUE(writeExactBytes(clientFd, ack.bytes, ack.length), "Failed writing Alias ack");
            } else if (req.command == JCOREWireCommandPropertyOperation) {
                ASSERT_TRUE(req.version == 1, "UserProperties version must be 1");
                serverPropsJson = [JCOREWireProtocol parseJsonBodyFromFrame:req];
                NSData *respBody = [JCOREWireProtocol buildUserPropertiesResponsePayloadWithStatusCode:0 jsonPayload:@{@"code": @0}];
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandPropertyOperation
                                                                      version:req.version
                                                                    requestId:req.requestId
                                                                          uid:assignedUid
                                                                    algorithm:req.algorithm
                                                                   statusCode:0
                                                                     bodyData:respBody];
                ASSERT_TRUE(writeExactBytes(clientFd, ack.bytes, ack.length), "Failed writing Properties ack");
            } else if (req.command == JCOREWireCommandMobileNumber) {
                ASSERT_TRUE(req.version == 0, "MobileNumber version must be 0");
                const uint8_t *mb = req.bodyData.bytes;
                ASSERT_TRUE(req.bodyData.length >= 4 && mb[0] == 7 && mb[1] == 1, "MobileNumber must start with 7, 1");
                serverMobileNumber = [JCOREWireProtocol parseMobileNumberFromFrame:req];
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandMobileNumber
                                                                      version:req.version
                                                                    requestId:req.requestId
                                                                          uid:assignedUid
                                                                    algorithm:req.algorithm
                                                                   statusCode:0
                                                                     bodyData:nil];
                ASSERT_TRUE(writeExactBytes(clientFd, ack.bytes, ack.length), "Failed writing MobileNumber ack");
            } else if (req.command == JCOREWireCommandLiveActivity) {
                ASSERT_TRUE(req.version == 1, "LiveActivity version must be 1");
                // Read token string and alias/attributes string, then flag byte
                NSUInteger off = 0;
                uint16_t tLen = 0;
                [req.bodyData getBytes:&tLen range:NSMakeRange(0, 2)];
                tLen = CFSwapInt16BigToHost(tLen);
                off = 2 + tLen;
                uint16_t aLen = 0;
                [req.bodyData getBytes:&aLen range:NSMakeRange(off, 2)];
                aLen = CFSwapInt16BigToHost(aLen);
                NSString *aliasStr = [[NSString alloc] initWithData:[req.bodyData subdataWithRange:NSMakeRange(off + 2, aLen)] encoding:NSUTF8StringEncoding];
                off += 2 + aLen;
                uint8_t laFlag = 0;
                [req.bodyData getBytes:&laFlag range:NSMakeRange(off, 1)];
                if (laFlag == 0) {
                    serverSawLiveActivityStart = YES;
                } else if (laFlag == 1) {
                    serverSawLiveActivityUpdate = YES;
                }
                NSData *laResp = [JCOREWireProtocol buildLiveActivityResponsePayloadWithStatusCode:0 alias:aliasStr];
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandLiveActivity
                                                                      version:1
                                                                    requestId:req.requestId
                                                                          uid:assignedUid
                                                                    algorithm:req.algorithm
                                                                   statusCode:0
                                                                     bodyData:laResp];
                ASSERT_TRUE(writeExactBytes(clientFd, ack.bytes, ack.length), "Failed writing LiveActivity ack");
            } else if (req.command == JCOREWireCommandPushCtrl) {
                ASSERT_TRUE(req.version == 1, "PushCtrl version must be 1");
                NSDictionary *pushCtrlJson = [JCOREWireProtocol parseJsonBodyFromFrame:req];
                ASSERT_TRUE([pushCtrlJson[@"type"] isEqualToString:@"notif_state"] && [pushCtrlJson[@"enabled"] boolValue] == YES,
                            "PushCtrl turnONPush body must have type=notif_state and enabled=YES, got %@", pushCtrlJson);
                serverSawPushCtrlResume = YES;
            } else {
                ASSERT_TRUE(NO, "Unexpected command from client: %u", req.command);
            }
        }

        // 4. Server pushes PushMessage (cmd=3, ver=4) with multi-line prefix + JSON, expects MessageReceipt (cmd=4, ver=3)
        NSString *pushContent = @"header_line\n{\"title\":\"OneKey Alert\",\"content\":\"BTC > 100k\",\"extras\":{\"chain\":\"btc\"}}";
        NSData *pushBody = [JCOREWireProtocol buildPushMessagePayloadWithMessageType:expectedMsgType
                                                                           messageId:expectedMsgId
                                                                             content:pushContent];
        NSData *pushFrame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandPushMessage
                                                                    version:4
                                                                  requestId:901
                                                                        uid:assignedUid
                                                                  algorithm:1
                                                                 statusCode:0
                                                                   bodyData:pushBody];
        ASSERT_TRUE(writeExactBytes(clientFd, pushFrame.bytes, pushFrame.length), "Failed writing PushMessage frame");

        JCOREWireFrame *ackFrame = readSingleClientFrameOnServer(clientFd);
        ASSERT_TRUE(ackFrame != nil && ackFrame.command == JCOREWireCommandMessageAck && ackFrame.version == 3,
                    "Expected MessageReceipt(cmd=4, ver=3), got cmd=%u ver=%u", ackFrame.command, ackFrame.version);
        BOOL parsedAck = [JCOREWireProtocol parseMessageAckFrame:ackFrame
                                                  outMessageType:&serverAckMsgType
                                                    outMessageId:&serverAckMsgId];
        ASSERT_TRUE(parsedAck, "Failed parsing MessageReceipt payload");

        // 5. Server sends CtrlCommand (cmd=25, ver=1) and expects CtrlAck (cmd=25, ver=1)
        NSData *ctrlBody = [JCOREWireProtocol buildCtrlCommandPayloadWithRequestId:expectedCtrlReqId
                                                                       jsonPayload:@{@"cmd": @1, @"content": @{@"op": @"sync"}}];
        NSData *ctrlFrame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandCtrlCommand
                                                                    version:1
                                                                  requestId:902
                                                                        uid:assignedUid
                                                                  algorithm:1
                                                                 statusCode:0
                                                                   bodyData:ctrlBody];
        ASSERT_TRUE(writeExactBytes(clientFd, ctrlFrame.bytes, ctrlFrame.length), "Failed writing CtrlCommand frame");

        JCOREWireFrame *ctrlAckFrame = readSingleClientFrameOnServer(clientFd);
        ASSERT_TRUE(ctrlAckFrame != nil && ctrlAckFrame.command == JCOREWireCommandCtrlCommand && ctrlAckFrame.version == 1,
                    "Expected CtrlAck(cmd=25, ver=1), got cmd=%u ver=%u", ctrlAckFrame.command, ctrlAckFrame.version);
        ASSERT_TRUE(ctrlAckFrame.bodyData.length == 10, "CtrlAck body must be 10 bytes (uint16 + uint64)");
        uint64_t rawCtrlReqId = 0;
        [ctrlAckFrame.bodyData getBytes:&rawCtrlReqId range:NSMakeRange(2, 8)];
        serverCtrlAckReqId = CFSwapInt64BigToHost(rawCtrlReqId);

        close(clientFd);
        close(listenFd);
        dispatch_semaphore_signal(serverDoneSem);
    });

    // Reset client state & point SIS override to our loopback mock server
    [[JCOREConfiguration shared] resetRegistration];
    ASSERT_TRUE([[JPUSHService registrationID] isEqualToString:@""],
                "Expected empty registrationID before TCP Register (cmd=0), got %@", [JPUSHService registrationID]);
    ASSERT_TRUE([JCOREConfiguration shared].uid == 0,
                "Expected uid == 0 before TCP Register (cmd=0), got %llu", [JCOREConfiguration shared].uid);
    [[JCORESisResolver shared] setCustomConnectHost:@"127.0.0.1" port:serverPort useSSL:NO];

    __block BOOL sawConnecting = NO;
    __block BOOL sawSetup = NO;
    __block BOOL sawRegister = NO;
    __block BOOL sawLogin = NO;
    __block NSDictionary *receivedMessagePayload = nil;
    __block NSDictionary *receivedCtrlPayload = nil;
    dispatch_semaphore_t loginSem = dispatch_semaphore_create(0);
    dispatch_semaphore_t pushMsgSem = dispatch_semaphore_create(0);
    dispatch_semaphore_t ctrlSem = dispatch_semaphore_create(0);

    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    id obs1 = [nc addObserverForName:kJPFNetworkIsConnectingNotification object:nil queue:nil usingBlock:^(NSNotification *note) {
        sawConnecting = YES;
    }];
    id obs2 = [nc addObserverForName:kJPFNetworkDidSetupNotification object:nil queue:nil usingBlock:^(NSNotification *note) {
        sawSetup = YES;
    }];
    id obs3 = [nc addObserverForName:kJPFNetworkDidRegisterNotification object:nil queue:nil usingBlock:^(NSNotification *note) {
        sawRegister = YES;
    }];
    id obs4 = [nc addObserverForName:kJPFNetworkDidLoginNotification object:nil queue:nil usingBlock:^(NSNotification *note) {
        sawLogin = YES;
        dispatch_semaphore_signal(loginSem);
    }];
    id obs5 = [nc addObserverForName:kJPFNetworkDidReceiveMessageNotification object:nil queue:nil usingBlock:^(NSNotification *note) {
        receivedMessagePayload = note.userInfo;
        dispatch_semaphore_signal(pushMsgSem);
    }];
    id obs6 = [nc addObserverForName:@"kJPFNetworkDidReceiveCtrlCommandNotification" object:nil queue:nil usingBlock:^(NSNotification *note) {
        receivedCtrlPayload = note.userInfo;
        dispatch_semaphore_signal(ctrlSem);
    }];

    [JGCOREAPI printLog:YES];
    // Non-production demo test AppKey used solely for automated native tests.
    [JPUSHService setupWithOption:@{}
                           appKey:@"1f2027d3cfb1b51dac664c42"
                          channel:@"App Store"
                 apsForProduction:NO];
    [JPUSHService turnONPush];

    uint8_t rawToken[32];
    for (int i = 0; i < 32; i++) {
        rawToken[i] = (uint8_t)(i + 1);
    }
    NSData *tokenData = [NSData dataWithBytes:rawToken length:sizeof(rawToken)];
    [JPUSHService registerDeviceToken:tokenData];

    uint8_t rawVoip[16];
    for (int i = 0; i < 16; i++) {
        rawVoip[i] = (uint8_t)(0xA0 + i);
    }
    NSData *voipData = [NSData dataWithBytes:rawVoip length:sizeof(rawVoip)];
    [JPUSHService registerVoipToken:voipData];

    dispatch_semaphore_t opsSem = dispatch_semaphore_create(0);
    __block int completedOps = 0;
    void (^markOpComplete)(void) = ^{
        @synchronized ([JPUSHService class]) {
            completedOps++;
            if (completedOps == 6) {
                dispatch_semaphore_signal(opsSem);
            }
        }
    };

    [JPUSHService setTags:[NSSet setWithObjects:@"vip", @"btc", nil]
               completion:^(NSInteger iResCode, NSSet *iTags, NSInteger seq) {
        ASSERT_TRUE(iResCode == 0, "setTags failed with code %ld", (long)iResCode);
        ASSERT_TRUE([iTags containsObject:@"vip"] && [iTags containsObject:@"btc"], "setTags tag set mismatch");
        ASSERT_TRUE(seq == 101, "setTags seq mismatch: %ld", (long)seq);
        markOpComplete();
    } seq:101];

    [JPUSHService setAlias:@"onekey_user_01"
                completion:^(NSInteger iResCode, NSString *iAlias, NSInteger seq) {
        ASSERT_TRUE(iResCode == 0, "setAlias failed with code %ld", (long)iResCode);
        ASSERT_TRUE([iAlias isEqualToString:@"onekey_user_01"], "setAlias value mismatch: %@", iAlias);
        ASSERT_TRUE(seq == 102, "setAlias seq mismatch: %ld", (long)seq);
        markOpComplete();
    } seq:102];

    [JPUSHService setProperties:@{@"tier": @"pro"}
                     completion:^(NSInteger iResCode, NSDictionary *iProperties, NSInteger seq) {
        ASSERT_TRUE(iResCode == 0, "setProperties failed with code %ld", (long)iResCode);
        ASSERT_TRUE([iProperties[@"tier"] isEqualToString:@"pro"], "setProperties value mismatch");
        ASSERT_TRUE(seq == 103, "setProperties seq mismatch: %ld", (long)seq);
        markOpComplete();
    } seq:103];

    [JPUSHService setMobileNumber:@"13800138000" completion:^(NSError *error) {
        ASSERT_TRUE(error == nil, "setMobileNumber failed with error: %@", error);
        markOpComplete();
    }];

    [JPUSHService registerLiveActivity:@"OneKeyTradeAttributes"
                      pushToStartToken:voipData
                            completion:^(NSInteger iResCode, NSString *iLiveActivityId, NSData *token, NSInteger seq) {
        ASSERT_TRUE(iResCode == 0 && [iLiveActivityId isEqualToString:@"OneKeyTradeAttributes"] && seq == 104,
                    "registerLiveActivity pushToStartToken failed");
        markOpComplete();
    } seq:104];

    [JPUSHService registerLiveActivityId:@"trade_activity_01"
                               pushToken:voipData
                              completion:^(NSInteger iResCode, NSString *iLiveActivityId, NSData *token, NSInteger seq) {
        ASSERT_TRUE(iResCode == 0 && [iLiveActivityId isEqualToString:@"trade_activity_01"] && seq == 105,
                    "registerLiveActivityId pushToken failed");
        markOpComplete();
    } seq:105];

    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:10.0];
    while (dispatch_semaphore_wait(loginSem, DISPATCH_TIME_NOW) != 0) {
        ASSERT_TRUE([[NSDate date] compare:deadline] == NSOrderedAscending, "Timed out waiting for Login");
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    }
    while (dispatch_semaphore_wait(opsSem, DISPATCH_TIME_NOW) != 0) {
        ASSERT_TRUE([[NSDate date] compare:deadline] == NSOrderedAscending, "Timed out waiting for business ops");
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    }
    while (dispatch_semaphore_wait(pushMsgSem, DISPATCH_TIME_NOW) != 0) {
        ASSERT_TRUE([[NSDate date] compare:deadline] == NSOrderedAscending, "Timed out waiting for PushMessage notification");
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    }
    while (dispatch_semaphore_wait(ctrlSem, DISPATCH_TIME_NOW) != 0) {
        ASSERT_TRUE([[NSDate date] compare:deadline] == NSOrderedAscending, "Timed out waiting for CtrlCommand notification");
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    }
    while (dispatch_semaphore_wait(serverDoneSem, DISPATCH_TIME_NOW) != 0) {
        ASSERT_TRUE([[NSDate date] compare:deadline] == NSOrderedAscending, "Timed out waiting for mock server completion");
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    }

    [nc removeObserver:obs1];
    [nc removeObserver:obs2];
    [nc removeObserver:obs3];
    [nc removeObserver:obs4];
    [nc removeObserver:obs5];
    [nc removeObserver:obs6];

    [[JCORETcpSession shared] disconnect];
    [[JCORESisResolver shared] clearCustomConnectEndpoint];

    ASSERT_TRUE(sawConnecting && sawSetup && sawRegister && sawLogin, "Did not observe full network notification sequence");
    ASSERT_TRUE([[JPUSHService registrationID] isEqualToString:assignedRegId], "RegistrationID mismatch");
    ASSERT_TRUE(serverRegistered && serverLoggedIn, "Server did not complete Register+Login");
    ASSERT_TRUE([serverDeviceToken isEqualToString:@"0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20"],
                "Server received unexpected device token: %@", serverDeviceToken);
    ASSERT_TRUE([serverVoipToken isEqualToString:@"a0a1a2a3a4a5a6a7a8a9aaabacadaeaf"],
                "Server received unexpected VoIP token: %@", serverVoipToken);
    ASSERT_TRUE([serverTagJson[@"platform"] isEqualToString:@"i"] && [serverTagJson[@"op"] isEqualToString:@"set"],
                "Server received unexpected Tag JSON: %@", serverTagJson);
    ASSERT_TRUE([serverAliasJson[@"platform"] isEqualToString:@"i"] && [serverAliasJson[@"op"] isEqualToString:@"set"] && [serverAliasJson[@"alias"] isEqualToString:@"onekey_user_01"],
                "Server received unexpected Alias JSON: %@", serverAliasJson);
    ASSERT_TRUE([serverPropsJson[@"op"] isEqualToString:@"set"] && [serverPropsJson[@"data"][@"tier"] isEqualToString:@"pro"],
                "Server received unexpected Properties JSON: %@", serverPropsJson);
    ASSERT_TRUE([serverMobileNumber isEqualToString:@"13800138000"],
                "Server received unexpected mobile number: %@", serverMobileNumber);
    ASSERT_TRUE(serverSawLiveActivityStart && serverSawLiveActivityUpdate,
                "Server did not receive both LiveActivity start (flag=0) and update (flag=1)");
    ASSERT_TRUE(serverSawPushCtrlResume,
                "Server did not receive queued PushCtrl(stopped=NO) from turnONPush after login");
    ASSERT_TRUE([receivedMessagePayload[@"title"] isEqualToString:@"OneKey Alert"] &&
                [receivedMessagePayload[@"_j_msgid"] unsignedLongLongValue] == expectedMsgId,
                "PushMessage payload mismatch: %@", receivedMessagePayload);
    ASSERT_TRUE(serverAckMsgId == expectedMsgId && serverAckMsgType == expectedMsgType,
                "Server MessageReceipt mismatch: id=%llu type=%u", serverAckMsgId, serverAckMsgType);
    ASSERT_TRUE(serverCtrlAckReqId == expectedCtrlReqId && [receivedCtrlPayload[@"requestId"] unsignedLongLongValue] == expectedCtrlReqId,
                "Server CtrlCommand ack mismatch: %llu", serverCtrlAckReqId);

    printf("[PASS] Suite 4: End-to-end TCP mock JPush server handshake (cmd=0,1,3,4,13,25,26,28,29,33,38,39) verified\n");
}

static void testFailoverFastLoginReRegisterAndHttpReport(void) {
    // 1. Reserve a dead port (bind then immediately close), a handshake-error port, and a live server port
    int deadFd = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in deadAddr;
    memset(&deadAddr, 0, sizeof(deadAddr));
    deadAddr.sin_family = AF_INET;
    deadAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    deadAddr.sin_port = 0;
    bind(deadFd, (struct sockaddr *)&deadAddr, sizeof(deadAddr));
    socklen_t deadLen = sizeof(deadAddr);
    getsockname(deadFd, (struct sockaddr *)&deadAddr, &deadLen);
    uint16_t deadPort = ntohs(deadAddr.sin_port);
    close(deadFd);

    int errFd = socket(AF_INET, SOCK_STREAM, 0);
    int reuse = 1;
    setsockopt(errFd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));
    struct sockaddr_in errAddr;
    memset(&errAddr, 0, sizeof(errAddr));
    errAddr.sin_family = AF_INET;
    errAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    errAddr.sin_port = 0;
    ASSERT_TRUE(bind(errFd, (struct sockaddr *)&errAddr, sizeof(errAddr)) == 0, "err bind() failed");
    ASSERT_TRUE(listen(errFd, 2) == 0, "err listen() failed");
    socklen_t errLen = sizeof(errAddr);
    getsockname(errFd, (struct sockaddr *)&errAddr, &errLen);
    uint16_t errPort = ntohs(errAddr.sin_port);

    int liveFd = socket(AF_INET, SOCK_STREAM, 0);
    setsockopt(liveFd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));
    struct sockaddr_in liveAddr;
    memset(&liveAddr, 0, sizeof(liveAddr));
    liveAddr.sin_family = AF_INET;
    liveAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    liveAddr.sin_port = 0;
    ASSERT_TRUE(bind(liveFd, (struct sockaddr *)&liveAddr, sizeof(liveAddr)) == 0, "live bind() failed");
    ASSERT_TRUE(listen(liveFd, 4) == 0, "live listen() failed");
    socklen_t liveLen = sizeof(liveAddr);
    getsockname(liveFd, (struct sockaddr *)&liveAddr, &liveLen);
    uint16_t livePort = ntohs(liveAddr.sin_port);

    NSString *expectedDeadIpPort = [NSString stringWithFormat:@"127.0.0.1:%u", deadPort];
    NSString *expectedErrIpPort = [NSString stringWithFormat:@"127.0.0.1:%u", errPort];

    // Client already has stored registration from Suite 4 (uid=900100200).
    // Candidate 0 (deadPort): TCP connect fails -> advances to Candidate 1
    // Candidate 1 (errPort): TCP connect succeeds, Fast-Login returns statusCode=1005 -> advances to Candidate 2
    // Candidate 2 (livePort):
    //   - On connection #1:
    //     - Client fast-logs-in (first frame is Login cmd=1, NOT Register cmd=0!)
    //     - Server replies with Login statusCode=108 (invalid uid), triggering automatic re-Register(cmd=0) -> Login(cmd=1)
    //     - Upon Login(cmd=1) success, failedIps is cleared!
    //     - Server then closes connection #1 to trigger auto-reconnect with exponential backoff
    //   - On connection #2 (auto-reconnect):
    //     - Client fast-logs-in with the new uid (900100201) and succeeds!
    const uint64_t newAssignedUid = 900100201ULL;
    NSString *const newPassword = @"mock_srv_pwd_re108";
    NSString *const newRegId = @"1a0018970a900100201";

    __block BOOL errCandidateAdvanced = NO;
    __block BOOL sawFailedIpsBeforeLogin = NO;
    __block BOOL firstFrameWasFastLogin = NO;
    __block BOOL reRegisteredAfter108 = NO;
    __block BOOL autoReconnectedAndLoggedIn = NO;
    dispatch_semaphore_t failoverDoneSem = dispatch_semaphore_create(0);

    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        struct sockaddr_in cAddr;
        socklen_t cLen = sizeof(cAddr);
        int cErrFd = accept(errFd, (struct sockaddr *)&cAddr, &cLen);
        ASSERT_TRUE(cErrFd >= 0, "accept on errFd failed");
        struct timeval tv = {.tv_sec = 10, .tv_usec = 0};
        setsockopt(cErrFd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

        JCOREWireFrame *errLoginFrame = readSingleClientFrameOnServer(cErrFd);
        ASSERT_TRUE(errLoginFrame != nil && errLoginFrame.command == JCOREWireCommandLogin,
                    "Expected Fast-Login (cmd=1) on errPort candidate");
        NSData *err1005Body = [JCOREWireProtocol buildLoginResponsePayloadWithStatusCode:1005
                                                                               sessionId:0
                                                                           serverVersion:600
                                                                            errorMessage:@"server busy"
                                                                         serverTimestamp:1700000199
                                                                                     idc:0];
        NSData *err1005Frame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandLogin
                                                                       version:27
                                                                     requestId:errLoginFrame.requestId
                                                                           uid:errLoginFrame.uid
                                                                     algorithm:1
                                                                    statusCode:1005
                                                                      bodyData:err1005Body];
        ASSERT_TRUE(writeExactBytes(cErrFd, err1005Frame.bytes, err1005Frame.length), "Failed writing 1005 response");
        errCandidateAdvanced = YES;
        close(cErrFd);
        close(errFd);

        int cFd1 = accept(liveFd, (struct sockaddr *)&cAddr, &cLen);
        ASSERT_TRUE(cFd1 >= 0, "accept #1 failed");
        setsockopt(cFd1, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

        // Connection #1: Expect Fast-Login (cmd=1) first!
        JCOREWireFrame *fastLoginFrame = readSingleClientFrameOnServer(cFd1);
        ASSERT_TRUE(fastLoginFrame != nil && fastLoginFrame.command == JCOREWireCommandLogin,
                    "Expected Fast-Login (cmd=1) on registered device, got cmd=%u", fastLoginFrame.command);
        firstFrameWasFastLogin = YES;
        NSSet<NSString *> *failedBeforeLogin = [NSSet setWithArray:[JCOREConfiguration shared].failedIps];
        sawFailedIpsBeforeLogin = [failedBeforeLogin containsObject:expectedDeadIpPort] &&
                                  [failedBeforeLogin containsObject:expectedErrIpPort];

        // Return statusCode = 108 to force re-registration
        NSData *err108Body = [JCOREWireProtocol buildLoginResponsePayloadWithStatusCode:108
                                                                              sessionId:0
                                                                          serverVersion:600
                                                                           errorMessage:@"uid invalid"
                                                                        serverTimestamp:1700000200
                                                                                    idc:0];
        NSData *err108Frame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandLogin
                                                                      version:27
                                                                    requestId:fastLoginFrame.requestId
                                                                          uid:fastLoginFrame.uid
                                                                    algorithm:1
                                                                   statusCode:108
                                                                     bodyData:err108Body];
        ASSERT_TRUE(writeExactBytes(cFd1, err108Frame.bytes, err108Frame.length), "Failed writing 108 response");

        // Expect immediate Register (cmd=0) on same connection
        JCOREWireFrame *regFrame = readSingleClientFrameOnServer(cFd1);
        ASSERT_TRUE(regFrame != nil && regFrame.command == JCOREWireCommandRegister,
                    "Expected Register(cmd=0) after statusCode=108, got cmd=%u", regFrame.command);
        reRegisteredAfter108 = YES;

        NSData *regRespBody = [JCOREWireProtocol buildRegisterResponsePayloadWithUid:newAssignedUid
                                                                            password:newPassword
                                                                      registrationID:newRegId];
        NSData *regRespFrame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandRegister
                                                                       version:30
                                                                     requestId:regFrame.requestId
                                                                           uid:newAssignedUid
                                                                     algorithm:1
                                                                    statusCode:0
                                                                      bodyData:regRespBody];
        ASSERT_TRUE(writeExactBytes(cFd1, regRespFrame.bytes, regRespFrame.length), "Failed writing new Register response");

        // Expect Login (cmd=1) with newAssignedUid
        JCOREWireFrame *loginAfter108 = readSingleClientFrameOnServer(cFd1);
        ASSERT_TRUE(loginAfter108 != nil && loginAfter108.command == JCOREWireCommandLogin && loginAfter108.uid == newAssignedUid,
                    "Expected Login with newAssignedUid after 108");
        NSData *loginOkBody = [JCOREWireProtocol buildLoginResponsePayloadWithSessionId:0x77889900
                                                                          serverVersion:600
                                                                        serverTimestamp:1700000205];
        NSData *loginOkFrame = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandLogin
                                                                       version:27
                                                                     requestId:loginAfter108.requestId
                                                                           uid:newAssignedUid
                                                                     algorithm:1
                                                                    statusCode:0
                                                                      bodyData:loginOkBody];
        ASSERT_TRUE(writeExactBytes(cFd1, loginOkFrame.bytes, loginOkFrame.length), "Failed writing Login OK");

        // Close cFd1 to simulate abrupt disconnect and trigger exponential backoff auto-reconnect
        close(cFd1);

        // Accept Connection #2 from auto-reconnect
        int cFd2 = accept(liveFd, (struct sockaddr *)&cAddr, &cLen);
        ASSERT_TRUE(cFd2 >= 0, "accept #2 (auto-reconnect) failed");
        setsockopt(cFd2, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

        JCOREWireFrame *reconnectLogin = readSingleClientFrameOnServer(cFd2);
        ASSERT_TRUE(reconnectLogin != nil && reconnectLogin.command == JCOREWireCommandLogin && reconnectLogin.uid == newAssignedUid,
                    "Expected Fast-Login on auto-reconnect");
        NSData *loginOkFrame2 = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandLogin
                                                                        version:27
                                                                      requestId:reconnectLogin.requestId
                                                                            uid:newAssignedUid
                                                                      algorithm:1
                                                                     statusCode:0
                                                                       bodyData:loginOkBody];
        ASSERT_TRUE(writeExactBytes(cFd2, loginOkFrame2.bytes, loginOkFrame2.length), "Failed writing Login OK #2");
        autoReconnectedAndLoggedIn = YES;

        close(cFd2);
        close(liveFd);
        dispatch_semaphore_signal(failoverDoneSem);
    });

    JCOREConfiguration *config = [JCOREConfiguration shared];
    config.deviceTokenHex = nil;
    config.voipTokenHex = nil;
    [config clearFailedIps];
    config.connectTimeoutInterval = 0.5;
    config.reconnectBaseInterval = 0.15;
    [config updateWithSisDictionary:@{
        @"ssl_ips": @[],
        @"ips": @[
            expectedDeadIpPort,
            expectedErrIpPort,
            [NSString stringWithFormat:@"127.0.0.1:%u", livePort],
        ],
    }];

    __block int loginNotificationCount = 0;
    dispatch_semaphore_t twoLoginsSem = dispatch_semaphore_create(0);
    id loginObs = [[NSNotificationCenter defaultCenter] addObserverForName:kJPFNetworkDidLoginNotification
                                                                    object:nil
                                                                     queue:nil
                                                                usingBlock:^(NSNotification *note) {
        loginNotificationCount++;
        if (loginNotificationCount >= 2) {
            dispatch_semaphore_signal(twoLoginsSem);
        }
    }];

    [[JCORETcpSession shared] reconnect];

    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:10.0];
    while (dispatch_semaphore_wait(twoLoginsSem, DISPATCH_TIME_NOW) != 0) {
        ASSERT_TRUE([[NSDate date] compare:deadline] == NSOrderedAscending, "Timed out waiting for failover + 108 re-register + auto-reconnect");
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    }
    while (dispatch_semaphore_wait(failoverDoneSem, DISPATCH_TIME_NOW) != 0) {
        ASSERT_TRUE([[NSDate date] compare:deadline] == NSOrderedAscending, "Timed out waiting for failover server completion");
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    }

    [[NSNotificationCenter defaultCenter] removeObserver:loginObs];
    [[JCORETcpSession shared] disconnect];

    ASSERT_TRUE(errCandidateAdvanced && sawFailedIpsBeforeLogin,
                "Failed endpoints (%@, %@) must be recorded in failedIps before login succeeds",
                expectedDeadIpPort, expectedErrIpPort);
    ASSERT_TRUE(config.failedIps.count == 0,
                "failedIps must be cleared upon successful CMD_LOGIN, got: %@", config.failedIps);
    ASSERT_TRUE(firstFrameWasFastLogin && reRegisteredAfter108 && autoReconnectedAndLoggedIn,
                "Failover / Fast-Login / 108 Re-Register / Auto-Reconnect state flags incomplete");

    // 2. Verify HTTP Report (/v3/report) and Badge Report (setBadge:) formatting, Basic Auth, HMAC-SHA256 x-jg-sign & AES encryption
    NSString *reportAesKey = nil;
    NSURL *reportUrl = [NSURL URLWithString:@"https://stats.jpush.cn/v3/report"];
    NSURLRequest *httpReportReq = [JCOREHttpReporter buildHttpReportRequestWithURL:reportUrl
                                                                      contentItems:@[@{@"type": @"msg_status", @"msg_id": @"12345"}]
                                                                               uid:newAssignedUid
                                                                          password:newPassword
                                                                            appKey:@"e2e_test_app_key_001"
                                                                         outAesKey:&reportAesKey];
    ASSERT_TRUE(httpReportReq != nil && httpReportReq.HTTPBody.length > 0 && reportAesKey.length == 32,
                "Failed building /v3/report NSURLRequest");
    NSDictionary *decryptedReport = [JCOREHttpReporter verifyAndDecryptHttpReportBody:httpReportReq.HTTPBody
                                                                  authorizationHeader:[httpReportReq valueForHTTPHeaderField:@"Authorization"]
                                                                          expectedUid:newAssignedUid
                                                                             password:newPassword];
    ASSERT_TRUE([decryptedReport[@"platform"] isEqualToString:@"i"] &&
                [decryptedReport[@"uid"] unsignedLongLongValue] == newAssignedUid &&
                [decryptedReport[@"content"] isKindOfClass:[NSArray class]],
                "/v3/report server verification & decryption failed: %@", decryptedReport);

    NSString *badgeKey16 = nil;
    NSURLRequest *badgeReq = [JCOREHttpReporter buildBadgeReportRequestWithBaseURL:nil
                                                                        badgeValue:7
                                                                               uid:newAssignedUid
                                                                            appKey:@"e2e_test_app_key_001"
                                                                       outAesKey16:&badgeKey16];
    ASSERT_TRUE(badgeReq != nil && badgeKey16.length == 16, "Failed building badge report NSURLRequest");
    NSString *badgeBodyStr = [[NSString alloc] initWithData:badgeReq.HTTPBody encoding:NSUTF8StringEncoding];
    NSDictionary *decryptedBadge = [JCOREHttpReporter verifyAndDecryptBadgeReportHeaders:badgeReq.allHTTPHeaderFields
                                                                              bodyString:badgeBodyStr
                                                                                  appKey:@"e2e_test_app_key_001"
                                                                                aesKey16:badgeKey16];
    ASSERT_TRUE([decryptedBadge[@"badge"] integerValue] == 7 && [decryptedBadge[@"uid"] unsignedLongLongValue] == newAssignedUid,
                "Badge report x-jg-sign verification & AES-128-CBC decryption failed: %@", decryptedBadge);

    // 3. Verify default logging and non-sensitive log redaction on iOS
    [JCOREConfiguration clearRecentLogs];
    [JCOREConfiguration logInfo:@"onNotifyMessage type=notificationArrived notificationType=remote msgId=msg_99001"];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"TCP register success uid=%llu registrationId=%@", newAssignedUid, newRegId]];
    NSString *joinedIosLogs = [[JCOREConfiguration recentLogs] componentsJoinedByString:@"\n"];
    ASSERT_TRUE([joinedIosLogs containsString:@"notificationType=remote"] && [joinedIosLogs containsString:newRegId],
                "Expected non-sensitive notificationType and registrationId in iOS logs: %@", joinedIosLogs);

    printf("[PASS] Suite 5: Multi-IP failover (fail_ips), Fast-Login, statusCode=108 re-register, auto-reconnect, & HTTP reports (/v3/report + setBadge:) verified\n");
}

@implementation RCTBridge
- (NSDictionary *)launchOptions {
    return nil;
}
- (void)enqueueJSCall:(NSString *)module method:(NSString *)method args:(NSArray *)args completion:(dispatch_block_t)completion {
    if (completion) {
        completion();
    }
}
@end

@interface MockRCTBridge : RCTBridge
@property (nonatomic, copy) void (^onEvent)(NSString *name);
@property (nonatomic, strong) NSMutableArray<NSDictionary *> *emittedEvents;
@end

@implementation MockRCTBridge
- (instancetype)init {
    self = [super init];
    if (self) {
        _emittedEvents = [NSMutableArray array];
    }
    return self;
}
- (NSDictionary *)launchOptions {
    return @{};
}
- (void)enqueueJSCall:(NSString *)module method:(NSString *)method args:(NSArray *)args completion:(dispatch_block_t)completion {
    if ([module isEqualToString:@"RCTDeviceEventEmitter"] && [method isEqualToString:@"emit"] && args.count >= 2) {
        [_emittedEvents addObject:@{
            @"name": args[0] ?: @"",
            @"payload": args[1] ?: @{},
        }];
        if (self.onEvent) { self.onEvent(args[0]); }
    }
    if (completion) {
        completion();
    }
}
@end

@interface MockCallableJSModules : NSObject
@property (nonatomic, strong) NSMutableArray<NSDictionary *> *emittedEvents;
- (void)invokeModule:(NSString *)moduleName method:(NSString *)methodName withArgs:(NSArray *)args;
@end

@implementation MockCallableJSModules
- (instancetype)init {
    self = [super init];
    if (self) {
        _emittedEvents = [NSMutableArray array];
    }
    return self;
}
- (void)invokeModule:(NSString *)moduleName method:(NSString *)methodName withArgs:(NSArray *)args {
    if ([moduleName isEqualToString:@"RCTDeviceEventEmitter"] && [methodName isEqualToString:@"emit"] && args.count >= 2) {
        [_emittedEvents addObject:@{
            @"name": args[0] ?: @"",
            @"payload": args[1] ?: @{},
        }];
    }
}
@end

@interface BridgelessRCTJPushModule : RCTJPushModule
@property (nonatomic, strong) MockCallableJSModules *mockCallableModules;
@end

@implementation BridgelessRCTJPushModule
- (id)callableJSModules {
    return _mockCallableModules;
}
@end

@interface DummyRegisterDelegate : NSObject <JPUSHRegisterDelegate>
@property (nonatomic, assign) NSUInteger callbackCount;
@end
@implementation DummyRegisterDelegate
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger))completionHandler { self.callbackCount++; completionHandler(UNNotificationPresentationOptionAlert); }
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler { self.callbackCount++; completionHandler(); }
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center openSettingsForNotification:(UNNotification *)notification {}
- (void)jpushNotificationAuthorization:(JPAuthorizationStatus)status withInfo:(NSDictionary *)info {}
@end

static void testRctJPushModuleMultiRuntimeAndBridgelessEvents(void) {
    [[JCORETcpSession shared] disconnect];
    [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.05]];
    [RCTJPushModule clearActiveModules];
    [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
    [[RCTJPushEventQueue sharedInstance]._localNotificationQueue removeAllObjects];

    // 1. Emit before any module has a bridge/callableJSModules attached -> must buffer into sPendingEvents
    [RCTJPushModule emitEventWithName:@"TagAliasEvent" payload:@{@"code": @0, @"sequence": @77, @"alias": @"cold_start_alias"}];

    // Queue a cold-start APNs notification in RCTJPushEventQueue
    [[RCTJPushEventQueue sharedInstance]._notificationQueue addObject:@{
        @"_j_msgid": @"apns_cold_01",
        @"aps": @{@"alert": @{@"title": @"ColdTitle", @"body": @"ColdBody"}},
    }];

    // 2. Instantiate main runtime module (with RCTBridge) and bg runtime module (Bridgeless with callableJSModules, bridge == nil)
    //    WITHOUT calling startObserving (_hasListeners == NO on both!).
    RCTJPushModule *mainModule = [[RCTJPushModule alloc] init];
    BridgelessRCTJPushModule *bgModule = [[BridgelessRCTJPushModule alloc] init];
    MockCallableJSModules *bgCallable = [[MockCallableJSModules alloc] init];
    bgModule.mockCallableModules = bgCallable;

    MockRCTBridge *mainBridge = [[MockRCTBridge alloc] init];
    [mainModule setValue:mainBridge forKey:@"bridge"];

    // Verify init and setBridge: ONLY flush sPendingEvents (flushPendingBridgeEvents) and do NOT prematurely drain _notificationQueue!
    [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.02]];
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._notificationQueue.count == 1,
                "init / setBridge: must NOT prematurely drain _notificationQueue before flushQueuedNotifications / initPush / setupWithConfig:");
    ASSERT_TRUE(mainBridge.emittedEvents.count == 1 && [mainBridge.emittedEvents.firstObject[@"name"] isEqualToString:@"TagAliasEvent"],
                "Expected only sPendingEvents (TagAliasEvent) to be flushed on setBridge:, got %lu",
                (unsigned long)mainBridge.emittedEvents.count);
    ASSERT_TRUE(bgCallable.emittedEvents.count == 1 && [bgCallable.emittedEvents.firstObject[@"name"] isEqualToString:@"TagAliasEvent"],
                "Expected only sPendingEvents (TagAliasEvent) to be flushed on bgCallable during setBridge:, got %lu",
                (unsigned long)bgCallable.emittedEvents.count);

    // Now explicitly flushQueuedNotifications (as called by setupWithConfig: / initPush / loadJS / startObserving)
    [RCTJPushModule flushQueuedNotifications];
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._notificationQueue.count == 0,
                "_notificationQueue must be drained after flushQueuedNotifications");

    // Both mainModule and bgModule must have received the buffered TagAliasEvent AND the flushed APNs NotificationEvent!
    ASSERT_TRUE(mainBridge.emittedEvents.count == 2,
                "Expected 2 flushed events on mainBridge when _hasListeners==NO, got %lu",
                (unsigned long)mainBridge.emittedEvents.count);
    ASSERT_TRUE(bgCallable.emittedEvents.count == 2,
                "Expected 2 flushed events on bgCallable (bridgeless) when _hasListeners==NO, got %lu",
                (unsigned long)bgCallable.emittedEvents.count);

    // 3. Emit a live event via +emitEventWithName:payload: -> both main and bg must receive it
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"live_msg_02", @"content": @"hello_dual_runtime"}];
    ASSERT_TRUE(mainBridge.emittedEvents.count == 3 && [mainBridge.emittedEvents.lastObject[@"name"] isEqualToString:@"CustomMessageEvent"],
                "mainBridge did not receive live CustomMessageEvent");
    ASSERT_TRUE(bgCallable.emittedEvents.count == 3 && [bgCallable.emittedEvents.lastObject[@"name"] isEqualToString:@"CustomMessageEvent"],
                "bgCallable did not receive live CustomMessageEvent");

    // 4. Verify setupOrRefreshJPushNativeState does NOT overwrite an existing AppDelegate registerDelegate
    DummyRegisterDelegate *appDelegateStub = [[DummyRegisterDelegate alloc] init];
    [JPUSHNotificationManager shared].registerDelegate = appDelegateStub;
    [mainModule setupOrRefreshJPushNativeState];
    ASSERT_TRUE([JPUSHNotificationManager shared].registerDelegate == appDelegateStub,
                "setupOrRefreshJPushNativeState must preserve existing registerDelegate");
    [JPUSHNotificationManager shared].registerDelegate = nil;

    // 5. Verify JCORESisResolver.defaultFallbackEndpoints uses plain TCP (useSSL == NO) on ports 7000-7003
    NSArray<JCORESisEndpoint *> *fallbackEps = [[JCORESisResolver shared] defaultFallbackEndpoints];
    NSUInteger expectedPortCount = [JCOREConfiguration shared].defaultConnectPorts.count;
    ASSERT_TRUE(fallbackEps.count >= expectedPortCount && expectedPortCount > 0, "Expected default fallback endpoints");
    NSUInteger verifiedPlainPorts = 0;
    for (JCORESisEndpoint *ep in fallbackEps) {
        if ([ep.host isEqualToString:[JCOREConfiguration shared].defaultConnectHost] && ep.port >= 7000 && ep.port <= 7009) {
            ASSERT_TRUE(ep.useSSL == NO, "Expected useSSL == NO on Jiguang TCP port %u", ep.port);
            verifiedPlainPorts++;
        }
    }
    ASSERT_TRUE(verifiedPlainPorts == expectedPortCount, "Expected %lu plain TCP fallback ports, got %lu",
                (unsigned long)expectedPortCount, (unsigned long)verifiedPlainPorts);

    // 6. Verify Nitro event hub (including when an eager RCTJPushModule with bridge already exists at cold start!):
    //    - Cold-start sPendingNitroEvents flush on registerNitroEventListener even when mainModule already emitted to RCTDeviceEventEmitter
    //    - Cold-start _notificationQueue flush on flushQueuedNotifications
    //    - NSNotificationCenter observer delivery via ensureSharedNativeObservers
    [RCTJPushModule clearActiveModules];
    [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
    [[RCTJPushEventQueue sharedInstance]._localNotificationQueue removeAllObjects];

    RCTJPushModule *eagerModule = [[RCTJPushModule alloc] init];
    MockRCTBridge *eagerBridge = [[MockRCTBridge alloc] init];
    [eagerModule setValue:eagerBridge forKey:@"bridge"];

    // Emit while eagerModule is active (requiresMainQueueSetup=YES), BEFORE any Nitro listener registers!
    [RCTJPushModule emitEventWithName:@"TagAliasEvent" payload:@{@"code": @0, @"sequence": @88, @"alias": @"pure_nitro_alias"}];
    [[RCTJPushEventQueue sharedInstance]._notificationQueue addObject:@{
        @"_j_msgid": @"nitro_apns_cold_02",
        @"aps": @{@"alert": @{@"title": @"NitroTitle", @"body": @"NitroBody"}},
    }];

    NSMutableArray<NSDictionary *> *nitroEvents = [NSMutableArray array];
    NSInteger nitroListenerId = [RCTJPushModule registerNitroEventListener:^(NSString *eventName, NSString *payloadJson) {
        [nitroEvents addObject:@{
            @"name": eventName ?: @"",
            @"json": payloadJson ?: @"",
        }];
    }];
    ASSERT_TRUE(nitroListenerId > 0, "Expected positive nitroListenerId");
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._notificationQueue.count == 0,
                "_notificationQueue must be drained when registerNitroEventListener is called");
    ASSERT_TRUE(nitroEvents.count == 2 && [nitroEvents.firstObject[@"name"] isEqualToString:@"TagAliasEvent"] &&
                [nitroEvents.firstObject[@"json"] containsString:@"pure_nitro_alias"],
                "Expected cold-start TagAliasEvent flushed from sPendingNitroEvents even when eagerModule was active, got %@", nitroEvents);
    ASSERT_TRUE([nitroEvents[1][@"name"] isEqualToString:@"NotificationEvent"] &&
                [nitroEvents[1][@"json"] containsString:@"nitro_apns_cold_02"],
                "Expected queued APNs NotificationEvent delivered to Nitro listener, got %@", nitroEvents);

    [[NSNotificationCenter defaultCenter] postNotificationName:J_CUSTOM_NOTIFICATION_EVENT
                                                        object:@{@"_j_msgid": @"nitro_custom_03", @"title": @"T", @"content": @"C"}];
    ASSERT_TRUE(nitroEvents.count == 3 && [nitroEvents[2][@"name"] isEqualToString:@"CustomMessageEvent"] &&
                [nitroEvents[2][@"json"] containsString:@"nitro_custom_03"],
                "Expected NSNotificationCenter J_CUSTOM_NOTIFICATION_EVENT delivered in pure Nitro mode without RCTBridge");

    [RCTJPushModule unregisterNitroEventListener:nitroListenerId];
    [RCTJPushModule clearActiveModules];

    // 6b. Verify cold-start J_APNS_NOTIFICATION_ARRIVED_EVENT and J_LOCAL_NOTIFICATION_ARRIVED_EVENT
    //     preserve notificationEventType == "notificationArrived" without leaking _j_InternalNotificationName
    [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
    [[RCTJPushEventQueue sharedInstance]._localNotificationQueue removeAllObjects];
    [RCTJPushModule ensureSharedNativeObservers];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_ARRIVED_EVENT
                                                        object:@{
                                                            @"_j_msgid": @"apns_arrived_cold_601",
                                                            @"aps": @{@"alert": @{@"title": @"ArrivedApns", @"body": @"Body"}},
                                                            @"customKey": @"customVal",
                                                        }];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_ARRIVED_EVENT
                                                        object:@{
                                                            @"messageID": @"local_arrived_cold_602",
                                                            @"title": @"ArrivedLocal",
                                                            @"content": @"LocalBody",
                                                            @"extras": @{@"k": @"v"},
                                                        }];
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._notificationQueue.count == 1,
                "Expected 1 queued APNs arrived notification");
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._localNotificationQueue.count == 1,
                "Expected 1 queued local arrived notification");
    NSMutableArray<NSDictionary *> *arrivedColdEvents = [NSMutableArray array];
    NSInteger arrivedListenerId = [RCTJPushModule registerNitroEventListener:^(NSString *eventName, NSString *payloadJson) {
        [arrivedColdEvents addObject:@{@"name": eventName ?: @"", @"json": payloadJson ?: @""}];
    }];
    ASSERT_TRUE(arrivedColdEvents.count == 2, "Expected 2 flushed arrived events, got %@", arrivedColdEvents);
    ASSERT_TRUE([arrivedColdEvents[0][@"name"] isEqualToString:@"NotificationEvent"] &&
                [arrivedColdEvents[0][@"json"] containsString:@"apns_arrived_cold_601"] &&
                [arrivedColdEvents[0][@"json"] containsString:@"notificationArrived"] &&
                ![arrivedColdEvents[0][@"json"] containsString:@"_j_InternalNotificationName"],
                "Cold-start APNs arrived event must preserve notificationArrived without leaking internal key, got %@", arrivedColdEvents[0]);
    ASSERT_TRUE([arrivedColdEvents[1][@"name"] isEqualToString:@"LocalNotificationEvent"] &&
                [arrivedColdEvents[1][@"json"] containsString:@"local_arrived_cold_602"] &&
                [arrivedColdEvents[1][@"json"] containsString:@"notificationArrived"] &&
                ![arrivedColdEvents[1][@"json"] containsString:@"_j_InternalNotificationName"],
                "Cold-start local arrived event must preserve notificationArrived without leaking internal key, got %@", arrivedColdEvents[1]);
    [RCTJPushModule unregisterNitroEventListener:arrivedListenerId];
    [RCTJPushModule clearActiveModules];

    // 6c. Verify JCOREConfiguration.setLocalLogEnabled buffers logs even when logOff == YES and clears when disabled
    [JCOREConfiguration clearRecentLogs];
    [JCOREConfiguration shared].logOff = YES;
    [JCOREConfiguration setLocalLogEnabled:YES];
    [JCOREConfiguration logInfo:@"local_log_while_off_01"];
    ASSERT_TRUE([JCOREConfiguration recentLogs].count == 1 &&
                [[JCOREConfiguration recentLogs].firstObject containsString:@"local_log_while_off_01"],
                "setLocalLogEnabled(YES) must buffer logs into recentLogs even when logOff == YES");
    [JCOREConfiguration setLocalLogEnabled:NO];
    ASSERT_TRUE([JCOREConfiguration recentLogs].count == 0,
                "setLocalLogEnabled(NO) must clear recentLogs");
    [JCOREConfiguration logInfo:@"local_log_disabled_02"];
    ASSERT_TRUE([JCOREConfiguration recentLogs].count == 0,
                "setLocalLogEnabled(NO) must prevent buffering into recentLogs");
    [JCOREConfiguration setLocalLogEnabled:YES];
    [JCOREConfiguration shared].logOff = NO;
    [JCOREConfiguration clearRecentLogs];

    // 7. Verify [JPUSHService setupWithOption:@{UIApplicationLaunchOptionsRemoteNotificationKey: ...}]
    //    registers RCTJPushModule native observers at cold start and queues the launch notification into RCTJPushEventQueue
    [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
    [JPUSHService setupWithOption:@{
        @"UIApplicationLaunchOptionsRemoteNotificationKey": @{
            @"_j_msgid": @"launch_apns_cold_99",
            @"aps": @{@"alert": @{@"title": @"LaunchTitle", @"body": @"LaunchBody"}},
        },
    }
                           appKey:@"1f2027d3cfb1b51dac664c42"
                          channel:@"App Store"
                 apsForProduction:NO];
    [[JCORETcpSession shared] disconnect];
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._notificationQueue.count == 1,
                "setupWithOption with UIApplicationLaunchOptionsRemoteNotificationKey must queue cold-start notification in RCTJPushEventQueue");
    NSMutableArray<NSDictionary *> *launchNitroEvents = [NSMutableArray array];
    NSInteger launchListenerId = [RCTJPushModule registerNitroEventListener:^(NSString *eventName, NSString *payloadJson) {
        [launchNitroEvents addObject:@{@"name": eventName ?: @"", @"json": payloadJson ?: @""}];
    }];
    [RCTJPushModule flushQueuedNotifications];
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._notificationQueue.count == 0,
                "_notificationQueue must be empty after flushQueuedNotifications");
    ASSERT_TRUE(launchNitroEvents.count >= 1 &&
                [launchNitroEvents.lastObject[@"name"] isEqualToString:@"NotificationEvent"] &&
                [launchNitroEvents.lastObject[@"json"] containsString:@"launch_apns_cold_99"] &&
                [launchNitroEvents.lastObject[@"json"] containsString:@"notificationOpened"],
                "Cold-start launch remote notification must be delivered as notificationOpened NotificationEvent, got %@", launchNitroEvents);
    [RCTJPushModule unregisterNitroEventListener:launchListenerId];
    [RCTJPushModule clearActiveModules];
    printf("[PASS] Suite 6: Pure Nitro Event Hub, RCTJPushModule DeviceEventEmitter (_hasListeners==NO), RN 0.88 Bridgeless (callableJSModules), Dual-Runtime (main + bg), & Delegate preservation verified\n");
}

static void runUntil(BOOL (^condition)(void), NSTimeInterval timeout, NSString *message) {
    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:timeout];
    while (!condition()) {
        ASSERT_TRUE([deadline timeIntervalSinceNow] > 0, "%@", message);
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
    }
}

static void testPolicyAndPendingContentCleanup(void) {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    [[JCORETcpSession shared] disconnect];
    config.collectionAuthorized = YES;
    config.pushStopped = NO;
    [RCTJPushModule clearActiveModules];
    [RCTJPushModule ensureSharedNativeObservers];
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"pending-custom"}];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_OPENED_EVENT object:@{@"_j_msgid": @"pending-apns"}];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_OPENED_EVENT object:@{@"messageID": @"pending-local"}];
    config.collectionAuthorized = NO;
    ASSERT_TRUE([RCTJPushEventQueue sharedInstance]._notificationQueue.count == 0 &&
                [RCTJPushEventQueue sharedInstance]._localNotificationQueue.count == 0, "Revocation must synchronously clear native queues");
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"disabled"}];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_OPENED_EVENT object:@{@"_j_msgid": @"disabled-open"}];
    __block NSUInteger contentCount = 0;
    NSInteger listener = [RCTJPushModule registerNitroEventListener:^(NSString *name, NSString *json) {
        if ([name isEqualToString:@"CustomMessageEvent"] || [name isEqualToString:@"NotificationEvent"]) { contentCount++; }
    }];
    config.collectionAuthorized = YES;
    [RCTJPushModule flushQueuedNotifications];
    ASSERT_TRUE(contentCount == 0, "Regrant must not replay old or disabled content");
    [RCTJPushModule unregisterNitroEventListener:listener];
    [RCTJPushModule clearActiveModules];

    NSUInteger staleGeneration = config.policyGeneration;
    config.pushStopped = YES;
    [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_OPENED_EVENT object:@{@"_j_msgid": @"stopped-open"}];
    config.pushStopped = NO;
    [[RCTJPushEventQueue sharedInstance]._notificationQueue addObject:@{@"_j_msgid": @"stale-raw-apns", @"_j_InternalPolicyGeneration": @(staleGeneration)}];
    [[RCTJPushEventQueue sharedInstance]._localNotificationQueue addObject:@{@"messageID": @"stale-raw-local", @"_j_InternalPolicyGeneration": @(staleGeneration)}];
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"fresh-generation"}];
    [[NSNotificationCenter defaultCenter] postNotificationName:JCOREPolicyDidChangeNotification object:config userInfo:@{@"generation": @(staleGeneration)}];
    __block NSUInteger freshEvents = 0;
    listener = [RCTJPushModule registerNitroEventListener:^(NSString *name, NSString *json) {
        if ([name isEqualToString:@"CustomMessageEvent"]) { freshEvents++; }
        else { ASSERT_TRUE(![name isEqualToString:@"NotificationEvent"] && ![name isEqualToString:@"LocalNotificationEvent"], "Old raw content must retain its original generation"); }
    }];
    ASSERT_TRUE(freshEvents == 1, "A delayed old policy notification must preserve fresh content");
    [[NSNotificationCenter defaultCenter] postNotificationName:J_CUSTOM_NOTIFICATION_EVENT
        object:@{@"_j_msgid": @"forwarded-old", @"_j_InternalPolicyGeneration": @(staleGeneration)}];
    ASSERT_TRUE(freshEvents == 1, "AppDelegate-shaped forwarding must preserve and discard an obsolete origin generation");
    [[NSNotificationCenter defaultCenter] postNotificationName:J_CUSTOM_NOTIFICATION_EVENT
        object:@{@"_j_msgid": @"forwarded-current", @"_j_InternalPolicyGeneration": @(config.policyGeneration)}];
    ASSERT_TRUE(freshEvents == 2, "AppDelegate-shaped forwarding must deliver current content once");
    [RCTJPushModule unregisterNitroEventListener:listener];
    [RCTJPushModule clearActiveModules];

    // A callback can withdraw and immediately regrant; the rest of its snapshot stays obsolete.
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"snapshot-first"}];
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"snapshot-second"}];
    __block NSUInteger snapshotCount = 0;
    listener = [RCTJPushModule registerNitroEventListener:^(NSString *name, NSString *json) {
        if (![name isEqualToString:@"CustomMessageEvent"]) { return; }
        snapshotCount++;
        config.collectionAuthorized = NO;
        config.collectionAuthorized = YES;
    }];
    ASSERT_TRUE(snapshotCount == 1, "Revocation inside a callback must invalidate the remaining snapshot");
    [RCTJPushModule unregisterNitroEventListener:listener];
    [RCTJPushModule clearActiveModules];

    RCTJPushModule *mainModule = [[RCTJPushModule alloc] init];
    RCTJPushModule *bgModule = [[RCTJPushModule alloc] init];
    MockRCTBridge *mainBridge = [[MockRCTBridge alloc] init];
    MockRCTBridge *bgBridge = [[MockRCTBridge alloc] init];
    [mainModule setValue:mainBridge forKey:@"bridge"];
    [bgModule setValue:bgBridge forKey:@"bridge"];
    [mainModule performSelector:NSSelectorFromString(@"invalidate")];
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"bg-survives"}];
    ASSERT_TRUE(mainBridge.emittedEvents.count == 0 && bgBridge.emittedEvents.count == 1,
                "Invalidating one native context must retain the other context");
    [RCTJPushModule clearActiveModules];

    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"legacy-first"}];
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{@"messageID": @"legacy-second"}];
    RCTJPushModule *legacyModule = [[RCTJPushModule alloc] init];
    MockRCTBridge *legacyBridge = [[MockRCTBridge alloc] init];
    legacyBridge.onEvent = ^(NSString *name) {
        if ([name isEqualToString:@"CustomMessageEvent"]) { config.collectionAuthorized = NO; config.collectionAuthorized = YES; }
    };
    [legacyModule setValue:legacyBridge forKey:@"bridge"];
    ASSERT_TRUE(legacyBridge.emittedEvents.count == 1, "Legacy reentrant revocation must invalidate the remaining native snapshot");
    legacyBridge.onEvent = nil;
    [RCTJPushModule clearActiveModules];

    // Unregistering another listener during delivery invalidates that listener's snapshot entry.
    __block NSInteger firstListener = 0;
    __block NSInteger secondListener = 0;
    __block NSUInteger liveCallbacks = 0;
    firstListener = [RCTJPushModule registerNitroEventListener:^(NSString *name, NSString *json) {
        liveCallbacks++; [RCTJPushModule unregisterNitroEventListener:secondListener];
    }];
    secondListener = [RCTJPushModule registerNitroEventListener:^(NSString *name, NSString *json) {
        liveCallbacks++; [RCTJPushModule unregisterNitroEventListener:firstListener];
    }];
    [RCTJPushModule emitEventWithName:@"CustomMessageEvent" payload:@{}];
    ASSERT_TRUE(liveCallbacks == 1, "Unregistered listener must not receive a retained snapshot callback");
    [RCTJPushModule clearActiveModules];

    config.collectionAuthorized = NO;
    DummyRegisterDelegate *delegate = [[DummyRegisterDelegate alloc] init];
    [JPUSHNotificationManager shared].registerDelegate = delegate;
    __block NSUInteger unCompletions = 0;
    [(id<UNUserNotificationCenterDelegate>)[JPUSHNotificationManager shared] userNotificationCenter:nil willPresentNotification:nil
        withCompletionHandler:^(UNNotificationPresentationOptions options) {
        ASSERT_TRUE(options == UNNotificationPresentationOptionNone, "Disabled UN callback must suppress presentation"); unCompletions++;
    }];
    [(id<UNUserNotificationCenterDelegate>)[JPUSHNotificationManager shared] userNotificationCenter:nil didReceiveNotificationResponse:nil
        withCompletionHandler:^{ unCompletions++; }];
    ASSERT_TRUE(unCompletions == 2 && delegate.callbackCount == 0, "Disabled UN callbacks must complete without forwarding to the host");
    [JPUSHNotificationManager shared].registerDelegate = nil;
    __block NSUInteger httpFailures = 0;
    [[JCOREHttpReporter shared] sendHttpReportItems:@[@{@"type": @"test"}] completion:^(BOOL ok, NSInteger status) {
        ASSERT_TRUE(!ok, "Disabled HTTP report must fail"); httpFailures++;
    }];
    [[JCOREHttpReporter shared] reportBadgeValue:1 completion:^(BOOL ok, NSInteger status) {
        ASSERT_TRUE(!ok, "Disabled badge report must fail"); httpFailures++;
    }];
    ASSERT_TRUE(httpFailures == 2, "Disabled HTTP entrypoints must complete immediately");
    [JPUSHService setupWithOption:@{} appKey:@"1f2027d3cfb1b51dac664c42" channel:@"test" apsForProduction:NO];
    __block BOOL blockedSend = NO;
    [[JCORETcpSession shared] sendHeartbeatNowWithCompletion:^(JCOREWireFrame *frame, NSError *error) {
        blockedSend = error.code == 6002;
    }];
    runUntil(^BOOL{ return blockedSend; }, 2, @"Disabled TCP send did not complete");
    ASSERT_TRUE(![JCORETcpSession shared].isConnected, "Disabled setup must not connect");
    [[JCORETcpSession shared] disconnect];
    config.appKey = @"";
    config.collectionAuthorized = YES;
    [[JCORETcpSession shared] startIfNeeded];
    __block NSUInteger cancelled = 0;
    __block BOOL overflow = NO;
    for (NSUInteger index = 0; index < 129; index++) {
        [[JCORETcpSession shared] sendCommand:JCOREWireCommandTagOperation version:1 bodyData:[NSData data]
            completion:^(JCOREWireFrame *frame, NSError *error) {
            ASSERT_TRUE(error.code == 6002, "Pending cancellation/overflow must retain code 6002");
            if (index == 128) { overflow = [error.localizedDescription containsString:@"queue full"]; }
            else { cancelled++; }
        }];
    }
    runUntil(^BOOL{ return overflow; }, 2, @"129th request was not rejected");
    ASSERT_TRUE(cancelled == 0, "Queue overflow must retain the first 128 requests");
    config.collectionAuthorized = NO;
    runUntil(^BOOL{ return cancelled == 128; }, 2, @"Revocation did not complete pending requests");
    config.collectionAuthorized = YES;
    [[JCORETcpSession shared] disconnect];
    printf("[PASS] Suite 7: Native policy, content snapshot invalidation, context isolation, disabled HTTP/TCP, and 128-request cleanup verified\n");
}

static void testTokenAcknowledgementAndConnectedPolicy(void) {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSData *deviceTokenA = [NSData dataWithBytes:"\x01\x02\x03\x04" length:4];
    NSData *deviceTokenB = [NSData dataWithBytes:"\x05\x06\x07\x08" length:4];
    NSData *voipToken = [NSData dataWithBytes:"\xa0\xa1\xa2\xa3" length:4];
    config.collectionAuthorized = NO;
    config.deviceTokenHex = @"01020304";
    config.voipTokenHex = @"a0a1a2a3";
    [config updateRegistrationWithUid:900100200 password:@"fixture-password" registrationID:@"fixture-rid"];
    config.requestTimeoutInterval = 0.25;
    config.requestResendInterval = 0.1;
    int listenFd = socket(AF_INET, SOCK_STREAM, 0);
    int reuse = 1;
    setsockopt(listenFd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));
    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    ASSERT_TRUE(bind(listenFd, (struct sockaddr *)&addr, sizeof(addr)) == 0 && listen(listenFd, 4) == 0, "Token fixture listener failed");
    socklen_t addrLength = sizeof(addr);
    getsockname(listenFd, (struct sockaddr *)&addr, &addrLength);
    [[JCORESisResolver shared] setCustomConnectHost:@"127.0.0.1" port:ntohs(addr.sin_port) useSSL:NO];
    dispatch_semaphore_t initialReports = dispatch_semaphore_create(0);
    dispatch_semaphore_t rotationStarted = dispatch_semaphore_create(0);
    dispatch_semaphore_t rotationRejected = dispatch_semaphore_create(0);
    dispatch_semaphore_t firstClosed = dispatch_semaphore_create(0);
    dispatch_semaphore_t secondBound = dispatch_semaphore_create(0);
    dispatch_semaphore_t serverDone = dispatch_semaphore_create(0);
    __block NSUInteger loginCount = 0;
    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        for (NSUInteger connectionIndex = 0; connectionIndex < 2; connectionIndex++) {
            int fd = accept(listenFd, NULL, NULL);
            ASSERT_TRUE(fd >= 0, "Token fixture accept failed");
            struct timeval tv = {.tv_sec = 5, .tv_usec = 0};
            setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));
            JCOREWireFrame *login = readSingleClientFrameOnServer(fd);
            ASSERT_TRUE(login.command == JCOREWireCommandLogin, "Token fixture expected fast login");
            NSData *loginAck = [JCOREWireProtocol encodeServerFrameWithCommand:JCOREWireCommandLogin version:27
                requestId:login.requestId uid:config.uid algorithm:login.algorithm statusCode:0
                bodyData:[JCOREWireProtocol buildLoginResponsePayloadWithSessionId:(uint32_t)(100 + connectionIndex) serverVersion:600 serverTimestamp:1]];
            ASSERT_TRUE(writeExactBytes(fd, loginAck.bytes, loginAck.length), "Token fixture login ACK failed");
            loginCount++;
            NSUInteger heartbeatCount = 0;
            NSUInteger deviceCount = 0;
            NSUInteger voipCount = 0;
            uint16_t firstVoipRid = 0;
            BOOL initialSignalled = NO;
            BOOL boundSignalled = NO;
            JCOREWireFrame *pendingRotation = nil;
            while (YES) {
                JCOREWireFrame *request = readSingleClientFrameOnServer(fd);
                if (request == nil) { break; }
                uint16_t status = 0;
                if (request.command == JCOREWireCommandDeviceToken) {
                    deviceCount++;
                    if (connectionIndex == 0 && deviceCount == 1) { status = 500; }
                    if (connectionIndex == 0 && deviceCount == 3) {
                        pendingRotation = request;
                        dispatch_semaphore_signal(rotationStarted);
                        continue;
                    }
                    if (connectionIndex == 0 && deviceCount == 6) {
                        dispatch_semaphore_signal(rotationStarted);
                        continue;
                    }
                    if (connectionIndex == 0 && deviceCount > 7) { ASSERT_TRUE(NO, "Superseded token must not resend after the current token ACK"); }
                    if (connectionIndex == 0 && deviceCount == 4) {
                        ASSERT_TRUE(pendingRotation != nil, "Expected the prior token report to remain pending");
                        NSData *oldAck = [JCOREWireProtocol encodeServerFrameWithCommand:pendingRotation.command version:pendingRotation.version
                            requestId:pendingRotation.requestId uid:config.uid algorithm:pendingRotation.algorithm statusCode:0 bodyData:[NSData data]];
                        ASSERT_TRUE(writeExactBytes(fd, oldAck.bytes, oldAck.length), "Old token ACK failed");
                        status = 500;
                    }
                } else if (request.command == JCOREWireCommandVoipToken) {
                    if (firstVoipRid == 0) { firstVoipRid = request.requestId; voipCount++; }
                    else if (request.requestId != firstVoipRid) { voipCount++; }
                    if (connectionIndex == 0 && request.requestId == firstVoipRid) {
                        if (deviceCount > 0 && !initialSignalled) { initialSignalled = YES; dispatch_semaphore_signal(initialReports); }
                        continue;
                    }
                } else if (request.command == JCOREWireCommandHeartbeat) {
                    heartbeatCount++;
                    NSUInteger expectedDevices = heartbeatCount <= 2 ? 2 : (heartbeatCount == 3 ? 4 : (heartbeatCount == 4 ? 5 : 7));
                    ASSERT_TRUE(connectionIndex == 0 && deviceCount == expectedDevices && voipCount == 2,
                                "Failed/timeout token must retry once; successful token must deduplicate");
                } else if (request.command == JCOREWireCommandPushCtrl) {
                    ASSERT_TRUE(connectionIndex == 1, "Stop control must use the resumed connection");
                    uint8_t byte = 0;
                    ASSERT_TRUE(recv(fd, &byte, 1, 0) == 0, "Stop control must be followed by connection close");
                    break;
                } else {
                    ASSERT_TRUE(NO, "Unexpected token fixture command %u", request.command);
                }
                NSData *ack = [JCOREWireProtocol encodeServerFrameWithCommand:request.command version:request.version
                    requestId:request.requestId uid:config.uid algorithm:request.algorithm statusCode:status bodyData:[NSData data]];
                ASSERT_TRUE(writeExactBytes(fd, ack.bytes, ack.length), "Token fixture ACK failed");
                if (connectionIndex == 0 && request.command == JCOREWireCommandDeviceToken && (deviceCount == 4 || deviceCount == 7)) {
                    dispatch_semaphore_signal(rotationRejected);
                }
                if (connectionIndex == 0 && deviceCount > 0 && firstVoipRid > 0 && !initialSignalled) {
                    initialSignalled = YES; dispatch_semaphore_signal(initialReports);
                }
                if (connectionIndex == 1 && deviceCount == 1 && voipCount == 1 && !boundSignalled) {
                    boundSignalled = YES; dispatch_semaphore_signal(secondBound);
                }
            }
            close(fd);
            if (connectionIndex == 0) { dispatch_semaphore_signal(firstClosed); }
        }
        close(listenFd);
        dispatch_semaphore_signal(serverDone);
    });
    [JPUSHService setupWithOption:@{} appKey:@"1f2027d3cfb1b51dac664c42" channel:@"test" apsForProduction:NO];
    config.collectionAuthorized = YES;
    runUntil(^BOOL{ return dispatch_semaphore_wait(initialReports, DISPATCH_TIME_NOW) == 0; }, 3, @"Initial token reports missing");
    NSDate *timeoutDeadline = [NSDate dateWithTimeIntervalSinceNow:0.4];
    while ([timeoutDeadline timeIntervalSinceNow] > 0) {
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
    }
    [JPUSHService registerDeviceToken:([config.deviceTokenHex isEqualToString:@"05060708"] ? deviceTokenB : deviceTokenA)];
    [JPUSHService registerVoipToken:voipToken];
    [[NSNotificationCenter defaultCenter] postNotificationName:JCOREPolicyDidChangeNotification object:config
        userInfo:@{@"generation": @(config.policyGeneration - 1)}];
    // A heartbeat ACK is ordered after token ACKs on the same native connection.
    __block BOOL firstBarrier = NO;
    [[JCORETcpSession shared] sendHeartbeatNowWithCompletion:^(JCOREWireFrame *frame, NSError *error) { firstBarrier = frame != nil && error == nil; }];
    runUntil(^BOOL{ return firstBarrier; }, 3, @"Retried tokens were not acknowledged");
    [JPUSHService registerDeviceToken:([config.deviceTokenHex isEqualToString:@"05060708"] ? deviceTokenB : deviceTokenA)];
    [JPUSHService registerVoipToken:voipToken];
    __block BOOL secondBarrier = NO;
    [[JCORETcpSession shared] sendHeartbeatNowWithCompletion:^(JCOREWireFrame *frame, NSError *error) { secondBarrier = frame != nil && error == nil; }];
    runUntil(^BOOL{ return secondBarrier; }, 3, @"Successful token deduplication barrier failed");
    // An acknowledged A cannot remain a dedup key while a different token B is pending.
    config.deviceTokenHex = @"05060708";
    [JPUSHService registerDeviceToken:([config.deviceTokenHex isEqualToString:@"05060708"] ? deviceTokenB : deviceTokenA)];
    runUntil(^BOOL{ return dispatch_semaphore_wait(rotationStarted, DISPATCH_TIME_NOW) == 0; }, 2, @"Token B report missing");
    config.deviceTokenHex = @"01020304";
    [JPUSHService registerDeviceToken:([config.deviceTokenHex isEqualToString:@"05060708"] ? deviceTokenB : deviceTokenA)];
    runUntil(^BOOL{ return dispatch_semaphore_wait(rotationRejected, DISPATCH_TIME_NOW) == 0; }, 2, @"Current A was incorrectly deduplicated against historical ACK");
    __block BOOL rotationBarrier = NO;
    [[JCORETcpSession shared] sendHeartbeatNowWithCompletion:^(JCOREWireFrame *frame, NSError *error) { rotationBarrier = frame != nil && error == nil; }];
    runUntil(^BOOL{ return rotationBarrier; }, 2, @"Token rotation ACK barrier missing");
    [JPUSHService registerDeviceToken:([config.deviceTokenHex isEqualToString:@"05060708"] ? deviceTokenB : deviceTokenA)];
    __block BOOL finalTokenBarrier = NO;
    [[JCORETcpSession shared] sendHeartbeatNowWithCompletion:^(JCOREWireFrame *frame, NSError *error) { finalTokenBarrier = frame != nil && error == nil; }];
    runUntil(^BOOL{ return finalTokenBarrier; }, 2, @"Current A retry was poisoned by obsolete B acknowledgement");
    config.deviceTokenHex = @"05060708";
    [JPUSHService registerDeviceToken:([config.deviceTokenHex isEqualToString:@"05060708"] ? deviceTokenB : deviceTokenA)];
    runUntil(^BOOL{ return dispatch_semaphore_wait(rotationStarted, DISPATCH_TIME_NOW) == 0; }, 2, @"Second token B report missing");
    config.deviceTokenHex = @"01020304";
    [JPUSHService registerDeviceToken:([config.deviceTokenHex isEqualToString:@"05060708"] ? deviceTokenB : deviceTokenA)];
    runUntil(^BOOL{ return dispatch_semaphore_wait(rotationRejected, DISPATCH_TIME_NOW) == 0; }, 2, @"Current token replacement ACK missing");
    NSDate *obsoleteResendDeadline = [NSDate dateWithTimeIntervalSinceNow:0.4];
    while ([obsoleteResendDeadline timeIntervalSinceNow] > 0) {
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
    }
    __block BOOL obsoleteResendBarrier = NO;
    [[JCORETcpSession shared] sendHeartbeatNowWithCompletion:^(JCOREWireFrame *frame, NSError *error) { obsoleteResendBarrier = frame != nil && error == nil; }];
    runUntil(^BOOL{ return obsoleteResendBarrier; }, 2, @"Obsolete token resend check failed");
    config.collectionAuthorized = NO;
    runUntil(^BOOL{ return dispatch_semaphore_wait(firstClosed, DISPATCH_TIME_NOW) == 0; }, 3, @"Revocation did not close the existing connection");
    ASSERT_TRUE(![JCORETcpSession shared].isConnected, "Revoked connection must be closed");
    config.collectionAuthorized = YES;
    runUntil(^BOOL{ return dispatch_semaphore_wait(secondBound, DISPATCH_TIME_NOW) == 0; }, 3, @"Regrant did not reconnect and rebind current tokens");
    ASSERT_TRUE(loginCount == 2, "Regrant must make one new login");
    [JPUSHService turnOFFPush];
    runUntil(^BOOL{ return dispatch_semaphore_wait(serverDone, DISPATCH_TIME_NOW) == 0; }, 3, @"Stop did not send control and close");
    config.requestTimeoutInterval = 20;
    config.requestResendInterval = 10;
    [[JCORETcpSession shared] disconnect];
    [[JCORESisResolver shared] clearCustomConnectEndpoint];
    config.appKey = @"";
    config.pushStopped = NO;
    printf("[PASS] Suite 8: Token failed-ACK/timeout retry, successful ACK deduplication, connected revocation/regrant, and stop control close verified\n");
}

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        testCryptoPrimitives();
        testSisWireFormatAndUdpLoopback();
        testWireProtocolFraming();
        testEndToEndMockServerHandshake();
        testFailoverFastLoginReRegisterAndHttpReport();
        testRctJPushModuleMultiRuntimeAndBridgelessEvents();
        testPolicyAndPendingContentCleanup();
        testTokenAcknowledgementAndConnectedPolicy();
    }
    return 0;
}
