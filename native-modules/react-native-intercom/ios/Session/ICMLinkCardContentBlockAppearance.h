// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
#import "ICMPartBlockAppearances.h"
@interface ICMLinkCardContentBlockAppearance : ICMInAppBlockAppearance {
    double avatarRadius;
}
- (double)avatarRadius;
- (void)setAvatarRadius:(double)radius;
- (NSAttributedString *)linkWrittenByAttributedStringForName:(NSString *)name;
- (NSAttributedString *)linkUpdatedAtAttributedStringForDate:(NSDate *)date;
@end
