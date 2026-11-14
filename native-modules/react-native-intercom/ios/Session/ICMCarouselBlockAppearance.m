#import "ICMCarouselBlockAppearance.h"
#import "ICMUtils+Fonts.h"
@implementation ICMCarouselBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.paragraphFont = [ICMUtils fontOfSize:20 weight:0];
        self.headingFont = [ICMUtils fontOfSize:48 weight:1];
        self.subheadingFont = [ICMUtils fontOfSize:34 weight:1];
        self.inlineImagePadding = 0;
        self.textInsets = UIEdgeInsetsZero;
        self.linkTextColor = self.textColor;
        self.singleImageCornerRadius = 0;
    }
    return self;
}
@end
