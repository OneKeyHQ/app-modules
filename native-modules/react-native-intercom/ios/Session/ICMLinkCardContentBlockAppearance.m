#import "ICMLinkCardContentBlockAppearance.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ICMChatNotificationContentImports.h"
@implementation ICMLinkCardContentBlockAppearance
- (instancetype)init {
    self = [super init];
    if (self) {
        self.subHeadingTextColor = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
        [self setAvatarRadius:28];
    }
    return self;
}
- (NSAttributedString *)linkWrittenByAttributedStringForName:(NSString *)name {
    NSString *text = [NSString stringWithFormat:@"Written by %@", name];
    NSMutableAttributedString *value = [[NSMutableAttributedString alloc] initWithString:text attributes:@{
        NSFontAttributeName: [ICMUtils fontOfSize:14 weight:0],
        NSForegroundColorAttributeName: [ICMRecoveredDefaultThemeProvider shared].theme.textMuted
    }];
    NSRange range = [text rangeOfString:name];
    [value addAttributes:@{NSFontAttributeName: [ICMUtils fontOfSize:14 weight:0],
        NSForegroundColorAttributeName: [ICMRecoveredDefaultThemeProvider shared].theme.textMuted} range:range];
    return value;
}
- (NSAttributedString *)linkUpdatedAtAttributedStringForDate:(NSDate *)date {
    return [[NSAttributedString alloc] initWithString:[[ICMTimestampCalculator sharedInstance] timestampTextForUpdatedDate:date]
        attributes:@{NSFontAttributeName: [ICMUtils fontOfSize:13 weight:0],
            NSForegroundColorAttributeName: [ICMRecoveredDefaultThemeProvider shared].theme.textMuted}];
}
- (double)avatarRadius { return avatarRadius; }
- (void)setAvatarRadius:(double)radius { avatarRadius = radius; }
@end
