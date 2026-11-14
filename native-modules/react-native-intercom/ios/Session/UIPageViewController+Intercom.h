#pragma once
#import <UIKit/UIKit.h>
@interface UIPageViewController (IntercomSDKPrivate)
+ (UIPageViewControllerNavigationDirection)localizedDirectionFor:(UIPageViewControllerNavigationDirection)direction interfaceDirection:(UIUserInterfaceLayoutDirection)interfaceDirection;
@end
