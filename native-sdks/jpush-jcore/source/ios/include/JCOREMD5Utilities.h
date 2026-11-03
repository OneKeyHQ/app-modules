#pragma once
#import <Foundation/Foundation.h>

typedef struct {
    unsigned int count[2];
    unsigned int state[4];
    unsigned char buffer[64];
} JCORE_MD5_CTX;

extern unsigned char JCOREPADDING[64];
void JCOREMD5Init(JCORE_MD5_CTX *context);
void JCOREMD5Update(JCORE_MD5_CTX *context, unsigned char *input, unsigned int inputlen);
void JCOREMD5Final(JCORE_MD5_CTX *context, unsigned char *digest);
void JCOREMD5Transform(unsigned int *state, unsigned char *block);
void JCOREMD5Encode(unsigned char *output, unsigned int *input, unsigned int len);
void JCOREMD5Decode(unsigned int *output, unsigned char *input, unsigned int len);
void jcore_md5(const void *hexbuf, int read_len, unsigned char *decrypt);

@interface JCOREMD5Utilities : NSObject
+ (NSString *)stringHashingByMD5:(id)string;
+ (NSData *)stringHashingByMD5Data:(id)string;
+ (NSString *)dataHashingMD5:(NSData *)data;
@end
