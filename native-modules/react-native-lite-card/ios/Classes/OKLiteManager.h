//
//  OKLiteManager.h
//  OneKeyWallet
//
//  Created by linleiqin on 2021/11/8.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^OKLiteCallback)(NSError * _Nullable error, id _Nullable result, NSDictionary * _Nullable info);

@interface OKLiteManager : NSObject

+ (instancetype)shared;
+ (BOOL)checkSDKValid:(NSError **)error;

- (void)checkNFCPermission:(OKLiteCallback)callback;
- (void)getLiteInfo:(OKLiteCallback)callback;
- (void)setMnemonic:(NSString *)mnemonic pin:(NSString *)pin overwrite:(BOOL)overwrite callback:(OKLiteCallback)callback;
- (void)getMnemonicWithPin:(NSString *)pin callback:(OKLiteCallback)callback;
- (void)changePin:(NSString *)oldPwd newPwd:(NSString *)newPwd callback:(OKLiteCallback)callback;
- (void)reset:(OKLiteCallback)callback;

@end

NS_ASSUME_NONNULL_END
