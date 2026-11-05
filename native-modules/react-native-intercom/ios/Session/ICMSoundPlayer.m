// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMSoundPlayer.h"
#import "ICMAssets.h"
#import "ICMConfig.h"
#import <AudioToolbox/AudioToolbox.h>
@implementation ICMSoundPlayer
+ (void)playSoundForInApp { [ICMSoundPlayer playSoundForAdminResponse]; }
+ (void)playSoundForAdminResponse {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        [ICMSoundPlayer playSoundForURL:[ICMAssets soundForIncomingAdminMessage]];
    });
}
+ (void)playSoundForOperatorResponse {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        [ICMSoundPlayer playSoundForURL:[ICMAssets soundForIncomingOperatorMessage]];
    });
}
+ (void)playSoundForFailedMessage {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        [ICMSoundPlayer playSoundForURL:[ICMAssets soundForMessageSendingFailure]];
    });
}
+ (void)playSoundForSendingMessage {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        [ICMSoundPlayer playSoundForURL:[ICMAssets soundForMessageSending]];
    });
}
+ (void)playSoundForURL:(NSURL *)url {
    if (!url) return;
    if (![[ICMConfig sharedConfig] audioEnabled]) return;
    SystemSoundID sound = 875;
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)url, &sound);
    AudioServicesPlaySystemSound(sound);
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 10000000000LL), dispatch_get_main_queue(), ^{
        AudioServicesDisposeSystemSoundID(sound);
    });
}
@end
