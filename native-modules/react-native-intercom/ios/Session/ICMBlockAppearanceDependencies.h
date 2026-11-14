// Original dynamic theme/asset/layout dependencies; missing implementations remain explicit.
#import "ICMWindowDependencies.h"
#import "ICMColor.h"
#import "ICMAssets.h"
#import "ICMRootDestinationDependencies.h"
@interface ICMRecoveredTheme (OriginalBlockColors)
@property (nonatomic, readonly) UIColor *base;
@property (nonatomic, readonly) UIColor *neutralContainer;
@property (nonatomic, readonly) UIColor *neutralBorder;
@property (nonatomic, readonly) UIColor *iconDefault;
@property (nonatomic, readonly) UIColor *primaryText;
@property (nonatomic, readonly) UIColor *textDefault;
@property (nonatomic, readonly) UIColor *textAlwaysLight;
@property (nonatomic, readonly) UIColor *primaryContainer;
@property (nonatomic, readonly) UIColor *primaryOnContainer;
@property (nonatomic, readonly) UIColor *neutralContainerEmphasis;
@property (nonatomic, readonly) UIColor *textMuted;
@property (nonatomic, readonly) UIColor *syntaxKeyword;
@property (nonatomic, readonly) UIColor *syntaxString;
@property (nonatomic, readonly) UIColor *syntaxNumber;
@property (nonatomic, readonly) UIColor *syntaxComment;
@property (nonatomic, readonly) UIColor *syntaxFunction;
@property (nonatomic, readonly) UIColor *syntaxTag;
@property (nonatomic, readonly) UIColor *syntaxOperator;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate27InlineCodeChipLayoutManager")))
@interface ICMRecoveredInlineCodeChipLayoutManager : NSLayoutManager
+ (CGFloat)horizontalPadding;
@end
@interface ICMAssets (OriginalBlockImages)
+ (UIImage *)defaultAvatarImageWithInitials:(NSString *)initials diameter:(CGFloat)diameter useActionColorAsBackgroundColor:(BOOL)actionColor;
+ (UIImage *)spinnerImage;
@end
#import "ICMUtils+Fonts.h"
