#import "ICMWindowDependencies.h"
@implementation ICMUtils (ApplicationWindows)
+ (NSArray *)fetchApplicationWindows {
    NSMutableArray *windows = [NSMutableArray array];
    for (UIScene *scene in [UIApplication sharedApplication].connectedScenes.allObjects) {
        if (scene.activationState != UISceneActivationStateForegroundActive) continue;
        if (scene.delegate && [scene.delegate respondsToSelector:@selector(window)]) {
            id window = [scene.delegate performSelector:@selector(window)];
            if (window) [windows addObject:window];
        }
    }
    return windows;
}
@end
