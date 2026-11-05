#import "ICMSurveyQuestionBlockAppearance.h"
@implementation ICMSurveyQuestionBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.paragraphFont = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
        self.headingFont = [UIFont preferredFontForTextStyle:UIFontTextStyleTitle1];
        self.subheadingFont = [UIFont preferredFontForTextStyle:UIFontTextStyleTitle2];
        self.inlineImagePadding = 0;
        self.textInsets = UIEdgeInsetsZero;
        self.linkTextColor = self.textColor;
        self.singleImageCornerRadius = 0;
    }
    return self;
}
- (instancetype)initWithRequiredIndicatorColor:(UIColor *)color {
    self = [self init];
    if (self) self.requiredIndicatorColor = color;
    return self;
}
@end
