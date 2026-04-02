#import <Pbkdf2Spec/Pbkdf2Spec.h>

@interface Pbkdf2 : NativePbkdf2SpecBase <NativePbkdf2Spec>

- (void)derive:(NSString *)password
          salt:(NSString *)salt
        rounds:(double)rounds
     keyLength:(double)keyLength
          hash:(NSString *)hash
       resolve:(RCTPromiseResolveBlock)resolve
        reject:(RCTPromiseRejectBlock)reject;

@end
