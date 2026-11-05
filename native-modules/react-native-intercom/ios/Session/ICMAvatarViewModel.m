#import "ICMAvatarViewModel.h"
@implementation ICMAvatarViewModel
- (instancetype)initWithAvatar:(Avatar *)avatar activeIndicatorSize:(NSInteger)size {
    self = [super init];
    if (self) { self.avatar = avatar; self.activeIndicatorSize = size; }
    return self;
}
@end
