#import "ICMBlockAppearance.h"
#import "ICMBlockAppearanceDependencies.h"
@implementation ICMBlockAppearance
@synthesize syntaxKeywordColor = _syntaxKeywordColor;
@synthesize syntaxStringColor = _syntaxStringColor;
@synthesize syntaxNumberColor = _syntaxNumberColor;
@synthesize syntaxCommentColor = _syntaxCommentColor;
@synthesize syntaxFunctionColor = _syntaxFunctionColor;
@synthesize syntaxTagColor = _syntaxTagColor;
@synthesize syntaxOperatorColor = _syntaxOperatorColor;
@synthesize linkTextColor = linkTextColor;
@synthesize textColor = textColor;
@synthesize headingTextColor = headingTextColor;
@synthesize subHeadingTextColor = subHeadingTextColor;
@synthesize buttonBackgroundColor = buttonBackgroundColor;
@synthesize buttonTextColor = buttonTextColor;
@synthesize codeBackgroundColor = codeBackgroundColor;
@synthesize codeTextColor = codeTextColor;
@synthesize typingDotColor = typingDotColor;
@synthesize separatorColor = separatorColor;
@synthesize appPrimaryColor = appPrimaryColor;
@synthesize requiredIndicatorColor = requiredIndicatorColor;
@synthesize paragraphFont = paragraphFont;
@synthesize codeFont = codeFont;
@synthesize headingFont = headingFont;
@synthesize subheadingFont = subheadingFont;
@synthesize buttonFont = buttonFont;
@synthesize textInsets = textInsets;
@synthesize codeInsets = codeInsets;
@synthesize linkInsets = linkInsets;
@synthesize inlineCodePadding = inlineCodePadding;
@synthesize loadingViewColorType = loadingViewColorType;
@synthesize inlineImagePadding = inlineImagePadding;
@synthesize singleImagePadding = singleImagePadding;
@synthesize inlineImageCornerRadius = inlineImageCornerRadius;
@synthesize singleImageCornerRadius = singleImageCornerRadius;
@synthesize twitterFollowString = twitterFollowString;
@synthesize facebookFollowString = facebookFollowString;
@synthesize isTextSelectable = isTextSelectable;
@synthesize linkFooterImage = linkFooterImage;
@synthesize linkFooterImageSize = linkFooterImageSize;
@synthesize linkFooterInsets = linkFooterInsets;
@synthesize maxLinkHeight = maxLinkHeight;
@synthesize defaultLinkHeight = defaultLinkHeight;
@synthesize activityIndicatorColor = activityIndicatorColor;
@synthesize activityIndicatorImage = activityIndicatorImage;
@synthesize messengerCardInsets = messengerCardInsets;
@synthesize showMessengerAppBorder = showMessengerAppBorder;
- (instancetype)init {
    if ((self = [super init])) {
        ICMRecoveredTheme *theme = [ICMRecoveredDefaultThemeProvider shared].theme;
        [self setSyntaxKeywordColor:theme.syntaxKeyword];
        [self setSyntaxStringColor:theme.syntaxString];
        [self setSyntaxNumberColor:theme.syntaxNumber];
        [self setSyntaxCommentColor:theme.syntaxComment];
        [self setSyntaxFunctionColor:theme.syntaxFunction];
        [self setSyntaxTagColor:theme.syntaxTag];
        [self setSyntaxOperatorColor:theme.syntaxOperator];
        [self setInlineCodePadding:[ICMRecoveredInlineCodeChipLayoutManager horizontalPadding]];
    }
    return self;
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
// Original getter ignores the backing ivar and reads the current theme.
- (UIColor *)appPrimaryColor { return [ICMRecoveredDefaultThemeProvider shared].theme.primaryText; }
#pragma clang diagnostic pop
- (UIImage *)linkAuthorInitialsImageForName:(NSString *)name diameter:(CGFloat)diameter {
    NSString *initial = [name stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    if (initial.length != 0) initial = [[initial substringWithRange:NSMakeRange(0, 1)] uppercaseString];
    return [ICMAssets defaultAvatarImageWithInitials:initial diameter:diameter useActionColorAsBackgroundColor:YES];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ICMBlockAppearance class]]) return NO;
    ICMBlockAppearance *other = object;
    if (![[ICMColor hexStringFromColor:self.linkTextColor] isEqualToString:[ICMColor hexStringFromColor:other.linkTextColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.textColor] isEqualToString:[ICMColor hexStringFromColor:other.textColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.headingTextColor] isEqualToString:[ICMColor hexStringFromColor:other.headingTextColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.subHeadingTextColor] isEqualToString:[ICMColor hexStringFromColor:other.subHeadingTextColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.buttonBackgroundColor] isEqualToString:[ICMColor hexStringFromColor:other.buttonBackgroundColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.buttonTextColor] isEqualToString:[ICMColor hexStringFromColor:other.buttonTextColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.codeBackgroundColor] isEqualToString:[ICMColor hexStringFromColor:other.codeBackgroundColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.codeTextColor] isEqualToString:[ICMColor hexStringFromColor:other.codeTextColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.syntaxKeywordColor] isEqualToString:[ICMColor hexStringFromColor:other.syntaxKeywordColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.syntaxStringColor] isEqualToString:[ICMColor hexStringFromColor:other.syntaxStringColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.syntaxNumberColor] isEqualToString:[ICMColor hexStringFromColor:other.syntaxNumberColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.syntaxCommentColor] isEqualToString:[ICMColor hexStringFromColor:other.syntaxCommentColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.syntaxFunctionColor] isEqualToString:[ICMColor hexStringFromColor:other.syntaxFunctionColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.syntaxTagColor] isEqualToString:[ICMColor hexStringFromColor:other.syntaxTagColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.syntaxOperatorColor] isEqualToString:[ICMColor hexStringFromColor:other.syntaxOperatorColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.typingDotColor] isEqualToString:[ICMColor hexStringFromColor:other.typingDotColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.separatorColor] isEqualToString:[ICMColor hexStringFromColor:other.separatorColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.appPrimaryColor] isEqualToString:[ICMColor hexStringFromColor:other.appPrimaryColor]]) return NO;
    if (![[ICMColor hexStringFromColor:self.requiredIndicatorColor] isEqualToString:[ICMColor hexStringFromColor:other.requiredIndicatorColor]]) return NO;
    if (!((self.paragraphFont == nil && other.paragraphFont == nil) || [self.paragraphFont isEqual:other.paragraphFont])) return NO;
    if (!((self.codeFont == nil && other.codeFont == nil) || [self.codeFont isEqual:other.codeFont])) return NO;
    if (!((self.headingFont == nil && other.headingFont == nil) || [self.headingFont isEqual:other.headingFont])) return NO;
    if (!((self.subheadingFont == nil && other.subheadingFont == nil) || [self.subheadingFont isEqual:other.subheadingFont])) return NO;
    if (!((self.buttonFont == nil && other.buttonFont == nil) || [self.buttonFont isEqual:other.buttonFont])) return NO;
    UIEdgeInsets selftextInsets = self.textInsets;
    UIEdgeInsets othertextInsets = other.textInsets;
    if (selftextInsets.left != othertextInsets.left || selftextInsets.top != othertextInsets.top || selftextInsets.right != othertextInsets.right || selftextInsets.bottom != othertextInsets.bottom) return NO;
    UIEdgeInsets selfcodeInsets = self.codeInsets;
    UIEdgeInsets othercodeInsets = other.codeInsets;
    if (selfcodeInsets.left != othercodeInsets.left || selfcodeInsets.top != othercodeInsets.top || selfcodeInsets.right != othercodeInsets.right || selfcodeInsets.bottom != othercodeInsets.bottom) return NO;
    UIEdgeInsets selflinkInsets = self.linkInsets;
    UIEdgeInsets otherlinkInsets = other.linkInsets;
    if (selflinkInsets.left != otherlinkInsets.left || selflinkInsets.top != otherlinkInsets.top || selflinkInsets.right != otherlinkInsets.right || selflinkInsets.bottom != otherlinkInsets.bottom) return NO;
    if (self.loadingViewColorType != other.loadingViewColorType) return NO;
    if (self.inlineImagePadding != other.inlineImagePadding) return NO;
    if (self.inlineCodePadding != other.inlineCodePadding) return NO;
    if (self.singleImagePadding != other.singleImagePadding) return NO;
    if (self.inlineImageCornerRadius != other.inlineImageCornerRadius) return NO;
    if (self.singleImageCornerRadius != other.singleImageCornerRadius) return NO;
    if (!((self.twitterFollowString == nil && other.twitterFollowString == nil) || [self.twitterFollowString isEqualToString:other.twitterFollowString])) return NO;
    if (!((self.facebookFollowString == nil && other.facebookFollowString == nil) || [self.facebookFollowString isEqualToString:other.facebookFollowString])) return NO;
    if (self.isTextSelectable != other.isTextSelectable) return NO;
    CGSize selfSize = self.linkFooterImageSize;
    CGSize otherSize = other.linkFooterImageSize;
    if (selfSize.width != otherSize.width || selfSize.height != otherSize.height) return NO;
    UIEdgeInsets selflinkFooterInsets = self.linkFooterInsets;
    UIEdgeInsets otherlinkFooterInsets = other.linkFooterInsets;
    if (selflinkFooterInsets.left != otherlinkFooterInsets.left || selflinkFooterInsets.top != otherlinkFooterInsets.top || selflinkFooterInsets.right != otherlinkFooterInsets.right || selflinkFooterInsets.bottom != otherlinkFooterInsets.bottom) return NO;
    if (self.defaultLinkHeight != other.defaultLinkHeight) return NO;
    if (self.maxLinkHeight != other.maxLinkHeight) return NO;
    if (![[ICMColor hexStringFromColor:self.activityIndicatorColor] isEqualToString:[ICMColor hexStringFromColor:other.activityIndicatorColor]]) return NO;
    UIEdgeInsets selfmessengerCardInsets = self.messengerCardInsets;
    UIEdgeInsets othermessengerCardInsets = other.messengerCardInsets;
    if (selfmessengerCardInsets.left != othermessengerCardInsets.left || selfmessengerCardInsets.top != othermessengerCardInsets.top || selfmessengerCardInsets.right != othermessengerCardInsets.right || selfmessengerCardInsets.bottom != othermessengerCardInsets.bottom) return NO;
    return self.showMessengerAppBorder == other.showMessengerAppBorder;
}
@end
