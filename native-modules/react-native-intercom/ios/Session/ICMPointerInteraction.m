#import "ICMPointerInteraction.h"
@implementation ICMPointerInteraction
+ (UIPointerStyle *)standardLargeButtonPointerStyle:(UIPointerInteraction *)interaction {
    UITargetedPreview *preview = [[UITargetedPreview alloc] initWithView:interaction.view];
    UIPointerLiftEffect *effect = [UIPointerLiftEffect effectWithPreview:preview];
    return [UIPointerStyle styleWithEffect:effect shape:nil];
}
@end
