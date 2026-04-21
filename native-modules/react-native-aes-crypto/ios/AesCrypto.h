#import <AesCryptoSpec/AesCryptoSpec.h>

@interface AesCrypto : NativeAesCryptoSpecBase <NativeAesCryptoSpec>

- (void)encrypt:(NSString *)data
            key:(NSString *)key
             iv:(NSString *)iv
      algorithm:(NSString *)algorithm
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject;

- (void)decrypt:(NSString *)base64
            key:(NSString *)key
             iv:(NSString *)iv
      algorithm:(NSString *)algorithm
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject;

- (void)pbkdf2:(NSString *)password
           salt:(NSString *)salt
           cost:(double)cost
         length:(double)length
      algorithm:(NSString *)algorithm
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject;

- (void)hmac256:(NSString *)base64
            key:(NSString *)key
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject;

- (void)hmac512:(NSString *)base64
            key:(NSString *)key
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject;

- (void)sha1:(NSString *)text
     resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject;

- (void)sha256:(NSString *)text
       resolve:(RCTPromiseResolveBlock)resolve
        reject:(RCTPromiseRejectBlock)reject;

- (void)sha512:(NSString *)text
       resolve:(RCTPromiseResolveBlock)resolve
        reject:(RCTPromiseRejectBlock)reject;

- (void)randomUuid:(RCTPromiseResolveBlock)resolve
            reject:(RCTPromiseRejectBlock)reject;

- (void)randomKey:(double)length
          resolve:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject;

@end
