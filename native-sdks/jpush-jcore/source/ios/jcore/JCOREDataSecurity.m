#import "JCOREDataSecurity.h"
#import "JCOREMD5Utilities.h"
#import "JCORESm4Crypt.h"
#import <CommonCrypto/CommonHMAC.h>
#import <UIKit/UIKit.h>
#include <stdlib.h>
#include <string.h>

CCOptions JCorePadding = kCCOptionPKCS7Padding;

static uint32_t JCORESHA1Rotate(uint32_t value, unsigned int shift) {
    return (value << shift) | (value >> (32 - shift));
}

void jcore_sha1_transform(uint32_t *state, const unsigned char *buffer) {
    uint32_t words[80];
    for (unsigned int i = 0; i < 16; i++) {
        words[i] = ((uint32_t)buffer[i * 4] << 24) | ((uint32_t)buffer[i * 4 + 1] << 16)
            | ((uint32_t)buffer[i * 4 + 2] << 8) | buffer[i * 4 + 3];
    }
    for (unsigned int i = 16; i < 80; i++) words[i] = JCORESHA1Rotate(words[i - 3] ^ words[i - 8] ^ words[i - 14] ^ words[i - 16], 1);
    uint32_t a = state[0], b = state[1], c = state[2], d = state[3], e = state[4];
    for (unsigned int i = 0; i < 80; i++) {
        uint32_t f, k;
        if (i < 20) { f = (b & c) | (~b & d); k = 0x5a827999; }
        else if (i < 40) { f = b ^ c ^ d; k = 0x6ed9eba1; }
        else if (i < 60) { f = (b & c) | (b & d) | (c & d); k = 0x8f1bbcdc; }
        else { f = b ^ c ^ d; k = 0xca62c1d6; }
        uint32_t temp = JCORESHA1Rotate(a, 5) + f + e + k + words[i];
        e = d; d = c; c = JCORESHA1Rotate(b, 30); b = a; a = temp;
    }
    state[0] += a; state[1] += b; state[2] += c; state[3] += d; state[4] += e;
}

void jcore_sha1(const unsigned char *message, size_t message_len, unsigned char *digest) {
    uint32_t state[5] = {0x67452301,0xefcdab89,0x98badcfe,0x10325476,0xc3d2e1f0};
    size_t complete = message_len & ~(size_t)63;
    for (size_t i = 0; i < complete; i += 64) jcore_sha1_transform(state, message + i);
    unsigned char tail[128] = {0};
    size_t remainder = message_len - complete;
    if (remainder) memcpy(tail, message + complete, remainder);
    tail[remainder] = 0x80;
    size_t padded = remainder < 56 ? 64 : 128;
    uint64_t bits = (uint64_t)message_len * 8;
    for (unsigned int i = 0; i < 8; i++) tail[padded - 1 - i] = (unsigned char)(bits >> (i * 8));
    jcore_sha1_transform(state, tail);
    if (padded == 128) jcore_sha1_transform(state, tail + 64);
    for (unsigned int i = 0; i < 20; i++) digest[i] = (unsigned char)(state[i / 4] >> (24 - (i % 4) * 8));
}

NSString *JCoreBase64UrlEncodedStringFromBase64String(NSString *base64String) {
    NSString *value = [base64String stringByReplacingOccurrencesOfString:@"=" withString:@""];
    value = [value stringByReplacingOccurrencesOfString:@"+" withString:@"-"];
    return [value stringByReplacingOccurrencesOfString:@"/" withString:@"_"];
}

@implementation JCOREDataSecurity
+ (NSString *)encryptDataTypeToCurrentStringType:(id)data {
    return [data isKindOfClass:NSData.class] && [data length] ? [self base64:data] : nil;
}
+ (NSString *)decrypt:(id)string aesKey:(NSString *)key {
    if (![string isKindOfClass:NSString.class] || ![string length]) return nil;
    NSData *data = [JCOREDataSecurity AES256DecryptData:[self dataFromBase64:string] withKey:key];
    return [data isKindOfClass:NSData.class] && data.length ? [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] : nil;
}
+ (NSString *)encrypt:(id)string aesKey:(NSString *)key {
    if (![string isKindOfClass:NSString.class] || ![string length]) return nil;
    return [self base64:[JCOREDataSecurity AES256EncryptData:[string dataUsingEncoding:NSUTF8StringEncoding] withKey:key]];
}
- (NSData *)encrypt:(NSData *)data key:(NSData *)key padding:(unsigned int *)padding {
    return [self doCipher:data key:key context:kCCEncrypt padding:padding];
}
- (NSData *)decrypt:(NSData *)data key:(NSData *)key padding:(unsigned int *)padding {
    return [self doCipher:data key:key context:kCCDecrypt padding:padding];
}
- (NSData *)doCipher:(NSData *)data key:(NSData *)key context:(unsigned int)context padding:(unsigned int *)padding {
    // These inputs would dereference NULL or read beyond the original key buffer.
    if (!padding || key.length < 16) return nil;
    CCCryptorRef cryptor = NULL;
    unsigned char iv[16] = {0};
    CCCryptorStatus status = CCCryptorCreate(context, kCCAlgorithmAES, *padding, key.bytes, 16, iv, &cryptor);
    if (status != kCCSuccess) return nil;
    size_t capacity = CCCryptorGetOutputLength(cryptor, data.length, true);
    void *output = malloc(capacity);
    bzero(output, capacity);
    size_t written = 0;
    status = CCCryptorUpdate(cryptor, data.bytes, data.length, output, capacity, &written);
    if (status != kCCSuccess) {
        CCCryptorRelease(cryptor);
        free(output);
        return nil;
    }
    size_t updateLength = written;
    status = CCCryptorFinal(cryptor, (unsigned char *)output + updateLength, capacity - updateLength, &written);
    if (cryptor) CCCryptorRelease(cryptor);
    NSData *result = status == kCCSuccess ? [NSData dataWithBytes:output length:updateLength + written] : nil;
    if (output) free(output);
    return result;
}
+ (NSData *)AES256EncryptData:(id)data withKey:(NSString *)key {
    return [self doCryptWith:data key:key keyLen:16 operation:kCCEncrypt options:3 iv:NULL];
}
+ (NSData *)AES256DecryptData:(id)data withKey:(NSString *)key {
    return [self doCryptWith:data key:key keyLen:16 operation:kCCDecrypt options:3 iv:NULL];
}
+ (NSData *)aes256EncryptData:(id)data withKey:(NSString *)key iv:(const char *)iv {
    return [self doCryptWith:data key:key keyLen:32 operation:kCCEncrypt options:1 iv:iv];
}
+ (NSData *)aes256DecryptData:(id)data withKey:(NSString *)key iv:(const char *)iv {
    return [self doCryptWith:data key:key keyLen:32 operation:kCCDecrypt options:1 iv:iv];
}
+ (NSData *)aesEncryptData:(id)data withKey:(NSString *)key iv:(const char *)iv {
    return [self doCryptWith:data key:key operation:kCCEncrypt options:1 iv:iv];
}
+ (NSData *)aesDecryptData:(id)data withKey:(NSString *)key iv:(const char *)iv {
    return [self doCryptWith:data key:key operation:kCCDecrypt options:1 iv:iv];
}
+ (NSData *)aes256EncryptData:(id)data withKey:(NSString *)key {
    return [self doCryptWith:data key:key keyLen:32 operation:kCCEncrypt options:3 iv:NULL];
}
+ (NSData *)aes256CBCEncryptData:(id)data withKey:(NSString *)key {
    NSData *decoded = [[NSData alloc] initWithBase64EncodedString:@"aW9wMjAzMDQwNTA2YVBrIQ==" options:0];
    NSString *iv = [decoded isKindOfClass:NSData.class] && decoded.length ? [[NSString alloc] initWithData:decoded encoding:NSUTF8StringEncoding] : @"";
    return [self doCryptWith:data key:key keyLen:32 operation:kCCEncrypt options:1 iv:iv.UTF8String];
}
+ (NSData *)aes256DecryptData:(id)data withKey:(NSString *)key {
    return [self doCryptWith:data key:key keyLen:32 operation:kCCDecrypt options:3 iv:NULL];
}
+ (NSData *)doCryptWith:(id)data key:(NSString *)key keyLen:(NSUInteger)keyLen operation:(unsigned int)operation options:(unsigned int)options iv:(const void *)iv {
    if (![data isKindOfClass:NSData.class] || ![data length]) return nil;
    char keyBytes[33] = {0};
    [key getCString:keyBytes maxLength:sizeof(keyBytes) encoding:NSUTF8StringEncoding];
    size_t capacity = [data length] + 16, written = 0;
    void *output = malloc(capacity);
    CCCryptorStatus status = CCCrypt(operation, kCCAlgorithmAES, options, keyBytes, keyLen, iv, [data bytes], [data length], output, capacity, &written);
    if (status == kCCSuccess) return [NSData dataWithBytesNoCopy:output length:written];
    free(output);
    return nil;
}
+ (NSData *)doCryptWith:(id)data key:(NSString *)key operation:(unsigned int)operation options:(unsigned int)options iv:(const void *)iv {
    if (![data isKindOfClass:NSData.class] || ![data length]) return nil;
    NSUInteger keyLength = key.length;
    char keyBytes[keyLength + 1];
    bzero(keyBytes, keyLength + 1);
    [key getCString:keyBytes maxLength:keyLength + 1 encoding:NSUTF8StringEncoding];
    size_t capacity = [data length] + 16, written = 0;
    void *output = malloc(capacity);
    CCCryptorStatus status = CCCrypt(operation, kCCAlgorithmAES, options, keyBytes, keyLength, iv, [data bytes], [data length], output, capacity, &written);
    if (status == kCCSuccess) return [NSData dataWithBytesNoCopy:output length:written];
    free(output);
    return nil;
}
+ (NSString *)encryptKeyWithSeed:(unsigned long long)seed {
    static unsigned long long oldSeed;
    static NSMutableDictionary *mutableDict;
    if (seed) oldSeed = seed;
    unsigned long long current = oldSeed;
    NSString *cached = [mutableDict isKindOfClass:NSMutableDictionary.class] ? mutableDict[@(current)] : nil;
    if (cached) return cached;
    NSString *value = [JCOREDataSecurity generateSeedKey:current];
    if (!mutableDict) mutableDict = [NSMutableDictionary dictionary];
    if (value) mutableDict[@(current)] = value;
    return value;
}
+ (NSString *)encryptSisKeyWithSeed:(unsigned long long)seed {
    static NSMutableDictionary *sisSeedDict;
    NSString *cached = [sisSeedDict isKindOfClass:NSMutableDictionary.class] ? sisSeedDict[@(seed)] : nil;
    if (cached) return cached;
    NSString *value = [JCOREDataSecurity generateSeedKey:seed];
    if (!sisSeedDict) sisSeedDict = [NSMutableDictionary dictionary];
    if (value) sisSeedDict[@(seed)] = value;
    return value;
}
+ (NSString *)encryptKeyForHttp {
    unsigned int seed;
    return [self encryptKeyForHttp:&seed];
}
+ (NSString *)encryptKeyForHttp:(unsigned int *)seed {
    *seed = arc4random();
    return [self generateSeedKey:*seed];
}
+ (NSString *)generateSeedKey:(unsigned long long)seed {
    static const unsigned int multipliers[10] = {8,5,23,3,13,17,7,31,29,37};
    static const unsigned int moduli[10] = {74,88,15,73,96,49,68,39,41,91};
    NSUInteger index = seed % 10;
    unsigned long long value = seed * multipliers[index] + seed % moduli[index];
    return [self stringHashingByMD5:[NSString stringWithFormat:@"JCKP%lld", (long long)value]];
}
+ (NSString *)sha1:(id)value {
    NSData *data;
    if ([value isKindOfClass:NSString.class] && [value length]) data = [value dataUsingEncoding:NSUTF8StringEncoding];
    else if ([value isKindOfClass:NSData.class] && [value length]) data = value;
    else return nil;
    unsigned char digest[20];
    jcore_sha1(data.bytes, data.length, digest);
    NSMutableString *result = [NSMutableString stringWithCapacity:40];
    for (NSUInteger i = 0; i < 20; i++) [result appendFormat:@"%02x", digest[i]];
    return [result copy];
}
+ (NSString *)hmacSHA256WithKey:(id)key content:(id)content {
    if (![key isKindOfClass:NSString.class] || ![key length] || ![content isKindOfClass:NSString.class] || ![content length]) return nil;
    const char *keyBytes = [key cStringUsingEncoding:NSUTF8StringEncoding];
    const char *contentBytes = [content cStringUsingEncoding:NSUTF8StringEncoding];
    unsigned char digest[32];
    CCHmac(kCCHmacAlgSHA256, keyBytes, strlen(keyBytes), contentBytes, strlen(contentBytes), digest);
    NSMutableString *result = [NSMutableString stringWithCapacity:64];
    for (NSUInteger i = 0; i < 32; i++) [result appendFormat:@"%02x", digest[i]];
    return result;
}
+ (NSString *)rsaEncrypt:(id)string publicKey:(id)publicKey {
    if (![string isKindOfClass:NSString.class] || ![publicKey isKindOfClass:NSString.class]) return nil;
    SecKeyRef key = [self addPublicKey:publicKey];
    if (!key) return nil;
    return [self base64:[self encryptData:[string dataUsingEncoding:NSUTF8StringEncoding] withKeyRef:key]];
}
+ (SecKeyRef)addPublicKey:(NSString *)key {
    NSRange begin = [key rangeOfString:@"-----BEGIN PUBLIC KEY-----"];
    NSRange end = [key rangeOfString:@"-----END PUBLIC KEY-----"];
    if (begin.location != NSNotFound && end.location != NSNotFound) key = [key substringWithRange:NSMakeRange(NSMaxRange(begin), end.location - NSMaxRange(begin))];
    for (NSString *space in @[@"\r", @"\n", @"\t", @" "]) key = [key stringByReplacingOccurrencesOfString:space withString:@""];
    NSData *data = [self stripPublicKeyHeader:[[NSData alloc] initWithBase64EncodedString:key options:NSDataBase64DecodingIgnoreUnknownCharacters]];
    if (!data) return NULL;
    NSString *tag = @"JCORE_RSA_PubKey";
    NSData *tagData = [NSData dataWithBytes:tag.UTF8String length:tag.length];
    NSMutableDictionary *query = [NSMutableDictionary dictionary];
    query[(__bridge id)kSecClass] = (__bridge id)kSecClassKey;
    query[(__bridge id)kSecAttrKeyType] = (__bridge id)kSecAttrKeyTypeRSA;
    query[(__bridge id)kSecAttrApplicationTag] = tagData;
    SecItemDelete((__bridge CFDictionaryRef)query);
    query[(__bridge id)kSecValueData] = data;
    query[(__bridge id)kSecAttrKeyClass] = (__bridge id)kSecAttrKeyClassPublic;
    query[(__bridge id)kSecReturnPersistentRef] = @YES;
    CFTypeRef persistent = NULL;
    OSStatus status = SecItemAdd((__bridge CFDictionaryRef)query, &persistent);
    if (persistent) CFRelease(persistent);
    if (status != errSecSuccess && status != errSecDuplicateItem) return NULL;
    [query removeObjectForKey:(__bridge id)kSecValueData];
    [query removeObjectForKey:(__bridge id)kSecReturnPersistentRef];
    query[(__bridge id)kSecReturnRef] = @YES;
    query[(__bridge id)kSecAttrKeyType] = (__bridge id)kSecAttrKeyTypeRSA;
    SecKeyRef result = NULL;
    status = SecItemCopyMatching((__bridge CFDictionaryRef)query, (CFTypeRef *)&result);
    return status == errSecSuccess ? result : NULL;
}
+ (NSData *)stripPublicKeyHeader:(NSData *)data {
    static const unsigned char sequence[15] = {0x30,0x0d,0x06,0x09,0x2a,0x86,0x48,0x86,0xf7,0x0d,0x01,0x01,0x01,0x05,0x00};
    const unsigned char *bytes = data.bytes;
    NSUInteger length = data.length;
    if (!length || bytes[0] != 0x30 || length < 2) return nil;
    NSUInteger index = bytes[1] > 0x80 ? 2 + bytes[1] - 0x80 : 2;
    // Reject truncated DER before dereferencing; the original has no defined result for these inputs.
    if (index > length || length - index < 17 || memcmp(bytes + index, sequence, sizeof(sequence))) return nil;
    index += 15;
    if (bytes[index++] != 0x03) return nil;
    unsigned char headerLength = bytes[index++];
    if (headerLength > 0x80) index += headerLength - 0x80;
    if (index >= length || bytes[index++] != 0) return nil;
    return [NSData dataWithBytes:bytes + index length:length - index];
}
+ (NSData *)encryptData:(NSData *)data withKeyRef:(SecKeyRef)key {
    if (@available(iOS 10.0, *)) return [self encryptDataIOS10Higher:data withKeyRef:key];
    return [self encryptDataIOS10Lower:data withKeyRef:key];
}
+ (NSData *)encryptDataIOS10Lower:(NSData *)data withKeyRef:(SecKeyRef)key {
    size_t blockSize = SecKeyGetBlockSize(key);
    void *output = malloc(blockSize);
    NSMutableData *result = [NSMutableData data];
    NSUInteger capacity = blockSize - 11;
    for (NSInteger offset = 0; (NSUInteger)offset < data.length; offset = (int)offset + (int)capacity) {
        size_t written = blockSize;
        size_t length = MIN(capacity, data.length - offset);
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        OSStatus status = SecKeyEncrypt(key, kSecPaddingPKCS1, (const unsigned char *)data.bytes + offset, length, output, &written);
#pragma clang diagnostic pop
        if (status) { result = nil; break; }
        [result appendBytes:output length:written];
    }
    free(output);
    CFRelease(key);
    return result;
}
+ (NSData *)encryptDataIOS10Higher:(NSData *)data withKeyRef:(SecKeyRef)key {
    NSUInteger capacity = SecKeyGetBlockSize(key) - 11;
    NSMutableData *result = [NSMutableData data];
    NSMutableArray *chunks = [NSMutableArray array];
    if (data.length < capacity) [chunks addObject:data];
    else {
        // Preserve the original loop bound, including its NSRangeException for oversized input.
        for (NSUInteger i = 0; i < capacity; i++) {
            NSUInteger offset = i * capacity;
            [chunks addObject:[data subdataWithRange:NSMakeRange(offset, MIN((i + 1) * capacity, data.length) - offset)]];
        }
    }
    for (NSData *chunk in chunks) {
        CFDataRef encrypted = NULL;
        if (@available(iOS 10.0, *)) encrypted = SecKeyCreateEncryptedData(key, kSecKeyAlgorithmRSAEncryptionPKCS1, (__bridge CFDataRef)chunk, NULL);
        if (!encrypted) { result = nil; break; }
        [result appendData:(__bridge NSData *)encrypted];
        CFRelease(encrypted);
    }
    CFRelease(key);
    return result;
}
+ (NSString *)base64:(NSData *)data {
    if ([UIDevice.currentDevice.systemVersion floatValue] >= 7.0) return [data base64EncodedStringWithOptions:0];
    static const char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    if (!data.length) return @"";
    NSMutableString *result = [NSMutableString stringWithCapacity:data.length];
    const unsigned char *bytes = data.bytes;
    for (NSUInteger offset = 0; offset < data.length; offset += 3) {
        NSUInteger count = MIN((NSUInteger)3, data.length - offset);
        unsigned int word = (unsigned int)bytes[offset] << 16;
        if (count > 1) word |= (unsigned int)bytes[offset + 1] << 8;
        if (count > 2) word |= bytes[offset + 2];
        for (NSUInteger i = 0; i < 4; i++) [result appendFormat:@"%c", i < count + 1 ? alphabet[(word >> (18 - i * 6)) & 63] : '='];
    }
    return result;
}
+ (NSData *)dataFromBase64:(id)string {
    if (![string isKindOfClass:NSString.class] || ![string length]) return nil;
    if ([UIDevice.currentDevice.systemVersion floatValue] >= 7.0) return [[NSData alloc] initWithBase64EncodedString:string options:0];
    @try {
    NSString *stripped = [string stringByReplacingOccurrencesOfString:@"=" withString:@""];
    NSData *encoded = [stripped dataUsingEncoding:NSASCIIStringEncoding];
    const unsigned char *bytes = encoded.bytes;
    NSMutableData *decoded = [NSMutableData data];
    unsigned char quartet[4]; NSUInteger count = 0;
    for (NSUInteger i = 0; i < encoded.length; i++) {
        unsigned char c = bytes[i]; int value = -1;
        if (c >= 'A' && c <= 'Z') value = c - 'A';
        else if (c >= 'a' && c <= 'z') value = c - 'a' + 26;
        else if (c >= '0' && c <= '9') value = c - '0' + 52;
        else if (c == '+') value = 62;
        else if (c == '/') value = 63;
        if (value < 0) continue;
        quartet[count++] = (unsigned char)value;
        if (count == 4) {
            unsigned char block[3] = {(unsigned char)((quartet[0] << 2) | (quartet[1] >> 4)), (unsigned char)((quartet[1] << 4) | (quartet[2] >> 2)), (unsigned char)((quartet[2] << 6) | quartet[3])};
            [decoded appendBytes:block length:3]; count = 0;
        }
    }
    if (count >= 2) {
        unsigned char block[2] = {(unsigned char)((quartet[0] << 2) | (quartet[1] >> 4)), 0};
        if (count == 3) block[1] = (unsigned char)((quartet[1] << 4) | (quartet[2] >> 2));
        [decoded appendBytes:block length:count - 1];
    }
    return [decoded copy];
    } @catch (NSException *exception) {
        NSLog(@"WARNING: error occured while decoding base 32 string: %@", exception.reason);
        return nil;
    }
}
+ (NSString *)stringFromBase64:(id)string {
    return [[NSString alloc] initWithData:[self dataFromBase64:string] encoding:NSUTF8StringEncoding];
}
+ (NSString *)stringHashingByMD5:(id)string { return [JCOREMD5Utilities stringHashingByMD5:string]; }
+ (NSData *)stringHashingByMD5Data:(id)string { return [JCOREMD5Utilities stringHashingByMD5Data:string]; }
+ (NSString *)dataHashingMD5:(NSData *)data { return [JCOREMD5Utilities dataHashingMD5:data]; }
+ (NSData *)sm4ECBEncryptData:(NSData *)data withKey:(NSString *)key { return [JCORESm4Crypt ecbEncrypt:data withKey:key]; }
+ (NSData *)sm4CBCEncryptData:(NSData *)data withKey:(NSString *)key iv:(const char *)iv { return [JCORESm4Crypt cbcEncrypt:data withKey:key iv:iv]; }
+ (NSData *)sm4ECBDecryptData:(NSData *)data withKey:(NSString *)key { return [JCORESm4Crypt ecbDecrypt:data withKey:key]; }
+ (NSData *)sm4CBCDecryptData:(NSData *)data withKey:(NSString *)key iv:(const char *)iv { return [JCORESm4Crypt cbcDecrypt:data withKey:key iv:iv]; }
@end
