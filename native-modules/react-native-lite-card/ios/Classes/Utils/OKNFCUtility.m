//
//  OKNFCUtility.m
//  OKNFC
//
//  Created by zj on 2021/5/1.
//

#import "OKNFCUtility.h"
@import ReactNativeNativeLogger;

@implementation OKNFCUtility

+ (void)logAPDU:(NSString *)name response:(NSData *)responseData sw1:(uint8_t)sw1 sw2:(uint8_t)sw2 error:(NSError *)error {
    BOOL ok = sw1 == OKNFC_SW1_OK;
    NSString *msg = [NSString stringWithFormat:@"APDU %@: %@ sw1:%d(%x) sw2:%d(%x)", name, ok ? @"OK" : @"FAIL", sw1, sw1, sw2, sw2];
    if (ok) {
        [OneKeyLog debug:@"LiteCard" :msg];
    } else {
        [OneKeyLog error:@"LiteCard" :msg];
    }
}

@end
