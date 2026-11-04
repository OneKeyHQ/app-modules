#import "JCOREWireProtocol.h"
#import "JCOREConfiguration.h"
#import "JCORECrypto.h"
#if __has_include(<UIKit/UIKit.h>)
#import <UIKit/UIKit.h>
#endif

static NSString *const kJCOREReportRsaPublicKey =
    @"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGHnOJjf1yD7SOWt0w4q5ZqER+VMOTJ9oPh+"
    @"9TNgW8WpXwCklSLxFDM46eHWzT2TV46gZ0A6xTNVHKX4zAW621jfb6qzljTvwzM5Cxa5n2440kU8HF/"
    @"EDu04yqUpWCsNXs+jQD4R2gjGE203LW87fBivmfq0mxPvIR94UdKf10YQIDAQAB";

static void JCoreAppendUInt8(NSMutableData *data, uint8_t value) {
    [data appendBytes:&value length:sizeof(uint8_t)];
}

static void JCoreAppendUInt16(NSMutableData *data, uint16_t value) {
    uint16_t be = CFSwapInt16HostToBig(value);
    [data appendBytes:&be length:sizeof(uint16_t)];
}

static void JCoreAppendUInt32(NSMutableData *data, uint32_t value) {
    uint32_t be = CFSwapInt32HostToBig(value);
    [data appendBytes:&be length:sizeof(uint32_t)];
}

static void JCoreAppendUInt64(NSMutableData *data, uint64_t value) {
    uint64_t be = CFSwapInt64HostToBig(value);
    [data appendBytes:&be length:sizeof(uint64_t)];
}

static void JCoreAppendString(NSMutableData *data, NSString *string) {
    NSData *utf8 = [string ?: @"" dataUsingEncoding:NSUTF8StringEncoding];
    uint16_t len = (uint16_t)MIN((NSUInteger)UINT16_MAX, utf8.length);
    JCoreAppendUInt16(data, len);
    if (len > 0) {
        [data appendBytes:utf8.bytes length:len];
    }
}

static BOOL JCoreReadUInt8(NSData *data, NSUInteger *offset, uint8_t *outValue) {
    if (*offset + 1 > data.length) {
        return NO;
    }
    [data getBytes:outValue range:NSMakeRange(*offset, 1)];
    *offset += 1;
    return YES;
}

static BOOL JCoreReadUInt16(NSData *data, NSUInteger *offset, uint16_t *outValue) {
    if (*offset + 2 > data.length) {
        return NO;
    }
    uint16_t raw = 0;
    [data getBytes:&raw range:NSMakeRange(*offset, 2)];
    *outValue = CFSwapInt16BigToHost(raw);
    *offset += 2;
    return YES;
}

static BOOL JCoreReadUInt32(NSData *data, NSUInteger *offset, uint32_t *outValue) {
    if (*offset + 4 > data.length) {
        return NO;
    }
    uint32_t raw = 0;
    [data getBytes:&raw range:NSMakeRange(*offset, 4)];
    *outValue = CFSwapInt32BigToHost(raw);
    *offset += 4;
    return YES;
}

static BOOL JCoreReadUInt64(NSData *data, NSUInteger *offset, uint64_t *outValue) {
    if (*offset + 8 > data.length) {
        return NO;
    }
    uint64_t raw = 0;
    [data getBytes:&raw range:NSMakeRange(*offset, 8)];
    *outValue = CFSwapInt64BigToHost(raw);
    *offset += 8;
    return YES;
}

static NSString *JCoreReadString(NSData *data, NSUInteger *offset) {
    uint16_t len = 0;
    if (!JCoreReadUInt16(data, offset, &len)) {
        return nil;
    }
    if (*offset + len > data.length) {
        return nil;
    }
    NSData *slice = [data subdataWithRange:NSMakeRange(*offset, len)];
    *offset += len;
    return [[NSString alloc] initWithData:slice encoding:NSUTF8StringEncoding] ?: @"";
}

@implementation JCOREWireFrame
@end

@implementation JCORERegisterResponse
@end

@implementation JCORELoginResponse
@end

@implementation JCOREPushEnvelope
@end

@implementation JCORECtrlEnvelope
@end

@implementation JCORELiveActivityResponse
@end

@implementation JCOREWireProtocol

#pragma mark - Body Encryption & Decryption

+ (nullable NSData *)encryptBodyData:(NSData *)plainData
                            withSeed:(uint64_t)seed
                           algorithm:(uint8_t)algorithm {
    NSData *input = plainData ?: [NSData data];
    NSString *key = [JCORECrypto generateSeedKey:seed];
    NSString *ivStr = [key substringToIndex:MIN((NSUInteger)16, key.length)];
    if (algorithm == 2) {
        return [JCORECrypto sm4CBCEncryptData:input withKey:key iv:ivStr.UTF8String];
    }
    return [JCORECrypto aesCbcEncrypt:input key:key iv:ivStr.UTF8String keySize:32];
}

+ (nullable NSData *)decryptBodyData:(NSData *)cipherData
                            withSeed:(uint64_t)seed
                           algorithm:(uint8_t)algorithm {
    if (cipherData.length == 0) {
        return [NSData data];
    }
    NSString *key = [JCORECrypto generateSeedKey:seed];
    NSString *ivStr = [key substringToIndex:MIN((NSUInteger)16, key.length)];
    if (algorithm == 2) {
        return [JCORECrypto sm4CBCDecryptData:cipherData withKey:key iv:ivStr.UTF8String];
    }
    return [JCORECrypto aesCbcDecrypt:cipherData key:key iv:ivStr.UTF8String keySize:32];
}

#pragma mark - Outbound Client -> Server Framing (24-byte header)

+ (NSData *)encodeClientFrameWithCommand:(uint8_t)command
                                 version:(uint8_t)version
                               requestId:(uint16_t)requestId
                               sessionId:(uint32_t)sessionId
                                     uid:(uint64_t)uid
                               algorithm:(uint8_t)algorithm
                                bodyData:(NSData *)bodyData {
    uint8_t effectiveAlg = (algorithm == 2) ? 2 : 1;
    uint32_t seedOrSid = sessionId;
    uint64_t packetUid = uid;

    if (command == JCOREWireCommandRegister) {
        packetUid = 0;
        if (seedOrSid == 0) {
            uint64_t savedSeed = [JCOREConfiguration shared].lastRegisterSeed;
            if (savedSeed > 0 && savedSeed <= UINT32_MAX) {
                seedOrSid = (uint32_t)savedSeed;
            } else {
                seedOrSid = (arc4random() % 10000000U) + 1U;
                [JCOREConfiguration shared].lastRegisterSeed = seedOrSid;
            }
        } else {
            [JCOREConfiguration shared].lastRegisterSeed = seedOrSid;
        }
    }

    uint64_t encryptionSeed = (command == JCOREWireCommandRegister || packetUid == 0)
        ? (uint64_t)seedOrSid
        : packetUid;

    NSData *encryptedBody = [self encryptBodyData:(bodyData ?: [NSData data])
                                         withSeed:encryptionSeed
                                        algorithm:effectiveAlg];
    if (encryptedBody == nil) {
        return nil;
    }

    uint16_t totalLength = (uint16_t)(24 + encryptedBody.length);
    NSMutableData *packet = [NSMutableData dataWithCapacity:totalLength];
    JCoreAppendUInt16(packet, (uint16_t)(totalLength | 0x8000));
    JCoreAppendUInt8(packet, version);
    JCoreAppendUInt8(packet, command);
    JCoreAppendUInt8(packet, effectiveAlg);
    JCoreAppendUInt8(packet, 0); // flag
    JCoreAppendUInt32(packet, 0); // reserved
    JCoreAppendUInt16(packet, requestId);
    JCoreAppendUInt32(packet, seedOrSid);
    JCoreAppendUInt64(packet, packetUid);
    if (encryptedBody.length > 0) {
        [packet appendData:encryptedBody];
    }
    return packet;
}

+ (NSData *)encodeFrameWithCommand:(uint8_t)command
                           version:(uint8_t)version
                         requestId:(uint16_t)requestId
                         sessionId:(uint32_t)sessionId
                               uid:(uint64_t)uid
                          bodyData:(NSData *)bodyData {
    uint8_t alg = [JCOREConfiguration shared].encryptionAlgorithm;
    if (alg != 1 && alg != 2) {
        alg = 1;
    }
    return [self encodeClientFrameWithCommand:command
                                      version:version
                                    requestId:requestId
                                    sessionId:sessionId
                                          uid:uid
                                    algorithm:alg
                                     bodyData:bodyData];
}

#pragma mark - Inbound Server -> Client Framing (20-byte header)

+ (NSData *)encodeServerFrameWithCommand:(uint8_t)command
                                 version:(uint8_t)version
                               requestId:(uint16_t)requestId
                                     uid:(uint64_t)uid
                               algorithm:(uint8_t)algorithm
                              statusCode:(uint16_t)statusCode
                                bodyData:(NSData *)bodyData {
    uint8_t effectiveAlg = (algorithm == 2) ? 2 : 1;
    NSMutableData *plainBody = [NSMutableData data];

    BOOL isServerPushOrCtrl = (command == JCOREWireCommandPushMessage ||
                               command == JCOREWireCommandCtrlCommand);
    BOOL isTagOrAlias = (command == JCOREWireCommandTagOperation ||
                         command == JCOREWireCommandAliasOperation);

    if (isServerPushOrCtrl) {
        if (bodyData.length > 0) {
            [plainBody appendData:bodyData];
        }
    } else if (isTagOrAlias) {
        if (bodyData.length > 0) {
            [plainBody appendData:bodyData];
        } else {
            JCoreAppendUInt16(plainBody, statusCode);
        }
    } else {
        if (bodyData.length == 0) {
            JCoreAppendUInt16(plainBody, statusCode);
        } else {
            [plainBody appendData:bodyData];
            if (statusCode != 0 && plainBody.length >= 2) {
                uint16_t existingStatus = 0;
                NSUInteger zeroOffset = 0;
                JCoreReadUInt16(plainBody, &zeroOffset, &existingStatus);
                if (existingStatus == 0) {
                    uint16_t beStatus = CFSwapInt16HostToBig(statusCode);
                    [plainBody replaceBytesInRange:NSMakeRange(0, 2) withBytes:&beStatus length:2];
                }
            }
        }
    }

    uint64_t encryptionSeed = uid;
    if (command == JCOREWireCommandRegister) {
        uint64_t regSeed = [JCOREConfiguration shared].lastRegisterSeed;
        encryptionSeed = (regSeed != 0) ? regSeed : uid;
    } else if (encryptionSeed == 0) {
        encryptionSeed = [JCOREConfiguration shared].lastRegisterSeed;
    }

    NSData *encryptedBody = [self encryptBodyData:plainBody
                                         withSeed:encryptionSeed
                                        algorithm:effectiveAlg];
    if (encryptedBody == nil) {
        return nil;
    }

    uint16_t totalLength = (uint16_t)(20 + encryptedBody.length);
    NSMutableData *packet = [NSMutableData dataWithCapacity:totalLength];
    JCoreAppendUInt16(packet, (uint16_t)(totalLength | 0x8000));
    JCoreAppendUInt8(packet, version);
    JCoreAppendUInt8(packet, command);
    JCoreAppendUInt8(packet, effectiveAlg);
    JCoreAppendUInt8(packet, 0); // flag
    JCoreAppendUInt32(packet, 0); // reserved
    JCoreAppendUInt16(packet, requestId);
    JCoreAppendUInt64(packet, uid);
    if (encryptedBody.length > 0) {
        [packet appendData:encryptedBody];
    }
    return packet;
}

+ (NSData *)encodeFrameWithCommand:(uint8_t)command
                           version:(uint8_t)version
                         requestId:(uint16_t)requestId
                         sessionId:(uint32_t)sessionId
                               uid:(uint64_t)uid
                        statusCode:(uint16_t)statusCode
                          bodyData:(NSData *)bodyData {
    (void)sessionId;
    uint8_t alg = [JCOREConfiguration shared].encryptionAlgorithm;
    if (alg != 1 && alg != 2) {
        alg = 1;
    }
    return [self encodeServerFrameWithCommand:command
                                      version:version
                                    requestId:requestId
                                          uid:uid
                                    algorithm:alg
                                   statusCode:statusCode
                                     bodyData:bodyData];
}

#pragma mark - Stream Decoders

+ (nullable JCOREWireFrame *)decodeClientSingleFrame:(NSData *)frameBytes {
    if (frameBytes.length < 24) {
        return nil;
    }
    NSUInteger cursor = 2;
    uint8_t version = 0;
    uint8_t command = 0;
    uint8_t algorithm = 1;
    uint8_t flag = 0;
    uint32_t reserved = 0;
    uint16_t requestId = 0;
    uint32_t seedOrSid = 0;
    uint64_t packetUid = 0;

    if (!JCoreReadUInt8(frameBytes, &cursor, &version) ||
        !JCoreReadUInt8(frameBytes, &cursor, &command) ||
        !JCoreReadUInt8(frameBytes, &cursor, &algorithm) ||
        !JCoreReadUInt8(frameBytes, &cursor, &flag) ||
        !JCoreReadUInt32(frameBytes, &cursor, &reserved) ||
        !JCoreReadUInt16(frameBytes, &cursor, &requestId) ||
        !JCoreReadUInt32(frameBytes, &cursor, &seedOrSid) ||
        !JCoreReadUInt64(frameBytes, &cursor, &packetUid)) {
        return nil;
    }

    if (command == JCOREWireCommandRegister && seedOrSid != 0) {
        [JCOREConfiguration shared].lastRegisterSeed = seedOrSid;
    }

    NSData *rawBody = frameBytes.length > 24
        ? [frameBytes subdataWithRange:NSMakeRange(24, frameBytes.length - 24)]
        : [NSData data];

    uint64_t seed = (command == JCOREWireCommandRegister || packetUid == 0)
        ? (uint64_t)seedOrSid
        : packetUid;

    NSData *decodedBody = [self decryptBodyData:rawBody withSeed:seed algorithm:algorithm];
    if (decodedBody == nil) {
        return nil;
    }

    JCOREWireFrame *frame = [[JCOREWireFrame alloc] init];
    frame.version = version;
    frame.command = command;
    frame.algorithm = algorithm;
    frame.flags = flag;
    frame.requestId = requestId;
    frame.sessionId = seedOrSid;
    frame.uid = packetUid;
    frame.statusCode = 0;
    frame.bodyData = decodedBody;
    return frame;
}

+ (nullable JCOREWireFrame *)decodeServerSingleFrame:(NSData *)frameBytes
                                          defaultUid:(uint64_t)defaultUid {
    if (frameBytes.length < 20) {
        return nil;
    }
    NSUInteger cursor = 2;
    uint8_t version = 0;
    uint8_t command = 0;
    uint8_t algorithm = 1;
    uint8_t flag = 0;
    uint32_t reserved = 0;
    uint16_t requestId = 0;
    uint64_t packetUid = 0;

    if (!JCoreReadUInt8(frameBytes, &cursor, &version) ||
        !JCoreReadUInt8(frameBytes, &cursor, &command) ||
        !JCoreReadUInt8(frameBytes, &cursor, &algorithm) ||
        !JCoreReadUInt8(frameBytes, &cursor, &flag) ||
        !JCoreReadUInt32(frameBytes, &cursor, &reserved) ||
        !JCoreReadUInt16(frameBytes, &cursor, &requestId) ||
        !JCoreReadUInt64(frameBytes, &cursor, &packetUid)) {
        return nil;
    }

    NSData *rawBody = frameBytes.length > 20
        ? [frameBytes subdataWithRange:NSMakeRange(20, frameBytes.length - 20)]
        : [NSData data];

    uint64_t lastRegSeed = [JCOREConfiguration shared].lastRegisterSeed;
    uint64_t primarySeed = 0;
    if (command == JCOREWireCommandRegister) {
        primarySeed = (lastRegSeed != 0) ? lastRegSeed : (packetUid != 0 ? packetUid : defaultUid);
    } else {
        primarySeed = (packetUid != 0) ? packetUid : (defaultUid != 0 ? defaultUid : lastRegSeed);
    }

    NSData *decodedBody = [self decryptBodyData:rawBody withSeed:primarySeed algorithm:algorithm];
    if (decodedBody == nil && packetUid != 0 && packetUid != primarySeed) {
        decodedBody = [self decryptBodyData:rawBody withSeed:packetUid algorithm:algorithm];
    }
    if (decodedBody == nil && defaultUid != 0 && defaultUid != primarySeed && defaultUid != packetUid) {
        decodedBody = [self decryptBodyData:rawBody withSeed:defaultUid algorithm:algorithm];
    }
    if (decodedBody == nil && lastRegSeed != 0 && lastRegSeed != primarySeed) {
        decodedBody = [self decryptBodyData:rawBody withSeed:lastRegSeed algorithm:algorithm];
    }
    if (decodedBody == nil) {
        return nil;
    }

    if ((flag & 0x01) != 0 && decodedBody.length >= 2) {
        const uint8_t *b = decodedBody.bytes;
        if (b[0] == 0x1f && b[1] == 0x8b) {
            NSData *unzipped = [JCORECrypto gzipDecompress:decodedBody];
            if (unzipped != nil) {
                decodedBody = unzipped;
            }
        }
    }

    uint16_t statusCode = 0;
    if (command != JCOREWireCommandPushMessage && command != JCOREWireCommandCtrlCommand) {
        if (command == JCOREWireCommandTagOperation || command == JCOREWireCommandAliasOperation) {
            if (decodedBody.length == 2) {
                NSUInteger scOffset = 0;
                JCoreReadUInt16(decodedBody, &scOffset, &statusCode);
            }
        } else if (decodedBody.length >= 2) {
            NSUInteger scOffset = 0;
            JCoreReadUInt16(decodedBody, &scOffset, &statusCode);
        }
    }

    JCOREWireFrame *frame = [[JCOREWireFrame alloc] init];
    frame.version = version;
    frame.command = command;
    frame.algorithm = algorithm;
    frame.flags = flag;
    frame.requestId = requestId;
    frame.sessionId = 0;
    frame.uid = packetUid;
    frame.statusCode = statusCode;
    frame.bodyData = decodedBody;
    return frame;
}

+ (NSArray<JCOREWireFrame *> *)decodeServerFramesFromStreamBuffer:(NSMutableData *)streamBuffer
                                                       defaultUid:(uint64_t)defaultUid {
    NSMutableArray<JCOREWireFrame *> *frames = [NSMutableArray array];
    while (streamBuffer.length >= 20) {
        const uint8_t *bytes = streamBuffer.bytes;
        uint16_t rawLen = ((uint16_t)bytes[0] << 8) | bytes[1];
        uint16_t frameLen = rawLen & 0x7FFF;
        if (frameLen < 20 || frameLen > 65535) {
            [streamBuffer setLength:0];
            break;
        }
        if (streamBuffer.length < frameLen) {
            break;
        }
        NSData *frameBytes = [streamBuffer subdataWithRange:NSMakeRange(0, frameLen)];
        [streamBuffer replaceBytesInRange:NSMakeRange(0, frameLen) withBytes:NULL length:0];

        JCOREWireFrame *frame = [self decodeServerSingleFrame:frameBytes defaultUid:defaultUid];
        if (frame != nil) {
            [frames addObject:frame];
        }
    }
    return frames;
}

+ (NSArray<JCOREWireFrame *> *)decodeClientFramesFromStreamBuffer:(NSMutableData *)streamBuffer {
    NSMutableArray<JCOREWireFrame *> *frames = [NSMutableArray array];
    while (streamBuffer.length >= 24) {
        const uint8_t *bytes = streamBuffer.bytes;
        uint16_t rawLen = ((uint16_t)bytes[0] << 8) | bytes[1];
        uint16_t frameLen = rawLen & 0x7FFF;
        if (frameLen < 24 || frameLen > 65535) {
            [streamBuffer setLength:0];
            break;
        }
        if (streamBuffer.length < frameLen) {
            break;
        }
        NSData *frameBytes = [streamBuffer subdataWithRange:NSMakeRange(0, frameLen)];
        [streamBuffer replaceBytesInRange:NSMakeRange(0, frameLen) withBytes:NULL length:0];

        JCOREWireFrame *frame = [self decodeClientSingleFrame:frameBytes];
        if (frame != nil) {
            [frames addObject:frame];
        }
    }
    return frames;
}

+ (NSArray<JCOREWireFrame *> *)decodeFramesFromStreamBuffer:(NSMutableData *)streamBuffer
                                                        uid:(uint64_t)uid {
    NSMutableArray<JCOREWireFrame *> *frames = [NSMutableArray array];
    while (streamBuffer.length >= 20) {
        const uint8_t *bytes = streamBuffer.bytes;
        uint16_t rawLen = ((uint16_t)bytes[0] << 8) | bytes[1];
        uint16_t frameLen = rawLen & 0x7FFF;
        if (frameLen < 20 || frameLen > 65535) {
            [streamBuffer setLength:0];
            break;
        }
        if (streamBuffer.length < frameLen) {
            break;
        }
        NSData *frameBytes = [streamBuffer subdataWithRange:NSMakeRange(0, frameLen)];
        [streamBuffer replaceBytesInRange:NSMakeRange(0, frameLen) withBytes:NULL length:0];

        JCOREWireFrame *frame = nil;
        if (frameLen >= 24 && ((frameLen - 24) % 16 == 0)) {
            frame = [self decodeClientSingleFrame:frameBytes];
        } else {
            frame = [self decodeServerSingleFrame:frameBytes defaultUid:uid];
        }
        if (frame != nil) {
            [frames addObject:frame];
        }
    }
    return frames;
}

#pragma mark - Register (cmd = 0, ver = 30)

+ (NSData *)buildRegisterPayloadWithAppKey:(NSString *)appKey
                                   channel:(NSString *)channel
                              bundleIdHash:(NSString *)bundleId {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSString *systemVersion = [config osVersionString];
    NSString *modelName = [config modelNameString];
    NSString *appVersion = [config appVersionString];
    NSString *effectiveBundleId = bundleId.length > 0 ? bundleId : [config bundleIdentifierString];
    NSString *deviceId = config.deviceID;
    NSString *fakeImei = [config fakeIMEI];

    NSString *keyString = [NSString stringWithFormat:@"%@$$ $$%@$$%@",
                           fakeImei ?: @"",
                           effectiveBundleId ?: @"",
                           appKey ?: @""];
    NSString *clientString = [NSString stringWithFormat:@"%@$$%@$$ $$ $$%@$$6.0.0|||5.2.0||||$$ $$ $$ $$ $$1",
                              systemVersion ?: @"",
                              modelName ?: @"",
                              channel.length > 0 ? channel : @"App Store"];
    NSString *deviceToken = config.deviceTokenHex ?: @"";
    NSString *voipToken = config.voipTokenHex ?: @"";
    NSString *tokenString = (deviceToken.length == 0 && voipToken.length == 0)
        ? @" "
        : [NSString stringWithFormat:@"%@$$%@", deviceToken, voipToken];
    NSString *advertisingString = config.advertisingIdentifier.length > 0
        ? config.advertisingIdentifier
        : @" ";
    NSString *deviceString = [NSString stringWithFormat:@"1$$%@", deviceId ?: @""];

    NSMutableData *body = [NSMutableData data];
    JCoreAppendString(body, keyString);          // 1. key
    JCoreAppendString(body, appVersion ?: @"");  // 2. appVersion
    JCoreAppendString(body, clientString);       // 3. client
    JCoreAppendString(body, tokenString);        // 4. token
    JCoreAppendString(body, advertisingString);  // 5. advertising
    JCoreAppendUInt8(body, config.buildType);    // 6. buildType (0=debug, 1=release)
    JCoreAppendUInt8(body, config.apsType);      // 7. apsType (0=dev, 1=prod)
    JCoreAppendUInt8(body, 1);                   // 8. platform (1=iOS)
    JCoreAppendString(body, deviceString);       // 9. device ("1$${deviceID}")
    JCoreAppendUInt32(body, 1);                  // 10. integrateBusiness (1=push)
    JCoreAppendString(body, @"");                // 11. account
    return body;
}

+ (NSData *)buildRegisterResponsePayloadWithStatusCode:(uint16_t)statusCode
                                                   uid:(uint64_t)uid
                                              password:(NSString *)password
                                        registrationID:(NSString *)registrationID
                                              deviceID:(NSString *)deviceID {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt16(body, statusCode);
    if (statusCode == 0) {
        JCoreAppendUInt64(body, uid);
        JCoreAppendString(body, password ?: @"");
        JCoreAppendString(body, registrationID ?: @"");
        JCoreAppendString(body, deviceID ?: @"");
    }
    return body;
}

+ (NSData *)buildRegisterResponsePayloadWithUid:(uint64_t)uid
                                       password:(NSString *)password
                                 registrationID:(NSString *)registrationID {
    return [self buildRegisterResponsePayloadWithStatusCode:0
                                                        uid:uid
                                                   password:password
                                             registrationID:registrationID
                                                   deviceID:[JCOREConfiguration shared].deviceID];
}

+ (JCORERegisterResponse *)parseRegisterResponse:(JCOREWireFrame *)frame {
    if (frame == nil) {
        return nil;
    }
    JCORERegisterResponse *response = [[JCORERegisterResponse alloc] init];
    response.statusCode = frame.statusCode;

    NSData *body = frame.bodyData;
    if (body.length < 2) {
        return response;
    }

    NSUInteger offset = 0;
    uint16_t bodyStatus = 0;
    if (!JCoreReadUInt16(body, &offset, &bodyStatus)) {
        return response;
    }
    response.statusCode = bodyStatus;
    if (bodyStatus != 0 || body.length < offset + 8) {
        return response;
    }

    uint64_t uid = 0;
    if (!JCoreReadUInt64(body, &offset, &uid)) {
        return response;
    }
    response.uid = uid;
    response.password = JCoreReadString(body, &offset);
    response.registrationID = JCoreReadString(body, &offset);
    if (offset + 2 <= body.length) {
        response.deviceID = JCoreReadString(body, &offset);
    }
    return response;
}

#pragma mark - Login (cmd = 1, ver = 27)

+ (NSData *)buildLoginPayloadWithPassword:(NSString *)password
                                   appKey:(NSString *)appKey {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSString *passwordMd5 = [JCORECrypto md5Hex:password ?: @""];
    NSString *modelName = [config modelNameString];
    NSString *appVersion = [config appVersionString];
    NSString *systemVersion = [config osVersionString];
    NSString *countryCode = [[NSLocale currentLocale] objectForKey:NSLocaleCountryCode] ?: @"CN";

    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt32(body, 105);               // Fixed magic 0x00000069
    JCoreAppendString(body, passwordMd5);       // MD5(password)
    JCoreAppendUInt32(body, 0x00050200);        // Core SDK version 5.2.0
    JCoreAppendString(body, appKey ?: @"");     // appKey
    JCoreAppendUInt8(body, 1);                  // platform = 1 (iOS)
    JCoreAppendUInt32(body, 0);                 // idc
    JCoreAppendUInt8(body, config.apsType);     // apsType (1=prod, 0=dev)
    JCoreAppendUInt8(body, config.buildType);   // buildType (0=debug, 1=release)
    JCoreAppendString(body, [NSString stringWithFormat:@"$$%@$$%@$$ ", modelName, appVersion]);
    JCoreAppendString(body, @"5.2.0|6.0.0");    // versions
    JCoreAppendUInt16(body, 0);                 // connectInfo
    JCoreAppendString(body, @"|||1$$0$$0$$0");  // extInfo
    JCoreAppendString(body, [NSString stringWithFormat:@"%@;;%@", systemVersion, countryCode]);
    return body;
}

+ (NSData *)buildLoginResponsePayloadWithStatusCode:(uint16_t)statusCode
                                          sessionId:(uint32_t)sessionId
                                      serverVersion:(uint16_t)serverVersion
                                       errorMessage:(NSString *)errorMessage
                                    serverTimestamp:(uint32_t)serverTimestamp
                                                idc:(int8_t)idc {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt16(body, statusCode);
    if (statusCode == 0) {
        JCoreAppendUInt32(body, sessionId);
        JCoreAppendUInt16(body, serverVersion);
        JCoreAppendString(body, errorMessage ?: @"");
        JCoreAppendUInt32(body, serverTimestamp);
        JCoreAppendUInt8(body, (uint8_t)idc);
    } else {
        JCoreAppendString(body, errorMessage ?: @"");
    }
    return body;
}

+ (NSData *)buildLoginResponsePayloadWithSessionId:(uint32_t)sessionId
                                     serverVersion:(uint16_t)serverVersion
                                   serverTimestamp:(uint32_t)serverTimestamp {
    return [self buildLoginResponsePayloadWithStatusCode:0
                                               sessionId:sessionId
                                           serverVersion:serverVersion
                                            errorMessage:@""
                                         serverTimestamp:serverTimestamp
                                                     idc:0];
}

+ (JCORELoginResponse *)parseLoginResponse:(JCOREWireFrame *)frame {
    if (frame == nil) {
        return nil;
    }
    JCORELoginResponse *response = [[JCORELoginResponse alloc] init];
    response.statusCode = frame.statusCode;

    NSData *body = frame.bodyData;
    if (body.length < 2) {
        return response;
    }

    NSUInteger offset = 0;
    uint16_t bodyStatus = 0;
    if (!JCoreReadUInt16(body, &offset, &bodyStatus)) {
        return response;
    }
    response.statusCode = bodyStatus;
    if (bodyStatus != 0) {
        if (offset + 2 <= body.length) {
            response.errorMessage = JCoreReadString(body, &offset);
        }
        return response;
    }
    if (body.length < offset + 6) {
        return response;
    }

    uint32_t sid = 0;
    uint16_t serverVer = 0;
    uint32_t serverTime = 0;
    JCoreReadUInt32(body, &offset, &sid);
    JCoreReadUInt16(body, &offset, &serverVer);
    NSString *errMsg = JCoreReadString(body, &offset);
    JCoreReadUInt32(body, &offset, &serverTime);
    uint8_t idcByte = 0;
    if (JCoreReadUInt8(body, &offset, &idcByte)) {
        response.idc = (int8_t)idcByte;
    }
    response.sessionId = sid;
    response.serverVersion = serverVer;
    response.errorMessage = errMsg;
    response.serverTimestamp = serverTime;
    return response;
}

#pragma mark - Heartbeat (cmd = 2, ver = 8)

+ (NSData *)buildHeartbeatPayloadWithPushStopped:(BOOL)pushStopped {
    NSMutableData *body = [NSMutableData dataWithCapacity:8];
    uint32_t sdkFlag = 1;
    uint8_t appState = pushStopped ? 0 : 1;
    uint16_t beatInterval = 290;
    uint8_t beatType = 1;
    JCoreAppendUInt32(body, sdkFlag);
    JCoreAppendUInt8(body, appState);
    JCoreAppendUInt16(body, beatInterval);
    JCoreAppendUInt8(body, beatType);
    return body;
}

#pragma mark - PushMessage (cmd = 3, ver = 4) & MessageReceipt (cmd = 4, ver = 3)

+ (NSData *)buildPushMessagePayloadWithMessageType:(uint8_t)messageType
                                         messageId:(uint64_t)messageId
                                           content:(NSString *)content {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt8(body, messageType);
    JCoreAppendUInt64(body, messageId);
    JCoreAppendString(body, content ?: @"");
    return body;
}

+ (JCOREPushEnvelope *)parsePushEnvelope:(JCOREWireFrame *)frame {
    if (frame.bodyData.length < 11) {
        return nil;
    }
    NSUInteger offset = 0;
    uint8_t msgType = 0;
    uint64_t msgId = 0;
    if (!JCoreReadUInt8(frame.bodyData, &offset, &msgType) ||
        !JCoreReadUInt64(frame.bodyData, &offset, &msgId)) {
        return nil;
    }
    NSString *content = JCoreReadString(frame.bodyData, &offset);
    JCOREPushEnvelope *envelope = [[JCOREPushEnvelope alloc] init];
    envelope.messageType = msgType;
    envelope.messageId = msgId;
    envelope.content = content;
    if (content.length > 0) {
        NSArray<NSString *> *segments = [content componentsSeparatedByString:@"\n"];
        NSString *jsonCandidate = segments.lastObject ?: content;
        NSData *jsonData = [jsonCandidate dataUsingEncoding:NSUTF8StringEncoding];
        id parsed = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:nil];
        if ([parsed isKindOfClass:[NSDictionary class]]) {
            envelope.jsonPayload = parsed;
        }
    }
    return envelope;
}

+ (NSData *)buildMessageAckPayloadWithStatusCode:(uint16_t)statusCode
                                     messageType:(uint8_t)messageType
                                       messageId:(uint64_t)messageId
                                         msgData:(NSString *)msgData {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt16(body, statusCode);
    JCoreAppendUInt8(body, messageType);
    JCoreAppendUInt64(body, messageId);
    if (msgData != nil) {
        JCoreAppendString(body, msgData);
    }
    return body;
}

+ (NSData *)buildMessageAckPayloadWithMessageType:(uint8_t)messageType
                                        messageId:(uint64_t)messageId {
    return [self buildMessageAckPayloadWithStatusCode:0
                                          messageType:messageType
                                            messageId:messageId
                                              msgData:nil];
}

+ (BOOL)parseMessageAckFrame:(JCOREWireFrame *)frame
              outMessageType:(uint8_t *)outMessageType
                outMessageId:(uint64_t *)outMessageId {
    if (frame.bodyData.length < 11) {
        return NO;
    }
    NSUInteger offset = 0;
    uint16_t statusCode = 0;
    uint8_t msgType = 0;
    uint64_t msgId = 0;
    if (!JCoreReadUInt16(frame.bodyData, &offset, &statusCode) ||
        !JCoreReadUInt8(frame.bodyData, &offset, &msgType) ||
        !JCoreReadUInt64(frame.bodyData, &offset, &msgId)) {
        return NO;
    }
    if (outMessageType != NULL) {
        *outMessageType = msgType;
    }
    if (outMessageId != NULL) {
        *outMessageId = msgId;
    }
    return YES;
}

#pragma mark - DeviceToken (cmd = 13, ver = 3) & VoIP Token (cmd = 33, ver = 2)

+ (NSData *)buildDeviceTokenPayloadWithTokenHex:(NSString *)tokenHex {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSMutableData *body = [NSMutableData data];
    JCoreAppendString(body, tokenHex ?: @"");
    JCoreAppendUInt8(body, config.buildType);
    JCoreAppendUInt8(body, config.apsType);
    JCoreAppendUInt8(body, 1);
    return body;
}

+ (NSString *)parseDeviceTokenFromFrame:(JCOREWireFrame *)frame {
    if (frame.bodyData.length < 2) {
        return nil;
    }
    NSUInteger offset = 0;
    return JCoreReadString(frame.bodyData, &offset);
}

+ (NSData *)buildVoipTokenPayloadWithTokenHex:(NSString *)tokenHex {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSMutableData *body = [NSMutableData data];
    JCoreAppendString(body, tokenHex ?: @"");
    JCoreAppendUInt8(body, 1);
    JCoreAppendUInt8(body, config.buildType);
    JCoreAppendUInt8(body, config.apsType);
    return body;
}

+ (NSString *)parseVoipTokenFromFrame:(JCOREWireFrame *)frame {
    if (frame.bodyData.length < 2) {
        return nil;
    }
    NSUInteger offset = 0;
    return JCoreReadString(frame.bodyData, &offset);
}

#pragma mark - LiveActivity Token (cmd = 39, ver = 1)

+ (NSData *)buildLiveActivityPayloadWithTokenHex:(NSString *)tokenHex
                           aliasOrAttributesType:(NSString *)aliasOrAttributesType
                                     isPushToken:(BOOL)isPushToken {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSMutableData *body = [NSMutableData data];
    JCoreAppendString(body, tokenHex ?: @"");
    JCoreAppendString(body, aliasOrAttributesType ?: @"");
    JCoreAppendUInt8(body, isPushToken ? 1 : 0);
    JCoreAppendUInt8(body, config.buildType);
    JCoreAppendUInt8(body, config.apsType);
    JCoreAppendUInt8(body, 1);
    return body;
}

+ (NSData *)buildLiveActivityResponsePayloadWithStatusCode:(uint16_t)statusCode
                                                     alias:(NSString *)alias {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt16(body, statusCode);
    JCoreAppendString(body, alias ?: @"");
    return body;
}

+ (NSInteger)mapLiveActivityStatusCode:(uint16_t)statusCode {
    switch (statusCode) {
        case 0:
            return 0;
        case 3900:
            return 6039;
        case 3901:
            return 6040;
        case 3902:
            return 6042;
        case 3950:
            return 6041;
        default:
            return (NSInteger)statusCode;
    }
}

+ (JCORELiveActivityResponse *)parseLiveActivityResponse:(JCOREWireFrame *)frame {
    if (frame == nil) {
        return nil;
    }
    JCORELiveActivityResponse *resp = [[JCORELiveActivityResponse alloc] init];
    resp.statusCode = frame.statusCode;
    if (frame.bodyData.length >= 2) {
        NSUInteger offset = 0;
        uint16_t bodyStatus = 0;
        if (JCoreReadUInt16(frame.bodyData, &offset, &bodyStatus)) {
            resp.statusCode = bodyStatus;
        }
        if (offset + 2 <= frame.bodyData.length) {
            resp.alias = JCoreReadString(frame.bodyData, &offset);
        }
    }
    resp.mappedErrorCode = [self mapLiveActivityStatusCode:resp.statusCode];
    return resp;
}

#pragma mark - CtrlCommand (cmd = 25, ver = 1)

+ (NSData *)buildCtrlCommandPayloadWithRequestId:(uint64_t)requestId
                                     jsonPayload:(NSDictionary *)jsonPayload {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt64(body, requestId);
    NSData *jsonData = [NSJSONSerialization isValidJSONObject:jsonPayload]
        ? [NSJSONSerialization dataWithJSONObject:jsonPayload options:0 error:nil]
        : nil;
    NSString *jsonString = jsonData != nil
        ? [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding]
        : @"{}";
    JCoreAppendString(body, jsonString);
    return body;
}

+ (JCORECtrlEnvelope *)parseCtrlEnvelope:(JCOREWireFrame *)frame {
    if (frame.bodyData.length < 10) {
        return nil;
    }
    NSUInteger offset = 0;
    uint64_t reqId = 0;
    if (!JCoreReadUInt64(frame.bodyData, &offset, &reqId)) {
        return nil;
    }
    NSString *rawJson = JCoreReadString(frame.bodyData, &offset);
    JCORECtrlEnvelope *env = [[JCORECtrlEnvelope alloc] init];
    env.requestId = reqId;
    env.rawJson = rawJson;
    if (rawJson.length > 0) {
        NSData *data = [rawJson dataUsingEncoding:NSUTF8StringEncoding];
        id parsed = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if ([parsed isKindOfClass:[NSDictionary class]]) {
            env.jsonPayload = parsed;
        }
    }
    return env;
}

+ (NSData *)buildCtrlAckPayloadWithStatusCode:(uint16_t)statusCode
                                    requestId:(uint64_t)requestId {
    NSMutableData *body = [NSMutableData dataWithCapacity:10];
    JCoreAppendUInt16(body, statusCode);
    JCoreAppendUInt64(body, requestId);
    return body;
}

#pragma mark - User Property / MobileNumber / SDKVersion (cmd = 26, ver = 0)

+ (NSData *)buildMobileNumberPayload:(NSString *)mobileNumber {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt8(body, 7); // property type 7 = mobile number
    JCoreAppendUInt8(body, 1); // version = 1
    JCoreAppendString(body, mobileNumber ?: @"");
    return body;
}

+ (NSString *)parseMobileNumberFromFrame:(JCOREWireFrame *)frame {
    if (frame.bodyData.length < 4) {
        return nil;
    }
    NSUInteger offset = 2;
    return JCoreReadString(frame.bodyData, &offset);
}

+ (NSData *)buildSdkVersionPropertyPayload:(NSString *)sdkVersion {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt8(body, 1); // property type 1 = JPush SDK version
    JCoreAppendUInt8(body, 1); // version = 1
    JCoreAppendString(body, sdkVersion ?: @"6.0.0");
    return body;
}

#pragma mark - Tags (cmd = 28, ver = 1), Alias (cmd = 29, ver = 1), User Properties (cmd = 38, ver = 1)

+ (NSData *)buildTagOperationPayloadWithOperator:(NSString *)opString
                                            tags:(NSArray<NSString *> *)tags
                                        currPage:(NSInteger)currPage {
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    dict[@"platform"] = @"i";
    dict[@"op"] = opString ?: @"set";
    if (tags != nil && ![opString isEqualToString:@"clean"] && ![opString isEqualToString:@"get"]) {
        dict[@"tags"] = tags;
    }
    if ([opString isEqualToString:@"get"] || currPage > 0) {
        dict[@"curr"] = @(currPage > 0 ? currPage : 1);
    }
    return [self buildJsonCommandPayload:dict];
}

+ (NSData *)buildAliasOperationPayloadWithOperator:(NSString *)opString
                                             alias:(NSString *)alias {
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    dict[@"platform"] = @"i";
    dict[@"op"] = opString ?: @"set";
    if (alias.length > 0 && ![opString isEqualToString:@"del"] && ![opString isEqualToString:@"get"]) {
        dict[@"alias"] = alias;
    }
    return [self buildJsonCommandPayload:dict];
}

+ (NSData *)buildUserPropertiesPayloadWithOperator:(NSString *)opString
                                        properties:(NSDictionary *)properties {
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    dict[@"op"] = opString ?: @"set";
    dict[@"data"] = properties ?: @{};
    return [self buildJsonCommandPayload:dict];
}

+ (NSData *)buildUserPropertiesResponsePayloadWithStatusCode:(uint16_t)statusCode
                                                 jsonPayload:(NSDictionary *)jsonPayload {
    NSMutableData *body = [NSMutableData data];
    JCoreAppendUInt16(body, statusCode);
    if (jsonPayload != nil && [NSJSONSerialization isValidJSONObject:jsonPayload]) {
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonPayload options:0 error:nil];
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding] ?: @"{}";
        JCoreAppendString(body, jsonString);
    } else {
        JCoreAppendString(body, @"{}");
    }
    return body;
}

+ (NSData *)buildJsonCommandPayload:(NSDictionary *)payload {
    NSMutableData *body = [NSMutableData data];
    NSData *jsonData = [NSJSONSerialization isValidJSONObject:payload]
        ? [NSJSONSerialization dataWithJSONObject:payload options:0 error:nil]
        : nil;
    NSString *jsonString = jsonData != nil
        ? [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding]
        : @"{}";
    JCoreAppendString(body, jsonString);
    return body;
}

+ (NSDictionary *)parseJsonBodyFromFrame:(JCOREWireFrame *)frame {
    if (frame.bodyData.length == 0) {
        return nil;
    }
    NSData *body = frame.bodyData;

    // 1. Try reading length-prefixed string at offset 0 (Tags/Alias request & response, Properties request)
    NSUInteger offset = 0;
    NSString *jsonString = JCoreReadString(body, &offset);
    if (jsonString.length > 0) {
        NSData *data = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
        id obj = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if ([obj isKindOfClass:[NSDictionary class]]) {
            return obj;
        }
    }

    // 2. Try reading length-prefixed string at offset 2 (after 2-byte statusCode in Properties response)
    if (body.length > 4) {
        NSUInteger offsetAfterStatus = 2;
        NSString *jsonAfterStatus = JCoreReadString(body, &offsetAfterStatus);
        if (jsonAfterStatus.length > 0) {
            NSData *data = [jsonAfterStatus dataUsingEncoding:NSUTF8StringEncoding];
            id obj = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
            if ([obj isKindOfClass:[NSDictionary class]]) {
                return obj;
            }
        }
    }

    // 3. Fallback: raw UTF-8 JSON body
    id rawObj = [NSJSONSerialization JSONObjectWithData:body options:0 error:nil];
    return [rawObj isKindOfClass:[NSDictionary class]] ? rawObj : nil;
}

+ (NSData *)buildPushControlPayloadWithStopped:(BOOL)stopped {
    NSDictionary *dict = @{
        @"type": @"notif_state",
        @"enabled": @(!stopped),
        @"itime": @((uint64_t)[[NSDate date] timeIntervalSince1970]),
    };
    return [self buildJsonCommandPayload:dict];
}

@end

#pragma mark - HTTP Reporter (Part 4.4)

@implementation JCOREHttpReporter

+ (instancetype)shared {
    static JCOREHttpReporter *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[JCOREHttpReporter alloc] init];
    });
    return instance;
}

+ (NSDictionary *)buildReportEnvelopeWithContentItems:(NSArray<NSDictionary *> *)items {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    return @{
        @"platform": @"i",
        @"uid": @(config.uid),
        @"app_key": config.appKey ?: @"",
        @"sdk_ver": @"5.2.0",
        @"core_sdk_ver": @"5.2.0",
        @"os_ver": [config osVersionString],
        @"model": [config modelNameString],
        @"bundle_id": [config bundleIdentifierString],
        @"ver": [config appVersionString],
        @"content": items ?: @[],
    };
}

+ (NSURLRequest *)buildHttpReportRequestWithURL:(NSURL *)url
                                   contentItems:(NSArray<NSDictionary *> *)items
                                            uid:(uint64_t)uid
                                       password:(NSString *)password
                                         appKey:(NSString *)appKey
                                      outAesKey:(NSString **)outAesKey {
    if (url == nil) {
        return nil;
    }
    NSMutableDictionary *envelope = [[self buildReportEnvelopeWithContentItems:items] mutableCopy];
    envelope[@"uid"] = @(uid);
    envelope[@"app_key"] = appKey ?: @"";

    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:envelope options:0 error:nil];
    if (jsonData.length == 0) {
        return nil;
    }
    NSData *compressed = [JCORECrypto gzipCompress:jsonData];
    if (compressed.length == 0) {
        return nil;
    }

    uint32_t seed = (arc4random() % 10000000U) + 1U;
    NSString *aesKey = [JCORECrypto generateSeedKey:seed];
    if (outAesKey != NULL) {
        *outAesKey = aesKey;
    }

    NSData *encryptedBody = [JCORECrypto aes256CBCEncryptWithFixedHttpIV:compressed key:aesKey];
    if (encryptedBody.length == 0) {
        return nil;
    }

    NSString *bodySha1 = [JCORECrypto sha1Hex:encryptedBody];
    NSString *pwdMd5 = [JCORECrypto md5Hex:password ?: @""];
    NSString *tokenInput = [NSString stringWithFormat:@"%llu%@%@", uid, pwdMd5, bodySha1];
    NSString *authToken = [JCORECrypto sha1HexForString:tokenInput];
    NSString *rawAuth = [NSString stringWithFormat:@"%llu:%@:%@", uid, authToken, aesKey];
    NSString *base64Auth = [[rawAuth dataUsingEncoding:NSUTF8StringEncoding] base64EncodedStringWithOptions:0];

    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                           cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                                       timeoutInterval:10.0];
    request.HTTPMethod = @"POST";
    request.HTTPBody = encryptedBody;
    [request setValue:[NSString stringWithFormat:@"Basic %@", base64Auth] forHTTPHeaderField:@"Authorization"];
    [request setValue:(appKey ?: @"") forHTTPHeaderField:@"X-App-Key"];
    [request setValue:@"application/octet-stream" forHTTPHeaderField:@"Content-Type"];
    [request setValue:@"application/jason" forHTTPHeaderField:@"Accept"];
    return request;
}

+ (NSDictionary *)verifyAndDecryptHttpReportBody:(NSData *)bodyData
                             authorizationHeader:(NSString *)authHeader
                                     expectedUid:(uint64_t)expectedUid
                                        password:(NSString *)password {
    if (bodyData.length == 0 || ![authHeader hasPrefix:@"Basic "]) {
        return nil;
    }
    NSString *b64 = [authHeader substringFromIndex:6];
    NSData *decodedAuthData = [[NSData alloc] initWithBase64EncodedString:b64 options:0];
    NSString *decodedAuth = [[NSString alloc] initWithData:decodedAuthData encoding:NSUTF8StringEncoding];
    NSArray<NSString *> *parts = [decodedAuth componentsSeparatedByString:@":"];
    if (parts.count != 3) {
        return nil;
    }
    uint64_t headerUid = strtoull(parts[0].UTF8String, NULL, 10);
    if (expectedUid != 0 && headerUid != expectedUid) {
        return nil;
    }
    NSString *authToken = parts[1];
    NSString *aesKey = parts[2];

    NSString *bodySha1 = [JCORECrypto sha1Hex:bodyData];
    NSString *pwdMd5 = [JCORECrypto md5Hex:password ?: @""];
    NSString *expectedToken = [JCORECrypto sha1HexForString:[NSString stringWithFormat:@"%llu%@%@", headerUid, pwdMd5, bodySha1]];
    if (![authToken isEqualToString:expectedToken]) {
        return nil;
    }

    NSData *decryptedCompressed = [JCORECrypto aes256CBCDecryptWithFixedHttpIV:bodyData key:aesKey];
    if (decryptedCompressed.length == 0) {
        return nil;
    }
    NSData *jsonData = [JCORECrypto gzipDecompress:decryptedCompressed];
    if (jsonData.length == 0) {
        return nil;
    }
    id obj = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:nil];
    return [obj isKindOfClass:[NSDictionary class]] ? obj : nil;
}

+ (NSURLRequest *)buildBadgeReportRequestWithBaseURL:(NSURL *)baseURL
                                          badgeValue:(NSInteger)badgeValue
                                                 uid:(uint64_t)uid
                                              appKey:(NSString *)appKey
                                         outAesKey16:(NSString **)outAesKey16 {
    NSString *baseStr = baseURL.absoluteString.length > 0
        ? baseURL.absoluteString
        : @"https://ali-stats.jpush.cn/v3/push/sdk/badge";
    NSString *urlStr = [NSString stringWithFormat:@"%@%@platform=i&uid=%llu&value=%ld",
                        baseStr,
                        ([baseStr containsString:@"?"] ? @"&" : @"?"),
                        uid,
                        (long)badgeValue];
    NSURL *fullURL = [NSURL URLWithString:urlStr];
    if (fullURL == nil) {
        return nil;
    }

    uint32_t seed = (arc4random() % 10000000U) + 1U;
    NSString *seedKey32 = [JCORECrypto generateSeedKey:seed];
    NSString *aesKey16 = [seedKey32 substringToIndex:16];
    if (outAesKey16 != NULL) {
        *outAesKey16 = aesKey16;
    }

    NSString *rsaEncryptedKey = [JCORECrypto rsaEncryptString:aesKey16 publicKeyBase64:kJCOREReportRsaPublicKey] ?: @"";
    NSString *rawAuth = [NSString stringWithFormat:@"%@:%@", appKey ?: @"", rsaEncryptedKey];
    NSString *authHeaderValue = [NSString stringWithFormat:@"Basic %@",
                                 [[rawAuth dataUsingEncoding:NSUTF8StringEncoding] base64EncodedStringWithOptions:0]];

    uint64_t timestampMs = (uint64_t)([[NSDate date] timeIntervalSince1970] * 1000.0);
    uint64_t timestampSec = timestampMs / 1000ULL;
    NSString *timestampStr = [NSString stringWithFormat:@"%llu", timestampMs];

    NSInteger clampedBadge = badgeValue < 0 ? 0 : badgeValue;
    NSDictionary *badgeDict = @{
        @"platform": @"i",
        @"uid": @(uid),
        @"app_key": appKey ?: @"",
        @"badge": @(clampedBadge),
        @"content": @{@"badge": @(clampedBadge)},
        @"itime": @(timestampSec),
    };
    NSData *badgeJsonData = [NSJSONSerialization dataWithJSONObject:badgeDict options:0 error:nil];
    NSData *encryptedBadge = [JCORECrypto aes128CBCEncryptData:badgeJsonData
                                                         key16:aesKey16
                                                          iv16:@"0000000000000000"];
    NSString *bodyString = [encryptedBadge base64EncodedStringWithOptions:0] ?: @"";

    NSString *hmacKey = [JCORECrypto md5Hex:[NSString stringWithFormat:@"%@%@", appKey ?: @"", timestampStr]];
    NSString *signInput = [NSString stringWithFormat:@"%@%@1.0%@", authHeaderValue, timestampStr, bodyString];
    NSString *signHex = [JCORECrypto hmacSHA256HexWithKey:hmacKey content:signInput];

    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:fullURL
                                                           cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                                       timeoutInterval:10.0];
    request.HTTPMethod = @"POST";
    request.HTTPBody = [bodyString dataUsingEncoding:NSUTF8StringEncoding];
    [request setValue:authHeaderValue forHTTPHeaderField:@"Authorization"];
    [request setValue:@"1.0" forHTTPHeaderField:@"x-jg-version"];
    [request setValue:timestampStr forHTTPHeaderField:@"x-jg-timestamp"];
    [request setValue:signHex forHTTPHeaderField:@"x-jg-sign"];
    [request setValue:@"text/plain;charset=UTF-8" forHTTPHeaderField:@"Content-Type"];
    return request;
}

+ (NSDictionary *)verifyAndDecryptBadgeReportHeaders:(NSDictionary<NSString *, NSString *> *)headers
                                          bodyString:(NSString *)bodyString
                                              appKey:(NSString *)appKey
                                            aesKey16:(NSString *)aesKey16 {
    NSString *auth = headers[@"Authorization"] ?: headers[@"authorization"];
    NSString *version = headers[@"x-jg-version"] ?: headers[@"X-Jg-Version"];
    NSString *timestamp = headers[@"x-jg-timestamp"] ?: headers[@"X-Jg-Timestamp"];
    NSString *sign = headers[@"x-jg-sign"] ?: headers[@"X-Jg-Sign"];
    if (auth.length == 0 || ![version isEqualToString:@"1.0"] || timestamp.length == 0 || sign.length == 0) {
        return nil;
    }

    NSString *hmacKey = [JCORECrypto md5Hex:[NSString stringWithFormat:@"%@%@", appKey ?: @"", timestamp]];
    NSString *expectedSign = [JCORECrypto hmacSHA256HexWithKey:hmacKey
                                                       content:[NSString stringWithFormat:@"%@%@1.0%@", auth, timestamp, bodyString ?: @""]];
    if (![sign isEqualToString:expectedSign]) {
        return nil;
    }

    if (bodyString.length > 0 && aesKey16.length == 16) {
        NSData *cipherData = [[NSData alloc] initWithBase64EncodedString:bodyString options:0];
        NSData *plainData = [JCORECrypto aes128CBCDecryptData:cipherData
                                                        key16:aesKey16
                                                         iv16:@"0000000000000000"];
        if (plainData.length > 0) {
            id obj = [NSJSONSerialization JSONObjectWithData:plainData options:0 error:nil];
            if ([obj isKindOfClass:[NSDictionary class]]) {
                return obj;
            }
        }
    }
    return @{@"verified": @YES};
}

- (void)sendHttpReportItems:(NSArray<NSDictionary *> *)items
                 completion:(void (^)(BOOL, NSInteger))completion {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    if (config.uid == 0 || config.appKey.length == 0) {
        if (completion) {
            completion(NO, -1);
        }
        return;
    }
    NSString *urlStr = config.httpReportUrl;
    if (urlStr.length == 0) {
        NSString *firstAddr = config.httpsReportAddresses.firstObject ?: config.httpReportAddresses.firstObject;
        if (firstAddr.length > 0) {
            urlStr = [firstAddr hasPrefix:@"http"] ? firstAddr : [NSString stringWithFormat:@"https://%@/v3/report", firstAddr];
        } else {
            urlStr = @"https://stats.jpush.cn/v3/report";
        }
    }
    NSURL *url = [NSURL URLWithString:urlStr];
    NSURLRequest *req = [JCOREHttpReporter buildHttpReportRequestWithURL:url
                                                            contentItems:items
                                                                     uid:config.uid
                                                                password:config.password ?: @""
                                                                  appKey:config.appKey
                                                               outAesKey:NULL];
    if (req == nil) {
        if (completion) {
            completion(NO, -1);
        }
        return;
    }
    [[[NSURLSession sharedSession] dataTaskWithRequest:req
                                     completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        (void)data;
        NSInteger status = [(NSHTTPURLResponse *)response statusCode];
        BOOL ok = (error == nil && status == 200);
        if (completion) {
            completion(ok, status);
        }
    }] resume];
}

- (void)reportBadgeValue:(NSInteger)badgeValue
              completion:(void (^)(BOOL, NSInteger))completion {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    if (config.uid == 0 || config.appKey.length == 0) {
        if (completion) {
            completion(NO, -1);
        }
        return;
    }
    NSString *urlStr = config.httpReportUrl;
    if (urlStr.length == 0) {
        NSString *firstAddr = config.httpsReportAddresses.firstObject ?: config.httpReportAddresses.firstObject;
        if (firstAddr.length > 0) {
            urlStr = [firstAddr hasPrefix:@"http"] ? firstAddr : [NSString stringWithFormat:@"https://%@/v3/push/sdk/badge", firstAddr];
        } else {
            urlStr = @"https://ali-stats.jpush.cn/v3/push/sdk/badge";
        }
    }
    NSURL *baseUrl = [NSURL URLWithString:urlStr];
    NSURLRequest *req = [JCOREHttpReporter buildBadgeReportRequestWithBaseURL:baseUrl
                                                                   badgeValue:badgeValue
                                                                          uid:config.uid
                                                                       appKey:config.appKey ?: @""
                                                                  outAesKey16:NULL];
    if (req == nil) {
        if (completion) {
            completion(NO, -1);
        }
        return;
    }
    [[[NSURLSession sharedSession] dataTaskWithRequest:req
                                     completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        (void)data;
        NSInteger status = [(NSHTTPURLResponse *)response statusCode];
        BOOL ok = (error == nil && status == 200);
        if (completion) {
            completion(ok, status);
        }
    }] resume];
}

@end
