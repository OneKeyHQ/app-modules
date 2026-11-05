// Original UIAppearance mutations scoped to Intercom's container class.
#import "ICMAppearanceReset.h"
@implementation ICMAppearanceReset
+ (void)resetAppearanceWithin:(Class)container {
    [UIActivityIndicatorView appearanceWhenContainedInInstancesOfClasses:@[container]].color = nil;
    UIButton *button = [UIButton appearanceWhenContainedInInstancesOfClasses:@[container]];
    [button setTitleColor:nil forState:UIControlStateNormal];
    [button setTitleShadowColor:nil forState:UIControlStateNormal];
    [button setBackgroundImage:nil forState:UIControlStateNormal];
    UITableView *table = [UITableView appearanceWhenContainedInInstancesOfClasses:@[container]];
    table.separatorInset = UIEdgeInsetsZero;
    table.sectionIndexColor = nil;
    table.sectionIndexBackgroundColor = nil;
    table.sectionIndexTrackingBackgroundColor = nil;
    table.separatorColor = nil;
    table.separatorEffect = nil;
    UITableViewCell *cell = [UITableViewCell appearanceWhenContainedInInstancesOfClasses:@[container]];
    cell.separatorInset = UIEdgeInsetsZero;
    cell.focusStyle = UITableViewCellFocusStyleDefault;
    [UIScrollView appearanceWhenContainedInInstancesOfClasses:@[container]].contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentAutomatic;
    [self resetUIView:container];
    [self resetUIAlertController:container];
}
+ (void)resetUIView:(Class)container {
    UIView *view = [UIView appearanceWhenContainedInInstancesOfClasses:@[container]];
    view.backgroundColor = [UIColor clearColor];
    view.semanticContentAttribute = UISemanticContentAttributeUnspecified;
}
+ (void)resetUIAlertController:(Class)container {
    [UIView appearanceWhenContainedInInstancesOfClasses:@[[UIAlertController class], container]].tintColor = [UIColor systemBlueColor];
}
@end
