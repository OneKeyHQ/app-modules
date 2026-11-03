#pragma once
#import <Foundation/Foundation.h>

typedef struct {
    int mode;
    unsigned long sk[32];
} JCORESm4Context;
void JCORESm4CryptECB(JCORESm4Context *ctx, int mode, int length, unsigned char *input, unsigned char *output);
void JCORESm4CryptCBC(JCORESm4Context *ctx, int mode, int length, unsigned char *iv, unsigned char *input, unsigned char *output);

@interface JCORESm4Crypt : NSObject
+ (NSData *)ecbEncrypt:(NSData *)data withKey:(NSString *)key;
+ (NSData *)ecbDecrypt:(NSData *)data withKey:(NSString *)key;
+ (NSData *)cbcEncrypt:(NSData *)data withKey:(NSString *)key iv:(const char *)iv;
+ (NSData *)cbcDecrypt:(NSData *)data withKey:(NSString *)key iv:(const char *)iv;
+ (void)convertString:(NSString *)string toChars:(char *)chars;
@end
