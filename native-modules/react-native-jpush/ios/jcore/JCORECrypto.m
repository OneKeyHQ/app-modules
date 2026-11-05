#import "JCORECrypto.h"
#import <CommonCrypto/CommonCryptor.h>
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonHMAC.h>
#import <Security/Security.h>
#import <zlib.h>

typedef struct {
    int mode;
    uint32_t sk[32];
} JCORESm4Context;

static const uint8_t kJCORESm4SBox[256] = {
    0xd6, 0x90, 0xe9, 0xfe, 0xcc, 0xe1, 0x3d, 0xb7, 0x16, 0xb6, 0x14, 0xc2, 0x28, 0xfb, 0x2c, 0x05,
    0x2b, 0x67, 0x9a, 0x76, 0x2a, 0xbe, 0x04, 0xc3, 0xaa, 0x44, 0x13, 0x26, 0x49, 0x86, 0x06, 0x99,
    0x9c, 0x42, 0x50, 0xf4, 0x91, 0xef, 0x98, 0x7a, 0x33, 0x54, 0x0b, 0x43, 0xed, 0xcf, 0xac, 0x62,
    0xe4, 0xb3, 0x1c, 0xa9, 0xc9, 0x08, 0xe8, 0x95, 0x80, 0xdf, 0x94, 0xfa, 0x75, 0x8f, 0x3f, 0xa6,
    0x47, 0x07, 0xa7, 0xfc, 0xf3, 0x73, 0x17, 0xba, 0x83, 0x59, 0x3c, 0x19, 0xe6, 0x85, 0x4f, 0xa8,
    0x68, 0x6b, 0x81, 0xb2, 0x71, 0x64, 0xda, 0x8b, 0xf8, 0xeb, 0x0f, 0x4b, 0x70, 0x56, 0x9d, 0x35,
    0x1e, 0x24, 0x0e, 0x5e, 0x63, 0x58, 0xd1, 0xa2, 0x25, 0x22, 0x7c, 0x3b, 0x01, 0x21, 0x78, 0x87,
    0xd4, 0x00, 0x46, 0x57, 0x9f, 0xd3, 0x27, 0x52, 0x4c, 0x36, 0x02, 0xe7, 0xa0, 0xc4, 0xc8, 0x9e,
    0xea, 0xbf, 0x8a, 0xd2, 0x40, 0xc7, 0x38, 0xb5, 0xa3, 0xf7, 0xf2, 0xce, 0xf9, 0x61, 0x15, 0xa1,
    0xe0, 0xae, 0x5d, 0xa4, 0x9b, 0x34, 0x1a, 0x55, 0xad, 0x93, 0x32, 0x30, 0xf5, 0x8c, 0xb1, 0xe3,
    0x1d, 0xf6, 0xe2, 0x2e, 0x82, 0x66, 0xca, 0x60, 0xc0, 0x29, 0x23, 0xab, 0x0d, 0x53, 0x4e, 0x6f,
    0xd5, 0xdb, 0x37, 0x45, 0xde, 0xfd, 0x8e, 0x2f, 0x03, 0xff, 0x6a, 0x72, 0x6d, 0x6c, 0x5b, 0x51,
    0x8d, 0x1b, 0xaf, 0x92, 0xbb, 0xdd, 0xbc, 0x7f, 0x11, 0xd9, 0x5c, 0x41, 0x1f, 0x10, 0x5a, 0xd8,
    0x0a, 0xc1, 0x31, 0x88, 0xa5, 0xcd, 0x7b, 0xbd, 0x2d, 0x74, 0xd0, 0x12, 0xb8, 0xe5, 0xb4, 0xb0,
    0x89, 0x69, 0x97, 0x4a, 0x0c, 0x96, 0x77, 0x7e, 0x65, 0xb9, 0xf1, 0x09, 0xc5, 0x6e, 0xc6, 0x84,
    0x18, 0xf0, 0x7d, 0xec, 0x3a, 0xdc, 0x4d, 0x20, 0x79, 0xee, 0x5f, 0x3e, 0xd7, 0xcb, 0x39, 0x48,
};

static inline uint32_t JCORESm4RotateLeft(uint32_t value, unsigned int shift) {
    return (value << shift) | (value >> (32U - shift));
}

static inline uint32_t JCORESm4SubstituteWord(uint32_t value) {
    return ((uint32_t)kJCORESm4SBox[(value >> 24) & 0xFF] << 24) |
           ((uint32_t)kJCORESm4SBox[(value >> 16) & 0xFF] << 16) |
           ((uint32_t)kJCORESm4SBox[(value >> 8) & 0xFF] << 8) |
           (uint32_t)kJCORESm4SBox[value & 0xFF];
}

static inline uint32_t JCORESm4ReadBE32(const uint8_t *bytes) {
    return ((uint32_t)bytes[0] << 24) |
           ((uint32_t)bytes[1] << 16) |
           ((uint32_t)bytes[2] << 8) |
           (uint32_t)bytes[3];
}

static inline void JCORESm4WriteBE32(uint8_t *bytes, uint32_t value) {
    bytes[0] = (uint8_t)(value >> 24);
    bytes[1] = (uint8_t)(value >> 16);
    bytes[2] = (uint8_t)(value >> 8);
    bytes[3] = (uint8_t)value;
}

static void JCORESm4InitKeySchedule(JCORESm4Context *ctx, const uint8_t key[16], BOOL decrypt) {
    static const uint32_t kFK[4] = { 0xa3b1bac6U, 0x56aa3350U, 0x677d9197U, 0xb27022dcU };
    uint32_t words[36];
    for (unsigned int i = 0; i < 4; i++) {
        words[i] = JCORESm4ReadBE32(key + i * 4) ^ kFK[i];
    }
    for (unsigned int i = 0; i < 32; i++) {
        uint32_t ck = 0;
        for (unsigned int j = 0; j < 4; j++) {
            ck = (ck << 8) | (((i * 4 + j) * 7) & 0xFF);
        }
        uint32_t t = JCORESm4SubstituteWord(words[i + 1] ^ words[i + 2] ^ words[i + 3] ^ ck);
        words[i + 4] = words[i] ^ t ^ JCORESm4RotateLeft(t, 13) ^ JCORESm4RotateLeft(t, 23);
        ctx->sk[decrypt ? (31 - i) : i] = words[i + 4];
    }
    ctx->mode = decrypt ? 0 : 1;
}

static void JCORESm4ProcessBlock(const uint32_t sk[32], const uint8_t input[16], uint8_t output[16]) {
    uint32_t words[36];
    for (unsigned int i = 0; i < 4; i++) {
        words[i] = JCORESm4ReadBE32(input + i * 4);
    }
    for (unsigned int i = 0; i < 32; i++) {
        uint32_t t = JCORESm4SubstituteWord(words[i + 1] ^ words[i + 2] ^ words[i + 3] ^ sk[i]);
        words[i + 4] = words[i] ^ t ^ JCORESm4RotateLeft(t, 2) ^ JCORESm4RotateLeft(t, 10) ^
                       JCORESm4RotateLeft(t, 18) ^ JCORESm4RotateLeft(t, 24);
    }
    for (unsigned int i = 0; i < 4; i++) {
        JCORESm4WriteBE32(output + i * 4, words[35 - i]);
    }
}

static BOOL JCORESm4DecodeHexKey16(NSString *hexKey, uint8_t outKey[16]) {
    if (hexKey.length != 32) {
        return NO;
    }
    const char *cstr = hexKey.UTF8String;
    if (cstr == NULL) {
        return NO;
    }
    for (int i = 0; i < 16; i++) {
        char hi = cstr[i * 2];
        char lo = cstr[i * 2 + 1];
        uint8_t hiVal = 0;
        uint8_t loVal = 0;
        if (hi >= '0' && hi <= '9') hiVal = (uint8_t)(hi - '0');
        else if (hi >= 'a' && hi <= 'f') hiVal = (uint8_t)(10 + hi - 'a');
        else if (hi >= 'A' && hi <= 'F') hiVal = (uint8_t)(10 + hi - 'A');
        else return NO;

        if (lo >= '0' && lo <= '9') loVal = (uint8_t)(lo - '0');
        else if (lo >= 'a' && lo <= 'f') loVal = (uint8_t)(10 + lo - 'a');
        else if (lo >= 'A' && lo <= 'F') loVal = (uint8_t)(10 + lo - 'A');
        else return NO;

        outKey[i] = (uint8_t)((hiVal << 4) | loVal);
    }
    return YES;
}

static NSData *JCORESm4StripPkcs7Padding(NSData *decrypted) {
    if (decrypted.length == 0 || (decrypted.length % 16) != 0) {
        return nil;
    }
    const uint8_t *bytes = decrypted.bytes;
    NSUInteger length = decrypted.length;
    uint8_t pad = bytes[length - 1];
    if (pad == 0 || pad > 16 || pad > length) {
        return decrypted;
    }
    for (NSUInteger i = 1; i < pad; i++) {
        if (bytes[length - 1 - i] != pad) {
            return decrypted;
        }
    }
    return [NSData dataWithBytes:bytes length:length - pad];
}

@implementation JCORECrypto

+ (NSString *)generateSeedKey:(uint64_t)seed {
    static const uint32_t kMultipliers[10] = { 8, 5, 23, 3, 13, 17, 7, 31, 29, 37 };
    static const uint32_t kModuli[10] = { 74, 88, 15, 73, 96, 49, 68, 39, 41, 91 };
    NSUInteger index = (NSUInteger)(seed % 10ULL);
    uint64_t transformed = seed * kMultipliers[index] + (seed % kModuli[index]);
    NSString *source = [NSString stringWithFormat:@"JCKP%lld", (long long)transformed];
    return [self md5Hex:source];
}

+ (NSString *)seedKeyForSeed:(uint64_t)seed {
    return [self generateSeedKey:seed];
}

+ (NSString *)httpSeedKeyWithOutSeed:(uint32_t *)outSeed {
    uint32_t randomSeed = arc4random();
    if (randomSeed == 0) {
        randomSeed = 1;
    }
    if (outSeed != NULL) {
        *outSeed = randomSeed;
    }
    return [self generateSeedKey:randomSeed];
}

+ (NSData *)cryptData:(NSData *)data
                  key:(NSString *)key
                   iv:(const void *)iv
              keySize:(size_t)keySize
            operation:(CCOperation)operation
              options:(CCOptions)options {
    if (data.length == 0 || key.length == 0) {
        return nil;
    }
    char keyBuffer[kCCKeySizeAES256 + 1];
    memset(keyBuffer, 0, sizeof(keyBuffer));
    [key getCString:keyBuffer maxLength:sizeof(keyBuffer) encoding:NSUTF8StringEncoding];

    size_t actualKeySize = (keySize == kCCKeySizeAES128 || keySize == kCCKeySizeAES256)
        ? keySize
        : (key.length >= kCCKeySizeAES256 ? kCCKeySizeAES256 : kCCKeySizeAES128);

    size_t bufferSize = data.length + kCCBlockSizeAES128;
    void *buffer = malloc(bufferSize);
    if (buffer == NULL) {
        return nil;
    }
    size_t bytesProcessed = 0;
    CCCryptorStatus status = CCCrypt(
        operation,
        kCCAlgorithmAES,
        options,
        keyBuffer,
        actualKeySize,
        iv,
        data.bytes,
        data.length,
        buffer,
        bufferSize,
        &bytesProcessed
    );
    if (status == kCCSuccess) {
        return [NSData dataWithBytesNoCopy:buffer length:bytesProcessed freeWhenDone:YES];
    }
    free(buffer);
    return nil;
}

+ (NSData *)aesCbcEncrypt:(NSData *)data
                      key:(NSString *)key
                       iv:(const void *)iv
                  keySize:(size_t)keySize {
    return [self cryptData:data
                       key:key
                        iv:iv
                   keySize:keySize
                 operation:kCCEncrypt
                   options:kCCOptionPKCS7Padding];
}

+ (NSData *)aesCbcDecrypt:(NSData *)data
                      key:(NSString *)key
                       iv:(const void *)iv
                  keySize:(size_t)keySize {
    return [self cryptData:data
                       key:key
                        iv:iv
                   keySize:keySize
                 operation:kCCDecrypt
                   options:kCCOptionPKCS7Padding];
}

+ (NSData *)aesEcbEncrypt:(NSData *)data
                      key:(NSString *)key
                  keySize:(size_t)keySize {
    return [self cryptData:data
                       key:key
                        iv:NULL
                   keySize:keySize
                 operation:kCCEncrypt
                   options:(kCCOptionPKCS7Padding | kCCOptionECBMode)];
}

+ (NSData *)aesEcbDecrypt:(NSData *)data
                      key:(NSString *)key
                  keySize:(size_t)keySize {
    return [self cryptData:data
                       key:key
                        iv:NULL
                   keySize:keySize
                 operation:kCCDecrypt
                   options:(kCCOptionPKCS7Padding | kCCOptionECBMode)];
}

+ (NSData *)fixedHttpReportIvData {
    static NSData *ivData = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        ivData = [[NSData alloc] initWithBase64EncodedString:@"aW9wMjAzMDQwNTA2YVBrIQ==" options:0];
    });
    return ivData;
}

+ (NSData *)aes256CBCEncryptWithFixedHttpIV:(NSData *)data
                                        key:(NSString *)key {
    NSData *ivData = [self fixedHttpReportIvData];
    if (ivData.length < 16) {
        return nil;
    }
    return [self aesCbcEncrypt:data key:key iv:ivData.bytes keySize:kCCKeySizeAES256];
}

+ (NSData *)aes256CBCDecryptWithFixedHttpIV:(NSData *)data
                                        key:(NSString *)key {
    NSData *ivData = [self fixedHttpReportIvData];
    if (ivData.length < 16) {
        return nil;
    }
    return [self aesCbcDecrypt:data key:key iv:ivData.bytes keySize:kCCKeySizeAES256];
}

+ (NSData *)aes128CBCEncryptData:(NSData *)data
                           key16:(NSString *)key16
                            iv16:(NSString *)iv16 {
    if (key16.length < 16 || iv16.length < 16) {
        return nil;
    }
    return [self aesCbcEncrypt:data key:key16 iv:iv16.UTF8String keySize:kCCKeySizeAES128];
}

+ (NSData *)aes128CBCDecryptData:(NSData *)data
                           key16:(NSString *)key16
                            iv16:(NSString *)iv16 {
    if (key16.length < 16 || iv16.length < 16) {
        return nil;
    }
    return [self aesCbcDecrypt:data key:key16 iv:iv16.UTF8String keySize:kCCKeySizeAES128];
}

+ (NSData *)sm4CBCEncryptData:(NSData *)data
                      withKey:(NSString *)hexKey
                           iv:(const char *)iv {
    if (data == nil || iv == NULL) {
        return nil;
    }
    uint8_t rawKey[16];
    if (!JCORESm4DecodeHexKey16(hexKey, rawKey)) {
        return nil;
    }
    uint8_t padding = (uint8_t)(16 - (data.length % 16));
    NSMutableData *padded = [NSMutableData dataWithLength:data.length + padding];
    memset(padded.mutableBytes, padding, padded.length);
    if (data.length > 0) {
        memcpy(padded.mutableBytes, data.bytes, data.length);
    }

    JCORESm4Context ctx;
    JCORESm4InitKeySchedule(&ctx, rawKey, NO);

    uint8_t currentIv[16];
    memcpy(currentIv, iv, 16);

    NSMutableData *output = [NSMutableData dataWithLength:padded.length];
    const uint8_t *inBytes = padded.bytes;
    uint8_t *outBytes = output.mutableBytes;
    for (NSUInteger offset = 0; offset < padded.length; offset += 16) {
        uint8_t xored[16];
        for (int i = 0; i < 16; i++) {
            xored[i] = inBytes[offset + i] ^ currentIv[i];
        }
        JCORESm4ProcessBlock(ctx.sk, xored, outBytes + offset);
        memcpy(currentIv, outBytes + offset, 16);
    }
    return output;
}

+ (NSData *)sm4CBCDecryptData:(NSData *)data
                      withKey:(NSString *)hexKey
                           iv:(const char *)iv {
    if (data.length == 0 || (data.length % 16) != 0 || iv == NULL) {
        return nil;
    }
    uint8_t rawKey[16];
    if (!JCORESm4DecodeHexKey16(hexKey, rawKey)) {
        return nil;
    }
    JCORESm4Context ctx;
    JCORESm4InitKeySchedule(&ctx, rawKey, YES);

    uint8_t currentIv[16];
    memcpy(currentIv, iv, 16);

    NSMutableData *output = [NSMutableData dataWithLength:data.length];
    const uint8_t *inBytes = data.bytes;
    uint8_t *outBytes = output.mutableBytes;
    for (NSUInteger offset = 0; offset < data.length; offset += 16) {
        uint8_t savedCipher[16];
        memcpy(savedCipher, inBytes + offset, 16);
        JCORESm4ProcessBlock(ctx.sk, inBytes + offset, outBytes + offset);
        for (int i = 0; i < 16; i++) {
            outBytes[offset + i] ^= currentIv[i];
        }
        memcpy(currentIv, savedCipher, 16);
    }
    return JCORESm4StripPkcs7Padding(output);
}

+ (NSData *)sm4ECBEncryptData:(NSData *)data
                      withKey:(NSString *)hexKey {
    if (data == nil) {
        return nil;
    }
    uint8_t rawKey[16];
    if (!JCORESm4DecodeHexKey16(hexKey, rawKey)) {
        return nil;
    }
    uint8_t padding = (uint8_t)(16 - (data.length % 16));
    NSMutableData *padded = [NSMutableData dataWithLength:data.length + padding];
    memset(padded.mutableBytes, padding, padded.length);
    if (data.length > 0) {
        memcpy(padded.mutableBytes, data.bytes, data.length);
    }

    JCORESm4Context ctx;
    JCORESm4InitKeySchedule(&ctx, rawKey, NO);

    NSMutableData *output = [NSMutableData dataWithLength:padded.length];
    const uint8_t *inBytes = padded.bytes;
    uint8_t *outBytes = output.mutableBytes;
    for (NSUInteger offset = 0; offset < padded.length; offset += 16) {
        JCORESm4ProcessBlock(ctx.sk, inBytes + offset, outBytes + offset);
    }
    return output;
}

+ (NSData *)sm4ECBDecryptData:(NSData *)data
                      withKey:(NSString *)hexKey {
    if (data.length == 0 || (data.length % 16) != 0) {
        return nil;
    }
    uint8_t rawKey[16];
    if (!JCORESm4DecodeHexKey16(hexKey, rawKey)) {
        return nil;
    }
    JCORESm4Context ctx;
    JCORESm4InitKeySchedule(&ctx, rawKey, YES);

    NSMutableData *output = [NSMutableData dataWithLength:data.length];
    const uint8_t *inBytes = data.bytes;
    uint8_t *outBytes = output.mutableBytes;
    for (NSUInteger offset = 0; offset < data.length; offset += 16) {
        JCORESm4ProcessBlock(ctx.sk, inBytes + offset, outBytes + offset);
    }
    return JCORESm4StripPkcs7Padding(output);
}

+ (NSData *)stripX509PublicKeyHeader:(NSData *)derData {
    static const unsigned char kRsaOidSequence[15] = {
        0x30, 0x0d, 0x06, 0x09, 0x2a, 0x86, 0x48, 0x86,
        0xf7, 0x0d, 0x01, 0x01, 0x01, 0x05, 0x00
    };
    const unsigned char *bytes = derData.bytes;
    NSUInteger length = derData.length;
    if (length < 20 || bytes[0] != 0x30) {
        return derData;
    }
    NSUInteger index = (bytes[1] > 0x80) ? (2 + bytes[1] - 0x80) : 2;
    if (index > length || length - index < 17) {
        return nil;
    }
    if (memcmp(bytes + index, kRsaOidSequence, sizeof(kRsaOidSequence)) != 0) {
        return derData;
    }
    index += sizeof(kRsaOidSequence);
    if (index >= length || bytes[index++] != 0x03) {
        return nil;
    }
    if (index >= length) {
        return nil;
    }
    unsigned char bitStringLength = bytes[index++];
    if (bitStringLength > 0x80) {
        index += (bitStringLength - 0x80);
    }
    if (index >= length || bytes[index++] != 0x00) {
        return nil;
    }
    return [NSData dataWithBytes:bytes + index length:length - index];
}

+ (NSString *)rsaEncryptString:(NSString *)plainText
               publicKeyBase64:(NSString *)publicKeyBase64 {
    if (plainText.length == 0 || publicKeyBase64.length == 0) {
        return nil;
    }
    NSString *cleaned = publicKeyBase64;
    cleaned = [cleaned stringByReplacingOccurrencesOfString:@"-----BEGIN PUBLIC KEY-----" withString:@""];
    cleaned = [cleaned stringByReplacingOccurrencesOfString:@"-----END PUBLIC KEY-----" withString:@""];
    cleaned = [[cleaned componentsSeparatedByCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] componentsJoinedByString:@""];

    NSData *rawKeyData = [[NSData alloc] initWithBase64EncodedString:cleaned
                                                             options:NSDataBase64DecodingIgnoreUnknownCharacters];
    NSData *pkcs1KeyData = [self stripX509PublicKeyHeader:rawKeyData];
    if (pkcs1KeyData.length == 0) {
        return nil;
    }

    NSDictionary *attributes = @{
        (__bridge id)kSecAttrKeyType: (__bridge id)kSecAttrKeyTypeRSA,
        (__bridge id)kSecAttrKeyClass: (__bridge id)kSecAttrKeyClassPublic,
    };
    CFErrorRef error = NULL;
    SecKeyRef publicKey = SecKeyCreateWithData((__bridge CFDataRef)pkcs1KeyData,
                                               (__bridge CFDictionaryRef)attributes,
                                               &error);
    if (error != NULL) {
        CFRelease(error);
        error = NULL;
    }
    if (publicKey == NULL && rawKeyData.length > 0) {
        publicKey = SecKeyCreateWithData((__bridge CFDataRef)rawKeyData,
                                         (__bridge CFDictionaryRef)attributes,
                                         &error);
        if (error != NULL) {
            CFRelease(error);
            error = NULL;
        }
    }
    if (publicKey == NULL) {
        return nil;
    }

    NSData *plainData = [plainText dataUsingEncoding:NSUTF8StringEncoding];
    size_t blockSize = SecKeyGetBlockSize(publicKey);
    size_t maxChunkSize = blockSize > 11 ? (blockSize - 11) : plainData.length;
    if (maxChunkSize == 0) {
        CFRelease(publicKey);
        return nil;
    }

    NSMutableData *encryptedTotal = [NSMutableData data];
    NSUInteger offset = 0;
    while (offset < plainData.length) {
        NSUInteger chunkLen = MIN((NSUInteger)maxChunkSize, plainData.length - offset);
        NSData *chunk = [plainData subdataWithRange:NSMakeRange(offset, chunkLen)];
        CFDataRef encryptedRef = SecKeyCreateEncryptedData(publicKey,
                                                           kSecKeyAlgorithmRSAEncryptionPKCS1,
                                                           (__bridge CFDataRef)chunk,
                                                           &error);
        if (error != NULL) {
            CFRelease(error);
            error = NULL;
        }
        if (encryptedRef == NULL) {
            CFRelease(publicKey);
            return nil;
        }
        NSData *encryptedChunk = (__bridge_transfer NSData *)encryptedRef;
        [encryptedTotal appendData:encryptedChunk];
        offset += chunkLen;
    }
    CFRelease(publicKey);
    return [encryptedTotal base64EncodedStringWithOptions:0];
}

+ (NSData *)gzipCompress:(NSData *)data {
    if (data.length == 0) {
        return data;
    }
    z_stream stream;
    memset(&stream, 0, sizeof(stream));
    stream.next_in = (Bytef *)data.bytes;
    stream.avail_in = (uInt)data.length;

    if (deflateInit2(&stream, Z_DEFAULT_COMPRESSION, Z_DEFLATED, 15 + 16, 8, Z_DEFAULT_STRATEGY) != Z_OK) {
        return nil;
    }
    NSMutableData *compressed = [NSMutableData dataWithLength:16384];
    int status = Z_OK;
    do {
        if (stream.total_out >= compressed.length) {
            [compressed increaseLengthBy:16384];
        }
        stream.next_out = (Bytef *)compressed.mutableBytes + stream.total_out;
        stream.avail_out = (uInt)(compressed.length - stream.total_out);
        status = deflate(&stream, Z_FINISH);
    } while (status == Z_OK);

    deflateEnd(&stream);
    if (status != Z_STREAM_END) {
        return nil;
    }
    compressed.length = stream.total_out;
    return compressed;
}

+ (NSData *)gzipDecompress:(NSData *)data {
    if (data.length == 0) {
        return data;
    }
    z_stream stream;
    memset(&stream, 0, sizeof(stream));
    stream.next_in = (Bytef *)data.bytes;
    stream.avail_in = (uInt)data.length;

    if (inflateInit2(&stream, 15 + 32) != Z_OK) {
        return nil;
    }
    NSMutableData *decompressed = [NSMutableData dataWithLength:data.length * 2];
    int status = Z_OK;
    while (status == Z_OK) {
        if (stream.total_out >= decompressed.length) {
            [decompressed increaseLengthBy:MAX((NSUInteger)4096, data.length)];
        }
        stream.next_out = (Bytef *)decompressed.mutableBytes + stream.total_out;
        stream.avail_out = (uInt)(decompressed.length - stream.total_out);
        status = inflate(&stream, Z_SYNC_FLUSH);
    }
    if (inflateEnd(&stream) != Z_OK || status != Z_STREAM_END) {
        return nil;
    }
    decompressed.length = stream.total_out;
    return decompressed;
}

+ (NSString *)md5Hex:(NSString *)input {
    NSData *data = [input dataUsingEncoding:NSUTF8StringEncoding];
    return [self md5HexForData:data];
}

+ (NSString *)md5HexForData:(NSData *)data {
    if (data == nil) {
        return @"";
    }
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    CC_MD5(data.bytes, (CC_LONG)data.length, digest);
#pragma clang diagnostic pop
    NSMutableString *hex = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_MD5_DIGEST_LENGTH; i++) {
        [hex appendFormat:@"%02x", digest[i]];
    }
    return hex;
}

+ (NSData *)sha1Data:(NSData *)data {
    if (data == nil) {
        return [NSData data];
    }
    unsigned char digest[CC_SHA1_DIGEST_LENGTH];
    CC_SHA1(data.bytes, (CC_LONG)data.length, digest);
    return [NSData dataWithBytes:digest length:CC_SHA1_DIGEST_LENGTH];
}

+ (NSString *)sha1Hex:(NSData *)data {
    if (data == nil) {
        return @"";
    }
    unsigned char digest[CC_SHA1_DIGEST_LENGTH];
    CC_SHA1(data.bytes, (CC_LONG)data.length, digest);
    NSMutableString *hex = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_SHA1_DIGEST_LENGTH; i++) {
        [hex appendFormat:@"%02x", digest[i]];
    }
    return hex;
}

+ (NSString *)sha1HexForString:(NSString *)input {
    NSData *data = [input ?: @"" dataUsingEncoding:NSUTF8StringEncoding];
    return [self sha1Hex:data];
}

+ (NSString *)hmacSHA256HexWithKey:(NSString *)key content:(NSString *)content {
    if (content.length == 0 || key.length == 0) {
        return nil;
    }
    const char *keyBytes = [key UTF8String];
    const char *contentBytes = [content UTF8String];
    unsigned char digest[CC_SHA256_DIGEST_LENGTH];
    CCHmac(kCCHmacAlgSHA256, keyBytes, strlen(keyBytes), contentBytes, strlen(contentBytes), digest);
    NSMutableString *hex = [NSMutableString stringWithCapacity:CC_SHA256_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_SHA256_DIGEST_LENGTH; i++) {
        [hex appendFormat:@"%02x", digest[i]];
    }
    return hex;
}

+ (NSString *)hmacSha256Hex:(NSString *)content key:(NSString *)key {
    return [self hmacSHA256HexWithKey:key content:content];
}

@end
