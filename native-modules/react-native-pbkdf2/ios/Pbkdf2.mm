#import "Pbkdf2.h"
#import <CommonCrypto/CommonKeyDerivation.h>

@implementation Pbkdf2

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativePbkdf2SpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"Pbkdf2";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

// MARK: - derive

- (void)derive:(NSString *)password
          salt:(NSString *)salt
        rounds:(double)rounds
     keyLength:(double)keyLength
          hash:(NSString *)hash
       resolve:(RCTPromiseResolveBlock)resolve
        reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSData *passwordData = [[NSData alloc] initWithBase64EncodedString:password options:0];
            NSData *saltData     = [[NSData alloc] initWithBase64EncodedString:salt options:0];

            NSUInteger keyLengthBytes = (NSUInteger)keyLength;
            NSMutableData *derivedKey = [NSMutableData dataWithLength:keyLengthBytes];

            CCPseudoRandomAlgorithm prf = kCCPRFHmacAlgSHA1;
            if ([hash isEqualToString:@"sha-512"]) {
                prf = kCCPRFHmacAlgSHA512;
            } else if ([hash isEqualToString:@"sha-256"]) {
                prf = kCCPRFHmacAlgSHA256;
            }

            CCKeyDerivationPBKDF(
                kCCPBKDF2,
                passwordData.bytes, passwordData.length,
                saltData.bytes, saltData.length,
                prf,
                (unsigned int)rounds,
                derivedKey.mutableBytes, derivedKey.length);

            resolve([derivedKey base64EncodedStringWithOptions:0]);
        } @catch (NSException *exception) {
            reject(@"derive_fail", exception.reason, nil);
        }
    });
}

@end
