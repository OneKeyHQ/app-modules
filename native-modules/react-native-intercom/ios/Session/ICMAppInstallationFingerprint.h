// Internal interface recovered from Intercom 19.9.1 metadata.
#import <Foundation/Foundation.h>
@interface ICMAppInstallationFingerprint : NSObject <NSSecureCoding>
@property (nonatomic, copy, readonly) NSString *appId;
@property (nonatomic, copy, readonly) NSString *bundleIdentifier;
@property (nonatomic, copy, readonly) NSString *appVersion;
@property (nonatomic, copy, readonly) NSString *intercomVersion;
+ (NSString *)currentIntercomVersion;
- (BOOL)isEqualToFingerprint:(ICMAppInstallationFingerprint *)fingerprint;
@end
