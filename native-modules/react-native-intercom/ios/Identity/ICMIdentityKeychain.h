// Objective-C declaration of the recovered Swift Keychain runtime class.
#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN

@protocol ICMIdentityKeychainStoring <NSObject>
- (nullable NSString *)stringForKey:(NSString *)key;
- (BOOL)setString:(NSString *)value forKey:(NSString *)key;
- (BOOL)deleteValueForKey:(NSString *)key;
- (BOOL)clearAll;
@end

__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate11ICMKeychain")))
@interface ICMRecoveredKeychain : NSObject <ICMIdentityKeychainStoring>
+ (instancetype)shared;
- (instancetype)initWithServiceIdentifier:(NSString *)serviceIdentifier;
@end
NS_ASSUME_NONNULL_END
