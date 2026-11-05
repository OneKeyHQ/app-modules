// Required original asset subset; see docs/IOS_PROVENANCE.md.
#import "ICMAssets.h"
@implementation ICMAssets
+ (UIImage *)spinnerImage { return [self imageNamed:@"spinner"]; }
+ (UIImage *)sendMessageIcon { return [self tintableImageNamed:@"send_message"]; }
+ (UIImage *)helpCenterChevron { return [self tintableImageNamed:@"help_center_chevron"]; }
+ (UIImage *)askAQuestion { return [self tintableImageNamed:@"ask_a_question"]; }
+ (UIImage *)replyTimeIcon { return [self tintableImageNamed:@"reply_time_icon"]; }
+ (UIImage *)aiIcon { return [self tintableImageNamed:@"icon_ai"]; }
+ (UIImage *)tintableImageNamed:(NSString *)name {
    return [[self imageNamed:name] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
}
+ (UIImage *)imageNamed:(NSString *)name {
    UIImage *image = [ICMRecoveredAssetManager imageWithNamed:name];
    if (!image) NSLog(@"[Intercom] ERROR - 💥 Resource 'images/%@.png' can't be found.\n", name);
    return image;
}
@end
