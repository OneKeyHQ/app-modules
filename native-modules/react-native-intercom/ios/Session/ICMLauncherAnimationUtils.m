#import "ICMLauncherAnimationUtils.h"
#import <UIKit/UIKit.h>
@implementation ICMLauncherAnimationUtils
+ (void)animate:(void (^)(void))animations completion:(void (^)(void))completion {
    [UIView animateWithDuration:0.3 delay:0 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:animations completion:^(BOOL finished) {
        (void)finished;
        if (completion) completion();
    }];
}
@end
