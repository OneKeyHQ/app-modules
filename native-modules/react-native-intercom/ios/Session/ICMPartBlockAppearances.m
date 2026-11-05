// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMPartBlockAppearances.h"
#import "ICMBlockAppearanceDependencies.h"
@implementation ICMContainerCardBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
        self.textInsets = UIEdgeInsetsMake(8, -4, 0, -6);
        self.linkInsets = UIEdgeInsetsMake(12, 12, 12, 12);
        self.linkTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.headingTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.subHeadingTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.buttonBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer;
        self.buttonTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
        self.codeBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
        self.codeTextColor = [UIColor whiteColor];
        self.typingDotColor = [UIColor clearColor];
        self.activityIndicatorColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
        self.activityIndicatorImage = [ICMAssets spinnerImage];
        self.paragraphFont = [ICMUtils fontOfSize:16 weight:0];
        self.codeFont = [UIFont monospacedSystemFontOfSize:14 weight:UIFontWeightRegular];
        self.headingFont = [ICMUtils fontOfSize:22 weight:0];
        self.subheadingFont = [ICMUtils fontOfSize:16 weight:0];
        self.buttonFont = [ICMUtils fontOfSize:15 weight:0];
        self.twitterFollowString = [[ICMTranslationManager messenger] translationForKey:@"twitter_follow"];
        self.facebookFollowString = [[ICMTranslationManager messenger] translationForKey:@"facebook_like"];
        self.inlineImageCornerRadius = 2;
        self.inlineImagePadding = 10;
        self.messengerCardInsets = UIEdgeInsetsZero;
        self.showMessengerAppBorder = YES;
    }
    return self;
}
@end
@implementation ICMMessengerCardBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.messengerCardInsets = UIEdgeInsetsZero;
        self.showMessengerAppBorder = NO;
    }
    return self;
}
@end
@implementation ICMInAppBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.headingTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.headingFont = [ICMUtils fontOfSize:22 weight:3];
        self.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
        self.paragraphFont = [ICMUtils fontOfSize:15 weight:0];
        self.linkTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.subHeadingTextColor = self.textColor;
        self.subheadingFont = [ICMUtils fontOfSize:16 weight:0];
        self.buttonBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer;
        self.buttonTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
        self.codeBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerEmphasis;
        self.codeTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
        self.codeFont = [UIFont monospacedSystemFontOfSize:14 weight:UIFontWeightRegular];
        self.typingDotColor = [UIColor clearColor];
        self.activityIndicatorColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
        self.activityIndicatorImage = [ICMAssets spinnerImage];
        self.buttonFont = [ICMUtils fontOfSize:15 weight:2];
        self.textInsets = UIEdgeInsetsMake(8, -4, 10, -4);
        self.twitterFollowString = [[ICMTranslationManager messenger] translationForKey:@"twitter_follow"];
        self.facebookFollowString = [[ICMTranslationManager messenger] translationForKey:@"facebook_like"];
        self.inlineImageCornerRadius = 2;
        self.inlineImagePadding = 0;
        self.singleImagePadding = 0;
        self.messengerCardInsets = UIEdgeInsetsZero;
        self.showMessengerAppBorder = YES;
    }
    return self;
}
@end
@implementation ICMPostBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.textColor = [UIColor whiteColor];
        self.headingTextColor = self.textColor;
        self.subHeadingTextColor = self.textColor;
        self.linkTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.buttonBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer;
        self.buttonTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
        self.codeBackgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.textAlwaysLight;
        self.codeTextColor = [UIColor whiteColor];
        self.typingDotColor = [UIColor clearColor];
        self.activityIndicatorColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
        self.activityIndicatorImage = [ICMAssets spinnerImage];
        self.paragraphFont = [ICMUtils fontOfSize:15 weight:0];
        self.codeFont = [UIFont monospacedSystemFontOfSize:14 weight:UIFontWeightRegular];
        self.headingFont = [ICMUtils fontOfSize:24 weight:0];
        self.subheadingFont = [ICMUtils fontOfSize:16 weight:3];
        self.buttonFont = [ICMUtils fontOfSize:14 weight:2];
        self.textInsets = UIEdgeInsetsMake(8, -4, 10, -4);
        self.twitterFollowString = [[ICMTranslationManager messenger] translationForKey:@"twitter_follow"];
        self.facebookFollowString = [[ICMTranslationManager messenger] translationForKey:@"facebook_like"];
        self.inlineImageCornerRadius = 2;
        self.inlineImagePadding = 10;
        self.loadingViewColorType = 1;
        self.messengerCardInsets = UIEdgeInsetsZero;
        self.showMessengerAppBorder = YES;
    }
    return self;
}
@end
