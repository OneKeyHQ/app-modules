#import "UIFont+ICMWeight.h"
@implementation UIFont (ICMWeight)
+ (UIFont *)preferredFontForTextStyle:(UIFontTextStyle)style weight:(UIFontWeight)weight {
    UIFontDescriptor *descriptor = [UIFontDescriptor preferredFontDescriptorWithTextStyle:style];
    return [UIFont systemFontOfSize:descriptor.pointSize weight:weight];
}
@end
