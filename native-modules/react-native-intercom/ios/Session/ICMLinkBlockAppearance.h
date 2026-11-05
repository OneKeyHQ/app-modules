#import "ICMBlockAppearance.h"
@interface ICMLinkCardBlockAppearance : ICMBlockAppearance {
    CGFloat avatarRadius;
}
- (CGFloat)avatarRadius;
- (void)setAvatarRadius:(CGFloat)radius;
- (NSAttributedString *)linkWrittenByAttributedStringForName:(NSString *)name;
@end
@interface ICMLinkListBlockAppearance : ICMLinkCardBlockAppearance
@end
