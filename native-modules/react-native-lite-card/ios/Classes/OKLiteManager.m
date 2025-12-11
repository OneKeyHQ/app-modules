//
//  OKLiteManager.m
//  OneKeyWallet
//
//  Created by linleiqin on 2021/11/8.
//

#import "OKLiteManager.h"
#import "NFCConfig.h"
#import "OKNFCManager.h"
#import "OKLiteV1.h"
#import <CoreNFC/CoreNFC.h>

typedef NS_ENUM(NSInteger, NFCLiteExceptions) {
  NFCLiteExceptionsInitChannel = 1000,// 初始化异常
  NFCLiteExceptionsNotExistsNFC = 1001,// 没有 NFC 设备
  NFCLiteExceptionsNotEnableNFC = 1002, // 没有开启 NFC 设备
  NFCLiteExceptionsNotNFCPermission = 1003,// 没有使用 NFC 的权限
  NFCLiteExceptionsConnectionFail = 2001,// 连接失败
  NFCLiteExceptionsInterrupt = 2002,// 操作中断（可能是连接问题）
  NFCLiteExceptionsDeviceMismatch = 2003,// 连接设备不匹配
  NFCLiteExceptionsUserCancel = 2004,// 用户取消连接
  NFCLiteExceptionsPasswordWrong = 3001,// 密码错误
  NFCLiteExceptionsInputPasswordEmpty = 3002,// 输入密码为空
  NFCLiteExceptionsPasswordEmpty = 3003,// 未设置过密码
  NFCLiteExceptionsInitPassword = 3004,// 设置初始化密码错误
  NFCLiteExceptionsCardLock = 3005,// 密码重试次数太多已经锁死
  NFCLiteExceptionsAutoReset = 3006,// 密码重试次数太多已经自动重制卡片
  NFCLiteExceptionsExecFailure = 4000,// 未知的命令执行失败
  NFCLiteExceptionsInitialized = 4001,// 已经备份过内容
  NFCLiteExceptionsNotInitialized = 4002,// 没有备份过内容
};

@implementation OKLiteManager

static OKLiteManager *_sharedInstance = nil;
static dispatch_once_t onceToken;

+ (instancetype)shared {
    dispatch_once(&onceToken, ^{
        _sharedInstance = [[OKLiteManager alloc] init];
    });
    return _sharedInstance;
}

- (void)checkNFCPermission:(OKLiteCallback)callback {
    BOOL permission = [NFCNDEFReaderSession readingAvailable];
    if (permission) {
        callback(nil, @(permission), nil);
    } else {
        NSError *error = [NSError errorWithDomain:@"NFCLiteError" 
                                             code:NFCLiteExceptionsNotExistsNFC 
                                         userInfo:@{NSLocalizedDescriptionKey: @"NFC not available"}];
        callback(error, nil, nil);
    }
}

- (void)getLiteInfo:(OKLiteCallback)callback {
    NSError *error = nil;
    if (![OKLiteManager checkSDKValid:&error]) {
        callback(error, nil, nil);
        return;
    }
    
    __block OKNFCManager *liteManager = [[OKNFCManager alloc] init];
    [liteManager getLiteInfo:^(OKLiteV1 *lite, OKNFCLiteStatus status) {
        NSDictionary *cardInfo = [lite cardInfo];
        BOOL isError = status == OKNFCLiteStatusError || status == OKNFCLiteStatusSNNotMatch;
        if (isError) {
            NSError *error = [NSError errorWithDomain:@"NFCLiteError" 
                                                 code:NFCLiteExceptionsConnectionFail 
                                             userInfo:@{NSLocalizedDescriptionKey: @"Connection failed"}];
            callback(error, nil, nil);
        } else {
            callback(nil, cardInfo, cardInfo);
        }
        liteManager = nil;
    }];
}

- (void)setMnemonic:(NSString *)mnemonic pin:(NSString *)pin overwrite:(BOOL)overwrite callback:(OKLiteCallback)callback {
    NSError *error = nil;
    if (![OKLiteManager checkSDKValid:&error]) {
        callback(error, nil, nil);
        return;
    }
    
    __block OKNFCManager *liteManager = [[OKNFCManager alloc] init];
    [liteManager setMnemonic:mnemonic withPin:pin overwrite:overwrite complete:^(OKLiteV1 *lite, OKNFCLiteSetMncStatus status) {
        NSDictionary *cardInfo = [lite cardInfo];
        NSError *error = nil;
        
        switch (status) {
            case OKNFCLiteSetMncStatusSuccess:
                callback(nil, @(YES), cardInfo);
                break;
            case OKNFCLiteSetMncStatusError:
                if (lite.status == OKNFCLiteStatusActivated) {
                    error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsInitialized userInfo:@{NSLocalizedDescriptionKey: @"Already initialized"}];
                } else {
                    error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsConnectionFail userInfo:@{NSLocalizedDescriptionKey: @"Connection failed"}];
                }
                callback(error, nil, nil);
                break;
            case OKNFCLiteSetMncStatusSNNotMatch:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsDeviceMismatch userInfo:@{NSLocalizedDescriptionKey: @"Device mismatch"}];
                callback(error, nil, nil);
                break;
            case OKNFCLiteSetMncStatusPinNotMatch:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsPasswordWrong userInfo:@{NSLocalizedDescriptionKey: @"Password wrong"}];
                callback(error, nil, cardInfo);
                break;
            case OKNFCLiteSetMncStatusWiped:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsAutoReset userInfo:@{NSLocalizedDescriptionKey: @"Auto reset"}];
                callback(error, nil, nil);
                break;
            case OKNFCLiteSetMncStatusCancel:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsUserCancel userInfo:@{NSLocalizedDescriptionKey: @"User cancelled"}];
                callback(error, nil, nil);
                break;
            default:
                break;
        }
        liteManager = nil;
    }];
}

- (void)getMnemonicWithPin:(NSString *)pin callback:(OKLiteCallback)callback {
    NSError *error = nil;
    if (![OKLiteManager checkSDKValid:&error]) {
        callback(error, nil, nil);
        return;
    }
    
    __block OKNFCManager *liteManager = [[OKNFCManager alloc] init];
    [liteManager getMnemonicWithPin:pin complete:^(OKLiteV1 *lite, NSString *mnemonic, OKNFCLiteGetMncStatus status) {
        NSDictionary *cardInfo = [lite cardInfo];
        NSError *error = nil;
        
        switch (status) {
            case OKNFCLiteGetMncStatusSuccess:
                if (mnemonic.length > 0) {
                    callback(nil, mnemonic, cardInfo);
                }
                break;
            case OKNFCLiteGetMncStatusError:
                if (lite.status == OKNFCLiteStatusNewCard && lite.SN != nil) {
                    error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsNotInitialized userInfo:@{NSLocalizedDescriptionKey: @"Not initialized"}];
                } else {
                    error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsConnectionFail userInfo:@{NSLocalizedDescriptionKey: @"Connection failed"}];
                }
                callback(error, nil, nil);
                break;
            case OKNFCLiteGetMncStatusSNNotMatch:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsDeviceMismatch userInfo:@{NSLocalizedDescriptionKey: @"Device mismatch"}];
                callback(error, nil, nil);
                break;
            case OKNFCLiteGetMncStatusPinNotMatch:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsPasswordWrong userInfo:@{NSLocalizedDescriptionKey: @"Password wrong"}];
                callback(error, nil, cardInfo);
                break;
            case OKNFCLiteGetMncStatusWiped:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsAutoReset userInfo:@{NSLocalizedDescriptionKey: @"Auto reset"}];
                callback(error, nil, nil);
                break;
            case OKNFCLiteGetMncStatusCancel:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsUserCancel userInfo:@{NSLocalizedDescriptionKey: @"User cancelled"}];
                callback(error, nil, nil);
                break;
            default:
                break;
        }
        liteManager = nil;
    }];
}

- (void)changePin:(NSString *)oldPwd newPwd:(NSString *)newPwd callback:(OKLiteCallback)callback {
    NSError *error = nil;
    if (![OKLiteManager checkSDKValid:&error]) {
        callback(error, nil, nil);
        return;
    }
    
    __block OKNFCManager *liteManager = [[OKNFCManager alloc] init];
    [liteManager changePin:oldPwd to:newPwd complete:^(OKLiteV1 *lite, OKNFCLiteChangePinStatus status) {
        NSDictionary *cardInfo = [lite cardInfo];
        NSError *error = nil;
        
        switch (status) {
            case OKNFCLiteChangePinStatusSuccess:
                callback(nil, @(YES), cardInfo);
                break;
            case OKNFCLiteChangePinStatusWiped:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsAutoReset userInfo:@{NSLocalizedDescriptionKey: @"Auto reset"}];
                callback(error, nil, nil);
                break;
            case OKNFCLiteChangePinStatusPinNotMatch:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsPasswordWrong userInfo:@{NSLocalizedDescriptionKey: @"Password wrong"}];
                callback(error, nil, cardInfo);
                break;
            case OKNFCLiteChangePinStatusError:
                if (lite.status == OKNFCLiteStatusNewCard && lite.SN != nil) {
                    error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsNotInitialized userInfo:@{NSLocalizedDescriptionKey: @"Not initialized"}];
                } else {
                    error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsConnectionFail userInfo:@{NSLocalizedDescriptionKey: @"Connection failed"}];
                }
                callback(error, nil, nil);
                break;
            case OKNFCLiteChangePinStatusCancel:
                error = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsUserCancel userInfo:@{NSLocalizedDescriptionKey: @"User cancelled"}];
                callback(error, nil, nil);
                break;
            default:
                break;
        }
        liteManager = nil;
    }];
}

- (void)reset:(OKLiteCallback)callback {
    NSError *error = nil;
    if (![OKLiteManager checkSDKValid:&error]) {
        callback(error, nil, nil);
        return;
    }
    
    __block OKNFCManager *liteManager = [[OKNFCManager alloc] init];
    [liteManager reset:^(OKLiteV1 *lite, BOOL isSuccess, NSError *error) {
        if (isSuccess) {
            NSDictionary *cardInfo = [lite cardInfo];
            callback(nil, @(YES), cardInfo);
        } else {
            NSError *callbackError = nil;
            if (error && error.code == 200) {
                callbackError = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsUserCancel userInfo:@{NSLocalizedDescriptionKey: @"User cancelled"}];
            } else {
                callbackError = [NSError errorWithDomain:@"NFCLiteError" code:NFCLiteExceptionsConnectionFail userInfo:@{NSLocalizedDescriptionKey: @"Connection failed"}];
            }
            callback(callbackError, nil, nil);
        }
        liteManager = nil;
    }];
}

+ (BOOL)checkSDKValid:(NSError **)error {
    if (![NFCNDEFReaderSession readingAvailable]) {
        if (error) {
            *error = [NSError errorWithDomain:@"NFCLiteError" 
                                         code:NFCLiteExceptionsNotNFCPermission 
                                     userInfo:@{NSLocalizedDescriptionKey: @"NFC permission not available"}];
        }
        return NO;
    }
    if ([NFCConfig envFor:@"crt"].length > 0 && [NFCConfig envFor:@"sk"].length > 0) {
        return YES;
    }
    if (error) {
        *error = [NSError errorWithDomain:@"NFCLiteError" 
                                     code:NFCLiteExceptionsInitChannel 
                                 userInfo:@{NSLocalizedDescriptionKey: @"Failed to initialize channel"}];
    }
    return NO;
}


@end
