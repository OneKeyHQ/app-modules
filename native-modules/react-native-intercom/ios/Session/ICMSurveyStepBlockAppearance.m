#import "ICMSurveyStepBlockAppearance.h"
#import "ICMColorHelperImports.h"
#import "UIFont+ICMWeight.h"
@implementation ICMSurveyStepBlockAppearance
- (instancetype)init { return [self initForBackgroundColor:nil]; }
- (instancetype)initForBackgroundColor:(UIColor *)color {
    self = [super init];
    if (self) {
        self.paragraphFont = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
        self.headingFont = [UIFont preferredFontForTextStyle:UIFontTextStyleLargeTitle weight:UIFontWeightBold];
        self.subheadingFont = [UIFont preferredFontForTextStyle:UIFontTextStyleTitle2 weight:UIFontWeightBold];
        if (color) {
            UIColor *foreground = [ICMRecoveredColorHelper suitableForegroundColorFor:color theme:ICMRecoveredDefaultThemeProvider.shared.theme];
            self.headingTextColor = foreground;
            self.subHeadingTextColor = foreground;
            self.textColor = foreground;
        }
        self.inlineImagePadding = 0;
        self.textInsets = UIEdgeInsetsZero;
        self.linkTextColor = self.textColor;
        self.singleImageCornerRadius = 0;
    }
    return self;
}
@end
