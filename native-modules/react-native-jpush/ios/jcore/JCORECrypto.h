#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JCORECrypto : NSObject

+ (NSString *)generateSeedKey:(uint64_t)seed;
+ (NSString *)seedKeyForSeed:(uint64_t)seed;
+ (NSString *)httpSeedKeyWithOutSeed:(uint32_t *_Nullable)outSeed;

+ (nullable NSData *)aesCbcEncrypt:(NSData *)data
                               key:(NSString *)key
                                iv:(nullable const void *)iv
                           keySize:(size_t)keySize;

+ (nullable NSData *)aesCbcDecrypt:(NSData *)data
                               key:(NSString *)key
                                iv:(nullable const void *)iv
                           keySize:(size_t)keySize;

+ (nullable NSData *)aesEcbEncrypt:(NSData *)data
                               key:(NSString *)key
                           keySize:(size_t)keySize;

+ (nullable NSData *)aesEcbDecrypt:(NSData *)data
                               key:(NSString *)key
                           keySize:(size_t)keySize;

+ (nullable NSData *)aes256CBCEncryptWithFixedHttpIV:(NSData *)data
                                                 key:(NSString *)key;

+ (nullable NSData *)aes256CBCDecryptWithFixedHttpIV:(NSData *)data
                                                 key:(NSString *)key;

+ (nullable NSData *)aes128CBCEncryptData:(NSData *)data
                                    key16:(NSString *)key16
                                     iv16:(NSString *)iv16;

+ (nullable NSData *)aes128CBCDecryptData:(NSData *)data
                                    key16:(NSString *)key16
                                     iv16:(NSString *)iv16;

+ (nullable NSData *)sm4CBCEncryptData:(NSData *)data
                               withKey:(NSString *)hexKey
                                    iv:(const char *)iv;

+ (nullable NSData *)sm4CBCDecryptData:(NSData *)data
                               withKey:(NSString *)hexKey
                                    iv:(const char *)iv;

+ (nullable NSData *)sm4ECBEncryptData:(NSData *)data
                               withKey:(NSString *)hexKey;

+ (nullable NSData *)sm4ECBDecryptData:(NSData *)data
                               withKey:(NSString *)hexKey;

+ (nullable NSString *)rsaEncryptString:(NSString *)plainText
                        publicKeyBase64:(NSString *)publicKeyBase64;

+ (nullable NSData *)gzipCompress:(NSData *)data;
+ (nullable NSData *)gzipDecompress:(NSData *)data;

+ (NSString *)md5Hex:(NSString *)input;
+ (NSString *)md5HexForData:(NSData *)data;
+ (NSData *)sha1Data:(NSData *)data;
+ (NSString *)sha1Hex:(NSData *)data;
+ (NSString *)sha1HexForString:(NSString *)input;
+ (nullable NSString *)hmacSHA256HexWithKey:(NSString *)key content:(NSString *)content;
+ (nullable NSString *)hmacSha256Hex:(NSString *)content key:(NSString *)key;

@end

NS_ASSUME_NONNULL_END
