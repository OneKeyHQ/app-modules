// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import <Foundation/Foundation.h>
@protocol ICMSoundPlayerProtocol
+ (void)playSoundForAdminResponse;
+ (void)playSoundForOperatorResponse;
+ (void)playSoundForSendingMessage;
@end
@interface ICMSoundPlayer : NSObject <ICMSoundPlayerProtocol>
+ (void)playSoundForInApp;
+ (void)playSoundForAdminResponse;
+ (void)playSoundForOperatorResponse;
+ (void)playSoundForFailedMessage;
+ (void)playSoundForSendingMessage;
+ (void)playSoundForURL:(nullable NSURL *)url;
@end
