#import "ICMAssets.h"
@interface ICMAssets (OriginalAvatars)
+ (UIImage *)defaultAvatarImageWithInitials:(NSString *)initials diameter:(CGFloat)diameter useActionColorAsBackgroundColor:(BOOL)actionColor;
+ (UIImage *)initialsImage:(NSString *)initials diameter:(CGFloat)diameter useActionColorAsBackgroundColor:(BOOL)actionColor;
+ (UIImage *)defaultAvatarImageWithActionColorAsBackgroundColor:(BOOL)actionColor;
@end
