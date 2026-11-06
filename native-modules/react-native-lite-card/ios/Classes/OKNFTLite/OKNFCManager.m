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

static NSString *backupInputReason(NSString *input, NSUInteger maxBytes) {
    if (!input) return @"missing";
    if (!input.length) return @"empty";
    if (input.length > maxBytes * 2) return @"too_large";
    for (NSUInteger index = 0; index < input.length; ++index) {
        const unichar digit = [input characterAtIndex:index];
        if (!((digit >= '0' && digit <= '9') || (digit >= 'a' && digit <= 'f') ||
              (digit >= 'A' && digit <= 'F'))) return @"non_hex";
    }
    return @"accepted";
}

static void logBackupPreflight(NSString *input, BOOL overwrite, NSString *reason, NSInteger version) {
    @try {
        [LCLogger info:[NSString stringWithFormat:@"operation=setMnemonic stage=input_preflight_%@ inputPresent=%d inputChars=%lu odd=%d versionKnown=%d cardVersion=%ld overwrite=%d reason=%@ accepted=%d", version ? @"detected" : @"entry", input != nil, (unsigned long)input.length, input.length % 2 != 0, version != 0, (long)version, overwrite, reason, [reason isEqualToString:@"accepted"]]];
    } @catch (NSException *exception) {
        // Diagnostics must not alter the input decision or callback.
    }
}



@interface OKNFCManager() <NFCTagReaderSessionDelegate,OKNFCManagerDelegate>
@property (nonatomic, strong) NFCTagReaderSession *session;
@property (nonatomic, assign) NSUInteger diagnosticID;
@property (nonatomic, assign) NSUInteger diagnosticSessionID;
@property (nonatomic, copy) NSString *pin;
@property (nonatomic, copy) NSString *neoPin;
@property (nonatomic, copy) NSString *exportMnemonic;
@property (nonatomic, copy) NSString *expectedSerial;
@property (nonatomic, assign) BOOL certVerified;
@property (nonatomic, assign) OKNFCLiteApp selectNFCApp;
@property (nonatomic, strong) OKLiteV1 *lite;

@property (nonatomic, strong) NSMutableDictionary *completionBlocks;
// CoreNFC reports the app's own invalidateSession with the same code as a user
// cancel, so the invalidation callback has to know who ended the session.
@property (atomic, assign) BOOL sessionEndedByApp;

@end

@implementation OKNFCManager

- (instancetype)init {
    self = [super init];
    if (self) {
        static NSUInteger nextID = 0;
        @synchronized (OKNFCManager.class) { _diagnosticID = ++nextID; }
    }
    return self;
}

- (void)logSessionStage:(NSString *)stage details:(NSString *)details {
    [LCLogger info:[NSString stringWithFormat:@"managerID=%lu sessionID=%lu operation=%ld stage=%@ wallTimeMs=%.0f %@", (unsigned long)self.diagnosticID, (unsigned long)self.diagnosticSessionID, (long)self.sessionType, stage, NSDate.date.timeIntervalSince1970 * 1000, details]];
}


#pragma mark - OKNFCManagerDelegate

- (id<NFCISO7816Tag>)getNFCsessionTag {
    id<NFCISO7816Tag> tag = [self.session.connectedTag asNFCISO7816Tag];
    return tag;
}

- (OKNFCLiteSessionType)getSessionType {
    return self.sessionType;
}


- (void)endNFCSessionWithError:(BOOL)isError {
    [self logSessionStage:@"invalidate_request" details:[NSString stringWithFormat:@"isError=%d", isError]];
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
            self.expectedSerial = nil;
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
            ChangePinCallback callback;
            @synchronized (self) {
                self.expectedSerial = nil;
                callback = [self takeCompletionForKey:kChangePinBlock];
            }
            if (callback) {
                callback(self.lite, asCancel ? OKNFCLiteChangePinStatusCancel : OKNFCLiteChangePinStatusError);
            }
        } break;
        default:
            break;
    }
}

- (void)beginNewNFCSession {
    self.diagnosticSessionID += 1;
    [self logSessionStage:@"begin" details:@""];
    self.sessionEndedByApp = NO;
    self.session = [[NFCTagReaderSession alloc] initWithPollingOption:NFCPollingISO14443 delegate:self queue:dispatch_get_global_queue(2, 0)];
    [self.session beginSession];
}

#pragma mark - NFCTagReaderSessionDelegate

- (void)tagReaderSession:(NFCTagReaderSession *)session didDetectTags:(NSArray<__kindof id<NFCTag>> *)tags {
    [self logSessionStage:@"detect" details:[NSString stringWithFormat:@"tagCount=%lu currentSession=%d", (unsigned long)tags.count, session == self.session]];

    id<NFCISO7816Tag> tag = [tags.firstObject asNFCISO7816Tag];
    if (!tag) { return; }

    [session connectToTag:tag completionHandler:^(NSError * _Nullable error) {
        if (error) {
            [self logSessionStage:@"connect_failure" details:[NSString stringWithFormat:@"code=%ld coreNFCDomain=%d", (long)error.code, [error.domain isEqualToString:NFCErrorDomain]]];
            [self endNFCSessionWithError:YES];
            [self finishPendingRequestAsCancel:NO sessionError:nil];
            return;
        }
        [self logSessionStage:@"connect_success" details:@""];
        [self nfcSessionComplete:session];
    }];
}

- (void)tagReaderSession:(NFCTagReaderSession *)session didInvalidateWithError:(NSError *)error {
    [self logSessionStage:@"invalidated" details:[NSString stringWithFormat:@"code=%ld coreNFCDomain=%d endedByApp=%d currentSession=%d", (long)error.code, [error.domain isEqualToString:NFCErrorDomain], self.sessionEndedByApp, session == self.session]];
    // When the app ended the session, the card operation that ended it reports
    // the real result; treating this as a cancel would race with it.
    if ((error.code == 200 || error.code == 6) && !self.sessionEndedByApp) {
        [self finishPendingRequestAsCancel:YES sessionError:error];
    }
    [session invalidateSession];
}

- (void)tagReaderSessionDidBecomeActive:(NFCTagReaderSession *)session {
    [self logSessionStage:@"active" details:[NSString stringWithFormat:@"currentSession=%d", session == self.session]];
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
     expectedSerial:(NSString * _Nullable)expectedSerial
           complete:(SetMnemonicCallback)complete {

    NSString *reason = backupInputReason(mnemonic, 65535);
    BOOL accepted = [reason isEqualToString:@"accepted"];
    logBackupPreflight(mnemonic, overwrite, reason, 0);
    if (!accepted) {
        complete(nil, OKNFCLiteSetMncStatusError);
        return;
    }
    if (pin.length != OKNFC_PIN_LENGTH) {
        [self logSessionStage:@"input_rejected" details:@"operation=setMnemonic"];
        return;
    }
    self.pin = pin;
    self.exportMnemonic = mnemonic;
    self.expectedSerial = expectedSerial;
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
    NSString *expectedSerial = self.expectedSerial;
    self.expectedSerial = nil;
    // Clear sensitive data from properties immediately
    self.exportMnemonic = nil;
    self.pin = nil;
    const NSUInteger maxBytes = self.lite.version == OKNFCLiteVersionV2 ? 65511 : 65535;
    NSString *reason = backupInputReason(mnemonic, maxBytes);
    BOOL accepted = [reason isEqualToString:@"accepted"];
    logBackupPreflight(mnemonic, force, reason, self.lite.version + 1);
    if (!accepted) {
        SetMnemonicCallback callback = [self takeCompletionForKey:kSetMnemonicBlock];
        [self endNFCSessionWithError:YES];
        if (callback) callback(nil, OKNFCLiteSetMncStatusError);
        return;
    }
    if (expectedSerial != nil) {
        BOOL infoValid = [self.lite syncLiteInfo];
        if (!infoValid || ![expectedSerial isEqualToString:self.lite.SN]) {
            [self logSessionStage:infoValid ? @"identity_mismatch" : @"identity_read_failed" details:@"operation=setMnemonic"];
            SetMnemonicCallback callback = [self takeCompletionForKey:kSetMnemonicBlock];
            [self endNFCSessionWithError:YES];
            if (callback) callback(self.lite, infoValid ? OKNFCLiteSetMncStatusSNNotMatch : OKNFCLiteSetMncStatusError);
            return;
        }
    }
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
        [self logSessionStage:@"input_rejected" details:@"operation=getMnemonicWithPin"];
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

- (void)changePin:(NSString *)oldPin to:(NSString *)newPin expectedSerial:(NSString *)expectedSerial complete:(ChangePinCallback)complete {
    self.pin = oldPin;
    self.neoPin = newPin;
    self.expectedSerial = expectedSerial;
    self.sessionType = OKNFCLiteSessionTypeChangePin;
    [self.completionBlocks setObject:complete forKey:kChangePinBlock];
    [self beginNewNFCSession];
}

- (void)_changePin {
    NSString *oldPin;
    NSString *newPin;
    NSString *expectedSerial;
    ChangePinCallback callback;
    @synchronized (self) {
        // Claim the request and its guard together so cancellation cannot
        // turn a late connection into an unguarded PIN change.
        callback = [self takeCompletionForKey:kChangePinBlock];
        if (!callback) return;
        oldPin = self.pin;
        newPin = self.neoPin;
        expectedSerial = self.expectedSerial;
        self.pin = nil;
        self.neoPin = nil;
        self.expectedSerial = nil;
    }
    if (expectedSerial != nil) {
        BOOL infoValid = [self.lite syncLiteInfo];
        if (!infoValid || !self.lite.SN.length || ![expectedSerial isEqualToString:self.lite.SN]) {
            [self endNFCSessionWithError:YES];
            callback(self.lite, infoValid ? OKNFCLiteChangePinStatusSNNotMatch : OKNFCLiteChangePinStatusError);
            return;
        }
    }
    [self.lite changePin:oldPin to:newPin complete:^(OKLiteV1 *lite, OKNFCLiteChangePinStatus status) {
        callback(lite, status);
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
