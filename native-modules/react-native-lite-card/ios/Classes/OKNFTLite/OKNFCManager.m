//
//  OKNFCManager.m
//  OneKeyWallet
//
//  Created by linleiqin on 2023/6/27.
//

#import "OKNFCManager.h"
#import "OKNFCBridge.h"
#import "OKNFCUtility.h"
#import <CoreNFC/CoreNFC.h>
#import "LCLogger.h"
//#import "OKNFCHintViewController.h"
//#import "OKMnemonic.h"
#import "NSString+OKAdd.h"
#import "OKTools.h"
#import "OKLiteV1.h"
#import "OKLiteV2.h"
#import "OKLiteProtocol.h"
#import "OKLiteCommandModal.h"



#define kGetLiteInfoBlock    @"kGetLiteInfoBlock"
#define kSetMnemonicBlock    @"kSetMnemonicBlock"
#define kGetMnemonicBlock    @"kGetMnemonicBlock"
#define kChangePinBlock      @"kChangePinBlock"
#define kResetBlock          @"kResetBlock"




@interface OKNFCManager() <NFCTagReaderSessionDelegate,OKNFCManagerDelegate>
@property (nonatomic, strong) NFCTagReaderSession *session;
@property (nonatomic, copy) NSString *pin;
@property (nonatomic, copy) NSString *neoPin;
@property (nonatomic, copy) NSString *exportMnemonic;
@property (nonatomic, assign) BOOL certVerified;
@property (nonatomic, assign) OKNFCLiteApp selectNFCApp;
@property (nonatomic, strong) OKLiteV1 *lite;

@property (nonatomic, strong) NSMutableDictionary *completionBlocks;
// CoreNFC reports the app's own invalidateSession with the same code as a user
// cancel, so the invalidation callback has to know who ended the session.
@property (atomic, assign) BOOL sessionEndedByApp;

@end

@implementation OKNFCManager

#pragma mark - OKNFCManagerDelegate

- (id<NFCISO7816Tag>)getNFCsessionTag {
    id<NFCISO7816Tag> tag = [self.session.connectedTag asNFCISO7816Tag];
    return tag;
}

- (OKNFCLiteSessionType)getSessionType {
    return self.sessionType;
}


- (void)endNFCSessionWithError:(BOOL)isError {
    self.sessionEndedByApp = YES;
    self.session.alertMessage = @"";
    if (isError) {
        [self.session invalidateSessionWithErrorMessage:OKTools.isChineseLan ? @"读取失败，请重试":@"Connect fail, please try again."];
    } else {
        [self.session invalidateSession];
    }
    self.session = nil;
}


-(NSMutableDictionary *)completionBlocks {
    if (!_completionBlocks) {
        _completionBlocks = [NSMutableDictionary dictionary];
    }
    return _completionBlocks;
}

// The card operation and the session invalidation run on different threads of
// the delegate queue and can both try to finish the same request, e.g. when the
// user cancels while the card is being read. Only the first caller gets the
// completion: React Native aborts the app when a callback runs twice.
- (id)takeCompletionForKey:(NSString *)key {
    id completion = nil;
    @synchronized (self) {
        completion = [_completionBlocks objectForKey:key];
        [_completionBlocks removeObjectForKey:key];
    }
    if (!completion) {
        [LCLogger debug:[NSString stringWithFormat:@"%@ already finished, dropping the late result", key]];
    }
    return completion;
}

// Finishes the pending request when the session ends before the card operation
// reports its own result: as a cancel when the user or system ended the session,
// otherwise as a connection failure.
- (void)finishPendingRequestAsCancel:(BOOL)asCancel sessionError:(NSError *)sessionError {
    switch (self.sessionType) {
        case OKNFCLiteSessionTypeGetInfo:
        case OKNFCLiteSessionTypeUpdateInfo:{
            GetLiteInfoCallback callback = [self takeCompletionForKey:kGetLiteInfoBlock];
            if (callback) {
                callback(nil, OKNFCLiteStatusError);
            }
        } break;
        case OKNFCLiteSessionTypeReset: {
            ResetCallback callback = [self takeCompletionForKey:kResetBlock];
            if (callback) {
                callback(self.lite, NO, sessionError);
            }
        } break;
        case OKNFCLiteSessionTypeSetMnemonic:
        case OKNFCLiteSessionTypeSetMnemonicForce: {
            SetMnemonicCallback callback = [self takeCompletionForKey:kSetMnemonicBlock];
            if (callback) {
                callback(self.lite, asCancel ? OKNFCLiteSetMncStatusCancel : OKNFCLiteSetMncStatusError);
            }
        } break;
        case OKNFCLiteSessionTypeGetMnemonic: {
            GetMnemonicCallback callback = [self takeCompletionForKey:kGetMnemonicBlock];
            if (callback) {
                callback(self.lite, nil, asCancel ? OKNFCLiteGetMncStatusCancel : OKNFCLiteGetMncStatusError);
            }
        } break;
        case OKNFCLiteSessionTypeChangePin: {
            ChangePinCallback callback = [self takeCompletionForKey:kChangePinBlock];
            if (callback) {
                callback(self.lite, asCancel ? OKNFCLiteChangePinStatusCancel : OKNFCLiteChangePinStatusError);
            }
        } break;
        default:
            break;
    }
}

- (void)beginNewNFCSession {
    self.sessionEndedByApp = NO;
    self.session = [[NFCTagReaderSession alloc] initWithPollingOption:NFCPollingISO14443 delegate:self queue:dispatch_get_global_queue(2, 0)];
    [self.session beginSession];
}

#pragma mark - NFCTagReaderSessionDelegate

- (void)tagReaderSession:(NFCTagReaderSession *)session didDetectTags:(NSArray<__kindof id<NFCTag>> *)tags {
    [LCLogger debug:@"tagReaderSession didDetectTags"];

    id<NFCISO7816Tag> tag = [tags.firstObject asNFCISO7816Tag];
    if (!tag) { return; }

    [session connectToTag:tag completionHandler:^(NSError * _Nullable error) {
        if (error) {
            NSString *errMsg = [NSString stringWithFormat:@"connectToTag error: %@", error.localizedDescription];
            [LCLogger error:errMsg];
            //            [kTools debugTipMessage:errMsg];
            [self endNFCSessionWithError:YES];
            [self finishPendingRequestAsCancel:NO sessionError:nil];
            return;
        }
        [self nfcSessionComplete:session];
    }];
}

- (void)tagReaderSession:(NFCTagReaderSession *)session didInvalidateWithError:(NSError *)error {
    [LCLogger debug:[NSString stringWithFormat:@"tagReaderSession didInvalidateWithError: %@", error.localizedDescription]];
    // When the app ended the session, the card operation that ended it reports
    // the real result; treating this as a cancel would race with it.
    if ((error.code == 200 || error.code == 6) && !self.sessionEndedByApp) {
        [self finishPendingRequestAsCancel:YES sessionError:error];
    }
    [session invalidateSession];
}

- (void)tagReaderSessionDidBecomeActive:(NFCTagReaderSession *)session {
    [LCLogger debug:@"tagReaderSessionDidBecomeActive"];
}

#pragma mark - Tasks

- (void)nfcSessionComplete:(NFCTagReaderSession *)session {
    if (![self checkLiteVersion]) {
        [self endNFCSessionWithError:YES];
        [self finishPendingRequestAsCancel:NO sessionError:nil];
        self.sessionType = OKNFCLiteSessionTypeNone;
        return;
    }
    self.selectNFCApp = OKNFCLiteAppNONE;
    switch (self.sessionType) {
        case OKNFCLiteSessionTypeGetInfo:
        case OKNFCLiteSessionTypeUpdateInfo:{
            [self _getLiteInfo];
        } break;
        case OKNFCLiteSessionTypeReset: {
            [self _reset];
        } break;
        case OKNFCLiteSessionTypeSetMnemonic: {
            [self _setMnemonic:NO];
        } break;
        case OKNFCLiteSessionTypeSetMnemonicForce: {
            [self _setMnemonic:YES];
        } break;
        case OKNFCLiteSessionTypeGetMnemonic: {
            [self _getMnemonic];
        } break;
        case OKNFCLiteSessionTypeChangePin: {
            [self _changePin];
        } break;
        default:
            break;
    }
    self.sessionType = OKNFCLiteSessionTypeNone;
}


#pragma mark - getLiteInfo

- (void)getLiteInfo:(GetLiteInfoCallback)callBack {
    if (self.lite.SN.length > 0) {
        self.sessionType = OKNFCLiteSessionTypeUpdateInfo;
    } else {
        self.sessionType = OKNFCLiteSessionTypeGetInfo;
    }
    [self.completionBlocks setObject:callBack forKey:kGetLiteInfoBlock];
    [self beginNewNFCSession];
}


- (void)getLiteInfo {
    if (self.lite.SN.length > 0) {
        self.sessionType = OKNFCLiteSessionTypeUpdateInfo;
    } else {
        self.sessionType = OKNFCLiteSessionTypeGetInfo;
    }
    [self beginNewNFCSession];
}

- (void)_getLiteInfo {
    __weak typeof(self) weakSelf = self;
    [self.lite getLiteInfo:^(OKLiteV1 *lite, OKNFCLiteStatus status) {
        GetLiteInfoCallback callback = [weakSelf takeCompletionForKey:kGetLiteInfoBlock];
        if (callback) {
            callback(lite, status);
        }
    }];
}

- (BOOL)syncLiteInfo {
    return [self.lite syncLiteInfo];
}

#pragma mark - setMnemonic

- (void)setMnemonic:(NSString *)mnemonic
            withPin:(NSString *)pin
          overwrite:(BOOL)overwrite
           complete:(SetMnemonicCallback)complete {

    if (pin.length != OKNFC_PIN_LENGTH) {
        return;
    }
    self.pin = pin;
    self.exportMnemonic = mnemonic;
    if(overwrite) {
        // 写入强制覆盖
        self.sessionType = OKNFCLiteSessionTypeSetMnemonicForce;
    } else {
        self.sessionType = OKNFCLiteSessionTypeSetMnemonic;
    }
    [self.completionBlocks setObject:complete forKey:kSetMnemonicBlock];
    [self beginNewNFCSession];

}

- (void)_setMnemonic:(BOOL)force {
    NSString *mnemonic = self.exportMnemonic;
    NSString *pin = self.pin;
    // Clear sensitive data from properties immediately
    self.exportMnemonic = nil;
    self.pin = nil;
    __weak typeof(self) weakSelf = self;
    [self.lite setMnemonic:mnemonic withPin:pin overwrite:force complete:^(OKLiteV1 *lite, OKNFCLiteSetMncStatus status) {
        SetMnemonicCallback callback = [weakSelf takeCompletionForKey:kSetMnemonicBlock];
        if (callback) {
            callback(lite, status);
        }
    }];
}

#pragma mark - getMnemonic

- (void)getMnemonicWithPin:(NSString *)pin complete:(GetMnemonicCallback)complete {
    if (pin.length != OKNFC_PIN_LENGTH) {
        return;
    }
    self.pin = pin;
    self.sessionType = OKNFCLiteSessionTypeGetMnemonic;
    [self.completionBlocks setObject:complete forKey:kGetMnemonicBlock];
    [self beginNewNFCSession];
}

- (void)_getMnemonic {
    NSString *pin = self.pin;
    // Clear sensitive data from property immediately
    self.pin = nil;
    __weak typeof(self) weakSelf = self;
    [self.lite getMnemonicWithPin:pin complete:^(OKLiteV1 *lite, NSString *mnemonic, OKNFCLiteGetMncStatus status) {
        GetMnemonicCallback callback = [weakSelf takeCompletionForKey:kGetMnemonicBlock];
        if (callback) {
            callback(lite, mnemonic, status);
        }
    }];
}

#pragma mark - changePin

- (void)changePin:(NSString *)oldPin to:(NSString *)newPin complete:(ChangePinCallback)complete {
    self.pin = oldPin;
    self.neoPin = newPin;
    self.sessionType = OKNFCLiteSessionTypeChangePin;
    [self.completionBlocks setObject:complete forKey:kChangePinBlock];
    [self beginNewNFCSession];
}

- (void)_changePin {
    NSString *oldPin = self.pin;
    NSString *newPin = self.neoPin;
    // Clear sensitive data from properties immediately
    self.pin = nil;
    self.neoPin = nil;
    __weak typeof(self) weakSelf = self;
    [self.lite changePin:oldPin to:newPin complete:^(OKLiteV1 *lite, OKNFCLiteChangePinStatus status) {
        ChangePinCallback callback = [weakSelf takeCompletionForKey:kChangePinBlock];
        if (callback) {
            callback(lite, status);
        }
    }];
}

#pragma mark - reset

- (void)reset:(ResetCallback)callBack {
    self.sessionType = OKNFCLiteSessionTypeReset;
    [self.completionBlocks setObject:callBack forKey:kResetBlock];
    [self beginNewNFCSession];
}

- (void)_reset {
    __weak typeof(self) weakSelf = self;
    [self.lite reset:^(OKLiteV1 *lite, BOOL isSuccess, NSError *error) {
        ResetCallback callback = [weakSelf takeCompletionForKey:kResetBlock];
        if (callback) {
            callback(lite, isSuccess, error);
        }
    }];
}

- (BOOL)_resetSync {
    return [self.lite resetSync];
}


#pragma mark - CheckLiteVersion

-(BOOL)checkLiteVersion {
    id<NFCISO7816Tag> tag = [self.session.connectedTag asNFCISO7816Tag];
    if (!tag) { return false; }

    __block BOOL success = NO;
    dispatch_semaphore_t sema = dispatch_semaphore_create(0);

    OKLiteCommandModal *modalV1 = [[OKLiteCommandModal alloc] initWithCommand:OKLiteCommandSelectBackup version:OKNFCLiteVersionV1];
    [tag sendCommandAPDU:[modalV1 buildAPDU] completionHandler:^(NSData *responseData, uint8_t sw1, uint8_t sw2, NSError *error) {
        [OKNFCUtility logAPDU:@"检查是否LiteV1" response:responseData sw1:sw1 sw2:sw2 error:error];
        success = sw1 == OKNFC_SW1_OK;

        dispatch_semaphore_signal(sema);
    }];
    dispatch_semaphore_wait(sema, dispatch_time(DISPATCH_TIME_NOW, 30 * NSEC_PER_SEC));

    if(success) {
        self.lite = [[OKLiteV1 alloc] initWithDelegate:self];
    } else {
        OKLiteCommandModal *modalV2 = [[OKLiteCommandModal alloc] initWithCommand:OKLiteCommandSelectBackup version:OKNFCLiteVersionV2];
        [tag sendCommandAPDU:[modalV2 buildAPDU] completionHandler:^(NSData *responseData, uint8_t sw1, uint8_t sw2, NSError *error) {
            [OKNFCUtility logAPDU:@"检查是否LiteV2" response:responseData sw1:sw1 sw2:sw2 error:error];
            success = sw1 == OKNFC_SW1_OK;
            dispatch_semaphore_signal(sema);
        }];
        dispatch_semaphore_wait(sema, dispatch_time(DISPATCH_TIME_NOW, 30 * NSEC_PER_SEC));
        if (success) {
            self.lite = [[OKLiteV2 alloc] initWithDelegate:self];
        }
    }

    return success;
}


@end
