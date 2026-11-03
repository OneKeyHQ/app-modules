#pragma once
#import <Foundation/Foundation.h>
#import <CommonCrypto/CommonCryptor.h>
#import <Security/Security.h>
#include <stdint.h>

FOUNDATION_EXPORT CCOptions JCorePadding;

void jcore_sha1_transform(uint32_t *state, const unsigned char *buffer);
void jcore_sha1(const unsigned char *message, size_t message_len, unsigned char *digest);
NSString *JCoreBase64UrlEncodedStringFromBase64String(NSString *base64String);

@interface JCOREDataSecurity : NSObject
+ (NSString *)encryptDataTypeToCurrentStringType:(id)data;
+ (NSString *)decrypt:(id)string aesKey:(NSString *)key;
+ (NSString *)encrypt:(id)string aesKey:(NSString *)key;
- (NSData *)encrypt:(NSData *)data key:(NSData *)key padding:(unsigned int *)padding;
- (NSData *)decrypt:(NSData *)data key:(NSData *)key padding:(unsigned int *)padding;
- (NSData *)doCipher:(NSData *)data key:(NSData *)key context:(unsigned int)context padding:(unsigned int *)padding;
+ (NSData *)AES256EncryptData:(id)data withKey:(NSString *)key;
+ (NSData *)AES256DecryptData:(id)data withKey:(NSString *)key;
+ (NSData *)aes256EncryptData:(id)data withKey:(NSString *)key iv:(const char *)iv;
+ (NSData *)aes256DecryptData:(id)data withKey:(NSString *)key iv:(const char *)iv;
+ (NSData *)aesEncryptData:(id)data withKey:(NSString *)key iv:(const char *)iv;
+ (NSData *)aesDecryptData:(id)data withKey:(NSString *)key iv:(const char *)iv;
+ (NSData *)aes256EncryptData:(id)data withKey:(NSString *)key;
+ (NSData *)aes256CBCEncryptData:(id)data withKey:(NSString *)key;
+ (NSData *)aes256DecryptData:(id)data withKey:(NSString *)key;
+ (NSData *)doCryptWith:(id)data key:(NSString *)key keyLen:(NSUInteger)keyLen operation:(unsigned int)operation options:(unsigned int)options iv:(const void *)iv;
+ (NSData *)doCryptWith:(id)data key:(NSString *)key operation:(unsigned int)operation options:(unsigned int)options iv:(const void *)iv;
+ (NSString *)encryptKeyWithSeed:(unsigned long long)seed;
+ (NSString *)encryptSisKeyWithSeed:(unsigned long long)seed;
+ (NSString *)encryptKeyForHttp;
+ (NSString *)encryptKeyForHttp:(unsigned int *)seed;
+ (NSString *)generateSeedKey:(unsigned long long)seed;
+ (NSString *)sha1:(id)value;
+ (NSString *)hmacSHA256WithKey:(id)key content:(id)content;
+ (NSString *)rsaEncrypt:(id)string publicKey:(id)publicKey;
+ (SecKeyRef)addPublicKey:(NSString *)key;
+ (NSData *)stripPublicKeyHeader:(NSData *)data;
+ (NSData *)encryptData:(NSData *)data withKeyRef:(SecKeyRef)key;
+ (NSData *)encryptDataIOS10Lower:(NSData *)data withKeyRef:(SecKeyRef)key;
+ (NSData *)encryptDataIOS10Higher:(NSData *)data withKeyRef:(SecKeyRef)key;
+ (NSString *)base64:(NSData *)data;
+ (NSData *)dataFromBase64:(id)string;
+ (NSString *)stringFromBase64:(id)string;
+ (NSString *)stringHashingByMD5:(id)string;
+ (NSData *)stringHashingByMD5Data:(id)string;
+ (NSString *)dataHashingMD5:(NSData *)data;
+ (NSData *)sm4ECBEncryptData:(NSData *)data withKey:(NSString *)key;
+ (NSData *)sm4CBCEncryptData:(NSData *)data withKey:(NSString *)key iv:(const char *)iv;
+ (NSData *)sm4ECBDecryptData:(NSData *)data withKey:(NSString *)key;
+ (NSData *)sm4CBCDecryptData:(NSData *)data withKey:(NSString *)key iv:(const char *)iv;
@end
