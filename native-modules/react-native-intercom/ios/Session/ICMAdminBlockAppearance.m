#import "ICMBlockAppearance.h"
#import "ICMBlockAppearanceDependencies.h"
@implementation ICMAdminBlockAppearance
- (instancetype)init {
    if ((self = [super init])) {
        self.linkTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
        self.headingTextColor = self.textColor;
        self.subHeadingTextColor = self.textColor;
        self.buttonBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer;
        self.buttonTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
        self.codeBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerEmphasis;
        self.codeTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
        self.typingDotColor = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
        self.activityIndicatorColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
        self.activityIndicatorImage = [ICMAssets spinnerImage];
        self.paragraphFont = [ICMUtils fontOfSize:16 weight:0];
        self.codeFont = [UIFont monospacedSystemFontOfSize:14 weight:UIFontWeightRegular];
        self.headingFont = [ICMUtils fontOfSize:16 weight:1];
        self.subheadingFont = [ICMUtils fontOfSize:16 weight:2];
        self.buttonFont = [ICMUtils fontOfSize:14 weight:2];
        self.textInsets = UIEdgeInsetsMake(10, 10, 10, 10);
        self.codeInsets = UIEdgeInsetsMake(0, 12, 12, 12);
        self.linkInsets = UIEdgeInsetsMake(12, 12, 12, 12);
        self.twitterFollowString = [[ICMTranslationManager messenger] translationForKey:@"twitter_follow"];
        self.facebookFollowString = [[ICMTranslationManager messenger] translationForKey:@"facebook_like"];
        self.inlineImageCornerRadius = 0;
        self.inlineImagePadding = 10;
        self.singleImageCornerRadius = 6;
        self.singleImagePadding = 0;
        self.isTextSelectable = NO;
        self.messengerCardInsets = UIEdgeInsetsMake(8, 8, 8, 8);
        self.showMessengerAppBorder = YES;
    }
    return self;
}
@end
