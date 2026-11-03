#import "JCORESm4Crypt.h"
#import "JCORELog.h"
#include <stdint.h>
#include <string.h>

static const unsigned char JCORESm4SBox[256] = {
    0xd6,0x90,0xe9,0xfe,0xcc,0xe1,0x3d,0xb7,0x16,0xb6,0x14,0xc2,0x28,0xfb,0x2c,0x05,
    0x2b,0x67,0x9a,0x76,0x2a,0xbe,0x04,0xc3,0xaa,0x44,0x13,0x26,0x49,0x86,0x06,0x99,
    0x9c,0x42,0x50,0xf4,0x91,0xef,0x98,0x7a,0x33,0x54,0x0b,0x43,0xed,0xcf,0xac,0x62,
    0xe4,0xb3,0x1c,0xa9,0xc9,0x08,0xe8,0x95,0x80,0xdf,0x94,0xfa,0x75,0x8f,0x3f,0xa6,
    0x47,0x07,0xa7,0xfc,0xf3,0x73,0x17,0xba,0x83,0x59,0x3c,0x19,0xe6,0x85,0x4f,0xa8,
    0x68,0x6b,0x81,0xb2,0x71,0x64,0xda,0x8b,0xf8,0xeb,0x0f,0x4b,0x70,0x56,0x9d,0x35,
    0x1e,0x24,0x0e,0x5e,0x63,0x58,0xd1,0xa2,0x25,0x22,0x7c,0x3b,0x01,0x21,0x78,0x87,
    0xd4,0x00,0x46,0x57,0x9f,0xd3,0x27,0x52,0x4c,0x36,0x02,0xe7,0xa0,0xc4,0xc8,0x9e,
    0xea,0xbf,0x8a,0xd2,0x40,0xc7,0x38,0xb5,0xa3,0xf7,0xf2,0xce,0xf9,0x61,0x15,0xa1,
    0xe0,0xae,0x5d,0xa4,0x9b,0x34,0x1a,0x55,0xad,0x93,0x32,0x30,0xf5,0x8c,0xb1,0xe3,
    0x1d,0xf6,0xe2,0x2e,0x82,0x66,0xca,0x60,0xc0,0x29,0x23,0xab,0x0d,0x53,0x4e,0x6f,
    0xd5,0xdb,0x37,0x45,0xde,0xfd,0x8e,0x2f,0x03,0xff,0x6a,0x72,0x6d,0x6c,0x5b,0x51,
    0x8d,0x1b,0xaf,0x92,0xbb,0xdd,0xbc,0x7f,0x11,0xd9,0x5c,0x41,0x1f,0x10,0x5a,0xd8,
    0x0a,0xc1,0x31,0x88,0xa5,0xcd,0x7b,0xbd,0x2d,0x74,0xd0,0x12,0xb8,0xe5,0xb4,0xb0,
    0x89,0x69,0x97,0x4a,0x0c,0x96,0x77,0x7e,0x65,0xb9,0xf1,0x09,0xc5,0x6e,0xc6,0x84,
    0x18,0xf0,0x7d,0xec,0x3a,0xdc,0x4d,0x20,0x79,0xee,0x5f,0x3e,0xd7,0xcb,0x39,0x48,
};

static uint32_t JCORESm4Rotate(uint32_t value, unsigned int shift) {
    return (value << shift) | (value >> (32 - shift));
}
static uint32_t JCORESm4Substitute(uint32_t value) {
    return ((uint32_t)JCORESm4SBox[value >> 24] << 24)
        | ((uint32_t)JCORESm4SBox[(value >> 16) & 255] << 16)
        | ((uint32_t)JCORESm4SBox[(value >> 8) & 255] << 8) | JCORESm4SBox[value & 255];
}
static uint32_t JCORESm4Read(const unsigned char *bytes) {
    return ((uint32_t)bytes[0] << 24) | ((uint32_t)bytes[1] << 16) | ((uint32_t)bytes[2] << 8) | bytes[3];
}
static void JCORESm4Write(unsigned char *bytes, uint32_t value) {
    bytes[0] = (unsigned char)(value >> 24); bytes[1] = (unsigned char)(value >> 16);
    bytes[2] = (unsigned char)(value >> 8); bytes[3] = (unsigned char)value;
}
static void JCORESm4Key(JCORESm4Context *ctx, const unsigned char *key, BOOL decrypt) {
    static const uint32_t fk[4] = {0xa3b1bac6,0x56aa3350,0x677d9197,0xb27022dc};
    uint32_t words[36];
    for (unsigned int i = 0; i < 4; i++) words[i] = JCORESm4Read(key + i * 4) ^ fk[i];
    for (unsigned int i = 0; i < 32; i++) {
        uint32_t ck = 0;
        for (unsigned int j = 0; j < 4; j++) ck = (ck << 8) | (((i * 4 + j) * 7) & 255);
        uint32_t value = JCORESm4Substitute(words[i + 1] ^ words[i + 2] ^ words[i + 3] ^ ck);
        words[i + 4] = words[i] ^ value ^ JCORESm4Rotate(value, 13) ^ JCORESm4Rotate(value, 23);
        ctx->sk[decrypt ? 31 - i : i] = words[i + 4];
    }
    ctx->mode = decrypt ? 0 : 1;
}
static void JCORESm4Round(const unsigned long *keys, const unsigned char *input, unsigned char *output) {
    uint32_t words[36];
    for (unsigned int i = 0; i < 4; i++) words[i] = JCORESm4Read(input + i * 4);
    for (unsigned int i = 0; i < 32; i++) {
        uint32_t value = JCORESm4Substitute(words[i + 1] ^ words[i + 2] ^ words[i + 3] ^ (uint32_t)keys[i]);
        words[i + 4] = words[i] ^ value ^ JCORESm4Rotate(value, 2) ^ JCORESm4Rotate(value, 10)
            ^ JCORESm4Rotate(value, 18) ^ JCORESm4Rotate(value, 24);
    }
    for (unsigned int i = 0; i < 4; i++) JCORESm4Write(output + i * 4, words[35 - i]);
}
void JCORESm4CryptECB(JCORESm4Context *ctx, int mode, int length, unsigned char *input, unsigned char *output) {
    for (int offset = 0; offset < length; offset += 16) JCORESm4Round(ctx->sk, input + offset, output + offset);
}
void JCORESm4CryptCBC(JCORESm4Context *ctx, int mode, int length, unsigned char *iv, unsigned char *input, unsigned char *output) {
    for (int offset = 0; offset < length; offset += 16) {
        if (mode == 1) {
            for (unsigned int i = 0; i < 16; i++) output[offset + i] = input[offset + i] ^ iv[i];
            JCORESm4Round(ctx->sk, output + offset, output + offset);
            memcpy(iv, output + offset, 16);
        } else {
            unsigned char saved[16]; memcpy(saved, input + offset, 16);
            JCORESm4Round(ctx->sk, input + offset, output + offset);
            for (unsigned int i = 0; i < 16; i++) output[offset + i] ^= iv[i];
            memcpy(iv, saved, 16);
        }
    }
}
static void JCORESm4Invalid(const char *function, NSUInteger line, NSString *message) {
    [JCORELog log:YES level:0x3ff flag:0x40 context:0
        file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/SM4/JCORESm4Crypt.m"
        function:function line:line tag:nil format:message];
}
static NSData *JCORESm4RemovePadding(NSData *data) {
    const unsigned char *bytes = data.bytes;
    NSUInteger length = data.length;
    unsigned int padding = bytes[length - 1];
    if (padding >= 16 || padding > length) padding = 0;
    for (unsigned int i = 1; i < padding; i++) if (bytes[length - 1 - i] != padding) { padding = 0; break; }
    return [NSData dataWithBytes:bytes length:length - padding];
}

@implementation JCORESm4Crypt
+ (void)convertString:(NSString *)string toChars:(char *)chars {
    unsigned int value = 0;
    for (NSUInteger i = 0; i < 16; i++) {
        NSScanner *scanner = [[NSScanner alloc] initWithString:[string substringWithRange:NSMakeRange(i * 2, 2)]];
        [scanner scanHexInt:&value];
        chars[i] = (char)value;
    }
}
+ (NSData *)ecbEncrypt:(NSData *)data withKey:(NSString *)key {
    if (!data || key.length != 32) {
        JCORESm4Invalid("+[JCORESm4Crypt ecbEncrypt:withKey:]", 58, @"source or key error, sm4 encrypt cancel"); return nil;
    }
    unsigned int padding = 16 - data.length % 16;
    NSMutableData *padded = [NSMutableData dataWithLength:data.length + padding];
    memset(padded.mutableBytes, padding, padded.length); memcpy(padded.mutableBytes, data.bytes, data.length);
    char keyBytes[16]; [self convertString:key toChars:keyBytes];
    JCORESm4Context ctx; JCORESm4Key(&ctx, (unsigned char *)keyBytes, NO);
    NSMutableData *output = [NSMutableData dataWithLength:padded.length];
    JCORESm4CryptECB(&ctx, 1, (int)padded.length, padded.mutableBytes, output.mutableBytes);
    return [NSData dataWithBytes:output.bytes length:output.length];
}
+ (NSData *)ecbDecrypt:(NSData *)data withKey:(NSString *)key {
    if (!data || key.length != 32) {
        JCORESm4Invalid("+[JCORESm4Crypt ecbDecrypt:withKey:]", 83, @"source or key error, sm4 decrypt cancel"); return nil;
    }
    // Partial/empty cipher blocks have no defined original result; never read outside NSData.
    if (!data.length || data.length % 16) return nil;
    char keyBytes[16]; [self convertString:key toChars:keyBytes];
    JCORESm4Context ctx; JCORESm4Key(&ctx, (unsigned char *)keyBytes, YES);
    NSMutableData *output = [NSMutableData dataWithLength:data.length];
    JCORESm4CryptECB(&ctx, 0, (int)data.length, (unsigned char *)data.bytes, output.mutableBytes);
    return JCORESm4RemovePadding(output);
}
+ (NSData *)cbcEncrypt:(NSData *)data withKey:(NSString *)key iv:(const char *)iv {
    if (!data || key.length != 32 || !iv) {
        JCORESm4Invalid("+[JCORESm4Crypt cbcEncrypt:withKey:iv:]", 123, @"source or key or iv error, sm4 encrypt cancel"); return nil;
    }
    unsigned int padding = 16 - data.length % 16;
    NSMutableData *padded = [NSMutableData dataWithLength:data.length + padding];
    memset(padded.mutableBytes, padding, padded.length); memcpy(padded.mutableBytes, data.bytes, data.length);
    char keyBytes[16]; [self convertString:key toChars:keyBytes];
    JCORESm4Context ctx; JCORESm4Key(&ctx, (unsigned char *)keyBytes, NO);
    unsigned char currentIV[16]; memcpy(currentIV, iv, 16);
    NSMutableData *output = [NSMutableData dataWithLength:padded.length];
    JCORESm4CryptCBC(&ctx, 1, (int)padded.length, currentIV, padded.mutableBytes, output.mutableBytes);
    return [NSData dataWithBytes:output.bytes length:output.length];
}
+ (NSData *)cbcDecrypt:(NSData *)data withKey:(NSString *)key iv:(const char *)iv {
    if (!data || key.length != 32 || !iv) {
        JCORESm4Invalid("+[JCORESm4Crypt cbcDecrypt:withKey:iv:]", 151, @"source or key or iv error, sm4 decrypt cancel"); return nil;
    }
    if (!data.length || data.length % 16) return nil;
    char keyBytes[16]; [self convertString:key toChars:keyBytes];
    JCORESm4Context ctx; JCORESm4Key(&ctx, (unsigned char *)keyBytes, YES);
    unsigned char currentIV[16]; memcpy(currentIV, iv, 16);
    NSMutableData *output = [NSMutableData dataWithLength:data.length];
    JCORESm4CryptCBC(&ctx, 0, (int)data.length, currentIV, (unsigned char *)data.bytes, output.mutableBytes);
    return JCORESm4RemovePadding(output);
}
@end
