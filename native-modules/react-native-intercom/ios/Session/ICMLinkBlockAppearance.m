#import "ICMLinkBlockAppearance.h"
#import "ICMBlockAppearanceDependencies.h"
@implementation ICMLinkCardBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.textInsets = UIEdgeInsetsMake(6, 8, 8, 8);
        self.linkInsets = UIEdgeInsetsMake(18, 18, 18, 18);
        self.headingTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        self.subHeadingTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
        self.headingFont = [ICMUtils fontOfSize:16 weight:0];
        self.subheadingFont = [ICMUtils fontOfSize:14 weight:0];
        [self setAvatarRadius:16];
    }
    return self;
}
- (NSAttributedString *)linkWrittenByAttributedStringForName:(NSString *)name {
    NSString *string = [NSString stringWithFormat:@"Written by %@", name];
    NSMutableAttributedString *attributed = [[NSMutableAttributedString alloc] initWithString:string attributes:@{
        NSFontAttributeName:[ICMUtils fontOfSize:14 weight:0],
        NSForegroundColorAttributeName:[ICMRecoveredDefaultThemeProvider shared].theme.textMuted
    }];
    NSRange range = [string rangeOfString:name];
    [attributed addAttributes:@{
        NSFontAttributeName:[ICMUtils fontOfSize:14 weight:0],
        NSForegroundColorAttributeName:[ICMRecoveredDefaultThemeProvider shared].theme.textMuted
    } range:range];
    return attributed;
}
- (CGFloat)avatarRadius { return avatarRadius; }
- (void)setAvatarRadius:(CGFloat)radius { avatarRadius = radius; }
@end
@implementation ICMLinkListBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.textInsets = UIEdgeInsetsMake(16, 16, 16, 16);
        self.separatorColor = [UIColor colorWithWhite:0.8588235378265381 alpha:0.800000011920929];
        self.linkFooterImage = [ICMAssets footerLinkIcon];
        self.linkFooterImageSize = CGSizeMake(16, 16);
        self.linkFooterInsets = UIEdgeInsetsMake(0, 22, 0, 22);
        self.defaultLinkHeight = 51;
        self.maxLinkHeight = 70;
    }
    return self;
}
@end
