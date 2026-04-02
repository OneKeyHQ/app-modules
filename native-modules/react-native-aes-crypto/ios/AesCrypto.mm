#import "AesCrypto.h"
#import <CommonCrypto/CommonCryptor.h>
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonHMAC.h>
#import <CommonCrypto/CommonKeyDerivation.h>
#import <Security/Security.h>

// ---------------------------------------------------------------------------
// MARK: - Internal helpers
// ---------------------------------------------------------------------------

static NSString *toHex(NSData *data) {
    const unsigned char *bytes = (const unsigned char *)data.bytes;
    NSMutableString *hex = [NSMutableString new];
    for (NSUInteger i = 0; i < data.length; i++) {
        [hex appendFormat:@"%02x", bytes[i]];
    }
    return [hex copy];
}

static NSData *fromHex(NSString *string) {
    NSMutableData *data = [[NSMutableData alloc] init];
    unsigned char whole_byte;
    char byte_chars[3] = {'\0', '\0', '\0'};
    NSUInteger len = string.length / 2;
    for (NSUInteger i = 0; i < len; i++) {
        byte_chars[0] = (char)[string characterAtIndex:i * 2];
        byte_chars[1] = (char)[string characterAtIndex:i * 2 + 1];
        whole_byte = (unsigned char)strtol(byte_chars, NULL, 16);
        [data appendBytes:&whole_byte length:1];
    }
    return data;
}

static NSData *aesCBC(NSString *operation, NSData *inputData, NSString *key, NSString *iv, NSString *algorithm) {
    NSData *keyData = fromHex(key);
    NSData *ivData  = fromHex(iv);

    NSArray *algorithms = @[@"aes-128-cbc", @"aes-192-cbc", @"aes-256-cbc"];
    NSUInteger item = [algorithms indexOfObject:algorithm];
    size_t keyLength;
    switch (item) {
        case 0:  keyLength = kCCKeySizeAES128; break;
        case 1:  keyLength = kCCKeySizeAES192; break;
        default: keyLength = kCCKeySizeAES256; break;
    }

    NSMutableData *buffer = [[NSMutableData alloc] initWithLength:inputData.length + kCCBlockSizeAES128];
    size_t numBytes = 0;

    CCCryptorStatus status = CCCrypt(
        [operation isEqualToString:@"encrypt"] ? kCCEncrypt : kCCDecrypt,
        kCCAlgorithmAES,
        kCCOptionPKCS7Padding,
        keyData.bytes, keyLength,
        ivData.length ? ivData.bytes : nil,
        inputData.bytes, inputData.length,
        buffer.mutableBytes, buffer.length,
        &numBytes);

    if (status == kCCSuccess) {
        [buffer setLength:numBytes];
        return buffer;
    }
    return nil;
}

static NSData *aesCTR(NSString *operation, NSData *inputData, NSString *key, NSString *iv, NSString *algorithm) {
    NSData *keyData = fromHex(key);
    NSData *ivData  = fromHex(iv);

    NSArray *algorithms = @[@"aes-128-ctr", @"aes-192-ctr", @"aes-256-ctr"];
    NSUInteger item = [algorithms indexOfObject:algorithm];
    size_t keyLength;
    switch (item) {
        case 0:  keyLength = kCCKeySizeAES128; break;
        case 1:  keyLength = kCCKeySizeAES192; break;
        default: keyLength = kCCKeySizeAES256; break;
    }

    NSMutableData *buffer = [[NSMutableData alloc] initWithLength:inputData.length + kCCBlockSizeAES128];
    size_t numBytes = 0;
    CCCryptorRef cryptor = NULL;

    CCCryptorStatus status = CCCryptorCreateWithMode(
        [operation isEqualToString:@"encrypt"] ? kCCEncrypt : kCCDecrypt,
        kCCModeCTR,
        kCCAlgorithmAES,
        ccPKCS7Padding,
        ivData.length ? ivData.bytes : nil,
        keyData.bytes,
        keyLength,
        NULL, 0, 0,
        kCCModeOptionCTR_BE,
        &cryptor);

    if (status != kCCSuccess) {
        return nil;
    }

    CCCryptorStatus updateStatus = CCCryptorUpdate(
        cryptor,
        inputData.bytes, inputData.length,
        buffer.mutableBytes, buffer.length,
        &numBytes);

    if (updateStatus != kCCSuccess) {
        CCCryptorRelease(cryptor);
        return nil;
    }
    buffer.length = numBytes;

    size_t finalBytes = 0;
    CCCryptorStatus finalStatus = CCCryptorFinal(
        cryptor,
        buffer.mutableBytes, buffer.length,
        &finalBytes);

    CCCryptorRelease(cryptor);

    if (finalStatus != kCCSuccess) {
        return nil;
    }
    return buffer;
}

// ---------------------------------------------------------------------------
// MARK: - TurboModule implementation
// ---------------------------------------------------------------------------

@implementation AesCrypto

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeAesCryptoSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"AesCrypto";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

// MARK: - encrypt

- (void)encrypt:(NSString *)data
            key:(NSString *)key
             iv:(NSString *)iv
      algorithm:(NSString *)algorithm
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *inputData = fromHex(data);
            NSData *result = nil;
            if ([algorithm containsString:@"ctr"]) {
                result = aesCTR(@"encrypt", inputData, key, iv, algorithm);
            } else {
                result = aesCBC(@"encrypt", inputData, key, iv, algorithm);
            }
            if (result == nil) {
                reject(@"encrypt_fail", @"Encrypt error", nil);
            } else {
                resolve(toHex(result));
            }
        } @catch (NSException *exception) {
            reject(@"encrypt_fail", exception.reason, nil);
        }
    });
}

// MARK: - decrypt

- (void)decrypt:(NSString *)base64
            key:(NSString *)key
             iv:(NSString *)iv
      algorithm:(NSString *)algorithm
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *inputData = fromHex(base64);
            NSData *result = nil;
            if ([algorithm containsString:@"ctr"]) {
                result = aesCTR(@"decrypt", inputData, key, iv, algorithm);
            } else {
                result = aesCBC(@"decrypt", inputData, key, iv, algorithm);
            }
            if (result == nil) {
                reject(@"decrypt_fail", @"Decrypt failed", nil);
            } else {
                resolve(toHex(result));
            }
        } @catch (NSException *exception) {
            reject(@"decrypt_fail", exception.reason, nil);
        }
    });
}

// MARK: - pbkdf2

- (void)pbkdf2:(NSString *)password
           salt:(NSString *)salt
           cost:(double)cost
         length:(double)length
      algorithm:(NSString *)algorithm
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *passwordData = fromHex(password);
            NSData *saltData = fromHex(salt);
            // length is in bits, convert to bytes
            NSUInteger keyLengthBytes = (NSUInteger)(length / 8);
            NSMutableData *hashKeyData = [NSMutableData dataWithLength:keyLengthBytes];

            CCPseudoRandomAlgorithm prf = kCCPRFHmacAlgSHA512;
            NSString *algoLower = [algorithm lowercaseString];
            if ([algoLower isEqualToString:@"sha1"]) {
                prf = kCCPRFHmacAlgSHA1;
            } else if ([algoLower isEqualToString:@"sha256"]) {
                prf = kCCPRFHmacAlgSHA256;
            } else if ([algoLower isEqualToString:@"sha512"]) {
                prf = kCCPRFHmacAlgSHA512;
            }

            int status = CCKeyDerivationPBKDF(
                kCCPBKDF2,
                passwordData.bytes, passwordData.length,
                saltData.bytes, saltData.length,
                prf,
                (unsigned int)cost,
                hashKeyData.mutableBytes, hashKeyData.length);

            if (status == kCCParamError) {
                reject(@"keygen_fail", @"Key derivation parameter error", nil);
            } else {
                resolve(toHex(hashKeyData));
            }
        } @catch (NSException *exception) {
            reject(@"keygen_fail", exception.reason, nil);
        }
    });
}

// MARK: - hmac256

- (void)hmac256:(NSString *)base64
            key:(NSString *)key
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *keyData   = fromHex(key);
            NSData *inputData = fromHex(base64);
            void *buffer = malloc(CC_SHA256_DIGEST_LENGTH);
            if (!buffer) {
                reject(@"hmac_fail", @"Memory allocation error", nil);
                return;
            }
            CCHmac(kCCHmacAlgSHA256,
                   keyData.bytes, keyData.length,
                   inputData.bytes, inputData.length,
                   buffer);
            NSData *result = [NSData dataWithBytesNoCopy:buffer
                                                  length:CC_SHA256_DIGEST_LENGTH
                                            freeWhenDone:YES];
            resolve(toHex(result));
        } @catch (NSException *exception) {
            reject(@"hmac_fail", exception.reason, nil);
        }
    });
}

// MARK: - hmac512

- (void)hmac512:(NSString *)base64
            key:(NSString *)key
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *keyData   = fromHex(key);
            NSData *inputData = fromHex(base64);
            void *buffer = malloc(CC_SHA512_DIGEST_LENGTH);
            if (!buffer) {
                reject(@"hmac_fail", @"Memory allocation error", nil);
                return;
            }
            CCHmac(kCCHmacAlgSHA512,
                   keyData.bytes, keyData.length,
                   inputData.bytes, inputData.length,
                   buffer);
            NSData *result = [NSData dataWithBytesNoCopy:buffer
                                                  length:CC_SHA512_DIGEST_LENGTH
                                            freeWhenDone:YES];
            resolve(toHex(result));
        } @catch (NSException *exception) {
            reject(@"hmac_fail", exception.reason, nil);
        }
    });
}

// MARK: - sha1

- (void)sha1:(NSString *)text
     resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *inputData = fromHex(text);
            NSMutableData *result = [[NSMutableData alloc] initWithLength:CC_SHA1_DIGEST_LENGTH];
            CC_SHA1(inputData.bytes, (CC_LONG)inputData.length, result.mutableBytes);
            resolve(toHex(result));
        } @catch (NSException *exception) {
            reject(@"sha1_fail", exception.reason, nil);
        }
    });
}

// MARK: - sha256

- (void)sha256:(NSString *)text
       resolve:(RCTPromiseResolveBlock)resolve
        reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *inputData = fromHex(text);
            unsigned char *buffer = (unsigned char *)malloc(CC_SHA256_DIGEST_LENGTH);
            if (!buffer) {
                reject(@"sha256_fail", @"Memory allocation error", nil);
                return;
            }
            CC_SHA256(inputData.bytes, (CC_LONG)inputData.length, buffer);
            NSData *result = [NSData dataWithBytesNoCopy:buffer
                                                  length:CC_SHA256_DIGEST_LENGTH
                                            freeWhenDone:YES];
            resolve(toHex(result));
        } @catch (NSException *exception) {
            reject(@"sha256_fail", exception.reason, nil);
        }
    });
}

// MARK: - sha512

- (void)sha512:(NSString *)text
       resolve:(RCTPromiseResolveBlock)resolve
        reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *inputData = fromHex(text);
            unsigned char *buffer = (unsigned char *)malloc(CC_SHA512_DIGEST_LENGTH);
            if (!buffer) {
                reject(@"sha512_fail", @"Memory allocation error", nil);
                return;
            }
            CC_SHA512(inputData.bytes, (CC_LONG)inputData.length, buffer);
            NSData *result = [NSData dataWithBytesNoCopy:buffer
                                                  length:CC_SHA512_DIGEST_LENGTH
                                            freeWhenDone:YES];
            resolve(toHex(result));
        } @catch (NSException *exception) {
            reject(@"sha512_fail", exception.reason, nil);
        }
    });
}

// MARK: - randomUuid

- (void)randomUuid:(RCTPromiseResolveBlock)resolve
            reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSString *uuid = [[NSUUID UUID] UUIDString];
            resolve(uuid);
        } @catch (NSException *exception) {
            reject(@"uuid_fail", exception.reason, nil);
        }
    });
}

// MARK: - randomKey

- (void)randomKey:(double)length
          resolve:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSUInteger len = (NSUInteger)length;
            NSMutableData *data = [NSMutableData dataWithLength:len];
            int result = SecRandomCopyBytes(kSecRandomDefault, len, data.mutableBytes);
            if (result != errSecSuccess) {
                reject(@"random_fail", @"Random key generation error", nil);
            } else {
                resolve(toHex(data));
            }
        } @catch (NSException *exception) {
            reject(@"random_fail", exception.reason, nil);
        }
    });
}

@end
