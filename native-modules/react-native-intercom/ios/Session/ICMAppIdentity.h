// Internal interface recovered from Intercom 19.9.1 Objective-C metadata.
#import <Foundation/Foundation.h>
@class ICMOreoEngine;
NS_ASSUME_NONNULL_BEGIN
@interface ICMAppIdentity : NSObject <NSSecureCoding>
@property (nonatomic, strong, nullable) NSString *appId;
@property (nonatomic, strong, nullable) NSString *apiKey;
@property (nonatomic, strong) ICMOreoEngine *engine;
- (BOOL)setApiKey:(nullable NSString *)apiKey appId:(nullable NSString *)appId;
- (BOOL)identityExists;
@end
NS_ASSUME_NONNULL_END
