#import "JCOREMD5Utilities.h"
#include <stdint.h>
#include <string.h>

unsigned char JCOREPADDING[64] = { 0x80 };

void JCOREMD5Init(JCORE_MD5_CTX *context) {
    context->count[0] = context->count[1] = 0;
    context->state[0] = 0x67452301;
    context->state[1] = 0xefcdab89;
    context->state[2] = 0x98badcfe;
    context->state[3] = 0x10325476;
}

void JCOREMD5Encode(unsigned char *output, unsigned int *input, unsigned int len) {
    for (unsigned int i = 0, j = 0; j < len; i++, j += 4) {
        output[j] = (unsigned char)input[i];
        output[j + 1] = (unsigned char)(input[i] >> 8);
        output[j + 2] = (unsigned char)(input[i] >> 16);
        output[j + 3] = (unsigned char)(input[i] >> 24);
    }
}

void JCOREMD5Decode(unsigned int *output, unsigned char *input, unsigned int len) {
    for (unsigned int i = 0, j = 0; j < len; i++, j += 4) {
        output[i] = (unsigned int)input[j] | ((unsigned int)input[j + 1] << 8)
            | ((unsigned int)input[j + 2] << 16) | ((unsigned int)input[j + 3] << 24);
    }
}

void JCOREMD5Transform(unsigned int *state, unsigned char *block) {
    static const uint32_t constants[64] = {
        0xd76aa478,0xe8c7b756,0x242070db,0xc1bdceee,0xf57c0faf,0x4787c62a,0xa8304613,0xfd469501,
        0x698098d8,0x8b44f7af,0xffff5bb1,0x895cd7be,0x6b901122,0xfd987193,0xa679438e,0x49b40821,
        0xf61e2562,0xc040b340,0x265e5a51,0xe9b6c7aa,0xd62f105d,0x02441453,0xd8a1e681,0xe7d3fbc8,
        0x21e1cde6,0xc33707d6,0xf4d50d87,0x455a14ed,0xa9e3e905,0xfcefa3f8,0x676f02d9,0x8d2a4c8a,
        0xfffa3942,0x8771f681,0x6d9d6122,0xfde5380c,0xa4beea44,0x4bdecfa9,0xf6bb4b60,0xbebfbc70,
        0x289b7ec6,0xeaa127fa,0xd4ef3085,0x04881d05,0xd9d4d039,0xe6db99e5,0x1fa27cf8,0xc4ac5665,
        0xf4292244,0x432aff97,0xab9423a7,0xfc93a039,0x655b59c3,0x8f0ccc92,0xffeff47d,0x85845dd1,
        0x6fa87e4f,0xfe2ce6e0,0xa3014314,0x4e0811a1,0xf7537e82,0xbd3af235,0x2ad7d2bb,0xeb86d391
    };
    static const unsigned char shifts[64] = {
        7,12,17,22,7,12,17,22,7,12,17,22,7,12,17,22,
        5,9,14,20,5,9,14,20,5,9,14,20,5,9,14,20,
        4,11,16,23,4,11,16,23,4,11,16,23,4,11,16,23,
        6,10,15,21,6,10,15,21,6,10,15,21,6,10,15,21
    };
    unsigned int words[16];
    JCOREMD5Decode(words, block, 64);
    uint32_t a = state[0], b = state[1], c = state[2], d = state[3];
    for (unsigned int i = 0; i < 64; i++) {
        uint32_t f;
        unsigned int word;
        if (i < 16) { f = (b & c) | (~b & d); word = i; }
        else if (i < 32) { f = (d & b) | (~d & c); word = (5 * i + 1) % 16; }
        else if (i < 48) { f = b ^ c ^ d; word = (3 * i + 5) % 16; }
        else { f = c ^ (b | ~d); word = (7 * i) % 16; }
        uint32_t value = a + f + constants[i] + words[word];
        a = d; d = c; c = b;
        b += (value << shifts[i]) | (value >> (32 - shifts[i]));
    }
    state[0] += a; state[1] += b; state[2] += c; state[3] += d;
}

void JCOREMD5Update(JCORE_MD5_CTX *context, unsigned char *input, unsigned int inputlen) {
    unsigned int index = (context->count[0] >> 3) & 63;
    unsigned int partlen = 64 - index;
    if ((context->count[0] += inputlen << 3) < (inputlen << 3)) context->count[1]++;
    context->count[1] += inputlen >> 29;
    unsigned int i = 0;
    if (inputlen >= partlen) {
        memmove(context->buffer + index, input, partlen);
        JCOREMD5Transform(context->state, context->buffer);
        for (i = partlen; i + 63 < inputlen; i += 64) JCOREMD5Transform(context->state, input + i);
        index = 0;
    }
    if (inputlen != i) memmove(context->buffer + index, input + i, inputlen - i);
}

void JCOREMD5Final(JCORE_MD5_CTX *context, unsigned char *digest) {
    unsigned char bits[8];
    JCOREMD5Encode(bits, context->count, 8);
    unsigned int index = (context->count[0] >> 3) & 63;
    JCOREMD5Update(context, JCOREPADDING, (index < 56 ? 56 : 120) - index);
    JCOREMD5Update(context, bits, 8);
    JCOREMD5Encode(digest, context->state, 16);
}

void jcore_md5(const void *hexbuf, int read_len, unsigned char *decrypt) {
    JCORE_MD5_CTX context;
    JCOREMD5Init(&context);
    JCOREMD5Update(&context, (unsigned char *)hexbuf, (unsigned int)read_len);
    JCOREMD5Final(&context, decrypt);
}

static NSString *JCOREMD5Hex(const unsigned char digest[16]) {
    NSMutableString *result = [NSMutableString stringWithCapacity:32];
    for (NSUInteger i = 0; i < 16; i++) [result appendFormat:@"%02x", digest[i]];
    return result;
}

@implementation JCOREMD5Utilities
+ (NSString *)stringHashingByMD5:(id)string {
    NSString *value = [string isKindOfClass:NSString.class] ? string : @"";
    unsigned char digest[16];
    // The original hashes the UTF-16 length of the UTF-8 buffer.
    jcore_md5(value.UTF8String, (int)value.length, digest);
    return JCOREMD5Hex(digest);
}
+ (NSData *)stringHashingByMD5Data:(id)string {
    NSString *value = [string isKindOfClass:NSString.class] ? string : @"";
    unsigned char digest[16];
    jcore_md5(value.UTF8String, (int)value.length, digest);
    return [NSData dataWithBytes:digest length:16];
}
+ (NSString *)dataHashingMD5:(NSData *)data {
    unsigned char digest[16];
    jcore_md5(data.bytes, (int)data.length, digest);
    return [JCOREMD5Hex(digest) copy];
}
@end
