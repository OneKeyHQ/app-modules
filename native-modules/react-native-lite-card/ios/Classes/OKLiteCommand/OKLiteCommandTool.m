//
//  OKLiteCommandTool.m
//  OneKeyWallet
//
//  Created by linleiqin on 2023/6/27.
//

#import "OKLiteCommandTool.h"
#import "OKNFCBridge.h"
#import "OKNFCUtility.h"
#import "LCLogger.h"

static NSString *OKLiteCommandName(OKLiteCommand command) {
    switch (command) {
        case OKLiteCommandSelectSecure: return @"select_secure";
        case OKLiteCommandSelectBackup: return @"select_backup";
        case OKLiteCommandGetCardSN: return @"get_serial";
        case OKLiteCommandGetCardCert: return @"get_certificate";
        case OKLiteCommandGetBackupStatus: return @"get_backup_status";
        case OKLiteCommandGetPINStatus: return @"get_pin_status";
        case OKLiteCommandSetPIN: return @"set_pin";
        case OKLiteCommandChangePIN: return @"change_pin";
        case OKLiteCommandVerifyPIN: return @"verify_pin";
        case OKLiteCommandImportMnemonic: return @"backup_data";
        case OKLiteCommandExportMnemonic: return @"export_data";
        case OKLiteCommandWipeCard: return @"reset_card";
        case OKLiteCommandPinRTL: return @"get_pin_retry_count";
        case OKLiteCommandOpenChannel_1: return @"verify_certificate";
        case OKLiteCommandOpenChannel_2: return @"verify_auth_data";
    }
    return @"unknown";
}

@implementation OKLiteCommandTool

- (void)sendCommandWithAPDU:(NFCISO7816APDU *)apdu
                  modal:(OKLiteCommandModal *)modal
      completionHandler:(void(^)(NSData *responseData, uint8_t sw1, uint8_t sw2, NSError * _Nullable error, NSString *parseRespon))completionHandler {

    static NSUInteger nextCommandID = 0;
    NSUInteger commandID;
    @synchronized (OKLiteCommandTool.class) { commandID = ++nextCommandID; }
    NSString *command = OKLiteCommandName(modal.command);
    [LCLogger info:[NSString stringWithFormat:@"commandID=%lu command=%@ stage=send buildSucceeded=%d parseRequested=%d", (unsigned long)commandID, command, apdu != nil, modal.parseResp]];
    id<NFCISO7816Tag> tag = [self.delegate getNFCsessionTag];
    [tag sendCommandAPDU:apdu completionHandler:^(NSData *responseData, uint8_t sw1, uint8_t sw2, NSError *error) {
        uint8_t rawSW1 = sw1;
        uint8_t rawSW2 = sw2;
        NSString *parseRespon = nil;
        if (modal.parseResp) {
            parseRespon = [OKNFCBridge parseSafeAPDUResponse:responseData sw1:sw1 sw2:sw2];
            if (sw1 == OKNFC_SW1_OK && parseRespon == nil) {
                sw1 = 0x00;
            }
        }

        BOOL coreNFCError = [error.domain isEqualToString:NFCErrorDomain];
        [LCLogger info:[NSString stringWithFormat:@"commandID=%lu command=%@ stage=response rawSW1=%u rawSW2=%u effectiveSW1=%u effectiveSW2=%u responseDataBytes=%lu parseRequested=%d parsePresent=%d transportError=%d coreNFCError=%d errorCode=%ld", (unsigned long)commandID, command, rawSW1, rawSW2, sw1, sw2, (unsigned long)responseData.length, modal.parseResp, parseRespon != nil, error != nil, coreNFCError, (long)error.code]];
        if (modal.command == OKLiteCommandGetBackupStatus || modal.command == OKLiteCommandGetPINStatus) {
            [LCLogger info:[NSString stringWithFormat:@"commandID=%lu command=%@ stage=state_read readSucceeded=%d", (unsigned long)commandID, command, error == nil && sw1 == OKNFC_SW1_OK && responseData.length > 0]];
        }
        [OKNFCUtility logAPDU:modal.desc response:responseData sw1:sw1 sw2:sw2 error:error];
        if (completionHandler) {
            completionHandler(responseData,sw1,sw2,error,parseRespon);
        }
    }];
}

//
//+ (OKNFCLiteApp)getLiteAppWithCommand:(OKLiteCommand)command version:(OKNFCLiteVersion)version {
//    switch (command) {
//        case OKLiteCommandGetCardSN:
//        case OKLiteCommandGetPINStatus:
//        case OKLiteCommandPinRTL:
//        case OKLiteCommandSetPIN:
//        case OKLiteCommandChangePIN:
//        case OKLiteCommandWipeCard:
//            return version == OKNFCLiteVersionV1 ? OKNFCLiteAppSecure : OKNFCLiteAppBackup;
//        case OKLiteCommandGetBackupStatus:
//        case OKLiteCommandVerifyPIN:
//        case OKLiteCommandImportMnemonic:
//        case OKLiteCommandExportMnemonic:
//            return OKNFCLiteAppBackup;
//        default:
//            return OKNFCLiteAppNONE;
//    }
//}


@end
