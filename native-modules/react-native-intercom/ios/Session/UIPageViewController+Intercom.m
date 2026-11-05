#import "UIPageViewController+Intercom.h"
@implementation UIPageViewController (IntercomSDKPrivate)
+ (UIPageViewControllerNavigationDirection)localizedDirectionFor:(UIPageViewControllerNavigationDirection)direction interfaceDirection:(UIUserInterfaceLayoutDirection)interfaceDirection {
    return interfaceDirection == UIUserInterfaceLayoutDirectionRightToLeft ? (direction == UIPageViewControllerNavigationDirectionReverse ? UIPageViewControllerNavigationDirectionForward : UIPageViewControllerNavigationDirectionReverse) : direction;
}
@end
